/**
 * 
 */
package at.dominik.dnshole;

import java.io.IOException;
import java.net.InetSocketAddress;

import at.dominik.dnshole.io.api.UDPForward;
import at.dominik.dnshole.io.servers.NettyUDPServer;

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
		try(NettyUDPServer server = new NettyUDPServer()) {
			server.setRequestProcessor(new UDPForward(new InetSocketAddress("8.8.8.8", 53)));
			
			server.bind(new InetSocketAddress("0.0.0.0", 53)).channel().closeFuture().sync();
		}
	}

}
