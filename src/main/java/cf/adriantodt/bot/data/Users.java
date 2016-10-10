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
import com.rethinkdb.model.MapObject;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;

import static cf.adriantodt.bot.data.DataManager.conn;
import static cf.adriantodt.bot.data.DataManager.r;

public class Users {
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
