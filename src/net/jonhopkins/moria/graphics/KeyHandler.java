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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class KeyHandler {
	public void handleKey(KeyEvent event) {
		int keystroke = event.getKeyCode();
		
		if (keystroke == 16 || keystroke == 17 || keystroke == 18) {
			return;
		}
		
		if (event.getModifiersEx() == 64) {
			keystroke |= MOD_SHIFT;
		} else if (event.getModifiersEx() == 128) {
			keystroke |= MOD_CTRL;
		} else if (event.getModifiersEx() == 192) {
			keystroke |= MOD_CTRLSHIFT;
		} else if (event.getModifiersEx() == 512) {
			keystroke |= MOD_ALT;
		} else if (event.getModifiersEx() == 576) {
			keystroke |= MOD_ALTSHIFT;
		} else if (event.getModifiersEx() == 640) {
			keystroke |= MOD_CTRLALT;
		}
		
		if (event.getKeyCode() == KeyEvent.VK_RIGHT ) {
			lastkeyhit = 39;
		} else if (event.getKeyCode() == KeyEvent.VK_LEFT ) {
			lastkeyhit = 37;
		} else if (event.getKeyCode() == KeyEvent.VK_UP ) {
			lastkeyhit = 38;
		} else if (event.getKeyCode() == KeyEvent.VK_DOWN ) {
			lastkeyhit = 40;
		} else { 
			lastkeyhit = event.getKeyChar();
		}
		queue.add(new Character((char)lastkeyhit));
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
		lastkeyhit = 0;
		
		return ch;
	}
	
	private final short MOD_SHIFT		=	0x0100;
	private final short MOD_CTRL		=	0x0200;
	private final short MOD_ALT			=	0x0400;
	private final short MOD_CTRLSHIFT	=	0x0800;
	private final short MOD_ALTSHIFT	=	0x1000;
	private final short MOD_CTRLALT		=	0x2000;
	
	private int lastkeyhit = 0;
	private BlockingQueue<Character> queue = new ArrayBlockingQueue<Character>(10);
}
