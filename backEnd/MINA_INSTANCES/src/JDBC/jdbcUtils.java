package JDBC;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author lijy3
 * @version 1.0
 * @ClassName jdbcUtils
 * @Description provide getConnection and closeConnect functions, read from jdbc.properties
 * @date 2020/5/24 18:12
 */
public class jdbcUtils {
    /**
     * getConnection
     * @return conn
     */
    public static Connection getConnection(){
        InputStream in=ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        Properties pros=new Properties();
        try {
            pros.load(in);
        } catch (IOException e) {
            System.out.println("��ȡ���ݿ������ļ�ʧ��");
            e.printStackTrace();
        }
        String user=pros.getProperty("user");
        String password=pros.getProperty("password");
        String url=pros.getProperty("url");
        String driverClass=pros.getProperty("driverClass");
        try {
            Class.forName(driverClass);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection conn=null;
        try {
            conn= DriverManager.getConnection(url,user,password);

        } catch (SQLException e) {
            System.out.println("���ݿ�����ʧ��");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * close conn and statement
     * @param conn
     * @param statement
     */
    public static void closeResource(Connection conn, Statement statement)
    {
        if(statement!=null)
        {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn!=null)
        {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * close conn and statement and rs
     * @param conn
     * @param statement
     * @param rs
     */
    public static void closeResource(Connection conn, Statement statement, ResultSet rs)
    {
        if(statement!=null)
        {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn!=null)
        {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(rs!=null)
        {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * close conn
     * @param conn
     */
    public  static void closeResource(Connection conn)
    {
        if(conn!=null)
        {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
