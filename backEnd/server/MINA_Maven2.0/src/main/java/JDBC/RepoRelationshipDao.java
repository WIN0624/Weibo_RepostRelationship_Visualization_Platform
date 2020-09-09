package JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import QDataType.RepoRelationship;
import org.json.JSONArray;
import org.json.JSONObject;
//import org.junit.Test;



/**
 * @ClassName repoRelationshipDao
 * @Description Provide some Functions to table 'repoRelationship'
 */
public class RepoRelationshipDao {


private final static Logger logger = Logger.getLogger(RepoRelationshipDao.class);
    /**
     * select from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONObject
     */
    public static JSONObject selectReporelationshipByBw_id(String bw_id) {
        String sql = "select user_id,screen_name,bw_id,reposts_count,fs_count,fs_user_id,fs_screen_name,fs_bw_id,fs_fans_count,level from load_test2 where bw_id=?;";
	Connection conn=JdbcUtils.getConnection();
        JSONObject a=BaseDao.findJSONObject(RepoRelationship.class, JdbcUtils.getConnection(), sql, bw_id);
	JdbcUtils.closeResource(conn);
    	return a;
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
     * select All Relationships from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONArray
     */
    public static JSONArray selectAllReporelationshipByBw_id(String bw_id)
    {
        JSONArray ajson=new JSONArray();
        List<RepoRelationship> tmp=selectRepoRelationshipByCenter_bw_id(bw_id);
	for( RepoRelationship i : tmp){
		if(!(i.getFs_count().equals("0"))&&!(i.getReposts_count().equals("0")))
			ajson.put(i.toJsonObject());
        }
	logger.info("筛选完成，json已经生成:\n"+ajson);
        return  ajson;
    }

    public static List<RepoRelationship> selectRepoRelationshipByCenter_bw_id(String bw_id)
    {
	List<RepoRelationship> res=null;
	String sql1="select user_id,screen_name,bw_id,reposts_count,fs_count,fs_user_id,fs_screen_name,fs_bw_id,fs_fans_count,level from load_test2 where center_bw_id=? and level=1;";
	Connection conn=JdbcUtils.getConnection();
	res=BaseDao.select(RepoRelationship.class,conn,sql1,bw_id);
	logger.info("sql1得到的List<RepoRelationship>长度为"+res.size());
	String sql2="select user_id,screen_name,bw_id,reposts_count,fs_count,fs_user_id,fs_screen_name,fs_bw_id,fs_fans_count,level from load_test2 where center_bw_id=? and level<>1;";
	res.addAll(BaseDao.select(RepoRelationship.class,conn,sql2,bw_id));	
	logger.info("sql2得到的List<RepoRelationship>长度为"+res.size());
	JdbcUtils.closeResource(conn);
	logger.info("查询完成,List<RepoRelationship>已经生成");
	return res;
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

/**
 * This is the version withour center_bw_id
 * @ClassName repoRelationshipDao
 * @Description Provide some Functions to table 'repoRelationship'

public class RepoRelationshipDao {


private final static Logger logger = Logger.getLogger(RepoRelationshipDao.class);
    /**
     * select from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONObject
     * /
    public static JSONObject selectReporelationshipByBw_id(String bw_id) {
        String sql = "select * from load_test2 where bw_id=?;";
	Connection conn=JdbcUtils.getConnection();
        JSONObject a=BaseDao.findJSONObject(RepoRelationship.class, JdbcUtils.getConnection(), sql, bw_id);
	JdbcUtils.closeResource(conn);
    	return a;
	}

    /**
     * select from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONObject String
     * /
    public static String selectReporelationshipByBw_idString(String bw_id) {
        return selectReporelationshipByBw_id(bw_id).toString();
    }

//    @Test
//    public void tttttt(){
//        List<String> ls=getAllbw_id("4470162294194950");
//        List<RepoRelationship> lr=selectRepoRelationshipByBw_ids(ls);
//        System.out.println(lr.toString());
//    }

    /**
     * select from repoRelationship using list(bw_id)
     * @param list
     * @return List<Relationship>
     * /
    public static List<RepoRelationship> selectRepoRelationshipByBw_ids(List<String> list) {
        int size = list.size();
        if(size==0) return null;
	String sql="select user_id,screen_name,bw_id,origin,reposts_count,fans_count,"+
		"fs_user_id,fs_screen_name,fs_bw_id,fs_fans_count,level,raw_text,created_at from load_test2"+
		" where bw_id in (";
        /*
	String sql = "select c.user_id,c.screen_name,c.bw_id,c.origin,c.reposts_count,c.fans_count," +
                "c.fs_user_id,c.fs_screen_name,c.fs_bw_id,c.fs_fans_count,c.level,c.raw_text,c.created_at " +
                "from (select a.user_id user_id,a.screen_name,a.bw_id ,a.origin,a.reposts_count ,a.fans_count ," +
                "b.user_id fs_user_id,b.screen_name fs_screen_name,b.bw_id fs_bw_id," +
                "b.fans_count fs_fans_count,b.level,b.text raw_text,b.created_at " +
                "from originAndRepo a inner join (select * from originAndRepo ) as b " +
                "where b.prev_bw_id=a.bw_id and b.prev_bw_id<>'Null' and (";
         * /
	String[] args = new String[size];
        for (int i = 0; i < size - 1; i++) {
            sql += "?,";
            args[i] = list.get(i);
        }
        sql += "?);";
        args[size - 1] = list.get(size - 1);
        Connection conn = JdbcUtils.getConnection();
        List<RepoRelationship> ls=BaseDao.select(RepoRelationship.class, conn, sql, args);
	JdbcUtils.closeResource(conn);
	return ls;
    }



    /**
     * select All Relationships from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONArray
     * /
    public static JSONArray selectAllReporelationshipByBw_id(String bw_id)
    {
        List<String> bw_ids=getAllbw_id(bw_id);
        JSONArray ajson=new JSONArray();
        List<RepoRelationship> tmp=selectRepoRelationshipByBw_ids(bw_ids);
        

	for( RepoRelationship i : tmp)
        {
	logger.info(i.toJsonObject());
	if(!i.reposts_count.equals("0"))
            ajson.put(i.toJsonObject());
        }
        return  ajson;
    }

    /**
     * @param bw_id
     * @return
     * /
    public static List<String> getAllbw_id(String bw_id){
        List<String> bw_ids=new ArrayList<String>();
        List<String> tmp=new ArrayList<String>();
        tmp.add(bw_id);
        Connection conn=JdbcUtils.getConnection();
        while(tmp.size()!=0)
        {
            String sql="select fs_bw_id from load_test2 where ";
            for(int i=0;i<tmp.size()-1;i++){
                sql+="bw_id=? or ";
            }
            sql+="bw_id=?;";
            for(String i : tmp)
            {
                bw_ids.add(i);
            }
            tmp=BaseDao.getcolumns2(conn,sql,tmp);
        }
	JdbcUtils.closeResource(conn);
        //for(int i=0;i<bw_ids.size();i++){
	//	logger.info(bw_ids.get(i));
	//}
	return  bw_ids;
    }


    /**
     * select All Relationships from repoRelationship using a String(bw_id)
     * @param bw_id
     * @return JSONArrayString
     * /
    public  static String selectAllReporelationshipByBw_idString(String bw_id)
    {
        return selectAllReporelationshipByBw_id(bw_id).toString();
    }
    
}
*/
