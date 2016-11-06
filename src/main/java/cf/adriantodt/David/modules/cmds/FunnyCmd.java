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

package cf.adriantodt.David.modules.cmds;

import cf.adriantodt.David.commands.base.Commands;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.commands.base.ProvidesCommand;

import cf.adriantodt.utils.TaskManager;
import cf.brforgers.core.lib.IOHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import static cf.adriantodt.David.modules.init.Statistics.clampIfNotOwner;
import static cf.adriantodt.David.modules.init.Statistics.parseInt;

import static cf.adriantodt.David.utils.Formatter.italic;
import static cf.adriantodt.utils.AsyncUtils.async;
import static cf.adriantodt.utils.AsyncUtils.sleep;
import static cf.adriantodt.utils.CollectionUtils.random;

public class FunnyCmd {
	private static final Logger LOGGER = LogManager.getLogger("ContentManager");
	public static String[][][] SU_THEORIES;
	public static String[] TESV_GUARDS, SU_STEVONNIE, TESV_LYDIA;
	public static boolean SU_THEORIES_LOADED = false, TESV_GUARDS_LOADED = false, SU_STEVONNIE_LOADED = false, TESV_LYDIA_LOADED = false;

	static {
		reload();
	}

	public static void reload() {
		try {
			TESV_GUARDS = resource("/assets/funny/skyrim_guards.txt").split("\\r?\\n");
			TESV_GUARDS_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"skyrim_guards.txt\" resource.", e);
		}

		try {
			TESV_LYDIA = resource("/assets/funny/skyrim_lydia.txt").split("\\r?\\n");
			TESV_LYDIA_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"skyrim_lydia.txt\" resource.", e);
		}

		try {
			JsonObject object = new JsonParser().parse(resource("/assets/funny/stevenuniverse_theories.json")).getAsJsonObject();
			List<List<List<String>>> SU_THEORIES_BUILD = Arrays.asList(Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),Arrays.asList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
			object.get("characters").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
			});

			object.get("places").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString());
			});

			object.get("objects").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString());
			});

			object.get("gems").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString() + "'s room");
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString() + "'s room");
			});

			object.get("fusionGems").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(0).add(element.getAsString());
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString() + "'s room");
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString() + "'s room");
				SU_THEORIES_BUILD.get(1).get(0).add(element.getAsString() + "'s fusion realm");
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString() + "'s fusion realm");
			});

			object.get("verb").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(1).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(1).add(element.getAsString());
			});
			object.get("revelation4characters").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(2).add(element.getAsString());
			});
			object.get("revelation4places").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(1).get(2).add(element.getAsString());
			});
			object.get("post").getAsJsonArray().forEach(element -> {
				SU_THEORIES_BUILD.get(0).get(3).add(element.getAsString());
				SU_THEORIES_BUILD.get(1).get(3).add(element.getAsString());
			});

			SU_THEORIES = SU_THEORIES_BUILD.stream().map(ll -> ll.stream().map(l -> l.stream().toArray(String[]::new)).toArray(String[][]::new)).toArray(String[][][]::new);
			SU_THEORIES_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"stevenuniverse_theories.json\" resource.", e);
		}

		try {
			SU_STEVONNIE = resource("/assets/funny/stevenuniverse_stevonnie.txt").split("\\r?\\n");
			SU_STEVONNIE_LOADED = true;
		} catch (Exception e) {
			LOGGER.error("Error while parsing \"stevenuniverse_stevonnie.txt\" resource.", e);
		}
	}

	@Command("funny")
	private static ICommand createCommand() {
		return Commands.buildTree()
			.addCommand("minecraft", Commands.buildTree()
				.addCommand("drama", Commands.buildSimple("funny.minecraft.drama.usage")
					.setAction(event -> {
						int amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor());
						if (amount > 1) {
							event.getAnswers().send(italic("Pulling " + amount + " dramas... This can take a while...")).queue(message -> event.sendTyping().queue());
						}
						for (int i = 0; i < amount; i++)
							async(() -> {
								Future<String> task = TaskManager.getThreadPool().submit(() -> {
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
			.addCommand("stevenuniverse", Commands.buildTree()
				.addCommand("theorygenerator", Commands.buildSimple("funny.stevenuniverse.theorygenerator.usage").setAction(event -> {
					if (!ContentManager.SU_THEORIES_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++) {
						String result = "";
						for (String[] theoryArray : random(SU_THEORIES))
							result = result + random(theoryArray) + " ";
						event.getAnswers().send("[#" + (i + 1) + "] What if " + result.trim() + "?").queue();
					}
				}).build())
				.addCommand("theorygen", "theorygenerator")
				.addCommand("stevonnie", Commands.buildSimple("funny.stevenuniverse.stevonnie.usage").setAction(event -> {
					if (!ContentManager.SU_STEVONNIE_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++)
						event.getAnswers().send("[#" + (i + 1) + "] " + random(SU_STEVONNIE)).queue();
				}).build())
				.build()
			)
			.addCommand("su", "stevenuniverse")
			.addCommand("skyrim", Commands.buildTree()
				.addCommand("guard", Commands.buildSimple("funny.skyrim.guard.usage").setAction(event -> {
					if (!ContentManager.TESV_GUARDS_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++)
						event.getAnswers().send("[#" + (i + 1) + "] " + random(TESV_GUARDS)).queue();
				}).build())
				.addCommand("lydia", Commands.buildSimple("funny.skyrim.lydia.usage").setAction(event -> {
					if (!ContentManager.TESV_LYDIA_LOADED) {
						event.awaitTyping();
						event.getAnswers().sendTranslated("error.contentmanager").queue();
						return;
					}
					for (int i = 0, amount = clampIfNotOwner(parseInt(event.getArgs(), 1), 0, 10, event.getAuthor()); i < amount; i++)
						event.getAnswers().send("[#" + (i + 1) + "] " + random(TESV_LYDIA)).queue();
				}).build())
				.build()
			)
			.build();
	}
}
