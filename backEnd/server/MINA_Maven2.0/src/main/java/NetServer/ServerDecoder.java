package NetServer;

import QDataType.Request_query;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.baidu.gson.Gson;
import com.baidu.gson.GsonBuilder;
import com.baidu.gson.JsonElement;
import com.baidu.mcpack.Mcpack;
import NSHEAD.Nshead;
import SearchConfig.SearchConfig;
import UTIL.Util;

public class ServerDecoder implements MessageDecoder {
	private final static Logger logger = Logger.getLogger(ServerDecoder.class);

	private final static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues()
			.create();

	private final static Mcpack mcpack = new Mcpack();

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

	public static Nshead decodeNsHead(IoBuffer in) {
		Nshead nshead = new Nshead();

		// id
		byte[] bytes = new byte[2];
		in.get(bytes);
		nshead.id = Util.makeShort(bytes[1], bytes[0]);

		in.get(bytes);
		nshead.version = Util.makeShort(bytes[1], bytes[0]);

		bytes = new byte[4];
		in.get(bytes);
		nshead.log_id = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);

		in.get(nshead.provider);

		in.get(bytes);
		nshead.magic_num = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);

		in.get(bytes);
		nshead.reserved = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);

		in.get(bytes);
		nshead.body_len = Util.makeInt(bytes[3], bytes[2], bytes[1], bytes[0]);

		return nshead;
	}

	@Override
	public MessageDecoderResult decode(IoSession paramIoSession, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		logger.info("START! decode begin.");
		long start = System.nanoTime();

		// head
		Nshead nshead = decodeNsHead(in);

		if (logger.isTraceEnabled()) {
			logger.trace(paramIoSession.getRemoteAddress().toString() + " nshead time: " + (System.nanoTime() - start));
			start = System.nanoTime();
		}

		// body
		byte[] data = new byte[nshead.body_len];
		in.get(data);
		JsonElement joe = mcpack.toJsonElement(SearchConfig.UI_COMPACK_CHARSET, data);
		logger.info("mcpack.toJsonElement");
		
		Request_query request = gson.fromJson(joe, Request_query.class);
		logger.info("gson.fromJson");
		
		//request.set_request_id(19820824);
		//request.set_query("The MINA Test is Successful!");
		out.write(request);
		
		if (logger.isDebugEnabled()) {
			String query_content = request.toString();
//                        String neopathy = request.get_neopathy();
			logger.debug(query_content + ": " +paramIoSession.getRemoteAddress().toString() + " unpack time: " + (System.nanoTime() - start) + " ns");
		}
		logger.info("decode end. unpack time: " + (System.nanoTime() - start));
		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession paramIoSession, ProtocolDecoderOutput out) throws Exception {
		// no-op
	}
}
