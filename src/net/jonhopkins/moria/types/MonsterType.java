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
	
	public MonsterType(int hp, int csleep, int cspeed, int mptr, int fy, 
			int fx, int cdis, boolean ml, int stunned, int confused) {
		this.hp = hp;
		this.csleep = csleep;
		this.cspeed = cspeed;
		this.mptr = mptr;
		this.fy = fy;
		this.fx = fx;
		this.cdis = cdis;
		this.ml = ml;
		this.stunned = stunned;
		this.confused = confused;
	}
	
	public void copyInto(MonsterType monster) {
		monster.hp = this.hp;
		monster.csleep = this.csleep;
		monster.cspeed = this.cspeed;
		monster.mptr = this.mptr;
		monster.fy = this.fy;
		monster.fx = this.fx;
		monster.cdis = this.cdis;
		monster.ml = this.ml;
		monster.stunned = this.stunned;
		monster.confused = this.confused;
	}
}
