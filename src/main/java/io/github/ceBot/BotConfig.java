package io.github.ceBot;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.Set;
import java.util.EnumSet;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import discord4j.common.jackson.PossibleModule;
import discord4j.common.jackson.UnknownPropertyHandler;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.util.Permission;
import discord4j.core.shard.ShardingClientBuilder;
import discord4j.rest.RestClient;
import discord4j.rest.http.ExchangeStrategies;
import discord4j.rest.http.client.DiscordWebClient;
import discord4j.rest.json.response.GatewayResponse;
import discord4j.rest.request.DefaultRouter;
import discord4j.rest.request.Router;
import discord4j.rest.route.Routes;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

class BotConfig {
	
	private BotConfig() {}
	
	
	static Mono<Void> login() {
		return login(true)
				.flatMap(DiscordClient::login)
				.then();
	}
	static Mono<Void> logout() {
		return login(false)
				.flatMap(DiscordClient::logout)
				.then();
	}
	static Flux<DiscordClient> login(boolean registerEvents){
		return new ShardingClientBuilder(System.getenv("TOKEN"))
				.build()
				.map(shard -> shard.setEventScheduler(Schedulers.immediate())
						.setInitialPresence(Presence.doNotDisturb(Activity.playing("the loading game"))))
				.map(DiscordClientBuilder::build)
				.doOnNext(client -> {
					if (registerEvents) registerEvents(client);
				});
		/*DiscordClientBuilder builder = new DiscordClientBuilder(System.getenv("TOKEN"))
				.setInitialPresence(Presence.doNotDisturb(Activity.playing("the loading game")));
		return getShardCount(builder.getToken())
                .flatMapMany(shardCount -> Flux.range(0, shardCount)
                        .map(i -> builder.setShardIndex(i).build())
                        .doOnNext(client -> {
                            if (registerEvents)
                            	registerEvents(client);
                        }) 
                );*/
	}
	

	

	
	
	private static void registerEvents(DiscordClient client) {
        EventDispatcher dispatcher = client.getEventDispatcher();
        Mono.when(
                dispatcher.on(MessageCreateEvent.class)
                        .filterWhen(e -> e.getMessage().getChannel().map(c -> c.getType() == Channel.Type.GUILD_TEXT))
                        .filterWhen(e -> e.getMessage().getChannel().cast(TextChannel.class)
                        		.flatMap(c -> Mono.justOrEmpty(client.getSelfId()).flatMap(c::getEffectivePermissions).map(set -> set.asEnumSet().contains(Permission.SEND_MESSAGES))))
                        .filter(e -> e.getMessage().getAuthor().map(u -> !u.isBot()).orElse(false))
                        .flatMap(BotEvents::onMessageCreate)
                        .onErrorContinue((error, event) -> LoggerFactory.getLogger(Main.class).error("Event listener had an uncaught exception!", error)),
                dispatcher.on(ReadyEvent.class)
                        .take(1)
                        .filter(ignored -> client.getConfig().getShardIndex() == 0) //only want to schedule once
                        .zipWith(Mono.delay(Duration.ofSeconds(4)))
                    	.flatMap(e -> client.updatePresence(Presence.online(Activity.watching("for commands!"))))
                        .doOnNext(ignored -> Main.setFirstOnline(System.currentTimeMillis())) //set the first online time on ready event of shard 0
                        .doOnNext(ignored -> Main.schedule(client))
                        
        				,
                dispatcher.on(VoiceStateUpdateEvent.class)
                                .filter(event -> event.getClient().getSelfId()
                                        .map(id -> !id.equals(event.getCurrent().getUserId()))
                                        .orElse(false)) //don't want bot user
                                .flatMap(BotEvents::onVoiceChannelLeave)).subscribe();
            }
}
