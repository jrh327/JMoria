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
	public PlayerRaceType(String race, int adj_str, int adj_int, int adj_wis,
			int adj_dex, int adj_con, int adj_chr, int base_age, int max_age,
			int mbase_ht, int mmax_ht, int mbase_wt, int mmax_wt,
			int fbase_ht, int fmax_ht, int fbase_wt, int fmax_wt, int dis,
			int srh, int stl, int fos, int bth, int bthb, int sav, int hitdie,
			int inf, int exp, int classtype) {
		this.trace = race;
		this.str_adj = adj_str;
		this.int_adj = adj_int;
		this.wis_adj = adj_wis;
		this.dex_adj = adj_dex;
		this.con_adj = adj_con;
		this.chr_adj = adj_chr;
		this.b_age = base_age;
		this.m_age = max_age;
		this.m_b_ht = mbase_ht;
		this.m_m_ht = mmax_ht;
		this.m_b_wt = mbase_wt;
		this.m_m_wt = mmax_wt;
		this.f_b_ht = fbase_ht;
		this.f_m_ht = fmax_ht;
		this.f_b_wt = fbase_wt;
		this.f_m_wt = fmax_wt;
		this.b_dis = dis;
		this.srh = srh;
		this.stl = stl;
		this.fos = fos;
		this.bth = bth;
		this.bthb = bthb;
		this.bsav = sav;
		this.bhitdie = hitdie;
		this.infra = inf;
		this.b_exp = exp;
		this.rtclass = classtype;
	}
	
	/**
	 * Type of race
	 */
	public String trace;
	
	/**
	 * Adjustment to strength
	 */
	public int str_adj;
	
	/**
	 * Adjustment to intelligence
	 */
	public int int_adj;
	
	/**
	 * Adjustment to wisdom
	 */
	public int wis_adj;
	
	/**
	 * Adjustment to dexterity
	 */
	public int dex_adj;
	
	/**
	 * Adjustment to constitution
	 */
	public int con_adj;
	
	/**
	 * Adjustment to charisma
	 */
	public int chr_adj;
	
	/**
	 * Base age of character
	 */
	public int b_age;
	
	/**
	 * Maximum age of character
	 */
	public int m_age;
	
	/**
	 * Base height for males
	 */
	public int m_b_ht;
	
	/**
	 * Modified height for males
	 */
	public int m_m_ht;
	
	/**
	 * Base weight for males
	 */
	public int m_b_wt;
	
	/**
	 * Modified weight for males
	 */
	public int m_m_wt;
	
	/**
	 * Base height females
	 */
	public int f_b_ht;
	
	/**
	 * Modified height for females
	 */
	public int f_m_ht;
	
	/**
	 * Base weight for female
	 */
	public int f_b_wt;
	
	/**
	 * Modified weight for females
	 */
	public int f_m_wt;
	
	/**
	 * Base chance to disarm
	 */
	public int b_dis;
	
	/**
	 * Base chance for search
	 */
	public int srh;
	
	/**
	 * Stealth of character
	 */
	public int stl;
	
	/**
	 * Frequency of auto search
	 */
	public int fos;
	
	/**
	 * Adjusted base chance to hit
	 */
	public int bth;
	
	/**
	 * Adjusted base to hit with bows
	 */
	public int bthb;
	
	/**
	 * Race base for saving throw
	 */
	public int bsav;
	
	/**
	 * Base hit points for race
	 */
	public int bhitdie;
	
	/**
	 * See infrared
	 */
	public int infra;
	
	/**
	 * Base experience factor
	 */
	public int b_exp;
	
	/**
	 * Bit field for class types
	 */
	public int rtclass;
}
