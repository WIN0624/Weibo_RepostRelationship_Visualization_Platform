package com.weibo.weibo.service;

import com.weibo.weibo.WeiboSpringboot192Application;


import com.weibo.weibo.grcp.QueryRequest;
import com.weibo.weibo.grcp.QueryResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Before;
// 注意！ 注意下面这个包！不要写成org.junit.jupiter.api.Test;！
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;


// 该测试需要使用jdk6||jdk7||jdk8
@SpringBootTest(classes= WeiboSpringboot192Application.class)
@Slf4j
@RunWith(JMockit.class)
public class SearchServiceImplTest {
    // final class 需要使用Mocked来进行
    @Mocked
    QueryRequest queryRequest;
    @Mocked
    SearchServiceImpl searchService;
    @Mocked
    StreamObserver<QueryResponse> responseObserver;

    @Before
    public void before(){
        queryRequest= QueryRequest.newBuilder().build();
    }

    @Test
    public void searchQueryOnlyMysql(){
        queryRequest.toBuilder().setPage("");
        queryRequest.toBuilder().setQueryType("relationship");
        queryRequest.toBuilder().setQuery("4523566437432908");
        searchService.searchQuery(queryRequest,responseObserver);
        Assert.assertNotNull(searchService.getQueryResponse().getResponse());
        // 验证执行次数
        new Verifications(){
            {
                searchService.searchQuery(queryRequest,responseObserver);
                times=1;
            }
        };
    }

    @Test
    public void searchQueryMysqlAndRedis(){
        queryRequest.toBuilder().setPage("1");
        queryRequest.toBuilder().setQueryType("keyword");
        // 在这里填上关键词
        queryRequest.toBuilder().setQuery("");
        searchService.searchQuery(queryRequest,responseObserver);

        // 在这里Assert

        // 验证执行次数
        new Verifications(){
            {
                searchService.searchQuery(queryRequest,responseObserver);
                times=1;
            }
        };
    }
}
