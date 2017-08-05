/*
 * Misc2.java: misc utility and initialization code, magic objects code
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

import net.jonhopkins.moria.types.BooleanPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.OptionDescType;

public class Misc2 {
	
	private Misc2() { }
	
	/**
	 * Chance of treasure having magic abilities.
	 * Chance increases with each dungeon level -RAK-
	 * 
	 * @param treasureIndex Index in the treasure array of the item being checked
	 * @param level Dungeon level at which the player found the object
	 */
	public static void addMagicToTreasure(int treasureIndex, int level) {
		int chance = Constants.OBJ_BASE_MAGIC + level;
		if (chance > Constants.OBJ_BASE_MAX) {
			chance = Constants.OBJ_BASE_MAX;
		}
		int special = chance / Constants.OBJ_DIV_SPECIAL;
		int cursed  = (10 * chance) / Constants.OBJ_DIV_CURSED;
		InvenType item = Treasure.treasureList[treasureIndex];
		
		// some objects appear multiple times in the object_list with different
		// levels, this is to make the object occur more often, however, for
		// consistency, must set the level of these duplicates to be the same
		// as the object with the lowest level
		
		// Depending on treasure type, it can have certain magical properties
		switch (item.category) {
		case Constants.TV_SHIELD: case Constants.TV_HARD_ARMOR: case Constants.TV_SOFT_ARMOR:
			if (Misc1.isMagik(chance)) {
				item.plusToArmorClass += Misc1.magicBonus(1, 30, level);
				if (Misc1.isMagik(special)) {
					switch (Rnd.randomInt(9)) {
					case 1:
						item.flags |= (Constants.TR_RES_LIGHT
								| Constants.TR_RES_COLD
								| Constants.TR_RES_ACID
								| Constants.TR_RES_FIRE);
						item.specialName = Constants.SN_R;
						item.plusToArmorClass += 5;
						item.cost += 2500;
						break;
					case 2: // Resist Acid
						item.flags |= Constants.TR_RES_ACID;
						item.specialName = Constants.SN_RA;
						item.cost += 1000;
						break;
					case 3:
					case 4: // Resist Fire
						item.flags |= Constants.TR_RES_FIRE;
						item.specialName = Constants.SN_RF;
						item.cost += 600;
						break;
					case 5:
					case 6: // Resist Cold
						item.flags |= Constants.TR_RES_COLD;
						item.specialName = Constants.SN_RC;
						item.cost += 600;
						break;
					case 7:
					case 8:
					case 9: // Resist Lightning
						item.flags |= Constants.TR_RES_LIGHT;
						item.specialName = Constants.SN_RL;
						item.cost += 500;
						break;
					default:
						break;
					}
				}
			} else if (Misc1.isMagik(cursed)) {
				item.plusToArmorClass -= Misc1.magicBonus(1, 40, level);
				item.cost = 0;
				item.flags |= Constants.TR_CURSED;
			}
			break;
			
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_SWORD:
			// always show tohit/todam values if identified
			item.identify |= Constants.ID_SHOW_HITDAM;
			if (Misc1.isMagik(chance)) {
				item.tohit += Misc1.magicBonus(0, 40, level);
				
				// Magical damage bonus now proportional to weapon base damage
				int damage = item.damage[0] * item.damage[1];
				item.plusToDam += Misc1.magicBonus(0, 4 * damage, damage * level / 10);
				
				// the 3*special/2 is needed because weapons are not as common as
				// before change to treasure distribution, this helps keep same
				// number of ego weapons same as before, see also missiles
				if (Misc1.isMagik(3 * special / 2)) {
					switch (Rnd.randomInt(16)) {
					case 1: // Holy Avenger
						item.flags |= (Constants.TR_SEE_INVIS
								| Constants.TR_SUST_STAT
								| Constants.TR_SLAY_UNDEAD
								| Constants.TR_SLAY_EVIL
								| Constants.TR_STR);
						item.tohit += 5;
						item.plusToDam += 5;
						item.plusToArmorClass += Rnd.randomInt(4);
						
						// the value in p1 is used for strength increase
						// p1 is also used for sustain stat
						item.misc = Rnd.randomInt(4);
						item.specialName = Constants.SN_HA;
						item.cost += item.misc * 500;
						item.cost += 10000;
						break;
					case 2: // Defender
						item.flags |= (Constants.TR_FFALL
								| Constants.TR_RES_LIGHT
								| Constants.TR_SEE_INVIS
								| Constants.TR_FREE_ACT
								| Constants.TR_RES_COLD
								| Constants.TR_RES_ACID
								| Constants.TR_RES_FIRE
								| Constants.TR_REGEN
								| Constants.TR_STEALTH);
						item.tohit += 3;
						item.plusToDam += 3;
						item.plusToArmorClass += 5 + Rnd.randomInt(5);
						item.specialName = Constants.SN_DF;
						
						// the value in p1 is used for stealth
						item.misc = Rnd.randomInt(3);
						item.cost += item.misc * 500;
						item.cost += 7500;
						break;
					case 3:
					case 4: // Slay Animal
						item.flags |= Constants.TR_SLAY_ANIMAL;
						item.tohit += 2;
						item.plusToDam += 2;
						item.specialName = Constants.SN_SA;
						item.cost += 3000;
						break;
					case 5:
					case 6: // Slay Dragon
						item.flags |= Constants.TR_SLAY_DRAGON;
						item.tohit += 3;
						item.plusToDam += 3;
						item.specialName = Constants.SN_SD;
						item.cost += 4000;
						break;
					case 7:
					case 8: // Slay Evil
						item.flags |= Constants.TR_SLAY_EVIL;
						item.tohit += 3;
						item.plusToDam += 3;
						item.specialName = Constants.SN_SE;
						item.cost += 4000;
						break;
					case 9:
					case 10: // Slay Undead
						item.flags |= (Constants.TR_SEE_INVIS
								| Constants.TR_SLAY_UNDEAD);
						item.tohit += 3;
						item.plusToDam += 3;
						item.specialName = Constants.SN_SU;
						item.cost += 5000;
						break;
					case 11:
					case 12:
					case 13: // Flame Tongue
						item.flags |= Constants.TR_FLAME_TONGUE;
						item.tohit++;
						item.plusToDam += 3;
						item.specialName = Constants.SN_FT;
						item.cost += 2000;
						break;
					case 14:
					case 15:
					case 16: // Frost Brand
						item.flags |= Constants.TR_FROST_BRAND;
						item.tohit++;
						item.plusToDam++;
						item.specialName = Constants.SN_FB;
						item.cost += 1200;
						break;
					default:
						break;
					}
				}
			} else if (Misc1.isMagik(cursed)) {
				item.tohit -= Misc1.magicBonus(1, 55, level);
				// Magical damage bonus now proportional to weapon base damage
				int damage = item.damage[0] * item.damage[1];
				item.plusToDam -= Misc1.magicBonus(1, 11 * damage / 2, damage * level / 10);
				item.flags |= Constants.TR_CURSED;
				item.cost = 0;
			}
			break;
			
		case Constants.TV_BOW:
			// always show tohit/todam values if identified
			item.identify |= Constants.ID_SHOW_HITDAM;
			if (Misc1.isMagik(chance)) {
				item.tohit += Misc1.magicBonus(1, 30, level);
				item.plusToDam += Misc1.magicBonus(1, 20, level); // add damage. -CJS-
			} else if (Misc1.isMagik(cursed)) {
				item.tohit -= Misc1.magicBonus(1, 50, level);
				item.plusToDam -= Misc1.magicBonus(1, 30, level); // add damage. -CJS-
				item.flags |= Constants.TR_CURSED;
				item.cost = 0;
			}
			break;
			
		case Constants.TV_DIGGING:
			// always show tohit/todam values if identified
			item.identify |= Constants.ID_SHOW_HITDAM;
			if (Misc1.isMagik(chance)) {
				int tmp = Rnd.randomInt(3);
				if (tmp < 3) {
					item.misc += Misc1.magicBonus(0, 25, level);
				} else {
					// a cursed digging tool
					item.misc = -Misc1.magicBonus(1, 30, level);
					item.cost = 0;
					item.flags |= Constants.TR_CURSED;
				}
			}
			break;
			
		case Constants.TV_GLOVES:
			if (Misc1.isMagik(chance)) {
				item.plusToArmorClass += Misc1.magicBonus(1, 20, level);
				if (Misc1.isMagik(special)) {
					if (Rnd.randomInt(2) == 1) {
						item.flags |= Constants.TR_FREE_ACT;
						item.specialName = Constants.SN_FREE_ACTION;
						item.cost += 1000;
					} else {
						item.identify |= Constants.ID_SHOW_HITDAM;
						item.tohit += 1 + Rnd.randomInt(3);
						item.plusToDam += 1 + Rnd.randomInt(3);
						item.specialName = Constants.SN_SLAYING;
						item.cost += (item.tohit + item.plusToDam) * 250;
					}
				}
			} else if (Misc1.isMagik(cursed)) {
				if (Misc1.isMagik(special)) {
					if (Rnd.randomInt(2) == 1) {
						item.flags |= Constants.TR_DEX;
						item.specialName = Constants.SN_CLUMSINESS;
					} else {
						item.flags |= Constants.TR_STR;
						item.specialName = Constants.SN_WEAKNESS;
					}
					item.identify |= Constants.ID_SHOW_P1;
					item.misc   = -Misc1.magicBonus(1, 10, level);
				}
				item.plusToArmorClass -= Misc1.magicBonus(1, 40, level);
				item.flags |= Constants.TR_CURSED;
				item.cost = 0;
			}
			break;
			
		case Constants.TV_BOOTS:
			if (Misc1.isMagik(chance)) {
				item.plusToArmorClass += Misc1.magicBonus(1, 20, level);
				if (Misc1.isMagik(special)) {
					int tmp = Rnd.randomInt(12);
					if (tmp > 5) {
						item.flags |= Constants.TR_FFALL;
						item.specialName = Constants.SN_SLOW_DESCENT;
						item.cost += 250;
					} else if (tmp == 1) {
						item.flags |= Constants.TR_SPEED;
						item.specialName = Constants.SN_SPEED;
						item.identify |= Constants.ID_SHOW_P1;
						item.misc = 1;
						item.cost += 5000;
					} else { // 2 - 5
						item.flags |= Constants.TR_STEALTH;
						item.identify |= Constants.ID_SHOW_P1;
						item.misc = Rnd.randomInt(3);
						item.specialName = Constants.SN_STEALTH;
						item.cost += 500;
					}
				}
			} else if (Misc1.isMagik(cursed)) {
				int tmp = Rnd.randomInt(3);
				if (tmp == 1) {
					item.flags |= Constants.TR_SPEED;
					item.specialName = Constants.SN_SLOWNESS;
					item.identify |= Constants.ID_SHOW_P1;
					item.misc = -1;
				} else if (tmp == 2) {
					item.flags |= Constants.TR_AGGRAVATE;
					item.specialName = Constants.SN_NOISE;
				} else {
					item.specialName = Constants.SN_GREAT_MASS;
					item.weight = item.weight * 5;
				}
				item.cost = 0;
				item.plusToArmorClass -= Misc1.magicBonus(2, 45, level);
				item.flags |= Constants.TR_CURSED;
			}
			break;
			
		case Constants.TV_HELM: // Helms
			if ((item.subCategory >= 6) && (item.subCategory <= 8)) {
				// give crowns a higher chance for magic
				chance += (item.cost / 100);
				special += special;
			}
			if (Misc1.isMagik(chance)) {
				item.plusToArmorClass += Misc1.magicBonus(1, 20, level);
				if (Misc1.isMagik(special)) {
					if (item.subCategory < 6) {
						int tmp = Rnd.randomInt(3);
						item.identify |= Constants.ID_SHOW_P1;
						if (tmp == 1) {
							item.misc = Rnd.randomInt(2);
							item.flags |= Constants.TR_INT;
							item.specialName = Constants.SN_INTELLIGENCE;
							item.cost += item.misc * 500;
						} else if (tmp == 2) {
							item.misc = Rnd.randomInt(2);
							item.flags |= Constants.TR_WIS;
							item.specialName = Constants.SN_WISDOM;
							item.cost += item.misc * 500;
						} else {
							item.misc = 1 + Rnd.randomInt(4);
							item.flags |= Constants.TR_INFRA;
							item.specialName = Constants.SN_INFRAVISION;
							item.cost += item.misc * 250;
						}
					} else {
						switch (Rnd.randomInt(6)) {
						case 1:
							item.identify |= Constants.ID_SHOW_P1;
							item.misc = Rnd.randomInt(3);
							item.flags |= (Constants.TR_FREE_ACT
									| Constants.TR_CON
									|  Constants.TR_DEX
									| Constants.TR_STR);
							item.specialName = Constants.SN_MIGHT;
							item.cost += 1000 + item.misc * 500;
							break;
						case 2:
							item.identify |= Constants.ID_SHOW_P1;
							item.misc = Rnd.randomInt(3);
							item.flags |= (Constants.TR_CHR | Constants.TR_WIS);
							item.specialName = Constants.SN_LORDLINESS;
							item.cost += 1000 + item.misc * 500;
							break;
						case 3:
							item.identify |= Constants.ID_SHOW_P1;
							item.misc = Rnd.randomInt(3);
							item.flags |= (Constants.TR_RES_LIGHT
									| Constants.TR_RES_COLD
									|  Constants.TR_RES_ACID
									| Constants.TR_RES_FIRE
									| Constants.TR_INT);
							item.specialName = Constants.SN_MAGI;
							item.cost += 3000 + item.misc * 500;
							break;
						case 4:
							item.identify |= Constants.ID_SHOW_P1;
							item.misc = Rnd.randomInt(3);
							item.flags |= Constants.TR_CHR;
							item.specialName = Constants.SN_BEAUTY;
							item.cost += 750;
							break;
						case 5:
							item.identify |= Constants.ID_SHOW_P1;
							item.misc = 5 * (1 + Rnd.randomInt(4));
							item.flags |= (Constants.TR_SEE_INVIS
									| Constants.TR_SEARCH);
							item.specialName = Constants.SN_SEEING;
							item.cost += 1000 + item.misc * 100;
							break;
						case 6:
							item.flags |= Constants.TR_REGEN;
							item.specialName = Constants.SN_REGENERATION;
							item.cost += 1500;
							break;
						default:
							break;
						}
					}
				}
			} else if (Misc1.isMagik(cursed)) {
				item.plusToArmorClass -= Misc1.magicBonus(1, 45, level);
				item.flags |= Constants.TR_CURSED;
				item.cost = 0;
				if (Misc1.isMagik(special)) {
					switch (Rnd.randomInt(7)) {
					case 1:
						item.identify |= Constants.ID_SHOW_P1;
						item.misc = -Rnd.randomInt(5);
						item.flags |= Constants.TR_INT;
						item.specialName = Constants.SN_STUPIDITY;
						break;
					case 2:
						item.identify |= Constants.ID_SHOW_P1;
						item.misc = -Rnd.randomInt(5);
						item.flags |= Constants.TR_WIS;
						item.specialName = Constants.SN_DULLNESS;
						break;
					case 3:
						item.flags |= Constants.TR_BLIND;
						item.specialName = Constants.SN_BLINDNESS;
						break;
					case 4:
						item.flags |= Constants.TR_TIMID;
						item.specialName = Constants.SN_TIMIDNESS;
						break;
					case 5:
						item.identify |= Constants.ID_SHOW_P1;
						item.misc = -Rnd.randomInt(5);
						item.flags |= Constants.TR_STR;
						item.specialName = Constants.SN_WEAKNESS;
						break;
					case 6:
						item.flags |= Constants.TR_TELEPORT;
						item.specialName = Constants.SN_TELEPORTATION;
						break;
					case 7:
						item.identify |= Constants.ID_SHOW_P1;
						item.misc = -Rnd.randomInt(5);
						item.flags |= Constants.TR_CHR;
						item.specialName = Constants.SN_UGLINESS;
						break;
					default:
						break;
					}
				}
			}
			break;
			
		case Constants.TV_RING: // Rings
			switch (item.subCategory) {
			case 0:
			case 1:
			case 2:
			case 3:
				if (Misc1.isMagik(cursed)) {
					item.misc = -Misc1.magicBonus(1, 20, level);
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				} else {
					item.misc = Misc1.magicBonus(1, 10, level);
					item.cost += item.misc * 100;
				}
				break;
			case 4:
				if (Misc1.isMagik(cursed)) {
					item.misc = -Rnd.randomInt(3);
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				} else {
					item.misc = 1;
				}
				break;
			case 5:
				item.misc = 5 * Misc1.magicBonus(1, 20, level);
				item.cost += item.misc * 50;
				if (Misc1.isMagik(cursed)) {
					item.misc = -item.misc;
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				}
				break;
			case 19: // Increase damage
				item.plusToDam += Misc1.magicBonus(1, 20, level);
				item.cost += item.plusToDam * 100;
				if (Misc1.isMagik(cursed)) {
					item.plusToDam = -item.plusToDam;
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				}
				break;
			case 20: // Increase To-Hit
				item.tohit += Misc1.magicBonus(1, 20, level);
				item.cost += item.tohit * 100;
				if (Misc1.isMagik(cursed)) {
					item.tohit = -item.tohit;
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				}
				break;
			case 21: // Protection
				item.plusToArmorClass += Misc1.magicBonus(1, 20, level);
				item.cost += item.plusToArmorClass * 100;
				if (Misc1.isMagik(cursed)) {
					item.plusToArmorClass = -item.plusToArmorClass;
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				}
				break;
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 29:
				item.identify |= Constants.ID_NOSHOW_P1;
				break;
			case 30: // Slaying
				item.identify |= Constants.ID_SHOW_HITDAM;
				item.plusToDam += Misc1.magicBonus(1, 25, level);
				item.tohit += Misc1.magicBonus(1, 25, level);
				item.cost += (item.tohit + item.plusToDam) * 100;
				if (Misc1.isMagik(cursed)) {
					item.tohit = -item.tohit;
					item.plusToDam = -item.plusToDam;
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				}
				break;
			default:
				break;
			}
			break;
			
		case Constants.TV_AMULET: // Amulets
			if (item.subCategory < 2) {
				if (Misc1.isMagik(cursed)) {
					item.misc = -Misc1.magicBonus(1, 20, level);
					item.flags |= Constants.TR_CURSED;
					item.cost = -item.cost;
				} else {
					item.misc = Misc1.magicBonus(1, 10, level);
					item.cost += item.misc * 100;
				}
			} else if (item.subCategory == 2) {
				item.misc = 5 * Misc1.magicBonus(1, 25, level);
				if (Misc1.isMagik(cursed)) {
					item.misc = -item.misc;
					item.cost = -item.cost;
					item.flags |= Constants.TR_CURSED;
				} else {
					item.cost += 50 * item.misc;
				}
			} else if (item.subCategory == 8) {
				// amulet of the magi is never cursed
				item.misc = 5 * Misc1.magicBonus(1, 25, level);
				item.cost += 20 * item.misc;
			}
			break;
			
			// Subval should be even for store, odd for dungeon
			// Dungeon found ones will be partially charged
		case Constants.TV_LIGHT:
			if ((item.subCategory % 2) != 0) {
				item.misc = Rnd.randomInt(item.misc);
				item.subCategory -= 1;
			}
			break;
			
		case Constants.TV_WAND:
			switch (item.subCategory) {
			case 0:
				item.misc = Rnd.randomInt(10) + 6;
				break;
			case 1:
				item.misc = Rnd.randomInt(8) + 6;
				break;
			case 2:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 3:
				item.misc = Rnd.randomInt(8) + 6;
				break;
			case 4:
				item.misc = Rnd.randomInt(4) + 3;
				break;
			case 5:
				item.misc = Rnd.randomInt(8) + 6;
				break;
			case 6:
				item.misc = Rnd.randomInt(20) + 12;
				break;
			case 7:
				item.misc = Rnd.randomInt(20) + 12;
				break;
			case 8:
				item.misc = Rnd.randomInt(10) + 6;
				break;
			case 9:
				item.misc = Rnd.randomInt(12) + 6;
				break;
			case 10:
				item.misc = Rnd.randomInt(10) + 12;
				break;
			case 11:
				item.misc = Rnd.randomInt(3) + 3;
				break;
			case 12:
				item.misc = Rnd.randomInt(8) + 6;
				break;
			case 13:
				item.misc = Rnd.randomInt(10) + 6;
				break;
			case 14:
				item.misc = Rnd.randomInt(5) + 3;
				break;
			case 15:
				item.misc = Rnd.randomInt(5) + 3;
				break;
			case 16:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 17:
				item.misc = Rnd.randomInt(5) + 4;
				break;
			case 18:
				item.misc = Rnd.randomInt(8) + 4;
				break;
			case 19:
				item.misc = Rnd.randomInt(6) + 2;
				break;
			case 20:
				item.misc = Rnd.randomInt(4) + 2;
				break;
			case 21:
				item.misc = Rnd.randomInt(8) + 6;
				break;
			case 22:
				item.misc = Rnd.randomInt(5) + 2;
				break;
			case 23:
				item.misc = Rnd.randomInt(12) + 12;
				break;
			default:
				break;
			}
			break;
			
		case Constants.TV_STAFF:
			switch (item.subCategory) {
			case 0:
				item.misc = Rnd.randomInt(20) + 12;
				break;
			case 1:
				item.misc = Rnd.randomInt(8) + 6;
				break;
			case 2:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 3:
				item.misc = Rnd.randomInt(20) + 12;
				break;
			case 4:
				item.misc = Rnd.randomInt(15) + 6;
				break;
			case 5:
				item.misc = Rnd.randomInt(4) + 5;
				break;
			case 6:
				item.misc = Rnd.randomInt(5) + 3;
				break;
			case 7:
				item.misc = Rnd.randomInt(3) + 1;
				item.level = 10;
				break;
			case 8:
				item.misc = Rnd.randomInt(3) + 1;
				break;
			case 9:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 10:
				item.misc = Rnd.randomInt(10) + 12;
				break;
			case 11:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 12:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 13:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 14:
				item.misc = Rnd.randomInt(10) + 12;
				break;
			case 15:
				item.misc = Rnd.randomInt(3) + 4;
				break;
			case 16:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 17:
				item.misc = Rnd.randomInt(5) + 6;
				break;
			case 18:
				item.misc = Rnd.randomInt(3) + 4;
				break;
			case 19:
				item.misc = Rnd.randomInt(10) + 12;
				break;
			case 20:
				item.misc = Rnd.randomInt(3) + 4;
				break;
			case 21:
				item.misc = Rnd.randomInt(3) + 4;
				break;
			case 22:
				item.misc = Rnd.randomInt(10) + 6;
				item.level = 5;
				break;
			default:
				break;
			}
			break;
			
		case Constants.TV_CLOAK:
			if (Misc1.isMagik(chance)) {
				if (Misc1.isMagik(special)) {
					if (Rnd.randomInt(2) == 1) {
						item.specialName = Constants.SN_PROTECTION;
						item.plusToArmorClass += Misc1.magicBonus(2, 40, level);
						item.cost += 250;
					} else {
						item.plusToArmorClass += Misc1.magicBonus(1, 20, level);
						item.identify |= Constants.ID_SHOW_P1;
						item.misc = Rnd.randomInt(3);
						item.flags |= Constants.TR_STEALTH;
						item.specialName = Constants.SN_STEALTH;
						item.cost += 500;
					}
				} else {
					item.plusToArmorClass += Misc1.magicBonus(1, 20, level);
				}
			} else if (Misc1.isMagik(cursed)) {
				int tmp = Rnd.randomInt(3);
				if (tmp == 1) {
					item.flags |= Constants.TR_AGGRAVATE;
					item.specialName = Constants.SN_IRRITATION;
					item.plusToArmorClass  -= Misc1.magicBonus(1, 10, level);
					item.identify |= Constants.ID_SHOW_HITDAM;
					item.tohit -= Misc1.magicBonus(1, 10, level);
					item.plusToDam -= Misc1.magicBonus(1, 10, level);
					item.cost =  0;
				} else if (tmp == 2) {
					item.specialName = Constants.SN_VULNERABILITY;
					item.plusToArmorClass -= Misc1.magicBonus(10, 100, level + 50);
					item.cost = 0;
				} else {
					item.specialName = Constants.SN_ENVELOPING;
					item.plusToArmorClass  -= Misc1.magicBonus(1, 10, level);
					item.identify |= Constants.ID_SHOW_HITDAM;
					item.tohit -= Misc1.magicBonus(2, 40, level+10);
					item.plusToDam -= Misc1.magicBonus(2, 40, level+10);
					item.cost = 0;
				}
				item.flags |= Constants.TR_CURSED;
			}
			break;
			
		case Constants.TV_CHEST:
			switch (Rnd.randomInt(level + 4)) {
			case 1:
				item.flags = 0;
				item.specialName = Constants.SN_EMPTY;
				break;
			case 2:
				item.flags |= Constants.CH_LOCKED;
				item.specialName = Constants.SN_LOCKED;
				break;
			case 3: case 4:
				item.flags |= (Constants.CH_LOSE_STR
						| Constants.CH_LOCKED);
				item.specialName = Constants.SN_POISON_NEEDLE;
				break;
			case 5: case 6:
				item.flags |= (Constants.CH_POISON
						| Constants.CH_LOCKED);
				item.specialName = Constants.SN_POISON_NEEDLE;
				break;
			case 7: case 8: case 9:
				item.flags |= (Constants.CH_PARALYSED
						| Constants.CH_LOCKED);
				item.specialName = Constants.SN_GAS_TRAP;
				break;
			case 10: case 11:
				item.flags |= (Constants.CH_EXPLODE
						| Constants.CH_LOCKED);
				item.specialName = Constants.SN_EXPLOSION_DEVICE;
				break;
			case 12: case 13: case 14:
				item.flags |= (Constants.CH_SUMMON
						| Constants.CH_LOCKED);
				item.specialName = Constants.SN_SUMMONING_RUNES;
				break;
			case 15: case 16: case 17:
				item.flags |= (Constants.CH_PARALYSED
						| Constants.CH_POISON
						| Constants.CH_LOSE_STR
						| Constants.CH_LOCKED);
				item.specialName = Constants.SN_MULTIPLE_TRAPS;
				break;
			default:
				item.flags |= (Constants.CH_SUMMON
						| Constants.CH_EXPLODE
						| Constants.CH_LOCKED);
				item.specialName = Constants.SN_MULTIPLE_TRAPS;
				break;
			}
			break;
			
		case Constants.TV_SLING_AMMO:
		case Constants.TV_SPIKE:
		case Constants.TV_BOLT:
		case Constants.TV_ARROW:
			if (item.category == Constants.TV_SLING_AMMO
					|| item.category == Constants.TV_BOLT
					|| item.category == Constants.TV_ARROW) {
				// always show tohit/todam values if identified
				item.identify |= Constants.ID_SHOW_HITDAM;
				
				if (Misc1.isMagik(chance)) {
					item.tohit += Misc1.magicBonus(1, 35, level);
					item.plusToDam += Misc1.magicBonus(1, 35, level);
					// see comment for weapons
					if (Misc1.isMagik(3 * special / 2)) {
						switch (Rnd.randomInt(10)) {
						case 1:
						case 2:
						case 3:
							item.specialName = Constants.SN_SLAYING;
							item.tohit += 5;
							item.plusToDam += 5;
							item.cost += 20;
							break;
						case 4:
						case 5:
							item.flags |= Constants.TR_FLAME_TONGUE;
							item.tohit += 2;
							item.plusToDam += 4;
							item.specialName = Constants.SN_FIRE;
							item.cost += 25;
							break;
						case 6:
						case 7:
							item.flags |= Constants.TR_SLAY_EVIL;
							item.tohit += 3;
							item.plusToDam += 3;
							item.specialName = Constants.SN_SLAY_EVIL;
							item.cost += 25;
							break;
						case 8:
						case 9:
							item.flags |= Constants.TR_SLAY_ANIMAL;
							item.tohit += 2;
							item.plusToDam += 2;
							item.specialName = Constants.SN_SLAY_ANIMAL;
							item.cost += 30;
							break;
						case 10:
							item.flags |= Constants.TR_SLAY_DRAGON;
							item.tohit += 3;
							item.plusToDam += 3;
							item.specialName = Constants.SN_DRAGON_SLAYING;
							item.cost += 35;
							break;
						default:
							break;
						}
					}
				} else if (Misc1.isMagik(cursed)) {
					item.tohit -= Misc1.magicBonus(5, 55, level);
					item.plusToDam -= Misc1.magicBonus(5, 55, level);
					item.flags |= Constants.TR_CURSED;
					item.cost = 0;
				}
			}
			
			item.number = 0;
			for (int i = 0; i < 7; i++) {
				item.number += Rnd.randomInt(6);
			}
			if (Variable.missileCounter == Constants.MAX_SHORT) {
				Variable.missileCounter = -Constants.MAX_SHORT - 1;
			} else {
				Variable.missileCounter++;
			}
			item.misc = Variable.missileCounter;
			break;
			
		case Constants.TV_FOOD:
			// make sure all food rations have the same level
			if (item.subCategory == 90) {
				item.level = 0; // give all elvish waybread the same level
			} else if (item.subCategory == 92) {
				item.level = 6;
			}
			break;
			
		case Constants.TV_SCROLL1:
			// give all identify scrolls the same level
			if (item.subCategory == 67) {
				item.level = 1; // scroll of light
			} else if (item.subCategory == 69) {
				item.level = 0; // scroll of trap detection
			} else if (item.subCategory == 80) {
				item.level = 5; // scroll of door/stair location
			} else if (item.subCategory == 81) {
				item.level = 5;
			}
			break;
			
		case Constants.TV_POTION1: // potions
			// cure light
			if (item.subCategory == 76) {
				item.level = 0;
			}
			break;
			
		default:
			break;
		}
	}
	
	public static OptionDescType[] options = new OptionDescType[] {
			new OptionDescType("Running: cut known corners", Variable.findCut),
			new OptionDescType("Running: examine potential corners", Variable.findExamine),
			new OptionDescType("Running: print self during run", Variable.findPrself),
			new OptionDescType("Running: stop when map sector changes", Variable.findBound),
			new OptionDescType("Running: run through open doors", Variable.findIgnoreDoors),
			new OptionDescType("Prompt to pick up objects", Variable.promptCarryFlag),
			new OptionDescType("Rogue like commands", Variable.rogueLikeCommands),
			new OptionDescType("Show weights in inventory", Variable.showWeightFlag),
			new OptionDescType("Highlight and notice mineral seams", Variable.highlightSeams),
			new OptionDescType("Beep for invalid character", Variable.soundBeepFlag),
			new OptionDescType("Display rest/repeat counts", Variable.displayCounts),
			new OptionDescType("", new BooleanPointer(false))
	};
	
	/**
	 * Set or unset various boolean options. -CJS-
	 */
	public static void setOptions() {
		int max = options.length;
		IO.print("  ESC when finished, y/n to set options, <return> or - to move cursor", 0, 0);
		for (int i = 0; i < max; i++) {
			String string = String.format(
					"%-38s: %s",
					options[i].optionPrompt,
					(options[i].isActive.value() ? "yes" : "no "));
			IO.print(string, i + 1, 0);
		}
		IO.eraseLine(max + 1, 0);
		
		int i = 0;
		for(;;) {
			IO.moveCursor(i + 1, 40);
			switch(IO.inkey()) {
			case Constants.ESCAPE:
				return;
			case '-':
				if (i > 0) {
					i--;
				} else {
					i = max - 1;
				}
				break;
			case ' ':
			case '\n':
			case '\r':
				if (i + 1 < max) {
					i++;
				} else {
					i = 0;
				}
				break;
			case 'y':
			case 'Y':
				IO.putBuffer("yes", i + 1, 40);
				options[i].isActive.value(true);
				if (i + 1 < max) {
					i++;
				} else {
					i = 0;
				}
				break;
			case 'n':
			case 'N':
				IO.putBuffer("no ", i + 1, 40);
				options[i].isActive.value(false);
				if (i + 1 < max) {
					i++;
				} else {
					i = 0;
				}
				break;
			default:
				IO.bell();
				break;
			}
		}
	}
}
