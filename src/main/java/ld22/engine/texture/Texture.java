package ld22.engine.texture;

import org.lwjgl.opengl.GL11;

public class Texture {

	private static Texture lastBound;
	private int textureId;

	private int width;
	private int height;
	private boolean alpha;
	private String keyReference;

	private int atlasX = 4;
	private int atlasY = 4;


	protected Texture(int textureId, int width, int height, boolean alpha, String key) {
		this.textureId = textureId;
		this.width = width;
		this.height = height;
		this.alpha = alpha;
		this.keyReference = key;
	}

	public static Texture getLastBound() {
		return lastBound;
	}

	public void bind() {
		if (lastBound == null) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		if (lastBound != this) {
			lastBound = this;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		}
	}

	public void unbind() {
		lastBound = null;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void unbindAll() {
		lastBound = null;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public void dispose() {
		if (lastBound == this) {
			unbind();
		}

		GL11.glDeleteTextures(textureId);
		// should keep from being able to bind after being deleted from GPU memory
		textureId = 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean hasAlpha() {
		return alpha;
	}

	public int getTextureId() {
		return textureId;
	}

	public String getKeyReference() {
		return keyReference;
	}


	// Ripped from old code, texture atlas code as at least spot on for its purpose
	// note, may need to switch from old 2D float array to 1D float array for use in VBO/VAO if convert to OpenGL 3.0+
	// until then, this code will suffice 
	public void setAtlasSize(int atlasx, int atlasy) {
		this.atlasX = atlasx;
		this.atlasY = atlasy;
	}

	public float[][] getTexAtlasCoords(int index) {
		return getTexAtlasCoords(index, atlasX, atlasY);
	}

	public float[][] getTexAtlasCoords(int index, int atlasx, int atlasy) {
		float[][] coords = new float[4][2];
		float xoffset = (float) 1f / atlasx;
		float yoffset = (float) 1f / atlasy; 
		int y = index / atlasy;
		int x = index - (atlasx * y);

		if (index == -1 || index > (atlasx * atlasy)) {
			coords[0][0] = 0;
			coords[0][1] = 0;
			coords[1][0] = 0;
			coords[1][1] = 0;
			coords[2][0] = 0;
			coords[2][1] = 0;
			coords[3][0] = 0;
			coords[3][1] = 0;

		} else {
			coords[0][0] = x * xoffset;
			coords[0][1] = y * yoffset;
			coords[1][0] = (x * xoffset) + xoffset;
			coords[1][1] = y * yoffset;
			coords[2][0] = (x * xoffset) + xoffset;
			coords[2][1] = (y * yoffset) + yoffset;
			coords[3][0] = x * xoffset;
			coords[3][1] = (y * yoffset) + yoffset;
		}

		return coords; 
	}

}

