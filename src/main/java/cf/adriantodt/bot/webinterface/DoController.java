/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [29/10/16 09:35]
 */

package cf.adriantodt.bot.webinterface;

import com.google.gson.JsonElement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static cf.adriantodt.bot.webinterface.WebInterfaceHelper.API_CALL_NOT_FOUND;
import static cf.adriantodt.bot.webinterface.WebInterfaceHelper.error;

@RestController
public class DoController {
	public static final Map<String, Function<Map<String, String>, JsonElement>> api = new HashMap<>();

	static {
		api.put("music", map -> error("Not implemented"));
	}

	@RequestMapping("/do")
	public String api(@RequestParam Map<String, String> params) {
		return api.getOrDefault(params.getOrDefault("type", ""), API_CALL_NOT_FOUND).apply(params).toString();
	}
}
