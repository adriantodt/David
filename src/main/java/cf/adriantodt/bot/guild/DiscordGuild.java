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

package cf.adriantodt.bot.guild;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.cmd.UserCommand;
import cf.adriantodt.bot.perm.Permissions;
import cf.adriantodt.bot.persistent.DataManager;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordGuild {
	public static final DiscordGuild PM, GLOBAL;
	public static List<DiscordGuild> all = new ArrayList<>();

	static {
		PM = new DiscordGuild();
		GLOBAL = new DiscordGuild();
		PM.id = "-2";
		GLOBAL.id = "-1";
		PM.name = "PM";
		GLOBAL.name = "GLOBAL";
		PM.guild = null;
		GLOBAL.guild = null;
	}

	public Guild guild = null;
	public List<MessageChannel> channelList = new ArrayList<>();
	public Map<String, Long> userPerms = new HashMap<>();
	public Map<String, UserCommand> commands = new HashMap<>();
	public String id = "-1", name = "";

	public DiscordGuild() {
		all.add(this);
		userPerms.put("default", Permissions.BASE_USER);
	}

	public static DiscordGuild fromDiscord(Guild guild) {
		if (guild == null) return PM;
		DiscordGuild g = null;
		for (DiscordGuild gs : all)
			if (guild.getId().equals(gs.id)) {
				g = gs;
				break;
			}

		if (g == null) g = new DiscordGuild();
		if (g.id.equals("-1")) g.id = guild.getId();
		if (g.guild == null) g.guild = guild;
		if (g.name.isEmpty()) g.name = getAvailable(guild.getName());
		if (g.channelList.isEmpty()) g.channelList.addAll(guild.getTextChannels());

		return g;
	}

	public static DiscordGuild fromId(String id) {
		for (DiscordGuild g : all) {
			if (g.id.equals(id)) return g;
		}
		return null;
	}

	public static DiscordGuild fromName(String name) {
		for (DiscordGuild g : all) {
			if (g.name.equals(name)) return g;
		}
		return null;
	}

	public static DiscordGuild fromDiscord(MessageReceivedEvent event) {
		return fromDiscord(event.getGuild());
	}

	private static String getAvailable(String name) {
		name = name.replace(" ", "_").replace(":", "");
		if (fromName(name) == null) return name;
		for (int i = 2; i < 1000; i++) {
			if (fromName(name + i) == null) return name + i;
		}
		throw new RuntimeException("AH PQP QUITEI DA VIDA");
	}

	public String toString() {
		return "Guild: " + name + (guild != null && !name.equals(guild.getName()) ? " (" + guild.getName() + ")" : "")
			+ "\n - Admin: " + (guild == null ? Bot.API.getUserById(DataManager.options.owner).getUsername() : guild.getOwner().getUsername())
			+ "\n - Comandos: " + commands.size()
			+ "\n - Canais: " + channelList.size()
			+ "\n - Usuários: " + (guild == null ? (this == PM ? channelList.size() : Bot.API.getUsers().size()) : guild.getUsers().size())
			+ "\n - GuildID: " + id
			;
	}
}
