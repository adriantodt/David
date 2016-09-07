/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [04/09/16 18:57]
 */

package cf.adriantodt.bot.impl;

import cf.adriantodt.bot.Answers;
import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Statistics;
import cf.adriantodt.bot.impl.i18n.I18n;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.audio.player.URLPlayer;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cf.adriantodt.bot.Answers.*;
import static cf.adriantodt.bot.Utils.name;

public class Audio {
	private static Map<Guild, List<Queue>> guildQueues = new HashMap<>();

	public static void setup() {
		if (!Bot.API.isAudioEnabled()) return;
		Thread audioDaemon = new Thread("AudioDaemon") {
			@Override
			public void run() {
				while (Bot.API.isAudioEnabled() && !this.isInterrupted()) {
					guildQueues.forEach(Audio::doQueueing);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException ignored) {
					}
				}
			}
		};
		audioDaemon.setPriority(Thread.NORM_PRIORITY - 1);
		audioDaemon.setDaemon(true);
		audioDaemon.start();
	}

	public static void queue(URL url, MessageReceivedEvent event) {
		if (url == null) {
			invalidargs(event);
			return;
		}

		if (event.getGuild() == null) {
			dear(event, I18n.getLocalized("audio.notInGuild", event));
			return;
		}
		VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(vch -> vch.getUsers().contains(event.getAuthor())
		).findFirst().orElse(null);

		if (channel == null) {
			dear(event, I18n.getLocalized("audio.notInChannel", event));
			return;
		}

		if (!guildQueues.containsKey(channel.getGuild())) guildQueues.put(channel.getGuild(), new ArrayList<>());
		Queue queue = new Queue();
		queue.channel = channel;
		queue.triggerEvent = event;
		queue.url = url;
		guildQueues.get(channel.getGuild()).add(queue);
		bool(event, true);
	}

	public static void skip(MessageReceivedEvent event) {
		if (event.getGuild() == null) {
			dear(event, I18n.getLocalized("audio.notInGuild", event));
			return;
		}

		VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(vch -> vch.getUsers().contains(event.getAuthor())
		).findFirst().orElse(null);

		if (channel == null) {
			dear(event, I18n.getLocalized("audio.notInChannel", event));
			return;
		}

		if (event.getGuild().getAudioManager().getConnectedChannel() != channel) {
			dear(event, I18n.getLocalized("audio.notInSameChannel", event));
			return;
		}

		Player player = guildQueues.get(event.getGuild()).get(0).player;
		if (player != null && !player.isStopped()) {
			player.stop();
		}

		doQueueing(event.getGuild(), guildQueues.get(event.getGuild()));
	}

	private static void doQueueing(Guild guild, List<Queue> queue) {
		if (queue.size() < 1 || (queue.size() == 1 && queue.get(0).player != null && queue.get(0).player.isStopped())) {
			if (guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect())
				guild.getAudioManager().closeAudioConnection();
			if (queue.size() == 1) {
				send(queue.get(0).triggerEvent, ":stop_button:");
				queue.remove(0);

			}
			return;
		}
		Queue q = queue.get(0);
		if (q.player == null || q.player.isStopped()) {
			boolean setup = false;
			while (!setup && queue.size() > 0) {
				if (q == null || q.player != null) {
					queue.remove(0);
					q = queue.size() > 0 ? queue.get(0) : null;
				}
				if (q != null) try {
					q.player = new URLPlayer(Bot.API);
					((URLPlayer) q.player).setAudioUrl(q.url);
					if (guild.getAudioManager().isAttemptingToConnect() || guild.getAudioManager().isConnected())
						guild.getAudioManager().moveAudioConnection(q.channel);
					else
						guild.getAudioManager().openAudioConnection(q.channel);
					guild.getAudioManager().setSendingHandler(q.player);
					q.player.play();
					Statistics.musics++;
					send(q.triggerEvent, q.toString());
					setup = true;
				} catch (Exception e) {
					Answers.exception(q.triggerEvent, e);
					queue.remove(q);
				}
			}
		}
	}

	public static String[] getQueue(Guild guild) {
		if (!guildQueues.containsKey(guild)) guildQueues.put(guild, new ArrayList<>());
		return guildQueues.get(guild).stream().map(Queue::toString).toArray(String[]::new);
	}

	private static class Queue {
		public VoiceChannel channel;
		public URL url;
		public MessageReceivedEvent triggerEvent;
		private Player player;

		@Override
		public String toString() {
			return (player != null ? player.isPlaying() ? ":play_pause: - " : ":stop_button: - " : "") + url.toString() + " (" + String.format(I18n.getLocalized("audio.queue", triggerEvent), "*" + name(triggerEvent.getAuthor(), triggerEvent.getGuild()) + "*", "*" + channel.getName() + "*") + ")";
		}
	}
}
