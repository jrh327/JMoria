/*
 * KillerBoringBeetle.java: creature object
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

public class KillerBoringBeetle extends CreatureType {
	public KillerBoringBeetle() {
		this.name = "Killer Boring Beetle";
		this.cmove = 0xAL;
		this.spells = 0x00000000L;
		this.cdefense = 0x2;
		this.mexp = 70;
		this.sleep = 30;
		this.aaf = 12;
		this.ac = 48;
		this.speed = 11;
		this.cchar = 'K';
		this.hd = new int[] { 18, 8 };
		this.damage = new int[] { 44, 0, 0, 0 };
		this.level = 21;
	}
}
