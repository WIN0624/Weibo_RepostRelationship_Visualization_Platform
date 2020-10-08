package com.weibo.weibo.service;

import com.weibo.weibo.entity.Query;
import com.weibo.weibo.entity.RepoRelationship;
import com.weibo.weibo.mapper.QueryMapper;
import com.weibo.weibo.mapper.RepoRelationshipMapper;
import com.weibo.weibo.grcp.QueryRequest;
import com.weibo.weibo.grcp.QueryResponse;
import com.weibo.weibo.grcp.SearchServiceGrpc;
import com.weibo.weibo.redisDao.RedisDao;
import com.weibo.weibo.util.createJson;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 接收grpc请求并返回
 */
@GRpcService
@Slf4j
public class SearchServiceImpl extends SearchServiceGrpc.SearchServiceImplBase{
    @Autowired
    private QueryMapper queryMapper;
    @Autowired
    private RepoRelationshipMapper repoRelationshipMapper;
    @Autowired
    private RedisDao redisDao;

    private createJson json=new createJson();
    @Override
    public void searchQuery(QueryRequest request, StreamObserver<QueryResponse> responseObserver) {
        //获取请求类型
        String queryType = request.getQueryType();
        //获取请求内容
        String query = request.getQuery();
        String page=request.getPage();
        log.info("Receive queryType = "+queryType+", query = "+query);
        //返回结果初始化
        String jsonInfo = "";

        //分情况

        //分页检索，每次返回二十条微博
        if(queryType.equals("keyword")){
            log.info("Start searching keyword");
            String keyword = query;
            //redis方法，传入一个(query,page)，返回list
            List<String> keys = redisDao.getIDListOnPage(keyword, Integer.parseInt(page));
            log.info("redis weibo list: "+keys.toString());
            //redis方法，传入一个(query)，返回总页数
            String pageCount=""+ redisDao.getPageNumber(keyword);
            log.info("redis page count: "+pageCount);
            //mysql方法
            jsonInfo=json.toJson(pageCount,queryMapper.selectQueryByBw_ids(keys));
        }
        //获取某条微博的关系和正文
        else if(queryType.equals("relationship")){
            log.info("Start searching relationship");
            String bwId = query;
            //调用mysql方法，获取相关id的关系
            List<RepoRelationship> relationships=repoRelationshipMapper.selectRepoRelationshipByCenter_bw_id(bwId);
            //调用mysql方法，获取相关id的正文
            List<Query> a_query=queryMapper.selectQueryByBw_id(bwId);
            jsonInfo=json.toJson(relationships,a_query);
        }
        else {
            log.error("Receive the wrong message!");
        }
        log.info("result: "+jsonInfo);
        //把结果放入response
        QueryResponse response = QueryResponse.newBuilder().setResponse(jsonInfo).build();
        //放入response，传回客户端
        responseObserver.onNext(response);
        //表示此次连接结束
        responseObserver.onCompleted();
    }
}
