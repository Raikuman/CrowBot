package com.reliquary.crow.main;

import com.reliquary.crow.listeners.TextChannelListener;
import com.reliquary.crow.resources.configs.DefaultConfigWriter;
import com.reliquary.crow.resources.configs.PresenceHandler;
import com.reliquary.crow.resources.configs.envConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.*;

public class CrowBot {

	private static final Logger logger = LoggerFactory.getLogger(CrowBot.class);

	public static void main(String[] args) throws LoginException, InterruptedException {

		// Create JDA object without initialization
		JDA jda;

		// Initialize JDA using configuration
		jda = JDABuilder.createDefault(
				envConfig.get("token"),
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS
			)
			.enableCache(
				CacheFlag.VOICE_STATE
			)
			.addEventListeners(new TextChannelListener())
			.build();

		// Block jda until connected
		jda.awaitStatus(JDA.Status.CONNECTED);

		// Generate folders
		List<String> folders = new ArrayList<>(Arrays.asList(
			envConfig.get("configdirectory"),
			envConfig.get("configdirectory") + "/" + envConfig.get("presencedirectory")
		));

		// Directories
		File directory;
		for (String folderName : folders) {
			directory = new File(folderName);

			if (directory.mkdir())
				logger.info("Created directory " + folderName + " successfully");
		}

		// Configs
		DefaultConfigWriter defaultConfigWriter = new DefaultConfigWriter();
		defaultConfigWriter.writeBotSettingsConfigFile();

		// Bot Presence
		PresenceHandler.writePresenceFiles();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				PresenceHandler.updatePresence(jda);
			}
		}, 0, 5 * 60 * 1000);
	}
}
