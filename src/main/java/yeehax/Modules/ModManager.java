package yeehax.Modules;

import java.util.ArrayList;
import java.util.List;

import yeehax.Modules.impl.Fly;
import yeehax.Modules.impl.Speed;

public class ModManager {
	public static final List<Mod> mods = new ArrayList<>();
	
	public static Fly fly;
	public static Speed speed;
	
	public ModManager() {
		mods.add(fly = new Fly());
		mods.add(speed = new Speed());
	}
	
	public static void onUpdate() {
		for (Mod m : mods) {
			if (!m.enabled)
				continue;
			m.update();
		}
	}

}
