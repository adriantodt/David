/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:38]
 */

package cf.adriantodt.bot.data;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.data.entities.I18n;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import static cf.adriantodt.bot.data.entities.I18n.setParent;
import static cf.adriantodt.utils.Log4jUtils.logger;

/**
 * Hardcoded Impl goes here. Shouldn't be used too much.
 * It's here for Anti Data Loss regeneration.
 */
public class I18nHardImpl {
	public static void impl() {
		JsonObject mainFile = new JsonParser().parse(ContentManager.resource("/assets/i18n/main.json")).getAsJsonObject();
		mainFile.entrySet().forEach(entry -> {
			//Before Load, Parse Contents
			JsonObject def = entry.getValue().getAsJsonObject();
			if (def.has("parent")) setParent(entry.getKey(), def.get("parent").getAsString());

			String resource = ContentManager.resource("/assets/i18n/" + entry.getKey() + ".json");
			if (resource == null) return;

			JsonObject file = new JsonParser().parse(resource).getAsJsonObject();
		});
	}

	private static void loadFile(String lang, JsonElement src) {
		if (!src.isJsonObject()) return;
		JsonObject file = src.getAsJsonObject();

		List<Exception> post = new ArrayList<>();

		try {
			file.get("translations").getAsJsonObject().entrySet().forEach(
				entry -> localize(lang, entry.getKey(), entry.getValue().getAsString())
			);
		} catch (Exception e) {
			post.add(e);
		}

		try {
			JsonElement metaSrc = file.get("meta");

			JsonObject meta = metaSrc.isJsonObject() ? metaSrc.getAsJsonObject() : null;

			file.get("commands").getAsJsonObject().entrySet().forEach(
				entry -> loadCommand(lang, entry.getKey(), entry.getValue(), meta, post)
			);
		} catch (Exception e) {
			post.add(e);
		}

		if (post.size() > 0) {
			logger().info("Errors occurred while loading I18N:");
			post.forEach(e -> logger().error(e));
		}
	}

	private static void loadCommand(String lang, String name, JsonElement src, JsonObject metadata, List<Exception> post) {
		if (!src.isJsonObject()) return;
		JsonObject cmd = src.getAsJsonObject();
		try {
			if (cmd.has("desc") || cmd.has("params") || cmd.has("info")) {
				localize(
					lang,
					name + ".usage",
					cmd.get("desc").getAsString() + "\n"
						+ metadata.get("params").getAsString() + ": " + cmd.get("params").getAsString()
						+ (cmd.has("info") ? "\n  " + cmd.get("info").getAsString().replace("\n", "\n  ") : ""));
			}
		} catch (Exception e) {
			post.add(e);
		}

		try {
			if (cmd.has("translations") && cmd.get("translations").isJsonObject()) {
				cmd.get("translations").getAsJsonObject().entrySet().forEach(
					entry -> localize(lang, name + "." + entry.getKey(), entry.getValue().getAsString())
				);
			}
		} catch (Exception e) {
			post.add(e);
		}

		try {
			if (cmd.has("subs") && cmd.get("subs").isJsonObject()) {
				cmd.get("subs").getAsJsonObject().entrySet().forEach(entry ->
					loadCommand(lang, name + "." + entry.getKey(), entry.getValue(), metadata, post)
				);
			}
		} catch (Exception e) {
			post.add(e);
		}
	}

	public static void implLocal() {
		localizeLocal("botname", Bot.SELF.getName());
		localizeLocal("mention", Bot.SELF.getAsMention());
	}

	private static void localize(String lang, String untranslated, String translated) {
		I18n.pushTranslation(untranslated, lang, translated);
		I18n.setModerated(untranslated, lang, true);
	}

	private static void localizeLocal(String untranslated, String translated) {
		I18n.setLocalTranslation("dynamic." + untranslated, "en_US", translated);
		I18n.setModerated("dynamic." + untranslated, "en_US", true);
	}
}
