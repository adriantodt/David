/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [29/10/16 17:05]
 */

package cf.adriantodt.bot.base.cmd;

import cf.adriantodt.bot.data.I18n;

public class AliasCommand {
	public static ICommand of(final ICommand cmd, final String name) {
		return new ICommand() {
			@Override
			public void run(CommandEvent event) {
				cmd.run(event.createChild(cmd, event.getArgs()));
			}

			@Override
			public long retrievePerm() {
				return cmd.retrievePerm();
			}

			@Override
			public boolean sendStartTyping() {
				return cmd.sendStartTyping();
			}

			@Override
			public String toString(String language) {
				return String.format(I18n.getLocalized("alias.of", language), name);
			}
		};
	}

	public static ICommand of(final ICommand cmd, final String args, final String name) {
		return new ICommand() {
			@Override
			public void run(CommandEvent event) {
				cmd.run(event.createChild(cmd, args));
			}

			@Override
			public long retrievePerm() {
				return cmd.retrievePerm();
			}

			@Override
			public boolean sendStartTyping() {
				return cmd.sendStartTyping();
			}

			@Override
			public String toString(String language) {
				return String.format(I18n.getLocalized("alias.of", language), name + " " + args);
			}
		};
	}
}
