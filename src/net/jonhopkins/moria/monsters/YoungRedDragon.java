/*
 * YoungRedDragon.java: creature object
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

public class YoungRedDragon extends CreatureType {
	public YoungRedDragon() {
		this.name = "Young Red Dragon";
		this.cmove = 0x1F00000AL;
		this.spells = 0x80100AL;
		this.cdefense = 0x6015;
		this.mexp = 650;
		this.sleep = 50;
		this.aaf = 20;
		this.ac = 60;
		this.speed = 11;
		this.cchar = 'd';
		this.hd = new int[] { 36, 8 };
		this.damage = new int[] { 54, 54, 37, 0 };
		this.level = 35;
	}
}
