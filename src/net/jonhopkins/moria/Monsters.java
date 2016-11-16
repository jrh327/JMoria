/*
 * Monsters.java: monster definitions
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

import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.MonsterAttackType;
import net.jonhopkins.moria.types.MonsterType;

public class Monsters {
	/* Following are creature arrays and variables			*/
		/* Creatures must be defined here				*/
		/*	See types.h under creature_type for a complete list
			of all variables for creatures.	 Some of the less obvious
			are explained below.
		
		Hit points:	#1, #2: where #2 is the range of each roll and
				#1 is the number of added up rolls to make.
				Example: a creature with 5 eight-sided hit die
				is given {5,8}.
			
			Attack types:
			1	Normal attack
			2	Poison Strength
			3	Confusion attack
			4	Fear attack
			5	Fire attack
			6	Acid attack
			7	Cold attack
			8	Lightning attack
			9	Corrosion attack
			10	Blindness attack
			11	Paralysis attack
			12	Steal Money
			13	Steal Object
			14	Poison
			15	Lose dexterity
			16	Lose constitution
			17	Lose intelligence
			18	Lose wisdom
			19	Lose experience
			20	Aggravation
			21	Disenchants
			22	Eats food
			23	Eats light
			24	Eats charges
			99	Blank
			
			Attack descriptions:
			1	hits you.
			2	bites you.
			3	claws you.
			4	stings you.
			5	touches you.
			6	kicks you.
			7	gazes at you.
			8	breathes on you.
			9	spits on you.
			10	makes a horrible wail.
			11	embraces you.
			12	crawls on you.
			13	releases a cloud of spores.
			14	begs you for money.
			15	You've been slimed.
			16	crushes you.
			17	tramples you.
			18	drools on you.
			19	insults you.
			99	is repelled.
			
		Example:  For a creature which bites for 1d6, then stings for
			  2d4 and loss of dex you would use:
			 new M_attack_type(1,2,1,6},{15,4,2,4}
			
			CMOVE flags:
	Movement.	00000001	Move only to attack
		.	00000002	Move, attack normal
		.	00000008	20% random movement
		.	00000010	40% random movement
		.	00000020	75% random movement
	Special +	00010000	Invisible movement
		+	00020000	Move through door
		+	00040000	Move through wall
		+	00080000	Move through creatures
		+	00100000	Picks up objects
		+	00200000	Multiply monster
	Carries =	01000000	Carries objects.
		=	02000000	Carries gold.
		=	04000000	Has 60% of time.
		=	08000000	Has 90% of time.
		=	10000000	1d2 objects/gold.
		=	20000000	2d2 objects/gold.
		=	40000000	4d2 objects/gold.
	Special ~	80000000	Win-the-Game creature.
	
			SPELL Flags:
	Frequency	000001	  1	These add up to x.  Then
	(1 in x).	000002	  2	if RANDINT(X) = 1 the
		.	000004	  4	creature casts a spell.
		.	000008	  8
	Spells	=	000010	Teleport short (blink)
		=	000020	Teleport long
		=	000040	Teleport player to monster
		=	000080	Cause light wound
		=	000100	Cause serious wound
		=	000200	Hold person (Paralysis)
		=	000400	Cause blindness
		=	000800	Cause confusion
		=	001000	Cause fear
		=	002000	Summon monster
		=	004000	Summon undead
		=	008000	Slow Person
		=	010000	Drain Mana
		=	020000	Not Used
		=	040000	Not Used
	Breath/ +	080000	Breathe/Resist Lightning
	Resist	+	100000	Breathe/Resist Gas
		+	200000	Breathe/Resist Acid
		+	400000	Breathe/Resist Frost
		+	800000	Breathe/Resist Fire
	
			CDEFENSE flags:
			0001	Hurt by Slay Dragon.
			0002	Hurt by Slay Animal.
			0004	Hurt by Slay Evil.
			0008	Hurt by Slay Undead.
			0010	Hurt by Frost.
			0020	Hurt by Fire.
			0040	Hurt by Poison.
			0080	Hurt by Acid.
			0100	Hurt by Light-Wand.
			0200	Hurt by Stone-to-Mud.
			0400	Not used.
			0800	Not used.
			1000	Cannot be charmed or slept.
			2000	Can be seen with infra-vision.
			4000	Max Hit points.
			8000	Not used.
			
			
		Sleep (sleep)	:	A measure in turns of how fast creature
					will notice player (on the average).
		Area of affect (aaf) :	Max range that creature is able to "notice"
					the player.
										*/
	
	//Creature_type[MAX_CREATURES]
	public static CreatureType[] creatureList = { //Name cmove,	    spells,defense,  exp, slp,aaf, ac,spd,char,	          hit-die,                      damage, lvl
		new CreatureType("Filthy Street Urchin",	0x0012000A, 0x00000000, 0x2034,    0,  40,  4,  1, 11, 'p', new int[] { 1, 4}, new int[] { 72,148,  0,  0},  0),
		new CreatureType("Blubbering Idiot",		0x0012000A, 0x00000000, 0x2030,    0,   0,  6,  1, 11, 'p', new int[] { 1, 2}, new int[] { 79,  0,  0,  0},  0),
		new CreatureType("Pitiful-Looking Beggar",	0x0012000A, 0x00000000, 0x2030,    0,  40, 10,  1, 11, 'p', new int[] { 1, 4}, new int[] { 72,  0,  0,  0},  0),
		new CreatureType("Mangy-Looking Leper",		0x0012000A, 0x00000000, 0x2030,    0,  50, 10,  1, 11, 'p', new int[] { 1, 1}, new int[] { 72,  0,  0,  0},  0),
		new CreatureType("Squint-Eyed Rogue",		0x07120002, 0x00000000, 0x2034,    0,  99, 10,  8, 11, 'p', new int[] { 2, 8}, new int[] {  5,149,  0,  0},  0),
		new CreatureType("Singing, Happy Drunk",	0x06120038, 0x00000000, 0x2030,    0,   0, 10,  1, 11, 'p', new int[] { 2, 3}, new int[] { 72,  0,  0,  0},  0),
		new CreatureType("Mean-Looking Mercenary",	0x0B12000A, 0x00000000, 0x2034,    0, 250, 10, 20, 11, 'p', new int[] { 5, 8}, new int[] {  9,  0,  0,  0},  0),
		new CreatureType("Battle-Scarred Veteran",	0x0B12000A, 0x00000000, 0x2030,    0, 250, 10, 30, 11, 'p', new int[] { 7, 8}, new int[] { 15,  0,  0,  0},  0),
		new CreatureType("Grey Mushroom patch",		0x00000001, 0x00000000, 0x10A0,    1,   0,  2,  1, 11, ',', new int[] { 1, 2}, new int[] { 91,  0,  0,  0},  1),
		new CreatureType("Giant Yellow Centipede", 	0x00000002, 0x00000000, 0x0002,    2,  30,  8, 12, 11, 'c', new int[] { 2, 6}, new int[] { 26, 60,  0,  0},  1),
		new CreatureType("Giant White Centipede",	0x0000000A, 0x00000000, 0x0002,    2,  40,  7, 10, 11, 'c', new int[] { 3, 5}, new int[] { 25, 59,  0,  0},  1),
		new CreatureType("White Icky-Thing",		0x00000012, 0x00000000, 0x0020,    2,  10, 12,  7, 11, 'i', new int[] { 3, 5}, new int[] { 63,  0,  0,  0},  1),
		new CreatureType("Clear Icky-Thing",		0x00010012, 0x00000000, 0x0020,    1,  10, 12,  6, 11, 'i', new int[] { 2, 5}, new int[] { 63,  0,  0,  0},  1),
		new CreatureType("Giant White Mouse",		0x0020000A, 0x00000000, 0x2072,    1,  20,  8,  4, 11, 'r', new int[] { 1, 3}, new int[] { 25,  0,  0,  0},  1),
		new CreatureType("Large Brown Snake",		0x0000000A, 0x00000000, 0x00B2,    3,  99,  4, 35, 10, 'R', new int[] { 4, 6}, new int[] { 26, 73,  0,  0},  1),
		new CreatureType("Large White Snake",		0x00000012, 0x00000000, 0x00B2,    2,  99,  4, 30, 11, 'R', new int[] { 3, 6}, new int[] { 24,  0,  0,  0},  1),
		new CreatureType("Kobold",					0x07020002, 0x00000000, 0x2030,    5,  10, 20, 16, 11, 'k', new int[] { 3, 7}, new int[] {  5,  0,  0,  0},  1),
		new CreatureType("White Worm mass",			0x00200022, 0x00000000, 0x01B2,    2,  10,  7,  1, 10, 'w', new int[] { 4, 4}, new int[] {173,  0,  0,  0},  1),
		new CreatureType("Floating Eye",			0x00000001, 0x0001000D, 0x2100,    1,  10,  2,  6, 11, 'e', new int[] { 3, 6}, new int[] {146,  0,  0,  0},  1),
		new CreatureType("Shrieker Mushroom patch",	0x00000001, 0x00000000, 0x10A0,    1,   0,  2,  1, 11, ',', new int[] { 1, 1}, new int[] {203,  0,  0,  0},  2),
		new CreatureType("Blubbering Icky-Thing",	0x0B180012, 0x00000000, 0x0020,    8,  10, 14,  4, 11, 'i', new int[] { 5, 8}, new int[] {174,210,  0,  0},  2),
		new CreatureType("Metallic Green Centipede",0x00000012, 0x00000000, 0x0002,    3,  10,  5,  4, 12, 'c', new int[] { 4, 4}, new int[] { 68,  0,  0,  0},  2),
		new CreatureType("Novice Warrior",			0x07020002, 0x00000000, 0x2030,    6,   5, 20, 16, 11, 'p', new int[] { 9, 4}, new int[] {  6,  0,  0,  0},  2),
		new CreatureType("Novice Rogue",			0x07020002, 0x00000000, 0x2034,    6,   5, 20, 12, 11, 'p', new int[] { 8, 4}, new int[] {  5,148,  0,  0},  2),
		new CreatureType("Novice Priest",			0x07020002, 0x0000108C, 0x2030,    7,   5, 20, 10, 11, 'p', new int[] { 7, 4}, new int[] {  4,  0,  0,  0},  2),
		new CreatureType("Novice Mage",				0x07020002, 0x0000089C, 0x2030,    7,   5, 20,  6, 11, 'p', new int[] { 6, 4}, new int[] {  3,  0,  0,  0},  2),
		new CreatureType("Yellow Mushroom patch",	0x00000001, 0x00000000, 0x10A0,    2,   0,  2,  1, 11, ',', new int[] { 1, 1}, new int[] {100,  0,  0,  0},  2),
		new CreatureType("White Jelly",				0x00000001, 0x00000000, 0x11A0,   10,  99,  2,  1, 12, 'J', new int[] { 8, 8}, new int[] {168,  0,  0,  0},  2),
		new CreatureType("Giant Green Frog",		0x0000000A, 0x00000000, 0x00A2,    6,  30, 12,  8, 11, 'f', new int[] { 2, 8}, new int[] { 26,  0,  0,  0},  2),
		new CreatureType("Giant Black Ant",			0x0000000A, 0x00000000, 0x0002,    8,  80,  8, 20, 11, 'a', new int[] { 3, 6}, new int[] { 27,  0,  0,  0},  2),
		new CreatureType("White Harpy",				0x00000012, 0x00000000, 0x2034,    5,  10, 16, 17, 11, 'h', new int[] { 2, 5}, new int[] { 49, 49, 25,  0},  2),
		new CreatureType("Blue Yeek",				0x07020002, 0x00000000, 0x2030,    4,  10, 18, 14, 11, 'y', new int[] { 2, 6}, new int[] {  4,  0,  0,  0},  2),
		new CreatureType("Green Worm mass",			0x00200022, 0x00000000, 0x0132,    3,  10,  7,  3, 10, 'w', new int[] { 6, 4}, new int[] {140,  0,  0,  0},  2),
		new CreatureType("Large Black Snake",		0x0000000A, 0x00000000, 0x00B2,    9,  75,  5, 38, 10, 'R', new int[] { 4, 8}, new int[] { 27, 74,  0,  0},  2),
		new CreatureType("Poltergeist",				0x0F15003A, 0x0000001F, 0x110C,    6,  10,  8, 15, 13, 'G', new int[] { 2, 5}, new int[] { 93,  0,  0,  0},  3),
		new CreatureType("Metallic Blue Centipede",	0x00000012, 0x00000000, 0x0002,    7,  15,  6,  6, 12, 'c', new int[] { 4, 5}, new int[] { 69,  0,  0,  0},  3),
		new CreatureType("Giant White Louse",		0x00200022, 0x00000000, 0x01F2,    1,  10,  6,  5, 12, 'l', new int[] { 1, 1}, new int[] { 24,  0,  0,  0},  3),
		new CreatureType("Black Naga",				0x0710000A, 0x00000000, 0x20E4,   20, 120, 16, 40, 11, 'n', new int[] { 6, 8}, new int[] { 75,  0,  0,  0},  3),
		new CreatureType("Spotted Mushroom patch",	0x00000001, 0x00000000, 0x10A0,    3,   0,  2,  1, 11, ',', new int[] { 1, 1}, new int[] {175,  0,  0,  0},  3),
		new CreatureType("Yellow Jelly",			0x00000001, 0x0001000F, 0x11A0,   12,  99,  2,  1, 12, 'J', new int[] {10, 8}, new int[] {169,  0,  0,  0},  3),
		new CreatureType("Scruffy-Looking Hobbit",	0x07020002, 0x00000000, 0x2034,    4,  10, 16,  8, 11, 'p', new int[] { 3, 5}, new int[] {  3,148,  0,  0},  3),
		new CreatureType("Huge Brown Bat",			0x00000022, 0x00000000, 0x2162,    4,  40,  8, 12, 13, 'b', new int[] { 2, 6}, new int[] { 25,  0,  0,  0},  3),
		new CreatureType("Giant White Ant",			0x00000002, 0x00000000, 0x0002,    7,  80,  8, 16, 11, 'a', new int[] { 3, 6}, new int[] { 27,  0,  0,  0},  3),
		new CreatureType("Yellow Mold",				0x00000001, 0x00000000, 0x10A0,    9,  99,  2, 10, 11, 'm', new int[] { 8, 8}, new int[] {  3,  0,  0,  0},  3),
		new CreatureType("Metallic Red Centipede",	0x0000000A, 0x00000000, 0x0002,   12,  20,  8,  9, 12, 'c', new int[] { 4, 8}, new int[] { 69,  0,  0,  0},  3),
		new CreatureType("Yellow Worm mass",		0x00200022, 0x00000000, 0x01B2,    4,  10,  7,  4, 10, 'w', new int[] { 4, 8}, new int[] {182,  0,  0,  0},  3),
		new CreatureType("Large Green Snake",		0x0000000A, 0x00000000, 0x00B2,   10,  70,  5, 40, 10, 'R', new int[] { 6, 8}, new int[] { 27, 74,  0,  0},  3),
		new CreatureType("Radiation Eye",			0x00000001, 0x0001000B, 0x2100,    6,  10,  2,  6, 11, 'e', new int[] { 3, 6}, new int[] { 88,  0,  0,  0},  3),
		new CreatureType("Drooling Harpy",			0x00000012, 0x00000000, 0x2034,    7,  10, 16, 22, 11, 'h', new int[] { 2, 8}, new int[] { 49, 49, 25, 79},  3),
		new CreatureType("Silver Mouse",			0x0020000A, 0x00000000, 0x0072,    1,  10,  8,  5, 11, 'r', new int[] { 1, 1}, new int[] {212,  0,  0,  0},  4),
		new CreatureType("Black Mushroom patch",	0x00000001, 0x00000000, 0x10A0,    8,   0,  2,  1, 11, ',', new int[] { 8, 8}, new int[] { 71,  0,  0,  0},  4),
		new CreatureType("Blue Jelly",				0x00000001, 0x00000000, 0x11A0,   14,  99,  2,  1, 11, 'J', new int[] {12, 8}, new int[] {125,  0,  0,  0},  4),
		new CreatureType("Creeping Copper Coins",	0x12000002, 0x00000000, 0x1000,    9,  10,  3, 24, 10, '$', new int[] { 7, 8}, new int[] {  3,170,  0,  0},  4),
		new CreatureType("Giant White Rat",			0x0020000A, 0x00000000, 0x2072,    1,  30,  8,  7, 11, 'r', new int[] { 2, 2}, new int[] {153,  0,  0,  0},  4),
		new CreatureType("Giant Black Centipede",	0x0000000A, 0x00000000, 0x0002,   11,  30,  8, 20, 11, 'c', new int[] { 5, 8}, new int[] { 25, 59,  0,  0},  4),
		new CreatureType("Giant Blue Centipede",	0x00000002, 0x00000000, 0x0002,   10,  50,  8, 20, 11, 'c', new int[] { 4, 8}, new int[] { 26, 61,  0,  0},  4),
		new CreatureType("Blue Worm mass",			0x00200022, 0x00000000, 0x01A2,    5,  10,  7, 12, 10, 'w', new int[] { 5, 8}, new int[] {129,  0,  0,  0},  4),
		new CreatureType("Large Grey Snake",		0x0000000A, 0x00000000, 0x00B2,   14,  50,  6, 41, 10, 'R', new int[] { 6, 8}, new int[] { 28, 75,  0,  0},  4),
		new CreatureType("Jackal",					0x00000012, 0x00000000, 0x2032,    8,  30, 12, 16, 11, 'j', new int[] { 3 ,8}, new int[] { 29,  0,  0,  0},  4),
		new CreatureType("Green Naga",				0x0710000A, 0x00000000, 0x2064,   30, 120, 18, 40, 11, 'n', new int[] { 9, 8}, new int[] { 75,118,  0,  0},  5),
		new CreatureType("Green Glutton Ghost",		0x0F150032, 0x0000003F, 0x110C,   15,  10, 10, 20, 13, 'G', new int[] { 3, 6}, new int[] {211,  0,  0,  0},  5),
		new CreatureType("White Mushroom patch",	0x00000001, 0x00000000, 0x10A0,    5,   0,  2,  1, 11, ',', new int[] { 1, 1}, new int[] {147,  0,  0,  0},  5),
		new CreatureType("Green Jelly",				0x00000001, 0x00000000, 0x1120,   18,  99,  2,  1, 12, 'J', new int[] {22, 8}, new int[] {136,  0,  0,  0},  5),
		new CreatureType("Skeleton Kobold",			0x00020002, 0x00000000, 0x100C,   12,  40, 20, 26, 11, 's', new int[] { 5, 8}, new int[] {  5,  0,  0,  0},  5),
		new CreatureType("Silver Jelly",			0x00000001, 0x00000000, 0x10A0,   15,  40,  2, 25, 11, 'J', new int[] {20, 8}, new int[] {213,  0,  0,  0},  5),
		new CreatureType("Giant Black Frog",		0x0000000A, 0x00000000, 0x00A2,   12,  40, 12, 18, 11, 'f', new int[] { 4, 8}, new int[] { 29,  0,  0,  0},  5),
		new CreatureType("Grey Icky-Thing",			0x00000012, 0x00000000, 0x0020,   10,  15, 14, 12, 11, 'i', new int[] { 4, 8}, new int[] { 66,  0,  0,  0},  5),
		new CreatureType("Disenchanter Eye",		0x00000001, 0x00010009, 0x2100,   20,  10,  2, 10, 10, 'e', new int[] { 7, 8}, new int[] {207,  0,  0,  0},  5),
		new CreatureType("Black Yeek",				0x07020002, 0x00000000, 0x2030,    8,  10, 18, 16, 11, 'y', new int[] { 2, 8}, new int[] {  4,  0,  0,  0},  5),
		new CreatureType("Red Worm mass",			0x00200022, 0x00000000, 0x2192,    6,  10,  7, 12, 10, 'w', new int[] { 5, 8}, new int[] {111,  0,  0,  0},  5),
		new CreatureType("Giant House Fly",			0x00000022, 0x00000000, 0x0062,   10,  20, 12, 16, 13, 'F', new int[] { 3, 8}, new int[] { 25,  0,  0,  0},  5),
		new CreatureType("Copperhead Snake",		0x00000012, 0x00000000, 0x00B2,   15,   1,  6, 20, 11, 'R', new int[] { 4, 6}, new int[] {158,  0,  0,  0},  5),
		new CreatureType("Rot Jelly",				0x00000001, 0x00000000, 0x10A0,   15,  99,  2, 30, 11, 'J', new int[] {20, 8}, new int[] {209,  0,  0,  0},  5),
		new CreatureType("Purple Mushroom patch",	0x00000001, 0x00000000, 0x10A0,   12,   0,  2,  1, 12, ',', new int[] { 1, 1}, new int[] {183,  0,  0,  0},  6),
		new CreatureType("Brown Mold",				0x00000001, 0x00000000, 0x10A0,   20,  99,  2, 12, 11, 'm', new int[] {15, 8}, new int[] { 89,  0,  0,  0},  6),
		new CreatureType("Giant Brown Bat",			0x0000001A, 0x00000000, 0x2162,   10,  30, 10, 15, 13, 'b', new int[] { 3, 8}, new int[] { 26,  0,  0,  0},  6),
		new CreatureType("Creeping Silver Coins",	0x16000002, 0x00000000, 0x1000,   18,  10,  4, 30, 10, '$', new int[] {12, 8}, new int[] {  5,171,  0,  0},  6),
		new CreatureType("Orc",						0x0B020002, 0x00000000, 0x2034,   16,  30, 20, 32, 11, 'o', new int[] { 9, 8}, new int[] {  7,  0,  0,  0},  6),
		new CreatureType("Grey Harpy",				0x00000012, 0x00000000, 0x2034,   14,  10, 16, 20, 12, 'h', new int[] { 3, 8}, new int[] { 50, 50, 25,  0},  6),
		new CreatureType("Blue Icky-Thing",			0x00000012, 0x00000000, 0x0020,   12,  20, 14, 14, 11, 'i', new int[] { 4, 8}, new int[] {126,  0,  0,  0},  6),
		new CreatureType("Rattlesnake",				0x00000012, 0x00000000, 0x00B2,   20,   1,  6, 24, 11, 'R', new int[] { 6, 7}, new int[] {159,  0,  0,  0},  6),
		new CreatureType("Bloodshot Eye",			0x00000001, 0x00010007, 0x2100,   15,  10,  2,  6, 11, 'e', new int[] { 4, 8}, new int[] {143,  0,  0,  0},  7),
		new CreatureType("Red Naga",				0x0710000A, 0x00000000, 0x20E4,   40, 120, 20, 40, 11, 'n', new int[] {11, 8}, new int[] { 76, 82,  0,  0},  7),
		new CreatureType("Red Jelly",				0x00000001, 0x00000000, 0x11A0,   26,  99,  2,  1, 11, 'J', new int[] {26, 8}, new int[] { 87,  0,  0,  0},  7),
		new CreatureType("Giant Red Frog",			0x0000000A, 0x00000000, 0x00A2,   16,  50, 12, 16, 11, 'f', new int[] { 5, 8}, new int[] { 83,  0,  0,  0},  7),
		new CreatureType("Green Icky-Thing",		0x00000012, 0x00000000, 0x0020,   18,  20, 14, 12, 11, 'i', new int[] { 5, 8}, new int[] {137,  0,  0,  0},  7),
		new CreatureType("Zombie Kobold",			0x00020002, 0x00000000, 0x102C,   14,  30, 20, 14, 11, 'z', new int[] { 6, 8}, new int[] {  1,  1,  0,  0},  7),
		new CreatureType("Lost Soul",				0x0F15001A, 0x0001002F, 0x110C,   18,  10, 12, 10, 11, 'G', new int[] { 2, 8}, new int[] { 11,185,  0,  0},  7),
		new CreatureType("Greedy Little Gnome",		0x0B020002, 0x00000000, 0x2034,   13,  10, 18, 14, 11, 'p', new int[] { 3, 8}, new int[] {  6,149,  0,  0},  7),
		new CreatureType("Giant Green Fly",			0x00000022, 0x00000000, 0x0062,   15,  20, 12, 14, 12, 'F', new int[] { 3, 8}, new int[] { 27,  0,  0,  0},  7),
		new CreatureType("Brown Yeek",				0x07020002, 0x00000000, 0x2030,   11,  10, 18, 18, 11, 'y', new int[] { 3, 8}, new int[] {  5,  0,  0,  0},  8),
		new CreatureType("Green Mold",				0x00000001, 0x00000000, 0x10A0,   28,  75,  2, 14, 11, 'm', new int[] {21, 8}, new int[] { 94,  0,  0,  0},  8),
		new CreatureType("Skeleton Orc",			0x00020002, 0x00000000, 0x100C,   26,  40, 20, 36, 11, 's', new int[] {10, 8}, new int[] { 14,  0,  0,  0},  8),
		new CreatureType("Seedy Looking Human",		0x13020002, 0x00000000, 0x2034,   22,  20, 20, 26, 11, 'p', new int[] { 8, 8}, new int[] { 17,  0,  0,  0},  8),
		new CreatureType("Red Icky-Thing",			0x00000012, 0x00000000, 0x0020,   22,  20, 14, 18, 12, 'i', new int[] { 4, 8}, new int[] { 64,117,  0,  0},  8),
		new CreatureType("Bandit",					0x13020002, 0x00000000, 0x2034,   26,  10, 20, 24, 11, 'p', new int[] { 8, 8}, new int[] { 13,148,  0,  0},  8),
		new CreatureType("Yeti",					0x00020002, 0x00000000, 0x2024,   30,  10, 20, 24, 11, 'Y', new int[] {11, 8}, new int[] { 51, 51, 27,  0},  9),
		new CreatureType("Bloodshot Icky-Thing",	0x00000012, 0x0001000B, 0x0020,   24,  20, 14, 18, 11, 'i', new int[] { 7, 8}, new int[] { 65,139,  0,  0},  9),
		new CreatureType("Giant Grey Rat",			0x0020000A, 0x00000000, 0x2072,    2,  20,  8, 12, 11, 'r', new int[] { 2, 3}, new int[] {154,  0,  0,  0},  9),
		new CreatureType("Black Harpy",				0x0000000A, 0x00000000, 0x2034,   19,  10, 16, 22, 12, 'h', new int[] { 3, 8}, new int[] { 50, 50, 26,  0},  9),
		new CreatureType("Giant Black Bat",			0x00000012, 0x00000000, 0x2162,   16,  25, 12, 18, 13, 'b', new int[] { 2, 8}, new int[] { 29,  0,  0,  0},  9),
		new CreatureType("Clear Yeek",				0x07030002, 0x00000000, 0x0030,   14,  10, 18, 24, 11, 'y', new int[] { 3, 6}, new int[] {  4,  0,  0,  0},  9),
		new CreatureType("Orc Shaman",				0x0B020002, 0x00008085, 0x2034,   30,  20, 20, 15, 11, 'o', new int[] { 7, 8}, new int[] {  5,  0,  0,  0},  9),
		new CreatureType("Giant Red Ant",			0x00000002, 0x00000000, 0x0002,   22,  60, 12, 34, 11, 'a', new int[] { 4, 8}, new int[] { 27, 85,  0,  0},  9),
		new CreatureType("King Cobra",				0x00000012, 0x00000000, 0x00B2,   28,   1,  8, 30, 11, 'R', new int[] { 8, 8}, new int[] {144,161,  0,  0},  9),
		new CreatureType("Clear Mushroom patch",	0x00210001, 0x00000000, 0x10A0,    1,   0,  4,  1, 12, ',', new int[] { 1, 1}, new int[] { 70,  0,  0,  0}, 10),
		new CreatureType("Giant White Tick",		0x0000000A, 0x00000000, 0x0022,   27,  20, 12, 40, 10, 't', new int[] {15, 8}, new int[] {160,  0,  0,  0}, 10),
		new CreatureType("Hairy Mold",				0x00000001, 0x00000000, 0x10A0,   32,  70,  2, 15, 11, 'm', new int[] {15, 8}, new int[] {151,  0,  0,  0}, 10),
		new CreatureType("Disenchanter Mold",		0x00000001, 0x0001000B, 0x10A0,   40,  70,  2, 20, 11, 'm', new int[] {16, 8}, new int[] {206,  0,  0,  0}, 10),
		new CreatureType("Giant Red Centipede",		0x00000002, 0x00000000, 0x0002,   24,  50, 12, 26, 12, 'c', new int[] { 3, 8}, new int[] { 25,164,  0,  0}, 10),
		new CreatureType("Creeping Gold Coins",		0x1A000002, 0x00000000, 0x1000,   32,  10,  5, 36, 10, '$', new int[] {18, 8}, new int[] { 14,172,  0,  0}, 10),
		new CreatureType("Giant Fruit Fly",			0x00200022, 0x00000000, 0x0062,    4,  10,  8, 14, 12, 'F', new int[] { 2, 2}, new int[] { 25,  0,  0,  0}, 10),
		new CreatureType("Brigand",					0x13020002, 0x00000000, 0x2034,   35,  10, 20, 32, 11, 'p', new int[] { 9, 8}, new int[] { 13,149,  0,  0}, 10),
		new CreatureType("Orc Zombie",				0x00020002, 0x00000000, 0x102C,   30,  25, 20, 24, 11, 'z', new int[] {11, 8}, new int[] {  3,  3,  0,  0}, 11),
		new CreatureType("Orc Warrior",				0x0B020002, 0x00000000, 0x2034,   34,  25, 20, 36, 11, 'o', new int[] {11, 8}, new int[] { 15,  0,  0,  0}, 11),
		new CreatureType("Vorpal Bunny",			0x0020000A, 0x00000000, 0x2072,    2,  30,  8, 10, 12, 'r', new int[] { 2, 3}, new int[] { 28,  0,  0,  0}, 11),
		new CreatureType("Nasty Little Gnome",		0x13020002, 0x000020B5, 0x2034,   32,  10, 18, 10, 11, 'p', new int[] { 4, 8}, new int[] {  4,  0,  0,  0}, 11),
		new CreatureType("Hobgoblin",				0x0F020002, 0x00000000, 0x2034,   38,  30, 20, 38, 11, 'H', new int[] {12, 8}, new int[] {  9,  0,  0,  0}, 11),
		new CreatureType("Black Mamba",				0x00000012, 0x00000000, 0x00B2,   40,   1, 10, 32, 12, 'R', new int[] {10, 8}, new int[] {163,  0,  0,  0}, 12),
		new CreatureType("Grape Jelly",				0x00000001, 0x0001000B, 0x11A0,   60,  99,  2,  1, 11, 'J', new int[] {52, 8}, new int[] {186,  0,  0,  0}, 12),
		new CreatureType("Master Yeek",				0x07020002, 0x00008018, 0x2030,   28,  10, 18, 24, 11, 'y', new int[] { 5, 8}, new int[] {  7,  0,  0,  0}, 12),
		new CreatureType("Priest",					0x13020002, 0x00000285, 0x2030,   36,  40, 20, 22, 11, 'p', new int[] { 7, 8}, new int[] { 12,  0,  0,  0}, 12),
		new CreatureType("Giant Clear Ant",			0x00010002, 0x00000000, 0x0002,   24,  60, 12, 18, 11, 'a', new int[] { 3, 7}, new int[] { 27,  0,  0,  0}, 12),
		new CreatureType("Air Spirit",				0x00030022, 0x00000000, 0x1000,   40,  20, 12, 20, 13, 'E', new int[] { 5, 8}, new int[] {  2,  0,  0,  0}, 12),
		new CreatureType("Skeleton Human",			0x00020002, 0x00000000, 0x100C,   38,  30, 20, 30, 11, 's', new int[] {12, 8}, new int[] {  7,  0,  0,  0}, 12),
		new CreatureType("Human Zombie",			0x00020002, 0x00000000, 0x102C,   34,  20, 20, 24, 11, 'z', new int[] {11, 8}, new int[] {  3,  3,  0,  0}, 12),
		new CreatureType("Moaning Spirit",			0x0F15000A, 0x0001002F, 0x110C,   44,  10, 14, 20, 11, 'G', new int[] { 4, 8}, new int[] { 99,178,  0,  0}, 12),
		new CreatureType("Swordsman",				0x13020002, 0x00000000, 0x2030,   40,  20, 20, 34, 11, 'p', new int[] {11, 8}, new int[] { 18,  0,  0,  0}, 12),
		new CreatureType("Killer Brown Beetle",		0x0000000A, 0x00000000, 0x0002,   38,  30, 10, 40, 11, 'K', new int[] {13, 8}, new int[] { 41,  0,  0,  0}, 13),
		new CreatureType("Ogre",					0x07020002, 0x00000000, 0x2034,   42,  30, 20, 32, 11, 'o', new int[] {13, 8}, new int[] { 16,  0,  0,  0}, 13),
		new CreatureType("Giant Red Speckled Frog",	0x0000000A, 0x00000000, 0x00A2,   32,  30, 12, 20, 11, 'f', new int[] { 6, 8}, new int[] { 41,  0,  0,  0}, 13),
		new CreatureType("Magic User",				0x13020002, 0x00002413, 0x2030,   35,  10, 20, 10, 11, 'p', new int[] { 7, 8}, new int[] { 11,  0,  0,  0}, 13),
		new CreatureType("Black Orc",				0x0B020002, 0x00000000, 0x2034,   40,  20, 20, 36, 11, 'o', new int[] {12, 8}, new int[] { 17,  0,  0,  0}, 13),
		new CreatureType("Giant Long-Eared Bat",	0x00000012, 0x00000000, 0x2162,   20,  20, 12, 20, 13, 'b', new int[] { 5, 8}, new int[] { 27, 50, 50,  0}, 13),
		new CreatureType("Giant Gnat",				0x00200022, 0x00000000, 0x0062,    1,  10,  8,  4, 13, 'F', new int[] { 1, 2}, new int[] { 24,  0,  0,  0}, 13),
		new CreatureType("Killer Green Beetle",		0x0000000A, 0x00000000, 0x0002,   46,  30, 12, 45, 11, 'K', new int[] {16, 8}, new int[] { 43,  0,  0,  0}, 14),
		new CreatureType("Giant Flea",				0x00200022, 0x00000000, 0x0062,    1,  10,  8, 25, 12, 'F', new int[] { 2, 2}, new int[] { 25,  0,  0,  0}, 14),
		new CreatureType("Giant White Dragon Fly",	0x00000012, 0x0040000A, 0x0062,   54,  50, 20, 20, 11, 'F', new int[] { 5, 8}, new int[] {122,  0,  0,  0}, 14),
		new CreatureType("Hill Giant",				0x07020002, 0x00000000, 0x2034,   52,  50, 20, 36, 11, 'P', new int[] {16, 8}, new int[] { 19,  0,  0,  0}, 14),
		new CreatureType("Skeleton Hobgoblin",		0x00020002, 0x00000000, 0x100C,   46,  30, 20, 34, 11, 's', new int[] {13, 8}, new int[] { 14,  0,  0,  0}, 14),
		new CreatureType("Flesh Golem",				0x00000002, 0x00000000, 0x1030,   48,  10, 12, 10, 11, 'g', new int[] {12, 8}, new int[] {  5,  5,  0,  0}, 14),
		new CreatureType("White Dragon Bat",		0x00000012, 0x00400004, 0x0162,   40,  50, 12, 20, 13, 'b', new int[] { 2, 6}, new int[] {121,  0,  0,  0}, 14),
		new CreatureType("Giant Black Louse",		0x00200012, 0x00000000, 0x01F2,    1,  10,  6,  7, 12, 'l', new int[] { 1, 1}, new int[] { 25,  0,  0,  0}, 14),
		new CreatureType("Guardian Naga",			0x1710000A, 0x00000000, 0x20E4,   60, 120, 20, 50, 11, 'n', new int[] {24, 8}, new int[] { 77, 31,  0,  0}, 15),
		new CreatureType("Giant Grey Bat",			0x00000012, 0x00000000, 0x2162,   22,  15, 12, 22, 13, 'b', new int[] { 4, 8}, new int[] { 29, 50, 50,  0}, 15),
		new CreatureType("Giant Clear Centipede",	0x00010002, 0x00000000, 0x0002,   30,  30, 12, 30, 11, 'c', new int[] { 5, 8}, new int[] { 34, 62,  0,  0}, 15),
		new CreatureType("Giant Yellow Tick",		0x0000000A, 0x00000000, 0x0022,   48,  20, 12, 48, 10, 't', new int[] {20, 8}, new int[] {162,  0,  0,  0}, 15),
		new CreatureType("Giant Ebony Ant",			0x00200002, 0x00000000, 0x0002,    3,  60, 12, 24, 11, 'a', new int[] { 3, 4}, new int[] { 33,  0,  0,  0}, 15),
		new CreatureType("Frost Giant",				0x07020002, 0x00000000, 0x0024,   54,  50, 20, 38, 11, 'P', new int[] {17, 8}, new int[] {120,  0,  0,  0}, 15),
		new CreatureType("Clay Golem",				0x00000002, 0x00000000, 0x1200,   50,  10, 12, 20, 11, 'g', new int[] {14, 8}, new int[] {  7,  7,  0,  0}, 15),
		new CreatureType("Huge White Bat",			0x00200012, 0x00000000, 0x2162,    3,  40,  7, 12, 12, 'b', new int[] { 3, 8}, new int[] { 29,  0,  0,  0}, 15),
		new CreatureType("Giant Tan Bat",			0x00000012, 0x00000000, 0x2162,   18,  40, 12, 18, 12, 'b', new int[] { 3, 8}, new int[] { 95, 49, 49,  0}, 15),
		new CreatureType("Violet Mold",				0x00000001, 0x00010009, 0x10A0,   50,  70,  2, 15, 11, 'm', new int[] {17, 8}, new int[] {145,  0,  0,  0}, 15),
		new CreatureType("Umber Hulk",				0x00020002, 0x00000000, 0x2124,   75,  10, 20, 20, 11, 'U', new int[] {20, 8}, new int[] { 92,  5,  5, 36}, 16),
		new CreatureType("Gelatinous Cube",			0x2F18000A, 0x00000000, 0x1020,   36,   1, 12, 18, 10, 'C', new int[] {45, 8}, new int[] {115,  0,  0,  0}, 16),
		new CreatureType("Giant Black Rat",			0x0020000A, 0x00000000, 0x2072,    3,  20,  8, 16, 11, 'r', new int[] { 3, 4}, new int[] {155,  0,  0,  0}, 16),
		new CreatureType("Giant Green Dragon Fly",	0x00000012, 0x0010000A, 0x0032,   58,  50, 20, 20, 11, 'F', new int[] { 5, 8}, new int[] {156,  0,  0,  0}, 16),
		new CreatureType("Fire Giant",				0x07020002, 0x00000000, 0x2014,   62,  50, 20, 40, 11, 'P', new int[] {20, 8}, new int[] {102,  0,  0,  0}, 16),
		new CreatureType("Green Dragon Bat",		0x00000012, 0x00100004, 0x2112,   44,  50, 12, 22, 13, 'b', new int[] { 2, 7}, new int[] {153,  0,  0,  0}, 16),
		new CreatureType("Quasit",					0x1103000A, 0x000010FA, 0x1004,   48,  20, 20, 30, 11, 'q', new int[] { 5, 8}, new int[] {176, 51, 51,  0}, 16),
		new CreatureType("Troll",					0x0F020002, 0x00000000, 0x2024,   64,  40, 20, 40, 11, 'T', new int[] {17, 8}, new int[] {  3,  3, 29,  0}, 17),
		new CreatureType("Water Spirit",			0x0000000A, 0x00000000, 0x1020,   58,  40, 12, 28, 12, 'E', new int[] { 8, 8}, new int[] { 13,  0,  0,  0}, 17),
		new CreatureType("Giant Brown Scorpion",	0x0000000A, 0x00000000, 0x0002,   62,  20, 12, 44, 11, 'S', new int[] {11, 8}, new int[] { 34, 86,  0,  0}, 17),
		new CreatureType("Earth Spirit",			0x0016000A, 0x00000000, 0x1200,   64,  50, 10, 40, 11, 'E', new int[] {13, 8}, new int[] {  7,  7,  0,  0}, 17),
		new CreatureType("Fire Spirit",				0x0000000A, 0x00000000, 0x3010,   66,  20, 16, 30, 12, 'E', new int[] {10, 8}, new int[] {101,  0,  0,  0}, 18),
		new CreatureType("Uruk-Hai Orc",			0x0B020002, 0x00000000, 0x2034,   68,  20, 20, 42, 11, 'o', new int[] {14, 8}, new int[] { 18,  0,  0,  0}, 18),
		new CreatureType("Stone Giant",				0x07020002, 0x00000000, 0x2204,   80,  50, 20, 40, 11, 'P', new int[] {22, 8}, new int[] { 20,  0,  0,  0}, 18),
		new CreatureType("Stone Golem",				0x00000002, 0x00000000, 0x1200,  100,  10, 12, 75, 10, 'g', new int[] {28, 8}, new int[] {  9,  9,  0,  0}, 19),
		new CreatureType("Grey Ooze",				0x07180022, 0x00000000, 0x10A0,   40,   1, 15, 10, 11, 'O', new int[] { 6, 8}, new int[] {127,  0,  0,  0}, 19),
		new CreatureType("Disenchanter Ooze",		0x07180022, 0x00000000, 0x10B0,   50,   1, 15, 15, 11, 'O', new int[] { 6, 8}, new int[] {205,  0,  0,  0}, 19),
		new CreatureType("Giant Spotted Rat",		0x0020000A, 0x00000000, 0x2072,    3,  20,  8, 20, 11, 'r', new int[] { 4, 3}, new int[] {155,  0,  0,  0}, 19),
		new CreatureType("Mummified Kobold",		0x0B020002, 0x00000000, 0x102C,   46,  75, 20, 24, 11, 'M', new int[] {13, 8}, new int[] {  5,  5,  0,  0}, 19),
		new CreatureType("Killer Black Beetle",		0x0000000A, 0x00000000, 0x0002,   75,  30, 12, 46, 11, 'K', new int[] {18, 8}, new int[] { 44,  0,  0,  0}, 19),
		new CreatureType("Red Mold",				0x00000001, 0x00000000, 0x3090,   64,  70,  2, 16, 11, 'm', new int[] {17, 8}, new int[] {108,  0,  0,  0}, 19),
		new CreatureType("Quylthulg",				0x00010004, 0x00002017, 0x5000,  200,   0, 10,  1, 11, 'Q', new int[] { 4, 8}, new int[] {  0,  0,  0,  0}, 20),
		new CreatureType("Giant Red Bat",			0x00000012, 0x00000000, 0x2162,   40,  20, 12, 24, 12, 'b', new int[] { 5, 8}, new int[] { 30, 51, 51,  0}, 20),
		new CreatureType("Giant Black Dragon Fly",	0x00000012, 0x00200009, 0x0072,   58,  50, 20, 22, 11, 'F', new int[] { 4, 8}, new int[] {141,  0,  0,  0}, 20),
		new CreatureType("Cloud Giant",				0x07020002, 0x00000000, 0x2034,  125,  50, 20, 44, 11, 'P', new int[] {24, 8}, new int[] {130,  0,  0,  0}, 20),
		new CreatureType("Black Dragon Bat",		0x00000012, 0x00200004, 0x2152,   50,  50, 12, 24, 13, 'b', new int[] { 2, 8}, new int[] {112,  0,  0,  0}, 21),
		new CreatureType("Blue Dragon Bat",			0x00000012, 0x00080004, 0x2052,   54,  50, 12, 26, 13, 'b', new int[] { 3, 6}, new int[] {131,  0,  0,  0}, 21),
		new CreatureType("Mummified Orc",			0x0B020002, 0x00000000, 0x102C,   56,  75, 20, 28, 11, 'M', new int[] {14, 8}, new int[] { 13, 13,  0,  0}, 21),
		new CreatureType("Killer Boring Beetle",	0x0000000A, 0x00000000, 0x0002,   70,  30, 12, 48, 11, 'K', new int[] {18, 8}, new int[] { 44,  0,  0,  0}, 21),
		new CreatureType("Killer Stag Beetle",		0x0000000A, 0x00000000, 0x0002,   80,  30, 12, 50, 11, 'K', new int[] {20, 8}, new int[] { 41, 10,  0,  0}, 22),
		new CreatureType("Black Mold",				0x00000081, 0x00000000, 0x10A0,   68,  50,  2, 18, 11, 'm', new int[] {15, 8}, new int[] { 21,  0,  0,  0}, 22),
		new CreatureType("Iron Golem",				0x00000002, 0x00000000, 0x1080,  160,  10, 12, 99,  9, 'g', new int[] {80, 8}, new int[] { 10, 10,  0,  0}, 22),
		new CreatureType("Giant Yellow Scorpion",	0x0000000A, 0x00000000, 0x0002,   60,  20, 12, 38, 11, 'S', new int[] {12, 8}, new int[] { 31,167,  0,  0}, 22),
		new CreatureType("Green Ooze",				0x073A0012, 0x00000000, 0x1030,    6,   1, 15,  5, 10, 'O', new int[] { 4, 8}, new int[] {116,  0,  0,  0}, 22),
		new CreatureType("Black Ooze",				0x073A0012, 0x0001000B, 0x1030,    7,   1, 10,  6,  9, 'O', new int[] { 6, 8}, new int[] {138,  0,  0,  0}, 23),
		new CreatureType("Warrior",					0x13020002, 0x00000000, 0x2030,   60,  40, 20, 40, 11, 'p', new int[] {15, 8}, new int[] { 18,  0,  0,  0}, 23),
		new CreatureType("Red Dragon Bat",			0x00000012, 0x00800004, 0x2152,   60,  50, 12, 28, 13, 'b', new int[] { 3, 8}, new int[] {105,  0,  0,  0}, 23),
		new CreatureType("Killer Blue Beetle",		0x0000000A, 0x00000000, 0x0002,   85,  30, 14, 50, 11, 'K', new int[] {20, 8}, new int[] { 44,  0,  0,  0}, 23),
		new CreatureType("Giant Silver Ant",		0x0000000A, 0x00000000, 0x0002,   45,  60, 10, 38, 11, 'a', new int[] { 6, 8}, new int[] {114,  0,  0,  0}, 23),
		new CreatureType("Crimson Mold",			0x00000001, 0x00000000, 0x10A0,   65,  50,  2, 18, 11, 'm', new int[] {16, 8}, new int[] {  2, 97,  0,  0}, 23),
		new CreatureType("Forest Wight",			0x0F02000A, 0x0000100F, 0x112C,  140,  30, 20, 30, 11, 'W', new int[] {12, 8}, new int[] {  5,  5,187,  0}, 24),
		new CreatureType("Berzerker",				0x13020002, 0x00000000, 0x2030,   65,  10, 20, 20, 11, 'p', new int[] {15, 8}, new int[] {  7,  7,  0,  0}, 24),
		new CreatureType("Mummified Human",			0x0B020002, 0x00000000, 0x102C,   70,  60, 20, 34, 11, 'M', new int[] {17, 8}, new int[] { 13, 13,  0,  0}, 24),
		new CreatureType("Banshee",					0x0F15001A, 0x0001002F, 0x110C,   60,  10, 20, 24, 12, 'G', new int[] { 6, 8}, new int[] { 99,188,  0,  0}, 24),
		new CreatureType("Giant Troll",				0x0F020002, 0x00000000, 0x2024,   85,  50, 20, 40, 11, 'T', new int[] {19, 8}, new int[] {  5,  5, 41,  0}, 25),
		new CreatureType("Giant Brown Tick",		0x0000000A, 0x00000000, 0x0022,   70,  20, 12, 50, 10, 't', new int[] {18, 8}, new int[] {157,142,  0,  0}, 25),
		new CreatureType("Killer Red Beetle",		0x0000000A, 0x00000000, 0x0002,   85,  30, 14, 50, 11, 'K', new int[] {20, 8}, new int[] { 84,  0,  0,  0}, 25),
		new CreatureType("Wooden Mold",				0x00000001, 0x00000000, 0x10A0,  100,  50,  2, 50, 11, 'm', new int[] {25, 8}, new int[] {171,  0,  0,  0}, 25),
		new CreatureType("Giant Blue Dragon Fly",	0x00000012, 0x00080009, 0x0072,   75,  50, 20, 24, 11, 'F', new int[] { 6, 8}, new int[] { 29,  0,  0,  0}, 25),
		new CreatureType("Giant Grey Ant Lion",		0x0008000A, 0x00000000, 0x0032,   90,  40, 10, 40, 11, 'A', new int[] {19, 8}, new int[] { 39,  0,  0,  0}, 26),
		new CreatureType("Disenchanter Bat",		0x00000012, 0x00000000, 0x2162,   75,   1, 14, 24, 13, 'b', new int[] { 4, 8}, new int[] {204,  0,  0,  0}, 26),
		new CreatureType("Giant Fire Tick",			0x0000000A, 0x00000000, 0x2012,   90,  20, 14, 54, 11, 't', new int[] {16, 8}, new int[] {109,  0,  0,  0}, 26),
		new CreatureType("White Wraith",			0x0F02000A, 0x0000100C, 0x112C,  165,  10, 20, 40, 11, 'W', new int[] {15, 8}, new int[] {  5,  5,189,  0}, 26),
		new CreatureType("Giant Black Scorpion",	0x0000000A, 0x00000000, 0x0002,   85,  20, 12, 50, 11, 'S', new int[] {13, 8}, new int[] { 32,167,  0,  0}, 26),
		new CreatureType("Clear Ooze",				0x0719000A, 0x00000000, 0x10B0,   12,   1, 10, 14, 11, 'O', new int[] { 4, 8}, new int[] { 90,  0,  0,  0}, 26),
		new CreatureType("Killer Fire Beetle",		0x0000000A, 0x00000000, 0x2012,   95,  30, 14, 45, 11, 'K', new int[] {13, 8}, new int[] { 41,110,  0,  0}, 27),
		new CreatureType("Vampire",					0x17020002, 0x00001209, 0x112C,  175,  10, 20, 45, 11, 'V', new int[] {20, 8}, new int[] {  5,  5,190,  0}, 27),
		new CreatureType("Giant Red Dragon Fly",	0x00000012, 0x00800008, 0x2052,   75,  50, 20, 24, 11, 'F', new int[] { 7, 8}, new int[] { 96,  0,  0,  0}, 27),
		new CreatureType("Shimmering Mold",			0x00000081, 0x00000000, 0x10A0,  180,  50,  2, 24, 11, 'm', new int[] {32, 8}, new int[] {135,  0,  0,  0}, 27),
		new CreatureType("Black Knight",			0x13020002, 0x0000010F, 0x2034,  140,  10, 20, 60, 11, 'p', new int[] {25, 8}, new int[] { 23,  0,  0,  0}, 28),
		new CreatureType("Mage",					0x13020002, 0x00002C73, 0x2030,  150,  10, 20, 30, 11, 'p', new int[] {10, 8}, new int[] { 14,  0,  0,  0}, 28),
		new CreatureType("Ice Troll",				0x0F020002, 0x00000000, 0x0024,  160,  50, 20, 46, 11, 'T', new int[] {22, 8}, new int[] {  4,  4,123,  0}, 28),
		new CreatureType("Giant Purple Worm",		0x0000000A, 0x00000000, 0x2032,  400,  30, 14, 65, 11, 'w', new int[] {65, 8}, new int[] {  7,113,166,  0}, 29),
		new CreatureType("Young Blue Dragon",		0x1F00000A, 0x0008100B, 0x2005,  300,  70, 20, 50, 11, 'd', new int[] {33, 8}, new int[] { 52, 52, 29,  0}, 29),
		new CreatureType("Young White Dragon",		0x1F00000A, 0x0040100B, 0x0025,  275,  70, 20, 50, 11, 'd', new int[] {32, 8}, new int[] { 52, 52, 29,  0}, 29),
		new CreatureType("Young Green Dragon",		0x1F00000A, 0x0010100B, 0x2005,  290,  70, 20, 50, 11, 'd', new int[] {32, 8}, new int[] { 52, 52, 29,  0}, 29),
		new CreatureType("Giant Fire Bat",			0x00000012, 0x00000000, 0x2152,   85,  10, 14, 30, 12, 'b', new int[] { 5, 8}, new int[] {106, 52, 52,  0}, 29),
		new CreatureType("Giant Glowing Rat",		0x0020000A, 0x00000000, 0x2072,    4,  20,  8, 24, 11, 'r', new int[] { 3, 3}, new int[] {132,  0,  0,  0}, 29),
		/* Now things are going to get tough.			 */
		/* Some of the creatures have Max hit points, denoted in */
		/* their CDEFENSE flags as the '4000' bit set		 */
		new CreatureType("Skeleton Troll",			0x00020002, 0x00000000, 0x500C,  225,  20, 20, 55, 11, 's', new int[] {14, 8}, new int[] {  5,  5, 41,  0}, 30),
		new CreatureType("Giant Lightning Bat",		0x00000012, 0x00000000, 0x2042,   80,  10, 15, 34, 12, 'b', new int[] { 8, 8}, new int[] {133, 53, 53,  0}, 30),
		new CreatureType("Giant Static Ant",		0x0000000A, 0x00000000, 0x0002,   80,  60, 10, 40, 11, 'a', new int[] { 8, 8}, new int[] {134,  0,  0,  0}, 30),
		new CreatureType("Grave Wight",				0x0F02000A, 0x0000110A, 0x512C,  325,  30, 20, 35, 11, 'W', new int[] {12, 8}, new int[] {  6,  6,191,  0}, 30),
		new CreatureType("Killer Slicer Beetle",	0x0000000A, 0x00000000, 0x0002,  200,  30, 14, 55, 11, 'K', new int[] {22, 8}, new int[] { 48,  0,  0,  0}, 30),
		new CreatureType("Giant White Ant Lion",	0x0008000A, 0x00000000, 0x0022,  175,  40, 12, 45, 11, 'A', new int[] {20, 8}, new int[] {124,  0,  0,  0}, 30),
		new CreatureType("Ghost",					0x1715000A, 0x0001002F, 0x510C,  350,  10, 20, 30, 12, 'G', new int[] {13, 8}, new int[] { 99,192,184,  0}, 31),
		new CreatureType("Giant Black Ant Lion",	0x0008000A, 0x00000000, 0x0032,  170,  40, 14, 45, 11, 'A', new int[] {23, 8}, new int[] { 39,119,  0,  0}, 31),
		new CreatureType("Death Watch Beetle",		0x0000000A, 0x00000000, 0x0002,  190,  30, 16, 60, 11, 'K', new int[] {25, 8}, new int[] { 47, 67,  0,  0}, 31),
		new CreatureType("Ogre Mage",				0x0B020002, 0x0000A355, 0x6034,  250,  30, 20, 42, 11, 'o', new int[] {14, 8}, new int[] { 19,  0,  0,  0}, 31),
		new CreatureType("Two-Headed Troll",		0x0F020002, 0x00000000, 0x6024,  275,  50, 20, 48, 11, 'T', new int[] {14, 8}, new int[] {  7,  7, 29, 29}, 32),
		new CreatureType("Invisible Stalker",		0x00030022, 0x00000000, 0x1000,  200,  20, 20, 46, 13, 'E', new int[] {19, 8}, new int[] {  5,  0,  0,  0}, 32),
		new CreatureType("Giant Hunter Ant",		0x00000002, 0x00000000, 0x0002,  150,   1, 16, 40, 11, 'a', new int[] {12, 8}, new int[] { 46,  0,  0,  0}, 32),
		new CreatureType("Ninja",					0x13020002, 0x00000000, 0x6034,  300,  10, 20, 65, 11, 'p', new int[] {15, 8}, new int[] {152, 80,  0,  0}, 32),
		new CreatureType("Barrow Wight",			0x0F02000A, 0x00001308, 0x512C,  375,  10, 20, 40, 11, 'W', new int[] {13, 8}, new int[] {  7,  7,193,  0}, 33),
		new CreatureType("Skeleton 2-Headed Troll",	0x00020002, 0x00000000, 0x500C,  325,  20, 20, 48, 11, 's', new int[] {20, 8}, new int[] {  8,  8, 28, 28}, 33),
		new CreatureType("Water Elemental",			0x0008000A, 0x00000000, 0x1020,  325,  50, 12, 36, 11, 'E', new int[] {25, 8}, new int[] {  9,  9,  0,  0}, 33),
		new CreatureType("Fire Elemental",			0x0008000A, 0x00000000, 0x3010,  350,  70, 16, 40, 10, 'E', new int[] {25, 8}, new int[] {103,  0,  0,  0}, 33),
		new CreatureType("Lich",					0x1F020002, 0x00019F75, 0x510C,  750,  60, 20, 50, 11, 'L', new int[] {25, 8}, new int[] {179,194,214,  0}, 34),
		new CreatureType("Master Vampire",			0x17020002, 0x00001307, 0x512C,  700,  10, 20, 55, 11, 'V', new int[] {23, 8}, new int[] {  5,  5,195,  0}, 34),
		new CreatureType("Spirit Troll",			0x17150002, 0x00000000, 0x510C,  425,  10, 20, 40, 11, 'G', new int[] {15, 8}, new int[] { 53, 53, 29,185}, 34),
		new CreatureType("Giant Red Scorpion",		0x0000000A, 0x00000000, 0x0002,  275,  40, 12, 50, 12, 'S', new int[] {18, 8}, new int[] { 29,165,  0,  0}, 34),
		new CreatureType("Earth Elemental",			0x001E000A, 0x00000000, 0x1200,  375,  90, 10, 60, 10, 'E', new int[] {30, 8}, new int[] { 22, 22,  0,  0}, 34),
		new CreatureType("Young Black Dragon",		0x1F00000A, 0x0020100B, 0x6005,  600,  50, 20, 55, 11, 'd', new int[] {32, 8}, new int[] { 53, 53, 29,  0}, 35),
		new CreatureType("Young Red Dragon",		0x1F00000A, 0x0080100A, 0x6015,  650,  50, 20, 60, 11, 'd', new int[] {36, 8}, new int[] { 54, 54, 37,  0}, 35),
		new CreatureType("Necromancer",				0x13020002, 0x00005763, 0x6030,  600,  10, 20, 40, 11, 'p', new int[] {17, 8}, new int[] { 15,  0,  0,  0}, 35),
		new CreatureType("Mummified Troll",			0x0F020002, 0x00000000, 0x502C,  400,  50, 20, 38, 11, 'M', new int[] {18, 8}, new int[] { 15, 15,  0,  0}, 35),
		new CreatureType("Giant Red Ant Lion",		0x0008000A, 0x00000000, 0x2012,  350,  40, 14, 48, 11, 'A', new int[] {23, 8}, new int[] {107,  0,  0,  0}, 35),
		new CreatureType("Mature White Dragon",		0x2F00000A, 0x0040100A, 0x4025, 1000,  70, 20, 65, 11, 'd', new int[] {48, 8}, new int[] { 54, 54, 37,  0}, 35),
		new CreatureType("Xorn",					0x00160002, 0x00000000, 0x4200,  650,  10, 20, 80, 11, 'X', new int[] {20, 8}, new int[] {  5,  5,  5,  0}, 36),
		new CreatureType("Giant Mottled Ant Lion",	0x0008000A, 0x00000000, 0x0032,  350,  40, 14, 50, 12, 'A', new int[] {24, 8}, new int[] { 38,  0,  0,  0}, 36),
		new CreatureType("Grey Wraith",				0x0F02000A, 0x00001308, 0x512C,  700,  10, 20, 50, 11, 'W', new int[] {23, 8}, new int[] {  9,  9,196,  0}, 36),
		new CreatureType("Young Multi-Hued Dragon",	0x4F00000A, 0x00F81005, 0x6005, 1250,  50, 20, 55, 11, 'd', new int[] {40, 8}, new int[] { 55, 55, 38,  0}, 36),
		new CreatureType("Mature Blue Dragon",		0x2F00000A, 0x00081009, 0x6005, 1200,  70, 20, 75, 11, 'd', new int[] {48, 8}, new int[] { 54, 54, 38,  0}, 36),
		new CreatureType("Mature Green Dragon",		0x2F00000A, 0x0010100A, 0x6005, 1100,  70, 20, 70, 11, 'd', new int[] {48, 8}, new int[] { 52, 52, 29,  0}, 36),
		new CreatureType("Iridescent Beetle",		0x0000000A, 0x00000000, 0x0002,  850,  30, 16, 60, 11, 'K', new int[] {32, 8}, new int[] { 45, 10,146,  0}, 37),
		new CreatureType("King Vampire",			0x17020002, 0x00001307, 0x512C, 1000,  10, 20, 65, 11, 'V', new int[] {38, 8}, new int[] {  5,  5,198,  0}, 37),
		new CreatureType("King Lich",				0x1F020002, 0x00019F73, 0x510C, 1400,  50, 20, 65, 11, 'L', new int[] {52, 8}, new int[] {180,197,214,  0}, 37),
		new CreatureType("Mature Red Dragon",		0x2F00000A, 0x00801808, 0x6015, 1400,  30, 20, 80, 11, 'd', new int[] {60, 8}, new int[] { 56, 56, 39,  0}, 37),
		new CreatureType("Mature Black Dragon",		0x2F00000A, 0x00201009, 0x6005, 1350,  30, 20, 55, 11, 'd', new int[] {58, 8}, new int[] { 54, 54, 38,  0}, 37),
		new CreatureType("Mature Multi-Hued Dragon",0x6F00000A, 0x00F81A05, 0x6005, 1650,  50, 20, 65, 11, 'd', new int[] {80, 8}, new int[] { 56, 56, 39,  0}, 38),
		new CreatureType("Ancient White Dragon",	0x4F000002, 0x00401A09, 0x4025, 1500,  80, 20, 80, 12, 'D', new int[] {88, 8}, new int[] { 54, 54, 37,  0}, 38),
		new CreatureType("Emperor Wight",			0x1B02000A, 0x00001306, 0x512C, 1600,  10, 20, 40, 12, 'W', new int[] {48, 8}, new int[] { 10, 10,199,  0}, 38),
		new CreatureType("Black Wraith",			0x1F02000A, 0x00001307, 0x512C, 1700,  10, 20, 55, 11, 'W', new int[] {50, 8}, new int[] { 10, 10,200,  0}, 38),
		new CreatureType("Nether Wraith",			0x1F07000A, 0x00005316, 0x512C, 2100,  10, 20, 55, 11, 'W', new int[] {58, 8}, new int[] { 10, 10,202,  0}, 39),
		new CreatureType("Sorcerer",				0x1F020002, 0x0000FF73, 0x6030, 2150,  10, 20, 50, 12, 'p', new int[] {30, 8}, new int[] { 16,  0,  0,  0}, 39),
		new CreatureType("Ancient Blue Dragon",		0x4F000002, 0x00081A08, 0x6005, 2500,  80, 20, 90, 12, 'D', new int[] {87, 8}, new int[] { 55, 55, 39,  0}, 39),
		new CreatureType("Ancient Green Dragon",	0x4F000002, 0x00101A09, 0x6005, 2400,  80, 20, 85, 12, 'D', new int[] {90, 8}, new int[] { 54, 54, 38,  0}, 39),
		new CreatureType("Ancient Black Dragon",	0x4F000002, 0x00201A07, 0x6005, 2500,  70, 20, 90, 12, 'D', new int[] {90, 8}, new int[] { 55, 55, 38,  0}, 39),
		new CreatureType("Crystal Ooze",			0x073B000A, 0x00000000, 0x10A0,    8,   1, 10, 30,  9, 'O', new int[] {12, 8}, new int[] {128,  0,  0,  0}, 40),
		new CreatureType("Disenchanter Worm",		0x00200022, 0x00000000, 0x01B2,   30,  10,  7,  5, 10, 'w', new int[] {10, 8}, new int[] {208,  0,  0,  0}, 40),
		new CreatureType("Rotting Quylthulg",		0x00010004, 0x00004014, 0x5000, 1000,   0, 20,  1, 12, 'Q', new int[] {12, 8}, new int[] {  0,  0,  0,  0}, 40),
		new CreatureType("Ancient Red Dragon",		0x6F000002, 0x00801E06, 0x6015, 2750,  70, 20,100, 12, 'D', new int[] {105,8}, new int[] { 56, 56, 40,  0}, 40),
		new CreatureType("Death Quasit",			0x1103000A, 0x000010FA, 0x1004, 1000,   0, 20, 80, 13, 'q', new int[] {55, 8}, new int[] {177, 58, 58,  0}, 40),
		new CreatureType("Emperor Lich",			0x2F020002, 0x00019F72, 0x510C,10000,  50, 20, 75, 12, 'L', new int[] {38,40}, new int[] {181,201,214,  0}, 40),
		new CreatureType("Ancient Multi-Hued Dragon",0x7F000002,0x00F89E05, 0x6005,12000,  70, 20,100, 12, 'D', new int[] {52,40}, new int[] { 57, 57, 42,  0}, 40),
		/* Winning creatures should follow here.			 */
		/* Winning creatures are denoted by the 32 bit in CMOVE		 */
		/* Iggy is not a win creature, just a royal pain in the ass.	 */
		new CreatureType("Evil Iggy",				0x7F130002, 0x0001D713, 0x5004,18000,   0, 30, 80, 12, 'p', new int[] {60,40}, new int[] { 81,150,  0,  0}, 50),
		/* Here is the only actual win creature.			 */
		new CreatureType("Balrog",					0xFF1F0002, 0x0081C743, 0x5004,55000,   0, 40,125, 13, 'B', new int[] {75,40}, new int[] {104, 78,214,  0},100)
	};
	
	/* ERROR: attack #35 is no longer used */
	//M_attack_type[N_MONS_ATTS]
	public static MonsterAttackType[] monsterAttacks = {
		/* 0 */
		new MonsterAttackType(0, 0, 0, 0), new MonsterAttackType(1, 1, 1, 2), new MonsterAttackType(1, 1, 1, 3),
		new MonsterAttackType(1, 1, 1, 4), new MonsterAttackType(1, 1, 1, 5), new MonsterAttackType(1, 1, 1, 6),
		new MonsterAttackType(1, 1, 1, 7), new MonsterAttackType(1, 1, 1, 8), new MonsterAttackType(1, 1, 1, 9),
		new MonsterAttackType(1, 1, 1,10), new MonsterAttackType(1, 1, 1,12), new MonsterAttackType(1, 1, 2, 2),
		new MonsterAttackType(1, 1, 2, 3), new MonsterAttackType(1, 1, 2, 4), new MonsterAttackType(1, 1, 2, 5),
		new MonsterAttackType(1, 1, 2, 6), new MonsterAttackType(1, 1, 2, 8), new MonsterAttackType(1, 1, 3, 4),
		new MonsterAttackType(1, 1, 3, 5), new MonsterAttackType(1, 1, 3, 6),
		/* 20 */
		new MonsterAttackType(1, 1, 3, 8), new MonsterAttackType(1, 1, 4, 3), new MonsterAttackType(1, 1, 4, 6),
		new MonsterAttackType(1, 1, 5, 5), new MonsterAttackType(1, 2, 1, 1), new MonsterAttackType(1, 2, 1, 2),
		new MonsterAttackType(1, 2, 1, 3), new MonsterAttackType(1, 2, 1, 4), new MonsterAttackType(1, 2, 1, 5),
		new MonsterAttackType(1, 2, 1, 6), new MonsterAttackType(1, 2, 1, 7), new MonsterAttackType(1, 2, 1, 8),
		new MonsterAttackType(1, 2, 1,10), new MonsterAttackType(1, 2, 2, 3), new MonsterAttackType(1, 2, 2, 4),
		new MonsterAttackType(1, 2, 2, 5), new MonsterAttackType(1, 2, 2, 6), new MonsterAttackType(1, 2, 2, 8),
		new MonsterAttackType(1, 2, 2,10), new MonsterAttackType(1, 2, 2,12),
		/* 40 */
		new MonsterAttackType(1, 2, 2,14), new MonsterAttackType(1, 2, 3, 4), new MonsterAttackType(1, 2, 3,12),
		new MonsterAttackType(1, 2, 4, 4), new MonsterAttackType(1, 2, 4, 5), new MonsterAttackType(1, 2, 4, 6),
		new MonsterAttackType(1, 2, 4, 8), new MonsterAttackType(1, 2, 5, 4), new MonsterAttackType(1, 2, 5, 8),
		new MonsterAttackType(1, 3, 1, 1), new MonsterAttackType(1, 3, 1, 2), new MonsterAttackType(1, 3, 1, 3),
		new MonsterAttackType(1, 3, 1, 4), new MonsterAttackType(1, 3, 1, 5), new MonsterAttackType(1, 3, 1, 8),
		new MonsterAttackType(1, 3, 1, 9), new MonsterAttackType(1, 3, 1,10), new MonsterAttackType(1, 3, 1,12),
		new MonsterAttackType(1, 3, 3, 3), new MonsterAttackType(1, 4, 1, 2),
		/* 60 */
		new MonsterAttackType(1, 4, 1, 3), new MonsterAttackType(1, 4, 1,  4), new MonsterAttackType(1, 4, 2, 4),
		new MonsterAttackType(1, 5, 1, 2), new MonsterAttackType(1, 5, 1,  3), new MonsterAttackType(1, 5, 1, 4),
		new MonsterAttackType(1, 5, 1, 5), new MonsterAttackType(1,10, 5,  6), new MonsterAttackType(1,12, 1, 1),
		new MonsterAttackType(1,12, 1, 2), new MonsterAttackType(1,13, 1,  1), new MonsterAttackType(1,13, 1, 3),
		new MonsterAttackType(1,14, 0, 0), new MonsterAttackType(1,16, 1,  4), new MonsterAttackType(1,16, 1, 6),
		new MonsterAttackType(1,16, 1, 8), new MonsterAttackType(1,16, 1, 10), new MonsterAttackType(1,16, 2, 8),
		new MonsterAttackType(1,17, 8,12), new MonsterAttackType(1,18, 0,  0),
		/* 80 */
		new MonsterAttackType(2, 1, 3, 4), new MonsterAttackType(2, 1, 4, 6), new MonsterAttackType(2, 2, 1, 4),
		new MonsterAttackType(2, 2, 2, 4), new MonsterAttackType(2, 2, 4, 4), new MonsterAttackType(2, 4, 1, 4),
		new MonsterAttackType(2, 4, 1, 7), new MonsterAttackType(2, 5, 1, 5), new MonsterAttackType(2, 7, 1, 6),
		new MonsterAttackType(3, 1, 1, 4), new MonsterAttackType(3, 5, 1, 8), new MonsterAttackType(3,13, 1, 4),
		new MonsterAttackType(3, 7, 0, 0), new MonsterAttackType(4, 1, 1, 1), new MonsterAttackType(4, 1, 1, 4),
		new MonsterAttackType(4, 2, 1, 2), new MonsterAttackType(4, 2, 1, 6), new MonsterAttackType(4, 5, 0, 0),
		new MonsterAttackType(4, 7, 0, 0), new MonsterAttackType(4,10, 0, 0),
		/*100 */
		new MonsterAttackType(4,13, 1, 6), new MonsterAttackType(5, 1, 2, 6), new MonsterAttackType(5, 1, 3, 7),
		new MonsterAttackType(5, 1, 4, 6), new MonsterAttackType(5, 1,10,12), new MonsterAttackType(5, 2, 1, 3),
		new MonsterAttackType(5, 2, 3, 6), new MonsterAttackType(5, 2, 3,12), new MonsterAttackType(5, 5, 4, 4),
		new MonsterAttackType(5, 9, 3, 7), new MonsterAttackType(5, 9, 4, 5), new MonsterAttackType(5,12, 1, 6),
		new MonsterAttackType(6, 2, 1, 3), new MonsterAttackType(6, 2, 2, 8), new MonsterAttackType(6, 2, 4, 4),
		new MonsterAttackType(6, 5, 1,10), new MonsterAttackType(6, 5, 2, 3), new MonsterAttackType(6, 8, 1, 5),
		new MonsterAttackType(6, 9, 2, 6), new MonsterAttackType(6, 9, 3, 6),
		/*120 */
		new MonsterAttackType(7, 1, 3, 6), new MonsterAttackType(7, 2, 1, 3), new MonsterAttackType(7, 2, 1, 6),
		new MonsterAttackType(7, 2, 3, 6), new MonsterAttackType(7, 2, 3,10), new MonsterAttackType(7, 5, 1, 6),
		new MonsterAttackType(7, 5, 2, 3), new MonsterAttackType(7, 5, 2, 6), new MonsterAttackType(7, 5, 4, 4),
		new MonsterAttackType(7,12, 1, 4), new MonsterAttackType(8, 1, 3, 8), new MonsterAttackType(8, 2, 1, 3),
		new MonsterAttackType(8, 2, 2, 6), new MonsterAttackType(8, 2, 3, 8), new MonsterAttackType(8, 2, 5, 5),
		new MonsterAttackType(8, 5, 5, 4), new MonsterAttackType(9, 5, 1, 2), new MonsterAttackType(9, 5, 2, 5),
		new MonsterAttackType(9, 5, 2, 6), new MonsterAttackType(9, 8, 2, 4),
		/*140 */
		new MonsterAttackType(9, 12, 1, 3), new MonsterAttackType(10, 2, 1, 6), new MonsterAttackType(10, 4, 1, 1),
		new MonsterAttackType(10, 7, 2, 6), new MonsterAttackType(10, 9, 1, 2), new MonsterAttackType(11, 1, 1, 2),
		new MonsterAttackType(11, 7, 0, 0), new MonsterAttackType(11,13, 2, 4), new MonsterAttackType(12, 5, 0, 0),
		new MonsterAttackType(13, 5, 0, 0), new MonsterAttackType(13,19, 0, 0), new MonsterAttackType(14, 1, 1, 3),
		new MonsterAttackType(14, 1, 3, 4), new MonsterAttackType(14, 2, 1, 3), new MonsterAttackType(14, 2, 1, 4),
		new MonsterAttackType(14, 2, 1, 5), new MonsterAttackType(14, 2, 1, 6), new MonsterAttackType(14, 2, 1,10),
		new MonsterAttackType(14, 2, 2, 4), new MonsterAttackType(14, 2, 2, 5),
		/*160 */
		new MonsterAttackType(14, 2, 2, 6), new MonsterAttackType(14, 2, 3, 4), new MonsterAttackType(14, 2, 3, 9),
		new MonsterAttackType(14, 2, 4, 4), new MonsterAttackType(14, 4, 1, 2), new MonsterAttackType(14, 4, 1, 4),
		new MonsterAttackType(14, 4, 1, 8), new MonsterAttackType(14, 4, 2, 5), new MonsterAttackType(14, 5, 1, 2),
		new MonsterAttackType(14, 5, 1, 3), new MonsterAttackType(14, 5, 2, 4), new MonsterAttackType(14, 5, 2, 6),
		new MonsterAttackType(14, 5, 3, 5), new MonsterAttackType(14,12, 1, 2), new MonsterAttackType(14,12, 1, 4),
		new MonsterAttackType(14,13, 2, 4), new MonsterAttackType(15, 2, 1, 6), new MonsterAttackType(15, 2, 3, 6),
		new MonsterAttackType(15, 5, 1, 8), new MonsterAttackType(15, 5, 2, 8),
		/*180 */
		new MonsterAttackType(15, 5, 2,10), new MonsterAttackType(15, 5, 2, 12), new MonsterAttackType(15,12, 1, 3),
		new MonsterAttackType(16,13, 1, 2), new MonsterAttackType(17, 3, 1, 10), new MonsterAttackType(18, 5, 0, 0),
		new MonsterAttackType(19, 5, 5, 8), new MonsterAttackType(19, 5, 12, 8), new MonsterAttackType(19, 5,14, 8),
		new MonsterAttackType(19, 5,15, 8), new MonsterAttackType(19, 5, 18, 8), new MonsterAttackType(19, 5,20, 8),
		new MonsterAttackType(19, 5,22, 8), new MonsterAttackType(19, 5, 26, 8), new MonsterAttackType(19, 5,30, 8),
		new MonsterAttackType(19, 5,32, 8), new MonsterAttackType(19, 5, 34, 8), new MonsterAttackType(19, 5,36, 8),
		new MonsterAttackType(19, 5,38, 8), new MonsterAttackType(19, 5, 42, 8),
		/*200 */
		new MonsterAttackType(19, 5,44, 8), new MonsterAttackType(19, 5,46, 8), new MonsterAttackType(19, 5,52, 8),
		new MonsterAttackType(20, 0, 0, 0), new MonsterAttackType(21, 1, 0, 0), new MonsterAttackType(21, 5, 0, 0),
		new MonsterAttackType(21, 5, 1, 6), new MonsterAttackType(21, 7, 0, 0), new MonsterAttackType(21,12, 1, 4),
		new MonsterAttackType(22, 5, 2, 3), new MonsterAttackType(22,12, 0, 0), new MonsterAttackType(22,15, 1, 1),
		/*212 */
		new MonsterAttackType(23, 1, 1, 1), new MonsterAttackType(23, 5, 1, 3), new MonsterAttackType(24, 5, 0, 0)
	};
	
	private Monsters() { }
	
	public static MonsterType[] monsterList = new MonsterType[Constants.MAX_MALLOC];
	public static int[] monsterLevel = new int[Constants.MAX_MONS_LEVEL + 1];
	
	/* Blank monster values	*/
	public static MonsterType getBlankMonster() {
		return new MonsterType(0, 0, 0, 0, 0, 0, 0, false, 0, 0);
	}
	
	public static int freeMonsterIndex;			/* Cur free monster ptr	*/
	public static int totalMonsterMultiples;		/* # of repro's of creature	*/
}
