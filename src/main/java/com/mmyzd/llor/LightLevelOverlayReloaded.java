package com.mmyzd.llor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.dimdev.rift.listener.client.KeybindHandler;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LightLevelOverlayReloaded implements KeybindHandler {

	public static LightLevelOverlayReloaded INSTANCE;

	public OverlayRenderer renderer;
	public OverlayPoller poller;
	public static ConfigManager CONFIG;
	public boolean active;

	public static final KeyBinding HOTKEY = new KeyBinding("Toggle Overlay",
		GLFW.GLFW_KEY_F4, "Light Level Overlay Reloaded");

	/**
	 * @see com.mmyzd.llor.mixin.MixinGuiKeyBindingList
	 */
	public static final KeyBinding HOTKEY_SHIFT = new KeyBinding("Light Calc",
		GLFW.GLFW_KEY_UNKNOWN, "Light Level Overlay Reloaded");
	public static final KeyBinding HOTKEY_CTRL = new KeyBinding("Display Mode",
		GLFW.GLFW_KEY_UNKNOWN, "Light Level Overlay Reloaded");

	public LightLevelOverlayReloaded() {
		INSTANCE = this;
	}

	private void launchPoller() {
		for (int i = 0; i < 3; i++) {
			try {
				Executors.newScheduledThreadPool(6).scheduleAtFixedRate(poller,
					1000, ConfigManager.pollingInterval, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				//e.printStackTrace();
				poller = new OverlayPoller();
			}
		}
	}

	@Override
	public void processKeybinds() {
		boolean withShift = GuiScreen.isShiftKeyDown();
		boolean withCtrl = GuiScreen.isCtrlKeyDown();
		boolean withAlt = GuiScreen.isAltKeyDown();
		if (HOTKEY.isPressed()) {
			if (active && withShift && !withCtrl) {
				boolean useSkyLight = !ConfigManager.useSkyLight;
				ConfigManager.useSkyLight = useSkyLight;
				sendMessageToPlayer((useSkyLight ? "Block Light + Sky Light" : "Block Light Only"));
			} else if (active && withCtrl && !withShift) {
				ConfigManager.DisplayMode mode = ConfigManager.displayMode;
				ConfigManager.displayMode = mode.getNext();
				sendMessageToPlayer(ConfigManager.displayMode.toString());
			} else if (!withShift && !withCtrl && !withAlt) {
				active = !active;
				launchPoller();
			}
		}
	}

	public void sendMessageToPlayer(String message) {
		Minecraft.getInstance().player.sendMessage(new TextComponentString(
			TextFormatting.GRAY + "[LightLevelOverlay] " +  message
		));
	}

	public void render() {
		if (!active) {
			return;
		}
		if (CONFIG == null) {
			File gameDir = Minecraft.getInstance().gameDir;
			CONFIG = new ConfigManager(new File(gameDir, "config"));
			renderer = new OverlayRenderer();
			poller = new OverlayPoller();
			active = true;
			launchPoller();
		}
		EntityPlayerSP player = Minecraft.getInstance().player;
		if (player != null) {
			final float partialTicks = getPartialTicks();
			double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			renderer.render(x, y, z, poller.getOverlays());
		}
	}

	public static float getPartialTicks() {
		try {
			return Minecraft.getInstance().getRenderPartialTicks();
		} catch (NullPointerException e) {
			// not ready
			return 0f;
		}
	}

}
