package NetClient;

import QDataType.*;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.baidu.gson.Gson;
import com.baidu.gson.GsonBuilder;
import com.baidu.gson.JsonElement;
import com.baidu.mcpack.Mcpack;
import NSHEAD.Nshead;
import QDataType.R_Request;
import QDataType.Request_query;

import SearchConfig.SearchConfig;
import UTIL.Util;

public class ClientEncoder implements MessageEncoder<Request_query> {
	
	private final static Logger logger = Logger.getLogger(ClientEncoder.class);
	
	private final static byte[] PROVIDER = new byte[] { 'B', 'a', 'i', 'd', 'u', '-', 'V', 'S', '-', 'D', 'A', 0, 0, 0,
		0, 0 };

	private final static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues() .create();
	public final static Gson inner_gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues()
			.create();
	
	private final static Mcpack mcpack = new Mcpack();

	@Override
	public void encode(IoSession session, Request_query message, ProtocolEncoderOutput out) throws Exception {
		long start = System.nanoTime();
		try {
//			String client = session.getRemoteAddress().toString();

			Query_content query_content = message.get_query_content();
			//String query = message.get_query();
			IoBuffer buf = IoBuffer.allocate(100).setAutoExpand(true);

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

			// 组装nshead
			buf.putUnsignedShort(Util.convertEndian(nshead.id));
			buf.putUnsignedShort(Util.convertEndian(nshead.version));
			buf.putUnsignedInt(Util.convertEndian(nshead.log_id));
			buf.put(PROVIDER);
			buf.putUnsignedInt(Util.convertEndian(nshead.magic_num));
			buf.putUnsignedInt(Util.convertEndian(nshead.reserved));
			buf.putInt(Util.convertEndian(nshead.body_len));

//			if (logger.isTraceEnabled()) {
//				logger.trace(id + " " + client + " send nshead: " + inner_gson.toJson(nshead));
//				logger.trace(id + " " + client + " send body: " + inner_gson.toJson(qRequestControl));
//				logger.trace(id + " " + client + " send body: " + inner_gson.toJson(qRequestData));
//			} else if (logger.isDebugEnabled()) {
//				logger.debug(id + " " + client + " send body: " + controlJson + realJson);
//			}

			// body 
			buf.put(data);

			buf.flip();

			if (logger.isDebugEnabled()) {
				logger.debug(query_content + "-pack time: " + (System.nanoTime() - start) + " ns");
			}
			logger.info("sendToQS. Encoder time ="+(System.nanoTime() - start));
			out.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
