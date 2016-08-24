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
	
	private Scrolls() { }
	
	/* Scrolls for the reading				-RAK-	*/
	public static void read_scroll() {
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
		
		Variable.free_turn_flag = true;
		if (Player.py.flags.blind > 0) {
			IO.msg_print("You can't see to read the scroll.");
		} else if (Moria1.no_light()) {
			IO.msg_print("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.msg_print("You are too confused to read a scroll.");
		} else if (Treasure.inven_ctr == 0) {
			IO.msg_print("You are not carrying anything!");
		} else if (!Misc3.find_range(Constants.TV_SCROLL1, Constants.TV_SCROLL2, j, k)) {
			IO.msg_print("You are not carrying any scrolls!");
		} else if (Moria1.get_item(item_val, "Read which scroll?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.free_turn_flag = false;
			used_up = true;
			i.value(i_ptr.flags);
			ident = false;
			
			while (i.value() != 0) {
				j.value(Misc1.bit_pos(i) + 1);
				if (i_ptr.tval == Constants.TV_SCROLL2) {
					j.value(j.value() + 32);
				}
				
				/* Scrolls.			*/
				switch(j.value())
				{
				case 1:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = Desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						IO.msg_print(out_val);
						ptr = new IntPointer(i_ptr.tohit);
						enchant = Spells.enchant(ptr, 10);
						i_ptr.tohit = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calc_bonuses();
						} else {
							IO.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 2:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = Desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						IO.msg_print(out_val);
						if ((i_ptr.tval >= Constants.TV_HAFTED)&&(i_ptr.tval <= Constants.TV_DIGGING)) {
							j.value(i_ptr.damage[0] * i_ptr.damage[1]);
						} else {
							/* Bows' and arrows' enchantments should not be limited
							 * by their low base damages */
							j.value(10);
						}
						ptr = new IntPointer(i_ptr.todam);
						enchant = Spells.enchant(ptr, j.value());
						i_ptr.todam = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calc_bonuses ();
						} else {
							IO.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 3:
					k.value(0);
					l = 0;
					if (Treasure.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_BODY;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_ARM;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_OUTER;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HANDS;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HEAD;
						k.value(k.value() + 1);
					}
					/* also enchant boots */
					if (Treasure.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_FEET;
						k.value(k.value() + 1);
					}
					
					if (k.value() > 0)	l = tmp[Misc1.randint(k.value()) - 1];
					if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_BODY].flags) != 0) {
						l = Constants.INVEN_BODY;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_ARM].flags) != 0) {
						l = Constants.INVEN_ARM;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_OUTER].flags) != 0) {
						l = Constants.INVEN_OUTER;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HEAD].flags) != 0) {
						l = Constants.INVEN_HEAD;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HANDS].flags) != 0) {
						l = Constants.INVEN_HANDS;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_FEET].flags) != 0) {
						l = Constants.INVEN_FEET;
					}
					
					if (l > 0) {
						i_ptr = Treasure.inventory[l];
						tmp_str = Desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						IO.msg_print(out_val);
						ptr = new IntPointer(i_ptr.toac);
						enchant = Spells.enchant(ptr, 10);
						i_ptr.toac = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calc_bonuses ();
						} else {
							IO.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 4:
					IO.msg_print("This is an identify scroll.");
					ident = true;
					used_up = Spells.ident_spell();
					
					/* The identify may merge objects, causing the identify scroll
					 * to move to a different place.	Check for that here.  It can
					 * move arbitrarily far if an identify scroll was used on
					 * another identify scroll, but it always moves down. */
					while (i_ptr.tval != Constants.TV_SCROLL1 || i_ptr.flags != 0x00000008) {
						item_val.value(item_val.value() - 1);
						i_ptr = Treasure.inventory[item_val.value()];
					}
					break;
				case 5:
					if (Spells.remove_curse()) {
						IO.msg_print("You feel as if someone is watching over you.");
						ident = true;
					}
					break;
				case 6:
					ident = Spells.light_area(Player.char_row, Player.char_col);
					break;
				case 7:
					for (k.value(0); k.value() < Misc1.randint(3); k.value(k.value() + 1)) {
						y = new IntPointer(Player.char_row);
						x = new IntPointer(Player.char_col);
						ident |= Misc1.summon_monster(y, x, false);
					}
					break;
				case 8:
					Misc3.teleport(10);
					ident = true;
					break;
				case 9:
					Misc3.teleport(100);
					ident = true;
					break;
				case 10:
					Variable.dun_level += (-3) + 2 * Misc1.randint(2);
					if (Variable.dun_level < 1) {
						Variable.dun_level = 1;
					}
					Variable.new_level_flag = true;
					ident = true;
					break;
				case 11:
					if (!Player.py.flags.confuse_monster) {
						IO.msg_print("Your hands begin to glow.");
						Player.py.flags.confuse_monster = true;
						ident = true;
					}
					break;
				case 12:
					ident = true;
					Spells.map_area();
					break;
				case 13:
					ident = Spells.sleep_monsters1(Player.char_row, Player.char_col);
					break;
				case 14:
					ident = true;
					Spells.warding_glyph();
					break;
				case 15:
					ident = Spells.detect_treasure();
					break;
				case 16:
					ident = Spells.detect_object();
					break;
				case 17:
					ident = Spells.detect_trap();
					break;
				case 18:
					ident = Spells.detect_sdoor();
					break;
				case 19:
					IO.msg_print("This is a mass genocide scroll.");
					Spells.mass_genocide();
					ident = true;
					break;
				case 20:
					ident = Spells.detect_invisible();
					break;
				case 21:
					IO.msg_print("There is a high pitched humming noise.");
					Spells.aggravate_monster(20);
					ident = true;
					break;
				case 22:
					ident = Spells.trap_creation();
					break;
				case 23:
					ident = Spells.td_destroy();
					break;
				case 24:
					ident = Spells.door_creation();
					break;
				case 25:
					IO.msg_print("This is a Recharge-Item scroll.");
					ident = true;
					used_up = Spells.recharge(60);
					break;
				case 26:
					IO.msg_print("This is a genocide scroll.");
					Spells.genocide();
					ident = true;
					break;
				case 27:
					ident = Spells.unlight_area(Player.char_row, Player.char_col);
					break;
				case 28:
					ident = Spells.protect_evil();
					break;
				case 29:
					ident = true;
					Spells.create_food();
					break;
				case 30:
					ident = Spells.dispel_creature(Constants.CD_UNDEAD, 60);
					break;
				case 33:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = Desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows brightly!", tmp_str);
						IO.msg_print(out_val);
						flag = false;
						ptr = new IntPointer();
						for (k.value(0); k.value() < Misc1.randint(2); k.value(k.value() + 1)) {
							ptr.value(i_ptr.tohit);
							enchant = Spells.enchant(ptr, 10);
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
						for (k.value(0); k.value() < Misc1.randint(2); k.value(k.value() + 1)) {
							ptr.value(i_ptr.todam);
							enchant = Spells.enchant(ptr, j.value());
							i_ptr.todam = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if (flag) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calc_bonuses ();
						} else {
							IO.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 34:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.tval != Constants.TV_NOTHING) {
						tmp_str = Desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows black, fades.", tmp_str);
						IO.msg_print(out_val);
						Desc.unmagic_name(i_ptr);
						i_ptr.tohit = -Misc1.randint(5) - Misc1.randint(5);
						i_ptr.todam = -Misc1.randint(5) - Misc1.randint(5);
						i_ptr.toac = 0;
						/* Must call py_bonuses() before set (clear) flags, and
						 * must call calc_bonuses() after set (clear) flags, so that
						 * all attributes will be properly turned off. */
						Moria1.py_bonuses(i_ptr, -1);
						i_ptr.flags = Constants.TR_CURSED;
						Moria1.calc_bonuses();
						ident = true;
					}
					break;
				case 35:
					k.value(0);
					l = 0;
					if (Treasure.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_BODY;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_ARM;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_OUTER;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HANDS;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HEAD;
						k.value(k.value() + 1);
					}
					/* also enchant boots */
					if (Treasure.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_FEET;
						k.value(k.value() + 1);
					}
					
					if (k.value() > 0)	l = tmp[Misc1.randint(k.value())-1];
					if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_BODY].flags) != 0) {
						l = Constants.INVEN_BODY;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_ARM].flags) != 0) {
						l = Constants.INVEN_ARM;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_OUTER].flags) != 0) {
						l = Constants.INVEN_OUTER;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HEAD].flags) != 0) {
						l = Constants.INVEN_HEAD;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HANDS].flags) != 0) {
						l = Constants.INVEN_HANDS;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_FEET].flags) != 0) {
						l = Constants.INVEN_FEET;
					}
					
					if (l > 0) {
						i_ptr = Treasure.inventory[l];
						tmp_str = Desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows brightly!", tmp_str);
						IO.msg_print(out_val);
						flag = false;
						ptr = new IntPointer();
						for (k.value(0); k.value() < Misc1.randint(2) + 1; k.value(k.value() + 1)) {
							ptr.value(i_ptr.toac);
							enchant = Spells.enchant(ptr, 10);
							i_ptr.toac = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if (flag) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calc_bonuses();
						} else {
							IO.msg_print("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 36:
					if ((Treasure.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) && (Misc1.randint(4) == 1)) {
						k.value(Constants.INVEN_BODY);
					} else if ((Treasure.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) && (Misc1.randint(3) == 1)) {
						k.value(Constants.INVEN_ARM);
					} else if ((Treasure.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) && (Misc1.randint(3) == 1)) {
						k.value(Constants.INVEN_OUTER);
					} else if ((Treasure.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) && (Misc1.randint(3) == 1)) {
						k.value(Constants.INVEN_HEAD);
					} else if ((Treasure.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) && (Misc1.randint(3) == 1)) {
						k.value(Constants.INVEN_HANDS);
					} else if ((Treasure.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) && (Misc1.randint(3) == 1)) {
						k.value(Constants.INVEN_FEET);
					} else if (Treasure.inventory[Constants.INVEN_BODY].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_BODY);
					} else if (Treasure.inventory[Constants.INVEN_ARM].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_ARM);
					} else if (Treasure.inventory[Constants.INVEN_OUTER].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_OUTER);
					} else if (Treasure.inventory[Constants.INVEN_HEAD].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_HEAD);
					} else if (Treasure.inventory[Constants.INVEN_HANDS].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_HANDS);
					} else if (Treasure.inventory[Constants.INVEN_FEET].tval != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_FEET);
					} else {
						k.value(0);
					}
					
					if (k.value() > 0) {
						i_ptr = Treasure.inventory[k.value()];
						tmp_str = Desc.objdes(i_ptr, false);
						out_val = String.format("Your %s glows black, fades.", tmp_str);
						IO.msg_print(out_val);
						Desc.unmagic_name(i_ptr);
						i_ptr.flags = Constants.TR_CURSED;
						i_ptr.tohit = 0;
						i_ptr.todam = 0;
						i_ptr.toac = -Misc1.randint(5) - Misc1.randint(5);
						Moria1.calc_bonuses();
						ident = true;
					}
					break;
				case 37:
					ident = false;
					for (k.value(0); k.value() < Misc1.randint(3); k.value(k.value() + 1)) {
						y = new IntPointer(Player.char_row);
						x = new IntPointer(Player.char_col);
						ident |= Misc1.summon_undead(y, x);
					}
					break;
				case 38:
					ident = true;
					Spells.bless(Misc1.randint(12) + 6);
					break;
				case 39:
					ident = true;
					Spells.bless(Misc1.randint(24) + 12);
					break;
				case 40:
					ident = true;
					Spells.bless(Misc1.randint(48) + 24);
					break;
				case 41:
					ident = true;
					if (Player.py.flags.word_recall == 0) {
						Player.py.flags.word_recall = 25 + Misc1.randint(30);
					}
					IO.msg_print("The air about you becomes charged.");
					break;
				case 42:
					Spells.destroy_area(Player.char_row, Player.char_col);
					ident = true;
					break;
				default:
					IO.msg_print("Internal error in scroll()");
					break;
				}
				/* End of Scrolls.			       */
			}
			i_ptr = Treasure.inventory[item_val.value()];
			if (ident) {
				if (!Desc.known1_p(i_ptr)) {
					m_ptr = Player.py.misc;
					/* round half-way case up */
					m_ptr.exp += (i_ptr.level + (m_ptr.lev >> 1)) / m_ptr.lev;
					Misc3.prt_experience();
					
					Desc.identify(item_val);
					i_ptr = Treasure.inventory[item_val.value()];
				}
			} else if (!Desc.known1_p(i_ptr)) {
				Desc.sample(i_ptr);
			}
			if (used_up) {
				Desc.desc_remain(item_val.value());
				Misc3.inven_destroy(item_val.value());
			}
		}
	}
}
