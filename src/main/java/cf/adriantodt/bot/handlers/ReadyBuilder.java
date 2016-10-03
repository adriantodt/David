/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [02/10/16 19:01]
 */

package cf.adriantodt.bot.handlers;

import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.hooks.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ReadyBuilder {
	public List<Consumer<ReadyEvent>> l = new ArrayList<>();

	public ReadyBuilder add(Consumer<ReadyEvent> c) {
		if (c != null) l.add(c);
		return this;
	}

	public EventListener build() {
		return event -> {
			if (event instanceof ReadyEvent) {
				l.forEach(readyEventConsumer -> readyEventConsumer.accept((ReadyEvent) event));
				event.getJDA().removeEventListener(this);
			}
		};
	}
}
