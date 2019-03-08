package io.github.ceBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import io.github.ceBot.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShutdownCommand extends Command {
    @Override
    public CommandInfo getInfo() {
        return new CommandInfo("%cmdname%",
                "Turns off boi");
    }

    @Override
    public Set<String> getNames() {
        return Stream.of("shutdown", "cease", "goaway").collect(Collectors.toSet());
    }

    @Override
    protected Mono<Message> run(MessageCreateEvent event, String[] args) {
    	if(Main.ownerIds.contains(event.getMessage().getAuthor().map(User::getId).map(Snowflake::asLong).orElse(0l))) {
    	return event.getMessage().getChannel()
    			.flatMap(c -> sendMessage("Ceasing to exist", c))
    			.doOnNext(bot -> bot.getClient().logout().subscribe(e -> 
    			System.exit(0)));
    	}
    	else {
		return event.getMessage().getChannel()
				.flatMap(c -> sendMessage("Insufficient permissions.", c));
    	
    	}
    }

}
