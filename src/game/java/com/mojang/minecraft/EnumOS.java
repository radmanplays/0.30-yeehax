package com.mojang.minecraft;

public enum EnumOS
{
	linux("linux", 0),
	solaris("solaris", 1),
	windows("windows", 2),
	macos("macos", 3),
	unknown("unknown", 4);

	private static final EnumOS[] values = new EnumOS[] {linux, solaris, windows, macos, unknown};

	private EnumOS(String name, int id)
	{
	}
}
