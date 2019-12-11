package com.ali.utils;

import com.ali.common.CommonData;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 连接池DBCP操作工具类
 */
public class DBCPUtil {
private static DataSource pool;

static {
    Properties properties=new Properties();

    try {
        properties.load(DBCPUtil.class.getClassLoader().getResourceAsStream(CommonData.DB_CONN_PROPERTIES));
        pool= BasicDataSourceFactory.createDataSource(properties);
    } catch (IOException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }

}

    /**
     * 返回连接池的实例
     * @return
     */
    public static DataSource getconnect(){
    return pool;
}

/**
 * 获取连接的实例
 */

public static Connection getconnection(){
    try {
        return pool.getConnection();
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException(CommonData.DB_CONN_EXCEPTION_INFO);
    }
}


}
