package dev.rndmorris.somberassembly;

import java.util.function.Predicate;

public class Utils {

    public static <T> Predicate<T> equalsPredicate(T value) {
        return value::equals;
    }
}
