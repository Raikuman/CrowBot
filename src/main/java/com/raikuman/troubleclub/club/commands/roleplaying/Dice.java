package com.raikuman.troubleclub.club.commands.roleplaying;

import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.security.SecureRandom;
import java.util.List;

/**
 * Handles rolling a dice or multiple dice with different number sides
 *
 * @version 1.0 2023-17-09
 * @since 1.0
 */
public class Dice implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getChannel().asTextChannel();

        int numDice, maxSides;
        if (ctx.getArgs().isEmpty()) {
            // Roll 1d20
            numDice = 1;
            maxSides = 20;
        } else if (ctx.getArgs().size() != 2) {
            // Incorrect args
            MessageResources.timedMessage(
                "You must provide a valid argument for this command: `" + getUsage() + "`",
                channel,
                5
            );
            return;
        } else {
            // Parse args
            try {
                numDice = Integer.parseInt(ctx.getArgs().get(0));
                maxSides = Integer.parseInt(ctx.getArgs().get(1));
            } catch (NumberFormatException e) {
                MessageResources.timedMessage(
                    "You must provide a valid argument for this command: `" + getUsage() + "`",
                    channel,
                    5
                );
                return;
            }
        }

        StringBuilder diceRolls = new StringBuilder();
        SecureRandom rand = new SecureRandom();
        int currentNum, diceTotal = 0;

        // Generate dice rolls
        for (int i = 0; i < numDice; i++) {
            currentNum = rand.nextInt(maxSides) + 1;
            diceTotal += currentNum;

            diceRolls.append(currentNum);

            if (i < numDice - 1) {
                diceRolls.append(" ");
            }

            rand.reseed();
        }

        // Build embed
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(RandomColor.getRandomColor())
            .setTitle(ctx.getEventMember().getNickname() + " rolled " + numDice + "d" + maxSides)
            .setFooter(DateAndTime.getDate() + " " + DateAndTime.getTime(), ctx.getEventMember().getEffectiveAvatarUrl());
        StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

        // Append rolls to embed
        descriptionBuilder
            .append("```md\n")
            .append("<Total: ")
            .append(diceTotal)
            .append("> < Rolls: ")
            .append(diceRolls)
            .append(">")
            .append("```");

        ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
        ctx.getEvent().getMessage().delete().queue();
    }

    @Override
    public String getUsage() {
        return "(<# of dice>) (<# of sides>)";
    }

    @Override
    public String getDescription() {
        return "Roll a dice!";
    }

    @Override
    public List<String> getAliases() {
        return List.of("roll", "d");
    }

    @Override
    public String getInvoke() {
        return "dice";
    }
}
