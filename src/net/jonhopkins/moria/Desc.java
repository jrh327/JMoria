/* 
 * Desc.java: handle object descriptions, mostly string handling code
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

public class Desc {
	
	private static String[] titles = new String[Constants.MAX_TITLES];
	
	private Desc() { }
	
	/* Object descriptor routines					*/
	
	/**
	 * Check if a character is a vowel
	 * 
	 * @param ch - Character being checked
	 * @return True if ch is a vowel
	 */
	public static boolean isVowel(char ch)  {
		switch (ch) {
		case 'a': case 'e': case 'i': case 'o': case 'u':
		case 'A': case 'E': case 'I': case 'O': case 'U':
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Initialize all Potions, wands, staves, scrolls, etc.
	 */
	public static void magicInit() {
		int h, i, j, k;
		String tmp;
		StringBuilder string;
		
		Misc1.setSeed(Variable.randesSeed);
		
		/* The first 3 entries for colors are fixed, (slime & apple juice, water) */
		for (i = 3; i < Constants.MAX_COLORS; i++) {
			j = Misc1.randomInt(Constants.MAX_COLORS - 3) + 2;
			tmp = Tables.colors[i];
			Tables.colors[i] = Tables.colors[j];
			Tables.colors[j] = tmp;
		}
		for (i = 0; i < Constants.MAX_WOODS; i++) {
			j = Misc1.randomInt(Constants.MAX_WOODS) - 1;
			tmp = Tables.woods[i];
			Tables.woods[i] = Tables.woods[j];
			Tables.woods[j] = tmp;
		}
		for (i = 0; i < Constants.MAX_METALS; i++) {
			j = Misc1.randomInt(Constants.MAX_METALS) - 1;
			tmp = Tables.metals[i];
			Tables.metals[i] = Tables.metals[j];
			Tables.metals[j] = tmp;
		}
		for (i = 0; i < Constants.MAX_ROCKS; i++) {
			j = Misc1.randomInt(Constants.MAX_ROCKS) - 1;
			tmp = Tables.rocks[i];
			Tables.rocks[i] = Tables.rocks[j];
			Tables.rocks[j] = tmp;
		}
		for (i = 0; i < Constants.MAX_AMULETS; i++) {
			j = Misc1.randomInt(Constants.MAX_AMULETS) - 1;
			tmp = Tables.amulets[i];
			Tables.amulets[i] = Tables.amulets[j];
			Tables.amulets[j] = tmp;
		}
		for (i = 0; i < Constants.MAX_MUSH; i++) {
			j = Misc1.randomInt(Constants.MAX_MUSH) - 1;
			tmp = Tables.mushrooms[i];
			Tables.mushrooms[i] = Tables.mushrooms[j];
			Tables.mushrooms[j] = tmp;
		}
		for (h = 0; h < Constants.MAX_TITLES; h++) {
			string = new StringBuilder(10);
			k = Misc1.randomInt(2) + 1;
			for (i = 0; i < k; i++) {
				for (j = Misc1.randomInt(2); j > 0; j--) {
					string = string.append(Tables.syllables[Misc1.randomInt(Constants.MAX_SYLLABLES) - 1]);
				}
				if (i < k - 1) {
					string = string.append(' ');
				}
			}
			int len = 9;
			if (len > string.length()) {
				len = string.length();
			}
			if (string.charAt(len - 1) == ' ') {
				string.setLength(len - 1);
			}
			titles[h] = string.toString();
		}
		Misc1.resetSeed();
	}
	
	/**
	 * Get the item type.
	 * 
	 * @param t_ptr - Item being checked
	 * @return Item type
	 */
	public static int getObjectOffset(InvenType t_ptr) {
		switch (t_ptr.category) {
		case Constants.TV_AMULET:  return 0;
		case Constants.TV_RING:    return 1;
		case Constants.TV_STAFF:   return 2;
		case Constants.TV_WAND:    return 3;
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2: return 4;
		case Constants.TV_POTION1:
		case Constants.TV_POTION2: return 5;
		case Constants.TV_FOOD:
			if ((t_ptr.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1)) < Constants.MAX_MUSH) {
				return 6;
			}
			return -1;
		default:
			return -1;
		}
	}
	
	/**
	 * Remove "Secret" symbol for identity of object
	 * 
	 * @param i_ptr - Item being identified
	 */
	public static void identifyItem(InvenType i_ptr) {
		int offset;
		int indexx;
		
		offset = getObjectOffset(i_ptr);
		if (offset < 0) {
			return;
		}
		offset <<= 6;
		indexx = i_ptr.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		Treasure.objectIdent[offset + indexx] |= Constants.OD_KNOWN1;
		/* clear the tried flag, since it is now known */
		Treasure.objectIdent[offset + indexx] &= ~Constants.OD_TRIED;
	}
	
	/**
	 * Check if the player knows what the item is.
	 * 
	 * @param i_ptr - Item being checked
	 * @return True if the item is known, otherwise false
	 */
	public static boolean isKnownByPlayer(InvenType i_ptr) {
		int offset;
		int indexx;
		
		/* Items which don't have a 'color' are always known1, so that they can
		 * be carried in order in the inventory.  */
		offset = getObjectOffset(i_ptr);
		if (offset < 0) {
			return true;
		}
		if (isStoreBought(i_ptr)) {
			return true;
		}
		offset <<= 6;
		indexx = i_ptr.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		return Treasure.objectIdent[offset + indexx] != 0;
	}
	
	/* Remove "Secret" symbol for identity of plusses			*/
	public static void identifyItemPlusses(InvenType i_ptr) {
		unsample(i_ptr);
		i_ptr.identify |= Constants.ID_KNOWN2;
	}
	
	public static boolean arePlussesKnownByPlayer(InvenType i_ptr) {
		return (i_ptr.identify & Constants.ID_KNOWN2) != 0;
	}
	
	public static void clearPlussesIdentity(InvenType i_ptr) {
		i_ptr.identify &= ~Constants.ID_KNOWN2;
	}
	
	public static void clearEmpty(InvenType i_ptr) {
		i_ptr.identify &= ~Constants.ID_EMPTY;
	}
	
	public static void setStoreBought(InvenType i_ptr) {
		i_ptr.identify |= Constants.ID_STOREBOUGHT;
		identifyItemPlusses(i_ptr);
	}
	
	public static boolean isStoreBought(InvenType i_ptr) {
		return (i_ptr.identify & Constants.ID_STOREBOUGHT) != 0;
	}
	
	/*	Remove an automatically generated inscription.	-CJS- */
	public static void unsample(InvenType i_ptr) {
		int offset;
		int indexx;
		
		/* used to clear Constants.ID_DAMD flag, but I think it should remain set */
		i_ptr.identify &= ~(Constants.ID_MAGIK|Constants.ID_EMPTY);
		offset = getObjectOffset(i_ptr);
		if (offset < 0) {
			return;
		}
		offset <<= 6;
		indexx = i_ptr.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		Treasure.objectIdent[offset + indexx] &= ~Constants.OD_TRIED;
	}
	
	/* unquote() is no longer needed */
	
	/* Somethings been sampled -CJS- */
	public static void sample(InvenType i_ptr) {
		int offset;
		int indexx;
		
		offset = getObjectOffset(i_ptr);
		if (offset < 0) {
			return;
		}
		offset <<= 6;
		indexx = i_ptr.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		Treasure.objectIdent[offset + indexx] |= Constants.OD_TRIED;
	}
	
	/* Somethings been identified					*/
	/* extra complexity by CJS so that it can merge store/dungeon objects
	 * when appropriate */
	public static void identify(IntPointer item) {
		int i, x1, x2;
		int j;
		InvenType i_ptr, t_ptr;
		
		i_ptr = Treasure.inventory[item.value()];
		
		if ((i_ptr.flags & Constants.TR_CURSED) != 0) {
			Misc4.addInscription(i_ptr, Constants.ID_DAMD);
		}
		
		if (!isKnownByPlayer(i_ptr)) {
			identifyItem(i_ptr);
			x1 = i_ptr.category;
			x2 = i_ptr.subCategory;
			if (x2 >= Constants.ITEM_SINGLE_STACK_MIN && x2 < Constants.ITEM_GROUP_MIN) {
				for (i = 0; i < Treasure.invenCounter; i++) {
					t_ptr = Treasure.inventory[i];
					if (t_ptr.category == x1 && t_ptr.subCategory == x2 && i != item.value() && (t_ptr.number + i_ptr.number < 256)) {
						/* make *item the smaller number */
						if (item.value() > i) {
							j = item.value(); item.value(i);
							i = j;
						}
						IO.printMessage("You combine similar objects from the shop and dungeon.");
						
						Treasure.inventory[item.value()].number += Treasure.inventory[i].number;
						Treasure.invenCounter--;
						for (j = i; j < Treasure.invenCounter; j++) {
							Treasure.inventory[j] = Treasure.inventory[j + 1];
						}
						copyIntoInventory(Treasure.inventory[j], Constants.OBJ_NOTHING);
					}
				}
			}
		}
	}
	
	/* If an object has lost magical properties,
	 * remove the appropriate portion of the name.	       -CJS-
	 */
	public static void unmagicName(InvenType i_ptr) {
		i_ptr.specialName = Constants.SN_NULL;
	}
	
	/* defines for p1_use, determine how the p1 field is printed */
	private static final int IGNORED   = 0;
	private static final int CHARGES   = 1;
	private static final int PLUSSES   = 2;
	private static final int LIGHT     = 3;
	private static final int FLAGS     = 4;
	private static final int Z_PLUSSES = 5;
	
	/* Returns a description of item for inventory			*/
	/* pref indicates that there should be an article added (prefix) */
	/* note that since out_val can easily exceed 80 characters, objdes must
	 * always be called with a bigvtype as the first parameter */
	public static String describeObject(InvenType i_ptr, boolean pref) {
		/* base name, modifier string*/
		String basenm, modstr;
		StringBuilder tmp_val = new StringBuilder();
		String tmp_str, damstr;
		StringBuilder out_val = new StringBuilder();
		int indexx, p1_use, tmp;
		boolean modify, append_name;
		
		indexx = i_ptr.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		basenm = Treasure.objectList[i_ptr.index].name;
		modstr = "";
		damstr = "";
		p1_use = IGNORED;
		modify = !isKnownByPlayer(i_ptr);
		append_name = false;
		switch(i_ptr.category)
		{
		case Constants.TV_MISC:
		case Constants.TV_CHEST:
			break;
		case Constants.TV_SLING_AMMO:
		case Constants.TV_BOLT:
		case Constants.TV_ARROW:
			damstr = String.format(" (%dd%d)", i_ptr.damage[0], i_ptr.damage[1]);
			break;
		case Constants.TV_LIGHT:
			p1_use = LIGHT;
			break;
		case Constants.TV_SPIKE:
			break;
		case Constants.TV_BOW:
			if (i_ptr.misc == 1 || i_ptr.misc == 2) {
				tmp = 2;
			} else if (i_ptr.misc == 3 || i_ptr.misc == 5) {
				tmp = 3;
			} else if (i_ptr.misc == 4 || i_ptr.misc == 6) {
				tmp = 4;
			} else {
				tmp = -1;
			}
			damstr = String.format(" (x%d)", tmp);
			break;
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_SWORD:
			damstr = String.format(" (%dd%d)", i_ptr.damage[0], i_ptr.damage[1]);
			p1_use = FLAGS;
			break;
		case Constants.TV_DIGGING:
			p1_use = Z_PLUSSES;
			damstr = String.format(" (%dd%d)", i_ptr.damage[0], i_ptr.damage[1]);
			break;
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:
		case Constants.TV_HELM:
		case Constants.TV_SHIELD:
		case Constants.TV_HARD_ARMOR:
		case Constants.TV_SOFT_ARMOR:
			break;
		case Constants.TV_AMULET:
			if (modify) {
				basenm = "& %s Amulet";
				modstr = Tables.amulets[indexx];
			} else {
				basenm = "& Amulet";
				append_name = true;
			}
			p1_use = PLUSSES;
			break;
		case Constants.TV_RING:
			if (modify) {
				basenm = "& %s Ring";
				modstr = Tables.rocks[indexx];
			} else {
				basenm = "& Ring";
				append_name = true;
			}
			p1_use = PLUSSES;
			break;
		case Constants.TV_STAFF:
			if (modify) {
				basenm = "& %s Staff";
				modstr = Tables.woods[indexx];
			} else {
				basenm = "& Staff";
				append_name = true;
			}
			p1_use = CHARGES;
			break;
		case Constants.TV_WAND:
			if (modify) {
				basenm = "& %s Wand";
				modstr = Tables.metals[indexx];
			} else {
				basenm = "& Wand";
				append_name = true;
			}
			p1_use = CHARGES;
			break;
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
			if (modify) {
				basenm =  "& Scroll~ titled \"%s\"";
				modstr = titles[indexx];
			} else {
				basenm = "& Scroll~";
				append_name = true;
			}
			break;
		case Constants.TV_POTION1:
		case Constants.TV_POTION2:
			if (modify) {
				basenm = "& %s Potion~";
				modstr = Tables.colors[indexx];
			} else {
				basenm = "& Potion~";
				append_name = true;
			}
			break;
		case Constants.TV_FLASK:
			break;
		case Constants.TV_FOOD:
			if (modify) {
				if (indexx <= 15) {
					basenm = "& %s Mushroom~";
				} else if (indexx <= 20) {
					basenm = "& Hairy %s Mold~";
				}
				if (indexx <= 20) {
					modstr = Tables.mushrooms[indexx];
				}
			} else {
				append_name = true;
				if (indexx <= 15) {
					basenm = "& Mushroom~";
				} else if (indexx <= 20) {
					basenm = "& Hairy Mold~";
				} else {
					/* Ordinary food does not have a name appended.  */
					append_name = false;
				}
			}
			break;
		case Constants.TV_MAGIC_BOOK:
			modstr = basenm;
			basenm = "& Book~ of Magic Spells %s";
			break;
		case Constants.TV_PRAYER_BOOK:
			modstr = basenm;
			basenm = "& Holy Book~ of Prayers %s";
			break;
		case Constants.TV_OPEN_DOOR:
		case Constants.TV_CLOSED_DOOR:
		case Constants.TV_SECRET_DOOR:
		case Constants.TV_RUBBLE:
			break;
		case Constants.TV_GOLD:
		case Constants.TV_INVIS_TRAP:
		case Constants.TV_VIS_TRAP:
		case Constants.TV_UP_STAIR:
		case Constants.TV_DOWN_STAIR:
			out_val.append(Treasure.objectList[i_ptr.index].name).append('.');
			return out_val.toString();
		case Constants.TV_STORE_DOOR:
			out_val.append(String.format("the entrance to the %s.", Treasure.objectList[i_ptr.index].name));
			return out_val.toString();
		default:
			out_val.append("Error in objdes()");
			return out_val.toString();
		}
		if (!modstr.isEmpty()) {
			tmp_val.append(String.format(basenm, modstr));
		} else {
			tmp_val.append(basenm);
		}
		if (append_name) {
			tmp_val.append(" of ").append(Treasure.objectList[i_ptr.index].name);
		}
		if (i_ptr.number != 1) {
			int start = tmp_val.indexOf("ch~");
			tmp_val.replace(start, start + 3, "ches");
			start = tmp_val.indexOf("~");
			tmp_val.replace(start, start + 1, "s");
		} else {
			int start = tmp_val.indexOf("~");
			tmp_val.replace(start, start + 1, "");
		}
		if (!pref) {
			if (!tmp_val.substring(0, 4).equals("some")) {
				out_val.append(tmp_val.substring(5));
			} else if (tmp_val.charAt(0) == '&') {
				/* eliminate the '& ' at the beginning */
				out_val.append(tmp_val.substring(2));
			} else {
				out_val.append(tmp_val);
			}
		} else {
			if (i_ptr.specialName != Constants.SN_NULL && arePlussesKnownByPlayer(i_ptr)) {
				tmp_val.append(' ').append(Treasure.specialNames[i_ptr.specialName]);
			}
			if (!damstr.isEmpty()) {
				tmp_val.append(damstr);
			}
			if (arePlussesKnownByPlayer(i_ptr)) {
				/* originally used %+d, but several machines don't support it */
				if ((i_ptr.identify & Constants.ID_SHOW_HITDAM) != 0) {
					tmp_str = String.format(" (%c%d,%c%d)", (i_ptr.tohit < 0) ? '-' : '+', Math.abs(i_ptr.tohit), (i_ptr.plusToDam < 0) ? '-' : '+', Math.abs(i_ptr.plusToDam));
				} else if (i_ptr.tohit != 0) {
					tmp_str = String.format(" (%c%d)", (i_ptr.tohit < 0) ? '-' : '+', Math.abs(i_ptr.tohit));
				} else if (i_ptr.plusToDam != 0) {
					tmp_str = String.format(" (%c%d)", (i_ptr.plusToDam < 0) ? '-' : '+', Math.abs(i_ptr.plusToDam));
				} else {
					tmp_str = "";
				}
				tmp_val.append(tmp_str);
			}
			/* Crowns have a zero base AC, so make a special test for them. */
			if (i_ptr.armorClass != 0 || (i_ptr.category == Constants.TV_HELM)) {
				tmp_str = String.format(" [%d", i_ptr.armorClass);
				tmp_val.append(tmp_str);
				if (arePlussesKnownByPlayer(i_ptr)) {
					/* originally used %+d, but several machines don't support it */
					tmp_str = String.format(",%c%d", (i_ptr.plusToArmorClass < 0) ? '-' : '+', Math.abs(i_ptr.plusToArmorClass));
					tmp_val.append(tmp_str);
				}
				tmp_val.append(']');
			} else if ((i_ptr.plusToArmorClass != 0) && arePlussesKnownByPlayer(i_ptr)) {
				/* originally used %+d, but several machines don't support it */
				tmp_str = String.format(" [%c%d]", (i_ptr.plusToArmorClass < 0) ? '-' : '+', Math.abs(i_ptr.plusToArmorClass));
				tmp_val.append(tmp_str);
			}
			
			/* override defaults, check for p1 flags in the ident field */
			if ((i_ptr.identify & Constants.ID_NOSHOW_P1) != 0) {
				p1_use = IGNORED;
			} else if ((i_ptr.identify & Constants.ID_SHOW_P1) != 0) {
				p1_use = Z_PLUSSES;
			}
			tmp_str = "";
			if (p1_use == LIGHT) {
				tmp_str = String.format(" with %d turns of light", i_ptr.misc);
			} else if (p1_use != IGNORED && arePlussesKnownByPlayer(i_ptr)) {
				if (p1_use == Z_PLUSSES) {
					/* originally used %+d, but several machines don't support it */
					tmp_str = String.format(" (%c%d)", (i_ptr.misc < 0) ? '-' : '+', Math.abs(i_ptr.misc));
				} else if (p1_use == CHARGES) {
					tmp_str = String.format(" (%d charges)", i_ptr.misc);
				} else if (i_ptr.misc != 0) {
					if (p1_use == PLUSSES) {
						tmp_str = String.format(" (%c%d)", (i_ptr.misc < 0) ? '-' : '+', Math.abs(i_ptr.misc));
					} else if (p1_use == FLAGS) {
						if ((i_ptr.flags & Constants.TR_STR) != 0) {
							tmp_str = String.format(" (%c%d to STR)", (i_ptr.misc < 0) ? '-' : '+', Math.abs(i_ptr.misc));
						} else if ((i_ptr.flags & Constants.TR_STEALTH) != 0) {
							tmp_str = String.format(" (%c%d to stealth)", (i_ptr.misc < 0) ? '-' : '+', Math.abs(i_ptr.misc));
						}
					}
				}
			}
			tmp_val.append(tmp_str);
			
			/* ampersand is always the first character */
			if (tmp_val.charAt(0) == '&') {
				/* use &tmp_val[1], so that & does not appear in output */
				if (i_ptr.number > 1) {
					out_val.append(String.format("%d%s", i_ptr.number, tmp_val.substring(1)));
				} else if (i_ptr.number < 1) {
					out_val.append(String.format("%s%s", "no more", tmp_val.substring(1)));
				} else if (isVowel(tmp_val.charAt(2))) {
					out_val.append(String.format("an%s", tmp_val.substring(1)));
				} else {
					out_val.append(String.format("a%s", tmp_val.substring(1)));
				}
			} else if (i_ptr.number < 1) {
			/* handle 'no more' case specially */
				/* check for "some" at start */
				if (!tmp_val.substring(0, 4).equals("some")) {
					out_val.append(String.format("no more %s", tmp_val.substring(5)));
				/* here if no article */
				} else {
					out_val.append(String.format("no more %s", tmp_val.toString()));
				}
			} else {
				out_val = tmp_val;
			}
			
			tmp_str = "";
			if ((indexx = getObjectOffset(i_ptr)) >= 0) {
				indexx = (indexx << 6) + (i_ptr.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1));
				/* don't print tried string for store bought items */
				if ((Treasure.objectIdent[indexx] & Constants.OD_TRIED) != 0 && !isStoreBought(i_ptr)) {
					tmp_str = tmp_str.concat("tried ");
				}
			}
			if ((i_ptr.identify & (Constants.ID_MAGIK|Constants.ID_EMPTY|Constants.ID_DAMD)) != 0) {
				if ((i_ptr.identify & Constants.ID_MAGIK) != 0) {
					tmp_str = tmp_str.concat("magik ");
				}
				if ((i_ptr.identify & Constants.ID_EMPTY) != 0) {
					tmp_str = tmp_str.concat("empty ");
				}
				if ((i_ptr.identify & Constants.ID_DAMD) != 0) {
					tmp_str = tmp_str.concat("damned ");
				}
			}
			if (!i_ptr.inscription.equals("")) {
				tmp_str = tmp_str.concat(i_ptr.inscription);
			} else if ((indexx = tmp_str.length()) > 0) {
				/* remove the extra blank at the end */
				tmp_str = tmp_str.substring(0, indexx - 1);
			}
			if (!tmp_str.isEmpty()) {
				out_val.append(String.format(" {%s}", tmp_str));
			}
			out_val = out_val.append('.');
		}
		return out_val.toString();
	}
	
	public static void copyIntoInventory(InvenType to, int from_index) {
		Treasure.objectList[from_index].copyInto(to);
		to.index = from_index;
	}
	
	/* Describe number of remaining charges.		-RAK-	*/
	public static void describeCharges(int item_val) {
		int rem_num;
		String out_val;
		
		if (arePlussesKnownByPlayer(Treasure.inventory[item_val])) {
			rem_num = Treasure.inventory[item_val].misc;
			out_val = String.format("You have %d charges remaining.", rem_num);
			IO.printMessage(out_val);
		}
	}
	
	/* Describe amount of item remaining.			-RAK-	*/
	public static void describeRemaining(int item_val) {
		String out_val, tmp_str;
		InvenType i_ptr;
		
		i_ptr = Treasure.inventory[item_val];
		i_ptr.number--;
		tmp_str = describeObject(i_ptr, true);
		i_ptr.number++;
		/* the string already has a dot at the end. */
		out_val = String.format("You have %s", tmp_str);
		IO.printMessage(out_val);
	}
}
