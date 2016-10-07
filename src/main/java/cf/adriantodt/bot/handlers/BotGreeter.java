/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:07]
 */

package cf.adriantodt.bot.handlers;

import cf.adriantodt.bot.base.I18n;
import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.utils.Utils;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.SubscribeEvent;

public class BotGreeter {
	public static void greet(MessageReceivedEvent event) {
		event.getChannel().sendMessage(I18n.getLocalized("bot.help", event));
	}

	@SubscribeEvent
	public static void onGuildJoin(GuildJoinEvent event) {
		try {
			Guilds.Data guild = Guilds.fromDiscord(event.getGuild());
			guild.setLang(Utils.guessGuildLanguage(event.getGuild()));
			event.getGuild().getPublicChannel().sendMessage(I18n.getLocalized("bot.hello1", guild.getLang()));
			event.getGuild().getPublicChannel().sendMessage(String.format(I18n.getLocalized("bot.hello2", guild.getLang()), event.getGuild().getOwner().getAsMention(), guild.getLang()));
		} catch (Exception e) {
			event.getGuild().getManager().leave();
		}
	}

	@SubscribeEvent
	public static void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getRawContent().trim().matches("<@!?" + event.getJDA().getSelfInfo().getId() + ">")) {
			greet(event);
		}
	}
}
