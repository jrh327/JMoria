/*
 * Dungeon.java: the main command interpreter, updating player status
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
import net.jonhopkins.moria.types.SpellType;

public class Dungeon {
	
	// Moria game module -RAK-
	// The code in this section has gone through many revisions, and
	// some of it could stand some more hard work. -RAK-
	
	// It has had a bit more hard work. -CJS-
	
	private Dungeon() { }
	
	/**
	 * Main procedure for dungeon. -RAK-
	 * <p>
	 * Note: There is a lot of preliminary magic going on here at first
	 */
	public static void dungeon() {
		// init pointers.
		PlayerFlags flags = Player.py.flags;
		PlayerMisc misc = Player.py.misc;
		
		// Check light status for setup
		InvenType lamp = Treasure.inventory[Constants.INVEN_LIGHT];
		if (lamp.misc > 0) {
			Variable.playerLight = true;
		} else {
			Variable.playerLight = false;
		}
		
		// Check for a maximum level
		if (Variable.dungeonLevel > misc.maxDungeonLevel) {
			misc.maxDungeonLevel = Variable.dungeonLevel;
		}
		
		// Reset flags and initialize variables
		Variable.commandCount = 0;
		Variable.newLevelFlag = false;
		Variable.findFlag = 0;
		Variable.teleportFlag = false;
		Monsters.totalMonsterMultiples = 0;
		Variable.cave[Player.y][Player.x].creatureIndex = 1;
		
		// Ensure we display the panel. Used to do this with a global var. -CJS-
		Variable.panelRow = -1;
		Variable.panelCol = -1;
		
		// Light up the area around character
		Misc4.checkView();
		
		// must do this after panel_row/col set to -1, because search_off() will
		// call check_view(), and so the panel_* variables must be valid before
		// search_off() is called
		if ((flags.status & Constants.PY_SEARCH) != 0) {
			Moria1.searchModeOff();
		}
		
		// Light, but do not move critters
		Creature.creatures(false);
		
		// Print the depth
		Misc3.printDepth();
		
		// Loop until dead, or new level
		do {
			// Increment turn counter
			Variable.turn++;
			
			// turn over the store contents every, say, 1000 turns
			if ((Variable.dungeonLevel != 0)
					&& ((Variable.turn % Constants.STORE_TURN_AROUND) == 0)) {
				Store1.storeInventoryInit();
			}
			
			// Check for creature generation
			if (Rnd.randomInt(Constants.MAX_MALLOC_CHANCE) == 1) {
				Misc1.spawnMonster(1, Constants.MAX_SIGHT, false);
			}
			
			// Check light status
			lamp = Treasure.inventory[Constants.INVEN_LIGHT];
			if (Variable.playerLight) {
				if (lamp.misc > 0) {
					lamp.misc--;
					if (lamp.misc == 0) {
						Variable.playerLight = false;
						IO.printMessage("Your light has gone out!");
						Moria1.disturbPlayer(false, true);
						
						// unlight creatures
						Creature.creatures(false);
					} else if ((lamp.misc < 40) && (Rnd.randomInt(5) == 1) && (flags.blind < 1)) {
						Moria1.disturbPlayer (false, false);
						IO.printMessage("Your light is growing faint.");
					}
				} else {
					Variable.playerLight = false;
					Moria1.disturbPlayer(false, true);
					
					// unlight creatures
					Creature.creatures(false);
				}
			} else if (lamp.misc > 0) {
				lamp.misc--;
				Variable.playerLight = true;
				Moria1.disturbPlayer(false, true);
				
				// light creatures
				Creature.creatures(false);
			}
			
			// Update counters and messages
			Player.py.updateStatusEffects();
			
			// Allow for a slim chance of detect enchantment -CJS-
			// for 1st level char, check once every 2160 turns
			// for 40th level char, check once every 416 turns
			if (((Variable.turn & 0xF) == 0)
					&& (flags.confused == 0)
					&& (Rnd.randomInt(10 + 750 / (5 + misc.level)) == 1)) {
				for (int i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
					if (i == Treasure.invenCounter) {
						i = 22;
					}
					lamp = Treasure.inventory[i];
					// if in inventory, succeed 1 out of 50 times,
					// if in equipment list, success 1 out of 10 times
					if ((lamp.category != Constants.TV_NOTHING)
							&& isEnchanted(lamp)
							&& (Rnd.randomInt((i < 22) ? 50 : 10) == 1)) {
						String tmp = String.format("There's something about what you are %s...",
								Moria1.describeUse(i));
						Moria1.disturbPlayer(false, false);
						IO.printMessage(tmp);
						Misc4.addInscription(lamp, Constants.ID_MAGIK);
					}
				}
			}
			
			// Check the state of the monster list, and delete some monsters if
			// the monster list is nearly full. This helps to avoid problems in
			// creature.c when monsters try to multiply. Compact_monsters() is
			// much more likely to succeed if called from here, than if called
			// from within creature.creatures().
			if (Constants.MAX_MALLOC - Monsters.freeMonsterIndex < 10) {
				Misc1.compactMonsters();
			}
			
			if ((flags.paralysis < 1) && (flags.rest == 0) && (!Variable.death)) {
				getCommand();
			} else {
				// if paralyzed, resting, or dead, flush output
				// but first move the cursor onto the player, for aesthetics
				IO.moveCursorRelative(Player.y, Player.x);
				IO.putQio();
			}
			
			// Teleport?
			if (Variable.teleportFlag) {
				Misc3.teleport(100);
			}
			
			// Move the creatures
			if (!Variable.newLevelFlag) {
				Creature.creatures(true);
			}
			// Exit when new_level_flag is set
		} while (!Variable.newLevelFlag && Variable.eofFlag == 0);
	}
	
	public static char mapToOriginalCommand(char command) {
		IntPointer direction = new IntPointer();
		
		switch(command) {
		case (Constants.CTRL & 'K'): // ^K = exit
			command = 'Q';
			break;
		case (Constants.CTRL & 'J'):
		case (Constants.CTRL & 'M'):
			command = '+';
			break;
		case (Constants.CTRL & 'P'): // ^P = repeat
		case (Constants.CTRL & 'W'): // ^W = password
		case (Constants.CTRL & 'X'): // ^X = save
		case (Constants.CTRL & 'V'): // ^V = view license
		case ' ':
		case '!':
		case '$':
			break;
		case '.':
			if (Moria1.getDirection("", direction)) {
				switch (direction.value()) {
				case 1:
					command = 'B';
					break;
				case 2:
					command = 'J';
					break;
				case 3:
					command = 'N';
					break;
				case 4:
					command = 'H';
					break;
				case 6:
					command = 'L';
					break;
				case 7:
					command = 'Y';
					break;
				case 8:
					command = 'K';
					break;
				case 9:
					command = 'U';
					break;
				default:
					command = ' ';
					break;
				}
			} else {
				command = ' ';
			}
			break;
		case '/':
		case '<':
		case '>':
		case '-':
		case '=':
		case '{':
		case '?':
		case 'A':
			break;
		case '1':
			command = 'b';
			break;
		case '2':
			command = 'j';
			break;
		case '3':
			command = 'n';
			break;
		case '4':
			command = 'h';
			break;
		case '5': // Rest one turn
			command = '.';
			break;
		case '6':
			command = 'l';
			break;
		case '7':
			command = 'y';
			break;
		case '8':
			command = 'k';
			break;
		case '9':
			command = 'u';
			break;
		case 'B':
			command = 'f';
			break;
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
			break;
		case 'L':
			command = 'W';
			break;
		case 'M':
			break;
		case 'R':
			break;
		case 'S':
			command = '#';
			break;
		case 'T':
			if (Moria1.getDirection("", direction)) {
				switch (direction.value()) {
				case 1:
					command = (Constants.CTRL & 'B');
					break;
				case 2:
					command = (Constants.CTRL & 'J');
					break;
				case 3:
					command = (Constants.CTRL & 'N');
					break;
				case 4:
					command = (Constants.CTRL & 'H');
					break;
				case 6:
					command = (Constants.CTRL & 'L');
					break;
				case 7:
					command = (Constants.CTRL & 'Y');
					break;
				case 8:
					command = (Constants.CTRL & 'K');
					break;
				case 9:
					command = (Constants.CTRL & 'U');
					break;
				default:
					command = ' ';
					break;
				}
			} else {
				command = ' ';
			}
			break;
		case 'V':
			break;
		case 'a':
			command = 'z';
			break;
		case 'b':
			command = 'P';
			break;
		case 'c':
		case 'd':
		case 'e':
			break;
		case 'f':
			command = 't';
			break;
		case 'h':
			command = '?';
			break;
		case 'i':
			break;
		case 'j':
			command = 'S';
			break;
		case 'l':
			command = 'x';
			break;
		case 'm':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
			break;
		case 't':
			command = 'T';
			break;
		case 'u':
			command = 'Z';
			break;
		case 'v':
		case 'w':
			break;
		case 'x':
			command = 'X';
			break;
			
		// wizard mode commands follow
		case (Constants.CTRL & 'A'): // ^A = cure all
			break;
		case (Constants.CTRL & 'B'): // ^B = objects
			command = (Constants.CTRL & 'O');
			break;
		case (Constants.CTRL & 'D'): // ^D = up/down
			break;
		case (Constants.CTRL & 'H'): // ^H = wizhelp
			command = '\\';
			break;
		case (Constants.CTRL & 'I'): // ^I = identify
			break;
		case (Constants.CTRL & 'L'): // ^L = wizlight
			command = '*';
			break;
		case ':':
		case (Constants.CTRL & 'T'): // ^T = teleport
		case (Constants.CTRL & 'E'): // ^E = wizchar
		case (Constants.CTRL & 'F'): // ^F = genocide
		case (Constants.CTRL & 'G'): // ^G = treasure
		case '@':
		case '+':
			break;
		case (Constants.CTRL & 'U'): // ^U = summon
			command = '&';
			break;
		default:
			command = '~'; // Anything illegal.
			break;
		}
		return command;
	}
	
	public static void getCommand() {
		int find_count = 0;
		PlayerFlags flags = Player.py.flags;
		
		// Accept a command and execute it
		do {
			if ((flags.status & Constants.PY_REPEAT) != 0) {
				Misc3.printState();
			}
			Variable.defaultDir = 0;
			Variable.freeTurnFlag = false;
			
			if (Variable.findFlag > 0) {
				Moria2.findRun();
				find_count--;
				if (find_count == 0) {
					Moria2.endFind();
				}
				IO.putQio();
			} else if (Variable.doingInven > 0) {
				Moria1.doInvenCommand(Variable.doingInven);
			} else {
				CharPointer command = new CharPointer(); // Last command
				// move the cursor to the players character
				IO.moveCursorRelative(Player.y, Player.x);
				if (Variable.commandCount > 0) {
					Variable.msgFlag = 0;
					Variable.defaultDir = 1;
				} else {
					Variable.msgFlag = 0;
					command.value(IO.inkey());
					int i = 0;
					
					// Get a count for a command.
					if ((Variable.rogueLikeCommands.value()
							&& command.value() >= '0' && command.value() <= '9')
							|| (!Variable.rogueLikeCommands.value()
									&& command.value() == '#')) {
						IO.print("Repeat count:", 0, 0);
						if (command.value() == '#') {
							command.value('0');
						}
						
						i = 0;
						while (true) {
							if (command.value() == Constants.DELETE
									|| command.value() == (Constants.CTRL & 'H')) {
								i = i / 10;
								String tmp = String.format("%d", i);
								if (tmp.length() > 8) {
									tmp = tmp.substring(0, 8);
								}
								IO.print(tmp, 0, 14);
							} else if (command.value() >= '0' && command.value() <= '9') {
								if (i > 99) {
									IO.bell();
								} else {
									i = i * 10 + command.value() - '0';
									String tmp = String.format("%d", i);
									IO.print(tmp, 0, 14);
								}
							} else {
								break;
							}
							command.value(IO.inkey());
						}
						
						if (i == 0) {
							i = 99;
							String tmp = String.format("%d", i);
							IO.print(tmp, 0, 14);
						}
						
						// a special hack to allow numbers as commands
						if (command.value() == ' ') {
							IO.print("Command:", 0, 20);
							command.value(IO.inkey());
						}
					}
					
					// Another way of typing control codes -CJS-
					if (command.value() == '^') {
						if (Variable.commandCount > 0) {
							Misc3.printState();
						}
						if (IO.getCommand("Control-", command)) {
							if (command.value() >= 'A' && command.value() <= 'Z') {
								command.value((char)(command.value() - 'A' - 1));
							} else if (command.value() >= 'a' && command.value() <= 'z') {
								command.value((char)(command.value() - 'a' - 1));
							} else {
								IO.printMessage("Type ^ <letter> for a control char");
								command.value(' ');
							}
						} else {
							command.value(' ');
						}
					}
					
					// move cursor to player char again, in case it moved
					IO.moveCursorRelative(Player.y, Player.x);
					
					// Commands are always converted to rogue form. -CJS-
					if (!Variable.rogueLikeCommands.value()) {
						command.value(mapToOriginalCommand(command.value()));
					}
					if (i > 0) {
						if (!isValidCountCommand(command.value())) {
							Variable.freeTurnFlag = true;
							IO.printMessage("Invalid command with a count.");
							command.value(' ');
						} else {
							Variable.commandCount = i;
							Misc3.printState();
						}
					}
				}
				
				// Flash the message line.
				IO.eraseLine(Constants.MSG_LINE, 0);
				IO.moveCursorRelative(Player.y, Player.x);
				IO.putQio();
				
				doCommand(command.value());
				
				// Find is counted differently, as the command changes.
				if (Variable.findFlag > 0) {
					find_count = Variable.commandCount - 1;
					Variable.commandCount = 0;
				} else if (Variable.freeTurnFlag) {
					Variable.commandCount = 0;
				} else if (Variable.commandCount > 0) {
					Variable.commandCount--;
				}
			}
			// End of commands
		} while (Variable.freeTurnFlag && !Variable.newLevelFlag && Variable.eofFlag == 0);
	}
	
	public static void doCommand(char command) {
		PlayerFlags flags = Player.py.flags;
		PlayerMisc misc = Player.py.misc;
		
		// hack for move without pickup. Map '-' to a movement command.
		boolean doPickup = true;
		int commandCount = 0;
		if (command == '-') {
			doPickup = false;
			commandCount = Variable.commandCount;
			IntPointer direction = new IntPointer();
			if (Moria1.getDirection("", direction)) {
				Variable.commandCount = commandCount;
				switch (direction.value()) {
				case 1:
					command = 'b';
					break;
				case 2:
					command = 'j';
					break;
				case 3:
					command = 'n';
					break;
				case 4:
					command = 'h';
					break;
				case 6:
					command = 'l';
					break;
				case 7:
					command = 'y';
					break;
				case 8:
					command = 'k';
					break;
				case 9:
					command = 'u';
					break;
				default:
					command = '~';
					break;
				}
			} else {
				command = ' ';
			}
		}
		
		switch(command) {
		case 'Q': // (Q)uit (^K)ill
			IO.flush();
			if (IO.getCheck("Do you really want to quit?")) {
				Variable.newLevelFlag = true;
				Variable.death = true;
				Variable.diedFrom = "Quitting";
			}
			Variable.freeTurnFlag = true;
			break;
		case (Constants.CTRL & 'P'): // (^P)revious message.
			if (Variable.commandCount > 0) {
				commandCount = Variable.commandCount;
				if (commandCount > Constants.MAX_SAVE_MSG) {
					commandCount = Constants.MAX_SAVE_MSG;
				}
				Variable.commandCount = 0;
			} else if (Variable.lastCommand != (Constants.CTRL & 'P')) {
				commandCount = 1;
			} else {
				commandCount = Constants.MAX_SAVE_MSG;
			}
			int message = Variable.lastMsg;
			if (commandCount > 1) {
				IO.saveScreen();
				IntPointer row = new IntPointer(commandCount);
				while (commandCount > 0) {
					commandCount--;
					IO.print(Variable.oldMsg[message], commandCount, 0);
					if (message == 0) {
						message = Constants.MAX_SAVE_MSG - 1;
					} else {
						message--;
					}
				}
				IO.eraseLine(row.value(), 0);
				IO.pauseLine(row.value());
				IO.restoreScreen();
			} else {
				// Distinguish real and recovered messages with a '>'. -CJS-
				IO.putBuffer(">", 0, 0);
				IO.print(Variable.oldMsg[message], 0, 1);
			}
			Variable.freeTurnFlag = true;
			break;
		case (Constants.CTRL & 'V'): // (^V)iew license
			Files.helpfile(Config.MORIA_GPL);
			Variable.freeTurnFlag = true;
			break;
		case (Constants.CTRL & 'W'): // (^W)izard mode
			if (Variable.isWizard) {
				Variable.isWizard = false;
				IO.printMessage("Wizard mode off.");
			} else if (Misc3.enterWizardMode()) {
				IO.printMessage("Wizard mode on.");
			}
			Misc3.printWinner();
			Variable.freeTurnFlag = true;
			break;
		case (Constants.CTRL & 'X'): // e(^X)it and save
			if (Variable.isTotalWinner) {
				IO.printMessage("You are a Total Winner,  your character must be retired.");
				if (Variable.rogueLikeCommands.value()) {
					IO.printMessage("Use 'Q' to when you are ready to quit.");
				} else {
					IO.printMessage ("Use <Control>-K when you are ready to quit.");
				}
			} else {
				Variable.diedFrom = "(saved)";
				IO.printMessage("Saving game...");
				if (Save.saveCharacter()) {
					Death.exitGame();
				}
				Variable.diedFrom = "(alive and well)";
			}
			Variable.freeTurnFlag = true;
			break;
		case '=': // (=) set options
			IO.saveScreen();
			Misc2.setOptions();
			IO.restoreScreen();
			Variable.freeTurnFlag = true;
			break;
		case '{': // ({) inscribe an object
			Misc4.enscribeObject();
			Variable.freeTurnFlag = true;
			break;
		case '!': // (!) escape to the shell
		case '$':
			IO.shellOut();
			Variable.freeTurnFlag = true;
			break;
		case Constants.ESCAPE: // (ESC) do nothing.
		case ' ': // (space) do nothing.
			Variable.freeTurnFlag = true;
			break;
		case 'b': // (b) down, left (1)
			Moria3.movePlayer(1, doPickup);
			break;
		case Constants.KEY_DOWN:
		case 'j': // (j) down (2)
			Moria3.movePlayer(2, doPickup);
			break;
		case 'n': // (n) down, right (3)
			Moria3.movePlayer(3, doPickup);
			break;
		case Constants.KEY_LEFT:
		case 'h': // (h) left (4)
			Moria3.movePlayer(4, doPickup);
			break;
		case Constants.KEY_RIGHT:
		case 'l': // (l) right (6)
			Moria3.movePlayer(6, doPickup);
			break;
		case 'y': // (y) up, left (7)
			Moria3.movePlayer(7, doPickup);
			break;
		case Constants.KEY_UP:
		case 'k': // (k) up (8)
			Moria3.movePlayer(8, doPickup);
			break;
		case 'u': // (u) up, right (9)
			Moria3.movePlayer(9, doPickup);
			break;
		case 'B': // (B) run down, left	(. 1)
			Moria2.findInit(1);
			break;
		case 'J': // (J) run down (. 2)
			Moria2.findInit(2);
			break;
		case 'N': // (N) run down, right (. 3)
			Moria2.findInit(3);
			break;
		case 'H': // (H) run left (. 4)
			Moria2.findInit(4);
			break;
		case 'L': // (L) run right (. 6)
			Moria2.findInit(6);
			break;
		case 'Y': // (Y) run up, left (. 7)
			Moria2.findInit(7);
			break;
		case 'K': // (K) run up (. 8)
			Moria2.findInit(8);
			break;
		case 'U': // (U) run up, right (. 9)
			Moria2.findInit(9);
			break;
		case '/': // (/) identify a symbol
			Help.identifySymbol();
			Variable.freeTurnFlag = true;
			break;
		case '.': // (.) stay in one place (5)
			Moria3.movePlayer(5, doPickup);
			if (Variable.commandCount > 1) {
				Variable.commandCount--;
				Moria1.rest();
			}
			break;
		case '<': // (<) go up a staircase
			goUp();
			break;
		case '>': // (>) go down a staircase
			goDown();
			break;
		case '?': // (?) help with commands
			if (Variable.rogueLikeCommands.value()) {
				Files.helpfile(Config.MORIA_HELP);
			} else {
				Files.helpfile(Config.MORIA_ORIG_HELP);
			}
			Variable.freeTurnFlag = true;
			break;
		case 'f': // (f)orce (B)ash
			Moria4.bash();
			break;
		case 'C': // (C)haracter description
			IO.saveScreen();
			Misc3.changeName();
			IO.restoreScreen();
			Variable.freeTurnFlag = true;
			break;
		case 'D': // (D)isarm trap
			Moria4.disarmTrap();
			break;
		case 'E': // (E)at food
			Eat.eat();
			break;
		case 'F': // (F)ill lamp
			refillLamp();
			break;
		case 'G': // (G)ain magic spells
			Misc3.gainSpells();
			break;
		case 'V': // (V)iew scores
			boolean b;
			if (Variable.lastCommand != 'V') {
				b = true;
			} else {
				b = false;
			}
			IO.saveScreen();
			Death.displayScores(b);
			IO.restoreScreen();
			Variable.freeTurnFlag = true;
			break;
		case 'W': // (W)here are we on the map (L)ocate on map
			if ((flags.blind > 0) || Moria1.playerHasNoLight()) {
				IO.printMessage("You can't see your map.");
			} else {
				IntPointer y = new IntPointer(Player.y);
				IntPointer x = new IntPointer(Player.x);
				if (Misc1.getPanel(y.value(), x.value(), true)) {
					Misc1.printMap();
				}
				
				int currPanelY = Variable.panelRow;
				int currPanelX = Variable.panelCol;
				for(;;) {
					int panelY = Variable.panelRow;
					int panelX = Variable.panelCol;
					String tmpStr = null;
					if (panelY == currPanelY && panelX == currPanelX) {
						tmpStr = "";
					} else {
						tmpStr = String.format("%s%s of",
								(panelY < currPanelY) ? " North"
										: (panelY > currPanelY) ? " South" : "",
								(panelX < currPanelX) ? " West"
										: (panelX > currPanelX) ? " East" : "");
					}
					
					String outVal = String.format("Map sector [%d,%d], which is%s your sector. Look which direction?",
							panelY, panelX, tmpStr);
					IntPointer direction = new IntPointer();
					if (!Moria1.getDirection(outVal, direction)) {
						break;
					}
					// -CJS-
					// Should really use the move function, but what the hell. This
					// is nicer, as it moves exactly to the same place in another
					// section. The direction calculation is not intuitive. Sorry.
					for(;;){
						x.value(x.value()
								+ ((direction.value() - 1) % 3 - 1)
								* Constants.SCREEN_WIDTH / 2);
						y.value(y.value()
								- ((direction.value() - 1) / 3 - 1)
								* Constants.SCREEN_HEIGHT / 2);
						if (x.value() < 0 || y.value() < 0
								|| x.value() >= Variable.currWidth
								|| y.value() >= Variable.currWidth) {
							IO.printMessage("You've gone past the end of your map.");
							x.value(x.value()
									- ((direction.value() - 1) % 3 - 1)
									* Constants.SCREEN_WIDTH / 2);
							y.value(y.value()
									+ ((direction.value() - 1) / 3 - 1)
									* Constants.SCREEN_HEIGHT / 2);
							break;
						}
						if (Misc1.getPanel(y.value(), x.value(), true)) {
							Misc1.printMap();
							break;
						}
					}
				}
				
				// Move to a new panel - but only if really necessary.
				if (Misc1.getPanel(Player.y, Player.x, false)) {
					Misc1.printMap();
				}
			}
			Variable.freeTurnFlag = true;
			break;
		case 'R': // (R)est a while
			Moria1.rest();
			break;
		case '#': // (#) search toggle (S)earch toggle
			if ((flags.status & Constants.PY_SEARCH) != 0) {
				Moria1.searchModeOff();
			} else {
				Moria1.searchModeOn();
			}
			Variable.freeTurnFlag = true;
			break;
		case (Constants.CTRL & 'B'): // (^B) tunnel down left (T 1)
			Moria4.tunnel(1);
			break;
		case (Constants.CTRL & 'M'): // cr must be treated same as lf.
		case (Constants.CTRL & 'J'): // (^J) tunnel down (T 2)
			Moria4.tunnel(2);
			break;
		case (Constants.CTRL & 'N'): // (^N) tunnel down right (T 3)
			Moria4.tunnel(3);
			break;
		case (Constants.CTRL & 'H'): // (^H) tunnel left (T 4)
			Moria4.tunnel(4);
			break;
		case (Constants.CTRL & 'L'): // (^L) tunnel right (T 6)
			Moria4.tunnel(6);
			break;
		case (Constants.CTRL & 'Y'): // (^Y) tunnel up left (T 7)
			Moria4.tunnel(7);
			break;
		case (Constants.CTRL & 'K'): // (^K) tunnel up (T 8)
			Moria4.tunnel(8);
			break;
		case (Constants.CTRL & 'U'): // (^U) tunnel up right (T 9)
			Moria4.tunnel(9);
			break;
		case 'z': // (z)ap a wand (a)im a wand
			Wands.aim();
			break;
		case 'M':
			IO.screenMap();
			Variable.freeTurnFlag = true;
			break;
		case 'P': // (P)eruse a book (B)rowse in a book
			examineBook();
			Variable.freeTurnFlag = true;
			break;
		case 'c': // (c)lose an object
			Moria3.closeDoor();
			break;
		case 'd': // (d)rop something
			Moria1.doInvenCommand('d');
			break;
		case 'e': // (e)quipment list
			Moria1.doInvenCommand('e');
			break;
		case 't': // (t)hrow something (f)ire something
			Moria4.throwObject();
			break;
		case 'i': // (i)nventory list
			Moria1.doInvenCommand('i');
			break;
		case 'S': // (S)pike a door (j)am a door
			jamDoor();
			break;
		case 'x': // e(x)amine surrounds (l)ook about
			Moria4.look();
			Variable.freeTurnFlag = true;
			break;
		case 'm': // (m)agic spells
			Magic.cast();
			break;
		case 'o': // (o)pen something
			Moria3.openDoorOrChest();
			break;
		case 'p': // (p)ray
			Prayer.pray();
			break;
		case 'q': // (q)uaff
			Potions.quaff();
			break;
		case 'r': // (r)ead
			Scrolls.readScroll();
			break;
		case 's': // (s)earch for a turn
			Moria2.search(Player.y, Player.x, misc.searchChance);
			break;
		case 'T': // (T)ake off something (t)ake off
			Moria1.doInvenCommand('t');
			break;
		case 'Z': // (Z)ap a staff (u)se a staff
			Staffs.use();
			break;
		case 'v': // (v)ersion of game
			Files.helpfile(Config.MORIA_VER);
			Variable.freeTurnFlag = true;
			break;
		case 'w': // (w)ear or wield
			Moria1.doInvenCommand('w');
			break;
		case 'X': // e(X)change weapons e(x)change
			Moria1.doInvenCommand('x');
			break;
		default:
			if (Variable.isWizard) {
				Variable.freeTurnFlag = true; // Wizard commands are free moves
				switch(command) {
				case (Constants.CTRL & 'A'): // ^A = Cure all
					Spells.removeCurse();
					Spells.cureBlindness();
					Spells.cureConfusion();
					Spells.curePoison();
					Spells.removeFear();
					Misc3.restoreStat(Constants.A_STR);
					Misc3.restoreStat(Constants.A_INT);
					Misc3.restoreStat(Constants.A_WIS);
					Misc3.restoreStat(Constants.A_CON);
					Misc3.restoreStat(Constants.A_DEX);
					Misc3.restoreStat(Constants.A_CHR);
					if (flags.slow > 1) {
						flags.slow = 1;
					}
					if (flags.imagine > 1) {
						flags.imagine = 1;
					}
					break;
				case (Constants.CTRL & 'E'): // ^E = wizchar
					Wizard.changeCharacter();
					IO.eraseLine(Constants.MSG_LINE, 0);
				break;
				case (Constants.CTRL & 'F'): // ^F = genocide
					Spells.massGenocide();
					break;
				case (Constants.CTRL & 'G'): // ^G = treasure
					if (Variable.commandCount > 0) {
						commandCount = Variable.commandCount;
						Variable.commandCount = 0;
					} else {
						commandCount = 1;
					}
					Misc3.spawnRandomObject(Player.y, Player.x, commandCount);
					Misc1.printMap();
					break;
				case (Constants.CTRL & 'D'): // ^D = up/down
					if (Variable.commandCount > 0) {
						if (Variable.commandCount > 99) {
							commandCount = 0;
						} else {
							commandCount = Variable.commandCount;
						}
						Variable.commandCount = 0;
					} else {
						IO.print("Go to which level (0-99) ? ", 0, 0);
						commandCount = -1;
						String tmpStr = null;
						if ((tmpStr = IO.getString(0, 27, 10)).length() > 0) {
							try {
								commandCount = Integer.parseInt(tmpStr);
							} catch (NumberFormatException e) {
								System.err.println("tmpStr cannot be converted to an integer in doCommand()");
								e.printStackTrace();
								commandCount = 0;
							}
						}
					}
					if (commandCount > -1) {
						Variable.dungeonLevel = commandCount;
						if (Variable.dungeonLevel > 99) {
							Variable.dungeonLevel = 99;
						}
						Variable.newLevelFlag = true;
					} else {
						IO.eraseLine(Constants.MSG_LINE, 0);
					}
					break;
				case (Constants.CTRL & 'O'): // ^O = objects
					Files.printObjects();
					break;
				case '\\': // \ wizard help
					if (Variable.rogueLikeCommands.value()) {
						Files.helpfile(Config.MORIA_WIZ_HELP);
					} else {
						Files.helpfile(Config.MORIA_OWIZ_HELP);
					}
					break;
				case (Constants.CTRL & 'I'): // ^I = identify
					Spells.identifyObject();
					break;
				case '*':
					Wizard.wizardLight();
					break;
				case ':':
					Spells.mapArea();
					break;
				case (Constants.CTRL & 'T'): // ^T = teleport
					Misc3.teleport(100);
					break;
				case '+':
					if (Variable.commandCount > 0) {
						misc.currExp = Variable.commandCount;
						Variable.commandCount = 0;
					} else if (misc.currExp == 0) {
						misc.currExp = 1;
					} else {
						misc.currExp = misc.currExp * 2;
					}
					Misc3.printExperience();
					break;
				case '&':	// & = summon
					IntPointer y = new IntPointer(Player.y);
					IntPointer x = new IntPointer(Player.x);
					Misc1.summonMonster(y, x, true);
					Creature.creatures(false);
					break;
				case '@':
					Wizard.wizardCreate();
					break;
				default:
					if (Variable.rogueLikeCommands.value()) {
						IO.print("Type '?' or '\\' for help.", 0, 0);
					} else {
						IO.print("Type '?' or ^H for help.", 0, 0);
					}
				}
			} else {
				IO.print("Type '?' for help.", 0, 0);
				Variable.freeTurnFlag = true;
			}
		}
		Variable.lastCommand = command;
	}
	
	/**
	 * Check whether this command will accept a count. -CJS-
	 * 
	 * @param command The command to check
	 * @return Whether the command accepts a count
	 */
	public static boolean isValidCountCommand(char command) {
		switch(command) {
		case 'Q':
		case (Constants.CTRL & 'W'):
		case (Constants.CTRL & 'X'):
		case '=':
		case '{':
		case '/':
		case '<':
		case '>':
		case '?':
		case 'C':
		case 'E':
		case 'F':
		case 'G':
		case 'V':
		case '#':
		case 'z':
		case 'P':
		case 'c':
		case 'd':
		case 'e':
		case 't':
		case 'i':
		case 'x':
		case 'm':
		case 'p':
		case 'q':
		case 'r':
		case 'T':
		case 'Z':
		case 'v':
		case 'w':
		case 'W':
		case 'X':
		case (Constants.CTRL & 'A'):
		case '\\':
		case (Constants.CTRL & 'I'):
		case '*':
		case ':':
		case (Constants.CTRL & 'T'):
		case (Constants.CTRL & 'E'):
		case (Constants.CTRL & 'F'):
		case (Constants.CTRL & 'S'):
		case (Constants.CTRL & 'Q'):
			return false;
		case (Constants.CTRL & 'P'):
		case Constants.ESCAPE:
		case ' ':
		case '-':
		case 'b':
		case 'f':
		case 'j':
		case 'n':
		case 'h':
		case 'l':
		case 'y':
		case 'k':
		case 'u':
		case '.':
		case 'B':
		case 'J':
		case 'N':
		case 'H':
		case 'L':
		case 'Y':
		case 'K':
		case 'U':
		case 'D':
		case 'R':
		case (Constants.CTRL & 'Y'):
		case (Constants.CTRL & 'K'):
		case (Constants.CTRL & 'U'):
		case (Constants.CTRL & 'L'):
		case (Constants.CTRL & 'N'):
		case (Constants.CTRL & 'J'):
		case (Constants.CTRL & 'B'):
		case (Constants.CTRL & 'H'):
		case 'S':
		case 'o':
		case 's':
		case (Constants.CTRL & 'D'):
		case (Constants.CTRL & 'G'):
		case '+':
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Is an item an enchanted weapon or armor and we don't know? -CJS-
	 * <p>
	 * Only returns true if it is a good enchantment
	 * 
	 * @param item The item to check
	 * @return Whether the item is enchanted
	 */
	public static boolean isEnchanted(InvenType item) {
		if (item.category < Constants.TV_MIN_ENCHANT
				|| item.category > Constants.TV_MAX_ENCHANT
				|| (item.flags & Constants.TR_CURSED) != 0) {
			return false;
		}
		if (Desc.arePlussesKnownByPlayer(item)) {
			return false;
		}
		if ((item.identify & Constants.ID_MAGIK) != 0) {
			return false;
		}
		if (item.tohit > 0 || item.plusToDam > 0 || item.plusToArmorClass > 0) {
			return true;
		}
		if ((0x4000107f & item.flags) != 0 && item.misc > 0) {
			return true;
		}
		if ((0x07ffe980 & item.flags) != 0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Examine a Book. -RAK-
	 */
	public static void examineBook() {
		IntPointer first = new IntPointer();
		IntPointer last = new IntPointer();
		IntPointer index = new IntPointer();
		
		if (!Misc3.findRange(Constants.TV_MAGIC_BOOK, Constants.TV_PRAYER_BOOK, first, last)) {
			IO.printMessage("You are not carrying any books.");
		} else if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see to read your spell book!");
		} else if (Moria1.playerHasNoLight()) {
			IO.printMessage("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.printMessage("You are too confused.");
		} else if (Moria1.getItemId(index, "Which Book?", first.value(), last.value(), null, "")) {
			boolean canUnderstand = true;
			InvenType book = Treasure.inventory[index.value()];
			if (Player.Class[Player.py.misc.playerClass].spell == Constants.MAGE) {
				if (book.category != Constants.TV_MAGIC_BOOK) {
					canUnderstand = false;
				}
			} else if (Player.Class[Player.py.misc.playerClass].spell == Constants.PRIEST) {
				if (book.category != Constants.TV_PRAYER_BOOK) {
					canUnderstand = false;
				}
			} else {
				canUnderstand = false;
			}
			
			if (!canUnderstand) {
				IO.printMessage("You do not understand the language.");
				return;
			}
			
			int spellNum = 0;
			int[] spells = new int[31];
			IntPointer flags = new IntPointer(Treasure.inventory[index.value()].flags);
			while (flags.value() != 0) {
				int spellIndex = Misc1.firstBitPos(flags);
				SpellType spell = Player.magicSpell[Player.py.misc.playerClass - 1][spellIndex];
				if (spell.level < 99) {
					spells[spellNum] = spellIndex;
					spellNum++;
				}
			}
			IO.saveScreen();
			Misc3.printSpells(spells, spellNum, true, -1);
			IO.pauseLine(0);
			IO.restoreScreen();
		}
	}
	
	/**
	 * Go up one level. -RAK-
	 */
	public static void goUp() {
		boolean noStairs = false;
		CaveType cavePos = Variable.cave[Player.y][Player.x];
		
		if (cavePos.treasureIndex != 0) {
			if (Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_UP_STAIR) {
				Variable.dungeonLevel--;
				Variable.newLevelFlag = true;
				IO.printMessage("You enter a maze of up staircases.");
				IO.printMessage("You pass through a one-way door.");
			} else {
				noStairs = true;
			}
		} else {
			noStairs = true;
		}
		
		if (noStairs) {
			IO.printMessage("I see no up staircase here.");
			Variable.freeTurnFlag = true;
		}
	}
	
	/**
	 * Go down one level. -RAK-
	 */
	public static void goDown() {
		boolean noStairs = false;
		CaveType cavePos = Variable.cave[Player.y][Player.x];
		
		if (cavePos.treasureIndex != 0) {
			if (Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_DOWN_STAIR) {
				Variable.dungeonLevel++;
				Variable.newLevelFlag = true;
				IO.printMessage("You enter a maze of down staircases.");
				IO.printMessage("You pass through a one-way door.");
			} else {
				noStairs = true;
			}
		} else {
			noStairs = true;
		}
		
		if (noStairs) {
			IO.printMessage("I see no down staircase here.");
			Variable.freeTurnFlag = true;
		}
	}
	
	/**
	 * Jam a closed door. -RAK-
	 */
	public static void jamDoor() {
		Variable.freeTurnFlag = true;
		
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		IntPointer dir = new IntPointer();
		if (Moria1.getDirection("", dir)) {
			Misc3.canMoveDirection(dir.value(), y, x);
			CaveType cavePos = Variable.cave[y.value()][x.value()];
			if (cavePos.treasureIndex != 0) {
				InvenType door = Treasure.treasureList[cavePos.treasureIndex];
				if (door.category == Constants.TV_CLOSED_DOOR) {
					if (cavePos.creatureIndex == 0) {
						IntPointer index = new IntPointer();
						if (Misc3.findRange(Constants.TV_SPIKE,
								Constants.TV_NEVER, index, new IntPointer())) {
							Variable.freeTurnFlag = false;
							IO.countMessagePrint("You jam the door with a spike.");
							if (door.misc > 0) {
								door.misc = -door.misc; // Make locked to stuck.
							}
							// Successive spikes have a progressively smaller effect.
							// Series is: 0 20 30 37 43 48 52 56 60 64 67 70 ...
							door.misc -= 1 + 190 / (10 - door.misc);
							InvenType spike = Treasure.inventory[index.value()];
							if (spike.number > 1) {
								spike.number--;
								Treasure.invenWeight -= spike.weight;
							} else {
								Misc3.destroyInvenItem(index.value());
							}
						} else {
							IO.printMessage("But you have no spikes.");
						}
					} else {
						Variable.freeTurnFlag = false;
						IO.printMessage(String.format("The %s is in your way!",
								Monsters.creatureList[Monsters.monsterList[cavePos.creatureIndex].index].name));
					}
				} else if (door.category == Constants.TV_OPEN_DOOR) {
					IO.printMessage("The door must be closed first.");
				} else {
					IO.printMessage("That isn't a door!");
				}
			} else {
				IO.printMessage("That isn't a door!");
			}
		}
	}
	
	/**
	 * Refill the player's lamp. -RAK-
	 */
	public static void refillLamp() {
		IntPointer indexPtr = new IntPointer();
		
		Variable.freeTurnFlag = true;
		int k = Treasure.inventory[Constants.INVEN_LIGHT].subCategory;
		if (k != 0) {
			IO.printMessage("But you are not using a lamp.");
		} else if (!Misc3.findRange(Constants.TV_FLASK,
				Constants.TV_NEVER, indexPtr, new IntPointer())) {
			IO.printMessage("You have no oil.");
		} else {
			Variable.freeTurnFlag = false;
			InvenType lamp = Treasure.inventory[Constants.INVEN_LIGHT];
			int index = indexPtr.value();
			lamp.misc += Treasure.inventory[index].misc;
			if (lamp.misc > Constants.OBJ_LAMP_MAX) {
				lamp.misc = Constants.OBJ_LAMP_MAX;
				IO.printMessage ("Your lamp overflows, spilling oil on the ground.");
				IO.printMessage("Your lamp is full.");
			} else if (lamp.misc > Constants.OBJ_LAMP_MAX / 2) {
				IO.printMessage ("Your lamp is more than half full.");
			} else if (lamp.misc == Constants.OBJ_LAMP_MAX / 2) {
				IO.printMessage ("Your lamp is half full.");
			} else {
				IO.printMessage ("Your lamp is less than half full.");
			}
			Desc.describeRemaining(index);
			Misc3.destroyInvenItem(index);
		}
	}
}
