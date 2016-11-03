/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/11/16 15:22]
 */

package cf.adriantodt.utils;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReflectionEasyAsFuck {
	public static <T,R> Function<T,R> getVirtualField(Class<T> theClass, String methodName, Class<R> returnType) {
		try {
			Field field = theClass.getDeclaredField(methodName);
			return t -> Functions.get(field, t, returnType);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static <R> Supplier<R> getStaticField(Class theClass, String methodName, Class<R> returnType) {
		try {
			Field field = theClass.getDeclaredField(methodName);
			return () -> Functions.get(field, null, returnType);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static class Functions {
		public static <R> R get(Field field, Object object, Class<R> returnType) {
			try {
				field.setAccessible(true);
				R r = (R) field.get(object);
				field.setAccessible(false);
				return r;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
