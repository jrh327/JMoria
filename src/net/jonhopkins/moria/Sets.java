/*
 * Sets.java: code to emulate the original Pascal sets
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

import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.TreasureType;

public class Sets {
	
	public static final int SET_ROOM = 1;
	public static boolean isRoom(int element) {
		return element == Constants.DARK_FLOOR || element == Constants.LIGHT_FLOOR;
	}
	
	public static final int SET_CORR = 2;
	public static boolean isCorridor(int element) {
		return element == Constants.CORR_FLOOR || element == Constants.BLOCKED_FLOOR;
	}
	
	public static final int SET_FLOOR = 3;
	public static boolean isFloor(int element) {
		return element <= Constants.MAX_CAVE_FLOOR;
	}
	
	public static final int SET_CORRODES = 4;
	public static boolean isCorrosive(InvenType item) {
		switch (item.category) {
		case Constants.TV_SWORD:
		case Constants.TV_HELM:
		case Constants.TV_SHIELD:
		case Constants.TV_HARD_ARMOR:
		case Constants.TV_WAND:
			return true;
		default:
			return false;
	    }
	}
	
	public static final int SET_FLAMMABLE = 5;
	public static boolean isFlammable(InvenType item) {
		switch (item.category) {
		case Constants.TV_ARROW:
		case Constants.TV_BOW:
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:
		case Constants.TV_SOFT_ARMOR:
			/* Items of (RF) should not be destroyed.  */
			return (item.flags & Constants.TR_RES_FIRE) == 0;
		case Constants.TV_STAFF:
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
			return true;
		default:
			return false;
		}
	}
	
	public static final int SET_FROST_DESTROY = 6;
	public static boolean doesFrostDestroy(InvenType item) {
		return item.category == Constants.TV_POTION1
				|| item.category == Constants.TV_POTION2
				|| item.category == Constants.TV_FLASK;
	}
	
	public static final int SET_ACID_AFFECT = 7;
	public static boolean doesAcidAffect(InvenType item) {
		switch (item.category) {
		case Constants.TV_MISC:
		case Constants.TV_CHEST:
			return true;
		case Constants.TV_BOLT:
		case Constants.TV_ARROW:
		case Constants.TV_BOW:
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:
		case Constants.TV_SOFT_ARMOR:
			return (item.flags & Constants.TR_RES_ACID) == 0;
		default:
			return false;
		}
	}
	
	public static final int SET_LIGHTNING_DESTROY = 8;
	public static boolean doesLightningDestroy(InvenType item) {
		return item.category == Constants.TV_RING
				|| item.category == Constants.TV_WAND
				|| item.category == Constants.TV_SPIKE;
	}
	
	public static final int SET_NULL = 9;
	public static boolean isNull(InvenType item) {
		return false;
	}
	
	public static final int SET_ACID_DESTROY = 10;
	public static boolean doesAcidDestroy(InvenType item) {
		switch (item.category) {
		case Constants.TV_ARROW:
		case Constants.TV_BOW:
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:
		case Constants.TV_HELM:
		case Constants.TV_SHIELD:
		case Constants.TV_HARD_ARMOR:
		case Constants.TV_SOFT_ARMOR:
			return (item.flags & Constants.TR_RES_ACID) == 0;
		case Constants.TV_STAFF:
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
		case Constants.TV_FOOD:
		case Constants.TV_OPEN_DOOR:
		case Constants.TV_CLOSED_DOOR:
			return true;
		default:
			return false;
		}
	}
	
	public static final int SET_FIRE_DESTROY = 11;
	public static boolean doesFireDestroy(InvenType item) {
		switch (item.category) {
		case Constants.TV_ARROW:
		case Constants.TV_BOW:
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:
		case Constants.TV_SOFT_ARMOR:
			return (item.flags & Constants.TR_RES_FIRE) == 0;
		case Constants.TV_STAFF:
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
		case Constants.TV_POTION1:
		case Constants.TV_POTION2:
		case Constants.TV_FLASK:
		case Constants.TV_FOOD:
		case Constants.TV_OPEN_DOOR:
		case Constants.TV_CLOSED_DOOR:
			return true;
		default:
			return false;
		}
	}
	
	/* Items too large to fit in chests 	-DJG- */
	public static boolean isTooLargeForChest(TreasureType item) {
		/* Use treasure_type since item not yet created */
		switch (item.category) {
		case Constants.TV_CHEST:
		case Constants.TV_BOW:
		case Constants.TV_POLEARM:
		case Constants.TV_HARD_ARMOR:
		case Constants.TV_SOFT_ARMOR:
		case Constants.TV_STAFF:
			return true;
		case Constants.TV_HAFTED:
		case Constants.TV_SWORD:
		case Constants.TV_DIGGING:
			return item.weight > 150;
		default:
			return false;
		}
	}
	
	public static final int GENERAL_STORE = 0;
	public static boolean isSoldInGeneralStore(int element) {
		switch (element) {
		case Constants.TV_DIGGING:
		case Constants.TV_BOOTS:
		case Constants.TV_CLOAK:
		case Constants.TV_FOOD:
		case Constants.TV_FLASK:
		case Constants.TV_LIGHT:
		case Constants.TV_SPIKE:
			return true;
		default:
			return false;
		}
	}
	
	public static final int ARMORY = 1;
	public static boolean isSoldInArmory(int element) {
		switch (element) {
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_HELM:
		case Constants.TV_SHIELD:
		case Constants.TV_HARD_ARMOR:
		case Constants.TV_SOFT_ARMOR:
			return true;
		default:
			return false;
		}
	}
	
	public static final int WEAPONSMITH = 2;
	public static boolean isSoldInWeaponsmith(int element) {
		switch (element) {
		case Constants.TV_SLING_AMMO:
		case Constants.TV_BOLT:
		case Constants.TV_ARROW:
		case Constants.TV_BOW:
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_SWORD:
			return true;
		default:
			return false;
		}
	}
	
	public static final int TEMPLE = 3;
	public static boolean isSoldInTemple(int element) {
		switch (element) {
		case Constants.TV_HAFTED:
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
		case Constants.TV_POTION1:
		case Constants.TV_POTION2:
		case Constants.TV_PRAYER_BOOK:
			return true;
		default:
			return false;
		}
	}
	
	public static final int ALCHEMIST = 4;
	public static boolean isSoldInAlchemist(int element) {
		switch (element) {
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
		case Constants.TV_POTION1:
		case Constants.TV_POTION2:
			return true;
		default:
			return false;
		}
	}
	
	public static final int MAGIC_SHOP = 5;
	public static boolean isSoldInMagicShop(int element) {
		switch (element) {
		case Constants.TV_AMULET:
		case Constants.TV_RING:
		case Constants.TV_STAFF:
		case Constants.TV_WAND:
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
		case Constants.TV_POTION1:
		case Constants.TV_POTION2:
		case Constants.TV_MAGIC_BOOK:
			return true;
		default:
			return false;
		}
	}
}
