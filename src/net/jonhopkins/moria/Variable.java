/*
 * Variable.java: Global variables
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

import net.jonhopkins.moria.types.BooleanPointer;
import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.MonsterRecallType;
import net.jonhopkins.moria.types.StoreType;

public class Variable {
	
	private Variable() { }
	
	public static String[] copyright = {
			"Copyright (c) 1989-92 James E. Wilson, Robert A. Keoneke",
			"",
			"This software may be copied and distributed for educational, research, and",
			"not for profit purposes provided that this copyright and statement are",
			"included in all such copies."
	};
	
	/* Save the store's last increment value.  */
	public static int lastStoreInc;
	
	/* a horrible hack: needed because compact_monster() can be called from
	 * creatures() via summon_monster() and place_monster() */
	public static int hackMonsterIndex = -1;
	
	public static boolean isWeaponHeavy = false;
	public static int isPackHeavy = Constants.FALSE;
	//vtype died_from;
	//public char[] died_from = new char[Constants.VTYPESIZ];
	public static String diedFrom;
	public static long birthDate;
	
	//vtype savefile;			/* The savefile to use. */
	//public char[] savefile = new char[Constants.VTYPESIZ];
	public static String savefile;
	
	public static boolean isTotalWinner = false;
	public static int maxScore = 0;
	public static boolean isCharacterGenerated = false; /* don't save score until char gen finished */
	public static int characterSaved = 0;               /* prevents save on kill after save_char() */
	public static java.io.File highscoreFile;           /* File pointer to high score file */
	public static long randesSeed;                      /* for restarting randes_state */
	public static long townSeed;                        /* for restarting town_seed */
	public static int currHeight;                       /* Cur dungeon size    */
	public static int currWidth;
	public static int dungeonLevel = 0;                 /* Cur dungeon level   */
	public static int missileCounter = 0;               /* Counter for missiles */
	public static int msgFlag;                          /* Set with first msg  */
	//vtype old_msg[Constants.MAX_SAVE_MSG];            /* Last message	      */
	public static String[] oldMsg = new String[Constants.MAX_SAVE_MSG];
	public static int lastMsg = 0;                  /* Where last is held */
	public static boolean death = false;            /* True if died	      */
	public static int findFlag;                     /* Used in MORIA for .(dir) */
	public static boolean freeTurnFlag;             /* Used in MORIA, do not move creatures  */
	public static int commandCount;                 /* Gives repetition of commands. -CJS- */
	public static int defaultDir = Constants.FALSE; /* Use last direction for repeated command */
	public static int turn = -1;                    /* Cur turn of game    */
	public static boolean isWizard = false;         /* Wizard flag	      */
	public static boolean toBeWizard = false;       /* used during startup, when -w option used */
	public static int panicSave = Constants.FALSE;  /* this is true if playing from a panic save */
	public static int noScore = 0;                  /* Don't log the game. -CJS- */
	
	public static BooleanPointer rogueLikeCommands = new BooleanPointer(); /* set in config.h/main.c */
	
	/* options set via the '=' command */
	public static BooleanPointer findCut = new BooleanPointer(true);
	public static BooleanPointer findExamine = new BooleanPointer(true);
	public static BooleanPointer findBound = new BooleanPointer(false);
	public static BooleanPointer findPrself = new BooleanPointer(false);
	public static BooleanPointer promptCarryFlag = new BooleanPointer(false);
	public static BooleanPointer showWeightFlag = new BooleanPointer(false);
	public static BooleanPointer highlightSeams = new BooleanPointer(false);
	public static BooleanPointer findIgnoreDoors = new BooleanPointer(false);
	public static BooleanPointer soundBeepFlag = new BooleanPointer(true);
	public static BooleanPointer displayCounts = new BooleanPointer(true);
	
	public static char doingInven = '\0'; /* Track inventory commands. -CJS- */
	public static boolean didScreenChange = false; /* Track screen updates for inven_commands. */
	public static char lastCommand = ' '; /* Memory of previous command. */
	
	/* these used to be in dungeon.c */
	public static boolean newLevelFlag;      /* Next level when true	 */
	public static boolean teleportFlag;      /* Handle teleport traps  */
	public static boolean playerLight;       /* Player carrying light */
	public static int eofFlag = 0;           /* Used to signal EOF/HANGUP condition */
	public static boolean lightFlag = false; /* Track if temporary light about player.  */
	
	public static int waitForMore = Constants.FALSE; /* used when ^C hit during -more- prompt */
	public static int closingFlag = Constants.FALSE; /* Used for closing   */
	
	/*  Following are calculated from max dungeon sizes		*/
	public static int maxPanelRows;
	public static int maxPanelCols;
	public static int panelRow;
	public static int panelCol;
	public static int panelRowMin;
	public static int panelRowMax;
	public static int panelColMin;
	public static int panelColMax;
	public static int panelColPrt;
	public static int panelRowPrt;
	
	public static char floorSymbol = '.';
	public static char wallSymbol = '#'; //(char)240

	public static StoreType[] store = new StoreType[Constants.MAX_STORES];
	
	public static CaveType[][] cave = new CaveType[Constants.MAX_HEIGHT][Constants.MAX_WIDTH];
	public static MonsterRecallType[] creatureRecall = new MonsterRecallType[Constants.MAX_CREATURES]; /* Monster memories */
	
	static {
		int i, j;
		
		for (i = 0; i < Constants.MAX_STORES; i++) {
			store[i] = new StoreType();
		}
		
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				cave[i][j] = new CaveType();
			}
		}
		
		for (i = 0; i < Constants.MAX_CREATURES; i++) {
			creatureRecall[i] = new MonsterRecallType();
		}
	}
}
