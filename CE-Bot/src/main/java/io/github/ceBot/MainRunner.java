package io.github.ceBot;

import java.util.Scanner;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class MainRunner {
	
	
	
	
	
	
	public static void main(String args) {
		Main.bot.checkLoggedIn(null);
		System.out.println(Main.bot.getApplicationClientID());
		while(Main.bot.isReady()) {
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(System.in);
			args = scan.nextLine();
			System.out.println("This is a test");
			if(args == "shutdown") {
        			Main.bot.logout();
        			System.exit(0);
			}
		}
		

	}
	
	public static IDiscordClient createClient(String token, boolean login) {
		
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token);
		try {
			
			if (login) {
				return clientBuilder.login();
				
			} else {
				return clientBuilder.build();
			}
		} catch (DiscordException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public static void sendMessage(IChannel channel, String message) {
		RequestBuffer.request(() -> {
			try {
				channel.sendMessage(message);
			} catch (DiscordException e) {
				System.out.println("Message could not be send with error: ");
				e.printStackTrace();
			}
			
		});
		
	}
	
}


