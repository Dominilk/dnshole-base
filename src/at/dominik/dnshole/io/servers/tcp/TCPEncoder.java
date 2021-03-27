/**
 * 
 */
package at.dominik.dnshole.io.servers.tcp;

import java.util.List;

import at.dominik.dnshole.io.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * @author Dominik Fluch
 *
 * Created on 27.03.2021
 *
 */
public class TCPEncoder extends MessageToMessageEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext context, Message message, List<Object> out) throws Exception {
		final ByteBuf buffer = Unpooled.buffer();
		
		// Writing message length
		buffer.writeByte(message.getData().length >> 8); // (Length is 2-byte meaning that we can not send a 4-byte integer. We cast it to short, meaning that we lose 2 bytes: Cast not necessary, which is why I removed it) and are left with 2, shift them to the right by 8 to get the first one.
		buffer.writeByte(message.getData().length & 0xFF); // Now we mask off the first 8 bit and let only the bits of the second byte through.
		
		buffer.writeBytes(message.getData());
		
		out.add(buffer);
	}

}
