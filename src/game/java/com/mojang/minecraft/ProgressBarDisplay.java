package com.mojang.minecraft;

import net.lax1dude.eaglercraft.Display;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

import org.lwjgl.opengl.GL11;

public final class ProgressBarDisplay {

	private String text = "";
	private Minecraft minecraft;
	private String title = "";
	private long start = EagRuntime.steadyTimeMillis();

	public ProgressBarDisplay(Minecraft var1) {
		this.minecraft = var1;
	}

	public final void setTitle(String var1) {
		if (!this.minecraft.running) {
			throw new StopGameException();
		} else {
			this.title = var1;
			int var3 = this.minecraft.width * 240 / this.minecraft.height;
			int var2 = this.minecraft.height * 240 / this.minecraft.height;
			GL11.glClear(256);
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, (double) var3, (double) var2, 0.0D, 100.0D, 300.0D);
			GL11.glMatrixMode(5888);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		}
	}

	public final void setText(String var1) {
		if (!this.minecraft.running) {
			throw new StopGameException();
		} else {
			this.text = var1;
			this.setProgress(-1);
		}
	}

	public final void setProgress(int var1) {
		if (!this.minecraft.running) {
			throw new StopGameException();
		} else {
			long var2;
			if ((var2 = EagRuntime.steadyTimeMillis()) - this.start < 0L || var2 - this.start >= 20L) {
				this.start = var2;
				int var4 = this.minecraft.width * 240 / this.minecraft.height;
				int var5 = this.minecraft.height * 240 / this.minecraft.height;
				GL11.glClear(16640);
				Tessellator tess = Tessellator.getInstance();
				WorldRenderer var6 = tess.getWorldRenderer();
				int var7 = this.minecraft.textureManager.load("/dirt.png");
				GL11.glBindTexture(3553, var7);
				float var10 = 32.0F;
				var6.begin(7, VertexFormat.POSITION_TEX_COLOR);
				var6.pos(0.0F, (float) var5, 0.0F).tex(0.0F, (float) var5 / var10).color(4210752).endVertex();
				var6.pos((float) var4, (float) var5, 0.0F).tex((float) var4 / var10, (float) var5 / var10)
						.color(4210752).endVertex();
				var6.pos((float) var4, 0.0F, 0.0F).tex((float) var4 / var10, 0.0F).color(4210752).endVertex();
				var6.pos(0.0F, 0.0F, 0.0F).tex(0.0F, 0.0F).color(4210752).endVertex();
				tess.draw();
				if (var1 >= 0) {
					var7 = var4 / 2 - 50;
					int var8 = var5 / 2 + 16;
					GL11.glDisable(3553);
					var6.begin(7, VertexFormat.POSITION_COLOR);
					var6.pos((float) var7, (float) var8, 0.0F).color(8421504).endVertex();
					var6.pos((float) var7, (float) (var8 + 2), 0.0F).color(8421504).endVertex();
					var6.pos((float) (var7 + 100), (float) (var8 + 2), 0.0F).color(8421504).endVertex();
					var6.pos((float) (var7 + 100), (float) var8, 0.0F).color(8421504).endVertex();
					var6.pos((float) var7, (float) var8, 0.0F).color(8454016).endVertex();
					var6.pos((float) var7, (float) (var8 + 2), 0.0F).color(8454016).endVertex();
					var6.pos((float) (var7 + var1), (float) (var8 + 2), 0.0F).color(8454016).endVertex();
					var6.pos((float) (var7 + var1), (float) var8, 0.0F).color(8454016).endVertex();
					tess.draw();
					GL11.glEnable(3553);
				}

				this.minecraft.fontRenderer.drawStringWithShadow(this.title, (var4 - this.minecraft.fontRenderer.getStringWidth(this.title)) / 2, var5 / 2 - 4 - 16, 16777215);
				this.minecraft.fontRenderer.drawStringWithShadow(this.text,
						(var4 - this.minecraft.fontRenderer.getStringWidth(this.text)) / 2, var5 / 2 - 4 + 8, 16777215);
				Display.update();
			}
		}
	}
}
