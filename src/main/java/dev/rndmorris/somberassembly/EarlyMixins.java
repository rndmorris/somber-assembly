package dev.rndmorris.somberassembly;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import dev.rndmorris.somberassembly.util.CollectionUtil;

public class EarlyMixins implements IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins." + SomberAssembly.MODID + ".json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return CollectionUtil.listOf("MixinWorld");
    }
}
