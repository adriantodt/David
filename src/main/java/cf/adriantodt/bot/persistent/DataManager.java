/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 08:18]
 */

package cf.adriantodt.bot.persistent;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Java;
import cf.adriantodt.bot.Statistics;
import cf.adriantodt.bot.cmd.ICommand;
import cf.adriantodt.bot.cmd.UserCommand;
import cf.adriantodt.bot.guild.DiscordGuild;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cf.adriantodt.bot.Bot.JSON;
import static cf.adriantodt.bot.Bot.LOGGER;

public class DataManager {
	public static BotData data;
	public static OptionsData options;

	public static void saveData() {
		Statistics.saves++;
		data = new BotData();

		data.game = Bot.GAME;

		DiscordGuild.all.forEach(guild -> {
			DiscordGuildData data = new DiscordGuildData();
			data.id = guild.id;
			data.name = guild.name;
			data.userPerms = guild.userPerms;
			guild.commands.forEach((cmdName, cmd) -> {
				if (cmd != null) data.commands.put(cmdName, cmd.responses);
			});
			DataManager.data.guilds.add(data);
		});

		try {
			Files.write(getPath(), JSON.toJson(data).getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadData() {
		Statistics.loads++;
		try {
			data = JSON.fromJson(new String(Files.readAllBytes(getPath()), Charset.forName("UTF-8")), BotData.class);
		} catch (Exception e) {
			data = new BotData();
			saveData();
		}

		Bot.GAME = data.game;

		data.guilds.forEach(data -> {
			DiscordGuild tmpG = DiscordGuild.fromId(data.id);
			DiscordGuild guild = (tmpG == null ? new DiscordGuild() : tmpG);
			if (guild.id.equals("-1")) guild.id = data.id;
			guild.name = data.name;
			guild.userPerms = data.userPerms;
			data.commands.forEach((cmdName, responses) -> {
				ICommand cmd = guild.commands.get(cmdName);
				if (cmd == null) {
					UserCommand usrCmd = new UserCommand();
					usrCmd.responses = responses;
					guild.commands.put(cmdName, usrCmd);
				} else {
					UserCommand usrCmd = (UserCommand) cmd;
					usrCmd.responses = responses;
				}
			});
		});

		Bot.setDefault();
	}

	public static void loadOptions() {
		try {
			options = JSON.fromJson(new String(Files.readAllBytes(getMaster()), Charset.forName("UTF-8")), OptionsData.class);
		} catch (Exception e) {
			LOGGER.error("Options File not found. The Bot will generate one at " + getMaster() + " and exit.");
			options = new OptionsData();
			try {
				Files.write(getMaster(), JSON.toJson(options).getBytes(Charset.forName("UTF-8")));
				LOGGER.error("Options File generated. Please fill the 2 required options. The bot will now Exit.");
			} catch (Exception ex) {
				LOGGER.error("Options File could not be generated. Please fix the permissions. The bot will now Exit.");
			}

			Java.stopApp();
		}
	}

	public static Path getPath() {
		try {
			return Paths.get(Bot.BOTNAME + ".JSON");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Path getMaster() {
		try {
			return Paths.get("BotOptions.JSON");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
