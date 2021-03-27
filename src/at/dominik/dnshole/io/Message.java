/**
 * 
 */
package at.dominik.dnshole.io;

import java.util.Arrays;

import at.dominik.dnshole.io.protocol.DNSStatus;

/**
 * @author Dominik Fluch
 *
 * Created on 11.03.2021
 *
 */
public class Message {

	private final byte[] data;
	
	/**
	 * @param data
	 */
	public Message(byte... data) {
		this.data = data;
	}
	
	/**
	 * The bits are represented as bytes having a value of either 0 or 1.
	 * 
	 * @return all the bits of the message.
	 */
	public byte[] extractBits() {
		final byte[] bits = new byte[this.getData().length * 8];
		
		for(int i = 0; i < this.getData().length; i++) {
			final byte b = this.getData()[i];
			
			for(int j = 0; j < 8; j++) bits[i * 8 + (7 - j)] = (byte) ((b & (int) Math.pow(2, j)) > 0 ? 1 : 0);
		}
		
		return bits;
	}
	
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * @param from
	 * @param to
	 * @return a sub-{@link Message} from the given from and the given to.
	 */
	public Message subMessage(int from, int to) {
		return new Message(Arrays.copyOfRange(this.getData(), from, to));
	}
	
	/**
	 * @param groups
	 * @return a {@link Message} created using the given {@link BitGroup}s.
	 */
	public static Message fromBits(BitGroup... groups) {
		final byte[] bits = new byte[Arrays.stream(groups).mapToInt(group -> group.getLength()).reduce((group, group2) -> group + group2).getAsInt()];
		
		int index = 0;
		
		for(BitGroup group : groups) {
			for(int i = 0; i < group.getLength(); i++) {
				bits[index++] = (byte) ((group.getData() & (int) Math.pow(2, i)) > 0 ? 1 : 0);
			}
		}
		
		return Message.fromBits(bits);
	}
	
	/**
	 * @param bits
	 * @return a {@link Message} constructed from the given bits.
	 */
	public static Message fromBits(byte... bits) {
		if(bits.length % 8 != 0) {
			final byte[] old = bits;
			
			bits = new byte[bits.length + (8 - bits.length % 8)];
			
			for(int i = 0; i < bits.length; i++) {
				bits[i] = i < (8 - old.length % 8) ? 0 : old[i - (8 - old.length % 8)];
			}
		}
		
		final byte[] bytes = new byte[bits.length / 8];
		
		for(int i = 0; i < bytes.length; i++) {
			byte b = 0;
			
			for(int j = 0; j < 8; j++) {
				if(bits[i * 8 + (7 - j)] == 1) b |= (int) Math.pow(2, j);
			}
			
			bytes[i] = b;
		}
		
		return new Message(bytes);
	}
	
	public static void main(String[] args) {
		final Message message = Message.fromBits(DNSStatus.SERVER_FAILURE);
		final byte[] bits = message.extractBits();
		System.out.println(Arrays.toString(bits));
		System.out.println(DNSStatus.from(BitGroup.fromBits(bits)));
	}
	
}
