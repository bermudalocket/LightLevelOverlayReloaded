package com.mmyzd.llor.mixin;

import com.mmyzd.llor.LightLevelOverlayReloaded;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE_STRING",
        target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
        args = { "ldc=hand" }))
    private void onRenderWorldLast(float partialTicks, long nanoTime, CallbackInfo ci) {
        if (LightLevelOverlayReloaded.INSTANCE != null) {
            LightLevelOverlayReloaded.INSTANCE.render();
        }
    }

}
