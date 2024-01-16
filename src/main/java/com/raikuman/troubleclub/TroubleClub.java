package com.raikuman.troubleclub;

import com.raikuman.botutilities.BotSetup;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.troubleclub.command.GetStickers;
import com.raikuman.troubleclub.command.TestDialogue;
import com.raikuman.troubleclub.dialogue.*;
import com.raikuman.troubleclub.dialogue.config.DayWeights;
import com.raikuman.troubleclub.dialogue.config.DialogueConfig;
import com.raikuman.troubleclub.dialogue.config.HourWeights;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TroubleClub {

    private static final Logger logger = LoggerFactory.getLogger(TroubleClub.class);

    public static void main(String[] args) {
        // Schedule tasks
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        DialogueManager dialogueManager = new DialogueManager(executor);
        dialogueManager.beginTask();

        HashMap<Club, JDA> clubMap = new HashMap<>();
        for (Club club : Club.values()) {
            //if (club != Club.SUU) continue;

            try {
                JDABuilder jdaBuilder = JDABuilder
                    .create(List.of(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.MESSAGE_CONTENT))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableCache(CacheFlag.VOICE_STATE)
                    //.addEventListeners()
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setMaxReconnectDelay(32)
                    .setAutoReconnect(true)
                    .setRequestTimeoutRetry(true);

                BotSetup setup = BotSetup
                    .setup(jdaBuilder)
                    .disableDatabase(true)
                    .setConfigs(new DialogueConfig(), new HourWeights(), new DayWeights());

                // Handle commands
                List<Command> commands = getCommands(dialogueManager).get(club);
                if (commands != null) {
                    setup = setup.addCommands(commands);
                }


                clubMap.put(club, setup.build(System.getenv(club + "TOKEN")));
            } catch (IllegalArgumentException e) {
                logger.error("Failed to initialize " + club + " bot: " + e.getMessage());
            }
        }

        File resourceDirectory = new File("resources");
        if (resourceDirectory.mkdirs()) {
            logger.info("Created resources directory");
        } else {
            logger.info("Resources directory already exists");
        }

        // Program must load with all bots
        if (clubMap.size() != Club.values().length) {
            logger.error("Failed to initialize all bots");
            System.exit(0);
        }

        dialogueManager.setClubMap(clubMap);
    }

    private static HashMap<Club, List<Command>> getCommands(DialogueManager dialogueManager) {
        HashMap<Club, List<Command>> commands = new HashMap<>();
        commands.put(Club.SUU, List.of(
            new GetStickers(),
            new TestDialogue(dialogueManager)
        ));

        return commands;
    }
}
