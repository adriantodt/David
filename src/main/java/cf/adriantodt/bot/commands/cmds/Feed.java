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

package cf.adriantodt.bot.commands.cmds;

import cf.adriantodt.bot.commands.base.Commands;
import cf.adriantodt.bot.commands.base.ICommand;
import cf.adriantodt.bot.commands.base.ProvidesCommand;
import cf.adriantodt.bot.data.entities.Feeds;
import cf.adriantodt.bot.data.entities.Pushes;
import cf.brforgers.core.lib.IOHelper;
import com.google.common.collect.Sets;

import static cf.adriantodt.bot.commands.Permissions.BOT_OWNER;

public class Feed {
	@ProvidesCommand("feed")
	private static ICommand createCommand() {
		return Commands.buildSimple(BOT_OWNER)
			.setAction(event -> {
				Feeds.whileOnLock(() -> Pushes.subscribe(event.getChannel(), Sets.newHashSet("feed_" + new Feeds.Subscription(IOHelper.newURL(event.getArg(2, 1)), event.getArg(2, 0)).pushName)));
				event.awaitTyping().getAnswers().bool(true).queue();
			})
			.build();
	}
}
