/*
 * Magic.java: code for mage spells
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
import net.jonhopkins.moria.types.SpellType;

public class Magic {
	
	private Magic() { }
	
	/**
	 * Throw a magic spell -RAK-
	 */
	public static void cast() {
		Variable.freeTurnFlag = true;
		if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see to read your spell book!");
			return;
		}
		
		if (Moria1.playerHasNoLight()) {
			IO.printMessage("You have no light to read by.");
			return;
		}
		
		if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused.");
			return;
		}
		
		if (Player.Class[Player.py.misc.playerClass].spell != Constants.MAGE) {
			IO.printMessage("You can't cast spells!");
			return;
		}
		
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything!");
			return;
		}
		
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		if (!Misc3.findRange(Constants.TV_MAGIC_BOOK, Constants.TV_NEVER, first, last)) {
			IO.printMessage("But you are not carrying any spell-books!");
			return;
		}
		
		IntPointer index = new IntPointer();
		if (!Moria1.getItemId(index, "Use which spell-book?", first.value(), last.value(), "", "")) {
			return;
		}
		
		IntPointer choice = new IntPointer();
		IntPointer chance = new IntPointer();
		int result = Moria3.castSpell("Cast which spell?", index.value(), choice, chance);
		if (result < 0) {
			IO.printMessage("You don't know any spells in that book.");
			return;
		}
		
		if (result == 0) {
			return;
		}
		
		SpellType spell = Player.magicSpell[Player.py.misc.playerClass - 1][choice.value()];
		Variable.freeTurnFlag = false;
		
		if (Rnd.randomInt(100) < chance.value()) {
			IO.printMessage("You failed to get the spell off!");
		} else {
			// Spells
			switch (choice.value() + 1) {
			case 1:
				castMagicMissile();
				break;
			case 2:
				castDetectMonsters();
				break;
			case 3:
				castPhaseDoor();
				break;
			case 4:
				castLightArea();
				break;
			case 5:
				castCureLightWounds();
				break;
			case 6:
				castFindHiddenTrapsAndDoors();
				break;
			case 7:
				castStinkingCloud();
				break;
			case 8:
				castConfusion();
				break;
			case 9:
				castLightningBolt();
				break;
			case 10:
				castTrapAndDoorDestruction();
				break;
			case 11:
				castSleepI();
				break;
			case 12:
				castCurePoison();
				break;
			case 13:
				castTeleportSelf();
				break;
			case 14:
				castRemoveCurse();
				break;
			case 15:
				castFrostBolt();
				break;
			case 16:
				castTurnStoneToMud();
				break;
			case 17:
				castCreateFood();
				break;
			case 18:
				castRechargeItemI();
				break;
			case 19:
				castSleepII();
				break;
			case 20:
				castPolymorphOther();
				break;
			case 21:
				castIdentify();
				break;
			case 22:
				castSleepIII();
				break;
			case 23:
				castFireBolt();
				break;
			case 24:
				castSlowMonster();
				break;
			case 25:
				castFrostBall();
				break;
			case 26:
				castRechargeItemII();
				break;
			case 27:
				castTeleportOther();
				break;
			case 28:
				castHasteSelf();
				break;
			case 29:
				castFireBall();
				break;
			case 30:
				castWordOfDestruction();
				break;
			case 31:
				castGenocide();
				break;
			default:
				break;
			}
			
			if (!Variable.freeTurnFlag) {
				PlayerMisc misc = Player.py.misc;
				if ((Player.spellWorked & (1L << choice.value())) == 0) {
					misc.currExp += spell.expGained << 2;
					Player.spellWorked |= (1L << choice.value());
					Misc3.printExperience();
				}
			}
		}
		
		if (!Variable.freeTurnFlag) {
			PlayerMisc misc = Player.py.misc;
			if (spell.manaCost > misc.currMana) {
				IO.printMessage("You faint from the effort!");
				Player.py.flags.paralysis = Rnd.randomInt(
						(5 * (spell.manaCost - misc.currMana)));
				misc.currMana = 0;
				misc.currManaFraction = 0;
				if (Rnd.randomInt(3) == 1) {
					IO.printMessage("You have damaged your health!");
					Misc3.decreaseStat(Constants.A_CON);
				}
			} else {
				misc.currMana -= spell.manaCost;
			}
			Misc3.printCurrentMana();
		}
	}
	
	private static void castMagicMissile() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBolt(Constants.GF_MAGIC_MISSILE, dir.value(),
					Player.y, Player.x, Misc1.damageRoll(2, 6),
					Player.spellNames[0]);
		}
	}
	
	private static void castDetectMonsters() {
		Spells.detectMonsters();
	}
	
	private static void castPhaseDoor() {
		Misc3.teleport(10);
	}
	
	private static void castLightArea() {
		Spells.lightArea(Player.y, Player.x);
	}
	
	private static void castCureLightWounds() {
		Spells.changePlayerHitpoints(Misc1.damageRoll(4, 4));
	}
	
	private static void castFindHiddenTrapsAndDoors() {
		Spells.detectSecretDoors();
		Spells.detectTrap();
	}
	
	private static void castStinkingCloud() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBall(Constants.GF_POISON_GAS, dir.value(),
					Player.y, Player.x, 12, Player.spellNames[6]);
		}
	}
	
	private static void castConfusion() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.confuseMonster(dir.value(), Player.y, Player.x);
		}
	}
	
	private static void castLightningBolt() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBolt(Constants.GF_LIGHTNING, dir.value(),
					Player.y, Player.x, Misc1.damageRoll(4, 8),
					Player.spellNames[8]);
		}
	}
	
	private static void castTrapAndDoorDestruction() {
		Spells.destroyTrapsAndDoors();
	}
	
	private static void castSleepI() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.sleepMonster(dir.value(), Player.y, Player.x);
		}
	}
	
	private static void castCurePoison() {
		Spells.curePoison();
	}
	
	private static void castTeleportSelf() {
		Misc3.teleport((Player.py.misc.level * 5));
	}
	
	private static void castRemoveCurse() {
		for (int i = 22; i < Constants.INVEN_ARRAY_SIZE; i++) {
			InvenType item = Treasure.inventory[i];
			item.flags &= ~Constants.TR_CURSED;
		}
	}
	
	private static void castFrostBolt() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBolt(Constants.GF_FROST, dir.value(),
					Player.y, Player.x, Misc1.damageRoll(6, 8),
					Player.spellNames[14]);
		}
	}
	
	private static void castTurnStoneToMud() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.transformWallToMud(dir.value(), Player.y, Player.x);
		}
	}
	
	private static void castCreateFood() {
		Spells.createFood();
	}
	
	private static void castRechargeItemI() {
		Spells.recharge(20);
	}
	
	private static void castSleepII() {
		Spells.sleepMonsters(Player.y, Player.x);
	}
	
	private static void castPolymorphOther() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.polymorphMonster(dir.value(), Player.y, Player.x);
		}
	}
	
	private static void castIdentify() {
		Spells.identifyObject();
	}
	
	private static void castSleepIII() {
		Spells.sleepMonsters();
	}
	
	private static void castFireBolt() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBolt(Constants.GF_FIRE, dir.value(),
					Player.y, Player.x, Misc1.damageRoll(9, 8),
					Player.spellNames[22]);
		}
	}
	
	private static void castSlowMonster() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.speedMonster(dir.value(), Player.y, Player.x, -1);
		}
	}
	
	private static void castFrostBall() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBall(Constants.GF_FROST, dir.value(),
					Player.y, Player.x, 48, Player.spellNames[24]);
		}
	}
	
	private static void castRechargeItemII() {
		Spells.recharge(60);
	}
	
	private static void castTeleportOther() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.teleportMonsters(dir.value(), Player.y, Player.x);
		}
	}
	
	private static void castHasteSelf() {
		PlayerFlags flags = Player.py.flags;
		flags.fast += Rnd.randomInt(20) + Player.py.misc.level;
	}
	
	private static void castFireBall() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBall(Constants.GF_FIRE, dir.value(),
					Player.y, Player.x, 72, Player.spellNames[28]);
		}
	}
	
	private static void castWordOfDestruction() {
		Spells.destroyArea(Player.y, Player.x);
	}
	
	private static void castGenocide() {
		Spells.genocide();
	}
}
