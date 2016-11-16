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
	 * Chance of treasure having magic abilities. Chance increases with each dungeon level -RAK-
	 * 
	 * @param x - Index in the treasure array of the item being checked
	 * @param level - Dungeon level at which the player found the object
	 */
	public static void addMagicToTreasure(int x, int level) {
		InvenType t_ptr;
		int chance, special, cursed, i;
		int tmp;
		
		chance = Constants.OBJ_BASE_MAGIC + level;
		if (chance > Constants.OBJ_BASE_MAX) {
			chance = Constants.OBJ_BASE_MAX;
		}
		special = chance / Constants.OBJ_DIV_SPECIAL;
		cursed  = (10 * chance) / Constants.OBJ_DIV_CURSED;
		t_ptr = Treasure.treasureList[x];
		
		/* some objects appear multiple times in the object_list with different
		 * levels, this is to make the object occur more often, however, for
		 * consistency, must set the level of these duplicates to be the same
		 * as the object with the lowest level */
		
		/* Depending on treasure type, it can have certain magical properties */
		switch (t_ptr.category) {
		case Constants.TV_SHIELD: case Constants.TV_HARD_ARMOR: case Constants.TV_SOFT_ARMOR:
			if (Misc1.magik(chance)) {
				t_ptr.plusToArmorClass += Misc1.m_bonus(1, 30, level);
				if (Misc1.magik(special)) {
					switch(Misc1.randomInt(9)) {
					case 1:
						t_ptr.flags |= (Constants.TR_RES_LIGHT | Constants.TR_RES_COLD | Constants.TR_RES_ACID | Constants.TR_RES_FIRE);
						t_ptr.specialName = Constants.SN_R;
						t_ptr.plusToArmorClass += 5;
						t_ptr.cost += 2500;
						break;
					case 2:	 /* Resist Acid	  */
						t_ptr.flags |= Constants.TR_RES_ACID;
						t_ptr.specialName = Constants.SN_RA;
						t_ptr.cost += 1000;
						break;
					case 3: case 4:	 /* Resist Fire	  */
						t_ptr.flags |= Constants.TR_RES_FIRE;
						t_ptr.specialName = Constants.SN_RF;
						t_ptr.cost += 600;
						break;
					case 5: case 6:	/* Resist Cold	 */
						t_ptr.flags |= Constants.TR_RES_COLD;
						t_ptr.specialName = Constants.SN_RC;
						t_ptr.cost += 600;
						break;
					case 7: case 8: case 9:  /* Resist Lightning*/
						t_ptr.flags |= Constants.TR_RES_LIGHT;
						t_ptr.specialName = Constants.SN_RL;
						t_ptr.cost += 500;
						break;
					default:
						break;
					}
				}
			} else if (Misc1.magik(cursed)) {
				t_ptr.plusToArmorClass -= Misc1.m_bonus(1, 40, level);
				t_ptr.cost = 0;
				t_ptr.flags |= Constants.TR_CURSED;
			}
			break;
			
		case Constants.TV_HAFTED: case Constants.TV_POLEARM: case Constants.TV_SWORD:
			/* always show tohit/todam values if identified */
			t_ptr.identify |= Constants.ID_SHOW_HITDAM;
			if (Misc1.magik(chance)) {
				t_ptr.tohit += Misc1.m_bonus(0, 40, level);
				/* Magical damage bonus now proportional to weapon base damage */
				tmp = t_ptr.damage[0] * t_ptr.damage[1];
				t_ptr.plusToDam += Misc1.m_bonus(0, 4 * tmp, tmp * level / 10);
				/* the 3*special/2 is needed because weapons are not as common as
				 * before change to treasure distribution, this helps keep same
				 * number of ego weapons same as before, see also missiles */
				if (Misc1.magik(3 * special / 2)) {
					switch(Misc1.randomInt(16)) {
					case 1:	/* Holy Avenger	 */
						t_ptr.flags |= (Constants.TR_SEE_INVIS | Constants.TR_SUST_STAT | Constants.TR_SLAY_UNDEAD | Constants.TR_SLAY_EVIL | Constants.TR_STR);
						t_ptr.tohit += 5;
						t_ptr.plusToDam += 5;
						t_ptr.plusToArmorClass  += Misc1.randomInt(4);
						/* the value in p1 is used for strength increase */
						/* p1 is also used for sustain stat */
						t_ptr.misc    = Misc1.randomInt(4);
						t_ptr.specialName = Constants.SN_HA;
						t_ptr.cost += t_ptr.misc * 500;
						t_ptr.cost += 10000;
						break;
					case 2:	/* Defender	 */
						t_ptr.flags |= (Constants.TR_FFALL | Constants.TR_RES_LIGHT |
								Constants.TR_SEE_INVIS | Constants.TR_FREE_ACT | Constants.TR_RES_COLD |
								Constants.TR_RES_ACID | Constants.TR_RES_FIRE | Constants.TR_REGEN |
								Constants.TR_STEALTH);
						t_ptr.tohit += 3;
						t_ptr.plusToDam += 3;
						t_ptr.plusToArmorClass  += 5 + Misc1.randomInt(5);
						t_ptr.specialName = Constants.SN_DF;
						/* the value in p1 is used for stealth */
						t_ptr.misc    = Misc1.randomInt(3);
						t_ptr.cost += t_ptr.misc * 500;
						t_ptr.cost += 7500;
						break;
					case 3: case 4:	 /* Slay Animal  */
						t_ptr.flags |= Constants.TR_SLAY_ANIMAL;
						t_ptr.tohit += 2;
						t_ptr.plusToDam += 2;
						t_ptr.specialName = Constants.SN_SA;
						t_ptr.cost += 3000;
						break;
					case 5: case 6:	/* Slay Dragon	 */
						t_ptr.flags |= Constants.TR_SLAY_DRAGON;
						t_ptr.tohit += 3;
						t_ptr.plusToDam += 3;
						t_ptr.specialName = Constants.SN_SD;
						t_ptr.cost += 4000;
						break;
					case 7: case 8:	  /* Slay Evil	   */
						t_ptr.flags |= Constants.TR_SLAY_EVIL;
						t_ptr.tohit += 3;
						t_ptr.plusToDam += 3;
						t_ptr.specialName = Constants.SN_SE;
						t_ptr.cost += 4000;
						break;
					case 9: case 10:	 /* Slay Undead	  */
						t_ptr.flags |= (Constants.TR_SEE_INVIS | Constants.TR_SLAY_UNDEAD);
						t_ptr.tohit += 3;
						t_ptr.plusToDam += 3;
						t_ptr.specialName = Constants.SN_SU;
						t_ptr.cost += 5000;
						break;
					case 11: case 12: case 13:   /* Flame Tongue  */
						t_ptr.flags |= Constants.TR_FLAME_TONGUE;
						t_ptr.tohit++;
						t_ptr.plusToDam += 3;
						t_ptr.specialName = Constants.SN_FT;
						t_ptr.cost += 2000;
						break;
					case 14: case 15: case 16:   /* Frost Brand   */
						t_ptr.flags |= Constants.TR_FROST_BRAND;
						t_ptr.tohit++;
						t_ptr.plusToDam++;
						t_ptr.specialName = Constants.SN_FB;
						t_ptr.cost += 1200;
						break;
					default:
						break;
					}
				}
			} else if (Misc1.magik(cursed)) {
				t_ptr.tohit -= Misc1.m_bonus(1, 55, level);
				/* Magical damage bonus now proportional to weapon base damage */
				tmp = t_ptr.damage[0] * t_ptr.damage[1];
				t_ptr.plusToDam -= Misc1.m_bonus(1, 11 * tmp / 2, tmp * level / 10);
				t_ptr.flags |= Constants.TR_CURSED;
				t_ptr.cost = 0;
			}
			break;
			
		case Constants.TV_BOW:
			/* always show tohit/todam values if identified */
			t_ptr.identify |= Constants.ID_SHOW_HITDAM;
			if (Misc1.magik(chance)) {
				t_ptr.tohit += Misc1.m_bonus(1, 30, level);
				t_ptr.plusToDam += Misc1.m_bonus(1, 20, level); /* add damage. -CJS- */
			} else if (Misc1.magik(cursed)) {
				t_ptr.tohit -= Misc1.m_bonus(1, 50, level);
				t_ptr.plusToDam -= Misc1.m_bonus(1, 30, level); /* add damage. -CJS- */
				t_ptr.flags |= Constants.TR_CURSED;
				t_ptr.cost = 0;
			}
			break;
			
		case Constants.TV_DIGGING:
			/* always show tohit/todam values if identified */
			t_ptr.identify |= Constants.ID_SHOW_HITDAM;
			if (Misc1.magik(chance)) {
				tmp = Misc1.randomInt(3);
				if (tmp < 3) {
					t_ptr.misc += Misc1.m_bonus(0, 25, level);
				} else {
					/* a cursed digging tool */
					t_ptr.misc = -Misc1.m_bonus(1, 30, level);
					t_ptr.cost = 0;
					t_ptr.flags |= Constants.TR_CURSED;
				}
			}
			break;
			
		case Constants.TV_GLOVES:
			if (Misc1.magik(chance)) {
				t_ptr.plusToArmorClass += Misc1.m_bonus(1, 20, level);
				if (Misc1.magik(special)) {
					if (Misc1.randomInt(2) == 1) {
						t_ptr.flags |= Constants.TR_FREE_ACT;
						t_ptr.specialName = Constants.SN_FREE_ACTION;
						t_ptr.cost += 1000;
					} else {
						t_ptr.identify |= Constants.ID_SHOW_HITDAM;
						t_ptr.tohit += 1 + Misc1.randomInt(3);
						t_ptr.plusToDam += 1 + Misc1.randomInt(3);
						t_ptr.specialName = Constants.SN_SLAYING;
						t_ptr.cost += (t_ptr.tohit + t_ptr.plusToDam) * 250;
					}
				}
			} else if (Misc1.magik(cursed)) {
				if (Misc1.magik(special)) {
					if (Misc1.randomInt(2) == 1) {
						t_ptr.flags |= Constants.TR_DEX;
						t_ptr.specialName = Constants.SN_CLUMSINESS;
					} else {
						t_ptr.flags |= Constants.TR_STR;
						t_ptr.specialName = Constants.SN_WEAKNESS;
					}
					t_ptr.identify |= Constants.ID_SHOW_P1;
					t_ptr.misc   = -Misc1.m_bonus(1, 10, level);
				}
				t_ptr.plusToArmorClass -= Misc1.m_bonus(1, 40, level);
				t_ptr.flags |= Constants.TR_CURSED;
				t_ptr.cost = 0;
			}
			break;
			
		case Constants.TV_BOOTS:
			if (Misc1.magik(chance)) {
				t_ptr.plusToArmorClass += Misc1.m_bonus(1, 20, level);
				if (Misc1.magik(special)) {
					tmp = Misc1.randomInt(12);
					if (tmp > 5) {
						t_ptr.flags |= Constants.TR_FFALL;
						t_ptr.specialName = Constants.SN_SLOW_DESCENT;
						t_ptr.cost += 250;
					} else if (tmp == 1) {
						t_ptr.flags |= Constants.TR_SPEED;
						t_ptr.specialName = Constants.SN_SPEED;
						t_ptr.identify |= Constants.ID_SHOW_P1;
						t_ptr.misc = 1;
						t_ptr.cost += 5000;
					} else { /* 2 - 5 */
						t_ptr.flags |= Constants.TR_STEALTH;
						t_ptr.identify |= Constants.ID_SHOW_P1;
						t_ptr.misc = Misc1.randomInt(3);
						t_ptr.specialName = Constants.SN_STEALTH;
						t_ptr.cost += 500;
					}
				}
			} else if (Misc1.magik(cursed)) {
				tmp = Misc1.randomInt(3);
				if (tmp == 1) {
					t_ptr.flags |= Constants.TR_SPEED;
					t_ptr.specialName = Constants.SN_SLOWNESS;
					t_ptr.identify |= Constants.ID_SHOW_P1;
					t_ptr.misc = -1;
				} else if (tmp == 2) {
					t_ptr.flags |= Constants.TR_AGGRAVATE;
					t_ptr.specialName = Constants.SN_NOISE;
				} else {
					t_ptr.specialName = Constants.SN_GREAT_MASS;
					t_ptr.weight = t_ptr.weight * 5;
				}
				t_ptr.cost = 0;
				t_ptr.plusToArmorClass -= Misc1.m_bonus(2, 45, level);
				t_ptr.flags |= Constants.TR_CURSED;
			}
			break;
			
		case Constants.TV_HELM:  /* Helms */
			if ((t_ptr.subCategory >= 6) && (t_ptr.subCategory <= 8)) {
				/* give crowns a higher chance for magic */
				chance += (t_ptr.cost / 100);
				special += special;
			}
			if (Misc1.magik(chance)) {
				t_ptr.plusToArmorClass += Misc1.m_bonus(1, 20, level);
				if (Misc1.magik(special)) {
					if (t_ptr.subCategory < 6) {
						tmp = Misc1.randomInt(3);
						t_ptr.identify |= Constants.ID_SHOW_P1;
						if (tmp == 1) {
							t_ptr.misc = Misc1.randomInt(2);
							t_ptr.flags |= Constants.TR_INT;
							t_ptr.specialName = Constants.SN_INTELLIGENCE;
							t_ptr.cost += t_ptr.misc * 500;
						} else if (tmp == 2) {
							t_ptr.misc = Misc1.randomInt(2);
							t_ptr.flags |= Constants.TR_WIS;
							t_ptr.specialName = Constants.SN_WISDOM;
							t_ptr.cost += t_ptr.misc * 500;
						} else {
							t_ptr.misc = 1 + Misc1.randomInt(4);
							t_ptr.flags |= Constants.TR_INFRA;
							t_ptr.specialName = Constants.SN_INFRAVISION;
							t_ptr.cost += t_ptr.misc * 250;
						}
					} else {
						switch(Misc1.randomInt(6)) {
						case 1:
							t_ptr.identify |= Constants.ID_SHOW_P1;
							t_ptr.misc = Misc1.randomInt(3);
							t_ptr.flags |= (Constants.TR_FREE_ACT | Constants.TR_CON |  Constants.TR_DEX | Constants.TR_STR);
							t_ptr.specialName = Constants.SN_MIGHT;
							t_ptr.cost += 1000 + t_ptr.misc * 500;
							break;
						case 2:
							t_ptr.identify |= Constants.ID_SHOW_P1;
							t_ptr.misc = Misc1.randomInt(3);
							t_ptr.flags |= (Constants.TR_CHR | Constants.TR_WIS);
							t_ptr.specialName = Constants.SN_LORDLINESS;
							t_ptr.cost += 1000 + t_ptr.misc * 500;
							break;
						case 3:
							t_ptr.identify |= Constants.ID_SHOW_P1;
							t_ptr.misc = Misc1.randomInt(3);
							t_ptr.flags |= (Constants.TR_RES_LIGHT | Constants.TR_RES_COLD |  Constants.TR_RES_ACID | Constants.TR_RES_FIRE | Constants.TR_INT);
							t_ptr.specialName = Constants.SN_MAGI;
							t_ptr.cost += 3000 + t_ptr.misc * 500;
							break;
						case 4:
							t_ptr.identify |= Constants.ID_SHOW_P1;
							t_ptr.misc = Misc1.randomInt(3);
							t_ptr.flags |= Constants.TR_CHR;
							t_ptr.specialName = Constants.SN_BEAUTY;
							t_ptr.cost += 750;
							break;
						case 5:
							t_ptr.identify |= Constants.ID_SHOW_P1;
							t_ptr.misc = 5 * (1 + Misc1.randomInt(4));
							t_ptr.flags |= (Constants.TR_SEE_INVIS | Constants.TR_SEARCH);
							t_ptr.specialName = Constants.SN_SEEING;
							t_ptr.cost += 1000 + t_ptr.misc * 100;
							break;
						case 6:
							t_ptr.flags |= Constants.TR_REGEN;
							t_ptr.specialName = Constants.SN_REGENERATION;
							t_ptr.cost += 1500;
							break;
						default:
							break;
						}
					}
				}
			} else if (Misc1.magik(cursed)) {
				t_ptr.plusToArmorClass -= Misc1.m_bonus(1, 45, level);
				t_ptr.flags |= Constants.TR_CURSED;
				t_ptr.cost = 0;
				if (Misc1.magik(special)) {
					switch(Misc1.randomInt(7)) {
					case 1:
						t_ptr.identify |= Constants.ID_SHOW_P1;
						t_ptr.misc = -Misc1.randomInt(5);
						t_ptr.flags |= Constants.TR_INT;
						t_ptr.specialName = Constants.SN_STUPIDITY;
						break;
					case 2:
						t_ptr.identify |= Constants.ID_SHOW_P1;
						t_ptr.misc = -Misc1.randomInt(5);
						t_ptr.flags |= Constants.TR_WIS;
						t_ptr.specialName = Constants.SN_DULLNESS;
						break;
					case 3:
						t_ptr.flags |= Constants.TR_BLIND;
						t_ptr.specialName = Constants.SN_BLINDNESS;
						break;
					case 4:
						t_ptr.flags |= Constants.TR_TIMID;
						t_ptr.specialName = Constants.SN_TIMIDNESS;
						break;
					case 5:
						t_ptr.identify |= Constants.ID_SHOW_P1;
						t_ptr.misc = -Misc1.randomInt(5);
						t_ptr.flags |= Constants.TR_STR;
						t_ptr.specialName = Constants.SN_WEAKNESS;
						break;
					case 6:
						t_ptr.flags |= Constants.TR_TELEPORT;
						t_ptr.specialName = Constants.SN_TELEPORTATION;
						break;
					case 7:
						t_ptr.identify |= Constants.ID_SHOW_P1;
						t_ptr.misc = -Misc1.randomInt(5);
						t_ptr.flags |= Constants.TR_CHR;
						t_ptr.specialName = Constants.SN_UGLINESS;
						break;
					default:
						break;
					}
				}
			}
			break;
			
		case Constants.TV_RING: /* Rings	      */
			switch(t_ptr.subCategory) {
			case 0: case 1: case 2: case 3:
				if (Misc1.magik(cursed)) {
					t_ptr.misc = -Misc1.m_bonus(1, 20, level);
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				} else {
					t_ptr.misc = Misc1.m_bonus(1, 10, level);
					t_ptr.cost += t_ptr.misc * 100;
				}
				break;
			case 4:
				if (Misc1.magik(cursed)) {
					t_ptr.misc = -Misc1.randomInt(3);
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				} else {
					t_ptr.misc = 1;
				}
				break;
			case 5:
				t_ptr.misc = 5 * Misc1.m_bonus(1, 20, level);
				t_ptr.cost += t_ptr.misc * 50;
				if (Misc1.magik(cursed)) {
					t_ptr.misc = -t_ptr.misc;
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				}
				break;
			case 19:     /* Increase damage	      */
				t_ptr.plusToDam += Misc1.m_bonus(1, 20, level);
				t_ptr.cost += t_ptr.plusToDam * 100;
				if (Misc1.magik(cursed)) {
					t_ptr.plusToDam = -t_ptr.plusToDam;
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				}
				break;
			case 20:     /* Increase To-Hit	      */
				t_ptr.tohit += Misc1.m_bonus(1, 20, level);
				t_ptr.cost += t_ptr.tohit * 100;
				if (Misc1.magik(cursed)) {
					t_ptr.tohit = -t_ptr.tohit;
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				}
				break;
			case 21:     /* Protection	      */
				t_ptr.plusToArmorClass += Misc1.m_bonus(1, 20, level);
				t_ptr.cost += t_ptr.plusToArmorClass * 100;
				if (Misc1.magik(cursed)) {
					t_ptr.plusToArmorClass = -t_ptr.plusToArmorClass;
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				}
				break;
			case 24: case 25: case 26:
			case 27: case 28: case 29:
				t_ptr.identify |= Constants.ID_NOSHOW_P1;
				break;
			case 30:     /* Slaying	      */
				t_ptr.identify |= Constants.ID_SHOW_HITDAM;
				t_ptr.plusToDam += Misc1.m_bonus(1, 25, level);
				t_ptr.tohit += Misc1.m_bonus(1, 25, level);
				t_ptr.cost += (t_ptr.tohit + t_ptr.plusToDam) * 100;
				if (Misc1.magik(cursed)) {
					t_ptr.tohit = -t_ptr.tohit;
					t_ptr.plusToDam = -t_ptr.plusToDam;
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				}
				break;
			default:
				break;
			}
			break;
			
		case Constants.TV_AMULET: /* Amulets	      */
			if (t_ptr.subCategory < 2) {
				if (Misc1.magik(cursed)) {
					t_ptr.misc = -Misc1.m_bonus(1, 20, level);
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = -t_ptr.cost;
				} else {
					t_ptr.misc = Misc1.m_bonus(1, 10, level);
					t_ptr.cost += t_ptr.misc * 100;
				}
			} else if (t_ptr.subCategory == 2) {
				t_ptr.misc = 5 * Misc1.m_bonus(1, 25, level);
				if (Misc1.magik(cursed)) {
					t_ptr.misc = -t_ptr.misc;
					t_ptr.cost = -t_ptr.cost;
					t_ptr.flags |= Constants.TR_CURSED;
				} else {
					t_ptr.cost += 50 * t_ptr.misc;
				}
			} else if (t_ptr.subCategory == 8) {
				/* amulet of the magi is never cursed */
				t_ptr.misc = 5 * Misc1.m_bonus(1, 25, level);
				t_ptr.cost += 20 * t_ptr.misc;
			}
			break;
			
			/* Subval should be even for store, odd for dungeon*/
			/* Dungeon found ones will be partially charged	 */
		case Constants.TV_LIGHT:
			if ((t_ptr.subCategory % 2) != 0) {
				t_ptr.misc = Misc1.randomInt(t_ptr.misc);
				t_ptr.subCategory -= 1;
			}
			break;
			
		case Constants.TV_WAND:
			switch(t_ptr.subCategory) {
			case 0:	  t_ptr.misc = Misc1.randomInt(10) +	 6; break;
			case 1:	  t_ptr.misc = Misc1.randomInt(8)  +	 6; break;
			case 2:	  t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 3:	  t_ptr.misc = Misc1.randomInt(8)  +	 6; break;
			case 4:	  t_ptr.misc = Misc1.randomInt(4)  +	 3; break;
			case 5:	  t_ptr.misc = Misc1.randomInt(8)  +	 6; break;
			case 6:	  t_ptr.misc = Misc1.randomInt(20) +	 12; break;
			case 7:	  t_ptr.misc = Misc1.randomInt(20) +	 12; break;
			case 8:	  t_ptr.misc = Misc1.randomInt(10) +	 6; break;
			case 9:	  t_ptr.misc = Misc1.randomInt(12) +	 6; break;
			case 10:   t_ptr.misc = Misc1.randomInt(10) +	 12; break;
			case 11:   t_ptr.misc = Misc1.randomInt(3)  +	 3; break;
			case 12:   t_ptr.misc = Misc1.randomInt(8)  +	 6; break;
			case 13:   t_ptr.misc = Misc1.randomInt(10) +	 6; break;
			case 14:   t_ptr.misc = Misc1.randomInt(5)  +	 3; break;
			case 15:   t_ptr.misc = Misc1.randomInt(5)  +	 3; break;
			case 16:   t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 17:   t_ptr.misc = Misc1.randomInt(5)  +	 4; break;
			case 18:   t_ptr.misc = Misc1.randomInt(8)  +	 4; break;
			case 19:   t_ptr.misc = Misc1.randomInt(6)  +	 2; break;
			case 20:   t_ptr.misc = Misc1.randomInt(4)  +	 2; break;
			case 21:   t_ptr.misc = Misc1.randomInt(8)  +	 6; break;
			case 22:   t_ptr.misc = Misc1.randomInt(5)  +	 2; break;
			case 23:   t_ptr.misc = Misc1.randomInt(12) + 12; break;
			default:
				break;
			}
			break;
			
		case Constants.TV_STAFF:
			switch(t_ptr.subCategory) {
			case 0:	  t_ptr.misc = Misc1.randomInt(20) +	 12; break;
			case 1:	  t_ptr.misc = Misc1.randomInt(8)  +	 6; break;
			case 2:	  t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 3:	  t_ptr.misc = Misc1.randomInt(20) +	 12; break;
			case 4:	  t_ptr.misc = Misc1.randomInt(15) +	 6; break;
			case 5:	  t_ptr.misc = Misc1.randomInt(4)  +	 5; break;
			case 6:	  t_ptr.misc = Misc1.randomInt(5)  +	 3; break;
			case 7:	  t_ptr.misc = Misc1.randomInt(3)  +	 1;
			t_ptr.level = 10;
			break;
			case 8:	  t_ptr.misc = Misc1.randomInt(3)  +	 1; break;
			case 9:	  t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 10:   t_ptr.misc = Misc1.randomInt(10) +	 12; break;
			case 11:   t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 12:   t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 13:   t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 14:   t_ptr.misc = Misc1.randomInt(10) +	 12; break;
			case 15:   t_ptr.misc = Misc1.randomInt(3)  +	 4; break;
			case 16:   t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 17:   t_ptr.misc = Misc1.randomInt(5)  +	 6; break;
			case 18:   t_ptr.misc = Misc1.randomInt(3)  +	 4; break;
			case 19:   t_ptr.misc = Misc1.randomInt(10) +	 12; break;
			case 20:   t_ptr.misc = Misc1.randomInt(3)  +	 4; break;
			case 21:   t_ptr.misc = Misc1.randomInt(3)  +	 4; break;
			case 22:   t_ptr.misc = Misc1.randomInt(10) + 6;
			t_ptr.level = 5;
			break;
			default:
				break;
			}
			break;
			
		case Constants.TV_CLOAK:
			if (Misc1.magik(chance)) {
				if (Misc1.magik(special)) {
					if (Misc1.randomInt(2) == 1) {
						t_ptr.specialName = Constants.SN_PROTECTION;
						t_ptr.plusToArmorClass += Misc1.m_bonus(2, 40, level);
						t_ptr.cost += 250;
					} else {
						t_ptr.plusToArmorClass += Misc1.m_bonus(1, 20, level);
						t_ptr.identify |= Constants.ID_SHOW_P1;
						t_ptr.misc = Misc1.randomInt(3);
						t_ptr.flags |= Constants.TR_STEALTH;
						t_ptr.specialName = Constants.SN_STEALTH;
						t_ptr.cost += 500;
					}
				} else {
					t_ptr.plusToArmorClass += Misc1.m_bonus(1, 20, level);
				}
			} else if (Misc1.magik(cursed)) {
				tmp = Misc1.randomInt(3);
				if (tmp == 1) {
					t_ptr.flags |= Constants.TR_AGGRAVATE;
					t_ptr.specialName = Constants.SN_IRRITATION;
					t_ptr.plusToArmorClass  -= Misc1.m_bonus(1, 10, level);
					t_ptr.identify |= Constants.ID_SHOW_HITDAM;
					t_ptr.tohit -= Misc1.m_bonus(1, 10, level);
					t_ptr.plusToDam -= Misc1.m_bonus(1, 10, level);
					t_ptr.cost =  0;
				} else if (tmp == 2) {
					t_ptr.specialName = Constants.SN_VULNERABILITY;
					t_ptr.plusToArmorClass -= Misc1.m_bonus(10, 100, level + 50);
					t_ptr.cost = 0;
				} else {
					t_ptr.specialName = Constants.SN_ENVELOPING;
					t_ptr.plusToArmorClass  -= Misc1.m_bonus(1, 10, level);
					t_ptr.identify |= Constants.ID_SHOW_HITDAM;
					t_ptr.tohit -= Misc1.m_bonus(2, 40, level+10);
					t_ptr.plusToDam -= Misc1.m_bonus(2, 40, level+10);
					t_ptr.cost = 0;
				}
				t_ptr.flags |= Constants.TR_CURSED;
			}
			break;
			
		case Constants.TV_CHEST:
			switch(Misc1.randomInt(level + 4)) {
			case 1:
				t_ptr.flags = 0;
				t_ptr.specialName = Constants.SN_EMPTY;
				break;
			case 2:
				t_ptr.flags |= Constants.CH_LOCKED;
				t_ptr.specialName = Constants.SN_LOCKED;
				break;
			case 3: case 4:
				t_ptr.flags |= (Constants.CH_LOSE_STR | Constants.CH_LOCKED);
				t_ptr.specialName = Constants.SN_POISON_NEEDLE;
				break;
			case 5: case 6:
				t_ptr.flags |= (Constants.CH_POISON | Constants.CH_LOCKED);
				t_ptr.specialName = Constants.SN_POISON_NEEDLE;
				break;
			case 7: case 8: case 9:
				t_ptr.flags |= (Constants.CH_PARALYSED | Constants.CH_LOCKED);
				t_ptr.specialName = Constants.SN_GAS_TRAP;
				break;
			case 10: case 11:
				t_ptr.flags |= (Constants.CH_EXPLODE | Constants.CH_LOCKED);
				t_ptr.specialName = Constants.SN_EXPLOSION_DEVICE;
				break;
			case 12: case 13: case 14:
				t_ptr.flags |= (Constants.CH_SUMMON | Constants.CH_LOCKED);
				t_ptr.specialName = Constants.SN_SUMMONING_RUNES;
				break;
			case 15: case 16: case 17:
				t_ptr.flags |= (Constants.CH_PARALYSED | Constants.CH_POISON | Constants.CH_LOSE_STR | Constants.CH_LOCKED);
				t_ptr.specialName = Constants.SN_MULTIPLE_TRAPS;
				break;
			default:
				t_ptr.flags |= (Constants.CH_SUMMON | Constants.CH_EXPLODE | Constants.CH_LOCKED);
				t_ptr.specialName = Constants.SN_MULTIPLE_TRAPS;
				break;
			}
			break;
			
		case Constants.TV_SLING_AMMO: case Constants.TV_SPIKE:
		case Constants.TV_BOLT: case Constants.TV_ARROW:
			if (t_ptr.category == Constants.TV_SLING_AMMO || t_ptr.category == Constants.TV_BOLT || t_ptr.category == Constants.TV_ARROW) {
				/* always show tohit/todam values if identified */
				t_ptr.identify |= Constants.ID_SHOW_HITDAM;
				
				if (Misc1.magik(chance)) {
					t_ptr.tohit += Misc1.m_bonus(1, 35, level);
					t_ptr.plusToDam += Misc1.m_bonus(1, 35, level);
					/* see comment for weapons */
					if (Misc1.magik(3 * special / 2)) {
						switch(Misc1.randomInt(10)) {
						case 1: case 2: case 3:
							t_ptr.specialName = Constants.SN_SLAYING;
							t_ptr.tohit += 5;
							t_ptr.plusToDam += 5;
							t_ptr.cost += 20;
							break;
						case 4: case 5:
							t_ptr.flags |= Constants.TR_FLAME_TONGUE;
							t_ptr.tohit += 2;
							t_ptr.plusToDam += 4;
							t_ptr.specialName = Constants.SN_FIRE;
							t_ptr.cost += 25;
							break;
						case 6: case 7:
							t_ptr.flags |= Constants.TR_SLAY_EVIL;
							t_ptr.tohit += 3;
							t_ptr.plusToDam += 3;
							t_ptr.specialName = Constants.SN_SLAY_EVIL;
							t_ptr.cost += 25;
							break;
						case 8: case 9:
							t_ptr.flags |= Constants.TR_SLAY_ANIMAL;
							t_ptr.tohit += 2;
							t_ptr.plusToDam += 2;
							t_ptr.specialName = Constants.SN_SLAY_ANIMAL;
							t_ptr.cost += 30;
							break;
						case 10:
							t_ptr.flags |= Constants.TR_SLAY_DRAGON;
							t_ptr.tohit += 3;
							t_ptr.plusToDam += 3;
							t_ptr.specialName = Constants.SN_DRAGON_SLAYING;
							t_ptr.cost += 35;
							break;
						default:
							break;
						}
					}
				} else if (Misc1.magik(cursed)) {
					t_ptr.tohit -= Misc1.m_bonus(5, 55, level);
					t_ptr.plusToDam -= Misc1.m_bonus(5, 55, level);
					t_ptr.flags |= Constants.TR_CURSED;
					t_ptr.cost = 0;
				}
			}
			
			t_ptr.number = 0;
			for (i = 0; i < 7; i++) {
				t_ptr.number += Misc1.randomInt(6);
			}
			if (Variable.missileCounter == Constants.MAX_SHORT) {
				Variable.missileCounter = -Constants.MAX_SHORT - 1;
			} else {
				Variable.missileCounter++;
			}
			t_ptr.misc = Variable.missileCounter;
			break;
			
		case Constants.TV_FOOD:
			/* make sure all food rations have the same level */
			if (t_ptr.subCategory == 90) {
				t_ptr.level = 0;
				/* give all elvish waybread the same level */
			} else if (t_ptr.subCategory == 92) {
				t_ptr.level = 6;
			}
			break;
			
		case Constants.TV_SCROLL1:
			/* give all identify scrolls the same level */
			if (t_ptr.subCategory == 67) {
				t_ptr.level = 1;
				/* scroll of light */
			} else if (t_ptr.subCategory == 69) {
				t_ptr.level = 0;
				/* scroll of trap detection */
			} else if (t_ptr.subCategory == 80) {
				t_ptr.level = 5;
				/* scroll of door/stair location */
			} else if (t_ptr.subCategory == 81) {
				t_ptr.level = 5;
			}
			break;
			
		case Constants.TV_POTION1:  /* potions */
			/* cure light */
			if (t_ptr.subCategory == 76) {
				t_ptr.level = 0;
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
	
	/* Set or unset various boolean options.		-CJS- */
	public static void setOptions() {
		int i, max;
		String string;
		
		IO.print("  ESC when finished, y/n to set options, <return> or - to move cursor", 0, 0);
		for (max = 0; !options[max].optionPrompt.isEmpty(); max++) {
			string = String.format("%-38s: %s", options[max].optionPrompt, (options[max].isActive.value() ? "yes" : "no "));
			IO.print(string, max + 1, 0);
		}
		IO.eraseLine(max + 1, 0);
		i = 0;
		for(;;) {
			IO.moveCursor(i + 1, 40);
			switch(IO.inkey())
			{
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
