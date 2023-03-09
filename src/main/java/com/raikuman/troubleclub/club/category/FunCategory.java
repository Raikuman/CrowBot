package com.raikuman.troubleclub.club.category;

import com.raikuman.botutilities.commands.manager.CategoryInterface;

/**
 * Provides name and emoji for the fun category
 *
 * @version 1.0 2023-06-03
 * @since 1.0
 */
public class FunCategory implements CategoryInterface {

	@Override
	public String getName() {
		return "fun";
	}

	@Override
	public String getEmoji() {
		return "\uD83C\uDF89";
	}
}
