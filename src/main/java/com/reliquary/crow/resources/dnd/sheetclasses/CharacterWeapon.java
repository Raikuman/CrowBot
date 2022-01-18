package com.reliquary.crow.resources.dnd.sheetclasses;

import java.util.List;

/**
 * This class provides an object for organizing data when fetching attack information from Google Sheets
 *
 * @version 1.2 2022-18-01
 * @since 1.1
 */
public class CharacterWeapon {

	private final String name, damageDie, damageType, proficiency, range, enchantment, bonusDamageDie,
		bonusDamageType, abilityOverride, properties, abilityModifier;

	public CharacterWeapon(List<String> weaponStats) {
		this.name = checkDefault(weaponStats.get(0));
		this.damageDie = checkDefault(weaponStats.get(1));
		this.damageType = checkDefault(weaponStats.get(2));
		this.proficiency = checkDefault(weaponStats.get(3));
		this.range = checkDefault(weaponStats.get(4));
		this.enchantment = checkDefault(weaponStats.get(6));
		this.bonusDamageDie = checkDefault(weaponStats.get(7));
		this.bonusDamageType = checkDefault(weaponStats.get(8));
		this.abilityOverride = checkDefault(weaponStats.get(9));
		this.properties = checkDefault(weaponStats.get(10).replace("\n", " "));
		this.abilityModifier = checkDefault(weaponStats.get(12));
	}

	public String getName() {
		return name;
	}

	public String getDamageDie() {
		return damageDie;
	}

	public String getDamageType() {
		return damageType;
	}

	public String getProficiency() {
		return proficiency;
	}

	public String getRange() {
		return range;
	}

	public int getEnchantment() {
		try {
			return Integer.parseInt(enchantment);
		} catch(NumberFormatException e) {
			return 0;
		}
	}

	public String getBonusDamageDie() {
		return bonusDamageDie;
	}

	public String getBonusDamageType() {
		return bonusDamageType;
	}

	public String getAbilityOverride() {
		return abilityOverride;
	}

	public String getProperties() {
		return properties;
	}

	public int getAbilityModifier() {
		if (!abilityModifier.isEmpty())
			return Integer.parseInt(abilityModifier.replace("+", ""));
		else
			return 0;
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
