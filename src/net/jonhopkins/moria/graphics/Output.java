/**
 * Output.java: description
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

import java.awt.*;
import java.awt.event.KeyEvent;
import java.applet.Applet;
import java.util.ArrayList;

public final class Output {
	public Output(Applet a) {
		this(25, 80, Color.black, a);
	}
	public Output(int h, int w, Color c, Applet a) {
		main = a;
		
		windows = new ArrayList<Window>();
		windowStack = new ArrayList<Integer>();
		
		createNewWindow(h, w, 0, 0);
		
		main.setVisible(true);
		
		addKeyHandler(this);
	}
	
	public void addKeyHandler(Output curses) {
		keyHandler = new KeyHandler();
	}
	public void handleKey(KeyEvent event) {
		keyHandler.handleKey(event);
	}
	public void addCharacter(char ch) {
		windowInFocus.addCharacter(ch);
		windowInFocus.advanceCursor();
	}
	public void moveCursorAddCharacter(int x, int y, char ch) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.addCharacter(ch);
		windowInFocus.advanceCursor();
	}
	public void placeCharacter(char ch) {
		windowInFocus.addCharacter(ch);
	}
	public void placeCharacterAt(int x, int y, char ch) {
		windowInFocus.addCharacter(ch, x, y);
	}
	public void moveCursorPlaceCharacter(int x, int y, char ch) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.addCharacter(ch);
	}
	public void addString(String str) {
		windowInFocus.addString(str);
	}
	public void addStringAt(String str, int x, int y) {
		windowInFocus.addString(str, x, y);
	}
	public void moveCursorAddString(int x, int y, String str) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.addString(str, x, y);
	}
	public void addSubstring(String str, int offset, int length) {
		windowInFocus.addSubstring(str, offset, length);
	}
	public void addSubstringAt(String str, int offset, int length, int x, int y) {
		windowInFocus.addSubstring(str, offset, length, x, y);
	}
	public void moveCursorAddSubstring(String str, int offset, int length, int x, int y) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.addSubstring(str, offset, length, x, y);
	}
	public void moveCursor(int x, int y) {
		windowInFocus.moveCursor(x, y);
	}
	public boolean kbhit() {
		return keyHandler.isKeyAvailable();
	}
	public char getch() {
		char ch = 0;
		main.requestFocus();
		try {
			ch = keyHandler.requestKeyHit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ch;
	}
	public char getCharacter() {
		return windowInFocus.getCharacter();
	}
	public char getCharacter(int x, int y) {
		return windowInFocus.getCharacter(x, y);
	}
	
	public String getString(int l) {
		return windowInFocus.getString(l);
	}
	public String getStringAt(int x, int y, int l) {
		return windowInFocus.getString(x, y, l);
	}
	public String moveCursorGetString(int x, int y, int l) {
		windowInFocus.moveCursor(x, y);
		return windowInFocus.getString(x, y, l);
	}
	public void insertLine() {
		windowInFocus.insertLine();
	}
	public void insertLineAt(int y) {
		windowInFocus.insertLine(y);
	}
	public void deleteCharacter() {
		windowInFocus.delete();
	}
	public void deleteCharaterAt(int x, int y) {
		windowInFocus.delete(x, y);
	}
	public void moveCursorDeleteCharacter(int x, int y) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.delete(x, y);
	}
	public void deleteLine() {
		windowInFocus.deleteLine();
	}
	public void deleteLineAt(int y) {
		windowInFocus.deleteLine(y);
	}
	public void moveCursorDeleteLine(int x, int y) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.deleteLine(y);
	}
	public void clearToEndOfLine() {
		windowInFocus.clearToEndOfLine();
	}
	public void clearToEndOfLineFrom(int x, int y) {
		windowInFocus.clearToEndOfLine(x, y);
	}
	public void moveCursorClearToEndOfLine(int x, int y) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.clearToEndOfLine(x, y);
	}
	public void clearToBottom() {
		windowInFocus.clearToBottom();
	}
	public void clearToBottomFrom(int x, int y) {
		windowInFocus.clearToBottom(x, y);
	}
	public void moveCursorClearToBottom(int x, int y) {
		windowInFocus.moveCursor(x, y);
		windowInFocus.clearToBottom(x, y);
	}
	public void clear() {
		windowInFocus.clear();
	}
	public void mvwin(int dir) {
		switch(dir) {
		case 1:
			windowInFocus.moveWindowUp();
			break;
		case 2:
			windowInFocus.moveWindowDown();
			break;
		case 3:
			windowInFocus.moveWindowRight();
			break;
		case 4:
			windowInFocus.moveWindowLeft();
			break;
		default:
			break;
		}
		
		refreshAllWindows();
	}
	public void scrollCursor(int dir) {
		windowInFocus.scrollCursor(dir);
	}
	public Window createNewWindow(int height, int width, int offsetx, int offsety) {
		windowInFocus = new Window(height, width, offsetx, offsety);
		windows.add(windowInFocus);
		windowStack.add(0, windows.size() - 1);
		
		if (windows.size() == 1) {
			main.setBackground(Color.black);
			main.resize(windowInFocus.getWidth() * fWidth, windowInFocus.getHeight() * fHeight);
			
			bufferImg = main.createImage(windowInFocus.getWidth() * fWidth, windowInFocus.getHeight() * fHeight);
			bufferGraphics = bufferImg.getGraphics();
			bufferGraphics.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		}
		
		refresh();
		return windowInFocus;
	}
	public void setFocusNextWindow() {
		if (windowStack.get(0) == windows.size() - 1) {
			return;
		}
		
		setFocusWindow(windowStack.get(0) + 1);
	}
	public void setFocusPreviousWindow() {
		if (windowStack.get(0) == 0) {
			return;
		}
		
		setFocusWindow(windowStack.get(0) - 1);
	}
	public void setFocusWindow(int index) {
		if (index < 0 || index >= windows.size()) {
			return;
		}
		
		windowInFocus.setFocus(false);
		windowInFocus = windows.get(index);
		windowInFocus.setFocus(true);
		
		int i = 0;
		for (; i < windows.size(); i++) {
			if (windowStack.get(i) == index) {
				break;
			}
		}
		
		for (; i > 0; i--) {
			windowStack.set(i, windowStack.get(i - 1));
		}
		
		windowStack.set(0, index);
		refreshAllWindows();
	}
	public void toggleBlink() {
		//windowInFocus.toggleBlink();
	}
	public void toggleBold() {
		//windowInFocus.toggleBold();
	}
	public void toggleDim() {
		//windowInFocus.toggleDim();
	}
	public void toggleInvisible() {
		//windowInFocus.toggleInvisible();
	}
	public void toggleLow() {
		//windowInFocus.toggleLow();
	}
	public void toggleProtected() {
		//windowInFocus.toggleProtected();
	}
	public void toggleReverseVideo() {
		//windowInFocus.toggleReverseVideo();
	}
	public void toggleStandout() {
		//windowInFocus.toggleStandout();
	}
	public void toggleUnderline() {
		//windowInFocus.toggleUnderline();
	}
	public void closeWindow() {
		if (windowStack.size() == 1) {
			System.exit(0);
		}
		
		int indexOfWindowClosing = windowStack.get(0);
		windows.remove((int)windowStack.get(0));
		windowStack.remove(0);
		
		for (int i = 0; i < windows.size(); i++) {
			if (windowStack.get(i) > indexOfWindowClosing) {
				windowStack.set(i, windowStack.get(i) - 1);
			}
		}
		
		windowInFocus = windows.get(windowStack.get(0));
		windowInFocus.setFocus(true);
		refreshAllWindows();
	}
	public int getHeight() {
		return windowInFocus.getHeight();
	}
	public int getWidth() {
		return windowInFocus.getWidth();
	}
	public int getOffsetX() {
		return windowInFocus.getOffsetX();
	}
	public int getOffsetY() {
		return windowInFocus.getOffsetY();
	}
	public int getCursorX() {
		return windowInFocus.getCursorX();
	}
	public int getCursorY() {
		return windowInFocus.getCursorY();
	}
	public void addBorder() {
		windowInFocus.addBorder();
	}
	public void setBackgroundColor(int bgColor) {
		if (bgColor == windowInFocus.getBackgroundColor()) {
			return;
		}
		
		windowInFocus.setBackgroundColor(bgColor);
		refresh();
	}
	public void setForegroundColor(int fgColor) {
		if (fgColor == windowInFocus.getForeGroundColor()) {
			return;
		}
		
		windowInFocus.setForegroundColor(fgColor);
		refresh();
	}
	public void refresh() {
		windowInFocus.refresh(bufferGraphics);
		main.getGraphics().drawImage(bufferImg, 0, 0, main);
	}
	public void refreshWholeWindow() {
		windowInFocus.refreshAll(bufferGraphics);
		main.getGraphics().drawImage(bufferImg, 0, 0, main);
	}
	public void refreshAllWindows() {
		bufferGraphics.clearRect(0, 0, main.getWidth(), main.getHeight());
		
		for (int i = windows.size() - 1; i >= 0; i--) {
			windows.get(windowStack.get(i)).refreshAll(bufferGraphics);
		}
		
		windowInFocus.refreshAll(bufferGraphics);
		main.getGraphics().drawImage(bufferImg, 0, 0, main);
	}
	
	public Window getWindow() {
		return windowInFocus;
	}
	
	public void overwrite(Window win1, Window win2) {
		win1.overwrite(win2);
	}
	
	private Window windowInFocus;
	private KeyHandler keyHandler;
	private ArrayList<Window> windows;
	private ArrayList<Integer> windowStack;
	private Image bufferImg;
	private Graphics bufferGraphics;
	private Applet main;
	private int fHeight = 19;
	private int fWidth = 10;
}
