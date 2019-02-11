package io.github.ceBot;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Owners {
	
	public static String owner; {
		
		String ownerguys[] = Main.BOT_OWNER;		
		if (Main.client.getEventDispatcher()
				.on(MessageCreateEvent.class)
				.map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().equals(ownerguys[1]))
				.toString() == "139541886888181760") {
			Owners.owner = "139541886888181760";
		}
		if (Main.client.getEventDispatcher()
				.on(MessageCreateEvent.class)
				.map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().equals(ownerguys[2]))
				.toString() == "422588424487174144") {
			Owners.owner = "422588424487174144";
		}
		
			
		
	}
	
}
