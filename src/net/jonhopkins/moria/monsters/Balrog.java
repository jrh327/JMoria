/*
 * Balrog.java: creature object
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

public class Balrog extends CreatureType {
	public Balrog() {
		this.name = "Balrog";
		this.cmove = 0xFF1F0002L;
		this.spells = 0x81C743L;
		this.cdefense = 0x5004;
		this.mexp = 55000;
		this.sleep = 0;
		this.aaf = 40;
		this.ac = 125;
		this.speed = 13;
		this.cchar = 'B';
		this.hd = new int[] { 75, 40 };
		this.damage = new int[] { 104, 78, 214, 0 };
		this.level = 100;
	}
}
