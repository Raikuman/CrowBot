package com.raikuman.troubleclub.club.statemanager.managers.status.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Holds information for the combination statuses
 *
 * @version 1.0 2023-22-01
 * @since 1.0
 */
public class StatusCombinedArray {

	@JsonProperty("combination")
	public List<StatusCombinedObject> combinedList;
}
