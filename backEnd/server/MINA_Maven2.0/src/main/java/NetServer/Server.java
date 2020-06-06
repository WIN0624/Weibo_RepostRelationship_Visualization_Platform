package NetServer;

import java.net.InetSocketAddress;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import BsConfig.BsConfig;

public class Server {
    private final static Logger logger = Logger.getLogger(Server.class);

    SocketAcceptor acceptor;

    public Server() {

    }

    public void start() throws Exception {

        //初始化
        init();
    }

    void init() throws Exception {
        acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerCodecFactory()));
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter(BsConfig.getInt(BsConfig.CF_THREADNUM)));

        acceptor.setReuseAddress(true);
        acceptor.getSessionConfig().setReuseAddress(true);
        acceptor.getSessionConfig().setReceiveBufferSize(1024*1024);
        acceptor.getSessionConfig().setSendBufferSize(1024*1024);
        acceptor.getSessionConfig().setTcpNoDelay(true);
        acceptor.getSessionConfig().setSoLinger(-1);
        acceptor.setBacklog(10240);
//		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        acceptor.setHandler(new ServerHandler());

        acceptor.bind(new InetSocketAddress(BsConfig.getInt(BsConfig.CF_PORT)));
        logger.info("Server init success.");
    }

    public static void main(String[] args) throws Exception {
//		BsConfig.load("E:\\devs_space\\med-bs\\conf\\bs.conf.win");
        PropertyConfigurator.configure(Server.class.getClassLoader().getResource("conf/log4j.win.properties"));//win下为conf\\log4j.win.properties;linux下为conf/log4j.properties
        BsConfig.load("conf/bs.conf");//win下为conf\\bs.conf.win;linux下为conf/bs.conf

//		PropertyConfigurator.configure("conf/log4j.properties");
//		BsConfig.load("conf/bs.conf");
        Server server = new Server();
        server.start();
    }
}
