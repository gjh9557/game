package com.ali.offline

import java.text.SimpleDateFormat
import java.util.Calendar

import com.ali.common.{CommonData, EventType}
import com.ali.utils.CommonUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object HotBloodOffineCal {

  def calnewuser(filter: RDD[(String, String, String, String)], castdata: Broadcast[String]) = {
val (currpent,next)=getCurrentAndNextday(castdata)
    val count = filter.filter(line => {
      //时间
      val time = line._3 //2018年2月1日，星期一，10:02:01
      val sdf = new SimpleDateFormat(CommonUtils.loadApolicationpropertie().getString(CommonData.TIME_PATTERN2))
      val timeMills = sdf.parse(time).getTime

      //事件类型
      val eventType = line._2
      (timeMills >= currpent && timeMills < next) && (eventType.equals(EventType.REDISTER.getEventType))

    }).map(_._1).distinct().count()
    count


  }

  /**
    * 获得基准日以及任意日的毫秒值
    * @param filter
    * @param castdata
    * @return
    */
  def getCurrentAndNextday(castdata: Broadcast[String]) = {


    val baseDatestr = castdata.value.concat(" 00:00:00")
    val sdf=new SimpleDateFormat(CommonUtils.loadApolicationpropertie().getString(CommonData.TIME_PATTERN))
    val date = sdf.parse(baseDatestr)
    val baseDateMillions = date.getTime


    val calendar=Calendar.getInstance()
    calendar.setTime(date)

    calendar.add(Calendar.DATE,1)
    //下一天的时间
    val next = calendar.getTime
    val nextMillions=calendar.getTimeInMillis

    //    println(s"开始时间：$date,结束时间：$next")
    (baseDateMillions,nextMillions)
  }

  def main(args: Array[String]): Unit = {
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
//sparkSession
    val spark= initEnv
    //利用sc从es里面读取数据
    val sc=spark.sparkContext

    //从es中读取数据
    val filter=ReadEsData(sc)

    //计算新增用户数
    val castdata: Broadcast[String] = sc.broadcast(baseData)

    val adduser = calnewuser(filter,castdata)

println(adduser)
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
