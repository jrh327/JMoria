/*
 * Moria3.java: misc code, mainly to handle player commands
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

import java.util.Arrays;

import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;
import net.jonhopkins.moria.types.SpellType;

public class Moria3 {
	
	private Moria3() { }
	
	/**
	 * Player hit a trap. -RAK-
	 * 
	 * @param y The y-coordinate of the trap
	 * @param x The x-coordinate of the trap
	 */
	/* Player hit a trap.	(Chuckle)			-RAK-	*/
	public static void tripTrap(int y, int x) {
		Moria2.endFind();
		Moria2.revealTrap(y, x);
		CaveType cavePos = Variable.cave[y][x];
		PlayerMisc misc = Player.py.misc;
		InvenType trap = Treasure.treasureList[cavePos.treasureIndex];
		int damage = Misc1.pDamageRoll(trap.damage);
		switch (trap.subCategory) {
		case 1: // Open pit
			IO.printMessage("You fell into a pit!");
			if (Player.py.flags.freeFall > 0) {
				IO.printMessage("You gently float down.");
			} else {
				String trapDesc = Desc.describeObject(trap, true);
				Moria1.takeHit(damage, trapDesc);
			}
			break;
		case 2: // Arrow trap
			if (Moria1.testHit(125, 0, 0, misc.totalArmorClass+misc.magicArmorClass, Constants.CLA_MISC_HIT)) {
				String trapDesc = Desc.describeObject(trap, true);
				Moria1.takeHit(damage, trapDesc);
				IO.printMessage("An arrow hits you.");
			} else {
				IO.printMessage("An arrow barely misses you.");
			}
			break;
		case 3: // Covered pit
			IO.printMessage("You fell into a covered pit.");
			if (Player.py.flags.freeFall > 0) {
				IO.printMessage("You gently float down.");
			} else {
				String trapDesc = Desc.describeObject(trap, true);
				Moria1.takeHit(damage, trapDesc);
			}
			Misc3.placeTrap(y, x, 0);
			break;
		case 4: // Trap door
			IO.printMessage("You fell through a trap door!");
			Variable.newLevelFlag = true;
			Variable.dungeonLevel++;
			if (Player.py.flags.freeFall > 0) {
				IO.printMessage("You gently float down.");
			} else {
				String trapDesc = Desc.describeObject(trap, true);
				Moria1.takeHit(damage, trapDesc);
			}
			// Force the messages to display before starting to generate the
			// next level.
			IO.printMessage("");
			break;
		case 5: // Sleep gas
			if (Player.py.flags.paralysis == 0) {
				IO.printMessage("A strange white mist surrounds you!");
				if (Player.py.flags.freeAct) {
					IO.printMessage("You are unaffected.");
				} else {
					IO.printMessage("You fall asleep.");
					Player.py.flags.paralysis += Rnd.randomInt(10) + 4;
				}
			}
			break;
		case 6: // Hidden Object
			deleteObject(y, x);
			Misc3.placeObject(y, x, false);
			IO.printMessage("Hmmm, there was something under this rock.");
			break;
		case 7: // STR Dart
			if (Moria1.testHit(125, 0, 0, misc.totalArmorClass+misc.magicArmorClass, Constants.CLA_MISC_HIT)) {
				if (!Player.py.flags.sustainStr) {
					Misc3.decreaseStat(Constants.A_STR);
					String trapDesc = Desc.describeObject(trap, true);
					Moria1.takeHit(damage, trapDesc);
					IO.printMessage("A small dart weakens you!");
				} else {
					IO.printMessage("A small dart hits you.");
				}
			} else {
				IO.printMessage("A small dart barely misses you.");
			}
			break;
		case 8: // Teleport
			Variable.teleportFlag = true;
			IO.printMessage("You hit a teleport trap!");
			// Light up the teleport trap, before we teleport away.
			Moria1.moveLight(y, x, y, x);
			break;
		case 9: // Rockfall
			Moria1.takeHit(damage, "a falling rock");
			deleteObject(y, x);
			Misc3.placeRubble(y, x);
			IO.printMessage("You are hit by falling rock.");
			break;
		case 10: // Corrode gas
			// Makes more sense to print the message first,
			// then damage an object.
			IO.printMessage("A strange red gas surrounds you.");
			Moria2.corrodeGas("corrosion gas");
			break;
		case 11: // Summon monster
			deleteObject(y, x);	// Rune disappears.
			int numMonsters = 2 + Rnd.randomInt (3);
			for (int i = 0; i < numMonsters; i++) {
				IntPointer ty = new IntPointer(y);
				IntPointer tx = new IntPointer(x);
				Misc1.summonMonster(ty, tx, false);
			}
			break;
		case 12: // Fire trap
			IO.printMessage("You are enveloped in flames!");
			Moria2.fireDamage(damage, "a fire trap");
			break;
		case 13: // Acid trap
			IO.printMessage("You are splashed with acid!");
			Moria2.acidDamage(damage, "an acid trap");
			break;
		case 14: // Poison gas
			IO.printMessage("A pungent green gas surrounds you!");
			Moria2.poisonGas(damage, "a poison gas trap");
			break;
		case 15: // Blind Gas
			IO.printMessage("A black gas surrounds you!");
			Player.py.flags.blind += Rnd.randomInt(50) + 50;
			break;
		case 16: // Confuse Gas
			IO.printMessage("A gas of scintillating colors surrounds you!");
			Player.py.flags.confused += Rnd.randomInt(15) + 15;
			break;
		case 17: // Slow Dart
			if (Moria1.testHit(125, 0, 0,
					misc.totalArmorClass + misc.magicArmorClass,
					Constants.CLA_MISC_HIT)) {
				String trapDesc = Desc.describeObject(trap, true);
				Moria1.takeHit(damage, trapDesc);
				IO.printMessage("A small dart hits you!");
				if (Player.py.flags.freeAct) {
					IO.printMessage("You are unaffected.");
				} else {
					Player.py.flags.slow += Rnd.randomInt(20) + 10;
				}
			} else {
				IO.printMessage("A small dart barely misses you.");
			}
			break;
		case 18: // CON Dart
			if (Moria1.testHit(125, 0, 0,
					misc.totalArmorClass + misc.magicArmorClass,
					Constants.CLA_MISC_HIT)) {
				if (!Player.py.flags.sustainCon) {
					Misc3.decreaseStat(Constants.A_CON);
					String trapDesc = Desc.describeObject(trap, true);
					Moria1.takeHit(damage, trapDesc);
					IO.printMessage("A small dart saps your health!");
				} else {
					IO.printMessage("A small dart hits you.");
				}
			} else {
				IO.printMessage("A small dart barely misses you.");
			}
			break;
		case 19: // Secret Door
			break;
		case 99: // Scare Mon
			break;
			// Town level traps are special, the stores.
		case 101: // General
			Store2.enterStore(0);
			break;
		case 102: // Armory
			Store2.enterStore(1);
			break;
		case 103: // Weaponsmith
			Store2.enterStore(2);
			break;
		case 104: // Temple
			Store2.enterStore(3);
			break;
		case 105: // Alchemy
			Store2.enterStore(4);
			break;
		case 106: // Magic-User
			Store2.enterStore(5);
			break;
		default:
			IO.printMessage("Unknown trap value.");
			break;
		}
	}
	
	/**
	 * Get a list of indices of spells the player has learned.
	 * 
	 * @param spellBook The spell book to check
	 * @return Array of learned spells' indices
	 */
	private static int[] getSpellsList(int spellBook) {
		IntPointer j = new IntPointer(Treasure.inventory[spellBook].flags & Player.spellLearned);
		SpellType[] classSpellBook = Player.magicSpell[Player.py.misc.playerClass - 1];
		int[] spells = new int[classSpellBook.length];
		int spellCount = 0;
		
		while (j.value() != 0) {
			int spellIndex = Misc1.firstBitPos(j);
			if (classSpellBook[spellIndex].level <= Player.py.misc.level) {
				spells[spellCount] = spellIndex;
				spellCount++;
			}
		}
		
		return Arrays.copyOf(spells, spellCount);
	}
	
	/**
	 * Check if the spell book has spells in it.
	 * 
	 * @param spellBook The spell book to check
	 * @return True if there are spells, false if spell book is empty
	 */
	public static boolean checkSpellBook(int spellBook) {
		return getSpellsList(spellBook).length > 0;
	}
	
	/**
	 * Choose a spell from the spell book.
	 * 
	 * @param prompt
	 * @param spellBook
	 * @return The index of the chosen spell, or -1 if no spell chosen
	 */
	public static int castSpell(String prompt, int spellBook) {
		IntPointer j = new IntPointer(Treasure.inventory[spellBook].flags);
		int firstSpell = Misc1.firstBitPos(j);
		int[] spells = getSpellsList(spellBook);
		
		if (spells.length == 0) {
			return -1;
		}
		
		int result = Misc3.getSpell(spells, prompt, firstSpell);
		
		// Didn't choose a valid spell
		if (result < 0) {
			return result;
		}
		
		// Valid spell and enough mana to cast it
		if (Player.magicSpell[Player.py.misc.playerClass - 1][result].manaCost < Player.py.misc.currMana) {
			return result;
		}
		
		// Not enough mana, check if the player actually wants to attempt it
		if (Player.Class[Player.py.misc.playerClass].spell == Constants.MAGE) {
			if (!IO.getCheck("You summon your limited strength to cast this one! Confirm?")) {
				result = -1;
			}
		} else {
			if (!IO.getCheck("The gods may think you presumptuous for this! Confirm?")) {
				result = -1;
			}
		}
		
		return result;
	}
	
	/**
	 * Player is on an object. Many things can happen based on the
	 * TVAL of the object. Traps are set off, money and most objects
	 * are picked up. Some objects, such as open doors, just sit there.
	 * -RAK-
	 * 
	 * @param y The y-coordinate of the object to pick up
	 * @param x The x-coordinate of the objec to pick up
	 * @param pickup Whether to automatically pick up the item
	 */
	public static void carry(int y, int x, boolean pickup) {
		CaveType cavePos = Variable.cave[y][x];
		InvenType item = Treasure.treasureList[cavePos.treasureIndex];
		int itemType = Treasure.treasureList[cavePos.treasureIndex].category;
		if (itemType <= Constants.TV_MAX_PICK_UP) {
			Moria2.endFind();
			// There's GOLD in them thar hills!
			if (itemType == Constants.TV_GOLD) {
				Player.py.misc.gold += item.cost;
				String itemDesc = Desc.describeObject(item, true);
				String msgFoundGold = String.format(
						"You have found %d gold pieces worth of %s",
						item.cost,
						itemDesc);
				Misc3.printGold();
				deleteObject(y, x);
				IO.printMessage(msgFoundGold);
			} else {
				if (Misc3.canPickUpItem(item)) { // Too many objects?
					// Okay, pick it up
					if (pickup && Variable.promptCarryFlag.value()) {
						StringBuilder itemDesc = new StringBuilder();
						itemDesc.append(Desc.describeObject(item, true));
						// change the period to a question mark
						itemDesc.setCharAt(itemDesc.length() - 1, '?');
						String msgPickUp = String.format(
								"Pick up %s",
								itemDesc.toString());
						pickup = IO.getCheck(msgPickUp);
					}
					
					// Check to see if it will change the players speed.
					if (pickup && !Misc3.checkItemWeight(item)) {
						StringBuilder itemDesc = new StringBuilder();
						itemDesc.append(Desc.describeObject(item, true));
						// change the period to a question mark
						itemDesc.setCharAt(itemDesc.length() - 1, '?');
						String msgHeavy = String.format(
								"Exceed your weight limit to pick up %s",
								itemDesc.toString());
						pickup = IO.getCheck(msgHeavy);
					}
					
					// Attempt to pick up an object.
					if (pickup) {
						int itemIndex = Misc3.pickUpItem(item);
						String itemDesc = Desc.describeObject(Treasure.inventory[itemIndex], true);
						String msgPickedUp = String.format(
								"You have %s (%c)", itemDesc, itemIndex + 'a');
						IO.printMessage(msgPickedUp);
						deleteObject(y, x);
					}
				} else {
					String itemDesc = Desc.describeObject(item, true);
					String msgCantCarry = String.format(
							"You can't carry %s", itemDesc);
					IO.printMessage(msgCantCarry);
				}
			}
		
		// OOPS!
		} else if (itemType == Constants.TV_INVIS_TRAP
				|| itemType == Constants.TV_VIS_TRAP
				|| itemType == Constants.TV_STORE_DOOR) {
			tripTrap(y, x);
		}
	}
	
	/**
	 * Deletes a monster entry from the level -RAK-
	 * 
	 * @param monsterIndex
	 */
	public static void deleteMonster(int monsterIndex) {
		MonsterType monster = Monsters.monsterList[monsterIndex];
		Variable.cave[monster.y][monster.x].creatureIndex = 0;
		if (monster.monsterLight) {
			Moria1.lightUpSpot(monster.y, monster.x);
		}
		if (monsterIndex != Monsters.freeMonsterIndex - 1) {
			monster = Monsters.monsterList[Monsters.freeMonsterIndex - 1];
			Variable.cave[monster.y][monster.x].creatureIndex = monsterIndex;
			Monsters.monsterList[Monsters.freeMonsterIndex - 1]
					.copyInto(Monsters.monsterList[monsterIndex]);
		}
		Monsters.freeMonsterIndex--;
		Monsters.getBlankMonster().copyInto(Monsters.monsterList[Monsters.freeMonsterIndex]);
		if (Monsters.totalMonsterMultiples > 0) {
			Monsters.totalMonsterMultiples--;
		}
	}
	
	/* The following two procedures implement the same function as delete monster.
	 * However, they are used within creatures(), because deleting a monster
	 * while scanning the m_list causes two problems, monsters might get two
	 * turns, and m_ptr/monptr might be invalid after the delete_monster.
	 * Hence the delete is done in two steps. */
	
	/**
	 * Does everything delete_monster does except delete the monster record
	 * and reduce mfptr. This is called in breathe, and a couple of places
	 * in Creature.java
	 * 
	 * @param monsterIndex Index of the monster to delete
	 */
	public static void deleteMonster1(int monsterIndex) {
		MonsterType monster = Monsters.monsterList[monsterIndex];
		
		// force the hp negative to ensure that the monster is dead, for example,
		// if the monster was just eaten by another, it will still have positive
		// hit points
		monster.hitpoints = -1;
		Variable.cave[monster.y][monster.x].creatureIndex = 0;
		if (monster.monsterLight) {
			Moria1.lightUpSpot(monster.y, monster.x);
		}
		if (Monsters.totalMonsterMultiples > 0) {
			Monsters.totalMonsterMultiples--;
		}
	}
	
	/**
	 * This does everything in delete_monster that wasn't done
	 * by fix1_monster_delete. This is only called in creatures().
	 * 
	 * @param monsterIndex Index of the monster to delete
	 */
	public static void deleteMonster2(int monsterIndex) {
		MonsterType monster;
		
		if (monsterIndex != Monsters.freeMonsterIndex - 1) {
			monster = Monsters.monsterList[Monsters.freeMonsterIndex - 1];
			Variable.cave[monster.y][monster.x].creatureIndex = monsterIndex;
			Monsters.monsterList[Monsters.freeMonsterIndex - 1]
					.copyInto(Monsters.monsterList[monsterIndex]);
		}
		Monsters.getBlankMonster()
				.copyInto(Monsters.monsterList[Monsters.freeMonsterIndex - 1]);
		Monsters.freeMonsterIndex--;
	}
	
	/**
	 * Creates objects nearby the coordinates given -RAK-
	 * 
	 * @param y The y-coordinate around which to summon objects
	 * @param x The x-coordinate around which to summon objects
	 * @param num Number of objects to try to summon
	 * @param itemType
	 * @return
	 */
	public static int summonObject(int y, int x, int num, int itemType) {
		int realType;
		if (itemType == 1 || itemType == 5) {
			realType = 1; // typ == 1 . objects
		} else {
			realType = 256; // typ == 2 . gold
		}
		
		int res = 0;
		for (int item = 0; item < num; item++) {
			for (int attempt = 0; attempt <= 20; attempt++) {
				int newY = y - 3 + Rnd.randomInt(5);
				int newX = x - 3 + Rnd.randomInt(5);
				if (Misc1.isInBounds(newY, newX) && Misc1.isInLineOfSight(y, x, newY, newX)) {
					CaveType cavePos = Variable.cave[newY][newX];
					if (cavePos.fval <= Constants.MAX_OPEN_SPACE && (cavePos.treasureIndex == 0)) {
						if (itemType == 3 || itemType == 7) {
							// typ == 3 . 50% objects, 50% gold
							if (Rnd.randomInt(100) < 50) {
								realType = 1;
							} else {
								realType = 256;
							}
						}
						if (realType == 1) {
							Misc3.placeObject(newY, newX, (itemType >= 4));
						} else {
							Misc3.placeGold(newY, newX);
						}
						Moria1.lightUpSpot(newY, newX);
						if (Misc1.testLight(newY, newX)) {
							res += realType;
						}
						break;
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * Deletes object from given location -RAK-
	 * 
	 * @param y The y-coordinate of the object to delete
	 * @param x The x-coordinate of the object to delete
	 * @return
	 */
	public static boolean deleteObject(int y, int x) {
		CaveType cavePos = Variable.cave[y][x];
		if (cavePos.fval == Constants.BLOCKED_FLOOR) {
			cavePos.fval = Constants.CORR_FLOOR;
		}
		
		Misc1.pusht(cavePos.treasureIndex);
		cavePos.treasureIndex = 0;
		cavePos.fieldMark = false;
		Moria1.lightUpSpot(y, x);
		
		if (Misc1.testLight(y, x)) {
			return true;
		} else {
			return false;
		}
	}
	
	/* 
	 * Oh well, another creature bites the dust. Reward the victor
	 * based on flags set in the main creature record
	 */
	/**
	 * Allocates objects upon a creatures death -RAK-
	 * 
	 * @param y The y-coordinate of the dead monster
	 * @param x The x-coordinate of the dead monster
	 * @param flags
	 * @return A mask of bits from the given flags which indicates what the
	 *         monster is seen to have dropped. This may be added to
	 *         monster memory.
	 */
	public static int monsterDeath(int y, int x, long flags) {
		int mask;
		if ((flags & Constants.CM_CARRY_OBJ) != 0) {
			mask = 1;
		} else {
			mask = 0;
		}
		if ((flags & Constants.CM_CARRY_GOLD) != 0) {
			mask += 2;
		}
		if ((flags & Constants.CM_SMALL_OBJ) != 0) {
			mask += 4;
		}
		
		int number = 0;
		if ((flags & Constants.CM_60_RANDOM) != 0 && (Rnd.randomInt(100) < 60)) {
			number++;
		}
		if ((flags & Constants.CM_90_RANDOM) != 0 && (Rnd.randomInt(100) < 90)) {
			number++;
		}
		if ((flags & Constants.CM_1D2_OBJ) != 0) {
			number += Rnd.randomInt(2);
		}
		if ((flags & Constants.CM_2D2_OBJ) != 0) {
			number += Misc1.damageRoll(2, 2);
		}
		if ((flags & Constants.CM_4D2_OBJ) != 0) {
			number += Misc1.damageRoll(4, 2);
		}
		
		int dump;
		if (number > 0) {
			dump = summonObject(y, x, number, mask);
		} else {
			dump = 0;
		}
		
		if ((flags & Constants.CM_WIN) != 0) {
			if (!Variable.death) {
				Variable.isTotalWinner = true;
				Misc3.printWinner();
				IO.printMessage("*** CONGRATULATIONS *** You have won the game.");
				IO.printMessage("You cannot save this game, but you may retire when ready.");
			}
		}
		
		int res;
		if (dump != 0) {
			res = 0;
			if ((dump & 255) != 0) {
				res |= Constants.CM_CARRY_OBJ;
				if ((mask & 0x04) != 0) {
					res |= Constants.CM_SMALL_OBJ;
				}
			}
			if (dump >= 256) {
				res |= Constants.CM_CARRY_GOLD;
			}
			dump = (dump % 256) + (dump / 256); // number of items
			res |= dump << Constants.CM_TR_SHIFT;
		} else {
			res = 0;
		}
		
		return res;
	}
	
	/* Decreases monsters hit points and deletes monster if needed.	*/
	/* (Picking on my babies.)			       -RAK-   */
	/**
	 * Decreases monsters hit points and deletes monster if needed. -RAK-
	 * 
	 * @param monsterIndex Index of the monster to damage
	 * @param damage Amount of damage to deal
	 * @return Index of monster if it dies, or -1
	 */
	public static int monsterTakeHit(int monsterIndex, int damage) {
		MonsterType monster = Monsters.monsterList[monsterIndex];
		monster.hitpoints -= damage;
		monster.sleep = 0;
		
		int monsterTakingHit;
		if (monster.hitpoints < 0) {
			int i = monsterDeath(monster.y, monster.x, Monsters.creatureList[monster.index].cmove);
			if ((Player.py.flags.blind < 1 && monster.monsterLight)
					|| (Monsters.creatureList[monster.index].cmove & Constants.CM_WIN) != 0) {
				int tmp = (Variable.creatureRecall[monster.index].cmove & Constants.CM_TREASURE)
						>> Constants.CM_TR_SHIFT;
				if (tmp > ((i & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT)) {
					i = (i & ~Constants.CM_TREASURE) | (tmp << Constants.CM_TR_SHIFT);
				}
				Variable.creatureRecall[monster.index].cmove
						= (Variable.creatureRecall[monster.index].cmove & ~Constants.CM_TREASURE) | i;
				if (Variable.creatureRecall[monster.index].kills < Constants.MAX_SHORT) {
					Variable.creatureRecall[monster.index].kills++;
				}
			}
			
			CreatureType creature = Monsters.creatureList[monster.index];
			PlayerMisc misc = Player.py.misc;
			int newExp = (creature.mexp * creature.level) / misc.level;
			int newExpFraction = (((creature.mexp * creature.level) % misc.level)
						* 0x10000 / misc.level)
					+ misc.expFraction;
			if (newExpFraction >= 0x10000) {
				newExp++;
				misc.expFraction = newExpFraction - 0x10000;
			} else {
				misc.expFraction = newExpFraction;
			}
			
			misc.currExp += newExp;
			// can't call prt_experience() here, as that would result in "new level"
			// message appearing before "monster dies" message
			monsterTakingHit = monster.index;
			// in case this is called from within creatures(), this is a
			// horrible hack, the m_list/creatures() code needs to be
			// rewritten TODO
			if (Variable.hackMonsterIndex < monsterIndex) {
				deleteMonster(monsterIndex);
			} else {
				deleteMonster1(monsterIndex);
			}
		} else {
			monsterTakingHit = -1;
		}
		return monsterTakingHit;
	}
	
	/**
	 * Player attacks a (poor, defenseless) creature -RAK-
	 * 
	 * @param y The y-coordinate of the monster
	 * @param x The x-coordinate of the monster
	 */
	public static void playerAttackMonster(int y, int x) {
		int creatureIndex = Variable.cave[y][x].creatureIndex;
		MonsterType monster = Monsters.monsterList[creatureIndex];
		int monsterIndex = monster.index;
		CreatureType creature = Monsters.creatureList[monsterIndex];
		
		monster.sleep = 0;
		InvenType item = Treasure.inventory[Constants.INVEN_WIELD];
		
		// Does the player know what he's fighting?
		String monsterName;
		if (!monster.monsterLight) {
			monsterName = "it";
		} else {
			monsterName = String.format("the %s", creature.name);
		}
		
		IntPointer totalToHit = new IntPointer();
		int blows;
		if (item.category != Constants.TV_NOTHING) { // Proper weapon
			blows = Misc3.attackBlows(item.weight, totalToHit);
		} else { // Bare hands?
			blows = 2;
			totalToHit.value(-3);
		}
		if ((item.category >= Constants.TV_SLING_AMMO)
				&& (item.category <= Constants.TV_SPIKE)) {
			// Fix for arrows
			blows = 1;
		}
		
		PlayerMisc misc = Player.py.misc;
		totalToHit.value(totalToHit.value() + misc.plusToHit);
		int baseToHit;
		// if creature not lit, make it more difficult to hit
		if (monster.monsterLight) {
			baseToHit = misc.baseToHit;
		} else {
			baseToHit = (misc.baseToHit / 2)
					- (totalToHit.value() * (Constants.BTH_PLUS_ADJ - 1))
					- (misc.level * Player.classLevelAdjust[misc.playerClass][Constants.CLA_BTH] / 2);
		}
		
		// Loop for number of blows, trying to hit the critter.
		for (int i = 0; i < blows; i++) {
			if (Moria1.testHit(baseToHit, misc.level,
					totalToHit.value(), creature.armorClass,
					Constants.CLA_BTH)) {
				String msgHit = String.format("You hit %s.", monsterName);
				IO.printMessage(msgHit);
				
				int damage;
				if (item.category != Constants.TV_NOTHING) {
					damage = Misc1.pDamageRoll(item.damage);
					damage = Misc3.totalDamage(item, damage, monsterIndex);
					damage = Misc3.criticalBlow(item.weight,
							totalToHit.value(), damage,
							Constants.CLA_BTH);
				} else { // Bare hands!?
					damage = Misc1.damageRoll(1, 1);
					damage = Misc3.criticalBlow(1, 0, damage, Constants.CLA_BTH);
				}
				damage += misc.plusToDamage;
				if (damage < 0) {
					damage = 0;
				}
				
				if (Player.py.flags.confuseMonster) {
					Player.py.flags.confuseMonster = false;
					IO.printMessage("Your hands stop glowing.");
					
					String msgConfused;
					if ((creature.cdefense & Constants.CD_NO_SLEEP) != 0
							|| (Rnd.randomInt(Constants.MAX_MONS_LEVEL) < creature.level)) {
						msgConfused = String.format("%s is unaffected.", monsterName);
					} else {
						msgConfused = String.format("%s appears confused.", monsterName);
						if (monster.confused > 0) {
							monster.confused += 3;
						} else {
							monster.confused = 2 + Rnd.randomInt(16);
						}
					}
					IO.printMessage(msgConfused);
					if (monster.monsterLight && Rnd.randomInt(4) == 1) {
						Variable.creatureRecall[monsterIndex].cdefense
								|= creature.cdefense & Constants.CD_NO_SLEEP;
					}
				}
				
				// See if we done it in.
				if (monsterTakeHit(creatureIndex, damage) >= 0) {
					String msgKilled = String.format("You have slain %s.", monsterName);
					IO.printMessage(msgKilled);
					Misc3.printExperience();
					break;
				}
				
				if ((item.category >= Constants.TV_SLING_AMMO)
						&& (item.category <= Constants.TV_SPIKE)) { // Use missiles up
					item.number--;
					Treasure.invenWeight -= item.weight;
					Player.py.flags.status |= Constants.PY_STR_WGT;
					if (item.number == 0) {
						Treasure.equipCounter--;
						Moria1.adjustPlayerBonuses(item, -1);
						Desc.copyIntoInventory(item, Constants.OBJ_NOTHING);
						Moria1.calcBonuses();
					}
				}
			} else {
				String msgMissed = String.format("You miss %s.", monsterName);
				IO.printMessage(msgMissed);
			}
		}
	}
	
	/**
	 * Moves player from one space to another. -RAK-
	 * 
	 * @param dir Direction to move
	 * @param doPickup Whether to pickup any object player moves onto
	 */
	public static void movePlayer(int dir, boolean doPickup) {
		if ((Player.py.flags.confused > 0) // Confused?
				&& (Rnd.randomInt(4) > 1) // 75% random movement
				&& (dir != 5)) { // Never random if sitting
			dir = Rnd.randomInt(9);
			Moria2.endFind();
		}
		
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		if (!Misc3.canMoveDirection(dir, y, x)) { // Legal move?
			return;
		}
		
		CaveType cavePos = Variable.cave[y.value()][x.value()];
		// if there is no creature, or an unlit creature in the walls then...
		// disallow attacks against unlit creatures in walls because moving into
		// a wall is a free turn normally, hence don't give player free turns
		// attacking each wall in an attempt to locate the invisible creature,
		// instead force player to tunnel into walls which always takes a turn
		if ((cavePos.creatureIndex < 2)
				|| (!Monsters.monsterList[cavePos.creatureIndex].monsterLight
						&& cavePos.fval >= Constants.MIN_CLOSED_SPACE)) {
			if (cavePos.fval <= Constants.MAX_OPEN_SPACE) { // Open floor spot
				// Make final assignments of char co-ords
				int oldRow = Player.y;
				int oldCol = Player.x;
				Player.y = y.value();
				Player.x = x.value();
				// Move character record (-1)
				Moria1.moveCreatureRecord(oldRow, oldCol, Player.y, Player.x);
				// Check for new panel
				if (Misc1.getPanel(Player.y, Player.x, false)) {
					Misc1.printMap();
				}
				// Check to see if he should stop
				if (Variable.findFlag != 0) {
					Moria2.areaAffect(dir, Player.y, Player.x);
				}
				// Check to see if he notices something
				// fos may be negative if have good rings of searching
				if ((Player.py.misc.freqOfSearch <= 1)
						|| (Rnd.randomInt(Player.py.misc.freqOfSearch) == 1)
						|| (Player.py.flags.status & Constants.PY_SEARCH) != 0) {
					Moria2.search(Player.y, Player.x, Player.py.misc.searchChance);
				}
				// A room of light should be lit.
				if (cavePos.fval == Constants.LIGHT_FLOOR) {
					if (!cavePos.permLight && Player.py.flags.blind == 0) {
						Moria1.lightUpRoom(Player.y, Player.x);
					}
				
				// In doorway of light-room?
				} else if (cavePos.litRoom && (Player.py.flags.blind < 1)) {
					for (int i = (Player.y - 1); i <= (Player.y + 1); i++) {
						for (int j = (Player.x - 1); j <= (Player.x + 1); j++) {
							CaveType adjacent = Variable.cave[i][j];
							if ((adjacent.fval == Constants.LIGHT_FLOOR) && (!adjacent.permLight)) {
								Moria1.lightUpRoom(i, j);
							}
						}
					}
				}
				// Move the light source
				Moria1.moveLight(oldRow, oldCol, Player.y, Player.x);
				// An object is beneath him.
				if (cavePos.treasureIndex != 0) {
					carry(Player.y, Player.x, doPickup);
					// if stepped on falling rock trap, and space contains
					// rubble, then step back into a clear area
					if (Treasure.treasureList[cavePos.treasureIndex].category
							== Constants.TV_RUBBLE) {
						Moria1.moveCreatureRecord(Player.y, Player.x, oldRow, oldCol);
						Moria1.moveLight(Player.y, Player.x, oldRow, oldCol);
						Player.y = oldRow;
						Player.x = oldCol;
						// check to see if we have stepped back onto another
						// trap, if so, set it off
						cavePos = Variable.cave[Player.y][Player.x];
						if (cavePos.treasureIndex != 0) {
							int category = Treasure.treasureList[cavePos.treasureIndex].category;
							if (category == Constants.TV_INVIS_TRAP
									|| category == Constants.TV_VIS_TRAP
									|| category == Constants.TV_STORE_DOOR) {
								tripTrap(Player.y, Player.x);
							}
						}
					}
				}
			} else { // Can't move onto floor space
				if (Variable.findFlag == 0 && (cavePos.treasureIndex != 0)) {
					if (Treasure.treasureList[cavePos.treasureIndex].category
							== Constants.TV_RUBBLE) {
						IO.printMessage("There is rubble blocking your way.");
					} else if (Treasure.treasureList[cavePos.treasureIndex].category
							== Constants.TV_CLOSED_DOOR) {
						IO.printMessage("There is a closed door blocking your way.");
					}
				} else {
					Moria2.endFind();
				}
				Variable.freeTurnFlag = true;
			}
		} else { // Attacking a creature!
			int oldFindFlag = Variable.findFlag;
			Moria2.endFind();
			// if player can see monster, and was in find mode, then nothing
			if (Monsters.monsterList[cavePos.creatureIndex].monsterLight && oldFindFlag != 0) {
				// did not do anything this turn
				Variable.freeTurnFlag = true;
			} else {
				if (Player.py.flags.afraid < 1) { // Coward?
					playerAttackMonster(y.value(), x.value());
				} else { // Coward!
					IO.printMessage("You are too afraid!");
				}
			}
		}
	}
	
	/**
	 * Chests have traps too. -RAK-
	 * <p>
	 * Note: Chest traps are based on the FLAGS value
	 * 
	 * @param y The y-coordinate of the chest
	 * @param x The x-coordinate of the chest
	 */
	public static void chestTrap(int y, int x) {
		InvenType chest = Treasure.treasureList[Variable.cave[y][x].treasureIndex];
		if ((Constants.CH_LOSE_STR & chest.flags) != 0) {
			IO.printMessage("A small needle has pricked you!");
			if (!Player.py.flags.sustainStr) {
				Misc3.decreaseStat(Constants.A_STR);
				Moria1.takeHit(Misc1.damageRoll(1, 4), "a poison needle");
				IO.printMessage("You feel weakened!");
			} else {
				IO.printMessage("You are unaffected.");
			}
		}
		if ((Constants.CH_POISON & chest.flags) != 0) {
			IO.printMessage("A small needle has pricked you!");
			Moria1.takeHit(Misc1.damageRoll(1, 6), "a poison needle");
			Player.py.flags.poisoned += 10 + Rnd.randomInt(20);
		}
		if ((Constants.CH_PARALYSED & chest.flags) != 0) {
			IO.printMessage("A puff of yellow gas surrounds you!");
			if (Player.py.flags.freeAct) {
				IO.printMessage("You are unaffected.");
			} else {
				IO.printMessage("You choke and pass out.");
				Player.py.flags.paralysis = 10 + Rnd.randomInt(20);
			}
		}
		if ((Constants.CH_SUMMON & chest.flags) != 0) {
			for (int i = 0; i < 3; i++) {
				IntPointer tmpY = new IntPointer(y);
				IntPointer tmpX = new IntPointer(x);
				Misc1.summonMonster(tmpY, tmpX, false);
			}
		}
		if ((Constants.CH_EXPLODE & chest.flags) != 0) {
			IO.printMessage("There is a sudden explosion!");
			deleteObject(y, x);
			Moria1.takeHit(Misc1.damageRoll(5, 8), "an exploding chest");
		}
	}
	
	/**
	 * Opens a closed door or closed chest. -RAK-
	 */
	public static void openDoorOrChest() {
		IntPointer dir = new IntPointer();
		if (!Moria1.getDirection("", dir)) {
			return;
		}
		
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		Misc3.canMoveDirection(dir.value(), y, x);
		CaveType cavePos = Variable.cave[y.value()][x.value()];
		boolean noObject = false;
		if (cavePos.creatureIndex > 1 && cavePos.treasureIndex != 0
				&& (Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_CLOSED_DOOR
					|| Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_CHEST)) {
			MonsterType monster = Monsters.monsterList[cavePos.creatureIndex];
			
			String monsterName;
			if (monster.monsterLight) {
				monsterName = String.format("The %s", Monsters.creatureList[monster.index].name);
			} else {
				monsterName = "Something";
			}
			
			String msgBlocked = String.format("%s is in your way!", monsterName);
			IO.printMessage(msgBlocked);
		} else if (cavePos.treasureIndex != 0) {
			// Closed door
			if (Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_CLOSED_DOOR) {
				InvenType door = Treasure.treasureList[cavePos.treasureIndex];
				if (door.misc > 0) { // It's locked.
					PlayerMisc misc = Player.py.misc;
					int disarmChance = misc.disarmChance + 2 * Misc3.adjustToDisarm()
							+ Misc3.adjustStat(Constants.A_INT)
							+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DISARM]
									* misc.level / 3);
					if (Player.py.flags.confused > 0) {
						IO.printMessage("You are too confused to pick the lock.");
					} else if ((disarmChance - door.misc) > Rnd.randomInt(100)) {
						IO.printMessage("You have picked the lock.");
						Player.py.misc.currExp++;
						Misc3.printExperience();
						door.misc = 0;
					} else {
						IO.countMessagePrint("You failed to pick the lock.");
					}
				} else if (door.misc < 0) { // It's stuck
					IO.printMessage("It appears to be stuck.");
				}
				
				if (door.misc == 0) {
					Desc.copyIntoInventory(
							Treasure.treasureList[cavePos.treasureIndex],
							Constants.OBJ_OPEN_DOOR);
					cavePos.fval = Constants.CORR_FLOOR;
					Moria1.lightUpSpot(y.value(), x.value());
					Variable.commandCount = 0;
				}
			
			// Open a closed chest.
			} else if (Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_CHEST) {
				PlayerMisc misc = Player.py.misc;
				int disarmChance = misc.disarmChance + 2 * Misc3.adjustToDisarm()
						+ Misc3.adjustStat(Constants.A_INT)
						+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DISARM]
								* misc.level / 3);
				InvenType chest = Treasure.treasureList[cavePos.treasureIndex];
				boolean disarmed = false;
				if ((Constants.CH_LOCKED & chest.flags) != 0) {
					if (Player.py.flags.confused > 0) {
						IO.printMessage("You are too confused to pick the lock.");
					} else if ((disarmChance - chest.level) > Rnd.randomInt(100)) {
						IO.printMessage("You have picked the lock.");
						disarmed = true;
						Player.py.misc.currExp += chest.level;
						Misc3.printExperience();
					} else {
						IO.countMessagePrint("You failed to pick the lock.");
					}
				} else {
					disarmed = true;
				}
				if (disarmed) {
					chest.flags &= ~Constants.CH_LOCKED;
					chest.specialName = Constants.SN_EMPTY;
					Desc.identifyItemPlusses(chest);
					chest.cost = 0;
				}
				
				boolean trapped = false;
				// Was chest still trapped? (Snicker)
				if ((Constants.CH_LOCKED & chest.flags) == 0) {
					chestTrap(y.value(), x.value());
					if (cavePos.treasureIndex != 0) {
						trapped = true;
					}
				}
				// Chest treasure is allocated as if a creature
				// had been killed.
				if (trapped) {
					// clear the cursed chest/monster win flag, so that people
					// can not win by opening a cursed chest
					Treasure.treasureList[cavePos.treasureIndex].flags &= ~Constants.TR_CURSED;
					monsterDeath(y.value(), x.value(),
							Treasure.treasureList[cavePos.treasureIndex].flags);
					Treasure.treasureList[cavePos.treasureIndex].flags = 0;
				}
			} else {
				noObject = true;
			}
		} else {
			noObject = true;
		}
		
		if (noObject) {
			IO.printMessage("I do not see anything you can open there.");
			Variable.freeTurnFlag = true;
		}
	}
	
	/**
	 * Closes an open door. -RAK-
	 */
	public static void closeDoor() {
		IntPointer dir = new IntPointer();
		if (!Moria1.getDirection("", dir)) {
			return;
		}
		
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		Misc3.canMoveDirection(dir.value(), y, x);
		CaveType cavePos = Variable.cave[y.value()][x.value()];
		boolean noObject = false;
		if (cavePos.treasureIndex != 0) {
			if (Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_OPEN_DOOR) {
				if (cavePos.creatureIndex == 0) {
					if (Treasure.treasureList[cavePos.treasureIndex].misc == 0) {
						Desc.copyIntoInventory(Treasure.treasureList[cavePos.treasureIndex],
								Constants.OBJ_CLOSED_DOOR);
						cavePos.fval = Constants.BLOCKED_FLOOR;
						Moria1.lightUpSpot(y.value(), x.value());
					} else {
						IO.printMessage("The door appears to be broken.");
					}
				} else {
					MonsterType monster = Monsters.monsterList[cavePos.creatureIndex];
					String monsterName;
					if (monster.monsterLight) {
						monsterName = String.format(
								"The %s",
								Monsters.creatureList[monster.index].name);
					} else {
						monsterName = "Something";
					}
					String msgBlocked = String.format("%s is in your way!", monsterName);
					IO.printMessage(msgBlocked);
				}
			} else {
				noObject = true;
			}
		} else {
			noObject = true;
		}
		
		if (noObject) {
			IO.printMessage("I do not see anything you can close there.");
			Variable.freeTurnFlag = true;
		}
	}
	
	/**
	 * Tunneling through real wall: 10, 11, 12 -RAK-
	 * <p>
	 * Used by TUNNEL and WALL_TO_MUD
	 * 
	 * @param y
	 * @param x
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean tunnelThroughWall(int y, int x, int t1, int t2) {
		if (t1 <= t2) {
			return false;
		}
		
		CaveType cavePos = Variable.cave[y][x];
		if (cavePos.litRoom) {
			// should become a room space, check to see whether it should be
			// LIGHT_FLOOR or DARK_FLOOR
			boolean found = false;
			for (int i = y - 1; i <= y + 1; i++) {
				for (int j = x - 1; j <= x + 1; j++) {
					if (Variable.cave[i][j].fval <= Constants.MAX_CAVE_ROOM) {
						cavePos.fval = Variable.cave[i][j].fval;
						cavePos.permLight = Variable.cave[i][j].permLight;
						found = true;
						break;
					}
				}
			}
			if (!found) {
				cavePos.fval = Constants.CORR_FLOOR;
				cavePos.permLight = false;
			}
		} else {
			// should become a corridor space
			cavePos.fval = Constants.CORR_FLOOR;
			cavePos.permLight = false;
		}
		
		cavePos.fieldMark = false;
		if (Misc1.panelContains(y, x)) {
			if ((cavePos.tempLight || cavePos.permLight) && cavePos.treasureIndex != 0) {
				IO.printMessage("You have found something!");
			}
		}
		Moria1.lightUpSpot(y, x);
		
		return true;
	}
}
