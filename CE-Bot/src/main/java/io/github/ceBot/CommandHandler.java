package io.github.ceBot;


// import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

import javax.imageio.ImageIO;

import org.omg.CORBA.portable.InputStream;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;



interface CommandHandler {
	//public static String BOT_PREFIX = ">";
	//public static Map<String, Command> commandMap = new HashMap<>();
    // Statically populate the commandMap with the intended functionality
    
    	//"testcommand" for what the user enters, follow up with desired actions
        /*GetMessageCommand.commandMap.put("testcommand", (event, args) -> {
            MainRunner.sendMessage(event.getChannel(), "You ran the test command with args: " + args);
        });*/
    static void handler() {
        Main.client.getEventDispatcher().on(MessageCreateEvent.class)
        	.map(MessageCreateEvent::getMessage)
        	.filterWhen(message -> message.getAuthor().map(user -> !user.isBot()))
        	.filter(message -> message.getContent().orElse("").equalsIgnoreCase(">>ping"))
        	.flatMap(Message::getChannel)
        	.flatMap(channel -> channel.createMessage("Pong!"))
        	.subscribe();
        //fix this
        
			
    	Main.client.getEventDispatcher().on(MessageCreateEvent.class)
    		.map(MessageCreateEvent::getMessage)
    		.filterWhen(message -> message.getAuthor().map(user -> !user.isBot()))
    		.filter(message -> message.getAuthorId().toString().equals(Main.BOT_OWNER))
    		.filter(message -> message.getContent().orElse("").equalsIgnoreCase(">>yeet"))
    		.flatMap(Message::getChannel)
    		.flatMap(channel -> channel.createMessage("boi"))
    		.subscribe();
    
        /*GetMessageCommand.commandMap.put("sendmessage", (event, args) -> {
        	if(GetOwners.getOwners(event)){
        		String channelidstring = args.get(0);
        		int finalarg = args.size() - 1;
        		String message = args.toString().substring(1, finalarg);
        		Long channelidlong = Long.parseLong(channelidstring);
        		
        	
        		MainRunner.sendMessage(event.getGuild().getChannelByID(channelidlong), message);
        		//MainRunner.sendMessage(event.getChannel(), message + channelidlong);
        	}
        });*/
        
		/*GetMessageCommand.commandMap.put("calc", (event, args) -> {
        	double n1,n2;
        	char z;
        	n1= Double.valueOf(args.get(0));
        	z= args.get(1).charAt(0);
        	n2= Double.valueOf(args.get(2));
        	double f = 0;
        	
        	switch(z) // switch is on operand // display answer as equaton
            {
                case '+': f = n1+n2; // if + is used ' ' used for values
        		MainRunner.sendMessage(event.getChannel(), String.valueOf(n1+n2));
                break; // breaks switch                 
                case '-': f = n1-n2; // if - is used
        		MainRunner.sendMessage(event.getChannel(), String.valueOf(n1-n2));
                break; // breaks switch                
                case '*': f = n1*n2; // if * is used ' ' used for values
        		MainRunner.sendMessage(event.getChannel(), String.valueOf(n1*n2));
                break; // breaks switch                
                case '/': f = n1/n2; // if / is used ' ' used for values
        		MainRunner.sendMessage(event.getChannel(), String.valueOf(n1/n2));
                break; // breaks switch 
                //error message 
                default: System.out.println("ERROR! INVALID OPERAND");
            }
        	
        });*/
		
        

    	// Changes Bot PFP, only bot owners can use

        /*GetMessageCommand.commandMap.put("alterpic", (event, args) -> {
			String imstring = args.get(0).toString();
        	//MainRunner.sendMessage(event.getChannel(), imstring);
			//File f = new File("temp");
			
        	if(GetOwners.getOwners(event)){
    			Image myimage = Image.forUrl("png", imstring);
				//URL imurl = new URL(imstring);
				//BufferedImage img = ImageIO.read(imurl);
				//ImageIO.write(img, "", f);
				//Image im = (Image) ImageIO.read(f);
				//image = ImageIO.read(ssaveImage());
				Main.bot.changeAvatar(myimage);
    		}
    	});*/
        
		/*GetMessageCommand.commandMap.put("cname", (event, args) -> {
    		if(GetOwners.getOwners(event)){
    			MainRunner.sendMessage(event.getChannel(), "Changing Name");
    			Main.bot.changeUsername(args.get(0));
    		}
    	});*/
        
		/*GetMessageCommand.commandMap.put("getGuild", (event, args) -> {
    		IGuild defg = event.getGuild();
    		MainRunner.sendMessage(event.getChannel(), defg.getName());

    	});*/
        
		/*GetMessageCommand.commandMap.put("getGuilds", (event, args) -> {
        	//This should grab the names of all guilds the bot is present in
        	//and send it to the requested channel. Currently does not work.
        	 
    		List<IGuild> guildlist = Main.bot.getGuilds();
    		String[] guilds = guildlist.toString().split("\n");
    		MainRunner.sendMessage(event.getChannel(), guilds.toString());
    		
    		String channelidstring = args.get(0);
    		int finalarg = args.size() - 1;
    		String message = args.toString().substring(1, finalarg);
    		Long channelidlong = Long.parseLong(channelidstring);
    		
    		
    	
    		MainRunner.sendMessage(event.getGuild().getChannelByID(channelidlong), message);

    	});*/
        
        
    }
    /*@EventSubscriber
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

    }*/
    
    //if you ever want to use this you can, if not you can get rid of it
    /*public static Image saveImage(String imageUrl) throws ClassCastException, IOException {
        URL url = new URL(imageUrl);
        String fileName = url.getFile();
        String destName = "./figures" + fileName.substring(fileName.lastIndexOf("/"));
        System.out.println(destName);
     
        InputStream is = (InputStream) url.openStream();
        OutputStream os = new FileOutputStream(destName);
        File im = new File(destName);
     
        byte[] b = new byte[2048];
        int length;
     
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        Image image = (Image) ImageIO.read(im);
        is.close();
        os.close();
        return image;
    }*/
    

}