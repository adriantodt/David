/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/10/16 07:53]
 */

package cf.adriantodt.bot.data;

import cf.adriantodt.bot.Java;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static cf.adriantodt.bot.Bot.JSON;
import static cf.adriantodt.bot.Bot.LOGGER;
import static cf.adriantodt.bot.data.DataManager.configs;
import static cf.adriantodt.bot.data.DataManager.getPath;

public class Configs {
	public String token = "", ownerID = "", hostname = "";
	public int port = 0;

	public static Path getConfigs() {
		return getPath("configs", "json");
	}

	public static void loadConfig() {
		try {
			configs = JSON.fromJson(new String(Files.readAllBytes(getConfigs()), Charset.forName("UTF-8")), Configs.class);
			Objects.requireNonNull(configs, "Config object is null (How do you  managed to even do that?)");
			Objects.requireNonNull(configs.hostname, "Hostname doesn't exists/is null");
			Objects.requireNonNull(configs.token, "Token doesn't exists/is null");
			Objects.requireNonNull(configs.ownerID, "OwnerID doesn't exists/is null");
			if (configs.port == 0) throw new IllegalStateException("Port number can't be 0");
		} catch (Exception e) {
			LOGGER.error("Configuration File not found or damaged. Generating one at " + getConfigs() + "...");
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
}
