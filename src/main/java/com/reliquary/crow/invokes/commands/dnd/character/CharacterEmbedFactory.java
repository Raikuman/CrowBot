package com.reliquary.crow.invokes.commands.dnd.character;

import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.equipment.Equipment;
import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.equipment.EquipmentEmbeds;
import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.home.Home;
import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.home.HomeEmbeds;
import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.statistics.Statistics;
import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.statistics.StatisticsEmbeds;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class controls creation of CharacterEmbedInterfaces and handles their instances
 *
 * @version 1.1 2022-24-01
 * @since 1.1
 */
public class CharacterEmbedFactory {

	private static CharacterEmbedFactory instance = null;

	private static final int MAX_INSTANCES = 5;
	private static final HashMap<String, List<CharacterEmbedInterface>> instances = new HashMap<>();

	private CharacterEmbedFactory() {}

	/**
	 * This method gets the current instance of this class
	 * @return Returns an instance of this class
	 */
	public static CharacterEmbedFactory getInstance() {
		if (instance == null)
			instance = new CharacterEmbedFactory();

		return instance;
	}

	/**
	 * This method gets an CharacterEmbedInterface instance from the hashmap using the interface name. This
	 * method also handles populating and deleting instances for a certain CharacterEmbedInterface
	 * @param embedName Provides the CharacterEmbedInterface name
	 * @param sheetsId Provides the Google Sheets id
	 * @param user Provides the user object
	 * @return Returns the CharacterEmbedInterface instance of the embed name for the user
	 */
	public CharacterEmbedInterface getEmbedInstance(String embedName, String sheetsId, User user) {

		CharacterEmbedInterface newEmbedInterface;

		// Empty case
		if (instances.isEmpty()) {
			System.out.println("EMPTY");
			newEmbedInterface = getMatchingInterface(embedName, sheetsId, user);
			if (newEmbedInterface == null)
				return null;

			List<CharacterEmbedInterface> embedInterfaceList = new ArrayList<>();
			embedInterfaceList.add(newEmbedInterface);

			instances.put(embedName, embedInterfaceList);
			return instances.get(embedName).get(0);
		}

		// Match case
		if (hasEmbedInstance(embedName, sheetsId, user)) {
			System.out.println("MATCH");
			return instances.get(embedName).stream().filter(
				inst -> inst.getUser().equals(user)
			).collect(Collectors.toList()).get(0);
		}

		// Empty instance list case
		if (instances.get(embedName) == null) {
			newEmbedInterface = getMatchingInterface(embedName, sheetsId, user);
			if (newEmbedInterface == null)
				return null;

			List<CharacterEmbedInterface> embedInterfaceList = new ArrayList<>();
			embedInterfaceList.add(newEmbedInterface);

			instances.put(embedName, embedInterfaceList);
			return instances.get(embedName).get(0);
		}

		// No match, add case
		if (instances.get(embedName).size() < MAX_INSTANCES) {
			newEmbedInterface = getMatchingInterface(embedName, sheetsId, user);
			if (newEmbedInterface == null)
				return null;

			instances.get(embedName).add(newEmbedInterface);
			return instances.get(embedName).get(instances.get(embedName).size() - 1);
		}

		newEmbedInterface = getMatchingInterface(embedName, sheetsId, user);
		if (newEmbedInterface == null)
			return null;

		// Remove and add case
		instances.get(embedName).remove(0);
		instances.get(embedName).add(newEmbedInterface);
		return instances.get(embedName).get(instances.get(embedName).size() - 1);
	}

	/**
	 * This method checks if there is an CharacterEmbedInterface instance of the interface's name for the user
	 * @param embedName Provides the CharacterEmbedInterface name
	 * @param user Provides the user object
	 * @return Returns true if an instance was found, false if no instances were found
	 */
	public boolean hasEmbedInstance(String embedName, String sheetsId, User user) {

		boolean hasInstance = false;

		try {
			CharacterEmbedInterface embedInterface = instances.get(embedName).stream().filter(
				inst -> inst.getUser().equals(user) && inst.getSheetsId().equals(sheetsId)
			).collect(Collectors.toList()).get(0);

			if (embedInterface != null)
				hasInstance = true;

		} catch (IndexOutOfBoundsException | NullPointerException ignored) {}

		return hasInstance;
	}

	/**
	 * This method creates a matching CharacterEmbedInterface instance of the embed's name with user's
	 * information
	 * @param embedName Provides the CharacterEmbedInterface name
	 * @param sheetsId Provides the Google Sheets id
	 * @param user Provides the user object
	 * @return Returns a new instance of the matching embed's interface
	 */
	private CharacterEmbedInterface getMatchingInterface(String embedName, String sheetsId, User user) {

		List<String> interfaceNames = new ArrayList<>(Arrays.asList(
			new Home().getMenuValue(),
			new Equipment().getMenuValue(),
			new Statistics().getMenuValue()
		));

		int index = 0;
		for (String interfaceName : interfaceNames) {
			if (interfaceName.equals(embedName)) {
				break;
			}
			index++;
		}

		switch (index) {

			case 0:
				return new HomeEmbeds(sheetsId, user);

			case 1:
				return new EquipmentEmbeds(sheetsId, user);

			case 2:
				return new StatisticsEmbeds(sheetsId, user);
		}

		return null;
	}
}
