package com.atguigu.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author keyboardhero
 * @create 2022-04-21 15:37
 */
public class JDBCUtils {
    /**
     * 获取连接的操作
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception{
        //1.读取配置文件中的4个基本信息
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        Properties pros = new Properties();
        pros.load(is);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driver = pros.getProperty("driver");
        //2.获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

    /**
     * 关闭资源的操作
     * @param conn
     * @param ps
     */
    public static void closeResource(Connection conn, Statement ps){
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void closeResource(Connection conn, Statement ps, ResultSet rs){
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用Druid数据库连接池技术获取数据库连接
     */
    private static DataSource source;
    static {
        try {
            Properties pros = new Properties();
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
            pros.load(is);
            source = DruidDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection2() throws SQLException {
        Connection conn = source.getConnection();
        return conn;
    }

    /**
     * 使用Druid数据库连接池技术关闭连接
     */
    public static void closeResource2(Connection conn, Statement ps, ResultSet rs){
        DbUtils.closeQuietly(rs);
        DbUtils.closeQuietly(ps);
        DbUtils.closeQuietly(conn);
    }
}
