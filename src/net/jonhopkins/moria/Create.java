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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.PlayerRaceType;
import net.jonhopkins.moria.types.PlayerStats;

public class Create {
	
	private Create() { }

	/**
	 * Generate character's stats -JWT-
	 */
	public static void generateStats() {
		int[] dice = new int[18];
		int total;
		
		do {
			total = 0;
			for (int i = 0; i < 18; i++) {
				dice[i] = Rnd.randomInt (3 + i % 3); // Roll 3,4,5 sided dice once each
				total += dice[i];
			}
		} while (total <= 42 || total >= 54);
		
		for (int i = 0; i < 6; i++) {
			int offset = i * 3;
			Player.py.stats.maxStat[i] = 5 + dice[offset] + dice[offset + 1] + dice[offset + 2];
		}
	}
	
	/**
	 * Change a stat by given amount -JWT-
	 * 
	 * @param stat The stat to change
	 * @param amount The amount by which to chance the stat
	 */
	public static void changeStat(int stat, int amount) {
		int tmpStat = Player.py.stats.maxStat[stat];
		
		if (amount < 0) {
			for (int i = 0; i > amount; i--) {
				if (tmpStat > 108) {
					tmpStat--;
				} else if (tmpStat > 88) {
					tmpStat += -Rnd.randomInt(6) - 2;
				} else if (tmpStat > 18) {
					tmpStat += -Rnd.randomInt(15) - 5;
					if (tmpStat < 18) {
						tmpStat = 18;
					}
				} else if (tmpStat > 3) {
					tmpStat--;
				}
			}
		} else {
			for (int i = 0; i < amount; i++) {
				if (tmpStat < 18) {
					tmpStat++;
				} else if (tmpStat < 88) {
					tmpStat += Rnd.randomInt(15) + 5;
				} else if (tmpStat < 108) {
					tmpStat += Rnd.randomInt(6) + 2;
				} else if (tmpStat < 118) {
					tmpStat++;
				}
			}
		}
		
		Player.py.stats.maxStat[stat] = tmpStat;
	}
	
	/**
	 * Generate all stats and modify for race. -RGM-
	 * <p>
	 * Needed in a separate module so looping of character 
	 * selection would be allowed.
	 */
	public static void generateAllStats() {
		PlayerFlags flags = Player.py.flags;
		PlayerMisc misc = Player.py.misc;
		PlayerRaceType race = Player.race[misc.playerRace];
		PlayerStats stats = Player.py.stats;
		
		generateStats();
		changeStat(Constants.A_STR, race.strAdjust);
		changeStat(Constants.A_INT, race.intAdjust);
		changeStat(Constants.A_WIS, race.wisAdjust);
		changeStat(Constants.A_DEX, race.dexAdjust);
		changeStat(Constants.A_CON, race.conAdjust);
		changeStat(Constants.A_CHR, race.chrAdjust);
		misc.level = 1;
		
		for (int i = 0; i < 6; i++) {
			stats.curStat[i] = stats.maxStat[i];
			Misc3.setStatUseValue(i);
		}
		
		misc.searchChance = race.baseSearchChance;
		misc.baseToHit = race.baseToHit;
		misc.baseToHitBow = race.baseToHitBow;
		misc.freqOfSearch = race.freqOfSearch;
		misc.stealth = race.stealth;
		misc.savingThrow = race.baseSavingThrow;
		misc.hitDie = race.baseHitDie;
		misc.plusToDamage = Misc3.adjustToDamage();
		misc.plusToHit = Misc3.adjustToHit();
		misc.magicArmorClass = 0;
		misc.totalArmorClass = Misc3.adjustToAc();
		misc.expFactor = race.baseExpFactor;
		flags.seeInfrared = race.seeInfrared;
	}
	
	private static boolean validChoice(int choice, int max) {
		return choice >= 0 && choice < max;
	}
	
	private static void printRaces() {
		IO.clearFrom(20);
		IO.putBuffer("Choose a race (? for Help):", 20, 2);
		
		int col = 2;
		int row = 21;
		for (int i = 0; i < Constants.MAX_RACES; i++) {
			IO.putBuffer(String.format("%c) %s", i + 'a', Player.race[i].raceType), row, col);
			
			col += 15;
			if (col > 70) {
				col = 2;
				row++;
			}
		}
	}
	
	/**
	 * Allow player to select a race -JWT-
	 */
	public static void chooseRace() {
		printRaces();
		
		boolean done = false;
		do {
			IO.moveCursor(20, 30);
			char input = IO.inkey();
			int raceChoice = input - 'a';
			if (validChoice(raceChoice, Constants.MAX_RACES)) {
				Player.py.misc.playerRace = raceChoice;
				IO.putBuffer(Player.race[raceChoice].raceType, 3, 15);
				done = true;
			} else if (input == '?') {
				Files.helpfile(Config.MORIA_WELCOME);
			} else {
				IO.bell();
			}
		} while (!done);
	}
	
	/**
	 * Print the history of a character -JWT-
	 */
	public static void printHistory() {
		IO.putBuffer("Character Background", 14, 27);
		for (int i = 0; i < 4; i++) {
			IO.print(Player.py.misc.history[i], i + 15, 10);
		}
	}
	
	/**
	 * Get the racial history, determine social class -RAK-
	 * <p>
	 * Assumptions:
	 * <ul>
	 *   <li>Each race has init history beginning at (race-1)*3+1</li>
	 *   <li>All history parts are in ascending order</li>
	 * </ul>
	 */
	public static void generateHistory() {
		
		// Get a block of history text
		int histIndex = Player.py.misc.playerRace * 3 + 1;
		int currIndex = 0;
		int socialClass = Rnd.randomInt(4);
		StringBuilder historyBlock = new StringBuilder();
		
		do {
			while (Player.background[currIndex].chart != histIndex) {
				currIndex++;
			}
			int testRoll = Rnd.randomInt(100);
			while (testRoll > Player.background[currIndex].roll) {
				currIndex++;
			}
			BackgroundType background = Player.background[currIndex];
			historyBlock.append(background.info);
			socialClass += background.bonus - 50;
			if (histIndex > background.next) {
				currIndex = 0;
			}
			histIndex = background.next;
		} while (histIndex >= 1);
		
		String[] history = Player.py.misc.history;
		
		// clear the previous history strings
		for (int i = 0; i < 4; i++) {
			history[i] = "";
		}
		
		// Process block of history text for pretty output
		int lineCounter = 0;
		int len = historyBlock.length();
		int newStart = 0;
		while (len - newStart > 60) {
			int startPos = newStart;
			int endPos = startPos + 60;
			while (historyBlock.charAt(endPos - 1) != ' ') {
				endPos--;
			}
			newStart = endPos;
			while (historyBlock.charAt(endPos - 1) == ' ') {
				endPos--;
			}
			history[lineCounter++] = historyBlock.substring(startPos, endPos);
		}
		history[lineCounter] = historyBlock.substring(newStart);
		
		// Compute social class for player
		if (socialClass > 100) {
			socialClass = 100;
		} else if (socialClass < 1) {
			socialClass = 1;
		}
		Player.py.misc.socialClass = socialClass;
	}
	
	/**
	 * Get the character's sex -JWT-
	 */
	public static void chooseSex() {
		IO.clearFrom(20);
		IO.putBuffer("Choose a sex (? for Help):", 20, 2);
		IO.putBuffer("m) Male       f) Female", 21, 2);
		
		boolean done = false;
		do {
			IO.moveCursor(20, 29);
			// speed not important here
			char c = IO.inkey();
			if (c == 'f' || c == 'F') {
				Player.py.misc.isMale = false;
				IO.putBuffer("Female", 4, 15);
				done = true;
			} else if (c == 'm' || c == 'M') {
				Player.py.misc.isMale = true;
				IO.putBuffer("Male", 4, 15);
				done = true;
			} else if (c == '?') {
				Files.helpfile(Config.MORIA_WELCOME);
			} else {
				IO.bell();
			}
		} while (!done);
	}
	
	/**
	 * Computes character's age, height, and weight -JWT-
	 */
	public static void generateAhw() {
		PlayerMisc misc = Player.py.misc;
		PlayerRaceType race = Player.race[misc.playerRace];
		misc.age = race.baseAge + Rnd.randomInt(race.maxAge);
		if (misc.isMale) {
			misc.height = Rnd.randomIntNormalized(race.baseHeightMale, race.modHeightMale);
			misc.weight = Rnd.randomIntNormalized(race.baseWeightMale, race.modWeightMale);
		} else {
			misc.height = Rnd.randomIntNormalized(race.baseHeightFemale, race.modHeightFemale);
			misc.weight = Rnd.randomIntNormalized(race.baseWeightFemale, race.modWeightFemale);
		}
		misc.disarmChance = race.baseDisarmChance + Misc3.adjustToDisarm();
	}
	
	private static void printAvailableClasses() {
		int rtclass = Player.race[Player.py.misc.playerRace].rtclass;
		int col = 2;
		int row = 21;
		int mask = 0x1;
		
		IO.clearFrom(20);
		IO.putBuffer("Choose a class (? for Help):", 20, 2);
		for (int i = 0; i < Constants.MAX_CLASS; i++) {
			if ((rtclass & mask) != 0) {
				IO.putBuffer(String.format("%c) %s", i + 'a', Player.Class[i].title), row, col);
				col += 15;
				if (col > 70) {
					col = 2;
					row++;
				}
			}
			mask <<= 1;
		}
	}
	
	private static int getAvailableClasses(int[] availableClasses) {
		int rtclass = Player.race[Player.py.misc.playerClass].rtclass;
		int numClasses = 0;
		int mask = 0x1;
		
		for (int i = 0; i < Constants.MAX_CLASS; i++) {
			if ((rtclass & mask) != 0) {
				availableClasses[numClasses++] = i;
			}
			mask <<= 1;
		}
		
		return numClasses;
	}
	
	/**
	 * Get a character class -JWT-
	 */
	public static void chooseClass() {
		printAvailableClasses();
		
		int[] classes = new int[Constants.MAX_CLASS];
		int numClasses = getAvailableClasses(classes);
		
		Player.py.misc.playerClass = 0;
		boolean done = false;
		do {
			IO.moveCursor(20, 31);
			char input = IO.inkey();
			int classChoice = input - 'a';
			if (validChoice(classChoice, numClasses)) {
				Player.py.misc.playerClass = classes[classChoice];
				ClassType classType = Player.Class[Player.py.misc.playerClass];
				done = true;
				IO.clearFrom(20);
				IO.putBuffer(classType.title, 5, 15);
				
				// Adjust the stats for the class adjustment -RAK-
				changeStat(Constants.A_STR, classType.modStrAdjust);
				changeStat(Constants.A_INT, classType.modIntAdjust);
				changeStat(Constants.A_WIS, classType.modWisAdjust);
				changeStat(Constants.A_DEX, classType.modDexAdjust);
				changeStat(Constants.A_CON, classType.modConAdjust);
				changeStat(Constants.A_CHR, classType.modChrAdjust);
				for (int i = 0; i < 6; i++) {
					Player.py.stats.curStat[i] = Player.py.stats.maxStat[i];
					Misc3.setStatUseValue(i);
				}
				
				PlayerMisc misc = Player.py.misc;
				misc.plusToDamage = Misc3.adjustToDamage();	// Real values
				misc.plusToHit = Misc3.adjustToHit();
				misc.magicArmorClass = Misc3.adjustToAc();
				misc.totalArmorClass = 0;
				misc.displayPlusToDamage = misc.plusToDamage; // Displayed values
				misc.displayPlusToHit = misc.plusToHit;
				misc.displayPlusTotalArmorClass= misc.magicArmorClass;
				misc.displayPlusToArmorClass = misc.totalArmorClass + misc.displayPlusTotalArmorClass;
				
				// now set misc stats, do this after setting stats because
				// of con_adj() for hitpoints
				misc.hitDie += classType.adjHitpoints;
				misc.maxHitpoints = Misc3.adjustConstitution() + misc.hitDie;
				misc.currHitpoints = misc.maxHitpoints;
				misc.currHitpointsFraction = 0;
				
				// initialize hit_points array
				// put bounds on total possible hp, only succeed if it is within
				// 1/8 of average value
				int minValue = (Constants.MAX_PLAYER_LEVEL * 3 / 8 * (misc.hitDie - 1)) + Constants.MAX_PLAYER_LEVEL;
				int maxValue = (Constants.MAX_PLAYER_LEVEL * 5 / 8 * (misc.hitDie - 1)) + Constants.MAX_PLAYER_LEVEL;
				Player.hitpoints[0] = misc.hitDie;
				do {
					for (int i = 1; i < Constants.MAX_PLAYER_LEVEL; i++) {
						Player.hitpoints[i] = Rnd.randomInt(misc.hitDie);
						Player.hitpoints[i] += Player.hitpoints[i - 1];
					}
				} while ((Player.hitpoints[Constants.MAX_PLAYER_LEVEL - 1] < minValue) || (Player.hitpoints[Constants.MAX_PLAYER_LEVEL - 1] > maxValue));
				
				misc.baseToHit += classType.modBaseToHit;
				misc.baseToHitBow += classType.modBaseToHitBow;	/*RAK*/
				misc.searchChance += classType.modSearch;
				misc.disarmChance += classType.modDisarm;
				misc.freqOfSearch += classType.modFreqOfSearch;
				misc.stealth += classType.modStealth;
				misc.savingThrow += classType.modSavingThrow;
				misc.expFactor += classType.modExpFactor;
			} else if (input == '?') {
				Files.helpfile(Config.MORIA_WELCOME);
			} else {
				IO.bell();
			}
		} while (!done);
	}
	
	/**
	 * Given a stat value, return a monetary value, which affects the amount
	 * of gold a player has.
	 * 
	 * @param stat The stat whose value is calculated
	 * @return The monetary value of the stat
	 */
	public static int monetaryValue(int stat) {
		return 5 * (stat - 10);
	}
	
	private static int socialClassAdjust() {
		return Player.py.misc.socialClass * 6 + Rnd.randomInt(25) + 325;
	}
	
	private static int statAdjust() {
		int[] stats = Player.py.stats.maxStat;
		return monetaryValue(stats[Constants.A_STR])
				+ monetaryValue(stats[Constants.A_INT])
				+ monetaryValue(stats[Constants.A_WIS])
				+ monetaryValue(stats[Constants.A_CON])
				+ monetaryValue(stats[Constants.A_DEX]);
	}
	
	private static int charismaAdjust() {
		int[] stats = Player.py.stats.maxStat;
		return monetaryValue(stats[Constants.A_CHR]);
	}
	
	public static void generateMoney() {
		int gold = socialClassAdjust() - statAdjust() + charismaAdjust();
		
		if (!Player.py.misc.isMale) {
			gold += 50;	// She charmed the banker into it! -CJS-
		}
		
		if (gold < 80) {
			gold = 80;	// Minimum
		}
		
		Player.py.misc.gold = gold;
	}
	
	/* ---------- M A I N  for Character Creation Routine ---------- */
	/*							-JWT-	*/
	/**
	 * Main character creation routine.
	 */
	public static void createCharacter() {
		Misc3.printCharacterInfo();
		chooseRace();
		chooseSex();
		
		// here we start a loop giving a player a choice of characters -RGM-
		generateAllStats();
		generateHistory();
		generateAhw();
		printHistory();
		Misc3.printAhws();
		Misc3.printStats();
		
		IO.clearFrom(20);
		IO.putBuffer("Hit space to reroll or ESC to accept characteristics: ", 20, 2);
		
		boolean done = false;
		do {
			IO.moveCursor(20, 56);
			char input = IO.inkey();
			if (input == Constants.ESCAPE) {
				done = true;
			} else if (input == ' ') {
				generateAllStats();
				generateHistory();
				generateAhw();
				printHistory();
				Misc3.printAhws();
				Misc3.printStats();
			} else {
				IO.bell();
			}
		} while (!done); // done with stats generation
		
		chooseClass();
		generateMoney();
		Misc3.printStats();
		Misc3.printLevelStats();
		Misc3.printAbilities();
		Misc3.chooseName();
		
		// This delay may be reduced, but is recommended to keep players
		// from continuously rolling up characters, which can be VERY
		// expensive CPU wise.
		IO.pauseExit(23, Constants.PLAYER_EXIT_PAUSE);
	}
}
