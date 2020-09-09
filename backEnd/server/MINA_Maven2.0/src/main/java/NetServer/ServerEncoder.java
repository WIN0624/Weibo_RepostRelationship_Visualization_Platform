package NetServer;

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
import QDataType.rank_response_vs;
import SearchConfig.SearchConfig;
import UTIL.Util;

public class ServerEncoder implements MessageEncoder<rank_response_vs> {

	private final static Logger logger = Logger.getLogger(ServerEncoder.class);

	private final static byte[] PROVIDER = new byte[] { 'B', 'a', 'i', 'd', 'u', '-', 'V', 'S', '-', 'B', 'S', 0, 0, 0,
			0, 0 };

	public final static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping()
			.serializeSpecialFloatingPointValues().create();

	private final static Mcpack mcpack = new Mcpack();

	@Override
	public void encode(IoSession session, rank_response_vs message, ProtocolEncoderOutput out) throws Exception {
		long start = System.nanoTime();
		logger.info("encode begin.");
		
		try {
			IoBuffer buf = IoBuffer.allocate(1024*64).setAutoExpand(true);
			Nshead nshead = new Nshead();
			JsonElement joe = gson.toJsonElement(message);
			//logger.info("gson.toJsonElement: "+joe);
			byte[] data = mcpack.toMcpack(SearchConfig.UI_COMPACK_CHARSET, joe);
			nshead.body_len = data.length;
			nshead.provider = PROVIDER;

			// head
			buf.putUnsignedShort(Util.convertEndian(nshead.id));
			buf.putUnsignedShort(Util.convertEndian(nshead.version));
			buf.putUnsignedInt(Util.convertEndian(nshead.log_id));
			buf.put(PROVIDER);
			buf.putUnsignedInt(Util.convertEndian(nshead.magic_num));
			buf.putUnsignedInt(Util.convertEndian(nshead.reserved));
			buf.putInt(Util.convertEndian(nshead.body_len));

			// body
			buf.put(data);

			buf.flip();

			if (logger.isDebugEnabled()) {
				logger.debug("pack time: " + (System.nanoTime() - start) + " ns");
			}
			out.write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			logger.info("encode end. pack time:" + (System.nanoTime() - start));
		}
	}
}
