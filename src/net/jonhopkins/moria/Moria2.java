/* 
 * Moria2.java: misc code, mainly handles player movement, t.inventory, etc
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
import net.jonhopkins.moria.types.InvenType;

public class Moria2 {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Monsters mon;
	private Moria1 mor1;
	private Moria3 mor3;
	private Player py;
	private Treasure t;
	private Variable var;
	
	private static Moria2 instance;
	private Moria2() { }
	public static Moria2 getInstance() {
		if (instance == null) {
			instance = new Moria2();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		mon = Monsters.getInstance();
		mor1 = Moria1.getInstance();
		mor3 = Moria3.getInstance();
		py = Player.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Change a trap from invisible to visible		-RAK-	*/
	/* Note: Secret doors are handled here				 */
	public void change_trap(int y, int x) {
		CaveType c_ptr;
		InvenType t_ptr;
		
		c_ptr = var.cave[y][x];
		t_ptr = t.t_list[c_ptr.tptr];
		if (t_ptr.tval == Constants.TV_INVIS_TRAP) {
			t_ptr.tval = Constants.TV_VIS_TRAP;
			mor1.lite_spot(y, x);
		} else if (t_ptr.tval == Constants.TV_SECRET_DOOR) {
			/* change secret door to closed door */
			t_ptr.index = Constants.OBJ_CLOSED_DOOR;
			t_ptr.tval = t.object_list[Constants.OBJ_CLOSED_DOOR].tval;
			t_ptr.tchar = (char)t.object_list[Constants.OBJ_CLOSED_DOOR].tchar;
			mor1.lite_spot(y, x);
		}
	}
	
	/* Searches for hidden things.			-RAK-	*/
	public void search(int y, int x, int chance) {
		int i, j;
		CaveType c_ptr;
		InvenType t_ptr;
		PlayerFlags p_ptr;
		String tmp_str, tmp_str2;
		
		p_ptr = py.py.flags;
		if (p_ptr.confused > 0) {
			chance = chance / 10;
		}
		if ((p_ptr.blind > 0) || mor1.no_light()) {
			chance = chance / 10;
		}
		if (p_ptr.image > 0) {
			chance = chance / 10;
		}
		for (i = (y - 1); i <= (y + 1); i++) {
			for (j = (x - 1); j <= (x + 1); j++) {
				if (m1.randint(100) < chance) {	/* always in_bounds here */
					c_ptr = var.cave[i][j];
					/* Search for hidden objects		   */
					if (c_ptr.tptr != 0) {
						t_ptr = t.t_list[c_ptr.tptr];
						/* Trap on floor?		       */
						if (t_ptr.tval == Constants.TV_INVIS_TRAP) {
							tmp_str2 = desc.objdes(t_ptr, true);
							tmp_str = String.format("You have found %s", tmp_str2);
							io.msg_print(tmp_str);
							change_trap(i, j);
							end_find();
						
						/* Secret door?		       */
						} else if (t_ptr.tval == Constants.TV_SECRET_DOOR) {
							io.msg_print("You have found a secret door.");
							change_trap(i, j);
							end_find();
						
						/* Chest is trapped?	       */
						} else if (t_ptr.tval == Constants.TV_CHEST) {
							/* mask out the treasure bits */
							if ((t_ptr.flags & Constants.CH_TRAPPED) > 1) {
								if (!desc.known2_p(t_ptr)) {
									desc.known2(t_ptr);
									io.msg_print("You have discovered a trap on the chest!");
								} else {
									io.msg_print("The chest is trapped!");
								}
							}
						}
					}
				}
			}
		}
	}
	
	/* The running algorithm:			-CJS-
	
	   Overview: You keep moving until something interesting happens.
	   If you are in an enclosed space, you follow corners. This is
	   the usual corridor scheme. If you are in an open space, you go
	   straight, but stop before entering enclosed space. This is
	   analogous to reaching doorways. If you have enclosed space on
	   one side only (that is, running along side a wall) stop if
	   your wall opens out, or your open space closes in. Either case
	   corresponds to a doorway.
	
	   What happens depends on what you can really SEE. (i.e. if you
	   have no light, then running along a dark corridor is JUST like
	   running in a dark room.) The algorithm works equally well in
	   corridors, rooms, mine tailings, earthquake rubble, etc, etc.
	
	   These conditions are kept in static memory:
		find_openarea	 You are in the open on at least one side.
		find_breakleft	 You have a wall on the left, and will stop if it opens
		find_breakright	 You have a wall on the right, and will stop if it opens
	
	   To initialize these conditions is the task of find_init. If
	   moving from the square marked @ to the square marked . (in the
	   two diagrams below), then two adjacent sqares on the left and
	   the right (L and R) are considered. If either one is seen to
	   be closed, then that side is considered to be closed. If both
	   sides are closed, then it is an enclosed (corridor) run.
	
		 LL		L
		@.	       L.R
		 RR	       @R
	
	   Looking at more than just the immediate squares is
	   significant. Consider the following case. A run along the
	   corridor will stop just before entering the center point,
	   because a choice is clearly established. Running in any of
	   three available directions will be defined as a corridor run.
	   Note that a minor hack is inserted to make the angled corridor
	   entry (with one side blocked near and the other side blocked
	   further away from the runner) work correctly. The runner moves
	   diagonally, but then saves the previous direction as being
	   straight into the gap. Otherwise, the tail end of the other
	   entry would be perceived as an alternative on the next move.
	
		   #.#
		  ##.##
		  .@...
		  ##.##
		   #.#
	
	   Likewise, a run along a wall, and then into a doorway (two
	   runs) will work correctly. A single run rightwards from @ will
	   stop at 1. Another run right and down will enter the corridor
	   and make the corner, stopping at the 2.
	
		#@	  1
		########### ######
		2	    #
		#############
		#
	
	   After any move, the function area_affect is called to
	   determine the new surroundings, and the direction of
	   subsequent moves. It takes a location (at which the runner has
	   just arrived) and the previous direction (from which the
	   runner is considered to have come). Moving one square in some
	   direction places you adjacent to three or five new squares
	   (for straight and diagonal moves) to which you were not
	   previously adjacent.
	
	       ...!	  ...	       EG Moving from 1 to 2.
	       .12!	  .1.!		  . means previously adjacent
	       ...!	  ..2!		  ! means newly adjacent
			   !!!
	
	   You STOP if you can't even make the move in the chosen
	   direction. You STOP if any of the new squares are interesting
	   in any way: usually containing monsters or treasure. You STOP
	   if any of the newly adjacent squares seem to be open, and you
	   are also looking for a break on that side. (i.e. find_openarea
	   AND find_break) You STOP if any of the newly adjacent squares
	   do NOT seem to be open and you are in an open area, and that
	   side was previously entirely open.
	
	   Corners: If you are not in the open (i.e. you are in a
	   corridor) and there is only one way to go in the new squares,
	   then turn in that direction. If there are more than two new
	   ways to go, STOP. If there are two ways to go, and those ways
	   are separated by a square which does not seem to be open, then
	   STOP.
	
	   Otherwise, we have a potential corner. There are two new open
	   squares, which are also adjacent. One of the new squares is
	   diagonally located, the other is straight on (as in the
	   diagram). We consider two more squares further out (marked
	   below as ?).
		  .X
		 @.?
		  #?
	   If they are both seen to be closed, then it is seen that no
	   benefit is gained from moving straight. It is a known corner.
	   To cut the corner, go diagonally, otherwise go straight, but
	   pretend you stepped diagonally into that next location for a
	   full view next time. Conversely, if one of the ? squares is
	   not seen to be closed, then there is a potential choice. We check
	   to see whether it is a potential corner or an intersection/room entrance.
	   If the square two spaces straight ahead, and the space marked with 'X'
	   are both blank, then it is a potential corner and enter if find_examine
	   is set, otherwise must stop because it is not a corner. */
	
	/* The cycle lists the directions in anticlockwise order, for	-CJS-
	 * over two complete cycles. The chome array maps a direction on
	 * to its position in the cycle.
	 */
	private static int cycle[] = { 1, 2, 3, 6, 9, 8, 7, 4, 1, 2, 3, 6, 9, 8, 7, 4, 1 };
	private int chome[] = { -1, 8, 9, 10, 7, -1, 11, 6, 5, 4 };
	private boolean find_openarea, find_breakright, find_breakleft;
	private int find_direction, find_prevdir; /* Keep a record of which way we are going. */
	
	public void find_init(int dir) {
		IntPointer row, col;
		int i;
		boolean deepleft, deepright;
		boolean shortleft, shortright;
		
		row = new IntPointer(py.char_row);
		col = new IntPointer(py.char_col);
		if (!m3.mmove(dir, row, col)) {
			var.find_flag = 0;
		} else {
			find_direction = dir;
			var.find_flag = 1;
			find_breakright = find_breakleft = false;
			find_prevdir = dir;
			if (py.py.flags.blind < 1) {
				i = chome[dir];
				deepleft = deepright = false;
				shortright = shortleft = false;
				if (see_wall(cycle[i + 1], py.char_row, py.char_col)) {
					find_breakleft = true;
					shortleft = true;
				} else if (see_wall(cycle[i + 1], row.value(), col.value())) {
					find_breakleft = true;
					deepleft = true;
				}
				if (see_wall(cycle[i - 1], py.char_row, py.char_col)) {
					find_breakright = true;
					shortright = true;
				} else if (see_wall(cycle[i - 1], row.value(), col.value())) {
					find_breakright = true;
					deepright = true;
				}
				if (find_breakleft && find_breakright) {
					find_openarea = false;
					if ((dir & 1) != 0) {	/* a hack to allow angled corridor entry */
						if (deepleft && !deepright) {
							find_prevdir = cycle[i - 1];
						} else if (deepright && !deepleft) {
							find_prevdir = cycle[i + 1];
						}
					
					/* else if there is a wall two spaces ahead and seem to be in a
					 * corridor, then force a turn into the side corridor, must
					 * be moving straight into a corridor here */
					} else if (see_wall(cycle[i], row.value(), col.value())) {
						if (shortleft && !shortright) {
							find_prevdir = cycle[i - 2];
						} else if (shortright && !shortleft) {
							find_prevdir = cycle[i + 2];
						}
					}
				} else {
					find_openarea = true;
				}
			}
		}
		
		/* We must erase the player symbol '@' here, because sub3_move_light()
		 * does not erase the previous location of the player when in find mode
		 * and when find_prself is false.  The player symbol is not draw at all
		 * in this case while moving, so the only problem is on the first turn
		 * of find mode, when the initial position of the character must be erased.
		 * Hence we must do the erasure here.  */
		if (! var.light_flag && ! var.find_prself.value()) {
			io.print(m1.loc_symbol(py.char_row, py.char_col), py.char_row, py.char_col);
		}
		
		mor3.move_char(dir, true);
		if (var.find_flag == 0) {
			var.command_count = 0;
		}
	}
	
	public void find_run() {
		/* prevent infinite loops in find mode, will stop after moving 100 times */
		if (var.find_flag++ > 100) {
			io.msg_print("You stop running to catch your breath.");
			end_find();
		} else {
			mor3.move_char(find_direction, true);
		}
	}
	
	/* Switch off the run flag - and get the light correct. -CJS- */
	public void end_find() {
		if (var.find_flag > 0) {
			var.find_flag = 0;
			mor1.move_light(py.char_row, py.char_col, py.char_row, py.char_col);
		}
	}
	
	/* Do we see a wall? Used in running.		-CJS- */
	public boolean see_wall(int dir, int y, int x) {
		char c;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		
		if (!m3.mmove(dir, y1, x1)) {	/* check to see if movement there possible */
			return true;
		} else if ((c = m1.loc_symbol(y1.value(), x1.value())) == var.wallsym || c == '%') {
			return true;
		} else {
			return false;
		}
	}
	
	/* Do we see anything? Used in running.		-CJS- */
	public boolean see_nothing(int dir, int y, int x) {
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		if (!m3.mmove(dir, y1, x1)) {	/* check to see if movement there possible */
			return false;
		} else if (m1.loc_symbol(y1.value(), x1.value()) == ' ') {
			return true;
		} else {
			return false;
		}
	}
	
	/* Determine the next direction for a run, or if we should stop.  -CJS- */
	public void area_affect(int dir, int y, int x) {
		int newdir, t1, check_dir = 0;
		boolean inv;
		IntPointer row = new IntPointer(), col = new IntPointer();
		int i, max, option, option2;
		CaveType c_ptr;
		
		if (py.py.flags.blind < 1) {
			option = 0;
			option2 = 0;
			dir = find_prevdir;
			max = (dir & 1) + 1;
			/* Look at every newly adjacent square. */
			for(i = -max; i <= max; i++) {
				newdir = cycle[chome[dir] + i];
				row.value(y);
				col.value(x);
				if (m3.mmove(newdir, row, col)) {
					/* Objects player can see (Including doors?) cause a stop. */
					c_ptr = var.cave[row.value()][col.value()];
					if (var.player_light || c_ptr.tl || c_ptr.pl || c_ptr.fm) {
						if (c_ptr.tptr != 0) {
							t1 = t.t_list[c_ptr.tptr].tval;
							if (t1 != Constants.TV_INVIS_TRAP && t1 != Constants.TV_SECRET_DOOR && (t1 != Constants.TV_OPEN_DOOR || !var.find_ignore_doors.value())) {
								end_find();
								return;
							}
						}
						/* Also Creatures		*/
						/* the monster should be visible since update_mon() checks
						 * for the special case of being in find mode */
						if (c_ptr.cptr > 1 && mon.m_list[c_ptr.cptr].ml) {
							end_find();
							return;
						}
						inv = false;
					} else {
						inv = true;	/* Square unseen. Treat as open. */
					}
					
					if (c_ptr.fval <= Constants.MAX_OPEN_SPACE || inv) {
						if (find_openarea) {
							/* Have we found a break? */
							if (i < 0) {
								if (find_breakright) {
									end_find();
									return;
								}
							} else if (i > 0) {
								if (find_breakleft) {
									end_find();
									return;
								}
							}
						} else if (option == 0) {
							option = newdir;	/* The first new direction. */
						} else if (option2 != 0) {
							end_find();	/* Three new directions. STOP. */
							return;
						} else if (option != cycle[chome[dir] + i - 1]) {
							end_find();	/* If not adjacent to prev, STOP */
							return;
						} else {
							/* Two adjacent choices. Make option2 the diagonal,
							 * and remember the other diagonal adjacent to the first
							 * option. */
							if ((newdir & 1) == 1) {
								check_dir = cycle[chome[dir] + i - 2];
								option2 = newdir;
							} else {
								check_dir = cycle[chome[dir] + i + 1];
								option2 = option;
								option = newdir;
							}
						}
					} else if (find_openarea) {
						/* We see an obstacle. In open area, STOP if on a side
						 * previously open. */
						if (i < 0) {
							if (find_breakleft) {
								end_find();
								return;
							}
							find_breakright = true;
						} else if (i > 0) {
							if (find_breakright) {
								end_find();
								return;
							}
							find_breakleft = true;
						}
					}
				}
			}
			
			if (!find_openarea) {	/* choose a direction. */
				if (option2 == 0 || (var.find_examine.value() && !var.find_cut.value())) {
					/* There is only one option, or if two, then we always examine
					 * potential corners and never cur known corners, so you step
					 * into the straight option. */
					if (option != 0) {
						find_direction = option;
					}
					if (option2 == 0) {
						find_prevdir = option;
					} else {
						find_prevdir = option2;
					}
				} else {
					/* Two options! */
					row.value(y);
					col.value(x);
					m3.mmove(option, row, col);
					if (!see_wall(option, row.value(), col.value()) || !see_wall(check_dir, row.value(), col.value())) {
						/* Don't see that it is closed off.  This could be a
						 * potential corner or an intersection. */
						if (var.find_examine.value() && see_nothing(option, row.value(), col.value()) && see_nothing(option2, row.value(), col.value())) {
							/* Can not see anything ahead and in the direction we are
							 * turning, assume that it is a potential corner. */
							find_direction = option;
							find_prevdir = option2;
						} else {
							/* STOP: we are next to an intersection or a room */
							end_find();
						}
					} else if (var.find_cut.value()) {
						/* This corner is seen to be enclosed; we cut the corner. */
						find_direction = option2;
						find_prevdir = option2;
					} else {
						/* This corner is seen to be enclosed, and we deliberately
						 * go the long way. */
						find_direction = option;
						find_prevdir = option2;
					}
				}
			}
		}
	}
	
	/* AC gets worse					-RAK-	*/
	/* Note: This routine affects magical AC bonuses so that stores	  */
	/*	 can detect the damage.					 */
	public boolean minus_ac(long typ_dam) {
		int i, j;
		int[] tmp = new int[6];
		boolean minus;
		InvenType i_ptr;
		String out_val, tmp_str;
		
		i = 0;
		if (t.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) {
			tmp[i] = Constants.INVEN_BODY;
			i++;
		}
		if (t.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) {
			tmp[i] = Constants.INVEN_ARM;
			i++;
		}
		if (t.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) {
			tmp[i] = Constants.INVEN_OUTER;
			i++;
		}
		if (t.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) {
			tmp[i] = Constants.INVEN_HANDS;
			i++;
		}
		if (t.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) {
			tmp[i] = Constants.INVEN_HEAD;
			i++;
		}
		/* also affect boots */
		if (t.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) {
			tmp[i] = Constants.INVEN_FEET;
			i++;
		}
		minus = false;
		if (i > 0) {
			j = tmp[m1.randint(i) - 1];
			i_ptr = t.inventory[j];
			if ((i_ptr.flags & typ_dam) != 0) {
				tmp_str = desc.objdes(t.inventory[j], false);
				out_val = String.format("Your %s resists damage!", tmp_str);
				io.msg_print(out_val);
				minus = true;
			} else if ((i_ptr.ac + i_ptr.toac) > 0) {
				tmp_str = desc.objdes(t.inventory[j], false);
				out_val = String.format("Your %s is damaged!", tmp_str);
				io.msg_print(out_val);
				i_ptr.toac--;
				mor1.calc_bonuses();
				minus = true;
			}
		}
		return minus;
	}
	
	/* Corrode the unsuspecting person's armor		 -RAK-	 */
	public void corrode_gas(String kb_str) {
		if (!minus_ac(Constants.TR_RES_ACID)) {
			mor1.take_hit(m1.randint(8), kb_str);
		}
		if (m3.inven_damage(Sets.set_corrodes, 5) > 0) {
			io.msg_print("There is an acrid smell coming from your pack.");
		}
	}
	
	/* Poison gas the idiot.				-RAK-	*/
	public void poison_gas(int dam, String kb_str) {
		mor1.take_hit(dam, kb_str);
		py.py.flags.poisoned += 12 + m1.randint(dam);
	}
	
	/* Burn the fool up.					-RAK-	*/
	public void fire_dam(int dam, String kb_str) {
		if (py.py.flags.fire_resist > 0) {
			dam = dam / 3;
		}
		if (py.py.flags.resist_heat > 0) {
			dam = dam / 3;
		}
		mor1.take_hit(dam, kb_str);
		if (m3.inven_damage(Sets.set_flammable, 3) > 0) {
			io.msg_print("There is smoke coming from your pack!");
		}
	}
	
	/* Freeze him to death.				-RAK-	*/
	public void cold_dam(int dam, String kb_str) {
		if (py.py.flags.cold_resist > 0) {
			dam = dam / 3;
		}
		if (py.py.flags.resist_cold > 0) {
			dam = dam / 3;
		}
		mor1.take_hit(dam, kb_str);
		if (m3.inven_damage(Sets.set_frost_destroy, 5) > 0) {
			io.msg_print("Something shatters inside your pack!");
		}
	}
	
	/* Lightning bolt the sucker away.			-RAK-	*/
	public void light_dam(int dam, String kb_str) {
		if (py.py.flags.lght_resist > 0) {
			mor1.take_hit((dam / 3), kb_str);
		} else {
			mor1.take_hit(dam, kb_str);
		}
		if (m3.inven_damage(Sets.set_lightning_destroy, 3) > 0) {
			io.msg_print("There are sparks coming from your pack!");
		}
	}
	
	/* Throw acid on the hapless victim			-RAK-	*/
	public void acid_dam(int dam, String kb_str) {
		int flag;
		
		flag = 0;
		if (minus_ac(Constants.TR_RES_ACID)) {
			flag = 1;
		}
		if (py.py.flags.acid_resist > 0) {
			flag += 2;
		}
		mor1.take_hit(dam / (flag + 1), kb_str);
		if (m3.inven_damage(Sets.set_acid_affect, 3) > 0) {
			io.msg_print("There is an acrid smell coming from your pack!");
		}
	}
}
