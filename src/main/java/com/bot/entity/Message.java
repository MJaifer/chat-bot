package com.bot.entity;

import java.util.Date;

public class Message {

	private String messageId;
	private String message;
	private Date timeStamp;
	private Integer fromBotId;
	private Integer toBotId;
	
	public Message(Integer fromBotId, Integer toBotId, String message) {
		this.timeStamp = new Date();
		this.fromBotId = fromBotId;
		this.toBotId = toBotId;
		this.setMessageId(message);
		this.message = message;
	}
	
	public String getMessageId() {
		return messageId;
	}

	public Integer getToBotId() {
		return toBotId;
	}

	public String getMessage() {
		return message;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public Integer getFromBotId() {
		return fromBotId;
	}

	private void setMessageId(String message) {
		this.messageId = "MSG#" + message;
	}
	
}
