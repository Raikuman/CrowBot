package com.reliquary.crow.slashcommands.ButtonTest;

import com.reliquary.crow.componentmanagers.buttons.ButtonInterface;
import com.reliquary.crow.slashcommands.ButtonTest.buttons.ButtonTestA;
import com.reliquary.crow.slashcommands.ButtonTest.buttons.ButtonTestB;
import com.reliquary.crow.slashcommands.ButtonTest.buttons.ButtonTestC;

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
