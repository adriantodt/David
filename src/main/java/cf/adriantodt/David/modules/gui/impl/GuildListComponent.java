/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [07/11/16 20:36]
 */

package cf.adriantodt.David.modules.gui.impl;


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