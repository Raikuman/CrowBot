package com.reliquary.crow.commands.dnd.charactermanager.objects;

public class Attribute {

	String name;
	int value, bonus;

	// Constructor
	public Attribute(String name, int value, int bonus) {
		this.name = name;
		this.value = value;
		this.bonus = bonus;
	}

	// Accessors
	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public int getBonus() {
		return bonus;
	}
}
