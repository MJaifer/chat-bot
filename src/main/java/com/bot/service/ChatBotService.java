package com.bot.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.bot.controller.MasterController;

public class ChatBotService {

	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("applicationContext.xml");

		MasterController controller = context.getBean("masterController", MasterController.class);
		controller.activateChatBots();
		context.close();
	}

}
