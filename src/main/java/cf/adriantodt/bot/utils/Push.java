/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/10/16 07:27]
 */

package cf.adriantodt.bot.utils;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.data.Guilds;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Push {
	private static Map<String, String> pushParenting = new HashMap<>(), pushAliases = new HashMap<>();
	private static Map<Supplier<List<String>>, String> dynamicParenting = new HashMap<>();
	private static List<PushSubscription> subscriptions = new ArrayList<>();

	static {
		registerType("*", null);

		registerType("bot", "*");
		registerType("stop", "bot");
		registerType("restart", "bot");
		registerType("save", "bot");
		registerType("load", "bot");

		registerType("update", "*");
		registerType("changelog", "update");

		registerType("ownerID", "*");

		registerType("guild", "*");
		registerDynamicTypes(() -> Bot.API.getGuilds().stream().map(guild -> "guild_" + Guilds.fromDiscord(guild).getName()).collect(Collectors.toList()), "guild");
	}

	public static void registerType(String type, String parent) {
		pushParenting.put(type.toLowerCase(), parent.toLowerCase());
	}

	public static void registerDynamicTypes(Supplier<List<String>> supplier, String parent) {
		dynamicParenting.put(supplier, parent.toLowerCase());
	}

	public static void push(String type, Supplier<String> pushSupplier) {

	}

	public static List<String> resolve(String type) {
		Map<String, String> resolvedMap = new HashMap<>(pushParenting);
		dynamicParenting.forEach((supplier, s) -> supplier.get().forEach(s1 -> resolvedMap.put(s1, s)));

		List<String> r = new ArrayList<>();

		do {
			r.add(type);
			type = type.equals("*") ? resolvedMap.getOrDefault(type, "*") : "*";
		} while (!type.equals("*"));

		return r;
	}

	public static class PushSubscription {
		public List<String> subscriptions = new ArrayList<>(), cached = new ArrayList<>();
		public MessageChannel sendTo;
	}
}
