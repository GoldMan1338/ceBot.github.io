package io.github.ceBot;


// import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.Image;


public class CommandHandler {
	
	public static String BOT_PREFIX = ">";
	
	private static Map<String, Command> commandMap = new HashMap<>();
	
    // Statically populate the commandMap with the intended functionality
    static {
    	//"testcommand" for what the user enters, follow up with desired actions
        commandMap.put("testcommand", (event, args) -> {
            MainRunner.sendMessage(event.getChannel(), "You ran the test command with args: " + args);
        });
        
        commandMap.put("ping", (event, args) -> {
        	MainRunner.sendMessage(event.getChannel(), "Pong!");;
        });
        
    	// alter image
    	commandMap.put("alterpic", (event, args) -> {
			String imstring = args.get(0).toString();
        	MainRunner.sendMessage(event.getChannel(), imstring);
        	if(event.getAuthor().getStringID().contains(Main.BOT_OWNER[0]) || event.getAuthor().getStringID().contains(Main.BOT_OWNER[1])){
    			try {
    				URL imurl = new URL(imstring);
    				BufferedImage img = ImageIO.read(imurl);
    				File f = new File("temp");
    				ImageIO.write(img, "", f);
    				Image im = (Image) ImageIO.read(f);
    				Main.bot.changeAvatar(im);
    			}
    				catch (IOException e) {
				 	// TODO Auto-generated catch block
				 	e.printStackTrace();
			 	}
    		}
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
    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event){
    	
		IUser sender = event.getMessage().getAuthor();
		String messageAuthor = sender.toString();
		
		
    	

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