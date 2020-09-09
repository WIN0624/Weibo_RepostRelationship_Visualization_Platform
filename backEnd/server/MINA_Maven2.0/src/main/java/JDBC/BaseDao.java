package JDBC;

import org.json.JSONArray;
import org.json.JSONObject;
//import org.junit.Test;
import org.apache.log4j.Logger;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName BaseDao
 * @Description provide basic operation to DataBase
 */
public  class BaseDao {

    private final static Logger logger = Logger.getLogger(BaseDao.class);
    /**
     * used to update DataBase
     * @param conn
     * @param sql
     * @param args
     */
    public static void update(Connection conn,String sql,Object...args){
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.execute();
            conn.commit();
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JdbcUtils.closeResource(null,ps);
        }
    }

    /**
     * used to select from database and return a List
     * @param clazz
     * @param conn
     * @param sql
     * @param args
     * @param <T>
     * @return List<T>
     */
    public static <T> List<T> select(Class<T> clazz, Connection conn, String sql, Object... args) {
        PreparedStatement ps=null;
        ResultSet rs=null;
        List<T> list=new ArrayList<T>();
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                T t = clazz.newInstance();
                ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount(); i++) {
                    Object columnVal = rs.getObject(i + 1);
                    String columName = rsmd.getColumnName(i + 1);
                    Field field = clazz.getDeclaredField(columName);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(t, columnVal);
                    }
                }
                list.add(t);
            }
        } catch (SQLException | NoSuchFieldException | IllegalAccessException | InstantiationException e){
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResource(null,ps,rs);
        }
        return list;
    }

    /**
     * used to select from database and return a JSONArray
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return JSONArray
     */
    public static <T> JSONArray findJSONArray(Class<T> clazz, Connection conn,String sql, Object... args)
    {
        PreparedStatement ps=null;
        JSONArray ajson=null;
        try {
            ps=conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++)
            {
                ps.setObject(i+1,args[i]);
            }
            ResultSet resultSet = ps.executeQuery();
            ajson=new JSONArray();
            while(resultSet.next())
            {
                JSONObject jsonObject=new JSONObject();
                ResultSetMetaData metaData=resultSet.getMetaData();
                String columnName=null;
                String columnValue=null;
                for(int i=1;i<metaData.getColumnCount();i++) {
                    columnName=metaData.getColumnLabel(i+1);
                    try {
                        columnValue=new String(resultSet.getBytes(columnName),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    jsonObject.put(columnName,columnValue);
                }
                ajson.put(jsonObject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResource(null,ps);
        }
        return ajson;
    }

    /**
     * used to select from database and return a JSONObject
     * @param clazz
     * @param conn
     * @param sql
     * @param args
     * @param <T>
     * @return JSONObject
     */
    public static <T> JSONObject findJSONObject(Class<T> clazz, Connection conn,String sql, Object... args)
    {
        PreparedStatement ps=null;
        JSONObject jsonObject=null;
        try {
            ps=conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++)
            {
                ps.setObject(i+1,args[i]);
            }
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next())
            {
                jsonObject=new JSONObject();
                ResultSetMetaData metaData=resultSet.getMetaData();
                String columnName=null;
                String columnValue=null;
                for(int i=1;i<metaData.getColumnCount();i++) {
                    columnName=metaData.getColumnLabel(i+1);
                    try {
                        columnValue=new String(resultSet.getBytes(columnName),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    jsonObject.put(columnName,columnValue);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResource(null,ps);
        }
        return jsonObject;
    }

    /**
     * used to select some special value in DataBase
     * @param conn
     * @param sql
     * @param args
     * @param <E>
     * @return <E>
     */
    public static <E> E getValue(Connection conn,String sql,Object...args)
    {
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return (E) rs.getObject(1);
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }finally {
            JdbcUtils.closeResource(null,ps,rs);
        }
        return null;
    }

    /**
     * @param conn
     * @param sql
     * @param args
     * @return List<String>
     */
    public static List<String> getcolumns(Connection conn,String sql,Object...args){
        PreparedStatement ps=null;
        ResultSet rs=null;
        List<String> ls=new ArrayList<String>();
        try {
            ps=conn.prepareStatement(sql);
            for(int i=0;i<args.length;i++){
                ps.setObject(i+1,args[i]);
            }
            rs=ps.executeQuery();
            while(rs.next()){
                ls.add(rs.getString(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JdbcUtils.closeResource(null,ps,rs);
        }
        return ls;
    }

    public static List<String> getcolumns2(Connection conn,String sql,List<String> args){
        PreparedStatement ps=null;
        ResultSet rs=null;
        List<String> ls=new ArrayList<String>();
        try {
            ps=conn.prepareStatement(sql);
            for(int i=0;i<args.size();i++){
		ps.setObject(i+1,args.get(i));
	    }
	    rs=ps.executeQuery();
	    while(rs.next()){
                //logger.info(rs.getString(1));
		 ls.add(rs.getString(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JdbcUtils.closeResource(null,ps,rs);
        }
	//logger.info("=====");
	//for(int i=0;i<ls.size();i++){
		//logger.info(ls.get(i));
	//}//
        return ls;
    }

}


