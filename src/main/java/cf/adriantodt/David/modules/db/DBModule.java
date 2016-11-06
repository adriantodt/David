/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 20:50]
 */

package cf.adriantodt.David.modules.db;

import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.Instance;
import cf.adriantodt.David.loader.Module.OnEnabled;
import cf.adriantodt.utils.data.ConfigUtils;
import cf.adriantodt.utils.data.ReturnHandler;
import cf.adriantodt.utils.data.ReturnHandler.HandlerInstance;
import cf.adriantodt.utils.ReflectionEasyAsFuck;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.ast.ReqlAst;
import com.rethinkdb.gen.ast.Uuid;
import com.rethinkdb.model.OptArgs;
import com.rethinkdb.net.Connection;

import java.util.function.Function;
import java.util.function.Supplier;

import static cf.adriantodt.David.loader.Module.Type.INSTANCE;
import static cf.adriantodt.David.loader.Module.Type.STATIC;

@Module({STATIC, INSTANCE})
public class DBModule {
	public static final Gson
		GSON_TO_FILES = new GsonBuilder().setPrettyPrinting().serializeNulls().create(),
		JSON_INTERNAL = new GsonBuilder().serializeNulls().create();

	@Instance
	private static DBModule instance = null;

	private final RethinkDB r = RethinkDB.r;
	private final ReturnHandler h = ReturnHandler.h;
	private final Connection conn;
	private final JsonObject mainConfig;

	private DBModule() {
		mainConfig = ConfigUtils.get(
			"main",
			ImmutableMap.<String, java.util.function.Predicate<JsonElement>>builder()
				.put("ownerID", ConfigUtils::isJsonString)
				.put("token", ConfigUtils::isJsonString)
				.build(),
			() -> {
				JsonObject object = new JsonObject();
				object.add("ownerID", null);
				object.add("token", null);
				return object;
			},
			false,
			true
		);

		JsonObject dbConfig = ConfigUtils.get(
			"db",
			ImmutableMap.<String, java.util.function.Predicate<JsonElement>>builder()
				.put("hostname", ConfigUtils::isJsonString)
				.put("port", element -> ConfigUtils.isJsonNumber(element) && element.getAsInt() != 0)
				.build(),
			() -> {
				JsonObject object = new JsonObject();
				object.addProperty("hostname", "localhost");
				object.addProperty("port", 28015);
				return object;
			},
			true,
			true
		);

		conn = r.connection().hostname(dbConfig.get("hostname").getAsString()).port(dbConfig.get("port").getAsInt()).db("bot").connect();
	}

	public static DBModule getInstance() {
		return instance;
	}

	public static JsonObject getConfig() {
		return getInstance().mainConfig;
	}

	public static Handler onDB(Function<RethinkDB, ReqlAst> dbConsumer) {
		return new Handler(dbConsumer.apply(instance.r));
	}

	public static Handler onDB(Supplier<ReqlAst> dbConsumer) {
		return new Handler(dbConsumer.get());
	}

	public static Handler onDB(ReqlAst db) {
		return new Handler(db);
	}

	public static class Handler {
		private final ReqlAst run;

		private Handler(ReqlAst run) {
			this.run = run;
		}

		public HandlerInstance run() {
			return instance.h.from(run.run(instance.conn));
		}

		public HandlerInstance run(OptArgs runOpts) {
			return instance.h.from(run.run(instance.conn, runOpts));
		}

		public void noReply() {
			run.runNoReply(instance.conn);
		}

		public void noReply(OptArgs globalOpts) {
			run.runNoReply(instance.conn, globalOpts);
		}
	}
}