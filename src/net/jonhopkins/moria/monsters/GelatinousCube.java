/*
 * GelatinousCube.java: creature object
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

public class GelatinousCube extends CreatureType {
	public GelatinousCube() {
		this.name = "Gelatinous Cube";
		this.cmove = 0x2F98000AL;
		this.spells = 0x200000L;
		this.cdefense = 0x1020;
		this.mexp = 36;
		this.sleep = 1;
		this.aaf = 12;
		this.ac = 18;
		this.speed = 10;
		this.cchar = 'C';
		this.hd = new int[] { 45, 8 };
		this.damage = new int[] { 115, 0, 0, 0 };
		this.level = 16;
	}
}
