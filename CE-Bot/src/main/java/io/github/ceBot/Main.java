package io.github.ceBot;


import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
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
    		.filterWhen(message -> message.getAuthorAsMember().map(user -> !user.isBot()))
    		.filter(message -> message.getContent().orElse("").equalsIgnoreCase(">>ping"))
    		.flatMap(Message::getChannel)
    		.flatMap(channel -> channel.createMessage("Pong!"))
    		.subscribe();
		//this is to make sure that we can always turn it off
		client.getEventDispatcher().on(MessageCreateEvent.class)
			.map(MessageCreateEvent::getMessage)
			.filterWhen(message -> message.getAuthorAsMember().map(user -> !user.isBot()))
			.filter(event -> ownerIds.contains(event.getAuthor().map(User::getId).map(Snowflake::asLong).orElse(0l)))
			.filter(message -> message.getContent().orElse("").equalsIgnoreCase(">>shutdown"))
			.flatMap(Message::getChannel)
			.flatMap(channel -> channel.createMessage("Ceasing to exist"))
			.doOnNext(client -> client.getClient().logout())
			.subscribe();
		//Goes without saying, these are the methods from other files.
		//All new methods that are commands must be made as interfaces with static voids
		//You can look into the v3 commmand module on the github if you want, just teach me b0ss
		Shutdown.shutdown();
		CommandHandler.handler();
		
		
		
		
		
		client.login().block();
	}
	
	public static final String BOT_PREFIX = ">";
	
	public static String[] BOT_OWNER = {"139541886888181760","422588424487174144","195621535703105536"};
	
	public static Set<Long> ownerIds = Stream.of(139541886888181760l,422588424487174144l,195621535703105536l).collect(Collectors.toSet());
	
	public static String BOT_INVITE = "https://discordapp.com/oauth2/authorize?client_id=542137435681718273&scope=bot&permissions=8";
	
}

