package Bot;

/*
 * Copyright (C) 2017 FF6EB4
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.text.Document;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class Launcher implements IListener<MessageReceivedEvent>{

	public static Launcher INSTANCE; // Singleton instance of the bot.
	public static IDiscordClient client; // The instance of the discord client.
        public static IChannel chan = null;
        
	public static void main(String[] args) { // Main method
		INSTANCE = login("Mjc1MzcwMDE2Mjk4MzY5MDI1.C3s07A.qQRhQAp7R61A-NYTvejx1JGWJhw"); // Creates the bot instance and logs it in.
        }
        
        public static Lock messageLock = new ReentrantLock();

	public Launcher(IDiscordClient client) {
            
		this.client = client; // Sets the client instance to the one provided
                
                
                EventDispatcher dispatcher = client.getDispatcher(); // Gets the client's event dispatcher
		dispatcher.registerListener(this);
                
                
	}

	/**
	 * A custom login() method to handle all of the possible exceptions and set the bot instance.
	 */
	public static Launcher login(String token) {
		Launcher bot = null; // Initializing the bot variable

		ClientBuilder builder = new ClientBuilder(); // Creates a new client builder instance
		builder.withToken(token); // Sets the bot token for the client
		try {
			IDiscordClient client = builder.login(); // Builds the IDiscordClient instance and logs it in
			bot = new Launcher(client); // Creating the bot instance
		} catch (DiscordException e) { // Error occurred logging in
			System.err.println("Error occurred while logging in!");
			e.printStackTrace();
		}

		return bot;
	}
        
       public void handle(MessageReceivedEvent event) {
            IMessage message = event.getMessage(); // Gets the message from the event object NOTE: This is not the content of the message, but the object itself
            IChannel channel = message.getChannel(); // Gets the channel in which this message was sent.
            
            Long ID = message.getAuthor().getLongID();
            
            if(ID == 198187787449532427L){
                String content = message.getContent();
                
                if(content.charAt(0) == '!'){
                    content = content.substring(1);
                    Long gnuChan = Long.parseLong(content);
                    IChannel get = client.getChannelByID(gnuChan);
                    this.chan = get;
                } else {
                    IChannel get = message.getChannel();
                    if(get.isPrivate()){
                        echo(message);
                    }
                }
            }
            if(chan == null)return;
            if(channel.getLongID() == chan.getLongID()){
                DM(message);
            }
       }
       
       

       
       private static void echo(IMessage get){
           if(chan == null)return;
           IChannel channel = chan;
           String message = get.getContent();
           
           try {
                    // Builds (sends) and new message in the channel that the original message was sent with the content of the original message.
                    new MessageBuilder(INSTANCE.client).withChannel(channel).withContent(message).build();
            } catch (RateLimitException e) {
            } catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
                    System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
                    e.printStackTrace();

            } catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
                    System.err.print("Missing permissions for channel!");
                    e.printStackTrace();
            }
       }
       
       private static void DM(IMessage get){
           
           IUser to = client.getUserByID(198187787449532427L);
           IChannel channel = to.getOrCreatePMChannel();
           String message = get.getAuthor().getName()+": "+get.getContent();
           
           try {
                    // Builds (sends) and new message in the channel that the original message was sent with the content of the original message.
                    new MessageBuilder(INSTANCE.client).withChannel(channel).withContent(message).build();
            } catch (RateLimitException e) {
            } catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
                    System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
                    e.printStackTrace();

            } catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
                    System.err.print("Missing permissions for channel!");
                    e.printStackTrace();
            }
       }

}
