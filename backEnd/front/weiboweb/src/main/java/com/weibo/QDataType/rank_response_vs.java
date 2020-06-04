package com.weibo.QDataType;


import com.baidu.gson.annotations.Expose;
import com.baidu.gson.annotations.SerializedName;
import org.springframework.stereotype.Component;

/**
 * 192返回121的数据，主要是返回json
 */
@Component
public class rank_response_vs {

	@Expose
	@SerializedName("jsonInfo")
	String jsonInfo;

	@SerializedName("status")
	int status;

	public long start;

	public String getJsonInfo() {
		return jsonInfo;
	}

	public void setJsonInfo(String jsonInfo) {
		this.jsonInfo = jsonInfo;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
