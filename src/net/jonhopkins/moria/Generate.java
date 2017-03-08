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
	
	/* Always picks a correct direction		*/
	private static void getCorrectDirection(IntPointer rdir, IntPointer cdir, int y1, int x1, int y2, int x2) {
		if (y1 < y2) {
			rdir.value(1);
		} else if (y1 == y2) {
			rdir.value(0);
		} else {
			rdir.value(-1);
		}
		if (x1 < x2) {
			cdir.value(1);
		} else if (x1 == x2) {
			cdir.value(0);
		} else {
			cdir.value(-1);
		}
		if ((rdir.value() != 0) && (cdir.value() != 0)) {
			if (Rnd.randomInt(2) == 1) {
				rdir.value(0);
			} else {
				cdir.value(0);
			}
		}
	}
	
	/* Chance of wandering direction			*/
	private static void getRandomDirection(IntPointer rdir, IntPointer cdir) {
		int tmp;
		
		tmp = Rnd.randomInt(4);
		
		if (tmp < 3) {
			cdir.value(0);
			rdir.value(-3 + (tmp << 1)); /* tmp=1 -> *rdir=-1; tmp=2 -> *rdir=1 */
		} else {
			rdir.value(0);
			cdir.value(-7 + (tmp << 1)); /* tmp=3 -> *cdir=-1; tmp=4 -> *cdir=1 */
		}
	}
	
	/* Blanks out entire cave				-RAK-	*/
	private static void clearCave() {
		for (int i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (int j = 0; j < Constants.MAX_WIDTH; j++) {
				Variable.cave[i][j].creatureIndex = 0;
				Variable.cave[i][j].treasureIndex = 0;
				Variable.cave[i][j].fval = 0;
				Variable.cave[i][j].litRoom = false;
				Variable.cave[i][j].fieldMark = false;
				Variable.cave[i][j].permLight = false;
				Variable.cave[i][j].tempLight = false;
			}
		}
	}
	
	/* Fills in empty spots with desired rock		-RAK-	*/
	/* Note: 9 is a temporary value.				*/
	private static void fillCave(int fval) {
		int i, j;
		int ptr_count;
		CaveType c_ptr;
		
		/* no need to check the border of the cave */
		
		for (i = Variable.currHeight - 2; i > 0; i--) {
			ptr_count = 1;
			c_ptr = Variable.cave[i][ptr_count];
			for (j = Variable.currWidth - 2; j > 0; j--) {
				if ((c_ptr.fval == Constants.NULL_WALL) || (c_ptr.fval == Constants.TMP1_WALL) || (c_ptr.fval == Constants.TMP2_WALL)) {
					c_ptr.fval = fval;
				}
				ptr_count++;
				c_ptr = Variable.cave[i][ptr_count];
			}
		}
	}
	
	/* Places indestructible rock around edges of dungeon	-RAK-	*/
	private static void placeBoundary() {
		int i;
		CaveType top_ptr, bottom_ptr;
		CaveType left_ptr;
		CaveType right_ptr;
		
		/* put permanent wall on leftmost row and rightmost row */
		for (i = 0; i < Variable.currHeight; i++) {
			left_ptr = Variable.cave[i][0];
			left_ptr.fval	= Constants.BOUNDARY_WALL;
			right_ptr = Variable.cave[i][Variable.currWidth - 1];
			right_ptr.fval	= Constants.BOUNDARY_WALL;
		}
		
		/* put permanent wall on top row and bottom row */
		for (i = 0; i < Variable.currWidth; i++) {
			top_ptr = Variable.cave[0][i];
			top_ptr.fval	= Constants.BOUNDARY_WALL;
			bottom_ptr = Variable.cave[Variable.currHeight - 1][i];
			bottom_ptr.fval	= Constants.BOUNDARY_WALL;
		}
	}
	
	/* Places "streamers" of rock through dungeon		-RAK-	*/
	private static void placeStreamer(int fval, int treas_chance) {
		int i, tx, ty;
		IntPointer y, x;
		int t1, t2, dir;
		CaveType c_ptr;
		
		/* Choose starting point and direction		*/
		y = new IntPointer((Variable.currHeight / 2) + 11 - Rnd.randomInt(23));
		x = new IntPointer((Variable.currWidth / 2)  + 16 - Rnd.randomInt(33));
		
		dir = Rnd.randomInt(8);	/* Number 1-4, 6-9	*/
		if (dir > 4) {
			++dir;
		}
		
		/* Place streamer into dungeon			*/
		t1 = 2 * Constants.DUN_STR_RNG + 1;	/* Constants	*/
		t2 =	 Constants.DUN_STR_RNG + 1;
		do {
			for (i = 0; i < Constants.DUN_STR_DEN; i++) {
				ty = y.value() + Rnd.randomInt(t1) - t2;
				tx = x.value() + Rnd.randomInt(t1) - t2;
				if (Misc1.isInBounds(ty, tx)) {
					c_ptr = Variable.cave[ty][tx];
					if (c_ptr.fval == Constants.GRANITE_WALL) {
						c_ptr.fval = fval;
						if (Rnd.randomInt(treas_chance) == 1) {
							Misc3.placeGold(ty, tx);
						}
					}
				}
			}
		} while (Misc3.moveMonster(dir, y, x));
	}
	
	private static void placeOpenDoor(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = Misc1.popTreasure();
		cave_ptr = Variable.cave[y][x];
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_OPEN_DOOR);
		cave_ptr.fval = Constants.CORR_FLOOR;
	}
	
	private static void placeBrokenDoor(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = Misc1.popTreasure();
		cave_ptr = Variable.cave[y][x];
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_OPEN_DOOR);
		cave_ptr.fval = Constants.CORR_FLOOR;
		Treasure.treasureList[cur_pos].misc = 1;
	}
	
	private static void placeClosedDoor(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = Misc1.popTreasure();
		cave_ptr = Variable.cave[y][x];
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_CLOSED_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
	}
	
	private static void placeLockedDoor(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = Misc1.popTreasure();
		cave_ptr = Variable.cave[y][x];
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_CLOSED_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
		Treasure.treasureList[cur_pos].misc = Rnd.randomInt(10) + 10;
	}
	
	private static void placeStuckDoor(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = Misc1.popTreasure();
		cave_ptr = Variable.cave[y][x];
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_CLOSED_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
		Treasure.treasureList[cur_pos].misc = -Rnd.randomInt(10) - 10;
	}
	
	private static void placeSecretDoor(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = Misc1.popTreasure();
		cave_ptr = Variable.cave[y][x];
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_SECRET_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
	}
	
	private static void placeDoor(int y, int x) {
		int tmp;
		
		tmp = Rnd.randomInt(3);
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
	
	/* Place an up staircase at given y, x			-RAK-	*/
	private static void placeUpStairs(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cave_ptr = Variable.cave[y][x];
		if (cave_ptr.treasureIndex != 0) {
			Moria3.deleteObject(y, x);
		}
		cur_pos = Misc1.popTreasure();
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_UP_STAIR);
	}
	
	/* Place a down staircase at given y, x			-RAK-	*/
	private static void placeDownStairs(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cave_ptr = Variable.cave[y][x];
		if (cave_ptr.treasureIndex != 0) {
			Moria3.deleteObject(y, x);
		}
		cur_pos = Misc1.popTreasure();
		cave_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_DOWN_STAIR);
	}
	
	/* Places a staircase 1=up, 2=down			-RAK-	*/
	private static void placeStairs(int typ, int num, int walls) {
		CaveType cave_ptr;
		int i, j;
		boolean flag;
		int y1, x1, y2, x2;
		
		for (i = 0; i < num; i++) {
			flag = false;
			do {
				j = 0;
				do {
					/* Note: don't let y1/x1 be zero, and don't let y2/x2 be equal
					   to cur_height-1/cur_width-1, these values are always
					   BOUNDARY_ROCK. */
					y1 = Rnd.randomInt(Variable.currHeight - 14);
					x1 = Rnd.randomInt(Variable.currWidth  - 14);
					y2 = y1 + 12;
					x2 = x1 + 12;
					do {
						do {
							cave_ptr = Variable.cave[y1][x1];
							if (cave_ptr.fval <= Constants.MAX_OPEN_SPACE && (cave_ptr.treasureIndex == 0) && (Misc1.isNextToWalls(y1, x1) >= walls)) {
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
	
	/* Place a trap with a given displacement of point	-RAK-	*/
	private static void vaultTrap(int y, int x, int yd, int xd, int num) {
		int count, y1, x1;
		int i;
		boolean flag;
		CaveType c_ptr;
		
		for (i = 0; i < num; i++) {
			flag = false;
			count = 0;
			do {
				y1 = y - yd - 1 + Rnd.randomInt(2 * yd + 1);
				x1 = x - xd - 1 + Rnd.randomInt(2 * xd + 1);
				c_ptr = Variable.cave[y1][x1];
				if ((c_ptr.fval != Constants.NULL_WALL) && (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) && (c_ptr.treasureIndex == 0)) {
					Misc3.placeTrap(y1, x1, Rnd.randomInt(Constants.MAX_TRAP) - 1);
					flag = true;
				}
				count++;
			} while ((!flag) && (count <= 5));
		}
	}
	
	/* Place a trap with a given displacement of point	-RAK-	*/
	private static void vaultMonster(int y, int x, int num) {
		int i;
		IntPointer y1 = new IntPointer(), x1 = new IntPointer();
		
		for (i = 0; i < num; i++) {
			y1.value(y);
			x1.value(x);
			Misc1.summonMonster(y1, x1, true);
		}
	}
	
	/* Builds a room at a row, column coordinate		-RAK-	*/
	private static void buildRoom(int yval, int xval) {
		int i, j, y_depth, x_right;
		int y_height, x_left;
		short floor;
		CaveType c_ptr, d_ptr;
		
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floor = Constants.LIGHT_FLOOR;	/* Floor with light	*/
		} else {
			floor = Constants.DARK_FLOOR;		/* Dark floor		*/
		}
		
		y_height = yval - Rnd.randomInt(4);
		y_depth  = yval + Rnd.randomInt(3);
		x_left   = xval - Rnd.randomInt(11);
		x_right  = xval + Rnd.randomInt(11);
		
		/* the x dim of rooms tends to be much larger than the y dim, so don't
		   bother rewriting the y loop */
		
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				c_ptr = Variable.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.litRoom = true;
			}
		}
		
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = Variable.cave[i][x_left - 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
			c_ptr = Variable.cave[i][x_right + 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
		}
		
		for (i = x_left; i <= x_right; i++) {
			c_ptr = Variable.cave[y_height - 1][i];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
			d_ptr = Variable.cave[y_depth + 1][i];
			d_ptr.fval = Constants.GRANITE_WALL;
			d_ptr.litRoom = true;
		}
	}
	
	/* Builds a room at a row, column coordinate		-RAK-	*/
	/* Type 1 unusual rooms are several overlapping rectangular ones	*/
	private static void buildType1(int yval, int xval) {
		int y_height, y_depth;
		int x_left, x_right, limit;
		int i0, i, j;
		short floor;
		CaveType c_ptr, d_ptr;
		
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floor = Constants.LIGHT_FLOOR;	/* Floor with light	*/
		} else {
			floor = Constants.DARK_FLOOR;		/* Dark floor		*/
		}
		limit = 1 + Rnd.randomInt(2);
		for (i0 = 0; i0 < limit; i0++) {
			y_height = yval - Rnd.randomInt(4);
			y_depth  = yval + Rnd.randomInt(3);
			x_left   = xval - Rnd.randomInt(11);
			x_right  = xval + Rnd.randomInt(11);
			
			/* the x dim of rooms tends to be much larger than the y dim, so don't
			   bother rewriting the y loop */
			
			for (i = y_height; i <= y_depth; i++) {
				for (j = x_left; j <= x_right; j++) {
					c_ptr = Variable.cave[i][j];
					c_ptr.fval = floor;
					c_ptr.litRoom = true;
				}
			}
			for (i = (y_height - 1); i <= (y_depth + 1); i++) {
				c_ptr = Variable.cave[i][x_left - 1];
				if (c_ptr.fval != floor) {
					c_ptr.fval = Constants.GRANITE_WALL;
					c_ptr.litRoom = true;
				}
				c_ptr = Variable.cave[i][x_right + 1];
				if (c_ptr.fval != floor) {
					c_ptr.fval = Constants.GRANITE_WALL;
					c_ptr.litRoom = true;
				}
			}
			for (i = x_left; i <= x_right; i++) {
				c_ptr = Variable.cave[y_height - 1][i];
				if (c_ptr.fval != floor) {
					c_ptr.fval = Constants.GRANITE_WALL;
					c_ptr.litRoom = true;
				}
				d_ptr = Variable.cave[y_depth + 1][i];
				if (d_ptr.fval != floor) {
					d_ptr.fval = Constants.GRANITE_WALL;
					d_ptr.litRoom = true;
				}
			}
		}
	}
	
	/* Builds an unusual room at a row, column coordinate	-RAK-	*/
	/* Type 2 unusual rooms all have an inner room:			*/
	/*   1 - Just an inner room with one door			*/
	/*   2 - An inner room within an inner room			*/
	/*   3 - An inner room with pillar(s)				*/
	/*   4 - Inner room has a maze					*/
	/*   5 - A set of four inner rooms				*/
	private static void buildType2(int yval, int xval) {
		int i, j, y_height, x_left;
		int y_depth, x_right, tmp;
		short floor;
		CaveType c_ptr, d_ptr;
		
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floor = Constants.LIGHT_FLOOR;	/* Floor with light	*/
		} else {
			floor = Constants.DARK_FLOOR;		/* Dark floor		*/
		}
		y_height = yval - 4;
		y_depth  = yval + 4;
		x_left   = xval - 11;
		x_right  = xval + 11;
		
		/* the x dim of rooms tends to be much larger than the y dim, so don't
		   bother rewriting the y loop */
		
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				c_ptr = Variable.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.litRoom = true;
			}
		}
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = Variable.cave[i][x_left - 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
			c_ptr = Variable.cave[i][x_right + 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = Variable.cave[y_height - 1][i];
			c_ptr.fval  = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
			d_ptr = Variable.cave[y_depth + 1][i];
			d_ptr.fval = Constants.GRANITE_WALL;
			d_ptr.litRoom = true;
		}
		/* The inner room		*/
		y_height = y_height + 2;
		y_depth  = y_depth  - 2;
		x_left   = x_left   + 2;
		x_right  = x_right  - 2;
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			Variable.cave[i][x_left - 1].fval = Constants.TMP1_WALL;
			Variable.cave[i][x_right + 1].fval = Constants.TMP1_WALL;
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = Variable.cave[y_height - 1][i];
			c_ptr.fval = Constants.TMP1_WALL;
			d_ptr = Variable.cave[y_depth + 1][i];
			d_ptr.fval = Constants.TMP1_WALL;
		}
		/* Inner room variations		*/
		switch (Rnd.randomInt(5)) {
		case 1:	/* Just an inner room.	*/
			tmp = Rnd.randomInt(4);
			if (tmp < 3) {	/* Place a door	*/
				if (tmp == 1) {
					placeSecretDoor(y_height - 1, xval);
				} else {
					placeSecretDoor(y_depth + 1, xval);
				}
			} else {
				if (tmp == 3) {
					placeSecretDoor(yval, x_left - 1);
				} else {
					placeSecretDoor(yval, x_right + 1);
				}
			}
			vaultMonster(yval, xval, 1);
			break;
			
		case 2:	/* Treasure Vault	*/
			tmp = Rnd.randomInt(4);
			if (tmp < 3) {	/* Place a door	*/
				if (tmp == 1) {
					placeSecretDoor(y_height - 1, xval);
				} else {
					placeSecretDoor(y_depth + 1, xval);
				}
			} else {
				if (tmp == 3) {
					placeSecretDoor(yval, x_left - 1);
				} else {
					placeSecretDoor(yval, x_right + 1);
				}
			}
			
			for (i = yval - 1; i <= yval + 1; i++) {
				Variable.cave[i][xval - 1].fval = Constants.TMP1_WALL;
				Variable.cave[i][xval + 1].fval = Constants.TMP1_WALL;
			}
			Variable.cave[yval - 1][xval].fval = Constants.TMP1_WALL;
			Variable.cave[yval + 1][xval].fval = Constants.TMP1_WALL;
			
			tmp = Rnd.randomInt(4);	/* Place a door	*/
			if (tmp < 3) {
				placeLockedDoor(yval - 3 + (tmp << 1), xval); /* 1 -> yval-1; 2 -> yval+1*/
			} else {
				placeLockedDoor(yval, xval - 7 + (tmp << 1));
			}
			
			/* Place an object in the treasure vault	*/
			tmp = Rnd.randomInt(10);
			if (tmp > 2) {
				Misc3.placeObject(yval, xval, false);
			} else if (tmp == 2) {
				placeDownStairs(yval, xval);
			} else {
				placeUpStairs(yval, xval);
			}
			
			/* Guard the treasure well		*/
			vaultMonster(yval, xval, 2 + Rnd.randomInt(3));
			/* If the monsters don't get 'em.	*/
			vaultTrap(yval, xval, 4, 10, 2 + Rnd.randomInt(3));
			break;
			
		case 3:	/* Inner pillar(s).	*/
			tmp = Rnd.randomInt(4);
			if (tmp < 3) {	/* Place a door	*/
				if (tmp == 1) {
					placeSecretDoor(y_height - 1, xval);
				} else {
					placeSecretDoor(y_depth + 1, xval);
				}
			} else {
				if (tmp == 3) {
					placeSecretDoor(yval, x_left - 1);
				} else {
					placeSecretDoor(yval, x_right + 1);
				}
			}
			
			for (i = yval - 1; i <= yval + 1; i++) {
				for (j = xval - 1; j <= xval + 1; j++) {
					c_ptr = Variable.cave[i][j];
					c_ptr.fval = Constants.TMP1_WALL;
				}
			}
			if (Rnd.randomInt(2) == 1) {
				tmp = Rnd.randomInt(2);
				for (i = yval - 1; i <= yval + 1; i++) {
					for (j = xval - 5 - tmp; j <= xval - 3 - tmp; j++) {
						c_ptr = Variable.cave[i][j];
						c_ptr.fval = Constants.TMP1_WALL;
					}
				}
				for (i = yval - 1; i <= yval + 1; i++) {
					for (j = xval + 3 + tmp; j <= xval + 5 + tmp; j++) {
						c_ptr = Variable.cave[i][j];
						c_ptr.fval = Constants.TMP1_WALL;
					}
				}
			}
			
			if (Rnd.randomInt(3) == 1) {	/* Inner rooms	*/
				for (i = xval - 5; i <= xval + 5; i++) {
					c_ptr = Variable.cave[yval - 1][i];
					c_ptr.fval = Constants.TMP1_WALL;
					d_ptr = Variable.cave[yval + 1][i];
					d_ptr.fval = Constants.TMP1_WALL;
				}
				Variable.cave[yval][xval - 5].fval = Constants.TMP1_WALL;
				Variable.cave[yval][xval + 5].fval = Constants.TMP1_WALL;
				placeSecretDoor(yval - 3 + (Rnd.randomInt(2) << 1), xval - 3);
				placeSecretDoor(yval - 3 + (Rnd.randomInt(2) << 1), xval + 3);
				if (Rnd.randomInt(3) == 1) {
					Misc3.placeObject(yval, xval - 2, false);
				}
				if (Rnd.randomInt(3) == 1) {
					Misc3.placeObject(yval, xval + 2, false);
				}
				vaultMonster(yval, xval - 2, Rnd.randomInt(2));
				vaultMonster(yval, xval + 2, Rnd.randomInt(2));
			}
			break;
			
		case 4:	/* Maze inside.	*/
			tmp = Rnd.randomInt(4);
			if (tmp < 3) {	/* Place a door	*/
				if (tmp == 1) {
					placeSecretDoor(y_height - 1, xval);
				} else {
					placeSecretDoor(y_depth + 1, xval);
				}
			} else {
				if (tmp == 3) {
					placeSecretDoor(yval, x_left - 1);
				} else {
					placeSecretDoor(yval, x_right + 1);
				}
			}
			
			for (i = y_height; i <= y_depth; i++) {
				for (j = x_left; j <= x_right; j++) {
					if ((0x1 & (j + i)) > 0) {
						Variable.cave[i][j].fval = Constants.TMP1_WALL;
					}
				}
			}
			
			/* Monsters just love mazes.		*/
			vaultMonster(yval, xval - 5, Rnd.randomInt(3));
			vaultMonster(yval, xval + 5, Rnd.randomInt(3));
			/* Traps make them entertaining.	*/
			vaultTrap(yval, xval - 3, 2, 8, Rnd.randomInt(3));
			vaultTrap(yval, xval + 3, 2, 8, Rnd.randomInt(3));
			/* Mazes should have some treasure too..	*/
			for (i = 0; i < 3; i++) {
				Misc3.spawnRandomObject(yval, xval, 1);
			}
			break;
			
		case 5:	/* Four small rooms.	*/
			for (i = y_height; i <= y_depth; i++) {
				Variable.cave[i][xval].fval = Constants.TMP1_WALL;
			}
			
			for (i = x_left; i <= x_right; i++) {
				c_ptr = Variable.cave[yval][i];
				c_ptr.fval = Constants.TMP1_WALL;
			}
			
			if (Rnd.randomInt(2) == 1) {
				i = Rnd.randomInt(10);
				placeSecretDoor(y_height - 1, xval - i);
				placeSecretDoor(y_height - 1, xval + i);
				placeSecretDoor(y_depth + 1, xval - i);
				placeSecretDoor(y_depth + 1, xval + i);
			} else {
				i = Rnd.randomInt(3);
				placeSecretDoor(yval + i, x_left - 1);
				placeSecretDoor(yval - i, x_left - 1);
				placeSecretDoor(yval + i, x_right + 1);
				placeSecretDoor(yval - i, x_right + 1);
			}
			
			/* Treasure in each one.		*/
			Misc3.spawnRandomObject(yval, xval, 2 + Rnd.randomInt(2));
			/* Gotta have some monsters.		*/
			vaultMonster(yval + 2, xval - 4, Rnd.randomInt(2));
			vaultMonster(yval + 2, xval + 4, Rnd.randomInt(2));
			vaultMonster(yval - 2, xval - 4, Rnd.randomInt(2));
			vaultMonster(yval - 2, xval + 4, Rnd.randomInt(2));
			break;
		default:
			break;
		}
	}
	
	/* Builds a room at a row, column coordinate		-RAK-	*/
	/* Type 3 unusual rooms are cross shaped				*/
	private static void buildType3(int yval, int xval) {
		int y_height, y_depth;
		int x_left, x_right;
		int tmp, i, j;
		short floor;
		CaveType c_ptr;
		
		if (Variable.dungeonLevel <= Rnd.randomInt(25)) {
			floor = Constants.LIGHT_FLOOR;	/* Floor with light	*/
		} else {
			floor = Constants.DARK_FLOOR;		/* Dark floor		*/
		}
		tmp = 2 + Rnd.randomInt(2);
		y_height = yval - tmp;
		y_depth  = yval + tmp;
		x_left   = xval - 1;
		x_right  = xval + 1;
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				c_ptr = Variable.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.litRoom = true;
			}
		}
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = Variable.cave[i][x_left-1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
			c_ptr = Variable.cave[i][x_right + 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = Variable.cave[y_height - 1][i];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
			c_ptr = Variable.cave[y_depth + 1][i];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.litRoom = true;
		}
		tmp = 2 + Rnd.randomInt(9);
		y_height = yval - 1;
		y_depth  = yval + 1;
		x_left   = xval - tmp;
		x_right  = xval + tmp;
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				c_ptr = Variable.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.litRoom = true;
			}
		}
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = Variable.cave[i][x_left - 1];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.litRoom = true;
			}
			c_ptr = Variable.cave[i][x_right + 1];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.litRoom = true;
			}
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = Variable.cave[y_height - 1][i];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.litRoom = true;
			}
			c_ptr = Variable.cave[y_depth + 1][i];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.litRoom = true;
			}
		}
		/* Special features.			*/
		switch (Rnd.randomInt(4)) {
		case 1:	/* Large middle pillar		*/
			for (i = yval-1; i <= yval+1; i++) {
				for (j = xval - 1; j <= xval + 1; j++) {
					c_ptr = Variable.cave[i][j];
					c_ptr.fval = Constants.TMP1_WALL;
				}
			}
			break;
			
		case 2:	/* Inner treasure vault		*/
			for (i = yval - 1; i <= yval + 1; i++) {
				Variable.cave[i][xval - 1].fval = Constants.TMP1_WALL;
				Variable.cave[i][xval + 1].fval = Constants.TMP1_WALL;
			}
			Variable.cave[yval - 1][xval].fval = Constants.TMP1_WALL;
			Variable.cave[yval + 1][xval].fval = Constants.TMP1_WALL;
			
			tmp = Rnd.randomInt(4);	/* Place a door	*/
			if (tmp < 3) {
				placeSecretDoor(yval - 3 + (tmp << 1), xval);
			} else {
				placeSecretDoor(yval, xval - 7 + (tmp << 1));
			}
			
			/* Place a treasure in the vault		*/
			Misc3.placeObject(yval, xval, false);
			/* Let's guard the treasure well.	*/
			vaultMonster(yval, xval, 2 + Rnd.randomInt(2));
			/* Traps naturally			*/
			vaultTrap(yval, xval, 4, 4, 1 + Rnd.randomInt(3));
			break;
			
		case 3:
			if (Rnd.randomInt(3) == 1) {
				Variable.cave[yval - 1][xval - 2].fval = Constants.TMP1_WALL;
				Variable.cave[yval + 1][xval - 2].fval = Constants.TMP1_WALL;
				Variable.cave[yval - 1][xval + 2].fval = Constants.TMP1_WALL;
				Variable.cave[yval + 1][xval + 2].fval = Constants.TMP1_WALL;
				Variable.cave[yval - 2][xval - 1].fval = Constants.TMP1_WALL;
				Variable.cave[yval - 2][xval + 1].fval = Constants.TMP1_WALL;
				Variable.cave[yval + 2][xval - 1].fval = Constants.TMP1_WALL;
				Variable.cave[yval + 2][xval + 1].fval = Constants.TMP1_WALL;
				if (Rnd.randomInt(3) == 1) {
					placeSecretDoor(yval, xval - 2);
					placeSecretDoor(yval, xval + 2);
					placeSecretDoor(yval - 2, xval);
					placeSecretDoor(yval + 2, xval);
				}
			} else if (Rnd.randomInt(3) == 1) {
				Variable.cave[yval][xval].fval = Constants.TMP1_WALL;
				Variable.cave[yval - 1][xval].fval = Constants.TMP1_WALL;
				Variable.cave[yval + 1][xval].fval = Constants.TMP1_WALL;
				Variable.cave[yval][xval - 1].fval = Constants.TMP1_WALL;
				Variable.cave[yval][xval + 1].fval = Constants.TMP1_WALL;
			} else if (Rnd.randomInt(3) == 1) {
				Variable.cave[yval][xval].fval = Constants.TMP1_WALL;
			}
			break;
			
		case 4:
			break;
		default:
			break;
		}
	}
	
	/* Constructs a tunnel between two points		*/
	private static void buildTunnel(int row1, int col1, int row2, int col2) {
		int tmp_row, tmp_col, i, j;
		CaveType c_ptr;
		CaveType d_ptr;
		Point[] tunstk = new Point[1000], wallstk = new Point[1000];
		Point tun_ptr;
		IntPointer row_dir = new IntPointer(), col_dir = new IntPointer();
		int tunindex, wallindex;
		boolean stop_flag, door_flag;
		int main_loop_count;
		int start_row, start_col;
		
		for (int k = 0; k < 1000; k++) {
			tunstk[k] = new Point();
			wallstk[k] = new Point();
		}
		
		/* Main procedure for Tunnel			*/
		/* Note: 9 is a temporary value		*/
		stop_flag = false;
		door_flag = false;
		tunindex = 0;
		wallindex = 0;
		main_loop_count = 0;
		start_row = row1;
		start_col = col1;
		getCorrectDirection(row_dir, col_dir, row1, col1, row2, col2);
		
		do {
			/* prevent infinite loops, just in case */
			main_loop_count++;
			if (main_loop_count > 2000) {
				stop_flag = true;
			}
			
			if (Rnd.randomInt(100) > Constants.DUN_TUN_CHG) {
				if (Rnd.randomInt(Constants.DUN_TUN_RND) == 1) {
					getRandomDirection(row_dir, col_dir);
				} else {
					getCorrectDirection(row_dir, col_dir, row1, col1, row2, col2);
				}
			}
			tmp_row = row1 + row_dir.value();
			tmp_col = col1 + col_dir.value();
			while (!Misc1.isInBounds(tmp_row, tmp_col)) {
				if (Rnd.randomInt(Constants.DUN_TUN_RND) == 1) {
					getRandomDirection(row_dir, col_dir);
				} else {
					getCorrectDirection(row_dir, col_dir, row1, col1, row2, col2);
				}
				tmp_row = row1 + row_dir.value();
				tmp_col = col1 + col_dir.value();
			}
			c_ptr = Variable.cave[tmp_row][tmp_col];
			switch (c_ptr.fval) {
			case Constants.NULL_WALL:
				row1 = tmp_row;
				col1 = tmp_col;
				if (tunindex < 1000) {
					tunstk[tunindex].y = row1;
					tunstk[tunindex].x = col1;
					tunindex++;
				}
				door_flag = false;
				break;
			case Constants.TMP2_WALL:
				/* do nothing */
				break;
			case Constants.GRANITE_WALL:
				row1 = tmp_row;
				col1 = tmp_col;
				if (wallindex < 1000) {
					wallstk[wallindex].y = row1;
					wallstk[wallindex].x = col1;
					wallindex++;
				}
				for (i = row1-1; i <= row1+1; i++) {
					for (j = col1-1; j <= col1+1; j++) {
						if (Misc1.isInBounds(i, j)) {
							d_ptr = Variable.cave[i][j];
							/* values 11 and 12 are impossible here, place_streamer
							   is never run before build_tunnel */
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
				row1 = tmp_row;
				col1 = tmp_col;
				if (!door_flag) {
					if (doorIndex < 100) {
						doorStack[doorIndex].y = row1;
						doorStack[doorIndex].x = col1;
						doorIndex++;
					}
					door_flag = true;
				}
				if (Rnd.randomInt(100) > Constants.DUN_TUN_CON) {
					/* make sure that tunnel has gone a reasonable distance
					   before stopping it, this helps prevent isolated rooms */
					tmp_row = row1 - start_row;
					if (tmp_row < 0) tmp_row = -tmp_row;
					tmp_col = col1 - start_col;
					if (tmp_col < 0) tmp_col = -tmp_col;
					if (tmp_row > 10 || tmp_col > 10) stop_flag = true;
				}
				break;
			default:  /* c_ptr->fval != NULL, TMP2, GRANITE, CORR */
				row1 = tmp_row;
				col1 = tmp_col;
				break;
			}
		} while (((row1 != row2) || (col1 != col2)) && !stop_flag);
		
		for (i = 0; i < tunindex; i++) {
			tun_ptr = tunstk[i];
			d_ptr = Variable.cave[tun_ptr.y][tun_ptr.x];
			d_ptr.fval = Constants.CORR_FLOOR;
		}
		for (i = 0; i < wallindex; i++) {
			c_ptr = Variable.cave[wallstk[i].y][wallstk[i].x];
			if (c_ptr.fval == Constants.TMP2_WALL) {
				if (Rnd.randomInt(100) < Constants.DUN_TUN_PEN) {
					placeDoor(wallstk[i].y, wallstk[i].x);
				} else {
					/* these have to be doorways to rooms */
					c_ptr.fval = Constants.CORR_FLOOR;
				}
			}
		}
	}
	
	private static boolean isNextTo(int y, int x) {
		boolean next;
		
		if (Misc1.isNextToCorridor(y, x) > 2) {
			if ((Variable.cave[y - 1][x].fval >= Constants.MIN_CAVE_WALL)
					&& (Variable.cave[y + 1][x].fval >= Constants.MIN_CAVE_WALL)) {
				next = true;
			} else if ((Variable.cave[y][x - 1].fval >= Constants.MIN_CAVE_WALL)
					&& (Variable.cave[y][x + 1].fval >= Constants.MIN_CAVE_WALL)) {
				next = true;
			} else {
				next = false;
			}
		} else {
			next = false;
		}
		return next;
	}
	
	/* Places door at y, x position if at least 2 walls found	*/
	private static void tryDoor(int y, int x) {
		if ((Variable.cave[y][x].fval == Constants.CORR_FLOOR) && (Rnd.randomInt(100) > Constants.DUN_TUN_JCT)  && isNextTo(y, x)) {
			placeDoor(y, x);
		}
	}
	
	/* Returns random co-ordinates				-RAK-	*/
	private static void newSpot(IntPointer y, IntPointer x) {
		int i, j;
		CaveType c_ptr;
		
		do {
			i = Rnd.randomInt(Variable.currHeight - 2);
			j = Rnd.randomInt(Variable.currWidth - 2);
			c_ptr = Variable.cave[i][j];
		} while (c_ptr.fval >= Constants.MIN_CLOSED_SPACE || (c_ptr.creatureIndex != 0) || (c_ptr.treasureIndex != 0));
		
		y.value(i);
		x.value(j);
	}
	
	/* Cave logic flow for generation of new dungeon		*/
	private static void generateCave() {
		int[][] room_map = new int[20][20];
		int i, j, k;
		int y1, x1, y2, x2, pick1, pick2, tmp;
		int row_rooms, col_rooms, alloc_level;
		int[] yloc = new int[400], xloc = new int[400];
		
		row_rooms = 2 * (Variable.currHeight / Constants.SCREEN_HEIGHT);
		col_rooms = 2 * (Variable.currWidth / Constants.SCREEN_WIDTH);
		for (i = 0; i < row_rooms; i++) {
			for (j = 0; j < col_rooms; j++) {
				room_map[i][j] = 0;
			}
		}
		k = Rnd.randomIntNormalized(Constants.DUN_ROO_MEA, 2);
		for (i = 0; i < k; i++) {
			room_map[Rnd.randomInt(row_rooms)-1][Rnd.randomInt(col_rooms)-1] = 1;
		}
		k = 0;
		for (i = 0; i < row_rooms; i++) {
			for (j = 0; j < col_rooms; j++) {
				if (room_map[i][j] != 0) {
					yloc[k] = i * (Constants.SCREEN_HEIGHT >> 1) + Constants.QUART_HEIGHT;
					xloc[k] = j * (Constants.SCREEN_WIDTH >> 1) + Constants.QUART_WIDTH;
					if (Variable.dungeonLevel > Rnd.randomInt(Constants.DUN_UNUSUAL)) {
						tmp = Rnd.randomInt(3);
						if (tmp == 1) {
							buildType1(yloc[k], xloc[k]);
						} else if (tmp == 2) {
							buildType2(yloc[k], xloc[k]);
						} else {
							buildType3(yloc[k], xloc[k]);
						}
					} else {
						buildRoom(yloc[k], xloc[k]);
					}
					k++;
				}
			}
		}
		
		for (i = 0; i < k; i++) {
			pick1 = Rnd.randomInt(k) - 1;
			pick2 = Rnd.randomInt(k) - 1;
			y1 = yloc[pick1];
			x1 = xloc[pick1];
			yloc[pick1] = yloc[pick2];
			xloc[pick1] = xloc[pick2];
			yloc[pick2] = y1;
			xloc[pick2] = x1;
		}
		doorIndex = 0;
		/* move zero entry to k, so that can call build_tunnel all k times */
		yloc[k] = yloc[0];
		xloc[k] = xloc[0];
		for (i = 0; i < k; i++) {
			y1 = yloc[i];
			x1 = xloc[i];
			y2 = yloc[i+1];
			x2 = xloc[i+1];
			buildTunnel(y2, x2, y1, x1);
		}
		fillCave(Constants.GRANITE_WALL);
		for (i = 0; i < Constants.DUN_STR_MAG; i++) {
			placeStreamer(Constants.MAGMA_WALL, Constants.DUN_STR_MC);
		}
		for (i = 0; i < Constants.DUN_STR_QUA; i++) {
			placeStreamer(Constants.QUARTZ_WALL, Constants.DUN_STR_QC);
		}
		placeBoundary();
		/* Place intersection doors	*/
		for (i = 0; i < doorIndex; i++) {
			tryDoor(doorStack[i].y, doorStack[i].x - 1);
			tryDoor(doorStack[i].y, doorStack[i].x + 1);
			tryDoor(doorStack[i].y - 1, doorStack[i].x);
			tryDoor(doorStack[i].y + 1, doorStack[i].x);
		}
		alloc_level = (Variable.dungeonLevel / 3);
		if (alloc_level < 2) {
			alloc_level = 2;
		} else if (alloc_level > 10) {
			alloc_level = 10;
		}
		placeStairs(2, Rnd.randomInt(2) + 2, 3);
		placeStairs(1, Rnd.randomInt(2), 3);
		/* Set up the character co-ords, used by alloc_monster, place_win_monster */
		IntPointer cr = new IntPointer(Player.y), cc = new IntPointer(Player.x);
		newSpot(cr, cc);
		Player.y = cr.value();
		Player.x = cc.value();
		Misc1.spawnMonster((Rnd.randomInt(8) + Constants.MIN_MALLOC_LEVEL + alloc_level), 0, true);
		Misc3.spawnObject(Sets.SET_CORR, 3, Rnd.randomInt(alloc_level));
		Misc3.spawnObject(Sets.SET_ROOM, 5, Rnd.randomIntNormalized(Constants.TREAS_ROOM_ALLOC, 3));
		Misc3.spawnObject(Sets.SET_FLOOR, 5, Rnd.randomIntNormalized(Constants.TREAS_ANY_ALLOC, 3));
		Misc3.spawnObject(Sets.SET_FLOOR, 4, Rnd.randomIntNormalized(Constants.TREAS_GOLD_ALLOC, 3));
		Misc3.spawnObject(Sets.SET_FLOOR, 1, Rnd.randomInt(alloc_level));
		if (Variable.dungeonLevel >= Constants.WIN_MON_APPEAR)  Misc1.placeWinMonster();
	}
	
	/* Builds a store at a row, column coordinate			*/
	private static void buildStore(int store_num, int y, int x) {
		int yval, y_height, y_depth;
		int xval, x_left, x_right;
		int i, j;
		int cur_pos, tmp;
		CaveType c_ptr;
		
		yval     = y * 10 + 5;
		xval     = x * 16 + 16;
		y_height = yval - Rnd.randomInt(3);
		y_depth  = yval + Rnd.randomInt(4);
		x_left   = xval - Rnd.randomInt(6);
		x_right  = xval + Rnd.randomInt(6);
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				Variable.cave[i][j].fval	= Constants.BOUNDARY_WALL;
			}
		}
		tmp = Rnd.randomInt(4);
		if (tmp < 3) {
			i = Rnd.randomInt(y_depth - y_height) + y_height - 1;
			if (tmp == 1) {
				j = x_left;
			} else {
				j = x_right;
			}
		} else {
			j = Rnd.randomInt(x_right - x_left) + x_left - 1;
			if (tmp == 3) {
				i = y_depth;
			} else {
				i = y_height;
			}
		}
		c_ptr = Variable.cave[i][j];
		c_ptr.fval = Constants.CORR_FLOOR;
		cur_pos = Misc1.popTreasure();
		c_ptr.treasureIndex = cur_pos;
		Desc.copyIntoInventory(Treasure.treasureList[cur_pos], Constants.OBJ_STORE_DOOR + store_num);
	}
	
	/* Link all free space in treasure list together		*/
	private static void clearTreasureList() {
		int i;
		
		for (i = 0; i < Constants.MAX_TALLOC; i++) {
			Desc.copyIntoInventory(Treasure.treasureList[i], Constants.OBJ_NOTHING);
		}
		Treasure.currTreasureIndex = Constants.MIN_TRIX;
	}
	
	/* Link all free space in monster list together			*/
	private static void clearMonsterList() {
		int i;
		
		for (i = 0; i < Constants.MAX_MALLOC; i++) {
			Monsters.monsterList[i] = Monsters.getBlankMonster();
		}
		Monsters.freeMonsterIndex = Constants.MIN_MONIX;
	}
	
	/* Town logic flow for generation of new town		*/
	private static void generateTown() {
		int i, j, k, l, m;
		CaveType c_ptr;
		int[] rooms = new int[6];
		
		Rnd.setSeed(Variable.townSeed);
		for (i = 0; i < 6; i++) {
			rooms[i] = i;
		}
		l = 6;
		for (i = 0; i < 2; i++) {
			for (j = 0; j < 3; j++) {
				k = Rnd.randomInt(l) - 1;
				buildStore(rooms[k], i, j);
				for (m = k; m < l - 1; m++) {
					rooms[m] = rooms[m + 1];
				}
				l--;
			}
		}
		fillCave(Constants.DARK_FLOOR);
		/* make stairs before reset_seed, so that they don't move around */
		placeBoundary();
		placeStairs(2, 1, 0);
		Rnd.resetSeed();
		/* Set up the character co-ords, used by alloc_monster below */
		IntPointer cr = new IntPointer(Player.y), cc = new IntPointer(Player.x);
		newSpot(cr, cc);
		Player.y = cr.value();
		Player.x = cc.value();
		if ((0x1 & (Variable.turn / 5000)) > 0) {
			/* Night	*/
			int ptr_count;
			for (i = 0; i < Variable.currHeight; i++) {
				ptr_count = 0;
				c_ptr = Variable.cave[i][ptr_count];
				for (j = 0; j < Variable.currWidth; j++) {
					if (c_ptr.fval != Constants.DARK_FLOOR) {
						c_ptr.permLight = true;
						ptr_count++;
						c_ptr = Variable.cave[i][ptr_count];
					}
				}
			}
			Misc1.spawnMonster(Constants.MIN_MALLOC_TN, 3, true);
		} else {
			/* Day	*/
			for (i = 0; i < Variable.currHeight; i++) {
				for (j = 0; j < Variable.currWidth; j++) {
					c_ptr = Variable.cave[i][j];
					c_ptr.permLight = true;
				}
			}
			Misc1.spawnMonster(Constants.MIN_MALLOC_TD, 3, true);
		}
		Store1.storeInventoryInit();
	}
	
	/* Generates a random dungeon level			-RAK-	*/
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
			Variable.maxPanelRows = (Variable.currHeight / Constants.SCREEN_HEIGHT) * 2 - 2;
			Variable.maxPanelCols = (Variable.currWidth / Constants.SCREEN_WIDTH) * 2 - 2;
			Variable.panelRow = Variable.maxPanelRows;
			Variable.panelCol = Variable.maxPanelCols;
			generateTown();
		} else {
			Variable.currHeight = Constants.MAX_HEIGHT;
			Variable.currWidth = Constants.MAX_WIDTH;
			Variable.maxPanelRows = (Variable.currHeight / Constants.SCREEN_HEIGHT) * 2 - 2;
			Variable.maxPanelCols = (Variable.currWidth / Constants.SCREEN_WIDTH) * 2 - 2;
			Variable.panelRow = Variable.maxPanelRows;
			Variable.panelCol = Variable.maxPanelCols;
			generateCave();
		}
	}
}
