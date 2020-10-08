package com.weibo.weibo.util;
import com.weibo.weibo.WeiboSpringboot192Application;
import com.weibo.weibo.entity.*;
import com.weibo.weibo.mapper.QueryMapper;
import com.weibo.weibo.mapper.RepoRelationshipMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import javax.annotation.Resource;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@Slf4j
@SpringBootTest(classes=WeiboSpringboot192Application.class)
public class utilTest {
    @Resource
    private QueryMapper queryMapper;
    @Resource
    private RepoRelationshipMapper repoRelationshipMapper;
    @Test
    public void toJson(){
        String bw_id="4529050938509504";
        List<Query> query=queryMapper.selectQueryByBw_id(bw_id);
        String center_bw_id="4523566437432908";
        List<RepoRelationship> repoRelationships=repoRelationshipMapper.selectRepoRelationshipByCenter_bw_id(center_bw_id);
        System.out.println(new createJson().toJson(repoRelationships,query));
    }
}
