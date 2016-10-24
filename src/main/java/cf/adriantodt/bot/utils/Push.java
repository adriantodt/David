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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Push {
	private static Map<String, String> pushParenting = new HashMap<>();
	private static Map<Supplier<List<String>>, String> dynamicParenting = new HashMap<>();
	private static Map<String, List<TextChannel>> subscripted = new HashMap<>();

	static {
		pushParenting.put("*", null);

		pushParenting.put("bot", "*");
		pushParenting.put("stop", "bot");
		pushParenting.put("restart", "bot");
		pushParenting.put("save", "bot");
		pushParenting.put("load", "bot");

		pushParenting.put("update", "*");
		pushParenting.put("changelog", "update");

		pushParenting.put("log", "*");

		pushParenting.put("ownerID", "*");

		pushParenting.put("guild", "*");
		dynamicParenting.put(() -> Bot.API.getGuilds().stream().map(guild -> "guild_" + Guilds.fromDiscord(guild).getName()).collect(Collectors.toList()), "guild");
	}

	public static void subscribe(TextChannel channel, List<String> types) {
		Set<String> valid = resolveTypeSet();
		types.removeIf(s -> !valid.contains(s));
		Set<String> typeSet = new HashSet<>(types);
		for (String type : typeSet) {
			if (!subscripted.containsKey(type)) subscripted.put(type, new ArrayList<>());
			List<TextChannel> subscriptedToType = subscripted.get(type);
			if (!subscriptedToType.contains(channel)) subscriptedToType.add(channel);
		}
	}

	public static void unsubscribe(TextChannel channel, List<String> types) {
		Set<String> valid = resolveTypeSet();
		types.removeIf(s -> !valid.contains(s));
		Set<String> typeSet = new HashSet<>(types);
		for (String type : typeSet) {
			if (subscripted.containsKey(type)) {
				List<TextChannel> subscriptedToType = subscripted.get(type);
				if (subscriptedToType.contains(channel)) subscriptedToType.remove(channel);
				if (subscriptedToType.size() == 0) subscripted.remove(type);
			}
		}
	}

	public static void registerType(String type, String parent) {
		pushParenting.put(type.toLowerCase(), parent.toLowerCase());
	}

	public static void registerDynamicTypes(Supplier<List<String>> supplier, String parent) {
		dynamicParenting.put(supplier, parent.toLowerCase());
	}

	public static void push(String type, Function<TextChannel, Message> pushSupplier) {
		Set<String> appliable = resolve(type);
		Set<TextChannel> channels = new HashSet<>();
		appliable.stream().filter(s -> subscripted.containsKey(s)).forEach(s -> channels.addAll(subscripted.get(s)));
		channels.forEach(channel -> channel.sendMessage(pushSupplier.apply(channel)).queue());
	}

	public static Map<String, String> resolveTypeMap() {
		Map<String, String> resolvedMap = new HashMap<>(pushParenting);
		dynamicParenting.forEach((supplier, s) -> supplier.get().forEach(s1 -> resolvedMap.put(s1, s)));
		return resolvedMap;
	}

	public static Set<String> resolveTypeSet() {
		Map<String, String> resolvedMap = resolveTypeMap();
		Set<String> set = new HashSet<>();
		set.addAll(resolvedMap.values());
		set.addAll(resolvedMap.keySet());
		set.remove(null);
		return set;
	}


	public static Set<String> resolve(String type) {
		Map<String, String> resolvedMap = resolveTypeMap();

		Set<String> r = new HashSet<>();

		do {
			r.add(type);
			type = resolvedMap.getOrDefault(type, "*");
			if (type == null) type = "*";
		} while (!type.equals("*"));

		return r;
	}
}
