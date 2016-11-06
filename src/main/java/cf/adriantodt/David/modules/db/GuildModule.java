/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:29]
 */

package cf.adriantodt.David.modules.db;

import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.JDAInstance;
import cf.adriantodt.David.loader.Module.OnEnabled;
import cf.adriantodt.David.loader.Module.SubscribeJDA;
import cf.adriantodt.utils.TaskManager;
import cf.adriantodt.utils.data.Commitable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;
import java.util.function.Function;

import static cf.adriantodt.David.loader.Module.Type.STATIC;

@Module(STATIC)
@SubscribeJDA
public class GuildModule {
	public static final String[] DEFAULT_PREFIXES = {"&", "!"};
	public static Data GLOBAL;
	@JDAInstance
	private static JDA jda = null;
	private static List<Data> all = new ArrayList<>();
	private static Map<Guild, Data> guildMap = new HashMap<Guild, Data>() {
		@Override
		public Data put(Guild key, Data value) {
			if (key == null) return null;
			return super.put(key, value);
		}

		@Override
		public void putAll(Map<? extends Guild, ? extends Data> m) {
			m.remove(null);
			super.putAll(m);
		}

		@Override
		public Data putIfAbsent(Guild key, Data value) {
			if (key == null) return null;
			return super.putIfAbsent(key, value);
		}
	};
	private static Map<Data, Integer> timeoutUntilDbRemoval = new HashMap<>();

	@OnEnabled
	private static void load() { //FakeGuilds Impl
		GLOBAL = new Data();
		GLOBAL.id = "-1";
		GLOBAL.name = "GLOBAL";

		TaskManager.startAsyncTask("GuildTimeoutCleanup", () -> {
			timeoutUntilDbRemoval.replaceAll((guild, integer) -> Math.min(integer - 1, 0));
			timeoutUntilDbRemoval.entrySet().stream().filter(entry -> entry.getValue() == 0).map(Map.Entry::getKey).forEach(data -> {
				DBModule.onDB(r -> r.table("commands").filter(r.row("gid").eq(data.id)).delete()).noReply();
				DBModule.onDB(r -> r.table("guilds").get(data.id).delete()).noReply();
				timeoutUntilDbRemoval.remove(data);
			});
		}, 60);
	}

	public static List<Data> all() {
		return Collections.unmodifiableList(all);
	}

	private static Data unpack(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		Data data = all.stream().filter(dataPredicate -> object.get("id").getAsString().equals(dataPredicate.id)).findFirst().orElseGet(Data::new);
		data.id = object.get("id").getAsString();
		data.name = object.get("name").getAsString();
		data.cmdPrefixes.clear();
		object.get("cmdPrefixes").getAsJsonArray().forEach(jsonElement -> data.cmdPrefixes.add(jsonElement.getAsString()));
		data.lang = object.get("lang").getAsString();
		data.flags.clear();
		object.get("flags").getAsJsonObject().entrySet().forEach(entry -> data.flags.put(entry.getKey(), entry.getValue().getAsBoolean()));
		object.get("userPerms").getAsJsonObject().entrySet().forEach(entry -> data.userPerms.put(entry.getKey(), entry.getValue().getAsLong()));
		return data;
	}

	@SubscribeEvent
	public static void newGuild(GuildJoinEvent e) {
		Data data = fromDiscord(e.getGuild());
		if (timeoutUntilDbRemoval.containsKey(data)) timeoutUntilDbRemoval.remove(data);
	}

	@SubscribeEvent
	public static void byeGuild(GuildLeaveEvent e) {
		timeoutUntilDbRemoval.put(fromDiscord(e.getGuild()), 5);
	}

	@SubscribeEvent
	public static void renamedGuild(GuildUpdateNameEvent e) {
		fromDiscord(e.getGuild()).setName(toGuildName(e.getGuild().getName()));
	}

	public static Data fromDiscord(Guild guild) {
		if (guild == null) return GLOBAL;
		if (guildMap.containsKey(guild)) {
			return guildMap.get(guild);
		} else {
			return getOrGen(Optional.of(guild), Optional.empty(), Optional.empty());
		}
	}

	private static Data getOrGen(Optional<Guild> guildOptional, Optional<String> optionalId, Optional<String> optionalName) {
		RuntimeException ex = new IllegalStateException("Id and/or Name can't be Optional if Guild isn't returned");
		String id = optionalId.orElseGet(() -> guildOptional.orElseThrow(() -> ex).getId());
		String name = optionalName.orElseGet(() -> guildOptional.orElseThrow(() -> ex).getName());

		Data data;

		JsonElement object = DBModule.onDB(r -> r.db("bot").table("guilds").get(id)).run().simpleExpected();
		if (object.isJsonNull()) {
			data = new Data();

			data.id = id;
			data.name = toGuildName(name);

			MapObject m =
				new MapObject()
					.with("id", data.id)
					.with("name", data.name)
					.with("cmdPrefixes", data.cmdPrefixes)
					.with("lang", data.lang)
					.with("userPerms", data.userPerms)
					.with("flags", data.flags);

			DBModule.onDB(r -> r.table("guilds").insert(m)).noReply();
		} else {
			data = unpack(object);
		}
		Data finalData = data;
		guildOptional.ifPresent(guild -> guildMap.put(guild, finalData));
		UserCommandsModule.loadAllFrom(data);
		return data;
	}

	public static Data fromName(String name) {
		for (Data g : all) {
			if (g.name.equals(name)) return g;
		}
		return null;
	}

	private static String toGuildName(String name) {
		name = name.replace(" ", "_").replace(":", "");
		if (fromName(name) == null) return name;
		for (int i = 2; i < 1000; i++) {
			if (fromName(name + i) == null) return name + i;
		}
		throw new RuntimeException("What. the. fuck.");
	}

	public static String toString(Data data, JDA jda, String language) {
		Guild guild = data.getGuild(jda);
		return I18nModule.getLocalized("guild.guild", language) + ": " + data.name + (data.getFlag("vip") ? " [VIP]" : "") + (guild != null && !data.name.equals(guild.getName()) ? " (" + guild.getName() + ")" : "")
			+ "\n - " + I18nModule.getLocalized("guild.admin", language) + ": " + (guild == null ? jda.getUserById(DBModule.getConfig().get("ownerID").getAsString()).getName() : guild.getOwner().getUser().getName())
			+ "\n - " + I18nModule.getLocalized("guild.cmds", language) + ": " + UserCommandsModule.allFrom(data).size()
			+ "\n - " + I18nModule.getLocalized("guild.channels", language) + ": " + (guild == null ? (jda.getTextChannels().size() + jda.getPrivateChannels().size()) : guild.getTextChannels().size())
			+ "\n - " + I18nModule.getLocalized("guild.users", language) + ": " + (guild == null ? jda.getUsers().size() : guild.getMembers().size())
			+ "\n - " + I18nModule.getLocalized("guild.id", language) + ": " + data.id;
	}

	public static class Data {
		private Map<String, Long> userPerms = new HashMap<>();
		private Map<String, Boolean> flags = new HashMap<>();
		private String id = "-1", name = "", lang = "en_US";
		private List<String> cmdPrefixes = new ArrayList<>(Arrays.asList(DEFAULT_PREFIXES));

		private Data() {
			flags.put("cleanup", true);
			flags.put("vip", true);
			userPerms.put("default", MakePermissionsAModule.BASE_USER);
			all.add(this);
		}

		private static void pushUpdate(Data data, Function<RethinkDB, MapObject> changes) {
			DBModule.onDB(r -> r.table("guilds").get(data.id).update(arg -> changes.apply(r))).noReply();
		}

		public List<String> getCmdPrefixes() {
			return Collections.unmodifiableList(cmdPrefixes);
		}

		public Commitable<List<String>> modifyCmdPrefixes() {
			int oldHash = Arrays.hashCode(cmdPrefixes.toArray());
			return Commitable.bake(new ArrayList<>(cmdPrefixes), list -> {
				if (Arrays.hashCode(list.toArray()) != oldHash) {
					pushUpdate(this, r -> r.hashMap("cmdPrefixes", list));
					cmdPrefixes.clear();
					cmdPrefixes.addAll(list);
				}
			});
		}

		public long getUserPerms(String s) {
			return getUserPerms(s, 0L);
		}

		public long getUserPerms(String s, long orDefault) {
			return userPerms.getOrDefault(s, orDefault);
		}

		public void setUserPerms(String s, long userPerms) {
			this.userPerms.put(s, userPerms);
			pushUpdate(this, r -> r.hashMap("userPerms", this.userPerms));
		}

		public boolean getFlag(String s) {
			return flags.getOrDefault(s, false);
		}

		public void setFlag(String s, Boolean flag) {
			this.flags.put(s, flag);
			pushUpdate(this, r -> r.hashMap("flags", flags));
		}

		public boolean toggleFlag(String s) {
			boolean f = !getFlag(s);
			setFlag(s, f);
			return f;
		}

		public Guild getGuild(JDA jda) {
			return jda.getGuildById(id);
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
			pushUpdate(this, r -> r.hashMap("name", name));
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
			pushUpdate(this, r -> r.hashMap("lang", lang));
		}
	}
}