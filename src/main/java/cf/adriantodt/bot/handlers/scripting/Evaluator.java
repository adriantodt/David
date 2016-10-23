/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [30/09/16 08:43]
 */

package cf.adriantodt.bot.handlers.scripting;

import cf.adriantodt.bot.data.Guilds;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public interface Evaluator {
	Map<String, Evaluator> EVALUATOR_REGISTER = new HashMap<>();
	List<String> BLACKLISTED_ACCESS = new ArrayList<>(Arrays.asList(
		"java.io.*", "java.nio.*", "cf.adriantodt.bot"
	)).stream().map(s -> s.replace("*", "[\\S\\s]+?")).collect(Collectors.toList());

	void eval(Guilds.Data guild, String command, GuildMessageReceivedEvent event);
}
