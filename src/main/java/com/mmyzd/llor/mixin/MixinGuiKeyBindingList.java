package com.mmyzd.llor.mixin;

import com.mmyzd.llor.LightLevelOverlayReloaded;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiKeyBindingList.KeyEntry.class)
public class MixinGuiKeyBindingList {

    @Shadow @Final private GuiButton btnChangeKeyBinding;

    @Shadow @Final private KeyBinding keybinding;

    @Inject(method = "drawEntry", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/GuiButton;render(IIF)V"))
    private void render(int entryWidth, int entryHeight, int mouseX, int mouseY, boolean p_194999_5_, float partialTicks, CallbackInfo ci) {
        if (this.keybinding == LightLevelOverlayReloaded.HOTKEY_SHIFT) {
            this.btnChangeKeyBinding.displayString = "Shift+" + LightLevelOverlayReloaded.HOTKEY.func_197978_k();
            this.btnChangeKeyBinding.enabled = false;
        } else if (this.keybinding == LightLevelOverlayReloaded.HOTKEY_CTRL) {
            this.btnChangeKeyBinding.displayString = "Ctrl+" + LightLevelOverlayReloaded.HOTKEY.func_197978_k();
            this.btnChangeKeyBinding.enabled = false;
        }
    }

}
