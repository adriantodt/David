/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:44]
 */

package cf.adriantodt.David.modules.db;

import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.commands.base.UserCommand;

import java.nio.charset.Charset;
import java.util.*;

import static cf.adriantodt.David.loader.Module.Type.*;

@Module(STATIC)
public class UserCommandsModule {
	private static Map<UserCommand, String> cachedCommands = new HashMap<>();
	private static Map<GuildModule.Data, Map<String, UserCommand>> guildCommands = new HashMap<>();

	public static void register(UserCommand command, String name, GuildModule.Data guild) {
		if (cachedCommands.containsKey(command)) throw new IllegalStateException("The Command is already registered");

		if (!guildCommands.containsKey(guild)) guildCommands.put(guild, new HashMap<>());
		guildCommands.get(guild).put(name, command);

		//Insert
		cachedCommands.put(command, DBModule.onDB(r -> r.table("commands").insert(
			r.hashMap("gid", guild.getId())
				.with("responses", cmdsToDB(new ArrayList<>(command.responses)))
				.with("name", name)
		)).run().mapExpected().get("generated_keys").getAsJsonArray().get(0).getAsString());
	}

	public static void update(UserCommand command) {
		if (!cachedCommands.containsKey(command)) throw new IllegalStateException("The Command isn't registered");

		//Update
		DBModule.onDB(r -> r.table("commands").get(cachedCommands.get(command)).update(arg -> r.hashMap("responses", cmdsToDB(new ArrayList<>(command.responses))))).noReply();
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
		DBModule.onDB(r -> r.table("commands").get(cachedCommands.get(command)).delete()).noReply();
	}

	public static void loadAllFrom(GuildModule.Data data) {
		Map<String, UserCommand> thisGuildCommands = guildCommands.containsKey(data) ? guildCommands.get(data) : new HashMap<>();

		DBModule.onDB(r -> r.table("commands").filter(row -> row.g("gid").eq(data.getId()))).run().cursorExpected().forEach(jsonElement -> {
			UserCommand cmd = new UserCommand();
			jsonElement.getAsJsonObject().get("responses").getAsJsonArray().forEach(jsonString -> cmd.responses.add(jsonString.getAsString()));
			cmd.responses = cmdsFromDB(cmd.responses);
			thisGuildCommands.put(jsonElement.getAsJsonObject().get("name").getAsString(), cmd);
			cachedCommands.put(cmd, jsonElement.getAsJsonObject().get("id").getAsString());
		});


		guildCommands.put(data, thisGuildCommands);
	}

	public static Map<String, UserCommand> allFrom(GuildModule.Data data) {
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