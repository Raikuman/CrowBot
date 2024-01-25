package com.raikuman.troubleclub;

import com.raikuman.botutilities.BotSetup;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.troubleclub.command.GetStickers;
import com.raikuman.troubleclub.command.Karma;
import com.raikuman.troubleclub.command.TestDialogue;
import com.raikuman.troubleclub.dialogue.*;
import com.raikuman.troubleclub.dialogue.config.DayWeights;
import com.raikuman.troubleclub.dialogue.config.DialogueConfig;
import com.raikuman.troubleclub.dialogue.config.HourWeights;
import com.raikuman.troubleclub.interaction.InteractionListener;
import com.raikuman.troubleclub.interaction.InteractionManager;
import com.raikuman.troubleclub.yamboard.YamboardListener;
import com.raikuman.troubleclub.yamboard.config.YamboardConfig;
import com.raikuman.troubleclub.yamboard.config.YamboardStartup;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TroubleClub {

    private static final Logger logger = LoggerFactory.getLogger(TroubleClub.class);

    public static void main(String[] args) {
        // Schedule tasks
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        DialogueManager dialogueManager = new DialogueManager(executor);
        dialogueManager.beginTask();

        InteractionManager interactionManager = new InteractionManager(executor);

        HashMap<Club, JDA> clubMap = new HashMap<>();
        for (Club club : Club.values()) {
            try {
                JDABuilder jdaBuilder = JDABuilder
                    .create(List.of(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
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
                    .setup(jdaBuilder);

                if (club == Club.SUU) {
                    setup = setup
                        .setDatabases(new YamboardStartup())
                        .setConfigs(new DialogueConfig(), new HourWeights(), new DayWeights(), new YamboardConfig());
                } else {
                    setup = setup
                        .disableDatabase(true);
                }

                // Handle commands
                List<Command> commands = getCommands(dialogueManager).get(club);
                if (commands != null) {
                    setup = setup.addCommands(commands);
                }

                // Handle listeners
                List<ListenerAdapter> listeners = getListeners(setup.getExecutorService(), interactionManager).get(club);
                if (listeners != null) {
                    setup = setup.addListeners(listeners);
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
        interactionManager.setClubMap(clubMap);
    }

    private static HashMap<Club, List<Command>> getCommands(DialogueManager dialogueManager) {
        HashMap<Club, List<Command>> commands = new HashMap<>();
        commands.put(Club.SUU, List.of(
            new GetStickers(),
            new TestDialogue(dialogueManager)
        ));

        commands.put(Club.DES, List.of(
            new Karma()
        ));

        return commands;
    }

    private static HashMap<Club, List<ListenerAdapter>> getListeners(ExecutorService executor,
                                                                     InteractionManager interactionManager) {
        HashMap<Club, List<ListenerAdapter>> listeners = new HashMap<>();
        listeners.put(Club.SUU, List.of(
            new InteractionListener(executor, interactionManager)
        ));

        listeners.put(Club.DES, List.of(
            new YamboardListener(executor)
        ));

        return listeners;
    }
}
