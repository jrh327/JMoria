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
	public CreatureType(String _name, long _cmove, long _spells, int _cdefense, int _mexp, int _sleep,
			int _aaf, int _ac, int _speed, char _cchar, int _hd[], int _damage[], int _level) {
		name = _name;
		cmove = _cmove;
		spells = _spells;
		cdefense = _cdefense;
		mexp = _mexp;
		sleep = _sleep;
		aaf = _aaf;
		ac = _ac;
		speed = _speed;
		cchar = _cchar;
		hd = _hd;
		damage = _damage;
		level = _level;
	}
	
	/**
	 * Description of creature
	 */
	public String name;
	
	/**
	 * Bit field
	 */
	public long cmove;
	
	/**
	 * Creature spells
	 */
	public long spells;
	
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
	public int aaf;
	
	/**
	 * AC
	 */
	public int ac;
	
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
	public int[] hd = new int[2];
	
	/**
	 * Type attack and damage
	 */
	public int[] damage = new int[4];
	
	/**
	 * Level of creature
	 */
	public int level;
}
