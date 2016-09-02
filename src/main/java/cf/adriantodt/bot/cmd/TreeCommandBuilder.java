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

package cf.adriantodt.bot.cmd;

import cf.adriantodt.bot.Answers;
import cf.adriantodt.bot.guild.DiscordGuild;
import cf.adriantodt.bot.impl.EventHandler;
import cf.adriantodt.bot.perm.Permissions;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static cf.adriantodt.bot.Answers.invalidargs;
import static cf.adriantodt.bot.Utils.splitArgs;

public class TreeCommandBuilder {
	private static final ICommand DEFAULT_IMPL = new CommandBuilder().setAction(Answers::invalidargs).setPermRequired(Permissions.RUN_BASECMD).build();
	private final Map<String, ICommand> SUBCMDS = new HashMap<>();
	private final Supplier<String> USAGE_IMPL = () -> {
		Holder<StringBuilder> b = new Holder<>();
		Holder<Boolean> first = new Holder<>();

		b.var = new StringBuilder("Sub-Comandos:");
		first.var = true;
		SUBCMDS.forEach((cmdName, cmd) -> {
			String usage = cmd.retrieveUsage();
			if (usage == null || usage.isEmpty()) return;
			if (first.var) {
				first.var = false;
			}
			String a = "\n - " + (cmdName.isEmpty() ? "(default)" : cmdName) + ": " + usage.replace("\n", "\n    ");
			b.var.append(a);
		});
		if (first.var) return null;
		return b.var.toString();
	};
	private Supplier<Long> permProvider = () -> 0L;
	private Supplier<String> usageProvider = USAGE_IMPL;

	public TreeCommandBuilder() {
		addDefault(DEFAULT_IMPL);
	}

	public TreeCommandBuilder addCommand(String cmd, ICommand command) {
		SUBCMDS.put(cmd.toLowerCase(), command);
		return this;
	}

	public TreeCommandBuilder addDefault(ICommand command) {
		SUBCMDS.put("", command);
		return this;
	}

	public TreeCommandBuilder addCommand(String cmd, String alias) {
		return addCommand(cmd, SUBCMDS.get(alias));
	}

	public TreeCommandBuilder addDefault(String alias) {
		return addDefault(SUBCMDS.get(alias));
	}

	public TreeCommandBuilder setPermRequired(long value) {
		return setPermRequired(() -> value);
	}

	public TreeCommandBuilder setUsage(String usage) {
		return setUsage(() -> usage);
	}

	public TreeCommandBuilder setPermRequired(Supplier<Long> provider) {
		permProvider = provider;
		return this;
	}

	public TreeCommandBuilder setUsage(Supplier<String> provider) {
		if (provider == null) usageProvider = USAGE_IMPL;
		else usageProvider = provider;
		return this;
	}

	public ICommand build() {
		return new ICommand() {
			@Override
			public void run(DiscordGuild guild, String arguments, MessageReceivedEvent event) {
				String[] args = splitArgs(arguments, 2);
				ICommand cmd = SUBCMDS.get(args[0].toLowerCase());
				if (cmd == null) invalidargs(event);
				else EventHandler.execute(cmd, guild, args[1], event);
			}

			@Override
			public long retrievePerm() {
				return permProvider == null ? 0 : permProvider.get();
			}

			@Override
			public String retrieveUsage() {
				return usageProvider == null ? null : usageProvider.get();
			}
		};
	}
}
