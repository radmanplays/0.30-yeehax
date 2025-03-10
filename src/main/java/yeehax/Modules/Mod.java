package yeehax.Modules;

import com.mojang.minecraft.Minecraft;

public class Mod {
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	public String name;
	public boolean enabled;

	public Mod(String name){
		this.name = name;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	// %%%%%%%%%%%%%%%%%%%
	// Events
	// %%%%%%%%%%%%%%%%%%%
	public void update() {
	}
}
