/*
 * Store1.java: store code, updating store inventory, pricing objects
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
import net.jonhopkins.moria.types.StoreType;

public class Store1 {
	
	private Store1() { }
	
	/**
	 * Returns the value for any given object. -RAK-
	 * 
	 * @param i_ptr - The item whose value is being returned
	 * @return The market value of the item in i_ptr
	 */
	public static int getItemValue(InvenType i_ptr) {
		int value;
		
		value = i_ptr.cost;
		/* don't purchase known cursed items */
		if ((i_ptr.identify & Constants.ID_DAMD) != 0) {
			value = 0;
		} else if (((i_ptr.category >= Constants.TV_BOW) && (i_ptr.category <= Constants.TV_SWORD)) || ((i_ptr.category >= Constants.TV_BOOTS) && (i_ptr.category <= Constants.TV_SOFT_ARMOR))) {
			/* Weapons and armor	*/
			if (!Desc.arePlussesKnownByPlayer(i_ptr)) {
				value = Treasure.objectList[i_ptr.index].cost;
			} else if ((i_ptr.category >= Constants.TV_BOW) && (i_ptr.category <= Constants.TV_SWORD)) {
				if (i_ptr.tohit < 0) {
					value = 0;
				} else if (i_ptr.plusToDam < 0) {
					value = 0;
				} else if (i_ptr.plusToArmorClass < 0) {
					value = 0;
				} else {
					value = i_ptr.cost + (i_ptr.tohit + i_ptr.plusToDam + i_ptr.plusToArmorClass) * 100;
				}
			} else {
				if (i_ptr.plusToArmorClass < 0) {
					value = 0;
				} else {
					value = i_ptr.cost + i_ptr.plusToArmorClass * 100;
				}
			}
		} else if ((i_ptr.category >= Constants.TV_SLING_AMMO) && (i_ptr.category <= Constants.TV_SPIKE)) {
			/* Ammo			*/
			if (!Desc.arePlussesKnownByPlayer(i_ptr)) {
				value = Treasure.objectList[i_ptr.index].cost;
			} else {
				if (i_ptr.tohit < 0) {
					value = 0;
				} else if (i_ptr.plusToDam < 0) {
					value = 0;
				} else if (i_ptr.plusToArmorClass < 0) {
					value = 0;
				} else {
					/* use 5, because missiles generally appear in groups of 20,
					 * so 20 * 5 == 100, which is comparable to weapon bonus above */
					value = i_ptr.cost + (i_ptr.tohit + i_ptr.plusToDam + i_ptr.plusToArmorClass) * 5;
				}
			}
		} else if ((i_ptr.category == Constants.TV_SCROLL1) || (i_ptr.category == Constants.TV_SCROLL2) || (i_ptr.category == Constants.TV_POTION1) || (i_ptr.category == Constants.TV_POTION2)) {
			/* Potions, Scrolls, and Food	*/
			if (!Desc.isKnownByPlayer(i_ptr)) {
				value = 20;
			}
		} else if (i_ptr.category == Constants.TV_FOOD) {
			if ((i_ptr.subCategory < (Constants.ITEM_SINGLE_STACK_MIN + Constants.MAX_MUSH)) && !Desc.isKnownByPlayer(i_ptr)) {
				value = 1;
			}
		} else if ((i_ptr.category == Constants.TV_AMULET) || (i_ptr.category == Constants.TV_RING)) {
			/* Rings and amulets	*/
			if (!Desc.isKnownByPlayer(i_ptr)) {
				/* player does not know what type of ring/amulet this is */
				value = 45;
			} else if (!Desc.arePlussesKnownByPlayer(i_ptr)) {
				/* player knows what type of ring, but does not know whether it is
				 * cursed or not, if refuse to buy cursed objects here, then
				 * player can use this to 'identify' cursed objects */
				value = Treasure.objectList[i_ptr.index].cost;
			}
		} else if ((i_ptr.category == Constants.TV_STAFF) || (i_ptr.category == Constants.TV_WAND)) {
			/* Wands and staffs*/
			if (!Desc.isKnownByPlayer(i_ptr)) {
				if (i_ptr.category == Constants.TV_WAND) {
					value = 50;
				} else {
					value = 70;
				}
			} else if (Desc.arePlussesKnownByPlayer(i_ptr)) {
				value = i_ptr.cost + (i_ptr.cost / 20) * i_ptr.misc;
			}
		} else if (i_ptr.category == Constants.TV_DIGGING) {
			/* picks and shovels */
			if (!Desc.arePlussesKnownByPlayer(i_ptr)) {
				value = Treasure.objectList[i_ptr.index].cost;
			} else {
				if (i_ptr.misc < 0) {
					value = 0;
				} else {
					/* some digging tools start with non-zero p1 values, so only
					 * multiply the plusses by 100, make sure result is positive */
					value = i_ptr.cost + (i_ptr.misc - Treasure.objectList[i_ptr.index].misc) * 100;
					if (value < 0) {
						value = 0;
					}
				}
			}
		}
		/* multiply value by number of items if it is a group stack item */
		if (i_ptr.subCategory > Constants.ITEM_GROUP_MIN) {	/* do not include torches here */
			value = value * i_ptr.number;
		}
		return value;
	}
	
	/**
	 * Returns the asking price for an item. -RAK-
	 * 
	 * @param snum - Store number
	 * @param max_sell - Stores the maximum price to sell for
	 * @param min_sell - Stores the minimum price to sell for
	 * @param item - Item being checked
	 * @return The store's asking price for the item
	 */
	public static int getSellPrice(int snum, IntPointer max_sell, IntPointer min_sell, InvenType item) {
		int i;
		StoreType s_ptr;
		
		s_ptr = Variable.store[snum];
		i = getItemValue(item);
		/* check item.cost in case it is cursed, check i in case it is damaged */
		if ((item.cost > 0) && (i > 0)) {
			i = i * Tables.raceGoldAdjust[Tables.owners[s_ptr.owner].ownerRace][Player.py.misc.playerRace] / 100;
			if (i < 1)  i = 1;
			max_sell.value(i * Tables.owners[s_ptr.owner].maxInflate / 100);
			min_sell.value(i * Tables.owners[s_ptr.owner].minInflate / 100);
			if (min_sell.value() > max_sell.value())	min_sell.value(max_sell.value());
			return i;
		} else {
			/* don't let the item get into the store inventory */
			return 0;
		}
	}
	
	/**
	 * Check to see if the store will be carrying too many objects. -RAK-
	 * 
	 * @param t_ptr - The item being added
	 * @param store_num - Store number
	 * @return Return true if there is room in the store, otherwise false
	 */
	public static boolean isStoreFull(InvenType t_ptr, int store_num) {
		boolean store_check;
		int i;
		StoreType s_ptr;
		InvenType i_ptr;
		
		store_check = false;
		s_ptr = Variable.store[store_num];
		if (s_ptr.storeCounter < Constants.STORE_INVEN_MAX) {
			store_check = true;
		} else if (t_ptr.subCategory >= Constants.ITEM_SINGLE_STACK_MIN) {
			for (i = 0; i < s_ptr.storeCounter; i++) {
				i_ptr = s_ptr.storeInven[i].item;
				/* note: items with subval of gte ITEM_SINGLE_STACK_MAX only stack
				 * if their subvals match */
				if (i_ptr.category == t_ptr.category && i_ptr.subCategory == t_ptr.subCategory
						&& (i_ptr.number + t_ptr.number < 256)
						&& (t_ptr.subCategory < Constants.ITEM_GROUP_MIN || (i_ptr.misc == t_ptr.misc))) {
					store_check = true;
				}
			}
		}
		return store_check;
	}
	
	/**
	 * Insert INVEN_MAX at given location.
	 * 
	 * @param store_num - Store number
	 * @param pos - Position in the store's inventory to add the item
	 * @param icost - Cost of the item being added
	 * @param i_ptr - Item being added
	 */
	public static void insertStore(int store_num, int pos, int icost, InvenType i_ptr) {
		int i;
		StoreType s_ptr;
		
		s_ptr = Variable.store[store_num];
		for (i = s_ptr.storeCounter - 1; i >= pos; i--) {
			s_ptr.storeInven[i].item.copyInto(s_ptr.storeInven[i + 1].item);
			s_ptr.storeInven[i + 1].cost = s_ptr.storeInven[i].cost;
		}
		i_ptr.copyInto(s_ptr.storeInven[pos].item);
		s_ptr.storeInven[pos].cost = -icost;
		s_ptr.storeCounter++;
	}
	
	/**
	 * Add the item in INVEN_MAX to stores inventory. -RAK-
	 * 
	 * @param store_num - Store number
	 * @param ipos - Stores the position in the store's inventory
	 * @param t_ptr - Item being added
	 */
	public static void storeCarry(int store_num, IntPointer ipos, InvenType t_ptr) {
		int item_num, item_val;
		boolean flag;
		int typ, subt;
		IntPointer icost = new IntPointer(), dummy = new IntPointer();
		InvenType i_ptr;
		StoreType s_ptr;
		
		ipos.value(-1);
		if (getSellPrice(store_num, icost, dummy, t_ptr) > 0) {
			s_ptr = Variable.store[store_num];
			item_val = 0;
			item_num = t_ptr.number;
			flag = false;
			typ  = t_ptr.category;
			subt = t_ptr.subCategory;
			do {
				i_ptr = s_ptr.storeInven[item_val].item;
				if (typ == i_ptr.category) {
					/* Adds to other item	*/
					if (subt == i_ptr.subCategory && subt >= Constants.ITEM_SINGLE_STACK_MIN && (subt < Constants.ITEM_GROUP_MIN || i_ptr.misc == t_ptr.misc)) {
						ipos.value(item_val);
						i_ptr.number += item_num;
						/* must set new scost for group items, do this only for items
						 * strictly greater than group_min, not for torches, this
						 * must be recalculated for entire group */
						if (subt > Constants.ITEM_GROUP_MIN) {
							getSellPrice(store_num, icost, dummy, i_ptr);
							s_ptr.storeInven[item_val].cost = -icost.value();
						
						/* must let group objects (except torches) stack over 24
						 * since there may be more than 24 in the group */
						} else if (i_ptr.number > 24) {
							i_ptr.number = 24;
						}
						flag = true;
					}
				} else if (typ > i_ptr.category) {
					/* Insert into list		*/
					insertStore(store_num, item_val, icost.value(), t_ptr);
					flag = true;
					ipos.value(item_val);
				}
				item_val++;
			} while ((item_val < s_ptr.storeCounter) && (!flag));
			if (!flag) {	/* Becomes last item in list	*/
				insertStore(store_num, s_ptr.storeCounter, icost.value(), t_ptr);
				ipos.value(s_ptr.storeCounter - 1);
			}
		}
	}
	
	/**
	 * Destroy an item in the stores inventory.  Note that if
	 * "one_of" is false, an entire slot is destroyed. -RAK-
	 * 
	 * @param store_num - Store number
	 * @param item_val - Item's position in the store's inventory
	 * @param one_of - If false, destroy entire slot
	 */
	public static void storeDestroy(int store_num, int item_val, boolean one_of) {
		int j, number;
		StoreType s_ptr;
		InvenType i_ptr;
		
		s_ptr = Variable.store[store_num];
		i_ptr = s_ptr.storeInven[item_val].item;
		
		/* for single stackable objects, only destroy one half on average,
		 * this will help ensure that general store and alchemist have
		 * reasonable selection of objects */
		if ((i_ptr.subCategory >= Constants.ITEM_SINGLE_STACK_MIN) && (i_ptr.subCategory <= Constants.ITEM_SINGLE_STACK_MAX)) {
			if (one_of) {
				number = 1;
			} else {
				number = Rnd.randomInt(i_ptr.number);
			}
		} else {
			number = i_ptr.number;
		}
		
		if (number != i_ptr.number) {
			i_ptr.number -= number;
		} else {
			for (j = item_val; j < s_ptr.storeCounter - 1; j++) {
				s_ptr.storeInven[j + 1].item.copyInto(s_ptr.storeInven[j].item);
				s_ptr.storeInven[j].cost = s_ptr.storeInven[j + 1].cost;
			}
			Desc.copyIntoInventory(s_ptr.storeInven[s_ptr.storeCounter - 1].item, Constants.OBJ_NOTHING);
			s_ptr.storeInven[s_ptr.storeCounter - 1].cost = 0;
			s_ptr.storeCounter--;
		}
	}
	
	/**
	 * Initializes the stores with owners. -RAK-
	 */
	public static void storeInit() {
		int i, j, k;
		StoreType s_ptr;
		
		i = Constants.MAX_OWNERS / Constants.MAX_STORES;
		for (j = 0; j < Constants.MAX_STORES; j++) {
			s_ptr = Variable.store[j];
			s_ptr.owner = Constants.MAX_STORES * (Rnd.randomInt(i) - 1) + j;
			s_ptr.currInsult = 0;
			s_ptr.storeOpen = 0;
			s_ptr.storeCounter	= 0;
			s_ptr.goodBuy = 0;
			s_ptr.badBuy = 0;
			for (k = 0; k < Constants.STORE_INVEN_MAX; k++) {
				Desc.copyIntoInventory(s_ptr.storeInven[k].item, Constants.OBJ_NOTHING);
				s_ptr.storeInven[k].cost = 0;
			}
		}
	}
	
	/**
	 * Creates an item and inserts it into store's inventory. -RAK-
	 * 
	 * @param store_num - Store number
	 */
	public static void storeCreate(int store_num) {
		int i, tries;
		int cur_pos;
		IntPointer dummy = new IntPointer();
		StoreType s_ptr;
		InvenType t_ptr;
		
		tries = 0;
		cur_pos = Misc1.popTreasure();
		s_ptr = Variable.store[store_num];
		do {
			i = Tables.storeChoice[store_num][Rnd.randomInt(Constants.STORE_CHOICES) - 1];
			Desc.copyIntoInventory(Treasure.treasureList[cur_pos], i);
			Misc2.addMagicToTreasure(cur_pos, Constants.OBJ_TOWN_LEVEL);
			t_ptr = Treasure.treasureList[cur_pos];
			if (isStoreFull(t_ptr, store_num)) {
				if ((t_ptr.cost > 0) &&	(t_ptr.cost < Tables.owners[s_ptr.owner].maxCost)) {
					/* Item must be good	*/
					/* equivalent to calling ident_spell(), except will not
					 * change the object_ident array */
					Desc.setStoreBought(t_ptr);
					storeCarry(store_num, dummy, t_ptr);
					tries = 10;
				}
			}
			tries++;
		} while (tries <= 3);
		Misc1.pusht(cur_pos);
	}
	
	/**
	 * Initialize and up-keep the store's inventory. -RAK-
	 */
	public static void storeInventoryInit() {
		int i, j;
		StoreType s_ptr;
		
		for (i = 0; i < Constants.MAX_STORES; i++) {
			s_ptr = Variable.store[i];
			s_ptr.currInsult = 0;
			if (s_ptr.storeCounter >= Constants.STORE_MIN_INVEN) {
				j = Rnd.randomInt(Constants.STORE_TURN_AROUND);
				if (s_ptr.storeCounter >= Constants.STORE_MAX_INVEN) {
					j += 1 + s_ptr.storeCounter - Constants.STORE_MAX_INVEN;
				}
				while (--j >= 0) {
					storeDestroy(i, Rnd.randomInt(s_ptr.storeCounter) - 1, false);
				}
			}
			
			if (s_ptr.storeCounter <= Constants.STORE_MAX_INVEN) {
				j = Rnd.randomInt(Constants.STORE_TURN_AROUND);
				if (s_ptr.storeCounter < Constants.STORE_MIN_INVEN) {
					j += Constants.STORE_MIN_INVEN - s_ptr.storeCounter;
				}
				while (--j >= 0) {
					storeCreate(i);
				}
			}
		}
	}
	
	/**
	 * Eliminate need to bargain if player has haggled well in the past. -DJB-
	 * 
	 * @param store_num - Store number
	 * @param minprice - Minimum selling price
	 * @return Whether the player needs to haggle
	 */
	public static boolean doesNotNeedToBargain(int store_num, int minprice) {
		boolean flagnoneed;
		int bargain_record;
		StoreType s_ptr;
		
		s_ptr = Variable.store[store_num];
		if (s_ptr.goodBuy == Constants.MAX_SHORT) {
			return true;
		}
		bargain_record = (s_ptr.goodBuy - 3 * s_ptr.badBuy - 5);
		flagnoneed = ((bargain_record > 0)
				&& ((long)bargain_record * (long)bargain_record > minprice/50));
		return flagnoneed;
	}
	
	/**
	 * Update the bargain info. -DJB-
	 * 
	 * @param store_num - Store number
	 * @param price - Store price
	 * @param minprice - Minimum selling price
	 */
	public static void updateBargain(int store_num, int price, int minprice) {
		StoreType s_ptr;
		
		s_ptr = Variable.store[store_num];
		if (minprice > 9) {
			if (price == minprice) {
				if (s_ptr.goodBuy < Constants.MAX_SHORT) {
					s_ptr.goodBuy++;
				}
			} else {
				if (s_ptr.badBuy < Constants.MAX_SHORT) {
					s_ptr.badBuy++;
				}
			}
		}
	}
}
