package yeehax;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.minecraft.Minecraft;

import net.lax1dude.eaglercraft.Display;

public class YeeHax {
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static Logger logger;
	
	public static void startup() {
		logger = LogManager.getLogger("YeeHax");
		logger.info("Starting YeeHax!");
		Display.setTitle("YeeHax");//dosnt work
	}
}
