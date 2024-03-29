/*
 * StoreOwnerType.java: store owner object
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

public final class StoreOwnerType {
	public StoreOwnerType(String n, int c, int max_inf, int min_inf, int h, int r, int max_ins) {
		owner_name = n;
		max_cost = c;
		max_inflate = max_inf;
		min_inflate = min_inf;
		haggle_per = h;
		owner_race = r;
		insult_max = max_ins;
	}
	
	public String owner_name;
	public int max_cost;
	public int max_inflate;
	public int min_inflate;
	public int haggle_per;
	public int owner_race;
	public int insult_max;
}
