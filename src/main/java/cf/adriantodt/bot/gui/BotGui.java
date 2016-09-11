/*
 * This class wasn't created by <AdrianTodt>.
 * It's a modification of Minecraft's Server
 * Management GUI. It have been modificated
 * to fit Java 8 and the Bot instead.
 */

package cf.adriantodt.bot.gui;

import cf.adriantodt.bot.Bot;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class BotGui extends JComponent {
	private static final MessageChannel CONSOLE_CHANNEL = ConsoleForger.forgeChannel(LogManager.getLogger("Console Channel"));
	private static final User CONSOLE_USER = ConsoleForger.forgeUser("Console", "Management", (PrivateChannel) CONSOLE_CHANNEL);
	private static final Font GUI_FONT = new Font("Monospaced", 0, 12);
	private static final Logger LOGGER = LogManager.getLogger();

	static {
		ConsoleForger.ForgedChannel c = ((ConsoleForger.ForgedChannel) CONSOLE_CHANNEL);
		c.user = CONSOLE_USER;
		c.topic = "É fake";
	}

	public BotGui() {
		this.setPreferredSize(new Dimension(858, 480));
		this.setLayout(new BorderLayout());

		try {
			this.add(this.getLogComponent(), "Center");
			this.add(this.getStatsComponent(), "West");
		} catch (Exception exception) {
			LOGGER.error("Couldn't build bot GUI", exception);
		}
	}

	/**
	 * Creates the bot GUI and sets it visible for the user.
	 */
	public static void createBotGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		BotGui botGui = new BotGui();
		JFrame frame = new JFrame("David");
		frame.add(botGui);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * Generates new StatsComponent and returns it.
	 */
	private JComponent getStatsComponent() throws Exception {
		JPanel jpanel = new JPanel(new BorderLayout());
		jpanel.add(new StatsComponent(), "North");
		jpanel.add(this.getGuildListComponent(), "Center");
		jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
		return jpanel;
	}

	/**
	 * Generates new GuildListComponent and returns it.
	 */
	private JComponent getGuildListComponent() throws Exception {
		JList jlist = new GuildListComponent();
		JScrollPane jscrollpane = new JScrollPane(jlist, 22, 30);
		jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Guilds"));
		return jscrollpane;
	}

	private JComponent getLogComponent() throws Exception {
		JPanel jpanel = new JPanel(new BorderLayout());
		final JTextArea jtextarea = new JTextArea();
		final JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
		jtextarea.setEditable(false);
		jtextarea.setFont(GUI_FONT);
		final JTextField jtextfield = new JTextField();
		jtextfield.addActionListener(actionPerformed -> {
			String s = jtextfield.getText().trim();

			if (!s.isEmpty()) {
				Bot.INSTANCE.onMessageReceived(ConsoleForger.forgeEvent(ConsoleForger.forgeMessage(s, CONSOLE_USER, CONSOLE_CHANNEL, LogManager.getLogger("Console Channel Event"))));
			}

			jtextfield.setText("");
		});
		jtextarea.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent p_focusGained_1_) {
			}
		});
		jpanel.add(jscrollpane, "Center");
		jpanel.add(jtextfield, "South");
		jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
		Thread thread = new Thread(() -> {
			String s;

			while ((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null)
				appendLine(jtextarea, jscrollpane, s);
		});
		thread.setDaemon(true);
		thread.start();
		return jpanel;
	}

	public void appendLine(final JTextArea textArea, final JScrollPane scrollPane, final String line) {

		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> BotGui.this.appendLine(textArea, scrollPane, line));
		} else {
			Document document = textArea.getDocument();
			JScrollBar jscrollbar = scrollPane.getVerticalScrollBar();
			boolean flag = false;

			if (scrollPane.getViewport().getView() == textArea) {
				flag = (double) jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double) (GUI_FONT.getSize() * 4) > (double) jscrollbar.getMaximum();
			}

			try {
				document.insertString(document.getLength(), line, null);
			} catch (BadLocationException ignored) {
			}

			if (flag) {
				jscrollbar.setValue(Integer.MAX_VALUE);
			}
		}
	}
}