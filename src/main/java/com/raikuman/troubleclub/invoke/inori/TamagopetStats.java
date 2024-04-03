package com.raikuman.troubleclub.invoke.inori;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.invoke.category.Tamagopet;
import com.raikuman.troubleclub.tamagopet.TamagopetDatabaseHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.List;

public class TamagopetStats extends Command {

    @Override
    public void handle(CommandContext ctx) {
        List<Member> mentioned = ctx.event().getMessage().getMentions().getMembers();

        User targetUser;
        boolean isSelf;
        if (mentioned.isEmpty()) {
            targetUser = ctx.event().getAuthor();
            isSelf = true;
        } else {
            targetUser = mentioned.get(0).getUser();
            isSelf = false;
        }

        com.raikuman.troubleclub.tamagopet.TamagopetStats stats =
            TamagopetDatabaseHandler.getUserStats(targetUser);

        if (stats == null) {
            String user;
            if (isSelf) {
                user = "your";
            } else {
                user = targetUser.getEffectiveName() + "'s";
            }

            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.error(
                    "Could not retrieve " + user + " stats!",
                    "Could not get stats from database.",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));
            ctx.event().getMessage().delete().queue();
            return;
        }

        ctx.event().getChannel().sendMessageEmbeds(
            createEmbed(
                stats,
                targetUser,
                ctx.event().getChannel(),
                isSelf)).queue();
        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "tamagopetstats";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ben", "benstats", "pet", "tamago", "tamagopet", "petstats", "tamagostats");
    }

    @Override
    public String getDescription() {
        return "Shows all your stats when interacting with Ben.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Tamagopet());
    }

    private MessageEmbed createEmbed(com.raikuman.troubleclub.tamagopet.TamagopetStats stats, User targetUser,
                                     MessageChannelUnion channel, boolean isSelf) {
        // Build stats
        StringBuilder statBuilder = new StringBuilder("```asciidoc\n");

        // User strings
        String userTitle, userAction;
        if (isSelf) {
            userTitle = "you've";
            userAction = "You";
        } else {
            userTitle = targetUser.getEffectiveName() + " has";
            userAction = targetUser.getEffectiveName();
        }

        // Ben
        statBuilder
            .append("[ Things ")
            .append(userTitle)
            .append(" done with Ben ]\n");

        // Food
        statBuilder
            .append(" - ")
            .append(userAction)
            .append(" fed Ben :: ")
            .append(plurality(stats.getFood()))
            .append("!\n");

        // Bath
        statBuilder
            .append(" - ")
            .append(userAction)
            .append(" bathed Ben :: ")
            .append(plurality(stats.getBath()))
            .append("!\n");

        // Spell
        statBuilder
            .append(" - ")
            .append(userAction)
            .append(" casted a spell on Ben :: ")
            .append(plurality(stats.getSpell()))
            .append("!\n\n");

        // Bengrammaz
        statBuilder
            .append("[ Attacks ")
            .append(userTitle)
            .append(" inflicted against Bengrammaz ]\n");

        // Physical
        statBuilder
            .append(" - ")
            .append(userAction)
            .append(" hit Bengrammaz :: ")
            .append(plurality(stats.getPhysical()))
            .append("!\n");

        // Magical
        statBuilder
            .append(" - ")
            .append(userAction)
            .append(" used magic against Bengrammaz :: ")
            .append(plurality(stats.getMagical()))
            .append("!");

        statBuilder.append("\n```");

        String embedTitle;
        if (isSelf) {
            embedTitle = "Your";
        } else {
            embedTitle = targetUser.getEffectiveName() + "s";
        }
        return EmbedResources.defaultResponse(
            Tamagopet.BEN_COLOR,
            "\uD83D\uDC14 " + embedTitle + " Tamagopet Stats",
            statBuilder.toString(),
            channel,
            targetUser).build();
    }

    private String plurality(int num) {
        String string = num + " time";

        if (num != 1) {
            string += "s";
        }

        return string;
    }
}
