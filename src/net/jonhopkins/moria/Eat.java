/*
 * Eat.java: food code
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

import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Eat {
	
	private Eat() { }
	
	/* Eat some food.					-RAK-	*/
	public static void eat() {
		IntPointer i = new IntPointer();
		IntPointer j = new IntPointer(), k = new IntPointer(), item_val = new IntPointer();
		boolean ident;
		PlayerFlags f_ptr;
		PlayerMisc m_ptr;
		InvenType i_ptr;
		
		Variable.free_turn_flag = true;
		if (Treasure.inven_ctr == 0) {
			IO.msg_print("But you are not carrying anything.");
		} else if (!Misc3.find_range(Constants.TV_FOOD, Constants.TV_NEVER, j, k)) {
			IO.msg_print("You are not carrying any food.");
		} else if (Moria1.get_item(item_val, "Eat what?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.free_turn_flag = false;
			i.value(i_ptr.flags);
			ident = false;
			while (i.value() != 0) {
				j.value(Misc1.bit_pos(i) + 1);
				/* Foods					*/
				switch(j.value()) {
					case 1:
						f_ptr = Player.py.flags;
						f_ptr.poisoned += Misc1.randint(10) + i_ptr.level;
						ident = true;
						break;
					case 2:
						f_ptr = Player.py.flags;
						f_ptr.blind += Misc1.randint(250) + 10 * i_ptr.level + 100;
						Misc3.draw_cave();
						IO.msg_print("A veil of darkness surrounds you.");
						ident = true;
						break;
					case 3:
						f_ptr = Player.py.flags;
						f_ptr.afraid += Misc1.randint(10) + i_ptr.level;
						IO.msg_print("You feel terrified!");
						ident = true;
						break;
					case 4:
						f_ptr = Player.py.flags;
						f_ptr.confused += Misc1.randint(10) + i_ptr.level;
						IO.msg_print("You feel drugged.");
						ident = true;
						break;
					case 5:
						f_ptr = Player.py.flags;
						f_ptr.image += Misc1.randint(200) + 25 * i_ptr.level + 200;
						IO.msg_print("You feel drugged.");
						ident = true;
						break;
					case 6:
						ident = Spells.cure_poison();
						break;
					case 7:
						ident = Spells.cure_blindness();
						break;
					case 8:
						f_ptr = Player.py.flags;
						if (f_ptr.afraid > 1) {
							f_ptr.afraid = 1;
							ident = true;
						}
						break;
					case 9:
						ident = Spells.cure_confusion();
						break;
					case 10:
						ident = true;
						Spells.lose_str();
						break;
					case 11:
						ident = true;
						Spells.lose_con();
						break;
					case 16:
						if (Misc3.res_stat(Constants.A_STR)) {
							IO.msg_print("You feel your strength returning.");
							ident = true;
						}
						break;
					case 17:
						if (Misc3.res_stat(Constants.A_CON)) {
							IO.msg_print("You feel your health returning.");
							ident = true;
						}
						break;
					case 18:
						if (Misc3.res_stat(Constants.A_INT)) {
							IO.msg_print("Your head spins a moment.");
							ident = true;
						}
						break;
					case 19:
						if (Misc3.res_stat(Constants.A_WIS)) {
							IO.msg_print("You feel your wisdom returning.");
							ident = true;
						}
						break;
					case 20:
						if (Misc3.res_stat(Constants.A_DEX)) {
							IO.msg_print("You feel more dextrous.");
							ident = true;
						}
						break;
					case 21:
						if (Misc3.res_stat(Constants.A_CHR)) {
							IO.msg_print("Your skin stops itching.");
							ident = true;
						}
						break;
					case 22:
						ident = Spells.hp_player(Misc1.randint(6));
						break;
					case 23:
						ident = Spells.hp_player(Misc1.randint(12));
						break;
					case 24:
						ident = Spells.hp_player(Misc1.randint(18));
						break;
					case 26:
						ident = Spells.hp_player(Misc1.damroll(3, 12));
						break;
					case 27:
						Moria1.take_hit(Misc1.randint(18), "poisonous food.");
						ident = true;
						break;
					default:
						IO.msg_print("Internal error in eat()");
						break;
				}
				/* End of food actions.				*/
			}
			if (ident) {
				if (!Desc.known1_p(i_ptr)) {
					/* use identified it, gain experience */
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
			Misc1.add_food(i_ptr.p1);
			Player.py.flags.status &= ~(Constants.PY_WEAK | Constants.PY_HUNGRY);
			Misc3.prt_hunger();
			Desc.desc_remain(item_val.value());
			Misc3.inven_destroy(item_val.value());
		}
	}
}
