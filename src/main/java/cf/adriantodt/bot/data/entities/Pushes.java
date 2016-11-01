/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:35]
 */

package cf.adriantodt.bot.data.entities;

import cf.adriantodt.bot.Bot;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cf.adriantodt.bot.data.DataManager.*;

public class Pushes {
	private static Map<String, String> pushParenting = new HashMap<>();
	private static Map<Supplier<Set<String>>, String> dynamicParenting = new HashMap<>();
	private static Multimap<TextChannel, String> subscriptions = MultimapBuilder.hashKeys().hashSetValues().build();

	static {
		pushParenting.put("*", null);
		pushParenting.put("bot", "*");
		pushParenting.put("stop", "bot");
		pushParenting.put("start", "bot");
		pushParenting.put("update", "*");
		pushParenting.put("changelog", "update");
		pushParenting.put("get", "*");
		pushParenting.put("ownerID", "*");
		pushParenting.put("guild", "*");
		pushParenting.put("i18n", "*");
		pushParenting.put("feeds", "*");

		dynamicParenting.put(() -> Bot.API.getGuilds().stream().map(guild -> "guild_" + Guilds.fromDiscord(guild).getName()).collect(Collectors.toSet()), "guild");

		h.from(r.table("pushSubs").run(conn)).cursorExpected().forEach(json -> {
			JsonObject subscription = json.getAsJsonObject();
			TextChannel channel = Bot.API.getTextChannelById(subscription.get("id").getAsString());

			if (channel == null) {
				r.table("pushSubs").get(subscription.get("id").getAsString()).delete().runNoReply(conn);
				return;
			}

			subscriptions.putAll(channel, StreamSupport.stream(subscription.get("types").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toSet()));
		});
	}

	public static boolean subscribe(TextChannel channel, Set<String> typesToAdd) {
		typesToAdd = new HashSet<>(typesToAdd);
		Set<String> valid = resolveTypeSet();
		typesToAdd.removeIf(s -> !valid.contains(s));
		typesToAdd.remove(null);

		if (typesToAdd.size() == 0) return false;

		if (subscriptions.containsKey(channel)) {
			Set<String> currentSubs = (Set<String>) subscriptions.get(channel);
			int size = currentSubs.size();
			currentSubs.addAll(typesToAdd);

			if (currentSubs.size() == size) return false;

			r.table("pushSubs").get(channel.getId()).update(arg -> r.hashMap("types", new ArrayList<>(currentSubs))).runNoReply(conn);
		} else {
			r.table("pushSubs").insert(r.hashMap("id", channel.getId()).with("types", new ArrayList<>(typesToAdd))).runNoReply(conn);
			subscriptions.putAll(channel, typesToAdd);
		}
		return true;
	}

	public static boolean unsubscribe(TextChannel channel, Set<String> typesToRemove) {
		if (!subscriptions.containsKey(channel)) return false;

		Set<String> currentSubs = (Set<String>) subscriptions.get(channel);
		int size = currentSubs.size();
		currentSubs.removeAll(typesToRemove);

		if (currentSubs.size() == size) return false;

		if (currentSubs.size() > 0) {
			r.table("pushSubs").get(channel.getId()).update(arg -> r.hashMap("types", new ArrayList<>(currentSubs))).runNoReply(conn);
		} else {
			r.table("pushSubs").get(channel.getId()).delete().runNoReply(conn);
			subscriptions.removeAll(channel);
		}

		return true;
	}

	public static void registerType(String type, String parent) {
		pushParenting.put(type.toLowerCase(), parent.toLowerCase());
	}

	public static void registerDynamicTypes(Supplier<Set<String>> supplier, String parent) {
		dynamicParenting.put(supplier, parent.toLowerCase());
	}

	public static Set<String> subscriptionsFor(TextChannel channel) {
		return Collections.unmodifiableSet((Set<String>) subscriptions.get(channel));
	}

	public static void pushMessage(String type, Function<TextChannel, Message> pushSupplier) {
		resolveTextChannels(type).forEach(channel -> channel.sendMessage(pushSupplier.apply(channel)).queue());
	}

	public static void pushSimple(String type, Function<TextChannel, String> pushSupplier) {
		pushMessage(type, channel -> new MessageBuilder().appendString(pushSupplier.apply(channel)).build());
	}

	public static void pushSimple(String type, String pushMessage) {
		pushMessage(type, channel -> new MessageBuilder().appendString(pushMessage).build());
	}

	public static Set<TextChannel> resolveTextChannels(String type) {
		Set<String> appliable = resolve(type);
		return subscriptions.asMap().entrySet().stream()
			.filter(entry -> entry.getValue().stream().anyMatch(appliable::contains))
			.map(Map.Entry::getKey)
			.collect(Collectors.toSet());
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
