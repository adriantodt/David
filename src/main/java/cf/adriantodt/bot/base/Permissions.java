/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [12/09/16 07:38]
 */

package cf.adriantodt.bot.base;

import cf.adriantodt.bot.base.cmd.ICommand;
import cf.adriantodt.bot.data.DataManager;
import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.utils.Utils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	RUN_SCT_CMD (2) -> TO BE IMPL. Executar Comandos de Usuário em Lua.
	PERMSYSTEM  (3) -> Sistema de Permissões.
	GUILD_PASS  (4) -> Permite o acesso a comandos dessa Guild por meio de &GUILD:<command>
	MANAGE_CMDS (5) -> Criação e Exclusão de comandos na Guild.
	MANAGE_SPCS (6) -> Permite a criação de comandos WEB:// ou SCRIPTS://
	PERMSYSTEM_ASSIST_PERMS (7-9)
	                -> Protege pessoas de nível maior de serem afetadas por pessoas com nível maior.
	PLAYING     (a) -> Comando &jogando
	SCRIPTS     (b) -> Comando &eval (JS), &lua (LuaJ)
	EDIT_GUILD  (c) -> Comando &guild
	SPY         (d) -> Comando &spy
	GLOBALS_IMP (e) -> Comando &globals e subcomando import
	GLOBALS_EXP (f) -> Subcomando export
	ANNOY       (g) -> Annoy command
	(h-z) -> Free Slots
	USE_INTERFACES  (A) -> Usar as Interfaces
	LUAENV_FULL (B) -> Lua pode ser executado/compilado no Ambiente Inseguro
	(C-X) -> Free Slots
	SAVE_LOAD   (Y) -> Salvar ou Carregar do Disco os Comandos/Guilds/Permissões (Unused)
	STOP_RESET  (Z) -> Parar ou Reiniciar o Bot
 */
public class Permissions {
	public static final long
		RUN_BASECMD = bits(0),
		RUN_USR_CMD = bits(1),
		RUN_SCT_CMD = bits(2), //SCripT
		PERMSYSTEM = bits(3),
		GUILD_PASS = bits(4),
		MANAGE_USR = bits(5),
		MANAGE_SPCS = bits(6),
		PERMSYS_GM = bits(7),
		PERMSYS_GO = bits(8),
		PERMSYS_BO = bits(9),
		PLAYING = bits(10),
		SCRIPTS = bits(11),
		EDIT_GUILD = bits(12),
		SPY = bits(13),
		GLOBALS_IMP = bits(14),
		GLOBALS_EXP = bits(15),
		ANNOY = bits(16),
		USE_INTERFACES = bits(36),
		LUAENV_FULL = bits(37),
	//		SAVE_LOAD = bits(61),
	STOP_RESET = bits(62);

	public static final long
		BASE_USER = RUN_BASECMD | RUN_USR_CMD | RUN_SCT_CMD | GUILD_PASS | USE_INTERFACES,
		GUILD_MOD = BASE_USER | MANAGE_USR | MANAGE_SPCS | PERMSYSTEM | PERMSYS_GM | GLOBALS_IMP,
		GUILD_OWNER = GUILD_MOD | GLOBALS_EXP | SCRIPTS | EDIT_GUILD | PERMSYS_GO,
		BOT_OWNER = GUILD_OWNER | PLAYING | LUAENV_FULL | SPY | STOP_RESET | PERMSYS_BO | ANNOY;

	public static Map<String, Long> perms = new HashMap<String, Long>() {{
		Arrays.stream(Permissions.class.getDeclaredFields()) //This Reflection is used to HashMap-fy all the Fields above.
			.filter(field -> Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) //public static final fields only
			.forEach(field -> {
				try {
					put(field.getName(), field.getLong(null));
				} catch (Exception ignored) {
				}
			});
	}};

	private static long bits(long... bits) {
		long mask = 0;
		for (long bit : bits) {
			mask |= (long) Math.pow(2, bit);
		}
		return mask;
	}

	public static long getSenderPerm(Guilds.Data guild, GuildMessageReceivedEvent event) {
		return getPermFor(guild, event.getAuthor().getId());
	}

	public static boolean setPerms(Guilds.Data guild, GuildMessageReceivedEvent event, String target, long permsToAdd, long permsToTake) {
		target = Utils.processId(target); //Un-mention ID
		if (target.equals(event.getAuthor().getId())) return false; //Disable changing itself
		long senderPerm = getSenderPerm(guild, event), targetPerm = getPermFor(guild, target); //Get perrms
		if (!checkPerms(senderPerm, targetPerm)) return false; //Check the Special Bits
		if ((senderPerm & (permsToAdd | permsToTake)) != (permsToAdd | permsToTake))
			return false; //Check if the Sender Perm have all the permissions
		guild.setUserPerms(target, targetPerm ^ (targetPerm & permsToTake) | permsToAdd);
		return true;
	}

	public static boolean checkPerms(long senderPerm, long targetPerm) {
		long perms = bits(7, 8, 9);
		senderPerm &= perms;
		targetPerm &= perms; //Select bits 7 8 9
		targetPerm = previousPowerOfTwo(roundToPowerOf2(targetPerm));
		senderPerm = previousPowerOfTwo(roundToPowerOf2(senderPerm)); //Get the biggest
		return targetPerm <= senderPerm;
	}

	public static long getPermFor(Guilds.Data guild, String target) {
		target = Utils.processId(target);
		long global = Guilds.GLOBAL.getUserPerms(target, 0L), unrevokeable = (target.equals(Utils.processId(DataManager.configs.ownerID)) || target.equals("console") ? BOT_OWNER : (guild.getGuild() != null && guild.getGuild().getOwner().getUser().getId().equals(target)) ? GUILD_OWNER : 0);
		return global | guild.getUserPerms(target, (global == 0 ? guild.getUserPerms("default", BASE_USER) : global)) | unrevokeable;
		//this will merge the Global Perms, the Local Perms, and Unrevokeable Perms (BOT_OWNER or GUILD_OWNER)
	}

	public static boolean canRunCommand(Guilds.Data guild, GuildMessageReceivedEvent event, ICommand cmd) {
		return havePermsRequired(guild, event, cmd.retrievePerm());
	}

	public static boolean havePermsRequired(Guilds.Data guild, GuildMessageReceivedEvent event, long perms) {
		return (perms & getSenderPerm(guild, event)) == perms;
	}

	public static List<String> toCollection(long userPerms) {
		return perms
			.entrySet()
			.stream()
			.filter(entry -> (entry.getValue() & userPerms) == entry.getValue())
			.map(Map.Entry::getKey)
			.sorted(String::compareTo).collect(Collectors.toList());
	}
}
