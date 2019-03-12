package io.github.ceBot.commands.music;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import ch.qos.logback.classic.pattern.Util;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.VoiceChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import io.github.ceBot.BotUtils;
import io.github.ceBot.Main;
import io.github.ceBot.commands.Command;
import io.github.ceBot.music.Music;
import io.github.ceBot.music.Music.LavaPlayerAudioProvider;
import io.github.ceBot.music.MusicPerms;
import io.github.ceBot.music.ServerMusic;
import io.github.ceBot.music.Tracks;
import reactor.core.publisher.Mono;

public class PlayCommand extends MusicCommandUtil {

	@Override
	public Set<MusicPerms> getRequiredPermission() {
		return EnumSet.of(MusicPerms.NONE);
	}

	@Override
	public CommandInfo getInfo() {
		return new CommandInfo("%cmdname% [song/url]",
				"Adds requesteed song to queue");
	}

	@Override
	public Set<String> getNames() {
		return Stream.of("play").collect(Collectors.toSet());
	}

	@Override
	protected Mono<?> run(MessageCreateEvent event, String[] args) {
		if(!event.getMember().isPresent()) return Mono.empty();
		if(args.length < 1) return incorrectUsage(event);
		return event.getMessage().getChannel()
				.filterWhen(c -> hasPermission(event))
				.flatMap(c -> Music.getConnectedVoiceChannel(event.getMember().get())
						.map(Optional::of)
						.defaultIfEmpty(Optional.empty()))
				.zipWith(Music.getBotConnectedVoiceChannel(event.getClient(), event.getGuildId().get())
						.map(Optional::of)
						.defaultIfEmpty(Optional.empty()))
				.flatMap(tuple -> {
					VoiceChannel userConnected = tuple.getT1().orElse(null);
					VoiceChannel botConnected = tuple.getT2().orElse(null);
					String query = formQuery(String.join(" ", args));
					return play(event, userConnected, botConnected, query);
				});
	}
	
	private Mono play(MessageCreateEvent event, VoiceChannel userConnected, VoiceChannel botConnected, String query) {
		if(botConnected != null && botConnected.equals(userConnected)) {
			return event.getMessage().getChannel().ofType(TextChannel.class)
					.doOnNext(c -> loadAndPlaySong(c, query));
		}
		else if(botConnected == null && userConnected == null) {
			return event.getMessage().getChannel()
					.flatMap(c -> sendMessage("Join a voice channel if you want me to play something.", c));
		}
		else if(botConnected != null && userConnected == null) {
			return event.getMessage().getChannel()
					.flatMap(c -> sendMessage("Join me in `" + botConnected.getName() + "` to add a song to the queue.", c));
		}
		else if(botConnected == null) {
			return join(event, userConnected)
					.then(event.getMessage().getChannel().ofType(TextChannel.class))
					.doOnNext(c -> loadAndPlaySong(c, query));
		}
		
		return Mono.empty();
	}
	
	private static Mono<VoiceConnection> join(MessageCreateEvent event, VoiceChannel userConnected) {
		ServerMusic manager = Music.getGuildManager(event.getClient(), event.getGuildId().get());
		Mono<VoiceConnection> connection = userConnected.join(voiceSpec -> voiceSpec.setProvider(new Music.LavaPlayerAudioProvider(manager.player)));
		return connection.doOnNext(vc -> Music.connections.put(event.getGuildId().get(), vc));
	}
	
	private static void loadAndPlaySong(final TextChannel channel, final String trackUrl) {
		ServerMusic manager = Music.getGuildManager(channel.getClient(), channel.getGuildId());
		Music.playerManager.loadItemOrdered(manager, trackUrl, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				playSong(channel, manager, track).subscribe();
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();
				if (firstTrack == null) firstTrack = playlist.getTracks().get(0);
				playSong(channel, manager, firstTrack).subscribe();
			}

			@Override
			public void noMatches() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				exception.printStackTrace();
			}
		});
	}
	
	private static Mono<Message> playSong(TextChannel channel, ServerMusic musicManager, AudioTrack track) {
		if(TimeUnit.MILLISECONDS.toSeconds(track.getDuration()) > TimeUnit.HOURS.toSeconds(10)) {
			return BotUtils.sendMessage(":x: Easy cowboy! That song is over 10 hours!", channel);
		}
		return musicManager.scheduler.queue(track, channel);
	}
	
	private String formQuery(String query) {
		boolean isValid = true;
		try {
			new URL(query).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			isValid = false;
		}
		if (!isValid) return "ytsearch:" + query;
		return query;
	}

}
