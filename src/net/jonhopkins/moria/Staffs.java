/*
 * Staffs.java: staff code
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

import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Staffs {
	
	private Staffs() { }
	
	/* Use a staff.					-RAK-	*/
	public static void use() {
		IntPointer i = new IntPointer();
		IntPointer j = new IntPointer(), k = new IntPointer();
		int chance;
		IntPointer y, x;
		IntPointer item_val = new IntPointer();
		boolean ident;
		PlayerMisc m_ptr;
		InvenType i_ptr;
		
		Variable.freeTurnFlag = true;
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
		} else if (!Misc3.findRange(Constants.TV_STAFF, Constants.TV_NEVER, j, k)) {
			IO.printMessage("You are not carrying any staffs.");
		} else if (Moria1.getItemId(item_val, "Use which staff?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.freeTurnFlag = false;
			m_ptr = Player.py.misc;
			chance = m_ptr.savingThrow + Misc3.adjustStat(Constants.A_INT) - i_ptr.level - 5 + (Player.classLevelAdjust[m_ptr.playerClass][Constants.CLA_DEVICE] * m_ptr.level / 3);
			if (Player.py.flags.confused > 0) {
				chance = chance / 2;
			}
			if ((chance < Constants.USE_DEVICE) && (Misc1.randomInt(Constants.USE_DEVICE - chance + 1) == 1)) {
				chance = Constants.USE_DEVICE; /* Give everyone a slight chance */
			}
			if (chance <= 0)	chance = 1;
			if (Misc1.randomInt(chance) < Constants.USE_DEVICE) {
				IO.printMessage("You failed to use the staff properly.");
			} else if (i_ptr.misc > 0) {
				i.value(i_ptr.flags);
				ident = false;
				(i_ptr.misc)--;
				while (i.value() != 0) {
					j.value(Misc1.firstBitPos(i) + 1);
					/* Staffs.				*/
					switch(j.value())
					{
					case 1:
						ident = Spells.lightArea(Player.y, Player.x);
						break;
					case 2:
						ident = Spells.detectSecretDoors();
						break;
					case 3:
						ident = Spells.detectTrap();
						break;
					case 4:
						ident = Spells.detectTreasure();
						break;
					case 5:
						ident = Spells.detectObject();
						break;
					case 6:
						Misc3.teleport(100);
						ident = true;
						break;
					case 7:
						ident = true;
						Spells.earthquake();
						break;
					case 8:
						ident = false;
						for (int k1 = 0; k1 < Misc1.randomInt(4); k1++) {
							y = new IntPointer(Player.y);
							x = new IntPointer(Player.x);
							ident |= Misc1.summonMonster(y, x, false);
						}
						break;
					case 10:
						ident = true;
						Spells.destroyArea(Player.y, Player.x);
						break;
					case 11:
						ident = true;
						Spells.starLight(Player.y, Player.x);
						break;
					case 12:
						ident = Spells.speedMonsters(1);
						break;
					case 13:
						ident = Spells.speedMonsters(-1);
						break;
					case 14:
						ident = Spells.sleepMonsters();
						break;
					case 15:
						ident = Spells.changePlayerHitpoints(Misc1.randomInt(8));
						break;
					case 16:
						ident = Spells.detectInvisibleCreatures();
						break;
					case 17:
						if (Player.py.flags.fast == 0) {
							ident = true;
						}
						Player.py.flags.fast += Misc1.randomInt(30) + 15;
						break;
					case 18:
						if (Player.py.flags.slow == 0) {
							ident = true;
						}
						Player.py.flags.slow += Misc1.randomInt(30) + 15;
						break;
					case 19:
						ident = Spells.massPolymorph();
						break;
					case 20:
						if (Spells.removeCurse()) {
							if (Player.py.flags.blind < 1) {
								IO.printMessage("The staff glows blue for a moment..");
							}
							ident = true;
						}
						break;
					case 21:
						ident = Spells.detectEvil();
						break;
					case 22:
						if (Spells.cureBlindness()
								|| Spells.curePoison()
								|| Spells.cureConfusion()) {
							ident = true;
						}
						break;
					case 23:
						ident = Spells.dispelCreature(Constants.CD_EVIL, 60);
						break;
					case 25:
						ident = Spells.unlightArea(Player.y, Player.x);
						break;
					case 32:
						/* store bought flag */
						break;
					default:
						IO.printMessage("Internal error in staffs()");
						break;
					}
					/* End of staff actions.		*/
				}
				if (ident) {
					if (!Desc.isKnownByPlayer(i_ptr)) {
						m_ptr = Player.py.misc;
						/* round half-way case up */
						m_ptr.currExp += (i_ptr.level + (m_ptr.level >> 1)) / m_ptr.level;
						Misc3.printExperience();
						
						Desc.identify(item_val);
						i_ptr = Treasure.inventory[item_val.value()];
					}
				} else if (!Desc.isKnownByPlayer(i_ptr)) {
					Desc.sample(i_ptr);
				}
				Desc.describeCharges(item_val.value());
			} else {
				IO.printMessage("The staff has no charges left.");
				if (!Desc.arePlussesKnownByPlayer(i_ptr)) {
					Misc4.addInscription(i_ptr, Constants.ID_EMPTY);
				}
			}
		}
	}
}
