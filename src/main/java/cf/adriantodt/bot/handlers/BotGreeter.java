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

import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.data.I18n;
import cf.adriantodt.bot.utils.Utils;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import static cf.adriantodt.bot.utils.Answers.sendTyping;

public class BotGreeter {
	public static void greet(GuildMessageReceivedEvent event) {
		sendTyping(event).queue();
		event.getChannel().sendMessage(I18n.getLocalized("bot.help", event)).queue();
	}

	@SubscribeEvent
	public static void onGuildJoin(GuildJoinEvent event) {
		try {
			Guilds.Data guild = Guilds.fromDiscord(event.getGuild());
			guild.setLang(Utils.guessGuildLanguage(event.getGuild()));
			event.getGuild().getPublicChannel().sendTyping().queue();
			event.getGuild().getPublicChannel().sendMessage(I18n.getLocalized("bot.hello1", guild.getLang())).queue();
			event.getGuild().getPublicChannel().sendMessage(String.format(I18n.getLocalized("bot.hello2", guild.getLang()), event.getGuild().getOwner().getAsMention(), guild.getLang())).queue();
		} catch (Exception e) {
			//TODO WAIT DV8 TO ADD LEAVE
			//event.getGuild();
		}
	}

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getMessage().getRawContent().trim().matches("<@!?" + event.getJDA().getSelfInfo().getId() + ">"))
			greet(event);
	}
}
