package com.raikuman.troubleclub.yamboard;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class YamboardDatabaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(YamboardDatabaseHandler.class);

    public static void populateYamboardUsers() {
        // Get user ids
        List<Integer> userIds = new ArrayList<>();
        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement();
            ) {
                statement.execute("SELECT user_id FROM user");
                try (ResultSet resultSet = statement.getResultSet()) {
                    while (resultSet.next()) {
                        userIds.add(resultSet.getInt("user_id"));
                    }
                }
        } catch (SQLException e) {
            logger.error("An error occurred populating the yamboard table");
        }

        userIds.forEach(YamboardDatabaseHandler::addYamboardUser);
    }

    public static void addYamboardUser(int userId) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO yamboard(user_id) VALUES(?)"
            )) {
            statement.setInt(1, userId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred adding user to yamboard for: {}", userId);
        }
    }

    public static void upvote(User user, int karma) {
        int userId = DefaultDatabaseHandler.getUserId(user);
        if (userId == -1) return;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE yamboard SET upvotes = upvotes + ? WHERE user_id = ?"
            )) {
            statement.setInt(1, karma);
            statement.setInt(2, userId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred upvoting a post for user: {}", userId);
        }
    }

    public static void downvote(User user, int karma) {
        int userId = DefaultDatabaseHandler.getUserId(user);
        if (userId == -1) return;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE yamboard SET downvotes = downvotes + ? WHERE user_id = ?"
            )) {
            statement.setInt(1, karma);
            statement.setInt(2, userId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred downvoting a post for user: {}", userId);
        }
    }

    public static void postAmount(User user, int amount) {
        int userId = DefaultDatabaseHandler.getUserId(user);
        if (userId == -1) return;

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE yamboard SET posts = posts + ? WHERE user_id = ?"
            )) {
            statement.setInt(1, amount);
            statement.setInt(2, userId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred setting posts for user: {}", userId);
        }
    }
}
