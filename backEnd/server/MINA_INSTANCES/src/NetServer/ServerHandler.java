package NetServer;

import java.util.ArrayList;
import java.util.List;

import JDBC.jdbc;
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
		R_Request idreq = null;
		try {
			// 判断传输过来的类型
			// 刚刚我看了一下，学长姐用的是hashmap来判断的，如果我们以后需求复杂了，应该也用那个比较好
			if (message instanceof Request_query) {
				logger.info("Use query to find bw_text");

				// 获取query请求
				req = (Request_query) message;
				String query = req.get_query();
				System.out.println("receive: "+query);

				// 调用redis的方法
				ArrayList<String> keys = getIDFromRedis(query);

				// 调用mysql方法
				String jsonInfo = "";
				for (String key : keys) {
					jsonInfo += jdbc.searchQuery(key)+",";
				}

				// 写入传输用的实体类
				resp.setJsonInfo(jsonInfo);

				// 一些无关紧要的打印
				sb.append(",resp=");
				sb.append(resp.toString());
				sb.append(",whole time=");

				// 传送回121
				session.write(resp);

			} else if(message instanceof R_Request){
				logger.info("Use bw_text to find relationship");
				// 获取relationship请求
				idreq = (R_Request) message;
				String bw_id = idreq.getBw_id();

				// 调用mysql方法
				String jsonInfo = jdbc.searchRelationship(bw_id);

				// 写入传输用的实体类
				resp.setJsonInfo(jsonInfo);

				// 传送回121
				session.write(resp);

			}
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
