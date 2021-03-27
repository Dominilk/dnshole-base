/**
 * 
 */
package at.dominik.dnshole.io.protocol;

import at.dominik.dnshole.io.BitGroup;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 */
public enum MessageFlow implements BitGroup {
	QUERY,
	RESPONSE;
	
	@Override
	public int getLength() {
		return 1;
	}
	
	@Override
	public byte getData() {
		return (byte) (this == MessageFlow.QUERY ? 0 : 1);
	}
	
	/**
	 * @param data
	 * @return {@link MessageFlow#QUERY} if data is 0 {@link MessageFlow#RESPONSE} if data is 1.
	 */
	public static MessageFlow from(byte data) {
		return data == 0 ? MessageFlow.QUERY : MessageFlow.RESPONSE;
	}
}
