package com.weibo.weibo.redisDao;

import com.weibo.weibo.WeiboSpringboot192Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zenglr
 * @ClassName
 * @Description
 * @create 2020-10-08-2:15 下午
 */
@Slf4j
@SpringBootTest(classes= WeiboSpringboot192Application.class)
public class RedisDaoTest {
    @Resource
    RedisDao redisDao;

    @Test
    public void getIdList(){
        String query = "新型冠状病毒";
        List<String> keys = redisDao.getIDList(query);
        Assert.assertNotNull(keys);
    }
    @Test
    public void getIdListOnPage(){
        String query = "新型冠状病毒";
        int page = 3;
        List<String> keys = redisDao.getIDListOnPage(query,page);
        Assert.assertNotNull(keys);
    }
    @Test
    public void getPage(){
        String query = "新型冠状病毒";
        long num = redisDao.getPageNumber(query);
        Assert.assertNotNull(num);
    }
}
