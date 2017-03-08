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
				identified = Spells.curePoison();
				break;
			case 7:
				identified = Spells.cureBlindness();
				break;
			case 8:
				identified = cureConfusion();
				break;
			case 9:
				identified = Spells.cureConfusion();
				break;
			case 10:
				identified = true;
				Spells.loseStrength();
				break;
			case 11:
				identified = true;
				Spells.loseConstitution();
				break;
			case 16:
				identified = restoreStrength();
				break;
			case 17:
				identified = restoreConstitution();
				break;
			case 18:
				identified = restoreIntelligence();
				break;
			case 19:
				identified = restoreWisdom();
				break;
			case 20:
				identified = restoreDexterity();
				break;
			case 21:
				identified = restoreCharisma();
				break;
			case 22:
				identified = Spells.changePlayerHitpoints(Rnd.randomInt(6));
				break;
			case 23:
				identified = Spells.changePlayerHitpoints(Rnd.randomInt(12));
				break;
			case 24:
				identified = Spells.changePlayerHitpoints(Rnd.randomInt(18));
				break;
			case 26:
				identified = Spells.changePlayerHitpoints(Misc1.damageRoll(3, 12));
				break;
			case 27:
				identified = damagePlayer();
				break;
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
	
	private static boolean cureConfusion() {
		if (Player.py.flags.afraid > 1) {
			Player.py.flags.afraid = 1;
			return true;
		}
		return false;
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
	
	private static boolean damagePlayer() {
		Moria1.takeHit(Rnd.randomInt(18), "poisonous food.");
		return true;
	}
}
