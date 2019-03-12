package io.github.ceBot.commands.music;

import java.util.Arrays;
import java.util.Set;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.VoiceChannel;
import io.github.ceBot.commands.Command;
import io.github.ceBot.music.*;
import reactor.core.publisher.Mono;

public abstract class MusicCommandUtil extends Command{

	public abstract Set<MusicPerms> getRequiredPermission();
	
	
	Mono<Boolean> hasPermission(MessageCreateEvent event) {
		return hasPermission(event, true);
	}
	
	Mono<Boolean> hasPermission(MessageCreateEvent event, boolean displayNoPermissions) {
		return event.getMessage().getChannel().cast(TextChannel.class)
				.flatMap(c -> MusicPerms.requirePermissions(event.getMember().get(), c, getRequiredPermission(), displayNoPermissions));
	}

	@Override
	public Mono<?> run(MessageCreateEvent event) {
		if(!event.getMessage().getContent().isPresent() || !event.getMember().isPresent()) return Mono.empty();
		String[] temp = event.getMessage().getContent().get().split(" ");
		String[] args = Arrays.copyOfRange(temp, 1, temp.length);
		
		Mono<VoiceChannel> botConnected = Music.getBotConnectedVoiceChannel(event.getClient(), event.getGuildId().get());
		Mono<VoiceChannel> userConnected = Music.getConnectedVoiceChannel(event.getMember().get());
		Mono<Boolean> sameChannel = botConnected
				.flatMap(voiceChannel -> userConnected.map(voiceChannel::equals))
				.defaultIfEmpty(false);
		Mono<Message> fallback = event.getMessage().getChannel()
				.flatMap(c -> sendMessage("You are not in the same channel as the me!", c));
		return locked(event)
				.filter(locked -> !locked)
				.flatMap(ignored -> sameChannel
						.filter(same -> this instanceof MusicCommand || this instanceof PlayCommand || same)
						.flatMap(same -> run(event, args).thenReturn(same))
						.cast(Object.class)
						.switchIfEmpty(fallback));
	}
	private Mono<Boolean> locked(MessageCreateEvent event){
		return event.getMessage().getChannel().cast(TextChannel.class)
				.flatMap(c -> Music.locked(event.getMember().get(), c));
	}
	@Override
	protected Mono<Message> incorrectUsage(MessageCreateEvent event){
		return event.getMessage().getChannel().flatMap(c -> sendEmbed(MusicCommand.displayMusic(event, this), c));
	}
	

}
