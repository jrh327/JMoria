/*
 * Moria4.java: misc code, mainly to handle player commands
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

import net.jonhopkins.moria.types.BooleanPointer;
import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.MonsterType;

public class Moria4 {
	
	/* Tunnels through rubble and walls			-RAK-	*/
	/* Must take into account: secret doors,  special tools		  */
	
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Monsters mon;
	private Moria1 mor1;
	private Moria2 mor2;
	private Moria3 mor3;
	private Player py;
	private Recall recall;
	private Treasure t;
	private Variable var;
	
	private static Moria4 instance;
	private Moria4() { }
	public static Moria4 getInstance() {
		if (instance == null) {
			instance = new Moria4();
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
		mor2 = Moria2.getInstance();
		mor3 = Moria3.getInstance();
		py = Player.getInstance();
		recall = Recall.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	public void tunnel(int dir) {
		int i, tabil;
		CaveType c_ptr;
		InvenType i_ptr;
		IntPointer y, x;
		MonsterType m_ptr;
		String out_val, m_name;
		
		if ((py.py.flags.confused > 0) &&	/* Confused?	     */
				(m1.randint(4) > 1)) {		/* 75% random movement   */
			dir = m1.randint(9);
		}
		y = new IntPointer(py.char_row);
		x = new IntPointer(py.char_col);
		m3.mmove(dir, y, x);
		
		c_ptr = var.cave[y.value()][x.value()];
		/* Compute the digging ability of player; based on	   */
		/* strength, and type of tool used			   */
		tabil = py.py.stats.use_stat[Constants.A_STR];
		i_ptr = t.inventory[Constants.INVEN_WIELD];
		
		/* Don't let the player tunnel somewhere illegal, this is necessary to
		 * prevent the player from getting a free attack by trying to tunnel
		 * somewhere where it has no effect.  */
		if (c_ptr.fval < Constants.MIN_CAVE_WALL && (c_ptr.tptr == 0 || (t.t_list[c_ptr.tptr].tval != Constants.TV_RUBBLE && t.t_list[c_ptr.tptr].tval != Constants.TV_SECRET_DOOR))) {
			if (c_ptr.tptr == 0) {
				io.msg_print ("Tunnel through what?  Empty air?!?");
				var.free_turn_flag = true;
			} else {
				io.msg_print("You can't tunnel through that.");
				var.free_turn_flag = true;
			}
			return;
		}
		
		if (c_ptr.cptr > 1) {
			m_ptr = mon.m_list[c_ptr.cptr];
			if (m_ptr.ml) {
				m_name = String.format("The %s", mon.c_list[m_ptr.mptr].name);
			} else {
				m_name = "Something";
			}
			out_val = String.format("%s is in your way!", m_name);
			io.msg_print(out_val);
			
			/* let the player attack the creature */
			if (py.py.flags.afraid < 1) {
				mor3.py_attack(y.value(), x.value());
			} else {
				io.msg_print("You are too afraid!");
			}
		} else if (i_ptr.tval != Constants.TV_NOTHING) {
			if ((Constants.TR_TUNNEL & i_ptr.flags) != 0) {
				tabil += 25 + i_ptr.p1 * 50;
			} else {
				tabil += (i_ptr.damage[0] * i_ptr.damage[1]) + i_ptr.tohit + i_ptr.todam;
				/* divide by two so that digging without shovel isn't too easy */
				tabil >>= 1;
			}
			
			/* If this weapon is too heavy for the player to wield properly, then
			 * also make it harder to dig with it.  */
			
			if (var.weapon_heavy) {
				tabil += (py.py.stats.use_stat[Constants.A_STR] * 15) - i_ptr.weight;
				if (tabil < 0) {
					tabil = 0;
				}
			}
			
			/* Regular walls; Granite, magma intrusion, quartz vein  */
			/* Don't forget the boundary walls, made of titanium (255)*/
			switch(c_ptr.fval)
			{
			case Constants.GRANITE_WALL:
				i = m1.randint(1200) + 80;
				if (mor3.twall(y.value(), x.value(), tabil, i)) {
					io.msg_print("You have finished the tunnel.");
				} else {
					io.count_msg_print("You tunnel into the granite wall.");
				}
				break;
			case Constants.MAGMA_WALL:
				i = m1.randint(600) + 10;
				if (mor3.twall(y.value(), x.value(), tabil, i)) {
					io.msg_print("You have finished the tunnel.");
				} else {
					io.count_msg_print("You tunnel into the magma intrusion.");
				}
				break;
			case Constants.QUARTZ_WALL:
				i = m1.randint(400) + 10;
				if (mor3.twall(y.value(), x.value(), tabil, i)) {
					io.msg_print("You have finished the tunnel.");
				} else {
					io.count_msg_print("You tunnel into the quartz vein.");
				}
				break;
			case Constants.BOUNDARY_WALL:
				io.msg_print("This seems to be permanent rock.");
				break;
			default:
				/* Is there an object in the way?  (Rubble and secret doors)*/
				if (c_ptr.tptr != 0) {
					/* Rubble.     */
					if (t.t_list[c_ptr.tptr].tval == Constants.TV_RUBBLE) {
						if (tabil > m1.randint(180)) {
							mor3.delete_object(y.value(), x.value());
							io.msg_print("You have removed the rubble.");
							if (m1.randint(10) == 1) {
								m3.place_object(y.value(), x.value(), false);
								if (m1.test_light(y.value(), x.value())) {
									io.msg_print("You have found something!");
								}
							}
							mor1.lite_spot(y.value(), x.value());
						} else {
							io.count_msg_print("You dig in the rubble.");
						}
					
					/* Secret doors.*/
					} else if (t.t_list[c_ptr.tptr].tval == Constants.TV_SECRET_DOOR) {
						io.count_msg_print("You tunnel into the granite wall.");
						mor2.search(py.char_row, py.char_col, py.py.misc.srh);
					} else {
						//abort();
						return;
					}
				} else {
					//abort();
					return;
				}
				break;
			}
		} else {
			io.msg_print("You dig with your hands, making no progress.");
		}
	}
	
	/* Disarms a trap					-RAK-	*/
	public void disarm_trap() {
		IntPointer y, x, dir = new IntPointer();
		int level, tmp;
		boolean no_disarm;
		int tot, i;
		CaveType c_ptr;
		InvenType i_ptr;
		MonsterType m_ptr;
		String m_name, out_val;
		
		y = new IntPointer(py.char_row);
		x = new IntPointer(py.char_col);
		if (mor1.get_dir("", dir)) {
			m3.mmove(dir.value(), y, x);
			c_ptr = var.cave[y.value()][x.value()];
			no_disarm = false;
			if (c_ptr.cptr > 1 && c_ptr.tptr != 0 && (t.t_list[c_ptr.tptr].tval == Constants.TV_VIS_TRAP || t.t_list[c_ptr.tptr].tval == Constants.TV_CHEST)) {
				m_ptr = mon.m_list[c_ptr.cptr];
				if (m_ptr.ml) {
					m_name = String.format("The %s", mon.c_list[m_ptr.mptr].name);
				} else {
					m_name = "Something";
				}
				out_val = String.format("%s is in your way!", m_name);
				io.msg_print(out_val);
			} else if (c_ptr.tptr != 0) {
				tot = py.py.misc.disarm + 2 * m3.todis_adj() + m3.stat_adj(Constants.A_INT) + (py.class_level_adj[py.py.misc.pclass][Constants.CLA_DISARM] * py.py.misc.lev / 3);
				if ((py.py.flags.blind > 0) || (mor1.no_light())) {
					tot = tot / 10;
				}
				if (py.py.flags.confused > 0) {
					tot = tot / 10;
				}
				if (py.py.flags.image > 0) {
					tot = tot / 10;
				}
				i_ptr = t.t_list[c_ptr.tptr];
				i = i_ptr.tval;
				level = i_ptr.level;
				if (i == Constants.TV_VIS_TRAP) {	/* Floor trap    */
					if ((tot + 100 - level) > m1.randint(100)) {
						io.msg_print("You have disarmed the trap.");
						py.py.misc.exp += i_ptr.p1;
						mor3.delete_object(y.value(), x.value());
						/* make sure we move onto the trap even if confused */
						tmp = py.py.flags.confused;
						py.py.flags.confused = 0;
						mor3.move_char(dir.value(), false);
						py.py.flags.confused = tmp;
						m3.prt_experience();
					
					/* avoid randint(0) call */
					} else if ((tot > 5) && (m1.randint(tot) > 5)) {
						io.count_msg_print("You failed to disarm the trap.");
					} else {
						io.msg_print("You set the trap off!");
						/* make sure we move onto the trap even if confused */
						tmp = py.py.flags.confused;
						py.py.flags.confused = 0;
						mor3.move_char(dir.value(), false);
						py.py.flags.confused += tmp;
					}
				} else if (i == Constants.TV_CHEST) {
					if (!desc.known2_p(i_ptr)) {
						io.msg_print("I don't see a trap.");
						var.free_turn_flag = true;
					} else if ((Constants.CH_TRAPPED & i_ptr.flags) != 0) {
						if ((tot - level) > m1.randint(100)) {
							i_ptr.flags &= ~Constants.CH_TRAPPED;
							if ((Constants.CH_LOCKED & i_ptr.flags) != 0) {
								i_ptr.name2 = Constants.SN_LOCKED;
							} else {
								i_ptr.name2 = Constants.SN_DISARMED;
							}
							io.msg_print("You have disarmed the chest.");
							desc.known2(i_ptr);
							py.py.misc.exp += level;
							m3.prt_experience();
						} else if ((tot > 5) && (m1.randint(tot) > 5)) {
							io.count_msg_print("You failed to disarm the chest.");
						} else {
							io.msg_print("You set a trap off!");
							desc.known2(i_ptr);
							mor3.chest_trap(y.value(), x.value());
						}
					} else {
						io.msg_print("The chest was not trapped.");
						var.free_turn_flag = true;
					}
				} else {
					no_disarm = true;
				}
			} else {
				no_disarm = true;
			}
			
			if (no_disarm) {
				io.msg_print("I do not see anything to disarm there.");
				var.free_turn_flag = true;
			}
		}
	}
	
	/* An enhanced look, with peripheral vision. Looking all 8	-CJS-
	   directions will see everything which ought to be visible. Can
	   specify direction 5, which looks in all directions.
	
	   For the purpose of hindering vision, each place is regarded as
	   a diamond just touching its four immediate neighbours. A
	   diamond is opaque if it is a wall, or shut door, or something
	   like that. A place is visible if any part of its diamond is
	   visible: i.e. there is a line from the view point to part of
	   the diamond which does not pass through any opaque diamonds.
	
	   Consider the following situation:
	
	     @....			    X	X   X	X   X
	     .##..			   / \ / \ / \ / \ / \
	     .....			  X @ X . X . X 1 X . X
					   \ / \ / \ / \ / \ /
					    X	X   X	X   X
		   Expanded view, with	   / \ / \ / \ / \ / \
		   diamonds inscribed	  X . X # X # X 2 X . X
		   about each point,	   \ / \ / \ / \ / \ /
		   and some locations	    X	X   X	X   X
		   numbered.		   / \ / \ / \ / \ / \
					  X . X . X . X 3 X 4 X
					   \ / \ / \ / \ / \ /
					    X	X   X	X   X
		- Location 1 is fully visible.
		- Location 2 is visible, even though partially obscured.
		- Location 3 is invisible, but if either # were
		  transparent, it would be visible.
		- Location 4 is completely obscured by a single #.
	
	   The function which does the work is look_ray. It sets up its
	   own co-ordinate frame (global variables map back to the
	   dungeon frame) and looks for everything between two angles
	   specified from a central line. It is recursive, and each call
	   looks at stuff visible along a line parallel to the center
	   line, and a set distance away from it. A diagonal look uses
	   more extreme peripheral vision from the closest horizontal and
	   vertical directions; horizontal or vertical looks take a call
	   for each side of the central line. */
	
	/* Globally accessed variables: gl_nseen counts the number of places where
	 * something is seen. gl_rock indicates a look for rock or objects.
	 *
	 * The others map co-ords in the ray frame to dungeon co-ords.
	 *
	 * dungeon y = py.char_row	 + gl_fyx * (ray x)  + gl_fyy * (ray y)
	 * dungeon x = py.char_col	 + gl_fxx * (ray x)  + gl_fxy * (ray y) */
	static int gl_fxx, gl_fxy, gl_fyx, gl_fyy;
	static int gl_nseen;
	static boolean gl_noquery;
	static int gl_rock;
	/* Intended to be indexed by dir/2, since is only relevant to horizontal or
	 * vertical directions. */
	static int[] set_fxy = { 0,  1,	 0,  0, -1 };
	static int[] set_fxx = { 0,  0, -1,  1,	 0 };
	static int[] set_fyy = { 0,  0,	 1, -1,	 0 };
	static int[] set_fyx = { 0,  1,	 0,  0, -1 };
	/* Map diagonal-dir/2 to a normal-dir/2. */
	static int[] map_diag1 = { 1, 3, 0, 2, 4 };
	static int[] map_diag2 = { 2, 1, 0, 4, 3 };
	
	private final int GRADF = 10000;	/* Any sufficiently big number will do */
	
	/* Look at what we can see. This is a free move.
	
	   Prompts for a direction, and then looks at every object in
	   turn within a cone of vision in that direction. For each
	   object, the cursor is moved over the object, a description is
	   given, and we wait for the user to type something. Typing
	   ESCAPE will abort the entire look.
	
	   Looks first at real objects and monsters, and looks at rock
	   types only after all other things have been seen.  Only looks at rock
	   types if the highlight_seams option is set. */
	
	public void look() {
		int i;
		boolean abort;
		IntPointer dir = new IntPointer();
		BooleanPointer dummy = new BooleanPointer();
		
		if (py.py.flags.blind > 0) {
			io.msg_print("You can't see a damn thing!");
		} else if (py.py.flags.image > 0) {
			io.msg_print("You can't believe what you are seeing! It's like a dream!");
		} else if (mor1.get_alldir("Look which direction?", dir)) {
			abort = false;
			gl_nseen = 0;
			gl_rock = 0;
			gl_noquery = false;	/* Have to set this up for the look_see */
			if (look_see(0, 0, dummy)) {
				abort = true;
			} else {
				do {
					abort = false;
					if (dir.value() == 5) {
						for (i = 1; i <= 4; i++) {
							gl_fxx = set_fxx[i]; gl_fyx = set_fyx[i];
							gl_fxy = set_fxy[i]; gl_fyy = set_fyy[i];
							if (look_ray(0, 2 * GRADF - 1, 1)) {
								abort = true;
								break;
							}
							gl_fxy = -	gl_fxy;
							gl_fyy = -gl_fyy;
							if (look_ray(0, 2 * GRADF, 2)) {
								abort = true;
								break;
							}
						}
					} else if ((dir.value() & 1) == 0) {	/* Straight directions */
						i = dir.value() >> 1;
						gl_fxx = set_fxx[i]; gl_fyx = set_fyx[i];
						gl_fxy = set_fxy[i]; gl_fyy = set_fyy[i];
						if (look_ray(0, GRADF, 1)) {
							abort = true;
						} else {
							gl_fxy = -gl_fxy;
							gl_fyy = -gl_fyy;
							abort = look_ray(0, GRADF, 2);
						}
					} else {
						i = map_diag1[dir.value() >> 1];
						gl_fxx = set_fxx[i]; gl_fyx = set_fyx[i];
						gl_fxy = -set_fxy[i]; gl_fyy = -set_fyy[i];
						if (look_ray(1, 2 * GRADF, GRADF)) {
							abort = true;
						} else {
							i = map_diag2[dir.value() >> 1];
							gl_fxx = set_fxx[i]; gl_fyx = set_fyx[i];
							gl_fxy = set_fxy[i]; gl_fyy = set_fyy[i];
							abort = look_ray(1, 2 * GRADF - 1, GRADF);
						}
					}
				} while (!abort && var.highlight_seams.value() && (++gl_rock < 2));
				if (abort) {
					io.msg_print("--Aborting look--");
				} else {
					if (gl_nseen > 0) {
						if (dir.value() == 5) {
							io.msg_print("That's all you see.");
						} else {
							io.msg_print("That's all you see in that direction.");
						}
					} else if (dir.value() == 5) {
						io.msg_print("You see nothing of interest.");
					} else {
						io.msg_print("You see nothing of interest in that direction.");
					}
				}
			}
		}
	}
	
	/* Look at everything within a cone of vision between two ray
	   lines emanating from the player, and y or more places away
	   from the direct line of view. This is recursive.
	
	   Rays are specified by gradients, y over x, multiplied by
	   2*GRADF. This is ONLY called with gradients between 2*GRADF
	   (45 degrees) and 1 (almost horizontal).
	
	   (y axis)/ angle from
	     ^	  /	    ___ angle to
	     |	 /	 ___
	  ...|../.....___.................... parameter y (look at things in the
	     | /   ___			      cone, and on or above this line)
	     |/ ___
	     @-------------------.   direction in which you are looking. (x axis)
	     |
	     | */
	public boolean look_ray(int y, int from, int to) {
		int max_x, x;
		BooleanPointer transparent = new BooleanPointer();
		
		/* from is the larger angle of the ray, since we scan towards the
	     * center line. If from is smaller, then the ray does not exist. */
		if (from <= to || y > Constants.MAX_SIGHT) {
			return false;
		}
		/* Find first visible location along this line. Minimum x such
	     * that (2x-1)/x < from/GRADF <=> x > GRADF(2x-1)/from. This may
	     * be called with y=0 whence x will be set to 0. Thus we need a
	     * special fix. */
		x = (GRADF * (2 * y - 1) / from + 1);
		if (x <= 0) {
			x = 1;
		}
		
		/* Find last visible location along this line.
	     * Maximum x such that (2x+1)/x > to/GRADF <=> x < GRADF(2x+1)/to */
		max_x = ((GRADF * (2 * y + 1) - 1) / to);
		if (max_x > Constants.MAX_SIGHT) {
			max_x = Constants.MAX_SIGHT;
		}
		if (max_x < x) {
			return false;
		}
		
		/* gl_noquery is a HACK to prevent doubling up on direct lines of
	     * sight. If 'to' is	greater than 1, we do not really look at
	     * stuff along the direct line of sight, but we do have to see
	     * what is opaque for the purposes of obscuring other objects. */
		if (y == 0 && to > 1 || y == x && from < GRADF * 2) {
			gl_noquery = true;
		} else {
			gl_noquery = false;
		}
		if (look_see(x, y, transparent)) {
			return true;
		}
		if (y == x) {
			gl_noquery = false;
		}
		
		boolean init_transparent = transparent.value();
		//if (transparent) {
		//	goto init_transparent;
		//}
		
		for (;;) {
			if (init_transparent) {
				/* Look down the window we've found. */
				if (look_ray(y + 1, from, ((2 * y + 1) * GRADF / x))) {
					return true;
				}
				/* Find the start of next window. */
				do {
					if (x == max_x) {
						return false;
					}
					/* See if this seals off the scan. (If y is zero, then it will.) */
					from = ((2 * y - 1) * GRADF / x);
					if (from <= to) {
						return false;
					}
					x++;
					if (look_see(x, y, transparent)) {
						return true;
					}
				} while (!transparent.value());
			}
			//init_transparent:
			init_transparent = true;
			/* Find the end of this window of visibility. */
			do {
				if (x == max_x) {
					/* The window is trimmed by an earlier limit. */
					return look_ray(y + 1, from, to);
				}
				x++;
				if (look_see(x, y, transparent)) {
					return true;
				}
			} while (transparent.value());
		}
	}
	
	public boolean look_see(int x, int y, BooleanPointer transparent) {
		String dstring, string;
		char query = '\0';
		CaveType c_ptr;
		int j;
		String out_val, tmp_str;
		
		if (x < 0 || y < 0 || y > x) {
			tmp_str = String.format("Illegal call to look_see(%d, %d)", x, y);
			io.msg_print(tmp_str);
		}
		if (x == 0 && y == 0) {
			dstring = "You are on";
		} else {
			dstring = "You see";
		}
		j = py.char_col + gl_fxx * x + gl_fxy * y;
		y = py.char_row + gl_fyx * x + gl_fyy * y;
		x = j;
		if (!m1.panel_contains(y, x)) {
			transparent.value(false);
			return false;
		}
		c_ptr = var.cave[y][x];
		transparent.value((c_ptr.fval <= Constants.MAX_OPEN_SPACE));
		if (gl_noquery) {
			return false; /* Don't look at a direct line of sight. A hack. */
		}
		out_val = "";
		if (gl_rock == 0 && c_ptr.cptr > 1 && mon.m_list[c_ptr.cptr].ml) {
			j = mon.m_list[c_ptr.cptr].mptr;
			out_val = String.format("%s %s %s. [(r)ecall]", dstring, desc.is_a_vowel(mon.c_list[j].name.charAt(0) ) ? "an" : "a", mon.c_list[j].name);
			dstring = "It is on";
			io.prt(out_val, 0, 0);
			io.move_cursor_relative(y, x);
			query = io.inkey();
			if (query == 'r' || query == 'R') {
				io.save_screen();
				query = (char)recall.roff_recall(j);
				io.restore_screen();
			}
		}
		if (c_ptr.tl || c_ptr.pl || c_ptr.fm) {
			boolean granite = false;
			if (c_ptr.tptr != 0) {
				if (t.t_list[c_ptr.tptr].tval == Constants.TV_SECRET_DOOR) {
					//goto granite;
					granite = true;
				}
				if (!granite && gl_rock == 0 && t.t_list[c_ptr.tptr].tval != Constants.TV_INVIS_TRAP) {
					tmp_str = desc.objdes(t.t_list[c_ptr.tptr], true);
					out_val = String.format("%s %s ---pause---", dstring, tmp_str);
					dstring = "It is in";
					io.prt(out_val, 0, 0);
					io.move_cursor_relative(y, x);
					query = io.inkey();
				}
			}
			if ((gl_rock != 0 || !out_val.equals("")) && c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				if (!granite) {
					switch(c_ptr.fval)
					{
					case Constants.BOUNDARY_WALL:
					case Constants.GRANITE_WALL:
						//granite:
						/* Granite is only interesting if it contains something. */
						if(!out_val.equals("")) {
							string = "a granite wall";
						} else {
							string = "";	/* In case we jump here */
						}
						break;
					case Constants.MAGMA_WALL: string = "some dark rock";
						break;
					case Constants.QUARTZ_WALL: string = "a quartz vein";
						break;
					default: string = "";
						break;
					}
				} else {
					if(!out_val.equals("")) {
						string = "a granite wall";
					} else {
						string = "";
					}
				}
				if (!string.equals("")) {
					out_val = String.format("%s %s ---pause---", dstring, string);
					io.prt(out_val, 0, 0);
					io.move_cursor_relative(y, x);
					query = io.inkey();
				}
			}
		}
		if (!out_val.equals("")) {
			gl_nseen++;
			if (query == Constants.ESCAPE) {
				return true;
			}
		}
		
		return false;
	}
	
	public void inven_throw(int item_val, InvenType t_ptr) {
		InvenType i_ptr;
		
		i_ptr = t.inventory[item_val];
		i_ptr.copyInto(t_ptr);
		if (i_ptr.number > 1) {
			t_ptr.number = 1;
			i_ptr.number--;
			t.inven_weight -= i_ptr.weight;
			py.py.flags.status |= Constants.PY_STR_WGT;
		} else {
			m3.inven_destroy(item_val);
		}
	}
	
	/* Obtain the hit and damage bonuses and the maximum distance for a
	 * thrown missile. */
	public void facts(InvenType i_ptr, IntPointer tbth, IntPointer tpth, IntPointer tdam, IntPointer tdis) {
		int tmp_weight;
		
		if (i_ptr.weight < 1) {
			tmp_weight = 1;
		} else {
			tmp_weight = i_ptr.weight;
		}
		
		/* Throwing objects			*/
		tdam.value(m1.pdamroll(i_ptr.damage) + i_ptr.todam);
		tbth.value(py.py.misc.bthb * 75 / 100);
		tpth.value(py.py.misc.ptohit + i_ptr.tohit);
		
		/* Add this back later if the correct throwing device. -CJS- */
		if (t.inventory[Constants.INVEN_WIELD].tval != Constants.TV_NOTHING) {
			tpth.value(tpth.value() - t.inventory[Constants.INVEN_WIELD].tohit);
		}
		
		tdis.value((((py.py.stats.use_stat[Constants.A_STR] + 20) * 10) / tmp_weight));
		if (tdis.value() > 10)	tdis.value(10);
		
		/* multiply damage bonuses instead of adding, when have proper
		 * missile/weapon combo, this makes them much more useful */
		
		/* Using Bows,  slings,  or crossbows	*/
		if (t.inventory[Constants.INVEN_WIELD].tval == Constants.TV_BOW) {
			switch(t.inventory[Constants.INVEN_WIELD].p1)
			{
			case 1:
				if (i_ptr.tval == Constants.TV_SLING_AMMO) {	/* Sling and ammo */
					tbth.value(py.py.misc.bthb);
					tpth.value(tpth.value() + 2 * t.inventory[Constants.INVEN_WIELD].tohit);
					tdam.value(tdam.value() + t.inventory[Constants.INVEN_WIELD].todam);
					tdam.value(tdam.value() * 2);
					tdis.value(20);
				}
				break;
			case 2:
				if (i_ptr.tval == Constants.TV_ARROW) {	/* Short Bow and Arrow	*/
					tbth.value(py.py.misc.bthb);
					tpth.value(tpth.value() + 2 * t.inventory[Constants.INVEN_WIELD].tohit);
					tdam.value(tdam.value() + t.inventory[Constants.INVEN_WIELD].todam);
					tdam.value(tdam.value() * 2);
					tdis.value(25);
				}
				break;
			case 3:
				if (i_ptr.tval == Constants.TV_ARROW) {	/* Long Bow and Arrow	*/
					tbth.value(py.py.misc.bthb);
					tpth.value(tpth.value() + 2 * t.inventory[Constants.INVEN_WIELD].tohit);
					tdam.value(tdam.value() + t.inventory[Constants.INVEN_WIELD].todam);
					tdam.value(tdam.value() * 3);
					tdis.value(30);
				}
				break;
			case 4:
				if (i_ptr.tval == Constants.TV_ARROW) {	/* Composite Bow and Arrow*/
					tbth.value(py.py.misc.bthb);
					tpth.value(tpth.value() + 2 * t.inventory[Constants.INVEN_WIELD].tohit);
					tdam.value(tdam.value() + t.inventory[Constants.INVEN_WIELD].todam);
					tdam.value(tdam.value() * 4);
					tdis.value(35);
				}
				break;
			case 5:
				if (i_ptr.tval == Constants.TV_BOLT) {	/* Light Crossbow and Bolt*/
					tbth.value(py.py.misc.bthb);
					tpth.value(tpth.value() + 2 * t.inventory[Constants.INVEN_WIELD].tohit);
					tdam.value(tdam.value() + t.inventory[Constants.INVEN_WIELD].todam);
					tdam.value(tdam.value() * 3);
					tdis.value(25);
				}
				break;
			case 6:
				if (i_ptr.tval == Constants.TV_BOLT) {	/* Heavy Crossbow and Bolt*/
					tbth.value(py.py.misc.bthb);
					tpth.value(tpth.value() + 2 * t.inventory[Constants.INVEN_WIELD].tohit);
					tdam.value(tdam.value() + t.inventory[Constants.INVEN_WIELD].todam);
					tdam.value(tdam.value() * 4);
					tdis.value(35);
				}
				break;
			}
		}
	}
	
	public void drop_throw(int y, int x, InvenType t_ptr) {
		int i, j, k;
		boolean flag;
		int cur_pos;
		String out_val, tmp_str;
		CaveType c_ptr;
		
		flag = false;
		i = y;
		j = x;
		k = 0;
		if (m1.randint(10) > 1) {
			do {
				if (m1.in_bounds(i, j)) {
					c_ptr = var.cave[i][j];
					if (c_ptr.fval <= Constants.MAX_OPEN_SPACE && c_ptr.tptr == 0) {
						flag = true;
					}
				}
				if (!flag) {
					i = y + m1.randint(3) - 2;
					j = x + m1.randint(3) - 2;
					k++;
				}
			} while ((!flag) && (k <= 9));
		}
		if (flag) {
			cur_pos = m1.popt();
			var.cave[i][j].tptr = cur_pos;
			t_ptr.copyInto(t.t_list[cur_pos]);
			mor1.lite_spot(i, j);
		} else {
			tmp_str = desc.objdes(t_ptr, false);
			out_val = String.format("The %s disappears.", tmp_str);
			io.msg_print(out_val);
		}
	}
	
	/* Throw an object across the dungeon.		-RAK-	*/
	/* Note: Flasks of oil do fire damage				 */
	/* Note: Extra damage and chance of hitting when missiles are used*/
	/*	 with correct weapon.  I.E.  wield bow and throw arrow.	 */
	public void throw_object() {
		IntPointer item_val = new IntPointer(), tbth = new IntPointer(), tpth = new IntPointer(), tdam = new IntPointer(), tdis = new IntPointer();
		IntPointer y, x, dir = new IntPointer();
		int oldy, oldx, cur_dis;
		boolean flag, visible;
		String out_val, tmp_str;
		InvenType throw_obj = new InvenType();
		CaveType c_ptr;
		MonsterType m_ptr;
		int i;
		char tchar;
		
		if (t.inven_ctr == 0) {
			io.msg_print("But you are not carrying anything.");
			var.free_turn_flag = true;
		} else if (mor1.get_item(item_val, "Fire/Throw which one?", 0, t.inven_ctr - 1, "", "")) {
			if (mor1.get_dir("", dir)) {
				desc.desc_remain(item_val.value());
				if (py.py.flags.confused > 0) {
					io.msg_print("You are confused.");
					do {
						dir.value(m1.randint(9));
					} while (dir.value() == 5);
				}
				inven_throw(item_val.value(), throw_obj);
				facts(throw_obj, tbth, tpth, tdam, tdis);
				tchar = throw_obj.tchar;
				flag = false;
				y = new IntPointer(py.char_row);
				x = new IntPointer(py.char_col);
				oldy = py.char_row;
				oldx = py.char_col;
				cur_dis = 0;
				do {
					m3.mmove(dir.value(), y, x);
					cur_dis++;
					mor1.lite_spot(oldy, oldx);
					if (cur_dis > tdis.value())	flag = true;
					c_ptr = var.cave[y.value()][x.value()];
					if ((c_ptr.fval <= Constants.MAX_OPEN_SPACE) && (!flag)) {
						if (c_ptr.cptr > 1) {
							flag = true;
							m_ptr = mon.m_list[c_ptr.cptr];
							tbth.value(tbth.value() - cur_dis);
							/* if monster not lit, make it much more difficult to
							 * hit, subtract off most bonuses, and reduce bthb
							 * depending on distance */
							if (!m_ptr.ml) {
								tbth.value((tbth.value() / (cur_dis + 2))
										- (py.py.misc.lev * py.class_level_adj[py.py.misc.pclass][Constants.CLA_BTHB] / 2)
										- (tpth.value() * (Constants.BTH_PLUS_ADJ - 1)));
							}
							if (mor1.test_hit(tbth.value(), py.py.misc.lev, tpth.value(), mon.c_list[m_ptr.mptr].ac, Constants.CLA_BTHB)) {
								i = m_ptr.mptr;
								tmp_str = desc.objdes(throw_obj, false);
								/* Does the player know what he's fighting?	   */
								if (!m_ptr.ml) {
									out_val = String.format("You hear a cry as the %s finds a mark.", tmp_str);
									visible = false;
								} else {
									out_val = String.format("The %s hits the %s.", tmp_str, mon.c_list[i].name);
									visible = true;
								}
								io.msg_print(out_val);
								tdam.value(m3.tot_dam(throw_obj, tdam.value(), i));
								tdam.value(m3.critical_blow(throw_obj.weight, tpth.value(), tdam.value(), Constants.CLA_BTHB));
								if (tdam.value() < 0) tdam.value(0);
								i = mor3.mon_take_hit(c_ptr.cptr, tdam.value());
								if (i >= 0) {
									if (!visible) {
										io.msg_print("You have killed something!");
									} else {
										out_val = String.format("You have killed the %s.", mon.c_list[i].name);
										io.msg_print(out_val);
									}
									m3.prt_experience();
								}
							} else {
								drop_throw(oldy, oldx, throw_obj);
							}
						} else {	/* do not test c_ptr.fm here */
							if (m1.panel_contains(y.value(), x.value()) && (py.py.flags.blind < 1) && (c_ptr.tl || c_ptr.pl)) {
								io.print(tchar, y.value(), x.value());
								io.put_qio(); /* show object moving */
							}
						}
					} else {
						flag = true;
						drop_throw(oldy, oldx, throw_obj);
					}
					oldy = y.value();
					oldx = x.value();
				} while (!flag);
			}
		}
	}
	
	/* Make a bash attack on someone.				-CJS-
	 * Used to be part of bash above. */
	public void py_bash(int y, int x) {
		int monster, k, avg_max_hp, base_tohit;
		CreatureType c_ptr;
		MonsterType m_ptr;
		String m_name, out_val;
		
		monster = var.cave[y][x].cptr;
		m_ptr = mon.m_list[monster];
		c_ptr = mon.c_list[m_ptr.mptr];
		m_ptr.csleep = 0;
		/* Does the player know what he's fighting?	   */
		if (!m_ptr.ml) {
			m_name = "it";
		} else {
			m_name = String.format("the %s", c_ptr.name);
		}
		base_tohit = py.py.stats.use_stat[Constants.A_STR] + t.inventory[Constants.INVEN_ARM].weight / 2 + py.py.misc.wt / 10;
		if (!m_ptr.ml) {
			base_tohit = (base_tohit / 2) - (py.py.stats.use_stat[Constants.A_DEX] * (Constants.BTH_PLUS_ADJ - 1))
					- (py.py.misc.lev * py.class_level_adj[py.py.misc.pclass][Constants.CLA_BTH] / 2);
		}
		
		if (mor1.test_hit(base_tohit, py.py.misc.lev, py.py.stats.use_stat[Constants.A_DEX], c_ptr.ac, Constants.CLA_BTH)) {
			out_val = String.format("You hit %s.", m_name);
			io.msg_print(out_val);
			k = m1.pdamroll(t.inventory[Constants.INVEN_ARM].damage);
			k = m3.critical_blow((t.inventory[Constants.INVEN_ARM].weight / 4 + py.py.stats.use_stat[Constants.A_STR]), 0, k, Constants.CLA_BTH);
			k += py.py.misc.wt/60 + 3;
			if (k < 0)	k = 0;
			
			/* See if we done it in.				     */
			if (mor3.mon_take_hit(monster, k) >= 0) {
				out_val = String.format("You have slain %s.", m_name);
				io.msg_print(out_val);
				m3.prt_experience();
			} else {
				m_name = Character.toUpperCase(m_name.charAt(0)) + m_name.substring(1);	/* Capitalize */
				/* Can not stun Balrog */
				avg_max_hp = ((c_ptr.cdefense & Constants.CD_MAX_HP) != 0 ? c_ptr.hd[0] * c_ptr.hd[1] : (c_ptr.hd[0] * (c_ptr.hd[1] + 1)) >> 1);
				if ((100 + m1.randint(400) + m1.randint(400)) > (m_ptr.hp + avg_max_hp)) {
					m_ptr.stunned += m1.randint(3) + 1;
					if (m_ptr.stunned > 24)	m_ptr.stunned = 24;
					out_val = String.format("%s appears stunned!", m_name);
				} else {
					out_val = String.format("%s ignores your bash!", m_name);
				}
				io.msg_print(out_val);
			}
		} else {
			out_val = String.format("You miss %s.", m_name);
			io.msg_print(out_val);
		}
		if (m1.randint(150) > py.py.stats.use_stat[Constants.A_DEX]) {
			io.msg_print("You are off balance.");
			py.py.flags.paralysis = 1 + m1.randint(2);
		}
	}
	
	/* Bash open a door or chest				-RAK-	*/
	/* Note: Affected by strength and weight of character
	
	   For a closed door, p1 is positive if locked; negative if
	   stuck. A disarm spell unlocks and unjams doors!
	
	   For an open door, p1 is positive for a broken door.
	
	   A closed door can be opened - harder if locked. Any door might
	   be bashed open (and thereby broken). Bashing a door is
	   (potentially) faster! You move into the door way. To open a
	   stuck door, it must be bashed. A closed door can be jammed
	   (which makes it stuck if previously locked).
	
	   Creatures can also open doors. A creature with open door
	   ability will (if not in the line of sight) move though a
	   closed or secret door with no changes. If in the line of
	   sight, closed door are opened, & secret door revealed.
	   Whether in the line of sight or not, such a creature may
	   unlock or unstick a door.
	
	   A creature with no such ability will attempt to bash a
	   non-secret door. */
	public void bash() {
		IntPointer y, x;
		int tmp;
		IntPointer dir = new IntPointer();
		CaveType c_ptr;
		InvenType t_ptr;
		
		y = new IntPointer(py.char_row);
		x = new IntPointer(py.char_col);
		if (mor1.get_dir("", dir)) {
			if (py.py.flags.confused > 0) {
				io.msg_print("You are confused.");
				do {
					dir.value(m1.randint(9));
				} while (dir.value() == 5);
			}
			m3.mmove(dir.value(), y, x);
			c_ptr = var.cave[y.value()][x.value()];
			if (c_ptr.cptr > 1) {
				if (py.py.flags.afraid > 0) {
					io.msg_print("You are afraid!");
				} else {
					py_bash(y.value(), x.value());
				}
			} else if (c_ptr.tptr != 0) {
				t_ptr = t.t_list[c_ptr.tptr];
				if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
					io.count_msg_print("You smash into the door!");
					tmp = py.py.stats.use_stat[Constants.A_STR] + py.py.misc.wt / 2;
					/* Use (roughly) similar method as for monsters. */
					if (m1.randint(tmp * (20 + Math.abs(t_ptr.p1))) < 10 * (tmp - Math.abs(t_ptr.p1))) {
						io.msg_print("The door crashes open!");
						desc.invcopy(t.t_list[c_ptr.tptr], Constants.OBJ_OPEN_DOOR);
						t_ptr.p1 = 1 - m1.randint(2); /* 50% chance of breaking door */
						c_ptr.fval = Constants.CORR_FLOOR;
						if (py.py.flags.confused == 0) {
							mor3.move_char(dir.value(), false);
						} else {
							mor1.lite_spot(y.value(), x.value());
						}
					} else if (m1.randint(150) > py.py.stats.use_stat[Constants.A_DEX]) {
						io.msg_print("You are off-balance.");
						py.py.flags.paralysis = 1 + m1.randint(2);
					} else if (var.command_count == 0) {
						io.msg_print("The door holds firm.");
					}
				} else if (t_ptr.tval == Constants.TV_CHEST) {
					if (m1.randint(10) == 1) {
						io.msg_print("You have destroyed the chest.");
						io.msg_print("and its contents!");
						t_ptr.index = Constants.OBJ_RUINED_CHEST;
						t_ptr.flags = 0;
					} else if ((Constants.CH_LOCKED & t_ptr.flags) != 0 && (m1.randint(10) == 1)) {
						io.msg_print("The lock breaks open!");
						t_ptr.flags &= ~Constants.CH_LOCKED;
					} else {
						io.count_msg_print("The chest holds firm.");
					}
				} else { 
					/* Can't give free turn, or else player could try directions
					 * until he found invisible creature */
					io.msg_print("You bash it, but nothing interesting happens.");
				}
			} else {
				if (c_ptr.fval < Constants.MIN_CAVE_WALL) {
					io.msg_print("You bash at empty space.");
				} else {
					/* same message for wall as for secret door */
					io.msg_print("You bash it, but nothing interesting happens.");
				}
			}
		}
	}
}
