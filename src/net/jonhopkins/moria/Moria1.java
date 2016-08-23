/*
 * Moria1.java: misc code, mainly handles player movement, inventory, etc
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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Moria1 {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Misc4 m4;
	private Monsters mon;
	private Player py;
	private Treasure t;
	private Variable var;
	
	private static Moria1 instance;
	private Moria1() { }
	public static Moria1 getInstance() {
		if (instance == null) {
			instance = new Moria1();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		m4 = Misc4.getInstance();
		mon = Monsters.getInstance();
		py = Player.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Changes speed of monsters relative to player		-RAK-	*/
	/* Note: When the player is sped up or slowed down, I simply	 */
	/*	 change the speed of all the monsters.	This greatly	 */
	/*	 simplified the logic.				       */
	public void change_speed(int num) {
		int i;
		
		py.py.flags.speed += num;
		py.py.flags.status |= Constants.PY_SPEED;
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX; i--) {
			mon.m_list[i].cspeed += num;
		}
	}
	
	/* Player bonuses					-RAK-	*/
	/* When an item is worn or taken off, this re-adjusts the player */
	/* bonuses.  Factor=1 : wear; Factor=-1 : removed		 */
	/* Only calculates properties with cumulative effect.  Properties that
	 * depend on everything being worn are recalculated by calc_bonuses() -CJS- */
	public void py_bonuses(InvenType t_ptr, int factor) {
		int i, amount;
		
		amount = t_ptr.p1 * factor;
		if ((t_ptr.flags & Constants.TR_STATS) != 0) {
			for(i = 0; i < 6; i++) {
				if (((1 << i) & t_ptr.flags) != 0) {
					m3.bst_stat(i, amount);
				}
			}
		}
		if ((Constants.TR_SEARCH & t_ptr.flags) != 0) {
			py.py.misc.srh += amount;
			py.py.misc.fos -= amount;
		}
		if ((Constants.TR_STEALTH & t_ptr.flags) != 0) {
			py.py.misc.stl += amount;
		}
		if ((Constants.TR_SPEED & t_ptr.flags) != 0) {
			change_speed(-amount);
		}
		if ((Constants.TR_BLIND & t_ptr.flags) != 0 && (factor > 0)) {
			py.py.flags.blind += 1000;
		}
		if ((Constants.TR_TIMID & t_ptr.flags) != 0 && (factor > 0)) {
			py.py.flags.afraid += 50;
		}
		if ((Constants.TR_INFRA & t_ptr.flags) != 0) {
			py.py.flags.see_infra += amount;
		}
	}
	
	/* Recalculate the effect of all the stuff we use.		  -CJS- */
	public void calc_bonuses() {
		long item_flags;
		int old_dis_ac;
		PlayerFlags p_ptr;
		PlayerMisc m_ptr;
		InvenType i_ptr;
		int i;
		
		p_ptr = py.py.flags;
		m_ptr = py.py.misc;
		if (p_ptr.slow_digest) {
			p_ptr.food_digested++;
		}
		if (p_ptr.regenerate) {
			p_ptr.food_digested -= 3;
		}
		p_ptr.see_inv     = false;
		p_ptr.teleport    = Constants.FALSE;
		p_ptr.free_act    = false;
		p_ptr.slow_digest = false;
		p_ptr.aggravate   = Constants.FALSE;
		p_ptr.sustain_str = false;
		p_ptr.sustain_int = false;
		p_ptr.sustain_wis = false;
		p_ptr.sustain_con = false;
		p_ptr.sustain_dex = false;
		p_ptr.sustain_chr = false;
		p_ptr.fire_resist = Constants.FALSE;
		p_ptr.acid_resist = Constants.FALSE;
		p_ptr.cold_resist = Constants.FALSE;
		p_ptr.regenerate  = false;
		p_ptr.lght_resist = Constants.FALSE;
		p_ptr.ffall       = Constants.FALSE;
		
		old_dis_ac		= m_ptr.dis_ac;
		m_ptr.ptohit	= m3.tohit_adj();	/* Real To Hit   */
		m_ptr.ptodam	= m3.todam_adj();	/* Real To Dam   */
		m_ptr.ptoac		= m3.toac_adj();	/* Real To AC    */
		m_ptr.pac		= 0;				/* Real AC	     */
		m_ptr.dis_th	= m_ptr.ptohit;		/* Display To Hit	    */
		m_ptr.dis_td	= m_ptr.ptodam;		/* Display To Dam	    */
		m_ptr.dis_ac	= 0;				/* Display AC		 */
		m_ptr.dis_tac	= m_ptr.ptoac;		/* Display To AC	    */
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_LIGHT; i++) {
			i_ptr = t.inventory[i];
			if (i_ptr.tval != Constants.TV_NOTHING) {
				m_ptr.ptohit += i_ptr.tohit;
				if (i_ptr.tval != Constants.TV_BOW) {	/* Bows can't damage. -CJS- */
					m_ptr.ptodam += i_ptr.todam;
				}
				m_ptr.ptoac	+= i_ptr.toac;
				m_ptr.pac += i_ptr.ac;
				if (desc.known2_p(i_ptr)) {
					m_ptr.dis_th += i_ptr.tohit;
					if (i_ptr.tval != Constants.TV_BOW) {
						m_ptr.dis_td  += i_ptr.todam;	/* Bows can't damage. -CJS- */
					}
					m_ptr.dis_tac += i_ptr.toac;
					m_ptr.dis_ac += i_ptr.ac;
				} else if ((Constants.TR_CURSED & i_ptr.flags) == 0) {
					/* Base AC values should always be visible, as long as the item
					 * is not cursed.  */
					m_ptr.dis_ac += i_ptr.ac;
				}
			}
		}
		m_ptr.dis_ac += m_ptr.dis_tac;
		
		if (var.weapon_heavy) {
			m_ptr.dis_th += (py.py.stats.use_stat[Constants.A_STR] * 15 - t.inventory[Constants.INVEN_WIELD].weight);
		}
		
		/* Add in temporary spell increases	*/
		if (p_ptr.invuln > 0) {
			m_ptr.pac += 100;
			m_ptr.dis_ac += 100;
		}
		if (p_ptr.blessed > 0) {
			m_ptr.pac    += 2;
			m_ptr.dis_ac += 2;
		}
		if (p_ptr.detect_inv > 0) {
			p_ptr.see_inv = true;
		}
		
		/* can't print AC here because might be in a store */
		if (old_dis_ac != m_ptr.dis_ac) {
			p_ptr.status |= Constants.PY_ARMOR;
		}
		
		item_flags = 0;
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_LIGHT; i++) {
			i_ptr = t.inventory[i];
			item_flags |= i_ptr.flags;
		}
		if ((Constants.TR_SLOW_DIGEST & item_flags) != 0) {
			p_ptr.slow_digest = true;
		}
		if ((Constants.TR_AGGRAVATE & item_flags) != 0) {
			p_ptr.aggravate = Constants.TRUE;
		}
		if ((Constants.TR_TELEPORT & item_flags) != 0) {
			p_ptr.teleport = Constants.TRUE;
		}
		if ((Constants.TR_REGEN & item_flags) != 0) {
			p_ptr.regenerate = true;
		}
		if ((Constants.TR_RES_FIRE & item_flags) != 0) {
			p_ptr.fire_resist = Constants.TRUE;
		}
		if ((Constants.TR_RES_ACID & item_flags) != 0) {
			p_ptr.acid_resist = Constants.TRUE;
		}
		if ((Constants.TR_RES_COLD & item_flags) != 0) {
			p_ptr.cold_resist = Constants.TRUE;
		}
		if ((Constants.TR_FREE_ACT & item_flags) != 0) {
			p_ptr.free_act = true;
		}
		if ((Constants.TR_SEE_INVIS & item_flags) != 0) {
			p_ptr.see_inv = true;
		}
		if ((Constants.TR_RES_LIGHT & item_flags) != 0) {
			p_ptr.lght_resist = Constants.TRUE;
		}
		if ((Constants.TR_FFALL & item_flags) != 0) {
			p_ptr.ffall = Constants.TRUE;
		}
		
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_LIGHT; i++) {
			i_ptr = t.inventory[i];
			if ((Constants.TR_SUST_STAT & i_ptr.flags) != 0) {
				switch(i_ptr.p1)
				{
				case 1: p_ptr.sustain_str = true; break;
				case 2: p_ptr.sustain_int = true; break;
				case 3: p_ptr.sustain_wis = true; break;
				case 4: p_ptr.sustain_con = true; break;
				case 5: p_ptr.sustain_dex = true; break;
				case 6: p_ptr.sustain_chr = true; break;
				default: break;
				}
			}
		}
		
		if (p_ptr.slow_digest) {
			p_ptr.food_digested--;
		}
		if (p_ptr.regenerate) {
			p_ptr.food_digested += 3;
		}
	}
	
	/* Displays inventory items from r1 to r2	-RAK-	*/
	/* Designed to keep the display as far to the right as possible.  The  -CJS-
	 * parameter col gives a column at which to start, but if the display does
	 * not fit, it may be moved left.  The return value is the left edge used. */
	/* If mask is non-zero, then only display those items which have a non-zero
	 * entry in the mask array.  */
	public int show_inven(int r1, int r2, boolean weight, int col, String mask) {
		int i;
		int total_weight, len, l, lim, current_line;
		String tmp_val;
		String[] out_val = new String[23];
		
		len = 79 - col;
		if (weight) {
			lim = 68;
		} else {
			lim = 76;
		}
		
		for (i = r1; i <= r2; i++) {	/* Print the items	  */
			if (mask.equals("") || mask.length() >= i) {
				tmp_val = desc.objdes(t.inventory[i], true);
				if (lim < tmp_val.length()) {
					tmp_val = tmp_val.substring(0, lim);	/* Truncate if too long. */
				}
				out_val[i] = String.format("%c) %s", (char)('a' + i), tmp_val);
				l = out_val[i].length() + 2;
				if (weight) {
					l += 9;
				}
				if (l > len) {
					len = l;
				}
			}
		}
		
		col = 79 - len;
		if (col < 0) {
			col = 0;
		}
		
		current_line = 1;
		for (i = r1; i <= r2; i++) {
			if (mask.equals("") || mask.length() >= i) {
				/* don't need first two spaces if in first column */
				if (col == 0) {
					io.prt(out_val[i], current_line, col);
				} else {
					io.put_buffer("  ", current_line, col);
					io.prt(out_val[i], current_line, col + 2);
				}
				if (weight) {
					total_weight = t.inventory[i].weight * t.inventory[i].number;
					tmp_val = String.format("%3d.%d lb", (total_weight) / 10, (total_weight) % 10);
					io.prt(tmp_val, current_line, 71);
				}
				current_line++;
			}
		}
		return col;
	}
	
	/* Return a string describing how a given equipment item is carried. -CJS- */
	public String describe_use(int i) {
		String p;
		
		switch(i)
		{
		case Constants.INVEN_WIELD:
			p = "wielding"; break;
		case Constants.INVEN_HEAD:
			p = "wearing on your head"; break;
		case Constants.INVEN_NECK:
			p = "wearing around your neck"; break;
		case Constants.INVEN_BODY:
			p = "wearing on your body"; break;
		case Constants.INVEN_ARM:
			p = "wearing on your arm"; break;
		case Constants.INVEN_HANDS:
			p = "wearing on your hands"; break;
		case Constants.INVEN_RIGHT:
			p = "wearing on your right hand"; break;
		case Constants.INVEN_LEFT:
			p = "wearing on your left hand"; break;
		case Constants.INVEN_FEET:
			p = "wearing on your feet"; break;
		case Constants.INVEN_OUTER:
			p = "wearing about your body"; break;
		case Constants.INVEN_LIGHT:
			p = "using to light the way"; break;
		case Constants.INVEN_AUX:
			p = "holding ready by your side"; break;
		default:
			p = "carrying in your pack"; break;
		}
		return p;
	}
	
	/* Displays equipment items from r1 to end	-RAK-	*/
	/* Keep display as far right as possible. -CJS- */
	public int show_equip(boolean weight, int col) {
		int i, line;
		int total_weight, l, len, lim;
		String prt1;
		String prt2;
		String[] out_val = new String[Constants.INVEN_ARRAY_SIZE - Constants.INVEN_WIELD];
		InvenType i_ptr;
		
		line = 0;
		len = 79 - col;
		if (weight) {
			lim = 52;
		} else {
			lim = 60;
		}
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) { /* Range of equipment */
			i_ptr = t.inventory[i];
			if (i_ptr.tval != Constants.TV_NOTHING) {
				switch(i)	     /* Get position	      */
				{
				case Constants.INVEN_WIELD:
					if (py.py.stats.use_stat[Constants.A_STR] * 15 < i_ptr.weight) {
						prt1 = "Just lifting";
					} else {
						prt1 = "Wielding";
					}
					break;
				case Constants.INVEN_HEAD:
					prt1 = "On head"; break;
				case Constants.INVEN_NECK:
					prt1 = "Around neck"; break;
				case Constants.INVEN_BODY:
					prt1 = "On body"; break;
				case Constants.INVEN_ARM:
					prt1 = "On arm"; break;
				case Constants.INVEN_HANDS:
					prt1 = "On hands"; break;
				case Constants.INVEN_RIGHT:
					prt1 = "On right hand"; break;
				case Constants.INVEN_LEFT:
					prt1 = "On left hand"; break;
				case Constants.INVEN_FEET:
					prt1 = "On feet"; break;
				case Constants.INVEN_OUTER:
					prt1 = "About body"; break;
				case Constants.INVEN_LIGHT:
					prt1 = "Light source"; break;
				case Constants.INVEN_AUX:
					prt1 = "Spare weapon"; break;
				default:
					prt1 = "Unknown value"; break;
				}
				prt2 = desc.objdes(t.inventory[i], true);
				if (lim < prt2.length()) {
					prt2 = prt2.substring(0, lim); /* Truncate if necessary */
				}
				out_val[line] = String.format("%c) %-14s: %s", line + 'a', prt1, prt2);
				l = out_val[line].length() + 2;
				if (weight) {
					l += 9;
				}
				if (l > len) {
					len = l;
				}
				line++;
			}
		}
		col = 79 - len;
		if (col < 0) {
			col = 0;
		}
		
		line = 0;
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {	/* Range of equipment */
			i_ptr = t.inventory[i];
			if (i_ptr.tval != Constants.TV_NOTHING) {
				/* don't need first two spaces when using whole screen */
				if (col == 0) {
					io.prt(out_val[line], line + 1, col);
				} else {
					io.put_buffer("  ", line + 1, col);
					io.prt(out_val[line], line + 1, col + 2);
				}
				if (weight) {
					total_weight = i_ptr.weight * i_ptr.number;
					prt2 = String.format("%3d.%d lb", (total_weight) / 10, (total_weight) % 10);
					io.prt(prt2, line + 1, 71);
				}
				line++;
			}
		}
		io.erase_line(line + 1, col);
		return col;
	}
	
	/* Remove item from equipment list		-RAK-	*/
	public void takeoff(int item_val, int posn) {
		String p;
		String out_val, prt2;
		InvenType t_ptr;
		
		t.equip_ctr--;
		t_ptr = t.inventory[item_val];
		t.inven_weight -= t_ptr.weight * t_ptr.number;
		py.py.flags.status |= Constants.PY_STR_WGT;
		
		if (item_val == Constants.INVEN_WIELD || item_val == Constants.INVEN_AUX) {
			p = "Was wielding ";
		} else if (item_val == Constants.INVEN_LIGHT) {
			p = "Light source was ";
		} else {
			p = "Was wearing ";
		}
		
		prt2 = desc.objdes(t_ptr, true);
		if (posn >= 0) {
			out_val = String.format("%s%s (%c)", p, prt2, 'a' + posn);
		} else {
			out_val = String.format("%s%s", p, prt2);
		}
		io.msg_print(out_val);
		if (item_val != Constants.INVEN_AUX) {	/* For secondary weapon  */
			py_bonuses(t_ptr, -1);
		}
		desc.invcopy(t_ptr, Constants.OBJ_NOTHING);
	}
	
	/* Used to verify if this really is the item we wish to	 -CJS-
	 * wear or read. */
	public boolean verify(String prompt, int item) {
		String out_str, object;
		
		object = desc.objdes(t.inventory[item], true);
		object = object.substring(0, object.length() - 1).concat("?");	/* change the period to a question mark */
		out_str = String.format("%s %s", prompt, object);
		return io.get_check(out_str);
	}
	
	/* All inventory commands (wear, exchange, take off, drop, inventory and
	 * equipment) are handled in an alternative command input mode, which accepts
	 * any of the inventory commands.
	 *
	 * It is intended that this function be called several times in succession,
	 * as some commands take up a turn, and the rest of moria must proceed in the
	 * interim. A global variable is provided, doing_inven, which is normally
	 * zero; however if on return from inven_command it is expected that
	 * inven_command should be called *again*, (being still in inventory command
	 * input mode), then doing_inven is set to the inventory command character
	 * which should be used in the next call to inven_command.
	 *
	 * On return, the screen is restored, but not flushed. Provided no flush of
	 * the screen takes place before the next call to inven_command, the inventory
	 * command screen is silently redisplayed, and no actual output takes place at
	 * all. If the screen is flushed before a subsequent call, then the player is
	 * prompted to see if we should continue. This allows the player to see any
	 * changes that take place on the screen during inventory command input.
	 *
	 * The global variable, screen_change, is cleared by inven_command, and set
	 * when the screen is flushed. This is the means by which inven_command tell
	 * if the screen has been flushed.
	 *
	 * The display of inventory items is kept to the right of the screen to
	 * minimize the work done to restore the screen afterwards.		-CJS-*/
	
	/* Inventory command screen states. */
	public final int BLANK_SCR =	0;
	public final int EQUIP_SCR =	1;
	public final int INVEN_SCR =	2;
	public final int WEAR_SCR  =	3;
	public final int HELP_SCR  =	4;
	public final int WRONG_SCR =	5;
	
	/* Keep track of the state of the inventory screen. */
	public int scr_state, scr_left, scr_base;
	public int wear_low, wear_high;
	
	/* Draw the inventory screen. */
	public void inven_screen(int new_scr) {
		int line = 0;
		
		if (new_scr != scr_state) {
			scr_state = new_scr;
			switch(new_scr)
			{
			case BLANK_SCR:
				line = 0;
				break;
			case HELP_SCR:
				if (scr_left > 52) {
					scr_left = 52;
				}
				io.prt("  ESC: exit", 1, scr_left);
				io.prt("  w  : wear or wield object", 2, scr_left);
				io.prt("  t  : take off item", 3, scr_left);
				io.prt("  d  : drop object", 4, scr_left);
				io.prt("  x  : exchange weapons", 5, scr_left);
				io.prt("  i  : inventory of pack", 6, scr_left);
				io.prt("  e  : list used equipment", 7, scr_left);
				line = 7;
				break;
			case INVEN_SCR:
				scr_left = show_inven(0, t.inven_ctr - 1, var.show_weight_flag.value(), scr_left, "");
				line = t.inven_ctr;
				break;
			case WEAR_SCR:
				scr_left = show_inven(wear_low, wear_high, var.show_weight_flag.value(), scr_left, "");
				line = wear_high - wear_low + 1;
				break;
			case EQUIP_SCR:
				scr_left = show_equip(var.show_weight_flag.value(), scr_left);
				line = t.equip_ctr;
				break;
			}
			if (line >= scr_base) {
				scr_base = line + 1;
				io.erase_line(scr_base, scr_left);
			} else {
				while (++line <= scr_base) {
					io.erase_line(line, scr_left);
				}
			}
		}
	}
	
	/* This does all the work. */
	public void inven_command(char command) {
		int slot = 0, item;
		int tmp, tmp2, from, to;
		boolean selecting;
		String prompt, swap, disp, string;
		CharPointer which = new CharPointer(), query = new CharPointer();
		String prt1, prt2;
		InvenType i_ptr;
		InvenType tmp_obj;
		
		var.free_turn_flag = true;
		io.save_screen();
		/* Take up where we left off after a previous inventory command. -CJS- */
		if (var.doing_inven != '\0') {
			/* If the screen has been flushed, we need to redraw. If the command is
			 * a simple ' ' to recover the screen, just quit. Otherwise, check and
			 * see what the user wants. */
			if (var.screen_change) {
				if (command == ' ' || !io.get_check("Continuing with inventory command?")) {
					var.doing_inven = '\0';
					return;
				}
				scr_left = 50;
				scr_base = 0;
			}
			tmp = scr_state;
			scr_state = WRONG_SCR;
			inven_screen(tmp);
		} else {
			scr_left = 50;
			scr_base = 0;
			/* this forces exit of inven_command() if selecting is not set true */
			scr_state = BLANK_SCR;
		}
		do {
			if (Character.isUpperCase(command)) {
				command = Character.toLowerCase(command);
			}
			
			/* Simple command getting and screen selection. */
			selecting = false;
			switch(command)
			{
			case 'i':	/* Inventory	    */
				if (t.inven_ctr == 0) {
					io.msg_print("You are not carrying anything.");
				} else {
					inven_screen(INVEN_SCR);
				}
				break;
			case 'e':	/* Equipment	   */
				if (t.equip_ctr == 0) {
					io.msg_print("You are not using any equipment.");
				} else {
					inven_screen(EQUIP_SCR);
				}
				break;
			case 't':	/* Take off	   */
				if (t.equip_ctr == 0) {
					io.msg_print("You are not using any equipment.");
				
				/* don't print message restarting inven command after taking off
				 * something, it is confusing */
				} else if (t.inven_ctr >= Constants.INVEN_WIELD && var.doing_inven == '\0') {
					io.msg_print("You will have to drop something first.");
				} else {
					if (scr_state != BLANK_SCR) {
						inven_screen(EQUIP_SCR);
					}
					selecting = true;
				}
				break;
			case 'd':	/* Drop */
				if (t.inven_ctr == 0 && t.equip_ctr == 0) {
					io.msg_print("But you're not carrying anything.");
				} else if (var.cave[py.char_row][py.char_col].tptr != 0) {
					io.msg_print("There's no room to drop anything here.");
				} else {
					selecting = true;
					if ((scr_state == EQUIP_SCR && t.equip_ctr > 0) || t.inven_ctr == 0) {
						if (scr_state != BLANK_SCR) {
							inven_screen(EQUIP_SCR);
						}
						command = 'r';	/* Remove - or take off and drop. */
					} else if (scr_state != BLANK_SCR) {
						inven_screen(INVEN_SCR);
					}
				}
				break;
			case 'w':	  /* Wear/wield	   */
				for (wear_low = 0; wear_low < t.inven_ctr && t.inventory[wear_low].tval > Constants.TV_MAX_WEAR; wear_low++) {
					;
				}
				for(wear_high = wear_low; wear_high < t.inven_ctr && t.inventory[wear_high].tval >= Constants.TV_MIN_WEAR; wear_high++) {
					;
				}
				wear_high--;
				if (wear_low > wear_high) {
					io.msg_print("You have nothing to wear or wield.");
				} else {
					if (scr_state != BLANK_SCR && scr_state != INVEN_SCR) {
						inven_screen(WEAR_SCR);
					}
					selecting = true;
				}
				break;
			case 'x':
				if (t.inventory[Constants.INVEN_WIELD].tval == Constants.TV_NOTHING && t.inventory[Constants.INVEN_AUX].tval == Constants.TV_NOTHING) {
					io.msg_print("But you are wielding no weapons.");
				} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_WIELD].flags) != 0) {
					prt1 = desc.objdes(t.inventory[Constants.INVEN_WIELD], false);
					prt2 = String.format("The %s you are wielding appears to be cursed.", prt1);
					io.msg_print(prt2);
				} else {
					var.free_turn_flag = false;
					tmp_obj = t.inventory[Constants.INVEN_AUX];
					t.inventory[Constants.INVEN_AUX] = t.inventory[Constants.INVEN_WIELD];
					t.inventory[Constants.INVEN_WIELD] = tmp_obj;
					if (scr_state == EQUIP_SCR) {
						scr_left = show_equip(var.show_weight_flag.value(), scr_left);
					}
					py_bonuses(t.inventory[Constants.INVEN_AUX], -1);	/* Subtract bonuses */
					py_bonuses(t.inventory[Constants.INVEN_WIELD], 1);	/* Add bonuses    */
					if (t.inventory[Constants.INVEN_WIELD].tval != Constants.TV_NOTHING) {
						prt1 = "Primary weapon   : ";
						prt2 = desc.objdes(t.inventory[Constants.INVEN_WIELD], true);
						io.msg_print(prt1.concat(prt2));
					} else {
						io.msg_print("No primary weapon.");
					}
					/* this is a new weapon, so clear the heavy flag */
					var.weapon_heavy = false;
					m3.check_strength();
				}
				break;
			case ' ':	/* Dummy command to return again to main prompt. */
				break;
			case '?':
				inven_screen(HELP_SCR);
				break;
			default:
				/* Nonsense command					   */
				io.bell();
				break;
			}
			
			/* Clear the doing_inven flag here, instead of at beginning, so that
			 * can use it to control when messages above appear. */
			var.doing_inven = '\0';
			
			/* Keep looking for objects to drop/wear/take off/throw off */
			which.value('z');
			while (selecting && var.free_turn_flag) {
				swap = "";
				if (command == 'w') {
					from = wear_low;
					to = wear_high;
					prompt = "Wear/Wield";
				} else {
					from = 0;
					if (command == 'd') {
						to = t.inven_ctr - 1;
						prompt = "Drop";
						if (t.equip_ctr > 0) {
							swap = ", / for Equip";
						}
					} else {
						to = t.equip_ctr - 1;
						if (command == 't') {
							prompt = "Take off";
						} else {	/* command == 'r' */
							prompt = "Throw off";
							if (t.inven_ctr > 0) {
								swap = ", / for Inven";
							}
						}
					}
				}
				if (from > to) {
					selecting = false;
				} else {
					if (scr_state == BLANK_SCR) {
						disp = ", * to list";
					} else {
						disp = "";
					}
					prt1 = String.format("(%c-%c%s%s, space to break, ESC to exit) %s which one?",
							from + 'a', to + 'a', disp, swap, 
							((command == 'w' || command == 'd') ? ", 0-9" : ""),
							prompt);
					
					/* Abort everything. */
					if (!io.get_com(prt1, which)) {
						selecting = false;
						which.value(Constants.ESCAPE);
					
					/* Draw the screen and maybe exit to main prompt. */
					} else if (which.value() == ' ' || which.value() == '*') {
						if (command == 't' || command == 'r') {
							inven_screen(EQUIP_SCR);
						} else if (command == 'w' && scr_state != INVEN_SCR) {
							inven_screen(WEAR_SCR);
						} else {
							inven_screen(INVEN_SCR);
						}
						if (which.value() == ' ') {
							selecting = false;
						}
					
					/* Swap screens (for drop) */
					} else if (which.value() == '/' && !swap.equals("")) {
						if (command == 'd') {
							command = 'r';
						} else {
							command = 'd';
						}
						if (scr_state == EQUIP_SCR) {
							inven_screen(INVEN_SCR);
						} else if (scr_state == INVEN_SCR) {
							inven_screen(EQUIP_SCR);
						}
					} else {
						if ((which.value() >= '0') && (which.value() <= '9') 
								&& (command != 'r') && (command != 't'))
						{
							/* look for item whose inscription matches "which" */
							int m;
							for (m = from;
									m <= to && ((t.inventory[m].inscrip.charAt(0) != which.value())
											|| (t.inventory[m].inscrip.length() > 1));
									m++);
							if (m <= to)
								item = m;
							else 
								item = -1;
						}
						else if ((which.value() >= 'A') && (which.value() <= 'Z')) {
							item = which.value() - 'A';
						} else {
							item = which.value() - 'a';
						}
						if (item < from || item > to) {
							io.bell();
						} else {  /* Found an item! */
							if (command == 'r' || command == 't') {
								/* Get its place in the equipment list. */
								tmp = item;
								item = 21;
								do {
									item++;
									if (t.inventory[item].tval != Constants.TV_NOTHING) {
										tmp--;
									}
								} while (tmp >= 0);
								if (Character.isUpperCase(which.value()) && !verify(prompt, item)) {
									item = -1;
								} else if ((Constants.TR_CURSED & t.inventory[item].flags) != 0) {
									io.msg_print("Hmmm, it seems to be cursed.");
									item = -1;
								} else if (command == 't' &&
										!m3.inven_check_num(t.inventory[item])) {
									if (var.cave[py.char_row][py.char_col].tptr != 0) {
										io.msg_print("You can't carry it.");
										item = -1;
									} else if (io.get_check("You can't carry it.  Drop it?")) {
										command = 'r';
									} else {
										item = -1;
									}
								}
								if (item >= 0) {
									if (command == 'r') {
										m3.inven_drop(item, true);
										/* As a safety measure, set the player's
										   inven weight to 0, 
										   when the last object is dropped*/
										if (t.inven_ctr == 0 && t.equip_ctr == 0) {
											t.inven_weight = 0;
										}
									} else {
										slot = m3.inven_carry(t.inventory[item]);
										takeoff(item, slot);
									}
									m3.check_strength();
									var.free_turn_flag = false;
									if (command == 'r') {
										selecting = false;
									}
								}
							} else if (command == 'w') {
								/* Wearing. Go to a bit of trouble over replacing
								 * existing equipment. */
								if (Character.isUpperCase(which.value()) && !verify(prompt, item)) {
									item = -1;
								} else {
									switch(t.inventory[item].tval)
									{ /* Slot for equipment	   */
									case Constants.TV_SLING_AMMO: case Constants.TV_BOLT: case Constants.TV_ARROW:
									case Constants.TV_BOW: case Constants.TV_HAFTED: case Constants.TV_POLEARM:
									case Constants.TV_SWORD: case Constants.TV_DIGGING: case Constants.TV_SPIKE:
										slot = Constants.INVEN_WIELD; break;
									case Constants.TV_LIGHT: slot = Constants.INVEN_LIGHT; break;
									case Constants.TV_BOOTS: slot = Constants.INVEN_FEET; break;
									case Constants.TV_GLOVES: slot = Constants.INVEN_HANDS; break;
									case Constants.TV_CLOAK: slot = Constants.INVEN_OUTER; break;
									case Constants.TV_HELM: slot = Constants.INVEN_HEAD; break;
									case Constants.TV_SHIELD: slot = Constants.INVEN_ARM; break;
									case Constants.TV_HARD_ARMOR: case Constants.TV_SOFT_ARMOR:
										slot = Constants.INVEN_BODY; break;
									case Constants.TV_AMULET: slot = Constants.INVEN_NECK; break;
									case Constants.TV_RING:
										if (t.inventory[Constants.INVEN_RIGHT].tval == Constants.TV_NOTHING) {
											slot = Constants.INVEN_RIGHT;
										} else if (t.inventory[Constants.INVEN_LEFT].tval == Constants.TV_NOTHING) {
											slot = Constants.INVEN_LEFT;
										} else {
											slot = 0;
											/* Rings. Give some choice over where they go. */
											do {
												if (!io.get_com( "Put ring on which hand (l/r/L/R)?", query)) {
													item = -1;
													slot = -1;
												} else if (query.value()== 'l') {
													slot = Constants.INVEN_LEFT;
												} else if (query.value()== 'r') {
													slot = Constants.INVEN_RIGHT;
												} else {
													if (query.value() == 'L') {
														slot = Constants.INVEN_LEFT;
													} else if (query.value() == 'R') {
														slot = Constants.INVEN_RIGHT;
													} else {
														io.bell();
													}
													if (slot != 0 && !verify("Replace", slot)) {
														slot = 0;
													}
												}
											} while (slot == 0);
										}
										break;
									default:
										io.msg_print("IMPOSSIBLE: I don't see how you can use that.");
										item = -1;
										break;
									}
								}
								if (item >= 0 && t.inventory[slot].tval != Constants.TV_NOTHING) {
									if ((Constants.TR_CURSED & t.inventory[slot].flags) != 0) {
										prt1 = desc.objdes(t.inventory[slot], false);
										prt2 = String.format("The %s you are ", prt1);
										if (slot == Constants.INVEN_HEAD) {
											prt2 = prt2.concat("wielding ");
										} else {
											prt2 = prt2.concat("wearing ");
										}
										io.msg_print(prt2.concat("appears to be cursed."));
										item = -1;
									} else if (t.inventory[item].subval == Constants.ITEM_GROUP_MIN && t.inventory[item].number > 1 && !m3.inven_check_num(t.inventory[slot])) {
										/* this can happen if try to wield a torch, and
										 * have more than one in your inventory */
										io.msg_print("You will have to drop something first.");
										item = -1;
									}
								}
								if (item >= 0) {
									/* OK. Wear it. */
									var.free_turn_flag = false;
									
									/* first remove new item from inventory */
									tmp_obj = t.inventory[item];
									i_ptr = new InvenType();
									tmp_obj.copyInto(i_ptr);
									
									wear_high--;
									/* Fix for torches	   */
									if (i_ptr.number > 1 && i_ptr.subval <= Constants.ITEM_SINGLE_STACK_MAX) {
										i_ptr.number = 1;
										wear_high++;
									}
									t.inven_weight += i_ptr.weight * i_ptr.number;
									m3.inven_destroy(item);	/* Subtracts weight */
									
									/* second, add old item to inv and remove from
									 * equipment list, if necessary */
									i_ptr = t.inventory[slot];
									if (i_ptr.tval != Constants.TV_NOTHING) {
										tmp2 = t.inven_ctr;
										tmp = m3.inven_carry(i_ptr);
										/* if item removed did not stack with anything in
										 * inventory, then increment wear_high */
										if (t.inven_ctr != tmp2) {
											wear_high++;
										}
										takeoff(slot, tmp);
									}
									
									/* third, wear new item */
									tmp_obj.copyInto(i_ptr);
									t.equip_ctr++;
									py_bonuses(i_ptr, 1);
									if (slot == Constants.INVEN_WIELD) {
										string = "You are wielding";
									} else if (slot == Constants.INVEN_LIGHT) {
										string = "Your light source is";
									} else {
										string = "You are wearing";
									}
									prt2 = desc.objdes(i_ptr, true);
									/* Get the right equipment letter. */
									tmp = Constants.INVEN_WIELD;
									item = 0;
									while (tmp != slot) {
										if (t.inventory[tmp++].tval != Constants.TV_NOTHING) {
											item++;
										}
									}
									
									prt1 = String.format("%s %s (%c)", string, prt2, 'a' + item);
									io.msg_print(prt1);
									/* this is a new weapon, so clear the heavy flag */
									if (slot == Constants.INVEN_WIELD) {
										var.weapon_heavy = false;
									}
									m3.check_strength();
									if ((i_ptr.flags & Constants.TR_CURSED) != 0) {
										io.msg_print("Oops! It feels deathly cold!");
										m4.add_inscribe(i_ptr, Constants.ID_DAMD);
										/* To force a cost of 0, even if unidentified. */
										i_ptr.cost = -1;
									}
								}
							} else {	/* command == 'd' */
								if (t.inventory[item].number > 1) {
									prt1 = desc.objdes(t.inventory[item], true);
									prt1 = prt1.substring(0, prt1.length() - 1).concat("?");
									prt2 = String.format("Drop all %s [y/n]", prt1);
									prt1 = prt1.substring(0, prt1.length() - 1).concat(".");
									io.prt(prt2, 0, 0);
									query.value(io.inkey());
									if (query.value()!= 'y' && query.value()!= 'n') {
										if (query.value()!= Constants.ESCAPE) {
											io.bell();
										}
										io.erase_line(Constants.MSG_LINE, 0);
										item = -1;
									}
								} else if (Character.isUpperCase(which.value()) && !verify(prompt, item)) {
									item = -1;
								} else {
									query.value('y');
								}
								if (item >= 0) {
									var.free_turn_flag = false;    /* Player turn   */
									m3.inven_drop(item, query.value()== 'y');
									m3.check_strength();
								}
								selecting = false;
								/* As a safety measure, set the player's inven weight
								 * to 0, when the last object is dropped.  */
								if (t.inven_ctr == 0 && t.equip_ctr == 0) {
									t.inven_weight = 0;
								}
							}
							if (!var.free_turn_flag && scr_state == BLANK_SCR) {
								selecting = false;
							}
						}
					}
				}
			}
			if (which.value() == Constants.ESCAPE || scr_state == BLANK_SCR) {
				command = Constants.ESCAPE;
			} else if (!var.free_turn_flag) {
				/* Save state for recovery if they want to call us again next turn.*/
				if (selecting) {
					var.doing_inven = command;
				} else {
					var.doing_inven = ' ';	/* A dummy command to recover screen. */
				}
				/* flush last message before clearing screen_change and exiting */
				io.msg_print("");
				var.screen_change = false;	/* This lets us know if the world changes */
				command = Constants.ESCAPE;
			} else {
				/* Put an appropriate header. */
				if (scr_state == INVEN_SCR) {
					if (!var.show_weight_flag.value()|| t.inven_ctr == 0) {
						prt1 = String.format("You are carrying %d.%d pounds. In your pack there is %s",
								t.inven_weight / 10, t.inven_weight % 10, (t.inven_ctr == 0 ? "nothing." : "-"));
					} else {
						prt1 = String.format("You are carrying %d.%d pounds. Your capacity is %d.%d pounds. %s",
								t.inven_weight / 10, t.inven_weight % 10, m3.weight_limit() / 10, m3.weight_limit() % 10, "In your pack is -");
					}
					io.prt(prt1, 0, 0);
				} else if (scr_state == WEAR_SCR) {
					if (wear_high < wear_low) {
						io.prt("You have nothing you could wield.", 0, 0);
					} else {
						io.prt("You could wield -", 0, 0);
					}
				} else if (scr_state == EQUIP_SCR) {
					if (t.equip_ctr == 0) {
						io.prt("You are not using anything.", 0, 0);
					} else {
						io.prt("You are using -", 0, 0);
					}
				} else {
					io.prt("Allowed commands:", 0, 0);
				}
				io.erase_line(scr_base, scr_left);
				io.put_buffer("e/i/t/w/x/d/?/ESC:", scr_base, 60);
				command = io.inkey();
				io.erase_line(scr_base, scr_left);
			}
		} while (command != Constants.ESCAPE);
		
		if (scr_state != BLANK_SCR) {
			io.restore_screen();
		}
		calc_bonuses();
	}
	
	/* Get the ID of an item and return the CTR value of it	-RAK-	*/
	public boolean get_item(IntPointer com_val, String pmt, int i, int j, String mask, String message) {
		String out_val;
		char which;
		boolean test_flag;
		int i_scr;
		boolean full, item, redraw;
		
		item = false;
		redraw = false;
		com_val.value(0);
		i_scr = 1;
		if (j > Constants.INVEN_WIELD) {
			full = true;
			if (t.inven_ctr == 0) {
				i_scr = 0;
				j = t.equip_ctr - 1;
			} else {
				j = t.inven_ctr - 1;
			}
		} else {
			full = false;
		}
		
		if (t.inven_ctr > 0 || (full && t.equip_ctr > 0)) {
			do {
				if (redraw) {
					if (i_scr > 0) {
						show_inven(i, j, false, 80, mask);
					} else {
						show_equip(false, 80);
					}
				}
				if (full) {
					out_val = String.format(
							"(%s: %c-%c,%s%s / for %s, or ESC) %s",
							(i_scr > 0 ? "Inven" : "Equip"), i + 'a', j + 'a',
							(i_scr > 0 ? " 0-9," : ""),
							(redraw ? "" : " * to see,"),
							(i_scr > 0 ? "Equip" : "Inven"), pmt);
				} else {
					out_val = String.format(
							"(Items %c-%c,%s%s ESC to exit) %s", 
							i + 'a', j + 'a',			   
							(i_scr > 0 ? " 0-9," : ""),
							(redraw ? "" : " * for inventory list,"), pmt);
				}
				test_flag = false;
				io.prt(out_val, 0, 0);
				do {
					which = io.inkey();
					switch(which)
					{
					case Constants.ESCAPE:
						test_flag = true;
						var.free_turn_flag = true;
						i_scr = -1;
						break;
					case '/':
						if (full) {
							if (i_scr > 0) {
								if (t.equip_ctr == 0) {
									io.prt("But you're not using anything -more-",0,0);
									io.inkey();
								} else {
									i_scr = 0;
									test_flag = true;
									if (redraw) {
										j = t.equip_ctr;
										while (j < t.inven_ctr) {
											j++;
											io.erase_line(j, 0);
										}
									}
									j = t.equip_ctr - 1;
								}
								io.prt(out_val, 0, 0);
							} else {
								if (t.inven_ctr == 0) {
									io.prt("But you're not carrying anything -more-",0,0);
									io.inkey();
								} else {
									i_scr = 1;
									test_flag = true;
									if (redraw) {
										j = t.inven_ctr;
										while (j < t.equip_ctr) {
											j++;
											io.erase_line (j, 0);
										}
									}
									j = t.inven_ctr - 1;
								}
							}
						}
						break;
					case '*':
						if (!redraw) {
							test_flag = true;
							io.save_screen();
							redraw = true;
						}
						break;
					default:
						if ((which >= '0') && (which <= '9') && (i_scr != 0))
							/* look for item whose inscription matches "which" */
						{
							int m;
							for (m = i;
									(m < Constants.INVEN_WIELD) 
									&& ((t.inventory[m].inscrip.charAt(0) != which)
											|| (t.inventory[m].inscrip.length() > 1));
									m++);
							if (m < Constants.INVEN_WIELD) {
								com_val.value(m);
							} else {
								com_val.value(-1);
							}
						} else if (Character.isUpperCase(which)) {
							com_val.value(which - 'A');
						} else {
							com_val.value(which - 'a');
						}
						if ((com_val.value()>= i) && (com_val.value()<= j) && (mask.equals("") || mask.length() > com_val.value())) {
							if (i_scr == 0) {
								i = 21;
								j = com_val.value();
								do {
									while (t.inventory[++i].tval == Constants.TV_NOTHING);
									j--;
								} while (j >= 0);
								com_val.value(i);
							}
							if (Character.isUpperCase(which) && !verify("Try", com_val.value())) {
								test_flag = true;
								var.free_turn_flag = true;
								i_scr = -1;
								break;
							}
							test_flag = true;
							item = true;
							i_scr = -1;
						} else if (!message.equals("")) {
							io.msg_print(message);
							/* Set test_flag to force redraw of the question.  */
							test_flag = true;
						} else {
							io.bell();
						}
						break;
					}
				} while (!test_flag);
			} while (i_scr >= 0);
			
			if (redraw) {
				io.restore_screen();
			}
			io.erase_line(Constants.MSG_LINE, 0);
		} else {
			io.prt("You are not carrying anything.", 0, 0);
		}
		
		return item;
	}
	
	/* I may have written the town level code, but I'm not exactly	 */
	/* proud of it.	 Adding the stores required some real slucky	 */
	/* hooks which I have not had time to re-think.		 -RAK-	 */
	
	/* Returns true if player has no light			-RAK-	*/
	public boolean no_light() {
		CaveType c_ptr;
		
		c_ptr = var.cave[py.char_row][py.char_col];
		if (!c_ptr.tl && !c_ptr.pl) {
			return true;
		}
		return false;
	}
	
	/* map rogue_like direction commands into numbers */
	public static char map_roguedir(char comval) {
		switch(comval)
		{
		case Constants.KEY_LEFT:
		case 'h':
			comval = '4';
			break;
		case 'y':
			comval = '7';
			break;
		case Constants.KEY_UP:
		case 'k':
			comval = '8';
			break;
		case 'u':
			comval = '9';
			break;
		case Constants.KEY_RIGHT:
		case 'l':
			comval = '6';
			break;
		case 'n':
			comval = '3';
			break;
		case Constants.KEY_DOWN:
		case 'j':
			comval = '2';
			break;
		case 'b':
			comval = '1';
			break;
		case '.':
			comval = '5';
			break;
		}
		return comval;
	}
	
	private int prev_dir;	/* Direction memory. -CJS- */
	
	/* Prompts for a direction				-RAK-	*/
	/* Direction memory added, for repeated commands.  -CJS */
	public boolean get_dir(String prompt, IntPointer dir) {
		CharPointer command = new CharPointer();
		int save;
		
		if (var.default_dir > 0) {	/* used in counted commands. -CJS- */
			dir.value(prev_dir);
			return true;
		}
		if (prompt.equals("")) {
			prompt = "Which direction?";
		}
		for (;;) {
			save = var.command_count;	/* Don't end a counted command. -CJS- */
			if (!io.get_com(prompt, command)) {
				var.free_turn_flag = true;
				return false;
			}
			var.command_count = save;
			if (var.rogue_like_commands.value()) {
				command.value(map_roguedir(command.value()));
			}
			if (command.value()>= '1' && command.value()<= '9' && command.value()!= '5') {
				prev_dir = command.value()- '0';
				dir.value(prev_dir);
				return true;
			}
			io.bell();
		}
	}
	
	/* Similar to get_dir, except that no memory exists, and it is		-CJS-
	 * allowed to enter the null direction. */
	public boolean get_alldir(String prompt, IntPointer dir) {
		CharPointer command = new CharPointer();
		
		for(;;) {
			if (!io.get_com(prompt, command)) {
				var.free_turn_flag = true;
				return false;
			}
			if (var.rogue_like_commands.value()) {
				command.value(map_roguedir(command.value()));
			}
			if (command.value()>= '1' && command.value()<= '9') {
				dir.value(command.value()- '0');
				return true;
			}
			io.bell();
		}
	}
	
	/* Moves creature record from one space to another	-RAK-	*/
	public void move_rec(int y1, int x1, int y2, int x2) {
		int tmp;
		
		/* this always works correctly, even if y1==y2 and x1==x2 */
		tmp = var.cave[y1][x1].cptr;
		var.cave[y1][x1].cptr = 0;
		var.cave[y2][x2].cptr = tmp;
	}
	
	/* Room is lit, make it appear				-RAK-	*/
	public void light_room(int y, int x) {
		int i, j, start_col, end_col;
		int tmp1, tmp2, start_row, end_row;
		CaveType c_ptr;
		int tval;
		
		tmp1 = (Constants.SCREEN_HEIGHT / 2);
		tmp2 = (Constants.SCREEN_WIDTH / 2);
		start_row = (y / tmp1) * tmp1;
		start_col = (x / tmp2) * tmp2;
		end_row = start_row + tmp1 - 1;
		end_col = start_col + tmp2 - 1;
		for (i = start_row; i <= end_row; i++) {
			for (j = start_col; j <= end_col; j++) {
				c_ptr = var.cave[i][j];
				if (c_ptr.lr && ! c_ptr.pl) {
					c_ptr.pl = true;
					if (c_ptr.fval == Constants.DARK_FLOOR) {
						c_ptr.fval = Constants.LIGHT_FLOOR;
					}
					if (! c_ptr.fm && c_ptr.tptr != 0) {
						tval = t.t_list[c_ptr.tptr].tval;
						if (tval >= Constants.TV_MIN_VISIBLE && tval <= Constants.TV_MAX_VISIBLE) {
							c_ptr.fm = true;
						}
					}
					io.print(m1.loc_symbol(i, j), i, j);
				}
			}
		}
	}
	
	/* Lights up given location				-RAK-	*/
	public void lite_spot(int y, int x) {
		if (m1.panel_contains(y, x))
			io.print(m1.loc_symbol(y, x), y, x);
	}
	
	/* Normal movement					*/
	/* When FIND_FLAG,  light only permanent features	*/
	public void sub1_move_light(int y1, int x1, int y2, int x2) {
		int i, j;
		CaveType c_ptr;
		int tval, top, left, bottom, right;
		
		if (var.light_flag) {
			for (i = y1 - 1; i <= y1 + 1; i++) {	/* Turn off lamp light	*/
				for (j = x1 - 1; j <= x1 + 1; j++) {
					var.cave[i][j].tl = false;
				}
			}
			if (var.find_flag != 0 && !var.find_prself.value()) {
				var.light_flag = false;
			}
		} else if (var.find_flag == 0 || var.find_prself.value()) {
			var.light_flag = true;
		}
		
		for (i = y2 - 1; i <= y2 + 1; i++) {
			for (j = x2 - 1; j <= x2 + 1; j++) {
				c_ptr = var.cave[i][j];
				/* only light up if normal movement */
				if (var.light_flag) {
					c_ptr.tl = true;
				}
				if (c_ptr.fval >= Constants.MIN_CAVE_WALL) {
					c_ptr.pl = true;
				} else if (!c_ptr.fm && c_ptr.tptr != 0) {
					tval = t.t_list[c_ptr.tptr].tval;
					if ((tval >= Constants.TV_MIN_VISIBLE) && (tval <= Constants.TV_MAX_VISIBLE)) {
						c_ptr.fm = true;
					}
				}
			}
		}
		
		/* From uppermost to bottom most lines player was on.	 */
		if (y1 < y2) {
			top = y1 - 1;
			bottom = y2 + 1;
		} else {
			top = y2 - 1;
			bottom = y1 + 1;
		}
		if (x1 < x2) {
			left = x1 - 1;
			right = x2 + 1;
		} else {
			left = x2 - 1;
			right = x1 + 1;
		}
		for (i = top; i <= bottom; i++) {
			for (j = left; j <= right; j++) {	/* Leftmost to rightmost do*/
				io.print(m1.loc_symbol(i, j), i, j);
			}
		}
	}
	
	/* When blinded,  move only the player symbol.		*/
	/* With no light,  movement becomes involved.		*/
	public void sub3_move_light(int y1, int x1, int y2, int x2) {
		int i, j;
		
		if (var.light_flag) {
			for (i = y1 - 1; i <= y1 + 1; i++) {
				for (j = x1 - 1; j <= x1 + 1; j++) {
					var.cave[i][j].tl = false;
					io.print(m1.loc_symbol(i, j), i, j);
				}
			}
			var.light_flag = false;
		} else if (var.find_flag == 0 || var.find_prself.value()) {
			io.print(m1.loc_symbol(y1, x1), y1, x1);
		}
		
		if (var.find_flag == 0 || var.find_prself.value()) {
			io.print('@', y2, x2);
		}
	}
	
	/* Package for moving the character's light about the screen	 */
	/* Four cases : Normal, Finding, Blind, and Nolight	 -RAK-	 */
	public void move_light(int y1, int x1, int y2, int x2) {
		if (py.py.flags.blind > 0 || !var.player_light) {
			sub3_move_light(y1, x1, y2, x2);
		} else {
			sub1_move_light(y1, x1, y2, x2);
		}
	}
	
	/* Something happens to disturb the player.		-CJS-
	 * The first arg indicates a major disturbance, which affects search.
	 * The second arg indicates a light change. */
	public void disturb(boolean s, boolean l) {
		var.command_count = 0;
		if (s && (py.py.flags.status & Constants.PY_SEARCH) != 0) {
			search_off();
		}
		if (py.py.flags.rest != 0) {
			rest_off();
		}
		if (l || var.find_flag != 0) {
			var.find_flag = 0;
			m4.check_view();
		}
		io.flush();
	}
	
	/* Search Mode enhancement				-RAK-	*/
	public void search_on() {
		change_speed(1);
		py.py.flags.status |= Constants.PY_SEARCH;
		m3.prt_state();
		m3.prt_speed();
		py.py.flags.food_digested++;
	}
	
	public void search_off() {
		m4.check_view();
		change_speed(-1);
		py.py.flags.status &= ~Constants.PY_SEARCH;
		m3.prt_state();
		m3.prt_speed();
		py.py.flags.food_digested--;
	}
	
	/* Resting allows a player to safely restore his hp	-RAK-	*/
	public void rest() {
		int rest_num;
		String rest_str;
		
		if (var.command_count > 0) {
			rest_num = var.command_count;
			var.command_count = 0;
		} else {
			io.prt("Rest for how long? ", 0, 0);
			rest_num = 0;
			rest_str = io.get_string(0, 19, 5);
			if (rest_str.length() > 0) {
				if (rest_str.charAt(0) == '*') {
					rest_num = -Constants.MAX_SHORT;
				} else {
					try {
						rest_num = Integer.parseInt(rest_str);
					} catch (NumberFormatException e) {
						System.err.println("Could not convert " + rest_str + " to an integer in Moria1.rest()");
						rest_num = 0;
					}
				}
			}
		}
		/* check for reasonable value, must be positive number in range of a
	     short, or must be -MAX_SHORT */
		if ((rest_num == -Constants.MAX_SHORT) || (rest_num > 0) && (rest_num < Constants.MAX_SHORT)) {
			if ((py.py.flags.status & Constants.PY_SEARCH) != 0) {
				search_off();
			}
			py.py.flags.rest = rest_num;
			py.py.flags.status |= Constants.PY_REST;
			m3.prt_state();
			py.py.flags.food_digested--;
			io.prt ("Press any key to stop resting...", 0, 0);
			io.put_qio();
		} else {
			if (rest_num != 0) {
				io.msg_print ("Invalid rest count.");
			}
			io.erase_line(Constants.MSG_LINE, 0);
			var.free_turn_flag = true;
		}
	}
	
	public void rest_off() {
		py.py.flags.rest = 0;
		py.py.flags.status &= ~Constants.PY_REST;
		m3.prt_state();
		io.msg_print(""); /* flush last message, or delete "press any key" message */
		py.py.flags.food_digested++;
	}
	
	/* Attacker's level and plusses,  defender's AC		-RAK-	*/
	public boolean test_hit(int bth, int level, int pth, int ac, int attack_type) {
		int i, die;
		
		disturb(true, false);
		i = bth + pth * Constants.BTH_PLUS_ADJ + (level * py.class_level_adj[py.py.misc.pclass][attack_type]);
		/* pth could be less than 0 if player wielding weapon too heavy for him */
		/* always miss 1 out of 20, always hit 1 out of 20 */
		die = m1.randint(20);
		if ((die != 1) && ((die == 20) || ((i > 0) && (m1.randint (i) > ac)))) {	/* normal hit */
			return true;
		} else {
			return false;
		}
	}
	
	/* Decreases players hit points and sets death flag if necessary*/
	/*							 -RAK-	 */
	public void take_hit(int damage, String hit_from) {
		if (py.py.flags.invuln > 0) {
			damage = 0;
		}
		py.py.misc.chp -= damage;
		if (py.py.misc.chp < 0) {
			if (!var.death) {
				var.death = true;
				var.died_from = hit_from;
				var.total_winner = false;
			}
			var.new_level_flag = true;
		} else {
			m3.prt_chp();
		}
	}
}
