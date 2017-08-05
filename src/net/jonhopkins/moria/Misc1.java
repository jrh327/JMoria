/*
 * Misc1.java: misc utility and initialization code, magic objects code
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
import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.MonsterType;

public class Misc1 {
	
	private Misc1() { }
	
	/**
	 * Check the day-time strings to see if open -RAK-
	 * 
	 * @return Always true, not using play-time hours anymore
	 */
	public static boolean checkTime() {
		return true;
	}
	
	/**
	 * Returns position of first set bit and clears that bit -RAK-
	 * 
	 * @param test The integer being checked for its first set bit
	 * @return The position of the first set bit
	 */
	public static int firstBitPos(IntPointer test) {
		for (int i = 0, mask = 0x1; i < 32; i++) {
			if ((test.value() & mask) != 0) {
				test.value(test.value() & ~mask);
				return i;
			}
			mask <<= 1;
		}
		
		// no one bits found
		return -1;
	}
	
	/**
	 * Checks a co-ordinate for in bounds status -RAK-
	 * 
	 * @param y The vertical position of the point to check
	 * @param x The horizontal position of the point to check
	 * @return True if the coordinate is within the level, otherwise false
	 */
	public static boolean isInBounds(int y, int x) {
		return (y > 0)
				&& (y < Variable.currHeight - 1)
				&& (x > 0)
				&& (x < Variable.currWidth - 1);
	}
	
	/**
	 * Calculates current boundaries -RAK-
	 */
	public static void calculatePanelBounds() {
		Variable.panelRowMin = Variable.panelRow * (Constants.SCREEN_HEIGHT / 2);
		Variable.panelRowMax = Variable.panelRowMin + Constants.SCREEN_HEIGHT - 1;
		Variable.panelRowPrt = Variable.panelRowMin - 1;
		Variable.panelColMin = Variable.panelCol * (Constants.SCREEN_WIDTH / 2);
		Variable.panelColMax = Variable.panelColMin + Constants.SCREEN_WIDTH - 1;
		Variable.panelColPrt = Variable.panelColMin - 13;
	}
	
	/**
	 * Given an row (y) and col (x), this routine detects when a move
	 * off the screen has occurred and figures new borders. -RAK-
	 * 
	 * @param y The vertical position of the point to check
	 * @param x The horizontal position of the point to check
	 * @param force Force the panel bounds to be recalculated, useful for 'W'here.
	 * @return True if the panel bounds have been recalculated, otherwise false
	 */
	public static boolean getPanel(int y, int x, boolean force) {
		int panelRow = Variable.panelRow;
		int panelCol = Variable.panelCol;
		
		if (force || (y < Variable.panelRowMin + 2) || (y > Variable.panelRowMax - 2)) {
			panelRow = ((y - Constants.SCREEN_HEIGHT / 4) / (Constants.SCREEN_HEIGHT / 2));
			if (panelRow > Variable.maxPanelRows) {
				panelRow = Variable.maxPanelRows;
			} else if (panelRow < 0) {
				panelRow = 0;
			}
		}
		
		if (force || (x < Variable.panelColMin + 3) || (x > Variable.panelColMax - 3)) {
			panelCol = ((x - Constants.SCREEN_WIDTH / 4) / (Constants.SCREEN_WIDTH / 2));
			if (panelCol > Variable.maxPanelCols) {
				panelCol = Variable.maxPanelCols;
			} else if (panelCol < 0) {
				panelCol = 0;
			}
		}
		
		if ((panelRow != Variable.panelRow) || (panelCol != Variable.panelCol)) {
			Variable.panelRow = panelRow;
			Variable.panelCol = panelCol;
			calculatePanelBounds();
			
			// stop movement if any
			if (Variable.findBound.value()) {
				Moria2.endFind();
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Tests a given point to see if it is within the screen boundaries -RAK-
	 * 
	 * @param y The vertical position of the point to check
	 * @param x The horizontal position of the point to check
	 * @return Whether the point is within the screen
	 */
	public static boolean panelContains(int y, int x) {
		return (y >= Variable.panelRowMin)
				&& (y <= Variable.panelRowMax)
				&& (x >= Variable.panelColMin)
				&& (x <= Variable.panelColMax);
	}
	
	/**
	 * Distance between two points -RAK-
	 * 
	 * @param y1 The vertical position of the starting point
	 * @param x1 The horizontal position of the starting point
	 * @param y2 The vertical position of the ending point
	 * @param x2 The horizontal position of the ending point
	 * @return The distance between the two points
	 */
	public static int distance(int y1, int x1, int y2, int x2) {
		int dy = y1 - y2;
		if (dy < 0) {
			dy = -dy;
		}
		
		int dx = x1 - x2;
		if (dx < 0) {
			dx = -dx;
		}
		
		return (((dy + dx) << 1) - ((dy > dx) ? dx : dy)) >> 1;
	}
	
	/**
	 * Checks points north, south, east, and west for a wall -RAK-
	 * <p>
	 * Note that (y, x) is always in_bounds(),
	 * i.e. (0 < y < cur_height - 1), and (0 < x < cur_width - 1)
	 * 
	 * @param y The vertical position of the point to check
	 * @param x The horizontal position of the point to check
	 * @return The number of walls adjacent to the point
	 */
	public static int numAdjacentWalls(int y, int x) {
		int numWalls = 0;
		
		CaveType cavePos = Variable.cave[y - 1][x];
		if (cavePos.fval >= Constants.MIN_CAVE_WALL) {
			numWalls++;
		}
		
		cavePos = Variable.cave[y + 1][x];
		if (cavePos.fval >= Constants.MIN_CAVE_WALL) {
			numWalls++;
		}
		
		cavePos = Variable.cave[y][x - 1];
		if (cavePos.fval >= Constants.MIN_CAVE_WALL) {
			numWalls++;
		}
		
		cavePos = Variable.cave[y][x + 1];
		if (cavePos.fval >= Constants.MIN_CAVE_WALL) {
			numWalls++;
		}
		
		return numWalls;
	}
	
	/**
	 * Checks all adjacent spots for corridors -RAK-
	 * <p>
	 * Note that (y, x) is always in_bounds(), hence no need to check that
	 * (j, k) is in_bounds(); even if they are 0 or cur_x - 1, it still works
	 * 
	 * @param y The vertical position of the point to check
	 * @param x The horizontal position of the point to check
	 * @return The number of corridors adjacent to the point
	 */
	public static int numAdjacentCorridors(int y, int x) {
		int numCorridors = 0;
		for (int j = y - 1; j <= (y + 1); j++) {
			for (int k = x - 1; k <= (x + 1); k++) {
				CaveType cavePos = Variable.cave[j][k];
				int treasureCategory = Treasure.treasureList[cavePos.treasureIndex].category;
				
				// should fail if there is already a door present
				if (cavePos.fval == Constants.CORR_FLOOR
						&& (cavePos.treasureIndex == 0 || treasureCategory < Constants.TV_MIN_DOORS)) {
					numCorridors++;
				}
			}
		}
		return numCorridors;
	}
	
	/**
	 * Generates damage for 2d6 style dice rolls
	 *
	 * @param num Number of times to roll the die
	 * @param sides Number of sides on the die
	 * @return The total damage counted
	 */
	public static int damageRoll(int num, int sides) {
		int sum = 0;
		for (int i = 0; i < num; i++) {
			sum += Rnd.randomInt(sides);
		}
		return sum;
	}
	
	public static int pDamageRoll(int[] array) {
		return damageRoll(array[0], array[1]);
	}
	
	/**
	 * A simple, fast, integer-based line-of-sight algorithm.  By Joseph Hall,
	 * 4116 Brewster Drive, Raleigh NC 27606.  Email to jnh@ecemwl.ncsu.edu.
	 * <p>
	 * Returns TRUE if a line of sight can be traced from x0, y0 to x1, y1.
	 * <p>
	 * The LOS begins at the center of the tile [x0, y0] and ends at
	 * the center of the tile [x1, y1].  If los() is to return TRUE, all of
	 * the tiles this line passes through must be transparent, WITH THE
	 * EXCEPTIONS of the starting and ending tiles.
	 * <p>
	 * We don't consider the line to be "passing through" a tile if
	 * it only passes across one corner of that tile.
	 * 
	 * @param fromY The vertical position of the starting point
	 * @param fromX The horizontal position of the starting point
	 * @param toY The vertical position of the ending point
	 * @param toX The horizontal position of the ending point
	 * @return Whether there is a clear line of sight from the starting point to the ending point
	 */
	public static boolean isInLineOfSight(int fromY, int fromX, int toY, int toX) {
		int deltaX = toX - fromX;
		int deltaY = toY - fromY;
		
		// Adjacent?
		if ((deltaX < 2) && (deltaX > -2) && (deltaY < 2) && (deltaY > -2)) {
			return true;
		}
		
		// Handle the cases where deltaX or deltaY == 0.
		if (deltaX == 0) {
			if (deltaY < 0) {
				int tmp = fromY;
				fromY = toY;
				toY = tmp;
			}
			for (int yPos = fromY + 1; yPos < toY; yPos++) {
				if (Variable.cave[yPos][fromX].fval >= Constants.MIN_CLOSED_SPACE) {
					return false;
				}
			}
			return true;
		} else if (deltaY == 0) {
			
			if (deltaX < 0) {
				int tmp = fromX;
				fromX = toX;
				toX = tmp;
			}
			for (int xPos = fromX + 1; xPos < toX; xPos++) {
				if (Variable.cave[fromY][xPos].fval >= Constants.MIN_CLOSED_SPACE) {
					return false;
				}
			}
			return true;
		}
		
		// Now, we've eliminated all the degenerate cases.
		// In the computations below, dy (or dx) and m are multiplied by a
		// scale factor, scale = abs(deltaX * deltaY * 2), so that we can use
		// integer arithmetic.	
		
		int scale2 = Math.abs(deltaX * deltaY); // above scale factor / 2
		int scale = scale2 << 1; // above scale factor
		int xSign = (deltaX < 0) ? -1 : 1; // sign of deltaX
		int ySign = (deltaY < 0) ? -1 : 1; // sign of deltaY
		
		// Travel from one end of the line to the other, oriented along
		// the longer axis.
		
		if (Math.abs(deltaX) >= Math.abs(deltaY)) {
			// We start at the border between the first and second tiles,
			// where the y offset = .5 * slope.  Remember the scale
			// factor.  We have:
			// 
			// m = deltaY / deltaX * 2 * (deltaY * deltaX)
			//   = 2 * deltaY * deltaY.
			
			int dy = deltaY * deltaY; // "fractional" y position
			int m = dy << 1; // slope or 1/slope of LOS
			int xPos = fromX + xSign;
			
			// Consider the special case where slope == 1.
			int yPos;
			if (dy == scale2) {
				yPos = fromY + ySign;
				dy -= scale;
			} else {
				yPos = fromY;
			}
			
			while (toX - xPos != 0) {
				if (Variable.cave[yPos][xPos].fval >= Constants.MIN_CLOSED_SPACE) {
					return false;
				}
				
				dy += m;
				if (dy < scale2) {
					xPos += xSign;
				} else if (dy > scale2) {
					yPos += ySign;
					if (Variable.cave[yPos][xPos].fval >= Constants.MIN_CLOSED_SPACE) {
						return false;
					}
					xPos += xSign;
					dy -= scale;
				} else {
					// This is the case, dy == scale2, where the LOS
					// exactly meets the corner of a tile.
					xPos += xSign;
					yPos += ySign;
					dy -= scale;
				}
			}
			return true;
		} else {
			int dx = deltaX * deltaX; // "fractional" x position
			int m = dx << 1;
			
			int yPos = fromY + ySign;
			int xPos;
			if (dx == scale2) {
				xPos = fromX + xSign;
				dx -= scale;
			} else {
				xPos = fromX;
			}
			
			while (toY - yPos != 0) {
				if (Variable.cave[yPos][xPos].fval >= Constants.MIN_CLOSED_SPACE) {
					return false;
				}
				dx += m;
				if (dx < scale2) {
					yPos += ySign;
				} else if (dx > scale2) {
					xPos += xSign;
					if (Variable.cave[yPos][xPos].fval >= Constants.MIN_CLOSED_SPACE) {
						return false;
					}
					yPos += ySign;
					dx -= scale;
				} else {
					xPos += xSign;
					yPos += ySign;
					dx -= scale;
				}
			}
			return true;
		}
	}
	
	/**
	 * Returns symbol for given row, column -RAK-
	 * 
	 * @param y The vertical position of the point to check
	 * @param x The horizontal position of the point to check
	 * @return The symbol at the given point
	 */
	public static char locateSymbol(int y, int x) {
		CaveType cavePos = Variable.cave[y][x];
		PlayerFlags playerFlags = Player.py.flags;
		
		if ((cavePos.creatureIndex == 1)
				&& (Variable.findFlag == 0 || !Variable.findPrself.value())) {
			return '@';
		} else if ((playerFlags.status & Constants.PY_BLIND) != 0) {
			return ' ';
		} else if ((playerFlags.imagine > 0)
				&& (Rnd.randomInt (12) == 1)) {
			return (char)(Rnd.randomInt(95) + 31);
		} else if ((cavePos.creatureIndex > 1)
				&& (Monsters.monsterList[cavePos.creatureIndex].monsterLight)) {
			return Monsters.creatureList[Monsters.monsterList[cavePos.creatureIndex].index].cchar;
		} else if (!cavePos.permLight && !cavePos.tempLight && !cavePos.fieldMark) {
			return ' ';
		} else if ((cavePos.treasureIndex != 0)
				&& (Treasure.treasureList[cavePos.treasureIndex].category != Constants.TV_INVIS_TRAP)) {
			return Treasure.treasureList[cavePos.treasureIndex].tchar;
		} else if (cavePos.fval <= Constants.MAX_CAVE_FLOOR) {
				return Variable.floorSymbol;
		} else if (cavePos.fval == Constants.GRANITE_WALL
				|| cavePos.fval == Constants.BOUNDARY_WALL
				|| !Variable.highlightSeams.value()) {
			return Variable.wallSymbol;
			//	return (char)240;
		} else {
			// Originally set highlight bit, but that is not portable, now use
			// the percent sign instead.
			return '%';
		}
	}
	
	/**
	 * Tests a spot for light or field mark status -RAK-
	 * 
	 * @param y The vertical position of the point to check
	 * @param x The horizonal position of the point to check
	 * @return Whether the point is lit or field-marked
	 */
	public static boolean testLight(int y, int x) {
		CaveType cavePos = Variable.cave[y][x];
		return cavePos.permLight  || cavePos.tempLight  || cavePos.fieldMark;
	}
	
	/**
	 * Prints the map of the dungeon -RAK-
	 */
	public static void printMap() {
		int lineNumber = 0;
		for (int y = Variable.panelRowMin; y <= Variable.panelRowMax; y++) { // Top to bottom
			lineNumber++;
			IO.eraseLine(lineNumber, 13);
			for (int x = Variable.panelColMin; x <= Variable.panelColMax; x++) { // Left to right
				char symbol = locateSymbol(y, x);
				if (symbol != ' ') {
					IO.print(symbol, y, x);
				}
			}
		}
	}
	
	/**
	 * Compact monsters -RAK-
	 * 
	 * @return True if any monsters were deleted, false if could not delete any monsters.
	 */
	public static boolean compactMonsters() {
		IO.printMessage("Compacting monsters...");
		
		int currDistance = 66;
		boolean deleteAny = false;
		do {
			for (int i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
				MonsterType monster = Monsters.monsterList[i];
				if ((currDistance < monster.currDistance) && (Rnd.randomInt(3) == 1)) {
					// Never compact away the Balrog!!
					if ((Monsters.creatureList[monster.index].cmove & Constants.CM_WIN) == 0) {
						// in case this is called from within creatures(), this is a
						// horrible hack, the m_list/creatures() code needs to be
						// rewritten TODO
						if (Variable.hackMonsterIndex < i) {
							Moria3.deleteMonster(i);
							deleteAny = true;
						} else {
							// fix1_delete_monster() does not decrement mfptr, so
							// don't set deleteAny if this was called
							Moria3.deleteMonster1(i);
						}
					}
				}
			}
			if (!deleteAny) {
				currDistance -= 6;
				// Can't delete any monsters, return failure.
				if (currDistance < 0) {
					return false;
				}
			}
		} while (!deleteAny);
		return true;
	}
	
	/**
	 * Add to the player's food time -RAK-
	 * 
	 * @param num Amount of food time to add
	 */
	public static void addFood(int num) {
		PlayerFlags playerFlags = Player.py.flags;
		if (playerFlags.food < 0) {
			playerFlags.food = 0;
		}
		
		playerFlags.food += num;
		if (playerFlags.food > Constants.PLAYER_FOOD_MAX) {
			IO.printMessage("You are bloated from overeating.");
			
			// Calculate how much of num is responsible for the bloating.
			// Give the player food credit for 1/50, and slow him for that many
			// turns also.
			int extra = playerFlags.food - Constants.PLAYER_FOOD_MAX;
			if (extra > num) {
				extra = num;
			}
			int penalty = extra / 50;
			
			playerFlags.slow += penalty;
			if (extra == num) {
				playerFlags.food = playerFlags.food - num + penalty;
			} else {
				playerFlags.food = Constants.PLAYER_FOOD_MAX + penalty;
			}
		} else if (playerFlags.food > Constants.PLAYER_FOOD_FULL) {
			IO.printMessage("You are full.");
		}
	}
	
	/**
	 * Returns a pointer to next free space -RAK-
	 * 
	 * @return The index of the first free space in the monster array,
	 *         or -1 if could not allocate a monster.
	 */
	public static int popMonster() {
		if (Monsters.freeMonsterIndex == Constants.MAX_MALLOC) {
			if (!compactMonsters()) {
				return -1;
			}
		}
		return Monsters.freeMonsterIndex++;
	}
	
	/**
	 * Gives Max hit points -RAK-
	 * 
	 * @param hitDie
	 * @return Number of hitpoints
	 */
	public static int getMaxHitpoints(int[] hitDie) {
		return hitDie[0] * hitDie[1];
	}
	
	/**
	 * Places a monster at given location -RAK-
	 * 
	 * @param y The vertical position at which to place the monster
	 * @param x The horizontal position at which to place the monster
	 * @param monsterIndex mptr of the monster to allocate
	 * @param putToSleep Whether to make the monster sleep
	 * @return True if the monster was placed, otherwise false
	 */
	public static boolean placeMonster(int y, int x, int monsterIndex, boolean putToSleep) {
		int curPos = popMonster();
		if (curPos == -1) {
			return false;
		}
		
		MonsterType monster = Monsters.monsterList[curPos];
		monster.y = y;
		monster.x = x;
		monster.index = monsterIndex;
		
		CreatureType creature = Monsters.creatureList[monsterIndex];
		if ((creature.cdefense & Constants.CD_MAX_HP) != 0) {
			monster.hitpoints = getMaxHitpoints(creature.hitDie);
		} else {
			monster.hitpoints = pDamageRoll(creature.hitDie);
		}
		
		monster.speed = creature.speed - 10 + Player.py.flags.speed;
		monster.stunned = 0;
		monster.currDistance = distance(Player.y, Player.x, y, x);
		monster.monsterLight = false;
		Variable.cave[y][x].creatureIndex = curPos;
		
		if (putToSleep) {
			if (creature.sleep == 0) {
				monster.sleep = 0;
			} else {
				monster.sleep = (creature.sleep * 2)
						+ Rnd.randomInt(creature.sleep * 10);
			}
		} else {
			monster.sleep = 0;
		}
		
		return true;
	}
	
	/**
	 * Places a win monster on the map -RAK-
	 */
	public static void placeWinMonster() {
		if (Variable.isTotalWinner) {
			return;
		}
		
		int curPos = popMonster();
		// Check for case where could not allocate space for the win monster,
		// this should never happen.
		if (curPos == -1) {
			//abort();
			return;
		}
		
		MonsterType monster = Monsters.monsterList[curPos];
		CaveType cavePos;
		int y;
		int x;
		do {
			y = Rnd.randomInt(Variable.currHeight - 2);
			x = Rnd.randomInt(Variable.currWidth - 2);
			cavePos = Variable.cave[y][x];
		} while ((cavePos.fval >= Constants.MIN_CLOSED_SPACE)
				|| (cavePos.creatureIndex != 0)
				|| (cavePos.treasureIndex != 0)
				|| (distance(y, x, Player.y, Player.x) <= Constants.MAX_SIGHT));
		
		monster.y = y;
		monster.x = x;
		monster.index = Rnd.randomInt(Constants.WIN_MON_TOT) - 1
				+ Monsters.monsterLevel[Constants.MAX_MONS_LEVEL];
		
		CreatureType creature = Monsters.creatureList[monster.index];
		if ((creature.cdefense & Constants.CD_MAX_HP) != 0) {
			monster.hitpoints = getMaxHitpoints(creature.hitDie);
		} else {
			monster.hitpoints = pDamageRoll(creature.hitDie);
		}
		
		monster.speed = creature.speed - 10 + Player.py.flags.speed;
		monster.stunned = 0;
		monster.currDistance = distance(Player.y, Player.x, y, x);
		monster.sleep = 0;
		cavePos.creatureIndex = curPos;
	}
	
	/**
	 * Return a monster suitable to be placed at a given level. This makes
	 * high level monsters (up to the given level) slightly more common than
	 * low level monsters at any given level. -CJS-
	 *
	 * @param level Level of the monster to place
	 * @return mptr of the monster to place
	 */
	public static int getRandomMonsterForLevel(int level) {
		if (level == 0) {
			return Rnd.randomInt(Monsters.monsterLevel[0]) - 1;
		}
		
		int monsterIndex;
		if (level > Constants.MAX_MONS_LEVEL) {
			level = Constants.MAX_MONS_LEVEL;
		}
		if (Rnd.randomInt(Constants.MON_NASTY) == 1) {
			monsterIndex = Rnd.randomIntNormalized (0, 4);
			level += Math.abs(monsterIndex) + 1;
			if (level > Constants.MAX_MONS_LEVEL) {
				level = Constants.MAX_MONS_LEVEL;
			}
		} else {
			// This code has been added to make it slightly more likely to
			// get the higher level monsters. Originally a uniform
			// distribution over all monsters of level less than or equal to the
			// dungeon level. This distribution makes a level n monster occur
			// approx 2/n% of the time on level n, and 1/n*n% are 1st level.
			
			int levelDiff = Monsters.monsterLevel[level] - Monsters.monsterLevel[0];
			monsterIndex = Rnd.randomInt(levelDiff) - 1;
			int index2 = Rnd.randomInt(levelDiff) - 1;
			if (index2 > monsterIndex) {
				monsterIndex = index2;
			}
			level = Monsters.creatureList[monsterIndex + Monsters.monsterLevel[0]].level;
		}
		
		monsterIndex = Rnd.randomInt(Monsters.monsterLevel[level] - Monsters.monsterLevel[level - 1])
				- 1 + Monsters.monsterLevel[level - 1];
		
		return monsterIndex;
	}
	
	/**
	 * Allocates a random monster -RAK-
	 * 
	 * @param numToPlace Number of monsters to place
	 * @param distance Minimum distance from the player to place monsters
	 * @param putToSleep Whether to make the monsters sleep
	 */
	public static void spawnMonster(int numToPlace, int distance, boolean putToSleep) {
		int y, x;
		
		for (int i = 0; i < numToPlace; i++) {
			do {
				y = Rnd.randomInt(Variable.currHeight - 2);
				x = Rnd.randomInt(Variable.currWidth - 2);
			} while (Variable.cave[y][x].fval >= Constants.MIN_CLOSED_SPACE
					|| (Variable.cave[y][x].creatureIndex != 0)
					|| (distance(y, x, Player.y, Player.x) <= distance));
			
			int monsterIndex = getRandomMonsterForLevel(Variable.dungeonLevel);
			
			// Dragons are always created sleeping here, so as to give the player a
			// sporting chance.
			if (Monsters.creatureList[monsterIndex].cchar == 'd'
					|| Monsters.creatureList[monsterIndex].cchar == 'D') {
				putToSleep = true;
			}
			
			// placeMonster() should always return TRUE here.  It does not
			// matter if it fails though.
			placeMonster(y, x, monsterIndex, putToSleep);
		}
	}
	
	/**
	 * Places creature adjacent to given location -RAK-
	 * 
	 * @param y The vertical position of the summoner,
	 *          stores the vertical position of the summoned monster
	 * @param x The horizontal position of the summoner,
	 *          stores the horizontal position of the summoned monster
	 * @param putToSleep Whether to make the monster sleep
	 * @return True if a monster was successfully summoned, otherwise false
	 */
	public static boolean summonMonster(IntPointer y, IntPointer x, boolean putToSleep) {
		int monsterIndex = getRandomMonsterForLevel(Variable.dungeonLevel + Constants.MON_SUMMON_ADJ);
		
		for (int i = 0; i < 10; i++) {
			int newY = y.value() - 2 + Rnd.randomInt(3);
			int newX = x.value() - 2 + Rnd.randomInt(3);
			
			if (isInBounds(newY, newX)) {
				CaveType cavePos = Variable.cave[newY][newX];
				if (cavePos.fval <= Constants.MAX_OPEN_SPACE && (cavePos.creatureIndex == 0)) {
					// placeMonster() should always return TRUE here.
					if (!placeMonster(newY, newX, monsterIndex, putToSleep)) {
						return false;
					}
					
					y.value(newY);
					x.value(newX);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Places undead adjacent to given location -RAK-
	 * 
	 * @param y The vertical position of the summoner,
	 *          stores the vertical position of the summoned undead
	 * @param x The horizontal position of the summoner,
	 *          stores the horizontal position of the summoned undead
	 * @return True if an undead was successfully summoned, otherwise false
	 */
	public static boolean summonUndead(IntPointer y, IntPointer x) {
		int monsterIndex;
		int monsterLevel = Monsters.monsterLevel[Constants.MAX_MONS_LEVEL];
		
		do {
			monsterIndex = Rnd.randomInt(monsterLevel) - 1;
			for (int i = 0; i < 20; i++) {
				if ((Monsters.creatureList[monsterIndex].cdefense & Constants.CD_UNDEAD) != 0) {
					monsterLevel = 0;
					break;
				} else {
					monsterIndex++;
					if (monsterIndex > monsterLevel) {
						break;
					}
				}
			}
		} while (monsterLevel != 0);
		
		for (int i = 0; i < 10; i++) {
			int newY = y.value() - 2 + Rnd.randomInt(3);
			int newX = x.value() - 2 + Rnd.randomInt(3);
			
			if (isInBounds(newY, newX) ) {
				CaveType cavePos = Variable.cave[newY][newX];
				if (cavePos.fval <= Constants.MAX_OPEN_SPACE && (cavePos.creatureIndex == 0)) {
					// placeMonster() should always return TRUE here.
					if (!placeMonster(newY, newX, monsterIndex, false)) {
						return false;
					}
					
					y.value(newY);
					x.value(newX);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * If too many objects on floor level, delete some of them -RAK-
	 */
	public static void compactObjects() {
		IO.printMessage("Compacting objects...");
		
		boolean deletedObject = false;
		int currDistance = 66;
		do {
			for (int y = 0; y < Variable.currHeight; y++) {
				for (int x = 0; x < Variable.currWidth; x++) {
					CaveType cavePos = Variable.cave[y][x];
					if ((cavePos.treasureIndex != 0)
							&& (distance(y, x, Player.y, Player.x) > currDistance)) {
						int chance = 0;
						
						switch (Treasure.treasureList[cavePos.treasureIndex].category) {
						case Constants.TV_VIS_TRAP:
							chance = 15;
							break;
						case Constants.TV_INVIS_TRAP:
						case Constants.TV_RUBBLE:
						case Constants.TV_OPEN_DOOR: case Constants.TV_CLOSED_DOOR:
							chance = 5;
							break;
						case Constants.TV_UP_STAIR:
						case Constants.TV_DOWN_STAIR:
						case Constants.TV_STORE_DOOR:
							// stairs, don't delete them
							// shop doors, don't delete them
							chance = 0;
							break;
						case Constants.TV_SECRET_DOOR: // secret doors
							chance = 3;
							break;
						default:
							chance = 10;
						}
						
						if (Rnd.randomInt(100) <= chance) {
							Moria3.deleteObject(y, x);
							deletedObject = true;
						}
					}
				}
			}
			if (!deletedObject) {
				currDistance -= 6;
			}
		} while (!deletedObject && currDistance > 0);
		
		if (currDistance < 66) {
			printMap();
		}
	}
	
	/**
	 * Gives pointer to next free space -RAK-
	 * 
	 * @return The index of the first free space in the treasure array
	 */
	public static int popTreasure() {
		if (Treasure.currTreasureIndex == Constants.MAX_TALLOC) {
			compactObjects();
		}
		return Treasure.currTreasureIndex++;
	}
	
	/**
	 * Pushes a record back onto free space list -RAK-
	 * <p>
	 * deleteObject() should always be called instead, unless the object in
	 * question is not in the dungeon, e.g. in Store1.java and Files.java
	 * 
	 * @param treasureIndex Location in the treasure array of the item to move
	 */
	public static void pusht(int treasureIndex) {
		if (treasureIndex != Treasure.currTreasureIndex - 1) {
			Treasure.treasureList[Treasure.currTreasureIndex - 1]
					.copyInto(Treasure.treasureList[treasureIndex]);
			
			// must change the tptr in the cave of the object just moved
			for (int y = 0; y < Variable.currHeight; y++) {
				for (int x = 0; x < Variable.currWidth; x++) {
					if (Variable.cave[y][x].treasureIndex == Treasure.currTreasureIndex - 1) {
						Variable.cave[y][x].treasureIndex = treasureIndex;
					}
				}
			}
		}
		Treasure.currTreasureIndex--;
		Desc.copyIntoInventory(
				Treasure.treasureList[Treasure.currTreasureIndex],
				Constants.OBJ_NOTHING);
	}
	
	/**
	 * Is object enchanted? -RAK-
	 * 
	 * @param chance Percent chance that the object is enchanted
	 * @return Whether the object is enchanted
	 */
	public static boolean isMagik(int chance) {
		return Rnd.randomInt(100) <= chance;
	}
	
	/**
	 * Enchant a bonus based on degree desired -RAK-
	 * 
	 * @param base Minimum bonus to award
	 * @param maxStdDeviation Maximum standard deviation
	 * @param level Dungeon level at which object was found
	 * @return The bonus to award
	 */
	public static int magicBonus(int base, int maxStdDeviation, int level) {
		int stdDeviation = (Constants.OBJ_STD_ADJ * level / 100) + Constants.OBJ_STD_MIN;
		// Check for level > max_std since that may have generated an overflow.
		if (stdDeviation > maxStdDeviation || level > maxStdDeviation) {
			stdDeviation = maxStdDeviation;
		}
		
		int bonus = (Math.abs(Rnd.randomIntNormalized(0, stdDeviation)) / 10) + base;
		if (bonus < base) {
			return base;
		} else {
			return bonus;
		}
	}
}
