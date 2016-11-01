/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:42]
 */

package cf.adriantodt.bot.impl.cmds;

import cf.adriantodt.bot.base.cmd.CommandBuilder;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.data.Feeds;
import cf.adriantodt.bot.impl.ProvidesCommand;
import cf.brforgers.core.lib.IOHelper;

import static cf.adriantodt.bot.base.Permissions.BOT_OWNER;

public class Feed {
	@ProvidesCommand("feed")
	private static ICommand createCommand() {
		return new CommandBuilder(BOT_OWNER)
			.setAction(event -> {
				new Feeds.Subscription(IOHelper.newURL(event.getArgs()), "feed");
				event.awaitTyping().getAnswers().bool(true).queue();
				Feeds.onFeed();
			})
			.build();
	}
}
