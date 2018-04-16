/*
 * Moria4.java: misc code, mainly to handle player commands
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
import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.MonsterType;

public class Moria4 {
	
	private Moria4() { }
	
	/**
	 * Tunnels through rubble and walls. -RAK-
	 * <p>
	 * Must take into account: secret doors, special tools.
	 * 
	 * @param dir the direction in which to tunnel
	 */
	public static void tunnel(int dir) {
		if ((Player.py.flags.confused > 0) && // Confused?
				(Rnd.randomInt(4) > 1)) { // 75% random movement
			dir = Rnd.randomInt(9);
		}
		
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		Misc3.canMoveDirection(dir, y, x);
		
		CaveType cavePos = Variable.cave[y.value()][x.value()];
		// Compute the digging ability of player; based on
		// strength, and type of tool used
		int tunnellingAbility = Player.py.stats.useStat[Constants.A_STR];
		InvenType item = Treasure.inventory[Constants.INVEN_WIELD];
		
		// Don't let the player tunnel somewhere illegal, this is necessary to
		// prevent the player from getting a free attack by trying to tunnel
		// somewhere where it has no effect.
		if (cavePos.fval < Constants.MIN_CAVE_WALL
				&& (cavePos.treasureIndex == 0
					|| (Treasure.treasureList[cavePos.treasureIndex].category
							!= Constants.TV_RUBBLE
					&& Treasure.treasureList[cavePos.treasureIndex].category
							!= Constants.TV_SECRET_DOOR))) {
			if (cavePos.treasureIndex == 0) {
				IO.printMessage ("Tunnel through what?  Empty air?!?");
				Variable.freeTurnFlag = true;
			} else {
				IO.printMessage("You can't tunnel through that.");
				Variable.freeTurnFlag = true;
			}
			return;
		}
		
		if (cavePos.creatureIndex > 1) {
			MonsterType monster = Monsters.monsterList[cavePos.creatureIndex];
			String monsterName;
			if (monster.monsterLight) {
				monsterName = String.format("The %s", Monsters.creatureList[monster.index].name);
			} else {
				monsterName = "Something";
			}
			String msgBlocked = String.format("%s is in your way!", monsterName);
			IO.printMessage(msgBlocked);
			
			// let the player attack the creature
			if (Player.py.flags.afraid < 1) {
				Moria3.playerAttackMonster(y.value(), x.value());
			} else {
				IO.printMessage("You are too afraid!");
			}
		} else if (item.category == Constants.TV_NOTHING) {
			IO.printMessage("You dig with your hands, making no progress.");
		}
		
		if ((Constants.TR_TUNNEL & item.flags) != 0) {
			tunnellingAbility += 25 + item.misc * 50;
		} else {
			tunnellingAbility += (item.damage[0] * item.damage[1]) + item.tohit + item.plusToDam;
			// divide by two so that digging without shovel isn't too easy
			tunnellingAbility >>= 1;
		}
		
		// If this weapon is too heavy for the player to wield properly, then
		// also make it harder to dig with it.
		if (Variable.isWeaponHeavy) {
			tunnellingAbility += (Player.py.stats.useStat[Constants.A_STR] * 15) - item.weight;
			if (tunnellingAbility < 0) {
				tunnellingAbility = 0;
			}
		}
		
		// Regular walls; Granite, magma intrusion, quartz vein
		// Don't forget the boundary walls, made of titanium (255)
		switch (cavePos.fval) {
		case Constants.GRANITE_WALL:
			int graniteReq = Rnd.randomInt(1200) + 80;
			if (Moria3.tunnelThroughWall(y.value(), x.value(), tunnellingAbility, graniteReq)) {
				IO.printMessage("You have finished the tunnel.");
			} else {
				IO.countMessagePrint("You tunnel into the granite wall.");
			}
			break;
		case Constants.MAGMA_WALL:
			int magmaReq = Rnd.randomInt(600) + 10;
			if (Moria3.tunnelThroughWall(y.value(), x.value(), tunnellingAbility, magmaReq)) {
				IO.printMessage("You have finished the tunnel.");
			} else {
				IO.countMessagePrint("You tunnel into the magma intrusion.");
			}
			break;
		case Constants.QUARTZ_WALL:
			int quartzReq = Rnd.randomInt(400) + 10;
			if (Moria3.tunnelThroughWall(y.value(), x.value(), tunnellingAbility, quartzReq)) {
				IO.printMessage("You have finished the tunnel.");
			} else {
				IO.countMessagePrint("You tunnel into the quartz vein.");
			}
			break;
		case Constants.BOUNDARY_WALL:
			IO.printMessage("This seems to be permanent rock.");
			break;
		default:
			// Is there an object in the way?  (Rubble and secret doors)
			if (cavePos.treasureIndex == 0) {
				//abort();
				return;
			}
			
			// Rubble.
			if (Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_RUBBLE) {
				if (tunnellingAbility > Rnd.randomInt(180)) {
					Moria3.deleteObject(y.value(), x.value());
					IO.printMessage("You have removed the rubble.");
					if (Rnd.randomInt(10) == 1) {
						Misc3.placeObject(y.value(), x.value(), false);
						if (Misc1.testLight(y.value(), x.value())) {
							IO.printMessage("You have found something!");
						}
					}
					Moria1.lightUpSpot(y.value(), x.value());
				} else {
					IO.countMessagePrint("You dig in the rubble.");
				}
			
			// Secret doors.
			} else if (Treasure.treasureList[cavePos.treasureIndex].category
					== Constants.TV_SECRET_DOOR) {
				IO.countMessagePrint("You tunnel into the granite wall.");
				Moria2.search(Player.y, Player.x, Player.py.misc.searchChance);
			} else {
				//abort();
				return;
			}
			
			break;
		}
	}
	
	/**
	 * Disarms a trap. -RAK-
	 */
	public static void disarmTrap() {
		IntPointer dir = new IntPointer();
		if (!Moria1.getDirection("", dir)) {
			return;
		}
		
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		Misc3.canMoveDirection(dir.value(), y, x);
		CaveType cavePos = Variable.cave[y.value()][x.value()];
		boolean noDisarm = false;
		if (cavePos.creatureIndex > 1
				&& cavePos.treasureIndex != 0
				&& (Treasure.treasureList[cavePos.treasureIndex].category
						== Constants.TV_VIS_TRAP
					|| Treasure.treasureList[cavePos.treasureIndex].category
						== Constants.TV_CHEST)) {
			MonsterType monster = Monsters.monsterList[cavePos.creatureIndex];
			String monsterName;
			if (monster.monsterLight) {
				monsterName = String.format("The %s", Monsters.creatureList[monster.index].name);
			} else {
				monsterName = "Something";
			}
			String msgBlocked = String.format("%s is in your way!", monsterName);
			IO.printMessage(msgBlocked);
		} else if (cavePos.treasureIndex != 0) {
			int chance = Player.py.misc.disarmChance + 2 * Misc3.adjustToDisarm()
					+ Misc3.adjustStat(Constants.A_INT)
					+ (Player.classLevelAdjust[Player.py.misc.playerClass][Constants.CLA_DISARM]
							* Player.py.misc.level / 3);
			if ((Player.py.flags.blind > 0) || (Moria1.playerHasNoLight())) {
				chance = chance / 10;
			}
			if (Player.py.flags.confused > 0) {
				chance = chance / 10;
			}
			if (Player.py.flags.imagine > 0) {
				chance = chance / 10;
			}
			
			InvenType item = Treasure.treasureList[cavePos.treasureIndex];
			int itemType = item.category;
			int level = item.level;
			if (itemType == Constants.TV_VIS_TRAP) { // Floor trap
				if ((chance + 100 - level) > Rnd.randomInt(100)) {
					IO.printMessage("You have disarmed the trap.");
					Player.py.misc.currExp += item.misc;
					Moria3.deleteObject(y.value(), x.value());
					// make sure we move onto the trap even if confused
					int tmp = Player.py.flags.confused;
					Player.py.flags.confused = 0;
					Moria3.movePlayer(dir.value(), false);
					Player.py.flags.confused = tmp;
					Misc3.printExperience();
				
				// avoid randint(0) call
				} else if ((chance > 5) && (Rnd.randomInt(chance) > 5)) {
					IO.countMessagePrint("You failed to disarm the trap.");
				} else {
					IO.printMessage("You set the trap off!");
					// make sure we move onto the trap even if confused
					int tmp = Player.py.flags.confused;
					Player.py.flags.confused = 0;
					Moria3.movePlayer(dir.value(), false);
					Player.py.flags.confused += tmp;
				}
			} else if (itemType == Constants.TV_CHEST) {
				if (!Desc.arePlussesKnownByPlayer(item)) {
					IO.printMessage("I don't see a trap.");
					Variable.freeTurnFlag = true;
				} else if ((Constants.CH_TRAPPED & item.flags) != 0) {
					if ((chance - level) > Rnd.randomInt(100)) {
						item.flags &= ~Constants.CH_TRAPPED;
						if ((Constants.CH_LOCKED & item.flags) != 0) {
							item.specialName = Constants.SN_LOCKED;
						} else {
							item.specialName = Constants.SN_DISARMED;
						}
						IO.printMessage("You have disarmed the chest.");
						Desc.identifyItemPlusses(item);
						Player.py.misc.currExp += level;
						Misc3.printExperience();
					} else if ((chance > 5) && (Rnd.randomInt(chance) > 5)) {
						IO.countMessagePrint("You failed to disarm the chest.");
					} else {
						IO.printMessage("You set a trap off!");
						Desc.identifyItemPlusses(item);
						Moria3.chestTrap(y.value(), x.value());
					}
				} else {
					IO.printMessage("The chest was not trapped.");
					Variable.freeTurnFlag = true;
				}
			} else {
				noDisarm = true;
			}
		} else {
			noDisarm = true;
		}
		
		if (noDisarm) {
			IO.printMessage("I do not see anything to disarm there.");
			Variable.freeTurnFlag = true;
		}
	}
	
	/* An enhanced look, with peripheral vision. Looking all 8	-CJS-
	   directions will see everything which ought to be visible. Can
	   specify direction 5, which looks in all directions.
	
	   For the purpose of hindering vision, each place is regarded as
	   a diamond just touching its four immediate neighbours. A
	   diamond is opaque if it is a wall, or shut door, or something
	   like that. A place is visible if any part of its diamond is
	   visible: i.e. there is a line from the view point to part of
	   the diamond which does not pass through any opaque diamonds.
	
	   Consider the following situation:
	
	     @....			    X	X   X	X   X
	     .##..			   / \ / \ / \ / \ / \
	     .....			  X @ X . X . X 1 X . X
					   \ / \ / \ / \ / \ /
					    X	X   X	X   X
		   Expanded view, with	   / \ / \ / \ / \ / \
		   diamonds inscribed	  X . X # X # X 2 X . X
		   about each point,	   \ / \ / \ / \ / \ /
		   and some locations	    X	X   X	X   X
		   numbered.		   / \ / \ / \ / \ / \
					  X . X . X . X 3 X 4 X
					   \ / \ / \ / \ / \ /
					    X	X   X	X   X
		- Location 1 is fully visible.
		- Location 2 is visible, even though partially obscured.
		- Location 3 is invisible, but if either # were
		  transparent, it would be visible.
		- Location 4 is completely obscured by a single #.
	
	   The function which does the work is look_ray. It sets up its
	   own co-ordinate frame (global variables map back to the
	   dungeon frame) and looks for everything between two angles
	   specified from a central line. It is recursive, and each call
	   looks at stuff visible along a line parallel to the center
	   line, and a set distance away from it. A diagonal look uses
	   more extreme peripheral vision from the closest horizontal and
	   vertical directions; horizontal or vertical looks take a call
	   for each side of the central line. */
	
	/* Globally accessed variables: gl_nseen counts the number of places where
	 * something is seen. gl_rock indicates a look for rock or objects.
	 *
	 * The others map co-ords in the ray frame to dungeon co-ords.
	 *
	 * dungeon y = py.char_row	 + gl_fyx * (ray x)  + gl_fyy * (ray y)
	 * dungeon x = py.char_col	 + gl_fxx * (ray x)  + gl_fxy * (ray y) */
	private static int glFxx;
	private static int glFxy;
	private static int glFyx;
	private static int glFyy;
	private static int glNseen;
	private static boolean glNoQuery;
	private static int glRock;
	/* Intended to be indexed by dir/2, since is only relevant to horizontal or
	 * vertical directions. */
	private static int[] setFxy = { 0,  1,  0,  0, -1 };
	private static int[] setFxx = { 0,  0, -1,  1,  0 };
	private static int[] setFyy = { 0,  0,  1, -1,  0 };
	private static int[] setFyx = { 0,  1,  0,  0, -1 };
	/* Map diagonal-dir/2 to a normal-dir/2. */
	private static int[] mapDiag1 = { 1, 3, 0, 2, 4 };
	private static int[] mapDiag2 = { 2, 1, 0, 4, 3 };
	
	private static final int GRADF = 10000;	/* Any sufficiently big number will do */
	
	/* Look at what we can see. This is a free move.
	
	   Prompts for a direction, and then looks at every object in
	   turn within a cone of vision in that direction. For each
	   object, the cursor is moved over the object, a description is
	   given, and we wait for the user to type something. Typing
	   ESCAPE will abort the entire look.
	
	   Looks first at real objects and monsters, and looks at rock
	   types only after all other things have been seen.  Only looks at rock
	   types if the highlight_seams option is set. */
	
	public static void look() {
		if (Player.py.flags.blind > 0) {
			IO.printMessage("You can't see a damn thing!");
			return;
		}
		if (Player.py.flags.imagine > 0) {
			IO.printMessage("You can't believe what you are seeing! It's like a dream!");
			return;
		}
		
		IntPointer dir = new IntPointer();
		if (!Moria1.getAnyDirection("Look which direction?", dir)) {
			return;
		}
		
		boolean abort = false;
		glNseen = 0;
		glRock = 0;
		glNoQuery = false; // Have to set this up for the look_see
		
		BooleanPointer dummy = new BooleanPointer();
		if (lookSee(0, 0, dummy)) {
			abort = true;
			return;
		}
		
		do {
			abort = false;
			if (dir.value() == 5) {
				for (int i = 1; i <= 4; i++) {
					glFxx = setFxx[i];
					glFyx = setFyx[i];
					glFxy = setFxy[i];
					glFyy = setFyy[i];
					if (lookRay(0, 2 * GRADF - 1, 1)) {
						abort = true;
						break;
					}
					glFxy = -glFxy;
					glFyy = -glFyy;
					if (lookRay(0, 2 * GRADF, 2)) {
						abort = true;
						break;
					}
				}
			} else if ((dir.value() & 1) == 0) { // Straight directions
				int i = dir.value() >> 1;
				glFxx = setFxx[i];
				glFyx = setFyx[i];
				glFxy = setFxy[i];
				glFyy = setFyy[i];
				if (lookRay(0, GRADF, 1)) {
					abort = true;
				} else {
					glFxy = -glFxy;
					glFyy = -glFyy;
					abort = lookRay(0, GRADF, 2);
				}
			} else {
				int i = mapDiag1[dir.value() >> 1];
				glFxx = setFxx[i];
				glFyx = setFyx[i];
				glFxy = -setFxy[i];
				glFyy = -setFyy[i];
				if (lookRay(1, 2 * GRADF, GRADF)) {
					abort = true;
				} else {
					i = mapDiag2[dir.value() >> 1];
					glFxx = setFxx[i];
					glFyx = setFyx[i];
					glFxy = setFxy[i];
					glFyy = setFyy[i];
					abort = lookRay(1, 2 * GRADF - 1, GRADF);
				}
			}
		} while (!abort && Variable.highlightSeams.value() && (++glRock < 2));
		
		if (abort) {
			IO.printMessage("--Aborting look--");
			return;
		}
		
		if (glNseen > 0) {
			if (dir.value() == 5) {
				IO.printMessage("That's all you see.");
			} else {
				IO.printMessage("That's all you see in that direction.");
			}
		} else if (dir.value() == 5) {
			IO.printMessage("You see nothing of interest.");
		} else {
			IO.printMessage("You see nothing of interest in that direction.");
		}
	}
	
	/* Look at everything within a cone of vision between two ray
	   lines emanating from the player, and y or more places away
	   from the direct line of view. This is recursive.
	
	   Rays are specified by gradients, y over x, multiplied by
	   2*GRADF. This is ONLY called with gradients between 2*GRADF
	   (45 degrees) and 1 (almost horizontal).
	
	   (y axis)/ angle from
	     ^	  /	    ___ angle to
	     |	 /	 ___
	  ...|../.....___.................... parameter y (look at things in the
	     | /   ___			      cone, and on or above this line)
	     |/ ___
	     @-------------------.   direction in which you are looking. (x axis)
	     |
	     | */
	public static boolean lookRay(int y, int from, int to) {
		// from is the larger angle of the ray, since we scan towards the
	    // center line. If from is smaller, then the ray does not exist.
		if (from <= to || y > Constants.MAX_SIGHT) {
			return false;
		}
		
		// Find first visible location along this line. Minimum x such
	    // that (2x-1)/x < from/GRADF <=> x > GRADF(2x-1)/from. This may
	    // be called with y=0 whence x will be set to 0. Thus we need a
	    // special fix.
		int x = (GRADF * (2 * y - 1) / from + 1);
		if (x <= 0) {
			x = 1;
		}
		
		// Find last visible location along this line.
	    // Maximum x such that (2x+1)/x > to/GRADF <=> x < GRADF(2x+1)/to
		int maxX = ((GRADF * (2 * y + 1) - 1) / to);
		if (maxX > Constants.MAX_SIGHT) {
			maxX = Constants.MAX_SIGHT;
		}
		if (maxX < x) {
			return false;
		}
		
		// gl_noquery is a HACK to prevent doubling up on direct lines of
	    // sight. If 'to' is greater than 1, we do not really look at
	    // stuff along the direct line of sight, but we do have to see
	    // what is opaque for the purposes of obscuring other objects.
		if (y == 0 && to > 1 || y == x && from < GRADF * 2) {
			glNoQuery = true;
		} else {
			glNoQuery = false;
		}
		
		BooleanPointer transparent = new BooleanPointer();
		if (lookSee(x, y, transparent)) {
			return true;
		}
		if (y == x) {
			glNoQuery = false;
		}
		
		boolean jumpToInitTransparent = transparent.value();
		//if (transparent) {
		//	goto init_transparent;
		//}
		
		while (true) {
			if (!jumpToInitTransparent) {
				// Look down the window we've found.
				if (lookRay(y + 1, from, ((2 * y + 1) * GRADF / x))) {
					return true;
				}
				
				// Find the start of next window.
				do {
					if (x == maxX) {
						return false;
					}
					
					// See if this seals off the scan. (If y is zero, then it will.)
					from = ((2 * y - 1) * GRADF / x);
					if (from <= to) {
						return false;
					}
					x++;
					if (lookSee(x, y, transparent)) {
						return true;
					}
				} while (!transparent.value());
			}
			
			//init_transparent:
			jumpToInitTransparent = false;
			// Find the end of this window of visibility.
			do {
				if (x == maxX) {
					// The window is trimmed by an earlier limit.
					return lookRay(y + 1, from, to);
				}
				x++;
				if (lookSee(x, y, transparent)) {
					return true;
				}
			} while (transparent.value());
		}
	}
	
	public static boolean lookSee(int x, int y, BooleanPointer transparent) {
		if (x < 0 || y < 0 || y > x) {
			String msgIllegal = String.format("Illegal call to look_see(%d, %d)", x, y);
			IO.printMessage(msgIllegal);
		}
		
		String msgPrefix;
		if (x == 0 && y == 0) {
			msgPrefix = "You are on";
		} else {
			msgPrefix = "You see";
		}
		
		int tmp = Player.x + glFxx * x + glFxy * y;
		y = Player.y + glFyx * x + glFyy * y;
		x = tmp;
		if (!Misc1.panelContains(y, x)) {
			transparent.value(false);
			return false;
		}
		
		CaveType cavePos = Variable.cave[y][x];
		transparent.value((cavePos.fval <= Constants.MAX_OPEN_SPACE));
		if (glNoQuery) {
			return false; // Don't look at a direct line of sight. A hack.
		}
		
		char query = 0;
		String message = "";
		if (glRock == 0 && cavePos.creatureIndex > 1
				&& Monsters.monsterList[cavePos.creatureIndex].monsterLight) {
			int j = Monsters.monsterList[cavePos.creatureIndex].index;
			message = String.format("%s %s %s. [(r)ecall]",
					msgPrefix,
					Desc.isVowel(Monsters.creatureList[j].name.charAt(0) ) ? "an" : "a",
					Monsters.creatureList[j].name);
			msgPrefix = "It is on";
			IO.print(message, 0, 0);
			IO.moveCursorRelative(y, x);
			query = IO.inkey();
			if (query == 'r' || query == 'R') {
				IO.saveScreen();
				query = Recall.recallMonster(j);
				IO.restoreScreen();
			}
		}
		
		if (cavePos.tempLight || cavePos.permLight || cavePos.fieldMark) {
			boolean jumpToGranite = false;
			if (cavePos.treasureIndex != 0) {
				if (Treasure.treasureList[cavePos.treasureIndex].category
						== Constants.TV_SECRET_DOOR) {
					jumpToGranite = true;
				} else if (glRock == 0
						&& Treasure.treasureList[cavePos.treasureIndex].category
							!= Constants.TV_INVIS_TRAP) {
					String itemDesc = Desc.describeObject(
							Treasure.treasureList[cavePos.treasureIndex],
							true);
					message = String.format("%s %s ---pause---", msgPrefix, itemDesc);
					msgPrefix = "It is in";
					IO.print(message, 0, 0);
					IO.moveCursorRelative(y, x);
					query = IO.inkey();
				}
			}
			
			if (jumpToGranite ||
					((glRock != 0 || !message.equals(""))
							&& cavePos.fval >= Constants.MIN_CLOSED_SPACE)) {
				String msgObject;
				if (!jumpToGranite) {
					switch (cavePos.fval) {
					case Constants.BOUNDARY_WALL:
					case Constants.GRANITE_WALL:
						// Granite is only interesting if it contains something.
						if (!message.isEmpty()) {
							msgObject = "a granite wall";
						} else {
							msgObject = ""; // In case we jump here
						}
						break;
					case Constants.MAGMA_WALL:
						msgObject = "some dark rock";
						break;
					case Constants.QUARTZ_WALL:
						msgObject = "a quartz vein";
						break;
					default:
						msgObject = "";
						break;
					}
				} else {
					if (!message.isEmpty()) {
						msgObject = "a granite wall";
					} else {
						msgObject = "";
					}
				}
				
				if (!msgObject.isEmpty()) {
					message = String.format("%s %s ---pause---", msgPrefix, msgObject);
					IO.print(message, 0, 0);
					IO.moveCursorRelative(y, x);
					query = IO.inkey();
				}
			}
		}
		
		if (!message.isEmpty()) {
			glNseen++;
			if (query == Constants.ESCAPE) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Throw an item.
	 * 
	 * @param itemIndex Index in the inventory of the item to throw
	 * @return An instance of the thrown object
	 */
	public static InvenType throwItem(int itemIndex) {
		InvenType item = Treasure.inventory[itemIndex];
		InvenType objectToThrow = new InvenType();
		item.copyInto(objectToThrow);
		
		if (item.number > 1) {
			objectToThrow.number = 1;
			item.number--;
			Treasure.invenWeight -= item.weight;
			Player.py.flags.status |= Constants.PY_STR_WGT;
		} else {
			Misc3.destroyInvenItem(itemIndex);
		}
		
		return objectToThrow;
	}
	
	/**
	 * Obtain the hit and damage bonuses and the maximum distance for a
	 * thrown missile.
	 * 
	 * @param itemToThrow The item being thrown
	 * @param totalBaseToHitBow
	 * @param totalPlusToHit
	 * @param totalDamage
	 * @param totalDistance
	 */
	public static void throwBonuses(InvenType itemToThrow, IntPointer totalBaseToHitBow,
			IntPointer totalPlusToHit, IntPointer totalDamage, IntPointer totalDistance) {
		int itemWeight;
		if (itemToThrow.weight < 1) {
			itemWeight = 1;
		} else {
			itemWeight = itemToThrow.weight;
		}
		
		// Throwing objects
		totalDamage.value(Misc1.pDamageRoll(itemToThrow.damage) + itemToThrow.plusToDam);
		totalBaseToHitBow.value(Player.py.misc.baseToHitBow * 75 / 100);
		totalPlusToHit.value(Player.py.misc.plusToHit + itemToThrow.tohit);
		
		// Add this back later if the correct throwing device. -CJS-
		if (Treasure.inventory[Constants.INVEN_WIELD].category != Constants.TV_NOTHING) {
			totalPlusToHit.value(totalPlusToHit.value()
					- Treasure.inventory[Constants.INVEN_WIELD].tohit);
		}
		
		totalDistance.value((((Player.py.stats.useStat[Constants.A_STR] + 20) * 10) / itemWeight));
		if (totalDistance.value() > 10) {
			totalDistance.value(10);
		}
		
		// multiply damage bonuses instead of adding, when have proper
		// missile/weapon combo, this makes them much more useful
		
		if (Treasure.inventory[Constants.INVEN_WIELD].category != Constants.TV_BOW) {
			return;
		}
		
		// Using Bows, slings, or crossbows
		switch (Treasure.inventory[Constants.INVEN_WIELD].misc) {
		case 1:
			if (itemToThrow.category == Constants.TV_SLING_AMMO) { // Sling and ammo
				totalBaseToHitBow.value(Player.py.misc.baseToHitBow);
				totalPlusToHit.value(totalPlusToHit.value()
						+ 2 * Treasure.inventory[Constants.INVEN_WIELD].tohit);
				totalDamage.value(totalDamage.value()
						+ Treasure.inventory[Constants.INVEN_WIELD].plusToDam);
				totalDamage.value(totalDamage.value() * 2);
				totalDistance.value(20);
			}
			break;
		case 2:
			if (itemToThrow.category == Constants.TV_ARROW) { // Short Bow and Arrow
				totalBaseToHitBow.value(Player.py.misc.baseToHitBow);
				totalPlusToHit.value(totalPlusToHit.value()
						+ 2 * Treasure.inventory[Constants.INVEN_WIELD].tohit);
				totalDamage.value(totalDamage.value()
						+ Treasure.inventory[Constants.INVEN_WIELD].plusToDam);
				totalDamage.value(totalDamage.value() * 2);
				totalDistance.value(25);
			}
			break;
		case 3:
			if (itemToThrow.category == Constants.TV_ARROW) { // Long Bow and Arrow
				totalBaseToHitBow.value(Player.py.misc.baseToHitBow);
				totalPlusToHit.value(totalPlusToHit.value()
						+ 2 * Treasure.inventory[Constants.INVEN_WIELD].tohit);
				totalDamage.value(totalDamage.value()
						+ Treasure.inventory[Constants.INVEN_WIELD].plusToDam);
				totalDamage.value(totalDamage.value() * 3);
				totalDistance.value(30);
			}
			break;
		case 4:
			if (itemToThrow.category == Constants.TV_ARROW) { // Composite Bow and Arrow
				totalBaseToHitBow.value(Player.py.misc.baseToHitBow);
				totalPlusToHit.value(totalPlusToHit.value()
						+ 2 * Treasure.inventory[Constants.INVEN_WIELD].tohit);
				totalDamage.value(totalDamage.value()
						+ Treasure.inventory[Constants.INVEN_WIELD].plusToDam);
				totalDamage.value(totalDamage.value() * 4);
				totalDistance.value(35);
			}
			break;
		case 5:
			if (itemToThrow.category == Constants.TV_BOLT) { // Light Crossbow and Bolt
				totalBaseToHitBow.value(Player.py.misc.baseToHitBow);
				totalPlusToHit.value(totalPlusToHit.value()
						+ 2 * Treasure.inventory[Constants.INVEN_WIELD].tohit);
				totalDamage.value(totalDamage.value()
						+ Treasure.inventory[Constants.INVEN_WIELD].plusToDam);
				totalDamage.value(totalDamage.value() * 3);
				totalDistance.value(25);
			}
			break;
		case 6:
			if (itemToThrow.category == Constants.TV_BOLT) { // Heavy Crossbow and Bolt
				totalBaseToHitBow.value(Player.py.misc.baseToHitBow);
				totalPlusToHit.value(totalPlusToHit.value()
						+ 2 * Treasure.inventory[Constants.INVEN_WIELD].tohit);
				totalDamage.value(totalDamage.value()
						+ Treasure.inventory[Constants.INVEN_WIELD].plusToDam);
				totalDamage.value(totalDamage.value() * 4);
				totalDistance.value(35);
			}
			break;
		default:
			break;
		}
	}
	
	public static void dropThrow(int y, int x, InvenType item) {
		if (Rnd.randomInt(10) > 1) {
			int newY = y;
			int newX = x;
			
			for (int k = 0; k < 10; k++) {
				if (Misc1.isInBounds(newY, newX)) {
					CaveType cavePos = Variable.cave[newY][newX];
					if (cavePos.fval <= Constants.MAX_OPEN_SPACE && cavePos.treasureIndex == 0) {
						int treasureIndex = Misc1.popTreasure();
						Variable.cave[newY][newX].treasureIndex = treasureIndex;
						item.copyInto(Treasure.treasureList[treasureIndex]);
						Moria1.lightUpSpot(newY, newX);
						return;
					}
				}
				
				newY = y + Rnd.randomInt(3) - 2;
				newX = x + Rnd.randomInt(3) - 2;
			}
		}
		
		IO.printMessage(String.format("The %s disappears.", Desc.describeObject(item, false)));
	}
	
	/**
	 * Throw an object across the dungeon. -RAK-
	 * <p>Note: Flasks of oil do fire damage
	 * <p>Note: Extra damage and chance of hitting when missiles are used
	 *	 with correct weapon.  I.E.  wield bow and throw arrow.
	 */
	public static void throwObject() {
		if (Treasure.invenCounter == 0) {
			IO.printMessage("But you are not carrying anything.");
			Variable.freeTurnFlag = true;
			return;
		}
		
		IntPointer itemVal = new IntPointer();
		if (!Moria1.getItemId(itemVal, "Fire/Throw which one?", 0, Treasure.invenCounter - 1, null, "")) {
			return;
		}
		
		IntPointer dir = new IntPointer();
		if (!Moria1.getDirection("", dir)) {
			return;
		}
		
		Desc.describeRemaining(itemVal.value());
		if (Player.py.flags.confused > 0) {
			IO.printMessage("You are confused.");
			do {
				dir.value(Rnd.randomInt(9));
			} while (dir.value() == 5);
		}
		
		IntPointer totalBaseToHitBow = new IntPointer();
		IntPointer totalPlusToHit = new IntPointer();
		IntPointer totalDamage = new IntPointer();
		IntPointer totalDistance = new IntPointer();
		
		InvenType objectToThrow = throwItem(itemVal.value());
		throwBonuses(objectToThrow, totalBaseToHitBow, totalPlusToHit, totalDamage, totalDistance);
		char tchar = objectToThrow.tchar;
		boolean objectIsTravelling = true;
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		int oldY = Player.y;
		int oldX = Player.x;
		int curDistance = 0;
		
		while (objectIsTravelling) {
			Misc3.canMoveDirection(dir.value(), y, x);
			curDistance++;
			Moria1.lightUpSpot(oldY, oldX);
			if (curDistance > totalDistance.value()) {
				objectIsTravelling = false;
			}
			
			CaveType cavePos = Variable.cave[y.value()][x.value()];
			if (cavePos.fval <= Constants.MAX_OPEN_SPACE && objectIsTravelling) {
				if (cavePos.creatureIndex > 1) {
					objectIsTravelling = false;
					MonsterType monster = Monsters.monsterList[cavePos.creatureIndex];
					totalBaseToHitBow.value(totalBaseToHitBow.value() - curDistance);
					
					// if monster not lit, make it much more difficult to
					// hit, subtract off most bonuses, and reduce bthb
					// depending on distance
					if (!monster.monsterLight) {
						totalBaseToHitBow.value((totalBaseToHitBow.value() / (curDistance + 2))
								- (Player.py.misc.level * Player.classLevelAdjust[Player.py.misc.playerClass][Constants.CLA_BTHB] / 2)
								- (totalPlusToHit.value() * (Constants.BTH_PLUS_ADJ - 1)));
					}
					
					if (Moria1.testHit(totalBaseToHitBow.value(), Player.py.misc.level, totalPlusToHit.value(),
							Monsters.creatureList[monster.index].armorClass, Constants.CLA_BTHB)) {
						int monsterIndex = monster.index;
						String itemDesc = Desc.describeObject(objectToThrow, false);
						
						// Does the player know what he's fighting?
						boolean visible = monster.monsterLight;
						if (visible) {
							IO.printMessage(String.format("The %s hits the %s.",
									itemDesc, Monsters.creatureList[monsterIndex].name));
						} else {
							IO.printMessage(String.format("You hear a cry as the %s finds a mark.",
									itemDesc));
						}
						
						totalDamage.value(Misc3.totalDamage(objectToThrow, totalDamage.value(), monsterIndex));
						totalDamage.value(Misc3.criticalBlow(objectToThrow.weight,
								totalPlusToHit.value(), totalDamage.value(), Constants.CLA_BTHB));
						if (totalDamage.value() < 0) {
							totalDamage.value(0);
						}
						
						monsterIndex = Moria3.monsterTakeHit(cavePos.creatureIndex, totalDamage.value());
						if (monsterIndex >= 0) {
							if (!visible) {
								IO.printMessage("You have killed something!");
							} else {
								IO.printMessage(String.format("You have killed the %s.",
										Monsters.creatureList[monsterIndex].name));
							}
							Misc3.printExperience();
						}
					} else {
						dropThrow(oldY, oldX, objectToThrow);
					}
				} else { // do not test c_ptr.fm here
					if (Misc1.panelContains(y.value(), x.value())
							&& Player.py.flags.blind < 1
							&& (cavePos.tempLight || cavePos.permLight)) {
						IO.print(tchar, y.value(), x.value());
						IO.putQio(); // show object moving
					}
				}
			} else {
				objectIsTravelling = false;
				dropThrow(oldY, oldX, objectToThrow);
			}
			
			oldY = y.value();
			oldX = x.value();
		}
	}
	
	/**
	 * Make a bash attack on someone. -CJS-
	 * <p>
	 * Used to be part of bash above.
	 * 
	 * @param y y-position of the target
	 * @param x x-position of the target
	 */
	public static void playerBash(int y, int x) {
		int monsterIndex = Variable.cave[y][x].creatureIndex;
		MonsterType monster = Monsters.monsterList[monsterIndex];
		CreatureType creature = Monsters.creatureList[monster.index];
		monster.sleep = 0;
		
		// Does the player know what he's fighting?
		String monsterName;
		if (!monster.monsterLight) {
			monsterName = "it";
		} else {
			monsterName = String.format("the %s", creature.name);
		}
		
		int baseToHit = Player.py.stats.useStat[Constants.A_STR]
				+ Treasure.inventory[Constants.INVEN_ARM].weight / 2
				+ Player.py.misc.weight / 10;
		if (!monster.monsterLight) {
			baseToHit = (baseToHit / 2)
					- (Player.py.stats.useStat[Constants.A_DEX] * (Constants.BTH_PLUS_ADJ - 1))
					- (Player.py.misc.level * Player.classLevelAdjust[Player.py.misc.playerClass][Constants.CLA_BTH] / 2);
		}
		
		if (Moria1.testHit(baseToHit, Player.py.misc.level,
				Player.py.stats.useStat[Constants.A_DEX],
				creature.armorClass, Constants.CLA_BTH)) {
			IO.printMessage(String.format("You hit %s.", monsterName));
			int damage = Misc1.pDamageRoll(Treasure.inventory[Constants.INVEN_ARM].damage);
			damage = Misc3.criticalBlow((Treasure.inventory[Constants.INVEN_ARM].weight / 4
					+ Player.py.stats.useStat[Constants.A_STR]),
					0, damage, Constants.CLA_BTH);
			damage += Player.py.misc.weight / 60 + 3;
			if (damage < 0) {
				damage = 0;
			}
			
			// See if we done it in.
			if (Moria3.monsterTakeHit(monsterIndex, damage) >= 0) {
				IO.printMessage(String.format("You have slain %s.", monsterName));
				Misc3.printExperience();
			} else {
				monsterName = Character.toUpperCase(monsterName.charAt(0)) + monsterName.substring(1); // Capitalize
				
				// Can not stun Balrog
				int avgMaxHp = (((creature.cdefense & Constants.CD_MAX_HP) != 0)
						? creature.hitDie[0] * creature.hitDie[1]
								: (creature.hitDie[0] * (creature.hitDie[1] + 1)) >> 1);
				if ((100 + Rnd.randomInt(400) + Rnd.randomInt(400)) > (monster.hitpoints + avgMaxHp)) {
					monster.stunned += Rnd.randomInt(3) + 1;
					if (monster.stunned > 24) {
						monster.stunned = 24;
					}
					IO.printMessage(String.format("%s appears stunned!", monsterName));
				} else {
					IO.printMessage(String.format("%s ignores your bash!", monsterName));
				}
			}
		} else {
			IO.printMessage(String.format("You miss %s.", monsterName));
		}
		
		if (Rnd.randomInt(150) > Player.py.stats.useStat[Constants.A_DEX]) {
			IO.printMessage("You are off balance.");
			Player.py.flags.paralysis = 1 + Rnd.randomInt(2);
		}
	}
	
	/**
	 * Bash open a door or chest. -RAK-
	 * <p>
	 * Note: Affected by strength and weight of character
	 * <p>
	 * For a closed door, p1 is positive if locked; negative if
	 * stuck. A disarm spell unlocks and unjams doors!
	 * <p>
	 * For an open door, p1 is positive for a broken door.
	 * <p>
	 * A closed door can be opened - harder if locked. Any door might
	 * be bashed open (and thereby broken). Bashing a door is
	 * (potentially) faster! You move into the door way. To open a
	 * stuck door, it must be bashed. A closed door can be jammed
	 * (which makes it stuck if previously locked).
	 * <p>
	 * Creatures can also open doors. A creature with open door
	 * ability will (if not in the line of sight) move though a
	 * closed or secret door with no changes. If in the line of
	 * sight, closed door are opened, & secret door revealed.
	 * Whether in the line of sight or not, such a creature may
	 * unlock or unstick a door.
	 * <p>
	 * A creature with no such ability will attempt to bash a
	 * non-secret door.
	 */
	public static void bash() {
		IntPointer dir = new IntPointer();
		if (!Moria1.getDirection("", dir)) {
			return;
		}
		
		if (Player.py.flags.confused > 0) {
			IO.printMessage("You are confused.");
			do {
				dir.value(Rnd.randomInt(9));
			} while (dir.value() == 5);
		}
		
		IntPointer y = new IntPointer(Player.y);
		IntPointer x = new IntPointer(Player.x);
		Misc3.canMoveDirection(dir.value(), y, x);
		CaveType cavePos = Variable.cave[y.value()][x.value()];
		if (cavePos.creatureIndex > 1) {
			if (Player.py.flags.afraid > 0) {
				IO.printMessage("You are afraid!");
			} else {
				playerBash(y.value(), x.value());
			}
		} else if (cavePos.treasureIndex != 0) {
			InvenType object = Treasure.treasureList[cavePos.treasureIndex];
			if (object.category == Constants.TV_CLOSED_DOOR) {
				IO.countMessagePrint("You smash into the door!");
				int force = Player.py.stats.useStat[Constants.A_STR] + Player.py.misc.weight / 2;
				
				// Use (roughly) similar method as for monsters.
				if (Rnd.randomInt(force * (20 + Math.abs(object.misc))) < 10 * (force - Math.abs(object.misc))) {
					IO.printMessage("The door crashes open!");
					Desc.copyIntoInventory(Treasure.treasureList[cavePos.treasureIndex], Constants.OBJ_OPEN_DOOR);
					object.misc = 1 - Rnd.randomInt(2); // 50% chance of breaking door
					cavePos.fval = Constants.CORR_FLOOR;
					
					if (Player.py.flags.confused == 0) {
						Moria3.movePlayer(dir.value(), false);
					} else {
						Moria1.lightUpSpot(y.value(), x.value());
					}
				} else if (Rnd.randomInt(150) > Player.py.stats.useStat[Constants.A_DEX]) {
					IO.printMessage("You are off-balance.");
					Player.py.flags.paralysis = 1 + Rnd.randomInt(2);
				} else if (Variable.commandCount == 0) {
					IO.printMessage("The door holds firm.");
				}
			} else if (object.category == Constants.TV_CHEST) {
				if (Rnd.randomInt(10) == 1) {
					IO.printMessage("You have destroyed the chest.");
					IO.printMessage("and its contents!");
					object.index = Constants.OBJ_RUINED_CHEST;
					object.flags = 0;
				} else if ((Constants.CH_LOCKED & object.flags) != 0 && (Rnd.randomInt(10) == 1)) {
					IO.printMessage("The lock breaks open!");
					object.flags &= ~Constants.CH_LOCKED;
				} else {
					IO.countMessagePrint("The chest holds firm.");
				}
			} else { 
				// Can't give free turn, or else player could try directions
				// until he found invisible creature
				IO.printMessage("You bash it, but nothing interesting happens.");
			}
		} else {
			if (cavePos.fval < Constants.MIN_CAVE_WALL) {
				IO.printMessage("You bash at empty space.");
			} else {
				// same message for wall as for secret door
				IO.printMessage("You bash it, but nothing interesting happens.");
			}
		}
	}
}
