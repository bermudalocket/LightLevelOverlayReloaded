package com.mmyzd.llor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiModConfig extends GuiScreen {

	private GuiButton USE_SKY_LIGHT_BUTTON;
	private GuiButton INCREASE_RENDER_DIST;
	private GuiButton DECREASE_RENDER_DIST;
	private GuiButton SHOW_RENDER_DIST;

	public static void open() throws IllegalAccessException, InstantiationException {
		Minecraft.getInstance().displayGuiScreen(GuiModConfig.class.newInstance());
	}

	@Override
	protected void initGui() {
		USE_SKY_LIGHT_BUTTON = addButton(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 100, ConfigManager.displayMode.toString()) {
			public void onClick(double x, double y) {
				ConfigManager.DisplayMode next = ConfigManager.displayMode.getNext();
				ConfigManager.displayMode = next;
				this.displayString = next.toString();
			}
		});
		INCREASE_RENDER_DIST = addButton(new GuiButton(1, this.width / 2 + 100, this.height/2-200, 50, 50, "+") {
			public void onClick(double x, double y) {
				int newDist = ConfigManager.blockRenderDistance++;
				if (newDist > 128) {
					newDist = 8;
				}
				SHOW_RENDER_DIST.displayString = String.valueOf(newDist);
				ConfigManager.blockRenderDistance = newDist;
			}
		});
		DECREASE_RENDER_DIST = addButton(new GuiButton(2, this.width / 2 - 100, this.height/2-200, 50, 50, "-") {
			public void onClick(double x, double y) {
				int newDist = ConfigManager.blockRenderDistance--;
				if (newDist < 8) {
					newDist = 8;
				}
				SHOW_RENDER_DIST.displayString = String.valueOf(newDist);
				ConfigManager.blockRenderDistance = newDist;
			}
		});
		SHOW_RENDER_DIST = addButton(new GuiButton(2, this.width / 2 - 50, this.height/2, "+") {
			public void render(int mouseX, int mouseY, float partialTicks) {
				this.displayString = String.valueOf(ConfigManager.blockRenderDistance);
				this.enabled = false;
			}
		});
	}

}