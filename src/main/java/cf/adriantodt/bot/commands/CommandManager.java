/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:31]
 */

package cf.adriantodt.bot.commands;

import cf.adriantodt.bot.commands.base.ICommand;
import cf.adriantodt.bot.commands.base.ProvidesCommand;
import cf.adriantodt.bot.commands.cmds.*;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static cf.adriantodt.bot.commands.CommandsProvider.addCommand;

public class CommandManager {
	public static void impl() {
		Arrays.asList(
			Bot.class, Cmds.class, Feed.class, Funny.class, Guild.class, Push.class, User.class
		).forEach(CommandManager::load);
	}

	public static void load(Class clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (!m.isAnnotationPresent(ProvidesCommand.class) || !Modifier.isStatic(m.getModifiers())) continue;
			if (m.getParameterTypes().length == 0 && ICommand.class.isAssignableFrom(m.getReturnType())) {
				m.setAccessible(true);
				try {
					addCommand(m.getAnnotation(ProvidesCommand.class).value(), (ICommand) m.invoke(null));
				} catch (Exception e) {
					LogManager.getLogger("CommandManager - AnnotationLoader").error("Error while creating new Command: ", e);
				}
			}
		}
	}
}