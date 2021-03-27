/**
 * 
 */
package at.dominik.dnshole;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import at.dominik.dnshole.io.Message;
import at.dominik.dnshole.io.ServerPeer;
import at.dominik.dnshole.io.protocol.DNSMessage;
import at.dominik.dnshole.io.protocol.DNSQuery;
import at.dominik.dnshole.io.protocol.DNSStatus;
import at.dominik.dnshole.io.protocol.MessageFlow;
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
			
			server.setRequestProcessor((ServerPeer peer, DNSMessage dns) -> {
				for(DNSQuery query : dns.getQueries()) {
					if(query.getDisplayName().contains("facebook")) { // For demo purposes we will deny access to domains containing the word "facebook".
						dns.getFlags().setDirection(MessageFlow.RESPONSE);
						dns.getFlags().setStatus(DNSStatus.SERVER_FAILURE);
						
						peer.send(dns.toMessage());
						return;
					}
				}
				
				try(DatagramSocket socket = new DatagramSocket()) {
					socket.setSoTimeout(2000);
					
					final Message serialized = dns.toMessage();
					final DatagramPacket packet = new DatagramPacket(serialized.getData(), serialized.getData().length, new InetSocketAddress("8.8.8.8", 53));
					
					socket.send(packet);
					packet.setData(new byte[512]);
					socket.receive(packet);
					
					peer.send(new Message(packet.getData()));
				}
			});
			
			server.bind(new InetSocketAddress("0.0.0.0", 53)).channel().closeFuture().sync();
		}
	}

}
