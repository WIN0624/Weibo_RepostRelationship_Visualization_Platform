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

## 项目代码详细说明

### BsConfig部分说明

BsConfig用于处理配置文件，分为BsConfig和FileHelper两个类，FileHelper用于直接读取及修改配置文件，BsConfig用于处理配置文件中的数据供其它类调用。

#### FileHelper.java

`FileHelper`类实现了`readFileContent`读取文件内容、`writeFileContent`将内容写入文件和`exists`判断文件是否存在三个功能。read与write通过方法重载实现了对传入String类和File类的支持，二者通过文件输入输出流套接`BufferredReader`/`BufferredWriter`实现文件读写；`exists`方法则直接通过File类的`exists`方法实现。

代码结构：

```java
public static String readFileContent(String fileName)
//直接用String类的文件名新建File类的对象实例并调用重载方法
public static String readFileContent(File file)
//使用套接有输入流的BufferredReader读取配置文件内容
public static void writeFileContent(String fileName, String content)
//将content添加到名为filename的文件中，仍是新建File类的对象实例，调用重载方法
public static void writeFileContent(File file, String content)
//使用套接有输出流的BufferredWriter输出内容
public static boolean exists(String file)
//直接使用File类的exists方法判断文件是否存在
```



#### BsConfig.java

`BsConfig`类使用`load`方法（实质上是调用了`reload`方法）加载配置文件并读入一个`HashMap`类的实例中，其中加载的具体实现是：先使用`FileHelper`类读取json格式的配置文件，然后使用第三方Gson包将json数据转换成HashMap类的实例。而后再使用多个不同类型的`get`方法从HashMap实例中取出数据以供其它类使用。另外，该类中创建了一个`ReloadThread`子线程用于每隔一秒判断一次配置文件是否有修改并读入修改过的配置文件。

代码主要结构：

```java
protected static Gson gson = new Gson();
//新建Gson实例用于从配置文件中读取json数据
static HashMap<String, String> data;
//新建HashMap实例用于存放json数据
static long lastModified = 0;
//新建长整数存放配置文件最后一次被修改的时间，用于判断是否需要重新加载
public static void load(String conf)
//调用重载方法并添加参数true
public static void load(String conf, boolean check)
//为避免代码重复，此处直接调用reload方法读取配置文件
public static void reload()
//判断lastModified是否更新过，若更新过则通过FileHelper重新读入配置文件
static class ReloadThread extends Thread
//新建ReloadThread子线程，每隔一秒运行一次外部类的reload方法
public static String get(String key)
//从配置文件中获取一个字符串数据，即从HashMap的实例data中读取一个值
public static int getInt(String key)
//从配置文件中获取一个整型数据，实质上是读取一个字符串后将其转化为整型
public static boolean getBool(String key)
//从配置文件中获取一个布尔型数据，实质上是读取一个字符串后将其转化为布尔型
public static long getLong(String key)
//从配置文件中获取一个长整型数据，实质上是读取一个字符串后将其转化为长整型
public static float getFloat(String key)
//从配置文件中获取一个单精度浮点型数据，实质上是读取一个字符串后将其转化为单精度浮点型
```



### NetClient部分说明

#### NetClient功能说明

#### Client.java说明

##### Client.java功能描述

>Client.java部分的代码用于
>Client.java将WeiboController接收到的Request_query对象qRequest，并转换为rank_response_vs实例，将其通过mina传给server，再接受server的回传。对编码过滤器和会话设置进行初始配置。

##### Client.java代码说明

```java
public class Client {

    //静态方法创建日志实例
    private final static Logger logger = Logger.getLogger(Client.class);

    //private static int PORT = 3600;//3600;
    //private static String HOST = "10.48.145.21:10.48.145.21";//"10.46.167.36";

    private static Random random = new Random();

    NioSocketConnector connector;

    ConnectFuture cf;

    public Client() {
    }

    //此方法用于将参数接收的Request_query类型实例qRequest转换为rank_response_vs类型实例
    public rank_response_vs process(Request_query qRequest) {
        //此处省略部分初始化操作
        while((!haveConnect)&&(retryNum!=0)){
            try{
                init();

                // 此处修改server的ip
                String connectHost = "192.168.1.116";
                //hostList[(randomNumber+retryNum)%hostNumber];

                logger.info("Try to connect. retryNum=" + retryNum + ",connectHost=" + connectHost);
                int PORT = 8770;
                //int PORT = 8276;//Integer.parseInt(BsConfig.Q_SERVER_PORT);
                //获取一个连接对象cf
                cf = connector.connect(new InetSocketAddress(connectHost, PORT));
                System.out.println("First Step!");
                //等待异步执行的结果返回
                cf.awaitUninterruptibly();
                System.out.println("Second Step!");
                //在会话中写入qRequest
                cf.getSession().write(qRequest);
                System.out.println("Third Step!");
                //让当前线程同步等待Client的close事件，即连接断开
                cf.getSession().getCloseFuture().awaitUninterruptibly();
                System.out.println("Finish Connecting the server!");
                //server传给client最后会放到这里，调试的时候可以设为断点查看
                qResponse = (rank_response_vs) cf.getSession().getAttribute("res");
                //qResponse = (rank_response_vs) cf.getSession().read().getMessage();
                haveConnect = true;
            }catch(Exception e1){
                e1.printStackTrace();
                haveConnect = false;
                logger.error("ConnectException");
                System.out.println("ERROR Happen!");
            }
            //不关闭的话会运行一段时间后抛出，too many open files异常，导致无法连接
            connector.dispose();
            logger.info("connector.dispose()");
            retryNum--;
        }

        return qResponse;
    }

    //对编码过滤器、Socket会话进行配置
    private void init(){
        connector = new NioSocketConnector();
        logger.info("ready to connect. new NioSocketConnector");
        //设置编码过滤器
        connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new ClientCodecFactory()));
        //ExecutorFilter保证在处理handler的时候是独立线程,session.wirte变成并行处理;
        //connector.getFilterChain().addLast( "threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
        SocketSessionConfig config = (SocketSessionConfig) connector.getSessionConfig();
        //此处省略部分会话配置和编码过滤器配置
    }

    //用户通信模拟的方法，方法体省略
    public Request_query process_exp()
}
```

#### ClientCodecFactory.java说明

##### ClientCodecFactory.java功能描述

>ClientCodecFactory.java用于
>Mina通信中编解码器的申明和注册

##### ClientCodecFactory.java代码说明

```java
public class ClientCodecFactory extends DemuxingProtocolCodecFactory {
    //创建解码器
    private MessageDecoder decoder = new ClientDecoder();

    //创建编码器
    private MessageEncoder<Request_query> encoder = new ClientEncoder();

    //注册编码器和解码器
    public ClientCodecFactory() {

        addMessageDecoder(decoder);
        addMessageEncoder(Request_query.class, encoder);
    }
}
```

#### ClientEncoder.java说明

##### ClientEncoder.java功能描述

>ClientEncoder.java用于
>实现编码过程，按照协议拼装传输内容到IoBuffer 缓冲区，然后调用ProtocolEncoderOutput 的write()方法输出字节流。

##### 编码器编写步骤

>ClientEncoder编写步骤包括

1. 将 encode()方法中的message对象转换为指定的对象类型；

2. 创建IoBuffer缓冲区对象，并设置为自动扩展；

3. 将需要传输的对象（此代码中为nshead）各个部分按照指定的应用层协议进行组装，并put()到IoBuffer 缓冲区；

4. 组装数据完毕之后，调用flip()方法，为输出做好准备，切记在write()方法之前，要调用IoBuffer 的flip()方法，否则缓冲区的position 的后面是没有数据可以用来输出的，你必须调用flip()方法将position 移至0，limit 移至刚才的position。

5. 最后调用ProtocolEncoderOutput的write()方法输出IoBuffer缓冲区实例。

##### ClientEncoder.java代码说明

```java
public class ClientEncoder implements MessageEncoder<Request_query> {
    //静态方法创建日志实例
    private final static Logger logger = Logger.getLogger(ClientEncoder.class);

    private final static byte[] PROVIDER = new byte[] { 'B', 'a', 'i', 'd', 'u', '-', 'V', 'S', '-', 'D', 'A', 0, 0, 0, 0, 0 };

    private final static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create();

    public final static Gson inner_gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create();

    private final static Mcpack mcpack = new Mcpack();

    //实现encode方法
    @Override
    public void encode(IoSession session, Request_query message, ProtocolEncoderOutput out) throws Exception {
        long start = System.nanoTime();
        try {
            //String client = session.getRemoteAddress().toString();

            String query = message.get_query();
            //String query = message.get_query();

            //创建IoBuffer缓冲区对象，并设置为自动扩展
            IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);

            //创建nshead
            Nshead nshead = new Nshead();

            /*
             * 控制部分打包
            */
            /*
             * 数据部分打包
             */
            JsonElement realJson = gson.toJsonElement(message);
            byte[] realDataBinary = mcpack.toMcpack(SearchConfig.Q_SERVER_COMPACK_CHARSET, realJson);

            /*
             * 控制部分与数据部分放一起
             */
            nshead.body_len = realDataBinary.length;
            byte[] data = new byte[nshead.body_len];
            System.arraycopy(realDataBinary,0,data,0,realDataBinary.length);

            nshead.provider = PROVIDER;

            // 组装nshead，将nshead对象中的各个部分按照指定的应用层协议进行组装，并put()到IoBuffer 缓冲区；
            buf.putUnsignedShort(Util.convertEndian(nshead.id));
            buf.putUnsignedShort(Util.convertEndian(nshead.version));
            //···此处省略nshead的部分组装代码

            buf.put(data);

            /*
            *调用flip()方法，为输出做好准备
            * 切记在write()方法之前，要调用IoBuffer 的flip()方法，否则缓冲区的position 的后面是没有数据可以用来输出的
            * 调用flip()方法将position移至0，limit移至刚才的position。
             */
            buf.flip();

            if (logger.isDebugEnabled()) {
                logger.debug(query + "-pack time: " + (System.nanoTime() - start) + " ns");
            }
            logger.info("sendToQS. Encoder time ="+(System.nanoTime() - start));
            //调用ProtocolEncoderOutput 的write()方法输出IoBuffer 缓冲区实例
            out.write(buf);
            } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

#### ClientDecoder.java说明

##### ClientDecoder.java功能描述

>ClientDecoder.java
>实现解码过程，可以实现ProtocolDecoder 接口，其中有decode()、finishDecode()、dispose()三个方法，其中，主要关注decode()方法即可。

##### ClientDecoder.java代码说明

```java
public class ClientDecoder implements MessageDecoder {
    //静态方法创建日志实例
    private final static Logger logger = Logger.getLogger(ClientDecoder.class);

    private final static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create();

    private final static Mcpack mcpack = new Mcpack();

    //实现decodable方法，判断IoBuffer是否可解码
    @Override
    public MessageDecoderResult decodable(IoSession paramIoSession, IoBuffer in) {
        if (in.remaining() < Nshead.HEAD_LENGTH) {
        return MessageDecoderResult.NEED_DATA;
    }

        Nshead nshead = decodeNsHead(in);

        if (in.remaining() < nshead.body_len) {
        return MessageDecoderResult.NEED_DATA;
        }

        return MessageDecoderResult.OK;
    }

    //该方法对nshead进行解码
    public static Nshead decodeNsHead(IoBuffer in) {
        Nshead nshead = new Nshead();
        System.out.println("I am here for NSHEAD!!!");


        //获取id
        byte[] bytes = new byte[2];
        in.get(bytes);
        nshead.id = Util.makeShort(bytes[1], bytes[0]);

        //获取body_len
        in.get(bytes);
        nshead.body_len = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);

        //···此处省略获取nshead的部分内容

        System.out.println("Finish Getting NSHEAD!");

        return nshead;
    }

    //实现decode方法，进行解码
    @Override
    public MessageDecoderResult decode(IoSession paramIoSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        System.out.println("ReceiveFromQS");
        logger.info("receiveFromQS.");
        long start = System.nanoTime();

        String client = paramIoSession.getRemoteAddress().toString();

        //调用decodeNsHead方法进行解码，获取解码后的nshead
        Nshead nshead = decodeNsHead(in);

        System.out.println("After decodeNsHead!");
        //body转换为字节数组allData
        byte[] allData = new byte[nshead.body_len];
        in.get(allData);

        System.out.println("After getting allData");
        //由字节数组allData转换为json对象realDataJson
        JsonElement realDataJson = mcpack.toJsonElement(SearchConfig.Q_SERVER_COMPACK_CHARSET, allData);
        //实现从json相关对象realDataJson到java实体对象qResponse
        rank_response_vs qResponse = gson.fromJson(realDataJson, rank_response_vs.class);
        System.out.println("hahaha! "+qResponse.toString());
        System.out.println("lalala! "+qResponse.getJsonInfo());
        if (logger.isDebugEnabled() || logger.isTraceEnabled()) {
            //logger.debug(" " + client + " body: " + gson.toJson(qResponse));
            logger.debug(" " + client + " unpack time: " + (System.nanoTime() - start) + " ns");
        }
        logger.info("Decoder time="+(System.nanoTime() - start));
        //以字符串形式输出
        out.write(qResponse);

        return MessageDecoderResult.OK;
    }

    //finishDecode方法一般不会被用到，只要继承适配器ProtocolDecoderAdapter即可
}
```

#### ClientHandler.java说明

##### ClientHandler.java功能描述

>ClientHandler.java
>ClientHandler类用于进行逻辑处理，类中的方法在通信中发生相应事件时触发，暂未使用

##### ClientHandler.java代码说明

```java
//ClientHandler类用于进行逻辑处理，暂未使用

public class ClientHandler implements IoHandler {

    private final static Logger logger = Logger.getLogger(ClientHandler.class);

    //捕捉到异常时的处理方法，关闭连接，日志记录异常信息
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        session.close(true);
        logger.error(cause.getMessage(), cause);
    }

    //接受消息时的处理方法
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        try {
            //判断类型
            if (message instanceof rank_response_vs) {
                rank_response_vs resp = (rank_response_vs) message;
                System.out.println(resp.toString());
            //设置会话属性
                session.setAttribute("res", resp);
                logger.debug("messageSent=+resp.toString()");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            rank_response_vs resp = new rank_response_vs();
            resp.setStatus(ResponseCode.INTERNAL_ERROR);
        }
        //消息完全接收后关闭会话
        session.close(true);
    }

    //其余逻辑处理方法暂不做实现

}

```





### MyRedis部分说明

MyRedis用于实现对Redis的操作。ReadDateFromRedis实现了根据检索词获取前十条微博的id。

#### ReadDateFromRedis.java

##### 功能描述

实现对Redis的操作方法。实现了`getIDFromRedis`根据检索词获取前十条微博的id。

##### 代码说明

使用Jedis连接到redis，设置redis的地址、端口以及密码，使用zrange方法获取到有序集合里面前十条结果，并存入到ArrayList<String>中，返回ArrayList。

```java
public class ReadDataFromRedis {
    /**
     * 根据query查取微博ID
     * @param query
     * @return wb_id
     */
    public static ArrayList<String> getIDFromRedis(String query){
        Jedis jedis = new Jedis("192.168.1.108",6479);//设置地址以及端口号
        jedis.auth("nopassword");//输入密码
        ArrayList<String> wb_id = new ArrayList<String>();
        wb_id.addAll(jedis.zrange(query,0,9));//获取前十条微博id
        // keys.addAll(jedis.zrevrange(query,0,-1));

        jedis.close();
        return wb_id;
    }
}
```



### Util.java说明

在使用mina的时候，会出现字符间相互转换的现象，为了**解决编码**的问题，用Util类检测和转换字节序

#### Util.java功能说明

Util.java提供了在不同类型的数据之间进行转换的方法，它将发送报文的数据形式转化成远程主机响应报文的数据类型。

#### Util.java代码说明

```java
public final class Util {
public static int convertEndian(int i) {
	return makeInt((byte) (i & 0xFF), (byte) ((i >>> 8) & 0xFF), (byte) ((i >>> 16) & 0xFF),
	(byte) ((i >>> 24) & 0xFF));
	}
//把32位的int通过移位转换为4个byte
    
public static long convertEndian(long i) {
	return makeLong((byte) (i & 0xFF), (byte) ((i >>> 8) & 0xFF), (byte) ((i >>> 16) & 0xFF),
			(byte) ((i >>> 24) & 0xFF), (byte) ((i >>> 32) & 0xFF), (byte) ((i >>> 40) & 0xFF),
			(byte) ((i >>> 48) & 0xFF), (byte) ((i >>> 56) & 0xFF));
}
//把64位的long通过移位转换为8个byte

public static short makeShort(byte b1, byte b0) {
	return (short) (((b1 & 0xff) << 8) | ((b0 & 0xff) << 0));
}
//byte是8位，short是16位，b1左移8位+b2就等于short

public static int makeInt(byte b3, byte b2, byte b1, byte b0) {
	return (int) ((((b3 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff) << 0)));
}
//同理makeShort

public static long makeLong(byte b7, byte b6, byte b5, byte b4, byte b3, byte b2, byte b1, byte b0) {
	return ((((long) b7 & 0xff) << 56) | (((long) b6 & 0xff) << 48) | (((long) b5 & 0xff) << 40)
			| (((long) b4 & 0xff) << 32) | (((long) b3 & 0xff) << 24) | (((long) b2 & 0xff) << 16)
			| (((long) b1 & 0xff) << 8) | (((long) b0 & 0xff) << 0));
}
//同理makeShort

private final static Gson gson = new GsonBuilder().disableInnerClassSerialization().serializeNulls()
		.disableHtmlEscaping().serializeSpecialFloatingPointValues().create();
//使用GsonBuilder构建gson,禁用内部类序列化,支持空对象序列化,跳过html特殊符号转码，支持特殊值序列化

public static String toJsonString(Object obj) {
	return gson.toJson(obj);
}
//返回JsonString

public static <T> T fromJson(Reader json, Class<T> classOfT) {
	return gson.fromJson(json, classOfT);
}
//从Json相关对象到Java实体

public static <T> T fromJson(String json, Class<T> classOfT) {
	return gson.fromJson(json, classOfT);
}
//从Json相关对象到Java实体
}
```



### Nshead.java说明

Nshead.java主要说明mina通讯中发送消息的一些参数

#### Nshead.java功能说明

Nshead.java对一些无符号的参数进行配置，说明了这些参数的长度范围，其中log_id用来作为一次request到response请求以及到相应到客户端的数据的请求的唯一标识，magic_num用来标记文件或者协议的格式，body_len说明了响应体的最大长度

#### Nshead.java代码说明

```java
public class Nshead {
	public final static int HEAD_LENGTH = 36;

	// unsigned short id;
	public short id;

	// unsigned short version;
	public short version;

	// unsigned int log_id;
	public int log_id;

	public byte[] provider = new byte[16];

	// unsigned int magic_num;
	public int magic_num = 0xfb709394;

	// unsigned int reserved;
	public int reserved;

	// unsigned int body_len;
	// 在DA中len最大不能超过Integer.MAX_VALUE
	public int body_len;
}
```

