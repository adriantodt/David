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

package cf.adriantodt.bot.base.persistence;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Java;
import cf.adriantodt.bot.Statistics;
import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.I18n;
import cf.adriantodt.bot.base.cmd.UserCommand;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static cf.adriantodt.bot.Bot.*;

public class DataManager {
	public static BotData data;
	public static Configs configs;

	public static void saveData() {
		Map<String, List<String>> persAnnoy = data.annoy;
		data = new BotData();
		data.game = Bot.GAME;
		data.annoy = persAnnoy;

		DiscordGuild.all.forEach(guild -> {
			DiscordGuildData data = new DiscordGuildData();
			data.id = guild.id;
			data.name = guild.name;
			data.userPerms = guild.userPerms;
			data.flags = guild.flags;
			data.lang = guild.defaultLanguage;
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
			guild.flags = data.flags;
			guild.defaultLanguage = data.lang;
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

	public static void saveI18n() {
		try {
			Files.write(getPath(BOTNAME + "-i18n"), JSON.toJson(I18n.instance).getBytes(Charset.forName("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void loadI18n() {
		try {
			I18n.instance = JSON.fromJson(new String(Files.readAllBytes(getPath(BOTNAME + "-i18n")), Charset.forName("UTF-8")), I18n.class);
		} catch (Exception e) {
			saveI18n();
		}
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
