package dev.rndmorris.somberassembly;

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
}
