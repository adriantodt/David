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

package cf.adriantodt.bot.hardimpl;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.base.cmd.*;
import cf.adriantodt.bot.data.*;
import cf.adriantodt.bot.handlers.BotGreeter;
import cf.adriantodt.bot.handlers.CommandHandler;
import cf.adriantodt.bot.utils.Channels;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Tasks;
import cf.adriantodt.bot.utils.Utils;
import cf.brforgers.core.lib.IOHelper;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static cf.adriantodt.bot.base.Permissions.*;
import static cf.adriantodt.bot.data.I18n.getLocale;
import static cf.adriantodt.bot.data.I18n.getLocalized;
import static cf.adriantodt.bot.utils.Answers.*;
import static cf.adriantodt.bot.utils.Commands.*;
import static cf.adriantodt.bot.utils.Statistics.parseInt;
import static cf.adriantodt.bot.utils.Utils.sleep;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class CmdsAndInterfaces {
	public static void impl() {
		//implUser
		addCommand("user", new CommandBuilder("cmd.user")
			.setAction((g, a, e) -> {
				String[] users = a.split("\\s+", 0);

				for (String userId : users) {
					User user = e.getJDA().getUserById(Utils.processId(userId));
					if (user == null) continue;

					send(e,
						user.getAsMention() + ": \n" + getLocalized("user.avatar", e) + ": " + user.getAvatarUrl() + "\n```" +
							Users.toString(Users.fromDiscord(user), Bot.API, getLocale(e), g.getGuild()) +
							"\n```"
					).queue();
				}
			}).build()
		);

		//implDrama
		addCommand("mcdrama", new CommandBuilder("cmd.mcdrama").setAction((args, event) -> {
			for (int i = 0, amount = Math.min(parseInt(args, 1), 5); i < amount; i++)
				Utils.async(() -> {
					Future<String> task = Tasks.getThreadPool().submit(() -> {
						String latestDrama = IOHelper.toString("https://drama.thog.eu/api/drama");
						if ("null".equals(latestDrama)) return "*Failed to retrieve the Drama*";
						return "**Minecrosoft**: *" + latestDrama + "*\n  *(Provided by Minecraft Drama Generator)*";
					});
					while (!task.isDone()) {
						sendTyping(event).queue();
						sleep(5000);
					}
					try {
						send(event, task.get()).queue();
					} catch (Exception e) {
						LogManager.getLogger("Command: Drama").error("An error ocurred fetching the latest Drama: ", e);
					}
				}).run();
		}).build());

		final String[][] SU_THEORIES_DB = {
			{
				"Steven", "Garnet", "Amethyst", "Pearl", "Rose", "Greg", "Opal", "Sugilite", "Sardonyx", "Alexandrite",
				"Ruby", "Sapphire", "Jasper", "Peridot", "Lapis", "Yellow Diamond", "Malachite", "Rainbow Quartz",
				"Lars", "Sadie", "Buck Dewey", "Mayor Dewey", "Sour Cream", "Jenny Pizza", "Stevonnie", "Vidalia",
				"Connie", "Marty", "Yellowtail", "Jamie", "Ronaldo", "Nanefua", "Peedee", "Kiki", "Mr. Smiley",
				"Kofi", "Mr. Fryman", "Lion", "Cookie Cat", "Mr. Queasy", "Centipeetle", "Citrine", "The Pink Diamond",
				"Frybo", "The Cluster", "An Old Testament God", "Some leftover pizza", "The Gem Drill", "Blue Pearl",
				"Yellow Pearl", "Pink Pearl", "White Pearl", "Amethyst's Room", "Pearl's Room", "Rose's Room",
				"A Tiny Pink Whale", "Stevonnie", "Onion", "Garnet's Room", "Smoky Quartz", "Rainbow Quartz", "Marty",
				"Garnet's fusion Realm", "Connie's mom", "Connie's dad", "Kevin", "Beach City", "Empire City"
			},
			{
				"is", "was"
			},
			{
				"a gun", "a ghost", "a lie", "a fusion", "dead", "dead this whole time", "alive", "a gem", "a human",
				"a cluster fusion", "a knife", "a weapon", "actually rose", "evil", "lars", "actually evil this whole time",
				"straight", "cis", "gay", "straight", "not real", "the reincarnation of rose", "The Yellow Diamond",
				"The Pink Diamond", "a corrupted gem", "corrupt", "a mass-produced servant", "The Blue Diamond", "The White Diamond",
				"The Green Diamond", "two lesbians in a trenchcoat", "present for the war 6000 years ago", "a spy for Homeworld",
				"part of the Diamond Triumverate", "part of the Diamond Authority", "one of the statues in the Sky Arena",
				"one of the carvings in the Pyramid Temple", "a Kindergarten Gem", "Bad", "a Bad Guy", "a Good Guy",
				"a fusion made of two fusions", "controlling Earth's currency", "a Homeworld experiment", "a *MALE* gem",
				"a Pearl", "the CLUSTER", "a Gem mutant", "behind everything", "Steven and Connie in a trenchcoat",
				"Steven", "Garnet", "Amethyst", "Pearl", "Rose", "Greg", "Opal", "Sugilite", "Sardonyx", "Alexandrite",
				"Ruby", "Sapphire", "Jasper", "Peridot", "Lapis", "Yellow Diamond", "Malachite", "Rainbow Quartz",
				"Lars", "Sadie", "Buck Dewey", "Mayor Dewey", "Sour Cream", "Jenny Pizza", "Stevonnie", "Vidalia",
				"Connie", "Marty", "Yellowtail", "Jamie", "Ronaldo", "Nanefua", "Peedee", "Kiki", "Mr. Smiley",
				"Kofi", "Mr. Fryman", "Lion", "Cookie Cat", "Mr. Queasy", "Centipeetle", "Citrine", "The Pink Diamond",
				"Frybo", "The Cluster", "an Old Testament God", "some leftover pizza", "The Gem Drill", "Blue Pearl",
				"Yellow Pearl", "Pink Pearl", "White Pearl", "Amethyst's Room", "Pearl's Room", "Rose's Room",
				"a Tiny Pink Whale", "Stevonnie", "Onion", "Garnet's Room", "Smoky Quartz", "Rainbow Quartz", "Marty",
				"Garnet's fusion Realm", "Connie's mom", "Connie's dad", "Kevin"
			}
		};

		//implSUTheory
		addCommand("sutheory", new CommandBuilder("cmd.sutheory").setAction((args, event) -> {
			for (int i = 0, amount = Math.min(parseInt(args, 1), 100); i < amount; i++) {
				String result = "";
				for (String[] theoryArray : SU_THEORIES_DB)
					result = result + theoryArray[(int) Math.floor(Math.random() * theoryArray.length)] + " ";
				send(event, "What if " + result.substring(0, result.length() - 1) + "?");
			}
		}).build());

		//implAnnoy
//		addCommand("annoy", new CommandBuilder()
//			.setPermRequired(ANNOY)
//			.setUsageDeprecatedMethod("")
//			.setAction(((arguments, event) -> {
//				String[] args = splitArgs(arguments, 3); //!spysend CH MSG
//				if (args[0].isEmpty() || args[1].isEmpty()) invalidargs(event).queue();
//				else {
//					args[0] = processId(args[0]);
//					if ("clear".equals(args[1])) {
//						DataManager.data.annoy.remove(args[0]);
//						bool(event, true).queue();
//					}
//					if ("add".equals(args[1])) {
//						if (!DataManager.data.annoy.containsKey(args[0]))
//							DataManager.data.annoy.put(args[0], new ArrayList<>());
//						DataManager.data.annoy.get(args[0]).add(args[2]);
//						bool(event, true).queue();
//					}
//				}
//			}))
//			.build()
//		);

		//implPerms
		addCommand("perms", new TreeCommandBuilder()
			.addCommand("get", new CommandBuilder()
				.setTranslatableUsage("perms.get.usage")
				.setAction((guild, arguments, event) -> {
					String arg = splitArgs(arguments, 1)[0]; //!getlevel USER
					if (arg.isEmpty()) arg = event.getAuthor().getId();
					send(event, "**" + getLocalized("perms.get.userPerms", event) + ":**\n *" + String.join(", ", toCollection(getPermFor(guild, arg)).stream().toArray(String[]::new)) + "*").queue();
				}).build())
			.addCommand("set", new CommandBuilder().setPermRequired(PERMSYSTEM)
				.setTranslatableUsage("perms.set.usage")
				.setAction((guild, arguments, event) -> {
					String[] args = splitArgs(arguments, 2); //!setlevel USER LEVEL
					if (args[0].isEmpty() || args[1].isEmpty()) invalidargs(event).queue();
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
						bool(event, setPerms(guild, event, args[0], toBeSet, toBeUnset)).queue();
					}
				})
				.build()
			)
			.addCommand("list",
				new CommandBuilder().setAction(
					(guild, arguments, event) -> send(event, "**" + getLocalized("perms.get.userPerms", event) + ":**\n *" + String.join(", ", toCollection(BOT_OWNER).stream().toArray(String[]::new)) + "*").queue()
				).setTranslatableUsage("perms.list.usage").build()
			)
			.build()
		);

		//implInviteMe
		addCommand("inviteme",
			new CommandBuilder().setAction(event -> send(event, "**" + getLocalized("inviteme.link", event) + ":**\nhttps://discordapp.com/oauth2/authorize?client_id=" + event.getJDA().getSelfInfo().getId() + "&scope=bot").queue())
				.setTranslatableUsage("inviteme.usage").build()
		);

		//implLang
		addCommand("lang",
			new CommandBuilder().setAction((arg, event) -> {
				Users.fromDiscord(event.getAuthor()).setLang(arg);
				if (arg.isEmpty()) announce(event, getLocalized("lang.setNone", event)).queue();
				else announce(event, String.format(getLocalized("lang.set", event), arg)).queue();
			}).setTranslatableUsage("lang.usage").build()
		);

		addCommand("guild", new TreeCommandBuilder()
			.setPermRequired(RUN_BASECMD)
			.addCommand("info",
				new CommandBuilder()
					.setAction((guild, arguments, event) -> sendCased(event, Guilds.toString(guild, event.getJDA(), I18n.getLocale(event))).queue())
					.setTranslatableUsage("guild.info.usage").build()
			)
			.addDefault("info")
			.addCommand("list",
				new CommandBuilder()
					.setAction((guild, arguments, event) -> Channels.listChannels(guild, event))
					.setTranslatableUsage("guild.list.usage").build()
			)
			.addCommand("lang",
				new CommandBuilder()
					.setPermRequired(EDIT_GUILD)
					.setAction((guild, arg, event) -> {
						arg = (arg.isEmpty() ? "en_US" : arg);
						guild.setLang(arg);
						announce(event, String.format(getLocalized("guild.lang.set", event), arg)).queue();
					}).setTranslatableUsage("guild.lang.usage").build()
			)
			.addCommand("broadcast",
				new CommandBuilder()
					.setAction(Channels::broadcast)
					.setTranslatableUsage("guild.broadcast.usage").build()
			)
			.addCommand("cleanup",
				new CommandBuilder().setPermRequired(EDIT_GUILD)
					.setTranslatableUsage("guild.cleanup.usage")
					.setAction((guild, args, event) -> bool(event, guild.toggleFlag("cleanup")).queue()).build()
			)
			.addCommand("prefixes",
				new CommandBuilder().setPermRequired(GUILD_MOD)
					.setTranslatableUsage("perms.set.usage")
					.setAction((guild, args, event) -> {
						if (args.trim().isEmpty()) {
							invalidargs(event).queue();
							return;
						}
						Commitable<List<String>> cmdPrefixesHandler = guild.modifyCmdPrefixes();
						List<String> cmdPrefixes = cmdPrefixesHandler.get();
						String[] all = args.split("\\s+", -1);
						for (String each : all) {
							if (each.toLowerCase().equals("+default")) {
								Arrays.asList(Guilds.DEFAULT_PREFIXES).forEach(s -> {
									if (!guild.getCmdPrefixes().contains(s)) cmdPrefixes.add(s);
								});
							} else if (each.charAt(0) == '+') {
								String v = each.substring(1);
								if (!guild.getCmdPrefixes().contains(v)) cmdPrefixes.add(v);
							} else if (each.toLowerCase().equals("clear")) {
								cmdPrefixes.clear();
							} else if (each.toLowerCase().equals("list") || each.toLowerCase().equals("get")) {
								send(event, Arrays.toString(cmdPrefixes.toArray())).queue();
							}
						}
						cmdPrefixesHandler.pushChanges();
						bool(event, true).queue();
					})
					.build()
			)
			.build()
		);

		addCommand("bot",
			new TreeCommandBuilder().setPermRequired(RUN_BASECMD)
				.addCommand("info",
					new CommandBuilder().setAction(BotGreeter::greet).build()
				)
				.addDefault("info")
				.addCommand("version", new CommandBuilder().setAction(e -> send(e, "").queue()).build()
				)
				.addCommand("stop",
					new CommandBuilder().setPermRequired(STOP_RESET)
						.setTranslatableUsage("bot.stop.usage")
						.setAction(event -> {
							announce(event, I18n.getLocalized("bot.stop", event)).queue();
							Bot.stopBot();
						})
						.build()
				)
				.addCommand("restart",
					new CommandBuilder().setPermRequired(STOP_RESET)
						.setTranslatableUsage("bot.restart.usage")
						.setAction(event -> {
							announce(event, I18n.getLocalized("bot.restart", event)).queue();
							Bot.restartBot();
						})
						.build()
				)
//				.addCommand("save",
//					new CommandBuilder().setPermRequired(SAVE_LOAD)
//						.setDynamicUsage("bot.save.usage")
//						.setAction(event -> {
//							announce(event, I18n.getLocalized("bot.save", event));
//							DataManager.saveData();
//							bool(event, true).queue();
//						})
//						.build()
//				)
//				.addCommand("load",
//					new CommandBuilder().setPermRequired(SAVE_LOAD)
//						.setDynamicUsage("bot.load.usage")
//						.setAction(event -> {
//							announce(event, I18n.getLocalized("bot.load", event));
//							DataManager.loadData();
//							bool(event, true).queue();
//						})
//						.build()
//				)
				.addCommand("toofast",
					new CommandBuilder().setPermRequired(BOT_OWNER)
						.setTranslatableUsage("bot.toofast.usage")
						.setAction((event) -> {
							CommandHandler.toofast = !CommandHandler.toofast;
							bool(event, CommandHandler.toofast).queue();
						}).build()
				)
				.addCommand("stats",
					new CommandBuilder().setAction(Statistics::printStats).setTranslatableUsage("bot.stats.usage").build()
				)
				.build()
		);

		addCommand("cmds",
			new TreeCommandBuilder()
				.addCommand("list",
					new CommandBuilder().setAction((guild, args, event) -> {
						List<String> cmds, userCmds, tmp = new ArrayList<>();

						cmds = getCommands(guild).entrySet().stream().filter(entry -> canRunCommand(guild, event, entry.getValue())).filter(entry -> {
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
									send(event, b.var.toString()).queue();
									b.var = new StringBuilder("*");
								}
								b.var.append(a);
							}

						});
						if (first.var) b.var.append("(nenhum comando disponível)");
						b.var.append("*");
						send(event, b.var.toString()).queue();

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
									send(event, b.var.toString()).queue();
									b.var = new StringBuilder("*");
								}
								b.var.append(a);
							}

						});
						if (first.var) b.var.append("(nenhum comando disponível)");
						b.var.append("*");
						send(event, b.var.toString()).queue();
					}).build())
				.addDefault("list")
				.addCommand("detailed", new CommandBuilder().setAction((guild, args, event) -> {
					event.getChannel().sendTyping().queue();
					MessageChannel channel = event.getAuthor().getPrivateChannel();
					List<String> cmds = getBaseCommands().entrySet().stream().filter(entry -> canRunCommand(guild, event, entry.getValue())).map(
						(entry) -> entry.getKey() + " - " + entry.getValue().toString(I18n.getLocale(event))).sorted(String::compareTo).collect(Collectors.toList());

					Holder<StringBuilder> b = new Holder<>();
					Holder<Boolean> first = new Holder<>();

					b.var = new StringBuilder("**Comandos:**\n```");
					first.var = true;
					cmds.forEach(s -> {
						if (first.var) {
							first.var = false;
							b.var.append(s);
						} else {
							String a = "\n" + s;
							if (b.var.length() + a.length() >= 1995) {
								b.var.append("```");
								channel.sendMessage(b.var.toString()).queue();
								b.var = new StringBuilder("*");
							}
							b.var.append(a);
						}

					});
					if (first.var) b.var.append("(nenhum comando disponível)");
					b.var.append("```");
					channel.sendMessage(b.var.toString()).queue();
					send(event, event.getAuthor().getAsMention() + " :mailbox_with_mail:").queue();
				}).build())
				.addCommand("add", new CommandBuilder().setPermRequired(MANAGE_USR)
					.setUsageDeprecatedMethod("Adiciona um Comando de Usuário")
					.setAction((guild, arg, event) -> {
						String[] args = splitArgs(arg, 2); //COMMAND_NAME RESPONSE
						if (args[0].isEmpty() | args[1].isEmpty()) invalidargs(event).queue();
						else {
							UserCommand cmd = getLocalUserCommands(guild).get(args[0].toLowerCase());
							if (cmd == null) {
								UserCommand ncmd = new UserCommand();
								ncmd.responses.add(args[1]);
								UserCommands.register(ncmd, args[0].toLowerCase(), guild);
								bool(event, true).queue();
							} else {
								cmd.responses.add(args[1]);
								UserCommands.update(cmd);
								bool(event, true).queue();
							}
						}
					}).build())
				.addCommand("rm", new CommandBuilder().setPermRequired(MANAGE_USR)
					.setUsageDeprecatedMethod("Remove um Comando de Usuário")
					.setAction((guild, arg, event) -> {
						if (arg.isEmpty()) invalidargs(event).queue();
						else {
							UserCommand command = getLocalUserCommands(guild).get(arg.toLowerCase());
							if (command == null) invalidargs(event).queue();
							else {
								UserCommands.remove(command);
								bool(event, true).queue();
							}
						}
					}).build())
				.addCommand("debug", new CommandBuilder().setPermRequired(MANAGE_USR).setAction((guild, arg, event) -> {
					if (arg.isEmpty()) invalidargs(event).queue();
					else {
						ICommand cmd = getLocalUserCommands(guild).get(arg);
						if (cmd == null) invalidargs(event).queue();
						else
							send(event, "***Debug do Comando `" + arg + "`:*** " + cmd.toString(I18n.getLocale(event))).queue();
					}
				})
					.build())
				.build()
		);

		addCommand("audio",
			new CommandBuilder().setAction((guild, args, event) -> send(event, "Our bot doesn't have Audio Support.").queue()).build()
		);
	}
}

//		//implOkay
//		addCommand("okay", (guild, arguments, event) -> bool(event, (arguments.isEmpty() || Boolean.parseBoolean(arguments)))).queue();
//
//		//implPlaying
//		addCommand("playing", new CommandBuilder().setAction((arg, event) -> {
//			if (arg.isEmpty()) announce(event, getLocalized("playing.notPlaying", event));
//			else announce(event, String.format(getLocalized("playing.nowPlaying", event), arg));
//			Bot.GAME = arg;
//		}).setPermRequired(Permissions.PLAYING).build());
//
//		//implWget
//		addCommand("wget", (guild, arguments, event) -> sendCased(event, IOHelper.toString(arguments), ""));
//
//		//implEval
//		addCommand("eval",
//			new CommandBuilder()
//				.setAction(JS::eval)
//				.setPermRequired(SCRIPTS | RUN_SCT_CMD)
//				.build()
//		);
//	}
//
//	private static void implSpy() {
//		addCommand("spy",
//			new TreeCommandBuilder().setPermRequired(SPY)
//				.addCommand("trigger", addUsage((guild, arguments, event) -> Spy.trigger(event), "Ativa/Desativa a Espionagem no Canal."))
//				.addCommand("log", addUsage((guild, arguments, event) -> Spy.triggerLog(event), "Ativa/Desativa a Leitura de Logs no Canal."))
//				.addCommand("send",
//					new CommandBuilder()
//						.setUsageDeprecatedMethod("Envia uma Mensagem para o canal especificado.\n(Parâmetros: <channel_id> <message>)\nO channel_id pode ser visto através do (#NUM) das mensagens de espionagem.")
//						.setAction((guild, arguments, event) -> {
//							String[] args = splitArgs(arguments, 2); //!spysend CH MSG
//							if (args[0].isEmpty() || args[1].isEmpty()) invalidargs(event).queue();
//							else {
//								int ch = parseInt(args[0], -1);
//								if (ch <= -1 || ch >= Spy.getChannels(guild).size()) invalidargs(event).queue();
//								else Spy.getChannels(guild).get(ch).sendMessageAsync(args[1], null);
//							}
//						}).build()
//				)
//				.addCommand("past",
//					new CommandBuilder()
//						.setUsageDeprecatedMethod("Mostra o Passado de um Canal.\n(Parâmetros: <channel_id>)\nO channel_id pode ser visto através do (#NUM) das mensagens de espionagem.")
//						.setAction((guild, arguments, event) -> {
//							String arg = splitArgs(arguments, 2)[0]; //!spypast CH
//							if (arg.isEmpty()) invalidargs(event).queue();
//							else {
//								int ch = parseInt(arg, -1);
//								if (ch <= -1 || ch >= Spy.getChannels(guild).size()) invalidargs(event).queue();
//								else Spy.getPast(event, Spy.getChannels(guild).get(ch));
//							}
//						}).build()
//				)
//				.addCommand("kickself",
//					new CommandBuilder().setAction((guild, arg, event) -> { //!broadcast MSG
//						if (arg.isEmpty()) invalidargs(event).queue();
//						else {
//							int ch = parseInt(arg, -1);
//							if (ch <= -1 || ch >= Spy.getChannels(guild).size()) invalidargs(event).queue();
//							else Spy.kickSelf(event, ch);
//							bool(event, true).queue();
//						}
//					}).build()
//				).build()
//		);
//	}