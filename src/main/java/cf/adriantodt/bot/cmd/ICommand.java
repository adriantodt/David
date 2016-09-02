/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 07:55]
 */

package cf.adriantodt.bot.cmd;

import cf.adriantodt.bot.guild.DiscordGuild;
import cf.adriantodt.bot.perm.Permissions;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

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
	 * @return the Usage
	 */
	default String retrieveUsage() {
		return null;
	}
}
