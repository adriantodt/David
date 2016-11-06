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

package cf.adriantodt.David.commands.base;

public class Holder<T> {
	public T var;

	public Holder() {
	}

	public Holder(T object) {
		var = object;
	}

	@Override
	public int hashCode() {
		return var.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Holder) {
			return super.equals(obj);
		}

		return obj.equals(var);
	}

	@Override
	public String toString() {
		return var.toString();
	}
}
