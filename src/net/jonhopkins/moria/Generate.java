/*
 * Generate.java: initialize/create a dungeon or town level
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

import java.awt.Point;

import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.IntPointer;

public class Generate {
	
	private Generate() { }
	
	private static Point[] doorStack = new Point[100];
	private static int doorIndex;
	
	static {
		for (int i = 0; i < 100; i++) {
			doorStack[i] = new Point();
		}
	}
	
	/**
	 * Always picks a correct direction.
	 * 
	 * @param vertDir which direction to go up or down
	 * @param horizDir which direction to go left or right
	 * @param origY starting y position
	 * @param origX starting x position
	 * @param newY y position to move to
	 * @param newX x position to move to
	 */
	private static void getCorrectDirection(IntPointer vertDir, IntPointer horizDir,
			int origY, int origX, int newY, int newX) {
		if (origY < newY) {
			vertDir.value(1);
		} else if (origY == newY) {
			vertDir.value(0);
		} else {
			vertDir.value(-1);
		}
		if (origX < newX) {
			horizDir.value(1);
		} else if (origX == newX) {
			horizDir.value(0);
		} else {
			horizDir.value(-1);
		}
		if ((vertDir.value() != 0) && (horizDir.value() != 0)) {
			if (Rnd.randomInt(2) == 1) {
				vertDir.value(0);
			} else {
				horizDir.value(0);
			}
		}
	}
	
	/**
	 * Chance of wandering direction.
	 * 
	 * @param vertDir which direction to go up or down
	 * @param horizDir which direction to go left or right
	 */
	private static void getRandomDirection(IntPointer vertDir, IntPointer horizDir) {
		int dir = Rnd.randomInt(4);
		if (dir < 3) {
			horizDir.value(0);
			// tmp=1 -> *rdir=-1; tmp=2 -> *rdir=1
			vertDir.value(-3 + (dir << 1));
		} else {
			vertDir.value(0);
			// tmp=3 -> *cdir=-1; tmp=4 -> *cdir=1
			horizDir.value(-7 + (dir << 1));
		}
	}
	
	/**
	 * Blanks out entire cave. -RAK-
	 */
	private static void clearCave() {
		for (int i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (int j = 0; j < Constants.MAX_WIDTH; j++) {
				Variable.cave[i][j].blankAll();
			}
		}
	}
	
	/**
	 * Fills in empty spots with desired rock. -RAK-
	 * 
	 * @param wallType
	 */
	// Note: 9 is a temporary value.
	private static void fillCave(int wallType) {
		// no need to check the border of the cave
		for (int i = Variable.currHeight - 2; i > 0; i--) {
			int ptrCount = 1;
			CaveType cavePos = Variable.cave[i][ptrCount];
			for (int j = Variable.currWidth - 2; j > 0; j--) {
				if ((cavePos.fval == Constants.NULL_WALL)
						|| (cavePos.fval == Constants.TMP1_WALL)
						|| (cavePos.fval == Constants.TMP2_WALL)) {
					cavePos.fval = wallType;
				}
				ptrCount++;
				cavePos = Variable.cave[i][ptrCount];
			}
		}
	}
	
	/**
	 * Places indestructible rock around edges of dungeon. -RAK-
	 */
	private static void placeBoundary() {
		final int left = Variable.currWidth - 1;
		final int bottom = Variable.currHeight - 1;
		
		// put permanent wall on leftmost row and rightmost row
		for (int i = 0; i < Variable.currHeight; i++) {
			Variable.cave[i][0].fval = Constants.BOUNDARY_WALL;
			Variable.cave[i][left].fval = Constants.BOUNDARY_WALL;
		}
		
		// put permanent wall on top row and bottom row
		for (int i = 0; i < Variable.currWidth; i++) {
			Variable.cave[0][i].fval = Constants.BOUNDARY_WALL;
			Variable.cave[bottom][i].fval = Constants.BOUNDARY_WALL;
		}
	}
	
	/**
	 * Places "streamers" of rock through dungeon. -RAK-
	 * 
	 * @param wallType
	 * @param treasureChance
	 */
	private static void placeStreamer(int wallType, int treasureChance) {
		// Choose starting point and direction
		IntPointer y = new IntPointer((Variable.currHeight / 2)
				+ 11 - Rnd.randomInt(23));
		IntPointer x = new IntPointer((Variable.currWidth / 2)
				+ 16 - Rnd.randomInt(33));
		
		int dir = Rnd.randomInt(8);	// Number 1-4, 6-9
		if (dir > 4) {
			++dir;
		}
		
		// Place streamer into dungeon
		int t1 = 2 * Constants.DUN_STR_RNG + 1;
		int t2 = Constants.DUN_STR_RNG + 1;
		do {
			for (int i = 0; i < Constants.DUN_STR_DEN; i++) {
				int ty = y.value() + Rnd.randomInt(t1) - t2;
				int tx = x.value() + Rnd.randomInt(t1) - t2;
				if (Misc1.isInBounds(ty, tx)) {
					CaveType cavePos = Variable.cave[ty][tx];
					if (cavePos.fval == Constants.GRANITE_WALL) {
						cavePos.fval = wallType;
						if (Rnd.randomInt(treasureChance) == 1) {
							Misc3.placeGold(ty, tx);
						}
					}
				}
			}
		} while (Misc3.canMoveDirection(dir, y, x));
	}
	
	private static void placeOpenDoor(int y, int x) {
		int treasureIndex = Misc1.popTreasure();
		CaveType cavePos = Variable.cave[y][x];
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_OPEN_DOOR);
		cavePos.fval = Constants.CORR_FLOOR;
	}
	
	private static void placeBrokenDoor(int y, int x) {
		int treasureIndex = Misc1.popTreasure();
		CaveType cavePos = Variable.cave[y][x];
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_OPEN_DOOR);
		cavePos.fval = Constants.CORR_FLOOR;
		Treasure.treasureList[treasureIndex].misc = 1;
	}
	
	private static void placeClosedDoor(int y, int x) {
		int treasureIndex = Misc1.popTreasure();
		CaveType cavePos = Variable.cave[y][x];
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_CLOSED_DOOR);
		cavePos.fval = Constants.BLOCKED_FLOOR;
	}
	
	private static void placeLockedDoor(int y, int x) {
		int treasureIndex = Misc1.popTreasure();
		CaveType cavePos = Variable.cave[y][x];
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_CLOSED_DOOR);
		cavePos.fval = Constants.BLOCKED_FLOOR;
		Treasure.treasureList[treasureIndex].misc = Rnd.randomInt(10) + 10;
	}
	
	private static void placeStuckDoor(int y, int x) {
		int treasureIndex = Misc1.popTreasure();
		CaveType cavePos = Variable.cave[y][x];
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_CLOSED_DOOR);
		cavePos.fval = Constants.BLOCKED_FLOOR;
		Treasure.treasureList[treasureIndex].misc = -Rnd.randomInt(10) - 10;
	}
	
	private static void placeSecretDoor(int y, int x) {
		int treasureIndex = Misc1.popTreasure();
		CaveType cavePos = Variable.cave[y][x];
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_SECRET_DOOR);
		cavePos.fval = Constants.BLOCKED_FLOOR;
	}
	
	private static void placeDoor(int y, int x) {
		int tmp = Rnd.randomInt(3);
		if (tmp == 1) {
			if (Rnd.randomInt(4) == 1) {
				placeBrokenDoor(y, x);
			} else {
				placeOpenDoor(y, x);
			}
		} else if (tmp == 2) {
			tmp = Rnd.randomInt(12);
			if (tmp > 3) {
				placeClosedDoor(y, x);
			} else if (tmp == 3) {
				placeStuckDoor(y, x);
			} else {
				placeLockedDoor(y, x);
			}
		} else {
			placeSecretDoor(y, x);
		}
	}
	
	/**
	 * Place an up staircase at given y, x. -RAK-
	 * 
	 * @param y
	 * @param x
	 */
	private static void placeUpStairs(int y, int x) {
		CaveType cavePos = Variable.cave[y][x];
		if (cavePos.treasureIndex != 0) {
			Moria3.deleteObject(y, x);
		}
		
		int treasureIndex = Misc1.popTreasure();
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_UP_STAIR);
	}
	
	/**
	 * Place a down staircase at given y, x. -RAK-
	 * 
	 * @param y
	 * @param x
	 */
	private static void placeDownStairs(int y, int x) {
		CaveType cavePos = Variable.cave[y][x];
		if (cavePos.treasureIndex != 0) {
			Moria3.deleteObject(y, x);
		}
		
		int treasureIndex = Misc1.popTreasure();
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_DOWN_STAIR);
	}
	
	/**
	 * Places a staircase 1=up, 2=down. -RAK-
	 * 
	 * @param typ
	 * @param num
	 * @param walls
	 */
	private static void placeStairs(int typ, int num, int walls) {
		for (int i = 0; i < num; i++) {
			boolean flag = false;
			do {
				int j = 0;
				do {
					// Note: don't let y1/x1 be zero, and don't let y2/x2 be equal
					// to cur_height-1/cur_width-1, these values are always
					// BOUNDARY_ROCK.
					int y1 = Rnd.randomInt(Variable.currHeight - 14);
					int x1 = Rnd.randomInt(Variable.currWidth  - 14);
					int y2 = y1 + 12;
					int x2 = x1 + 12;
					do {
						do {
							CaveType cavePos = Variable.cave[y1][x1];
							if (cavePos.fval <= Constants.MAX_OPEN_SPACE
									&& (cavePos.treasureIndex == 0)
									&& (Misc1.numAdjacentWalls(y1, x1) >= walls)) {
								flag = true;
								if (typ == 1) {
									placeUpStairs(y1, x1);
								} else {
									placeDownStairs(y1, x1);
								}
							}
							x1++;
						} while ((x1 != x2) && (!flag));
						x1 = x2 - 12;
						y1++;
					} while ((y1 != y2) && (!flag));
					j++;
				} while ((!flag) && (j <= 30));
				walls--;
			} while (!flag);
	    }
	}
	
	/**
	 * Place a trap with a given displacement of point. -RAK-
	 * 
	 * @param y
	 * @param x
	 * @param yd
	 * @param xd
	 * @param num
	 */
	private static void vaultTrap(int y, int x, int yd, int xd, int num) {
		for (int i = 0; i < num; i++) {
			for (int tries = 0; tries <= 5; tries++) {
				int y1 = y - yd - 1 + Rnd.randomInt(2 * yd + 1);
				int x1 = x - xd - 1 + Rnd.randomInt(2 * xd + 1);
				CaveType c_ptr = Variable.cave[y1][x1];
				if ((c_ptr.fval != Constants.NULL_WALL)
						&& (c_ptr.fval <= Constants.MAX_CAVE_FLOOR)
						&& (c_ptr.treasureIndex == 0)) {
					Misc3.placeTrap(y1, x1, Rnd.randomInt(Constants.MAX_TRAP) - 1);
					break;
				}
			}
		}
	}
	
	/**
	 * Place a trap with a given displacement of point. -RAK-
	 * 
	 * @param y
	 * @param x
	 * @param num
	 */
	private static void vaultMonster(int y, int x, int num) {
		for (int i = 0; i < num; i++) {
			Misc1.summonMonster(new IntPointer(y), new IntPointer(x), true);
		}
	}
	
	/**
	 * Builds a room at a row, column coordinate. -RAK-
	 * 
	 * @param y
	 * @param x
	 */
	private static void buildRoom(int y, int x) {
		final int floorType;
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floorType = Constants.LIGHT_FLOOR; // Floor with light
		} else {
			floorType = Constants.DARK_FLOOR; // Dark floor
		}
		
		int top = y - Rnd.randomInt(4);
		int bottom = y + Rnd.randomInt(3);
		int left = x - Rnd.randomInt(11);
		int right = x + Rnd.randomInt(11);
		
		// the x dim of rooms tends to be much larger than the y dim, so don't
		// bother rewriting the y loop
		
		for (int i = top; i <= bottom; i++) {
			for (int j = left; j <= right; j++) {
				CaveType floor = Variable.cave[i][j];
				floor.fval = floorType;
				floor.litRoom = true;
			}
		}
		
		for (int i = (top - 1); i <= (bottom + 1); i++) {
			CaveType leftWall = Variable.cave[i][left - 1];
			leftWall.fval = Constants.GRANITE_WALL;
			leftWall.litRoom = true;
			
			CaveType rightWall = Variable.cave[i][right + 1];
			rightWall.fval = Constants.GRANITE_WALL;
			rightWall.litRoom = true;
		}
		
		for (int i = left; i <= right; i++) {
			CaveType topWall = Variable.cave[top - 1][i];
			topWall.fval = Constants.GRANITE_WALL;
			topWall.litRoom = true;
			
			CaveType bottomWall = Variable.cave[bottom + 1][i];
			bottomWall.fval = Constants.GRANITE_WALL;
			bottomWall.litRoom = true;
		}
	}
	
	/**
	 * Builds a room at a row, column coordinate. -RAK-
	 * <p>
	 * Type 1 unusual rooms are several overlapping rectangular ones
	 * 
	 * @param y
	 * @param x
	 */
	private static void buildType1(int y, int x) {
		final int floorType;
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floorType = Constants.LIGHT_FLOOR; // Floor with light
		} else {
			floorType = Constants.DARK_FLOOR; // Dark floor
		}
		
		final int limit = 1 + Rnd.randomInt(2);
		for (int roomNumber = 0; roomNumber < limit; roomNumber++) {
			final int top = y - Rnd.randomInt(4);
			final int bottom = y + Rnd.randomInt(3);
			final int left = x - Rnd.randomInt(11);
			final int right = x + Rnd.randomInt(11);
			
			// the x dim of rooms tends to be much larger than the y dim,
			// so don't bother rewriting the y loop
			
			for (int i = top; i <= bottom; i++) {
				for (int j = left; j <= right; j++) {
					CaveType floor = Variable.cave[i][j];
					floor.fval = floorType;
					floor.litRoom = true;
				}
			}
			
			for (int i = (top - 1); i <= (bottom + 1); i++) {
				CaveType leftWall = Variable.cave[i][left - 1];
				if (leftWall.fval != floorType) {
					leftWall.fval = Constants.GRANITE_WALL;
					leftWall.litRoom = true;
				}
				
				CaveType rightWall = Variable.cave[i][right + 1];
				if (rightWall.fval != floorType) {
					rightWall.fval = Constants.GRANITE_WALL;
					rightWall.litRoom = true;
				}
			}
			
			for (int i = left; i <= right; i++) {
				CaveType topWall = Variable.cave[top - 1][i];
				if (topWall.fval != floorType) {
					topWall.fval = Constants.GRANITE_WALL;
					topWall.litRoom = true;
				}
				
				CaveType bottomWall = Variable.cave[bottom + 1][i];
				if (bottomWall.fval != floorType) {
					bottomWall.fval = Constants.GRANITE_WALL;
					bottomWall.litRoom = true;
				}
			}
		}
	}
	
	/**
	 * Builds an unusual room at a row, column coordinate -RAK-
	 * <p>
	 * Type 2 unusual rooms all have an inner room:
	 * <ul>
	 *   <li>1 - Just an inner room with one door</li>
	 *   <li>2 - An inner room within an inner room</li>
	 *   <li>3 - An inner room with pillar(s)</li>
	 *   <li>4 - Inner room has a maze</li>
	 *   <li>5 - A set of four inner rooms</li>
	 * </ul>
	 * 
	 * @param y
	 * @param x
	 */
	private static void buildType2(int y, int x) {
		final int floorType;
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floorType = Constants.LIGHT_FLOOR; // Floor with light
		} else {
			floorType = Constants.DARK_FLOOR; // Dark floor
		}
		
		int top = y - 4;
		int bottom = y + 4;
		int left = x - 11;
		int right = x + 11;
		
		// the x dim of rooms tends to be much larger than the y dim, so don't
		// bother rewriting the y loop
		
		for (int i = top; i <= bottom; i++) {
			for (int j = left; j <= right; j++) {
				CaveType floor = Variable.cave[i][j];
				floor.fval = floorType;
				floor.litRoom = true;
			}
		}
		
		for (int i = (top - 1); i <= (bottom + 1); i++) {
			CaveType leftWall = Variable.cave[i][left - 1];
			leftWall.fval = Constants.GRANITE_WALL;
			leftWall.litRoom = true;
			
			CaveType rightWall = Variable.cave[i][right + 1];
			rightWall.fval = Constants.GRANITE_WALL;
			rightWall.litRoom = true;
		}
		
		for (int i = left; i <= right; i++) {
			CaveType topWall = Variable.cave[top - 1][i];
			topWall.fval = Constants.GRANITE_WALL;
			topWall.litRoom = true;
			
			CaveType bottomWall = Variable.cave[bottom + 1][i];
			bottomWall.fval = Constants.GRANITE_WALL;
			bottomWall.litRoom = true;
		}
		
		// The inner room
		top = top + 2;
		bottom = bottom - 2;
		left = left + 2;
		right = right - 2;
		
		for (int i = (top - 1); i <= (bottom + 1); i++) {
			Variable.cave[i][left - 1].fval = Constants.TMP1_WALL;
			Variable.cave[i][right + 1].fval = Constants.TMP1_WALL;
		}
		
		for (int i = left; i <= right; i++) {
			Variable.cave[top - 1][i].fval = Constants.TMP1_WALL;
			Variable.cave[bottom + 1][i].fval = Constants.TMP1_WALL;
		}
		
		// Inner room variations
		switch (Rnd.randomInt(5)) {
		case 1: // Just an inner room.
			switch (Rnd.randomInt(4)) { // Place a door
			case 1:
				placeSecretDoor(top - 1, x);
				break;
			case 2:
				placeSecretDoor(bottom + 1, x);
				break;
			case 3:
				placeSecretDoor(y, left - 1);
				break;
			case 4:
				placeSecretDoor(y, right + 1);
				break;
			}
			vaultMonster(y, x, 1);
			break;
			
		case 2: // Treasure Vault
			switch (Rnd.randomInt(4)) { // Place a door
			case 1:
				placeSecretDoor(top - 1, x);
				break;
			case 2:
				placeSecretDoor(bottom + 1, x);
				break;
			case 3:
				placeSecretDoor(y, left - 1);
				break;
			case 4:
				placeSecretDoor(y, right + 1);
				break;
			}
			
			for (int i = y - 1; i <= y + 1; i++) {
				Variable.cave[i][x - 1].fval = Constants.TMP1_WALL;
				Variable.cave[i][x + 1].fval = Constants.TMP1_WALL;
			}
			
			Variable.cave[y - 1][x].fval = Constants.TMP1_WALL;
			Variable.cave[y + 1][x].fval = Constants.TMP1_WALL;
			
			switch (Rnd.randomInt(4)) { // Place a door
			case 1:
				placeLockedDoor(y - 3 + (1 << 1), x);
				break;
			case 2:
				placeLockedDoor(y - 3 + (2 << 1), x);
				break;
			case 3:
				placeLockedDoor(y, x - 7 + (3 << 1));
				break;
			case 4:
				placeLockedDoor(y, x - 7 + (4 << 1));
				break;
			}
			
			// Place an object in the treasure vault
			switch (Rnd.randomInt(10)) {
			case 1:
				placeUpStairs(y, x);
				break;
			case 2:
				placeDownStairs(y, x);
				break;
			default:
				Misc3.placeObject(y, x, false);
				break;
			}
			
			// Guard the treasure well
			vaultMonster(y, x, 2 + Rnd.randomInt(3));
			
			// If the monsters don't get 'em.
			vaultTrap(y, x, 4, 10, 2 + Rnd.randomInt(3));
			break;
			
		case 3: // Inner pillar(s).
			switch (Rnd.randomInt(4)) { // Place a door
			case 1:
				placeSecretDoor(top - 1, x);
				break;
			case 2:
				placeSecretDoor(bottom + 1, x);
				break;
			case 3:
				placeSecretDoor(y, left - 1);
				break;
			case 4:
				placeSecretDoor(y, right + 1);
				break;
			}
			
			for (int i = y - 1; i <= y + 1; i++) {
				for (int j = x - 1; j <= x + 1; j++) {
					Variable.cave[i][j].fval = Constants.TMP1_WALL;
				}
			}
			if (Rnd.randomInt(2) == 1) {
				int tmp = Rnd.randomInt(2);
				for (int i = y - 1; i <= y + 1; i++) {
					for (int j = x - 5 - tmp; j <= x - 3 - tmp; j++) {
						Variable.cave[i][j].fval = Constants.TMP1_WALL;
					}
				}
				
				for (int i = y - 1; i <= y + 1; i++) {
					for (int j = x + 3 + tmp; j <= x + 5 + tmp; j++) {
						Variable.cave[i][j].fval = Constants.TMP1_WALL;
					}
				}
			}
			
			if (Rnd.randomInt(3) == 1) { // Inner rooms
				for (int i = x - 5; i <= x + 5; i++) {
					Variable.cave[y - 1][i].fval = Constants.TMP1_WALL;
					Variable.cave[y + 1][i].fval = Constants.TMP1_WALL;
				}
				
				Variable.cave[y][x - 5].fval = Constants.TMP1_WALL;
				Variable.cave[y][x + 5].fval = Constants.TMP1_WALL;
				placeSecretDoor(y - 3 + (Rnd.randomInt(2) << 1), x - 3);
				placeSecretDoor(y - 3 + (Rnd.randomInt(2) << 1), x + 3);
				
				if (Rnd.randomInt(3) == 1) {
					Misc3.placeObject(y, x - 2, false);
				}
				
				if (Rnd.randomInt(3) == 1) {
					Misc3.placeObject(y, x + 2, false);
				}
				
				vaultMonster(y, x - 2, Rnd.randomInt(2));
				vaultMonster(y, x + 2, Rnd.randomInt(2));
			}
			break;
			
		case 4: // Maze inside.
			switch (Rnd.randomInt(4)) { // Place a door
			case 1:
				placeSecretDoor(top - 1, x);
				break;
			case 2:
				placeSecretDoor(bottom + 1, x);
				break;
			case 3:
				placeSecretDoor(y, left - 1);
				break;
			case 4:
				placeSecretDoor(y, right + 1);
				break;
			}
			
			for (int i = top; i <= bottom; i++) {
				for (int j = left; j <= right; j++) {
					if ((0x1 & (j + i)) > 0) {
						Variable.cave[i][j].fval = Constants.TMP1_WALL;
					}
				}
			}
			
			// Monsters just love mazes.
			vaultMonster(y, x - 5, Rnd.randomInt(3));
			vaultMonster(y, x + 5, Rnd.randomInt(3));
			
			// Traps make them entertaining.
			vaultTrap(y, x - 3, 2, 8, Rnd.randomInt(3));
			vaultTrap(y, x + 3, 2, 8, Rnd.randomInt(3));
			
			// Mazes should have some treasure too..
			for (int i = 0; i < 3; i++) {
				Misc3.spawnRandomObject(y, x, 1);
			}
			break;
			
		case 5: // Four small rooms.
			for (int i = top; i <= bottom; i++) {
				Variable.cave[i][x].fval = Constants.TMP1_WALL;
			}
			
			for (int i = left; i <= right; i++) {
				Variable.cave[y][i].fval = Constants.TMP1_WALL;
			}
			
			if (Rnd.randomInt(2) == 1) {
				int offset = Rnd.randomInt(10);
				placeSecretDoor(top - 1, x - offset);
				placeSecretDoor(top - 1, x + offset);
				placeSecretDoor(bottom + 1, x - offset);
				placeSecretDoor(bottom + 1, x + offset);
			} else {
				int offset = Rnd.randomInt(3);
				placeSecretDoor(y + offset, left - 1);
				placeSecretDoor(y - offset, left - 1);
				placeSecretDoor(y + offset, right + 1);
				placeSecretDoor(y - offset, right + 1);
			}
			
			// Treasure in each one.
			Misc3.spawnRandomObject(y, x, 2 + Rnd.randomInt(2));
			
			// Gotta have some monsters.
			vaultMonster(y + 2, x - 4, Rnd.randomInt(2));
			vaultMonster(y + 2, x + 4, Rnd.randomInt(2));
			vaultMonster(y - 2, x - 4, Rnd.randomInt(2));
			vaultMonster(y - 2, x + 4, Rnd.randomInt(2));
			break;
		default:
			break;
		}
	}
	
	/**
	 * Builds a room at a row, column coordinate. -RAK-
	 * <p>
	 * Type 3 unusual rooms are cross shaped
	 * 
	 * @param y
	 * @param x
	 */
	private static void buildType3(int y, int x) {
		final int floorType;
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floorType = Constants.LIGHT_FLOOR; // Floor with light
		} else {
			floorType = Constants.DARK_FLOOR; // Dark floor
		}
		
		int offset = 2 + Rnd.randomInt(2);
		int top = y - offset;
		int bottom = y + offset;
		int left = x - 1;
		int right = x + 1;
		
		for (int i = top; i <= bottom; i++) {
			for (int j = left; j <= right; j++) {
				CaveType floor = Variable.cave[i][j];
				floor.fval = floorType;
				floor.litRoom = true;
			}
		}
		
		for (int i = (top - 1); i <= (bottom + 1); i++) {
			CaveType leftWall = Variable.cave[i][left - 1];
			leftWall.fval = Constants.GRANITE_WALL;
			leftWall.litRoom = true;
			
			CaveType rightWall = Variable.cave[i][right + 1];
			rightWall.fval = Constants.GRANITE_WALL;
			rightWall.litRoom = true;
		}
		
		for (int i = left; i <= right; i++) {
			CaveType topWall = Variable.cave[top - 1][i];
			topWall.fval = Constants.GRANITE_WALL;
			topWall.litRoom = true;
			
			CaveType bottomWall = Variable.cave[bottom + 1][i];
			bottomWall.fval = Constants.GRANITE_WALL;
			bottomWall.litRoom = true;
		}
		
		offset = 2 + Rnd.randomInt(9);
		top = y - 1;
		bottom  = y + 1;
		left   = x - offset;
		right  = x + offset;
		
		for (int i = top; i <= bottom; i++) {
			for (int j = left; j <= right; j++) {
				CaveType floor = Variable.cave[i][j];
				floor.fval = floorType;
				floor.litRoom = true;
			}
		}
		for (int i = (top - 1); i <= (bottom + 1); i++) {
			CaveType leftWall = Variable.cave[i][left - 1];
			if (leftWall.fval != floorType) {
				leftWall.fval = Constants.GRANITE_WALL;
				leftWall.litRoom = true;
			}
			
			CaveType rightWall = Variable.cave[i][right + 1];
			if (rightWall.fval != floorType) {
				rightWall.fval = Constants.GRANITE_WALL;
				rightWall.litRoom = true;
			}
		}
		
		for (int i = left; i <= right; i++) {
			CaveType topWall = Variable.cave[top - 1][i];
			if (topWall.fval != floorType) {
				topWall.fval = Constants.GRANITE_WALL;
				topWall.litRoom = true;
			}
			
			CaveType bottomWall = Variable.cave[bottom + 1][i];
			if (bottomWall.fval != floorType) {
				bottomWall.fval = Constants.GRANITE_WALL;
				bottomWall.litRoom = true;
			}
		}
		
		// Special features.
		switch (Rnd.randomInt(4)) {
		case 1: // Large middle pillar
			for (int i = y - 1; i <= y + 1; i++) {
				for (int j = x - 1; j <= x + 1; j++) {
					Variable.cave[i][j].fval = Constants.TMP1_WALL;
				}
			}
			break;
			
		case 2: // Inner treasure vault
			for (int i = y - 1; i <= y + 1; i++) {
				Variable.cave[i][x - 1].fval = Constants.TMP1_WALL;
				Variable.cave[i][x + 1].fval = Constants.TMP1_WALL;
			}
			
			Variable.cave[y - 1][x].fval = Constants.TMP1_WALL;
			Variable.cave[y + 1][x].fval = Constants.TMP1_WALL;
			
			int tmp = Rnd.randomInt(4); // Place a door
			if (tmp < 3) {
				placeSecretDoor(y - 3 + (tmp << 1), x);
			} else {
				placeSecretDoor(y, x - 7 + (tmp << 1));
			}
			
			// Place a treasure in the vault
			Misc3.placeObject(y, x, false);
			
			// Let's guard the treasure well.
			vaultMonster(y, x, 2 + Rnd.randomInt(2));
			
			// Traps naturally
			vaultTrap(y, x, 4, 4, 1 + Rnd.randomInt(3));
			break;
			
		case 3:
			if (Rnd.randomInt(3) == 1) {
				Variable.cave[y - 1][x - 2].fval = Constants.TMP1_WALL;
				Variable.cave[y + 1][x - 2].fval = Constants.TMP1_WALL;
				Variable.cave[y - 1][x + 2].fval = Constants.TMP1_WALL;
				Variable.cave[y + 1][x + 2].fval = Constants.TMP1_WALL;
				Variable.cave[y - 2][x - 1].fval = Constants.TMP1_WALL;
				Variable.cave[y - 2][x + 1].fval = Constants.TMP1_WALL;
				Variable.cave[y + 2][x - 1].fval = Constants.TMP1_WALL;
				Variable.cave[y + 2][x + 1].fval = Constants.TMP1_WALL;
				
				if (Rnd.randomInt(3) == 1) {
					placeSecretDoor(y, x - 2);
					placeSecretDoor(y, x + 2);
					placeSecretDoor(y - 2, x);
					placeSecretDoor(y + 2, x);
				}
			} else if (Rnd.randomInt(3) == 1) {
				Variable.cave[y][x].fval = Constants.TMP1_WALL;
				Variable.cave[y - 1][x].fval = Constants.TMP1_WALL;
				Variable.cave[y + 1][x].fval = Constants.TMP1_WALL;
				Variable.cave[y][x - 1].fval = Constants.TMP1_WALL;
				Variable.cave[y][x + 1].fval = Constants.TMP1_WALL;
			} else if (Rnd.randomInt(3) == 1) {
				Variable.cave[y][x].fval = Constants.TMP1_WALL;
			}
			break;
			
		case 4:
			break;
		default:
			break;
		}
	}
	
	/**
	 * Constructs a tunnel between two points.
	 * 
	 * @param row1
	 * @param col1
	 * @param row2
	 * @param col2
	 */
	private static void buildTunnel(int row1, int col1, int row2, int col2) {
		Point[] tunnelStack = new Point[1000];
		Point[] wallStack = new Point[1000];
		for (int k = 0; k < 1000; k++) {
			tunnelStack[k] = new Point();
			wallStack[k] = new Point();
		}
		
		// Main procedure for Tunnel
		// Note: 9 is a temporary value
		boolean stopFlag = false;
		boolean doorFlag = false;
		int tunnelIndex = 0;
		int wallIndex = 0;
		int mainLoopCount = 0;
		int startRow = row1;
		int startCol = col1;
		
		IntPointer rowDir = new IntPointer();
		IntPointer colDir = new IntPointer();
		getCorrectDirection(rowDir, colDir, row1, col1, row2, col2);
		
		do {
			// prevent infinite loops, just in case
			mainLoopCount++;
			if (mainLoopCount > 2000) {
				stopFlag = true;
			}
			
			if (Rnd.randomInt(100) > Constants.DUN_TUN_CHG) {
				if (Rnd.randomInt(Constants.DUN_TUN_RND) == 1) {
					getRandomDirection(rowDir, colDir);
				} else {
					getCorrectDirection(rowDir, colDir, row1, col1, row2, col2);
				}
			}
			
			int tmpRow = row1 + rowDir.value();
			int tmpCol = col1 + colDir.value();
			while (!Misc1.isInBounds(tmpRow, tmpCol)) {
				if (Rnd.randomInt(Constants.DUN_TUN_RND) == 1) {
					getRandomDirection(rowDir, colDir);
				} else {
					getCorrectDirection(rowDir, colDir, row1, col1, row2, col2);
				}
				tmpRow = row1 + rowDir.value();
				tmpCol = col1 + colDir.value();
			}
			
			switch (Variable.cave[tmpRow][tmpCol].fval) {
			case Constants.NULL_WALL:
				row1 = tmpRow;
				col1 = tmpCol;
				if (tunnelIndex < 1000) {
					tunnelStack[tunnelIndex].y = row1;
					tunnelStack[tunnelIndex].x = col1;
					tunnelIndex++;
				}
				doorFlag = false;
				break;
			case Constants.TMP2_WALL:
				// do nothing
				break;
			case Constants.GRANITE_WALL:
				row1 = tmpRow;
				col1 = tmpCol;
				if (wallIndex < 1000) {
					wallStack[wallIndex].y = row1;
					wallStack[wallIndex].x = col1;
					wallIndex++;
				}
				
				for (int i = row1 - 1; i <= row1 + 1; i++) {
					for (int j = col1 - 1; j <= col1 + 1; j++) {
						if (Misc1.isInBounds(i, j)) {
							CaveType d_ptr = Variable.cave[i][j];
							// values 11 and 12 are impossible here, place_streamer
							// is never run before build_tunnel
							if (d_ptr.fval == Constants.GRANITE_WALL) {
								d_ptr.fval = Constants.TMP2_WALL;
							}
						}
					}
				}
				break;
			case Constants.CORR_FLOOR:
				// fall through
			case Constants.BLOCKED_FLOOR:
				row1 = tmpRow;
				col1 = tmpCol;
				if (!doorFlag) {
					if (doorIndex < 100) {
						doorStack[doorIndex].y = row1;
						doorStack[doorIndex].x = col1;
						doorIndex++;
					}
					doorFlag = true;
				}
				
				if (Rnd.randomInt(100) > Constants.DUN_TUN_CON) {
					// make sure that tunnel has gone a reasonable distance
					// before stopping it, this helps prevent isolated rooms
					tmpRow = row1 - startRow;
					if (tmpRow < 0) {
						tmpRow = -tmpRow;
					}
					
					tmpCol = col1 - startCol;
					if (tmpCol < 0) {
						tmpCol = -tmpCol;
					}
					
					if (tmpRow > 10 || tmpCol > 10) {
						stopFlag = true;
					}
				}
				break;
			default: // c_ptr->fval != NULL, TMP2, GRANITE, CORR
				row1 = tmpRow;
				col1 = tmpCol;
				break;
			}
		} while (((row1 != row2) || (col1 != col2)) && !stopFlag);
		
		for (int i = 0; i < tunnelIndex; i++) {
			Variable.cave[tunnelStack[i].y][tunnelStack[i].x].fval =
					Constants.CORR_FLOOR;
		}
		
		for (int i = 0; i < wallIndex; i++) {
			CaveType cavePos = Variable.cave[wallStack[i].y][wallStack[i].x];
			if (cavePos.fval == Constants.TMP2_WALL) {
				if (Rnd.randomInt(100) < Constants.DUN_TUN_PEN) {
					placeDoor(wallStack[i].y, wallStack[i].x);
				} else {
					// these have to be doorways to rooms
					cavePos.fval = Constants.CORR_FLOOR;
				}
			}
		}
	}
	
	private static boolean isNextToCorridor(int y, int x) {
		boolean nextTo;
		
		if (Misc1.numAdjacentCorridors(y, x) > 2) {
			if ((Variable.cave[y - 1][x].fval >= Constants.MIN_CAVE_WALL)
					&& (Variable.cave[y + 1][x].fval >= Constants.MIN_CAVE_WALL)) {
				nextTo = true;
			} else if ((Variable.cave[y][x - 1].fval >= Constants.MIN_CAVE_WALL)
					&& (Variable.cave[y][x + 1].fval >= Constants.MIN_CAVE_WALL)) {
				nextTo = true;
			} else {
				nextTo = false;
			}
		} else {
			nextTo = false;
		}
		return nextTo;
	}
	
	/**
	 * Places door at y, x position if at least 2 walls found
	 * 
	 * @param y
	 * @param x
	 */
	private static void tryDoor(int y, int x) {
		if ((Variable.cave[y][x].fval == Constants.CORR_FLOOR)
				&& (Rnd.randomInt(100) > Constants.DUN_TUN_JCT)
				&& isNextToCorridor(y, x)) {
			placeDoor(y, x);
		}
	}
	
	/**
	 * Returns random co-ordinates. -RAK-
	 * 
	 * @param y
	 * @param x
	 */
	private static void newSpot(IntPointer y, IntPointer x) {
		int newY;
		int newX;
		CaveType cavePos;
		
		do {
			newY = Rnd.randomInt(Variable.currHeight - 2);
			newX = Rnd.randomInt(Variable.currWidth - 2);
			cavePos = Variable.cave[newY][newX];
		} while (cavePos.fval >= Constants.MIN_CLOSED_SPACE
				|| (cavePos.creatureIndex != 0)
				|| (cavePos.treasureIndex != 0));
		
		y.value(newY);
		x.value(newX);
	}
	
	/**
	 * Cave logic flow for generation of new dungeon.
	 */
	private static void generateCave() {
		final int ROOM_WIDTH = 20;
		final int ROOM_HEIGHT = 20;
		final int ROOM_BLOCKS = ROOM_WIDTH * ROOM_HEIGHT;
		
		int[][] roomMap = new int[ROOM_HEIGHT][ROOM_WIDTH];
		int[] yloc = new int[ROOM_BLOCKS];
		int[] xloc = new int[ROOM_BLOCKS];
		
		final int roomRows = 2 * (Variable.currHeight / Constants.SCREEN_HEIGHT);
		final int roomCols = 2 * (Variable.currWidth / Constants.SCREEN_WIDTH);
		for (int i = 0; i < roomRows; i++) {
			for (int j = 0; j < roomCols; j++) {
				roomMap[i][j] = 0;
			}
		}
		
		final int numRooms = Rnd.randomIntNormalized(Constants.DUN_ROO_MEA, 2);
		for (int i = 0; i < numRooms; i++) {
			roomMap[Rnd.randomInt(roomRows) - 1][Rnd.randomInt(roomCols) - 1] = 1;
		}
		int maxPos = 0;
		for (int i = 0; i < roomRows; i++) {
			for (int j = 0; j < roomCols; j++) {
				if (roomMap[i][j] != 0) {
					yloc[maxPos] = i * (Constants.SCREEN_HEIGHT >> 1) + Constants.QUART_HEIGHT;
					xloc[maxPos] = j * (Constants.SCREEN_WIDTH >> 1) + Constants.QUART_WIDTH;
					if (Variable.dungeonLevel > Rnd.randomInt(Constants.DUN_UNUSUAL)) {
						switch (Rnd.randomInt(3)) {
						case 1:
							buildType1(yloc[maxPos], xloc[maxPos]);
							break;
						case 2:
							buildType2(yloc[maxPos], xloc[maxPos]);
							break;
						default:
							buildType3(yloc[maxPos], xloc[maxPos]);
							break;
						}
					} else {
						buildRoom(yloc[maxPos], xloc[maxPos]);
					}
					maxPos++;
				}
			}
		}
		
		for (int i = 0; i < maxPos; i++) {
			int pick1 = Rnd.randomInt(maxPos) - 1;
			int pick2 = Rnd.randomInt(maxPos) - 1;
			int tmpY = yloc[pick1];
			int tmpX = xloc[pick1];
			yloc[pick1] = yloc[pick2];
			xloc[pick1] = xloc[pick2];
			yloc[pick2] = tmpY;
			xloc[pick2] = tmpX;
		}
		
		doorIndex = 0;
		// move zero entry to k, so that can call build_tunnel all k times
		yloc[maxPos] = yloc[0];
		xloc[maxPos] = xloc[0];
		for (int i = 0; i < maxPos; i++) {
			buildTunnel(yloc[i + 1], xloc[i + 1], yloc[i], xloc[i]);
		}
		
		fillCave(Constants.GRANITE_WALL);
		for (int i = 0; i < Constants.DUN_STR_MAG; i++) {
			placeStreamer(Constants.MAGMA_WALL, Constants.DUN_STR_MC);
		}
		for (int i = 0; i < Constants.DUN_STR_QUA; i++) {
			placeStreamer(Constants.QUARTZ_WALL, Constants.DUN_STR_QC);
		}
		
		placeBoundary();
		// Place intersection doors
		for (int i = 0; i < doorIndex; i++) {
			tryDoor(doorStack[i].y, doorStack[i].x - 1);
			tryDoor(doorStack[i].y, doorStack[i].x + 1);
			tryDoor(doorStack[i].y - 1, doorStack[i].x);
			tryDoor(doorStack[i].y + 1, doorStack[i].x);
		}
		
		int allocLevel = (Variable.dungeonLevel / 3);
		if (allocLevel < 2) {
			allocLevel = 2;
		} else if (allocLevel > 10) {
			allocLevel = 10;
		}
		
		placeStairs(2, Rnd.randomInt(2) + 2, 3);
		placeStairs(1, Rnd.randomInt(2), 3);
		// Set up the character co-ords, used by alloc_monster, place_win_monster
		IntPointer cr = new IntPointer(Player.y);
		IntPointer cc = new IntPointer(Player.x);
		newSpot(cr, cc);
		Player.y = cr.value();
		Player.x = cc.value();
		Misc1.spawnMonster((Rnd.randomInt(8) + Constants.MIN_MALLOC_LEVEL + allocLevel), 0, true);
		Misc3.spawnObject(Sets.SET_CORR, 3, Rnd.randomInt(allocLevel));
		Misc3.spawnObject(Sets.SET_ROOM, 5, Rnd.randomIntNormalized(Constants.TREAS_ROOM_ALLOC, 3));
		Misc3.spawnObject(Sets.SET_FLOOR, 5, Rnd.randomIntNormalized(Constants.TREAS_ANY_ALLOC, 3));
		Misc3.spawnObject(Sets.SET_FLOOR, 4, Rnd.randomIntNormalized(Constants.TREAS_GOLD_ALLOC, 3));
		Misc3.spawnObject(Sets.SET_FLOOR, 1, Rnd.randomInt(allocLevel));
		if (Variable.dungeonLevel >= Constants.WIN_MON_APPEAR) {
			Misc1.placeWinMonster();
		}
	}
	
	/**
	 * Builds a store at a row, column coordinate.
	 * 
	 * @param storeNum
	 * @param y
	 * @param x
	 */
	private static void buildStore(int storeNum, int y, int x) {
		int yval = y * 10 + 5;
		int xval = x * 16 + 16;
		int top = yval - Rnd.randomInt(3);
		int bottom = yval + Rnd.randomInt(4);
		int left = xval - Rnd.randomInt(6);
		int right = xval + Rnd.randomInt(6);
		
		for (int i = top; i <= bottom; i++) {
			for (int j = left; j <= right; j++) {
				Variable.cave[i][j].fval = Constants.BOUNDARY_WALL;
			}
		}
		
		int tmp = Rnd.randomInt(4);
		int doorY = 0;
		int doorX = 0;
		if (tmp < 3) {
			doorY = Rnd.randomInt(bottom - top) + top - 1;
			if (tmp == 1) {
				doorX = left;
			} else {
				doorX = right;
			}
		} else {
			doorX = Rnd.randomInt(right - left) + left - 1;
			if (tmp == 3) {
				doorY = bottom;
			} else {
				doorY = top;
			}
		}
		
		CaveType cavePos = Variable.cave[doorY][doorX];
		cavePos.fval = Constants.CORR_FLOOR;
		int treasureIndex = Misc1.popTreasure();
		cavePos.treasureIndex = treasureIndex;
		Desc.copyIntoInventory(Treasure.treasureList[treasureIndex],
				Constants.OBJ_STORE_DOOR + storeNum);
	}
	
	/**
	 * Link all free space in treasure list together
	 */
	private static void clearTreasureList() {
		for (int i = 0; i < Constants.MAX_TALLOC; i++) {
			Desc.copyIntoInventory(Treasure.treasureList[i],
					Constants.OBJ_NOTHING);
		}
		Treasure.currTreasureIndex = Constants.MIN_TRIX;
	}
	
	/**
	 * Link all free space in monster list together
	 */
	private static void clearMonsterList() {
		for (int i = 0; i < Constants.MAX_MALLOC; i++) {
			Monsters.monsterList[i] = Monsters.getBlankMonster();
		}
		Monsters.freeMonsterIndex = Constants.MIN_MONIX;
	}
	
	/**
	 * Town logic flow for generation of new town
	 */
	private static void generateTown() {
		int[] rooms = new int[6];
		
		Rnd.setSeed(Variable.townSeed);
		for (int i = 0; i < 6; i++) {
			rooms[i] = i;
		}
		int l = 6;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				int k = Rnd.randomInt(l) - 1;
				buildStore(rooms[k], i, j);
				for (int m = k; m < l - 1; m++) {
					rooms[m] = rooms[m + 1];
				}
				l--;
			}
		}
		fillCave(Constants.DARK_FLOOR);
		
		// make stairs before reset_seed, so that they don't move around
		placeBoundary();
		placeStairs(2, 1, 0);
		Rnd.resetSeed();
		
		// Set up the character co-ords, used by alloc_monster below
		IntPointer cr = new IntPointer(Player.y);
		IntPointer cc = new IntPointer(Player.x);
		newSpot(cr, cc);
		Player.y = cr.value();
		Player.x = cc.value();
		if ((0x1 & (Variable.turn / 5000)) > 0) {
			// Night
			for (int i = 0; i < Variable.currHeight; i++) {
				for (int j = 0; j < Variable.currWidth; j++) {
					CaveType cavePos = Variable.cave[i][j];
					if (cavePos.fval != Constants.DARK_FLOOR) {
						cavePos.permLight = true;
					}
				}
			}
			Misc1.spawnMonster(Constants.MIN_MALLOC_TN, 3, true);
		} else {
			// Day
			for (int i = 0; i < Variable.currHeight; i++) {
				for (int j = 0; j < Variable.currWidth; j++) {
					Variable.cave[i][j].permLight = true;
				}
			}
			Misc1.spawnMonster(Constants.MIN_MALLOC_TD, 3, true);
		}
		Store1.storeInventoryInit();
	}
	
	/**
	 * Generates a random dungeon level. -RAK-
	 */
	public static void generateLevel() {
		Variable.panelRowMin = 0;
		Variable.panelRowMax = 0;
		Variable.panelColMin = 0;
		Variable.panelColMax = 0;
		Player.y = -1;
		Player.x = -1;
		
		clearTreasureList();
		clearMonsterList();
		clearCave();
		
		if (Variable.dungeonLevel == 0) {
			Variable.currHeight = Constants.SCREEN_HEIGHT;
			Variable.currWidth	 = Constants.SCREEN_WIDTH;
			Variable.maxPanelRows = (Variable.currHeight
					/ Constants.SCREEN_HEIGHT) * 2 - 2;
			Variable.maxPanelCols = (Variable.currWidth
					/ Constants.SCREEN_WIDTH) * 2 - 2;
			Variable.panelRow = Variable.maxPanelRows;
			Variable.panelCol = Variable.maxPanelCols;
			generateTown();
		} else {
			Variable.currHeight = Constants.MAX_HEIGHT;
			Variable.currWidth = Constants.MAX_WIDTH;
			Variable.maxPanelRows = (Variable.currHeight
					/ Constants.SCREEN_HEIGHT) * 2 - 2;
			Variable.maxPanelCols = (Variable.currWidth
					/ Constants.SCREEN_WIDTH) * 2 - 2;
			Variable.panelRow = Variable.maxPanelRows;
			Variable.panelCol = Variable.maxPanelCols;
			generateCave();
		}
	}
}
