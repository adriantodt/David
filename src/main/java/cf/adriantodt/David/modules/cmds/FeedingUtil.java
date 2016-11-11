/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [11/11/16 08:20]
 */

package cf.adriantodt.David.modules.cmds;

import cf.adriantodt.David.modules.cmds.FeedCmd;
import cf.adriantodt.David.modules.db.I18nModule;
import cf.adriantodt.utils.EncodingUtil;
import cf.adriantodt.utils.HTML2Discord;
import cf.adriantodt.utils.PatternCollection;
import cf.brforgers.core.lib.IOHelper;
import com.rometools.rome.feed.synd.SyndEntry;
import net.dv8tion.jda.core.entities.TextChannel;

import java.net.URL;
import java.util.function.Function;

import static cf.adriantodt.utils.PatternCollection.compileReplace;
import static cf.adriantodt.utils.StringUtils.limit;

@SuppressWarnings("ConstantConditions")
public class FeedingUtil {

	public static String shorten(String url) {
		return IOHelper.toString("https://is.gd/create.php?format=simple&url=" + EncodingUtil.encodeURIComponent(url));
	}

	public static String shorten(String url, String shorturl) {
		System.out.println("https://is.gd/create.php?format=simple&url=" + EncodingUtil.encodeURIComponent(url) + "&shorturl=" + shorturl);
		return IOHelper.toString("https://is.gd/create.php?format=simple&url=" + EncodingUtil.encodeURIComponent(url) + "&shorturl=" + shorturl);
	}

	public static URL shorten(URL url) {
		return IOHelper.newURL(shorten(url.toString()));
	}

	public static URL shorten(URL url, String shorturl) {
		return IOHelper.newURL(shorten(url.toString(), shorturl));
	}

	public static Function<TextChannel, String> handleEntry(final FeedCmd.Subscription subscription, final SyndEntry feed) {
		//Compile static things
		Function<TextChannel, String> chunk2, chunk4, chunk6, chunk7;
		String chunk1 = "***:envelope_with_arrow: - ";
		String chunk3 = " (:bell:: `" + subscription.pushName + "` -" + subscription.url.toString() + ")***"
			+ (feed.getDescription() != null ? "\n" + limit(HTML2Discord.toDiscordFormat(feed.getDescription().getValue()), 750) : "") +
			"\n***:envelope:: ";
		String chunk5 = " (";
		String chunk8 = ")***";

		if (feed.getTitle() != null) {
			String titleStatic = limit(compileReplace(PatternCollection.MULTIPLE_LINES, "\n").apply(HTML2Discord.toPlainText(feed.getTitle())), 70);
			chunk2 = c -> titleStatic;
		} else {
			chunk2 = c -> I18nModule.getLocalized("feed.untitled", c);
		}

		if (feed.getLink() != null) {
			String linkStatic = shorten(HTML2Discord.toPlainText(feed.getLink()));
			chunk4 = c -> linkStatic;
		} else {
			chunk4 = c -> I18nModule.getLocalized("feed.unknown", c);
		}

		chunk6 = c -> I18nModule.getLocalized("feed.at", c);

		if (feed.getPublishedDate() != null) {
			String dateStatic = feed.getPublishedDate().toString();
			chunk7 = c -> dateStatic;
		} else {
			chunk7 = c -> I18nModule.getLocalized("feed.unknown", c);
		}

		return channel -> chunk1 + chunk2.apply(channel) + chunk3 + chunk4.apply(channel) + chunk5 + chunk6.apply(channel) + " " + chunk7.apply(channel) + chunk8;
	}
}
