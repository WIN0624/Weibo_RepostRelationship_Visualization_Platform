package com.example.rwredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPoolUtil {
    private static JedisPool pool;
    static{
        JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setMaxTotal(5);
        jpc.setMaxIdle(1);

//        int port = 6379;
      int port = 6479;
//        String host = "Localhost";
      String host = "192.168.1.108";
        
        pool = new JedisPool(jpc, host, port);
    }
    public static Jedis getJedis(){
        return pool.getResource();
    }
    public static void release(Jedis jedis){
        if(jedis != null)
            jedis.close();
    }
}
