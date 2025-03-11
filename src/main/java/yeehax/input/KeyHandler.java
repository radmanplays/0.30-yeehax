package yeehax.input;

import net.lax1dude.eaglercraft.Keyboard;
import yeehax.YeeHax;
import yeehax.Modules.Mod;

public class KeyHandler {
    public static void checkKeys() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) { 
                int key = Keyboard.getEventKey(); 
                onKey(key); 
            }
        }
    }

    public static void onKey(int key) {
        for (Mod mod : YeeHax.modManager.mods) {
            if (key == mod.getKey()) {
                mod.toggle(); 
            }
        }
    }
}
