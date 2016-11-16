/*
 * IO.java: terminal I/O code, uses the curses package
 * 
 * Copyright (C) 1989-2008 James E. Wilson, Robert A. Koeneke, 
 *                         David J. Grabiner
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
package net.jonhopkins.moria;

import net.jonhopkins.moria.graphics.Output;
import net.jonhopkins.moria.graphics.Window;
import net.jonhopkins.moria.types.CharPointer;

public class IO {
	//typedef struct { int stuff; } fpvmach;
	
	//struct screen { int dumb; };
	
	private static boolean cursesOn = false;
	private static Window stdScreen;
	private static Window saveScreen;		/* Spare window for saving the screen. -CJS-*/
	
	private static final int COLS = 80;
	private static final int LINES = 24;
	
	private IO() { }
	
	/* suspend()							   -CJS-
	 * Handle the stop and start signals. This ensures that the log
	 * is up to date, and that the terminal is fully reset and
	 * restored.  */
	public static int suspend() {
		/*struct sgttyb tbuf;
		struct ltchars lcbuf;
		struct tchars cbuf;
		int lbuf;
		long time;
		
		py.misc.male |= 2;
		ioctl(0, TIOCGETP, String(tbuf));
		ioctl(0, TIOCGETC, String(cbuf));
		ioctl(0, TIOCGLTC, String(lcbuf));
		
		ioctl(0, TIOCLGET, String(lbuf));
		
		restore_term();
		kill(0, SIGSTOP);
		curses_on = TRUE;
		ioctl(0, TIOCSETP, String(tbuf));
		ioctl(0, TIOCSETC, String(cbuf));
		ioctl(0, TIOCSLTC, String(lcbuf));
		
		ioctl(0, TIOCLSET, String(lbuf));
		
		wrefresh(curscr);
		py.misc.male &= ~2;
		*/
		return 0;
	}
	
	/* initializes curses routines */
	public static void initCurses() {
		int i, y, x;
		
		/*
		Window w = new Window(40, 20, true, "Moria");
        DefaultLayoutManager mgr = new DefaultLayoutManager();
        mgr.bindToContainer(w.getRootPanel());
        mgr.addWidget(
            new Label("Hello World!", new CharColor(CharColor.WHITE, CharColor.GREEN)),
            0, 0, 40, 20,
            WidgetsConstants.ALIGNMENT_CENTER,
            WidgetsConstants.ALIGNMENT_CENTER);
        w.show();
        Thread.currentThread().sleep(5000);
        w.close(); // reset the native console
		*/
		/*
		ioctl(0, TIOCGLTC, String(save_special_chars));
		ioctl(0, TIOCGETP, String(save_ttyb));
		ioctl(0, TIOCGETC, String(save_tchars));
		
		ioctl(0, TIOCLGET, String(save_local_chars));
		*/
		/* PC curses returns ERR */
		/*
		if (initscr() == ERR) {
			printf("Error allocating screen in curses package.\n");
			exit(1);
		}
		*/
		//if (LINES < 24 || COLS < 80) {	 /* Check we have enough screen. -CJS- */
		//	printf("Screen too small for moria.\n");
		//	exit(1);
		//}
		
		//signal (SIGTSTP, suspend);
		/*
		if (((savescr = newwin (0, 0, 0, 0)) == NULL) || ((tempscr = newwin (0, 0, 0, 0)) == NULL))) {
			printf ("Out of memory in starting up curses.\n");
			exit_game();
		}
		
		clear();
		refresh();
		moriaterm ();
		*/
		/* This assumes that the terminal is 80 characters wide, which is not
		 * guaranteed to be true.  */
		
		/* check tab settings, exit with error if they are not 8 spaces apart */
		Output.moveCursor(0, 0);
		for (i = 1; i < 10; i++) {
			Output.addCharacter('\t');
			y = Output.getCursorY();
			x = Output.getCursorX();
			
			if (y != 0 || x != i * 8) {
				break;
			}
		}
		
		if (i != 10) {
			printMessage("Tabs must be set 8 spaces apart.");
			Death.exitGame();
		}
		
		stdScreen = Output.getWindow();
		saveScreen = new Window(Output.getHeight(), Output.getWidth());
	}
	
	/* Set up the terminal into a suitable state for moria.	 -CJS- */
	public static void moriaTerminal() {
		cursesOn = true;
		//use_value(crmode());
		//use_value(noecho());
		/* can not use nonl(), because some curses do not handle it correctly */
		//msdos_raw();
	}
	
	/* Dump IO to buffer					-RAK-	*/
	public static void putBuffer(String out_str, int row, int col) {
		//vtype tmp_str;
		String tmp_str;
		
		/* truncate the string, to make sure that it won't go past right edge of
		 * screen */
		if (col > 79) {
			col = 79;
		}
		
		int len = 79 - col;
		if (len > out_str.length()) {
			len = out_str.length();
		}
		
		tmp_str = out_str.substring(0, len);
		//tmp_str [79 - col] = '\0';
		Output.moveCursorAddString(col, row, tmp_str);
		//if (mvaddstr(row, col, tmp_str) == ERR) {
		//	abort();
			/* clear msg_flag to avoid problems with unflushed messages */
		//	var.msg_flag = 0;
		//	sprintf(tmp_str, "error in put_buffer, row = %d col = %d\n", row, col);
		//	prt(tmp_str, 0, 0);
		//	bell();
			/* wait so user can see error */
		//	Thread.sleep(2);
		//}
	}
	
	/* Dump the IO buffer to terminal			-RAK-	*/
	public static void putQio() {
		Variable.didScreenChange = true;	/* Let inven_command know something has changed. */
		Output.refresh();
	}
	
	/* Put the terminal in the original mode.			   -CJS- */
	public static void restoreTerminal() {
		if (!cursesOn) {
			return;
		}
		
		putQio();  /* Dump any remaining buffer */
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}   /* And let it be read. */
		/* this moves curses to bottom right corner */
		//mvcur(stdscr._cury, stdscr._curx, LINES-1, 0);
		Output.moveCursor(0, LINES - 1);
		Output.closeWindow();
		//endwin();  /* exit curses */
		//fflush (stdout);
		//msdos_noraw();
		/* restore the saved values of the special chars */
		//ioctl(0, TIOCSLTC, (char *)&save_special_chars);
		//ioctl(0, TIOCSETP, (char *)&save_ttyb);
		//ioctl(0, TIOCSETC, (char *)&save_tchars);
		//ioctl(0, TIOCLSET, (char *)&save_local_chars);
		cursesOn = false;
	}
	
	public static void shellOut() {
		//struct sgttyb tbuf;
		//struct ltchars lcbuf;
		//struct tchars cbuf;
		//int lbuf;
		//String comspec;
		char key;		  	
		saveScreen();
		/* clear screen and print 'exit' message */
		clearScreen();
		putBuffer("[Entering shell, type 'exit' to resume your game.]\n",0,0);
		putQio();
		
		//ioctl(0, TIOCGETP, (char *)&tbuf);
		//ioctl(0, TIOCGETC, (char *)&cbuf);
		//ioctl(0, TIOCGLTC, (char *)&lcbuf);
		//ioctl(0, TIOCLGET, (char *)&lbuf);
		/* would call nl() here if could use nl()/nonl(), see moriaterm() */
		
		//use_value(nocrmode());
		//use_value(msdos_noraw());
		//use_value(echo());
		Signals.ignoreSignals();
		if (System.getenv("COMSPEC").equals("")) { // ||  spawnl(P_WAIT, comspec, comspec, CNIL) < 0) {
			clearScreen();	/* BOSS key if shell failed */
			putBuffer("M:\\> ", 0, 0);
			do {
				key = inkey();
			} while (key != '!');
		}
		
		Signals.restoreSignals();
		/* restore the cave to the screen */
		restoreScreen();
		
		//use_value(vms_crmode());
		//use_value(cbreak());
		//use_value(noecho());
		/* would call nonl() here if could use nl()/nonl(), see moriaterm() */
		
		//msdos_raw();
		
		/* disable all of the local special characters except the suspend char */
		/* have to disable ^Y for tunneling */
		
		//ioctl(0, TIOCSLTC, (char *)&lcbuf);
		//ioctl(0, TIOCSETP, (char *)&tbuf);
		//ioctl(0, TIOCSETC, (char *)&cbuf);
		//ioctl(0, TIOCLSET, (char *)&lbuf);
		
		//wrefresh(curscr);
	}
	
	/* Returns a single character input from the terminal.	This silently -CJS-
	 * consumes ^R to redraw the screen and reset the terminal, so that this
	 * operation can always be performed at any input prompt.  inkey() never
	 * returns ^R.	*/
	public static char inkey() {
		char i;
		
		putQio();			/* Dump IO buffer		*/
		Variable.commandCount = 0;	/* Just to be safe -CJS- */
		while (true) {
			i = Output.getch();
			
			/* some machines may not sign extend. */
			if (i == 0 /* EOF */) {
				Variable.eofFlag++;
				/* avoid infinite loops while trying to call inkey() for a -more-
				 * prompt. */
				Variable.msgFlag = Constants.FALSE;
				
				Output.refresh();
				if (!Variable.isCharacterGenerated || Variable.characterSaved != 0) {
					Death.exitGame();
				}
				
				Moria1.disturbPlayer(true, false);
				
				if (Variable.eofFlag > 100) {
					/* just in case, to make sure that the process eventually dies */
					Variable.panicSave = 1;
					Variable.diedFrom = "(end of input: panic saved)";
					if (!Save.saveCharacter()) {
						Variable.diedFrom = "panic: unexpected eof";
						Variable.death = true;
					}
					Death.exitGame();
				}
				return Constants.ESCAPE;
			}
			if (i != (Constants.CTRL & 'R')) {
				return i;
			}
			
			//wrefresh(curscr);
			moriaTerminal();
	    }
	}
	
	public static boolean isKeyAvailable() {
		return Output.kbhit();
	}
	
	public static char getChar() {
		return Output.getch();
	}
	
	/* Flush the buffer					-RAK-	*/
	public static void flush() {
		while (Output.kbhit()) {
			Output.getch();
		}
		/* used to call put_qio() here to drain output, but it is not necessary */
	}
	
	/* Clears given line of text				-RAK-	*/
	public static void eraseLine(int row, int col) {
		if (row == Constants.MSG_LINE && Variable.msgFlag > 0) {
			printMessage("");
		}
		
		Output.moveCursor(col, row);
		Output.clearToEndOfLine();
	}
	
	/* Clears screen */
	public static void clearScreen() {
		if (Variable.msgFlag > 0) {
			printMessage("");
		}
		Output.clear();
	}
	
	public static void clearFrom(int row) {
		Output.moveCursor(0, row);
		Output.clearToBottom();
	}
	
	/* Outputs a char to a given interpolated y, x position	-RAK-	*/
	/* sign bit of a character used to indicate standout mode. -CJS */
	public static void print(char ch, int row, int col) {
		//char[] tmp_str = new char[Constants.VTYPESIZ];
		//String tmp_str;
		
		row -= Variable.panelRowPrt;/* Real co-ords convert to screen positions */
		col -= Variable.panelColPrt;
		Output.moveCursorAddCharacter(col, row, ch);
		//if (mvaddch(row, col, ch) == ERR) {
		//	abort();
			/* clear msg_flag to avoid problems with unflushed messages */
		//	var.msg_flag = 0;
		//	tmp_str = String.format("error in print, row = %d col = %d\n", row, col);
		//	prt(tmp_str, 0, 0);
		//	bell();
			/* wait so user can see error */
		//	Thread.sleep(2);
	    //}
	}
	
	/* Moves the cursor to a given interpolated y, x position	-RAK-	*/
	public static void moveCursorRelative(int row, int col) {
		//char[] tmp_str = new char[Constants.VTYPESIZ];
		
		row -= Variable.panelRowPrt;/* Real co-ords convert to screen positions */
		col -= Variable.panelColPrt;
		Output.moveCursor(col, row);
		//if (move(row, col) == ERR) {
		//	abort();
			/* clear msg_flag to avoid problems with unflushed messages */
		//	var.msg_flag = 0;
		//	sprintf(tmp_str, "error in move_cursor_relative, row = %d col = %d\n", row, col);
		//	prt(tmp_str, 0, 0);
		//	bell();
			/* wait so user can see error */
		//	sleep(2);
		//}
	}
	
	/* Print a message so as not to interrupt a counted command. -CJS- */
	public static void countMessagePrint(String p) {
		int i;
		
		i = Variable.commandCount;
		printMessage(p);
		Variable.commandCount = i;
	}
	
	/* Outputs a line to a given y, x position		-RAK-	*/
	public static void print(String str_buff, int row, int col) {
		if (row == Constants.MSG_LINE && Variable.msgFlag > 0) {
			printMessage("");
		}
		
		Output.moveCursor(col, row);
		Output.clearToEndOfLine();
		putBuffer(str_buff, row, col);
	}
	
	/* move cursor to a given y, x position */
	public static void moveCursor(int row, int col) {
		Output.moveCursor(col, row);
	}
	
	/* Outputs message to top line of screen				*/
	/* These messages are kept for later reference.	 */
	public static void printMessage(String str_buff) {
		int old_len = 0, new_len;
		boolean combine_messages = false;
		char in_char;
		
		if (Variable.msgFlag == Constants.TRUE) {
			old_len = Variable.oldMsg[Variable.lastMsg].length() + 1;
			
			/* If the new message and the old message are short enough, we want
		 	 * display them together on the same line.  So we don't flush the old
		 	 * message in this case.  */
			
			if (!str_buff.equals("")) {
				new_len = str_buff.length();
			} else {
				new_len = 0;
			}
			
			if (str_buff.equals("") || (new_len + old_len + 2 >= 73)) {
				/* ensure that the complete -more- message is visible. */
				if (old_len > 73) {
					old_len = 73;
				}
				
				putBuffer(" -more-", Constants.MSG_LINE, old_len);
				/* let sigint handler know that we are waiting for a space */
				Variable.waitForMore = 1;
				do {
					in_char = inkey();
				} while ((in_char != ' ') && (in_char != Constants.ESCAPE) && (in_char != '\n') && (in_char != '\r'));
				
				Variable.waitForMore = 0;
			} else {
				combine_messages = true;
			}
		}
		
		if (!combine_messages) {
			Output.moveCursor(0, Constants.MSG_LINE);
			Output.clearToEndOfLine();
		}
		
		/* Make the null string a special case.  -CJS- */
		if (!str_buff.equals("")) {
			Variable.commandCount = 0;
			Variable.msgFlag = 1;
			/* If the new message and the old message are short enough, display
			 * them on the same line.  */
			if (combine_messages) {
				putBuffer(str_buff, Constants.MSG_LINE, old_len + 2);
				Variable.oldMsg[Variable.lastMsg] = Variable.oldMsg[Variable.lastMsg].concat("  ");
				Variable.oldMsg[Variable.lastMsg] = Variable.oldMsg[Variable.lastMsg].concat(str_buff);
			} else {
				putBuffer(str_buff, Constants.MSG_LINE, 0);
				Variable.lastMsg++;
				if (Variable.lastMsg >= Constants.MAX_SAVE_MSG) {
					Variable.lastMsg = 0;
				}
				Variable.oldMsg[Variable.lastMsg]= str_buff; 
				//strncpy(var.old_msg[var.last_msg], str_buff, Constants.VTYPESIZ);
				//var.old_msg[var.last_msg][Constants.VTYPESIZ - 1] = '\0';
			}
			Output.refresh();
		} else {
			Variable.msgFlag = Constants.FALSE;
		}
	}
	
	/* Used to verify a choice - user gets the chance to abort choice.  -CJS- */
	public static boolean getCheck(String prompt) {
		int res;
		int y, x;
		
		print(prompt, 0, 0);
		//getyx(stdscr, y, x);
		y = Output.getCursorY();
		x = Output.getCursorX();
		
		x = y;
		res = y;
		
		if (x > 73) {
			Output.moveCursor(73, 0);
		}
		
		Output.addString(" [y/n]");
		
		do {
			res = inkey();
		} while(res == ' ');
		
		eraseLine(0, 0);
		
		return res == 'Y' || res == 'y';
	}
	
	/* Prompts (optional) and returns ord value of input char	*/
	/* Function returns false if <ESCAPE> is input	*/
	public static boolean getCommand(String prompt, CharPointer command) {
		boolean res;
		
		if (prompt != null) {
			print(prompt, 0, 0);
		}
		
		command.value(inkey());
		if (command.value() == Constants.ESCAPE) {
			res = false;
		} else {
			res = true;
		}
		eraseLine(Constants.MSG_LINE, 0);
		return res;
	}
	
	/* Gets a string terminated by <RETURN>		*/
	/* Function returns false if <ESCAPE> is input	*/
	public static String getString(int row, int column, int slen) {
		int start_col, end_col, i;
		StringBuilder in_str = new StringBuilder(slen);
		boolean flag, aborted;
		
		aborted = false;
		flag = false;
		
		Output.moveCursor(column, row);
		for (i = slen; i > 0; i--) {
			Output.addCharacter(' ');
		}
		
		Output.moveCursor(column, row);
		
		start_col = column;
		end_col = column + slen - 1;
		if (end_col > 79) {
			slen = 80 - column;
			end_col = 79;
	    }
		
		do {
			i = inkey();
			switch(i) {
				case Constants.ESCAPE:
					aborted = true;
					break;
				case (Constants.CTRL & 'J'): case (Constants.CTRL & 'M'):
					flag = true;
					break;
				case Constants.DELETE: case (Constants.CTRL & 'H'):
					if (column > start_col) {
						column--;
						putBuffer(" ", row, column);
						moveCursor(row, column);
						//*--p = '\0';
						in_str.setLength(in_str.length() - 1);
					}
					break;
				default:
					if (!(i >= 32 && i <= 126) || column > end_col) {
						bell();
					} else {
						Output.moveCursorAddCharacter(column, row, (char)i);
						//*p++ = i;
						in_str.append((char)i);
						column++;
					}
					break;
			}
	    } while ((!flag) && (!aborted));
		
		if (aborted) {
			return in_str.toString();
		}
		/* Remove trailing blanks	*/
		while (in_str.length() > 1 && in_str.charAt(in_str.length() - 1) == ' ') {
			in_str.setLength(in_str.length() - 1);
		}
		
		return in_str.toString();
	}
	
	/* Pauses for user response before returning		-RAK-	*/
	public static void pauseLine(int prt_line) {
		print("[Press any key to continue.]", prt_line, 23);
		inkey();
		eraseLine(prt_line, 0);
	}
	
	/* Pauses for user response before returning		-RAK-	*/
	/* NOTE: Delay is for players trying to roll up "perfect"	*/
	/*	characters.  Make them wait a bit.			*/
	public static void pauseExit(int prt_line, int delay) {
		char dummy;
		
		print("[Press any key to continue, or Q to exit.]", prt_line, 10);
		dummy = inkey();
		if (dummy == 'Q') {
			eraseLine(prt_line, 0);
			Death.exitGame();
		}
		/*
		try {
			Thread.sleep(delay * 100);
		} catch (InterruptedException e) {
			
		}
		*/
		eraseLine(prt_line, 0);
	}
	
	public static void saveScreen() {
		Output.overwrite(stdScreen, saveScreen);
	}
	
	public static void restoreScreen() {
		Output.overwrite(saveScreen, stdScreen);
		//touchwin(stdscr);
	}
	
	public static void bell() {
		putQio();
		
		/* The player can turn off beeps if he/she finds them annoying.  */
		if (!Variable.soundBeepFlag.value()) {
			return;
		}
		
		//System.out.println("\007");
		//System.out.flush();
		java.awt.Toolkit.getDefaultToolkit().beep();
	}
	
	/* definitions used by screen_map() */
	/* index into border character array */
	private static final int TL = 0;	/* top left */
	private static final int TR = 1;
	private static final int BL = 2;
	private static final int BR = 3;
	private static final int HE = 4;	/* horizontal edge */
	private static final int VE = 5;
	private static char[][] screenBorder = new char[][] {
		{'+', '+', '+', '+', '-', '|'},	/* normal chars */
    	{201, 187, 200, 188, 205, 186}	/* graphics chars */
	};
	
	private static char borderChar(int x) {
		return screenBorder[1][x];
	}
	
	/* Display highest priority object in the RATIO by RATIO area */
	private static final int RATIO = 3;
	
	public static void screenMap() {
		int i, j;
		char[] map = new char[Constants.MAX_WIDTH / RATIO + 1];
		char tmp;
		int[] priority = new int[256];
		int row, orow, col, myrow = 0, mycol = 0;
		//char[] prntscrnbuf = new char[80];
		String prntscrnbuf;
		
		for (i = 0; i < 256; i++) {
			priority[i] = 0;
		}
		
		priority['<'] = 5;
		priority['>'] = 5;
		priority['@'] = 10;
		priority[Variable.wallSymbol] = -5;
		priority[Variable.floorSymbol] = -10;
		priority['\''] = -3;
		priority[' '] = -15;
		
		saveScreen();
		clearScreen();
		Output.moveCursorAddCharacter(0, 0, borderChar(TL));
		for (i = 0; i < Constants.MAX_WIDTH / RATIO; i++) {
			Output.addCharacter(borderChar(HE));
		}
		
		Output.addCharacter(borderChar(TR));
		
		orow = -1;
		map[Constants.MAX_WIDTH / RATIO] = '\0';
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			row = i / RATIO;
			if (row != orow) {
				if (orow >= 0) {
					/* can not use mvprintw() on ibmpc, because PC-Curses is horribly
			 		 * written, and mvprintw() causes the fp emulation library to be
			 		 * linked with PC-Moria, makes the program 10K bigger */
					prntscrnbuf = String.format("%c%s%c", borderChar(VE), new String(map), borderChar(VE));
					Output.moveCursorAddString(0, orow + 1, prntscrnbuf);
				}
				
				for (j = 0; j < Constants.MAX_WIDTH / RATIO; j++) {
					map[j] = ' ';
				}
				
				orow = row;
			}
			
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				col = j / RATIO;
				tmp = Misc1.locateSymbol(i, j);
				
				if (priority[map[col]] < priority[tmp]) {
					map[col] = tmp;
				}
				
				if (map[col] == '@') {
					mycol = col + 1; /* account for border */
					myrow = row + 1;
				}
			}
		}
		
		if (orow >= 0) {
			prntscrnbuf = String.format("%c%s%c",borderChar(VE), new String(map), borderChar(VE));
			Output.moveCursorAddString(0, orow + 1, prntscrnbuf);
		}
		
		Output.moveCursorAddCharacter(0, orow + 2, borderChar(BL));
		for (i = 0; i < Constants.MAX_WIDTH / RATIO; i++) {
			Output.addCharacter(borderChar(HE));
		}
		
		Output.addCharacter(borderChar(BR));
		
		Output.moveCursorAddString(23, 23, "Hit any key to continue");
		if (mycol > 0) {
			Output.moveCursor(mycol, myrow);
		}
		inkey();
		restoreScreen();
	}
}
