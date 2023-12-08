package org.example.model;

public enum Gender {
    MALE("M"),
    FEMALE("Ж");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
