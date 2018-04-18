/*
 * Files.java: misc code to access files used by Moria
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;

import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Files {
	
	private Files() { }
	
	/**
	 * Open the score file while we still have the setuid privileges. Later
	 * when the score is being written out, you must be sure to flock the file
	 * so we don't have multiple people trying to write to it at the same time.
	 * <p>
	 * Craig Norborg (doc) Mon Aug 10 16:41:59 EST 1987
	 */
	public static void initScoreFile() {
		Variable.highscoreFile = new File(Config.MORIA_TOP);
		
		if (!Variable.highscoreFile.isFile()
				|| !Variable.highscoreFile.canRead()
				|| !Variable.highscoreFile.canWrite()) {
			System.err.printf("Can't open score file \"%s\"\n", Config.MORIA_TOP);
			System.exit(1);
			return;
		}
	}
	
	/**
	 * Operating hours for Moria -RAK-
	 * X = Open; . = Closed
	 */
	public static String[] days = {
			"SUN:XXXXXXXXXXXXXXXXXXXXXXXX",
			"MON:XXXXXXXX.........XXXXXXX",
			"TUE:XXXXXXXX.........XXXXXXX",
			"WED:XXXXXXXX.........XXXXXXX",
			"THU:XXXXXXXX.........XXXXXXX",
			"FRI:XXXXXXXX.........XXXXXXX",
			"SAT:XXXXXXXXXXXXXXXXXXXXXXXX"
	};
	
	/**
	 * Attempt to open the intro file. -RAK-
	 * <p>
	 * This routine also checks the hours file vs. what time it is -Doc
	 */
	public static void readTimes() {
		if (Config.MORIA_HOU.isEmpty()) {
			return;
		}
		
		// Attempt to read hours.dat. If it does not exist,
		// inform the user so he can tell the wizard about it
		try {
			List<String> lines = java.nio.file.Files.readAllLines(Paths.get(Config.MORIA_HOU));
			for (String line : lines) {
				if (line.length() > 3) {
					if (line.startsWith("SUN:")) {
						days[0] = line;
					} else if (line.startsWith("MON:")) {
						days[1] = line;
					} else if (line.startsWith("TUE:")) {
						days[2] = line;
					} else if (line.startsWith("WED:")) {
						days[3] = line;
					} else if (line.startsWith("THU:")) {
						days[4] = line;
					} else if (line.startsWith("FRI:")) {
						days[5] = line;
					} else if (line.startsWith("SAT:")) {
						days[6] = line;
					}
				}
			}
			
			// Check the hours, if closed then exit.
			if (!Misc1.checkTime()) {
				IO.clearScreen();
				int i = 0;
				for (String line : lines) {
					IO.putBuffer(line, i, 0);
					i++;
				}
				IO.pauseLine(23);
				Death.exitGame();
			}
		} catch (IOException e) {
			IO.restoreTerminal();
			System.err.printf("There is no hours file \"%s\".\n", Config.MORIA_HOU);
			System.err.printf("Please inform the wizard, %s, so he ", Config.WIZARD);
			System.err.printf("can correct this!\n");
			System.exit(1);
		}
		
		try {
			// Print the introduction message, news, etc.
			List<String> lines = java.nio.file.Files.readAllLines(Paths.get(Config.MORIA_MOR));
			IO.clearScreen();
			int i = 0;
			for (String line : lines) {
				IO.putBuffer(line, i, 0);
				i++;
			}
			IO.pauseLine(23);
		} catch (IOException e) {
			IO.restoreTerminal();
			System.err.printf("There is no news file \"%s\".\n", Config.MORIA_MOR);
			System.err.printf("Please inform the wizard, %s, so he ", Config.WIZARD);
			System.err.printf("can correct this!\n");
			System.exit(1);
		}
	}
	
	/**
	 * File perusal. -CJS-
	 * <p>
	 * Primitive, but portable.
	 * 
	 * @param filename the path to the help file
	 */
	public static void helpfile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			IO.print(String.format("Can not find help file \"%s\".\n", filename), 0, 0);
			return;
		}
		
		IO.saveScreen();
		
		try {
			List<String> lines = java.nio.file.Files.readAllLines(Paths.get(filename));
			boolean eof = false;
			int lineIndex = 0;
			while (!eof) {
				IO.clearScreen();
				for (int i = 1; i < 23 && lineIndex < lines.size(); i++, lineIndex++) {
					IO.putBuffer(lines.get(lineIndex), i, 0);
				}
				if (lineIndex >= lines.size()) {
					eof = true;
				}
				IO.print("[Press any key to continue.]", 23, 23);
				char input = IO.inkey();
				if (input == Constants.ESCAPE) {
					break;
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		
		IO.restoreScreen();
	}
	
	/**
	 * Prints a list of random objects to a file. -RAK-
	 * <p>
	 * Note that the objects produced is a sampling of 
	 * objects which be expected to appear on that level.
	 */
	public static void printObjects() {
		IO.print("Produce objects on what level?: ", 0, 0);
		String strLevel = IO.getString(0, 32, 10).trim();
		if (strLevel.isEmpty()) {
			return;
		}
		
		int level = 0;
		try {
			level = Integer.parseInt(strLevel);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		IO.print("Produce how many objects?: ", 0, 0);
		boolean small = IO.getCheck("Small objects only?");
		String strNumObjects = IO.getString(0, 27, 10).trim();
		if (strNumObjects.isEmpty()) {
			return;
		}
		
		int numObjects = 0;
		try {
			numObjects = Integer.parseInt(strNumObjects);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			numObjects = 1;
		}
		
		if (numObjects > 0 && level > -1 && level < 1201) {
			if (numObjects > 10000) {
				numObjects = 10000;
			}
			
			IO.print("File name: ", 0, 0);
			String filename = IO.getString(0, 11, 64).trim();
			if (filename.isEmpty()) {
				return;
			}
			File file = new File(filename);
			if (!file.exists()) {
				IO.print("File could not be opened.", 0, 0);
				return;
			}
			
			IO.print(String.format("%d random objects being produced...", numObjects), 0, 0);
			IO.putQio();
			
			Writer output;
			try {
				output = new BufferedWriter(new FileWriter(file));
			} catch (IOException e) {
				IO.print("Files.print_objects(): Failed to open file as BufferedWriter.", 0, 0);
				e.printStackTrace();
				return;
			}
			
			try {
				output.write("*** Random Object Sampling:\n");
				output.write(String.format("*** %d objects\n", numObjects));
				output.write(String.format("*** For Level %d\n", level));
				output.write("\n");
				output.write("\n");
				
				int treasureIndex = Misc1.popTreasure();
				for (int i = 0; i < numObjects; i++) {
					Desc.copyIntoInventory(Treasure.treasureList[treasureIndex], Treasure.sortedObjects[Misc3.getRandomObjectForLevel(level, small)]);
					Misc2.addMagicToTreasure(treasureIndex, level);
					InvenType item = Treasure.treasureList[treasureIndex];
					Desc.setStoreBought(item);
					if ((item.flags & Constants.TR_CURSED) != 0) {
						Misc4.addInscription(item, Constants.ID_DAMD);
					}
					output.write(String.format("%d %s\n", item.level, Desc.describeObject(item, true)));
				}
				Misc1.pusht(treasureIndex);
				
				output.close();
				IO.print("Completed.", 0, 0);
			} catch (IOException e) {
				IO.print("Files.print_objects(): Failed to write to file.", 0, 0);
				e.printStackTrace();
				return;
			}
		} else {
			IO.print("Parameters no good.", 0, 0);
		}
	}
	
	/**
	 * Print the character to a file or device. -RAK-
	 * 
	 * @param filename the name of the file to write to
	 */
	public static boolean fileCharacter(String filename) {
		File file = new File(filename);
		if (!file.isFile()) {
			IO.print("Files.fileCharacter(): Should not be a directory: " + file, 0, 0);
			return false;
		}
		
		if (!file.canWrite()) {
			IO.print("Files.fileCharacter(): File cannot be written: " + file, 0, 0);
			return false;
		}
		
		try {
			Writer output = new BufferedWriter(new FileWriter(file));
			IO.print("Writing character sheet...", 0, 0);
			IO.putQio();
			final String colon = ":";
			final String blank = " ";
			
			output.write(String.format("%c\n\n", (Constants.CTRL & 'L')));
			output.write(String.format(" Name%9s %-23s", colon, Player.py.misc.name));
			output.write(String.format(" Age%11s %6d", colon, Player.py.misc.age));
			output.write(String.format("   STR : %s\n", Misc3.convertStat(Player.py.stats.useStat[Constants.A_STR])));
			output.write(String.format(" Race%9s %-23s", colon, Player.race[Player.py.misc.playerRace].raceType));
			output.write(String.format(" Height%8s %6d", colon, Player.py.misc.height));
			output.write(String.format("   INT : %s\n", Misc3.convertStat(Player.py.stats.useStat[Constants.A_INT])));
			output.write(String.format(" Sex%10s %-23s", colon, (Player.py.misc.isMale ? "Male" : "Female")));
			output.write(String.format(" Weight%8s %6d", colon, Player.py.misc.weight));
			output.write(String.format("   WIS : %s\n", Misc3.convertStat(Player.py.stats.useStat[Constants.A_WIS])));
			output.write(String.format(" Class%8s %-23s", colon, Player.Class[Player.py.misc.playerClass].title));
			output.write(String.format(" Social Class : %6d", Player.py.misc.socialClass));
			output.write(String.format("   DEX : %s\n", Misc3.convertStat(Player.py.stats.useStat[Constants.A_DEX])));
			output.write(String.format(" Title%8s %-23s", colon, Misc3.getPlayerTitle()));
			output.write(String.format("%22s", blank));
			output.write(String.format( "   CON : %s\n", Misc3.convertStat(Player.py.stats.useStat[Constants.A_CON])));
			output.write(String.format("%34s", blank));
			output.write(String.format("%26s", blank));
			output.write(String.format("   CHR : %s\n\n", Misc3.convertStat(Player.py.stats.useStat[Constants.A_CHR])));
			
			output.write(String.format(" + To Hit    : %6d", Player.py.misc.displayPlusToHit));
			output.write(String.format("%7sLevel      : %7d", blank, Player.py.misc.level));
			output.write(String.format("    Max Hit Points : %6d\n", Player.py.misc.maxHitpoints));
			output.write(String.format(" + To Damage : %6d", Player.py.misc.displayPlusToDamage));
			output.write(String.format("%7sExperience : %7d", blank, Player.py.misc.currExp));
			output.write(String.format("    Cur Hit Points : %6d\n", Player.py.misc.currHitpoints));
			output.write(String.format(" + To AC     : %6d", Player.py.misc.displayPlusTotalArmorClass));
			output.write(String.format("%7sMax Exp    : %7d", blank, Player.py.misc.maxExp));
			output.write(String.format("    Max Mana%8s %6d\n", colon, Player.py.misc.maxMana));
			output.write(String.format("   Total AC  : %6d", Player.py.misc.displayPlusToArmorClass));
			
			if (Player.py.misc.level == Constants.MAX_PLAYER_LEVEL) {
				output.write(String.format("%7sExp to Adv : *******", blank));
			} else {
				output.write(String.format("%7sExp to Adv : %7d", blank, (Player.exp[Player.py.misc.level - 1] * Player.py.misc.expFactor / 100)));
			}
			
			output.write(String.format("    Cur Mana%8s %6d\n", colon, Player.py.misc.currMana));
			output.write(String.format("%28sGold%8s %7d\n\n", blank, colon, Player.py.misc.gold));
			
			PlayerMisc misc = Player.py.misc;
			int xbth = misc.baseToHit + misc.plusToHit * Constants.BTH_PLUS_ADJ + (Player.classLevelAdjust[misc.playerClass][Constants.CLA_BTH] * misc.level);
			int xbthb = misc.baseToHitBow + misc.plusToHit * Constants.BTH_PLUS_ADJ + (Player.classLevelAdjust[misc.playerClass][Constants.CLA_BTHB] * misc.level);
			
			// this results in a range from 0 to 29
			int xfos = 40 - misc.freqOfSearch;
			if (xfos < 0) {
				xfos = 0;
			}
			int xsrh = misc.searchChance;
			
			// this results in a range from 0 to 9
			int xstl = misc.stealth + 1;
			int xdis = misc.disarmChance + 2 * Misc3.adjustToDisarm() + Misc3.adjustStat(Constants.A_INT)
					+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DISARM] * misc.level / 3);
			int xsave = misc.savingThrow + Misc3.adjustStat(Constants.A_WIS)
					+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_SAVE] * misc.level / 3);
			int xdev = misc.savingThrow + Misc3.adjustStat(Constants.A_INT)
					+ (Player.classLevelAdjust[misc.playerClass][Constants.CLA_DEVICE] * misc.level / 3);
			
			String xinfra = String.format("%d feet", Player.py.flags.seeInfrared * 10);
			
			output.write("(Miscellaneous Abilities)\n\n");
			output.write(String.format(" Fighting    : %-10s", Misc3.likeRating(xbth, 12)));
			output.write(String.format("   Stealth     : %-10s", Misc3.likeRating(xstl, 1)));
			output.write(String.format("   Perception  : %s\n", Misc3.likeRating(xfos, 3)));
			output.write(String.format(" Bows/Throw  : %-10s", Misc3.likeRating(xbthb, 12)));
			output.write(String.format("   Disarming   : %-10s", Misc3.likeRating(xdis, 8)));
			output.write(String.format("   Searching   : %s\n", Misc3.likeRating(xsrh, 6)));
			output.write(String.format(" Saving Throw: %-10s", Misc3.likeRating(xsave, 6)));
			output.write(String.format("   Magic Device: %-10s", Misc3.likeRating(xdev, 6)));
			output.write(String.format("   Infra-Vision: %s\n\n", xinfra));
			
			// Write out the character's history
			output.write("Character Background\n");
			for (int i = 0; i < 4; i++) {
				output.write(String.format(" %s\n", Player.py.misc.history[i]));
			}
			
			// Write out the equipment list.
			int j = 0;
			output.write(String.format("\n  [Character's Equipment List]\n\n"));
			if (Treasure.equipCounter == 0) {
				output.write(String.format("  Character has no equipment in use.\n"));
			} else {
				for (int i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
					InvenType item = Treasure.inventory[i];
					if (item.category != Constants.TV_NOTHING) {
						String prefix;
						switch (i) {
						case Constants.INVEN_WIELD:
							prefix = "You are wielding";
							break;
						case Constants.INVEN_HEAD:
							prefix = "Worn on head";
							break;
						case Constants.INVEN_NECK:
							prefix = "Worn around neck";
							break;
						case Constants.INVEN_BODY:
							prefix = "Worn on body";
							break;
						case Constants.INVEN_ARM:
							prefix = "Worn on shield arm";
							break;
						case Constants.INVEN_HANDS:
							prefix = "Worn on hands";
							break;
						case Constants.INVEN_RIGHT:
							prefix = "Right ring finger";
							break;
						case Constants.INVEN_LEFT:
							prefix = "Left  ring finger";
							break;
						case Constants.INVEN_FEET:
							prefix = "Worn on feet";
							break;
						case Constants.INVEN_OUTER:
							prefix = "Worn about body";
							break;
						case Constants.INVEN_LIGHT:
							prefix = "Light source is";
							break;
						case Constants.INVEN_AUX:
							prefix = "Secondary weapon";
							break;
						default:
							prefix = "*Unknown value*";
							break;
						}
						String itemDesc = Desc.describeObject(Treasure.inventory[i], true);
						output.write(String.format("  %c) %-19s: %s\n", j + 'a', prefix, itemDesc));
						j++;
					}
				}
			}
			
			// Write out the character's inventory.
			output.write(String.format("%c\n\n", (Constants.CTRL & 'L')));
			output.write("  [General Inventory List]\n\n");
			if (Treasure.invenCounter == 0) {
				output.write("  Character has no objects in inventory.\n");
			} else {
				for (int i = 0; i < Treasure.invenCounter; i++) {
					String itemDesc = Desc.describeObject(Treasure.inventory[i], true);
					output.write(String.format("%c) %s\n", i + 'a', itemDesc));
				}
			}
			output.write(String.format("%c", (Constants.CTRL & 'L')));
			output.close();
			IO.print("Completed.", 0, 0);
			return true;
		} catch (IOException e) {
			IO.printMessage(String.format("Can't open file %s:", filename));
			e.printStackTrace();
			return false;
		}
	}
}
