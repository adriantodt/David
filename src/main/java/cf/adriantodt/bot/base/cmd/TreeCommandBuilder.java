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

import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.data.I18n;
import cf.adriantodt.bot.handlers.CommandHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
	private long permRequired = Permissions.RUN_CMDS;
	private Function<String, String> usageProvider = USAGE_IMPL;

	public TreeCommandBuilder() {
		addDefault((ICommand) null);
	}

	public TreeCommandBuilder(long permRequired) {
		this();
		setPermRequired(permRequired);
	}

	public TreeCommandBuilder addCommand(String cmd, ICommand command) {
		SUBCMDS.put(cmd.toLowerCase(), command);
		return this;
	}

	public TreeCommandBuilder addDefault(ICommand command) {
		return addCommand("", command);
	}

	public TreeCommandBuilder addCommand(String cmd, String alias) {
		ICommand base = SUBCMDS.get(alias);
		return addCommand(cmd, base == null ? null : new ICommand() {
			@Override
			public void run(CommandEvent event) {
				base.run(event.createChild(base, event.getArgs()));
			}

			@Override
			public long retrievePerm() {
				return base.retrievePerm();
			}

			@Override
			public boolean sendStartTyping() {
				return base.sendStartTyping();
			}

			@Override
			public String toString(String language) {
				return "Alias of " + alias;
			}
		});
	}

	public TreeCommandBuilder addDefault(String alias) {
		return addCommand("", alias);
	}

	private TreeCommandBuilder setPermRequired(long value) {
		permRequired = value;
		return this;
	}

	public ICommand build() {
		return new CommandBuilder(USAGE_IMPL, permRequired).setAction(event -> {
			String[] args = event.getArgs(2);
			ICommand cmd = SUBCMDS.get(args[0].toLowerCase());
			if (cmd == null) event.getAnswers().invalidargs().queue();
			else CommandHandler.execute(new CommandEvent(event.getEvent(), event.getGuild(), cmd, args[1]));
		}).build();
	}
}
