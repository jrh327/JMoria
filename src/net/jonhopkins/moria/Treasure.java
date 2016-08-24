/*
 * Treasure.java: dungeon object definitions
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

import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.TreasureType;

public class Treasure {
	/* Following are treasure arrays	and variables			*/
	
	/* Object description:	Objects are defined here.  Each object has
	  the following attributes:
		Descriptor : Name of item and formats.
			& is replaced with 'a', 'an', or a number.
			~ is replaced with null or 's'.
		Character  : Character that represents the item.
		Type value : Value representing the type of object.
		Sub value  : separate value for each item of a type.
			0 - 63: object can not stack
			64 - 127: dungeon object, can stack with other D object
			128 - 191: unused, previously for store items
			192: stack with other iff have same p1 value, always
				treated as individual objects
			193 - 255: object can stack with others iff they have
				the same p1 value, usually considered one group
			Objects which have two type values, e.g. potions and
			scrolls, need to have distinct subvals for
			each item regardless of its tval
		Damage	   : amount of damage item can cause.
		Weight	   : relative weight of an item.
		Number	   : number of items appearing in group.
		To hit	   : magical plusses to hit.
		To damage  : magical plusses to damage.
		AC	   : objects relative armor class.
			1 is worse than 5 is worse than 10 etc.
		To AC	   : Magical bonuses to AC.
		P1	   : Catch all for magical abilities such as
			     plusses to strength, minuses to searching.
		Flags	   : Abilities of object.  Each ability is a
			     bit.  Bits 1-31 are used. (Signed integer)
		Level	   : Minimum level on which item can be found.
		Cost	   : Relative cost of item.
	
		Special Abilities can be added to item by magic_init(),
		found in misc.c.
	
		Scrolls, Potions, and Food:
		Flags is used to define a function which reading/quaffing
		will cause.  Most scrolls and potions have only one bit
		set.  Potions will generally have some food value, found
		in p1.
	
		Wands and Staffs:
		Flags defines a function, p1 contains number of charges
		for item.  p1 is set in magic_init() in misc.c.
	
		Chests:
		Traps are added randomly by magic_init() in misc.c.	*/
	
	private Treasure() { }
	
	/* Object list (All objects must be defined here)		 */
	
	//treasure_type object_list[MAX_OBJECTS]
	public static TreasureType[] object_list = {
	/* Dungeon items from 0 to MAX_DUNGEON_OBJ */
	new TreasureType("Poison"			,0x00000001L,	Constants.TV_FOOD, ',',	/*  0*/
	  500,	   0,	64,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Blindness"			,0x00000002L,	Constants.TV_FOOD, ',',	/*  1*/
	  500,	   0,	65,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  9),
	new TreasureType("Paranoia"			,0x00000004L,	Constants.TV_FOOD, ',',	/*  2*/
	  500,	   0,	66,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  9),
	new TreasureType("Confusion"			,0x00000008L,	Constants.TV_FOOD, ',',	/*  3*/
	  500,	   0,	67,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Hallucination"		,0x00000010L,	Constants.TV_FOOD, ',',	/*  4*/
	  500,	   0,	68,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 13),
	new TreasureType("Cure Poison"			,0x00000020L,	Constants.TV_FOOD, ',',	/*  5*/
	  500,	  60,	69,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  8),
	new TreasureType("Cure Blindness"		,0x00000040L,	Constants.TV_FOOD, ',',	/*  6*/
	  500,	  50,	70,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	new TreasureType("Cure Paranoia"		,0x00000080L,	Constants.TV_FOOD, ',',	/*  7*/
	  500,	  25,	71,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Cure Confusion"		,0x00000100L,	Constants.TV_FOOD, ',',	/*  8*/
	  500,	  50,	72,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  6),
	new TreasureType("Weakness"			,0x04000200L,	Constants.TV_FOOD, ',',	/*  9*/
	  500,	   0,	73,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Unhealth"			,0x04000400L,	Constants.TV_FOOD, ',',	/* 10*/
	  500,	  50,	74,   1,   1,	0,   0,	 0,   0, new int[] {10,10}, 15),
	new TreasureType("Restore Constitution"		,0x00010000L,	Constants.TV_FOOD, ',',	/* 11*/
	  500,	 350,	75,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 20),
	new TreasureType("First-Aid"			,0x00200000L,	Constants.TV_FOOD, ',',	/* 12*/
	  500,	   5,	76,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  6),
	new TreasureType("Minor Cures"			,0x00400000L,	Constants.TV_FOOD, ',',	/* 13*/
	  500,	  20,	77,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Light Cures"			,0x00800000L,	Constants.TV_FOOD, ',',	/* 14*/
	  500,	  30,	78,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	new TreasureType("Restoring"			,0x001F8040L,	Constants.TV_FOOD, ',',	/* 15*/
	  500,	1000,	79,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 30),
	new TreasureType("Poison"			,0x00000001L,	Constants.TV_FOOD, ',',	/* 16*/
	 1200,	   0,	80,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 15),
	new TreasureType("Hallucinations"		,0x00000010L,	Constants.TV_FOOD, ',',	/* 17*/
	 1200,	   0,	81,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 18),
	new TreasureType("Cure Poison"			,0x00000020L,	Constants.TV_FOOD, ',',	/* 18*/
	 1200,	  75,	82,   1,   1,	0,   0,	 0,   0, new int[] {0,0}	, 19),
	new TreasureType("Unhealth"			,0x00000400L,	Constants.TV_FOOD, ',',	/* 19*/
	 1200,	  25,	83,   1,   1,	0,   0,	 0,   0, new int[] {6,8}	, 28),
	new TreasureType("Cure Serious Wounds"		,0x02000000L,	Constants.TV_FOOD, ',',	/* 20*/
	 1200,	  75,	84,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 16),
	new TreasureType("& Ration~ of Food"		,0x00000000L,	Constants.TV_FOOD, ',',	/* 21*/
	 5000,	   3,	90,   1,  10,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("& Ration~ of Food"		,0x00000000L,	Constants.TV_FOOD, ',',	/* 22*/
	 5000,	   3,	90,   1,  10,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("& Ration~ of Food"		,0x00000000L,	Constants.TV_FOOD, ',',	/* 23*/
	 5000,	   3,	90,   1,  10,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	new TreasureType("& Slime Mold~"		,0x00000000L,	Constants.TV_FOOD, ',',	/* 24*/
	 3000,	   2,	91,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("& Piece~ of Elvish Waybread"	,0x02000020L,	Constants.TV_FOOD, ',',	/* 25*/
	 7500,	  10,	92,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	,  6),
	new TreasureType("& Piece~ of Elvish Waybread"	,0x02000020L,	Constants.TV_FOOD, ',',	/* 26*/
	 7500,	  10,	92,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("& Piece~ of Elvish Waybread"	,0x02000020L,	Constants.TV_FOOD, ',',	/* 27*/
	 7500,	  10,	92,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 20),
	new TreasureType("& Dagger (Main Gauche)"	,0x00000000L,	Constants.TV_SWORD, '|',	/* 28*/
	    0,	  25,	1,   1,  30,	0,   0,	 0,   0, new int[] {1,5}	,  2),
	new TreasureType("& Dagger (Misericorde)"	,0x00000000L,	Constants.TV_SWORD, '|',	/* 29*/
	    0,	  10,	2,   1,  15,	0,   0,	 0,   0, new int[] {1,4}	,  0),
	new TreasureType("& Dagger (Stiletto)"		,0x00000000L,	Constants.TV_SWORD, '|',	/* 30*/
	    0,	  10,	3,   1,  12,	0,   0,	 0,   0, new int[] {1,4}	,  0),
	new TreasureType("& Dagger (Bodkin)"		,0x00000000L,	Constants.TV_SWORD, '|',	/* 31*/
	    0,	  10,	4,   1,  20,	0,   0,	 0,   0, new int[] {1,4}	,  1),
	new TreasureType("& Broken dagger"		,0x00000000L,	Constants.TV_SWORD, '|',	/* 32*/
	    0,	   0,	5,   1,  15,  -2,  -2,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& Backsword"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 33*/
	    0,	  60,	6,   1,  95,	0,   0,	 0,   0, new int[] {1,9}	,  7),
	new TreasureType("& Bastard Sword"		,0x00000000L,	Constants.TV_SWORD, '|',	/* 34*/
	    0,	 350,	7,   1, 140,	0,   0,	 0,   0, new int[] {3,4}	, 14),
	new TreasureType("& Thrusting Sword (Bilbo)"	,0x00000000L,	Constants.TV_SWORD, '|',	/* 35*/
	    0,	  60,	8,   1,  80,	0,   0,	 0,   0, new int[] {1,6}	,  4),
	new TreasureType("& Thrusting Sword (Baselard)"	,0x00000000L,	Constants.TV_SWORD, '|',	/* 36*/
	    0,	  80,	9,   1, 100,	0,   0,	 0,   0, new int[] {1,7}	,  5),
	new TreasureType("& Broadsword"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 37*/
	    0,	 255,	10,   1, 150,	0,   0,	 0,   0, new int[] {2,5}	,  9),
	new TreasureType("& Two-Handed Sword (Claymore)",0x00000000L,	Constants.TV_SWORD, '|',	/* 38*/
	    0,	 775,	11,   1, 200,	0,   0,	 0,   0, new int[] {3,6}	, 30),
	new TreasureType("& Cutlass"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 39*/
	    0,	  85,	12,   1, 110,	0,   0,	 0,   0, new int[] {1,7}	,  7),
	new TreasureType("& Two-Handed Sword (Espadon)"	,0x00000000L,	Constants.TV_SWORD, '|',	/* 40*/
	    0,	 655,	13,   1, 180,	0,   0,	 0,   0, new int[] {3,6}	, 35),
	new TreasureType("& Executioner's Sword"	,0x00000000L,	Constants.TV_SWORD, '|',	/* 41*/
	    0,	 850,	14,   1, 260,	0,   0,	 0,   0, new int[] {4,5}	, 40),
	new TreasureType("& Two-Handed Sword (Flamberge)",0x00000000L,	Constants.TV_SWORD, '|',	/* 42*/
	    0,	1000,	15,   1, 240,	0,   0,	 0,   0, new int[] {4,5}	, 45),
	new TreasureType("& Foil"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 43*/
	    0,	  35,	16,   1,  30,	0,   0,	 0,   0, new int[] {1,5}	,  2),
	new TreasureType("& Katana"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 44*/
	    0,	 400,	17,   1, 120,	0,   0,	 0,   0, new int[] {3,4}	, 18),
	new TreasureType("& Longsword"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 45*/
	    0,	 300,	18,   1, 130,	0,   0,	 0,   0, new int[] {1,10} , 12),
	new TreasureType("& Two-Handed Sword (No-Dachi)",0x00000000L,	Constants.TV_SWORD, '|',	/* 46*/
	    0,	 675,	19,   1, 200,	0,   0,	 0,   0, new int[] {4,4}	, 45),
	new TreasureType("& Rapier"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 47*/
	    0,	  42,	20,   1,  40,	0,   0,	 0,   0, new int[] {1,6}	,  4),
	new TreasureType("& Sabre"			,0x00000000L,	Constants.TV_SWORD, '|',	/* 48*/
	    0,	  50,	21,   1,  50,	0,   0,	 0,   0, new int[] {1,7}	,  5),
	new TreasureType("& Small Sword"		,0x00000000L,	Constants.TV_SWORD, '|',	/* 49*/
	    0,	  48,	22,   1,  75,	0,   0,	 0,   0, new int[] {1,6}	,  5),
	new TreasureType("& Two-Handed Sword (Zweihander)",0x00000000L,	Constants.TV_SWORD, '|',	/* 50*/
	    0,	1500,	23,   1, 280,	0,   0,	 0,   0, new int[] {4,6}	, 50),
	new TreasureType("& Broken sword"		,0x00000000L,	Constants.TV_SWORD, '|',	/* 51*/
	    0,	   0,	24,   1,  75,  -2,  -2,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& Ball and Chain"		,0x00000000L,	Constants.TV_HAFTED, '\\',/* 52*/
	    0,	 200,	1,   1, 150,	0,   0,	 0,   0, new int[] {2,4}	, 20),
	new TreasureType("& Cat-O-Nine Tails"		,0x00000000L,	Constants.TV_HAFTED, '\\',/* 53*/
	    0,	  14,	2,   1,  40,	0,   0,	 0,   0, new int[] {1,4}	,  3),
	new TreasureType("& Wooden Club"		,0x00000000L,	Constants.TV_HAFTED, '\\',/* 54*/
	    0,	  10,	3,   1, 100,	0,   0,	 0,   0, new int[] {1,3}	,  0),
	new TreasureType("& Flail"			,0x00000000L,	Constants.TV_HAFTED, '\\',/* 55*/
	    0,	 353,	4,   1, 150,	0,   0,	 0,   0, new int[] {2,6}	, 12),
	new TreasureType("& Two-Handed Great Flail"	,0x00000000L,	Constants.TV_HAFTED, '\\',/* 56*/
	    0,	 590,	5,   1, 280,	0,   0,	 0,   0, new int[] {3,6}	, 45),
	new TreasureType("& Morningstar"		,0x00000000L,	Constants.TV_HAFTED, '\\',/* 57*/
	    0,	 396,	6,   1, 150,	0,   0,	 0,   0, new int[] {2,6}	, 10),
	new TreasureType("& Mace"			,0x00000000L,	Constants.TV_HAFTED, '\\',/* 58*/
	    0,	 130,	7,   1, 120,	0,   0,	 0,   0, new int[] {2,4}	,  6),
	new TreasureType("& War Hammer"			,0x00000000L,	Constants.TV_HAFTED, '\\',/* 59*/
	    0,	 225,	8,   1, 120,	0,   0,	 0,   0, new int[] {3,3}	,  5),
	new TreasureType("& Mace (Lead-filled)"		,0x00000000L,	Constants.TV_HAFTED, '\\',/* 60*/
	    0,	 502,	9,   1, 180,	0,   0,	 0,   0, new int[] {3,4}	, 15),
	new TreasureType("& Awl-Pike"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 61*/
	    0,	 340,	1,   1, 160,	0,   0,	 0,   0, new int[] {1,8}	,  8),
	new TreasureType("& Beaked Axe"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 62*/
	    0,	 408,	2,   1, 180,	0,   0,	 0,   0, new int[] {2,6}	, 15),
	new TreasureType("& Fauchard"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 63*/
	    0,	 376,	3,   1, 170,	0,   0,	 0,   0, new int[] {1,10} , 17),
	new TreasureType("& Glaive"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 64*/
	    0,	 363,	4,   1, 190,	0,   0,	 0,   0, new int[] {2,6}	, 20),
	new TreasureType("& Halberd"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 65*/
	    0,	 430,	5,   1, 190,	0,   0,	 0,   0, new int[] {3,4}	, 22),
	new TreasureType("& Lucerne Hammer"		,0x00000000L,	Constants.TV_POLEARM, '/',/* 66*/
	    0,	 376,	6,   1, 120,	0,   0,	 0,   0, new int[] {2,5}	, 11),
	new TreasureType("& Pike"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 67*/
	    0,	 358,	7,   1, 160,	0,   0,	 0,   0, new int[] {2,5}	, 15),
	new TreasureType("& Spear"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 68*/
	    0,	  36,	8,   1,  50,	0,   0,	 0,   0, new int[] {1,6}	,  5),
	new TreasureType("& Lance"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 69*/
	    0,	 230,	9,   1, 300,	0,   0,	 0,   0, new int[] {2,8}	, 10),
	new TreasureType("& Javelin"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 70*/
	    0,	  18,	10,   1,  30,	0,   0,	 0,   0, new int[] {1,4}	,  4),
	new TreasureType("& Battle Axe (Balestarius)"	,0x00000000L,	Constants.TV_POLEARM, '/',/* 71*/
	    0,	 500,	11,   1, 180,	0,   0,	 0,   0, new int[] {2,8}	, 30),
	new TreasureType("& Battle Axe (European)"	,0x00000000L,	Constants.TV_POLEARM, '/',/* 72*/
	    0,	 334,	12,   1, 170,	0,   0,	 0,   0, new int[] {3,4}	, 13),
	new TreasureType("& Broad Axe"			,0x00000000L,	Constants.TV_POLEARM, '/',/* 73*/
	    0,	 304,	13,   1, 160,	0,   0,	 0,   0, new int[] {2,6}	, 17),
	new TreasureType("& Short Bow"			,0x00000000L,	Constants.TV_BOW, '}',	/* 74*/
	    2,	  50,	1,   1,  30,	0,   0,	 0,   0, new int[] {0,0}	,  3),
	new TreasureType("& Long Bow"			,0x00000000L,	Constants.TV_BOW, '}',	/* 75*/
	    3,	 120,	2,   1,  40,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	new TreasureType("& Composite Bow"		,0x00000000L,	Constants.TV_BOW, '}',	/* 76*/
	    4,	 240,	3,   1,  40,	0,   0,	 0,   0, new int[] {0,0}	, 40),
	new TreasureType("& Light Crossbow"		,0x00000000L,	Constants.TV_BOW, '}',	/* 77*/
	    5,	 140,	10,   1, 110,	0,   0,	 0,   0, new int[] {0,0}	, 15),
	new TreasureType("& Heavy Crossbow"		,0x00000000L,	Constants.TV_BOW, '}',	/* 78*/
	    6,	 300,	11,   1, 200,	0,   0,	 0,   0, new int[] {1,1}	, 30),
	new TreasureType("& Sling"			,0x00000000L,	Constants.TV_BOW, '}',	/* 79*/
	    1,	   5,	20,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("& Arrow~"			,0x00000000L,	Constants.TV_ARROW, '{',	/* 80*/
	    0,	   1, 193,   1,   2,	0,   0,	 0,   0, new int[] {1,4}	,  2),
	new TreasureType("& Bolt~"			,0x00000000L,	Constants.TV_BOLT, '{',	/* 81*/
	    0,	   2, 193,   1,   3,	0,   0,	 0,   0, new int[] {1,5}	,  2),
	new TreasureType("& Rounded Pebble~"		,0x00000000L,	Constants.TV_SLING_AMMO, '{',/* 82*/
	    0,	   1, 193,   1,   4,	0,   0,	 0,   0, new int[] {1,2}	,  0),
	new TreasureType("& Iron Shot~"			,0x00000000L,	Constants.TV_SLING_AMMO, '{',/* 83*/
	    0,	   2, 194,   1,   5,	0,   0,	 0,   0, new int[] {1,3}	,  3),
	new TreasureType("& Iron Spike~"		,0x00000000L,	Constants.TV_SPIKE, '~',	/* 84*/
	    0,	   1, 193,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Brass Lantern~"		,0x00000000L,	Constants.TV_LIGHT, '~',	/* 85*/
	 7500,	  35,	1,   1,  50,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Wooden Torch~"		,0x00000000L,	Constants.TV_LIGHT, '~',	/* 86*/
	 4000,	   2, 193,   1,  30,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Orcish Pick"		,0x20000000L,	Constants.TV_DIGGING, '\\',/* 87*/
	    2,	 500,	2,   1, 180,	0,   0,	 0,   0, new int[] {1,3}	, 20),
	new TreasureType("& Dwarven Pick"	       ,0x20000000L,	Constants.TV_DIGGING, '\\',/* 88*/
	    3,	1200,	3,   1, 200,	0,   0,	 0,   0, new int[] {1,4}	, 50),
	new TreasureType("& Gnomish Shovel"		,0x20000000L,	Constants.TV_DIGGING, '\\',/* 89*/
	    1,	 100,	5,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 20),
	new TreasureType("& Dwarven Shovel"		,0x20000000L,	Constants.TV_DIGGING, '\\',/* 90*/
	    2,	 250,	6,   1, 120,	0,   0,	 0,   0, new int[] {1,3}	, 40),
	new TreasureType("& Pair of Soft Leather Shoes"	,0x00000000L,	Constants.TV_BOOTS, ']',	/* 91*/
	    0,	   4,	1,   1,   5,	0,   0,	 1,   0, new int[] {0,0}	,  1),
	new TreasureType("& Pair of Soft Leather Boots"	,0x00000000L,	Constants.TV_BOOTS, ']',	/* 92*/
	    0,	   7,	2,   1,  20,	0,   0,	 2,   0, new int[] {1,1}	,  4),
	new TreasureType("& Pair of Hard Leather Boots"	,0x00000000L,	Constants.TV_BOOTS, ']',	/* 93*/
	    0,	  12,	3,   1,  40,	0,   0,	 3,   0, new int[] {1,1}	,  6),
	new TreasureType("& Soft Leather Cap"		,0x00000000L,	Constants.TV_HELM, ']',	/* 94*/
	    0,	   4,	1,   1,  10,	0,   0,	 1,   0, new int[] {0,0}	,  2),
	new TreasureType("& Hard Leather Cap"		,0x00000000L,	Constants.TV_HELM, ']',	/* 95*/
	    0,	  12,	2,   1,  15,	0,   0,	 2,   0, new int[] {0,0}	,  4),
	new TreasureType("& Metal Cap"			,0x00000000L,	Constants.TV_HELM, ']',	/* 96*/
	    0,	  30,	3,   1,  20,	0,   0,	 3,   0, new int[] {1,1}	,  7),
	new TreasureType("& Iron Helm"			,0x00000000L,	Constants.TV_HELM, ']',	/* 97*/
	    0,	  75,	4,   1,  75,	0,   0,	 5,   0, new int[] {1,3}	, 20),
	new TreasureType("& Steel Helm"			,0x00000000L,	Constants.TV_HELM, ']',	/* 98*/
	    0,	 200,	5,   1,  60,	0,   0,	 6,   0, new int[] {1,3}	, 40),
	new TreasureType("& Silver Crown"		,0x00000000L,	Constants.TV_HELM, ']',	/* 99*/
	    0,	 500,	6,   1,  20,	0,   0,	 0,   0, new int[] {1,1}	, 44),
	new TreasureType("& Golden Crown"		,0x00000000L,	Constants.TV_HELM, ']',	/*100*/
	    0,	1000,	7,   1,  30,	0,   0,	 0,   0, new int[] {1,1}	, 47),
	new TreasureType("& Jewel-Encrusted Crown"	,0x00000000L,	Constants.TV_HELM, ']',	/*101*/
	    0,	2000,	8,   1,  40,	0,   0,	 0,   0, new int[] {1,1}	, 50),
	new TreasureType("& Robe"			,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*102*/
	    0,	   4,	1,   1,  20,	0,   0,	 2,   0, new int[] {0,0}	,  1),
	new TreasureType("Soft Leather Armor"		,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*103*/
	    0,	  18,	2,   1,  80,	0,   0,	 4,   0, new int[] {0,0}	,  2),
	new TreasureType("Soft Studded Leather"		,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*104*/
	    0,	  35,	3,   1,  90,	0,   0,	 5,   0, new int[] {1,1}	,  3),
	new TreasureType("Hard Leather Armor"		,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*105*/
	    0,	  55,	4,   1, 100,  -1,   0,	 6,   0, new int[] {1,1}	,  5),
	new TreasureType("Hard Studded Leather"		,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*106*/
	    0,	 100,	5,   1, 110,  -1,   0,	 7,   0, new int[] {1,2}	,  7),
	new TreasureType("Woven Cord Armor"		,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*107*/
	    0,	  45,	6,   1, 150,  -1,   0,	 6,   0, new int[] {0,0}	,  7),
	new TreasureType("Soft Leather Ring Mail"	,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*108*/
	    0,	 160,	7,   1, 130,  -1,   0,	 6,   0, new int[] {1,2}	, 10),
	new TreasureType("Hard Leather Ring Mail"	,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*109*/
	    0,	 230,	8,   1, 150,  -2,   0,	 8,   0, new int[] {1,3}	, 12),
	new TreasureType("Leather Scale Mail"		,0x00000000L,	Constants.TV_SOFT_ARMOR, '(',/*110*/
	    0,	 330,	9,   1, 140,  -1,   0,	11,   0, new int[] {1,1}	, 14),
	new TreasureType("Metal Scale Mail"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*111*/
	    0,	 430,	1,   1, 250,  -2,   0,	13,   0, new int[] {1,4}	, 24),
	new TreasureType("Chain Mail"			,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*112*/
	    0,	 530,	2,   1, 220,  -2,   0,	14,   0, new int[] {1,4}	, 26),
	new TreasureType("Rusty Chain Mail"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*113*/
	    0,	   0,	3,   1, 200,  -5,   0,	14,  -8, new int[] {1,4}	, 26),
	new TreasureType("Double Chain Mail"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*114*/
	    0,	 630,	4,   1, 260,  -2,   0,	15,   0, new int[] {1,4}	, 28),
	new TreasureType("Augmented Chain Mail"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*115*/
	    0,	 675,	5,   1, 270,  -2,   0,	16,   0, new int[] {1,4}	, 30),
	new TreasureType("Bar Chain Mail"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*116*/
	    0,	 720,	6,   1, 280,  -2,   0,	18,   0, new int[] {1,4}	, 34),
	new TreasureType("Metal Brigandine Armor"	,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*117*/
	    0,	 775,	7,   1, 290,  -3,   0,	19,   0, new int[] {1,4}	, 36),
	new TreasureType("Laminated Armor"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*118*/
	    0,	 825,	8,   1, 300,  -3,   0,	20,   0, new int[] {1,4}	, 38),
	new TreasureType("Partial Plate Armor"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*119*/
	    0,	 900,	9,   1, 260,  -3,   0,	22,   0, new int[] {1,6}	, 42),
	new TreasureType("Metal Lamellar Armor"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*120*/
	    0,	 950,	10,   1, 340,  -3,   0,	23,   0, new int[] {1,6}	, 44),
	new TreasureType("Full Plate Armor"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*121*/
	    0,	1050,	11,   1, 380,  -3,   0,	25,   0, new int[] {2,4}	, 48),
	new TreasureType("Ribbed Plate Armor"		,0x00000000L,	Constants.TV_HARD_ARMOR, '[',/*122*/
	    0,	1200,	12,   1, 380,  -3,   0,	28,   0, new int[] {2,4}	, 50),
	new TreasureType("& Cloak"			,0x00000000L,	Constants.TV_CLOAK, '(',	/*123*/
	    0,	   3,	1,   1,  10,	0,   0,	 1,   0, new int[] {0,0}	,  1),
	new TreasureType("& Set of Leather Gloves"	,0x00000000L,	Constants.TV_GLOVES, ']',	/*124*/
	    0,	   3,	1,   1,   5,	0,   0,	 1,   0, new int[] {0,0}	,  1),
	new TreasureType("& Set of Gauntlets"		,0x00000000L,	Constants.TV_GLOVES, ']',	/*125*/
	    0,	  35,	2,   1,  25,	0,   0,	 2,   0, new int[] {1,1}	, 12),
	new TreasureType("& Small Leather Shield"	,0x00000000L,	Constants.TV_SHIELD, ')',	/*126*/
	    0,	  30,	1,   1,  50,	0,   0,	 2,   0, new int[] {1,1}	,  3),
	new TreasureType("& Medium Leather Shield"	,0x00000000L,	Constants.TV_SHIELD, ')',	/*127*/
	    0,	  60,	2,   1,  75,	0,   0,	 3,   0, new int[] {1,2}	,  8),
	new TreasureType("& Large Leather Shield"	,0x00000000L,	Constants.TV_SHIELD, ')',	/*128*/
	    0,	 120,	3,   1, 100,	0,   0,	 4,   0, new int[] {1,2}	, 15),
	new TreasureType("& Small Metal Shield"		,0x00000000L,	Constants.TV_SHIELD, ')',	/*129*/
	    0,	  50,	4,   1,  65,	0,   0,	 3,   0, new int[] {1,2}	, 10),
	new TreasureType("& Medium Metal Shield"	,0x00000000L,	Constants.TV_SHIELD, ')',	/*130*/
	    0,	 125,	5,   1,  90,	0,   0,	 4,   0, new int[] {1,3}	, 20),
	new TreasureType("& Large Metal Shield"		,0x00000000L,	Constants.TV_SHIELD, ')',	/*131*/
	    0,	 200,	6,   1, 120,	0,   0,	 5,   0, new int[] {1,3}	, 30),
	new TreasureType("Gain Strength"		,0x00000001L,	Constants.TV_RING, '=',	/*132*/
	    0,	 400,	0,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 30),
	new TreasureType("Gain Dexterity"		,0x00000008L,	Constants.TV_RING, '=',	/*133*/
	    0,	 400,	1,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 30),
	new TreasureType("Gain Constitution"		,0x00000010L,	Constants.TV_RING, '=',	/*134*/
	    0,	 400,	2,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 30),
	new TreasureType("Gain Intelligence"		,0x00000002L,	Constants.TV_RING, '=',	/*135*/
	    0,	 350,	3,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 30),
	new TreasureType("Speed"			,0x00001000L,	Constants.TV_RING, '=',	/*136*/
	    0,	3000,	4,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Searching"			,0x00000040L,	Constants.TV_RING, '=',	/*137*/
	    0,	 250,	5,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Teleportation"		,0x80000400L,	Constants.TV_RING, '=',	/*138*/
	    0,	   0,	6,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Slow Digestion"		,0x00000080L,	Constants.TV_RING, '=',	/*139*/
	    0,	 250,	7,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Resist Fire"			,0x00080000L,	Constants.TV_RING, '=',	/*140*/
	    0,	 250,	8,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 14),
	new TreasureType("Resist Cold"			,0x00200000L,	Constants.TV_RING, '=',	/*141*/
	    0,	 250,	9,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 14),
	new TreasureType("Feather Falling"		,0x04000000L,	Constants.TV_RING, '=',	/*142*/
	    0,	 200,	10,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Adornment"			,0x00000000L,	Constants.TV_RING, '=',	/*143*/
	    0,	  20,	11,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	/* was a ring of adornment, subval = 12 here */
	new TreasureType("& Arrow~"			,0x00000000L,	Constants.TV_ARROW, '{',	/*144*/
	    0,	   1, 193,   1,   2,	0,   0,	 0,   0, new int[] {1,4}	, 15),
	new TreasureType("Weakness"			,0x80000001L,	Constants.TV_RING, '=',	/*145*/
	   -5,	   0,	13,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Lordly Protection (FIRE)"	,0x00080000L,	Constants.TV_RING, '=',	/*146*/
	    0,	1200,	14,   1,   2,	0,   0,	 0,   5, new int[] {0,0}	, 50),
	new TreasureType("Lordly Protection (ACID)"	,0x00100000L,	Constants.TV_RING, '=',	/*147*/
	    0,	1200,	15,   1,   2,	0,   0,	 0,   5, new int[] {0,0}	, 50),
	new TreasureType("Lordly Protection (COLD)"	,0x00200000L,	Constants.TV_RING, '=',	/*148*/
	    0,	1200,	16,   1,   2,	0,   0,	 0,   5, new int[] {0,0}	, 50),
	new TreasureType("WOE"				,0x80000644L,	Constants.TV_RING, '=',	/*149*/
	   -5,	   0,	17,   1,   2,	0,   0,	 0,  -3, new int[] {0,0}	, 50),
	new TreasureType("Stupidity"			,0x80000002L,	Constants.TV_RING, '=',	/*150*/
	   -5,	   0,	18,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Increase Damage"		,0x00000000L,	Constants.TV_RING, '=',	/*151*/
	    0,	 100,	19,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 20),
	new TreasureType("Increase To-Hit"		,0x00000000L,	Constants.TV_RING, '=',	/*152*/
	    0,	 100,	20,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 20),
	new TreasureType("Protection"			,0x00000000L,	Constants.TV_RING, '=',	/*153*/
	    0,	 100,	21,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Aggravate Monster"		,0x80000200L,	Constants.TV_RING, '=',	/*154*/
	    0,	   0,	22,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("See Invisible"		,0x01000000L,	Constants.TV_RING, '=',	/*155*/
	    0,	 340,	23,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 40),
	new TreasureType("Sustain Strength"		,0x00400000L,	Constants.TV_RING, '=',	/*156*/
	    1,	 750,	24,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 44),
	new TreasureType("Sustain Intelligence"		,0x00400000L,	Constants.TV_RING, '=',	/*157*/
	    2,	 600,	25,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 44),
	new TreasureType("Sustain Wisdom"		,0x00400000L,	Constants.TV_RING, '=',	/*158*/
	    3,	 600,	26,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 44),
	new TreasureType("Sustain Constitution"		,0x00400000L,	Constants.TV_RING, '=',	/*159*/
	    4,	 750,	27,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 44),
	new TreasureType("Sustain Dexterity"		,0x00400000L,	Constants.TV_RING, '=',	/*160*/
	    5,	 750,	28,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 44),
	new TreasureType("Sustain Charisma"		,0x00400000L,	Constants.TV_RING, '=',	/*161*/
	    6,	 500,	29,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 44),
	new TreasureType("Slaying"			,0x00000000L,	Constants.TV_RING, '=',	/*162*/
	    0,	1000,	30,   1,   2,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Wisdom"			,0x00000004L,	Constants.TV_AMULET, '"',	/*163*/
	    0,	 300,	0,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 20),
	new TreasureType("Charisma"			,0x00000020L,	Constants.TV_AMULET, '"',	/*164*/
	    0,	 250,	1,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 20),
	new TreasureType("Searching"			,0x00000040L,	Constants.TV_AMULET, '"',	/*165*/
	    0,	 250,	2,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 14),
	new TreasureType("Teleportation"		,0x80000400L,	Constants.TV_AMULET, '"',	/*166*/
	    0,	   0,	3,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 14),
	new TreasureType("Slow Digestion"		,0x00000080L,	Constants.TV_AMULET, '"',	/*167*/
	    0,	 200,	4,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 14),
	new TreasureType("Resist Acid"			,0x00100000L,	Constants.TV_AMULET, '"',	/*168*/
	    0,	 300,	5,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 24),
	new TreasureType("Adornment"			,0x00000000L,	Constants.TV_AMULET, '"',	/*169*/
	    0,	  20,	6,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 16),
	/* was an amulet of adornment here, subval = 7 */
	new TreasureType("& Bolt~"			,0x00000000L,	Constants.TV_BOLT, '{',	/*170*/
	    0,	   2, 193,   1,   3,	0,   0,	 0,   0, new int[] {1,5}	, 25),
	new TreasureType("the Magi"			,0x01800040L,	Constants.TV_AMULET, '"',	/*171*/
	    0,	5000,	8,   1,   3,	0,   0,	 0,   3, new int[] {0,0}	, 50),
	new TreasureType("DOOM"				,0x8000007FL,	Constants.TV_AMULET, '"',	/*172*/
	   -5,	   0,	9,   1,   3,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Enchant Weapon To-Hit"	,0x00000001L,	Constants.TV_SCROLL1, '?',/*173*/
	    0,	 125,	64,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Enchant Weapon To-Dam"	,0x00000002L,	Constants.TV_SCROLL1, '?',/*174*/
	    0,	 125,	65,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Enchant Armor"		,0x00000004L,	Constants.TV_SCROLL1, '?',/*175*/
	    0,	 125,	66,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Identify"			,0x00000008L,	Constants.TV_SCROLL1, '?',/*176*/
	    0,	  50,	67,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("Identify"			,0x00000008L,	Constants.TV_SCROLL1, '?',/*177*/
	    0,	  50,	67,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Identify"			,0x00000008L,	Constants.TV_SCROLL1, '?',/*178*/
	    0,	  50,	67,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	new TreasureType("Identify"			,0x00000008L,	Constants.TV_SCROLL1, '?',/*179*/
	    0,	  50,	67,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 30),
	new TreasureType("Remove Curse"			,0x00000010L,	Constants.TV_SCROLL1, '?',/*180*/
	    0,	 100,	68,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Light"			,0x00000020L,	Constants.TV_SCROLL1, '?',/*181*/
	    0,	  15,	69,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Light"			,0x00000020L,	Constants.TV_SCROLL1, '?',/*182*/
	    0,	  15,	69,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  3),
	new TreasureType("Light"			,0x00000020L,	Constants.TV_SCROLL1, '?',/*183*/
	    0,	  15,	69,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  7),
	new TreasureType("Summon Monster"		,0x00000040L,	Constants.TV_SCROLL1, '?',/*184*/
	    0,	   0,	70,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("Phase Door"			,0x00000080L,	Constants.TV_SCROLL1, '?',/*185*/
	    0,	  15,	71,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("Teleport"			,0x00000100L,	Constants.TV_SCROLL1, '?',/*186*/
	    0,	  40,	72,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	new TreasureType("Teleport Level"		,0x00000200L,	Constants.TV_SCROLL1, '?',/*187*/
	    0,	  50,	73,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 20),
	new TreasureType("Monster Confusion"		,0x00000400L,	Constants.TV_SCROLL1, '?',/*188*/
	    0,	  30,	74,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Magic Mapping"		,0x00000800L,	Constants.TV_SCROLL1, '?',/*189*/
	    0,	  40,	75,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Sleep Monster"		,0x00001000L,	Constants.TV_SCROLL1, '?',/*190*/
	    0,	  35,	76,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Rune of Protection"		,0x00002000L,	Constants.TV_SCROLL1, '?',/*191*/
	    0,	 500,	77,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Treasure Detection"		,0x00004000L,	Constants.TV_SCROLL1, '?',/*192*/
	    0,	  15,	78,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Object Detection"		,0x00008000L,	Constants.TV_SCROLL1, '?',/*193*/
	    0,	  15,	79,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Trap Detection"		,0x00010000L,	Constants.TV_SCROLL1, '?',/*194*/
	    0,	  35,	80,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Trap Detection"		,0x00010000L,	Constants.TV_SCROLL1, '?',/*195*/
	    0,	  35,	80,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  8),
	new TreasureType("Trap Detection"		,0x00010000L,	Constants.TV_SCROLL1, '?',/*196*/
	    0,	  35,	80,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Door/Stair Location"		,0x00020000L,	Constants.TV_SCROLL1, '?',/*197*/
	    0,	  35,	81,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Door/Stair Location"		,0x00020000L,	Constants.TV_SCROLL1, '?',/*198*/
	    0,	  35,	81,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	new TreasureType("Door/Stair Location"		,0x00020000L,	Constants.TV_SCROLL1, '?',/*199*/
	    0,	  35,	81,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 15),
	new TreasureType("Mass Genocide"		,0x00040000L,	Constants.TV_SCROLL1, '?',/*200*/
	    0,	1000,	82,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Detect Invisible"		,0x00080000L,	Constants.TV_SCROLL1, '?',/*201*/
	    0,	  15,	83,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("Aggravate Monster"		,0x00100000L,	Constants.TV_SCROLL1, '?',/*202*/
	    0,	   0,	84,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Trap Creation"		,0x00200000L,	Constants.TV_SCROLL1, '?',/*203*/
	    0,	   0,	85,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Trap/Door Destruction"	,0x00400000L,	Constants.TV_SCROLL1, '?',/*204*/
	    0,	  50,	86,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Door Creation"		,0x00800000L,	Constants.TV_SCROLL1, '?',/*205*/
	    0,	 100,	87,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Recharging"			,0x01000000L,	Constants.TV_SCROLL1, '?',/*206*/
	    0,	 200,	88,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 40),
	new TreasureType("Genocide"			,0x02000000L,	Constants.TV_SCROLL1, '?',/*207*/
	    0,	 750,	89,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 35),
	new TreasureType("Darkness"			,0x04000000L,	Constants.TV_SCROLL1, '?',/*208*/
	    0,	   0,	90,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("Protection from Evil"		,0x08000000L,	Constants.TV_SCROLL1, '?',/*209*/
	    0,	  50,	91,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 30),
	new TreasureType("Create Food"			,0x10000000L,	Constants.TV_SCROLL1, '?',/*210*/
	    0,	  10,	92,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("Dispel Undead"		,0x20000000L,	Constants.TV_SCROLL1, '?',/*211*/
	    0,	 200,	93,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 40),
	new TreasureType("*Enchant Weapon*"		,0x00000001L,	Constants.TV_SCROLL2, '?',/*212*/
	    0,	 500,	94,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Curse Weapon"			,0x00000002L,	Constants.TV_SCROLL2, '?',/*213*/
	    0,	   0,	95,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("*Enchant Armor*"		,0x00000004L,	Constants.TV_SCROLL2, '?',/*214*/
	    0,	 500,	96,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Curse Armor"			,0x00000008L,	Constants.TV_SCROLL2, '?',/*215*/
	    0,	   0,	97,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 50),
	new TreasureType("Summon Undead"		,0x00000010L,	Constants.TV_SCROLL2, '?',/*216*/
	    0,	   0,	98,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 15),
	new TreasureType("Blessing"			,0x00000020L,	Constants.TV_SCROLL2, '?',/*217*/
	    0,	  15,	99,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  1),
	new TreasureType("Holy Chant"			,0x00000040L,	Constants.TV_SCROLL2, '?',/*218*/
	    0,	  40, 100,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 12),
	new TreasureType("Holy Prayer"			,0x00000080L,	Constants.TV_SCROLL2, '?',/*219*/
	    0,	  80, 101,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 24),
	new TreasureType("Word-of-Recall"		,0x00000100L,	Constants.TV_SCROLL2, '?',/*220*/
	    0,	 150, 102,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	,  5),
	new TreasureType("*Destruction*"		,0x00000200L,	Constants.TV_SCROLL2, '?',/*221*/
	    0,	 250, 103,   1,   5,	0,   0,	 0,   0, new int[] {0,0}	, 40),
	/* SMJ, AJ, Water must be subval 64-66 resp. for objdes to work */
	new TreasureType("Slime Mold Juice"		,0x30000000L,	Constants.TV_POTION1, '!',/*222*/
	  400,	   2,	64,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Apple Juice"			,0x00000000L,	Constants.TV_POTION1, '!',/*223*/
	  250,	   1,	65,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Water"			,0x00000000L,	Constants.TV_POTION1, '!',/*224*/
	  200,	   0,	66,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Gain Strength"		,0x00000001L,	Constants.TV_POTION1, '!',/*225*/
	    0,	 300,	67,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Weakness"			,0x00000002L,	Constants.TV_POTION1, '!',/*226*/
	    0,	   0,	68,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  3),
	new TreasureType("Restore Strength"		,0x00000004L,	Constants.TV_POTION1, '!',/*227*/
	    0,	 300,	69,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Gain Intelligence"		,0x00000008L,	Constants.TV_POTION1, '!',/*228*/
	    0,	 300,	70,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Lose Intelligence"		,0x00000010L,	Constants.TV_POTION1, '!',/*229*/
	    0,	   0,	71,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Restore Intelligence"		,0x00000020L,	Constants.TV_POTION1, '!',/*230*/
	    0,	 300,	72,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Gain Wisdom"			,0x00000040L,	Constants.TV_POTION1, '!',/*231*/
	    0,	 300,	73,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Lose Wisdom"			,0x00000080L,	Constants.TV_POTION1, '!',/*232*/
	    0,	   0,	74,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Restore Wisdom"		,0x00000100L,	Constants.TV_POTION1, '!',/*233*/
	    0,	 300,	75,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Charisma"			,0x00000200L,	Constants.TV_POTION1, '!',/*234*/
	    0,	 300,	76,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Ugliness"			,0x00000400L,	Constants.TV_POTION1, '!',/*235*/
	    0,	   0,	77,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Restore Charisma"		,0x00000800L,	Constants.TV_POTION1, '!',/*236*/
	    0,	 300,	78,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Cure Light Wounds"		,0x10001000L,	Constants.TV_POTION1, '!',/*237*/
	   50,	  15,	79,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Cure Light Wounds"		,0x10001000L,	Constants.TV_POTION1, '!',/*238*/
	   50,	  15,	79,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Cure Light Wounds"		,0x10001000L,	Constants.TV_POTION1, '!',/*239*/
	   50,	  15,	79,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Cure Serious Wounds"		,0x30002000L,	Constants.TV_POTION1, '!',/*240*/
	  100,	  40,	80,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  3),
	new TreasureType("Cure Critical Wounds"		,0x70004000L,	Constants.TV_POTION1, '!',/*241*/
	  100,	 100,	81,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  5),
	new TreasureType("Healing"			,0x70008000L,	Constants.TV_POTION1, '!',/*242*/
	  200,	 200,	82,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 12),
	new TreasureType("Gain Constitution"		,0x00010000L,	Constants.TV_POTION1, '!',/*243*/
	    0,	 300,	83,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Gain Experience"		,0x00020000L,	Constants.TV_POTION1, '!',/*244*/
	    0,	2500,	84,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 50),
	new TreasureType("Sleep"			,0x00040000L,	Constants.TV_POTION1, '!',/*245*/
	  100,	   0,	85,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Blindness"			,0x00080000L,	Constants.TV_POTION1, '!',/*246*/
	    0,	   0,	86,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Confusion"			,0x00100000L,	Constants.TV_POTION1, '!',/*247*/
	   50,	   0,	87,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Poison"			,0x00200000L,	Constants.TV_POTION1, '!',/*248*/
	    0,	   0,	88,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  3),
	new TreasureType("Haste Self"			,0x00400000L,	Constants.TV_POTION1, '!',/*249*/
	    0,	  75,	89,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Slowness"			,0x00800000L,	Constants.TV_POTION1, '!',/*250*/
	   50,	   0,	90,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Gain Dexterity"		,0x02000000L,	Constants.TV_POTION1, '!',/*251*/
	    0,	 300,	91,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Restore Dexterity"		,0x04000000L,	Constants.TV_POTION1, '!',/*252*/
	    0,	 300,	92,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Restore Constitution"		,0x68000000L,	Constants.TV_POTION1, '!',/*253*/
	    0,	 300,	93,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Lose Experience"		,0x00000002L,	Constants.TV_POTION2, '!',/*254*/
	    0,	   0,	95,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 10),
	new TreasureType("Salt Water"			,0x00000004L,	Constants.TV_POTION2, '!',/*255*/
	    0,	   0,	96,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Invulnerability"		,0x00000008L,	Constants.TV_POTION2, '!',/*256*/
	    0,	 250,	97,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Heroism"			,0x00000010L,	Constants.TV_POTION2, '!',/*257*/
	    0,	  35,	98,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Super Heroism"		,0x00000020L,	Constants.TV_POTION2, '!',/*258*/
	    0,	 100,	99,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  3),
	new TreasureType("Boldness"			,0x00000040L,	Constants.TV_POTION2, '!',/*259*/
	    0,	  10, 100,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Restore Life Levels"		,0x00000080L,	Constants.TV_POTION2, '!',/*260*/
	    0,	 400, 101,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Resist Heat"			,0x00000100L,	Constants.TV_POTION2, '!',/*261*/
	    0,	  30, 102,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Resist Cold"			,0x00000200L,	Constants.TV_POTION2, '!',/*262*/
	    0,	  30, 103,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Detect Invisible"		,0x00000400L,	Constants.TV_POTION2, '!',/*263*/
	    0,	  50, 104,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  3),
	new TreasureType("Slow Poison"			,0x00000800L,	Constants.TV_POTION2, '!',/*264*/
	    0,	  25, 105,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("Neutralize Poison"		,0x00001000L,	Constants.TV_POTION2, '!',/*265*/
	    0,	  75, 106,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  5),
	new TreasureType("Restore Mana"			,0x00002000L,	Constants.TV_POTION2, '!',/*266*/
	    0,	 350, 107,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Infra-Vision"			,0x00004000L,	Constants.TV_POTION2, '!',/*267*/
	    0,	  20, 108,   1,   4,	0,   0,	 0,   0, new int[] {1,1}	,  3),
	new TreasureType("& Flask~ of oil"		,0x00040000L,	Constants.TV_FLASK, '!',	/*268*/
	 7500,	   3,	64,   1,  10,	0,   0,	 0,   0, new int[] {2,6}	,  1),
	new TreasureType("Light"			,0x00000001L,	Constants.TV_WAND, '-',	/*269*/
	    0,	 200,	0,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Lightning Bolts"		,0x00000002L,	Constants.TV_WAND, '-',	/*270*/
	    0,	 600,	1,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 15),
	new TreasureType("Frost Bolts"			,0x00000004L,	Constants.TV_WAND, '-',	/*271*/
	    0,	 800,	2,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 20),
	new TreasureType("Fire Bolts"			,0x00000008L,	Constants.TV_WAND, '-',	/*272*/
	    0,	1000,	3,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 30),
	new TreasureType("Stone-to-Mud"			,0x00000010L,	Constants.TV_WAND, '-',	/*273*/
	    0,	 300,	4,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 12),
	new TreasureType("Polymorph"			,0x00000020L,	Constants.TV_WAND, '-',	/*274*/
	    0,	 400,	5,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 20),
	new TreasureType("Heal Monster"			,0x00000040L,	Constants.TV_WAND, '-',	/*275*/
	    0,	   0,	6,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Haste Monster"		,0x00000080L,	Constants.TV_WAND, '-',	/*276*/
	    0,	   0,	7,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Slow Monster"			,0x00000100L,	Constants.TV_WAND, '-',	/*277*/
	    0,	 500,	8,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Confuse Monster"		,0x00000200L,	Constants.TV_WAND, '-',	/*278*/
	    0,	 400,	9,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Sleep Monster"		,0x00000400L,	Constants.TV_WAND, '-',	/*279*/
	    0,	 500,	10,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  7),
	new TreasureType("Drain Life"			,0x00000800L,	Constants.TV_WAND, '-',	/*280*/
	    0,	1200,	11,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 50),
	new TreasureType("Trap/Door Destruction"	,0x00001000L,	Constants.TV_WAND, '-',	/*281*/
	    0,	 100,	12,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 12),
	new TreasureType("Magic Missile"		,0x00002000L,	Constants.TV_WAND, '-',	/*282*/
	    0,	 200,	13,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Wall Building"		,0x00004000L,	Constants.TV_WAND, '-',	/*283*/
	    0,	 400,	14,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 25),
	new TreasureType("Clone Monster"		,0x00008000L,	Constants.TV_WAND, '-',	/*284*/
	    0,	   0,	15,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 15),
	new TreasureType("Teleport Away"		,0x00010000L,	Constants.TV_WAND, '-',	/*285*/
	    0,	 350,	16,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 20),
	new TreasureType("Disarming"			,0x00020000L,	Constants.TV_WAND, '-',	/*286*/
	    0,	 700,	17,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 20),
	new TreasureType("Lightning Balls"		,0x00040000L,	Constants.TV_WAND, '-',	/*287*/
	    0,	1200,	18,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 35),
	new TreasureType("Cold Balls"			,0x00080000L,	Constants.TV_WAND, '-',	/*288*/
	    0,	1500,	19,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("Fire Balls"			,0x00100000L,	Constants.TV_WAND, '-',	/*289*/
	    0,	1800,	20,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 50),
	new TreasureType("Stinking Cloud"		,0x00200000L,	Constants.TV_WAND, '-',	/*290*/
	    0,	 400,	21,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  5),
	new TreasureType("Acid Balls"			,0x00400000L,	Constants.TV_WAND, '-',	/*291*/
	    0,	1650,	22,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	, 48),
	new TreasureType("Wonder"			,0x00800000L,	Constants.TV_WAND, '-',	/*292*/
	    0,	 250,	23,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  2),
	new TreasureType("Light"			,0x00000001L,	Constants.TV_STAFF, '_',	/*293*/
	    0,	 250,	0,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	,  5),
	new TreasureType("Door/Stair Location"		,0x00000002L,	Constants.TV_STAFF, '_',	/*294*/
	    0,	 350,	1,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 10),
	new TreasureType("Trap Location"		,0x00000004L,	Constants.TV_STAFF, '_',	/*295*/
	    0,	 350,	2,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 10),
	new TreasureType("Treasure Location"		,0x00000008L,	Constants.TV_STAFF, '_',	/*296*/
	    0,	 200,	3,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	,  5),
	new TreasureType("Object Location"		,0x00000010L,	Constants.TV_STAFF, '_',	/*297*/
	    0,	 200,	4,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	,  5),
	new TreasureType("Teleportation"		,0x00000020L,	Constants.TV_STAFF, '_',	/*298*/
	    0,	 400,	5,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 20),
	new TreasureType("Earthquakes"			,0x00000040L,	Constants.TV_STAFF, '_',	/*299*/
	    0,	 350,	6,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 40),
	new TreasureType("Summoning"			,0x00000080L,	Constants.TV_STAFF, '_',	/*300*/
	    0,	   0,	7,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 10),
	new TreasureType("Summoning"			,0x00000080L,	Constants.TV_STAFF, '_',	/*301*/
	    0,	   0,	7,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 50),
	new TreasureType("*Destruction*"		,0x00000200L,	Constants.TV_STAFF, '_',	/*302*/
	    0,	2500,	8,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 50),
	new TreasureType("Starlight"			,0x00000400L,	Constants.TV_STAFF, '_',	/*303*/
	    0,	 800,	9,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 20),
	new TreasureType("Haste Monsters"		,0x00000800L,	Constants.TV_STAFF, '_',	/*304*/
	    0,	   0,	10,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 10),
	new TreasureType("Slow Monsters"		,0x00001000L,	Constants.TV_STAFF, '_',	/*305*/
	    0,	 800,	11,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 10),
	new TreasureType("Sleep Monsters"		,0x00002000L,	Constants.TV_STAFF, '_',	/*306*/
	    0,	 700,	12,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 10),
	new TreasureType("Cure Light Wounds"		,0x00004000L,	Constants.TV_STAFF, '_',	/*307*/
	    0,	 350,	13,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	,  5),
	new TreasureType("Detect Invisible"		,0x00008000L,	Constants.TV_STAFF, '_',	/*308*/
	    0,	 200,	14,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	,  5),
	new TreasureType("Speed"			,0x00010000L,	Constants.TV_STAFF, '_',	/*309*/
	    0,	1000,	15,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 40),
	new TreasureType("Slowness"			,0x00020000L,	Constants.TV_STAFF, '_',	/*310*/
	    0,	   0,	16,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 40),
	new TreasureType("Mass Polymorph"		,0x00040000L,	Constants.TV_STAFF, '_',	/*311*/
	    0,	 750,	17,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 46),
	new TreasureType("Remove Curse"			,0x00080000L,	Constants.TV_STAFF, '_',	/*312*/
	    0,	 500,	18,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 47),
	new TreasureType("Detect Evil"			,0x00100000L,	Constants.TV_STAFF, '_',	/*313*/
	    0,	 350,	19,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 20),
	new TreasureType("Curing"			,0x00200000L,	Constants.TV_STAFF, '_',	/*314*/
	    0,	1000,	20,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 25),
	new TreasureType("Dispel Evil"			,0x00400000L,	Constants.TV_STAFF, '_',	/*315*/
	    0,	1200,	21,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 49),
	new TreasureType("Darkness"			,0x01000000L,	Constants.TV_STAFF, '_',	/*316*/
	    0,	   0,	22,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	, 50),
	new TreasureType("Darkness"			,0x01000000L,	Constants.TV_STAFF, '_',	/*317*/
	    0,	   0,	22,   1,  50,	0,   0,	 0,   0, new int[] {1,2}	,  5),
	new TreasureType("[Beginners-Magik]"		,0x0000007FL,	Constants.TV_MAGIC_BOOK, '?',/*318*/
	    0,	  25,	64,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("[Magik I]"			,0x0000FF80L,	Constants.TV_MAGIC_BOOK, '?',/*319*/
	    0,	 100,	65,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("[Magik II]"			,0x00FF0000L,	Constants.TV_MAGIC_BOOK, '?',/*320*/
	    0,	 400,	66,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("[The Mages Guide to Power]"	,0x7F000000L,	Constants.TV_MAGIC_BOOK, '?',/*321*/
	    0,	 800,	67,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("[Beginners Handbook]"		,0x000000FFL,	Constants.TV_PRAYER_BOOK, '?',/*322*/
	    0,	  25,	64,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("[Words of Wisdom]"		,0x0000FF00L,	Constants.TV_PRAYER_BOOK, '?',/*323*/
	    0,	 100,	65,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("[Chants and Blessings]"	,0x01FF0000L,	Constants.TV_PRAYER_BOOK, '?',/*324*/
	    0,	 300,	66,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("[Exorcism and Dispelling]"	,0x7E000000L,	Constants.TV_PRAYER_BOOK, '?',/*325*/
	    0,	 900,	67,   1,  30,   0,   0,	 0,   0, new int[] {1,1}	, 40),
	new TreasureType("& Small wooden chest"		,0x0F000000L,	 Constants.TV_CHEST, '&',	/*326*/
	    0,	  20,	1,   1, 250,	0,   0,	 0,   0, new int[] {2,3}	,  7),
	new TreasureType("& Large wooden chest"		,0x15000000L,	 Constants.TV_CHEST, '&',	/*327*/
	    0,	  60,	4,   1, 500,	0,   0,	 0,   0, new int[] {2,5}	, 15),
	new TreasureType("& Small iron chest"		,0x0F000000L,	 Constants.TV_CHEST, '&',	/*328*/
	    0,	 100,	7,   1, 300,	0,   0,	 0,   0, new int[] {2,4}	, 25),
	new TreasureType("& Large iron chest"		,0x1F000000L,	 Constants.TV_CHEST, '&',	/*329*/
	    0,	 150,	10,   1,1000,	0,   0,	 0,   0, new int[] {2,6}	, 35),
	new TreasureType("& Small steel chest"		,0x0F000000L,	 Constants.TV_CHEST, '&',	/*330*/
	    0,	 200,	13,   1, 500,	0,   0,	 0,   0, new int[] {2,4}	, 45),
	new TreasureType("& Large steel chest"		,0x23000000L,	 Constants.TV_CHEST, '&',	/*331*/
	    0,	 250,	16,   1,1000,	0,   0,	 0,   0, new int[] {2,6}	, 50),
	new TreasureType("& Rat Skeleton"		,0x00000000L,	 Constants.TV_MISC, 's',	/*332*/
	    0,	   0,	1,   1,  10,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Giant Centipede Skeleton"	,0x00000000L,	 Constants.TV_MISC, 's',	/*333*/
	    0,	   0,	2,   1,  25,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("some filthy rags"		,0x00000000L,	Constants.TV_SOFT_ARMOR, '~',/*334*/
	    0,	   0,	63,   1,  20,	0,   0,	 1,   0, new int[] {0,0}	,  0),
	new TreasureType("& empty bottle"		,0x00000000L,	 Constants.TV_MISC, '!',	/*335*/
	    0,	   0,	4,   1,   2,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("some shards of pottery"	,0x00000000L,	 Constants.TV_MISC, '~',	/*336*/
	    0,	   0,	5,   1,   5,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& Human Skeleton"		,0x00000000L,	 Constants.TV_MISC, 's',	/*337*/
	    0,	   0,	7,   1,  50,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Dwarf Skeleton"		,0x00000000L,	 Constants.TV_MISC, 's',	/*338*/
	    0,	   0,	8,   1,  60,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Elf Skeleton"		,0x00000000L,	 Constants.TV_MISC, 's',	/*339*/
	    0,	   0,	9,   1,  40,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Gnome Skeleton"		,0x00000000L,	 Constants.TV_MISC, 's',	/*340*/
	    0,	   0,	10,   1,  25,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& broken set of teeth"	,0x00000000L,	 Constants.TV_MISC, 's',	/*341*/
	    0,	   0,	11,   1,   3,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& large broken bone"		,0x00000000L,	 Constants.TV_MISC, 's',	/*342*/
	    0,	   0,	12,   1,   2,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& broken stick"		,0x00000000L,	 Constants.TV_MISC, '~',	/*343*/
	    0,	   0,	13,   1,   3,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	/* end of Dungeon items */
	/* Store items, which are not also dungeon items, some of these can be
	   found above, except that the number is >1 below */
	new TreasureType("& Ration~ of Food"		,0x00000000L,	Constants.TV_FOOD, ',',	/*344*/
	 5000,	   3,	90,   5,  10,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("& Hard Biscuit~"		,0x00000000L,	Constants.TV_FOOD, ',',	/*345*/
	  500,	   1,	93,   5,   2,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("& Strip~ of Beef Jerky"	,0x00000000L,	Constants.TV_FOOD, ',',	/*346*/
	 1750,	   2,	94,   5,   2,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("& Pint~ of Fine Ale"		,0x00000000L,	Constants.TV_FOOD, ',',	/*347*/
	  500,	   1,	95,   3,  10,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("& Pint~ of Fine Wine"		,0x00000000L,	Constants.TV_FOOD, ',',	/*348*/
	  400,	   2,	96,   1,  10,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("& Pick"			,0x20000000L,	Constants.TV_DIGGING, '\\',/*349*/
	    1,	  50,	1,   1, 150,	0,   0,	 0,   0, new int[] {1,3}	,  0),
	new TreasureType("& Shovel"			,0x20000000L,	Constants.TV_DIGGING, '\\',/*350*/
	    0,	  15,	4,   1,  60,	0,   0,	 0,   0, new int[] {1,2}	,  0),
	new TreasureType("Identify"			,0x00000008L,	Constants.TV_SCROLL1, '?',/*351*/
	    0,	  50,  67,   2,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Light"			,0x00000020L,	Constants.TV_SCROLL1, '?',/*352*/
	    0,	  15,  69,   3,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Phase Door"			,0x00000080L,	Constants.TV_SCROLL1, '?',/*353*/
	    0,	  15,  71,   2,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Magic Mapping"		,0x00000800L,	Constants.TV_SCROLL1, '?',/*354*/
	    0,	  40,  75,   2,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Treasure Detection"		,0x00004000L,	Constants.TV_SCROLL1, '?',/*355*/
	    0,	  15,  78,   2,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Object Detection"		,0x00008000L,	Constants.TV_SCROLL1, '?',/*356*/
	    0,	  15,  79,   2,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Detect Invisible"		,0x00080000L,	Constants.TV_SCROLL1, '?',/*357*/
	    0,	  15,  83,   2,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Blessing"			,0x00000020L,	Constants.TV_SCROLL2, '?',/*358*/
	    0,	  15,  99,   2,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Word-of-Recall"		,0x00000100L,	Constants.TV_SCROLL2, '?',/*359*/
	    0,	 150, 102,   3,   5,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Cure Light Wounds"		,0x10001000L,	Constants.TV_POTION1, '!',/*360*/
	   50,	  15,  79,   2,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Heroism"			,0x00000010L,	Constants.TV_POTION2, '!',/*361*/
	    0,	  35,  98,   2,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Boldness"			,0x00000040L,	Constants.TV_POTION2, '!',/*362*/
	    0,	  10, 100,   2,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("Slow Poison"			,0x00000800L,	Constants.TV_POTION2, '!',/*363*/
	    0,	  25, 105,   2,   4,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& Brass Lantern~"		,0x00000000L,	Constants.TV_LIGHT, '~',	/*364*/
	 7500,	  35,	0,   1,  50,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Wooden Torch~"		,0x00000000L,	Constants.TV_LIGHT, '~',	/*365*/
	 4000,	   2, 192,   5,  30,	0,   0,	 0,   0, new int[] {1,1}	,  1),
	new TreasureType("& Flask~ of oil"		,0x00040000L,	Constants.TV_FLASK, '!',	/*366*/
	 7500,	   3,	64,   5,  10,	0,   0,	 0,   0, new int[] {2,6}	,  1),
	/* end store items */
	/* start doors */
	/* Secret door must have same subval as closed door in	*/
	/* TRAP_LISTB.	See CHANGE_TRAP. Must use & because of stone_to_mud. */
	new TreasureType("& open door"			,0x00000000L, Constants.TV_OPEN_DOOR, '\'',
	    0,	   0,	1,   1,   0,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& closed door"		,0x00000000L, Constants.TV_CLOSED_DOOR, '+',
	    0,	   0,	19,   1,   0,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("& secret door"		,0x00000000L, Constants.TV_SECRET_DOOR, '#',/* 369 */
	    0,	   0,	19,   1,   0,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	/* end doors */
	/* stairs */
	new TreasureType("an up staircase "		,0x00000000L, Constants.TV_UP_STAIR, '<',	/* 370 */
	    0,	   0,	1,   1,   0,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	new TreasureType("a down staircase"		,0x00000000L, Constants.TV_DOWN_STAIR, '>',/* 371 */
	    0,	   0,	1,   1,   0,	0,   0,	 0,   0, new int[] {1,1}	,  0),
	/* store door */
	/* Stores are just special traps		*/
	new TreasureType("General Store"		,0x00000000L, Constants.TV_STORE_DOOR, '1',/* 372 */
	    0,	   0, 101,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Armory"			,0x00000000L, Constants.TV_STORE_DOOR, '2',
	    0,	   0, 102,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Weapon Smiths"		,0x00000000L, Constants.TV_STORE_DOOR, '3',
	    0,	   0, 103,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Temple"			,0x00000000L, Constants.TV_STORE_DOOR, '4',
	    0,	   0, 104,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Alchemy Shop"			,0x00000000L, Constants.TV_STORE_DOOR, '5',
	    0,	   0, 105,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType("Magic Shop"			,0x00000000L, Constants.TV_STORE_DOOR, '6',/* 377 */
	    0,	   0, 106,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	/* end store door */
	/* Traps are just Nasty treasures.				*/
	/* Traps: Level represents the relative difficulty of disarming;	*/
	/* and P1 represents the experienced gained when disarmed*/
	new TreasureType("an open pit"			,0x00000000L, Constants.TV_VIS_TRAP, ' ',	/* 378 */
	    1,	   0,	1,   1,   0,	0,   0,	 0,   0, new int[] {2,6}	,50),
	new TreasureType("an arrow trap"		,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    3,	   0,	2,   1,   0,	0,   0,	 0,   0, new int[] {1,8}	,90),
	new TreasureType("a covered pit"		,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    2,	   0,	3,   1,   0,	0,   0,	 0,   0, new int[] {2,6}	,60),
	new TreasureType("a trap door"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	4,   1,   0,	0,   0,	 0,   0, new int[] {2,8}	,75),
	new TreasureType("a gas trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    3,	   0,	5,   1,   0,	0,   0,	 0,   0, new int[] {1,4}	,95),
	new TreasureType("a loose rock"			,0x00000000L, Constants.TV_INVIS_TRAP, ';',
	    0,	   0,	6,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,10),
	new TreasureType("a dart trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	7,   1,   0,	0,   0,	 0,   0, new int[] {1,4}	,110),
	new TreasureType("a strange rune"		,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	8,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,90),
	new TreasureType("some loose rock"		,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	9,   1,   0,	0,   0,	 0,   0, new int[] {2,6}	,90),
	new TreasureType("a gas trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	   10,	   0,	10,   1,   0,	0,   0,	 0,   0, new int[] {1,4}	,105),
	new TreasureType("a strange rune"		,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	11,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,90),
	new TreasureType("a blackened spot"		,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	   10,	   0,	12,   1,   0,	0,   0,	 0,   0, new int[] {4,6}	,110),
	new TreasureType("some corroded rock"		,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	   10,	   0,	13,   1,   0,	0,   0,	 0,   0, new int[] {4,6}	,110),
	new TreasureType("a gas trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	14,   1,   0,	0,   0,	 0,   0, new int[] {2,6}	,105),
	new TreasureType("a gas trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	15,   1,   0,	0,   0,	 0,   0, new int[] {1,4}	,110),
	new TreasureType("a gas trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	16,   1,   0,	0,   0,	 0,   0, new int[] {1,8}	,105),
	new TreasureType("a dart trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',
	    5,	   0,	17,   1,   0,	0,   0,	 0,   0, new int[] {1,8}	,110),
	new TreasureType("a dart trap"			,0x00000000L, Constants.TV_INVIS_TRAP, '^',/* 395 */
	    5,	   0,	18,   1,   0,	0,   0,	 0,   0, new int[] {1,8}	,110),
	/* rubble */
	new TreasureType("some rubble"			,0x00000000L, Constants.TV_RUBBLE, ':',	/* 396 */
	    0,	   0,	1,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	/* mush */
	new TreasureType("& pint~ of fine grade mush"	,0x00000000L, Constants.TV_FOOD, ',',	/* 397 */
	 1500,	   0,  97,   1,   1,   0,   0,   0,   0, new int[] {1,1}  ,  1),
	/* Special trap	*/
	new TreasureType("a strange rune"		,0x00000000L, Constants.TV_VIS_TRAP, '^',	/* 398 */
	    0,	   0,	99,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	, 10),
	/* Gold list (All types of gold and gems are defined here)	*/
	new TreasureType("copper"			,0x00000000L, Constants.TV_GOLD, '$',	/* 399 */
	    0,	   3,	1,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("copper"			,0x00000000L, Constants.TV_GOLD, '$',
	    0,	   4,	2,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("copper"			,0x00000000L, Constants.TV_GOLD, '$',
	    0,	   5,	3,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("silver"			,0x00000000L, Constants.TV_GOLD, '$',
	    0,	   6,	4,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("silver"			,0x00000000L, Constants.TV_GOLD, '$',
	    0,	   7,	5,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("silver"			,0x00000000L, Constants.TV_GOLD, '$',
	    0,	   8,	6,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("garnets"			,0x00000000L, Constants.TV_GOLD, '*',
	    0,	   9,	7,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("garnets"			,0x00000000L, Constants.TV_GOLD, '*',
	    0,	  10,	8,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("gold"				,0x00000000L, Constants.TV_GOLD, '$',
	    0,	  12,	9,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("gold"				,0x00000000L, Constants.TV_GOLD, '$',
	    0,	  14,	10,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("gold"				,0x00000000L, Constants.TV_GOLD, '$',
	    0,	  16,	11,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("opals"			,0x00000000L, Constants.TV_GOLD, '*',
	    0,	  18,	12,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("sapphires"			,0x00000000L, Constants.TV_GOLD, '*',
	    0,	  20,	13,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("gold"				,0x00000000L, Constants.TV_GOLD, '$',
	    0,	  24,	14,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("rubies"			,0x00000000L, Constants.TV_GOLD, '*',
	    0,	  28,	15,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("diamonds"			,0x00000000L, Constants.TV_GOLD, '*',
	    0,	  32,	16,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("emeralds"			,0x00000000L, Constants.TV_GOLD, '*',
	    0,	  40,	17,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	new TreasureType("mithril"			,0x00000000L, Constants.TV_GOLD, '$',	/* 416 */
	    0,	  80,	18,   1,   0,	0,   0,	 0,   0, new int[] {0,0}	  ,  1),
	/* nothing, used as inventory place holder */
	/* must be stackable, so that can be picked up by inven_carry */
	new TreasureType("nothing"			,0x00000000L,  Constants.TV_NOTHING, ' ',	/* 417 */
	   0,       0,  64,   0,   0,   0,   0,   0,   0, new int[] {0,0}    ,  0),
	/* these next two are needed only for the names */
	new TreasureType("& ruined chest"		,0x00000000L,   Constants.TV_CHEST, '&',	/* 418 */
	   0,	   0,	0,   1, 250,	0,   0,	 0,   0, new int[] {0,0}	,  0),
	new TreasureType(""				,0x00000000L,  Constants.TV_NOTHING, ' ',	/* 419 */
	   0,       0,   0,   0,   0,   0,   0,   0,   0, new int[] {0,0}  ,  0)
	};
	
	//char *special_names[SN_ARRAY_SIZE]
	public static String[] special_names = {
		"",						"(R)",				"(RA)",
		"(RF)",					"(RC)",				"(RL)",
		"(HA)",					"(DF)",				"(SA)",
		"(SD)",					"(SE)",				"(SU)",
		"(FT)",					"(FB)",				"of Free Action",
		"of Slaying",			"of Clumsiness",	"of Weakness",
		"of Slow Descent",		"of Speed",			"of Stealth",
		"of Slowness",			"of Noise",			"of Great Mass",
		"of Intelligence",		"of Wisdom",		"of Infra-Vision",
		"of Might",				"of Lordliness",	"of the Magi",
		"of Beauty",			"of Seeing",		"of Regeneration",
		"of Stupidity",			"of Dullness",		"of Blindness",
		"of Timidness",			"of Teleportation",	"of Ugliness",
		"of Protection",		"of Irritation",	"of Vulnerability",
		"of Enveloping",		"of Fire",			"of Slay Evil",
		"of Dragon Slaying",	"(Empty)",			"(Locked)",
		"(Poison Needle)",		"(Gas Trap)",		"(Explosion Device)",
		"(Summoning Runes)",	"(Multiple Traps)",	"(Disarmed)",
		"(Unlocked)",			"of Slay Animal"
	};
	
	/* Pairing things down for THINK C.  */
	public static int[] sorted_objects = new int[Constants.MAX_DUNGEON_OBJ];
	
	/* Identified objects flags					*/
	public static int[] object_ident = new int[Constants.OBJECT_IDENT_SIZE];
	public static int[] t_level = new int[Constants.MAX_OBJ_LEVEL + 1];
	public static InvenType[] t_list = new InvenType[Constants.MAX_TALLOC];
	public static InvenType[] inventory = new InvenType[Constants.INVEN_ARRAY_SIZE];
	
	/* Treasure related values					*/
	public static int inven_ctr = 0;		/* Total different obj's	*/
	public static int inven_weight = 0;	/* Cur carried weight	*/
	public static int equip_ctr = 0;		/* Cur equipment ctr	*/
	public static int tcptr;				/* Cur treasure heap ptr	*/
	
	static {
		int i;
		
		for (i = 0; i < Constants.MAX_TALLOC; i++) {
			t_list[i] = new InvenType();
		}
		for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
			inventory[i] = new InvenType();
		}
	}
	
}
