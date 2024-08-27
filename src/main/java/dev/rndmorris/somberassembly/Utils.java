package dev.rndmorris.somberassembly;

import java.util.function.Predicate;

public class Utils {

    /**
     * Convenience method to qualify a name with the mod's id.
     *
     * @param name The name to prefix.
     * @return The qualified name.
     */
    public static String prefixModId(String name) {
        return SomberAssembly.MODID + ":" + name;
    }

    public interface InitializerClosure<T> {

        T create(int index);
    }

    public static <T> Predicate<T> equalsPredicate(T value) {
        return value::equals;
    }
}
