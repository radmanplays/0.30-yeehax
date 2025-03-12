package com.mojang.minecraft.particle;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.TextureManager;
import java.util.ArrayList;
import java.util.List;

public final class ParticleManager {

	public List<List<Particle>> particles = new ArrayList<List<Particle>>();
	public TextureManager textureManager;

	public ParticleManager(Level var1, TextureManager var2) {
		if (var1 != null) {
			var1.particleEngine = this;
		}

		this.textureManager = var2;

		for (int var3 = 0; var3 < 2; ++var3) {
			this.particles.add(var3, new ArrayList<Particle>());
		}

	}

	public final void spawnParticle(Entity var1) {
		Particle var3;
		int var2 = (var3 = (Particle) var1).getParticleTexture();
		this.particles.get(var2).add(var3);
	}

	public final void tick() {
		for (int var1 = 0; var1 < 2; ++var1) {
			for (int var2 = 0; var2 < this.particles.get(var1).size(); ++var2) {
				Particle var3;
				(var3 = (Particle) this.particles.get(var1).get(var2)).tick();
				if (var3.removed) {
					this.particles.get(var1).remove(var2--);
				}
			}
		}

	}
}
