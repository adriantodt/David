/*
 * This class wasn't created by <AdrianTodt>.
 * It's a modification of Minecraft's Server
 * Management GUI. It have been modificated
 * to fit Java 8 and the Bot instead.
 */

package cf.adriantodt.bot.base.gui;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.Utils;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;

import javax.swing.*;
import java.util.Vector;

public class GuildListComponent extends JList implements Runnable {

	public GuildListComponent() {
		Utils.startAsyncTask(this, 10);
	}

	@SuppressWarnings("unchecked")
	public void run() {
		if (Bot.API == null || Bot.API.getStatus() == JDA.Status.INITIALIZING) return;
		Vector<String> vector = new Vector<>();
		Bot.API.getGuilds().stream().map(Guild::getName).forEach(vector::add);
		this.setListData(vector);
	}
}