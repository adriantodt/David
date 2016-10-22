/*
 * This class was created by <AdrianTodt>. It's distributed as
 * part of the DavidBot. Get the Source Code in github:
 * https://github.com/adriantodt/David
 *
 * DavidBot is Open Source and distributed under the
 * GNU Lesser General Public License v2.1:
 * https://github.com/adriantodt/David/blob/master/LICENSE
 *
 * File Created @ [28/09/16 22:15]
 */

package cf.adriantodt.bot.hardimpl;

import cf.adriantodt.bot.Bot;
import cf.adriantodt.bot.data.I18n;

import static cf.adriantodt.bot.data.I18n.setParent;

/**
 * Hardcoded Impl goes here. Shouldn't be used too much.
 * It's here for Anti Data Loss regeneration.
 */
public class I18nHardImpl {
	public static void impl() {
		setParent("pt_BR", "en_US");
		setParent("en_GB", "en_US");
		setParent("en_SG", "en_US");
		setParent("en_AU", "en_US");

		localize("en_US", "bot.hello1", "Hello! I'm $(dynamic.botname). Someone dropped me here!");
		localize("pt_BR", "bot.hello1", "Oi! Eu sou o $(dynamic.botname). Alguém me jogou aqui!");
		localize("en_US", "bot.hello2", "Someone call %s to set my Default Language!\n(Because of the Region, I guessed the language as `%s`, but the Guild Owner can set it anytime by executing `$(dynamic.mention) guild lang <en_US|pt_BR|etc>`)");
		localize("pt_BR", "bot.hello2", "Alguém chame %s para definir minha Língua Padrão!\n(Por causa da Região, eu adivinhei a língua como `%s`, mas o Guild Owner pode defini-la a qualquer mmomento executando `$(dynamic.mention) guild lang <en_US|pt_BR|etc>`)");
		localize("en_US", "bot.stop", "Stopping...");
		localize("pt_BR", "bot.stop", "Saindo...");
		localize("en_US", "bot.restart", "Restarting...");
		localize("pt_BR", "bot.restart", "Reiniciando...");
		localize("en_US", "bot.save", "Saving...");
		localize("pt_BR", "bot.save", "Salvando...");
		localize("en_US", "bot.load", "Loading...");
		localize("pt_BR", "bot.load", "Carregando...");
		localize("en_US", "bot.stop.usage", "Stops the Bot");
		localize("pt_BR", "bot.stop.usage", "Desliga o Bot");
		localize("en_US", "bot.restart.usage", "Restarts the Bot");
		localize("pt_BR", "bot.restart.usage", "Reinicia o Bot");
		localize("en_US", "bot.save.usage", "Saves the Data to Disk");
		localize("pt_BR", "bot.save.usage", "Salva os Dados no Disco");
		localize("en_US", "bot.load.usage", "Loads the Data from Disk");
		localize("pt_BR", "bot.load.usage", "Carrega os Dados atuais do Disco");
		localize("en_US", "bot.toofast.usage", "Toggles \"TooFast\" SpamProtection.");
		localize("pt_BR", "bot.toofast.usage", "Ativa ou Desativa o \"Toofast\" SpamProtection.");
		localize("en_US", "bot.stats.usage", "Session Statistics.");
		localize("pt_BR", "bot.stats.usage", "Estatísticas da sessão.");
		localize("en_US", "bot.info.usage", "Bot Information");
		localize("pt_BR", "bot.info.usage", "Informações do Bot");
		localize("en_US", "bot.help", "Hello! I'm $(dynamic.botname).\nTo get started with the Commands, send: $(dynamic.mention) cmds\nTo invite me to your Guild, send: $(dynamic.mention) inviteme");
		localize("pt_BR", "bot.help", "Olá! Eu sou o $(dynamic.botname).\nPara começar a usar os Comandos, envie: $(dynamic.mention) cmds\nPara me convidar para a sua Guild, envie: $(dynamic.mention) inviteme");

		localize("en_US", "tree.subcmds", "Sub-Commands");
		localize("pt_BR", "tree.subcmds", "Sub-Comandos");
		localize("en_US", "tree.default", "default");
		localize("pt_BR", "tree.default", "padrão");

		localize("en_US", "guild.guild", "Guild");
		localize("pt_BR", "guild.guild", "Guild");
		localize("en_US", "guild.admin", "Admin");
		localize("pt_BR", "guild.admin", "Admin");
		localize("en_US", "guild.cmds", "Commands");
		localize("pt_BR", "guild.cmds", "Comandos");
		localize("en_US", "guild.channels", "Channels");
		localize("pt_BR", "guild.channels", "Canais");
		localize("en_US", "guild.users", "Users");
		localize("pt_BR", "guild.users", "Usuários");
		localize("en_US", "guild.info.usage", "Show Guild info.");
		localize("pt_BR", "guild.info.usage", "Mostre informações sobre a Guild.");
		localize("en_US", "guild.list.usage", "List the Guild channels.");
		localize("pt_BR", "guild.list.usage", "Mostre os canais da Guild.");
		localize("en_US", "guild.lang.set", "Now I'll speak %s in this Guild!");
		localize("pt_BR", "guild.lang.set", "Agora eu vou falar em %s nessa Guild!");
		localize("en_US", "guild.broadcast.usage", "Send a message to all channels of the Guild.");
		localize("pt_BR", "guild.broadcast.usage", "Envie uma mensagem para todos os canais da Guild.");
		localize("en_US", "guild.cleanup.usage", "Toggles Message Cleanup (Messages from the bot are deleted after 15s).");
		localize("pt_BR", "guild.cleanup.usage", "Ativa ou Desativa o \"Cleanup\" de mensagens (Mensagens do bot são deletadas após 15s).");

		localize("en_US", "user.none", "none");
		localize("pt_BR", "user.none", "nenhum");
		localize("en_US", "user.avatar", "Avatar");
		localize("pt_BR", "user.avatar", "Avatar");
		localize("en_US", "user.name", "Name");
		localize("pt_BR", "user.name", "Nome");
		localize("en_US", "user.nick", "Nick");
		localize("pt_BR", "user.nick", "Apelido");
		localize("en_US", "user.roles", "Roles");
		localize("pt_BR", "user.roles", "Cargos");
		localize("en_US", "user.memberSince", "Member Since");
		localize("pt_BR", "user.memberSince", "Membro Desde");
		localize("en_US", "user.commonGuilds", "Common Guilds");
		localize("pt_BR", "user.commonGuilds", "Guilds em Comum");
		localize("en_US", "user.status", "Status");
		localize("pt_BR", "user.status", "Status");
		localize("en_US", "user.playing", "Playing");
		localize("pt_BR", "user.playing", "Jogando");

		localize("en_US", "lang.set", "Now I'll speak in %s to you!");
		localize("pt_BR", "lang.set", "Agora eu vou falar em %s para você!");
		localize("en_US", "lang.setNone", "Now I'll speak the Guild's Default Language!");
		localize("pt_BR", "lang.setNone", "Agora eu vou falar na lingua padrão da Guild!");
		localize("en_US", "lang.usage", "Define the language the bot will talk with you");
		localize("pt_br", "lang.usage", "Define a língua que o Bot vai falar com você");

		localize("en_US", "inviteme.link", "Click the Link to invite the Bot to your Guild");
		localize("pt_BR", "inviteme.link", "Clique no Link para convidar o Bot para a sua Guild");
		localize("en_US", "inviteme.usage", "Sends an Link to Invite the Bot to your Guild");
		localize("pt_br", "inviteme.usage", "Envia um Link para Conviar o Bot para a sua Guild");

		localize("en_US", "lang.usage", "Define the language the bot will speak at the Guild");
		localize("pt_br", "lang.usage", "Define a língua que o Bot vai falar na Guild");

		localize("en_US", "perms.get.usage", "Show the current User Permissions.\n(Parameters: [user])\nIf executed without arguments, return the sender's permissions.\nIf a user is specified, the user's permissions is returned instead.");
		localize("pt_BR", "perms.get.usage", "Mostrar as Permissões que o Usuário tem.\n(Parâmetros: [user])\nSe executado sem argumentos, retorna as suas permissões.\nSe um usuário for suprido, retorna as permissões do usuário.");
		localize("en_US", "perms.get.userPerms", "User Permissions");
		localize("pt_BR", "perms.get.userPerms", "Permissões do Usuário");
		localize("en_US", "perms.get.none", "none");
		localize("pt_BR", "perms.get.none", "nenhuma");
		localize("en_US", "perms.set.usage", "Define the User Permissions.\n(Parameters: <user>)\nDefine the permissions of a user specified by the parameter.");
		localize("pt_BR", "perms.set.usage", "Define as Permissões de um Usuário.\n(Parâmetros: <user>)\nDefine as permissões do usuário suprido pelo parâmetro.");
		localize("en_US", "perms.list.usage", "List all the permissions");
		localize("pt_BR", "perms.list.usage", "Lista todas as permissões");

		localize("en_US", "answers.dear", "Dear");
		localize("pt_BR", "answers.dear", "Prezado");
		localize("en_US", "answers.calmDown", "Calm down");
		localize("pt_BR", "answers.calmDown", "Acalme-se");
		localize("en_US", "answers.tooFast", "You're running commands too fast!");
		localize("pt_BR", "answers.tooFast", "Você está executando comandos rápido de mais");
		localize("en_US", "answers.invalidArgs", "you've sent invalid arguments to the command.");
		localize("pt_BR", "answers.invalidArgs", "você enviou argumento(s) incorreto(s) para o comando.");
		localize("pt_BR", "answers.exception", "uma exceção ocorreu durante a execução do comando:");
		localize("en_US", "answers.exception", "uma exceção ocorreu durante a execução do comando:");

		localize("en_US", "eval.noOut", "Executed successfully (No output was provided)");
		localize("pt_BR", "eval.noOut", "Executou com sucesso (Nenhum objeto foi retornado)");

		localize("en_US", "stats.negativeTime", "<Negative difference of time>");
		localize("pt_BR", "stats.negativeTime", "<Diferença negativa de tempo>");
		localize("en_US", "stats.timeFormat", "%d days, %d hours, %d minutes, %d seconds");
		localize("pt_BR", "stats.timeFormat", "%d dias, %d horas, %d minutos, %d segundos");

		localize("en_US", "gui.title", "$(dynamic.botname) - GUI");
		//pt_BR = en_US
		localize("en_US", "gui.stats", "Stats");
		localize("pt_BR", "gui.stats", "Estatísticas");
		localize("en_US", "gui.guilds", "Guilds");
		//pt_BR = en_US
		localize("en_US", "gui.input", "Input");
		//pt_BR = en_US
		localize("en_US", "gui.logAndChat", "Log and Chat");
		localize("pt_BR", "gui.logAndChat", "Log e Chat");
		localize("en_US", "gui.cmds.help", "Show all Console Commands");
		localize("pt_BR", "gui.cmds.help", "Mostra todos os Comandos de Console");
		localize("en_US", "gui.cmds.save", "$$=bot.save.usage;");
		localize("pt_BR", "gui.cmds.save", "$$=bot.save.usage;");
		localize("en_US", "gui.cmds.load", "$$=bot.load.usage;");
		localize("pt_BR", "gui.cmds.load", "$$=bot.load.usage;");
		localize("en_US", "gui.cmds.stop", "$$=bot.stop.usage;");
		localize("pt_BR", "gui.cmds.stop", "$$=bot.stop.usage;");
		localize("en_US", "gui.cmds.restart", "$$=bot.restart.usage;");
		localize("pt_BR", "gui.cmds.restart", "$$=bot.restart.usage;");
		localize("en_US", "gui.cmds.lang", "Set GUI Language");
		localize("pt_BR", "gui.cmds.lang", "Define a Língua da GUI");
		localize("en_US", "gui.stats0", "Uptime: %s");
		//pt_BR = en_US
		localize("en_US", "gui.stats1", "%d msgs; %d cmds; %d crashes; %d spam; %d noperms; %d invalidargs.");
		//pt_BR = en_US
		localize("en_US", "gui.stats2", "%d wgets; %d active threads.");
		localize("pt_BR", "gui.stats2", "%d wgets; %d threads ativas.");
		localize("en_US", "gui.stats3", "%d saves; %d loads.");
		//pt_BR = en_US
		localize("en_US", "gui.stats4", "RAM(Using/Total/Max): %s MB/%d MB/%d MB.");
		localize("en_US", "gui.stats4", "RAM(Usando/Total/Max): %s MB/%d MB/%d MB.");
		localize("en_US", "gui.stats5", "CPU Usage: %.2f%%.");
		localize("en_US", "gui.stats5", "CPU Usage: %.2f%%.");
		localize("en_US", "gui.load", "$$=bot.load;");
		localize("pt_BR", "gui.load", "$$=bot.load;");
		localize("en_US", "gui.save", "$$=bot.save;");
		localize("pt_BR", "gui.save", "$$=bot.save;");
		localize("en_US", "gui.done", "Done.");
		localize("pt_BR", "gui.done", "Feito.");
	}

	public static void implLocal() {
		localizeLocal("botname", Bot.SELF.getName());
		localizeLocal("mention", Bot.SELF.getAsMention());
	}

	private static void localize(String lang, String untranslated, String translated) {
		I18n.pushTranslation(untranslated, lang, translated);
		I18n.setModerated(untranslated, lang, true);
	}

	private static void localizeLocal(String untranslated, String translated) {
		I18n.setLocalTranslation("dynamic." + untranslated, "en_US", translated);
		I18n.setModerated("dynamic." + untranslated, "en_US", true);
	}
}
