/*
 * Potions.java: code for potions
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

public class Potions {
	
	private Potions() { }
	
	/* Potions for the quaffing				-RAK-	*/
	public static void quaff() {
		IntPointer i = new IntPointer();
		long l;
		IntPointer j = new IntPointer(), k = new IntPointer();
		IntPointer item_val = new IntPointer();
		boolean ident;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		PlayerFlags f_ptr;
		
		Variable.freeTurnFlag = true;
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
		} else if (!Misc3.findRange(Constants.TV_POTION1, Constants.TV_POTION2, j, k)) {
			IO.printMessage("You are not carrying any potions.");
		} else if (Moria1.getItemId(item_val, "Quaff which potion?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			i.value(i_ptr.flags);
			Variable.freeTurnFlag = false;
			ident = false;
			if (i.value() == 0) {
				IO.printMessage ("You feel less thirsty.");
				ident = true;
			} else while (i.value() != 0) {
				j.value(Misc1.firstBitPos(i) + 1);
				if (i_ptr.category == Constants.TV_POTION2) {
					j.value(j.value() + 32);
				}
				/* Potions						*/
				switch(j.value())
				{
				case 1:
					if (Misc3.increaseStat(Constants.A_STR)) {
						IO.printMessage("Wow!  What bulging muscles!");
						ident = true;
					}
					break;
				case 2:
					ident = true;
					Spells.loseStrength();
					break;
				case 3:
					if (Misc3.restoreStat(Constants.A_STR)) {
						IO.printMessage("You feel warm all over.");
						ident = true;
					}
					break;
				case 4:
					if (Misc3.increaseStat(Constants.A_INT)) {
						IO.printMessage("Aren't you brilliant!");
						ident = true;
					}
					break;
				case 5:
					ident = true;
					Spells.loseIntelligence();
					break;
				case 6:
					if (Misc3.restoreStat(Constants.A_INT)) {
						IO.printMessage("You have have a warm feeling.");
						ident = true;
					}
					break;
				case 7:
					if (Misc3.increaseStat(Constants.A_WIS)) {
						IO.printMessage("You suddenly have a profound thought!");
						ident = true;
					}
					break;
				case 8:
					ident = true;
					Spells.loseWisdom();
					break;
				case 9:
					if (Misc3.restoreStat(Constants.A_WIS)) {
						IO.printMessage("You feel your wisdom returning.");
						ident = true;
					}
					break;
				case 10:
					if (Misc3.increaseStat(Constants.A_CHR)) {
						IO.printMessage("Gee, ain't you cute!");
						ident = true;
					}
					break;
				case 11:
					ident = true;
					Spells.loseCharisma();
					break;
				case 12:
					if (Misc3.restoreStat(Constants.A_CHR)) {
						IO.printMessage("You feel your looks returning.");
						ident = true;
					}
					break;
				case 13:
					ident = Spells.changePlayerHitpoints(Misc1.damageRoll(2, 7));
					break;
				case 14:
					ident = Spells.changePlayerHitpoints(Misc1.damageRoll(4, 7));
					break;
				case 15:
					ident = Spells.changePlayerHitpoints(Misc1.damageRoll(6, 7));
					break;
				case 16:
					ident = Spells.changePlayerHitpoints(1000);
					break;
				case 17:
					if (Misc3.increaseStat(Constants.A_CON)) {
						IO.printMessage("You feel tingly for a moment.");
						ident = true;
					}
					break;
				case 18:
					m_ptr = Player.py.misc;
					if (m_ptr.currExp < Constants.MAX_EXP) {
						l = (m_ptr.currExp / 2) + 10;
						if (l > 100000L)  l = 100000L;
						m_ptr.currExp += l;
						IO.printMessage("You feel more experienced.");
						Misc3.printExperience();
						ident = true;
					}
					break;
				case 19:
					f_ptr = Player.py.flags;
					if (!f_ptr.freeAct) {
						/* paralysis must == 0, otherwise could not drink potion */
						IO.printMessage("You fall asleep.");
						f_ptr.paralysis += Misc1.randomInt(4) + 4;
						ident = true;
					}
					break;
				case 20:
					f_ptr = Player.py.flags;
					if (f_ptr.blind == 0) {
						IO.printMessage("You are covered by a veil of darkness.");
						ident = true;
					}
					f_ptr.blind += Misc1.randomInt(100) + 100;
					break;
				case 21:
					f_ptr = Player.py.flags;
					if (f_ptr.confused == 0) {
						IO.printMessage("Hey!  This is good stuff!  * Hick! *");
						ident = true;
					}
					f_ptr.confused += Misc1.randomInt(20) + 12;
					break;
				case 22:
					f_ptr = Player.py.flags;
					if (f_ptr.poisoned == 0) {
						IO.printMessage("You feel very sick.");
						ident = true;
					}
					f_ptr.poisoned += Misc1.randomInt(15) + 10;
					break;
				case 23:
					if (Player.py.flags.fast == 0) {
						ident = true;
					}
					Player.py.flags.fast += Misc1.randomInt(25) + 15;
					break;
				case 24:
					if (Player.py.flags.slow == 0) {
						ident = true;
					}
					Player.py.flags.slow += Misc1.randomInt(25) + 15;
					break;
				case 26:
					if (Misc3.increaseStat(Constants.A_DEX)) {
						IO.printMessage("You feel more limber!");
						ident = true;
					}
					break;
				case 27:
					if (Misc3.restoreStat(Constants.A_DEX)) {
						IO.printMessage("You feel less clumsy.");
						ident = true;
					}
					break;
				case 28:
					if (Misc3.restoreStat(Constants.A_CON)) {
						IO.printMessage("You feel your health returning!");
						ident = true;
					}
					break;
				case 29:
					ident = Spells.cureBlindness();
					break;
				case 30:
					ident = Spells.cureConfusion();
					break;
				case 31:
					ident = Spells.curePoison();
					break;
				case 34:
					if (Player.py.misc.currExp > 0) {
						int m, scale;
						IO.printMessage("You feel your memories fade.");
						/* Lose between 1/5 and 2/5 of your experience */
						m = Player.py.misc.currExp / 5;
						if (Player.py.misc.currExp > Constants.MAX_SHORT) {
							scale = (int)(Constants.MAX_LONG / Player.py.misc.currExp);
							m += (Misc1.randomInt(scale) * Player.py.misc.currExp) / (scale * 5);
						} else {
							m += Misc1.randomInt(Player.py.misc.currExp) / 5;
						}
						Spells.loseExperience(m);
						ident = true;
					}
					break;
				case 35:
					f_ptr = Player.py.flags;
					Spells.curePoison();
					if (f_ptr.food > 150)  f_ptr.food = 150;
					f_ptr.paralysis = 4;
					IO.printMessage("The potion makes you vomit!");
					ident = true;
					break;
				case 36:
					if (Player.py.flags.invulnerability == 0) {
						ident = true;
					}
					Player.py.flags.invulnerability += Misc1.randomInt(10) + 10;
					break;
				case 37:
					if (Player.py.flags.hero == 0) {
						ident = true;
					}
					Player.py.flags.hero += Misc1.randomInt(25) + 25;
					break;
				case 38:
					if (Player.py.flags.superHero == 0) {
						ident = true;
					}
					Player.py.flags.superHero += Misc1.randomInt(25) + 25;
					break;
				case 39:
					ident = Spells.removeFear();
					break;
				case 40:
					ident = Spells.restoreLevel();
					break;
				case 41:
					f_ptr = Player.py.flags;
					if (f_ptr.resistHeat == 0) {
						ident = true;
					}
					f_ptr.resistHeat += Misc1.randomInt(10) + 10;
					break;
				case 42:
					f_ptr = Player.py.flags;
					if (f_ptr.resistCold == 0) {
						ident = true;
					}
					f_ptr.resistCold += Misc1.randomInt(10) + 10;
					break;
				case 43:
					if (Player.py.flags.detectInvisible == 0) {
						ident = true;
					}
					Spells.detectInvisibleMonsters(Misc1.randomInt(12) + 12);
					break;
				case 44:
					ident = Spells.slowPoison();
					break;
				case 45:
					ident = Spells.curePoison();
					break;
				case 46:
					m_ptr = Player.py.misc;
					if (m_ptr.currMana < m_ptr.maxMana) {
						m_ptr.currMana = m_ptr.maxMana;
						ident = true;
						IO.printMessage("Your feel your head clear.");
						Misc3.printCurrentMana();
					}
					break;
				case 47:
					f_ptr = Player.py.flags;
					if (f_ptr.timedSeeInfrared == 0) {
						IO.printMessage("Your eyes begin to tingle.");
						ident = true;
					}
					f_ptr.timedSeeInfrared += 100 + Misc1.randomInt(100);
					break;
				default:
					IO.printMessage("Internal error in potion()");
					break;
				}
				/* End of Potions.					*/
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
			
			Misc1.addFood(i_ptr.misc);
			Desc.describeRemaining(item_val.value());
			Misc3.destroyInvenItem(item_val.value());
		}
	}
}
