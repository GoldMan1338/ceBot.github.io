package io.github.ceBot.commands;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import io.github.ceBot.Main;
import reactor.core.publisher.Mono;

public class InviteCommand extends Command {

	@Override
	public CommandInfo getInfo() {
		return new CommandInfo("%cmdname%",
				"Provides an invitation link for the bot");
	}

	@Override
	public Set<String> getNames() {
		return Stream.of("invite").collect(Collectors.toSet());
	}

	@Override
	protected Mono<Message> run(MessageCreateEvent event, String[] args) {
		return event.getMessage().getChannel().flatMap(c -> sendMessage(Main.BOT_INVITE.toString(), c));
	}
	
	
	
	/*
	 * This is for a future update
	 * Ignore it
	@Override
	public Set<String> getModuleNames() {
		return Stream.of("General").collect(Collectors.toSet());
	}*/
}
