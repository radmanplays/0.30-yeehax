package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;

import yeehax.YeeHax;


public class InputHandlerImpl extends InputHandler
{
	private boolean flyEnabled = false; 
	protected Minecraft mc = Minecraft.getMinecraft();

	
	public InputHandlerImpl(GameSettings gameSettings)
	{
		settings = gameSettings;
	}
	@Override
	public void updateMovement() {
	    xxa = 0.0F;
	    jumping = 0.0F;

	    if (keyStates[0]) { 
	        jumping--;
	    }

	    if (keyStates[1]) { 
	        jumping++;
	    }

	    if (keyStates[2]) {
	        xxa--;
	    }

	    if (keyStates[3]) { 
	        xxa++;
	    }

	    if (flyEnabled) {
	        if (keyStates[4]) { 
	            mc.player.yd = 0.2F; 
		        mc.player.speed = 10.0F;
	        } else if (keyStates[5]) {
	            mc.player.yd = -0.2F; 
		        mc.player.speed = 10.0F;

	        } else {
	            mc.player.yd = 0.0F; 
	        }
	    }

	    yya = keyStates[4];
	}


	@Override
	public void resetKeys()
	{
		for (int i = 0; i < keyStates.length; ++i)
		{
			keyStates[i] = false;
		}

	}

	@Override
	public void setKeyState(int key, boolean state) {
	    byte index = -1;

	    if (key == settings.forwardKey.key) {
	        index = 0;
	    }

	    if (key == settings.backKey.key) {
	        index = 1;
	    }

	    if (key == settings.leftKey.key) {
	        index = 2;
	    }

	    if (key == settings.rightKey.key) {
	        index = 3;
	    }

	    if (key == settings.jumpKey.key) {
	        index = 4;
	    }

	  
	    if (key == settings.flyKey.key) { 
	        if (state) {
	            flyEnabled = !flyEnabled;
	            YeeHax.modManager.fly.toggle();
	            System.out.println("Fly mod " + (flyEnabled ? "enabled" : "disabled"));
	        }
	    }

	    if (index >= 0) {
	        keyStates[index] = state;
	    }
	}
	private boolean[] keyStates = new boolean[10];
	private GameSettings settings;
}
