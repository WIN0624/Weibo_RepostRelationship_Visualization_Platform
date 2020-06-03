package com.weibo.QDataType;

import java.util.List;
import com.baidu.gson.annotations.Expose;
import com.baidu.gson.annotations.SerializedName;

/**
 * 121发送给192的微博内容请求
 */

public class Request_query {

    @Expose
    @SerializedName("query")
    String query;

    @Expose
    @SerializedName("query_type")
    String query_type;

    public void set_query(String query){
        this.query=query;
    }
    
    public String get_query(){
        return query;
    }

    public String get_query_type() {
        return query_type;
    }

    public void set_query_type(String query_type) {
        this.query_type = query_type;
    }

    @Override
    public String toString() {
        return "Request_query{" +
                ", query_type='" + query_type + '\'' +
                "query='" + query + '\'' +
                '}';
    }
}
