/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:54]
 */

package cf.adriantodt.David.modules.cmds;

import cf.adriantodt.David.commands.base.Holder;
import cf.adriantodt.David.modules.db.GuildModule;
import cf.adriantodt.David.modules.db.I18nModule;
import cf.adriantodt.David.modules.db.UserModule;
import cf.adriantodt.David.modules.init.Statistics;
import cf.adriantodt.David.utils.DiscordUtils;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.Optional;

import static cf.adriantodt.utils.StringUtils.notNullOrDefault;

public class BotGreeter {
	public static void greet(TextChannel channel, Optional<User> optionalUser) {
		Holder<String> lang = new Holder<>(GuildModule.fromDiscord(channel.getGuild()).getLang());
		optionalUser.ifPresent(user -> lang.var = notNullOrDefault(UserModule.fromDiscord(user).getLang(), lang.var));
		channel.sendTyping().queue(success -> {
			Statistics.restActions++;
			channel.sendMessage(I18nModule.getLocalized("bot.help", lang.var)).queue();
		});
	}

	@SubscribeEvent
	public static void onGuildJoin(GuildJoinEvent event) {
		try {
			GuildModule.Data guild = GuildModule.fromDiscord(event.getGuild());
			guild.setLang(DiscordUtils.guessGuildLanguage(event.getGuild()));
			event.getGuild().getPublicChannel().sendTyping().queue();
			event.getGuild().getPublicChannel().sendMessage(I18nModule.getLocalized("bot.hello1", guild.getLang())).queue();
			event.getGuild().getPublicChannel().sendMessage(String.format(I18nModule.getLocalized("bot.hello2", guild.getLang()), event.getGuild().getOwner().getAsMention(), guild.getLang())).queue();
		} catch (Exception e) {
			//TODO WAIT DV8 TO ADD LEAVE
			//event.getOriginGuild();
		}
	}

	@SubscribeEvent
	public static void onMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getMessage().getRawContent().trim().matches("<@!?" + event.getJDA().getSelfUser().getId() + ">"))
			greet(event.getChannel(), Optional.of(event.getAuthor()));
	}
}
