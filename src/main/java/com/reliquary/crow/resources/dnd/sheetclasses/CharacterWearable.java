package com.reliquary.crow.resources.dnd.sheetclasses;

import java.util.List;

/**
 * This class provides an object for organizing data when fetching equipped item information from Google
 * Sheets
 *
 * @version 1.0 2022-18-01
 * @since 1.1
 */
public class CharacterWearable {

	private final String name, armorClassBonus, maxDexMod, category, strengthRequirement, stealthAdvantage,
		enchantment, proficiencyBonusBonus, skillBonus, saveBonus, properties;

	public CharacterWearable(List<String> itemStats) {
		this.name = checkDefault(itemStats.get(0));
		this.armorClassBonus = checkDefault(itemStats.get(1));
		this.maxDexMod = checkDefault(itemStats.get(2));
		this.category = checkDefault(itemStats.get(3));
		this.strengthRequirement = checkDefault(itemStats.get(4));
		this.stealthAdvantage = checkDefault(itemStats.get(5));
		this.enchantment = checkDefault(itemStats.get(6));
		this.proficiencyBonusBonus = checkDefault(itemStats.get(7));
		this.skillBonus = checkDefault(itemStats.get(8));
		this.saveBonus = checkDefault(itemStats.get(9));
		this.properties = checkDefault(itemStats.get(10));
	}

	public String getName() {
		return name;
	}

	public int getArmorClassBonus() {
		try {
			return Integer.parseInt(armorClassBonus);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	public String getMaxDexMod() {
		return maxDexMod;
	}

	public String getCategory() {
		return category;
	}

	public String getStrengthRequirement() {
		return strengthRequirement;
	}

	public String getStealthAdvantage() {
		return stealthAdvantage;
	}

	public int getEnchantment() {
		try {
			return Integer.parseInt(enchantment);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	public int getProficiencyBonusBonus() {
		try {
			return Integer.parseInt(proficiencyBonusBonus);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	public int getSkillBonus() {
		try {
			return Integer.parseInt(skillBonus);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	public int getSaveBonus() {
		try {
			return Integer.parseInt(saveBonus);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	public String getProperties() {
		return properties;
	}

	/**
	 * This method sets default values for missing data when fetching info
	 * @param input Provides string to check for defaults
	 * @return Return a fixed string for default values
	 */
	private String checkDefault(String input) {
		if (input.equals("-"))
			return "";
		else
			return input;
	}
}
