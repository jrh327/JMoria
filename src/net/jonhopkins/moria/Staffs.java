/*
 * Staffs.java: staff code
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

public class Staffs {
	
	private Staffs() { }
	
	/* Use a staff.					-RAK-	*/
	public static void use() {
		LongPointer i = new LongPointer();
		IntPointer j = new IntPointer(), k = new IntPointer();
		int chance;
		IntPointer y, x;
		IntPointer item_val = new IntPointer();
		boolean ident;
		PlayerMisc m_ptr;
		InvenType i_ptr;
		
		Variable.free_turn_flag = true;
		if (Treasure.inven_ctr == 0) {
			IO.msg_print("But you are not carrying anything.");
		} else if (!Misc3.find_range(Constants.TV_STAFF, Constants.TV_NEVER, j, k)) {
			IO.msg_print("You are not carrying any staffs.");
		} else if (Moria1.get_item(item_val, "Use which staff?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.free_turn_flag = false;
			m_ptr = Player.py.misc;
			chance = m_ptr.save + Misc3.stat_adj(Constants.A_INT) - i_ptr.level - 5 + (Player.class_level_adj[m_ptr.pclass][Constants.CLA_DEVICE] * m_ptr.lev / 3);
			if (Player.py.flags.confused > 0) {
				chance = chance / 2;
			}
			if ((chance < Constants.USE_DEVICE) && (Misc1.randint(Constants.USE_DEVICE - chance + 1) == 1)) {
				chance = Constants.USE_DEVICE; /* Give everyone a slight chance */
			}
			if (chance <= 0)	chance = 1;
			if (Misc1.randint(chance) < Constants.USE_DEVICE) {
				IO.msg_print("You failed to use the staff properly.");
			} else if (i_ptr.p1 > 0) {
				i.value(i_ptr.flags);
				ident = false;
				(i_ptr.p1)--;
				while (i.value() != 0) {
					j.value(Misc1.bit_pos(i) + 1);
					/* Staffs.				*/
					switch(j.value())
					{
					case 1:
						ident = Spells.light_area(Player.char_row, Player.char_col);
						break;
					case 2:
						ident = Spells.detect_sdoor();
						break;
					case 3:
						ident = Spells.detect_trap();
						break;
					case 4:
						ident = Spells.detect_treasure();
						break;
					case 5:
						ident = Spells.detect_object();
						break;
					case 6:
						Misc3.teleport(100);
						ident = true;
						break;
					case 7:
						ident = true;
						Spells.earthquake();
						break;
					case 8:
						ident = false;
						for (int k1 = 0; k1 < Misc1.randint(4); k1++) {
							y = new IntPointer(Player.char_row);
							x = new IntPointer(Player.char_col);
							ident |= Misc1.summon_monster(y, x, false);
						}
						break;
					case 10:
						ident = true;
						Spells.destroy_area(Player.char_row, Player.char_col);
						break;
					case 11:
						ident = true;
						Spells.starlite(Player.char_row, Player.char_col);
						break;
					case 12:
						ident = Spells.speed_monsters(1);
						break;
					case 13:
						ident = Spells.speed_monsters(-1);
						break;
					case 14:
						ident = Spells.sleep_monsters2();
						break;
					case 15:
						ident = Spells.hp_player(Misc1.randint(8));
						break;
					case 16:
						ident = Spells.detect_invisible();
						break;
					case 17:
						if (Player.py.flags.fast == 0) {
							ident = true;
						}
						Player.py.flags.fast += Misc1.randint(30) + 15;
						break;
					case 18:
						if (Player.py.flags.slow == 0) {
							ident = true;
						}
						Player.py.flags.slow += Misc1.randint(30) + 15;
						break;
					case 19:
						ident = Spells.mass_poly();
						break;
					case 20:
						if (Spells.remove_curse()) {
							if (Player.py.flags.blind < 1) {
								IO.msg_print("The staff glows blue for a moment..");
							}
							ident = true;
						}
						break;
					case 21:
						ident = Spells.detect_evil();
						break;
					case 22:
						if (Spells.cure_blindness()
								|| Spells.cure_poison()
								|| Spells.cure_confusion()) {
							ident = true;
						}
						break;
					case 23:
						ident = Spells.dispel_creature(Constants.CD_EVIL, 60);
						break;
					case 25:
						ident = Spells.unlight_area(Player.char_row, Player.char_col);
						break;
					case 32:
						/* store bought flag */
						break;
					default:
						IO.msg_print("Internal error in staffs()");
						break;
					}
					/* End of staff actions.		*/
				}
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
				Desc.desc_charges(item_val.value());
			} else {
				IO.msg_print("The staff has no charges left.");
				if (!Desc.known2_p(i_ptr)) {
					Misc4.add_inscribe(i_ptr, Constants.ID_EMPTY);
				}
			}
		}
	}
}
