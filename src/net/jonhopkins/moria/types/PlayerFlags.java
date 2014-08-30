/**
 * PlayerFlags.java: global type declarations
 * <p>
 * Copyright (c) 2007 James E. Wilson, Robert A. Koeneke
 * <br>
 * Copyright (c) 2014 Jon Hopkins
 * <p>
 * This file is part of Moria.
 * <p>
 * Moria is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * Moria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Moria.  If not, see <http://www.gnu.org/licenses/>.
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
	public int food_digested;
	
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
	public int image;
	
	/**
	 * Protect against evil
	 */
	public int protevil;
	
	/**
	 * Increases AC
	 */
	public int invuln;
	
	/**
	 * Heroism
	 */
	public int hero;
	
	/**
	 * Super Heroism
	 */
	public int shero;
	
	/**
	 * Blessed
	 */
	public int blessed;
	
	/**
	 * Timed heat resist
	 */
	public int resist_heat;
	
	/**
	 * Timed cold resist
	 */
	public int resist_cold;
	
	/**
	 * Timed see invisible
	 */
	public int detect_inv;
	
	/**
	 * Timed teleport level
	 */
	public int word_recall;
	
	/**
	 * See warm creatures
	 */
	public int see_infra;
	
	/**
	 * Timed infra vision
	 */
	public int tim_infra;
	
	/**
	 * Can see invisible
	 */
	public boolean see_inv;
	
	/**
	 * Random teleportation
	 */
	public int teleport;
	
	/**
	 * Never paralyzed
	 */
	public boolean free_act;
	
	/**
	 * Lower food needs
	 */
	public boolean slow_digest;
	
	/**
	 * Aggravate monsters
	 */
	public int aggravate;
	
	/**
	 * Resistance to fire
	 */
	public int fire_resist;
	
	/**
	 * Resistance to cold
	 */
	public int cold_resist;
	
	/**
	 * Resistance to acid
	 */
	public int acid_resist;
	
	/**
	 * Regenerate hit points
	 */
	public boolean regenerate;
	
	/**
	 * Resistance to light
	 */
	public int lght_resist;
	
	/**
	 * No falling damage
	 */
	public int ffall;
	
	/**
	 * Keep strength
	 */
	public boolean sustain_str;
	
	/**
	 * Keep intelligence
	 */
	public boolean sustain_int;
	
	/**
	 * Keep wisdom
	 */
	public boolean sustain_wis;
	
	/**
	 * Keep constitution
	 */
	public boolean sustain_con;
	
	/**
	 * Keep dexterity
	 */
	public boolean sustain_dex;
	
	/**
	 * Keep charisma
	 */
	public boolean sustain_chr;
	
	/**
	 * Glowing hands.
	 */
	public boolean confuse_monster;
	
	/**
	 * Number of spells can learn.
	 */
	public int new_spells;
}
