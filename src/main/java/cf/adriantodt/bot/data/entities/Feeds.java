/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 12:35]
 */

package cf.adriantodt.bot.data.entities;

import cf.adriantodt.bot.commands.base.Holder;
import cf.adriantodt.bot.data.ContentManager;
import cf.adriantodt.utils.CollectionUtils;
import cf.adriantodt.utils.TaskManager;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import gui.ava.html.image.generator.HtmlImageGenerator;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Feeds {
	private static final String RENDER_PATTERN = ContentManager.resource("/assets/feed/base_render.html");
	private static final Set<Subscription> ALL = new HashSet<>();

	static {
		TaskManager.startAsyncTask("Feeds: From External", Feeds::onFeed, 60);
		TaskManager.startAsyncTask("Feeds: To Pushes", Feeds::onSendFeed, 5);
		Pushes.registerDynamicTypes(() -> ALL.stream().map(s -> "feed_" + s.pushName).collect(Collectors.toSet()), "feeds");
	}

	public static void onFeed() {
		whileOnLock(() -> {
			cleanup();
			ALL.forEach(Feeds::onFeed);
		});
	}

	public static void cleanup() {
		ALL.removeIf(s -> !s.isActive());
		ALL.removeIf(s -> Pushes.resolveTextChannels("feed_" + s.pushName).size() == 0);
	}

	public static void onFeed(Subscription subs) {
		try {
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(subs.url));
			Holder<Optional<Integer>> lastHashCode = new Holder<>(Optional.empty());
			CollectionUtils.subListOn(
				feed.getEntries(),
				entryPredicate -> subs.equalsLastHashCode(entryPredicate.getLink().hashCode())
			).forEach(entry -> {
				HtmlImageGenerator generator = new HtmlImageGenerator();
				generator.loadHtml(String.format(RENDER_PATTERN, entry.getDescription().getValue()));
				//TODO FIX THIS MESS
//				generator.saveAsImage();
//				subs.compiledPushes.add(
//					("***RSS - " + feed.getTitle() + " (Feed " + subs.pushName + " - " + feed.getLink() + ")***:\n" +
//						"**" + entry.getTitle().trim() + "**\n" +
//						"" + REMARK.convert("<html><body>" + +"</body></html>").trim() + "**\n" +
//						"" + entry.getLink() + " (at " + entry.getPublishedDate().toString() + ")")
//						.replace("\n\n", "\n")
//				);
				if (!lastHashCode.var.isPresent()) lastHashCode.var = Optional.of(entry.getLink().hashCode());
			});

			lastHashCode.var.ifPresent(subs::setLastHashCode);
		} catch (Exception e) {
			System.out.println("Something weird happened on Feeds");
			e.printStackTrace();
		}
	}

	public static void whileOnLock(Runnable r) {
		synchronized (ALL) {
			r.run();
		}
	}

	public static void onSendFeed() {
		whileOnLock(() -> {
			cleanup();
			ALL.forEach(Feeds::onSendFeed);
		});
	}

	public static void onSendFeed(Subscription subs) {
		System.out.println(subs.pushName + ".size() = " + subs.compiledPushes.size());
		if (subs.compiledPushes.size() == 0) return;
		Pushes.pushSimple("feed_" + subs.pushName, subs.compiledPushes.remove(0));
	}

	public static class Subscription {
		public final URL url;
		public final String pushName;
		List<String> compiledPushes = new ArrayList<>();
		private int lastHashCode = 0;
		private boolean active = true, loadedOnce = false;

		public Subscription(URL url, String pushName) {
			ALL.add(this);
			this.url = url;
			this.pushName = pushName;
		}

		public boolean equalsLastHashCode(int newestHashCode) {
			return !ignoreHashCode() && getLastHashCode() == newestHashCode;
		}

		public int getLastHashCode() {
			return lastHashCode;
		}

		public void setLastHashCode(int lastHashCode) {
			this.lastHashCode = lastHashCode;
			this.loadedOnce = true;
		}

		public boolean ignoreHashCode() {
			return lastHashCode != 0 || loadedOnce;
		}

		public boolean isActive() {
			return active;
		}

		public void cancel() {
			this.active = false;
		}
	}
}
