package JDBC;
import QDataType.Query;
import QDataType.Relationship;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * provide update table, find info from table and run sql File function
 */
public class jdbc {
    public static String searchQuery(String bwId) {
        //update
        //String sql1;
        //update(sql1+sql2);

        //findQuery
//        List<Query> query = findQuery(Query.class, "select * from query where bw_id =?",bwId);
//       for(Query i:query)
//       {
//           System.out.println(i);
//       }
//        query.forEach(System.out::println);

        //findQuery and return jsonArray
        JSONArray jsonArray=findQueryJSON(Query.class, "select * from query where bw_id =?",bwId);
        String json=jsonArray.toString();
//        creatJsonFile(json,"C:/Users/lijy3/Desktop","TenRecords");

        //run sql file
        //runSqlFile("");
        return json;
    }

    public static String searchRelationship(String bwId) {
        //update
        //String sql1;
        //update(sql1+sql2);

        //findQuery
//        List<Relationship> relationship = findQuery(Relationship.class, "select * from reporelationship where bw_id =?",bwId);
//        for(Relationship i:relationship)
//        {
//            System.out.println(i);
//        }
//        query.forEach(System.out::println);

        //findQuery and return jsonArray
        JSONArray jsonArray=findQueryJSON(Relationship.class, "select * from reporelationship where bw_id =?",bwId);
        String json=jsonArray.toString();
//        creatJsonFile(json,"C:/Users/lijy3/Desktop","TenRecords");

        //run sql file
        //runSqlFile("");
        return json;
    }

//    /**
//     * Update a table
//     * @param sql sql with ?
//     * @param args ?
//     */
//    public static void update(String sql,Object ...args)
//    {
//        Connection conn=null;
//        PreparedStatement ps=null;
//        try {
//            conn= jdbcUtils.getConnection();
//            ps=conn.prepareStatement(sql);
//            for(int i=0;i<args.length;i++)
//            {
//                ps.setObject(i+1,args[i]);
//            }
//            ps.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            jdbcUtils.closeResource(conn,ps);
//        }
//    }
//
//    /**
//     * find information in table
//     * @param clazz name of the table(class)
//     * @param sql sql with ?
//     * @param args ?
//     * @param <T> table(class)
//     */
//    public static <T> List<T> findQuery(Class<T> clazz, String sql, Object... args)
//    {
//        Connection conn=null;
//        PreparedStatement ps=null;
//        ArrayList<T> alt=null;
//        try {
//            conn=jdbcUtils.getConnection();
//            ps=conn.prepareStatement(sql);
//            for(int i=0;i<args.length;i++)
//            {
//                ps.setObject(i+1,args[i]);
//            }
//            ResultSet resultSet = ps.executeQuery();
//            alt=new ArrayList<T>();
//            while(resultSet.next())
//            {
//                T t=null;
//                try {
//                    t=clazz.newInstance();
//                } catch (InstantiationException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//                ResultSetMetaData metaData=resultSet.getMetaData();
//                for(int i=1;i<metaData.getColumnCount();i++) {
//                    Object columnValue=resultSet.getObject(i+1);
//
//                    String columnName=metaData.getColumnLabel(i+1);
//                    Field field=null;
//                    try {
//                        field=clazz.getDeclaredField(columnName);
//                    } catch (NoSuchFieldException e) {
//                        e.printStackTrace();
//                    }
//                    if(field!=null)
//                    {
//                        field.setAccessible(true);
//                        try {
//                            field.set(t,columnValue);
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                alt.add(t);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            jdbcUtils.closeResource(conn,ps);
//        }
//        return alt;
//    }
//
//    /**
//     * excute a sql file
//     * @param sqlFilePath
//     */
//    public static void runSqlFile(String sqlFilePath){
//        Connection conn=jdbcUtils.getConnection();
//        ScriptRunner runner=new ScriptRunner(conn);
//
//        //���ò��Զ��ύ����ʹ�ûع�
//        runner.setAutoCommit(false);
//
//        //setStopOnError����
//        // true���������ִֹͣ�У���ӡ���׳��쳣����׽�쳣�����лع�����֤��һ��������ִ��
//        //false�������󲻻�ֹͣ�������ִ�У����ӡ�쳣��Ϣ���������׳��쳣����ǰ�����޷���׽�쳣�޷����лع��������޷���֤��һ��������ִ��
//        runner.setStopOnError(true);
//        //setSendFullScript����
//        //true���ȡ�����ű���ִ��
//        //false�����Զ���ָ���ÿ��ִ��
//        runner.setSendFullScript(false);
//
//        runner.setDelimiter(";");
//        runner.setErrorLogWriter(null);
//
//        //Null�������־
//        //�������Զ�����־���������̨
//        //runner.setLogWriter(null);
//        try {
//            runner.runScript(new InputStreamReader(new FileInputStream(sqlFilePath),"utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            System.out.println("���벻֧��");
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            System.out.println("�Ҳ���sql�ű��ļ�");
//            e.printStackTrace();
//        }
//        try {
//            conn.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        jdbcUtils.closeResource(conn);
//    }

    public static <T> org.json.JSONArray findQueryJSON(Class<T> clazz, String sql, Object... args)
    {
        Connection conn=null;
        PreparedStatement ps=null;
        JSONArray ajson=null;
        try {
            conn=jdbcUtils.getConnection();
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
            jdbcUtils.closeResource(conn,ps);
        }
        return ajson;
    }

    //Create a Json File
    public static boolean creatJsonFile(String json,String filePath,String fileName){
        boolean flag=true;
        String fullPath=filePath+ File.separator+fileName+".json";

        File file=new File(fullPath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(file.exists())
        {
            file.delete();
        }
        try {
            file.createNewFile();
            Writer writer=new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            flag=false;
            e.printStackTrace();
        }
        return flag;
    }
}
