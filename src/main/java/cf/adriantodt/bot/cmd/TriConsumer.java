/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/09/16 08:18]
 */

package cf.adriantodt.bot.cmd;

import java.util.Objects;

/**
 * Represents an operation that accepts three input arguments and returns no
 * result.  This is the three-arity specialization of {@link java.util.function.Consumer}.
 * Unlike most other functional interfaces, {@code {@link java.util.function.BiConsumer}} is expected
 * to operate via side-effects.
 * <p>
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @see java.util.function.Consumer
 * @since 1.8
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {

	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t the first input argument
	 * @param u the second input argument
	 * @param v the third input argument
	 */
	void accept(T t, U u, V v);

	/**
	 * Returns a composed {@code TriConsumer} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the
	 * composed operation.  If performing this operation throws an exception,
	 * the {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code TriConsumer} that performs in sequence this
	 * operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 */
	default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
		Objects.requireNonNull(after);

		return (l, r, s) -> {
			accept(l, r, s);
			after.accept(l, r, s);
		};
	}
}