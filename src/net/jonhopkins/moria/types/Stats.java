/**
 * Stats.java: global type declarations
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

/* Stats now kept in arrays, for more efficient access. -CJS- */
public final class Stats {
	
	/**
	 *  What is restored
	 */
	public int[] max_stat = new int[6];
	
	/**
	 * What is natural
	 */
	public int[] cur_stat = new int[6];
	
	/**
	 * What is modified, may be +/-
	 */
	public int[] mod_stat = new int[6];
	
	/**
	 * What is used
	 */
	public int[] use_stat = new int[6];
}
