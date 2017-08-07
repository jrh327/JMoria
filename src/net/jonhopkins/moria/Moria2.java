/* 
 * Moria2.java: misc code, mainly handles player movement, t.inventory, etc
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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;

public class Moria2 {
	
	private Moria2() { }
	
	/**
	 * Change a trap from invisible to visible
	 * Note: Secret doors are handled here -RAK-
	 * 
	 * @param y
	 * @param x
	 */
	public static void revealTrap(int y, int x) {
		CaveType cavePos = Variable.cave[y][x];
		InvenType item = Treasure.treasureList[cavePos.treasureIndex];
		if (item.category == Constants.TV_INVIS_TRAP) {
			item.category = Constants.TV_VIS_TRAP;
			Moria1.lightUpSpot(y, x);
		} else if (item.category == Constants.TV_SECRET_DOOR) {
			// change secret door to closed door
			item.index = Constants.OBJ_CLOSED_DOOR;
			item.category = Treasure.objectList[Constants.OBJ_CLOSED_DOOR].category;
			item.tchar = Treasure.objectList[Constants.OBJ_CLOSED_DOOR].tchar;
			Moria1.lightUpSpot(y, x);
		}
	}
	
	/**
	 * Searches for hidden things. -RAK-
	 * 
	 * @param y
	 * @param x
	 * @param chance
	 */
	public static void search(int y, int x, int chance) {
		PlayerFlags flags = Player.py.flags;
		if (flags.confused > 0) {
			chance = chance / 10;
		}
		if ((flags.blind > 0) || Moria1.playerHasNoLight()) {
			chance = chance / 10;
		}
		if (flags.imagine > 0) {
			chance = chance / 10;
		}
		
		for (int i = (y - 1); i <= (y + 1); i++) {
			for (int j = (x - 1); j <= (x + 1); j++) {
				if (Rnd.randomInt(100) < chance) { // always in_bounds here
					CaveType cavePos = Variable.cave[i][j];
					// Search for hidden objects
					if (cavePos.treasureIndex != 0) {
						InvenType item = Treasure.treasureList[cavePos.treasureIndex];
						// Trap on floor?
						if (item.category == Constants.TV_INVIS_TRAP) {
							String itemDesc = Desc.describeObject(item, true);
							String msgFound = String.format("You have found %s", itemDesc);
							IO.printMessage(msgFound);
							revealTrap(i, j);
							endFind();
						
						// Secret door?
						} else if (item.category == Constants.TV_SECRET_DOOR) {
							IO.printMessage("You have found a secret door.");
							revealTrap(i, j);
							endFind();
						
						// Chest is trapped?
						} else if (item.category == Constants.TV_CHEST) {
							// mask out the treasure bits
							if ((item.flags & Constants.CH_TRAPPED) > 1) {
								if (!Desc.arePlussesKnownByPlayer(item)) {
									Desc.identifyItemPlusses(item);
									IO.printMessage("You have discovered a trap on the chest!");
								} else {
									IO.printMessage("The chest is trapped!");
								}
							}
						}
					}
				}
			}
		}
	}
	
	/* The running algorithm:			-CJS-
	
	   Overview: You keep moving until something interesting happens.
	   If you are in an enclosed space, you follow corners. This is
	   the usual corridor scheme. If you are in an open space, you go
	   straight, but stop before entering enclosed space. This is
	   analogous to reaching doorways. If you have enclosed space on
	   one side only (that is, running along side a wall) stop if
	   your wall opens out, or your open space closes in. Either case
	   corresponds to a doorway.
	
	   What happens depends on what you can really SEE. (i.e. if you
	   have no light, then running along a dark corridor is JUST like
	   running in a dark room.) The algorithm works equally well in
	   corridors, rooms, mine tailings, earthquake rubble, etc, etc.
	
	   These conditions are kept in static memory:
		find_openarea	 You are in the open on at least one side.
		find_breakleft	 You have a wall on the left, and will stop if it opens
		find_breakright	 You have a wall on the right, and will stop if it opens
	
	   To initialize these conditions is the task of find_init. If
	   moving from the square marked @ to the square marked . (in the
	   two diagrams below), then two adjacent sqares on the left and
	   the right (L and R) are considered. If either one is seen to
	   be closed, then that side is considered to be closed. If both
	   sides are closed, then it is an enclosed (corridor) run.
	
		 LL		L
		@.	       L.R
		 RR	       @R
	
	   Looking at more than just the immediate squares is
	   significant. Consider the following case. A run along the
	   corridor will stop just before entering the center point,
	   because a choice is clearly established. Running in any of
	   three available directions will be defined as a corridor run.
	   Note that a minor hack is inserted to make the angled corridor
	   entry (with one side blocked near and the other side blocked
	   further away from the runner) work correctly. The runner moves
	   diagonally, but then saves the previous direction as being
	   straight into the gap. Otherwise, the tail end of the other
	   entry would be perceived as an alternative on the next move.
	
		   #.#
		  ##.##
		  .@...
		  ##.##
		   #.#
	
	   Likewise, a run along a wall, and then into a doorway (two
	   runs) will work correctly. A single run rightwards from @ will
	   stop at 1. Another run right and down will enter the corridor
	   and make the corner, stopping at the 2.
	
		#@	  1
		########### ######
		2	    #
		#############
		#
	
	   After any move, the function area_affect is called to
	   determine the new surroundings, and the direction of
	   subsequent moves. It takes a location (at which the runner has
	   just arrived) and the previous direction (from which the
	   runner is considered to have come). Moving one square in some
	   direction places you adjacent to three or five new squares
	   (for straight and diagonal moves) to which you were not
	   previously adjacent.
	
	       ...!	  ...	       EG Moving from 1 to 2.
	       .12!	  .1.!		  . means previously adjacent
	       ...!	  ..2!		  ! means newly adjacent
			   !!!
	
	   You STOP if you can't even make the move in the chosen
	   direction. You STOP if any of the new squares are interesting
	   in any way: usually containing monsters or treasure. You STOP
	   if any of the newly adjacent squares seem to be open, and you
	   are also looking for a break on that side. (i.e. find_openarea
	   AND find_break) You STOP if any of the newly adjacent squares
	   do NOT seem to be open and you are in an open area, and that
	   side was previously entirely open.
	
	   Corners: If you are not in the open (i.e. you are in a
	   corridor) and there is only one way to go in the new squares,
	   then turn in that direction. If there are more than two new
	   ways to go, STOP. If there are two ways to go, and those ways
	   are separated by a square which does not seem to be open, then
	   STOP.
	
	   Otherwise, we have a potential corner. There are two new open
	   squares, which are also adjacent. One of the new squares is
	   diagonally located, the other is straight on (as in the
	   diagram). We consider two more squares further out (marked
	   below as ?).
		  .X
		 @.?
		  #?
	   If they are both seen to be closed, then it is seen that no
	   benefit is gained from moving straight. It is a known corner.
	   To cut the corner, go diagonally, otherwise go straight, but
	   pretend you stepped diagonally into that next location for a
	   full view next time. Conversely, if one of the ? squares is
	   not seen to be closed, then there is a potential choice. We check
	   to see whether it is a potential corner or an intersection/room entrance.
	   If the square two spaces straight ahead, and the space marked with 'X'
	   are both blank, then it is a potential corner and enter if find_examine
	   is set, otherwise must stop because it is not a corner. */
	
	/* The cycle lists the directions in anticlockwise order, for	-CJS-
	 * over two complete cycles. The chome array maps a direction on
	 * to its position in the cycle.
	 */
	private static int[] cycle = new int[] { 1, 2, 3, 6, 9, 8, 7, 4, 1, 2, 3, 6, 9, 8, 7, 4, 1 };
	private static int[] chome =  new int[] { -1, 8, 9, 10, 7, -1, 11, 6, 5, 4 };
	private static boolean findOpenArea;
	private static boolean findBreakRight;
	private static boolean findBreakLeft;
	private static int findDirection;
	private static int findPrevDir; // Keep a record of which way we are going.
	
	public static void findInit(int dir) {
		IntPointer row = new IntPointer(Player.y);
		IntPointer col = new IntPointer(Player.x);
		
		if (!Misc3.canMoveDirection(dir, row, col)) {
			Variable.findFlag = 0;
		} else {
			findDirection = dir;
			Variable.findFlag = 1;
			findBreakRight = false;
			findBreakLeft = false;
			findPrevDir = dir;
			if (Player.py.flags.blind < 1) {
				int i = chome[dir];
				boolean deepleft = false;
				boolean deepright = false;
				boolean shortright = false;
				boolean shortleft = false;
				if (canSeeWall(cycle[i + 1], Player.y, Player.x)) {
					findBreakLeft = true;
					shortleft = true;
				} else if (canSeeWall(cycle[i + 1], row.value(), col.value())) {
					findBreakLeft = true;
					deepleft = true;
				}
				if (canSeeWall(cycle[i - 1], Player.y, Player.x)) {
					findBreakRight = true;
					shortright = true;
				} else if (canSeeWall(cycle[i - 1], row.value(), col.value())) {
					findBreakRight = true;
					deepright = true;
				}
				if (findBreakLeft && findBreakRight) {
					findOpenArea = false;
					if ((dir & 1) != 0) { // a hack to allow angled corridor entry
						if (deepleft && !deepright) {
							findPrevDir = cycle[i - 1];
						} else if (deepright && !deepleft) {
							findPrevDir = cycle[i + 1];
						}
					
					// else if there is a wall two spaces ahead and seem to be in a
					// corridor, then force a turn into the side corridor, must
					// be moving straight into a corridor here
					} else if (canSeeWall(cycle[i], row.value(), col.value())) {
						if (shortleft && !shortright) {
							findPrevDir = cycle[i - 2];
						} else if (shortright && !shortleft) {
							findPrevDir = cycle[i + 2];
						}
					}
				} else {
					findOpenArea = true;
				}
			}
		}
		
		/* We must erase the player symbol '@' here, because sub3_move_light()
		 * does not erase the previous location of the player when in find mode
		 * and when find_prself is false.  The player symbol is not draw at all
		 * in this case while moving, so the only problem is on the first turn
		 * of find mode, when the initial position of the character must be erased.
		 * Hence we must do the erasure here.  */
		if (! Variable.lightFlag && ! Variable.findPrself.value()) {
			IO.print(Misc1.locateSymbol(Player.y, Player.x), Player.y, Player.x);
		}
		
		Moria3.movePlayer(dir, true);
		if (Variable.findFlag == 0) {
			Variable.commandCount = 0;
		}
	}
	
	public static void findRun() {
		// prevent infinite loops in find mode, will stop after moving 100 times
		if (Variable.findFlag++ > 100) {
			IO.printMessage("You stop running to catch your breath.");
			endFind();
		} else {
			Moria3.movePlayer(findDirection, true);
		}
	}
	
	/**
	 * Switch off the run flag - and get the light correct. -CJS-
	 */
	public static void endFind() {
		if (Variable.findFlag > 0) {
			Variable.findFlag = 0;
			Moria1.moveLight(Player.y, Player.x, Player.y, Player.x);
		}
	}
	
	/**
	 * Do we see a wall? Used in running. -CJS-
	 * 
	 * @param dir
	 * @param y
	 * @param x
	 * @return
	 */
	public static boolean canSeeWall(int dir, int y, int x) {
		IntPointer y1 = new IntPointer(y);
		IntPointer x1 = new IntPointer(x);
		
		// check to see if movement there possible
		if (!Misc3.canMoveDirection(dir, y1, x1)) {
			return true;
		}
		
		char c = Misc1.locateSymbol(y1.value(), x1.value());
		if (c == Variable.wallSymbol || c == '%') {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Do we see anything? Used in running. -CJS-
	 * 
	 * @param dir
	 * @param y
	 * @param x
	 * @return
	 */
	public static boolean canSeeNothing(int dir, int y, int x) {
		IntPointer y1 = new IntPointer(y);
		IntPointer x1 = new IntPointer(x);
		
		// check to see if movement there possible
		if (!Misc3.canMoveDirection(dir, y1, x1)) {
			return false;
		} else if (Misc1.locateSymbol(y1.value(), x1.value()) == ' ') {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Determine the next direction for a run, or if we should stop. -CJS-
	 * 
	 * @param dir
	 * @param y
	 * @param x
	 */
	public static void areaAffect(int dir, int y, int x) {
		if (Player.py.flags.blind >= 1) {
			return;
		}
		
		int option = 0;
		int option2 = 0;
		int dirToCheck = 0;
		dir = findPrevDir; // why is dir even a parameter?
		int max = (dir & 1) + 1;
		// Look at every newly adjacent square.
		for (int i = -max; i <= max; i++) {
			int newdir = cycle[chome[dir] + i];
			IntPointer row = new IntPointer(y);
			IntPointer col = new IntPointer(x);
			if (Misc3.canMoveDirection(newdir, row, col)) {
				// Objects player can see (Including doors?) cause a stop.
				CaveType cavePos = Variable.cave[row.value()][col.value()];
				boolean invisibleSquare;
				if (Variable.playerLight
						|| cavePos.tempLight
						|| cavePos.permLight
						|| cavePos.fieldMark) {
					if (cavePos.treasureIndex != 0) {
						int t1 = Treasure.treasureList[cavePos.treasureIndex].category;
						if (t1 != Constants.TV_INVIS_TRAP
								&& t1 != Constants.TV_SECRET_DOOR
								&& (t1 != Constants.TV_OPEN_DOOR
									|| !Variable.findIgnoreDoors.value())) {
							endFind();
							return;
						}
					}
					// Also Creatures
					// the monster should be visible since update_mon() checks
					// for the special case of being in find mode
					if (cavePos.creatureIndex > 1
							&& Monsters.monsterList[cavePos.creatureIndex].monsterLight) {
						endFind();
						return;
					}
					invisibleSquare = false;
				} else {
					invisibleSquare = true;	// Square unseen. Treat as open.
				}
				
				if (cavePos.fval <= Constants.MAX_OPEN_SPACE || invisibleSquare) {
					if (findOpenArea) {
						// Have we found a break?
						if (i < 0) {
							if (findBreakRight) {
								endFind();
								return;
							}
						} else if (i > 0) {
							if (findBreakLeft) {
								endFind();
								return;
							}
						}
					} else if (option == 0) {
						option = newdir; // The first new direction.
					} else if (option2 != 0) {
						endFind(); // Three new directions. STOP.
						return;
					} else if (option != cycle[chome[dir] + i - 1]) {
						endFind(); // If not adjacent to prev, STOP
						return;
					} else {
						// Two adjacent choices. Make option2 the diagonal,
						// and remember the other diagonal adjacent to the first
						// option.
						if ((newdir & 1) == 1) {
							dirToCheck = cycle[chome[dir] + i - 2];
							option2 = newdir;
						} else {
							dirToCheck = cycle[chome[dir] + i + 1];
							option2 = option;
							option = newdir;
						}
					}
				} else if (findOpenArea) {
					// We see an obstacle. In open area, STOP if on a side
					// previously open.
					if (i < 0) {
						if (findBreakLeft) {
							endFind();
							return;
						}
						findBreakRight = true;
					} else if (i > 0) {
						if (findBreakRight) {
							endFind();
							return;
						}
						findBreakLeft = true;
					}
				}
			}
		}
		
		if (!findOpenArea) { // choose a direction.
			if (option2 == 0 || (Variable.findExamine.value() && !Variable.findCut.value())) {
				// There is only one option, or if two, then we always examine
				// potential corners and never cur known corners, so you step
				// into the straight option.
				if (option != 0) {
					findDirection = option;
				}
				if (option2 == 0) {
					findPrevDir = option;
				} else {
					findPrevDir = option2;
				}
			} else {
				// Two options!
				IntPointer row = new IntPointer(y);
				IntPointer col = new IntPointer(x);
				Misc3.canMoveDirection(option, row, col);
				if (!canSeeWall(option, row.value(), col.value())
						|| !canSeeWall(dirToCheck, row.value(), col.value())) {
					// Don't see that it is closed off.  This could be a
					// potential corner or an intersection.
					if (Variable.findExamine.value()
							&& canSeeNothing(option, row.value(), col.value())
							&& canSeeNothing(option2, row.value(), col.value())) {
						// Can not see anything ahead and in the direction we are
						// turning, assume that it is a potential corner.
						findDirection = option;
						findPrevDir = option2;
					} else {
						// STOP: we are next to an intersection or a room
						endFind();
					}
				} else if (Variable.findCut.value()) {
					// This corner is seen to be enclosed; we cut the corner.
					findDirection = option2;
					findPrevDir = option2;
				} else {
					// This corner is seen to be enclosed, and we deliberately
					// go the long way.
					findDirection = option;
					findPrevDir = option2;
				}
			}
		}
	}
	
	/**
	 * AC gets worse -RAK-
	 * Note: This routine affects magical AC bonuses so that stores
	 *	 can detect the damage.
	 * 
	 * @param damageType The type of damage to deal
	 * @return Whether the damage attempted to affect an item.
	 *         Does not necessarily mean damage was dealt
	 */
	public static boolean minusAc(long damageType) {
		int[] wornItems = new int[6];
		int wornCount = 0;
		
		if (Treasure.inventory[Constants.INVEN_BODY].category != Constants.TV_NOTHING) {
			wornItems[wornCount] = Constants.INVEN_BODY;
			wornCount++;
		}
		if (Treasure.inventory[Constants.INVEN_ARM].category != Constants.TV_NOTHING) {
			wornItems[wornCount] = Constants.INVEN_ARM;
			wornCount++;
		}
		if (Treasure.inventory[Constants.INVEN_OUTER].category != Constants.TV_NOTHING) {
			wornItems[wornCount] = Constants.INVEN_OUTER;
			wornCount++;
		}
		if (Treasure.inventory[Constants.INVEN_HANDS].category != Constants.TV_NOTHING) {
			wornItems[wornCount] = Constants.INVEN_HANDS;
			wornCount++;
		}
		if (Treasure.inventory[Constants.INVEN_HEAD].category != Constants.TV_NOTHING) {
			wornItems[wornCount] = Constants.INVEN_HEAD;
			wornCount++;
		}
		// also affect boots
		if (Treasure.inventory[Constants.INVEN_FEET].category != Constants.TV_NOTHING) {
			wornItems[wornCount] = Constants.INVEN_FEET;
			wornCount++;
		}
		
		boolean minus = false;
		if (wornCount > 0) {
			int j = wornItems[Rnd.randomInt(wornCount) - 1];
			InvenType item = Treasure.inventory[j];
			if ((item.flags & damageType) != 0) {
				String itemDesc = Desc.describeObject(Treasure.inventory[j], false);
				String msgResisted = String.format("Your %s resists damage!", itemDesc);
				IO.printMessage(msgResisted);
				minus = true;
			} else if ((item.armorClass + item.plusToArmorClass) > 0) {
				String itemDesc = Desc.describeObject(Treasure.inventory[j], false);
				String msgDamaged = String.format("Your %s is damaged!", itemDesc);
				IO.printMessage(msgDamaged);
				item.plusToArmorClass--;
				Moria1.calcBonuses();
				minus = true;
			}
		}
		return minus;
	}
	
	/**
	 * Corrode the unsuspecting person's armor -RAK-
	 * 
	 * @param corrosionSource
	 */
	public static void corrodeGas(String corrosionSource) {
		if (!minusAc(Constants.TR_RES_ACID)) {
			Moria1.takeHit(Rnd.randomInt(8), corrosionSource);
		}
		if (Misc3.damageInvenItem(Sets.SET_CORRODES, 5) > 0) {
			IO.printMessage("There is an acrid smell coming from your pack.");
		}
	}
	
	/**
	 * Poison gas the idiot. -RAK-
	 * 
	 * @param damage
	 * @param poisonSource
	 */
	public static void poisonGas(int damage, String poisonSource) {
		Moria1.takeHit(damage, poisonSource);
		Player.py.flags.poisoned += 12 + Rnd.randomInt(damage);
	}
	
	/**
	 * Burn the fool up. -RAK-
	 * 
	 * @param damage
	 * @param fireSource
	 */
	public static void fireDamage(int damage, String fireSource) {
		if (Player.py.flags.fireResistance > 0) {
			damage = damage / 3;
		}
		if (Player.py.flags.resistHeat > 0) {
			damage = damage / 3;
		}
		Moria1.takeHit(damage, fireSource);
		if (Misc3.damageInvenItem(Sets.SET_FLAMMABLE, 3) > 0) {
			IO.printMessage("There is smoke coming from your pack!");
		}
	}
	
	/**
	 * Freeze him to death. -RAK-
	 * 
	 * @param damage
	 * @param coldSource
	 */
	public static void coldDamage(int damage, String coldSource) {
		if (Player.py.flags.coldResistance > 0) {
			damage = damage / 3;
		}
		if (Player.py.flags.resistCold > 0) {
			damage = damage / 3;
		}
		Moria1.takeHit(damage, coldSource);
		if (Misc3.damageInvenItem(Sets.SET_FROST_DESTROY, 5) > 0) {
			IO.printMessage("Something shatters inside your pack!");
		}
	}
	
	/**
	 * Lightning bolt the sucker away. -RAK-
	 * 
	 * @param damage
	 * @param lightningSource
	 */
	public static void lightningDamage(int damage, String lightningSource) {
		if (Player.py.flags.lightningResistance > 0) {
			Moria1.takeHit((damage / 3), lightningSource);
		} else {
			Moria1.takeHit(damage, lightningSource);
		}
		if (Misc3.damageInvenItem(Sets.SET_LIGHTNING_DESTROY, 3) > 0) {
			IO.printMessage("There are sparks coming from your pack!");
		}
	}
	
	/**
	 * Throw acid on the hapless victim -RAK-
	 * 
	 * @param damage
	 * @param acidSource
	 */
	public static void acidDamage(int damage, String acidSource) {
		int flag;
		
		flag = 0;
		if (minusAc(Constants.TR_RES_ACID)) {
			flag = 1;
		}
		if (Player.py.flags.acidResistance > 0) {
			flag += 2;
		}
		Moria1.takeHit(damage / (flag + 1), acidSource);
		if (Misc3.damageInvenItem(Sets.SET_ACID_AFFECT, 3) > 0) {
			IO.printMessage("There is an acrid smell coming from your pack!");
		}
	}
}
