/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 07:55]
 */

package cf.adriantodt.bot.spy;

import cf.adriantodt.bot.Answers;
import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.guild.DiscordGuild;
import net.dv8tion.jda.entities.*;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

import static cf.adriantodt.bot.Answers.bool;

public class Spy {
	public static List<MessageChannel> nodes = new ArrayList<>();

	public static void trigger(MessageReceivedEvent event) {
		if (nodes.contains(event.getChannel())) nodes.remove(event.getChannel());
		else nodes.add(event.getChannel());
		bool(event, nodes.contains(event.getChannel()));
	}

	public static void spy(MessageReceivedEvent event) {
		if (!DiscordGuild.GLOBAL.channelList.contains(event.getChannel()))
			DiscordGuild.GLOBAL.channelList.add(event.getChannel());
		if (nodes.contains(event.getChannel())) return;

		String message = "[" + getChannelName(event.getChannel()) + "] <" + event.getAuthor().getUsername() + "> " + event.getMessage().getContent();
		for (MessageChannel channel : nodes) if (channel != null) channel.sendMessage(message);
	}

	public static void spyPast(MessageReceivedEvent event, MessageChannel channel) {
		String msgs = "***Passado:***";
		List<Message> msgl = channel.getHistory().retrieve(10);
		for (int i = Math.min(10, msgl.size() - 1); i > 0; i--) {
			Message msg = msgl.get(i);
			msgs += "\n[" + getChannelName(channel) + "] <" + msg.getAuthor().getUsername() + "> " + msg.getContent();
		}
		Answers.send(event, msgs);
	}

//	public static void broadcast(String message) {
//		for (MessageChannel channel : DiscordGuild.GLOBAL.channelList)
//			if (channel != null) try {
//				channel.sendMessage(message);
//			} catch (Exception e) {
//				DiscordGuild.GLOBAL.channelList.remove(channel);
//			}
//	}

	public static void broadcast(DiscordGuild guild, String message, MessageReceivedEvent event) {
		for (MessageChannel channel : guild.channelList)
			try {
				channel.sendMessage(message);
			} catch (Exception e) {
				//guild.channelList.remove(channel);
			}
		bool(event, true);
	}

	public static void discoverChannels(MessageReceivedEvent event) {
		for (Guild guild : Bot.API.getGuilds()) {
			DiscordGuild discordGuild = DiscordGuild.fromDiscord(guild);
			for (TextChannel channel : guild.getTextChannels()) {
				if (!DiscordGuild.GLOBAL.channelList.contains(channel)) DiscordGuild.GLOBAL.channelList.add(channel);
				if (!discordGuild.channelList.contains(channel)) discordGuild.channelList.add(channel);
			}
		}
		bool(event, true);
	}

	public static void listChannelsKnown(DiscordGuild guild, MessageReceivedEvent event) {
		String msgs = "***Canais Conhecidos:***";
		for (int i = 0; i < guild.channelList.size(); i++) {
			MessageChannel channel = guild.channelList.get(i);
			if (channel instanceof TextChannel) {
				msgs += "\n[" + i + "] = " + ((TextChannel) channel).getGuild().getName() + ":" + ((TextChannel) channel).getName();
			} else if (channel instanceof PrivateChannel) {
				msgs += "\n[" + i + "] = " + ((PrivateChannel) channel).getUser().getUsername() + "'s PM";
			}
		}
		Answers.send(event, msgs);
	}

	public static void kickSelf(int channelId) {
		MessageChannel channel = DiscordGuild.GLOBAL.channelList.get(channelId);

		if (channel instanceof TextChannel) {
			((TextChannel) channel).getGuild().getManager().leave();
		}
	}

	public static String getChannelName(MessageChannel channel) {
		if (channel instanceof TextChannel) {
			return ((TextChannel) channel).getGuild().getName() + ":" + ((TextChannel) channel).getName() + "(#" + DiscordGuild.GLOBAL.channelList.indexOf(channel) + ")";
		} else if (channel instanceof PrivateChannel) {
			return ((PrivateChannel) channel).getUser().getUsername() + "'s PM" + "(#" + DiscordGuild.GLOBAL.channelList.indexOf(channel) + ")";
		}

		return "Além";
	}

	public static void channelFlush(DiscordGuild guild, MessageReceivedEvent event) {
		guild.channelList = new ArrayList<>();
		bool(event, true);
	}
}
