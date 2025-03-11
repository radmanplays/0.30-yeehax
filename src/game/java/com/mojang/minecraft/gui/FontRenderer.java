//package com.mojang.minecraft.gui;
//
//import com.mojang.minecraft.GameSettings;
//import com.mojang.minecraft.render.TextureManager;
//
//import net.lax1dude.eaglercraft.EagRuntime;
//import net.lax1dude.eaglercraft.opengl.ImageData;
//import net.lax1dude.eaglercraft.opengl.Tessellator;
//import net.lax1dude.eaglercraft.opengl.VertexFormat;
//import net.lax1dude.eaglercraft.opengl.WorldRenderer;
//
//import org.lwjgl.opengl.GL11;
//
//public final class FontRenderer {
//
//   private int[] widthmap = new int[256];
//   private int fontTexture = 0;
//   private GameSettings settings;
//
//
//   public FontRenderer(GameSettings var1, String var2, TextureManager var3) {
//      this.settings = var1;
//
//      ImageData var14;
//      try {
//         var14 = ImageData.loadImageFile(EagRuntime.getResourceStream(var2)).swapRB();
//      } catch (Exception var13) {
//         throw new RuntimeException(var13);
//      }
//
//      int var4 = var14.width;
//      int var5 = var14.height;
//      int[] var6 = new int[var4 * var5];
//      var14.getRGB(0, 0, var4, var5, var6, 0, var4);
//
//      for(int var15 = 0; var15 < 128; ++var15) {
//         var5 = var15 % 16;
//         int var7 = var15 / 16;
//         int var8 = 0;
//
//         for(boolean var9 = false; var8 < 8 && !var9; ++var8) {
//            int var10 = (var5 << 3) + var8;
//            var9 = true;
//
//            for(int var11 = 0; var11 < 8 && var9; ++var11) {
//               int var12 = ((var7 << 3) + var11) * var4;
//               if((var6[var10 + var12] & 255) > 128) {
//                  var9 = false;
//               }
//            }
//         }
//
//         if(var15 == 32) {
//            var8 = 4;
//         }
//
//         this.widthmap[var15] = var8;
//      }
//
//      this.fontTexture = var3.load(var2);
//   }
//
//   public final void render(String var1, int var2, int var3, int var4) {
//      this.render(var1, var2 + 1, var3 + 1, var4, true);
//      this.renderNoShadow(var1, var2, var3, var4);
//   }
//
//   public final void renderNoShadow(String var1, int var2, int var3, int var4) {
//      this.render(var1, var2, var3, var4, false);
//   }
//
//   private void render(String var1, int var2, int var3, int var4, boolean var5) {
//      if(var1 != null) {
//         char[] var12 = var1.toCharArray();
//         if(var5) {
//            var4 = (var4 & 16579836) >> 2;
//         }
//
//         GL11.glBindTexture(3553, this.fontTexture);
//         Tessellator tess = Tessellator.getInstance();
//         WorldRenderer var6 = tess.getWorldRenderer();
//         var6.begin(7, VertexFormat.POSITION_TEX_COLOR);
//         int var7 = 0;
//
//         for(int var8 = 0; var8 < var12.length; ++var8) {
//            int var9;
//            if(var12[var8] == 38 && var12.length > var8 + 1) {
//               if((var4 = "0123456789abcdef".indexOf(var12[var8 + 1])) < 0) {
//                  var4 = 15;
//               }
//
//               var9 = (var4 & 8) << 3;
//               int var10 = (var4 & 1) * 191 + var9;
//               int var11 = ((var4 & 2) >> 1) * 191 + var9;
//               var4 = ((var4 & 4) >> 2) * 191 + var9;
//               if(this.settings.anaglyph) {
//                  var9 = (var4 * 30 + var11 * 59 + var10 * 11) / 100;
//                  var11 = (var4 * 30 + var11 * 70) / 100;
//                  var10 = (var4 * 30 + var10 * 70) / 100;
//                  var4 = var9;
//                  var11 = var11;
//                  var10 = var10;
//               }
//
//               var4 = var4 << 16 | var11 << 8 | var10;
//               var8 += 2;
//               if(var5) {
//                  var4 = (var4 & 16579836) >> 2;
//               }
//            }
//
//            var4 = var12[var8] % 16 << 3;
//            var9 = var12[var8] / 16 << 3;
//            float var13 = 7.99F;
//            var6.pos((float)(var2 + var7), (float)var3 + var13, 0.0F).tex((float)var4 / 128.0F, ((float)var9 + var13) / 128.0F).color(var4).endVertex();
//            var6.pos((float)(var2 + var7) + var13, (float)var3 + var13, 0.0F).tex(((float)var4 + var13) / 128.0F, ((float)var9 + var13) / 128.0F).color(var4).endVertex();
//            var6.pos((float)(var2 + var7) + var13, (float)var3, 0.0F).tex(((float)var4 + var13) / 128.0F, (float)var9 / 128.0F).color(var4).endVertex();
//            var6.pos((float)(var2 + var7), (float)var3, 0.0F).tex((float)var4 / 128.0F, (float)var9 / 128.0F).color(var4).endVertex();
//            var7 += this.widthmap[var12[var8]];
//         }
//
//         tess.draw();
//      }
//   }
//
//   public final int getWidth(String var1) {
//      if(var1 == null) {
//         return 0;
//      } else {
//         char[] var4 = var1.toCharArray();
//         int var2 = 0;
//
//         for(int var3 = 0; var3 < var4.length; ++var3) {
//            if(var4[var3] == 38) {
//               ++var3;
//            } else {
//               var2 += this.widthmap[var4[var3]];
//            }
//         }
//
//         return var2;
//      }
//   }
//
//   public static String stripColor(String var0) {
//      char[] var3 = var0.toCharArray();
//      String var1 = "";
//
//      for(int var2 = 0; var2 < var3.length; ++var2) {
//         if(var3[var2] == 38) {
//            ++var2;
//         } else {
//            var1 = var1 + var3[var2];
//         }
//      }
//
//      return var1;
//   }
//}

package com.mojang.minecraft.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.render.TextureManager;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

public class FontRenderer {

	public FontRenderer(GameSettings gamesettings, String s, TextureManager var3) {
		charWidth = new int[256];
      ImageData var14;
      try {
         var14 = ImageData.loadImageFile(EagRuntime.getResourceStream(s)).swapRB();
      } catch (Exception var13) {
         throw new RuntimeException(var13);
      }
		int i = var14.width;
		int ai[] = var14.pixels;
		for (int k = 0; k < 256; k++) {
			int l = k % 16;
			int k1 = k / 16;
			int j2 = 7;
			do {
				if (j2 < 0) {
					break;
				}
				int i3 = l * 8 + j2;
				boolean flag = true;
				for (int l3 = 0; l3 < 8 && flag; l3++) {
					int i4 = (k1 * 8 + l3) * i;
					int k4 = ai[i3 + i4] & 0xff;
					if (k4 > 0) {
						flag = false;
					}
				}

				if (!flag) {
					break;
				}
				j2--;
			} while (true);
			if (k == 32) {
				j2 = 2;
			}
			charWidth[k] = j2 + 2;
		}

		this.fontTexture = var3.load(s);
		fontDisplayLists = GL11.glGenLists(288);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		for (int i1 = 0; i1 < 256; i1++) {
			GL11.glNewList(fontDisplayLists + i1, 4864 /* GL_COMPILE */);
			renderer.begin(7, VertexFormat.POSITION_TEX);
			int l1 = (i1 % 16) * 8;
			int k2 = (i1 / 16) * 8;
			float f = 7.99F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			renderer.pos(0.0D, 0.0F + f, 0.0D).tex((float) l1 / 128F + f1, ((float) k2 + f) / 128F + f2).endVertex();
			renderer.pos(0.0F + f, 0.0F + f, 0.0D).tex(((float) l1 + f) / 128F + f1, ((float) k2 + f) / 128F + f2).endVertex();
			renderer.pos(0.0F + f, 0.0D, 0.0D).tex(((float) l1 + f) / 128F + f1, (float) k2 / 128F + f2).endVertex();
			renderer.pos(0.0D, 0.0D, 0.0D).tex((float) l1 / 128F + f1, (float) k2 / 128F + f2).endVertex();
			tessellator.draw();
			GL11.glEndList();
		}

		for (int j1 = 0; j1 < 32; j1++) {
			int i2 = (j1 >> 3 & 1) * 85;
			int l2 = (j1 >> 2 & 1) * 170 + i2;
			int j3 = (j1 >> 1 & 1) * 170 + i2;
			int k3 = (j1 >> 0 & 1) * 170 + i2;
			if (j1 == 6) {
				l2 += 85;
			}
			boolean flag1 = j1 >= 16;
			if (gamesettings.anaglyph) {
				int j4 = (l2 * 30 + j3 * 59 + k3 * 11) / 100;
				int l4 = (l2 * 30 + j3 * 70) / 100;
				int i5 = (l2 * 30 + k3 * 70) / 100;
				l2 = j4;
				j3 = l4;
				k3 = i5;
			}
			if (flag1) {
				l2 /= 4;
				j3 /= 4;
				k3 /= 4;
			}
			GL11.glNewList(fontDisplayLists + 256 + j1, 4864 /* GL_COMPILE */);
			GL11.glColor3f((float) l2 / 255F, (float) j3 / 255F, (float) k3 / 255F);
			GL11.glEndList();
		}

	}

	public void drawStringWithShadow(String s, int i, int j, int k) {
		render(s, i + 1, j + 1, k, true);
		render(s, i, j, k);
	}

	public void render(String s, int i, int j, int k) {
		render(s, i, j, k, false);
	}

	public void render(String s, int i, int j, int k, boolean flag) {
		if (s == null) {
			return;
		}
		if (flag) {
			int l = k & 0xff000000;
			k = (k & 0xfcfcfc) >> 2;
			k += l;
		}
		GL11.glBindTexture(3553, this.fontTexture);
		float f = (float) (k >> 16 & 0xff) / 255F;
		float f1 = (float) (k >> 8 & 0xff) / 255F;
		float f2 = (float) (k & 0xff) / 255F;
		float f3 = (float) (k >> 24 & 0xff) / 255F;
		if (f3 == 0.0F) {
			f3 = 1.0F;
		}
		GL11.glColor4f(f, f1, f2, f3);
		GL11.glPushMatrix();
		GL11.glTranslatef(i, j, 0.0F);
		for (int i1 = 0; i1 < s.length(); i1++) {
			for (; s.length() > i1 + 1 && s.charAt(i1) == '\247'; i1 += 2) {
				int j1 = "0123456789abcdef".indexOf(s.toLowerCase().charAt(i1 + 1));
				if (j1 < 0 || j1 > 15) {
					j1 = 15;
				}
				continue;
			}

			if (i1 < s.length()) {
				int k1 = FontAllowedCharacters.isAllowed(s.charAt(i1));
				if (k1 >= 0) {
					GL11.glCallList(fontDisplayLists + k1 + 32);
					GL11.glTranslatef(charWidth[k1 + 32], 0.0F, 0.0F);
				}
			}
		}
		
		GL11.glPopMatrix();
	}

	public int getWidth(String s) {
		if (s == null) {
			return 0;
		}
		int i = 0;
		for (int j = 0; j < s.length(); j++) {
			if (s.charAt(j) == '\247') {
				j++;
				continue;
			}
			int k = FontAllowedCharacters.isAllowed(s.charAt(j));
			if (k >= 0) {
				i += charWidth[k + 32];
			}
		}

		return i;
	}
	
	public static String stripColor(String var0) {
		char[] var3 = var0.toCharArray();
		String var1 = "";

		for(int var2 = 0; var2 < var3.length; ++var2) {
			if(var3[var2] == 38) {
				++var2;
			} else {
				var1 = var1 + var3[var2];
			}
		}

		return var1;
	}

	private int charWidth[];
	public int fontTexture;
	private int fontDisplayLists;
	
	public static final char formatChar = '\247';
}
