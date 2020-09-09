package MyRedis;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;

public class ReadDataFromRedis {
    /**
     * 根据query查取微博ID
     * @param query
     * @return keys
     */
    static int pagerecord=20;

    public static ArrayList<String> getIDFromRedis(String query){
        Jedis jedis = new Jedis("192.168.1.103",6479);
        jedis.auth("nopassword");
        ArrayList<String> wb_id = new ArrayList<String>();
        wb_id.addAll(jedis.zrevrange(query,0,9));
        // keys.addAll(jedis.zrevrange(query,0,-1));

        jedis.close();
        return wb_id;
    }

    public static ArrayList<String> getIDFromRedisOnPage(String query, int page){
        Jedis jedis = new Jedis("192.168.1.103",6479);
        jedis.auth("nopassword");
        int start=(page-1)*pagerecord;
        int end=start+pagerecord-1;
        ArrayList<String> wb_id = new ArrayList<String>();
        wb_id.addAll(jedis.zrevrange(query,start,end));
        // keys.addAll(jedis.zrevrange(query,0,-1));
        jedis.close();
        return wb_id;
    }

    public static long getPageNumber(String query){
        Jedis jedis = new Jedis("192.168.1.103",6479);
        jedis.auth("nopassword");
        long num=jedis.zcard(query);
        long page=num/pagerecord+1;
        jedis.close();
        return page;
    }
}
