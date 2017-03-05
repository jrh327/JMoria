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
	 * Check if a character is a vowel.
	 * 
	 * @param ch Character being checked
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
		Misc1.setSeed(Variable.randesSeed);
		
		// The first 3 entries for colors are fixed, (slime & apple juice, water)
		for (int i = 3; i < Constants.MAX_COLORS; i++) {
			int j = Misc1.randomInt(Constants.MAX_COLORS - 3) + 2;
			String tmp = Tables.colors[i];
			Tables.colors[i] = Tables.colors[j];
			Tables.colors[j] = tmp;
		}
		for (int i = 0; i < Constants.MAX_WOODS; i++) {
			int j = Misc1.randomInt(Constants.MAX_WOODS) - 1;
			String tmp = Tables.woods[i];
			Tables.woods[i] = Tables.woods[j];
			Tables.woods[j] = tmp;
		}
		for (int i = 0; i < Constants.MAX_METALS; i++) {
			int j = Misc1.randomInt(Constants.MAX_METALS) - 1;
			String tmp = Tables.metals[i];
			Tables.metals[i] = Tables.metals[j];
			Tables.metals[j] = tmp;
		}
		for (int i = 0; i < Constants.MAX_ROCKS; i++) {
			int j = Misc1.randomInt(Constants.MAX_ROCKS) - 1;
			String tmp = Tables.rocks[i];
			Tables.rocks[i] = Tables.rocks[j];
			Tables.rocks[j] = tmp;
		}
		for (int i = 0; i < Constants.MAX_AMULETS; i++) {
			int j = Misc1.randomInt(Constants.MAX_AMULETS) - 1;
			String tmp = Tables.amulets[i];
			Tables.amulets[i] = Tables.amulets[j];
			Tables.amulets[j] = tmp;
		}
		for (int i = 0; i < Constants.MAX_MUSH; i++) {
			int j = Misc1.randomInt(Constants.MAX_MUSH) - 1;
			String tmp = Tables.mushrooms[i];
			Tables.mushrooms[i] = Tables.mushrooms[j];
			Tables.mushrooms[j] = tmp;
		}
		for (int h = 0; h < Constants.MAX_TITLES; h++) {
			StringBuilder string = new StringBuilder(10);
			int k = Misc1.randomInt(2) + 1;
			for (int i = 0; i < k; i++) {
				for (int j = Misc1.randomInt(2); j > 0; j--) {
					string.append(Tables.syllables[Misc1.randomInt(Constants.MAX_SYLLABLES) - 1]);
				}
				if (i < k - 1) {
					string.append(' ');
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
	 * @param item Item being checked
	 * @return Item type
	 */
	public static int getObjectOffset(InvenType item) {
		switch (item.category) {
		case Constants.TV_AMULET:  return 0;
		case Constants.TV_RING:    return 1;
		case Constants.TV_STAFF:   return 2;
		case Constants.TV_WAND:    return 3;
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2: return 4;
		case Constants.TV_POTION1:
		case Constants.TV_POTION2: return 5;
		case Constants.TV_FOOD:
			if ((item.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1)) < Constants.MAX_MUSH) {
				return 6;
			}
			return -1;
		default:
			return -1;
		}
	}
	
	/**
	 * Remove "Secret" symbol for identity of object.
	 * 
	 * @param item Item being identified
	 */
	public static void identifyItem(InvenType item) {
		int offset = getObjectOffset(item);
		if (offset < 0) {
			return;
		}
		offset <<= 6;
		int index = item.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		Treasure.objectIdent[offset + index] |= Constants.OD_KNOWN1;
		// clear the tried flag, since it is now known
		Treasure.objectIdent[offset + index] &= ~Constants.OD_TRIED;
	}
	
	/**
	 * Check if the player knows what the item is.
	 * 
	 * @param item Item being checked
	 * @return True if the item is known, otherwise false
	 */
	public static boolean isKnownByPlayer(InvenType item) {
		// Items which don't have a 'color' are always known1, so that they can
		// be carried in order in the inventory.
		int offset = getObjectOffset(item);
		if (offset < 0) {
			return true;
		}
		if (isStoreBought(item)) {
			return true;
		}
		offset <<= 6;
		int index = item.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		return Treasure.objectIdent[offset + index] != 0;
	}
	
	/**
	 * Remove "Secret" symbol for identity of plusses.
	 * 
	 * @param item The item whose plusses are being identified
	 */
	public static void identifyItemPlusses(InvenType item) {
		unsample(item);
		item.identify |= Constants.ID_KNOWN2;
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
	
	/**
	 * Remove an automatically generated inscription. -CJS-
	 * 
	 * @param item The item to unsample
	 */
	public static void unsample(InvenType item) {
		// used to clear ID_DAMD flag, but I think it should remain set
		item.identify &= ~(Constants.ID_MAGIK|Constants.ID_EMPTY);
		int offset = getObjectOffset(item);
		if (offset < 0) {
			return;
		}
		offset <<= 6;
		int index = item.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		Treasure.objectIdent[offset + index] &= ~Constants.OD_TRIED;
	}
	
	/* unquote() is no longer needed */
	
	/**
	 * Somethings been sampled. -CJS-
	 * 
	 * @param item The item to sample
	 */
	public static void sample(InvenType item) {
		int offset = getObjectOffset(item);
		if (offset < 0) {
			return;
		}
		offset <<= 6;
		int index = item.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		Treasure.objectIdent[offset + index] |= Constants.OD_TRIED;
	}
	
	/**
	 * Somethings been identified.
	 * <p>
 	 * Extra complexity by CJS so that it can merge store/dungeon objects
	 * when appropriate.
	 * 
	 * @param index Pointer to inventory index of item to identify
	 */
	public static void identify(IntPointer index) {
		InvenType item = Treasure.inventory[index.value()];
		
		if ((item.flags & Constants.TR_CURSED) != 0) {
			Misc4.addInscription(item, Constants.ID_DAMD);
		}
		
		if (isKnownByPlayer(item)) {
			return;
		}
		
		identifyItem(item);
		int category = item.category;
		int subCategory = item.subCategory;
		if (subCategory < Constants.ITEM_SINGLE_STACK_MIN
				|| subCategory >= Constants.ITEM_GROUP_MIN) {
			return;
		}
		
		for (int i = 0; i < Treasure.invenCounter; i++) {
			InvenType tempItem = Treasure.inventory[i];
			if (tempItem.category == category
					&& tempItem.subCategory == subCategory
					&& i != index.value()
					&& (tempItem.number + item.number < 256)) {
				// make *index the smaller number
				if (index.value() > i) {
					int tmp = index.value();
					index.value(i);
					i = tmp;
				}
				IO.printMessage("You combine similar objects from the shop and dungeon.");
				
				Treasure.inventory[index.value()].number += Treasure.inventory[i].number;
				Treasure.invenCounter--;
				for (int j = i; j < Treasure.invenCounter; j++) {
					Treasure.inventory[j] = Treasure.inventory[j + 1];
				}
				copyIntoInventory(Treasure.inventory[Treasure.invenCounter], Constants.OBJ_NOTHING);
			}
		}
	}
	
	/**
	 * If an object has lost magical properties,
	 * remove the appropriate portion of the name. -CJS-
	 * 
	 * @param item The item to rename
	 */
	public static void unmagicName(InvenType item) {
		item.specialName = Constants.SN_NULL;
	}
	
	// defines for p1_use, determine how the p1 field is printed
	private static final int IGNORED   = 0;
	private static final int CHARGES   = 1;
	private static final int PLUSSES   = 2;
	private static final int LIGHT     = 3;
	private static final int FLAGS     = 4;
	private static final int Z_PLUSSES = 5;
	
	/**
	 * Returns a description of item for inventory.
	 * 
	 * @param item The item to describe
	 * @param prefix Indicates that there should be an article added
	 * @return The description of the object
	 */
	public static String describeObject(InvenType item, boolean prefix) {
		int index = item.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1);
		String baseName = Treasure.objectList[item.index].name;
		String modStr = null;
		String damageStr = null;
		int p1_use = IGNORED;
		boolean modify = !isKnownByPlayer(item);
		boolean appendName = false;
		
		switch(item.category) {
		case Constants.TV_MISC:
		case Constants.TV_CHEST:
			break;
		case Constants.TV_SLING_AMMO:
		case Constants.TV_BOLT:
		case Constants.TV_ARROW:
			damageStr = String.format(" (%dd%d)", item.damage[0], item.damage[1]);
			break;
		case Constants.TV_LIGHT:
			p1_use = LIGHT;
			break;
		case Constants.TV_SPIKE:
			break;
		case Constants.TV_BOW:
			int tmp;
			if (item.misc == 1 || item.misc == 2) {
				tmp = 2;
			} else if (item.misc == 3 || item.misc == 5) {
				tmp = 3;
			} else if (item.misc == 4 || item.misc == 6) {
				tmp = 4;
			} else {
				tmp = -1;
			}
			damageStr = String.format(" (x%d)", tmp);
			break;
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_SWORD:
			damageStr = String.format(" (%dd%d)", item.damage[0], item.damage[1]);
			p1_use = FLAGS;
			break;
		case Constants.TV_DIGGING:
			p1_use = Z_PLUSSES;
			damageStr = String.format(" (%dd%d)", item.damage[0], item.damage[1]);
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
				baseName = "& %s Amulet";
				modStr = Tables.amulets[index];
			} else {
				baseName = "& Amulet";
				appendName = true;
			}
			p1_use = PLUSSES;
			break;
		case Constants.TV_RING:
			if (modify) {
				baseName = "& %s Ring";
				modStr = Tables.rocks[index];
			} else {
				baseName = "& Ring";
				appendName = true;
			}
			p1_use = PLUSSES;
			break;
		case Constants.TV_STAFF:
			if (modify) {
				baseName = "& %s Staff";
				modStr = Tables.woods[index];
			} else {
				baseName = "& Staff";
				appendName = true;
			}
			p1_use = CHARGES;
			break;
		case Constants.TV_WAND:
			if (modify) {
				baseName = "& %s Wand";
				modStr = Tables.metals[index];
			} else {
				baseName = "& Wand";
				appendName = true;
			}
			p1_use = CHARGES;
			break;
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
			if (modify) {
				baseName =  "& Scroll~ titled \"%s\"";
				modStr = titles[index];
			} else {
				baseName = "& Scroll~";
				appendName = true;
			}
			break;
		case Constants.TV_POTION1:
		case Constants.TV_POTION2:
			if (modify) {
				baseName = "& %s Potion~";
				modStr = Tables.colors[index];
			} else {
				baseName = "& Potion~";
				appendName = true;
			}
			break;
		case Constants.TV_FLASK:
			break;
		case Constants.TV_FOOD:
			if (modify) {
				if (index <= 15) {
					baseName = "& %s Mushroom~";
				} else if (index <= 20) {
					baseName = "& Hairy %s Mold~";
				}
				if (index <= 20) {
					modStr = Tables.mushrooms[index];
				}
			} else {
				appendName = true;
				if (index <= 15) {
					baseName = "& Mushroom~";
				} else if (index <= 20) {
					baseName = "& Hairy Mold~";
				} else {
					// Ordinary food does not have a name appended.
					appendName = false;
				}
			}
			break;
		case Constants.TV_MAGIC_BOOK:
			modStr = baseName;
			baseName = "& Book~ of Magic Spells %s";
			break;
		case Constants.TV_PRAYER_BOOK:
			modStr = baseName;
			baseName = "& Holy Book~ of Prayers %s";
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
			return String.format("%s.", Treasure.objectList[item.index].name);
		case Constants.TV_STORE_DOOR:
			return String.format("the entrance to the %s.",
					Treasure.objectList[item.index].name);
		default:
			return "Error in objdes()";
		}
		
		StringBuilder outVal = new StringBuilder();
		StringBuilder tmpVal = new StringBuilder();
		if (!modStr.isEmpty()) {
			tmpVal.append(String.format(baseName, modStr));
		} else {
			tmpVal.append(baseName);
		}
		
		if (appendName) {
			tmpVal.append(" of ").append(Treasure.objectList[item.index].name);
		}
		
		if (item.number != 1) {
			int start = tmpVal.indexOf("ch~");
			tmpVal.replace(start, start + 3, "ches");
			start = tmpVal.indexOf("~");
			tmpVal.replace(start, start + 1, "s");
		} else {
			int start = tmpVal.indexOf("~");
			tmpVal.replace(start, start + 1, "");
		}
		
		if (!prefix) {
			if (!tmpVal.substring(0, 4).equals("some")) {
				outVal.append(tmpVal.substring(5));
			} else if (tmpVal.charAt(0) == '&') {
				// eliminate the '& ' at the beginning
				outVal.append(tmpVal.substring(2));
			} else {
				outVal.append(tmpVal);
			}
		} else {
			if (item.specialName != Constants.SN_NULL && arePlussesKnownByPlayer(item)) {
				tmpVal.append(' ').append(Treasure.specialNames[item.specialName]);
			}
			
			if (!damageStr.isEmpty()) {
				tmpVal.append(damageStr);
			}
			
			if (arePlussesKnownByPlayer(item)) {
				// originally used %+d, but several machines don't support it
				if ((item.identify & Constants.ID_SHOW_HITDAM) != 0) {
					tmpVal.append(String.format(" (%c%d,%c%d)",
							(item.tohit < 0) ? '-' : '+',
							Math.abs(item.tohit),
							(item.plusToDam < 0) ? '-' : '+',
							Math.abs(item.plusToDam)));
				} else if (item.tohit != 0) {
					tmpVal.append(String.format(" (%c%d)",
							(item.tohit < 0) ? '-' : '+',
							Math.abs(item.tohit)));
				} else if (item.plusToDam != 0) {
					tmpVal.append(String.format(" (%c%d)",
							(item.plusToDam < 0) ? '-' : '+',
							Math.abs(item.plusToDam)));
				}
			}
			
			// Crowns have a zero base AC, so make a special test for them.
			if (item.armorClass != 0 || (item.category == Constants.TV_HELM)) {
				tmpVal.append(String.format(" [%d", item.armorClass));
				if (arePlussesKnownByPlayer(item)) {
					// originally used %+d, but several machines don't support it
					tmpVal.append(String.format(",%c%d",
							(item.plusToArmorClass < 0) ? '-' : '+',
							Math.abs(item.plusToArmorClass)));
				}
				tmpVal.append(']');
			} else if ((item.plusToArmorClass != 0) && arePlussesKnownByPlayer(item)) {
				// originally used %+d, but several machines don't support it
				tmpVal.append(String.format(" [%c%d]",
						(item.plusToArmorClass < 0) ? '-' : '+',
						Math.abs(item.plusToArmorClass)));
			}
			
			// override defaults, check for p1 flags in the ident field
			if ((item.identify & Constants.ID_NOSHOW_P1) != 0) {
				p1_use = IGNORED;
			} else if ((item.identify & Constants.ID_SHOW_P1) != 0) {
				p1_use = Z_PLUSSES;
			}
			
			if (p1_use == LIGHT) {
				tmpVal.append(String.format(" with %d turns of light", item.misc));
			} else if (p1_use != IGNORED && arePlussesKnownByPlayer(item)) {
				if (p1_use == Z_PLUSSES) {
					// originally used %+d, but several machines don't support it
					tmpVal.append(String.format(" (%c%d)",
							(item.misc < 0) ? '-' : '+', Math.abs(item.misc)));
				} else if (p1_use == CHARGES) {
					tmpVal.append(String.format(" (%d charges)", item.misc));
				} else if (item.misc != 0) {
					if (p1_use == PLUSSES) {
						tmpVal.append(String.format(" (%c%d)",
								(item.misc < 0) ? '-' : '+',
								Math.abs(item.misc)));
					} else if (p1_use == FLAGS) {
						if ((item.flags & Constants.TR_STR) != 0) {
							tmpVal.append(String.format(" (%c%d to STR)",
									(item.misc < 0) ? '-' : '+',
									Math.abs(item.misc)));
						} else if ((item.flags & Constants.TR_STEALTH) != 0) {
							tmpVal.append(String.format(" (%c%d to stealth)",
									(item.misc < 0) ? '-' : '+',
									Math.abs(item.misc)));
						}
					}
				}
			}
			
			// ampersand is always the first character
			if (tmpVal.charAt(0) == '&') {
				// use &tmp_val[1], so that & does not appear in output
				if (item.number > 1) {
					outVal.append(String.format("%d%s", item.number, tmpVal.substring(1)));
				} else if (item.number < 1) {
					outVal.append(String.format("%s%s", "no more", tmpVal.substring(1)));
				} else if (isVowel(tmpVal.charAt(2))) {
					outVal.append(String.format("an%s", tmpVal.substring(1)));
				} else {
					outVal.append(String.format("a%s", tmpVal.substring(1)));
				}
			
			// handle 'no more' case specially
			} else if (item.number < 1) {
				// check for "some" at start
				if (!tmpVal.substring(0, 4).equals("some")) {
					outVal.append(String.format("no more %s", tmpVal.substring(5)));
				
				// here if no article
				} else {
					outVal.append(String.format("no more %s", tmpVal.toString()));
				}
			} else {
				outVal = tmpVal;
			}
			
			tmpVal = new StringBuilder();
			if ((index = getObjectOffset(item)) >= 0) {
				index = (index << 6) + (item.subCategory & (Constants.ITEM_SINGLE_STACK_MIN - 1));
				// don't print tried string for store bought items
				if ((Treasure.objectIdent[index] & Constants.OD_TRIED) != 0 && !isStoreBought(item)) {
					tmpVal.append("tried ");
				}
			}
			if ((item.identify & (Constants.ID_MAGIK|Constants.ID_EMPTY|Constants.ID_DAMD)) != 0) {
				if ((item.identify & Constants.ID_MAGIK) != 0) {
					tmpVal.append("magik ");
				}
				if ((item.identify & Constants.ID_EMPTY) != 0) {
					tmpVal.append("empty ");
				}
				if ((item.identify & Constants.ID_DAMD) != 0) {
					tmpVal.append("damned ");
				}
			}
			if (!item.inscription.isEmpty()) {
				tmpVal.append(item.inscription);
			} else if ((index = tmpVal.length()) > 0) {
				// remove the extra blank at the end
				tmpVal.substring(0, index - 1);
			}
			if (!tmpVal.toString().isEmpty()) {
				outVal.append(String.format(" {%s}", tmpVal.toString()));
			}
			outVal = outVal.append('.');
		}
		return outVal.toString();
	}
	
	public static void copyIntoInventory(InvenType to, int from_index) {
		Treasure.objectList[from_index].copyInto(to);
		to.index = from_index;
	}
	
	/**
	 * Describe number of remaining charges. -RAK-
	 * 
	 * @param index Inventory index of the item to describe
	 */
	public static void describeCharges(int index) {
		if (arePlussesKnownByPlayer(Treasure.inventory[index])) {
			int remaining = Treasure.inventory[index].misc;
			String outVal = String.format("You have %d charges remaining.", remaining);
			IO.printMessage(outVal);
		}
	}
	
	/**
	 * Describe amount of item remaining. -RAK-
	 * 
	 * @param index Inventory index of the idem to describe
	 */
	public static void describeRemaining(int index) {
		InvenType item = Treasure.inventory[index];
		item.number--;
		String tmp = describeObject(item, true);
		item.number++;
		
		// the string already has a dot at the end.
		String outVal = String.format("You have %s", tmp);
		IO.printMessage(outVal);
	}
}
