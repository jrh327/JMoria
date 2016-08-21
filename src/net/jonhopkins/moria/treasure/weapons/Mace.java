/*
 * Mace.java: item object
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
package net.jonhopkins.moria.treasure.weapons;

import net.jonhopkins.moria.Constants;
import net.jonhopkins.moria.types.TreasureType;

public class Mace extends TreasureType {
	public Mace() {
		this.name = "& Mace";
		this.flags = 0x00000000L;
		this.tval = Constants.TV_HAFTED;
		this.tchar = '\\';
		this.p1 = 0;
		this.cost = 130;
		this.subval = 7;
		this.number = 1;
		this.weight = 120;
		this.tohit = 0;
		this.todam = 0;
		this.ac = 0;
		this.toac = 0;
		this.damage = new int[] { 2, 4 };
		this.level = 6;
	}
}
