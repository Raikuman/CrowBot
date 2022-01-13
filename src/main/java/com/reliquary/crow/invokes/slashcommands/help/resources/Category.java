package com.reliquary.crow.invokes.slashcommands.help.resources;

/**
 * This class provides a simple object to easily get category names and their emoji
 *
 * @version 1.0 2021-09-12
 * @since 1.0
 */
public class Category {

	private final String name, emoji;

	public Category(String name, String emoji) {
		this.name = name;
		this.emoji = emoji;
	}

	public String getName() {
		return name;
	}

	public String getEmoji() {
		return emoji;
	}

}
