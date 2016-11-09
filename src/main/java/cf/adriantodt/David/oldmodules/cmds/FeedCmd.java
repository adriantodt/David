/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/11/16 20:36]
 */

package cf.adriantodt.David.oldmodules.cmds;

import cf.adriantodt.David.commands.base.Commands;
import cf.adriantodt.David.commands.base.Holder;
import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.Command;
import cf.adriantodt.David.loader.Module.PostReady;
import cf.adriantodt.David.loader.Module.Type;
import cf.adriantodt.David.oldmodules.db.DBModule;
import cf.adriantodt.utils.AsyncUtils;
import cf.adriantodt.utils.CollectionUtils;
import cf.adriantodt.utils.EncodingUtil;
import cf.adriantodt.utils.TaskManager;
import cf.adriantodt.utils.data.ConfigUtils;
import cf.brforgers.core.lib.IOHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

import static cf.adriantodt.David.modules.cmds.PermissionsModule.BOT_OWNER;
import static cf.adriantodt.utils.Log4jUtils.logger;
import static cf.brforgers.core.lib.IOHelper.newURL;

@Module(Type.STATIC)
public class FeedCmd {
	private static final Set<Subscription> ALL = Collections.synchronizedSet(new HashSet<>());
	private static final Set<String> ALL_TYPES = Collections.synchronizedSet(new HashSet<>());
	private static Logger logger;
	private static int i = 0;

	@PostReady
	private static void lazyLoad() {
		AsyncUtils.asyncSleepThen(100, () -> {
			logger().trace("Loading...");
			PushCmd.registerDynamicTypes(() -> ALL_TYPES, "feeds");


			DBModule.onDB(r -> r.table("feeds")).run().cursorExpected().forEach(json -> {
				JsonObject feed = json.getAsJsonObject();
				Subscription s = new Subscription(
					feed.get("pushName").getAsString(),
					newURL(EncodingUtil.decodeURIComponent(feed.get("url").getAsString())),
					feed.get("id").getAsString()
				);

				if (feed.has("lastHashCode") && ConfigUtils.isJsonNumber(feed.get("lastHashCode")))
					s.setLastHashCode(feed.get("lastHashCode").getAsInt());
			});

			i = 1;
			logger().trace("k");

			TaskManager.startAsyncTask("Feed Loop", FeedCmd::loop, 5);
		}).run();
	}

	@Command("feed")
	private static ICommand createCommand() {
		return Commands.buildSimple("feed.usage", BOT_OWNER)
			.setAction(event -> {
				PushCmd.subscribe(event.getChannel(), Sets.newHashSet("feed_" + new FeedCmd.Subscription(event.getArg(2, 0), IOHelper.newURL(event.getArg(2, 1))).pushName));
				event.awaitTyping().getAnswers().bool(true).queue();
			})
			.build();
	}

	public static void loop() {
		logger().trace("Loop(" + i + ");");
		logger().trace("ALL.size() = " + ALL.size());
		cleanup();
		ALL.forEach(FeedCmd::onSendFeed);
		if (i == 0) {
			ALL.forEach(FeedCmd::onFeed);
			i = 12;
		} else i--;
		logger().trace("Loop.End;");
	}

	public static void cleanup() {
		ALL.removeIf(s -> !s.isActive());
		ALL.removeIf(s -> PushCmd.resolveTextChannels("feed_" + s.pushName).size() == 0);
	}

	public static void onFeed(Subscription subs) {
		try {
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(subs.url));
			Holder<Integer> i = new Holder<>(0);
			Holder<Optional<Integer>> h = new Holder<>(Optional.empty());
			Lists.reverse(CollectionUtils.subListOn(
				feed.getEntries(),
				entryPredicate -> subs.equalsLastHashCode(entryPredicate.getLink().hashCode())
			)).forEach(entry -> {
				i.var++;
				subs.compiledPushes.add(FeedingUtil.handleEntry(subs, entry));
				h.var = Optional.of(entry.getLink().hashCode());
			});
			if (i.var != 0) logger().trace(subs.pushName + ".size() += " + i.var);
			h.var.ifPresent(subs::setLastHashCode);
		} catch (Exception e) {
			logger().error("Error while creating messages", e);
		}
	}

	public static void onSendFeed(Subscription subs) {
		if (subs.compiledPushes.size() == 0) return;
		logger().trace(subs.pushName + ".size() = " + subs.compiledPushes.size());
		AsyncUtils.async(() -> PushCmd.pushSimple("feed_" + subs.pushName, subs.compiledPushes.remove(0))).run();
	}

	public static class Subscription {
		public final URL url;
		public final String pushName, id;
		List<Function<TextChannel, String>> compiledPushes = Collections.synchronizedList(new ArrayList<>());
		private int lastHashCode = 0;
		private boolean active = true, loadedOnce = false;

		public Subscription(String pushName, URL url) {
			this(pushName, url, DBModule.onDB(r -> r.table("feeds").insert(
				r.hashMap("pushName", pushName)
					.with("url", EncodingUtil.encodeURIComponent(url.toString()))
					.with("lastHashCode", null)
			)).run().mapExpected().get("generated_keys").getAsJsonArray().get(0).getAsString());
		}

		private Subscription(String pushName, URL url, String id) {
			this.url = url;
			this.pushName = pushName;
			this.id = id;
			ALL.add(this);
			ALL_TYPES.add("feed_" + pushName);
		}

		public boolean equalsLastHashCode(int newestHashCode) {
			boolean v;
			//ignoreHashCode = true -> FALSE
			//ignoreHashCode = false -> getLastHashCode() == newestHashCode
			// - IntelliJ
			v = !ignoreHashCode() && (getLastHashCode() == newestHashCode);
			logger().trace(v);
			return v;
		}

		public int getLastHashCode() {
			return lastHashCode;
		}

		public void setLastHashCode(int lastHashCode) {
			this.lastHashCode = lastHashCode;
			this.loadedOnce = true;
			DBModule.onDB(r -> r.table("feeds").get(id).update(r.hashMap("lastHashCode", lastHashCode))).noReply();
		}

		public boolean ignoreHashCode() {
			return !loadedOnce;
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
				DBModule.onDB(r -> r.table("feeds").get(id).delete()).noReply();
				PushCmd.unsubscribeAll(Collections.singleton(pushName));
			}).run();
			super.finalize();
		}
	}


}
