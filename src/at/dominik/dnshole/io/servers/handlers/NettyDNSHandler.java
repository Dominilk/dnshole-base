/**
 * 
 */
package at.dominik.dnshole.io.servers.handlers;

import java.net.InetSocketAddress;

import at.dominik.dnshole.io.Message;
import at.dominik.dnshole.io.peers.NettyUDPPeer;
import at.dominik.dnshole.io.servers.NettyUDPServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.AttributeKey;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 */
public class NettyDNSHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private static final AttributeKey<InetSocketAddress> RECIPIENT = AttributeKey.newInstance("recipient");
	
	private final NettyUDPServer server;
	
	/**
	 * @param server
	 */
	public NettyDNSHandler(NettyUDPServer server) {
		this.server = server;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext context, DatagramPacket packet) throws Exception {
		context.channel().attr(NettyDNSHandler.RECIPIENT).set(packet.recipient());
		
		final byte[] content = new byte[packet.content().readableBytes()];
		
		packet.content().readBytes(content);
		
		this.getServer().handle(new NettyUDPPeer(context, packet.sender()), new Message(content));
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext context) throws Exception {
		context.flush();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
		final InetSocketAddress recipient = context.channel().remoteAddress() instanceof InetSocketAddress ? (InetSocketAddress) context.channel().remoteAddress() : context.channel().hasAttr(NettyDNSHandler.RECIPIENT) ? context.channel().attr(NettyDNSHandler.RECIPIENT).get() : null;
		this.getServer().handleError(recipient != null ? new NettyUDPPeer(context, recipient) : null, cause);
	}

	/**
	 * @return the server
	 */
	public NettyUDPServer getServer() {
		return server;
	}
	
}
