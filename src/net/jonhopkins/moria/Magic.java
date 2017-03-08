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
		
		Variable.freeTurnFlag = true;
		if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see to read your spell book!");
		} else if (Moria1.playerHasNoLight()) {
			IO.printMessage("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused.");
		} else if (Player.Class[Player.py.misc.playerClass].spell != Constants.MAGE) {
			IO.printMessage("You can't cast spells!");
		} else if (!Misc3.findRange(Constants.TV_MAGIC_BOOK, Constants.TV_NEVER, i, j)) {
			IO.printMessage("But you are not carrying any spell-books!");
		} else if (Moria1.getItemId(item_val, "Use which spell-book?", i.value(), j.value(), "", "")) {
			result = Moria3.castSpell("Cast which spell?", item_val.value(), choice, chance);
			if (result < 0) {
				IO.printMessage("You don't know any spells in that book.");
			} else if (result > 0) {
				m_ptr = Player.magicSpell[Player.py.misc.playerClass - 1][choice.value()];
				Variable.freeTurnFlag = false;
				
				if (Rnd.randomInt(100) < chance.value()) {
					IO.printMessage("You failed to get the spell off!");
				} else {
					/* Spells.  */
					switch(choice.value() + 1)
					{
					case 1:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBolt(Constants.GF_MAGIC_MISSILE, dir.value(), Player.y, Player.x, Misc1.damageRoll(2, 6), Player.spellNames[0]);
						}
						break;
					case 2:
						Spells.detectMonsters();
						break;
					case 3:
						Misc3.teleport(10);
						break;
					case 4:
						Spells.lightArea(Player.y, Player.x);
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
							Spells.fireBall(Constants.GF_POISON_GAS, dir.value(), Player.y, Player.x, 12, Player.spellNames[6]);
						}
						break;
					case 8:
						if (Moria1.getDirection("", dir)) {
							Spells.confuseMonster(dir.value(), Player.y, Player.x);
						}
						break;
					case 9:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBolt(Constants.GF_LIGHTNING, dir.value(), Player.y, Player.x, Misc1.damageRoll(4, 8), Player.spellNames[8]);
						}
						break;
					case 10:
						Spells.destroyTrapsAndDoors();
						break;
					case 11:
						if (Moria1.getDirection("", dir)) {
							Spells.sleepMonster(dir.value(), Player.y, Player.x);
						}
						break;
					case 12:
						Spells.curePoison();
						break;
					case 13:
						Misc3.teleport((Player.py.misc.level * 5));
						break;
					case 14:
						for (i.value(22); i.value() < Constants.INVEN_ARRAY_SIZE; i.value(i.value() + 1)) {
							i_ptr = Treasure.inventory[i.value()];
							i_ptr.flags = (i_ptr.flags & ~Constants.TR_CURSED);
						}
						break;
					case 15:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBolt(Constants.GF_FROST, dir.value(), Player.y, Player.x, Misc1.damageRoll(6, 8), Player.spellNames[14]);
						}
						break;
					case 16:
						if (Moria1.getDirection("", dir)) {
							Spells.transformWallToMud(dir.value(), Player.y, Player.x);
						}
						break;
					case 17:
						Spells.createFood();
						break;
					case 18:
						Spells.recharge(20);
						break;
					case 19:
						Spells.sleepMonsters(Player.y, Player.x);
						break;
					case 20:
						if (Moria1.getDirection("", dir)) {
							Spells.polymorphMonster(dir.value(), Player.y, Player.x);
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
							Spells.fireBolt(Constants.GF_FIRE, dir.value(), Player.y, Player.x, Misc1.damageRoll(9, 8), Player.spellNames[22]);
						}
						break;
					case 24:
						if (Moria1.getDirection("", dir)) {
							Spells.speedMonster(dir.value(), Player.y, Player.x, -1);
						}
						break;
					case 25:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBall(Constants.GF_FROST, dir.value(), Player.y, Player.x, 48, Player.spellNames[24]);
						}
						break;
					case 26:
						Spells.recharge(60);
						break;
					case 27:
						if (Moria1.getDirection("", dir)) {
							Spells.teleportMonsters(dir.value(), Player.y, Player.x);
						}
						break;
					case 28:
						f_ptr = Player.py.flags;
						f_ptr.fast += Rnd.randomInt(20) + Player.py.misc.level;
						break;
					case 29:
						if (Moria1.getDirection("", dir)) {
							Spells.fireBall(Constants.GF_FIRE, dir.value(), Player.y, Player.x, 72, Player.spellNames[28]);
						}
						break;
					case 30:
						Spells.destroyArea(Player.y, Player.x);
						break;
					case 31:
						Spells.genocide();
						break;
					default:
						break;
					}
					/* End of spells.				     */
					if (!Variable.freeTurnFlag) {
						p_ptr = Player.py.misc;
						if ((Player.spellWorked & (1L << choice.value())) == 0) {
							p_ptr.currExp += m_ptr.expGained << 2;
							Player.spellWorked |= (1L << choice.value());
							Misc3.printExperience();
						}
					}
				}
				p_ptr = Player.py.misc;
				if (!Variable.freeTurnFlag) {
					if (m_ptr.manaCost > p_ptr.currMana) {
						IO.printMessage("You faint from the effort!");
						Player.py.flags.paralysis = Rnd.randomInt((5 * (m_ptr.manaCost - p_ptr.currMana)));
						p_ptr.currMana = 0;
						p_ptr.currManaFraction = 0;
						if (Rnd.randomInt(3) == 1) {
							IO.printMessage("You have damaged your health!");
							Misc3.decreaseStat(Constants.A_CON);
						}
					} else {
						p_ptr.currMana -= m_ptr.manaCost;
					}
					Misc3.printCurrentMana();
				}
			}
	    }
	}
}
