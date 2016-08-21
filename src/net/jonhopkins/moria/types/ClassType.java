/*
 * ClasType.java: character class
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
	public ClassType(String _title, int _hd, int dis, int srh, int stl, int fos, int bth, int bthb, int sav,
			int adj_str, int adj_int, int adj_wis, int adj_dex, int adj_con, int adj_chr, int _spell, int exp, int first_spell_level) {
		title = _title;
		adj_hd = _hd;
		mdis = dis;
		msrh = srh;
		mstl = stl;
		mfos = fos;
		mbth = bth;
		mbthb = bthb;
		msav = sav;
		madj_str = adj_str;
		madj_int = adj_int;
		madj_wis = adj_wis;
		madj_dex = adj_dex;
		madj_con = adj_con;
		madj_chr = adj_chr;
		spell = _spell;
		m_exp = exp;
		first_spell_lev = first_spell_level;
	}
	
	/**
	 * Type of class
	 */
	public String title;
	
	/**
	 * Adjust hit points
	 */
	public int adj_hd;
	
	/**
	 * Class modifier disarming traps
	 */
	public int mdis;
	
	/**
	 * Class modifier to searching
	 */
	public int msrh;
	
	/**
	 * Class modifier to stealth
	 */
	public int mstl;
	
	/**
	 * Class modifier to frequency-of-search
	 */
	public int mfos;
	
	/**
	 * Class modifier to base to hit
	 */
	public int mbth;
	
	/**
	 * Class modifier to base to hit - bows
	 */
	public int mbthb;
	
	/**
	 * Class modifier to save
	 */
	public int msav;
	
	/**
	 * Class modifier for strength
	 */
	public int madj_str;
	
	/**
	 * Class modifier for intelligence
	 */
	public int madj_int;
	
	/**
	 * Class modifier for wisdom
	 */
	public int madj_wis;
	
	/**
	 * Class modifier for dexterity
	 */
	public int madj_dex;
	
	/**
	 * Class modifier for constitution
	 */
	public int madj_con;
	
	/**
	 * Class modifier for charisma
	 */
	public int madj_chr;
	
	/**
	 * Class use mage spells
	 */
	public int spell;
	
	/**
	 * Class experience factor
	 */
	public int m_exp;
	
	/**
	 * First level where class can use spells.
	 */
	public int first_spell_lev;
}
