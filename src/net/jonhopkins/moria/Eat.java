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

import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Eat {
	
	private Eat() { }
	
	/**
	 * Eat some food. -RAK-
	 */
	public static void eat() {
		Variable.freeTurnFlag = true;
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
			return;
		}
		
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		if (!Misc3.findRange(Constants.TV_FOOD, Constants.TV_NEVER, first, last)) {
			IO.printMessage("You are not carrying any food.");
			return;
		}
		
		IntPointer item_val = new IntPointer();
		if (!Moria1.getItemId(item_val, "Eat what?", first.value(), last.value(), "", "")) {
			return;
		}
		
		InvenType food = Treasure.inventory[item_val.value()];
		Variable.freeTurnFlag = false;
		IntPointer flags = new IntPointer(food.flags);
		boolean identified = false;
		while (flags.value() != 0) {
			int foodType = Misc1.firstBitPos(flags) + 1;
			// Foods
			switch (foodType) {
			case 1:
				identified = poisonPlayer(food);
				break;
			case 2:
				identified = blindPlayer(food);
				break;
			case 3:
				identified = scarePlayer(food);
				break;
			case 4:
				identified = confusePlayer(food);
				break;
			case 5:
				identified = hallucinatePlayer(food);
				break;
			case 6:
				identified = curePoison();
				break;
			case 7:
				identified = cureBlindness();
				break;
			case 8:
				if (cureFear()) {
					identified = true;
				}
				break;
			case 9:
				identified = cureConfusion();
				break;
			case 10:
				identified = loseStrength();
				break;
			case 11:
				identified = loseConstitution();
				break;
			/*
			case 12:
				identified = true;
				loseIntelligence();
				break;
			case 13:
				identified = true;
				loseWisdom();
				break;
			case 14:
				identified = true;
				loseDexterity();
				break;
			case 15:
				identified = true;
				loseCharisma();
				break;
			*/
			case 16:
				if (restoreStrength()) {
					identified = true;
				}
				break;
			case 17:
				if (restoreConstitution()) {
					identified = true;
				}
				break;
			case 18:
				if (restoreIntelligence()) {
					identified = true;
				}
				break;
			case 19:
				if (restoreWisdom()) {
					identified = true;
				}
				break;
			case 20:
				if (restoreDexterity()) {
					identified = true;
				}
				break;
			case 21:
				if (restoreCharisma()) {
					identified = true;
				}
				break;
			case 22:
				identified = changeHitpoints1();
				break;
			case 23:
				identified = changeHitpoints2();
				break;
			case 24:
				identified = changeHitpoints3();
				break;
			/*
			case 25:
				identified = Spells.changePlayerHitpoints(Misc1.damageRoll(3, 6));
				break;
			*/
			case 26:
				identified = changeHitpoints4();
				break;
			case 27:
				identified = damagePlayer();
				break;
			/*
			case 28:
				Moria1.takeHit(Rnd.randomInt(8), "poisonous food.");
				identified = true;
				break;
			case 29:
				Moria1.takeHit(Misc1.damageRoll(2, 8), "poisonous food.");
				identified = true;
				break;
			case 30:
				Moria1.takeHit(Misc1.damageRoll(3, 8), "poisonous food.");
				identified = true;
				break;
			*/
			default:
				IO.printMessage("Internal error in eat()");
				break;
			}
		}	
		
		if (identified) {
			if (!Desc.isKnownByPlayer(food)) {
				// use identified it, gain experience
				// round half-way case up
				PlayerMisc misc = Player.py.misc;
				misc.currExp += (food.level + (misc.level >> 1)) / misc.level;
				Misc3.printExperience();
				
				Desc.identify(item_val);
				food = Treasure.inventory[item_val.value()];
			}
		} else if (!Desc.isKnownByPlayer(food)) {
			Desc.sample(food);
		}
		
		Misc1.addFood(food.misc);
		Player.py.flags.status &= ~(Constants.PY_WEAK | Constants.PY_HUNGRY);
		Misc3.printHunger();
		Desc.describeRemaining(item_val.value());
		Misc3.destroyInvenItem(item_val.value());
	}
	
	private static boolean poisonPlayer(InvenType food) {
		Player.py.flags.poisoned += Rnd.randomInt(10) + food.level;
		return true;
	}
	
	private static boolean blindPlayer(InvenType food) {
		Player.py.flags.blind += Rnd.randomInt(250) + 10 * food.level + 100;
		Misc3.drawCave();
		IO.printMessage("A veil of darkness surrounds you.");
		return true;
	}
	
	private static boolean scarePlayer(InvenType food) {
		Player.py.flags.afraid += Rnd.randomInt(10) + food.level;
		IO.printMessage("You feel terrified!");
		return true;
	}
	
	private static boolean confusePlayer(InvenType food) {
		Player.py.flags.confused += Rnd.randomInt(10) + food.level;
		IO.printMessage("You feel drugged.");
		return true;
	}
	
	private static boolean hallucinatePlayer(InvenType food) {
		Player.py.flags.imagine += Rnd.randomInt(200) + 25 * food.level + 200;
		IO.printMessage("You feel drugged.");
		return true;
	}
	
	private static boolean curePoison() {
		return Spells.curePoison();
	}
	
	private static boolean cureBlindness() {
		return Spells.cureBlindness();
	}
	
	private static boolean cureFear() {
		if (Player.py.flags.afraid > 1) {
			Player.py.flags.afraid = 1;
			return true;
		}
		return false;
	}
	
	private static boolean cureConfusion() {
		return Spells.cureConfusion();
	}
	
	private static boolean loseStrength() {
		Spells.loseStrength();
		return true;
	}
	
	private static boolean loseConstitution() {
		Spells.loseConstitution();
		return true;
	}
	
	private static boolean restoreStrength() {
		if (Misc3.restoreStat(Constants.A_STR)) {
			IO.printMessage("You feel your strength returning.");
			return true;
		}
		return false;
	}
	
	private static boolean restoreConstitution() {
		if (Misc3.restoreStat(Constants.A_CON)) {
			IO.printMessage("You feel your health returning.");
			return true;
		}
		return false;
	}
	
	private static boolean restoreIntelligence() {
		if (Misc3.restoreStat(Constants.A_INT)) {
			IO.printMessage("Your head spins a moment.");
			return true;
		}
		return false;
	}
	
	private static boolean restoreWisdom() {
		if (Misc3.restoreStat(Constants.A_WIS)) {
			IO.printMessage("You feel your wisdom returning.");
			return true;
		}
		return false;
	}
	
	private static boolean restoreDexterity() {
		if (Misc3.restoreStat(Constants.A_DEX)) {
			IO.printMessage("You feel more dextrous.");
			return true;
		}
		return false;
	}
	
	private static boolean restoreCharisma() {
		if (Misc3.restoreStat(Constants.A_CHR)) {
			IO.printMessage("Your skin stops itching.");
			return true;
		}
		return false;
	}
	
	private static boolean changeHitpoints1() {
		return Spells.changePlayerHitpoints(Rnd.randomInt(6));
	}
	
	private static boolean changeHitpoints2() {
		return Spells.changePlayerHitpoints(Rnd.randomInt(12));
	}
	
	private static boolean changeHitpoints3() {
		return Spells.changePlayerHitpoints(Rnd.randomInt(18));
	}
	
	private static boolean changeHitpoints4() {
		return Spells.changePlayerHitpoints(Misc1.damageRoll(3, 12));
	}
	
	private static boolean damagePlayer() {
		Moria1.takeHit(Rnd.randomInt(18), "poisonous food.");
		return true;
	}
}
