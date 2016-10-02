/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [12/09/16 07:39]
 */

package cf.adriantodt.bot.base;

import cf.adriantodt.bot.handlers.CommandHandler;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class I18n {
	public static I18n instance = new I18n();

	private Map<String, Map<String, String>> locales = new HashMap<>();
	private Map<String, String> parents = new HashMap<>(), userLangs = new HashMap<>();

	private I18n() {
	}

	private static Map<String, Map<String, String>> getLocales() {
		return instance.locales;
	}

	private static Map<String, String> getParenting() {
		return instance.parents;
	}

	public static void setParent(String locale, String parent) {
		getParenting().put(locale, parent);
	}

	public static void setLang(User user, String lang) {
		String userid = Permissions.processID(user.getId());
		if (lang.isEmpty()) {
			instance.userLangs.remove(userid);
		} else {
			instance.userLangs.put(userid, lang);
		}
	}

	public static String getLang(MessageReceivedEvent event) {
		return instance.userLangs.getOrDefault(Permissions.processID(event.getAuthor().getId()), CommandHandler.getGuild(event).defaultLanguage);
	}

	public static String getLocalized(String unlocalized, String locale) {
		String localized = unlocalized;
		Map<String, String> locales = I18n.getLocales().get(unlocalized);
		while (unlocalized.equals(localized) && locale != null) {
			localized = locales != null ? locales.getOrDefault(locale, unlocalized) : unlocalized;
			if (unlocalized.equals(localized)) locale = getParenting().get(locale);
		}
		return localized;
	}

	public static String getLocalized(String unlocalized, MessageReceivedEvent event) {
		return getLocalized(unlocalized, getLang(event));
	}

	public static void localize(String target, String unlocalized, String localized) {
		if (!getLocales().containsKey(unlocalized)) getLocales().put(unlocalized, new HashMap<>());
		getLocales().get(unlocalized).put(target, localized);
	}
}
