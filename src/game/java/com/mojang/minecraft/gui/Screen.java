package com.mojang.minecraft.gui;

import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

import org.lwjgl.opengl.GL11;

public class Screen {

   protected float imgZ = 0.0F;

   protected static void drawBox(int var0, int var1, int var2, int var3, int var4) {
      float var5 = (float)(var4 >>> 24) / 255.0F;
      float var6 = (float)(var4 >> 16 & 255) / 255.0F;
      float var7 = (float)(var4 >> 8 & 255) / 255.0F;
      float var9 = (float)(var4 & 255) / 255.0F;
      Tessellator tess = Tessellator.getInstance();
      WorldRenderer var8 = tess.getWorldRenderer();
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(var6, var7, var9, var5);
      var8.begin(7, VertexFormat.POSITION);
      var8.pos((float)var0, (float)var3, 0.0F).endVertex();;
      var8.pos((float)var2, (float)var3, 0.0F).endVertex();;
      var8.pos((float)var2, (float)var1, 0.0F).endVertex();;
      var8.pos((float)var0, (float)var1, 0.0F).endVertex();;
      tess.draw();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
   }
   
   static Tessellator tessellator = Tessellator.getInstance();
   static WorldRenderer renderer = tessellator.getWorldRenderer();

   protected static void drawFadingBox(int var0, int var1, int var2, int var3, int var4, int var5) {
	  float var6 = (float)(var4 >>> 24) / 255.0F;
      float var7 = (float)(var4 >> 16 & 255) / 255.0F;
      float var8 = (float)(var4 >> 8 & 255) / 255.0F;
      float var12 = (float)(var4 & 255) / 255.0F;
      float var9 = (float)(var5 >>> 24) / 255.0F;
      float var10 = (float)(var5 >> 16 & 255) / 255.0F;
      float var11 = (float)(var5 >> 8 & 255) / 255.0F;
      float var13 = (float)(var5 & 255) / 255.0F;
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      renderer.begin(7, VertexFormat.POSITION_COLOR);
      renderer.pos((float)var2, (float)var1, 0.0f).color(var7, var8, var12, var6).endVertex();
      renderer.pos((float)var0, (float)var1, 0.0f).color(var7, var8, var12, var6).endVertex();
      renderer.pos((float)var0, (float)var3, 0.0f).color(var10, var11, var13, var9).endVertex();
      renderer.pos((float)var2, (float)var3, 0.0f).color(var10, var11, var13, var9).endVertex();
      tessellator.draw();
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public static void drawCenteredString(FontRenderer var0, String var1, int var2, int var3, int var4) {
      var0.render(var1, var2 - var0.getWidth(var1) / 2, var3, var4);
   }

   public static void drawString(FontRenderer var0, String var1, int var2, int var3, int var4) {
      var0.render(var1, var2, var3, var4);
   }

   public final void drawImage(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      Tessellator tess = Tessellator.getInstance();
      WorldRenderer var9 = tess.getWorldRenderer();
      var9.begin(7, VertexFormat.POSITION_TEX);
      var9.pos((float)var1, (float)(var2 + var6), this.imgZ).tex((float)var3 * var7, (float)(var4 + var6) * var8).endVertex();
      var9.pos((float)(var1 + var5), (float)(var2 + var6), this.imgZ).tex((float)(var3 + var5) * var7, (float)(var4 + var6) * var8).endVertex();
      var9.pos((float)(var1 + var5), (float)var2, this.imgZ).tex((float)(var3 + var5) * var7, (float)var4 * var8).endVertex();
      var9.pos((float)var1, (float)var2, this.imgZ).tex((float)var3 * var7, (float)var4 * var8).endVertex();
      tess.draw();
   }
}
