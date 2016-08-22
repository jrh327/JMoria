/*
 * IridescentBeetle.java: creature object
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

public class IridescentBeetle extends CreatureType {
	public IridescentBeetle() {
		this.name = "Iridescent Beetle";
		this.cmove = 0xAL;
		this.spells = 0x00000000L;
		this.cdefense = 0x2;
		this.mexp = 850;
		this.sleep = 30;
		this.aaf = 16;
		this.ac = 60;
		this.speed = 11;
		this.cchar = 'K';
		this.hd = new int[] { 32, 8 };
		this.damage = new int[] { 45, 10, 146, 0 };
		this.level = 37;
	}
}
