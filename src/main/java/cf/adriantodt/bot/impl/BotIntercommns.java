/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [10/09/16 22:56]
 */

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Utils;
import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.EventHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cf.adriantodt.bot.Bot.JSON_INTERNAL;

public class BotIntercommns {
	private static final Logger log = LogManager.getLogger("BotIntercommns");
	private static Map<User, List<String>> bots = new HashMap<>();
	private static String cached = calcString();

	public static void onEvent(MessageReceivedEvent event) {
		if (!event.isPrivate() || !event.getAuthor().isBot() || Bot.API.getSelfInfo().equals(event.getAuthor())) {
			onSubEvent(event);
			return;
		}

		String msg = event.getMessage().getRawContent();
		if (msg.charAt(0) != '§' || msg.charAt(1) != 'B' || msg.charAt(2) != 'I' || msg.charAt(3) != 'C') return;
		msg = msg.substring(4);

		if (!bots.containsKey(event.getAuthor())) {
			bots.put(event.getAuthor(), new ArrayList<>());
			pmBot(event.getAuthor());
		}

		List<String> p = bots.get(event.getAuthor());
		p.clear();
		JsonArray arr = new JsonParser().parse(msg).getAsJsonArray();
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getAsString() != null && !arr.get(i).getAsString().isEmpty())
				p.add(arr.get(i).getAsString());
		}
		log.info("Bot \"" + event.getAuthor().getUsername() + "\" answered: " + Arrays.toString(p.toArray()));
	}

	private static void onSubEvent(MessageReceivedEvent event) {
		String base = Utils.splitArgs(event.getMessage().getRawContent(), 2)[0];
		List<User> botss = bots.entrySet().stream().filter(entry -> entry.getValue().stream().anyMatch(base::equals)).map(Map.Entry::getKey).collect(Collectors.toList());
		if (botss.size() != 0 && botss.stream().filter(user -> user.getOnlineStatus() != OnlineStatus.OFFLINE).count() == 0) {
			event.getChannel().sendMessageAsync("*Nenhum dos Bots responsáveis por esse comando está online. Tente mais tarde.*", null);
		}
	}

	private static String calcString() {
		return "§BIC" + JSON_INTERNAL.toJson(Stream.concat(EventHandler.getCommands(DiscordGuild.GLOBAL).keySet().stream().map(s -> "&" + s), EventHandler.getCommands(DiscordGuild.GLOBAL).keySet().stream().map(s -> "?" + s)).toArray());
	}

	public static void redoCache() {
		cached = calcString();
	}

	public static void pmBot(User user) {
		user.getPrivateChannel().sendMessageAsync(cached, null);
	}

	public static void batchDoCommn() {
		Bot.API.getUsers().stream().filter(user -> user.isBot() && !Bot.API.getSelfInfo().equals(user) && user.getOnlineStatus() != OnlineStatus.OFFLINE).forEach(BotIntercommns::pmBot);
	}
}
