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

package cf.adriantodt.bot.perm;

import cf.adriantodt.bot.cmd.ICommand;
import cf.adriantodt.bot.guild.DiscordGuild;
import cf.adriantodt.bot.persistent.DataManager;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.Collator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static cf.brforgers.core.lib.MathHelper.previousPowerOfTwo;
import static cf.brforgers.core.lib.MathHelper.roundToPowerOf2;

/*
USE ESSE COMENTÁRIO PARA EXPLICAR O SISTEMA DE PERMISSÕES. OBRIGADO.

Como funciona o sistema de permissões:
	Inteiro Longo (long/64-bits). Cada permissão é colocada em um Bit, ou seja, 64 combinações:

Guia de Referência:
	LONG: !!ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba9876543210 //No caso, 0 = 2^0 e Z = 2^62
	- [!] = Bits reservados para nunca serem usados (Eles podem deixar o valor do Long grande demais...)
	- [0-9] = Bits relacionados a execução de comandos e sistema de permissão.
	- [a-z] = Bits para permissões especiais menores (Comandos específicos em geral).
	- [A-Z] = Bits para permissões especiais maiores (Opções geralmente perigosas/insegurasS).
Permissões:
	RUN_BASECMD (0) -> Permissão básica. Sem ela o Bot ignora seus comandos.
	RUN_USR_CMD (1) -> Executar Comandos de Usuário.
	RUN_LUA_CMD (2) -> TO BE IMPL. Executar Comandos de Usuário em Lua.
	PERMSYSTEM  (3) -> Sistema de Permissões.
	GUILD_PASS  (4) -> Permite o acesso a comandos dessa Guild por meio de &GUILD:<command>
	MANAGE_CMDS (5) -> Criação e Exclusão de comandos na Guild.
	MANAGE_SPCS (6) -> Permite a criação de comandos WEB:// ou LUA://
	PERMSYSTEM_ASSIST_PERMS (7-9)
	                -> Protege pessoas de nível maior de serem afetadas por pessoas com nível maior.
	PLAYING     (a) -> Comando &jogando
	LUA         (b) -> Comando &lua
	GUILD       (c) -> Comando &guild
	SPY         (d) -> Comando &spy
	GLOBALS_IMP (e) -> Comando &globals e subcomando import
	GLOBALS_EXP (f) -> Subcomando export
	(g-z) -> Free Slots
	INTERFACES  (A) -> Usar as Interfaces
	LUAENV_FULL (B) -> Lua pode ser executado/compilado no Ambiente Inseguro
	(C-X) -> Free Slots
	SAVE_LOAD   (Y) -> Salvar ou Carregar do Disco os Comandos/Guilds/Permissões
	STOP_RESET  (Z) -> Parar ou Reiniciar o Bot
 */
public class Permissions {
	public static final long
		RUN_BASECMD = bits(0),
		RUN_USR_CMD = bits(1),
		RUN_LUA_CMD = bits(2),
		PERMSYSTEM = bits(3),
		GUILD_PASS = bits(4),
		MANAGE_USR = bits(5),
		MANAGE_SPCS = bits(6),
		PERMSYS_GM = bits(7),
		PERMSYS_GO = bits(8),
		PERMSYS_BO = bits(9),
		PLAYING = bits(10),
		LUA = bits(11),
		GUILD = bits(12),
		SPY = bits(13),
		GLOBALS_IMP = bits(14),
		GLOBALS_EXP = bits(15),
		INTERFACES = bits(36),
		LUAENV_FULL = bits(37),
		SAVE_LOAD = bits(61),
		STOP_RESET = bits(62);

	public static final long
		BASE_USER = RUN_BASECMD | RUN_USR_CMD | RUN_LUA_CMD | GUILD_PASS | INTERFACES,
		GUILD_MOD = BASE_USER | MANAGE_USR | MANAGE_SPCS | PERMSYSTEM | PERMSYS_GM | GLOBALS_IMP,
		GUILD_OWNER = GUILD_MOD | GLOBALS_EXP | LUA | GUILD | PERMSYS_GO,
		BOT_OWNER = GUILD_OWNER | PLAYING | LUAENV_FULL | SPY | SAVE_LOAD | STOP_RESET | PERMSYS_BO;

	public static Map<String, Long> perms = new HashMap<String, Long>() {{
		for (Field field : Permissions.class.getDeclaredFields()) //This Reflection is used to HashMap-fy all the Fields above.
			if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) //public static final fields only
				try {
					put(field.getName(), field.getLong(null));
				} catch (Exception ignored) {
				}
	}};

	private static long bits(long... bits) {
		long mask = 0;
		for (long bit : bits) {
			mask |= (long) Math.pow(2, bit);
		}
		return mask;
	}

	public static long getSenderPerm(DiscordGuild guild, MessageReceivedEvent event) {
		return getPermFor(guild, event.getAuthor().getId());
	}

	public static boolean setPerms(DiscordGuild guild, MessageReceivedEvent event, String target, long permsToAdd, long permsToTake) {
		target = processID(target); //Un-mention ID
		if (target.equals(event.getAuthor().getId())) return false; //Disable changing itself
		long senderPerm = getSenderPerm(guild, event), targetPerm = getPermFor(guild, target); //Get perrms
		if (!checkPerms(senderPerm, targetPerm)) return false; //Check the Special Bits
		if ((senderPerm & (permsToAdd | permsToTake)) != (permsToAdd | permsToTake))
			return false; //Check if the Sender Perm have all the permissions
		guild.userPerms.put(target, targetPerm ^ (targetPerm & permsToTake) | permsToAdd);
		return true;
	}

	public static boolean checkPerms(long senderPerm, long targetPerm) {
		long perms = bits(13, 14, 15);
		senderPerm &= perms;
		targetPerm &= perms; //Select bits 13 14 15
		targetPerm = previousPowerOfTwo(roundToPowerOf2(targetPerm));
		senderPerm = previousPowerOfTwo(roundToPowerOf2(senderPerm)); //Get the biggest
		return targetPerm <= senderPerm;
	}

	public static long getPermFor(DiscordGuild guild, String target) {
		target = processID(target);
		long global = DiscordGuild.GLOBAL.userPerms.getOrDefault(target, 0L), unrevokeable = (target.equals(processID(DataManager.options.owner)) ? BOT_OWNER : (guild.guild != null && guild.guild.getOwnerId().equals(target)) ? GUILD_OWNER : 0);
		return global | guild.userPerms.getOrDefault(target, (global == 0 ? guild.userPerms.getOrDefault("default", BASE_USER) : global)) | unrevokeable;
		//this will merge the Global Perms, the Local Perms, and Unrevokeable Perms (BOT_OWNER or GUILD_OWNER)
	}

	public static boolean canRunCommand(DiscordGuild guild, MessageReceivedEvent event, ICommand cmd) {
		return havePermsRequired(guild, event, cmd.retrievePerm());
	}

	public static boolean havePermsRequired(DiscordGuild guild, MessageReceivedEvent event, long perms) {
		return (perms & getSenderPerm(guild, event)) == perms;
	}

	public static boolean isMention(String string) {
		return (string.charAt(0) == '<' && string.charAt(1) == '@' && string.charAt(string.length() - 1) == '>');
	}

	public static String processID(String string) {
		if (isMention(string)) string = string.substring(2, string.length() - 1);
		if (string.charAt(0) == '!') string = string.substring(1);
		return string.toLowerCase();
	}

	public static Collection<String> toCollection(long perms) {
		Collection<String> collection = new TreeSet<>(Collator.getInstance());

		Permissions.perms.forEach((pName, pBits) -> {
			if ((pBits & perms) == pBits) collection.add(pName);
		});

		return collection;
	}
}
