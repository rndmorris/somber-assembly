package dev.rndmorris.somberassembly.utils;

import java.util.List;

public final class ArrayUtil {

    private ArrayUtil() {}

    public static <E, L extends List<E>> E[] fillFromList(E[] fillArray, L fromList) {
        for (var index = 0; index < fillArray.length && index < fromList.size(); ++index) {
            fillArray[index] = fromList.get(index);
        }
        return fillArray;
    }

    public static <T> T[] fillFromInitializer(T[] array, InitializerClosure<T> initializer) {
        for (var index = 0; index < array.length; ++index) {
            array[index] = initializer.create(index);
        }
        return array;
    }

    public interface InitializerClosure<T> {

        T create(int index);
    }
}
