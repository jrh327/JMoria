/*
 * Output.java: main curses emulation class
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

import java.awt.*;
import java.applet.Applet;
import java.util.ArrayList;

public final class Output {
	private static Output output = null;
	private Output() {
		
	}
	
	public static void initialize(Applet app) {
		if (output == null) {
			output = new Output(app);
		}
		createNewWindow(25, 80, 0, 0);
	}
	
	private Output(Applet app) {
		main = app;
		
		windows = new ArrayList<Window>();
		windowStack = new ArrayList<Integer>();
		
		main.setVisible(true);
		main.setFocusable(true);
		
		keyHandler = new KeyHandler();
		main.addKeyListener(keyHandler);
		
		main.requestFocus();
	}
	
	public static void addCharacter(char ch) {
		output.windowInFocus.addCharacter(ch);
		output.windowInFocus.advanceCursor();
	}
	
	public static void moveCursorAddCharacter(int x, int y, char ch) {
		output.windowInFocus.moveCursor(x, y);
		output.windowInFocus.addCharacter(ch);
		output.windowInFocus.advanceCursor();
	}
	
	public static void addString(String str) {
		output.windowInFocus.addString(str);
	}
	
	public static void moveCursorAddString(int x, int y, String str) {
		output.windowInFocus.moveCursor(x, y);
		output.windowInFocus.addString(str, x, y);
	}
	
	public static void moveCursor(int x, int y) {
		output.windowInFocus.moveCursor(x, y);
	}
	
	public static boolean kbhit() {
		return output.keyHandler.isKeyAvailable();
	}
	
	public static char getch() {
		char ch = 0;
		output.main.requestFocus();
		try {
			ch = output.keyHandler.requestKeyHit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ch;
	}
	
	public static void clearToEndOfLine() {
		output.windowInFocus.clearToEndOfLine();
	}
	
	public static void clearToBottom() {
		output.windowInFocus.clearToBottom();
	}
	
	public static void clear() {
		output.windowInFocus.clear();
	}
	
	private static Window createNewWindow(int height, int width, int offsetx, int offsety) {
		output.windowInFocus = new Window(height, width, offsetx, offsety);
		output.windows.add(output.windowInFocus);
		output.windowStack.add(0, output.windows.size() - 1);
		
		if (output.windows.size() == 1) {
			output.main.setBackground(Color.black);
			output.main.resize(
					output.windowInFocus.getWidth() * output.fWidth,
					output.windowInFocus.getHeight() * output.fHeight);
			
			output.bufferImg = output.main.createImage(
					output.windowInFocus.getWidth() * output.fWidth,
					output.windowInFocus.getHeight() * output.fHeight);
			output.bufferGraphics = output.bufferImg.getGraphics();
			output.bufferGraphics.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		}
		
		refresh();
		return output.windowInFocus;
	}
	
	public static void closeWindow() {
		if (output.windowStack.size() == 1) {
			System.exit(0);
		}
		
		int indexOfWindowClosing = output.windowStack.get(0);
		output.windows.remove((int)output.windowStack.get(0));
		output.windowStack.remove(0);
		
		for (int i = 0; i < output.windows.size(); i++) {
			if (output.windowStack.get(i) > indexOfWindowClosing) {
				output.windowStack.set(i, output.windowStack.get(i) - 1);
			}
		}
		
		output.windowInFocus = output.windows.get(output.windowStack.get(0));
		output.windowInFocus.setFocus(true);
		refreshAllWindows();
	}
	
	public static int getHeight() {
		return output.windowInFocus.getHeight();
	}
	
	public static int getWidth() {
		return output.windowInFocus.getWidth();
	}
	
	public static int getCursorX() {
		return output.windowInFocus.getCursorX();
	}
	
	public static int getCursorY() {
		return output.windowInFocus.getCursorY();
	}
	
	public static void refresh() {
		output.windowInFocus.refresh(output.bufferGraphics);
		output.main.getGraphics().drawImage(output.bufferImg, 0, 0, output.main);
	}
	
	private static void refreshAllWindows() {
		output.bufferGraphics.clearRect(0, 0, output.main.getWidth(), output.main.getHeight());
		
		for (int i = output.windows.size() - 1; i >= 0; i--) {
			output.windows.get(output.windowStack.get(i)).refreshAll(output.bufferGraphics);
		}
		
		output.windowInFocus.refreshAll(output.bufferGraphics);
		output.main.getGraphics().drawImage(output.bufferImg, 0, 0, output.main);
	}
	
	public static Window getWindow() {
		return output.windowInFocus;
	}
	
	public static void overwrite(Window win1, Window win2) {
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
