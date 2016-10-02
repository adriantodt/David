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
import cf.adriantodt.bot.base.I18n;
import cf.adriantodt.bot.base.Permissions;
import cf.adriantodt.bot.handlers.CommandHandler;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import static cf.adriantodt.bot.utils.Formatter.*;
import static cf.adriantodt.bot.utils.Utils.limit;


public class Answers {
	public static void exception(MessageReceivedEvent event, Exception e) {
		dear(event, "uma exceção ocorreu durante a execução do comando:");
		sendCased(event, limit(e.toString(), 500), "java");
		Bot.LOGGER.error("Exception occurred during command \"" + event.getMessage().getContent() + "\": ", e);
		Statistics.crashes++;
	}

	public static void toofast(MessageReceivedEvent event) {
		send(event, "*" + I18n.getLocalized("answers.calmDown", event) + " " + event.getAuthor().getAsMention() + "! " + I18n.getLocalized("answers.tooFast", event) + "!*");
	}

	public static void send(MessageReceivedEvent event, String message) {
		event.getChannel().sendMessageAsync(message, null);
		Statistics.msgs++;
	}

	public static void sendCased(MessageReceivedEvent event, String message) {
		sendCased(event, message, "");
	}

	public static void sendCased(MessageReceivedEvent event, String message, String format) {
		send(event, encase(message, format));
	}

	public static void announce(MessageReceivedEvent event, String message) {
		send(event, boldAndItalic(message));
	}

	public static void noperm(MessageReceivedEvent event) {
		long perm = CommandHandler.getSelf(event).retrievePerm();
		perm ^= Permissions.getSenderPerm(CommandHandler.getGuild(event), event) & perm;
		noperm(event, perm);
	}

	public static void noperm(MessageReceivedEvent event, long permsMissing) {
		StringBuilder b = new StringBuilder("*(Permissões Ausentes:");
		Permissions.toCollection(permsMissing).forEach(s -> b.append(" ").append(s));
		b.append(")*");
		dear(event, "você não tem permissão para executar esse comando.");
		send(event, b.toString());
		Statistics.noperm++;
	}

	public static void bool(MessageReceivedEvent event, boolean v) {
		send(event, (v ? ":white_check_mark:" : ":negative_squared_cross_mark:"));
	}

	public static void invalidargs(MessageReceivedEvent event) {
		String usage = CommandHandler.getSelf(event).retrieveUsage(I18n.getLang(event));
		if (usage == null) dear(event, I18n.getLocalized("answers.invalidArgs", event));
		else if (!usage.isEmpty()) sendCased(event, usage, "");
		Statistics.invalidargs++;
	}

	public static void dear(MessageReceivedEvent event, String answer) {
		send(event, italic(I18n.getLocalized("answers.dear", event) + " " + event.getAuthor().getUsername() + ", " + answer));
	}
}
