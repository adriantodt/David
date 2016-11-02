/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 22:37]
 */

package cf.adriantodt.bot.data.entities;

import cf.adriantodt.bot.data.ConfigUtils;
import cf.adriantodt.utils.ssh.EzSSH;
import cf.brforgers.core.lib.IOHelper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gui.ava.html.image.generator.HtmlImageGenerator;

import javax.naming.CannotProceedException;
import java.io.File;
import java.net.URL;
import java.util.function.Predicate;

@SuppressWarnings("ConstantConditions")
public class FeedingUtil {
	public static final JsonObject conf = ConfigUtils.get(
		"feeds",
		ImmutableMap.<String, Predicate<JsonElement>>builder()
			.put("sshConnectionString", ConfigUtils::isJsonString)
			.put("httpPath", ConfigUtils::isJsonString)
			.build(),
		() -> {
			JsonObject object = new JsonObject();
			object.addProperty("sshConnectionString", "ssh://user:pass@host/home");
			object.addProperty("httpPath", "http://example.com/host/");
			return object;
		},
		false,
		true
	);

	public static String shorten(String url) {
		return IOHelper.toString("https://is.gd/create.php?format=simple&url=" + url);
	}

	public static URL shorten(URL url) {
		return IOHelper.newURL(shorten(url.toString()));
	}

	public static String handleHTML(String html, String fileName) {
		try {
			HtmlImageGenerator generator = new HtmlImageGenerator();
			generator.loadHtml(html);
			File image = File.createTempFile(fileName, ".png");
			generator.saveAsImage(image);
			EzSSH.sftp(image.toURI().toString(), conf.get("sshConnectionString").getAsString() + image.getName());
			image.delete();
			return shorten(conf.get("httpPath").getAsString() + image.getName());
		} catch (Exception e) {

		}
		throw new RuntimeException(new CannotProceedException());
	}
}
