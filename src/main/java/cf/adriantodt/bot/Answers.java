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

package cf.adriantodt.bot;

import cf.adriantodt.bot.impl.EventHandler;
import cf.adriantodt.bot.perm.Permissions;
import net.dv8tion.jda.events.message.MessageReceivedEvent;


public class Answers {
	public static void exception(MessageReceivedEvent event, Exception e) {
		prezado(event, "uma exceção ocorreu durante a execução do comando:");
		sendCased(event, limit(e.toString(), 500), "java");
		Bot.LOGGER.error("Exception occurred during command \"" + event.getMessage().getContent() + "\": ", e);
		Statistics.crashes++;
	}

	public static void toofast(MessageReceivedEvent event) {
		send(event, "*Acalme-se " + event.getAuthor().getUsername() + "! Você está executando comandos rápido de mais!*");
	}

	public static void send(MessageReceivedEvent event, String message) {
		event.getChannel().sendMessage(message);
		Statistics.msgs++;
	}

	public static void sendCased(MessageReceivedEvent event, String message) {
		sendCased(event, message, "xl");
	}

	public static void sendCased(MessageReceivedEvent event, String message, String format) {
		send(event, "```" + format + "\n" + message + "\n```");
	}

	public static void announce(MessageReceivedEvent event, String message) {
		send(event, "***" + message + "***");
	}

	public static void noperm(MessageReceivedEvent event) {
		long perm = EventHandler.getSelf(event).retrievePerm();
		perm ^= Permissions.getSenderPerm(EventHandler.getGuild(event), event) & perm;
		StringBuilder b = new StringBuilder("*(Permissões Ausentes:");
		Permissions.toCollection(perm).forEach(s -> b.append(" ").append(s));
		b.append(")*");
		prezado(event, "você não tem permissão para executar esse comando.");
		send(event, b.toString());
		Statistics.noperm++;
	}

	public static void bool(MessageReceivedEvent event, boolean v) {
		send(event, (v ? ":white_check_mark:" : ":negative_squared_cross_mark:"));
	}

	public static void invalidargs(MessageReceivedEvent event) {
		String usage = EventHandler.getSelf(event).retrieveUsage();
		if (usage == null) prezado(event, "você enviou argumento(s) incorreto(s) para o comando.");
		else if (!usage.isEmpty()) sendCased(event, usage, "");
		Statistics.invalidargs++;
	}

	public static void nonoverwriteable(MessageReceivedEvent event) {
		prezado(event, "o comando enviado como argumento não é volátil, e portanto não pode ser modificado.");
		Statistics.invalidargs++;
	}

	public static void prezado(MessageReceivedEvent event, String resposta) {
		send(event, "*Prezado " + event.getAuthor().getUsername() + ", " + resposta + "*");
	}

	public static String limit(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length - 3);
			buf.append("...");
		}

		return buf.toString();
	}
}
