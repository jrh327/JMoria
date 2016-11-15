/*
 * Eat.java: food code
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

import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Eat {
	
	private Eat() { }
	
	/* Eat some food.					-RAK-	*/
	public static void eat() {
		IntPointer i = new IntPointer();
		IntPointer j = new IntPointer(), k = new IntPointer(), item_val = new IntPointer();
		boolean ident;
		PlayerFlags f_ptr;
		PlayerMisc m_ptr;
		InvenType i_ptr;
		
		Variable.free_turn_flag = true;
		if (Treasure.inven_ctr == 0) {
			IO.printMessage("But you are not carrying anything.");
		} else if (!Misc3.findRange(Constants.TV_FOOD, Constants.TV_NEVER, j, k)) {
			IO.printMessage("You are not carrying any food.");
		} else if (Moria1.getItemId(item_val, "Eat what?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.free_turn_flag = false;
			i.value(i_ptr.flags);
			ident = false;
			while (i.value() != 0) {
				j.value(Misc1.firstBitPos(i) + 1);
				/* Foods					*/
				switch(j.value()) {
					case 1:
						f_ptr = Player.py.flags;
						f_ptr.poisoned += Misc1.randomInt(10) + i_ptr.level;
						ident = true;
						break;
					case 2:
						f_ptr = Player.py.flags;
						f_ptr.blind += Misc1.randomInt(250) + 10 * i_ptr.level + 100;
						Misc3.drawCave();
						IO.printMessage("A veil of darkness surrounds you.");
						ident = true;
						break;
					case 3:
						f_ptr = Player.py.flags;
						f_ptr.afraid += Misc1.randomInt(10) + i_ptr.level;
						IO.printMessage("You feel terrified!");
						ident = true;
						break;
					case 4:
						f_ptr = Player.py.flags;
						f_ptr.confused += Misc1.randomInt(10) + i_ptr.level;
						IO.printMessage("You feel drugged.");
						ident = true;
						break;
					case 5:
						f_ptr = Player.py.flags;
						f_ptr.image += Misc1.randomInt(200) + 25 * i_ptr.level + 200;
						IO.printMessage("You feel drugged.");
						ident = true;
						break;
					case 6:
						ident = Spells.curePoison();
						break;
					case 7:
						ident = Spells.cureBlindness();
						break;
					case 8:
						f_ptr = Player.py.flags;
						if (f_ptr.afraid > 1) {
							f_ptr.afraid = 1;
							ident = true;
						}
						break;
					case 9:
						ident = Spells.cureConfusion();
						break;
					case 10:
						ident = true;
						Spells.loseStrength();
						break;
					case 11:
						ident = true;
						Spells.loseConstitution();
						break;
					case 16:
						if (Misc3.restoreStat(Constants.A_STR)) {
							IO.printMessage("You feel your strength returning.");
							ident = true;
						}
						break;
					case 17:
						if (Misc3.restoreStat(Constants.A_CON)) {
							IO.printMessage("You feel your health returning.");
							ident = true;
						}
						break;
					case 18:
						if (Misc3.restoreStat(Constants.A_INT)) {
							IO.printMessage("Your head spins a moment.");
							ident = true;
						}
						break;
					case 19:
						if (Misc3.restoreStat(Constants.A_WIS)) {
							IO.printMessage("You feel your wisdom returning.");
							ident = true;
						}
						break;
					case 20:
						if (Misc3.restoreStat(Constants.A_DEX)) {
							IO.printMessage("You feel more dextrous.");
							ident = true;
						}
						break;
					case 21:
						if (Misc3.restoreStat(Constants.A_CHR)) {
							IO.printMessage("Your skin stops itching.");
							ident = true;
						}
						break;
					case 22:
						ident = Spells.changePlayerHitpoints(Misc1.randomInt(6));
						break;
					case 23:
						ident = Spells.changePlayerHitpoints(Misc1.randomInt(12));
						break;
					case 24:
						ident = Spells.changePlayerHitpoints(Misc1.randomInt(18));
						break;
					case 26:
						ident = Spells.changePlayerHitpoints(Misc1.damageRoll(3, 12));
						break;
					case 27:
						Moria1.takeHit(Misc1.randomInt(18), "poisonous food.");
						ident = true;
						break;
					default:
						IO.printMessage("Internal error in eat()");
						break;
				}
				/* End of food actions.				*/
			}
			if (ident) {
				if (!Desc.isKnownByPlayer(i_ptr)) {
					/* use identified it, gain experience */
					m_ptr = Player.py.misc;
					/* round half-way case up */
					m_ptr.exp += (i_ptr.level + (m_ptr.lev >> 1)) / m_ptr.lev;
					Misc3.printExperience();
					
					Desc.identify(item_val);
					i_ptr = Treasure.inventory[item_val.value()];
				}
			} else if (!Desc.isKnownByPlayer(i_ptr)) {
				Desc.sample(i_ptr);
			}
			Misc1.addFood(i_ptr.p1);
			Player.py.flags.status &= ~(Constants.PY_WEAK | Constants.PY_HUNGRY);
			Misc3.printHunger();
			Desc.describeRemaining(item_val.value());
			Misc3.destroyInvenItem(item_val.value());
		}
	}
}
