/**
 * Potions.java: code for potions
 * <p>
 * Copyright (c) 2007 James E. Wilson, Robert A. Koeneke
 * <br>
 * Copyright (c) 2014 Jon Hopkins
 * <p>
 * This file is part of Moria.
 * <p>
 * Moria is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * Moria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Moria.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package net.jonhopkins.moria;

import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.PlayerMisc;

public class Potions {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Moria1 mor1;
	private Player py;
	private Spells spells;
	private Treasure t;
	private Variable var;
	
	private static Potions instance;
	private Potions() { }
	public static Potions getInstance() {
		if (instance == null) {
			instance = new Potions();
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
	
	/* Potions for the quaffing				-RAK-	*/
	public void quaff() {
		LongPointer i = new LongPointer();
		long l;
		IntPointer j = new IntPointer(), k = new IntPointer();
		IntPointer item_val = new IntPointer();
		boolean ident;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		PlayerFlags f_ptr;
		
		var.free_turn_flag = true;
		if (t.inven_ctr == 0) {
			io.msg_print("But you are not carrying anything.");
		} else if (!m3.find_range(Constants.TV_POTION1, Constants.TV_POTION2, j, k)) {
			io.msg_print("You are not carrying any potions.");
		} else if (mor1.get_item(item_val, "Quaff which potion?", j.value(), k.value(), "", "")) {
			i_ptr = t.inventory[item_val.value()];
			i.value(i_ptr.flags);
			var.free_turn_flag = false;
			ident = false;
			if (i.value() == 0) {
				io.msg_print ("You feel less thirsty.");
				ident = true;
			} else while (i.value() != 0) {
				j.value(m1.bit_pos(i) + 1);
				if (i_ptr.tval == Constants.TV_POTION2)
					j.value(j.value() + 32);
				/* Potions						*/
				switch(j.value())
				{
				case 1:
					if (m3.inc_stat(Constants.A_STR)) {
						io.msg_print("Wow!  What bulging muscles!");
						ident = true;
					}
					break;
				case 2:
					ident = true;
					spells.lose_str();
					break;
				case 3:
					if (m3.res_stat(Constants.A_STR)) {
						io.msg_print("You feel warm all over.");
						ident = true;
					}
					break;
				case 4:
					if (m3.inc_stat(Constants.A_INT)) {
						io.msg_print("Aren't you brilliant!");
						ident = true;
					}
					break;
				case 5:
					ident = true;
					spells.lose_int();
					break;
				case 6:
					if (m3.res_stat(Constants.A_INT)) {
						io.msg_print("You have have a warm feeling.");
						ident = true;
					}
					break;
				case 7:
					if (m3.inc_stat(Constants.A_WIS)) {
						io.msg_print("You suddenly have a profound thought!");
						ident = true;
					}
					break;
				case 8:
					ident = true;
					spells.lose_wis();
					break;
				case 9:
					if (m3.res_stat(Constants.A_WIS)) {
						io.msg_print("You feel your wisdom returning.");
						ident = true;
					}
					break;
				case 10:
					if (m3.inc_stat(Constants.A_CHR)) {
						io.msg_print("Gee, ain't you cute!");
						ident = true;
					}
					break;
				case 11:
					ident = true;
					spells.lose_chr();
					break;
				case 12:
					if (m3.res_stat(Constants.A_CHR)) {
						io.msg_print("You feel your looks returning.");
						ident = true;
					}
					break;
				case 13:
					ident = spells.hp_player(m1.damroll(2, 7));
					break;
				case 14:
					ident = spells.hp_player(m1.damroll(4, 7));
					break;
				case 15:
					ident = spells.hp_player(m1.damroll(6, 7));
					break;
				case 16:
					ident = spells.hp_player(1000);
					break;
				case 17:
					if (m3.inc_stat(Constants.A_CON)) {
						io.msg_print("You feel tingly for a moment.");
						ident = true;
					}
					break;
				case 18:
					m_ptr = py.py.misc;
					if (m_ptr.exp < Constants.MAX_EXP) {
						l = (m_ptr.exp / 2) + 10;
						if (l > 100000L)  l = 100000L;
						m_ptr.exp += l;
						io.msg_print("You feel more experienced.");
						m3.prt_experience();
						ident = true;
					}
					break;
				case 19:
					f_ptr = py.py.flags;
					if (!f_ptr.free_act) {
						/* paralysis must == 0, otherwise could not drink potion */
						io.msg_print("You fall asleep.");
						f_ptr.paralysis += m1.randint(4) + 4;
						ident = true;
					}
					break;
				case 20:
					f_ptr = py.py.flags;
					if (f_ptr.blind == 0) {
						io.msg_print("You are covered by a veil of darkness.");
						ident = true;
					}
					f_ptr.blind += m1.randint(100) + 100;
					break;
				case 21:
					f_ptr = py.py.flags;
					if (f_ptr.confused == 0) {
						io.msg_print("Hey!  This is good stuff!  * Hick! *");
						ident = true;
					}
					f_ptr.confused += m1.randint(20) + 12;
					break;
				case 22:
					f_ptr = py.py.flags;
					if (f_ptr.poisoned == 0) {
						io.msg_print("You feel very sick.");
						ident = true;
					}
					f_ptr.poisoned += m1.randint(15) + 10;
					break;
				case 23:
					if (py.py.flags.fast == 0) {
						ident = true;
					}
					py.py.flags.fast += m1.randint(25) + 15;
					break;
				case 24:
					if (py.py.flags.slow == 0) {
						ident = true;
					}
					py.py.flags.slow += m1.randint(25) + 15;
					break;
				case 26:
					if (m3.inc_stat(Constants.A_DEX)) {
						io.msg_print("You feel more limber!");
						ident = true;
					}
					break;
				case 27:
					if (m3.res_stat(Constants.A_DEX)) {
						io.msg_print("You feel less clumsy.");
						ident = true;
					}
					break;
				case 28:
					if (m3.res_stat(Constants.A_CON)) {
						io.msg_print("You feel your health returning!");
						ident = true;
					}
					break;
				case 29:
					ident = spells.cure_blindness();
					break;
				case 30:
					ident = spells.cure_confusion();
					break;
				case 31:
					ident = spells.cure_poison();
					break;
				case 34:
					if (py.py.misc.exp > 0) {
						int m, scale;
						io.msg_print("You feel your memories fade.");
						/* Lose between 1/5 and 2/5 of your experience */
						m = py.py.misc.exp / 5;
						if (py.py.misc.exp > Constants.MAX_SHORT) {
							scale = (int)(Constants.MAX_LONG / py.py.misc.exp);
							m += (m1.randint(scale) * py.py.misc.exp) / (scale * 5);
						} else {
							m += m1.randint(py.py.misc.exp) / 5;
						}
						spells.lose_exp(m);
						ident = true;
					}
					break;
				case 35:
					f_ptr = py.py.flags;
					spells.cure_poison();
					if (f_ptr.food > 150)  f_ptr.food = 150;
					f_ptr.paralysis = 4;
					io.msg_print("The potion makes you vomit!");
					ident = true;
					break;
				case 36:
					if (py.py.flags.invuln == 0) {
						ident = true;
					}
					py.py.flags.invuln += m1.randint(10) + 10;
					break;
				case 37:
					if (py.py.flags.hero == 0) {
						ident = true;
					}
					py.py.flags.hero += m1.randint(25) + 25;
					break;
				case 38:
					if (py.py.flags.shero == 0) {
						ident = true;
					}
					py.py.flags.shero += m1.randint(25) + 25;
					break;
				case 39:
					ident = spells.remove_fear();
					break;
				case 40:
					ident = spells.restore_level();
					break;
				case 41:
					f_ptr = py.py.flags;
					if (f_ptr.resist_heat == 0) {
						ident = true;
					}
					f_ptr.resist_heat += m1.randint(10) + 10;
					break;
				case 42:
					f_ptr = py.py.flags;
					if (f_ptr.resist_cold == 0) {
						ident = true;
					}
					f_ptr.resist_cold += m1.randint(10) + 10;
					break;
				case 43:
					if (py.py.flags.detect_inv == 0) {
						ident = true;
					}
					spells.detect_inv2(m1.randint(12) + 12);
					break;
				case 44:
					ident = spells.slow_poison();
					break;
				case 45:
					ident = spells.cure_poison();
					break;
				case 46:
					m_ptr = py.py.misc;
					if (m_ptr.cmana < m_ptr.mana) {
						m_ptr.cmana = m_ptr.mana;
						ident = true;
						io.msg_print("Your feel your head clear.");
						m3.prt_cmana();
					}
					break;
				case 47:
					f_ptr = py.py.flags;
					if (f_ptr.tim_infra == 0) {
						io.msg_print("Your eyes begin to tingle.");
						ident = true;
					}
					f_ptr.tim_infra += 100 + m1.randint(100);
					break;
				default:
					io.msg_print("Internal error in potion()");
					break;
				}
				/* End of Potions.					*/
			}
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
			
			m1.add_food(i_ptr.p1);
			desc.desc_remain(item_val.value());
			m3.inven_destroy(item_val.value());
		}
	}
}
