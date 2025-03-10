package yeehax.Modules;

import java.util.ArrayList;
import java.util.List;

public class ModManager {
	public static final List<Mod> Mods = new ArrayList<>();
	
	
	public static void onUpdate() {
		for (Mod m : Mods) {
			if (!m.enabled)
				continue;
			m.update();
		}
	}
}
