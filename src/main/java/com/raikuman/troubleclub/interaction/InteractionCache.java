package com.raikuman.troubleclub.interaction;

import com.raikuman.troubleclub.Club;

import java.io.File;
import java.util.List;

public record InteractionCache(List<Club> actors, List<String> matchWords, int requiredMatches, File interactionFile) { }
