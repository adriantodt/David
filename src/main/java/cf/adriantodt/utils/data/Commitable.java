/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [01/11/16 13:19]
 */

package cf.adriantodt.utils.data;

import java.util.function.Consumer;

public interface Commitable<T> {
	static <T> Commitable<T> bake(T object, Consumer<T> onPushChanges) {
		return new Commitable<T>() {
			public boolean c = false;

			@Override
			public T get() {
				if (c) throw new IllegalStateException("Already Pushed Changes.");
				return object;
			}

			@Override
			public void pushChanges() {
				if (c) throw new IllegalStateException("Already Pushed Changes.");
				c = true;
				onPushChanges.accept(object);
			}
		};
	}

	T get();

	void pushChanges();
}
