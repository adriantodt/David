/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [11/11/16 08:16]
 */

package cf.adriantodt.David.modules.cmds;


import cf.adriantodt.David.commands.base.Commands;
import cf.adriantodt.David.commands.base.Holder;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.Command;
import cf.adriantodt.David.loader.Module.Type;
import cf.adriantodt.David.modules.db.UserModule;
import cf.adriantodt.David.utils.DiscordUtils;

import static cf.adriantodt.David.modules.db.I18nModule.getLocale;
import static cf.adriantodt.David.modules.db.I18nModule.getLocalized;

@Module(name = "cmds.user", type = Type.STATIC)
public class UserCmd {
	@Command("user")
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
								UserModule.toString(UserModule.fromDiscord(user), event.getJDA(), getLocale(event), event.getGuild().getGuild(event.getJDA())) +
								"\n```"
						).queue();
					}

					if (!any.var) {
						net.dv8tion.jda.core.entities.User user = event.getAuthor();
						any.var = true;
						event.awaitTyping().getAnswers().send(
							user.getAsMention() + ": \n" + getLocalized("user.avatar", event) + ": " + user.getAvatarUrl() + "\n```" +
								UserModule.toString(UserModule.fromDiscord(user), event.getJDA(), getLocale(event), event.getGuild().getGuild(event.getJDA())) +
								"\n```"
						).queue();
					}

				}).build()
			)
			.addCommand("lang",
				Commands.buildSimple("user.lang.usage")
					.setAction(event -> {
						UserModule.fromDiscord(event.getAuthor()).setLang(event.getArgs().trim());
						if (event.getArgs().trim().isEmpty())
							event.getAnswers().announce(getLocalized("user.lang.setNone", event)).queue();
						else
							event.getAnswers().announce(String.format(getLocalized("user.lang.set", event), event.getArgs().trim())).queue();
					})
					.build()
			)
			.addDefault("info")
			.build();
	}
}
