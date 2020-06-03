package QDataType;

import org.json.JSONObject;
import com.baidu.gson.annotations.Expose;
import com.baidu.gson.annotations.SerializedName;

/**
 * @author lijy3
 * @version 1.0
 * @ClassName Relationship 微博关系的实体类
 * @Description TODO
 * @date 2020/5/26 11:53
 */

public class Relationship {
    private String user_id;
    private String screen_name;
    private String bw_id;
    private String origin;
    private String reposts_count;
    private String fs_user_id;
    private String fs_screen_name;
    private String fs_bw_id;
    private String level;

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
                ", level='" + level + '\'' +
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
                ','+'\"'+"level"+'\"'+"=" +'\"'+level+'\"'+
                '}';
    }

    public JSONObject toJsonObject(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("user_id",user_id);
        jsonObject.put("screen_name", screen_name);
        jsonObject.put("bw_id",bw_id);
        jsonObject.put("origin",origin);
        jsonObject.put("reposts_count",reposts_count);
        jsonObject.put("fs_user_id",fs_bw_id);
        jsonObject.put("fs_screen_name",fs_screen_name);
        jsonObject.put("fs_bw_id",fs_bw_id);
        jsonObject.put("level",level);
        return  jsonObject;
    }
}

