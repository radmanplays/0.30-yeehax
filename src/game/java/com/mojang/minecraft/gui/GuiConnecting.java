package com.mojang.minecraft.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.net.PacketType;

import net.lax1dude.eaglercraft.Display;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.PlatformNetworking;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

public class GuiConnecting extends GuiScreen {
	
	private int timer = 0;
	private String currentAddress;
	private String username;
	private String key;
	private boolean connected = false;
	private Minecraft minecraft;
	
	private String title;
	private String message;
	private int progress = -1;
	
	private long start = EagRuntime.steadyTimeMillis();
	
	private Logger logger = LogManager.getLogger();
	
	public GuiConnecting(String addr, String username, String key) {
		this.currentAddress = addr;
		this.username = username;
		this.key = key;
		minecraft = Minecraft.getMinecraft();
		
		if(currentAddress.contains("ws://")) {
			if(EagRuntime.requireSSL()) {
				currentAddress = currentAddress.replace("ws://", "wss://");
			}
		} else if(!currentAddress.contains("://")) {
			currentAddress = EagRuntime.requireSSL() ? "wss://" + currentAddress : "ws://" + currentAddress;
		}
	}
	
	boolean b = false;
	public void tick() {
		++timer;
		if(timer > 1) {
			if(this.minecraft.networkManager.netHandler.webSocket == null) {
				logger.info("Connecting to: {}", currentAddress);
				this.minecraft.networkManager.netHandler.webSocket = PlatformNetworking.openWebSocket(currentAddress);
				if(this.minecraft.networkManager.netHandler.webSocket == null) {
					minecraft.online = false;
					minecraft.networkManager = null;
					this.minecraft.setCurrentScreen(new ErrorScreen("Failed to connect", "Could not open websocket to \"" + this.currentAddress + "\"!"));
				}
			} else {
				if(!this.minecraft.networkManager.isConnected() && !b) {
					this.setText("Connecting..", null);
					b = true;
				}
				
				if (this.minecraft.networkManager.netHandler.webSocket.getState() == EnumEaglerConnectionState.CONNECTED) {
					if(!this.minecraft.networkManager.successful) {
						this.minecraft.networkManager.netHandler.send(PacketType.IDENTIFICATION, new Object[]{Byte.valueOf((byte)7), username, key, Integer.valueOf(0)});
						this.minecraft.networkManager.successful = true;
						this.connected = true;
					}
				} else if (this.minecraft.networkManager.netHandler.webSocket.getState() == EnumEaglerConnectionState.FAILED) {
					if(this.currentAddress.contains("ws://") && !EagRuntime.requireSSL()) {
						if (this.minecraft.networkManager.netHandler.webSocket != null) {
							this.minecraft.networkManager.netHandler.webSocket.close();
							this.minecraft.networkManager.netHandler.webSocket = null;
						}
						currentAddress = currentAddress.replace("ws://", "wss://");
						timer = 0;
					} else {
						minecraft.online = false;
						minecraft.networkManager = null;
						minecraft.setCurrentScreen(new ErrorScreen("Failed to connect", "You failed to connect to the server. It\'s probably down!"));
					}
				}
			}
			if(timer > 200 && !this.connected) {
				if (this.minecraft.networkManager.netHandler.webSocket != null) {
					this.minecraft.networkManager.netHandler.webSocket.close();
				}
				minecraft.online = false;
				minecraft.networkManager = null;
				minecraft.setCurrentScreen(new ErrorScreen("Failed to connect", "Connection timed out"));
			}
		}
	}
	
	public void setText(String title, String message) {
		this.title = title;
		this.message = message;
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	@Override
	public void render(int i, int i2) {
		int var31 = this.minecraft.width * 240 / this.minecraft.height;
		int var21 = this.minecraft.height * 240 / this.minecraft.height;
		GL11.glClear(256);
		GL11.glMatrixMode(5889);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)var31, (double)var21, 0.0D, 100.0D, 300.0D);
		GL11.glMatrixMode(5888);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		
		long var2;
        if((var2 = EagRuntime.steadyTimeMillis()) - this.start < 0L || var2 - this.start >= 20L) {
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
           var6.pos(0.0F, (float)var5, 0.0F).tex(0.0F, (float)var5 / var10).color(4210752).endVertex();
           var6.pos((float)var4, (float)var5, 0.0F).tex((float)var4 / var10, (float)var5 / var10).color(4210752).endVertex();
           var6.pos((float)var4, 0.0F, 0.0F).tex((float)var4 / var10, 0.0F).color(4210752).endVertex();
           var6.pos(0.0F, 0.0F, 0.0F).tex(0.0F, 0.0F).color(4210752).endVertex();
           tess.draw();
           if(this.progress >= 0) {
              var7 = var4 / 2 - 50;
              int var8 = var5 / 2 + 16;
              GL11.glDisable(3553);
              var6.begin(7, VertexFormat.POSITION_COLOR);
              var6.pos((float)var7, (float)var8, 0.0F).color(8421504).endVertex();
              var6.pos((float)var7, (float)(var8 + 2), 0.0F).color(8421504).endVertex();
              var6.pos((float)(var7 + 100), (float)(var8 + 2), 0.0F).color(8421504).endVertex();
              var6.pos((float)(var7 + 100), (float)var8, 0.0F).color(8421504).endVertex();
              var6.pos((float)var7, (float)var8, 0.0F).color(8454016).endVertex();
              var6.pos((float)var7, (float)(var8 + 2), 0.0F).color(8454016).endVertex();
              var6.pos((float)(var7 + this.progress), (float)(var8 + 2), 0.0F).color(8454016).endVertex();
              var6.pos((float)(var7 + this.progress), (float)var8, 0.0F).color(8454016).endVertex();
              tess.draw();
              GL11.glEnable(3553);
           }

           this.minecraft.fontRenderer.render(this.title, (var4 - this.minecraft.fontRenderer.getWidth(this.title)) / 2, var5 / 2 - 4 - 16, 16777215);
           this.minecraft.fontRenderer.render(this.message, (var4 - this.minecraft.fontRenderer.getWidth(this.message)) / 2, var5 / 2 - 4 + 8, 16777215);
           Display.update();
        }
	}
}
