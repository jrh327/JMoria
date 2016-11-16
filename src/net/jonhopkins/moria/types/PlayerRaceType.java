/*
 * PlayerRaceType.java: player race information
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

public final class PlayerRaceType {
	public PlayerRaceType(String race, int adjStr, int adjInt, int adjWis,
			int adjDex, int adjCon, int adjChr, int baseAge, int maxAge,
			int mBaseHt, int mMaxHt, int mBaseWt, int mMaxWt,
			int fBaseHt, int fMaxHt, int fBaseWt, int fMaxWt, int disarm,
			int search, int stealth, int freqOfSearch, int baseToHit,
			int baseToHitBow, int savingThrow, int hitDie, int seeInfrared,
			int exp, int classtype) {
		this.raceType = race;
		this.strAdjust = adjStr;
		this.intAdjust = adjInt;
		this.wisAdjust = adjWis;
		this.dexAdjust = adjDex;
		this.conAdjust = adjCon;
		this.chrAdjust = adjChr;
		this.baseAge = baseAge;
		this.maxAge = maxAge;
		this.baseHeightMale = mBaseHt;
		this.modHeightMale = mMaxHt;
		this.baseWeightMale = mBaseWt;
		this.modWeightMale = mMaxWt;
		this.baseHeightFemale = fBaseHt;
		this.modHeightFemale = fMaxHt;
		this.baseWeightFemale = fBaseWt;
		this.modWeightFemale = fMaxWt;
		this.baseDisarmChance = disarm;
		this.baseSearchChance = search;
		this.stealth = stealth;
		this.freqOfSearch = freqOfSearch;
		this.baseToHit = baseToHit;
		this.baseToHitBow = baseToHitBow;
		this.baseSavingThrow = savingThrow;
		this.baseHitDie = hitDie;
		this.seeInfrared = seeInfrared;
		this.baseExpFactor = exp;
		this.rtclass = classtype;
	}
	
	/**
	 * Type of race
	 */
	public String raceType;
	
	/**
	 * Adjustment to strength
	 */
	public int strAdjust;
	
	/**
	 * Adjustment to intelligence
	 */
	public int intAdjust;
	
	/**
	 * Adjustment to wisdom
	 */
	public int wisAdjust;
	
	/**
	 * Adjustment to dexterity
	 */
	public int dexAdjust;
	
	/**
	 * Adjustment to constitution
	 */
	public int conAdjust;
	
	/**
	 * Adjustment to charisma
	 */
	public int chrAdjust;
	
	/**
	 * Base age of character
	 */
	public int baseAge;
	
	/**
	 * Maximum age of character
	 */
	public int maxAge;
	
	/**
	 * Base height for males
	 */
	public int baseHeightMale;
	
	/**
	 * Modified height for males
	 */
	public int modHeightMale;
	
	/**
	 * Base weight for males
	 */
	public int baseWeightMale;
	
	/**
	 * Modified weight for males
	 */
	public int modWeightMale;
	
	/**
	 * Base height females
	 */
	public int baseHeightFemale;
	
	/**
	 * Modified height for females
	 */
	public int modHeightFemale;
	
	/**
	 * Base weight for female
	 */
	public int baseWeightFemale;
	
	/**
	 * Modified weight for females
	 */
	public int modWeightFemale;
	
	/**
	 * Base chance to disarm
	 */
	public int baseDisarmChance;
	
	/**
	 * Base chance for search
	 */
	public int baseSearchChance;
	
	/**
	 * Stealth of character
	 */
	public int stealth;
	
	/**
	 * Frequency of auto search
	 */
	public int freqOfSearch;
	
	/**
	 * Adjusted base chance to hit
	 */
	public int baseToHit;
	
	/**
	 * Adjusted base to hit with bows
	 */
	public int baseToHitBow;
	
	/**
	 * Race base for saving throw
	 */
	public int baseSavingThrow;
	
	/**
	 * Base hit points for race
	 */
	public int baseHitDie;
	
	/**
	 * See infrared
	 */
	public int seeInfrared;
	
	/**
	 * Base experience factor
	 */
	public int baseExpFactor;
	
	/**
	 * Bit field for class types
	 */
	public int rtclass;
}
