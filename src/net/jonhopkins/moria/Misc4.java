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
	
	private Misc4() { }
	
	/* Add a comment to an object description.		-CJS- */
	public static void enscribeObject() {
		IntPointer item_val = new IntPointer();
		int j;
		String out_val, tmp_str;
		
		if (Treasure.invenCounter > 0 || Treasure.equipCounter > 0) {
			if (Moria1.getItemId(item_val, "Which one? ", 0, Constants.INVEN_ARRAY_SIZE, "", "")) {
				tmp_str = Desc.describeObject(Treasure.inventory[item_val.value()], true);
				out_val = String.format("Inscribing %s", tmp_str);
				IO.printMessage(out_val);
				if (!Treasure.inventory[item_val.value()].inscription.isEmpty()) {
					out_val = String.format("Replace %s New inscription:", Treasure.inventory[item_val.value()].inscription);
				} else {
					out_val = "Inscription: ";
				}
				j = 78 - tmp_str.length();
				if (j > 12) {
					j = 12;
				}
				IO.print(out_val, 0, 0);
				out_val = IO.getString(0, out_val.length(), j);
				if (!out_val.isEmpty()) {
					inscribe(Treasure.inventory[item_val.value()], out_val);
				}
			}
		} else {
			IO.printMessage("You are not carrying anything to inscribe.");
		}
	}
	
	/* Append an additional comment to an object description.	-CJS- */
	public static void addInscription(InvenType i_ptr, int type) {
		i_ptr.identify |= type;
	}

	/* Replace any existing comment in an object description with a new one. CJS*/
	public static void inscribe(InvenType i_ptr, String str) {
		i_ptr.inscription = str;
	}
	
	/* We need to reset the view of things.			-CJS- */
	public static void checkView() {
		int i, j;
		CaveType c_ptr, d_ptr;
		
		c_ptr = Variable.cave[Player.y][Player.x];
		/* Check for new panel		   */
		if (Misc1.getPanel(Player.y, Player.x, false)) {
			Misc1.printMap();
		}
		/* Move the light source		   */
		Moria1.moveLight(Player.y, Player.x, Player.y, Player.x);
		/* A room of light should be lit.	 */
		if (c_ptr.fval == Constants.LIGHT_FLOOR) {
			if ((Player.py.flags.blind < 1) && !c_ptr.permLight) {
				Moria1.lightUpRoom(Player.y, Player.x);
			}
		/* In doorway of light-room?		   */
		} else if (c_ptr.litRoom && (Player.py.flags.blind < 1)) {
			for (i = (Player.y - 1); i <= (Player.y + 1); i++) {
				for (j = (Player.x - 1); j <= (Player.x + 1); j++) {
					d_ptr = Variable.cave[i][j];
					if ((d_ptr.fval == Constants.LIGHT_FLOOR) && !d_ptr.permLight) {
						Moria1.lightUpRoom(i, j);
					}
				}
			}
		}
	}
}
