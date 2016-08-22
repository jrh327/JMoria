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

import net.jonhopkins.moria.monsters.*;
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
	public CreatureType[] c_list = {
			new FilthyStreetUrchin(),
			new BlubberingIdiot(),
			new PitifulLookingBeggar(),
			new MangyLookingLeper(),
			new SquintEyedRogue(),
			new SingingHappyDrunk(),
			new MeanLookingMercenary(),
			new BattleScarredVeteran(),
			new GreyMushroompatch(),
			new GiantYellowCentipede(),
			new GiantWhiteCentipede(),
			new WhiteIckyThing(),
			new ClearIckyThing(),
			new GiantWhiteMouse(),
			new LargeBrownSnake(),
			new LargeWhiteSnake(),
			new Kobold(),
			new WhiteWormmass(),
			new FloatingEye(),
			new ShriekerMushroompatch(),
			new BlubberingIckyThing(),
			new MetallicGreenCentipede(),
			new NoviceWarrior(),
			new NoviceRogue(),
			new NovicePriest(),
			new NoviceMage(),
			new YellowMushroompatch(),
			new WhiteJelly(),
			new GiantGreenFrog(),
			new GiantBlackAnt(),
			new WhiteHarpy(),
			new BlueYeek(),
			new GreenWormmass(),
			new LargeBlackSnake(),
			new Poltergeist(),
			new MetallicBlueCentipede(),
			new GiantWhiteLouse(),
			new BlackNaga(),
			new SpottedMushroompatch(),
			new YellowJelly(),
			new ScruffyLookingHobbit(),
			new HugeBrownBat(),
			new GiantWhiteAnt(),
			new YellowMold(),
			new MetallicRedCentipede(),
			new YellowWormmass(),
			new LargeGreenSnake(),
			new RadiationEye(),
			new DroolingHarpy(),
			new SilverMouse(),
			new BlackMushroompatch(),
			new BlueJelly(),
			new CreepingCopperCoins(),
			new GiantWhiteRat(),
			new GiantBlackCentipede(),
			new GiantBlueCentipede(),
			new BlueWormmass(),
			new LargeGreySnake(),
			new Jackal(),
			new GreenNaga(),
			new GreenGluttonGhost(),
			new WhiteMushroompatch(),
			new GreenJelly(),
			new SkeletonKobold(),
			new SilverJelly(),
			new GiantBlackFrog(),
			new GreyIckyThing(),
			new DisenchanterEye(),
			new BlackYeek(),
			new RedWormmass(),
			new GiantHouseFly(),
			new CopperheadSnake(),
			new RotJelly(),
			new PurpleMushroompatch(),
			new BrownMold(),
			new GiantBrownBat(),
			new CreepingSilverCoins(),
			new Orc(),
			new GreyHarpy(),
			new BlueIckyThing(),
			new Rattlesnake(),
			new BloodshotEye(),
			new RedNaga(),
			new RedJelly(),
			new GiantRedFrog(),
			new GreenIckyThing(),
			new ZombieKobold(),
			new LostSoul(),
			new GreedyLittleGnome(),
			new GiantGreenFly(),
			new BrownYeek(),
			new GreenMold(),
			new SkeletonOrc(),
			new SeedyLookingHuman(),
			new RedIckyThing(),
			new Bandit(),
			new Yeti(),
			new BloodshotIckyThing(),
			new GiantGreyRat(),
			new BlackHarpy(),
			new GiantBlackBat(),
			new ClearYeek(),
			new OrcShaman(),
			new GiantRedAnt(),
			new KingCobra(),
			new ClearMushroompatch(),
			new GiantWhiteTick(),
			new HairyMold(),
			new DisenchanterMold(),
			new GiantRedCentipede(),
			new CreepingGoldCoins(),
			new GiantFruitFly(),
			new Brigand(),
			new OrcZombie(),
			new OrcWarrior(),
			new VorpalBunny(),
			new NastyLittleGnome(),
			new Hobgoblin(),
			new BlackMamba(),
			new GrapeJelly(),
			new MasterYeek(),
			new Priest(),
			new GiantClearAnt(),
			new AirSpirit(),
			new SkeletonHuman(),
			new HumanZombie(),
			new MoaningSpirit(),
			new Swordsman(),
			new KillerBrownBeetle(),
			new Ogre(),
			new GiantRedSpeckledFrog(),
			new MagicUser(),
			new BlackOrc(),
			new GiantLongEaredBat(),
			new GiantGnat(),
			new KillerGreenBeetle(),
			new GiantFlea(),
			new GiantWhiteDragonFly(),
			new HillGiant(),
			new SkeletonHobgoblin(),
			new FleshGolem(),
			new WhiteDragonBat(),
			new GiantBlackLouse(),
			new GuardianNaga(),
			new GiantGreyBat(),
			new GiantClearCentipede(),
			new GiantYellowTick(),
			new GiantEbonyAnt(),
			new FrostGiant(),
			new ClayGolem(),
			new HugeWhiteBat(),
			new GiantTanBat(),
			new VioletMold(),
			new UmberHulk(),
			new GelatinousCube(),
			new GiantBlackRat(),
			new GiantGreenDragonFly(),
			new FireGiant(),
			new GreenDragonBat(),
			new Quasit(),
			new Troll(),
			new WaterSpirit(),
			new GiantBrownScorpion(),
			new EarthSpirit(),
			new FireSpirit(),
			new UrukHaiOrc(),
			new StoneGiant(),
			new StoneGolem(),
			new GreyOoze(),
			new DisenchanterOoze(),
			new GiantSpottedRat(),
			new MummifiedKobold(),
			new KillerBlackBeetle(),
			new RedMold(),
			new Quylthulg(),
			new GiantRedBat(),
			new GiantBlackDragonFly(),
			new CloudGiant(),
			new BlackDragonBat(),
			new BlueDragonBat(),
			new MummifiedOrc(),
			new KillerBoringBeetle(),
			new KillerStagBeetle(),
			new BlackMold(),
			new IronGolem(),
			new GiantYellowScorpion(),
			new GreenOoze(),
			new BlackOoze(),
			new Warrior(),
			new RedDragonBat(),
			new KillerBlueBeetle(),
			new GiantSilverAnt(),
			new CrimsonMold(),
			new ForestWight(),
			new Berzerker(),
			new MummifiedHuman(),
			new Banshee(),
			new GiantTroll(),
			new GiantBrownTick(),
			new KillerRedBeetle(),
			new WoodenMold(),
			new GiantBlueDragonFly(),
			new GiantGreyAntLion(),
			new DisenchanterBat(),
			new GiantFireTick(),
			new WhiteWraith(),
			new GiantBlackScorpion(),
			new ClearOoze(),
			new KillerFireBeetle(),
			new Vampire(),
			new GiantRedDragonFly(),
			new ShimmeringMold(),
			new BlackKnight(),
			new Mage(),
			new IceTroll(),
			new GiantPurpleWorm(),
			new YoungBlueDragon(),
			new YoungWhiteDragon(),
			new YoungGreenDragon(),
			new GiantFireBat(),
			new GiantGlowingRat(),
			/* Now things are going to get tough.			 */
			/* Some of the creatures have Max hit points, denoted in */
			/* their CDEFENSE flags as the '4000' bit set		 */
			new SkeletonTroll(),
			new GiantLightningBat(),
			new GiantStaticAnt(),
			new GraveWight(),
			new KillerSlicerBeetle(),
			new GiantWhiteAntLion(),
			new Ghost(),
			new GiantBlackAntLion(),
			new DeathWatchBeetle(),
			new OgreMage(),
			new TwoHeadedTroll(),
			new InvisibleStalker(),
			new GiantHunterAnt(),
			new Ninja(),
			new BarrowWight(),
			new SkeletonHeadedTroll(),
			new WaterElemental(),
			new FireElemental(),
			new Lich(),
			new MasterVampire(),
			new SpiritTroll(),
			new GiantRedScorpion(),
			new EarthElemental(),
			new YoungBlackDragon(),
			new YoungRedDragon(),
			new Necromancer(),
			new MummifiedTroll(),
			new GiantRedAntLion(),
			new MatureWhiteDragon(),
			new Xorn(),
			new GiantMottledAntLion(),
			new GreyWraith(),
			new YoungMultiHuedDragon(),
			new MatureBlueDragon(),
			new MatureGreenDragon(),
			new IridescentBeetle(),
			new KingVampire(),
			new KingLich(),
			new MatureRedDragon(),
			new MatureBlackDragon(),
			new MatureMultiHuedDragon(),
			new AncientWhiteDragon(),
			new EmperorWight(),
			new BlackWraith(),
			new NetherWraith(),
			new Sorcerer(),
			new AncientBlueDragon(),
			new AncientGreenDragon(),
			new AncientBlackDragon(),
			new CrystalOoze(),
			new DisenchanterWorm(),
			new RottingQuylthulg(),
			new AncientRedDragon(),
			new DeathQuasit(),
			new EmperorLich(),
			new AncientMultiHuedDragon(),
			/* Winning creatures should follow here.			 */
			/* Winning creatures are denoted by the 32 bit in CMOVE		 */
			/* Iggy is not a win creature, just a royal pain in the ass.	 */
			new EvilIggy(),
			/* Here is the only actual win creature.			 */
			new Balrog()
	};
	
	/* ERROR: attack #35 is no longer used */
	//M_attack_type[N_MONS_ATTS]
	public MonsterAttackType[] monster_attacks = {
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
	
	private static Monsters instance;
	private Monsters() { }
	public static Monsters getInstance() {
		if (instance == null) instance = new Monsters();
		return instance;
	}
	
	public MonsterType[] m_list = new MonsterType[Constants.MAX_MALLOC];
	public int[] m_level = new int[Constants.MAX_MONS_LEVEL + 1];
	
	/* Blank monster values	*/
	public MonsterType blank_monster() { return new MonsterType(0, 0, 0, 0, 0, 0, 0, false, 0, 0); };
	public int mfptr;			/* Cur free monster ptr	*/
	public int mon_tot_mult;		/* # of repro's of creature	*/
}
