package io.github.ceBot;

import sx.blah.discord.api.IDiscordClient;

public class Main {
	
	public static final IDiscordClient bot = MainRunner.createClient("NTQyMTM3NDM1NjgxNzE4Mjcz.Dzp-hA.sSAi0DTA9rcprC3VKTLKHxLHdAU", true);
	
	public static void main(String[] event) {
		//add more of these if you make new classes
		//import the location of said classes too
		bot.getDispatcher().registerListener(new MainRunner());
		bot.checkLoggedIn(null);
		
	}
	
	public static final String BOT_PREFIX = ">>";
	
	public static String[] BOT_OWNER = {"139541886888181760","422588424487174144"};
	
	public static String BOT_INVITE = "https://discordapp.com/oauth2/authorize?client_id=542137435681718273&scope=bot&permissions=8";
	
}

