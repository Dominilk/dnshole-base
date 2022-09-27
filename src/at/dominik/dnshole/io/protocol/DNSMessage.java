/**
 * 
 */
package at.dominik.dnshole.io.protocol;

import java.util.Arrays;

import at.dominik.dnshole.io.BitGroup;
import at.dominik.dnshole.io.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 */
public class DNSMessage {

	private byte[] transactionId; // We do not really need to take much care about this id, as it is primarily used by the client.
	private DNSFlags flags;
	private DNSQuery[] queries;
	private DNSAnswer[] answers;
	private DNSAnswer[] authoritativeNameservers;
	private DNSAnswer[] additionalRecords;
	
	
	/**
	 * @param message
	 */
	public DNSMessage(Message message) {
		this.transactionId = Arrays.copyOf(message.getData(), 2);
		
		{
			// Flags
			final Message flags = message.subMessage(2, 4);
			final byte[] bits = flags.extractBits();
			
			this.flags = new DNSFlags(MessageFlow.from(bits[0]), QueryType.from(BitGroup.fromBits(Arrays.copyOfRange(bits, 1, 5))), AnswerType.from(bits[5]), bits[6] == 1, bits[7] == 1, bits[8] == 1, bits[10] == 1, DNSStatus.from(BitGroup.fromBits(Arrays.copyOfRange(bits, 12, 16))));
		}
		{
			final byte high = message.getData()[4]; // Our byte containing the bits for the higher parts of the number.
			final byte low = message.getData()[5]; // Our byte containing the bits for the lower part.

			this.queries = new DNSQuery[(high << 8) | low]; // Shift high bits into correct position (8 to the left as low is 8 bits long) and then adding the 1 in low with the OR.
		}
		{
			final byte high = message.getData()[6];
			final byte low = message.getData()[7];
			
			this.answers = new DNSAnswer[(high << 8) | low]; // Shift high bits into correct position (8 to the left as low is 8 bits long) and then adding the 1 in low with the OR.
		}
		{
			final byte high = message.getData()[8];
			final byte low = message.getData()[9];
			
			this.authoritativeNameservers = new DNSAnswer[(high << 8) | low];
		}
		{
			final byte high = message.getData()[10];
			final byte low = message.getData()[11];
			
			this.additionalRecords = new DNSAnswer[(high << 8) | low];
		}
		
		int offset = 12;

		// TODO: Maybe package all the following into neat parse methods.
		
		for(int i = 0; i < this.getQueries().length; i++) {
			final ByteBuf name = Unpooled.buffer();
			
			while(true) {
				final byte b = message.getData()[offset++];
				
				name.writeByte(b);
				
				if(b == 0) break;
			}
			
			final byte[] namesBytes = new byte[name.readableBytes()];
			
			name.readBytes(namesBytes);
			
			this.getQueries()[i] = new DNSQuery(namesBytes, (message.getData()[offset++] << 8) 
					| message.getData()[offset++], (message.getData()[offset++] << 8) |
					message.getData()[offset++]);
		}
		
		for(int i = 0; i < this.getAnswers().length; i++) {
			final byte[] name = Arrays.copyOfRange(message.getData(), offset, offset += (message.getData()[offset] == 0 ? 1 : 2));
			final int type = (message.getData()[offset++] << 8) | message.getData()[offset++];
			final int queryClass = (message.getData()[offset++] << 8) | message.getData()[offset++];
			final long timeToLive = ((message.getData()[offset++] & 0xFFL) << 24) | ((message.getData()[offset++] & 0xFFL) << 16) | ((message.getData()[offset++] & 0xFFL) << 8) | (message.getData()[offset++] & 0xFFL);
			final int dataLength = (message.getData()[offset++] << 8) | message.getData()[offset++];
			
			final byte[] data = Arrays.copyOfRange(message.getData(), offset, offset += dataLength);
			
			this.getAnswers()[i] = new DNSAnswer(name, type, queryClass, timeToLive, data);
		}
		
		for(int i = 0; i < this.getAuthoritativeNameservers().length; i++) {
			final byte[] name = Arrays.copyOfRange(message.getData(), offset, offset += (message.getData()[offset] == 0 ? 1 : 2));
			final int type = (message.getData()[offset++] << 8) | message.getData()[offset++];
			final int queryClass = (message.getData()[offset++] << 8) | message.getData()[offset++];
			final long timeToLive = ((message.getData()[offset++] & 0xFFL) << 24) | ((message.getData()[offset++] & 0xFFL) << 16) | ((message.getData()[offset++] & 0xFFL) << 8) | (message.getData()[offset++] & 0xFFL);
			final int dataLength = (message.getData()[offset++] << 8) | message.getData()[offset++];
			final byte[] data = Arrays.copyOfRange(message.getData(), offset, offset += dataLength);
			
			this.getAuthoritativeNameservers()[i] = new DNSAnswer(name, type, queryClass, timeToLive, data);
		}
		
		for(int i = 0; i < this.getAdditionalRecords().length; i++) {
			final byte[] name = Arrays.copyOfRange(message.getData(), offset, offset += (message.getData()[offset] == 0 ? 1 : 2));
			final int type = (message.getData()[offset++] << 8) | message.getData()[offset++];
			
			if(type == DNSQuery.TYPE_OPT) {
				final int payloadSize = (message.getData()[offset++] << 8) | message.getData()[offset++];
				final byte higherBits = message.getData()[offset++];
				final byte ednsVersion = message.getData()[offset++];
				
				offset += 2; // Skip reserved.
				
				final int dataLength = (message.getData()[offset++] << 8) | message.getData()[offset++];
				 
				this.getAdditionalRecords()[i] = new OptAnswer(name, payloadSize, higherBits, ednsVersion, Arrays.copyOfRange(message.getData(), offset, offset += dataLength));
			} else {
				final int queryClass = (message.getData()[offset++] << 8) | message.getData()[offset++];
				final long timeToLive = ((message.getData()[offset++] & 0xFFL) << 24) | ((message.getData()[offset++] & 0xFFL) << 16) | ((message.getData()[offset++] & 0xFFL) << 8) | (message.getData()[offset++] & 0xFFL);
				final int dataLength = (message.getData()[offset++] << 8) | message.getData()[offset++];
				
				final byte[] data = Arrays.copyOfRange(message.getData(), offset, offset += dataLength);
				
				this.getAdditionalRecords()[i] = new DNSAnswer(name, type, queryClass, timeToLive, data);
			}
		}
	}
	
	@Override
	public String toString() {
		return "{\"transactionId\": " + Arrays.toString(this.getTransactionId()) + ", \"flags\": " + this.getFlags() + ", \"questions\": " + this.getQuestions() + ", \"answerRRs\": " + this.getAnswerRRs() + ", \"authorityRRs\": " + this.getAuthorityRRs() + ", \"additionalRRs\": " + this.getAdditionalRRs() + ", \"queries\": " + Arrays.toString(this.getQueries()) + ", \"answers\": " + Arrays.toString(this.getAnswers()) + ", \"authoritativeNameservers\": " + Arrays.toString(this.getAuthoritativeNameservers()) + ", \"additionalRecords\": " + Arrays.toString(this.getAdditionalRecords()) + "}";
	}
	
	/**
	 * @return the {@link DNSMessage} serialized back to a {@link Message}.
	 */
	public Message toMessage() {
		final ByteBuf buffer = Unpooled.buffer();
		
		buffer.writeBytes(this.getTransactionId());
		buffer.writeBytes(this.getFlags().serialize());
		
		buffer.writeByte(this.getQuestions() >> 8);
		buffer.writeByte(this.getQuestions() & 0xFF);

		buffer.writeByte(this.getAnswerRRs() >> 8);
		buffer.writeByte(this.getAnswerRRs() & 0xFF);
		
		buffer.writeByte(this.getAuthorityRRs() >> 8);
		buffer.writeByte(this.getAuthorityRRs() & 0xFF);
		
		buffer.writeByte(this.getAdditionalRRs() >> 8);
		buffer.writeByte(this.getAdditionalRRs() & 0xFF);
		
		for(DNSQuery query : this.getQueries()) buffer.writeBytes(query.serialize());
		
		for(DNSAnswer answer : this.getAnswers()) buffer.writeBytes(answer.serialize());
		for(DNSAnswer answer : this.getAuthoritativeNameservers()) buffer.writeBytes(answer.serialize());
		for(DNSAnswer answer : this.getAdditionalRecords()) buffer.writeBytes(answer.serialize());
		
		final byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);
		
		return new Message(data);
	}
	
	/**
	 * @return the transactionId
	 */
	public byte[] getTransactionId() {
		return transactionId;
	}
	
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(byte[] transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * @return the flags
	 */
	public DNSFlags getFlags() {
		return flags;
	}
	
	/**
	 * @param flags the flags to set
	 */
	public void setFlags(DNSFlags flags) {
		this.flags = flags;
	}
	
	/**
	 * @return the additionalRRs
	 */
	public int getAdditionalRRs() {
		return this.getAdditionalRecords().length;
	}
	
	/**
	 * @return the answerRRs
	 */
	public int getAnswerRRs() {
		return this.getAnswers().length;
	}
	
	/**
	 * @return the authorityRRs
	 */
	public int getAuthorityRRs() {
		return this.getAuthoritativeNameservers().length;
	}
	
	/**
	 * @return the questions
	 */
	public int getQuestions() {
		return this.getQueries().length;
	}
	
	/**
	 * @return the queries
	 */
	public DNSQuery[] getQueries() {
		return queries;
	}
	
	/**
	 * @param queries the queries to set
	 */
	public void setQueries(DNSQuery[] queries) {
		this.queries = queries;
	}
	
	/**
	 * @return the answers
	 */
	public DNSAnswer[] getAnswers() {
		return answers;
	}
	
	/**
	 * @param answers the answers to set
	 */
	public void setAnswers(DNSAnswer[] answers) {
		this.answers = answers;
	}
	
	/**
	 * @return the authoritativeNameservers
	 */
	public DNSAnswer[] getAuthoritativeNameservers() {
		return authoritativeNameservers;
	}
	
	/**
	 * @param authoritativeNameservers the authoritativeNameservers to set
	 */
	public void setAuthoritativeNameservers(DNSAnswer[] authoritativeNameservers) {
		this.authoritativeNameservers = authoritativeNameservers;
	}
	
	/**
	 * @return the additionalRecords
	 */
	public DNSAnswer[] getAdditionalRecords() {
		return additionalRecords;
	}
	
	/**
	 * @param additionalRecords the additionalRecords to set
	 */
	public void setAdditionalRecords(DNSAnswer[] additionalRecords) {
		this.additionalRecords = additionalRecords;
	}
	
}
