package ld22.engine.texture;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TextureManager {

	private static HashMap<String, Texture> textures = new HashMap<>();

	public static Texture getTexture(String name) {
		return textures.get(name);
	}

	public static void disposeAll() {
		for (Texture tex : textures.values()) {
			tex.dispose();
		}

		textures.clear();
	}

	public static Texture generateTexture(String resourcePath, String name) {
		BufferedImage texture;
		try {
			texture = ImageIO.read((TextureManager.class.getClassLoader()).getResourceAsStream(resourcePath));
		} catch (Exception e) {
			System.out.println("Failed to find texture file: " + resourcePath);
			e.printStackTrace();

			// Ehhh screw it, backup texture!
			texture = new BufferedImage(64, 64, 2);
			Graphics g = texture.getGraphics();
			g.setColor(Color.magenta);
			g.fillRect(0, 0, 64, 64);
			g.setColor(Color.black);
			g.drawString("no texture", 2, 10);
			g.dispose();
		}

		return generateTexture(texture, name);
	}

	public static Texture generateTexture(BufferedImage texture, String name) {
		IntBuffer temp = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, temp);
		int max = temp.get(0);
		if ((texture.getWidth() > max) || (texture.getHeight() > max)) {
			System.out.println("Attempt to allocate a texture to big for the current hardware");

			// backup texture to the rescue again!
			texture = new BufferedImage(64, 64, 2);
			Graphics g = texture.getGraphics();
			g.setColor(Color.red);
			g.fillRect(0, 0, 64, 64);
			g.setColor(Color.black);
			g.drawString("tex too big", 1, 10);
			g.dispose();
		}

		boolean alpha = texture.getColorModel().hasAlpha();
		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

		// Look into implementation of mipmap for future
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		int pixFmt = (alpha ? GL11.GL_RGBA : GL11.GL_RGB);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, pixFmt, texture.getWidth(), texture.getHeight(), 0, pixFmt, GL11.GL_UNSIGNED_BYTE, getTextureData(texture, alpha));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		Texture finishedTex = new Texture(id, texture.getWidth(), texture.getHeight(), alpha, name);
		textures.put(name, finishedTex);

		return finishedTex;

	}


	private static ByteBuffer getTextureData(BufferedImage texture, boolean alpha) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(texture.getWidth() * texture.getHeight() * (alpha ? 4 : 3));
		int[] pixels = new int[texture.getWidth() * texture.getHeight()];
		texture.getRGB(0, 0, texture.getWidth(), texture.getHeight(), pixels, 0, texture.getWidth());

		// screw bytebuffers and their inability to process full int arrays....
		// Also not bothering to implement 16bit channels... that can come later on.
		for (int y = 0; y < texture.getHeight(); y++) {
			for (int x = 0; x < texture.getWidth(); x++) {
				int pixel = pixels[y * texture.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				if (alpha) {
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				}

			}
		}

		buffer.flip();
		return buffer;

	}


}
