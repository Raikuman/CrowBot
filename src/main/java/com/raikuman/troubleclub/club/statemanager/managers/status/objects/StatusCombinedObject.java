package com.raikuman.troubleclub.club.statemanager.managers.status.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raikuman.troubleclub.club.statemanager.StatusTypes;
import com.raikuman.troubleclub.club.utilities.CharacterNames;

import java.util.List;

/**
 * Holds information for the individual combined statuses
 *
 * @version 1.0 2023-22-01
 * @since 1.0
 */
public class StatusCombinedObject {

	@JsonProperty("characters")
	public List<CharacterNames> character;

	@JsonProperty("all-required")
	public boolean allRequired;

	@JsonProperty("type")
	public StatusTypes statusType;

	@JsonProperty("text")
	public String text;
}
