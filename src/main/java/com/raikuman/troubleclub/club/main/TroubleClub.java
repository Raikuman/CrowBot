package com.raikuman.troubleclub.club.main;

import com.raikuman.botutilities.configs.ConfigFileWriter;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.botutilities.listener.ListenerManager;
import com.raikuman.troubleclub.club.chat.configs.ChatConfig;
import com.raikuman.troubleclub.club.chat.configs.DialogueSchedulerConfig;
import com.raikuman.troubleclub.club.chat.dialogue.DialogueScheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Bot main class
 *
 * @version 1.1 2023-20-01
 * @since 1.0
 */
public class TroubleClub {

	private static final Logger logger = LoggerFactory.getLogger(TroubleClub.class);

	public static void main(String[] args) {
		ConfigFileWriter.handleConfigs(true, new ChatConfig(), new DialogueSchedulerConfig());

		HashMap<String, JDA> jdaMap = constructJDAList();

		for (Map.Entry<String, JDA> entry : jdaMap.entrySet()) {
			if (entry.getValue() == null) {
				logger.error("Could not create JDA objects for all bots");
				return;
			} else {
				logger.info("Created JDA object for: " + entry.getKey());
			}
		}

		for (Map.Entry<String, JDA> entry : jdaMap.entrySet()) {
			try {
				entry.getValue().awaitStatus(JDA.Status.CONNECTED);
				logger.info("Bot " + entry.getValue() + " connected to Discord");
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("Bot " + entry.getValue() + " could not connect to Discord");
			}
		}

		// Set JDA map
		JDAFinder.getInstance().setJDAMap(jdaMap);

		// Handle dialogue scheduling
		DialogueScheduler.checkDialogueSchedulingTesting();
	}

	/**
	 * Builds the JDA objects
	 * @return The list of JDA objects
	 */
	private static HashMap<String, JDA> constructJDAList() {
		HashMap<String, JDA> jdaMap = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : getBotTokenVars().entrySet()) {
			ListenerManager listenerManager = ListenerHandler.getListenerManager(entry.getKey());

			if (listenerManager == null) {
				logger.error("Could not load ListenerManager for token " + entry.getValue());
				continue;
			}

			try {
				jdaMap.put(
					entry.getKey(),
					JDABuilder
						.createDefault(EnvLoader.get(entry.getValue()))
						.enableIntents(getGatewayIntents())
						.setChunkingFilter(ChunkingFilter.ALL)
						.enableCache(CacheFlag.VOICE_STATE)
						.addEventListeners(listenerManager.getListeners())
						.setMemberCachePolicy(MemberCachePolicy.ALL)
						.setMaxReconnectDelay(32)
						.setAutoReconnect(true)
						.setRequestTimeoutRetry(true)
						.build()
				);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				jdaMap.put(
					entry.getKey(),
					null
				);
			}
		}

		return jdaMap;
	}

	/**
	 * Returns a list of gateway intents
	 * @return The GatewayIntent list
	 */
	private static List<GatewayIntent> getGatewayIntents() {
		return Arrays.asList(
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.MESSAGE_CONTENT
		);
	}

	/**
	 * Returns a map of variables for the EnvLoader to get tokens from
	 * @return The map of token variables to load
	 */
	private static HashMap<String, String> getBotTokenVars() {
		HashMap<String, String> tokenVarMap = new LinkedHashMap<>();
		tokenVarMap.put("des", "destoken");
		tokenVarMap.put("inori", "inoritoken");
		tokenVarMap.put("crow", "crowtoken");
		tokenVarMap.put("suu", "suutoken");
		return tokenVarMap;
	}
}
