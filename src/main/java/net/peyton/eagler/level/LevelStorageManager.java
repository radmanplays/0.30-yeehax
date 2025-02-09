package net.peyton.eagler.level;

import java.io.*;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.peyton.eagler.level.nbt.CompressedStreamTools;
import net.peyton.eagler.level.nbt.NBTBase;
import net.peyton.eagler.level.nbt.NBTTagCompound;

public class LevelStorageManager {
	
	public static NBTTagCompound levelStorage = null;
	
	public static void loadLevelData() throws IOException {
		VFile2 level = new VFile2("/saves/level.dat");
		
		if(level.exists()) {
			NBTBase nbtBase = CompressedStreamTools.readCompressed(level.getInputStream());
			if(nbtBase != null && nbtBase instanceof NBTTagCompound) {
				levelStorage = (NBTTagCompound)nbtBase;
			}
		} else {
			return;
		}
		
		if(levelStorage.tagMap == null) {
			levelStorage = null;
		} else if(levelStorage.tagMap.size() == 0) {
			levelStorage = null;
		}
	}
	
	public static void saveLevelData() throws IOException {
		VFile2 level = new VFile2("/saves/level.dat");
		CompressedStreamTools.writeCompressed(levelStorage, level.getOutputStream());
		if(levelStorage.tagMap == null) {
			levelStorage = null;
		} else if(levelStorage.tagMap.size() == 0) {
			levelStorage = null;
		}
	}
	
	public static void deleteLevelData() {
		new VFile2("/saves/level.dat").delete();
		levelStorage = null;
	}

}
