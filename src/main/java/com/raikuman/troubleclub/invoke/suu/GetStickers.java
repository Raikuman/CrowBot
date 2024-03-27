package com.raikuman.troubleclub.invoke.suu;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

import java.util.List;

public class GetStickers extends Command {

    @Override
    public void handle(CommandContext ctx) {
        if (!ctx.event().getAuthor().getEffectiveName().equals("Raiku")) return;

        StringBuilder builder = new StringBuilder();
        for (GuildSticker sticker : ctx.event().getGuild().getStickers()) {
            builder
                .append(sticker.getName())
                .append(" - ")
                .append(sticker.getId())
                .append("\n");
        }

        ctx.event().getChannel().sendMessage(builder.toString()).queue();
        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "getstickers";
    }

    @Override
    public List<String> getAliases() {
        return List.of("gs");
    }
}
