package com.reliquary.crow.commands.dnd.charactermanager.objects;

public class Equipment {

	String name, itemCategory, stealthAdvantage, additionalProperties;
	int armorClassBonus, maxDexMod, reqStrength, enchantmentBonus, allSkillBonus, allSaveBonus;

	// Constructor
	public Equipment(String name, int armorClassBonus, int maxDexMod, String itemCategory, int reqStrength, String stealthAdvantage, int enchantmentBonus, int allSkillBonus, int allSaveBonus, String additionalProperties) {
		this.name = name;
		this.armorClassBonus = armorClassBonus;
		this.maxDexMod = maxDexMod;
		this.itemCategory = itemCategory;
		this.reqStrength = reqStrength;
		this.stealthAdvantage = stealthAdvantage;
		this.enchantmentBonus = enchantmentBonus;
		this.allSkillBonus = allSkillBonus;
		this.allSaveBonus = allSaveBonus;
		this.additionalProperties = additionalProperties;
	}

	// Accessors
	public String getName() {
		return name;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public String getStealthAdvantage() {
		return stealthAdvantage;
	}

	public String getAdditionalProperties() {
		return additionalProperties;
	}

	public int getArmorClassBonus() {
		return armorClassBonus;
	}

	public int getMaxDexMod() {
		return maxDexMod;
	}

	public int getReqStrength() {
		return reqStrength;
	}

	public int getEnchantmentBonus() {
		return enchantmentBonus;
	}

	public int getAllSkillBonus() {
		return allSkillBonus;
	}

	public int getAllSaveBonus() {
		return allSaveBonus;
	}
}
