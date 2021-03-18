package ld22.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputHandler {
	
	public void pollInput(int delta) {
		
		// WILL be needed.... Stop button not always reliable with some of these
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			System.exit(0);
		}
		
		//TODO: remake all input systems.
		
		/*
		 * Input systems in order:
		 * UI Controls
		 * Player Movements
		 * 2D Bounding Box collision detection
		 * 
		 * Probably should add a way to control UI Size, and keybinds later.. Not highest priority atm
		 * 
		 */
		
		
		// Leaving just so I don't have to reimport later.
		Mouse.getX();
		Mouse.getY();
		

	}

}
