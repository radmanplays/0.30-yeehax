package net.peyton.eagler.level;

import java.io.IOException;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.player.Inventory;
import com.mojang.minecraft.player.Player;

import net.peyton.eagler.level.nbt.NBTTagCompound;
import net.peyton.eagler.level.nbt.NBTTagList;
import net.peyton.eagler.level.nbt.NBTTagShort;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.phys.AABB;

public class LevelUtils {

	public static float x, y, z, rotY, rotX;

	public static final boolean loadPlayer(Player player) {
		if (player == null) {
			return false;
		}
		NBTTagCompound var13 = LevelStorageManager.levelStorage;

		if (var13 == null) {
			return false;
		}

		NBTTagCompound var2 = var13.getCompoundTag("Player");

		player.rot = var2.getFloat("rot");
		player.timeOffs = var2.getFloat("timeOffs");
		player.speed = var2.getFloat("speed");
		player.rotA = var2.getFloat("rotA");
		player.rotOffs = var2.getFloat("rotOffs");
		player.deathScore = var2.getInteger("deathScore");
		player.renderOffset = var2.getFloat("renderOffset");
		player.health = var2.getInteger("health");
		player.lastHealth = var2.getInteger("lastHealth");
		player.airSupply = var2.getInteger("airSupply");
		player.hurtTime = var2.getInteger("hurtTime");
		player.hurtDuration = var2.getInteger("hurtDuration");
		player.hurtDir = var2.getFloat("hurtDir");
		player.deathTime = var2.getInteger("deathTime");
		player.attackTime = var2.getInteger("attackTime");
		player.oTilt = var2.getFloat("oTilt");
		player.tilt = var2.getFloat("tilt");
		player.dead = var2.getBoolean("dead");
		player.xo = var2.getFloat("xo");
		player.yo = var2.getFloat("yo");
		player.zo = var2.getFloat("zo");
		x = var2.getFloat("x");
		y = var2.getFloat("y");
		z = var2.getFloat("z");
		player.x = x;
		player.y = y;
		player.z = z;
		float var4 = 0.6F / 2.0F;
		float var5 = 1.8F / 2.0F;
		player.bb = new AABB(x - var4, y - var5, z - var4, x + var4, y + var5, z + var4);
		player.xd = var2.getFloat("xd");
		player.yd = var2.getFloat("yd");
		player.zd = var2.getFloat("zd");
		rotY = var2.getFloat("yRot");
		rotX = var2.getFloat("xRot");
		player.xRot = rotX;
		player.yRot = rotY;
		player.yRotO = var2.getFloat("yRotO");
		player.xRotO = var2.getFloat("xRotO");
		player.onGround = var2.getBoolean("onGround");
		player.horizontalCollision = var2.getBoolean("horizontalCollision");
		player.collision = var2.getBoolean("collision");
		player.slide = var2.getBoolean("slide");
		player.fallDistance = var2.getFloat("fallDistance");
		player.xOld = var2.getFloat("xOld");
		player.yOld = var2.getFloat("yOld");
		player.zOld = var2.getFloat("zOld");
		player.noPhysics = var2.getBoolean("noPhysics");
		player.pushthrough = var2.getFloat("pushthrough");
		player.hovered = var2.getBoolean("hovered");
		player.score = var2.getInteger("score");

		player.inventory = new Inventory();
		for (int i = 0; i < 9; i++) {
			player.inventory.slots[i] = var2.getInteger("inv-" + i);
			player.inventory.count[i] = var2.getInteger("inv-" + i + "-size");
		}

		return true;
	}

	public static final Level load() {
		NBTTagCompound var13 = LevelStorageManager.levelStorage;
		if (var13 == null) {
			return null;
		}
		NBTTagCompound var2 = var13.getCompoundTag("About");
		NBTTagCompound var3 = var13.getCompoundTag("Map");
		NBTTagCompound var4 = var13.getCompoundTag("Environment");
		Level level = new Level();
		level.creator = "Player";
		level.name = "Level";
		level.createTime = var2.getLong("CreatedOn");

		level.width = var3.getShort("Width");
		level.height = var3.getShort("Height");
		level.depth = var3.getShort("Depth");
		level.blocks = var3.getByteArray("Blocks");
		level.creativeMode = var3.getBoolean("CreativeMode");

		level.cloudColor = var4.getInteger("CloudColor");
		level.skyColor = var4.getInteger("SkyColor");
		level.fogColor = var4.getInteger("FogColor");
		level.waterLevel = var4.getInteger("SurroundingWaterHeight");

		return level;
	}

	public static void save() {

		if (Minecraft.getMinecraft().level == null || Minecraft.getMinecraft().networkManager != null) {
			return;
		}

		Level var1 = Minecraft.getMinecraft().level;
		NBTTagCompound var3 = new NBTTagCompound();

		var3.setInteger("CloudColor", var1.cloudColor);
		var3.setInteger("SkyColor", var1.skyColor);
		var3.setInteger("FogColor", var1.fogColor);
		var3.setShort("SurroundingGroundHeight", (short) var1.getGroundLevel());
		var3.setShort("SurroundingWaterHeight", (short) var1.waterLevel);
		var3.setByte("SurroundingGroundType", (byte) Block.GRASS.id);
		var3.setByte("SurroundingWaterType", (byte) Block.WATER.id);

		NBTTagCompound var4 = new NBTTagCompound();
		var4.setShort("Width", (short) var1.width);
		var4.setShort("Height", (short) var1.height);
		var4.setShort("Depth", (short) var1.depth);
		var4.setByteArray("Blocks", var1.blocks);
		var4.setBoolean("CreativeMode", var1.creativeMode);

		NBTTagList var5 = new NBTTagList();
		var5.appendTag(new NBTTagShort((short) var1.xSpawn));
		var5.appendTag(new NBTTagShort((short) var1.ySpawn));
		var5.appendTag(new NBTTagShort((short) var1.zSpawn));
		var4.setTag("Spawn", var5);

		NBTTagCompound var15 = new NBTTagCompound();
		var15.setString("Author", "Player");
		var15.setString("Name", "Level");
		var15.setLong("CreatedOn", var1.createTime);

		NBTTagCompound var6 = new NBTTagCompound();
		Player player = Minecraft.getMinecraft().player;

		var6.setFloat("rot", player.rot);
		var6.setFloat("timeOffs", player.timeOffs);
		var6.setFloat("speed", player.speed);
		var6.setFloat("rotA", player.rotA);
		var6.setFloat("rotOffs", player.rotOffs);
		var6.setInteger("deathScore", player.deathScore);
		var6.setFloat("renderOffset", player.renderOffset);
		var6.setInteger("health", player.health);
		var6.setInteger("lastHealth", player.lastHealth);
		var6.setInteger("airSupply", player.airSupply);
		var6.setInteger("hurtTime", player.hurtTime);
		var6.setInteger("hurtDuration", player.hurtDuration);
		var6.setFloat("hurtDir", player.hurtDir);
		var6.setInteger("deathTime", player.deathTime);
		var6.setInteger("attackTime", player.attackTime);
		var6.setFloat("oTilt", player.oTilt);
		var6.setFloat("tilt", player.tilt);
		var6.setBoolean("dead", player.dead);
		var6.setFloat("xo", player.xo);
		var6.setFloat("yo", player.yo);
		var6.setFloat("zo", player.zo);
		var6.setFloat("x", player.x);
		var6.setFloat("y", player.y);
		var6.setFloat("z", player.z);
		var6.setFloat("xd", player.xd);
		var6.setFloat("yd", player.yd);
		var6.setFloat("zd", player.zd);
		var6.setFloat("yRot", player.yRot);
		var6.setFloat("xRot", player.xRot);
		var6.setFloat("yRotO", player.yRotO);
		var6.setFloat("xRotO", player.xRotO);
		var6.setBoolean("onGround", player.onGround);
		var6.setBoolean("horizontalCollision", player.horizontalCollision);
		var6.setBoolean("collision", player.collision);
		var6.setBoolean("slide", player.slide);
		var6.setFloat("fallDistance", player.fallDistance);
		var6.setFloat("xOld", player.xOld);
		var6.setFloat("yOld", player.yOld);
		var6.setFloat("zOld", player.zOld);
		var6.setBoolean("noPhysics", player.noPhysics);
		var6.setFloat("pushthrough", player.pushthrough);
		var6.setBoolean("hovered", player.hovered);
		var6.setInteger("score", player.score);

		for (int i = 0; i < 9; i++) {
			var6.setInteger("inv-" + i, player.inventory.slots[i]);
			var6.setInteger("inv-" + i + "-size", player.inventory.count[i]);
		}

		NBTTagCompound var18 = new NBTTagCompound();
		var18.setTag("About", var15);
		var18.setTag("Map", var4);
		var18.setTag("Environment", var3);
		var18.setTag("Player", var6);

		LevelStorageManager.levelStorage = var18;
		try {
			LevelStorageManager.saveLevelData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
