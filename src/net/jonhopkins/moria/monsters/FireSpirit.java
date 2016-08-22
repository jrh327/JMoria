/*
 * FireSpirit.java: creature object
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

public class FireSpirit extends CreatureType {
	public FireSpirit() {
		this.name = "Fire Spirit";
		this.cmove = 0xAL;
		this.spells = 0x800000L;
		this.cdefense = 0x3010;
		this.mexp = 66;
		this.sleep = 20;
		this.aaf = 16;
		this.ac = 30;
		this.speed = 12;
		this.cchar = 'E';
		this.hd = new int[] { 10, 8 };
		this.damage = new int[] { 101, 0, 0, 0 };
		this.level = 18;
	}
}
