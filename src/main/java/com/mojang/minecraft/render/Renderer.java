package com.mojang.minecraft.render;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.model.Vec3D;
import com.mojang.minecraft.player.Player;
import com.mojang.util.MathHelper;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;

import net.lax1dude.eaglercraft.Random;
import org.lwjgl.opengl.GL11;

public final class Renderer {

   public Minecraft minecraft;
   public float fogColorMultiplier = 1.0F;
   public boolean displayActive = false;
   public float fogEnd = 0.0F;
   public HeldBlock heldBlock;
   public int levelTicks;
   public Entity entity = null;
   public Random random = new Random();
   public float fogRed;
   public float fogBlue;
   public float fogGreen;
   
   private final FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
   
   public Renderer(Minecraft var1) {
      this.minecraft = var1;
      this.heldBlock = new HeldBlock(var1);
   }

   public Vec3D getPlayerVector(float var1) {
      Player var4;
      float var2 = (var4 = this.minecraft.player).xo + (var4.x - var4.xo) * var1;
      float var3 = var4.yo + (var4.y - var4.yo) * var1;
      float var5 = var4.zo + (var4.z - var4.zo) * var1;
      return new Vec3D(var2, var3, var5);
   }

   public void hurtEffect(float var1) {
      Player var3;
      float var2 = (float)(var3 = this.minecraft.player).hurtTime - var1;
      if(var3.health <= 0) {
         var1 += (float)var3.deathTime;
         GL11.glRotatef(40.0F - 8000.0F / (var1 + 200.0F), 0.0F, 0.0F, 1.0F);
      }

      if(var2 >= 0.0F) {
         var2 = MathHelper.sin((var2 /= (float)var3.hurtDuration) * var2 * var2 * var2 * 3.1415927F);
         var1 = var3.hurtDir;
         GL11.glRotatef(-var3.hurtDir, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-var2 * 14.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(var1, 0.0F, 1.0F, 0.0F);
      }
   }

   public void applyBobbing(float var1) {
      Player var4;
      float var2 = (var4 = this.minecraft.player).walkDist - var4.walkDistO;
      var2 = var4.walkDist + var2 * var1;
      float var3 = var4.oBob + (var4.bob - var4.oBob) * var1;
      float var5 = var4.oTilt + (var4.tilt - var4.oTilt) * var1;
      GL11.glTranslatef(MathHelper.sin(var2 * 3.1415927F) * var3 * 0.5F, -Math.abs(MathHelper.cos(var2 * 3.1415927F) * var3), 0.0F);
      GL11.glRotatef(MathHelper.sin(var2 * 3.1415927F) * var3 * 3.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(Math.abs(MathHelper.cos(var2 * 3.1415927F + 0.2F) * var3) * 5.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(var5, 1.0F, 0.0F, 0.0F);
   }

   public final void setLighting(boolean var1) {
      if(!var1) {
    	  disableStandardItemLighting();
      } else {
    	  GlStateManager.pushMatrix();
  		GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
  		GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
    	  enableStandardItemLighting();
    	  GlStateManager.popMatrix();
      }
   }
   
   private static final Vec3D LIGHT0_POS = (new Vec3D(0.20000000298023224F, 1.0F, -0.699999988079071F)).normalize();
	private static final Vec3D LIGHT1_POS = (new Vec3D(-0.20000000298023224F, 1.0F, 0.699999988079071F)).normalize();
   
   public static void disableStandardItemLighting() {
		GlStateManager.disableLighting();
		GlStateManager.disableMCLight(0);
		GlStateManager.disableMCLight(1);
		GlStateManager.disableColorMaterial();
	}
   
   public static void enableStandardItemLighting() {
		GlStateManager.enableLighting();
		GlStateManager.enableMCLight(0, 0.6f, -LIGHT0_POS.x, -LIGHT0_POS.y, -LIGHT0_POS.z, 0.0D);
		GlStateManager.enableMCLight(1, 0.6f, -LIGHT1_POS.x, -LIGHT1_POS.y, -LIGHT1_POS.z, 0.0D);
		GlStateManager.setMCLightAmbient(0.4f, 0.4f, 0.4f);
		GlStateManager.enableColorMaterial();
	}

   public final void enableGuiMode() {
      int var1 = this.minecraft.width * 240 / this.minecraft.height;
      int var2 = this.minecraft.height * 240 / this.minecraft.height;
      GL11.glClear(256);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, (double)var1, (double)var2, 0.0D, 100.0D, 300.0D);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -200.0F);
   }

   public void updateFog() {
      Level var1 = this.minecraft.level;
      Player var2 = this.minecraft.player;
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      EaglercraftGPU.glFog(2918, this.setFogColorBuffer(this.fogRed, this.fogGreen, this.fogBlue, 1.0F));
      Block var5;
      if((var5 = Block.blocks[var1.getTile((int)var2.x, (int)(var2.y + 0.12F), (int)var2.z)]) != null && var5.getLiquidType() != LiquidType.NOT_LIQUID) {
         LiquidType var6 = var5.getLiquidType();
         GL11.glFogi(2917, 2048);
         if(var6 == LiquidType.WATER) {
            GL11.setFogDensity(0.1f);
         } else if(var6 == LiquidType.LAVA) {
            GL11.setFogDensity(2.0f);
         }
      } else {
         GL11.setFogStart(0.0f);
         GL11.setFogEnd(this.fogEnd);
      }

      GL11.enableColorMaterial();
      GL11.enableFog();
   }
   
   private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}
}
