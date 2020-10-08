package com.weibo.weibo.mapper;

import com.weibo.weibo.WeiboSpringboot192Application;
import com.weibo.weibo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.Assert;
import javax.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes=WeiboSpringboot192Application.class)
public class MapperTest{
    @Resource
    private QueryMapper queryMapper;
    @Resource
    private RepoRelationshipMapper repoRelationshipMapper;

    @Test
    public void selectQueryByBw_id(){
        String bw_id="4529050938509504";
        List<Query> query=queryMapper.selectQueryByBw_id(bw_id);
        Assert.assertNotNull(query);
        log.debug("【query】={}",query);
    }
    @Test
    public void selectQueryByBw_ids(){
        List<String> bw_ids=new ArrayList<String>(){{add("4529050938509504");}};
        List<Query> queryList=queryMapper.selectQueryByBw_ids(bw_ids);
        Assert.assertNotNull(queryList);
        log.debug("【queryList】={}",queryList);
    }

    @Test
    public void selectRepoRelationshipByCenter_bw_id(){
        String center_bw_id="4523566437432908";
        List<RepoRelationship> repoRelationships=repoRelationshipMapper.selectRepoRelationshipByCenter_bw_id(center_bw_id);
        log.debug("【repoRelationships】={}",repoRelationships);
    }

}
