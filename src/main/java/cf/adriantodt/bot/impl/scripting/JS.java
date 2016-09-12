/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [06/09/16 22:09]
 */

package cf.adriantodt.bot.impl.scripting;

import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.I18n;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static cf.adriantodt.bot.Answers.exception;
import static cf.adriantodt.bot.Answers.sendCased;

public class JS {
	private static final ScriptEngine engine;

	static {
		engine = new ScriptEngineManager().getEngineByName("nashorn");
		try {
			engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util);");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public static void eval(DiscordGuild guild, String command, MessageReceivedEvent event) {
		try {
			engine.put("event", event);
			engine.put("guild", guild);
			Object out = engine.eval(
				"(function() {" +
					"with (imports) {" +
					command +
					"}" +
					"})();");
			sendCased(event, out == null ? I18n.getLocalized("eval.noOut", event) : out.toString());
		} catch (ScriptException e) {
			exception(event, e);
		}
	}
}
