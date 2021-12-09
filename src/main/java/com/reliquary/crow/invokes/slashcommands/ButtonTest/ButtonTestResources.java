package com.reliquary.crow.invokes.slashcommands.ButtonTest;

import com.reliquary.crow.managers.componentmanagers.buttons.ButtonInterface;
import com.reliquary.crow.invokes.slashcommands.ButtonTest.buttons.ButtonTestA;
import com.reliquary.crow.invokes.slashcommands.ButtonTest.buttons.ButtonTestB;
import com.reliquary.crow.invokes.slashcommands.ButtonTest.buttons.ButtonTestC;

import java.util.Arrays;
import java.util.List;

public class ButtonTestResources {

	private static final List<ButtonInterface> buttonInterfaces = Arrays.asList(
		new ButtonTestA(),
		new ButtonTestB(),
		new ButtonTestC()
	);

	public static List<ButtonInterface> getButtonInterfaces() {
		return buttonInterfaces;
	}

}
