package io.github.ceBot;

import discord4j.core.DiscordClient;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.VoiceChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;

public class BotUtils {
	private BotUtils() {}
	
	public static Mono<Message> sendMessage(String string, MessageChannel channel){
		return channel.createMessage("\u200B" + string);
	}
	
    public static Mono<Message> sendEmbed(Consumer<? super EmbedCreateSpec> spec, MessageChannel channel) {
        return channel.createMessage(message -> message.setEmbed(spec));
    }
	//2 character prefix btw, itll be ">>"
	public static String getCommandName(Message message) {
		String content = message.getContent().orElse("");
		if(content.isEmpty()) return "";
		return content.substring(2).split(" ")[0].toLowerCase();
	}
	
	public static Mono<Boolean> isBotInVoiceChannel(DiscordClient client, Snowflake voiceChannelId) {
        return client.getChannelById(voiceChannelId)
                .ofType(VoiceChannel.class)
                .flatMap(BotUtils::isBotInVoiceChannel);
    }

    public static Mono<Boolean> isBotInVoiceChannel(VoiceChannel voiceChannel) {
        return voiceChannel.getVoiceStates()
                .any(vs -> voiceChannel.getClient().getSelfId().map(vs.getUserId()::equals).orElse(false));
    }

    public static Mono<Boolean> isBotAlone(DiscordClient client, Snowflake guildId) {
        return client.getSelf()
                .flatMap(u -> u.asMember(guildId))
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMapMany(VoiceChannel::getVoiceStates)
                .count()
                .map(count -> count == 1);
    }

    public static Mono<Boolean> isMemberAloneWithBot(Member member) {
        return member.getVoiceState()
                .flatMap(VoiceState::getChannel)
                .filterWhen(BotUtils::isBotInVoiceChannel) //bot is in voice channel
                .flatMapMany(VoiceChannel::getVoiceStates)
                .count()
                .map(it -> it == 2) //includes bot
                .defaultIfEmpty(false);
    }
    
    static String getTime(ZonedDateTime time) {
        String ordinal;
        switch (time.getDayOfMonth()) {
            case 1:
            case 21:
            case 31:
                ordinal = "st";
                break;
            case 2:
            case 22:
                ordinal = "nd";
                break;
            case 3:
            case 23:
                ordinal = "rd";
                break;
            default:
                ordinal = "th";
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE, MMMM d'" + ordinal + "', yyyy");
        return format.format(time);
    }
    static Duration happy420() {
        ZonedDateTime time = ZonedDateTime.now(ZoneId.of("CAN/Mountain"));
        ZonedDateTime tomorrow = time.withHour(16).withMinute(20).withSecond(0);
        if (time.compareTo(tomorrow) > 0) tomorrow = tomorrow.plusDays(1);
        return Duration.between(time, tomorrow);
    }

}
