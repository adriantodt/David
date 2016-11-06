/*
 * This class wasn't created by <AdrianTodt>.
 * It's a modification of Minecraft's Server
 * Management GUI. It have been modificated
 * to fit Java 8 and the Bot instead.
 */

package cf.adriantodt.David.modules.gui.impl;

import cf.adriantodt.oldbot.Bot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import javax.swing.*;
import java.util.Vector;

public class GuildListComponent extends JList implements Runnable {
	public GuildListComponent() {
		run();
		Bot.onLoaded.add(this);
		Bot.onLoaded.add(() -> Bot.API.addEventListener(this));
	}

	@SubscribeEvent
	public void onGuildJoin(GuildJoinEvent event) {
		run();
	}

	@SubscribeEvent
	public void onGuildLeave(GuildLeaveEvent event) {
		run();
	}

	@SuppressWarnings("unchecked")
	public void run() {
		Vector<String> vector = new Vector<>();
		if (Bot.API == null || Bot.API.getStatus() == JDA.Status.INITIALIZING) {
			vector.add("<Bot being Loaded>");
		} else {
			Bot.API.getGuilds().stream().map(Guild::getName).forEach(vector::add);
		}
		this.setListData(vector);
	}
}