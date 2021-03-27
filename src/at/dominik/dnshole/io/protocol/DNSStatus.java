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
public enum DNSStatus implements BitGroup {
	NO_ERROR(0),
	FORMAT_ERROR(4),
	SERVER_FAILURE(2),
	NAME_UNKNOWN(1);
	
	private final byte data;
	
	/**
	 * Creates a status, which offers the given byte as identification.
	 * @param data
	 */
	private DNSStatus(int data) {
		this.data = (byte) data;
	}
	
	@Override
	public byte getData() {
		return this.data;
	}

	@Override
	public int getLength() {
		return 4;
	}
	
	/**
	 * @param group
	 * @throws IllegalStateException
	 * @return the {@link DNSStatus} mapped to the given group.
	 */
	public static DNSStatus from(BitGroup group) throws IllegalStateException {
		for(DNSStatus status : DNSStatus.values()) {
			if(status.getData() == group.getData()) return status;
		}
		throw new IllegalStateException("Supplied invalid bit group. (" + group.getData() + ")");
	}
	
}
