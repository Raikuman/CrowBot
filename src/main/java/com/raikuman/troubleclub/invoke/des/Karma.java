package com.raikuman.troubleclub.invoke.des;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.invoke.category.Fun;
import com.raikuman.troubleclub.yamboard.YamboardDatabaseHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import java.awt.*;
import java.util.List;

public class Karma extends Command {

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
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(
                    getInvoke(),
                    getUsage(),
                    ctx.event().getChannel()));
            return;
        }

        // Handle getting info from database
        KarmaInfo karmaInfo = YamboardDatabaseHandler.getUserKarma(targetUser);
        if (karmaInfo == null) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.error(
                    "Error getting karma",
                    "Could not retrieve karma from command",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()
                ));
            ctx.event().getMessage().delete().queue();
            return;
        }

        String title;
        if (!mentionedMembers.isEmpty()) {
            title = targetUser.getEffectiveName() + "'s Karma";
        } else {
            title = "Your Karma";
        }

        ctx.event().getChannel().sendMessageEmbeds(
            EmbedResources.defaultResponse(
                Color.decode("#fc8403"),
                "\uD83C\uDF60 " + title,
                "",
                ctx.event().getChannel(),
                ctx.event().getAuthor())
                .addField("Upvotes", "" + karmaInfo.upvotes, true)
                .addField("Downvotes", "" + karmaInfo.downvotes, true)
                .addField("Posts", "" + karmaInfo.posts, true)
                .build()
        ).queue();
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
        return "Shows your (or another user's) karma";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Fun());
    }

    public record KarmaInfo(int upvotes, int downvotes, int posts) {}
}
