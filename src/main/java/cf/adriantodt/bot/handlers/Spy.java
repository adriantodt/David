/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:08]
 */

package cf.adriantodt.bot.handlers;

import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.utils.Channels;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cf.adriantodt.bot.utils.Answers.bool;
import static cf.adriantodt.bot.utils.Channels.getChannels;

public class Spy {
	public static final Map<SpyType, List<MessageChannel>> channels = new HashMap<>();

	static {
		channels.put(SpyType.NODES, new ArrayList<>());
		channels.put(SpyType.IGNORE, new ArrayList<>());
		channels.put(SpyType.LOG, new ArrayList<>());
	}

	public static List<MessageChannel> get(SpyType type) {
		return channels.get(type);
	}

	public static List<MessageChannel> nodes() {
		return get(SpyType.NODES);
	}

	public static List<MessageChannel> logs() {
		return get(SpyType.NODES);
	}

	public static List<MessageChannel> ignored() {
		return get(SpyType.NODES);
	}

	public static void toNodes(String message) {
		nodes().stream().filter(channel -> channel != null).forEach(channel -> channel.sendMessage(message).queue());
	}

	public static void trigger(GuildMessageReceivedEvent event, SpyType type) {
		if (get(type).contains(event.getChannel())) get(type).remove(event.getChannel());
		else get(type).add(event.getChannel());
		bool(event, get(type).contains(event.getChannel())).queue();
	}

	public static void kickSelf(Guilds.Data guild, GuildMessageReceivedEvent event, int channelId) {
		MessageChannel channel = getChannels(guild).get(channelId);

		if (channel instanceof TextChannel) {
			//TODO WAIT DV8 ADD LEAVE
			//((TextChannel) channel).getGuild().getManager().leave();
		}
		bool(event, channel instanceof TextChannel).queue();
	}

	@SubscribeEvent
	public static void onGuildLeave(GuildLeaveEvent event) {
		toNodes("[!] Leaved " + event.getGuild().getName() + "!");
	}

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent event) {
		if (nodes().contains(event.getChannel()) || ignored().contains(event.getChannel())) return;
		toNodes("[" + Channels.getChannelName(Guilds.fromDiscord(event), event.getChannel()) + "] <" + event.getAuthor().getName() + "> " + event.getMessage().getContent());
	}

	@SubscribeEvent
	public static void onGuildJoin(GuildJoinEvent event) {
		toNodes("[!] Joined " + event.getGuild().getName() + "!");
	}

	public enum SpyType {
		NODES, IGNORE, LOG
	}
}
