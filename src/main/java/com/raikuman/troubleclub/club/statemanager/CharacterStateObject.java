package com.raikuman.troubleclub.club.statemanager;

import com.raikuman.botutilities.configs.ConfigIO;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

/**
 * Holds commonly used character information for use in the state manager
 *
 * @version 1.1 2023-22-01
 * @since 1.0
 */
public class CharacterStateObject {

	private final JDA jda;
	private final Guild guild;

	public CharacterStateObject(JDA jda) {
		this.jda = jda;
		this.guild = jda.getGuildById(ConfigIO.readConfig("troubleclub/voice", "guildid"));
	}

	public JDA getJda() {
		return jda;
	}

	public Guild getGuild() {
		return guild;
	}
}
