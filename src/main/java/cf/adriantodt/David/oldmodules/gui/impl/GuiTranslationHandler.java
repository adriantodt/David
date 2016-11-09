/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/11/16 20:36]
 */

package cf.adriantodt.David.oldmodules.gui.impl;




import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiTranslationHandler {
	private static final List<Runnable> hooks = new ArrayList<>(), lazyHooks = new ArrayList<>();
	private static String lang = "en_US";

	public static void update() {
		hooks.forEach(Runnable::run);
		lazyHooks.forEach(Runnable::run);
	}

	public static void addHook(Runnable runnable) {
		hooks.add(runnable);
	}

	public static void addLazyHook(Runnable runnable) {
		lazyHooks.add(runnable);
	}

	public static void addHook(Consumer<String> consumer, String unlocalized) {
		addHook(() -> consumer.accept(get(unlocalized)));
	}

	public static void addLazyHook(Consumer<String> consumer, String unlocalized) {
		addLazyHook(() -> consumer.accept(get(unlocalized)));
	}

	public static String get(String unlocalized) {
		if (!Bot.LOADED) return unlocalized;
		else return I18nModule.getLocalized("gui." + unlocalized, lang);
	}

	public static String getLang() {
		return lang;
	}

	public static void setLang(String lang) {
		GuiTranslationHandler.lang = lang;
		update();
	}
}
