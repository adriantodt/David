/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [23/10/16 14:25]
 */

package cf.adriantodt.bot.base.cmd;

import cf.adriantodt.bot.data.Guilds;
import cf.adriantodt.bot.utils.Statistics;
import cf.adriantodt.bot.utils.Tasks;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.io.File;
import java.util.concurrent.Future;

import static cf.adriantodt.bot.utils.Utils.sleep;
import static cf.adriantodt.bot.utils.Utils.splitArgs;

public class CommandEvent {
	private final GuildMessageReceivedEvent event;
	private final Guilds.Data targetGuild;
	private final ICommand command;
	private final String args;
	private final FastAnswers answers;
	private Future<Void> awaitableTyping = null;
	public CommandEvent(GuildMessageReceivedEvent event, Guilds.Data targetGuild, ICommand command, String args) {
		Statistics.cmds++;
		this.event = event;
		this.targetGuild = targetGuild;
		this.command = command;
		this.args = args;
		this.answers = new FastAnswers(this);
	}

	public FastAnswers getAnswersForChannel(MessageChannel channel) {
		return getAnswers().forChannel(channel);
	}

	public FastAnswers getAnswers() {
		return answers;
	}

	public String getArgs() {
		return args;
	}

	public String[] getArgs(int expectedArgs) {
		return splitArgs(getArgs(), expectedArgs);
	}

	public String getArgument(int expectedArgs, int arg) {
		return getArgs(expectedArgs)[arg];
	}

	public GuildMessageReceivedEvent getEvent() {
		return event;
	}

	public Guilds.Data getGuild() {
		return targetGuild;
	}

	public ICommand getCommand() {
		return command;
	}

	public TextChannel getChannel() {
		return getEvent().getChannel();
	}

	public Guild getOriginGuild() {
		return getEvent().getGuild();
	}

	public Member getMember() {
		return getEvent().getMember();
	}

	public Message getMessage() {
		return getEvent().getMessage();
	}

	public User getAuthor() {
		return getEvent().getAuthor();
	}

	public JDA getJDA() {
		return getEvent().getJDA();
	}

	public RestAction<Message> sendMessage(String text) {
		return getChannel().sendMessage(text);
	}

	public RestAction<Message> sendMessage(Message msg) {
		return getChannel().sendMessage(msg);
	}

	public RestAction<Message> sendFile(File file, Message message) {
		return getChannel().sendFile(file, message);
	}

	public RestAction<Void> sendTyping() {
		return getChannel().sendTyping();
	}

	public void sendAwaitableTyping() {
		awaitableTyping = Tasks.getThreadPool().submit(() -> sendTyping().block());
	}

	public CommandEvent createChild(ICommand command, String args) {
		return new CommandEvent(getEvent(), getGuild(), command, args);
	}

	public CommandEvent awaitTyping() {
		if (awaitableTyping == null) return this;
		while (!awaitableTyping.isDone()) {
			sleep(100);
		}

		return this;
	}
}
