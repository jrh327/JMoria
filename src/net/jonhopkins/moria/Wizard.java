/*
 * Wizard.java: Version history and info, and wizard mode debugging aids.
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

import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.CharPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Wizard {
	
	private Wizard() { }
	
	/* Light up the dungeon					-RAK-	*/
	public static void wizard_light() {
		CaveType c_ptr;
		int k, l, i, j;
		boolean flag;
		
		if (Variable.cave[Player.char_row][Player.char_col].pl) {
			flag = false;
		} else {
			flag = true;
		}
		for (i = 0; i < Variable.cur_height; i++) {
			for (j = 0; j < Variable.cur_width; j++) {
				if (Variable.cave[i][j].fval <= Constants.MAX_CAVE_FLOOR) {
					for (k = i-1; k <= i+1; k++) {
						for (l = j-1; l <= j+1; l++) {
							c_ptr = Variable.cave[k][l];
							c_ptr.pl = flag;
							if (!flag) {
								c_ptr.fm = false;
							}
						}
					}
				}
			}
		}
		Misc1.prt_map();
	}
	
	/* Wizard routine for gaining on stats			-RAK-	*/
	public static void change_character() {
		int tmp_val;
		int[] a_ptr;
		String tmp_str;
		PlayerMisc m_ptr;
		
		a_ptr = Player.py.stats.max_stat;
		IO.prt("(3 - 118) Strength     = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_STR] = tmp_val;
				Misc3.res_stat(Constants.A_STR);
			}
		} else {
			return;
		}
		
		IO.prt("(3 - 118) Intelligence = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_INT] = tmp_val;
				Misc3.res_stat(Constants.A_INT);
			}
		} else {
			return;
		}
		
		IO.prt("(3 - 118) Wisdom       = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_WIS] = tmp_val;
				Misc3.res_stat(Constants.A_WIS);
			}
		} else {
			return;
		}
		
		IO.prt("(3 - 118) Dexterity    = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_DEX] = tmp_val;
				Misc3.res_stat(Constants.A_DEX);
			}
		} else {
			return;
		}
		
		IO.prt("(3 - 118) Constitution = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_CON] = tmp_val;
				Misc3.res_stat(Constants.A_CON);
			}
		} else {
			return;
		}
		
		IO.prt("(3 - 118) Charisma     = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_CHR] = tmp_val;
				Misc3.res_stat(Constants.A_CHR);
			}
		} else {
			return;
		}
		
		m_ptr = Player.py.misc;
		IO.prt("(1 - 32767) Hit points = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 5)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 0) && (tmp_val <= Constants.MAX_SHORT)) {
				m_ptr.mhp  = tmp_val;
				m_ptr.chp  = tmp_val;
				m_ptr.chp_frac = 0;
				Misc3.prt_mhp();
				Misc3.prt_chp();
			}
		} else {
			return;
		}
		
		IO.prt("(0 - 32767) Mana       = ", 0, 0);
		if ((tmp_str = IO.get_string(0, 25, 5)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > -1) && (tmp_val <= Constants.MAX_SHORT) && (!tmp_str.equals(""))) {
				m_ptr.mana  = tmp_val;
				m_ptr.cmana = tmp_val;
				m_ptr.cmana_frac = 0;
				Misc3.prt_cmana();
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  Gold = ", m_ptr.au);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 7)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
				tmp_val = 0;
			}
			if (tmp_val > -1 && (!tmp_str.equals(""))) {
				m_ptr.au = tmp_val;
				Misc3.prt_gold();
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Searching = ", m_ptr.srh);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > -1) && (tmp_val < 201) && (!tmp_str.equals(""))) {
				m_ptr.srh  = tmp_val;
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  (-1-18) Stealth = ", m_ptr.stl);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > -2) && (tmp_val < 19) && (!tmp_str.equals(""))) {
				m_ptr.stl  = tmp_val;
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Disarming = ", m_ptr.disarm);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > -1) && (tmp_val < 201) && (!tmp_str.equals(""))) {
				m_ptr.disarm = tmp_val;
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  (0-100) Save = ", m_ptr.save);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > -1) && (tmp_val < 201) && (!tmp_str.equals(""))) {
				m_ptr.save = tmp_val;
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Base to hit = ", m_ptr.bth);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > -1) && (tmp_val < 201) && (!tmp_str.equals(""))) {
				m_ptr.bth = tmp_val;
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Bows/Throwing = ", m_ptr.bthb);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > -1) && (tmp_val < 201) && (!tmp_str.equals(""))) {
				m_ptr.bthb = tmp_val;
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  Weight = ", m_ptr.wt);
		tmp_val = tmp_str.length();
		IO.prt(tmp_str, 0, 0);
		if ((tmp_str = IO.get_string(0, tmp_val, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if (tmp_val > -1 && (!tmp_str.equals(""))) {
				m_ptr.wt = tmp_val;
			}
		} else {
			return;
		}
		
		CharPointer tmp_ch = new CharPointer();
		while(IO.get_com("Alter speed? (+/-)", tmp_ch)) {
			if (tmp_ch.value() == '+') {
				Moria1.change_speed(-1);
			} else if (tmp_ch.value() == '-') {
				Moria1.change_speed(1);
			} else {
				break;
			}
			Misc3.prt_speed();
		}
	}
	
	/* Wizard routine for creating objects			-RAK-	*/
	public static void wizard_create() {
		int tmp_val;
		long tmp_lval;
		String tmp_str;
		InvenType i_ptr;
		InvenType forge = new InvenType();
		CaveType c_ptr;
		//char[] pattern = new char[4];
		
		IO.msg_print("Warning: This routine can cause a fatal error.");
		i_ptr = forge;
		i_ptr.index = Constants.OBJ_WIZARD;
		i_ptr.name2 = 0;
		Misc4.inscribe(i_ptr, "wizard item");
		i_ptr.ident = Constants.ID_KNOWN2|Constants.ID_STOREBOUGHT;
		
		IO.prt("Tval   : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.tval = tmp_val;
		
		IO.prt("Tchar  : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 1)).length() == 0) {
			return;
		}
		i_ptr.tchar = tmp_str.charAt(0);
		
		IO.prt("Subval : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 5)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.subval = tmp_val;
		
		IO.prt("Weight : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 5)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.weight = tmp_val;
		
		IO.prt("Number : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 5)).length() == 0)
			return;
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.number = tmp_val;
		
		IO.prt("Damage (dice): ", 0, 0);
		if ((tmp_str = IO.get_string(0, 15, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.damage[0] = tmp_val;
		
		IO.prt("Damage (sides): ", 0, 0);
		if ((tmp_str = IO.get_string(0, 16, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.damage[1] = tmp_val;
		
		IO.prt("+To hit: ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.tohit = tmp_val;
		
		IO.prt("+To dam: ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.todam = tmp_val;
		
		IO.prt("AC     : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.ac = tmp_val;
		
		IO.prt("+To AC : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.toac = tmp_val;
		
		IO.prt("P1     : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 5)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.p1 = tmp_val;
		
		IO.prt("Flags (In HEX): ", 0, 0);
		if ((tmp_str = IO.get_string(0, 16, 8)).length() == 0) {
			return;
		}
		/* can't be constant string, this causes problems with the GCC compiler
	 	 * and some scanf routines */
		
		try {
			tmp_lval = Long.parseLong(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to a long in Wizard.wizard_create()");
			tmp_lval = 0;
		}
		//finds a long in tmp_str and places it in tmp_lval
		//pattern = "%lx";
		//sscanf(tmp_str, pattern, tmp_lval);
		i_ptr.flags = tmp_lval;
		
		IO.prt("Cost : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 9, 8)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.cost = tmp_val;
		
		IO.prt("Level : ", 0, 0);
		if ((tmp_str = IO.get_string(0, 10, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.level = tmp_val;
		
		if (IO.get_check("Allocate?")) {
			/* delete object first if any, before call popt */
			c_ptr = Variable.cave[Player.char_row][Player.char_col];
			if (c_ptr.tptr != 0) {
				Moria3.delete_object(Player.char_row, Player.char_col);
			}
			
			tmp_val = Misc1.popt();
			forge.copyInto(Treasure.t_list[tmp_val]);
			c_ptr.tptr = tmp_val;
			IO.msg_print("Allocated.");
		} else {
			IO.msg_print("Aborted.");
		}
	}
}
