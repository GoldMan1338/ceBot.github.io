package io.github.ceBot;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public interface GetOwners {
	@EventSubscriber
	public static boolean getOwners(MessageReceivedEvent event) {
		if(event.getAuthor().getStringID().contains(Main.BOT_OWNER[0])){
			boolean OWNER_C = event.getAuthor().getStringID().contains(Main.BOT_OWNER[0]);
			return OWNER_C;
		}
		else if(event.getAuthor().getStringID().contains(Main.BOT_OWNER[1])){
			boolean OWNER_E = event.getAuthor().getStringID().contains(Main.BOT_OWNER[1]);
			return OWNER_E;
		}
		else {
			return false;
		}
	}

}
