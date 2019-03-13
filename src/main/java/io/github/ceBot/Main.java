package io.github.ceBot;


import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.util.Snowflake;
import io.github.ceBot.*;
import io.github.ceBot.BotYoutube;
import reactor.core.publisher.Mono;
@SpringBootApplication
public class Main {
	

public static void main(String[] args){
    SpringApplication.run(Main.class, args);
    BotConfig.login().block();
}

public static void schedule(DiscordClient client) {
	BotYoutube.schedule(client);
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    boolean heroku = System.getenv().containsKey("HEROKU");
    if (heroku && System.getenv("HEROKU").equalsIgnoreCase("true")) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL(System.getenv("URL")).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                connection.getResponseCode();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.MINUTES);
    }
}

    public static long getFirstOnline() {
        return firstOnline;
    }

    static void setFirstOnline(long millis) {
        firstOnline = millis;
    }

    public static String getPrefix(DiscordClient client, Snowflake guildId) {
        //TODO add support for changing prefix later
        if (System.getenv().containsKey("PREFIX")) return System.getenv("PREFIX");
        return ",";
    }
	
	public static final ObjectMapper mapper = new ObjectMapper();
	
	private static long firstOnline;
	
	public static final long START_CHANNEL = 489194048519667723L;
	
	//public static final String PREFIX = ">";
	
	public static String[] BOT_OWNER = {"139541886888181760","422588424487174144","195621535703105536"};
	
	public static Set<Long> ownerIds = Stream.of(139541886888181760l,422588424487174144l).collect(Collectors.toSet());
	
	public static String BOT_INVITE = "https://discordapp.com/oauth2/authorize?client_id=542137435681718273&scope=bot&permissions=8";
	
}

