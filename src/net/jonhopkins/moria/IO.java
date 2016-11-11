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
	
	private static boolean curses_on = false;
	private static Window stdscr;
	private static Window savescr;		/* Spare window for saving the screen. -CJS-*/
	
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
	public static void init_curses() {
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
			msg_print("Tabs must be set 8 spaces apart.");
			Death.exit_game();
		}
		
		stdscr = Output.getWindow();
		savescr = new Window(Output.getHeight(), Output.getWidth());
	}
	
	/* Set up the terminal into a suitable state for moria.	 -CJS- */
	public static void moriaterm() {
		curses_on = true;
		//use_value(crmode());
		//use_value(noecho());
		/* can not use nonl(), because some curses do not handle it correctly */
		//msdos_raw();
	}
	
	/* Dump IO to buffer					-RAK-	*/
	public static void put_buffer(String out_str, int row, int col) {
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
	public static void put_qio() {
		Variable.screen_change = true;	/* Let inven_command know something has changed. */
		Output.refresh();
	}
	
	/* Put the terminal in the original mode.			   -CJS- */
	public static void restore_term() {
		if (!curses_on) {
			return;
		}
		
		put_qio();  /* Dump any remaining buffer */
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			
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
		curses_on = false;
	}
	
	public static void shell_out() {
		//struct sgttyb tbuf;
		//struct ltchars lcbuf;
		//struct tchars cbuf;
		//int lbuf;
		//String comspec;
		char key;		  	
		save_screen();
		/* clear screen and print 'exit' message */
		clear_screen();
		put_buffer("[Entering shell, type 'exit' to resume your game.]\n",0,0);
		put_qio();
		
		//ioctl(0, TIOCGETP, (char *)&tbuf);
		//ioctl(0, TIOCGETC, (char *)&cbuf);
		//ioctl(0, TIOCGLTC, (char *)&lcbuf);
		//ioctl(0, TIOCLGET, (char *)&lbuf);
		/* would call nl() here if could use nl()/nonl(), see moriaterm() */
		
		//use_value(nocrmode());
		//use_value(msdos_noraw());
		//use_value(echo());
		Signals.ignore_signals();
		if (System.getenv("COMSPEC").equals("")) { // ||  spawnl(P_WAIT, comspec, comspec, CNIL) < 0) {
			clear_screen();	/* BOSS key if shell failed */
			put_buffer("M:\\> ", 0, 0);
			do {
				key = inkey();
			} while (key != '!');
		}
		
		Signals.restore_signals();
		/* restore the cave to the screen */
		restore_screen();
		
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
		/*
import jcurses.system.InputChar;
import jcurses.system.Toolkit;

public class Console {
  public static void main (String[] args) {
    while (true) {
      InputChar c = Toolkit.readCharacter ();
      System.out.println (”you typed ‘” + c.getCharacter() + “‘ (” + c.getCode() + “)”);

      // break on Ctrl-C (3)
      if (c.getCode() == 3) break;
    }
  }
}
		 */
		
		
		char i;
		
		put_qio();			/* Dump IO buffer		*/
		Variable.command_count = 0;	/* Just to be safe -CJS- */
		while (true) {
			i = Output.getch();
			
			/* some machines may not sign extend. */
			if (i == 0 /* EOF */) {
				Variable.eof_flag++;
				/* avoid infinite loops while trying to call inkey() for a -more-
				 * prompt. */
				Variable.msg_flag = Constants.FALSE;
				
				Output.refresh();
				if (!Variable.character_generated || Variable.character_saved != 0) {
					Death.exit_game();
				}
				
				Moria1.disturb(true, false);
				
				if (Variable.eof_flag > 100) {
					/* just in case, to make sure that the process eventually dies */
					Variable.panic_save = 1;
					Variable.died_from = "(end of input: panic saved)";
					if (!Save.save_char()) {
						Variable.died_from = "panic: unexpected eof";
						Variable.death = true;
					}
					Death.exit_game();
				}
				return Constants.ESCAPE;
			}
			if (i != (Constants.CTRL & 'R')) {
				return i;
			}
			
			//wrefresh(curscr);
			moriaterm();
	    }
	}
	
	public static boolean isKeyAvailable() {
		return Output.kbhit();
	}
	
	public static char getch() {
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
	public static void erase_line(int row, int col) {
		if (row == Constants.MSG_LINE && Variable.msg_flag > 0) {
			msg_print("");
		}
		
		Output.moveCursor(col, row);
		Output.clearToEndOfLine();
	}
	
	/* Clears screen */
	public static void clear_screen() {
		if (Variable.msg_flag > 0) {
			msg_print("");
		}
		Output.clear();
	}
	
	public static void clear_from(int row) {
		Output.moveCursor(0, row);
		Output.clearToBottom();
	}
	
	/* Outputs a char to a given interpolated y, x position	-RAK-	*/
	/* sign bit of a character used to indicate standout mode. -CJS */
	public static void print(char ch, int row, int col) {
		//char[] tmp_str = new char[Constants.VTYPESIZ];
		//String tmp_str;
		
		row -= Variable.panel_row_prt;/* Real co-ords convert to screen positions */
		col -= Variable.panel_col_prt;
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
	public static void move_cursor_relative(int row, int col) {
		//char[] tmp_str = new char[Constants.VTYPESIZ];
		
		row -= Variable.panel_row_prt;/* Real co-ords convert to screen positions */
		col -= Variable.panel_col_prt;
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
	public static void count_msg_print(String p) {
		int i;
		
		i = Variable.command_count;
		msg_print(p);
		Variable.command_count = i;
	}
	
	/* Outputs a line to a given y, x position		-RAK-	*/
	public static void prt(String str_buff, int row, int col) {
		if (row == Constants.MSG_LINE && Variable.msg_flag > 0) {
			msg_print("");
		}
		
		Output.moveCursor(col, row);
		Output.clearToEndOfLine();
		put_buffer(str_buff, row, col);
	}
	
	/* move cursor to a given y, x position */
	public static void move_cursor(int row, int col) {
		Output.moveCursor(col, row);
	}
	
	/* Outputs message to top line of screen				*/
	/* These messages are kept for later reference.	 */
	public static void msg_print(String str_buff) {
		int old_len = 0, new_len;
		boolean combine_messages = false;
		char in_char;
		
		if (Variable.msg_flag == Constants.TRUE) {
			old_len = Variable.old_msg[Variable.last_msg].length() + 1;
			
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
				
				put_buffer(" -more-", Constants.MSG_LINE, old_len);
				/* let sigint handler know that we are waiting for a space */
				Variable.wait_for_more = 1;
				do {
					in_char = inkey();
				} while ((in_char != ' ') && (in_char != Constants.ESCAPE) && (in_char != '\n') && (in_char != '\r'));
				
				Variable.wait_for_more = 0;
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
			Variable.command_count = 0;
			Variable.msg_flag = 1;
			/* If the new message and the old message are short enough, display
			 * them on the same line.  */
			if (combine_messages) {
				put_buffer(str_buff, Constants.MSG_LINE, old_len + 2);
				Variable.old_msg[Variable.last_msg] = Variable.old_msg[Variable.last_msg].concat("  ");
				Variable.old_msg[Variable.last_msg] = Variable.old_msg[Variable.last_msg].concat(str_buff);
			} else {
				put_buffer(str_buff, Constants.MSG_LINE, 0);
				Variable.last_msg++;
				if (Variable.last_msg >= Constants.MAX_SAVE_MSG) {
					Variable.last_msg = 0;
				}
				Variable.old_msg[Variable.last_msg]= str_buff; 
				//strncpy(var.old_msg[var.last_msg], str_buff, Constants.VTYPESIZ);
				//var.old_msg[var.last_msg][Constants.VTYPESIZ - 1] = '\0';
			}
			Output.refresh();
		} else {
			Variable.msg_flag = Constants.FALSE;
		}
	}
	
	/* Used to verify a choice - user gets the chance to abort choice.  -CJS- */
	public static boolean get_check(String prompt) {
		int res;
		int y, x;
		
		prt(prompt, 0, 0);
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
		
		erase_line(0, 0);
		
		if (res == 'Y' || res == 'y') {
			return true;
		} else {
			return false;
		}
	}
	
	/* Prompts (optional) and returns ord value of input char	*/
	/* Function returns false if <ESCAPE> is input	*/
	public static boolean get_com(String prompt, CharPointer command) {
		boolean res;
		
		if (prompt != null)
			prt(prompt, 0, 0);
		
		command.value(inkey());
		if (command.value() == Constants.ESCAPE) {
			res = false;
		} else {
			res = true;
		}
		erase_line(Constants.MSG_LINE, 0);
		return res;
	}
	
	/* Gets a string terminated by <RETURN>		*/
	/* Function returns false if <ESCAPE> is input	*/
	public static String get_string(int row, int column, int slen) {
		int start_col, end_col, i;
		String in_str = "";
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
						put_buffer(" ", row, column);
						move_cursor(row, column);
						//*--p = '\0';
						in_str = in_str.substring(0, in_str.length() - 1);
					}
					break;
				default:
					if (!(i >= 32 && i <= 126) || column > end_col) {
						bell();
					} else {
						Output.moveCursorAddCharacter(column, row, (char)i);
						//*p++ = i;
						in_str += (char)i;
						column++;
					}
					break;
			}
	    } while ((!flag) && (!aborted));
		
		if (aborted) {
			return in_str;
		}
		/* Remove trailing blanks	*/
		while (in_str.length() > 1 && in_str.charAt(in_str.length() - 1) == ' ') {
			in_str = in_str.substring(0, in_str.length() - 1);
		}
		
		return in_str;
	}
	
	/* Pauses for user response before returning		-RAK-	*/
	public static void pause_line(int prt_line) {
		prt("[Press any key to continue.]", prt_line, 23);
		inkey();
		erase_line(prt_line, 0);
	}
	
	/* Pauses for user response before returning		-RAK-	*/
	/* NOTE: Delay is for players trying to roll up "perfect"	*/
	/*	characters.  Make them wait a bit.			*/
	public static void pause_exit(int prt_line, int delay) {
		char dummy;
		
		prt("[Press any key to continue, or Q to exit.]", prt_line, 10);
		dummy = inkey();
		if (dummy == 'Q') {
			erase_line(prt_line, 0);
			Death.exit_game();
		}
		/*
		try {
			Thread.sleep(delay * 100);
		} catch (InterruptedException e) {
			
		}
		*/
		erase_line(prt_line, 0);
	}
	
	public static void save_screen() {
		Output.overwrite(stdscr, savescr);
	}
	
	public static void restore_screen() {
		Output.overwrite(savescr, stdscr);
		//touchwin(stdscr);
	}
	
	public static void bell() {
		put_qio();
		
		/* The player can turn off beeps if he/she finds them annoying.  */
		if (!Variable.sound_beep_flag.value()) {
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
	private static char[][] screen_border = new char[][] {
		{'+', '+', '+', '+', '-', '|'},	/* normal chars */
    	{201, 187, 200, 188, 205, 186}	/* graphics chars */
	};
	
	private static char CH(int x) {
		return screen_border[1][x];
	}
	
	/* Display highest priority object in the RATIO by RATIO area */
	private static final int RATIO = 3;
	
	public static void screen_map() {
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
		priority[Variable.wallsym] = -5;
		priority[Variable.floorsym] = -10;
		priority['\''] = -3;
		priority[' '] = -15;
		
		save_screen();
		clear_screen();
		Output.moveCursorAddCharacter(0, 0, CH(TL));
		for (i = 0; i < Constants.MAX_WIDTH / RATIO; i++) {
			Output.addCharacter(CH(HE));
		}
		
		Output.addCharacter(CH(TR));
		
		orow = -1;
		map[Constants.MAX_WIDTH / RATIO] = '\0';
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			row = i / RATIO;
			if (row != orow) {
				if (orow >= 0) {
					/* can not use mvprintw() on ibmpc, because PC-Curses is horribly
			 		 * written, and mvprintw() causes the fp emulation library to be
			 		 * linked with PC-Moria, makes the program 10K bigger */
					prntscrnbuf = String.format("%c%s%c", CH(VE), new String(map), CH(VE));
					Output.moveCursorAddString(0, orow + 1, prntscrnbuf);
				}
				
				for (j = 0; j < Constants.MAX_WIDTH / RATIO; j++) {
					map[j] = ' ';
				}
				
				orow = row;
			}
			
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				col = j / RATIO;
				tmp = Misc1.loc_symbol(i, j);
				
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
			prntscrnbuf = String.format("%c%s%c",CH(VE), new String(map), CH(VE));
			Output.moveCursorAddString(0, orow + 1, prntscrnbuf);
		}
		
		Output.moveCursorAddCharacter(0, orow + 2, CH(BL));
		for (i = 0; i < Constants.MAX_WIDTH / RATIO; i++) {
			Output.addCharacter(CH(HE));
		}
		
		Output.addCharacter(CH(BR));
		
		Output.moveCursorAddString(23, 23, "Hit any key to continue");
		if (mycol > 0) {
			Output.moveCursor(mycol, myrow);
		}
		inkey();
		restore_screen();
	}
}
