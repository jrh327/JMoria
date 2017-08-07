/*
 * Moria1.java: misc code, mainly handles player movement, inventory, etc
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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;

public class Moria1 {
	
	private Moria1() { }
	
	/**
	 * Changes speed of monsters relative to player -RAK-
	 * <p>
	 * Note: When the player is sped up or slowed down, I simply
	 *   change the speed of all the monsters. This greatly
	 *   simplified the logic.
	 * 
	 * @param amount Amount to change speed by
	 */
	public static void changeSpeed(int amount) {
		Player.py.flags.speed += amount;
		Player.py.flags.status |= Constants.PY_SPEED;
		for (int i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			Monsters.monsterList[i].speed += amount;
		}
	}
	
	/**
	 * Player bonuses -RAK-
	 * <p>
	 * When an item is worn or taken off, this re-adjusts the player
	 * bonuses.
	 * <p>
	 * Factor=1 : wear; Factor=-1 : removed
	 * <p>
	 * Only calculates properties with cumulative effect. Properties that
	 * depend on everything being worn are recalculated by calcBonuses() -CJS-
	 */
	public static void adjustPlayerBonuses(InvenType t_ptr, int factor) {
		int amount = t_ptr.misc * factor;
		if ((t_ptr.flags & Constants.TR_STATS) != 0) {
			for (int i = 0; i < 6; i++) {
				if (((1 << i) & t_ptr.flags) != 0) {
					Misc3.boostStat(i, amount);
				}
			}
		}
		if ((Constants.TR_SEARCH & t_ptr.flags) != 0) {
			Player.py.misc.searchChance += amount;
			Player.py.misc.freqOfSearch -= amount;
		}
		if ((Constants.TR_STEALTH & t_ptr.flags) != 0) {
			Player.py.misc.stealth += amount;
		}
		if ((Constants.TR_SPEED & t_ptr.flags) != 0) {
			changeSpeed(-amount);
		}
		if ((Constants.TR_BLIND & t_ptr.flags) != 0 && (factor > 0)) {
			Player.py.flags.blind += 1000;
		}
		if ((Constants.TR_TIMID & t_ptr.flags) != 0 && (factor > 0)) {
			Player.py.flags.afraid += 50;
		}
		if ((Constants.TR_INFRA & t_ptr.flags) != 0) {
			Player.py.flags.seeInfrared += amount;
		}
	}
	
	/**
	 * Recalculate the effect of all the stuff we use. -CJS-
	 */
	public static void calcBonuses() {
		PlayerFlags playerFlags = Player.py.flags;
		PlayerMisc misc = Player.py.misc;
		if (playerFlags.slowDigestion) {
			playerFlags.foodDigested++;
		}
		if (playerFlags.regenerate) {
			playerFlags.foodDigested -= 3;
		}
		playerFlags.seeInvisible = false;
		playerFlags.teleport = Constants.FALSE;
		playerFlags.freeAct = false;
		playerFlags.slowDigestion = false;
		playerFlags.aggravate = Constants.FALSE;
		playerFlags.sustainStr = false;
		playerFlags.sustainInt = false;
		playerFlags.sustainWis = false;
		playerFlags.sustainCon = false;
		playerFlags.sustainDex = false;
		playerFlags.sustainChr = false;
		playerFlags.fireResistance = Constants.FALSE;
		playerFlags.acidResistance = Constants.FALSE;
		playerFlags.coldResistance = Constants.FALSE;
		playerFlags.regenerate = false;
		playerFlags.lightningResistance = Constants.FALSE;
		playerFlags.freeFall = Constants.FALSE;
		
		int oldDisplayAC = misc.displayPlusToArmorClass;
		misc.plusToHit = Misc3.adjustToHit(); // Real To Hit
		misc.plusToDamage = Misc3.adjustToDamage(); // Real To Dam
		misc.magicArmorClass = Misc3.adjustToAc(); // Real To AC
		misc.totalArmorClass = 0; // Real AC
		misc.displayPlusToHit = misc.plusToHit; // Display To Hit
		misc.displayPlusToDamage = misc.plusToDamage; // Display To Dam
		misc.displayPlusToArmorClass = 0; // Display AC
		misc.displayPlusTotalArmorClass	= misc.magicArmorClass; // Display To AC
		
		for (int i = Constants.INVEN_WIELD; i < Constants.INVEN_LIGHT; i++) {
			InvenType item = Treasure.inventory[i];
			if (item.category != Constants.TV_NOTHING) {
				misc.plusToHit += item.tohit;
				if (item.category != Constants.TV_BOW) { // Bows can't damage. -CJS-
					misc.plusToDamage += item.plusToDam;
				}
				misc.magicArmorClass += item.plusToArmorClass;
				misc.totalArmorClass += item.armorClass;
				if (Desc.arePlussesKnownByPlayer(item)) {
					misc.displayPlusToHit += item.tohit;
					if (item.category != Constants.TV_BOW) {
						misc.displayPlusToDamage += item.plusToDam; // Bows can't damage. -CJS-
					}
					misc.displayPlusTotalArmorClass += item.plusToArmorClass;
					misc.displayPlusToArmorClass += item.armorClass;
				} else if ((Constants.TR_CURSED & item.flags) == 0) {
					// Base AC values should always be visible, as long as the item
					// is not cursed.
					misc.displayPlusToArmorClass += item.armorClass;
				}
			}
		}
		misc.displayPlusToArmorClass += misc.displayPlusTotalArmorClass;
		
		if (Variable.isWeaponHeavy) {
			misc.displayPlusToHit += (Player.py.stats.useStat[Constants.A_STR] * 15
					- Treasure.inventory[Constants.INVEN_WIELD].weight);
		}
		
		// Add in temporary spell increases
		if (playerFlags.invulnerability > 0) {
			misc.totalArmorClass += 100;
			misc.displayPlusToArmorClass += 100;
		}
		if (playerFlags.blessed > 0) {
			misc.totalArmorClass += 2;
			misc.displayPlusToArmorClass += 2;
		}
		if (playerFlags.detectInvisible > 0) {
			playerFlags.seeInvisible = true;
		}
		
		// can't print AC here because might be in a store
		if (oldDisplayAC != misc.displayPlusToArmorClass) {
			playerFlags.status |= Constants.PY_ARMOR;
		}
		
		int itemFlags = 0;
		for (int i = Constants.INVEN_WIELD; i < Constants.INVEN_LIGHT; i++) {
			InvenType item = Treasure.inventory[i];
			itemFlags |= item.flags;
		}
		if ((Constants.TR_SLOW_DIGEST & itemFlags) != 0) {
			playerFlags.slowDigestion = true;
		}
		if ((Constants.TR_AGGRAVATE & itemFlags) != 0) {
			playerFlags.aggravate = Constants.TRUE;
		}
		if ((Constants.TR_TELEPORT & itemFlags) != 0) {
			playerFlags.teleport = Constants.TRUE;
		}
		if ((Constants.TR_REGEN & itemFlags) != 0) {
			playerFlags.regenerate = true;
		}
		if ((Constants.TR_RES_FIRE & itemFlags) != 0) {
			playerFlags.fireResistance = Constants.TRUE;
		}
		if ((Constants.TR_RES_ACID & itemFlags) != 0) {
			playerFlags.acidResistance = Constants.TRUE;
		}
		if ((Constants.TR_RES_COLD & itemFlags) != 0) {
			playerFlags.coldResistance = Constants.TRUE;
		}
		if ((Constants.TR_FREE_ACT & itemFlags) != 0) {
			playerFlags.freeAct = true;
		}
		if ((Constants.TR_SEE_INVIS & itemFlags) != 0) {
			playerFlags.seeInvisible = true;
		}
		if ((Constants.TR_RES_LIGHT & itemFlags) != 0) {
			playerFlags.lightningResistance = Constants.TRUE;
		}
		if ((Constants.TR_FFALL & itemFlags) != 0) {
			playerFlags.freeFall = Constants.TRUE;
		}
		
		for (int i = Constants.INVEN_WIELD; i < Constants.INVEN_LIGHT; i++) {
			InvenType item = Treasure.inventory[i];
			if ((Constants.TR_SUST_STAT & item.flags) != 0) {
				switch (item.misc) {
				case 1:
					playerFlags.sustainStr = true;
					break;
				case 2:
					playerFlags.sustainInt = true;
					break;
				case 3:
					playerFlags.sustainWis = true;
					break;
				case 4:
					playerFlags.sustainCon = true;
					break;
				case 5:
					playerFlags.sustainDex = true;
					break;
				case 6:
					playerFlags.sustainChr = true;
					break;
				default:
					break;
				}
			}
		}
		
		if (playerFlags.slowDigestion) {
			playerFlags.foodDigested--;
		}
		if (playerFlags.regenerate) {
			playerFlags.foodDigested += 3;
		}
	}
	
	/**
	 * Displays inventory items from r1 to r2 -RAK-
	 * <p>
	 * Designed to keep the display as far to the right as possible. The
	 * parameter col gives a column at which to start, but if the display does
	 * not fit, it may be moved left. The return value is the left edge used.
	 * If mask is non-zero, then only display those items which have a non-zero
	 * entry in the mask array. -CJS-
	 * 
	 * @param r1
	 * @param r2
	 * @param showWeight Whether to display items' weight
	 * @param col Column to start printing at
	 * @param mask Determines which type(s) of items to print
	 * @return Left edge of the output
	 */
	public static int showInventory(int r1, int r2, boolean showWeight, int col, char[] mask) {
		String[] invenLines = new String[23];
		
		int len = 79 - col;
		int lim;
		if (showWeight) {
			lim = 68;
		} else {
			lim = 76;
		}
		
		for (int i = r1; i <= r2; i++) { // Print the items
			if (mask == null || mask[i] != 0) {
				String itemDesc = Desc.describeObject(Treasure.inventory[i], true);
				if (lim < itemDesc.length()) {
					itemDesc = itemDesc.substring(0, lim); // Truncate if too long.
				}
				invenLines[i] = String.format(
						"%c) %s",
						(char)('a' + i),
						itemDesc);
				int newLength = invenLines[i].length() + 2;
				if (showWeight) {
					newLength += 9;
				}
				if (newLength > len) {
					len = newLength;
				}
			}
		}
		
		col = 79 - len;
		if (col < 0) {
			col = 0;
		}
		
		int currentLine = 1;
		for (int i = r1; i <= r2; i++) {
			if (mask == null || mask[i] != 0) {
				// don't need first two spaces if in first column
				if (col == 0) {
					IO.print(invenLines[i], currentLine, col);
				} else {
					IO.putBuffer("  ", currentLine, col);
					IO.print(invenLines[i], currentLine, col + 2);
				}
				if (showWeight) {
					int totalWeight = Treasure.inventory[i].weight * Treasure.inventory[i].number;
					String msgWeight = String.format(
							"%3d.%d lb",
							totalWeight / 10,
							totalWeight % 10);
					IO.print(msgWeight, currentLine, 71);
				}
				currentLine++;
			}
		}
		return col;
	}
	
	/**
	 * Return a string describing how a given equipment item is carried. -CJS-
	 * 
	 * @param index Item's index in the inventory
	 * @return
	 */
	public static String describeUse(int index) {
		switch (index) {
		case Constants.INVEN_WIELD:
			return "wielding";
		case Constants.INVEN_HEAD:
			return "wearing on your head";
		case Constants.INVEN_NECK:
			return "wearing around your neck";
		case Constants.INVEN_BODY:
			return "wearing on your body";
		case Constants.INVEN_ARM:
			return "wearing on your arm";
		case Constants.INVEN_HANDS:
			return "wearing on your hands";
		case Constants.INVEN_RIGHT:
			return "wearing on your right hand";
		case Constants.INVEN_LEFT:
			return "wearing on your left hand";
		case Constants.INVEN_FEET:
			return "wearing on your feet";
		case Constants.INVEN_OUTER:
			return "wearing about your body";
		case Constants.INVEN_LIGHT:
			return "using to light the way";
		case Constants.INVEN_AUX:
			return "holding ready by your side";
		default:
			return "carrying in your pack";
		}
	}
	
	/**
	 * Displays equipment items from r1 to end -RAK-
	 * <p>
	 * Keep display as far right as possible. -CJS-
	 * 
	 * @param showWeight Whether to display items' weight
	 * @param col Column to start printing at
	 * @return
	 */
	public static int showEquippedItems(boolean showWeight, int col) {
		String equipPosition;
		String itemDesc;
		String[] equipmentLines = new String[Constants.INVEN_ARRAY_SIZE - Constants.INVEN_WIELD];
		
		int line = 0;
		int len = 79 - col;
		int lim;
		if (showWeight) {
			lim = 52;
		} else {
			lim = 60;
		}
		
		// Range of equipment
		for (int i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
			InvenType item = Treasure.inventory[i];
			if (item.category != Constants.TV_NOTHING) {
				switch (i) { // Get position
				case Constants.INVEN_WIELD:
					if (Player.py.stats.useStat[Constants.A_STR] * 15 < item.weight) {
						equipPosition = "Just lifting";
					} else {
						equipPosition = "Wielding";
					}
					break;
				case Constants.INVEN_HEAD:
					equipPosition = "On head";
					break;
				case Constants.INVEN_NECK:
					equipPosition = "Around neck";
					break;
				case Constants.INVEN_BODY:
					equipPosition = "On body";
					break;
				case Constants.INVEN_ARM:
					equipPosition = "On arm";
					break;
				case Constants.INVEN_HANDS:
					equipPosition = "On hands";
					break;
				case Constants.INVEN_RIGHT:
					equipPosition = "On right hand";
					break;
				case Constants.INVEN_LEFT:
					equipPosition = "On left hand";
					break;
				case Constants.INVEN_FEET:
					equipPosition = "On feet";
					break;
				case Constants.INVEN_OUTER:
					equipPosition = "About body";
					break;
				case Constants.INVEN_LIGHT:
					equipPosition = "Light source";
					break;
				case Constants.INVEN_AUX:
					equipPosition = "Spare weapon";
					break;
				default:
					equipPosition = "Unknown value";
					break;
				}
				
				itemDesc = Desc.describeObject(Treasure.inventory[i], true);
				if (lim < itemDesc.length()) {
					itemDesc = itemDesc.substring(0, lim); // Truncate if necessary
				}
				
				equipmentLines[line] = String.format(
						"%c) %-14s: %s",
						line + 'a',
						equipPosition,
						itemDesc);
				int newLength = equipmentLines[line].length() + 2;
				if (showWeight) {
					newLength += 9;
				}
				if (newLength > len) {
					len = newLength;
				}
				line++;
			}
		}
		
		col = 79 - len;
		if (col < 0) {
			col = 0;
		}
		
		// Range of equipment
		line = 0;
		for (int i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
			InvenType item = Treasure.inventory[i];
			if (item.category != Constants.TV_NOTHING) {
				// don't need first two spaces when using whole screen
				if (col == 0) {
					IO.print(equipmentLines[line], line + 1, col);
				} else {
					IO.putBuffer("  ", line + 1, col);
					IO.print(equipmentLines[line], line + 1, col + 2);
				}
				
				if (showWeight) {
					int totalWeight = item.weight * item.number;
					itemDesc = String.format(
							"%3d.%d lb",
							totalWeight / 10,
							totalWeight % 10);
					IO.print(itemDesc, line + 1, 71);
				}
				line++;
			}
		}
		IO.eraseLine(line + 1, col);
		return col;
	}
	
	/**
	 * Remove item from equipment list -RAK-
	 * 
	 * @param itemIndex Item's index in the inventory
	 * @param position If non-negative, show item's position
	 *                 in inventory, displayed as a letter
	 */
	public static void unequipItem(int itemIndex, int position) {
		Treasure.equipCounter--;
		
		InvenType item = Treasure.inventory[itemIndex];
		Treasure.invenWeight -= item.weight * item.number;
		Player.py.flags.status |= Constants.PY_STR_WGT;
		
		String prevCondition;
		if (itemIndex == Constants.INVEN_WIELD || itemIndex == Constants.INVEN_AUX) {
			prevCondition = "Was wielding ";
		} else if (itemIndex == Constants.INVEN_LIGHT) {
			prevCondition = "Light source was ";
		} else {
			prevCondition = "Was wearing ";
		}
		
		String itemDesc = Desc.describeObject(item, true);
		String msgUnequip;
		if (position >= 0) {
			msgUnequip = String.format("%s%s (%c)", prevCondition, itemDesc, 'a' + position);
		} else {
			msgUnequip = String.format("%s%s", prevCondition, itemDesc);
		}
		IO.printMessage(msgUnequip);
		
		if (itemIndex != Constants.INVEN_AUX) { // For secondary weapon
			adjustPlayerBonuses(item, -1);
		}
		Desc.copyIntoInventory(item, Constants.OBJ_NOTHING);
	}
	
	/**
	 * Used to verify if this really is the item we wish to
	 * wear or read. -CJS-
	 * 
	 * @param prompt Prompt to display
	 * @param item Item whose use is to be verified
	 * @return Whether to wear or read the item
	 */
	public static boolean verify(String prompt, int item) {
		StringBuilder msgVerify = new StringBuilder();
		msgVerify.append(prompt);
		msgVerify.append(' ');
		msgVerify.append(Desc.describeObject(Treasure.inventory[item], true));
		
		// change the period to a question mark
		msgVerify.setCharAt(msgVerify.length() - 1, '?');
		
		return IO.getCheck(msgVerify.toString());
	}
	
	/* All inventory commands (wear, exchange, take off, drop, inventory and
	 * equipment) are handled in an alternative command input mode, which accepts
	 * any of the inventory commands.
	 *
	 * It is intended that this function be called several times in succession,
	 * as some commands take up a turn, and the rest of moria must proceed in the
	 * interim. A global variable is provided, doing_inven, which is normally
	 * zero; however if on return from inven_command it is expected that
	 * inven_command should be called *again*, (being still in inventory command
	 * input mode), then doing_inven is set to the inventory command character
	 * which should be used in the next call to inven_command.
	 *
	 * On return, the screen is restored, but not flushed. Provided no flush of
	 * the screen takes place before the next call to inven_command, the inventory
	 * command screen is silently redisplayed, and no actual output takes place at
	 * all. If the screen is flushed before a subsequent call, then the player is
	 * prompted to see if we should continue. This allows the player to see any
	 * changes that take place on the screen during inventory command input.
	 *
	 * The global variable, screen_change, is cleared by inven_command, and set
	 * when the screen is flushed. This is the means by which inven_command tell
	 * if the screen has been flushed.
	 *
	 * The display of inventory items is kept to the right of the screen to
	 * minimize the work done to restore the screen afterwards.		-CJS-*/
	
	/* Inventory command screen states. */
	public static final int BLANK_SCR =	0;
	public static final int EQUIP_SCR =	1;
	public static final int INVEN_SCR =	2;
	public static final int WEAR_SCR  =	3;
	public static final int HELP_SCR  =	4;
	public static final int WRONG_SCR =	5;
	
	/* Keep track of the state of the inventory screen. */
	public static int screenState;
	public static int screenLeft;
	public static int screenBase;
	public static int wearLow;
	public static int wearHigh;
	
	/**
	 * Draw the inventory screen.
	 * 
	 * @param newScreen Which inventory screen to show
	 */
	public static void showInvenScreen(int newScreen) {
		int line = 0;
		
		if (newScreen != screenState) {
			screenState = newScreen;
			switch (newScreen) {
			case BLANK_SCR:
				line = 0;
				break;
			case HELP_SCR:
				if (screenLeft > 52) {
					screenLeft = 52;
				}
				IO.print("  ESC: exit", 1, screenLeft);
				IO.print("  w  : wear or wield object", 2, screenLeft);
				IO.print("  t  : take off item", 3, screenLeft);
				IO.print("  d  : drop object", 4, screenLeft);
				IO.print("  x  : exchange weapons", 5, screenLeft);
				IO.print("  i  : inventory of pack", 6, screenLeft);
				IO.print("  e  : list used equipment", 7, screenLeft);
				line = 7;
				break;
			case INVEN_SCR:
				screenLeft = showInventory(0, Treasure.invenCounter - 1,
						Variable.showWeightFlag.value(), screenLeft, null);
				line = Treasure.invenCounter;
				break;
			case WEAR_SCR:
				screenLeft = showInventory(wearLow, wearHigh,
						Variable.showWeightFlag.value(), screenLeft, null);
				line = wearHigh - wearLow + 1;
				break;
			case EQUIP_SCR:
				screenLeft = showEquippedItems(Variable.showWeightFlag.value(),
						screenLeft);
				line = Treasure.equipCounter;
				break;
			default:
				break;
			}
			if (line >= screenBase) {
				screenBase = line + 1;
				IO.eraseLine(screenBase, screenLeft);
			} else {
				while (++line <= screenBase) {
					IO.eraseLine(line, screenLeft);
				}
			}
		}
	}
	
	/* This does all the work. */ // TODO find a way to reduce complexity
	public static void doInvenCommand(char command) {
		boolean selecting;
		
		Variable.freeTurnFlag = true;
		IO.saveScreen();
		
		// Take up where we left off after a previous inventory command. -CJS-
		if (Variable.doingInven != '\0') {
			// If the screen has been flushed, we need to redraw. If the
			// command is a simple ' ' to recover the screen, just quit.
			// Otherwise, check and see what the user wants.
			if (Variable.didScreenChange) {
				if (command == ' '
						|| !IO.getCheck("Continuing with inventory command?")) {
					Variable.doingInven = '\0';
					return;
				}
				screenLeft = 50;
				screenBase = 0;
			}
			int tmp = screenState;
			screenState = WRONG_SCR;
			showInvenScreen(tmp);
		} else {
			screenLeft = 50;
			screenBase = 0;
			// this forces exit of inven_command() if selecting is not set true
			screenState = BLANK_SCR;
		}
		
		do {
			if (Character.isUpperCase(command)) {
				command = Character.toLowerCase(command);
			}
			
			// Simple command getting and screen selection.
			selecting = false;
			switch (command) {
			case 'i': // Inventory
				if (Treasure.invenCounter == 0) {
					IO.printMessage("You are not carrying anything.");
				} else {
					showInvenScreen(INVEN_SCR);
				}
				break;
			case 'e': // Equipment
				if (Treasure.equipCounter == 0) {
					IO.printMessage("You are not using any equipment.");
				} else {
					showInvenScreen(EQUIP_SCR);
				}
				break;
			case 't': // Take off
				if (Treasure.equipCounter == 0) {
					IO.printMessage("You are not using any equipment.");
				
				// don't print message restarting inven command after taking off
				// something, it is confusing
				} else if (Treasure.invenCounter >= Constants.INVEN_WIELD && Variable.doingInven == '\0') {
					IO.printMessage("You will have to drop something first.");
				} else {
					if (screenState != BLANK_SCR) {
						showInvenScreen(EQUIP_SCR);
					}
					selecting = true;
				}
				break;
			case 'd': // Drop
				if (Treasure.invenCounter == 0 && Treasure.equipCounter == 0) {
					IO.printMessage("But you're not carrying anything.");
				} else if (Variable.cave[Player.y][Player.x].treasureIndex != 0) {
					IO.printMessage("There's no room to drop anything here.");
				} else {
					selecting = true;
					if ((screenState == EQUIP_SCR && Treasure.equipCounter > 0) || Treasure.invenCounter == 0) {
						if (screenState != BLANK_SCR) {
							showInvenScreen(EQUIP_SCR);
						}
						command = 'r'; // Remove - or take off and drop.
					} else if (screenState != BLANK_SCR) {
						showInvenScreen(INVEN_SCR);
					}
				}
				break;
			case 'w': // Wear/wield
				for (wearLow = 0; wearLow < Treasure.invenCounter && Treasure.inventory[wearLow].category > Constants.TV_MAX_WEAR; wearLow++) {
					;
				}
				for(wearHigh = wearLow; wearHigh < Treasure.invenCounter && Treasure.inventory[wearHigh].category >= Constants.TV_MIN_WEAR; wearHigh++) {
					;
				}
				wearHigh--;
				if (wearLow > wearHigh) {
					IO.printMessage("You have nothing to wear or wield.");
				} else {
					if (screenState != BLANK_SCR && screenState != INVEN_SCR) {
						showInvenScreen(WEAR_SCR);
					}
					selecting = true;
				}
				break;
			case 'x':
				if (Treasure.inventory[Constants.INVEN_WIELD].category == Constants.TV_NOTHING && Treasure.inventory[Constants.INVEN_AUX].category == Constants.TV_NOTHING) {
					IO.printMessage("But you are wielding no weapons.");
				} else if ((Constants.TR_CURSED & Treasure.inventory[Constants.INVEN_WIELD].flags) != 0) {
					String itemDesc = Desc.describeObject(Treasure.inventory[Constants.INVEN_WIELD], false);
					String msgCursed = String.format(
							"The %s you are wielding appears to be cursed.",
							itemDesc);
					IO.printMessage(msgCursed);
				} else {
					Variable.freeTurnFlag = false;
					InvenType tmpItem = Treasure.inventory[Constants.INVEN_AUX];
					Treasure.inventory[Constants.INVEN_AUX] = Treasure.inventory[Constants.INVEN_WIELD];
					Treasure.inventory[Constants.INVEN_WIELD] = tmpItem;
					if (screenState == EQUIP_SCR) {
						screenLeft = showEquippedItems(Variable.showWeightFlag.value(), screenLeft);
					}
					adjustPlayerBonuses(Treasure.inventory[Constants.INVEN_AUX], -1); // Subtract bonuses
					adjustPlayerBonuses(Treasure.inventory[Constants.INVEN_WIELD], 1); // Add bonuses
					if (Treasure.inventory[Constants.INVEN_WIELD].category != Constants.TV_NOTHING) {
						IO.printMessage("Primary weapon   : "
								+ Desc.describeObject(Treasure.inventory[Constants.INVEN_WIELD], true));
					} else {
						IO.printMessage("No primary weapon.");
					}
					// this is a new weapon, so clear the heavy flag
					Variable.isWeaponHeavy = false;
					Misc3.checkStrength();
				}
				break;
			case ' ': // Dummy command to return again to main prompt.
				break;
			case '?':
				showInvenScreen(HELP_SCR);
				break;
			default:
				// Nonsense command
				IO.bell();
				break;
			}
			
			// Clear the doing_inven flag here, instead of at beginning, so that
			// can use it to control when messages above appear.
			Variable.doingInven = '\0';
			
			// Keep looking for objects to drop/wear/take off/throw off
			CharPointer which = new CharPointer('z');
			while (selecting && Variable.freeTurnFlag) {
				String prompt;
				int from;
				int to;
				String swap = "";
				if (command == 'w') {
					from = wearLow;
					to = wearHigh;
					prompt = "Wear/Wield";
				} else {
					from = 0;
					if (command == 'd') {
						to = Treasure.invenCounter - 1;
						prompt = "Drop";
						if (Treasure.equipCounter > 0) {
							swap = ", / for Equip";
						}
					} else {
						to = Treasure.equipCounter - 1;
						if (command == 't') {
							prompt = "Take off";
						} else { // command == 'r'
							prompt = "Throw off";
							if (Treasure.invenCounter > 0) {
								swap = ", / for Inven";
							}
						}
					}
				}
				if (from > to) {
					selecting = false;
				} else {
					String disp;
					if (screenState == BLANK_SCR) {
						disp = ", * to list";
					} else {
						disp = "";
					}
					String msgHeader = String.format(
							"(%c-%c%s%s%s, space to break, ESC to exit) %s which one?",
							from + 'a', to + 'a', disp, swap, 
							((command == 'w' || command == 'd') ? ", 0-9" : ""),
							prompt);
					
					// Abort everything.
					if (!IO.getCommand(msgHeader, which)) {
						selecting = false;
						which.value(Constants.ESCAPE);
					
					// Draw the screen and maybe exit to main prompt.
					} else if (which.value() == ' ' || which.value() == '*') {
						if (command == 't' || command == 'r') {
							showInvenScreen(EQUIP_SCR);
						} else if (command == 'w' && screenState != INVEN_SCR) {
							showInvenScreen(WEAR_SCR);
						} else {
							showInvenScreen(INVEN_SCR);
						}
						if (which.value() == ' ') {
							selecting = false;
						}
					
					// Swap screens (for drop)
					} else if (which.value() == '/' && !swap.equals("")) {
						if (command == 'd') {
							command = 'r';
						} else {
							command = 'd';
						}
						if (screenState == EQUIP_SCR) {
							showInvenScreen(INVEN_SCR);
						} else if (screenState == INVEN_SCR) {
							showInvenScreen(EQUIP_SCR);
						}
					} else {
						int itemIndex;
						if ((which.value() >= '0') && (which.value() <= '9') 
								&& (command != 'r') && (command != 't'))
						{
							// look for item whose inscription matches "which"
							int m;
							for (m = from;
									m <= to && ((Treasure.inventory[m].inscription.charAt(0) != which.value())
											|| (Treasure.inventory[m].inscription.length() > 1));
									m++);
							if (m <= to) {
								itemIndex = m;
							} else {
								itemIndex = -1;
							}
						}
						else if ((which.value() >= 'A') && (which.value() <= 'Z')) {
							itemIndex = which.value() - 'A';
						} else {
							itemIndex = which.value() - 'a';
						}
						if (itemIndex < from || itemIndex > to) {
							IO.bell();
						} else { // Found an item!
							int slot = 0;
							if (command == 'r' || command == 't') {
								// Get its place in the equipment list.
								int tmp = itemIndex;
								itemIndex = 21;
								do {
									itemIndex++;
									if (Treasure.inventory[itemIndex].category != Constants.TV_NOTHING) {
										tmp--;
									}
								} while (tmp >= 0);
								if (Character.isUpperCase(which.value()) && !verify(prompt, itemIndex)) {
									itemIndex = -1;
								} else if ((Constants.TR_CURSED & Treasure.inventory[itemIndex].flags) != 0) {
									IO.printMessage("Hmmm, it seems to be cursed.");
									itemIndex = -1;
								} else if (command == 't' &&
										!Misc3.canPickUpItem(Treasure.inventory[itemIndex])) {
									if (Variable.cave[Player.y][Player.x].treasureIndex != 0) {
										IO.printMessage("You can't carry it.");
										itemIndex = -1;
									} else if (IO.getCheck("You can't carry it.  Drop it?")) {
										command = 'r';
									} else {
										itemIndex = -1;
									}
								}
								if (itemIndex >= 0) {
									if (command == 'r') {
										Misc3.dropInvenItem(itemIndex, true);
										// As a safety measure, set the player's
										// inven weight to 0, 
										// when the last object is dropped
										if (Treasure.invenCounter == 0 && Treasure.equipCounter == 0) {
											Treasure.invenWeight = 0;
										}
									} else {
										slot = Misc3.pickUpItem(Treasure.inventory[itemIndex]);
										unequipItem(itemIndex, slot);
									}
									Misc3.checkStrength();
									Variable.freeTurnFlag = false;
									if (command == 'r') {
										selecting = false;
									}
								}
							} else if (command == 'w') {
								// Wearing. Go to a bit of trouble over replacing
								// existing equipment.
								if (Character.isUpperCase(which.value()) && !verify(prompt, itemIndex)) {
									itemIndex = -1;
								} else {
									switch (Treasure.inventory[itemIndex].category) {
									// Slot for equipment
									case Constants.TV_SLING_AMMO: case Constants.TV_BOLT: case Constants.TV_ARROW:
									case Constants.TV_BOW: case Constants.TV_HAFTED: case Constants.TV_POLEARM:
									case Constants.TV_SWORD: case Constants.TV_DIGGING: case Constants.TV_SPIKE:
										slot = Constants.INVEN_WIELD; break;
									case Constants.TV_LIGHT: slot = Constants.INVEN_LIGHT; break;
									case Constants.TV_BOOTS: slot = Constants.INVEN_FEET; break;
									case Constants.TV_GLOVES: slot = Constants.INVEN_HANDS; break;
									case Constants.TV_CLOAK: slot = Constants.INVEN_OUTER; break;
									case Constants.TV_HELM: slot = Constants.INVEN_HEAD; break;
									case Constants.TV_SHIELD: slot = Constants.INVEN_ARM; break;
									case Constants.TV_HARD_ARMOR: case Constants.TV_SOFT_ARMOR:
										slot = Constants.INVEN_BODY; break;
									case Constants.TV_AMULET: slot = Constants.INVEN_NECK; break;
									case Constants.TV_RING:
										if (Treasure.inventory[Constants.INVEN_RIGHT].category == Constants.TV_NOTHING) {
											slot = Constants.INVEN_RIGHT;
										} else if (Treasure.inventory[Constants.INVEN_LEFT].category == Constants.TV_NOTHING) {
											slot = Constants.INVEN_LEFT;
										} else {
											slot = 0;
											// Rings. Give some choice over where they go.
											do {
												CharPointer query = new CharPointer();
												if (!IO.getCommand( "Put ring on which hand (l/r/L/R)?", query)) {
													itemIndex = -1;
													slot = -1;
												} else if (query.value() == 'l') {
													slot = Constants.INVEN_LEFT;
												} else if (query.value() == 'r') {
													slot = Constants.INVEN_RIGHT;
												} else {
													if (query.value() == 'L') {
														slot = Constants.INVEN_LEFT;
													} else if (query.value() == 'R') {
														slot = Constants.INVEN_RIGHT;
													} else {
														IO.bell();
													}
													if (slot != 0 && !verify("Replace", slot)) {
														slot = 0;
													}
												}
											} while (slot == 0);
										}
										break;
									default:
										IO.printMessage("IMPOSSIBLE: I don't see how you can use that.");
										itemIndex = -1;
										break;
									}
								}
								if (itemIndex >= 0 && Treasure.inventory[slot].category != Constants.TV_NOTHING) {
									if ((Constants.TR_CURSED & Treasure.inventory[slot].flags) != 0) {
										String itemDesc = Desc.describeObject(Treasure.inventory[slot], false);
										String msgCursed = String.format("The %s you are ", itemDesc);
										if (slot == Constants.INVEN_HEAD) {
											msgCursed += "wielding ";
										} else {
											msgCursed += "wearing ";
										}
										msgCursed += "appears to be cursed.";
										IO.printMessage(msgCursed);
										itemIndex = -1;
									} else if (Treasure.inventory[itemIndex].subCategory == Constants.ITEM_GROUP_MIN && Treasure.inventory[itemIndex].number > 1 && !Misc3.canPickUpItem(Treasure.inventory[slot])) {
										// this can happen if try to wield a torch, and
										// have more than one in your inventory
										IO.printMessage("You will have to drop something first.");
										itemIndex = -1;
									}
								}
								if (itemIndex >= 0) {
									// OK. Wear it.
									Variable.freeTurnFlag = false;
									
									// first remove new item from inventory
									InvenType newItem = new InvenType();
									Treasure.inventory[itemIndex].copyInto(newItem);
									
									wearHigh--;
									// Fix for torches
									if (newItem.number > 1 && newItem.subCategory <= Constants.ITEM_SINGLE_STACK_MAX) {
										newItem.number = 1;
										wearHigh++;
									}
									Treasure.invenWeight += newItem.weight * newItem.number;
									Misc3.destroyInvenItem(itemIndex); // Subtracts weight
									
									// second, add old item to inv and remove from
									// equipment list, if necessary
									InvenType oldItem = Treasure.inventory[slot];
									if (oldItem.category != Constants.TV_NOTHING) {
										int tmp2 = Treasure.invenCounter;
										int tmp = Misc3.pickUpItem(oldItem);
										// if item removed did not stack with anything in
										// inventory, then increment wear_high
										if (Treasure.invenCounter != tmp2) {
											wearHigh++;
										}
										unequipItem(slot, tmp);
									}
									
									// third, wear new item
									newItem.copyInto(oldItem);
									Treasure.equipCounter++;
									adjustPlayerBonuses(newItem, 1);
									String holdingType;
									if (slot == Constants.INVEN_WIELD) {
										holdingType = "You are wielding";
									} else if (slot == Constants.INVEN_LIGHT) {
										holdingType = "Your light source is";
									} else {
										holdingType = "You are wearing";
									}
									String itemDesc = Desc.describeObject(newItem, true);
									// Get the right equipment letter.
									int tmp = Constants.INVEN_WIELD;
									itemIndex = 0;
									while (tmp != slot) {
										if (Treasure.inventory[tmp++].category != Constants.TV_NOTHING) {
											itemIndex++;
										}
									}
									
									String msgEquipped = String.format(
											"%s %s (%c)",
											holdingType, itemDesc,
											'a' + itemIndex);
									IO.printMessage(msgEquipped);
									// this is a new weapon, so clear the heavy flag
									if (slot == Constants.INVEN_WIELD) {
										Variable.isWeaponHeavy = false;
									}
									Misc3.checkStrength();
									if ((newItem.flags & Constants.TR_CURSED) != 0) {
										IO.printMessage("Oops! It feels deathly cold!");
										Misc4.addInscription(newItem, Constants.ID_DAMD);
										// To force a cost of 0, even if unidentified.
										newItem.cost = -1;
									}
								}
							} else { // command == 'd'
								char ch = 0;
								if (Treasure.inventory[itemIndex].number > 1) {
									StringBuilder itemDesc = new StringBuilder();
									itemDesc.append(Desc.describeObject(Treasure.inventory[itemIndex], true));
									itemDesc.setCharAt(itemDesc.length() - 1, '?');
									IO.print(String.format("Drop all %s [y/n]",
											itemDesc.toString()), 0, 0);
									ch = IO.inkey();
									if (ch != 'y' && ch != 'n') {
										if (ch != Constants.ESCAPE) {
											IO.bell();
										}
										IO.eraseLine(Constants.MSG_LINE, 0);
										itemIndex = -1;
									}
								} else if (Character.isUpperCase(which.value()) && !verify(prompt, itemIndex)) {
									itemIndex = -1;
								} else {
									ch = 'y';
								}
								if (itemIndex >= 0) {
									Variable.freeTurnFlag = false; // Player turn
									Misc3.dropInvenItem(itemIndex, ch == 'y');
									Misc3.checkStrength();
								}
								selecting = false;
								// As a safety measure, set the player's inven weight
								// to 0, when the last object is dropped.
								if (Treasure.invenCounter == 0 && Treasure.equipCounter == 0) {
									Treasure.invenWeight = 0;
								}
							}
							if (!Variable.freeTurnFlag && screenState == BLANK_SCR) {
								selecting = false;
							}
						}
					}
				}
			}
			
			if (which.value() == Constants.ESCAPE || screenState == BLANK_SCR) {
				command = Constants.ESCAPE;
			} else if (!Variable.freeTurnFlag) {
				// Save state for recovery if they want to call us again next turn.
				if (selecting) {
					Variable.doingInven = command;
				} else {
					Variable.doingInven = ' '; // A dummy command to recover screen.
				}
				// flush last message before clearing screen_change and exiting
				IO.printMessage("");
				Variable.didScreenChange = false; // This lets us know if the world changes
				command = Constants.ESCAPE;
			} else {
				// Put an appropriate header.
				if (screenState == INVEN_SCR) {
					String msgCarrying;
					if (!Variable.showWeightFlag.value() || Treasure.invenCounter == 0) {
						msgCarrying = String.format(
								"You are carrying %d.%d pounds. In your pack there is %s",
								Treasure.invenWeight / 10,
								Treasure.invenWeight % 10,
								((Treasure.invenCounter == 0) ? "nothing." : "-"));
					} else {
						msgCarrying = String.format(
								"You are carrying %d.%d pounds. Your capacity is %d.%d pounds. %s",
								Treasure.invenWeight / 10,
								Treasure.invenWeight % 10,
								Misc3.weightLimit() / 10, Misc3.weightLimit() % 10,
								"In your pack is -");
					}
					IO.print(msgCarrying, 0, 0);
				} else if (screenState == WEAR_SCR) {
					if (wearHigh < wearLow) {
						IO.print("You have nothing you could wield.", 0, 0);
					} else {
						IO.print("You could wield -", 0, 0);
					}
				} else if (screenState == EQUIP_SCR) {
					if (Treasure.equipCounter == 0) {
						IO.print("You are not using anything.", 0, 0);
					} else {
						IO.print("You are using -", 0, 0);
					}
				} else {
					IO.print("Allowed commands:", 0, 0);
				}
				IO.eraseLine(screenBase, screenLeft);
				IO.putBuffer("e/i/t/w/x/d/?/ESC:", screenBase, 60);
				command = IO.inkey();
				IO.eraseLine(screenBase, screenLeft);
			}
		} while (command != Constants.ESCAPE);
		
		if (screenState != BLANK_SCR) {
			IO.restoreScreen();
		}
		calcBonuses();
	}
	
	/** Get the ID of an item and return the CTR value of it. -RAK-
	 * 
	 * @param com_val
	 * @param pmt
	 * @param i
	 * @param j
	 * @param mask
	 * @param message
	 * @return
	 */ // TODO find a way to reduce complexity
	public static boolean getItemId(IntPointer com_val, String pmt,
			int i, int j, char[] mask, String message) {
		boolean item = false;
		boolean redraw = false;
		boolean full;
		com_val.value(0);
		int i_scr = 1;
		if (j > Constants.INVEN_WIELD) {
			full = true;
			if (Treasure.invenCounter == 0) {
				i_scr = 0;
				j = Treasure.equipCounter - 1;
			} else {
				j = Treasure.invenCounter - 1;
			}
		} else {
			full = false;
		}
		
		if (Treasure.invenCounter > 0 || (full && Treasure.equipCounter > 0)) {
			do {
				if (redraw) {
					if (i_scr > 0) {
						showInventory(i, j, false, 80, mask);
					} else {
						showEquippedItems(false, 80);
					}
				}
				
				String out_val;
				if (full) {
					out_val = String.format(
							"(%s: %c-%c,%s%s / for %s, or ESC) %s",
							((i_scr > 0) ? "Inven" : "Equip"), i + 'a', j + 'a',
							((i_scr > 0) ? " 0-9," : ""),
							((redraw) ? "" : " * to see,"),
							((i_scr > 0) ? "Equip" : "Inven"), pmt);
				} else {
					out_val = String.format(
							"(Items %c-%c,%s%s ESC to exit) %s", 
							i + 'a', j + 'a',			   
							((i_scr > 0) ? " 0-9," : ""),
							((redraw) ? "" : " * for inventory list,"), pmt);
				}
				boolean test_flag = false;
				IO.print(out_val, 0, 0);
				do {
					char which = IO.inkey();
					switch(which) {
					case Constants.ESCAPE:
						test_flag = true;
						Variable.freeTurnFlag = true;
						i_scr = -1;
						break;
					case '/':
						if (full) {
							if (i_scr > 0) {
								if (Treasure.equipCounter == 0) {
									IO.print("But you're not using anything -more-", 0, 0);
									IO.inkey();
								} else {
									i_scr = 0;
									test_flag = true;
									if (redraw) {
										j = Treasure.equipCounter;
										while (j < Treasure.invenCounter) {
											j++;
											IO.eraseLine(j, 0);
										}
									}
									j = Treasure.equipCounter - 1;
								}
								IO.print(out_val, 0, 0);
							} else {
								if (Treasure.invenCounter == 0) {
									IO.print("But you're not carrying anything -more-", 0, 0);
									IO.inkey();
								} else {
									i_scr = 1;
									test_flag = true;
									if (redraw) {
										j = Treasure.invenCounter;
										while (j < Treasure.equipCounter) {
											j++;
											IO.eraseLine(j, 0);
										}
									}
									j = Treasure.invenCounter - 1;
								}
							}
						}
						break;
					case '*':
						if (!redraw) {
							test_flag = true;
							IO.saveScreen();
							redraw = true;
						}
						break;
					default:
						if ((which >= '0') && (which <= '9') && (i_scr != 0))
							/* look for item whose inscription matches "which" */
						{
							int m;
							for (m = i;
									(m < Constants.INVEN_WIELD) 
									&& ((Treasure.inventory[m].inscription.charAt(0) != which)
											|| (Treasure.inventory[m].inscription.length() > 1));
									m++);
							if (m < Constants.INVEN_WIELD) {
								com_val.value(m);
							} else {
								com_val.value(-1);
							}
						} else if (Character.isUpperCase(which)) {
							com_val.value(which - 'A');
						} else {
							com_val.value(which - 'a');
						}
						if ((com_val.value() >= i) && (com_val.value() <= j) && (mask == null || mask[com_val.value()] != 0)) {
							if (i_scr == 0) {
								i = 21;
								j = com_val.value();
								do {
									while (Treasure.inventory[++i].category == Constants.TV_NOTHING);
									j--;
								} while (j >= 0);
								com_val.value(i);
							}
							if (Character.isUpperCase(which) && !verify("Try", com_val.value())) {
								test_flag = true;
								Variable.freeTurnFlag = true;
								i_scr = -1;
								break;
							}
							test_flag = true;
							item = true;
							i_scr = -1;
						} else if (!message.equals("")) {
							IO.printMessage(message);
							/* Set test_flag to force redraw of the question.  */
							test_flag = true;
						} else {
							IO.bell();
						}
						break;
					}
				} while (!test_flag);
			} while (i_scr >= 0);
			
			if (redraw) {
				IO.restoreScreen();
			}
			IO.eraseLine(Constants.MSG_LINE, 0);
		} else {
			IO.print("You are not carrying anything.", 0, 0);
		}
		
		return item;
	}
	
	/* I may have written the town level code, but I'm not exactly	 */
	/* proud of it.	 Adding the stores required some real slucky	 */
	/* hooks which I have not had time to re-think.		 -RAK-	 */
	
	/**
	 * Returns true if player has no light -RAK-
	 * 
	 * @return
	 */
	public static boolean playerHasNoLight() {
		CaveType cavePos = Variable.cave[Player.y][Player.x];
		if (!cavePos.tempLight && !cavePos.permLight) {
			return true;
		}
		return false;
	}
	
	/**
	 * Map rogue_like direction commands into numbers
	 * 
	 * @param commandValue
	 * @return
	 */
	public static char mapRogueDirection(char commandValue) {
		switch (commandValue) {
		case Constants.KEY_LEFT:
		case 'h':
			commandValue = '4';
			break;
		case 'y':
			commandValue = '7';
			break;
		case Constants.KEY_UP:
		case 'k':
			commandValue = '8';
			break;
		case 'u':
			commandValue = '9';
			break;
		case Constants.KEY_RIGHT:
		case 'l':
			commandValue = '6';
			break;
		case 'n':
			commandValue = '3';
			break;
		case Constants.KEY_DOWN:
		case 'j':
			commandValue = '2';
			break;
		case 'b':
			commandValue = '1';
			break;
		case '.':
			commandValue = '5';
			break;
		default:
			break;
		}
		return commandValue;
	}
	
	private static int prevDir;	// Direction memory. -CJS-
	
	/**
	 * Prompts for a direction -RAK-
	 * Direction memory added, for repeated commands. -CJS
	 * 
	 * @param prompt
	 * @param dir
	 * @return
	 */
	public static boolean getDirection(String prompt, IntPointer dir) {
		if (Variable.defaultDir > 0) { // used in counted commands. -CJS-
			dir.value(prevDir);
			return true;
		}
		if (prompt.equals("")) {
			prompt = "Which direction?";
		}
		
		while (true) {
			int save = Variable.commandCount; // Don't end a counted command. -CJS-
			CharPointer command = new CharPointer();
			if (!IO.getCommand(prompt, command)) {
				Variable.freeTurnFlag = true;
				return false;
			}
			Variable.commandCount = save;
			
			if (Variable.rogueLikeCommands.value()) {
				command.value(mapRogueDirection(command.value()));
			}
			if (command.value() >= '1'
					&& command.value() <= '9'
					&& command.value() != '5') {
				prevDir = command.value() - '0';
				dir.value(prevDir);
				return true;
			}
			IO.bell();
		}
	}
	
	/**
	 * Similar to get_dir, except that no memory exists, and it is
	 * allowed to enter the null direction. -CJS-
	 * 
	 * @param prompt
	 * @param dir
	 * @return
	 */
	public static boolean getAnyDirection(String prompt, IntPointer dir) {
		while (true) {
			CharPointer command = new CharPointer();
			if (!IO.getCommand(prompt, command)) {
				Variable.freeTurnFlag = true;
				return false;
			}
			if (Variable.rogueLikeCommands.value()) {
				command.value(mapRogueDirection(command.value()));
			}
			if (command.value() >= '1' && command.value() <= '9') {
				dir.value(command.value() - '0');
				return true;
			}
			IO.bell();
		}
	}
	
	/**
	 * Moves creature record from one space to another -RAK-
	 * 
	 * @param y1
	 * @param x1
	 * @param y2
	 * @param x2
	 */
	public static void moveCreatureRecord(int y1, int x1, int y2, int x2) {
		// this always works correctly, even if y1==y2 and x1==x2
		int tmp = Variable.cave[y1][x1].creatureIndex;
		Variable.cave[y1][x1].creatureIndex = 0;
		Variable.cave[y2][x2].creatureIndex = tmp;
	}
	
	/**
	 * Room is lit, make it appear -RAK-
	 * 
	 * @param yPos
	 * @param xPos
	 */
	public static void lightUpRoom(int yPos, int xPos) {
		int halfHeight = (Constants.SCREEN_HEIGHT / 2);
		int halfWidth = (Constants.SCREEN_WIDTH / 2);
		int startRow = (yPos / halfHeight) * halfHeight;
		int startCol = (xPos / halfWidth) * halfWidth;
		int endRow = startRow + halfHeight - 1;
		int endCol = startCol + halfWidth - 1;
		for (int y = startRow; y <= endRow; y++) {
			for (int x = startCol; x <= endCol; x++) {
				CaveType cavePos = Variable.cave[y][x];
				if (cavePos.litRoom && ! cavePos.permLight) {
					cavePos.permLight = true;
					if (cavePos.fval == Constants.DARK_FLOOR) {
						cavePos.fval = Constants.LIGHT_FLOOR;
					}
					if (!cavePos.fieldMark && cavePos.treasureIndex != 0) {
						int tval = Treasure.treasureList[cavePos.treasureIndex].category;
						if (tval >= Constants.TV_MIN_VISIBLE && tval <= Constants.TV_MAX_VISIBLE) {
							cavePos.fieldMark = true;
						}
					}
					IO.print(Misc1.locateSymbol(y, x), y, x);
				}
			}
		}
	}
	
	/**
	 * Lights up given location -RAK-
	 * 
	 * @param y
	 * @param x
	 */
	public static void lightUpSpot(int y, int x) {
		if (Misc1.panelContains(y, x)) {
			IO.print(Misc1.locateSymbol(y, x), y, x);
		}
	}
	
	/**
	 * Normal movement
	 * When FIND_FLAG, light only permanent features 
	 * 
	 * @param y1
	 * @param x1
	 * @param y2
	 * @param x2
	 */
	public static void moveLightNormal(int y1, int x1, int y2, int x2) {
		if (Variable.lightFlag) {
			for (int y = y1 - 1; y <= y1 + 1; y++) { // Turn off lamp light
				for (int x = x1 - 1; x <= x1 + 1; x++) {
					Variable.cave[y][x].tempLight = false;
				}
			}
			if (Variable.findFlag != 0 && !Variable.findPrself.value()) {
				Variable.lightFlag = false;
			}
		} else if (Variable.findFlag == 0 || Variable.findPrself.value()) {
			Variable.lightFlag = true;
		}
		
		for (int y = y2 - 1; y <= y2 + 1; y++) {
			for (int x = x2 - 1; x <= x2 + 1; x++) {
				CaveType cavePos = Variable.cave[y][x];
				// only light up if normal movement
				if (Variable.lightFlag) {
					cavePos.tempLight = true;
				}
				if (cavePos.fval >= Constants.MIN_CAVE_WALL) {
					cavePos.permLight = true;
				} else if (!cavePos.fieldMark && cavePos.treasureIndex != 0) {
					int tval = Treasure.treasureList[cavePos.treasureIndex].category;
					if ((tval >= Constants.TV_MIN_VISIBLE) && (tval <= Constants.TV_MAX_VISIBLE)) {
						cavePos.fieldMark = true;
					}
				}
			}
		}
		
		// From uppermost to bottom most lines player was on.
		int top;
		int bottom;
		if (y1 < y2) {
			top = y1 - 1;
			bottom = y2 + 1;
		} else {
			top = y2 - 1;
			bottom = y1 + 1;
		}
		
		int left;
		int right;
		if (x1 < x2) {
			left = x1 - 1;
			right = x2 + 1;
		} else {
			left = x2 - 1;
			right = x1 + 1;
		}
		for (int y = top; y <= bottom; y++) {
			for (int x = left; x <= right; x++) { // Leftmost to rightmost do
				IO.print(Misc1.locateSymbol(y, x), y, x);
			}
		}
	}
	
	/**
	 * When blinded, move only the player symbol.
	 * With no light, movement becomes involved.
	 * 
	 * @param y1
	 * @param x1
	 * @param y2
	 * @param x2
	 */
	public static void moveLightWhileBlind(int y1, int x1, int y2, int x2) {
		if (Variable.lightFlag) {
			for (int y = y1 - 1; y <= y1 + 1; y++) {
				for (int x = x1 - 1; x <= x1 + 1; x++) {
					Variable.cave[y][x].tempLight = false;
					IO.print(Misc1.locateSymbol(y, x), y, x);
				}
			}
			Variable.lightFlag = false;
		} else if (Variable.findFlag == 0 || Variable.findPrself.value()) {
			IO.print(Misc1.locateSymbol(y1, x1), y1, x1);
		}
		
		if (Variable.findFlag == 0 || Variable.findPrself.value()) {
			IO.print('@', y2, x2);
		}
	}
	
	/**
	 * Package for moving the character's light about the screen
	 * Four cases : Normal, Finding, Blind, and Nolight	 -RAK-
	 * 
	 * @param y1
	 * @param x1
	 * @param y2
	 * @param x2
	 */
	public static void moveLight(int y1, int x1, int y2, int x2) {
		if (Player.py.flags.blind > 0 || !Variable.playerLight) {
			moveLightWhileBlind(y1, x1, y2, x2);
		} else {
			moveLightNormal(y1, x1, y2, x2);
		}
	}
	
	/**
	 * Something happens to disturb the player.
	 * The first arg indicates a major disturbance, which affects search.
	 * The second arg indicates a light change. -CJS-
	 * 
	 * @param affectSearch
	 * @param affectLight
	 */
	public static void disturbPlayer(boolean affectSearch, boolean affectLight) {
		Variable.commandCount = 0;
		if (affectSearch && (Player.py.flags.status & Constants.PY_SEARCH) != 0) {
			searchModeOff();
		}
		if (Player.py.flags.rest != 0) {
			stopResting();
		}
		if (affectLight || Variable.findFlag != 0) {
			Variable.findFlag = 0;
			Misc4.checkView();
		}
		IO.flush();
	}
	
	/**
	 * Search Mode enhancement -RAK-
	 */
	public static void searchModeOn() {
		changeSpeed(1);
		Player.py.flags.status |= Constants.PY_SEARCH;
		Misc3.printState();
		Misc3.printSpeed();
		Player.py.flags.foodDigested++;
	}
	
	public static void searchModeOff() {
		Misc4.checkView();
		changeSpeed(-1);
		Player.py.flags.status &= ~Constants.PY_SEARCH;
		Misc3.printState();
		Misc3.printSpeed();
		Player.py.flags.foodDigested--;
	}
	
	/**
	 * Resting allows a player to safely restore his hp -RAK-
	 */
	public static void rest() {
		int restNum;
		
		if (Variable.commandCount > 0) {
			restNum = Variable.commandCount;
			Variable.commandCount = 0;
		} else {
			IO.print("Rest for how long? ", 0, 0);
			restNum = 0;
			String strRestNum = IO.getString(0, 19, 5);
			if (strRestNum.length() > 0) {
				if (strRestNum.charAt(0) == '*') {
					restNum = -Constants.MAX_SHORT;
				} else {
					try {
						restNum = Integer.parseInt(strRestNum);
					} catch (NumberFormatException e) {
						System.err.format(
								"Could not convert %s to an integer in Moria1.rest()",
								strRestNum);
						e.printStackTrace();
						restNum = 0;
					}
				}
			}
		}
		
		// check for reasonable value, must be positive number in range of a
	    // short, or must be -MAX_SHORT
		if ((restNum == -Constants.MAX_SHORT)
				|| (restNum > 0) && (restNum < Constants.MAX_SHORT)) {
			if ((Player.py.flags.status & Constants.PY_SEARCH) != 0) {
				searchModeOff();
			}
			Player.py.flags.rest = restNum;
			Player.py.flags.status |= Constants.PY_REST;
			Misc3.printState();
			Player.py.flags.foodDigested--;
			IO.print ("Press any key to stop resting...", 0, 0);
			IO.putQio();
		} else {
			if (restNum != 0) {
				IO.printMessage ("Invalid rest count.");
			}
			IO.eraseLine(Constants.MSG_LINE, 0);
			Variable.freeTurnFlag = true;
		}
	}
	
	public static void stopResting() {
		Player.py.flags.rest = 0;
		Player.py.flags.status &= ~Constants.PY_REST;
		Misc3.printState();
		// flush last message, or delete "press any key" message
		IO.printMessage("");
		Player.py.flags.foodDigested++;
	}
	
	/**
	 * Attacker's level and plusses, defender's AC -RAK-
	 * 
	 * @param baseToHit
	 * @param level
	 * @param plusToHit
	 * @param ac
	 * @param attackType
	 * @return
	 */
	public static boolean testHit(int baseToHit, int level, int plusToHit,
			int ac, int attackType) {
		disturbPlayer(true, false);
		int i = baseToHit + plusToHit * Constants.BTH_PLUS_ADJ
				+ (level * Player.classLevelAdjust[Player.py.misc.playerClass][attackType]);
		// pth could be less than 0 if player wielding weapon too heavy for him
		// always miss 1 out of 20, always hit 1 out of 20
		int die = Rnd.randomInt(20);
		return (die != 1) && ((die == 20) || ((i > 0) && (Rnd.randomInt (i) > ac))); // normal hit
	}
	
	/**
	 * Decreases players hit points and sets death flag if necessary -RAK-
	 * 
	 * @param damage
	 * @param attackerName
	 */
	public static void takeHit(int damage, String attackerName) {
		if (Player.py.flags.invulnerability > 0) {
			damage = 0;
		}
		Player.py.misc.currHitpoints -= damage;
		if (Player.py.misc.currHitpoints < 0) {
			if (!Variable.death) {
				Variable.death = true;
				Variable.diedFrom = attackerName;
				Variable.isTotalWinner = false;
			}
			Variable.newLevelFlag = true;
		} else {
			Misc3.printCurrentHitpoints();
		}
	}
}
