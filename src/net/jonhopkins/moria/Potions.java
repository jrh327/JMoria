/*
 * Potions.java: code for potions
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

public class Potions {
	
	private Potions() { }
	
	/* Potions for the quaffing				-RAK-	*/
	public static void quaff() {
		IntPointer i = new IntPointer();
		long l;
		IntPointer j = new IntPointer(), k = new IntPointer();
		IntPointer item_val = new IntPointer();
		boolean ident;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		PlayerFlags f_ptr;
		
		Variable.free_turn_flag = true;
		if (Treasure.inven_ctr == 0) {
			IO.msg_print("But you are not carrying anything.");
		} else if (!Misc3.find_range(Constants.TV_POTION1, Constants.TV_POTION2, j, k)) {
			IO.msg_print("You are not carrying any potions.");
		} else if (Moria1.get_item(item_val, "Quaff which potion?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			i.value(i_ptr.flags);
			Variable.free_turn_flag = false;
			ident = false;
			if (i.value() == 0) {
				IO.msg_print ("You feel less thirsty.");
				ident = true;
			} else while (i.value() != 0) {
				j.value(Misc1.bit_pos(i) + 1);
				if (i_ptr.tval == Constants.TV_POTION2) {
					j.value(j.value() + 32);
				}
				/* Potions						*/
				switch(j.value())
				{
				case 1:
					if (Misc3.inc_stat(Constants.A_STR)) {
						IO.msg_print("Wow!  What bulging muscles!");
						ident = true;
					}
					break;
				case 2:
					ident = true;
					Spells.lose_str();
					break;
				case 3:
					if (Misc3.res_stat(Constants.A_STR)) {
						IO.msg_print("You feel warm all over.");
						ident = true;
					}
					break;
				case 4:
					if (Misc3.inc_stat(Constants.A_INT)) {
						IO.msg_print("Aren't you brilliant!");
						ident = true;
					}
					break;
				case 5:
					ident = true;
					Spells.lose_int();
					break;
				case 6:
					if (Misc3.res_stat(Constants.A_INT)) {
						IO.msg_print("You have have a warm feeling.");
						ident = true;
					}
					break;
				case 7:
					if (Misc3.inc_stat(Constants.A_WIS)) {
						IO.msg_print("You suddenly have a profound thought!");
						ident = true;
					}
					break;
				case 8:
					ident = true;
					Spells.lose_wis();
					break;
				case 9:
					if (Misc3.res_stat(Constants.A_WIS)) {
						IO.msg_print("You feel your wisdom returning.");
						ident = true;
					}
					break;
				case 10:
					if (Misc3.inc_stat(Constants.A_CHR)) {
						IO.msg_print("Gee, ain't you cute!");
						ident = true;
					}
					break;
				case 11:
					ident = true;
					Spells.lose_chr();
					break;
				case 12:
					if (Misc3.res_stat(Constants.A_CHR)) {
						IO.msg_print("You feel your looks returning.");
						ident = true;
					}
					break;
				case 13:
					ident = Spells.hp_player(Misc1.damroll(2, 7));
					break;
				case 14:
					ident = Spells.hp_player(Misc1.damroll(4, 7));
					break;
				case 15:
					ident = Spells.hp_player(Misc1.damroll(6, 7));
					break;
				case 16:
					ident = Spells.hp_player(1000);
					break;
				case 17:
					if (Misc3.inc_stat(Constants.A_CON)) {
						IO.msg_print("You feel tingly for a moment.");
						ident = true;
					}
					break;
				case 18:
					m_ptr = Player.py.misc;
					if (m_ptr.exp < Constants.MAX_EXP) {
						l = (m_ptr.exp / 2) + 10;
						if (l > 100000L)  l = 100000L;
						m_ptr.exp += l;
						IO.msg_print("You feel more experienced.");
						Misc3.prt_experience();
						ident = true;
					}
					break;
				case 19:
					f_ptr = Player.py.flags;
					if (!f_ptr.free_act) {
						/* paralysis must == 0, otherwise could not drink potion */
						IO.msg_print("You fall asleep.");
						f_ptr.paralysis += Misc1.randint(4) + 4;
						ident = true;
					}
					break;
				case 20:
					f_ptr = Player.py.flags;
					if (f_ptr.blind == 0) {
						IO.msg_print("You are covered by a veil of darkness.");
						ident = true;
					}
					f_ptr.blind += Misc1.randint(100) + 100;
					break;
				case 21:
					f_ptr = Player.py.flags;
					if (f_ptr.confused == 0) {
						IO.msg_print("Hey!  This is good stuff!  * Hick! *");
						ident = true;
					}
					f_ptr.confused += Misc1.randint(20) + 12;
					break;
				case 22:
					f_ptr = Player.py.flags;
					if (f_ptr.poisoned == 0) {
						IO.msg_print("You feel very sick.");
						ident = true;
					}
					f_ptr.poisoned += Misc1.randint(15) + 10;
					break;
				case 23:
					if (Player.py.flags.fast == 0) {
						ident = true;
					}
					Player.py.flags.fast += Misc1.randint(25) + 15;
					break;
				case 24:
					if (Player.py.flags.slow == 0) {
						ident = true;
					}
					Player.py.flags.slow += Misc1.randint(25) + 15;
					break;
				case 26:
					if (Misc3.inc_stat(Constants.A_DEX)) {
						IO.msg_print("You feel more limber!");
						ident = true;
					}
					break;
				case 27:
					if (Misc3.res_stat(Constants.A_DEX)) {
						IO.msg_print("You feel less clumsy.");
						ident = true;
					}
					break;
				case 28:
					if (Misc3.res_stat(Constants.A_CON)) {
						IO.msg_print("You feel your health returning!");
						ident = true;
					}
					break;
				case 29:
					ident = Spells.cure_blindness();
					break;
				case 30:
					ident = Spells.cure_confusion();
					break;
				case 31:
					ident = Spells.cure_poison();
					break;
				case 34:
					if (Player.py.misc.exp > 0) {
						int m, scale;
						IO.msg_print("You feel your memories fade.");
						/* Lose between 1/5 and 2/5 of your experience */
						m = Player.py.misc.exp / 5;
						if (Player.py.misc.exp > Constants.MAX_SHORT) {
							scale = (int)(Constants.MAX_LONG / Player.py.misc.exp);
							m += (Misc1.randint(scale) * Player.py.misc.exp) / (scale * 5);
						} else {
							m += Misc1.randint(Player.py.misc.exp) / 5;
						}
						Spells.lose_exp(m);
						ident = true;
					}
					break;
				case 35:
					f_ptr = Player.py.flags;
					Spells.cure_poison();
					if (f_ptr.food > 150)  f_ptr.food = 150;
					f_ptr.paralysis = 4;
					IO.msg_print("The potion makes you vomit!");
					ident = true;
					break;
				case 36:
					if (Player.py.flags.invuln == 0) {
						ident = true;
					}
					Player.py.flags.invuln += Misc1.randint(10) + 10;
					break;
				case 37:
					if (Player.py.flags.hero == 0) {
						ident = true;
					}
					Player.py.flags.hero += Misc1.randint(25) + 25;
					break;
				case 38:
					if (Player.py.flags.shero == 0) {
						ident = true;
					}
					Player.py.flags.shero += Misc1.randint(25) + 25;
					break;
				case 39:
					ident = Spells.remove_fear();
					break;
				case 40:
					ident = Spells.restore_level();
					break;
				case 41:
					f_ptr = Player.py.flags;
					if (f_ptr.resist_heat == 0) {
						ident = true;
					}
					f_ptr.resist_heat += Misc1.randint(10) + 10;
					break;
				case 42:
					f_ptr = Player.py.flags;
					if (f_ptr.resist_cold == 0) {
						ident = true;
					}
					f_ptr.resist_cold += Misc1.randint(10) + 10;
					break;
				case 43:
					if (Player.py.flags.detect_inv == 0) {
						ident = true;
					}
					Spells.detect_inv2(Misc1.randint(12) + 12);
					break;
				case 44:
					ident = Spells.slow_poison();
					break;
				case 45:
					ident = Spells.cure_poison();
					break;
				case 46:
					m_ptr = Player.py.misc;
					if (m_ptr.cmana < m_ptr.mana) {
						m_ptr.cmana = m_ptr.mana;
						ident = true;
						IO.msg_print("Your feel your head clear.");
						Misc3.prt_cmana();
					}
					break;
				case 47:
					f_ptr = Player.py.flags;
					if (f_ptr.tim_infra == 0) {
						IO.msg_print("Your eyes begin to tingle.");
						ident = true;
					}
					f_ptr.tim_infra += 100 + Misc1.randint(100);
					break;
				default:
					IO.msg_print("Internal error in potion()");
					break;
				}
				/* End of Potions.					*/
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
			
			Misc1.add_food(i_ptr.p1);
			Desc.desc_remain(item_val.value());
			Misc3.inven_destroy(item_val.value());
		}
	}
}
