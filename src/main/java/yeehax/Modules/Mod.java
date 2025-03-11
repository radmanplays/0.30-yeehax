package yeehax.Modules;

import com.mojang.minecraft.Minecraft;

public class Mod {
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	public String name;
	public boolean enabled;
	public int key;

	public Mod(String name, int key){
		this.name = name;
		this.key = key;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getKey()
	{
		return key;
	}
	
	// %%%%%%%%%%%%%%%%%%%
	// Events
	// %%%%%%%%%%%%%%%%%%%
	public void update() {
	}
	public void onEnable() {
	}
	public void onDisable() {
	}
	
	public void toggle() {
		if (enabled) {
			onEnable();
		}else {
		onDisable();
		}
	}
	
}
