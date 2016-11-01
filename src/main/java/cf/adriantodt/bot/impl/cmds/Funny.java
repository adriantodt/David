/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 21:44]
 */

package cf.adriantodt.bot.impl.cmds;

import cf.adriantodt.bot.base.cmd.CommandBuilder;
import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.base.cmd.TreeCommandBuilder;
import cf.adriantodt.bot.impl.ContentManager;
import cf.adriantodt.bot.impl.ProvidesCommand;
import cf.adriantodt.bot.utils.Tasks;
import cf.adriantodt.bot.utils.Utils;
import cf.brforgers.core.lib.IOHelper;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.Future;

import static cf.adriantodt.bot.impl.ContentManager.*;
import static cf.adriantodt.bot.utils.Formatter.italic;
import static cf.adriantodt.bot.utils.Statistics.clampIfNotOwner;
import static cf.adriantodt.bot.utils.Statistics.parseInt;
import static cf.adriantodt.bot.utils.Utils.sleep;

public class Funny {
	@ProvidesCommand("funny")
	private static ICommand createCommand() {
		return new TreeCommandBuilder()
			.addCommand("minecraft", new TreeCommandBuilder()
				.addCommand("drama", new CommandBuilder("funny.minecraft.drama.usage")
					.setAction(event -> {
						int amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor());
						if (amount > 1) {
							event.getAnswers().send(italic("Pulling " + amount + " dramas... This can take a while...")).queue(message -> event.sendTyping().queue());
						}
						for (int i = 0; i < amount; i++)
							Utils.async(() -> {
								Future<String> task = Tasks.getThreadPool().submit(() -> {
									String latestDrama = IOHelper.toString("https://drama.thog.eu/api/drama");
									if ("null".equals(latestDrama)) return "*Failed to retrieve the Drama*";
									return "**Minecrosoft**: *" + latestDrama + "*\n  *(Provided by Minecraft Drama Generator)*";
								});
								while (!task.isDone()) {
									event.awaitTyping();
									event.sendAwaitableTyping();
									sleep(2000);
								}
								try {
									event.getAnswers().send(task.get()).queue();
								} catch (Exception e) {
									LogManager.getLogger("Command: Drama").error("An error ocurred fetching the latest Drama: ", e);
								}
							}).run();
					}).build()
				).build()
			)
			.addCommand("mc", "minecraft")
			.addCommand("stevenuniverse", new TreeCommandBuilder()
				.addCommand("theorygenerator", new CommandBuilder("funny.stevenuniverse.theorygenerator.usage").setAction(event -> {
					if (!ContentManager.SU_THEORIES_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++) {
						String result = "";
						for (String[] theoryArray : SU_THEORIES)
							result = result + theoryArray[(int) Math.floor(Math.random() * theoryArray.length)] + " ";
						event.getAnswers().send("[#" + (i + 1) + "] What if " + result.substring(0, result.length() - 1) + "?").queue();
					}
				}).build())
				.addCommand("theorygen", "theorygenerator")
				.addCommand("stevonnie", new CommandBuilder("funny.stevenuniverse.stevonnie.usage").setAction(event -> {
					if (!ContentManager.SU_STEVONNIE_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++)
						event.getAnswers().send("[#" + (i + 1) + "] " + SU_STEVONNIE[(int) Math.floor(Math.random() * SU_STEVONNIE.length)]).queue();
				}).build())
				.build()
			)
			.addCommand("su", "stevenuniverse")
			.addCommand("skyrim", new TreeCommandBuilder()
				.addCommand("guard", new CommandBuilder("funny.skyrim.guard.usage").setAction(event -> {
					if (!ContentManager.TESV_GUARDS_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++)
						event.getAnswers().send("[#" + (i + 1) + "] " + TESV_GUARDS[(int) Math.floor(Math.random() * TESV_GUARDS.length)]).queue();
				}).build())
				.addCommand("lydia", new CommandBuilder("funny.skyrim.lydia.usage").setAction(event -> {
					if (!ContentManager.TESV_LYDIA_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++)
						event.getAnswers().send("[#" + (i + 1) + "] " + TESV_LYDIA[(int) Math.floor(Math.random() * TESV_LYDIA.length)]).queue();
				}).build())
				.build()
			)
			.build();
	}
}
