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
import cf.adriantodt.bot.base.cmd.CommandEvent;
import cf.adriantodt.bot.data.Guilds;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.List;

import static cf.adriantodt.bot.utils.Answers.bool;
import static cf.adriantodt.bot.utils.Answers.send;

public class Channels {
	public static void getPast(CommandEvent event, TextChannel channel) throws RateLimitedException {
		String msgs = "***Past:***";
		List<Message> msgl = channel.getHistory().retrievePast(10).block();
		for (int i = Math.min(10, msgl.size() - 1); i > 0; i--) {
			Message msg = msgl.get(i);
			msgs += "\n[" + getChannelName(event.getGuild(), channel) + "] <" + msg.getAuthor().getName() + "> " + msg.getContent();
		}
		send(event, msgs).queue();
	}

	public static String getChannelName(Guilds.Data guild, TextChannel channel) {
		if (channel != null) {
			return channel.getGuild().getName() + ":" + channel.getName() + "(#" + getChannels(guild).indexOf(channel) + ")";
		}

		return "Além";
	}

	public static List<TextChannel> getChannels(Guilds.Data guild) {
		List<TextChannel> r = new ArrayList<>();
		if (guild == Guilds.GLOBAL) r.addAll(Bot.API.getTextChannels());
		else r.addAll(guild.getGuild().getTextChannels());
		return r;
	}

	public static void broadcast(Guilds.Data guild, String message, CommandEvent event) {
		for (TextChannel channel : getChannels(guild))
			try {
				channel.sendMessage(message).queue();
			} catch (Exception ignored) {
			}
		bool(event, true).queue();
	}

	public static void listChannels(Guilds.Data guild, CommandEvent event) {
		String msgs = "***Canais:***";
		List<TextChannel> l = Channels.getChannels(guild);
		for (int i = 0; i < l.size(); i++) {
			TextChannel channel = l.get(i);
			msgs += "\n[" + i + "] = " + channel.getGuild().getName() + ":" + channel.getName();
		}
		send(event, msgs).queue();
	}
}
