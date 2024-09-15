package net.etfbl.ip.beans;

import java.io.Serializable;
import java.sql.Timestamp;

public class MessageBean implements Serializable {
	private static final long serialVersionUID = 6647725897168797810L;

	public MessageBean() {
	}

	private int id;
	private int senderId;
	private int recipientId;
	private String subject;
	private String content;
	private Timestamp sentAt;
	private Timestamp readAt;
	private String senderName;
	private String senderEmail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public int getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(int recipientId) {
		this.recipientId = recipientId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getSentAt() {
		return sentAt;
	}

	public void setSentAt(Timestamp sentAt) {
		this.sentAt = sentAt;
	}

	public Timestamp getReadAt() {
		return readAt;
	}

	public void setReadAt(Timestamp readAt) {
		this.readAt = readAt;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getShorterContent() {
		if (content != null && content.length() > 20) {
			return content.substring(0, 20) + "...";
		}
		return content;
	}

}
