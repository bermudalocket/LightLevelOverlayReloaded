package com.mmyzd.llor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiModConfig extends GuiScreen {

	private GuiButton USE_SKY_LIGHT_BUTTON;

	public GuiModConfig() {

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

	}

}