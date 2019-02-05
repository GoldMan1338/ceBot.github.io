package io.github.ceBot;

import java.util.*;

import io.github.ceBot.Main;
import io.github.ceBot.MainRunner;
import sx.blah.discord.handle.obj.IUser;

@SuppressWarnings("unlikely-arg-type")
public class CommandHandler {
	
	public static String BOT_PREFIX = ">>";
	
	private static Map<String, Command> commandMap = new HashMap<>();
	
    // Statically populate the commandMap with the intended functionality
    static {
    	//"testcommand" for what the user enters, follow up with desired actions
        commandMap.put("testcommand", (event, args) -> {
            MainRunner.sendMessage(event.getChannel(), "You ran the test command with args: " + args);
        });
        
        commandMap.put("shutdown", (event, args) -> {
        	Timer timer = new Timer();
    		IUser sender = event.getMessage().getAuthor();
			if(sender.getStringID().contains(Main.BOT_OWNER[0]) || sender.getStringID().contains(Main.BOT_OWNER[1])){
				timer.schedule(new TimerTask() {
	            	@Override
	            	public void run() {
	            		Main.bot.logout();
	            		System.exit(0);
	            	}
	            }, 2*1000);
				MainRunner.sendMessage(event.getChannel(), "Ceasing to exist");
			}
			else {
				MainRunner.sendMessage(event.getChannel(), "Invalid Permissions");
			}
			
        });

    }

}