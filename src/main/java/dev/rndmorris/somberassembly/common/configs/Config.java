package dev.rndmorris.somberassembly.common.configs;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    static final String categoryItems = "items";
    static final String categoryVillages = "villages";

    public static int decayingFleshDecayChance = 50;
    public static int decayingFleshDropRate = 4;

    public static int graveyardLargeBasementFrequency = 3;
    public static int graveyardLargeLimit = 1;
    public static int graveyardLargeWeight = 25;
    public static int graveyardSmallLimit = 2;
    public static int graveyardSmallWeight = 25;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        decayingFleshDecayChance = configuration.getInt(
            "Decaying Flesh decay chance",
            categoryItems,
            decayingFleshDecayChance,
            0,
            100,
            "Probability that Decaying Flesh will rot away, as a percentage");
        decayingFleshDropRate = configuration.getInt(
            "Decaying Flesh drop rate",
            categoryItems,
            decayingFleshDropRate,
            0,
            999999,
            "Higher is less frequent. 0 is guaranteed.");

        graveyardLargeBasementFrequency = configuration.getInt(
            "Large Graveyard basement frequency",
            categoryVillages,
            graveyardLargeBasementFrequency,
            -1,
            999999,
            "Higher is less frequent. 0 = always, -1 = never.");
        graveyardLargeLimit = configuration.getInt(
            "Large Graveyard Generation Limit",
            categoryVillages,
            graveyardLargeLimit,
            -1,
            999999,
            "Maximum number of large graveyards that can generate in a village.");
        graveyardLargeWeight = configuration.getInt(
            "Large Graveyard Generation Weight",
            categoryVillages,
            graveyardLargeWeight,
            -1,
            999999,
            "Likelihood of a large graveyard generating in a village.");

        graveyardSmallLimit = configuration.getInt(
            "Small Graveyard Generation Limit",
            categoryVillages,
            graveyardSmallLimit,
            0,
            999999,
            "Maximum number of graveyards that can generate in a village.");
        graveyardSmallWeight = configuration.getInt(
            "Small Graveyard Generation Rate",
            categoryVillages,
            graveyardSmallWeight,
            0,
            999999,
            "Likelihood of a small graveyard generating in a village.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
