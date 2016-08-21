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
import net.jonhopkins.moria.treasure.ammo.*;
import net.jonhopkins.moria.treasure.armor.*;
import net.jonhopkins.moria.treasure.books.*;
import net.jonhopkins.moria.treasure.food.*;
import net.jonhopkins.moria.treasure.gems.*;
import net.jonhopkins.moria.treasure.jewelry.*;
import net.jonhopkins.moria.treasure.miscellaneous.*;
import net.jonhopkins.moria.treasure.potions.*;
import net.jonhopkins.moria.treasure.scrolls.*;
import net.jonhopkins.moria.treasure.staffs.*;
import net.jonhopkins.moria.treasure.traps.*;
import net.jonhopkins.moria.treasure.wands.*;
import net.jonhopkins.moria.treasure.weapons.*;

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
	
	private static Treasure instance;
	private Treasure() {
		int i;
		
		for (i = 0; i < Constants.MAX_TALLOC; i++) {
			t_list[i] = new InvenType();
		}
		for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
			inventory[i] = new InvenType();
		}
	}
	public static Treasure getInstance() {
		if (instance == null) {
			instance = new Treasure();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		
	}
	
	/* Object list (All objects must be defined here)		 */
	
	//treasure_type object_list[MAX_OBJECTS]
	public TreasureType[] object_list = {
			/* Dungeon items from 0 to MAX_DUNGEON_OBJ */
			new Poison(),						/*   0 */
			new Blindness(),					/*   1 */
			new Paranoia(),						/*   2 */
			new Confusion(),					/*   3 */
			new Hallucination(),				/*   4 */
			new CurePoison(),					/*   5 */
			new CureBlindness(),				/*   6 */
			new CureParanoia(),					/*   7 */
			new CureConfusion(),				/*   8 */
			new Weakness(),						/*   9 */
			new Unhealth(),						/*  10 */
			new RestoreConstitution(),			/*  11 */
			new FirstAid(),						/*  12 */
			new MinorCures(),					/*  13 */
			new LightCures(),					/*  14 */
			new Restoration(),					/*  15 */
			new Poison2(),						/*  16 */
			new Hallucination2(),				/*  17 */
			new CurePoison2(),					/*  18 */
			new Unhealth2(),					/*  19 */
			new MajorCures(),					/*  20 */
			new RationOfFood(),					/*  21 */
			new RationOfFood2(),				/*  22 */
			new RationOfFood3(),				/*  23 */
			new SlimeMold(),					/*  24 */
			new PieceOfElvishWaybread(),		/*  25 */
			new PieceOfElvishWaybread2(),		/*  26 */
			new PieceOfElvishWaybread3(),		/*  27 */
			new DaggerMainGauche(),				/*  28 */
			new DaggerMisericorde(),			/*  29 */
			new DaggerStiletto(),				/*  30 */
			new DaggerBodkin(),					/*  31 */
			new BrokenDagger(),					/*  32 */
			new Backsword(),					/*  33 */
			new BastardSword(),					/*  34 */
			new ThrustingSwordBilbo(),			/*  35 */
			new ThrustingSwordBaselard(),		/*  36 */
			new Broadsword(),					/*  37 */
			new TwoHandedSwordClaymore(),		/*  38 */
			new Cutlass(),						/*  39 */
			new TwoHandedSwordEspadon(),		/*  40 */
			new ExecutionersSword(),			/*  41 */
			new TwoHandedSwordFlamberge(),		/*  42 */
			new Foil(),							/*  43 */
			new Katana(),						/*  44 */
			new Longsword(),					/*  45 */
			new TwoHandedSwordNoDachi(),		/*  46 */
			new Rapier(),						/*  47 */
			new Sabre(),						/*  48 */
			new SmallSword(),					/*  49 */
			new TwoHandedSwordZweihander(),		/*  50 */
			new BrokenSword(),					/*  51 */
			new BallAndChain(),					/*  52 */
			new CatONineTails(),				/*  53 */
			new WoodenClub(),					/*  54 */
			new Flail(),						/*  55 */
			new TwoHandedGreatFlail(),			/*  56 */
			new Morningstar(),					/*  57 */
			new Mace(),							/*  58 */
			new WarHammer(),					/*  59 */
			new LeadFilledMace(),				/*  60 */
			new AwlPike(),						/*  61 */
			new BeakedAxe(),					/*  62 */
			new Fauchard(),						/*  63 */
			new Glaive(),						/*  64 */
			new Halberd(),						/*  65 */
			new LucerneHammer(),				/*  66 */
			new Pike(),							/*  67 */
			new Spear(),						/*  68 */
			new Lance(),						/*  69 */
			new Javelin(),						/*  70 */
			new BattleAxeBalestarius(),			/*  71 */
			new BattleAxeEuropean(),			/*  72 */
			new BroadAxe(),						/*  73 */
			new ShortBow(),						/*  74 */
			new LongBow(),						/*  75 */
			new CompositeBow(),					/*  76 */
			new LightCrossbow(),				/*  77 */
			new HeavyCrossbow(),				/*  78 */
			new Sling(),						/*  79 */
			new Arrow(),						/*  80 */
			new Bolt(),							/*  81 */
			new RoundedPebble(),				/*  82 */
			new IronShot(),						/*  83 */
			new IronSpike(),					/*  84 */
			new BrassLantern(),					/*  85 */
			new WoodenTorch(),					/*  86 */
			new OrcishPick(),					/*  87 */
			new DwarvenPick(),					/*  88 */
			new GnomishShovel(),				/*  89 */
			new DwarvenShovel(),				/*  90 */
			new SoftLeatherShoes(),				/*  91 */
			new SoftLeatherBoots(),				/*  92 */
			new HardLeatherBoots(),				/*  93 */
			new SoftLeatherCap(),				/*  94 */
			new HardLeatherCap(),				/*  95 */
			new MetalCap(),						/*  96 */
			new IronHelm(),						/*  97 */
			new SteelHelm(),					/*  98 */
			new SilverCrown(),					/*  99 */
			new GoldenCrown(),					/* 100 */
			new JewelEncrustedCrown(),			/* 101 */
			new Robe(),							/* 102 */
			new SoftLeatherArmor(),				/* 103 */
			new SoftStuddedLeather(),			/* 104 */
			new HardLeatherArmor(),				/* 105 */
			new HardStuddedLeather(),			/* 106 */
			new WovenCordArmor(),				/* 107 */
			new SoftLeatherRingMail(),			/* 108 */
			new HardLeatherRingMail(),			/* 109 */
			new LeatherScaleMail(),				/* 110 */
			new MetalScaleMail(),				/* 111 */
			new ChainMail(),					/* 112 */
			new RustyChainMail(),				/* 113 */
			new DoubleChainMail(),				/* 114 */
			new AugmentedChainMail(),			/* 115 */
			new BarChainMail(),					/* 116 */
			new MetalBrigandineArmor(),			/* 117 */
			new LaminatedArmor(),				/* 118 */
			new PartialPlateArmor(),			/* 119 */
			new MetalLamellarArmor(),			/* 120 */
			new FullPlateArmor(),				/* 121 */
			new RibbedPlateArmor(),				/* 122 */
			new Cloak(),						/* 123 */
			new LeatherGloves(),				/* 124 */
			new Gauntlets(),					/* 125 */
			new SmallLeatherShield(),			/* 126 */
			new MediumLeatherShield(),			/* 127 */
			new LargeLeatherShield(),			/* 128 */
			new SmallMetalShield(),				/* 129 */
			new MediumMetalShield(),			/* 130 */
			new LargeMetalShield(),				/* 131 */
			new RingOfStrength(),				/* 132 */
			new RingOfDexterity(),				/* 133 */
			new RingOfConstitution(),			/* 134 */
			new RingOfIntelligence(),			/* 135 */
			new RingOfSpeed(),					/* 136 */
			new RingOfSearching(),				/* 137 */
			new RingOfTeleportation(),			/* 138 */
			new RingOfSlowDigestion(),			/* 139 */
			new RingOfResistFire(),				/* 140 */
			new RingOfResistCold(),				/* 141 */
			new RingOfFeatherFalling(),			/* 142 */
			new RingOfAdornment(),				/* 143 */
			/* was a ring of adornment, subval = 12 here */
			new Arrow2(),						/* 144 */
			new RingOfWeakness(),				/* 145 */
			new RingOfLordlyFireProtection(),	/* 146 */
			new RingOfLordlyAcidProtection(),	/* 147 */
			new RingOfLordlyColdProtection(),	/* 148 */
			new RingOfWoe(),					/* 149 */
			new RingOfStupidity(),				/* 150 */
			new RingOfIncreaseDamage(),			/* 151 */
			new RingOfIncreaseToHit(),			/* 152 */
			new RingOfProtection(),				/* 153 */
			new RingOfAggravateMonster(),		/* 154 */
			new RingOfSeeInvisible(),			/* 155 */
			new RingOfSustainStrength(),		/* 156 */
			new RingOfSustainIntelligence(),	/* 157 */
			new RingOfSustainWisdom(),			/* 158 */
			new RingOfSustainConstitution(),	/* 159 */
			new RingOfSustainDexterity(),		/* 160 */
			new RingOfSustainCharisma(),		/* 161 */
			new RingOfSlaying(),				/* 162 */
			new AmuletOfWisdom(),				/* 163 */
			new AmuletOfCharisma(),				/* 164 */
			new AmuletOfSearching(),			/* 165 */
			new AmuletOfTeleportation(),		/* 166 */
			new AmuletOfSlowDigestion(),		/* 167 */
			new AmuletOfResistAcid(),			/* 168 */
			new AmuletOfAdornment(),			/* 169 */
			/* was an amulet of adornment here, subval = 7 */
			new Bolt2(),						/* 170 */
			new AmuletOftheMagi(),				/* 171 */
			new AmuletOfDoom(),					/* 172 */
			new ScrollOfEnchantWeaponToHit(),	/* 173 */
			new ScrollOfEnchantWeaponToDam(),	/* 174 */
			new ScrollOfEnchantArmor(),			/* 175 */
			new ScrollOfIdentify(),				/* 176 */
			new ScrollOfIdentify2(),			/* 177 */
			new ScrollOfIdentify3(),			/* 178 */
			new ScrollOfIdentify4(),			/* 179 */
			new ScrollOfRemoveCurse(),			/* 180 */
			new ScrollOfLight(),				/* 181 */
			new ScrollOfLight2(),				/* 182 */
			new ScrollOfLight3(),				/* 183 */
			new ScrollOfSummonMonster(),		/* 184 */
			new ScrollOfPhaseDoor(),			/* 185 */
			new ScrollOfTeleport(),				/* 186 */
			new ScrollOfTeleportLevel(),		/* 187 */
			new ScrollOfMonsterConfusion(),		/* 188 */
			new ScrollOfMagicMapping(),			/* 189 */
			new ScrollOfSleepMonster(),			/* 190 */
			new ScrollOfRuneOfProtection(),		/* 191 */
			new ScrollOfTreasureDetection(),	/* 192 */
			new ScrollOfObjectDetection(),		/* 193 */
			new ScrollOfTrapDetection(),		/* 194 */
			new ScrollOfTrapDetection2(),		/* 195 */
			new ScrollOfTrapDetection3(),		/* 196 */
			new ScrollOfDoorStairLocation(),	/* 197 */
			new ScrollOfDoorStairLocation2(),	/* 198 */
			new ScrollOfDoorStairLocation3(),	/* 199 */
			new ScrollOfMassGenocide(),			/* 200 */
			new ScrollOfDetectInvisible(),		/* 201 */
			new ScrollOfAggravateMonster(),		/* 202 */
			new ScrollOfTrapCreation(),			/* 203 */
			new ScrollOfTrapDoorDestruction(),	/* 204 */
			new ScrollOfDoorCreation(),			/* 205 */
			new ScrollOfRecharging(),			/* 206 */
			new ScrollOfGenocide(),				/* 207 */
			new ScrollOfDarkness(),				/* 208 */
			new ScrollOfProtectionfromEvil(),	/* 209 */
			new ScrollOfCreateFood(),			/* 210 */
			new ScrollOfDispelUndead(),			/* 211 */
			new ScrollOfEnchantWeapon(),		/* 212 */
			new ScrollOfCurseWeapon(),			/* 213 */
			new ScrollOfEnchantArmor2(),		/* 214 */
			new ScrollOfCurseArmor(),			/* 215 */
			new ScrollOfSummonUndead(),			/* 216 */
			new ScrollOfBlessing(),				/* 217 */
			new ScrollOfHolyChant(),			/* 218 */
			new ScrollOfHolyPrayer(),			/* 219 */
			new ScrollOfWordOfRecall(),			/* 220 */
			new ScrollOfDestruction(),			/* 221 */
			/* SMJ, AJ, Water must be subval 64-66 resp. for objdes to work */
			new PotionOfSlimeMoldJuice(),		/* 222 */
			new PotionOfAppleJuice(),			/* 223 */
			new PotionOfWater(),				/* 224 */
			new PotionOfStrength(),				/* 225 */
			new PotionOfWeakness(),				/* 226 */
			new PotionOfRestoreStrength(),		/* 227 */
			new PotionOfIntelligence(),			/* 228 */
			new PotionOfLoseIntelligence(),		/* 229 */
			new PotionOfRestoreIntelligence(),	/* 230 */
			new PotionOfWisdom(),				/* 231 */
			new PotionOfLoseWisdom(),			/* 232 */
			new PotionOfRestoreWisdom(),		/* 233 */
			new PotionOfCharisma(),				/* 234 */
			new PotionOfUgliness(),				/* 235 */
			new PotionOfRestoreCharisma(),		/* 236 */
			new PotionOfCureLightWounds(),		/* 237 */
			new PotionOfCureLightWounds2(),		/* 238 */
			new PotionOfCureLightWounds3(),		/* 239 */
			new PotionOfCureSeriousWounds(),	/* 240 */
			new PotionOfCureCriticalWounds(),	/* 241 */
			new PotionOfHealing(),				/* 242 */
			new PotionOfConstitution(),			/* 243 */
			new PotionOfGainExperience(),		/* 244 */
			new PotionOfSleep(),				/* 245 */
			new PotionOfBlindness(),			/* 246 */
			new PotionOfConfusion(),			/* 247 */
			new PotionOfPoison(),				/* 248 */
			new PotionOfHasteSelf(),			/* 249 */
			new PotionOfSlowness(),				/* 250 */
			new PotionOfDexterity(),			/* 251 */
			new PotionOfRestoreDexterity(),		/* 252 */
			new PotionOfRestoreConstitution(),	/* 253 */
			new PotionOfLoseExperience(),		/* 254 */
			new PotionOfSaltWater(),			/* 255 */
			new PotionOfInvulnerability(),		/* 256 */
			new PotionOfHeroism(),				/* 257 */
			new PotionOfSuperHeroism(),			/* 258 */
			new PotionOfBoldness(),				/* 259 */
			new PotionOfRestoreLifeLevels(),	/* 260 */
			new PotionOfResistHeat(),			/* 261 */
			new PotionOfResistCold(),			/* 262 */
			new PotionOfDetectInvisible(),		/* 263 */
			new PotionOfSlowPoison(),			/* 264 */
			new PotionOfNeutralizePoison(),		/* 265 */
			new PotionOfRestoreMana(),			/* 266 */
			new PotionOfInfraVision(),			/* 267 */
			new FlaskOfOil(),					/* 268 */
			new WandOfLight(),					/* 269 */
			new WandOfLightningBolts(),			/* 270 */
			new WandOfFrostBolts(),				/* 271 */
			new WandOfFireBolts(),				/* 272 */
			new WandOfStonetoMud(),				/* 273 */
			new WandOfPolymorph(),				/* 274 */
			new WandOfHealMonster(),			/* 275 */
			new WandOfHasteMonster(),			/* 276 */
			new WandOfSlowMonster(),			/* 277 */
			new WandOfConfuseMonster(),			/* 278 */
			new WandOfSleepMonster(),			/* 279 */
			new WandOfDrainLife(),				/* 280 */
			new WandOfTrapDoorDestruction(),	/* 281 */
			new WandOfMagicMissile(),			/* 282 */
			new WandOfWallBuilding(),			/* 283 */
			new WandOfCloneMonster(),			/* 284 */
			new WandOfTeleportAway(),			/* 285 */
			new WandOfDisarming(),				/* 286 */
			new WandOfLightningBalls(),			/* 287 */
			new WandOfColdBalls(),				/* 288 */
			new WandOfFireBalls(),				/* 289 */
			new WandOfStinkingCloud(),			/* 290 */
			new WandOfAcidBalls(),				/* 291 */
			new WandOfWonder(),					/* 292 */
			new StaffOfLight(),					/* 293 */
			new StaffOfDoorStairLocation(),		/* 294 */
			new StaffOfTrapLocation(),			/* 295 */
			new StaffOfTreasureLocation(),		/* 296 */
			new StaffOfObjectLocation(),		/* 297 */
			new StaffOfTeleportation(),			/* 298 */
			new StaffOfEarthquakes(),			/* 299 */
			new StaffOfSummoning(),				/* 300 */
			new StaffOfSummoning2(),			/* 301 */
			new StaffOfDestruction(),			/* 302 */
			new StaffOfStarlight(),				/* 303 */
			new StaffOfHasteMonsters(),			/* 304 */
			new StaffOfSlowMonsters(),			/* 305 */
			new StaffOfSleepMonsters(),			/* 306 */
			new StaffOfCureLightWounds(),		/* 307 */
			new StaffOfDetectInvisible(),		/* 308 */
			new StaffOfSpeed(),					/* 309 */
			new StaffOfSlowness(),				/* 310 */
			new StaffOfMassPolymorph(),			/* 311 */
			new StaffOfRemoveCurse(),			/* 312 */
			new StaffOfDetectEvil(),			/* 313 */
			new StaffOfCuring(),				/* 314 */
			new StaffOfDispelEvil(),			/* 315 */
			new StaffOfDarkness(),				/* 316 */
			new StaffOfDarkness2(),				/* 317 */
			new BeginnersMagick(),				/* 318 */
			new MagickI(),						/* 319 */
			new MagickII(),						/* 320 */
			new TheMagesGuideToPower(),			/* 321 */
			new BeginnersHandbook(),			/* 322 */
			new WordsOfWisdom(),				/* 323 */
			new ChantsAndBlessings(),			/* 324 */
			new ExorcismsAndDispellings(),		/* 325 */
			new SmallWoodenChest(),				/* 326 */
			new LargeWoodenChest(),				/* 327 */
			new SmallIronChest(),				/* 328 */
			new LargeIronChest(),				/* 329 */
			new SmallSteelChest(),				/* 330 */
			new LargeSteelChest(),				/* 331 */
			new RatSkeleton(),					/* 332 */
			new GiantCentipedeSkeleton(),		/* 333 */
			new FilthyRags(),					/* 334 */
			new EmptyBottle(),					/* 335 */
			new ShardsOfPottery(),				/* 336 */
			new HumanSkeleton(),				/* 337 */
			new DwarfSkeleton(),				/* 338 */
			new ElfSkeleton(),					/* 339 */
			new GnomeSkeleton(),				/* 340 */
			new BrokenSetOfTeeth(),				/* 341 */
			new LargeBrokenBone(),				/* 342 */
			new BrokenStick(),					/* 343 */
			/* end of Dungeon items */
			/* Store items, which are not also dungeon items, some of these can be
			   found above, except that the number is >1 below */
			new RationOfFood4(),				/* 344 */
			new HardBiscuit(),					/* 345 */
			new StripOfBeefJerky(),				/* 346 */
			new PintOfFineAle(),				/* 347 */
			new PintOfFineWine(),				/* 348 */
			new Pick(),							/* 349 */
			new Shovel(),						/* 350 */
			new ScrollOfIdentify5(),			/* 351 */
			new ScrollOfLight4(),				/* 352 */
			new ScrollOfPhaseDoor2(),			/* 353 */
			new ScrollOfMagicMapping2(),		/* 354 */
			new ScrollOfTreasureDetection2(),	/* 355 */
			new ScrollOfObjectDetection2(),		/* 356 */
			new ScrollOfDetectInvisible2(),		/* 357 */
			new ScrollOfBlessing2(),			/* 358 */
			new ScrollOfWordOfRecall2(),		/* 359 */
			new PotionOfCureLightWounds4(),		/* 360 */
			new PotionOfHeroism2(),				/* 361 */
			new PotionOfBoldness2(),			/* 362 */
			new PotionOfSlowPoison2(),			/* 363 */
			new BrassLantern2(),				/* 364 */
			new WoodenTorch2(),					/* 365 */
			new FlaskOfOil2(),					/* 366 */
			/* end store items */
			/* start doors */
			/* Secret door must have same subval as closed door in	*/
			/* TRAP_LISTB.	See CHANGE_TRAP. Must use & because of stone_to_mud. */
			new OpenDoor(),						/* 367 */
			new ClosedDoor(),					/* 368 */
			new SecretDoor(),					/* 369 */
			/* end doors */
			/* stairs */
			new UpStaircase(),					/* 370 */
			new DownStaircase(),				/* 371 */
			/* store door */
			/* Stores are just special traps		*/
			new GeneralStore(),					/* 372 */
			new Armory(),						/* 373 */
			new WeaponSmiths(),					/* 374 */
			new Temple(),						/* 375 */
			new AlchemyShop(),					/* 376 */
			new MagicShop(),					/* 377 */
			/* end store door */
			/* Traps are just Nasty treasures.				*/
			/* Traps: Level represents the relative difficulty of disarming;	*/
			/* and P1 represents the experienced gained when disarmed*/
			new OpenPit(),						/* 378 */
			new ArrowTrap(),					/* 379 */
			new CoveredPit(),					/* 380 */
			new TrapDoor(),						/* 381 */
			new GasTrap(),						/* 382 */
			new LooseRock(),					/* 383 */
			new DartTrap(),						/* 384 */
			new StrangeRune(),					/* 385 */
			new LooseRock2(),					/* 386 */
			new GasTrap2(),						/* 387 */
			new StrangeRune2(),					/* 388 */
			new BlackenedSpot(),				/* 389 */
			new CorrodedRock(),					/* 390 */
			new GasTrap3(),						/* 391 */
			new GasTrap4(),						/* 392 */
			new GasTrap5(),						/* 393 */
			new DartTrap2(),					/* 394 */
			new DartTrap3(),					/* 395 */
			/* rubble */
			new Rubble(),						/* 396 */
			/* mush */
			new PintOfFineGradeMush(),			/* 397 */
			/* Special trap	*/
			new StrangeRune3(),					/* 398 */
			/* Gold list (All types of gold and gems are defined here)	*/
			new Copper(),						/* 399 */
			new Copper2(),						/* 400 */
			new Copper3(),						/* 401 */
			new Silver(),						/* 402 */
			new Silver2(),						/* 403 */
			new Silver3(),						/* 404 */
			new Garnets(),						/* 405 */
			new Garnets2(),						/* 406 */
			new Gold(),							/* 407 */
			new Gold2(),						/* 408 */
			new Gold3(),						/* 409 */
			new Opals(),						/* 410 */
			new Sapphires(),					/* 411 */
			new Gold4(),						/* 412 */
			new Rubies(),						/* 413 */
			new Diamonds(),						/* 414 */
			new Emeralds(),						/* 415 */
			new Mithril(),						/* 416 */
			/* nothing, used as inventory place holder */
			/* must be stackable, so that can be picked up by inven_carry */
			new Nothing(),						/* 417 */
			/* these next two are needed only for the names */
			new RuinedChest(),					/* 418 */
			new Nothing2()						/* 419 */
	};
	
	//char *special_names[SN_ARRAY_SIZE]
	public String[] special_names = {
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
	public int[] sorted_objects = new int[Constants.MAX_DUNGEON_OBJ];
	
	/* Identified objects flags					*/
	public int[] object_ident = new int[Constants.OBJECT_IDENT_SIZE];
	public int[] t_level = new int[Constants.MAX_OBJ_LEVEL + 1];
	public InvenType[] t_list = new InvenType[Constants.MAX_TALLOC];
	public InvenType[] inventory = new InvenType[Constants.INVEN_ARRAY_SIZE];
	
	/* Treasure related values					*/
	public int inven_ctr = 0;		/* Total different obj's	*/
	public int inven_weight = 0;	/* Cur carried weight	*/
	public int equip_ctr = 0;		/* Cur equipment ctr	*/
	public int tcptr;				/* Cur treasure heap ptr	*/
}
