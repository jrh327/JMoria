/*
 * Prayer.java: code for priest spells
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

public class Prayer {
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Moria1 mor1;
	private Moria3 mor3;
	private Player py;
	private Spells spells;
	private Treasure t;
	private Variable var;
	
	private static Prayer instance;
	private Prayer() { }
	public static Prayer getInstance() {
		if (instance == null) {
			instance = new Prayer();
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
	
	/* Pray like HELL.					-RAK-	*/
	public void pray() {
		IntPointer i = new IntPointer(), j = new IntPointer();
		IntPointer item_val = new IntPointer(), dir = new IntPointer();
		IntPointer choice = new IntPointer(), chance = new IntPointer();
		int result;
		SpellType s_ptr;
		PlayerMisc m_ptr;
		PlayerFlags f_ptr;
		InvenType i_ptr;
		
		var.free_turn_flag = true;
		if (py.py.flags.blind > 0) {
			io.msg_print("You can't see to read your prayer!");
		} else if (mor1.no_light()) {
			io.msg_print("You have no light to read by.");
		} else if (py.py.flags.confused > 0) {
			io.msg_print("You are too confused.");
		} else if (py.Class[py.py.misc.pclass].spell != Constants.PRIEST) {
			io.msg_print("Pray hard enough and your prayers may be answered.");
		} else if (t.inven_ctr == 0) {
			io.msg_print("But you are not carrying anything!");
		} else if (!m3.find_range(Constants.TV_PRAYER_BOOK, Constants.TV_NEVER, i, j)) {
			io.msg_print("You are not carrying any Holy Books!");
		} else if (mor1.get_item(item_val, "Use which Holy Book?", i.value(), j.value(), "", "")) {
			result = mor3.cast_spell("Recite which prayer?", item_val.value(), choice, chance);
			if (result < 0) {
				io.msg_print("You don't know any prayers in that book.");
			} else if (result > 0) {
				s_ptr = py.magic_spell[py.py.misc.pclass - 1][choice.value()];
				var.free_turn_flag = false;
				
				if (m1.randint(100) < chance.value()) {
					io.msg_print("You lost your concentration!");
				} else {
					/* Prayers.					*/
					switch(choice.value() + 1)
					{
					case 1:
						spells.detect_evil();
						break;
					case 2:
						spells.hp_player(m1.damroll(3, 3));
						break;
					case 3:
						spells.bless(m1.randint(12) + 12);
						break;
					case 4:
						spells.remove_fear();
						break;
					case 5:
						spells.light_area(py.char_row, py.char_col);
						break;
					case 6:
						spells.detect_trap();
						break;
					case 7:
						spells.detect_sdoor();
						break;
					case 8:
						spells.slow_poison();
						break;
					case 9:
						if (mor1.get_dir("", dir)) {
							spells.confuse_monster(dir.value(), py.char_row, py.char_col);
						}
						break;
					case 10:
						m3.teleport((py.py.misc.lev * 3));
						break;
					case 11:
						spells.hp_player(m1.damroll(4, 4));
						break;
					case 12:
						spells.bless(m1.randint(24) + 24);
						break;
					case 13:
						spells.sleep_monsters1(py.char_row, py.char_col);
						break;
					case 14:
						spells.create_food();
						break;
					case 15:
						for (i.value(0); i.value() < Constants.INVEN_ARRAY_SIZE; i.value(i.value() + 1)) {
							i_ptr = t.inventory[i.value()];
							/* only clear flag for items that are wielded or worn */
							if (i_ptr.tval >= Constants.TV_MIN_WEAR && i_ptr.tval <= Constants.TV_MAX_WEAR) {
								i_ptr.flags &= ~Constants.TR_CURSED;
							}
						}
						break;
					case 16:
						f_ptr = py.py.flags;
						f_ptr.resist_heat += m1.randint(10) + 10;
						f_ptr.resist_cold += m1.randint(10) + 10;
						break;
					case 17:
						spells.cure_poison();
						break;
					case 18:
						if (mor1.get_dir("", dir))
							spells.fire_ball(Constants.GF_HOLY_ORB, dir.value(), py.char_row, py.char_col, (m1.damroll(3, 6) + py.py.misc.lev), "Black Sphere");
						break;
					case 19:
						spells.hp_player(m1.damroll(8, 4));
						break;
					case 20:
						spells.detect_inv2(m1.randint(24) + 24);
						break;
					case 21:
						spells.protect_evil();
						break;
					case 22:
						spells.earthquake();
						break;
					case 23:
						spells.map_area();
						break;
					case 24:
						spells.hp_player(m1.damroll(16, 4));
						break;
					case 25:
						spells.turn_undead();
						break;
					case 26:
						spells.bless(m1.randint(48) + 48);
						break;
					case 27:
						spells.dispel_creature(Constants.CD_UNDEAD, (3 * py.py.misc.lev));
						break;
					case 28:
						spells.hp_player(200);
						break;
					case 29:
						spells.dispel_creature(Constants.CD_EVIL, (3 * py.py.misc.lev));
						break;
					case 30:
						spells.warding_glyph();
						break;
					case 31:
						spells.remove_fear();
						spells.cure_poison();
						spells.hp_player(1000);
						for (i.value(Constants.A_STR);
								i.value() <= Constants.A_CHR;
								i.value(i.value() + 1)) {
							m3.res_stat(i.value());
						}
						spells.dispel_creature(Constants.CD_EVIL, (int)(4 * py.py.misc.lev));
						spells.turn_undead();
						if (py.py.flags.invuln < 3) {
							py.py.flags.invuln = 3;
						} else {
							py.py.flags.invuln++;
						}
						break;
					default:
						break;
					}
					/* End of prayers.				*/
					if (!var.free_turn_flag) {
						m_ptr = py.py.misc;
						if ((py.spell_worked & (1L << choice.value())) == 0) {
							m_ptr.exp += s_ptr.sexp << 2;
							m3.prt_experience();
							py.spell_worked |= (1L << choice.value());
						}
					}
				}
				m_ptr = py.py.misc;
				if (!var.free_turn_flag) {
					if (s_ptr.smana > m_ptr.cmana) {
						io.msg_print("You faint from fatigue!");
						py.py.flags.paralysis = m1.randint((5 * (s_ptr.smana - m_ptr.cmana)));
						m_ptr.cmana = 0;
						m_ptr.cmana_frac = 0;
						if (m1.randint(3) == 1) {
							io.msg_print("You have damaged your health!");
							m3.dec_stat(Constants.A_CON);
						}
					} else {
						m_ptr.cmana -= s_ptr.smana;
					}
					m3.prt_cmana();
				}
			}
		}
	}
}
