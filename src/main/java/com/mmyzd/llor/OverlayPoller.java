package com.mmyzd.llor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashSet;

public class OverlayPoller extends Thread {
	
	private volatile ArrayList<Overlay>[][] overlays;

	public void run() {
		if (!LightLevelOverlayReloaded.INSTANCE.active) {
			return;
		}
		int radius = 0;
		int chunkRadius = updateChunkRadius();
		radius = radius % chunkRadius + 1;
		updateLightLevel(radius, chunkRadius);
	}

	public ArrayList<Overlay>[][] getOverlays() {
		if (overlays == null) {
			updateChunkRadius();
		}
		return overlays;
	}

	@SuppressWarnings("unchecked")
	private int updateChunkRadius() {
		int size = ConfigManager.chunkRadius + 1;
		if (overlays == null || overlays.length != size * 2 + 1) {
			overlays = new ArrayList[size * 2 + 1][size * 2 + 1];
			for (int i = 0; i < overlays.length; i++) {
				for (int j = 0; j < overlays[i].length; j++) {
					overlays[i][j] = new ArrayList<>();
				}
			}
		}
		return size;
	}

	private HashSet<Chunk> getChunksAroundPlayer(int radius) {
		Minecraft mc = Minecraft.getInstance();
		int chunkX = mc.player.chunkCoordX;
		int chunkZ = mc.player.chunkCoordZ;
		HashSet<Chunk> chunks = new HashSet<>();
		for (int x = chunkX - radius; x <= chunkX + radius; x++) {
			for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
				if (mc.world.isChunkLoaded(x, z, false)) {
					chunks.add(mc.world.getChunk(x, z));
				}
			}
		}
		return chunks;
	}

	private void updateLightLevel(int radius, int chunkRadius) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return;
		}
		WorldClient world = mc.world;
		int playerPosY = (int) Math.floor(mc.player.posY);
		int skyLightSub = world.calculateSkylightSubtracted(1.0f);
		ConfigManager.DisplayMode displayMode = ConfigManager.displayMode;
		boolean useSkyLight = ConfigManager.useSkyLight;

		for (Chunk chunk : getChunksAroundPlayer(radius)) {
			ArrayList<Overlay> buffer = new ArrayList<>();
			for (int offsetX = 0; offsetX < 16; offsetX++) {
				for (int offsetZ = 0; offsetZ < 16; offsetZ++) {
					int posX = (chunk.x << 4) + offsetX;
					int posZ = (chunk.z << 4) + offsetZ;
					int maxY = playerPosY + 4;
					int minY = Math.max(playerPosY - 40, 0);
					IBlockState curBlockState = chunk.getBlockState(offsetX, maxY, offsetZ);
					Block curBlock = curBlockState.getBlock();
					BlockPos curPos = new BlockPos(posX, maxY, posZ);
					for (int posY = maxY - 1; posY >= minY; posY--) {
						IBlockState preBlockState = curBlockState;
						curBlockState = chunk.getBlockState(offsetX, posY, offsetZ);
						Block preBlock = curBlock;
						curBlock = curBlockState.getBlock();
						BlockPos prePos = curPos;
						curPos = new BlockPos(posX, posY, posZ);
						if (curBlock == Blocks.AIR || curBlock == Blocks.BEDROCK || curBlock == Blocks.BARRIER ||
							preBlock instanceof BlockFence || preBlock instanceof BlockFenceGate ||
							preBlockState.isBlockNormalCube() ||
							preBlockState.getMaterial().isLiquid() ||
							preBlockState.canProvidePower() ||
							!curBlockState.isTopSolid() ||
							BlockRailBase.isRail(preBlockState)) {
							continue;
						}
						double offsetY = 0;
						if (preBlock == Blocks.SNOW || preBlock.getNameTextComponent().getUnformattedComponentText().contains("CARPET")) {
							offsetY = preBlockState.getShape(world, prePos).getBoundingBox().maxY;
							if (offsetY >= 0.15) {
								continue; // Snow layer too high
							}
						}
						int blockLight = chunk.getLightFor(EnumLightType.BLOCK, prePos);
						int skyLight = chunk.getLightFor(EnumLightType.SKY, prePos) - skyLightSub;
						int mixedLight = Math.max(blockLight, skyLight);
						int lightIndex = useSkyLight ? mixedLight : blockLight;
						if (displayMode == ConfigManager.DisplayMode.ADVANCED) {
							if (mixedLight >= 8 && blockLight < 8) {
								lightIndex += 32;
							}
						} else if (displayMode == ConfigManager.DisplayMode.MINIMAL) {
							if (blockLight >= 8) continue;
							if (lightIndex >= 8) lightIndex += 32;
						}
						if (lightIndex >= 8 && lightIndex < 24) {
							lightIndex ^= 16;
						}
						buffer.add(new Overlay(posX, posY + offsetY + 1, posZ, lightIndex));
					}
				}
			}
			int len = chunkRadius * 2 + 1;
			int arrayX = (chunk.x % len + len) % len;
			int arrayZ = (chunk.z % len + len) % len;
			overlays[arrayX][arrayZ] = buffer;
		}
	}

}
