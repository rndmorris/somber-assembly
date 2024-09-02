package dev.rndmorris.somberassembly.common.items;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import thaumcraft.api.ItemApi;

public class ItemHelper {

    public static class Thaumcraft {

        public static ItemStack inkwell() {
            return ItemApi.getItem("itemInkwell", 0);
        }

        public static ItemStack inkwell(int damageValue) {
            return ItemApi.getItem("itemInkwell", MathHelper.clamp_int(damageValue, 0, 100));
        }
    }
}
