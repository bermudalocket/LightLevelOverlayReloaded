package com.mmyzd.llor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class OverlayRenderer {

	private static final Tessellator TESSELLATOR = Tessellator.getInstance();

	private static final BufferBuilder BUILDER = TESSELLATOR.getBuffer();

	private final ResourceLocation TEXTURE_MAP;
	private double[] texureMinX, texureMaxX;
	private double[] texureMinY, texureMaxY;
	
	public OverlayRenderer() {
		TEXTURE_MAP = new ResourceLocation("llor", "textures/overlay.png");
		texureMinX = new double[64];
		texureMaxX = new double[64];
		texureMinY = new double[64];
		texureMaxY = new double[64];
		for (int i = 0; i < 64; i++) {
			texureMinX[i] = (i % 8) / 8.0;
			texureMaxX[i] = (i % 8 + 1) / 8.0;
			texureMinY[i] = (i >> 3) / 8.0;
			texureMaxY[i] = ((i >> 3) + 1) / 8.0;
		}
	}

	public void render(double x, double y, double z, final ArrayList<Overlay>[][] overlays) {
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE_MAP);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_DST_COLOR, GL11.GL_ZERO);
		GlStateManager.translated(-x, -y, -z);
		BUILDER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (ArrayList<Overlay>[] overlay : overlays) {
			for (ArrayList<Overlay> overlayArrayList : overlay) {
				for (Overlay u : overlayArrayList) {
					if ((x-u.x)*(x-u.x) + (y-u.y)*(y-u.y) + (z-u.z)*(z-u.z) > ConfigManager.blockRenderDistance*ConfigManager.blockRenderDistance) {
						continue;
					}
					BUILDER.pos(u.x, u.y, u.z).tex(texureMinX[u.index], texureMinY[u.index]).endVertex();
					BUILDER.pos(u.x, u.y, u.z + 1).tex(texureMinX[u.index], texureMaxY[u.index]).endVertex();
					BUILDER.pos(u.x + 1, u.y, u.z + 1).tex(texureMaxX[u.index], texureMaxY[u.index]).endVertex();
					BUILDER.pos(u.x + 1, u.y, u.z).tex(texureMaxX[u.index], texureMinY[u.index]).endVertex();
				}
			}
		}
		Tessellator.getInstance().draw();
		GlStateManager.popMatrix();
	}

}
