package net.lax1dude.eaglercraft.opengl;

import net.lax1dude.eaglercraft.internal.IBufferArrayGL;
import net.lax1dude.eaglercraft.internal.IBufferGL;

class DisplayList {
		final int id;
		IBufferArrayGL vertexArray = null;
		IBufferGL vertexBuffer = null;
		int attribs = -1;
		int mode = -1;
		int count = 0;
		boolean bindQuad16 = false;
		boolean bindQuad32 = false;

		DisplayList(int id) {
			this.id = id;
		}
	}