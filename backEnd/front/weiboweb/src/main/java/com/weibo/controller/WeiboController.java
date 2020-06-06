package com.weibo.controller;

import com.weibo.NetClient.Client;
import com.weibo.QDataType.Request_query;
import com.weibo.QDataType.rank_response_vs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * web层：用于接收ajax请求
 */

@CrossOrigin("*")
@RestController
public class WeiboController {

    private static final Logger log = LoggerFactory.getLogger(WeiboController.class);
    @Autowired
    private Client clt;
    @Autowired
    private Request_query qrst;
    @Autowired
    private rank_response_vs qrep;

    @RequestMapping(value = "/getWeibo/{query}",method = RequestMethod.GET)
    public String query(@PathVariable("query")String query){
        log.info("收到query请求："+query);
        qrst.set_query_type("keyword");
        qrst.set_query(query);
        qrep = clt.process(qrst);
        log.info("成功连接服务器，获取检索结果："+qrep.getJsonInfo());
        return qrep.getJsonInfo();
    }

    @RequestMapping(value = "/getRelationship/{weiboId}",method = RequestMethod.GET)
    public String relationship(@PathVariable("weiboId")String weiboId){
        log.info("收到relationship请求："+weiboId);
        qrst.set_query_type("bw_id");
        qrst.set_query(weiboId);
        qrep = clt.process(qrst);
        log.info("成功连接服务器，获取检索结果："+qrep.getJsonInfo());
        return qrep.getJsonInfo();
    }

    @RequestMapping(value = "/getRelationshipBody/{weiboId}",method = RequestMethod.GET)
    public String relationshipBody(@PathVariable("weiboId")String weiboId){
        log.info("收到relationshipBody请求："+weiboId);
        qrst.set_query_type("rpBody");
        qrst.set_query(weiboId);
        qrep = clt.process(qrst);
        log.info("成功连接服务器，获取检索结果："+qrep.getJsonInfo());
        return qrep.getJsonInfo();
    }
}
