package com.atguigu.dao;

import com.atguigu.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author keyboardhero
 * @create 2022-05-08 20:46
 */
public abstract class BaseDAO {
    //通用的增删改
    public int update(Connection conn, String sql, Object ...args){
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
    //通用的查，一条记录
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
    //通用的查，多条记录
    public <T> List<T> getForList(Connection conn,Class<T> clazz, String sql, Object...args){
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
            //创建集合对象
            ArrayList<T> list=new ArrayList<T>();
            while(rs.next()){
                T t = clazz.newInstance();
                for(int i=0;i<columnCount;i++) {
                    //获取列值
                    Object columnValue = rs.getObject(i + 1);
                    //获取每个列的列名
                    String columnName = rsmd.getColumnName(i + 1);

                    //给cust对象指定的columnName属性，赋值为columValue:通过反射
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null,ps,rs);
        }
        return null;
    }

    //查询特殊值的方法
    public <E> E getValue(Connection conn,String sql,Object...args)  {
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps = conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            rs = ps.executeQuery();
            if(rs.next()){
                return (E) rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null,ps,rs);
        }
        return null;
    }
}
