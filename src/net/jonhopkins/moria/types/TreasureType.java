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

/* only damage, ac, and tchar are constant; level could possibly be made
 * constant by changing index instead; all are used rarely */
/* extra fields x and y for location in dungeon would simplify pusht() */

public final class TreasureType {
	public TreasureType(String _name, long _flags, int _tval, int _tchar, int _p1, int _cost, int _subval,
			int _number, int _weight, int _tohit, int _todam, int _ac, int _toac, int[] _damage, int _level) {
		name = _name;
		flags = _flags;
		tval = _tval;
		tchar = _tchar;
		p1 = _p1;
		cost = _cost;
		subval = _subval;
		number = _number;
		weight = _weight;
		tohit = _tohit;
		todam = _todam;
		ac = _ac;
		toac = _toac;
		damage = _damage;
		level = _level;
	}
	
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
	public int tchar;
	
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
}
