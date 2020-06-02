package NetClient;


import java.io.*;
import java.util.*;
import java.text.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import QDataType.Request_query;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import BsConfig.BsConfig;


import QDataType.rank_response_vs;
import SearchConfig.SearchConfig;


public class Client {

    private final static Logger logger = Logger.getLogger(Client.class);

//	private static int PORT = 3600;//3600;
//	private static String HOST = "10.48.145.21:10.48.145.21";//"10.46.167.36";

    private static Random random = new Random();

    NioSocketConnector connector;

    ConnectFuture cf;

    public Client() {
    }

    public rank_response_vs process(Request_query qRequest) {
        String HOST = BsConfig.Q_SERVER_HOST;
        String[] hostList = HOST.split(":");
        if(hostList.length==0){
            logger.error("hostList.length==0 Please check bs.conf");
            return null;
        }
        int hostNumber = hostList.length;
        int randomNumber = Client.random.nextInt(hostNumber);
        int retryNum = SearchConfig.QSERVER_RANDOM_RETRY_NUM + 1;
        boolean haveConnect = false;
        rank_response_vs qResponse = null;
        while((!haveConnect)&&(retryNum!=0)){
            try{
                init();

                // 此处修改server的ip
                String connectHost = "192.168.1.108";//hostList[(randomNumber+retryNum)%hostNumber];

                logger.info("Try to connect. retryNum=" + retryNum + ",connectHost=" + connectHost);
                int PORT = 8770;
                //int PORT = 8276;//Integer.parseInt(BsConfig.Q_SERVER_PORT);
                cf = connector.connect(new InetSocketAddress(connectHost, PORT));
                System.out.println("First Step!");
                cf.awaitUninterruptibly();
                System.out.println("Second Step!");
                cf.getSession().write(qRequest);
                System.out.println("Third Step!");
                cf.getSession().getCloseFuture().awaitUninterruptibly();
                System.out.println("Finish Connecting the server!");
                qResponse = (rank_response_vs) cf.getSession().getAttribute("res");//server传给client最后会放到这里，调试的时候可以设为断点查看
//		        	qResponse = (rank_response_vs) cf.getSession().read().getMessage();
                haveConnect = true;
            }catch(Exception e1){
                e1.printStackTrace();
                haveConnect = false;
                logger.error("ConnectException");
                System.out.println("ERROR Happen!");
            }
            connector.dispose();    //不关闭的话会运行一段时间后抛出，too many open files异常，导致无法连接
            logger.info("connector.dispose()");
            retryNum--;
        }

        return qResponse;
    }

    private void init(){
        connector = new NioSocketConnector();
        logger.info("ready to connect. new NioSocketConnector");
        connector.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new ClientCodecFactory())); //设置编码过滤器
        //ExecutorFilter保证在处理handler的时候是独立线程,session.wirte变成并行处理;
//		connector.getFilterChain().addLast( "threadPool", new ExecutorFilter(Executors.newCachedThreadPool())); 
        SocketSessionConfig config = (SocketSessionConfig) connector.getSessionConfig();
        config.setSendBufferSize(10240);
        config.setReceiveBufferSize(10240);
        config.setSoLinger(0);
        config.setTcpNoDelay(true);
        connector.setConnectTimeoutMillis(100L);
        connector.setHandler(new ClientHandler());
    }

    public Request_query process_exp() {
        // 模拟用的
        Request_query rq = new Request_query();
//        rq.set_query("新型冠状病毒");
//        rq.set_query_type("keyword");
//        rq.set_query("4505896136902915");
//     rq.set_query_type("bw_id");
	rq.set_query("4508748939731025");
	rq.set_query_type("rpBody");
        return rq;

    }

    public static void main(String[] args) throws IOException{
        Client clt = new Client();
        Request_query qrst = clt.process_exp();
        rank_response_vs qrep=clt.process(qrst);
        System.out.println("Get the response from Server! " );
	System.out.println("result:"+qrep.getJsonInfo());

    }

}
