package dev.rndmorris.somberassembly.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.utils.Randoms;
import dev.rndmorris.somberassembly.common.potions.SomberPotion;
import thaumcraft.common.lib.utils.InventoryUtils;

public class ItemResource extends SomberItem {

    public static final String NAME = "item_resource";

    public static final int DECAYING_FLESH = 0;
    public static final int VARIANT_COUNT = 1;

    public final IIcon[] icons = new IIcon[VARIANT_COUNT];

    public ItemResource() {
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
    public IIcon getIconFromDamage(int damage) {
        if (0 <= damage && damage < icons.length) {
            return icons[damage];
        }
        return null;
    }

    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName() + "." + itemStack.getItemDamage();
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int p_77663_4_, boolean p_77663_5_) {
        if (!worldIn.isRemote && entityIn instanceof EntityPlayer player) {
            var itemType = stack.getItemDamage();
            if (itemType == DECAYING_FLESH) {
                onUpdateDecayingFlesh(stack, player);
            }
        }
    }

    private void onUpdateDecayingFlesh(ItemStack stack, EntityPlayer player) {
        // Give the player the Stench of Death, or refresh it if needed
        final var effectDuration = 260;
        final var refreshInterval = 200;
        final var decayChance = 50;
        if (stack.stackSize < 1) {
            return;
        }
        final var effect = player.getActivePotionEffect(SomberPotion.deathStench);
        if (effect == null || (effect.getDuration() < (effectDuration - refreshInterval))) {
            var newEffect = new PotionEffect(SomberPotion.deathStench.id, effectDuration, 0, false);
            player.addPotionEffect(newEffect);
            if (!player.capabilities.isCreativeMode && Randoms.nextPercentile(player.worldObj.rand) <= decayChance) {
                // Why reinvent the wheel if we know this is going to be here anyway?
                InventoryUtils.consumeInventoryItem(player, stack.getItem(), stack.getItemDamage());
                player.addChatMessage(new ChatComponentTranslation("msg.somberassembly.decaying_flesh_rotted"));
            }
        }
    }
}
