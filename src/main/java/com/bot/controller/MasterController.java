package com.bot.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.chatbot.ChatBot;
import com.bot.entity.Message;

public class MasterController {

	private static Logger LOG = LoggerFactory.getLogger(MasterController.class);

	int noOfBots;
	int minutesAlive;
	int frequency;
	Map<Integer, ChatBot> chatBotMap;
	
	long waitTime;

	public MasterController(int noOfBots, int minutesAlive, int frequency) {
		LOG.debug("Initializing MasterController...");
		LOG.debug("NoOf bots : " + noOfBots);
		LOG.debug("MinutesAlive : " + minutesAlive);
		LOG.debug("Frequency : " + frequency);

		this.noOfBots = noOfBots;
		this.minutesAlive = minutesAlive;
		this.frequency = frequency;

		// throw IllegalArgumentException if any of the arguments are 0
		this.checkArguments();

		// initialize chatBots
		this.initBots();
		
		// set waitTime
		this.setWaitTime();
	}

	/**
	 * Instructs chatBots to send messages until the minutesAlive is up.
	 * Sends message in the given frequency
	 */
	public void activateChatBots() {
		
		LOG.debug("Activating chatbots");
		
		// set end time as currentTime + minutesAlive (both time in milliSeconds)
		long endTime = System.currentTimeMillis() + (1000 * 60 * this.minutesAlive);
		
		// ender the loop if currentTime < endTime
		while (System.currentTimeMillis() < endTime) {
			for (ChatBot bot : this.chatBotMap.values()) {
				bot.sendMessage(this);
			}
			
			// make the Thread sleep for waitTime 
			try {
				Thread.sleep(this.waitTime);
			} catch (InterruptedException e) {
				LOG.error("Exception while waiting because ");
				LOG.error(e.getMessage());
			}
		}
		
		LOG.debug("Suspending chatbots");
		this.validateMessages();
	}
	
	private void initBots() {

		LOG.debug("Initializing " + this.noOfBots + "  ChatBots");
		this.chatBotMap = new HashMap<Integer, ChatBot>();

		for (int i = 1; i <= noOfBots; i++) {
			this.chatBotMap.put(i, new ChatBot(i));
		}
	}
	
	private void setWaitTime() {
		// frequency is in messages per minutes
		// frequency = 6 message/minutes
		// waitTime = 60/6 = 10 seconds
		// at 0th sec, send message
		// at 10th sec, send message. then 20, 30, 40, 50
		this.waitTime = (long) (60000 / this.frequency);
	}

	public int getNoOfBots() {
		return noOfBots;
	}
	
	public ChatBot getChatBot(int botId) {
		return this.chatBotMap.get(botId);
	}
	
	private void validateMessages() {
		// get each chatBot, loop through the sendMessages
		// get recipient from each message => take corresponding message from recipient
		// validate both messages
		LOG.debug("Validating messages");
		for (ChatBot bot : this.chatBotMap.values()) {
			int senderId = bot.getBotId();
			Map<String, Message> sendMessageMap = bot.getSendMessageMap();
			for (Map.Entry<String, Message> entry : sendMessageMap.entrySet()) {
				String messageId = entry.getKey();
				Message messageFromSender = entry.getValue();
				
				// get recipient id
				int recipientId = messageFromSender.getToBotId();
				
				// get recipient
				ChatBot recipient = this.chatBotMap.get(recipientId);
				
				// get message from recipient
				Message messageFromRecipient = recipient.getRecievedMessage(messageId);
				
				// if both messages are not same, Log the details
				if (!messageFromRecipient.getMessage()
						.equals(messageFromSender.getMessage())) {
					String errorMsgPrefix = 
							this.getErrorMessagePrefix(senderId, recipientId, messageId);
					LOG.error(errorMsgPrefix + "messageIds are not matching");
				}
				if (messageFromRecipient.getFromBotId() != senderId) {
					String errorMsgPrefix = 
							this.getErrorMessagePrefix(senderId, recipientId, messageId);
					LOG.error(errorMsgPrefix + "senderIds are not matching");
				}
				
				if (messageFromRecipient.getToBotId() != recipientId) {
					String errorMsgPrefix = 
							this.getErrorMessagePrefix(senderId, recipientId, messageId);
					LOG.error(errorMsgPrefix + "recipientIds are not matching");
				}
				
				if (!messageFromRecipient.getTimeStamp().equals(messageFromSender.getTimeStamp())) {
					String errorMsgPrefix = 
							this.getErrorMessagePrefix(senderId, recipientId, messageId);
					LOG.error(errorMsgPrefix + "timeStamps are not matching");
				}
			}
		}
		LOG.debug("Message validation finished.");
	}
	
	private String getErrorMessagePrefix(int fromBotId, int toBotId, String messageId) {
		StringBuilder builder = new StringBuilder();
		builder.append("Message with messageId \"");
		builder.append(messageId);
		builder.append("\" send from \"ChatBot ");
		builder.append(fromBotId);
		builder.append("\" to \"ChatBot ");
		builder.append(toBotId);
		builder.append("\" is not valid because");
		
		return builder.toString();
	}

	private void checkArguments() {

		LOG.debug("Checking arguments");
		if (this.noOfBots < 2) {
			LOG.error("Argument noOfBots can't be less than 2");
			throw new IllegalArgumentException("Argument noOfBots can't be less than 2");
		}

		if (this.minutesAlive <= 0) {
			LOG.error("Argument minutesAlive can't be negative or 0");
			throw new IllegalArgumentException("Argument minutesAlive can't be negative or 0");
		}

		if (this.frequency <= 0) {
			LOG.error("Argument frequency can't be  negative or 0");
			throw new IllegalArgumentException("Argument frequency can't be  negative or 0");
		}
		LOG.debug("Arguments are valid");
	}
}
