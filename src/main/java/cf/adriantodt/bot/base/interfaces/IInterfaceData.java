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

package cf.adriantodt.bot.base.interfaces;

import java.util.Map;

public interface IInterfaceData {
	/**
	 * Unhandles the Interface.<br>
	 * The Channel won't be handled my the interface anymore.
	 */
	void stop();

	/**
	 * This method makes the Message be processed AFTER the Interface is processed.<br>
	 * The Channel won't be handled my the interface anymore.
	 */
	void pass();

	/**
	 * Get the per-Channel-Session General Registry (Exclusive of this channel until {@link IInterfaceData#stop()}. After the unhandle the Registry is deleted.)
	 *
	 * @return the Volatile Registry
	 */
	Map<String, Object> getVolatile();

	/**
	 * Get the per-Session General Registry (Exclusive of the interface until the Application is closed. After close the Registry is deleted.)
	 *
	 * @return the Session Registry
	 */
	Map<String, Object> getPersistent();
}
