package com.reliquary.crow.commands.dnd.charactermanager.objects;

public class SavingThrow {

	String name, proficiency;
	int value;

	// Constructor
	public SavingThrow(String name, String proficiency, int value) {
		this.name = name;
		this.value = value;

		if (proficiency.toLowerCase().contentEquals("p"))
			this.proficiency = "proficient";
		else
			this.proficiency = null;
	}

	// Accessors
	public String getName() {
		return name;
	}

	public String getProficiency() {
		return proficiency;
	}

	public int getValue() {
		return value;
	}
}
