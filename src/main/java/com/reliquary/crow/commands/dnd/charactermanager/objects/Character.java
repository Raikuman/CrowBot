package com.reliquary.crow.commands.dnd.charactermanager.objects;

import java.util.ArrayList;
import java.util.List;

public class Character {

	// Banner Info
	String characterName, playerName, race, hitDice;
	int level, inspiration, proficiencyBonus, armorClass, initiative, speed;

	List<String> characterClasses;

	// Background Info
		// Background Type : Personality Traits : Ideals : Bonds : Flaws
	List<String> backgroundInfo;

	// Attributes and Skills
	Attribute[] attributes;
	Skill[] skills;

	// Proficiencies
		// Armor : Weapon : Vehicle : Tool : Other
	List<ArrayList<String>> proficiencies;

	// Languages (Max 12)
	String[] languages;

	// Equipped Weapons (Max 5)
	Weapon[] equippedWeapons;

	// Equipped Items (Max 12)
	Equipment[] equippedEquipment;

	List<String> items;

	// Features & Traits
	List<String> featsAndTraits;

	// Appearance
	Appearance appearance;

	// Constructor
	public Character(String characterName, String playerName, String race, String hitDice, int level, int inspiration,
	                 int proficiencyBonus, int armorClass, int initiative, int speed) {
		this.characterName = characterName;
		this.playerName = playerName;
		this.race = race;
		this.hitDice = hitDice;
		this.level = level;
		this.inspiration = inspiration;
		this.proficiencyBonus = proficiencyBonus;
		this.armorClass = armorClass;
		this.initiative = initiative;
		this.speed = speed;

		attributes = new Attribute[6];
		skills = new Skill[18];
		languages = new String[12];
		equippedWeapons = new Weapon[5];
		equippedEquipment = new Equipment[5];
		items = new ArrayList<>();

		characterClasses = new ArrayList<>();
		backgroundInfo = new ArrayList<>();
		proficiencies = new ArrayList<>(2);
		featsAndTraits = new ArrayList<>();

		appearance = new Appearance();
	}

	// Mutators
	public void setAttributes(Attribute[] attributes) {
		this.attributes = attributes;
	}

	public void setSkills(Skill[] skills) {
		this.skills = skills;
	}

	public void setProficiencies(List<ArrayList<String>> proficiencies) {
		this.proficiencies = proficiencies;
	}

	public void setLanguages(String[] languages) {
		this.languages = languages;
	}

	public void setEquippedWeapons(Weapon[] equippedWeapons) {
		this.equippedWeapons = equippedWeapons;
	}

	public void setEquippedEquipment(Equipment[] equippedEquipment) {
		this.equippedEquipment = equippedEquipment;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public void setFeatsAndTraits(List<String> featsAndTraits) {
		this.featsAndTraits = featsAndTraits;
	}

	public void setAppearance(Appearance appearance) {
		this.appearance = appearance;
	}

	// Accessors
	public List<String> getCharacterClasses() {
		return characterClasses;
	}

	public List<String> getBackgroundInfo() {
		return backgroundInfo;
	}

	public Attribute[] getAttributes() {
		return attributes;
	}

	public Skill[] getSkills() {
		return skills;
	}

	public List<ArrayList<String>> getProficiencies() {
		return proficiencies;
	}

	public String[] getLanguages() {
		return languages;
	}

	public Weapon[] getEquippedWeapons() {
		return equippedWeapons;
	}

	public Equipment[] getEquippedEquipment() {
		return equippedEquipment;
	}

	public List<String> getItems() {
		return items;
	}

	public List<String> getFeatsAndTraits() {
		return featsAndTraits;
	}

	public Appearance getAppearance() {
		return appearance;
	}
}

class Appearance {

	String height, weight, size, gender, eyes, hair, skin;
	int age;

	// Constructor
	Appearance() {
		this.height = "height";
		this.weight = "weight";
		this.size = "size";
		this.gender = "gender";
		this.eyes = "eyes";
		this.hair = "hair";
		this.skin = "skin";
		this.age = 0;
	}

	Appearance(String height, String weight, String size, String gender,
	           String eyes, String hair, String skin, int age) {
		this.height = height;
		this.weight = weight;
		this.size = size;
		this.gender = gender;
		this.eyes = eyes;
		this.hair = hair;
		this.skin = skin;
		this.age = age;
	}

	// Mutators
	public void setHeight(String height) {
		this.height = height;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setEyes(String eyes) {
		this.eyes = eyes;
	}

	public void setHair(String hair) {
		this.hair = hair;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public void setAge(int age) {
		this.age = age;
	}

	// Accessors
	public String getHeight() {
		return height;
	}

	public String getWeight() {
		return weight;
	}

	public String getSize() {
		return size;
	}

	public String getGender() {
		return gender;
	}

	public String getEyes() {
		return eyes;
	}

	public String getHair() {
		return hair;
	}

	public String getSkin() {
		return skin;
	}

	public int getAge() {
		return age;
	}
}
