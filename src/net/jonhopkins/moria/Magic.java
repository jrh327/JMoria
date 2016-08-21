/*
 * Magic.java: code for mage spells
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
import net.jonhopkins.moria.types.SpellType;

public class Magic {
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Moria1 mor1;
	private Moria3 mor3;
	private Player py;
	private Spells spells;
	private Treasure t;
	private Variable var;
	
	private static Magic instance;
	private Magic() { }
	public static Magic getInstance() {
		if (instance == null) {
			instance = new Magic();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		mor1 = Moria1.getInstance();
		mor3 = Moria3.getInstance();
		py = Player.getInstance();
		spells = Spells.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Throw a magic spell					-RAK-	*/
	public void cast() {
		IntPointer i = new IntPointer(), j = new IntPointer(), item_val = new IntPointer(), dir = new IntPointer();
		IntPointer choice = new IntPointer(), chance = new IntPointer();
		int result;
		PlayerFlags f_ptr;
		PlayerMisc p_ptr;
		InvenType i_ptr;
		SpellType m_ptr;
		
		var.free_turn_flag = true;
		if (py.py.flags.blind > 0) {
			io.msg_print("You can't see to read your spell book!");
		} else if (mor1.no_light()) {
			io.msg_print("You have no light to read by.");
		} else if (py.py.flags.confused > 0) {
			io.msg_print("You are too confused.");
		} else if (py.Class[py.py.misc.pclass].spell != Constants.MAGE) {
			io.msg_print("You can't cast spells!");
		} else if (!m3.find_range(Constants.TV_MAGIC_BOOK, Constants.TV_NEVER, i, j)) {
			io.msg_print("But you are not carrying any spell-books!");
		} else if (mor1.get_item(item_val, "Use which spell-book?", i.value(), j.value(), "", "")) {
			result = mor3.cast_spell("Cast which spell?", item_val.value(), choice, chance);
			if (result < 0) {
				io.msg_print("You don't know any spells in that book.");
			} else if (result > 0) {
				m_ptr = py.magic_spell[py.py.misc.pclass - 1][choice.value()];
				var.free_turn_flag = false;
				
				if (m1.randint(100) < chance.value()) {
					io.msg_print("You failed to get the spell off!");
				} else {
					/* Spells.  */
					switch(choice.value() + 1)
					{
					case 1:
						if (mor1.get_dir("", dir)) {
							spells.fire_bolt(Constants.GF_MAGIC_MISSILE, dir.value(), py.char_row, py.char_col, m1.damroll(2, 6), py.spell_names[0]);
						}
						break;
					case 2:
						spells.detect_monsters();
						break;
					case 3:
						m3.teleport(10);
						break;
					case 4:
						spells.light_area(py.char_row, py.char_col);
						break;
					case 5:
						spells.hp_player(m1.damroll(4, 4));
						break;
					case 6:
						spells.detect_sdoor();
						spells.detect_trap();
						break;
					case 7:
						if (mor1.get_dir("", dir)) {
							spells.fire_ball(Constants.GF_POISON_GAS, dir.value(), py.char_row, py.char_col, 12, py.spell_names[6]);
						}
						break;
					case 8:
						if (mor1.get_dir("", dir)) {
							spells.confuse_monster(dir.value(), py.char_row, py.char_col);
						}
						break;
					case 9:
						if (mor1.get_dir("", dir)) {
							spells.fire_bolt(Constants.GF_LIGHTNING, dir.value(), py.char_row, py.char_col, m1.damroll(4, 8), py.spell_names[8]);
						}
						break;
					case 10:
						spells.td_destroy();
						break;
					case 11:
						if (mor1.get_dir("", dir)) {
							spells.sleep_monster(dir.value(), py.char_row, py.char_col);
						}
						break;
					case 12:
						spells.cure_poison();
						break;
					case 13:
						m3.teleport((py.py.misc.lev * 5));
						break;
					case 14:
						for (i.value(22); i.value() < Constants.INVEN_ARRAY_SIZE; i.value(i.value() + 1)) {
							i_ptr = t.inventory[i.value()];
							i_ptr.flags = (i_ptr.flags & ~Constants.TR_CURSED);
						}
						break;
					case 15:
						if (mor1.get_dir("", dir)) {
							spells.fire_bolt(Constants.GF_FROST, dir.value(), py.char_row, py.char_col, m1.damroll(6, 8), py.spell_names[14]);
						}
						break;
					case 16:
						if (mor1.get_dir("", dir))
							spells.wall_to_mud(dir.value(), py.char_row, py.char_col);
						break;
					case 17:
						spells.create_food();
						break;
					case 18:
						spells.recharge(20);
						break;
					case 19:
						spells.sleep_monsters1(py.char_row, py.char_col);
						break;
					case 20:
						if (mor1.get_dir("", dir)) {
							spells.poly_monster(dir.value(), py.char_row, py.char_col);
						}
						break;
					case 21:
						spells.ident_spell();
						break;
					case 22:
						spells.sleep_monsters2();
						break;
					case 23:
						if (mor1.get_dir("", dir)) {
							spells.fire_bolt(Constants.GF_FIRE, dir.value(), py.char_row, py.char_col, m1.damroll(9, 8), py.spell_names[22]);
						}
						break;
					case 24:
						if (mor1.get_dir("", dir)) {
							spells.speed_monster(dir.value(), py.char_row, py.char_col, -1);
						}
						break;
					case 25:
						if (mor1.get_dir("", dir)) {
							spells.fire_ball(Constants.GF_FROST, dir.value(), py.char_row, py.char_col, 48, py.spell_names[24]);
						}
						break;
					case 26:
						spells.recharge(60);
						break;
					case 27:
						if (mor1.get_dir("", dir)) {
							spells.teleport_monster(dir.value(), py.char_row, py.char_col);
						}
						break;
					case 28:
						f_ptr = py.py.flags;
						f_ptr.fast += m1.randint(20) + py.py.misc.lev;
						break;
					case 29:
						if (mor1.get_dir("", dir)) {
							spells.fire_ball(Constants.GF_FIRE, dir.value(), py.char_row, py.char_col, 72, py.spell_names[28]);
						}
						break;
					case 30:
						spells.destroy_area(py.char_row, py.char_col);
						break;
					case 31:
						spells.genocide();
						break;
					default:
						break;
					}
					/* End of spells.				     */
					if (var.free_turn_flag == false) {
						p_ptr = py.py.misc;
						if ((py.spell_worked & (1L << choice.value())) == 0) {
							p_ptr.exp += m_ptr.sexp << 2;
							py.spell_worked |= (1L << choice.value());
							m3.prt_experience();
						}
					}
				}
				p_ptr = py.py.misc;
				if (!var.free_turn_flag) {
					if (m_ptr.smana > p_ptr.cmana) {
						io.msg_print("You faint from the effort!");
						py.py.flags.paralysis = m1.randint((5 * (m_ptr.smana - p_ptr.cmana)));
						p_ptr.cmana = 0;
						p_ptr.cmana_frac = 0;
						if (m1.randint(3) == 1) {
							io.msg_print("You have damaged your health!");
							m3.dec_stat(Constants.A_CON);
						}
					} else {
						p_ptr.cmana -= m_ptr.smana;
					}
					m3.prt_cmana();
				}
			}
	    }
	}
}
