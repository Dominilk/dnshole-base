/**
 * 
 */
package at.dominik.dnshole.io.servers.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.dominik.dnshole.io.DNSServer;
import at.dominik.dnshole.io.Message;
import at.dominik.dnshole.io.peers.NettyUDPPeer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.AttributeKey;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 * Simple UDP implementation of DNSHole.
 *
 */
public class NettyDNSServer extends DNSServer {

	private static final AttributeKey<InetSocketAddress> SENDER = AttributeKey.newInstance("sender");
	
	/**
	 * @author Dominik Fluch
	 *
	 * Created on 25.03.2021
	 *
	 */
	public class NettyHandler extends SimpleChannelInboundHandler<DatagramPacket> {

		private final ExecutorService executorService;
		
		/**
		 * @param server
		 */
		public NettyHandler() {
			this.executorService = Executors.newCachedThreadPool();
		}
		
		@Override
		protected void channelRead0(ChannelHandlerContext context, DatagramPacket packet) throws Exception {
			context.channel().attr(NettyDNSServer.SENDER).set(packet.sender());
			final byte[] content = new byte[packet.content().readableBytes()];
			
			packet.content().readBytes(content);
			
			this.getExecutorService().execute(() -> {
				try {
					NettyDNSServer.this.handle(new NettyUDPPeer(context, packet.sender()), new Message(content));
				}catch(Exception exception) {
					try {
						this.exceptionCaught(context, exception);
					} catch (Exception exception2) {
						context.close();
					}
				}
			});
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext context) throws Exception {}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
			final InetSocketAddress recipient = context.channel().remoteAddress() instanceof InetSocketAddress ? (InetSocketAddress) context.channel().remoteAddress() : context.channel().hasAttr(NettyDNSServer.SENDER) ? context.channel().attr(NettyDNSServer.SENDER).get() : null;
			NettyDNSServer.this.handleError(recipient != null ? new NettyUDPPeer(context, recipient) : null, cause);
		}
		
		/**
		 * @return the executorService
		 */
		public ExecutorService getExecutorService() {
			return executorService;
		}
		
	}

	
	private final EventLoopGroup eventLoopGroup;
	private final Bootstrap bootstrap;
	private ChannelFuture channelFuture;
	
	/**
	 * 
	 */
	public NettyDNSServer() {
		this(new NioEventLoopGroup());
	}
	
	/**
	 * @param eventLoopGroup
	 */
	public NettyDNSServer(EventLoopGroup eventLoopGroup) {
		// TODO: Currently Datagram -> Message logic is in handler. Maybe add encoder/decoder in future here?
		this.bootstrap = new Bootstrap().channel(NioDatagramChannel.class).group(this.eventLoopGroup = eventLoopGroup).handler(this.new NettyHandler());
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
	public Bootstrap getBootstrap() {
		return bootstrap;
	}
	
	/**
	 * @return the eventLoopGroup
	 */
	public EventLoopGroup getEventLoopGroup() {
		return eventLoopGroup;
	}

}
