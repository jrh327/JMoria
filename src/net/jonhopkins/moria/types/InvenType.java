/**
 * InvenType.java: global type declarations
 * <p>
 * Copyright (c) 2007 James E. Wilson, Robert A. Koeneke
 * <br>
 * Copyright (c) 2014 Jon Hopkins
 * <p>
 * This file is part of Moria.
 * <p>
 * Moria is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * Moria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Moria.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jonhopkins.moria.types;

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
	public int name2;
	
	/**
	 * Object inscription
	 */
	public String inscrip;
	
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
	
	/**
	 * Identify information
	 */
	public int ident;
}
