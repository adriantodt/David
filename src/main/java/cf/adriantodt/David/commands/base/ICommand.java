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

package cf.adriantodt.David.commands.base;

import cf.adriantodt.David.modules.cmds.manager.PermissionsModule;

public interface ICommand extends ITranslatable {
	void run(CommandEvent event);

	/**
	 * Provides Check for Minimal Perm usage.
	 *
	 * @return the Permission Required
	 */
	default long retrievePerm() {
		return PermissionsModule.RUN_CMDS;
	}

	default boolean sendStartTyping() {
		return true;
	}
}