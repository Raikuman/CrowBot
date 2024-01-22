package com.raikuman.troubleclub.command;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Karma extends Command {

    private static final Logger logger = LoggerFactory.getLogger(Karma.class);

    @Override
    public void handle(CommandContext ctx) {
        List<Member> mentionedMembers = ctx.event().getMessage().getMentions().getMembers();

        User targetUser;
        if (mentionedMembers.isEmpty()) {
            // Current user
            targetUser = ctx.event().getAuthor();
        } else if (mentionedMembers.size() == 1){
            // Other user
            targetUser = mentionedMembers.get(0).getUser();
        } else {
            // Incorrect usage
            ctx.event().getChannel().sendMessageEmbeds(
                EmbedResources.incorrectUsage(
                    getInvoke(),
                    getUsage(),
                    ctx.event().getChannel()
                ).build()
            ).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
            return;
        }

        // Handle getting info from database
        KarmaInfo karmaInfo = getUserKarma(targetUser);
        if (karmaInfo == null) {
            ctx.event().getChannel().sendMessageEmbeds(
                EmbedResources.error(
                    "Error getting karma",
                    "Could not retrieve karma from command",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()
                ).build()
            ).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.decode("#fc8403"))
            .setAuthor(targetUser.getEffectiveName() + "'s karma", null, targetUser.getEffectiveAvatarUrl())
            .setFooter("#" + ctx.event().getChannel().getName())
            .setTimestamp(Instant.now())
            .addField("Upvotes", "" + karmaInfo.upvotes, true)
            .addField("Downvotes", "" + karmaInfo.downvotes, true)
            .addField("Posts", "" + karmaInfo.posts, true);

        ctx.event().getChannel().sendMessageEmbeds(builder.build()).queue();
        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "karma";
    }

    @Override
    public List<String> getAliases() {
        return List.of("k");
    }

    @Override
    public String getUsage() {
        return "(<@user>)";
    }

    @Override
    public String getDescription() {
        return "Shows your (or another) karma";
    }

    private KarmaInfo getUserKarma(User user) {
        int userId = DefaultDatabaseHandler.getUserId(user);
        if (userId == -1) {
            return null;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT upvotes, downvotes, posts FROM yamboard WHERE user_id = ?"
            )) {
            statement.setInt(1, userId);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    return new KarmaInfo(
                        resultSet.getInt("upvotes"),
                        resultSet.getInt("downvotes"),
                        resultSet.getInt("posts"));
                }
            }
        } catch (SQLException e) {
            logger.error("Could not retrieve karma info for user: {}", userId);
        }

        return null;
    }

    class KarmaInfo {
        int upvotes, downvotes, posts;

        public KarmaInfo(int upvotes, int downvotes, int posts) {

        }
    }
}
