package NetServer;

import java.util.ArrayList;
import java.util.List;

import JDBC.*;
import QDataType.*;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import QDataType.Request_query;

import static MyRedis.ReadDataFromRedis.getIDFromRedisOnPage;

import static MyRedis.ReadDataFromRedis.getPageNumber;

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
        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("rank_response_vs req=");
        rank_response_vs resp = new rank_response_vs();
        Request_query req = null;
        String query_type;
        try {
            // 判断传输过来的类型
            // 刚刚我看了一下，学长姐用的是hashmap来判断的，如果我们以后需求复杂了，应该也用那个比较好
            if (message instanceof Request_query) {
                query_type = ((Request_query) message).get_query_type();
                if (query_type.equals("keyword")) {
                    
                    Query_content query_content = ((Request_query) message).get_query_content();
		    logger.info("Use query to find bw_text");
                    System.out.println("receive keyword: " + query_content);

                    // 调用redis的方法
                    ArrayList<String> keys = getIDFromRedisOnPage(query_content.get_query(),Integer.parseInt(query_content.get_page()));
		    String pageCount=String.valueOf(getPageNumber(query_content.get_query()));
                    System.out.println("完成索引返回");
                    int sz = keys.size();
                    if(sz>0){
                        /*System.out.println("返回id：");
                        for (int i=0; i<sz; i++)
                            System.out.println(keys.get(i).toString());*/
		    }
                    // 调用mysql方法
                    String jsonInfo =QueryDao.selectQueryByBw_idsString(keys);
		    String jsonPageCount="[{\"pageCount\":\""+pageCount+"\"}";
		    if(jsonInfo.equals("")) jsonInfo=jsonPageCount+"]";
		    else jsonInfo=jsonPageCount+","+jsonInfo.substring(1);
                    System.out.println("完成mysql方法调用");

                    // 写入传输用的实体类
                    resp.setJsonInfo(jsonInfo);
                    System.out.println("完成写入实体类json");

                    // 一些无关紧要的打印
                    sb.append(",resp=");
                    sb.append(resp.toString());
                    sb.append(",whole time=");
                } else if (query_type.equals("bw_id")) {
                    
                    Query_content query_content = ((Request_query) message).get_query_content();
		    logger.info("Use bw_id to find relationship");
                    // 获取relationship请求
                    String bw_id = query_content.get_query();
                    // 查询该id的正文部分
                    String weiboContext = QueryDao.selectQueryByBw_idString(bw_id);
                    // 查询该id的关系
                    String weiboRelation = RepoRelationshipDao.selectAllReporelationshipByBw_idString(bw_id);
                    //将正文和关系进行拼接
                    String jsonInfo = "[" + weiboContext + ",{\"relationship\":" + weiboRelation + "}]";

                    // 写入传输用的实体类
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
        long end = System.currentTimeMillis();
        sb.append(System.nanoTime() - resp.start);
        //logger.info(sb.toString());
        logger.info("检索时间："+(end-start));
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
