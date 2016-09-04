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

package cf.adriantodt.bot.impl.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class I18n {
	/**
	 * Dummy instance. Uses = DataManager
	 */
	public static I18n instance = new I18n();

	static {
		getParenting().put(Locale.ROOT, null);
	}

	private Map<Locale, Map<String, String>> locales = new HashMap<>();
	private Map<Locale, Locale> parents = new HashMap<>();

	private I18n() {
	}

	private static Map<Locale, Map<String, String>> getLocales() {
		return instance.locales;
	}

	private static Map<Locale, Locale> getParenting() {
		return instance.parents;
	}

	public static void setParent(Locale locale, Locale parent) {
		if (locale == Locale.ROOT) return;
		if (parent == null) parent = Locale.ROOT;
		getParenting().put(locale, parent);
	}

	public static String getLocalized(String unlocalized, Locale locale) {
		String localized = unlocalized;
		while (unlocalized.equals(localized) && locale != null) {
			Map<String, String> locales = I18n.getLocales().get(locale);
			localized = locales != null ? locales.getOrDefault(unlocalized, unlocalized) : unlocalized;
			if (unlocalized.equals(localized)) locale = getParenting().get(locale);
		}
		return localized;
	}

	public static void localize(Locale target, String unlocalized, String localized) {
		if (!getLocales().containsKey(target)) getLocales().put(target, new HashMap<>());
		getLocales().get(target).put(unlocalized, localized);
	}
}
