package com.raikuman.troubleclub.yamboard.config;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.database.DatabaseStartup;
import com.raikuman.troubleclub.yamboard.YamboardDatabaseHandler;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class YamboardStartup implements DatabaseStartup {

    private static final Logger logger = LoggerFactory.getLogger(YamboardStartup.class);

    @Override
    public void startup(JDA jda) {
        // Setup tables
        if (!setupTables()) return;

        YamboardDatabaseHandler.populateYamboardUsers();
    }

    private boolean setupTables() {
        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
            ) {
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS yamboard(" +
                        "yamboard_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER UNIQUE NOT NULL," +
                        "upvotes INTEGER NOT NULL DEFAULT 0," +
                        "downvotes INTEGER NOT NULL DEFAULT 0," +
                        "posts INTEGER NOT NULL DEFAULT 0," +
                        "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE," +
                        "CHECK(upvotes >= 0)," +
                        "CHECK(downvotes >= 0)" +
                        ")"
                );

                return true;
        } catch (SQLException e) {
            logger.error("An error occurred creating yamboard tables");
            return false;
        }
    }
}
