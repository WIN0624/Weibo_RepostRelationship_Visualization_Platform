# MINA的NetServer说明1.0

## 模块概况

### 模块功能
该文件包含MINA通信的服务器端相应代码，部署在192.168.1.116上。

主要功能是接收来自客户端的请求，根据请求调用相应方法在数据库进行检索，再将检索结果返回客户端。

客户端部署在121.46.19.26上，服务器端部署在192.168.1.116上。

### 技术栈
客户端和服务器端通信：mina

服务器端编解码：Gson、McPack

### 模块结构
src/main/java/NetServer/
   Server.java -初始化并开启服务器
   ServerCodecFactory.java  -编解码工厂，提供编解码器
   ServerDecoder.java  -解码
   ServerEncoder.java  -编码
   ServerHandler.java  -接收并处理客户端的请求，根据请求参数调用数据库进行相应检索，将检索结果返回给客户端

### 端口使用
8770：用于mina通讯

## 模块详细说明

### 模块代码

#### 1.Server.java
```java
public class Server {
    private final static Logger logger = Logger.getLogger(Server.class);  //日志记录

    SocketAcceptor acceptor; //接收客户端请求的对象

    public Server() {

    }

    public void start() throws Exception { //开启服务器
        init();//初始化服务器
    }
    void init() throws Exception { 
        acceptor = new NioSocketAcceptor(); //Nio保证同步非阻塞通信
        //获得过滤器链，向过滤器链中添加编解码工厂提供的自定义编解码器
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerCodecFactory()));
        //获得过滤器链，向过滤器链中添加处理器
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter(BsConfig.getInt(BsConfig.CF_THREADNUM)));
        //启动端口重用，需要在绑定端口之前设置
        acceptor.setReuseAddress(true);
        acceptor.getSessionConfig().setReuseAddress(true);
        //设置服务器端接收的缓冲区大小
        acceptor.getSessionConfig().setReceiveBufferSize(1024*1024);
        //设置服务器端发送的缓冲区大小
        acceptor.getSessionConfig().setSendBufferSize(1024*1024);
        //设置不开启Nagle算法，客户端无需等待服务器端的确认ACK就可继续发送数据
        acceptor.getSessionConfig().setTcpNoDelay(true);
        //设置服务器端等待客户端将要发送的所有数据发送完毕后才正常关闭连接
        acceptor.getSessionConfig().setSoLinger(-1);
        //设置服务器端监听端口的最大数目
        acceptor.setBacklog(10240);
        //服务器端连接客户端10ms后若无读和写的操作，连接进入休眠状态
//		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        //设置业务处理器
        acceptor.setHandler(new ServerHandler());
        //绑定监听的客户端端口
        acceptor.bind(new InetSocketAddress(BsConfig.getInt(BsConfig.CF_PORT)));
        logger.info("Server init success.");
    }

    public static void main(String[] args) throws Exception {
       //读取并配置log4j的配置文件，log4j是日志框架
       //win下为conf\\log4j.win.properties;linux下为conf/log4j.properties
        PropertyConfigurator.configure(Server.class.getClassLoader().getResource("conf/log4j.win.properties"));
        //加载配置文件
        BsConfig.load("conf/bs.conf");
        //win下为conf\\bs.conf.win;linux下为conf/bs.conf

        Server server = new Server(); //服务器对象
        server.start(); //开启服务器
    }
}
```
#### 2.ServerCodecFactory.java
```java
public class ServerCodecFactory extends DemuxingProtocolCodecFactory {
   //自定义解码器，对客户端发送的Request_query对象进行解码
	private MessageDecoder decoder = new ServerDecoder();
   //自定义编码器，封装为rank_response_vs对象，发送给客户端
	private MessageEncoder<rank_response_vs> encoder = new ServerEncoder();
   //向编解码工厂中添加自定义的编解码器
	public ServerCodecFactory() { 
		addMessageDecoder(decoder);
		addMessageEncoder(rank_response_vs.class, encoder);
	}
}
```
#### 3.ServerHandler.java
```java
public class ServerHandler extends IoHandlerAdapter {
    private final static Logger logger = Logger.getLogger(ServerHandler.class); //日志记录

    @Override  //服务器端异常处理
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (session.isConnected()) { //服务器端和客户端连接正常
            rank_response_vs resp = new rank_response_vs();
            //服务器端出现内部错误
            resp.setStatus(ResponseCode.INTERNAL_ERROR);
            //服务器端向客户端返回错误信息对象
            session.write(resp);
        }
        session.close(true); //关闭服务器端和客户端之间的连接
        // logger.info("What the fuck is here?");
        logger.info(cause.getMessage(), cause);//记录错误原因
    }

    @Override //服务器端接收到客户端发送的数据
    public void messageReceived(IoSession session, Object message) throws Exception {  
        StringBuilder sb = new StringBuilder("rank_response_vs req=");
        //服务器端传输数据的类型
        rank_response_vs resp = new rank_response_vs();
        Request_query req = null; 
        String query; //客户端发送的请求内容
        String query_type; //客户端发送的请求类型
        try {
            //判断客户端发送的是否是Request_query实例
            if (message instanceof Request_query) {
               //获取客户端发送的请求内容和请求类型
                query = ((Request_query) message).get_query();
                query_type = ((Request_query) message).get_query_type();
                //如果请求类型是检索词，使用的功能是根据检索词获取前十条微博
                if (query_type.equals("keyword")) {
                    logger.info("Use query to find bw_text");
                    System.out.println("receive keyword: " + query);

                    //调用redis的方法，传入检索词返回前十条微博的id
                    ArrayList<String> keys = getIDFromRedis(query);
                    System.out.println("完成索引返回");
                    int sz = keys.size();
                    if(sz>0){ //后台依次打印前十条微博的ID
                        System.out.println("返回id：");
                        for (int i=0; i<sz; i++)
                            System.out.println(keys.get(i).toString());
		    }
                   //调用mysql的方法，传入前十条微博的id
                   //以json数据格式返回这十条微博的id、用户id、用户名、微博内容，完成获取前十条微博的检索
                    String jsonInfo =QueryDao.selectQueryByBw_idsString(keys);
                    System.out.println("完成mysql方法调用");

                    //将json数据写入服务器端传输的实体类对象
                    resp.setJsonInfo(jsonInfo);
                    System.out.println("完成写入实体类json");

                    //打印服务器端传输的实体类对象
                    sb.append(",resp=");
                    sb.append(resp.toString());
                    sb.append(",whole time=");
               //如果请求类型是微博id，使用的功能是根据微博id获取微博转发关系
                } else if (query_type.equals("bw_id")) {
                    logger.info("Use bw_id to find relationship");
                    //获取传入的微博id
                    String bw_id = query;

                    //调用mysql方法，传入微博id返回json格式的转发关系
                    String jsonInfo = RepoRelationshipDao.selectAllReporelationshipByBw_idString(bw_id);

                    //将json数据写入服务器端传输的实体类对象
                    resp.setJsonInfo(jsonInfo);
              //如果请求类型是rpBody，使用的功能是根据微博id获取该微博信息    
                } else if (query_type.equals("rpBody")){
		    logger.info("Use bw_id to find rpBody");
		    //获取微博id
		    String bw_id = query;
		    
		    // 调用mysql方法，传入微博id返回微博id、用户名、微博内容等相关信息
		    String jsonInfo =QueryDao.selectQueryByBw_idString(bw_id); 
		    
		    //将json数据写入服务器端传输的实体类对象
		    resp.setJsonInfo(jsonInfo);
		} else {      //请求类型不是以上三种，日志记录请求类型错误
                    logger.error("Get wrong query_type");
                }
            }
            else{
               //客户端发送的信息不是Request_query实例，日志记录收到错误信息
                logger.error("Receive the wrong message!");
            }
                //把服务器端要传输的对象发送给客户端
                session.write(resp);
        } catch (Exception e) {
            //日志记录错误信息
            logger.error(e.getMessage(), e);
            session.write(resp);
        }
        //打印服务器端处理客户端请求并返回结果整个进程的时间
        sb.append(System.nanoTime() - resp.start);
        logger.info(sb.toString());
    }
    //服务器端关闭与客户端的连接
    public void sessionClosed(IoSession session) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("close" + session);
        }
        //session.close(true);
    }

    @Override //服务器端没有进行读写操作
    public void sessionIdle(IoSession session, IdleStatus status) {
        logger.info("Disconnectingthe idle.");
        session.close(true); //关闭客户端和服务器端之间的连接
    }

    @Override //服务器端发送数据
    public void messageSent(IoSession session, Object message) throws Exception {
        session.close(true); //关闭客户端和服务器端之间的连接
        logger.info("FINISH!");
    }
}
```
#### 4.ServerDecoder.java

```java
public class ServerDecoder implements MessageDecoder {
	private final static Logger logger = Logger.getLogger(ServerDecoder.class); //日志记录

    //构建Gson对象将json数据转换为java对象
    //转换时跳过HTML特殊符号的转码，同时转换特殊的float和double值
	private final static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues()
			.create();

	private final static Mcpack mcpack = new Mcpack();

	@Override //判断客户端发送的数据是否可以解码
	public MessageDecoderResult decodable(IoSession paramIoSession, IoBuffer in) {
        //客户端发送的数据长度小于协议包的头部长度
        //说明发送的协议包包头不完整，没有包体
		if (in.remaining() < Nshead.HEAD_LENGTH) {
			return MessageDecoderResult.NEED_DATA;
            //客户端发送的数据不能解码
		}
        //获取客户端发送的协议包的头部数据
		Nshead nshead = decodeNsHead(in);
        //如果客户端发送的协议包长度小于协议包的包体长度
        //说明包体不完整，客户端请求部分缺失
		if (in.remaining() < nshead.body_len) {
			return MessageDecoderResult.NEED_DATA;
             //客户端发送的数据不能解码
		}
        //客户端发送的数据即有包头又有完整的包体，可以进行解码
		return MessageDecoderResult.OK;
	}
    //从输入流中获取客户端发送的协议包的包头各项数据，放入自定义的协议包包头
	public static Nshead decodeNsHead(IoBuffer in) {
		Nshead nshead = new Nshead(); //自定义协议包

		// id 获取输入流中协议包id，放入自定义协议包包头
		byte[] bytes = new byte[2];
		in.get(bytes);
		nshead.id = Util.makeShort(bytes[1], bytes[0]);
        //获取输入流中协议包版本，放入自定义协议包包头
		in.get(bytes);
		nshead.version = Util.makeShort(bytes[1], bytes[0]);
        //获取输入流中协议包日志id，放入自定义协议包包头
		bytes = new byte[4];
		in.get(bytes);
		nshead.log_id = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);
        //获取输入流中协议包的其他包头数据，放入自定义协议包包头
		in.get(nshead.provider);

		in.get(bytes);
		nshead.magic_num = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);

		in.get(bytes);
		nshead.reserved = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);
        //获取输入流中协议包的包体长度，放入自定义协议包包头
		in.get(bytes);
		nshead.body_len = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);

		return nshead;
	}

	@Override  //服务器端对客户端发送的协议包进行解码
	public MessageDecoderResult decode(IoSession paramIoSession, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		logger.info("START! decode begin.");
		long start = System.nanoTime(); //记录开始解码的时间

		// head 客户端发送的协议包的包头各项数据
		Nshead nshead = decodeNsHead(in);

		if (logger.isTraceEnabled()) {
            //记录获得包头各项数据的所需时间
			logger.trace(paramIoSession.getRemoteAddress().toString() + " nshead time: " + (System.nanoTime() - start));
			start = System.nanoTime(); //记录开始对包体解码的时间
		}

		// body 获取客户端发送的协议包包体（客户端请求）
		byte[] data = new byte[nshead.body_len];
		in.get(data);
        //把字节形式的客户端的请求转换为json格式
		JsonElement joe = mcpack.toJsonElement(SearchConfig.UI_COMPACK_CHARSET, data);
		logger.info("mcpack.toJsonElement");
		//把json格式的请求转换为Request_query对象，解码成功
		Request_query request = gson.fromJson(joe, Request_query.class);
		logger.info("gson.fromJson");
		
		//request.set_request_id(19820824);
		//request.set_query("The MINA Test is Successful!");
		out.write(request);
        //在输出流中写入客户端请求对象，交给服务器端的业务处理器
		
		if (logger.isDebugEnabled()) {
            //获取客户端请求内容
			String query = request.get_query(); 
//                        String neopathy = request.get_neopathy();
			logger.debug(query + ": " +paramIoSession.getRemoteAddress().toString() + " unpack time: " + (System.nanoTime() - start) + " ns");//记录对包体（客户端请求）解码的所需时间
		}
		logger.info("decode end. unpack time: " + (System.nanoTime() - start));//记录解码全过程的时间
		return MessageDecoderResult.OK; //表明客户端发送的数据可以解码
	}

	@Override  //服务器端完成对客户端发送的协议包的解码
	public void finishDecode(IoSession paramIoSession, ProtocolDecoderOutput out) throws Exception {
		// no-op
	}
}
```
#### 5.ServerEncoder.java

```java
public class ServerEncoder implements MessageEncoder<rank_response_vs> {

	private final static Logger logger = Logger.getLogger(ServerEncoder.class);//日志记录
	private final static byte[] PROVIDER = new byte[] { 'B', 'a', 'i', 'd', 'u', '-', 'V', 'S', '-', 'B', 'S', 0, 0, 0,
			0, 0 };
    //构建Gson对象把java对象转换为json格式，不转换实体类中没有用@Expose注解的属性
    //转换为json格式时，跳过HTML特殊符号的转码，同时转换特殊的float和double值
	public final static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping()
			.serializeSpecialFloatingPointValues().create();

	private final static Mcpack mcpack = new Mcpack();

	@Override //服务器端对检索结果进行编码
	public void encode(IoSession session, rank_response_vs message, ProtocolEncoderOutput out) throws Exception {
		long start = System.nanoTime();//记录编码开始时间
		logger.info("encode begin.");
		
		try {
            //分配指定大小的缓冲区，允许缓冲区动态扩展
			IoBuffer buf = IoBuffer.allocate(1024*64).setAutoExpand(true);
            //自定义协议包的包头，存放协议包id、版本、包体长度等信息
			Nshead nshead = new Nshead();
            //把要传回客户端的检索结果变为json数据格式，方便传输
			JsonElement joe = gson.toJsonElement(message);
			logger.info("gson.toJsonElement: "+joe);
            //把json数据转换为UTF-8编码的字节数组，方便放入自定义的协议包
			byte[] data = mcpack.toMcpack(SearchConfig.UI_COMPACK_CHARSET, joe);
            //协议包包体长度为检索结果的字节数组长度
			nshead.body_len = data.length; 
			nshead.provider = PROVIDER;

			// head 把协议包包头的各项数据以字节格式依次放入缓冲区
			buf.putUnsignedShort(Util.convertEndian(nshead.id));
			buf.putUnsignedShort(Util.convertEndian(nshead.version));
			buf.putUnsignedInt(Util.convertEndian(nshead.log_id));
			buf.put(PROVIDER);
			buf.putUnsignedInt(Util.convertEndian(nshead.magic_num));
			buf.putUnsignedInt(Util.convertEndian(nshead.reserved));
			buf.putInt(Util.convertEndian(nshead.body_len));

			// body 把协议包包体放入缓冲区
			buf.put(data); 
            //切换缓冲区读写模式：由写模式切换到读模式
			buf.flip();

			if (logger.isDebugEnabled()) {
				logger.debug("pack time: " + (System.nanoTime() - start) + " ns"); //记录编码打包时间
			}
            //在输出流中加入缓冲区数据，发送数据给客户端
			out.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			logger.info("encode end. pack time:" + (System.nanoTime() - start)); //记录编码打包发送全过程的时间
		}
	}
}
```


### 业务处理说明

服务器端接收客户端发送的Request_query实例，服务器端返回给客户端rank_response_vs实例。

#### 客户端请求类型是keyword，使用的功能是根据检索词获取前十条微博

查询Redis数据库，传入keyword，返回前十条微博的id。
查询mysql数据库，传入前十条微博id，返回这十条微博的信息。

#### 客户端请求类型是bw_id，使用的功能是根据微博id获取微博转发关系

查询mysql数据库，传入微博id，返回这条微博的所有转发关系。

#### 客户端请求类型是rpBody，使用的功能是根据微博id获取微博相关信息

查询mysql数据库，传入微博id，返回这条微博的相关关系。



### 封装说明
本模块使用两个javabean，分别用于封装客户端的请求和服务器端返回的结果。

```java
public class rank_response_vs { //封装服务器端发送给客户端的检索结果

	@Expose //可以由Gson对象序列化为json数据
    //json数据格式为key-value，指定序列化名字（key）为jsonInfo
	@SerializedName("jsonInfo") 
	String jsonInfo; //检索结果内容
   
	@SerializedName("status")
	int status;  //mina通讯使用
```
```java
public class Request_query { //封装客户端发送给服务器端的请求

    @Expose  
    @SerializedName("query")
    String query; //请求内容

    @Expose  
    @SerializedName("query_type")
    String query_type; //请求类型，分为keyword、bw_id和rpBody三种
```



### 自定义协议包说明
自定义协议包分为包头和包体两部分，以字节为单位。

包头固定36字节，包括协议包id、协议包版本、包体长度等信息。

包体包含需要传输的请求或检索结果。

#### 自定义协议包的包头

```java
public class Nshead {  
    //自定义协议包的头部长度
    public final static int HEAD_LENGTH = 36;

    // unsigned short id;
    public short id; //协议包id

    // unsigned short version;
    public short version; //协议包版本

    // unsigned int log_id;
    public int log_id; //协议包日志id

    public byte[] provider = new byte[16];

    // unsigned int magic_num;
    public int magic_num = 0xfb709394;

    // unsigned int reserved;
    public int reserved;

    // unsigned int body_len;
    // 在DA中len最大不能超过Integer.MAX_VALUE
    public int body_len; //协议包包体长度
}
```


### 编解码说明
编码是把java对象转换为json数据，再转换为字节数组，放入自定义协议包。

解码是把接收到的协议包中的字节数组转换为json数据，再转换为java对象。

#### 1.编码过程
将检索结果rank_vs_response转换为json数据，将json数据转换为字节数组，放入自定义协议包的包体，包头存放协议包id、协议包版本等内容，把协议包的包头和包体分别放入缓冲区中，写入操作结束。

缓冲区由写模式切换到读模式，准备读取客户端发送的数据，最后在输出流中加入缓冲区数据，发送数据给客户端。

#### 2.解码过程

##### 判断是否可以解码

客户端发送的数据长度小于协议包包头长度，包头不完整且无包体-不可解码

客户端发送的数据长度小于协议包包体长度，包体（请求）不完整-不可解码

客户端发送的数据即有包头又有完整包体-可解码

##### 进行解码

服务器端获取客户端发送的协议包的包体（请求），将字节数组形式的请求转换为json格式，将json格式转换为请求对象Request_Query。

在输出流中写入客户端请求对象Request_Query，交给服务器端的业务处理器ServerHandler处理。



## Nagle算法说明
Nagle算法要求一个TCP连接上最多只能有一个未被确认的小分组，在sender收到该小分组的确认之前，sender不能发送其他小分组。

小分组是报文长度小于MSS长度的分组。

如果小分组的确认ACK一直未被收到，可能触发TCP超时重传的定时器。

Nagle算法旨在避免网络中充塞小的封装包，提高网络的利用率。



## JSON规范
JSON不允许特殊的double值（NaN、Infinity、-Infinity）和float值（Float.POSITIVE_INFINITY等)。

用Gson序列化上述数据会抛出异常。

解决办法是通过GsonBuilder设置serializeSpecialFloatingPointValues()允许序列化特殊的double和float值。
