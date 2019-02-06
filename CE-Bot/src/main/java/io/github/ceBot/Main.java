package io.github.ceBot;

import java.util.List;
import java.util.Scanner;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;

public class Main {
	
	public static final IDiscordClient bot = MainRunner.createClient("NTQyMTM3NDM1NjgxNzE4Mjcz.Dzp-hA.sSAi0DTA9rcprC3VKTLKHxLHdAU", true);
	
	public static void main(String[] event) {
		//add more of these if you make new classes
		//import the location of said classes too (ONLY IF IN OTHER PACKAGES)
		bot.getDispatcher().registerListener(new MainRunner());
		bot.getDispatcher().registerListener(new CommandHandler());
		bot.getDispatcher().registerListener(new GetMessageCommand());
		bot.checkLoggedIn(null);
		
		
		
	}
	public static List<IGuild> BOT_GUILDS = bot.getGuilds();
	
	public static final String BOT_PREFIX = ">";
	
	public static String[] BOT_OWNER = {"139541886888181760","422588424487174144","195621535703105536"};
	
	public static String BOT_INVITE = "https://discordapp.com/oauth2/authorize?client_id=542137435681718273&scope=bot&permissions=8";
	
}

