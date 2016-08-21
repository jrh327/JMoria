/* 
 * Config.java: configuration definitions
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
package net.jonhopkins.moria;

public class Config {
	/* Person to bother if something goes wrong.  */
	/* Recompile files.c and misc2.c if this changes.  */
	public static final String WIZARD	= "David Grabiner";
	/* The wizard password and wizard uid are no longer used.  */
	
	/* Files used by moria, set these to valid pathnames for your system.  */
	/* Files which can be in a varying place */
	public static final String MORIA_SAV		= "files/moriasav";
	public static final String MORIA_TOP		= "files/moriatop";
	public static final String MORIA_MOR		= "files/news";
	public static final String MORIA_TOP_NAME	= "files/scores";
	public static final String MORIA_SAV_NAME	= "files/MORIA.SAV";
	public static final String MORIA_CNF_NAME	= "files/MORIA.CNF";
	public static final String MORIA_HELP		= "files/roglcmds.hlp";
	public static final String MORIA_ORIG_HELP	= "files/origcmds.hlp";
	public static final String MORIA_WIZ_HELP	= "files/rwizcmds.hlp";
	public static final String MORIA_OWIZ_HELP	= "files/owizcmds.hlp";
	public static final String MORIA_WELCOME	= "files/welcome.hlp";
	public static final String MORIA_VER		= "files/version.hlp";
	
	/* This sets the default user interface.  */
	/* To use the original key bindings (keypad for movement) set ROGUE_LIKE
	 * to FALSE; to use the rogue-like key bindings (vi style movement)
	 * set ROGUE_LIKE to TRUE.  */
	/* If you change this, you only need to recompile main.c.  */
	public static final boolean ROGUE_LIKE = false;
}
