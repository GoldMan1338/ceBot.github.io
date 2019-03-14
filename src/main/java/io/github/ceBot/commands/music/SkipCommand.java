package io.github.ceBot.commands.music;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.util.Snowflake;
import io.github.ceBot.BotUtils;
import io.github.ceBot.music.Music;
import io.github.ceBot.music.MusicPerms;
import io.github.ceBot.music.ServerMusic;
import reactor.core.publisher.Mono;

public class SkipCommand extends MusicCommandUtil {

	@Override
	public Set<MusicPerms> getRequiredPermission() {
		return EnumSet.of(MusicPerms.DJ, MusicPerms.ALONE);
	}

	@Override
	public  CommandInfo getInfo() {
		return new CommandInfo(
				"%cmdname%",
				"DJ - Force bot to skip current song and play next in queue" + 
				"ALONE - Bot will skip current song and play next in queue" +
				"NONE - Request that the current track be skipped (majority vote)");
	}

	@Override
	public Set<String> getNames() {
		return Stream.of("skip").collect(Collectors.toSet());
	}

	@Override
	protected Mono<Message> run(MessageCreateEvent event, String[] args) {
		return event.getMessage().getChannel().cast(TextChannel.class)
				.flatMap(c -> hasPermission(event, false)
						.filter(hasPermission -> hasPermission)
						.doOnNext(b -> Music.skipTrack(event.getClient(),  event.getGuildId().get()))
						.flatMap(b -> sendMessage("Song skipped <:babylaugh:547261342000742410>", c))
						.switchIfEmpty(voteSkip(event.getMember().get(), c)));
	}
	
	private static Mono<Message> voteSkip(Member member, TextChannel channel) {
		ServerMusic musicManager = Music.getGuildManager(member.getClient(), channel.getGuildId());
		Set<Snowflake> usersSkipping = musicManager.usersSkipping;
		if(usersSkipping.contains(member.getId())) {
			return BotUtils.sendMessage(":x: You already voted" + member.getMention() + "!", channel);
		}
		
		Mono<Integer> majority = Music.getBotVoiceChannelMajority(member.getClient(), channel.getGuildId());
		usersSkipping.add(member.getId());
		Mono<Message> added = majority
				.flatMap(num -> BotUtils.sendMessage(musicManager.usersSkipping.size() + "/" + num + " users have voted to skip.", channel));
		
		return majority
				.filter(maj -> musicManager.usersSkipping.size() >= maj)
				.doOnNext(ignored -> Music.skipTrack(member.getClient(), channel.getGuildId()))
				.flatMap(ignored -> BotUtils.sendMessage("Majority rules, song skipped <:babylaugh:547261342000742410>", channel))
				.switchIfEmpty(added);
	}

	

}
