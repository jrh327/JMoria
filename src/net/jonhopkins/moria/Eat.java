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
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.PlayerMisc;

public class Eat {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Moria1 mor1;
	private Player py;
	private Spells spells;
	private Treasure t;
	private Variable var;
	
	private static Eat instance;
	private Eat() { }
	public static Eat getInstance() {
		if (instance == null) {
			instance = new Eat();
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
	
	/* Eat some food.					-RAK-	*/
	public void eat() {
		LongPointer i = new LongPointer();
		IntPointer j = new IntPointer(), k = new IntPointer(), item_val = new IntPointer();
		boolean ident;
		PlayerFlags f_ptr;
		PlayerMisc m_ptr;
		InvenType i_ptr;
		
		var.free_turn_flag = true;
		if (t.inven_ctr == 0)
			io.msg_print("But you are not carrying anything.");
		else if (!m3.find_range(Constants.TV_FOOD, Constants.TV_NEVER, j, k))
			io.msg_print("You are not carrying any food.");
		else if (mor1.get_item(item_val, "Eat what?", j.value(), k.value(), "", "")) {
			i_ptr = t.inventory[item_val.value()];
			var.free_turn_flag = false;
			i.value(i_ptr.flags);
			ident = false;
			while (i.value() != 0) {
				j.value(m1.bit_pos(i) + 1);
				/* Foods					*/
				switch(j.value())
				{
					case 1:
						f_ptr = py.py.flags;
						f_ptr.poisoned += m1.randint(10) + i_ptr.level;
						ident = true;
						break;
					case 2:
						f_ptr = py.py.flags;
						f_ptr.blind += m1.randint(250) + 10 * i_ptr.level + 100;
						m3.draw_cave();
						io.msg_print("A veil of darkness surrounds you.");
						ident = true;
						break;
					case 3:
						f_ptr = py.py.flags;
						f_ptr.afraid += m1.randint(10) + i_ptr.level;
						io.msg_print("You feel terrified!");
						ident = true;
						break;
					case 4:
						f_ptr = py.py.flags;
						f_ptr.confused += m1.randint(10) + i_ptr.level;
						io.msg_print("You feel drugged.");
						ident = true;
						break;
					case 5:
						f_ptr = py.py.flags;
						f_ptr.image += m1.randint(200) + 25 * i_ptr.level + 200;
						io.msg_print("You feel drugged.");
						ident = true;
						break;
					case 6:
						ident = spells.cure_poison();
						break;
					case 7:
						ident = spells.cure_blindness();
						break;
					case 8:
						f_ptr = py.py.flags;
						if (f_ptr.afraid > 1) {
							f_ptr.afraid = 1;
							ident = true;
						}
						break;
					case 9:
						ident = spells.cure_confusion();
						break;
					case 10:
						ident = true;
						spells.lose_str();
						break;
					case 11:
						ident = true;
						spells.lose_con();
						break;
					case 16:
						if (m3.res_stat(Constants.A_STR)) {
							io.msg_print("You feel your strength returning.");
							ident = true;
						}
						break;
					case 17:
						if (m3.res_stat(Constants.A_CON)) {
							io.msg_print("You feel your health returning.");
							ident = true;
						}
						break;
					case 18:
						if (m3.res_stat(Constants.A_INT)) {
							io.msg_print("Your head spins a moment.");
							ident = true;
						}
						break;
					case 19:
						if (m3.res_stat(Constants.A_WIS)) {
							io.msg_print("You feel your wisdom returning.");
							ident = true;
						}
						break;
					case 20:
						if (m3.res_stat(Constants.A_DEX)) {
							io.msg_print("You feel more dextrous.");
							ident = true;
						}
						break;
					case 21:
						if (m3.res_stat(Constants.A_CHR)) {
							io.msg_print("Your skin stops itching.");
							ident = true;
						}
						break;
					case 22:
						ident = spells.hp_player(m1.randint(6));
						break;
					case 23:
						ident = spells.hp_player(m1.randint(12));
						break;
					case 24:
						ident = spells.hp_player(m1.randint(18));
						break;
					case 26:
						ident = spells.hp_player(m1.damroll(3, 12));
						break;
					case 27:
						mor1.take_hit(m1.randint(18), "poisonous food.");
						ident = true;
						break;
					default:
						io.msg_print("Internal error in eat()");
						break;
				}
				/* End of food actions.				*/
			}
			if (ident) {
				if (!desc.known1_p(i_ptr)) {
					/* use identified it, gain experience */
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
			m1.add_food(i_ptr.p1);
			py.py.flags.status &= ~(Constants.PY_WEAK | Constants.PY_HUNGRY);
			m3.prt_hunger();
			desc.desc_remain(item_val.value());
			m3.inven_destroy(item_val.value());
		}
	}
}
