package com.raikuman.troubleclub.invoke.inori;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.invoke.category.Tamagopet;
import com.raikuman.troubleclub.tamagopet.TamagopetDatabaseHandler;

import java.util.List;

public class TamagopetStats extends Command {

    @Override
    public void handle(CommandContext ctx) {
        com.raikuman.troubleclub.tamagopet.TamagopetStats stats =
            TamagopetDatabaseHandler.getUserStats(ctx.event().getAuthor());

        if (stats == null) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.error(
                    "Could not retrieve your stats!",
                    "Could not get stats from database.",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));
            ctx.event().getMessage().delete().queue();
            return;
        }

        // Build stats
        StringBuilder statBuilder = new StringBuilder("```asciidoc\n");

        // Ben
        statBuilder.append("[ Things you've done with Ben ]\n");

        // Food
        statBuilder.append(" - You fed Ben :: ");
        statBuilder.append(plurality(stats.getFood()));
        statBuilder.append("!\n");

        // Bath
        statBuilder.append(" - You bathed Ben :: ");
        statBuilder.append(plurality(stats.getBath()));
        statBuilder.append("!\n");

        // Bath
        statBuilder.append(" - You casted a spell on Ben :: ");
        statBuilder.append(plurality(stats.getSpell()));
        statBuilder.append("!\n\n");

        // Bengrammaz
        statBuilder.append("[ Attacks you've inflicted against Bengrammaz ]\n");

        // Physical
        statBuilder.append(" - You hit Bengrammaz :: ");
        statBuilder.append(plurality(stats.getPhysical()));
        statBuilder.append("!\n");

        statBuilder.append(" - You used magic against Bengrammaz :: ");
        statBuilder.append(plurality(stats.getMagic()));
        statBuilder.append("!");

        statBuilder.append("\n```");

        ctx.event().getChannel().sendMessageEmbeds(
            EmbedResources.defaultResponse(
                Tamagopet.BEN_COLOR,
                "\uD83D\uDC14 Your Tamagopet Stats",
                statBuilder.toString(),
                ctx.event().getChannel(),
                ctx.event().getAuthor()).build()
        ).queue();
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

    private String plurality(int num) {
        String string = num + " time";

        if (num != 1) {
            string += "s";
        }

        return string;
    }
}
