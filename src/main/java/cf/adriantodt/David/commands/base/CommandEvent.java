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

package cf.adriantodt.David.commands.base;

import cf.adriantodt.David.modules.init.Statistics;
import cf.adriantodt.David.modules.db.GuildModule;
import cf.adriantodt.utils.TaskManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Future;

import static cf.adriantodt.utils.AsyncUtils.sleep;
import static cf.adriantodt.utils.StringUtils.splitArgs;


public class CommandEvent {
	private final GuildMessageReceivedEvent event;
	private final GuildModule.Data targetGuild;
	private final ICommand command;
	private final String args;
	private final FastAnswers answers;
	private Future<Void> awaitableTyping = null;

	public CommandEvent(GuildMessageReceivedEvent event, GuildModule.Data targetGuild, ICommand command, String args) {
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

	public String getArg(int expectedArgs, int arg) {
		return getArgs(expectedArgs)[arg];
	}

	public GuildMessageReceivedEvent getEvent() {
		return event;
	}

	public GuildModule.Data getGuild() {
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

	public RestAction<Message> sendFile(File file, Message message) throws IOException {
		return getChannel().sendFile(file, message);
	}

	public RestAction<Void> sendTyping() {
		return getChannel().sendTyping();
	}

	public void sendAwaitableTyping() {
		awaitableTyping = TaskManager.getThreadPool().submit(() -> sendTyping().block());
	}

	public CommandEvent createChild(ICommand command, String args) {
		return new CommandEvent(getEvent(), getGuild(), command, args);
	}

	public CommandEvent awaitTyping() {
		if (awaitableTyping == null) return this;
		while (awaitableTyping != null && !awaitableTyping.isDone()) {
			sleep(200);
		}
		awaitableTyping = null;
		return this;
	}

	public boolean tryOpenPrivateChannel() {
		if (!event.getAuthor().hasPrivateChannel()) {
			try {
				event.getAuthor().openPrivateChannel().block();
				return true;
			} catch (Exception e) {
				LogManager.getLogger("CommandEvent - PMs").info("Failure when trying to open private channel for user " + event.getAuthor().toString() + ". User was asked to send a pm.\n" +
					e.getClass().getSimpleName() + ": " + e.getMessage());
				awaitTyping().sendMessage(event.getMember().getAsMention() + " I can't send any DM messages to you, please send a DM to me with the message \"!ping\" to resolve the issue.\n" +
					"You should receive a \"pong!\" as response\n" +
					"Your command was ignored because it is required that the user can receive a DM to execute commands.").queue();
				return false;
			}
		} else {
			return true;
		}
	}

	public Optional<FastAnswers> getAnswersForPrivate() {
		if (tryOpenPrivateChannel()) return Optional.ofNullable(answers.forChannel(getAuthor().getPrivateChannel()));
		return Optional.empty();
	}
}
