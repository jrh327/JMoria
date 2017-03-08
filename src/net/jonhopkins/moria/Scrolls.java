/*
 * Scrolls.java: scroll code
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

import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Scrolls {
	
	private Scrolls() { }
	
	/* Scrolls for the reading				-RAK-	*/
	public static void readScroll() {
		IntPointer i = new IntPointer();
		IntPointer j = new IntPointer(), k = new IntPointer(), item_val = new IntPointer(), y, x;
		IntPointer ptr;
		boolean enchant;
		int[] tmp = new int[6];
		boolean flag, used_up;
		String out_val, tmp_str;
		boolean ident;
		int l;
		InvenType i_ptr;
		PlayerMisc m_ptr;
		
		Variable.freeTurnFlag = true;
		if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see to read the scroll.");
		} else if (Moria1.playerHasNoLight()) {
			IO.printMessage("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused to read a scroll.");
		} else if (Treasure.invenCounter == 0) {
			IO.printMessage("You are not carrying anything!");
		} else if (!Misc3.findRange(Constants.TV_SCROLL1, Constants.TV_SCROLL2, j, k)) {
			IO.printMessage("You are not carrying any scrolls!");
		} else if (Moria1.getItemId(item_val, "Read which scroll?", j.value(), k.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			Variable.freeTurnFlag = false;
			used_up = true;
			i.value(i_ptr.flags);
			ident = false;
			
			while (i.value() != 0) {
				j.value(Misc1.firstBitPos(i) + 1);
				if (i_ptr.category == Constants.TV_SCROLL2) {
					j.value(j.value() + 32);
				}
				
				/* Scrolls.			*/
				switch(j.value())
				{
				case 1:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.category != Constants.TV_NOTHING) {
						tmp_str = Desc.describeObject(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						IO.printMessage(out_val);
						ptr = new IntPointer(i_ptr.tohit);
						enchant = Spells.enchant(ptr, 10);
						i_ptr.tohit = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calcBonuses();
						} else {
							IO.printMessage("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 2:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.category != Constants.TV_NOTHING) {
						tmp_str = Desc.describeObject(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						IO.printMessage(out_val);
						if ((i_ptr.category >= Constants.TV_HAFTED)&&(i_ptr.category <= Constants.TV_DIGGING)) {
							j.value(i_ptr.damage[0] * i_ptr.damage[1]);
						} else {
							/* Bows' and arrows' enchantments should not be limited
							 * by their low base damages */
							j.value(10);
						}
						ptr = new IntPointer(i_ptr.plusToDam);
						enchant = Spells.enchant(ptr, j.value());
						i_ptr.plusToDam = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calcBonuses ();
						} else {
							IO.printMessage("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 3:
					k.value(0);
					l = 0;
					if (Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_BODY;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_ARM;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_OUTER;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HANDS;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HEAD;
						k.value(k.value() + 1);
					}
					/* also enchant boots */
					if (Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_FEET;
						k.value(k.value() + 1);
					}
					
					if (k.value() > 0)	l = tmp[Rnd.randomInt(k.value()) - 1];
					if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_BODY].flags) != 0) {
						l = Constants.INVEN_BODY;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_ARM].flags) != 0) {
						l = Constants.INVEN_ARM;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_OUTER].flags) != 0) {
						l = Constants.INVEN_OUTER;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HEAD].flags) != 0) {
						l = Constants.INVEN_HEAD;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HANDS].flags) != 0) {
						l = Constants.INVEN_HANDS;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_FEET].flags) != 0) {
						l = Constants.INVEN_FEET;
					}
					
					if (l > 0) {
						i_ptr = Treasure.inventory[l];
						tmp_str = Desc.describeObject(i_ptr, false);
						out_val = String.format("Your %s glows faintly!", tmp_str);
						IO.printMessage(out_val);
						ptr = new IntPointer(i_ptr.plusToArmorClass);
						enchant = Spells.enchant(ptr, 10);
						i_ptr.plusToArmorClass = ptr.value();
						if (enchant) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calcBonuses ();
						} else {
							IO.printMessage("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 4:
					IO.printMessage("This is an identify scroll.");
					ident = true;
					used_up = Spells.identifyObject();
					
					/* The identify may merge objects, causing the identify scroll
					 * to move to a different place.	Check for that here.  It can
					 * move arbitrarily far if an identify scroll was used on
					 * another identify scroll, but it always moves down. */
					while (i_ptr.category != Constants.TV_SCROLL1 || i_ptr.flags != 0x00000008) {
						item_val.value(item_val.value() - 1);
						i_ptr = Treasure.inventory[item_val.value()];
					}
					break;
				case 5:
					if (Spells.removeCurse()) {
						IO.printMessage("You feel as if someone is watching over you.");
						ident = true;
					}
					break;
				case 6:
					ident = Spells.lightArea(Player.y, Player.x);
					break;
				case 7:
					for (k.value(0); k.value() < Rnd.randomInt(3); k.value(k.value() + 1)) {
						y = new IntPointer(Player.y);
						x = new IntPointer(Player.x);
						ident |= Misc1.summonMonster(y, x, false);
					}
					break;
				case 8:
					Misc3.teleport(10);
					ident = true;
					break;
				case 9:
					Misc3.teleport(100);
					ident = true;
					break;
				case 10:
					Variable.dungeonLevel += (-3) + 2 * Rnd.randomInt(2);
					if (Variable.dungeonLevel < 1) {
						Variable.dungeonLevel = 1;
					}
					Variable.newLevelFlag = true;
					ident = true;
					break;
				case 11:
					if (!Player.py.flags.confuseMonster) {
						IO.printMessage("Your hands begin to glow.");
						Player.py.flags.confuseMonster = true;
						ident = true;
					}
					break;
				case 12:
					ident = true;
					Spells.mapArea();
					break;
				case 13:
					ident = Spells.sleepMonsters(Player.y, Player.x);
					break;
				case 14:
					ident = true;
					Spells.wardingGlyph();
					break;
				case 15:
					ident = Spells.detectTreasure();
					break;
				case 16:
					ident = Spells.detectObject();
					break;
				case 17:
					ident = Spells.detectTrap();
					break;
				case 18:
					ident = Spells.detectSecretDoors();
					break;
				case 19:
					IO.printMessage("This is a mass genocide scroll.");
					Spells.massGenocide();
					ident = true;
					break;
				case 20:
					ident = Spells.detectInvisibleCreatures();
					break;
				case 21:
					IO.printMessage("There is a high pitched humming noise.");
					Spells.aggravateMonster(20);
					ident = true;
					break;
				case 22:
					ident = Spells.createTraps();
					break;
				case 23:
					ident = Spells.destroyTrapsAndDoors();
					break;
				case 24:
					ident = Spells.createDoors();
					break;
				case 25:
					IO.printMessage("This is a Recharge-Item scroll.");
					ident = true;
					used_up = Spells.recharge(60);
					break;
				case 26:
					IO.printMessage("This is a genocide scroll.");
					Spells.genocide();
					ident = true;
					break;
				case 27:
					ident = Spells.unlightArea(Player.y, Player.x);
					break;
				case 28:
					ident = Spells.protectFromEvil();
					break;
				case 29:
					ident = true;
					Spells.createFood();
					break;
				case 30:
					ident = Spells.dispelCreature(Constants.CD_UNDEAD, 60);
					break;
				case 33:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.category != Constants.TV_NOTHING) {
						tmp_str = Desc.describeObject(i_ptr, false);
						out_val = String.format("Your %s glows brightly!", tmp_str);
						IO.printMessage(out_val);
						flag = false;
						ptr = new IntPointer();
						for (k.value(0); k.value() < Rnd.randomInt(2); k.value(k.value() + 1)) {
							ptr.value(i_ptr.tohit);
							enchant = Spells.enchant(ptr, 10);
							i_ptr.tohit = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if ((i_ptr.category >= Constants.TV_HAFTED)&&(i_ptr.category <= Constants.TV_DIGGING)) {
							j.value(i_ptr.damage[0] * i_ptr.damage[1]);
						} else {
							/* Bows' and arrows' enchantments should not be limited
							 * by their low base damages */
							j.value(10);
						}
						ptr = new IntPointer();
						for (k.value(0); k.value() < Rnd.randomInt(2); k.value(k.value() + 1)) {
							ptr.value(i_ptr.plusToDam);
							enchant = Spells.enchant(ptr, j.value());
							i_ptr.plusToDam = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if (flag) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calcBonuses ();
						} else {
							IO.printMessage("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 34:
					i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
					if (i_ptr.category != Constants.TV_NOTHING) {
						tmp_str = Desc.describeObject(i_ptr, false);
						out_val = String.format("Your %s glows black, fades.", tmp_str);
						IO.printMessage(out_val);
						Desc.unmagicName(i_ptr);
						i_ptr.tohit = -Rnd.randomInt(5) - Rnd.randomInt(5);
						i_ptr.plusToDam = -Rnd.randomInt(5) - Rnd.randomInt(5);
						i_ptr.plusToArmorClass = 0;
						/* Must call py_bonuses() before set (clear) flags, and
						 * must call calc_bonuses() after set (clear) flags, so that
						 * all attributes will be properly turned off. */
						Moria1.adjustPlayerBonuses(i_ptr, -1);
						i_ptr.flags = Constants.TR_CURSED;
						Moria1.calcBonuses();
						ident = true;
					}
					break;
				case 35:
					k.value(0);
					l = 0;
					if (Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_BODY;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_ARM;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_OUTER;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HANDS;
						k.value(k.value() + 1);
					}
					if (Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_HEAD;
						k.value(k.value() + 1);
					}
					/* also enchant boots */
					if (Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) {
						tmp[k.value()] = Constants.INVEN_FEET;
						k.value(k.value() + 1);
					}
					
					if (k.value() > 0)	l = tmp[Rnd.randomInt(k.value())-1];
					if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_BODY].flags) != 0) {
						l = Constants.INVEN_BODY;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_ARM].flags) != 0) {
						l = Constants.INVEN_ARM;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_OUTER].flags) != 0) {
						l = Constants.INVEN_OUTER;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HEAD].flags) != 0) {
						l = Constants.INVEN_HEAD;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_HANDS].flags) != 0) {
						l = Constants.INVEN_HANDS;
					} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_FEET].flags) != 0) {
						l = Constants.INVEN_FEET;
					}
					
					if (l > 0) {
						i_ptr = Treasure.inventory[l];
						tmp_str = Desc.describeObject(i_ptr, false);
						out_val = String.format("Your %s glows brightly!", tmp_str);
						IO.printMessage(out_val);
						flag = false;
						ptr = new IntPointer();
						for (k.value(0); k.value() < Rnd.randomInt(2) + 1; k.value(k.value() + 1)) {
							ptr.value(i_ptr.plusToArmorClass);
							enchant = Spells.enchant(ptr, 10);
							i_ptr.plusToArmorClass = ptr.value();
							if (enchant) {
								flag = true;
							}
						}
						if (flag) {
							i_ptr.flags &= ~Constants.TR_CURSED;
							Moria1.calcBonuses();
						} else {
							IO.printMessage("The enchantment fails.");
						}
						ident = true;
					}
					break;
				case 36:
					if ((Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) && (Rnd.randomInt(4) == 1)) {
						k.value(Constants.INVEN_BODY);
					} else if ((Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) && (Rnd.randomInt(3) == 1)) {
						k.value(Constants.INVEN_ARM);
					} else if ((Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) && (Rnd.randomInt(3) == 1)) {
						k.value(Constants.INVEN_OUTER);
					} else if ((Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) && (Rnd.randomInt(3) == 1)) {
						k.value(Constants.INVEN_HEAD);
					} else if ((Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) && (Rnd.randomInt(3) == 1)) {
						k.value(Constants.INVEN_HANDS);
					} else if ((Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) && (Rnd.randomInt(3) == 1)) {
						k.value(Constants.INVEN_FEET);
					} else if (Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_BODY);
					} else if (Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_ARM);
					} else if (Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_OUTER);
					} else if (Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_HEAD);
					} else if (Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_HANDS);
					} else if (Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) {
						k.value(Constants.INVEN_FEET);
					} else {
						k.value(0);
					}
					
					if (k.value() > 0) {
						i_ptr = Treasure.inventory[k.value()];
						tmp_str = Desc.describeObject(i_ptr, false);
						out_val = String.format("Your %s glows black, fades.", tmp_str);
						IO.printMessage(out_val);
						Desc.unmagicName(i_ptr);
						i_ptr.flags = Constants.TR_CURSED;
						i_ptr.tohit = 0;
						i_ptr.plusToDam = 0;
						i_ptr.plusToArmorClass = -Rnd.randomInt(5) - Rnd.randomInt(5);
						Moria1.calcBonuses();
						ident = true;
					}
					break;
				case 37:
					ident = false;
					for (k.value(0); k.value() < Rnd.randomInt(3); k.value(k.value() + 1)) {
						y = new IntPointer(Player.y);
						x = new IntPointer(Player.x);
						ident |= Misc1.summonUndead(y, x);
					}
					break;
				case 38:
					ident = true;
					Spells.bless(Rnd.randomInt(12) + 6);
					break;
				case 39:
					ident = true;
					Spells.bless(Rnd.randomInt(24) + 12);
					break;
				case 40:
					ident = true;
					Spells.bless(Rnd.randomInt(48) + 24);
					break;
				case 41:
					ident = true;
					if (Player.py.flags.wordRecall == 0) {
						Player.py.flags.wordRecall = 25 + Rnd.randomInt(30);
					}
					IO.printMessage("The air about you becomes charged.");
					break;
				case 42:
					Spells.destroyArea(Player.y, Player.x);
					ident = true;
					break;
				default:
					IO.printMessage("Internal error in scroll()");
					break;
				}
				/* End of Scrolls.			       */
			}
			i_ptr = Treasure.inventory[item_val.value()];
			if (ident) {
				if (!Desc.isKnownByPlayer(i_ptr)) {
					m_ptr = Player.py.misc;
					/* round half-way case up */
					m_ptr.currExp += (i_ptr.level + (m_ptr.level >> 1)) / m_ptr.level;
					Misc3.printExperience();
					
					Desc.identify(item_val);
					i_ptr = Treasure.inventory[item_val.value()];
				}
			} else if (!Desc.isKnownByPlayer(i_ptr)) {
				Desc.sample(i_ptr);
			}
			if (used_up) {
				Desc.describeRemaining(item_val.value());
				Misc3.destroyInvenItem(item_val.value());
			}
		}
	}
}
