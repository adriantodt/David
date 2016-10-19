/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [08/10/16 11:26]
 */

package cf.adriantodt.bot.data;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.utils.Tasks;
import com.google.gson.JsonElement;
import com.rethinkdb.model.MapObject;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

import static cf.adriantodt.bot.data.DataManager.conn;
import static cf.adriantodt.bot.data.DataManager.r;

public class Users {
	private static List<Data> all = new ArrayList<>();
	private static Map<User, Data> userMap = new HashMap<>();
	private static Map<Data, Integer> timeoutUntilDbRemoval = new HashMap<>();

	static {

		Tasks.startAsyncTask(() -> {
			timeoutUntilDbRemoval.replaceAll((guild, integer) -> Math.min(integer - 1, 0));
			timeoutUntilDbRemoval.entrySet().stream().filter(entry -> entry.getValue() == 0).map(Map.Entry::getKey).forEach(data -> {
				//TODO IMPL DB REMOVAL
				timeoutUntilDbRemoval.remove(data);
			});
		}, 60);
	}

	public static List<Data> all() {
		return Collections.unmodifiableList(all);
	}

	public static void loadAll() {
		//TODO IMPLEMENT
		//h.query(r.table("guilds").getAll().run(conn)).list().getAsJsonArray().forEach(Guilds::unpack);
	}

	private static Data unpack(JsonElement element) {
		//TODO IMPLEMENT
//		JsonObject object = element.getAsJsonObject();
//		Data data = all.stream().filter(dataPredicate -> object.get("id").getAsString().equals(dataPredicate.id)).findFirst().orElseGet(Data::new);
//		data.id = object.get("id").getAsString();
//		data.name = object.get("name").getAsString();
//		data.cmdPrefixes.clear();
//		object.get("cmdPrefixes").getAsJsonArray().forEach(jsonElement -> data.cmdPrefixes.add(jsonElement.getAsString()));
//		data.lang = object.get("lang").getAsString();
//		data.flags.clear();
//		object.get("flags").getAsJsonObject().entrySet().forEach(entry -> data.flags.put(entry.getKey(), entry.getValue().getAsBoolean()));
//		object.get("userPerms").getAsJsonObject().entrySet().forEach(entry -> data.userPerms.put(entry.getKey(), entry.getValue().getAsLong()));
//		guildMap.put(data.getGuild(), data);
		return data;
	}

	//TODO FIND REPLACEMENTS
//	@SubscribeEvent
//	public static void newUser(EVENTHERE e) {
//		Data data = fromDiscord(e.getAuthor());
//		if (timeoutUntilDbRemoval.containsKey(data)) timeoutUntilDbRemoval.remove(data);
//	}
//
//	@SubscribeEvent
//	public static void byeUer(EVENTHERE e) {
//		timeoutUntilDbRemoval.put(fromDiscord(e.getAuthor()), 5);
//	}

	public static Data fromDiscord(User user) {
		if (userMap.containsKey(user)) {
			return userMap.get(user);
		} else {
			//TODO IMPLEMENT ADD
//			Data data = new Data();
//			userMap.put(user, data);
//			data.id = user.getId();
//
//			MapObject m =
//				r.hashMap("id", data.id)
//					.with("name", data.name)
//					.with("cmdPrefixes", r.array(data.cmdPrefixes.toArray()))
//					.with("lang", data.lang)
//					.with("userPerms", data.userPerms)
//					.with("flags", data.flags);
//
//			r.table("users").insert(m).runNoReply(conn);
//
//			return data;
		}
	}

	public static Data fromId(String id) {
		for (Data g : all) {
			if (g.id.equals(id)) return g;
		}

		return null;
	}

	public static Data fromDiscord(MessageReceivedEvent event) {
		return fromDiscord(event.getAuthor());
	}

//	public static String toString(Data data, JDA jda, String language) {
//		user user = data.getuser(jda);
//		return I18n.getLocalized("user.user", language) + ": " + data.name + (user != null && !data.name.equals(user.getName()) ? " (" + user.getName() + ")" : "")
//			+ "\n - " + I18n.getLocalized("user.admin", language) + ": " + (user == null ? Bot.API.getUserById(DataManager.configs.ownerID).getName() : user.getOwner().getName())
//			//+ "\n - " + I18n.getLocalized("user.cmds", language) + ": " + commands.size()
//			+ "\n - " + I18n.getLocalized("user.channels", language) + ": " + (user == null ? (data == PM ? Bot.API.getPrivateChannels().size() : Bot.API.getTextChannels().size() + Bot.API.getPrivateChannels().size()) : user.getTextChannels().size())
//			+ "\n - " + I18n.getLocalized("user.users", language) + ": " + (user == null ? (data == PM ? Bot.API.getPrivateChannels().size() : Bot.API.getUsers().size()) : user.getUsers().size())
//			+ "\n - ID: " + data.id;
//	}

	public static class Data {
		private String id = "-1", lang = "en_US";

		private static void pushUpdate(Users.Data data, MapObject changes) {
			r.table("users").get(data.id).update(arg -> changes).runNoReply(conn);
		}

		public String getId() {
			return id;
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
			pushUpdate(this, r.hashMap("lang", lang));
		}

		public long getUserPerms(Guilds.Data data) {
			return data.getUserPerms(id);
		}

		public long getUserPerms(Guilds.Data data, long orDefault) {
			return data.getUserPerms(id, orDefault);
		}

		public void setUserPerms(Guilds.Data data, long userPerms) {
			data.setUserPerms(id, userPerms);
		}

		public long getUserPerms(Guild guild) {
			return getUserPerms(Guilds.fromDiscord(guild));
		}

		public long getUserPerms(Guild guild, long orDefault) {
			return getUserPerms(Guilds.fromDiscord(guild), orDefault);
		}

		public void setUserPerms(Guild guild, long userPerms) {
			setUserPerms(Guilds.fromDiscord(guild), userPerms);
		}

		public User getUser(JDA jda) {
			return jda.getUserById(id);
		}

		public User getUser() {
			return getUser(Bot.API);
		}
	}
}
