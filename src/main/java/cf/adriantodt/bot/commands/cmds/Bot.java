/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:47]
 */

package cf.adriantodt.bot.commands.cmds;

import cf.adriantodt.bot.BotGreeter;
import cf.adriantodt.bot.BotInfo;
import cf.adriantodt.bot.commands.CommandManager;
import cf.adriantodt.bot.commands.base.Commands;
import cf.adriantodt.bot.commands.base.ICommand;
import cf.adriantodt.bot.commands.base.ProvidesCommand;
import cf.adriantodt.bot.commands.utils.Statistics;
import cf.adriantodt.bot.commands.utils.scripting.JS;
import cf.adriantodt.bot.data.entities.I18n;
import net.dv8tion.jda.core.JDAInfo;

import java.util.Optional;

import static cf.adriantodt.bot.commands.Permissions.*;
import static cf.adriantodt.bot.data.entities.I18n.getLocalized;

public class Bot {
	@ProvidesCommand("bot")
	private static ICommand createCommand() {
		return Commands.buildTree(RUN_CMDS)
			.addCommand("info",
				Commands.buildSimple("bot.info.usage").setAction((event) -> BotGreeter.greet(event.getChannel(), Optional.of(event.getAuthor()))).build()
			)
			.addDefault("info")
			.addCommand("version", Commands.buildSimple("bot.version.usage").setAction(e -> e.getAnswers().send("**Bot Version:** " + BotInfo.VERSION + "\n**JDA Version** " + JDAInfo.VERSION).queue()).build())
			.addCommand("stop",
				Commands.buildSimple("bot.stop.usage", STOP_BOT)
					.setAction(event -> {
						event.getAnswers().announce(I18n.getLocalized("bot.stop", event)).queue();
						cf.adriantodt.bot.Bot.stopBot();
					})
					.build()
			)
			.addCommand("toofast",
				Commands.buildSimple("bot.toofast.usage", BOT_OWNER)
					.setAction((event) -> event.getAnswers().bool(CommandManager.toofast = !CommandManager.toofast).queue()).build()
			)
			.addCommand("stats",
				Commands.buildSimple("bot.stats.usage").setAction(Statistics::printStats).build()
			)
			.addCommand("inviteme",
				Commands.buildSimple("inviteme.usage")
					.setAction(event -> event.getAnswers().send("**" + getLocalized("inviteme.link", event) + ":**\nhttps://discordapp.com/oauth2/authorize?client_id=" + event.getJDA().getSelfInfo().getId() + "&scope=bot").queue())
					.build()
			)
			.addCommand("administration", Commands.buildTree()
				.build()
			)
			.addCommand("eval",
				Commands.buildSimple("eval.usage", SCRIPTS | RUN_SCRIPT_CMDS | SCRIPTS_UNSAFEENV)
					.setAction(JS::eval)
					.build()
			)
			.build();
	}
}
