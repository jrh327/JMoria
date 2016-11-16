/*
 * ClassType.java: character class
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

public final class ClassType {
	public ClassType(String title, int hd, int dis, int srh, int stl, int fos,
			int bth, int bthb, int sav, int adjStr, int adjInt, int adjWis,
			int adjDex, int adjCon, int adjChr, int spell, int exp,
			int firstSpellLevel) {
		this.title = title;
		this.adjHitpoints = hd;
		this.modDisarm = dis;
		this.modSearch = srh;
		this.modStealth = stl;
		this.modFreqOfSearch = fos;
		this.modBaseToHit = bth;
		this.modBaseToHitBow = bthb;
		this.modSavingThrow = sav;
		this.modStrAdjust = adjStr;
		this.modIntAdjust = adjInt;
		this.modWisAdjust = adjWis;
		this.modDexAdjust = adjDex;
		this.modConAdjust = adjCon;
		this.modChrAdjust = adjChr;
		this.spell = spell;
		this.modExpFactor = exp;
		this.firstSpellLevel = firstSpellLevel;
	}
	
	/**
	 * Type of class
	 */
	public String title;
	
	/**
	 * Adjust hit points
	 */
	public int adjHitpoints;
	
	/**
	 * Class modifier disarming traps
	 */
	public int modDisarm;
	
	/**
	 * Class modifier to searching
	 */
	public int modSearch;
	
	/**
	 * Class modifier to stealth
	 */
	public int modStealth;
	
	/**
	 * Class modifier to frequency-of-search
	 */
	public int modFreqOfSearch;
	
	/**
	 * Class modifier to base to hit
	 */
	public int modBaseToHit;
	
	/**
	 * Class modifier to base to hit - bows
	 */
	public int modBaseToHitBow;
	
	/**
	 * Class modifier to save
	 */
	public int modSavingThrow;
	
	/**
	 * Class modifier for strength
	 */
	public int modStrAdjust;
	
	/**
	 * Class modifier for intelligence
	 */
	public int modIntAdjust;
	
	/**
	 * Class modifier for wisdom
	 */
	public int modWisAdjust;
	
	/**
	 * Class modifier for dexterity
	 */
	public int modDexAdjust;
	
	/**
	 * Class modifier for constitution
	 */
	public int modConAdjust;
	
	/**
	 * Class modifier for charisma
	 */
	public int modChrAdjust;
	
	/**
	 * Class use mage spells
	 */
	public int spell;
	
	/**
	 * Class experience factor
	 */
	public int modExpFactor;
	
	/**
	 * First level where class can use spells.
	 */
	public int firstSpellLevel;
}
