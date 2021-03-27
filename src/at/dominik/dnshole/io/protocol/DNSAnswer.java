/**
 * 
 */
package at.dominik.dnshole.io.protocol;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;

/**
 * @author Dominik Fluch
 *
 * Created on 26.03.2021
 *
 */
public class DNSAnswer extends DNSQuery {

	private long timeToLive;
	private byte[] data;
	
	/**
	 * Construct {@link DNSAnswer} with the given data.
	 * @param name
	 * @param type
	 * @param queryClass
	 * @param timeToLive
	 * @param data
	 */
	public DNSAnswer(byte[] name, int type, int queryClass, long timeToLive, byte... data) {
		super(name, type, queryClass);
		this.timeToLive = timeToLive;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "{\"name\": \"" + this.getDisplayName() + "\", \"type\": " + this.getType() + ", \"queryClass\": " + this.getQueryClass() + ", \"timeToLive\": " + this.getTimeToLive() + ", \"data\": " + Arrays.toString(this.getData()) + "}";
	}
	
	/**
	 * @return the {@link DNSAnswer} but serialized.
	 */
	@Override
	protected ByteBuf serialize() {
		final ByteBuf buffer = super.serialize();
		
		buffer.writeByte((byte) this.getTimeToLive() >> 24);
		buffer.writeByte((byte) this.getTimeToLive() >> 16 & 0xFF);
		buffer.writeByte((byte) this.getTimeToLive() >> 8 & 0xFF);
		buffer.writeByte((byte) this.getTimeToLive() & 0xFF);
		
		buffer.writeByte(this.getDataLength() >> 8);
		buffer.writeByte(this.getDataLength() & 0xFF);
		
		buffer.writeBytes(this.getData());
		
		return buffer;
	}
	
	/**
	 * @return the timeToLive
	 */
	public long getTimeToLive() {
		return timeToLive;
	}
	
	/**
	 * @param timeToLive the timeToLive to set
	 */
	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}
	
	/**
	 * @return the length of the data. Does effectively the same as: <code>getData().length</code>.
	 */
	public int getDataLength() {
		return this.getData().length;
	}
	
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
}
