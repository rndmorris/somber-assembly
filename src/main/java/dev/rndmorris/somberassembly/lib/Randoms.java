package dev.rndmorris.somberassembly.lib;

import java.util.Random;

public abstract class Randoms {

    public static int nextInt(Random random, int from, int to) {
        if (to < from) {
            throw new IllegalArgumentException("Argument \"to\" must not be smaller than argument \"from\".");
        }

        return from + random.nextInt(to);
    }

    public static int nextPercentile(Random random) {
        return Randoms.nextInt(random, 1, 101);
    }
}
