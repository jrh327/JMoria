/*
 * Constants.java: global constants used by Moria
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

public class Constants {
	
	/* Note to the Wizard:                  - RAK -              */
	/*  Tweaking these constants can *GREATLY* change the game.  */
	/*  Two years of constant tuning have generated these        */
	/*  values.  Minor adjustments are encouraged, but you must  */
	/*  be very careful not to unbalance the game.  Moria was    */
	/*  meant to be challenging, not a give away.  Many          */
	/*  adjustments can cause the game to act strangely, or even */
	/*  cause errors.                                            */
	
	/*Addendum:                         - JEW -
	 *  I have greatly expanded the number of defined constants.  However, if
	 *  you change anything below, without understanding EXACTLY how the game
	 *  uses the number, the program may stop working correctly.  Modify the
	 *  constants at your own risk. */
	
	//Constant.h should always be included after config.h, because it uses
	//some of the system defines set up there.
	
	/* Current version number of Moria				*/
	public static final int CUR_VERSION_MAJ = 5; /* version 5.5 */
	public static final int CUR_VERSION_MIN = 6;
	public static final int PATCH_LEVEL = 0;
	
	public static final int TRUE = 1;
	public static final int FALSE = 0;
	
	public static final short MAX_UCHAR = 255;
	public static final short MAX_SHORT = 32767; /* maximum short/long signed ints */
	public static final long MAX_LONG   = 0x7FFFFFFFL;
	
	/* Changing values below this line may be hazardous to your health! */
	
	/* message line location */
	public static final int MSG_LINE = 0;
	
	/* number of messages to save in a buffer */
	public static final int MAX_SAVE_MSG = 22; /* How many messages to save -CJS- */
	
	public static final int VTYPESIZ    = 80;
	public static final int BIGVTYPESIZ = 160;
	public static final int STATTYPESIZ = 7;
	
	/* Dungeon size parameters */
	public static final int MAX_HEIGHT    = 66; /* Multiple of 11; >= 22 */
	public static final int MAX_WIDTH     = 198; /* Multiple of 33; >= 66 */
	public static final int SCREEN_HEIGHT = 22;
	public static final int SCREEN_WIDTH  = 66;
	public static final int QUART_HEIGHT  = (SCREEN_HEIGHT / 4);
	public static final int QUART_WIDTH   = (SCREEN_WIDTH / 4);
	
	/* Dungeon generation values */
	/* Note: The entire design of dungeon can be changed by only */
	/*   slight adjustments here. */
	public static final int DUN_TUN_RND = 9;   /* 1/Chance of Random direction */
	public static final int DUN_TUN_CHG = 70;  /* Chance of changing direction (99 max) */
	public static final int DUN_TUN_CON = 15;  /* Chance of extra tunneling */
	public static final int DUN_ROO_MEA = 32;  /* Mean of # of rooms, standard dev2 */
	public static final int DUN_TUN_PEN = 25;  /* % chance of room doors */
	public static final int DUN_TUN_JCT = 15;  /* % chance of doors at tunnel junctions */
	public static final int DUN_STR_DEN = 5;   /* Density of streamers */
	public static final int DUN_STR_RNG = 2;   /* Width of streamers */
	public static final int DUN_STR_MAG = 3;   /* Number of magma streamers */
	public static final int DUN_STR_MC  = 90;  /* 1/x chance of treasure per magma */
	public static final int DUN_STR_QUA = 2;   /* Number of quartz streamers */
	public static final int DUN_STR_QC  = 40;  /* 1/x chance of treasure per quartz */
	public static final int DUN_UNUSUAL = 300; /* Level/x chance of unusual room */
	
	/* Store constants */
	public static final int MAX_OWNERS        = 18;  /* Number of owners to choose from */
	public static final int MAX_STORES        = 6;   /* Number of different stores */
	public static final int STORE_INVEN_MAX   = 24;  /* Max number of discrete objs in inven */
	public static final int STORE_CHOICES     = 26;  /* NUMBER of items to choose stock from */
	public static final int STORE_MAX_INVEN   = 18;  /* Max diff objs in stock for auto buy */
	public static final int STORE_MIN_INVEN   = 10;  /* Min diff objs in stock for auto sell */
	public static final int STORE_TURN_AROUND = 9;   /* Amount of buying and selling normally */
	public static final int COST_ADJ          = 100; /* Adjust prices for buying and selling */
	public static final int STORE_TURNOVER    = 1000; /* Number of turns before stores get new stock */
	
	/* Treasure constants */
	public static final int INVEN_ARRAY_SIZE = 34; /* Size of inventory array(Do not change) */
	public static final int MAX_OBJ_LEVEL    = 50; /* Maximum level of magic in dungeon */
	public static final int OBJ_GREAT        = 12; /* 1/n Chance of item being a Great Item */
	
	/* Note that the following constants are all related, if you change one,
	 * you must also change all succeeding ones.  Also, player_init[] and
	 * store_choice[] may also have to be changed. */
	public static final int MAX_OBJECTS       = 420; /* Number of objects for universe */
	public static final int MAX_DUNGEON_OBJ   = 344; /* Number of dungeon objects */
	public static final int OBJ_OPEN_DOOR     = 367;
	public static final int OBJ_CLOSED_DOOR   = 368;
	public static final int OBJ_SECRET_DOOR   = 369;
	public static final int OBJ_UP_STAIR      = 370;
	public static final int OBJ_DOWN_STAIR    = 371;
	public static final int OBJ_STORE_DOOR    = 372;
	public static final int OBJ_TRAP_LIST     = 378;
	public static final int OBJ_RUBBLE        = 396;
	public static final int OBJ_MUSH          = 397;
	public static final int OBJ_SCARE_MON     = 398;
	public static final int OBJ_GOLD_LIST     = 399;
	public static final int OBJ_NOTHING       = 417;
	public static final int OBJ_RUINED_CHEST  = 418;
	public static final int OBJ_WIZARD        = 419;
	public static final int OBJECT_IDENT_SIZE = 448; /* 7*64, see object_offset() in desc.c,
													    could be MAX_OBJECTS o_o() rewritten */
	public static final int MAX_GOLD = 18; /* Number of different types of gold */
	
	/* with MAX_TALLOC 150, it is possible to get compacting objects during
	 * level generation, although it is extremely rare */
	public static final int MAX_TALLOC       = 175; /* Max objects per level */
	public static final int MIN_TRIX         = 1;   /* Minimum t_list index used */
	public static final int TREAS_ROOM_ALLOC = 7;   /* Amount of objects for rooms */
	public static final int TREAS_ANY_ALLOC  = 2;   /* Amount of objects for corridors */
	public static final int TREAS_GOLD_ALLOC = 2;   /* Amount of gold (and gems) */
	
	/* Magic Treasure Generation constants */
	/* Note: Number of special objects, and degree of enchantments */
	/*       can be adjusted here. */
	public static final int OBJ_STD_ADJ     = 125; /* Adjust STD per level * 100 */
	public static final int OBJ_STD_MIN     = 7;   /* Minimum STD */
	public static final int OBJ_TOWN_LEVEL  = 7;   /* Town object generation level */
	public static final int OBJ_BASE_MAGIC  = 15;  /* Base amount of magic */
	public static final int OBJ_BASE_MAX    = 70;  /* Max amount of magic */
	public static final int OBJ_DIV_SPECIAL = 6;   /* magic_chance/#    special magic */
	public static final int OBJ_DIV_CURSED  = 13;  /* 10*magic_chance/# cursed items */
	
	/* Constants describing limits of certain objects */
	public static final int OBJ_LAMP_MAX   = 15000; /* Maximum amount that lamp can be filled */
	public static final int OBJ_BOLT_RANGE = 18;    /* Maximum range of bolts and balls */
	public static final int OBJ_RUNE_PROT  = 3000;  /* Rune of protection resistance */
	
	/* Creature constants */
	public static final int MAX_CREATURES = 279; /* Number of creatures defined for univ */
	public static final int N_MONS_ATTS   = 215; /* Number of monster attack types. */
	
	/* with MAX_MALLOC 101, it is possible to get compacting monsters messages
	 * while breeding/cloning monsters */
	public static final int MAX_MALLOC        = 125; /* Max that can be allocated */
	public static final int MAX_MALLOC_CHANCE = 160; /* 1/x chance of new monster each round */
	public static final int MAX_MONS_LEVEL    = 40;  /* Maximum level of creatures */
	public static final int MAX_SIGHT         = 20;  /* Maximum dis a creature can be seen */
	public static final int MAX_SPELL_DIS     = 20;  /* Maximum dis creat. spell can be cast */
	public static final int MAX_MON_MULT      = 75;  /* Maximum reproductions on a level */
	public static final int MON_MULT_ADJ      = 7;   /* High value slows multiplication */
	public static final int MON_NASTY         = 50;  /* 1/x chance of high level creat */
	public static final int MIN_MALLOC_LEVEL  = 14;  /* Minimum number of monsters/level */
	public static final int MIN_MALLOC_TD     = 4;   /* Number of people on town level (day) */
	public static final int MIN_MALLOC_TN     = 8;   /* Number of people on town level (night) */
	public static final int WIN_MON_TOT       = 2;   /* Total number of "win" creatures */
	public static final int WIN_MON_APPEAR    = 50;  /* Level where winning creatures begin */
	public static final int MON_SUMMON_ADJ    = 2;   /* Adjust level of summoned creatures */
	public static final int MON_DRAIN_LIFE    = 2;   /* Percent of player exp drained per hit */
	public static final int MAX_MON_NATTACK   = 4;   /* Max num atts (used in mons memory) -CJS- */
	public static final int MIN_MONIX         = 2;   /* Min index in m_list (1 = py, 0 = no mon) */

	/* Trap constants */
	public static final int MAX_TRAP = 18; /* Number of defined traps */
	
	public static final int SCARE_MONSTER = 99;
	
	/* Descriptive constants */
	public static final int MAX_COLORS    = 49;  /* Used with potions */
	public static final int MAX_MUSH      = 22;  /* Used with mushrooms */
	public static final int MAX_WOODS     = 25;  /* Used with staffs */
	public static final int MAX_METALS    = 25;  /* Used with wands */
	public static final int MAX_ROCKS     = 32;  /* Used with rings */
	public static final int MAX_AMULETS   = 11;  /* Used with amulets */
	public static final int MAX_TITLES    = 45;  /* Used with scrolls */
	public static final int MAX_SYLLABLES = 153; /* Used with scrolls */
	
	/* Player constants */
	public static final int MAX_PLAYER_LEVEL    = 40;      /* Maximum possible character level */
	public static final int MAX_EXP             = 9999999; /* Maximum amount of experience -CJS- */
	public static final int MAX_RACES           = 8;       /* Number of defined races */
	public static final int MAX_CLASS           = 6;       /* Number of defined classes */
	public static final int USE_DEVICE          = 3;       /* x> Harder devices x< Easier devices */
	public static final int MAX_BACKGROUND      = 128;     /* Number of types of histories for univ */
	public static final int PLAYER_FOOD_FULL    = 10000;   /* Getting full */
	public static final int PLAYER_FOOD_MAX     = 15000;   /* Maximum food value, beyond is wasted */
	public static final int PLAYER_FOOD_FAINT   = 300;     /* Character begins fainting */
	public static final int PLAYER_FOOD_WEAK    = 1000;    /* Warn player that he is getting very low */
	public static final int PLAYER_FOOD_ALERT   = 2000;    /* Warn player that he is getting low */
	public static final int PLAYER_REGEN_FAINT  = 33;      /* Regen factor*2^16 when fainting */
	public static final int PLAYER_REGEN_WEAK   = 98;      /* Regen factor*2^16 when weak */
	public static final int PLAYER_REGEN_NORMAL = 197;     /* Regen factor*2^16 when full */
	public static final int PLAYER_REGEN_HPBASE = 1442;    /* Min amount hp regen*2^16 */
	public static final int PLAYER_REGEN_MNBASE = 524;     /* Min amount mana regen*2^16 */
	public static final int PLAYER_WEIGHT_CAP   = 130;     /* "#"*(1/10 pounds) per strength point */
	public static final int PLAYER_EXIT_PAUSE   = 2;       /* Pause time before player can re-roll */
	
	/* class level adjustment constants */
	public static final int CLA_BTH    = 0;
	public static final int CLA_BTHB   = 1;
	public static final int CLA_DEVICE = 2;
	public static final int CLA_DISARM = 3;
	public static final int CLA_SAVE   = 4;
	/* this depends on the fact that CLA_SAVE values are all the same, if not,
	 * then should add a separate column for this */
	public static final int CLA_MISC_HIT = 4;
	public static final int MAX_LEV_ADJ  = 5;
	
	/* Base to hit constants */
	public static final int BTH_PLUS_ADJ = 3; /* Adjust BTH per plus-to-hit */
	
	/* magic numbers for players inventory array */
	public static final int INVEN_WIELD = 22; /* must be first item in equipment list */
	public static final int INVEN_HEAD  = 23;
	public static final int INVEN_NECK  = 24;
	public static final int INVEN_BODY  = 25;
	public static final int INVEN_ARM   = 26;
	public static final int INVEN_HANDS = 27;
	public static final int INVEN_RIGHT = 28;
	public static final int INVEN_LEFT  = 29;
	public static final int INVEN_FEET  = 30;
	public static final int INVEN_OUTER = 31;
	public static final int INVEN_LIGHT = 32;
	public static final int INVEN_AUX   = 33;
	
	/* Attribute indexes -CJS- */
	public static final int A_STR = 0;
	public static final int A_INT = 1;
	public static final int A_WIS = 2;
	public static final int A_DEX = 3;
	public static final int A_CON = 4;
	public static final int A_CHR = 5;
	
	/* some systems have a non-ANSI definition of this, so undef it first */
	//#define CTRL(x)		(x & 0x1F)
	public static final int CTRL = 0x1F;
	//#define DELETE		0x7f
	public static final int DELETE = 0x7F;
	public static final int KEY_LEFT = 37;
	public static final int KEY_UP = 38;
	public static final int KEY_RIGHT = 39;
	public static final int KEY_DOWN = 40;
	//#ifdef VMS
	//#define ESCAPE        '\032'	/* Use CTRL-Z instead of ESCAPE.  */
	//#else
	//#define ESCAPE	      '\033'	/* ESCAPE character -CJS-  */
	public static final char ESCAPE = '\033';
	//#endif
	
	/* This used to be NULL, but that was technically incorrect.  CNIL is used
	 * instead of null to help avoid lint errors.  */
	public static final char CNIL = '\0';
	
	/* Fval definitions: these describe the various types of dungeon floors and
	 * walls, if numbers above 15 are ever used, then the test against
	 * MIN_CAVE_WALL will have to be changed, also the save routines will have
	 * to be changed. */
	public static final int NULL_WALL      = 0;
	public static final int DARK_FLOOR     = 1;
	public static final int LIGHT_FLOOR    = 2;
	public static final int MAX_CAVE_ROOM  = 2;
	public static final int CORR_FLOOR     = 3;
	public static final int BLOCKED_FLOOR  = 4; /* a corridor space with cl/st/se door or rubble */
	public static final int MAX_CAVE_FLOOR = 4;
	
	public static final int MAX_OPEN_SPACE   = 3;
	public static final int MIN_CLOSED_SPACE = 4;
	
	public static final int TMP1_WALL = 8;
	public static final int TMP2_WALL = 9;
	
	public static final int MIN_CAVE_WALL = 12;
	public static final int GRANITE_WALL  = 12;
	public static final int MAGMA_WALL    = 13;
	public static final int QUARTZ_WALL   = 14;
	public static final int BOUNDARY_WALL = 15;
	
	/* Column for stats    */
	public static final int STAT_COLUMN = 0;
	
	/* Class spell types */
	public static final int NONE   = 0;
	public static final int MAGE   = 1;
	public static final int PRIEST = 2;
	
	/* offsets to spell names in spell_names[] array */
	public static final int SPELL_OFFSET  = 0;
	public static final int PRAYER_OFFSET = 31;
	
	/* definitions for the psuedo-normal distribution generation */
	public static final int NORMAL_TABLE_SIZE = 256;
	public static final int NORMAL_TABLE_SD   = 64;  /* the standard deviation for the table */
	
	/* definitions for the player's status field */
	public static final int PY_HUNGRY   = 0x00000001;
	public static final int PY_WEAK     = 0x00000002;
	public static final int PY_BLIND    = 0x00000004;
	public static final int PY_CONFUSED = 0x00000008;
	public static final int PY_FEAR     = 0x00000010;
	public static final int PY_POISONED = 0x00000020;
	public static final int PY_FAST     = 0x00000040;
	public static final int PY_SLOW     = 0x00000080;
	public static final int PY_SEARCH   = 0x00000100;
	public static final int PY_REST     = 0x00000200;
	public static final int PY_STUDY    = 0x00000400;
	
	public static final int PY_INVULN    = 0x00001000;
	public static final int PY_HERO      = 0x00002000;
	public static final int PY_SHERO     = 0x00004000;
	public static final int PY_BLESSED   = 0x00008000;
	public static final int PY_DET_INV   = 0x00010000;
	public static final int PY_TIM_INFRA = 0x00020000;
	public static final int PY_SPEED     = 0x00040000;
	public static final int PY_STR_WGT   = 0x00080000;
	public static final int PY_PARALYSED = 0x00100000;
	public static final int PY_REPEAT    = 0x00200000;
	public static final int PY_ARMOR     = 0x00400000;
	
	public static final int PY_STATS = 0x3F000000;
	public static final int PY_STR   = 0x01000000; /* these 6 stat flags must be adjacent */
	public static final int PY_INT   = 0x02000000;
	public static final int PY_WIS   = 0x04000000;
	public static final int PY_DEX   = 0x08000000;
	public static final int PY_CON   = 0x10000000;
	public static final int PY_CHR   = 0x20000000;
	
	public static final int PY_HP   = 0x40000000;
	public static final int PY_MANA = 0x80000000;
	
	/* definitions for objects that can be worn */
	public static final int TR_STATS       = 0x0000003F; /* the stats must be the low 6 bits */
	public static final int TR_STR         = 0x00000001;
	public static final int TR_INT         = 0x00000002;
	public static final int TR_WIS         = 0x00000004;
	public static final int TR_DEX         = 0x00000008;
	public static final int TR_CON         = 0x00000010;
	public static final int TR_CHR         = 0x00000020;
	public static final int TR_SEARCH      = 0x00000040;
	public static final int TR_SLOW_DIGEST = 0x00000080;
	public static final int TR_STEALTH     = 0x00000100;
	public static final int TR_AGGRAVATE   = 0x00000200;
	public static final int TR_TELEPORT    = 0x00000400;
	public static final int TR_REGEN       = 0x00000800;
	public static final int TR_SPEED       = 0x00001000;
	
	public static final int TR_EGO_WEAPON   = 0x0007E000;
	public static final int TR_SLAY_DRAGON  = 0x00002000;
	public static final int TR_SLAY_ANIMAL  = 0x00004000;
	public static final int TR_SLAY_EVIL    = 0x00008000;
	public static final int TR_SLAY_UNDEAD  = 0x00010000;
	public static final int TR_FROST_BRAND  = 0x00020000;
	public static final int TR_FLAME_TONGUE = 0x00040000;
	
	public static final int TR_RES_FIRE  = 0x00080000;
	public static final int TR_RES_ACID  = 0x00100000;
	public static final int TR_RES_COLD  = 0x00200000;
	public static final int TR_SUST_STAT = 0x00400000;
	public static final int TR_FREE_ACT  = 0x00800000;
	public static final int TR_SEE_INVIS = 0x01000000;
	public static final int TR_RES_LIGHT = 0x02000000;
	public static final int TR_FFALL     = 0x04000000;
	public static final int TR_BLIND     = 0x08000000;
	public static final int TR_TIMID     = 0x10000000;
	public static final int TR_TUNNEL    = 0x20000000;
	public static final int TR_INFRA     = 0x40000000;
	public static final int TR_CURSED    = 0x80000000;
	
	/* definitions for chests */
	public static final int CH_LOCKED    = 0x00000001;
	public static final int CH_TRAPPED   = 0x000001F0;
	public static final int CH_LOSE_STR  = 0x00000010;
	public static final int CH_POISON    = 0x00000020;
	public static final int CH_PARALYSED = 0x00000040;
	public static final int CH_EXPLODE   = 0x00000080;
	public static final int CH_SUMMON    = 0x00000100;
	
	/* definitions for creatures, cmove field */
	public static final int CM_ALL_MV_FLAGS = 0x0000003F;
	public static final int CM_ATTACK_ONLY  = 0x00000001;
	public static final int CM_MOVE_NORMAL  = 0x00000002;
	/* For Quylthulgs, which have no physical movement.  */
	public static final int CM_ONLY_MAGIC   = 0x00000004;
	
	public static final int CM_RANDOM_MOVE = 0x00000038;
	public static final int CM_20_RANDOM   = 0x00000008;
	public static final int CM_40_RANDOM   = 0x00000010;
	public static final int CM_75_RANDOM   = 0x00000020;
	
	public static final int CM_SPECIAL    = 0x003F0000;
	public static final int CM_INVISIBLE  = 0x00010000;
	public static final int CM_OPEN_DOOR  = 0x00020000;
	public static final int CM_PHASE      = 0x00040000;
	public static final int CM_EATS_OTHER = 0x00080000;
	public static final int CM_PICKS_UP   = 0x00100000;
	public static final int CM_MULTIPLY   = 0x00200000;
	
	public static final int CM_SMALL_OBJ  = 0x00800000;
	public static final int CM_CARRY_OBJ  = 0x01000000;
	public static final int CM_CARRY_GOLD = 0x02000000;
	public static final int CM_TREASURE   = 0x7C000000;
	public static final int CM_TR_SHIFT   = 26; /* used for recall of treasure */
	public static final int CM_60_RANDOM  = 0x04000000;
	public static final int CM_90_RANDOM  = 0x08000000;
	public static final int CM_1D2_OBJ    = 0x10000000;
	public static final int CM_2D2_OBJ    = 0x20000000;
	public static final int CM_4D2_OBJ    = 0x40000000;
	public static final int CM_WIN        = 0x80000000;
	
	/* creature spell definitions */
	public static final int CS_FREQ       = 0x0000000F;
	public static final int CS_SPELLS     = 0x0001FFF0;
	public static final int CS_TEL_SHORT  = 0x00000010;
	public static final int CS_TEL_LONG   = 0x00000020;
	public static final int CS_TEL_TO     = 0x00000040;
	public static final int CS_LGHT_WND   = 0x00000080;
	public static final int CS_SER_WND    = 0x00000100;
	public static final int CS_HOLD_PER   = 0x00000200;
	public static final int CS_BLIND      = 0x00000400;
	public static final int CS_CONFUSE    = 0x00000800;
	public static final int CS_FEAR       = 0x00001000;
	public static final int CS_SUMMON_MON = 0x00002000;
	public static final int CS_SUMMON_UND = 0x00004000;
	public static final int CS_SLOW_PER   = 0x00008000;
	public static final int CS_DRAIN_MANA = 0x00010000;

	public static final int CS_BREATHE  = 0x00F80000; /* may also just indicate resistance */
	public static final int CS_BR_LIGHT = 0x00080000; /* if no spell frequency set */
	public static final int CS_BR_GAS   = 0x00100000;
	public static final int CS_BR_ACID  = 0x00200000;
	public static final int CS_BR_FROST = 0x00400000;
	public static final int CS_BR_FIRE  = 0x00800000;
	
	/* creature defense flags */
	public static final int CD_DRAGON   = 0x0001;
	public static final int CD_ANIMAL   = 0x0002;
	public static final int CD_EVIL     = 0x0004;
	public static final int CD_UNDEAD   = 0x0008;
	public static final int CD_WEAKNESS = 0x03F0;
	public static final int CD_FROST    = 0x0010;
	public static final int CD_FIRE     = 0x0020;
	public static final int CD_POISON   = 0x0040;
	public static final int CD_ACID     = 0x0080;
	public static final int CD_LIGHT    = 0x0100;
	public static final int CD_STONE    = 0x0200;

	public static final int CD_NO_SLEEP = 0x1000;
	public static final int CD_INFRA    = 0x2000;
	public static final int CD_MAX_HP   = 0x4000;
	
	/* inventory stacking subvals */
	/* these never stack */
	public static final int ITEM_NEVER_STACK_MIN = 0;
	public static final int ITEM_NEVER_STACK_MAX = 63;
	/* these items always stack with others of same subval, always treated as
	 * single objects, must be power of 2 */
	public static final int ITEM_SINGLE_STACK_MIN = 64;
	public static final int ITEM_SINGLE_STACK_MAX = 192;	/* see NOTE below */
	/* these items stack with others only if have same subval and same p1,
	 * they are treated as a group for wielding, etc. */
	public static final int ITEM_GROUP_MIN = 192;
	public static final int ITEM_GROUP_MAX = 255;
	/* NOTE: items with subval 192 are treated as single objects, but only stack
	 * with others of same subval if have the same p1 value, only used for
	 * torches */
	
	/* id's used for object description, stored in object_ident */
	public static final int OD_TRIED  = 0x1;
	public static final int OD_KNOWN1 = 0x2;
	
	/* id's used for item description, stored in i_ptr->ident */
	public static final int ID_MAGIK       = 0x1;
	public static final int ID_DAMD        = 0x2;
	public static final int ID_EMPTY       = 0x4;
	public static final int ID_KNOWN2      = 0x8;
	public static final int ID_STOREBOUGHT = 0x10;
	public static final int ID_SHOW_HITDAM = 0x20;
	public static final int ID_NOSHOW_P1   = 0x40;
	public static final int ID_SHOW_P1     = 0x80;

	/* indexes into the special name table */
	public static final int SN_NULL             = 0;
	public static final int SN_R                = 1;
	public static final int SN_RA               = 2;
	public static final int SN_RF               = 3;
	public static final int SN_RC               = 4;
	public static final int SN_RL               = 5;
	public static final int SN_HA               = 6;
	public static final int SN_DF               = 7;
	public static final int SN_SA               = 8;
	public static final int SN_SD               = 9;
	public static final int SN_SE               = 10;
	public static final int SN_SU               = 11;
	public static final int SN_FT               = 12;
	public static final int SN_FB               = 13;
	public static final int SN_FREE_ACTION      = 14;
	public static final int SN_SLAYING          = 15;
	public static final int SN_CLUMSINESS       = 16;
	public static final int SN_WEAKNESS         = 17;
	public static final int SN_SLOW_DESCENT     = 18;
	public static final int SN_SPEED            = 19;
	public static final int SN_STEALTH          = 20;
	public static final int SN_SLOWNESS         = 21;
	public static final int SN_NOISE            = 22;
	public static final int SN_GREAT_MASS       = 23;
	public static final int SN_INTELLIGENCE     = 24;
	public static final int SN_WISDOM           = 25;
	public static final int SN_INFRAVISION      = 26;
	public static final int SN_MIGHT            = 27;
	public static final int SN_LORDLINESS       = 28;
	public static final int SN_MAGI             = 29;
	public static final int SN_BEAUTY           = 30;
	public static final int SN_SEEING           = 31;
	public static final int SN_REGENERATION     = 32;
	public static final int SN_STUPIDITY        = 33;
	public static final int SN_DULLNESS         = 34;
	public static final int SN_BLINDNESS        = 35;
	public static final int SN_TIMIDNESS        = 36;
	public static final int SN_TELEPORTATION    = 37;
	public static final int SN_UGLINESS         = 38;
	public static final int SN_PROTECTION       = 39;
	public static final int SN_IRRITATION       = 40;
	public static final int SN_VULNERABILITY    = 41;
	public static final int SN_ENVELOPING       = 42;
	public static final int SN_FIRE             = 43;
	public static final int SN_SLAY_EVIL        = 44;
	public static final int SN_DRAGON_SLAYING   = 45;
	public static final int SN_EMPTY            = 46;
	public static final int SN_LOCKED           = 47;
	public static final int SN_POISON_NEEDLE    = 48;
	public static final int SN_GAS_TRAP         = 49;
	public static final int SN_EXPLOSION_DEVICE = 50;
	public static final int SN_SUMMONING_RUNES  = 51;
	public static final int SN_MULTIPLE_TRAPS   = 52;
	public static final int SN_DISARMED         = 53;
	public static final int SN_UNLOCKED         = 54;
	public static final int SN_SLAY_ANIMAL      = 55;
	public static final int SN_ARRAY_SIZE       = 56; /* must be at end of this list */
	
	/* defines for treasure type values (tval) */
	public static final int TV_NEVER   = -1; /* used by find_range() for non-search */
	public static final int TV_NOTHING = 0;
	public static final int TV_MISC    = 1;
	public static final int TV_CHEST   = 2;
	/* min tval for wearable items, all items between TV_MIN_WEAR and TV_MAX_WEAR
	 * use the same flag bits, see the TR_* defines */
	public static final int TV_MIN_WEAR = 10;
	/* items tested for enchantments, i.e. the MAGIK inscription, see the
	 * enchanted() procedure */
	public static final int TV_MIN_ENCHANT = 10;
	public static final int TV_SLING_AMMO  = 10;
	public static final int TV_BOLT        = 11;
	public static final int TV_ARROW       = 12;
	public static final int TV_SPIKE       = 13;
	public static final int TV_LIGHT       = 15;
	public static final int TV_BOW         = 20;
	public static final int TV_HAFTED      = 21;
	public static final int TV_POLEARM     = 22;
	public static final int TV_SWORD       = 23;
	public static final int TV_DIGGING     = 25;
	public static final int TV_BOOTS       = 30;
	public static final int TV_GLOVES      = 31;
	public static final int TV_CLOAK       = 32;
	public static final int TV_HELM        = 33;
	public static final int TV_SHIELD      = 34;
	public static final int TV_HARD_ARMOR  = 35;
	public static final int TV_SOFT_ARMOR  = 36;
	/* max tval that uses the TR_* flags */
	public static final int TV_MAX_ENCHANT = 39;
	public static final int TV_AMULET      = 40;
	public static final int TV_RING        = 45;
	/* max tval for wearable items */
	public static final int TV_MAX_WEAR    = 50;
	public static final int TV_STAFF       = 55;
	public static final int TV_WAND        = 65;
	public static final int TV_SCROLL1     = 70;
	public static final int TV_SCROLL2     = 71;
	public static final int TV_POTION1     = 75;
	public static final int TV_POTION2     = 76;
	public static final int TV_FLASK       = 77;
	public static final int TV_FOOD        = 80;
	public static final int TV_MAGIC_BOOK  = 90;
	public static final int TV_PRAYER_BOOK = 91;
	/* objects with tval above this are never picked up by monsters */
	public static final int TV_MAX_OBJECT  = 99;
	public static final int TV_GOLD        = 100;
	/* objects with higher tvals can not be picked up */
	public static final int TV_MAX_PICK_UP = 100;
	public static final int TV_INVIS_TRAP  = 101;
	/* objects between TV_MIN_VISIBLE and TV_MAX_VISIBLE are always visible,
	 * i.e. the cave fm flag is set when they are present */
	public static final int TV_MIN_VISIBLE = 102;
	public static final int TV_VIS_TRAP    = 102;
	public static final int TV_RUBBLE      = 103;
	/* following objects are never deleted when trying to create another one
	 * during level generation */
	public static final int TV_MIN_DOORS   = 104;
	public static final int TV_OPEN_DOOR   = 104;
	public static final int TV_CLOSED_DOOR = 105;
	public static final int TV_UP_STAIR    = 107;
	public static final int TV_DOWN_STAIR  = 108;
	public static final int TV_SECRET_DOOR = 109;
	public static final int TV_STORE_DOOR  = 110;
	public static final int TV_MAX_VISIBLE = 110;
	
	/* spell types used by get_flags(), breathe(), fire_bolt() and fire_ball() */
	public static final int GF_MAGIC_MISSILE = 0;
	public static final int GF_LIGHTNING     = 1;
	public static final int GF_POISON_GAS    = 2;
	public static final int GF_ACID          = 3;
	public static final int GF_FROST         = 4;
	public static final int GF_FIRE          = 5;
	public static final int GF_HOLY_ORB      = 6;
	
	/* Number of entries allowed in the scorefile.  */
	public static final int SCOREFILE_SIZE = 1000;
}
