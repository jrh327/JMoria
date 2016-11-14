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
	
	private static String[] stat_names = { "STR : ", "INT : ", "WIS : ", "DEX : ", "CON : ", "CHR : " };
	private static final int BLANK_LENGTH	= 24;
	private static String blank_string = "                        ";
	
	private Misc3() { }
	
	/**
	 * Places a particular trap at location y, x -RAK-
	 * 
	 * @param y - The vertical position at which to place the trap
	 * @param x - The horizontal position at which to place the trap
	 * @param subval - The type of trap to place
	 */
	public static void place_trap(int y, int x, int subval) {
		int cur_pos;
		
		cur_pos = Misc1.popt();
		Variable.cave[y][x].tptr = cur_pos;
		Desc.invcopy(Treasure.t_list[cur_pos], Constants.OBJ_TRAP_LIST + subval);
	}
	
	/**
	 * Places rubble at location y, x -RAK-
	 * 
	 * @param y - The vertical position at which to place the rubble
	 * @param x - The horizontal position at which to place the rubble
	 */
	public static void place_rubble(int y, int x) {
		int cur_pos;
		CaveType cave_ptr;
		
		cur_pos = Misc1.popt();
		cave_ptr = Variable.cave[y][x];
		cave_ptr.tptr = cur_pos;
		cave_ptr.fval = Constants.BLOCKED_FLOOR;
		Desc.invcopy(Treasure.t_list[cur_pos], Constants.OBJ_RUBBLE);
	}
	
	/**
	 * Places a treasure (Gold or Gems) at given row, column -RAK-
	 * 
	 * @param y - The vertical position at which to place the gold
	 * @param x - The horizontal position at which to place the gold
	 */
	public static void place_gold(int y, int x) {
		int i, cur_pos;
		InvenType t_ptr;
		
		cur_pos = Misc1.popt();
		i = ((Misc1.randint(Variable.dun_level + 2) + 2) / 2) - 1;
		if (Misc1.randint(Constants.OBJ_GREAT) == 1) {
			i += Misc1.randint(Variable.dun_level + 1);
		}
		if (i >= Constants.MAX_GOLD) {
			i = Constants.MAX_GOLD - 1;
		}
		Variable.cave[y][x].tptr = cur_pos;
		Desc.invcopy(Treasure.t_list[cur_pos], Constants.OBJ_GOLD_LIST + i);
		t_ptr = Treasure.t_list[cur_pos];
		t_ptr.cost += (8L * (long)Misc1.randint(t_ptr.cost)) + Misc1.randint(8);
		if (Variable.cave[y][x].cptr == 1) {
			IO.msg_print("You feel something roll beneath your feet.");
		}
	}
	
	public static int get_obj_num(int level, boolean must_be_small) {
		int i, j;
		
		if (level == 0) {
			i = Misc1.randint(Treasure.t_level[0]) - 1;
		} else {
			if (level >= Constants.MAX_OBJ_LEVEL) {
				level = Constants.MAX_OBJ_LEVEL;
			} else if (Misc1.randint(Constants.OBJ_GREAT) == 1) {
				level = level * Constants.MAX_OBJ_LEVEL / Misc1.randint(Constants.MAX_OBJ_LEVEL) + 1;
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
				if (Misc1.randint(2) == 1) {
					i = Misc1.randint(Treasure.t_level[level]) - 1;
				} else { /* Choose three objects, pick the highest level. */
					i = Misc1.randint(Treasure.t_level[level]) - 1;
					j = Misc1.randint(Treasure.t_level[level]) - 1;
					if (i < j) {
						i = j;
					}
					j = Misc1.randint(Treasure.t_level[level]) - 1;
					if (i < j) {
						i = j;
					}
					
					j = Treasure.object_list[Treasure.sorted_objects[i]].level;
					if (j == 0) {
						i = Misc1.randint(Treasure.t_level[0]) - 1;
					} else {
						i = Misc1.randint(Treasure.t_level[j] - Treasure.t_level[j - 1]) - 1 + Treasure.t_level[j - 1];
					}
				}
			} while (must_be_small && Sets.set_large(Treasure.object_list[Treasure.sorted_objects[i]]));
		}
		return i;
	}

	/**
	 * Places an object at given row, column co-ordinate -RAK-
	 * 
	 * @param y - The vertical position at which to place the object
	 * @param x - The horizontal position at which to place the object
	 * @param must_be_small - ignore
	 */
	public static void place_object(int y, int x, boolean must_be_small) {
		int cur_pos, tmp;
		
		cur_pos = Misc1.popt();
		Variable.cave[y][x].tptr = cur_pos;
		/* split this line up to avoid a reported compiler bug */
		tmp = get_obj_num(Variable.dun_level, must_be_small);
		Desc.invcopy(Treasure.t_list[cur_pos], Treasure.sorted_objects[tmp]);
		Misc2.magic_treasure(cur_pos, Variable.dun_level);
		if (Variable.cave[y][x].cptr == 1) {
			IO.msg_print("You feel something roll beneath your feet.");	/* -CJS- */
		}
	}
	
	/**
	 * Allocates an object for tunnels and rooms -RAK-
	 * 
	 * @param alloc_set - Place the object in a corridor, floor, or room
	 * @param typ - Type of object to allocate
	 * @param num - Number of objects to allocate
	 */
	public static void alloc_object(int alloc_set, int typ, int num) {
		int i, j, k;
		
		for (k = 0; k < num; k++) {
			do {
				i = Misc1.randint(Variable.cur_height) - 1;
				j = Misc1.randint(Variable.cur_width) - 1;
				
				/* don't put an object beneath the player, this could cause problems
				 * if player is standing under rubble, or on a trap */
			} while ((!alloc_set(Variable.cave[i][j].fval, alloc_set))
					|| (Variable.cave[i][j].tptr != 0)
					|| (i == Player.char_row && j == Player.char_col));
			if (typ < 4) {	/* typ == 2 not used, used to be visible traps */
				if (typ == 1) {
					place_trap(i, j, Misc1.randint(Constants.MAX_TRAP) - 1); /* typ == 1 */
				} else {
					place_rubble(i, j); /* typ == 3 */
				}
			} else {
				if (typ == 4) {
					place_gold(i, j); /* typ == 4 */
				} else {
					place_object(i, j, false); /* typ == 5 */
				}
			}
		}
	}
	
	private static boolean alloc_set(int val, int alloc_set) {
		switch (alloc_set)
		{
		case Sets.set_corr:
			return Sets.set_corr(val);
		case Sets.set_floor:
			return Sets.set_floor(val);
		case Sets.set_room:
			return Sets.set_room(val);
		default:
			return false;
		}
	}
	
	/**
	 * Creates objects nearby the coordinates given -RAK-
	 * 
	 * @param y - The vertical position around which to place the object
	 * @param x - The horizontal position around which to place the object
	 * @param num - Number of objects to allocate
	 */
	public static void random_object(int y, int x, int num) {
		int i, j, k;
		CaveType cave_ptr;
		
		do {
			i = 0;
			do {
				j = y - 3 + Misc1.randint(5);
				k = x - 4 + Misc1.randint(7);
				cave_ptr = Variable.cave[j][k];
				if (Misc1.in_bounds(j, k) && (cave_ptr.fval <= Constants.MAX_CAVE_FLOOR)
						&& (cave_ptr.tptr == 0)) {
					if (Misc1.randint(100) < 75) {
						place_object(j, k, false);
					} else {
						place_gold(j, k);
					}
					i = 9;
				}
				i++;
			} while(i <= 10);
			num--;
		} while (num != 0);
	}
	
	/**
	 * Converts stat num into string -RAK-
	 * 
	 * @param stat - The stat to be converted
	 * @return The stringified stat
	 */
	public static String cnv_stat(int stat) {
		int part1, part2;
		
		if (stat > 18) {
			part1 = 18;
			part2 = stat - 18;
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
	 * @param stat - The stat to print
	 */
	public static void prt_stat(int stat) {
		String out_val1;
		
		out_val1 = cnv_stat(Player.py.stats.use_stat[stat]);
		IO.put_buffer(stat_names[stat], 6 + stat, Constants.STAT_COLUMN);
		IO.put_buffer(out_val1, 6 + stat, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Print character info in given row, column.
	 * The longest title is 13 characters, so only pad to 13 -RAK-
	 * 
	 * @param info - What to print
	 * @param row - The vertical position at which to print
	 * @param column - The horizontal position at which to print
	 */
	public static void prt_field(String info, int row, int column) {
		IO.put_buffer(blank_string.substring(BLANK_LENGTH - 13), row, column);
		IO.put_buffer(info, row, column);
	}
	
	/**
	 * Print long number with header at given row, column
	 * 
	 * @param header - The header to print
	 * @param num - The number to print
	 * @param row - The vertical position at which to print
	 * @param column - The horizontal position at which to print
	 */
	public static void prt_lnum(String header, int num, int row, int column) {
		String out_val;
		
		out_val = String.format("%s: %6d", header, num);
		IO.put_buffer(out_val, row, column);
	}
	
	/**
	 * Print long number (7 digits of space) with header at given row, column
	 * 
	 * @param header - The header to print
	 * @param num - The number to print
	 * @param row - The vertical position at which to print
	 * @param column - The horizonal position at which to print
	 */
	public static void prt_7lnum(String header, int num, int row, int column) {
		String out_val;
		
		out_val = String.format("%s: %7d", header, num);
		IO.put_buffer(out_val, row, column);
	}
	
	/**
	 * Print number with header at given row, column -RAK-
	 * 
	 * @param header - The header to print
	 * @param num - The number to print
	 * @param row - The vertical position at which to print
	 * @param column - The horizontal position at which to print
	 */
	public static void prt_num(String header, int num, int row, int column) {
		String out_val;
		
		out_val = String.format("%s: %6d", header, num);
		IO.put_buffer(out_val, row, column);
	}
	
	/**
	 * Print long number at given row, column
	 * 
	 * @param num - The number to print
	 * @param row - The vertical position at which to print
	 * @param column - The horizontal position at which to print
	 */
	public static void prt_long(int num, int row, int column) {
		String out_val;
		
		out_val = String.format("%6d", num);
		IO.put_buffer(out_val, row, column);
	}

	/**
	 * Print number at given row, column -RAK-
	 * 
	 * @param num - The number to print
	 * @param row - The vertical position at which to print
	 * @param column - The horizontal position at which to print
	 */
	public static void prt_int(int num, int row, int column) {
		String out_val;
		
		out_val = String.format("%6d", num);
		IO.put_buffer(out_val, row, column);
	}
	
	/**
	 * Adjustment for wisdom/intelligence -JWT-
	 * 
	 * @param stat - The stat to adjust
	 * @return The adjusted value of the player's stat
	 */
	public static int stat_adj(int stat) {
		int value;
		
		value = Player.py.stats.use_stat[stat];
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
	public static int chr_adj() {
		int charisma;
		
		charisma = Player.py.stats.use_stat[Constants.A_CHR];
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
			switch(charisma)
			{
			case 18:	return 100;
			case 17:	return 101;
			case 16:	return 102;
			case 15:	return 103;
			case 14:	return 104;
			case 13:	return 106;
			case 12:	return 108;
			case 11:	return 110;
			case 10:	return 112;
			case 9:  	return 114;
			case 8:  	return 116;
			case 7:  	return 118;
			case 6:  	return 120;
			case 5:  	return 122;
			case 4:  	return 125;
			case 3:  	return 130;
			default: 	return 100;
			}
		}
	}
	
	/**
	 * Returns a character's adjustment to hit points -JWT-
	 * 
	 * @return The adjusted value of the player's constitution
	 */
	public static int con_adj() {
		int con;
		
		con = Player.py.stats.use_stat[Constants.A_CON];
		if (con < 7) {
			return con - 7;
		} else if (con < 17) {
			return 0;
		} else if (con ==  17) {
			return 1;
		} else if (con <  94) {
			return 2;
		} else if (con < 117) {
			return 3;
		} else {
			return 4;
		}
	}
	
	public static String title_string() {
		String p;
		
		if (Player.py.misc.lev < 1) {
			p = "Babe in arms";
		} else if (Player.py.misc.lev <= Constants.MAX_PLAYER_LEVEL) {
			p = Player.player_title[Player.py.misc.pclass][Player.py.misc.lev - 1];
		} else if (Player.py.misc.male) {
			p = "**KING**";
		} else {
			p = "**QUEEN**";
		}
		return p;
	}
	
	/**
	 * Prints title of character -RAK-
	 */
	public static void prt_title() {
		prt_field(title_string(), 4, Constants.STAT_COLUMN);
	}
	
	/**
	 * Prints player's level -RAK-
	 */
	public static void prt_level() {
		prt_int(Player.py.misc.lev, 13, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints player's current mana points. -RAK-
	 */
	public static void prt_cmana() {
		prt_int(Player.py.misc.cmana, 15, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints Max hit points -RAK-
	 */
	public static void prt_mhp() {
		prt_int(Player.py.misc.mhp, 16, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints player's current hit points -RAK-
	 */
	public static void prt_chp() {
		prt_int(Player.py.misc.chp, 17, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints current AC -RAK-
	 */
	public static void prt_pac() {
		prt_int(Player.py.misc.dis_ac, 19, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints current gold -RAK-
	 */
	public static void prt_gold() {
		prt_long(Player.py.misc.au, 20, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Prints depth in stat area -RAK-
	 */
	public static void prt_depth() {
		String depths;
		int depth;
		
		depth = Variable.dun_level * 50;
		if (depth == 0) {
			depths = "Town level";
		} else {
			depths = String.format("%d feet", depth);
		}
		IO.prt(depths, 23, 65);
	}
	
	/**
	 * Prints status of hunger -RAK-
	 */
	public static void prt_hunger() {
		if ((Constants.PY_WEAK & Player.py.flags.status) != 0) {
			IO.put_buffer("Weak  ", 23, 0);
		} else if ((Constants.PY_HUNGRY & Player.py.flags.status) != 0) {
			IO.put_buffer("Hungry", 23, 0);
		} else {
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 6), 23, 0);
		}
	}
	
	/**
	 * Prints Blind status -RAK-
	 */
	public static void prt_blind() {
		if ((Constants.PY_BLIND & Player.py.flags.status) != 0) {
			IO.put_buffer("Blind", 23, 7);
		} else {
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 5), 23, 7);
		}
	}
	
	/**
	 * Prints Confusion status -RAK-
	 */
	public static void prt_confused() {
		if ((Constants.PY_CONFUSED & Player.py.flags.status) != 0) {
			IO.put_buffer("Confused", 23, 13);
		} else {
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 8), 23, 13);
		}
	}
	
	/**
	 * Prints Fear status -RAK-
	 */
	public static void prt_afraid() {
		if ((Constants.PY_FEAR & Player.py.flags.status) != 0) {
			IO.put_buffer("Afraid", 23, 22);
		} else {
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 6), 23, 22);
		}
	}
	
	/**
	 * Prints Poisoned status -RAK-
	 */
	public static void prt_poisoned() {
		if ((Constants.PY_POISONED & Player.py.flags.status) != 0) {
			IO.put_buffer("Poisoned", 23, 29);
		} else {
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 8), 23, 29);
		}
	}
	
	/**
	 * Prints Searching, Resting, Paralysis, or 'count' status -RAK-
	 */
	public static void prt_state() {
		String tmp;
		Player.py.flags.status &= ~Constants.PY_REPEAT;
		if (Player.py.flags.paralysis > 1) {
			IO.put_buffer("Paralysed", 23, 38);
		} else if ((Constants.PY_REST & Player.py.flags.status) != 0) {
			if (Player.py.flags.rest < 0) {
				tmp = "Rest *";
			} else if (Variable.display_counts.value()) {
				tmp = String.format("Rest %-5d", Player.py.flags.rest);
			} else {
				tmp = "Rest";
			}
			IO.put_buffer(tmp, 23, 38);
		} else if (Variable.command_count > 0) {
			if (Variable.display_counts.value()) {
				tmp = String.format("Repeat %-3d", Variable.command_count);
			} else {
				tmp ="Repeat";
			}
			Player.py.flags.status |= Constants.PY_REPEAT;
			IO.put_buffer(tmp, 23, 38);
			if ((Constants.PY_SEARCH & Player.py.flags.status) != 0) {
				IO.put_buffer("Search", 23, 38);
			}
		} else if ((Constants.PY_SEARCH & Player.py.flags.status) != 0) {
			IO.put_buffer("Searching", 23, 38);
		} else {		/* "repeat 999" is 10 characters */
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 10), 23, 38);
		}
	}
	
	/**
	 * Prints the speed of a character. -CJS-
	 */
	public static void prt_speed () {
		int i;
		
		i = Player.py.flags.speed;
		if ((Constants.PY_SEARCH & Player.py.flags.status) != 0) {	/* Search mode. */
			i--;
		}
		if (i > 1) {
			IO.put_buffer("Very Slow", 23, 49);
		} else if (i == 1) {
			IO.put_buffer("Slow     ", 23, 49);
		} else if (i == 0) {
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 9), 23, 49);
		} else if (i == -1) {
			IO.put_buffer("Fast     ", 23, 49);
		} else {
			IO.put_buffer("Very Fast", 23, 49);
		}
	}
	
	public static void prt_study() {
		Player.py.flags.status &= ~Constants.PY_STUDY;
		if (Player.py.flags.new_spells == 0) {
			IO.put_buffer(blank_string.substring(BLANK_LENGTH - 5), 23, 59);
		} else {
			IO.put_buffer("Study", 23, 59);
		}
	}
	
	/**
	 * Prints winner status on display -RAK-
	 */
	public static void prt_winner() {
		if ((Variable.noscore & 0x2) != 0) {
			if (Variable.wizard) {
				IO.put_buffer("Is wizard  ", 22, 0);
			} else {
				IO.put_buffer("Was wizard ", 22, 0);
			}
		} else if ((Variable.noscore & 0x1) != 0) {
			IO.put_buffer("Resurrected", 22, 0);
		} else if ((Variable.noscore & 0x4) != 0) {
			IO.put_buffer("Duplicate", 22, 0);
		} else if (Variable.total_winner) {
			IO.put_buffer("*Winner*   ", 22, 0);
		}
	}
	
	/**
	 * Change the value of one of the player's stats
	 * 
	 * @param stat - Which stat to change
	 * @param amount - How much to change the stat by
	 * @return Final value of the stat
	 */
	public static int modify_stat(int stat, int amount) {
		int loop, i;
		int tmp_stat;
		
		tmp_stat = Player.py.stats.cur_stat[stat];
		loop = Math.abs(amount);
		for (i = 0; i < loop; i++) {
			if (amount > 0) {
				if (tmp_stat < 18) {
					tmp_stat++;
				} else if (tmp_stat < 108) {
					tmp_stat += 10;
				} else {
					tmp_stat = 118;
				}
			} else {
				if (tmp_stat > 27) {
					tmp_stat -= 10;
				} else if (tmp_stat > 18) {
					tmp_stat = 18;
				} else if (tmp_stat > 3) {
					tmp_stat--;
				}
			}
		}
		return tmp_stat;
	}
	
	/**
	 * Set the value of the stat which is actually used. -CJS-
	 * 
	 * @param stat - Which stat to set
	 */
	public static void set_use_stat(int stat) {
		Player.py.stats.use_stat[stat] = modify_stat(stat, Player.py.stats.mod_stat[stat]);
		
		if (stat == Constants.A_STR) {
			Player.py.flags.status |= Constants.PY_STR_WGT;
			Moria1.calc_bonuses();
		} else if (stat == Constants.A_DEX) {
			Moria1.calc_bonuses();
		} else if (stat == Constants.A_INT && Player.Class[Player.py.misc.pclass].spell == Constants.MAGE) {
			calc_spells(Constants.A_INT);
			calc_mana(Constants.A_INT);
		} else if (stat == Constants.A_WIS && Player.Class[Player.py.misc.pclass].spell == Constants.PRIEST) {
			calc_spells(Constants.A_WIS);
			calc_mana(Constants.A_WIS);
		} else if (stat == Constants.A_CON) {
			calc_hitpoints();
		}
	}
	
	/**
	 * Increases a stat by one randomized level -RAK-
	 * 
	 * @param stat - Which stat to increase
	 * @return Whether the stat was increased
	 */
	public static boolean inc_stat(int stat) {
		int tmp_stat, gain;
		
		tmp_stat = Player.py.stats.cur_stat[stat];
		if (tmp_stat < 118) {
			if (tmp_stat < 18) {
				tmp_stat++;
			} else if (tmp_stat < 116) {
				/* stat increases by 1/6 to 1/3 of difference from max */
				gain = ((118 - tmp_stat) / 3 + 1) >> 1;
				tmp_stat += Misc1.randint(gain) + gain;
			} else {
				tmp_stat++;
			}
			
			Player.py.stats.cur_stat[stat] = tmp_stat;
			if (tmp_stat > Player.py.stats.max_stat[stat]) {
				Player.py.stats.max_stat[stat] = tmp_stat;
			}
			set_use_stat(stat);
			prt_stat(stat);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Decreases a stat by one randomized level -RAK-
	 * 
	 * @param stat - Which stat to decrease
	 * @return Whether the stat was decreased
	 */
	public static boolean dec_stat(int stat) {
		int tmp_stat, loss;
		
		tmp_stat = Player.py.stats.cur_stat[stat];
		if (tmp_stat > 3) {
			if (tmp_stat < 19) {
				tmp_stat--;
			} else if (tmp_stat < 117) {
				loss = (((118 - tmp_stat) >> 1) + 1) >> 1;
				tmp_stat += -Misc1.randint(loss) - loss;
				if (tmp_stat < 18) {
					tmp_stat = 18;
				}
			} else {
				tmp_stat--;
			}
			
			Player.py.stats.cur_stat[stat] = tmp_stat;
			set_use_stat(stat);
			prt_stat(stat);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Restore a stat. Return TRUE only if this actually makes a difference.
	 * 
	 * @param stat - Which stat to restore
	 * @return True if the stat actually needed to be restored, otherwise false
	 */
	public static boolean res_stat(int stat) {
		int i;
		
		i = Player.py.stats.max_stat[stat] - Player.py.stats.cur_stat[stat];
		if (i != 0) {
			Player.py.stats.cur_stat[stat] += i;
			set_use_stat(stat);
			prt_stat(stat);
			return true;
		}
		return false;
	}
	
	/**
	 * Boost a stat artificially (by wearing something). If the display argument
	 * is TRUE, then increase is shown on the screen.
	 * 
	 * @param stat - Which stat to boost
	 * @param amount - How much to boost the stat by
	 */
	public static void bst_stat(int stat, int amount) {
		Player.py.stats.mod_stat[stat] += amount;
		
		set_use_stat(stat);
		/* can not call prt_stat() here, may be in store, may be in inven_command */
		Player.py.flags.status |= (Constants.PY_STR << stat);
	}
	
	/**
	 * Returns a character's adjustment to hit. -JWT-
	 */
	public static int tohit_adj() {
		int total, stat;
		
		stat = Player.py.stats.use_stat[Constants.A_DEX];
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
		
		stat = Player.py.stats.use_stat[Constants.A_STR];
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
	public static int toac_adj() {
		int stat;
		
		stat = Player.py.stats.use_stat[Constants.A_DEX];
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
	public static int todis_adj() {
		int stat;
		
		stat = Player.py.stats.use_stat[Constants.A_DEX];
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
	public static int todam_adj() {
		int stat;
		
		stat = Player.py.stats.use_stat[Constants.A_STR];
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
	public static void prt_stat_block() {
		long status;
		PlayerMisc m_ptr;
		int i;
		
		m_ptr = Player.py.misc;
		prt_field(Player.race[Player.py.misc.prace].trace,		2, Constants.STAT_COLUMN);
		prt_field(Player.Class[Player.py.misc.pclass].title,	3, Constants.STAT_COLUMN);
		prt_field(title_string(),						4, Constants.STAT_COLUMN);
		for (i = 0; i < 6; i++) {
			prt_stat(i);
		}
		prt_num ("LEV ", m_ptr.lev,		13, Constants.STAT_COLUMN);
		prt_lnum("EXP ", m_ptr.exp,		14, Constants.STAT_COLUMN);
		prt_num ("MANA", m_ptr.cmana,	15, Constants.STAT_COLUMN);
		prt_num ("MHP ", m_ptr.mhp,		16, Constants.STAT_COLUMN);
		prt_num ("CHP ", m_ptr.chp,		17, Constants.STAT_COLUMN);
		prt_num ("AC  ", m_ptr.dis_ac,	19, Constants.STAT_COLUMN);
		prt_lnum("GOLD", m_ptr.au,		20, Constants.STAT_COLUMN);
		prt_winner();
		status = Player.py.flags.status;
		if (((Constants.PY_HUNGRY | Constants.PY_WEAK) & status) != 0) {
			prt_hunger();
		}
		if ((Constants.PY_BLIND & status) != 0) {
			prt_blind();
		}
		if ((Constants.PY_CONFUSED & status) != 0) {
			prt_confused();
		}
		if ((Constants.PY_FEAR & status) != 0) {
			prt_afraid();
		}
		if ((Constants.PY_POISONED & status) != 0) {
			prt_poisoned();
		}
		if (((Constants.PY_SEARCH | Constants.PY_REST) & status) != 0) {
			prt_state ();
		}
		/* if speed non zero, print it, modify speed if Searching */
		if (Player.py.flags.speed - ((Constants.PY_SEARCH & status) >> 8) != 0) {
			prt_speed ();
		}
		/* display the study field */
		prt_study();
	}
	
	/**
	 * Draws entire screen -RAK-
	 */
	public static void draw_cave() {
		IO.clear_screen ();
		prt_stat_block();
		Misc1.prt_map();
		prt_depth();
	}
	
	/**
	 * Prints the following information on the screen. -JWT-
	 */
	public static void put_character() {
		PlayerMisc m_ptr;
		
		m_ptr = Player.py.misc;
		IO.clear_screen();
		IO.put_buffer("Name        :", 2, 1);
		IO.put_buffer("Race        :", 3, 1);
		IO.put_buffer("Sex         :", 4, 1);
		IO.put_buffer("Class       :", 5, 1);
		if (Variable.character_generated) {
			IO.put_buffer(m_ptr.name, 2, 15);
			IO.put_buffer(Player.race[m_ptr.prace].trace, 3, 15);
			IO.put_buffer(m_ptr.male ? "Male" : "Female", 4, 15);
			IO.put_buffer(Player.Class[m_ptr.pclass].title, 5, 15);
		}
	}
	
	/**
	 * Prints stats and adjustments. -JWT-
	 */
	public static void put_stats() {
		PlayerMisc m_ptr;
		int i;
		String buf;
		
		m_ptr = Player.py.misc;
		for (i = 0; i < 6; i++) {
			buf = cnv_stat(Player.py.stats.use_stat[i]);
			IO.put_buffer(stat_names[i], 2 + i, 61);
			IO.put_buffer(buf, 2 + i, 66);
			if (Player.py.stats.max_stat[i] > Player.py.stats.cur_stat[i]) {
				buf = cnv_stat(Player.py.stats.max_stat[i]);
				IO.put_buffer(buf, 2 + i, 73);
			}
		}
		prt_num("+ To Hit    ", m_ptr.dis_th,  9, 1);
		prt_num("+ To Damage ", m_ptr.dis_td, 10, 1);
		prt_num("+ To AC     ", m_ptr.dis_tac, 11, 1);
		prt_num("  Total AC  ", m_ptr.dis_ac, 12, 1);
	}
	
	/**
	 * Returns a rating of x depending on y -JWT-
	 * 
	 * @param x - Being rated
	 * @param y - Being compared to
	 * @return - Rating of x
	 */
	public static String likert(int x, int y) {
		switch((x / y))
		{
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
	public static void put_misc1() {
		PlayerMisc m_ptr;
		
		m_ptr = Player.py.misc;
		prt_num("Age          ", m_ptr.age, 2, 38);
		prt_num("Height       ", m_ptr.ht, 3, 38);
		prt_num("Weight       ", m_ptr.wt, 4, 38);
		prt_num("Social Class ", m_ptr.sc, 5, 38);
	}
	
	/**
	 * Prints player information. -JWT-
	 */
	public static void put_misc2() {
		PlayerMisc m_ptr;
		
		m_ptr = Player.py.misc;
		prt_7lnum ("Level      ", m_ptr.lev, 9, 28);
		prt_7lnum("Experience ", m_ptr.exp, 10, 28);
		prt_7lnum("Max Exp    ", m_ptr.max_exp, 11, 28);
		if (m_ptr.lev == Constants.MAX_PLAYER_LEVEL) {
			IO.prt("Exp to Adv.: *******", 12, 28);
		} else {
			prt_7lnum("Exp to Adv.", (Player.player_exp[m_ptr.lev - 1] * m_ptr.expfact / 100), 12, 28);
		}
		prt_7lnum("Gold       ", m_ptr.au, 13, 28);
		prt_num("Max Hit Points ", m_ptr.mhp, 9, 52);
		prt_num("Cur Hit Points ", m_ptr.chp, 10, 52);
		prt_num("Max Mana       ", m_ptr.mana, 11, 52);
		prt_num("Cur Mana       ", m_ptr.cmana, 12, 52);
	}
	
	/**
	 * Prints ratings on certain abilities -RAK-
	 */
	public static void put_misc3() {
		int xbth, xbthb, xfos, xsrh, xstl, xdis, xsave, xdev;
		String xinfra;
		PlayerMisc p_ptr;
		
		IO.clear_from(14);
		p_ptr	= Player.py.misc;
		xbth	= p_ptr.bth + p_ptr.ptohit * Constants.BTH_PLUS_ADJ
				+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_BTH] * p_ptr.lev);
		xbthb = p_ptr.bthb + p_ptr.ptohit * Constants.BTH_PLUS_ADJ
				+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_BTHB] * p_ptr.lev);
		/* this results in a range from 0 to 29 */
		xfos	= 40 - p_ptr.fos;
		if (xfos < 0) xfos = 0;
		xsrh	= p_ptr.srh;
		/* this results in a range from 0 to 9 */
		xstl	= p_ptr.stl + 1;
		xdis	= p_ptr.disarm + 2 * todis_adj() + stat_adj(Constants.A_INT)
				+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_DISARM] * p_ptr.lev / 3);
		xsave	= p_ptr.save + stat_adj(Constants.A_WIS)
				+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_SAVE] * p_ptr.lev / 3);
		xdev	= p_ptr.save + stat_adj(Constants.A_INT)
				+ (Player.class_level_adj[p_ptr.pclass][Constants.CLA_DEVICE] * p_ptr.lev / 3);
		
		xinfra = String.format("%d feet", Player.py.flags.see_infra * 10);
		
		IO.put_buffer("(Miscellaneous Abilities)", 15, 25);
		IO.put_buffer("Fighting    :", 16, 1);
		IO.put_buffer(likert (xbth, 12), 16, 15);
		IO.put_buffer("Bows/Throw  :", 17, 1);
		IO.put_buffer(likert (xbthb, 12), 17, 15);
		IO.put_buffer("Saving Throw:", 18, 1);
		IO.put_buffer(likert (xsave, 6), 18, 15);
		
		IO.put_buffer("Stealth     :", 16, 28);
		IO.put_buffer(likert (xstl, 1), 16, 42);
		IO.put_buffer("Disarming   :", 17, 28);
		IO.put_buffer(likert (xdis, 8), 17, 42);
		IO.put_buffer("Magic Device:", 18, 28);
		IO.put_buffer(likert (xdev, 6), 18, 42);
		
		IO.put_buffer("Perception  :", 16, 55);
		IO.put_buffer(likert (xfos, 3), 16, 69);
		IO.put_buffer("Searching   :", 17, 55);
		IO.put_buffer(likert (xsrh, 6), 17, 69);
		IO.put_buffer("Infra-Vision:", 18, 55);
		IO.put_buffer(xinfra, 18, 69);
	}
	
	/**
	 * Prints out all character information. -RAK-
	 */
	public static void display_char() {
		put_character();
		put_misc1();
		put_stats();
		put_misc2();
		put_misc3();
	}
	
	/**
	 * Gets a name for the character -JWT-
	 */
	public static void get_name() {
		IO.prt("Enter your player's name  [press <RETURN> when finished]", 21, 2);
		IO.put_buffer(blank_string.substring(BLANK_LENGTH - 23), 2, 15);
		if ((Player.py.misc.name = IO.get_string(2, 15, 23)).isEmpty() || Player.py.misc.name.isEmpty()) {
			Player.py.misc.name = user_name();
			IO.put_buffer(Player.py.misc.name, 2, 15);
		}
		IO.clear_from(20);
	}
	
	private static String user_name() {
		return getlogin();
	}
	
	private static String getlogin() {
		String cp;
		
		if ((cp = System.getenv("USER")) == null) {
			cp = "player";
		}
		return cp;
	}
	
	/**
	 * Changes the name of the character -JWT-
	 */
	public static void change_name() {
		char c;
		boolean flag;
		String temp;
		
		flag = false;
		display_char();
		do {
			IO.prt("<f>ile character description. <c>hange character name.", 21, 2);
			c = IO.inkey();
			switch(c)
			{
			case 'c':
				get_name();
				flag = true;
				break;
			case 'f':
				IO.prt("File name:", 0, 0);
				if ((temp = IO.get_string(0, 10, 60)).length() > 0 && temp.charAt(0) != '\0') {
					if (Files.file_character(temp)) {
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
	 * @param item_val - Index in inventory array of item to destroy
	 */
	public static void inven_destroy(int item_val) {
		int j;
		InvenType i_ptr;
		
		i_ptr = Treasure.inventory[item_val];
		if ((i_ptr.number > 1) && (i_ptr.subval <= Constants.ITEM_SINGLE_STACK_MAX)) {
			i_ptr.number--;
			Treasure.inven_weight -= i_ptr.weight;
		} else {
			Treasure.inven_weight -= i_ptr.weight * i_ptr.number;
			for (j = item_val; j < Treasure.inven_ctr - 1; j++) {
				Treasure.inventory[j + 1].copyInto(Treasure.inventory[j]);
			}
			Desc.invcopy(Treasure.inventory[Treasure.inven_ctr - 1], Constants.OBJ_NOTHING);
			Treasure.inven_ctr--;
		}
		Player.py.flags.status |= Constants.PY_STR_WGT;
	}
	
	/**
	 * Copies the object in the second argument over the first argument.
	 * However, the second always gets a number of one except for ammo etc.
	 * 
	 * @param s_ptr - Item receiving information
	 * @param i_ptr - Item being copied
	 */
	public static void take_one_item(InvenType s_ptr, InvenType i_ptr) {
		i_ptr.copyInto(s_ptr);
		if ((s_ptr.number > 1) && (s_ptr.subval >= Constants.ITEM_SINGLE_STACK_MIN)
				&& (s_ptr.subval <= Constants.ITEM_SINGLE_STACK_MAX)) {
			s_ptr.number = 1;
		}
	}
	
	/**
	 * Drops an item from t.inventory to given location -RAK-
	 * 
	 * @param item_val - Index in inventory array of item to drop
	 * @param drop_all - Whether to drop all of that item
	 */
	public static void inven_drop(int item_val, boolean drop_all) {
		int i;
		InvenType i_ptr;
		String prt1;
		String prt2;
		
		if (Variable.cave[Player.char_row][Player.char_col].tptr != 0) {
			Moria3.delete_object(Player.char_row, Player.char_col);
		}
		i = Misc1.popt();
		i_ptr = Treasure.inventory[item_val];
		i_ptr.copyInto(Treasure.t_list[i]);
		Variable.cave[Player.char_row][Player.char_col].tptr = i;
		
		if (item_val >= Constants.INVEN_WIELD) {
			Moria1.takeoff(item_val, -1);
		} else {
			if (drop_all || i_ptr.number == 1) {
				Treasure.inven_weight -= i_ptr.weight * i_ptr.number;
				Treasure.inven_ctr--;
				while (item_val < Treasure.inven_ctr) {
					Treasure.inventory[item_val + 1].copyInto(Treasure.inventory[item_val]);
					item_val++;
				}
				Desc.invcopy(Treasure.inventory[Treasure.inven_ctr], Constants.OBJ_NOTHING);
			} else {
				Treasure.t_list[i].number = 1;
				Treasure.inven_weight -= i_ptr.weight;
				i_ptr.number--;
			}
			prt1 = Desc.objdes(Treasure.t_list[i], true);
			prt2 = String.format("Dropped %s", prt1);
			IO.msg_print(prt2);
		}
		Player.py.flags.status |= Constants.PY_STR_WGT;
	}
	
	private static boolean set_inven_damage(int typ, InvenType inv) {
		switch(typ)
		{
		case Sets.set_corrodes:
			return Sets.set_corrodes(inv);
		case Sets.set_flammable:
			return Sets.set_flammable(inv);
		case Sets.set_frost_destroy:
			return Sets.set_frost_destroy(inv);
		case Sets.set_acid_affect:
			return Sets.set_acid_affect(inv);
		case Sets.set_lightning_destroy:
			return Sets.set_lightning_destroy(inv);
		case Sets.set_null:
			return Sets.set_null(inv);
		case Sets.set_acid_destroy:
			return Sets.set_acid_destroy(inv);
		case Sets.set_fire_destroy:
			return Sets.set_fire_destroy(inv);
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
	public static int inven_damage(int typ, int perc) {
		int i, j;
		
		j = 0;
		for (i = 0; i < Treasure.inven_ctr; i++) {
			if (set_inven_damage(typ, Treasure.inventory[i]) && (Misc1.randint(100) < perc)) {
				inven_destroy(i);
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
	public static int weight_limit() {
		int weight_cap;
		
		weight_cap = Player.py.stats.use_stat[Constants.A_STR] * Constants.PLAYER_WEIGHT_CAP + Player.py.misc.wt;
		if (weight_cap > 3000) weight_cap = 3000;
		return weight_cap;
	}
	
	/**
	 * Check if an object can be picked up
	 * 
	 * @param t_ptr - The object being picked up
	 * @return Whether the object can fit in the player's inventory
	 */
	public static boolean inven_check_num(InvenType t_ptr) {
		/* this code must be identical to the inven_carry() code below */
		int i;
		
		if (Treasure.inven_ctr < Constants.INVEN_WIELD) {
			return true;
		} else if (t_ptr.subval >= Constants.ITEM_SINGLE_STACK_MIN) {
			for (i = 0; i < Treasure.inven_ctr; i++) {
				if (Treasure.inventory[i].tval == t_ptr.tval
				&& Treasure.inventory[i].subval == t_ptr.subval
				/* make sure the number field doesn't overflow */
				&& (Treasure.inventory[i].number + t_ptr.number < 256)
				/* they always stack (subval < 192), or else they have same p1 */
				&& ((t_ptr.subval < Constants.ITEM_GROUP_MIN) || (Treasure.inventory[i].p1 == t_ptr.p1))
				/* only stack if both or neither are identified */
				&& (Desc.known1_p(Treasure.inventory[i]) == Desc.known1_p(t_ptr))) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Check if picking up an object would change the player's speed
	 * 
	 * @param i_ptr - The object being picked up
	 * @return Whether picking up the object would change the player's speed
	 */
	public static boolean inven_check_weight(InvenType i_ptr) {
		int i, new_inven_weight;
		
		i = weight_limit();
		new_inven_weight = i_ptr.number * i_ptr.weight + Treasure.inven_weight;
		if (i < new_inven_weight) {
			i = new_inven_weight / (i + 1);
		} else {
			i = 0;
		}
		
		return Variable.pack_heavy == i;
	}
	
	/**
	 * Are we strong enough for the current pack and weapon? -CJS-
	 */
	public static void check_strength() {
		int i;
		InvenType i_ptr;
		
		i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
		if (i_ptr.tval != Constants.TV_NOTHING
				&& (Player.py.stats.use_stat[Constants.A_STR] * 15 < i_ptr.weight)) {
			if (!Variable.weapon_heavy) {
				IO.msg_print("You have trouble wielding such a heavy weapon.");
				Variable.weapon_heavy = true;
				Moria1.calc_bonuses();
			}
		} else if (Variable.weapon_heavy) {
			Variable.weapon_heavy = false;
			if (i_ptr.tval != Constants.TV_NOTHING) {
				IO.msg_print("You are strong enough to wield your weapon.");
			}
			Moria1.calc_bonuses();
		}
		i = weight_limit();
		if (i < Treasure.inven_weight) {
			i = Treasure.inven_weight / (i + 1);
		} else {
			i = 0;
		}
		if (Variable.pack_heavy != i) {
			if (Variable.pack_heavy < i) {
				IO.msg_print("Your pack is so heavy that it slows you down.");
			} else {
				IO.msg_print("You move more easily under the weight of your pack.");
			}
			Moria1.change_speed(i - Variable.pack_heavy);
			Variable.pack_heavy = i;
		}
		Player.py.flags.status &= ~Constants.PY_STR_WGT;
	}
	
	/**
	 * Add an item to players inventory.
	 * Return the item position for a description if needed. -RAK-
	 * 
	 * @param i_ptr - The item being added to the player's inventory
	 * @return The index in the inventory array where the item was placed
	 */
	public static int inven_carry(InvenType i_ptr) {
		/* this code must be identical to the inven_check_num() code above */
		int locn, i;
		int typ, subt;
		InvenType t_ptr;
		boolean known1p, always_known1p;
		
		typ = i_ptr.tval;
		subt = i_ptr.subval;
		known1p = Desc.known1_p(i_ptr);
		always_known1p = (Desc.object_offset(i_ptr) == -1);
		/* Now, check to see if player can carry object  */
		for (locn = 0; ; locn++) {
			t_ptr = Treasure.inventory[locn];
			if ((typ == t_ptr.tval) && (subt == t_ptr.subval)
					&& (subt >= Constants.ITEM_SINGLE_STACK_MIN)
					&& (t_ptr.number + i_ptr.number < 256)
					&& ((subt < Constants.ITEM_GROUP_MIN) || (t_ptr.p1 == i_ptr.p1))
					/* only stack if both or neither are identified */
					&& (known1p == Desc.known1_p(t_ptr))) {
				t_ptr.number += i_ptr.number;
				break;
				
			/* For items which are always known1p, i.e. never have a 'color',
			 * insert them into the t.inventory in sorted order.  */
			} else if ((typ == t_ptr.tval && subt < t_ptr.subval && always_known1p) || (typ > t_ptr.tval)) {
				for (i = Treasure.inven_ctr - 1; i >= locn; i--) {
					Treasure.inventory[i].copyInto(Treasure.inventory[i + 1]);
				}
				i_ptr.copyInto(Treasure.inventory[locn]);
				Treasure.inven_ctr++;
				break;
			}
		}
		
		Treasure.inven_weight += i_ptr.number * i_ptr.weight;
		Player.py.flags.status |= Constants.PY_STR_WGT;
		return locn;
	}
	
	/**
	 * Returns spell chance of failure for spell -RAK-
	 * 
	 * @param spell - The spell being checked
	 * @return The percent chance of spell failing
	 */
	public static int spell_chance(int spell) {
		SpellType s_ptr;
		int chance;
		int stat;
		
		s_ptr = Player.magic_spell[Player.py.misc.pclass - 1][spell];
		chance = s_ptr.sfail - 3 * (Player.py.misc.lev - s_ptr.slevel);
		if (Player.Class[Player.py.misc.pclass].spell == Constants.MAGE) {
			stat = Constants.A_INT;
		} else {
			stat = Constants.A_WIS;
		}
		chance -= 3 * (stat_adj(stat) - 1);
		if (s_ptr.smana > Player.py.misc.cmana) {
			chance += 5 * (s_ptr.smana - Player.py.misc.cmana);
		}
		if (chance > 95) {
			chance = 95;
		} else if (chance < 5) {
			chance = 5;
		}
		return chance;
	}
	
	/* Print list of spells					-RAK-	*/
	/* if nonconsec is -1: spells numbered consecutively from 'a' to 'a'+num
	                  >=0: spells numbered by offset from nonconsec */
	public static void print_spells(int[] spell, int num, boolean comment, int nonconsec) {
		int i, j;
		String out_val;
		SpellType s_ptr;
		int col, offset;
		String p;
		char spell_char;
		
		if (comment) {
			col = 22;
		} else {
			col = 31;
		}
		offset = ((Player.Class[Player.py.misc.pclass].spell == Constants.MAGE) ? Constants.SPELL_OFFSET : Constants.PRAYER_OFFSET);
		IO.erase_line(1, col);
		IO.put_buffer("Name", 1, col + 5);
		IO.put_buffer("Lv Mana Fail", 1, col + 35);
		/* only show the first 22 choices */
		if (num > 22) {
			num = 22;
		}
		for (i = 0; i < num; i++) {
			j = spell[i];
			s_ptr = Player.magic_spell[Player.py.misc.pclass - 1][j];
			if (!comment) {
				p = "";
			} else if ((Player.spell_forgotten & (1L << j)) != 0) {
				p = " forgotten";
			} else if ((Player.spell_learned & (1L << j)) == 0) {
				p = " unknown";
			} else if ((Player.spell_worked & (1L << j)) == 0) {
				p = " untried";
			} else {
				p = "";
			}
			/* determine whether or not to leave holes in character choices,
			   nonconsec -1 when learning spells, consec offset>=0 when asking which
			   spell to cast */
			if (nonconsec == -1) {
				spell_char = (char)('a' + i);
			} else {
				spell_char = (char)('a' + j - nonconsec);
			}
			out_val = String.format("  %c) %-30s%2d %4d %3d%%%s", spell_char, Player.spell_names[j + offset], s_ptr.slevel, s_ptr.smana, spell_chance(j), p);
			IO.prt(out_val, 2 + i, col);
		}
	}
	
	/* Returns spell pointer				-RAK-	*/
	public static boolean get_spell(int[] spell, int num, IntPointer sn, IntPointer sc, String prompt, int first_spell) {
		SpellType s_ptr;
		boolean flag, redraw;
		int offset, i;
		CharPointer choice = new CharPointer();
		String out_str, tmp_str;
		
		sn.value(-1);
		flag = false;
		out_str = String.format("(Spells %c-%c, *=List, <ESCAPE>=exit) %s", spell[0] + 'a' - first_spell, spell[num - 1] + 'a' - first_spell, prompt);
		redraw = false;
		offset = ((Player.Class[Player.py.misc.pclass].spell == Constants.MAGE) ?
				Constants.SPELL_OFFSET : Constants.PRAYER_OFFSET);
		while (!flag && IO.get_com(out_str, choice)) {
			if (Character.isUpperCase(choice.value())) {
				sn.value(choice.value() - 'A' + first_spell);
				/* verify that this is in spell[], at most 22 entries in spell[] */
				for (i = 0; i < num; i++) {
					if (sn.value() == spell[i]) {
						break;
					}
				}
				if (i == num) {
					sn.value(-2);
				} else {
					s_ptr = Player.magic_spell[Player.py.misc.pclass - 1][sn.value()];
					tmp_str = String.format("Cast %s (%d mana, %d%% fail)?", Player.spell_names[sn.value() + offset], s_ptr.smana, spell_chance(sn.value()));
					if (IO.get_check(tmp_str)) {
						flag = true;
					} else {
						sn.value(-1);
					}
				}
			} else if (Character.isLowerCase(choice.value())) {
				sn.value(choice.value() - 'a' + first_spell);
				/* verify that this is in spell[], at most 22 entries in spell[] */
				for (i = 0; i < num; i++) {
					if (sn.value() == spell[i]) {
						break;
					}
				}
				if (i == num) {
					sn.value(-2);
				} else {
					flag = true;
				}
			} else if (choice.value() == '*') {
				/* only do this drawing once */
				if (!redraw) {
					IO.save_screen();
					redraw = true;
					print_spells (spell, num, false, first_spell);
				}
			} else if (Character.isLetter(choice.value())) {
				sn.value(-2);
			} else {
				sn.value(-1);
				IO.bell();
			}
			if (sn.value() == -2) {
				tmp_str = String.format("You don't know that %s.", ((offset == Constants.SPELL_OFFSET) ? "spell" : "prayer"));
				IO.msg_print(tmp_str);
			}
		}
		if (redraw) {
			IO.restore_screen ();
		}
		
		IO.erase_line(Constants.MSG_LINE, 0);
		if (flag) {
			sc.value(spell_chance(sn.value()));
		}
		
		return flag;
	}


	/**
	 * Calculate number of spells player should have, and learn/forget spells
	 * until that number is met -JEW-
	 * 
	 * @param stat - 
	 */
	public static void calc_spells(int stat) {
		int i;
		int mask;
		int spell_flag;
		int j, offset;
		int num_allowed = 0, new_spells, num_known, levels;
		String tmp_str;
		String p;
		PlayerMisc p_ptr;
		SpellType[] msp_ptr;
		
		p_ptr = Player.py.misc;
		msp_ptr = Player.magic_spell[p_ptr.pclass - 1];
		if (stat == Constants.A_INT) {
			p = "spell";
			offset = Constants.SPELL_OFFSET;
		} else {
			p = "prayer";
			offset = Constants.PRAYER_OFFSET;
	    }
		
		/* check to see if know any spells greater than level, eliminate them */
		for (i = 31, mask = 0x80000000; mask != 0; mask >>= 1, i--) {
			if ((mask & Player.spell_learned) != 0) {
				if (msp_ptr[i].slevel > p_ptr.lev) {
					Player.spell_learned &= ~mask;
					Player.spell_forgotten |= mask;
					tmp_str = String.format("You have forgotten the %s of %s.", p, Player.spell_names[i + offset]);
					IO.msg_print(tmp_str);
				} else {
					break;
				}
			}
		}
		
		/* calc number of spells allowed */
		levels = p_ptr.lev - Player.Class[p_ptr.pclass].first_spell_lev + 1;
		switch(stat_adj(stat)) {
		case 0:			num_allowed = 0; break;
		case 1: case 2: case 3: num_allowed = 1 * levels; break;
		case 4: case 5: num_allowed = 3 * levels / 2; break;
		case 6:		    num_allowed = 2 * levels; break;
		case 7:		    num_allowed = 5 * levels / 2; break;
		default: break;
		}
		
		num_known = 0;
		for (mask = 0x1; mask != 0; mask <<= 1) {
			if ((mask & Player.spell_learned) != 0) {
				num_known++;
			}
		}
		new_spells = num_allowed - num_known;
		
		if (new_spells > 0) {
			/* remember forgotten spells while forgotten spells exist of new_spells
			 * positive, remember the spells in the order that they were learned */
			for (i = 0; (Player.spell_forgotten != 0 && new_spells != 0 && (i < num_allowed) && (i < 32)); i++) {
				/* j is (i+1)th spell learned */
				j = Player.spell_order[i];
				/* shifting by amounts greater than number of bits in long gives
				 * an undefined result, so don't shift for unknown spells */
				if (j == 99) {
					mask = 0x0;
				} else {
					mask = 1 << j;
				}
				if ((mask & Player.spell_forgotten) != 0) {
					if (msp_ptr[j].slevel <= p_ptr.lev) {
						new_spells--;
						Player.spell_forgotten &= ~mask;
						Player.spell_learned |= mask;
						tmp_str = String.format("You have remembered the %s of %s.", p, Player.spell_names[j + offset]);
						IO.msg_print(tmp_str);
					} else {
						num_allowed++;
					}
				}
			}
			
			if (new_spells > 0) {
				/* determine which spells player can learn */
				/* must check all spells here, in gain_spell() we actually check
				 * if the books are present */
				spell_flag = 0x7FFFFFFF & ~Player.spell_learned;
				
				mask = 0x1;
				i = 0;
				for (j = 0, mask = 0x1; spell_flag != 0; mask <<= 1, j++) {
					if ((spell_flag & mask) != 0) {
						spell_flag &= ~mask;
						if (msp_ptr[j].slevel <= p_ptr.lev) {
							i++;
						}
					}
				}
				
				if (new_spells > i) {
					new_spells = i;
				}
			}
		} else if (new_spells < 0) {
			/* forget spells until new_spells zero or no more spells know, spells
			 * are forgotten in the opposite order that they were learned */
			for (i = 31; new_spells != 0 && Player.spell_learned != 0; i--) {
				/* j is the (i+1)th spell learned */
				j = Player.spell_order[i];
				/* shifting by amounts greater than number of bits in long gives
				 * an undefined result, so don't shift for unknown spells */
				if (j == 99) {
					mask = 0x0;
				} else {
					mask = 1 << j;
				}
				
				if ((mask & Player.spell_learned) != 0) {
					Player.spell_learned &= ~mask;
					Player.spell_forgotten |= mask;
					new_spells++;
					tmp_str = String.format("You have forgotten the %s of %s.", p, Player.spell_names[j + offset]);
					IO.msg_print(tmp_str);
				}
			}
			
			new_spells = 0;
		}
		
		if (new_spells != Player.py.flags.new_spells) {
			if (new_spells > 0 && Player.py.flags.new_spells == 0) {
				tmp_str = String.format("You can learn some new %ss now.", p);
				IO.msg_print(tmp_str);
			}
			
			Player.py.flags.new_spells = new_spells;
			Player.py.flags.status |= Constants.PY_STUDY;
		}
	}
	
	/**
	 * Gain spells when player wants to -JEW-
	 */
	public static void gain_spells() {
		CharPointer query = new CharPointer();
		int stat, diff_spells, new_spells;
		int[] spells = new int[31];
		int offset, last_known;
		int i, j;
		long spell_flag, mask;
		String tmp_str = "";
		PlayerMisc p_ptr;
		SpellType[] msp_ptr;
		
		/* Priests don't need light because they get spells from their god,
		 * so only fail when can't see if player has MAGE spells.  This check
		 * is done below.  */
		if (Player.py.flags.confused > 0) {
			IO.msg_print("You are too confused.");
			return;
		}
		
		new_spells = Player.py.flags.new_spells;
		diff_spells = 0;
		p_ptr = Player.py.misc;
		msp_ptr = Player.magic_spell[p_ptr.pclass - 1];
		if (Player.Class[p_ptr.pclass].spell == Constants.MAGE) {
			stat = Constants.A_INT;
			offset = Constants.SPELL_OFFSET;
			
			/* People with MAGE spells can't learn spells if they can't read their books.  */
			if (Player.py.flags.blind > 0) {
				IO.msg_print("You can't see to read your spell book!");
				return;
			} else if (Moria1.no_light()) {
				IO.msg_print("You have no light to read by.");
				return;
			}
		} else {
			stat = Constants.A_WIS;
			offset = Constants.PRAYER_OFFSET;
		}
		
		for (last_known = 0; last_known < 32; last_known++) {
			if (Player.spell_order[last_known] == 99) {
				break;
			}
		}
		
		if (new_spells == 0) {
			tmp_str = String.format("You can't learn any new %ss!", ((stat == Constants.A_INT) ? "spell" : "prayer"));
			IO.msg_print(tmp_str);
			Variable.free_turn_flag = true;
		} else {
			/* determine which spells player can learn */
			/* mages need the book to learn a spell, priests do not need the book */
			if (stat == Constants.A_INT) {
				spell_flag = 0;
				for (i = 0; i < Treasure.inven_ctr; i++) {
					if (Treasure.inventory[i].tval == Constants.TV_MAGIC_BOOK) {
						spell_flag |= Treasure.inventory[i].flags;
					}
				}
			} else {
				spell_flag = 0x7FFFFFFF;
			}
			
			/* clear bits for spells already learned */
			spell_flag &= ~Player.spell_learned;
			
			mask = 0x1;
			i = 0;
			for (j = 0, mask = 0x1; spell_flag != 0; mask <<= 1, j++) {
				if ((spell_flag & mask) != 0) {
					spell_flag &= ~mask;
					if (msp_ptr[j].slevel <= p_ptr.lev) {
						spells[i] = j;
						i++;
					}
				}
			}
			
			if (new_spells > i) {
				IO.msg_print("You seem to be missing a book.");
				diff_spells = new_spells - i;
				new_spells = i;
			}
			if (new_spells != 0 && stat == Constants.A_INT) {
				/* get to choose which mage spells will be learned */
				IO.save_screen();
				print_spells (spells, i, false, -1);
				while (new_spells != 0 && IO.get_com("Learn which spell?", query)) {
					j = query.value() - 'a';
					/* test j < 23 in case i is greater than 22, only 22 spells
					   are actually shown on the screen, so limit choice to those */
					if (j >= 0 && j < i && j < 22) {
						new_spells--;
						Player.spell_learned |= 1L << spells[j];
						Player.spell_order[last_known++] = spells[j];
						for (; j <= i - 1; j++) {
							spells[j] = spells[j + 1];
						}
						i--;
						IO.erase_line (j + 1, 31);
						print_spells (spells, i, false, -1);
					} else {
						IO.bell();
					}
				}
				IO.restore_screen();
			} else {
				/* pick a prayer at random */
				while (new_spells != 0) {
					j = Misc1.randint(i) - 1;
					Player.spell_learned |= 1L << spells[j];
					Player.spell_order[last_known++] = spells[j];
					tmp_str = String.format("You have learned the prayer of %s.", Player.spell_names[spells[j] + offset]);
					IO.msg_print(tmp_str);
					for (; j <= i - 1; j++) {
						spells[j] = spells[j + 1];
					}
					i--;
					new_spells--;
				}
			}
			Player.py.flags.new_spells = new_spells + diff_spells;
			if (Player.py.flags.new_spells == 0) {
				Player.py.flags.status |= Constants.PY_STUDY;
			}
			/* set the mana for first level characters when they learn their
			 * first spell */
			if (Player.py.misc.mana == 0) {
				calc_mana(stat);
			}
		}
	}
	
	/** 
	 * Gain some mana if you know at least one spell -RAK-
	 * 
	 * @param stat - 
	 */
	public static void calc_mana(int stat) {
		int new_mana, levels;
		PlayerMisc p_ptr;
		int value;
		
		p_ptr = Player.py.misc;
		if (Player.spell_learned != 0) {
			levels = p_ptr.lev - Player.Class[p_ptr.pclass].first_spell_lev + 1;
			switch(stat_adj(stat))
			{
			case 0: new_mana = 0; break;
			case 1: case 2: new_mana = 1 * levels; break;
			case 3: new_mana = 3 * levels / 2; break;
			case 4: new_mana = 2 * levels; break;
			case 5: new_mana = 5 * levels / 2; break;
			case 6: new_mana = 3 * levels; break;
			case 7: new_mana = 4 * levels; break;
			default: new_mana = 0;
			}
			/* increment mana by one, so that first level chars have 2 mana */
			if (new_mana > 0) {
				new_mana++;
			}
			
			/* mana can be zero when creating character */
			if (p_ptr.mana != new_mana) {
				if (p_ptr.mana != 0) {
					/* change current mana proportionately to change of max mana,
					   divide first to avoid overflow, little loss of accuracy */
					value = ((p_ptr.cmana << 16) + p_ptr.cmana_frac) / p_ptr.mana * new_mana;
					p_ptr.cmana = value >> 16;
					p_ptr.cmana_frac = value & 0xFFFF;
				} else {
					p_ptr.cmana = new_mana;
					p_ptr.cmana_frac = 0;
				}
				p_ptr.mana = new_mana;
				/* can't print mana here, may be in store or t.inventory mode */
				Player.py.flags.status |= Constants.PY_MANA;
			}
		} else if (p_ptr.mana != 0) {
			p_ptr.mana = 0;
			p_ptr.cmana = 0;
			/* can't print mana here, may be in store or t.inventory mode */
			Player.py.flags.status |= Constants.PY_MANA;
		}
	}
	
	/**
	 * Increases hit points and level -RAK-
	 */
	public static void gain_level() {
		int dif_exp, need_exp;
		String out_val;
		PlayerMisc p_ptr;
		ClassType c_ptr;
		
		p_ptr = Player.py.misc;
		p_ptr.lev++;
		out_val = String.format("Welcome to level %d.", p_ptr.lev);
		IO.msg_print(out_val);
		calc_hitpoints();
		
		need_exp = Player.player_exp[p_ptr.lev - 1] * p_ptr.expfact / 100;
		if (p_ptr.exp > need_exp) {
			/* lose some of the 'extra' exp when gaining several levels at once */
			dif_exp = p_ptr.exp - need_exp;
			p_ptr.exp = need_exp + (dif_exp / 2);
		}
		prt_level();
		prt_title();
		c_ptr = Player.Class[p_ptr.pclass];
		if (c_ptr.spell == Constants.MAGE) {
			calc_spells(Constants.A_INT);
			calc_mana(Constants.A_INT);
		} else if (c_ptr.spell == Constants.PRIEST) {
	      calc_spells(Constants.A_WIS);
	      calc_mana(Constants.A_WIS);
	    }
	}

	/**
	 * Prints experience -RAK-
	 */
	public static void prt_experience() {
		PlayerMisc p_ptr;
		
		p_ptr = Player.py.misc;
		if (p_ptr.exp > Constants.MAX_EXP) {
			p_ptr.exp = Constants.MAX_EXP;
		}
		while (p_ptr.lev < Constants.MAX_PLAYER_LEVEL
				&& (Player.player_exp[p_ptr.lev - 1] * p_ptr.expfact / 100) <= p_ptr.exp) {
			gain_level();
		}
		if (p_ptr.exp > p_ptr.max_exp) {
			p_ptr.max_exp = p_ptr.exp;
		}
		prt_long(p_ptr.exp, 14, Constants.STAT_COLUMN + 6);
	}
	
	/**
	 * Calculate the player's hit points
	 */
	public static void calc_hitpoints() {
		int hitpoints;
		PlayerMisc p_ptr;
		int value;
		
		p_ptr = Player.py.misc;
		hitpoints = Player.player_hp[p_ptr.lev - 1] + (con_adj() * p_ptr.lev);
		/* always give at least one point per level + 1 */
		if (hitpoints < (p_ptr.lev + 1)) {
			hitpoints = p_ptr.lev + 1;
		}
		
		if ((Player.py.flags.status & Constants.PY_HERO) != 0) {
			hitpoints += 10;
		}
		if ((Player.py.flags.status & Constants.PY_SHERO) != 0) {
			hitpoints += 20;
		}
		
		/* mhp can equal zero while character is being created */
		if ((hitpoints != p_ptr.mhp) && (p_ptr.mhp != 0)) {
			/* change current hit points proportionately to change of mhp,
			 * divide first to avoid overflow, little loss of accuracy */
			value = ((p_ptr.chp << 16) + p_ptr.chp_frac) / p_ptr.mhp * hitpoints;
			p_ptr.chp = value >> 16;
			p_ptr.chp_frac = value & 0xFFFF;
			p_ptr.mhp = hitpoints;
			
			/* can't print hit points here, may be in store or t.inventory mode */
			Player.py.flags.status |= Constants.PY_HP;
		}
	}
	
	/**
	 * Inserts a string into a string
	 * 
	 * @param object_str - Original string
	 * @param mtc_str - String to replace
	 * @param insert - String to replace with
	 * @return The final string
	 */
	public static String insert_str(String object_str, String mtc_str, String insert) {
		String out_val;
		out_val = object_str.replaceAll(mtc_str, insert);
		return out_val;
		
		/*
		int obj_len;
		String bound, pc;
		int i, mtc_len;
		String temp_obj, temp_mtc;
		String out_val;
		
		mtc_len = mtc_str.length();
		obj_len = object_str.length();
		bound = object_str + obj_len - mtc_len;
		for (pc = object_str; pc <= bound; pc++) {
			temp_obj = pc;
			temp_mtc = mtc_str;
			for (i = 0; i < mtc_len; i++) {
				if (*temp_obj++ != *temp_mtc++) {
					break;
				}
			}
			if (i == mtc_len) {
				break;
			}
		}
		
		if (pc <= bound) {
			strncpy(out_val, object_str, (pc - object_str));
			/* Turbo C needs int for array index.  */
		/*
			out_val[pc - object_str] = '\0';
			if (insert) {
				strcat(out_val, insert);
			}
			strcat(out_val, (char *)(pc+mtc_len));
			strcpy(object_str, out_val);
		}
		*/
	}
	
	public static String insert_lnum(String object_str, String mtc_str, int number, boolean show_sign) {
		String str1, str2;
		String out_val;
		if (object_str.indexOf(mtc_str) < 0) return object_str;
		str1 = object_str.substring(0, object_str.indexOf(mtc_str));
		str2 = object_str.substring(object_str.indexOf(mtc_str) + mtc_str.length());
		if ((number > 0) && show_sign) {
			out_val = String.format("%s+%d%s", str1, number, str2);
		} else {
			out_val = String.format("%s%d%s", str1, number, str2);
		}
		return out_val;
		
		/*
		int mlen;
		char[] str1 = new char[Constants.VTYPESIZ], str2 = new char[Constants.VTYPESIZ];
		char *string, *tmp_str;
		int flag;
		
		flag = 1;
		mlen = strlen(mtc_str);
		tmp_str = object_str;
		do {
			string = index(tmp_str, mtc_str[0]);
			if (string == 0)
				flag = 0;
			else
			{
				flag = strncmp(string, mtc_str, mlen);
				if (flag)
					tmp_str = string+1;
			}
		} while (flag);
		
		if (string)
		{
			strncpy(str1, object_str, string - object_str);
			str1[string - object_str] = '\0';
			strcpy(str2, string + mlen);
			if ((number >= 0) && (show_sign))
				object_str = String.format("%s+%ld%s", str1, number, str2);
			else
				object_str = String.format("%s%ld%s", str1, number, str2);
		}
		*/
	}
	
	/**
	 * Lets anyone enter wizard mode after a disclaimer... - JEW-
	 */
	public static boolean enter_wiz_mode() {
		boolean answer = false;
		
		if (Variable.noscore == 0) {
			IO.msg_print("Wizard mode is for debugging and experimenting.");
			answer = IO.get_check("The game will not be scored if you enter wizard mode. Are you sure?");
		}
		if (Variable.noscore != 0 || answer) {
			Variable.noscore = 1;
			Variable.wizard = true;
			return true;
		}
		return false;
	}
	
	/**
	 * Weapon weight VS strength and dexterity -RAK-
	 * 
	 * @param weight - Weight of the weapon
	 * @param wtohit - Stores the weapon's tohit
	 * @return The value of the attack blow
	 */
	public static int attack_blows(int weight, IntPointer wtohit) {
		int adj_weight;
		int str_index, dex_index, s, d;
		
		s = Player.py.stats.use_stat[Constants.A_STR];
		d = Player.py.stats.use_stat[Constants.A_DEX];
		if (s * 15 < weight) {
			wtohit.value(s * 15 - weight);
			return 1;
		} else {
			wtohit.value(0);
			if (d < 10) {
				dex_index = 0;
			} else if (d < 19) {
				dex_index = 1;
			} else if (d < 68) {
				dex_index = 2;
			} else if (d < 108) {
				dex_index = 3;
			} else if (d < 118) {
				dex_index = 4;
			} else {
				dex_index = 5;
			}
			
			adj_weight = (s * 10 / weight);
			if (adj_weight < 2) {
				str_index = 0;
			} else if (adj_weight < 3) {
				str_index = 1;
			} else if (adj_weight < 4) {
				str_index = 2;
			} else if (adj_weight < 5) {
				str_index = 3;
			} else if (adj_weight < 7) {
				str_index = 4;
			} else if (adj_weight < 9) {
				str_index = 5;
			} else {
				str_index = 6;
			}
			
			return Tables.blows_table[str_index][dex_index];
		}
	}
	
	/**
	 * Special damage due to magical abilities of object -RAK-
	 * 
	 * @param i_ptr - Weapon being used
	 * @param tdam - Original damage of the attack
	 * @param monster - Index in the creature list of the monster being attacked
	 * @return Final damage of the attack
	 */
	public static int tot_dam(InvenType i_ptr, int tdam, int monster) {
		CreatureType m_ptr;
		MonsterRecallType r_ptr;
		
		if ((i_ptr.flags & Constants.TR_EGO_WEAPON) != 0
				&& (((i_ptr.tval >= Constants.TV_SLING_AMMO) && (i_ptr.tval <= Constants.TV_ARROW))
				|| ((i_ptr.tval >= Constants.TV_HAFTED) && (i_ptr.tval <= Constants.TV_SWORD))
				|| (i_ptr.tval == Constants.TV_FLASK))) {
			m_ptr = Monsters.c_list[monster];
			r_ptr = Variable.c_recall[monster];
			/* Slay Dragon  */
			if ((m_ptr.cdefense & Constants.CD_DRAGON) != 0
					&& (i_ptr.flags & Constants.TR_SLAY_DRAGON) != 0) {
				tdam = tdam * 4;
				r_ptr.r_cdefense |= Constants.CD_DRAGON;
			
			/* Slay Undead  */
			} else if ((m_ptr.cdefense & Constants.CD_UNDEAD) != 0 && (i_ptr.flags & Constants.TR_SLAY_UNDEAD) != 0) {
				tdam = tdam * 3;
				r_ptr.r_cdefense |= Constants.CD_UNDEAD;
			
			/* Slay Animal  */
			} else if ((m_ptr.cdefense & Constants.CD_ANIMAL) != 0 && (i_ptr.flags & Constants.TR_SLAY_ANIMAL) != 0) {
				tdam = tdam * 2;
				r_ptr.r_cdefense |= Constants.CD_ANIMAL;
			
			/* Slay Evil     */
			} else if ((m_ptr.cdefense & Constants.CD_EVIL) != 0 && (i_ptr.flags & Constants.TR_SLAY_EVIL) != 0) {
				tdam = tdam * 2;
				r_ptr.r_cdefense |= Constants.CD_EVIL;
			
			/* Frost	       */
			} else if ((m_ptr.cdefense & Constants.CD_FROST) != 0 && (i_ptr.flags & Constants.TR_FROST_BRAND) != 0) {
				tdam = tdam * 3 / 2;
				r_ptr.r_cdefense |= Constants.CD_FROST;
			
			/* Fire	      */
			} else if ((m_ptr.cdefense & Constants.CD_FIRE) != 0 && (i_ptr.flags & Constants.TR_FLAME_TONGUE) != 0) {
				tdam = tdam * 3 / 2;
				r_ptr.r_cdefense |= Constants.CD_FIRE;
			}
		}
		return tdam;
	}
	
	/**
	 * Critical hits, Nasty way to die. -RAK-
	 * 
	 * @param weight - 
	 * @param plus - 
	 * @param dam - 
	 * @param attack_type - 
	 * @return 
	 */
	public static int critical_blow(int weight, int plus, int dam, int attack_type) {
		int critical;
		
		critical = dam;
		/* Weight of weapon, plusses to hit, and character level all	    */
		/* contribute to the chance of a critical			   */
		if (Misc1.randint(5000) <= (weight + 5 * plus + (Player.class_level_adj[Player.py.misc.pclass][attack_type] * Player.py.misc.lev))) {
			weight += Misc1.randint(650);
			if (weight < 400) {
				critical = 2 * dam + 5;
				IO.msg_print("It was a good hit! (x2 damage)");
			} else if (weight < 700) {
				critical = 3 * dam + 10;
				IO.msg_print("It was an excellent hit! (x3 damage)");
			} else if (weight < 900) {
				critical = 4 * dam + 15;
				IO.msg_print("It was a superb hit! (x4 damage)");
			} else {
				critical = 5 * dam + 20;
				IO.msg_print("It was a *GREAT* hit! (x5 damage)");
			}
		}
		return critical;
	}
	
	/**
	 * Given direction "dir", returns new row, column location -RAK-
	 * 
	 * @param dir - Direction to move monster
	 * @param y - The vertical position of the monster, stores the new vertical position of the monster
	 * @param x - The horizontal position of the monster, stores the new horizontal position of the monster
	 * @return Whether the monster could move in the given direction
	 */
	public static boolean mmove(int dir, IntPointer y, IntPointer x) {
		int new_row, new_col;
		boolean bool;
		
		switch(dir)
		{
		case 1:
			new_row = y.value() + 1;
			new_col = x.value() - 1;
			break;
		case 2:
			new_row = y.value() + 1;
			new_col = x.value();
			break;
		case 3:
			new_row = y.value() + 1;
			new_col = x.value() + 1;
			break;
		case 4:
			new_row = y.value();
			new_col = x.value() - 1;
			break;
		case 5:
			new_row = y.value();
			new_col = x.value();
			break;
		case 6:
			new_row = y.value();
			new_col = x.value() + 1;
			break;
		case 7:
			new_row = y.value() - 1;
			new_col = x.value() - 1;
			break;
		case 8:
			new_row = y.value() - 1;
			new_col = x.value();
			break;
		case 9:
			new_row = y.value() - 1;
			new_col = x.value() + 1;
			break;
		default:
			new_row = y.value();
			new_col = x.value();
			break;
		}
		bool = false;
		if ((new_row >= 0) && (new_row < Variable.cur_height) && (new_col >= 0) && (new_col < Variable.cur_width)) {
			y.value(new_row);
			x.value(new_col);
			bool = true;
		}
		return bool;
	}
	
	/**
	 * Saving throws for player character. -RAK-
	 * 
	 * @return 
	 */
	public static boolean player_saves() {
		/* MPW C couldn't handle the expression, so split it into two parts */
		short temp = Player.class_level_adj[Player.py.misc.pclass][Constants.CLA_SAVE];

		return Misc1.randint(100) <= (Player.py.misc.save + stat_adj(Constants.A_WIS) + (temp * Player.py.misc.lev / 3));
	}
	
	/**
	 * Finds range of item in inventory list -RAK-
	 * 
	 * @param item1 - 
	 * @param item2 - 
	 * @param j - 
	 * @param k - 
	 * @return 
	 */
	public static boolean find_range(int item1, int item2, IntPointer j, IntPointer k) {
		int i;
		InvenType i_ptr;
		boolean flag;
		
		i = 0;
		j.value(-1);
		k.value(-1);
		flag = false;
		while (i < Treasure.inven_ctr) {
			i_ptr = Treasure.inventory[i];
			if (!flag) {
				if ((i_ptr.tval == item1) || (i_ptr.tval == item2)) {
					flag = true;
					j.value(i);
				}
			} else {
				if ((i_ptr.tval != item1) && (i_ptr.tval != item2)) {
					k.value(i - 1);
					break;
				}
			}
			i++;
		}
		if (flag && (k.value() == -1)) {
			k.value(Treasure.inven_ctr - 1);
		}
		return flag;
	}
	
	/**
	 * Teleport the player to a new location -RAK-
	 * 
	 * @param dis - Distance from current location to new location
	 */
	public static void teleport(int dis) {
		int y, x, i, j;
		
		do {
			y = Misc1.randint(Variable.cur_height) - 1;
			x = Misc1.randint(Variable.cur_width) - 1;
			while (Misc1.distance(y, x, Player.char_row, Player.char_col) > dis) {
				y += ((Player.char_row - y) / 2);
				x += ((Player.char_col - x) / 2);
			}
		} while ((Variable.cave[y][x].fval >= Constants.MIN_CLOSED_SPACE) || (Variable.cave[y][x].cptr >= 2));
		
		Moria1.move_rec(Player.char_row, Player.char_col, y, x);
		
		for (i = Player.char_row - 1; i <= Player.char_row + 1; i++) {
			for (j = Player.char_col - 1; j <= Player.char_col + 1; j++) {
				Variable.cave[i][j].tl = false;
				Moria1.lite_spot(i, j);
			}
		}
		Moria1.lite_spot(Player.char_row, Player.char_col);
		Player.char_row = y;
		Player.char_col = x;
		Misc4.check_view();
		Creature.creatures(false);
		Variable.teleport_flag = false;
	}
}
