/**
 * 
 */
package at.dominik.dnshole.io.protocol;

import at.dominik.dnshole.io.BitGroup;
import at.dominik.dnshole.io.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Dominik Fluch
 *
 * Created on 25.03.2021
 *
 * Class representing the 16bit flag-region of a DNS-Message.
 */
public class DNSFlags {

	// TODO: Maybe create a separate DNS-Query & DNS-Answer class so that we do not have to hold both QueryType & AnswerType. 
	private MessageFlow direction;
	
	private QueryType queryType;
	private AnswerType answerType;
	private boolean answerAuthenticated;
	
	private boolean truncated;
	
	private boolean recursive;
	private boolean recursionAvailable;
	
	private DNSStatus status;
	
	/**
	 * @param direction
	 * @param queryType
	 * @param answerType
	 * @param truncated
	 * @param recursive
	 * @param recursionAvailable
	 * @param answerAuthenticated
	 * @param status
	 */
	protected DNSFlags(MessageFlow direction, QueryType queryType, AnswerType answerType, boolean truncated, boolean recursive, boolean recursionAvailable, boolean answerAuthenticated, DNSStatus status) {
		this.direction = direction;
		this.queryType = queryType;
		this.answerType = answerType;
		this.answerAuthenticated = answerAuthenticated;
		this.truncated = truncated;
		this.recursive = recursive;
		this.recursionAvailable = recursionAvailable;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "{\"direction\": " + this.getDirection() + ", \"queryType\": " + this.getQueryType() + ", \"answerType\": " + this.getAnswerType() + ", \"answerAuthenticated\": " + this.isAnswerAuthenticated() + ", \"truncated\": " + this.isTruncated() + ", \"recursive\": " + this.isRecursive() + ", \"recursionAvailable\": " + this.isRecursionAvailable() + ", \"status\": " + this.getStatus() + "}";
	}
	
	/**
	 * @return the flags but serialized.
	 */
	protected ByteBuf serialize() {
		final Message message = Message.fromBits(this.getDirection(), this.getQueryType(), this.getAnswerType(), BitGroup.fromBoolean(this.isTruncated()), BitGroup.fromBoolean(this.isRecursive()), BitGroup.fromBoolean(this.isRecursionAvailable()), BitGroup.reserved(2), BitGroup.fromBoolean(this.isAnswerAuthenticated()), this.getStatus());
		
		return Unpooled.copiedBuffer(message.getData());
	}
	
	/**
	 * @return the direction
	 */
	public MessageFlow getDirection() {
		return direction;
	}
	
	/**
	 * @param direction the direction to set
	 */
	public void setDirection(MessageFlow direction) {
		this.direction = direction;
	}
	
	/**
	 * @return the queryType
	 */
	public QueryType getQueryType() {
		return queryType;
	}
	
	/**
	 * @param queryType the queryType to set
	 */
	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}
	
	/**
	 * @return the answerType
	 */
	public AnswerType getAnswerType() {
		return answerType;
	}
	
	/**
	 * @return the answerAuthenticated
	 */
	public boolean isAnswerAuthenticated() {
		return answerAuthenticated;
	}
	
	/**
	 * @param answerAuthenticated the answerAuthenticated to set
	 */
	public void setAnswerAuthenticated(boolean answerAuthenticated) {
		this.answerAuthenticated = answerAuthenticated;
	}
	
	/**
	 * @return the recursionAvailable
	 */
	public boolean isRecursionAvailable() {
		return recursionAvailable;
	}
	
	/**
	 * @param recursionAvailable the recursionAvailable to set
	 */
	public void setRecursionAvailable(boolean recursionAvailable) {
		this.recursionAvailable = recursionAvailable;
	}
	
	/**
	 * @return the recursive
	 */
	public boolean isRecursive() {
		return recursive;
	}
	
	/**
	 * @param recursive the recursive to set
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
	/**
	 * @return the truncated
	 */
	public boolean isTruncated() {
		return truncated;
	}
	
	/**
	 * @param truncated the truncated to set
	 */
	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}
	
	/**
	 * @return the status
	 */
	public DNSStatus getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(DNSStatus status) {
		this.status = status;
	}
	
}
