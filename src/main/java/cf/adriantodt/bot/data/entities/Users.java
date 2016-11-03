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
import cf.adriantodt.bot.data.ConfigUtils;
import cf.adriantodt.utils.AsyncUtils;
import cf.adriantodt.utils.TaskManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rethinkdb.model.MapObject;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static cf.adriantodt.bot.data.DataManager.conn;
import static cf.adriantodt.bot.data.DataManager.r;
import static cf.adriantodt.bot.data.ReturnHandler.h;
import static cf.adriantodt.utils.StringUtils.notNullOrDefault;

public class Users {
	private static List<Data> all = new ArrayList<>();
	private static Map<User, Data> userMap = new HashMap<>();
	private static Map<Data, Integer> timeoutUntilDbRemoval = new HashMap<>();

	static {
		TaskManager.startAsyncTask("UserTimeoutCleanup", () -> {
			timeoutUntilDbRemoval.replaceAll((guild, integer) -> Math.min(integer - 1, 0));
			timeoutUntilDbRemoval.entrySet().stream().filter(entry -> entry.getValue() == 0).map(Map.Entry::getKey).forEach(data -> {
				//TODO IMPL DB REMOVAL
				timeoutUntilDbRemoval.remove(data);
			});
		}, 60);

		AsyncUtils.asyncSleepThen(100, Users::loadAll);
	}

	public static List<Data> all() {
		return Collections.unmodifiableList(all);
	}

	private static void loadAll() {
		h.from(r.table("users").run(conn)).cursorExpected().forEach(Users::unpack);
	}

	private static Data unpack(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		Data data = all.stream().filter(dataPredicate -> object.get("id").getAsString().equals(dataPredicate.id)).findFirst().orElseGet(Data::new);
		data.id = object.get("id").getAsString();
		data.lang = ConfigUtils.isJsonString(object.get("lang")) ? object.get("lang").getAsString() : null;
		userMap.put(data.getUser(), data);
		if (data.getUser() == null) {
			timeoutUntilDbRemoval.put(data, 5);
		}
		return data;
	}

	@SubscribeEvent
	public static void newUser(GuildMemberJoinEvent e) {
		Data data = fromDiscord(e.getMember().getUser());
		if (timeoutUntilDbRemoval.containsKey(data)) timeoutUntilDbRemoval.remove(data);
	}

	@SubscribeEvent
	public static void byeUser(GuildMemberLeaveEvent e) {
		if (e.getJDA().getGuilds().stream().anyMatch(guild -> guild != e.getGuild() && guild.isMember(e.getMember().getUser())))
			return;
		timeoutUntilDbRemoval.put(fromDiscord(e.getMember().getUser()), 5);
	}

	public static Data fromDiscord(User user) {
		if (userMap.containsKey(user)) {
			return userMap.get(user);
		} else {
			Data data = new Data();
			userMap.put(user, data);
			data.id = user.getId();

			MapObject m =
				r.hashMap("id", data.id)
					.with("lang", data.lang);

			r.table("users").insert(m).runNoReply(conn);

			return data;
		}
	}

	public static Data fromId(String id) {
		for (Data g : all) {
			if (g.id.equals(id)) return g;
		}

		return null;
	}

	public static Data fromDiscord(GuildMessageReceivedEvent event) {
		return fromDiscord(event.getAuthor());
	}

	public static String toString(Data data, JDA jda, String language, Guild guildAt) {
		User user = data.getUser(jda);
		Member member = data.getMember(guildAt);
		if (member == null) throw new RuntimeException("User doesn't belong to the Guild.");
		return I18n.getLocalized("user.name", language) + ": " + user.getName() + "\n" +
			I18n.getLocalized("user.nick", language) + ": " + (member.getNickname() == null ? "(" + I18n.getLocalized("user.none", language) + ")" : member.getNickname()) + "\n" +
			I18n.getLocalized("user.roles", language) + ": " + notNullOrDefault(String.join(", ", member.getRoles().stream().map(Role::getName).toArray(String[]::new)), "(" + I18n.getLocalized("user.none", language) + ")") + "\n" +
			I18n.getLocalized("user.memberSince", language) + ": " + member.getJoinDate().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "\n" +
			I18n.getLocalized("user.commonGuilds", language) + ": " + notNullOrDefault(String.join(", ", jda.getGuilds().stream().filter(guild -> guild.isMember(user)).map(Guild::getName).toArray(String[]::new)), "(" + I18n.getLocalized("user.none", language) + ")") + "\n" +
			"ID: " + user.getId() + "\n" +
			I18n.getLocalized("user.status", language) + ": " + member.getOnlineStatus() + "\n" +
			I18n.getLocalized("user.playing", language) + ": " + (member.getGame() == null ? "(" + I18n.getLocalized("user.none", language) + ")" : member.getGame().getName());
	}

	public static class Data {
		private String id = "-1", lang = null;

		private static void pushUpdate(Users.Data data, MapObject changes) {
			r.table("users").get(data.id).update(changes).runNoReply(conn);
		}

		public String getId() {
			return id;
		}

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			if (lang.isEmpty()) lang = null;
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

		public Member getMember(Guilds.Data data, JDA jda) {
			return getMember(data.getGuild(jda));
		}

		public Member getMember(Guilds.Data data) {
			return getMember(data.getGuild(Bot.API));
		}

		public Member getMember(Guild guild) {
			return guild == null ? null : guild.getMember(getUser(guild.getJDA()));
		}
	}
}
