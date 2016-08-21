/*
 * MonsterType.java: monster object
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

public final class MonsterType {
	public MonsterType(int _hp, int _csleep, int _cspeed, int _mptr, int _fy, int _fx, int _cdis, boolean _ml, int _stunned, int _confused) {
		hp = _hp;
		csleep = _csleep;
		cspeed = _cspeed;
		mptr = _mptr;
		fy = _fy;
		fx = _fx;
		cdis = _cdis;
		ml = _ml;
		stunned = _stunned;
		confused = _confused;
	}
	
	
	/**
	 * Hitpoints
	 */
	public int hp;
	
	/**
	 * Inactive counter
	 */
	public int csleep;
	
	/**
	 * Movement speed
	 */
	public int cspeed;
	
	/**
	 * Pointer into creature
	 */
	public int mptr;
	
	/**
	 * Y-coordinate on map
	 */
	public int fy;
	
	/**
	 * X-coordinate on map
	 */
	public int fx;
	
	/**
	 * Current distance from player
	 */
	public int cdis;
	public boolean ml;
	public int stunned;
	public int confused;
}
