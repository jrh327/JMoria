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
		this.FG_COLOR = FG_WHITE;
		this.BG_COLOR = BG_BLACK;
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
	
	public int getOffsetX() {
		return offsetx;
	}
	
	public int getOffsetY() {
		return offsety;
	}
	
	public int getCursorX() {
		return cur.getX();
	}
	
	public int getCursorY() {
		return cur.getY();
	}
	
	public int getCursorMinX() {
		return cursorMinX;
	}
	
	public int getCursorMaxX() {
		return cursorMaxX;
	}
	
	public int getCursorMinY() {
		return cursorMinY;
	}
	
	public int getCursorMaxY() {
		return cursorMaxY;
	}
	
	public boolean isFocused() {
		return hasFocus;
	}
	
	public int getBackgroundColor() {
		return BG_COLOR;
	}
	
	public int getForeGroundColor() {
		return FG_COLOR;
	}
	
	public void setBackgroundColor(int bgColor) {
		if (bgColor == BG_COLOR) {
			return;
		}
		
		BG_COLOR = bgColor;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//bufferScreen[i][j] = (bufferScreen[i][j] & 0xfff8ffff) | BG_COLOR;
			}
		}
	}
	
	public void setForegroundColor(int fgColor) {
		if (fgColor == FG_COLOR) {
			return;
		}
		
		FG_COLOR = fgColor;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//bufferScreen[i][j] = (bufferScreen[i][j] & 0xffc7ffff) | BG_COLOR;
			}
		}
	}
	
	public void addBorder() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (i == 0 && j == 0) {
					addCharacter(ACS_ULCORNER, j, i);
				} else if (i == 0 && j == width - 1) {
					addCharacter(ACS_URCORNER, j, i);
				} else if (i == height - 1 && j == 0) {
					addCharacter(ACS_LLCORNER, j, i);
				} else if (i == height - 1 && j == width - 1) {
					addCharacter(ACS_LRCORNER, j, i);
				} else if (i == 0 || i == height - 1) {
					addCharacter(ACS_HLINE, j, i);
				} else if (j == 0 || j == width - 1) {
					addCharacter(ACS_VLINE, j, i);
				}
			}
		}
		
		cursorMinX++;
		cursorMaxX--;
		cursorMinY++;
		cursorMaxY--;
		moveCursor(cursorMinX, cursorMinY);
	}
	
	public void moveCursor(int x, int y) {
		if (cur.getY() < height - 1) {
			bufferScreen[y][x] |= WA_CHANGED;
		} else {
			bufferScreen[cur.getY()][cur.getX()] |= WA_CHANGED;
		}
		
		cur.move(x, y);
	}
	
	public void newline() {
		newline(cur.getX(), cur.getY());
	}
	
	public void newline(int x, int y) {
		if (y == cursorMaxY) {
			return;
		}
		bufferScreen[y][x] = '\n'; // (A_ATTRIBUTES | FG_COLOR | BG_COLOR | '\n');
		moveCursor(cursorMinX, cur.getY() + 1);
	}
	
	public void backspace() {
		backspace(cur.getX(), cur.getY());
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
	
	public void delete() {
		delete(cur.getX(), cur.getY());
	}
	
	public void delete(int x, int y) {
		if ((char)bufferScreen[y][x] == '\n') {
			if ((char)bufferScreen[y + 1][cursorMinX] == '\0' || y == cursorMaxY) {
				bufferScreen[y][x] = 0; // (A_ATTRIBUTES | FG_COLOR | BG_COLOR | '\0');
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
			bufferScreen[cursorMaxY][cursorMaxX] = 0; //(A_ATTRIBUTES | WA_CHANGED | FG_COLOR | BG_COLOR | '\0');
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
		
		bufferScreen[y][x] = ch; //(A_ATTRIBUTES | WA_CHANGED | FG_COLOR | BG_COLOR | ch);
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
	
	public void addSubstring(String str, int offset, int length) {
		addSubstring(str, offset, length, cur.getX(), cur.getY());
	}
	
	public void addSubstring(String str, int offset, int length, int x, int y) {
		for (int i = offset; i < length; i++) {
			if (str.charAt(i) == '\n') {
				y++;
				x = cursorMinX;
			} else if (str.charAt(i) == '\b') {
				x--;
			}else {
				addCharacter(str.charAt(i), x, y);
				x++;
				if (x > cursorMaxX) {
					return;
				}
			}
		}
	}
	
	public char getCharacter() {
		return getCharacter(cur.getX(), cur.getY());
	}
	
	public char getCharacter(int x, int y) {
		return (char)bufferScreen[y][x];
	}
	
	public String getString(int l) {
		return getString(cur.getX(), cur.getY(), l);
	}
	
	public String getString(int x, int y, int l) {
		String str = "";
		
		for (int i = 0; i < l; i++) {
			str += (char)bufferScreen[y][x + i];
		}
		
		return str;
	}
	
	public void insertLine() {
		insertLine(cur.getY());
	}
	
	public void insertLine(int y) {
		for (int i = cursorMaxY; i > y; i--) {
			bufferScreen[i] = bufferScreen[i - 1].clone();
		}
		
		clearToEndOfLine(cursorMinX, y);
		bufferScreen[y][cursorMinX] = '\n'; //(A_ATTRIBUTES | FG_COLOR | BG_COLOR | '\n');
		moveCursor(cursorMinX, y);
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//bufferScreen[i][j] |= WA_CHANGED;
			}
		}
	}
	
	public void deleteLine() {
		deleteLine(cur.getY());
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
			bufferScreen[y][x] = 0; // (A_ATTRIBUTES | WA_CHANGED | FG_COLOR | BG_COLOR | '\0');
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
	
	public void scrollCursor(int direction) {
		if (direction == 1) {
			if (cur.getY() == cursorMinY) {
				moveCursor(cursorMinX, cursorMinY);
				return;
			}
			
			if (bufferScreen[cur.getY() - 1][cur.getX()] != '\0') {
				moveCursor(cur.getX(), cur.getY() - 1);
			} else {
				int x = cur.getX();
				int y = cur.getY() - 1;
				
				for (; x > cursorMinX; x--) {
					if (bufferScreen[y][x] != '\0') {
						break;
					}
				}
				
				moveCursor(x, y);
			}
		} else if (direction == 2) {
			if (cur.getY() == cursorMaxY) {
				int x = cur.getX();
				for (; x < cursorMaxX; x++) {
					if (bufferScreen[cursorMaxY][x] == '\0') {
						break;
					}
				}
				moveCursor(x, cursorMaxY);
				return;
			}
			
			if (bufferScreen[cur.getY() + 1][cur.getX()] != '\0') {
				moveCursor(cur.getX(), cur.getY() + 1);
			} else {
				int x = cur.getX();
				int y = cur.getY();
				
				boolean found = false;
				
				for (; x < cursorMaxX; x++) {
					if (bufferScreen[y][x] == '\n') {
						found = true;
						break;
					}
					else if (bufferScreen[y][x] == '\0') {
						break;
					}
				}
				
				if (!found) {
					if (bufferScreen[y][cursorMaxX] != '\0') {
						found = true;
					}
				}
				
				if (found) {
					y++;
					for (; x > cursorMinX; x--) {
						if (bufferScreen[y][x - 1] != '\0' && bufferScreen[y][x - 1] != '\n') {
							break;
						}
					}
				}
				
				moveCursor(x, y);
			}
		} else if (direction == 3) {
			if (bufferScreen[cur.getY()][cur.getX()] == '\n' && cur.getY() != cursorMaxY) {
				moveCursor(cursorMinX, cur.getY() + 1);
			} else if (bufferScreen[cur.getY()][cur.getX()] != '\0') {
				if (cur.getX() != cursorMaxX) {
					moveCursor(cur.getX() + 1, cur.getY());
				} else {
					moveCursor(cursorMinX, cur.getY() + 1);
				}
			}
		} else if (direction == 4) {
			if (cur.getX() == cursorMinX) {
				if (cur.getY() == cursorMinY) {
					return;
				}
				
				if (bufferScreen[cur.getY() - 1][cursorMaxX] != '\0') {
					moveCursor(cursorMaxX, cur.getY() - 1);
				} else {
					int x = cursorMaxX;
					int y = cur.getY() - 1;
					
					for (; x > cursorMinX; x--) {
						if ((char)bufferScreen[y][x] == '\n') {
							break;
						}
					}
					
					moveCursor(x, y);
				}
			} else {
				moveCursor(cur.getX() - 1, cur.getY());
			}
		}
	}
	
	public void moveWindowUp() {
		this.offsety--;
	}
	
	public void moveWindowDown() {
		this.offsety++;
	}
	
	public void moveWindowRight() {
		this.offsetx++;
	}
	
	public void moveWindowLeft() {
		this.offsetx--;
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
				
				g.drawImage(Textures.chars.get(String.valueOf(currentChar)), (j + offsetx) * fWidth, (i + offsety) * fHeight, 10, 19, null);
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
				
				g.drawImage(Textures.chars.get(String.valueOf(currentChar)), (j + offsetx) * fWidth, (i + offsety) * fHeight, 10, 19, null);
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
	
	private int FG_COLOR					=	0x00000000;	//Bit-mask to extract foreground color
	private int BG_COLOR					=	0x00000000;	//Bit-mask to extract background color
	private int A_ATTRIBUTES				=	0x00000000;	//Bit-mask to extract attributes
	private final int A_COLOR				=	0x003f0000;	//Bit-mask to extract colour-pair information
	private final int A_CHARTEXT			=	0x0000ffff;	//Bit-mask to extract a character
	
	//window attribute bits
	private final static int WA_CHANGED		=	0x00400000;	//Indicates if a position on the screen has changed and needs to be redrawn
	public final static int WA_BLINK		=	0x00800000;	//Blinking
	public final static int WA_BOLD			=	0x01000000;	//Extra bright or bold
	public final static int WA_DIM			=	0x02000000;	//Half bright
	public final static int WA_INVIS		=	0x04000000;	//Invisible
	public final static int WA_LOW			=	0x08000000;	//Low highlight
	public final static int WA_PROTECT		=	0x10000000;	//Protected
	public final static int WA_REVERSE		=	0x20000000;	//Reverse video
	public final static int WA_STANDOUT		=	0x40000000;	//Best highlighting mode of the terminal
	public final static int WA_UNDERLINE	=	0x80000000;	//Underlining
	
	private final int A_BGCOLOR				=	0x00070000;	//Bit-mask to extract background color
	private final int A_FGCOLOR				=	0x00380000;	//Bit-mask to extract foreground color
	
	//background color constants
	public final static int BG_BLACK		=	0x00000000;
	public final static int BG_RED			=	0x00010000;
	public final static int BG_GREEN		=	0x00020000;
	public final static int BG_YELLOW		=	0x00030000;
	public final static int BG_BLUE			=	0x00040000;
	public final static int BG_PURPLE		=	0x00050000;
	public final static int BG_CYAN			=	0x00060000;
	public final static int BG_WHITE		=	0x00070000;
	
	//foreground color constants
	public final static int FG_BLACK		=	0x00000000;
	public final static int FG_RED			=	0x00080000;
	public final static int FG_GREEN		=	0x00100000;
	public final static int FG_YELLOW		=	0x00180000;
	public final static int FG_BLUE			=	0x00200000;
	public final static int FG_PURPLE		=	0x00280000;
	public final static int FG_CYAN			=	0x00300000;
	public final static int FG_WHITE		=	0x00380000;
	
	//line drawing constants
	public final static char ACS_ULCORNER	=	127;	//upper left-hand corner  
	public final static char ACS_LLCORNER	=	128;	//lower left-hand corner  
	public final static char ACS_URCORNER	=	129;	//upper right-hand corner  
	public final static char ACS_LRCORNER	=	130;	//lower right-hand corner  
	public final static char ACS_RTEE		=	131;	//right tee  
	public final static char ACS_LTEE		=	132;	//left tee  
	public final static char ACS_BTEE		=	133;	//bottom tee  
	public final static char ACS_TTEE		=	134;	//top tee  
	public final static char ACS_HLINE		=	135;	//horizontal line  
	public final static char ACS_VLINE		=	136;	//vertical line  
	public final static char ACS_PLUS		=	137;	//plus  
	public final static char ACS_S1			=	138;	//line 1  
	public final static char ACS_S9			=	139;	//line 9  
	public final static char ACS_DIAMOND	=	140;	//diamond  
	public final static char ACS_CKBOARD	=	141;	//checker board (stipple)  
	public final static char ACS_DEGREE		=	142;	//degree symbol  
	public final static char ACS_PLMINUS	=	143;	//plus/minus  
	public final static char ACS_BULLET		=	144;	//bullet  
	public final static char ACS_LARROW		=	145;	//arrow pointing left  
	public final static char ACS_RARROW		=	146;	//arrow pointing right  
	public final static char ACS_DARROW		=	147;	//arrow pointing down  
	public final static char ACS_UARROW		=	148;	//arrow pointing up  
	public final static char ACS_BOARD		=	149;	//board of squares  
	public final static char ACS_LANTERN	=	150;	//lantern symbol  
	public final static char ACS_BLOCK		=	151;	//solid square block 
}
