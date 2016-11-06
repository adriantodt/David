/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:23]
 */

package cf.adriantodt.David.modules.db;

import cf.adriantodt.David.Loader;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.JDAInstance;
import cf.adriantodt.David.loader.Module.PostReady;
import cf.adriantodt.David.loader.Module.Resource;
import cf.adriantodt.David.commands.base.CommandEvent;
import cf.adriantodt.oldbot.data.entities.Guilds;
import cf.adriantodt.David.modules.cmds.Pushes;
import cf.adriantodt.oldbot.data.entities.Users;
import cf.adriantodt.utils.data.ConfigUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;
import java.util.regex.Pattern;

import static cf.adriantodt.David.loader.Module.Type.STATIC;
import static cf.adriantodt.utils.AsyncUtils.asyncSleepThen;
import static cf.adriantodt.utils.CollectionUtils.iterate;
import static cf.adriantodt.utils.Log4jUtils.logger;
import static cf.adriantodt.utils.StringUtils.notNullOrDefault;

@Module(STATIC)
public class I18nModule {
	private static final Pattern compiledPattern = Pattern.compile("\\$\\([A-Za-z.]+?\\)");
	@Resource("/assets/i18n/main.json")
	private static String i18nMain = "";
	@JDAInstance
	private static JDA jda = null;
	private static List<String> syncedLocalizations = new ArrayList<>(), moderated = new ArrayList<>();
	private static Map<String, Map<String, String>> locales = new HashMap<>();
	private static Map<String, String> parents = new HashMap<>();

	@PostReady
	private static void load() {
		localizeLocal("botname", jda.getSelfUser().getName());
		localizeLocal("mention", jda.getSelfUser().getAsMention());

		JsonObject mainFile = new JsonParser().parse(i18nMain).getAsJsonObject();
		mainFile.entrySet().forEach(entry -> {
			//Before Load, Parse Contents
			JsonObject def = entry.getValue().getAsJsonObject();
			if (def.has("parent")) setParent(entry.getKey(), def.get("parent").getAsString());

			String resource = Loader.resource("/assets/i18n/" + entry.getKey() + ".json");
			if (resource == null) return;

			loadFile(entry.getKey(), new JsonParser().parse(resource));
		});
	}

	private static void loadFile(String lang, JsonElement src) {
		if (!src.isJsonObject()) return;
		JsonObject file = src.getAsJsonObject();

		List<Exception> post = new ArrayList<>();

		try {
			if (file.has("translations")) {
				loadTranslation(lang, "", file.get("translations"), post);
			}
		} catch (Exception e) {
			post.add(e);
		}

		try {
			if (file.has("commands")) {
				JsonElement metaSrc = file.get("meta");

				JsonObject meta = metaSrc.isJsonObject() ? metaSrc.getAsJsonObject() : null;

				file.get("commands").getAsJsonObject().entrySet().forEach(
					entry -> loadCommand(lang, entry.getKey(), entry.getValue(), meta, post)
				);
			}
		} catch (Exception e) {
			post.add(e);
		}

		if (post.size() > 0) {
			logger().info("Errors occurred while loading I18n:");
			post.forEach(e -> logger().error(e));
		}
	}

	private static void loadTranslation(String lang, String base, JsonElement src, List<Exception> post) {
		if (!src.isJsonObject()) return;
		JsonObject t = src.getAsJsonObject();

		t.entrySet().forEach(
			entry -> {
				if (ConfigUtils.isJsonString(entry.getValue())) {
					localize(lang, base + entry.getKey(), entry.getValue().getAsString());
				}
				if (entry.getValue().isJsonObject()) {
					loadTranslation(lang, base + entry.getKey() + ".", entry.getValue(), post);
				}
			}
		);
	}

	private static void loadCommand(String lang, String name, JsonElement src, JsonObject metadata, List<Exception> post) {
		logger().trace(lang + " - " + name + " - " + src.toString());
		if (!src.isJsonObject()) return;
		JsonObject cmd = src.getAsJsonObject();
		try {
			if (cmd.has("desc") || cmd.has("params") || cmd.has("info")) {
				String desc = cmd.has("desc") ? cmd.get("desc").getAsString() : metadata.get("noDesc").getAsString();
				String params = cmd.has("params") ? cmd.get("params").getAsString() : metadata.get("noParams").getAsString();
				String info = cmd.has("info") ? "\n  " + cmd.get("info").getAsString().replace("\n", "\n  ") : "";
				localize(
					lang,
					name + ".usage",
					desc + "\n" + metadata.get("params").getAsString() + ": " + params + info
				);
			}
		} catch (Exception e) {
			post.add(e);
		}

		try {
			if (cmd.has("translations")) {
				loadTranslation(lang, name + ".", cmd.get("translations"), post);
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

	private static void localize(String lang, String untranslated, String translated) {
		pushTranslation(untranslated, lang, translated);
		setModerated(untranslated, lang, true);
	}

	private static void localizeLocal(String untranslated, String translated) {
		setLocalTranslation("dynamic." + untranslated, "en_US", translated);
		setModerated("dynamic." + untranslated, "en_US", true);
	}

	public static String generateJsonDump() {
		System.out.println();
		JsonObject json = new JsonObject();
		JsonObject parentsJson = new JsonObject();
		JsonObject localizations = new JsonObject();

		parents.forEach(parentsJson::addProperty);
		locales.forEach((k, v) -> {
			JsonObject localization = new JsonObject();
			v.forEach(localization::addProperty);
			localizations.add(k, localization);
		});

		json.add("parents", parentsJson);
		json.add("localizations", localizations);
		return json.toString();
	}

	public static void pushTranslation(String unlocalized, String locale, String localized) {
		String localeId = unlocalized + ":" + locale;
		if (syncedLocalizations.contains(localeId)) {
//			r.table("i18n").get(localeId).update(arg -> r.hashMap("value", encode(localized))).runNoReply(conn);
		} else {
//			r.table("i18n").insert(r.hashMap("id", localeId).with("value", encode(localized)).with("moderated", moderated.contains(localeId))).runNoReply(conn);
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
//			r.table("i18n").get(localeId).update(arg -> r.hashMap("moderated", flag)).runNoReply(conn);
		}
	}

	public static void setLocalTranslation(String unlocalized, String locale, String localized) {
		if (!locales.containsKey(unlocalized)) locales.put(unlocalized, new HashMap<>());
		locales.get(unlocalized).put(locale, localized);
	}

	public static String getLocale(CommandEvent event) {
		return notNullOrDefault(Users.fromDiscord(event.getAuthor()).getLang(), event.getGuild().getLang());
	}

	public static void setParent(String locale, String parent) {
		parents.put(locale, parent);
	}

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
		Map<String, String> currentLocales = locales.get(unlocalizing);
		while (unlocalizing.equals(localized) && localed != null) {
			localized = currentLocales != null ? currentLocales.getOrDefault(localed, unlocalizing) : unlocalizing;
			if (unlocalizing.equals(localized)) localed = parents.get(localed);
			else if (localized.length() > 1 && localized.startsWith("$$=") && localized.endsWith(";")) { //This won't change the parent
				localized = localized.substring(3, localized.length() - 1); //Substring localized
				if (unlocalizing.equals(localized)) {//unlocalized = localized -> LOOP
					break;
				} else {
					unlocalizing = localized;
					currentLocales = locales.get(unlocalizing);
				}
			}
		}

		if (unlocalizing.equals(localized) || localed == null) {
			asyncSleepThen(1000, () -> Pushes.pushSimple("i18n", channel -> "I18n Warn: Detected an untranslated String: " + unlocalized + ":" + locale)).run();
		}

		return localized;
	}
}