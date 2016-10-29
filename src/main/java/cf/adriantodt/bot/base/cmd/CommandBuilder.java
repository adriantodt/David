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
import org.apache.logging.log4j.LogManager;

import java.util.function.Consumer;
import java.util.function.Function;

import static cf.adriantodt.bot.data.I18n.getLocalized;

public class CommandBuilder {
	private static final Function<String, String> DEFAULT_NOOP_PROVIDER = (s) -> null;
	private Consumer<CommandEvent> action = null;
	private long permRequired = Permissions.RUN_CMDS;
	private Function<String, String> usageProvider = DEFAULT_NOOP_PROVIDER;

	public CommandBuilder() {
	}

	public CommandBuilder(Function<String, String> usageProvider) {
		setDynamicUsage(usageProvider);
	}

	public CommandBuilder(String translatableUsage) {
		setDynamicUsage((lang) -> getLocalized(translatableUsage, lang));
	}

	public CommandBuilder(long permRequired) {
		this.permRequired = permRequired;
	}

	public CommandBuilder(Function<String, String> usageProvider, long permRequired) {
		setDynamicUsage(usageProvider);
		setPermRequired(permRequired);
	}

	public CommandBuilder(String translatableUsage, long permRequired) {
		setTranslatableUsage(translatableUsage);
		setPermRequired(permRequired);
	}

	public CommandBuilder setAction(Consumer<CommandEvent> consumer) {
		action = consumer;
		return this;
	}

//	public CommandBuilder setAction(Runnable runnable) {
//		action = (guild, s, event) -> runnable.run();
//		return this;
//	}

	private CommandBuilder setPermRequired(long value) {
		permRequired = value;
		return this;
	}

	private CommandBuilder setTranslatableUsage(String translatableUsage) {
		return setDynamicUsage((lang) -> getLocalized(translatableUsage, lang));
	}

	private CommandBuilder setDynamicUsage(Function<String, String> provider) {
		usageProvider = provider;
		return this;
	}

	public ICommand build() {
		if (usageProvider == DEFAULT_NOOP_PROVIDER) {
			LogManager.getLogger("CommandBuilder - Unsafe").warn("No Usage was provided to the Command being built! Please set a Usage to it!", new Throwable("Stacktrace:"));
		}

		return new ICommand() {
			@Override
			public void run(CommandEvent event) {
				action.accept(event);
			}

			@Override
			public long retrievePerm() {
				return permRequired;
			}

			@Override
			public String toString(String language) {
				return usageProvider.apply(language);
			}
		};
	}
}
