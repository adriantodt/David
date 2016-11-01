/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:43]
 */

package cf.adriantodt.bot.commands.cmds;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.commands.base.Commands;
import cf.adriantodt.bot.commands.base.Holder;
import cf.adriantodt.bot.commands.base.ICommand;
import cf.adriantodt.bot.commands.base.ProvidesCommand;
import cf.adriantodt.bot.data.entities.Users;
import cf.adriantodt.bot.utils.DiscordUtils;

import static cf.adriantodt.bot.data.entities.I18n.getLocale;
import static cf.adriantodt.bot.data.entities.I18n.getLocalized;

public class User {
	@ProvidesCommand("user")
	private static ICommand createCommand() {
		return Commands.buildTree()
			.addCommand("info", Commands.buildSimple("user.info.usage")
				.setAction(event -> {
					String[] users = event.getArgs(0);
					if (users.length == 0) {
						users = new String[]{event.getAuthor().getId()};
					}

					Holder<Boolean> any = new Holder<>(false);

					for (String userId : users) {
						net.dv8tion.jda.core.entities.User user = event.getJDA().getUserById(DiscordUtils.processId(userId));
						if (user == null) continue;
						any.var = true;
						event.awaitTyping().getAnswers().send(
							user.getAsMention() + ": \n" + getLocalized("user.avatar", event) + ": " + user.getAvatarUrl() + "\n```" +
								Users.toString(Users.fromDiscord(user), Bot.API, getLocale(event), event.getGuild().getGuild()) +
								"\n```"
						).queue();
					}

					if (!any.var) {
						net.dv8tion.jda.core.entities.User user = event.getAuthor();
						any.var = true;
						event.awaitTyping().getAnswers().send(
							user.getAsMention() + ": \n" + getLocalized("user.avatar", event) + ": " + user.getAvatarUrl() + "\n```" +
								Users.toString(Users.fromDiscord(user), Bot.API, getLocale(event), event.getOriginGuild()) +
								"\n```"
						).queue();
					}

				}).build()
			)
			.addCommand("lang",
				Commands.buildSimple("user.lang.usage")
					.setAction(event -> {
						Users.fromDiscord(event.getAuthor()).setLang(event.getArgs().trim());
						if (event.getArgs().trim().isEmpty())
							event.getAnswers().announce(getLocalized("lang.setNone", event)).queue();
						else
							event.getAnswers().announce(String.format(getLocalized("lang.set", event), event.getArgs().trim())).queue();
					})
					.build()
			)
			.addDefault("info")
			.build();
	}
}
