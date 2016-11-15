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
	public static void wizardLight() {
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
					for (k = i - 1; k <= i + 1; k++) {
						for (l = j - 1; l <= j + 1; l++) {
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
		Misc1.printMap();
	}
	
	/* Wizard routine for gaining on stats			-RAK-	*/
	public static void changeCharacter() {
		int tmp_val;
		int[] a_ptr;
		String tmp_str;
		PlayerMisc m_ptr;
		
		a_ptr = Player.py.stats.max_stat;
		IO.print("(3 - 118) Strength     = ", 0, 0);
		tmp_str = IO.getString(0, 25, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > 2) && (tmp_val < 119)) {
			a_ptr[Constants.A_STR] = tmp_val;
			Misc3.restoreStat(Constants.A_STR);
		}
		
		IO.print("(3 - 118) Intelligence = ", 0, 0);
		tmp_str = IO.getString(0, 25, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > 2) && (tmp_val < 119)) {
			a_ptr[Constants.A_INT] = tmp_val;
			Misc3.restoreStat(Constants.A_INT);
		}
		
		IO.print("(3 - 118) Wisdom       = ", 0, 0);
		tmp_str = IO.getString(0, 25, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > 2) && (tmp_val < 119)) {
			a_ptr[Constants.A_WIS] = tmp_val;
			Misc3.restoreStat(Constants.A_WIS);
		}
		
		IO.print("(3 - 118) Dexterity    = ", 0, 0);
		tmp_str = IO.getString(0, 25, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > 2) && (tmp_val < 119)) {
			a_ptr[Constants.A_DEX] = tmp_val;
			Misc3.restoreStat(Constants.A_DEX);
		}
		
		IO.print("(3 - 118) Constitution = ", 0, 0);
		tmp_str = IO.getString(0, 25, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > 2) && (tmp_val < 119)) {
			a_ptr[Constants.A_CON] = tmp_val;
			Misc3.restoreStat(Constants.A_CON);
		}
		
		IO.print("(3 - 118) Charisma     = ", 0, 0);
		tmp_str = IO.getString(0, 25, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > 2) && (tmp_val < 119)) {
			a_ptr[Constants.A_CHR] = tmp_val;
			Misc3.restoreStat(Constants.A_CHR);
		}
		
		m_ptr = Player.py.misc;
		IO.print("(1 - 32767) Hit points = ", 0, 0);
		tmp_str = IO.getString(0, 25, 5);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > 0) && (tmp_val <= Constants.MAX_SHORT)) {
			m_ptr.mhp  = tmp_val;
			m_ptr.chp  = tmp_val;
			m_ptr.chp_frac = 0;
			Misc3.printMaxHitpoints();
			Misc3.printCurrentHitpoints();
		}
		
		IO.print("(0 - 32767) Mana       = ", 0, 0);
		tmp_str = IO.getString(0, 25, 5);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > -1) && (tmp_val <= Constants.MAX_SHORT)) {
			m_ptr.mana  = tmp_val;
			m_ptr.cmana = tmp_val;
			m_ptr.cmana_frac = 0;
			Misc3.printCurrentMana();
		}
		
		tmp_str = String.format("Current=%d  Gold = ", m_ptr.au);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 7);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if (tmp_val > -1) {
			m_ptr.au = tmp_val;
			Misc3.printGold();
		}
		
		tmp_str = String.format("Current=%d  (0-200) Searching = ", m_ptr.srh);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > -1) && (tmp_val < 201)) {
			m_ptr.srh  = tmp_val;
		}
		
		tmp_str = String.format("Current=%d  (-1-18) Stealth = ", m_ptr.stl);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > -2) && (tmp_val < 19)) {
			m_ptr.stl  = tmp_val;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Disarming = ", m_ptr.disarm);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > -1) && (tmp_val < 201)) {
			m_ptr.disarm = tmp_val;
		}
		
		tmp_str = String.format("Current=%d  (0-100) Save = ", m_ptr.save);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > -1) && (tmp_val < 201)) {
			m_ptr.save = tmp_val;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Base to hit = ", m_ptr.bth);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > -1) && (tmp_val < 201)) {
			m_ptr.bth = tmp_val;
		}
		
		tmp_str = String.format("Current=%d  (0-200) Bows/Throwing = ", m_ptr.bthb);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if ((tmp_val > -1) && (tmp_val < 201)) {
			m_ptr.bthb = tmp_val;
		}
		
		tmp_str = String.format("Current=%d  Weight = ", m_ptr.wt);
		tmp_val = tmp_str.length();
		IO.print(tmp_str, 0, 0);
		tmp_str = IO.getString(0, tmp_val, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.change_character()");
			e.printStackTrace();
			tmp_val = 0;
		}
		if (tmp_val > -1) {
			m_ptr.wt = tmp_val;
		}
		
		CharPointer tmp_ch = new CharPointer();
		while(IO.getCommand("Alter speed? (+/-)", tmp_ch)) {
			if (tmp_ch.value() == '+') {
				Moria1.changeSpeed(-1);
			} else if (tmp_ch.value() == '-') {
				Moria1.changeSpeed(1);
			} else {
				break;
			}
			Misc3.printSpeed();
		}
	}
	
	/* Wizard routine for creating objects			-RAK-	*/
	public static void wizardCreate() {
		int tmp_val;
		String tmp_str;
		InvenType i_ptr;
		InvenType forge = new InvenType();
		CaveType c_ptr;
		//char[] pattern = new char[4];
		
		IO.printMessage("Warning: This routine can cause a fatal error.");
		i_ptr = forge;
		i_ptr.index = Constants.OBJ_WIZARD;
		i_ptr.name2 = 0;
		Misc4.inscribe(i_ptr, "wizard item");
		i_ptr.ident = Constants.ID_KNOWN2|Constants.ID_STOREBOUGHT;
		
		IO.print("Tval   : ", 0, 0);
		tmp_str = IO.getString(0, 9, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.tval = tmp_val;
		
		IO.print("Tchar  : ", 0, 0);
		tmp_str = IO.getString(0, 9, 1);
		if (tmp_str.isEmpty()) {
			return;
		}
		i_ptr.tchar = tmp_str.charAt(0);
		
		IO.print("Subval : ", 0, 0);
		tmp_str = IO.getString(0, 9, 5);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.subval = tmp_val;
		
		IO.print("Weight : ", 0, 0);
		tmp_str = IO.getString(0, 9, 5);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.weight = tmp_val;
		
		IO.print("Number : ", 0, 0);
		tmp_str = IO.getString(0, 9, 5);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.number = tmp_val;
		
		IO.print("Damage (dice): ", 0, 0);
		tmp_str = IO.getString(0, 15, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.damage[0] = tmp_val;
		
		IO.print("Damage (sides): ", 0, 0);
		tmp_str = IO.getString(0, 16, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.damage[1] = tmp_val;
		
		IO.print("+To hit: ", 0, 0);
		tmp_str = IO.getString(0, 9, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.tohit = tmp_val;
		
		IO.print("+To dam: ", 0, 0);
		tmp_str = IO.getString(0, 9, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.todam = tmp_val;
		
		IO.print("AC     : ", 0, 0);
		tmp_str = IO.getString(0, 9, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.ac = tmp_val;
		
		IO.print("+To AC : ", 0, 0);
		tmp_str = IO.getString(0, 9, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.toac = tmp_val;
		
		IO.print("P1     : ", 0, 0);
		tmp_str = IO.getString(0, 9, 5);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.p1 = tmp_val;
		
		IO.print("Flags (In HEX): ", 0, 0);
		tmp_str = IO.getString(0, 16, 8);
		if (tmp_str.isEmpty()) {
			return;
		}
		
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		//finds a long in tmp_str and places it in tmp_lval
		//pattern = "%lx";
		//sscanf(tmp_str, pattern, tmp_lval);
		i_ptr.flags = tmp_val;
		
		IO.print("Cost : ", 0, 0);
		tmp_str = IO.getString(0, 9, 8);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.cost = tmp_val;
		
		IO.print("Level : ", 0, 0);
		tmp_str = IO.getString(0, 10, 3);
		if (tmp_str.isEmpty()) {
			return;
		}
		try {
			tmp_val = Integer.parseInt(tmp_str);
		} catch (NumberFormatException e) {
			System.err.println("Could not convert tmp_str to an integer in Wizard.wizard_create()");
			e.printStackTrace();
			tmp_val = 0;
		}
		i_ptr.level = tmp_val;
		
		if (IO.getCheck("Allocate?")) {
			/* delete object first if any, before call popt */
			c_ptr = Variable.cave[Player.char_row][Player.char_col];
			if (c_ptr.tptr != 0) {
				Moria3.deleteObject(Player.char_row, Player.char_col);
			}
			
			tmp_val = Misc1.popTreasure();
			forge.copyInto(Treasure.t_list[tmp_val]);
			c_ptr.tptr = tmp_val;
			IO.printMessage("Allocated.");
		} else {
			IO.printMessage("Aborted.");
		}
	}
}
