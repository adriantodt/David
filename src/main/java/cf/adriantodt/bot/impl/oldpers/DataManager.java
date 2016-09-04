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

package cf.adriantodt.bot.impl.oldpers;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Java;
import cf.adriantodt.bot.Statistics;
import cf.adriantodt.bot.base.cmd.UserCommand;
import cf.adriantodt.bot.base.guild.DiscordGuild;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cf.adriantodt.bot.Bot.JSON;
import static cf.adriantodt.bot.Bot.LOGGER;

public class DataManager {
	public static BotData data;
	public static Configs configs;

	public static void saveData() {
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
			Files.write(getSaveFile(), JSON.toJson(data).getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Statistics.saves++;
	}

	public static void loadData() {
		try {
			data = JSON.fromJson(new String(Files.readAllBytes(getSaveFile()), Charset.forName("UTF-8")), BotData.class);
		} catch (Exception e) {
			data = new BotData();
			saveData();
		}

		Bot.GAME = data.game;
		Bot.setDefault();

		data.guilds.forEach(data -> {
			DiscordGuild tmpG = DiscordGuild.fromId(data.id);
			DiscordGuild guild = (tmpG == null ? new DiscordGuild() : tmpG);
			if (guild.id.equals("-1")) guild.id = data.id;
			guild.name = data.name;
			guild.userPerms = data.userPerms;
			data.commands.forEach((cmdName, responses) -> {
				UserCommand cmd = guild.commands.get(cmdName);
				if (cmd == null) {
					UserCommand usrCmd = new UserCommand();
					usrCmd.responses = responses;
					guild.commands.put(cmdName, usrCmd);
				} else {
					cmd.responses = responses;
				}
			});
		});

		Statistics.loads++;
	}

	public static void loadConfig() {
		try {
			configs = JSON.fromJson(new String(Files.readAllBytes(getConfigs()), Charset.forName("UTF-8")), Configs.class);
		} catch (Exception e) {
			LOGGER.error("Configuration File not found. Generating one at " + getConfigs() + "...");
			configs = new Configs();
			try {
				Files.write(getConfigs(), JSON.toJson(configs).getBytes(Charset.forName("UTF-8")));
				LOGGER.error("Configuration File generated. Please fill the 2 required configs. The App will now Exit.");
			} catch (Exception ex) {
				LOGGER.error("Configuration File could not be generated. Please fix the permissions. The App will now Exit.");
			}

			Java.stopApp();
		}
	}

	public static Path getSaveFile() {
		return getPath(Bot.BOTNAME);
	}

	public static Path getPath(String file) {
		try {
			return Paths.get(file + ".json");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Path getConfigs() {
		return getPath("Configs");
	}
}
