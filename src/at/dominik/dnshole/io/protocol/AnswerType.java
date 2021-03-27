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
public enum AnswerType implements BitGroup {
	NON_AUTHORITATIVE,
	AUTHORITATIVE;

	@Override
	public byte getData() {
		return (byte) (this == AnswerType.AUTHORITATIVE ? 1 : 0);
	}

	@Override
	public int getLength() {
		return 1;
	}
	
	/**
	 * @param data
	 * @return a {@link AnswerType#NON_AUTHORITATIVE} if the given data is 1, {@link AnswerType#AUTHORITATIVE} if it is 0.
	 */
	public static AnswerType from(byte data) {
		return data == 0 ? AnswerType.NON_AUTHORITATIVE : AnswerType.AUTHORITATIVE;
	}
	
}