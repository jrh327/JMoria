/* 
 * Create.java: create a player character
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
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.PlayerType;
import net.jonhopkins.moria.types.PlayerRaceType;

public class Create {
	private Files files;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Player py;
	
	private static Create instance;
	private Create() { }
	public static Create getInstance() {
		if (instance == null) {
			instance = new Create();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		files = Files.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		py = Player.getInstance();
	}
	
	/* Generates character's stats				-JWT-	*/
	public void get_stats() {
		int i, tot;
		int[] dice = new int[18];
		
		do {
			tot = 0;
			for (i = 0; i < 18; i++) {
				dice[i] = m1.randint (3 + i % 3);  /* Roll 3,4,5 sided dice once each */
				tot += dice[i];
			}
		} while (tot <= 42 || tot >= 54);
		
		for (i = 0; i < 6; i++) {
			py.py.stats.max_stat[i] = 5 + dice[3 * i] + dice[3 * i + 1] + dice[3 * i + 2];
		}
	}
	
	/* Changes stats by given amount				-JWT-	*/
	public void change_stat(int stat, int amount) {
		int i;
		int tmp_stat;
		
		tmp_stat = py.py.stats.max_stat[stat];
		if (amount < 0) {
			for (i = 0; i > amount; i--) {
				if (tmp_stat > 108) {
					tmp_stat--;
				} else if (tmp_stat > 88) {
					tmp_stat += -m1.randint(6) - 2;
				} else if (tmp_stat > 18) {
					tmp_stat += -m1.randint(15) - 5;
					if (tmp_stat < 18) {
						tmp_stat = 18;
					}
				} else if (tmp_stat > 3) {
					tmp_stat--;
				}
			}
		} else {
			for (i = 0; i < amount; i++) {
				if (tmp_stat < 18) {
					tmp_stat++;
				} else if (tmp_stat < 88) {
					tmp_stat += m1.randint(15) + 5;
				} else if (tmp_stat < 108) {
					tmp_stat += m1.randint(6) + 2;
				} else if (tmp_stat < 118) {
					tmp_stat++;
				}
			}
		}
		py.py.stats.max_stat[stat] = tmp_stat;
	}
	
	/* generate all stats and modify for race. needed in a separate module so
	 * looping of character selection would be allowed     -RGM- */
	public void get_all_stats() {
		PlayerType p_ptr;
		PlayerRaceType r_ptr;
		int j;
		
		p_ptr = py.py;
		r_ptr = py.race[p_ptr.misc.prace];
		get_stats();
		change_stat(Constants.A_STR, r_ptr.str_adj);
		change_stat(Constants.A_INT, r_ptr.int_adj);
		change_stat(Constants.A_WIS, r_ptr.wis_adj);
		change_stat(Constants.A_DEX, r_ptr.dex_adj);
		change_stat(Constants.A_CON, r_ptr.con_adj);
		change_stat(Constants.A_CHR, r_ptr.chr_adj);
		p_ptr.misc.lev = 1;
		for (j = 0; j < 6; j++) {
			py.py.stats.cur_stat[j] = py.py.stats.max_stat[j];
			m3.set_use_stat(j);
		}
		
		p_ptr.misc.srh    = r_ptr.srh;
		p_ptr.misc.bth    = r_ptr.bth;
		p_ptr.misc.bthb   = r_ptr.bthb;
		p_ptr.misc.fos    = r_ptr.fos;
		p_ptr.misc.stl    = r_ptr.stl;
		p_ptr.misc.save   = r_ptr.bsav;
		p_ptr.misc.hitdie = r_ptr.bhitdie;
		p_ptr.misc.ptodam = m3.todam_adj();
		p_ptr.misc.ptohit = m3.tohit_adj();
		p_ptr.misc.ptoac  = 0;
		p_ptr.misc.pac    = m3.toac_adj();
		p_ptr.misc.expfact = r_ptr.b_exp;
		p_ptr.flags.see_infra = r_ptr.infra;
	}
	
	/* Allows player to select a race			-JWT-	*/
	public void choose_race() {
		int j, k;
		int l, m;
		boolean exit_flag;
		char s;
		String tmp_str;
		PlayerType p_ptr;
		PlayerRaceType r_ptr;
		
		j = 0;
		k = 0;
		l = 2;
		m = 21;
		io.clear_from(20);
		io.put_buffer("Choose a race (? for Help):", 20, 2);
		
		do {
			tmp_str = String.format("%c) %s", k + 'a', py.race[j].trace);
			io.put_buffer(tmp_str, m, l);
			
			k++;
			l += 15;
			if (l > 70)
			{
				l = 2;
				m++;
			}
			j++;
		} while (j < Constants.MAX_RACES);
		
		exit_flag = false;
		
		do {
			io.move_cursor(20, 30);
			s = io.inkey();
			j = s - 'a';
			if ((j < Constants.MAX_RACES) && (j >= 0)) {
				exit_flag = true;
			} else if (s == '?') {
				files.helpfile(Config.MORIA_WELCOME);
			} else {
				io.bell();
			}
		} while (!exit_flag);
		
		p_ptr = py.py;
		r_ptr = py.race[j];
		p_ptr.misc.prace = j;
		io.put_buffer(r_ptr.trace, 3, 15);
	}
	
	/* Will print the history of a character			-JWT-	*/
	public void print_history() {
		io.put_buffer("Character Background", 14, 27);
		for (int i = 0; i < 4; i++) {
			io.prt(py.py.misc.history[i], i + 15, 10);
		}
	}
	
	/* Get the racial history, determines social class	-RAK-	*/
	/* Assumptions:	Each race has init history beginning at		*/
	/*		(race-1)*3+1					*/
	/*		All history parts are in ascending order	*/
	public void get_history() {
		int hist_ptr, cur_ptr, test_roll;
		boolean flag;
		int start_pos, end_pos, cur_len;
		int line_ctr, new_start = 0, social_class;
		String history_block;
		BackgroundType b_ptr;
		
		/* Get a block of history text				*/
		hist_ptr = py.py.misc.prace * 3 + 1;
		history_block = "";
		social_class = m1.randint(4);
		cur_ptr = 0;
		do {
			flag = false;
			do {
				if (py.background[cur_ptr].chart == hist_ptr) {
					test_roll = m1.randint(100);
					while (test_roll > py.background[cur_ptr].roll) {
						cur_ptr++;
					}
					b_ptr = py.background[cur_ptr];
					history_block = history_block.concat(b_ptr.info);
					social_class += b_ptr.bonus - 50;
					if (hist_ptr > b_ptr.next) {
						cur_ptr = 0;
					}
					hist_ptr = b_ptr.next;
					flag = true;
				} else {
					cur_ptr++;
				}
			} while (!flag);
		} while (hist_ptr >= 1);
		
		/* clear the previous history strings */
		for (hist_ptr = 0; hist_ptr < 4; hist_ptr++) {
			py.py.misc.history[hist_ptr] = "";
		}
		
		/* Process block of history text for pretty output	*/
		start_pos = 0;
		end_pos   = history_block.length() - 1;
		line_ctr  = 0;
		flag = false;
		while (history_block.charAt(end_pos) == ' ') {
			end_pos--;
		}
		do {
			while (history_block.charAt(start_pos) == ' ') {
				start_pos++;
			}
			cur_len = end_pos - start_pos + 1;
			if (cur_len > 60) {
				cur_len = 60;
				while (history_block.charAt(start_pos + cur_len - 1) != ' ') {
					cur_len--;
				}
				new_start = start_pos + cur_len;
				while (history_block.charAt(start_pos + cur_len - 1) == ' ') {
					cur_len--;
				}
			} else {
				flag = true;
			}
			py.py.misc.history[line_ctr] = history_block.substring(start_pos, cur_len + start_pos);
			py.py.misc.history[line_ctr] = py.py.misc.history[line_ctr].substring(0, cur_len);
			line_ctr++;
			start_pos = new_start;
		} while (!flag);
		
		/* Compute social class for player			*/
		if (social_class > 100) {
			social_class = 100;
		} else if (social_class < 1) {
			social_class = 1;
		}
		py.py.misc.sc = social_class;
	}
	
	/* Gets the character's sex				-JWT-	*/
	public void get_sex() {
		boolean exit_flag;
		char c;
		
		exit_flag = false;
		io.clear_from(20);
		io.put_buffer("Choose a sex (? for Help):", 20, 2);
		io.put_buffer("m) Male       f) Female", 21, 2);
		do {
			io.move_cursor(20, 29);
			/* speed not important here */
			c = io.inkey();
			if (c == 'f' || c == 'F') {
				py.py.misc.male = false;
				io.put_buffer("Female", 4, 15);
				exit_flag = true;
			} else if (c == 'm' || c == 'M') {
				py.py.misc.male = true;
				io.put_buffer("Male", 4, 15);
				exit_flag = true;
			} else if (c == '?') {
				files.helpfile(Config.MORIA_WELCOME);
			} else {
				io.bell();
			}
		} while (!exit_flag);
	}
	
	/* Computes character's age, height, and weight		-JWT-	*/
	public void get_ahw() {
		int i = py.py.misc.prace;
		py.py.misc.age = py.race[i].b_age + m1.randint(py.race[i].m_age);
		if (py.py.misc.male) {
			py.py.misc.ht = m1.randnor(py.race[i].m_b_ht, py.race[i].m_m_ht);
			py.py.misc.wt = m1.randnor(py.race[i].m_b_wt, py.race[i].m_m_wt);
		} else {
			py.py.misc.ht = m1.randnor(py.race[i].f_b_ht, py.race[i].f_m_ht);
			py.py.misc.wt = m1.randnor(py.race[i].f_b_wt, py.race[i].f_m_wt);
		}
		py.py.misc.disarm = py.race[i].b_dis + m3.todis_adj();
	}
	
	/* Gets a character class				-JWT-	*/
	public void get_class() {
		int i, j;
		int k, l, m, min_value, max_value;
		int[] cl = new int[Constants.MAX_CLASS];
		boolean exit_flag;
		PlayerMisc m_ptr;
		PlayerType p_ptr;
		ClassType c_ptr;
		String tmp_str;
		char s;
		long mask;
		
		for (j = 0; j < Constants.MAX_CLASS; j++) {
			cl[j] = 0;
		}
		i = py.py.misc.prace;
		j = 0;
		k = 0;
		l = 2;
		m = 21;
		mask = 0x1;
		io.clear_from(20);
		io.put_buffer("Choose a class (? for Help):", 20, 2);
		do {
			if ((py.race[i].rtclass & mask) != 0) {
				tmp_str = String.format("%c) %s", k + 'a', py.Class[j].title);
				io.put_buffer(tmp_str, m, l);
				cl[k] = j;
				l += 15;
				if (l > 70) {
					l = 2;
					m++;
				}
				k++;
			}
			j++;
			mask <<= 1;
		} while (j < Constants.MAX_CLASS);
		
		py.py.misc.pclass = 0;
		exit_flag = false;
		
		do {
			io.move_cursor(20, 31);
			s = io.inkey();
			j = s - 'a';
			if ((j < k) && (j >= 0)) {
				py.py.misc.pclass = cl[j];
				c_ptr = py.Class[py.py.misc.pclass];
				exit_flag = true;
				io.clear_from(20);
				io.put_buffer(c_ptr.title, 5, 15);
				
				/* Adjust the stats for the class adjustment		-RAK-	*/
				p_ptr = py.py;
				change_stat(Constants.A_STR, c_ptr.madj_str);
				change_stat(Constants.A_INT, c_ptr.madj_int);
				change_stat(Constants.A_WIS, c_ptr.madj_wis);
				change_stat(Constants.A_DEX, c_ptr.madj_dex);
				change_stat(Constants.A_CON, c_ptr.madj_con);
				change_stat(Constants.A_CHR, c_ptr.madj_chr);
				for(i = 0; i < 6; i++) {
					p_ptr.stats.cur_stat[i] = p_ptr.stats.max_stat[i];
					m3.set_use_stat(i);
				}
				
				p_ptr.misc.ptodam = m3.todam_adj();	/* Real values		*/
				p_ptr.misc.ptohit = m3.tohit_adj();
				p_ptr.misc.ptoac  = m3.toac_adj();
				p_ptr.misc.pac    = 0;
				p_ptr.misc.dis_td = p_ptr.misc.ptodam; /* Displayed values	*/
				p_ptr.misc.dis_th = p_ptr.misc.ptohit;
				p_ptr.misc.dis_tac= p_ptr.misc.ptoac;
				p_ptr.misc.dis_ac = p_ptr.misc.pac + p_ptr.misc.dis_tac;
				
				/* now set misc stats, do this after setting stats because
				 * of con_adj() for hitpoints */
				m_ptr = py.py.misc;
				m_ptr.hitdie += c_ptr.adj_hd;
				m_ptr.mhp = m3.con_adj() + m_ptr.hitdie;
				m_ptr.chp = m_ptr.mhp;
				m_ptr.chp_frac = 0;
				
				/* initialize hit_points array */
				/* put bounds on total possible hp, only succeed if it is within
				 * 1/8 of average value */
				min_value = (Constants.MAX_PLAYER_LEVEL*3/8 * (m_ptr.hitdie-1)) + Constants.MAX_PLAYER_LEVEL;
				max_value = (Constants.MAX_PLAYER_LEVEL*5/8 * (m_ptr.hitdie-1)) + Constants.MAX_PLAYER_LEVEL;
				py.player_hp[0] = m_ptr.hitdie;
				do {
					for (i = 1; i < Constants.MAX_PLAYER_LEVEL; i++) {
						py.player_hp[i] = m1.randint(m_ptr.hitdie);
						py.player_hp[i] += py.player_hp[i - 1];
					}
				} while ((py.player_hp[Constants.MAX_PLAYER_LEVEL - 1] < min_value) || (py.player_hp[Constants.MAX_PLAYER_LEVEL - 1] > max_value));
				
				m_ptr.bth += c_ptr.mbth;
				m_ptr.bthb += c_ptr.mbthb;	/*RAK*/
				m_ptr.srh += c_ptr.msrh;
				m_ptr.disarm += c_ptr.mdis;
				m_ptr.fos += c_ptr.mfos;
				m_ptr.stl += c_ptr.mstl;
				m_ptr.save += c_ptr.msav;
				m_ptr.expfact += c_ptr.m_exp;
			} else if (s == '?') {
				files.helpfile(Config.MORIA_WELCOME);
			} else {
				io.bell();
			}
		} while (!exit_flag);
	}
	
	/* Given a stat value, return a monetary value, which affects the amount
	 * of gold a player has. */
	public static int monval(int i) {
		return 5 * (i - 10);
	}
	
	public void get_money() {
		int tmp, gold;
		int[] a_ptr;
		
		a_ptr = py.py.stats.max_stat;
		tmp = monval(a_ptr[Constants.A_STR]) + monval(a_ptr[Constants.A_INT]) + monval(a_ptr[Constants.A_WIS]) + monval(a_ptr[Constants.A_CON]) + monval(a_ptr[Constants.A_DEX]);
		
		gold = py.py.misc.sc * 6 + m1.randint(25) + 325;	/* Social Class adj */
		gold -= tmp;										/* Stat adj */
		gold += monval(a_ptr[Constants.A_CHR]);				/* Charisma adj	*/
		if (!py.py.misc.male) {
			gold += 50;	/* She charmed the banker into it! -CJS- */
		}
		if (gold < 80) {
			gold = 80;	/* Minimum */
		}
		py.py.misc.au = gold;
	}
	
	/* ---------- M A I N  for Character Creation Routine ---------- */
	/*							-JWT-	*/
	public void create_character() {
		boolean exit_flag = true;
		char c;
		
		m3.put_character();
		choose_race();
		get_sex();
		
		/* here we start a loop giving a player a choice of characters -RGM- */
		get_all_stats();
		get_history();
		get_ahw();
		print_history();
		m3.put_misc1();
		m3.put_stats();
		
		io.clear_from(20);
		io.put_buffer("Hit space to reroll or ESC to accept characteristics: ", 20, 2);
		do {
			io.move_cursor(20, 56);
			c = io.inkey();
			if (c == Constants.ESCAPE) {
				exit_flag = false;
			} else if (c == ' ') {
				get_all_stats();
				get_history();
				get_ahw();
				print_history();
				m3.put_misc1();
				m3.put_stats();
			} else {
				io.bell();
			}
		} while (exit_flag); /* done with stats generation */
		
		get_class();
		get_money();
		m3.put_stats();
		m3.put_misc2();
		m3.put_misc3();
		m3.get_name();
		
		/* This delay may be reduced, but is recommended to keep players	*/
		/* from continuously rolling up characters, which can be VERY	*/
		/* expensive CPU wise.						*/
		io.pause_exit(23, Constants.PLAYER_EXIT_PAUSE);
	}
}
