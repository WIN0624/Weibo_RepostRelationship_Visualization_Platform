package MyRedis;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;

public class ReadDataFromRedis {
    /**
     * 根据query查取微博ID
     * @param query
     * @return keys
     */
    public static ArrayList<String> getIDFromRedis(String query){
        Jedis jedis = new Jedis("192.168.1.108",6479);
        jedis.auth("nopassword");
        ArrayList<String> wb_id = new ArrayList<String>();
        wb_id.addAll(jedis.zrange(query,0,9));
        // keys.addAll(jedis.zrevrange(query,0,-1));

        jedis.close();
        return wb_id;
    }
}
