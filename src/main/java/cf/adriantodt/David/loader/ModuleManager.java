/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 19:24]
 */

package cf.adriantodt.David.loader;

import cf.adriantodt.David.commands.base.ICommand;
import cf.adriantodt.David.loader.Module.LoggerInstance;
import cf.adriantodt.David.loader.Module.Resource;
import cf.adriantodt.David.loader.Module.Type;
import cf.adriantodt.David.loader.entities.ModuleContainer;
import cf.adriantodt.David.loader.entities.impl.ModuleContainerImpl;
import cf.adriantodt.David.modules.cmds.manager.CommandManager;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static cf.adriantodt.David.Loader.resource;
import static cf.adriantodt.David.loader.Module.Type.INSTANCE;
import static cf.adriantodt.David.loader.Module.Type.STATIC;
import static cf.adriantodt.utils.Log4jUtils.logger;

public class ModuleManager {
	private static final Logger LOGGER = logger();
	private static Map<Class, ModuleContainer> INSTANCE_MAP = new HashMap<>();
	private static Set<Object> JDA_LISTENERS = new HashSet<>();
	private static boolean firedPre = false;
	private static boolean firedPost = false;

	static {
		JDA_LISTENERS.add(ModuleManager.class);
	}

	public static void add(Class<?> clazz) {
		try {
			//Check if class already is loaded as module
			if (INSTANCE_MAP.containsKey(clazz)) return;

			if (!clazz.isAnnotationPresent(Module.class)) return;

			//Can be instantiable?
			Object instance = makeInstance(clazz);
			if (instance == null) return;

			ModuleContainer module = new ModuleContainerImpl(clazz, instance);

			//Fields being initialized before Module.Predicate
			for (Field f : module.getFieldsForAnnotation(LoggerInstance.class)) {
				try {
					f.set(module.getInstance(), logger(clazz));
				} catch (Exception e) {
					LOGGER.error("Error while injecting Logger into " + f + ":", e);
				}
			}

			for (Field f : module.getFieldsForAnnotation(Module.Instance.class)) {
				try {
					f.set(module.getInstance(), module.getInstance());
				} catch (Exception e) {
					LOGGER.error("Error while injecting Instance into " + f + ":", e);
				}
			}

			for (Field f : module.getFieldsForAnnotation(Resource.class)) {
				try {
					f.set(module.getInstance(), resource(f.getAnnotation(Resource.class).value()));
				} catch (Exception e) {
					LOGGER.error("Error while injecting Resource \"" + f.getAnnotation(Resource.class).value() + "\" into " + f + ":", e);
				}
			}

			//If any Module.Predicate is present and fails, it will stop the
			if (!module.getMethodsForAnnotation(Module.Predicate.class).stream().allMatch(method -> {
				try {
					return (Boolean) method.invoke(instance);
				} catch (Exception e) {
					LOGGER.error("Error while Predicating:", e);
				}
				return false;
			})) {
				fireEventsFor(module, Module.OnDisabled.class);
				return;
			} else {
				fireEventsFor(module, Module.OnEnabled.class);
			}

			//We past this far. Time to register.
			INSTANCE_MAP.put(clazz, module);

			//Yeah, we need to make this.
			if (module.isAnnotationPresent(Module.SubscribeJDA.class)) JDA_LISTENERS.add(module.getInstance());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object[] jdaListeners() {
		return JDA_LISTENERS.toArray();
	}

	public static void firePreReadyEvents() {
		if (firedPre) throw new RuntimeException(new IllegalAccessException("Already fired."));
		fireEvents(Module.PreReady.class);
		firedPre = true;
	}

	public static void firePostReadyEvents() {
		if (firedPost) throw new RuntimeException(new IllegalAccessException("Already fired."));
		fireEvents(Module.PostReady.class);
		firedPost = true;
	}

	@SubscribeEvent
	private static void ready(ReadyEvent event) {
		for (ModuleContainer module : INSTANCE_MAP.values()) {
			for (Field f : module.getFieldsForAnnotation(Module.JDAInstance.class)) {
				try {
					f.set(module.getInstance(), event.getJDA());
				} catch (Exception e) {
					LOGGER.error("Error while injecting JDA Instance into " + f + ":", e);
				}
			}
			for (Field f : module.getFieldsForAnnotation(Module.SelfUserInstance.class)) {
				try {
					f.set(module.getInstance(), event.getJDA().getSelfUser());
				} catch (Exception e) {
					LOGGER.error("Error while injecting SelfUser Instance into " + f + ":", e);
				}
			}
			for (Method m : module.getMethodsForAnnotation(Module.Command.class)) {
				try {
					CommandManager.addCommand(m.getAnnotation(Module.Command.class).value(), (ICommand) m.invoke(module.getInstance()));
				} catch (Exception e) {
					LOGGER.error("Error while registering command \"" + m.getAnnotation(Module.Command.class).value() + "\" from " + m + ":", e);
				}
			}
		}

		fireEvents(Module.Ready.class);
	}

	private static void fireEvents(Class<? extends Annotation> annotation) {
		for (ModuleContainer module : INSTANCE_MAP.values()) fireEventsFor(module, annotation);
	}

	private static void fireEventsFor(ModuleContainer module, Class<? extends Annotation> annotation) {
		for (Method m : module.getMethodsForAnnotation(annotation)) {
			try {
				m.invoke(module.getInstance());
			} catch (Exception e) {
				LOGGER.error("Error while firing event \"" + annotation + "\" from " + m + ":", e);
			}
		}
	}

	public static Object makeInstance(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		//Creates a Set from the ModuleType[]
		Set<Type> moduleTypes = new HashSet<>();
		Collections.addAll(moduleTypes, clazz.getAnnotation(Module.class).value());

		//Instanciates a new Instance, leave it null or return
		if (moduleTypes.contains(INSTANCE)) {
			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		if (moduleTypes.contains(STATIC)) {
			return clazz;
		}

		return null;
	}


}
