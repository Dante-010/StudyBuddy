package com.danteculaciati.studybuddy.Objectives;

import androidx.room.TypeConverter;

import java.time.LocalDate;


public class Converters {
    @TypeConverter
    public static LocalDate fromTimestamp(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long localDateToTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }

    @TypeConverter
    public static ObjectiveType fromTypeString(String type) {
        return type == null ? null : ObjectiveType.valueOf(type);
    }

    @TypeConverter
    public static String toTypeString(ObjectiveType type) {
        return type == null ? null : type.name();
    }
}