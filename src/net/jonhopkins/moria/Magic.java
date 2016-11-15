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
	
	private Magic() { }
	
	/* Throw a magic spell					-RAK-	*/
	public static void cast() {
		IntPointer i = new IntPointer(), j = new IntPointer(), item_val = new IntPointer(), dir = new IntPointer();
		IntPointer choice = new IntPointer(), chance = new IntPointer();
		int result;
		PlayerFlags f_ptr;
		PlayerMisc p_ptr;
		InvenType i_ptr;
		SpellType m_ptr;
		
		Variable.free_turn_flag = true;
		if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see to read your spell book!");
		} else if (Moria1.playerHasNoLight()) {
			IO.printMessage("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused.");
		} else if (Player.Class[Player.py.misc.pclass].spell != Constants.MAGE) {
			IO.printMessage("You can't cast spells!");
		} else if (!Misc3.findRange(Constants.TV_MAGIC_BOOK, Constants.TV_NEVER, i, j)) {
			IO.printMessage("But you are not carrying any spell-books!");
		} else if (Moria1.getItemId(item_val, "Use which spell-book?", i.value(), j.value(), "", "")) {
			result = Moria3.castSpell("Cast which spell?", item_val.value(), choice, chance);
			if (result < 0) {
				IO.printMessage("You don't know any spells in that book.");
			} else if (result > 0) {
				m_ptr = Player.magic_spell[Player.py.misc.pclass - 1][choice.value()];
				Variable.free_turn_flag = false;
				
				if (Misc1.randomInt(100) < chance.value()) {
					IO.printMessage("You failed to get the spell off!");
				} else {
					/* Spells.  */
					switch(choice.value() + 1)
					{
					case 1:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBolt(Constants.GF_MAGIC_MISSILE, dir.value(), Player.char_row, Player.char_col, Misc1.damageRoll(2, 6), Player.spell_names[0]);
						}
						break;
					case 2:
						Spells.detectMonsters();
						break;
					case 3:
						Misc3.teleport(10);
						break;
					case 4:
						Spells.lightArea(Player.char_row, Player.char_col);
						break;
					case 5:
						Spells.changePlayerHitpoints(Misc1.damageRoll(4, 4));
						break;
					case 6:
						Spells.detectSecretDoors();
						Spells.detectTrap();
						break;
					case 7:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBall(Constants.GF_POISON_GAS, dir.value(), Player.char_row, Player.char_col, 12, Player.spell_names[6]);
						}
						break;
					case 8:
						if (Moria1.getDirection("", dir)) {
							Spells.confuseMonster(dir.value(), Player.char_row, Player.char_col);
						}
						break;
					case 9:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBolt(Constants.GF_LIGHTNING, dir.value(), Player.char_row, Player.char_col, Misc1.damageRoll(4, 8), Player.spell_names[8]);
						}
						break;
					case 10:
						Spells.destroyTrapsAndDoors();
						break;
					case 11:
						if (Moria1.getDirection("", dir)) {
							Spells.sleepMonster(dir.value(), Player.char_row, Player.char_col);
						}
						break;
					case 12:
						Spells.curePoison();
						break;
					case 13:
						Misc3.teleport((Player.py.misc.lev * 5));
						break;
					case 14:
						for (i.value(22); i.value() < Constants.INVEN_ARRAY_SIZE; i.value(i.value() + 1)) {
							i_ptr = Treasure.inventory[i.value()];
							i_ptr.flags = (i_ptr.flags & ~Constants.TR_CURSED);
						}
						break;
					case 15:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBolt(Constants.GF_FROST, dir.value(), Player.char_row, Player.char_col, Misc1.damageRoll(6, 8), Player.spell_names[14]);
						}
						break;
					case 16:
						if (Moria1.getDirection("", dir)) {
							Spells.transformWallToMud(dir.value(), Player.char_row, Player.char_col);
						}
						break;
					case 17:
						Spells.createFood();
						break;
					case 18:
						Spells.recharge(20);
						break;
					case 19:
						Spells.sleepMonsters(Player.char_row, Player.char_col);
						break;
					case 20:
						if (Moria1.getDirection("", dir)) {
							Spells.polymorphMonster(dir.value(), Player.char_row, Player.char_col);
						}
						break;
					case 21:
						Spells.identifyObject();
						break;
					case 22:
						Spells.sleepMonsters();
						break;
					case 23:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBolt(Constants.GF_FIRE, dir.value(), Player.char_row, Player.char_col, Misc1.damageRoll(9, 8), Player.spell_names[22]);
						}
						break;
					case 24:
						if (Moria1.getDirection("", dir)) {
							Spells.speedMonster(dir.value(), Player.char_row, Player.char_col, -1);
						}
						break;
					case 25:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBall(Constants.GF_FROST, dir.value(), Player.char_row, Player.char_col, 48, Player.spell_names[24]);
						}
						break;
					case 26:
						Spells.recharge(60);
						break;
					case 27:
						if (Moria1.getDirection("", dir)) {
							Spells.teleportMonsters(dir.value(), Player.char_row, Player.char_col);
						}
						break;
					case 28:
						f_ptr = Player.py.flags;
						f_ptr.fast += Misc1.randomInt(20) + Player.py.misc.lev;
						break;
					case 29:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBall(Constants.GF_FIRE, dir.value(), Player.char_row, Player.char_col, 72, Player.spell_names[28]);
						}
						break;
					case 30:
						Spells.destroyArea(Player.char_row, Player.char_col);
						break;
					case 31:
						Spells.genocide();
						break;
					default:
						break;
					}
					/* End of spells.				     */
					if (!Variable.free_turn_flag) {
						p_ptr = Player.py.misc;
						if ((Player.spell_worked & (1L << choice.value())) == 0) {
							p_ptr.exp += m_ptr.sexp << 2;
							Player.spell_worked |= (1L << choice.value());
							Misc3.printExperience();
						}
					}
				}
				p_ptr = Player.py.misc;
				if (!Variable.free_turn_flag) {
					if (m_ptr.smana > p_ptr.cmana) {
						IO.printMessage("You faint from the effort!");
						Player.py.flags.paralysis = Misc1.randomInt((5 * (m_ptr.smana - p_ptr.cmana)));
						p_ptr.cmana = 0;
						p_ptr.cmana_frac = 0;
						if (Misc1.randomInt(3) == 1) {
							IO.printMessage("You have damaged your health!");
							Misc3.decreaseStat(Constants.A_CON);
						}
					} else {
						p_ptr.cmana -= m_ptr.smana;
					}
					Misc3.printCurrentMana();
				}
			}
	    }
	}
}
