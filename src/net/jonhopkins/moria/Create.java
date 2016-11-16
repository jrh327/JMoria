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
	
	private Create() { }
	
	/* Generates character's stats				-JWT-	*/
	public static void generateStats() {
		int i, tot;
		int[] dice = new int[18];
		
		do {
			tot = 0;
			for (i = 0; i < 18; i++) {
				dice[i] = Misc1.randomInt (3 + i % 3);  /* Roll 3,4,5 sided dice once each */
				tot += dice[i];
			}
		} while (tot <= 42 || tot >= 54);
		
		for (i = 0; i < 6; i++) {
			Player.py.stats.maxStat[i] = 5 + dice[3 * i] + dice[3 * i + 1] + dice[3 * i + 2];
		}
	}
	
	/* Changes stats by given amount				-JWT-	*/
	public static void changeStat(int stat, int amount) {
		int i;
		int tmp_stat;
		
		tmp_stat = Player.py.stats.maxStat[stat];
		if (amount < 0) {
			for (i = 0; i > amount; i--) {
				if (tmp_stat > 108) {
					tmp_stat--;
				} else if (tmp_stat > 88) {
					tmp_stat += -Misc1.randomInt(6) - 2;
				} else if (tmp_stat > 18) {
					tmp_stat += -Misc1.randomInt(15) - 5;
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
					tmp_stat += Misc1.randomInt(15) + 5;
				} else if (tmp_stat < 108) {
					tmp_stat += Misc1.randomInt(6) + 2;
				} else if (tmp_stat < 118) {
					tmp_stat++;
				}
			}
		}
		Player.py.stats.maxStat[stat] = tmp_stat;
	}
	
	/* generate all stats and modify for race. needed in a separate module so
	 * looping of character selection would be allowed     -RGM- */
	public static void generateAllStats() {
		PlayerType p_ptr;
		PlayerRaceType r_ptr;
		int j;
		
		p_ptr = Player.py;
		r_ptr = Player.race[p_ptr.misc.playerRace];
		generateStats();
		changeStat(Constants.A_STR, r_ptr.strAdjust);
		changeStat(Constants.A_INT, r_ptr.intAdjust);
		changeStat(Constants.A_WIS, r_ptr.wisAdjust);
		changeStat(Constants.A_DEX, r_ptr.dexAdjust);
		changeStat(Constants.A_CON, r_ptr.conAdjust);
		changeStat(Constants.A_CHR, r_ptr.chrAdjust);
		p_ptr.misc.level = 1;
		for (j = 0; j < 6; j++) {
			Player.py.stats.curStat[j] = Player.py.stats.maxStat[j];
			Misc3.setStatUseValue(j);
		}
		
		p_ptr.misc.searchChance    = r_ptr.baseSearchChance;
		p_ptr.misc.baseToHit    = r_ptr.baseToHit;
		p_ptr.misc.baseToHitBow   = r_ptr.baseToHitBow;
		p_ptr.misc.freqOfSearch    = r_ptr.freqOfSearch;
		p_ptr.misc.stealth    = r_ptr.stealth;
		p_ptr.misc.savingThrow   = r_ptr.baseSavingThrow;
		p_ptr.misc.hitDie = r_ptr.baseHitDie;
		p_ptr.misc.plusToDamage = Misc3.adjustToDamage();
		p_ptr.misc.plusToHit = Misc3.adjustToHit();
		p_ptr.misc.magicArmorClass  = 0;
		p_ptr.misc.totalArmorClass    = Misc3.adjustToAc();
		p_ptr.misc.expFactor = r_ptr.baseExpFactor;
		p_ptr.flags.seeInfrared = r_ptr.seeInfrared;
	}
	
	/* Allows player to select a race			-JWT-	*/
	public static void chooseRace() {
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
		IO.clearFrom(20);
		IO.putBuffer("Choose a race (? for Help):", 20, 2);
		
		do {
			tmp_str = String.format("%c) %s", k + 'a', Player.race[j].raceType);
			IO.putBuffer(tmp_str, m, l);
			
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
			IO.moveCursor(20, 30);
			s = IO.inkey();
			j = s - 'a';
			if ((j < Constants.MAX_RACES) && (j >= 0)) {
				exit_flag = true;
			} else if (s == '?') {
				Files.helpfile(Config.MORIA_WELCOME);
			} else {
				IO.bell();
			}
		} while (!exit_flag);
		
		p_ptr = Player.py;
		r_ptr = Player.race[j];
		p_ptr.misc.playerRace = j;
		IO.putBuffer(r_ptr.raceType, 3, 15);
	}
	
	/* Will print the history of a character			-JWT-	*/
	public static void printHistory() {
		IO.putBuffer("Character Background", 14, 27);
		for (int i = 0; i < 4; i++) {
			IO.print(Player.py.misc.history[i], i + 15, 10);
		}
	}
	
	/* Get the racial history, determines social class	-RAK-	*/
	/* Assumptions:	Each race has init history beginning at		*/
	/*		(race-1)*3+1					*/
	/*		All history parts are in ascending order	*/
	public static void generateHistory() {
		int hist_ptr, cur_ptr, test_roll;
		boolean flag;
		int start_pos, end_pos, cur_len;
		int line_ctr, new_start = 0, social_class;
		StringBuilder history_block;
		BackgroundType b_ptr;
		
		/* Get a block of history text				*/
		hist_ptr = Player.py.misc.playerRace * 3 + 1;
		history_block = new StringBuilder();
		social_class = Misc1.randomInt(4);
		cur_ptr = 0;
		do {
			flag = false;
			do {
				if (Player.background[cur_ptr].chart == hist_ptr) {
					test_roll = Misc1.randomInt(100);
					while (test_roll > Player.background[cur_ptr].roll) {
						cur_ptr++;
					}
					b_ptr = Player.background[cur_ptr];
					history_block = history_block.append(b_ptr.info);
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
			Player.py.misc.history[hist_ptr] = "";
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
			Player.py.misc.history[line_ctr] = history_block.substring(start_pos, cur_len + start_pos);
			Player.py.misc.history[line_ctr] = Player.py.misc.history[line_ctr].substring(0, cur_len);
			line_ctr++;
			start_pos = new_start;
		} while (!flag);
		
		/* Compute social class for player			*/
		if (social_class > 100) {
			social_class = 100;
		} else if (social_class < 1) {
			social_class = 1;
		}
		Player.py.misc.socialClass = social_class;
	}
	
	/* Gets the character's sex				-JWT-	*/
	public static void chooseSex() {
		boolean exit_flag;
		char c;
		
		exit_flag = false;
		IO.clearFrom(20);
		IO.putBuffer("Choose a sex (? for Help):", 20, 2);
		IO.putBuffer("m) Male       f) Female", 21, 2);
		do {
			IO.moveCursor(20, 29);
			/* speed not important here */
			c = IO.inkey();
			if (c == 'f' || c == 'F') {
				Player.py.misc.isMale = false;
				IO.putBuffer("Female", 4, 15);
				exit_flag = true;
			} else if (c == 'm' || c == 'M') {
				Player.py.misc.isMale = true;
				IO.putBuffer("Male", 4, 15);
				exit_flag = true;
			} else if (c == '?') {
				Files.helpfile(Config.MORIA_WELCOME);
			} else {
				IO.bell();
			}
		} while (!exit_flag);
	}
	
	/* Computes character's age, height, and weight		-JWT-	*/
	public static void generateAhw() {
		int i = Player.py.misc.playerRace;
		Player.py.misc.age = Player.race[i].baseAge + Misc1.randomInt(Player.race[i].maxAge);
		if (Player.py.misc.isMale) {
			Player.py.misc.height = Misc1.randomIntNormalized(Player.race[i].baseHeightMale, Player.race[i].modHeightMale);
			Player.py.misc.weight = Misc1.randomIntNormalized(Player.race[i].baseWeightMale, Player.race[i].modWeightMale);
		} else {
			Player.py.misc.height = Misc1.randomIntNormalized(Player.race[i].baseHeightFemale, Player.race[i].modHeightFemale);
			Player.py.misc.weight = Misc1.randomIntNormalized(Player.race[i].baseWeightFemale, Player.race[i].modWeightFemale);
		}
		Player.py.misc.disarmChance = Player.race[i].baseDisarmChance + Misc3.adjustToDisarm();
	}
	
	/* Gets a character class				-JWT-	*/
	public static void chooseClass() {
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
		i = Player.py.misc.playerRace;
		j = 0;
		k = 0;
		l = 2;
		m = 21;
		mask = 0x1;
		IO.clearFrom(20);
		IO.putBuffer("Choose a class (? for Help):", 20, 2);
		do {
			if ((Player.race[i].rtclass & mask) != 0) {
				tmp_str = String.format("%c) %s", k + 'a', Player.Class[j].title);
				IO.putBuffer(tmp_str, m, l);
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
		
		Player.py.misc.playerClass = 0;
		exit_flag = false;
		
		do {
			IO.moveCursor(20, 31);
			s = IO.inkey();
			j = s - 'a';
			if ((j < k) && (j >= 0)) {
				Player.py.misc.playerClass = cl[j];
				c_ptr = Player.Class[Player.py.misc.playerClass];
				exit_flag = true;
				IO.clearFrom(20);
				IO.putBuffer(c_ptr.title, 5, 15);
				
				/* Adjust the stats for the class adjustment		-RAK-	*/
				p_ptr = Player.py;
				changeStat(Constants.A_STR, c_ptr.modStrAdjust);
				changeStat(Constants.A_INT, c_ptr.modIntAdjust);
				changeStat(Constants.A_WIS, c_ptr.modWisAdjust);
				changeStat(Constants.A_DEX, c_ptr.modDexAdjust);
				changeStat(Constants.A_CON, c_ptr.modConAdjust);
				changeStat(Constants.A_CHR, c_ptr.modChrAdjust);
				for(i = 0; i < 6; i++) {
					p_ptr.stats.curStat[i] = p_ptr.stats.maxStat[i];
					Misc3.setStatUseValue(i);
				}
				
				p_ptr.misc.plusToDamage = Misc3.adjustToDamage();	/* Real values		*/
				p_ptr.misc.plusToHit = Misc3.adjustToHit();
				p_ptr.misc.magicArmorClass  = Misc3.adjustToAc();
				p_ptr.misc.totalArmorClass    = 0;
				p_ptr.misc.displayPlusToDamage = p_ptr.misc.plusToDamage; /* Displayed values	*/
				p_ptr.misc.displayPlusToHit = p_ptr.misc.plusToHit;
				p_ptr.misc.displayPlusTotalArmorClass= p_ptr.misc.magicArmorClass;
				p_ptr.misc.displayPlusToArmorClass = p_ptr.misc.totalArmorClass + p_ptr.misc.displayPlusTotalArmorClass;
				
				/* now set misc stats, do this after setting stats because
				 * of con_adj() for hitpoints */
				m_ptr = Player.py.misc;
				m_ptr.hitDie += c_ptr.adjHitpoints;
				m_ptr.maxHitpoints = Misc3.adjustConstitution() + m_ptr.hitDie;
				m_ptr.currHitpoints = m_ptr.maxHitpoints;
				m_ptr.currHitpointsFraction = 0;
				
				/* initialize hit_points array */
				/* put bounds on total possible hp, only succeed if it is within
				 * 1/8 of average value */
				min_value = (Constants.MAX_PLAYER_LEVEL * 3 / 8 * (m_ptr.hitDie - 1)) + Constants.MAX_PLAYER_LEVEL;
				max_value = (Constants.MAX_PLAYER_LEVEL * 5 / 8 * (m_ptr.hitDie - 1)) + Constants.MAX_PLAYER_LEVEL;
				Player.hitpoints[0] = m_ptr.hitDie;
				do {
					for (i = 1; i < Constants.MAX_PLAYER_LEVEL; i++) {
						Player.hitpoints[i] = Misc1.randomInt(m_ptr.hitDie);
						Player.hitpoints[i] += Player.hitpoints[i - 1];
					}
				} while ((Player.hitpoints[Constants.MAX_PLAYER_LEVEL - 1] < min_value) || (Player.hitpoints[Constants.MAX_PLAYER_LEVEL - 1] > max_value));
				
				m_ptr.baseToHit += c_ptr.modBaseToHit;
				m_ptr.baseToHitBow += c_ptr.modBaseToHitBow;	/*RAK*/
				m_ptr.searchChance += c_ptr.modSearch;
				m_ptr.disarmChance += c_ptr.modDisarm;
				m_ptr.freqOfSearch += c_ptr.modFreqOfSearch;
				m_ptr.stealth += c_ptr.modStealth;
				m_ptr.savingThrow += c_ptr.modSavingThrow;
				m_ptr.expFactor += c_ptr.modExpFactor;
			} else if (s == '?') {
				Files.helpfile(Config.MORIA_WELCOME);
			} else {
				IO.bell();
			}
		} while (!exit_flag);
	}
	
	/* Given a stat value, return a monetary value, which affects the amount
	 * of gold a player has. */
	public static int monetaryValue(int stat) {
		return 5 * (stat - 10);
	}
	
	public static void generateMoney() {
		int tmp, gold;
		int[] a_ptr;
		
		a_ptr = Player.py.stats.maxStat;
		tmp = monetaryValue(a_ptr[Constants.A_STR])
				+ monetaryValue(a_ptr[Constants.A_INT])
				+ monetaryValue(a_ptr[Constants.A_WIS])
				+ monetaryValue(a_ptr[Constants.A_CON])
				+ monetaryValue(a_ptr[Constants.A_DEX]);
		
		gold = Player.py.misc.socialClass * 6 + Misc1.randomInt(25) + 325;	/* Social Class adj */
		gold -= tmp;										/* Stat adj */
		gold += monetaryValue(a_ptr[Constants.A_CHR]);				/* Charisma adj	*/
		if (!Player.py.misc.isMale) {
			gold += 50;	/* She charmed the banker into it! -CJS- */
		}
		if (gold < 80) {
			gold = 80;	/* Minimum */
		}
		Player.py.misc.gold = gold;
	}
	
	/* ---------- M A I N  for Character Creation Routine ---------- */
	/*							-JWT-	*/
	public static void createCharacter() {
		boolean exit_flag = true;
		char c;
		
		Misc3.printCharacterInfo();
		chooseRace();
		chooseSex();
		
		/* here we start a loop giving a player a choice of characters -RGM- */
		generateAllStats();
		generateHistory();
		generateAhw();
		printHistory();
		Misc3.printAhws();
		Misc3.printStats();
		
		IO.clearFrom(20);
		IO.putBuffer("Hit space to reroll or ESC to accept characteristics: ", 20, 2);
		do {
			IO.moveCursor(20, 56);
			c = IO.inkey();
			if (c == Constants.ESCAPE) {
				exit_flag = false;
			} else if (c == ' ') {
				generateAllStats();
				generateHistory();
				generateAhw();
				printHistory();
				Misc3.printAhws();
				Misc3.printStats();
			} else {
				IO.bell();
			}
		} while (exit_flag); /* done with stats generation */
		
		chooseClass();
		generateMoney();
		Misc3.printStats();
		Misc3.printLevelStats();
		Misc3.printAbilities();
		Misc3.chooseName();
		
		/* This delay may be reduced, but is recommended to keep players	*/
		/* from continuously rolling up characters, which can be VERY	*/
		/* expensive CPU wise.						*/
		IO.pauseExit(23, Constants.PLAYER_EXIT_PAUSE);
	}
}
