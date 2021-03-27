/**
 * 
 */
package at.dominik.dnshole.io;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 * 
 * An interface used to encode bytes to bits.
 * The data is a normal byte. The length is
 * the amount of bits to map the data over. The
 * length should be longer than the actual amount of bits used.
 * 
 */
public interface BitGroup {

	/**
	 * @return the data
	 */
	public byte getData();
	
	/**
	 * @return the length
	 */
	public int getLength();
	
	/**
	 * @param data
	 * @param length
	 * @return a {@link BitGroup} always returning 0 used for reserving the given amount (length) of bits.
	 */
	public static BitGroup staticGroup(byte data, int length) {
		return new BitGroup() {
			
			@Override
			public int getLength() {
				return length;
			}
			
			@Override
			public byte getData() {
				return data;
			}
			
		};
	}
	
	/**
	 * Turns the given {@link Boolean} to a {@link BitGroup}.
	 * @param bool
	 * @return a {@link BitGroup} containing either 1 or 0.
	 */
	public static BitGroup fromBoolean(boolean bool) {
		return BitGroup.staticGroup((byte) (bool ? 1 : 0), 1);
	}
	
	/**
	 * @param length
	 * @return a {@link BitGroup} always returning 0 used for reserving the given amount (length) of bits.
	 */
	public static BitGroup reserved(int length) {
		return BitGroup.staticGroup((byte) 0, length);
	}
	
	/**
	 * @param bytes
	 * @return a {@link BitGroup} containing the given bits as a byte.
	 */
	public static BitGroup fromBits(byte... bits) {
		assert bits.length <= 8 : "Length needs to be smaller than 8.";
		
		byte b = 0;
		
		for(int j = 0; j < bits.length; j++) {
			if(bits[bits.length - 1 - j] == 1) b |= (int) Math.pow(2, j);
		}
		
		return BitGroup.staticGroup(b, bits.length);
	}
}
