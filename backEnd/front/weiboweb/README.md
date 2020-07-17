# web层项目说明 V1.0

## 项目概况

### 项目功能

该项目文件进充当后端的触发，即仅包含web层代码。

主要功能为接收来自前端发送的请求，并将请求传送至server（数据访问层），待server检索出数据后，接收来自server返回的结果，并将结果返回给前端。



### 技术栈

整体框架：springboot

后端内部通信：mina



### 项目结构

```
src/main/java/com/weibo/
	controller/
		WeiboController.java     - 主要的接口，用于与前端进行数据交流
	BsConfig/
		BsConfig.java      - 基础设置
		FileHelper.java    - 读取配置文件用
    NetClient/
    	Client.java      - 将WeiboController接收到的内容通过mina传给server，再接受server的回传
    	ClientCodeFactory.java    - 控制编解码
    	ClientDecoder.java     - 解码
    	ClientEncoder.java     - 编码
    	ClientHandler.java     - 逻辑处理，目前并未使用
    NSHEAD/
    	Nshead.java       - mina通讯的消息头
    QDataType/
    	rank_response_vs.java     - 封装回传结果的javabean
    	Request_query.java       - 封装传入请求的javabean
    	ResponseCode.java        - mina通讯用的参数控制
    SearchConfig/
    	SearchConfig.java       - 检索的参数控制，目前并未修改
    UTIL/
    	Util.java      - 工具类，服务于mina通讯
```



### 端口使用

8288：开放端口，用于与前端进行连接

8770：用于mina通讯



## 项目详细说明

### 接口说明

#### 接口文档

##### 1. 根据检索词获取前十条微博

url：http://121.46.19.26:8288/getWeibo/{keyword}

请求方式：http get

请求参数：

| 参数    | 说明   |
| ------- | ------ |
| keyword | 检索词 |

返回参数：

| 参数      | 说明     |
| --------- | -------- |
| bw_id     | 微博id   |
| user_id   | 用户id   |
| user_name | 用户名   |
| content   | 微博正文 |



##### 2. 根据微博id获取其转发关系

url：http://121.46.19.26:8288/getRelationship/{weiboId}

请求方式：http get

请求参数：

| 参数    | 说明   |
| ------- | ------ |
| weiboId | 微博id |

返回参数：

| 参数           | 说明                   |
| -------------- | ---------------------- |
| fs_fans_count  | 转发用户的粉丝数       |
| raw_text       | 转发时写的内容         |
| fans_count     | 被转发的粉丝数         |
| screen_name    | 被转发的用户名         |
| level          | 转发层次               |
| fs_user_id     | 转发用户的id           |
| origin         | 是否为原创             |
| bw_id          | 被转发微博的id         |
| created_at     | 该记录生成的日期       |
| reposts_count  | 被转发微博的被转发次数 |
| fs_screen_name | 转发用户的用户名       |
| fs_bw_id       | 转发后的微博id         |



##### 3. 根据微博id获取该微博信息

url：http://121.46.19.26:8288/getRelationshipBody/{weiboId}

请求方式：http get

请求参数：

| 参数    | 说明   |
| ------- | ------ |
| weiboId | 微博id |

返回参数：

| 参数       | 说明             |
| ---------- | ---------------- |
| bw_id      | 微博id           |
| user_id    | 用户id           |
| user_name  | 用户名           |
| created_at | 该记录的创建日期 |
| content    | 微博内容         |



#### 接口代码

接口使用controller层进行实现，主要用于和前端进行数据交换

```java
@CrossOrigin("*")  //用于跨域，“*”表示允许任何域进行访问
@RestController   //表明这是个controller类，并采用返回json的风格
public class WeiboController {

    private static final Logger log = LoggerFactory.getLogger(WeiboController.class);  //写日志用
    @Autowired
    private Client clt;  //mina的Client端
    @Autowired
    private Request_query qrst;  //封装请求的javabean
    @Autowired
    private rank_response_vs qrep;   //封装结果的javabean

    //根据检索词获取前十条微博
    @RequestMapping(value = "/getWeibo/{query}",method = RequestMethod.GET)  //表明url和请求方式（get）
    public String query(@PathVariable("query")String query){
        log.info("收到query请求："+query);
        //封装请求，详细可见本文档的javabean说明
        qrst.set_query_type("keyword");
        qrst.set_query(query);
        //调用mina，传入请求，获取结果
        qrep = clt.process(qrst);
        log.info("成功连接服务器，获取检索结果："+qrep.getJsonInfo());
        //将结果返回给前端
        return qrep.getJsonInfo();
    }

    //根据微博id获取其转发关系
    @RequestMapping(value = "/getRelationship/{weiboId}",method = RequestMethod.GET)
    public String relationship(@PathVariable("weiboId")String weiboId){
        //此处代码省略，使用方式同上接口
    }

    //根据微博id获取该微博信息
    @RequestMapping(value = "/getRelationshipBody/{weiboId}",method = RequestMethod.GET)
    public String relationshipBody(@PathVariable("weiboId")String weiboId){
        //此处代码省略，使用方式同上接口
    }
}
```



### javabean说明

本项目主要使用了两个javabean，分别用于封装请求和结果

##### 1. 封装请求

```java
@Component  //放入容器
public class Request_query {

    //请求的具体内容
    @Expose
    @SerializedName("query")
    String query;

    //说明请求的类型，类型有以下三种可选
    //query：根据检索词获取前十条微博
    //bw_id：根据微博id获取其转发关系
    //rpBody：根据微博id获取该微博信息
    @Expose
    @SerializedName("query_type")
    String query_type;
    
    //此处省略getter、setter和toString方法
}
```

##### 2. 封装结果

```java
@Component  //放入容器
public class rank_response_vs {

    //结果内容
	@Expose
	@SerializedName("jsonInfo")
	String jsonInfo;

    //mina通讯用
	@SerializedName("status")
	int status;
    
    //此处省略getter、setter和toString方法
}
```



## mina通讯说明

在mina通讯过程中，数据结点充当server，触发充当client。

contoller类接收前端传过来的请求后，通过mina框架的client将请求传送给数据节点的server；server检索成功后，通过mina框架将结果返回给web层mina框架的client，最后contoller将接收到的结果返回给前端。