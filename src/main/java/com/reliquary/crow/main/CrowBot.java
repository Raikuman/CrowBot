package com.reliquary.crow.main;

import com.reliquary.crow.listeners.VoiceEventListener;
import com.reliquary.crow.listeners.componentlisteners.ButtonEventListener;
import com.reliquary.crow.listeners.ReactionEventListener;
import com.reliquary.crow.listeners.SlashEventListener;
import com.reliquary.crow.listeners.TextEventListener;
import com.reliquary.crow.listeners.componentlisteners.SelectionMenuEventListener;
import com.reliquary.crow.resources.configs.ConfigHandler;
import com.reliquary.crow.resources.configs.DefaultConfigWriter;
import com.reliquary.crow.resources.configs.PresenceHandler;
import com.reliquary.crow.resources.configs.envConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.*;

public class CrowBot {

	private static final Logger logger = LoggerFactory.getLogger(CrowBot.class);

	public static void main(String[] args) throws LoginException, InterruptedException {

		JDA jda;

		// Initialize JDA using configuration
		jda = JDABuilder.createDefault(
				envConfig.get("token"),
				GatewayIntent.GUILD_MEMBERS,
				GatewayIntent.GUILD_MESSAGES,
				GatewayIntent.GUILD_VOICE_STATES,
				GatewayIntent.GUILD_MESSAGE_REACTIONS)
			.setChunkingFilter(ChunkingFilter.ALL)
			.enableCache(
				CacheFlag.VOICE_STATE)
			.addEventListeners(
				new TextEventListener(),
				new ReactionEventListener(),
				new SlashEventListener(),
				new ButtonEventListener(),
				new SelectionMenuEventListener(),
				new VoiceEventListener())
			.setMemberCachePolicy(MemberCachePolicy.ALL)
			.build();

		// Block jda until connected
		jda.awaitStatus(JDA.Status.CONNECTED);

		generateDefault();
		handlePresence(jda);
	}

	/**
	 * Generates the default configs and directories for the bot
	 */
	private static void generateDefault() {

		// Generate config folder first
		File directory = new File(envConfig.get("configdirectory"));
		if (directory.mkdir())
			logger.info("Created directory " + directory.getName() + " successfully");

		// Generate botSetting config first
		DefaultConfigWriter defaultConfigWriter = new DefaultConfigWriter();
		defaultConfigWriter.writeBotSettingsConfigFile();

		// Generate folders
		List<String> folders = new ArrayList<>(Arrays.asList(
			envConfig.get("configdirectory"),
			envConfig.get("configdirectory") + "/" +
				ConfigHandler.loadConfigSetting("botSettings", "presencedirectory"),
			ConfigHandler.loadConfigSetting("botSettings", "commandresourcedirectory")
		));

		// Generate other configs
		defaultConfigWriter.writeYamBoardConfigFile();

		// Directories
		for (String folderName : folders) {
			directory = new File(folderName);

			if (directory.mkdir())
				logger.info("Created directory " + folderName + " successfully");
		}
	}

	/**
	 * Updates the presence of the bot on a timer
	 * @param jda Provides the jda object to change presence
	 */
	private static void handlePresence(JDA jda) {

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
