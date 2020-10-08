package com.example.rwredis;

import redis.clients.jedis.Jedis;
import java.io.*;
public class Write {
    private static Jedis jedis;
    static{
        jedis = RedisPoolUtil.getJedis();
        jedis.auth("nopassword");
        System.out.println(jedis.ping());
    }

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        Data[] d1=Read.readDB(file);
        for(int i=0; i<d1.length; i++)
        {
            jedis.zadd(d1[i].getQuery(),d1[i].getNumber(),d1[i].getWeibo_id());
        }
    }
}
