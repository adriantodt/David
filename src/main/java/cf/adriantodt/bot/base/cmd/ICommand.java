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
import cf.adriantodt.bot.base.Permissions;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.function.Function;

public interface ICommand {
	void run(DiscordGuild guild, String arguments, MessageReceivedEvent event);

	/**
	 * Provides Check for Minimal Perm usage.
	 *
	 * @return the Permission Required
	 */
	default long retrievePerm() {
		return Permissions.RUN_BASECMD;
	}

	/**
	 * Provides Usage on invalidargs().<br>
	 * <br>
	 * Null = Default invalidargs message<br>
	 * Empty = No invalidargs message<br>
	 * Not-Empty = Shows the String as message<br>
	 *
	 * @param language the Language the Usage must be
	 * @return the Usage
	 */
	default String retrieveUsage(String language) {
		return null;
	}

	default ICommand addUsage(Function<String, String> usageProvider) {
		ICommand base = this;
		return new ICommand() {
			@Override
			public void run(DiscordGuild guild, String arguments, MessageReceivedEvent event) {
				base.run(guild, arguments, event);
			}

			@Override
			public long retrievePerm() {
				return base.retrievePerm();
			}

			@Override
			public String retrieveUsage(String language) {
				return usageProvider.apply(language);
			}
		};
	}
}
