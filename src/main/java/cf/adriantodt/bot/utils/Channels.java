/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:11]
 */

package cf.adriantodt.bot.utils;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.data.Guilds;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

import static cf.adriantodt.bot.utils.Answers.bool;

public class Channels {
	public static void getPast(MessageReceivedEvent event, MessageChannel channel) {
		String msgs = "***Past:***";
		List<Message> msgl = channel.getHistory().retrieve(10);
		for (int i = Math.min(10, msgl.size() - 1); i > 0; i--) {
			Message msg = msgl.get(i);
			msgs += "\n[" + getChannelName(Guilds.fromDiscord(event), channel) + "] <" + msg.getAuthor().getUsername() + "> " + msg.getContent();
		}
		Answers.send(event, msgs);
	}

	public static String getChannelName(Guilds.Data guild, MessageChannel channel) {
		if (channel instanceof PrivateChannel) {
			return ((PrivateChannel) channel).getUser().getUsername() + "'s PM" + "(#" + getChannels(guild).indexOf(channel) + ")";
		} else if (channel instanceof TextChannel) {
			return ((TextChannel) channel).getGuild().getName() + ":" + ((TextChannel) channel).getName() + "(#" + getChannels(guild).indexOf(channel) + ")";
		}

		return "Além";
	}

	public static List<MessageChannel> getChannels(Guilds.Data guild) {
		List<MessageChannel> r = new ArrayList<>();
		if (guild == Guilds.PM || guild == Guilds.GLOBAL) r.addAll(Bot.API.getPrivateChannels());
		if (guild == Guilds.GLOBAL) r.addAll(Bot.API.getTextChannels());
		if (guild.getGuild() != null) r.addAll(guild.getGuild().getTextChannels());
		return r;
	}

	public static void broadcast(Guilds.Data guild, String message, MessageReceivedEvent event) {
		for (MessageChannel channel : getChannels(guild))
			try {
				channel.sendMessageAsync(message, null);
			} catch (Exception ignored) {
			}
		bool(event, true);
	}

	public static void listChannels(Guilds.Data guild, MessageReceivedEvent event) {
		String msgs = "***Canais:***";
		List<MessageChannel> l = Channels.getChannels(guild);
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
}
