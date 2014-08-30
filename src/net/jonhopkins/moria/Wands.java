/**
 * Wands.java: wand code
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

public class Wands {
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
	
	private static Wands instance;
	private Wands() { }
	public static Wands getInstance() {
		if (instance == null) {
			instance = new Wands();
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
	
	/* Wands for the aiming.				*/
	public void aim() {
		LongPointer i = new LongPointer();
		int l;
		boolean ident;
		IntPointer item_val = new IntPointer(), dir = new IntPointer();
		IntPointer j = new IntPointer(), k = new IntPointer();
		int chance;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		
		var.free_turn_flag = true;
		if (t.inven_ctr == 0) {
			io.msg_print("But you are not carrying anything.");
		} else if (!m3.find_range(Constants.TV_WAND, Constants.TV_NEVER, j, k)) {
			io.msg_print("You are not carrying any wands.");
		} else if (mor1.get_item(item_val, "Aim which wand?", j.value(), k.value(), "", "")) {
			i_ptr = t.inventory[item_val.value()];
			var.free_turn_flag = false;
			if (mor1.get_dir("", dir)) {
				if (py.py.flags.confused > 0) {
					io.msg_print("You are confused.");
					do {
						dir.value(m1.randint(9));
					} while (dir.value() == 5);
				}
				ident = false;
				m_ptr = py.py.misc;
				chance = m_ptr.save + m3.stat_adj(Constants.A_INT) - i_ptr.level + (py.class_level_adj[m_ptr.pclass][Constants.CLA_DEVICE] * m_ptr.lev / 3);
				if (py.py.flags.confused > 0) {
					chance = chance / 2;
				}
				if ((chance < Constants.USE_DEVICE) && (m1.randint(Constants.USE_DEVICE - chance + 1) == 1)) {
					chance = Constants.USE_DEVICE; /* Give everyone a slight chance */
				}
				if (chance <= 0)	chance = 1;
				if (m1.randint(chance) < Constants.USE_DEVICE) {
					io.msg_print("You failed to use the wand properly.");
				} else if (i_ptr.p1 > 0) {
					i.value(i_ptr.flags);
					i_ptr.p1--;
					while (i.value() != 0) {
						j.value(m1.bit_pos(i) + 1);
						k.value(py.char_row);
						l = py.char_col;
						/* Wands			 */
						switch(j.value())
						{
						case 1:
							io.msg_print("A line of blue shimmering light appears.");
							spells.light_line(dir.value(), py.char_row, py.char_col);
							ident = true;
							break;
						case 2:
							spells.fire_bolt(Constants.GF_LIGHTNING, dir.value(), k.value(), l, m1.damroll(4, 8), py.spell_names[8]);
							ident = true;
							break;
						case 3:
							spells.fire_bolt(Constants.GF_FROST, dir.value(), k.value(), l, m1.damroll(6, 8), py.spell_names[14]);
							ident = true;
							break;
						case 4:
							spells.fire_bolt(Constants.GF_FIRE, dir.value(), k.value(), l, m1.damroll(9, 8), py.spell_names[22]);
							ident = true;
							break;
						case 5:
							ident = spells.wall_to_mud(dir.value(), k.value(), l);
							break;
						case 6:
							ident = spells.poly_monster(dir.value(), k.value(), l);
							break;
						case 7:
							ident = spells.hp_monster(dir.value(), k.value(), l, -m1.damroll(4, 6));
							break;
						case 8:
							ident = spells.speed_monster(dir.value(), k.value(), l, 1);
							break;
						case 9:
							ident = spells.speed_monster(dir.value(), k.value(), l, -1);
							break;
						case 10:
							ident = spells.confuse_monster(dir.value(), k.value(), l);
							break;
						case 11:
							ident = spells.sleep_monster(dir.value(), k.value(), l);
							break;
						case 12:
							ident = spells.drain_life(dir.value(), k.value(), l);
							break;
						case 13:
							ident = spells.td_destroy2(dir.value(), k.value(), l);
							break;
						case 14:
							spells.fire_bolt(Constants.GF_MAGIC_MISSILE, dir.value(), k.value(), l, m1.damroll(2, 6), py.spell_names[0]);
							ident = true;
							break;
						case 15:
							ident = spells.build_wall(dir.value(), k.value(), l);
							break;
						case 16:
							ident = spells.clone_monster(dir.value(), k.value(), l);
							break;
						case 17:
							ident = spells.teleport_monster(dir.value(), k.value(), l);
							break;
						case 18:
							ident = spells.disarm_all(dir.value(), k.value(), l);
							break;
						case 19:
							spells.fire_ball(Constants.GF_LIGHTNING, dir.value(), k.value(), l, 32, "Lightning Ball");
							ident = true;
							break;
						case 20:
							spells.fire_ball(Constants.GF_FROST, dir.value(), k.value(), l, 48, "Cold Ball");
							ident = true;
							break;
						case 21:
							spells.fire_ball(Constants.GF_FIRE, dir.value(), k.value(), l, 72, py.spell_names[28]);
							ident = true;
							break;
						case 22:
							spells.fire_ball(Constants.GF_POISON_GAS, dir.value(), k.value(), l, 12, py.spell_names[6]);
							ident = true;
							break;
						case 23:
							spells.fire_ball(Constants.GF_ACID, dir.value(), k.value(), l, 60, "Acid Ball");
							ident = true;
							break;
						case 24:
							i.value(1L << (m1.randint(23) - 1));
							break;
						default:
							io.msg_print("Internal error in wands()");
							break;
						}
						/* End of Wands.		    */
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
					io.msg_print("The wand has no charges left.");
					if (!desc.known2_p(i_ptr)) {
						m4.add_inscribe(i_ptr, Constants.ID_EMPTY);
					}
				}
			}
		}
	}
}
