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
	
	private static boolean sv_write() {
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
				wr_int(i);
				wr_long(r_ptr.r_cmove);
				wr_long(r_ptr.r_spells);
				wr_int(r_ptr.r_kills);
				wr_int(r_ptr.r_deaths);
				wr_int(r_ptr.r_cdefense);
				wr_int(r_ptr.r_wake);
				wr_int(r_ptr.r_ignore);
				wr_ints(r_ptr.r_attacks, Constants.MAX_MON_NATTACK);
			}
		}
		wr_int(0xFFFF); /* sentinel to indicate no more monster info */
		
		wr_long(l);
		
		m_ptr = Player.py.misc;
		wr_string(m_ptr.name, Player.py.PLAYER_NAME_SIZE);
		wr_byte((m_ptr.male ? (byte)1 : (byte)0));
		wr_int(m_ptr.au);
		wr_int(m_ptr.max_exp);
		wr_int(m_ptr.exp);
		wr_int(m_ptr.exp_frac);
		wr_int(m_ptr.age);
		wr_int(m_ptr.ht);
		wr_int(m_ptr.wt);
		wr_int(m_ptr.lev);
		wr_int(m_ptr.max_dlv);
		wr_int(m_ptr.srh);
		wr_int(m_ptr.fos);
		wr_int(m_ptr.bth);
		wr_int(m_ptr.bthb);
		wr_int(m_ptr.mana);
		wr_int(m_ptr.mhp);
		wr_int(m_ptr.ptohit);
		wr_int(m_ptr.ptodam);
		wr_int(m_ptr.pac);
		wr_int(m_ptr.ptoac);
		wr_int(m_ptr.dis_th);
		wr_int(m_ptr.dis_td);
		wr_int(m_ptr.dis_ac);
		wr_int(m_ptr.dis_tac);
		wr_int(m_ptr.disarm);
		wr_int(m_ptr.save);
		wr_int(m_ptr.sc);
		wr_int(m_ptr.stl);
		wr_int(m_ptr.pclass);
		wr_int(m_ptr.prace);
		wr_int(m_ptr.hitdie);
		wr_int(m_ptr.expfact);
		wr_int(m_ptr.cmana);
		wr_int(m_ptr.cmana_frac);
		wr_int(m_ptr.chp);
		wr_int(m_ptr.chp_frac);
		for (i = 0; i < 4; i++) {
			wr_string(m_ptr.history[i], 60);
		}
		
		s_ptr = Player.py.stats;
		wr_ints(s_ptr.max_stat, 6);
		wr_ints(s_ptr.cur_stat, 6);
		wr_ints(s_ptr.mod_stat, 6);
		wr_ints(s_ptr.use_stat, 6);
		
		f_ptr = Player.py.flags;
		wr_long(f_ptr.status);
		wr_int(f_ptr.rest);
		wr_int(f_ptr.blind);
		wr_int(f_ptr.paralysis);
		wr_int(f_ptr.confused);
		wr_int(f_ptr.food);
		wr_int(f_ptr.food_digested);
		wr_int(f_ptr.protection);
		wr_int(f_ptr.speed);
		wr_int(f_ptr.fast);
		wr_int(f_ptr.slow);
		wr_int(f_ptr.afraid);
		wr_int(f_ptr.poisoned);
		wr_int(f_ptr.image);
		wr_int(f_ptr.protevil);
		wr_int(f_ptr.invuln);
		wr_int(f_ptr.hero);
		wr_int(f_ptr.shero);
		wr_int(f_ptr.blessed);
		wr_int(f_ptr.resist_heat);
		wr_int(f_ptr.resist_cold);
		wr_int(f_ptr.detect_inv);
		wr_int(f_ptr.word_recall);
		wr_int(f_ptr.see_infra);
		wr_int(f_ptr.tim_infra);
		wr_byte((f_ptr.see_inv ? (byte)1 : (byte)0));
		wr_byte((byte)f_ptr.teleport);
		wr_byte((f_ptr.free_act ? (byte)1 : (byte)0));
		wr_byte((f_ptr.slow_digest ? (byte)1 : (byte)0));
		wr_byte((byte)f_ptr.aggravate);
		wr_byte((byte)f_ptr.fire_resist);
		wr_byte((byte)f_ptr.cold_resist);
		wr_byte((byte)f_ptr.acid_resist);
		wr_byte((f_ptr.regenerate ? (byte)1 : (byte)0));
		wr_byte((byte)f_ptr.lght_resist);
		wr_byte((byte)f_ptr.ffall);
		wr_byte((f_ptr.sustain_str ? (byte)1 : (byte)0));
		wr_byte((f_ptr.sustain_int ? (byte)1 : (byte)0));
		wr_byte((f_ptr.sustain_wis ? (byte)1 : (byte)0));
		wr_byte((f_ptr.sustain_con ? (byte)1 : (byte)0));
		wr_byte((f_ptr.sustain_dex ? (byte)1 : (byte)0));
		wr_byte((f_ptr.sustain_chr ? (byte)1 : (byte)0));
		wr_byte((f_ptr.confuse_monster ? (byte)1 : (byte)0));
		wr_int(f_ptr.new_spells);
		
		wr_int(Variable.missile_ctr);
		wr_int(Variable.turn);
		wr_int(Treasure.inven_ctr);
		for (i = 0; i < Treasure.inven_ctr; i++) {
			wr_item(Treasure.inventory[i]);
		}
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
			wr_item(Treasure.inventory[i]);
		}
		wr_int(Treasure.inven_weight);
		wr_int(Treasure.equip_ctr);
		wr_long(Player.spell_learned);
		wr_long(Player.spell_worked);
		wr_long(Player.spell_forgotten);
		wr_ints(Player.spell_order, 32);
		wr_ints(Treasure.object_ident, Constants.OBJECT_IDENT_SIZE);
		wr_long(Variable.randes_seed);
		wr_long(Variable.town_seed);
		wr_int(Variable.last_msg);
		for (i = 0; i < Constants.MAX_SAVE_MSG; i++) {
			wr_string(Variable.old_msg[i], Constants.VTYPESIZ);
		}
		
		/* this indicates 'cheating' if it is a one */
		wr_int(Variable.panic_save);
		wr_byte((Variable.total_winner ? (byte)1 : (byte)0));
		wr_int(Variable.noscore);
		wr_ints(Player.player_hp, Constants.MAX_PLAYER_LEVEL);
		
		for (i = 0; i < Constants.MAX_STORES; i++) {
			st_ptr = Variable.store[i];
			wr_long((long)st_ptr.store_open);
			wr_int(st_ptr.insult_cur);
			wr_byte((byte)st_ptr.owner);
			wr_byte((byte)st_ptr.store_ctr);
			wr_int(st_ptr.good_buy);
			wr_int(st_ptr.bad_buy);
			for (j = 0; j < st_ptr.store_ctr; j++) {
				wr_long((long)st_ptr.store_inven[j].scost);
				wr_item(st_ptr.store_inven[j].sitem);
			}
		}
		
		/* save the current time in the savefile */
		l = java.util.Calendar.getInstance().getTimeInMillis();
		if (l < start_time) {
			/* someone is messing with the clock!, assume that we have been
			 * playing for 1 day */
			l = start_time + 86400L;
		}
		wr_long(l);
		
		/* starting with 5.2, put died_from string in savefile */
		wr_string(Variable.died_from, 25);
		
		/* starting with 5.2.2, put the max_score in the savefile */
		l = Death.total_points();
		wr_long(l);
		
		/* starting with 5.2.2, put the birth_date in the savefile */
		wr_long(Variable.birth_date);
		
		/* only level specific info follows, this allows characters to be
		 * resurrected, the dungeon level info is not needed for a resurrection */
		if (Variable.death) {
			/*if (ferror(fileptr) || fflush(fileptr) == EOF) {
				return false;
			}*/
			return true;
		}
		
		wr_int(Variable.dun_level);
		wr_int(Player.char_row);
		wr_int(Player.char_col);
		wr_int(Monsters.mon_tot_mult);
		wr_int(Variable.cur_height);
		wr_int(Variable.cur_width);
		wr_int(Variable.max_panel_rows);
		wr_int(Variable.max_panel_cols);
		
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.cptr != 0) {
					wr_byte((byte)i);
					wr_byte((byte)j);
					wr_byte((byte)c_ptr.cptr);
				}
			}
		}
		wr_byte((byte)0xFF); /* marks end of cptr info */
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.tptr != 0) {
					wr_byte((byte)i);
					wr_byte((byte)j);
					wr_byte((byte)c_ptr.tptr);
				}
			}
		}
		wr_byte((byte)0xFF); /* marks end of tptr info */
		/* must set counter to zero, note that code may write out two bytes
		 * unnecessarily */
		count = 0;
		prev_char = 0;
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				char_tmp = (short)(c_ptr.fval | ((c_ptr.lr ? 1 : 0) << 4) | ((c_ptr.fm ? 1 : 0) << 5) | ((c_ptr.pl ? 1 : 0) << 6) | ((c_ptr.tl ? 1 : 0) << 7));
				if (char_tmp != prev_char || count == Constants.MAX_UCHAR) {
					wr_byte((byte)count);
					wr_byte((byte)prev_char);
					prev_char = char_tmp;
					count = 1;
				} else {
					count++;
				}
			}
		}
		/* save last entry */
		wr_byte((byte)count);
		wr_byte((byte)prev_char);
		
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
		wr_int(Treasure.tcptr);
		for (i = Constants.MIN_TRIX; i < Treasure.tcptr; i++) {
			wr_item(Treasure.t_list[i]);
		}
		wr_int(Monsters.mfptr);
		for (i = Constants.MIN_MONIX; i < Monsters.mfptr; i++) {
			wr_monster(Monsters.m_list[i]);
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
	
	public static boolean save_char() {
		int i;
		String temp;
		
		while (!_save_char(Variable.savefile)) {
			temp = String.format("Savefile '%s' fails.", Variable.savefile);
			IO.msg_print(temp);
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
			IO.prt(temp, 0, 0);
		}
		return true;
	}
	
	private static boolean _save_char(String fnam) {
		String temp;
		boolean ok;
		int fd = 0;
		short char_tmp;
		
		if (Variable.character_saved != 0) {
			return true;	/* Nothing to save. */
		}
		
		sigs.nosignals();
		IO.put_qio();
		Moria1.disturb(true, false);	/* Turn off resting and searching. */
		Moria1.change_speed(-Variable.pack_heavy);	/* Fix the speed */
		Variable.pack_heavy = 0;
		ok = false;
//		fileptr = fopen(var.savefile, "w");
		
		if (fileptr != null) {
			xor_byte = 0;
			wr_int(Constants.CUR_VERSION_MAJ);
			xor_byte = 0;
			wr_int(Constants.CUR_VERSION_MIN);
			xor_byte = 0;
			wr_int(Constants.PATCH_LEVEL);
			xor_byte = 0;
			char_tmp = (short)(Misc1.randint(256) - 1);
			wr_byte((byte)char_tmp);
			/* Note that xor_byte is now equal to char_tmp */
			
			ok = sv_write();
			
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
			IO.msg_print(temp);
			return false;
		} else {
			Variable.character_saved = 1;
		}
		
		Variable.turn = -1;
		sigs.signals();
		
		return true;
	}
	
	/* Certain checks are omitted for the wizard. -CJS- */
	
	public static boolean get_char(BooleanPointer generate) {
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
		
		sigs.nosignals();
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
		
		IO.clear_screen();
		
		temp = String.format("Savefile %s present. Attempting restore.", Variable.savefile);
		IO.put_buffer(temp, 23, 0);
		
		if (Variable.turn >= 0) {
			IO.msg_print("IMPOSSIBLE! Attempt to restore while still alive!");
		
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
				return _error(ok, fd, time_saved, version_maj, version_min);
			}
			
			IO.prt("Restoring Memory...", 0, 0);
			IO.put_qio();
			
			xor_byte = 0;
			version_maj = rd_byte();
			xor_byte = 0;
			version_min = rd_byte();
			xor_byte = 0;
			patch_level = rd_byte();
			xor_byte = 0;
			xor_byte = rd_byte();
			
			/* COMPAT support savefiles from 5.0.14 to 5.0.17 */
			/* support savefiles from 5.1.0 to present */
			if ((version_maj != Constants.CUR_VERSION_MAJ) || (version_min == 0 && patch_level < 14)) {
				IO.prt("Sorry. This savefile is from a different version of umoria.", 2, 0);
				return _error(ok, fd, time_saved, version_maj, version_min);
			}
			
			int_tmp = rd_int();
			while (int_tmp != 0xFFFF) {
				if (int_tmp >= Constants.MAX_CREATURES) {
					return _error(ok, fd, time_saved, version_maj, version_min);
				}
				r_ptr = Variable.c_recall[int_tmp];
				r_ptr.r_cmove = rd_int();
				r_ptr.r_spells = rd_int();
				r_ptr.r_kills = rd_int();
				r_ptr.r_deaths = rd_int();
				r_ptr.r_cdefense = rd_int();
				r_ptr.r_wake = rd_byte();
				r_ptr.r_ignore = rd_byte();
				rd_ints(r_ptr.r_attacks, Constants.MAX_MON_NATTACK);
				int_tmp = rd_int();
			}
			
			/* for save files before 5.2.2, read and ignore log_index (sic) */
			if ((version_min < 2) || (version_min == 2 && patch_level < 2)) {
				int_tmp = rd_int();
			}
			l = rd_long();
			
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
				IO.msg_print("Sorry, this character is retired from moria.");
				IO.msg_print("You can not resurrect a retired character.");
			} else if (Variable.to_be_wizard && (l & 0x80000000L) != 0 && IO.get_check("Resurrect a dead character?")) {
				l &= ~0x80000000L;
			}
			if ((l & 0x80000000L) == 0) {
				m_ptr = Player.py.misc;
				m_ptr.name = rd_string(Player.py.PLAYER_NAME_SIZE);
				m_ptr.male = (rd_byte() == 1);
				m_ptr.au = rd_int();
				m_ptr.max_exp = rd_int();
				m_ptr.exp = rd_int();
				m_ptr.exp_frac = rd_int();
				m_ptr.age = rd_int();
				m_ptr.ht = rd_int();
				m_ptr.wt = rd_int();
				m_ptr.lev = rd_int();
				m_ptr.max_dlv = rd_int();
				m_ptr.srh = rd_int();
				m_ptr.fos = rd_int();
				m_ptr.bth = rd_int();
				m_ptr.bthb = rd_int();
				m_ptr.mana = rd_int();
				m_ptr.mhp = rd_int();
				m_ptr.ptohit = rd_int();
				m_ptr.ptodam = rd_int();
				m_ptr.pac = rd_int();
				m_ptr.ptoac = rd_int();
				m_ptr.dis_th = rd_int();
				m_ptr.dis_td = rd_int();
				m_ptr.dis_ac = rd_int();
				m_ptr.dis_tac = rd_int();
				m_ptr.disarm = rd_int();
				m_ptr.save = rd_int();
				m_ptr.sc = rd_int();
				m_ptr.stl = rd_int();
				m_ptr.pclass = rd_byte();
				m_ptr.prace = rd_byte();
				m_ptr.hitdie = rd_byte();
				m_ptr.expfact = rd_byte();
				m_ptr.cmana = rd_int();
				m_ptr.cmana_frac = rd_int();
				m_ptr.chp = rd_int();
				m_ptr.chp_frac = rd_int();
				for (i = 0; i < 4; i++) {
					m_ptr.history[i] = rd_string(60);
				}
				
				s_ptr = Player.py.stats;
				rd_ints(s_ptr.max_stat, 6);
				rd_ints(s_ptr.cur_stat, 6);
				rd_ints(s_ptr.mod_stat, 6);
				rd_ints(s_ptr.use_stat, 6);
				
				f_ptr = Player.py.flags;
				f_ptr.status = rd_long();
				f_ptr.rest = rd_int();
				f_ptr.blind = rd_int();
				f_ptr.paralysis = rd_int();
				f_ptr.confused = rd_int();
				f_ptr.food = rd_int();
				f_ptr.food_digested = rd_int();
				f_ptr.protection = rd_int();
				f_ptr.speed = rd_int();
				f_ptr.fast = rd_int();
				f_ptr.slow = rd_int();
				f_ptr.afraid = rd_int();
				f_ptr.poisoned = rd_int();
				f_ptr.image = rd_int();
				f_ptr.protevil = rd_int();
				f_ptr.invuln = rd_int();
				f_ptr.hero = rd_int();
				f_ptr.shero = rd_int();
				f_ptr.blessed = rd_int();
				f_ptr.resist_heat = rd_int();
				f_ptr.resist_cold = rd_int();
				f_ptr.detect_inv = rd_int();
				f_ptr.word_recall = rd_int();
				f_ptr.see_infra = rd_int();
				f_ptr.tim_infra = rd_int();
				f_ptr.see_inv = (rd_byte() == 1);
				f_ptr.teleport = rd_byte();
				f_ptr.free_act = (rd_byte() == 1);
				f_ptr.slow_digest = (rd_byte() == 1);
				f_ptr.aggravate = rd_byte();
				f_ptr.fire_resist = rd_byte();
				f_ptr.cold_resist = rd_byte();
				f_ptr.acid_resist = rd_byte();
				f_ptr.regenerate = (rd_byte() == 1);
				f_ptr.lght_resist = rd_byte();
				f_ptr.ffall = rd_byte();
				f_ptr.sustain_str = (rd_byte() == 1);
				f_ptr.sustain_int = (rd_byte() == 1);
				f_ptr.sustain_wis = (rd_byte() == 1);
				f_ptr.sustain_con = (rd_byte() == 1);
				f_ptr.sustain_dex = (rd_byte() == 1);
				f_ptr.sustain_chr = (rd_byte() == 1);
				f_ptr.confuse_monster = (rd_byte() == 1);
				f_ptr.new_spells = rd_byte();
				
				Variable.missile_ctr = rd_int();
				Variable.turn = rd_int();
				Treasure.inven_ctr = rd_int();
				if (Treasure.inven_ctr > Constants.INVEN_WIELD) {
					return _error(ok, fd, time_saved, version_maj, version_min);
				}
				for (i = 0; i < Treasure.inven_ctr; i++) {
					rd_item(Treasure.inventory[i]);
				}
				for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
					rd_item(Treasure.inventory[i]);
				}
				Treasure.inven_weight = rd_int();
				Treasure.equip_ctr = rd_int();
				Player.spell_learned = rd_int();
				Player.spell_worked = rd_int();
				Player.spell_forgotten = rd_int();
				rd_ints(Player.spell_order, 32);
				rd_ints(Treasure.object_ident, Constants.OBJECT_IDENT_SIZE);
				Variable.randes_seed = rd_long();
				Variable.town_seed = rd_long();
				Variable.last_msg = rd_int();
				for (i = 0; i < Constants.MAX_SAVE_MSG; i++) {
					Variable.old_msg[i] = rd_string(Constants.VTYPESIZ);
				}
				
				Variable.panic_save = rd_int();
				Variable.total_winner = (rd_int() == 1);
				Variable.noscore = rd_int();
				rd_ints(Player.player_hp, Constants.MAX_PLAYER_LEVEL);
				
				if ((version_min >= 2) || (version_min == 1 && patch_level >= 3)) {
					for (i = 0; i < Constants.MAX_STORES; i++) {
						st_ptr = Variable.store[i];
						st_ptr.store_open = rd_int();
						st_ptr.insult_cur = rd_int();
						st_ptr.owner = rd_byte();
						st_ptr.store_ctr = rd_byte();
						st_ptr.good_buy = rd_int();
						st_ptr.bad_buy = rd_int();
						if (st_ptr.store_ctr > Constants.STORE_INVEN_MAX) {
							return _error(ok, fd, time_saved, version_maj, version_min);
						}
						for (j = 0; j < st_ptr.store_ctr; j++) {
							st_ptr.store_inven[j].scost = rd_int();
							rd_item(st_ptr.store_inven[j].sitem);
						}
					}
				}
				
				if ((version_min >= 2) || (version_min == 1 && patch_level >= 3)) {
					time_saved = rd_long();
				}
				
				if (version_min >= 2) {
					Variable.died_from = rd_string(25);
				}
				
				if ((version_min >= 3) || (version_min == 2 && patch_level >= 2)) {
					Variable.max_score = rd_int();
				} else {
					Variable.max_score = 0;
				}
				
				if ((version_min >= 3) || (version_min == 2 && patch_level >= 2)) {
					Variable.birth_date = rd_long();
				} else {
					Variable.birth_date = java.util.Calendar.getInstance().getTimeInMillis();
				}
			}
			if (/*(c = getc(fileptr)) == EOF ||*/ (l & 0x80000000L) != 0) {
				if ((l & 0x80000000L) == 0) {
					if (!Variable.to_be_wizard || Variable.turn < 0) {
						return _error(ok, fd, time_saved, version_maj, version_min);
					}
					IO.prt("Attempting a resurrection!", 0, 0);
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
					IO.msg_print("Restoring Memory of a departed spirit...");
					Variable.turn = -1;
				}
				IO.put_qio();
				return closefiles(ok, fd, time_saved, version_maj, version_min);
			}
//			if (ungetc(c, fileptr) == EOF) {
//				return _error(ok, fd, time_saved, version_maj, version_min);
//			}
			
			IO.prt("Restoring Character...", 0, 0);
			IO.put_qio();
			
			/* only level specific info should follow, not present for dead
			 * characters */
			
			Variable.dun_level = rd_int();
			Player.char_row = rd_int();
			Player.char_col = rd_int();
			Monsters.mon_tot_mult = rd_int();
			Variable.cur_height = rd_int();
			Variable.cur_width = rd_int();
			Variable.max_panel_rows = rd_int();
			Variable.max_panel_cols = rd_int();
			
			/* read in the creature ptr info */
			char_tmp = rd_byte() & 0xFF;
			while (char_tmp != 0xFF) {
				ychar = char_tmp;
				xchar = rd_byte() & 0xFF;
				char_tmp = rd_byte() & 0xFF;
				if (xchar > Constants.MAX_WIDTH || ychar > Constants.MAX_HEIGHT) {
					return _error(ok, fd, time_saved, version_maj, version_min);
				}
				Variable.cave[ychar][xchar].cptr = char_tmp;
				char_tmp = rd_byte();
			}
			/* read in the treasure ptr info */
			char_tmp = rd_byte() & 0xFF;
			while (char_tmp != 0xFF) {
				ychar = char_tmp;
				xchar = rd_byte() & 0xFF;
				char_tmp = rd_byte() & 0xFF;
				if (xchar > Constants.MAX_WIDTH || ychar > Constants.MAX_HEIGHT) {
					return _error(ok, fd, time_saved, version_maj, version_min);
				}
				Variable.cave[ychar][xchar].tptr = char_tmp;
				char_tmp = rd_byte() & 0xFF;
			}
			/* read in the rest of the cave info */
			c_ptr = Variable.cave[0][0];
			total_count = 0;
			while (total_count != Constants.MAX_HEIGHT * Constants.MAX_WIDTH) {
				count = rd_byte();
				char_tmp = rd_byte();
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
			
			Treasure.tcptr = rd_int();
			if (Treasure.tcptr > Constants.MAX_TALLOC) {
				return _error(ok, fd, time_saved, version_maj, version_min);
			}
			for (i = Constants.MIN_TRIX; i < Treasure.tcptr; i++) {
				rd_item(Treasure.t_list[i]);
			}
			Monsters.mfptr = rd_int();
			if (Monsters.mfptr > Constants.MAX_MALLOC) {
				return _error(ok, fd, time_saved, version_maj, version_min);
			}
			for (i = Constants.MIN_MONIX; i < Monsters.mfptr; i++) {
				rd_monster(Monsters.m_list[i]);
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
					st_ptr.store_open = rd_int();
					st_ptr.insult_cur = rd_int();
					st_ptr.owner = rd_byte();
					st_ptr.store_ctr = rd_byte();
					st_ptr.good_buy = rd_int();
					st_ptr.bad_buy = rd_int();
					if (st_ptr.store_ctr > Constants.STORE_INVEN_MAX) {
						return _error(ok, fd, time_saved, version_maj, version_min);
					}
					for (j = 0; j < st_ptr.store_ctr; j++) {
						st_ptr.store_inven[j].scost = rd_int();
						rd_item(st_ptr.store_inven[j].sitem);
					}
				}
			}
			
			/* read the time that the file was saved */
			if (version_min == 0 && patch_level < 16) {
				time_saved = 0; /* no time in file, clear to zero */
			} else if (version_min == 1 && patch_level < 3) {
				time_saved = rd_long();
			}
			
//			if (ferror(fileptr)) {
//				return _error(ok, fd, time_saved, version_maj, version_min);
//			}
			
			if (Variable.turn < 0) {
				return _error(ok, fd, time_saved, version_maj, version_min);	/* Assume bad data. */
			} else {
				/* don't overwrite the killed by string if character is dead */
				if (Player.py.misc.chp >= 0) {
					Variable.died_from = "(alive and well)";
				}
				Variable.character_generated = true;
			}
			
			closefiles(ok, fd, time_saved, version_maj, version_min);
		}
		
		Variable.turn = -1;
		IO.prt("Please try again without that savefile.", 1, 0);
		sigs.signals();
		Death.exit_game();
		
		return false;	/* not reached, unless on mac */
	}
	
	private static boolean _error(boolean ok, int fd, long time_saved, int version_maj, int version_min) {
//		return closefiles(ok, fd, time_saved, version_maj, version_min);
		return false;
	}
	
	private static boolean closefiles(boolean ok, int fd, long time_saved, int version_maj, int version_min) {
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
			IO.msg_print("Error during reading of file.");
		} else {
			/* let the user overwrite the old savefile when save/quit */
			from_savefile = 1;
			
			sigs.signals();
			
			if (Variable.panic_save == 1) {
				temp = String.format("This game is from a panic save.  Score will not be added to scoreboard.");
				IO.msg_print(temp);
			} else if ((Variable.noscore & 0x04) == 0 && Death.duplicate_character()) {
				temp = String.format("This character is already on the scoreboard; it will not be scored again.");
				IO.msg_print(temp);
				Variable.noscore |= 0x4;
			}
			
			if (Variable.turn >= 0) {
				/* Only if a full restoration. */
				Variable.weapon_heavy = false;
				Variable.pack_heavy = 0;
				Misc3.check_strength();
				
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
					store1.store_maint();
				}
			}
			
			if (Variable.noscore == 0) {
				IO.msg_print("This save file cannot be used to get on the score board.");
			}
			
			if (version_maj != Constants.CUR_VERSION_MAJ || version_min != Constants.CUR_VERSION_MIN) {
				temp = String.format(
						"Save file version %d.%d %s on game version %d.%d.",
						version_maj, version_min,
						(version_min <= Constants.CUR_VERSION_MIN) ? "accepted" : "risky" ,
						Constants.CUR_VERSION_MAJ, Constants.CUR_VERSION_MIN);
				IO.msg_print(temp);
			}
			
			return Variable.turn >= 0;
			/* If false, only restored options and monster memory. */
		}
		return false;
	}
	
	private static void wr_byte(byte c) {
		if (b_ptr >= bytes.length) {
			bytes = Arrays.copyOf(bytes, bytes.length * 2);
		}
		xor_byte ^= c;
		bytes[b_ptr] = xor_byte;
		b_ptr++;
	}
	
	private static void wr_char(char c) {
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			wr_byte((byte)(c >> (8 * i)));
		}
	}
	
	private static void wr_int(int s) {
		for (int i = 0; i < SIZEOF_INT; i++) {
			wr_byte((byte)(s >> (8 * i)));
		}
	}
	
	private static void wr_long(long l) {
		for (int i = 0; i < SIZEOF_LONG; i++) {
			wr_byte((byte)(l >> (8 * i)));
		}
	}
	
	private static void wr_bytes(byte[] c, int count) {
		for (int i = 0; i < count; i++) {
			wr_byte(c[i]);
		}
	}
	
	private static void wr_string(String str, int len) {
		char[] c = str.toCharArray();
		int i;
		
		for (i = 0; i < c.length; i++) {
			if (i == len) {
				break;
			}
			wr_char(c[i]);
		}
		for (; i < len; i++) {
			wr_char('\0');
		}
	}
	
	private static void wr_ints(int[] s, int count) {
		int i;
		
		for (i = 0; i < s.length; i++) {
			if (i == count) {
				break;
			}
			wr_int(s[i]);
		}
		for (; i < count; i++) {
			wr_int(0);
		}
	}
	
	private static void wr_item(InvenType item) {
		wr_int(item.index);
		wr_int(item.name2);
		wr_string(item.inscrip, item.INSCRIP_SIZE);
		wr_int(item.flags);
		wr_int(item.tval);
		wr_int(item.tchar);
		wr_int(item.p1);
		wr_int(item.cost);
		wr_int(item.subval);
		wr_int(item.number);
		wr_int(item.weight);
		wr_int(item.tohit);
		wr_int(item.todam);
		wr_int(item.ac);
		wr_int(item.toac);
		wr_ints(item.damage, 2);
		wr_int(item.level);
		wr_int(item.ident);
	}
	
	private static void wr_monster(MonsterType mon) {
		wr_int(mon.hp);
		wr_int(mon.csleep);
		wr_int(mon.cspeed);
		wr_int(mon.mptr);
		wr_int(mon.fy);
		wr_int(mon.fx);
		wr_int(mon.cdis);
		wr_byte((byte)(mon.ml ? 0x1 : 0x0));
		wr_int(mon.stunned);
		wr_byte((byte)((mon.confused > 0) ? 0x1 : 0x0));
	}
	
	private static byte getNextByte() {
		if (b_ptr >= bytes.length) {
			return 0;
		}
		byte b = bytes[b_ptr];
		b_ptr++;
		return b;
	}
	
	private static byte rd_byte() {
		byte c, ptr;
		
		c = getNextByte();
		ptr = (byte)((c ^ xor_byte) & 0xFF);
		xor_byte = c;
		
		return ptr;
	}
	
	private static char rd_char() {
		char c = 0;
		
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			c |= (rd_byte() & 0xFF) << (8 * i);
		}
		
		return c;
	}
	
	private static int rd_int() {
		int s;
		s = 0;
		
		for (int i = 0; i < SIZEOF_INT; i++) {
			s |= (rd_byte() & 0xFF) << (8 * i);
		}
		
		return s;
	}
	
	private static long rd_long() {
		long l = 0;
		
		for (int i = 0; i < SIZEOF_LONG; i++) {
			l |= (rd_byte() & 0xFF) << (8 * i);
		}
		
		return l;
	}
	
	private static void rd_bytes(byte[] ch_ptr, int count) {
		int i;
		if (count > ch_ptr.length) {
			count = ch_ptr.length;
		}
		for (i = 0; i < ch_ptr.length; i++) {
			ch_ptr[i] = rd_byte();
		}
		for (; i < count; i++) {
			rd_byte();
		}
	}
	
	private static String rd_string(int len) {
		char[] str = new char[len];
		int i;
		
		for (i = 0; i < len; i++) {
			char c = rd_char();
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
			rd_char();
		}
		
		return new String(str);
	}
	
	private static void rd_ints(int[] ptr, int count) {
		int i;
		
		for (i = 0; i < ptr.length; i++) {
			if (i == count) {
				break;
			}
			ptr[i] = rd_int();
		}
		for (; i < count; i++) {
			rd_int();
		}
	}
	
	private static void rd_item(InvenType item) {
		item.index = rd_int();
		item.name2 = rd_byte();
		item.inscrip = rd_string(item.INSCRIP_SIZE);
		item.flags = rd_int();
		item.tval = rd_byte();
		item.tchar = (char)rd_byte();
		item.p1 = rd_int();
		item.cost = rd_int();
		item.subval = rd_byte();
		item.number = rd_byte();
		item.weight = rd_int();
		item.tohit = rd_int();
		item.todam = rd_int();
		item.ac = rd_int();
		item.toac = rd_int();
		rd_ints(item.damage, 2);
		item.level = rd_byte();
		item.ident = rd_byte();
	}
	
	private static void rd_monster(MonsterType mon) {
		mon.hp = rd_int();
		mon.csleep = rd_int();
		mon.cspeed = rd_int();
		mon.mptr = rd_int();
		mon.fy = rd_byte();
		mon.fx = rd_byte();
		mon.cdis = rd_byte();
		mon.ml = (rd_byte() == 1);
		mon.stunned = rd_byte();
		mon.confused = rd_byte();
	}
	
	/* functions called from death.c to implement the score file */
	
	/* set the local fileptr to the scorefile fileptr */
	public static void set_fileptr(java.io.File file) {
		fileptr = file;
	}
	
	public static void wr_highscore(HighScoreType score) {
		/* Save the encryption byte for robustness.  */
		wr_byte(xor_byte);
		
		wr_int(score.points);
		wr_long(score.birth_date);
		wr_int(score.uid);
		wr_int(score.mhp);
		wr_int(score.chp);
		wr_int(score.dun_level);
		wr_int(score.lev);
		wr_int(score.max_dlv);
		wr_int(score.sex);
		wr_int(score.race);
		wr_int(score.Class);
		wr_string(score.name, Player.py.PLAYER_NAME_SIZE);
		wr_string(score.died_from, 25);
	}
	
	public static void rd_highscore(HighScoreType score) {
		/* Read the encryption byte.  */
		xor_byte = rd_byte();
		
		score.points = rd_int();
		score.birth_date = rd_long();
		score.uid = rd_int();
		score.mhp = rd_int();
		score.chp = rd_int();
		score.dun_level = rd_int();
		score.lev = rd_int();
		score.max_dlv = rd_int();
		score.sex = rd_int();
		score.race = rd_int();
		score.Class = rd_int();
		score.name = rd_string(Player.py.PLAYER_NAME_SIZE);
		score.died_from = rd_string(25);
	}
}
