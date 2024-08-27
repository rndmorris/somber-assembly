package dev.rndmorris.somberassembly.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.potions.SomberPotions;

public class ItemSomberResource extends Item {

    public static final String NAME = "ItemSomberResource";

    public static final int DECAYING_FLESH = 0;
    public static final int VARIANT_COUNT = 1;

    public final IIcon[] icons = new IIcon[VARIANT_COUNT];

    public ItemSomberResource() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setUnlocalizedName(NAME);

        GameRegistry.registerItem(this, NAME, SomberAssembly.MODID);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icons[DECAYING_FLESH] = ir.registerIcon("somberassembly:decaying_flesh");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        if (0 <= damage && damage < icons.length) {
            return icons[damage];
        }
        return null;
    }

    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + itemStack.getItemDamage();
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int gameTime, boolean p_77663_5_) {
        if (!worldIn.isRemote && entityIn instanceof EntityPlayer player) {
            var itemType = stack.getItemDamage();
            if (itemType == DECAYING_FLESH) {
                onUpdateDecayingFlesh(stack, player, gameTime);
            }
        }
        if (gameTime % 20 == 0) {
            SomberAssembly.LOG.info("Suspected gameTime: {}", gameTime);
        }
    }

    private void onUpdateDecayingFlesh(ItemStack stack, EntityPlayer player, int gameTime) {
        // Give the player the Stench of Death, or refresh it if needed
        final var effectDuration = 260;
        final var refreshInterval = 200;
        if (stack.stackSize < 1) {
            return;
        }
        final var effect = player.getActivePotionEffect(SomberPotions.deathMask);
        if (effect == null || (effect.getDuration() < (effectDuration - refreshInterval))) {
            var newEffect = new PotionEffect(SomberPotions.deathMask.id, effectDuration, 0, false);
            player.addPotionEffect(newEffect);
        }
    }
}
