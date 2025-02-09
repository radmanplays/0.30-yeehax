package com.mojang.minecraft.model;

import org.lwjgl.opengl.GL11;

import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;

public final class ModelPart {
	public Vertex[] vertices;
	public TexturedQuad[] quads;
	private int textureOffsetX;
	private int textureOffsetY;
	public float x;
	public float y;
	public float z;
	public float pitch;
	public float yaw;
	public float roll;
	public boolean compiled = false;
	public int displayList = 0;
	public boolean mirror = false;
	public boolean render = true;
	private boolean isHidden = false;

	public ModelPart(int var1, int var2) {
		this.textureOffsetX = var1;
		this.textureOffsetY = var2;
	}

	public final void setBounds(float var1, float var2, float var3, int var4, int var5, int var6, float var7) {
		this.vertices = new Vertex[8];
		this.quads = new TexturedQuad[6];
		float var8 = var1 + (float)var4;
		float var9 = var2 + (float)var5;
		float var10 = var3 + (float)var6;
		var1 -= var7;
		var2 -= var7;
		var3 -= var7;
		var8 += var7;
		var9 += var7;
		var10 += var7;
		if(this.mirror) {
			var7 = var8;
			var8 = var1;
			var1 = var7;
		}

		Vertex var20 = new Vertex(var1, var2, var3, 0.0F, 0.0F);
		Vertex var11 = new Vertex(var8, var2, var3, 0.0F, 8.0F);
		Vertex var12 = new Vertex(var8, var9, var3, 8.0F, 8.0F);
		Vertex var18 = new Vertex(var1, var9, var3, 8.0F, 0.0F);
		Vertex var13 = new Vertex(var1, var2, var10, 0.0F, 0.0F);
		Vertex var15 = new Vertex(var8, var2, var10, 0.0F, 8.0F);
		Vertex var21 = new Vertex(var8, var9, var10, 8.0F, 8.0F);
		Vertex var14 = new Vertex(var1, var9, var10, 8.0F, 0.0F);
		this.vertices[0] = var20;
		this.vertices[1] = var11;
		this.vertices[2] = var12;
		this.vertices[3] = var18;
		this.vertices[4] = var13;
		this.vertices[5] = var15;
		this.vertices[6] = var21;
		this.vertices[7] = var14;
		this.quads[0] = new TexturedQuad(new Vertex[]{var15, var11, var12, var21}, this.textureOffsetX + var6 + var4, this.textureOffsetY + var6, this.textureOffsetX + var6 + var4 + var6, this.textureOffsetY + var6 + var5);
		this.quads[1] = new TexturedQuad(new Vertex[]{var20, var13, var14, var18}, this.textureOffsetX, this.textureOffsetY + var6, this.textureOffsetX + var6, this.textureOffsetY + var6 + var5);
		this.quads[2] = new TexturedQuad(new Vertex[]{var15, var13, var20, var11}, this.textureOffsetX + var6, this.textureOffsetY, this.textureOffsetX + var6 + var4, this.textureOffsetY + var6);
		this.quads[3] = new TexturedQuad(new Vertex[]{var12, var18, var14, var21}, this.textureOffsetX + var6 + var4, this.textureOffsetY, this.textureOffsetX + var6 + var4 + var4, this.textureOffsetY + var6);
		this.quads[4] = new TexturedQuad(new Vertex[]{var11, var20, var18, var12}, this.textureOffsetX + var6, this.textureOffsetY + var6, this.textureOffsetX + var6 + var4, this.textureOffsetY + var6 + var5);
		this.quads[5] = new TexturedQuad(new Vertex[]{var13, var15, var21, var14}, this.textureOffsetX + var6 + var4 + var6, this.textureOffsetY + var6, this.textureOffsetX + var6 + var4 + var6 + var4, this.textureOffsetY + var6 + var5);
		if(this.mirror) {
			for(int var16 = 0; var16 < this.quads.length; ++var16) {
				TexturedQuad var17 = this.quads[var16];
				Vertex[] var19 = new Vertex[var17.vertices.length];

				for(var4 = 0; var4 < var17.vertices.length; ++var4) {
					var19[var4] = var17.vertices[var17.vertices.length - var4 - 1];
				}

				var17.vertices = var19;
			}
		}

	}

	public final void setPosition(float var1, float var2, float var3) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
	}

	public final void render(float var1) {
		if(this.render) {
			if(!this.compiled) {
				float var3 = var1;
				ModelPart var2 = this;
				this.displayList = GL11.glGenLists(1);
				GL11.glNewList(this.displayList, RealOpenGLEnums.GL_COMPILE);
				Tessellator tess = Tessellator.getInstance();
				WorldRenderer var4 = tess.getWorldRenderer();

				for(int var5 = 0; var5 < var2.quads.length; ++var5) {
					var4.begin(7, VertexFormat.POSITION_TEX_NORMAL);
					TexturedQuad var10000 = var2.quads[var5];
					float var8 = var3;
					TexturedQuad var6 = var10000;
					Vec3D var9 = var6.vertices[1].vector.subtract(var6.vertices[0].vector).normalize();
					Vec3D var10 = var6.vertices[1].vector.subtract(var6.vertices[2].vector).normalize();
					var9 = (new Vec3D(var9.y * var10.z - var9.z * var10.y, var9.z * var10.x - var9.x * var10.z, var9.x * var10.y - var9.y * var10.x)).normalize();

					for(int var11 = 0; var11 < 4; ++var11) {
						Vertex var12 = var6.vertices[var11];
						var4.pos(var12.vector.x * var8, var12.vector.y * var8, var12.vector.z * var8).tex(var12.u, var12.v).normal(-var9.x, -var9.y, -var9.z).endVertex();;
					}

					tess.draw();
				}

				GL11.glEndList();
				var2.compiled = true;
			}

			if(this.pitch == 0.0F && this.yaw == 0.0F && this.roll == 0.0F) {
				if(this.x == 0.0F && this.y == 0.0F && this.z == 0.0F) {
					GL11.glCallList(this.displayList);
				} else {
					GL11.glTranslatef(this.x * var1, this.y * var1, this.z * var1);
					GL11.glCallList(this.displayList);
					GL11.glTranslatef(-this.x * var1, -this.y * var1, -this.z * var1);
				}
			} else {
				GL11.glPushMatrix();
				GL11.glTranslatef(this.x * var1, this.y * var1, this.z * var1);
				if(this.roll != 0.0F) {
					GL11.glRotatef(this.roll * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				}

				if(this.yaw != 0.0F) {
					GL11.glRotatef(this.yaw * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				}

				if(this.pitch != 0.0F) {
					GL11.glRotatef(this.pitch * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				}

				GL11.glCallList(this.displayList);
				GL11.glPopMatrix();
			}
		}
	}
}
