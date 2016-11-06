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

package cf.adriantodt.David.modules.cmds;

import cf.adriantodt.David.commands.base.Commands;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.commands.base.ProvidesCommand;
import cf.brforgers.core.lib.IOHelper;
import com.google.common.collect.Sets;

import static cf.adriantodt.David.modules.db.MakePermissionsAModule.BOT_OWNER;

public class FeedCmd {
	@ProvidesCommand("feed")
	private static ICommand createCommand() {
		return Commands.buildSimple("feed.usage",BOT_OWNER)
			.setAction(event -> {
				Pushes.subscribe(event.getChannel(), Sets.newHashSet("feed_" + new Feeds.Subscription(event.getArg(2, 0), IOHelper.newURL(event.getArg(2, 1))).pushName));
				event.awaitTyping().getAnswers().bool(true).queue();
			})
			.build();
	}
}
