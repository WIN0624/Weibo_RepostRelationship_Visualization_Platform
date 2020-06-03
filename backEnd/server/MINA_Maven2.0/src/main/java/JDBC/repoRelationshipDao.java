package JDBC;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import QDataType.Query;
import QDataType.Relationship;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @ClassName repoRelationshipDao
 * @Description Provide some Functions to table 'repoRelationship'
 */
public class repoRelationshipDao {


    /**
     * select from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONObject
     */
    public static JSONObject selectReporelationshipByBw_id(String bw_id) {
        String sql = "select * from repoRelationship where bw_id=?;";
        return BaseDao.findJSONObject(Relationship.class, jdbcUtils.getConnection(), sql, bw_id);
    }

    /**
     * select from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONObject String
     */
    public static String selectReporelationshipByBw_idString(String bw_id) {
        return selectReporelationshipByBw_id(bw_id).toString();
    }

    /**
     * select from repoRelationship using list(bw_id)
     * @param list
     * @return List<Relationship>
     */
    public static List<Relationship> selectRepoRelationshipByBw_ids(List<String> list) {
        int size = list.size();
        if(size==0) return null;
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

    /**
     * select All Relationships from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONArray
     */
    public static JSONArray selectAllReporelationshipByBw_id(String bw_id)
    {
        List<String> bw_ids=new ArrayList<String>();
        bw_ids.add(bw_id);
        Connection conn=jdbcUtils.getConnection();
        String sql="select * from repoRelationship where bw_id=?";
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

    /**
     * select All Relationships from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONArrayString
     */
    public  static String selectAllReporelationshipByBw_idString(String bw_id)
    {
        return selectAllReporelationshipByBw_id(bw_id).toString();
    }
}
