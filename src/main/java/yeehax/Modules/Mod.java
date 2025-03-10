package yeehax.Modules;

public class Mod {
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
