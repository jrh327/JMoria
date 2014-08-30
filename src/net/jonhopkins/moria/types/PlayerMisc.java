/**
 * PlayerMisc.java: global type declarations
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

public final class PlayerMisc {
	
	/**
	 * Name of character
	 */
	public String name;

	/**
	 * Sex of character
	 */
	public boolean male;

	/**
	 * Gold
	 */
	public int au;

	/**
	 * Max experience
	 */
	public int max_exp;

	/**
	 * Current experience
	 */
	public int exp;

	/**
	 * Current experience fraction * 2^16
	 */
	public int exp_frac;

	/**
	 * Character's age
	 */
	public int age;

	/**
	 * Character's height
	 */
	public int ht;

	/**
	 * Character's weight
	 */
	public int wt;

	/**
	 * Character's level
	 */
	public int lev = 1;

	/**
	 * Maximum dungeon level explored
	 */
	public int max_dlv;

	/**
	 * Chance in search
	 */
	public int srh;

	/**
	 * Frequency of search
	 */
	public int fos;

	/**
	 * Base to hit
	 */
	public int bth;

	/**
	 * Base to hit with bows
	 */
	public int bthb;

	/**
	 * Mana points
	 */
	public int mana;

	/**
	 * Maximum hitpoints
	 */
	public int mhp;

	/**
	 * Pluses to hit
	 */
	public int ptohit;

	/**
	 * Pluses to dam
	 */
	public int ptodam;

	/**
	 * Total AC
	 */
	public int pac;

	/**
	 * Magical AC
	 */
	public int ptoac;

	/**
	 * Display +ToHit
	 */
	public int dis_th;

	/**
	 * Display +ToDam
	 */
	public int dis_td;

	/**
	 * Display +ToAC
	 */
	public int dis_ac;

	/**
	 * Display +ToTAC
	 */
	public int dis_tac;

	/**
	 * Percent chance to Disarm
	 */
	public int disarm;

	/**
	 * Saving throw
	 */
	public int save;

	/**
	 * Social Class
	 */
	public int sc;

	/**
	 * Stealth factor
	 */
	public int stl;

	/**
	 * # of class
	 */
	public int pclass;

	/**
	 * # of race
	 */
	public int prace;

	/**
	 * Character hit die
	 */
	public int hitdie;

	/**
	 * Experience factor
	 */
	public int expfact;

	/**
	 * Current mana points
	 */
	public int cmana;

	/**
	 * Current mana fraction * 2^16
	 */
	public int cmana_frac;

	/**
	 * Current hitpoints
	 */
	public int chp;

	/**
	 * Current hit fraction * 2^16
	 */
	public int chp_frac;

	/**
	 * History record
	 */
	public String[] history = new String[4];
}
