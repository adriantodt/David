/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [05/11/16 19:18]
 */

package cf.adriantodt.David.loader.entities;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ModuleContainer {
	Class<?> getModuleClass();

	Object getInstance();

	default Set<Field> getFieldsForAnnotation(Class<? extends Annotation> annotation) {
		return Stream.of(getModuleClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(annotation)).collect(Collectors.toSet());
	}

	default Set<Method> getMethodsForAnnotation(Class<? extends Annotation> annotation) {
		return Stream.of(getModuleClass().getDeclaredMethods()).filter(f -> f.isAnnotationPresent(annotation)).collect(Collectors.toSet());
	}

	default boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
		return getModuleClass().isAnnotationPresent(annotation);
	}

	default <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		return getModuleClass().getAnnotation(annotationClass);
	}
}
