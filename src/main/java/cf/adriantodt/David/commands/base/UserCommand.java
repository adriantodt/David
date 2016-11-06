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

package cf.adriantodt.David.commands.base;

import cf.adriantodt.David.modules.db.I18nModule;
import cf.adriantodt.David.modules.db.PermissionsModule;

import cf.adriantodt.David.utils.DiscordUtils;

import java.util.*;

import static cf.adriantodt.utils.CollectionUtils.random;


//import cf.adriantodt.bot.commands.cmds.utils.scripting.JS;

public class UserCommand implements ICommand, ITranslatable {
	public List<String> responses = new ArrayList<>();

	@Override
	public void run(CommandEvent event) {
		String response = random(responses);
		if (response.length() > 7) {
//			if (response.substring(0, 6).equals("get://")) {
//				event.getAnswers().send(IOHelper.toString(response.substring(6))).queue();
//				return;
//			} else
				//} else if (response.substring(0, 6).equals("aud://")) {
				//	Audio.queue(IOHelper.newURL(response.substring(6)), event);
				//	return;
			} else if (response.substring(0, 5).equals("js://")) {
				if (PermissionsModule.havePermsRequired(event.getGuild(), event.getAuthor(), PermissionsModule.RUN_SCRIPT_CMDS)) {
					//JS.eval(event.getGuild(), response.substring(5), event.getEvent());
				} else {
					event.awaitTyping().getAnswers().noperm().queue();
				}
				return;
			}

		Map<String, String> dynamicMap = new HashMap<>();
		dynamicMap.put("event.username", event.getAuthor().getName());
		dynamicMap.put("event.nickname", event.getMember().getNickname());
		dynamicMap.put("event.name", DiscordUtils.name(event.getAuthor(), event.getGuild().getGuild(event.getJDA())));
		dynamicMap.put("event.mentionUser", event.getAuthor().getAsMention());
		dynamicMap.put("event.args", event.getArgs());
		dynamicMap.put("event.guild", event.getGuild().getName());
		event.awaitTyping().getAnswers().send(I18nModule.dynamicTranslate(response, I18nModule.getLocale(event), Optional.of(dynamicMap))).queue();
	}

	@Override
	public long retrievePerm() {
		return PermissionsModule.RUN_CMDS | PermissionsModule.RUN_USER_CMDS;
	}

	@Override
	public String toString(String language) {
		return Arrays.toString(responses.toArray());
	}
}
