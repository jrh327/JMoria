/*
 * CreatureType.java: creature object
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
package net.jonhopkins.moria.types;

public final class CreatureType {
	/**
	 * Description of creature
	 */
	public String name;
	
	/**
	 * Bit field
	 */
	public int cmove;
	
	/**
	 * Creature spells
	 */
	public int spells;
	
	/**
	 * Bit field
	 */
	public int cdefense;
	
	/**
	 * Experience value for kill
	 */
	public int mexp;
	
	/**
	 * Inactive counter/10
	 */
	public int sleep;
	
	/**
	 * Area affect radius
	 */
	public int aoeRadius;
	
	/**
	 * AC
	 */
	public int armorClass;
	
	/**
	 * Movement speed+10
	 */
	public int speed;
	
	/**
	 * Character representation
	 */
	public char cchar;
	
	/**
	 * Creatures hit die
	 */
	public int[] hitDie = new int[2];
	
	/**
	 * Type attack and damage
	 */
	public int[] damage = new int[4];
	
	/**
	 * Level of creature
	 */
	public int level;
	
	public CreatureType(String name, int cmove, int spells, int cdefense,
			int mexp, int sleep, int aaf, int ac, int speed, char cchar,
			int hd[], int damage[], int level) {
		this.name = name;
		this.cmove = cmove;
		this.spells = spells;
		this.cdefense = cdefense;
		this.mexp = mexp;
		this.sleep = sleep;
		this.aoeRadius = aaf;
		this.armorClass = ac;
		this.speed = speed;
		this.cchar = cchar;
		this.hitDie = hd;
		this.damage = damage;
		this.level = level;
	}
}
