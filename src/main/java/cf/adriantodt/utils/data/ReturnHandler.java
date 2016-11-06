/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 22:08]
 */

package cf.adriantodt.utils.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rethinkdb.net.Cursor;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class ReturnHandler {
	public static final ReturnHandler h = new ReturnHandler();
	private static final Gson json = new Gson();

	private ReturnHandler() {
	}

	public HandlerInstance from(Object object) {
		return new HandlerInstance(object);
	}

	public static class HandlerInstance {
		private final Object object;

		public HandlerInstance(Object object) {
			this.object = object;
		}

		public JsonObject mapExpected() {
			return simpleExpected().getAsJsonObject();
		}

		public JsonElement simpleExpected() {
			if (object instanceof Cursor)
				throw new IllegalStateException("Json-able expected, got " + Cursor.class + ".");
			return json.toJsonTree(object);
		}

		public CursorHandler cursorExpected() {
			if (!(object instanceof Cursor))
				throw new IllegalStateException(Cursor.class + " expected, got " + object.getClass() + ".");
			return new CursorHandler((Cursor) object);
		}

		public JsonArray arrayExpected() {
			return cursorExpected().toList();
		}

		public static class CursorHandler implements Iterator<JsonElement>, Iterable<JsonElement>, Closeable {
			private final Cursor cursor;

			private CursorHandler(Cursor cursor) {
				this.cursor = cursor;
			}

			@Override
			public Iterator<JsonElement> iterator() {
				return this;
			}

			@Override
			public boolean hasNext() {
				return cursor.hasNext();
			}

			@Override
			public JsonElement next() {
				return json.toJsonTree(cursor.next());
			}

			@Override
			public void close() throws IOException {
				cursor.close();
			}

			public JsonArray toList() {
				return json.toJsonTree(cursor.toList()).getAsJsonArray();
			}
		}
	}
}
