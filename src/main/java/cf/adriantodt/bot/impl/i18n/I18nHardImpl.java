/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/09/16 18:35]
 */

package cf.adriantodt.bot.impl.i18n;

import java.util.Locale;

import static cf.adriantodt.bot.impl.i18n.I18n.localize;
import static java.util.Locale.ENGLISH;

/**
 * Hardcoded Impl goes here. Shouldn't be used too much.
 * It's here for Anti Data Loss regeneration.
 */
public class I18nHardImpl {
	public static final Locale PORTUGUESE = new Locale("pt-BR");

	public static void impl() {
		localize(ENGLISH, "bot.hello1", "Hello! I'm David. Someone dropped me here!");
		localize(ENGLISH, "bot.hello2", "Someone call %s to set my Default Language! (By default is English)");
		localize(ENGLISH, "bot.stop", "Stopping...");
		localize(PORTUGUESE, "bot.stop", "Saindo...");
		localize(ENGLISH, "bot.restart", "Restarting...");
		localize(PORTUGUESE, "bot.restart", "Reiniciando...");

		localize(ENGLISH, "tree.subcmds", "Sub-Commands");
		localize(PORTUGUESE, "tree.subcmds", "Sub-Comandos");
		localize(ENGLISH, "tree.default", "default");
		localize(PORTUGUESE, "tree.default", "padrão");

		localize(ENGLISH, "guild.guild", "Guild");
		localize(PORTUGUESE, "guild.guild", "Guild");
		localize(ENGLISH, "guild.admin", "Admin");
		localize(PORTUGUESE, "guild.admin", "Admin");
		localize(ENGLISH, "guild.cmds", "Commands");
		localize(PORTUGUESE, "guild.cmds", "Comandos");
		localize(ENGLISH, "guild.channels", "Channels");
		localize(PORTUGUESE, "guild.channels", "Canais");
		localize(ENGLISH, "guild.users", "Users");
		localize(PORTUGUESE, "guild.users", "Usuários");

		localize(ENGLISH, "audio.queue", "added by %s for channel &s");
		localize(PORTUGUESE, "audio.queue", "adicionado por %s para o canal %s");
		localize(ENGLISH, "audio.notInGuild", "you need to be in a Guild to queue a music.");
		localize(PORTUGUESE, "audio.notInGuild", "você tem que estar em uma Guild para pedir uma música.");
		localize(ENGLISH, "audio.notInChannel", "you need to be connected to queue a music.");
		localize(PORTUGUESE, "audio.notInChannel", "você tem que estar em conectado em um canal para pedir uma música.");
		localize(ENGLISH, "audio.notInSameChannel", "you need to be connected to the same channel as the Bot to skip a music.");
		localize(PORTUGUESE, "audio.notInSameChannel", "você tem que estar em conectado no mesmo canal do Bot para pular uma música.");
	}
}
