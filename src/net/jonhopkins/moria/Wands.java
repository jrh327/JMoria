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
	
	/**
	 * Wands for the aiming.
	 */
	public static void aim() {
		Variable.freeTurnFlag = true;
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
			return;
		}
		
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		if (!Misc3.findRange(Constants.TV_WAND, Constants.TV_NEVER, first, last)) {
			IO.printMessage("You are not carrying any wands.");
			return;
		}
		
		IntPointer index = new IntPointer();
		if (!Moria1.getItemId(index, "Aim which wand?", first.value(), last.value(), "", "")) {
			return;
		}
		
		InvenType wand = Treasure.inventory[index.value()];
		Variable.freeTurnFlag = false;
		IntPointer dir = new IntPointer();
		if (!Moria1.getDirection("", dir)) {
			return;
		}
		
		if (Player.py.flags.confused > 0) {
			IO.printMessage("You are confused.");
			do {
				dir.value(Rnd.randomInt(9));
			} while (dir.value() == 5);
		}
		
		boolean identified = false;
		PlayerMisc misc = Player.py.misc;
		int chance = misc.savingThrow
				+ Misc3.adjustStat(Constants.A_INT)
				- wand.level
				+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DEVICE] * misc.level / 3);
		if (Player.py.flags.confused > 0) {
			chance = chance / 2;
		}
		if ((chance < Constants.USE_DEVICE)
				&& (Rnd.randomInt(Constants.USE_DEVICE - chance + 1) == 1)) {
			chance = Constants.USE_DEVICE; // Give everyone a slight chance
		}
		if (chance <= 0) {
			chance = 1;
		}
		if (Rnd.randomInt(chance) < Constants.USE_DEVICE) {
			IO.printMessage("You failed to use the wand properly.");
			return;
		}
		
		if (wand.misc <= 0) {
			IO.printMessage("The wand has no charges left.");
			if (!Desc.arePlussesKnownByPlayer(wand)) {
				Misc4.addInscription(wand, Constants.ID_EMPTY);
			}
			return;
		}
		
		IntPointer flags = new IntPointer(wand.flags);
		wand.misc--;
		while (flags.value() != 0) {
			int wandType = Misc1.firstBitPos(flags) + 1;
			// Wands
			switch (wandType) {
			case 1:
				identified = castLight(dir.value());
				break;
			case 2:
				identified = castLightningBolts(dir.value());
				break;
			case 3:
				identified = castFrostBolts(dir.value());
				break;
			case 4:
				identified = castFireBolts(dir.value());
				break;
			case 5:
				identified = castStoneToMud(dir.value());
				break;
			case 6:
				identified = castPolymorph(dir.value());
				break;
			case 7:
				identified = castHealMonster(dir.value());
				break;
			case 8:
				identified = castHasteMonster(dir.value());
				break;
			case 9:
				identified = castSlowMonster(dir.value());
				break;
			case 10:
				identified = castConfuseMonster(dir.value());
				break;
			case 11:
				identified = castSleepMonster(dir.value());
				break;
			case 12:
				identified = castDrainLife(dir.value());
				break;
			case 13:
				identified = castTrapAndDoorDestruction(dir.value());
				break;
			case 14:
				identified = castMagicMissile(dir.value());
				break;
			case 15:
				identified = castWallBuilding(dir.value());
				break;
			case 16:
				identified = castCloneMonster(dir.value());
				break;
			case 17:
				identified = castTeleportAway(dir.value());
				break;
			case 18:
				identified = castDisarming(dir.value());
				break;
			case 19:
				identified = castLightningBalls(dir.value());
				break;
			case 20:
				identified = castColdBalls(dir.value());
				break;
			case 21:
				identified = castFireBalls(dir.value());
				break;
			case 22:
				identified = castStinkingCloud(dir.value());
				break;
			case 23:
				identified = castAcidBalls(dir.value());
				break;
			case 24:
				castWonder(flags);
				break;
			default:
				IO.printMessage("Internal error in wands()");
				break;
			}
		}
		if (identified) {
			if (!Desc.isKnownByPlayer(wand)) {
				// round half-way case up
				misc.currExp += (wand.level + (misc.level >> 1)) / misc.level;
				Misc3.printExperience();
				
				Desc.identify(index);
				wand = Treasure.inventory[index.value()];
			}
		} else if (!Desc.isKnownByPlayer(wand)) {
			Desc.sample(wand);
		}
		Desc.describeCharges(index.value());
	}
	
	private static boolean castLight(int dir) {
		IO.printMessage("A line of blue shimmering light appears.");
		Spells.lightLine(dir, Player.y, Player.x);
		return true;
	}
	
	private static boolean castLightningBolts(int dir) {
		Spells.fireBolt(Constants.GF_LIGHTNING, dir,
				Player.y, Player.x, Misc1.damageRoll(4, 8),
				Player.spellNames[8]);
		return true;
	}
	
	private static boolean castFrostBolts(int dir) {
		Spells.fireBolt(Constants.GF_FROST, dir,
				Player.y, Player.x, Misc1.damageRoll(6, 8),
				Player.spellNames[14]);
		return true;
	}
	
	private static boolean castFireBolts(int dir) {
		Spells.fireBolt(Constants.GF_FIRE, dir,
				Player.y, Player.x, Misc1.damageRoll(9, 8),
				Player.spellNames[22]);
		return true;
	}
	
	private static boolean castStoneToMud(int dir) {
		return Spells.transformWallToMud(dir, Player.y, Player.x);
	}
	
	private static boolean castPolymorph(int dir) {
		return Spells.polymorphMonster(dir, Player.y, Player.x);
	}
	
	private static boolean castHealMonster(int dir) {
		return Spells.changeMonsterHitpoints(dir, Player.y, Player.x,
				-Misc1.damageRoll(4, 6));
	}
	
	private static boolean castHasteMonster(int dir) {
		return Spells.speedMonster(dir, Player.y, Player.x, 1);
	}
	
	private static boolean castSlowMonster(int dir) {
		return Spells.speedMonster(dir, Player.y, Player.x, -1);
	}
	
	private static boolean castConfuseMonster(int dir) {
		return Spells.confuseMonster(dir, Player.y, Player.x);
	}
	
	private static boolean castSleepMonster(int dir) {
		return Spells.sleepMonster(dir, Player.y, Player.x);
	}
	
	private static boolean castDrainLife(int dir) {
		return Spells.drainLife(dir, Player.y, Player.x);
	}
	
	private static boolean castTrapAndDoorDestruction(int dir) {
		return Spells.destroyTrapsAndDoors(dir, Player.y, Player.x);
	}
	
	private static boolean castMagicMissile(int dir) {
		Spells.fireBolt(Constants.GF_MAGIC_MISSILE, dir,
				Player.y, Player.x, Misc1.damageRoll(2, 6),
				Player.spellNames[0]);
		return true;
	}
	
	private static boolean castWallBuilding(int dir) {
		return Spells.buildWall(dir, Player.y, Player.x);
	}
	
	private static boolean castCloneMonster(int dir) {
		return Spells.cloneMonster(dir, Player.y, Player.x);
	}
	
	private static boolean castTeleportAway(int dir) {
		return Spells.teleportMonsters(dir, Player.y, Player.x);
	}
	
	private static boolean castDisarming(int dir) {
		return Spells.disarmAll(dir, Player.y, Player.x);
	}
	
	private static boolean castLightningBalls(int dir) {
		Spells.fireBall(Constants.GF_LIGHTNING, dir,
				Player.y, Player.x, 32, "Lightning Ball");
		return true;
	}
	
	private static boolean castColdBalls(int dir) {
		Spells.fireBall(Constants.GF_FROST, dir,
				Player.y, Player.x, 48, "Cold Ball");
		return true;
	}
	
	private static boolean castFireBalls(int dir) {
		Spells.fireBall(Constants.GF_FIRE, dir,
				Player.y, Player.x, 72, Player.spellNames[28]);
		return true;
	}
	
	private static boolean castStinkingCloud(int dir) {
		Spells.fireBall(Constants.GF_POISON_GAS, dir,
				Player.y, Player.x, 12, Player.spellNames[6]);
		return true;
	}
	
	private static boolean castAcidBalls(int dir) {
		Spells.fireBall(Constants.GF_ACID, dir,
				Player.y, Player.x, 60, "Acid Ball");
		return true;
	}
	
	private static void castWonder(IntPointer flags) {
		flags.value(1 << (Rnd.randomInt(23) - 1));
	}
}
