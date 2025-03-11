package com.mojang.minecraft.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.SessionData;

import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.Mouse;

public class MultiplayerMenu extends GuiScreen {
	
	boolean textBox1Active = false;
	boolean textBox2Active = false;
	private int counter = 0;
	String server = "";
	String username = "";
	Button connect;
	
	public final void onOpen() {
		Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
		this.buttons.add(connect = new Button(0, this.width / 2 - 100, this.height / 4 + 96 + 12, "Connect"));
		this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + 12, "Cancel"));
		connect.active = false;
	}
	
	public final void tick() {
		++this.counter;
	}
	
	protected final void onButtonClick(Button var1) {
		if(var1.id == 0 && var1.active) {
			Keyboard.enableRepeatEvents(false);
			this.minecraft.setCurrentScreen(new PauseScreen());
			this.minecraft.setLevel(null);
			this.minecraft.startNetworkManager(this.server, new SessionData(username, "mcpass"));
		} else if(var1.id == 1) {
			Keyboard.enableRepeatEvents(false);
			minecraft.setCurrentScreen(new PauseScreen());
		}
	}
	
	protected void onMouseClick(int var1, int var2, int var3) {
		if(var3 == 0) {
			if(var1 >= this.width / 2 - 100 && var1 < (this.width / 2 - 100) + 200 && var2 >= this.height / 4 - 10 + 50 + 18 && var2 < (this.height / 4 - 10 + 50 + 18) + 20) {
				Keyboard.enableRepeatEvents(true);
				textBox1Active = true;
				textBox2Active = false;
			} else if(var1 >= this.width / 2 - 100 && var1 < (this.width / 2 - 100) + 200 && var2 >= this.height / 4 - 10 + 50 - 20 && var2 < (this.height / 4 - 10 + 50 - 20) + 20) {
				Keyboard.enableRepeatEvents(true);
				textBox2Active = true;
				textBox1Active = false;
			} else {
				Keyboard.enableRepeatEvents(false);
				textBox1Active = false;
				textBox2Active = false;
			}
		}
		super.onMouseClick(var1, var2, var3);
	}
	
	protected final void onKeyPress(char var1, int var2) {
		if(textBox1Active) {
			if(var2 == 14 && this.server.length() > 0) {
				this.server = this.server.substring(0, this.server.length() - 1);
			}
			if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\\\"#%/()=+?[]{}<>@|$;".indexOf(var1) >= 0 && this.server.length() < 64) {
				this.server = this.server + var1;
			}
			if(server.length() > 0 && username.length() > 0) {
				connect.active = true;
			} else {
				connect.active = false;
			}
		} else if(textBox2Active) {
			if(var2 == 14 && this.username.length() > 0) {
				this.username = this.username.substring(0, this.username.length() - 1);
			}
			if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\\\"#%/()=+?[]{}<>@|$;".indexOf(var1) >= 0 && this.username.length() < 64) {
				this.username = this.username + var1;
			}
			if(server.length() > 0 && username.length() > 0) {
				connect.active = true;
			} else {
				connect.active = false;
			}
		}
	}
	
	public final void render(int var1, int var2) {
		//username
		drawBox((this.width / 2 - 100) - 1, (this.height / 4 - 10 + 50 - 20) - 1, (this.width / 2 - 100) + 200 + 1, (this.height / 4 - 10 + 50 - 20) + 20 + 1, -6250336);
		drawBox(this.width / 2 - 100, this.height / 4 - 10 + 50 - 20, (this.width / 2 - 100) + 200, (this.height / 4 - 10 + 50 - 20) + 20, -16777216);
		
		drawString(this.fontRenderer, "Username:", this.width / 2 - 100, this.height / 4 - 10 + 50 - 30, 10526880);
		
		//server IP
		drawBox((this.width / 2 - 100) - 1, (this.height / 4 - 10 + 50 + 18) - 1, (this.width / 2 - 100) + 200 + 1, (this.height / 4 - 10 + 50 + 18) + 20 + 1, -6250336);
		drawBox(this.width / 2 - 100, this.height / 4 - 10 + 50 + 18, (this.width / 2 - 100) + 200, (this.height / 4 - 10 + 50 + 18) + 20, -16777216);
		
		drawString(this.fontRenderer, "Server address:", this.width / 2 - 100, this.height / 4 - 10 + 50 + 8, 10526880);
		if(textBox1Active) {
			boolean e = this.counter / 6 % 2 == 0;
			drawString(this.fontRenderer, server + (e ? "_" : ""), (this.width / 2 - 100) + 4, (this.height / 4 - 10 + 50 + 18) + (20 - 8) / 2, 14737632);
		} else {
			drawString(this.fontRenderer, server, (this.width / 2 - 100) + 4, (this.height / 4 - 10 + 50 + 18) + (20 - 8) / 2, 14737632);
		}
		if(textBox2Active) {
			boolean e = this.counter / 6 % 2 == 0;
			drawString(this.fontRenderer, username + (e ? "_" : ""), (this.width / 2 - 100) + 4, (this.height / 4 - 10 + 50 - 20) + (20 - 8) / 2, 14737632);
		} else {
			drawString(this.fontRenderer, username, (this.width / 2 - 100) + 4, (this.height / 4 - 10 + 50 - 20) + (20 - 8) / 2, 14737632);
		}
		
		drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
		drawCenteredString(this.fontRenderer, "Multiplayer Menu", this.width / 2, 40, 16777215);
		super.render(var1, var2);
	}

}
