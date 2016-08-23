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
	public long flags;
	
	/**
	 * Category number
	 */
	public int tval;
	
	/**
	 * Character representation
	 */
	public char tchar;
	
	/**
	 * Misc. use variable
	 */
	public int p1;
	
	/**
	 * Cost of item
	 */
	public int cost;
	
	/**
	 * Sub-category number
	 */
	public int subval;
	
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
	public int todam;
	
	/**
	 * Normal AC
	 */
	public int ac;
	
	/**
	 * Pluses to AC
	 */
	public int toac;
	
	/**
	 * Damage when hits
	 */
	public int[] damage = new int[2];
	
	/**
	 * Level on which item can first be found
	 */
	public int level;
	
	public TreasureType(String name, long flags, int tval, char tchar, int p1,
			int cost, int subval, int number, int weight, int tohit, 
			int todam, int ac, int toac, int[] damage, int level) {
		this.name = name;
		this.flags = flags;
		this.tval = tval;
		this.tchar = tchar;
		this.p1 = p1;
		this.cost = cost;
		this.subval = subval;
		this.number = number;
		this.weight = weight;
		this.tohit = tohit;
		this.todam = todam;
		this.ac = ac;
		this.toac = toac;
		this.damage = damage;
		this.level = level;
	}
	
	public void copyInto(InvenType item) {
		item.name2 = Constants.SN_NULL;
		item.inscrip = "";
		item.flags = this.flags;
		item.tval = this.tval;
		item.tchar = this.tchar;
		item.p1 = this.p1;
		item.cost = this.cost;
		item.subval = this.subval;
		item.number = this.number;
		item.weight = this.weight;
		item.tohit = this.tohit;
		item.todam = this.todam;
		item.ac = this.ac;
		item.toac = this.toac;
		item.damage[0] = this.damage[0];
		item.damage[1] = this.damage[1];
		item.level = this.level;
		item.ident = 0;
	}
}
