/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/10/16 15:51]
 */

package cf.adriantodt.bot.data;

import cf.adriantodt.bot.base.cmd.UserCommand;

import java.nio.charset.Charset;
import java.util.*;

import static cf.adriantodt.bot.data.DataManager.*;

@SuppressWarnings("unchecked")
public class UserCommands {
	/**
	 * A cache of commands (Command -> Primary Key)
	 */
	private static Map<UserCommand, String> cachedCommands = new HashMap<>();
	/**
	 * Control of Commands (Guild -> Command Name -> Command)
	 */
	private static Map<Guilds.Data, Map<String, UserCommand>> guildCommands = new HashMap<>();

	public static void register(UserCommand command, String name, Guilds.Data guild) {
		if (cachedCommands.containsKey(command)) throw new IllegalStateException("The Command is already registered");

		if (!guildCommands.containsKey(guild)) guildCommands.put(guild, new HashMap<>());
		guildCommands.get(guild).put(name, command);

		//Insert
		cachedCommands.put(command, h.response(r.table("commands").insert(
			r.hashMap("gid", guild.getId())
				.with("responses", cmdsToDB(new ArrayList<>(command.responses)))
				.with("name", name)
		).run(conn)).object().getAsJsonObject().get("generated_keys").getAsJsonArray().get(0).getAsString());
	}

	public static void update(UserCommand command) {
		if (!cachedCommands.containsKey(command)) throw new IllegalStateException("The Command isn't registered");

		//Update
		r.table("commands").get(cachedCommands.get(command)).update(arg -> r.hashMap("responses", cmdsToDB(new ArrayList<>(command.responses)))).runNoReply(conn);
	}

	public static void remove(UserCommand command) {
		if (!cachedCommands.containsKey(command)) throw new IllegalStateException("The Command isn't at the database");

		cachedCommands.remove(command);
		List<Runnable> post = new ArrayList<>();
		guildCommands.forEach((data, map) -> map.forEach((s, cmd) -> {
			if (cmd == command) post.add(() -> guildCommands.get(data).remove(s));
		}));
		post.forEach(Runnable::run);

		//Delete
		r.table("commands").get(cachedCommands.get(command)).delete().runNoReply(conn);
	}

	public static void loadAllFrom(Guilds.Data data) {
		Map<String, UserCommand> thisGuildCommands = guildCommands.containsKey(data) ? guildCommands.get(data) : new HashMap<>();

		h.query(r.table("commands").filter(row -> row.g("gid").eq(data.getId())).run(conn)).forEach(jsonElement -> {
			UserCommand cmd = new UserCommand();
			jsonElement.getAsJsonObject().get("responses").getAsJsonArray().forEach(jsonString -> cmd.responses.add(jsonString.getAsString()));
			cmd.responses = cmdsFromDB(cmd.responses);
			thisGuildCommands.put(jsonElement.getAsJsonObject().get("name").getAsString(), cmd);
			cachedCommands.put(cmd, jsonElement.getAsJsonObject().get("id").getAsString());
		});


		guildCommands.put(data, thisGuildCommands);
	}

	public static Map<String, UserCommand> allFrom(Guilds.Data data) {
		if (!guildCommands.containsKey(data)) guildCommands.put(data, new HashMap<>());
		return Collections.unmodifiableMap(guildCommands.get(data));
	}

	public static List<String> cmdsFromDB(List<String> l) {
		l.replaceAll(s -> new String(Base64.getDecoder().decode(s.getBytes()), Charset.forName("UTF-8")));
		return l;
	}

	public static List<String> cmdsToDB(List<String> l) {
		l.replaceAll(s -> Base64.getEncoder().encodeToString(s.getBytes(Charset.forName("UTF-8"))));
		return l;
	}
}
