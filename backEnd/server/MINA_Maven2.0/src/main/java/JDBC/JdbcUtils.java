package JDBC;


import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName JdbcUtils
 * @Description provide getConnection and closeConnect functions, read from jdbc.properties
 */
public class JdbcUtils {
    /**
     * getConnection
     * @return conn
     */

    public  static Connection getConnection(){
        Properties pros= new Properties();
        InputStream inputStream= JdbcUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        try {
            pros.load(inputStream);
        } catch (IOException e) {
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
            System.out.println("Something Wrong when connecting to the DataBase!");
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
