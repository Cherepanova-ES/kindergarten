package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class Child {
    private int id;
    private int groupNumber;
    private String fullName;
    private Gender gender;
    private int age;

    public Child(int groupNumber, String fullName, Gender gender, int age) {
        this.groupNumber = groupNumber;
        this.fullName = fullName;
        this.gender = gender;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", gender, fullName, getAgeAsString(age));
    }

    private String getAgeAsString(int age) {
        if (age == 1) {
            return age + " год";
        } else if (age > 1 && age < 5) {
            return age + " года";
        } else {
            return age + " лет";
        }
    }
}
