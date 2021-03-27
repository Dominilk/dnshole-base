/**
 * 
 */
package at.dominik.dnshole.io.peers;

import java.io.IOException;
import java.net.InetSocketAddress;

import at.dominik.dnshole.io.Message;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 */
public class NettyUDPPeer extends NettyPeer {

	private final InetSocketAddress recipient;
	
	/**
	 * @param channelHandlerContext
	 * @param recipient
	 */
	public NettyUDPPeer(ChannelHandlerContext channelHandlerContext, InetSocketAddress recipient) {
		super(channelHandlerContext);
		this.recipient = recipient;
	}
	
	@Override
	public void send(Message message) throws IOException {
		this.getChannelHandlerContext().write(new DatagramPacket(Unpooled.copiedBuffer(message.getData()), this.getRecipient()));
		this.getChannelHandlerContext().flush();
	}
	
	/**
	 * @return the recipient
	 */
	public InetSocketAddress getRecipient() {
		return recipient;
	}
	
}
