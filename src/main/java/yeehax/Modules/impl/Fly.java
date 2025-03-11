package yeehax.Modules.impl;

import net.lax1dude.eaglercraft.KeyboardConstants;
import yeehax.Modules.Mod;

public class Fly extends Mod{

	public Fly()
	{
		super("Yee Hax Fly+", KeyboardConstants.KEY_F);
	}

	@Override
	public void onEnable()
	{
		System.out.println("Yee");
		super.onEnable();
	}
}
