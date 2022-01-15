package com.reliquary.crow.invokes.commands.dnd.character;

import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.home.Home;
import com.reliquary.crow.invokes.commands.dnd.character.selectionmenus.home.HomeEmbeds;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class controls creation of CharacterEmbedInterfaces and handles their instances
 *
 * @version 1.0 2022-14-01
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
			newEmbedInterface = getMatchingInterface(embedName, sheetsId, user);
			if (newEmbedInterface == null)
				return null;

			List<CharacterEmbedInterface> embedInterfaceList = new ArrayList<>();
			embedInterfaceList.add(newEmbedInterface);

			instances.put(embedName, embedInterfaceList);
			return instances.get(embedName).get(0);
		}

		// Match case
		if (hasEmbedInstance(embedName, user)) {
			return instances.get(embedName).stream().filter(
				inst -> inst.getUser().equals(user)
			).collect(Collectors.toList()).get(0);
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
	public boolean hasEmbedInstance(String embedName, User user) {

		if (instances.isEmpty())
			return false;

		return instances.get(embedName).stream().anyMatch(inst -> inst.getUser().equals(user));
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

		if (new Home().getMenuValue().equals(embedName)) {
			return new HomeEmbeds(sheetsId, user);
		}
		return null;
	}
}
