package com.reliquary.crow.commands.dnd.charactermanager.objects;

public class Item {

	String name;
	int num;

	// Constructor
	public Item(String name, int num) {
		this.name = name;
		this.num = num;
	}

	// Accessors
	public String getName() {
		return name;
	}

	public int getNum() {
		return num;
	}
}
