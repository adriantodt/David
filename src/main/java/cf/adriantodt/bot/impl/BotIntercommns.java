/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [14/09/16 15:39]
 */

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Statistics;
import cf.adriantodt.bot.Utils;
import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.EventHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cf.adriantodt.bot.impl.BotIntercommns.Opcodes.*;

public class BotIntercommns {
	private static final Logger log = LogManager.getLogger("OldBotIntercommns");
	private static Map<User, BotInfo> info = new HashMap<>();
	private static BotInfo self = new BotInfo() {{
		p = 5; //David priority = 5
	}};

	private static BotInfo get(User bot) {
		if (!info.containsKey(bot)) {
			info.put(bot, new BotInfo());
		}

		return info.get(bot);
	}

	public static void updateCmds() {
		List<String> cmds = EventHandler.getCommands(DiscordGuild.GLOBAL).keySet().stream().collect(Collectors.toList());
		self.cmds.clear();
		self.cmds.addAll(cmds.stream().map(s -> "&" + s).collect(Collectors.toList()));
		self.cmds.addAll(cmds.stream().map(s -> "?" + s).collect(Collectors.toList()));
	}

	public static void onEvent(MessageReceivedEvent event) {
		if (!event.isPrivate() || !event.getAuthor().isBot() || Bot.API.getSelfInfo().equals(event.getAuthor())) {
			onSubEvent(event);
			return;
		}

		transaction(event.getAuthor(), event.getMessage().getRawContent());
	}

	private static void onSubEvent(MessageReceivedEvent event) {
		String base = Utils.splitArgs(event.getMessage().getRawContent(), 2)[0];
		List<User> bots = info.entrySet().stream().filter(entry -> entry.getValue().cmds.stream().anyMatch(base::equals)).map(Map.Entry::getKey).collect(Collectors.toList());
		if (bots.size() != 0 && bots.stream().filter(user -> user.getOnlineStatus() != OnlineStatus.OFFLINE).count() == 0) {
			bots = info.entrySet().stream().filter(entry -> entry.getValue().p <= self.p && entry.getKey().getOnlineStatus() != OnlineStatus.OFFLINE).map(Map.Entry::getKey).sorted((user1, user2) -> user1.toString().compareTo(user2.toString())).collect(Collectors.toList());
			if (bots.indexOf(Bot.SELF) == 0)
				event.getChannel().sendMessageAsync("*Nenhum dos Bots responsáveis por esse comando está online. Tente mais tarde.*", null);
		}
	}

	public static void transaction(User bot, String msg) {
		if (msg.length() < (IC_CALL.length() + 1) || !msg.startsWith(IC_CALL)) return;
		msg = msg.substring(IC_CALL.length());

		char opcode = msg.charAt(0);

		if (opcode == SUPPORTS_IC) {
			pm(bot, IC_CALL + SUPPORTED);
			return;
		}

		if (opcode == TRANSACTION) {
			if (msg.length() < 2) return;
			msg = msg.substring(1);
			opcode = msg.charAt(0);

			if (opcode == TRANSACTION_CHECK) {
				msg = msg.substring(2);
				int oldHash = Statistics.parseInt(msg, 0);
				if (oldHash != self.hashCode()) {
					pm(bot, IC_CALL + TRANSACTION + TRANSACTION_UPDATE);
				}
				return;
			}

			if (opcode == TRANSACTION_GET) {
				msg = msg.substring(2);

				if (msg.equals("p")) {
					pm(bot, IC_CALL + TRANSACTION + TRANSACTION_SET + "$p=" + self.p);
				}

				if (msg.equals("cmds")) {
					boolean first = true;
					JsonArray array = new JsonArray();
					String state = array.toString();

					for (int i = 0; i < self.cmds.size(); i++) {
						state = array.toString();
						array.add(new JsonPrimitive(self.cmds.get(i)));

						if (array.toString().length() > 1900) {
							pm(bot, IC_CALL + TRANSACTION + TRANSACTION_SET + "$cmds" + (first ? "=" : "+") + state);
							first = false;
							array = new JsonArray();
							array.add(new JsonPrimitive(self.cmds.get(i)));
						}
					}

					pm(bot, IC_CALL + TRANSACTION + TRANSACTION_SET + "$cmds" + (first ? "=" : "+") + state);
					return;
				}

				return;
			}

			if (opcode == TRANSACTION_SET) {
				if (msg.equals("p")) {
					get(bot).p = Statistics.parseInt(msg.substring(2), 0);
					return;
				}

				if (msg.equals("cmds")) {
					opcode = msg.charAt(1);
					msg = msg.substring(2);
					JsonArray array = new JsonParser().parse(msg).getAsJsonArray();

					if (opcode == '=') {
						get(bot).cmds.clear();
					}

					if (opcode == '-') {
						get(bot).cmds.removeIf(s -> StreamSupport.stream(array.spliterator(), false).anyMatch(j -> s.equals(j.getAsString()))); //I Know, it's lazy.
					}

					if (opcode == '=' || opcode == '+') {
						get(bot).cmds.addAll(StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toList()));
					}

					return;
				}
				return;
			}

			if (opcode == TRANSACTION_UPDATE) {
				pm(bot, IC_CALL + TRANSACTION + TRANSACTION_GET + "$p");
				pm(bot, IC_CALL + TRANSACTION + TRANSACTION_GET + "$cmds");
				return;
			}

			return;
		}

		if (msg.startsWith(SUPPORTED)) {
			if (Character.toLowerCase(msg.charAt(1)) == 'y') {
				pm(bot, IC_CALL + TRANSACTION + TRANSACTION_CHECK + TRANSACTION_VALUE + get(bot).hashCode());
			}
		}
	}

	public static void pm(User user, String content) {
		user.getPrivateChannel().sendMessageAsync(content, null);
	}

	public static void init() {
		updateCmds();
		info.put(Bot.SELF, self);
		Bot.API.getUsers().stream().filter(user -> user.isBot() && !user.equals(Bot.SELF) && user.getOnlineStatus() != OnlineStatus.OFFLINE).forEach(BotIntercommns::start);
	}

	public static void start(User bot) {
		pm(bot, IC_CALL + SUPPORTS_IC);
	}

	public static class BotInfo {
		public int p;
		public List<String> cmds = new ArrayList<>();

		@Override
		public int hashCode() {
			return (p + ";" + String.join(",", cmds.stream().sorted().toArray(String[]::new)) + ";").hashCode();
		}
	}

	public static class Opcodes {
		public static final String IC_CALL = "~~ic", SUPPORTED = "!y", NOT_SUPPORTED = "!n";
		public static final char SUPPORTS_IC = '?', TRANSACTION = ':';
		public static final char TRANSACTION_GET = 'g', TRANSACTION_SET = 's', TRANSACTION_CHECK = 'c', TRANSACTION_UPDATE = 'u', TRANSACTION_VALUE = '$';
	}

}
