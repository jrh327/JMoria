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
	private Point[] doorstk = new Point[100];
	private int doorindex;
	private Desc desc;
	private Misc1 m1;
	private Misc3 m3;
	private Monsters mon;
	private Moria3 mor3;
	private Player py;
	private Store1 store1;
	private Treasure t;
	private Variable var;
	
	private static Generate instance;
	private Generate() {
		for (int i = 0; i < 100; i++) {
			doorstk[i] = new Point();
		}
	}
	public static Generate getInstance() {
		if (instance == null) {
			instance = new Generate();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		mon = Monsters.getInstance();
		mor3 = Moria3.getInstance();
		py = Player.getInstance();
		store1 = Store1.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Always picks a correct direction		*/
	private void correct_dir(IntPointer rdir, IntPointer cdir, int y1, int x1, int y2, int x2) {
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
			if (m1.randint(2) == 1) {
				rdir.value(0);
			} else {
				cdir.value(0);
			}
		}
	}
	
	/* Chance of wandering direction			*/
	private void rand_dir(IntPointer rdir, IntPointer cdir) {
		int tmp;
		
		tmp = m1.randint(4);
		
		if (tmp < 3) {
			cdir.value(0);
			rdir.value(-3 + (tmp << 1)); /* tmp=1 -> *rdir=-1; tmp=2 -> *rdir=1 */
		} else {
			rdir.value(0);
			cdir.value(-7 + (tmp << 1)); /* tmp=3 -> *cdir=-1; tmp=4 -> *cdir=1 */
		}
	}
	
	/* Blanks out entire cave				-RAK-	*/
	private void blank_cave() {
		for (int i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (int j = 0; j < Constants.MAX_WIDTH; j++) {
				var.cave[i][j].cptr = 0;
				var.cave[i][j].tptr = 0;
				var.cave[i][j].fval = 0;
				var.cave[i][j].lr = false;
				var.cave[i][j].fm = false;
				var.cave[i][j].pl = false;
				var.cave[i][j].tl = false;
			}
		}
	}
	
	/* Fills in empty spots with desired rock		-RAK-	*/
	/* Note: 9 is a temporary value.				*/
	private void fill_cave(int fval) {
		int i, j;
		int ptr_count;
		CaveType c_ptr;
		
		/* no need to check the border of the cave */
		
		for (i = var.cur_height - 2; i > 0; i--) {
			ptr_count = 1;
			c_ptr = var.cave[i][ptr_count];
			for (j = var.cur_width - 2; j > 0; j--) {
				if ((c_ptr.fval == Constants.NULL_WALL) || (c_ptr.fval == Constants.TMP1_WALL) || (c_ptr.fval == Constants.TMP2_WALL)) {
					c_ptr.fval = fval;
				}
				ptr_count++;
				c_ptr = var.cave[i][ptr_count];
			}
		}
	}
	
	/* Places indestructible rock around edges of dungeon	-RAK-	*/
	private void place_boundary() {
		int i;
		CaveType top_ptr, bottom_ptr;
		CaveType left_ptr;
		CaveType right_ptr;
		
		/* put permanent wall on leftmost row and rightmost row */
		for (i = 0; i < var.cur_height; i++) {
			left_ptr = var.cave[i][0];
			left_ptr.fval	= Constants.BOUNDARY_WALL;
			right_ptr = var.cave[i][var.cur_width - 1];
			right_ptr.fval	= Constants.BOUNDARY_WALL;
		}
		
		/* put permanent wall on top row and bottom row */
		for (i = 0; i < var.cur_width; i++) {
			top_ptr = var.cave[0][i];
			top_ptr.fval	= Constants.BOUNDARY_WALL;
			bottom_ptr = var.cave[var.cur_height - 1][i];
			bottom_ptr.fval	= Constants.BOUNDARY_WALL;
		}
	}
	
	/* Places "streamers" of rock through dungeon		-RAK-	*/
	private void place_streamer(int fval, int treas_chance) {
		int i, tx, ty;
		IntPointer y, x;
		int t1, t2, dir;
		CaveType c_ptr;
		
		/* Choose starting point and direction		*/
		y = new IntPointer((var.cur_height / 2) + 11 - m1.randint(23));
		x = new IntPointer((var.cur_width / 2)  + 16 - m1.randint(33));
		
		dir = m1.randint(8);	/* Number 1-4, 6-9	*/
		if (dir > 4) {
			dir = dir + 1;
		}
		
		/* Place streamer into dungeon			*/
		t1 = 2 * Constants.DUN_STR_RNG + 1;	/* Constants	*/
		t2 =	 Constants.DUN_STR_RNG + 1;
		do {
			for (i = 0; i < Constants.DUN_STR_DEN; i++) {
				ty = y.value() + m1.randint(t1) - t2;
				tx = x.value() + m1.randint(t1) - t2;
				if (m1.in_bounds(ty, tx)) {
					c_ptr = var.cave[ty][tx];
					if (c_ptr.fval == Constants.GRANITE_WALL) {
						c_ptr.fval = fval;
						if (m1.randint(treas_chance) == 1) {
							m3.place_gold(ty, tx);
						}
					}
				}
			}
		} while (m3.mmove(dir, y, x));
	}
	
	private void place_open_door(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = m1.popt();
		cave_ptr = var.cave[y][x];
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_OPEN_DOOR);
		cave_ptr.fval = Constants.CORR_FLOOR;
	}
	
	private void place_broken_door(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = m1.popt();
		cave_ptr = var.cave[y][x];
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_OPEN_DOOR);
		cave_ptr.fval = Constants.CORR_FLOOR;
		t.t_list[cur_pos].p1 = 1;
	}
	
	private void place_closed_door(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = m1.popt();
		cave_ptr = var.cave[y][x];
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_CLOSED_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
	}
	
	private void place_locked_door(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = m1.popt();
		cave_ptr = var.cave[y][x];
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_CLOSED_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
		t.t_list[cur_pos].p1 = m1.randint(10) + 10;
	}
	
	private void place_stuck_door(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = m1.popt();
		cave_ptr = var.cave[y][x];
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_CLOSED_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
		t.t_list[cur_pos].p1 = -m1.randint(10) - 10;
	}
	
	private void place_secret_door(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = m1.popt();
		cave_ptr = var.cave[y][x];
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_SECRET_DOOR);
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
	}
	
	private void place_door(int y, int x) {
		int tmp;
		
		tmp = m1.randint(3);
		if (tmp == 1) {
			if (m1.randint(4) == 1) {
				place_broken_door(y, x);
			} else {
				place_open_door(y, x);
			}
		} else if (tmp == 2) {
			tmp = m1.randint(12);
			if (tmp > 3) {
				place_closed_door(y, x);
			} else if (tmp == 3) {
				place_stuck_door(y, x);
			} else {
				place_locked_door(y, x);
			}
		} else {
			place_secret_door(y, x);
		}
	}
	
	/* Place an up staircase at given y, x			-RAK-	*/
	private void place_up_stairs(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cave_ptr = var.cave[y][x];
		if (cave_ptr.tptr != 0) {
			mor3.delete_object(y, x);
		}
		cur_pos = m1.popt();
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_UP_STAIR);
	}
	
	/* Place a down staircase at given y, x			-RAK-	*/
	private void place_down_stairs(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cave_ptr = var.cave[y][x];
		if (cave_ptr.tptr != 0) {
			mor3.delete_object(y, x);
		}
		cur_pos = m1.popt();
		cave_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_DOWN_STAIR);
	}
	
	/* Places a staircase 1=up, 2=down			-RAK-	*/
	private void place_stairs(int typ, int num, int walls) {
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
					y1 = m1.randint(var.cur_height - 14);
					x1 = m1.randint(var.cur_width  - 14);
					y2 = y1 + 12;
					x2 = x1 + 12;
					do {
						do {
							cave_ptr = var.cave[y1][x1];
							if (cave_ptr.fval <= Constants.MAX_OPEN_SPACE && (cave_ptr.tptr == 0) && (m1.next_to_walls(y1, x1) >= walls)) {
								flag = true;
								if (typ == 1) {
									place_up_stairs(y1, x1);
								} else {
									place_down_stairs(y1, x1);
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
	private void vault_trap(int y, int x, int yd, int xd, int num) {
		int count, y1, x1;
		int i;
		boolean flag;
		CaveType c_ptr;
		
		for (i = 0; i < num; i++) {
			flag = false;
			count = 0;
			do {
				y1 = y - yd - 1 + m1.randint(2 * yd + 1);
				x1 = x - xd - 1 + m1.randint(2 * xd + 1);
				c_ptr = var.cave[y1][x1];
				if ((c_ptr.fval != Constants.NULL_WALL) && (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) && (c_ptr.tptr == 0)) {
					m3.place_trap(y1, x1, m1.randint(Constants.MAX_TRAP) - 1);
					flag = true;
				}
				count++;
			} while ((!flag) && (count <= 5));
		}
	}
	
	/* Place a trap with a given displacement of point	-RAK-	*/
	private void vault_monster(int y, int x, int num) {
		int i;
		IntPointer y1 = new IntPointer(), x1 = new IntPointer();
		
		for (i = 0; i < num; i++) {
			y1.value(y);
			x1.value(x);
			m1.summon_monster(y1, x1, true);
		}
	}
	
	/* Builds a room at a row, column coordinate		-RAK-	*/
	private void build_room(int yval, int xval) {
		int i, j, y_depth, x_right;
		int y_height, x_left;
		short floor;
		CaveType c_ptr, d_ptr;
		
		if (var.dun_level <= m1.randint(25)) {
			floor = Constants.LIGHT_FLOOR;	/* Floor with light	*/
		} else {
			floor = Constants.DARK_FLOOR;		/* Dark floor		*/
		}
		
		y_height = yval - m1.randint(4);
		y_depth  = yval + m1.randint(3);
		x_left   = xval - m1.randint(11);
		x_right  = xval + m1.randint(11);
		
		/* the x dim of rooms tends to be much larger than the y dim, so don't
		   bother rewriting the y loop */
		
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				c_ptr = var.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.lr = true;
			}
		}
		
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = var.cave[i][x_left - 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
			c_ptr = var.cave[i][x_right + 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
		}
		
		for (i = x_left; i <= x_right; i++) {
			c_ptr = var.cave[y_height - 1][i];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
			d_ptr = var.cave[y_depth + 1][i];
			d_ptr.fval = Constants.GRANITE_WALL;
			d_ptr.lr = true;
		}
	}
	
	/* Builds a room at a row, column coordinate		-RAK-	*/
	/* Type 1 unusual rooms are several overlapping rectangular ones	*/
	private void build_type1(int yval, int xval) {
		int y_height, y_depth;
		int x_left, x_right, limit;
		int i0, i, j;
		short floor;
		CaveType c_ptr, d_ptr;
		
		if (var.dun_level <= m1.randint(25)) {
			floor = Constants.LIGHT_FLOOR;	/* Floor with light	*/
		} else {
			floor = Constants.DARK_FLOOR;		/* Dark floor		*/
		}
		limit = 1 + m1.randint(2);
		for (i0 = 0; i0 < limit; i0++) {
			y_height = yval - m1.randint(4);
			y_depth  = yval + m1.randint(3);
			x_left   = xval - m1.randint(11);
			x_right  = xval + m1.randint(11);
			
			/* the x dim of rooms tends to be much larger than the y dim, so don't
			   bother rewriting the y loop */
			
			for (i = y_height; i <= y_depth; i++) {
				for (j = x_left; j <= x_right; j++) {
					c_ptr = var.cave[i][j];
					c_ptr.fval = floor;
					c_ptr.lr = true;
				}
			}
			for (i = (y_height - 1); i <= (y_depth + 1); i++) {
				c_ptr = var.cave[i][x_left - 1];
				if (c_ptr.fval != floor) {
					c_ptr.fval = Constants.GRANITE_WALL;
					c_ptr.lr = true;
				}
				c_ptr = var.cave[i][x_right + 1];
				if (c_ptr.fval != floor) {
					c_ptr.fval = Constants.GRANITE_WALL;
					c_ptr.lr = true;
				}
			}
			for (i = x_left; i <= x_right; i++) {
				c_ptr = var.cave[y_height - 1][i];
				if (c_ptr.fval != floor) {
					c_ptr.fval = Constants.GRANITE_WALL;
					c_ptr.lr = true;
				}
				d_ptr = var.cave[y_depth + 1][i];
				if (d_ptr.fval != floor) {
					d_ptr.fval = Constants.GRANITE_WALL;
					d_ptr.lr = true;
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
	private void build_type2(int yval, int xval) {
		int i, j, y_height, x_left;
		int y_depth, x_right, tmp;
		short floor;
		CaveType c_ptr, d_ptr;
		
		if (var.dun_level <= m1.randint(25)) {
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
				c_ptr = var.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.lr = true;
			}
		}
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = var.cave[i][x_left - 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
			c_ptr = var.cave[i][x_right + 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = var.cave[y_height - 1][i];
			c_ptr.fval  = Constants.GRANITE_WALL;
			c_ptr.lr = true;
			d_ptr = var.cave[y_depth + 1][i];
			d_ptr.fval = Constants.GRANITE_WALL;
			d_ptr.lr = true;
		}
		/* The inner room		*/
		y_height = y_height + 2;
		y_depth  = y_depth  - 2;
		x_left   = x_left   + 2;
		x_right  = x_right  - 2;
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			var.cave[i][x_left - 1].fval = Constants.TMP1_WALL;
			var.cave[i][x_right + 1].fval = Constants.TMP1_WALL;
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = var.cave[y_height - 1][i];
			c_ptr.fval = Constants.TMP1_WALL;
			d_ptr = var.cave[y_depth + 1][i];
			d_ptr.fval = Constants.TMP1_WALL;
		}
		/* Inner room variations		*/
		switch(m1.randint(5))
		{
			case 1:	/* Just an inner room.	*/
				tmp = m1.randint(4);
				if (tmp < 3) {	/* Place a door	*/
					if (tmp == 1) {
						place_secret_door(y_height - 1, xval);
					} else {
						place_secret_door(y_depth + 1, xval);
					}
				} else {
					if (tmp == 3) {
						place_secret_door(yval, x_left - 1);
					} else {
						place_secret_door(yval, x_right + 1);
					}
				}
				vault_monster(yval, xval, 1);
				break;
				
			case 2:	/* Treasure Vault	*/
				tmp = m1.randint(4);
				if (tmp < 3) {	/* Place a door	*/
					if (tmp == 1) {
						place_secret_door(y_height - 1, xval);
					} else {
						place_secret_door(y_depth + 1, xval);
					}
				} else {
					if (tmp == 3) {
						place_secret_door(yval, x_left - 1);
					} else {
						place_secret_door(yval, x_right + 1);
					}
				}
				
				for (i = yval - 1; i <= yval + 1; i++) {
					var.cave[i][xval - 1].fval = Constants.TMP1_WALL;
					var.cave[i][xval + 1].fval = Constants.TMP1_WALL;
				}
				var.cave[yval - 1][xval].fval = Constants.TMP1_WALL;
				var.cave[yval + 1][xval].fval = Constants.TMP1_WALL;
				
				tmp = m1.randint(4);	/* Place a door	*/
				if (tmp < 3) {
					place_locked_door(yval - 3 + (tmp << 1), xval); /* 1 -> yval-1; 2 -> yval+1*/
				} else {
					place_locked_door(yval, xval - 7 + (tmp << 1));
				}
				
				/* Place an object in the treasure vault	*/
				tmp = m1.randint(10);
				if (tmp > 2) {
					m3.place_object(yval, xval, false);
				} else if (tmp == 2) {
					place_down_stairs(yval, xval);
				} else {
					place_up_stairs(yval, xval);
				}
				
				/* Guard the treasure well		*/
				vault_monster(yval, xval, 2 + m1.randint(3));
				/* If the monsters don't get 'em.	*/
				vault_trap(yval, xval, 4, 10, 2 + m1.randint(3));
				break;
				
			case 3:	/* Inner pillar(s).	*/
				tmp = m1.randint(4);
				if (tmp < 3) {	/* Place a door	*/
					if (tmp == 1) {
						place_secret_door(y_height - 1, xval);
					} else {
						place_secret_door(y_depth + 1, xval);
					}
				} else {
					if (tmp == 3) {
						place_secret_door(yval, x_left - 1);
					} else {
						place_secret_door(yval, x_right + 1);
					}
				}
				
				for (i = yval - 1; i <= yval + 1; i++) {
					for (j = xval - 1; j <= xval + 1; j++) {
						c_ptr = var.cave[i][j];
						c_ptr.fval = Constants.TMP1_WALL;
					}
				}
				if (m1.randint(2) == 1) {
					tmp = m1.randint(2);
					for (i = yval - 1; i <= yval + 1; i++) {
						for (j = xval - 5 - tmp; j <= xval - 3 - tmp; j++) {
							c_ptr = var.cave[i][j];
							c_ptr.fval = Constants.TMP1_WALL;
						}
					}
					for (i = yval - 1; i <= yval + 1; i++) {
						for (j = xval + 3 + tmp; j <= xval + 5 + tmp; j++) {
							c_ptr = var.cave[i][j];
							c_ptr.fval = Constants.TMP1_WALL;
						}
					}
				}
				
				if (m1.randint(3) == 1) {	/* Inner rooms	*/
					for (i = xval - 5; i <= xval + 5; i++) {
						c_ptr = var.cave[yval - 1][i];
						c_ptr.fval = Constants.TMP1_WALL;
						d_ptr = var.cave[yval + 1][i];
						d_ptr.fval = Constants.TMP1_WALL;
					}
					var.cave[yval][xval - 5].fval = Constants.TMP1_WALL;
					var.cave[yval][xval + 5].fval = Constants.TMP1_WALL;
					place_secret_door(yval - 3 + (m1.randint(2) << 1), xval - 3);
					place_secret_door(yval - 3 + (m1.randint(2) << 1), xval + 3);
					if (m1.randint(3) == 1) {
						m3.place_object(yval, xval - 2, false);
					}
					if (m1.randint(3) == 1) {
						m3.place_object(yval, xval + 2, false);
					}
					vault_monster(yval, xval - 2, m1.randint(2));
					vault_monster(yval, xval + 2, m1.randint(2));
				}
				break;
				
			case 4:	/* Maze inside.	*/
				tmp = m1.randint(4);
				if (tmp < 3) {	/* Place a door	*/
					if (tmp == 1) {
						place_secret_door(y_height - 1, xval);
					} else {
						place_secret_door(y_depth + 1, xval);
					}
				} else {
					if (tmp == 3) {
						place_secret_door(yval, x_left - 1);
					} else {
						place_secret_door(yval, x_right + 1);
					}
				}
				
				for (i = y_height; i <= y_depth; i++) {
					for (j = x_left; j <= x_right; j++) {
						if ((0x1 & (j + i)) > 0) {
							var.cave[i][j].fval = Constants.TMP1_WALL;
						}
					}
				}
				
				/* Monsters just love mazes.		*/
				vault_monster(yval, xval - 5, m1.randint(3));
				vault_monster(yval, xval + 5, m1.randint(3));
				/* Traps make them entertaining.	*/
				vault_trap(yval, xval - 3, 2, 8, m1.randint(3));
				vault_trap(yval, xval + 3, 2, 8, m1.randint(3));
				/* Mazes should have some treasure too..	*/
				for (i = 0; i < 3; i++) {
					m3.random_object(yval, xval, 1);
				}
				break;
				
			case 5:	/* Four small rooms.	*/
				for (i = y_height; i <= y_depth; i++) {
					var.cave[i][xval].fval = Constants.TMP1_WALL;
				}
				
				for (i = x_left; i <= x_right; i++) {
					c_ptr = var.cave[yval][i];
					c_ptr.fval = Constants.TMP1_WALL;
				}
				
				if (m1.randint(2) == 1) {
					i = m1.randint(10);
					place_secret_door(y_height - 1, xval - i);
					place_secret_door(y_height - 1, xval + i);
					place_secret_door(y_depth + 1, xval - i);
					place_secret_door(y_depth + 1, xval + i);
				} else {
					i = m1.randint(3);
					place_secret_door(yval + i, x_left - 1);
					place_secret_door(yval - i, x_left - 1);
					place_secret_door(yval + i, x_right + 1);
					place_secret_door(yval - i, x_right + 1);
				}
				
				/* Treasure in each one.		*/
				m3.random_object(yval, xval, 2 + m1.randint(2));
				/* Gotta have some monsters.		*/
				vault_monster(yval + 2, xval - 4, m1.randint(2));
				vault_monster(yval + 2, xval + 4, m1.randint(2));
				vault_monster(yval - 2, xval - 4, m1.randint(2));
				vault_monster(yval - 2, xval + 4, m1.randint(2));
				break;
		}
	}
	
	/* Builds a room at a row, column coordinate		-RAK-	*/
	/* Type 3 unusual rooms are cross shaped				*/
	private void build_type3(int yval, int xval) {
		int y_height, y_depth;
		int x_left, x_right;
		int tmp, i, j;
		short floor;
		CaveType c_ptr;
		
		if (var.dun_level <= m1.randint(25)) {
			floor = Constants.LIGHT_FLOOR;	/* Floor with light	*/
		} else {
			floor = Constants.DARK_FLOOR;		/* Dark floor		*/
		}
		tmp = 2 + m1.randint(2);
		y_height = yval - tmp;
		y_depth  = yval + tmp;
		x_left   = xval - 1;
		x_right  = xval + 1;
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				c_ptr = var.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.lr = true;
			}
		}
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = var.cave[i][x_left-1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
			c_ptr = var.cave[i][x_right + 1];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = var.cave[y_height - 1][i];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
			c_ptr = var.cave[y_depth + 1][i];
			c_ptr.fval = Constants.GRANITE_WALL;
			c_ptr.lr = true;
		}
		tmp = 2 + m1.randint(9);
		y_height = yval - 1;
		y_depth  = yval + 1;
		x_left   = xval - tmp;
		x_right  = xval + tmp;
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				c_ptr = var.cave[i][j];
				c_ptr.fval = floor;
				c_ptr.lr = true;
			}
		}
		for (i = (y_height - 1); i <= (y_depth + 1); i++) {
			c_ptr = var.cave[i][x_left - 1];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.lr = true;
			}
			c_ptr = var.cave[i][x_right + 1];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.lr = true;
			}
		}
		for (i = x_left; i <= x_right; i++) {
			c_ptr = var.cave[y_height - 1][i];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.lr = true;
			}
			c_ptr = var.cave[y_depth + 1][i];
			if (c_ptr.fval != floor) {
				c_ptr.fval = Constants.GRANITE_WALL;
				c_ptr.lr = true;
			}
		}
		/* Special features.			*/
		switch(m1.randint(4))
		{
		case 1:	/* Large middle pillar		*/
			for (i = yval-1; i <= yval+1; i++) {
				for (j = xval - 1; j <= xval + 1; j++) {
					c_ptr = var.cave[i][j];
					c_ptr.fval = Constants.TMP1_WALL;
				}
			}
			break;
			
		case 2:	/* Inner treasure vault		*/
			for (i = yval - 1; i <= yval + 1; i++) {
				var.cave[i][xval - 1].fval = Constants.TMP1_WALL;
				var.cave[i][xval + 1].fval = Constants.TMP1_WALL;
			}
			var.cave[yval - 1][xval].fval = Constants.TMP1_WALL;
			var.cave[yval + 1][xval].fval = Constants.TMP1_WALL;
			
			tmp = m1.randint(4);	/* Place a door	*/
			if (tmp < 3) {
				place_secret_door(yval - 3 + (tmp << 1), xval);
			} else {
				place_secret_door(yval, xval - 7 + (tmp << 1));
			}
			
			/* Place a treasure in the vault		*/
			m3.place_object(yval, xval, false);
			/* Let's guard the treasure well.	*/
			vault_monster(yval, xval, 2 + m1.randint(2));
			/* Traps naturally			*/
			vault_trap(yval, xval, 4, 4, 1 + m1.randint(3));
			break;
			
		case 3:
			if (m1.randint(3) == 1) {
				var.cave[yval - 1][xval - 2].fval = Constants.TMP1_WALL;
				var.cave[yval + 1][xval - 2].fval = Constants.TMP1_WALL;
				var.cave[yval - 1][xval + 2].fval = Constants.TMP1_WALL;
				var.cave[yval + 1][xval + 2].fval = Constants.TMP1_WALL;
				var.cave[yval - 2][xval - 1].fval = Constants.TMP1_WALL;
				var.cave[yval - 2][xval + 1].fval = Constants.TMP1_WALL;
				var.cave[yval + 2][xval - 1].fval = Constants.TMP1_WALL;
				var.cave[yval + 2][xval + 1].fval = Constants.TMP1_WALL;
				if (m1.randint(3) == 1) {
					place_secret_door(yval, xval - 2);
					place_secret_door(yval, xval + 2);
					place_secret_door(yval - 2, xval);
					place_secret_door(yval + 2, xval);
				}
			} else if (m1.randint(3) == 1) {
				var.cave[yval][xval].fval = Constants.TMP1_WALL;
				var.cave[yval - 1][xval].fval = Constants.TMP1_WALL;
				var.cave[yval + 1][xval].fval = Constants.TMP1_WALL;
				var.cave[yval][xval - 1].fval = Constants.TMP1_WALL;
				var.cave[yval][xval + 1].fval = Constants.TMP1_WALL;
			} else if (m1.randint(3) == 1) {
				var.cave[yval][xval].fval = Constants.TMP1_WALL;
			}
			break;
			
		case 4:
			break;
		}
	}
	
	/* Constructs a tunnel between two points		*/
	private void build_tunnel(int row1, int col1, int row2, int col2) {
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
		correct_dir(row_dir, col_dir, row1, col1, row2, col2);
		
		do {
			/* prevent infinite loops, just in case */
			main_loop_count++;
			if (main_loop_count > 2000) {
				stop_flag = true;
			}
			
			if (m1.randint(100) > Constants.DUN_TUN_CHG) {
				if (m1.randint(Constants.DUN_TUN_RND) == 1) {
					rand_dir(row_dir, col_dir);
				} else {
					correct_dir(row_dir, col_dir, row1, col1, row2, col2);
				}
			}
			tmp_row = row1 + row_dir.value();
			tmp_col = col1 + col_dir.value();
			while (!m1.in_bounds(tmp_row, tmp_col)) {
				if (m1.randint(Constants.DUN_TUN_RND) == 1) {
					rand_dir(row_dir, col_dir);
				} else {
					correct_dir(row_dir, col_dir, row1, col1, row2, col2);
				}
				tmp_row = row1 + row_dir.value();
				tmp_col = col1 + col_dir.value();
			}
			c_ptr = var.cave[tmp_row][tmp_col];
			if (c_ptr.fval == Constants.NULL_WALL) {
				row1 = tmp_row;
				col1 = tmp_col;
				if (tunindex < 1000) {
					tunstk[tunindex].y = row1;
					tunstk[tunindex].x = col1;
					tunindex++;
				}
				door_flag = false;
			} else if (c_ptr.fval == Constants.TMP2_WALL) {
				/* do nothing */
				;
			} else if (c_ptr.fval == Constants.GRANITE_WALL) {
				row1 = tmp_row;
				col1 = tmp_col;
				if (wallindex < 1000) {
					wallstk[wallindex].y = row1;
					wallstk[wallindex].x = col1;
					wallindex++;
				}
				for (i = row1-1; i <= row1+1; i++) {
					for (j = col1-1; j <= col1+1; j++) {
						if (m1.in_bounds(i, j)) {
							d_ptr = var.cave[i][j];
							/* values 11 and 12 are impossible here, place_streamer
							   is never run before build_tunnel */
							if (d_ptr.fval == Constants.GRANITE_WALL) {
								d_ptr.fval = Constants.TMP2_WALL;
							}
						}
					}
				}
			} else if (c_ptr.fval == Constants.CORR_FLOOR || c_ptr.fval == Constants.BLOCKED_FLOOR) {
				row1 = tmp_row;
				col1 = tmp_col;
				if (!door_flag) {
					if (doorindex < 100) {
						doorstk[doorindex].y = row1;
						doorstk[doorindex].x = col1;
						doorindex++;
					}
					door_flag = true;
				}
				if (m1.randint(100) > Constants.DUN_TUN_CON) {
					/* make sure that tunnel has gone a reasonable distance
					   before stopping it, this helps prevent isolated rooms */
					tmp_row = row1 - start_row;
					if (tmp_row < 0) tmp_row = -tmp_row;
					tmp_col = col1 - start_col;
					if (tmp_col < 0) tmp_col = -tmp_col;
					if (tmp_row > 10 || tmp_col > 10) stop_flag = true;
				}
			} else {  /* c_ptr->fval != NULL, TMP2, GRANITE, CORR */
				row1 = tmp_row;
				col1 = tmp_col;
			}
		} while (((row1 != row2) || (col1 != col2)) && (stop_flag == false));
		
		for (i = 0; i < tunindex; i++) {
			tun_ptr = tunstk[i];
			d_ptr = var.cave[tun_ptr.y][tun_ptr.x];
			d_ptr.fval = Constants.CORR_FLOOR;
		}
		for (i = 0; i < wallindex; i++) {
			c_ptr = var.cave[wallstk[i].y][wallstk[i].x];
			if (c_ptr.fval == Constants.TMP2_WALL) {
				if (m1.randint(100) < Constants.DUN_TUN_PEN) {
					place_door(wallstk[i].y, wallstk[i].x);
				} else {
					/* these have to be doorways to rooms */
					c_ptr.fval = Constants.CORR_FLOOR;
				}
			}
		}
	}
	
	private boolean next_to(int y, int x) {
		boolean next;
		
		if (m1.next_to_corr(y, x) > 2) {
			if ((var.cave[y - 1][x].fval >= Constants.MIN_CAVE_WALL)
					&& (var.cave[y + 1][x].fval >= Constants.MIN_CAVE_WALL)) {
				next = true;
			} else if ((var.cave[y][x - 1].fval >= Constants.MIN_CAVE_WALL)
					&& (var.cave[y][x + 1].fval >= Constants.MIN_CAVE_WALL)) {
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
	private void try_door(int y, int x) {
		if ((var.cave[y][x].fval == Constants.CORR_FLOOR) && (m1.randint(100) > Constants.DUN_TUN_JCT)  && next_to(y, x)) {
			place_door(y, x);
		}
	}
	
	/* Returns random co-ordinates				-RAK-	*/
	private void new_spot(IntPointer y, IntPointer x) {
		int i, j;
		CaveType c_ptr;
		
		do {
			i = m1.randint(var.cur_height - 2);
			j = m1.randint(var.cur_width - 2);
			c_ptr = var.cave[i][j];
		} while (c_ptr.fval >= Constants.MIN_CLOSED_SPACE || (c_ptr.cptr != 0) || (c_ptr.tptr != 0));
		
		y.value(i);
		x.value(j);
	}
	
	/* Cave logic flow for generation of new dungeon		*/
	private void cave_gen() {
		int[][] room_map = new int[20][20];
		int i, j, k;
		int y1, x1, y2, x2, pick1, pick2, tmp;
		int row_rooms, col_rooms, alloc_level;
		int[] yloc = new int[400], xloc = new int[400];
		
		row_rooms = 2 * (var.cur_height / Constants.SCREEN_HEIGHT);
		col_rooms = 2 * (var.cur_width / Constants.SCREEN_WIDTH);
		for (i = 0; i < row_rooms; i++) {
			for (j = 0; j < col_rooms; j++) {
				room_map[i][j] = 0;
			}
		}
		k = m1.randnor(Constants.DUN_ROO_MEA, 2);
		for (i = 0; i < k; i++) {
			room_map[m1.randint(row_rooms)-1][m1.randint(col_rooms)-1] = 1;
		}
		k = 0;
		for (i = 0; i < row_rooms; i++) {
			for (j = 0; j < col_rooms; j++) {
				if (room_map[i][j] != 0) {
					yloc[k] = i * (Constants.SCREEN_HEIGHT >> 1) + Constants.QUART_HEIGHT;
					xloc[k] = j * (Constants.SCREEN_WIDTH >> 1) + Constants.QUART_WIDTH;
					if (var.dun_level > m1.randint(Constants.DUN_UNUSUAL)) {
						tmp = m1.randint(3);
						if (tmp == 1) {
							build_type1(yloc[k], xloc[k]);
						} else if (tmp == 2) {
							build_type2(yloc[k], xloc[k]);
						} else {
							build_type3(yloc[k], xloc[k]);
						}
					} else {
						build_room(yloc[k], xloc[k]);
					}
					k++;
				}
			}
		}
		
		for (i = 0; i < k; i++) {
			pick1 = m1.randint(k) - 1;
			pick2 = m1.randint(k) - 1;
			y1 = yloc[pick1];
			x1 = xloc[pick1];
			yloc[pick1] = yloc[pick2];
			xloc[pick1] = xloc[pick2];
			yloc[pick2] = y1;
			xloc[pick2] = x1;
		}
		doorindex = 0;
		/* move zero entry to k, so that can call build_tunnel all k times */
		yloc[k] = yloc[0];
		xloc[k] = xloc[0];
		for (i = 0; i < k; i++) {
			y1 = yloc[i];
			x1 = xloc[i];
			y2 = yloc[i+1];
			x2 = xloc[i+1];
			build_tunnel(y2, x2, y1, x1);
		}
		fill_cave(Constants.GRANITE_WALL);
		for (i = 0; i < Constants.DUN_STR_MAG; i++) {
			place_streamer(Constants.MAGMA_WALL, Constants.DUN_STR_MC);
		}
		for (i = 0; i < Constants.DUN_STR_QUA; i++) {
			place_streamer(Constants.QUARTZ_WALL, Constants.DUN_STR_QC);
		}
		place_boundary();
		/* Place intersection doors	*/
		for (i = 0; i < doorindex; i++) {
			try_door(doorstk[i].y, doorstk[i].x - 1);
			try_door(doorstk[i].y, doorstk[i].x + 1);
			try_door(doorstk[i].y - 1, doorstk[i].x);
			try_door(doorstk[i].y + 1, doorstk[i].x);
		}
		alloc_level = (var.dun_level / 3);
		if (alloc_level < 2) {
			alloc_level = 2;
		} else if (alloc_level > 10) {
			alloc_level = 10;
		}
		place_stairs(2, m1.randint(2) + 2, 3);
		place_stairs(1, m1.randint(2), 3);
		/* Set up the character co-ords, used by alloc_monster, place_win_monster */
		IntPointer cr = new IntPointer(py.char_row), cc = new IntPointer(py.char_col);
		new_spot(cr, cc);
		py.char_row = cr.value();
		py.char_col = cc.value();
		m1.alloc_monster((m1.randint(8) + Constants.MIN_MALLOC_LEVEL + alloc_level), 0, true);
		m3.alloc_object(Sets.set_corr, 3, m1.randint(alloc_level));
		m3.alloc_object(Sets.set_room, 5, m1.randnor(Constants.TREAS_ROOM_ALLOC, 3));
		m3.alloc_object(Sets.set_floor, 5, m1.randnor(Constants.TREAS_ANY_ALLOC, 3));
		m3.alloc_object(Sets.set_floor, 4, m1.randnor(Constants.TREAS_GOLD_ALLOC, 3));
		m3.alloc_object(Sets.set_floor, 1, m1.randint(alloc_level));
		if (var.dun_level >= Constants.WIN_MON_APPEAR)  m1.place_win_monster();
	}
	
	/* Builds a store at a row, column coordinate			*/
	private void build_store(int store_num, int y, int x) {
		int yval, y_height, y_depth;
		int xval, x_left, x_right;
		int i, j;
		int cur_pos, tmp;
		CaveType c_ptr;
		
		yval     = y * 10 + 5;
		xval     = x * 16 + 16;
		y_height = yval - m1.randint(3);
		y_depth  = yval + m1.randint(4);
		x_left   = xval - m1.randint(6);
		x_right  = xval + m1.randint(6);
		for (i = y_height; i <= y_depth; i++) {
			for (j = x_left; j <= x_right; j++) {
				var.cave[i][j].fval	= Constants.BOUNDARY_WALL;
			}
		}
		tmp = m1.randint(4);
		if (tmp < 3) {
			i = m1.randint(y_depth - y_height) + y_height - 1;
			if (tmp == 1) {
				j = x_left;
			} else {
				j = x_right;
			}
		} else {
			j = m1.randint(x_right - x_left) + x_left - 1;
			if (tmp == 3) {
				i = y_depth;
			} else {
				i = y_height;
			}
		}
		c_ptr = var.cave[i][j];
		c_ptr.fval = Constants.CORR_FLOOR;
		cur_pos = m1.popt();
		c_ptr.tptr = cur_pos;
		desc.invcopy(t.t_list[cur_pos], Constants.OBJ_STORE_DOOR + store_num);
	}
	
	/* Link all free space in treasure list together		*/
	private void tlink() {
		int i;
		
		for (i = 0; i < Constants.MAX_TALLOC; i++) {
			desc.invcopy(t.t_list[i], Constants.OBJ_NOTHING);
		}
		t.tcptr = Constants.MIN_TRIX;
	}
	
	/* Link all free space in monster list together			*/
	private void mlink() {
		int i;
		
		for (i = 0; i < Constants.MAX_MALLOC; i++) {
			mon.m_list[i] = mon.blank_monster();
		}
		mon.mfptr = Constants.MIN_MONIX;
	}
	
	/* Town logic flow for generation of new town		*/
	private void town_gen() {
		int i, j, k, l, m;
		CaveType c_ptr;
		int[] rooms = new int[6];
		
		m1.set_seed(var.town_seed);
		for (i = 0; i < 6; i++) {
			rooms[i] = i;
		}
		l = 6;
		for (i = 0; i < 2; i++) {
			for (j = 0; j < 3; j++) {
				k = m1.randint(l) - 1;
				build_store(rooms[k], i, j);
				for (m = k; m < l - 1; m++)
					rooms[m] = rooms[m + 1];
				l--;
			}
		}
		fill_cave(Constants.DARK_FLOOR);
		/* make stairs before reset_seed, so that they don't move around */
		place_boundary();
		place_stairs(2, 1, 0);
		m1.reset_seed();
		/* Set up the character co-ords, used by alloc_monster below */
		IntPointer cr = new IntPointer(py.char_row), cc = new IntPointer(py.char_col);
		new_spot(cr, cc);
		py.char_row = cr.value();
		py.char_col = cc.value();
		if ((0x1 & (var.turn / 5000)) > 0) {
			/* Night	*/
			int ptr_count;
			for (i = 0; i < var.cur_height; i++) {
				ptr_count = 0;
				c_ptr = var.cave[i][ptr_count];
				for (j = 0; j < var.cur_width; j++) {
					if (c_ptr.fval != Constants.DARK_FLOOR) {
						c_ptr.pl = true;
						ptr_count++;
						c_ptr = var.cave[i][ptr_count];
					}
				}
			}
			m1.alloc_monster(Constants.MIN_MALLOC_TN, 3, true);
		} else {
			/* Day	*/
			for (i = 0; i < var.cur_height; i++) {
				for (j = 0; j < var.cur_width; j++) {
					c_ptr = var.cave[i][j];
					c_ptr.pl = true;
				}
			}
			m1.alloc_monster(Constants.MIN_MALLOC_TD, 3, true);
		}
		store1.store_maint();
	}
	
	/* Generates a random dungeon level			-RAK-	*/
	public void generate_cave() {
		var.panel_row_min = 0;
		var.panel_row_max = 0;
		var.panel_col_min = 0;
		var.panel_col_max = 0;
		py.char_row = -1;
		py.char_col = -1;
		
		tlink();
		mlink();
		blank_cave();
		
		if (var.dun_level == 0) {
			var.cur_height = Constants.SCREEN_HEIGHT;
			var.cur_width	 = Constants.SCREEN_WIDTH;
			var.max_panel_rows = (var.cur_height / Constants.SCREEN_HEIGHT) * 2 - 2;
			var.max_panel_cols = (var.cur_width / Constants.SCREEN_WIDTH) * 2 - 2;
			var.panel_row = var.max_panel_rows;
			var.panel_col = var.max_panel_cols;
			town_gen();
		} else {
			var.cur_height = Constants.MAX_HEIGHT;
			var.cur_width = Constants.MAX_WIDTH;
			var.max_panel_rows = (var.cur_height / Constants.SCREEN_HEIGHT) * 2 - 2;
			var.max_panel_cols = (var.cur_width / Constants.SCREEN_WIDTH) * 2 - 2;
			var.panel_row = var.max_panel_rows;
			var.panel_col = var.max_panel_cols;
			cave_gen();
		}
	}
}
