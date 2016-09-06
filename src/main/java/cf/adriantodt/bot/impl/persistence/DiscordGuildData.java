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

package cf.adriantodt.bot.impl.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordGuildData {
	public Map<String, List<String>> commands = new HashMap<>();
	public Map<String, Long> userPerms = new HashMap<>();
	public Map<String, Boolean> flags = new HashMap<>();
	public String id, name;
}
