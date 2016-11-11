/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [11/11/16 08:25]
 */

package cf.adriantodt.David.loader.entities;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public interface ModuleResourceManager {
	String get(String path);

	default JsonElement getAsJson(String path) {
		return new JsonParser().parse(get(path));
	}
}
