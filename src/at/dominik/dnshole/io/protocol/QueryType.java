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
public enum QueryType implements BitGroup {
	STANDARD(0),
	INVERSE(4);
	
	private final byte data;
	
	/**
	 * @param data
	 */
	private QueryType(int data) {
		this.data = (byte) data;
	}
	
	@Override
	public byte getData() {
		return data;
	}
	
	@Override
	public int getLength() {
		return 4;
	}
	
	/**
	 * @param group
	 * @throws IllegalStateException
	 * @return the {@link QueryType} mapped to the given group.
	 */
	public static QueryType from(BitGroup group) throws IllegalStateException {
		switch(group.getData()) {
		case 0:
			return QueryType.STANDARD;
		case 4:
			return QueryType.INVERSE;
		default:
		throw new IllegalStateException("Supplied invalid bit group. (" + group.getData() + ")");
		}
	}
	
}
