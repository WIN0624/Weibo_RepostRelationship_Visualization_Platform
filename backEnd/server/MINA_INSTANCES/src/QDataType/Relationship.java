package QDataType;

import com.baidu.gson.annotations.Expose;
import com.baidu.gson.annotations.SerializedName;

/**
 * @author lijy3
 * @version 1.0
 * @ClassName Relationship 用于检索微博关系的实体类
 * @Description TODO
 * @date 2020/5/26 11:53
 */
public class Relationship {

    private String user_id;
    private  String screen_id;
    private  String bw_id;
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

    public String getScreen_id() {
        return screen_id;
    }

    public void setScreen_id(String screen_id) {
        this.screen_id = screen_id;
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
}
