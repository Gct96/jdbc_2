package com.atguigu.transaction;

import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;

/**
 * @author keyboardhero
 * @create 2022-05-04 17:02
 */
public class ConnectionTest {
    @Test
    public void testGetConnection() throws Exception{
        Connection conn = JDBCUtils.getConnection();
        System.out.println(conn);
    }
}
