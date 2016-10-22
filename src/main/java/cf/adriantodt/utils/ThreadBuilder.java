/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [21/10/16 16:42]
 */

package cf.adriantodt.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadBuilder {
	private Consumer<Thread> builder = thread -> {
	};

	public ThreadBuilder setPriority(int newPriority) {
		builder.andThen(thread -> thread.setPriority(newPriority));
		return this;
	}

	public ThreadBuilder setName(String name) {
		builder.andThen(thread -> thread.setName(name));
		return this;
	}

	public ThreadBuilder setDaemon(boolean on) {
		builder.andThen(thread -> thread.setDaemon(on));
		return this;
	}

	public ThreadBuilder setContextClassLoader(ClassLoader cl) {
		builder.andThen(thread -> thread.setContextClassLoader(cl));
		return this;
	}

	public ThreadBuilder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh) {
		builder.andThen(thread -> thread.setUncaughtExceptionHandler(eh));
		return this;
	}

	public Thread build() {
		return build(Thread::new);
	}

	public Thread build(Supplier<Thread> threadSupplier) {
		Thread thread = threadSupplier.get();
		builder.accept(thread);
		return thread;
	}
}
