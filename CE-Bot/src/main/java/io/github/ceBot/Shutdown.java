package io.github.ceBot;

import java.util.function.Function;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

public interface Shutdown {
	
	static void shutdown() {
	
	Main.client.getEventDispatcher().on(MessageCreateEvent.class)
	.map(MessageCreateEvent::getMessage)
	.filterWhen(message -> message.getAuthor().map(user -> !user.isBot()))
	.filter(message -> message.getAuthorId().toString().equals(Main.BOT_OWNER))
	.filter(message -> message.getContent().orElse("").equalsIgnoreCase(">>shutdown"))
	.flatMap(Message::getChannel)
	.flatMap(channel -> channel.createMessage("Ceasing to exist"))
	.doOnNext(client -> client.getClient().logout())
	.subscribe();
	
	}
}
