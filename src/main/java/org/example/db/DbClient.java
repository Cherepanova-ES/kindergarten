package org.example.db;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Function;

import static org.example.utils.Constants.DATABASE_URL;
import static org.example.utils.Constants.FAILED_TO_INSERT_DATA;
import static org.example.utils.Constants.FAILED_TO_UPDATE_DATA;
import static org.example.utils.Constants.PASSWORD;
import static org.example.utils.Constants.USER;

public class DbClient {

    private DbClient() {
    }

    private static Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }

    public static <R> R select(String sql, Function<ResultSet, R> function, Object... params) throws SQLException, ClassNotFoundException {
        try (var connection = createConnection()) {
            try (var statement = connection.prepareStatement(sql)) {

                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }

                var resultSet = statement.executeQuery();
                return function.apply(resultSet);
            }
        }
    }

    public static <R> R select(String sql, Function<ResultSet, R> function) throws SQLException, ClassNotFoundException {
        return select(sql, function, List.of().toArray());
    }

    public static int insert(String sql, Object... params) throws SQLException, ClassNotFoundException {
        try (var connection = createConnection()) {
            try (var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }

                var affected = statement.executeUpdate();

                if (affected > 0) {
                    var generatedKeys = statement.getGeneratedKeys();

                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }

                throw new SQLException(FAILED_TO_INSERT_DATA);
            }
        }
    }

    public static void update(String sql, Object... params) throws SQLException, ClassNotFoundException {
        try (var connection = createConnection()) {
            try (var statement = connection.prepareStatement(sql)) {

                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }

                var affected = statement.executeUpdate();

                if (affected <= 0) {
                    throw new SQLException(FAILED_TO_UPDATE_DATA);
                }
            }
        }
    }

    public static void loadTestData() throws Exception {
        try (var connection = createConnection()) {
            var sr = new ScriptRunner(connection);
            var is = DbClient.class.getResourceAsStream("/db/schema.sql");
            var reader = new BufferedReader(new InputStreamReader(is));
            sr.runScript(reader);

            is = DbClient.class.getResourceAsStream("/db/data.sql");
            reader = new BufferedReader(new InputStreamReader(is));
            sr.runScript(reader);
        }
    }
}
