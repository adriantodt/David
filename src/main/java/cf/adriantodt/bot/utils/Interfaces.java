/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:16]
 */

package cf.adriantodt.bot.utils;

import cf.adriantodt.bot.base.interfaces.IChannelInterface;

import java.util.HashMap;
import java.util.Map;

public class Interfaces {
	public static final Map<String, IChannelInterface> INTERFACES = new HashMap<>();

	public static void addInterface(String name, IChannelInterface channelInterface) {
		INTERFACES.put(name, channelInterface);
	}
}
