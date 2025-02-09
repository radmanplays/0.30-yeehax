package com.mojang.minecraft;

import com.mojang.util.MathHelper;

public class ScaledResolution {
	private final double scaledWidthD;
	private final double scaledHeightD;
	private int scaledWidth;
	private int scaledHeight;
	private int scaleFactor;

	public ScaledResolution(Minecraft minecraftClient) {
		this.scaledWidth = minecraftClient.width;
		this.scaledHeight = minecraftClient.height;
		this.scaleFactor = 1;
		int i = 1000;

		while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320
				&& this.scaledHeight / (this.scaleFactor + 1) >= 240) {
			++this.scaleFactor;
		}

		this.scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
		this.scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
		this.scaledWidth = MathHelper.ceil(this.scaledWidthD);
		this.scaledHeight = MathHelper.ceil(this.scaledHeightD);
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public int getScaledHeight() {
		return this.scaledHeight;
	}

	public double getScaledWidth_double() {
		return this.scaledWidthD;
	}

	public double getScaledHeight_double() {
		return this.scaledHeightD;
	}

	public int getScaleFactor() {
		return this.scaleFactor;
	}
}