package dev.rndmorris.somberassembly;

import net.minecraft.block.Block;

import thaumcraft.api.ItemApi;

public class Utils {

    /**
     * Wraps ItemApi.getBlock and returns the block itself, instead of the ItemStack.
     *
     * @param itemString The string name of the block.
     * @param meta       The meta id of the block.
     * @return The requested block, or null if not found.
     */
    public static Block getThaumcraftBlock(String itemString, int meta) {
        var itemStack = ItemApi.getBlock(itemString, meta);
        if (itemStack == null) {
            return null;
        }
        var item = itemStack.getItem();
        if (item == null) {
            return null;
        }
        return Block.getBlockFromItem(item);
    }

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
