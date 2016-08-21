/*
 * Misc4.java: misc code for maintaining the dungeon, printing player info
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
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
public class Misc4 {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Moria1 mor1;
	private Player py;
	private Treasure t;
	private Variable var;
	
	private static Misc4 instance;
	private Misc4() { }
	public static Misc4 getInstance() {
		if (instance == null) {
			instance = new Misc4();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		mor1 = Moria1.getInstance();
		py = Player.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Add a comment to an object description.		-CJS- */
	public void scribe_object() {
		IntPointer item_val = new IntPointer();
		int j;
		String out_val, tmp_str;
		
		if (t.inven_ctr > 0 || t.equip_ctr > 0) {
			if (mor1.get_item(item_val, "Which one? ", 0, Constants.INVEN_ARRAY_SIZE, "", "")) {
				tmp_str = desc.objdes(t.inventory[item_val.value()], true);
				out_val = String.format("Inscribing %s", tmp_str);
				io.msg_print(out_val);
				if (!t.inventory[item_val.value()].inscrip.equals("")) {
					out_val = String.format("Replace %s New inscription:", t.inventory[item_val.value()].inscrip);
				} else {
					out_val = "Inscription: ";
				}
				j = 78 - tmp_str.length();
				if (j > 12) {
					j = 12;
				}
				io.prt(out_val, 0, 0);
				if (!(out_val = io.get_string(0, out_val.length(), j)).equals("")) {
					inscribe(t.inventory[item_val.value()], out_val);
				}
			}
		} else {
			io.msg_print("You are not carrying anything to inscribe.");
		}
	}
	
	/* Append an additional comment to an object description.	-CJS- */
	public void add_inscribe(InvenType i_ptr, int type) {
		i_ptr.ident |= type;
	}

	/* Replace any existing comment in an object description with a new one. CJS*/
	public void inscribe(InvenType i_ptr, String str) {
		i_ptr.inscrip = str;
	}
	
	/* We need to reset the view of things.			-CJS- */
	public void check_view() {
		int i, j;
		CaveType c_ptr, d_ptr;
		
		c_ptr = var.cave[py.char_row][py.char_col];
		/* Check for new panel		   */
		if (m1.get_panel(py.char_row, py.char_col, false)) {
			m1.prt_map();
		}
		/* Move the light source		   */
		mor1.move_light(py.char_row, py.char_col, py.char_row, py.char_col);
		/* A room of light should be lit.	 */
		if (c_ptr.fval == Constants.LIGHT_FLOOR) {
			if ((py.py.flags.blind < 1) && !c_ptr.pl) {
				mor1.light_room(py.char_row, py.char_col);
			}
		/* In doorway of light-room?		   */
		} else if (c_ptr.lr && (py.py.flags.blind < 1)) {
			for (i = (py.char_row - 1); i <= (py.char_row + 1); i++) {
				for (j = (py.char_col - 1); j <= (py.char_col + 1); j++) {
					d_ptr = var.cave[i][j];
					if ((d_ptr.fval == Constants.LIGHT_FLOOR) && !d_ptr.pl) {
						mor1.light_room(i, j);
					}
				}
			}
		}
	}
}
