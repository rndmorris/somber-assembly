package dev.rndmorris.somberassembly.common.configs;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    static final String categoryItems = "items";

    public static int decayingFleshDropRate = 4;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        configuration.getInt(
            "Decaying Flesh drop rate",
            categoryItems,
            decayingFleshDropRate,
            0,
            999999,
            "Higher is less frequent. 0 is guaranteed.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
