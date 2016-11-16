/*
 * TreasureType.java: item object
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

/* only damage, ac, and tchar are constant; level could possibly be made
 * constant by changing index instead; all are used rarely */
/* extra fields x and y for location in dungeon would simplify pusht() */

public final class TreasureType {
	/**
	 * Object name
	 */
	public String name;
	
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
	public int plusToHit;
	
	/**
	 * Pluses to damage
	 */
	public int plusToDamage;
	
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
	
	public TreasureType(String name, int flags, int tval, char tchar, int p1,
			int cost, int subval, int number, int weight, int tohit, 
			int todam, int ac, int toac, int[] damage, int level) {
		this.name = name;
		this.flags = flags;
		this.category = tval;
		this.tchar = tchar;
		this.misc = p1;
		this.cost = cost;
		this.subCategory = subval;
		this.number = number;
		this.weight = weight;
		this.plusToHit = tohit;
		this.plusToDamage = todam;
		this.armorClass = ac;
		this.plusToArmorClass = toac;
		this.damage = damage;
		this.level = level;
	}
	
	public void copyInto(InvenType item) {
		item.specialName = Constants.SN_NULL;
		item.inscription = "";
		item.flags = this.flags;
		item.category = this.category;
		item.tchar = this.tchar;
		item.misc = this.misc;
		item.cost = this.cost;
		item.subCategory = this.subCategory;
		item.number = this.number;
		item.weight = this.weight;
		item.tohit = this.plusToHit;
		item.plusToDam = this.plusToDamage;
		item.armorClass = this.armorClass;
		item.plusToArmorClass = this.plusToArmorClass;
		item.damage[0] = this.damage[0];
		item.damage[1] = this.damage[1];
		item.level = this.level;
		item.identify = 0;
	}
}
