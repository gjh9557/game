package com.ali.offline

import java.text.SimpleDateFormat
import java.util.Calendar

import com.ali.common.{CommonData, EventType}
import com.ali.dao.iGameAnalysisResult
import com.ali.dao.impl.GameAnalysisResultImpl
import com.ali.entity.GameAnalysisResultBean
import com.ali.utils.CommonUtils
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ArrayBuffer

/**
  * sparkcore版本
  */

object HotBloodOffineCal {

  def publicType(sc:SparkContext,filter:RDD[(String, String, String, String)], castdata: Broadcast[String],x:Int,y:Int) = {
    val (currpent,next)=getCurrentAndNextday(castdata,x,y)
val broadcast = sc.broadcast((currpent,next))
    val cal = filter.map(line => {
      //时间
      val time = line._3 //2018年2月1日，星期一，10:02:01
      val sdf = new SimpleDateFormat(CommonUtils.loadApolicationpropertie().getString(CommonData.TIME_PATTERN2))
      val timeMills = sdf.parse(time).getTime

      //事件类型
      val eventType = line._2
      (timeMills,broadcast.value._1,broadcast.value._2,eventType,line._1)
    })
    cal
//      (timeMills >= currpent && timeMills < next) && (eventType.equals(EventType.REDISTER.getEventType))
//
//    }).map(_._1).distinct()
//    count

  }

  def calnewuser(sc:SparkContext,filter: RDD[(String, String, String, String)], castdata: Broadcast[String],x:Int,y:Int)={
    val cal=publicType(sc,filter,castdata,x,y)
    cal.filter(line=>{
      (line._1>=line._2&&line._1<line._3)&&(line._4.equals(EventType.REDISTER.getEventType))
    }).map(_._5).distinct()
  }

//  def calnewuser(filter: RDD[(String, String, String, String)], castdata: Broadcast[String],x:Int,y:Int) = {
//val (currpent,next)=getCurrentAndNextday(castdata,x,y)
//
//    val count = filter.filter(line => {
//      //时间
//      val time = line._3 //2018年2月1日，星期一，10:02:01
//      val sdf = new SimpleDateFormat(CommonUtils.loadApolicationpropertie().getString(CommonData.TIME_PATTERN2))
//      val timeMills = sdf.parse(time).getTime
//
//      //事件类型
//      val eventType = line._2
//      (timeMills >= currpent && timeMills < next) && (eventType.equals(EventType.REDISTER.getEventType))
//
//    }).map(_._1).distinct()
//    count
//
//
//  }

  /**
    * 获得基准日以及任意日的毫秒值用来比较那个注册的基准日和日期
    * @param filter
    * @param castdata
    * @return
    */
  def getCurrentAndNextday(castdata: Broadcast[String],x:Int,y:Int) = {
    //这里用了x,y来标榜是基于基准日后的那个时间段，如要基准日到下一天则，casedata,0,1  要求基准日的下一天到下一天
    //则casedata,1,1
    val baseDatestr = castdata.value.concat(" 00:00:00")
    val sdf=new SimpleDateFormat(CommonUtils.loadApolicationpropertie().getString(CommonData.TIME_PATTERN))
    val date = sdf.parse(baseDatestr)
    val baseDateMillions = date.getTime


    val calendar=Calendar.getInstance()
    calendar.setTime(date)

    calendar.add(Calendar.DATE,x)
    //下一天的时间
    val next = calendar.getTime
    val nextMillions=calendar.getTimeInMillis

    val calendar1=Calendar.getInstance()

    calendar1.setTime(next)
    calendar1.add(Calendar.DATE,y)

    val end=calendar1.getTime
    val endMillions=calendar1.getTimeInMillis

    (nextMillions,endMillions)
  }

  def calActiveUser(sc:SparkContext,filter: RDD[(String, String, String, String)], castdata: Broadcast[String],x:Int,y:Int) = {
//    得到当前和下一天的时间
//    val (currpent,next)=getCurrentAndNextday(castdata,x,y)
//    val count = filter.filter(line => {
//      //时间
//      val time = line._3 //2018年2月1日，星期一，10:02:01
//      val sdf = new SimpleDateFormat(CommonUtils.loadApolicationpropertie().getString(CommonData.TIME_PATTERN2))
//      val timeMills = sdf.parse(time).getTime
//
//      //事件类型
//      val eventType = line._2
//
//      (timeMills >= currpent && timeMills < next) &&
//        (eventType.equals(EventType.REDISTER.getEventType)||eventType.equals(EventType.LOGIN.getEventType))
//
//    }).map(_._1).distinct()
//    count
val cal=publicType(sc,filter,castdata,x,y)
    cal.filter(line=>{
      (line._1>=line._2&&line._1<line._3)&&
        (line._4.equals(EventType.REDISTER.getEventType)||line._4.equals(EventType.LOGIN.getEventType))
    }).map(_._5).distinct()
  }


  def calnextActivePretenge(newuser:RDD[String],sc:SparkContext,baseData: Broadcast[String], filter: RDD[(String, String, String, String)],x:Int,y:Int) = {
    //未进行优化：19090 不添加广播变量
    //17797广播变量后

    val count1=newuser.count()

  val tmp1 = newuser.map(line=>{(line,"")})
    val activeuser=calActiveUser(sc,filter,baseData,x,y)
    val count2=activeuser.count()

      val tmp2 = activeuser.map(line=>(line,""))
    val join: RDD[(String, (String, String))] = tmp1.join(tmp2)

    val pentenge=join.count().toDouble/count1.toDouble
    pentenge

  }

  def save2db(baseData: String, newusertotal: Long, activeUsertotal: Long, array: ArrayBuffer[String]) = {
    //Dao层实例准备
    val dao:iGameAnalysisResult=new GameAnalysisResultImpl
    //构建实例
    val bean=new GameAnalysisResultBean(baseData,newusertotal,activeUsertotal,array(0),array(1),array(2),array(3),array(4))
      //save
dao.saveday(bean)
  }

  def main(args: Array[String]): Unit = {
    val current=System.currentTimeMillis()

    //1:拦截非法参数
    if(args==null||args.length!=1){
      println(
        """
          |警告：
          |请录入参数！基准日：如：2018-02-01
        """.stripMargin)
      sys.exit(-1)
    }
//    2:2 获得参数

    val Array(baseData)=args

//    println(s"基准日：$baseData")
//sparkSession rdd里面的缓存默认是在内存中的，dataframe的缓存机制是内存和磁盘那个选项
    val spark= initEnv


    val sc: SparkContext = spark.sparkContext
    //利用sc从es里面读取数据

    //从es中读取数据
    val filter=ReadEsData(sc)

    //计算新增用户数
    /**
      * 获取基准日信息
      * 对rdd中消息进行过滤计算，返回
      * 条件：玩游戏时间>基准日<第二天 and 事件类型为注册
      */
    val castdata: Broadcast[String] = sc.broadcast(baseData)

    val adduser = calnewuser(sc,filter,castdata,0,1).cache()
      val newusertotal = adduser.count()

    println(newusertotal)
    //活跃用户数是
    /**
      * 获取基准日信息
      * 对rdd中消息进行过滤计算，返回
      * 条件：玩游戏时间>基准日<第二天 and 事件类型为注册或登录

      */

    val activeUser=calActiveUser(sc,filter,castdata,0,1)

val activeUsertotal=activeUser.count()
    println(activeUsertotal)

    /**
      * 计算次日留存率，当日登录用户前一天的登录用户，相除得出
      *
      * ！！！！！这里计算次日留存率的时候其实用到了那个当日去注册用户那个rdd，可以把它也作为一个变量传递进去。节省计算
      * !!!!!!!!这里的计算几日留存率的问题都是基于传入的那一天的那个注册用户然后去计算最后那天的活跃用户，所以可以传递参数去
      * 计算，直接将几日留存搞进去，这里传递一个x,y的参数分别是1,1时代表在计算基准日的后一天到大后天的一个用户活跃，意思就是在求
      * 基准日后一天的活跃，所以这个可以求出次日留存率，求7日的时候可以将7,1传入此时他就会去计算第七天登陆的活跃用户，然后与基准日
      * 注册用户进行相关联合系列操作
      */
//    val nextActivePretenge=calnextActivePretenge(adduser,sc,castdata,filter,2,1)
    //求出七天的留存率
    val array=new ArrayBuffer[String]()
    for(i<- 1 to 7){
      val next=calnextActivePretenge(adduser,sc,castdata,filter,i,1)
      val pre = ((next*100).toString+"000000").substring(0,4).concat("%")
      array.append(pre)
    }
    println("留存率："+array)

    save2db(baseData,newusertotal,activeUsertotal,array)


    //println(nextActivePretenge)

    val end=System.currentTimeMillis()
    println("耗时"+(end-current))

    spark.stop()
  }
//初始化spark环境
  private def initEnv = {
    SparkSession
      .builder()
      .master("local[*]")
      .appName("a")
      .config("es.nodes", "hadoop01")
      .config("port", "9200")
      .getOrCreate()
  }

  /**
    * 从es中读取数据并将其中ip地址的脏数据过滤掉
    *
    * @param sc
    */
  private def ReadEsData(sc: _root_.org.apache.spark.SparkContext) = {
    import org.elasticsearch.spark._

    val query =
      """
        |{
        |  "query": {
        |    "match_all": {}
        |  }
        |}
      """.stripMargin
    //     sc.esRDD("gamelog", query)
    // 用来看下数据有多少
    //      .distinct()
    //      .count()
    //    println(count)
    val value: RDD[(String, String, String, String)] = sc.esRDD("gamelog", query)
      .map(line => {
        val record = line._2
        //用户名
        val username = record.getOrElse("userName", "").asInstanceOf[String]
        //时间
        val time = record.getOrElse("time", "").asInstanceOf[String]
        //事件类型
        val event = record.getOrElse("eventType", "").asInstanceOf[String]
        //ip
        val ip = record.getOrElse("ip", "").asInstanceOf[String]

        (username, event, time, ip)

      })
    value.cache();
    val filter: RDD[(String, String, String, String)] = value.filter(line => {
      val ip = line._4.asInstanceOf[String]
      val regex ="""(\d{1,3}\.){3}\d{1,3}""" //三扩韩里面的特殊含义字符不用转移
      ip.matches(regex)
    })
    filter
  }
}
