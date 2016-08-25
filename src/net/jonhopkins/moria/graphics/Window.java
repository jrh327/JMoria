/*
 * Window.java: store and display characters
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

import java.awt.Graphics;

public class Window {
	public Window(int h, int w) {
		this(h, w, 0, 0);
	}
	
	public Window(int h, int w, int offsetx, int offsety) {
		this.height = h;
		this.width = w;
		this.offsetx = offsetx;
		this.offsety = offsety;
		this.hasFocus = true;
		bufferScreen = new char[height][width];
		cur = new Cursor();
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				bufferScreen[i][j] = ' ';
			}
		}
		
		cursorMinX = 0;
		cursorMaxX = width - 1;
		cursorMinY = 0;
		cursorMaxY = height - 1;
		hasFocus = true;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getCursorX() {
		return cur.getX();
	}
	
	public int getCursorY() {
		return cur.getY();
	}
	
	public boolean isFocused() {
		return hasFocus;
	}
	
	public void moveCursor(int x, int y) {
		if (cur.getY() < height - 1) {
			bufferScreen[y][x] |= WA_CHANGED;
		} else {
			bufferScreen[cur.getY()][cur.getX()] |= WA_CHANGED;
		}
		
		cur.move(x, y);
	}
	
	public void newline(int x, int y) {
		if (y == cursorMaxY) {
			return;
		}
		bufferScreen[y][x] = '\n';
		moveCursor(cursorMinX, cur.getY() + 1);
	}
	
	public void backspace(int x, int y) {
		if (x == cursorMinX) {
			if (y == cursorMinY) {
				return;
			} else {
				int i = cursorMaxX;
				for (; i > cursorMinX; i--) {
					if (bufferScreen[y - 1][i] != 0) {
						break;
					}
				}
				
				delete(i, y - 1);
				moveCursor(i, y - 1);
				
				return;
			}
		}
		delete(x - 1, y);
		moveCursor(x - 1, y);
	}
	
	public void delete(int x, int y) {
		if ((char)bufferScreen[y][x] == '\n') {
			if ((char)bufferScreen[y + 1][cursorMinX] == '\0' || y == cursorMaxY) {
				bufferScreen[y][x] = 0;
				return;
			} else if (x == cursorMinX) {
				deleteLine(y);
				moveCursor(cursorMinX, y);
				return;
			}
			
			for (int i = 0; i < cursorMaxX - x; i++) {
				if (bufferScreen[y + 1][cursorMinX] == '\0') {
					return;
				}
				if (bufferScreen[y + 1][cursorMinX] == '\n') {
					delete(cursorMinX, y + 1);
					moveCursor(x, y);
					return;
				}
				
				bufferScreen[y][x + i] = bufferScreen[y + 1][cursorMinX];
				bufferScreen[cursorMaxY][cursorMaxX] |= WA_CHANGED;
				delete(cursorMinX, y + 1);
			}
			
			return;
		}
		
		for (; x < cursorMaxX; x++) {
			if (bufferScreen[y][x] == '\0') {
				break;
			}
			
			bufferScreen[y][x] = bufferScreen[y][x + 1];
			bufferScreen[y][x] |= WA_CHANGED;
		}
		if (bufferScreen[y][cursorMaxX] != '\0' && y < cursorMaxY) {
			bufferScreen[y][cursorMaxX] = bufferScreen[y + 1][cursorMinX];
			bufferScreen[y][x] |= WA_CHANGED;
			delete(cursorMinX, y + 1);
		} else if (bufferScreen[y][cursorMaxX] != '\0' && y == cursorMaxY) {
			bufferScreen[cursorMaxY][cursorMaxX] = 0;
		}
	}
	
	public void addCharacter(char ch) {
		addCharacter(ch, cur.getX(), cur.getY());
	}
	
	public void addCharacter(char ch, int x, int y) {
		if (x < cursorMinX || y < cursorMinY || x > cursorMaxX || y > cursorMaxY) {
			return;
		}
		
		if (ch == '\n') {
			newline(x, y);
			return;
		} else if (ch == '\b') {
			backspace(x, y);
			return;
		} else if (ch == '\t') {
			for (int i = 0; i < 7; i++) {
				advanceCursor();
			}
			addCharacter(' ');
			return;
		} else if (ch == '\0') {
			delete(x, y);
			return;
		}
		
		if (cur.getX() == cursorMaxX && cur.getY() == cursorMaxY) {
			return;
		}
		
		bufferScreen[y][x] = ch;
	}
	
	public void addString(String str) {
		addString(str, cur.getX(), cur.getY());
	}
	
	public void addString(String str, int x, int y) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\n') {
				y++;
				x = cursorMinX;
			} else if (str.charAt(i) == '\b') {
				x--;
			} else {
				addCharacter(str.charAt(i), x, y);
				advanceCursor();
				x++;
				if (x > cursorMaxX) {
					return;
				}
			}
		}
	}
	
	public void deleteLine(int y) {
		for (int i = y; i < cursorMaxY; i++) {
			bufferScreen[i] = bufferScreen[i + 1].clone();
		}
		
		clearToEndOfLine(cursorMinX, cursorMaxY);
		
		if (y > cursorMinY) {
			moveCursor(cursorMinX, y - 1);
		} else {
			moveCursor(cursorMinX, y);
		}
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//bufferScreen[i][j] |= WA_CHANGED;
			}
		}
	}
	
	public void clearToEndOfLine() {
		clearToEndOfLine(cur.getX(), cur.getY());
	}
	
	public void clearToEndOfLine(int x, int y) {
		for (; x < cursorMaxX; x++) {
			bufferScreen[y][x] = 0;
		}
	}
	
	public void clearToBottom() {
		clearToBottom(cur.getX(), cur.getY());
	}
	
	public void clearToBottom(int x, int y) {
		clearToEndOfLine(x, y);
		
		for(; y < cursorMaxY; y++) {
			clearToEndOfLine(cursorMinX, y + 1);
		}
	}
	
	public void clear() {
		for (int i = cursorMinY; i <= cursorMaxY; i++) {
			for (int j = cursorMinX; j < cursorMaxX; j++) {
				//addCharacter('\0', j, i);
				bufferScreen[i][j] = 0;
			}
		}
		
		moveCursor(cursorMinX, cursorMinY);
	}
	
	public void advanceCursor() {
		if (cur.getX() < cursorMaxX) {
			moveCursor(cur.getX() + 1, cur.getY());
		} else if (cur.getY() < cursorMaxY) {
			moveCursor(cursorMinX, cur.getY() + 1);
		}
	}
	
	public void setFocus(boolean focused) {
		this.hasFocus = focused;
	}
	
	public void refresh(Graphics g) {
		char currentChar;
		
		for (int i = height - 1; i >= 0; i--) {
			for (int j = 0; j < width; j++) {
				currentChar = (char)bufferScreen[i][j];
				if (currentChar < ' ') {
					currentChar = ' ';
				}
				
				g.drawImage(Textures.chars.get(String.valueOf(currentChar)),
						(j + offsetx) * fWidth, (i + offsety) * fHeight,
						10, 19, null);
			}
		}
	}
	
	public void refreshAll(Graphics g) {
		char currentChar;
		
		for (int i = height - 1; i >= 0; i--) {
			for (int j = 0; j < width; j++) {
				currentChar = (char)bufferScreen[i][j];
				
				if (currentChar < ' ') {
					currentChar = ' ';
				}
				
				g.drawImage(Textures.chars.get(String.valueOf(currentChar)),
						(j + offsetx) * fWidth, (i + offsety) * fHeight,
						10, 19, null);
			}
		}
	}
	
	public void overwrite(Window win) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				win.bufferScreen[i][j] = bufferScreen[i][j];
			}
		}
	}
	
	protected char[][] bufferScreen;
	private int height;
	private int width;
	private int offsetx;
	private int offsety;
	private int cursorMinX;
	private int cursorMaxX;
	private int cursorMinY;
	private int cursorMaxY;
	private boolean hasFocus;
	private int fHeight = 19;
	private int fWidth = 10;
	private Cursor cur;
	
	//Indicates if a position on the screen has changed and needs to be redrawn
	private final int WA_CHANGED = 0x00400000;
}
