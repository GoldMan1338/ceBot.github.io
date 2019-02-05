package io.github.ceBot;

import java.util.*;

public class CommandHandler {
	
	public static String BOT_PREFIX = ">>";
	
	private static Map<String, Command> commandMap = new HashMap<>();
	
    // Statically populate the commandMap with the intended functionality
    static {
    	//"testcommand" for what the user enters, follow up with desired actions
        commandMap.put("testcommand", (event, args) -> {
            MainRunner.sendMessage(event.getChannel(), "You ran the test command with args: " + args);
        });

    }

}