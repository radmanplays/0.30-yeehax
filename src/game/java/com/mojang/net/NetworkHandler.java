package com.mojang.net;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.PlatformNetworking;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public final class NetworkHandler {

   public ByteBuffer in = ByteBuffer.allocate(1048576);
   public ByteBuffer out = ByteBuffer.allocate(1048576);
   public NetworkManager netManager;
   private byte[] stringBytes = new byte[64];


   public NetworkHandler(String server) {
	   if(server.contains("ws://")) {
		   if(EagRuntime.requireSSL()) {
			   server = server.replace("ws://", "wss://");
		   }
	   } else if(!server.contains("://")) {
		   server = EagRuntime.requireSSL() ? "wss://" + server : "ws://" + server;
	   }
	   
	   this.in.clear();
	   this.out.clear();
	   
	   PlatformNetworking.startPlayConnection(server);
		   
	   boolean connected = false;
	   boolean connectionFailed = false;
	   while(!connected && !connectionFailed) {
		   EnumEaglerConnectionState state = PlatformNetworking.playConnectionState();
		   if(state == EnumEaglerConnectionState.FAILED) {
			   connectionFailed = true;
		   } else if(state == EnumEaglerConnectionState.CONNECTED) {
			   connected = true;
		   }
	   }
	   
	   if(connectionFailed && server.contains("ws://")) {
		   server = server.replace("ws://", "wss://");
	   }
	   
	   connected = false;
	   connectionFailed = false;
	   PlatformNetworking.startPlayConnection(server);
	   
	   while(!connected && !connectionFailed) {
		   EnumEaglerConnectionState state = PlatformNetworking.playConnectionState();
		   if(state == EnumEaglerConnectionState.FAILED) {
			   connectionFailed = true;
		   } else if(state == EnumEaglerConnectionState.CONNECTED) {
			   connected = true;
		   }
	   }
   }

   public final void close() {
	   PlatformNetworking.playDisconnect();

	   this.netManager.isConnected();
   }

   public final void send(PacketType var1, Object ... var2) {
      if(this.netManager.isConnected()) {
         this.out.put(var1.opcode);

         for(int var3 = 0; var3 < var2.length; ++var3) {
            Class<?> var10001 = var1.params[var3];
            Object var4 = var2[var3];
            Class<?> var5 = var10001;
            NetworkHandler var6 = this;
            if(this.netManager.isConnected()) {
               try {
                  if(var5 == Long.TYPE) {
                     var6.out.putLong(((Long)var4).longValue());
                  } else if(var5 == Integer.TYPE) {
                     var6.out.putInt(((Number)var4).intValue());
                  } else if(var5 == Short.TYPE) {
                     var6.out.putShort(((Number)var4).shortValue());
                  } else if(var5 == Byte.TYPE) {
                     var6.out.put(((Number)var4).byteValue());
                  } else if(var5 == Double.TYPE) {
                     var6.out.putDouble(((Double)var4).doubleValue());
                  } else if(var5 == Float.TYPE) {
                     var6.out.putFloat(((Float)var4).floatValue());
                  } else {
                     byte[] var9;
                     if(var5 != String.class) {
                        if(var5 == byte[].class) {
                           if((var9 = (byte[])((byte[])var4)).length < 1024) {
                              var9 = Arrays.copyOf(var9, 1024);
                           }

                           var6.out.put(var9);
                        }
                     } else {
                        var9 = ((String)var4).getBytes("UTF-8");
                        Arrays.fill(var6.stringBytes, (byte)32);

                        int var8;
                        for(var8 = 0; var8 < 64 && var8 < var9.length; ++var8) {
                           var6.stringBytes[var8] = var9[var8];
                        }

                        for(var8 = var9.length; var8 < 64; ++var8) {
                           var6.stringBytes[var8] = 32;
                        }

                        var6.out.put(var6.stringBytes);
                     }
                  }
               } catch (Exception var7) {
                  this.netManager.error(var7);
               }
            }
         }

      }
   }

   public Object readObject(Class<?> var1) {
      if(!this.netManager.isConnected()) {
         return null;
      } else {
         try {
            if(var1 == Long.TYPE) {
               return Long.valueOf(this.in.getLong());
            } else if(var1 == Integer.TYPE) {
               return Integer.valueOf(this.in.getInt());
            } else if(var1 == Short.TYPE) {
               return Short.valueOf(this.in.getShort());
            } else if(var1 == Byte.TYPE) {
               return Byte.valueOf(this.in.get());
            } else if(var1 == Double.TYPE) {
               return Double.valueOf(this.in.getDouble());
            } else if(var1 == Float.TYPE) {
               return Float.valueOf(this.in.getFloat());
            } else if(var1 == String.class) {
               this.in.get(this.stringBytes);
               return (new String(this.stringBytes, "UTF-8")).trim();
            } else if(var1 == byte[].class) {
               byte[] var3 = new byte[1024];
               this.in.get(var3);
               return var3;
            } else {
               return null;
            }
         } catch (Exception var2) {
            this.netManager.error(var2);
            return null;
         }
      }
   }
   
   public void read(ByteBuffer in2) {
	   if(PlatformNetworking.playConnectionState() != EnumEaglerConnectionState.CONNECTED) {
		   return;
	   }
	   
	   List<byte[]> packets = PlatformNetworking.readAllPacket();
	   
	   if(packets != null) {
		   for(byte[] bytes : packets) {
			   in2.put(bytes);
		   }
	   }
   }

   public void write(ByteBuffer out2) {
	   if(PlatformNetworking.playConnectionState() != EnumEaglerConnectionState.CONNECTED) {
		   return;
	   }
	   
	   int len = out2.remaining();
	   byte[] data = new byte[len];
	   out2.get(data);
	   
	   PlatformNetworking.writePlayPacket(data);
   }
}