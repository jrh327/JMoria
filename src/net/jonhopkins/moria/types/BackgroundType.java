/*
 * BackgroundType.java: character background
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

public final class BackgroundType {
	public BackgroundType(String info, int roll, int chart, int next, int bonus) {
		this.info = info;
		this.roll = roll;
		this.chart = chart;
		this.next = next;
		this.bonus = bonus;
	}
	

	/**
	 * History information
	 */
	public String info;
	
	/**
	 * Die roll needed for history
	 */
	public int roll;
	
	/**
	 * Table number
	 */
	public int chart;
	
	/**
	 * Pointer to next table
	 */
	public int next;
	
	/**
	 * Bonus to the Social Class+50
	 */
	public int bonus;
}
