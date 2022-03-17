package com.danteculaciati.studybuddy.Objectives;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public enum ObjectiveType {
    OBJECTIVE_READ ("Read"),
    OBJECTIVE_WATCH ("Watch"),
    OBJECTIVE_LISTEN ("Listen"),
    OBJECTIVE_DO ("Do");

    private final String name;

    ObjectiveType(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    @NonNull
    public String toString() {
        return this.name;
    }

    public static String[] names() {
        ObjectiveType[] ObjectiveTypes = values();
        String[] names = new String[ObjectiveTypes.length];

        for (int i = 0; i < ObjectiveTypes.length; i++) {
            names[i] = ObjectiveTypes[i].toString();
        }

        return names;
    }

    public static ObjectiveType getEnum(String value) {
        for(ObjectiveType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }

    public static int getNumberOfTypes() {
        return values().length;
    }
}