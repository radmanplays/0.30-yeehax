package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;

import yeehax.YeeHax;


public class InputHandlerImpl extends InputHandler {

	private boolean flyEnabled = false; 
	private boolean sneaking = false; 
	protected Minecraft mc = Minecraft.getMinecraft();
	
	public InputHandlerImpl(GameSettings gameSettings) {
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
	            mc.player.yd = 0.4F; 
	        } else if (sneaking) {
	            mc.player.yd = -0.4F; 

	        } else {
	            mc.player.yd = 0.0F; 
	        }

	        if (xxa != 0 || jumping != 0) {
	            mc.player.xd *= 1.1;
	            mc.player.zd *= 1.1;
	        }else {
	            mc.player.xd *= 0.8;
	            mc.player.zd *= 0.8;

	            // Prevent tiny movements from lasting forever
	            if (Math.abs(mc.player.xd) < 0.01) mc.player.xd = 0.0F;
	            if (Math.abs(mc.player.zd) < 0.01) mc.player.zd = 0.0F;
	        }
	    }
	    
	    if(!flyEnabled && YeeHax.modManager.noclip.isEnabled()) {
	    	mc.player.yd = 0.0F;
	    }

	    yya = keyStates[4];
	}


	@Override
	public void resetKeys() {
		for (int i = 0; i < keyStates.length; ++i) {
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

		if (index >= 0) {
			keyStates[index] = state;
		}

	    if (index >= 0) {
	        keyStates[index] = state;
	    }

		if (key == settings.flyKey.key) { 
	        if (state) {
	            YeeHax.modManager.fly.toggle();
	            flyEnabled = YeeHax.modManager.fly.isEnabled();
	            System.out.println("Fly mod " + (flyEnabled ? "enabled" : "disabled"));
	        }
	    }
		if(key == settings.flyDownKey.key) {
			sneaking = !sneaking;
		}
		if(key == settings.speed.key) {
			if (state) {
				YeeHax.modManager.speed.toggle();
			}
		}
		if(key == settings.noclip.key) {
			if (state) {
				YeeHax.modManager.noclip.toggle();
			}
		}
	}
	private boolean[] keyStates = new boolean[10];
	private GameSettings settings;
}
