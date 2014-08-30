/**
 * Wizard.java: Version history and info, and wizard mode debugging aids.
 * <p>
 * Copyright (c) 2007 James E. Wilson, Robert A. Koeneke
 * <br>
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

package net.jonhopkins.moria;

import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.CharPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Wizard {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Misc4 m4;
	private Moria1 mor1;
	private Moria3 mor3;
	private Player py;
	private Treasure t;
	private Variable var;
	
	private static Wizard instance;
	private Wizard() { }
	public static Wizard getInstance() {
		if (instance == null) {
			instance = new Wizard();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		m4 = Misc4.getInstance();
		mor1 = Moria1.getInstance();
		mor3 = Moria3.getInstance();
		py = Player.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Light up the dungeon					-RAK-	*/
	public void wizard_light() {
		CaveType c_ptr;
		int k, l, i, j;
		boolean flag;
		
		if (var.cave[py.char_row][py.char_col].pl) {
			flag = false;
		} else {
			flag = true;
		}
		for (i = 0; i < var.cur_height; i++) {
			for (j = 0; j < var.cur_width; j++) {
				if (var.cave[i][j].fval <= Constants.MAX_CAVE_FLOOR) {
					for (k = i-1; k <= i+1; k++) {
						for (l = j-1; l <= j+1; l++) {
							c_ptr = var.cave[k][l];
							c_ptr.pl = flag;
							if (!flag) {
								c_ptr.fm = false;
							}
						}
					}
				}
			}
		}
		m1.prt_map();
	}
	
	/* Wizard routine for gaining on stats			-RAK-	*/
	public void change_character() {
		int tmp_val;
		int[] a_ptr;
		String tmp_str;
		PlayerMisc m_ptr;
		
		a_ptr = py.py.stats.max_stat;
		io.prt("(3 - 118) Strength     = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_STR] = tmp_val;
				m3.res_stat(Constants.A_STR);
			}
		} else {
			return;
		}
		
		io.prt("(3 - 118) Intelligence = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_INT] = tmp_val;
				m3.res_stat(Constants.A_INT);
			}
		} else {
			return;
		}
		
		io.prt("(3 - 118) Wisdom       = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_WIS] = tmp_val;
				m3.res_stat(Constants.A_WIS);
			}
		} else {
			return;
		}
		
		io.prt("(3 - 118) Dexterity    = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_DEX] = tmp_val;
				m3.res_stat(Constants.A_DEX);
			}
		} else {
			return;
		}
		
		io.prt("(3 - 118) Constitution = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_CON] = tmp_val;
				m3.res_stat(Constants.A_CON);
			}
		} else {
			return;
		}
		
		io.prt("(3 - 118) Charisma     = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 3)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
				tmp_val = 0;
			}
			if ((tmp_val > 2) && (tmp_val < 119)) {
				a_ptr[Constants.A_CHR] = tmp_val;
				m3.res_stat(Constants.A_CHR);
			}
		} else {
			return;
		}
		
		m_ptr = py.py.misc;
		io.prt("(1 - 32767) Hit points = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 5)).length() > 0) {
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
				m3.prt_mhp();
				m3.prt_chp();
			}
		} else {
			return;
		}
		
		io.prt("(0 - 32767) Mana       = ", 0, 0);
		if ((tmp_str = io.get_string(0, 25, 5)).length() > 0) {
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
				m3.prt_cmana();
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  Gold = ", m_ptr.au);
		tmp_val = tmp_str.length();
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 7)).length() > 0) {
			try {
				tmp_val = Integer.parseInt(tmp_str);
			} catch (NumberFormatException e) {
				System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
				tmp_val = 0;
			}
			if (tmp_val > -1 && (!tmp_str.equals(""))) {
				m_ptr.au = tmp_val;
				m3.prt_gold();
			}
		} else {
			return;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Searching = ", m_ptr.srh);
		tmp_val = tmp_str.length();
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 3)).length() > 0) {
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
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 3)).length() > 0) {
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
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 3)).length() > 0) {
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
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 3)).length() > 0) {
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
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 3)).length() > 0) {
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
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 3)).length() > 0) {
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
		io.prt(tmp_str, 0, 0);
		if ((tmp_str = io.get_string(0, tmp_val, 3)).length() > 0) {
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
		while(io.get_com("Alter speed? (+/-)", tmp_ch)) {
			if (tmp_ch.value() == '+') {
				mor1.change_speed(-1);
			} else if (tmp_ch.value() == '-') {
				mor1.change_speed(1);
			} else {
				break;
			}
			m3.prt_speed();
		}
	}
	
	/* Wizard routine for creating objects			-RAK-	*/
	public void wizard_create() {
		int tmp_val;
		long tmp_lval;
		String tmp_str;
		InvenType i_ptr;
		InvenType forge = new InvenType();
		CaveType c_ptr;
		//char[] pattern = new char[4];
		
		io.msg_print("Warning: This routine can cause a fatal error.");
		i_ptr = forge;
		i_ptr.index = Constants.OBJ_WIZARD;
		i_ptr.name2 = 0;
		m4.inscribe(i_ptr, "wizard item");
		i_ptr.ident = Constants.ID_KNOWN2|Constants.ID_STOREBOUGHT;
		
		io.prt("Tval   : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.tval = tmp_val;
		
		io.prt("Tchar  : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 1)).length() == 0) {
			return;
		}
		i_ptr.tchar = tmp_str.charAt(0);
		
		io.prt("Subval : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 5)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.subval = tmp_val;
		
		io.prt("Weight : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 5)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.weight = tmp_val;
		
		io.prt("Number : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 5)).length() == 0)
			return;
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.number = tmp_val;
		
		io.prt("Damage (dice): ", 0, 0);
		if ((tmp_str = io.get_string(0, 15, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.damage[0] = tmp_val;
		
		io.prt("Damage (sides): ", 0, 0);
		if ((tmp_str = io.get_string(0, 16, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.damage[1] = tmp_val;
		
		io.prt("+To hit: ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.tohit = tmp_val;
		
		io.prt("+To dam: ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.todam = tmp_val;
		
		io.prt("AC     : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.ac = tmp_val;
		
		io.prt("+To AC : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.toac = tmp_val;
		
		io.prt("P1     : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 5)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.p1 = tmp_val;
		
		io.prt("Flags (In HEX): ", 0, 0);
		if ((tmp_str = io.get_string(0, 16, 8)).length() == 0) {
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
		
		io.prt("Cost : ", 0, 0);
		if ((tmp_str = io.get_string(0, 9, 8)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.cost = tmp_val;
		
		io.prt("Level : ", 0, 0);
		if ((tmp_str = io.get_string(0, 10, 3)).length() == 0) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			tmp_val = 0;
		}
		i_ptr.level = tmp_val;
		
		if (io.get_check("Allocate?")) {
			/* delete object first if any, before call popt */
			c_ptr = var.cave[py.char_row][py.char_col];
			if (c_ptr.tptr != 0) {
				mor3.delete_object(py.char_row, py.char_col);
			}
			
			tmp_val = m1.popt();
			desc.invdeepcopy(t.t_list[tmp_val], forge);
			c_ptr.tptr = tmp_val;
			io.msg_print("Allocated.");
		} else {
			io.msg_print("Aborted.");
		}
	}
}
