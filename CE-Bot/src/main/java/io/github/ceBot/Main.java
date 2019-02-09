package io.github.ceBot;


import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import io.github.ceBot.*;

public class Main {
	
	public final static DiscordClientBuilder builder = new DiscordClientBuilder("NTQyMTM3NDM1NjgxNzE4Mjcz.Dzp-hA.sSAi0DTA9rcprC3VKTLKHxLHdAU");
	public final static DiscordClient client = builder.build();
	
	public static void main(String[] args) throws ClassNotFoundException {
		
		
		client.getEventDispatcher().on(ReadyEvent.class)
			.subscribe(event -> {
        		User self = event.getSelf();
          		System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
        	});

		
		client.getEventDispatcher().on(MessageCreateEvent.class)
    		.map(MessageCreateEvent::getMessage)
    		.filterWhen(message -> message.getAuthor().map(user -> !user.isBot()))
    		.filter(message -> message.getContent().orElse("").equalsIgnoreCase(">ping"))
    		.flatMap(Message::getChannel)
    		.flatMap(channel -> channel.createMessage("Pong!"))
    		.subscribe();
		
		Shutdown.shutdown();
		
		
		
		
		client.login().block();
	}
	
	public static final String BOT_PREFIX = ">";
	
	public static String[] BOT_OWNER = {"139541886888181760","422588424487174144","195621535703105536"};
	
	public static String BOT_INVITE = "https://discordapp.com/oauth2/authorize?client_id=542137435681718273&scope=bot&permissions=8";
	
}

