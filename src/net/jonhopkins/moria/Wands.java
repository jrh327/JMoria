/*
 * Wands.java: wand code
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

public class Wands {
	
	private Wands() { }
	
	/* Wands for the aiming.				*/
	public static void aim() {
		IntPointer i = new IntPointer();
		int l;
		boolean ident;
		IntPointer item_val = new IntPointer(), dir = new IntPointer();
		IntPointer j = new IntPointer(), k = new IntPointer();
		int chance;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		
		Variable.freeTurnFlag = true;
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
		} else if (!Misc3.findRange(Constants.TV_WAND, Constants.TV_NEVER, j, k)) {
			IO.printMessage("You are not carrying any wands.");
		} else if (Moria1.getItemId(item_val, "Aim which wand?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.freeTurnFlag = false;
			if (Moria1.getDirection("", dir)) {
				if (Player.py.flags.confused > 0) {
					IO.printMessage("You are confused.");
					do {
						dir.value(Misc1.randomInt(9));
					} while (dir.value() == 5);
				}
				ident = false;
				m_ptr = Player.py.misc;
				chance = m_ptr.savingThrow + Misc3.adjustStat(Constants.A_INT) - i_ptr.level + (Player.classLevelAdjust[m_ptr.playerClass][Constants.CLA_DEVICE] * m_ptr.level / 3);
				if (Player.py.flags.confused > 0) {
					chance = chance / 2;
				}
				if ((chance < Constants.USE_DEVICE) && (Misc1.randomInt(Constants.USE_DEVICE - chance + 1) == 1)) {
					chance = Constants.USE_DEVICE; /* Give everyone a slight chance */
				}
				if (chance <= 0)	chance = 1;
				if (Misc1.randomInt(chance) < Constants.USE_DEVICE) {
					IO.printMessage("You failed to use the wand properly.");
				} else if (i_ptr.misc > 0) {
					i.value(i_ptr.flags);
					i_ptr.misc--;
					while (i.value() != 0) {
						j.value(Misc1.firstBitPos(i) + 1);
						k.value(Player.y);
						l = Player.x;
						/* Wands			 */
						switch(j.value())
						{
						case 1:
							IO.printMessage("A line of blue shimmering light appears.");
							Spells.lightLine(dir.value(), Player.y, Player.x);
							ident = true;
							break;
						case 2:
							Spells.fireBolt(Constants.GF_LIGHTNING, dir.value(), k.value(), l, Misc1.damageRoll(4, 8), Player.spellNames[8]);
							ident = true;
							break;
						case 3:
							Spells.fireBolt(Constants.GF_FROST, dir.value(), k.value(), l, Misc1.damageRoll(6, 8), Player.spellNames[14]);
							ident = true;
							break;
						case 4:
							Spells.fireBolt(Constants.GF_FIRE, dir.value(), k.value(), l, Misc1.damageRoll(9, 8), Player.spellNames[22]);
							ident = true;
							break;
						case 5:
							ident = Spells.transformWallToMud(dir.value(), k.value(), l);
							break;
						case 6:
							ident = Spells.polymorphMonster(dir.value(), k.value(), l);
							break;
						case 7:
							ident = Spells.changeMonsterHitpoints(dir.value(), k.value(), l, -Misc1.damageRoll(4, 6));
							break;
						case 8:
							ident = Spells.speedMonster(dir.value(), k.value(), l, 1);
							break;
						case 9:
							ident = Spells.speedMonster(dir.value(), k.value(), l, -1);
							break;
						case 10:
							ident = Spells.confuseMonster(dir.value(), k.value(), l);
							break;
						case 11:
							ident = Spells.sleepMonster(dir.value(), k.value(), l);
							break;
						case 12:
							ident = Spells.drainLife(dir.value(), k.value(), l);
							break;
						case 13:
							ident = Spells.destroyTrapsAndDoors(dir.value(), k.value(), l);
							break;
						case 14:
							Spells.fireBolt(Constants.GF_MAGIC_MISSILE, dir.value(), k.value(), l, Misc1.damageRoll(2, 6), Player.spellNames[0]);
							ident = true;
							break;
						case 15:
							ident = Spells.buildWall(dir.value(), k.value(), l);
							break;
						case 16:
							ident = Spells.cloneMonster(dir.value(), k.value(), l);
							break;
						case 17:
							ident = Spells.teleportMonsters(dir.value(), k.value(), l);
							break;
						case 18:
							ident = Spells.disarmAll(dir.value(), k.value(), l);
							break;
						case 19:
							Spells.fireBall(Constants.GF_LIGHTNING, dir.value(), k.value(), l, 32, "Lightning Ball");
							ident = true;
							break;
						case 20:
							Spells.fireBall(Constants.GF_FROST, dir.value(), k.value(), l, 48, "Cold Ball");
							ident = true;
							break;
						case 21:
							Spells.fireBall(Constants.GF_FIRE, dir.value(), k.value(), l, 72, Player.spellNames[28]);
							ident = true;
							break;
						case 22:
							Spells.fireBall(Constants.GF_POISON_GAS, dir.value(), k.value(), l, 12, Player.spellNames[6]);
							ident = true;
							break;
						case 23:
							Spells.fireBall(Constants.GF_ACID, dir.value(), k.value(), l, 60, "Acid Ball");
							ident = true;
							break;
						case 24:
							i.value(1 << (Misc1.randomInt(23) - 1));
							break;
						default:
							IO.printMessage("Internal error in wands()");
							break;
						}
						/* End of Wands.		    */
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
					IO.printMessage("The wand has no charges left.");
					if (!Desc.arePlussesKnownByPlayer(i_ptr)) {
						Misc4.addInscription(i_ptr, Constants.ID_EMPTY);
					}
				}
			}
		}
	}
}
