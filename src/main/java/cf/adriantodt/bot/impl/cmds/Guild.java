/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:46]
 */

package cf.adriantodt.bot.impl.cmds;

import cf.adriantodt.bot.base.cmd.CommandBuilder;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.base.cmd.TreeCommandBuilder;
import cf.adriantodt.bot.data.Commitable;
import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.data.I18n;
import cf.adriantodt.bot.impl.ProvidesCommand;

import java.util.Arrays;
import java.util.List;

import static cf.adriantodt.bot.base.Permissions.*;
import static cf.adriantodt.bot.data.I18n.getLocalized;

public class Guild {
	@ProvidesCommand("guild")
	private static ICommand createCommand() {
		return new TreeCommandBuilder()
			.addCommand("info",
				new CommandBuilder("guild.info.usage")
					.setAction(event -> event.awaitTyping().getAnswers().sendCased(Guilds.toString(event.getGuild(), event.getJDA(), I18n.getLocale(event))).queue())
					.build()
			)
			.addDefault("info")
			.addCommand("lang",
				new CommandBuilder("guild.lang.usage", SET_GUILD)
					.setAction(event -> {
						event.getGuild().setLang(event.getArgs().isEmpty() ? "en_US" : event.getArgs());
						event.awaitTyping().getAnswers().announce(String.format(getLocalized("guild.lang.set", event), event.getGuild().getLang())).queue();
					}).build()
			)
			.addCommand("cleanup",
				new CommandBuilder("guild.cleanup.usage", SET_GUILD)
					.setAction(event -> event.awaitTyping().getAnswers().bool(event.getGuild().toggleFlag("cleanup")).queue())
					.build()
			)
			.addCommand("prefixes",
				new CommandBuilder("guild.prefixes.usage", SET_GUILD)
					.setAction(event -> {
						if (event.getArgs().trim().isEmpty()) {
							event.awaitTyping().getAnswers().invalidargs().queue();
							return;
						}
						Commitable<List<String>> cmdPrefixesHandler = event.getGuild().modifyCmdPrefixes();
						List<String> cmdPrefixes = cmdPrefixesHandler.get();
						String[] all = event.getArgs(-1);
						for (String each : all) {
							if (each.toLowerCase().equals("+default")) {
								Arrays.asList(Guilds.DEFAULT_PREFIXES).forEach(s -> {
									if (!cmdPrefixes.contains(s)) cmdPrefixes.add(s);
								});
							} else if (each.charAt(0) == '+') {
								String v = each.substring(1);
								if (!cmdPrefixes.contains(v)) cmdPrefixes.add(v);
							} else if (each.toLowerCase().equals("clear")) {
								cmdPrefixes.clear();
							} else if (each.toLowerCase().equals("list") || each.toLowerCase().equals("get")) {
								event.awaitTyping().getAnswers().send(Arrays.toString(cmdPrefixes.toArray())).queue();
							}
						}
						cmdPrefixesHandler.pushChanges();
						event.awaitTyping().getAnswers().bool(true).queue();
					})
					.build()
			)
			.addCommand("perms", new TreeCommandBuilder()
				.addCommand("get", new CommandBuilder("perms.get.usage")
					.setAction(event -> {
						String arg = event.getArgument(1, 0); //!getlevel USER
						if (arg.isEmpty()) arg = event.getAuthor().getId();
						event.getAnswers().send("**" + getLocalized("perms.get.userPerms", event) + ":**\n *" + String.join(", ", toCollection(getPermFor(event.getGuild(), arg)).stream().toArray(String[]::new)) + "*").queue();
					}).build())
				.addCommand("set", new CommandBuilder("perms.set.usage", SET_PERMS)
					.setAction(event -> {
						String[] args = event.getArgs(2); //!setlevel USER LEVEL
						if (args[0].isEmpty() || args[1].isEmpty()) event.getAnswers().invalidargs().queue();
						else {
							String[] all = args[1].split("\\s+", -1);
							int toBeSet = 0, toBeUnset = 0;
							for (String each : all) {
								if (each.charAt(0) == '+') {
									String p = each.substring(1).toUpperCase();
									if (perms.containsKey(p)) toBeSet |= perms.get(p);
								} else if (each.charAt(0) == '-') {
									String p = each.substring(1).toUpperCase();
									if (perms.containsKey(p)) toBeUnset |= perms.get(p);
								}
							}
							event.getAnswers().bool(setPerms(event.getGuild(), event, args[0], toBeSet, toBeUnset)).queue();
						}
					})
					.build()
				)
				.addCommand("list",
					new CommandBuilder("perms.list.usage")
						.setAction(event -> event.getAnswers().send("**" + getLocalized("perms.get.userPerms", event) + ":**\n *" + String.join(", ", toCollection(BOT_OWNER).stream().toArray(String[]::new)) + "*").queue())
						.build()
				)
				.build()
			)
			.build();
	}
}
