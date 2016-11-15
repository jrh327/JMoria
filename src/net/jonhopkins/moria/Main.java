/*
 * UNIX Moria Version 5.x
 * Main.java: initialization, main() function and main loop
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

/* Original copyright message follows; included for historical reasons
   but no longer valid. */

/* Moria Version 4.8	COPYRIGHT (c) Robert Alan Koeneke		*/
/*																*/
/*	 I lovingly dedicate this game to hackers and adventurers	*/
/*	 everywhere...												*/
/*																*/
/*																*/
/*	 Designer and Programmer : Robert Alan Koeneke				*/
/*					University of Oklahoma						*/
/*																*/
/*	 Assistant Programmers	 : Jimmey Wayne Todd				*/
/*					University of Oklahoma						*/
/*																*/
/*							   Gary D. McAdoo					*/
/*					University of Oklahoma						*/
/*																*/
/*	UNIX Port		: James E. Wilson							*/
/*				   UC Berkeley									*/
/*				   wilson@kithrup.com							*/
/*																*/
/*	MSDOS Port		: Don Kneller								*/
/*				   1349 - 10th ave								*/
/*				   San Francisco, CA 94122						*/
/*				   kneller@cgl.ucsf.EDU							*/
/*				   ...ucbvax!ucsfcgl!kneller					*/
/*				   kneller@ucsf-cgl.BITNET						*/
/*									 							*/
/*	BRUCE Moria		: Christopher Stuart						*/
/*				   Monash University							*/
/*				   Melbourne, Victoria, AUSTRALIA				*/
/*				   cjs@moncsbruce.oz							*/
/*																*/
/*	Amiga Port		: Corey Gehman								*/
/*				   Clemson University							*/
/*				   cg377170@eng.clemson.edu						*/
/*																*/
/*	Version 5.5		: David Grabiner							*/
/*				   Harvard University			 				*/
/*				   grabiner@math.harvard.edu					*/
/*																*/
/*	 Moria may be copied and modified freely as long as the above	*/
/*	 credits are retained.	No one who-so-ever may sell or market	*/
/*	 this software in any form without the expressed written consent*/
/*	 of the author Robert Alan Koeneke.								*/
/*																	*/

package net.jonhopkins.moria;

import java.applet.Applet;
import java.io.File;

import net.jonhopkins.moria.graphics.Output;
import net.jonhopkins.moria.types.BooleanPointer;
import net.jonhopkins.moria.types.InvenType;

public class Main extends Applet {
	public void init() {
		Output.initialize(this);
		moria_main(2, new char[] { 'n', 'r' });
	}
	
	public void start() {
	}
	public void stop() {
		running = false;
		Thread.currentThread().interrupt();
		System.exit(0);
		return;
	}
	
	public void destroy() {
		running = false;
		Thread.currentThread().interrupt();
		System.exit(0);
		return;
	}
	
	public void run() {
		running = true;
		/*while (running)
		{
			//curses.refresh();
			
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				
			}
		}*/
	}
	
	//long _stksize = 18000;		/*(SAJ) for MWC	*/
	//unsigned _stklen = 0x3fff;	/* increase stack from 4K to 16K */
	
	/* Initialize, restore, and get the ball rolling.	-RAK-	*/
	int moria_main(int argc, char[] argv) {
		int seed;
		BooleanPointer generate = new BooleanPointer();
		String p;
		boolean result;
		boolean new_game = false;
		boolean force_rogue_like = false;
		boolean force_keys_to = false;
		
		/* default command set defined in config.h file */
		Variable.rogue_like_commands.value(Config.ROGUE_LIKE);
		
		/* call this routine to grab a file pointer to the highscore file */
		/* and prepare things to relinquish setuid privileges */
		Files.initScoreFile();
		
		/* use curses */
		IO.initCurses();
		
		/* catch those nasty signals */
		/* must come after init_curses as some of the signal handlers use curses */
		Signals.initSignals();
		
		seed = 0; /* let wizard specify rng seed */
		/* check for user interface option */
		int i = 0;
		//for (--argc, ++i; argc > 0 && argv[i] == '-'; --argc, ++i) {
		for (i = 0; i < argc; i++) {
			//switch (argv[i + 1])
			switch (argv[i])
			{
			case 'N':
			case 'n': new_game = true; break;
			case 'O':
			case 'o':
				/* rogue_like_commands may be set in get_char(), so delay this
				 * until after read savefile if any */
				force_rogue_like = true;
				force_keys_to = false;
				break;
			case 'R':
			case 'r':
				force_rogue_like = true;
				force_keys_to = true;
				break;
			case 'S':
				Death.displayScores(true);
				Death.exitGame();
				break;
			case 's':
				Death.displayScores(true);
				Death.exitGame();
				break;
			case 'W':
			case 'w':
				Variable.to_be_wizard = true;
				
				if (Character.isDigit(argv[i + 2])) {
					try {
						seed = Integer.parseInt("" + argv[i + 2]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						seed = 0;
					}
				}
				break;
			default:
				System.out.println("Usage: moria [-norsw] [savefile]\n");
				Death.exitGame();
			}
		}
		
		/* Some necessary initializations		*/
		/* all made into constants or initialized in variables.c */
		
		if (Constants.COST_ADJ != 100) adjustPrices();
		
		/* Grab a random seed from the clock		*/
		Misc1.initSeeds(seed);
		
		/* Init monster and treasure levels for allocate */
		initMonsterLevels();
		initTreasureLevels();
		
		/* Init the store inventories			*/
		Store1.storeInit();
		
		/* Auto-restart of saved file */
		if (argv[0] != Constants.CNIL) {
			Variable.savefile = "" + argv[0];
		} else if ((p = System.getenv("MORIA_SAV")) != null) {
			Variable.savefile =  p;
		} else if ((p = System.getenv("HOME")) != null) {
			Variable.savefile = String.format("%s/%s", p, Config.MORIA_SAV);
		} else {
			Variable.savefile = Config.MORIA_SAV;
		}
		
		/* This restoration of a saved character may get ONLY the monster memory. In
		 * this case, get_char returns false. It may also resurrect a dead character
		 * (if you are the wizard). In this case, it returns true, but also sets the
		 * parameter "generate" to true, as it does not recover any cave details. */
		
		result = false;
		
		if ((!new_game) && !(new File(Variable.savefile)).canRead() && Save.getCharacter(generate)) {
			result = true;
		}
		
		/* enter wizard mode before showing the character display, but must wait
		 * until after get_char in case it was just a resurrection */
		if (Variable.to_be_wizard) {
			if (!Misc3.enterWizardMode()) {
				Death.exitGame();
			}
		}
		
		if (result) {
			Misc3.changeName();
			
			/* could be restoring a dead character after a signal or HANGUP */
			if (Player.py.misc.chp < 0) {
				Variable.death = true;
			}
		} else {
			/* Create character	   */
			Create.createCharacter();
			Variable.birth_date = java.util.Calendar.getInstance().getTimeInMillis();
			initCharacterInventory();
			Player.py.flags.food = 7500;
			Player.py.flags.food_digested = 2;
			
			if (Player.Class[Player.py.misc.pclass].spell == Constants.MAGE) {
				/* Magic realm   */
				IO.clearScreen(); /* makes spell list easier to read */
				Misc3.calcSpells(Constants.A_INT);
				Misc3.calcMana(Constants.A_INT);
			} else if (Player.Class[Player.py.misc.pclass].spell == Constants.PRIEST) {
				/* Clerical realm*/
				Misc3.calcSpells(Constants.A_WIS);
				IO.clearScreen(); /* force out the 'learn prayer' message */
				Misc3.calcMana(Constants.A_WIS);
			}
			/* prevent ^c quit from entering score into scoreboard,
			 * and prevent signal from creating panic save until this point,
			 * all info needed for save file is now valid */
			Variable.character_generated = true;
			generate.value(true);
	    }
		
		if (force_rogue_like) {
			Variable.rogue_like_commands.value(force_keys_to);
		}
		
		Desc.magicInit();
		
		/* Begin the game				*/
		IO.clearScreen();
		Misc3.printStatBlock();
		
		if (generate.value()) {
			Generate.generateLevel();
		}
		
		/* Loop till dead, or exit			*/
		while(!Variable.death) {
			Dungeon.dungeon();	/* Dungeon logic */
			/* check for eof here, see inkey() in io.c */
			/* eof can occur if the process gets a HANGUP signal */
			if (Variable.eof_flag == Constants.TRUE) {
				Variable.died_from = "(end of input: saved)";
				
				if (!Save.saveCharacter()) {
					Variable.died_from = "unexpected eof";
				}
				/* should not reach here, by if we do, this guarantees exit */
				Variable.death = true;
			}
			
			if (!Variable.death) Generate.generateLevel();	/* New level	*/
	    }
		
		Death.exitGame();	/* Character gets buried. */
		/* should never reach here, but just in case */
		return 0;
	}
	
	/* Init players with some belongings			-RAK-	*/
	public void initCharacterInventory() {
		int i, j;
		InvenType inven_init;
		
		/* this is needed for bash to work right, it can't hurt anyway */
		for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
			Desc.copyIntoInventory(Treasure.inventory[i], Constants.OBJ_NOTHING);
		}
		
		for (i = 0; i < 5; i++) {
			inven_init = new InvenType();
			j = Player.player_init[Player.py.misc.pclass][i];
			Desc.copyIntoInventory(inven_init, j);
			/* this makes it known2 and known1 */
			Desc.setStoreBought(inven_init);
			/* must set this bit to display tohit/todam for stiletto */
			if (inven_init.tval == Constants.TV_SWORD) {
				inven_init.ident |= Constants.ID_SHOW_HITDAM;
			}
			
			Misc3.pickUpItem(inven_init);
	    }
		
		/* wierd place for it, but why not? */
		for (i = 0; i < 32; i++) {
			Player.spell_order[i] = 99;
		}
	}
	
	/* Initializes M_LEVEL array for use with PLACE_MONSTER	-RAK-	*/
	private void initMonsterLevels() {
		int i, k;
		
		for (i = 0; i <= Constants.MAX_MONS_LEVEL; i++) {
			Monsters.m_level[i] = 0;
		}
		
		k = Constants.MAX_CREATURES - Constants.WIN_MON_TOT;
		
		for (i = 0; i < k; i++) {
			Monsters.m_level[Monsters.c_list[i].level]++;
		}
		
		for (i = 1; i <= Constants.MAX_MONS_LEVEL; i++) {
			Monsters.m_level[i] += Monsters.m_level[i - 1];
		}
	}
	
	/* Initializes T_LEVEL array for use with PLACE_OBJECT	-RAK-	*/
	private void initTreasureLevels() {
		int i, l;
		int[] tmp = new int[Constants.MAX_OBJ_LEVEL + 1];
		
		for (i = 0; i <= Constants.MAX_OBJ_LEVEL; i++) {
			Treasure.t_level[i] = 0;
		}
		
		for (i = 0; i < Constants.MAX_DUNGEON_OBJ; i++) {
			Treasure.t_level[Treasure.object_list[i].level]++;
		}
		
		for (i = 1; i <= Constants.MAX_OBJ_LEVEL; i++) {
			Treasure.t_level[i] += Treasure.t_level[i - 1];
		}
		
		/* now produce an array with object indexes sorted by level, by using
		 * the info in t_level, this is an O(n) sort! */
		/* this is not a stable sort, but that does not matter */
		for (i = 0; i <= Constants.MAX_OBJ_LEVEL; i++) {
			tmp[i] = 1;
		}
		
		for (i = 0; i < Constants.MAX_DUNGEON_OBJ; i++) {
			l = Treasure.object_list[i].level;
			Treasure.sorted_objects[Treasure.t_level[l] - tmp[l]] = i;
			tmp[l]++;
		}
	}
	
	/* Adjust prices of objects				-RAK-	*/
	private void adjustPrices() {
		int i;
		
		/* round half-way cases up */
		for (i = 0; i < Constants.MAX_OBJECTS; i++) {
			Treasure.object_list[i].cost = ((Treasure.object_list[i].cost * Constants.COST_ADJ) + 50) / 100;
		}
	}
	
	private static final long serialVersionUID = 6414814785166412325L;
	boolean running;
}
