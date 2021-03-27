/**
 * 
 */
package at.dominik.dnshole.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * @author Dominik Fluch
 *
 * Created on 11.03.2021
 *
 */
public interface ServerPeer extends Closeable {

	/**
	 * Sends the given message to the remote client.
	 * @param message
	 */
	public void send(Message message) throws IOException;

	/**
	 * @return the remote {@link SocketAddress} of the client.
	 */
	public SocketAddress getRemoteAddress();
	
}
