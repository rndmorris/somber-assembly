package dev.rndmorris.somberassembly.messages;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.messages.converters.PacketScannedToServerConverter;
import thaumcraft.common.lib.network.PacketHandler;

public class ThaumcraftEventSnooper
    implements IMessageHandler<thaumcraft.common.lib.network.playerdata.PacketScannedToServer, IMessage> {

    public static void init() {
        // chosen at random
//        final int DISCRIMINATOR = 991388426;
//        PacketHandler.INSTANCE.registerMessage(
//            new ThaumcraftEventSnooper(),
//            thaumcraft.common.lib.network.playerdata.PacketScannedToServer.class,
//            DISCRIMINATOR,
//            Side.SERVER);
    }

    @Override
    public IMessage onMessage(thaumcraft.common.lib.network.playerdata.PacketScannedToServer message,
        MessageContext ctx) {
        final var convertedMessage = PacketScannedToServerConverter.convert(message);
        final var event = buildEvent(convertedMessage);
        final var proxy = SomberAssembly.proxy;
        SomberAssembly.LOG.info("Scan message: {}", convertedMessage);
        if (event instanceof ItemScannedEvent itemScannedEvent) {
            SomberAssembly.LOG.info(
                "An item ({}) was scanned!",
                itemScannedEvent.scannedObject()
                    .getItem()
                    .getUnlocalizedName());
            proxy.itemScannedEventRegistrar()
                .announceEvent(itemScannedEvent);
        } else if (event instanceof EntityScannedEvent entityScannedEvent) {
            SomberAssembly.LOG.info(
                "An entity ({}) was scanned!",
                entityScannedEvent.scannedObject()
                    .getClass()
                    .getName());
            proxy.entityScannedEventRegistrar()
                .announceEvent(entityScannedEvent);
        }
        return message;
    }

    private IEvent buildEvent(PacketScannedToServer message) {
        final int TYPE_ITEM = 1;
        final int TYPE_ENTITY = 2;
        final int TYPE_PHENOMENA = 3; // ?

        final var world = DimensionManager.getWorld(message.dim);
        if (world == null) {
            return null;
        }
        final var player = world.getEntityByID(message.playerid);
        if (!(player instanceof EntityPlayer entityPlayer)) {
            return null;
        }
        return switch (message.type) {
            case TYPE_ITEM -> new ItemScannedEvent(
                entityPlayer,
                new ItemStack(Item.getItemById(message.id), 0, message.md));
            case TYPE_ENTITY -> {
                final var entity = world.getEntityByID(message.entityid);
                if (entity instanceof EntityItem entityItem) {
                    yield new ItemScannedEvent(entityPlayer, entityItem.getEntityItem());
                }
                yield new EntityScannedEvent(entityPlayer, entity);
            }
            default -> null;
        };
    }

}
