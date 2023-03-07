package com.raikuman.troubleclub.club.config.member;

import com.raikuman.botutilities.configs.DatabaseConfigInterface;

import java.util.List;

/**
 *  Provides configuration for members
 *
 * @version 1.0 2023-05-03
 * @since 1.0
 */
public class MemberConfig implements DatabaseConfigInterface {

	@Override
	public List<String> tableStatements() {
		// language=SQLITE-SQL
		return List.of(
			"CREATE TABLE IF NOT EXISTS members(" +
				"member_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"member_long VARCHAR(20) NOT NULL UNIQUE);"
		);
	}
}