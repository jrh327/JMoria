/*
 * PotionOfHealing.java: item object
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
package net.jonhopkins.moria.treasure.potions;

import net.jonhopkins.moria.Constants;
import net.jonhopkins.moria.types.TreasureType;

public class PotionOfHealing extends TreasureType {
	public PotionOfHealing() {
		this.name = "Healing";
		this.flags = 0x70008000L;
		this.tval = Constants.TV_POTION1;
		this.tchar = '!';
		this.p1 = 200;
		this.cost = 200;
		this.subval = 82;
		this.number = 1;
		this.weight = 4;
		this.tohit = 0;
		this.todam = 0;
		this.ac = 0;
		this.toac = 0;
		this.damage = new int[] { 1, 1 };
		this.level = 12;
	}
}
