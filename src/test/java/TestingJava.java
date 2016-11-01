/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [31/10/16 18:00]
 */

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TestingJava {
	public static <T> List<T> subListOn(List<T> list, Predicate<T> predicate) {
		Optional<T> first = list.stream().filter(predicate).findFirst();
		if (!first.isPresent()) return list;
		return list.subList(0, list.indexOf(first.get()));
	}
}
