package io.github.ceBot;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.List;

public interface Command {
	
	//interface for command to be implemented into command map
	void runCommand(MessageReceivedEvent event, List<String> args);

}