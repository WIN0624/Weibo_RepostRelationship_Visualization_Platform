package com.weibo.weibo.util;

import com.google.gson.Gson;
import com.weibo.weibo.entity.Query;
import com.weibo.weibo.entity.RepoRelationship;

import java.util.List;

/**
 * 生成json用的
 */
public class createJson {
    private Gson gson = new Gson();

    public  String toJson(List list){
        return gson.toJson(list);
    }

    /**
     * 为请求类型为keyword的结果生成JSON
     * @param pageCount
     * @param queries
     * @return
     */
    public  String toJson(String pageCount, List<Query> queries){
        String jsonInfo=toJson(queries),
                jsonPageCount="[{\"pageCount\":\""+pageCount+"\"}";
        if(queries.size()==0) jsonInfo=jsonPageCount+"]";
        else jsonInfo=jsonPageCount+","+jsonInfo.substring(1);
        return jsonInfo;
    }

    /**
     * 为请求类型为bw_id的生成结果JSON
     * @param relationships
     * @param query
     * @return
     */
    public  String toJson(List<RepoRelationship> relationships,List<Query> query){
        String queryJson=toJson(query),
                relationshipsJson=toJson(relationships);
        String jsonInfo=queryJson.substring(0, queryJson.length()-1)+",{\"relationship\":"+relationshipsJson+"}]";
        return jsonInfo;
    }
}
