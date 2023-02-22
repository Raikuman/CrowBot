package com.raikuman.troubleclub.club.statemanager.managers.status.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import com.raikuman.troubleclub.club.statemanager.StatusTypes;

import java.util.HashMap;
import java.util.List;

/**
 * Holds information for the individual statuses
 *
 * @version 1.0 2023-22-01
 * @since 1.0
 */
public class StatusObject {

	@JsonProperty("type")
	public StatusTypes type;

	@JsonProperty("characters")
	public HashMap<CharacterNames, List<String>> statusMap;
}
