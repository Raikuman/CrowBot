package com.reliquary.crow.commands.dnd.charactermanager.objects;

public class Weapon {

	String name, damageDice, damageType, proficiencyCategory, rangeClass, bonusDamageDice,
		bonusDamageType, additionalProperties;
	int enchantmentBonus;

	// Constructor
	public Weapon(String name, String damageDice, String damageType, String proficiencyCategory,
	              String rangeClass, int enchantmentBonus, String bonusDamageDice, String bonusDamageType,
	              String additionalProperties) {
		this.name = name;
		this.damageDice = damageDice;
		this.damageType = damageType;
		this.proficiencyCategory = proficiencyCategory;
		this.rangeClass = rangeClass;
		this.enchantmentBonus = enchantmentBonus;
		this.bonusDamageDice = bonusDamageDice;
		this.bonusDamageType = bonusDamageType;
		this.additionalProperties = additionalProperties;
	}

	// Accessors
	public String getName() {
		return name;
	}

	public String getDamageDice() {
		return damageDice;
	}

	public String getDamageType() {
		return damageType;
	}

	public String getProficiencyCategory() {
		return proficiencyCategory;
	}

	public String getRangeClass() {
		return rangeClass;
	}

	public String getBonusDamageDice() {
		return bonusDamageDice;
	}

	public String getBonusDamageType() {
		return bonusDamageType;
	}

	public String getAdditionalProperties() {
		return additionalProperties;
	}

	public int getEnchantmentBonus() {
		return enchantmentBonus;
	}
}
