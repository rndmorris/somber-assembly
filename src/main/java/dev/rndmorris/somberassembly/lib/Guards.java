package dev.rndmorris.somberassembly.lib;

public abstract class Guards
{
    public static int ensureNonNegative(int value, String argName) {
        if (value < 0) {
            throw new IllegalArgumentException(String.format("Argument %1s must not be negative.", argName));
        }
        return value;
    }
}
