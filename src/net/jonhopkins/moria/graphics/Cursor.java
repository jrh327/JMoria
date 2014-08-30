/**
 * Cursor.java: description
 * <p>
 * Copyright (c) 2014 Jon Hopkins
 * <p>
 * This file is part of Moria.
 * <p>
 * Moria is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * Moria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Moria.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jonhopkins.moria.graphics;

import java.awt.Color;

final class Cursor {
	public Cursor() {
		this(0, 0);
	}
	public Cursor(int x, int y) {
		this.x = x;
		this.y = y;
		c = Color.white;
	}
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getHeight() {
		return height;
	}
	public Color getColor() {
		return c;
	}
	public boolean isVisible() {
		return visible;
	}
	public boolean isBlinking() {
		return blinking;
	}
	public boolean hasBlinked() {
		return blinked;
	}
	public void blinked(boolean b) {
		blinked = b;
	}
	
	private int x;
	private int y;
	private Color c;
	private boolean visible = false;
	private boolean blinking = true;
	private boolean blinked = false;
	private int height = 3;
}
