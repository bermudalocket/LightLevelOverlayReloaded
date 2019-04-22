package com.mmyzd.llor;

import net.minecraft.client.settings.KeyBinding;
import org.dimdev.rift.listener.client.KeyBindingAdder;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class MixinLoader implements InitializationListener, KeyBindingAdder {

    @Override
    public void onInitialization() {
        Mixins.addConfiguration("mixins.llor.json");
    }

    @Override
    public Collection<? extends KeyBinding> getKeyBindings() {
        return new HashSet<>(Arrays.asList(
            LightLevelOverlayReloaded.HOTKEY,
            LightLevelOverlayReloaded.HOTKEY_SHIFT,
            LightLevelOverlayReloaded.HOTKEY_CTRL,
            LightLevelOverlayReloaded.HOTKEY_ALT
        ));
    }

}
