package com.raikuman.troubleclub.tamagopet.config;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.database.DatabaseStartup;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TamagopetStartup implements DatabaseStartup {

    private static final Logger logger = LoggerFactory.getLogger(TamagopetStartup.class);

    @Override
    public void startup(JDA jda) {
        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
        ) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS tamagopet_stats(" +
                    "tamagopet_stats_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER UNIQUE NOT NULL," +
                    "food INTEGER NOT NULL DEFAULT 0," +
                    "bath INTEGER NOT NULL DEFAULT 0," +
                    "spell INTEGER NOT NULL DEFAULT 0," +
                    "physical INTEGER NOT NULL DEFAULT 0," +
                    "magical INTEGER NOT NULL DEFAULT 0," +
                    "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE" +
                    ")"
            );
        } catch (SQLException e) {
            logger.error("An error occurred creating tamagopet tables");
        }
    }
}
