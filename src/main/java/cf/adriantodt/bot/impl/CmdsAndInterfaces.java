/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:15]
 */

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.BotInfo;
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.base.cmd.*;
import cf.adriantodt.bot.data.*;
import cf.adriantodt.bot.handlers.BotGreeter;
import cf.adriantodt.bot.handlers.CommandHandler;
import cf.adriantodt.bot.handlers.scripting.JS;
import cf.adriantodt.bot.utils.*;
import cf.brforgers.core.lib.IOHelper;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cf.adriantodt.bot.base.Permissions.*;
import static cf.adriantodt.bot.data.I18n.getLocale;
import static cf.adriantodt.bot.data.I18n.getLocalized;
import static cf.adriantodt.bot.impl.ContentManager.*;
import static cf.adriantodt.bot.utils.Commands.*;
import static cf.adriantodt.bot.utils.Formatter.encase;
import static cf.adriantodt.bot.utils.Formatter.italic;
import static cf.adriantodt.bot.utils.Statistics.clampIfNotOwner;
import static cf.adriantodt.bot.utils.Statistics.parseInt;
import static cf.adriantodt.bot.utils.Utils.sleep;

public class CmdsAndInterfaces {
	public static void impl() {
		addCommand("user", new TreeCommandBuilder()
			.addCommand("info", new CommandBuilder("user.info.usage")
				.setAction(event -> {
					String[] users = event.getArgs(0);
					for (String userId : users) {
						User user = event.getJDA().getUserById(Utils.processId(userId));
						if (user == null) continue;

						event.getAnswers().send(
							user.getAsMention() + ": \n" + getLocalized("user.avatar", event) + ": " + user.getAvatarUrl() + "\n```" +
								Users.toString(Users.fromDiscord(user), Bot.API, getLocale(event), event.getOriginGuild()) +
								"\n```"
						).queue();
					}
				}).build()
			)
			.addCommand("lang",
				new CommandBuilder("user.lang.usage")
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
			.build()
		);

		addCommand("funny", new TreeCommandBuilder()
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
				.build()
			)
			.build()
		);

		//implDrama
		;

		//implSUTheory

		//implAnnoy
//		addCommand("annoy", new CommandBuilder()
//			.setPermRequired(ANNOY)
//			.setUsageDeprecatedMethod("")
//			.setAction(((arguments, event) -> {
//				String[] args = splitArgs(arguments, 3); //!spysend CH MSG
//				if (args[0].isEmpty() || args[1].isEmpty()) event.getAnswers().invalidargs().queue();
//				else {
//					args[0] = processId(args[0]);
//					if ("clear".equals(args[1])) {
//						DataManager.data.annoy.remove(args[0]);
//						event.getAnswers().bool( true).queue();
//					}
//					if ("add".equals(args[1])) {
//						if (!DataManager.data.annoy.containsKey(args[0]))
//							DataManager.data.annoy.put(args[0], new ArrayList<>());
//						DataManager.data.annoy.get(args[0]).add(args[2]);
//						event.getAnswers().bool( true).queue();
//					}
//				}
//			}))
//			.build()
//		);

		addCommand("guild", new TreeCommandBuilder()
			.addCommand("info",
				new CommandBuilder("guild.info.usage")
					.setAction(event -> event.getAnswers().sendCased(Guilds.toString(event.getGuild(), event.getJDA(), I18n.getLocale(event))).queue())
					.build()
			)
			.addDefault("info")
			.addCommand("lang",
				new CommandBuilder("guild.lang.usage", SET_GUILD)
					.setAction(event -> {
						event.getGuild().setLang(event.getArgs().isEmpty() ? "en_US" : event.getArgs());
						event.getAnswers().announce(String.format(getLocalized("guild.lang.set", event), event.getGuild().getLang())).queue();
					}).build()
			)
			.addCommand("cleanup",
				new CommandBuilder("guild.cleanup.usage", SET_GUILD)
					.setAction(event -> event.getAnswers().bool(event.getGuild().toggleFlag("cleanup")).queue())
					.build()
			)
			.addCommand("prefixes",
				new CommandBuilder("perms.set.usage", SET_GUILD)
					.setAction(event -> {
						if (event.getArgs().trim().isEmpty()) {
							event.getAnswers().invalidargs().queue();
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
								event.getAnswers().send(Arrays.toString(cmdPrefixes.toArray())).queue();
							}
						}
						cmdPrefixesHandler.pushChanges();
						event.getAnswers().bool(true).queue();
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
									if (perms.containsKey(p)) {
										toBeSet |= perms.get(p);
									}
								} else if (each.charAt(0) == '-') {
									String p = each.substring(1).toUpperCase();
									if (perms.containsKey(p)) {
										toBeUnset |= perms.get(p);
									}
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
			.build()
		);

		addCommand("bot",
			new TreeCommandBuilder(RUN_CMDS)
				.addCommand("info",
					new CommandBuilder("bot.info.usage").setAction((event) -> BotGreeter.greet(event.getChannel(), Optional.of(event.getAuthor()))).build()
				)
				.addDefault("info")
				.addCommand("version", new CommandBuilder("bot.version.usage").setAction(e -> e.getAnswers().send("**Bot Version:** " + BotInfo.VERSION + "\n**JDA Version** " + JDAInfo.VERSION).queue()).build())
				.addCommand("stop",
					new CommandBuilder("bot.stop.usage", STOP_BOT)
						.setAction(event -> {
							event.getAnswers().announce(I18n.getLocalized("bot.stop", event)).queue();
							Bot.stopBot();
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
				.build()
		);

		addCommand("push",
			new TreeCommandBuilder()
				.addCommand("subscribe", new CommandBuilder(PUSH_SUBSCRIBE)
					.setAction(event -> {
						Push.subscribe(event.getChannel(), Arrays.asList(event.getArgs(0)));
						event.awaitTyping().getAnswers().bool(true).queue();
					})
					.build()
				)
				.addCommand("unsubscribe", new CommandBuilder(PUSH_SUBSCRIBE)
					.setAction(event -> {
						Push.unsubscribe(event.getChannel(), Arrays.asList(event.getArgs(0)));
						event.awaitTyping().getAnswers().bool(true).queue();
					})
					.build()
				)
				.addCommand("send", new CommandBuilder(PUSH_SEND)
					.setAction(event -> {
						Push.pushSimple(event.getArgument(2, 0), (channel) -> event.getArgument(2, 1));
						event.awaitTyping().getAnswers().bool(true).queue();
					})
					.build()
				)
				.build()
		);

		addCommand("cmds",
			new TreeCommandBuilder()
				.addCommand("list",
					new CommandBuilder("cmds.list.usage").setAction(event -> {
						List<String> cmds, userCmds, tmp = new ArrayList<>();

						cmds = getCommands(event.getGuild()).entrySet().stream().filter(entry -> canRunCommand(event.getGuild(), event.createChild(entry.getValue(), event.getArgs()))).filter(entry -> {
							if (entry.getValue() instanceof UserCommand) {
								tmp.add(entry.getKey());
								return false;
							}
							return true;
						}).map(Map.Entry::getKey).sorted(String::compareTo).collect(Collectors.toList());

						userCmds = tmp.stream().sorted(String::compareTo).collect(Collectors.toList());

						Holder<StringBuilder> b = new Holder<>();
						Holder<Boolean> first = new Holder<>();

						b.var = new StringBuilder("**Comandos Disponíveis:**\n *");
						first.var = true;
						cmds.forEach(s -> {
							if (first.var) {
								first.var = false;
								b.var.append(s);
							} else {
								String a = " " + s;
								if (b.var.length() + a.length() >= 1999) {
									b.var.append("*");
									event.getAnswers().send(b.var.toString()).queue();
									b.var = new StringBuilder("*");
								}
								b.var.append(a);
							}

						});
						if (first.var) b.var.append("(nenhum comando disponível)");
						b.var.append("*");
						event.getAnswers().send(b.var.toString()).queue();

						b.var = new StringBuilder("**Comandos de Usuário Disponíveis:**\n *");
						first.var = true;

						userCmds.forEach(s -> {
							if (first.var) {
								first.var = false;
								b.var.append(s);
							} else {
								String a = " " + s;
								if (b.var.length() + a.length() >= 1999) {
									b.var.append("*");
									event.getAnswers().send(b.var.toString()).queue();
									b.var = new StringBuilder("*");
								}
								b.var.append(a);
							}

						});
						if (first.var) b.var.append("(nenhum comando disponível)");
						b.var.append("*");
						event.getAnswers().send(b.var.toString()).queue();
					}).build())
				.addDefault("list")
				.addCommand("detailed", new CommandBuilder("cmds.detailed.usage").setAction(event -> {
					if (!event.getAuthor().hasPrivateChannel()) {
						try {
							event.getAuthor().openPrivateChannel().block();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}

					MessageChannel channel = event.getAuthor().getPrivateChannel();
					List<String> cmds = getBaseCommands().entrySet().stream().filter(entry -> canRunCommand(event.getGuild(), event.createChild(entry.getValue(), event.getArgs()))).map(
						(entry) -> entry.getKey() + " - " + entry.getValue().toString(I18n.getLocale(event))).sorted(String::compareTo).collect(Collectors.toList());

					Holder<StringBuilder> b = new Holder<>();
					Holder<Boolean> first = new Holder<>();

					b.var = new StringBuilder("**Comandos:**\n");
					first.var = true;
					cmds.forEach(s -> {
						String v = encase(s);
						if (first.var) {
							first.var = false;
							b.var.append(v);
						} else {
							if (b.var.length() + v.length() >= 1995) {
								channel.sendMessage(b.var.toString()).queue();
								b.var = new StringBuilder();
							}
							b.var.append(v);
						}

					});
					if (first.var) b.var.append("(nenhum comando disponível)");
					channel.sendMessage(b.var.toString()).queue();
					event.getAnswers().send(event.getAuthor().getAsMention() + " :mailbox_with_mail:").queue();
				}).build())
				.addCommand("add", new CommandBuilder("cmds.add.usage", MANAGE_USER_CMDS)
					.setAction(event -> {
						String[] args = event.getArgs(2); //COMMAND_NAME RESPONSE
						if (args[0].isEmpty() | args[1].isEmpty()) event.getAnswers().invalidargs().queue();
						else {
							if (Stream.of("loc://", "js://", "aud://").anyMatch(args[1]::startsWith) && !Permissions.havePermsRequired(event.getGuild(), event.getAuthor(), MANAGE_SPECIAL_USER_CMDS)) {
								event.awaitTyping().getAnswers().noperm(MANAGE_SPECIAL_USER_CMDS).queue();
								return;
							}

							UserCommand cmd = getLocalUserCommands(event.getGuild()).get(args[0].toLowerCase());
							if (cmd == null) {
								UserCommand ncmd = new UserCommand();
								ncmd.responses.add(args[1]);
								UserCommands.register(ncmd, args[0].toLowerCase(), event.getGuild());
								event.getAnswers().bool(true).queue();
							} else {
								cmd.responses.add(args[1]);
								UserCommands.update(cmd);
								event.getAnswers().bool(true).queue();
							}
						}
					}).build())
				.addCommand("rm", new CommandBuilder("cmds.add.usage", MANAGE_USER_CMDS)
					.setAction(event -> {
						if (event.getArgs().trim().isEmpty()) event.getAnswers().invalidargs().queue();
						else {
							UserCommand command = getLocalUserCommands(event.getGuild()).get(event.getArgs().toLowerCase());
							if (command == null) event.getAnswers().invalidargs().queue();
							else {
								UserCommands.remove(command);
								event.getAnswers().bool(true).queue();
							}
						}
					}).build())
				.addCommand("debug", new CommandBuilder("cmds.debug.usage", MANAGE_USER_CMDS).setAction(event -> {
					if (event.getArgs().trim().isEmpty()) event.getAnswers().invalidargs().queue();
					else {
						ICommand cmd = Commands.getCommands(event.getGuild()).get(event.getArgs());
						if (cmd == null) event.getAnswers().invalidargs().queue();
						else
							event.getAnswers().send("***Debug do Comando `" + event.getArgs() + "`:*** " + cmd.toString(I18n.getLocale(event))).queue();
					}
				}).build())
				.build()
		);
	}
}