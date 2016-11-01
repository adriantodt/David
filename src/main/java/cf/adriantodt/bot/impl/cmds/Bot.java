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

package cf.adriantodt.bot.impl.cmds;

import cf.adriantodt.bot.BotInfo;
import cf.adriantodt.bot.base.cmd.CommandBuilder;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.base.cmd.TreeCommandBuilder;
import cf.adriantodt.bot.data.I18n;
import cf.adriantodt.bot.handlers.BotGreeter;
import cf.adriantodt.bot.handlers.CommandHandler;
import cf.adriantodt.bot.handlers.scripting.JS;
import cf.adriantodt.bot.impl.ProvidesCommand;
import cf.adriantodt.bot.utils.Statistics;
import net.dv8tion.jda.core.JDAInfo;

import java.util.Optional;

import static cf.adriantodt.bot.base.Permissions.*;
import static cf.adriantodt.bot.data.I18n.getLocalized;

public class Bot {
	@ProvidesCommand("bot")
	private static ICommand createCommand() {
		return new TreeCommandBuilder(RUN_CMDS)
			.addCommand("info",
				new CommandBuilder("bot.info.usage").setAction((event) -> BotGreeter.greet(event.getChannel(), Optional.of(event.getAuthor()))).build()
			)
			.addDefault("info")
			.addCommand("version", new CommandBuilder("bot.version.usage").setAction(e -> e.getAnswers().send("**Bot Version:** " + BotInfo.VERSION + "\n**JDA Version** " + JDAInfo.VERSION).queue()).build())
			.addCommand("stop",
				new CommandBuilder("bot.stop.usage", STOP_BOT)
					.setAction(event -> {
						event.getAnswers().announce(I18n.getLocalized("bot.stop", event)).queue();
						cf.adriantodt.bot.Bot.stopBot();
					})
					.build()
			)
			.addCommand("toofast",
				new CommandBuilder("bot.toofast.usage", BOT_OWNER)
					.setAction((event) -> event.getAnswers().bool(CommandHandler.toofast = !CommandHandler.toofast).queue()).build()
			)
			.addCommand("stats",
				new CommandBuilder("bot.stats.usage").setAction(Statistics::printStats).build()
			)
			.addCommand("inviteme",
				new CommandBuilder("inviteme.usage")
					.setAction(event -> event.getAnswers().send("**" + getLocalized("inviteme.link", event) + ":**\nhttps://discordapp.com/oauth2/authorize?client_id=" + event.getJDA().getSelfInfo().getId() + "&scope=bot").queue())
					.build()
			)
			.addCommand("administration", new TreeCommandBuilder()
				.build()
			)
			.addCommand("eval",
				new CommandBuilder("eval.usage", SCRIPTS | RUN_SCRIPT_CMDS | SCRIPTS_UNSAFEENV)
					.setAction(JS::eval)
					.build()
			)
			.build();
	}
}
