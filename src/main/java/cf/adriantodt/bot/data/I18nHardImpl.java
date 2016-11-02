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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static cf.adriantodt.bot.data.entities.I18n.setParent;

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

			file.get("translations").getAsJsonObject().entrySet().forEach(
				entry2 -> localize(entry.getKey(), entry2.getKey(), entry2.getValue().getAsString())
			);

			file.get("commands").getAsJsonObject().entrySet().forEach(
				entry2 -> {
					JsonObject v = entry2.getValue().getAsJsonObject();
					localize(
						entry.getKey(),
						entry2.getKey() + ".usage",
						v.get("desc").getAsString() + "\n"
							+ file.get("meta").getAsJsonObject().get("params").getAsString() + ": " + v.get("params").getAsString()
							+ (v.has("info") ? "\n  " + v.get("info").getAsString().replace("\n", "\n  ") : ""));
				}
			);
		});
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
