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
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BotIntercommns {
	private static Map<User, List<Character>> bots = new HashMap<>();

	public static void onEvent(MessageReceivedEvent event) {
		if (!event.isPrivate() || !event.getAuthor().isBot()) onSubEvent(event);
		String msg = event.getMessage().getRawContent();
		if (msg.charAt(0) != '§' || msg.charAt(1) != 'B' || msg.charAt(2) != 'I' || msg.charAt(3) != 'C') return;
		msg = msg.substring(4);

		if (!bots.containsKey(event.getAuthor())) {
			bots.put(event.getAuthor(), new ArrayList<>());
			event.getPrivateChannel().sendMessage("§BIC['&','?']".replace('\'', '"'));
		}

		List<Character> p = bots.get(event.getAuthor());
		p.clear();
		JsonArray arr = new JsonParser().parse(msg).getAsJsonArray();
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).getAsString() != null && !arr.get(i).getAsString().isEmpty())
				p.add(arr.get(i).getAsString().charAt(0));
		}
	}

	private static void onSubEvent(MessageReceivedEvent event) {
		char c = event.getMessage().getRawContent().charAt(0);
		List<User> botss = bots.entrySet().stream().filter(entry -> entry.getValue().stream().anyMatch(character -> character == c)).map(Map.Entry::getKey).collect(Collectors.toList());
		if (!Character.isLetterOrDigit(c) && botss.size() != 0 && botss.stream().filter(user -> user.getOnlineStatus() != OnlineStatus.OFFLINE).count() == 0) {
			event.getChannel().sendMessageAsync("*Nenhum dos Bots responsáveis por esse comando está online. Tente mais tarde.*", null);
		}
	}

	public static void batchDoCommn() {
		Bot.API.getUsers().stream().filter(User::isBot).filter(user -> !Bot.SELF.equals(user)).forEach(user -> user.getPrivateChannel().sendMessageAsync("§BIC['&','?']".replace('\'', '"'), null));
	}
}
