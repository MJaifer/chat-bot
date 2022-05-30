package com.bot.chatbot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.controller.MasterController;
import com.bot.entity.Message;

public class ChatBot {
	private static Logger LOG = LoggerFactory.getLogger(ChatBot.class);
	
	private Integer botId;
	private Map<String, Message> recievedMessageMap;
	private Map<String, Message> sendMessageMap;
	
	public ChatBot(Integer botId) {
		this.botId = botId;
		this.recievedMessageMap = new LinkedHashMap<String, Message>();
		this.sendMessageMap = new LinkedHashMap<String, Message>();
	}
	
	public Integer getBotId() {
		return botId;
	}

	public Map<String, Message> getSendMessageMap() {
		return sendMessageMap;
	}
	
	public Message getRecievedMessage(String messageId) {
		return this.recievedMessageMap.get(messageId);
	}

	/**
	 * This <code>ChatBot</code> selects a <code>random ChatBot</code> from the <b>MasterController</b><br>
	 * Creates a <b>Message</b> object with  UUID as <code>message</code>, and currentTime as <code>timeStamp</code><br>
	 * Adds the message to SendMessageMap and sends the message to the random ChatBot
	 * @param MasterController
	 */
	public void sendMessage(MasterController controller) {
		
		// select recipient
		ChatBot recipient = selectRecipient(controller, this.getBotId());
		
		// create message
		Message message = createMessage(this, recipient);
		
		// add message to sendMessageMap
		this.sendMessageMap.put(message.getMessageId(), message);
		
		// send message to recipient
		this.pushMessage(recipient, message);
	}
	
	private void pushMessage(ChatBot recipient, Message message) {
		StringBuilder builder = new StringBuilder();
		builder.append("Notification from ChatBot" + this.getBotId() + " :");
		builder.append("Sending message \"");
		builder.append(message.getMessage());
		builder.append("\" to \"ChatBot ");
		builder.append(message.getToBotId());
		builder.append("\" at ");
		builder.append(message.getTimeStamp().toString());
		builder.append("\"");
		
		LOG.debug(builder.toString());
		
		recipient.addMessage(message);
	}
	
	private static Message createMessage(ChatBot sender, ChatBot recipient) {
		Message message = new Message(
				sender.getBotId(), 
				recipient.getBotId(), 
				UUID.randomUUID().toString());
		
		return message;
	}
	
	private static ChatBot selectRecipient(MasterController controller, int senderId) {
		// select a random botId
		int recipientId = getRandomBotId(senderId, controller.getNoOfBots());
		
		return controller.getChatBot(recipientId);
	}
	
	private static int getRandomId(int noOfBots) {
		// generate a random number from 1 to no:of bots
		return (int) (Math.random() * (noOfBots - 1) + 1);
	}

	private static int getRandomBotId(int firstBotId, int noOfBots) {
		int secondBotId = getRandomId(noOfBots);

		if (secondBotId != firstBotId) {
			return secondBotId;
		} else {
			return getRandomBotId(firstBotId, noOfBots);
		}
	}
	
	public void addMessage(Message message) {
		this.recievedMessageMap.put(message.getMessageId(), message);
		displayMessage(message);
	}
	
	private static void displayMessage(Message message) {
		
		StringBuilder displayMessageBuilder = new StringBuilder();
		displayMessageBuilder.append("Notification from ChatBot" + message.getToBotId() + " :");
		displayMessageBuilder.append("Recieved message \"");
		displayMessageBuilder.append(message.getMessage());
		displayMessageBuilder.append("\" from \"ChatBot ");
		displayMessageBuilder.append(message.getFromBotId());
		displayMessageBuilder.append("\" at ");
		displayMessageBuilder.append(message.getTimeStamp().toString());
		displayMessageBuilder.append("\"");
		
		LOG.debug(displayMessageBuilder.toString());
	}
}
