/*
 * Prayer.java: code for priest spells
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

public class Prayer {
	
	private Prayer() { }
	
	/**
	 * Pray like HELL. -RAK-
	 */
	public static void pray() {
		Variable.freeTurnFlag = true;
		if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see to read your prayer!");
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
		
		if (Player.Class[Player.py.misc.playerClass].spell != Constants.PRIEST) {
			IO.printMessage("Pray hard enough and your prayers may be answered.");
			return;
		}
		
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything!");
			return;
		}
		
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		if (!Misc3.findRange(Constants.TV_PRAYER_BOOK, Constants.TV_NEVER, first, last)) {
			IO.printMessage("You are not carrying any Holy Books!");
			return;
		}
		
		IntPointer index = new IntPointer();
		if (!Moria1.getItemId(index, "Use which Holy Book?", first.value(), last.value(), null, "")) {
			return;
		}
		
		if (!Moria3.checkSpellBook(index.value())) {
			IO.printMessage("You don't know any prayers in that book.");
			return;
		}
		
		IntPointer choice = new IntPointer();
		IntPointer chance = new IntPointer();
		int result = Moria3.castSpell("Recite which prayer?", index.value());
		if (result == 0) {
			return;
		}
		
		SpellType prayer = Player.magicSpell[Player.py.misc.playerClass - 1][choice.value()];
		Variable.freeTurnFlag = false;
		
		if (Rnd.randomInt(100) < Misc3.spellFailChance(result)) {
			IO.printMessage("You lost your concentration!");
		} else {
			// Prayers
			switch (choice.value() + 1) {
			case 1:
				prayDetectEvil();
				break;
			case 2:
				prayCureLightWounds();
				break;
			case 3:
				prayBless();
				break;
			case 4:
				prayRemoveFear();
				break;
			case 5:
				prayCallLight();
				break;
			case 6:
				prayFindTraps();
				break;
			case 7:
				prayDetectDoorsAndStairs();
				break;
			case 8:
				praySlowPoison();
				break;
			case 9:
				prayBlindCreature();
				break;
			case 10:
				prayPortal();
				break;
			case 11:
				prayCureMediumWounds();
				break;
			case 12:
				prayChant();
				break;
			case 13:
				praySanctuary();
				break;
			case 14:
				prayCreateFood();
				break;
			case 15:
				prayRemoveCurse();
				break;
			case 16:
				prayResistHeatAndCold();
				break;
			case 17:
				prayNeutralizePoison();
				break;
			case 18:
				prayOrbOfDraining();
				break;
			case 19:
				prayCureSeriousWounds();
				break;
			case 20:
				praySenseInvisible();
				break;
			case 21:
				prayProtectionFromEvil();
				break;
			case 22:
				prayEarthquake();
				break;
			case 23:
				praySenseSurroundings();
				break;
			case 24:
				prayCureCriticalWounds();
				break;
			case 25:
				prayTurnUndead();
				break;
			case 26:
				prayPrayer();
				break;
			case 27:
				prayDispelUndead();
				break;
			case 28:
				prayHeal();
				break;
			case 29:
				prayDispelEvil();
				break;
			case 30:
				prayGlyphOfWarding();
				break;
			case 31:
				prayHolyWord();
				break;
			default:
				break;
			}
			
			if (!Variable.freeTurnFlag) {
				PlayerMisc misc = Player.py.misc;
				if ((Player.spellWorked & (1L << choice.value())) == 0) {
					misc.currExp += prayer.expGained << 2;
					Player.spellWorked |= (1L << choice.value());
					Misc3.printExperience();
				}
			}
		}
		
		if (!Variable.freeTurnFlag) {
			PlayerMisc misc = Player.py.misc;
			if (prayer.manaCost > misc.currMana) {
				IO.printMessage("You faint from fatigue!");
				Player.py.flags.paralysis = Rnd.randomInt(
						(5 * (prayer.manaCost - misc.currMana)));
				misc.currMana = 0;
				misc.currManaFraction = 0;
				if (Rnd.randomInt(3) == 1) {
					IO.printMessage("You have damaged your health!");
					Misc3.decreaseStat(Constants.A_CON);
				}
			} else {
				misc.currMana -= prayer.manaCost;
			}
			Misc3.printCurrentMana();
		}
	}
	
	private static void prayDetectEvil() {
		Spells.detectEvil();
	}
	
	private static void prayCureLightWounds() {
		Spells.changePlayerHitpoints(Misc1.damageRoll(3, 3));
	}
	
	private static void prayBless() {
		Spells.bless(Rnd.randomInt(12) + 12);
	}
	
	private static void prayRemoveFear() {
		Spells.removeFear();
	}
	
	private static void prayCallLight() {
		Spells.lightArea(Player.y, Player.x);
	}
	
	private static void prayFindTraps() {
		Spells.detectTrap();
	}
	
	private static void prayDetectDoorsAndStairs() {
		Spells.detectSecretDoors();
	}
	
	private static void praySlowPoison() {
		Spells.slowPoison();
	}
	
	private static void prayBlindCreature() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.confuseMonster(dir.value(), Player.y, Player.x);
		}
	}
	
	private static void prayPortal() {
		Misc3.teleport((Player.py.misc.level * 3));
	}
	
	private static void prayCureMediumWounds() {
		Spells.changePlayerHitpoints(Misc1.damageRoll(4, 4));
	}
	
	private static void prayChant() {
		Spells.bless(Rnd.randomInt(24) + 24);
	}
	
	private static void praySanctuary() {
		Spells.sleepMonsters(Player.y, Player.x);
	}
	
	private static void prayCreateFood() {
		Spells.createFood();
	}
	
	private static void prayRemoveCurse() {
		for (int i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
			InvenType item = Treasure.inventory[i];
			// only clear flag for items that are wielded or worn
			if (item.category >= Constants.TV_MIN_WEAR
					&& item.category <= Constants.TV_MAX_WEAR) {
				item.flags &= ~Constants.TR_CURSED;
			}
		}
	}
	
	private static void prayResistHeatAndCold() {
		PlayerFlags flags = Player.py.flags;
		flags.resistHeat += Rnd.randomInt(10) + 10;
		flags.resistCold += Rnd.randomInt(10) + 10;
	}
	
	private static void prayNeutralizePoison() {
		Spells.curePoison();
	}
	
	private static void prayOrbOfDraining() {
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Spells.fireBall(Constants.GF_HOLY_ORB, dir.value(),
					Player.y, Player.x,
					(Misc1.damageRoll(3, 6) + Player.py.misc.level),
					"Black Sphere");
		}
	}
	
	private static void prayCureSeriousWounds() {
		Spells.changePlayerHitpoints(Misc1.damageRoll(8, 4));
	}
	
	private static void praySenseInvisible() {
		Spells.detectInvisibleMonsters(Rnd.randomInt(24) + 24);
	}
	
	private static void prayProtectionFromEvil() {
		Spells.protectFromEvil();
	}
	
	private static void prayEarthquake() {
		Spells.earthquake();
	}
	
	private static void praySenseSurroundings() {
		Spells.mapArea();
	}
	
	private static void prayCureCriticalWounds() {
		Spells.changePlayerHitpoints(Misc1.damageRoll(16, 4));
	}
	
	private static void prayTurnUndead() {
		Spells.turnUndead();
	}
	
	private static void prayPrayer() {
		Spells.bless(Rnd.randomInt(48) + 48);
	}
	
	private static void prayDispelUndead() {
		Spells.dispelCreature(Constants.CD_UNDEAD, (3 * Player.py.misc.level));
	}
	
	private static void prayHeal() {
		Spells.changePlayerHitpoints(200);
	}
	
	private static void prayDispelEvil() {
		Spells.dispelCreature(Constants.CD_EVIL, (3 * Player.py.misc.level));
	}
	
	private static void prayGlyphOfWarding() {
		Spells.wardingGlyph();
	}
	
	private static void prayHolyWord() {
		Spells.removeFear();
		Spells.curePoison();
		Spells.changePlayerHitpoints(1000);
		
		for (int i = Constants.A_STR; i <= Constants.A_CHR; i++) {
			Misc3.restoreStat(i);
		}
		
		Spells.dispelCreature(Constants.CD_EVIL, 4 * Player.py.misc.level);
		Spells.turnUndead();
		
		if (Player.py.flags.invulnerability < 3) {
			Player.py.flags.invulnerability = 3;
		} else {
			Player.py.flags.invulnerability++;
		}
	}
}
