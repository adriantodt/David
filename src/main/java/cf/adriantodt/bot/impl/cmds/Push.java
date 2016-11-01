/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:49]
 */

package cf.adriantodt.bot.impl.cmds;

import cf.adriantodt.bot.base.cmd.CommandBuilder;
import cf.adriantodt.bot.base.cmd.Holder;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.base.cmd.TreeCommandBuilder;
import cf.adriantodt.bot.impl.ProvidesCommand;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static cf.adriantodt.bot.base.Permissions.PUSH_SEND;
import static cf.adriantodt.bot.base.Permissions.PUSH_SUBSCRIBE;
import static cf.adriantodt.bot.data.I18n.getLocalized;

public class Push {
	@ProvidesCommand("guild")
	private static ICommand createCommand() {
		return new TreeCommandBuilder()
			.addCommand("subscribe", new CommandBuilder("push.subscribe.usage", PUSH_SUBSCRIBE)
				.setAction(event -> {
					Set<String> args = new HashSet<>();
					Collections.addAll(args, event.getArgs(0));
					event.awaitTyping().getAnswers().bool(cf.adriantodt.bot.data.Push.subscribe(event.getChannel(), args)).queue();
				})
				.build()
			)
			.addCommand("unsubscribe", new CommandBuilder("push.unsubscribe.usage", PUSH_SUBSCRIBE)
				.setAction(event -> {
					Set<String> args = new HashSet<>();
					Collections.addAll(args, event.getArgs(0));
					event.awaitTyping().getAnswers().bool(cf.adriantodt.bot.data.Push.unsubscribe(event.getChannel(), args)).queue();
				})
				.build()
			)
			.addCommand("send", new CommandBuilder("push.send.usage", PUSH_SEND)
				.setAction(event -> {
					cf.adriantodt.bot.data.Push.pushSimple(event.getArgument(2, 0), (channel) -> event.getArgument(2, 1));
					event.awaitTyping().getAnswers().bool(true).queue();
				})
				.build()
			)
			.addCommand("list", new CommandBuilder("push.list.usage")
				.setAction(event -> {
					Set<String> subscribed = new TreeSet<>(cf.adriantodt.bot.data.Push.subscriptionsFor(event.getChannel())), all = new TreeSet<>(cf.adriantodt.bot.data.Push.resolveTypeSet());
					Holder<StringBuilder> b = new Holder<>(new StringBuilder().append("**").append(getLocalized("push.list", event)).append(":**\n "));
					Holder<Boolean> first = new Holder<>(true);
					first.var = true;
					all.forEach(s -> {
						if (subscribed.contains(s)) s = "**" + s + "**";
						if ("*****".equals(s)) s = "*";

						if (first.var) {
							first.var = false;
							b.var.append(s);
						} else {
							String a = " " + s;
							if (b.var.length() + a.length() >= 1999) {
								event.awaitTyping().getAnswers().send(b.var.toString()).queue();
								b.var = new StringBuilder();
							}
							b.var.append(a);
						}
					});
					if (first.var) b.var.append("(").append(getLocalized("push.none", event)).append(")");
					event.awaitTyping().getAnswers().send(b.var.toString()).queue();
				})
				.build()
			)
			.addDefault("list")
			.build();
	}
}
