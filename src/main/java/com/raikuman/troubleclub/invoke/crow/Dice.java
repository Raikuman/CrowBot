package com.raikuman.troubleclub.invoke.crow;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.invoke.category.Fun;

import java.util.List;
import java.util.Random;

public class Dice extends Command {

    @Override
    public void handle(CommandContext ctx) {
        boolean incorrectUsage = false;
        int numDice = 1, numSides = 20;

        if (ctx.args().size() == 2) {
            // Custom size
            try {
                numDice = Integer.parseInt(ctx.args().get(0));
                numSides = Integer.parseInt(ctx.args().get(1));

                if (numDice < 0 || numSides < 0) {
                    MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                        EmbedResources.error(
                            "Your arguments for rolling a dice must be positive!",
                            "Use positive numbers when rolling dice.",
                            ctx.event().getChannel(),
                            ctx.event().getAuthor()));
                    return;
                }
            } catch (NumberFormatException e) {
                // Incorrect usage
                incorrectUsage = true;
            }
        } else if (!ctx.args().isEmpty()) {
            incorrectUsage = true;
        }

        if (incorrectUsage) {
            // Incorrect usage
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            return;
        }

        // Check for good ranges
        if (numDice > 100) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error(
                    "You are rolling too many dice!",
                    "Maximum dice allowed to roll is 100.",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));
            return;
        }

        if (numSides > 200) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error(
                    "Your dice have too many sides!",
                    "Maximum sides allowed to roll is 200.",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));
            return;
        }

        // Construct roll
        StringBuilder rollBuilder = new StringBuilder("< Rolls: ");
        int total = 0, roll;
        for (int i = 0; i < numDice; i++) {
            roll = new Random().nextInt(numSides) + 1;
            total += roll;

            if (i != 0) {
                rollBuilder.append(" ");
            }

            rollBuilder.append(roll);
        }

        rollBuilder.append(" >");

        ctx.event().getChannel().sendMessageEmbeds(
            EmbedResources.defaultResponse(
                Fun.FUN_COLOR,
                "\uD83C\uDFB2 " + ctx.event().getAuthor().getEffectiveName() + " rolled " + numDice + "d" + numSides,
                "```md\n<Total: " + total + "> " + rollBuilder + "\n```",
                ctx.event().getChannel(),
                ctx.event().getAuthor()
            ).build()
        ).queue();
        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "dice";
    }

    @Override
    public List<String> getAliases() {
        return List.of("roll", "d", "r");
    }

    @Override
    public String getUsage() {
        return "(<# of dice> <# of sides>)";
    }

    @Override
    public String getDescription() {
        return "Roll a dice!";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Fun());
    }
}
