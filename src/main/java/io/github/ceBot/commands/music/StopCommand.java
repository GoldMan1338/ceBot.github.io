package io.github.ceBot.commands.music;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.VoiceChannel;
import io.github.ceBot.commands.Command;
import io.github.ceBot.music.Music;
import io.github.ceBot.music.MusicPerms;
import reactor.core.publisher.Mono;

public class StopCommand extends MusicCommandUtil {

	@Override
	public CommandInfo getInfo() {
		return new CommandInfo("%cmdname%",
				"Stops the bot playing music and leave voice");
	}

	@Override
	public Set<String> getNames() {
		
		return Stream.of("stop", "leave").collect(Collectors.toSet());
	}

	@Override
	protected Mono<?> run(MessageCreateEvent event, String[] args) {
		if (!event.getMember().isPresent()) return Mono.empty();
		return event.getMessage().getChannel()
				.filterWhen(c -> hasPermission(event))
				.flatMap(c -> Music.getBotConnectedVoiceChannel(event.getClient(), event.getGuildId().get())
						.flatMap(Mono::justOrEmpty)
						.flatMap(voiceChannel -> {
							Music.disconnectBotFromChannel(event.getGuildId().get());
							return sendMessage("Stopped playing music in `" + voiceChannel.getName() + "`", c);
						})
						.switchIfEmpty(sendMessage("I'm not even in a voice channel so you must be a troll.", c)));
						
	}

	@Override
	public Set<MusicPerms> getRequiredPermission() {
		return EnumSet.of(MusicPerms.DJ, MusicPerms.ALONE);
	}

}
