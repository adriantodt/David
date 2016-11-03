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
import cf.adriantodt.utils.AsyncUtils;
import cf.adriantodt.utils.CollectionUtils;
import cf.adriantodt.utils.EncodingUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import net.dv8tion.jda.core.entities.TextChannel;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

import static cf.adriantodt.bot.data.DataManager.*;
import static cf.adriantodt.utils.Log4jUtils.logger;
import static cf.brforgers.core.lib.IOHelper.newURL;

public class Feeds {
	private static final Set<Subscription> ALL = Collections.synchronizedSet(new HashSet<>());
	private static final Set<String> ALL_TYPES = Collections.synchronizedSet(new HashSet<>());
	private static int i = 0;

	static {
		AsyncUtils.asyncSleepThen(100, () -> {
			Pushes.registerDynamicTypes(() -> ALL_TYPES, "feeds");

			h.from(r.table("feeds").run(conn)).cursorExpected().forEach(json -> {
				JsonObject feed = json.getAsJsonObject();
				new Subscription(
					feed.get("pushName").getAsString(),
					newURL(EncodingUtil.decodeURIComponent(feed.get("url").getAsString())),
					feed.get("id").getAsString()
				).setLastHashCode(feed.get("lastHashCode").getAsInt());
			});

			i = 1;
		}).run();
	}

	public static void loop() {
		System.out.println("Loop();");
		System.out.println("ALL.size() = " + ALL.size());
		cleanup();
		ALL.forEach(Feeds::onSendFeed);
		if (i == 0) {
			ALL.forEach(Feeds::onFeed);
			i = 12;
		} else i--;
		System.out.println("Loop.End;");
	}

	public static void cleanup() {
		ALL.removeIf(s -> !s.isActive());
		ALL.removeIf(s -> Pushes.resolveTextChannels("feed_" + s.pushName).size() == 0);
	}

	public static void onFeed(Subscription subs) {
		try {
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(subs.url));
			Holder<Integer> i = new Holder<>(0);
			Lists.reverse(CollectionUtils.subListOn(
				feed.getEntries(),
				entryPredicate -> subs.equalsLastHashCode(entryPredicate.getLink().hashCode())
			)).forEach(entry -> {
				i.var++;
				subs.compiledPushes.add(FeedingUtil.handleEntry(subs, entry));
				subs.setLastHashCode(entry.getLink().hashCode());
			});
			if (i.var != 0) System.out.println(subs.pushName + ".size() += " + i.var);
		} catch (Exception e) {
			logger().error("Error while creating messages", e);
		}
	}

	public static void onSendFeed(Subscription subs) {
		if (subs.compiledPushes.size() == 0) return;
		System.out.println(subs.pushName + ".size() = " + subs.compiledPushes.size());
		AsyncUtils.async(() -> Pushes.pushSimple("feed_" + subs.pushName, subs.compiledPushes.remove(0))).run();
	}

	public static class Subscription {
		public final URL url;
		public final String pushName, id;
		List<Function<TextChannel, String>> compiledPushes = new ArrayList<>();
		private int lastHashCode = 0;
		private boolean active = true, loadedOnce = false;

		public Subscription(String pushName, URL url) {
			this(pushName, url, h.from(r.table("feeds").insert(
				r.hashMap("pushName", pushName)
					.with("url", EncodingUtil.encodeURIComponent(url.toString()))
					.with("lastHashCode", 0)
			).run(conn)).mapExpected().get("generated_keys").getAsJsonArray().get(0).getAsString());
		}

		private Subscription(String pushName, URL url, String id) {
			this.url = url;
			this.pushName = pushName;
			this.id = id;
			ALL.add(this);
			ALL_TYPES.add("feed_" + pushName);
		}

		public boolean equalsLastHashCode(int newestHashCode) {
			return ignoreHashCode() || getLastHashCode() == newestHashCode;
		}

		public int getLastHashCode() {
			return lastHashCode;
		}

		public void setLastHashCode(int lastHashCode) {
			this.lastHashCode = lastHashCode;
			this.loadedOnce = true;
			r.table("feeds").get(id).update(r.hashMap("lastHashCode", lastHashCode)).runNoReply(conn);
		}

		public boolean ignoreHashCode() {
			return loadedOnce;
		}

		public boolean isActive() {
			return active;
		}

		public void cancel() {
			this.active = false;
		}

		@Override
		protected void finalize() throws Throwable {
			cancel();
			AsyncUtils.async("Finalizer-AsyncJob", () -> {
				ALL_TYPES.remove("feed_" + pushName);
				r.table("feeds").get(id).delete().runNoReply(conn);
				Pushes.unsubscribeAll(Collections.singleton(pushName));
			}).run();
			super.finalize();
		}
	}
}
