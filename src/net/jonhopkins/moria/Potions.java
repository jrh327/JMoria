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
	
	/**
	 * Potions for the quaffing -RAK-
	 */
	public static void quaff() {
		Variable.freeTurnFlag = true;
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
			return;
		}
		
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		if (!Misc3.findRange(Constants.TV_POTION1, Constants.TV_POTION2, first, last)) {
			IO.printMessage("You are not carrying any potions.");
			return;
		}
		
		IntPointer index = new IntPointer();
		if (!Moria1.getItemId(index, "Quaff which potion?", first.value(), last.value(), null, "")) {
			return;
		}
		
		InvenType potion = Treasure.inventory[index.value()];
		IntPointer flags = new IntPointer(potion.flags);
		Variable.freeTurnFlag = false;
		boolean identified = false;
		if (flags.value() == 0) {
			IO.printMessage ("You feel less thirsty.");
			identified = true;
		}
		
		while (flags.value() != 0) {
			int potionType = Misc1.firstBitPos(flags) + 1;
			if (potion.category == Constants.TV_POTION2) {
				potionType += 32;
			}
			// Potions
			switch (potionType) {
			case 1:
				if (potionOfGainStrength()) {
					identified = true;
				}
				break;
			case 2:
				identified = potionOfWeakness();
				break;
			case 3:
				if (potionOfRestoreStrength()) {
					identified = true;
				}
				break;
			case 4:
				if (potionOfGainIntelligence()) {
					identified = true;
				}
				break;
			case 5:
				identified = potionOfLoseIntelligence();
				break;
			case 6:
				if (potionOfRestoreIntelligence()) {
					identified = true;
				}
				break;
			case 7:
				if (potionOfGainWisdom()) {
					identified = true;
				}
				break;
			case 8:
				identified = potionOfLoseWisdom();
				break;
			case 9:
				if (potionOfRestoreWisdom()) {
					identified = true;
				}
				break;
			case 10:
				if (potionOfCharisma()) {
					identified = true;
				}
				break;
			case 11:
				identified = potionOfUgliness();
				break;
			case 12:
				if (potionOfRestoreCharisma()) {
					identified = true;
				}
				break;
			case 13:
				identified = potionOfCureLightWounds();
				break;
			case 14:
				identified = potionOfCureSeriousWounds();
				break;
			case 15:
				identified = potionOfCureCriticalWounds();
				break;
			case 16:
				identified = potionOfHealing();
				break;
			case 17:
				if (potionOfGainConstitution()) {
					identified = true;
				}
				break;
			case 18:
				if (potionOfGainExperience()) {
					identified = true;
				}
				break;
			case 19:
				if (potionOfSleep()) {
					identified = true;
				}
				break;
			case 20:
				if (potionOfBlindness()) {
					identified = true;
				}
				break;
			case 21:
				if (potionOfConfusion()) {
					identified = true;
				}
				break;
			case 22:
				if (potionOfPoison()) {
					identified = true;
				}
				break;
			case 23:
				if (potionOfHasteSelf()) {
					identified = true;
				}
				break;
			case 24:
				if (potionOfSlowness()) {
					identified = true;
				}
				break;
			case 26:
				if (potionOfGainDexterity()) {
					identified = true;
				}
				break;
			case 27:
				if (potionOfRestoreDexterity()) {
					identified = true;
				}
				break;
			case 28:
				if (potionOfRestoreConstitution()) {
					identified = true;
				}
				break;
			case 29:
				identified = potionOfCureBlindness();
				break;
			case 30:
				identified = potionOfCureConfusion();
				break;
			case 31:
				identified = potionOfCurePoison();
				break;
			case 34:
				if (potionOfLoseExperience()) {
					identified = true;
				}
				break;
			case 35:
				identified = potionOfSaltWater();
				break;
			case 36:
				if (potionOfInvulnerability()) {
					identified = true;
				}
				break;
			case 37:
				if (potionOfHeroism()) {
					identified = true;
				}
				break;
			case 38:
				if (potionOfSuperHeroism()) {
					identified = true;
				}
				break;
			case 39:
				identified = potionOfBoldness();
				break;
			case 40:
				identified = potionOfRestoreLifeLevels();
				break;
			case 41:
				if (potionOfResistHeat()) {
					identified = true;
				}
				break;
			case 42:
				if (potionOfResistCold()) {
					identified = true;
				}
				break;
			case 43:
				if (potionOfDetectInvisible()) {
					identified = true;
				}
				break;
			case 44:
				identified = potionOfSlowPoison();
				break;
			case 45:
				identified = potionOfNeutralizePoison();
				break;
			case 46:
				if (potionOfRestoreMana()) {
					identified = true;
				}
				break;
			case 47:
				if (potionOfInfraVision()) {
					identified = true;
				}
				break;
			default:
				IO.printMessage("Internal error in potion()");
				break;
			}
		}
		
		if (identified) {
			if (!Desc.isKnownByPlayer(potion)) {
				PlayerMisc misc = Player.py.misc;
				// round half-way case up
				misc.currExp += (potion.level + (misc.level >> 1)) / misc.level;
				Misc3.printExperience();
				
				Desc.identify(index);
				potion = Treasure.inventory[index.value()];
			}
		} else if (!Desc.isKnownByPlayer(potion)) {
			Desc.sample(potion);
		}
		
		Misc1.addFood(potion.misc);
		Desc.describeRemaining(index.value());
		Misc3.destroyInvenItem(index.value());
	}
	
	private static boolean potionOfGainStrength() {
		if (Misc3.increaseStat(Constants.A_STR)) {
			IO.printMessage("Wow!  What bulging muscles!");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfWeakness() {
		Spells.loseStrength();
		return true;
	}
	
	private static boolean potionOfRestoreStrength() {
		if (Misc3.restoreStat(Constants.A_STR)) {
			IO.printMessage("You feel warm all over.");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfGainIntelligence() {
		if (Misc3.increaseStat(Constants.A_INT)) {
			IO.printMessage("Aren't you brilliant!");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfLoseIntelligence() {
		Spells.loseIntelligence();
		return true;
	}
	
	private static boolean potionOfRestoreIntelligence() {
		if (Misc3.restoreStat(Constants.A_INT)) {
			IO.printMessage("You have have a warm feeling.");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfGainWisdom() {
		if (Misc3.increaseStat(Constants.A_WIS)) {
			IO.printMessage("You suddenly have a profound thought!");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfLoseWisdom() {
		Spells.loseWisdom();
		return true;
	}
	
	private static boolean potionOfRestoreWisdom() {
		if (Misc3.restoreStat(Constants.A_WIS)) {
			IO.printMessage("You feel your wisdom returning.");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfCharisma() {
		if (Misc3.increaseStat(Constants.A_CHR)) {
			IO.printMessage("Gee, ain't you cute!");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfUgliness() {
		Spells.loseCharisma();
		return true;
	}
	
	private static boolean potionOfRestoreCharisma() {
		if (Misc3.restoreStat(Constants.A_CHR)) {
			IO.printMessage("You feel your looks returning.");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfCureLightWounds() {
		return Spells.changePlayerHitpoints(Misc1.damageRoll(2, 7));
	}
	
	private static boolean potionOfCureSeriousWounds() {
		return Spells.changePlayerHitpoints(Misc1.damageRoll(4, 7));
	}
	
	private static boolean potionOfCureCriticalWounds() {
		return Spells.changePlayerHitpoints(Misc1.damageRoll(6, 7));
	}
	
	private static boolean potionOfHealing() {
		return Spells.changePlayerHitpoints(1000);
	}
	
	private static boolean potionOfGainConstitution() {
		if (Misc3.increaseStat(Constants.A_CON)) {
			IO.printMessage("You feel tingly for a moment.");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfGainExperience() {
		PlayerMisc misc = Player.py.misc;
		if (misc.currExp < Constants.MAX_EXP) {
			long l = (misc.currExp / 2) + 10;
			if (l > 100000L) {
				l = 100000L;
			}
			misc.currExp += l;
			IO.printMessage("You feel more experienced.");
			Misc3.printExperience();
			return true;
		}
		return false;
	}
	
	private static boolean potionOfSleep() {
		PlayerFlags flags = Player.py.flags;
		if (!flags.freeAct) {
			// paralysis must == 0, otherwise could not drink potion
			IO.printMessage("You fall asleep.");
			flags.paralysis += Rnd.randomInt(4) + 4;
			return true;
		}
		return false;
	}
	
	private static boolean potionOfBlindness() {
		PlayerFlags flags = Player.py.flags;
		boolean blinded = false;
		if (flags.blind == 0) {
			IO.printMessage("You are covered by a veil of darkness.");
			blinded = true;
		}
		flags.blind += Rnd.randomInt(100) + 100;
		return blinded;
	}
	
	private static boolean potionOfConfusion() {
		PlayerFlags flags = Player.py.flags;
		boolean confused = false;
		if (flags.confused == 0) {
			IO.printMessage("Hey!  This is good stuff!  * Hick! *");
			confused = true;
		}
		flags.confused += Rnd.randomInt(20) + 12;
		return confused;
	}
	
	private static boolean potionOfPoison() {
		PlayerFlags flags = Player.py.flags;
		boolean poisoned = false;
		if (flags.poisoned == 0) {
			IO.printMessage("You feel very sick.");
			poisoned = true;
		}
		flags.poisoned += Rnd.randomInt(15) + 10;
		return poisoned;
	}
	
	private static boolean potionOfHasteSelf() {
		PlayerFlags flags = Player.py.flags;
		boolean hasted = false;
		if (flags.fast == 0) {
			hasted = true;
		}
		flags.fast += Rnd.randomInt(25) + 15;
		return hasted;
	}
	
	private static boolean potionOfSlowness() {
		PlayerFlags flags = Player.py.flags;
		boolean slowed = false;
		if (flags.slow == 0) {
			slowed = true;
		}
		flags.slow += Rnd.randomInt(25) + 15;
		return slowed;
	}
	
	private static boolean potionOfGainDexterity() {
		if (Misc3.increaseStat(Constants.A_DEX)) {
			IO.printMessage("You feel more limber!");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfRestoreDexterity() {
		if (Misc3.restoreStat(Constants.A_DEX)) {
			IO.printMessage("You feel less clumsy.");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfRestoreConstitution() {
		if (Misc3.restoreStat(Constants.A_CON)) {
			IO.printMessage("You feel your health returning!");
			return true;
		}
		return false;
	}
	
	private static boolean potionOfCureBlindness() {
		return Spells.cureBlindness();
	}
	
	private static boolean potionOfCureConfusion() {
		return Spells.cureConfusion();
	}
	
	private static boolean potionOfCurePoison() {
		return Spells.curePoison();
	}
	
	private static boolean potionOfLoseExperience() {
		if (Player.py.misc.currExp > 0) {
			IO.printMessage("You feel your memories fade.");
			// Lose between 1/5 and 2/5 of your experience
			int amount = Player.py.misc.currExp / 5;
			if (Player.py.misc.currExp > Constants.MAX_SHORT) {
				int scale = (int)(Constants.MAX_LONG / Player.py.misc.currExp);
				amount += (Rnd.randomInt(scale) * Player.py.misc.currExp) / (scale * 5);
			} else {
				amount += Rnd.randomInt(Player.py.misc.currExp) / 5;
			}
			Spells.loseExperience(amount);
			return true;
		}
		return false;
	}
	
	private static boolean potionOfSaltWater() {
		PlayerFlags flags = Player.py.flags;
		Spells.curePoison();
		if (flags.food > 150) {
			flags.food = 150;
		}
		flags.paralysis = 4;
		IO.printMessage("The potion makes you vomit!");
		return true;
	}
	
	private static boolean potionOfInvulnerability() {
		PlayerFlags flags = Player.py.flags;
		boolean invulnerable = false;
		if (flags.invulnerability == 0) {
			invulnerable = true;
		}
		flags.invulnerability += Rnd.randomInt(10) + 10;
		return invulnerable;
	}
	
	private static boolean potionOfHeroism() {
		PlayerFlags flags = Player.py.flags;
		boolean heroic = false;
		if (flags.hero == 0) {
			heroic = true;
		}
		flags.hero += Rnd.randomInt(25) + 25;
		return heroic;
	}
	
	private static boolean potionOfSuperHeroism() {
		PlayerFlags flags = Player.py.flags;
		boolean superHeroic = false;
		if (flags.superHero == 0) {
			superHeroic = true;
		}
		flags.superHero += Rnd.randomInt(25) + 25;
		return superHeroic;
	}
	
	private static boolean potionOfBoldness() {
		return Spells.removeFear();
	}
	
	private static boolean potionOfRestoreLifeLevels() {
		return Spells.restoreLevel();
	}
	
	private static boolean potionOfResistHeat() {
		PlayerFlags flags = Player.py.flags;
		boolean resisted = false;
		if (flags.resistHeat == 0) {
			resisted = true;
		}
		flags.resistHeat += Rnd.randomInt(10) + 10;
		return resisted;
	}
	
	private static boolean potionOfResistCold() {
		PlayerFlags flags = Player.py.flags;
		boolean resisted = false;
		if (flags.resistCold == 0) {
			resisted = true;
		}
		flags.resistCold += Rnd.randomInt(10) + 10;
		return resisted;
	}
	
	private static boolean potionOfDetectInvisible() {
		boolean detected = false;
		if (Player.py.flags.detectInvisible == 0) {
			detected = true;
		}
		Spells.detectInvisibleMonsters(Rnd.randomInt(12) + 12);
		return detected;
	}
	
	private static boolean potionOfSlowPoison() {
		return Spells.slowPoison();
	}
	
	private static boolean potionOfNeutralizePoison() {
		return Spells.curePoison();
	}
	
	private static boolean potionOfRestoreMana() {
		PlayerMisc misc = Player.py.misc;
		boolean restored = false;
		if (misc.currMana < misc.maxMana) {
			misc.currMana = misc.maxMana;
			restored = true;
			IO.printMessage("Your feel your head clear.");
			Misc3.printCurrentMana();
		}
		return restored;
	}
	
	private static boolean potionOfInfraVision() {
		PlayerFlags flags = Player.py.flags;
		boolean infra = false;
		if (flags.timedSeeInfrared == 0) {
			IO.printMessage("Your eyes begin to tingle.");
			infra = true;
		}
		flags.timedSeeInfrared += 100 + Rnd.randomInt(100);
		return infra;
	}
}
