package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.phys.AABB;
import com.mojang.util.MathHelper;

import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

import net.lax1dude.eaglercraft.Random;

public class FlowerBlock extends Block {

	protected FlowerBlock(int var1, int var2) {
		super(var1);
		this.textureId = var2;
		this.setPhysics(true);
		float var3 = 0.2F;
		this.setBounds(0.5F - var3, 0.0F, 0.5F - var3, var3 + 0.5F, var3 * 3.0F, var3 + 0.5F);
	}

	public void update(Level level, int x, int y, int z, Random rand) {
		if (!level.growTrees) {
			int var6 = level.getTile(x, y - 1, z);
			if (!level.isLit(x, y, z) || var6 != Block.DIRT.id && var6 != Block.GRASS.id) {
				level.setTile(x, y, z, 0);
			}

		}
	}

	private void render(WorldRenderer var1, float var2, float var3, float var4) {
		int var15;
		int var5 = (var15 = this.getTextureId(15)) % 16 << 4;
		int var6 = var15 / 16 << 4;
		float var16 = (float) var5 / 256.0F;
		float var17 = ((float) var5 + 15.99F) / 256.0F;
		float var7 = (float) var6 / 256.0F;
		float var18 = ((float) var6 + 15.99F) / 256.0F;

		for (int var8 = 0; var8 < 2; ++var8) {
			float var9 = (float) ((double) MathHelper.sin((float) var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
			float var10 = (float) ((double) MathHelper.cos((float) var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
			float var11 = var2 + 0.5F - var9;
			var9 += var2 + 0.5F;
			float var13 = var3 + 1.0F;
			float var14 = var4 + 0.5F - var10;
			var10 += var4 + 0.5F;
			var1.pos(var11, var13, var14).tex(var17, var7).endVertex();
			var1.pos(var9, var13, var10).tex(var16, var7).endVertex();
			var1.pos(var9, var3, var10).tex(var16, var18).endVertex();
			var1.pos(var11, var3, var14).tex(var17, var18).endVertex();
			var1.pos(var9, var13, var10).tex(var17, var7).endVertex();
			var1.pos(var11, var13, var14).tex(var16, var7).endVertex();
			var1.pos(var11, var3, var14).tex(var16, var18).endVertex();
			var1.pos(var9, var3, var10).tex(var17, var18).endVertex();
		}

	}

	private void renderWithColors(WorldRenderer var1, float var2, float var3, float var4, float r, float g, float b,
			float a) {
		int var15;
		int var5 = (var15 = this.getTextureId(15)) % 16 << 4;
		int var6 = var15 / 16 << 4;
		float var16 = (float) var5 / 256.0F;
		float var17 = ((float) var5 + 15.99F) / 256.0F;
		float var7 = (float) var6 / 256.0F;
		float var18 = ((float) var6 + 15.99F) / 256.0F;

		for (int var8 = 0; var8 < 2; ++var8) {
			float var9 = (float) ((double) MathHelper.sin((float) var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
			float var10 = (float) ((double) MathHelper.cos((float) var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
			float var11 = var2 + 0.5F - var9;
			var9 += var2 + 0.5F;
			float var13 = var3 + 1.0F;
			float var14 = var4 + 0.5F - var10;
			var10 += var4 + 0.5F;
			var1.pos(var11, var13, var14).tex(var17, var7).color(r, g, b, a).endVertex();
			var1.pos(var9, var13, var10).tex(var16, var7).color(r, g, b, a).endVertex();
			var1.pos(var9, var3, var10).tex(var16, var18).color(r, g, b, a).endVertex();
			var1.pos(var11, var3, var14).tex(var17, var18).color(r, g, b, a).endVertex();
			var1.pos(var9, var13, var10).tex(var17, var7).color(r, g, b, a).endVertex();
			var1.pos(var11, var13, var14).tex(var16, var7).color(r, g, b, a).endVertex();
			var1.pos(var11, var3, var14).tex(var16, var18).color(r, g, b, a).endVertex();
			var1.pos(var9, var3, var10).tex(var17, var18).color(r, g, b, a).endVertex();
		}

	}

	private void renderWithNormals(WorldRenderer var1, float var2, float var3, float var4, float normalx, float normaly,
			float normalz) {
		int var15;
		int var5 = (var15 = this.getTextureId(15)) % 16 << 4;
		int var6 = var15 / 16 << 4;
		float var16 = (float) var5 / 256.0F;
		float var17 = ((float) var5 + 15.99F) / 256.0F;
		float var7 = (float) var6 / 256.0F;
		float var18 = ((float) var6 + 15.99F) / 256.0F;

		for (int var8 = 0; var8 < 2; ++var8) {
			float var9 = (float) ((double) MathHelper.sin((float) var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
			float var10 = (float) ((double) MathHelper.cos((float) var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
			float var11 = var2 + 0.5F - var9;
			var9 += var2 + 0.5F;
			float var13 = var3 + 1.0F;
			float var14 = var4 + 0.5F - var10;
			var10 += var4 + 0.5F;
			var1.pos(var11, var13, var14).tex(var17, var7).normal(normalx, normaly, normalz).endVertex();
			var1.pos(var9, var13, var10).tex(var16, var7).normal(normalx, normaly, normalz).endVertex();
			var1.pos(var9, var3, var10).tex(var16, var18).normal(normalx, normaly, normalz).endVertex();
			var1.pos(var11, var3, var14).tex(var17, var18).normal(normalx, normaly, normalz).endVertex();
			var1.pos(var9, var13, var10).tex(var17, var7).normal(normalx, normaly, normalz).endVertex();
			var1.pos(var11, var13, var14).tex(var16, var7).normal(normalx, normaly, normalz).endVertex();
			var1.pos(var11, var3, var14).tex(var16, var18).normal(normalx, normaly, normalz).endVertex();
			var1.pos(var9, var3, var10).tex(var17, var18).normal(normalx, normaly, normalz).endVertex();
		}

	}

	public final boolean isOpaque() {
		return false;
	}

	public final boolean isSolid() {
		return false;
	}

	public final void renderPreview(WorldRenderer var1, Tessellator tess) {
		var1.begin(7, VertexFormat.POSITION_TEX_NORMAL);
		this.renderWithNormals(var1, 0.0F, 0.4F, -0.3F, 0.0F, 1.0F, 0.0F);
		tess.draw();
	}

	public final boolean isCube() {
		return false;
	}

	public final boolean render(Level var1, int var2, int var3, int var4, WorldRenderer var5) {
		float var6 = var1.getBrightness(var2, var3, var4);
		this.renderWithColors(var5, (float) var2, (float) var3, (float) var4, var6, var6, var6, 1.0f);
		return true;
	}

	public final void renderFullbright(WorldRenderer shapeRenderer) {
		shapeRenderer.color(1.0F, 1.0F, 1.0F, 1.0f);
		this.render(shapeRenderer, (float) -2, 0.0F, 0.0F);
	}

	@Override
	public AABB getCollisionBox(int x, int y, int z) {
		return null;
	}
}
