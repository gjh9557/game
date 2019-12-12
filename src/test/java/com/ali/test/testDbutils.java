package com.ali.test;
import com.ali.utils.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class testDbutils {
    private QueryRunner qu;

    @Before
    public void init(){
        qu=new QueryRunner(DBCPUtil.getconnect());
    }
    @Test
    public void testsave() throws SQLException {
        int cnt=qu.update("insert into test1 values(?)","回复短时");
        int cnt1=qu.update("delete from test1 where name=?","回复短时");
        System.out.println(cnt1>0?"成功":"失败");
    }
}
