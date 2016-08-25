/*
 * KeyHandler.java: receive input and store it until it's requested
 * 
 * Copyright (C) 2014 Jon Hopkins
 * 
 * This file is part of Umoria.
 * 
 * Umoria is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Umoria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with Umoria.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jonhopkins.moria.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class KeyHandler implements KeyListener {
	public void handleKey(KeyEvent event) {
		char keyChar;
		int keyCode = event.getKeyCode();
		
		switch (keyCode) {
		case 16:
		case 17:
		case 18:
			return;
		case KeyEvent.VK_LEFT:
			keyChar = KeyEvent.VK_LEFT;
			break;
		case KeyEvent.VK_UP:
			keyChar = KeyEvent.VK_UP;
			break;
		case KeyEvent.VK_RIGHT:
			keyChar = KeyEvent.VK_RIGHT;
			break;
		case KeyEvent.VK_DOWN:
			keyChar = KeyEvent.VK_DOWN;
			break;
		default:
			keyChar = event.getKeyChar();
			break;
		}
		
		queue.add(keyChar);
	}
	
	public boolean isKeyAvailable() {
		return !queue.isEmpty();
	}
	
	public char requestKeyHit() {
		char ch = 0;
		
		try {
			ch = queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return ch;
	}
	
	private BlockingQueue<Character> queue = new ArrayBlockingQueue<Character>(10);
	
	@Override
	public void keyTyped(KeyEvent event) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		handleKey(event);
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		
	}
}
