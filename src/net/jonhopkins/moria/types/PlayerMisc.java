/*
 * PlayerMisc.java: player stats and information
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

public final class PlayerMisc {
	
	/**
	 * Name of character
	 */
	public String name;
	
	/**
	 * Sex of character
	 */
	public boolean isMale;
	
	/**
	 * Gold
	 */
	public int gold;
	
	/**
	 * Max experience
	 */
	public int maxExp;
	
	/**
	 * Current experience
	 */
	public int currExp;
	
	/**
	 * Current experience fraction * 2^16
	 */
	public int expFraction;
	
	/**
	 * Character's age
	 */
	public int age;
	
	/**
	 * Character's height
	 */
	public int height;
	
	/**
	 * Character's weight
	 */
	public int weight;
	
	/**
	 * Character's level
	 */
	public int level = 1;
	
	/**
	 * Maximum dungeon level explored
	 */
	public int maxDungeonLevel;
	
	/**
	 * Chance in search
	 */
	public int searchChance;
	
	/**
	 * Frequency of search
	 */
	public int freqOfSearch;
	
	/**
	 * Base to hit
	 */
	public int baseToHit;
	
	/**
	 * Base to hit with bows
	 */
	public int baseToHitBow;
	
	/**
	 * Mana points
	 */
	public int maxMana;
	
	/**
	 * Maximum hitpoints
	 */
	public int maxHitpoints;
	
	/**
	 * Pluses to hit
	 */
	public int plusToHit;
	
	/**
	 * Pluses to dam
	 */
	public int plusToDamage;
	
	/**
	 * Total AC
	 */
	public int totalArmorClass;
	
	/**
	 * Magical AC
	 */
	public int magicArmorClass;
	
	/**
	 * Display +ToHit
	 */
	public int displayPlusToHit;
	
	/**
	 * Display +ToDam
	 */
	public int displayPlusToDamage;
	
	/**
	 * Display +ToAC
	 */
	public int displayPlusToArmorClass;
	
	/**
	 * Display +ToTAC
	 */
	public int displayPlusTotalArmorClass;
	
	/**
	 * Percent chance to Disarm
	 */
	public int disarmChance;
	
	/**
	 * Saving throw
	 */
	public int savingThrow;
	
	/**
	 * Social Class
	 */
	public int socialClass;
	
	/**
	 * Stealth factor
	 */
	public int stealth;
	
	/**
	 * # of class
	 */
	public int playerClass;
	
	/**
	 * # of race
	 */
	public int playerRace;
	
	/**
	 * Character hit die
	 */
	public int hitDie;
	
	/**
	 * Experience factor
	 */
	public int expFactor;
	
	/**
	 * Current mana points
	 */
	public int currMana;
	
	/**
	 * Current mana fraction * 2^16
	 */
	public int currManaFraction;
	
	/**
	 * Current hitpoints
	 */
	public int currHitpoints;
	
	/**
	 * Current hit fraction * 2^16
	 */
	public int currHitpointsFraction;
	
	/**
	 * History record
	 */
	public String[] history = new String[4];
}
