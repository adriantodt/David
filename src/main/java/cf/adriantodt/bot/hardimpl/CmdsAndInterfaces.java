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
import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.I18n;
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.base.cmd.*;
import cf.adriantodt.bot.handlers.BotGreeter;
import cf.adriantodt.bot.handlers.BotIntercommns;
import cf.adriantodt.bot.handlers.CommandHandler;
import cf.adriantodt.bot.persistence.DataManager;
import cf.adriantodt.bot.utils.Channels;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Tasks;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cf.adriantodt.bot.base.I18n.getLocalized;
import static cf.adriantodt.bot.base.Permissions.*;
import static cf.adriantodt.bot.utils.Answers.*;
import static cf.adriantodt.bot.utils.Commands.*;
import static cf.adriantodt.bot.utils.Utils.nnOrD;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class CmdsAndInterfaces {
	public static void impl() {
		//implUser
		addCommand("user", new CommandBuilder()
			.setAction((g, a, e) -> {
				User pre =
					(e.getMessage().getMentionedUsers().isEmpty() ? e.getAuthor() : e.getMessage().getMentionedUsers().get(e.getMessage().getMentionedUsers().size() - 1));
				User user = pre == Bot.SELF && a.trim().isEmpty() ? e.getAuthor() : pre;
				send(e,
					user.getAsMention() + ": \n" + getLocalized("user.avatar", e) + ": " + user.getAvatarUrl() + "\n```" +
						getLocalized("user.name", e) + ": " + user.getUsername() + "\n" +

						(g.guild == null ? "" :
							getLocalized("user.nick", e) + ": " + (e.getGuild().getNicknameForUser(user) == null ? "(" + getLocalized("user.none", e) + ")" : e.getGuild().getNicknameForUser(user)) + "\n" +
								getLocalized("user.roles", e) + ": " + nnOrD(String.join(", ", e.getGuild().getRolesForUser(user).stream().map(Role::getName).toArray(String[]::new)), "(" + getLocalized("user.none", e) + ")") + "\n" +
								getLocalized("user.memberSince", e) + ": " + g.guild.getJoinDateForUser(user).format(DateTimeFormatter.RFC_1123_DATE_TIME) + "\n"
						) +
						getLocalized("user.commonGuilds", e) + ": " + nnOrD(String.join(", ", e.getJDA().getGuilds().stream().filter(guild -> guild.isMember(user)).map(Guild::getName).toArray(String[]::new)), "(" + getLocalized("user.none", e) + ")") + "\n" +
						"ID: " + user.getId() + "\n" +
						getLocalized("user.status", e) + ": " + user.getOnlineStatus() + "\n" +
						getLocalized("user.playing", e) + ": " + (user.getCurrentGame() == null ? "(" + getLocalized("user.none", e) + ")" : user.getCurrentGame().toString()) + "\n```"
				);
			}).build()
		);

		//implDrama
		addCommand("drama", new CommandBuilder().setAction((event) -> send(event, "**Minecrosoft**: *" + Tasks.latestDrama.var + "*\n  *(Provided by Minecraft Drama Generator)*")).build());

		//implAnnoy
		addCommand("annoy", new CommandBuilder()
			.setPermRequired(ANNOY)
			.setUsage("")
			.setAction(((arguments, event) -> {
				String[] args = splitArgs(arguments, 3); //!spysend CH MSG
				if (args[0].isEmpty() || args[1].isEmpty()) invalidargs(event);
				else {
					args[0] = processID(args[0]);
					if ("clear".equals(args[1])) {
						DataManager.data.annoy.remove(args[0]);
						bool(event, true);
					}
					if ("add".equals(args[1])) {
						if (!DataManager.data.annoy.containsKey(args[0]))
							DataManager.data.annoy.put(args[0], new ArrayList<>());
						DataManager.data.annoy.get(args[0]).add(args[2]);
						bool(event, true);
					}
				}
			}))
			.build()
		);

		//implPerms
		addCommand("perms", new TreeCommandBuilder()
			.addCommand("get", new CommandBuilder()
				.setTranslatableUsage("perms.get.usage")
				.setAction((guild, arguments, event) -> {
					String arg = splitArgs(arguments, 1)[0]; //!getlevel USER
					if (arg.isEmpty()) arg = event.getAuthor().getId();
					send(event, "**" + getLocalized("perms.get.userPerms", event) + ":**\n *" + String.join(", ", Permissions.toCollection(getPermFor(guild, arg)).stream().toArray(String[]::new)) + "*");
				}).build())
			.addCommand("set", new CommandBuilder().setPermRequired(PERMSYSTEM)
				.setTranslatableUsage("perms.set.usage")
				.setAction((guild, arguments, event) -> {
					String[] args = splitArgs(arguments, 2); //!setlevel USER LEVEL
					if (args[0].isEmpty() || args[1].isEmpty()) invalidargs(event);
					else {
						String[] all = args[1].split("\\s+", -1);
						int toBeSet = 0, toBeUnset = 0;
						for (String each : all) {
							if (each.charAt(0) == '+') {
								String p = each.substring(1).toUpperCase();
								if (Permissions.perms.containsKey(p)) {
									toBeSet |= Permissions.perms.get(p);
								}
							} else if (each.charAt(0) == '-') {
								String p = each.substring(1).toUpperCase();
								if (Permissions.perms.containsKey(p)) {
									toBeUnset |= Permissions.perms.get(p);
								}
							}
						}
						bool(event, Permissions.setPerms(guild, event, args[0], toBeSet, toBeUnset));
					}
				})
				.build()
			)
			.addCommand("list",
				new CommandBuilder().setAction(
					(guild, arguments, event) -> send(event, "**" + getLocalized("perms.get.userPerms", event) + ":**\n *" + String.join(", ", Permissions.toCollection(BOT_OWNER).stream().toArray(String[]::new)) + "*")
				).setTranslatableUsage("perms.list.usage").build()
			)
			.build()
		);

		//implInviteMe
		addCommand("inviteme",
			new CommandBuilder().setAction(event -> send(event, "**" + getLocalized("inviteme.link", event) + ":**\n" + Bot.API.getSelfInfo().getAuthUrl()))
				.setTranslatableUsage("inviteme.usage").build()
		);

		//implLang
		addCommand("lang",
			new CommandBuilder().setAction((arg, event) -> {
				I18n.setLang(event.getAuthor(), arg);
				if (arg.isEmpty()) announce(event, getLocalized("lang.setNone", event));
				else announce(event, String.format(getLocalized("lang.set", event), arg));
			}).setTranslatableUsage("lang.usage").build()
		);

		addCommand("guild", new TreeCommandBuilder()
			.setPermRequired(Permissions.RUN_BASECMD)
			.addCommand("info",
				new CommandBuilder()
					.setAction((guild, arguments, event) -> sendCased(event, guild.toString(I18n.getLang(event))))
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
					.setPermRequired(Permissions.EDIT_GUILD)
					.setAction((guild, arg, event) -> {
						arg = (arg.isEmpty() ? "en_US" : arg);
						guild.defaultLanguage = arg;
						announce(event, String.format(getLocalized("guild.lang.set", event), arg));
					}).setTranslatableUsage("guild.lang.usage").build()
			)
			.addCommand("broadcast",
				new CommandBuilder()
					.setAction(Channels::broadcast)
					.setTranslatableUsage("guild.broadcast.usage").build()
			)
			.addCommand("cleanup",
				new CommandBuilder().setPermRequired(Permissions.EDIT_GUILD)
					.setUsage("Ativa ou Desativa o \"Cleanup\" de Mensagens do Bot.")
					.setAction((guild, args, event) -> {
						guild.flags.put("cleanup", !guild.flags.get("cleanup"));
						bool(event, guild.flags.get("cleanup"));
					}).build()
			)
			.addCommand("prefixes",
				new CommandBuilder().setPermRequired(PERMSYSTEM)
					.setTranslatableUsage("perms.set.usage")
					.setAction((guild, args, event) -> {
						if (args.trim().isEmpty()) {
							invalidargs(event);
							return;
						}
						String[] all = args.split("\\s+", -1);
						for (String each : all) {
							if (each.toLowerCase().equals("+default")) {
								Arrays.asList(DiscordGuild.DEFAULT_PREFIXES).forEach(s -> {
									if (!guild.cmdPrefixes.contains(s)) guild.cmdPrefixes.add(s);
								});
							} else if (each.charAt(0) == '+') {
								String v = each.substring(1);
								if (!guild.cmdPrefixes.contains(v)) guild.cmdPrefixes.add(v);
							} else if (each.toLowerCase().equals("clear")) {
								guild.cmdPrefixes.clear();
							} else if (each.toLowerCase().equals("list") || each.toLowerCase().equals("get")) {
								send(event, Arrays.toString(guild.cmdPrefixes.toArray()));
							}

						}
						bool(event, true);
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
				.addCommand("stop",
					new CommandBuilder().setPermRequired(STOP_RESET)
						.setAction(event -> {
							announce(event, I18n.getLocalized("bot.stop", event));
							Bot.stopBot();
						})
						.build()
				)
				.addCommand("restart",
					new CommandBuilder().setPermRequired(STOP_RESET)
						.setAction(event -> {
							announce(event, I18n.getLocalized("bot.restart", event));
							Bot.restartBot();
						})
						.build()
				)
				.addCommand("save",
					new CommandBuilder().setPermRequired(SAVE_LOAD)
						.setAction(event -> {
							announce(event, I18n.getLocalized("bot.save", event));
							DataManager.saveData();
							bool(event, true);
						})
						.build()
				)
				.addCommand("load",
					new CommandBuilder().setPermRequired(SAVE_LOAD)
						.setAction(event -> {
							announce(event, I18n.getLocalized("bot.load", event));
							DataManager.loadData();
							bool(event, true);
						})
						.build()
				)
				.addCommand("toofast",
					new CommandBuilder().setPermRequired(Permissions.BOT_OWNER)
						.setTranslatableUsage("bot.toofast.usage")
						.setAction((event) -> {
							CommandHandler.toofast = !CommandHandler.toofast;
							bool(event, CommandHandler.toofast);
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

						cmds = getCommands(guild).entrySet().stream().filter(entry -> Permissions.canRunCommand(guild, event, entry.getValue())).filter(entry -> {
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
									send(event, b.var.toString());
									b.var = new StringBuilder("*");
								}
								b.var.append(a);
							}

						});
						if (first.var) b.var.append("(nenhum comando disponível)");
						b.var.append("*");
						send(event, b.var.toString());

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
									send(event, b.var.toString());
									b.var = new StringBuilder("*");
								}
								b.var.append(a);
							}

						});
						if (first.var) b.var.append("(nenhum comando disponível)");
						b.var.append("*");
						send(event, b.var.toString());
					}).build())
				.addDefault("list")
				.addCommand("detailed", new CommandBuilder().setAction((guild, args, event) -> {
					MessageChannel channel = event.getAuthor().getPrivateChannel();
					List<String> cmds = getBaseCommands().entrySet().stream().filter(entry -> Permissions.canRunCommand(guild, event, entry.getValue())).map(
						(entry) -> entry.getKey() + " - " + entry.getValue().toString(I18n.getLang(event))).sorted(String::compareTo).collect(Collectors.toList());

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
								channel.sendMessageAsync(b.var.toString(), null);
								b.var = new StringBuilder("*");
							}
							b.var.append(a);
						}

					});
					if (first.var) b.var.append("(nenhum comando disponível)");
					b.var.append("```");
					channel.sendMessageAsync(b.var.toString(), null);
					send(event, event.getAuthor().getAsMention() + " :mailbox_with_mail:");
				}).build())
				.addCommand("add", new CommandBuilder().setPermRequired(Permissions.MANAGE_USR)
					.setUsage("Adiciona um Comando de Usuário")
					.setAction((guild, arg, event) -> {
						String[] args = splitArgs(arg, 2); //COMMAND_NAME RESPONSE
						if (args[0].isEmpty() | args[1].isEmpty()) invalidargs(event);
						else {
							UserCommand cmd = getLocalUserCommands(guild).get(args[0].toLowerCase());
							if (cmd == null) {
								UserCommand ncmd = new UserCommand();
								ncmd.responses.add(args[1]);
								getLocalUserCommands(guild).put(args[0].toLowerCase(), ncmd);
								bool(event, true);
							} else {
								cmd.responses.add(args[1]);
								bool(event, true);
							}
						}
					}).build())
				.addCommand("rm", new CommandBuilder().setPermRequired(Permissions.MANAGE_USR)
					.setUsage("Remove um Comando de Usuário")
					.setAction((guild, arg, event) -> {
						if (arg.isEmpty()) invalidargs(event);
						else {
							ICommand pre = getLocalUserCommands(guild).get(arg.toLowerCase());
							if (pre == null) invalidargs(event);
							else {
								getLocalUserCommands(guild).remove(arg.toLowerCase());
								bool(event, true);
								BotIntercommns.updateCmds();
							}
						}
					}).build())
				.addCommand("debug", new CommandBuilder().setPermRequired(Permissions.MANAGE_USR).setAction((guild, arg, event) -> {
					if (arg.isEmpty()) invalidargs(event);
					else {
						ICommand cmd = getLocalUserCommands(guild).get(arg);
						if (cmd == null) invalidargs(event);
						else
							send(event, "***Debug do Comando `" + arg + "`:*** " + Arrays.toString(((UserCommand) cmd).responses.toArray()));
					}
				})
					.build())
				.build()
		);

		addCommand("audio",
			new CommandBuilder().setAction((guild, args, event) -> send(event, "Our bot doesn't have Audio Support.")).build()
		);
	}
}

//		//implOkay
//		addCommand("okay", (guild, arguments, event) -> bool(event, (arguments.isEmpty() || Boolean.parseBoolean(arguments))));
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
//						.setUsage("Envia uma Mensagem para o canal especificado.\n(Parâmetros: <channel_id> <message>)\nO channel_id pode ser visto através do (#NUM) das mensagens de espionagem.")
//						.setAction((guild, arguments, event) -> {
//							String[] args = splitArgs(arguments, 2); //!spysend CH MSG
//							if (args[0].isEmpty() || args[1].isEmpty()) invalidargs(event);
//							else {
//								int ch = parseInt(args[0], -1);
//								if (ch <= -1 || ch >= Spy.getChannels(guild).size()) invalidargs(event);
//								else Spy.getChannels(guild).get(ch).sendMessageAsync(args[1], null);
//							}
//						}).build()
//				)
//				.addCommand("past",
//					new CommandBuilder()
//						.setUsage("Mostra o Passado de um Canal.\n(Parâmetros: <channel_id>)\nO channel_id pode ser visto através do (#NUM) das mensagens de espionagem.")
//						.setAction((guild, arguments, event) -> {
//							String arg = splitArgs(arguments, 2)[0]; //!spypast CH
//							if (arg.isEmpty()) invalidargs(event);
//							else {
//								int ch = parseInt(arg, -1);
//								if (ch <= -1 || ch >= Spy.getChannels(guild).size()) invalidargs(event);
//								else Spy.getPast(event, Spy.getChannels(guild).get(ch));
//							}
//						}).build()
//				)
//				.addCommand("kickself",
//					new CommandBuilder().setAction((guild, arg, event) -> { //!broadcast MSG
//						if (arg.isEmpty()) invalidargs(event);
//						else {
//							int ch = parseInt(arg, -1);
//							if (ch <= -1 || ch >= Spy.getChannels(guild).size()) invalidargs(event);
//							else Spy.kickSelf(event, ch);
//							bool(event, true);
//						}
//					}).build()
//				).build()
//		);
//	}