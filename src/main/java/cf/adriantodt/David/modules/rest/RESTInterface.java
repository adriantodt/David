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

package cf.adriantodt.David.modules.rest;

import cf.adriantodt.David.loader.Module;
import cf.adriantodt.David.loader.Module.JDAInstance;
import cf.adriantodt.David.loader.Module.PostReady;
import cf.adriantodt.utils.AsyncUtils;
import cf.adriantodt.utils.TaskManager;
import cf.adriantodt.utils.ThreadBuilder;
import net.dv8tion.jda.core.JDA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import static cf.adriantodt.David.loader.Module.Type.STATIC;

@Module(name = "rest", type = STATIC)
@Controller
@SpringBootApplication
public class RESTInterface {
	@JDAInstance
	public static JDA jda = null;

	@PostReady
	public static void startWebServer() {
		AsyncUtils.async("RESTInterface", () -> SpringApplication.run(RESTInterface.class)).run();
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return (container -> {
			container.setPort(8012);
			container.setDisplayName(jda.getSelfUser().getName() + " REST API");
		});
	}
}
