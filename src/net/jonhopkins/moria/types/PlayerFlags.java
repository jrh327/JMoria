/*
 * PlayerFlags.java: status effects
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

public final class PlayerFlags {
	
	/**
	 * Status of player
	 */
	public long status;
	
	/**
	 * Rest counter
	 */
	public int rest;
	
	/**
	 * Blindness counter
	 */
	public int blind;
	
	/**
	 * Paralysis counter
	 */
	public int paralysis;
	
	/**
	 * Confusion counter
	 */
	public int confused;
	
	/**
	 * Food counter
	 */
	public int food;
	
	/**
	 * Food per round
	 */
	public int foodDigested;
	
	/**
	 * Protection from evil
	 */
	public int protection;
	
	/**
	 * Current speed adjust
	 */
	public int speed;
	
	/**
	 * Temporary speed change
	 */
	public int fast;
	
	/**
	 * Temporary speed change
	 */
	public int slow;
	
	/**
	 * Fear
	 */
	public int afraid;
	
	/**
	 * Poisoned
	 */
	public int poisoned;
	
	/**
	 * Hallucinate
	 */
	public int imagine;
	
	/**
	 * Protect against evil
	 */
	public int protectFromEvil;
	
	/**
	 * Increases AC
	 */
	public int invulnerability;
	
	/**
	 * Heroism
	 */
	public int hero;
	
	/**
	 * Super Heroism
	 */
	public int superHero;
	
	/**
	 * Blessed
	 */
	public int blessed;
	
	/**
	 * Timed heat resist
	 */
	public int resistHeat;
	
	/**
	 * Timed cold resist
	 */
	public int resistCold;
	
	/**
	 * Timed see invisible
	 */
	public int detectInvisible;
	
	/**
	 * Timed teleport level
	 */
	public int wordRecall;
	
	/**
	 * See warm creatures
	 */
	public int seeInfrared;
	
	/**
	 * Timed infra vision
	 */
	public int timedSeeInfrared;
	
	/**
	 * Can see invisible
	 */
	public boolean seeInvisible;
	
	/**
	 * Random teleportation
	 */
	public int teleport;
	
	/**
	 * Never paralyzed
	 */
	public boolean freeAct;
	
	/**
	 * Lower food needs
	 */
	public boolean slowDigestion;
	
	/**
	 * Aggravate monsters
	 */
	public int aggravate;
	
	/**
	 * Resistance to fire
	 */
	public int fireResistance;
	
	/**
	 * Resistance to cold
	 */
	public int coldResistance;
	
	/**
	 * Resistance to acid
	 */
	public int acidResistance;
	
	/**
	 * Regenerate hit points
	 */
	public boolean regenerate;
	
	/**
	 * Resistance to light
	 */
	public int lightningResistance;
	
	/**
	 * No falling damage
	 */
	public int freeFall;
	
	/**
	 * Keep strength
	 */
	public boolean sustainStr;
	
	/**
	 * Keep intelligence
	 */
	public boolean sustainInt;
	
	/**
	 * Keep wisdom
	 */
	public boolean sustainWis;
	
	/**
	 * Keep constitution
	 */
	public boolean sustainCon;
	
	/**
	 * Keep dexterity
	 */
	public boolean sustainDex;
	
	/**
	 * Keep charisma
	 */
	public boolean sustainChr;
	
	/**
	 * Glowing hands.
	 */
	public boolean confuseMonster;
	
	/**
	 * Number of spells can learn.
	 */
	public int newSpells;
}
