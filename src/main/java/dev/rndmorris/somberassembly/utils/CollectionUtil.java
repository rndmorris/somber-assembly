package dev.rndmorris.somberassembly.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

public final class CollectionUtil {

    private CollectionUtil() {}

    /**
     * Because `List.of()` does not build
     *
     * @param elements The elements to be in the list.
     * @return The new list.
     * @param <E> The type of list
     */
    @SafeVarargs
    public static <E> List<E> listOf(E... elements) {
        return Arrays.asList(elements);
    }

    @SafeVarargs
    public static <E> Stream<E> concat(Stream<E>... streams) {
        return Arrays.stream(streams)
            .reduce(Stream.empty(), Stream::concat);
    }

    public static boolean containsIgnoreCase(String[] array, String find) {
        if (array == null || array.length == 0) {
            return false;
        }
        return Arrays.stream(array)
            .anyMatch(find::equalsIgnoreCase);
    }

    @SafeVarargs
    public static <E, L1 extends Collection<E>> L1 addAll(@Nonnull L1 addTo, Collection<E>... addFrom) {
        for (var add : addFrom) {
            addTo.addAll(add);
        }
        return addTo;
    }

    public static <E, L1 extends Collection<E>> L1 empty(@Nonnull L1 empty) {
        empty.removeIf((e) -> true);
        return empty;
    }

    public static <E, L1 extends List<E>> int indexOf(@Nonnull L1 list, @Nonnull Predicate<E> predicate) {
        return IntStream.range(0, list.size())
            .filter(index -> predicate.test(list.get(index)))
            .findFirst()
            .orElse(-1);
    }

    public static <T> T randomElement(Random random, List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
