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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.PlayerMisc;

public class Staffs {
	
	private Staffs() { }
	
	/**
	 * Use a staff. -RAK-
	 */
	public static void use() {
		Variable.freeTurnFlag = true;
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
			return;
		}
		
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		if (!Misc3.findRange(Constants.TV_STAFF, Constants.TV_NEVER, first, last)) {
			IO.printMessage("You are not carrying any staffs.");
			return;
		}
		
		IntPointer index = new IntPointer();
		if (!Moria1.getItemId(index, "Use which staff?", first.value(), last.value(), "", "")) {
			return;
		}
		
		InvenType staff = Treasure.inventory[index.value()];
		Variable.freeTurnFlag = false;
		PlayerMisc misc = Player.py.misc;
		int chance = misc.savingThrow
				+ Misc3.adjustStat(Constants.A_INT)
				- staff.level - 5
				+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DEVICE] * misc.level / 3);
		if (Player.py.flags.confused > 0) {
			chance = chance / 2;
		}
		if ((chance < Constants.USE_DEVICE)
				&& (Rnd.randomInt(Constants.USE_DEVICE - chance + 1) == 1)) {
			chance = Constants.USE_DEVICE; // Give everyone a slight chance
		}
		if (chance <= 0)	chance = 1;
		if (Rnd.randomInt(chance) < Constants.USE_DEVICE) {
			IO.printMessage("You failed to use the staff properly.");
			return;
		}
		if (staff.misc <= 0) {
			IO.printMessage("The staff has no charges left.");
			if (!Desc.arePlussesKnownByPlayer(staff)) {
				Misc4.addInscription(staff, Constants.ID_EMPTY);
			}
			return;
		}
	
		IntPointer flags = new IntPointer(staff.flags);
		boolean identified = false;
		staff.misc--;
		while (flags.value() != 0) {
			first.value(Misc1.firstBitPos(flags) + 1);
			// Staffs
			switch (first.value()) {
			case 1:
				identified = castLight();
				break;
			case 2:
				identified = castDoorAndStairLocation();
				break;
			case 3:
				identified = castTrapLocation();
				break;
			case 4:
				identified = castTreasureLocation();
				break;
			case 5:
				identified = castObjectLocation();
				break;
			case 6:
				identified = castTeleportation();
				break;
			case 7:
				identified = castEarthquakes();
				break;
			case 8:
				identified |= castSummoning();
				break;
			case 10:
				identified = castDestruction();
				break;
			case 11:
				identified = castStarlight();
				break;
			case 12:
				identified = castHasteMonsters();
				break;
			case 13:
				identified = castSlowMonsters();
				break;
			case 14:
				identified = castSleepMonsters();
				break;
			case 15:
				identified = castCureLightWounds();
				break;
			case 16:
				identified = castDetectInvisible();
				break;
			case 17:
				identified |= castSpeed();
				break;
			case 18:
				identified |= castSlowness();
				break;
			case 19:
				identified = castMassPolymorph();
				break;
			case 20:
				identified |= castRemoveCurse();
				break;
			case 21:
				identified = castDetectEvil();
				break;
			case 22:
				identified |= castCuring();
				break;
			case 23:
				identified = castDispelEvil();
				break;
			case 25:
				identified = castDarkness();
				break;
			case 32:
				// store bought flag
				break;
			default:
				IO.printMessage("Internal error in staffs()");
				break;
			}
		}
		
		if (identified) {
			if (!Desc.isKnownByPlayer(staff)) {
				// round half-way case up
				misc.currExp += (staff.level + (misc.level >> 1)) / misc.level;
				Misc3.printExperience();
				
				Desc.identify(index);
				staff = Treasure.inventory[index.value()];
			}
		} else if (!Desc.isKnownByPlayer(staff)) {
			Desc.sample(staff);
		}
		Desc.describeCharges(index.value());
	}
	
	private static boolean castLight() {
		return Spells.lightArea(Player.y, Player.x);
	}
	
	private static boolean castDoorAndStairLocation() {
		return Spells.detectSecretDoors();
	}
	
	private static boolean castTrapLocation() {
		return Spells.detectTrap();
	}
	
	private static boolean castTreasureLocation() {
		return Spells.detectTreasure();
	}
	
	private static boolean castObjectLocation() {
		return Spells.detectObject();
	}
	
	private static boolean castTeleportation() {
		Misc3.teleport(100);
		return true;
	}
	
	private static boolean castEarthquakes() {
		Spells.earthquake();
		return true;
	}
	
	private static boolean castSummoning() {
		boolean summon = false;
		for (int i = 0; i < Rnd.randomInt(4); i++) {
			IntPointer y = new IntPointer(Player.y);
			IntPointer x = new IntPointer(Player.x);
			summon |= Misc1.summonMonster(y, x, false);
		}
		return summon;
	}
	
	private static boolean castDestruction() {
		Spells.destroyArea(Player.y, Player.x);
		return true;
	}
	
	private static boolean castStarlight() {
		Spells.starLight(Player.y, Player.x);
		return true;
	}
	
	private static boolean castHasteMonsters() {
		return Spells.speedMonsters(1);
	}
	
	private static boolean castSlowMonsters() {
		return Spells.speedMonsters(-1);
	}
	
	private static boolean castSleepMonsters() {
		return Spells.sleepMonsters();
	}
	
	private static boolean castCureLightWounds() {
		return Spells.changePlayerHitpoints(Rnd.randomInt(8));
	}
	
	private static boolean castDetectInvisible() {
		return Spells.detectInvisibleCreatures();
	}
	
	private static boolean castSpeed() {
		PlayerFlags flags = Player.py.flags;
		boolean speed = false;
		if (flags.fast == 0) {
			speed = true;
		}
		flags.fast += Rnd.randomInt(30) + 15;
		return speed;
	}
	
	private static boolean castSlowness() {
		PlayerFlags flags = Player.py.flags;
		boolean slow = false;
		if (flags.slow == 0) {
			slow = true;
		}
		flags.slow += Rnd.randomInt(30) + 15;
		return slow;
	}
	
	private static boolean castMassPolymorph() {
		return Spells.massPolymorph();
	}
	
	private static boolean castRemoveCurse() {
		if (Spells.removeCurse()) {
			if (Player.py.flags.blind < 1) {
				IO.printMessage("The staff glows blue for a moment..");
			}
			return true;
		}
		return false;
	}
	
	private static boolean castDetectEvil() {
		return Spells.detectEvil();
	}
	
	private static boolean castCuring() {
		if (Spells.cureBlindness()
				|| Spells.curePoison()
				|| Spells.cureConfusion()) {
			return true;
		}
		return false;
	}
	
	private static boolean castDispelEvil() {
		return Spells.dispelCreature(Constants.CD_EVIL, 60);
	}
	
	private static boolean castDarkness() {
		return Spells.unlightArea(Player.y, Player.x);
	}
}
