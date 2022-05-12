package com.atguigu.transaction;

import com.atguigu.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @author keyboardhero
 * @create 2022-05-04 17:08
 */
public class TransactionTest {
    /**
     * 考虑事务原子性后的转账操作
     */
    @Test
    public void testUpdateWithTx(){
        Connection conn=null;
        try {
            conn = JDBCUtils.getConnection();
            conn.setAutoCommit(false);
            String sql1="update user_table set balance=balance-100 where user=?";
            update(conn,sql1,"AA");
            //模拟异常
            //System.out.println(10/0);
            String sql2="update user_table set balance=balance+100 where user=?";
            update(conn,sql2,"BB");
            System.out.println("转账成功");
            //提交数据
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //回滚数据
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    //通用的增删改操作--考虑事务原子性
    public int update(Connection conn,String sql, Object ...args){
        PreparedStatement ps = null;
        try {
            //1.预编译
            ps = conn.prepareStatement(sql);
            //2.填充占位符
            for(int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            //3.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //4.关闭资源
            JDBCUtils.closeResource(null,ps);
        }
        return 0;
    }

    public <T> T getInstance(Connection conn,Class<T> clazz,String sql,Object...args){ //泛型方法
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据：ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();
            //通过ResultSetMetaData获取结果集中的列数
            int columnCount=rsmd.getColumnCount();

            if(rs.next()){
                T t = clazz.newInstance();
                for(int i=0;i<columnCount;i++){
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的列名
                    String columnName=rsmd.getColumnName(i+1);

                    //给cust对象指定的columnName属性，赋值为columValue:通过反射
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null,ps,rs);
        }
        return null;
    }

}
