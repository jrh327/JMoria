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
	
	private Prayer() { }
	
	/* Pray like HELL.					-RAK-	*/
	public static void pray() {
		IntPointer i = new IntPointer(), j = new IntPointer();
		IntPointer item_val = new IntPointer(), dir = new IntPointer();
		IntPointer choice = new IntPointer(), chance = new IntPointer();
		int result;
		SpellType s_ptr;
		PlayerMisc m_ptr;
		PlayerFlags f_ptr;
		InvenType i_ptr;
		
		Variable.free_turn_flag = true;
		if (Player.py.flags.blind > 0) {
			IO.msg_print("You can't see to read your prayer!");
		} else if (Moria1.no_light()) {
			IO.msg_print("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.msg_print("You are too confused.");
		} else if (Player.Class[Player.py.misc.pclass].spell != Constants.PRIEST) {
			IO.msg_print("Pray hard enough and your prayers may be answered.");
		} else if (Treasure.inven_ctr == 0) {
			IO.msg_print("But you are not carrying anything!");
		} else if (!Misc3.find_range(Constants.TV_PRAYER_BOOK, Constants.TV_NEVER, i, j)) {
			IO.msg_print("You are not carrying any Holy Books!");
		} else if (Moria1.get_item(item_val, "Use which Holy Book?", i.value(), j.value(), "", "")) {
			result = Moria3.cast_spell("Recite which prayer?", item_val.value(), choice, chance);
			if (result < 0) {
				IO.msg_print("You don't know any prayers in that book.");
			} else if (result > 0) {
				s_ptr = Player.magic_spell[Player.py.misc.pclass - 1][choice.value()];
				Variable.free_turn_flag = false;
				
				if (Misc1.randint(100) < chance.value()) {
					IO.msg_print("You lost your concentration!");
				} else {
					/* Prayers.					*/
					switch(choice.value() + 1)
					{
					case 1:
						Spells.detect_evil();
						break;
					case 2:
						Spells.hp_player(Misc1.damroll(3, 3));
						break;
					case 3:
						Spells.bless(Misc1.randint(12) + 12);
						break;
					case 4:
						Spells.remove_fear();
						break;
					case 5:
						Spells.light_area(Player.char_row, Player.char_col);
						break;
					case 6:
						Spells.detect_trap();
						break;
					case 7:
						Spells.detect_sdoor();
						break;
					case 8:
						Spells.slow_poison();
						break;
					case 9:
						if (Moria1.get_dir("", dir)) {
							Spells.confuse_monster(dir.value(), Player.char_row, Player.char_col);
						}
						break;
					case 10:
						Misc3.teleport((Player.py.misc.lev * 3));
						break;
					case 11:
						Spells.hp_player(Misc1.damroll(4, 4));
						break;
					case 12:
						Spells.bless(Misc1.randint(24) + 24);
						break;
					case 13:
						Spells.sleep_monsters1(Player.char_row, Player.char_col);
						break;
					case 14:
						Spells.create_food();
						break;
					case 15:
						for (i.value(0); i.value() < Constants.INVEN_ARRAY_SIZE; i.value(i.value() + 1)) {
							i_ptr = Treasure.inventory[i.value()];
							/* only clear flag for items that are wielded or worn */
							if (i_ptr.tval >= Constants.TV_MIN_WEAR && i_ptr.tval <= Constants.TV_MAX_WEAR) {
								i_ptr.flags &= ~Constants.TR_CURSED;
							}
						}
						break;
					case 16:
						f_ptr = Player.py.flags;
						f_ptr.resist_heat += Misc1.randint(10) + 10;
						f_ptr.resist_cold += Misc1.randint(10) + 10;
						break;
					case 17:
						Spells.cure_poison();
						break;
					case 18:
						if (Moria1.get_dir("", dir))
							Spells.fire_ball(Constants.GF_HOLY_ORB, dir.value(), Player.char_row, Player.char_col, (Misc1.damroll(3, 6) + Player.py.misc.lev), "Black Sphere");
						break;
					case 19:
						Spells.hp_player(Misc1.damroll(8, 4));
						break;
					case 20:
						Spells.detect_inv2(Misc1.randint(24) + 24);
						break;
					case 21:
						Spells.protect_evil();
						break;
					case 22:
						Spells.earthquake();
						break;
					case 23:
						Spells.map_area();
						break;
					case 24:
						Spells.hp_player(Misc1.damroll(16, 4));
						break;
					case 25:
						Spells.turn_undead();
						break;
					case 26:
						Spells.bless(Misc1.randint(48) + 48);
						break;
					case 27:
						Spells.dispel_creature(Constants.CD_UNDEAD, (3 * Player.py.misc.lev));
						break;
					case 28:
						Spells.hp_player(200);
						break;
					case 29:
						Spells.dispel_creature(Constants.CD_EVIL, (3 * Player.py.misc.lev));
						break;
					case 30:
						Spells.warding_glyph();
						break;
					case 31:
						Spells.remove_fear();
						Spells.cure_poison();
						Spells.hp_player(1000);
						for (i.value(Constants.A_STR);
								i.value() <= Constants.A_CHR;
								i.value(i.value() + 1)) {
							Misc3.res_stat(i.value());
						}
						Spells.dispel_creature(Constants.CD_EVIL, (int)(4 * Player.py.misc.lev));
						Spells.turn_undead();
						if (Player.py.flags.invuln < 3) {
							Player.py.flags.invuln = 3;
						} else {
							Player.py.flags.invuln++;
						}
						break;
					default:
						break;
					}
					/* End of prayers.				*/
					if (!Variable.free_turn_flag) {
						m_ptr = Player.py.misc;
						if ((Player.spell_worked & (1L << choice.value())) == 0) {
							m_ptr.exp += s_ptr.sexp << 2;
							Misc3.prt_experience();
							Player.spell_worked |= (1L << choice.value());
						}
					}
				}
				m_ptr = Player.py.misc;
				if (!Variable.free_turn_flag) {
					if (s_ptr.smana > m_ptr.cmana) {
						IO.msg_print("You faint from fatigue!");
						Player.py.flags.paralysis = Misc1.randint((5 * (s_ptr.smana - m_ptr.cmana)));
						m_ptr.cmana = 0;
						m_ptr.cmana_frac = 0;
						if (Misc1.randint(3) == 1) {
							IO.msg_print("You have damaged your health!");
							Misc3.dec_stat(Constants.A_CON);
						}
					} else {
						m_ptr.cmana -= s_ptr.smana;
					}
					Misc3.prt_cmana();
				}
			}
		}
	}
}
