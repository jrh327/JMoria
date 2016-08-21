/*
 * Player.java: player specific variable definitions
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

import net.jonhopkins.moria.types.BackgroundType;
import net.jonhopkins.moria.types.ClassType;
import net.jonhopkins.moria.types.PlayerType;
import net.jonhopkins.moria.types.PlayerRaceType;
import net.jonhopkins.moria.types.SpellType;

public class Player {
	private static Player instance;
	private Player() { }
	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		
	}
	
	/* Player record for most player related info */
	public PlayerType py = new PlayerType();
	
	/* player location in dungeon */
	public int char_row;
	public int char_col;
	/* calculated base hp values for player at each level, store them so that
	 * drain life + restore life does not affect hit points */
	public int[] player_hp = new int[Constants.MAX_PLAYER_LEVEL];
	
	/* Class titles for different levels				*/
	//String[MAX_CLASS][MAX_PLAYER_LEVEL]
	public String[][] player_title = {
		/* Warrior	 */
		{"Rookie","Private","Soldier","Mercenary","Veteran(1st)","Veteran(2nd)",
			"Veteran(3rd)","Warrior(1st)","Warrior(2nd)","Warrior(3rd)","Warrior(4th)",
			"Swordsman-1","Swordsman-2","Swordsman-3","Hero","Swashbuckler","Myrmidon",
			"Champion-1","Champion-2","Champion-3","Superhero","Knight","Superior Knt",
			"Gallant Knt","Knt Errant","Guardian Knt","Baron","Duke","Lord (1st)",
			"Lord (2nd)","Lord (3rd)","Lord (4th)","Lord (5th)","Lord (6th)","Lord (7th)",
			"Lord (8th)","Lord (9th)","Lord Gallant","Lord Keeper","Lord Noble"},
		/* Mage		 */
		{"Novice","Apprentice","Trickster-1","Trickster-2","Trickster-3","Cabalist-1",
			"Cabalist-2","Cabalist-3","Visionist","Phantasmist","Shadowist","Spellbinder",
			"Illusionist","Evoker (1st)","Evoker (2nd)","Evoker (3rd)","Evoker (4th)",
			"Conjurer","Theurgist","Thaumaturge","Magician","Enchanter","Warlock",
			"Sorcerer","Necromancer","Mage (1st)","Mage (2nd)","Mage (3rd)","Mage (4th)",
			"Mage (5th)","Wizard (1st)","Wizard (2nd)","Wizard (3rd)","Wizard (4th)",
			"Wizard (5th)","Wizard (6th)","Wizard (7th)","Wizard (8th)","Wizard (9th)",
			"Wizard Lord"},
		/* Priests	 */
		{"Believer","Acolyte(1st)","Acolyte(2nd)","Acolyte(3rd)","Adept (1st)",
			"Adept (2nd)","Adept (3rd)","Priest (1st)","Priest (2nd)","Priest (3rd)",
			"Priest (4th)","Priest (5th)","Priest (6th)","Priest (7th)","Priest (8th)",
			"Priest (9th)","Curate (1st)","Curate (2nd)","Curate (3rd)","Curate (4th)",
			"Curate (5th)","Curate (6th)","Curate (7th)","Curate (8th)","Curate (9th)",
			"Canon (1st)","Canon (2nd)","Canon (3rd)","Canon (4th)","Canon (5th)",
			"Low Lama","Lama-1","Lama-2","Lama-3","High Lama","Great Lama","Patriarch",
			"High Priest","Great Priest","Noble Priest"},
		/* Rogues	 */
		{"Vagabond","Footpad","Cutpurse","Robber","Burglar","Filcher","Sharper",
			"Magsman","Common Rogue","Rogue (1st)","Rogue (2nd)","Rogue (3rd)",
			"Rogue (4th)","Rogue (5th)","Rogue (6th)","Rogue (7th)","Rogue (8th)",
			"Rogue (9th)","Master Rogue","Expert Rogue","Senior Rogue","Chief Rogue",
			"Prime Rogue","Low Thief","Thief (1st)","Thief (2nd)","Thief (3rd)",
			"Thief (4th)","Thief (5th)","Thief (6th)","Thief (7th)","Thief (8th)",
			"Thief (9th)","High Thief","Master Thief","Executioner","Low Assassin",
			"Assassin","High Assassin","Guildsmaster"},
		/* Rangers	 */
		{"Runner (1st)","Runner (2nd)","Runner (3rd)","Strider (1st)","Strider (2nd)",
			"Strider (3rd)","Scout (1st)","Scout (2nd)","Scout (3rd)","Scout (4th)",
			"Scout (5th)","Courser (1st)","Courser (2nd)","Courser (3rd)","Courser (4th)",
			"Courser (5th)","Tracker (1st)","Tracker (2nd)","Tracker (3rd)",
			"Tracker (4th)","Tracker (5th)","Tracker (6th)","Tracker (7th)",
			"Tracker (8th)","Tracker (9th)","Guide (1st)","Guide (2nd)","Guide (3rd)",
			"Guide (4th)","Guide (5th)","Guide (6th)","Guide (7th)","Guide (8th)",
			"Guide (9th)","Pathfinder-1","Pathfinder-2","Pathfinder-3","Ranger",
			"High Ranger","Ranger Lord"},
		/* Paladins	 */
		{"Gallant","Keeper (1st)","Keeper (2nd)","Keeper (3rd)","Keeper (4th)",
			"Keeper (5th)","Keeper (6th)","Keeper (7th)","Keeper (8th)","Keeper (9th)",
			"Protector-1","Protector-2","Protector-3","Protector-4","Protector-5",
			"Protector-6","Protector-7","Protector-8","Defender-1","Defender-2",
			"Defender-3","Defender-4","Defender-5","Defender-6","Defender-7","Defender-8",
			"Warder (1st)","Warder (2nd)","Warder (3rd)","Warder (4th)","Warder (5th)",
			"Warder (6th)","Warder (7th)","Warder (8th)","Warder (9th)","Guardian",
			"Chevalier","Justiciar","Paladin","High Lord"}
	};
	
	/* Base experience levels, may be adjusted up for race and/or class*/
	//long[MAX_PLAYER_LEVEL]
	public int[] player_exp = {
	      10,      25,      45,	     70,      100,      140,      200,      280,
	     380,     500,     650,     850,	 1100,	   1400,     1800,     2300,
	    2900,    3600,    4400,    5400,	 6800,	   8400,    10200,    12500,
	   17500,   25000,   35000,   50000,    75000,	 100000,   150000,   200000,
	  300000,  400000,  500000,  750000,  1500000,  2500000,  5000000, 10000000
	};
	
	/*Race	STR,INT,WIS,DEX,CON,CHR,
		Ages, heights, and weights (male then female)
		Racial Bases for: dis,srh,stl,fos,bth,bthb,bsav,hitdie,
		infra, exp base, choice-classes */
	//Race_type[MAX_RACES]
	public PlayerRaceType[] race = {
	   new PlayerRaceType("Human",	 0,  0,	 0,  0,	 0,  0,
	      14,  6, 72,  6,180, 25, 66,  4,150, 20,
	      0,  0,  0,  0,  0,  0,  0, 10,  0, 100, 0x3F
	    ),
	    new PlayerRaceType("Half-Elf", -1,  1,	 0,  1, -1,  1,
	      24, 16, 66,  6,130, 15, 62,  6,100, 10,
	      2,  6,  1, -1, -1,  5,  3,  9,  2, 110, 0x3F
	    ),
	    new PlayerRaceType("Elf",	-1,  2,	 1,  1, -2,  1,
	      75, 75, 60,  4,100,  6, 54,  4, 80,  6,
	      5,  8,  1, -2, -5, 15,  6,  8,  3, 120, 0x1F
	    ),
	    new PlayerRaceType("Halfling", -2,  2,	 1,  3,	 1,  1,
	      21, 12, 36,  3, 60,  3, 33,  3, 50,  3,
	      15, 12,  4, -5,-10, 20, 18,  6,  4, 110, 0x0B
	    ),
	    new PlayerRaceType("Gnome",	-1,  2,	 0,  2,	 1, -2,
	      50, 40, 42,  3, 90,  6, 39,  3, 75,  3,
	      10,  6,  3, -3, -8, 12, 12,  7,  4, 125, 0x0F
	    ),
	    new PlayerRaceType("Dwarf",	 2, -3,	 1, -2,	 2, -3,
	      35, 15, 48,  3,150, 10, 46,  3,120, 10,
	      2,  7,  -1,  0, 15,  0,  9,  9,  5, 120, 0x05
	    ),
	    new PlayerRaceType("Half-Orc",	 2, -1,	 0,  0,	 1, -4,
	      11,  4, 66,  1,150,  5, 62,  1,120,  5,
	      -3,  0, -1,  3, 12, -5, -3, 10,  3, 110, 0x0D
	    ),
	    new PlayerRaceType("Half-Troll",4, -4, -2, -4,	 3, -6,
	      20, 10, 96, 10,255, 50, 84,  8,225, 40,
	      -5, -1, -2,  5, 20,-10, -8, 12,  3, 120, 0x05
	    )
	 };
	
	/* Background information					*/
	//Backgound_type[MAX_BACKGROUND]
	public BackgroundType[] background = {
	new BackgroundType("You are the illegitimate and unacknowledged child ", 10, 1, 2, 25),
	new BackgroundType("You are the illegitimate but acknowledged child ", 20, 1, 2, 35),
	new BackgroundType("You are one of several children ", 95, 1, 2, 45),
	new BackgroundType("You are the first child ", 100, 1, 2, 50),
	new BackgroundType("of a Serf.  ", 40, 2, 3, 65),
	new BackgroundType("of a Yeoman.  ", 65, 2, 3, 80),
	new BackgroundType("of a Townsman.  ", 80, 2, 3, 90),
	new BackgroundType("of a Guildsman.  ", 90, 2, 3, 105),
	new BackgroundType("of a Landed Knight.  ", 96, 2, 3, 120),
	new BackgroundType("of a Titled Noble.  ", 99, 2, 3, 130),
	new BackgroundType("of a Royal Blood Line.  ", 100, 2, 3,140),
	new BackgroundType("You are the black sheep of the family.  ", 20, 3,50, 20),
	new BackgroundType("You are a credit to the family.  ", 80, 3,50, 55),
	new BackgroundType("You are a well liked child.  ", 100, 3,50, 60),
	new BackgroundType("Your mother was a Green-Elf.  ", 40, 4, 1, 50),
	new BackgroundType("Your father was a Green-Elf.  ", 75, 4, 1, 55),
	new BackgroundType("Your mother was a Grey-Elf.  ", 90, 4, 1, 55),
	new BackgroundType("Your father was a Grey-Elf.  ", 95, 4, 1, 60),
	new BackgroundType("Your mother was a High-Elf.  ", 98, 4, 1, 65),
	new BackgroundType("Your father was a High-Elf.  ", 100, 4, 1, 70),
	new BackgroundType("You are one of several children ", 60, 7, 8, 50),
	new BackgroundType("You are the only child ", 100, 7, 8, 55),
	new BackgroundType("of a Green-Elf ", 75, 8, 9, 50),
	new BackgroundType("of a Grey-Elf ", 95, 8, 9, 55),
	new BackgroundType("of a High-Elf ", 100, 8, 9, 60),
	new BackgroundType("Ranger.  ", 40, 9, 54, 80),
	new BackgroundType("Archer.  ", 70, 9, 54, 90),
	new BackgroundType("Warrior.  ", 87, 9, 54, 110),
	new BackgroundType("Mage.  ", 95, 9, 54, 125),
	new BackgroundType("Prince.  ", 99, 9, 54, 140),
	new BackgroundType("King.  ", 100, 9, 54, 145),
	new BackgroundType("You are one of several children of a Halfling ", 85, 10, 11, 45),
	new BackgroundType("You are the only child of a Halfling ", 100, 10, 11, 55),
	new BackgroundType("Bum.  ", 20, 11, 3, 55),
	new BackgroundType("Tavern Owner.  ", 30, 11, 3, 80),
	new BackgroundType("Miller.  ", 40, 11, 3, 90),
	new BackgroundType("Home Owner.  ", 50, 11, 3, 100),
	new BackgroundType("Burglar.  ", 80, 11, 3, 110),
	new BackgroundType("Warrior.  ", 95, 11, 3,115),
	new BackgroundType("Mage.  ", 99, 11, 3, 125),
	new BackgroundType("Clan Elder.  ", 100, 11, 3, 140),
	new BackgroundType("You are one of several children of a Gnome ", 85, 13, 14, 45),
	new BackgroundType("You are the only child of a Gnome ", 100, 13, 14, 55),
	new BackgroundType("Beggar.  ", 20, 14, 3, 55),
	new BackgroundType("Braggart.  ", 50, 14, 3, 70),
	new BackgroundType("Prankster.  ", 75, 14, 3, 85),
	new BackgroundType("Warrior.  ", 95, 14, 3, 100),
	new BackgroundType("Mage.  ", 100, 14, 3, 125),
	new BackgroundType("You are one of two children of a Dwarven ", 25, 16, 17, 40),
	new BackgroundType("You are the only child of a Dwarven ", 100, 16, 17, 50),
	new BackgroundType("Thief.  ", 10, 17, 18, 60),
	new BackgroundType("Prison Guard.  ", 25, 17, 18, 75),
	new BackgroundType("Miner.  ", 75, 17, 18, 90),
	new BackgroundType("Warrior.  ", 90, 17, 18, 110),
	new BackgroundType("Priest.  ", 99, 17, 18, 130),
	new BackgroundType("King.  ", 100, 17, 18, 150),
	new BackgroundType("You are the black sheep of the family.  ", 15, 18, 57, 10),
	new BackgroundType("You are a credit to the family.  ", 85, 18, 57, 50),
	new BackgroundType("You are a well liked child.  ", 100, 18, 57, 55),
	new BackgroundType("Your mother was an Orc, but it is unacknowledged.  ", 25, 19, 20, 25),
	new BackgroundType("Your father was an Orc, but it is unacknowledged.  ", 100, 19, 20, 25),
	new BackgroundType("You are the adopted child ", 100, 20, 2, 50),
	new BackgroundType("Your mother was a Cave-Troll ", 30, 22, 23, 20),
	new BackgroundType("Your father was a Cave-Troll ", 60, 22, 23, 25),
	new BackgroundType("Your mother was a Hill-Troll ", 75, 22, 23, 30),
	new BackgroundType("Your father was a Hill-Troll ", 90, 22, 23, 35),
	new BackgroundType("Your mother was a Water-Troll ", 95, 22, 23, 40),
	new BackgroundType("Your father was a Water-Troll ", 100, 22, 23, 45),
	new BackgroundType("Cook.  ", 5, 23, 62, 60),
	new BackgroundType("Warrior.  ", 95, 23, 62, 55),
	new BackgroundType("Shaman.  ", 99, 23, 62, 65),
	new BackgroundType("Clan Chief.  ", 100, 23, 62, 80),
	new BackgroundType("You have dark brown eyes, ", 20, 50, 51, 50),
	new BackgroundType("You have brown eyes, ", 60, 50, 51, 50),
	new BackgroundType("You have hazel eyes, ", 70, 50, 51, 50),
	new BackgroundType("You have green eyes, ", 80, 50, 51, 50),
	new BackgroundType("You have blue eyes, ", 90, 50, 51, 50),
	new BackgroundType("You have blue-gray eyes, ", 100, 50, 51, 50),
	new BackgroundType("straight ", 70, 51, 52, 50),
	new BackgroundType("wavy ", 90, 51, 52, 50),
	new BackgroundType("curly ", 100, 51, 52, 50),
	new BackgroundType("black hair, ", 30, 52, 53, 50),
	new BackgroundType("brown hair, ", 70, 52, 53, 50),
	new BackgroundType("auburn hair, ", 80, 52, 53, 50),
	new BackgroundType("red hair, ", 90, 52, 53, 50),
	new BackgroundType("blond hair, ", 100, 52, 53, 50),
	new BackgroundType("and a very dark complexion.", 10, 53, 0, 50),
	new BackgroundType("and a dark complexion.", 30, 53, 0, 50),
	new BackgroundType("and an average complexion.", 80, 53, 0, 50),
	new BackgroundType("and a fair complexion.", 90, 53, 0, 50),
	new BackgroundType("and a very fair complexion.", 100, 53, 0, 50),
	new BackgroundType("You have light grey eyes, ", 85, 54, 55, 50),
	new BackgroundType("You have light blue eyes, ", 95, 54, 55, 50),
	new BackgroundType("You have light green eyes, ", 100, 54, 55, 50),
	new BackgroundType("straight ", 75, 55, 56, 50),
	new BackgroundType("wavy ", 100, 55, 56, 50),
	new BackgroundType("black hair, and a fair complexion.", 75, 56, 0, 50),
	new BackgroundType("brown hair, and a fair complexion.", 85, 56, 0, 50),
	new BackgroundType("blond hair, and a fair complexion.", 95, 56, 0, 50),
	new BackgroundType("silver hair, and a fair complexion.", 100, 56, 0, 50),
	new BackgroundType("You have dark brown eyes, ", 99, 57, 58, 50),
	new BackgroundType("You have glowing red eyes, ", 100, 57, 58, 60),
	new BackgroundType("straight ", 90, 58, 59, 50),
	new BackgroundType("wavy ", 100, 58, 59, 50),
	new BackgroundType("black hair, ", 75, 59, 60, 50),
	new BackgroundType("brown hair, ", 100, 59, 60, 50),
	new BackgroundType("a one foot beard, ", 25, 60, 61, 50),
	new BackgroundType("a two foot beard, ", 60, 60, 61, 51),
	new BackgroundType("a three foot beard, ", 90, 60, 61, 53),
	new BackgroundType("a four foot beard, ", 100, 60, 61, 55),
	new BackgroundType("and a dark complexion.", 100, 61, 0, 50),
	new BackgroundType("You have slime green eyes, ", 60, 62, 63, 50),
	new BackgroundType("You have puke yellow eyes, ", 85, 62, 63, 50),
	new BackgroundType("You have blue-bloodshot eyes, ", 99, 62, 63, 50),
	new BackgroundType("You have glowing red eyes, ", 100, 62, 63, 55),
	new BackgroundType("dirty ", 33, 63, 64, 50),
	new BackgroundType("mangy ", 66, 63, 64, 50),
	new BackgroundType("oily ", 100, 63, 64, 50),
	new BackgroundType("sea-weed green hair, ", 33, 64, 65, 50),
	new BackgroundType("bright red hair, ", 66, 64, 65, 50),
	new BackgroundType("dark purple hair, ", 100, 64, 65, 50),
	new BackgroundType("and green ", 25, 65, 66, 50),
	new BackgroundType("and blue ", 50, 65, 66, 50),
	new BackgroundType("and white ", 75, 65, 66, 50),
	new BackgroundType("and black ", 100, 65, 66, 50),
	new BackgroundType("ulcerous skin.", 33, 66, 0, 50),
	new BackgroundType("scabby skin.", 66, 66, 0, 50),
	new BackgroundType("leprous skin.", 100, 66, 0, 50)
	};
	
	/* Classes.							*/
	//Class_type[MAX_CLASS]
	public ClassType[] Class = {
	/*	  HP Dis Src Stl Fos bth btb sve S  I  W  D Co Ch  Spell Exp  spl */
	new ClassType("Warrior",9, 25, 14, 1, 38, 70, 55, 18, 5,-2,-2, 2, 2,-1, Constants.NONE,    0, 0),
	new ClassType("Mage",   0, 30, 16, 2, 20, 34, 20, 36,-5, 3, 0, 1,-2, 1, Constants.MAGE,   30, 1),
	new ClassType("Priest", 2, 25, 16, 2, 32, 48, 35, 30,-3,-3, 3,-1, 0, 2, Constants.PRIEST, 20, 1),
	new ClassType("Rogue",  6, 45, 32, 5, 16, 60, 66, 30, 2, 1,-2, 3, 1,-1, Constants.MAGE,    0, 5),
	new ClassType("Ranger", 4, 30, 24, 3, 24, 56, 72, 30, 2, 2, 0, 1, 1, 1, Constants.MAGE,   40, 3),
	new ClassType("Paladin",6, 20, 12, 1, 38, 68, 40, 24, 3,-3, 1, 0, 2, 2, Constants.PRIEST, 35, 1)
	};
	
	/* making it 16 bits wastes a little space, but saves much signed/unsigned
	 * headaches in its use */
	/* CLA_MISC_HIT is identical to CLA_SAVE, which takes advantage of
	 * the fact that the save values are independent of the class */
	//short[MAX_CLASS][MAX_LEV_ADJ]
	public short[][] class_level_adj= {
	/*	       bth    bthb   device  disarm   save/misc hit  */
	/* Warrior */ {	4,	4,	2,	2,	3 },
	/* Mage    */ { 2,	2,	4,	3,	3 },
	/* Priest  */ { 2,	2,	4,	3,	3 },
	/* Rogue   */ { 3,	4,	3,	4,	3 },
	/* Ranger  */ { 3,	4,	3,	3,	3 },
	/* Paladin */ { 3,	3,	3,	2,	3 }
	};
	
	public long spell_learned = 0;				/* bit mask of spells learned */
	public long spell_worked = 0;				/* bit mask of spells tried and worked */
	public long spell_forgotten = 0;			/* bit mask of spells learned but forgotten */
	public int[] spell_order = new int[32];		/* order spells learned/remembered/forgotten */
	
	/* Warriors don't have spells, so there is no entry for them.  Note that
	 * this means you must always subtract one from the py.misc.pclass before
	 * indexing into magic_spell[]. */
	//Spell_type[MAX_CLASS - 1][31]
	public SpellType[][] magic_spell = {
	{	/* Mage	   */
		new SpellType(	1,  1,  22, 1  ),
		new SpellType(	1,  1,  23, 1  ),
		new SpellType(	1,  2,  24, 1  ),
		new SpellType(	1,  2,  26, 1  ),
		new SpellType(	3,  3,  25, 2  ),
		new SpellType(	3,  3,  25, 1  ),
		new SpellType(	3,  3,  27, 2  ),
		new SpellType(	3,  4,  30, 1  ),
		new SpellType(	5,  4,  30, 6  ),
		new SpellType(	5,  5,  30, 8  ),
		new SpellType(	5,  5,  30, 5  ),
		new SpellType(	5,  5,  35, 6  ),
		new SpellType(	7,  6,  35, 9  ),
		new SpellType(	7,  6,  50, 10 ),
		new SpellType(	7,  6,  40, 12 ),
		new SpellType(	9,  7,  44, 19 ),
		new SpellType(	9,  7,  45, 19 ),
		new SpellType(	9,  7,  75, 22 ),
		new SpellType(	9,  7,  45, 19 ),
		new SpellType( 11, 7,  45, 25 ),
		new SpellType( 11, 7,  99, 19 ),
		new SpellType( 13, 7,  50, 22 ),
		new SpellType( 15, 9,  50, 25 ),
		new SpellType( 17, 9,  50, 31 ),
		new SpellType( 19, 12, 55, 38 ),
		new SpellType( 21, 12, 90, 44 ),
		new SpellType( 23, 12, 60, 50 ),
		new SpellType( 25, 12, 65, 63 ),
		new SpellType( 29, 18, 65, 88 ),
		new SpellType( 33, 21, 80, 125),
		new SpellType( 37, 25, 95, 200)
	},
	{	/* Priest	   */
		new SpellType( 1,  1,  10, 1  ),
		new SpellType( 1,  2,  15, 1  ),
		new SpellType( 1,  2,  20, 1  ),
		new SpellType( 1,  2,  25, 1  ),
		new SpellType( 3,  2,  25, 1  ),
		new SpellType( 3,  3,  27, 2  ),
		new SpellType( 3,  3,  27, 2  ),
		new SpellType( 3,  3,  28, 3  ),
		new SpellType( 5,  4,  29, 4  ),
		new SpellType( 5,  4,  30, 5  ),
		new SpellType( 5,  4,  32, 5  ),
		new SpellType( 5,  5,  34, 5  ),
		new SpellType( 7,  5,  36, 6  ),
		new SpellType( 7,  5,  38, 7  ),
		new SpellType( 7,  6,  38, 9  ),
		new SpellType( 7,  7,  38, 9  ),
		new SpellType( 9,  6,  38, 10 ),
		new SpellType( 9,  7,  38, 10 ),
		new SpellType( 9,  7,  40, 10 ),
		new SpellType( 11, 8,  42, 10 ),
		new SpellType( 11, 8,  42, 12 ),
		new SpellType( 11, 9,  55, 15 ),
		new SpellType( 13, 10, 45, 15 ),
		new SpellType( 13, 11, 45, 16 ),
		new SpellType( 15, 12, 50, 20 ),
		new SpellType( 15, 14, 50, 22 ),
		new SpellType( 17, 14, 55, 32 ),
		new SpellType( 21, 16, 60, 38 ),
		new SpellType( 25, 20, 70, 75 ),
		new SpellType( 33, 24, 90, 125),
		new SpellType( 39, 32, 99, 200)
	},
	{	/* Rogue	   */
		new SpellType( 99, 99, 0,  0  ),
		new SpellType( 5,  1,  50, 1  ),
		new SpellType( 7,  2,  55, 1  ),
		new SpellType( 9,  3,  60, 2  ),
		new SpellType( 11, 4,  65, 2  ),
		new SpellType( 13, 5,  70, 3  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 15, 6,  75, 3  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 17, 7,  80, 4  ),
		new SpellType( 19, 8,  85, 5  ),
		new SpellType( 21, 9,  90, 6  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 23, 10, 95, 7  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 25, 12, 95, 9  ),
		new SpellType( 27, 15, 99, 11 ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 29, 18, 99, 19 ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  ),
		new SpellType( 99, 99,	0,  0  )
	},
	{	/* Ranger	    */
		new SpellType( 3,  1,  30, 1  ),
		new SpellType( 3,  2,  35, 2  ),
		new SpellType( 3,  2,  35, 2  ),
		new SpellType( 5,  3,  35, 2  ),
		new SpellType( 5,  3,  40, 2  ),
		new SpellType( 5,  4,  45, 3  ),
		new SpellType( 7,  5,  40, 6  ),
		new SpellType( 7,  6,  40, 5  ),
		new SpellType( 9,  7,  40, 7  ),
		new SpellType( 9,  8,  45, 8  ),
		new SpellType( 11, 8,  40, 10 ),
		new SpellType( 11, 9,  45, 10 ),
		new SpellType( 13, 10, 45, 12 ),
		new SpellType( 13, 11, 55, 13 ),
		new SpellType( 15, 12, 50, 15 ),
		new SpellType( 15, 13, 50, 15 ),
		new SpellType( 17, 17, 55, 15 ),
		new SpellType( 17, 17, 90, 17 ),
		new SpellType( 21, 17, 55, 17 ),
		new SpellType( 21, 19, 60, 18 ),
		new SpellType( 23, 25, 95, 20 ),
		new SpellType( 23, 20, 60, 20 ),
		new SpellType( 25, 20, 60, 20 ),
		new SpellType( 25, 21, 65, 20 ),
		new SpellType( 27, 21, 65, 22 ),
		new SpellType( 29, 23, 95, 23 ),
		new SpellType( 31, 25, 70, 25 ),
		new SpellType( 33, 25, 75, 38 ),
		new SpellType( 35, 25, 80, 50 ),
		new SpellType( 37, 30, 95, 100),
		new SpellType( 99, 99,	0,  0  )
	},
	{	/* Paladin	   */
		new SpellType( 1,  1,  30, 1  ),
		new SpellType( 2,  2,  35, 2  ),
		new SpellType( 3,  3,  35, 3  ),
		new SpellType( 5,  3,  35, 5  ),
		new SpellType( 5,  4,  35, 5  ),
		new SpellType( 7,  5,  40, 6  ),
		new SpellType( 7,  5,  40, 6  ),
		new SpellType( 9,  7,  40, 7  ),
		new SpellType( 9,  7,  40, 8  ),
		new SpellType( 9,  8,  40, 8  ),
		new SpellType( 11, 9,  40, 10 ),
		new SpellType( 11, 10, 45, 10 ),
		new SpellType( 11, 10, 45, 10 ),
		new SpellType( 13, 10, 45, 12 ),
		new SpellType( 13, 11, 45, 13 ),
		new SpellType( 15, 13, 45, 15 ),
		new SpellType( 15, 15, 50, 15 ),
		new SpellType( 17, 15, 50, 17 ),
		new SpellType( 17, 15, 50, 18 ),
		new SpellType( 19, 15, 50, 19 ),
		new SpellType( 19, 15, 50, 19 ),
		new SpellType( 21, 17, 50, 20 ),
		new SpellType( 23, 17, 50, 20 ),
		new SpellType( 25, 20, 50, 20 ),
		new SpellType( 27, 21, 50, 22 ),
		new SpellType( 29, 22, 50, 24 ),
		new SpellType( 31, 24, 60, 25 ),
		new SpellType( 33, 28, 60, 31 ),
		new SpellType( 35, 32, 70, 38 ),
		new SpellType( 37, 36, 90, 50 ),
		new SpellType( 39, 38, 95, 100)
	}
	};
	
	//String[62]
	public String[] spell_names = {
		/* Mage Spells */
		"Magic Missile", "Detect Monsters", "Phase Door", "Light Area",
		"Cure Light Wounds", "Find Hidden Traps/Doors", "Stinking Cloud",
		"Confusion", "Lightning Bolt", "Trap/Door Destruction", "Sleep I",
		"Cure Poison", "Teleport Self", "Remove Curse", "Frost Bolt",
		"Turn Stone to Mud", "Create Food", "Recharge Item I", "Sleep II",
		"Polymorph Other", "Identify", "Sleep III", "Fire Bolt", "Slow Monster",
		"Frost Ball", "Recharge Item II", "Teleport Other", "Haste Self",
		"Fire Ball", "Word of Destruction", "Genocide",
		/* Priest Spells, start at index 31 */
		"Detect Evil", "Cure Light Wounds", "Bless", "Remove Fear", "Call Light",
		"Find Traps", "Detect Doors/Stairs", "Slow Poison", "Blind Creature",
		"Portal", "Cure Medium Wounds", "Chant", "Sanctuary", "Create Food",
		"Remove Curse", "Resist Heat and Cold", "Neutralize Poison",
		"Orb of Draining", "Cure Serious Wounds", "Sense Invisible",
		"Protection from Evil", "Earthquake", "Sense Surroundings",
		"Cure Critical Wounds", "Turn Undead", "Prayer", "Dispel Undead",
		"Heal", "Dispel Evil", "Glyph of Warding", "Holy Word"
	};
	
	/* Each type of character starts out with a few provisions.	*/
	/* Note that the entries refer to elements of the object_list[] array*/
	/* 344 = Food Ration, 365 = Wooden Torch, 123 = Cloak, 318 = Beginners-Majik,
	 * 103 = Soft Leather Armor, 30 = Stiletto, 322 = Beginners Handbook */
	//short[MAX_CLASS][5]
	public short[][] player_init = {
		{ 344, 365, 123,  30, 103},	/* Warrior	 */
		{ 344, 365, 123,  30, 318},	/* Mage		 */
		{ 344, 365, 123,  30, 322},	/* Priest	 */
		{ 344, 365, 123,  30, 318},	/* Rogue	 */
		{ 344, 365, 123,  30, 318},	/* Ranger	 */
		{ 344, 365, 123,  30, 322}	/* Paladin	 */
	};
}
