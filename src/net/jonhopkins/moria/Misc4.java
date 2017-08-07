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
	
	/**
	 * Add a comment to an object description. -CJS-
	 */
	public static void enscribeObject() {
		if (Treasure.invenCounter == 0 && Treasure.equipCounter == 0) {
			IO.printMessage("You are not carrying anything to inscribe.");
			return;
		}
		
		IntPointer itemIndex = new IntPointer();
		if (Moria1.getItemId(itemIndex, "Which one? ", 0, Constants.INVEN_ARRAY_SIZE, null, "")) {
			String itemDesc = Desc.describeObject(Treasure.inventory[itemIndex.value()], true);
			String msgInscribing = String.format("Inscribing %s", itemDesc);
			IO.printMessage(msgInscribing);
			
			String msgInscription;
			if (!Treasure.inventory[itemIndex.value()].inscription.isEmpty()) {
				msgInscription = String.format(
						"Replace %s New inscription: ",
						Treasure.inventory[itemIndex.value()].inscription);
			} else {
				msgInscription = "Inscription: ";
			}
			IO.print(msgInscription, 0, 0);
			
			int lenInscription = 78 - itemDesc.length();
			if (lenInscription > 12) {
				lenInscription = 12;
			}
			String inscription = IO.getString(0, msgInscription.length(), lenInscription);
			if (!inscription.isEmpty()) {
				inscribe(Treasure.inventory[itemIndex.value()], inscription);
			}
		}
	}
	
	/**
	 * Append an additional comment to an object description. -CJS-
	 * 
	 * @param item The item to inscribe
	 * @param type
	 */
	public static void addInscription(InvenType item, int type) {
		item.identify |= type;
	}
	
	/**
	 * Replace any existing comment in an object description with a new one. -CJS-
	 * 
	 * @param item The item to inscribe
	 * @param str The inscription
	 */
	public static void inscribe(InvenType item, String str) {
		item.inscription = str;
	}
	
	/**
	 * We need to reset the view of things. -CJS-
	 */
	public static void checkView() {
		CaveType playerPos = Variable.cave[Player.y][Player.x];
		// Check for new panel
		if (Misc1.getPanel(Player.y, Player.x, false)) {
			Misc1.printMap();
		}
		
		// Move the light source
		Moria1.moveLight(Player.y, Player.x, Player.y, Player.x);
		
		// A room of light should be lit.
		if (playerPos.fval == Constants.LIGHT_FLOOR) {
			if ((Player.py.flags.blind < 1) && !playerPos.permLight) {
				Moria1.lightUpRoom(Player.y, Player.x);
			}
		
		// In doorway of light-room?
		} else if (playerPos.litRoom && (Player.py.flags.blind < 1)) {
			for (int i = (Player.y - 1); i <= (Player.y + 1); i++) {
				for (int j = (Player.x - 1); j <= (Player.x + 1); j++) {
					CaveType cavePos = Variable.cave[i][j];
					if ((cavePos.fval == Constants.LIGHT_FLOOR) && !cavePos.permLight) {
						Moria1.lightUpRoom(i, j);
					}
				}
			}
		}
	}
}
