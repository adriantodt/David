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

package cf.adriantodt.David.modules.cmds;

import cf.adriantodt.David.Info;
import cf.adriantodt.David.commands.base.Commands;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.modules.init.Statistics;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.Command;
import cf.adriantodt.David.loader.Module.SubscribeJDA;
import cf.adriantodt.David.loader.Module.Type;
import cf.adriantodt.David.modules.cmds.utils.scripting.JS;
import cf.adriantodt.David.modules.init.InitModule;
import net.dv8tion.jda.core.JDAInfo;

import java.util.Optional;

import static cf.adriantodt.David.modules.db.I18nModule.getLocalized;
import static cf.adriantodt.David.modules.db.PermissionsModule.*;

@Module(Type.STATIC)
@SubscribeJDA
public class BotCmd {
	@Command("bot")
	private static ICommand createCommand() {
		return Commands.buildTree(RUN_CMDS)
			.addCommand("info",
				Commands.buildSimple("bot.info.usage").setAction((event) -> BotGreeter.greet(event.getChannel(), Optional.of(event.getAuthor()))).build()
			)
			.addDefault("info")
			.addCommand("version", Commands.buildSimple("bot.version.usage").setAction(e -> e.getAnswers().send("**Bot Version:** " + Info.VERSION + "\n**JDA Version** " + JDAInfo.VERSION).queue()).build())
			.addCommand("stop",
				Commands.buildSimple("bot.stop.usage", STOP_BOT)
					.setAction(event -> {
						event.getAnswers().announce(getLocalized("bot.stop", event)).queue();
						InitModule.stopBot();
					})
					.build()
			)
			.addCommand("enabled",
				Commands.buildSimple("bot.enabled.usage", BOT_OWNER)
					.setAction((event) -> event.getAnswers().bool(MakeCommandManagerAModule.toofast = !MakeCommandManagerAModule.toofast).queue()).build()
			)
			.addCommand("session",
				Commands.buildSimple("bot.session.usage").setAction(Statistics::printStats).build()
			)
			.addCommand("inviteme",
				Commands.buildSimple("bot.inviteme.usage")
					.setAction(event -> event.getAnswers().send("**" + getLocalized("bot.inviteme.link", event) + ":**\nhttps://discordapp.com/oauth2/authorize?client_id=" + event.getJDA().getSelfUser().getId() + "&scope=bot").queue())
					.build()
			)
			.addCommand("administration", Commands.buildTree()
				.build()
			)
			.addCommand("eval",
				Commands.buildSimple("bot.eval.usage", SCRIPTS | RUN_SCRIPT_CMDS | SCRIPTS_UNSAFEENV)
					.setAction(JS::eval)
					.build()
			)
			.build();
	}
}
