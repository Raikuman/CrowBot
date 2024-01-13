package com.raikuman.troubleclub.club.listener.invoke;

import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.club.commands.roleplaying.Dice;

import java.util.Arrays;
import java.util.List;

public class CrowInvoke {

    protected static List<CommandInterface> provideCommands() {
        return Arrays.asList(
            new Dice()
        );
    }
}
