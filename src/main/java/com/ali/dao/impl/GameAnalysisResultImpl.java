package com.ali.dao.impl;

import com.ali.dao.iGameAnalysisResult;
import com.ali.entity.GameAnalysisResultBean;
import com.ali.utils.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

/**
 * 接口实现类
 */
public class GameAnalysisResultImpl implements iGameAnalysisResult {
    private QueryRunner qr=new QueryRunner(DBCPUtil.getconnect());

    /**
     *     private String data;
     * //    新增用户
     *     private Long newAdduserCnt;
     * //    活跃用户
     *     private Long ActiveUserCnt;
     *     //次日留存率
     *     private double nextdayRate;
     *     //二日留存率
     *     private double twodayRate;
     *     private double threedayRate;
     *     private double fourdayRate;
     *     private double fivedayRate;
     * @param bean
     */

    @Override
    public void saveday(GameAnalysisResultBean bean)  {
        try {
            qr.update("insert into gamelog1 values(?,?,?,?,?,?,?,?) on duplicate key update "+
                            "newAdduserCnt=?," +
                            "ActiveUserCnt=?," +
                            "nextdayRate=?," +
                            "twodayRate=?," +
                            "threedayRate=?," +
                            "fourdayRate=?," +
                            "fivedayRate=?"
                    ,bean.getData()
                    ,bean.getNewAdduserCnt()
                    ,bean.getActiveUserCnt()
            ,bean.getNextdayRate()
            ,bean.getTwodayRate()
            ,bean.getThreedayRate()
            ,bean.getFourdayRate()
            ,bean.getFivedayRate()

                    ,bean.getNewAdduserCnt()
                    ,bean.getActiveUserCnt()
                    ,bean.getNextdayRate()
                    ,bean.getTwodayRate()
                    ,bean.getThreedayRate()
                    ,bean.getFourdayRate()
                    ,bean.getFivedayRate()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
