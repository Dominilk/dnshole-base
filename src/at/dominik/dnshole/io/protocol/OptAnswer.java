/**
 * 
 */
package at.dominik.dnshole.io.protocol;

/**
 * @author Dominik Fluch
 *
 * Created on 15.05.2021
 *
 */
public class OptAnswer extends DNSAnswer {

	private final int payloadSize;
	private final byte higherBits;
	private final byte ednsVersion;
	
	/**
	 * @param name
	 * @param type
	 * @param queryClass
	 * @param data
	 */
	public OptAnswer(byte[] name, int payloadSize, byte higherBits, byte ednsVersion, byte... data) {
		super(name, OptAnswer.TYPE_OPT, 0, -1L, data);
		this.payloadSize = payloadSize;
		this.higherBits = higherBits;
		this.ednsVersion = ednsVersion;
	}
	
	/**
	 * @return the payloadSize
	 */
	public int getPayloadSize() {
		return payloadSize;
	}
	
	/**
	 * @return the higherBits
	 */
	public byte getHigherBits() {
		return higherBits;
	}
	
	/**
	 * @return the ednsVersion
	 */
	public byte getEDNSVersion() {
		return ednsVersion;
	}

}
