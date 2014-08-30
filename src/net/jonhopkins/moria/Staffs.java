/**
 * Staffs.java: staff code
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

import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.PlayerMisc;

public class Staffs {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Misc4 m4;
	private Moria1 mor1;
	private Player py;
	private Spells spells;
	private Treasure t;
	private Variable var;
	
	private static Staffs instance;
	private Staffs() { }
	public static Staffs getInstance() {
		if (instance == null) {
			instance = new Staffs();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		m4 = Misc4.getInstance();
		mor1 = Moria1.getInstance();
		py = Player.getInstance();
		spells = Spells.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Use a staff.					-RAK-	*/
	public void use() {
		LongPointer i = new LongPointer();
		IntPointer j = new IntPointer(), k = new IntPointer();
		int chance;
		IntPointer y, x;
		IntPointer item_val = new IntPointer();
		boolean ident;
		PlayerMisc m_ptr;
		InvenType i_ptr;
		
		var.free_turn_flag = true;
		if (t.inven_ctr == 0) {
			io.msg_print("But you are not carrying anything.");
		} else if (!m3.find_range(Constants.TV_STAFF, Constants.TV_NEVER, j, k)) {
			io.msg_print("You are not carrying any staffs.");
		} else if (mor1.get_item(item_val, "Use which staff?", j.value(), k.value(), "", "")) {
			i_ptr = t.inventory[item_val.value()];
			var.free_turn_flag = false;
			m_ptr = py.py.misc;
			chance = m_ptr.save + m3.stat_adj(Constants.A_INT) - i_ptr.level - 5 + (py.class_level_adj[m_ptr.pclass][Constants.CLA_DEVICE] * m_ptr.lev / 3);
			if (py.py.flags.confused > 0) {
				chance = chance / 2;
			}
			if ((chance < Constants.USE_DEVICE) && (m1.randint(Constants.USE_DEVICE - chance + 1) == 1)) {
				chance = Constants.USE_DEVICE; /* Give everyone a slight chance */
			}
			if (chance <= 0)	chance = 1;
			if (m1.randint(chance) < Constants.USE_DEVICE) {
				io.msg_print("You failed to use the staff properly.");
			} else if (i_ptr.p1 > 0) {
				i.value(i_ptr.flags);
				ident = false;
				(i_ptr.p1)--;
				while (i.value() != 0) {
					j.value(m1.bit_pos(i) + 1);
					/* Staffs.				*/
					switch(j.value())
					{
					case 1:
						ident = spells.light_area(py.char_row, py.char_col);
						break;
					case 2:
						ident = spells.detect_sdoor();
						break;
					case 3:
						ident = spells.detect_trap();
						break;
					case 4:
						ident = spells.detect_treasure();
						break;
					case 5:
						ident = spells.detect_object();
						break;
					case 6:
						m3.teleport(100);
						ident = true;
						break;
					case 7:
						ident = true;
						spells.earthquake();
						break;
					case 8:
						ident = false;
						for (int k1 = 0; k1 < m1.randint(4); k1++) {
							y = new IntPointer(py.char_row);
							x = new IntPointer(py.char_col);
							ident |= m1.summon_monster(y, x, false);
						}
						break;
					case 10:
						ident = true;
						spells.destroy_area(py.char_row, py.char_col);
						break;
					case 11:
						ident = true;
						spells.starlite(py.char_row, py.char_col);
						break;
					case 12:
						ident = spells.speed_monsters(1);
						break;
					case 13:
						ident = spells.speed_monsters(-1);
						break;
					case 14:
						ident = spells.sleep_monsters2();
						break;
					case 15:
						ident = spells.hp_player(m1.randint(8));
						break;
					case 16:
						ident = spells.detect_invisible();
						break;
					case 17:
						if (py.py.flags.fast == 0) {
							ident = true;
						}
						py.py.flags.fast += m1.randint(30) + 15;
						break;
					case 18:
						if (py.py.flags.slow == 0) {
							ident = true;
						}
						py.py.flags.slow += m1.randint(30) + 15;
						break;
					case 19:
						ident = spells.mass_poly();
						break;
					case 20:
						if (spells.remove_curse()) {
							if (py.py.flags.blind < 1) {
								io.msg_print("The staff glows blue for a moment..");
							}
							ident = true;
						}
						break;
					case 21:
						ident = spells.detect_evil();
						break;
					case 22:
						if ((spells.cure_blindness()) || (spells.cure_poison()) || (spells.cure_confusion())) {
							ident = true;
						}
						break;
					case 23:
						ident = spells.dispel_creature(Constants.CD_EVIL, 60);
						break;
					case 25:
						ident = spells.unlight_area(py.char_row, py.char_col);
						break;
					case 32:
						/* store bought flag */
						break;
					default:
						io.msg_print("Internal error in staffs()");
						break;
					}
					/* End of staff actions.		*/
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
				desc.desc_charges(item_val.value());
			} else {
				io.msg_print("The staff has no charges left.");
				if (!desc.known2_p(i_ptr)) {
					m4.add_inscribe(i_ptr, Constants.ID_EMPTY);
				}
			}
		}
	}
}
