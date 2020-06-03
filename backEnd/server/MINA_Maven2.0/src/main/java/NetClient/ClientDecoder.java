package NetClient;

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
import QDataType.rank_response_vs;
import SearchConfig.SearchConfig;
import UTIL.Util;

public class ClientDecoder implements MessageDecoder {
	
	private final static Logger logger = Logger.getLogger(ClientDecoder.class);

	private final static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create();

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
                System.out.println("I am here for NSHEAD!!!");


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

                System.out.println("Finish Getting NSHEAD!");

		return nshead;
	}
	
	@Override
	public MessageDecoderResult decode(IoSession paramIoSession, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		System.out.println("ReceiveFromQS");
                logger.info("receiveFromQS.");
		long start = System.nanoTime();

		String client = paramIoSession.getRemoteAddress().toString();

		// head
		Nshead nshead = decodeNsHead(in);

//		if (logger.isTraceEnabled()) {
//			logger.trace(client + " nshead: " + gson.toJson(nshead));
//		}
                System.out.println("After decodeNsHead!");
		// body
		byte[] allData = new byte[nshead.body_len];
		in.get(allData);

		// 控制头;长度固定为44;直接抛弃
//		byte[] contorlDataBinary = new byte[SearchConfig.QSERVER_BODY_CONTROLPACK_LEN];
//		System.arraycopy(allData,0,contorlDataBinary,0,SearchConfig.QSERVER_BODY_CONTROLPACK_LEN);
//		JsonElement controlDataJson = mcpack.toJsonElement("UTF-8", contorlDataBinary);
                System.out.println("After getting allData");

                JsonElement realDataJson = mcpack.toJsonElement(SearchConfig.Q_SERVER_COMPACK_CHARSET, allData);
		rank_response_vs qResponse = gson.fromJson(realDataJson, rank_response_vs.class);
                System.out.println("hahaha! "+qResponse.toString());
                System.out.println("lalala! "+qResponse.getJsonInfo());
		if (logger.isDebugEnabled() || logger.isTraceEnabled()) {
//			logger.debug(" " + client + " body: " + gson.toJson(qResponse));
			logger.debug(" " + client + " unpack time: " + (System.nanoTime() - start) + " ns");
		}
		logger.info("Decoder time="+(System.nanoTime() - start));
		out.write(qResponse);

		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {
		
	}
	
}
