/*
 * Quylthulg.java: creature object
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

public class Quylthulg extends CreatureType {
	public Quylthulg() {
		this.name = "Quylthulg";
		this.cmove = 0x10004L;
		this.spells = 0x2017L;
		this.cdefense = 0x5000;
		this.mexp = 200;
		this.sleep = 0;
		this.aaf = 10;
		this.ac = 1;
		this.speed = 11;
		this.cchar = 'Q';
		this.hd = new int[] { 4, 8 };
		this.damage = new int[] { 0, 0, 0, 0 };
		this.level = 20;
	}
}
