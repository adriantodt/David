/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [22/09/16 18:35]
 */

package cf.adriantodt.oldbot;


import cf.adriantodt.David.modules.cmds.Pushes;
import cf.adriantodt.David.oldmodules.init.MergeTasksWithInitModule;

public class Bot {

	public static void init() throws Exception {
		MergeTasksWithInitModule.startAsyncTasks();


		MergeTasksWithInitModule.startJDAAsyncTasks();


	}


}
