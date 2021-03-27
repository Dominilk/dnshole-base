/**
 * 
 */
package at.dominik.dnshole.io.servers;

import java.io.IOException;
import java.net.SocketAddress;

import at.dominik.dnshole.io.DNSServer;
import at.dominik.dnshole.io.servers.handlers.NettyDNSHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 * UDP implementation of DNSHole.
 *
 */
public class NettyUDPServer extends DNSServer {
	
	private final NioEventLoopGroup eventLoopGroup;
	private final Bootstrap bootstrap;
	private ChannelFuture channelFuture;
	
	/**
	 * 
	 */
	public NettyUDPServer() {
		this.bootstrap = new Bootstrap().channel(NioDatagramChannel.class).group(this.eventLoopGroup = new NioEventLoopGroup()).handler(new NettyDNSHandler(this));
	}
	
	@Override
	public void close() throws IOException {
		if(this.channelFuture == null) return;
		
		this.getChannelFuture().channel().close();
		this.getEventLoopGroup().shutdownGracefully();
		
		this.channelFuture = null;
	}
	
	/**
	 * Binds the server on the given {@link SocketAddress}.
	 * @param address
	 * @return the {@link ChannelFuture} of the bind.
	 */
	public ChannelFuture bind(SocketAddress address) {
		return this.channelFuture = this.getBootstrap().bind(address);
	}
	
	/**
	 * @return the channelFuture
	 */
	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}
	
	/**
	 * @return the bootstrap
	 */
	public Bootstrap getBootstrap() {
		return bootstrap;
	}
	
	/**
	 * @return the eventLoopGroup
	 */
	public NioEventLoopGroup getEventLoopGroup() {
		return eventLoopGroup;
	}

}
