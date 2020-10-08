package JDBC;

import QDataType.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.List;

/**
 * @ClassName queryDao
 * @Description Provide some Functions to table 'query'
 */
public class queryDao {
    /**
     *select from query using list(bw_id)
     * @param list
     * @return JSONArray
     */
    public static JSONArray selectQueryByBw_ids(List<String> list) {
        int size = list.size();
        if(size==0) return null;
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
     *select from query using a String(bw_id)
     * @param bw_id
     * @return JSONObject
     */
    public static JSONObject selectQueryByBw_id(String bw_id) {
        String sql = "select * from query where bw_id = ?";
        Connection conn = jdbcUtils.getConnection();
        return BaseDao.findJSONObject(Query.class, conn, sql, bw_id);
    }

    /**
     *select from query using list(bw_id)
     * @param list
     * @return JSONArrayString
     */
    public static String selectQueryByBw_idsString(List<String> list) {
        if(list.size()==0) return null;
        return selectQueryByBw_ids(list).toString();
    }


    /**
     *select from query using a String(bw_id)
     * @param bw_id
     * @return JSONObjectString
     */
    public static String selectQueryByBw_idString(String bw_id)
    {
        return selectQueryByBw_id(bw_id).toString();
    }
}
