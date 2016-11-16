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
	public int hitpoints;
	
	/**
	 * Inactive counter
	 */
	public int sleep;
	
	/**
	 * Movement speed
	 */
	public int speed;
	
	/**
	 * Pointer into creature
	 */
	public int index;
	
	/**
	 * Y-coordinate on map
	 */
	public int y;
	
	/**
	 * X-coordinate on map
	 */
	public int x;
	
	/**
	 * Current distance from player
	 */
	public int currDistance;
	public boolean monsterLight;
	public int stunned;
	public int confused;
	
	public MonsterType(int hp, int csleep, int cspeed, int mptr, int fy, 
			int fx, int cdis, boolean ml, int stunned, int confused) {
		this.hitpoints = hp;
		this.sleep = csleep;
		this.speed = cspeed;
		this.index = mptr;
		this.y = fy;
		this.x = fx;
		this.currDistance = cdis;
		this.monsterLight = ml;
		this.stunned = stunned;
		this.confused = confused;
	}
	
	public void copyInto(MonsterType monster) {
		monster.hitpoints = this.hitpoints;
		monster.sleep = this.sleep;
		monster.speed = this.speed;
		monster.index = this.index;
		monster.y = this.y;
		monster.x = this.x;
		monster.currDistance = this.currDistance;
		monster.monsterLight = this.monsterLight;
		monster.stunned = this.stunned;
		monster.confused = this.confused;
	}
}
