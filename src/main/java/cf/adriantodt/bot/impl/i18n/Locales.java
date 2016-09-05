/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/09/16 19:16]
 */

package cf.adriantodt.bot.impl.i18n;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Locales {
	private static Map<String, Locale> cache = new HashMap<>();

	static {
//		for (Field field : Locale.class.getDeclaredFields()) //This Reflection is used to HashMap-fy all the Fields above.
//			if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) //public static final fields only
//				try {
//					put(field.getName(), field.getLong(null));
//				} catch (Exception ignored) {
//				}

	}

	public static Locale get(String language, String country) {
		if (cache.containsKey(language + "-" + country))
			cache.put(language + "-" + country, new Locale(language, country));
		return cache.get(language + "-" + country);
	}
}
