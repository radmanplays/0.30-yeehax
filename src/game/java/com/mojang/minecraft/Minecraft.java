package com.mojang.minecraft;

import com.mojang.minecraft.gamemode.CreativeGameMode;
import com.mojang.minecraft.gamemode.GameMode;
import com.mojang.minecraft.gamemode.SurvivalGameMode;
import com.mojang.minecraft.gui.*;
import com.mojang.minecraft.item.Arrow;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelIO;
import com.mojang.minecraft.level.generator.LevelGenerator;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.ModelManager;
import com.mojang.minecraft.model.ModelPart;
import com.mojang.minecraft.model.Vec3D;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.NetworkPlayer;
import com.mojang.minecraft.net.PacketType;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleManager;
import com.mojang.minecraft.particle.WaterDropParticle;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.player.InputHandlerImpl;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.*;
import com.mojang.minecraft.render.texture.TextureFX;
import com.mojang.minecraft.render.texture.TextureLavaFX;
import com.mojang.minecraft.render.texture.TextureWaterFX;
import com.mojang.minecraft.sound.SoundManager;
import com.mojang.minecraft.sound.SoundPlayer;
import com.mojang.util.MathHelper;
import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.peyton.eagler.level.LevelStorageManager;
import net.peyton.eagler.level.LevelUtils;
import yeehax.YeeHax;
import net.lax1dude.eaglercraft.Display;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EagUtils;
import net.lax1dude.eaglercraft.EaglerInputStream;
import net.lax1dude.eaglercraft.EaglerOutputStream;

import org.lwjgl.opengl.GL11;

import java.io.*;

import net.lax1dude.eaglercraft.internal.PlatformOpenGL;
import java.util.Collections;
import java.util.List;

public final class Minecraft implements Runnable {

   public GameMode gamemode = new SurvivalGameMode(this);
   public int width;
   public int height;
   public float dpi;
   private Timer timer = new Timer(20.0F);
   public Level level;
   public LevelRenderer levelRenderer;
   public Player player;
   public ParticleManager particleManager;
   public SessionData session = null;
   public String host;
   public boolean levelLoaded = false;
   public volatile boolean waiting = false;
   public TextureManager textureManager;
   public FontRenderer fontRenderer;
   public GuiScreen currentScreen = null;
   public ProgressBarDisplay progressBar = new ProgressBarDisplay(this);
   public Renderer renderer = new Renderer(this);
   public SoundManager sound;
   private int ticks;
   private int blockHitTime;
   public String levelName;
   public int levelId;
   public HUDScreen hud;
   public boolean online;
   public NetworkManager networkManager;
   public SoundPlayer soundPlayer;
   public MovingObjectPosition selected;
   public GameSettings settings;
   String server;
   public volatile boolean running;
   public String debug;
   public boolean hasMouse;
   private int lastClick;
   public boolean raining;
   private boolean enableGLErrorChecking = false;
   private static Minecraft mc;
   public boolean mpRestart = false;
   
   public Minecraft(int var3, int var4, boolean var5) {
      this.sound = new SoundManager();
      this.ticks = 0;
      this.blockHitTime = 0;
      this.levelName = null;
      this.levelId = 0;
      this.online = false;
      //new HumanoidModel(0.0F); ???
      this.selected = null;
      this.running = false;
      this.debug = "";
      this.hasMouse = false;
      this.lastClick = 0;
      this.raining = false;

      this.width = var3;
      this.height = var4;
      
      this.enableGLErrorChecking = EagRuntime.getConfiguration().isCheckGLErrors();
      mc = this;
      
      try {
    	  LevelStorageManager.loadLevelData();
      } catch (IOException e) {
    	  e.printStackTrace();
      }
   }

   public final void setCurrentScreen(GuiScreen var1) {
      if(!(this.currentScreen instanceof ErrorScreen)) {
         if(this.currentScreen != null) {
            this.currentScreen.onClose();
         }

         if(var1 == null && this.player.health <= 0) {
            var1 = new GameOverScreen();
         }

         this.currentScreen = (GuiScreen)var1;
         if(var1 != null) {
            if(this.hasMouse) {
               this.player.releaseAllKeys();
               this.hasMouse = false;
               if(!this.levelLoaded) {
                  Mouse.setGrabbed(false);
               }
            }

            int var2 = this.width * 240 / this.height;
            int var3 = this.height * 240 / this.height;
            ((GuiScreen)var1).open(this, var2, var3);
            this.online = false;
         } else {
            this.grabMouse();
         }
      }
      ScaledResolution scaledresolution = new ScaledResolution(this);
      EagRuntime.getConfiguration().getHooks().callScreenChangedHook(currentScreen != null ? currentScreen.getClass().getName() : null, scaledresolution.getScaledWidth(),
				scaledresolution.getScaledHeight(), width, height, scaledresolution.getScaleFactor());
   }

   private void checkGLError(String var0) {
	  if(this.enableGLErrorChecking) {
		  int i = EaglercraftGPU.glGetError();

		  if (i != 0) {
			  String s = EaglercraftGPU.gluErrorString(i);
			  System.out.println("########## GL ERROR ##########");
			  System.out.println("@ " + var0);
			  System.out.println(i + ": " + s);
		  }
	  }
   }
   
   public final void shutdown() {
	   EagRuntime.destroy();
	   EagRuntime.exit();
   }

   public final void run() {
      this.running = true;

      Display.setTitle("Minecraft 0.30");

      try {
    	  Display.create();
      } catch (Exception var57) {
    	  var57.printStackTrace();
    	  EagUtils.sleep(1000L);
    	  Display.create();
      }

      checkGLError("Pre startup");
      GL11.glEnable(3553);
      GL11.glShadeModel(7425);
      GL11.glClearDepth(1.0D);
      GL11.glEnable(2929);
      GL11.glDepthFunc(515);
      GL11.glEnable(3008);
      GL11.glAlphaFunc(516, 0.0F);
      GL11.glCullFace(1029);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(5888);
      checkGLError("Startup");
         
      this.settings = new GameSettings(this);
      this.textureManager = new TextureManager(this.settings);
      this.textureManager.registerAnimation(new TextureLavaFX());
      this.textureManager.registerAnimation(new TextureWaterFX());
      this.fontRenderer = new FontRenderer(this.settings, "/default.png", this.textureManager);
      this.levelRenderer = new LevelRenderer(this, this.textureManager);
      Item.initModels();
      Mob.modelCache = new ModelManager();
      GL11.glViewport(0, 0, this.width, this.height);
      if(this.server != null && this.session != null) {
    	  Level var85;
    	  (var85 = new Level()).setData(8, 8, 8, new byte[512]);
    	  this.setLevel(var85);
      } else {
    	  try {
    		  if(!this.levelLoaded) {
    			  Level var11 = null;
    			  if((var11 = LevelUtils.load()) != null) {
                     this.setLevel(var11);
                  }
               }
            } catch (Exception var54) {
               var54.printStackTrace();
            }

            if(this.level == null) {
               this.generateLevel(1);
            }
         }

         this.particleManager = new ParticleManager(this.level, this.textureManager);

         checkGLError("Post startup");
         this.hud = new HUDScreen(this, this.width, this.height);
         if(this.server != null && this.session != null) {
        	 this.gamemode = new CreativeGameMode(this);
        	 this.networkManager = new NetworkManager(this, this.server, this.session.username, this.session.mppass);
         }
         
         YeeHax.startup();

         long var13 = EagRuntime.steadyTimeMillis();
         int var15 = 0;

         try {
        	 while(this.running) {
        		 if(this.waiting) {
        			 EagUtils.sleep(100L);
        		 } else {
        			 if(Display.isCloseRequested()) {
        				 this.running = false;
        			 }

        			 try {
        				 long var16;
        				 long var18 = (var16 = EagRuntime.steadyTimeMillis()) - this.timer.lastSysClock;
        				 long var20 = EagRuntime.nanoTime() / 1000000L;
        				 double var24;
        				 if(var18 > 1000L) {
        					 long var22 = var20 - this.timer.lastHRClock;
        					 var24 = (double)var18 / (double)var22;
        					 this.timer.adjustment += (var24 - this.timer.adjustment) * 0.20000000298023224D;
        					 this.timer.lastSysClock = var16;
        					 this.timer.lastHRClock = var20;
        				 }

        				 if(var18 < 0L) {
        					 this.timer.lastSysClock = var16;
        					 this.timer.lastHRClock = var20;
        				 }

        				 double var95;
        				 var24 = ((var95 = (double)var20 / 1000.0D) - this.timer.lastHR) * this.timer.adjustment;
        				 this.timer.lastHR = var95;
        				 if(var24 < 0.0D) {
        					 var24 = 0.0D;
        				 }

        				 if(var24 > 1.0D) {
        					 var24 = 1.0D;
        				 }

        				 this.timer.elapsedDelta = (float)((double)this.timer.elapsedDelta + var24 * (double)this.timer.speed * (double)this.timer.tps);
        				 this.timer.elapsedTicks = (int)this.timer.elapsedDelta;
        				 if(this.timer.elapsedTicks > 100) {
        					 this.timer.elapsedTicks = 100;
        				 }

        				 this.timer.elapsedDelta -= (float)this.timer.elapsedTicks;
        				 this.timer.delta = this.timer.elapsedDelta;

        				 for(int var64 = 0; var64 < this.timer.elapsedTicks; ++var64) {
        					 ++this.ticks;
        					 this.tick();
        				 }

        				 checkGLError("Pre render");
                  
        				 if (!Display.contextLost()) {
        					 EaglercraftGPU.optimize();
        					 PlatformOpenGL._wglBindFramebuffer(0x8D40, null);
        					 GlStateManager.viewport(0, 0, this.width, this.height);
        					 GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
        					 GlStateManager.pushMatrix();
        					 GlStateManager.clear(RealOpenGLEnums.GL_COLOR_BUFFER_BIT | RealOpenGLEnums.GL_DEPTH_BUFFER_BIT);
        					 GlStateManager.enableTexture2D();
        					 GlStateManager.popMatrix();
        				 }
                  
        				 GL11.glEnable(3553);
        				 if(!this.online) {
        					 this.gamemode.applyCracks(this.timer.delta);
        					 if(this.renderer.displayActive && !Display.isActive()) {
        						 this.pause();
        					 }

        					 this.renderer.displayActive = Display.isActive();
        					 int var68;
        					 int var70;
        					 int var86;
        					 int var81;
        					 if(this.hasMouse) {
        						 var81 = 0;
        						 var86 = 0;
        						 if(!this.levelLoaded) {
        							 var81 = Mouse.getDX();
        							 var86 = Mouse.getDY();
        						 }

        						 byte var91 = 1;
        						 if(this.settings.invertMouse) {
        							 var91 = -1;
        						 }

        						 this.player.turn((float)var81, (float)(var86 * var91));
        					 }

        					 if(!this.online) {
        						 Vec3D playerVector = this.renderer.getPlayerVector(this.timer.delta);
        						 var81 = this.width * 240 / this.height;
        						 var86 = this.height * 240 / this.height;
        						 int var94 = Mouse.getX() * var81 / this.width;
        						 var70 = var86 - Mouse.getY() * var86 / this.height - 1;
        						 if(this.level != null) {
        							 float var29 = this.player.xRotO + (this.player.xRot - this.player.xRotO) * this.timer.delta;
        							 float var30 = this.player.yRotO + (this.player.yRot - this.player.yRotO) * this.timer.delta;
        							 float var32 = MathHelper.cos(-var30 * 0.017453292F - 3.1415927F);
        							 float var69 = MathHelper.sin(-var30 * 0.017453292F - 3.1415927F);
        							 float var74 = MathHelper.cos(-var29 * 0.017453292F);
        							 float var33 = MathHelper.sin(-var29 * 0.017453292F);
        							 float var34 = var69 * var74;
        							 float var87 = var32 * var74;
        							 float var36 = this.gamemode.getReachDistance();
        							 this.selected = this.level.clip(playerVector, playerVector.add(var34 * var36, var33 * var36, var87 * var36));
        							 var74 = var36;
        							 if(this.selected != null) {
        								 var74 = this.selected.vec.distance(playerVector);
        							 }

        							 if(this.gamemode instanceof CreativeGameMode) {
        								 var36 = 32.0F;
        							 } else {
        								 var36 = var74;
        							 }

        							 this.renderer.entity = null;
        							 List var37 = this.level.blockMap.getEntities(this.player, this.player.bb.expand(var34 * var36, var33 * var36, var87 * var36));
        							 float var35 = 0.0F;

        							 for(var81 = 0; var81 < var37.size(); ++var81) {
        								 Entity var88;
        								 if((var88 = (Entity)var37.get(var81)).isPickable()) {
        									 var74 = 0.1F;
        									 MovingObjectPosition var78;
        									 if((var78 = var88.bb.grow(var74, var74, var74).clip(playerVector, playerVector.add(var34 * var36, var33 * var36, var87 * var36))) != null && ((var74 = playerVector.distance(var78.vec)) < var35 || var35 == 0.0F)) {
        										 this.renderer.entity = var88;
        										 var35 = var74;
        									 }
        								 }
        							 }

        							 if(this.renderer.entity != null && !(this.gamemode instanceof CreativeGameMode)) {
        								 this.selected = new MovingObjectPosition(this.renderer.entity);
        							 }

        							 int var77 = 0;

        							 while(true) {
        								 if(var77 >= 2) {
        									 GL11.glColorMask(true, true, true, false);
        									 break;
        								 }

        								 if(this.settings.anaglyph) {
        									 if(var77 == 0) {
        										 GL11.glColorMask(false, true, true, false);
        									 } else {
        										 GL11.glColorMask(true, false, false, false);
        									 }
        								 }

        								 ParticleManager var93 = this.particleManager;
                              GL11.glViewport(0, 0, this.width, this.height);
                              Level var26 = this.level;
                              var29 = 1.0F / (float)(4 - this.settings.viewDistance);
                              var29 = 1.0F - (float)Math.pow((double)var29, 0.25D);
                              var30 = (float)(var26.skyColor >> 16 & 255) / 255.0F;
                              float var117 = (float)(var26.skyColor >> 8 & 255) / 255.0F;
                              var32 = (float)(var26.skyColor & 255) / 255.0F;
                              this.renderer.fogRed = (float)(var26.fogColor >> 16 & 255) / 255.0F;
                              this.renderer.fogBlue = (float)(var26.fogColor >> 8 & 255) / 255.0F;
                              this.renderer.fogGreen = (float)(var26.fogColor & 255) / 255.0F;
                              this.renderer.fogRed += (var30 - this.renderer.fogRed) * var29;
                              this.renderer.fogBlue += (var117 - this.renderer.fogBlue) * var29;
                              this.renderer.fogGreen += (var32 - this.renderer.fogGreen) * var29;
                              this.renderer.fogRed *= this.renderer.fogColorMultiplier;
                              this.renderer.fogBlue *= this.renderer.fogColorMultiplier;
                              this.renderer.fogGreen *= this.renderer.fogColorMultiplier;
                              Block var73;
                              if((var73 = Block.blocks[var26.getTile((int)this.player.x, (int)(this.player.y + 0.12F), (int)this.player.z)]) != null && var73.getLiquidType() != LiquidType.NOT_LIQUID) {
                                 LiquidType var79;
                                 if((var79 = var73.getLiquidType()) == LiquidType.WATER) {
                                	 this.renderer.fogRed = 0.02F;
                                	 this.renderer.fogBlue = 0.02F;
                                	 this.renderer.fogGreen = 0.2F;
                                 } else if(var79 == LiquidType.LAVA) {
                                	 this.renderer.fogRed = 0.6F;
                                	 this.renderer.fogBlue = 0.1F;
                                	 this.renderer.fogGreen = 0.0F;
                                 }
                              }

                              if(this.settings.anaglyph) {
                                 var74 = (this.renderer.fogRed * 30.0F + this.renderer.fogBlue * 59.0F + this.renderer.fogGreen * 11.0F) / 100.0F;
                                 var33 = (this.renderer.fogRed * 30.0F + this.renderer.fogBlue * 70.0F) / 100.0F;
                                 var34 = (this.renderer.fogRed * 30.0F + this.renderer.fogGreen * 70.0F) / 100.0F;
                                 this.renderer.fogRed = var74;
                                 this.renderer.fogBlue = var33;
                                 this.renderer.fogGreen = var34;
                              }

                              GL11.glClearColor(this.renderer.fogRed, this.renderer.fogBlue, this.renderer.fogGreen, 0.0F);
                              GL11.glClear(16640);
                              this.renderer.fogColorMultiplier = 1.0F;
                              GL11.glEnable(2884);
                              this.renderer.fogEnd = (float)(512 >> (this.renderer.minecraft.settings.viewDistance << 1));
                              GL11.glMatrixMode(5889);
                              GL11.glLoadIdentity();
                              var29 = 0.07F;
                              if(this.settings.anaglyph) {
                                 GL11.glTranslatef((float)(-((var77 << 1) - 1)) * var29, 0.0F, 0.0F);
                              }

                              Player var116 = this.player;
                              var69 = 70.0F;
                              if(var116.health <= 0) {
                                 var74 = (float)var116.deathTime + this.timer.delta;
                                 var69 /= (1.0F - 500.0F / (var74 + 500.0F)) * 2.0F + 1.0F;
                              }

                              GL11.gluPerspective(var69, (float)this.width / (float)this.height, 0.05F, this.renderer.fogEnd);
                              GL11.glMatrixMode(5888);
                              GL11.glLoadIdentity();
                              if(this.settings.anaglyph) {
                                 GL11.glTranslatef((float)((var77 << 1) - 1) * 0.1F, 0.0F, 0.0F);
                              }

                              this.renderer.hurtEffect(this.timer.delta);
                              if(this.settings.viewBobbing) {
                            	  this.renderer.applyBobbing(this.timer.delta);
                              }

                              var116 = this.player;
                              GL11.glTranslatef(0.0F, 0.0F, -0.1F);
                              GL11.glRotatef(var116.xRotO + (var116.xRot - var116.xRotO) * this.timer.delta, 1.0F, 0.0F, 0.0F);
                              GL11.glRotatef(var116.yRotO + (var116.yRot - var116.yRotO) * this.timer.delta, 0.0F, 1.0F, 0.0F);
                              var69 = var116.xo + (var116.x - var116.xo) * this.timer.delta;
                              var74 = var116.yo + (var116.y - var116.yo) * this.timer.delta;
                              var33 = var116.zo + (var116.z - var116.zo) * this.timer.delta;
                              GL11.glTranslatef(-var69, -var74, -var33);
                              Frustrum var76 = FrustrumImpl.update();
                              Frustrum var100 = var76;

                              int var98;
                              for(var98 = 0; var98 < this.levelRenderer.chunkCache.length; ++var98) {
                            	  this.levelRenderer.chunkCache[var98].clip(var100);
                              }

                              this.levelRenderer = this.levelRenderer;
                              Collections.sort(this.levelRenderer.chunks, new ChunkDirtyDistanceComparator(this.player));
                              var98 = this.levelRenderer.chunks.size() - 1;
                              int var105;
                              if((var105 = this.levelRenderer.chunks.size()) > 3) {
                                 var105 = 3;
                              }

                              int var104;
                              for(var104 = 0; var104 < var105; ++var104) {
                                 Chunk var118;
                                 (var118 = (Chunk)this.levelRenderer.chunks.remove(var98 - var104)).update();
                                 var118.loaded = false;
                              }

                              this.renderer.updateFog();
                              GL11.glEnable(2912);
                              this.levelRenderer.sortChunks(this.player, 0);
                              int var83;
                              int var110;
                              Tessellator tess = Tessellator.getInstance();
                              WorldRenderer var115;
                              int var114;
                              int var125;
                              int var122;
                              int var120;
                              if(this.level.isSolid(this.player.x, this.player.y, this.player.z, 0.1F)) {
                                 var120 = (int)this.player.x;
                                 var83 = (int)this.player.y;
                                 var110 = (int)this.player.z;

                                 for(var122 = var120 - 1; var122 <= var120 + 1; ++var122) {
                                    for(var125 = var83 - 1; var125 <= var83 + 1; ++var125) {
                                       for(int var38 = var110 - 1; var38 <= var110 + 1; ++var38) {
                                          var105 = var38;
                                          var98 = var125;
                                          int var99 = var122;
                                          if((var104 = this.levelRenderer.level.getTile(var122, var125, var38)) != 0 && Block.blocks[var104].isSolid()) {
                                             GL11.glColor4f(0.2F, 0.2F, 0.2F, 1.0F);
                                             GL11.glDepthFunc(513);
                                             var115 = tess.getWorldRenderer();
                                             var115.begin(7, VertexFormat.POSITION_TEX);

                                             for(var114 = 0; var114 < 6; ++var114) {
                                                Block.blocks[var104].renderInside(var115, var99, var98, var105, var114);
                                             }

                                             tess.draw();
                                             GL11.glCullFace(1028);
                                             var115.begin(7, VertexFormat.POSITION_TEX);

                                             for(var114 = 0; var114 < 6; ++var114) {
                                                Block.blocks[var104].renderInside(var115, var99, var98, var105, var114);
                                             }

                                             tess.draw();
                                             GL11.glCullFace(1029);
                                             GL11.glDepthFunc(515);
                                          }
                                       }
                                    }
                                 }
                              }

                              this.renderer.setLighting(true);
                              this.levelRenderer.level.blockMap.render(playerVector, var76, this.textureManager, this.timer.delta);
                              this.renderer.setLighting(false);
                              this.renderer.updateFog();
                              float var107 = this.timer.delta;
                              ParticleManager var96 = var93;
                              var29 = -MathHelper.cos(this.player.yRot * 3.1415927F / 180.0F);
                              var117 = -(var30 = -MathHelper.sin(this.player.yRot * 3.1415927F / 180.0F)) * MathHelper.sin(this.player.xRot * 3.1415927F / 180.0F);
                              var32 = var29 * MathHelper.sin(this.player.xRot * 3.1415927F / 180.0F);
                              var69 = MathHelper.cos(this.player.xRot * 3.1415927F / 180.0F);

                              for(var83 = 0; var83 < 2; ++var83) {
                                 if(var96.particles[var83].size() != 0) {
                                    var110 = 0;
                                    if(var83 == 0) {
                                       var110 = var96.textureManager.load("/particles.png");
                                    }

                                    if(var83 == 1) {
                                       var110 = var96.textureManager.load("/terrain.png");
                                    }

                                    GL11.glBindTexture(3553, var110);
                                    WorldRenderer var121 = tess.getWorldRenderer();
                                    var121.begin(7, VertexFormat.POSITION_TEX_COLOR);

                                    for(var120 = 0; var120 < var96.particles[var83].size(); ++var120) {
                                       ((Particle)var96.particles[var83].get(var120)).render(var121, var107, var29, var69, var30, var117, var32);
                                    }

                                    tess.draw();
                                 }
                              }

                              GL11.glBindTexture(3553, this.textureManager.load("/rock.png"));
                              GL11.glEnable(3553);
                              GL11.glCallList(this.levelRenderer.listId);
                              this.renderer.updateFog();
                              GL11.glBindTexture(3553, this.textureManager.load("/clouds.png"));
                              GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                              var107 = (float)(this.levelRenderer.level.cloudColor >> 16 & 255) / 255.0F;
                              var29 = (float)(this.levelRenderer.level.cloudColor >> 8 & 255) / 255.0F;
                              var30 = (float)(this.levelRenderer.level.cloudColor & 255) / 255.0F;
                              if(this.settings.anaglyph) {
                                 var117 = (var107 * 30.0F + var29 * 59.0F + var30 * 11.0F) / 100.0F;
                                 var32 = (var107 * 30.0F + var29 * 70.0F) / 100.0F;
                                 var69 = (var107 * 30.0F + var30 * 70.0F) / 100.0F;
                                 var107 = var117;
                                 var29 = var32;
                                 var30 = var69;
                              }

                              var115 = tess.getWorldRenderer();
                              var74 = 0.0F;
                              var33 = 4.8828125E-4F;
                              var74 = (float)(this.levelRenderer.level.depth + 2);
                              var34 = ((float)this.levelRenderer.ticks + this.timer.delta) * var33 * 0.03F;
                              var35 = 0.0F;
                              var115.begin(7, VertexFormat.POSITION_TEX_COLOR);

                              for(var86 = -2048; var86 < this.levelRenderer.level.width + 2048; var86 += 512) {
                                 for(var125 = -2048; var125 < this.levelRenderer.level.height + 2048; var125 += 512) {
                                    var115.pos((float)var86, var74, (float)(var125 + 512)).tex((float)var86 * var33 + var34, (float)(var125 + 512) * var33).color(var107, var29, var30, 1.0f).endVertex();
                                    var115.pos((float)(var86 + 512), var74, (float)(var125 + 512)).tex((float)(var86 + 512) * var33 + var34, (float)(var125 + 512) * var33).color(var107, var29, var30, 1.0f).endVertex();
                                    var115.pos((float)(var86 + 512), var74, (float)var125).tex((float)(var86 + 512) * var33 + var34, (float)var125 * var33).color(var107, var29, var30, 1.0f).endVertex();
                                    var115.pos((float)var86, var74, (float)var125).tex((float)var86 * var33 + var34, (float)var125 * var33).color(var107, var29, var30, 1.0f).endVertex();
                                    var115.pos((float)var86, var74, (float)var125).tex((float)var86 * var33 + var34, (float)var125 * var33).color(var107, var29, var30, 1.0f).endVertex();
                                    var115.pos((float)(var86 + 512), var74, (float)var125).tex((float)(var86 + 512) * var33 + var34, (float)var125 * var33).color(var107, var29, var30, 1.0f).endVertex();
                                    var115.pos((float)(var86 + 512), var74, (float)(var125 + 512)).tex((float)(var86 + 512) * var33 + var34, (float)(var125 + 512) * var33).color(var107, var29, var30, 1.0f).endVertex();
                                    var115.pos((float)var86, var74, (float)(var125 + 512)).tex((float)var86 * var33 + var34, (float)(var125 + 512) * var33).color(var107, var29, var30, 1.0f).endVertex();
                                 }
                              }

                              tess.draw();
                              GL11.glDisable(3553);
                              var115.begin(7, VertexFormat.POSITION_COLOR);
                              var34 = (float)(this.levelRenderer.level.skyColor >> 16 & 255) / 255.0F;
                              var35 = (float)(this.levelRenderer.level.skyColor >> 8 & 255) / 255.0F;
                              var87 = (float)(this.levelRenderer.level.skyColor & 255) / 255.0F;
                              if(this.settings.anaglyph) {
                                 var36 = (var34 * 30.0F + var35 * 59.0F + var87 * 11.0F) / 100.0F;
                                 var69 = (var34 * 30.0F + var35 * 70.0F) / 100.0F;
                                 var74 = (var34 * 30.0F + var87 * 70.0F) / 100.0F;
                                 var34 = var36;
                                 var35 = var69;
                                 var87 = var74;
                              }

                              var74 = (float)(this.levelRenderer.level.depth + 10);

                              for(var125 = -2048; var125 < this.levelRenderer.level.width + 2048; var125 += 512) {
                                 for(var68 = -2048; var68 < this.levelRenderer.level.height + 2048; var68 += 512) {
                                    var115.pos((float)var125, var74, (float)var68).color(var34, var35, var87, 1.0f).endVertex();
                                    var115.pos((float)(var125 + 512), var74, (float)var68).color(var34, var35, var87, 1.0f).endVertex();
                                    var115.pos((float)(var125 + 512), var74, (float)(var68 + 512)).color(var34, var35, var87, 1.0f).endVertex();
                                    var115.pos((float)var125, var74, (float)(var68 + 512)).color(var34, var35, var87, 1.0f).endVertex();
                                 }
                              }

                              tess.draw();
                              GL11.glEnable(3553);
                              this.renderer.updateFog();
                              int var108;
                              if(this.selected != null) {
                                 GL11.glDisable(3008);
                                 MovingObjectPosition var10001 = this.selected;
                                 var105 = this.player.inventory.getSelected();
                                 boolean var106 = false;
                                 MovingObjectPosition var102 = var10001;
                                 WorldRenderer var113 = tess.getWorldRenderer();
                                 GL11.glEnable(3042);
                                 GL11.glEnable(3008);
                                 GL11.glBlendFunc(770, 1);
                                 GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)EagRuntime.steadyTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
                                 if(this.levelRenderer.cracks > 0.0F) {
                                    GL11.glBlendFunc(774, 768);
                                    var108 = this.textureManager.load("/terrain.png");
                                    GL11.glBindTexture(3553, var108);
                                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
                                    GL11.glPushMatrix();
                                    Block var10000 = (var114 = this.levelRenderer.level.getTile(var102.x, var102.y, var102.z)) > 0?Block.blocks[var114]:null;
                                    var73 = var10000;
                                    var74 = (var10000.x1 + var73.x2) / 2.0F;
                                    var33 = (var73.y1 + var73.y2) / 2.0F;
                                    var34 = (var73.z1 + var73.z2) / 2.0F;
                                    GL11.glTranslatef((float)var102.x + var74, (float)var102.y + var33, (float)var102.z + var34);
                                    var35 = 1.01F;
                                    GL11.glScalef(1.01F, var35, var35);
                                    GL11.glTranslatef(-((float)var102.x + var74), -((float)var102.y + var33), -((float)var102.z + var34));
                                    var113.begin(7, VertexFormat.POSITION_TEX);
                                    var113.markDirty();
                                    GL11.glDepthMask(false);
                                    if(var73 == null) {
                                       var73 = Block.STONE;
                                    }

                                    for(var86 = 0; var86 < 6; ++var86) {
                                       var73.renderSide(var113, var102.x, var102.y, var102.z, var86, 240 + (int)(this.levelRenderer.cracks * 10.0F));
                                    }

                                    tess.draw();
                                    GL11.glDepthMask(true);
                                    GL11.glPopMatrix();
                                 }

                                 GL11.glDisable(3042);
                                 GL11.glDisable(3008);
                                 var10001 = this.selected;
                                 var106 = false;
                                 var102 = var10001;
                                 GL11.glEnable(3042);
                                 GL11.glBlendFunc(770, 771);
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
                                 GL11.glLineWidth(2.0F);
                                 GL11.glDisable(3553);
                                 GL11.glDepthMask(false);
                                 var29 = 0.002F;
                                 if((var104 = this.levelRenderer.level.getTile(var102.x, var102.y, var102.z)) > 0) {
									 AABB var111 = Block.blocks[var104].getSelectionBox(var102.x, var102.y, var102.z).grow(var29, var29, var29);
									 var113.begin(3, VertexFormat.POSITION);
									 var113.pos(var111.x0, var111.y0, var111.z0).endVertex();
									 var113.pos(var111.x1, var111.y0, var111.z0).endVertex();
									 var113.pos(var111.x1, var111.y0, var111.z1).endVertex();
									 var113.pos(var111.x0, var111.y0, var111.z1).endVertex();
									 var113.pos(var111.x0, var111.y0, var111.z0).endVertex();
									 tess.draw();
									 var113.begin(3, VertexFormat.POSITION);
									 var113.pos(var111.x0, var111.y1, var111.z0).endVertex();
									 var113.pos(var111.x1, var111.y1, var111.z0).endVertex();
									 var113.pos(var111.x1, var111.y1, var111.z1).endVertex();
									 var113.pos(var111.x0, var111.y1, var111.z1).endVertex();
									 var113.pos(var111.x0, var111.y1, var111.z0).endVertex();
									 tess.draw();
									 var113.begin(1, VertexFormat.POSITION);
									 var113.pos(var111.x0, var111.y0, var111.z0).endVertex();
									 var113.pos(var111.x0, var111.y1, var111.z0).endVertex();
									 var113.pos(var111.x1, var111.y0, var111.z0).endVertex();
									 var113.pos(var111.x1, var111.y1, var111.z0).endVertex();
									 var113.pos(var111.x1, var111.y0, var111.z1).endVertex();
									 var113.pos(var111.x1, var111.y1, var111.z1).endVertex();
									 var113.pos(var111.x0, var111.y0, var111.z1).endVertex();
									 var113.pos(var111.x0, var111.y1, var111.z1).endVertex();
									 tess.draw();
                                 }

                                 GL11.glDepthMask(true);
                                 GL11.glEnable(3553);
                                 GL11.glDisable(3042);
                                 GL11.glEnable(3008);
                              }

                              GL11.glBlendFunc(770, 771);
                              this.renderer.updateFog();
                              GL11.glEnable(3553);
                              GL11.glEnable(3042);
                              GL11.glBindTexture(3553, this.textureManager.load("/water.png"));
                              GL11.glCallList(this.levelRenderer.listId + 1);
                              GL11.glDisable(3042);
                              GL11.glEnable(3042);
                              GL11.glColorMask(false, false, false, false);
                              GL11.glColorMask(true, true, true, true);
                              if(this.settings.anaglyph) {
                                 if(var77 == 0) {
                                    GL11.glColorMask(false, true, true, false);
                                 } else {
                                    GL11.glColorMask(true, false, false, false);
                                 }
                              }

                              var120 = this.levelRenderer.sortChunks(this.player, 1);
                              if(var120 > 0) {
                                 GL11.glBindTexture(3553, this.textureManager.load("/terrain.png"));
                              }

                              GL11.glDepthMask(true);
                              GL11.glDisable(3042);
                              GL11.glDisable(2912);
                              if(this.raining) {
                                 float var97 = this.timer.delta;
                                 Level var109 = this.level;
                                 var104 = (int)this.player.x;
                                 var108 = (int)this.player.y;
                                 var114 = (int)this.player.z;
                                 WorldRenderer var84 = tess.getWorldRenderer();
                                 GL11.glDisable(2884);
                                 GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                                 GL11.glEnable(3042);
                                 GL11.glBlendFunc(770, 771);
                                 GL11.glBindTexture(3553, this.textureManager.load("/rain.png"));

                                 for(var110 = var104 - 5; var110 <= var104 + 5; ++var110) {
                                    for(var122 = var114 - 5; var122 <= var114 + 5; ++var122) {
                                       var120 = var109.getHighestTile(var110, var122);
                                       var86 = var108 - 5;
                                       var125 = var108 + 5;
                                       if(var86 < var120) {
                                          var86 = var120;
                                       }

                                       if(var125 < var120) {
                                          var125 = var120;
                                       }

                                       if(var86 != var125) {
                                          var74 = ((float)((this.renderer.levelTicks + var110 * 3121 + var122 * 418711) % 32) + var97) / 32.0F;
                                          float var124 = (float)var110 + 0.5F - this.player.x;
                                          var35 = (float)var122 + 0.5F - this.player.z;
                                          float var92 = MathHelper.sqrt(var124 * var124 + var35 * var35) / (float)5;
                                          GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - var92 * var92) * 0.7F);
                                          var84.begin(7, VertexFormat.POSITION_TEX);
                                          var84.pos((float)var110, (float)var86, (float)var122).tex(0.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          var84.pos((float)(var110 + 1), (float)var86, (float)(var122 + 1)).tex(2.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          var84.pos((float)(var110 + 1), (float)var125, (float)(var122 + 1)).tex(2.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          var84.pos((float)var110, (float)var125, (float)var122).tex(0.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          var84.pos((float)var110, (float)var86, (float)(var122 + 1)).tex(0.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          var84.pos((float)(var110 + 1), (float)var86, (float)var122).tex(2.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          var84.pos((float)(var110 + 1), (float)var125, (float)var122).tex(2.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          var84.pos((float)var110, (float)var125, (float)(var122 + 1)).tex(0.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F).endVertex();
                                          tess.draw();
                                       }
                                    }
                                 }

                                 GL11.glEnable(2884);
                                 GL11.glDisable(3042);
                              }

                              if(this.renderer.entity != null) {
                            	  this.renderer.entity.renderHover(this.textureManager, this.timer.delta);
                              }

                              GL11.glClear(256);
                              GL11.glLoadIdentity();
                              if(this.settings.anaglyph) {
                                 GL11.glTranslatef((float)((var77 << 1) - 1) * 0.1F, 0.0F, 0.0F);
                              }

                              this.renderer.hurtEffect(this.timer.delta);
                              if(this.settings.viewBobbing) {
                            	  this.renderer.applyBobbing(this.timer.delta);
                              }

                              HeldBlock var112 = this.renderer.heldBlock;
                              var117 = this.renderer.heldBlock.lastPos + (var112.pos - var112.lastPos) * this.timer.delta;
                              var116 = var112.minecraft.player;
                              GL11.glPushMatrix();
                              GL11.glRotatef(var116.xRotO + (var116.xRot - var116.xRotO) * this.timer.delta, 1.0F, 0.0F, 0.0F);
                              GL11.glRotatef(var116.yRotO + (var116.yRot - var116.yRotO) * this.timer.delta, 0.0F, 1.0F, 0.0F);
                              var112.minecraft.renderer.setLighting(true);
                              GL11.glPopMatrix();
                              GL11.glPushMatrix();
                              var69 = 0.8F;
                              if(var112.moving) {
                                 var33 = MathHelper.sin((var74 = ((float)var112.offset + this.timer.delta) / 7.0F) * 3.1415927F);
                                 GL11.glTranslatef(-MathHelper.sin(MathHelper.sqrt(var74) * 3.1415927F) * 0.4F, MathHelper.sin(MathHelper.sqrt(var74) * 3.1415927F * 2.0F) * 0.2F, -var33 * 0.2F);
                              }

                              GL11.glTranslatef(0.7F * var69, -0.65F * var69 - (1.0F - var117) * 0.6F, -0.9F * var69);
                              GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                              GL11.glEnable(2977);
                              if(var112.moving) {
                                 var33 = MathHelper.sin((var74 = ((float)var112.offset + this.timer.delta) / 7.0F) * var74 * 3.1415927F);
                                 GL11.glRotatef(MathHelper.sin(MathHelper.sqrt(var74) * 3.1415927F) * 80.0F, 0.0F, 1.0F, 0.0F);
                                 GL11.glRotatef(-var33 * 20.0F, 1.0F, 0.0F, 0.0F);
                              }

                              GL11.glColor4f(var74 = var112.minecraft.level.getBrightness((int)var116.x, (int)var116.y, (int)var116.z), var74, var74, 1.0F);
                              WorldRenderer var123 = tess.getWorldRenderer();
                              if(var112.block != null) {
                                 var34 = 0.4F;
                                 GL11.glScalef(0.4F, var34, var34);
                                 GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                                 GL11.glBindTexture(3553, var112.minecraft.textureManager.load("/terrain.png"));
                                 var112.block.renderPreview(var123, tess);
                              } else {
                                 var116.bindTexture(var112.minecraft.textureManager);
                                 GL11.glScalef(1.0F, -1.0F, -1.0F);
                                 GL11.glTranslatef(0.0F, 0.2F, 0.0F);
                                 GL11.glRotatef(-120.0F, 0.0F, 0.0F, 1.0F);
                                 GL11.glScalef(1.0F, 1.0F, 1.0F);
                                 var34 = 0.0625F;
                                 ModelPart var127 = var112.minecraft.player.getModel().leftArm;
                                 if(!(var127 = var112.minecraft.player.getModel().leftArm).compiled) {
                                    var127.render(var34);
                                 }

                                 GL11.glCallList(var127.displayList);
                              }

                              GL11.glDisable(2977);
                              GL11.glPopMatrix();
                              var112.minecraft.renderer.setLighting(false);
                              if(!this.settings.anaglyph) {
                                 break;
                              }

                              ++var77;
                           }

                           this.hud.render(this.timer.delta, this.currentScreen != null, var94, var70);
                        } else {
                           GL11.glViewport(0, 0, this.width, this.height);
                           GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                           GL11.glClear(16640);
                           GL11.glMatrixMode(5889);
                           GL11.glLoadIdentity();
                           GL11.glMatrixMode(5888);
                           GL11.glLoadIdentity();
                           this.renderer.enableGuiMode();
                        }

                        if(this.currentScreen != null) {
                           this.currentScreen.render(var94, var70);
                        }

                        this.updateDisplay();
                     }
                  }

                  checkGLError("Post render");
                  ++var15;
               } catch (Exception var58) {
                  this.setCurrentScreen(new ErrorScreen("Client error", "The game broke! [" + var58 + "]"));
                  var58.printStackTrace();
               }

               while(EagRuntime.steadyTimeMillis() >= var13 + 1000L) {
                  this.debug = var15 + " fps, " + Chunk.chunkUpdates + " chunk updates";
                  Chunk.chunkUpdates = 0;
                  var13 += 1000L;
                  var15 = 0;
               }
            }
         }

         return;
      } catch (StopGameException var59) {
         ;
      } catch (Exception var60) {
         var60.printStackTrace();
         return;
      } finally {
    	  this.shutdown();
      }

   }
   
   public void updateDisplay() {
		if (Display.isVSyncSupported()) {
			Display.setVSync(true);
		} else {
			this.settings.limitFramerate = false;
		}
		Display.update(0);
		this.checkWindowResize();
	}

	protected void checkWindowResize() {
		float dpiFetch = -1.0f;
		if ((Display.wasResized() || (dpiFetch = Math.max(Display.getDPI(), 1.0f)) != this.dpi)) {
			int i = this.width;
			int j = this.height;
			float f = this.dpi;
			this.width = Display.getWidth();
			this.height = Display.getHeight();
			this.dpi = dpiFetch == -1.0f ? Math.max(Display.getDPI(), 1.0f) : dpiFetch;
			if (this.width != i || this.height != j || this.dpi != f) {
				if (this.width <= 0) {
					this.width = 1;
				}

				if (this.height <= 0) {
					this.height = 1;
				}

				this.width = Math.max(1, width);
				this.height = Math.max(1, height);
				if (this.currentScreen != null) {
					this.setCurrentScreen(currentScreen);
				}
				
				this.hud = new HUDScreen(this, width, height);
				if(this.currentScreen == null) {
					this.hasMouse = true;
					Mouse.setGrabbed(true);
				}
				this.progressBar = new ProgressBarDisplay(this);
			}
		}
	}
	
	public int getLimitFramerate() {
		return this.level == null && this.currentScreen != null ? 30 : 260;
	}

	public boolean isFramerateLimitBelowMax() {
		return (float) this.getLimitFramerate() < 260;
	}

   public final void grabMouse() {
      if(!this.hasMouse) {
         this.hasMouse = true;
         if(!this.levelLoaded) {
            Mouse.setGrabbed(true);
         }

         this.setCurrentScreen((GuiScreen)null);
         this.lastClick = this.ticks + 10000;
      }
   }

   public final void pause() {
      if(this.currentScreen == null) {
         this.setCurrentScreen(new PauseScreen());
      }
   }

   private void onMouseClick(int var1) {
      if(var1 != 0 || this.blockHitTime <= 0) {
         HeldBlock var2;
         if(var1 == 0) {
            var2 = this.renderer.heldBlock;
            this.renderer.heldBlock.offset = -1;
            var2.moving = true;
         }

         int var3;
         if(var1 == 1 && (var3 = this.player.inventory.getSelected()) > 0 && this.gamemode.useItem(this.player, var3)) {
            var2 = this.renderer.heldBlock;
            this.renderer.heldBlock.pos = 0.0F;
         } else if(this.selected == null) {
            if(var1 == 0 && !(this.gamemode instanceof CreativeGameMode)) {
               this.blockHitTime = 10;
            }

         } else {
            if(this.selected.entityPos == 1) {
               if(var1 == 0) {
                  this.selected.entity.hurt(this.player, 4);
                  return;
               }
            } else if(this.selected.entityPos == 0) {
               var3 = this.selected.x;
               int var4 = this.selected.y;
               int var5 = this.selected.z;
               if(var1 != 0) {
                  if(this.selected.face == 0) {
                     --var4;
                  }

                  if(this.selected.face == 1) {
                     ++var4;
                  }

                  if(this.selected.face == 2) {
                     --var5;
                  }

                  if(this.selected.face == 3) {
                     ++var5;
                  }

                  if(this.selected.face == 4) {
                     --var3;
                  }

                  if(this.selected.face == 5) {
                     ++var3;
                  }
               }

               Block var6 = Block.blocks[this.level.getTile(var3, var4, var5)];
               if(var1 == 0) {
                  if(var6 != Block.BEDROCK || this.player.userType >= 100) {
                     this.gamemode.hitBlock(var3, var4, var5);
                     return;
                  }
               } else {
                  int var10;
                  if((var10 = this.player.inventory.getSelected()) <= 0) {
                     return;
                  }

                  Block var8;
                  AABB var9;
                  if(((var8 = Block.blocks[this.level.getTile(var3, var4, var5)]) == null || var8 == Block.WATER || var8 == Block.STATIONARY_WATER || var8 == Block.LAVA || var8 == Block.STATIONARY_LAVA) && ((var9 = Block.blocks[var10].getCollisionBox(var3, var4, var5)) == null || (this.player.bb.intersects(var9)?false:this.level.isFree(var9)))) {
                     if(!this.gamemode.canPlace(var10)) {
                        return;
                     }
                     
                     if(this.isOnline()) {
                         this.networkManager.sendBlockChange(var3, var4, var5, var1, var10);
                      }

                     this.level.netSetTile(var3, var4, var5, var10);
                     var2 = this.renderer.heldBlock;
                     this.renderer.heldBlock.pos = 0.0F;
                     Block.blocks[var10].onPlace(this.level, var3, var4, var5);
                  }
               }
            }

         }
      }
   }
   
   private void tick() {
	  this.levelSave();
      if(this.soundPlayer != null) {
         SoundPlayer var1 = this.soundPlayer;
         SoundManager var2 = this.sound;
         if(EagRuntime.steadyTimeMillis() > var2.lastMusic && var2.playMusic(var1, "calm")) {
            var2.lastMusic = EagRuntime.steadyTimeMillis() + (long)var2.random.nextInt(900000) + 300000L;
         }
      }

      this.gamemode.spawnMob();
      HUDScreen var17 = this.hud;
      ++this.hud.ticks;

      int var16;
      for(var16 = 0; var16 < var17.chat.size(); ++var16) {
         ++((ChatLine)var17.chat.get(var16)).time;
      }

      GL11.glBindTexture(3553, this.textureManager.load("/terrain.png"));
      TextureManager var19 = this.textureManager;

      for(var16 = 0; var16 < var19.animations.size(); ++var16) {
         TextureFX var3;
         (var3 = (TextureFX)var19.animations.get(var16)).anaglyph = var19.settings.anaglyph;
         var3.animate();
         var19.textureBuffer.clear();
         var19.textureBuffer.put(var3.textureData);
         var19.textureBuffer.position(0).limit(var3.textureData.length);
         GL11.glTexSubImage2D(3553, 0, var3.textureId % 16 << 4, var3.textureId / 16 << 4, 16, 16, 6408, 5121, var19.textureBuffer);
      }

      int var4;
      int var8;
      int var40;
      int var46;
      int var45;
      if(this.networkManager != null && !(this.currentScreen instanceof ErrorScreen)) {
    	  if(this.networkManager.isConnected()) {
             if(this.networkManager.successful) {
                if(this.networkManager.isConnected()) {
                   try {
                      this.networkManager.netHandler.read();
                      var4 = 0;

                      //TODO
                      while(this.networkManager.netHandler.in.position() > 0) {
                    	  this.networkManager.netHandler.in.flip();
                         byte var5 = this.networkManager.netHandler.in.get(0);
                         PacketType var6;
                         if((var6 = PacketType.packets[var5]) == null) {
                            throw new IOException("Bad command: " + var5);
                         }

                         if(this.networkManager.netHandler.in.remaining() < var6.length + 1) {
                        	 this.networkManager.netHandler.in.compact();
                            break;
                         }

                         this.networkManager.netHandler.in.get();
                         Object[] var7 = new Object[var6.params.length];

                         for(var8 = 0; var8 < var7.length; ++var8) {
                            var7[var8] = this.networkManager.netHandler.readObject(var6.params[var8]);
                         }
                         
                         if(this.networkManager.successful) {
                            if(var6 == PacketType.IDENTIFICATION) {
                               ((GuiConnecting)currentScreen).setText(var7[1].toString(), var7[2].toString());
                               this.player.userType = ((Byte)var7[3]).byteValue();
                            } else if(var6 == PacketType.LEVEL_INIT) {
                               this.setLevel((Level)null);
                               this.networkManager.levelData = new EaglerOutputStream();
                            } else if(var6 == PacketType.LEVEL_DATA) {
                               short var11 = ((Short)var7[0]).shortValue();
                               byte[] var12 = (byte[])((byte[])var7[1]);
                               byte var13 = ((Byte)var7[2]).byteValue();
                               ((GuiConnecting)currentScreen).setProgress(var13);
                               this.networkManager.levelData.write(var12, 0, var11);
                            } else if(var6 == PacketType.LEVEL_FINALIZE) {
                               try {
                            	   this.networkManager.levelData.close();
                               } catch (IOException var14) {
                                  var14.printStackTrace();
                               }

                               byte[] var51 = LevelIO.decompress(new EaglerInputStream(this.networkManager.levelData.toByteArray()));
                               this.networkManager.levelData = null;
                               short var55 = ((Short)var7[0]).shortValue();
                               short var63 = ((Short)var7[1]).shortValue();
                               short var21 = ((Short)var7[2]).shortValue();
                               Level var30;
                               (var30 = new Level()).setNetworkMode(true);
                               var30.setData(var55, var63, var21, var51);
                               this.setLevel(var30);
                               this.online = false;
                               this.networkManager.levelLoaded = true;
                               this.setCurrentScreen(new PauseScreen());
                            } else if(var6 == PacketType.BLOCK_CHANGE) {
                               if(this.level != null) {
                                  this.level.netSetTile(((Short)var7[0]).shortValue(), ((Short)var7[1]).shortValue(), ((Short)var7[2]).shortValue(), ((Byte)var7[3]).byteValue());
                               }
                            } else {
                               byte var9;
                               String var34;
                               NetworkPlayer var33;
                               short var36;
                               short var10004;
                               byte var10001;
                               short var47;
                               short var10003;
                               if(var6 == PacketType.SPAWN_PLAYER) {
                                  var10001 = ((Byte)var7[0]).byteValue();
                                  String var10002 = (String)var7[1];
                                  var10003 = ((Short)var7[2]).shortValue();
                                  var10004 = ((Short)var7[3]).shortValue();
                                  short var10005 = ((Short)var7[4]).shortValue();
                                  byte var10006 = ((Byte)var7[5]).byteValue();
                                  byte var58 = ((Byte)var7[6]).byteValue();
                                  var9 = var10006;
                                  short var10 = var10005;
                                  var47 = var10004;
                                  var36 = var10003;
                                  var34 = var10002;
                                  var5 = var10001;
                                  if(var5 >= 0) {
                                     var9 = (byte)(var9 + 128);
                                     var47 = (short)(var47 - 22);
                                     var33 = new NetworkPlayer(this, var5, var34, var36, var47, var10, (float)(var9 * 360) / 256.0F, (float)(var58 * 360) / 256.0F);
                                     this.networkManager.players.put(Byte.valueOf(var5), var33);
                                     this.level.addEntity(var33);
                                  } else {
                                     this.level.setSpawnPos(var36 / 32, var47 / 32, var10 / 32, (float)(var9 * 320 / 256));
                                     this.player.moveTo((float)var36 / 32.0F, (float)var47 / 32.0F, (float)var10 / 32.0F, (float)(var9 * 360) / 256.0F, (float)(var58 * 360) / 256.0F);
                                  }
                               } else {
                                  byte var53;
                                  NetworkPlayer var61;
                                  byte var69;
                                  if(var6 == PacketType.POSITION_ROTATION) {
                                     var10001 = ((Byte)var7[0]).byteValue();
                                     short var66 = ((Short)var7[1]).shortValue();
                                     var10003 = ((Short)var7[2]).shortValue();
                                     var10004 = ((Short)var7[3]).shortValue();
                                     var69 = ((Byte)var7[4]).byteValue();
                                     var9 = ((Byte)var7[5]).byteValue();
                                     var53 = var69;
                                     var47 = var10004;
                                     var36 = var10003;
                                     short var38 = var66;
                                     var5 = var10001;
                                     if(var5 < 0) {
                                        this.player.moveTo((float)var38 / 32.0F, (float)var36 / 32.0F, (float)var47 / 32.0F, (float)(var53 * 360) / 256.0F, (float)(var9 * 360) / 256.0F);
                                     } else {
                                        var53 = (byte)(var53 + 128);
                                        var36 = (short)(var36 - 22);
                                        if((var61 = (NetworkPlayer)this.networkManager.players.get(Byte.valueOf(var5))) != null) {
                                           var61.teleport(var38, var36, var47, (float)(var53 * 360) / 256.0F, (float)(var9 * 360) / 256.0F);
                                        }
                                     }
                                  } else {
                                     byte var37;
                                     byte var44;
                                     byte var49;
                                     byte var65;
                                     byte var67;
                                     if(var6 == PacketType.POSITION_ROTATION_UPDATE) {
                                        var10001 = ((Byte)var7[0]).byteValue();
                                        var67 = ((Byte)var7[1]).byteValue();
                                        var65 = ((Byte)var7[2]).byteValue();
                                        byte var64 = ((Byte)var7[3]).byteValue();
                                        var69 = ((Byte)var7[4]).byteValue();
                                        var9 = ((Byte)var7[5]).byteValue();
                                        var53 = var69;
                                        var49 = var64;
                                        var44 = var65;
                                        var37 = var67;
                                        var5 = var10001;
                                        if(var5 >= 0) {
                                           var53 = (byte)(var53 + 128);
                                           if((var61 = (NetworkPlayer)this.networkManager.players.get(Byte.valueOf(var5))) != null) {
                                              var61.queue(var37, var44, var49, (float)(var53 * 360) / 256.0F, (float)(var9 * 360) / 256.0F);
                                           }
                                        }
                                     } else if(var6 == PacketType.ROTATION_UPDATE) {
                                        var10001 = ((Byte)var7[0]).byteValue();
                                        var67 = ((Byte)var7[1]).byteValue();
                                        var44 = ((Byte)var7[2]).byteValue();
                                        var37 = var67;
                                        var5 = var10001;
                                        if(var5 >= 0) {
                                           var37 = (byte)(var37 + 128);
                                           NetworkPlayer var54;
                                           if((var54 = (NetworkPlayer)this.networkManager.players.get(Byte.valueOf(var5))) != null) {
                                              var54.queue((float)(var37 * 360) / 256.0F, (float)(var44 * 360) / 256.0F);
                                           }
                                        }
                                     } else if(var6 == PacketType.POSITION_UPDATE) {
                                        var10001 = ((Byte)var7[0]).byteValue();
                                        var67 = ((Byte)var7[1]).byteValue();
                                        var65 = ((Byte)var7[2]).byteValue();
                                        var49 = ((Byte)var7[3]).byteValue();
                                        var44 = var65;
                                        var37 = var67;
                                        var5 = var10001;
                                        NetworkPlayer var59;
                                        if(var5 >= 0 && (var59 = (NetworkPlayer)this.networkManager.players.get(Byte.valueOf(var5))) != null) {
                                           var59.queue(var37, var44, var49);
                                        }
                                     } else if(var6 == PacketType.DESPAWN_PLAYER) {
                                        var5 = ((Byte)var7[0]).byteValue();
                                        if(var5 >= 0 && (var33 = (NetworkPlayer)this.networkManager.players.remove(Byte.valueOf(var5))) != null) {
                                           var33.clear();
                                           this.level.removeEntity(var33);
                                        }
                                     } else if(var6 == PacketType.CHAT_MESSAGE) {
                                        var10001 = ((Byte)var7[0]).byteValue();
                                        var34 = (String)var7[1];
                                        var5 = var10001;
                                        if(var5 < 0) {
                                           this.hud.addChat(var34);
                                        } else {
                                           this.networkManager.players.get(Byte.valueOf(var5));
                                           this.hud.addChat(var34);
                                        }
                                     } else if(var6 == PacketType.DISCONNECT) {
                                    	 this.networkManager.netHandler.close();
                                        this.setCurrentScreen(new ErrorScreen("Connection lost", (String)var7[0]));
                                     } else if(var6 == PacketType.UPDATE_PLAYER_TYPE) {
                                        this.player.userType = ((Byte)var7[0]).byteValue();
                                     }
                                  }
                               }
                            }
                         }

                         if(!this.networkManager.isConnected()) {
                            break;
                         }

                         this.networkManager.netHandler.in.compact();
                      }
                      
                      if(this.networkManager.netHandler.out.position() > 0) {
                    	  this.networkManager.netHandler.out.flip();
                    	  this.networkManager.netHandler.write();
                    	  this.networkManager.netHandler.out.compact();
                      }
                   } catch (Exception var15) {
                      this.setCurrentScreen(new ErrorScreen("Disconnected!", "You\'ve lost connection to the server"));
                      this.online = false;
                      var15.printStackTrace();
                      this.networkManager.netHandler.close();
                      this.networkManager = null;
                   }
                }
             }

             Player var28 = this.player;
             if(this.networkManager.levelLoaded) {
                int var24 = (int)(var28.x * 32.0F);
                var4 = (int)(var28.y * 32.0F);
                var40 = (int)(var28.z * 32.0F);
                var46 = (int)(var28.yRot * 256.0F / 360.0F) & 255;
                var45 = (int)(var28.xRot * 256.0F / 360.0F) & 255;
                this.networkManager.netHandler.send(PacketType.POSITION_ROTATION, new Object[]{Integer.valueOf(-1), Integer.valueOf(var24), Integer.valueOf(var4), Integer.valueOf(var40), Integer.valueOf(var46), Integer.valueOf(var45)});
             }
          } else if(this.networkManager.didConnectionClose()) {
        	  this.setCurrentScreen(new ErrorScreen("Disconnected!", "You\'ve lost connection to the server"));
          }
       }
      
      if(this.currentScreen == null && this.player != null && this.player.health <= 0) {
         this.setCurrentScreen((GuiScreen)null);
      }

      if(this.currentScreen == null || this.currentScreen.grabsMouse) {
         int var25;
         while(Mouse.next()) {
            if((var25 = Mouse.getEventDWheel()) != 0) {
               this.player.inventory.swapPaint(var25);
            }

            if(this.currentScreen == null) {
               if(!this.hasMouse && Mouse.getEventButtonState()) {
                  this.grabMouse();
               } else {
                  if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
                     this.onMouseClick(0);
                     this.lastClick = this.ticks;
                  }

                  if(Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
                     this.onMouseClick(1);
                     this.lastClick = this.ticks;
                  }

                  if(Mouse.getEventButton() == 2 && Mouse.getEventButtonState() && this.selected != null) {
                     if((var16 = this.level.getTile(this.selected.x, this.selected.y, this.selected.z)) == Block.GRASS.id) {
                        var16 = Block.DIRT.id;
                     }

                     if(var16 == Block.DOUBLE_SLAB.id) {
                        var16 = Block.SLAB.id;
                     }

                     if(var16 == Block.BEDROCK.id) {
                        var16 = Block.STONE.id;
                     }

                     this.player.inventory.grabTexture(var16, this.gamemode instanceof CreativeGameMode);
                  }
               }
            }

            if(this.currentScreen != null) {
               this.currentScreen.mouseEvent();
            }
         }

         if(this.blockHitTime > 0) {
            --this.blockHitTime;
         }

         while(Keyboard.next()) {
            this.player.setKey(Keyboard.getEventKey(), Keyboard.getEventKeyState());
            if(Keyboard.getEventKeyState()) {
               if(this.currentScreen != null) {
                  this.currentScreen.keyboardEvent();
               }

               if(this.currentScreen == null) {
                  if(Keyboard.getEventKey() == 1) {
                     this.pause();
                  }

                  if(this.gamemode instanceof CreativeGameMode) {
                     if(Keyboard.getEventKey() == this.settings.loadLocationKey.key) {
                        this.player.resetPos();
                     }

                     if(Keyboard.getEventKey() == this.settings.saveLocationKey.key) {
                        this.level.setSpawnPos((int)this.player.x, (int)this.player.y, (int)this.player.z, this.player.yRot);
                        this.player.resetPos();
                     }
                  }

                  Keyboard.getEventKey();
                  if(Keyboard.getEventKey() == 63) {
                     this.raining = !this.raining;
                  }

                  if(Keyboard.getEventKey() == 15 && this.gamemode instanceof SurvivalGameMode && this.player.arrows > 0) {
                     this.level.addEntity(new Arrow(this.level, this.player, this.player.x, this.player.y, this.player.z, this.player.yRot, this.player.xRot, 1.2F));
                     --this.player.arrows;
                  }

                  if(Keyboard.getEventKey() == this.settings.buildKey.key) {
                     this.gamemode.openInventory();
                  }
                  
                  if(Keyboard.getEventKey() == this.settings.chatKey.key && this.networkManager != null && this.networkManager.isConnected()) {
                      this.player.releaseAllKeys();
                      this.setCurrentScreen(new ChatInputScreen());
                   }
                  
                  if(Keyboard.getEventKey() == this.settings.gameModeKey.key) {
                	  if(!this.isOnline()) {
                		  if(this.gamemode instanceof SurvivalGameMode) {
                			  GameMode gamemode = new CreativeGameMode(this);
                			  gamemode.apply(this.level);
                			  this.gamemode = gamemode;
                		  } else {
                			  GameMode gamemode = new SurvivalGameMode(this);
                			  gamemode.apply(this.level);
                			  this.gamemode = gamemode;
                		  }
                	  }
                  }
               }

               for(var25 = 0; var25 < 9; ++var25) {
                  if(Keyboard.getEventKey() == var25 + 2) {
                     this.player.inventory.selected = var25;
                  }
               }

               if(Keyboard.getEventKey() == this.settings.toggleFogKey.key) {
                  this.settings.toggleSetting(4, !Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)?1:-1);
               }
            }
         }

         if(this.currentScreen == null) {
            if(Mouse.isButtonDown(0) && (float)(this.ticks - this.lastClick) >= this.timer.tps / 4.0F && this.hasMouse) {
               this.onMouseClick(0);
               this.lastClick = this.ticks;
            }

            if(Mouse.isButtonDown(1) && (float)(this.ticks - this.lastClick) >= this.timer.tps / 4.0F && this.hasMouse) {
               this.onMouseClick(1);
               this.lastClick = this.ticks;
            }
         }

         boolean var26 = this.currentScreen == null && Mouse.isButtonDown(0) && this.hasMouse;
         boolean var35 = false;
         if(!this.gamemode.instantBreak && this.blockHitTime <= 0) {
            if(var26 && this.selected != null && this.selected.entityPos == 0) {
               var4 = this.selected.x;
               var40 = this.selected.y;
               var46 = this.selected.z;
               this.gamemode.hitBlock(var4, var40, var46, this.selected.face);
            } else {
               this.gamemode.resetHits();
            }
         }
      }

      if(this.currentScreen != null) {
         this.lastClick = this.ticks + 10000;
      }
      

      if(this.currentScreen != null) {
         this.currentScreen.doInput();
         if(this.currentScreen != null) {
            this.currentScreen.tick();
         }
      }
      
      if(this.level != null) {
         Renderer var29 = this.renderer;
         ++this.renderer.levelTicks;
         HeldBlock var41 = var29.heldBlock;
         var29.heldBlock.lastPos = var41.pos;
         if(var41.moving) {
            ++var41.offset;
            if(var41.offset == 7) {
               var41.offset = 0;
               var41.moving = false;
            }
         }

         Player var27 = var41.minecraft.player;
         var4 = var41.minecraft.player.inventory.getSelected();
         Block var43 = null;
         if(var4 > 0) {
            var43 = Block.blocks[var4];
         }

         float var48 = 0.4F;
         float var50;
         if((var50 = (var43 == var41.block?1.0F:0.0F) - var41.pos) < -var48) {
            var50 = -var48;
         }

         if(var50 > var48) {
            var50 = var48;
         }

         var41.pos += var50;
         if(var41.pos < 0.1F) {
            var41.block = var43;
         }

         if(var29.minecraft.raining) {
            Renderer var39 = var29;
            var27 = var29.minecraft.player;
            Level var32 = var29.minecraft.level;
            var40 = (int)var27.x;
            var46 = (int)var27.y;
            var45 = (int)var27.z;

            for(var8 = 0; var8 < 50; ++var8) {
               int var60 = var40 + var39.random.nextInt(9) - 4;
               int var52 = var45 + var39.random.nextInt(9) - 4;
               int var57;
               if((var57 = var32.getHighestTile(var60, var52)) <= var46 + 4 && var57 >= var46 - 4) {
                  float var56 = var39.random.nextFloat();
                  float var62 = var39.random.nextFloat();
                  var39.minecraft.particleManager.spawnParticle(new WaterDropParticle(var32, (float)var60 + var56, (float)var57 + 0.1F, (float)var52 + var62));
               }
            }
         }

         LevelRenderer var31 = this.levelRenderer;
         ++this.levelRenderer.ticks;
         this.level.tickEntities();
         if(!this.isOnline()) {
             this.level.tick();
          }

         this.particleManager.tick();
      }

   }
   
   public int ticksUntilSave = 600;

	private void levelSave() {
		if(this.isOnline()) {
			return;
		}
		
		if(this.level == null) {
			ticksUntilSave = this.ticks + 600;
		}
		
		if(this.ticks >= this.ticksUntilSave) {
			LevelUtils.save();
			ticksUntilSave = this.ticks + 600;
		}
	}
	
	public final boolean isOnline() {
		return this.networkManager != null;
	}

   public final void generateLevel(int var1) {
	  LevelStorageManager.deleteLevelData();
      String var2 = this.session != null?this.session.username:"anonymous";
      Level var4 = (new LevelGenerator(this.progressBar)).generate(var2, 128 << var1, 128 << var1, 64);
      this.gamemode.prepareLevel(var4);
      this.setLevel(var4);
      LevelUtils.save();
   }

   public final void setLevel(Level var1) {
      this.level = var1;
      if(var1 != null) {
         var1.initTransient();
         this.gamemode.apply(var1);
         var1.font = this.fontRenderer;
         var1.rendererContext$5cd64a7f = this;
         if(!this.isOnline()) {
             this.player = (Player)var1.findSubclassOf(Player.class);
          } else if(this.player != null) {
             this.player.resetPos();
             this.gamemode.preparePlayer(this.player);
             if(var1 != null) {
                var1.player = this.player;
                var1.addEntity(this.player);
             }
          }
      }

      if(this.player == null) {
    	  this.player = new Player(var1);
    	  this.player.resetPos();
    	  boolean b = LevelUtils.loadPlayer(this.player) && !this.isOnline();
    	  if(!b) {
    		  this.gamemode.preparePlayer(this.player);
    	  }
    	  if(var1 != null) {
    		  var1.player = this.player;
    	  }
      }
      
      if(this.player != null) {
          this.player.input = new InputHandlerImpl(this.settings);
          this.gamemode.apply(this.player);
       }

      if(this.levelRenderer != null) {
         LevelRenderer var3 = this.levelRenderer;
         if(this.levelRenderer.level != null) {
            var3.level.removeListener(var3);
         }

         var3.level = var1;
         if(var1 != null) {
            var1.addListener(var3);
            var3.refresh();
         }
      }

      if(this.particleManager != null) {
         ParticleManager var5 = this.particleManager;
         if(var1 != null) {
            var1.particleEngine = var5;
         }

         for(int var4 = 0; var4 < 2; ++var4) {
            var5.particles[var4].clear();
         }
      }

      System.gc();
   }
   
   public void startNetworkManager(String serverIP, SessionData session) {
	   this.gamemode = new CreativeGameMode(this);
	   this.networkManager = new NetworkManager(this, serverIP, session.username, session.mppass);
	   this.setCurrentScreen(new GuiConnecting(serverIP, session.username, session.mppass));
	   this.server = serverIP;
	   this.session = session;
   }
   
   public static Minecraft getMinecraft() {
	   return mc;
   }
}
