/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 08:18]
 */

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Statistics;
import cf.adriantodt.bot.Utils;
import cf.adriantodt.bot.cmd.*;
import cf.adriantodt.bot.guild.DiscordGuild;
import cf.adriantodt.bot.perm.Permissions;
import cf.adriantodt.bot.persistent.DataManager;
import cf.adriantodt.bot.spy.Spy;
import cf.brforgers.core.lib.IOHelper;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.text.Collator;
import java.util.*;

import static cf.adriantodt.bot.Answers.*;
import static cf.adriantodt.bot.Statistics.parseInt;
import static cf.adriantodt.bot.Utils.splitArgs;
import static cf.adriantodt.bot.Utils.startAsyncDramaProvider;
import static cf.adriantodt.bot.impl.EventHandler.*;
import static cf.adriantodt.bot.perm.Permissions.*;

public class Commands {
	public static final Map<String, ICommand> COMMANDS = new HashMap<>();

	public static void impl() {
		implCmds();
		implSpy();
		implBot();
		implPerms();
		implGuild();
		//implDrama
		addCommand("drama", (guild, cmd, event) ->
			send(event, "**Minecrosoft**: *" + Utils.latestDrama.var + "*\n  *(Provided by Minecraft Drama Generator)*")
		);
		startAsyncDramaProvider();

		//implOkay
		addCommand("okay", (guild, arguments, event) -> bool(event, (arguments.isEmpty() || Boolean.parseBoolean(arguments))));

		//implJogando
		addCommand("jogando", new CommandBuilder().setAction((arg, event) -> {
			if (arg.isEmpty()) send(event, "***Agora não estou jogando!***");
			else send(event, "***Agora estou jogando " + arg + "***");
			Bot.GAME = arg;
		}).setPermRequired(Permissions.PLAYING).build());

		//implWget
		addCommand("wget", (guild, arguments, event) -> sendCased(event, IOHelper.toString(arguments), ""));
	}

	private static void implGuild() {
		addCommand("guild", new TreeCommandBuilder()
			.setPermRequired(Permissions.RUN_BASECMD)
			.addCommand("info",
				addUsage((guild, arguments, event) -> sendCased(event, guild.toString(), ""), "Mostre informações sobre a Guild.")
			)
			.addDefault("info")
			.addCommand("channels",
				new TreeCommandBuilder().setPermRequired(GUILD)
					.addCommand("list", addUsage((guild, arguments, event) -> Spy.listChannelsKnown(guild, event), "Liste os Canais conhecidos.")
					)
					.addCommand("flush", addUsage((guild, arguments, event) -> Spy.channelFlush(guild, event), "Limpe os Canais conhecidos.")
					)
					.addCommand("discover",
						new CommandBuilder().setUsage("Descubra todos os Canais visíveis da Guild.").setAction(Spy::discoverChannels).build()
					)
					.addCommand("broadcast", addUsage(Spy::broadcast, "Envie uma mensagem para todos os canais (conhecidos) da Guild"))
					.build()
			)
			.build()
		);
	}

	private static void implPerms() {
		addCommand("perms", new TreeCommandBuilder()
			.addCommand("get", new CommandBuilder()
				.setUsage("Mostrar as Permissões.\n(Parâmetros: [user])\nSe executado sem argumentos, retorna as suas permissões.\nSe um usuário for suprido, retorna as permissões do usuário.")
				.setAction((guild, arguments, event) -> {
					String arg = splitArgs(arguments, 1)[0]; //!getlevel USER
					if (arg.isEmpty()) arg = event.getAuthor().getId();
					Collection<String> perms = Permissions.toCollection(getPermFor(guild, arg));

					Holder<StringBuilder> b = new Holder<>();
					Holder<Boolean> first = new Holder<>();

					b.var = new StringBuilder("**Permissões do Usuário:**\n *");
					first.var = true;
					perms.forEach(s -> {
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
					if (first.var) b.var.append("(nenhuma)");
					b.var.append("*");
					send(event, b.var.toString());
				}).build())
			.addCommand("set", new CommandBuilder().setPermRequired(Permissions.PERMSYSTEM)
				.setUsage("Define as Permissões.\n(Parâmetros: <user>)\nDefine as permissões do usuário suprido pelo parâmetro.")
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
			.addCommand("list", addUsage((guild, arguments, event) -> {
				Collection<String> perms = Permissions.toCollection(getSenderPerm(guild, event));

				Holder<StringBuilder> b = new Holder<>();
				Holder<Boolean> first = new Holder<>();

				b.var = new StringBuilder("**Permissões Disponíveis:**\n *");
				first.var = true;
				perms.forEach(s -> {
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
				if (first.var) b.var.append("(nenhuma permissão disponível)");
				b.var.append("*");
				send(event, b.var.toString());
			}, "Lista as permissões disponíveis."))
			.build()
		);
	}

	private static void implBot() {
		addCommand("bot",
			new TreeCommandBuilder().setPermRequired(RUN_BASECMD)
				.addCommand("info", addUsage(
					(guild, arguments, event) -> sendCased(event,
						"Olá, eu sou o David, o Bot mais aleatório feito em Java.\n\n" +
							"Os Dados e Comandos são Guardados de Forma Dependente de Guilds, o que significa que não espalho as piadas para outros grupos!\n\n" +
							"Use ?cmds ou &cmds (? e & são aceitos em todos os comandos) para começar!",
						""
					), "Informações sobre Bot.")
				)
				.addDefault("info")
				.addCommand("stop",
					new CommandBuilder().setPermRequired(STOP_RESET)
						.setUsage("Para o Bot (Não salva as informações).")
						.setAction(event -> {
							announce(event, "Saindo...");
							Bot.stopBot();
						})
						.build()
				)
				.addCommand("restart",
					new CommandBuilder().setPermRequired(STOP_RESET)
						.setUsage("Reinicia o Bot (Requer que o Bot esteja executando em um Jar).")
						.setAction(event -> {
							announce(event, "Saindo...");
							Bot.restartBot();
						})
						.build()
				)
				.addCommand("save",
					new CommandBuilder().setPermRequired(SAVE_LOAD)
						.setUsage("Salva as informações no Disco.")
						.setAction(event -> {
							announce(event, "Salvando...");
							DataManager.saveData();
							bool(event, true);
						})
						.build()
				)
				.addCommand("cleanup",
					new CommandBuilder().setPermRequired(Permissions.GUILD)
						.setUsage("Ativa ou Desativa o \"Cleanup\" de Mensagens do Bot.")
						.setAction((event) -> {
							cleanup = !cleanup;
							bool(event, cleanup);
						}).build()
				)
				.addCommand("load",
					new CommandBuilder().setPermRequired(SAVE_LOAD)
						.setUsage("Carrega as informações do Disco.")
						.setAction(event -> {
							announce(event, "Carregando...");
							DataManager.loadData();
							bool(event, true);
						})
						.build()
				)
				.addCommand("stats",
					addUsage((guild, arguments, event) -> Statistics.printStats(event), "Estatísticas sobre a última sessão.")
				)
				.build()
		);
	}

	private static void implSpy() {
		addCommand("spy",
			new TreeCommandBuilder().setPermRequired(SPY)
				.addCommand("trigger", addUsage((guild, arguments, event) -> Spy.trigger(event), "Ativa/Desativa a Espionagem no Canal."))
				.addCommand("send",
					new CommandBuilder()
						.setUsage("Envia uma Mensagem para o canal especificado.\n(Parâmetros: <channel_id> <message>)\nO channel_id pode ser visto através do (#NUM) das mensagens de espionagem.")
						.setAction((arguments, event) -> {
							String[] args = splitArgs(arguments, 2); //!spysend CH MSG
							if (args[0].isEmpty() || args[1].isEmpty()) invalidargs(event);
							else {
								int ch = parseInt(args[0], -1);
								if (ch <= -1 || ch >= DiscordGuild.GLOBAL.channelList.size()) invalidargs(event);
								else DiscordGuild.GLOBAL.channelList.get(ch).sendMessage(args[1]);
							}
						}).build()
				)
				.addCommand("past",
					new CommandBuilder()
						.setUsage("Mostra o Passado de um Canal.\n(Parâmetros: <channel_id>)\nO channel_id pode ser visto através do (#NUM) das mensagens de espionagem.")
						.setAction((arguments, event) -> {
							String arg = splitArgs(arguments, 2)[0]; //!spypast CH
							if (arg.isEmpty()) invalidargs(event);
							else {
								int ch = parseInt(arg, -1);
								if (ch <= -1 || ch >= DiscordGuild.GLOBAL.channelList.size()) invalidargs(event);
								else Spy.spyPast(event, DiscordGuild.GLOBAL.channelList.get(ch));
							}
						}).build()
				)
				.addCommand("kickself",
					new CommandBuilder().setAction((arg, event) -> { //!broadcast MSG
						if (arg.isEmpty()) invalidargs(event);
						else {
							int ch = parseInt(arg, -1);
							if (ch <= -1 || ch >= DiscordGuild.GLOBAL.channelList.size()) invalidargs(event);
							else Spy.kickSelf(ch);
							bool(event, true);
						}
					}).build()
				).build()
		);
	}

	private static void implCmds() {
		addCommand("cmds",
			new TreeCommandBuilder()
				.addCommand("list",
					addUsage((guild, args, event) -> {
							Collection<String> cmds = new TreeSet<>(Collator.getInstance()), userCmds = new TreeSet<>(Collator.getInstance());

							getCommands(guild).forEach((cmdName, cmd) -> {
								if (Permissions.canRunCommand(guild, event, cmd)) {
									if (cmd instanceof UserCommand) userCmds.add(cmdName);
									else cmds.add(cmdName);
								}
							});

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
						}
						, "Mostra os Comandos disponíveis"))
				.addDefault("list")
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
	}

	public static void addCommand(String name, ICommand command) {
		COMMANDS.put(name, command);
	}

	public static ICommand addUsage(ICommand command, String usage) {
		return new ICommand() {
			@Override
			public void run(DiscordGuild guild, String arguments, MessageReceivedEvent event) {
				command.run(guild, arguments, event);
			}

			@Override
			public long retrievePerm() {
				return command.retrievePerm();
			}

			@Override
			public String retrieveUsage() {
				return usage;
			}
		};
	}
}
