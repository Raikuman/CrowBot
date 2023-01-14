package com.raikuman.troubleclub.club;

import com.raikuman.botutilities.configs.EnvLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TroubleClub {

	private static final Logger logger = LoggerFactory.getLogger(TroubleClub.class);

	public static void main(String[] args) {
		List<JDA> jdaList = constructJDAList();

		for (int i = 0; i < jdaList.size(); i++) {
			if (jdaList.get(i) == null)
				logger.error("Could not create JDA object of bot #" + i);
			else
				logger.info("Created JDA object of bot #" + i);
		}

		// Remove all null entries
		jdaList.removeAll(Collections.singleton(null));

		for (JDA jda : jdaList) {
			try {
				jda.awaitStatus(JDA.Status.CONNECTED);
				logger.info("Bot " + jda + " connected to Discord");
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("Bot " + jda + " could not connect to Discord");
			}
		}
	}

	private static List<JDA> constructJDAList() {
		List<String> botTokenVars = Arrays.asList(
			"destoken",
			"inoritoken",
			"crowtoken",
			"suutoken"
		);

		List<JDA> jdaList = new ArrayList<>();
		for (String tokenVar : botTokenVars) {
			try {
				jdaList.add(
					JDABuilder
						.createDefault(EnvLoader.get(tokenVar))
						.enableIntents(getGatewayIntents())
						.setChunkingFilter(ChunkingFilter.ALL)
						.enableCache(CacheFlag.VOICE_STATE)
						//.addEventListeners()
						.setMemberCachePolicy(MemberCachePolicy.ALL)
						.setMaxReconnectDelay(32)
						.setAutoReconnect(true)
						.setRequestTimeoutRetry(true)
						.build()
				);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return jdaList;
	}

	private static List<GatewayIntent> getGatewayIntents() {
		return Arrays.asList(
			GatewayIntent.GUILD_MEMBERS,
			GatewayIntent.GUILD_MESSAGES,
			GatewayIntent.MESSAGE_CONTENT
		);
	}
}
