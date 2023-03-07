package com.raikuman.troubleclub.club.main;

import com.raikuman.botutilities.configs.ConfigFileWriter;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.listener.ListenerManager;
import com.raikuman.troubleclub.club.config.member.MemberConfig;
import com.raikuman.troubleclub.club.config.member.MemberDB;
import com.raikuman.troubleclub.club.config.yamboard.YamboardConfig;
import com.raikuman.troubleclub.club.statemanager.CharacterStateManager;
import com.raikuman.troubleclub.club.statemanager.configs.*;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import com.raikuman.troubleclub.club.utilities.JDAFinder;
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
 * @version 1.4 2023-05-03
 * @since 1.0
 */
public class TroubleClub {

	private static final Logger logger = LoggerFactory.getLogger(TroubleClub.class);

	public static void main(String[] args) {
		ConfigFileWriter.handleConfigs(true,
			new ReplyConfig(),
			new DialogueConfig(),
			new VoiceConfig(),
			new StatusConfig(),
			new StateConfig(),
			new YamboardConfig());

		HashMap<CharacterNames, JDA> jdaMap = constructJDAList();

		for (Map.Entry<CharacterNames, JDA> entry : jdaMap.entrySet()) {
			if (entry.getValue() == null) {
				logger.error("Could not create JDA objects for all bots");
				return;
			} else {
				logger.info("Created JDA object for: " + entry.getKey());
			}
		}

		for (Map.Entry<CharacterNames, JDA> entry : jdaMap.entrySet()) {
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

		// Handle state scheduling
		CharacterStateManager.getInstance().handleScheduling();

		setDatabase(JDAFinder.getInstance().getJDA(CharacterNames.SUU));
	}

	/**
	 * Builds the JDA objects
	 * @return The list of JDA objects
	 */
	private static HashMap<CharacterNames, JDA> constructJDAList() {
		HashMap<CharacterNames, JDA> jdaMap = new LinkedHashMap<>();
		for (Map.Entry<CharacterNames, String> entry : getBotTokenVars().entrySet()) {
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
	private static HashMap<CharacterNames, String> getBotTokenVars() {
		HashMap<CharacterNames, String> tokenVarMap = new LinkedHashMap<>();
		tokenVarMap.put(CharacterNames.DES, "destoken");
		tokenVarMap.put(CharacterNames.INORI, "inoritoken");
		tokenVarMap.put(CharacterNames.CROW, "crowtoken");
		tokenVarMap.put(CharacterNames.SUU, "suutoken");
		return tokenVarMap;
	}

	/**
	 * Handle database methods
	 * @param jda The jda object to set database with
	 */
	private static void setDatabase(JDA jda) {
		DatabaseManager.executeConfigStatements(List.of(
			new MemberConfig(),
			new YamboardConfig()
		));

		MemberDB.populateMemberTable(jda.getGuilds());
	}
}
