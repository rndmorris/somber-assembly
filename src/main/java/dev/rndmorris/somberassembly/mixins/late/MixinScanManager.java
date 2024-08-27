package dev.rndmorris.somberassembly.mixins.late;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.data.events.SuccessfulScanEvent;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.lib.research.ScanManager;

@Mixin(value = ScanManager.class)
public abstract class MixinScanManager {

    @Inject(method = "completeScan", at = @At(value = "RETURN", remap = false), remap = false)
    private static void onCompleteScan(EntityPlayer player, ScanResult scan, String prefix,
        CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            SomberAssembly.LOG.info("{} scanned: {}!", player.getCommandSenderName(), scan.type);
            SomberAssembly.proxy.getScanEventManager()
                .announceEvent(new SuccessfulScanEvent(player, scan));
        }
    }
}
