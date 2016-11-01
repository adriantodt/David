/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 13:07]
 */

package cf.adriantodt.utils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtils {
	public static <T, U> Map<T, U> concatMaps(Map<T, U> map1, Map<T, U> map2) {
		return Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(entry1, entry2) -> (entry1 == null ? entry2 : entry1)
				)
			);
	}

	public static Iterable<String> iterate(Matcher matcher) {
		return new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					@Override
					public boolean hasNext() {
						return matcher.find();
					}

					@Override
					public String next() {
						return matcher.group();
					}
				};
			}

			@Override
			public void forEach(Consumer<? super String> action) {
				while (matcher.find()) {
					action.accept(matcher.group());
				}
			}
		};
	}

	public static <T> List<T> subListOn(List<T> list, Predicate<T> predicate) {
		Optional<T> first = list.stream().filter(predicate).findFirst();
		if (!first.isPresent()) return list;
		return list.subList(0, list.indexOf(first.get()));
	}

	public static <T> T random(List<T> list, Random random) {
		return list.get(random.nextInt(list.size()));
	}

	public static <T> T random(T[] array, Random random) {
		return array[random.nextInt(array.length)];
	}
}

