package com.mojang.minecraft.render;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.render.texture.TextureFX;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class TextureManager {

	public HashMap<String, Integer> textures = new HashMap<String, Integer>();
	public ByteBuffer textureBuffer = GLAllocation.createDirectByteBuffer(262144);
	public List<TextureFX> animations = new ArrayList<TextureFX>();
	public GameSettings settings;

	public TextureManager(GameSettings var1) {
		this.settings = var1;
	}

	public final int load(String var1) {
		Integer var2;
		if ((var2 = (Integer) this.textures.get(var1)) != null) {
			return var2.intValue();
		} else {
			try {
				int var4 = GL11.generateTexture();
				this.load(ImageData.loadImageFile(EagRuntime.getResourceStream(var1)).swapRB(), var4);

				this.textures.put(var1, Integer.valueOf(var4));
				return var4;
			} catch (Exception var3) {
				throw new RuntimeException("!!");
			}
		}
	}
	
	public ImageData loadImageData(String var1) {
		try {
			return ImageData.loadImageFile(EagRuntime.getResourceStream(var1));
		} catch(Exception e) {
			throw new RuntimeException("!!");
		}
	}

	public void load(ImageData var1, int var2) {
		GL11.glAlphaFunc(516, 0.1F);
		GL11.glBindTexture(3553, var2);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10241 /* GL_TEXTURE_MIN_FILTER */, 9728 /* GL_NEAREST */);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10240 /* GL_TEXTURE_MAG_FILTER */, 9728 /* GL_NEAREST */);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10242 /* GL_TEXTURE_WRAP_S */, 10497 /* GL_REPEAT */);
		GL11.glTexParameteri(3553 /* GL_TEXTURE_2D */, 10243 /* GL_TEXTURE_WRAP_T */, 10497 /* GL_REPEAT */);
		var2 = var1.width;
		int var3 = var1.height;
		int[] var4 = new int[var2 * var3];
		byte[] var5 = new byte[var2 * var3 << 2];
		var1.getRGB(0, 0, var2, var3, var4, 0, var2);

		for (int var11 = 0; var11 < var4.length; ++var11) {
			int var6 = var4[var11] >>> 24;
			int var7 = var4[var11] >> 16 & 255;
			int var8 = var4[var11] >> 8 & 255;
			int var9 = var4[var11] & 255;
			if (this.settings.anaglyph) {
				int var10 = (var7 * 30 + var8 * 59 + var9 * 11) / 100;
				var8 = (var7 * 30 + var8 * 70) / 100;
				var9 = (var7 * 30 + var9 * 70) / 100;
				var7 = var10;
			}

			var5[var11 << 2] = (byte) var7;
			var5[(var11 << 2) + 1] = (byte) var8;
			var5[(var11 << 2) + 2] = (byte) var9;
			var5[(var11 << 2) + 3] = (byte) var6;
		}

		this.textureBuffer.clear();
		this.textureBuffer.put(var5);
		this.textureBuffer.position(0).limit(var5.length);
		EaglercraftGPU.glTexImage2D(RealOpenGLEnums.GL_TEXTURE_2D, 0, RealOpenGLEnums.GL_RGBA8, var1.width, var1.height,
				0, RealOpenGLEnums.GL_RGBA, RealOpenGLEnums.GL_UNSIGNED_BYTE, (ByteBuffer) textureBuffer);
		// GL11.glTexImage2D(3553, 0, 6408, var2, var3, 0, 6408, 5121,
		// this.textureBuffer);
	}

	public final void registerAnimation(TextureFX var1) {
		this.animations.add(var1);
		var1.animate();
	}
}
