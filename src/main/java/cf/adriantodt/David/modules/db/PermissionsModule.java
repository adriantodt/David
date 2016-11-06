/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:53]
 */

package cf.adriantodt.David.modules.db;

import cf.adriantodt.David.commands.base.CommandEvent;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.JDAInstance;
import cf.adriantodt.David.loader.Module.Type;
import cf.adriantodt.David.utils.DiscordUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

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
	(0)		RUN_CMDS
				Basic Permission. If took from User, the Bot will ignore any commands.
	(1)		RUN_USER_CMDS
				Execute User Coomands. May be took from User because of spam.
	(2)		RUN_SCRIPT_CMDS
				Run Script Commands. Disabled from everyone until proper Sandboxing.
	(3)		SET_PERMS
				Set others users Permissions (using &guild perms set)
	(4)		GUILD_PASS
				Access the Guild in another using &GUILD:<command>
	(5)		MANAGE_USER_CMDS
				Allows creating and removing User Commands using &cmds add/rm
	(6)		MANAGE_SPECIAL_USER_CMDS
				Allows creating and removing User Commands that do special things
	(7-9)	PERMSYS_GM/PERMSYS_GO/PERMSYS_BO
				Assist Perms. Protects people with higher perms from being affected by lower rank people
	(10/a)	PUSH_SUBSCRIBE
				Subscribe the channel to Push Notifications.
	(11/b)	SCRIPTS
				Run Scripts
	(12/c)	SET_GUILD
				Set Guild configs
	(13/d)	PUSH_SEND
				Send Push Notifications
	(36/A)	USE_INTERFACES
				Use Interfaces (Currently broken)
	(37/B)	SCRIPTS_UNSAFEENV
				Run Commands in a unsafe environiment (Disabled until proper Sandboxing)
	(62/Z)	STOP_BOT
				Stops/Resets the Bot
 */
@Module(Type.STATIC)
public class PermissionsModule {
	public static final long
		RUN_CMDS = bits(0),
		RUN_USER_CMDS = bits(1),
		RUN_SCRIPT_CMDS = bits(2), //SCripT
		SET_PERMS = bits(3),
		GUILD_PASS = bits(4),
		MANAGE_USER_CMDS = bits(5),
		MANAGE_SPECIAL_USER_CMDS = bits(6),
		PERMSYS_GM = bits(7),
		PERMSYS_GO = bits(8),
		PERMSYS_BO = bits(9),
		PUSH_SUBSCRIBE = bits(10),
		SCRIPTS = bits(11),
		SET_GUILD = bits(12),
		PUSH_SEND = bits(13),
		USE_INTERFACES = bits(36),
		SCRIPTS_UNSAFEENV = bits(37),
		STOP_BOT = bits(62);
	public static final long
		BASE_USER = RUN_CMDS | RUN_USER_CMDS | GUILD_PASS | USE_INTERFACES,
		GUILD_MOD = BASE_USER | MANAGE_USER_CMDS | MANAGE_SPECIAL_USER_CMDS | SET_PERMS | PERMSYS_GM | PUSH_SUBSCRIBE,
		GUILD_OWNER = GUILD_MOD | SCRIPTS | SET_GUILD | PERMSYS_GO,
		BOT_OWNER = GUILD_OWNER | SCRIPTS_UNSAFEENV | PUSH_SEND | STOP_BOT | PERMSYS_BO | RUN_SCRIPT_CMDS;
	public static Map<String, Long> perms = new HashMap<String, Long>() {{
		Arrays.stream(PermissionsModule.class.getDeclaredFields()) //This Reflection is used to HashMap-fy all the Fields above.
			.filter(field -> Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) //public static final fields only
			.forEach(field -> {
				try {
					put(field.getName(), field.getLong(null));
				} catch (Exception ignored) {
				}
			});
	}};
	@JDAInstance
	private static JDA jda = null;

	private static long bits(long... bits) {
		long mask = 0;
		for (long bit : bits) {
			mask |= (long) Math.pow(2, bit);
		}
		return mask;
	}

	public static long getSenderPerm(GuildModule.Data guild, CommandEvent event) {
		return getPermFor(guild, event.getAuthor().getId());
	}

	public static boolean setPerms(GuildModule.Data guild, CommandEvent event, String target, long permsToAdd, long permsToTake) {
		target = DiscordUtils.processId(target); //Un-mention ID
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

	public static long getPermFor(GuildModule.Data guild, String target) {
		target = DiscordUtils.processId(target);
		long global = GuildModule.GLOBAL.getUserPerms(target, 0L), unrevokeable = (target.equals(DiscordUtils.processId(DBModule.getConfig().get("ownerID").getAsString())) || target.equals("console") ? BOT_OWNER : (guild.getGuild(jda) != null && guild.getGuild(jda).getOwner().getUser().getId().equals(target)) ? GUILD_OWNER : 0);
		return global | guild.getUserPerms(target, (global == 0 ? guild.getUserPerms("default", BASE_USER) : global)) | unrevokeable;
		//this will merge the Global Perms, the Local Perms, and Unrevokeable Perms (BOT_OWNER or GUILD_OWNER)
	}

	public static boolean canRunCommand(GuildModule.Data guild, CommandEvent event) {
		return havePermsRequired(guild, event.getAuthor(), event.getCommand().retrievePerm());
	}

	public static boolean havePermsRequired(GuildModule.Data guild, User user, long perms) {
		return (perms & getPermFor(guild, user.getId())) == perms;
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
