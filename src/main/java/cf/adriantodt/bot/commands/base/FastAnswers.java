/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [23/10/16 21:32]
 */

package cf.adriantodt.bot.commands.base;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.commands.Permissions;
import cf.adriantodt.bot.commands.utils.Statistics;
import cf.adriantodt.bot.data.entities.I18n;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.requests.RestAction;

import static cf.adriantodt.bot.utils.Formatter.*;
import static cf.adriantodt.bot.utils.Utils.limit;


public class FastAnswers {
	public final MessageChannel channel;
	public final CommandEvent event;

	public FastAnswers(CommandEvent event) {
		this(event.getChannel(), event);
	}

	public FastAnswers(MessageChannel channel, CommandEvent event) {
		this.channel = channel;
		this.event = event;
	}

	public FastAnswers forChannel(MessageChannel channel) {
		return new FastAnswers(channel, event);
	}

	public RestAction<Message> exception(Exception e) {
		dear("uma exceção ocorreu durante a execução do comando:");
		Bot.LOGGER.error("Exception occurred during command \"" + event.getMessage().getContent() + "\": ", e);
		Statistics.crashes++;
		return sendCased(limit(e.toString(), 500), "java");
	}

	public RestAction<Message> toofast() {
		Statistics.toofasts++;
		return send("*" + I18n.getLocalized("answers.calmDown", event) + " " + event.getAuthor().getAsMention() + "! " + I18n.getLocalized("answers.tooFast", event) + "!*");
	}

	public RestAction<Message> sendTranslated(String unlocalized) {
		return send(I18n.getLocalized(unlocalized, event));
	}

	public RestAction<Message> send(String message) {
		//Statistics.msgs++;
		event.awaitTyping();
		return event.getChannel().sendMessage(message);
	}

	public RestAction<Message> sendCased(String message) {
		return sendCased(message, "");
	}

	public RestAction<Message> sendCased(String message, String format) {
		return send(encase(message, format));
	}

	public RestAction<Message> announce(String message) {
		return send(boldAndItalic(message));
	}

	public RestAction<Message> noperm() {
		long perm = event.getCommand().retrievePerm();
		perm ^= Permissions.getSenderPerm(event.getGuild(), event) & perm;
		return noperm(perm);
	}

	public RestAction<Message> noperm(long permsMissing) {
		Statistics.noperm++;
		StringBuilder b = new StringBuilder("*(Permissões Ausentes:");
		Permissions.toCollection(permsMissing).forEach(s -> b.append(" ").append(s));
		b.append(")*");
		dear("você não tem permissão para executar esse comando.");
		return send(b.toString());
	}

	public RestAction<Message> bool(boolean v) {
		return send(v ? ":white_check_mark:" : ":negative_squared_cross_mark:");
	}

	public RestAction<Message> invalidargs() {
		Statistics.invalidargs++;
		String usage = event.getCommand().toString(I18n.getLocale(event));
		if (usage == null) return dear(I18n.getLocalized("answers.invalidArgs", event));
		else if (!usage.isEmpty()) return sendCased(usage);
		return new RestAction.EmptyRestAction<>(null);
	}

	public RestAction<Message> dear(String answer) {
		return send(italic(I18n.getLocalized("answers.dear", event) + " " + event.getAuthor().getName() + ", " + answer));
	}
}
