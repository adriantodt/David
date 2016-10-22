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

import cf.adriantodt.bot.data.Guilds;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.logging.log4j.LogManager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static cf.adriantodt.bot.data.I18n.getLocalized;

public class CommandBuilder {
	private static final Function<String, String> DEFAULT_NOOP_PROVIDER = (s) -> null;
	private TriConsumer<Guilds.Data, String, GuildMessageReceivedEvent> action = null;
	private long permRequired = 0L;
	private Function<String, String> usageProvider = DEFAULT_NOOP_PROVIDER;

	public CommandBuilder() {
	}

	public CommandBuilder(Function<String, String> usageProvider) {
		setDynamicUsage(usageProvider);
	}

	public CommandBuilder(String translatableUsage) {
		setTranslatableUsage(translatableUsage);
	}

	public CommandBuilder setAction(TriConsumer<Guilds.Data, String, GuildMessageReceivedEvent> consumer) {
		action = consumer;
		return this;
	}

	public CommandBuilder setAction(BiConsumer<String, GuildMessageReceivedEvent> consumer) {
		action = (guild, s, event) -> consumer.accept(s, event);
		return this;
	}

	public CommandBuilder setAction(Consumer<GuildMessageReceivedEvent> consumer) {
		action = (guild, s, event) -> consumer.accept(event);
		return this;
	}

	public CommandBuilder setAction(Runnable runnable) {
		action = (guild, s, event) -> runnable.run();
		return this;
	}

	public CommandBuilder setPermRequired(long value) {
		permRequired = value;
		return this;
	}

	@Deprecated
	public CommandBuilder setUsageDeprecatedMethod(String usage) {
		usageProvider = (s) -> usage;
		return this;
	}

	public CommandBuilder setTranslatableUsage(String translatableUsage) {
		return setDynamicUsage((lang) -> getLocalized(translatableUsage, lang));
	}

	public CommandBuilder setDynamicUsage(Function<String, String> provider) {
		usageProvider = provider;
		return this;
	}

	public ICommand build() {
		if (usageProvider == DEFAULT_NOOP_PROVIDER) {
			LogManager.getLogger("CommandBuilder - Deprecated").warn("No Usage was provided to the Command being built!", new Exception("Stacktrace Exception"));
		}

		return new ICommand() {
			@Override
			public void run(Guilds.Data guild, String arguments, GuildMessageReceivedEvent event) {
				action.accept(guild, arguments, event);
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
