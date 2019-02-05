package io.github.ceBot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class MainRunner {
	
	
	
	
	
	
	public static void main(String args[]) {
		
		System.out.println(Main.bot.getApplicationClientID());

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


