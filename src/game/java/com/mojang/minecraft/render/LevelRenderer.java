package com.mojang.minecraft.render;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.player.Player;

import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.opengl.GL11;

public final class LevelRenderer {

   public Level level;
   public TextureManager textureManager;
   public int listId;
   public IntBuffer buffer = GLAllocation.createDirectIntBuffer(65536);
   public List<Chunk> chunks = new ArrayList<Chunk>();
   private Chunk[] loadQueue;
   public Chunk[] chunkCache;
   private int xChunks;
   private int yChunks;
   private int zChunks;
   private int baseListId;
   public Minecraft minecraft;
   private int[] chunkDataCache = new int['\uc350'];
   public int ticks = 0;
   private float lastLoadX = -9999.0F;
   private float lastLoadY = -9999.0F;
   private float lastLoadZ = -9999.0F;
   public float cracks;


   public LevelRenderer(Minecraft var1, TextureManager var2) {
      this.minecraft = var1;
      this.textureManager = var2;
      this.listId = GL11.glGenLists(2);
      this.baseListId = GL11.glGenLists(4096 << 6 << 1);
   }

   public final void refresh() {
      int var1;
      if(this.chunkCache != null) {
         for(var1 = 0; var1 < this.chunkCache.length; ++var1) {
            this.chunkCache[var1].dispose();
         }
      }

      this.xChunks = this.level.width / 16;
      this.yChunks = this.level.depth / 16;
      this.zChunks = this.level.height / 16;
      this.chunkCache = new Chunk[this.xChunks * this.yChunks * this.zChunks];
      this.loadQueue = new Chunk[this.xChunks * this.yChunks * this.zChunks];
      var1 = 0;

      int var2;
      int var4;
      for(var2 = 0; var2 < this.xChunks; ++var2) {
         for(int var3 = 0; var3 < this.yChunks; ++var3) {
            for(var4 = 0; var4 < this.zChunks; ++var4) {
               this.chunkCache[(var4 * this.yChunks + var3) * this.xChunks + var2] = new Chunk(this.level, var2 << 4, var3 << 4, var4 << 4, 16, this.baseListId + var1);
               this.loadQueue[(var4 * this.yChunks + var3) * this.xChunks + var2] = this.chunkCache[(var4 * this.yChunks + var3) * this.xChunks + var2];
               var1 += 2;
            }
         }
      }

      for(var2 = 0; var2 < this.chunks.size(); ++var2) {
         ((Chunk)this.chunks.get(var2)).loaded = false;
      }

      this.chunks.clear();
      GL11.glNewList(this.listId, 4864);
      LevelRenderer var9 = this;
      float var10 = 0.5F;
      Tessellator tess = Tessellator.getInstance();
      WorldRenderer var11 = tess.getWorldRenderer();
      float var12 = this.level.getGroundLevel();
      int var5 = 128;
      if(128 > this.level.width) {
         var5 = this.level.width;
      }

      if(var5 > this.level.height) {
         var5 = this.level.height;
      }

      int var6 = 2048 / var5;
      var11.begin(7, VertexFormat.POSITION_TEX_COLOR);

      int var7;
      for(var7 = -var5 * var6; var7 < var9.level.width + var5 * var6; var7 += var5) {
         for(int var8 = -var5 * var6; var8 < var9.level.height + var5 * var6; var8 += var5) {
            var10 = var12;
            if(var7 >= 0 && var8 >= 0 && var7 < var9.level.width && var8 < var9.level.height) {
               var10 = 0.0F;
            }

            var11.pos((float)var7, var10, (float)(var8 + var5)).tex((float)var5, (float)var5).color(0.5F, var10, var10, 1.0F).endVertex();
            var11.pos((float)(var7 + var5), var10, (float)(var8 + var5)).tex((float)var5, (float)var5).color(0.5F, var10, var10, 1.0F).endVertex();
            var11.pos((float)(var7 + var5), var10, (float)var8).tex((float)var5, 0.0F).color(0.5F, var10, var10, 1.0F).endVertex();
            var11.pos((float)var7, var10, (float)var8).tex(0.0F, 0.0F).color(0.5F, var10, var10, 1.0F).endVertex();
         }
      }

      tess.draw();
      var11.begin(7, VertexFormat.POSITION_TEX_COLOR);

      for(var7 = 0; var7 < var9.level.width; var7 += var5) {
         var11.pos((float)var7, 0.0F, 0.0F).tex(0.0F, 0.0F).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();;
         var11.pos((float)(var7 + var5), 0.0F, 0.0F).tex((float)var5, 0.0F).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();;
         var11.pos((float)(var7 + var5), var12, 0.0F).tex((float)var5, var12).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();;
         var11.pos((float)var7, var12, 0.0F).tex(0.0F, var12).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();;
         var11.pos((float)var7, var12, (float)var9.level.height).tex(0.0F, var12).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();;
         var11.pos((float)(var7 + var5), var12, (float)var9.level.height).tex((float)var5, var12).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();;
         var11.pos((float)(var7 + var5), 0.0F, (float)var9.level.height).tex((float)var5, 0.0F).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();;
         var11.pos((float)var7, 0.0F, (float)var9.level.height).tex(0.0F, 0.0F).color(0.8F, 0.8F, 0.8F, 1.0f).endVertex();
      }

      for(var7 = 0; var7 < var9.level.height; var7 += var5) {
         var11.pos(0.0F, var12, (float)var7).tex(0.0F, 0.0F).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();
         var11.pos(0.0F, var12, (float)(var7 + var5)).tex((float)var5, 0.0F).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();
         var11.pos(0.0F, 0.0F, (float)(var7 + var5)).tex((float)var5, var12).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();;
         var11.pos(0.0F, 0.0F, (float)var7).tex(0.0F, var12).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();;
         var11.pos((float)var9.level.width, 0.0F, (float)var7).tex(0.0F, var12).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();
         var11.pos((float)var9.level.width, 0.0F, (float)(var7 + var5)).tex((float)var5, var12).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();;
         var11.pos((float)var9.level.width, var12, (float)(var7 + var5)).tex((float)var5, 0.0F).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();;
         var11.pos((float)var9.level.width, var12, (float)var7).tex(0.0F, 0.0F).color(0.6F, 0.6F, 0.6F, 1.0f).endVertex();;
      }

      tess.draw();
      GL11.glEndList();
      GL11.glNewList(this.listId + 1, 4864);
      var9 = this;
      var10 = this.level.getWaterLevel();
      GL11.glBlendFunc(770, 771);
      var4 = 128;
      if(128 > this.level.width) {
         var4 = this.level.width;
      }

      if(var4 > this.level.height) {
         var4 = this.level.height;
      }

      var5 = 2048 / var4;
      var11.begin(7, VertexFormat.POSITION_TEX_COLOR);

      for(var6 = -var4 * var5; var6 < var9.level.width + var4 * var5; var6 += var4) {
         for(var7 = -var4 * var5; var7 < var9.level.height + var4 * var5; var7 += var4) {
            float var13 = var10 - 0.1F;
            if(var6 < 0 || var7 < 0 || var6 >= var9.level.width || var7 >= var9.level.height) {
               var11.pos((float)var6, var13, (float)(var7 + var4)).tex(0.0F, (float)var4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
               var11.pos((float)(var6 + var4), var13, (float)(var7 + var4)).tex((float)var4, (float)var4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
               var11.pos((float)(var6 + var4), var13, (float)var7).tex((float)var4, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
               var11.pos((float)var6, var13, (float)var7).tex(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
               var11.pos((float)var6, var13, (float)var7).tex(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
               var11.pos((float)(var6 + var4), var13, (float)var7).tex((float)var4, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
               var11.pos((float)(var6 + var4), var13, (float)(var7 + var4)).tex((float)var4, (float)var4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
               var11.pos((float)var6, var13, (float)(var7 + var4)).tex(0.0F, (float)var4).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();;
            }
         }
      }

      tess.draw();
      GL11.glDisable(3042);
      GL11.glEndList();
      this.queueChunks(0, 0, 0, this.level.width, this.level.depth, this.level.height);
   }

   public final int sortChunks(Player var1, int var2) {
      float var3 = var1.x - this.lastLoadX;
      float var4 = var1.y - this.lastLoadY;
      float var5 = var1.z - this.lastLoadZ;
      if(var3 * var3 + var4 * var4 + var5 * var5 > 64.0F) {
         this.lastLoadX = var1.x;
         this.lastLoadY = var1.y;
         this.lastLoadZ = var1.z;
         Arrays.sort(this.loadQueue, new ChunkDistanceComparator(var1));
      }

      int var6 = 0;

      for(int var7 = 0; var7 < this.loadQueue.length; ++var7) {
         var6 = this.loadQueue[var7].appendLists(this.chunkDataCache, var6, var2);
      }

      this.buffer.clear();
      this.buffer.put(this.chunkDataCache, 0, var6);
      this.buffer.flip();
      if(this.buffer.remaining() > 0) {
    	 GL11.glBindTexture(3553, this.textureManager.load("/terrain.png"));
         GL11.glCallLists(this.buffer);
      }

      return this.buffer.remaining();
   }

   public final void queueChunks(int var1, int var2, int var3, int var4, int var5, int var6) {
      var1 /= 16;
      var2 /= 16;
      var3 /= 16;
      var4 /= 16;
      var5 /= 16;
      var6 /= 16;
      if(var1 < 0) {
         var1 = 0;
      }

      if(var2 < 0) {
         var2 = 0;
      }

      if(var3 < 0) {
         var3 = 0;
      }

      if(var4 > this.xChunks - 1) {
         var4 = this.xChunks - 1;
      }

      if(var5 > this.yChunks - 1) {
         var5 = this.yChunks - 1;
      }

      if(var6 > this.zChunks - 1) {
         var6 = this.zChunks - 1;
      }

      for(; var1 <= var4; ++var1) {
         for(int var7 = var2; var7 <= var5; ++var7) {
            for(int var8 = var3; var8 <= var6; ++var8) {
               Chunk var9;
               if(!(var9 = this.chunkCache[(var8 * this.yChunks + var7) * this.xChunks + var1]).loaded) {
                  var9.loaded = true;
                  this.chunks.add(this.chunkCache[(var8 * this.yChunks + var7) * this.xChunks + var1]);
               }
            }
         }
      }

   }
}
