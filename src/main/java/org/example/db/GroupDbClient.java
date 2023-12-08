package org.example.db;

import lombok.SneakyThrows;
import org.example.model.Group;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.utils.Constants.FAILED_TO_INSERT_DATA;

public class GroupDbClient {

    private static final String QUERY_SELECT_ALL = "SELECT * FROM kindergarten.group";
    private static final String QUERY_SELECT_BY_NUMBER = "SELECT * FROM kindergarten.group WHERE number = ? LIMIT 1";
    private static final String QUERY_INSERT = "INSERT INTO kindergarten.group (name) VALUES (?)";
    private static final String QUERY_UPDATE = "UPDATE kindergarten.group SET name = ? WHERE number = ?";
    private static final String QUERY_DELETE = "DELETE FROM kindergarten.group WHERE number = ? LIMIT 1";

    private GroupDbClient() {
    }

    public static List<Group> findAll() throws SQLException, ClassNotFoundException {
        return DbClient.select(QUERY_SELECT_ALL, GroupDbClient::mapToList);
    }

    public static Optional<Group> findByNumber(int number) throws SQLException, ClassNotFoundException {
        return DbClient.select(QUERY_SELECT_BY_NUMBER, GroupDbClient::mapToGroup, number);
    }

    public static Group create(Group group) throws SQLException, ClassNotFoundException {
        var number = DbClient.insert(QUERY_INSERT, group.getName());
        return findByNumber(number).orElseThrow(() -> new SQLException(FAILED_TO_INSERT_DATA));
    }

    public static Group update(Group group) throws SQLException, ClassNotFoundException {
        DbClient.update(QUERY_UPDATE, group.getName(), group.getNumber());
        return group;
    }

    public static void delete(Group group) throws SQLException, ClassNotFoundException {
        DbClient.update(QUERY_DELETE, group.getNumber());
    }

    @SneakyThrows
    private static List<Group> mapToList(ResultSet resultSet) {
        var groups = new ArrayList<Group>();

        while (resultSet != null && resultSet.next()) {
            groups.add(createGroup(resultSet));
        }

        return groups;
    }

    @SneakyThrows
    private static Optional<Group> mapToGroup(ResultSet resultSet) {
        if (resultSet != null && resultSet.next()) {
            return Optional.of(createGroup(resultSet));
        }

        return Optional.empty();
    }

    private static Group createGroup(ResultSet resultSet) throws SQLException {
        return new Group(
                resultSet.getInt("number"),
                resultSet.getString("name"));
    }
}
