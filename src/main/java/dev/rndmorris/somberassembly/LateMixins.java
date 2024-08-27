package dev.rndmorris.somberassembly;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import dev.rndmorris.somberassembly.util.CollectionUtil;

@LateMixin
public class LateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins." + SomberAssembly.MODID + ".late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return CollectionUtil.listOf("late.MixinScanManager");
    }
}
