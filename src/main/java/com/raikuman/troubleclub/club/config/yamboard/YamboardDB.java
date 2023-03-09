package com.raikuman.troubleclub.club.config.yamboard;

import com.raikuman.botutilities.database.DatabaseIO;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.troubleclub.club.members.des.yamboard.KarmaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles IO for the yamboard database
 *
 * @version 1.0 2023-07-03
 * @since 1.0
 */
public class YamboardDB {

	private static final Logger logger = LoggerFactory.getLogger(YamboardDB.class);

	/**
	 * Creates a yamboard entry in the database for the associated member
	 * @param memberIdString The member to create a yamboard entry for
	 */
	public static void addYamboardProfile(long memberIdString) {
		// Get member id from members table
		// language=SQLITE-SQL
		String memberId = DatabaseIO.getConfig(
			"SELECT members.member_id " +
				"FROM members " +
				"WHERE members.member_long = ?",
			"member_id",
			String.valueOf(memberIdString)
		);

		if (memberId.isEmpty())
			return;

		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"INSERT OR IGNORE INTO yamboard(member_id) VALUES(?)")
		) {

			preparedStatement.setString(1, memberId);
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not add yamboard profile to the yamboard table");
		}
	}

	/**
	 * Check if the user has a yamboard entry in the database
	 * @param memberIdString The member to check for a yamboard entry
	 * @return Whether the member has a yamboard entry or not
	 */
	public static boolean checkForProfile(long memberIdString) {
		// Get member id from members table
		// language=SQLITE-SQL
		String memberId = DatabaseIO.getConfig(
			"SELECT members.member_id " +
				"FROM members " +
				"WHERE members.member_long = ?",
			"member_id",
			String.valueOf(memberIdString)
		);

		// language=SQLITE-SQL
		String yamboardId = DatabaseIO.getConfig(
			"SELECT yamboard.yamboard_id " +
				"FROM yamboard " +
				"WHERE yamboard.member_id = ?",
			"yamboard_id",
			memberId
		);

		return yamboardId != null && !yamboardId.isEmpty();
	}

	/**
	 * Handles incrementing or decrementing the user's amount of yamboard posts
	 * @param memberIdString The member to increment or decrement yamboard posts
	 * @param add To add or remove a post from the member
	 */
	public static void handlePostNumber(long memberIdString, boolean add) {
		// Get member id from members table
		// language=SQLITE-SQL
		String memberId = DatabaseIO.getConfig(
			"SELECT members.member_id " +
				"FROM members " +
				"WHERE members.member_long = ?",
			"member_id",
			String.valueOf(memberIdString)
		);

		if (memberId.isEmpty())
			return;

		// Get post number from db
		// language=SQLITE-SQL
		String postsString = DatabaseIO.getConfig(
			"SELECT yamboard.posts " +
				"FROM yamboard " +
				"WHERE yamboard.member_id = ?",
			"posts",
			memberId
		);

		// Parse post number
		int posts;
		try {
			posts = Integer.parseInt(postsString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error("Could not retrieve posts for user " + memberIdString);
			return;
		}

		int additive;
		if (add)
			additive = 1;
		else
			additive = -1;

		// Update post to db
		// language=SQLITE-SQL
		boolean updated = DatabaseIO.updateConfig(
			"UPDATE yamboard " +
			"SET posts = ? " +
			"WHERE yamboard.member_id = ?",
			String.valueOf(posts + additive),
			memberId
		);

		if (!updated)
			logger.error("Could not update karma of user " + memberIdString);
	}

	/**
	 * Changes the amount of karma for the user's yamboard entry
	 * @param memberIdString The member to change the amount of karma for
	 * @param upvote Whether to change the upvote or downvote karma
	 * @param add Whether to add or subtract karma
	 * @param amount The amount of karma to change
	 */
	public static void manipulateKarma(long memberIdString, boolean upvote, boolean add, int amount) {
		// Get member id from members table
		// language=SQLITE-SQL
		String memberId = DatabaseIO.getConfig(
			"SELECT members.member_id " +
				"FROM members " +
				"WHERE members.member_long = ?",
			"member_id",
			String.valueOf(memberIdString)
		);

		if (memberId.isEmpty())
			return;

		StringBuilder statement = new StringBuilder();
		String configName;
		if (upvote) {
			// language=SQLITE-SQL
			statement.append("SELECT yamboard.upvotes ");
			configName = "upvotes";
		} else {
			// language=SQLITE-SQL
			statement.append("SELECT yamboard.downvotes ");
			configName = "downvotes";
		}

		// language=SQLITE-SQL
		statement
			.append("FROM yamboard ")
			.append("WHERE yamboard.member_id = ?");

		// Get karma from db
		String selectedKarma = DatabaseIO.getConfig(
			String.valueOf(statement),
			configName,
			memberId
		);

		if (selectedKarma == null || selectedKarma.isEmpty())
			return;

		// Increment karma
		int karma;
		try {
			karma = Integer.parseInt(selectedKarma);

			if (add)
				karma += amount;
			else
				karma -= amount;

			if (karma < 0)
				karma = 0;
		} catch (NumberFormatException e) {
			logger.error("Could not update karma for user " + memberIdString);
			return;
		}

		// Update selected karma
		statement = new StringBuilder();

		statement.append("UPDATE yamboard ");

		// language=SQLITE-SQL
		if (upvote)
			statement.append("SET upvotes = ? ");
		else
			statement.append("SET downvotes = ? ");

		// language=SQLITE-SQL
		statement.append("WHERE yamboard.member_id = ?");

		// language=SQLITE-SQL
		boolean updated = DatabaseIO.updateConfig(
			String.valueOf(statement),
			String.valueOf(karma),
			memberId
		);

		if (!updated)
			logger.error("Could not update karma of user " + memberIdString);
	}

	/**
	 * Get the karma object from the user's yamboard entry
	 * @param memberIdString The member to get the karma entry from
	 * @return The karma object for the user
	 */
	public static KarmaObject getKarma(long memberIdString) {
		if (!checkForProfile(memberIdString))
			return null;

		// Get member id from members table
		// language=SQLITE-SQL
		String memberId = DatabaseIO.getConfig(
			"SELECT members.member_id " +
				"FROM members " +
				"WHERE members.member_long = ?",
			"member_id",
			String.valueOf(memberIdString)
		);

		if (memberId.isEmpty())
			return null;

		// Get both karmas from db
		// language=SQLITE-SQL
		String upvotes = DatabaseIO.getConfig(
			"SELECT yamboard.upvotes " +
				"FROM yamboard " +
				"WHERE yamboard.member_id = ?",
			"upvotes",
			memberId
		);

		// language=SQLITE-SQL
		String downvotes = DatabaseIO.getConfig(
			"SELECT yamboard.downvotes " +
				"FROM yamboard " +
				"WHERE yamboard.member_id = ?",
			"downvotes",
			memberId
		);

		// language=SQLITE-SQL
		String posts = DatabaseIO.getConfig(
			"SELECT yamboard.posts " +
				"FROM yamboard " +
				"WHERE yamboard.member_id = ?",
			"posts",
			memberId
		);

		int upvoteCount, downvoteCount, postCount;
		try {
			upvoteCount = Integer.parseInt(upvotes);
			downvoteCount = Integer.parseInt(downvotes);
			postCount = Integer.parseInt(posts);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error("Could not retrieve karma for user " + memberIdString);
			return null;
		}

		return new KarmaObject(upvoteCount, downvoteCount, postCount);
	}
}
