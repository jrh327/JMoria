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
			IO.printMessage("You can't see to read your prayer!");
		} else if (Moria1.playerHasNoLight()) {
			IO.printMessage("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused.");
		} else if (Player.Class[Player.py.misc.pclass].spell != Constants.PRIEST) {
			IO.printMessage("Pray hard enough and your prayers may be answered.");
		} else if (Treasure.inven_ctr == 0) {
			IO.printMessage("But you are not carrying anything!");
		} else if (!Misc3.findRange(Constants.TV_PRAYER_BOOK, Constants.TV_NEVER, i, j)) {
			IO.printMessage("You are not carrying any Holy Books!");
		} else if (Moria1.getItemId(item_val, "Use which Holy Book?", i.value(), j.value(), "", "")) {
			result = Moria3.castSpell("Recite which prayer?", item_val.value(), choice, chance);
			if (result < 0) {
				IO.printMessage("You don't know any prayers in that book.");
			} else if (result > 0) {
				s_ptr = Player.magic_spell[Player.py.misc.pclass - 1][choice.value()];
				Variable.free_turn_flag = false;
				
				if (Misc1.randomInt(100) < chance.value()) {
					IO.printMessage("You lost your concentration!");
				} else {
					/* Prayers.					*/
					switch(choice.value() + 1)
					{
					case 1:
						Spells.detectEvil();
						break;
					case 2:
						Spells.changePlayerHitpoints(Misc1.damageRoll(3, 3));
						break;
					case 3:
						Spells.bless(Misc1.randomInt(12) + 12);
						break;
					case 4:
						Spells.removeFear();
						break;
					case 5:
						Spells.lightArea(Player.char_row, Player.char_col);
						break;
					case 6:
						Spells.detectTrap();
						break;
					case 7:
						Spells.detectSecretDoors();
						break;
					case 8:
						Spells.slowPoison();
						break;
					case 9:
						if (Moria1.getDirection("", dir)) {
							Spells.confuseMonster(dir.value(), Player.char_row, Player.char_col);
						}
						break;
					case 10:
						Misc3.teleport((Player.py.misc.lev * 3));
						break;
					case 11:
						Spells.changePlayerHitpoints(Misc1.damageRoll(4, 4));
						break;
					case 12:
						Spells.bless(Misc1.randomInt(24) + 24);
						break;
					case 13:
						Spells.sleepMonsters(Player.char_row, Player.char_col);
						break;
					case 14:
						Spells.createFood();
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
						f_ptr.resist_heat += Misc1.randomInt(10) + 10;
						f_ptr.resist_cold += Misc1.randomInt(10) + 10;
						break;
					case 17:
						Spells.curePoison();
						break;
					case 18:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBall(Constants.GF_HOLY_ORB, dir.value(), Player.char_row, Player.char_col, (Misc1.damageRoll(3, 6) + Player.py.misc.lev), "Black Sphere");
						}
						break;
					case 19:
						Spells.changePlayerHitpoints(Misc1.damageRoll(8, 4));
						break;
					case 20:
						Spells.detectInvisibleMonsters(Misc1.randomInt(24) + 24);
						break;
					case 21:
						Spells.protectFromEvil();
						break;
					case 22:
						Spells.earthquake();
						break;
					case 23:
						Spells.mapArea();
						break;
					case 24:
						Spells.changePlayerHitpoints(Misc1.damageRoll(16, 4));
						break;
					case 25:
						Spells.turnUndead();
						break;
					case 26:
						Spells.bless(Misc1.randomInt(48) + 48);
						break;
					case 27:
						Spells.dispelCreature(Constants.CD_UNDEAD, (3 * Player.py.misc.lev));
						break;
					case 28:
						Spells.changePlayerHitpoints(200);
						break;
					case 29:
						Spells.dispelCreature(Constants.CD_EVIL, (3 * Player.py.misc.lev));
						break;
					case 30:
						Spells.wardingGlyph();
						break;
					case 31:
						Spells.removeFear();
						Spells.curePoison();
						Spells.changePlayerHitpoints(1000);
						for (i.value(Constants.A_STR);
								i.value() <= Constants.A_CHR;
								i.value(i.value() + 1)) {
							Misc3.restoreStat(i.value());
						}
						Spells.dispelCreature(Constants.CD_EVIL, 4 * Player.py.misc.lev);
						Spells.turnUndead();
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
							Misc3.printExperience();
							Player.spell_worked |= (1L << choice.value());
						}
					}
				}
				m_ptr = Player.py.misc;
				if (!Variable.free_turn_flag) {
					if (s_ptr.smana > m_ptr.cmana) {
						IO.printMessage("You faint from fatigue!");
						Player.py.flags.paralysis = Misc1.randomInt((5 * (s_ptr.smana - m_ptr.cmana)));
						m_ptr.cmana = 0;
						m_ptr.cmana_frac = 0;
						if (Misc1.randomInt(3) == 1) {
							IO.printMessage("You have damaged your health!");
							Misc3.decreaseStat(Constants.A_CON);
						}
					} else {
						m_ptr.cmana -= s_ptr.smana;
					}
					Misc3.printCurrentMana();
				}
			}
		}
	}
}
