package NetClient;

import QDataType.Request_query;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageEncoder;

public class ClientCodecFactory extends DemuxingProtocolCodecFactory {
	private MessageDecoder decoder = new ClientDecoder();

	private MessageEncoder<Request_query> encoder = new ClientEncoder();

	public ClientCodecFactory() {
	
		addMessageDecoder(decoder);
		addMessageEncoder(Request_query.class, encoder);
	}
}
