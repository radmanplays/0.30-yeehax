package yeehax.Modules;

import java.util.ArrayList;
import java.util.List;

import yeehax.Modules.impl.Fly;

public class ModManager {
	public static final List<Mod> mods = new ArrayList<>();
	
	public static Fly fly;
	
	public ModManager() {
		mods.add(fly = new Fly());
	}
	
	public static void onUpdate() {
		for (Mod m : mods) {
			if (!m.enabled)
				continue;
			m.update();
		}
	}

}
