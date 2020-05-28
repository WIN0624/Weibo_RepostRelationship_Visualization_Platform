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
        Jedis jedis = new Jedis("localhost",6379);
        ArrayList<String> wb_id = new ArrayList<String>();
        wb_id.addAll(jedis.zrange(query,0,9));
        // keys.addAll(jedis.zrevrange(query,0,-1));

        jedis.close();
        return wb_id;
    }
}
