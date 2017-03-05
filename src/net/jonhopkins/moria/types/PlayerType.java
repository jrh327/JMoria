/*
 * PlayerType.java: player object
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
package net.jonhopkins.moria.types;

import net.jonhopkins.moria.Constants;
import net.jonhopkins.moria.Creature;
import net.jonhopkins.moria.IO;
import net.jonhopkins.moria.Misc1;
import net.jonhopkins.moria.Misc3;
import net.jonhopkins.moria.Moria1;
import net.jonhopkins.moria.Moria2;
import net.jonhopkins.moria.Variable;

public final class PlayerType {
	public PlayerType() {
		misc = new PlayerMisc();
		flags = new PlayerFlags();
		stats = new PlayerStats();
	}
	
	public final int PLAYER_NAME_SIZE = 27;
	
	public PlayerMisc misc;
	public PlayerFlags flags;
	public PlayerStats stats;
	
	public void updateStatusEffects() {
		updateHeroism();
		updateSuperHeroism();
		regeneration();
		updateBlindness();
		updateConfusion();
		updateFear();
		updatePoison();
		updateFastness();
		updateSlowness();
		resting();
		updateHallucinations();
		updateParalysis();
		updateProtectFromEvil();
		updateInvulnerability();
		updateBlessings();
		updateHeatResistance();
		updateColdResistance();
		updateDetectInvisibility();
		updateTimedInfraVision();
		updateWordOfRecall();
		teleport();
		updateWeakness();
		printStatuses();
	}
	
	private void updateHeroism() {
		// Heroism (must precede anything that can damage player)
		if (flags.hero > 0) {
			if ((Constants.PY_HERO & flags.status) == 0) {
				flags.status |= Constants.PY_HERO;
				Moria1.disturbPlayer(false, false);
				misc.maxHitpoints += 10;
				misc.currHitpoints += 10;
				misc.baseToHit += 12;
				misc.baseToHitBow+= 12;
				IO.printMessage("You feel like a HERO!");
				Misc3.printMaxHitpoints();
				Misc3.printCurrentHitpoints();
			}
			
			flags.hero--;
			if (flags.hero == 0) {
				flags.status &= ~Constants.PY_HERO;
				Moria1.disturbPlayer(false, false);
				misc.maxHitpoints -= 10;
				if (misc.currHitpoints > misc.maxHitpoints) {
					misc.currHitpoints = misc.maxHitpoints;
					misc.currHitpointsFraction = 0;
					Misc3.printCurrentHitpoints();
				}
				misc.baseToHit -= 12;
				misc.baseToHitBow-= 12;
				IO.printMessage("The heroism wears off.");
				Misc3.printMaxHitpoints();
			}
		}
	}
	
	private void updateSuperHeroism() {
		// Super Heroism
		if (flags.superHero > 0) {
			if ((Constants.PY_SHERO & flags.status) == 0) {
				flags.status |= Constants.PY_SHERO;
				Moria1.disturbPlayer(false, false);
				misc.maxHitpoints += 20;
				misc.currHitpoints += 20;
				misc.baseToHit += 24;
				misc.baseToHitBow+= 24;
				IO.printMessage("You feel like a SUPER HERO!");
				Misc3.printMaxHitpoints();
				Misc3.printCurrentHitpoints();
			}
			
			flags.superHero--;
			if (flags.superHero == 0) {
				flags.status &= ~Constants.PY_SHERO;
				Moria1.disturbPlayer(false, false);
				misc.maxHitpoints -= 20;
				if (misc.currHitpoints > misc.maxHitpoints) {
					misc.currHitpoints = misc.maxHitpoints;
					misc.currHitpointsFraction = 0;
					Misc3.printCurrentHitpoints();
				}
				misc.baseToHit -= 24;
				misc.baseToHitBow-= 24;
				IO.printMessage("The super heroism wears off.");
				Misc3.printMaxHitpoints();
			}
		}
	}
	
	private void regeneration() {
		int regenAmount = Constants.PLAYER_REGEN_NORMAL;
		
		// Check food status
		if (flags.food < Constants.PLAYER_FOOD_ALERT) {
			if (flags.food < Constants.PLAYER_FOOD_WEAK) {
				if (flags.food < 0) {
					regenAmount = 0;
				} else if (flags.food < Constants.PLAYER_FOOD_FAINT) {
					regenAmount = Constants.PLAYER_REGEN_FAINT;
				} else if (flags.food < Constants.PLAYER_FOOD_WEAK) {
					regenAmount = Constants.PLAYER_REGEN_WEAK;
				}
				if ((Constants.PY_WEAK & flags.status) == 0) {
					flags.status |= Constants.PY_WEAK;
					IO.printMessage("You are getting weak from hunger.");
					Moria1.disturbPlayer(false, false);
					Misc3.printHunger();
				}
				if ((flags.food < Constants.PLAYER_FOOD_FAINT) && (Misc1.randomInt(8) == 1)) {
					flags.paralysis += Misc1.randomInt(5);
					IO.printMessage("You faint from the lack of food.");
					Moria1.disturbPlayer(true, false);
				}
			} else if ((Constants.PY_HUNGRY & flags.status) == 0) {
				flags.status |= Constants.PY_HUNGRY;
				IO.printMessage("You are getting hungry.");
				Moria1.disturbPlayer(false, false);
				Misc3.printHunger();
			}
		}
		
		// Food consumption
		// Note: Speeded up characters really burn up the food!
		if (flags.speed < 0) {
			flags.food -= flags.speed * flags.speed;
		}
		flags.food -= flags.foodDigested;
		if (flags.food < 0) {
			Moria1.takeHit(-flags.food / 16, "starvation"); // -CJS-
			Moria1.disturbPlayer(true, false);
		}
		
		// Regenerate
		if (flags.regenerate) {
			regenAmount = regenAmount * 3 / 2;
		}
		if ((flags.status & Constants.PY_SEARCH) != 0 || flags.rest != 0) {
			regenAmount = regenAmount * 2;
		}
		if ((flags.poisoned < 1) && (misc.currHitpoints < misc.maxHitpoints)) {
			regenerateHitpoints(regenAmount);
		}
		if (misc.currMana < misc.maxMana) {
			regenerateMana(regenAmount);
		}
	}
	
	private void updateBlindness() {
		// Blindness
		if (flags.blind > 0) {
			if ((Constants.PY_BLIND & flags.status) == 0) {
				flags.status |= Constants.PY_BLIND;
				Misc1.printMap();
				Misc3.printBlindness();
				Moria1.disturbPlayer(false, true);
				
				// unlight creatures
				Creature.creatures(false);
			}
			
			flags.blind--;
			if (flags.blind == 0) {
				flags.status &= ~Constants.PY_BLIND;
				Misc3.printBlindness();
				Misc1.printMap();
				/* light creatures */
				Moria1.disturbPlayer(false, true);
				Creature.creatures(false);
				IO.printMessage("The veil of darkness lifts.");
			}
		}
	}
	
	private void updateConfusion() {
		// Confusion
		if (flags.confused > 0) {
			if ((Constants.PY_CONFUSED & flags.status) == 0) {
				flags.status |= Constants.PY_CONFUSED;
				Misc3.printConfusion();
			}
			
			flags.confused--;
			if (flags.confused == 0) {
				flags.status &= ~Constants.PY_CONFUSED;
				Misc3.printConfusion();
				IO.printMessage("You feel less confused now.");
				if (flags.rest != 0) {
					Moria1.stopResting();
				}
			}
		}
	}
	
	private void updateFear() {
		// Afraid
		if (flags.afraid > 0) {
			if ((Constants.PY_FEAR & flags.status) == 0) {
				if ((flags.superHero + flags.hero) > 0) {
					flags.afraid = 0;
				} else {
					flags.status |= Constants.PY_FEAR;
					Misc3.printFear();
				}
			} else if ((flags.superHero + flags.hero) > 0) {
				flags.afraid = 1;
			}
			
			flags.afraid--;
			if (flags.afraid == 0) {
				flags.status &= ~Constants.PY_FEAR;
				Misc3.printFear();
				IO.printMessage("You feel bolder now.");
				Moria1.disturbPlayer(false, false);
			}
		}
	}
	
	private void updatePoison() {
		// Poisoned
		if (flags.poisoned > 0) {
			if ((Constants.PY_POISONED & flags.status) == 0) {
				flags.status |= Constants.PY_POISONED;
				Misc3.printPoisoned();
			}
			
			flags.poisoned--;
			if (flags.poisoned == 0) {
				flags.status &= ~Constants.PY_POISONED;
				Misc3.printPoisoned();
				IO.printMessage("You feel better.");
				Moria1.disturbPlayer(false, false);
			} else {
				int damage = 0;
				switch(Misc3.adjustConstitution()) {
				case -4: damage = 4; break;
				case -3:
				case -2: damage = 3; break;
				case -1: damage = 2; break;
				case 0:	 damage = 1; break;
				case 1: case 2: case 3:
					damage = ((Variable.turn % 2) == 0) ? 1 : 0;
					break;
				case 4: case 5:
					damage = ((Variable.turn % 3) == 0) ? 1 : 0;
					break;
				case 6:
					damage = ((Variable.turn % 4) == 0) ? 1 : 0;
					break;
				default:
					break;
				}
				Moria1.takeHit(damage, "poison");
				Moria1.disturbPlayer(true, false);
			}
		}
	}
	
	private void updateFastness() {
		// Fast
		if (flags.fast > 0) {
			if ((Constants.PY_FAST & flags.status) == 0) {
				flags.status |= Constants.PY_FAST;
				Moria1.changeSpeed(-1);
				IO.printMessage("You feel yourself moving faster.");
				Moria1.disturbPlayer(false, false);
			}
			
			flags.fast--;
			if (flags.fast == 0) {
				flags.status &= ~Constants.PY_FAST;
				Moria1.changeSpeed(1);
				IO.printMessage("You feel yourself slow down.");
				Moria1.disturbPlayer(false, false);
			}
		}
	}
	
	private void updateSlowness() {
		// Slow
		if (flags.slow > 0) {
			if ((Constants.PY_SLOW & flags.status) == 0) {
				flags.status |= Constants.PY_SLOW;
				Moria1.changeSpeed(1);
				IO.printMessage("You feel yourself moving slower.");
				Moria1.disturbPlayer(false, false);
			}
			
			flags.slow--;
			if (flags.slow == 0) {
				flags.status &= ~Constants.PY_SLOW;
				Moria1.changeSpeed(-1);
				IO.printMessage("You feel yourself speed up.");
				Moria1.disturbPlayer(false, false);
			}
		}
	}
	
	private void resting() {
		// Resting is over?
		if (flags.rest > 0) {
			flags.rest--;
			if (flags.rest == 0) {	// Resting over
				Moria1.stopResting();
			}
		} else if (flags.rest < 0) {
			// Rest until reach max mana and max hit points.
			flags.rest++;
			if ((misc.currHitpoints == misc.maxHitpoints && misc.currMana == misc.maxMana)
					|| flags.rest == 0) {
				Moria1.stopResting();
			}
		}
		
		// Check for interrupts to find or rest.
		if ((Variable.commandCount > 0 || Variable.findFlag > 0 || flags.rest != 0)
				&& IO.isKeyAvailable()) {	
			IO.getChar();
			Moria1.disturbPlayer(false, false);
		}
	}
	
	private void updateHallucinations() {
		// Hallucinating? (Random characters appear!)
		if (flags.imagine > 0) {
			Moria2.endFind();
			flags.imagine--;
			if (flags.imagine == 0) {
				Misc1.printMap(); // Used to draw entire screen! -CJS-
			}
		}
	}
	
	private void updateParalysis() {
		// Paralysis
		if (flags.paralysis > 0) {
			// when paralysis true, you can not see any movement that occurs
			flags.paralysis--;
			Moria1.disturbPlayer(true, false);
		}
	}
	
	private void updateProtectFromEvil() {
		// Protection from evil counter
		if (flags.protectFromEvil > 0) {
			flags.protectFromEvil--;
			if (flags.protectFromEvil == 0) {
				IO.printMessage("You no longer feel safe from evil.");
			}
		}
	}
	
	private void updateInvulnerability() {
		// Invulnerability
		if (flags.invulnerability > 0) {
			if ((Constants.PY_INVULN & flags.status) == 0) {
				flags.status |= Constants.PY_INVULN;
				Moria1.disturbPlayer(false, false);
				misc.totalArmorClass += 100;
				misc.displayPlusToArmorClass += 100;
				Misc3.printCurrentAc();
				IO.printMessage("Your skin turns into steel!");
			}
			
			flags.invulnerability--;
			if (flags.invulnerability == 0) {
				flags.status &= ~Constants.PY_INVULN;
				Moria1.disturbPlayer(false, false);
				misc.totalArmorClass -= 100;
				misc.displayPlusToArmorClass -= 100;
				Misc3.printCurrentAc();
				IO.printMessage("Your skin returns to normal.");
			}
		}
	}
	
	private void updateBlessings() {
		// Blessed
		if (flags.blessed > 0) {
			if ((Constants.PY_BLESSED & flags.status) == 0) {
				flags.status |= Constants.PY_BLESSED;
				Moria1.disturbPlayer(false, false);
				misc.baseToHit += 5;
				misc.baseToHitBow+= 5;
				misc.totalArmorClass += 2;
				misc.displayPlusToArmorClass+= 2;
				IO.printMessage("You feel righteous!");
				Misc3.printCurrentAc();
			}
			
			flags.blessed--;
			if (flags.blessed == 0) {
				flags.status &= ~Constants.PY_BLESSED;
				Moria1.disturbPlayer(false, false);
				misc.baseToHit -= 5;
				misc.baseToHitBow-= 5;
				misc.totalArmorClass -= 2;
				misc.displayPlusToArmorClass -= 2;
				IO.printMessage("The prayer has expired.");
				Misc3.printCurrentAc();
			}
		}
	}
	
	private void updateHeatResistance() {
		// Resist Heat
		if (flags.resistHeat > 0) {
			flags.resistHeat--;
			if (flags.resistHeat == 0) {
				IO.printMessage("You no longer feel safe from flame.");
			}
		}
	}
	
	private void updateColdResistance() {
		// Resist Cold
		if (flags.resistCold > 0) {
			flags.resistCold--;
			if (flags.resistCold == 0) {
				IO.printMessage("You no longer feel safe from cold.");
			}
		}
	}
	
	private void updateDetectInvisibility() {
		// Detect Invisible
		if (flags.detectInvisible > 0) {
			if ((Constants.PY_DET_INV & flags.status) == 0) {
				flags.status |= Constants.PY_DET_INV;
				flags.seeInvisible = true;
				
				// light but don't move creatures
				Creature.creatures(false);
			}
			
			flags.detectInvisible--;
			if (flags.detectInvisible == 0) {
				flags.status &= ~Constants.PY_DET_INV;
				
				// may still be able to see_inv if wearing magic item
				Moria1.calcBonuses();
				
				// unlight but don't move creatures
				Creature.creatures(false);
			}
		}
	}
	
	private void updateTimedInfraVision() {
		// Timed infra-vision
		if (flags.timedSeeInfrared > 0) {
			if ((Constants.PY_TIM_INFRA & flags.status) == 0) {
				flags.status |= Constants.PY_TIM_INFRA;
				flags.seeInfrared++;
				
				// light but don't move creatures
				Creature.creatures(false);
			}
			
			flags.timedSeeInfrared--;
			if (flags.timedSeeInfrared == 0) {
				flags.status &= ~Constants.PY_TIM_INFRA;
				flags.seeInfrared--;
				
				// unlight but don't move creatures
				Creature.creatures(false);
			}
		}
	}
	
	private void updateWordOfRecall() {
		// Word-of-Recall
		// Note: Word-of-Recall is a delayed action
		if (flags.wordRecall > 0) {
			if (flags.wordRecall == 1) {
				Variable.newLevelFlag = true;
				flags.paralysis++;
				flags.wordRecall = 0;
				if (Variable.dungeonLevel > 0) {
					Variable.dungeonLevel = 0;
					IO.printMessage("You feel yourself yanked upwards!");
				} else if (misc.maxDungeonLevel != 0) {
					Variable.dungeonLevel = misc.maxDungeonLevel;
					IO.printMessage("You feel yourself yanked downwards!");
				}
			} else {
				flags.wordRecall--;
			}
		}
	}
	
	private void teleport() {
		// Random teleportation
		if ((flags.teleport > 0) && (Misc1.randomInt(100) == 1)) {
			Moria1.disturbPlayer(false, false);
			Misc3.teleport(40);
		}
	}
	
	private void updateWeakness() {
		// See if we are too weak to handle the weapon or pack. -CJS-
		if ((flags.status & Constants.PY_STR_WGT) != 0) {
			Misc3.checkStrength();
		}
	}
	
	private void printStatuses() {
		if ((flags.status & Constants.PY_STUDY) != 0) {
			Misc3.printStudy();
		}
		if ((flags.status & Constants.PY_SPEED) != 0) {
			flags.status &= ~Constants.PY_SPEED;
			Misc3.printSpeed();
		}
		if ((flags.status & Constants.PY_PARALYSED) != 0 && (flags.paralysis < 1)) {
			Misc3.printState();
			flags.status &= ~Constants.PY_PARALYSED;
		} else if (flags.paralysis > 0) {
			Misc3.printState();
			flags.status |= Constants.PY_PARALYSED;
		} else if (flags.rest != 0) {
			Misc3.printState();
		}
		
		if ((flags.status & Constants.PY_ARMOR) != 0) {
			Misc3.printCurrentAc();
			flags.status &= ~Constants.PY_ARMOR;
		}
		if ((flags.status & Constants.PY_STATS) != 0) {
			for (int i = 0; i < 6; i++) {
				if (((Constants.PY_STR << i) & flags.status) != 0) {
					Misc3.printStat(i);
				}
			}
			flags.status &= ~Constants.PY_STATS;
		}
		if ((flags.status & Constants.PY_HP) != 0) {
			Misc3.printMaxHitpoints();
			Misc3.printCurrentHitpoints();
			flags.status &= ~Constants.PY_HP;
		}
		if ((flags.status & Constants.PY_MANA) != 0) {
			Misc3.printCurrentMana();
			flags.status &= ~Constants.PY_MANA;
		}
	}
	
	/**
	 * Regenerate hit points. -RAK-
	 */
	private void regenerateHitpoints(int percent) {
		int currHitpointsOrig = misc.currHitpoints;
		int newHitpoints = misc.maxHitpoints * percent + Constants.PLAYER_REGEN_HPBASE;
		misc.currHitpoints += newHitpoints >> 16; // div 65536
		
		// check for overflow
		if (misc.currHitpoints < 0 && currHitpointsOrig > 0) {
			misc.currHitpoints = Constants.MAX_SHORT;
		}
		
		int newHitpointsFraction = (newHitpoints & 0xFFFF) + misc.currHitpointsFraction; // mod 65536
		if (newHitpointsFraction >= 0x10000) {
			misc.currHitpointsFraction = newHitpointsFraction - 0x10000;
			misc.currHitpoints++;
		} else {
			misc.currHitpointsFraction = newHitpointsFraction;
		}
		
		// must set frac to zero even if equal
		if (misc.currHitpoints >= misc.maxHitpoints) {
			misc.currHitpoints = misc.maxHitpoints;
			misc.currHitpointsFraction = 0;
		}
		if (currHitpointsOrig != misc.currHitpoints) {
			Misc3.printCurrentHitpoints();
		}
	}
	
	/**
	 * Regenerate mana points. -RAK-
	 */
	private void regenerateMana(int percent) {
		int currManaOrig = misc.currMana;
		int newMana = misc.maxMana * percent + Constants.PLAYER_REGEN_MNBASE;
		misc.currMana += newMana >> 16; // div 65536
		
		// check for overflow
		if (misc.currMana < 0 && currManaOrig > 0) {
			misc.currMana = Constants.MAX_SHORT;
		}
		
		int newManaFraction = (newMana & 0xFFFF) + misc.currManaFraction; // mod 65536
		if (newManaFraction >= 0x10000) {
			misc.currManaFraction = newManaFraction - 0x10000;
			misc.currMana++;
		} else {
			misc.currManaFraction = newManaFraction;
		}
		
		// must set frac to zero even if equal
		if (misc.currMana >= misc.maxMana) {
			misc.currMana = misc.maxMana;
			misc.currManaFraction = 0;
		}
		if (currManaOrig != misc.currMana) {
			Misc3.printCurrentMana();
		}
	}
}
