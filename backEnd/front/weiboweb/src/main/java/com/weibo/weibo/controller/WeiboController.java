package com.weibo.weibo.controller;


import com.weibo.weibo.grcp.QueryRequest;
import com.weibo.weibo.grcp.QueryResponse;
import com.weibo.weibo.grcp.SearchServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * web层：用于接收ajax请求
 */
@Slf4j
@CrossOrigin("*")
@RestController
public class WeiboController {
    //从容器中获取调用GRpc stub
    @Autowired
    SearchServiceGrpc.SearchServiceBlockingStub searchServiceBlockingStub;

    @GetMapping("/getWeibo/{query}/{page}")
    public String query(@PathVariable("query")String query, @PathVariable("page")String page){
        log.info("Receive query request : "+query+" page="+page);
        long start = System.currentTimeMillis();
        QueryResponse response = this.searchServiceBlockingStub
                .searchQuery(QueryRequest.newBuilder()
                .setQuery(query).setQueryType("keyword").setPage(page).build());
        long end = System.currentTimeMillis();
        log.info("Search result : "+response.getResponse());
        log.info("Retrieval time: "+(end-start));
        return response.getResponse();
    }

    @GetMapping("/getRelationship/{weiboId}")
    public String relationship(@PathVariable("weiboId")String weiboId){
        log.info("Receive relationship request : "+weiboId);
        long start = System.currentTimeMillis();
        QueryResponse response = this.searchServiceBlockingStub
                .searchQuery(QueryRequest.newBuilder()
                        .setQuery(weiboId).setQueryType("relationship").build());
        long end = System.currentTimeMillis();
        log.info("Search result : "+response.getResponse());
        log.info("Retrieval time: "+(end-start));
        return response.getResponse();
    }
}
