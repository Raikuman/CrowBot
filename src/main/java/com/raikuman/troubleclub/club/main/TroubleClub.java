package com.raikuman.troubleclub.club.main;

import com.raikuman.botutilities.BotUtilsSetup;
import com.raikuman.troubleclub.club.config.YamboardConfig;
import com.raikuman.troubleclub.club.listener.invoke.CrowInvoke;
import com.raikuman.troubleclub.club.listener.invoke.InoriInvoke;
import com.raikuman.troubleclub.club.listener.invoke.InvokeData;
import com.raikuman.troubleclub.club.listener.invoke.SuuInvoke;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TroubleClub {

	private static final Logger logger = LoggerFactory.getLogger(TroubleClub.class);

	public static void main(String[] args) {
		HashMap<CharacterNames, JDA> jdaMap = new LinkedHashMap<>();

		List<CharacterNames> characters = new ArrayList<>(List.of(CharacterNames.values()));
		characters.remove(CharacterNames.INORI);
		characters.remove(CharacterNames.CROW);
		//characters.remove(CharacterNames.DES);
		characters.remove(CharacterNames.SUU);

		for (CharacterNames character : characters) {
			BotUtilsSetup setup = BotUtilsSetup.setupJDA(
				getJDABuilder(
					List.of(
						GatewayIntent.GUILD_MEMBERS,
						GatewayIntent.GUILD_MESSAGES,
						GatewayIntent.GUILD_VOICE_STATES,
						GatewayIntent.MESSAGE_CONTENT)),
					InvokeData.getManager(character))
				.setTokenName(character.name() + "TOKEN");

			switch (character) {
				case SUU:

					break;

				case INORI:
					break;

				case CROW:
					break;

				case DES:
					setup = setup.setDatabases(new YamboardConfig());
					break;
			}

			jdaMap.put(character, setup.build());
		}

		// Pass unique interactions
	}

	private static List<GatewayIntent> getGatewayIntents() {
		return List.of(
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.GUILD_VOICE_STATES,
			GatewayIntent.MESSAGE_CONTENT
		);
	}

	/**
	 * Sets up a JDA Builder
	 * @param gatewayIntents The list of gateway intents
	 * @return The JDABuilder object
	 */
	private static JDABuilder getJDABuilder(List<GatewayIntent> gatewayIntents) {
		JDABuilder jdaBUilder = null;

		try {
			jdaBUilder = JDABuilder
				.create(gatewayIntents)
				.setChunkingFilter(ChunkingFilter.ALL)
				.enableCache(CacheFlag.VOICE_STATE)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.setMaxReconnectDelay(32)
				.setAutoReconnect(true)
				.setRequestTimeoutRetry(true);
		} catch (IllegalArgumentException e) {
			logger.error("Error creating JDABuilder object, skipping...");
		}

		return jdaBUilder;
	}
}
