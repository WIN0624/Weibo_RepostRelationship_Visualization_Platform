package JDBC;

import QDataType.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.List;

/**
 * @ClassName QueryDao
 * @Description Provide some Functions to table 'query'
 */
public class QueryDao {

private final static Logger logger = Logger.getLogger(QueryDao.class);
    /**
     * @param list
     * @return JSONArray
     */
    public static JSONArray selectQueryByBw_ids(List<String> list) {
        int size = list.size();
        if(size==0) return null;
        String sql = "select id,user_id,screen_name user_name,bw_id,content,created_at,reposts_count from load_test where";
        String[] args = new String[size];
        for (int i = 0; i < size - 1; i++) {
            sql += " bw_id = ? or";
            args[i] = list.get(i);
        }
        sql += " bw_id = ?;";
        args[size - 1] = list.get(size - 1);
        Connection conn = JdbcUtils.getConnection();
        JSONArray a=BaseDao.findJSONArray(Query.class, conn, sql, args);
	JdbcUtils.closeResource(conn);
	return a;
    }

    /**
     *select from query using a String(bw_id)
     * @param bw_id
     * @return JSONObject
     */
    public static JSONObject selectQueryByBw_id(String bw_id) {
        String sql = "select id,user_id,screen_name user_name,bw_id,content,created_at,reposts_count from load_test where bw_id = ?";
        Connection conn = JdbcUtils.getConnection();
        JSONObject b=BaseDao.findJSONObject(Query.class, conn, sql, bw_id);
	JdbcUtils.closeResource(conn);
	if(b==null) logger.info("rpBody is empty when use bw_id");
	return (b==null)?new JSONObject():b;

    }

    /**
     *select from query using list(bw_id)
     * @param list
     * @return JSONArrayString
     */
    public static String selectQueryByBw_idsString(List<String> list) {
        if(list.size()==0) return "";
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
