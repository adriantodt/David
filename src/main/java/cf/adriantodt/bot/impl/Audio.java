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
import net.dv8tion.jda.audio.player.URLPlayer;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cf.adriantodt.bot.Answers.invalidargs;
import static cf.adriantodt.bot.Answers.prezado;

public class Audio {
	private static Map<Guild, List<Queue>> guildQueues = new HashMap<>();
	private static List<Guild> toBeRemovedFromMap = new ArrayList<>();

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
		}

		if (event.getGuild() == null) {
			prezado(event, "você tem que estar em uma Guild para pedir uma música.");
			return;
		}
		VoiceChannel channel = event.getGuild().getVoiceChannels().stream().filter(vch -> vch.getUsers().contains(event.getAuthor())
		).findFirst().orElse(null);

		if (channel == null) {
			prezado(event, "você tem que estar em conectado em um canal para pedir uma música.");
			return;
		}

		if (!guildQueues.containsKey(channel.getGuild())) guildQueues.put(channel.getGuild(), new ArrayList<>());
		Queue queue = new Queue();
		queue.channel = channel;
		queue.sourceOfAllEvil = event;
		queue.url = url;
		guildQueues.get(channel.getGuild()).add(queue);
	}

	private static void doQueueing(Guild guild, List<Queue> queue) {
		if (queue.size() < 0) {
			if (!guild.getAudioManager().isConnected() && !guild.getAudioManager().isAttemptingToConnect())
				guild.getAudioManager().closeAudioConnection();
			return;
		}
		Queue q = queue.get(0);
		if (q.player == null || q.player.isStopped()) {
			boolean setup = false;
			while (!setup && queue.size() > 0) {
				if (q.player != null) {
					queue.remove(0);
					q = queue.get(0);
				}
				try {
					q.player = new URLPlayer(Bot.API);
					q.player.setAudioUrl(q.url);
					if (guild.getAudioManager().isAttemptingToConnect() || guild.getAudioManager().isConnected())
						guild.getAudioManager().moveAudioConnection(q.channel);
					else
						guild.getAudioManager().openAudioConnection(q.channel);
					guild.getAudioManager().setSendingHandler(q.player);
					q.player.play();
					setup = true;
				} catch (Exception e) {
					Answers.exception(q.sourceOfAllEvil, e);
				}
			}
		}
	}

	private static class Queue {
		public VoiceChannel channel;
		public URL url;
		public MessageReceivedEvent sourceOfAllEvil;
		private URLPlayer player;
	}
}
