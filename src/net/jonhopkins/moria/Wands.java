/*
 * Wands.java: wand code
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

public class Wands {
	
	private Wands() { }
	
	/* Wands for the aiming.				*/
	public static void aim() {
		LongPointer i = new LongPointer();
		int l;
		boolean ident;
		IntPointer item_val = new IntPointer(), dir = new IntPointer();
		IntPointer j = new IntPointer(), k = new IntPointer();
		int chance;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		
		Variable.free_turn_flag = true;
		if (Treasure.inven_ctr == 0) {
			IO.msg_print("But you are not carrying anything.");
		} else if (!Misc3.find_range(Constants.TV_WAND, Constants.TV_NEVER, j, k)) {
			IO.msg_print("You are not carrying any wands.");
		} else if (Moria1.get_item(item_val, "Aim which wand?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.free_turn_flag = false;
			if (Moria1.get_dir("", dir)) {
				if (Player.py.flags.confused > 0) {
					IO.msg_print("You are confused.");
					do {
						dir.value(Misc1.randint(9));
					} while (dir.value() == 5);
				}
				ident = false;
				m_ptr = Player.py.misc;
				chance = m_ptr.save + Misc3.stat_adj(Constants.A_INT) - i_ptr.level + (Player.class_level_adj[m_ptr.pclass][Constants.CLA_DEVICE] * m_ptr.lev / 3);
				if (Player.py.flags.confused > 0) {
					chance = chance / 2;
				}
				if ((chance < Constants.USE_DEVICE) && (Misc1.randint(Constants.USE_DEVICE - chance + 1) == 1)) {
					chance = Constants.USE_DEVICE; /* Give everyone a slight chance */
				}
				if (chance <= 0)	chance = 1;
				if (Misc1.randint(chance) < Constants.USE_DEVICE) {
					IO.msg_print("You failed to use the wand properly.");
				} else if (i_ptr.p1 > 0) {
					i.value(i_ptr.flags);
					i_ptr.p1--;
					while (i.value() != 0) {
						j.value(Misc1.bit_pos(i) + 1);
						k.value(Player.char_row);
						l = Player.char_col;
						/* Wands			 */
						switch(j.value())
						{
						case 1:
							IO.msg_print("A line of blue shimmering light appears.");
							Spells.light_line(dir.value(), Player.char_row, Player.char_col);
							ident = true;
							break;
						case 2:
							Spells.fire_bolt(Constants.GF_LIGHTNING, dir.value(), k.value(), l, Misc1.damroll(4, 8), Player.spell_names[8]);
							ident = true;
							break;
						case 3:
							Spells.fire_bolt(Constants.GF_FROST, dir.value(), k.value(), l, Misc1.damroll(6, 8), Player.spell_names[14]);
							ident = true;
							break;
						case 4:
							Spells.fire_bolt(Constants.GF_FIRE, dir.value(), k.value(), l, Misc1.damroll(9, 8), Player.spell_names[22]);
							ident = true;
							break;
						case 5:
							ident = Spells.wall_to_mud(dir.value(), k.value(), l);
							break;
						case 6:
							ident = Spells.poly_monster(dir.value(), k.value(), l);
							break;
						case 7:
							ident = Spells.hp_monster(dir.value(), k.value(), l, -Misc1.damroll(4, 6));
							break;
						case 8:
							ident = Spells.speed_monster(dir.value(), k.value(), l, 1);
							break;
						case 9:
							ident = Spells.speed_monster(dir.value(), k.value(), l, -1);
							break;
						case 10:
							ident = Spells.confuse_monster(dir.value(), k.value(), l);
							break;
						case 11:
							ident = Spells.sleep_monster(dir.value(), k.value(), l);
							break;
						case 12:
							ident = Spells.drain_life(dir.value(), k.value(), l);
							break;
						case 13:
							ident = Spells.td_destroy2(dir.value(), k.value(), l);
							break;
						case 14:
							Spells.fire_bolt(Constants.GF_MAGIC_MISSILE, dir.value(), k.value(), l, Misc1.damroll(2, 6), Player.spell_names[0]);
							ident = true;
							break;
						case 15:
							ident = Spells.build_wall(dir.value(), k.value(), l);
							break;
						case 16:
							ident = Spells.clone_monster(dir.value(), k.value(), l);
							break;
						case 17:
							ident = Spells.teleport_monster(dir.value(), k.value(), l);
							break;
						case 18:
							ident = Spells.disarm_all(dir.value(), k.value(), l);
							break;
						case 19:
							Spells.fire_ball(Constants.GF_LIGHTNING, dir.value(), k.value(), l, 32, "Lightning Ball");
							ident = true;
							break;
						case 20:
							Spells.fire_ball(Constants.GF_FROST, dir.value(), k.value(), l, 48, "Cold Ball");
							ident = true;
							break;
						case 21:
							Spells.fire_ball(Constants.GF_FIRE, dir.value(), k.value(), l, 72, Player.spell_names[28]);
							ident = true;
							break;
						case 22:
							Spells.fire_ball(Constants.GF_POISON_GAS, dir.value(), k.value(), l, 12, Player.spell_names[6]);
							ident = true;
							break;
						case 23:
							Spells.fire_ball(Constants.GF_ACID, dir.value(), k.value(), l, 60, "Acid Ball");
							ident = true;
							break;
						case 24:
							i.value(1L << (Misc1.randint(23) - 1));
							break;
						default:
							IO.msg_print("Internal error in wands()");
							break;
						}
						/* End of Wands.		    */
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
					IO.msg_print("The wand has no charges left.");
					if (!Desc.known2_p(i_ptr)) {
						Misc4.add_inscribe(i_ptr, Constants.ID_EMPTY);
					}
				}
			}
		}
	}
}
