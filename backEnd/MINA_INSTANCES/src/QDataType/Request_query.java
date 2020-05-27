package QDataType;

import java.util.List;
import com.baidu.gson.annotations.Expose;
import com.baidu.gson.annotations.SerializedName;

/**
 * 前端主机发送的微博内容请求
 */

public class Request_query {

    @Expose
    @SerializedName("query")
    String query;

    public void set_query(String query){
        this.query=query;
    }
    
    public String get_query(){
        return query;
    }

    @Override
    public String toString() {
        return "Request_query{" +
                "query='" + query + '\'' +
                '}';
    }
}
