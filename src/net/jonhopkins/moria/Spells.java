/*
 * Spells.java: player/creature spells, breaths, wands, scrolls, etc. code
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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;

public class Spells {
	/* Following are spell procedure/functions			-RAK-	*/
	/* These routines are commonly used in the scroll, potion, wands, and	 */
	/* staves routines, and are occasionally called from other areas.	  */
	/* Now included are creature spells also.		       -RAK    */
	
	private Creature creature;
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Misc4 m4;
	private Monsters mon;
	private Moria1 mor1;
	private Moria2 mor2;
	private Moria3 mor3;
	private Player py;
	private Treasure t;
	private Variable var;
	
	private static Spells instance;
	private Spells() { }
	public static Spells getInstance() {
		if (instance == null) {
			instance = new Spells();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		creature = Creature.getInstance();
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		m4 = Misc4.getInstance();
		mon = Monsters.getInstance();
		mor1 = Moria1.getInstance();
		mor2 = Moria2.getInstance();
		mor3 = Moria3.getInstance();
		py = Player.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	public String monster_name(MonsterType m_ptr, CreatureType r_ptr) {
		String m_name;
		if (!m_ptr.ml) {
			m_name = "It";
		} else {
			m_name = String.format("The %s", r_ptr.name);
		}
		return m_name;
	}
	
	public String lower_monster_name(MonsterType m_ptr, CreatureType r_ptr) {
		String m_name;
		if (!m_ptr.ml) {
			m_name = "it";
		} else {
			m_name = String.format("the %s", r_ptr.name);
		}
		return m_name;
	}
	
	/* Sleep creatures adjacent to player			-RAK-	*/
	public boolean sleep_monsters1(int y, int x) {
		int i, j;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		boolean sleep;
		String out_val, m_name;
		
		sleep = false;
		for (i = y - 1; i <= y + 1; i++) {
			for (j = x - 1; j <= x + 1; j++) {
				c_ptr = var.cave[i][j];
				if (c_ptr.cptr > 1) {
					m_ptr = mon.m_list[c_ptr.cptr];
					r_ptr = mon.c_list[m_ptr.mptr];
					
					m_name = monster_name(m_ptr, r_ptr);
					if ((m1.randint(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) > 0) {
						if (m_ptr.ml && (r_ptr.cdefense & Constants.CD_NO_SLEEP) > 0)
							var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_NO_SLEEP;
							out_val = String.format("%s is unaffected.", m_name);
							io.msg_print(out_val);
					} else {
						sleep = true;
						m_ptr.csleep = 500;
						out_val = String.format("%s falls asleep.", m_name);
						io.msg_print(out_val);
					}
				}
			}
		}
		return sleep;
	}
	
	/* Detect any treasure on the current panel		-RAK-	*/
	public boolean detect_treasure() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		
		detect = false;
		for (i = var.panel_row_min; i <= var.panel_row_max; i++) {
			for (j = var.panel_col_min; j <= var.panel_col_max; j++) {
				c_ptr = var.cave[i][j];
				if ((c_ptr.tptr != 0) && (t.t_list[c_ptr.tptr].tval == Constants.TV_GOLD) && !m1.test_light(i, j)) {
					c_ptr.fm = true;
					mor1.lite_spot(i, j);
					detect = true;
				}
			}
		}
		return detect;
	}
	
	/* Detect all objects on the current panel		-RAK-	*/
	public boolean detect_object() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		
		detect = false;
		for (i = var.panel_row_min; i <= var.panel_row_max; i++) {
			for (j = var.panel_col_min; j <= var.panel_col_max; j++) {
				c_ptr = var.cave[i][j];
				if ((c_ptr.tptr != 0) && (t.t_list[c_ptr.tptr].tval < Constants.TV_MAX_OBJECT) && !m1.test_light(i, j)) {
					c_ptr.fm = true;
					mor1.lite_spot(i, j);
					detect = true;
				}
			}
		}
		return detect;
	}
	
	/* Locates and displays traps on current panel		-RAK-	*/
	public boolean detect_trap() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		InvenType t_ptr;
		
		detect = false;
		for (i = var.panel_row_min; i <= var.panel_row_max; i++) {
			for (j = var.panel_col_min; j <= var.panel_col_max; j++) {
				c_ptr = var.cave[i][j];
				if (c_ptr.tptr != 0) {
					if (t.t_list[c_ptr.tptr].tval == Constants.TV_INVIS_TRAP) {
						c_ptr.fm = true;
						mor2.change_trap(i, j);
						detect = true;
					} else if (t.t_list[c_ptr.tptr].tval == Constants.TV_CHEST) {
						t_ptr = t.t_list[c_ptr.tptr];
						desc.known2(t_ptr);
					}
				}
			}
		}
		return detect;
	}
	
	/* Locates and displays all secret doors on current panel -RAK-	*/
	public boolean detect_sdoor() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		
		detect = false;
		for (i = var.panel_row_min; i <= var.panel_row_max; i++) {
			for (j = var.panel_col_min; j <= var.panel_col_max; j++) {
				c_ptr = var.cave[i][j];
				if (c_ptr.tptr != 0) {
					/* Secret doors  */
					if (t.t_list[c_ptr.tptr].tval == Constants.TV_SECRET_DOOR) {
						c_ptr.fm = true;
						mor2.change_trap(i, j);
						detect = true;
					}
				
				/* Staircases	 */
				} else if (((t.t_list[c_ptr.tptr].tval == Constants.TV_UP_STAIR) || (t.t_list[c_ptr.tptr].tval == Constants.TV_DOWN_STAIR)) && !c_ptr.fm) {
						c_ptr.fm = true;
						mor1.lite_spot(i, j);
						detect = true;
				}
			}
		}
		return detect;
	}
	
	/* Locates and displays all invisible creatures on current panel -RAK-*/
	public boolean detect_invisible() {
		int i;
		boolean flag;
		MonsterType m_ptr;
		
		flag = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			if (m1.panel_contains(m_ptr.fy, m_ptr.fx) && (Constants.CM_INVISIBLE & mon.c_list[m_ptr.mptr].cmove) > 0) {
				m_ptr.ml = true;
				/* works correctly even if hallucinating */
				io.print(mon.c_list[m_ptr.mptr].cchar, m_ptr.fy, m_ptr.fx);
				flag = true;
			}
		}
		if (flag) {
			io.msg_print("You sense the presence of invisible creatures!");
			io.msg_print("");
			/* must unlight every monster just lighted */
			creature.creatures(false);
		}
		return flag;
	}
	
	/* Light an area: 1.  If corridor  light immediate area -RAK-*/
	/*		  2.  If room  light entire room plus immediate area.     */
	public boolean light_area(int y, int x) {
		int i, j;
		boolean light;
		
		if (py.py.flags.blind < 1) {
			io.msg_print("You are surrounded by a white light.");
		}
		light = true;
		if (var.cave[y][x].lr && (var.dun_level > 0)) {
			mor1.light_room(y, x);
		}
		/* Must always light immediate area, because one might be standing on
		   the edge of a room, or next to a destroyed area, etc.  */
		for (i = y - 1; i <= y + 1; i++) {
			for (j = x - 1; j <= x + 1; j++) {
				var.cave[i][j].pl = true;
				mor1.lite_spot(i, j);
			}
		}
		return light;
	}
	
	/* Darken an area, opposite of light area		-RAK-	*/
	public boolean unlight_area(int y, int x) {
		int i, j;
		int tmp1, tmp2;
		boolean unlight;
		int start_row, start_col, end_row, end_col;
		CaveType c_ptr;
		
		unlight = false;
		if (var.cave[y][x].lr && (var.dun_level > 0)) {
			tmp1 = (Constants.SCREEN_HEIGHT / 2);
			tmp2 = (Constants.SCREEN_WIDTH / 2);
			start_row = (y / tmp1) * tmp1 + 1;
			start_col = (x / tmp2) * tmp2 + 1;
			end_row = start_row + tmp1 - 1;
			end_col = start_col + tmp2 - 1;
			for (i = start_row; i <= end_row; i++) {
				for (j = start_col; j <= end_col; j++) {
					c_ptr = var.cave[i][j];
					if (c_ptr.lr && c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
						c_ptr.pl = false;
						c_ptr.fval = Constants.DARK_FLOOR;
						mor1.lite_spot(i, j);
						if (!m1.test_light(i, j)) {
							unlight = true;
						}
					}
				}
			}
		} else {
			for (i = y - 1; i <= y + 1; i++) {
				for (j = x - 1; j <= x + 1; j++) {
					c_ptr = var.cave[i][j];
					if ((c_ptr.fval == Constants.CORR_FLOOR) && c_ptr.pl) {
						/* pl could have been set by star-lite wand, etc */
						c_ptr.pl = false;
						unlight = true;
					}
				}
			}
		}
		
		if (unlight && py.py.flags.blind <= 0) {
			io.msg_print("Darkness surrounds you.");
		}
		
		return unlight;
	}
	
	/* Map the current area plus some			-RAK-	*/
	public void map_area() {
		CaveType c_ptr;
		int i7, i8, n, m;
		int i, j, k, l;
		
		i = var.panel_row_min - m1.randint(10);
		j = var.panel_row_max + m1.randint(10);
		k = var.panel_col_min - m1.randint(20);
		l = var.panel_col_max + m1.randint(20);
		for (m = i; m <= j; m++) {
			for (n = k; n <= l; n++) {
				if (m1.in_bounds(m, n) && (var.cave[m][n].fval <= Constants.MAX_CAVE_FLOOR)) {
					for (i7 = m - 1; i7 <= m + 1; i7++) {
						for (i8 = n - 1; i8 <= n + 1; i8++) {
							c_ptr = var.cave[i7][i8];
							if (c_ptr.fval >= Constants.MIN_CAVE_WALL) {
								c_ptr.pl = true;
							} else if ((c_ptr.tptr != 0) &&
									(t.t_list[c_ptr.tptr].tval >= Constants.TV_MIN_VISIBLE) &&
									(t.t_list[c_ptr.tptr].tval <= Constants.TV_MAX_VISIBLE)) {
								c_ptr.fm = true;
							}
						}
					}
				}
			}
		}
		m1.prt_map();
	}
	
	/* Identify an object					-RAK-	*/
	public boolean ident_spell() {
		IntPointer item_val = new IntPointer();
		String out_val, tmp_str;
		boolean ident;
		InvenType i_ptr;
		
		ident = false;
		if (mor1.get_item(item_val, "Item you wish identified?", 0, Constants.INVEN_ARRAY_SIZE, "", "")) {
			ident = true;
			desc.identify(item_val);
			i_ptr = t.inventory[item_val.value()];
			desc.known2(i_ptr);
			tmp_str = desc.objdes(i_ptr, true);
			if (item_val.value() >= Constants.INVEN_WIELD) {
				mor1.calc_bonuses();
				out_val = String.format("%s: %s", mor1.describe_use(item_val.value()), tmp_str);
			} else {
				out_val = String.format("%c %s", item_val.value() + 97, tmp_str);
			}
			io.msg_print(out_val);
		}
		return ident;
	}
	
	/* Get all the monsters on the level pissed off.	-RAK-	*/
	public boolean aggravate_monster(int dis_affect) {
		int i;
		boolean aggravate;
		MonsterType m_ptr;
		
		aggravate = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			m_ptr.csleep = 0;
			if ((m_ptr.cdis <= dis_affect) && (m_ptr.cspeed < 2)) {
				m_ptr.cspeed++;
				aggravate = true;
			}
		}
		if (aggravate) {
			io.msg_print("You hear a sudden stirring in the distance!");
		}
		return aggravate;
	}
	
	/* Surround the fool with traps (chuckle)		-RAK-	*/
	public boolean trap_creation() {
		int i, j;
		boolean trap;
		CaveType c_ptr;
		
		trap = true;
		for (i = py.char_row - 1; i <= py.char_row + 1; i++) {
			for (j = py.char_col - 1; j <= py.char_col + 1; j++) {
				/* Don't put a trap under the player, since this can lead to
				 * strange situations, e.g. falling through a trap door while
				 * trying to rest, setting off a falling rock trap and ending
				 * up under the rock.  */
				if (i == py.char_row && j == py.char_col) {
					continue;
				}
				c_ptr = var.cave[i][j];
				if (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
					if (c_ptr.tptr != 0) {
						mor3.delete_object(i, j);
					}
					m3.place_trap(i, j, m1.randint(Constants.MAX_TRAP) - 1);
					/* don't let player gain exp from the newly created traps */
					t.t_list[c_ptr.tptr].p1 = 0;
					/* open pits are immediately visible, so call mor1.lite_spot */
					mor1.lite_spot(i, j);
				}
			}
		}
		return trap;
	}
	
	/* Surround the player with doors.			-RAK-	*/
	public boolean door_creation() {
		int i, j;
		boolean door;
		int k;
		CaveType c_ptr;
		
		door = false;
		for (i = py.char_row - 1; i <= py.char_row + 1; i++) {
			for (j = py.char_col - 1; j <= py.char_col + 1; j++) {
				if ((i != py.char_row) || (j != py.char_col)) {
					c_ptr = var.cave[i][j];
					if (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
						door = true;
						if (c_ptr.tptr != 0) {
							mor3.delete_object(i, j);
						}
						k = m1.popt();
						c_ptr.fval = Constants.BLOCKED_FLOOR;
						c_ptr.tptr = k;
						desc.invcopy(t.t_list[k], Constants.OBJ_CLOSED_DOOR);
						mor1.lite_spot(i, j);
					}
				}
			}
		}
		return door;
	}
	
	/* Destroys any adjacent door(s)/trap(s)		-RAK-	*/
	public boolean td_destroy() {
		int i, j;
		boolean destroy;
		CaveType c_ptr;
		
		destroy = false;
		for (i = py.char_row - 1; i <= py.char_row + 1; i++) {
			for (j = py.char_col - 1; j <= py.char_col + 1; j++) {
				c_ptr = var.cave[i][j];
				if (c_ptr.tptr != 0) {
					if (((t.t_list[c_ptr.tptr].tval >= Constants.TV_INVIS_TRAP)
							&& (t.t_list[c_ptr.tptr].tval <= Constants.TV_CLOSED_DOOR)
							&& (t.t_list[c_ptr.tptr].tval != Constants.TV_RUBBLE))
							|| (t.t_list[c_ptr.tptr].tval == Constants.TV_SECRET_DOOR)) {
						if (mor3.delete_object(i, j)) {
							destroy = true;
						}
					} else if (t.t_list[c_ptr.tptr].tval == Constants.TV_CHEST
							&& t.t_list[c_ptr.tptr].flags != 0) {
						/* destroy traps on chest and unlock */
						t.t_list[c_ptr.tptr].flags &= ~(Constants.CH_TRAPPED | Constants.CH_LOCKED);
						t.t_list[c_ptr.tptr].name2 = Constants.SN_UNLOCKED;
						io.msg_print("You have disarmed the chest.");
						desc.known2(t.t_list[c_ptr.tptr]);
						destroy = true;
					}
				}
			}
		}
		return destroy;
	}
	
	/* Display all creatures on the current panel		-RAK-	*/
	public boolean detect_monsters() {
		int i;
		boolean detect;
		MonsterType m_ptr;
		
		detect = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			if (m1.panel_contains(m_ptr.fy, m_ptr.fx) && ((Constants.CM_INVISIBLE & mon.c_list[m_ptr.mptr].cmove) == 0)) {
				m_ptr.ml = true;
				/* works correctly even if hallucinating */
				io.print(mon.c_list[m_ptr.mptr].cchar, m_ptr.fy, m_ptr.fx);
				detect = true;
			}
		}
		if (detect) {
			io.msg_print("You sense the presence of monsters!");
			io.msg_print("");
			/* must unlight every monster just lighted */
			creature.creatures(false);
		}
		return detect;
	}
	
	/* Leave a line of light in given dir, blue light can sometimes	*/
	/* hurt creatures.				       -RAK-   */
	public void light_line(int dir, int y, int x) {
		int i;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		int dist;
		boolean flag;
		String out_val, m_name;
		
		dist = -1;
		flag = false;
		do {
			/* put mmove at end because want to light up current spot */
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else {
				if (c_ptr.pl == false && c_ptr.tl == false) {
					/* set pl so that mor1.lite_spot will work */
					c_ptr.pl = true;
					if (c_ptr.fval == Constants.LIGHT_FLOOR) {
						if (m1.panel_contains(y1.value(), x1.value())) {
							mor1.light_room(y1.value(), x1.value());
						}
					} else {
						mor1.lite_spot(y1.value(), x1.value());
					}
				}
				/* set pl in case tl was true above */
				c_ptr.pl = true;
				if (c_ptr.cptr > 1) {
					m_ptr = mon.m_list[c_ptr.cptr];
					r_ptr = mon.c_list[m_ptr.mptr];
					/* light up and draw monster */
					creature.update_mon(c_ptr.cptr);
					m_name = monster_name(m_ptr, r_ptr);
					if ((Constants.CD_LIGHT & r_ptr.cdefense) > 0) {
						if (m_ptr.ml) {
							var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_LIGHT;
						}
						i = mor3.mon_take_hit(c_ptr.cptr, m1.damroll(2, 8));
						if (i >= 0) {
							out_val = String.format("%s shrivels away in the light!", m_name);
							io.msg_print(out_val);
							m3.prt_experience();
						} else {
							out_val = String.format("%s cringes from the light!", m_name);
							io.msg_print(out_val);
						}
					}
				}
			}
			m3.mmove(dir, y1, x1);
		} while (!flag);
	}
	
	/* Light line in all directions				-RAK-	*/
	public void starlite(int y, int x) {
		int i;
		
		if (py.py.flags.blind < 1) {
			io.msg_print("The end of the staff bursts into a blue shimmering light.");
		}
		for (i = 1; i <= 9; i++) {
			if (i != 5) {
				light_line(i, y, x);
			}
		}
	}
	
	/* Disarms all traps/chests in a given direction	-RAK-	*/
	public boolean disarm_all(int dir, int y, int x) {
		CaveType c_ptr;
		InvenType t_ptr;
		boolean disarm;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		
		disarm = false;
		dist = -1;
		do {
			/* put m3.mmove at end, in case standing on a trap */
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			/* note, must continue up to and including the first non open space,
			 * because secret doors have fval greater than MAX_OPEN_SPACE */
			if (c_ptr.tptr != 0) {
				t_ptr = t.t_list[c_ptr.tptr];
				if ((t_ptr.tval == Constants.TV_INVIS_TRAP) || (t_ptr.tval == Constants.TV_VIS_TRAP)) {
					if (mor3.delete_object(y1.value(), x1.value())) {
						disarm = true;
					}
				} else if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
					t_ptr.p1 = 0;  /* Locked or jammed doors become merely closed. */
				} else if (t_ptr.tval == Constants.TV_SECRET_DOOR) {
						c_ptr.fm = true;
						mor2.change_trap(y1.value(), x1.value());
						disarm = true;
				} else if ((t_ptr.tval == Constants.TV_CHEST) && (t_ptr.flags != 0)) {
					io.msg_print("Click!");
					t_ptr.flags &= ~(Constants.CH_TRAPPED | Constants.CH_LOCKED);
					disarm = true;
					t_ptr.name2 = Constants.SN_UNLOCKED;
					desc.known2(t_ptr);
				}
			}
			m3.mmove(dir, y1, x1);
		} while ((dist <= Constants.OBJ_BOLT_RANGE) && c_ptr.fval <= Constants.MAX_OPEN_SPACE);
		return disarm;
	}
	
	/* Return flags for given type area affect		-RAK-	*/
	public void get_flags(int typ, IntPointer weapon_type, IntPointer harm_type, IntPointer destroy) {
		switch(typ)
		{
		case Constants.GF_MAGIC_MISSILE:
			weapon_type.value(0);
			harm_type.value(  0);
			destroy.value(    0);	//set_null;
			break;
		case Constants.GF_LIGHTNING:
			weapon_type.value(Constants.CS_BR_LIGHT);
			harm_type.value(  Constants.CD_LIGHT);
			destroy.value(    1);	//set_lightning_destroy;
			break;
		case Constants.GF_POISON_GAS:
			weapon_type.value(Constants.CS_BR_GAS);
			harm_type.value(  Constants.CD_POISON);
			destroy.value(    0);	//set_null;
			break;
		case Constants.GF_ACID:
			weapon_type.value(Constants.CS_BR_ACID);
			harm_type.value(  Constants.CD_ACID);
			destroy.value(    2);	//set_acid_destroy;
			break;
		case Constants.GF_FROST:
			weapon_type.value(Constants.CS_BR_FROST);
			harm_type.value(  Constants.CD_FROST);
			destroy.value(    3);	//set_frost_destroy;
			break;
		case Constants.GF_FIRE:
			weapon_type.value(Constants.CS_BR_FIRE);
			harm_type.value(  Constants.CD_FIRE);
			destroy.value(    4);	//set_fire_destroy;
			break;
		case Constants.GF_HOLY_ORB:
			weapon_type.value(0);
			harm_type.value(  Constants.CD_EVIL);
			destroy.value(    0);	//set_null;
			break;
		default:
			io.msg_print("ERROR in get_flags()\n");
		}
	}
	
	private boolean check_destroy(int func, InvenType item) {
		switch (func)
		{
		case 0:
			return Sets.set_null(item);
		case 1:
			return Sets.set_lightning_destroy(item);
		case 2:
			return Sets.set_acid_destroy(item);
		case 3:
			return Sets.set_frost_destroy(item);
		case 4:
			return Sets.set_fire_destroy(item);
		default:
			return false;
		}
	}
	
	/* Shoot a bolt in a given direction			-RAK-	*/
	public void fire_bolt(int typ, int dir, int y, int x, int dam, String bolt_typ) {
		int oldy, oldx, dist, i;
		boolean pl, flag;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		IntPointer weapon_type = new IntPointer(), harm_type = new IntPointer();
		IntPointer dummy = new IntPointer();
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		flag = false;
		get_flags(typ, weapon_type, harm_type, dummy);
		oldy = y;
		oldx = x;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			mor1.lite_spot(oldy, oldx);
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else {
				if (c_ptr.cptr > 1) {
					flag = true;
					m_ptr = mon.m_list[c_ptr.cptr];
					r_ptr = mon.c_list[m_ptr.mptr];
					
					/* light up monster and draw monster, temporarily set
					 * pl so that creature.update_mon() will work */
					pl = c_ptr.pl;
					c_ptr.pl = true;
					creature.update_mon(c_ptr.cptr);
					c_ptr.pl = pl;
					/* draw monster and clear previous bolt */
					io.put_qio();
					
					m_name = lower_monster_name(m_ptr, r_ptr);
					out_val = String.format("The %s strikes %s.", bolt_typ, m_name);
					io.msg_print(out_val);
					if ((harm_type.value() & r_ptr.cdefense) > 0) {
						dam *= 2;
						if (m_ptr.ml) {
							var.c_recall[m_ptr.mptr].r_cdefense |= harm_type.value();
						}
					} else if ((weapon_type.value() & r_ptr.spells) > 0) {
						dam /=  4;
						if (m_ptr.ml) {
							var.c_recall[m_ptr.mptr].r_spells |= weapon_type.value();
						}
					}
					m_name = monster_name(m_ptr, r_ptr);
					i = mor3.mon_take_hit(c_ptr.cptr, dam);
					if (i >= 0) {
						out_val = String.format("%s dies in a fit of agony.", m_name);
						io.msg_print(out_val);
						m3.prt_experience();
					} else if (dam > 0) {
						out_val = String.format("%s screams in agony.", m_name);
						io.msg_print(out_val);
					}
				} else if (m1.panel_contains(y1.value(), x1.value()) && (py.py.flags.blind < 1)) {
					io.print('*', y1.value(), x1.value());
					/* show the bolt */
					io.put_qio();
				}
			}
			oldy = y1.value();
			oldx = x1.value();
		} while (!flag);
	}
	
	/* Shoot a ball in a given direction.  Note that balls have an	*/
	/* area affect.					      -RAK-   */
	public void fire_ball(int typ, int dir, int y, int x, int dam_hp, String descrip) {
		int i, j;
		int dam, max_dis, thit, tkill, k;
		int oldy, oldx, dist;
		boolean flag, tmp;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		IntPointer harm_type = new IntPointer();
		IntPointer weapon_type = new IntPointer();
		IntPointer destroy = new IntPointer();
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val;
		
		thit	= 0;
		tkill	= 0;
		max_dis	= 2;
		get_flags(typ, weapon_type, harm_type, destroy);
		flag = false;
		oldy = y;
		oldx = x;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			mor1.lite_spot(oldy, oldx);
			if (dist > Constants.OBJ_BOLT_RANGE) {
				flag = true;
			} else {
				c_ptr = var.cave[y1.value()][x1.value()];
				if ((c_ptr.fval >= Constants.MIN_CLOSED_SPACE) || (c_ptr.cptr > 1)) {
					flag = true;
					if (c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
						y1.value(oldy);
						x1.value(oldx);
					}
					/* The ball hits and explodes.		     */
					/* The explosion.			     */
					for (i = y1.value() - max_dis; i <= y1.value() + max_dis; i++) {
						for (j = x1.value() - max_dis; j <= x1.value() + max_dis; j++) {
							if (m1.in_bounds(i, j) && (m1.distance(y1.value(), x1.value(), i, j) <= max_dis) && m1.los(y1.value(), x1.value(), i, j)) {
								c_ptr = var.cave[i][j];
								if ((c_ptr.tptr != 0) && check_destroy(destroy.value(), t.t_list[c_ptr.tptr])) {
									mor3.delete_object(i, j);
								}
								if (c_ptr.fval <= Constants.MAX_OPEN_SPACE) {
									if (c_ptr.cptr > 1) {
										m_ptr = mon.m_list[c_ptr.cptr];
										r_ptr = mon.c_list[m_ptr.mptr];
										
										/* lite up creature if visible, temp
										 * set pl so that creature.update_mon works */
										tmp = c_ptr.pl;
										c_ptr.pl = true;
										creature.update_mon(c_ptr.cptr);
										
										thit++;
										dam = dam_hp;
										if ((harm_type.value() & r_ptr.cdefense) > 0) {
											dam *= 2;
											if (m_ptr.ml) {
												var.c_recall[m_ptr.mptr].r_cdefense |= harm_type.value();
											}
										} else if ((weapon_type.value() & r_ptr.spells) > 0) {
											dam /= 4;
											if (m_ptr.ml) {
												var.c_recall[m_ptr.mptr].r_spells |= weapon_type.value();
											}
										}
										dam = (dam / (m1.distance(i, j, y1.value(), x1.value()) + 1));
										k = mor3.mon_take_hit(c_ptr.cptr, dam);
										if (k >= 0) {
											tkill++;
										}
										c_ptr.pl = tmp;
									} else if (m1.panel_contains(i, j) && (py.py.flags.blind < 1)) {
										io.print('*', i, j);
									}
								}
							}
						}
					}
					/* show ball of whatever */
					io.put_qio();
					
					for (i = (y1.value() - 2); i <= (y1.value() + 2); i++) {
						for (j = (x1.value() - 2); j <= (x1.value() + 2); j++) {
							if (m1.in_bounds(i, j) && m1.panel_contains(i, j) && (m1.distance(y1.value(), x1.value(), i, j) <= max_dis)) {
								mor1.lite_spot(i, j);
							}
						}
					}
					
					/* End  explosion.		     */
					if (thit == 1) {
						out_val = String.format("The %s envelops a creature!", descrip);
						io.msg_print(out_val);
					} else if (thit > 1) {
						out_val = String.format("The %s envelops several creatures!", descrip);
						io.msg_print(out_val);
					}
					if (tkill == 1) {
						io.msg_print("There is a scream of agony!");
					} else if (tkill > 1) {
						io.msg_print("There are several screams of agony!");
					}
					if (tkill >= 0) {
						m3.prt_experience();
					}
					/* End ball hitting.		     */
				} else if (m1.panel_contains(y1.value(), x1.value()) && (py.py.flags.blind < 1)) {
					io.print('*', y1.value(), x1.value());
					/* show bolt */
					io.put_qio();
				}
				oldy = y1.value();
				oldx = x1.value();
			}
		} while (!flag);
	}
	
	/* Breath weapon works like a fire_ball, but affects the player. */
	/* Note the area affect.			      -RAK-   */
	public void breath(int typ, int y, int x, int dam_hp, String ddesc, int monptr) {
		int i, j;
		int dam, max_dis;
		IntPointer harm_type = new IntPointer();
		IntPointer weapon_type = new IntPointer();
		IntPointer destroy = new IntPointer();
		long tmp, treas;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		max_dis = 2;
		get_flags(typ, weapon_type, harm_type, destroy);
		for (i = y - 2; i <= y + 2; i++) {
			for (j = x - 2; j <= x + 2; j++) {
				if (m1.in_bounds(i, j) && (m1.distance(y, x, i, j) <= max_dis) && m1.los(y, x, i, j)) {
					c_ptr = var.cave[i][j];
					if (c_ptr.tptr != 0 && check_destroy(destroy.value(), t.t_list[c_ptr.tptr])) {
						mor3.delete_object(i, j);
					}
					if (c_ptr.fval <= Constants.MAX_OPEN_SPACE) {
						/* must test status bit, not py.flags.blind here, flag could have
						 * been set by a previous monster, but the breath should still
						 * be visible until the blindness takes effect */
						if (m1.panel_contains(i, j) && !((py.py.flags.status & Constants.PY_BLIND) > 0)) {
							io.print('*', i, j);
						}
						if (c_ptr.cptr > 1) {
							m_ptr = mon.m_list[c_ptr.cptr];
							r_ptr = mon.c_list[m_ptr.mptr];
							dam = dam_hp;
							if ((harm_type.value() & r_ptr.cdefense) > 0) {
								dam *= 2;
							} else if ((weapon_type.value() & r_ptr.spells) > 0) {
								dam /= 4;
							}
							dam /= (m1.distance(i, j, y, x) + 1);
							/* can not call mor3.mon_take_hit here, since player does not
							 * get experience for kill */
							m_ptr.hp -= dam;
							m_ptr.csleep = 0;
							if (m_ptr.hp < 0) {
								treas = mor3.monster_death(m_ptr.fy, m_ptr.fx, r_ptr.cmove);
								if (m_ptr.ml) {
									tmp = (var.c_recall[m_ptr.mptr].r_cmove & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT;
									if (tmp > ((treas & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT)) {
										treas = (treas & ~Constants.CM_TREASURE) | (tmp << Constants.CM_TR_SHIFT);
									}
									var.c_recall[m_ptr.mptr].r_cmove = treas | (var.c_recall[m_ptr.mptr].r_cmove & ~Constants.CM_TREASURE);
								}
								
								/* It ate an already processed monster.Handle normally.*/
								if (monptr < c_ptr.cptr) {
									mor3.delete_monster(c_ptr.cptr);
								/* If it eats this monster, an already processed monster
								 * will take its place, causing all kinds of havoc.
								 * Delay the kill a bit. */
								} else {
									mor3.fix1_delete_monster(c_ptr.cptr);
								}
							}
						} else if (c_ptr.cptr == 1) {
							dam = (dam_hp / (m1.distance(i, j, y, x) + 1));
							/* let's do at least one point of damage */
							/* prevents randint(0) problem with poison_gas, also */
							if (dam == 0) {
								dam = 1;
							}
							switch(typ)
							{
							case Constants.GF_LIGHTNING: mor2.light_dam(dam, ddesc); break;
							case Constants.GF_POISON_GAS: mor2.poison_gas(dam, ddesc); break;
							case Constants.GF_ACID: mor2.acid_dam(dam, ddesc); break;
							case Constants.GF_FROST: mor2.cold_dam(dam, ddesc); break;
							case Constants.GF_FIRE: mor2.fire_dam(dam, ddesc); break;
							}
						}
					}
				}
			}
		}
		/* show the ball of gas */
		io.put_qio();
		
		for (i = (y - 2); i <= (y + 2); i++) {
			for (j = (x - 2); j <= (x + 2); j++) {
				if (m1.in_bounds(i, j) && m1.panel_contains(i, j) && (m1.distance(y, x, i, j) <= max_dis)) {
					mor1.lite_spot(i, j);
				}
			}
		}
	}
	
	/* Recharge a wand, staff, or rod.  Sometimes the item breaks. -RAK-*/
	public boolean recharge(int num) {
		IntPointer i = new IntPointer(), j = new IntPointer();
		IntPointer item_val = new IntPointer();
		boolean res;
		InvenType i_ptr;
		
		res = false;
		if (!m3.find_range(Constants.TV_STAFF, Constants.TV_WAND, i, j)) {
			io.msg_print("You have nothing to recharge.");
		} else if (mor1.get_item(item_val, "Recharge which item?", i.value(), j.value(), "", "")) {
			i_ptr = t.inventory[item_val.value()];
			res = true;
			/* recharge I = recharge(20) = 1/6 failure for empty 10th level wand */
			/* recharge II = recharge(60) = 1/10 failure for empty 10th level wand*/
			/* make it harder to recharge high level, and highly charged wands, note
			 * that i can be negative, so check its value before trying to call
			 * randint().  */
			i.value(num + 50 - i_ptr.level - i_ptr.p1);
			if (i.value() < 19) {
				i.value(1);	/* Automatic failure.  */
			} else {
				i.value(m1.randint(i.value() / 10));
			}
			if (i.value() == 1) {
				io.msg_print("There is a bright flash of light.");
				m3.inven_destroy(item_val.value());
			} else {
				num = (num / (i_ptr.level + 2)) + 1;
				i_ptr.p1 += 2 + m1.randint(num);
				if (desc.known2_p(i_ptr)) {
					desc.clear_known2(i_ptr);
				}
				desc.clear_empty(i_ptr);
			}
		}
		return res;
	}
	
	/* Increase or decrease a creatures hit points		-RAK-	*/
	public boolean hp_monster(int dir, int y, int x, int dam) {
		int i;
		boolean flag, monster;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		monster = false;
		flag = false;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				flag = true;
				m_ptr = mon.m_list[c_ptr.cptr];
				r_ptr = mon.c_list[m_ptr.mptr];
				m_name = monster_name(m_ptr, r_ptr);
				monster = true;
				i = mor3.mon_take_hit(c_ptr.cptr, dam);
				if (i >= 0) {
					out_val = String.format("%s dies in a fit of agony.", m_name);
					io.msg_print(out_val);
					m3.prt_experience();
				} else if (dam > 0) {
					out_val = String.format("%s screams in agony.", m_name);
					io.msg_print(out_val);
				}
			}
		} while (!flag);
		return monster;
	}
	
	/* Drains life; note it must be living.		-RAK-	*/
	public boolean drain_life(int dir, int y, int x) {
		int i;
		boolean flag, drain;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		drain = false;
		flag = false;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				flag = true;
				m_ptr = mon.m_list[c_ptr.cptr];
				r_ptr = mon.c_list[m_ptr.mptr];
				if ((r_ptr.cdefense & Constants.CD_UNDEAD) == 0) {
					drain = true;
					m_name = monster_name(m_ptr, r_ptr);
					i = mor3.mon_take_hit(c_ptr.cptr, 75);
					if (i >= 0) {
						out_val = String.format("%s dies in a fit of agony.", m_name);
						io.msg_print(out_val);
						m3.prt_experience();
					} else {
						out_val = String.format("%s screams in agony.", m_name);
						io.msg_print(out_val);
					}
				} else {
					var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_UNDEAD;
				}
			}
		} while (!flag);
		return drain;
	}
	
	/* Increase or decrease a creatures speed		-RAK-	*/
	/* NOTE: cannot slow a winning creature (BALROG)		 */
	public boolean speed_monster(int dir, int y, int x, int spd) {
		boolean flag, speed;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		speed = false;
		flag = false;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				flag = true;
				m_ptr = mon.m_list[c_ptr.cptr];
				r_ptr = mon.c_list[m_ptr.mptr];
				m_name = monster_name(m_ptr, r_ptr);
				if (spd > 0) {
					m_ptr.cspeed += spd;
					m_ptr.csleep = 0;
					out_val = String.format("%s starts moving faster.", m_name);
					io.msg_print(out_val);
					speed = true;
				} else if (m1.randint(Constants.MAX_MONS_LEVEL) > r_ptr.level) {
					m_ptr.cspeed += spd;
					m_ptr.csleep = 0;
					out_val = String.format("%s starts moving slower.", m_name);
					io.msg_print(out_val);
					speed = true;
				} else {
					m_ptr.csleep = 0;
					out_val = String.format("%s is unaffected.", m_name);
					io.msg_print(out_val);
				}
			}
		} while (!flag);
		return speed;
	}
	
	/* Confuse a creature					-RAK-	*/
	public boolean confuse_monster(int dir, int y, int x) {
		boolean flag, confuse;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		confuse = false;
		flag = false;
		dist = 0;
		do	{
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				m_ptr = mon.m_list[c_ptr.cptr];
				r_ptr = mon.c_list[m_ptr.mptr];
				m_name = monster_name(m_ptr, r_ptr);
				flag = true;
				if ((m1.randint(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) > 0) {
					if (m_ptr.ml && (r_ptr.cdefense & Constants.CD_NO_SLEEP) > 0) {
						var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_NO_SLEEP;
					}
					/* Monsters which resisted the attack should wake up.
					 * Monsters with innate resistance ignore the attack.  */
					if ((Constants.CD_NO_SLEEP & r_ptr.cdefense) == 0) {
						m_ptr.csleep = 0;
					}
					out_val = String.format("%s is unaffected.", m_name);
					io.msg_print(out_val);
				} else {
					if (m_ptr.confused > 0) {
						m_ptr.confused += 3;
					} else {
						m_ptr.confused = 2 + m1.randint(16);
					}
					confuse = true;
					m_ptr.csleep = 0;
					out_val = String.format("%s appears confused.", m_name);
					io.msg_print(out_val);
				}
			}
		} while (!flag);
		return confuse;
	}
	
	/* Sleep a creature.					-RAK-	*/
	public boolean sleep_monster(int dir, int y, int x) {
		boolean flag, sleep;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		sleep = false;
		flag = false;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				m_ptr = mon.m_list[c_ptr.cptr];
				r_ptr = mon.c_list[m_ptr.mptr];
				flag = true;
				m_name = monster_name(m_ptr, r_ptr);
				if ((m1.randint(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) > 0) {
					if (m_ptr.ml && (r_ptr.cdefense & Constants.CD_NO_SLEEP) > 0) {
						var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_NO_SLEEP;
					}
					out_val = String.format("%s is unaffected.", m_name);
					io.msg_print(out_val);
				} else {
					m_ptr.csleep = 500;
					sleep = true;
					out_val = String.format("%s falls asleep.", m_name);
					io.msg_print(out_val);
				}
			}
		} while (!flag);
		return sleep;
	}
	
	/* Turn stone to mud, delete wall.			-RAK-	*/
	public boolean wall_to_mud(int dir, int y, int x) {
		int i, dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean wall;
		String out_val, tmp_str;
		boolean flag;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String m_name;
		
		wall = false;
		flag = false;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			x = x1.value();
			y = y1.value();
			dist++;
			c_ptr = var.cave[y][x];
			/* note, this ray can move through walls as it turns them to mud */
			if (dist == Constants.OBJ_BOLT_RANGE) {
				flag = true;
			}
			if ((c_ptr.fval >= Constants.MIN_CAVE_WALL) && (c_ptr.fval != Constants.BOUNDARY_WALL)) {
				flag = true;
				mor3.twall(y, x, 1, 0);
				if (m1.test_light(y, x)) {
					io.msg_print("The wall turns into mud.");
					wall = true;
				}
			} else if ((c_ptr.tptr != 0) && (c_ptr.fval >= Constants.MIN_CLOSED_SPACE)) {
				flag = true;
				if (m1.panel_contains(y, x) && m1.test_light(y, x)) {
					tmp_str = desc.objdes(t.t_list[c_ptr.tptr], false);
					out_val = String.format("The %s turns into mud.", tmp_str);
					io.msg_print(out_val);
					wall = true;
				}
				if (t.t_list[c_ptr.tptr].tval == Constants.TV_RUBBLE) {
					mor3.delete_object(y, x);
					if (m1.randint(10) == 1) {
						m3.place_object(y, x, false);
						if (m1.test_light(y, x)) {
							io.msg_print("You have found something!");
						}
					}
					mor1.lite_spot(y, x);
				} else {
					mor3.delete_object(y, x);
				}
			}
			if (c_ptr.cptr > 1) {
				m_ptr = mon.m_list[c_ptr.cptr];
				r_ptr = mon.c_list[m_ptr.mptr];
				if ((Constants.CD_STONE & r_ptr.cdefense) > 0) {
					m_name = monster_name(m_ptr, r_ptr);
					i = mor3.mon_take_hit(c_ptr.cptr, 100);
					/* Should get these messages even if the monster is not
					   visible.  */
					if (i >= 0) {
						var.c_recall[i].r_cdefense |= Constants.CD_STONE;
						out_val = String.format("%s dissolves!", m_name);
						io.msg_print(out_val);
						m3.prt_experience(); /* print msg before calling prt_exp */
					} else {
						var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_STONE;
						out_val = String.format("%s grunts in pain!", m_name);
						io.msg_print(out_val);
					}
					flag = true;
				}
			}
		} while (!flag);
		return wall;
	}
	
	/* Destroy all traps and doors in a given direction	-RAK-	*/
	public boolean td_destroy2(int dir, int y, int x) {
		boolean destroy2;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		InvenType t_ptr;
		
		destroy2 = false;
		dist= 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			/* must move into first closed spot, as it might be a secret door */
			if (c_ptr.tptr != 0) {
				t_ptr = t.t_list[c_ptr.tptr];
				if ((t_ptr.tval == Constants.TV_INVIS_TRAP) || (t_ptr.tval == Constants.TV_CLOSED_DOOR)
						|| (t_ptr.tval == Constants.TV_VIS_TRAP) || (t_ptr.tval == Constants.TV_OPEN_DOOR)
						|| (t_ptr.tval == Constants.TV_SECRET_DOOR)) {
					if (mor3.delete_object(y1.value(), x1.value())) {
						io.msg_print("There is a bright flash of light!");
						destroy2 = true;
					}
				} else if (t_ptr.tval == Constants.TV_CHEST && t_ptr.flags != 0) {
					io.msg_print("Click!");
					t_ptr.flags &= ~(Constants.CH_TRAPPED|Constants.CH_LOCKED);
					destroy2 = true;
					t_ptr.name2 = Constants.SN_UNLOCKED;
					desc.known2(t_ptr);
				}
			}
		} while ((dist <= Constants.OBJ_BOLT_RANGE) || c_ptr.fval <= Constants.MAX_OPEN_SPACE);
		return destroy2;
	}
	
	/* Polymorph a monster					-RAK-	*/
	/* NOTE: cannot polymorph a winning creature (BALROG)		 */
	public boolean poly_monster(int dir, int y, int x) {
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean flag, poly;
		CaveType c_ptr;
		CreatureType r_ptr;
		MonsterType m_ptr;
		String out_val, m_name;
		
		poly = false;
		flag = false;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				m_ptr = mon.m_list[c_ptr.cptr];
				r_ptr = mon.c_list[m_ptr.mptr];
				if (m1.randint(Constants.MAX_MONS_LEVEL) > r_ptr.level) {
					flag = true;
					mor3.delete_monster(c_ptr.cptr);
					/* Place_monster() should always return true here.  */
					poly = m1.place_monster(y1.value(), x1.value(), m1.randint(mon.m_level[Constants.MAX_MONS_LEVEL] - mon.m_level[0]) - 1 + mon.m_level[0], false);
					/* don't test c_ptr.fm here, only pl/tl */
					if (poly && m1.panel_contains(y, x) && (c_ptr.tl || c_ptr.pl)) {
						poly = true;
					}
				} else {
					m_name = monster_name(m_ptr, r_ptr);
					out_val = String.format("%s is unaffected.", m_name);
					io.msg_print(out_val);
				}
			}
		} while (!flag);
		return poly;
	}
	
	/* Create a wall.					-RAK-	*/
	public boolean build_wall(int dir, int y, int x) {
		int i = 0;
		int damage, dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean build, flag;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String m_name, out_val;
		
		build = false;
		dist = 0;
		flag = false;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else {
				if (c_ptr.tptr != 0) {
					mor3.delete_object(y1.value(), x1.value());
				}
				if (c_ptr.cptr > 1) {
					/* stop the wall building */
					flag = true;
					m_ptr = mon.m_list[c_ptr.cptr];
					r_ptr = mon.c_list[m_ptr.mptr];
					
					if ((r_ptr.cmove & Constants.CM_PHASE) == 0) {
						/* monster does not move, can't escape the wall */
						if ((r_ptr.cmove & Constants.CM_ATTACK_ONLY) != 0) {
							damage = 3000; /* this will kill everything */
						} else {
							damage = m1.damroll(4, 8);
						}
						
						m_name = monster_name(m_ptr, r_ptr);
						out_val = String.format("%s wails out in pain!", m_name);
						io.msg_print(out_val);
						i = mor3.mon_take_hit(c_ptr.cptr, damage);
						if (i >= 0) {
							out_val = String.format("%s is embedded in the rock.", m_name);
							io.msg_print(out_val);
							m3.prt_experience();
						}
					} else if (r_ptr.cchar == 'E' || r_ptr.cchar == 'X') {
						/* must be an earth elemental or an earth spirit, or a Xorn
						 * increase its hit points */
						m_ptr.hp += m1.damroll(4, 8);
					}
				}
				c_ptr.fval = Constants.MAGMA_WALL;
				c_ptr.fm = false;
				/* Permanently light this wall if it is lit by player's lamp.  */
				c_ptr.pl = (c_ptr.tl || c_ptr.pl);
				mor1.lite_spot(y1.value(), x1.value());
				i++;
				build = true;
			}
		} while (!flag);
		return build;
	}
	
	/* Replicate a creature					-RAK-	*/
	public boolean clone_monster(int dir, int y, int x) {
		CaveType c_ptr;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean flag;
		
		dist = 0;
		flag = false;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				mon.m_list[c_ptr.cptr].csleep = 0;
				/* monptr of 0 is safe here, since can't reach here from creatures */
				return creature.multiply_monster(y1.value(), x1.value(), mon.m_list[c_ptr.cptr].mptr, 0);
			}
		} while (!flag);
		return false;
	}
	
	/* Move the creature record to a new location		-RAK-	*/
	public void teleport_away(int monptr, int dis) {
		int yn, xn, ctr;
		MonsterType m_ptr;
		
		m_ptr = mon.m_list[monptr];
		ctr = 0;
		do {
			do {
				yn = m_ptr.fy + (m1.randint(2 * dis + 1) - (dis + 1));
				xn = m_ptr.fx + (m1.randint(2 * dis + 1) - (dis + 1));
			} while (!m1.in_bounds(yn, xn));
			ctr++;
			if (ctr > 9) {
				ctr = 0;
				dis += 5;
			}
		} while ((var.cave[yn][xn].fval >= Constants.MIN_CLOSED_SPACE) || (var.cave[yn][xn].cptr != 0));
		mor1.move_rec(m_ptr.fy, m_ptr.fx, yn, xn);
		mor1.lite_spot(m_ptr.fy, m_ptr.fx);
		m_ptr.fy = yn;
		m_ptr.fx = xn;
		/* this is necessary, because the creature is not currently visible
	     in its new position */
		m_ptr.ml = false;
		m_ptr.cdis = m1.distance(py.char_row, py.char_col, yn, xn);
		creature.update_mon(monptr);
	}
	
	/* Teleport player to spell casting creature		-RAK-	*/
	public void teleport_to(int ny, int nx) {
		int dis, ctr, y, x;
		int i, j;
		CaveType c_ptr;
		
		dis = 1;
		ctr = 0;
		do {
			y = ny + (m1.randint(2 * dis + 1) - (dis + 1));
			x = nx + (m1.randint(2 * dis + 1) - (dis + 1));
			ctr++;
			if (ctr > 9) {
				ctr = 0;
				dis++;
			}
		} while (!m1.in_bounds(y, x) || (var.cave[y][x].fval >= Constants.MIN_CLOSED_SPACE) || (var.cave[y][x].cptr >= 2));
		mor1.move_rec(py.char_row, py.char_col, y, x);
		for (i = py.char_row - 1; i <= py.char_row + 1; i++) {
			for (j = py.char_col - 1; j <= py.char_col + 1; j++) {
				c_ptr = var.cave[i][j];
				c_ptr.tl = false;
				mor1.lite_spot(i, j);
			}
		}
		mor1.lite_spot(py.char_row, py.char_col);
		py.char_row = y;
		py.char_col = x;
		m4.check_view();
		/* light creatures */
		creature.creatures(false);
	}
	
	/* Teleport all creatures in a given direction away	-RAK-	*/
	public boolean teleport_monster(int dir, int y, int x) {
		boolean flag, result;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		
		flag = false;
		result = false;
		dist = 0;
		do {
			m3.mmove(dir, y1, x1);
			dist++;
			c_ptr = var.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.cptr > 1) {
				mon.m_list[c_ptr.cptr].csleep = 0; /* wake it up */
				teleport_away(c_ptr.cptr, Constants.MAX_SIGHT);
				result = true;
			}
		} while (!flag);
		return result;
	}
	
	/* Delete all creatures within max_sight distance	-RAK-	*/
	/* NOTE : Winning creatures cannot be genocided			 */
	public boolean mass_genocide() {
		int i;
		boolean result;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		result = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			r_ptr = mon.c_list[m_ptr.mptr];
			if ((m_ptr.cdis <= Constants.MAX_SIGHT) && ((r_ptr.cmove & Constants.CM_WIN) == 0)) {
				mor3.delete_monster(i);
				result = true;
			}
		}
		return result;
	}
	
	/* Delete all creatures of a given type from level.	-RAK-	*/
	/* This does not keep creatures of type from appearing later.	 */
	/* NOTE : Winning creatures can not be genocided. */
	public boolean genocide() {
		int i;
		boolean killed;
		CharPointer typ = new CharPointer();
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val;
		
		killed = false;
		if (io.get_com("Which type of creature do you wish exterminated?", typ)) {
			for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
				m_ptr = mon.m_list[i];
				r_ptr = mon.c_list[m_ptr.mptr];
				if (typ.value() == mon.c_list[m_ptr.mptr].cchar) {
					if ((r_ptr.cmove & Constants.CM_WIN) == 0) {
						mor3.delete_monster(i);
						killed = true;
					} else {
						/* genocide is a powerful spell, so we will let the player
						 * know the names of the creatures he did not destroy,
						 * this message makes no sense otherwise */
						out_val = String.format("The %s is unaffected.", r_ptr.name);
						io.msg_print(out_val);
					}
				}
			}
		}
		return killed;
	}
	
	/* Change speed of any creature .			-RAK-	*/
	/* NOTE: cannot slow a winning creature (BALROG)		 */
	public boolean speed_monsters(int spd) {
		int i;
		boolean speed;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		speed = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			r_ptr = mon.c_list[m_ptr.mptr];
			m_name = monster_name(m_ptr, r_ptr);
			
			if ((m_ptr.cdis > Constants.MAX_SIGHT) || !m1.los(py.char_row, py.char_col, m_ptr.fy, m_ptr.fx)) {
				/* do nothing */
				;
			} else if (spd > 0) {
				m_ptr.cspeed += spd;
				m_ptr.csleep = 0;
				if (m_ptr.ml) {
					speed = true;
					out_val = String.format("%s starts moving faster.", m_name);
					io.msg_print (out_val);
				}
			} else if (m1.randint(Constants.MAX_MONS_LEVEL) > r_ptr.level) {
				m_ptr.cspeed += spd;
				m_ptr.csleep = 0;
				if (m_ptr.ml) {
					out_val = String.format("%s starts moving slower.", m_name);
					io.msg_print(out_val);
					speed = true;
				}
			} else if (m_ptr.ml) {
				m_ptr.csleep = 0;
				out_val = String.format("%s is unaffected.", m_name);
				io.msg_print(out_val);
			}
		}
		return speed;
	}
	
	/* Sleep any creature .		-RAK-	*/
	public boolean sleep_monsters2() {
		int i;
		boolean sleep;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		sleep = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			r_ptr = mon.c_list[m_ptr.mptr];
			m_name = monster_name(m_ptr, r_ptr);
			if ((m_ptr.cdis > Constants.MAX_SIGHT) || !m1.los(py.char_row, py.char_col, m_ptr.fy, m_ptr.fx)) {
				/* do nothing */
				;
			} else if ((m1.randint(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) != 0) {
				if (m_ptr.ml) {
					if ((r_ptr.cdefense & Constants.CD_NO_SLEEP) != 0) {
						var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_NO_SLEEP;
					}
					out_val = String.format("%s is unaffected.", m_name);
					io.msg_print(out_val);
				}
			} else {
				m_ptr.csleep = 500;
				if (m_ptr.ml) {
					out_val = String.format("%s falls asleep.", m_name);
					io.msg_print(out_val);
					sleep = true;
				}
			}
		}
		return sleep;
	}
	
	/* Polymorph any creature that player can see.	-RAK-	*/
	/* NOTE: cannot polymorph a winning creature (BALROG)		 */
	public boolean mass_poly() {
		int i;
		int y, x;
		boolean mass;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		mass = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			if (m_ptr.cdis <= Constants.MAX_SIGHT) {
				r_ptr = mon.c_list[m_ptr.mptr];
				if ((r_ptr.cmove & Constants.CM_WIN) == 0) {
					y = m_ptr.fy;
					x = m_ptr.fx;
					mor3.delete_monster(i);
					/* Place_monster() should always return true here.  */
					mass = m1.place_monster(y, x, m1.randint(mon.m_level[Constants.MAX_MONS_LEVEL] - mon.m_level[0]) - 1 + mon.m_level[0], false);
				}
			}
		}
		return mass;
	}
	
	/* Display evil creatures on current panel		-RAK-	*/
	public boolean detect_evil() {
		int i;
		boolean flag;
		MonsterType m_ptr;
		
		flag = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			if (m1.panel_contains(m_ptr.fy, m_ptr.fx) && (Constants.CD_EVIL & mon.c_list[m_ptr.mptr].cdefense) != 0) {
				m_ptr.ml = true;
				/* works correctly even if hallucinating */
				io.print(mon.c_list[m_ptr.mptr].cchar, m_ptr.fy, m_ptr.fx);
				flag = true;
			}
		}
		if (flag) {
			io.msg_print("You sense the presence of evil!");
			io.msg_print("");
			/* must unlight every monster just lighted */
			creature.creatures(false);
		}
		return flag;
	}
	
	/* Change players hit points in some manner		-RAK-	*/
	public boolean hp_player(int num) {
		boolean res;
		PlayerMisc m_ptr;
		
		res = false;
		m_ptr = py.py.misc;
		if (m_ptr.chp < m_ptr.mhp) {
			m_ptr.chp += num;
			if (m_ptr.chp > m_ptr.mhp) {
				m_ptr.chp = m_ptr.mhp;
				m_ptr.chp_frac = 0;
			}
			m3.prt_chp();
			
			num = num / 5;
			if (num < 3) {
				if (num == 0) io.msg_print("You feel a little better.");
				else	      io.msg_print("You feel better.");
			} else {
				if (num < 7) io.msg_print("You feel much better.");
				else	     io.msg_print("You feel very good.");
			}
			res = true;
		}
		return res;
	}
	
	/* Cure players confusion				-RAK-	*/
	public boolean cure_confusion() {
		boolean cure;
		PlayerFlags f_ptr;
		
		cure = false;
		f_ptr = py.py.flags;
		if (f_ptr.confused > 1) {
			f_ptr.confused = 1;
			cure = true;
		}
		return cure;
	}
	
	/* Cure players blindness				-RAK-	*/
	public boolean cure_blindness() {
		boolean cure;
		PlayerFlags f_ptr;
		
		cure = false;
		f_ptr = py.py.flags;
		if (f_ptr.blind > 1) {
			f_ptr.blind = 1;
			cure = true;
		}
		return cure;
	}
	
	/* Cure poisoning					-RAK-	*/
	public boolean cure_poison() {
		boolean cure;
		PlayerFlags f_ptr;
		
		cure = false;
		f_ptr = py.py.flags;
		if (f_ptr.poisoned > 1) {
			f_ptr.poisoned = 1;
			cure = true;
		}
		return cure;
	}
	
	/* Cure the players fear				-RAK-	*/
	public boolean remove_fear() {
		boolean result;
		PlayerFlags f_ptr;
		
		result = false;
		f_ptr = py.py.flags;
		if (f_ptr.afraid > 1) {
			f_ptr.afraid = 1;
			result = true;
		}
		return result;
	}
	
	/* This is a fun one.  In a given block, pick some walls and	*/
	/* turn them into open spots.  Pick some open spots and turn	 */
	/* them into walls.  An "Earthquake" effect.	       -RAK-   */
	public void earthquake() {
		int i, j;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		int damage, tmp;
		String out_val, m_name;
		
		for (i = py.char_row - 8; i <= py.char_row + 8; i++) {
			for (j = py.char_col - 8; j <= py.char_col + 8; j++) {
				if (((i != py.char_row) || (j != py.char_col)) && m1.in_bounds(i, j) && (m1.randint(8) == 1)) {
					c_ptr = var.cave[i][j];
					if (c_ptr.tptr != 0) {
						mor3.delete_object(i, j);
					}
					if (c_ptr.cptr > 1) {
						m_ptr = mon.m_list[c_ptr.cptr];
						r_ptr = mon.c_list[m_ptr.mptr];
						
						if ((r_ptr.cmove & Constants.CM_PHASE) == 0) {
							if((r_ptr.cmove & Constants.CM_ATTACK_ONLY) != 0) {
								damage = 3000; /* this will kill everything */
							} else {
								damage = m1.damroll(4, 8);
							}
							
							m_name = monster_name(m_ptr, r_ptr);
							out_val = String.format("%s wails out in pain!", m_name);
							io.msg_print (out_val);
							i = mor3.mon_take_hit(c_ptr.cptr, damage);
							if (i >= 0) {
								out_val = String.format("%s is embedded in the rock.", m_name);
								io.msg_print(out_val);
								m3.prt_experience();
							}
						} else if (r_ptr.cchar == 'E' || r_ptr.cchar == 'X') {
							/* must be an earth elemental or an earth spirit, or a Xorn
							 * increase its hit points */
							m_ptr.hp += m1.damroll(4, 8);
						}
					}
					
					if ((c_ptr.fval >= Constants.MIN_CAVE_WALL) && (c_ptr.fval != Constants.BOUNDARY_WALL)) {
						c_ptr.fval  = Constants.CORR_FLOOR;
						c_ptr.pl = false;
						c_ptr.fm = false;
					} else if (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
						tmp = m1.randint(10);
						if (tmp < 6) {
							c_ptr.fval  = Constants.QUARTZ_WALL;
						} else if (tmp < 9) {
							c_ptr.fval  = Constants.MAGMA_WALL;
						} else {
							c_ptr.fval  = Constants.GRANITE_WALL;
						}
						
						c_ptr.fm = false;
					}
					mor1.lite_spot(i, j);
				}
			}
		}
	}
	
	/* Evil creatures don't like this.		       -RAK-   */
	public boolean protect_evil() {
		boolean res;
		PlayerFlags f_ptr;
		
		f_ptr = py.py.flags;
		if (f_ptr.protevil == 0) {
			res = true;
		} else {
			res = false;
		}
		f_ptr.protevil += m1.randint(25) + 3 * py.py.misc.lev;
		return res;
	}
	
	/* Create some high quality mush for the player.	-RAK-	*/
	public void create_food() {
		CaveType c_ptr;
		
		c_ptr = var.cave[py.char_row][py.char_col];
		if (c_ptr.tptr != 0) {
			/* take no action here, don't want to destroy object under player */
			io.msg_print ("There is already an object under you.");
			/* set free_turn_flag so that scroll/spell points won't be used */
			var.free_turn_flag = true;
		} else {
			m3.place_object(py.char_row, py.char_col, false);
			desc.invcopy(t.t_list[c_ptr.tptr], Constants.OBJ_MUSH);
		}
	}
	
	/* Attempts to destroy a type of creature.  Success depends on	*/
	/* the creatures level VS. the player's level		 -RAK-	 */
	public boolean dispel_creature(int cflag, int damage) {
		int i;
		int k;
		boolean dispel;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		dispel = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			if ((m_ptr.cdis <= Constants.MAX_SIGHT) && (cflag & mon.c_list[m_ptr.mptr].cdefense) != 0 && m1.los(py.char_row, py.char_col, m_ptr.fy, m_ptr.fx)) {
				r_ptr = mon.c_list[m_ptr.mptr];
				var.c_recall[m_ptr.mptr].r_cdefense |= cflag;
				m_name = monster_name(m_ptr, r_ptr);
				k = mor3.mon_take_hit(i, m1.randint(damage));
				/* Should get these messages even if the monster is not
				 * visible.  */
				if (k >= 0) {
					out_val = String.format("%s dissolves!", m_name);
				} else {
					out_val = String.format("%s shudders.", m_name);
				}
				io.msg_print(out_val);
				dispel = true;
				if (k >= 0) {
					m3.prt_experience();
				}
			}
		}
		return dispel;
	}
	
	/* Attempt to turn (confuse) undead creatures.	-RAK-	*/
	public boolean turn_undead() {
		int i;
		boolean turn_und;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		turn_und = false;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = mon.m_list[i];
			r_ptr = mon.c_list[m_ptr.mptr];
			if ((m_ptr.cdis <= Constants.MAX_SIGHT) && (Constants.CD_UNDEAD & r_ptr.cdefense) != 0 && (m1.los(py.char_row, py.char_col, m_ptr.fy, m_ptr.fx))) {
				m_name = monster_name(m_ptr, r_ptr);
				if (((py.py.misc.lev + 1) > r_ptr.level) || (m1.randint(5) == 1)) {
					if (m_ptr.ml) {
						out_val = String.format("%s runs frantically!", m_name);
						io.msg_print(out_val);
						turn_und = true;
						var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_UNDEAD;
					}
					m_ptr.confused = py.py.misc.lev;
				} else if (m_ptr.ml) {
					out_val = String.format("%s is unaffected.", m_name);
					io.msg_print(out_val);
				}
			}
		}
		return turn_und;
	}
	
	/* Leave a glyph of warding. Creatures will not pass over! -RAK-*/
	public void warding_glyph() {
		int i;
		CaveType c_ptr;
		
		c_ptr = var.cave[py.char_row][py.char_col];
		if (c_ptr.tptr == 0) {
			i = m1.popt();
			c_ptr.tptr = i;
			desc.invcopy(t.t_list[i], Constants.OBJ_SCARE_MON);
		}
	}
	
	/* Lose a strength point.				-RAK-	*/
	public void lose_str() {
		if (!py.py.flags.sustain_str) {
			m3.dec_stat(Constants.A_STR);
			io.msg_print("You feel very sick.");
		} else {
			io.msg_print("You feel sick for a moment,  it passes.");
		}
	}
	
	/* Lose an intelligence point.				-RAK-	*/
	public void lose_int() {
		if (!py.py.flags.sustain_int) {
			m3.dec_stat(Constants.A_INT);
			io.msg_print("You become very dizzy.");
		} else {
			io.msg_print("You become dizzy for a moment,  it passes.");
		}
	}
	
	/* Lose a wisdom point.					-RAK-	*/
	public void lose_wis() {
		if (!py.py.flags.sustain_wis) {
			m3.dec_stat(Constants.A_WIS);
			io.msg_print("You feel very naive.");
		} else {
			io.msg_print("You feel naive for a moment,  it passes.");
		}
	}
	
	/* Lose a dexterity point.				-RAK-	*/
	public void lose_dex() {
		if (!py.py.flags.sustain_dex) {
			m3.dec_stat(Constants.A_DEX);
			io.msg_print("You feel very sore.");
		} else {
			io.msg_print("You feel sore for a moment,  it passes.");
		}
	}
	
	/* Lose a constitution point.				-RAK-	*/
	public void lose_con() {
		if (!py.py.flags.sustain_con) {
			m3.dec_stat(Constants.A_CON);
			io.msg_print("You feel very sick.");
		} else {
			io.msg_print("You feel sick for a moment,  it passes.");
		}
	}
	
	/* Lose a charisma point.				-RAK-	*/
	public void lose_chr() {
		if (!py.py.flags.sustain_chr) {
			m3.dec_stat(Constants.A_CHR);
			io.msg_print("Your skin starts to itch.");
		} else {
			io.msg_print("Your skin starts to itch, but feels better now.");
		}
	}
	
	/* Lose experience					-RAK-	*/
	public void lose_exp(int amount) {
		int i;
		PlayerMisc m_ptr;
		ClassType c_ptr;
		
		m_ptr = py.py.misc;
		if (amount > m_ptr.exp) {
			m_ptr.exp = 0;
		} else {
			m_ptr.exp -= amount;
		}
		m3.prt_experience();
		
		i = 0;
		while ((py.player_exp[i] * m_ptr.expfact / 100) <= m_ptr.exp) {
			i++;
		}
		/* increment i once more, because level 1 exp is stored in player_exp[0] */
		i++;
		
		if (m_ptr.lev != i) {
			m_ptr.lev = i;
			
			m3.calc_hitpoints();
			c_ptr = py.Class[m_ptr.pclass];
			if (c_ptr.spell == Constants.MAGE) {
				m3.calc_spells(Constants.A_INT);
				m3.calc_mana(Constants.A_INT);
			} else if (c_ptr.spell == Constants.PRIEST) {
				m3.calc_spells(Constants.A_WIS);
				m3.calc_mana(Constants.A_WIS);
			}
			m3.prt_level();
			m3.prt_title();
		}
	}
	
	/* Slow Poison						-RAK-	*/
	public boolean slow_poison() {
		boolean slow;
		PlayerFlags f_ptr;
		
		slow = false;
		f_ptr = py.py.flags;
		if (f_ptr.poisoned > 0) {
			f_ptr.poisoned = f_ptr.poisoned / 2;
			if (f_ptr.poisoned < 1)	f_ptr.poisoned = 1;
			slow = true;
			io.msg_print("The effect of the poison has been reduced.");
		}
		return slow;
	}
	
	/* Bless						-RAK-	*/
	public void bless(int amount) {
		py.py.flags.blessed += amount;
	}
	
	/* Detect Invisible for period of time			-RAK-	*/
	public void detect_inv2(int amount) {
		py.py.flags.detect_inv += amount;
	}
	
	public void replace_spot(int y, int x, int typ) {
		CaveType c_ptr;
		
		c_ptr = var.cave[y][x];
		switch(typ)
		{
		case 1: case 2: case 3:
			c_ptr.fval  = Constants.CORR_FLOOR;
			break;
		case 4: case 7: case 10:
			c_ptr.fval  = Constants.GRANITE_WALL;
			break;
		case 5: case 8: case 11:
			c_ptr.fval  = Constants.MAGMA_WALL;
			break;
		case 6: case 9: case 12:
			c_ptr.fval  = Constants.QUARTZ_WALL;
			break;
		}
		c_ptr.pl = false;
		c_ptr.fm = false;
		c_ptr.lr = false;  /* this is no longer part of a room */
		if (c_ptr.tptr != 0) {
			mor3.delete_object(y, x);
		}
		if (c_ptr.cptr > 1) {
			mor3.delete_monster(c_ptr.cptr);
		}
	}
	
	/* The spell of destruction.				-RAK-	*/
	/* NOTE : Winning creatures that are deleted will be considered	 */
	/*	  as teleporting to another level.  This will NOT win the*/
	/*	  game.						       */
	public void destroy_area(int y, int x) {
		int i, j, k;
		
		if (var.dun_level > 0) {
			for (i = (y - 15); i <= (y + 15); i++) {
				for (j = (x - 15); j <= (x + 15); j++) {
					if (m1.in_bounds(i, j) && (var.cave[i][j].fval != Constants.BOUNDARY_WALL)) {
						k = m1.distance(i, j, y, x);
						if (k == 0) {	/* clear player's spot, but don't put wall there */
							replace_spot(i, j, 1);
						} else if (k < 13) {
							replace_spot(i, j, m1.randint(6));
						} else if (k < 16) {
							replace_spot(i, j, m1.randint(9));
						}
					}
				}
			}
		}
		io.msg_print("There is a searing blast of light!");
		py.py.flags.blind += 10 + m1.randint(10);
	}
	
	/* Enchants a plus onto an item.			-RAK-	*/
	public boolean enchant(IntPointer plusses, int limit) {
		/* limit is the maximum bonus allowed; usually 10, but weapon's maximum damage
		 * when enchanting melee weapons to damage */
		
		int chance;
		boolean res;
		
		if (limit <= 0) {	/* avoid randint(0) call */
			return false;
		}
		chance = 0;
		res = false;
		if (plusses.value() > 0) {
			chance = plusses.value();
			if (m1.randint(100) == 1) {	/* very rarely allow enchantment over limit */
				chance = m1.randint(chance) - 1;
			}
		}
		if (m1.randint(limit) > chance) {
			plusses.value(plusses.value() + 1);
			res = true;
		}
		return res;
	}
	
	/* Removes curses from items in inventory		-RAK-	*/
	public boolean remove_curse() {
		int i;
		boolean result;
		InvenType i_ptr;
		
		result = false;
		for (i = Constants.INVEN_WIELD; i <= Constants.INVEN_OUTER; i++) {
			i_ptr = t.inventory[i];
			if ((Constants.TR_CURSED & i_ptr.flags) != 0) {
				i_ptr.flags &= ~Constants.TR_CURSED;
				mor1.calc_bonuses();
				result = true;
			}
		}
		return result;
	}
	
	/* Restores any drained experience			-RAK-	*/
	public boolean restore_level() {
		boolean restore;
		PlayerMisc m_ptr;
		
		restore = false;
		m_ptr = py.py.misc;
		if (m_ptr.max_exp > m_ptr.exp) {
			restore = true;
			io.msg_print("You feel your life energies returning.");
			/* this while loop is not redundant, ptr_exp may reduce the exp level */
			while (m_ptr.exp < m_ptr.max_exp) {
				m_ptr.exp = m_ptr.max_exp;
				m3.prt_experience();
			}
		}
		return restore;
	}
}
