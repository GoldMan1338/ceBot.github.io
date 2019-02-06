package io.github.ceBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import io.github.ceBot.Command;

public class GetMessageCommand {
		public static String BOT_PREFIX = ">";
		public static Map<String, Command> commandMap = new HashMap<>();
		@EventSubscriber
	    public void onMessageReceived(MessageReceivedEvent event){
	    	
	        // Note for error handling, you'll probably want to log failed commands with a logger or sout
	        // In most cases it's not advised to annoy the user with a reply incase they didn't intend to trigger a
	        // command anyway, such as a user typing ?notacommand, the bot should not say "notacommand" doesn't exist in
	        // most situations. It's partially good practise and partially developer preference

	        // Given a message "/test arg1 arg2", argArray will contain ["/test", "arg1", "arg"]
	        String[] argArray = event.getMessage().getContent().split(" ");

	        // First ensure at least the command and prefix is present, the arg length can be handled by your command func
	        if(argArray.length == 0)
	            return;

	        // Check if the first arg (the command) starts with the prefix defined in the utils class
	        if(!argArray[0].startsWith(BOT_PREFIX))
	            return;

	        // Extract the "command" part of the first arg out by just ditching the first character
	        String commandStr = argArray[0].substring(1);

	        // Load the rest of the args in the array into a List for safer access
	        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
	        argsList.remove(0); // Remove the command

	        // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists

	        if(commandMap.containsKey(commandStr))
	            commandMap.get(commandStr).runCommand(event, argsList);

	    }

}
