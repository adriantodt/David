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

package cf.adriantodt.bot.base.cmd;

import cf.adriantodt.bot.base.DiscordGuild;
import cf.adriantodt.bot.base.I18n;
import cf.adriantodt.bot.handlers.CommandHandler;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static cf.adriantodt.bot.utils.Answers.invalidargs;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class TreeCommandBuilder {
	private final Map<String, ICommand> SUBCMDS = new HashMap<>();
	private final Function<String, String> USAGE_IMPL = (lang) -> {
		Holder<StringBuilder> b = new Holder<>();
		Holder<Boolean> first = new Holder<>();

		b.var = new StringBuilder(I18n.getLocalized("tree.subcmds", lang) + ":");
		first.var = true;
		SUBCMDS.forEach((cmdName, cmd) -> {
			String usage = (cmd == null) ? null : cmd.toString(lang);
			if (usage == null || usage.isEmpty()) return;
			if (first.var) {
				first.var = false;
			}
			String a = "\n - " + (cmdName.isEmpty() ? "(" + I18n.getLocalized("tree.default", lang) + ")" : cmdName) + ": " + usage.replace("\n", "\n    ");
			b.var.append(a);
		});
		if (first.var) return null;
		return b.var.toString();
	};
	private Supplier<Long> permProvider = () -> 0L;
	private Function<String, String> usageProvider = USAGE_IMPL;

	public TreeCommandBuilder() {
		addDefault((ICommand) null);
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
		return setUsage((s) -> usage);
	}

	public TreeCommandBuilder setPermRequired(Supplier<Long> provider) {
		permProvider = provider;
		return this;
	}

	public TreeCommandBuilder setUsage(Function<String, String> provider) {
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
				else {
					CommandHandler.onTree(event, cmd);
					CommandHandler.execute(cmd, guild, args[1], event);
					CommandHandler.onTree(event, this);
				}
			}

			@Override
			public long retrievePerm() {
				return permProvider == null ? 0 : permProvider.get();
			}

			@Override
			public String toString(String language) {
				return usageProvider == null ? null : usageProvider.apply(language);
			}
		};
	}
}
