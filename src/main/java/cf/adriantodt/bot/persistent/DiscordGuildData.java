/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 07:55]
 */

package cf.adriantodt.bot.persistent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordGuildData {
	public Map<String, List<String>> commands = new HashMap<>();
	public Map<String, Long> userPerms = new HashMap<>();
	public String id, name;
}
