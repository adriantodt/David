/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:07]
 */

package cf.adriantodt.bot.handlers;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.utils.Commands;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cf.adriantodt.bot.handlers.BotIntercommns.Opcodes.*;

public class BotIntercommns {
	public static Map<String, BotInfo> info = new HashMap<>();

	public static BotInfo self = new BotInfo() {{
		p = 5; //David priority = 5
	}};

	private static BotInfo get(User bot) {
		if (!info.containsKey(bot.getId())) {
			info.put(bot.getId(), new BotInfo());
		}

		return info.get(bot.getId());
	}

	public static void updateCmds() {
		List<String> cmds = Commands.getBaseCommands().keySet().stream().collect(Collectors.toList());
		self.cmds.clear();
		self.cmds.addAll(cmds.stream().map(s -> "&" + s).collect(Collectors.toList()));
		self.cmds.addAll(cmds.stream().map(s -> "?" + s).collect(Collectors.toList()));
	}

	private static void pm(User user, String content) {
		user.getPrivateChannel().sendMessageAsync(content, null);
	}

	public static void start(User bot) {
		pm(bot, IC_CALL + SUPPORTS_IC);
	}

	@SubscribeEvent
	public static void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		if (event.getUser().isBot()) Utils.asyncSleepThen(2000, () -> BotIntercommns.start(event.getUser())).run();
	}

	@SubscribeEvent
	public static void onMessageReceived(MessageReceivedEvent event) {
		if (event.getChannelType() != ChannelType.PRIVATE || !event.getAuthor().isBot() || Bot.API.getSelfInfo().equals(event.getAuthor())) {
			//onSubEvent(event);
			String base = Utils.splitArgs(event.getMessage().getRawContent(), 2)[0];
			List<User> bots = info.entrySet().stream().filter(entry -> entry.getValue().cmds.stream().anyMatch(base::equals)).map((entry) -> Bot.API.getUserById(entry.getKey())).collect(Collectors.toList());
			if (bots.size() != 0 && bots.stream().filter(user -> user.getOnlineStatus() != OnlineStatus.OFFLINE).count() == 0) {
				bots = info.entrySet().stream().filter(entry -> entry.getValue().p <= self.p && Bot.API.getUserById(entry.getKey()).getOnlineStatus() != OnlineStatus.OFFLINE).map((entry) -> Bot.API.getUserById(entry.getKey())).sorted((user1, user2) -> user1.toString().compareTo(user2.toString())).collect(Collectors.toList());
				if (bots.indexOf(Bot.SELF) == 0)
					event.getChannel().sendMessage("*Nenhum dos Bots responsáveis por esse comando está online. Tente mais tarde.*").queue();
			}

			return;
		}

		transaction(event.getAuthor(), event.getMessage().getRawContent());
	}

	private static void transaction(User bot, String msg) {
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
				if (oldHash != self.toString().hashCode()) {
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
				msg = msg.substring(2);
				if (msg.startsWith("p")) {
					get(bot).p = Statistics.parseInt(msg.substring(2), 0);
					return;
				}

				if (msg.startsWith("cmds")) {
					msg = msg.substring("cmds".length());
					opcode = msg.charAt(0);
					msg = msg.substring(1);
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
				pm(bot, IC_CALL + TRANSACTION + TRANSACTION_CHECK + TRANSACTION_VALUE + get(bot).toString().hashCode());
			}
		}
	}

	@SubscribeEvent
	public static void onReady(ReadyEvent event) {
		updateCmds();
		info.put(event.getJDA().getSelfInfo().getId(), self);
		event.getJDA().getUsers().stream().filter(user -> user.isBot() && !user.equals(event.getJDA().getSelfInfo()) && user.getOnlineStatus() != OnlineStatus.OFFLINE).forEach(BotIntercommns::start);
	}

	public static class BotInfo {
		public int p;
		public List<String> cmds = new ArrayList<>();

		@Override
		public String toString() {
			return (p + ";" + String.join(",", cmds.stream().sorted().toArray(String[]::new)) + ";");
		}
	}

	public static class Opcodes {
		public static final String IC_CALL = "~~ic", SUPPORTED = "!y", NOT_SUPPORTED = "!n";
		public static final char SUPPORTS_IC = '?', TRANSACTION = ':';
		public static final char TRANSACTION_GET = 'g', TRANSACTION_SET = 's', TRANSACTION_CHECK = 'c', TRANSACTION_UPDATE = 'u', TRANSACTION_VALUE = '$';
	}

}
