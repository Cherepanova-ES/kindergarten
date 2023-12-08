package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class Group {
    private int number;
    private String name;

    public Group(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("[%03d] %s", number, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return number == group.number && Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name);
    }
}


