package dev.rndmorris.somberassembly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    public static <T> T[] fillArray(T[] array, InitializerClosure<T> initializer) {
        for (var index = 0; index < array.length; ++index) {
            array[index] = initializer.create(index);
        }
        return array;
    }

    public static <T> List<T> list(T... elements) {
        return arrayList(elements);
    }

    public static <E> ArrayList<E> arrayList(E... elements) {
        final var result = new ArrayList<E>(elements.length);
        Collections.addAll(result, elements);
        return result;
    }

    public static <E> List<E> streamToList(Stream<E> stream) {
        final var result = new ArrayList<E>();
        stream.forEach(result::add);
        return result;
    }

    public static <E, L1 extends Collection<E>, L2 extends Collection<E>> L1 append(L1 appendTo, L2... appendValues) {
        for (var values : appendValues) {
            appendTo.addAll(values);
        }
        return appendTo;
    }

    public interface InitializerClosure<T> {

        T create(int index);
    }

    public static <T> Predicate<T> equalsPredicate(T value) {
        return value::equals;
    }
}
