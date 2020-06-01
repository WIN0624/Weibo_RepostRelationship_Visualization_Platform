package com.weibo.SearchConfig;

public class SearchConfig {
	
	/**
	 * 触发词的匹配模式:1）15 宽泛匹配；2）31 短语匹配；3）63 精确匹配；
	 */
	public static final int QUERY_SEG_WMATCH_JINGZHUN = 63;
	public static final int QUERY_SEG_WMATCH_DUANYU = 31;
	public static final int QUERY_SEG_WMATCH_GUANGFAN = 15;
	
	/**
	 * WordInfo的匹配模式:63精准匹配;2短语匹配;3广泛匹配;
	 */
	public static final int WORDINFO_WMATCH_JINGZHUN = 63;
	public static final int WORDINFO_WMATCH_DUANYU = 31;
	public static final int WORDINFO_WMATCH_GUANGFAN = 15;
	
	/**
	 * 计费最低价格
	 */
	public static final int DEFAULT_MIN_PRICE = 10;
	
	/**
	 * 计费附加价格
	 */
	public static final int DEFAULT_ADD_PRICE = 1;

	/**
	 * 是否继承上一级
	 */
	public static final int EXTEND_NOT_OK = 0;
	public static final int EXTEND_OK = 1;
	
	/**
	 * 全文检索时返回是最大结果数
	 */
	public static final int FULLTEXT_SEARCH_MAX_RESULT = 500;
	
	public static final int QSERVER_RANDOM_RETRY_NUM = 1;

	/**
	 * 请求Q server得到的body中的第一个包的固定长度
	 */
	public static final int QSERVER_BODY_CONTROLPACK_LEN = 44; 
	
	/**
	 * 与Q server、UI交互的字符编码
	 */
	public static final String Q_SERVER_COMPACK_CHARSET = "UTF8";//char set
	public static final String UI_COMPACK_CHARSET = "UTF8";//char set
	
}
