package com.reliquary.crow.commands.dnd.charactermanager.objects;

public class Skill {

	String name, proficiency, abilityModifier;
	int value;

	// Constructor
	public Skill(String name, String proficiency, String abilityModifier, int value) {
		this.name = name;
		this.abilityModifier = abilityModifier;
		this.value = value;

		if (proficiency.toLowerCase().contentEquals("e"))
			this.proficiency = "expert";
		else if (proficiency.toLowerCase().contentEquals("p"))
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

	public String getAbilityModifier() {
		return abilityModifier;
	}

	public int getValue() {
		return value;
	}
}
