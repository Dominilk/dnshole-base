/**
 * 
 */
package at.dominik.dnshole.io.servers.tcp;

import java.util.List;

import at.dominik.dnshole.io.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * @author Dominik Fluch
 *
 * Created on 27.03.2021
 *
 */
public class TCPDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext context, ByteBuf message, List<Object> out) throws Exception {
		message.markReaderIndex();
		
		final ByteBuf messageLength = message.readBytes(2);
		final int length = messageLength.readByte() << 8 | messageLength.readByte();
		
		if(message.readableBytes() < length) {
			message.resetReaderIndex();
			
			return;
		}
		
		final byte[] data = new byte[length];
		
		message.readBytes(data);
		
		out.add(new Message(data));
	}
	
}
