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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.MonsterType;

public class Misc1 {
	
	private Misc1() { }
	
	/**
	 * Gets a new random seed for the random number generator
	 * 
	 * @param seed - Used to seed the RNG
	 */
	public static void init_seeds(long seed) {
		long clock_var;
		
		if (seed == 0) {
			clock_var = java.util.Calendar.getInstance().getTimeInMillis();
		} else {
			clock_var = seed;
		}
		
		Variable.randes_seed = clock_var;
		
		clock_var += 8762;
		Variable.town_seed = clock_var;
		
		clock_var += 113452L;
		Rnd.set_rnd_seed(clock_var);
		/* make it a little more random */
		for (clock_var = randint(100); clock_var != 0; clock_var--) {
			Rnd.rnd();
		}
	}
	
	/* holds the previous rnd state */
	private static long old_seed;
	
	/**
	 * Change to different random number generator state
	 * 
	 * @param seed - Used to seed the RNG
	 */
	public static void set_seed(long seed) {
		old_seed = Rnd.get_rnd_seed();
		
		/* want reproducible state here */
		Rnd.set_rnd_seed(seed);
	}
	
	/**
	 * Restore the normal random generator state
	 */
	public static void reset_seed() {
		Rnd.set_rnd_seed(old_seed);
	}
	
	/**
	 * Check the day-time strings to see if open -RAK-
	 * 
	 * @return Always true, not using play-time hours anymore
	 */
	public static boolean check_time() {
		return true;
	}
	
	/**
	 * Generates a random integer x where 1<=X<=MAXVAL -RAK-
	 * 
	 * @param maxval - The maximum value to be returned
	 * @return A random integer x where 1 <= x <= maxval
	 */
	public static int randint(int maxval) {
		long randval;
		
		randval = Rnd.rnd();
		return ((int)(randval % maxval) + 1);
	}
	
	/**
	 * Generates a random integer number of NORMAL distribution -RAK-
	 * 
	 * @param mean - 
	 * @param stand - 
	 * @return 
	 */
	public static int randnor(int mean, int stand) {
		int tmp, offset, low, iindex, high;
		
		tmp = randint(Constants.MAX_SHORT);
		
		/* off scale, assign random value between 4 and 5 times SD */
		if (tmp == Constants.MAX_SHORT) {
			offset = 4 * stand + randint(stand);
			
			/* one half are negative */
			if (randint(2) == 1) {
				offset = -offset;
			}
			
			return mean + offset;
		}
		
		/* binary search normal normal_table to get index that matches tmp */
		/* this takes up to 8 iterations */
		low = 0;
		iindex = Constants.NORMAL_TABLE_SIZE >> 1;
		high = Constants.NORMAL_TABLE_SIZE;
		while (true) {
			if ((Tables.normal_table[iindex] == tmp) || (high == (low + 1))) {
				break;
			}
			
			if (Tables.normal_table[iindex] > tmp) {
				high = iindex;
				iindex = low + ((iindex - low) >> 1);
			} else {
				low = iindex;
				iindex = iindex + ((high - iindex) >> 1);
			}
		}
		
		/* might end up one below target, check that here */
		if (Tables.normal_table[iindex] < tmp) {
			iindex = iindex + 1;
		}
		
		/* normal_table is based on SD of 64, so adjust the index value here,
		 * round the half way case up */
		offset = ((stand * iindex) + (Constants.NORMAL_TABLE_SD >> 1)) / Constants.NORMAL_TABLE_SD;
		
		/* one half should be negative */
		if (randint(2) == 1) {
			offset = -offset;
		}
		
		return mean + offset;
	}
	
	/**
	 * Returns position of first set bit and clears that bit -RAK-
	 * 
	 * @param test - The integer being checked for its first set bit
	 * @return The position of the first set bit
	 */
	public static int bit_pos(IntPointer test) {
		int i;
		int mask = 0x1;
		
		for (i = 0; i < 32; i++) {
			if ((test.value() & mask) != 0) {
				test.value(test.value() & ~mask);
				return i;
			}
			mask <<= 1;
		}
		
		/* no one bits found */
		return -1;
	}
	
	/**
	 * Returns position of first set bit and clears that bit -RAK-
	 * 
	 * @param test - The long being checked for its first set bit
	 * @return The position of the first set bit
	 */
	public static int bit_pos(LongPointer test) {
		int i;
		long mask = 0x1;
		
		for (i = 0; i < 32; i++) {
			if ((test.value() & mask) != 0) {
				test.value(test.value() & ~mask);
				return i;
			}
			mask <<= 1;
		}
		
		/* no one bits found */
		return -1;
	}
	
	/**
	 * Checks a co-ordinate for in bounds status -RAK-
	 * 
	 * @param y - The vertical position of the point to check
	 * @param x - The horizontal position of the point to check
	 * @return True if the coordinate is within the level, otherwise false
	 */
	public static boolean in_bounds(int y, int x) {
		if ((y > 0) && (y < Variable.cur_height - 1) && (x > 0) && (x < Variable.cur_width - 1)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Calculates current boundaries -RAK-
	 */
	public static void panel_bounds() {
		Variable.panel_row_min = Variable.panel_row * (Constants.SCREEN_HEIGHT / 2);
		Variable.panel_row_max = Variable.panel_row_min + Constants.SCREEN_HEIGHT - 1;
		Variable.panel_row_prt = Variable.panel_row_min - 1;
		Variable.panel_col_min = Variable.panel_col * (Constants.SCREEN_WIDTH / 2);
		Variable.panel_col_max = Variable.panel_col_min + Constants.SCREEN_WIDTH - 1;
		Variable.panel_col_prt = Variable.panel_col_min - 13;
	}
	
	/**
	 * Given an row (y) and col (x), this routine detects when a move
	 * off the screen has occurred and figures new borders. -RAK-
	 * 
	 * @param y - The vertical position of the point to check
	 * @param x - The horizontal position of the point to check
	 * @param force - Force the panel bounds to be recalculated, useful for 'W'here.
	 * @return True if the panel bounds have been recalculated, otherwise false
	 */
	public static boolean get_panel(int y, int x, boolean force) {
		int prow, pcol;
		boolean panel;
		
		prow = Variable.panel_row;
		pcol = Variable.panel_col;
		if (force || (y < Variable.panel_row_min + 2) || (y > Variable.panel_row_max - 2)) {
			prow = ((y - Constants.SCREEN_HEIGHT / 4) / (Constants.SCREEN_HEIGHT / 2));
			if (prow > Variable.max_panel_rows) {
				prow = Variable.max_panel_rows;
			} else if (prow < 0) {
				prow = 0;
			}
		}
		if (force || (x < Variable.panel_col_min + 3) || (x > Variable.panel_col_max - 3)) {
			pcol = ((x - Constants.SCREEN_WIDTH / 4) / (Constants.SCREEN_WIDTH / 2));
			if (pcol > Variable.max_panel_cols) {
				pcol = Variable.max_panel_cols;
			} else if (pcol < 0) {
				pcol = 0;
			}
		}
		if ((prow != Variable.panel_row) || (pcol != Variable.panel_col)) {
			Variable.panel_row = prow;
			Variable.panel_col = pcol;
			panel_bounds();
			panel = true;
			/* stop movement if any */
			if (Variable.find_bound.value()) {
				Moria2.end_find();
			}
		} else {
			panel = false;
		}
		return panel;
	}
	
	/**
	 * Tests a given point to see if it is within the screen boundaries -RAK-
	 * 
	 * @param y - The vertical position of the point to check
	 * @param x - The horizontal position of the point to check
	 * @return Whether the point is within the screen
	 */
	public static boolean panel_contains(int y, int x) {
		if ((y >= Variable.panel_row_min) && (y <= Variable.panel_row_max) && (x >= Variable.panel_col_min) && (x <= Variable.panel_col_max)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Distance between two points -RAK-
	 * 
	 * @param y1 - The vertical position of the starting point
	 * @param x1 - The horizontal position of the starting point
	 * @param y2 - The vertical position of the ending point
	 * @param x2 - The horizontal position of the ending point
	 * @return The distance between the two points
	 */
	public static int distance(int y1, int x1, int y2, int x2) {
		int dy, dx;
		
		dy = y1 - y2;
		if (dy < 0) {
			dy = -dy;
		}
		dx = x1 - x2;
		if (dx < 0) {
			dx = -dx;
		}
		
		return ((((dy + dx) << 1) - (dy > dx ? dx : dy)) >> 1);
	}
	
	/**
	 * Checks points north, south, east, and west for a wall -RAK-
	 * <p>
	 * Note that (y, x) is always in_bounds(),
	 * i.e. (0 < y < cur_height - 1), and (0 < x < cur_width - 1)
	 * 
	 * @param y - The vertical position of the point to check
	 * @param x - The horizontal position of the point to check
	 * @return The number of walls adjacent to the point
	 */
	public static int next_to_walls(int y, int x) {
		int i;
		CaveType c_ptr;
		
		i = 0;
		c_ptr = Variable.cave[y - 1][x];
		if (c_ptr.fval >= Constants.MIN_CAVE_WALL) {
			i++;
		}
		c_ptr = Variable.cave[y + 1][x];
		if (c_ptr.fval >= Constants.MIN_CAVE_WALL) {
			i++;
		}
		c_ptr = Variable.cave[y][x - 1];
		if (c_ptr.fval >= Constants.MIN_CAVE_WALL) {
			i++;
		}
		c_ptr = Variable.cave[y][x + 1];
		if (c_ptr.fval >= Constants.MIN_CAVE_WALL) {
			i++;
		}
		
		return i;
	}
	
	/**
	 * Checks all adjacent spots for corridors -RAK-
	 * <p>
	 * Note that (y, x) is always in_bounds(), hence no need to check that
	 * (j, k) is in_bounds(); even if they are 0 or cur_x - 1, it still works
	 * 
	 * @param y - The vertical position of the point to check
	 * @param x - The horizontal position of the point to check
	 * @return The number of corridors adjacent to the point
	 */
	public static int next_to_corr(int y, int x) {
		int k, j, i;
		CaveType c_ptr;
		
		i = 0;
		for (j = y - 1; j <= (y + 1); j++) {
			for (k = x - 1; k <= (x + 1); k++) {
				c_ptr = Variable.cave[j][k];
				/* should fail if there is already a door present */
				if (c_ptr.fval == Constants.CORR_FLOOR && (c_ptr.tptr == 0 || Treasure.t_list[c_ptr.tptr].tval < Constants.TV_MIN_DOORS)) {
					i++;
				}
			}
		}
		return i;
	}
	
	/**
	 * Generates damage for 2d6 style dice rolls
	 *
	 * @param num - Number of times to roll the die
	 * @param sides - Number of sides on the die
	 * @return The total damage counted
	 */
	public static int damroll(int num, int sides) {
		int i, sum = 0;
		
		for (i = 0; i < num; i++) {
			sum += randint(sides);
		}
		return sum;
	}
	
	public static int pdamroll(int[] array) {
		return damroll(array[0], array[1]);
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
	 * @param fromY - The vertical position of the starting point
	 * @param fromX - The horizontal position of the starting point
	 * @param toY - The vertical position of the ending point
	 * @param toX - The horizontal position of the ending point
	 * @return Whether there is a clear line of sight from the starting point to the ending point
	 */
	public static boolean los(int fromY, int fromX, int toY, int toX) {
		int tmp, deltaX, deltaY;
		
		deltaX = toX - fromX;
		deltaY = toY - fromY;
		
		/* Adjacent? */
		if ((deltaX < 2) && (deltaX > -2) && (deltaY < 2) && (deltaY > -2)) {
			return true;
		}
		
		/* Handle the cases where deltaX or deltaY == 0. */
		if (deltaX == 0) {
			int p_y;	/* y position -- loop variable	*/
			
			if (deltaY < 0) {
				tmp = fromY;
				fromY = toY;
				toY = tmp;
			}
			for (p_y = fromY + 1; p_y < toY; p_y++) {
				if (Variable.cave[p_y][fromX].fval >= Constants.MIN_CLOSED_SPACE) {
					return false;
				}
			}
			return true;
		} else if (deltaY == 0) {
			int px;	/* x position -- loop variable	*/
			
			if (deltaX < 0) {
				tmp = fromX;
				fromX = toX;
				toX = tmp;
			}
			for (px = fromX + 1; px < toX; px++) {
				if (Variable.cave[fromY][px].fval >= Constants.MIN_CLOSED_SPACE) {
					return false;
				}
			}
			return true;
		}
		
		/* Now, we've eliminated all the degenerate cases.
		 * In the computations below, dy (or dx) and m are multiplied by a
		 * scale factor, scale = abs(deltaX * deltaY * 2), so that we can use
		 * integer arithmetic. */
		
		{
			int px,		/* x position				*/
			p_y,		/* y position				*/
			scale2;		/* above scale factor / 2	*/
			int scale,	/* above scale factor		*/
			xSign,		/* sign of deltaX			*/
			ySign,		/* sign of deltaY			*/
			m;			/* slope or 1/slope of LOS	*/
			
			scale2 = Math.abs(deltaX * deltaY);
			scale = scale2 << 1;
			xSign = (deltaX < 0) ? -1 : 1;
			ySign = (deltaY < 0) ? -1 : 1;
			
			/* Travel from one end of the line to the other, oriented along
			 * the longer axis. */
			
			if (Math.abs(deltaX) >= Math.abs(deltaY)) {
				int dy;		/* "fractional" y position	*/
				/* We start at the border between the first and second tiles,
				 * where the y offset = .5 * slope.  Remember the scale
				 * factor.  We have:
				 * 
				 * m = deltaY / deltaX * 2 * (deltaY * deltaX)
				 *   = 2 * deltaY * deltaY. */
				
				dy = deltaY * deltaY;
				m = dy << 1;
				px = fromX + xSign;
				
				/* Consider the special case where slope == 1. */
				if (dy == scale2) {
					p_y = fromY + ySign;
					dy -= scale;
				} else {
					p_y = fromY;
				}
				
				while (toX - px != 0) {
					if (Variable.cave[p_y][px].fval >= Constants.MIN_CLOSED_SPACE) {
						return false;
					}
					
					dy += m;
					if (dy < scale2) {
						px += xSign;
					} else if (dy > scale2) {
						p_y += ySign;
						if (Variable.cave[p_y][px].fval >= Constants.MIN_CLOSED_SPACE) {
							return false;
						}
						px += xSign;
						dy -= scale;
					} else {
						/* This is the case, dy == scale2, where the LOS
						 * exactly meets the corner of a tile. */
						px += xSign;
						p_y += ySign;
						dy -= scale;
					}
				}
				return true;
			} else {
				int dx;		/* "fractional" x position	*/
				dx = deltaX * deltaX;
				m = dx << 1;
				
				p_y = fromY + ySign;
				if (dx == scale2) {
					px = fromX + xSign;
					dx -= scale;
				} else {
					px = fromX;
				}
				
				while (toY - p_y != 0) {
					if (Variable.cave[p_y][px].fval >= Constants.MIN_CLOSED_SPACE) {
						return false;
					}
					dx += m;
					if (dx < scale2) {
						p_y += ySign;
					} else if (dx > scale2) {
						px += xSign;
						if (Variable.cave[p_y][px].fval >= Constants.MIN_CLOSED_SPACE) {
							return false;
						}
						p_y += ySign;
						dx -= scale;
					} else {
						px += xSign;
						p_y += ySign;
						dx -= scale;
					}
				}
				return true;
			}
		}
	}
	
	/**
	 * Returns symbol for given row, column -RAK-
	 * 
	 * @param y - The vertical position of the point to check
	 * @param x - The horizontal position of the point to check
	 * @return The symbol at the given point
	 */
	public static char loc_symbol(int y, int x) {
		CaveType cave_ptr;
		PlayerFlags f_ptr;
		
		cave_ptr = Variable.cave[y][x];
		f_ptr = Player.py.flags;
		
		if ((cave_ptr.cptr == 1) && (Variable.find_flag == 0 || !Variable.find_prself.value())) {
			return '@';
		} else if ((f_ptr.status & Constants.PY_BLIND) != 0) {
			return ' ';
		} else if ((f_ptr.image > 0) && (randint (12) == 1)) {
			return (char)(randint(95) + 31);
		} else if ((cave_ptr.cptr > 1) && (Monsters.m_list[cave_ptr.cptr].ml )) {
			return Monsters.c_list[Monsters.m_list[cave_ptr.cptr].mptr].cchar;
		} else if (!cave_ptr.pl && !cave_ptr.tl && !cave_ptr.fm) {
			return ' ';
		} else if ((cave_ptr.tptr != 0)
				&& (Treasure.t_list[cave_ptr.tptr].tval != Constants.TV_INVIS_TRAP)) {
			return Treasure.t_list[cave_ptr.tptr].tchar;
		} else if (cave_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
				return Variable.floorsym;
		} else if (cave_ptr.fval == Constants.GRANITE_WALL || cave_ptr.fval == Constants.BOUNDARY_WALL || !Variable.highlight_seams.value()) {
				return Variable.wallsym;
			//	return (char)240;
		} else {
			/* Originally set highlight bit, but that is not portable, now use
			 * the percent sign instead. */
			return '%';
		}
	}
	
	/**
	 * Tests a spot for light or field mark status -RAK-
	 * 
	 * @param y - The vertical position of the point to check
	 * @param x - The horizonal position of the point to check
	 * @return Whether the point is lit or field-marked
	 */
	public static boolean test_light(int y, int x) {
		CaveType cave_ptr;
		
		cave_ptr = Variable.cave[y][x];
		if (cave_ptr.pl  || cave_ptr.tl  || cave_ptr.fm ) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Prints the map of the dungeon -RAK-
	 */
	public static void prt_map() {
		int i, j, k;
		char tmp_char;
		
		k = 0;
		for (i = Variable.panel_row_min; i <= Variable.panel_row_max; i++) {	/* Top to bottom */
			k++;
			IO.erase_line(k, 13);
			for (j = Variable.panel_col_min; j <= Variable.panel_col_max; j++) {	/* Left to right */
				tmp_char = loc_symbol(i, j);
				if (tmp_char != ' ') {
					IO.print(tmp_char, i, j);
				}
			}
		}
	}
	
	/**
	 * Compact monsters -RAK-
	 * 
	 * @return True if any monsters were deleted, false if could not delete any monsters.
	 */
	public static boolean compact_monsters() {
		int i;
		int cur_dis;
		boolean delete_any;
		MonsterType mon_ptr;
		
		IO.msg_print("Compacting monsters...");
		
		cur_dis = 66;
		delete_any = false;
		do {
			for (i = Monsters.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
				mon_ptr = Monsters.m_list[i];
				if ((cur_dis < mon_ptr.cdis) && (randint(3) == 1)) {
					/* Never compact away the Balrog!! */
					if ((Monsters.c_list[mon_ptr.mptr].cmove & Constants.CM_WIN) != 0) {
						/* Do nothing */
						;
					/* in case this is called from within creatures(), this is a
					 * horrible hack, the m_list/creatures() code needs to be
					 * rewritten */
					} else if (Variable.hack_monptr < i) {
						Moria3.delete_monster(i);
						delete_any = true;
					} else {
						/* fix1_delete_monster() does not decrement mfptr, so
						 * don't set delete_any if this was called */
						Moria3.fix1_delete_monster(i);
					}
				}
			}
			if (!delete_any) {
				cur_dis -= 6;
				/* Can't delete any monsters, return failure.  */
				if (cur_dis < 0) {
					return false;
				}
			}
		} while (delete_any == false);
		return true;
	}
	
	/**
	 * Add to the player's food time -RAK-
	 * 
	 * @param num - Amount of food time to add
	 */
	public static void add_food(int num) {
		PlayerFlags p_ptr;
		int extra, penalty;
		
		p_ptr = Player.py.flags;
		if (p_ptr.food < 0)	p_ptr.food = 0;
		p_ptr.food += num;
		if (p_ptr.food > Constants.PLAYER_FOOD_MAX) {
			IO.msg_print("You are bloated from overeating.");
			
			/* Calculate how much of num is responsible for the bloating.
			 * Give the player food credit for 1/50, and slow him for that many
			 * turns also.  */
			extra = p_ptr.food - Constants.PLAYER_FOOD_MAX;
			if (extra > num) {
				extra = num;
			}
			penalty = extra / 50;
			
			p_ptr.slow += penalty;
			if (extra == num) {
				p_ptr.food = p_ptr.food - num + penalty;
			} else {
				p_ptr.food = Constants.PLAYER_FOOD_MAX + penalty;
			}
		} else if (p_ptr.food > Constants.PLAYER_FOOD_FULL) {
			IO.msg_print("You are full.");
		}
	}
	
	/**
	 * Returns a pointer to next free space -RAK-
	 * 
	 * @return The index of the first free space in the monster array, or -1 if could not allocate a monster.
	 */
	public static int popm() {
		if (Monsters.mfptr == Constants.MAX_MALLOC) {
			if (!compact_monsters()) {
				return -1;
			}
		}
		return (Monsters.mfptr++);
	}
	
	/**
	 * Gives Max hit points -RAK-
	 * 
	 * @param array - 
	 * @return Number of hitpoints
	 */
	public static int max_hp(int[] array) {
		return(array[0] * array[1]);
	}
	
	/**
	 * Places a monster at given location -RAK-
	 * 
	 * @param y - The vertical position at which to place the monster
	 * @param x - The horizontal position at which to place the monster
	 * @param z - mptr of the monster to allocate
	 * @param slp - Whether to make the monster sleep
	 * @return True if the monster was placed, otherwise false
	 */
	public static boolean place_monster(int y, int x, int z, boolean slp) {
		int cur_pos;
		MonsterType mon_ptr;
		
		cur_pos = popm();
		if (cur_pos == -1) {
			return false;
		}
		mon_ptr = Monsters.m_list[cur_pos];
		mon_ptr.fy = y;
		mon_ptr.fx = x;
		mon_ptr.mptr = z;
		if ((Monsters.c_list[z].cdefense & Constants.CD_MAX_HP) != 0) {
			mon_ptr.hp = max_hp(Monsters.c_list[z].hd);
		} else {
			mon_ptr.hp = pdamroll(Monsters.c_list[z].hd);
		}
		/* the c_list speed value is 10 greater, so that it can be a int8u */
		mon_ptr.cspeed = Monsters.c_list[z].speed - 10 + Player.py.flags.speed;
		mon_ptr.stunned = 0;
		mon_ptr.cdis = distance(Player.char_row, Player.char_col, y, x);
		mon_ptr.ml = false;
		Variable.cave[y][x].cptr = cur_pos;
		if (slp) {
			if (Monsters.c_list[z].sleep == 0) {
				mon_ptr.csleep = 0;
			} else {
				mon_ptr.csleep = (Monsters.c_list[z].sleep * 2) + randint(Monsters.c_list[z].sleep * 10);
			}
		} else {
			mon_ptr.csleep = 0;
		}
		return true;
	}
	
	/**
	 * Places a win monster on the map -RAK-
	 */
	public static void place_win_monster() {
		int y, x, cur_pos;
		MonsterType mon_ptr;
		
		if (!Variable.total_winner) {
			cur_pos = popm();
			/* Check for case where could not allocate space for the win monster,
			 * this should never happen.  */
			if (cur_pos == -1) {
				//abort();
				return;
			}
			mon_ptr = Monsters.m_list[cur_pos];
			do {
				y = randint(Variable.cur_height - 2);
				x = randint(Variable.cur_width - 2);
			} while ((Variable.cave[y][x].fval >= Constants.MIN_CLOSED_SPACE)
					|| (Variable.cave[y][x].cptr != 0)
					|| (Variable.cave[y][x].tptr != 0)
					|| (distance(y, x, Player.char_row, Player.char_col) <= Constants.MAX_SIGHT));
			mon_ptr.fy = y;
			mon_ptr.fx = x;
			mon_ptr.mptr = randint(Constants.WIN_MON_TOT) - 1 + Monsters.m_level[Constants.MAX_MONS_LEVEL];
			if ((Monsters.c_list[mon_ptr.mptr].cdefense & Constants.CD_MAX_HP) != 0) {
				mon_ptr.hp = max_hp(Monsters.c_list[mon_ptr.mptr].hd);
			} else {
				mon_ptr.hp = pdamroll(Monsters.c_list[mon_ptr.mptr].hd);
			}
			/* the c_list speed value is 10 greater, so that it can be a int8u */
			mon_ptr.cspeed = Monsters.c_list[mon_ptr.mptr].speed - 10 + Player.py.flags.speed;
			mon_ptr.stunned = 0;
			mon_ptr.cdis = distance(Player.char_row, Player.char_col, y, x);
			Variable.cave[y][x].cptr = cur_pos;
			mon_ptr.csleep = 0;
		}
	}
	
	/**
	 * Return a monster suitable to be placed at a given level. This makes
	 * high level monsters (up to the given level) slightly more common than
	 * low level monsters at any given level. -CJS-
	 *
	 * @param level - Level of the monster to place
	 * @return mptr of the monster to place
	 */
	public static int get_mons_num(int level) {
		int i, j, num;
		
		if (level == 0) {
			i = randint(Monsters.m_level[0]) - 1;
		} else {
			if (level > Constants.MAX_MONS_LEVEL) {
				level = Constants.MAX_MONS_LEVEL;
			}
			if (randint(Constants.MON_NASTY) == 1) {
				i = randnor (0, 4);
				level = level + Math.abs(i) + 1;
				if (level > Constants.MAX_MONS_LEVEL) {
					level = Constants.MAX_MONS_LEVEL;
				}
			} else {
				/* This code has been added to make it slightly more likely to
				 * get the higher level monsters. Originally a uniform
				 * distribution over all monsters of level less than or equal to the
				 * dungeon level. This distribution makes a level n monster occur
				 * approx 2/n% of the time on level n, and 1/n*n% are 1st level. */
				
				num = Monsters.m_level[level] - Monsters.m_level[0];
				i = randint(num) - 1;
				j = randint(num) - 1;
				if (j > i) {
					i = j;
				}
				level = Monsters.c_list[i + Monsters.m_level[0]].level;
			}
			i = randint(Monsters.m_level[level] - Monsters.m_level[level - 1]) - 1 + Monsters.m_level[level - 1];
		}
		return i;
	}
	
	/**
	 * Allocates a random monster -RAK-
	 * 
	 * @param num - Number of monsters to place
	 * @param dis - Minimum distance from the player to place monsters
	 * @param slp - Whether to make the monsters sleep
	 */
	public static void alloc_monster(int num, int dis, boolean slp) {
		int y, x, i;
		int l;
		
		for (i = 0; i < num; i++) {
			do {
				y = randint(Variable.cur_height - 2);
				x = randint(Variable.cur_width - 2);
			} while (Variable.cave[y][x].fval >= Constants.MIN_CLOSED_SPACE || (Variable.cave[y][x].cptr != 0) || (distance(y, x, Player.char_row, Player.char_col) <= dis));
			
			l = get_mons_num(Variable.dun_level);
			/* Dragons are always created sleeping here, so as to give the player a
			 * sporting chance.  */
			if (Monsters.c_list[l].cchar == 'd' || Monsters.c_list[l].cchar == 'D') {
				slp = true;
			}
			/* Place_monster() should always return TRUE here.  It does not
			 * matter if it fails though.  */
			place_monster(y, x, l, slp);
		}
	}
	
	/**
	 * Places creature adjacent to given location -RAK-
	 * 
	 * @param y - The vertical position of the summoner, stores the vertical position of the summoned monster
	 * @param x - The horizontal position of the summoner, stores the horizontal position of the summoned monster
	 * @param slp - Whether to make the monster sleep
	 * @return True if a monster was successfully summoned, otherwise false
	 */
	public static boolean summon_monster(IntPointer y, IntPointer x, boolean slp) {
		int i, j, k;
		int l;
		boolean summon;
		CaveType cave_ptr;
		
		i = 0;
		summon = false;
		l = get_mons_num(Variable.dun_level + Constants.MON_SUMMON_ADJ);
		do {
			j = y.value() - 2 + randint(3);
			k = x.value() - 2 + randint(3);
			if (in_bounds(j, k) ) {
				cave_ptr = Variable.cave[j][k];
				if (cave_ptr.fval <= Constants.MAX_OPEN_SPACE && (cave_ptr.cptr == 0)) {
					/* Place_monster() should always return TRUE here.  */
					if (!place_monster(j, k, l, slp)) {
						return false;
					}
					summon = true;
					i = 9;
					y.value(j);
					x.value(k);
				}
			}
			i++;
		} while (i <= 9);
		return summon;
	}
	
	/**
	 * Places undead adjacent to given location -RAK-
	 * 
	 * @param y - The vertical position of the summoner, stores the vertical position of the summoned undead
	 * @param x - The horizontal position of the summoner, stores the horizontal position of the summoned undead
	 * @return True if an undead was successfully summoned, otherwise false
	 */
	public static boolean summon_undead(IntPointer y, IntPointer x) {
		int i, j, k;
		int l, m, ctr;
		boolean summon;
		CaveType cave_ptr;
		
		i = 0;
		summon = false;
		l = Monsters.m_level[Constants.MAX_MONS_LEVEL];
		do {
			m = randint(l) - 1;
			ctr = 0;
			do {
				if ((Monsters.c_list[m].cdefense & Constants.CD_UNDEAD) != 0) {
					ctr = 20;
					l = 0;
				} else {
					m++;
					if (m > l) {
						ctr = 20;
					} else {
						ctr++;
					}
				}
			} while (ctr <= 19);
		} while(l != 0);
		
		do {
			j = y.value() - 2 + randint(3);
			k = x.value() - 2 + randint(3);
			if (in_bounds(j, k) ) {
				cave_ptr = Variable.cave[j][k];
				if (cave_ptr.fval <= Constants.MAX_OPEN_SPACE && (cave_ptr.cptr == 0)) {
					/* Place_monster() should always return TRUE here.  */
					if (!place_monster(j, k, m, false)) {
						return false;
					}
					summon = true;
					i = 9;
					y.value(j);
					x.value(k);
				}
			}
			i++;
		} while(i <= 9);
		return summon;
	}
	
	/**
	 * If too many objects on floor level, delete some of them -RAK-
	 */
	public static void compact_objects() {
		int i, j;
		int ctr, cur_dis, chance;
		CaveType cave_ptr;
		
		IO.msg_print("Compacting objects...");
		
		ctr = 0;
		cur_dis = 66;
		do {
			for (i = 0; i < Variable.cur_height; i++)
				for (j = 0; j < Variable.cur_width; j++) {
					cave_ptr = Variable.cave[i][j];
					if ((cave_ptr.tptr != 0) && (distance(i, j, Player.char_row, Player.char_col) > cur_dis)) {
						switch(Treasure.t_list[cave_ptr.tptr].tval)
						{
						case Constants.TV_VIS_TRAP:
							chance = 15;
							break;
						case Constants.TV_INVIS_TRAP:
						case Constants.TV_RUBBLE:
						case Constants.TV_OPEN_DOOR: case Constants.TV_CLOSED_DOOR:
							chance = 5;
							break;
						case Constants.TV_UP_STAIR: case Constants.TV_DOWN_STAIR:
						case Constants.TV_STORE_DOOR:
							/* stairs, don't delete them */
							/* shop doors, don't delete them */
							chance = 0;
							break;
						case Constants.TV_SECRET_DOOR: /* secret doors */
							chance = 3;
							break;
						default:
							chance = 10;
						}
						if (randint (100) <= chance) {
							Moria3.delete_object(i, j);
							ctr++;
						}
					}
				}
			if (ctr == 0)  cur_dis -= 6;
		} while (ctr <= 0);
		if (cur_dis < 66)  prt_map();
	}
	
	/**
	 * Gives pointer to next free space -RAK-
	 * 
	 * @return The index of the first free space in the treasure array
	 */
	public static int popt() {
		if (Treasure.tcptr == Constants.MAX_TALLOC) {
			compact_objects();
		}
		return (Treasure.tcptr++);
	}
	
	/**
	 * Pushes a record back onto free space list -RAK-
	 * <p>
	 * Delete_object() should always be called instead, unless the object in
	 * question is not in the dungeon, e.g. in Store1.java and Files.java
	 * 
	 * @param x - Location in the treasure array of the item to move
	 */
	public static void pusht(int x) {
		int i, j;
		
		if (x != Treasure.tcptr - 1) {
			Treasure.t_list[Treasure.tcptr - 1].copyInto(Treasure.t_list[x]);
			
			/* must change the tptr in the cave of the object just moved */
			for (i = 0; i < Variable.cur_height; i++) {
				for (j = 0; j < Variable.cur_width; j++) {
					if (Variable.cave[i][j].tptr == Treasure.tcptr - 1) {
						Variable.cave[i][j].tptr = x;
					}
				}
			}
		}
		Treasure.tcptr--;
		Desc.invcopy(Treasure.t_list[Treasure.tcptr], Constants.OBJ_NOTHING);
	}
	
	/**
	 * Boolean : is object enchanted -RAK-
	 * 
	 * @param chance - Percent chance that the object is enchanted
	 * @return Whether the object is enchanted
	 */
	public static boolean magik(int chance) {
		if (randint(100) <= chance) {
			return true;
		} else {
			return false;
		}
	}
	
	/* Enchant a bonus based on degree desired -RAK- */
	public static int m_bonus(int base, int max_std, int level) {
		int x, stand_dev, tmp;
		
		stand_dev = (Constants.OBJ_STD_ADJ * level / 100) + Constants.OBJ_STD_MIN;
		/* Check for level > max_std since that may have generated an overflow.  */
		if (stand_dev > max_std || level > max_std) {
			stand_dev = max_std;
		}
		/* abs may be a macro, don't call it with randnor as a parameter */
		tmp = randnor(0, stand_dev);
		x = (Math.abs(tmp) / 10) + base;
		if (x < base) {
			return base;
		} else {
			return x;
		}
	}
}
