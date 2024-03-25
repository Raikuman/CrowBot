package com.raikuman.troubleclub.yamboard;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.troubleclub.yamboard.buttons.Downvote;
import com.raikuman.troubleclub.yamboard.buttons.Upvote;
import com.raikuman.troubleclub.yamboard.buttons.YamboardButtonManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class YamboardManager {

    private static final Logger logger = LoggerFactory.getLogger(YamboardManager.class);
    private final YamboardButtonManager buttonManager;
    private final LinkedList<YamboardMessage> yamboardMessages = new LinkedList<>();
    private final ConfigData yamboardConfig;
    private final List<Emoji> emojis;
    private final TextChannel yamChannel, listenChannel;
    private final File yamFile;

    public YamboardManager(YamboardButtonManager buttonManager, ConfigData yamboardConfig,
                           TextChannel yamChannel,
                           TextChannel listenChannel) {
        this.buttonManager = buttonManager;
        this.yamboardConfig = yamboardConfig;

        emojis = List.of(
            Emoji.fromFormatted(yamboardConfig.getConfig("reactionemoji")),
            Emoji.fromFormatted(yamboardConfig.getConfig("upemoji")),
            Emoji.fromFormatted(yamboardConfig.getConfig("downemoji"))
        );

        this.yamChannel = yamChannel;
        this.listenChannel = listenChannel;

        // Create file for yams
        yamFile = new File("resources" + File.separator + " yamboard.txt");
        try {
            if (yamFile.createNewFile()) {
                logger.info("Created file {}", yamFile.getAbsolutePath());
            } else {
                logger.info("File {} exists", yamFile.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", yamFile.getAbsolutePath());
        }

        loadYams();
    }

    public ConfigData getConfig() {
        return yamboardConfig;
    }

    public List<Emoji> getEmojis() {
        return emojis;
    }

    public void handleYam(User user, Message message, boolean isUpvote) {
        MessageReaction reaction = message.getReaction(emojis.get(0));

        // Count true reactions
        int reactions;
        if (reaction == null) {
            reactions = 0;
        } else {
            if (Boolean.parseBoolean(yamboardConfig.getConfig("enableselfkarma"))) {
                reactions = reaction.getCount();
            } else {
                List<User> reactors = new ArrayList<>(reaction.retrieveUsers().complete());
                reactors.remove(message.getAuthor());
                reactions = reactors.size();
            }
        }

        if (reactions == 1 && !yamExists(message)) {
            createYam(user, message);
        } else if (reactions == 0 && yamExists(message)) {
            removeYam(message);
        } else {
            handleKarma(user, message, true, isUpvote);
        }
    }

    private void createYam(User yammer, Message message) {
        // Handle yamboard messages
        List<String> upvoters = new ArrayList<>();
        if (yammer.getId().equals(message.getAuthor().getId())) {
            if (Boolean.parseBoolean(yamboardConfig.getConfig("enableselfkarma"))) {
                upvoters.add(yammer.getId());
            }
        } else {
            upvoters.add(yammer.getId());
        }

        // Handle embed
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.decode("#e37827"))
            .setAuthor(yammer.getEffectiveName() + " yammed " + message.getAuthor().getEffectiveName() + "'s message!", null,
                message.getAuthor().getEffectiveAvatarUrl())
            .setTitle(message.getAuthor().getEffectiveName() + "'s original message", message.getJumpUrl())
            .setDescription(message.getContentDisplay())
            .setFooter("#" + message.getChannel().getName())
            .setTimestamp(Instant.now());

        if (!upvoters.isEmpty()) {
            builder
                .addField("Upvotes", "1", true)
                .addField("Downvotes", "0", true)
                .addField("Ratio", "100%", true);
        } else {
            builder
                .addField("Upvotes", "0", true)
                .addField("Downvotes", "0", true)
                .addField("Ratio", "-%", true);
        }

        List<ButtonComponent> buttons = List.of(
            new Upvote(this, emojis.get(1)),
            new Downvote(this, emojis.get(2))
        );

        Message output = yamChannel.sendMessageEmbeds(builder.build()).setComponents(
            ComponentBuilder.buildButtons(
                message.getAuthor(),
                buttons
            )
        ).complete();

        // Handle embed buttons
        buttonManager.addButtons(output, buttons);

        yamboardMessages.add(new YamboardMessage(message, output, new ArrayList<>(upvoters), new ArrayList<>()));

        if (yamboardMessages.size() > YamboardButtonManager.MAX_ENTRIES) {
            yamboardMessages.removeFirst();
        }

        saveYams();
        YamboardDatabaseHandler.upvote(message.getAuthor(), 1);

        // Add post to db
        YamboardDatabaseHandler.postAmount(message.getAuthor(), 1);
    }

    private void removeYam(Message message) {
        // Get post message
        YamboardMessage found = null;
        for (YamboardMessage yamboardMessage : yamboardMessages) {
            if (yamboardMessage.yamMessage.getId().equals(message.getId())) {
                found = yamboardMessage;

                // Remove post message
                buttonManager.removeButtons(yamboardMessage.postMessage);
                yamChannel.deleteMessageById(yamboardMessage.postMessage.getId()).complete();
                break;
            }
        }

        if (found == null) return;

        // Remove yam from local
        yamboardMessages.remove(found);
        saveYams();

        // Remove upvotes and downvotes from db
        YamboardDatabaseHandler.upvote(found.yamMessage.getAuthor(), -1 * found.upvoters.size());
        YamboardDatabaseHandler.downvote(found.yamMessage.getAuthor(), -1 * found.downvoters.size());

        // Remove post from db
        YamboardDatabaseHandler.postAmount(found.yamMessage.getAuthor(), -1);
    }

    private void saveYams() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(yamFile))) {
            bufferedWriter.write("");
            for (YamboardMessage yamboardMessage : yamboardMessages) {
                StringBuilder builder = new StringBuilder();

                // Handle message ids
                builder
                    .append(yamboardMessage.yamMessage.getId())
                    .append(",")
                    .append(yamboardMessage.postMessage.getId())
                    .append("|");

                // Handle upvoter ids
                for (int i = 0; i < yamboardMessage.upvoters.size(); i++) {
                    builder.append(yamboardMessage.upvoters.get(i));

                    if (i != yamboardMessage.upvoters.size() - 1) {
                        builder.append(",");
                    }
                }

                builder.append(":");

                // Handle downvoter ids
                for (int i = 0; i < yamboardMessage.downvoters.size(); i++) {
                    builder.append(yamboardMessage.downvoters.get(i));

                    if (i != yamboardMessage.downvoters.size() - 1) {
                        builder.append(",");
                    }
                }

                bufferedWriter.write(builder + System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("Error writing file {}", yamFile.getAbsolutePath());
        }
    }

    private void loadYams() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(yamFile))) {
            for (String line; (line = bufferedReader.readLine()) != null;) {
                // Parse line
                String[] split = line.split("\\|");
                if (split.length != 2) continue;

                // Get messages
                Message original, post;
                String[] messages = split[0].split(",");
                try {
                    original = listenChannel.retrieveMessageById(messages[0]).complete();
                    post = yamChannel.retrieveMessageById(messages[1]).complete();
                } catch (ErrorResponseException e) {
                    logger.error("Could not retrieve messages as they do not exist/are unknown.");
                    continue;
                }

                if (original == null || post == null) {
                    logger.error("Error getting messages for {}", line);
                    continue;
                }

                // Get users
                String[] voters = split[1].split(":");
                List<String> upvoters = new ArrayList<>(), downvoters = new ArrayList<>();
                if (voters.length > 0) {
                    upvoters = new ArrayList<>(List.of(voters[0].split(",")));
                }

                if (voters.length > 1) {
                    downvoters = new ArrayList<>(List.of(voters[1].split(",")));
                }

                yamboardMessages.add(new YamboardMessage(
                    original,
                    post,
                    new ArrayList<>(upvoters),
                    new ArrayList<>(downvoters)));
            }
        } catch (IOException e) {
            logger.error("Error reading file {}", yamFile.getAbsolutePath());
        }
    }

    public void handleKarma(User voter, Message message, boolean isReaction, boolean isUpvote) {
        YamboardMessage yamboardMessage = null;
        for (YamboardMessage yam : yamboardMessages) {
            if (yam.yamMessage.getId().equals(message.getId()) || yam.postMessage.getId().equals(message.getId())) {
                yamboardMessage = yam;
                break;
            }
        }

        if (yamboardMessage == null) return;

        boolean voted = false;
        if (isReaction) {
            if (isUpvote) {
                if (!yamboardMessage.upvoters.contains(voter.getId())) {
                    yamboardMessage.upvoters.add(voter.getId());
                    YamboardDatabaseHandler.upvote(yamboardMessage.yamMessage.getAuthor(), 1);
                    voted = true;
                }
            } else {
                if (yamboardMessage.upvoters.contains(voter.getId())) {
                    yamboardMessage.upvoters.remove(voter.getId());
                    YamboardDatabaseHandler.upvote(yamboardMessage.yamMessage.getAuthor(), -1);
                    voted = true;
                }
            }
        } else {
            if (isUpvote) {
                if (!yamboardMessage.upvoters.contains(voter.getId())) {
                    yamboardMessage.upvoters.add(voter.getId());
                    YamboardDatabaseHandler.upvote(yamboardMessage.yamMessage.getAuthor(), 1);

                    // Check if voter is in downvoters
                    if (yamboardMessage.downvoters.contains(voter.getId())) {
                        yamboardMessage.downvoters.remove(voter.getId());
                        YamboardDatabaseHandler.downvote(yamboardMessage.yamMessage.getAuthor(), -1);
                    }

                    voted = true;
                }
            } else {
                if (!yamboardMessage.downvoters.contains(voter.getId())) {
                    yamboardMessage.downvoters.add(voter.getId());
                    YamboardDatabaseHandler.downvote(yamboardMessage.yamMessage.getAuthor(), 1);

                    // Check if voter is in upvoters
                    if (yamboardMessage.upvoters.contains(voter.getId())) {
                        yamboardMessage.upvoters.remove(voter.getId());
                        YamboardDatabaseHandler.upvote(yamboardMessage.yamMessage.getAuthor(), -1);
                    }

                    voted = true;
                }
            }
        }

        if (!voted) return;

        // Update embed
        yamboardMessage.postMessage.editMessageEmbeds(
            updatePost(yamboardMessage.postMessage.getEmbeds().get(0), yamboardMessage).build()
        ).queue();

        saveYams();
    }

    private boolean yamExists(Message message) {
        for (YamboardMessage yamboardMessage : yamboardMessages) {
            if (yamboardMessage.yamMessage.getId().equals(message.getId())) {
                return true;
            }
        }

        return false;
    }

    private EmbedBuilder updatePost(MessageEmbed messageEmbed, YamboardMessage yamboardMessage) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.decode("#e37827"))
            .setAuthor(Objects.requireNonNull(messageEmbed.getAuthor()).getName(), null, messageEmbed.getAuthor().getProxyIconUrl())
            .setTitle(messageEmbed.getTitle(), messageEmbed.getUrl())
            .setDescription(messageEmbed.getDescription())
            .setFooter(Objects.requireNonNull(messageEmbed.getFooter()).getText())
            .setTimestamp(messageEmbed.getTimestamp());

        DecimalFormat format = new DecimalFormat("0.#");

        builder
            .addField("Upvotes", String.valueOf(yamboardMessage.upvoters.size()), true)
            .addField("Downvotes", String.valueOf(yamboardMessage.downvoters.size()), true)
            .addField("Ratio",
                format.format((double) yamboardMessage.upvoters.size() / (yamboardMessage.upvoters.size() + yamboardMessage.downvoters.size()) * 100) + "%", true);

        return builder;
    }

    static class YamboardMessage {

        private final Message yamMessage, postMessage;
        private final List<String> upvoters, downvoters;

        public YamboardMessage(Message yamMessage, Message postMessage, List<String> upvoters, List<String> downvoters) {
            this.yamMessage = yamMessage;
            this.postMessage = postMessage;
            this.upvoters = upvoters;
            this.downvoters = downvoters;
        }

        public void addUpvote(User user) {

        }
    }
}
