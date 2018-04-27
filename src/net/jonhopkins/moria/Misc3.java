/*
 * Misc3.java: misc code for maintaining the dungeon, printing player info
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

import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.CharPointer;
import net.jonhopkins.moria.types.ClassType;
import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterRecallType;
import net.jonhopkins.moria.types.SpellType;

public class Misc3 {
	
	private static String[] statNames = { "STR : ", "INT : ", "WIS : ", "DEX : ", "CON : ", "CHR : " };
	private static final int BLANK_LENGTH = 24;
	private static String blankString = "                        ";
	
	private static final int STATUS_ROW = 23;
	private static final int HUNGER_COL = 0;
	private static final int BLIND_COL = 7;
	private static final int CONFUSED_COL = 13;
	private static final int FEAR_COL = 22;
	private static final int POISONED_COL = 29;
	private static final int ETC_COL = 38;
	private static final int SPEED_COL = 49;
	private static final int STUDY_COL = 59;
	
	private static final int WINNER_ROW = 22;
	private static final int WINNER_COL = 0;
	
	private Misc3() { }
	
	/**
	 * Places a particular trap at location y, x -RAK-
	 * 
	 * @param y The vertical position at which to place the trap
	 * @param x The horizontal position at which to place the trap
	 * @param subval The type of trap to place
	 */
	public static void placeTrap(int y, int x, int subval) {
		int curPos = Misc1.popTreasure();
		Variable.cave[y][x].treasureIndex = curPos;
		Desc.copyIntoInventory(Treasure.treasureList[curPos],
				Constants.OBJ_TRAP_LIST + subval);
	}
	
	/**
	 * Places rubble at location y, x -RAK-
	 * 
	 * @param y The vertical position at which to place the rubble
	 * @param x The horizontal position at which to place the rubble
	 */
	public static void placeRubble(int y, int x) {
		int curPos = Misc1.popTreasure();
		CaveType cavePos = Variable.cave[y][x];
		cavePos.treasureIndex = curPos;
		cavePos.fval = Constants.BLOCKED_FLOOR;
		Desc.copyIntoInventory(Treasure.treasureList[curPos],
				Constants.OBJ_RUBBLE);
	}
	
	/**
	 * Places a treasure (Gold or Gems) at given row, column -RAK-
	 * 
	 * @param y The vertical position at which to place the gold
	 * @param x The horizontal position at which to place the gold
	 */
	public static void placeGold(int y, int x) {
		int curPos = Misc1.popTreasure();
		
		int itemNum = ((Rnd.randomInt(Variable.dungeonLevel + 2) + 2) / 2) - 1;
		if (Rnd.randomInt(Constants.OBJ_GREAT) == 1) {
			itemNum += Rnd.randomInt(Variable.dungeonLevel + 1);
		}
		if (itemNum >= Constants.MAX_GOLD) {
			itemNum = Constants.MAX_GOLD - 1;
		}
		
		Variable.cave[y][x].treasureIndex = curPos;
		Desc.copyIntoInventory(Treasure.treasureList[curPos], Constants.OBJ_GOLD_LIST + itemNum);
		InvenType item = Treasure.treasureList[curPos];
		item.cost += (8L * (long)Rnd.randomInt(item.cost)) + Rnd.randomInt(8);
		if (Variable.cave[y][x].creatureIndex == 1) {
			IO.printMessage("You feel something roll beneath your feet.");
		}
	}
	
	public static int getRandomObjectForLevel(int level, boolean mustBeSmall) {
		int i;
		
		if (level == 0) {
			i = Rnd.randomInt(Treasure.treasureLevel[0]) - 1;
		} else {
			if (level >= Constants.MAX_OBJ_LEVEL) {
				level = Constants.MAX_OBJ_LEVEL;
			} else if (Rnd.randomInt(Constants.OBJ_GREAT) == 1) {
				level = level * Constants.MAX_OBJ_LEVEL / Rnd.randomInt(Constants.MAX_OBJ_LEVEL) + 1;
				if (level > Constants.MAX_OBJ_LEVEL) {
					level = Constants.MAX_OBJ_LEVEL;
				}
			}
			
			/* This code has been added to make it slightly more likely to get the
			 * higher level objects.	Originally a uniform distribution over all
			 * objects less than or equal to the dungeon level.  This distribution
			 * makes a level n objects occur approx 2/n% of the time on level n,
			 * and 1/2n are 0th level. */
			
			do {
				if (Rnd.randomInt(2) == 1) {
					i = Rnd.randomInt(Treasure.treasureLevel[level]) - 1;
				} else { // Choose three objects, pick the highest level.
					i = Rnd.randomInt(Treasure.treasureLevel[level]) - 1;
					int j = Rnd.randomInt(Treasure.treasureLevel[level]) - 1;
					if (i < j) {
						i = j;
					}
					j = Rnd.randomInt(Treasure.treasureLevel[level]) - 1;
					if (i < j) {
						i = j;
					}
					
					j = Treasure.objectList[Treasure.sortedObjects[i]].level;
					if (j == 0) {
						i = Rnd.randomInt(Treasure.treasureLevel[0]) - 1;
					} else {
						i = Rnd.randomInt(Treasure.treasureLevel[j]
								- Treasure.treasureLevel[j - 1])
								- 1 + Treasure.treasureLevel[j - 1];
					}
				}
			} while (mustBeSmall && Treasure.objectList[Treasure.sortedObjects[i]].isTooLargeForChest());
		}
		return i;
	}

	/**
	 * Places an object at given row, column co-ordinate -RAK-
	 * 
	 * @param y The vertical position at which to place the object
	 * @param x The horizontal position at which to place the object
	 * @param mustBeSmall ignore
	 */
	public static void placeObject(int y, int x, boolean mustBeSmall) {
		int curPos = Misc1.popTreasure();
		Variable.cave[y][x].treasureIndex = curPos;
		
		int itemIndex = getRandomObjectForLevel(Variable.dungeonLevel, mustBeSmall);
		Desc.copyIntoInventory(Treasure.treasureList[curPos],
				Treasure.sortedObjects[itemIndex]);
		Misc2.addMagicToTreasure(curPos, Variable.dungeonLevel);
		if (Variable.cave[y][x].creatureIndex == 1) {
			IO.printMessage("You feel something roll beneath your feet."); /* -CJS- */
		}
	}
	
	/**
	 * Allocates an object for tunnels and rooms -RAK-
	 * 
	 * @param allocSet Place the object in a corridor, floor, or room
	 * @param typ Type of object to allocate
	 * @param num Number of objects to allocate
	 */
	public static void spawnObject(int allocSet, int typ, int num) {
		for (int i = 0; i < num; i++) {
			int x;
			int y;
			
			do {
				y = Rnd.randomInt(Variable.currHeight) - 1;
				x = Rnd.randomInt(Variable.currWidth) - 1;
				
				/* don't put an object beneath the player, this could cause problems
				 * if player is standing under rubble, or on a trap */
			} while ((!allocSet(Variable.cave[y][x], allocSet))
					|| (Variable.cave[y][x].treasureIndex != 0)
					|| (y == Player.y && x == Player.x));
			if (typ < 4) { // typ == 2 not used, used to be visible traps
				if (typ == 1) {
					placeTrap(y, x, Rnd.randomInt(Constants.MAX_TRAP) - 1); // typ == 1
				} else {
					placeRubble(y, x); // typ == 3
				}
			} else {
				if (typ == 4) {
					placeGold(y, x); // typ == 4
				} else {
					placeObject(y, x, false); // typ == 5
				}
			}
		}
	}
	
	private static boolean allocSet(CaveType cavePos, int allocSet) {
		switch (allocSet) {
		case Sets.SET_CORR:
			return cavePos.isCorridor();
		case Sets.SET_FLOOR:
			return cavePos.isFloor();
		case Sets.SET_ROOM:
			return cavePos.isRoom();
		default:
			return false;
		}
	}
	
	/**
	 * Creates objects nearby the coordinates given -RAK-
	 * 
	 * @param y The vertical position around which to place the object
	 * @param x The horizontal position around which to place the object
	 * @param num Number of objects to allocate
	 */
	public static void spawnRandomObject(int y, int x, int num) {
		do {
			int i = 0;
			do {
				int j = y - 3 + Rnd.randomInt(5);
				int k = x - 4 + Rnd.randomInt(7);
				CaveType cavePos = Variable.cave[j][k];
				if (Misc1.isInBounds(j, k) && (cavePos.fval <= Constants.MAX_CAVE_FLOOR)
						&& (cavePos.treasureIndex == 0)) {
					if (Rnd.randomInt(100) < 75) {
						placeObject(j, k, false);
					} else {
						placeGold(j, k);
					}
					i = 9;
				}
				i++;
			} while (i <= 10);
			num--;
		} while (num != 0);
	}
	
	/**
	 * Converts stat num into string -RAK-
	 * 
	 * @param stat The stat to be converted
	 * @return The stringified stat
	 */
	public static String convertStat(int stat) {
		if (stat > 18) {
			int part1 = 18;
			int part2 = stat - 18;
			if (part2 == 100) {
				return "18/100";
			} else {
				return String.format(" %2d/%02d", part1, part2);
			}
		} else {
			return String.format("%6d", stat);
		}
	}
	
	/**
	 * Print character stat in given row, column -RAK-
	 * 
	 * @param stat The stat to print
	 */
	public static void printStat(int stat) {
		String strStat = convertStat(Player.py.stats.useStat[stat]);
		IO.putBuffer(statNames[stat], 6 + stat, Constants.STAT_COLUMN);
		IO.putBuffer(strStat, 6 + stat, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Print character info in given row, column.
	 * The longest title is 13 characters, so only pad to 13 -RAK-
	 * 
	 * @param info What to print
	 * @param row The vertical position at which to print
	 * @param column The horizontal position at which to print
	 */
	public static void printField(String info, int row, int column) {
		IO.putBuffer(blankString.substring(BLANK_LENGTH - 13), row, column);
		IO.putBuffer(info, row, column);
	}
	
	/**
	 * Print long number with header at given row, column
	 * 
	 * @param header The header to print
	 * @param num The number to print
	 * @param row The vertical position at which to print
	 * @param column The horizontal position at which to print
	 */
	public static void printLongWithHeader(String header, int num, int row, int column) {
		String outVal = String.format("%s: %6d", header, num);
		IO.putBuffer(outVal, row, column);
	}
	
	/**
	 * Print long number (7 digits of space) with header at given row, column
	 * 
	 * @param header The header to print
	 * @param num The number to print
	 * @param row The vertical position at which to print
	 * @param column The horizonal position at which to print
	 */
	public static void prtLong7WithHeader(String header, int num, int row, int column) {
		String outVal = String.format("%s: %7d", header, num);
		IO.putBuffer(outVal, row, column);
	}
	
	/**
	 * Print number with header at given row, column -RAK-
	 * 
	 * @param header The header to print
	 * @param num The number to print
	 * @param row The vertical position at which to print
	 * @param column The horizontal position at which to print
	 */
	public static void printNum(String header, int num, int row, int column) {
		String outVal = String.format("%s: %6d", header, num);
		IO.putBuffer(outVal, row, column);
	}
	
	/**
	 * Print long number at given row, column
	 * 
	 * @param num The number to print
	 * @param row The vertical position at which to print
	 * @param column The horizontal position at which to print
	 */
	public static void printLong(int num, int row, int column) {
		String outVal = String.format("%6d", num);
		IO.putBuffer(outVal, row, column);
	}

	/**
	 * Print number at given row, column -RAK-
	 * 
	 * @param num The number to print
	 * @param row The vertical position at which to print
	 * @param column The horizontal position at which to print
	 */
	public static void printInt(int num, int row, int column) {
		String outVal = String.format("%6d", num);
		IO.putBuffer(outVal, row, column);
	}
	
	/**
	 * Adjustment for wisdom/intelligence -JWT-
	 * 
	 * @param stat The stat to adjust
	 * @return The adjusted value of the player's stat
	 */
	public static int adjustStat(int stat) {
		int value = Player.py.stats.useStat[stat];
		if (value > 117) {
			return 7;
		} else if (value > 107) {
			return 6;
		} else if (value > 87) {
			return 5;
		} else if (value > 67) {
			return 4;
		} else if (value > 17) {
			return 3;
		} else if (value > 14) {
			return 2;
		} else if (value > 7) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Adjustment for charisma.
	 * Percent decrease or increase in price of goods -RAK-
	 * 
	 * @return The adjusted value of the player's charisma
	 */
	public static int adjustCharisma() {
		int charisma = Player.py.stats.useStat[Constants.A_CHR];
		if (charisma > 117) {
			return 90;
		} else if (charisma > 107) {
			return 92;
		} else if (charisma > 87) {
			return 94;
		} else if (charisma > 67) {
			return 96;
		} else if (charisma > 18) {
			return 98;
		} else {
			switch (charisma) {
			case 18: return 100;
			case 17: return 101;
			case 16: return 102;
			case 15: return 103;
			case 14: return 104;
			case 13: return 106;
			case 12: return 108;
			case 11: return 110;
			case 10: return 112;
			case 9:  return 114;
			case 8:  return 116;
			case 7:  return 118;
			case 6:  return 120;
			case 5:  return 122;
			case 4:  return 125;
			case 3:  return 130;
			default: return 100;
			}
		}
	}
	
	/**
	 * Returns a character's adjustment to hit points -JWT-
	 * 
	 * @return The adjusted value of the player's constitution
	 */
	public static int adjustConstitution() {
		int con = Player.py.stats.useStat[Constants.A_CON];
		if (con < 7) {
			return con - 7;
		} else if (con < 17) {
			return 0;
		} else if (con == 17) {
			return 1;
		} else if (con < 94) {
			return 2;
		} else if (con < 117) {
			return 3;
		} else {
			return 4;
		}
	}
	
	public static String getPlayerTitle() {
		if (Player.py.misc.level < 1) {
			return "Babe in arms";
		} else if (Player.py.misc.level <= Constants.MAX_PLAYER_LEVEL) {
			return Player.title[Player.py.misc.playerClass][Player.py.misc.level - 1];
		} else if (Player.py.misc.isMale) {
			return "**KING**";
		} else {
			return "**QUEEN**";
		}
	}
	
	/**
	 * Prints title of character -RAK-
	 */
	public static void printPlayerTitle() {
		printField(getPlayerTitle(), 4, Constants.STAT_COLUMN);
	}
	
	/**
	 * Prints player's level -RAK-
	 */
	public static void printLevel() {
		printInt(Player.py.misc.level, 13, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints player's current mana points. -RAK-
	 */
	public static void printCurrentMana() {
		printInt(Player.py.misc.currMana, 15, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints Max hit points -RAK-
	 */
	public static void printMaxHitpoints() {
		printInt(Player.py.misc.maxHitpoints, 16, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints player's current hit points -RAK-
	 */
	public static void printCurrentHitpoints() {
		printInt(Player.py.misc.currHitpoints, 17, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints current AC -RAK-
	 */
	public static void printCurrentAc() {
		printInt(Player.py.misc.displayPlusToArmorClass, 19, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints current gold -RAK-
	 */
	public static void printGold() {
		printLong(Player.py.misc.gold, 20, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints depth in stat area -RAK-
	 */
	public static void printDepth() {
		String depths;
		int depth = Variable.dungeonLevel * 50;
		
		if (depth == 0) {
			depths = "Town level";
		} else {
			depths = String.format("%d feet", depth);
		}
		
		IO.print(depths, 23, 65);
	}
	
	/**
	 * Prints status of hunger -RAK-
	 */
	public static void printHunger() {
		if ((Constants.PY_WEAK & Player.py.flags.status) != 0) {
			IO.putBuffer("Weak  ", STATUS_ROW, HUNGER_COL);
		} else if ((Constants.PY_HUNGRY & Player.py.flags.status) != 0) {
			IO.putBuffer("Hungry", STATUS_ROW, HUNGER_COL);
		} else {
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 6),
					STATUS_ROW, HUNGER_COL);
		}
	}
	
	/**
	 * Prints Blind status -RAK-
	 */
	public static void printBlindness() {
		if ((Constants.PY_BLIND & Player.py.flags.status) != 0) {
			IO.putBuffer("Blind", STATUS_ROW, BLIND_COL);
		} else {
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 5),
					STATUS_ROW, BLIND_COL);
		}
	}
	
	/**
	 * Prints Confusion status -RAK-
	 */
	public static void printConfusion() {
		if ((Constants.PY_CONFUSED & Player.py.flags.status) != 0) {
			IO.putBuffer("Confused", STATUS_ROW, CONFUSED_COL);
		} else {
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 8),
					STATUS_ROW, CONFUSED_COL);
		}
	}
	
	/**
	 * Prints Fear status -RAK-
	 */
	public static void printFear() {
		if ((Constants.PY_FEAR & Player.py.flags.status) != 0) {
			IO.putBuffer("Afraid", STATUS_ROW, FEAR_COL);
		} else {
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 6),
					STATUS_ROW, FEAR_COL);
		}
	}
	
	/**
	 * Prints Poisoned status -RAK-
	 */
	public static void printPoisoned() {
		if ((Constants.PY_POISONED & Player.py.flags.status) != 0) {
			IO.putBuffer("Poisoned", STATUS_ROW, POISONED_COL);
		} else {
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 8),
					STATUS_ROW, POISONED_COL);
		}
	}
	
	/**
	 * Prints Searching, Resting, Paralysis, or 'count' status -RAK-
	 */
	public static void printState() {
		Player.py.flags.status &= ~Constants.PY_REPEAT;
		if (Player.py.flags.paralysis > 1) {
			IO.putBuffer("Paralysed", STATUS_ROW, ETC_COL);
		} else if ((Constants.PY_REST & Player.py.flags.status) != 0) {
			String rest;
			if (Player.py.flags.rest < 0) {
				rest = "Rest *";
			} else if (Variable.displayCounts.value()) {
				rest = String.format("Rest %-5d", Player.py.flags.rest);
			} else {
				rest = "Rest";
			}
			IO.putBuffer(rest, STATUS_ROW, ETC_COL);
		} else if (Variable.commandCount > 0) {
			String repeat;
			if (Variable.displayCounts.value()) {
				repeat = String.format("Repeat %-3d", Variable.commandCount);
			} else {
				repeat = "Repeat";
			}
			Player.py.flags.status |= Constants.PY_REPEAT;
			IO.putBuffer(repeat, STATUS_ROW, ETC_COL);
			if ((Constants.PY_SEARCH & Player.py.flags.status) != 0) {
				IO.putBuffer("Search", STATUS_ROW, ETC_COL);
			}
		} else if ((Constants.PY_SEARCH & Player.py.flags.status) != 0) {
			IO.putBuffer("Searching", STATUS_ROW, ETC_COL);
		} else { // "repeat 999" is 10 characters
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 10),
					STATUS_ROW, ETC_COL);
		}
	}
	
	/**
	 * Prints the speed of a character. -CJS-
	 */
	public static void printSpeed() {
		int i = Player.py.flags.speed;
		if ((Constants.PY_SEARCH & Player.py.flags.status) != 0) { // Search mode.
			i--;
		}
		if (i > 1) {
			IO.putBuffer("Very Slow", STATUS_ROW, SPEED_COL);
		} else if (i == 1) {
			IO.putBuffer("Slow     ", STATUS_ROW, SPEED_COL);
		} else if (i == 0) {
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 9),
					STATUS_ROW, SPEED_COL);
		} else if (i == -1) {
			IO.putBuffer("Fast     ", STATUS_ROW, SPEED_COL);
		} else {
			IO.putBuffer("Very Fast", STATUS_ROW, SPEED_COL);
		}
	}
	
	public static void printStudy() {
		Player.py.flags.status &= ~Constants.PY_STUDY;
		if (Player.py.flags.newSpells == 0) {
			IO.putBuffer(blankString.substring(BLANK_LENGTH - 5),
					STATUS_ROW, STUDY_COL);
		} else {
			IO.putBuffer("Study", STATUS_ROW, STUDY_COL);
		}
	}
	
	/**
	 * Prints winner status on display -RAK-
	 */
	public static void printWinner() {
		if ((Variable.noScore & 0x2) != 0) {
			if (Variable.isWizard) {
				IO.putBuffer("Is wizard  ", WINNER_ROW, WINNER_COL);
			} else {
				IO.putBuffer("Was wizard ", WINNER_ROW, WINNER_COL);
			}
		} else if ((Variable.noScore & 0x1) != 0) {
			IO.putBuffer("Resurrected", WINNER_ROW, WINNER_COL);
		} else if ((Variable.noScore & 0x4) != 0) {
			IO.putBuffer("Duplicate", WINNER_ROW, WINNER_COL);
		} else if (Variable.isTotalWinner) {
			IO.putBuffer("*Winner*   ", WINNER_ROW, WINNER_COL);
		}
	}
	
	/**
	 * Change the value of one of the player's stats
	 * 
	 * @param stat Which stat to change
	 * @param amount How much to change the stat by
	 * @return Final value of the stat
	 */
	public static int modifyStat(int stat, int amount) {
		int tmpStat = Player.py.stats.curStat[stat];
		int loop = Math.abs(amount);
		
		for (int i = 0; i < loop; i++) {
			if (amount > 0) {
				if (tmpStat < 18) {
					tmpStat++;
				} else if (tmpStat < 108) {
					tmpStat += 10;
				} else {
					tmpStat = 118;
				}
			} else {
				if (tmpStat > 27) {
					tmpStat -= 10;
				} else if (tmpStat > 18) {
					tmpStat = 18;
				} else if (tmpStat > 3) {
					tmpStat--;
				}
			}
		}
		
		return tmpStat;
	}
	
	/**
	 * Set the value of the stat which is actually used. -CJS-
	 * 
	 * @param stat Which stat to set
	 */
	public static void setStatUseValue(int stat) {
		Player.py.stats.useStat[stat] = modifyStat(stat, Player.py.stats.modStat[stat]);
		
		if (stat == Constants.A_STR) {
			Player.py.flags.status |= Constants.PY_STR_WGT;
			Moria1.calcBonuses();
		} else if (stat == Constants.A_DEX) {
			Moria1.calcBonuses();
		} else if (stat == Constants.A_INT
				&& Player.Class[Player.py.misc.playerClass].spell == Constants.MAGE) {
			calcSpells(Constants.A_INT);
			calcMana(Constants.A_INT);
		} else if (stat == Constants.A_WIS
				&& Player.Class[Player.py.misc.playerClass].spell == Constants.PRIEST) {
			calcSpells(Constants.A_WIS);
			calcMana(Constants.A_WIS);
		} else if (stat == Constants.A_CON) {
			calcHitpoints();
		}
	}
	
	/**
	 * Increases a stat by one randomized level -RAK-
	 * 
	 * @param stat Which stat to increase
	 * @return Whether the stat was increased
	 */
	public static boolean increaseStat(int stat) {
		int tmpStat = Player.py.stats.curStat[stat];
		if (tmpStat < 118) {
			if (tmpStat < 18) {
				tmpStat++;
			} else if (tmpStat < 116) {
				// stat increases by 1/6 to 1/3 of difference from max
				int gain = ((118 - tmpStat) / 3 + 1) >> 1;
				tmpStat += Rnd.randomInt(gain) + gain;
			} else {
				tmpStat++;
			}
			
			Player.py.stats.curStat[stat] = tmpStat;
			if (tmpStat > Player.py.stats.maxStat[stat]) {
				Player.py.stats.maxStat[stat] = tmpStat;
			}
			setStatUseValue(stat);
			printStat(stat);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Decreases a stat by one randomized level -RAK-
	 * 
	 * @param stat Which stat to decrease
	 * @return Whether the stat was decreased
	 */
	public static boolean decreaseStat(int stat) {
		int tmpStat = Player.py.stats.curStat[stat];
		if (tmpStat > 3) {
			if (tmpStat < 19) {
				tmpStat--;
			} else if (tmpStat < 117) {
				int loss = (((118 - tmpStat) >> 1) + 1) >> 1;
				tmpStat += -Rnd.randomInt(loss) - loss;
				if (tmpStat < 18) {
					tmpStat = 18;
				}
			} else {
				tmpStat--;
			}
			
			Player.py.stats.curStat[stat] = tmpStat;
			setStatUseValue(stat);
			printStat(stat);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Restore a stat. Return TRUE only if this actually makes a difference.
	 * 
	 * @param stat Which stat to restore
	 * @return True if the stat actually needed to be restored, otherwise false
	 */
	public static boolean restoreStat(int stat) {
		int statAdjust = Player.py.stats.maxStat[stat]
				- Player.py.stats.curStat[stat];
		if (statAdjust != 0) {
			Player.py.stats.curStat[stat] += statAdjust;
			setStatUseValue(stat);
			printStat(stat);
			return true;
		}
		return false;
	}
	
	/**
	 * Boost a stat artificially (by wearing something). If the display argument
	 * is TRUE, then increase is shown on the screen.
	 * 
	 * @param stat Which stat to boost
	 * @param amount How much to boost the stat by
	 */
	public static void boostStat(int stat, int amount) {
		Player.py.stats.modStat[stat] += amount;
		
		setStatUseValue(stat);
		// can not call prt_stat() here, may be in store, may be in inven_command
		Player.py.flags.status |= (Constants.PY_STR << stat);
	}
	
	/**
	 * Returns a character's adjustment to hit. -JWT-
	 */
	public static int adjustToHit() {
		int total;
		int stat = Player.py.stats.useStat[Constants.A_DEX];
		
		if (stat < 4) {
			total = -3;
		} else if (stat < 6) {
			total = -2;
		} else if (stat < 8) {
			total = -1;
		} else if (stat < 16) {
			total = 0;
		} else if (stat < 17) {
			total = 1;
		} else if (stat < 18) {
			total = 2;
		} else if (stat < 69) {
			total = 3;
		} else if (stat < 118) {
			total = 4;
		} else {
			total = 5;
		}
		
		stat = Player.py.stats.useStat[Constants.A_STR];
		if (stat < 4) {
			total -= 3;
		} else if (stat < 5) {
			total -= 2;
		} else if (stat < 7) {
			total -= 1;
		} else if (stat < 18) {
			total -= 0;
		} else if (stat < 94) {
			total += 1;
		} else if (stat < 109) {
			total += 2;
		} else if (stat < 117) {
			total += 3;
		} else {
			total += 4;
		}
		
		return total;
	}
	
	/**
	 * Returns a character's adjustment to armor class -JWT-
	 */
	public static int adjustToAc() {
		int stat = Player.py.stats.useStat[Constants.A_DEX];
		if (stat < 4) {
			return -4;
		} else if (stat == 4) {
			return -3;
		} else if (stat == 5) {
			return -2;
		} else if (stat == 6) {
			return -1;
		} else if (stat < 15) {
			return 0;
		} else if (stat < 18) {
			return 1;
		} else if (stat < 59) {
			return 2;
		} else if (stat < 94) {
			return 3;
		} else if (stat < 117) {
			return 4;
		} else {
			return 5;
		}
	}
	
	/**
	 * Returns a character's adjustment to disarm -RAK-
	 */
	public static int adjustToDisarm() {
		int stat = Player.py.stats.useStat[Constants.A_DEX];
		if (stat < 4) {
			return -8;
		} else if (stat == 4) {
			return -6;
		} else if (stat == 5) {
			return -4;
		} else if (stat == 6) {
			return -2;
		} else if (stat == 7) {
			return -1;
		} else if (stat < 13) {
			return  0;
		} else if (stat < 16) {
			return  1;
		} else if (stat < 18) {
			return  2;
		} else if (stat < 59) {
			return  4;
		} else if (stat < 94) {
			return  5;
		} else if (stat < 117) {
			return  6;
		} else {
			return  8;
		}
	}
	
	/**
	 * Returns a character's adjustment to damage -JWT-
	 */
	public static int adjustToDamage() {
		int stat = Player.py.stats.useStat[Constants.A_STR];
		if (stat < 4) {
			return -2;
		} else if (stat < 5) {
			return -1;
		} else if (stat < 16) {
			return  0;
		} else if (stat < 17) {
			return  1;
		} else if (stat < 18) {
			return  2;
		} else if (stat < 94) {
			return  3;
		} else if (stat < 109) {
			return  4;
		} else if (stat < 117) {
			return  5;
		} else {
			return  6;
		}
	}
	
	/**
	 * Prints character-screen info -RAK-
	 */
	public static void printStatBlock() {
		PlayerMisc misc = Player.py.misc;
		printField(Player.race[Player.py.misc.playerRace].raceType, 2, Constants.STAT_COLUMN);
		printField(Player.Class[Player.py.misc.playerClass].title, 3, Constants.STAT_COLUMN);
		printField(getPlayerTitle(), 4, Constants.STAT_COLUMN);
		for (int i = 0; i < 6; i++) {
			printStat(i);
		}
		printNum ("LEV ", misc.level, 13, Constants.STAT_COLUMN);
		printLongWithHeader("EXP ", misc.currExp, 14, Constants.STAT_COLUMN);
		printNum ("MANA", misc.currMana, 15, Constants.STAT_COLUMN);
		printNum ("MHP ", misc.maxHitpoints, 16, Constants.STAT_COLUMN);
		printNum ("CHP ", misc.currHitpoints, 17, Constants.STAT_COLUMN);
		printNum ("AC  ", misc.displayPlusToArmorClass, 19, Constants.STAT_COLUMN);
		printLongWithHeader("GOLD", misc.gold, 20, Constants.STAT_COLUMN);
		printWinner();
		long status = Player.py.flags.status;
		if (((Constants.PY_HUNGRY | Constants.PY_WEAK) & status) != 0) {
			printHunger();
		}
		if ((Constants.PY_BLIND & status) != 0) {
			printBlindness();
		}
		if ((Constants.PY_CONFUSED & status) != 0) {
			printConfusion();
		}
		if ((Constants.PY_FEAR & status) != 0) {
			printFear();
		}
		if ((Constants.PY_POISONED & status) != 0) {
			printPoisoned();
		}
		if (((Constants.PY_SEARCH | Constants.PY_REST) & status) != 0) {
			printState ();
		}
		// if speed non zero, print it, modify speed if Searching
		if (Player.py.flags.speed - ((Constants.PY_SEARCH & status) >> 8) != 0) {
			printSpeed ();
		}
		// display the study field
		printStudy();
	}
	
	/**
	 * Draws entire screen -RAK-
	 */
	public static void drawCave() {
		IO.clearScreen ();
		printStatBlock();
		Misc1.printMap();
		printDepth();
	}
	
	/**
	 * Prints the following information on the screen. -JWT-
	 */
	public static void printCharacterInfo() {
		PlayerMisc misc = Player.py.misc;
		IO.clearScreen();
		IO.putBuffer("Name        :", 2, 1);
		IO.putBuffer("Race        :", 3, 1);
		IO.putBuffer("Sex         :", 4, 1);
		IO.putBuffer("Class       :", 5, 1);
		if (Variable.isCharacterGenerated) {
			IO.putBuffer(misc.name, 2, 15);
			IO.putBuffer(Player.race[misc.playerRace].raceType, 3, 15);
			IO.putBuffer(misc.isMale ? "Male" : "Female", 4, 15);
			IO.putBuffer(Player.Class[misc.playerClass].title, 5, 15);
		}
	}
	
	/**
	 * Prints stats and adjustments. -JWT-
	 */
	public static void printStats() {
		PlayerMisc misc = Player.py.misc;
		for (int i = 0; i < 6; i++) {
			String buf = convertStat(Player.py.stats.useStat[i]);
			IO.putBuffer(statNames[i], 2 + i, 61);
			IO.putBuffer(buf, 2 + i, 66);
			if (Player.py.stats.maxStat[i] > Player.py.stats.curStat[i]) {
				buf = convertStat(Player.py.stats.maxStat[i]);
				IO.putBuffer(buf, 2 + i, 73);
			}
		}
		printNum("+ To Hit    ", misc.displayPlusToHit,  9, 1);
		printNum("+ To Damage ", misc.displayPlusToDamage, 10, 1);
		printNum("+ To AC     ", misc.displayPlusTotalArmorClass, 11, 1);
		printNum("  Total AC  ", misc.displayPlusToArmorClass, 12, 1);
	}
	
	/**
	 * Returns a rating of x depending on y -JWT-
	 * 
	 * @param x Being rated
	 * @param y Being compared to
	 * @return Rating of x
	 */
	public static String likeRating(int x, int y) {
		switch (x / y) {
		case -3: case -2: case -1:	return "Very Bad";
		case 0: case 1:				return "Bad";
		case 2:						return "Poor";
		case 3: case 4:				return "Fair";
		case  5:					return "Good";
		case 6:						return "Very Good";
		case 7: case 8:				return "Excellent";
		default:					return "Superb";
		}
	}
	
	/**
	 * Prints age, height, weight, and social class -JWT-
	 */
	public static void printAhws() {
		PlayerMisc misc = Player.py.misc;
		printNum("Age          ", misc.age, 2, 38);
		printNum("Height       ", misc.height, 3, 38);
		printNum("Weight       ", misc.weight, 4, 38);
		printNum("Social Class ", misc.socialClass, 5, 38);
	}
	
	/**
	 * Prints player information. -JWT-
	 */
	public static void printLevelStats() {
		PlayerMisc misc = Player.py.misc;
		prtLong7WithHeader("Level      ", misc.level, 9, 28);
		prtLong7WithHeader("Experience ", misc.currExp, 10, 28);
		prtLong7WithHeader("Max Exp    ", misc.maxExp, 11, 28);
		if (misc.level == Constants.MAX_PLAYER_LEVEL) {
			IO.print("Exp to Adv.: *******", 12, 28);
		} else {
			prtLong7WithHeader(
					"Exp to Adv.",
					(Player.exp[misc.level - 1] * misc.expFactor / 100),
					12,
					28);
		}
		prtLong7WithHeader("Gold       ", misc.gold, 13, 28);
		printNum("Max Hit Points ", misc.maxHitpoints, 9, 52);
		printNum("Cur Hit Points ", misc.currHitpoints, 10, 52);
		printNum("Max Mana       ", misc.maxMana, 11, 52);
		printNum("Cur Mana       ", misc.currMana, 12, 52);
	}
	
	/**
	 * Prints ratings on certain abilities -RAK-
	 */
	public static void printAbilities() {
		IO.clearFrom(14);
		PlayerMisc misc = Player.py.misc;
		int xbth = misc.baseToHit + misc.plusToHit * Constants.BTH_PLUS_ADJ
				+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_BTH] * misc.level);
		int xbthb = misc.baseToHitBow + misc.plusToHit * Constants.BTH_PLUS_ADJ
				+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_BTHB] * misc.level);
		// this results in a range from 0 to 29
		int xfos = 40 - misc.freqOfSearch;
		if (xfos < 0) xfos = 0;
		int xsrh = misc.searchChance;
		// this results in a range from 0 to 9
		int xstl = misc.stealth + 1;
		int xdis = misc.disarmChance + 2 * adjustToDisarm() + adjustStat(Constants.A_INT)
				+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DISARM] * misc.level / 3);
		int xsave = misc.savingThrow + adjustStat(Constants.A_WIS)
				+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_SAVE] * misc.level / 3);
		int xdev = misc.savingThrow + adjustStat(Constants.A_INT)
				+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DEVICE] * misc.level / 3);
		
		String xinfra = String.format("%d feet", Player.py.flags.seeInfrared * 10);
		
		IO.putBuffer("(Miscellaneous Abilities)", 15, 25);
		IO.putBuffer("Fighting    :", 16, 1);
		IO.putBuffer(likeRating(xbth, 12), 16, 15);
		IO.putBuffer("Bows/Throw  :", 17, 1);
		IO.putBuffer(likeRating(xbthb, 12), 17, 15);
		IO.putBuffer("Saving Throw:", 18, 1);
		IO.putBuffer(likeRating(xsave, 6), 18, 15);
		
		IO.putBuffer("Stealth     :", 16, 28);
		IO.putBuffer(likeRating(xstl, 1), 16, 42);
		IO.putBuffer("Disarming   :", 17, 28);
		IO.putBuffer(likeRating(xdis, 8), 17, 42);
		IO.putBuffer("Magic Device:", 18, 28);
		IO.putBuffer(likeRating(xdev, 6), 18, 42);
		
		IO.putBuffer("Perception  :", 16, 55);
		IO.putBuffer(likeRating(xfos, 3), 16, 69);
		IO.putBuffer("Searching   :", 17, 55);
		IO.putBuffer(likeRating(xsrh, 6), 17, 69);
		IO.putBuffer("Infra-Vision:", 18, 55);
		IO.putBuffer(xinfra, 18, 69);
	}
	
	/**
	 * Prints out all character information. -RAK-
	 */
	public static void displayCharacter() {
		printCharacterInfo();
		printAhws();
		printStats();
		printLevelStats();
		printAbilities();
	}
	
	/**
	 * Gets a name for the character -JWT-
	 */
	public static void chooseName() {
		IO.print("Enter your player's name  [press <RETURN> when finished]", 21, 2);
		IO.putBuffer(blankString.substring(BLANK_LENGTH - 23), 2, 15);
		Player.py.misc.name = IO.getString(2, 15, 23);
		if (Player.py.misc.name.isEmpty()) {
			Player.py.misc.name = userName();
			IO.putBuffer(Player.py.misc.name, 2, 15);
		}
		IO.clearFrom(20);
	}
	
	private static String userName() {
		return getLogin();
	}
	
	private static String getLogin() {
		String cp = System.getenv("USER");
		if (cp == null) {
			cp = "player";
		}
		return cp;
	}
	
	/**
	 * Changes the name of the character -JWT-
	 */
	public static void changeName() {
		boolean flag = false;
		displayCharacter();
		do {
			IO.print("<f>ile character description. <c>hange character name.", 21, 2);
			char c = IO.inkey();
			switch (c) {
			case 'c':
				chooseName();
				flag = true;
				break;
			case 'f':
				IO.print("File name:", 0, 0);
				String filename = IO.getString(0, 10, 60);
				if (!filename.isEmpty()) {
					if (Files.fileCharacter(filename)) {
						flag = true;
					}
				}
				break;
			case Constants.ESCAPE: case ' ':
			case '\n': case '\r':
				flag = true;
				break;
			default:
				IO.bell();
				break;
			}
		} while (!flag);
	}
	
	/**
	 * Destroy an item in the inventory -RAK-
	 * 
	 * @param itemIndex Index in inventory array of item to destroy
	 */
	public static void destroyInvenItem(int itemIndex) {
		InvenType item = Treasure.inventory[itemIndex];
		if ((item.number > 1) && (item.subCategory <= Constants.ITEM_SINGLE_STACK_MAX)) {
			item.number--;
			Treasure.invenWeight -= item.weight;
		} else {
			Treasure.invenWeight -= item.weight * item.number;
			for (int j = itemIndex; j < Treasure.invenCounter - 1; j++) {
				Treasure.inventory[j + 1].copyInto(Treasure.inventory[j]);
			}
			Desc.copyIntoInventory(
					Treasure.inventory[Treasure.invenCounter - 1],
					Constants.OBJ_NOTHING);
			Treasure.invenCounter--;
		}
		Player.py.flags.status |= Constants.PY_STR_WGT;
	}
	
	/**
	 * Copies the object in the second argument over the first argument.
	 * However, the second always gets a number of one except for ammo etc.
	 * 
	 * @param targetItem Item receiving information
	 * @param sourceItem Item being copied
	 */
	public static void takeOneItem(InvenType targetItem, InvenType sourceItem) {
		sourceItem.copyInto(targetItem);
		if ((targetItem.number > 1) && (targetItem.subCategory >= Constants.ITEM_SINGLE_STACK_MIN)
				&& (targetItem.subCategory <= Constants.ITEM_SINGLE_STACK_MAX)) {
			targetItem.number = 1;
		}
	}
	
	/**
	 * Drops an item from t.inventory to given location -RAK-
	 * 
	 * @param itemIndex Index in inventory array of item to drop
	 * @param dropAll Whether to drop all of that item
	 */
	public static void dropInvenItem(int itemIndex, boolean dropAll) {
		if (Variable.cave[Player.y][Player.x].treasureIndex != 0) {
			Moria3.deleteObject(Player.y, Player.x);
		}
		int treasureIndex = Misc1.popTreasure();
		InvenType item = Treasure.inventory[itemIndex];
		item.copyInto(Treasure.treasureList[treasureIndex]);
		Variable.cave[Player.y][Player.x].treasureIndex = treasureIndex;
		
		if (itemIndex >= Constants.INVEN_WIELD) {
			Moria1.unequipItem(itemIndex, -1);
		} else {
			if (dropAll || item.number == 1) {
				Treasure.invenWeight -= item.weight * item.number;
				Treasure.invenCounter--;
				while (itemIndex < Treasure.invenCounter) {
					Treasure.inventory[itemIndex + 1].copyInto(Treasure.inventory[itemIndex]);
					itemIndex++;
				}
				Desc.copyIntoInventory(
						Treasure.inventory[Treasure.invenCounter],
						Constants.OBJ_NOTHING);
			} else {
				Treasure.treasureList[treasureIndex].number = 1;
				Treasure.invenWeight -= item.weight;
				item.number--;
			}
			String prt1 = Desc.describeObject(Treasure.treasureList[treasureIndex], true);
			String prt2 = String.format("Dropped %s", prt1);
			IO.printMessage(prt2);
		}
		Player.py.flags.status |= Constants.PY_STR_WGT;
	}
	
	private static boolean isSetDamageType(int typ, InvenType inv) {
		switch (typ) {
		case Sets.SET_CORRODES:
			return inv.isCorrosive();
		case Sets.SET_FLAMMABLE:
			return inv.isFlammable();
		case Sets.SET_FROST_DESTROY:
			return inv.doesFrostDestroy();
		case Sets.SET_ACID_AFFECT:
			return inv.doesAcidAffect();
		case Sets.SET_LIGHTNING_DESTROY:
			return inv.doesLightningDestroy();
		case Sets.SET_NULL:
			return inv.isNull();
		case Sets.SET_ACID_DESTROY:
			return inv.doesAcidDestroy();
		case Sets.SET_FIRE_DESTROY:
			return inv.doesFireDestroy();
		default:
			return false;
		}
	}
	
	/**
	 * Destroys a type of item on a given percent chance -RAK-
	 * 
	 * @param typ - Type of damage to use to destroy item
	 * @param perc - Percent chance of item being destroyed
	 * @return Number of items in inventory destroyed
	 */
	public static int damageInvenItem(int typ, int perc) {
		int i, j;
		
		j = 0;
		for (i = 0; i < Treasure.invenCounter; i++) {
			if (isSetDamageType(typ, Treasure.inventory[i]) && (Rnd.randomInt(100) < perc)) {
				destroyInvenItem(i);
				j++;
			}
		}
		return j;
	}
	
	/**
	 * Computes current weight limit -RAK-
	 * 
	 * @return The weight limit
	 */
	public static int weightLimit() {
		int weightCap = Player.py.stats.useStat[Constants.A_STR]
				* Constants.PLAYER_WEIGHT_CAP + Player.py.misc.weight;
		if (weightCap > 3000) {
			weightCap = 3000;
		}
		return weightCap;
	}
	
	/**
	 * Check if an object can be picked up
	 * 
	 * @param item The object being picked up
	 * @return Whether the object can fit in the player's inventory
	 */
	public static boolean canPickUpItem(InvenType item) {
		// this code must be identical to the inven_carry() code below
		if (Treasure.invenCounter < Constants.INVEN_WIELD) {
			return true;
		} else if (item.subCategory >= Constants.ITEM_SINGLE_STACK_MIN) {
			for (int i = 0; i < Treasure.invenCounter; i++) {
				if (Treasure.inventory[i].category == item.category
						&& Treasure.inventory[i].subCategory == item.subCategory
						// make sure the number field doesn't overflow
						&& Treasure.inventory[i].number + item.number < 256
						// they always stack (subval < 192), or else they have same p1
						&& (item.subCategory < Constants.ITEM_GROUP_MIN
								|| Treasure.inventory[i].misc == item.misc)
						// only stack if both or neither are identified
						&& (Desc.isKnownByPlayer(Treasure.inventory[i])
								== Desc.isKnownByPlayer(item))) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if picking up an object would change the player's speed
	 * 
	 * @param item The object being picked up
	 * @return Whether picking up the object would change the player's speed
	 */
	public static boolean checkItemWeight(InvenType item) {
		int weightLimit = weightLimit();
		int newInvenWeight = item.number * item.weight + Treasure.invenWeight;
		if (weightLimit < newInvenWeight) {
			weightLimit = newInvenWeight / (weightLimit + 1);
		} else {
			weightLimit = 0;
		}
		
		return Variable.isPackHeavy == weightLimit;
	}
	
	/**
	 * Are we strong enough for the current pack and weapon? -CJS-
	 */
	public static void checkStrength() {
		InvenType item = Treasure.inventory[Constants.INVEN_WIELD];
		if (item.category != Constants.TV_NOTHING
				&& (Player.py.stats.useStat[Constants.A_STR] * 15 < item.weight)) {
			if (!Variable.isWeaponHeavy) {
				IO.printMessage("You have trouble wielding such a heavy weapon.");
				Variable.isWeaponHeavy = true;
				Moria1.calcBonuses();
			}
		} else if (Variable.isWeaponHeavy) {
			Variable.isWeaponHeavy = false;
			if (item.category != Constants.TV_NOTHING) {
				IO.printMessage("You are strong enough to wield your weapon.");
			}
			Moria1.calcBonuses();
		}
		int weightLimit = weightLimit();
		if (weightLimit < Treasure.invenWeight) {
			weightLimit = Treasure.invenWeight / (weightLimit + 1);
		} else {
			weightLimit = 0;
		}
		if (Variable.isPackHeavy != weightLimit) {
			if (Variable.isPackHeavy < weightLimit) {
				IO.printMessage("Your pack is so heavy that it slows you down.");
			} else {
				IO.printMessage("You move more easily under the weight of your pack.");
			}
			Moria1.changeSpeed(weightLimit - Variable.isPackHeavy);
			Variable.isPackHeavy = weightLimit;
		}
		Player.py.flags.status &= ~Constants.PY_STR_WGT;
	}
	
	/**
	 * Add an item to players inventory.
	 * Return the item position for a description if needed. -RAK-
	 * 
	 * @param item The item being added to the player's inventory
	 * @return The index in the inventory array where the item was placed
	 */
	public static int pickUpItem(InvenType item) {
		// this code must be identical to the inven_check_num() code above
		int typ = item.category;
		int subt = item.subCategory;
		boolean known1p = Desc.isKnownByPlayer(item);
		boolean always_known1p = (Desc.getObjectOffset(item) == -1);
		int invenIndex;
		
		// Now, check to see if player can carry object
		for (invenIndex = 0; ; invenIndex++) {
			InvenType invenItem = Treasure.inventory[invenIndex];
			if ((typ == invenItem.category) && (subt == invenItem.subCategory)
					&& (subt >= Constants.ITEM_SINGLE_STACK_MIN)
					&& (invenItem.number + item.number < 256)
					&& ((subt < Constants.ITEM_GROUP_MIN) || (invenItem.misc == item.misc))
					// only stack if both or neither are identified
					&& (known1p == Desc.isKnownByPlayer(invenItem))) {
				invenItem.number += item.number;
				break;
				
			/* For items which are always known1p, i.e. never have a 'color',
			 * insert them into the t.inventory in sorted order.  */
			} else if ((typ == invenItem.category && subt < invenItem.subCategory && always_known1p)
					|| (typ > invenItem.category)) {
				for (int i = Treasure.invenCounter - 1; i >= invenIndex; i--) {
					Treasure.inventory[i].copyInto(Treasure.inventory[i + 1]);
				}
				item.copyInto(Treasure.inventory[invenIndex]);
				Treasure.invenCounter++;
				break;
			}
		}
		
		Treasure.invenWeight += item.number * item.weight;
		Player.py.flags.status |= Constants.PY_STR_WGT;
		return invenIndex;
	}
	
	/**
	 * Returns spell chance of failure for spell. -RAK-
	 * 
	 * @param spellIndex The spell being checked
	 * @return The percent chance of spell failing
	 */
	public static int spellFailChance(int spellIndex) {
		SpellType spell = Player.magicSpell[Player.py.misc.playerClass - 1][spellIndex];
		int chance = spell.failChance - 3 * (Player.py.misc.level - spell.level);
		int stat;
		
		if (Player.Class[Player.py.misc.playerClass].spell == Constants.MAGE) {
			stat = Constants.A_INT;
		} else {
			stat = Constants.A_WIS;
		}
		
		chance -= 3 * (adjustStat(stat) - 1);
		if (spell.manaCost > Player.py.misc.currMana) {
			chance += 5 * (spell.manaCost - Player.py.misc.currMana);
		}
		if (chance > 95) {
			chance = 95;
		} else if (chance < 5) {
			chance = 5;
		}
		
		return chance;
	}
	
	/**
	 * Print list of spells. -RAK-
	 * 
	 * @param spells Indices of spells to print
	 * @param num Number of spells to print
	 * @param comment Whether to comment on player's knowledge of each spell
	 * @param nonconsec Whether or not to leave holes in character choices
	 */
	/* if nonconsec is -1: spells numbered consecutively from 'a' to 'a'+num
	                  >=0: spells numbered by offset from nonconsec */
	public static void printSpells(int[] spells, int num, boolean comment, int nonconsec) {
		int col;
		if (comment) {
			col = 22;
		} else {
			col = 31;
		}
		
		int offset = ((Player.Class[Player.py.misc.playerClass].spell == Constants.MAGE) ?
						Constants.SPELL_OFFSET : Constants.PRAYER_OFFSET);
		IO.eraseLine(1, col);
		IO.putBuffer("Name", 1, col + 5);
		IO.putBuffer("Lv Mana Fail", 1, col + 35);
		
		// only show the first 22 choices
		if (num > 22) {
			num = 22;
		}
		if (num > spells.length) {
			num = spells.length;
		}
		
		for (int i = 0; i < num; i++) {
			int spellIndex = spells[i];
			SpellType spell = Player.magicSpell[Player.py.misc.playerClass - 1][spellIndex];
			String p;
			
			if (!comment) {
				p = "";
			} else if ((Player.spellForgotten & (1L << spellIndex)) != 0) {
				p = " forgotten";
			} else if ((Player.spellLearned & (1L << spellIndex)) == 0) {
				p = " unknown";
			} else if ((Player.spellWorked & (1L << spellIndex)) == 0) {
				p = " untried";
			} else {
				p = "";
			}
			
			// determine whether or not to leave holes in character choices,
			// nonconsec -1 when learning spells, consec offset>=0 when asking which
			// spell to cast
			char spellChar;
			if (nonconsec == -1) {
				spellChar = (char)('a' + i);
			} else {
				spellChar = (char)('a' + spellIndex - nonconsec);
			}
			String outVal = String.format("  %c) %-30s%2d %4d %3d%%%s",
					spellChar, Player.spellNames[spellIndex + offset],
					spell.level, spell.manaCost, spellFailChance(spellIndex),
					p);
			IO.print(outVal, 2 + i, col);
		}
	}
	
	/**
	 * Returns spell pointer. -RAK-
	 * 
	 * @param spells Array of spell indices
	 * @param prompt
	 * @param firstSpell Index of the first spell the player knows
	 * @return The index of the chosen spell, -1 if no spell chosen,
	 *         or -2 if attempted to choose an unknown spell
	 */
	public static int getSpell(int[] spells, String prompt, int firstSpell) {
		int chosenSpell = -1;
		boolean redraw = false;
		
		prompt = String.format("(Spells %c-%c, *=List, <ESCAPE>=exit) %s",
				spells[0] + 'a' - firstSpell,
				spells[spells.length - 1] + 'a' - firstSpell,
				prompt);
		
		int offset;
		if (Player.Class[Player.py.misc.playerClass].spell == Constants.MAGE) {
			offset = Constants.SPELL_OFFSET;
		} else {
			offset = Constants.PRAYER_OFFSET;
		}
		
		boolean spellHasBeenChosen = false;
		CharPointer choice = new CharPointer();
		while (!spellHasBeenChosen && IO.getCommand(prompt, choice)) {
			if (Character.isUpperCase(choice.value())) {
				int spellChoice = choice.value() - 'A' + firstSpell;
				chosenSpell = -2;
				// verify that this is in spell[], at most 22 entries in spell[]
				for (int spellIndex : spells) {
					if (spellChoice == spellIndex) {
						chosenSpell = spellChoice;
						SpellType spell = Player.magicSpell[Player.py.misc.playerClass - 1][chosenSpell];
						
						String msgConfirmCast = String.format(
								"Cast %s (%d mana, %d%% fail)?",
								Player.spellNames[chosenSpell + offset],
								spell.manaCost,
								spellFailChance(chosenSpell));
						if (IO.getCheck(msgConfirmCast)) {
							spellHasBeenChosen = true;
						} else {
							chosenSpell = -1;
						}
						break;
					}
				}
			} else if (Character.isLowerCase(choice.value())) {
				int spellChoice = choice.value() - 'a' + firstSpell;
				chosenSpell = -2;
				// verify that this is in spell[], at most 22 entries in spell[]
				for (int spellIndex : spells) {
					if (spellChoice == spellIndex) {
						chosenSpell = spellChoice;
						spellHasBeenChosen = true;
						break;
					}
				}
			} else if (choice.value() == '*') {
				// only do this drawing once
				if (!redraw) {
					IO.saveScreen();
					redraw = true;
					printSpells(spells, spells.length, false, firstSpell);
				}
			} else if (Character.isLetter(choice.value())) {
				chosenSpell = -2;
			} else {
				chosenSpell = -1;
				IO.bell();
			}
			
			if (chosenSpell == -2) {
				String msgUnknownSpell = String.format("You don't know that %s.",
						((offset == Constants.SPELL_OFFSET) ? "spell" : "prayer"));
				IO.printMessage(msgUnknownSpell);
			}
		}
		
		if (redraw) {
			IO.restoreScreen();
		}
		
		IO.eraseLine(Constants.MSG_LINE, 0);
		
		return chosenSpell;
	}
	
	/**
	 * Calculate the number of spells the player is allowed to know
	 * at their current level and intelligence/wisdom.
	 * 
	 * @param stat
	 * @return The number of spells allowed
	 */
	private static int numSpellsAllowed(final int stat) {
		PlayerMisc misc = Player.py.misc;
		int levels = misc.level
				- Player.Class[misc.playerClass].firstSpellLevel + 1;
		
		switch (adjustStat(stat)) {
		case 0:
			return 0;
		case 1:
		case 2:
		case 3:
			return 1 * levels;
		case 4:
		case 5:
			return 3 * levels / 2;
		case 6:
			return 2 * levels;
		case 7:
			return 5 * levels / 2;
		default:
			return 0;
		}
	}
	
	/**
	 * Calculate number of spells player should have, and learn/forget spells
	 * until that number is met -JEW-
	 * 
	 * @param stat
	 */
	public static void calcSpells(final int stat) {
		PlayerMisc misc = Player.py.misc;
		SpellType[] spells = Player.magicSpell[misc.playerClass - 1];
		
		String spellType;
		int offset;
		if (stat == Constants.A_INT) {
			spellType = "spell";
			offset = Constants.SPELL_OFFSET;
		} else {
			spellType = "prayer";
			offset = Constants.PRAYER_OFFSET;
	    }
		
		// check to see if know any spells greater than level, eliminate them
		for (int i = 31, mask = 0x80000000; mask != 0; mask >>>= 1, i--) {
			if ((mask & Player.spellLearned) != 0) {
				if (spells[i].level > misc.level) {
					Player.spellLearned &= ~mask;
					Player.spellForgotten |= mask;
					String msgForgot = String.format(
							"You have forgotten the %s of %s.",
							spellType,
							Player.spellNames[i + offset]);
					IO.printMessage(msgForgot);
				} else {
					break;
				}
			}
		}
		
		// calc number of spells allowed
		int numAllowed = numSpellsAllowed(stat);
		int numKnown = 0;
		for (int mask = 0x1; mask != 0; mask <<= 1) {
			if ((mask & Player.spellLearned) != 0) {
				numKnown++;
			}
		}
		
		int newSpells = numAllowed - numKnown;
		if (newSpells > 0) {
			// remember forgotten spells while forgotten spells exist or newSpells is
			// positive, remember the spells in the order that they were learned
			for (int i = 0; (Player.spellForgotten != 0
					&& newSpells != 0
					&& (i < numAllowed) && (i < 32));
					i++) {
				// spellOrder is (i+1)th spell learned
				int spellOrder = Player.spellOrder[i];
				
				// shifting by amounts greater than number of bits in int gives
				// an undefined result, so don't shift for unknown spells
				int mask;
				if (spellOrder == 99) {
					mask = 0x0;
				} else {
					mask = 1 << spellOrder;
				}
				if ((mask & Player.spellForgotten) != 0) {
					if (spells[spellOrder].level <= misc.level) {
						newSpells--;
						Player.spellForgotten &= ~mask;
						Player.spellLearned |= mask;
						String msgRemembered = String.format(
								"You have remembered the %s of %s.",
								spellType,
								Player.spellNames[spellOrder + offset]);
						IO.printMessage(msgRemembered);
					} else {
						numAllowed++;
					}
				}
			}
			
			if (newSpells > 0) {
				// determine which spells player can learn
				// must check all spells here, in gainSpell() we actually check
				// if the books are present
				int spellFlags = 0x7FFFFFFF & ~Player.spellLearned;
				
				int numLearnableSpells = 0;
				for (int j = 0, mask = 0x1; spellFlags != 0; mask <<= 1, j++) {
					if ((spellFlags & mask) != 0) {
						spellFlags &= ~mask;
						if (spells[j].level <= misc.level) {
							numLearnableSpells++;
						}
					}
				}
				
				if (newSpells > numLearnableSpells) {
					newSpells = numLearnableSpells;
				}
			}
		} else if (newSpells < 0) {
			// forget spells until newSpells is zero or no more spells are known
			// spells are forgotten in the opposite order that they were learned
			for (int i = 31; newSpells != 0 && Player.spellLearned != 0; i--) {
				// spellOrder is the (i+1)th spell learned
				int spellOrder = Player.spellOrder[i];
				
				// shifting by amounts greater than number of bits in int gives
				// an undefined result, so don't shift for unknown spells
				int mask;
				if (spellOrder == 99) {
					mask = 0x0;
				} else {
					mask = 1 << spellOrder;
				}
				
				if ((mask & Player.spellLearned) != 0) {
					Player.spellLearned &= ~mask;
					Player.spellForgotten |= mask;
					newSpells++;
					String msgForgot = String.format(
							"You have forgotten the %s of %s.",
							spellType,
							Player.spellNames[spellOrder + offset]);
					IO.printMessage(msgForgot);
				}
			}
			
			newSpells = 0;
		}
		
		if (newSpells != Player.py.flags.newSpells) {
			if (newSpells > 0 && Player.py.flags.newSpells == 0) {
				String msgCanLearn = String.format(
						"You can learn some new %ss now.",
						spellType);
				IO.printMessage(msgCanLearn);
			}
			
			Player.py.flags.newSpells = newSpells;
			Player.py.flags.status |= Constants.PY_STUDY;
		}
	}
	
	/**
	 * Gain spells when player wants to -JEW-
	 */
	public static void gainSpells() {
		// Priests don't need light because they get spells from their god,
		// so only fail when can't see if player has MAGE spells. This check
		// is done below.
		if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused.");
			return;
		}
		
		PlayerMisc misc = Player.py.misc;
		int stat;
		int offset;
		if (Player.Class[misc.playerClass].spell == Constants.MAGE) {
			stat = Constants.A_INT;
			offset = Constants.SPELL_OFFSET;
			
			// People with MAGE spells can't learn spells if they can't read their books.
			if (Player.py.flags.blind > 0) {
				IO.printMessage("You can't see to read your spell book!");
				return;
			} else if (Moria1.playerHasNoLight()) {
				IO.printMessage("You have no light to read by.");
				return;
			}
		} else {
			stat = Constants.A_WIS;
			offset = Constants.PRAYER_OFFSET;
		}
		
		int newSpells = Player.py.flags.newSpells;
		if (newSpells == 0) {
			String msgCantLearn = String.format(
					"You can't learn any new %ss!",
					((stat == Constants.A_INT) ? "spell" : "prayer"));
			IO.printMessage(msgCantLearn);
			Variable.freeTurnFlag = true;
			return;
		}
		
		// determine which spells player can learn
		// mages need the book to learn a spell, priests do not need the book
		int spellFlags;
		if (stat == Constants.A_INT) {
			spellFlags = 0;
			for (int i = 0; i < Treasure.invenCounter; i++) {
				if (Treasure.inventory[i].category == Constants.TV_MAGIC_BOOK) {
					spellFlags |= Treasure.inventory[i].flags;
				}
			}
		} else {
			spellFlags = 0x7FFFFFFF;
		}
		
		// clear bits for spells already learned
		spellFlags &= ~Player.spellLearned;
		
		SpellType[] spells = Player.magicSpell[misc.playerClass - 1];
		int spellCount = 0;
		int[] spellIndices = new int[31];
		for (int j = 0, mask = 0x1; spellFlags != 0; mask <<= 1, j++) {
			if ((spellFlags & mask) != 0) {
				spellFlags &= ~mask;
				if (spells[j].level <= misc.level) {
					spellIndices[spellCount] = j;
					spellCount++;
				}
			}
		}
		
		int diffSpells = 0;
		if (newSpells > spellCount) {
			IO.printMessage("You seem to be missing a book.");
			diffSpells = newSpells - spellCount;
			newSpells = spellCount;
		}
		
		int lastKnown;
		for (lastKnown = 0; lastKnown < 32; lastKnown++) {
			if (Player.spellOrder[lastKnown] == 99) {
				break;
			}
		}
		
		if (newSpells != 0 && stat == Constants.A_INT) {
			// get to choose which mage spells will be learned
			IO.saveScreen();
			printSpells(spellIndices, spellCount, false, -1);
			
			CharPointer query = new CharPointer();
			while (newSpells != 0 && IO.getCommand("Learn which spell?", query)) {
				int spellChoice = query.value() - 'a';
				// test j < 23 in case i is greater than 22, only 22 spells
				// are actually shown on the screen, so limit choice to those
				if (spellChoice >= 0 && spellChoice < spellCount && spellChoice < 22) {
					newSpells--;
					Player.spellLearned |= 1L << spellIndices[spellChoice];
					Player.spellOrder[lastKnown++] = spellIndices[spellChoice];
					for (; spellChoice <= spellCount - 1; spellChoice++) {
						spellIndices[spellChoice] = spellIndices[spellChoice + 1];
					}
					spellCount--;
					IO.eraseLine (spellChoice + 1, 31);
					printSpells (spellIndices, spellCount, false, -1);
				} else {
					IO.bell();
				}
			}
			IO.restoreScreen();
		} else {
			// pick a prayer at random
			while (newSpells != 0) {
				int index = Rnd.randomInt(spellCount) - 1;
				Player.spellLearned |= 1L << spellIndices[index];
				Player.spellOrder[lastKnown++] = spellIndices[index];
				String msgLearned = String.format(
						"You have learned the prayer of %s.",
						Player.spellNames[spellIndices[index] + offset]);
				IO.printMessage(msgLearned);
				for (; index <= spellCount - 1; index++) {
					spellIndices[index] = spellIndices[index + 1];
				}
				spellCount--;
				newSpells--;
			}
		}
		
		Player.py.flags.newSpells = newSpells + diffSpells;
		if (Player.py.flags.newSpells == 0) {
			Player.py.flags.status |= Constants.PY_STUDY;
		}
		
		// set the mana for first level characters when they learn their
		// first spell
		if (Player.py.misc.maxMana == 0) {
			calcMana(stat);
		}
	}
	
	/** 
	 * Gain some mana if you know at least one spell -RAK-
	 * 
	 * @param stat
	 */
	public static void calcMana(int stat) {
		PlayerMisc misc = Player.py.misc;
		if (Player.spellLearned != 0) {
			int new_mana;
			int levels = misc.level - Player.Class[misc.playerClass].firstSpellLevel + 1;
			switch (adjustStat(stat)) {
			case 0:
				new_mana = 0;
				break;
			case 1:
			case 2:
				new_mana = 1 * levels;
				break;
			case 3:
				new_mana = 3 * levels / 2;
				break;
			case 4:
				new_mana = 2 * levels;
				break;
			case 5:
				new_mana = 5 * levels / 2;
				break;
			case 6:
				new_mana = 3 * levels;
				break;
			case 7:
				new_mana = 4 * levels;
				break;
			default:
				new_mana = 0;
			}
			// increment mana by one, so that first level chars have 2 mana
			if (new_mana > 0) {
				new_mana++;
			}
			
			// mana can be zero when creating character
			if (misc.maxMana != new_mana) {
				if (misc.maxMana != 0) {
					// change current mana proportionately to change of max mana,
					// divide first to avoid overflow, little loss of accuracy
					int value = ((misc.currMana << 16) + misc.currManaFraction)
							/ misc.maxMana * new_mana;
					misc.currMana = value >> 16;
					misc.currManaFraction = value & 0xFFFF;
				} else {
					misc.currMana = new_mana;
					misc.currManaFraction = 0;
				}
				misc.maxMana = new_mana;
				// can't print mana here, may be in store or t.inventory mode
				Player.py.flags.status |= Constants.PY_MANA;
			}
		} else if (misc.maxMana != 0) {
			misc.maxMana = 0;
			misc.currMana = 0;
			// can't print mana here, may be in store or t.inventory mode
			Player.py.flags.status |= Constants.PY_MANA;
		}
	}
	
	/**
	 * Increases hit points and level -RAK-
	 */
	public static void gainLevel() {
		PlayerMisc misc = Player.py.misc;
		misc.level++;
		String msgLevelUp = String.format("Welcome to level %d.", misc.level);
		IO.printMessage(msgLevelUp);
		calcHitpoints();
		
		int expNeeded = Player.exp[misc.level - 1] * misc.expFactor / 100;
		if (misc.currExp > expNeeded) {
			// lose some of the 'extra' exp when gaining several levels at once
			int expDiff = misc.currExp - expNeeded;
			misc.currExp = expNeeded + (expDiff / 2);
		}
		
		printLevel();
		printPlayerTitle();
		ClassType playerClass = Player.Class[misc.playerClass];
		if (playerClass.spell == Constants.MAGE) {
			calcSpells(Constants.A_INT);
			calcMana(Constants.A_INT);
		} else if (playerClass.spell == Constants.PRIEST) {
	      calcSpells(Constants.A_WIS);
	      calcMana(Constants.A_WIS);
	    }
	}

	/**
	 * Prints experience -RAK-
	 */
	public static void printExperience() {
		PlayerMisc misc = Player.py.misc;
		if (misc.currExp > Constants.MAX_EXP) {
			misc.currExp = Constants.MAX_EXP;
		}
		while (misc.level < Constants.MAX_PLAYER_LEVEL
				&& (Player.exp[misc.level - 1] * misc.expFactor / 100) <= misc.currExp) {
			gainLevel();
		}
		if (misc.currExp > misc.maxExp) {
			misc.maxExp = misc.currExp;
		}
		printLong(misc.currExp, 14, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Calculate the player's hit points
	 */
	public static void calcHitpoints() {
		PlayerMisc misc = Player.py.misc;
		int hitpoints = Player.hitpoints[misc.level - 1]
				+ (adjustConstitution() * misc.level);
		
		// always give at least one point per level + 1
		if (hitpoints < (misc.level + 1)) {
			hitpoints = misc.level + 1;
		}
		
		if ((Player.py.flags.status & Constants.PY_HERO) != 0) {
			hitpoints += 10;
		}
		if ((Player.py.flags.status & Constants.PY_SHERO) != 0) {
			hitpoints += 20;
		}
		
		// mhp can equal zero while character is being created
		if ((hitpoints != misc.maxHitpoints) && (misc.maxHitpoints != 0)) {
			// change current hit points proportionately to change of mhp,
			// divide first to avoid overflow, little loss of accuracy
			int value = ((misc.currHitpoints << 16) + misc.currHitpointsFraction)
					/ misc.maxHitpoints * hitpoints;
			misc.currHitpoints = value >> 16;
			misc.currHitpointsFraction = value & 0xFFFF;
			misc.maxHitpoints = hitpoints;
			
			// can't print hit points here, may be in store or t.inventory mode
			Player.py.flags.status |= Constants.PY_HP;
		}
	}
	
	/**
	 * Inserts a string into a string
	 * 
	 * @param str Original string
	 * @param toReplace String to replace
	 * @param toInsert String to replace with
	 * @return The final string
	 */
	public static String insertString(String str, String toReplace, String toInsert) {
		return str.replaceAll(toReplace, toInsert);
	}
	
	/**
	 * Insert a number into a string
	 * 
	 * @param str Original string
	 * @param toReplace String to replace
	 * @param number Number to replace with
	 * @param showSign Whether to show the sign
	 * @return The final string
	 */
	public static String insertLong(String str, String toReplace,
			int number, boolean showSign) {
		String num;
		if (showSign) {
			num = String.format("%+d", number);
		} else {
			num = Integer.toString(number);
		}
		return str.replaceAll(toReplace, num);
	}
	
	/**
	 * Lets anyone enter wizard mode after a disclaimer... - JEW-
	 */
	public static boolean enterWizardMode() {
		boolean answer = false;
		
		if (Variable.noScore == 0) {
			IO.printMessage("Wizard mode is for debugging and experimenting.");
			answer = IO.getCheck("The game will not be scored if you enter wizard mode. Are you sure?");
		}
		if (Variable.noScore != 0 || answer) {
			Variable.noScore = 1;
			Variable.isWizard = true;
			return true;
		}
		return false;
	}
	
	/**
	 * Weapon weight VS strength and dexterity -RAK-
	 * 
	 * @param weight Weight of the weapon
	 * @param weaponToHit Stores the weapon's tohit
	 * @return The value of the attack blow
	 */
	public static int attackBlows(int weight, IntPointer weaponToHit) {
		int strength = Player.py.stats.useStat[Constants.A_STR];
		int dexterity = Player.py.stats.useStat[Constants.A_DEX];
		if (strength * 15 < weight) {
			weaponToHit.value(strength * 15 - weight);
			return 1;
		}
		
		weaponToHit.value(0);
		int dexIndex;
		if (dexterity < 10) {
			dexIndex = 0;
		} else if (dexterity < 19) {
			dexIndex = 1;
		} else if (dexterity < 68) {
			dexIndex = 2;
		} else if (dexterity < 108) {
			dexIndex = 3;
		} else if (dexterity < 118) {
			dexIndex = 4;
		} else {
			dexIndex = 5;
		}
		
		int adjWeight = (strength * 10 / weight);
		int strIndex;
		if (adjWeight < 2) {
			strIndex = 0;
		} else if (adjWeight < 3) {
			strIndex = 1;
		} else if (adjWeight < 4) {
			strIndex = 2;
		} else if (adjWeight < 5) {
			strIndex = 3;
		} else if (adjWeight < 7) {
			strIndex = 4;
		} else if (adjWeight < 9) {
			strIndex = 5;
		} else {
			strIndex = 6;
		}
		
		return Tables.blowsTable[strIndex][dexIndex];
	}
	
	/**
	 * Special damage due to magical abilities of object -RAK-
	 * 
	 * @param weapon Weapon being used
	 * @param tdam Original damage of the attack
	 * @param monsterIndex Index in the creature list of the monster being attacked
	 * @return Final damage of the attack
	 */
	public static int totalDamage(InvenType weapon, int tdam, int monsterIndex) {
		if ((weapon.flags & Constants.TR_EGO_WEAPON) != 0
				&& ((weapon.category >= Constants.TV_SLING_AMMO
					&& weapon.category <= Constants.TV_ARROW)
				|| (weapon.category >= Constants.TV_HAFTED
					&& weapon.category <= Constants.TV_SWORD)
				|| (weapon.category == Constants.TV_FLASK))) {
			CreatureType monster = Monsters.creatureList[monsterIndex];
			MonsterRecallType recall = Variable.creatureRecall[monsterIndex];
			// Slay Dragon
			if ((monster.cdefense & Constants.CD_DRAGON) != 0
					&& (weapon.flags & Constants.TR_SLAY_DRAGON) != 0) {
				tdam = tdam * 4;
				recall.cdefense |= Constants.CD_DRAGON;
			
			// Slay Undead
			} else if ((monster.cdefense & Constants.CD_UNDEAD) != 0
					&& (weapon.flags & Constants.TR_SLAY_UNDEAD) != 0) {
				tdam = tdam * 3;
				recall.cdefense |= Constants.CD_UNDEAD;
			
			// Slay Animal
			} else if ((monster.cdefense & Constants.CD_ANIMAL) != 0
					&& (weapon.flags & Constants.TR_SLAY_ANIMAL) != 0) {
				tdam = tdam * 2;
				recall.cdefense |= Constants.CD_ANIMAL;
			
			// Slay Evil
			} else if ((monster.cdefense & Constants.CD_EVIL) != 0
					&& (weapon.flags & Constants.TR_SLAY_EVIL) != 0) {
				tdam = tdam * 2;
				recall.cdefense |= Constants.CD_EVIL;
			
			// Frost
			} else if ((monster.cdefense & Constants.CD_FROST) != 0
					&& (weapon.flags & Constants.TR_FROST_BRAND) != 0) {
				tdam = tdam * 3 / 2;
				recall.cdefense |= Constants.CD_FROST;
			
			// Fire
			} else if ((monster.cdefense & Constants.CD_FIRE) != 0
					&& (weapon.flags & Constants.TR_FLAME_TONGUE) != 0) {
				tdam = tdam * 3 / 2;
				recall.cdefense |= Constants.CD_FIRE;
			}
		}
		return tdam;
	}
	
	/**
	 * Critical hits, Nasty way to die. -RAK-
	 * 
	 * @param weight Weight of the weapon
	 * @param plusses Plusses to hit
	 * @param damage Base damage
	 * @param attackType Attack type
	 * @return Final damage
	 */
	public static int criticalBlow(int weight, int plusses, int damage, int attackType) {
		int critical = damage;
		// Weight of weapon, plusses to hit, and character level all
		// contribute to the chance of a critical
		PlayerMisc misc = Player.py.misc;
		int critChance = weight
				+ 5 * plusses
				+ Player.classLevelAdjust[misc.playerClass][attackType] * misc.level;
		if (Rnd.randomInt(5000) <= critChance) {
			weight += Rnd.randomInt(650);
			if (weight < 400) {
				critical = 2 * damage + 5;
				IO.printMessage("It was a good hit! (x2 damage)");
			} else if (weight < 700) {
				critical = 3 * damage + 10;
				IO.printMessage("It was an excellent hit! (x3 damage)");
			} else if (weight < 900) {
				critical = 4 * damage + 15;
				IO.printMessage("It was a superb hit! (x4 damage)");
			} else {
				critical = 5 * damage + 20;
				IO.printMessage("It was a *GREAT* hit! (x5 damage)");
			}
		}
		return critical;
	}
	
	/**
	 * Given direction "dir", returns new row, column location -RAK-
	 * 
	 * @param dir direction to to check for available move
	 * @param y current vertical position, stores new vertical position
	 * @param x current horizontal position, stores new horizontal position
	 * @return whether it is legal to move in the given direction
	 */
	public static boolean canMoveDirection(int dir, IntPointer y, IntPointer x) {
		int dX = 0;
		if (((dir - 1) % 3) == 0) { // 1, 4, 7
			dX = -1;
		} else if ((dir % 3) == 0) { // 3, 6, 9
			dX = 1;
		}
		
		int dY = 0;
		if (dir < 4) { // 1, 2, 3
			dY = 1;
		} else if (dir > 6) { // 7, 8, 9
			dY = -1;
		}
		
		int newRow = y.value() + dY;
		int newCol = x.value() + dX;
		
		boolean canMove = false;
		if ((newRow >= 0)
				&& (newRow < Variable.currHeight)
				&& (newCol >= 0)
				&& (newCol < Variable.currWidth)) {
			y.value(newRow);
			x.value(newCol);
			canMove = true;
		}
		
		return canMove;
	}
	
	/**
	 * Saving throws for player character. -RAK-
	 * 
	 * @return Whether the player saved
	 */
	public static boolean playerSavingThrow() {
		return Rnd.randomInt(100) <= (Player.py.misc.savingThrow
				+ adjustStat(Constants.A_WIS)
				+ (Player.classLevelAdjust[Player.py.misc.playerClass][Constants.CLA_SAVE]
						* Player.py.misc.level / 3));
	}
	
	/**
	 * Finds range of item in inventory list. -RAK-
	 * 
	 * @param category1 Lowest category of items in range to find 
	 * @param category2 Highest category of items in range to find, or
	 *                  TV_NEVER to only search for a single category of item
	 * @param first Pointer to the index of the first matching item found
	 * @param last Pointer to the index of the last matching item found
	 * @return Whether any matching items were found
	 */
	public static boolean findRange(int category1, int category2, IntPointer first, IntPointer last) {
		first.value(-1);
		last.value(-1);
		
		boolean found = false;
		for (int i = 0; i < Treasure.invenCounter; i++) {
			InvenType item = Treasure.inventory[i];
			int category = item.category;
			if (!found) { // haven't found a matching item yet
				if (category == category1 || category == category2) {
					found = true;
					first.value(i);
				}
			} else {
				if (category != category1 && category != category2) {
					// item's category outside range
					last.value(i - 1);
					break;
				}
			}
		}
		
		// reached the end of the inventory without reaching end of range
		if (found && (last.value() == -1)) {
			last.value(Treasure.invenCounter - 1);
		}
		
		return found;
	}
	
	/**
	 * Teleport the player to a new location -RAK-
	 * 
	 * @param distance Distance from current location to new location
	 */
	public static void teleport(int distance) {
		int y = 0;
		int x = 0;
		do {
			y = Rnd.randomInt(Variable.currHeight) - 1;
			x = Rnd.randomInt(Variable.currWidth) - 1;
			while (Misc1.distance(y, x, Player.y, Player.x) > distance) {
				y += ((Player.y - y) / 2);
				x += ((Player.x - x) / 2);
			}
		} while ((Variable.cave[y][x].fval >= Constants.MIN_CLOSED_SPACE)
				|| (Variable.cave[y][x].creatureIndex >= 2));
		
		Moria1.moveCreatureRecord(Player.y, Player.x, y, x);
		
		for (int i = Player.y - 1; i <= Player.y + 1; i++) {
			for (int j = Player.x - 1; j <= Player.x + 1; j++) {
				Variable.cave[i][j].tempLight = false;
				Moria1.lightUpSpot(i, j);
			}
		}
		Moria1.lightUpSpot(Player.y, Player.x);
		Player.y = y;
		Player.x = x;
		Misc4.checkView();
		Creature.creatures(false);
		Variable.teleportFlag = false;
	}
}
