/**
 * 
 */
package at.dominik.dnshole.io.servers.tcp;

import java.io.IOException;
import java.net.SocketAddress;

import at.dominik.dnshole.io.DNSServer;
import at.dominik.dnshole.io.Message;
import at.dominik.dnshole.io.peers.NettyPeer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 * Simple TCP implementation of DNSHole.
 *
 */
public class NettyDNSServerTCP extends DNSServer {
	
	/**
	 * @author Dominik Fluch
	 *
	 * Created on 25.03.2021
	 *
	 */
	public class NettyHandler extends SimpleChannelInboundHandler<Message> {
		
		@Override
		protected void channelRead0(ChannelHandlerContext context, Message message) throws Exception {
			NettyDNSServerTCP.this.handle(new NettyPeer(context), message);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext context) throws Exception {
			context.flush();
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
			NettyDNSServerTCP.this.handleError(new NettyPeer(context), cause);
		}
		
	}
	
	private final EventLoopGroup eventLoopGroup;
	private final EventLoopGroup workerEventLoopGroup;
	private final ServerBootstrap bootstrap;
	private ChannelFuture channelFuture;
	
	/**
	 * 
	 */
	public NettyDNSServerTCP() {
		this(new NioEventLoopGroup(), new NioEventLoopGroup());
	}
	
		
	/**
	 * @param eventLoopGroup
	 * @param workerEventLoopGroup
	 */
	public NettyDNSServerTCP(EventLoopGroup eventLoopGroup, EventLoopGroup workerEventLoopGroup) {
		this.bootstrap = new ServerBootstrap().channel(NioServerSocketChannel.class).group(this.eventLoopGroup = eventLoopGroup, this.workerEventLoopGroup = workerEventLoopGroup).childHandler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				channel.pipeline().addLast(new TCPEncoder());
				channel.pipeline().addLast(new TCPDecoder());
				channel.pipeline().addLast(NettyDNSServerTCP.this.new NettyHandler());
			}
			
		});
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
		return this.setChannelFuture(this.getBootstrap().bind(address));
	}
	
	/**
	 * @return the channelFuture
	 */
	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}
	
	/**
	 * @param channelFuture the channelFuture to set
	 * @return the given {@link ChannelFuture}.
	 * 
	 * @throws IllegalStateException		when server has already started.
	 */
	public ChannelFuture setChannelFuture(ChannelFuture channelFuture) throws IllegalStateException {
		if(this.getChannelFuture() != null) throw new IllegalStateException("Server has already been started.");
		return this.channelFuture = channelFuture;
	}
	
	/**
	 * @return the bootstrap
	 */
	public ServerBootstrap getBootstrap() {
		return bootstrap;
	}
	
	/**
	 * @return the eventLoopGroup
	 */
	public EventLoopGroup getEventLoopGroup() {
		return eventLoopGroup;
	}
	
	/**
	 * @return the workerEventLoopGroup
	 */
	public EventLoopGroup getWorkerEventLoopGroup() {
		return workerEventLoopGroup;
	}

}
