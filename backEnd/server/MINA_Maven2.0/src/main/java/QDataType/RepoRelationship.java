package QDataType;

import org.json.JSONObject;

/**
 * @author lijy3
 * @version 1.0
 * @ClassName Relationship 
 * @Description TODO
 * @date 2020/5/26 11:53
 */

public class RepoRelationship {
    //user_id,screen_name,bw_id,origin,reposts_count,
    // fs_user_id,fs_screen_name,fs_bw_id,fans_count,
    // fs_fans_count,level,raw_text,created_at
    private String user_id;
    private String screen_name;
    private String bw_id;
    private String origin;
    private String reposts_count;
    private String fs_user_id;
    private String fs_screen_name;
    private String fs_bw_id;
    private String fs_count;
    private String fs_fans_count;
    private String level;
    private String raw_text;
    private String created_at;

    public String getFs_count() {
        return fs_count;
    }

    public void setFs_count(String fs_count) {
        this.fs_count = fs_count;
    }

    public String getFs_fans_count() {
        return fs_fans_count;
    }

    public void setFs_fans_count(String fs_fans_count) {
        this.fs_fans_count = fs_fans_count;
    }

    public String getRaw_text() {
        return raw_text;
    }

    public void setRaw_text(String raw_text) {
        this.raw_text = raw_text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getBw_id() {
        return bw_id;
    }

    public void setBw_id(String bw_id) {
        this.bw_id = bw_id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(String reposts_count) {
        this.reposts_count = reposts_count;
    }

    public String getFs_user_id() {
        return fs_user_id;
    }

    public void setFs_user_id(String fs_user_id) {
        this.fs_user_id = fs_user_id;
    }

    public String getFs_screen_name() {
        return fs_screen_name;
    }

    public void setFs_screen_name(String fs_screen_name) {
        this.fs_screen_name = fs_screen_name;
    }

    public String getFs_bw_id() {
        return fs_bw_id;
    }

    public void setFs_bw_id(String fs_bw_id) {
        this.fs_bw_id = fs_bw_id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "user_id='" + user_id + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", bw_id='" + bw_id + '\'' +
                ", origin='" + origin + '\'' +
                ", reposts_count='" + reposts_count + '\'' +
                ", fs_user_id='" + fs_user_id + '\'' +
                ", fs_screen_name='" + fs_screen_name + '\'' +
                ", fs_bw_id='" + fs_bw_id + '\'' +
                ", fans_count='" + fs_count + '\'' +
                ", fs_fans_count='" + fs_fans_count + '\'' +
                ", level='" + level + '\'' +
                ", raw_text='" + raw_text + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public String toJsonString(){
        return '{' +
                ','+'\"'+"user_id"+'\"'+"=" +'\"'+user_id+'\"'+
                ','+'\"'+"screen_name"+'\"'+"=" +'\"'+ screen_name +'\"'+
                ','+'\"'+"bw_id"+'\"'+"=" +'\"'+bw_id+'\"'+
                ','+'\"'+"origin"+'\"'+"=" +'\"'+origin+'\"'+
                ','+'\"'+"reposts_count"+'\"'+"=" +'\"'+reposts_count+'\"'+
                ','+'\"'+"fs_user_id"+'\"'+"=" +'\"'+fs_user_id+'\"'+
                ','+'\"'+"fs_screen_name"+'\"'+"=" +'\"'+fs_screen_name+'\"'+
                ','+'\"'+"fs_bw_id"+'\"'+"=" +'\"'+fs_bw_id+'\"'+
                ','+ '\"'+"fans_count"+'\"'+"="+'\"'+ fs_count + '\"' +
                ','+ '\"'+"fs_fans_count"+'\"'+"="+'\"'+fs_fans_count + '\"' +
                ','+'\"'+"level"+'\"'+"=" +'\"'+level+'\"'+
                ','+ '\"'+"raw_text"+'\"'+"="+'\"'+ raw_text + '\"' +
                ','+ '\"'+"created_at"+'\"'+"="+'\"'+created_at + '\"' +
                '}';
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("user_id",user_id);
        jsonObject.put("screen_name", screen_name);
        jsonObject.put("bw_id",bw_id);
        jsonObject.put("origin",origin);
        jsonObject.put("reposts_count",reposts_count);
        jsonObject.put("fs_user_id",fs_user_id);
        jsonObject.put("fs_screen_name",fs_screen_name);
        jsonObject.put("fs_bw_id",fs_bw_id);
        jsonObject.put("fans_count",fs_count);
        jsonObject.put("fs_fans_count",fs_fans_count);
        jsonObject.put("level",level);
        jsonObject.put("raw_text", raw_text);
        jsonObject.put("created_at",created_at);
        return  jsonObject;
    }
}

