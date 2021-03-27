/**
 * 
 */
package at.dominik.dnshole.io.api;

import at.dominik.dnshole.io.ServerPeer;
import at.dominik.dnshole.io.protocol.DNSMessage;

/**
 * @author Dominik Fluch
 *
 * Created on 26.03.2021
 *
 */
public interface DNSProcessor {

	/**
	 * Handles the given request.
	 * @param peer
	 * @param message
	 * @throws Exception
	 */
	public void processRequest(ServerPeer peer, DNSMessage message) throws Exception;
	
	/**
	 * Handles the given {@link Throwable}.
	 * @param throwable
	 * @param peer
	 */
	public default void handleError(ServerPeer peer, Throwable throwable) {
		throwable.printStackTrace();
	}
}
