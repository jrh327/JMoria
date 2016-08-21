/*
 * Scrolls.java: scroll code
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

import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.PlayerMisc;

public class Scrolls {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Moria1 mor1;
	private Player py;
	private Spells spells;
	private Treasure t;
	private Variable var;
	
	private static Scrolls instance;
	private Scrolls() { }
	public static Scrolls getInstance() {
		if (instance == null) {
			instance = new Scrolls();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		mor1 = Moria1.getInstance();
		py = Player.getInstance();
		spells = Spells.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Scrolls for the reading				-RAK-	*/
	public void read_scroll() {
		LongPointer i = new LongPointer();
		IntPointer j = new IntPointer(), k = new IntPointer(), item_val = new IntPointer(), y, x;
		IntPointer ptr;
		boolean enchant;
		int[] tmp = new int[6];
		boolean flag, used_up;
		String out_val, tmp_str;
		boolean ident;
		int l;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		
		var.free_turn_flag = true;
		if (py.py.flags.blind > 0) {
			io.msg_print("You can't see to read the scroll.");
		} else if (mor1.no_light()) {
			io.msg_print("You have no light to read by.");
		} else if (py.py.flags.confused > 0) {
			io.msg_print("You are too confused to read a scroll.");
		} else if (t.inven_ctr == 0) {
			io.msg_print("You are not carrying anything!");
		} else if (!m3.find_range(Constants.TV_SCROLL1, Constants.TV_SCROLL2, j, k)) {
			io.msg_print("You are not carrying any scrolls!");
		} else if (mor1.get_item(item_val, "Read which scroll?", j.value(), k.value(), "", "")) {
			i_ptr = t.inventory[item_val.value()];
			var.free_turn_flag = false;
			used_up = true;
			i.value(i_ptr.flags);
			ident = false;
			
			while (i.value() != 0) {
				j.value(m1.bit_pos(i) + 1);
				if (i_ptr.tval == Constants.TV_SCROLL2) {
					j.value(j.value() + 32);
				}
				
				/* Scrolls.			*/
				switch(j.value())
				{
				case 1:
					i_ptr = t.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						io.msg_print(out_val);
						ptr = new IntPointer(i_ptr.tohit);
						enchant = spells.enchant(ptr, 10);
						i_ptr.tohit = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							mor1.calc_bonuses();
						} else {
							io.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 2:
					i_ptr = t.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						io.msg_print(out_val);
						if ((i_ptr.tval >= Constants.TV_HAFTED)&&(i_ptr.tval <= Constants.TV_DIGGING)) {
							j.value(i_ptr.damage[0] * i_ptr.damage[1]);
						} else {
							/* Bows' and arrows' enchantments should not be limited
							 * by their low base damages */
							j.value(10);
						}
						ptr = new IntPointer(i_ptr.todam);
						enchant = spells.enchant(ptr, j.value());
						i_ptr.todam = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							mor1.calc_bonuses ();
						} else {
							io.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 3:
					k.value(0);
					l = 0;
					if (t.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_BODY;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_ARM;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_OUTER;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HANDS;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HEAD;
						k.value(k.value() + 1);
					}
					/* also enchant boots */
					if (t.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_FEET;
						k.value(k.value() + 1);
					}
					
					if (k.value() > 0)	l = tmp[m1.randint(k.value()) - 1];
					if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_BODY].flags) != 0) {
						l = Constants.INVEN_BODY;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_ARM].flags) != 0) {
						l = Constants.INVEN_ARM;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_OUTER].flags) != 0) {
						l = Constants.INVEN_OUTER;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_HEAD].flags) != 0) {
						l = Constants.INVEN_HEAD;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_HANDS].flags) != 0) {
						l = Constants.INVEN_HANDS;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_FEET].flags) != 0) {
						l = Constants.INVEN_FEET;
					}
					
					if (l > 0) {
						i_ptr = t.inventory[l];
						tmp_str = desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						io.msg_print(out_val);
						ptr = new IntPointer(i_ptr.toac);
						enchant = spells.enchant(ptr, 10);
						i_ptr.toac = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							mor1.calc_bonuses ();
						} else {
							io.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 4:
					io.msg_print("This is an identify scroll.");
					ident = true;
					used_up = spells.ident_spell();
					
					/* The identify may merge objects, causing the identify scroll
					 * to move to a different place.	Check for that here.  It can
					 * move arbitrarily far if an identify scroll was used on
					 * another identify scroll, but it always moves down. */
					while (i_ptr.tval != Constants.TV_SCROLL1 || i_ptr.flags != 0x00000008) {
						item_val.value(item_val.value() - 1);
						i_ptr = t.inventory[item_val.value()];
					}
					break;
				case 5:
					if (spells.remove_curse()) {
						io.msg_print("You feel as if someone is watching over you.");
						ident = true;
					}
					break;
				case 6:
					ident = spells.light_area(py.char_row, py.char_col);
					break;
				case 7:
					for (k.value(0); k.value() < m1.randint(3); k.value(k.value() + 1)) {
						y = new IntPointer(py.char_row);
						x = new IntPointer(py.char_col);
						ident |= m1.summon_monster(y, x, false);
					}
					break;
				case 8:
					m3.teleport(10);
					ident = true;
					break;
				case 9:
					m3.teleport(100);
					ident = true;
					break;
				case 10:
					var.dun_level += (-3) + 2 * m1.randint(2);
					if (var.dun_level < 1) {
						var.dun_level = 1;
					}
					var.new_level_flag = true;
					ident = true;
					break;
				case 11:
					if (!py.py.flags.confuse_monster) {
						io.msg_print("Your hands begin to glow.");
						py.py.flags.confuse_monster = true;
						ident = true;
					}
					break;
				case 12:
					ident = true;
					spells.map_area();
					break;
				case 13:
					ident = spells.sleep_monsters1(py.char_row, py.char_col);
					break;
				case 14:
					ident = true;
					spells.warding_glyph();
					break;
				case 15:
					ident = spells.detect_treasure();
					break;
				case 16:
					ident = spells.detect_object();
					break;
				case 17:
					ident = spells.detect_trap();
					break;
				case 18:
					ident = spells.detect_sdoor();
					break;
				case 19:
					io.msg_print("This is a mass genocide scroll.");
					spells.mass_genocide();
					ident = true;
					break;
				case 20:
					ident = spells.detect_invisible();
					break;
				case 21:
					io.msg_print("There is a high pitched humming noise.");
					spells.aggravate_monster(20);
					ident = true;
					break;
				case 22:
					ident = spells.trap_creation();
					break;
				case 23:
					ident = spells.td_destroy();
					break;
				case 24:
					ident = spells.door_creation();
					break;
				case 25:
					io.msg_print("This is a Recharge-Item scroll.");
					ident = true;
					used_up = spells.recharge(60);
					break;
				case 26:
					io.msg_print("This is a genocide scroll.");
					spells.genocide();
					ident = true;
					break;
				case 27:
					ident = spells.unlight_area(py.char_row, py.char_col);
					break;
				case 28:
					ident = spells.protect_evil();
					break;
				case 29:
					ident = true;
					spells.create_food();
					break;
				case 30:
					ident = spells.dispel_creature(Constants.CD_UNDEAD, 60);
					break;
				case 33:
					i_ptr = t.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows brightly!", tmp_str);
						io.msg_print(out_val);
						flag = false;
						ptr = new IntPointer();
						for (k.value(0); k.value() < m1.randint(2); k.value(k.value() + 1)) {
							ptr.value(i_ptr.tohit);
							enchant = spells.enchant(ptr, 10);
							i_ptr.tohit = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if ((i_ptr.tval >= Constants.TV_HAFTED)&&(i_ptr.tval <= Constants.TV_DIGGING)) {
							j.value(i_ptr.damage[0] * i_ptr.damage[1]);
						} else {
							/* Bows' and arrows' enchantments should not be limited
							 * by their low base damages */
							j.value(10);
						}
						ptr = new IntPointer();
						for (k.value(0); k.value() < m1.randint(2); k.value(k.value() + 1)) {
							ptr.value(i_ptr.todam);
							enchant = spells.enchant(ptr, j.value());
							i_ptr.todam = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if (flag) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							mor1.calc_bonuses ();
						} else {
							io.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 34:
					i_ptr = t.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows black, fades.", tmp_str);
						io.msg_print(out_val);
						desc.unmagic_name(i_ptr);
						i_ptr.tohit = -m1.randint(5) - m1.randint(5);
						i_ptr.todam = -m1.randint(5) - m1.randint(5);
						i_ptr.toac = 0;
						/* Must call py_bonuses() before set (clear) flags, and
						 * must call calc_bonuses() after set (clear) flags, so that
						 * all attributes will be properly turned off. */
						mor1.py_bonuses(i_ptr, -1);
						i_ptr.flags = Constants.TR_CURSED;
						mor1.calc_bonuses();
						ident = true;
					}
					break;
				case 35:
					k.value(0);
					l = 0;
					if (t.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_BODY;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_ARM;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_OUTER;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HANDS;
						k.value(k.value() + 1);
					}
					if (t.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HEAD;
						k.value(k.value() + 1);
					}
					/* also enchant boots */
					if (t.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_FEET;
						k.value(k.value() + 1);
					}
					
					if (k.value() > 0)	l = tmp[m1.randint(k.value())-1];
					if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_BODY].flags) != 0) {
						l = Constants.INVEN_BODY;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_ARM].flags) != 0) {
						l = Constants.INVEN_ARM;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_OUTER].flags) != 0) {
						l = Constants.INVEN_OUTER;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_HEAD].flags) != 0) {
						l = Constants.INVEN_HEAD;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_HANDS].flags) != 0) {
						l = Constants.INVEN_HANDS;
					} else if ((Constants.TR_CURSED & t.inventory[Constants.INVEN_FEET].flags) != 0) {
						l = Constants.INVEN_FEET;
					}
					
					if (l > 0) {
						i_ptr = t.inventory[l];
						tmp_str = desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows brightly!", tmp_str);
						io.msg_print(out_val);
						flag = false;
						ptr = new IntPointer();
						for (k.value(0); k.value() < m1.randint(2) + 1; k.value(k.value() + 1)) {
							ptr.value(i_ptr.toac);
							enchant = spells.enchant(ptr, 10);
							i_ptr.toac = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if (flag) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							mor1.calc_bonuses();
						} else {
							io.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 36:
					if ((t.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) && (m1.randint(4) == 1)) {
						k.value(Constants.INVEN_BODY);
					} else if ((t.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) && (m1.randint(3) == 1)) {
						k.value(Constants.INVEN_ARM);
					} else if ((t.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) && (m1.randint(3) == 1)) {
						k.value(Constants.INVEN_OUTER);
					} else if ((t.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) && (m1.randint(3) == 1)) {
						k.value(Constants.INVEN_HEAD);
					} else if ((t.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) && (m1.randint(3) == 1)) {
						k.value(Constants.INVEN_HANDS);
					} else if ((t.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) && (m1.randint(3) == 1)) {
						k.value(Constants.INVEN_FEET);
					} else if (t.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_BODY);
					} else if (t.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_ARM);
					} else if (t.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_OUTER);
					} else if (t.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_HEAD);
					} else if (t.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_HANDS);
					} else if (t.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_FEET);
					} else {
						k.value(0);
					}
					
					if (k.value() > 0) {
						i_ptr = t.inventory[k.value()];
						tmp_str = desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows black, fades.", tmp_str);
						io.msg_print(out_val);
						desc.unmagic_name(i_ptr);
						i_ptr.flags = Constants.TR_CURSED;
						i_ptr.tohit = 0;
						i_ptr.todam = 0;
						i_ptr.toac = -m1.randint(5) - m1.randint(5);
						mor1.calc_bonuses();
						ident = true;
					}
					break;
				case 37:
					ident = false;
					for (k.value(0); k.value() < m1.randint(3); k.value(k.value() + 1)) {
						y = new IntPointer(py.char_row);
						x = new IntPointer(py.char_col);
						ident |= m1.summon_undead(y, x);
					}
					break;
				case 38:
					ident = true;
					spells.bless(m1.randint(12) + 6);
					break;
				case 39:
					ident = true;
					spells.bless(m1.randint(24) + 12);
					break;
				case 40:
					ident = true;
					spells.bless(m1.randint(48) + 24);
					break;
				case 41:
					ident = true;
					if (py.py.flags.word_recall == 0) {
						py.py.flags.word_recall = 25 + m1.randint(30);
					}
					io.msg_print("The air about you becomes charged.");
					break;
				case 42:
					spells.destroy_area(py.char_row, py.char_col);
					ident = true;
					break;
				default:
					io.msg_print("Internal error in scroll()");
					break;
				}
				/* End of Scrolls.			       */
			}
			i_ptr = t.inventory[item_val.value()];
			if (ident) {
				if (!desc.known1_p(i_ptr)) {
					m_ptr = py.py.misc;
					/* round half-way case up */
					m_ptr.exp += (i_ptr.level + (m_ptr.lev >> 1)) / m_ptr.lev;
					m3.prt_experience();
					
					desc.identify(item_val);
					i_ptr = t.inventory[item_val.value()];
				}
			} else if (!desc.known1_p(i_ptr)) {
				desc.sample(i_ptr);
			}
			if (used_up) {
				desc.desc_remain(item_val.value());
				m3.inven_destroy(item_val.value());
			}
		}
	}
}
