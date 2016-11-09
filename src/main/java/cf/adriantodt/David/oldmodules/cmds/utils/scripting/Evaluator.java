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

package cf.adriantodt.David.oldmodules.cmds.utils.scripting;

import cf.adriantodt.David.commands.base.CommandEvent;

import java.util.HashMap;
import java.util.Map;

public interface Evaluator {
	Map<String, Evaluator> EVALUATOR_REGISTER = new HashMap<>();

	void eval(CommandEvent event);
}
