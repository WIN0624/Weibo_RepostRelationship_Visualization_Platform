package NetServer;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import QDataType.rank_response_vs;

public class ServerCodecFactory extends DemuxingProtocolCodecFactory {
	private MessageDecoder decoder = new ServerDecoder();

	private MessageEncoder<rank_response_vs> encoder = new ServerEncoder();

	public ServerCodecFactory() {
		addMessageDecoder(decoder);
		addMessageEncoder(rank_response_vs.class, encoder);
	}
}
