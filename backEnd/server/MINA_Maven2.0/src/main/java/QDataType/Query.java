package QDataType;

import org.json.JSONObject;

/**
 * @author lijy3
 * @version 1.0
 * @ClassName Query 微博内容实体类
 * @Description TODO
 * @date 2020/5/24 16:11
 */
public class Query {
    private String query;
    private  String user_id;
    private  String user_name;
    private  String bw_id;
    private String bw_text;

    public Query() {}

    public Query(String id, String query, String user_id, String user_name, String bw_id, String bw_text) {
        this.query = query;
        this.user_id = user_id;
        this.user_name = user_name;
        this.bw_id = bw_id;
        this.bw_text = bw_text;
    }

    @Override
    public String toString() {
        return "Query{" +
                "query='" + query + '\'' +
                ", user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                ", bw_id=" + bw_id +
                ", bw_text='" + bw_text + '\'' +
                '}';
    }


    public String toJsonString(){
        return '{' +
                '\"'+"query"+'\"'+"=" +'\"'+query+'\"'+
                ','+'\"'+"user_id"+'\"'+"=" +'\"'+user_id+'\"'+
                ','+'\"'+"user_name"+'\"'+"=" +'\"'+user_name+'\"'+
                ','+'\"'+"bw_id"+'\"'+"=" +'\"'+bw_id+'\"'+
                ','+'\"'+"bw_text"+'\"'+"=" +'\"'+bw_text+'\"'+
                '}';
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("query",query);
        jsonObject.put("user_id",user_id);
        jsonObject.put("user_name",user_name);
        jsonObject.put("bw_id",bw_id);
        jsonObject.put("bw_text",bw_text);
        return  jsonObject;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getBw_id() {
        return bw_id;
    }

    public void setBw_id(String bw_id) {
        this.bw_id = bw_id;
    }

    public String getBw_text() {
        return bw_text;
    }

    public void setBw_text(String bw_text) {
        this.bw_text = bw_text;
    }
}
