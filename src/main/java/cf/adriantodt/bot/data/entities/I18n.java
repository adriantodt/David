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

import cf.adriantodt.bot.commands.base.CommandEvent;
import com.google.gson.JsonObject;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.regex.Pattern;

import static cf.adriantodt.bot.data.DataManager.*;
import static cf.adriantodt.bot.utils.Utils.*;

/*
 * LOCALES:
 * [pt_BR, en_US] = defaultLocales
 * [g_GUILDNAME] = guild translation
 */
public class I18n {
	private static final Pattern compiledPattern = Pattern.compile("\\$\\([A-Za-z.]+?\\)");
	private static List<String> syncedLocalizations = new ArrayList<>(), moderated = new ArrayList<>();
	private static Map<String, Map<String, String>> locales = new HashMap<>();
	private static Map<String, String> parents = new HashMap<>();

	public static String generateJsonDump() {
		System.out.println();
		JsonObject json = new JsonObject();
		JsonObject parents = new JsonObject();
		JsonObject localizations = new JsonObject();

		I18n.parents.forEach(parents::addProperty);
		locales.forEach((k, v) -> {
			JsonObject localization = new JsonObject();
			v.forEach(localization::addProperty);
			localizations.add(k, localization);
		});

		json.add("parents", parents);
		json.add("localizations", localizations);
		return json.toString();
	}

	public static void pushTranslation(String unlocalized, String locale, String localized) {
		String localeId = unlocalized + ":" + locale;
		if (syncedLocalizations.contains(localeId)) {
			r.table("i18n").get(localeId).update(arg -> r.hashMap("value", encode(localized))).runNoReply(conn);
		} else {
			r.table("i18n").insert(r.hashMap("id", localeId).with("value", encode(localized)).with("moderated", moderated.contains(localeId))).runNoReply(conn);
			syncedLocalizations.add(localeId);
		}

		setLocalTranslation(unlocalized, locale, localized);
	}

	public static void setModerated(String unlocalized, String locale, boolean flag) {
		String localeId = unlocalized + ":" + locale;

		if (flag && !moderated.contains(localeId)) {
			moderated.add(localeId);
		} else if (!flag && moderated.contains(localeId)) {
			moderated.remove(localeId);
		}

		if (syncedLocalizations.contains(localeId)) {
			r.table("i18n").get(localeId).update(arg -> r.hashMap("moderated", flag)).runNoReply(conn);
		}
	}

	public static void setLocalTranslation(String unlocalized, String locale, String localized) {
		if (!locales.containsKey(unlocalized)) locales.put(unlocalized, new HashMap<>());
		locales.get(unlocalized).put(locale, localized);
	}

	public static String getLocale(CommandEvent event) {
		return nnOrD(Users.fromDiscord(event.getAuthor()).getLang(), event.getGuild().getLang());
	}

	public static void setParent(String locale, String parent) {
		parents.put(locale, parent);
	}

//	public static String getLocalized(String unlocalized, String locale) {
//		String localized = getBaseLocalized(unlocalized, locale);
//		if (!localized.contains("$(")) return localized;
//
//		Set<String> skipIfIterated = new HashSet<>();
//		for (String key : iterate(compiledPattern.matcher(localized))) {
//			if (skipIfIterated.contains(key)) continue;
//			String unlocalizedKey = key.substring(2, key.length() - 1);
//			localized = localized.replace(key, getLocalized(unlocalizedKey, locale));
//			if (!localized.contains("$(")) break;
//			skipIfIterated.add(key);
//		}
//
//		return localized;
//	}


	public static String getLocalized(String unlocalized, String locale) {
		return dynamicTranslate(getBaseLocalized(unlocalized, locale), locale, null);
	}

	public static String dynamicTranslate(String string, String locale, Optional<Map<String, String>> dynamicMap) {
		if (dynamicMap == null) dynamicMap = Optional.empty();
		if (!string.contains("$(")) return string;

		Set<String> skipIfIterated = new HashSet<>();
		for (String key : iterate(compiledPattern.matcher(string))) {
			if (skipIfIterated.contains(key)) continue;
			String unlocalizedKey = key.substring(2, key.length() - 1);

			if (dynamicMap.isPresent()) {
				string = string.replace(key, Optional.ofNullable(dynamicMap.get().get(unlocalizedKey)).orElseGet(() -> getLocalized(unlocalizedKey, locale)));
			} else {
				string = string.replace(key, getLocalized(unlocalizedKey, locale));
			}

			if (!string.contains("$(")) break;
			skipIfIterated.add(key);
		}

		return string;
	}

	public static String getLocalized(String unlocalized, CommandEvent event) {
		return getLocalized(unlocalized, getLocale(event));
	}

	public static String getLocalized(String unlocalized, TextChannel channel) {
		return getLocalized(unlocalized, Guilds.fromDiscord(channel.getGuild()).getLang());
	}

	private static String getBaseLocalized(final String unlocalized, final String locale) {
		String unlocalizing = unlocalized, localed = locale, localized = unlocalizing;
		Map<String, String> locales = I18n.locales.get(unlocalizing);
		while (unlocalizing.equals(localized) && localed != null) {
			localized = locales != null ? locales.getOrDefault(localed, unlocalizing) : unlocalizing;
			if (unlocalizing.equals(localized)) localed = parents.get(localed);
			else if (localized.length() > 1 && localized.startsWith("$$=") && localized.endsWith(";")) { //This won't change the parent
				localized = localized.substring(3, localized.length() - 1); //Substring localized
				if (unlocalizing.equals(localized)) {//unlocalized = localized -> LOOP
					break;
				} else {
					unlocalizing = localized;
					locales = I18n.locales.get(unlocalizing);
				}
			}
		}

		if (unlocalizing.equals(localized) || localed == null) {
			async(() -> Pushes.pushSimple("i18n", channel -> "I18n Warn: Detected an untranslated String: " + unlocalized + ":" + locale)).run();
		}

		return localized;
	}
}
