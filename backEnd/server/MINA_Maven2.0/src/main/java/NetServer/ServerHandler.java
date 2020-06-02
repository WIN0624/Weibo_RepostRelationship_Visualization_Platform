package NetServer;

import java.util.ArrayList;
import java.util.List;

import JDBC.Dao;
import QDataType.*;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import QDataType.Request_query;

import static MyRedis.ReadDataFromRedis.getIDFromRedis;

public class ServerHandler extends IoHandlerAdapter {
    private final static Logger logger = Logger.getLogger(ServerHandler.class);

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (session.isConnected()) {
            rank_response_vs resp = new rank_response_vs();
            resp.setStatus(ResponseCode.INTERNAL_ERROR);
            session.write(resp);
        }
        session.close(true);
        // logger.info("What the fuck is here?");
        logger.info(cause.getMessage(), cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        StringBuilder sb = new StringBuilder("rank_response_vs req=");
        rank_response_vs resp = new rank_response_vs();
        Request_query req = null;
        String query;
        String query_type;
        try {
            // 判断传输过来的类型
            // 刚刚我看了一下，学长姐用的是hashmap来判断的，如果我们以后需求复杂了，应该也用那个比较好
            if (message instanceof Request_query) {
                query = ((Request_query) message).get_query();
                query_type = ((Request_query) message).get_query_type();
                if (query_type.equals("keyword")) {
                    logger.info("Use query to find bw_text");
                    System.out.println("receive keyword: " + query);

                    // 调用redis的方法
                    ArrayList<String> keys = getIDFromRedis(query);
                    System.out.println("完成索引返回");
                    System.out.println("尝试输出一个id:" + keys.get(2).toString());

                    // 调用mysql方法
                    String jsonInfo =Dao.selectQueryByBw_idsString(keys);
                    System.out.println("完成mysql方法调用");

                    // 写入传输用的实体类
                    resp.setJsonInfo(jsonInfo);
                    System.out.println("完成写入实体类json");

                    // 一些无关紧要的打印
                    sb.append(",resp=");
                    sb.append(resp.toString());
                    sb.append(",whole time=");
                } else if (query_type.equals("bw_id")) {
                    logger.info("Use bw_id to find relationship");
                    // 获取relationship请求
                    String bw_id = query;

                    // 调用mysql方法
                    String jsonInfo = Dao.selectAllReporelationshipByBw_idString(bw_id);

                    // 写入传输用的实体类
                    resp.setJsonInfo(jsonInfo);
                } else if (query_type.equals("rpBody")){
		    logger.info("Use bw_id to find rpBody");
		    // 获取bw_id
		    String bw_id = query;
		    
		    // 调用mysql方法，传入一个id，返回一个微博内容；传入id时所使用的变量是bw_id
		    String jsonInfo =Dao.selectQueryByBw_idString(bw_id); 
		    
		    //写入传输用的实体类
		    resp.setJsonInfo(jsonInfo);
		} else {
                    logger.error("Get wrong query_type");
                }
            }
            else{
                logger.error("Receive the wrong message!");
            }
                //传回121
                session.write(resp);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            session.write(resp);
        }
        sb.append(System.nanoTime() - resp.start);
        logger.info(sb.toString());
    }

    public void sessionClosed(IoSession session) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("close" + session);
        }
        //session.close(true);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        logger.info("Disconnectingthe idle.");
        session.close(true);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        session.close(true);
        logger.info("FINISH!");
    }
}
