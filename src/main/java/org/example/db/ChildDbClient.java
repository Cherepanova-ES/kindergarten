package org.example.db;

import lombok.SneakyThrows;
import org.example.model.Child;
import org.example.model.Gender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.utils.Constants.FAILED_TO_INSERT_DATA;

public class ChildDbClient {

    private static final String QUERY_SELECT_COUNT = "SELECT COUNT(*) FROM kindergarten.child";
    private static final String QUERY_SELECT_ALL_BY_GROUP_ID = "SELECT * FROM kindergarten.child WHERE group_number = ?";
    private static final String QUERY_SELECT_BY_ID = "SELECT * FROM kindergarten.child WHERE id = ? LIMIT 1";
    private static final String QUERY_INSERT = "INSERT INTO kindergarten.child (group_number, full_name, gender, age) VALUES (?, ?, ?, ?)";
    private static final String QUERY_UPDATE = "UPDATE kindergarten.child SET group_number = ?, full_name = ?, gender = ?, age = ?  WHERE id = ?";
    private static final String QUERY_DELETE = "DELETE FROM kindergarten.child WHERE id = ? LIMIT 1";

    private ChildDbClient() {
    }

    public static List<Child> findAllByGroupId(int groupId) throws SQLException, ClassNotFoundException {
        return DbClient.select(QUERY_SELECT_ALL_BY_GROUP_ID, ChildDbClient::mapToList, groupId);
    }

    public static Optional<Child> findById(int id) throws SQLException, ClassNotFoundException {
        return DbClient.select(QUERY_SELECT_BY_ID, ChildDbClient::mapToChild, id);
    }

    public static Child create(Child child) throws SQLException, ClassNotFoundException {
        var id = DbClient.insert(QUERY_INSERT,
                child.getGroupNumber(),
                child.getFullName(),
                child.getGender().name(),
                child.getAge());
        return findById(id).orElseThrow(() -> new SQLException(FAILED_TO_INSERT_DATA));
    }

    public static Child update(Child child) throws SQLException, ClassNotFoundException {
        DbClient.update(QUERY_UPDATE,
                child.getGroupNumber(),
                child.getFullName(),
                child.getGender().name(),
                child.getAge(),
                child.getId());
        return child;
    }

    public static void delete(Child child) throws SQLException, ClassNotFoundException {
        DbClient.update(QUERY_DELETE, child.getId());
    }

    public static Optional<Integer> count() {
        try {
            return DbClient.select(QUERY_SELECT_COUNT, ChildDbClient::mapCount);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @SneakyThrows
    private static List<Child> mapToList(ResultSet resultSet) {
        var children = new ArrayList<Child>();

        while (resultSet != null && resultSet.next()) {
            children.add(createChild(resultSet));
        }

        return children;
    }

    @SneakyThrows
    private static Optional<Child> mapToChild(ResultSet resultSet) {
        if (resultSet != null && resultSet.next()) {
            return Optional.of(createChild(resultSet));
        }

        return Optional.empty();
    }

    @SneakyThrows
    private static Optional<Integer> mapCount(ResultSet resultSet) {
        if (resultSet != null && resultSet.next()) {
            return Optional.of(resultSet.getInt(1));
        }

        return Optional.empty();
    }

    private static Child createChild(ResultSet resultSet) throws SQLException {
        return new Child(
                resultSet.getInt("id"),
                resultSet.getInt("group_number"),
                resultSet.getString("full_name"),
                Gender.valueOf(resultSet.getString("gender")),
                resultSet.getInt("age")
        );
    }
}
