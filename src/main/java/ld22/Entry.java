package ld22;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import ld22.engine.InputHandler;

public class Entry {

	// pull from manifest later on
	private String version = "1.0.0";

	private int width = 800;
	private int height = 600;
	private long lastFrame;
	private long lastFPS;
	private int fps;

	public InputHandler inputHandler = new InputHandler();


	public static void main(String[] argv) {
		Entry entryPoint = new Entry();


		// will need to be redone later, don't like this, but was a quick method to set dimension without parsing too much
		for (String arg : argv) {
			if (arg.startsWith("-res:")) {
				String[] dim = arg.replace("-res:", "").split("x");
				try {
					int tempwidth = Integer.valueOf(dim[0]);
					int tempheight = Integer.valueOf(dim[1]);
					entryPoint.updateDimensions(tempwidth, tempheight);
				} catch (Exception e) {
					System.out.println("Invalid resolution dimension");
					e.printStackTrace();
					System.exit(0);
				}

			}
		}

		entryPoint.start();
	}

	private void updateDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	private void start() {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();

		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		initGL();
		getDelta();
		// Counter for last fps calculation, not last fps value... probably need to rename...
		lastFPS = getTime();

		while (!Display.isCloseRequested()) {
			renderLoop();
		}

		Display.destroy();
	}

	private void initGL() {

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);


		// Ripped from old code, I think there was an error with the Alpha Function. Need to test later
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1f);

		GL11.glClearColor(0, 0, 0, 0);



	}

	private void renderLoop() {
		int delta = getDelta();
		updateFPS();
		inputHandler.pollInput(delta);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();

		//draw stuff in here


		Display.update();
		
		// blah blah blah, yes, Intentionally 60fps. Due to light nature, possible to exceed 1k fps, which would destroy calculations
		Display.sync(60); 

	}


	public int getDelta() {
		long time = getTime();
		int delta = (int)(time - lastFrame);
		lastFrame = time;

		return delta;
	}

	public long getTime() {
		return ((Sys.getTime() * 1000) / Sys.getTimerResolution());
	}

	private void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("Lava Jumper Remake v" + version + " - FPS:" + fps);
			fps = 0;
			lastFPS += 1000;
		}

		fps++;
	}
}
