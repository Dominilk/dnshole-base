/**
 * 
 */
package at.dominik.dnshole.io.api;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import at.dominik.dnshole.io.Message;
import at.dominik.dnshole.io.ServerPeer;
import at.dominik.dnshole.io.protocol.DNSMessage;

/**
 * @author Dominik Fluch
 *
 * Created on 27.03.2021
 *
 * A {@link DNSProcessor} which simply forwards all requests to another
 * DNS-Server.
 *
 */
public class UDPForward implements DNSProcessor {
	
	private InetSocketAddress proxied;
	private int soTimeout;
	
	/**
	 * @param proxied		The address of the proxied server (= the server this proxy is forwarding to).
	 */
	public UDPForward(InetSocketAddress proxied) {
		this.proxied = proxied;
		this.soTimeout = 1000;
	}
	
	@Override
	public void processRequest(ServerPeer peer, DNSMessage message) throws Exception {
		peer.send(this.forward(message));
	}
	
	@Override
	public void handleError(ServerPeer peer, Throwable throwable) {
		if(throwable instanceof SocketTimeoutException) return;
		
		DNSProcessor.super.handleError(peer, throwable);
	}
	
	/**
	 * @param message
	 * @return the response.
	 */
	public Message forward(DNSMessage message) throws Exception {
		// TODO: Use asynchronous I/O instead of blocking DatagramSocket.
		try(final DatagramSocket socket = new DatagramSocket()) {
			socket.setSoTimeout(this.getSoTimeout());
			
			final byte[] serialized = message.toMessage().getData();
			final DatagramPacket datagram = new DatagramPacket(serialized, serialized.length, this.getProxied());
			
			socket.send(datagram);
			datagram.setData(new byte[512]);
			socket.receive(datagram);
			
			return new DNSMessage(new Message(Arrays.copyOf(datagram.getData(), datagram.getLength()))).toMessage();
		}	
	}
	
	/**
	 * @return the proxied
	 */
	public InetSocketAddress getProxied() {
		return proxied;
	}

	/**
	 * @param proxied the proxied to set
	 */
	public void setProxied(InetSocketAddress proxied) {
		this.proxied = proxied;
	}
	
	/**
	 * @return the soTimeout
	 */
	public int getSoTimeout() {
		return soTimeout;
	}
	
	/**
	 * @param soTimeout the soTimeout to set
	 */
	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}
	
}
