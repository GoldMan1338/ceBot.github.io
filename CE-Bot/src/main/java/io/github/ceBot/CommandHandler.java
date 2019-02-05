package io.github.ceBot;


// import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.Image;


public class CommandHandler {
	
	public static String BOT_PREFIX = ">>";
	
	private static Map<String, Command> commandMap = new HashMap<>();
	
    // Statically populate the commandMap with the intended functionality
    static {
    	//"testcommand" for what the user enters, follow up with desired actions
        commandMap.put("testcommand", (event, args) -> {
            MainRunner.sendMessage(event.getChannel(), "You ran the test command with args: " + args);
        });
        
    	// alter image
    	commandMap.put("alterpic", (event, args) -> {
    		try {
			String imstring;
			imstring = args.toString();
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

}}