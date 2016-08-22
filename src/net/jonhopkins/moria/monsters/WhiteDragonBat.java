/*
 * WhiteDragonBat.java: creature object
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
package net.jonhopkins.moria.monsters;

import net.jonhopkins.moria.types.CreatureType;

public class WhiteDragonBat extends CreatureType {
	public WhiteDragonBat() {
		this.name = "White Dragon Bat";
		this.cmove = 0x12L;
		this.spells = 0x400004L;
		this.cdefense = 0x162;
		this.mexp = 40;
		this.sleep = 50;
		this.aaf = 12;
		this.ac = 20;
		this.speed = 13;
		this.cchar = 'b';
		this.hd = new int[] { 2, 6 };
		this.damage = new int[] { 121, 0, 0, 0 };
		this.level = 14;
	}
}
