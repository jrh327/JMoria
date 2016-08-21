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
	public static final int set_room = 1;
	public static boolean set_room(int element) {
		if ((element == Constants.DARK_FLOOR) || (element == Constants.LIGHT_FLOOR)) {
			return true;
		}
		return false;
	}
	
	public static final int set_corr = 2;
	public static boolean set_corr(int element) {
		if (element == Constants.CORR_FLOOR || element == Constants.BLOCKED_FLOOR) {
			return true;
		}
		return false;
	}
	
	public static final int set_floor = 3;
	public static boolean set_floor(int element) {
		if (element <= Constants.MAX_CAVE_FLOOR) {
			return true;
		} else {
			return false;
		}
	}
	
	public static final int set_corrodes = 4;
	public static boolean set_corrodes(InvenType item) {
		switch(item.tval)
		{
		case Constants.TV_SWORD:	case Constants.TV_HELM:
		case Constants.TV_SHIELD:	case Constants.TV_HARD_ARMOR:
		case Constants.TV_WAND:
			return  true;
	    }
		return false;
	}
	
	public static final int set_flammable = 5;
	public static boolean set_flammable(InvenType item) {
		switch(item.tval)
		{
		case Constants.TV_ARROW:	case Constants.TV_BOW:
		case Constants.TV_HAFTED:	case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:	case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:	case Constants.TV_SOFT_ARMOR:
			/* Items of (RF) should not be destroyed.  */
			if ((item.flags & Constants.TR_RES_FIRE) != 0) {
				return false;
			} else {
				return true;
			}
			
		case Constants.TV_STAFF:	case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
			return true;
		}
		return false;
	}
	
	public static final int set_frost_destroy = 6;
	public static boolean set_frost_destroy(InvenType item) {
		if ((item.tval == Constants.TV_POTION1) || (item.tval == Constants.TV_POTION2) || (item.tval == Constants.TV_FLASK)) {
			return true;
		}
		return false;
	}
	
	public static final int set_acid_affect = 7;
	public static boolean set_acid_affect(InvenType item) {
		switch(item.tval)
		{
		case Constants.TV_MISC:		case Constants.TV_CHEST:
			return true;
		case Constants.TV_BOLT:		case Constants.TV_ARROW:
		case Constants.TV_BOW:		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:	case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:	case Constants.TV_CLOAK:
		case Constants.TV_SOFT_ARMOR:
			if ((item.flags & Constants.TR_RES_ACID) != 0) {
				return  false;
			} else {
				return  true;
			}
		}
		return false;
	}
	
	public static final int set_lightning_destroy = 8;
	public static boolean set_lightning_destroy(InvenType item) {
		if ((item.tval == Constants.TV_RING) || (item.tval == Constants.TV_WAND) || (item.tval == Constants.TV_SPIKE)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static final int set_null = 9;
	public static boolean set_null(InvenType item) {
		return false;
	}
	
	public static final int set_acid_destroy = 10;
	public static boolean set_acid_destroy(InvenType item) {
		switch(item.tval)
		{
		case Constants.TV_ARROW:	case Constants.TV_BOW:
		case Constants.TV_HAFTED:	case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:	case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:	case Constants.TV_HELM:
		case Constants.TV_SHIELD:	case Constants.TV_HARD_ARMOR:
		case Constants.TV_SOFT_ARMOR:
			if ((item.flags & Constants.TR_RES_ACID) != 0) {
				return false;
			} else {
				return true;
			}
		case Constants.TV_STAFF:		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:		case Constants.TV_FOOD:
		case Constants.TV_OPEN_DOOR:	case Constants.TV_CLOSED_DOOR:
			return true;
		}
		return false;
	}
	
	public static final int set_fire_destroy = 11;
	public static boolean set_fire_destroy(InvenType item) {
		switch(item.tval)
		{
		case Constants.TV_ARROW:	case Constants.TV_BOW:
		case Constants.TV_HAFTED:	case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:	case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:	case Constants.TV_SOFT_ARMOR:
			if ((item.flags & Constants.TR_RES_FIRE) != 0) {
				return false;
			} else {
				return true;
			}
		case Constants.TV_STAFF:	case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:	case Constants.TV_POTION1:
		case Constants.TV_POTION2:	case Constants.TV_FLASK:
		case Constants.TV_FOOD:		case Constants.TV_OPEN_DOOR:
		case Constants.TV_CLOSED_DOOR:
			return true;
		}
		return false;
	}
	
	/* Items too large to fit in chests 	-DJG- */
	public static boolean set_large(TreasureType item) {
		/* Use treasure_type since item not yet created */
		switch(item.tval)
		{
		case Constants.TV_CHEST: case Constants.TV_BOW: case Constants.TV_POLEARM: 
		case Constants.TV_HARD_ARMOR: case Constants.TV_SOFT_ARMOR: case Constants.TV_STAFF:
			return true;
		case Constants.TV_HAFTED: case Constants.TV_SWORD: case Constants.TV_DIGGING:
			if (item.weight > 150) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public static final int general_store = 0;
	public static boolean general_store(int element) {
		switch(element)
		{
		case Constants.TV_DIGGING:	case Constants.TV_BOOTS:
		case Constants.TV_CLOAK:	case Constants.TV_FOOD:
		case Constants.TV_FLASK:	case Constants.TV_LIGHT:
		case Constants.TV_SPIKE:
			return true;
		}
		return false;
	}
	
	public static final int armory = 1;
	public static boolean armory(int element) {
		switch(element)
		{
		case Constants.TV_BOOTS:		case Constants.TV_GLOVES:
		case Constants.TV_HELM:			case Constants.TV_SHIELD:
		case Constants.TV_HARD_ARMOR:	case Constants.TV_SOFT_ARMOR:
			return true;
		}
		return false;
	}
	
	public static final int weaponsmith = 2;
	public static boolean weaponsmith(int element) {
		switch(element)
		{
		case Constants.TV_SLING_AMMO:	case Constants.TV_BOLT:
		case Constants.TV_ARROW:		case Constants.TV_BOW:
		case Constants.TV_HAFTED:		case Constants.TV_POLEARM:
		case Constants.TV_SWORD:
			return true;
		}
		return false;
	}
	
	public static final int temple = 3;
	public static boolean temple(int element) {
		switch(element)
		{
		case Constants.TV_HAFTED:	case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:	case Constants.TV_POTION1:
		case Constants.TV_POTION2:	case Constants.TV_PRAYER_BOOK:
			return true;
		}
		return false;
	}
	
	public static final int alchemist = 4;
	public static boolean alchemist(int element) {
		switch(element)
		{
		case Constants.TV_SCROLL1:	case Constants.TV_SCROLL2:
		case Constants.TV_POTION1:	case Constants.TV_POTION2:
			return true;
		}
		return false;
	}
	
	public static final int magic_shop = 5;
	public static boolean magic_shop(int element) {
		switch(element)
		{
		case Constants.TV_AMULET:	case Constants.TV_RING:
		case Constants.TV_STAFF:	case Constants.TV_WAND:
		case Constants.TV_SCROLL1:	case Constants.TV_SCROLL2:
		case Constants.TV_POTION1:	case Constants.TV_POTION2:
		case Constants.TV_MAGIC_BOOK:
			return true;
		}
		return false;
	}
}
