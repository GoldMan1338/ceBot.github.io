package io.github.ceBot;

import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.util.Snowflake;
import io.github.ceBot.commands.Command;
import io.github.ceBot.music.Music;
import javassist.Modifier;
import reactor.core.publisher.Mono;

public class BotEvents {
	private static Map<String, Command> names = new HashMap<>();
	private static Set<Command> commands = new HashSet<>();
	private BotEvents() {}
	static {
        Reflections reflections = new Reflections(BotEvents.class.getPackage().getName());
        Set<Class<? extends Command>> set = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> cless : set) {
            try {
                if (Modifier.isAbstract(cless.getModifiers())) continue;
                Command command = cless.getDeclaredConstructor().newInstance();
                commands.add(command);
                command.getNames().forEach(name -> names.put(name, command));
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                System.out.println("An error happened when doing that command, gonna fukn kms");
                System.exit(-1);
			}
        }
    }
			
			
		    static Mono<?> onMessageCreate(MessageCreateEvent event) {
		        if (!event.getMessage().getContent().isPresent() || !event.getMessage().getAuthor().isPresent())
		            return Mono.empty();
		        String prefix = Main.getPrefix(event.getClient(), event.getGuildId().get());
		        Command command = null;
		        if (event.getMessage().getContent().get().startsWith(prefix)) {
		            String commandName = BotUtils.getCommandName(event.getMessage());
		            command = names.get(commandName);
		        }
		        if (command == null) {
		            command = commands.stream()
		                    .filter(cmd -> cmd.isCommand(event.getMessage()))
		                    .findAny().orElse(null);
		        }
		        if (command != null) return command.run(event);
		        return Mono.empty();
		    }
		    
		    static Mono<Snowflake> onVoiceChannelLeave(VoiceStateUpdateEvent event) {
		        if (Music.connections.get(event.getCurrent().getGuildId()) == null) return Mono.empty();
		        if (!event.getOld().isPresent()) return Mono.empty(); //not possible for user to have left channel
		        Snowflake guildId = event.getCurrent().getGuildId();

		        return Mono.justOrEmpty(event.getOld().flatMap(VoiceState::getChannelId))
		                .filterWhen(old -> event.getCurrent().getChannel().hasElement().map(connected -> !connected)) //user left channel
		                .doOnNext(old -> Music.getGuildManager(event.getClient(), guildId).usersSkipping.remove(event.getCurrent().getUserId()))
		                .filterWhen(old -> BotUtils.isBotInVoiceChannel(event.getClient(), old))
		                .filterWhen(old -> BotUtils.isBotAlone(event.getClient(), guildId))
		                .doOnNext(ignored -> Music.disconnectBotFromChannel(event.getCurrent().getGuildId()));
		    }
			
			public static Map<String, Command> getCommandNameMap() {
		        return new HashMap<>(names);
		    }

		    public static Set<Command> getCommands() {
		        return new HashSet<>(commands);
		    }
		    
	
	
}