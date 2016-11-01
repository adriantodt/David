/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:15]
 */

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.impl.cmds.*;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static cf.adriantodt.bot.utils.Commands.addCommand;

public class CmdsAndInterfaces {
	public static void impl() {
		Arrays.asList(
			Bot.class, Cmds.class, /*Feed.class,*/ Funny.class, Guild.class, Push.class, User.class
		).forEach(CmdsAndInterfaces::load);
	}

	public static void load(Class clazz) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (!m.isAnnotationPresent(ProvidesCommand.class) || !Modifier.isStatic(m.getModifiers())) continue;
			if (m.getParameterTypes().length == 0 && ICommand.class.isAssignableFrom(m.getReturnType())) {
				m.setAccessible(true);
				try {
					addCommand(m.getAnnotation(ProvidesCommand.class).value(), (ICommand) m.invoke(null));
				} catch (Exception e) {
					LogManager.getLogger("CmdsAndInterfaces - AnnotationLoader").error("Error while creating new Command: ", e);
				}
			}
		}
	}
}