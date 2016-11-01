/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [29/10/16 09:24]
 */

package cf.adriantodt.bot.webinterface;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Function;

@RestController
public class WebInterfaceHelper implements ErrorController {
	public static final Function<Map<String, String>, JsonElement> API_CALL_NOT_FOUND = map -> error("API Call not found");

	public static JsonObject object() {
		JsonObject object = new JsonObject();
		object.addProperty("fine", true);
		return object;
	}

	public static JsonObject error(String error) {
		JsonObject object = object();
		object.addProperty("fine", false);
		object.addProperty("error", error);
		return object;
	}

	@RequestMapping("/error")
	public String api(@RequestParam Map<String, String> params) {
		return API_CALL_NOT_FOUND.apply(params).toString();
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}
