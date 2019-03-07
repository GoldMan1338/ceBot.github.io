package io.github.ceBot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import lombok.Value;
import io.github.ceBot.BotUtils;
import io.github.ceBot.commands.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public abstract class Command {
	@Value
	protected static class CommandInfo {
		String usage;
		String description;
		public CommandInfo(String usage, String description) {
			this.usage = usage;
			this.description = description;
		}
		public String getUsage() {
			return usage;
		}
		public String getDescription() {
			return description;
		}

	}
	
	public abstract CommandInfo getInfo();
	public abstract Set<String> getNames();
	
	public boolean isCommand(Message message) {
		return false;
	}
	public Mono<?> run(MessageCreateEvent event){
		if (!event.getMessage().getContent().isPresent()) return Mono.empty();
		String[] args = event.getMessage().getContent().get().split(" ");
		args = Arrays.copyOfRange(args, 1, args.length);
		return run(event, args);
	}
	
	protected abstract Mono<?> run(MessageCreateEvent event, String[] args);
    protected Mono<Message> incorrectUsage(MessageCreateEvent event) {
        return event.getMessage().getChannel().flatMap(c -> sendEmbed(HelpCommand.display(event, this), c));
    }

    protected static Mono<Message> sendMessage(String string, MessageChannel channel) {
        return BotUtils.sendMessage(string, channel);
    }

    protected static Mono<Message> sendEmbed(Consumer<? super EmbedCreateSpec> spec, MessageChannel channel) {
        return BotUtils.sendEmbed(spec, channel);
    }
}
