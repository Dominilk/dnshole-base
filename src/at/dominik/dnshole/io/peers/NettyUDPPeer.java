/**
 * 
 */
package at.dominik.dnshole.io.peers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import at.dominik.dnshole.io.ServerPeer;
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
public class NettyUDPPeer implements ServerPeer {

	private final ChannelHandlerContext channelHandlerContext;
	private final InetSocketAddress recipient;
	
	/**
	 * @param channelHandlerContext
	 * @param recipient
	 */
	public NettyUDPPeer(ChannelHandlerContext channelHandlerContext, InetSocketAddress recipient) {
		this.channelHandlerContext = channelHandlerContext;
		this.recipient = recipient;
	}
	
	@Override
	public void close() throws IOException {
		this.getChannelHandlerContext().close(); // Will not do anything in the case of an UDP peer.
	}
	
	@Override
	public SocketAddress getRemoteAddress() {
		return this.getChannelHandlerContext().channel().remoteAddress();
	}
	
	@Override
	public void send(Message message) throws IOException {
		this.getChannelHandlerContext().write(new DatagramPacket(Unpooled.copiedBuffer(message.getData()), this.getRecipient())); // Could also use nettys encoder/decoder.
	}
	
	/**
	 * @return the recipient
	 */
	public InetSocketAddress getRecipient() {
		return recipient;
	}
	
	/**
	 * @return the channelHandlerContext
	 */
	public ChannelHandlerContext getChannelHandlerContext() {
		return channelHandlerContext;
	}
	
}
