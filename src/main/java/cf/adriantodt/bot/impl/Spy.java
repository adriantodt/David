/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 08:18]
 */

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.Answers;
import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.base.guild.DiscordGuild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

import static cf.adriantodt.bot.Answers.bool;

public class Spy {
	public static List<MessageChannel> nodes = new ArrayList<>();

	public static List<MessageChannel> getChannels(DiscordGuild guild) {
		List<MessageChannel> r = new ArrayList<>();
		if (guild == DiscordGuild.PM || guild == DiscordGuild.GLOBAL) r.addAll(Bot.API.getPrivateChannels());
		if (guild == DiscordGuild.GLOBAL) r.addAll(Bot.API.getTextChannels());
		if (guild.guild != null) r.addAll(guild.guild.getTextChannels());
		return r;
	}


	public static void sendToNodes(String message) {
		nodes.stream().filter(channel -> channel != null).forEach(channel -> channel.sendMessage(message));
	}


	public static void trigger(MessageReceivedEvent event) {
		if (nodes.contains(event.getChannel())) nodes.remove(event.getChannel());
		else nodes.add(event.getChannel());
		bool(event, nodes.contains(event.getChannel()));
	}

	public static void spy(MessageReceivedEvent event) {
		if (nodes.contains(event.getChannel())) return;
		sendToNodes("[" + getChannelName(DiscordGuild.fromDiscord(event), event.getChannel()) + "] <" + event.getAuthor().getUsername() + "> " + event.getMessage().getContent());
	}

	public static void spy(GuildJoinEvent event) {
		sendToNodes("[!] Joined " + event.getGuild().getName() + "!");
	}

	public static void spyPast(MessageReceivedEvent event, MessageChannel channel) {
		String msgs = "***Past:***";
		List<Message> msgl = channel.getHistory().retrieve(10);
		for (int i = Math.min(10, msgl.size() - 1); i > 0; i--) {
			Message msg = msgl.get(i);
			msgs += "\n[" + getChannelName(DiscordGuild.fromDiscord(event), channel) + "] <" + msg.getAuthor().getUsername() + "> " + msg.getContent();
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
		for (MessageChannel channel : getChannels(guild))
			try {
				channel.sendMessage(message);
			} catch (Exception e) {
				//guild.channelList.remove(channel);
			}
		bool(event, true);
	}

	public static void listChannels(DiscordGuild guild, MessageReceivedEvent event) {
		String msgs = "***Canais:***";
		List<MessageChannel> l = getChannels(guild);
		for (int i = 0; i < l.size(); i++) {
			MessageChannel channel = l.get(i);
			if (channel instanceof TextChannel) {
				msgs += "\n[" + i + "] = " + ((TextChannel) channel).getGuild().getName() + ":" + ((TextChannel) channel).getName();
			} else if (channel instanceof PrivateChannel) {
				msgs += "\n[" + i + "] = " + ((PrivateChannel) channel).getUser().getUsername() + "'s PM";
			}
		}
		Answers.send(event, msgs);
	}

	public static void kickSelf(MessageReceivedEvent event, int channelId) {
		MessageChannel channel = getChannels(DiscordGuild.GLOBAL).get(channelId);

		if (channel instanceof TextChannel) {
			((TextChannel) channel).getGuild().getManager().leave();
		}
		bool(event, channel instanceof TextChannel);
	}

	public static String getChannelName(DiscordGuild guild, MessageChannel channel) {
		if (channel instanceof TextChannel) {
			return ((TextChannel) channel).getGuild().getName() + ":" + ((TextChannel) channel).getName() + "(#" + getChannels(guild).indexOf(channel) + ")";
		} else if (channel instanceof PrivateChannel) {
			return ((PrivateChannel) channel).getUser().getUsername() + "'s PM" + "(#" + getChannels(guild).indexOf(channel) + ")";
		}

		return "Além";
	}

	public static void spy(GuildLeaveEvent event) {
		sendToNodes("[!] Leaved " + event.getGuild().getName() + "!");
	}
}
