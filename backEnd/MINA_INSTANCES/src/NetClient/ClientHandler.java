package NetClient;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import QDataType.ResponseCode;
import QDataType.rank_response_vs;


public class ClientHandler implements IoHandler {

	private final static Logger logger = Logger.getLogger(ClientHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		session.close(true);
		logger.error(cause.getMessage(), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		try {
			if (message instanceof rank_response_vs) {
				rank_response_vs resp = (rank_response_vs) message;
				session.setAttribute("res", resp);
				logger.debug("messageSent=+resp.toString()");
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			rank_response_vs resp = new rank_response_vs();
			resp.setStatus(ResponseCode.INTERNAL_ERROR);
		}
		session.close(true);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

//		if(message instanceof QRequest){
//			QRequest qRequest =(QRequest)message;
//			logger.debug("messageSent=+qRequest.toString()");
//		}
	}

	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus arg1) throws Exception {
		session.close(true);
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
