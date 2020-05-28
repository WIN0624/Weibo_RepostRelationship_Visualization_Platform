package QDataType;

import java.util.List;

import com.baidu.gson.annotations.Expose;
import com.baidu.gson.annotations.SerializedName;

/**
 * 这个是后端主机返回前端主机“检索微博转发关系”的实体类
 * 这里的我暂时把所有内容都标了，但实际上可能最后只返回一个封装好的string类型的实体，这个需要重新定义
 */

public class R_Response {

	@Expose
	@SerializedName("screen_name")
	String screen_name;

	@Expose
	@SerializedName("fs_screen_name")
	String fs_screen_name;

	@Expose
	@SerializedName("fs_user_id")
	String fs_user_id;

	@Expose
	@SerializedName("fs_bw_id")
	String fs_bw_id;

	@Expose
	@SerializedName("level")
	String level;

	@Expose
	@SerializedName("origin")
	String origin;

	@Expose
	@SerializedName("user_id")
	String user_id;

	@Expose
	@SerializedName("bw_id")
	String bw_id;

	@Expose
	@SerializedName("reposts_count")
	String reposts_count;


	private long errorNumber;//不为0的都有问题。
	
	public long getErrorNumber() {
		return errorNumber;
	}

	public void setErrorNumber(long errorNumber) {
		this.errorNumber = errorNumber;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getFs_screen_name() {
		return fs_screen_name;
	}

	public void setFs_screen_name(String fs_screen_name) {
		this.fs_screen_name = fs_screen_name;
	}

	public String getFs_user_id() {
		return fs_user_id;
	}

	public void setFs_user_id(String fs_user_id) {
		this.fs_user_id = fs_user_id;
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

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getBw_id() {
		return bw_id;
	}

	public void setBw_id(String bw_id) {
		this.bw_id = bw_id;
	}

	public String getReposts_count() {
		return reposts_count;
	}

	public void setReposts_count(String reposts_count) {
		this.reposts_count = reposts_count;
	}

	@Override
	public String toString() {
		return "R_Response{" +
				"screen_name='" + screen_name + '\'' +
				", fs_screen_name='" + fs_screen_name + '\'' +
				", fs_user_id='" + fs_user_id + '\'' +
				", fs_bw_id='" + fs_bw_id + '\'' +
				", level='" + level + '\'' +
				", origin='" + origin + '\'' +
				", user_id='" + user_id + '\'' +
				", bw_id='" + bw_id + '\'' +
				", reposts_count='" + reposts_count + '\'' +
				", errorNumber=" + errorNumber +
				'}';
	}
}
