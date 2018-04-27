/*
 * Wizard.java: Version history and info, and wizard mode debugging aids.
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

import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.CharPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Wizard {
	
	private Wizard() { }
	
	/**
	 * Light up the dungeon. -RAK-
	 */
	public static void wizardLight() {
		boolean permReveal;
		if (Variable.cave[Player.y][Player.x].permLight) {
			permReveal = false;
		} else {
			permReveal = true;
		}
		
		for (int y = 0; y < Variable.currHeight; y++) {
			for (int x = 0; x < Variable.currWidth; x++) {
				if (Variable.cave[y][x].fval <= Constants.MAX_CAVE_FLOOR) {
					for (int k = y - 1; k <= y + 1; k++) {
						for (int l = x - 1; l <= x + 1; l++) {
							CaveType cavePos = Variable.cave[k][l];
							cavePos.permLight = permReveal;
							if (!permReveal) {
								cavePos.fieldMark = false;
							}
						}
					}
				}
			}
		}
		Misc1.printMap();
	}
	
	/**
	 * Wizard routine for gaining on stats. -RAK-
	 */
	public static void changeCharacter() {
		changeStrength();
		changeIntelligence();
		changeWisdom();
		changeDexterity();
		changeConstitution();
		changeCharisma();
		changeHitpoints();
		changeMana();
		changeGold();
		changeSearchChance();
		changeStealth();
		changeDisarmChance();
		changeSavingThrow();
		changeBaseToHit();
		changeBaseToHitBow();
		changeWeight();
		changeSpeed();
	}
	
	private static void changeStrength() {
		IO.print("(3 - 118) Strength     = ", 0, 0);
		String response = IO.getString(0, 25, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newStrength = Integer.parseInt(response);
			if (newStrength > 2 && newStrength < 119) {
				Player.py.stats.maxStat[Constants.A_STR] = newStrength;
				Misc3.restoreStat(Constants.A_STR);
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeStrength()");
		}
	}
	
	private static void changeIntelligence() {
		IO.print("(3 - 118) Intelligence = ", 0, 0);
		String response = IO.getString(0, 25, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newIntelligence = Integer.parseInt(response);
			if (newIntelligence > 2 && newIntelligence < 119) {
				Player.py.stats.maxStat[Constants.A_INT] = newIntelligence;
				Misc3.restoreStat(Constants.A_INT);
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeIntelligence()");
		}
	}
	
	private static void changeWisdom() {
		IO.print("(3 - 118) Wisdom       = ", 0, 0);
		String response = IO.getString(0, 25, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newWisdom = Integer.parseInt(response);
			if (newWisdom > 2 && newWisdom < 119) {
				Player.py.stats.maxStat[Constants.A_WIS] = newWisdom;
				Misc3.restoreStat(Constants.A_WIS);
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeWisdom()");
		}
	}
	
	private static void changeDexterity() {
		IO.print("(3 - 118) Dexterity    = ", 0, 0);
		String response = IO.getString(0, 25, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newDexterity = Integer.parseInt(response);
			if (newDexterity > 2 && newDexterity < 119) {
				Player.py.stats.maxStat[Constants.A_DEX] = newDexterity;
				Misc3.restoreStat(Constants.A_DEX);
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeDexterity()");
		}
	}
	
	private static void changeConstitution() {
		IO.print("(3 - 118) Constitution = ", 0, 0);
		String response = IO.getString(0, 25, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newConstitution = Integer.parseInt(response);
			if (newConstitution > 2 && newConstitution < 119) {
				Player.py.stats.maxStat[Constants.A_CON] = newConstitution;
				Misc3.restoreStat(Constants.A_CON);
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeConstitution()");
		}
	}
	
	private static void changeCharisma() {
		IO.print("(3 - 118) Charisma     = ", 0, 0);
		String response = IO.getString(0, 25, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newCharisma = Integer.parseInt(response);
			if (newCharisma > 2 && newCharisma < 119) {
				Player.py.stats.maxStat[Constants.A_CHR] = newCharisma;
				Misc3.restoreStat(Constants.A_CHR);
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeCharisma()");
		}
	}
	
	private static void changeHitpoints() {
		IO.print("(1 - 32767) Hit points = ", 0, 0);
		String response = IO.getString(0, 25, 5);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newHitpoints = Integer.parseInt(response);
			if (newHitpoints > 0 && newHitpoints <= Constants.MAX_SHORT) {
				PlayerMisc misc = Player.py.misc;
				misc.maxHitpoints = newHitpoints;
				misc.currHitpoints = newHitpoints;
				misc.currHitpointsFraction = 0;
				Misc3.printMaxHitpoints();
				Misc3.printCurrentHitpoints();
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeHitpoints()");
		}
	}
	
	private static void changeMana() {
		IO.print("(0 - 32767) Mana       = ", 0, 0);
		String response = IO.getString(0, 25, 5);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newMana = Integer.parseInt(response);
			if (newMana > -1 && newMana <= Constants.MAX_SHORT) {
				PlayerMisc misc = Player.py.misc;
				misc.maxMana = newMana;
				misc.currMana = newMana;
				misc.currManaFraction = 0;
				Misc3.printCurrentMana();
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeMana()");
		}
	}
	
	private static void changeGold() {
		PlayerMisc misc = Player.py.misc;
		String prompt = String.format("Current=%d  Gold = ", misc.gold);
		IO.print(prompt, 0, 0);
		String response = IO.getString(0, prompt.length(), 7);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newGold = Integer.parseInt(response);
			if (newGold > -1) {
				misc.gold = newGold;
				Misc3.printGold();
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeGold()");
		}
	}
	
	private static void changeSearchChance() {
		PlayerMisc misc = Player.py.misc;
		String prompt = String.format("Current=%d  (0-200) Searching = ", misc.searchChance);
		IO.print(prompt, 0, 0);
		String response = IO.getString(0, prompt.length(), 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newChance = Integer.parseInt(response);
			if (newChance > -1 && newChance < 201) {
				misc.searchChance = newChance;
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeSearchChance()");
		}
	}
	
	private static void changeStealth() {
		PlayerMisc misc = Player.py.misc;
		String response = String.format("Current=%d  (-1-18) Stealth = ", misc.stealth);
		IO.print(response, 0, 0);
		response = IO.getString(0, response.length(), 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newStealth = Integer.parseInt(response);
			if (newStealth > -2 && newStealth < 19) {
				misc.stealth = newStealth;
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeStealth()");
		}
	}
	
	private static void changeDisarmChance() {
		PlayerMisc misc = Player.py.misc;
		String prompt = String.format("Current=%d  (0-200) Disarming = ", misc.disarmChance);
		IO.print(prompt, 0, 0);
		String response = IO.getString(0, prompt.length(), 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newChance = Integer.parseInt(response);
			if (newChance > -1 && newChance < 201) {
				misc.disarmChance = newChance;
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeDisarmChance()");
		}
	}
	
	private static void changeSavingThrow() {
		PlayerMisc misc = Player.py.misc;
		String prompt = String.format("Current=%d  (0-100) Save = ", misc.savingThrow);
		IO.print(prompt, 0, 0);
		String response = IO.getString(0, prompt.length(), 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newSavingThrow = Integer.parseInt(response);
			if (newSavingThrow > -1 && newSavingThrow < 201) {
				misc.savingThrow = newSavingThrow;
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeSavingThrow()");
		}
	}
	
	private static void changeBaseToHit() {
		PlayerMisc misc = Player.py.misc;
		String prompt = String.format("Current=%d  (0-200) Base to hit = ", misc.baseToHit);
		IO.print(prompt, 0, 0);
		String response = IO.getString(0, prompt.length(), 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newBaseToHit = Integer.parseInt(response);
			if (newBaseToHit > -1 && newBaseToHit < 201) {
				misc.baseToHit = newBaseToHit;
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeBaseToHit()");
		}
	}
	
	private static void changeBaseToHitBow() {
		PlayerMisc misc = Player.py.misc;
		String prompt = String.format("Current=%d  (0-200) Bows/Throwing = ", misc.baseToHitBow);
		IO.print(prompt, 0, 0);
		String response = IO.getString(0, prompt.length(), 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newBaseToHitBow = Integer.parseInt(response);
			if (newBaseToHitBow > -1 && newBaseToHitBow < 201) {
				misc.baseToHitBow = newBaseToHitBow;
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeBaseToHitBow()");
		}
	}
	
	private static void changeWeight() {
		PlayerMisc misc = Player.py.misc;
		String prompt = String.format("Current=%d  Weight = ", misc.weight);
		IO.print(prompt, 0, 0);
		String response = IO.getString(0, prompt.length(), 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			int newWeight = Integer.parseInt(response);
			if (newWeight > -1) {
				misc.weight = newWeight;
			}
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.changeWeight()");
		}
	}
	
	private static void changeSpeed() {
		CharPointer speedAdjust = new CharPointer();
		while(IO.getCommand("Alter speed? (+/-)", speedAdjust)) {
			if (speedAdjust.value() == '+') {
				Moria1.changeSpeed(-1);
			} else if (speedAdjust.value() == '-') {
				Moria1.changeSpeed(1);
			} else {
				break;
			}
			Misc3.printSpeed();
		}
	}
	
	/**
	 * Wizard routine for creating objects. -RAK-
	 */
	public static void wizardCreate() {
		InvenType forge = new InvenType();
		//char[] pattern = new char[4];
		
		IO.printMessage("Warning: This routine can cause a fatal error.");
		forge.index = Constants.OBJ_WIZARD;
		forge.specialName = 0;
		Misc4.inscribe(forge, "wizard item");
		forge.identify = Constants.ID_KNOWN2|Constants.ID_STOREBOUGHT;
		
		getTval(forge);
		getTchar(forge);
		getSubval(forge);
		getWeight(forge);
		getNumber(forge);
		getDamageDice(forge);
		getDamageSides(forge);
		getPlussesToHit(forge);
		getPlussesToDamage(forge);
		getAC(forge);
		getPlussesToAC(forge);
		getP1(forge);
		getFlags(forge);
		getCost(forge);
		getLevel(forge);
		confirm(forge);
	}
	
	private static void getTval(InvenType forge) {
		IO.print("Tval   : ", 0, 0);
		String response = IO.getString(0, 9, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.category = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getTval()");
		}
	}
	
	private static void getTchar(InvenType forge) {
		IO.print("Tchar  : ", 0, 0);
		String response = IO.getString(0, 9, 1);
		if (response.isEmpty()) {
			return;
		}
		forge.tchar = response.charAt(0);
	}
	
	private static void getSubval(InvenType forge) {
		IO.print("Subval : ", 0, 0);
		String response = IO.getString(0, 9, 5);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.subCategory = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getSubval()");
		}
	}
	
	private static void getWeight(InvenType forge) {
		IO.print("Weight : ", 0, 0);
		String response = IO.getString(0, 9, 5);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.weight = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getWeight()");
		}
	}
	
	private static void getNumber(InvenType forge) {
		IO.print("Number : ", 0, 0);
		String response = IO.getString(0, 9, 5);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.number = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getNumber()");
		}
	}
	
	private static void getDamageDice(InvenType forge) {
		IO.print("Damage (dice): ", 0, 0);
		String response = IO.getString(0, 15, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.damage[0] = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getDamageDice()");
		}
	}
	
	private static void getDamageSides(InvenType forge) {
		IO.print("Damage (sides): ", 0, 0);
		String response = IO.getString(0, 16, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.damage[1] = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getDamageSides()");
		}
	}
	
	private static void getPlussesToHit(InvenType forge) {
		IO.print("+To hit: ", 0, 0);
		String response = IO.getString(0, 9, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.tohit = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getPlussesToHit()");
		}
	}
	
	private static void getPlussesToDamage(InvenType forge) {
		IO.print("+To dam: ", 0, 0);
		String response = IO.getString(0, 9, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.plusToDam = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getPlussesToDamage()");
		}
	}
	
	private static void getAC(InvenType forge) {
		IO.print("AC     : ", 0, 0);
		String response = IO.getString(0, 9, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.armorClass = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getAC()");
		}
	}
	
	private static void getPlussesToAC(InvenType forge) {
		IO.print("+To AC : ", 0, 0);
		String response = IO.getString(0, 9, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.plusToArmorClass = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getPlussesToAC()");
		}
	}
	
	private static void getP1(InvenType forge) {
		IO.print("P1     : ", 0, 0);
		String response = IO.getString(0, 9, 5);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.misc = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getP1()");
		}
	}
	
	private static void getFlags(InvenType forge) {
		IO.print("Flags (In HEX): ", 0, 0);
		String response = IO.getString(0, 16, 8);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			//finds a long in tmp_str and places it in tmp_lval
			//pattern = "%lx";
			//sscanf(tmp_str, pattern, tmp_lval);
			forge.flags = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getFlags()");
		}
	}
	
	private static void getCost(InvenType forge) {
		IO.print("Cost : ", 0, 0);
		String response = IO.getString(0, 9, 8);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.cost = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getCost()");
		}
	}
	
	private static void getLevel(InvenType forge) {
		IO.print("Level : ", 0, 0);
		String response = IO.getString(0, 10, 3);
		if (response.isEmpty()) {
			return;
		}
		
		try {
			forge.level = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			System.err.println("Could not process response in Wizard.getLevel()");
		}
	}
	
	private static void confirm(InvenType forge) {
		if (IO.getCheck("Allocate?")) {
			// delete object first if any, before call popt
			CaveType cavePos = Variable.cave[Player.y][Player.x];
			if (cavePos.treasureIndex != 0) {
				Moria3.deleteObject(Player.y, Player.x);
			}
			
			int index = Misc1.popTreasure();
			forge.copyInto(Treasure.treasureList[index]);
			cavePos.treasureIndex = index;
			IO.printMessage("Allocated.");
		} else {
			IO.printMessage("Aborted.");
		}
	}
}
