/**
 * 
 */
package at.dominik.dnshole.io;

import java.io.Closeable;

import at.dominik.dnshole.io.api.DNSProcessor;
import at.dominik.dnshole.io.protocol.DNSMessage;

/**
 * @author Dominik Fluch
 *
 * Created on 11.03.2021
 * 
 * Class offering abstraction over the raw implementations.
 *
 */
public abstract class DNSServer implements Closeable {

	private DNSProcessor requestProcessor;
	
	/**
	 * Handles the given message for the given client.
	 * @param serverPeer
	 * @param message
	 * @throws Exception
	 */
	public void handle(ServerPeer peer, Message message) throws Exception {
		final DNSMessage dns = new DNSMessage(message);
		
		if(this.getRequestProcessor() != null) this.getRequestProcessor().processRequest(peer, dns);
	}
	
	/**
	 * Passes on the given {@link Throwable} to the RequestProcessor, which should then take meassures.
	 * @param peer
	 * @param throwable
	 */
	public void handleError(ServerPeer peer, Throwable throwable) {
		if(this.getRequestProcessor() != null) this.getRequestProcessor().handleError(peer, throwable);
	}
	
	/**
	 * @param requestProcessor the requestProcessor to set
	 */
	public void setRequestProcessor(DNSProcessor requestProcessor) {
		this.requestProcessor = requestProcessor;
	}
	
	/**
	 * @return the requestProcessor
	 */
	public DNSProcessor getRequestProcessor() {
		return requestProcessor;
	}
	
}
