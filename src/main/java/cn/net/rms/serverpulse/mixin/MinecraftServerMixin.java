package cn.net.rms.serverpulse.mixin;

import cn.net.rms.serverpulse.PulseHttpServer;
import cn.net.rms.serverpulse.ServerPulse;
import cn.net.rms.serverpulse.TickMonitor;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private boolean serverPulse$httpStarted = false;

    @Unique
    private PulseHttpServer serverPulse$httpServer;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (!serverPulse$httpStarted) {
            serverPulse$httpStarted = true;
            serverPulse$httpServer = new PulseHttpServer();
            serverPulse$httpServer.start(ServerPulse.getPort());
        }
        TickMonitor.getInstance().onTickStart();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        TickMonitor.getInstance().onTickEnd();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        if (serverPulse$httpServer != null) {
            serverPulse$httpServer.stop();
            serverPulse$httpServer = null;
            serverPulse$httpStarted = false;
        }
    }
}
