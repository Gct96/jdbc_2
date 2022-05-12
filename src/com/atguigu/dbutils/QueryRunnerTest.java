package com.atguigu.dbutils;

import com.atguigu.bean.Customer;
import com.atguigu.util.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * commons-dbutils是Apache组织提供的一个开源JDBC工具类库，封装了针对于数据库的增删改查
 * @author keyboardhero
 * @create 2022-05-11 20:47
 */
public class QueryRunnerTest {

    @Test
    public void testInsert() throws SQLException {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();
            String sql="insert into customers(name,email,birth)values(?,?,?)";

            int insertCount = runner.update(conn, sql, "Jerry", "Jerry@163.com", new Date(453999999L));
            System.out.println("添加了"+insertCount+"条记录");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //测试查询
    /*
     *BeanHandler：是ResultSetHandler接口的实现类，用于封装表中的一条记录。
     */
    @Test
    public void  testQuery1(){
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();
            String sql="select id,name,email,birth from customers where id=?";
            BeanHandler<Customer> handler=new BeanHandler<>(Customer.class);
            Customer cus = runner.query(conn, sql, handler, 18);
            System.out.println(cus);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Test
    public void  testQuery2() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();
            String sql="select id,name,email,birth from customers where id<?";
            BeanListHandler<Customer> handler=new BeanListHandler<>(Customer.class);
            List<Customer> custs = runner.query(conn, sql, handler, 18);
            custs.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /*
     *MapHandler：是ResultSetHandler接口的实现类，对应表中的一条记录。
     * 将字段及相应字段的值作为map中的key和value
     */
    @Test
    public void  testQuery3() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();
            String sql="select id,name,email,birth from customers where id=?";
            MapHandler handler=new MapHandler();
            Map<String, Object> map = runner.query(conn, sql, handler, 18);
            System.out.println(map);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /*
     *MapListHandler：是ResultSetHandler接口的实现类，对应表中的多条记录。
     * 将字段及相应字段的值作为map中的key和value,将map添加到list中
     */
    @Test
    public void  testQuery4() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();
            String sql="select id,name,email,birth from customers where id<?";
            MapListHandler handler=new MapListHandler();
            List<Map<String, Object>> maps = runner.query(conn, sql, handler, 18);
            maps.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /*
     *ScalarHandler用于查询特殊值
     *
     */
    @Test
    public void  testQuery5() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();

            String sql="select count(*) from customers";
            ScalarHandler handler = new ScalarHandler();
            Long count=(Long)runner.query(conn, sql, handler);

            System.out.println(count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /*
     *ScalarHandler用于查询特殊值
     *
     */
    @Test
    public void  testQuery6() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();

            String sql="select max(birth) from customers";
            ScalarHandler handler = new ScalarHandler();
            Date maxBirth=(Date)runner.query(conn, sql, handler);

            System.out.println(maxBirth);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    /**
     * 自定义ResultSetHandler的实现类
     */
    @Test
    public void  testQuery7() {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = JDBCUtils.getConnection2();
            String sql="select id,name,email,birth from customers where id=?";
            ResultSetHandler<Customer> handler=new ResultSetHandler<Customer>() {//匿名实现类

                @Override
                public Customer handle(ResultSet rs) throws SQLException {
                    if(rs.next()){
                        int id=rs.getInt("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        Date birth = rs.getDate("birth");
                        Customer customer = new Customer(id, name, email, birth);
                        return customer;
                    }
                    return null;
                }
            };
            Customer cust = runner.query(conn, sql, handler, 18);
            System.out.println(cust);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }
}
