package QDataType;

import java.util.List;
import com.baidu.gson.annotations.Expose;
import com.baidu.gson.annotations.SerializedName;

/**
 * 这个是前端主机传给后端主机“检索微博转发关系”的实体类
 * 假设传入的是微博id
 */

public class R_Request {
       
	@Expose
	@SerializedName("bw_id")
	private String bw_id;

	public void setBw_id(String bw_id){
		this.bw_id=bw_id;
	}
	
	public String getBw_id() {
		return bw_id;
	}

	
}
