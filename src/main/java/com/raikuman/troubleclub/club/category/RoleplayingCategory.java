package com.raikuman.troubleclub.club.category;

import com.raikuman.botutilities.commands.manager.CategoryInterface;

/**
 * Provides name and emoji for the roleplaying category
 *
 * @version 1.0 2023-14-01
 * @since 1.0
 */
public class RoleplayingCategory implements CategoryInterface {

	@Override
	public String getName() {
		return "roleplaying";
	}

	@Override
	public String getEmoji() {
		return "\uD83D\uDC09";
	}
}
