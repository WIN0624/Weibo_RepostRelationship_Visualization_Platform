package com.weibo.weibo.redisDao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zenglr
 * @ClassName RedisDao
 * @Description
 * @create 2020-10-08-9:17 上午
 */
@Slf4j
@Component
public class RedisDao {
    @Autowired
    private StringRedisTemplate redisTemplate;

    static int pagecount=20;
    /**
     * 查询首页的id
     * @param query 检索词
     * @return keys
     */
//    @Cacheable(value = "bw_id", key = "#query")
    public List<String> getIDList(String query) {
        List<String> keys = new ArrayList<String>();
        try {
            if (redisTemplate.hasKey(query)) {
                log.info("使用redis查询query返回idList");
                keys.addAll(redisTemplate.opsForZSet().reverseRange(query,0,19));
            } else {
                log.info("redis没有找到query");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return keys;
    }

    /**
     * 根据页码查询id
     * @param query 检索词
     * @param page 页码
     * @return keys
     */
    public List<String> getIDListOnPage(String query, int page) {
        int start = (page - 1 ) * pagecount;
        int end = start + pagecount - 1;
        List<String> keys = new ArrayList<String>();
        try {
            if (redisTemplate.hasKey(query)) {
                log.info("使用redis查询query返回idList");
                keys.addAll(redisTemplate.opsForZSet().reverseRange(query,start,end));
            } else {
                log.info("redis没有找到query");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return keys;
    }

    /**
     * 返回页码总数
     * @param query 检索词
     * @return page
     */
    public long getPageNumber(String query)
    {
        long num = redisTemplate.opsForZSet().zCard(query);
        long page = num/pagecount + 1;
        return page;
    }

}
