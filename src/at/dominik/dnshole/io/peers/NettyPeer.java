/**
 * 
 */
package at.dominik.dnshole.io.peers;

import java.io.IOException;
import java.net.SocketAddress;

import at.dominik.dnshole.io.Message;
import at.dominik.dnshole.io.ServerPeer;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 */
public class NettyPeer implements ServerPeer {

	private final ChannelHandlerContext channelHandlerContext;
	
	/**
	 * @param channelHandlerContext
	 */
	public NettyPeer(ChannelHandlerContext channelHandlerContext) {
		this.channelHandlerContext = channelHandlerContext;
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
		this.getChannelHandlerContext().write(message);
	}
	
	/**
	 * @return the channelHandlerContext
	 */
	public ChannelHandlerContext getChannelHandlerContext() {
		return channelHandlerContext;
	}
	
}
