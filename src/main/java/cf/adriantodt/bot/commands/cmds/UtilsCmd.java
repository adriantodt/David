/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/11/16 20:14]
 */

package cf.adriantodt.bot.commands.cmds;

import cf.adriantodt.bot.commands.base.Commands;
import cf.adriantodt.bot.commands.base.ICommand;
import cf.adriantodt.bot.commands.base.ProvidesCommand;
import cf.adriantodt.bot.data.entities.FeedingUtil;
import cf.adriantodt.utils.HTML2Discord;

public class UtilsCmd {
	@ProvidesCommand("utils")
	private static ICommand utils() {
		return Commands.buildTree()
			.addCommand("convert", Commands.buildTree()
				.addCommand("html2md", Commands.buildSimple()
					.setAction(event -> event.awaitTyping().sendMessage(HTML2Discord.toDiscordFormat(event.getArgs())).queue())
					.build()
				)
				.addCommand("md2text", Commands.buildSimple()
					.setAction(event -> event.awaitTyping().sendMessage(HTML2Discord.toPlainText(event.getArgs())).queue())
					.build()
				)
				.addCommand("html2text", Commands.buildSimple()
					.setAction(event -> event.awaitTyping().sendMessage(HTML2Discord.toPlainText(HTML2Discord.toDiscordFormat(event.getArgs()))).queue())
					.build()
				)
				.build()
			)
			.addCommand("shorten", Commands.buildSimple()
				.setAction(event -> {
					String r;
					if (event.getArgs(0).length == 1) {
						r = FeedingUtil.shorten(event.getArgs());
					} else if (event.getArgs(0).length == 2) {
						r = FeedingUtil.shorten(event.getArg(2,0), event.getArg(2,1));
					} else {
						event.awaitTyping().getAnswers().invalidargs().queue();
						return;
					}
					event.awaitTyping().getAnswers().bool(!r.contains("Error:"),": " + r).queue();
				})
				.build()
			)
			.build();
	}
}
