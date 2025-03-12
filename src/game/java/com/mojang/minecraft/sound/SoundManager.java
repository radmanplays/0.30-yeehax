package com.mojang.minecraft.sound;

import java.util.*;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.tile.Tile$SoundType;
import com.mojang.minecraft.player.Player;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglerInputStream;
import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.IAudioCacheLoader;
import net.lax1dude.eaglercraft.internal.IAudioHandle;
import net.lax1dude.eaglercraft.internal.IAudioResource;
import net.lax1dude.eaglercraft.internal.PlatformAudio;

public final class SoundManager {

   private Map<String, IAudioResource> sounds = new HashMap<String, IAudioResource>();
   private Map<String, IAudioResource> music = new HashMap<String, IAudioResource>();
   public Random random = new Random();
   public long lastMusic = 0;
   private int lastSongID = 0;
   
   private IAudioHandle musicHandle;
   
   public void playSound(String var2, Entity var3) {
	   try {
		   IAudioResource trk;
		   Tile$SoundType soundType = Tile$SoundType.getSoundType(var2);
		   if(var2 != null) {
			   var2 = var2.replace(".", "/");
		   }
		   String s = "/sounds/blocks/" + Tile$SoundType.mapSound(var2) + (this.random.nextInt(4) + 1) + ".ogg";
		   if(!sounds.containsKey(s)) {
			   if (EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP) {
				   trk = PlatformAudio.loadAudioDataNew(s, true, browserResourceLoader);
			   } else {
				   trk = PlatformAudio.loadAudioData(s, true);
			   }
			   if(trk != null) {
				   sounds.put(s, trk);
			   }
		   } else {
			   trk = sounds.get(s);
		   }

		   PlatformAudio.beginPlayback(trk, var3.x + 0.5f, var3.y + 0.5f, var3.z + 0.5f, soundType.getVolume(), soundType.getPitch(), false);
	   } catch(Exception e) {
		   e.printStackTrace();
	   }
   }
   
   public void playSound(String var2, float x, float y, float z) {
	   try {
		   IAudioResource trk;
		   Tile$SoundType soundType = Tile$SoundType.getSoundType(var2);
		   if(var2 != null) {
			   var2 = var2.replace(".", "/");
		   }
		   String s = "/sounds/blocks/" + Tile$SoundType.mapSound(var2) + (this.random.nextInt(4) + 1) + ".ogg";
		   if(!sounds.containsKey(s)) {
			   if (EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP) {
				   trk = PlatformAudio.loadAudioDataNew(s, true, browserResourceLoader);
			   } else {
				   trk = PlatformAudio.loadAudioData(s, true);
			   }
			   if(trk != null) {
				   sounds.put(s, trk);
			   }
		   } else {
			   trk = sounds.get(s);
		   }

		   PlatformAudio.beginPlayback(trk, x, y, z, soundType.getVolume(), soundType.getPitch(), false);
	   } catch(Exception e) {
		   e.printStackTrace();
	   }
   }

   public boolean playMusic(String var2) {
	   try {
		   IAudioResource trk;
		   int i = this.random.nextInt(3) + 1;
		   while(i == lastSongID) {
			   i = this.random.nextInt(3) + 1;
		   }
		   lastSongID = i;
		   String s = "/sounds/music/" + var2 + i + ".ogg";
		   if(!music.containsKey(s)) {
			   if (EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP) {
				   trk = PlatformAudio.loadAudioDataNew(s, false, browserResourceLoader);
			   } else {
				   trk = PlatformAudio.loadAudioData(s, false);
			   }
			   if(trk != null) {
				   music.put(s, trk);
			   }
		   } else {
			   trk = music.get(s);
		   }
	   
		   musicHandle = PlatformAudio.beginPlaybackStatic(trk, 1.0f, 1.0f, false);
		   return true;
	   } catch(Exception e) {
		   e.printStackTrace();
		   return false;
	   }
   }
   
   public void updatePosition(Player var1, float var2) {
	   if(var1 != null) {
		   try {
			   float var9 = var1.xRotO + (var1.xRot - var1.xRotO) * var2;
			   float var3 = var1.yRotO + (var1.yRot - var1.yRotO) * var2;
			   double var4 = var1.xOld + (var1.x - var1.xOld) * (double)var2;
			   double var6 = var1.yOld + (var1.y - var1.yOld) * (double)var2;
			   double var8 = var1.zOld + (var1.z - var1.zOld) * (double)var2;
			   PlatformAudio.setListener((float)var4, (float)var6, (float)var8, (float)var9, (float)var3);
		   } catch(Exception e) {
			   // eaglercraft 1.5.2 had Infinity/NaN crashes for this function which
			   // couldn't be resolved via if statement checks in the above variables
		   }
	   }
	}
   
   private final IAudioCacheLoader browserResourceLoader = filename -> {
		try {
			return EaglerInputStream.inputStreamToBytesQuiet(EagRuntime.getRequiredResourceStream(filename));
		} catch (Throwable t) {
			return null;
		}
	};
	
	public void settingsChanged() {
		if(musicHandle != null && !musicHandle.shouldFree() && !Minecraft.getMinecraft().settings.music) {
			musicHandle.end();
			this.lastMusic = EagRuntime.steadyTimeMillis();
		}
	}
}
