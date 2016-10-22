/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:11]
 */

package cf.adriantodt.bot.utils;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.data.I18n;
import cf.adriantodt.bot.handlers.CommandHandler;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static cf.adriantodt.bot.utils.Formatter.*;
import static cf.adriantodt.bot.utils.Utils.limit;


public class Answers {
	public static RestAction<Message> exception(GuildMessageReceivedEvent event, Exception e) {
		dear(event, "uma exceção ocorreu durante a execução do comando:");
		Bot.LOGGER.error("Exception occurred during command \"" + event.getMessage().getContent() + "\": ", e);
		Statistics.crashes++;
		return sendCased(event, limit(e.toString(), 500), "java");
	}

	public static RestAction<Message> toofast(GuildMessageReceivedEvent event) {
		return send(event, "*" + I18n.getLocalized("answers.calmDown", event) + " " + event.getAuthor().getAsMention() + "! " + I18n.getLocalized("answers.tooFast", event) + "!*");
	}

	public static RestAction<Message> sendTranslated(GuildMessageReceivedEvent event, String unlocalized) {
		return send(event, I18n.getLocalized(unlocalized, event));
	}

	public static RestAction<Message> send(GuildMessageReceivedEvent event, String message) {
		Statistics.msgs++;
		return event.getChannel().sendMessage(message);
	}

	public static RestAction<Message> sendCased(GuildMessageReceivedEvent event, String message) {
		return sendCased(event, message, "");
	}

	public static RestAction<Message> sendCased(GuildMessageReceivedEvent event, String message, String format) {
		return send(event, encase(message, format));
	}

	public static RestAction<Message> announce(GuildMessageReceivedEvent event, String message) {
		return send(event, boldAndItalic(message));
	}

	public static RestAction<Message> noperm(GuildMessageReceivedEvent event) {
		long perm = CommandHandler.getSelf(event).retrievePerm();
		perm ^= Permissions.getSenderPerm(CommandHandler.getGuild(event), event) & perm;
		return noperm(event, perm);
	}

	public static RestAction<Message> noperm(GuildMessageReceivedEvent event, long permsMissing) {
		Statistics.noperm++;
		StringBuilder b = new StringBuilder("*(Permissões Ausentes:");
		Permissions.toCollection(permsMissing).forEach(s -> b.append(" ").append(s));
		b.append(")*");
		dear(event, "você não tem permissão para executar esse comando.");
		return send(event, b.toString());
	}

	public static RestAction<Message> bool(GuildMessageReceivedEvent event, boolean v) {
		return send(event, (v ? ":white_check_mark:" : ":negative_squared_cross_mark:"));
	}

	public static RestAction<Message> invalidargs(GuildMessageReceivedEvent event) {
		Statistics.invalidargs++;
		String usage = CommandHandler.getSelf(event).toString(I18n.getLocale(event));
		if (usage == null) return dear(event, I18n.getLocalized("answers.invalidArgs", event));
		else if (!usage.isEmpty()) return sendCased(event, usage, "");
		return new RestAction.EmptyRestAction<>(null);
	}

	public static RestAction<Message> dear(GuildMessageReceivedEvent event, String answer) {
		return send(event, italic(I18n.getLocalized("answers.dear", event) + " " + event.getAuthor().getName() + ", " + answer));
	}

	public static Stream<RestAction<Message>> sendLongMessage(GuildMessageReceivedEvent event, String message) {
		List<RestAction<Message>> msgs = new ArrayList<>();
		int lastIndex = 0;

		message = message.trim();

		//If it isn't it'll skip to the final part
		while (message.length() > 2000) {
			int index = message.replace('\n', ' ').indexOf(' ', lastIndex + 1); //This is just to
			if (index >= 1999) { //We should cut this thing
				if (lastIndex == 0) { //Well, brute-cut...
					msgs.add(send(event, message.substring(0, 1999)));
					message = message.substring(2000);
				} else { //Let's be gentle.
					msgs.add(send(event, message.substring(0, index)));
					message = message.substring(index + 1).trim();
					lastIndex = 0;
				}
			} else { //Keep going
				lastIndex = index;
			}
		}
		msgs.add(send(event, message));
		return msgs.stream();
	}

	public static RestAction<Void> sendTyping(GuildMessageReceivedEvent event) {
		return event.getChannel().sendTyping();
	}
}
