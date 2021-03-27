/**
 * 
 */
package at.dominik.dnshole;

import java.io.IOException;
import java.net.InetSocketAddress;

import at.dominik.dnshole.io.ServerPeer;
import at.dominik.dnshole.io.api.UDPForward;
import at.dominik.dnshole.io.protocol.DNSMessage;
import at.dominik.dnshole.io.servers.tcp.NettyDNSServerTCP;
import at.dominik.dnshole.io.servers.udp.NettyDNSServer;

/**
 * @author Dominik Fluch
 *
 * Created on 11.03.2021
 *
 */
public class DNSHole {
	
	/**
	 * Starts the {@link DNSHole}.
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		try(NettyDNSServerTCP tcp = new NettyDNSServerTCP()) {
			tcp.setRequestProcessor(new UDPForward(new InetSocketAddress("8.8.8.8", 53)) {
				
				@Override
				public void processRequest(ServerPeer peer, DNSMessage message) throws Exception {
					final DNSMessage response = new DNSMessage(this.forward(message));
					
					peer.send(response.toMessage());
				};
				
			});
			
			tcp.bind(new InetSocketAddress("0.0.0.0", 53)).sync();
			
			try(NettyDNSServer udp = new NettyDNSServer()) {
				udp.setRequestProcessor(new UDPForward(new InetSocketAddress("8.8.8.8", 53)) {
					
					@Override
					public void processRequest(ServerPeer peer, DNSMessage message) throws Exception {
						final DNSMessage response = new DNSMessage(this.forward(message));
						
						peer.send(response.toMessage());
					};
					
				});
				
				udp.bind(new InetSocketAddress("0.0.0.0", 53)).channel().closeFuture().sync();
			}
		}
	}

}
