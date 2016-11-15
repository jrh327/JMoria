/*
 * Save.java: save and restore games and monster memory info
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import net.jonhopkins.moria.types.BooleanPointer;
import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.HighScoreType;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;
import net.jonhopkins.moria.types.MonsterRecallType;
import net.jonhopkins.moria.types.Stats;
import net.jonhopkins.moria.types.StoreType;

public class Save {
	/* For debugging the savefile code on systems with broken compilers.  */
	
	//	#define DEBUG(x)
	//	DEBUG(static FILE *logfile);
	
	/* these are used for the save file, to avoid having to pass them to every
	 * procedure */
	private static File fileptr;
	private static InputStream fis;
	private static OutputStream fos;
	private static byte[] bytes;
	private static int b_ptr;
	private static byte xor_byte;
	private static int from_savefile;	/* can overwrite old savefile when save */
	private static long start_time;	/* time that play started */
	
	private static final int SIZEOF_CHAR = 2;
	private static final int SIZEOF_INT  = 4;
	private static final int SIZEOF_LONG = 8;
	
	private static Signals sigs;
	private static Store1 store1;
	
	private Save() { }
	
	/* This save package was brought to by			-JWT-
	 * and							-RAK-
	 * and has been completely rewritten for UNIX by	-JEW-  */
	/* and has been completely rewritten again by	 -CJS-	*/
	/* and completely rewritten again! for portability by -JEW- */
	/* and completely rewritten yet again for java by -JRH- */
	
	private static boolean saveWrite() {
		long l;
		int i, j;
		int count;
		short char_tmp, prev_char;
		CaveType c_ptr;
		MonsterRecallType r_ptr;
		Stats s_ptr;
		PlayerFlags f_ptr;
		StoreType st_ptr;
		PlayerMisc m_ptr;
		InvenType t_ptr;
		
		b_ptr = 0;
		bytes = new byte[512];
		
		/* clear the death flag when creating a HANGUP save file, so that player
		 * can see tombstone when restart */
		if (Variable.eof_flag > 0) {
			Variable.death = false;
		}
		
		l = 0;
		if (Variable.find_cut.value()) {
			l |= 0x1;
		}
		if (Variable.find_examine.value()) {
			l |= 0x2;
		}
		if (Variable.find_prself.value()) {
			l |= 0x4;
		}
		if (Variable.find_bound.value()) {
			l |= 0x8;
		}
		if (Variable.prompt_carry_flag.value()) {
			l |= 0x10;
		}
		if (Variable.rogue_like_commands.value()) {
			l |= 0x20;
		}
		if (Variable.show_weight_flag.value()) {
			l |= 0x40;
		}
		if (Variable.highlight_seams.value()) {
			l |= 0x80;
		}
		if (Variable.find_ignore_doors.value()) {
			l |= 0x100;
		}
		if (Variable.sound_beep_flag.value()) {
			l |= 0x200;
		}
		if (Variable.display_counts.value()) {
			l |= 0x400;
		}
		if (Variable.death) {
			l |= 0x80000000L;	/* Sign bit */
		}
		if (Variable.total_winner) {
			l |= 0x40000000L;
		}
		
		for (i = 0; i < Constants.MAX_CREATURES; i++) {
			r_ptr = Variable.c_recall[i];
			if (r_ptr.r_cmove != 0 || r_ptr.r_cdefense != 0 || r_ptr.r_kills != 0
					|| r_ptr.r_spells != 0 || r_ptr.r_deaths != 0 || r_ptr.r_attacks[0] != 0
					|| r_ptr.r_attacks[1] != 0 || r_ptr.r_attacks[2] != 0 || r_ptr.r_attacks[3] != 0)
			{
				writeInt(i);
				writeLong(r_ptr.r_cmove);
				writeLong(r_ptr.r_spells);
				writeInt(r_ptr.r_kills);
				writeInt(r_ptr.r_deaths);
				writeInt(r_ptr.r_cdefense);
				writeInt(r_ptr.r_wake);
				writeInt(r_ptr.r_ignore);
				writeInts(r_ptr.r_attacks, Constants.MAX_MON_NATTACK);
			}
		}
		writeInt(0xFFFF); /* sentinel to indicate no more monster info */
		
		writeLong(l);
		
		m_ptr = Player.py.misc;
		writeString(m_ptr.name, Player.py.PLAYER_NAME_SIZE);
		writeByte((m_ptr.male ? (byte)1 : (byte)0));
		writeInt(m_ptr.au);
		writeInt(m_ptr.max_exp);
		writeInt(m_ptr.exp);
		writeInt(m_ptr.exp_frac);
		writeInt(m_ptr.age);
		writeInt(m_ptr.ht);
		writeInt(m_ptr.wt);
		writeInt(m_ptr.lev);
		writeInt(m_ptr.max_dlv);
		writeInt(m_ptr.srh);
		writeInt(m_ptr.fos);
		writeInt(m_ptr.bth);
		writeInt(m_ptr.bthb);
		writeInt(m_ptr.mana);
		writeInt(m_ptr.mhp);
		writeInt(m_ptr.ptohit);
		writeInt(m_ptr.ptodam);
		writeInt(m_ptr.pac);
		writeInt(m_ptr.ptoac);
		writeInt(m_ptr.dis_th);
		writeInt(m_ptr.dis_td);
		writeInt(m_ptr.dis_ac);
		writeInt(m_ptr.dis_tac);
		writeInt(m_ptr.disarm);
		writeInt(m_ptr.save);
		writeInt(m_ptr.sc);
		writeInt(m_ptr.stl);
		writeInt(m_ptr.pclass);
		writeInt(m_ptr.prace);
		writeInt(m_ptr.hitdie);
		writeInt(m_ptr.expfact);
		writeInt(m_ptr.cmana);
		writeInt(m_ptr.cmana_frac);
		writeInt(m_ptr.chp);
		writeInt(m_ptr.chp_frac);
		for (i = 0; i < 4; i++) {
			writeString(m_ptr.history[i], 60);
		}
		
		s_ptr = Player.py.stats;
		writeInts(s_ptr.max_stat, 6);
		writeInts(s_ptr.cur_stat, 6);
		writeInts(s_ptr.mod_stat, 6);
		writeInts(s_ptr.use_stat, 6);
		
		f_ptr = Player.py.flags;
		writeLong(f_ptr.status);
		writeInt(f_ptr.rest);
		writeInt(f_ptr.blind);
		writeInt(f_ptr.paralysis);
		writeInt(f_ptr.confused);
		writeInt(f_ptr.food);
		writeInt(f_ptr.food_digested);
		writeInt(f_ptr.protection);
		writeInt(f_ptr.speed);
		writeInt(f_ptr.fast);
		writeInt(f_ptr.slow);
		writeInt(f_ptr.afraid);
		writeInt(f_ptr.poisoned);
		writeInt(f_ptr.image);
		writeInt(f_ptr.protevil);
		writeInt(f_ptr.invuln);
		writeInt(f_ptr.hero);
		writeInt(f_ptr.shero);
		writeInt(f_ptr.blessed);
		writeInt(f_ptr.resist_heat);
		writeInt(f_ptr.resist_cold);
		writeInt(f_ptr.detect_inv);
		writeInt(f_ptr.word_recall);
		writeInt(f_ptr.see_infra);
		writeInt(f_ptr.tim_infra);
		writeByte((f_ptr.see_inv ? (byte)1 : (byte)0));
		writeByte((byte)f_ptr.teleport);
		writeByte((f_ptr.free_act ? (byte)1 : (byte)0));
		writeByte((f_ptr.slow_digest ? (byte)1 : (byte)0));
		writeByte((byte)f_ptr.aggravate);
		writeByte((byte)f_ptr.fire_resist);
		writeByte((byte)f_ptr.cold_resist);
		writeByte((byte)f_ptr.acid_resist);
		writeByte((f_ptr.regenerate ? (byte)1 : (byte)0));
		writeByte((byte)f_ptr.lght_resist);
		writeByte((byte)f_ptr.ffall);
		writeByte((f_ptr.sustain_str ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustain_int ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustain_wis ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustain_con ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustain_dex ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustain_chr ? (byte)1 : (byte)0));
		writeByte((f_ptr.confuse_monster ? (byte)1 : (byte)0));
		writeInt(f_ptr.new_spells);
		
		writeInt(Variable.missile_ctr);
		writeInt(Variable.turn);
		writeInt(Treasure.inven_ctr);
		for (i = 0; i < Treasure.inven_ctr; i++) {
			writeItem(Treasure.inventory[i]);
		}
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
			writeItem(Treasure.inventory[i]);
		}
		writeInt(Treasure.inven_weight);
		writeInt(Treasure.equip_ctr);
		writeLong(Player.spell_learned);
		writeLong(Player.spell_worked);
		writeLong(Player.spell_forgotten);
		writeInts(Player.spell_order, 32);
		writeInts(Treasure.object_ident, Constants.OBJECT_IDENT_SIZE);
		writeLong(Variable.randes_seed);
		writeLong(Variable.town_seed);
		writeInt(Variable.last_msg);
		for (i = 0; i < Constants.MAX_SAVE_MSG; i++) {
			writeString(Variable.old_msg[i], Constants.VTYPESIZ);
		}
		
		/* this indicates 'cheating' if it is a one */
		writeInt(Variable.panic_save);
		writeByte((Variable.total_winner ? (byte)1 : (byte)0));
		writeInt(Variable.noscore);
		writeInts(Player.player_hp, Constants.MAX_PLAYER_LEVEL);
		
		for (i = 0; i < Constants.MAX_STORES; i++) {
			st_ptr = Variable.store[i];
			writeLong((long)st_ptr.store_open);
			writeInt(st_ptr.insult_cur);
			writeByte((byte)st_ptr.owner);
			writeByte((byte)st_ptr.store_ctr);
			writeInt(st_ptr.good_buy);
			writeInt(st_ptr.bad_buy);
			for (j = 0; j < st_ptr.store_ctr; j++) {
				writeLong((long)st_ptr.store_inven[j].scost);
				writeItem(st_ptr.store_inven[j].sitem);
			}
		}
		
		/* save the current time in the savefile */
		l = java.util.Calendar.getInstance().getTimeInMillis();
		if (l < start_time) {
			/* someone is messing with the clock!, assume that we have been
			 * playing for 1 day */
			l = start_time + 86400L;
		}
		writeLong(l);
		
		/* starting with 5.2, put died_from string in savefile */
		writeString(Variable.died_from, 25);
		
		/* starting with 5.2.2, put the max_score in the savefile */
		l = Death.getTotalPoints();
		writeLong(l);
		
		/* starting with 5.2.2, put the birth_date in the savefile */
		writeLong(Variable.birth_date);
		
		/* only level specific info follows, this allows characters to be
		 * resurrected, the dungeon level info is not needed for a resurrection */
		if (Variable.death) {
			/*if (ferror(fileptr) || fflush(fileptr) == EOF) {
				return false;
			}*/
			return true;
		}
		
		writeInt(Variable.dun_level);
		writeInt(Player.char_row);
		writeInt(Player.char_col);
		writeInt(Monsters.mon_tot_mult);
		writeInt(Variable.cur_height);
		writeInt(Variable.cur_width);
		writeInt(Variable.max_panel_rows);
		writeInt(Variable.max_panel_cols);
		
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.cptr != 0) {
					writeByte((byte)i);
					writeByte((byte)j);
					writeByte((byte)c_ptr.cptr);
				}
			}
		}
		writeByte((byte)0xFF); /* marks end of cptr info */
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.tptr != 0) {
					writeByte((byte)i);
					writeByte((byte)j);
					writeByte((byte)c_ptr.tptr);
				}
			}
		}
		writeByte((byte)0xFF); /* marks end of tptr info */
		/* must set counter to zero, note that code may write out two bytes
		 * unnecessarily */
		count = 0;
		prev_char = 0;
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				char_tmp = (short)(c_ptr.fval | ((c_ptr.lr ? 1 : 0) << 4) | ((c_ptr.fm ? 1 : 0) << 5) | ((c_ptr.pl ? 1 : 0) << 6) | ((c_ptr.tl ? 1 : 0) << 7));
				if (char_tmp != prev_char || count == Constants.MAX_UCHAR) {
					writeByte((byte)count);
					writeByte((byte)prev_char);
					prev_char = char_tmp;
					count = 1;
				} else {
					count++;
				}
			}
		}
		/* save last entry */
		writeByte((byte)count);
		writeByte((byte)prev_char);
		
		/* must change graphics symbols for walls and floors back to default chars,
		 * this is necessary so that if the user changes the graphics line, the
		 * program will be able change all existing walls and floors to the new
		 * symbol */
		/* Or if the user moves the savefile from one machine to another, we
		 * must have a consistent representation here.  */
		for (i = Treasure.tcptr - 1; i >= Constants.MIN_TRIX; i--) {
			t_ptr = Treasure.t_list[i];
			if (t_ptr.tchar == Variable.wallsym) {
				t_ptr.tchar = '#';
			}
			if (t_ptr.tchar == 240) {
				t_ptr.tchar = '#';
			}
		}
		writeInt(Treasure.tcptr);
		for (i = Constants.MIN_TRIX; i < Treasure.tcptr; i++) {
			writeItem(Treasure.t_list[i]);
		}
		writeInt(Monsters.mfptr);
		for (i = Constants.MIN_MONIX; i < Monsters.mfptr; i++) {
			writeMonster(Monsters.m_list[i]);
		}
		
		/*if (ferror(fileptr) || (fflush(fileptr) == EOF)) {
			return false;
		}*/
		
		// truncate any extra length
		bytes = Arrays.copyOf(bytes, b_ptr);
		
		try {
			fos = new BufferedOutputStream(new FileOutputStream(fileptr));
			fos.write(bytes);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean saveCharacter() {
		int i;
		String temp;
		
		while (!saveCharacter(Variable.savefile)) {
			temp = String.format("Savefile '%s' fails.", Variable.savefile);
			IO.printMessage(temp);
			i = 0;
/*			if (access(var.savefile, 0) < 0 || !io.get_check("File exists. Delete old savefile?") || (i = unlink(var.savefile)) < 0) {
				if (i < 0) {
					temp = String.format("Can't delete '%s'", var.savefile);
					io.msg_print(temp);
				}
				io.prt("New Savefile [ESC to give up]:", 0, 0);
				temp = io.get_string(0, 31, 45);
				if (temp.length() == 0) {
					return false;
				}
				if (!temp.equals("")) {
					var.savefile = temp;
				}
			}
*/
			temp = String.format("Saving with %s...", Variable.savefile);
			IO.print(temp, 0, 0);
		}
		return true;
	}
	
	private static boolean saveCharacter(String fnam) {
		String temp;
		boolean ok;
		int fd = 0;
		short char_tmp;
		
		if (Variable.character_saved != 0) {
			return true;	/* Nothing to save. */
		}
		
		sigs.noSignals();
		IO.putQio();
		Moria1.disturbPlayer(true, false);	/* Turn off resting and searching. */
		Moria1.changeSpeed(-Variable.pack_heavy);	/* Fix the speed */
		Variable.pack_heavy = 0;
		ok = false;
//		fileptr = fopen(var.savefile, "w");
		
		if (fileptr != null) {
			xor_byte = 0;
			writeInt(Constants.CUR_VERSION_MAJ);
			xor_byte = 0;
			writeInt(Constants.CUR_VERSION_MIN);
			xor_byte = 0;
			writeInt(Constants.PATCH_LEVEL);
			xor_byte = 0;
			char_tmp = (short)(Misc1.randomInt(256) - 1);
			writeByte((byte)char_tmp);
			/* Note that xor_byte is now equal to char_tmp */
			
			ok = saveWrite();
			
//			if (fclose(fileptr) == EOF) {
//				ok = false;
//			}
		}
		
		if (!ok) {
			if (fd >= 0) {
//				unlink(fnam);
			}
			sigs.signals();
			if (fd >= 0) {
				temp = String.format("Error writing to file %s", fnam);
			} else {
				temp = String.format("Can't create new file %s", fnam);
			}
			IO.printMessage(temp);
			return false;
		} else {
			Variable.character_saved = 1;
		}
		
		Variable.turn = -1;
		sigs.signals();
		
		return true;
	}
	
	/* Certain checks are omitted for the wizard. -CJS- */
	
	public static boolean getCharacter(BooleanPointer generate) {
		int i, j;
		int fd, c, total_count;
		boolean ok;
		long l, age, time_saved = 0;
		String temp;
		int int_tmp;
		CaveType c_ptr;
		MonsterRecallType r_ptr;
		PlayerMisc m_ptr;
		Stats s_ptr;
		PlayerFlags f_ptr;
		StoreType st_ptr;
		int char_tmp, ychar, xchar, count;
		int version_maj = 0, version_min = 0, patch_level = 0;
		InvenType t_ptr;
		
		sigs.noSignals();
		generate.value(true);
		fd = -1;
		
		DataInputStream dis;
		
		try {
			dis = new DataInputStream(new FileInputStream(fileptr));
			bytes = new byte[(int)fileptr.length()];
			b_ptr = 0;
			dis.readFully(bytes);
			dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
//		if (access(var.savefile, 0) != 0) {
//			sigs.signals();
//			io.msg_print("Savefile does not exist.");
//			return false;	/* Don't bother with messages here. File absent. */
//		}
		
		IO.clearScreen();
		
		temp = String.format("Savefile %s present. Attempting restore.", Variable.savefile);
		IO.putBuffer(temp, 23, 0);
		
		if (Variable.turn >= 0) {
			IO.printMessage("IMPOSSIBLE! Attempt to restore while still alive!");
		
		/* Allow restoring a file belonging to someone else, if we can delete it. */
		/* Hence first try to read without doing a chmod. */
		
//		} else if ((fd = open(var.savefile, O_RDONLY, 0)) < 0 && (chmod(var.savefile, 0400) < 0 || (fd = open(var.savefile, O_RDONLY, 0)) < 0)) {
//			io.msg_print("Can't open file for reading.");
		} else {
			Variable.turn = -1;
			ok = true;
			
//			close(fd);
//			fileptr = fopen(var.savefile, "r");
			if (fileptr == null) {
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			
			IO.print("Restoring Memory...", 0, 0);
			IO.putQio();
			
			xor_byte = 0;
			version_maj = readByte();
			xor_byte = 0;
			version_min = readByte();
			xor_byte = 0;
			patch_level = readByte();
			xor_byte = 0;
			xor_byte = readByte();
			
			/* COMPAT support savefiles from 5.0.14 to 5.0.17 */
			/* support savefiles from 5.1.0 to present */
			if ((version_maj != Constants.CUR_VERSION_MAJ) || (version_min == 0 && patch_level < 14)) {
				IO.print("Sorry. This savefile is from a different version of umoria.", 2, 0);
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			
			int_tmp = readInt();
			while (int_tmp != 0xFFFF) {
				if (int_tmp >= Constants.MAX_CREATURES) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				r_ptr = Variable.c_recall[int_tmp];
				r_ptr.r_cmove = readInt();
				r_ptr.r_spells = readInt();
				r_ptr.r_kills = readInt();
				r_ptr.r_deaths = readInt();
				r_ptr.r_cdefense = readInt();
				r_ptr.r_wake = readByte();
				r_ptr.r_ignore = readByte();
				readInts(r_ptr.r_attacks, Constants.MAX_MON_NATTACK);
				int_tmp = readInt();
			}
			
			/* for save files before 5.2.2, read and ignore log_index (sic) */
			if ((version_min < 2) || (version_min == 2 && patch_level < 2)) {
				int_tmp = readInt();
			}
			l = readLong();
			
			Variable.find_cut.value((l & 0x1) != 0);
			Variable.find_examine.value((l & 0x2) != 0);
			Variable.find_prself.value((l & 0x4) != 0);
			Variable.find_bound.value((l & 0x8) != 0);
			Variable.prompt_carry_flag.value((l & 0x10) != 0);
			Variable.rogue_like_commands.value((l & 0x20) != 0);
			Variable.show_weight_flag.value((l & 0x40) != 0);
			Variable.highlight_seams.value((l & 0x80) != 0);
			Variable.find_ignore_doors.value((l & 0x100) != 0);
			/* save files before 5.2.2 don't have sound_beep_flag, set it on
			 * for compatibility */
			if ((version_min < 2) || (version_min == 2 && patch_level < 2)) {
				Variable.sound_beep_flag.value(true);
			} else {
				Variable.sound_beep_flag.value((l & 0x200) != 0);
			}
			/* save files before 5.2.2 don't have display_counts, set it on
			 * for compatibility */
			if ((version_min < 2) || (version_min == 2 && patch_level < 2)) {
				Variable.display_counts.value(true);
			} else {
				Variable.display_counts.value((l & 0x400) != 0);
			}
			
			/* Don't allow resurrection of total_winner characters.  It causes
			 * problems because the character level is out of the allowed range.  */
			if (Variable.to_be_wizard && (l & 0x40000000L) != 0) {
				IO.printMessage("Sorry, this character is retired from moria.");
				IO.printMessage("You can not resurrect a retired character.");
			} else if (Variable.to_be_wizard && (l & 0x80000000L) != 0 && IO.getCheck("Resurrect a dead character?")) {
				l &= ~0x80000000L;
			}
			if ((l & 0x80000000L) == 0) {
				m_ptr = Player.py.misc;
				m_ptr.name = readString(Player.py.PLAYER_NAME_SIZE);
				m_ptr.male = (readByte() == 1);
				m_ptr.au = readInt();
				m_ptr.max_exp = readInt();
				m_ptr.exp = readInt();
				m_ptr.exp_frac = readInt();
				m_ptr.age = readInt();
				m_ptr.ht = readInt();
				m_ptr.wt = readInt();
				m_ptr.lev = readInt();
				m_ptr.max_dlv = readInt();
				m_ptr.srh = readInt();
				m_ptr.fos = readInt();
				m_ptr.bth = readInt();
				m_ptr.bthb = readInt();
				m_ptr.mana = readInt();
				m_ptr.mhp = readInt();
				m_ptr.ptohit = readInt();
				m_ptr.ptodam = readInt();
				m_ptr.pac = readInt();
				m_ptr.ptoac = readInt();
				m_ptr.dis_th = readInt();
				m_ptr.dis_td = readInt();
				m_ptr.dis_ac = readInt();
				m_ptr.dis_tac = readInt();
				m_ptr.disarm = readInt();
				m_ptr.save = readInt();
				m_ptr.sc = readInt();
				m_ptr.stl = readInt();
				m_ptr.pclass = readByte();
				m_ptr.prace = readByte();
				m_ptr.hitdie = readByte();
				m_ptr.expfact = readByte();
				m_ptr.cmana = readInt();
				m_ptr.cmana_frac = readInt();
				m_ptr.chp = readInt();
				m_ptr.chp_frac = readInt();
				for (i = 0; i < 4; i++) {
					m_ptr.history[i] = readString(60);
				}
				
				s_ptr = Player.py.stats;
				readInts(s_ptr.max_stat, 6);
				readInts(s_ptr.cur_stat, 6);
				readInts(s_ptr.mod_stat, 6);
				readInts(s_ptr.use_stat, 6);
				
				f_ptr = Player.py.flags;
				f_ptr.status = readLong();
				f_ptr.rest = readInt();
				f_ptr.blind = readInt();
				f_ptr.paralysis = readInt();
				f_ptr.confused = readInt();
				f_ptr.food = readInt();
				f_ptr.food_digested = readInt();
				f_ptr.protection = readInt();
				f_ptr.speed = readInt();
				f_ptr.fast = readInt();
				f_ptr.slow = readInt();
				f_ptr.afraid = readInt();
				f_ptr.poisoned = readInt();
				f_ptr.image = readInt();
				f_ptr.protevil = readInt();
				f_ptr.invuln = readInt();
				f_ptr.hero = readInt();
				f_ptr.shero = readInt();
				f_ptr.blessed = readInt();
				f_ptr.resist_heat = readInt();
				f_ptr.resist_cold = readInt();
				f_ptr.detect_inv = readInt();
				f_ptr.word_recall = readInt();
				f_ptr.see_infra = readInt();
				f_ptr.tim_infra = readInt();
				f_ptr.see_inv = (readByte() == 1);
				f_ptr.teleport = readByte();
				f_ptr.free_act = (readByte() == 1);
				f_ptr.slow_digest = (readByte() == 1);
				f_ptr.aggravate = readByte();
				f_ptr.fire_resist = readByte();
				f_ptr.cold_resist = readByte();
				f_ptr.acid_resist = readByte();
				f_ptr.regenerate = (readByte() == 1);
				f_ptr.lght_resist = readByte();
				f_ptr.ffall = readByte();
				f_ptr.sustain_str = (readByte() == 1);
				f_ptr.sustain_int = (readByte() == 1);
				f_ptr.sustain_wis = (readByte() == 1);
				f_ptr.sustain_con = (readByte() == 1);
				f_ptr.sustain_dex = (readByte() == 1);
				f_ptr.sustain_chr = (readByte() == 1);
				f_ptr.confuse_monster = (readByte() == 1);
				f_ptr.new_spells = readByte();
				
				Variable.missile_ctr = readInt();
				Variable.turn = readInt();
				Treasure.inven_ctr = readInt();
				if (Treasure.inven_ctr > Constants.INVEN_WIELD) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				for (i = 0; i < Treasure.inven_ctr; i++) {
					readItem(Treasure.inventory[i]);
				}
				for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
					readItem(Treasure.inventory[i]);
				}
				Treasure.inven_weight = readInt();
				Treasure.equip_ctr = readInt();
				Player.spell_learned = readInt();
				Player.spell_worked = readInt();
				Player.spell_forgotten = readInt();
				readInts(Player.spell_order, 32);
				readInts(Treasure.object_ident, Constants.OBJECT_IDENT_SIZE);
				Variable.randes_seed = readLong();
				Variable.town_seed = readLong();
				Variable.last_msg = readInt();
				for (i = 0; i < Constants.MAX_SAVE_MSG; i++) {
					Variable.old_msg[i] = readString(Constants.VTYPESIZ);
				}
				
				Variable.panic_save = readInt();
				Variable.total_winner = (readInt() == 1);
				Variable.noscore = readInt();
				readInts(Player.player_hp, Constants.MAX_PLAYER_LEVEL);
				
				if ((version_min >= 2) || (version_min == 1 && patch_level >= 3)) {
					for (i = 0; i < Constants.MAX_STORES; i++) {
						st_ptr = Variable.store[i];
						st_ptr.store_open = readInt();
						st_ptr.insult_cur = readInt();
						st_ptr.owner = readByte();
						st_ptr.store_ctr = readByte();
						st_ptr.good_buy = readInt();
						st_ptr.bad_buy = readInt();
						if (st_ptr.store_ctr > Constants.STORE_INVEN_MAX) {
							return error(ok, fd, time_saved, version_maj, version_min);
						}
						for (j = 0; j < st_ptr.store_ctr; j++) {
							st_ptr.store_inven[j].scost = readInt();
							readItem(st_ptr.store_inven[j].sitem);
						}
					}
				}
				
				if ((version_min >= 2) || (version_min == 1 && patch_level >= 3)) {
					time_saved = readLong();
				}
				
				if (version_min >= 2) {
					Variable.died_from = readString(25);
				}
				
				if ((version_min >= 3) || (version_min == 2 && patch_level >= 2)) {
					Variable.max_score = readInt();
				} else {
					Variable.max_score = 0;
				}
				
				if ((version_min >= 3) || (version_min == 2 && patch_level >= 2)) {
					Variable.birth_date = readLong();
				} else {
					Variable.birth_date = java.util.Calendar.getInstance().getTimeInMillis();
				}
			}
			if (/*(c = getc(fileptr)) == EOF ||*/ (l & 0x80000000L) != 0) {
				if ((l & 0x80000000L) == 0) {
					if (!Variable.to_be_wizard || Variable.turn < 0) {
						return error(ok, fd, time_saved, version_maj, version_min);
					}
					IO.print("Attempting a resurrection!", 0, 0);
					if (Player.py.misc.chp < 0) {
						Player.py.misc.chp =	 0;
						Player.py.misc.chp_frac = 0;
					}
					/* don't let him starve to death immediately */
					if (Player.py.flags.food < 0) {
						Player.py.flags.food = 0;
					}
					/* don't let him die of poison again immediately */
					if (Player.py.flags.poisoned > 1) {
						Player.py.flags.poisoned = 1;
					}
					Variable.dun_level = 0; /* Resurrect on the town level. */
					Variable.character_generated = true;
					/* set noscore to indicate a resurrection, and don't enter
					 * wizard mode */
					Variable.to_be_wizard = false;
					Variable.noscore |= 0x1;
				} else {
					/* Make sure that this message is seen, since it is a bit
					 * more interesting than the other messages.  */
					IO.printMessage("Restoring Memory of a departed spirit...");
					Variable.turn = -1;
				}
				IO.putQio();
				return closeFiles(ok, fd, time_saved, version_maj, version_min);
			}
//			if (ungetc(c, fileptr) == EOF) {
//				return _error(ok, fd, time_saved, version_maj, version_min);
//			}
			
			IO.print("Restoring Character...", 0, 0);
			IO.putQio();
			
			/* only level specific info should follow, not present for dead
			 * characters */
			
			Variable.dun_level = readInt();
			Player.char_row = readInt();
			Player.char_col = readInt();
			Monsters.mon_tot_mult = readInt();
			Variable.cur_height = readInt();
			Variable.cur_width = readInt();
			Variable.max_panel_rows = readInt();
			Variable.max_panel_cols = readInt();
			
			/* read in the creature ptr info */
			char_tmp = readByte() & 0xFF;
			while (char_tmp != 0xFF) {
				ychar = char_tmp;
				xchar = readByte() & 0xFF;
				char_tmp = readByte() & 0xFF;
				if (xchar > Constants.MAX_WIDTH || ychar > Constants.MAX_HEIGHT) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				Variable.cave[ychar][xchar].cptr = char_tmp;
				char_tmp = readByte();
			}
			/* read in the treasure ptr info */
			char_tmp = readByte() & 0xFF;
			while (char_tmp != 0xFF) {
				ychar = char_tmp;
				xchar = readByte() & 0xFF;
				char_tmp = readByte() & 0xFF;
				if (xchar > Constants.MAX_WIDTH || ychar > Constants.MAX_HEIGHT) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				Variable.cave[ychar][xchar].tptr = char_tmp;
				char_tmp = readByte() & 0xFF;
			}
			/* read in the rest of the cave info */
			c_ptr = Variable.cave[0][0];
			total_count = 0;
			while (total_count != Constants.MAX_HEIGHT * Constants.MAX_WIDTH) {
				count = readByte();
				char_tmp = readByte();
//				for (i = count; i > 0; i--) {
//					if (c_ptr >= var.cave[Constants.MAX_HEIGHT][0]) {
//						return _error(ok, fd, time_saved, version_maj, version_min);
//					}
//					c_ptr.fval = char_tmp & 0xF;
//					c_ptr.lr = (((char_tmp >> 4) & 0x1) == 1);
//					c_ptr.fm = (((char_tmp >> 5) & 0x1) == 1);
//					c_ptr.pl = (((char_tmp >> 6) & 0x1) == 1);
//					c_ptr.tl = (((char_tmp >> 7) & 0x1) == 1);
//					c_ptr++;
//				}
				total_count += count;
			}
			
			Treasure.tcptr = readInt();
			if (Treasure.tcptr > Constants.MAX_TALLOC) {
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			for (i = Constants.MIN_TRIX; i < Treasure.tcptr; i++) {
				readItem(Treasure.t_list[i]);
			}
			Monsters.mfptr = readInt();
			if (Monsters.mfptr > Constants.MAX_MALLOC) {
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			for (i = Constants.MIN_MONIX; i < Monsters.mfptr; i++) {
				readMonster(Monsters.m_list[i]);
			}
			
			/* change walls and floors to graphic symbols */
			for (i = Treasure.tcptr - 1; i >= Constants.MIN_TRIX; i--) {
				t_ptr = Treasure.t_list[Treasure.tcptr - 1];
				if (t_ptr.tchar == '#') {
					t_ptr.tchar = Variable.wallsym;
				}
			}
			
			generate.value(false);  /* We have restored a cave - no need to generate. */
			
			if ((version_min == 1 && patch_level < 3) || (version_min == 0)) {
				for (i = 0; i < Constants.MAX_STORES; i++) {
					st_ptr = Variable.store[i];
					st_ptr.store_open = readInt();
					st_ptr.insult_cur = readInt();
					st_ptr.owner = readByte();
					st_ptr.store_ctr = readByte();
					st_ptr.good_buy = readInt();
					st_ptr.bad_buy = readInt();
					if (st_ptr.store_ctr > Constants.STORE_INVEN_MAX) {
						return error(ok, fd, time_saved, version_maj, version_min);
					}
					for (j = 0; j < st_ptr.store_ctr; j++) {
						st_ptr.store_inven[j].scost = readInt();
						readItem(st_ptr.store_inven[j].sitem);
					}
				}
			}
			
			/* read the time that the file was saved */
			if (version_min == 0 && patch_level < 16) {
				time_saved = 0; /* no time in file, clear to zero */
			} else if (version_min == 1 && patch_level < 3) {
				time_saved = readLong();
			}
			
//			if (ferror(fileptr)) {
//				return _error(ok, fd, time_saved, version_maj, version_min);
//			}
			
			if (Variable.turn < 0) {
				return error(ok, fd, time_saved, version_maj, version_min);	/* Assume bad data. */
			} else {
				/* don't overwrite the killed by string if character is dead */
				if (Player.py.misc.chp >= 0) {
					Variable.died_from = "(alive and well)";
				}
				Variable.character_generated = true;
			}
			
			closeFiles(ok, fd, time_saved, version_maj, version_min);
		}
		
		Variable.turn = -1;
		IO.print("Please try again without that savefile.", 1, 0);
		sigs.signals();
		Death.exitGame();
		
		return false;	/* not reached, unless on mac */
	}
	
	private static boolean error(boolean ok, int fd, long time_saved, int version_maj, int version_min) {
//		return closefiles(ok, fd, time_saved, version_maj, version_min);
		return false;
	}
	
	private static boolean closeFiles(boolean ok, int fd, long time_saved, int version_maj, int version_min) {
		long age;
		String temp;
		
//		if (fileptr != null) {
//			if (fclose(fileptr) < 0) {
//				ok = false;
//			}
//		}
//		if (fd >= 0) {
//			close(fd);
//		}
		
		if (!ok) {
			IO.printMessage("Error during reading of file.");
		} else {
			/* let the user overwrite the old savefile when save/quit */
			from_savefile = 1;
			
			sigs.signals();
			
			if (Variable.panic_save == 1) {
				temp = String.format("This game is from a panic save.  Score will not be added to scoreboard.");
				IO.printMessage(temp);
			} else if ((Variable.noscore & 0x04) == 0 && Death.isDuplicateCharacter()) {
				temp = String.format("This character is already on the scoreboard; it will not be scored again.");
				IO.printMessage(temp);
				Variable.noscore |= 0x4;
			}
			
			if (Variable.turn >= 0) {
				/* Only if a full restoration. */
				Variable.weapon_heavy = false;
				Variable.pack_heavy = 0;
				Misc3.checkStrength();
				
				/* rotate store inventory, depending on how old the save file */
				/* is foreach day old (rounded up), call store_maint */
				/* calculate age in seconds */
				start_time = java.util.Calendar.getInstance().getTimeInMillis();
				/* check for reasonable values of time here ... */
				if (start_time < time_saved) {
					age = 0;
				} else {
					age = start_time - time_saved;
				}
				
				age = (age + 43200L) / 86400L;  /* age in days */
				if (age > 10) age = 10; /* in case savefile is very old */
				for (int i = 0; i < age; i++) {
					store1.storeInventoryInit();
				}
			}
			
			if (Variable.noscore == 0) {
				IO.printMessage("This save file cannot be used to get on the score board.");
			}
			
			if (version_maj != Constants.CUR_VERSION_MAJ || version_min != Constants.CUR_VERSION_MIN) {
				temp = String.format(
						"Save file version %d.%d %s on game version %d.%d.",
						version_maj, version_min,
						(version_min <= Constants.CUR_VERSION_MIN) ? "accepted" : "risky" ,
						Constants.CUR_VERSION_MAJ, Constants.CUR_VERSION_MIN);
				IO.printMessage(temp);
			}
			
			return Variable.turn >= 0;
			/* If false, only restored options and monster memory. */
		}
		return false;
	}
	
	private static void writeByte(byte c) {
		if (b_ptr >= bytes.length) {
			bytes = Arrays.copyOf(bytes, bytes.length * 2);
		}
		xor_byte ^= c;
		bytes[b_ptr] = xor_byte;
		b_ptr++;
	}
	
	private static void writeChar(char c) {
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			writeByte((byte)(c >> (8 * i)));
		}
	}
	
	private static void writeInt(int s) {
		for (int i = 0; i < SIZEOF_INT; i++) {
			writeByte((byte)(s >> (8 * i)));
		}
	}
	
	private static void writeLong(long l) {
		for (int i = 0; i < SIZEOF_LONG; i++) {
			writeByte((byte)(l >> (8 * i)));
		}
	}
	
	private static void writeBytes(byte[] c, int count) {
		for (int i = 0; i < count; i++) {
			writeByte(c[i]);
		}
	}
	
	private static void writeString(String str, int len) {
		char[] c = str.toCharArray();
		int i;
		
		for (i = 0; i < c.length; i++) {
			if (i == len) {
				break;
			}
			writeChar(c[i]);
		}
		for (; i < len; i++) {
			writeChar('\0');
		}
	}
	
	private static void writeInts(int[] s, int count) {
		int i;
		
		for (i = 0; i < s.length; i++) {
			if (i == count) {
				break;
			}
			writeInt(s[i]);
		}
		for (; i < count; i++) {
			writeInt(0);
		}
	}
	
	private static void writeItem(InvenType item) {
		writeInt(item.index);
		writeInt(item.name2);
		writeString(item.inscrip, item.INSCRIP_SIZE);
		writeInt(item.flags);
		writeInt(item.tval);
		writeInt(item.tchar);
		writeInt(item.p1);
		writeInt(item.cost);
		writeInt(item.subval);
		writeInt(item.number);
		writeInt(item.weight);
		writeInt(item.tohit);
		writeInt(item.todam);
		writeInt(item.ac);
		writeInt(item.toac);
		writeInts(item.damage, 2);
		writeInt(item.level);
		writeInt(item.ident);
	}
	
	private static void writeMonster(MonsterType mon) {
		writeInt(mon.hp);
		writeInt(mon.csleep);
		writeInt(mon.cspeed);
		writeInt(mon.mptr);
		writeInt(mon.fy);
		writeInt(mon.fx);
		writeInt(mon.cdis);
		writeByte((byte)(mon.ml ? 0x1 : 0x0));
		writeInt(mon.stunned);
		writeByte((byte)((mon.confused > 0) ? 0x1 : 0x0));
	}
	
	private static byte getNextByte() {
		if (b_ptr >= bytes.length) {
			return 0;
		}
		byte b = bytes[b_ptr];
		b_ptr++;
		return b;
	}
	
	private static byte readByte() {
		byte c, ptr;
		
		c = getNextByte();
		ptr = (byte)((c ^ xor_byte) & 0xFF);
		xor_byte = c;
		
		return ptr;
	}
	
	private static char readChar() {
		char c = 0;
		
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			c |= (readByte() & 0xFF) << (8 * i);
		}
		
		return c;
	}
	
	private static int readInt() {
		int s;
		s = 0;
		
		for (int i = 0; i < SIZEOF_INT; i++) {
			s |= (readByte() & 0xFF) << (8 * i);
		}
		
		return s;
	}
	
	private static long readLong() {
		long l = 0;
		
		for (int i = 0; i < SIZEOF_LONG; i++) {
			l |= (readByte() & 0xFF) << (8 * i);
		}
		
		return l;
	}
	
	private static void readBytes(byte[] ch_ptr, int count) {
		int i;
		if (count > ch_ptr.length) {
			count = ch_ptr.length;
		}
		for (i = 0; i < ch_ptr.length; i++) {
			ch_ptr[i] = readByte();
		}
		for (; i < count; i++) {
			readByte();
		}
	}
	
	private static String readString(int len) {
		char[] str = new char[len];
		int i;
		
		for (i = 0; i < len; i++) {
			char c = readChar();
			if (c == '\0') {
				break;
			}
			str[i] = c;
		}
		if (i < len) {
			// truncate empty characters
			str = Arrays.copyOf(str, i);
		}
		for (; i < len; i++) {
			readChar();
		}
		
		return new String(str);
	}
	
	private static void readInts(int[] ptr, int count) {
		int i;
		
		for (i = 0; i < ptr.length; i++) {
			if (i == count) {
				break;
			}
			ptr[i] = readInt();
		}
		for (; i < count; i++) {
			readInt();
		}
	}
	
	private static void readItem(InvenType item) {
		item.index = readInt();
		item.name2 = readByte();
		item.inscrip = readString(item.INSCRIP_SIZE);
		item.flags = readInt();
		item.tval = readByte();
		item.tchar = (char)readByte();
		item.p1 = readInt();
		item.cost = readInt();
		item.subval = readByte();
		item.number = readByte();
		item.weight = readInt();
		item.tohit = readInt();
		item.todam = readInt();
		item.ac = readInt();
		item.toac = readInt();
		readInts(item.damage, 2);
		item.level = readByte();
		item.ident = readByte();
	}
	
	private static void readMonster(MonsterType mon) {
		mon.hp = readInt();
		mon.csleep = readInt();
		mon.cspeed = readInt();
		mon.mptr = readInt();
		mon.fy = readByte();
		mon.fx = readByte();
		mon.cdis = readByte();
		mon.ml = (readByte() == 1);
		mon.stunned = readByte();
		mon.confused = readByte();
	}
	
	/* functions called from death.c to implement the score file */
	
	/* set the local fileptr to the scorefile fileptr */
	public static void setFilePointer(java.io.File file) {
		fileptr = file;
	}
	
	public static void writeHighScore(HighScoreType score) {
		/* Save the encryption byte for robustness.  */
		writeByte(xor_byte);
		
		writeInt(score.points);
		writeLong(score.birth_date);
		writeInt(score.uid);
		writeInt(score.mhp);
		writeInt(score.chp);
		writeInt(score.dun_level);
		writeInt(score.lev);
		writeInt(score.max_dlv);
		writeInt(score.sex);
		writeInt(score.race);
		writeInt(score.Class);
		writeString(score.name, Player.py.PLAYER_NAME_SIZE);
		writeString(score.died_from, 25);
	}
	
	public static void readHighScore(HighScoreType score) {
		/* Read the encryption byte.  */
		xor_byte = readByte();
		
		score.points = readInt();
		score.birth_date = readLong();
		score.uid = readInt();
		score.mhp = readInt();
		score.chp = readInt();
		score.dun_level = readInt();
		score.lev = readInt();
		score.max_dlv = readInt();
		score.sex = readInt();
		score.race = readInt();
		score.Class = readInt();
		score.name = readString(Player.py.PLAYER_NAME_SIZE);
		score.died_from = readString(25);
	}
}
