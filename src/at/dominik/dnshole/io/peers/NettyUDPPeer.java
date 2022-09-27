/**
 * 
 */
package at.dominik.dnshole.io.peers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

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

	private final InetSocketAddress sender;
	
	/**
	 * @param channelHandlerContext
	 * @param sender
	 */
	public NettyUDPPeer(ChannelHandlerContext channelHandlerContext, InetSocketAddress sender) {
		super(channelHandlerContext);
		this.sender = sender;
	}
	
	@Override
	public void send(Message message) throws IOException {
		this.getChannelHandlerContext().write(new DatagramPacket(Unpooled.copiedBuffer(message.getData()), this.getSender()));
		this.getChannelHandlerContext().flush();
	}
	
	@Override
	public SocketAddress getRemoteAddress() {
		return this.getSender();
	}
	
	/**
	 * @return the recipient
	 */
	public InetSocketAddress getSender() {
		return sender;
	}
	
}
