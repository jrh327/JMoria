/**
 * BackgroundType.java: global type declarations
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

public final class BackgroundType {
	public BackgroundType(String _info, int _roll, int _chart, int _next, int _bonus) {
		info = _info;
		roll = _roll;
		chart = _chart;
		next = _next;
		bonus = _bonus;
	}
	

	/**
	 * History information
	 */
	public String info;
	
	/**
	 * Die roll needed for history
	 */
	public int roll;
	
	/**
	 * Table number
	 */
	public int chart;
	
	/**
	 * Pointer to next table
	 */
	public int next;
	
	/**
	 * Bonus to the Social Class+50
	 */
	public int bonus;
}
