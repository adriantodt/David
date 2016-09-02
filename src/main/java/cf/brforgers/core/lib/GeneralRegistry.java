package cf.brforgers.core.lib;

import java.util.HashMap;
import java.util.Map;

public class GeneralRegistry {
	private static final GeneralRegistry instance = new GeneralRegistry();
	private Map<Class, Map> internal = new HashMap<>();

	private GeneralRegistry() {
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> getGlobal(Class<T> register) {
		Map<String, T> r = instance.get(register);
		if (r == null) {
			r = new HashMap<>();
			instance.internal.put(register, r);
		}

		return r;
	}

	public static GeneralRegistry getInstance() {
		return instance;
	}

	public static GeneralRegistry getPersonal() {
		return new GeneralRegistry();
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String, T> get(Class<T> register) {
		Map<String, T> r = internal.get(register);
		if (r == null) {
			r = new HashMap<>();
			internal.put(register, r);
		}

		return r;
	}
}
