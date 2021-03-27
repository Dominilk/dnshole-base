/**
 * 
 */
package at.dominik.dnshole.io.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 */
public class DNSQuery {
	
	// Today there is pretty much only one relevant query class.
	public static final int CLASS_INET = 1;

	public static final int TYPE_A = 1;
	public static final int TYPE_AAAA = 28;
	public static final int TYPE_PTR = 12;
	public static final int TYPE_UNKNOWN = 65;

	private byte[] name;
	private int type;
	private int queryClass;
	
	// TODO: Maybe change constructor to parse bytes instead of taking pre-parsed arguments.
	
	/**
	 * @param name
	 * @param type
	 * @param queryClass
	 */
	protected DNSQuery(byte[] name, int type, int queryClass) {
		this.name = name;
		this.type = type;
		this.queryClass = queryClass;
	}
	
	@Override
	public String toString() {
		return "{\"name\": \"" + this.getDisplayName() + "\", \"type\": " + this.getType() + ", \"queryClass\": " + this.getQueryClass() + "}";
	}
	
	/**
	 * Serializes the {@link DNSQuery}. 
	 * @return the serialized query.
	 */
	protected ByteBuf serialize() {
		final ByteBuf buffer = Unpooled.buffer();
		
		buffer.writeBytes(this.getName());
		buffer.writeByte(this.getType() >> 8);
		buffer.writeByte(this.getType() & 0xFF);
		buffer.writeByte(this.getQueryClass() >> 8);
		buffer.writeByte(this.getQueryClass() & 0xFF);
		
		return buffer;
	}
	
	/**
	 * @return the name but in a displayable version.
	 */
	public String getDisplayName() {
		final StringBuilder builder = new StringBuilder();
		
		// TODO: Check if the following can cause problems (encoding of chars and stuff)
		
		for(byte b : this.getName()) {
			builder.append(b <= 0x1F ? '.' : (char) b);
		}
		
		return builder.toString();
	}
	
	/**
	 * @return the queryClass
	 */
	public int getQueryClass() {
		return queryClass;
	}
	
	/**
	 * @param queryClass the queryClass to set
	 */
	public void setQueryClass(int queryClass) {
		this.queryClass = queryClass;
	}
	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * @return the name
	 */
	public byte[] getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(byte[] name) {
		this.name = name;
	}
	
}
