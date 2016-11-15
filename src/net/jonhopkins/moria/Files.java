/*
 * Files.java: misc code to access files used by Moria
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

import java.io.*;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Files {
	
	private Files() { }
	
	/*
	 *  init_scorefile
	 *  Open the score file while we still have the setuid privileges.  Later
	 *  when the score is being written out, you must be sure to flock the file
	 *  so we don't have multiple people trying to write to it at the same time.
	 *  Craig Norborg (doc)		Mon Aug 10 16:41:59 EST 1987
	 */
	public static void initScoreFile() {
		Variable.highscore_fp = new File(Config.MORIA_TOP);
		
		if (Variable.highscore_fp == null) {
			System.err.printf("Can't open score file \"%s\"\n", Config.MORIA_TOP);
			System.exit(1);
			return;
		}
		/* can't leave it open, since this causes problems on networked PCs and VMS,
		 * we DO want to check to make sure we can open the file, though */
		//fclose (var.highscore_fp);
	}
	
	/* Attempt to open the intro file			-RAK-	 */
	/* This routine also checks the hours file vs. what time it is	-Doc */
	public static void readTimes() {
		//String in_line;
		//int i;
		//File file1;
		
		/* Attempt to read hours.dat.	 If it does not exist,	   */
		/* inform the user so he can tell the wizard about it	 */
		/*
		if ((file1 = fopen(Config.MORIA_HOU, "r")) != NULL) {
			while (fgets(in_line, 80, file1) != CNIL) {
				if (in_line.length() > 3) {
					if (!strncmp(in_line, "SUN:", 4))
						strcpy(days[0], in_line);
					else if (!strncmp(in_line, "MON:", 4))
						strcpy(days[1], in_line);
					else if (!strncmp(in_line, "TUE:", 4))
						strcpy(days[2], in_line);
					else if (!strncmp(in_line, "WED:", 4))
						strcpy(days[3], in_line);
					else if (!strncmp(in_line, "THU:", 4))
						strcpy(days[4], in_line);
					else if (!strncmp(in_line, "FRI:", 4))
						strcpy(days[5], in_line);
					else if (!strncmp(in_line, "SAT:", 4))
						strcpy(days[6], in_line);
				}
			}
			fclose(file1);
		} else {
			io.restore_term();
			System.err.printf("There is no hours file \"%s\".\n", Config.MORIA_HOU);
			System.err.printf("Please inform the wizard, %s, so he ", Config.WIZARD);
			System.err.printf("can correct this!\n");
			System.exit(1);
		}
		*/
		/* Check the hours, if closed	then exit. */
		/*
		if (!check_time()) {
			if ((file1 = fopen(Config.MORIA_HOU, "r")) != NULL) {
				io.clear_screen();
				for (i = 0; fgets(in_line, 80, file1) != CNIL; i++) {
					io.put_buffer(in_line, i, 0);
				}
				io.pause_line(23);
				fclose(file1);
			}
			exit_game();
		}
		*/
		/* Print the introduction message, news, etc.		 */
		/*
		if ((file1 = fopen(Config.MORIA_MOR, "r")) != NULL) {
			io.clear_screen();
			for (i = 0; fgets(in_line, 80, file1) != CNIL; i++) {
				io.put_buffer(in_line, i, 0);
			}
			io.pause_line(23);
			fclose(file1);
		}
		*/
	}
	
	/* File perusal.	    -CJS-
	 * primitive, but portable */
	public static void helpfile(String filename) {
		String tmp_str = "";
		java.io.File file;
		char input;
		int i;
		
		file = new File(filename);
		if (!file.exists()) {
			tmp_str = String.format("Can not find help file \"%s\".\n", filename);
			IO.print(tmp_str, 0, 0);
			return;
		}
		
		IO.saveScreen();
		
		try{
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while ((tmp_str = br.readLine()) != null) {
				IO.clearScreen();
				IO.putBuffer(tmp_str, 0, 0);
				for (i = 1; i < 23 && (tmp_str = br.readLine()) != null; i++) {
					IO.putBuffer(tmp_str, i, 0);
				}
				IO.print("[Press any key to continue.]", 23, 23);
				input = IO.inkey();
				if (input == Constants.ESCAPE) {
					break;
				}
			}
			br.close();
			in.close();
			fstream.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		IO.restoreScreen();
	}
	
	/* Prints a list of random objects to a file.  Note that -RAK-	 */
	/* the objects produced is a sampling of objects which		 */
	/* be expected to appear on that level.				 */
	public static void printObjects() {
		int i;
		int nobj, j = 0, level;
		String filename1, tmp_str;
		File file1;
		InvenType i_ptr;
		boolean small;
		
		IO.print("Produce objects on what level?: ", 0, 0);
		level = 0;
		
		if ((tmp_str = IO.getString(0, 32, 10)).length() == 0) {
			return;
		}
		try {
			level = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			level = 0;
		}
		IO.print("Produce how many objects?: ", 0, 0);
		nobj = 0;
		small = IO.getCheck("Small objects only?");
		
		if ((tmp_str = IO.getString(0, 27,10)).length() == 0) {
			return;
		}
		try {
			nobj = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			nobj = 1;
		}
		if ((nobj > 0) && (level > -1) && (level < 1201)) {
			if (nobj > 10000) {
				nobj = 10000;
			}
			IO.print("File name: ", 0, 0);
			if ((filename1 = IO.getString(0, 11, 64)).length() > 0) {
				if ((file1 = new File(filename1)).exists()) {
					tmp_str = String.format("%d", nobj);
					IO.print(tmp_str.concat(" random objects being produced..."), 0, 0);
					IO.putQio();
					Writer output;
					try {
						output = new BufferedWriter(new FileWriter(file1));
					} catch (IOException e) {
						IO.print("Files.print_objects(): Failed to open file as BufferedWriter.", 0, 0);
						e.printStackTrace();
						return;
					}
					try {
						output.write("*** Random Object Sampling:\n");
						output.write(String.format("*** %d objects\n", nobj));
						output.write(String.format("*** For Level %d\n", level));
						output.write("\n");
						output.write("\n");
						j = Misc1.popTreasure();
						for (i = 0; i < nobj; i++) {
							Desc.copyIntoInventory(Treasure.t_list[j], Treasure.sorted_objects[Misc3.getRandomObjectForLevel(level, small)]);
							Misc2.addMagicToTreasure(j, level);
							i_ptr = Treasure.t_list[j];
							Desc.setStoreBought(i_ptr);
							if ((i_ptr.flags & Constants.TR_CURSED) != 0) {
								Misc4.addInscription(i_ptr, Constants.ID_DAMD);
							}
							tmp_str = Desc.describeObject(i_ptr, true);
							output.write(String.format("%d %s\n", i_ptr.level, tmp_str));
						}
						Misc1.pusht(j);
						output.close();
						IO.print("Completed.", 0, 0);
					} catch (IOException e) {
						IO.print("Files.print_objects(): Failed to write to file.", 0, 0);
						e.printStackTrace();
						return;
					}
				} else {
					IO.print("File could not be opened.", 0, 0);
				}
			} else {
				return;
			}
		} else {
			IO.print("Parameters no good.", 0, 0);
		}
	}
	
	/* Print the character to a file or device		-RAK-	 */
	public static boolean fileCharacter(String filename1) {
		int i;
		int j, xbth, xbthb, xfos, xsrh, xstl, xdis, xsave, xdev;
		String xinfra;
		File file1;
		String prt2;
		PlayerMisc p_ptr;
		InvenType i_ptr;
		String out_val, prt1;
		String p, colon, blank;
		
		file1 = new File(filename1);
		if (!file1.isFile()) {
			IO.print("Files.fileCharacter(): Should not be a directory: " + file1, 0, 0);
			return false;
		}
		if (!file1.canWrite()) {
			IO.print("Files.fileCharacter(): File cannot be written: " + file1, 0, 0);
			return false;
		}
		try {
			Writer output = new BufferedWriter(new FileWriter(file1));
			IO.print("Writing character sheet...", 0, 0);
			IO.putQio();
			colon = ":";
			blank = " ";
			output.write(String.format("%c\n\n", (Constants.CTRL & 'L')));
			output.write(String.format(" Name%9s %-23s", colon, Player.py.misc.name));
			output.write(String.format(" Age%11s %6d", colon, Player.py.misc.age));
			prt1 = Misc3.convertStat(Player.py.stats.use_stat[Constants.A_STR]);
			output.write(String.format("   STR : %s\n", prt1));
			output.write(String.format(" Race%9s %-23s", colon, Player.race[Player.py.misc.prace].trace));
			output.write(String.format(" Height%8s %6d", colon, Player.py.misc.ht));
			prt1 = Misc3.convertStat(Player.py.stats.use_stat[Constants.A_INT]);
			output.write(String.format("   INT : %s\n", prt1));
			output.write(String.format(" Sex%10s %-23s", colon, (Player.py.misc.male ? "Male" : "Female")));
			output.write(String.format(" Weight%8s %6d", colon, Player.py.misc.wt));
			prt1 = Misc3.convertStat(Player.py.stats.use_stat[Constants.A_WIS]);
			output.write(String.format("   WIS : %s\n", prt1));
			output.write(String.format(" Class%8s %-23s", colon, Player.Class[Player.py.misc.pclass].title));
			output.write(String.format(" Social Class : %6d", Player.py.misc.sc));
			prt1 = Misc3.convertStat(Player.py.stats.use_stat[Constants.A_DEX]);
			output.write(String.format("   DEX : %s\n", prt1));
			output.write(String.format(" Title%8s %-23s", colon, Misc3.getPlayerTitle()));
			output.write(String.format("%22s", blank));
			prt1 = Misc3.convertStat(Player.py.stats.use_stat[Constants.A_CON]);
			output.write(String.format( "   CON : %s\n", prt1));
			output.write(String.format("%34s", blank));
			output.write(String.format("%26s", blank));
			prt1 = Misc3.convertStat(Player.py.stats.use_stat[Constants.A_CHR]);
			output.write(String.format("   CHR : %s\n\n", prt1));
			
			output.write(String.format(" + To Hit    : %6d", Player.py.misc.dis_th));
			output.write(String.format("%7sLevel      : %7d", blank, Player.py.misc.lev));
			output.write(String.format("    Max Hit Points : %6d\n", Player.py.misc.mhp));
			output.write(String.format(" + To Damage : %6d", Player.py.misc.dis_td));
			output.write(String.format("%7sExperience : %7d", blank, Player.py.misc.exp));
			output.write(String.format("    Cur Hit Points : %6d\n", Player.py.misc.chp));
			output.write(String.format(" + To AC     : %6d", Player.py.misc.dis_tac));
			output.write(String.format("%7sMax Exp    : %7d", blank, Player.py.misc.max_exp));
			output.write(String.format("    Max Mana%8s %6d\n", colon, Player.py.misc.mana));
			output.write(String.format("   Total AC  : %6d", Player.py.misc.dis_ac));
			if (Player.py.misc.lev == Constants.MAX_PLAYER_LEVEL) {
				output.write(String.format("%7sExp to Adv : *******", blank));
			} else {
				output.write(String.format("%7sExp to Adv : %7d", blank, (Player.player_exp[Player.py.misc.lev - 1] * Player.py.misc.expfact / 100)));
			}
			output.write(String.format("    Cur Mana%8s %6d\n", colon, Player.py.misc.cmana));
			output.write(String.format("%28sGold%8s %7d\n\n", blank, colon, Player.py.misc.au));
			
			p_ptr = Player.py.misc;
			xbth = p_ptr.bth + p_ptr.ptohit * Constants.BTH_PLUS_ADJ + (Player.class_level_adj[p_ptr.pclass][Constants.CLA_BTH] * p_ptr.lev);
			xbthb = p_ptr.bthb + p_ptr.ptohit * Constants.BTH_PLUS_ADJ + (Player.class_level_adj[p_ptr.pclass][Constants.CLA_BTHB] * p_ptr.lev);
			/* this results in a range from 0 to 29 */
			xfos = 40 - p_ptr.fos;
			if (xfos < 0) {
				xfos = 0;
			}
			xsrh = p_ptr.srh;
			/* this results in a range from 0 to 9 */
			xstl = p_ptr.stl + 1;
			xdis = p_ptr.disarm + 2 * Misc3.adjustToDisarm() + Misc3.adjustStat(Constants.A_INT)
					+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_DISARM] * p_ptr.lev / 3);
			xsave = p_ptr.save + Misc3.adjustStat(Constants.A_WIS)
					+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_SAVE] * p_ptr.lev / 3);
			xdev = p_ptr.save + Misc3.adjustStat(Constants.A_INT)
					+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_DEVICE] * p_ptr.lev / 3);
			
			xinfra = String.format("%d feet", Player.py.flags.see_infra * 10);
			
			output.write("(Miscellaneous Abilities)\n\n");
			output.write(String.format(" Fighting    : %-10s", Misc3.likeRating(xbth, 12)));
			output.write(String.format("   Stealth     : %-10s", Misc3.likeRating(xstl, 1)));
			output.write(String.format("   Perception  : %s\n", Misc3.likeRating(xfos, 3)));
			output.write(String.format(" Bows/Throw  : %-10s", Misc3.likeRating(xbthb, 12)));
			output.write(String.format("   Disarming   : %-10s", Misc3.likeRating(xdis, 8)));
			output.write(String.format("   Searching   : %s\n", Misc3.likeRating(xsrh, 6)));
			output.write(String.format(" Saving Throw: %-10s", Misc3.likeRating(xsave, 6)));
			output.write(String.format("   Magic Device: %-10s", Misc3.likeRating(xdev, 6)));
			output.write(String.format("   Infra-Vision: %s\n\n", xinfra));
			/* Write out the character's history     */
			output.write("Character Background\n");
			for (i = 0; i < 4; i++) {
				output.write(String.format(" %s\n", Player.py.misc.history[i]));
			}
			/* Write out the equipment list.	     */
			j = 0;
			output.write(String.format("\n  [Character's Equipment List]\n\n"));
			if (Treasure.equip_ctr == 0) {
				output.write(String.format("  Character has no equipment in use.\n"));
			} else {
				for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
					i_ptr = Treasure.inventory[i];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						switch (i)
						{
						case Constants.INVEN_WIELD:	p = "You are wielding";	break;
						case Constants.INVEN_HEAD:	p = "Worn on head";	break;
						case Constants.INVEN_NECK:	p = "Worn around neck";	break;
						case Constants.INVEN_BODY:	p = "Worn on body";	break;
						case Constants.INVEN_ARM:	p = "Worn on shield arm";break;
						case Constants.INVEN_HANDS:	p = "Worn on hands";	break;
						case Constants.INVEN_RIGHT:	p = "Right ring finger";break;
						case Constants.INVEN_LEFT:	p = "Left  ring finger";break;
						case Constants.INVEN_FEET:	p = "Worn on feet";	break;
						case Constants.INVEN_OUTER:	p = "Worn about body";	break;
						case Constants.INVEN_LIGHT:	p = "Light source is";	break;
						case Constants.INVEN_AUX:	p = "Secondary weapon";	break;
						default: p = "*Unknown value*";     break;
						}
						prt2 = Desc.describeObject(Treasure.inventory[i], true);
						output.write(String.format("  %c) %-19s: %s\n", j+'a', p, prt2));
						j++;
					}
				}
			}
			
			/* Write out the character's inventory.	     */
			output.write(String.format("%c\n\n", (Constants.CTRL & 'L')));
			output.write("  [General Inventory List]\n\n");
			if (Treasure.inven_ctr == 0) {
				output.write("  Character has no objects in inventory.\n");
			} else {
				for (i = 0; i < Treasure.inven_ctr; i++) {
					prt2 = Desc.describeObject(Treasure.inventory[i], true);
					output.write(String.format("%c) %s\n", i + 'a', prt2));
				}
			}
			output.write(String.format("%c", (Constants.CTRL & 'L')));
			output.close();
			IO.print("Completed.", 0, 0);
			return true;
		} catch (IOException e) {
			out_val = String.format("Can't open file %s:", filename1);
			IO.printMessage(out_val);
			e.printStackTrace();
			return false;
		}
	}
}
