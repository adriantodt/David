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

import cf.adriantodt.bot.base.guild.DiscordGuild;
import cf.adriantodt.bot.base.perm.Permissions;
import cf.adriantodt.bot.impl.Audio;
import cf.adriantodt.bot.impl.i18n.I18n;
import cf.brforgers.core.lib.IOHelper;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static cf.adriantodt.bot.Answers.noperm;
import static cf.adriantodt.bot.Answers.send;
import static cf.adriantodt.bot.Bot.RAND;

public class UserCommand implements ICommand {
	public List<String> responses = new ArrayList<>();

	@Override
	public void run(DiscordGuild guild, String arguments, MessageReceivedEvent event) {
		String response = responses.get(RAND.nextInt(responses.size()));
		if (response.length() > 7) {
			if (response.substring(0, 6).equals("get://")) {
				send(event, IOHelper.toString(response.substring(6)));
				return;
			} else if (response.substring(0, 6).equals("loc://")) {
				send(event, I18n.getLocalized(response.substring(6), Locale.ENGLISH));
				return;
			} else if (response.substring(0, 6).equals("aud://")) {
				Audio.queue(IOHelper.newURL(response.substring(6)), event);
				return;
			} else if (response.substring(0, 6).equals("lua://")) {
				if (Permissions.havePermsRequired(guild, event, Permissions.RUN_LUA_CMD)) {
					send(event, "TODO Lua Impl");
					//lua command
				} else {
					noperm(event);
				}

				return;
			}
		}

		send(event, response);
	}

	@Override
	public long retrievePerm() {
		return Permissions.RUN_BASECMD | Permissions.RUN_USR_CMD;
	}
}
