/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:51]
 */

package cf.adriantodt.bot.impl.cmds;

import cf.adriantodt.bot.base.cmd.CommandBuilder;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.impl.ProvidesCommand;
import cf.adriantodt.bot.utils.Utils;

import java.util.Arrays;

import static cf.adriantodt.bot.utils.Utils.advancedSplitArgs;

public class TestCmds {
	@ProvidesCommand("parser")
	private static ICommand parser() {
		return new CommandBuilder("noUsage")
			.setAction(event -> event.awaitTyping().sendMessage(Arrays.toString(Utils.parse(event.getArgs(0)).entrySet().toArray())).queue())
			.build();
	}

	@ProvidesCommand("splitargs")
	private static ICommand splitargs() {
		return new CommandBuilder("noUsage")
			.setAction(event -> event.awaitTyping().sendMessage(Arrays.toString(advancedSplitArgs(event.getArgs(), 0))).queue())
			.build();
	}

	//implAnnoy
//		addCommand("annoy", new CommandBuilder()
//			.setPermRequired(ANNOY)
//			.setUsageDeprecatedMethod("")
//			.setAction(((arguments, event) -> {
//				String[] args = splitArgs(arguments, 3); //!spysend CH MSG
//				if (args[0].isEmpty() || args[1].isEmpty()) event.getAnswers().invalidargs().queue();
//				else {
//					args[0] = processId(args[0]);
//					if ("clear".equals(args[1])) {
//						DataManager.data.annoy.remove(args[0]);
//						event.getAnswers().bool( true).queue();
//					}
//					if ("add".equals(args[1])) {
//						if (!DataManager.data.annoy.containsKey(args[0]))
//							DataManager.data.annoy.put(args[0], new ArrayList<>());
//						DataManager.data.annoy.get(args[0]).add(args[2]);
//						event.getAnswers().bool( true).queue();
//					}
//				}
//			}))
//			.build()
//		);
}