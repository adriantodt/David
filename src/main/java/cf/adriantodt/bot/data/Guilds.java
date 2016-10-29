/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/10/16 15:40]
 */

package cf.adriantodt.bot.data;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.utils.Tasks;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rethinkdb.model.MapObject;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.*;

import static cf.adriantodt.bot.data.DataManager.*;

public class Guilds {
	public static final String[] DEFAULT_PREFIXES = {"&", "!"};
	public static final Data GLOBAL;
	private static List<Data> all = new ArrayList<>();
	private static Map<Guild, Data> guildMap = new HashMap<>();
	private static Map<Data, Integer> timeoutUntilDbRemoval = new HashMap<>();

	static { //FakeGuilds Impl
		GLOBAL = new Data();
		GLOBAL.id = "-1";
		GLOBAL.name = "GLOBAL";

		Tasks.startAsyncTask("GuildTimeoutCleanup", () -> {
			timeoutUntilDbRemoval.replaceAll((guild, integer) -> Math.min(integer - 1, 0));
			timeoutUntilDbRemoval.entrySet().stream().filter(entry -> entry.getValue() == 0).map(Map.Entry::getKey).forEach(data -> {
				r.table("commands").filter(r.row("gid").eq(data.id)).delete().runNoReply(conn);
				r.table("guilds").get(data.id).delete().runNoReply(conn);
				timeoutUntilDbRemoval.remove(data);
			});
		}, 60);
	}

	public static List<Data> all() {
		return Collections.unmodifiableList(all);
	}

	public static void loadAll() {
		h.query(r.table("guilds").getAll().run(conn)).list().getAsJsonArray().forEach(Guilds::unpack);
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
		guildMap.put(data.getGuild(), data);
		if (data.getGuild() == null) {
			timeoutUntilDbRemoval.put(data, 5);
		}
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
			Data data = new Data();
			guildMap.put(guild, data);
			data.id = guild.getId();
			data.name = toGuildName(guild.getName());

			MapObject m =
				r.hashMap("id", data.id)
					.with("name", data.name)
					.with("cmdPrefixes", r.array(data.cmdPrefixes.toArray()))
					.with("lang", data.lang)
					.with("userPerms", data.userPerms)
					.with("flags", data.flags);

			r.table("guilds").insert(m).runNoReply(conn);

			return data;
		}
	}

	public static Data fromId(String id) {
		for (Data g : all) {
			if (g.id.equals(id)) return g;
		}

		return null;
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
		return I18n.getLocalized("guild.guild", language) + ": " + data.name + (guild != null && !data.name.equals(guild.getName()) ? " (" + guild.getName() + ")" : "")
			+ "\n - " + I18n.getLocalized("guild.admin", language) + ": " + (guild == null ? Bot.API.getUserById(DataManager.configs.ownerID).getName() : guild.getOwner().getUser().getName())
			+ "\n - " + I18n.getLocalized("guild.cmds", language) + ": " + UserCommands.allFrom(data).size()
			+ "\n - " + I18n.getLocalized("guild.channels", language) + ": " + (guild == null ? (Bot.API.getTextChannels().size() + Bot.API.getPrivateChannels().size()) : guild.getTextChannels().size())
			+ "\n - " + I18n.getLocalized("guild.users", language) + ": " + (guild == null ? Bot.API.getUsers().size() : guild.getMembers().size())
			+ "\n - ID: " + data.id;
	}

	public static class Data {
		private Map<String, Long> userPerms = new HashMap<>();
		private Map<String, Boolean> flags = new HashMap<>();
		private String id = "-1", name = "", lang = "en_US";
		private List<String> cmdPrefixes = new ArrayList<>(Arrays.asList(DEFAULT_PREFIXES));

		private Data() {
			flags.put("cleanup", true);
			flags.put("vip", true);
			userPerms.put("default", Permissions.BASE_USER);
			all.add(this);
		}

		private static void pushUpdate(Data data, MapObject changes) {
			r.table("guilds").get(data.id).update(arg -> changes).runNoReply(conn);
		}

		public List<String> getCmdPrefixes() {
			return Collections.unmodifiableList(cmdPrefixes);
		}

		public Commitable<List<String>> modifyCmdPrefixes() {
			int oldHash = Arrays.hashCode(cmdPrefixes.toArray());
			return Commitable.bake(new ArrayList<>(cmdPrefixes), list -> {
				if (Arrays.hashCode(list.toArray()) != oldHash) {
					pushUpdate(this, r.hashMap("cmdPrefixes", list));
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
			pushUpdate(this, r.hashMap("userPerms", userPerms));
		}

		public boolean getFlag(String s) {
			return flags.getOrDefault(s, false);
		}

		public void setFlag(String s, Boolean flag) {
			this.flags.put(s, flag);
			pushUpdate(this, r.hashMap("flags", flags));
		}

		public boolean toggleFlag(String s) {
			boolean f = !getFlag(s);
			setFlag(s, f);
			return f;
		}

		public Guild getGuild(JDA jda) {
			return jda.getGuildById(id);
		}

		public Guild getGuild() {
			return getGuild(Bot.API);
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
			pushUpdate(this, r.hashMap("name", name));
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
			pushUpdate(this, r.hashMap("lang", lang));
		}
	}
}
