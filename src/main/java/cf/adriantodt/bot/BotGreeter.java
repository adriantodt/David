/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:38]
 */

package cf.adriantodt.bot;

import cf.adriantodt.bot.commands.base.Holder;
import cf.adriantodt.bot.commands.utils.Statistics;
import cf.adriantodt.bot.data.entities.Guilds;
import cf.adriantodt.bot.data.entities.I18n;
import cf.adriantodt.bot.data.entities.Users;
import cf.adriantodt.bot.utils.Utils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Optional;

import static cf.adriantodt.bot.utils.Utils.nnOrD;

public class BotGreeter {
	public static void greet(TextChannel channel, Optional<User> optionalUser) {
		Holder<String> lang = new Holder<>(Guilds.fromDiscord(channel.getGuild()).getLang());
		optionalUser.ifPresent(user -> lang.var = nnOrD(Users.fromDiscord(user).getLang(), lang.var));
		channel.sendTyping().queue(success -> {
			Statistics.restActions++;
			channel.sendMessage(I18n.getLocalized("bot.help", lang.var)).queue();
		});
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
			//event.getOriginGuild();
		}
	}

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getMessage().getRawContent().trim().matches("<@!?" + event.getJDA().getSelfInfo().getId() + ">"))
			greet(event.getChannel(), Optional.of(event.getAuthor()));
	}
}
