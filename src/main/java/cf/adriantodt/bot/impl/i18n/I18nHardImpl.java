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

import static cf.adriantodt.bot.impl.i18n.I18n.localize;

/**
 * Hardcoded Impl goes here. Shouldn't be used too much.
 * It's here for Anti Data Loss regeneration.
 */
public class I18nHardImpl {

	public static void impl() {
		localize("en_US", "bot.hello1", "Hello! I'm David. Someone dropped me here!");
		localize("en_US", "bot.hello2", "Someone call %s to set my Default Language! (By default is English)");
		localize("en_US", "bot.stop", "Stopping...");
		localize("pt_BR", "bot.stop", "Saindo...");
		localize("en_US", "bot.restart", "Restarting...");
		localize("pt_BR", "bot.restart", "Reiniciando...");

		localize("en_US", "tree.subcmds", "Sub-Commands");
		localize("pt_BR", "tree.subcmds", "Sub-Comandos");
		localize("en_US", "tree.default", "default");
		localize("pt_BR", "tree.default", "padrão");

		localize("en_US", "guild.guild", "Guild");
		localize("pt_BR", "guild.guild", "Guild");
		localize("en_US", "guild.admin", "Admin");
		localize("pt_BR", "guild.admin", "Admin");
		localize("en_US", "guild.cmds", "Commands");
		localize("pt_BR", "guild.cmds", "Comandos");
		localize("en_US", "guild.channels", "Channels");
		localize("pt_BR", "guild.channels", "Canais");
		localize("en_US", "guild.users", "Users");
		localize("pt_BR", "guild.users", "Usuários");

		localize("en_US", "audio.queue", "added by %s for channel &s");
		localize("pt_BR", "audio.queue", "adicionado por %s para o canal %s");
		localize("en_US", "audio.notInGuild", "you need to be in a Guild to use audio commands.");
		localize("pt_BR", "audio.notInGuild", "você tem que estar em uma Guild para usar comandos de áudio.");
		localize("en_US", "audio.notInChannel", "you need to be connected to a channel to use audio commands.");
		localize("pt_BR", "audio.notInChannel", "você tem que estar em conectado em um canal para usar comandos de áudio.");
		localize("en_US", "audio.notInSameChannel", "you need to be connected to the same channel as the Bot to skip a music.");
		localize("pt_BR", "audio.notInSameChannel", "você tem que estar em conectado no mesmo canal do Bot para pular uma música.");

		localize("en_US", "playing.nowPlaying", "Now I'm playing %s!");
		localize("pt_BR", "playing.nowPlaying", "Agora estou jogando %s!");
		localize("en_US", "playing.notPlaying", "Now I'm not playing!");
		localize("pt_BR", "playing.notPlaying", "Agora não estou jogando!");

		localize("en_US", "user.none", "none");
		localize("pt_BR", "user.none", "nenhum");
		localize("en_US", "user.avatar", "Avatar");
		localize("pt_BR", "user.avatar", "Avatar");
		localize("en_US", "user.name", "Name");
		localize("pt_BR", "user.name", "Nome");
		localize("en_US", "user.nick", "Nick");
		localize("pt_BR", "user.nick", "Apelido");
		localize("en_US", "user.roles", "Roles");
		localize("pt_BR", "user.roles", "Cargos");
		localize("en_US", "user.memberSince", "Member Since");
		localize("pt_BR", "user.memberSince", "Membro Desde");
		localize("en_US", "user.commonGuilds", "Common Guilds");
		localize("pt_BR", "user.commonGuilds", "Guilds em Comum");
		localize("en_US", "user.status", "Status");
		localize("pt_BR", "user.status", "Status");
		localize("en_US", "user.playing", "Playing");
		localize("pt_BR", "user.playing", "Jogando");

		localize("en_US", "play.usage", "Play an audio track from internet.\nOnly direct link are accepted (Youtube is not supported.)");
		localize("pt_BR", "play.usage", "Toca uma faixa de áudio da internet.\nSó são aceitos links diretos (YouTube não é suportado.)");

		localize("en_US", "queue.queue", "Queue");
		localize("pt_BR", "queue.queue", "Fila");
		localize("en_US", "queue.noMusics", "no musics added");
		localize("pt_BR", "queue.noMusics", "nenhuma música atualmente");

		localize("en_US", "skip.usage", "Skip current audio track.");
		localize("pt_BR", "skip.usage", "Pula a faixa de áudio atual.");

		localize("en_US", "lang.set", "Now I'll speak in %s to you!");
		localize("pt_BR", "lang.set", "Agora eu vou falar em %s para você!");
		localize("en_US", "lang.setNone", "Now I'll speak the Guild's Default Language!");
		localize("pt_BR", "lang.setNone", "Agora eu vou falar na lingua padrão da Guild!");

		localize("en_US", "inviteme.link", "Click the Link to invite the Bot to your Guild");
		localize("pt_BR", "inviteme.link", "Clique no Link para convidar o Bot para a sua Guild");

		localize("en_US", "guild.info.usage", "Show Guild info.");
		localize("pt_BR", "guild.info.usage", "Mostre informações sobre a Guild.");
		localize("en_US", "guild.list.usage", "List the Guild channels.");
		localize("pt_BR", "guild.list.usage", "Mostre os canais da Guild.");
		localize("en_US", "guild.broadcast.usage", "Send a message to all channels of the Guild.");
		localize("pt_BR", "guild.broadcast.usage", "Envie uma mensagem para todos os canais da Guild.");

		localize("en_US", "answers.dear", "Dear");
		localize("pt_BR", "answers.dear", "Prezado");
	}
}
