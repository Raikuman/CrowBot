package com.raikuman.troubleclub.club.listener.invoke;

import com.raikuman.botutilities.invokes.manager.InvokeManager;
import com.raikuman.botutilities.invokes.manager.InvokeProvider;
import com.raikuman.troubleclub.club.main.CharacterNames;

public class InvokeData {

    public static InvokeManager getManager(CharacterNames character) {
        InvokeProvider invokeProvider = new InvokeProvider();

        switch (character) {
            case SUU:
                invokeProvider.addCommands(SuuInvoke.provideCommands());
                break;

            case INORI:
                invokeProvider.addCommands(InoriInvoke.provideCommands());
                break;

            case CROW:
                invokeProvider.addCommands(CrowInvoke.provideCommands());
                break;
        }

        return new InvokeManager(invokeProvider);
    }


}
