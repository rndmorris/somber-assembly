package dev.rndmorris.somberassembly.utils;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class Reflections {

    public static <C> Field findField(Class<C> clazz, String... potentialNames) {
        for (final var field : clazz.getDeclaredFields()) {
            final var fieldName = field.getName();
            for (final var name : potentialNames) {
                if (name.equals(fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    public static Field setAccessible(Field field) {
        Objects.requireNonNull(field);
        return setAccessible(field, true);
    }

    public static Field setAccessible(Field field, boolean accessible) {
        field.setAccessible(accessible);
        return field;
    }
}
