package com.raikuman.troubleclub.tamagopet;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TamagopetDatabaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(TamagopetDatabaseHandler.class);

    public static TamagopetStats getUserStats(User user) {
        int userId = DefaultDatabaseHandler.getUserId(user);
        if (userId == -1) {
            return null;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT food, bath, spell, physical, magical FROM tamagopet_stats WHERE user_id = ?"
            )) {
            statement.setInt(1, userId);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    // Return stat object
                    return new TamagopetStats(
                        user,
                        resultSet.getInt("food"),
                        resultSet.getInt("bath"),
                        resultSet.getInt("spell"),
                        resultSet.getInt("physical"),
                        resultSet.getInt("magical")
                    );
                } else {
                    // Create row
                    if (createUser(userId)) {
                        return new TamagopetStats(
                            user, 0, 0, 0, 0, 0
                        );
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred geting user stats for: {}", user.getEffectiveName());
            return null;
        }
    }

    private static boolean createUser(int userId) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO tamagopet_stats(user_id) VALUES(?)"
            )) {
            statement.setInt(1, userId);
            statement.execute();
            return true;
        } catch (SQLException e) {
            logger.error("An error occurred adding user to tamagopet_stats for: {}", userId);
            return false;
        }
    }

    public static void addFoodEvent(User user, int amount) {
        addEvent(user, "food", amount);
    }

    public static void addBathEvent(User user, int amount) {
        addEvent(user, "bath", amount);
    }

    public static void addSpellEvent(User user, int amount) {
        addEvent(user, "spell", amount);
    }

    public static void addPhysicalEvent(User user, int amount) {
        addEvent(user, "physical", amount);
    }

    public static void addMagicalEvent(User user, int amount) {
        addEvent(user, "magical", amount);
    }

    private static void addEvent(User user, String event, int amount) {
        int userId = DefaultDatabaseHandler.getUserId(user);
        if (userId == -1) {
            return;
        }

        getUserStats(user);

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE tamagopet_stats SET " + event + " = " + event + " + " + amount + " WHERE user_id = ?"
            )) {
            statement.setInt(1, userId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("An error occurred modifying event for: {}", event);
        }
    }
}
