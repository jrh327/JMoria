/*
 * Scrolls.java: scroll code
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

public class Scrolls {
	
	private Scrolls() { }
	
	/**
	 * Scrolls for the reading -RAK-
	 */
	public static void readScroll() {
		Variable.freeTurnFlag = true;
		if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see to read the scroll.");
			return;
		}
		
		if (Moria1.playerHasNoLight()) {
			IO.printMessage("You have no light to read by.");
			return;
		}
		
		if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused to read a scroll.");
			return;
		}
		
		if (Treasure.invenCounter == 0) {
			IO.printMessage("You are not carrying anything!");
			return;
		}
		
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		if (!Misc3.findRange(Constants.TV_SCROLL1, Constants.TV_SCROLL2, first, last)) {
			IO.printMessage("You are not carrying any scrolls!");
			return;
		}
		
		IntPointer index = new IntPointer();
		if (!Moria1.getItemId(index, "Read which scroll?", first.value(), last.value(), "", "")) {
			return;
		}
		
		InvenType scroll = Treasure.inventory[index.value()];
		Variable.freeTurnFlag = false;
		boolean used_up = true;
		IntPointer flags = new IntPointer(scroll.flags);
		boolean identified = false;
		
		while (flags.value() != 0) {
			int scrollType = Misc1.firstBitPos(flags) + 1;
			if (scroll.category == Constants.TV_SCROLL2) {
				scrollType += 32;
			}
			
			// Scrolls
			switch (scrollType) {
			case 1:
				if (readEnchantWeaponToHit()) {
					identified = true;
				}
				break;
			case 2:
				if (readEnchantWeaponToDam()) {
					identified = true;
				}
				break;
			case 3:
				if (readEnchantArmor()) {
					identified = true;
				}
				break;
			case 4:
				used_up = readIdentify();
				
				// TODO
				// The identify may merge objects, causing the identify scroll
				// to move to a different place. Check for that here. It can
				// move arbitrarily far if an identify scroll was used on
				// another identify scroll, but it always moves down.
				while (scroll.category != Constants.TV_SCROLL1 || scroll.flags != 0x00000008) {
					index.value(index.value() - 1);
					scroll = Treasure.inventory[index.value()];
				}
				
				identified = true;
				break;
			case 5:
				if (readRemoveCurse()) {
					identified = true;
				}
				break;
			case 6:
				identified = readLight();
				break;
			case 7:
				identified |= readSummonMonster();
				break;
			case 8:
				identified = readPhaseDoor();
				break;
			case 9:
				identified = readTeleport();
				break;
			case 10:
				identified = readTeleportLevel();
				break;
			case 11:
				if (readMonsterConfusion()) {
					identified = true;
				}
				break;
			case 12:
				identified = readMagicMapping();
				break;
			case 13:
				identified = readSleepMonster();
				break;
			case 14:
				identified = readRuneOfProtection();
				break;
			case 15:
				identified = readTreasureDetection();
				break;
			case 16:
				identified = readObjectDetection();
				break;
			case 17:
				identified = readTrapDetection();
				break;
			case 18:
				identified = readDoorAndStairLocation();
				break;
			case 19:
				identified = readMassGenocide();
				break;
			case 20:
				identified = readDetectInvisible();
				break;
			case 21:
				identified = readAggravateMonster();
				break;
			case 22:
				identified = readTrapCreation();
				break;
			case 23:
				identified = readTrapAndDoorDestruction();
				break;
			case 24:
				identified = readDoorCreation();
				break;
			case 25:
				used_up = readRecharging();
				identified = true;
				break;
			case 26:
				identified = readGenocide();
				break;
			case 27:
				identified = readDarkness();
				break;
			case 28:
				identified = readProtectionFromEvil();
				break;
			case 29:
				identified = readCreateFood();
				break;
			case 30:
				identified = readDispelUndead();
				break;
			case 33:
				identified = readEnchantWeapon();
				break;
			case 34:
				if (readCurseWeapon()) {
					identified = true;
				}
				break;
			case 35:
				readEnchantArmorII();
				break;
			case 36:
				identified = readCurseArmor();
				break;
			case 37:
				identified |= readSummonUndead();
				break;
			case 38:
				identified = readBlessing();
				break;
			case 39:
				identified = readHolyChant();
				break;
			case 40:
				identified = readHolyPrayer();
				break;
			case 41:
				identified = readWordOfRecall();
				break;
			case 42:
				identified = readDestruction();
				break;
			default:
				IO.printMessage("Internal error in scroll()");
				break;
			}
		}
		
		scroll = Treasure.inventory[index.value()];
		if (identified) {
			if (!Desc.isKnownByPlayer(scroll)) {
				PlayerMisc misc = Player.py.misc;
				// round half-way case up
				misc.currExp += (scroll.level + (misc.level >> 1)) / misc.level;
				Misc3.printExperience();
				
				Desc.identify(index);
				scroll = Treasure.inventory[index.value()];
			}
		} else if (!Desc.isKnownByPlayer(scroll)) {
			Desc.sample(scroll);
		}
		
		if (used_up) {
			Desc.describeRemaining(index.value());
			Misc3.destroyInvenItem(index.value());
		}
	}
	
	private static boolean readEnchantWeaponToHit() {
		InvenType weapon = Treasure.inventory[Constants.INVEN_WIELD];
		if (weapon.category == Constants.TV_NOTHING) {
			return false;
		}
		
		String weaponName = Desc.describeObject(weapon, false);
		IO.printMessage(String.format("Your %s glows faintly!", weaponName));
		IntPointer plusses = new IntPointer(weapon.tohit);
		boolean enchant = Spells.enchant(plusses, 10);
		weapon.tohit = plusses.value();
		if (enchant) {
			weapon.flags &= ~Constants.TR_CURSED;
			Moria1.calcBonuses();
		} else {
			IO.printMessage("The enchantment fails.");
		}
		
		return true;
	}
	
	private static boolean readEnchantWeaponToDam() {
		InvenType weapon = Treasure.inventory[Constants.INVEN_WIELD];
		if (weapon.category == Constants.TV_NOTHING) {
			return false;
		}
		
		String weaponName = Desc.describeObject(weapon, false);
		IO.printMessage(String.format("Your %s glows faintly!", weaponName));
		int limit = 0;
		if ((weapon.category >= Constants.TV_HAFTED)
				&& (weapon.category <= Constants.TV_DIGGING)) {
			limit = weapon.damage[0] * weapon.damage[1];
		} else {
			// Bows' and arrows' enchantments should not be limited
			// by their low base damages
			limit = 10;
		}
		IntPointer plusses = new IntPointer(weapon.plusToDam);
		boolean enchant = Spells.enchant(plusses, limit);
		weapon.plusToDam = plusses.value();
		if (enchant) {
			weapon.flags &= ~Constants.TR_CURSED;
			Moria1.calcBonuses();
		} else {
			IO.printMessage("The enchantment fails.");
		}
		
		return true;
	}
	
	private static boolean readEnchantArmor() {
		int index = 0;
		int[] armorSlots = new int[6];
		if (Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_BODY;
		}
		if (Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_ARM;
		}
		if (Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_OUTER;
		}
		if (Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_HANDS;
		}
		if (Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_HEAD;
		}
		// also enchant boots
		if (Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_FEET;
		}
		
		int armorType = 0;
		if (index > 0) {
			armorType = armorSlots[Rnd.randomInt(index) - 1];
		}
		if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_BODY].flags) != 0) {
			armorType = Constants.INVEN_BODY;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_ARM].flags) != 0) {
			armorType = Constants.INVEN_ARM;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_OUTER].flags) != 0) {
			armorType = Constants.INVEN_OUTER;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HEAD].flags) != 0) {
			armorType = Constants.INVEN_HEAD;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HANDS].flags) != 0) {
			armorType = Constants.INVEN_HANDS;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_FEET].flags) != 0) {
			armorType = Constants.INVEN_FEET;
		}
		
		if (armorType == 0) {
			return false;
		}
		
		InvenType armor = Treasure.inventory[armorType];
		String armorName = Desc.describeObject(armor, false);
		IO.printMessage(String.format("Your %s glows faintly!", armorName));
		IntPointer plusses = new IntPointer(armor.plusToArmorClass);
		boolean enchanted = Spells.enchant(plusses, 10);
		armor.plusToArmorClass = plusses.value();
		if (enchanted) {
			armor.flags &= ~Constants.TR_CURSED;
			Moria1.calcBonuses();
		} else {
			IO.printMessage("The enchantment fails.");
		}
		
		return true;
	}
	
	private static boolean readIdentify() {
		IO.printMessage("This is an identify scroll.");
		return Spells.identifyObject();
	}
	
	private static boolean readRemoveCurse() {
		if (Spells.removeCurse()) {
			IO.printMessage("You feel as if someone is watching over you.");
			return true;
		}
		return false;
	}
	
	private static boolean readLight() {
		return Spells.lightArea(Player.y, Player.x);
	}
	
	private static boolean readSummonMonster() {
		boolean summoned = false;
		for (int i = 0; i < Rnd.randomInt(3); i++) {
			IntPointer y = new IntPointer(Player.y);
			IntPointer x = new IntPointer(Player.x);
			summoned |= Misc1.summonMonster(y, x, false);
		}
		return summoned;
	}
	
	private static boolean readPhaseDoor() {
		Misc3.teleport(10);
		return true;
	}
	
	private static boolean readTeleport() {
		Misc3.teleport(100);
		return true;
	}
	
	private static boolean readTeleportLevel() {
		Variable.dungeonLevel += (-3) + 2 * Rnd.randomInt(2);
		if (Variable.dungeonLevel < 1) {
			Variable.dungeonLevel = 1;
		}
		Variable.newLevelFlag = true;
		return true;
	}
	
	private static boolean readMonsterConfusion() {
		PlayerFlags flags = Player.py.flags;
		if (!flags.confuseMonster) {
			IO.printMessage("Your hands begin to glow.");
			flags.confuseMonster = true;
			return true;
		}
		return false;
	}
	
	private static boolean readMagicMapping() {
		Spells.mapArea();
		return true;
	}
	
	private static boolean readSleepMonster() {
		return Spells.sleepMonsters(Player.y, Player.x);
	}
	
	private static boolean readRuneOfProtection() {
		Spells.wardingGlyph();
		return true;
	}
	
	private static boolean readTreasureDetection() {
		return Spells.detectTreasure();
	}
	
	private static boolean readObjectDetection() {
		return Spells.detectObject();
	}
	
	private static boolean readTrapDetection() {
		return Spells.detectTrap();
	}
	
	private static boolean readDoorAndStairLocation() {
		return Spells.detectSecretDoors();
	}
	
	private static boolean readMassGenocide() {
		IO.printMessage("This is a mass genocide scroll.");
		Spells.massGenocide();
		return true;
	}
	
	private static boolean readDetectInvisible() {
		return Spells.detectInvisibleCreatures();
	}
	
	private static boolean readAggravateMonster() {
		IO.printMessage("There is a high pitched humming noise.");
		Spells.aggravateMonster(20);
		return true;
	}
	
	private static boolean readTrapCreation() {
		return Spells.createTraps();
	}
	
	private static boolean readTrapAndDoorDestruction() {
		return Spells.destroyTrapsAndDoors();
	}
	
	private static boolean readDoorCreation() {
		return Spells.createDoors();
	}
	
	// TODO
	private static boolean readRecharging() {
		IO.printMessage("This is a Recharge-Item scroll.");
		boolean used = Spells.recharge(60);
		return used;
	}
	
	private static boolean readGenocide() {
		IO.printMessage("This is a genocide scroll.");
		Spells.genocide();
		return true;
	}
	
	private static boolean readDarkness() {
		return Spells.unlightArea(Player.y, Player.x);
	}
	
	private static boolean readProtectionFromEvil() {
		return Spells.protectFromEvil();
	}
	
	private static boolean readCreateFood() {
		Spells.createFood();
		return true;
	}
	
	private static boolean readDispelUndead() {
		return Spells.dispelCreature(Constants.CD_UNDEAD, 60);
	}
	
	private static boolean readEnchantWeapon() {
		InvenType weapon = Treasure.inventory[Constants.INVEN_WIELD];
		if (weapon.category == Constants.TV_NOTHING) {
			return false;
		}
		
		String weaponName = Desc.describeObject(weapon, false);
		IO.printMessage(String.format("Your %s glows brightly!", weaponName));
		boolean flag = false;
		for (int i = 0; i < Rnd.randomInt(2); i++) {
			IntPointer plusses = new IntPointer(weapon.tohit);
			boolean enchant = Spells.enchant(plusses, 10);
			weapon.tohit = plusses.value();
			if (enchant) {
				flag = true;
			}
		}
		
		int limit = 0;
		if ((weapon.category >= Constants.TV_HAFTED)
				&& (weapon.category <= Constants.TV_DIGGING)) {
			limit = weapon.damage[0] * weapon.damage[1];
		} else {
			// Bows' and arrows' enchantments should not be limited
			// by their low base damages
			limit = 10;
		}
		
		for (int i = 0; i < Rnd.randomInt(2); i++) {
			IntPointer plusses = new IntPointer(weapon.plusToDam);
			boolean enchant = Spells.enchant(plusses, limit);
			weapon.plusToDam = plusses.value();
			if (enchant) {
				flag = true;
			}
		}
		
		if (flag) {
			weapon.flags &= ~Constants.TR_CURSED;
			Moria1.calcBonuses ();
		} else {
			IO.printMessage("The enchantment fails.");
		}
		
		return true;
	}
	
	private static boolean readCurseWeapon() {
		InvenType weapon = Treasure.inventory[Constants.INVEN_WIELD];
		if (weapon.category == Constants.TV_NOTHING) {
			return false;
		}
		
		String weaponName = Desc.describeObject(weapon, false);
		IO.printMessage(String.format("Your %s glows black, fades.", weaponName));
		Desc.unmagicName(weapon);
		weapon.tohit = -Rnd.randomInt(5) - Rnd.randomInt(5);
		weapon.plusToDam = -Rnd.randomInt(5) - Rnd.randomInt(5);
		weapon.plusToArmorClass = 0;
		
		// Must call py_bonuses() before set (clear) flags, and
		// must call calc_bonuses() after set (clear) flags, so that
		// all attributes will be properly turned off.
		Moria1.adjustPlayerBonuses(weapon, -1);
		weapon.flags = Constants.TR_CURSED;
		Moria1.calcBonuses();
		return true;
	}
	
	private static boolean readEnchantArmorII() {
		int index = 0;
		int[] armorSlots = new int[6];
		if (Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_BODY;
		}
		if (Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_ARM;
		}
		if (Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_OUTER;
		}
		if (Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_HANDS;
		}
		if (Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_HEAD;
		}
		// also enchant boots
		if (Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) {
			armorSlots[index++] = Constants.INVEN_FEET;
		}
		
		int armorType = 0;
		if (index > 0){
			armorType = armorSlots[Rnd.randomInt(index) - 1];
		}
		
		if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_BODY].flags) != 0) {
			armorType = Constants.INVEN_BODY;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_ARM].flags) != 0) {
			armorType = Constants.INVEN_ARM;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_OUTER].flags) != 0) {
			armorType = Constants.INVEN_OUTER;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HEAD].flags) != 0) {
			armorType = Constants.INVEN_HEAD;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HANDS].flags) != 0) {
			armorType = Constants.INVEN_HANDS;
		} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_FEET].flags) != 0) {
			armorType = Constants.INVEN_FEET;
		}
		
		if (armorType == 0) {
			return false;
		}
		
		InvenType armor = Treasure.inventory[armorType];
		String armorName = Desc.describeObject(armor, false);
		IO.printMessage(String.format("Your %s glows brightly!", armorName));
		boolean cursed = false;
		
		for (int i = 0; i < Rnd.randomInt(2) + 1; i++) {
			IntPointer plusses = new IntPointer(armor.plusToArmorClass);
			boolean enchanted = Spells.enchant(plusses, 10);
			armor.plusToArmorClass = plusses.value();
			if (enchanted) {
				cursed = true;
			}
		}
		
		if (cursed) {
			armor.flags &= ~Constants.TR_CURSED;
			Moria1.calcBonuses();
		} else {
			IO.printMessage("The enchantment fails.");
		}
		
		return true;
	}
	
	private static boolean readCurseArmor() {
		int armorType = 0;
		if ((Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING)
				&& (Rnd.randomInt(4) == 1)) {
			armorType = Constants.INVEN_BODY;
		} else if ((Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING)
				&& (Rnd.randomInt(3) == 1)) {
			armorType = Constants.INVEN_ARM;
		} else if ((Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING)
				&& (Rnd.randomInt(3) == 1)) {
			armorType = Constants.INVEN_OUTER;
		} else if ((Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING)
				&& (Rnd.randomInt(3) == 1)) {
			armorType = Constants.INVEN_HEAD;
		} else if ((Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING)
				&& (Rnd.randomInt(3) == 1)) {
			armorType = Constants.INVEN_HANDS;
		} else if ((Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING)
				&& (Rnd.randomInt(3) == 1)) {
			armorType = Constants.INVEN_FEET;
		} else if (Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) {
			armorType = Constants.INVEN_BODY;
		} else if (Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) {
			armorType = Constants.INVEN_ARM;
		} else if (Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) {
			armorType = Constants.INVEN_OUTER;
		} else if (Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) {
			armorType = Constants.INVEN_HEAD;
		} else if (Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) {
			armorType = Constants.INVEN_HANDS;
		} else if (Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) {
			armorType = Constants.INVEN_FEET;
		} else {
			armorType = 0;
		}
		
		if (armorType == 0) {
			return false;
		}
		
		InvenType armor = Treasure.inventory[armorType];
		String armorName = Desc.describeObject(armor, false);
		IO.printMessage(String.format("Your %s glows black, fades.", armorName));
		Desc.unmagicName(armor);
		armor.flags = Constants.TR_CURSED;
		armor.tohit = 0;
		armor.plusToDam = 0;
		armor.plusToArmorClass = -Rnd.randomInt(5) - Rnd.randomInt(5);
		Moria1.calcBonuses();
		return true;
	}
	
	private static boolean readSummonUndead() {
		boolean summoned = false;
		for (int i = 0; i < Rnd.randomInt(3); i++) {
			IntPointer y = new IntPointer(Player.y);
			IntPointer x = new IntPointer(Player.x);
			summoned |= Misc1.summonUndead(y, x);
		}
		return summoned;
	}
	
	private static boolean readBlessing() {
		Spells.bless(Rnd.randomInt(12) + 6);
		return true;
	}
	
	private static boolean readHolyChant() {
		Spells.bless(Rnd.randomInt(24) + 12);
		return true;
	}
	
	private static boolean readHolyPrayer() {
		Spells.bless(Rnd.randomInt(48) + 24);
		return true;
	}
	
	private static boolean readWordOfRecall() {
		PlayerFlags flags = Player.py.flags;
		if (flags.wordRecall == 0) {
			flags.wordRecall = 25 + Rnd.randomInt(30);
		}
		IO.printMessage("The air about you becomes charged.");
		return true;
	}
	
	private static boolean readDestruction() {
		Spells.destroyArea(Player.y, Player.x);
		return true;
	}
}
