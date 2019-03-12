package io.github.ceBot.commands.music;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Consumer;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import io.github.ceBot.BotEvents;
import io.github.ceBot.BotUtils;
import io.github.ceBot.Main;
import io.github.ceBot.commands.Command;
import io.github.ceBot.music.MusicPerms;
import reactor.core.publisher.Mono;

public class MusicCommand extends MusicCommandUtil {

	@Override
	public CommandInfo getInfo() {
		return getStaticInfo();
	}

	private static CommandInfo getStaticInfo() {
		return new CommandInfo("music [command]",
				"Music module");
	}
	
	@Override
	public Set<MusicPerms> getRequiredPermission() {
		return null;
	}

	@Override
	public Set<String> getNames() {
		return Stream.of("music").collect(Collectors.toSet());
	}

	@Override
	protected Mono<Message> run(MessageCreateEvent event, String[] args) {
		if (args.length != 1) return displayMusicCommand(event);
		String inputCmd = args[0].toLowerCase();
		if (inputCmd.equals("music")) return displayMusicCommand(event);
		Command selected = BotEvents.getCommandNameMap().get(inputCmd);
		if(!(selected instanceof MusicCommandUtil)) return displayMusicCommand(event);
		Consumer<EmbedCreateSpec> spec = displayMusic(event, inputCmd, (MusicCommandUtil) selected);
		return event.getMessage().getChannel().flatMap(c -> sendEmbed(spec, c));
	}
	
	static Consumer<EmbedCreateSpec> displayMusic(MessageCreateEvent event, MusicCommandUtil command) {
        return displayMusic(event, BotUtils.getCommandName(event.getMessage(), event.getGuildId().get()), command);
    }

    private static Consumer<EmbedCreateSpec> displayMusic(MessageCreateEvent event, String inputCmd, MusicCommandUtil command) {
        return displayMusic(event, inputCmd, command.getClass().getSimpleName().replace("Command", ""),
                command.getInfo(), command.getRequiredPermission());
    }
    private static Consumer<EmbedCreateSpec> displayMusic(MessageCreateEvent event, String inputCmd, String commandName,
    		CommandInfo commandInfo, Set<MusicPerms> permissions){
    	String prefix = Main.getPrefix(event.getClient(), event.getGuildId().get());
    	
    	String usage = commandInfo.getUsage().replace("%cmdname", inputCmd);
    	String description = commandInfo.getDescription().replace("%prefix%", prefix);
    	String title = commandName + " Command - Music";
    	String perms = permissions == null ? null :
    		String.join(", ", permissions.stream().map(Enum::toString).collect(Collectors.toSet()));
    	
    	return embed -> {
    		embed.setColor(new Color(42, 100, 74));
    		embed.setAuthor(title, null, null);
    		embed.addField("Usage", "`" + usage + "`", false);
    		embed.addField("Description", description, false);
    		if (perms != null) embed.addField("Required Permissions", perms, false);
    	};
    }
    
    public static Mono<Message> displayMusicCommand(MessageCreateEvent event) {
    	List<String> aliases = BotEvents.getCommands()
    			.stream()
    			.filter(cmd -> cmd instanceof MusicCommandUtil)
    			.flatMap(cmd -> cmd.getNames().stream())
    			.collect(Collectors.toList());
    	String commandNames = String.join(", ", aliases);
    	String permissions = "DJ - Unlimited command usage, more of an MC\n" +
    						 "ALONE - Limited command usage but only when alone with me\n" +
    						 "NONE - No permissions required and can use anytime";
    	Consumer<EmbedCreateSpec> spec = displayMusic(event, "music", "Music", getStaticInfo(), null)
    			.andThen(embed -> {
    				embed.addField("Permissions", permissions, false);
    				embed.addField("Commands", commandNames, false);
    			});
    	return event.getMessage().getChannel().flatMap(c -> sendEmbed(spec, c));
    }

}
