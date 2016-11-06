/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [21/10/16 15:44]
 */

package cf.adriantodt.David.modules.rest;

import cf.adriantodt.oldbot.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class BotWebInterface {
	public static void startWebServer() {
		SpringApplication.run(BotWebInterface.class);
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return (container -> {
			container.setPort(8012);
			container.setDisplayName(Bot.SELF.getName() + " REST API");
		});
	}
}
