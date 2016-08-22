/*
 * EvilIggy.java: creature object
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

public class EvilIggy extends CreatureType {
	public EvilIggy() {
		this.name = "Evil Iggy";
		this.cmove = 0x7F130002L;
		this.spells = 0x1D713L;
		this.cdefense = 0x5004;
		this.mexp = 18000;
		this.sleep = 0;
		this.aaf = 30;
		this.ac = 80;
		this.speed = 12;
		this.cchar = 'p';
		this.hd = new int[] { 60, 40 };
		this.damage = new int[] { 81, 150, 0, 0 };
		this.level = 50;
	}
}
