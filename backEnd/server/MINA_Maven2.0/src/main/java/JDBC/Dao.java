package JDBC;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import QDataType.Query;
import QDataType.Relationship;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author lijy3
 * @version 1.0
 * @ClassName Dao
 * @Description TODO
 * @date 2020/6/1 16:54
 */
public class Dao {
    public static JSONArray selectQueryByBw_ids(List<String> list) {
        int size = list.size();
        String sql = "select * from query where";
        String[] args = new String[size];
        for (int i = 0; i < size - 1; i++) {
            sql += " bw_id = ? or";
            args[i] = list.get(i);
        }
        sql += " bw_id = ?;";
        args[size - 1] = list.get(size - 1);
        Connection conn = jdbcUtils.getConnection();
        return BaseDao.findJSONArray(Query.class, conn, sql, args);
    }

    /**
     *
     * @param list
     * @return
     */
    public static String selectQueryByBw_idsString(List<String> list) {
        return selectQueryByBw_ids(list).toString();
    }

    public static JSONObject selectQueryByBw_id(String bw_id) {
        String sql = "select * from query where bw_id = ?";
        Connection conn = jdbcUtils.getConnection();
        return BaseDao.findJSONObject(Query.class, conn, sql, bw_id);
    }

    public static JSONObject selectReporelationshipByBw_id(String bw_id) {
        String sql = "select * from repoRelationship where bw_id=?;";
        return BaseDao.findJSONObject(Relationship.class, jdbcUtils.getConnection(), sql, bw_id);
    }

    public static String selectReporelationshipByBw_idString(String bw_id) {
        return selectReporelationshipByBw_id(bw_id).toString();
    }

    public static String selectQueryByBw_idString(String bw_id)
    {
        return selectQueryByBw_id(bw_id).toString();
    }

    public static List<Relationship> selectRepoRelationshipByBw_ids(List<String> list) {
        int size = list.size();
        String sql = "select * from repoRelationship where";
        String[] args = new String[size];
        for (int i = 0; i < size - 1; i++) {
            sql += " bw_id = ? or";
            args[i] = list.get(i);
        }
        sql += " bw_id = ?;";
        args[size - 1] = list.get(size - 1);
        Connection conn = jdbcUtils.getConnection();
        return BaseDao.select(Relationship.class, conn, sql, args);
    }

    public static JSONArray selectAllReporelationshipByBw_id(String bw_id)
    {
        List<String> bw_ids=new ArrayList<String>();
        bw_ids.add(bw_id);
        System.out.println(bw_ids);
        Connection conn=jdbcUtils.getConnection();
        String sql="select * from reporelationship where bw_id=?";
        JSONArray ajson=new JSONArray();
        List<Relationship> tmp=null;
        while(bw_ids.size()!=0)
        {
            tmp=selectRepoRelationshipByBw_ids(bw_ids);
            bw_ids=new ArrayList<String>();
            for(Relationship i : tmp)
            {
                bw_ids.add(i.getFs_bw_id());
                ajson.put(i.toJsonObject());
            }
        }
        return  ajson;
    }

    public  static String selectAllReporelationshipByBw_idString(String bw_id)
    {
        return selectAllReporelationshipByBw_id(bw_id).toString();
    }
}
