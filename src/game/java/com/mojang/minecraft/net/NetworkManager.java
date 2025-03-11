package com.mojang.minecraft.net;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.net.NetworkHandler;

import net.lax1dude.eaglercraft.EaglerOutputStream;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.PlatformNetworking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NetworkManager
{
	public NetworkManager(Minecraft minecraft, String server, String username, String key)
	{
		minecraft.online = true;
		this.minecraft = minecraft;
		players = new HashMap<Byte, NetworkPlayer>();
		this.netHandler = new NetworkHandler(this);
	}

	public EaglerOutputStream levelData;

	public NetworkHandler netHandler;

	public Minecraft minecraft;

	public boolean successful = false;
	public boolean levelLoaded = false;
	
	public HashMap<Byte, NetworkPlayer> players;

	public void sendBlockChange(int x, int y, int z, int mode, int block)
	{
		netHandler.send(PacketType.PLAYER_SET_BLOCK, new Object[] {x, y, z, mode, block});
	}

	public void error(Exception e)
	{
		netHandler.close();

		ErrorScreen errorScreen = new ErrorScreen("Disconnected!", e.getMessage());

		minecraft.setCurrentScreen(errorScreen);

		e.printStackTrace();
	}

	public boolean isConnected()
	{
		return netHandler != null && netHandler.webSocket != null && netHandler.webSocket.getState() == EnumEaglerConnectionState.CONNECTED;
	}
	
	public boolean didConnectionClose() {
		return successful && this.netHandler != null && netHandler.webSocket != null && netHandler.webSocket.getState() != EnumEaglerConnectionState.CONNECTED;
	}

	public List getPlayers()
	{
		ArrayList list = new ArrayList();

		list.add(minecraft.session.username);

		Iterator playerIterator = this.players.values().iterator();

		while(playerIterator.hasNext())
		{
			NetworkPlayer networkPlayer = (NetworkPlayer)playerIterator.next();

			list.add(networkPlayer.name);
		}

		return list;
	}
}