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
import cf.brforgers.core.lib.IOHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cf.adriantodt.bot.Bot.RAND;
import static cf.adriantodt.bot.utils.Answers.noperm;
import static cf.adriantodt.bot.utils.Answers.send;

//import cf.adriantodt.bot.handlers.scripting.JS;

public class UserCommand implements ICommand, ITranslatable {
	public List<String> responses = new ArrayList<>();

	@Override
	public void run(CommandEvent event) {
		String response = responses.get(RAND.nextInt(responses.size()));
		if (response.length() > 7) {
			if (response.substring(0, 6).equals("get://")) {
				send(event, IOHelper.toString(response.substring(6))).queue();
				return;
			} else if (response.substring(0, 6).equals("loc://")) {
				send(event, I18n.getLocalized(response.substring(6), "en_US")).queue();
				return;
				//} else if (response.substring(0, 6).equals("aud://")) {
				//	Audio.queue(IOHelper.newURL(response.substring(6)), event);
				//	return;
			} else if (response.substring(0, 5).equals("js://")) {
				if (Permissions.havePermsRequired(event.getGuild(), event.getAuthor(), Permissions.RUN_SCT_CMD)) {
					//JS.eval(event.getGuild(), response.substring(5), event.getEvent());
				} else {
					noperm(event).queue();
				}
				return;
			}
		}

		send(event, response).queue();
	}

	@Override
	public long retrievePerm() {
		return Permissions.RUN_BASECMD | Permissions.RUN_USR_CMD;
	}

	@Override
	public String toString(String language) {
		return Arrays.toString(responses.toArray());
	}
}
