/**
 * KeyHandler.java: description
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

import java.awt.event.KeyEvent;

class KeyHandler {
	public KeyHandler(Output curses) {
		this.curses = curses;
	}
	
	public void handleKey(KeyEvent event) {
		//if (!acceptingKeyEvents) {
		//	return;
		//}
		
		
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
		
		if (waitingforkeyhit) {
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
			System.out.println("Got a key event '" + (new Integer(lastkeyhit)).toString() + "' asd");
			
			return;
		}
		
		System.out.println("missed the waitingforkeyhit");
		
		if (!cursorEnabled) {
			switch (keystroke) {
			case KEY_CLOSE:
				curses.closeWindow();
				break;
			case KEY_EXIT:
				System.exit(0);
			case KEY_NEXT:
				curses.setFocusNextWindow();
				break;
			case KEY_PREVIOUS:
				curses.setFocusPreviousWindow();
				break;
			case KEY_OPEN:
				curses.createNewWindow(30, 20, 0, 0);
				curses.addBorder();
				curses.refresh();
				break;
			case KEY_REFRESH:
				curses.refreshAllWindows();
				break;
			case KEY_SLEFT:
				curses.mvwin(4);
				break;
			case KEY_SRIGHT:
				curses.mvwin(3);
				break;
			case KEY_SUP:
				curses.mvwin(1);
				break;
			case KEY_SDOWN:
				curses.mvwin(2);
				break;
			}
			
			return;
		}
		
		switch (keystroke) {
		//case KEY_CODE_YES:
		//case KEY_BREAK:
		case KEY_DOWN:
			curses.scrollCursor(2);
			curses.refresh();
			break;
		case KEY_UP:
			curses.scrollCursor(1);
			curses.refresh();
			break;
		case KEY_LEFT:
			curses.scrollCursor(4);
			curses.refresh();
			break;
		case KEY_RIGHT:
			curses.scrollCursor(3);
			curses.refresh();
			break;
		case KEY_HOME:
		case KEY_BACKSPACE:
			curses.placeCharacter('\b');
			curses.refresh();
			break;
		//case KEY_F0:
		case KEY_DL:
			curses.deleteLine();
			curses.refreshWholeWindow();
			break;
		case KEY_IL:
			curses.insertLine();
			curses.refreshWholeWindow();
			break;
		case KEY_DC:
			curses.deleteCharacter();
			curses.refresh();
			break;
		case KEY_IC:
			break;
		//case KEY_EIC:
		//case KEY_CLEAR:
		//case KEY_EOS:
		//case KEY_EOL:
		//case KEY_SF:
		//case KEY_SR:
		//case KEY_NPAGE:
		//case KEY_PPAGE:
		//case KEY_STAB:
		//case KEY_CTAB:
		//case KEY_CATAB:
		case KEY_ENTER:
			curses.placeCharacter('\n');
			curses.refresh();
			break;
		//case KEY_SRESET:
		//case KEY_RESET:
		//case KEY_PRINT:
		//case KEY_LL:
		//case KEY_A1:
		//case KEY_A3:
		//case KEY_B2:
		//case KEY_C1:
		//case KEY_C3: 
		//case KEY_BTAB:
		//case KEY_BEG:
		//case KEY_CANCEL:
		case KEY_CLOSE:
			curses.closeWindow();
			break;
		//case KEY_COMMAND:
		//case KEY_COPY:
		//case KEY_CREATE:
		//case KEY_END:
		case KEY_EXIT:
			System.exit(0);
		//case KEY_FIND:
		//case KEY_HELP:
		//case KEY_MARK:
		//case KEY_MESSAGE:
		//case KEY_MOVE:
		case KEY_NEXT:
			curses.setFocusNextWindow();
			break;
		case KEY_PREVIOUS:
			curses.setFocusPreviousWindow();
			break;
		case KEY_OPEN:
			curses.createNewWindow(30, 20, 0, 0);
			curses.addBorder();
			curses.refresh();
			break;
		//case KEY_OPTIONS:
		//case KEY_REDO:
		//case KEY_REFERENCE:
		case KEY_REFRESH:
			curses.refreshAllWindows();
			break;
		//case KEY_REPLACE:
		//case KEY_RESTART:
		//case KEY_RESUME:
		//case KEY_SAVE:
		//case KEY_SBEG:
		//case KEY_SCANCEL:
		//case KEY_SCOMMAND:
		//case KEY_SCOPY:
		//case KEY_SCREATE:
		//case KEY_SDC:
		//case KEY_SDL:
		//case KEY_SELECT:
		//case KEY_SEND:
		//case KEY_SEOL:
		//case KEY_SEXIT:
		//case KEY_SFIND:
		//case KEY_SHELP:
		//case KEY_SHOME:
		//case KEY_SIC:
		case KEY_SLEFT:
			curses.mvwin(4);
			break;
		case KEY_SRIGHT:
			curses.mvwin(3);
			break;
		case KEY_SUP:
			curses.mvwin(1);
			break;
		case KEY_SDOWN:
			curses.mvwin(2);
			break;
		//case KEY_SMESSAGE:
		//case KEY_SMOVE:
		//case KEY_SNEXT:
		//case KEY_SOPTIONS:
		//case KEY_SPREVIOUS:
		//case KEY_SPRINT:
		//case KEY_SREDO:
		//case KEY_SREPLACE:
		//case KEY_SRSUME:
		//case KEY_SSAVE:
		//case KEY_SSUSPEND:
		//case KEY_SUNDO:
		//case KEY_SUSPEND:
		//case KEY_UNDO:
		default:
			if ((keystroke & MOD_CTRL) != 0 || (keystroke & MOD_ALT) != 0) {
				break;
			}
			
			curses.addCharacter(event.getKeyChar());
			curses.refresh();
		}
	}
	
	public char requestKeyHit() {
		char ch = 0;
		
		waitingforkeyhit = true;
		
		if (lastkeyhit != 0) {
			System.out.println("lastkeyhit = " + lastkeyhit);
			ch = (char)lastkeyhit;
			lastkeyhit = 0;
			waitingforkeyhit = false;
		}
		
		return ch;
	}
	
	public void clearKeyHit() {
		lastkeyhit = 0;
	}
	
	public void cursorEnabled(boolean enabled) {
		cursorEnabled = enabled;
	}
	
	private final short MOD_SHIFT		=	0x0100;
	private final short MOD_CTRL		=	0x0200;
	private final short MOD_ALT			=	0x0400;
	private final short MOD_CTRLSHIFT	=	0x0800;
	private final short MOD_ALTSHIFT	=	0x1000;
	private final short MOD_CTRLALT		=	0x2000;
	
	private final short KEY_CODE_YES	=	0x0000;	//Used to indicate that a wchar_t variable contains a key code
	private final short KEY_BREAK		=	0x0000;	//Break key
	private final short KEY_DOWN		=	0x0028;	//Down arrow key
	private final short KEY_UP			=	0x0026;	//Up arrow key
	private final short KEY_LEFT		=	0x0025;	//Left arrow key
	private final short KEY_RIGHT		=	0x0027;	//Right arrow key
	private final short KEY_HOME		=	0x0024;	//Home key
	private final short KEY_BACKSPACE	=	0x0008;	//Backspace
	private final short KEY_F0			=	0x0000;	//Function keys; space for 64 keys is reserved
	//private short KEY_F(n);	//For 0 <=n<=63
	private final short KEY_DL			=	0x027f;	//Delete line
	private final short KEY_IL			=	0x029b;	//Insert line
	private final short KEY_DC			=	0x007f;	//Delete character
	private final short KEY_IC			=	0x009b;	//Insert char or enter insert mode
	private final short KEY_EIC			=	0x0000;	//Exit insert char mode
	private final short KEY_CLEAR		=	0x0000;	//Clear screen
	private final short KEY_EOS			=	0x0000;	//Clear to end of screen
	private final short KEY_EOL			=	0x0000;	//Clear to end of line
	private final short KEY_SF			=	0x0000;	//Scroll 1 line forward
	private final short KEY_SR			=	0x0000;	//Scroll 1 line backward (reverse)
	private final short KEY_NPAGE		=	0x0000;	//Next page
	private final short KEY_PPAGE		=	0x0000;	//Previous page
	private final short KEY_STAB		=	0x0000;	//Set tab
	private final short KEY_CTAB		=	0x0000;	//Clear tab
	private final short KEY_CATAB		=	0x0000;	//Clear all tabs
	private final short KEY_ENTER		=	0x000a;	//Enter or send
	private final short KEY_SRESET		=	0x0000;	//Soft (partial) reset
	private final short KEY_RESET		=	0x0000;	//Reset or hard reset
	private final short KEY_PRINT		=	0x0000;	//Print or copy
	private final short KEY_LL			=	0x0000;	//Home down or bottom
	private final short KEY_A1			=	0x0000;	//Upper left of keypad
	private final short KEY_A3			=	0x0000;	//Upper right of keypad
	private final short KEY_B2			=	0x0000;	//Center of keypad
	private final short KEY_C1			=	0x0000;	//Lower left of keypad
	private final short KEY_C3			=	0x0000;	//Lower right of keypad 

	private final short KEY_BTAB		=	0x0000;	//Back tab key
	private final short KEY_BEG			=	0x0000;	//Beginning key
	private final short KEY_CANCEL		=	0x0000;	//Cancel key
	private final short KEY_CLOSE		=	0x0243;	//Close key
	private final short KEY_COMMAND		=	0x0000;	//Cmd (command) key
	private final short KEY_COPY		=	0x0000;	//Copy key
	private final short KEY_CREATE		=	0x0000;	//Create key
	private final short KEY_END			=	0x0000;	//End key
	private final short KEY_EXIT		=	0x001b;	//Exit key
	private final short KEY_FIND		=	0x0000;	//Find key
	private final short KEY_HELP		=	0x0000;	//Help key
	private final short KEY_MARK		=	0x0000;	//Mark key
	private final short KEY_MESSAGE		=	0x0000;	//Message key
	private final short KEY_MOVE		=	0x0000;	//Move key
	private final short KEY_NEXT		=	0x0227;	//Next object key
	private final short KEY_PREVIOUS	=	0x0225;	//Previous object key
	private final short KEY_OPEN		=	0x024f;	//Open key
	private final short KEY_OPTIONS		=	0x0000;	//Options key
	private final short KEY_REDO		=	0x0000;	//Redo key
	private final short KEY_REFERENCE	=	0x0000;	//Reference key
	private final short KEY_REFRESH		=	0x0252;	//Refresh key
	private final short KEY_REPLACE		=	0x0000;	//Replace key
	private final short KEY_RESTART		=	0x0000;	//Restart key
	private final short KEY_RESUME		=	0x0000;	//Resume key
	private final short KEY_SAVE		=	0x0000;	//Save key
	private final short KEY_SBEG		=	0x0000;	//Shifted beginning key
	private final short KEY_SCANCEL		=	0x0000;	//Shifted cancel key
	private final short KEY_SCOMMAND	=	0x0000;	//Shifted command key
	private final short KEY_SCOPY		=	0x0000;	//Shifted copy key
	private final short KEY_SCREATE		=	0x0000;	//Shifted create key
	private final short KEY_SDC			=	0x0000;	//Shifted delete char key
	private final short KEY_SDL			=	0x0000;	//Shifted delete line key
	private final short KEY_SELECT		=	0x0000;	//Select key
	private final short KEY_SEND		=	0x0000;	//Shifted end key
	private final short KEY_SEOL		=	0x0000;	//Shifted clear line key
	private final short KEY_SEXIT		=	0x0000;	//Shifted exit key
	private final short KEY_SFIND		=	0x0000;	//Shifted find key
	private final short KEY_SHELP		=	0x0000;	//Shifted help key
	private final short KEY_SHOME		=	0x0000;	//Shifted home key
	private final short KEY_SIC			=	0x0000;	//Shifted input key
	private final short KEY_SLEFT		=	0x0125;	//Shifted left arrow key
	private final short KEY_SRIGHT		=	0x0127;	//Shifted right arrow key
	private final short KEY_SUP			=	0x0126;	//Shifted up arrow key
	private final short KEY_SDOWN		=	0x0128;	//Shifted down arrow key
	private final short KEY_SMESSAGE	=	0x0000;	//Shifted message key
	private final short KEY_SMOVE		=	0x0000;	//Shifted move key
	private final short KEY_SNEXT		=	0x0000;	//Shifted next key
	private final short KEY_SOPTIONS	=	0x0000;	//Shifted options key
	private final short KEY_SPREVIOUS	=	0x0000;	//Shifted prev key
	private final short KEY_SPRINT		=	0x0000;	//Shifted print key
	private final short KEY_SREDO		=	0x0000;	//Shifted redo key
	private final short KEY_SREPLACE	=	0x0000;	//Shifted replace key
	private final short KEY_SRSUME		=	0x0000;	//Shifted resume key
	private final short KEY_SSAVE		=	0x0000;	//Shifted save key
	private final short KEY_SSUSPEND	=	0x0000;	//Shifted suspend key
	private final short KEY_SUNDO		=	0x0000;	//Shifted undo key
	private final short KEY_SUSPEND		=	0x0000;	//Suspend key
	private final short KEY_UNDO		=	0x0000;	//Undo key
	
	private Output curses;
	private boolean waitingforkeyhit = false;
	private int lastkeyhit = 0;
	private boolean cursorEnabled = true;
}
