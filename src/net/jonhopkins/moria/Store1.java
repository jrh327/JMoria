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
	 * @param item the item whose value is being returned
	 * @return the market value of the item
	 */
	public static int getItemValue(InvenType item) {
		// don't purchase known cursed items
		if ((item.identify & Constants.ID_DAMD) != 0) {
			return 0;
		}
		
		int value = item.cost;
		if ((item.category >= Constants.TV_BOW && item.category <= Constants.TV_SWORD)
				|| (item.category >= Constants.TV_BOOTS && item.category <= Constants.TV_SOFT_ARMOR)) {
			// Weapons and armor
			if (!Desc.arePlussesKnownByPlayer(item)) {
				value = Treasure.objectList[item.index].cost;
			} else if ((item.category >= Constants.TV_BOW) && (item.category <= Constants.TV_SWORD)) {
				if (item.tohit < 0) {
					value = 0;
				} else if (item.plusToDam < 0) {
					value = 0;
				} else if (item.plusToArmorClass < 0) {
					value = 0;
				} else {
					value = item.cost + (item.tohit + item.plusToDam + item.plusToArmorClass) * 100;
				}
			} else if (item.plusToArmorClass < 0) {
				value = 0;
			} else {
				value = item.cost + item.plusToArmorClass * 100;
			}
		} else if (item.category >= Constants.TV_SLING_AMMO && item.category <= Constants.TV_SPIKE) {
			// Ammo
			if (!Desc.arePlussesKnownByPlayer(item)) {
				value = Treasure.objectList[item.index].cost;
			} else if (item.tohit < 0) {
				value = 0;
			} else if (item.plusToDam < 0) {
				value = 0;
			} else if (item.plusToArmorClass < 0) {
				value = 0;
			} else {
				// use 5, because missiles generally appear in groups of 20,
				// so 20 * 5 == 100, which is comparable to weapon bonus above
				value = item.cost + (item.tohit + item.plusToDam + item.plusToArmorClass) * 5;
			}
		} else if (item.category == Constants.TV_SCROLL1
				|| item.category == Constants.TV_SCROLL2
				|| item.category == Constants.TV_POTION1
				|| item.category == Constants.TV_POTION2) {
			// Potions, Scrolls, and Food
			if (!Desc.isKnownByPlayer(item)) {
				value = 20;
			}
		} else if (item.category == Constants.TV_FOOD) {
			if (item.subCategory < (Constants.ITEM_SINGLE_STACK_MIN + Constants.MAX_MUSH)
					&& !Desc.isKnownByPlayer(item)) {
				value = 1;
			}
		} else if ((item.category == Constants.TV_AMULET) || (item.category == Constants.TV_RING)) {
			// Rings and amulets
			if (!Desc.isKnownByPlayer(item)) {
				// player does not know what type of ring/amulet this is
				value = 45;
			} else if (!Desc.arePlussesKnownByPlayer(item)) {
				// player knows what type of ring, but does not know whether it is
				// cursed or not, if refuse to buy cursed objects here, then
				// player can use this to 'identify' cursed objects
				value = Treasure.objectList[item.index].cost;
			}
		} else if ((item.category == Constants.TV_STAFF) || (item.category == Constants.TV_WAND)) {
			// Wands and staffs
			if (!Desc.isKnownByPlayer(item)) {
				if (item.category == Constants.TV_WAND) {
					value = 50;
				} else {
					value = 70;
				}
			} else if (Desc.arePlussesKnownByPlayer(item)) {
				value = item.cost + (item.cost / 20) * item.misc;
			}
		} else if (item.category == Constants.TV_DIGGING) {
			// picks and shovels
			if (!Desc.arePlussesKnownByPlayer(item)) {
				value = Treasure.objectList[item.index].cost;
			} else if (item.misc < 0) {
				value = 0;
			} else {
				// some digging tools start with non-zero p1 values, so only
				// multiply the plusses by 100, make sure result is positive
				value = item.cost + (item.misc - Treasure.objectList[item.index].misc) * 100;
				if (value < 0) {
					value = 0;
				}
			}
		}
		
		// multiply value by number of items if it is a group stack item
		if (item.subCategory > Constants.ITEM_GROUP_MIN) { // do not include torches here
			value = value * item.number;
		}
		
		return value;
	}
	
	/**
	 * Returns the asking price for an item. -RAK-
	 * 
	 * @param storeNum store number
	 * @param maxSell stores the maximum price to sell for
	 * @param minSell stores the minimum price to sell for
	 * @param item item being checked
	 * @return the store's asking price for the item
	 */
	public static int getSellPrice(int storeNum, IntPointer maxSell, IntPointer minSell, InvenType item) {
		StoreType store = Variable.store[storeNum];
		int value = getItemValue(item);
		
		// check item.cost in case it is cursed, check value in case it is damaged
		if (item.cost <= 0 || value <= 0) {
			// don't let the item get into the store inventory
			return 0;
		}
		
		value = value * Tables.raceGoldAdjust[Tables.owners[store.owner].ownerRace][Player.py.misc.playerRace] / 100;
		if (value < 1) {
			value = 1;
		}
		
		maxSell.value(value * Tables.owners[store.owner].maxInflate / 100);
		minSell.value(value * Tables.owners[store.owner].minInflate / 100);
		if (minSell.value() > maxSell.value()) {
			minSell.value(maxSell.value());
		}
		
		return value;
	}
	
	/**
	 * Check to see if the store will be carrying too many objects. -RAK-
	 * 
	 * @param item the item being added
	 * @param storeNum store number
	 * @return true if there is room in the store, otherwise false
	 */
	public static boolean storeHasRoom(InvenType item, int storeNum) {
		StoreType store = Variable.store[storeNum];
		if (store.storeCounter < Constants.STORE_INVEN_MAX) {
			return true;
		}
		
		if (item.subCategory < Constants.ITEM_SINGLE_STACK_MIN) {
			return false;
		}
		
		for (int i = 0; i < store.storeCounter; i++) {
			InvenType storeItem = store.storeInven[i].item;
			// note: items with subval of gte ITEM_SINGLE_STACK_MAX only stack
			// if their subvals match
			if (storeItem.category == item.category && storeItem.subCategory == item.subCategory
					&& storeItem.number + item.number < 256
					&& (item.subCategory < Constants.ITEM_GROUP_MIN || storeItem.misc == item.misc)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Insert INVEN_MAX at given location.
	 * 
	 * @param storeNum store number
	 * @param pos position in the store's inventory to add the item
	 * @param cost cost of the item being added
	 * @param item item being added
	 */
	public static void insertStore(int storeNum, int pos, int cost, InvenType item) {
		StoreType store = Variable.store[storeNum];
		for (int i = store.storeCounter - 1; i >= pos; i--) {
			store.storeInven[i].item.copyInto(store.storeInven[i + 1].item);
			store.storeInven[i + 1].cost = store.storeInven[i].cost;
		}
		item.copyInto(store.storeInven[pos].item);
		store.storeInven[pos].cost = -cost;
		store.storeCounter++;
	}
	
	/**
	 * Add the item in INVEN_MAX to stores inventory. -RAK-
	 * 
	 * @param storeNum store number
	 * @param pos stores the position in the store's inventory
	 * @param item item being added
	 */
	public static void storeCarry(int storeNum, IntPointer pos, InvenType item) {
		IntPointer cost = new IntPointer();
		IntPointer dummy = new IntPointer();
		
		pos.value(-1);
		if (getSellPrice(storeNum, cost, dummy, item) <= 0) {
			return;
		}
		
		StoreType store = Variable.store[storeNum];
		int itemNum = item.number;
		int category = item.category;
		int subCategory = item.subCategory;
		for (int i = 0; i < store.storeCounter; i++) {
			InvenType storeItem = store.storeInven[i].item;
			if (category > storeItem.category) {
				// Insert into list
				insertStore(storeNum, i, cost.value(), item);
				pos.value(i);
				break;
			}
			
			if (category < storeItem.category) {
				continue;
			}
			
			// Adds to other item
			if (subCategory == storeItem.subCategory
					&& subCategory >= Constants.ITEM_SINGLE_STACK_MIN
					&& (subCategory < Constants.ITEM_GROUP_MIN || storeItem.misc == item.misc)) {
				pos.value(i);
				storeItem.number += itemNum;
				
				// must set new scost for group items, do this only for items
				// strictly greater than group_min, not for torches, this
				// must be recalculated for entire group
				if (subCategory > Constants.ITEM_GROUP_MIN) {
					getSellPrice(storeNum, cost, dummy, storeItem);
					store.storeInven[i].cost = -cost.value();
				
				// must let group objects (except torches) stack over 24
				// since there may be more than 24 in the group
				} else if (storeItem.number > 24) {
					storeItem.number = 24;
				}
				break;
			}
		}
		
		if (pos.value() < 0) { // Becomes last item in list
			insertStore(storeNum, store.storeCounter, cost.value(), item);
			pos.value(store.storeCounter - 1);
		}
	}
	
	/**
	 * Destroy an item in the stores inventory. Note that if
	 * "destroyOnlyOne" is false, an entire slot is destroyed. -RAK-
	 * 
	 * @param storeNum store number
	 * @param pos item's position in the store's inventory
	 * @param destroyOnlyOne if false, destroy entire slot
	 */
	public static void storeDestroy(int storeNum, int pos, boolean destroyOnlyOne) {
		StoreType store = Variable.store[storeNum];
		InvenType item = store.storeInven[pos].item;
		
		// for single stackable objects, only destroy one half on average,
		// this will help ensure that general store and alchemist have
		// reasonable selection of objects
		int number;
		if (item.subCategory >= Constants.ITEM_SINGLE_STACK_MIN
				&& item.subCategory <= Constants.ITEM_SINGLE_STACK_MAX) {
			if (destroyOnlyOne) {
				number = 1;
			} else {
				number = Rnd.randomInt(item.number);
			}
		} else {
			number = item.number;
		}
		
		if (number != item.number) {
			item.number -= number;
		} else {
			for (int i = pos; i < store.storeCounter - 1; i++) {
				store.storeInven[i + 1].item.copyInto(store.storeInven[i].item);
				store.storeInven[i].cost = store.storeInven[i + 1].cost;
			}
			Desc.copyIntoInventory(store.storeInven[store.storeCounter - 1].item, Constants.OBJ_NOTHING);
			store.storeInven[store.storeCounter - 1].cost = 0;
			store.storeCounter--;
		}
	}
	
	/**
	 * Initializes the stores with owners. -RAK-
	 */
	public static void storeInit() {
		int i = Constants.MAX_OWNERS / Constants.MAX_STORES;
		for (int j = 0; j < Constants.MAX_STORES; j++) {
			StoreType store = Variable.store[j];
			store.owner = Constants.MAX_STORES * (Rnd.randomInt(i) - 1) + j;
			store.currInsult = 0;
			store.storeOpen = 0;
			store.storeCounter	= 0;
			store.goodBuy = 0;
			store.badBuy = 0;
			
			for (int k = 0; k < Constants.STORE_INVEN_MAX; k++) {
				Desc.copyIntoInventory(store.storeInven[k].item, Constants.OBJ_NOTHING);
				store.storeInven[k].cost = 0;
			}
		}
	}
	
	/**
	 * Creates an item and inserts it into store's inventory. -RAK-
	 * 
	 * @param storeNum store number
	 */
	public static void storeCreate(int storeNum) {
		int pos = Misc1.popTreasure();
		StoreType store = Variable.store[storeNum];
		
		for (int tries = 0; tries < 4; tries++) {
			int i = Tables.storeChoice[storeNum][Rnd.randomInt(Constants.STORE_CHOICES) - 1];
			Desc.copyIntoInventory(Treasure.treasureList[pos], i);
			Misc2.addMagicToTreasure(pos, Constants.OBJ_TOWN_LEVEL);
			InvenType item = Treasure.treasureList[pos];
			if (storeHasRoom(item, storeNum)) {
				if (item.cost > 0 && item.cost < Tables.owners[store.owner].maxCost) {
					// Item must be good
					// equivalent to calling ident_spell(), except will not
					// change the object_ident array
					Desc.setStoreBought(item);
					storeCarry(storeNum, new IntPointer(), item);
					break;
				}
			}
		}
		Misc1.pusht(pos);
	}
	
	/**
	 * Initialize and up-keep the store's inventory. -RAK-
	 */
	public static void storeInventoryInit() {
		for (int i = 0; i < Constants.MAX_STORES; i++) {
			StoreType store = Variable.store[i];
			store.currInsult = 0;
			if (store.storeCounter >= Constants.STORE_MIN_INVEN) {
				int turnAround = Rnd.randomInt(Constants.STORE_TURN_AROUND);
				if (store.storeCounter >= Constants.STORE_MAX_INVEN) {
					turnAround += 1 + store.storeCounter - Constants.STORE_MAX_INVEN;
				}
				while (--turnAround >= 0) {
					storeDestroy(i, Rnd.randomInt(store.storeCounter) - 1, false);
				}
			}
			
			if (store.storeCounter <= Constants.STORE_MAX_INVEN) {
				int turnAround = Rnd.randomInt(Constants.STORE_TURN_AROUND);
				if (store.storeCounter < Constants.STORE_MIN_INVEN) {
					turnAround += Constants.STORE_MIN_INVEN - store.storeCounter;
				}
				while (--turnAround >= 0) {
					storeCreate(i);
				}
			}
		}
	}
	
	/**
	 * Eliminate need to bargain if player has haggled well in the past. -DJB-
	 * 
	 * @param storeNum store number
	 * @param minPrice minimum selling price
	 * @return whether the player needs to haggle
	 */
	public static boolean doesNotNeedToBargain(int storeNum, int minPrice) {
		StoreType store = Variable.store[storeNum];
		if (store.goodBuy == Constants.MAX_SHORT) {
			return true;
		}
		
		int bargainRecord = store.goodBuy - 3 * store.badBuy - 5;
		if (bargainRecord <= 0) {
			return false;
		}
		
		return bargainRecord * bargainRecord > minPrice / 50;
	}
	
	/**
	 * Update the bargain info. -DJB-
	 * 
	 * @param storeNum store number
	 * @param price store price
	 * @param minPrice minimum selling price
	 */
	public static void updateBargain(int storeNum, int price, int minPrice) {
		if (minPrice < 10) {
			return;
		}
		
		StoreType store = Variable.store[storeNum];
		if (price == minPrice) {
			if (store.goodBuy < Constants.MAX_SHORT) {
				store.goodBuy++;
			}
		} else {
			if (store.badBuy < Constants.MAX_SHORT) {
				store.badBuy++;
			}
		}
	}
}
