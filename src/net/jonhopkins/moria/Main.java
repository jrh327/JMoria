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
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import net.jonhopkins.moria.graphics.Output;
import net.jonhopkins.moria.types.BooleanPointer;
import net.jonhopkins.moria.types.InvenType;

public class Main extends Applet implements KeyListener {
	public void init() {
		setFocusable(true);
		curses = new Output(24, 80, Color.BLACK, this);
		curses.refresh();
		addKeyListener(this);
		
		main(2, new char[] { 'n', 'r' });
	}
	
	public void start() {
	}
	public void stop() {
		running = false;
		Thread.currentThread().interrupt();
		System.exit(0);
	}
	
	public void destroy() {
		running = false;
		Thread.currentThread().interrupt();
		System.exit(0);
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
	
	public void keyPressed(KeyEvent event) {
		curses.handleKey(event);
	}
	
	public void keyReleased(KeyEvent event) {
		//displayInfo(event, "RELEASED");
	}
	
	public void keyTyped(KeyEvent event) {
		//displayInfo(event, "TYPED");
	}
	

	
	//long _stksize = 18000;		/*(SAJ) for MWC	*/
	//unsigned _stklen = 0x3fff;	/* increase stack from 4K to 16K */
	
	private Create create = Create.getInstance();
	private Death death = Death.getInstance();
	private Desc desc = Desc.getInstance();
	private Dungeon dun = Dungeon.getInstance();
	private Files files = Files.getInstance();
	private Generate gen = Generate.getInstance();
	private IO io = IO.getInstance();
	private Misc1 m1 = Misc1.getInstance();
	private Misc3 m3 = Misc3.getInstance();
	private Monsters mon = Monsters.getInstance();
	private Player py = Player.getInstance();
	private Save save = Save.getInstance();
	private Signals sigs = Signals.getInstance();
	private Store1 store1 = Store1.getInstance();
	private Treasure t = Treasure.getInstance();
	private Variable var = Variable.getInstance();
	
	/* Initialize, restore, and get the ball rolling.	-RAK-	*/
	int main(int argc, char[] argv) {
		int seed;
		BooleanPointer generate = new BooleanPointer();
		String p;
		boolean result;
		boolean new_game = false;
		boolean force_rogue_like = false;
		boolean force_keys_to = false;
		
		/* default command set defined in config.h file */
		var.rogue_like_commands.value(Config.ROGUE_LIKE);
		
		/* call this routine to grab a file pointer to the highscore file */
		/* and prepare things to relinquish setuid privileges */
		files.init_scorefile();
		
		/* use curses */
		io.init_curses(curses);
		
		/* catch those nasty signals */
		/* must come after init_curses as some of the signal handlers use curses */
		sigs.init_signals();
		
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
				death.display_scores(true);
				death.exit_game();
			case 's':
				death.display_scores(true);
				death.exit_game();
			case 'W':
			case 'w':
				var.to_be_wizard = true;
				
				if (Character.isDigit(argv[i + 2]))
					try {
						seed = Integer.parseInt("" + argv[i + 2]);
					} catch (NumberFormatException e) {
						seed = 0;
					}
				break;
			default:
				System.out.println("Usage: moria [-norsw] [savefile]\n");
				death.exit_game();
			}
		}
		
		/* Some necessary initializations		*/
		/* all made into constants or initialized in variables.c */
		
		if (Constants.COST_ADJ != 100) price_adjust();
		
		/* Grab a random seed from the clock		*/
		m1.init_seeds(seed);
		
		/* Init monster and treasure levels for allocate */
		init_m_level();
		init_t_level();
		
		/* Init the store inventories			*/
		store1.store_init();
		
		/* Auto-restart of saved file */
		if (argv[0] != Constants.CNIL) {
			var.savefile = "" + argv[0];
		} else if (!(p = System.getenv("MORIA_SAV")).equals(null)) {
			var.savefile =  p;
		} else if (!(p = System.getenv("HOME")).equals(null)) {
			var.savefile = String.format("%s/%s", p, Config.MORIA_SAV);
		} else {
			var.savefile = Config.MORIA_SAV;
		}
		
		/* This restoration of a saved character may get ONLY the monster memory. In
		 * this case, get_char returns false. It may also resurrect a dead character
		 * (if you are the wizard). In this case, it returns true, but also sets the
		 * parameter "generate" to true, as it does not recover any cave details. */
		
		result = false;
		
		if ((!new_game) && !(new File(var.savefile)).canRead() && save.get_char(generate)) {
			result = true;
		}
		
		/* enter wizard mode before showing the character display, but must wait
		 * until after get_char in case it was just a resurrection */
		if (var.to_be_wizard) {
			if (!m3.enter_wiz_mode()) {
				death.exit_game();
			}
		}
		
		if (result) {
			m3.change_name();
			
			/* could be restoring a dead character after a signal or HANGUP */
			if (py.py.misc.chp < 0) {
				var.death = true;
			}
		} else {
			/* Create character	   */
			create.create_character();
			var.birth_date = java.util.Calendar.getInstance().getTimeInMillis();
			char_inven_init();
			py.py.flags.food = 7500;
			py.py.flags.food_digested = 2;
			
			if (py.Class[py.py.misc.pclass].spell == Constants.MAGE) {
				/* Magic realm   */
				io.clear_screen(); /* makes spell list easier to read */
				m3.calc_spells(Constants.A_INT);
				m3.calc_mana(Constants.A_INT);
			} else if (py.Class[py.py.misc.pclass].spell == Constants.PRIEST) {
				/* Clerical realm*/
				m3.calc_spells(Constants.A_WIS);
				io.clear_screen(); /* force out the 'learn prayer' message */
				m3.calc_mana(Constants.A_WIS);
			}
			/* prevent ^c quit from entering score into scoreboard,
			 * and prevent signal from creating panic save until this point,
			 * all info needed for save file is now valid */
			var.character_generated = true;
			generate.value(true);
	    }
		
		if (force_rogue_like) {
			var.rogue_like_commands.value(force_keys_to);
		}
		
		desc.magic_init();
		
		/* Begin the game				*/
		io.clear_screen();
		m3.prt_stat_block();
		
		if (generate.value()) {
			gen.generate_cave();
		}
		
		/* Loop till dead, or exit			*/
		while(!var.death) {
			synchronized(this) {
				dun.dungeon();	/* Dungeon logic */
			}
			/* check for eof here, see inkey() in io.c */
			/* eof can occur if the process gets a HANGUP signal */
			if (var.eof_flag == Constants.TRUE) {
				var.died_from = "(end of input: saved)";
				
				if (!save.save_char()) {
					var.died_from = "unexpected eof";
				}
				/* should not reach here, by if we do, this guarantees exit */
				var.death = true;
			}
			
			if (!var.death) gen.generate_cave();	/* New level	*/
	    }
		
		death.exit_game();	/* Character gets buried. */
		/* should never reach here, but just in case */
		return 0;
	}
	
	/* Init players with some belongings			-RAK-	*/
	public void char_inven_init() {
		int i, j;
		InvenType inven_init;
		
		/* this is needed for bash to work right, it can't hurt anyway */
		for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
			desc.invcopy(t.inventory[i], Constants.OBJ_NOTHING);
		}
		
		for (i = 0; i < 5; i++) {
			inven_init = new InvenType();
			j = py.player_init[py.py.misc.pclass][i];
			desc.invcopy(inven_init, j);
			/* this makes it known2 and known1 */
			desc.store_bought(inven_init);
			/* must set this bit to display tohit/todam for stiletto */
			if (inven_init.tval == Constants.TV_SWORD) {
				inven_init.ident |= Constants.ID_SHOW_HITDAM;
			}
			
			m3.inven_carry(inven_init);
	    }
		
		/* wierd place for it, but why not? */
		for (i = 0; i < 32; i++) {
			py.spell_order[i] = 99;
		}
	}
	
	/* Initializes M_LEVEL array for use with PLACE_MONSTER	-RAK-	*/
	private void init_m_level() {
		int i, k;
		
		for (i = 0; i <= Constants.MAX_MONS_LEVEL; i++) {
			mon.m_level[i] = 0;
		}
		
		k = Constants.MAX_CREATURES - Constants.WIN_MON_TOT;
		
		for (i = 0; i < k; i++) {
			mon.m_level[mon.c_list[i].level]++;
		}
		
		for (i = 1; i <= Constants.MAX_MONS_LEVEL; i++) {
			mon.m_level[i] += mon.m_level[i - 1];
		}
	}
	
	/* Initializes T_LEVEL array for use with PLACE_OBJECT	-RAK-	*/
	private void init_t_level() {
		int i, l;
		int[] tmp = new int[Constants.MAX_OBJ_LEVEL + 1];
		
		for (i = 0; i <= Constants.MAX_OBJ_LEVEL; i++) {
			t.t_level[i] = 0;
		}
		
		for (i = 0; i < Constants.MAX_DUNGEON_OBJ; i++) {
			t.t_level[t.object_list[i].level]++;
		}
		
		for (i = 1; i <= Constants.MAX_OBJ_LEVEL; i++) {
			t.t_level[i] += t.t_level[i - 1];
		}
		
		/* now produce an array with object indexes sorted by level, by using
		 * the info in t_level, this is an O(n) sort! */
		/* this is not a stable sort, but that does not matter */
		for (i = 0; i <= Constants.MAX_OBJ_LEVEL; i++) {
			tmp[i] = 1;
		}
		
		for (i = 0; i < Constants.MAX_DUNGEON_OBJ; i++) {
			l = t.object_list[i].level;
			t.sorted_objects[t.t_level[l] - tmp[l]] = i;
			tmp[l]++;
		}
	}
	
	/* Adjust prices of objects				-RAK-	*/
	private void price_adjust() {
		int i;
		
		/* round half-way cases up */
		for (i = 0; i < Constants.MAX_OBJECTS; i++) {
			t.object_list[i].cost = ((t.object_list[i].cost * Constants.COST_ADJ) + 50) / 100;
		}
	}
	
	private static final long serialVersionUID = 6414814785166412325L;
	boolean running;
	public Output curses;
}
