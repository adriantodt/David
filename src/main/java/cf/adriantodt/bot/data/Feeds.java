///*
// * This class was created by <AdrianTodt>. It's distributed as
// * part of the DavidBot. Get the Source Code in github:
// * https://github.com/adriantodt/David
// *
// * DavidBot is Open Source and distributed under the
// * GNU Lesser General Public License v2.1:
// * https://github.com/adriantodt/David/blob/master/LICENSE
// *
// * File Created @ [31/10/16 09:12]
// */
//
//package cf.adriantodt.bot.data;
//
//import cf.adriantodt.bot.utils.Tasks;
//import cf.adriantodt.bot.utils.Utils;
//import com.rometools.rome.feed.synd.SyndFeed;
//import com.rometools.rome.io.SyndFeedInput;
//import com.rometools.rome.io.XmlReader;
//
//import java.net.URL;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//public class Feeds {
//	private static Set<Subscription> all = new HashSet<>();
//
//	static {
//		Tasks.startAsyncTask("Feeds to Pushes", Feeds::onFeed, 300);
//		Push.registerDynamicTypes(() -> all.stream().map(s -> "feed_" + s.pushName).collect(Collectors.toSet()), "feeds");
//	}
//
//	public static void onFeed() {
//		all.removeIf(s -> !s.isActive());
//		all.forEach(Feeds::onFeed);
//	}
//
//	public static void onFeed(Subscription subs) {
//		try {
//			SyndFeedInput input = new SyndFeedInput();
//			SyndFeed feed = input.build(new XmlReader(subs.url));
//			Utils.subListOn(feed.getEntries(), entry -> subs.equalsLastHashCode(entry.getLink().hashCode()));
//			//Push.pushSimple(subs.pushName, c -> feed.toString());
//			System.out.println(subs.pushName + ": " + feed.toString());
//		} catch (Exception e) {
//
//		}
//	}
//
//	public static class Subscription {
//		public final URL url;
//		public final String pushName;
//		private int lastHashCode = 0;
//		private boolean active = true, loadedOnce = false;
//
//		public Subscription(URL url, String pushName) {
//			all.add(this);
//			this.url = url;
//			this.pushName = pushName;
//		}
//
//		public boolean equalsLastHashCode(int newestHashCode) {
//			if (ignoreHashCode()) return false;
//			return getLastHashCode() == newestHashCode;
//		}
//
//		public int getLastHashCode() {
//			return lastHashCode;
//		}
//
//		public void setLastHashCode(int lastHashCode) {
//			this.lastHashCode = lastHashCode;
//			this.loadedOnce = true;
//		}
//
//		public boolean ignoreHashCode() {
//			return lastHashCode != 0 || loadedOnce;
//		}
//
//		public boolean isActive() {
//			return active;
//		}
//
//		public void cancel() {
//			this.active = false;
//		}
//	}
//}
