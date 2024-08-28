package dev.rndmorris.somberassembly.common.configs;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    static final String categoryItems = "items";

    public static int decayingFleshDropRate = 4;
    public static int decayingFleshDecayChance = 50;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        decayingFleshDropRate = configuration.getInt(
            "Decaying Flesh drop rate",
            categoryItems,
            decayingFleshDropRate,
            0,
            999999,
            "Higher is less frequent. 0 is guaranteed.");
        decayingFleshDecayChance = configuration.getInt(
            "Decaying Flesh decay chance",
            categoryItems,
            decayingFleshDecayChance,
            0,
            100,
            "Probability that Decaying Flesh will rot away, as a percentage");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
