/*
 * InvenType.java: player inventory slot
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
package net.jonhopkins.moria.types;

import net.jonhopkins.moria.Constants;

public final class InvenType {
	
	/**
	 * Maximum inscription length
	 */
	public final int INSCRIP_SIZE = 13;
	
	/**
	 * Index to object_list
	 */
	public int index;
	
	/**
	 * Object special name
	 */
	public int specialName;
	
	/**
	 * Object inscription
	 */
	public String inscription;
	
	/**
	 * Special flags
	 */
	public int flags;
	
	/**
	 * Category number
	 */
	public int category;
	
	/**
	 * Character representation
	 */
	public char tchar;
	
	/**
	 * Misc. use variable
	 */
	public int misc;
	
	/**
	 * Cost of item
	 */
	public int cost;
	
	/**
	 * Sub-category number
	 */
	public int subCategory;
	
	/**
	 * Number of items in stack
	 */
	public int number;
	
	/**
	 * Weight
	 */
	public int weight;
	
	/**
	 * Pluses to hit
	 */
	public int tohit;
	
	/**
	 * Pluses to damage
	 */
	public int plusToDam;
	
	/**
	 * Normal AC
	 */
	public int armorClass;
	
	/**
	 * Pluses to AC
	 */
	public int plusToArmorClass;
	
	/**
	 * Damage when hits
	 */
	public int[] damage = new int[2];
	
	/**
	 * Level on which item can first be found
	 */
	public int level;
	
	/**
	 * Identify information
	 */
	public int identify;
	
	public void copyInto(InvenType item) {
		item.index = this.index;
		item.specialName = this.specialName;
		item.inscription = this.inscription;
		item.flags = this.flags;
		item.category = this.category;
		item.tchar = this.tchar;
		item.misc = this.misc;
		item.cost = this.cost;
		item.subCategory = this.subCategory;
		item.number = this.number;
		item.weight = this.weight;
		item.tohit = this.tohit;
		item.plusToDam = this.plusToDam;
		item.armorClass = this.armorClass;
		item.plusToArmorClass = this.plusToArmorClass;
		item.damage[0] = this.damage[0];
		item.damage[1] = this.damage[1];
		item.level = this.level;
		item.identify = this.identify;
	}
	
	public boolean isCorrosive() {
		switch (category) {
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
	
	public boolean isFlammable() {
		switch (category) {
		case Constants.TV_ARROW:
		case Constants.TV_BOW:
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:
		case Constants.TV_SOFT_ARMOR:
			// Items of (RF) should not be destroyed
			return (flags & Constants.TR_RES_FIRE) == 0;
		case Constants.TV_STAFF:
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
			return true;
		default:
			return false;
		}
	}
	
	public boolean doesFrostDestroy() {
		return category == Constants.TV_POTION1
				|| category == Constants.TV_POTION2
				|| category == Constants.TV_FLASK;
	}
	
	public boolean doesAcidAffect() {
		switch (category) {
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
			return (flags & Constants.TR_RES_ACID) == 0;
		default:
			return false;
		}
	}
	
	public boolean doesLightningDestroy() {
		return category == Constants.TV_RING
				|| category == Constants.TV_WAND
				|| category == Constants.TV_SPIKE;
	}
	
	public boolean isNull() {
		return false;
	}
	
	public boolean doesAcidDestroy() {
		switch (category) {
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
			return (flags & Constants.TR_RES_ACID) == 0;
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
	
	public boolean doesFireDestroy() {
		switch (category) {
		case Constants.TV_ARROW:
		case Constants.TV_BOW:
		case Constants.TV_HAFTED:
		case Constants.TV_POLEARM:
		case Constants.TV_BOOTS:
		case Constants.TV_GLOVES:
		case Constants.TV_CLOAK:
		case Constants.TV_SOFT_ARMOR:
			return (flags & Constants.TR_RES_FIRE) == 0;
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
	
	public boolean isSoldInGeneralStore() {
		switch (category) {
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
	
	public boolean isSoldInArmory() {
		switch (category) {
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
	
	public boolean isSoldInWeaponsmith() {
		switch (category) {
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
	
	public boolean isSoldInTemple() {
		switch (category) {
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
	
	public boolean isSoldInAlchemist() {
		switch (category) {
		case Constants.TV_SCROLL1:
		case Constants.TV_SCROLL2:
		case Constants.TV_POTION1:
		case Constants.TV_POTION2:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isSoldInMagicShop() {
		switch (category) {
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
