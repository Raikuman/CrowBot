package com.raikuman.troubleclub.club.config.yamboard;

import com.raikuman.botutilities.configs.ConfigInterface;
import com.raikuman.botutilities.configs.DatabaseConfigInterface;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Provides configuration for yamboard
 *
 * @version 1.0 2023-05-03
 * @since 1.0
 */
public class YamboardConfig implements ConfigInterface, DatabaseConfigInterface {

	@Override
	public String fileName() {
		return "yamboard";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		return new LinkedHashMap<>() {{
			put("reactionemoji", "U+1f360");
			put("postchannel", "");
			put("enableselfkarma", "false");
		}};
	}

	@Override
	public List<String> tableStatements() {
		// language=SQLITE-SQL
		return List.of(
			"CREATE TABLE IF NOT EXISTS yamboard(" +
			"yamboard_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"member_id INTEGER NOT NULL," +
			"upvotes INTEGER NOT NULL DEFAULT 0," +
			"downvotes INTEGER NOT NULL DEFAULT 0," +
			"posts INTEGER NOT NULL DEFAULT 0," +
			"FOREIGN KEY(member_id) REFERENCES members(member_id));"
		);
	}
}
