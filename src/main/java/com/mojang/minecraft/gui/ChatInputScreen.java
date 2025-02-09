package com.mojang.minecraft.gui;

import net.lax1dude.eaglercraft.Keyboard;

public final class ChatInputScreen extends GuiScreen {

   private String message = "";
   private int counter = 0;


   public final void onOpen() {
      Keyboard.enableRepeatEvents(true);
   }

   public final void onClose() {
      Keyboard.enableRepeatEvents(false);
   }

   public final void tick() {
      ++this.counter;
   }

   protected final void onKeyPress(char var1, int var2) {
      if(var2 == 1) {
         this.minecraft.setCurrentScreen((GuiScreen)null);
      } else if(var2 == 28) {
         this.minecraft.setCurrentScreen((GuiScreen)null);
      } else {
         if(var2 == 14 && this.message.length() > 0) {
            this.message = this.message.substring(0, this.message.length() - 1);
         }

         if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\\\"#%/()=+?[]{}<>@|$;".indexOf(var1) >= 0 && this.message.length() < 64 - (this.minecraft.session.username.length() + 2)) {
            this.message = this.message + var1;
         }

      }
   }

   public final void render(int var1, int var2) {
      drawBox(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      drawString(this.fontRenderer, "> " + this.message + (this.counter / 6 % 2 == 0?"_":""), 4, this.height - 12, 14737632);
   }

   protected final void onMouseClick(int var1, int var2, int var3) {
      if(var3 == 0 && this.minecraft.hud.hoveredPlayer != null) {
         if(this.message.length() > 0 && !this.message.endsWith(" ")) {
            this.message = this.message + " ";
         }

         this.message = this.message + this.minecraft.hud.hoveredPlayer;
         var1 = 64 - (this.minecraft.session.username.length() + 2);
         if(this.message.length() > var1) {
            this.message = this.message.substring(0, var1);
         }
      }

   }
}
