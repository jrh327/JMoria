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
	private static Variable instance;
	private Variable() { }
	public static Variable getInstance() {
		if (instance == null) {
			instance = new Variable();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
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
			c_recall[i] = new MonsterRecallType();
		}
	}
	
	public String[] copyright = {
	"Copyright (c) 1989-92 James E. Wilson, Robert A. Keoneke",
	"",
	"This software may be copied and distributed for educational, research, and",
	"not for profit purposes provided that this copyright and statement are",
	"included in all such copies."};
	
	/* Save the store's last increment value.  */
	public int last_store_inc;
	
	/* a horrible hack: needed because compact_monster() can be called from
	 * creatures() via summon_monster() and place_monster() */
	public int hack_monptr = -1;
	
	public boolean weapon_heavy = false;
	public int pack_heavy = Constants.FALSE;
	//vtype died_from;
	//public char[] died_from = new char[Constants.VTYPESIZ];
	public String died_from;
	public long birth_date;
	
	//vtype savefile;			/* The savefile to use. */
	//public char[] savefile = new char[Constants.VTYPESIZ];
	public String savefile;
	
	public boolean total_winner = false;
	public int max_score = 0;
	public boolean character_generated = false;	/* don't save score until char gen finished */
	public int character_saved = 0;				/* prevents save on kill after save_char() */
	public java.io.File highscore_fp;			/* File pointer to high score file */
	public long randes_seed;						/* for restarting randes_state */
	public long town_seed;						/* for restarting town_seed */
	public int cur_height, cur_width;			/* Cur dungeon size    */
	public int dun_level = 0;					/* Cur dungeon level   */
	public int missile_ctr = 0;					/* Counter for missiles */
	public int msg_flag;							/* Set with first msg  */
	//vtype old_msg[Constants.MAX_SAVE_MSG];			/* Last message	      */
	public String[] old_msg = new String[Constants.MAX_SAVE_MSG];
	public int last_msg = 0;						/* Where last is held */
	public boolean death = false;			/* True if died	      */
	public int find_flag;					/* Used in MORIA for .(dir) */
	public boolean free_turn_flag;					/* Used in MORIA, do not move creatures  */
	public int command_count;					/* Gives repetition of commands. -CJS- */
	public int default_dir = Constants.FALSE;	/* Use last direction for repeated command */
	public int turn = -1;						/* Cur turn of game    */
	public boolean wizard = false;				/* Wizard flag	      */
	public boolean to_be_wizard = false;	/* used during startup, when -w option used */
	public int panic_save = Constants.FALSE;		/* this is true if playing from a panic save */
	public int noscore = 0;		/* Don't log the game. -CJS- */
	
	public BooleanPointer rogue_like_commands = new BooleanPointer();	/* set in config.h/main.c */
	
	/* options set via the '=' command */
	public BooleanPointer find_cut = new BooleanPointer(true);
	public BooleanPointer find_examine = new BooleanPointer(true);
	public BooleanPointer find_bound = new BooleanPointer(false);
	public BooleanPointer find_prself = new BooleanPointer(false);
	public BooleanPointer prompt_carry_flag = new BooleanPointer(false);
	public BooleanPointer show_weight_flag = new BooleanPointer(false);
	public BooleanPointer highlight_seams = new BooleanPointer(false);
	public BooleanPointer find_ignore_doors = new BooleanPointer(false);
	public BooleanPointer sound_beep_flag = new BooleanPointer(true);
	public BooleanPointer display_counts = new BooleanPointer(true);
	
	public char doing_inven = '\0';	/* Track inventory commands. -CJS- */
	public boolean screen_change = false;	/* Track screen updates for inven_commands. */
	public char last_command = ' ';  			/* Memory of previous command. */
	
	/* these used to be in dungeon.c */
	public boolean new_level_flag;					/* Next level when true	 */
	public boolean teleport_flag;					/* Handle teleport traps  */
	public boolean player_light;						/* Player carrying light */
	public int eof_flag = 0;		/* Used to signal EOF/HANGUP condition */
	public boolean light_flag = false;		/* Track if temporary light about player.  */
	
	public int wait_for_more = Constants.FALSE;	/* used when ^C hit during -more- prompt */
	public int closing_flag = Constants.FALSE;	/* Used for closing   */
	
	/*  Following are calculated from max dungeon sizes		*/
	public int max_panel_rows, max_panel_cols;
	public int panel_row, panel_col;
	public int panel_row_min, panel_row_max;
	public int panel_col_min, panel_col_max;
	public int panel_col_prt, panel_row_prt;
	
	public char floorsym = '.';
	public char wallsym = '#';	//(char)240

	public StoreType[] store = new StoreType[Constants.MAX_STORES];
	
	public CaveType[][] cave = new CaveType[Constants.MAX_HEIGHT][Constants.MAX_WIDTH];
	public MonsterRecallType[] c_recall = new MonsterRecallType[Constants.MAX_CREATURES]; /* Monster memories */
}
