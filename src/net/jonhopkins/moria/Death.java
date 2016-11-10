/* 
 * Death.java: code executed when player dies
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

import java.util.Calendar;

import net.jonhopkins.moria.types.HighScoreType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.Stats;

public class Death {
	
	private static final int L_SET = 0;
	private static final int L_INCR = 1;
	
	private Death() { }
	
	public static String date() {
		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); 
		
		switch (day) {
		case Calendar.SUNDAY:
			return "Sunday";
		case Calendar.MONDAY:
			return "Monday";
		case Calendar.TUESDAY:
			return "Tuesday";
		case Calendar.WEDNESDAY:
			return "Wednesday";
		case Calendar.THURSDAY:
			return "Thursday";
		case Calendar.FRIDAY:
			return "Friday";
		case Calendar.SATURDAY:
			return "Saturday";
		default:
			return "Sunday";
		}
	}
	
	/* Centers a string within a 31 character string		-JWT-	 */
	public static String center_string(String centered_str, String in_str) {
		int i, j;
		
		i = in_str.length();
		j = 15 - i / 2;
		
		for (i = 0; i < j - 1; i++) {
			centered_str = centered_str + '*';
		}
		if ((in_str.length() % 2) == 0) {
			centered_str = centered_str + '*';
		}
		centered_str = centered_str + ' ' + in_str + ' ';
		for (i = 0; i < j - 1; i++) {
			centered_str = centered_str + '*';
		}
		
		//centered_str = String.format("%*s%s%*s", j, "", in_str, 31 - i - j, "");
		return centered_str;
	}
	
	/* The following code is provided especially for systems which		-CJS-
	 * have no flock system call. It has never been tested.		*/
	
	private static final int LOCK_EX = 1;
	private static final int LOCK_SH = 2;
	private static final int LOCK_NB = 4;
	private static final int LOCK_UN = 8;

	/* An flock HACK.  LOCK_SH and LOCK_EX are not distinguished.  DO NOT release
	 * a lock which you failed to set!  ALWAYS release a lock you set! */
	private static int flock(int f, int l) {
		Stats sbuf;
/*
		char lockname[80];
		
		if (fstat(f, &sbuf) < 0) {
			return -1;
		}
		lockname = String.format("/tmp/moria.%d", sbuf.st_ino);
		if ((l & LOCK_UN) != 0) {
			return unlink(lockname);
		}
		
		while (open (lockname, O_WRONLY|O_CREAT|O_EXCL, 0644) < 0) {
			if (errno != EEXIST) {
				return -1;
			}
			if (stat(lockname, &sbuf) < 0) {
				return -1;
			}
*/
			/* Locks which last more than 10 seconds get deleted. */
/*
			if (time((long *)0) - sbuf.st_mtime > 10) {
				if (unlink(lockname) < 0)
					return -1;
			} else if ((l & LOCK_NB) != 0) {
				return -1;
			} else {
				Thread.sleep(1);
			}
		}
*/
		return 0;
	}
	
	public static void display_scores(boolean show_player) {
/*
		int i, rank;
		High_scores score;
		char input;
		String string;
		short version_maj, version_min, patch_level;
		int player_uid;
		
		if ((var.highscore_fp = fopen(Config.MORIA_TOP, "r")) == NULL) {
			string = String.format("Error opening score file \"%s\"\n", Config.MORIA_TOP);
			io.msg_print(string);
			io.msg_print("");
			return;
		}
		
		fseek(var.highscore_fp, (long)0, L_SET);
*/
		/* Read version numbers from the score file, and check for validity.  */
//		version_maj = getc(var.highscore_fp);
//		version_min = getc(var.highscore_fp);
//		patch_level = getc(var.highscore_fp);
		/* Support score files from 5.2.2 to present.  */
//		if (feof(var.highscore_fp)) {
			/* An empty score file. */
/*			;
		} else if ((version_maj != Constants.CUR_VERSION_MAJ)
				|| (version_min > Constants.CUR_VERSION_MIN)
				|| (version_min == Constants.CUR_VERSION_MIN && patch_level > Constants.PATCH_LEVEL)
				|| (version_min == 2 && patch_level < 2)
				|| (version_min < 2))
		{
			io.msg_print("Sorry. This scorefile is from a different version of umoria.");
			io.msg_print("");
			fclose(var.highscore_fp);
			return;
		}
		
		player_uid = (getgid() * 1000) + getuid();
*/
		/* set the static fileptr in save.c to the highscore file pointer */
/*
		save.set_fileptr(var.highscore_fp);
		
		rank = 1;
		save.rd_highscore(score);
		while (!feof(var.highscore_fp)) {
			i = 1;
			io.clear_screen();
*/			/* Put twenty scores on each page, on lines 2 through 21. */
//			while (!feof(var.highscore_fp) && i < 21) {
				/* Only show the entry if show_player false, or if the entry
				 * belongs to the current player.  */
/*				if (!show_player || score.uid == player_uid) {
					string = String.format(
							"%-4d%8ld %-19.19s %c %-10.10s %-7.7s%3d %-22.22s",
							rank, score.points, score.name, score.sex,
							py.race[score.race].trace, py.Class[score.Class].title,
							score.lev, score.died_from);
					io.prt(string, ++i, 0);
				}
				rank++;
				save.rd_highscore(score);
			}
			io.prt("Rank  Points Name              Sex Race       Class  Lvl Killed By", 0, 0);
			io.erase_line (1, 0);
			io.prt("[Press any key to continue.]", 23, 23);
			input = io.inkey();
			if (input == Constants.ESCAPE) {
				break;
			}
		}
		fclose(var.highscore_fp);
*/
	}
	
	public static boolean duplicate_character () {
		/* Only check for duplicate characters under unix and VMS.  */
/*
		High_scores score;
		short version_maj, version_min, patch_level;
		int player_uid;
		String string;
		
		if ((var.highscore_fp = fopen(Config.MORIA_TOP, "r")) == NULL) {
			string = String.format("Error opening score file \"%s\"\n", Config.MORIA_TOP);
			io.msg_print(string);
			io.msg_print("");
			return false;
		}
		
		fseek(var.highscore_fp, (long)0, L_SET);
*/		
		/* Read version numbers from the score file, and check for validity.  */
//		version_maj = getc(var.highscore_fp);
//		version_min = getc(var.highscore_fp);
//		patch_level = getc(var.highscore_fp);
		/* Support score files from 5.2.2 to present.  */
//		if (feof (var.highscore_fp)) {
			/* An empty score file.  */
/*			return false;
		}
		if ((version_maj != Constants.CUR_VERSION_MAJ)
				|| (version_min > Constants.CUR_VERSION_MIN)
				|| (version_min == Constants.CUR_VERSION_MIN && patch_level > Constants.PATCH_LEVEL)
				|| (version_min == 2 && patch_level < 2)
				|| (version_min < 2))
		{
			io.msg_print("Sorry. This scorefile is from a different version of umoria.");
			io.msg_print("");
			fclose(var.highscore_fp);
			return false;
		}
*/		
		/* set the static fileptr in save.c to the highscore file pointer */
/*		save.set_fileptr(var.highscore_fp);
		
		player_uid = (getgid() * 1000) + getuid();
		
		save.rd_highscore(score);
		while (!feof(var.highscore_fp)) {
			if (score.uid == player_uid && score.birth_date == var.birth_date
					&& score.Class == py.py.misc.pclass && score.race == py.py.misc.prace
					&& score.sex == (py.py.misc.male ? 'M' : 'F')
					&& score.died_from.equals("(saved)"))
			{
				return true;
			}
			save.rd_highscore(score);
		}
		fclose(var.highscore_fp);
*/		
		return false;
	}
	
	/* Prints the gravestone of the character		-RAK-	 */
	private static void print_tomb() {
		String str, tmp_str = "";
		int i;
		String day;
		String p;
		
		IO.clear_screen();
		IO.put_buffer("_______________________", 1, 15);
		IO.put_buffer("/", 2, 14);
		IO.put_buffer("\\         ___", 2, 38);
		IO.put_buffer("/", 3, 13);
		IO.put_buffer("\\ ___   /   \\      ___", 3, 39);
		IO.put_buffer("/            RIP            \\   \\  :   :     /   \\", 4, 12);
		IO.put_buffer("/", 5, 11);
		IO.put_buffer("\\  : _;,,,;_    :   :", 5, 41);
		str = String.format("/%s\\,;_          _;,,,;_", center_string(tmp_str, Player.py.misc.name));
		IO.put_buffer(str, 6, 10);
		IO.put_buffer("|               the               |   ___", 7, 9);
		if (!Variable.total_winner) {
			p = Misc3.title_string();
		} else {
			p = "Magnificent";
		}
		str = String.format("| %s |  /   \\", center_string(tmp_str, p));
		IO.put_buffer(str, 8, 9);
		IO.put_buffer("|", 9, 9);
		IO.put_buffer("|  :   :", 9, 43);
		if (!Variable.total_winner) {
			p = Player.Class[Player.py.misc.pclass].title;
		} else if (Player.py.misc.male) {
			p = "*King*";
		} else {
			p = "*Queen*";
		}
		str = String.format("| %s | _;,,,;_   ____", center_string(tmp_str, p));
		IO.put_buffer (str, 10, 9);
		str = String.format("Level : %d", Player.py.misc.lev);
		str = String.format("| %s |          /    \\", center_string(tmp_str, str));
		IO.put_buffer (str, 11, 9);
		str = String.format("%d Exp", Player.py.misc.exp);
		str = String.format("| %s |          :    :", center_string(tmp_str, str));
		IO.put_buffer (str, 12, 9);
		str = String.format("%d Au", Player.py.misc.au);
		str = String.format("| %s |          :    :", center_string(tmp_str, str));
		IO.put_buffer (str, 13, 9);
		str = String.format("Died on Level : %d", Variable.dun_level);
		str = String.format("| %s |         _;,,,,;_", center_string(tmp_str, str));
		IO.put_buffer(str, 14, 9);
		IO.put_buffer("|            killed by            |", 15, 9);
		p = Variable.died_from;
		i = p.length();
		p = p + ".";  /* add a trailing period */
		str = String.format("| %s |", center_string (tmp_str, p));
		IO.put_buffer(str, 16, 9);
		p = p.substring(0, p.length() - 1);	 /* strip off the period */
		day = date();
		str = String.format("| %s |", center_string(tmp_str, day));
		IO.put_buffer(str, 17, 9);
		IO.put_buffer("*|   *     *     *    *   *     *  | *", 18, 8);
		IO.put_buffer("________)/\\\\_)_/___(\\/___(//_\\)/_\\//__\\\\(/_|_)_______", 19, 0);
		
		boolean retry = true;
		while (retry) {
			retry = false;
			IO.flush();
			IO.put_buffer("(ESC to abort, return to print on screen, or file name)", 23, 0);
			IO.put_buffer("Character record?", 22, 0);
			if ((str = IO.get_string(22, 18, 60)).length() > 0) {
				for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
					Desc.known1(Treasure.inventory[i]);
					Desc.known2(Treasure.inventory[i]);
				}
				Moria1.calc_bonuses();
				if (str.length() > 0) {
					if (!Files.file_character(str)) {
						retry = true;
					}
				} else {
					IO.clear_screen();
					Misc3.display_char();
					IO.put_buffer("Type ESC to skip the inventory:", 23, 0);
					if (IO.inkey() != Constants.ESCAPE) {
						IO.clear_screen();
						IO.msg_print("You are using:");
						Moria1.show_equip(true, 0);
						IO.msg_print("");
						IO.msg_print("You are carrying:");
						IO.clear_from(1);
						Moria1.show_inven(0, Treasure.inven_ctr - 1, true, 0, "");
						IO.msg_print("");
					}
				}
			}
		}
	}
	
	/* Calculates the total number of points earned		-JWT-	 */
	public static int total_points() {
		int total;
		int i;
		
		total = Player.py.misc.max_exp + (100 * Player.py.misc.max_dlv);
		total += Player.py.misc.au / 100;
		for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
			total += Store1.item_value(Treasure.inventory[i]);
		}
		total += Variable.dun_level * 50;
		
		/* Don't ever let the score decrease from one save to the next.  */
		if (Variable.max_score > total) {
			return Variable.max_score;
		}
		
		return total;
	}
	
	/* Enters a players name on the top twenty list		-JWT-	 */
	private static void highscores() {
		HighScoreType old_entry, new_entry = new HighScoreType(), entry;
		int i = 0;
		String tmp;
		short version_maj, version_min, patch_level;
		long curpos;
		String string;
		
		IO.clear_screen();
		
		if (Variable.noscore != 0) {
			return;
		}
		
		if (Variable.panic_save == 1) {
			IO.msg_print("Sorry, scores for games restored from panic save files are not saved.");
			return;
		}
		
		new_entry.points = total_points();
		new_entry.birth_date = Variable.birth_date;
		new_entry.uid =  1000000; //(getgid()*1000) + getuid();
		new_entry.mhp = Player.py.misc.mhp;
		new_entry.chp = Player.py.misc.chp;
		new_entry.dun_level = Variable.dun_level;
		new_entry.lev = Player.py.misc.lev;
		new_entry.max_dlv = Player.py.misc.max_dlv;
		new_entry.sex = (Player.py.misc.male ? 'M' : 'F');
		new_entry.race = Player.py.misc.prace;
		new_entry.Class = Player.py.misc.pclass;
		new_entry.name = Player.py.misc.name;
		tmp = Variable.died_from;
		
		int i1 = 0;
		if ('a' == tmp.charAt(i1)) {
			if ('n' == tmp.charAt(++i1)) {
				i1++;
			}
			while (Character.isWhitespace(tmp.charAt(i1))) {
				i1++;
			}
		}
		new_entry.died_from = tmp.substring(i);
		
		/*  First, get a lock on the high score file so no-one else tries */
		/*  to write to it while we are using it, on VMS and IBMPCs only one
		 * process can have the file open at a time, so we just open it here */
/*		if ((var.highscore_fp = fopen(Config.MORIA_TOP, "r+")) == NULL) {
			string = String.format("Error opening score file \"%s\"\n", Config.MORIA_TOP);
			io.msg_print(string);
			io.msg_print("");
			return;
		}
*/		
		/* Search file to find where to insert this character, if uid != 0 and
		 * find same uid/sex/race/class combo then exit without saving this score */
		/* Seek to the beginning of the file just to be safe. */
//		fseek(var.highscore_fp, (long)0, L_SET);
		
		/* Read version numbers from the score file, and check for validity.  */
//		version_maj = getc(var.highscore_fp);
//		version_min = getc(var.highscore_fp);
//		patch_level = getc(var.highscore_fp);
		/* If this is a new scorefile, it should be empty.  Write the current
		 * version numbers to the score file.  */
//		if (feof(var.highscore_fp)) {
			/* Seek to the beginning of the file just to be safe. */
//			fseek(var.highscore_fp, (long)0, L_SET);
			
//			putc(Constants.CUR_VERSION_MAJ, var.highscore_fp);
//			putc(Constants.CUR_VERSION_MIN, var.highscore_fp);
//			putc(Constants.PATCH_LEVEL, var.highscore_fp);
			
			/* must fseek() before can change read/write mode */
//			fseek(var.highscore_fp, (long)0, L_INCR);
		
		/* Support score files from 5.2.2 to present.  */
/*		} else if ((version_maj != Constants.CUR_VERSION_MAJ)
				|| (version_min > Constants.CUR_VERSION_MIN)
				|| (version_min == Constants.CUR_VERSION_MIN && patch_level > Constants.PATCH_LEVEL)
				|| (version_min == 2 && patch_level < 2)
				|| (version_min < 2))
		{
*/			/* No need to print a message, a subsequent call to display_scores()
			 * will print a message.  */
/*			fclose(var.highscore_fp);
			return;
		}
*/		
		/* set the static fileptr in save.c to the highscore file pointer */
/*		save.set_fileptr(var.highscore_fp);
		
		i = 0;
		curpos = ftell(var.highscore_fp);
		save.rd_highscore(old_entry);
		while (!feof(var.highscore_fp)) {
			if (new_entry.points >= old_entry.points) {
				break;
*/			
			/* under unix and VMS, only allow one sex/race/class combo per person,
			 * on single user system, allow any number of entries, but try to
			 * prevent multiple entries per character by checking for case when
			 * birthdate/sex/race/class are the same, and died_from of scorefile
			 * entry is "(saved)" */
/*			} else if (((new_entry.uid != 0 && new_entry.uid == old_entry.uid)
					|| (new_entry.uid == 0 &&old_entry.died_from != "(saved)"
					&& new_entry.birth_date == old_entry.birth_date))
					&& new_entry.sex == old_entry.sex
					&& new_entry.race == old_entry.race
					&& new_entry.Class == old_entry.Class)
			{
				fclose(var.highscore_fp);
				return;
			} else if (++i >= Constants.SCOREFILE_SIZE) {
*/				/* only allow one thousand scores in the score file */
/*				fclose(var.highscore_fp);
				return;
			}
			curpos = ftell(var.highscore_fp);
			save.rd_highscore(old_entry);
		}
		
		if (feof(var.highscore_fp)) {
*/			/* write out new_entry at end of file */
/*			fseek(var.highscore_fp, curpos, L_SET);
			save.wr_highscore(new_entry);
		} else {
			entry = new_entry;
			while (!feof(var.highscore_fp)) {
				fseek(var.highscore_fp, -(long)sizeof(high_scores) - (long)sizeof(char), L_INCR);
				wr_highscore(entry);
*/				/* under unix and VMS, only allow one sex/race/class combo per
				 * person, on single user system, allow any number of entries, but
				 * try to prevent multiple entries per character by checking for
				 * case when birthdate/sex/race/class are the same, and died_from of
				 * scorefile entry is "(saved)" */
/*				if (((new_entry.uid != 0 && new_entry.uid == old_entry.uid)
						|| (new_entry.uid == 0 &&!strcmp(old_entry.died_from,"(saved)")
						&& new_entry.birth_date == old_entry.birth_date))
						&& new_entry.sex == old_entry.sex
						&& new_entry.race == old_entry.race
						&& new_entry.Class == old_entry.Class)
				{
					break;
				}
				entry = old_entry;
*/				/* must fseek() before can change read/write mode */
/*				fseek(var.highscore_fp, (long)0, L_INCR);
				curpos = ftell (var.highscore_fp);
				rd_highscore(old_entry);
			}
			if (feof(var.highscore_fp)) {
				fseek(var.highscore_fp, curpos, L_SET);
				wr_highscore(entry);
			}
		}
		
		fclose(var.highscore_fp);
*/	}
	
	/* Change the player into a King!			-RAK-	 */
	private static void kingly() {
		PlayerMisc p_ptr;
		String p;
		
		/* Change the character attributes.		 */
		Variable.dun_level = 0;
		Variable.died_from = "Ripe Old Age";
		p_ptr = Player.py.misc;
		Spells.restore_level();
		p_ptr.lev += Constants.MAX_PLAYER_LEVEL;
		p_ptr.au += 250000L;
		p_ptr.max_exp += 5000000L;
		p_ptr.exp = p_ptr.max_exp;
		
		/* Let the player know that he did good.	 */
		IO.clear_screen();
		IO.put_buffer("#", 1, 34);
		IO.put_buffer("#####", 2, 32);
		IO.put_buffer("#", 3, 34);
		IO.put_buffer(",,,  $$$  ,,,", 4, 28);
		IO.put_buffer(",,=$   \"$$$$$\"   $=,,", 5, 24);
		IO.put_buffer(",$$        $$$        $$,", 6, 22);
		IO.put_buffer("*>         <*>         <*", 7, 22);
		IO.put_buffer("$$         $$$         $$", 8, 22);
		IO.put_buffer("\"$$        $$$        $$\"", 9, 22);
		IO.put_buffer("\"$$       $$$       $$\"", 10, 23);
		p = "*#########*#########*";
		IO.put_buffer(p, 11, 24);
		IO.put_buffer(p, 12, 24);
		IO.put_buffer("Veni, Vidi, Vici!", 15, 26);
		IO.put_buffer("I came, I saw, I conquered!", 16, 21);
		if (p_ptr.male) {
			IO.put_buffer("All Hail the Mighty King!", 17, 22);
		} else {
			IO.put_buffer("All Hail the Mighty Queen!", 17, 22);
		}
		IO.flush();
		IO.pause_line(23);
	}
	
	/* Handles the gravestone end top-twenty routines	-RAK-	 */
	public static void exit_game() {
		/* What happens upon dying.				-RAK-	 */
		IO.msg_print("");
		IO.flush();  /* flush all input */
		Signals.nosignals();	 /* Can't interrupt or suspend. */
		/* If the game has been saved, then save sets turn back to -1, which
		 * inhibits the printing of the tomb.	 */
		if (Variable.turn >= 0) {
			if (Variable.total_winner) {
				kingly();
			}
			print_tomb();
		}
		if (Variable.character_generated && Variable.character_saved == 0) {
			Save.save_char();		/* Save the memory at least. */
		}
		/* add score to scorefile if applicable */
		if (Variable.character_generated) {
			/* Clear character_saved, strange thing to do, but it prevents inkey()
			 * from recursively calling exit_game() when there has been an eof
			 * on stdin detected.  */
			Variable.character_saved = 0;
			highscores();
			display_scores(true);
		}
		IO.erase_line(23, 0);
		IO.restore_term();
		System.exit(0);
	}
}
