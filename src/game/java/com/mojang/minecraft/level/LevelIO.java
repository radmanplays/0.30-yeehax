package com.mojang.minecraft.level;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import com.mojang.minecraft.ProgressBarDisplay;

import net.lax1dude.eaglercraft.EaglerZLIB;

public final class LevelIO {

   private ProgressBarDisplay progressBar;


   public LevelIO(ProgressBarDisplay var1) {
      this.progressBar = var1;
   }

   public static byte[] decompress(InputStream var0) {
      try {
         DataInputStream var3;
         byte[] var1 = new byte[(var3 = new DataInputStream(new BufferedInputStream(EaglerZLIB.newGZIPInputStream(var0)))).readInt()];
         var3.readFully(var1);
         var3.close();
         return var1;
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }
}
