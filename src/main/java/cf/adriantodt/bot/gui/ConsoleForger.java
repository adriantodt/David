/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/09/16 17:49]
 */

package cf.adriantodt.bot.gui;

import cf.adriantodt.bot.Bot;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.*;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.managers.ChannelManager;
import net.dv8tion.jda.managers.PermissionOverrideManager;
import net.dv8tion.jda.utils.InviteUtil;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ConsoleForger {
	public static User forgeUser(String name, String game, PrivateChannel pm) {
		return new User() {
			@Override
			public String getId() {
				return "CONSOLE";
			}

			@Override
			public String getUsername() {
				return name;
			}

			@Override
			public String getDiscriminator() {
				return "";
			}

			@Override
			public String getAsMention() {
				return "";
			}

			@Override
			public String getAvatarId() {
				return "";
			}

			@Override
			public String getAvatarUrl() {
				return "";
			}

			@Override
			public String getDefaultAvatarId() {
				return "";
			}

			@Override
			public String getDefaultAvatarUrl() {
				return "";
			}

			@Override
			public Game getCurrentGame() {
				return new Game() {
					@Override
					public String getName() {
						return game;
					}

					@Override
					public String getUrl() {
						return null;
					}

					@Override
					public GameType getType() {
						return GameType.DEFAULT;
					}
				};
			}

			@Override
			public OnlineStatus getOnlineStatus() {
				return OnlineStatus.ONLINE;
			}

			@Override
			public PrivateChannel getPrivateChannel() {
				return pm;
			}

			@Override
			public boolean isBot() {
				return false;
			}

			@Override
			public JDA getJDA() {
				return Bot.API;
			}
		};
	}

	public static Message forgeMessage(String message, User user, MessageChannel channel, Logger msgUpdtLog) {
		return new ForgedMessage(message, user, channel, msgUpdtLog);
	}

	public static MessageChannel forgeChannel(Logger log) {
		return new ForgedChannel(log);
	}

	public static MessageReceivedEvent forgeEvent(Message msg) {
		return new MessageReceivedEvent(Bot.API, -1, msg) {

			@Override
			public Message getMessage() {
				return super.getMessage();
			}

			@Override
			public boolean isPrivate() {
				return super.isPrivate();
			}

			@Override
			public TextChannel getTextChannel() {
				return getChannel() instanceof TextChannel ? (TextChannel) getChannel() : null;
			}

			@Override
			public PrivateChannel getPrivateChannel() {
				return getChannel() instanceof PrivateChannel ? (PrivateChannel) getChannel() : null;
			}

			@Override
			public MessageChannel getChannel() {
				return msg.getChannel();
			}

			@Override
			public Guild getGuild() {
				return super.getGuild();
			}
		};
	}

	public static class ForgedMessage implements Message {
		private final String message;
		private final User user;
		private final MessageChannel channel;
		private final Logger msgUpdtLog;
		private String msg;
		private boolean edited;

		public ForgedMessage(String message, User user, MessageChannel channel, Logger msgUpdtLog) {
			this.message = message;
			this.user = user;
			this.channel = channel;
			this.msgUpdtLog = msgUpdtLog;
			msg = message;
			edited = false;
		}

		@Override
		public String getId() {
			return "";
		}

		@Override
		public List<User> getMentionedUsers() {
			return new ArrayList<>();
		}

		@Override
		public boolean isMentioned(User user) {
			return false;
		}

		@Override
		public List<TextChannel> getMentionedChannels() {
			return new ArrayList<>();
		}

		@Override
		public List<Role> getMentionedRoles() {
			return new ArrayList<>();
		}

		@Override
		public boolean mentionsEveryone() {
			return false;
		}

		@Override
		public OffsetDateTime getTime() {
			return OffsetDateTime.now();
		}

		@Override
		public boolean isEdited() {
			return edited;
		}

		@Override
		public OffsetDateTime getEditedTimestamp() {
			return edited ? OffsetDateTime.now() : null;
		}

		@Override
		public User getAuthor() {
			return user;
		}

		@Override
		public String getContent() {
			return msg;
		}

		@Override
		public String getRawContent() {
			return msg;
		}

		@Override
		public String getStrippedContent() {
			return msg;
		}

		@Override
		public boolean isPrivate() {
			return true;
		}

		@Override
		public String getChannelId() {
			return "";
		}

		@Override
		public MessageChannel getChannel() {
			return channel;
		}

		@Override
		public List<Attachment> getAttachments() {
			return new ArrayList<>();
		}

		@Override
		public List<MessageEmbed> getEmbeds() {
			return new ArrayList<>();
		}

		@Override
		public List<Emote> getEmotes() {
			return new ArrayList<>();
		}

		@Override
		public boolean isTTS() {
			return false;
		}

		@Override
		public Message updateMessage(String newContent) {

			msgUpdtLog.info("Message \"" + msg + "\"" + " have been edited. New Message: \"" + newContent + "\"");
			msg = newContent;
			return this;
		}

		@Override
		public void updateMessageAsync(String newContent, Consumer<Message> callback) {
			updateMessage(newContent);
			callback.accept(this);
		}

		@Override
		public void deleteMessage() {
			msgUpdtLog.info("Message \"" + msg + "\"" + " have been \"deleted\".");
		}

		@Override
		public JDA getJDA() {
			return Bot.API;
		}

		@Override
		public boolean isPinned() {
			return false;
		}

		@Override
		public boolean pin() {
			return false;
		}

		@Override
		public boolean unpin() {
			return false;
		}

		@Override
		public MessageType getType() {
			return MessageType.DEFAULT;
		}
	}

	public static class ForgedChannel implements TextChannel, PrivateChannel {
		private final Logger log;
		public User user = null;
		public String topic = "";

		public ForgedChannel(Logger log) {
			this.log = log;
		}

		@Override
		public JDA getJDA() {
			return Bot.API;
		}

		@Override
		public PermissionOverride getOverrideForUser(User user) {
			return null;
		}

		@Override
		public PermissionOverride getOverrideForRole(Role role) {
			return null;
		}

		@Override
		public List<PermissionOverride> getPermissionOverrides() {
			return null;
		}

		@Override
		public List<PermissionOverride> getUserPermissionOverrides() {
			return null;
		}

		@Override
		public List<PermissionOverride> getRolePermissionOverrides() {
			return null;
		}

		@Override
		public PermissionOverrideManager createPermissionOverride(User user) {
			return null;
		}

		@Override
		public PermissionOverrideManager createPermissionOverride(Role role) {
			return null;
		}

		@Override
		public List<InviteUtil.AdvancedInvite> getInvites() {
			return new ArrayList<>();
		}

		@Override
		public void close() {
			log.info("PM Channel Closed.");
		}

		@Override
		public String getId() {
			return "";
		}

		@Override
		public User getUser() {
			return user;
		}

		@Override
		public String getName() {
			return user.getUsername();
		}

		@Override
		public String getTopic() {
			return topic;
		}

		@Override
		public Guild getGuild() {
			return null;
		}

		@Override
		public List<User> getUsers() {
			return new ArrayList<>();
		}

		@Override
		public int getPosition() {
			return 0;
		}

		@Override
		public int getPositionRaw() {
			return 0;
		}

		@Override
		public boolean checkPermission(User user, Permission... permissions) {
			return false;
		}

		@Override
		public ChannelManager getManager() {
			return null;
		}

		@Override
		public Message sendMessage(String text) {
			log.info(text);
			return forgeMessage(text, null, this, log);
		}

		@Override
		public Message sendMessage(Message msg) {
			return sendMessage(msg.getRawContent());
		}

		@Override
		public void sendMessageAsync(String msg, Consumer<Message> callback) {
			callback.accept(sendMessage(msg));

		}

		@Override
		public void sendMessageAsync(Message msg, Consumer<Message> callback) {
			callback.accept(sendMessage(msg));
		}

		@Override
		public Message sendFile(File file, Message message) {
			return null;
		}

		@Override
		public void sendFileAsync(File file, Message message, Consumer<Message> callback) {
			//lel
		}

		@Override
		public Message getMessageById(String messageId) {
			return null;
		}

		@Override
		public boolean deleteMessageById(String messageId) {
			return false;
		}

		@Override
		public MessageHistory getHistory() {
			return null;
		}

		@Override
		public void sendTyping() {
			//lel
		}

		@Override
		public boolean pinMessageById(String messageId) {
			return false;
		}

		@Override
		public boolean unpinMessageById(String messageId) {
			return false;
		}

		@Override
		public List<Message> getPinnedMessages() {
			return new ArrayList<>();
		}

		@Override
		public String getAsMention() {
			return null;
		}

		@Override
		public void deleteMessages(Collection<Message> messages) {
			messages.forEach(Message::deleteMessage);
		}

		@Override
		public void deleteMessagesByIds(Collection<String> messageIds) {
			//lel
		}

		@Override
		public int compareTo(TextChannel o) {
			return 0;
		}
	}
}
