package com.raikuman.troubleclub.invoke.suu;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.troubleclub.conversation.Conversation;
import com.raikuman.troubleclub.conversation.ConversationManager;
import com.raikuman.troubleclub.dialogue.Dialogue;
import com.raikuman.troubleclub.dialogue.DialoguePlayer;
import com.raikuman.troubleclub.dialogue.DialogueParser;
import net.dv8tion.jda.api.Permission;

import java.io.File;
import java.util.List;

public class TestDialogue extends Command {

    private final ConversationManager conversationManager;

    public TestDialogue(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    @Override
    public void handle(CommandContext ctx) {
        if (ctx.event().getMember() != null && !ctx.event().getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            return;
        }

        File file = new File("resources/conversations/sande.json");
        Conversation conversation = DialogueParser.getConversation(file.toPath());
        if (conversation == null) return;

        Dialogue dialogue = conversation.getDialogue();

        if (dialogue != null) {
            DialoguePlayer.setup(conversationManager.getClubMap(), dialogue).play(null);
        }

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "testdialogue";
    }

    @Override
    public List<String> getAliases() {
        return List.of("td");
    }
}
