package com.raikuman.troubleclub.invoke;

import com.raikuman.botutilities.defaults.invocation.Help;
import com.raikuman.botutilities.defaults.invocation.Settings;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.invocation.type.Slash;
import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.conversation.ConversationManager;
import com.raikuman.troubleclub.invoke.category.Fun;
import com.raikuman.troubleclub.invoke.category.Tamagopet;
import com.raikuman.troubleclub.invoke.crow.Dice;
import com.raikuman.troubleclub.invoke.des.Karma;
import com.raikuman.troubleclub.invoke.inori.Ben;
import com.raikuman.troubleclub.invoke.inori.TamagopetStats;
import com.raikuman.troubleclub.invoke.suu.GetStickers;
import com.raikuman.troubleclub.invoke.suu.TestDialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Invokes {

    public static HashMap<Club, List<Command>> getCommands(ConversationManager conversationManager) {
        HashMap<Club, List<Command>> commands = new HashMap<>();
        commands.put(Club.SUU, getSuuCommands(conversationManager));

        commands.put(Club.DES, getDesCommands());

        commands.put(Club.CROW, getCrowCommands());

        commands.put(Club.INORI, getInoriCommands());

        return commands;
    }

    public static HashMap<Club, List<Slash>> getSlashes(ConversationManager conversationManager) {
        HashMap<Club, List<Slash>> slashes = new HashMap<>();
        slashes.put(Club.SUU, getSuuSlashes(conversationManager));

        return slashes;
    }

    private static List<Command> getSuuCommands(ConversationManager conversationManager) {
        return List.of(
            new GetStickers(),
            new TestDialogue(conversationManager)
        );
    }

    private static List<Command> getDesCommands() {
        return List.of(
            new Karma()
        );
    }

    private static List<Command> getCrowCommands() {
        return List.of(
            new Dice()
        );
    }

    private static List<Command> getInoriCommands() {
        return List.of(
            new TamagopetStats(),
            new Ben()
        );
    }

    private static List<Slash> getSuuSlashes(ConversationManager conversationManager) {
        List<Slash> slashes = new ArrayList<>();
        List<Command> commands = new ArrayList<>();
        commands.addAll(getSuuCommands(conversationManager));
        commands.addAll(getDesCommands());
        commands.addAll(getCrowCommands());
        commands.addAll(getInoriCommands());

        slashes.add(new Help(
            "club",
            "Shows all commands for Trouble Club!",
            List.of(
                new Tamagopet(),
                new Fun(),
                new Settings()
            ),
            slashes,
            commands
        ));

        return slashes;
    }
}
