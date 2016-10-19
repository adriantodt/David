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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import static cf.adriantodt.bot.utils.Formatter.*;
import static cf.adriantodt.bot.utils.Utils.limit;


public class Answers {
	public static RestAction<Message> exception(MessageReceivedEvent event, Exception e) {
		dear(event, "uma exceção ocorreu durante a execução do comando:");
		Bot.LOGGER.error("Exception occurred during command \"" + event.getMessage().getContent() + "\": ", e);
		Statistics.crashes++;
		return sendCased(event, limit(e.toString(), 500), "java");
	}

	public static RestAction<Message> toofast(MessageReceivedEvent event) {
		return send(event, "*" + I18n.getLocalized("answers.calmDown", event) + " " + event.getAuthor().getAsMention() + "! " + I18n.getLocalized("answers.tooFast", event) + "!*");
	}

	public static RestAction<Message> sendTranslated(MessageReceivedEvent event, String unlocalized) {
		return send(event, I18n.getLocalized(unlocalized, event));
	}

	public static RestAction<Message> send(MessageReceivedEvent event, String message) {
		Statistics.msgs++;
		return event.getChannel().sendMessage(message);
	}

	public static RestAction<Message> sendCased(MessageReceivedEvent event, String message) {
		return sendCased(event, message, "");
	}

	public static RestAction<Message> sendCased(MessageReceivedEvent event, String message, String format) {
		return send(event, encase(message, format));
	}

	public static RestAction<Message> announce(MessageReceivedEvent event, String message) {
		return send(event, boldAndItalic(message));
	}

	public static RestAction<Message> noperm(MessageReceivedEvent event) {
		long perm = CommandHandler.getSelf(event).retrievePerm();
		perm ^= Permissions.getSenderPerm(CommandHandler.getGuild(event), event) & perm;
		return noperm(event, perm);
	}

	public static RestAction<Message> noperm(MessageReceivedEvent event, long permsMissing) {
		Statistics.noperm++;
		StringBuilder b = new StringBuilder("*(Permissões Ausentes:");
		Permissions.toCollection(permsMissing).forEach(s -> b.append(" ").append(s));
		b.append(")*");
		dear(event, "você não tem permissão para executar esse comando.");
		return send(event, b.toString());
	}

	public static RestAction<Message> bool(MessageReceivedEvent event, boolean v) {
		return send(event, (v ? ":white_check_mark:" : ":negative_squared_cross_mark:"));
	}

	public static RestAction<Message> invalidargs(MessageReceivedEvent event) {
		Statistics.invalidargs++;
		String usage = CommandHandler.getSelf(event).toString(I18n.getLang(event));
		if (usage == null) return dear(event, I18n.getLocalized("answers.invalidArgs", event));
		else if (!usage.isEmpty()) return sendCased(event, usage, "");
		return new RestAction.EmptyRestAction<>(null);
	}

	public static RestAction<Message> dear(MessageReceivedEvent event, String answer) {
		return send(event, italic(I18n.getLocalized("answers.dear", event) + " " + event.getAuthor().getName() + ", " + answer));
	}
}
