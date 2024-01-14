package com.raikuman.troubleclub.command;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.troubleclub.dialogue.DialogueManager;

import java.util.List;

public class TestDialogue extends Command {

    private final DialogueManager dialogueManager;

    public TestDialogue(DialogueManager dialogueManager) {
        this.dialogueManager = dialogueManager;
    }

    @Override
    public void handle(CommandContext ctx) {
        ctx.event().getMessage().delete().queue();
        dialogueManager.testDialogue();
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
