/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/10/16 08:15]
 */

package cf.adriantodt.bot.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.rethinkdb.net.Cursor;

import java.util.HashMap;
import java.util.function.Consumer;

public class ReturnHandler {
	public static final ReturnHandler h = new ReturnHandler();
	private static final Gson json = new Gson();

	private ReturnHandler() {
	}

	public CursorHandler cursor(Cursor<HashMap<String, Object>> cursor) {
		return new CursorHandler(cursor);
	}

	public MapHandler map(HashMap<String, Object> map) {
		return new MapHandler(map);
	}

	public CursorHandler query(Cursor<HashMap<String, Object>> cursor) {
		return cursor(cursor);
	}

	public MapHandler response(HashMap<String, Object> map) {
		return map(map);
	}

	public static class CursorHandler {
		private final Cursor<HashMap<String, Object>> cursor;

		private CursorHandler(Cursor<HashMap<String, Object>> cursor) {
			this.cursor = cursor;
		}

		public JsonArray list() {
			return json.toJsonTree(cursor.toList()).getAsJsonArray();
		}

		public void forEach(Consumer<? super JsonElement> consumer) {
			list().forEach(consumer);
		}

		public MapHandler first() {
			return new MapHandler(cursor.next());
		}

		public CursorStreamHandler stream() {
			return new CursorStreamHandler(cursor);
		}

		public static class CursorStreamHandler {
			private final Cursor<HashMap<String, Object>> cursor;
			private CursorStreamHandler(Cursor<HashMap<String, Object>> cursor) {
				this.cursor = cursor;
			}

			public Runnable onStream(Consumer<StreamHandler> streamConsumer) {
				return () -> {
					for (Object next : cursor) {
						StreamHandler handler = new StreamHandler(cursor, json.toJsonTree(next));
						streamConsumer.accept(handler);
						if (handler.breakAfter()) break;
					}
				};
			}

			public static class StreamHandler {
				private final JsonElement element;
				private final Cursor<HashMap<String, Object>> cursor;
				private boolean breakAfter = false;

				public StreamHandler(Cursor<HashMap<String, Object>> cursor, JsonElement element) {
					this.element = element;
					this.cursor = cursor;
				}

				public boolean breakAfter() {
					return breakAfter;
				}

				public void doBreak(boolean breakAfter) {
					this.breakAfter = breakAfter;
				}
			}
		}
	}

	public static class MapHandler {
		private final HashMap<String, Object> map;

		private MapHandler(HashMap<String, Object> map) {
			this.map = map;
		}

		public JsonElement object() {
			return json.toJsonTree(map);
		}
	}
}
