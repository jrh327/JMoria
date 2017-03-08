/*
 * Spells.java: player/creature spells, breaths, wands, scrolls, etc. code
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
import net.jonhopkins.moria.types.ClassType;
import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;

public class Spells {
	/* Following are spell procedure/functions			-RAK-	*/
	/* These routines are commonly used in the scroll, potion, wands, and	 */
	/* staves routines, and are occasionally called from other areas.	  */
	/* Now included are creature spells also.		       -RAK    */
	
	private Spells() { }
	
	public static String getMonsterName(MonsterType m_ptr, CreatureType r_ptr) {
		String m_name;
		if (!m_ptr.monsterLight) {
			m_name = "It";
		} else {
			m_name = String.format("The %s", r_ptr.name);
		}
		return m_name;
	}
	
	public static String getMonsterNameLowercase(MonsterType m_ptr, CreatureType r_ptr) {
		String m_name;
		if (!m_ptr.monsterLight) {
			m_name = "it";
		} else {
			m_name = String.format("the %s", r_ptr.name);
		}
		return m_name;
	}
	
	/* Sleep creatures adjacent to player			-RAK-	*/
	public static boolean sleepMonsters(int y, int x) {
		int i, j;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		boolean sleep;
		String out_val, m_name;
		
		sleep = false;
		for (i = y - 1; i <= y + 1; i++) {
			for (j = x - 1; j <= x + 1; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.creatureIndex > 1) {
					m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
					r_ptr = Monsters.creatureList[m_ptr.index];
					
					m_name = getMonsterName(m_ptr, r_ptr);
					if ((Rnd.randomInt(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) > 0) {
						if (m_ptr.monsterLight && (r_ptr.cdefense & Constants.CD_NO_SLEEP) > 0) {
							Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_NO_SLEEP;
						}
						out_val = String.format("%s is unaffected.", m_name);
						IO.printMessage(out_val);
					} else {
						sleep = true;
						m_ptr.sleep = 500;
						out_val = String.format("%s falls asleep.", m_name);
						IO.printMessage(out_val);
					}
				}
			}
		}
		return sleep;
	}
	
	/* Detect any treasure on the current panel		-RAK-	*/
	public static boolean detectTreasure() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		
		detect = false;
		for (i = Variable.panelRowMin; i <= Variable.panelRowMax; i++) {
			for (j = Variable.panelColMin; j <= Variable.panelColMax; j++) {
				c_ptr = Variable.cave[i][j];
				if ((c_ptr.treasureIndex != 0) && (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_GOLD) && !Misc1.testLight(i, j)) {
					c_ptr.fieldMark = true;
					Moria1.lightUpSpot(i, j);
					detect = true;
				}
			}
		}
		return detect;
	}
	
	/* Detect all objects on the current panel		-RAK-	*/
	public static boolean detectObject() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		
		detect = false;
		for (i = Variable.panelRowMin; i <= Variable.panelRowMax; i++) {
			for (j = Variable.panelColMin; j <= Variable.panelColMax; j++) {
				c_ptr = Variable.cave[i][j];
				if ((c_ptr.treasureIndex != 0) && (Treasure.treasureList[c_ptr.treasureIndex].category < Constants.TV_MAX_OBJECT) && !Misc1.testLight(i, j)) {
					c_ptr.fieldMark = true;
					Moria1.lightUpSpot(i, j);
					detect = true;
				}
			}
		}
		return detect;
	}
	
	/* Locates and displays traps on current panel		-RAK-	*/
	public static boolean detectTrap() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		InvenType t_ptr;
		
		detect = false;
		for (i = Variable.panelRowMin; i <= Variable.panelRowMax; i++) {
			for (j = Variable.panelColMin; j <= Variable.panelColMax; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.treasureIndex != 0) {
					if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_INVIS_TRAP) {
						c_ptr.fieldMark = true;
						Moria2.revealTrap(i, j);
						detect = true;
					} else if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_CHEST) {
						t_ptr = Treasure.treasureList[c_ptr.treasureIndex];
						Desc.identifyItemPlusses(t_ptr);
					}
				}
			}
		}
		return detect;
	}
	
	/* Locates and displays all secret doors on current panel -RAK-	*/
	public static boolean detectSecretDoors() {
		int i, j;
		boolean detect;
		CaveType c_ptr;
		
		detect = false;
		for (i = Variable.panelRowMin; i <= Variable.panelRowMax; i++) {
			for (j = Variable.panelColMin; j <= Variable.panelColMax; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.treasureIndex != 0) {
					/* Secret doors  */
					if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_SECRET_DOOR) {
						c_ptr.fieldMark = true;
						Moria2.revealTrap(i, j);
						detect = true;
					}
				
				/* Staircases	 */
				} else if (((Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_UP_STAIR) || (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_DOWN_STAIR)) && !c_ptr.fieldMark) {
						c_ptr.fieldMark = true;
						Moria1.lightUpSpot(i, j);
						detect = true;
				}
			}
		}
		return detect;
	}
	
	/* Locates and displays all invisible creatures on current panel -RAK-*/
	public static boolean detectInvisibleCreatures() {
		int i;
		boolean flag;
		MonsterType m_ptr;
		
		flag = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			if (Misc1.panelContains(m_ptr.y, m_ptr.x) && (Constants.CM_INVISIBLE & Monsters.creatureList[m_ptr.index].cmove) > 0) {
				m_ptr.monsterLight = true;
				/* works correctly even if hallucinating */
				IO.print(Monsters.creatureList[m_ptr.index].cchar, m_ptr.y, m_ptr.x);
				flag = true;
			}
		}
		if (flag) {
			IO.printMessage("You sense the presence of invisible creatures!");
			IO.printMessage("");
			/* must unlight every monster just lighted */
			Creature.creatures(false);
		}
		return flag;
	}
	
	/* Light an area: 1.  If corridor  light immediate area -RAK-*/
	/*		  2.  If room  light entire room plus immediate area.     */
	public static boolean lightArea(int y, int x) {
		int i, j;
		boolean light;
		
		if (Player.py.flags.blind < 1) {
			IO.printMessage("You are surrounded by a white light.");
		}
		light = true;
		if (Variable.cave[y][x].litRoom && (Variable.dungeonLevel > 0)) {
			Moria1.lightUpRoom(y, x);
		}
		/* Must always light immediate area, because one might be standing on
		   the edge of a room, or next to a destroyed area, etc.  */
		for (i = y - 1; i <= y + 1; i++) {
			for (j = x - 1; j <= x + 1; j++) {
				Variable.cave[i][j].permLight = true;
				Moria1.lightUpSpot(i, j);
			}
		}
		return light;
	}
	
	/* Darken an area, opposite of light area		-RAK-	*/
	public static boolean unlightArea(int y, int x) {
		int i, j;
		int tmp1, tmp2;
		boolean unlight;
		int start_row, start_col, end_row, end_col;
		CaveType c_ptr;
		
		unlight = false;
		if (Variable.cave[y][x].litRoom && (Variable.dungeonLevel > 0)) {
			tmp1 = (Constants.SCREEN_HEIGHT / 2);
			tmp2 = (Constants.SCREEN_WIDTH / 2);
			start_row = (y / tmp1) * tmp1 + 1;
			start_col = (x / tmp2) * tmp2 + 1;
			end_row = start_row + tmp1 - 1;
			end_col = start_col + tmp2 - 1;
			for (i = start_row; i <= end_row; i++) {
				for (j = start_col; j <= end_col; j++) {
					c_ptr = Variable.cave[i][j];
					if (c_ptr.litRoom && c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
						c_ptr.permLight = false;
						c_ptr.fval = Constants.DARK_FLOOR;
						Moria1.lightUpSpot(i, j);
						if (!Misc1.testLight(i, j)) {
							unlight = true;
						}
					}
				}
			}
		} else {
			for (i = y - 1; i <= y + 1; i++) {
				for (j = x - 1; j <= x + 1; j++) {
					c_ptr = Variable.cave[i][j];
					if ((c_ptr.fval == Constants.CORR_FLOOR) && c_ptr.permLight) {
						/* pl could have been set by star-lite wand, etc */
						c_ptr.permLight = false;
						unlight = true;
					}
				}
			}
		}
		
		if (unlight && Player.py.flags.blind <= 0) {
			IO.printMessage("Darkness surrounds you.");
		}
		
		return unlight;
	}
	
	/* Map the current area plus some			-RAK-	*/
	public static void mapArea() {
		CaveType c_ptr;
		int i7, i8, n, m;
		int i, j, k, l;
		
		i = Variable.panelRowMin - Rnd.randomInt(10);
		j = Variable.panelRowMax + Rnd.randomInt(10);
		k = Variable.panelColMin - Rnd.randomInt(20);
		l = Variable.panelColMax + Rnd.randomInt(20);
		for (m = i; m <= j; m++) {
			for (n = k; n <= l; n++) {
				if (Misc1.isInBounds(m, n) && (Variable.cave[m][n].fval <= Constants.MAX_CAVE_FLOOR)) {
					for (i7 = m - 1; i7 <= m + 1; i7++) {
						for (i8 = n - 1; i8 <= n + 1; i8++) {
							c_ptr = Variable.cave[i7][i8];
							if (c_ptr.fval >= Constants.MIN_CAVE_WALL) {
								c_ptr.permLight = true;
							} else if ((c_ptr.treasureIndex != 0) &&
									(Treasure.treasureList[c_ptr.treasureIndex].category >= Constants.TV_MIN_VISIBLE) &&
									(Treasure.treasureList[c_ptr.treasureIndex].category <= Constants.TV_MAX_VISIBLE)) {
								c_ptr.fieldMark = true;
							}
						}
					}
				}
			}
		}
		Misc1.printMap();
	}
	
	/* Identify an object					-RAK-	*/
	public static boolean identifyObject() {
		IntPointer item_val = new IntPointer();
		String out_val, tmp_str;
		boolean ident;
		InvenType i_ptr;
		
		ident = false;
		if (Moria1.getItemId(item_val, "Item you wish identified?", 0, Constants.INVEN_ARRAY_SIZE, "", "")) {
			ident = true;
			Desc.identify(item_val);
			i_ptr = Treasure.inventory[item_val.value()];
			Desc.identifyItemPlusses(i_ptr);
			tmp_str = Desc.describeObject(i_ptr, true);
			if (item_val.value() >= Constants.INVEN_WIELD) {
				Moria1.calcBonuses();
				out_val = String.format("%s: %s", Moria1.describeUse(item_val.value()), tmp_str);
			} else {
				out_val = String.format("%c %s", item_val.value() + 97, tmp_str);
			}
			IO.printMessage(out_val);
		}
		return ident;
	}
	
	/* Get all the monsters on the level pissed off.	-RAK-	*/
	public static boolean aggravateMonster(int dis_affect) {
		int i;
		boolean aggravate;
		MonsterType m_ptr;
		
		aggravate = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			m_ptr.sleep = 0;
			if ((m_ptr.currDistance <= dis_affect) && (m_ptr.speed < 2)) {
				m_ptr.speed++;
				aggravate = true;
			}
		}
		if (aggravate) {
			IO.printMessage("You hear a sudden stirring in the distance!");
		}
		return aggravate;
	}
	
	/* Surround the fool with traps (chuckle)		-RAK-	*/
	public static boolean createTraps() {
		int i, j;
		boolean trap;
		CaveType c_ptr;
		
		trap = true;
		for (i = Player.y - 1; i <= Player.y + 1; i++) {
			for (j = Player.x - 1; j <= Player.x + 1; j++) {
				/* Don't put a trap under the player, since this can lead to
				 * strange situations, e.g. falling through a trap door while
				 * trying to rest, setting off a falling rock trap and ending
				 * up under the rock.  */
				if (i == Player.y && j == Player.x) {
					continue;
				}
				c_ptr = Variable.cave[i][j];
				if (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
					if (c_ptr.treasureIndex != 0) {
						Moria3.deleteObject(i, j);
					}
					Misc3.placeTrap(i, j, Rnd.randomInt(Constants.MAX_TRAP) - 1);
					/* don't let player gain exp from the newly created traps */
					Treasure.treasureList[c_ptr.treasureIndex].misc = 0;
					/* open pits are immediately visible, so call mor1.lite_spot */
					Moria1.lightUpSpot(i, j);
				}
			}
		}
		return trap;
	}
	
	/* Surround the player with doors.			-RAK-	*/
	public static boolean createDoors() {
		int i, j;
		boolean door;
		int k;
		CaveType c_ptr;
		
		door = false;
		for (i = Player.y - 1; i <= Player.y + 1; i++) {
			for (j = Player.x - 1; j <= Player.x + 1; j++) {
				if ((i != Player.y) || (j != Player.x)) {
					c_ptr = Variable.cave[i][j];
					if (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
						door = true;
						if (c_ptr.treasureIndex != 0) {
							Moria3.deleteObject(i, j);
						}
						k = Misc1.popTreasure();
						c_ptr.fval = Constants.BLOCKED_FLOOR;
						c_ptr.treasureIndex = k;
						Desc.copyIntoInventory(Treasure.treasureList[k], Constants.OBJ_CLOSED_DOOR);
						Moria1.lightUpSpot(i, j);
					}
				}
			}
		}
		return door;
	}
	
	/* Destroys any adjacent door(s)/trap(s)		-RAK-	*/
	public static boolean destroyTrapsAndDoors() {
		int i, j;
		boolean destroy;
		CaveType c_ptr;
		
		destroy = false;
		for (i = Player.y - 1; i <= Player.y + 1; i++) {
			for (j = Player.x - 1; j <= Player.x + 1; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.treasureIndex != 0) {
					if (((Treasure.treasureList[c_ptr.treasureIndex].category >= Constants.TV_INVIS_TRAP)
							&& (Treasure.treasureList[c_ptr.treasureIndex].category <= Constants.TV_CLOSED_DOOR)
							&& (Treasure.treasureList[c_ptr.treasureIndex].category != Constants.TV_RUBBLE))
							|| (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_SECRET_DOOR)) {
						if (Moria3.deleteObject(i, j)) {
							destroy = true;
						}
					} else if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_CHEST
							&& Treasure.treasureList[c_ptr.treasureIndex].flags != 0) {
						/* destroy traps on chest and unlock */
						Treasure.treasureList[c_ptr.treasureIndex].flags &= ~(Constants.CH_TRAPPED | Constants.CH_LOCKED);
						Treasure.treasureList[c_ptr.treasureIndex].specialName = Constants.SN_UNLOCKED;
						IO.printMessage("You have disarmed the chest.");
						Desc.identifyItemPlusses(Treasure.treasureList[c_ptr.treasureIndex]);
						destroy = true;
					}
				}
			}
		}
		return destroy;
	}
	
	/* Display all creatures on the current panel		-RAK-	*/
	public static boolean detectMonsters() {
		int i;
		boolean detect;
		MonsterType m_ptr;
		
		detect = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			if (Misc1.panelContains(m_ptr.y, m_ptr.x) && ((Constants.CM_INVISIBLE & Monsters.creatureList[m_ptr.index].cmove) == 0)) {
				m_ptr.monsterLight = true;
				/* works correctly even if hallucinating */
				IO.print(Monsters.creatureList[m_ptr.index].cchar, m_ptr.y, m_ptr.x);
				detect = true;
			}
		}
		if (detect) {
			IO.printMessage("You sense the presence of monsters!");
			IO.printMessage("");
			/* must unlight every monster just lighted */
			Creature.creatures(false);
		}
		return detect;
	}
	
	/* Leave a line of light in given dir, blue light can sometimes	*/
	/* hurt creatures.				       -RAK-   */
	public static void lightLine(int dir, int y, int x) {
		int i;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		int dist;
		boolean flag;
		String out_val, m_name;
		
		dist = -1;
		flag = false;
		do {
			/* put mmove at end because want to light up current spot */
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else {
				if (!c_ptr.permLight && !c_ptr.tempLight) {
					/* set pl so that mor1.lite_spot will work */
					c_ptr.permLight = true;
					if (c_ptr.fval == Constants.LIGHT_FLOOR) {
						if (Misc1.panelContains(y1.value(), x1.value())) {
							Moria1.lightUpRoom(y1.value(), x1.value());
						}
					} else {
						Moria1.lightUpSpot(y1.value(), x1.value());
					}
				}
				/* set pl in case tl was true above */
				c_ptr.permLight = true;
				if (c_ptr.creatureIndex > 1) {
					m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
					r_ptr = Monsters.creatureList[m_ptr.index];
					/* light up and draw monster */
					Creature.updateMonster(c_ptr.creatureIndex);
					m_name = getMonsterName(m_ptr, r_ptr);
					if ((Constants.CD_LIGHT & r_ptr.cdefense) > 0) {
						if (m_ptr.monsterLight) {
							Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_LIGHT;
						}
						i = Moria3.monsterTakeHit(c_ptr.creatureIndex, Misc1.damageRoll(2, 8));
						if (i >= 0) {
							out_val = String.format("%s shrivels away in the light!", m_name);
							IO.printMessage(out_val);
							Misc3.printExperience();
						} else {
							out_val = String.format("%s cringes from the light!", m_name);
							IO.printMessage(out_val);
						}
					}
				}
			}
			Misc3.moveMonster(dir, y1, x1);
		} while (!flag);
	}
	
	/* Light line in all directions				-RAK-	*/
	public static void starLight(int y, int x) {
		int i;
		
		if (Player.py.flags.blind < 1) {
			IO.printMessage("The end of the staff bursts into a blue shimmering light.");
		}
		for (i = 1; i <= 9; i++) {
			if (i != 5) {
				lightLine(i, y, x);
			}
		}
	}
	
	/* Disarms all traps/chests in a given direction	-RAK-	*/
	public static boolean disarmAll(int dir, int y, int x) {
		CaveType c_ptr;
		InvenType t_ptr;
		boolean disarm;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		
		disarm = false;
		dist = -1;
		do {
			/* put m3.mmove at end, in case standing on a trap */
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			/* note, must continue up to and including the first non open space,
			 * because secret doors have fval greater than MAX_OPEN_SPACE */
			if (c_ptr.treasureIndex != 0) {
				t_ptr = Treasure.treasureList[c_ptr.treasureIndex];
				if ((t_ptr.category == Constants.TV_INVIS_TRAP) || (t_ptr.category == Constants.TV_VIS_TRAP)) {
					if (Moria3.deleteObject(y1.value(), x1.value())) {
						disarm = true;
					}
				} else if (t_ptr.category == Constants.TV_CLOSED_DOOR) {
					t_ptr.misc = 0;  /* Locked or jammed doors become merely closed. */
				} else if (t_ptr.category == Constants.TV_SECRET_DOOR) {
						c_ptr.fieldMark = true;
						Moria2.revealTrap(y1.value(), x1.value());
						disarm = true;
				} else if ((t_ptr.category == Constants.TV_CHEST) && (t_ptr.flags != 0)) {
					IO.printMessage("Click!");
					t_ptr.flags &= ~(Constants.CH_TRAPPED | Constants.CH_LOCKED);
					disarm = true;
					t_ptr.specialName = Constants.SN_UNLOCKED;
					Desc.identifyItemPlusses(t_ptr);
				}
			}
			Misc3.moveMonster(dir, y1, x1);
		} while ((dist <= Constants.OBJ_BOLT_RANGE) && c_ptr.fval <= Constants.MAX_OPEN_SPACE);
		return disarm;
	}
	
	/* Return flags for given type area affect		-RAK-	*/
	public static void getFlags(int typ, IntPointer weapon_type, IntPointer harm_type, IntPointer destroy) {
		switch(typ) {
		case Constants.GF_MAGIC_MISSILE:
			weapon_type.value(0);
			harm_type.value(  0);
			destroy.value(    0);	//set_null;
			break;
		case Constants.GF_LIGHTNING:
			weapon_type.value(Constants.CS_BR_LIGHT);
			harm_type.value(  Constants.CD_LIGHT);
			destroy.value(    1);	//set_lightning_destroy;
			break;
		case Constants.GF_POISON_GAS:
			weapon_type.value(Constants.CS_BR_GAS);
			harm_type.value(  Constants.CD_POISON);
			destroy.value(    0);	//set_null;
			break;
		case Constants.GF_ACID:
			weapon_type.value(Constants.CS_BR_ACID);
			harm_type.value(  Constants.CD_ACID);
			destroy.value(    2);	//set_acid_destroy;
			break;
		case Constants.GF_FROST:
			weapon_type.value(Constants.CS_BR_FROST);
			harm_type.value(  Constants.CD_FROST);
			destroy.value(    3);	//set_frost_destroy;
			break;
		case Constants.GF_FIRE:
			weapon_type.value(Constants.CS_BR_FIRE);
			harm_type.value(  Constants.CD_FIRE);
			destroy.value(    4);	//set_fire_destroy;
			break;
		case Constants.GF_HOLY_ORB:
			weapon_type.value(0);
			harm_type.value(  Constants.CD_EVIL);
			destroy.value(    0);	//set_null;
			break;
		default:
			IO.printMessage("ERROR in get_flags()\n");
		}
	}
	
	private static boolean doesDestroy(int func, InvenType item) {
		switch (func) {
		case 0:
			return Sets.isNull(item);
		case 1:
			return Sets.doesLightningDestroy(item);
		case 2:
			return Sets.doesAcidDestroy(item);
		case 3:
			return Sets.doesFrostDestroy(item);
		case 4:
			return Sets.doesFireDestroy(item);
		default:
			return false;
		}
	}
	
	/* Shoot a bolt in a given direction			-RAK-	*/
	public static void fireBolt(int typ, int dir, int y, int x, int dam, String bolt_typ) {
		int oldy, oldx, dist, i;
		boolean pl, flag;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		IntPointer weapon_type = new IntPointer(), harm_type = new IntPointer();
		IntPointer dummy = new IntPointer();
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		flag = false;
		getFlags(typ, weapon_type, harm_type, dummy);
		oldy = y;
		oldx = x;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			Moria1.lightUpSpot(oldy, oldx);
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else {
				if (c_ptr.creatureIndex > 1) {
					flag = true;
					m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
					r_ptr = Monsters.creatureList[m_ptr.index];
					
					/* light up monster and draw monster, temporarily set
					 * pl so that creature.update_mon() will work */
					pl = c_ptr.permLight;
					c_ptr.permLight = true;
					Creature.updateMonster(c_ptr.creatureIndex);
					c_ptr.permLight = pl;
					/* draw monster and clear previous bolt */
					IO.putQio();
					
					m_name = getMonsterNameLowercase(m_ptr, r_ptr);
					out_val = String.format("The %s strikes %s.", bolt_typ, m_name);
					IO.printMessage(out_val);
					if ((harm_type.value() & r_ptr.cdefense) > 0) {
						dam *= 2;
						if (m_ptr.monsterLight) {
							Variable.creatureRecall[m_ptr.index].cdefense |= harm_type.value();
						}
					} else if ((weapon_type.value() & r_ptr.spells) > 0) {
						dam /=  4;
						if (m_ptr.monsterLight) {
							Variable.creatureRecall[m_ptr.index].spells |= weapon_type.value();
						}
					}
					m_name = getMonsterName(m_ptr, r_ptr);
					i = Moria3.monsterTakeHit(c_ptr.creatureIndex, dam);
					if (i >= 0) {
						out_val = String.format("%s dies in a fit of agony.", m_name);
						IO.printMessage(out_val);
						Misc3.printExperience();
					} else if (dam > 0) {
						out_val = String.format("%s screams in agony.", m_name);
						IO.printMessage(out_val);
					}
				} else if (Misc1.panelContains(y1.value(), x1.value()) && (Player.py.flags.blind < 1)) {
					IO.print('*', y1.value(), x1.value());
					/* show the bolt */
					IO.putQio();
				}
			}
			oldy = y1.value();
			oldx = x1.value();
		} while (!flag);
	}
	
	/* Shoot a ball in a given direction.  Note that balls have an	*/
	/* area affect.					      -RAK-   */
	public static void fireBall(int typ, int dir, int y, int x, int dam_hp, String descrip) {
		int i, j;
		int dam, max_dis, thit, tkill, k;
		int oldy, oldx, dist;
		boolean flag, tmp;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		IntPointer harm_type = new IntPointer();
		IntPointer weapon_type = new IntPointer();
		IntPointer destroy = new IntPointer();
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val;
		
		thit	= 0;
		tkill	= 0;
		max_dis	= 2;
		getFlags(typ, weapon_type, harm_type, destroy);
		flag = false;
		oldy = y;
		oldx = x;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			Moria1.lightUpSpot(oldy, oldx);
			if (dist > Constants.OBJ_BOLT_RANGE) {
				flag = true;
			} else {
				c_ptr = Variable.cave[y1.value()][x1.value()];
				if ((c_ptr.fval >= Constants.MIN_CLOSED_SPACE) || (c_ptr.creatureIndex > 1)) {
					flag = true;
					if (c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
						y1.value(oldy);
						x1.value(oldx);
					}
					/* The ball hits and explodes.		     */
					/* The explosion.			     */
					for (i = y1.value() - max_dis; i <= y1.value() + max_dis; i++) {
						for (j = x1.value() - max_dis; j <= x1.value() + max_dis; j++) {
							if (Misc1.isInBounds(i, j) && (Misc1.distance(y1.value(), x1.value(), i, j) <= max_dis) && Misc1.isInLineOfSight(y1.value(), x1.value(), i, j)) {
								c_ptr = Variable.cave[i][j];
								if ((c_ptr.treasureIndex != 0) && doesDestroy(destroy.value(), Treasure.treasureList[c_ptr.treasureIndex])) {
									Moria3.deleteObject(i, j);
								}
								if (c_ptr.fval <= Constants.MAX_OPEN_SPACE) {
									if (c_ptr.creatureIndex > 1) {
										m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
										r_ptr = Monsters.creatureList[m_ptr.index];
										
										/* lite up creature if visible, temp
										 * set pl so that creature.update_mon works */
										tmp = c_ptr.permLight;
										c_ptr.permLight = true;
										Creature.updateMonster(c_ptr.creatureIndex);
										
										thit++;
										dam = dam_hp;
										if ((harm_type.value() & r_ptr.cdefense) > 0) {
											dam *= 2;
											if (m_ptr.monsterLight) {
												Variable.creatureRecall[m_ptr.index].cdefense |= harm_type.value();
											}
										} else if ((weapon_type.value() & r_ptr.spells) > 0) {
											dam /= 4;
											if (m_ptr.monsterLight) {
												Variable.creatureRecall[m_ptr.index].spells |= weapon_type.value();
											}
										}
										dam = (dam / (Misc1.distance(i, j, y1.value(), x1.value()) + 1));
										k = Moria3.monsterTakeHit(c_ptr.creatureIndex, dam);
										if (k >= 0) {
											tkill++;
										}
										c_ptr.permLight = tmp;
									} else if (Misc1.panelContains(i, j) && (Player.py.flags.blind < 1)) {
										IO.print('*', i, j);
									}
								}
							}
						}
					}
					/* show ball of whatever */
					IO.putQio();
					
					for (i = (y1.value() - 2); i <= (y1.value() + 2); i++) {
						for (j = (x1.value() - 2); j <= (x1.value() + 2); j++) {
							if (Misc1.isInBounds(i, j) && Misc1.panelContains(i, j) && (Misc1.distance(y1.value(), x1.value(), i, j) <= max_dis)) {
								Moria1.lightUpSpot(i, j);
							}
						}
					}
					
					/* End  explosion.		     */
					if (thit == 1) {
						out_val = String.format("The %s envelops a creature!", descrip);
						IO.printMessage(out_val);
					} else if (thit > 1) {
						out_val = String.format("The %s envelops several creatures!", descrip);
						IO.printMessage(out_val);
					}
					if (tkill == 1) {
						IO.printMessage("There is a scream of agony!");
					} else if (tkill > 1) {
						IO.printMessage("There are several screams of agony!");
					}
					if (tkill >= 0) {
						Misc3.printExperience();
					}
					/* End ball hitting.		     */
				} else if (Misc1.panelContains(y1.value(), x1.value()) && (Player.py.flags.blind < 1)) {
					IO.print('*', y1.value(), x1.value());
					/* show bolt */
					IO.putQio();
				}
				oldy = y1.value();
				oldx = x1.value();
			}
		} while (!flag);
	}
	
	/* Breath weapon works like a fire_ball, but affects the player. */
	/* Note the area affect.			      -RAK-   */
	public static void breath(int typ, int y, int x, int dam_hp, String ddesc, int monptr) {
		int i, j;
		int dam, max_dis;
		IntPointer harm_type = new IntPointer();
		IntPointer weapon_type = new IntPointer();
		IntPointer destroy = new IntPointer();
		int tmp, treas;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		max_dis = 2;
		getFlags(typ, weapon_type, harm_type, destroy);
		for (i = y - 2; i <= y + 2; i++) {
			for (j = x - 2; j <= x + 2; j++) {
				if (Misc1.isInBounds(i, j) && (Misc1.distance(y, x, i, j) <= max_dis) && Misc1.isInLineOfSight(y, x, i, j)) {
					c_ptr = Variable.cave[i][j];
					if (c_ptr.treasureIndex != 0 && doesDestroy(destroy.value(), Treasure.treasureList[c_ptr.treasureIndex])) {
						Moria3.deleteObject(i, j);
					}
					if (c_ptr.fval <= Constants.MAX_OPEN_SPACE) {
						/* must test status bit, not py.flags.blind here, flag could have
						 * been set by a previous monster, but the breath should still
						 * be visible until the blindness takes effect */
						if (Misc1.panelContains(i, j) && !((Player.py.flags.status & Constants.PY_BLIND) > 0)) {
							IO.print('*', i, j);
						}
						if (c_ptr.creatureIndex > 1) {
							m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
							r_ptr = Monsters.creatureList[m_ptr.index];
							dam = dam_hp;
							if ((harm_type.value() & r_ptr.cdefense) > 0) {
								dam *= 2;
							} else if ((weapon_type.value() & r_ptr.spells) > 0) {
								dam /= 4;
							}
							dam /= (Misc1.distance(i, j, y, x) + 1);
							/* can not call mor3.mon_take_hit here, since player does not
							 * get experience for kill */
							m_ptr.hitpoints -= dam;
							m_ptr.sleep = 0;
							if (m_ptr.hitpoints < 0) {
								treas = Moria3.monsterDeath(m_ptr.y, m_ptr.x, r_ptr.cmove);
								if (m_ptr.monsterLight) {
									tmp = (Variable.creatureRecall[m_ptr.index].cmove & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT;
									if (tmp > ((treas & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT)) {
										treas = (treas & ~Constants.CM_TREASURE) | (tmp << Constants.CM_TR_SHIFT);
									}
									Variable.creatureRecall[m_ptr.index].cmove = treas | (Variable.creatureRecall[m_ptr.index].cmove & ~Constants.CM_TREASURE);
								}
								
								/* It ate an already processed monster.Handle normally.*/
								if (monptr < c_ptr.creatureIndex) {
									Moria3.deleteMonster(c_ptr.creatureIndex);
								/* If it eats this monster, an already processed monster
								 * will take its place, causing all kinds of havoc.
								 * Delay the kill a bit. */
								} else {
									Moria3.deleteMonster1(c_ptr.creatureIndex);
								}
							}
						} else if (c_ptr.creatureIndex == 1) {
							dam = (dam_hp / (Misc1.distance(i, j, y, x) + 1));
							/* let's do at least one point of damage */
							/* prevents randint(0) problem with poison_gas, also */
							if (dam == 0) {
								dam = 1;
							}
							switch(typ) {
							case Constants.GF_LIGHTNING: Moria2.lightningDamage(dam, ddesc); break;
							case Constants.GF_POISON_GAS: Moria2.poisonGas(dam, ddesc); break;
							case Constants.GF_ACID: Moria2.acidDamage(dam, ddesc); break;
							case Constants.GF_FROST: Moria2.coldDamage(dam, ddesc); break;
							case Constants.GF_FIRE: Moria2.fireDamage(dam, ddesc); break;
							default: break;
							}
						}
					}
				}
			}
		}
		/* show the ball of gas */
		IO.putQio();
		
		for (i = (y - 2); i <= (y + 2); i++) {
			for (j = (x - 2); j <= (x + 2); j++) {
				if (Misc1.isInBounds(i, j) && Misc1.panelContains(i, j) && (Misc1.distance(y, x, i, j) <= max_dis)) {
					Moria1.lightUpSpot(i, j);
				}
			}
		}
	}
	
	/* Recharge a wand, staff, or rod.  Sometimes the item breaks. -RAK-*/
	public static boolean recharge(int num) {
		IntPointer i = new IntPointer(), j = new IntPointer();
		IntPointer item_val = new IntPointer();
		boolean res;
		InvenType i_ptr;
		
		res = false;
		if (!Misc3.findRange(Constants.TV_STAFF, Constants.TV_WAND, i, j)) {
			IO.printMessage("You have nothing to recharge.");
		} else if (Moria1.getItemId(item_val, "Recharge which item?", i.value(), j.value(), "", "")) {
			i_ptr = Treasure.inventory[item_val.value()];
			res = true;
			/* recharge I = recharge(20) = 1/6 failure for empty 10th level wand */
			/* recharge II = recharge(60) = 1/10 failure for empty 10th level wand*/
			/* make it harder to recharge high level, and highly charged wands, note
			 * that i can be negative, so check its value before trying to call
			 * randint().  */
			i.value(num + 50 - i_ptr.level - i_ptr.misc);
			if (i.value() < 19) {
				i.value(1);	/* Automatic failure.  */
			} else {
				i.value(Rnd.randomInt(i.value() / 10));
			}
			if (i.value() == 1) {
				IO.printMessage("There is a bright flash of light.");
				Misc3.destroyInvenItem(item_val.value());
			} else {
				num = (num / (i_ptr.level + 2)) + 1;
				i_ptr.misc += 2 + Rnd.randomInt(num);
				if (Desc.arePlussesKnownByPlayer(i_ptr)) {
					Desc.clearPlussesIdentity(i_ptr);
				}
				Desc.clearEmpty(i_ptr);
			}
		}
		return res;
	}
	
	/* Increase or decrease a creatures hit points		-RAK-	*/
	public static boolean changeMonsterHitpoints(int dir, int y, int x, int dam) {
		int i;
		boolean flag, monster;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		monster = false;
		flag = false;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				flag = true;
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				r_ptr = Monsters.creatureList[m_ptr.index];
				m_name = getMonsterName(m_ptr, r_ptr);
				monster = true;
				i = Moria3.monsterTakeHit(c_ptr.creatureIndex, dam);
				if (i >= 0) {
					out_val = String.format("%s dies in a fit of agony.", m_name);
					IO.printMessage(out_val);
					Misc3.printExperience();
				} else if (dam > 0) {
					out_val = String.format("%s screams in agony.", m_name);
					IO.printMessage(out_val);
				}
			}
		} while (!flag);
		return monster;
	}
	
	/* Drains life; note it must be living.		-RAK-	*/
	public static boolean drainLife(int dir, int y, int x) {
		int i;
		boolean flag, drain;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		drain = false;
		flag = false;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				flag = true;
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				r_ptr = Monsters.creatureList[m_ptr.index];
				if ((r_ptr.cdefense & Constants.CD_UNDEAD) == 0) {
					drain = true;
					m_name = getMonsterName(m_ptr, r_ptr);
					i = Moria3.monsterTakeHit(c_ptr.creatureIndex, 75);
					if (i >= 0) {
						out_val = String.format("%s dies in a fit of agony.", m_name);
						IO.printMessage(out_val);
						Misc3.printExperience();
					} else {
						out_val = String.format("%s screams in agony.", m_name);
						IO.printMessage(out_val);
					}
				} else {
					Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_UNDEAD;
				}
			}
		} while (!flag);
		return drain;
	}
	
	/* Increase or decrease a creatures speed		-RAK-	*/
	/* NOTE: cannot slow a winning creature (BALROG)		 */
	public static boolean speedMonster(int dir, int y, int x, int spd) {
		boolean flag, speed;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		speed = false;
		flag = false;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				flag = true;
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				r_ptr = Monsters.creatureList[m_ptr.index];
				m_name = getMonsterName(m_ptr, r_ptr);
				if (spd > 0) {
					m_ptr.speed += spd;
					m_ptr.sleep = 0;
					out_val = String.format("%s starts moving faster.", m_name);
					IO.printMessage(out_val);
					speed = true;
				} else if (Rnd.randomInt(Constants.MAX_MONS_LEVEL) > r_ptr.level) {
					m_ptr.speed += spd;
					m_ptr.sleep = 0;
					out_val = String.format("%s starts moving slower.", m_name);
					IO.printMessage(out_val);
					speed = true;
				} else {
					m_ptr.sleep = 0;
					out_val = String.format("%s is unaffected.", m_name);
					IO.printMessage(out_val);
				}
			}
		} while (!flag);
		return speed;
	}
	
	/* Confuse a creature					-RAK-	*/
	public static boolean confuseMonster(int dir, int y, int x) {
		boolean flag, confuse;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		confuse = false;
		flag = false;
		dist = 0;
		do	{
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				r_ptr = Monsters.creatureList[m_ptr.index];
				m_name = getMonsterName(m_ptr, r_ptr);
				flag = true;
				if ((Rnd.randomInt(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) > 0) {
					if (m_ptr.monsterLight && (r_ptr.cdefense & Constants.CD_NO_SLEEP) > 0) {
						Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_NO_SLEEP;
					}
					/* Monsters which resisted the attack should wake up.
					 * Monsters with innate resistance ignore the attack.  */
					if ((Constants.CD_NO_SLEEP & r_ptr.cdefense) == 0) {
						m_ptr.sleep = 0;
					}
					out_val = String.format("%s is unaffected.", m_name);
					IO.printMessage(out_val);
				} else {
					if (m_ptr.confused > 0) {
						m_ptr.confused += 3;
					} else {
						m_ptr.confused = 2 + Rnd.randomInt(16);
					}
					confuse = true;
					m_ptr.sleep = 0;
					out_val = String.format("%s appears confused.", m_name);
					IO.printMessage(out_val);
				}
			}
		} while (!flag);
		return confuse;
	}
	
	/* Sleep a creature.					-RAK-	*/
	public static boolean sleepMonster(int dir, int y, int x) {
		boolean flag, sleep;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		sleep = false;
		flag = false;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				r_ptr = Monsters.creatureList[m_ptr.index];
				flag = true;
				m_name = getMonsterName(m_ptr, r_ptr);
				if ((Rnd.randomInt(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) > 0) {
					if (m_ptr.monsterLight && (r_ptr.cdefense & Constants.CD_NO_SLEEP) > 0) {
						Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_NO_SLEEP;
					}
					out_val = String.format("%s is unaffected.", m_name);
					IO.printMessage(out_val);
				} else {
					m_ptr.sleep = 500;
					sleep = true;
					out_val = String.format("%s falls asleep.", m_name);
					IO.printMessage(out_val);
				}
			}
		} while (!flag);
		return sleep;
	}
	
	/* Turn stone to mud, delete wall.			-RAK-	*/
	public static boolean transformWallToMud(int dir, int y, int x) {
		int i, dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean wall;
		String out_val, tmp_str;
		boolean flag;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String m_name;
		
		wall = false;
		flag = false;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			x = x1.value();
			y = y1.value();
			dist++;
			c_ptr = Variable.cave[y][x];
			/* note, this ray can move through walls as it turns them to mud */
			if (dist == Constants.OBJ_BOLT_RANGE) {
				flag = true;
			}
			if ((c_ptr.fval >= Constants.MIN_CAVE_WALL) && (c_ptr.fval != Constants.BOUNDARY_WALL)) {
				flag = true;
				Moria3.tunnelThroughWall(y, x, 1, 0);
				if (Misc1.testLight(y, x)) {
					IO.printMessage("The wall turns into mud.");
					wall = true;
				}
			} else if ((c_ptr.treasureIndex != 0) && (c_ptr.fval >= Constants.MIN_CLOSED_SPACE)) {
				flag = true;
				if (Misc1.panelContains(y, x) && Misc1.testLight(y, x)) {
					tmp_str = Desc.describeObject(Treasure.treasureList[c_ptr.treasureIndex], false);
					out_val = String.format("The %s turns into mud.", tmp_str);
					IO.printMessage(out_val);
					wall = true;
				}
				if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_RUBBLE) {
					Moria3.deleteObject(y, x);
					if (Rnd.randomInt(10) == 1) {
						Misc3.placeObject(y, x, false);
						if (Misc1.testLight(y, x)) {
							IO.printMessage("You have found something!");
						}
					}
					Moria1.lightUpSpot(y, x);
				} else {
					Moria3.deleteObject(y, x);
				}
			}
			if (c_ptr.creatureIndex > 1) {
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				r_ptr = Monsters.creatureList[m_ptr.index];
				if ((Constants.CD_STONE & r_ptr.cdefense) > 0) {
					m_name = getMonsterName(m_ptr, r_ptr);
					i = Moria3.monsterTakeHit(c_ptr.creatureIndex, 100);
					/* Should get these messages even if the monster is not
					   visible.  */
					if (i >= 0) {
						Variable.creatureRecall[i].cdefense |= Constants.CD_STONE;
						out_val = String.format("%s dissolves!", m_name);
						IO.printMessage(out_val);
						Misc3.printExperience(); /* print msg before calling prt_exp */
					} else {
						Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_STONE;
						out_val = String.format("%s grunts in pain!", m_name);
						IO.printMessage(out_val);
					}
					flag = true;
				}
			}
		} while (!flag);
		return wall;
	}
	
	/* Destroy all traps and doors in a given direction	-RAK-	*/
	public static boolean destroyTrapsAndDoors(int dir, int y, int x) {
		boolean destroy2;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		InvenType t_ptr;
		
		destroy2 = false;
		dist= 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			/* must move into first closed spot, as it might be a secret door */
			if (c_ptr.treasureIndex != 0) {
				t_ptr = Treasure.treasureList[c_ptr.treasureIndex];
				if ((t_ptr.category == Constants.TV_INVIS_TRAP) || (t_ptr.category == Constants.TV_CLOSED_DOOR)
						|| (t_ptr.category == Constants.TV_VIS_TRAP) || (t_ptr.category == Constants.TV_OPEN_DOOR)
						|| (t_ptr.category == Constants.TV_SECRET_DOOR)) {
					if (Moria3.deleteObject(y1.value(), x1.value())) {
						IO.printMessage("There is a bright flash of light!");
						destroy2 = true;
					}
				} else if (t_ptr.category == Constants.TV_CHEST && t_ptr.flags != 0) {
					IO.printMessage("Click!");
					t_ptr.flags &= ~(Constants.CH_TRAPPED|Constants.CH_LOCKED);
					destroy2 = true;
					t_ptr.specialName = Constants.SN_UNLOCKED;
					Desc.identifyItemPlusses(t_ptr);
				}
			}
		} while ((dist <= Constants.OBJ_BOLT_RANGE) || c_ptr.fval <= Constants.MAX_OPEN_SPACE);
		return destroy2;
	}
	
	/* Polymorph a monster					-RAK-	*/
	/* NOTE: cannot polymorph a winning creature (BALROG)		 */
	public static boolean polymorphMonster(int dir, int y, int x) {
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean flag, poly;
		CaveType c_ptr;
		CreatureType r_ptr;
		MonsterType m_ptr;
		String out_val, m_name;
		
		poly = false;
		flag = false;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				r_ptr = Monsters.creatureList[m_ptr.index];
				if (Rnd.randomInt(Constants.MAX_MONS_LEVEL) > r_ptr.level) {
					flag = true;
					Moria3.deleteMonster(c_ptr.creatureIndex);
					/* Place_monster() should always return true here.  */
					poly = Misc1.placeMonster(y1.value(), x1.value(), Rnd.randomInt(Monsters.monsterLevel[Constants.MAX_MONS_LEVEL] - Monsters.monsterLevel[0]) - 1 + Monsters.monsterLevel[0], false);
					/* don't test c_ptr.fm here, only pl/tl */
					if (poly && Misc1.panelContains(y, x) && (c_ptr.tempLight || c_ptr.permLight)) {
						poly = true;
					}
				} else {
					m_name = getMonsterName(m_ptr, r_ptr);
					out_val = String.format("%s is unaffected.", m_name);
					IO.printMessage(out_val);
				}
			}
		} while (!flag);
		return poly;
	}
	
	/* Create a wall.					-RAK-	*/
	public static boolean buildWall(int dir, int y, int x) {
		int i = 0;
		int damage, dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean build, flag;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String m_name, out_val;
		
		build = false;
		dist = 0;
		flag = false;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else {
				if (c_ptr.treasureIndex != 0) {
					Moria3.deleteObject(y1.value(), x1.value());
				}
				if (c_ptr.creatureIndex > 1) {
					/* stop the wall building */
					flag = true;
					m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
					r_ptr = Monsters.creatureList[m_ptr.index];
					
					if ((r_ptr.cmove & Constants.CM_PHASE) == 0) {
						/* monster does not move, can't escape the wall */
						if ((r_ptr.cmove & Constants.CM_ATTACK_ONLY) != 0) {
							damage = 3000; /* this will kill everything */
						} else {
							damage = Misc1.damageRoll(4, 8);
						}
						
						m_name = getMonsterName(m_ptr, r_ptr);
						out_val = String.format("%s wails out in pain!", m_name);
						IO.printMessage(out_val);
						i = Moria3.monsterTakeHit(c_ptr.creatureIndex, damage);
						if (i >= 0) {
							out_val = String.format("%s is embedded in the rock.", m_name);
							IO.printMessage(out_val);
							Misc3.printExperience();
						}
					} else if (r_ptr.cchar == 'E' || r_ptr.cchar == 'X') {
						/* must be an earth elemental or an earth spirit, or a Xorn
						 * increase its hit points */
						m_ptr.hitpoints += Misc1.damageRoll(4, 8);
					}
				}
				c_ptr.fval = Constants.MAGMA_WALL;
				c_ptr.fieldMark = false;
				/* Permanently light this wall if it is lit by player's lamp.  */
				c_ptr.permLight = (c_ptr.tempLight || c_ptr.permLight);
				Moria1.lightUpSpot(y1.value(), x1.value());
				i++;
				build = true;
			}
		} while (!flag);
		return build;
	}
	
	/* Replicate a creature					-RAK-	*/
	public static boolean cloneMonster(int dir, int y, int x) {
		CaveType c_ptr;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		boolean flag;
		
		dist = 0;
		flag = false;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				Monsters.monsterList[c_ptr.creatureIndex].sleep = 0;
				/* monptr of 0 is safe here, since can't reach here from creatures */
				return Creature.multiplyMonster(y1.value(), x1.value(), Monsters.monsterList[c_ptr.creatureIndex].index, 0);
			}
		} while (!flag);
		return false;
	}
	
	/* Move the creature record to a new location		-RAK-	*/
	public static void teleportMonsterAway(int monptr, int dis) {
		int yn, xn, ctr;
		MonsterType m_ptr;
		
		m_ptr = Monsters.monsterList[monptr];
		ctr = 0;
		do {
			do {
				yn = m_ptr.y + (Rnd.randomInt(2 * dis + 1) - (dis + 1));
				xn = m_ptr.x + (Rnd.randomInt(2 * dis + 1) - (dis + 1));
			} while (!Misc1.isInBounds(yn, xn));
			ctr++;
			if (ctr > 9) {
				ctr = 0;
				dis += 5;
			}
		} while ((Variable.cave[yn][xn].fval >= Constants.MIN_CLOSED_SPACE) || (Variable.cave[yn][xn].creatureIndex != 0));
		Moria1.moveCreatureRecord(m_ptr.y, m_ptr.x, yn, xn);
		Moria1.lightUpSpot(m_ptr.y, m_ptr.x);
		m_ptr.y = yn;
		m_ptr.x = xn;
		/* this is necessary, because the creature is not currently visible
	     in its new position */
		m_ptr.monsterLight = false;
		m_ptr.currDistance = Misc1.distance(Player.y, Player.x, yn, xn);
		Creature.updateMonster(monptr);
	}
	
	/* Teleport player to spell casting creature		-RAK-	*/
	public static void teleportPlayerTo(int ny, int nx) {
		int dis, ctr, y, x;
		int i, j;
		CaveType c_ptr;
		
		dis = 1;
		ctr = 0;
		do {
			y = ny + (Rnd.randomInt(2 * dis + 1) - (dis + 1));
			x = nx + (Rnd.randomInt(2 * dis + 1) - (dis + 1));
			ctr++;
			if (ctr > 9) {
				ctr = 0;
				dis++;
			}
		} while (!Misc1.isInBounds(y, x) || (Variable.cave[y][x].fval >= Constants.MIN_CLOSED_SPACE) || (Variable.cave[y][x].creatureIndex >= 2));
		Moria1.moveCreatureRecord(Player.y, Player.x, y, x);
		for (i = Player.y - 1; i <= Player.y + 1; i++) {
			for (j = Player.x - 1; j <= Player.x + 1; j++) {
				c_ptr = Variable.cave[i][j];
				c_ptr.tempLight = false;
				Moria1.lightUpSpot(i, j);
			}
		}
		Moria1.lightUpSpot(Player.y, Player.x);
		Player.y = y;
		Player.x = x;
		Misc4.checkView();
		/* light creatures */
		Creature.creatures(false);
	}
	
	/* Teleport all creatures in a given direction away	-RAK-	*/
	public static boolean teleportMonsters(int dir, int y, int x) {
		boolean flag, result;
		int dist;
		IntPointer y1 = new IntPointer(y), x1 = new IntPointer(x);
		CaveType c_ptr;
		
		flag = false;
		result = false;
		dist = 0;
		do {
			Misc3.moveMonster(dir, y1, x1);
			dist++;
			c_ptr = Variable.cave[y1.value()][x1.value()];
			if ((dist > Constants.OBJ_BOLT_RANGE) || c_ptr.fval >= Constants.MIN_CLOSED_SPACE) {
				flag = true;
			} else if (c_ptr.creatureIndex > 1) {
				Monsters.monsterList[c_ptr.creatureIndex].sleep = 0; /* wake it up */
				teleportMonsterAway(c_ptr.creatureIndex, Constants.MAX_SIGHT);
				result = true;
			}
		} while (!flag);
		return result;
	}
	
	/* Delete all creatures within max_sight distance	-RAK-	*/
	/* NOTE : Winning creatures cannot be genocided			 */
	public static boolean massGenocide() {
		int i;
		boolean result;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		result = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			r_ptr = Monsters.creatureList[m_ptr.index];
			if ((m_ptr.currDistance <= Constants.MAX_SIGHT) && ((r_ptr.cmove & Constants.CM_WIN) == 0)) {
				Moria3.deleteMonster(i);
				result = true;
			}
		}
		return result;
	}
	
	/* Delete all creatures of a given type from level.	-RAK-	*/
	/* This does not keep creatures of type from appearing later.	 */
	/* NOTE : Winning creatures can not be genocided. */
	public static boolean genocide() {
		int i;
		boolean killed;
		CharPointer typ = new CharPointer();
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val;
		
		killed = false;
		if (IO.getCommand("Which type of creature do you wish exterminated?", typ)) {
			for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
				m_ptr = Monsters.monsterList[i];
				r_ptr = Monsters.creatureList[m_ptr.index];
				if (typ.value() == Monsters.creatureList[m_ptr.index].cchar) {
					if ((r_ptr.cmove & Constants.CM_WIN) == 0) {
						Moria3.deleteMonster(i);
						killed = true;
					} else {
						/* genocide is a powerful spell, so we will let the player
						 * know the names of the creatures he did not destroy,
						 * this message makes no sense otherwise */
						out_val = String.format("The %s is unaffected.", r_ptr.name);
						IO.printMessage(out_val);
					}
				}
			}
		}
		return killed;
	}
	
	/* Change speed of any creature .			-RAK-	*/
	/* NOTE: cannot slow a winning creature (BALROG)		 */
	public static boolean speedMonsters(int spd) {
		int i;
		boolean speed;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		speed = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			r_ptr = Monsters.creatureList[m_ptr.index];
			m_name = getMonsterName(m_ptr, r_ptr);
			
			if ((m_ptr.currDistance > Constants.MAX_SIGHT) || !Misc1.isInLineOfSight(Player.y, Player.x, m_ptr.y, m_ptr.x)) {
				/* do nothing */
				;
			} else if (spd > 0) {
				m_ptr.speed += spd;
				m_ptr.sleep = 0;
				if (m_ptr.monsterLight) {
					speed = true;
					out_val = String.format("%s starts moving faster.", m_name);
					IO.printMessage (out_val);
				}
			} else if (Rnd.randomInt(Constants.MAX_MONS_LEVEL) > r_ptr.level) {
				m_ptr.speed += spd;
				m_ptr.sleep = 0;
				if (m_ptr.monsterLight) {
					out_val = String.format("%s starts moving slower.", m_name);
					IO.printMessage(out_val);
					speed = true;
				}
			} else if (m_ptr.monsterLight) {
				m_ptr.sleep = 0;
				out_val = String.format("%s is unaffected.", m_name);
				IO.printMessage(out_val);
			}
		}
		return speed;
	}
	
	/* Sleep any creature .		-RAK-	*/
	public static boolean sleepMonsters() {
		int i;
		boolean sleep;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		sleep = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			r_ptr = Monsters.creatureList[m_ptr.index];
			m_name = getMonsterName(m_ptr, r_ptr);
			if ((m_ptr.currDistance > Constants.MAX_SIGHT) || !Misc1.isInLineOfSight(Player.y, Player.x, m_ptr.y, m_ptr.x)) {
				/* do nothing */
				;
			} else if ((Rnd.randomInt(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) != 0) {
				if (m_ptr.monsterLight) {
					if ((r_ptr.cdefense & Constants.CD_NO_SLEEP) != 0) {
						Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_NO_SLEEP;
					}
					out_val = String.format("%s is unaffected.", m_name);
					IO.printMessage(out_val);
				}
			} else {
				m_ptr.sleep = 500;
				if (m_ptr.monsterLight) {
					out_val = String.format("%s falls asleep.", m_name);
					IO.printMessage(out_val);
					sleep = true;
				}
			}
		}
		return sleep;
	}
	
	/* Polymorph any creature that player can see.	-RAK-	*/
	/* NOTE: cannot polymorph a winning creature (BALROG)		 */
	public static boolean massPolymorph() {
		int i;
		int y, x;
		boolean mass;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		mass = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			if (m_ptr.currDistance <= Constants.MAX_SIGHT) {
				r_ptr = Monsters.creatureList[m_ptr.index];
				if ((r_ptr.cmove & Constants.CM_WIN) == 0) {
					y = m_ptr.y;
					x = m_ptr.x;
					Moria3.deleteMonster(i);
					/* Place_monster() should always return true here.  */
					mass = Misc1.placeMonster(y, x, Rnd.randomInt(Monsters.monsterLevel[Constants.MAX_MONS_LEVEL] - Monsters.monsterLevel[0]) - 1 + Monsters.monsterLevel[0], false);
				}
			}
		}
		return mass;
	}
	
	/* Display evil creatures on current panel		-RAK-	*/
	public static boolean detectEvil() {
		int i;
		boolean flag;
		MonsterType m_ptr;
		
		flag = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			if (Misc1.panelContains(m_ptr.y, m_ptr.x) && (Constants.CD_EVIL & Monsters.creatureList[m_ptr.index].cdefense) != 0) {
				m_ptr.monsterLight = true;
				/* works correctly even if hallucinating */
				IO.print(Monsters.creatureList[m_ptr.index].cchar, m_ptr.y, m_ptr.x);
				flag = true;
			}
		}
		if (flag) {
			IO.printMessage("You sense the presence of evil!");
			IO.printMessage("");
			/* must unlight every monster just lighted */
			Creature.creatures(false);
		}
		return flag;
	}
	
	/* Change players hit points in some manner		-RAK-	*/
	public static boolean changePlayerHitpoints(int num) {
		boolean res;
		PlayerMisc m_ptr;
		
		res = false;
		m_ptr = Player.py.misc;
		if (m_ptr.currHitpoints < m_ptr.maxHitpoints) {
			m_ptr.currHitpoints += num;
			if (m_ptr.currHitpoints > m_ptr.maxHitpoints) {
				m_ptr.currHitpoints = m_ptr.maxHitpoints;
				m_ptr.currHitpointsFraction = 0;
			}
			Misc3.printCurrentHitpoints();
			
			num = num / 5;
			if (num < 3) {
				if (num == 0) {
					IO.printMessage("You feel a little better.");
				} else {
					IO.printMessage("You feel better.");
				}
			} else {
				if (num < 7) {
					IO.printMessage("You feel much better.");
				} else {
					IO.printMessage("You feel very good.");
				}
			}
			res = true;
		}
		return res;
	}
	
	/* Cure players confusion				-RAK-	*/
	public static boolean cureConfusion() {
		boolean cure;
		PlayerFlags f_ptr;
		
		cure = false;
		f_ptr = Player.py.flags;
		if (f_ptr.confused > 1) {
			f_ptr.confused = 1;
			cure = true;
		}
		return cure;
	}
	
	/* Cure players blindness				-RAK-	*/
	public static boolean cureBlindness() {
		boolean cure;
		PlayerFlags f_ptr;
		
		cure = false;
		f_ptr = Player.py.flags;
		if (f_ptr.blind > 1) {
			f_ptr.blind = 1;
			cure = true;
		}
		return cure;
	}
	
	/* Cure poisoning					-RAK-	*/
	public static boolean curePoison() {
		boolean cure;
		PlayerFlags f_ptr;
		
		cure = false;
		f_ptr = Player.py.flags;
		if (f_ptr.poisoned > 1) {
			f_ptr.poisoned = 1;
			cure = true;
		}
		return cure;
	}
	
	/* Cure the players fear				-RAK-	*/
	public static boolean removeFear() {
		boolean result;
		PlayerFlags f_ptr;
		
		result = false;
		f_ptr = Player.py.flags;
		if (f_ptr.afraid > 1) {
			f_ptr.afraid = 1;
			result = true;
		}
		return result;
	}
	
	/* This is a fun one.  In a given block, pick some walls and	*/
	/* turn them into open spots.  Pick some open spots and turn	 */
	/* them into walls.  An "Earthquake" effect.	       -RAK-   */
	public static void earthquake() {
		int i, j;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		int damage, tmp;
		String out_val, m_name;
		
		for (i = Player.y - 8; i <= Player.y + 8; i++) {
			for (j = Player.x - 8; j <= Player.x + 8; j++) {
				if (((i != Player.y) || (j != Player.x)) && Misc1.isInBounds(i, j) && (Rnd.randomInt(8) == 1)) {
					c_ptr = Variable.cave[i][j];
					if (c_ptr.treasureIndex != 0) {
						Moria3.deleteObject(i, j);
					}
					if (c_ptr.creatureIndex > 1) {
						m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
						r_ptr = Monsters.creatureList[m_ptr.index];
						
						if ((r_ptr.cmove & Constants.CM_PHASE) == 0) {
							if((r_ptr.cmove & Constants.CM_ATTACK_ONLY) != 0) {
								damage = 3000; /* this will kill everything */
							} else {
								damage = Misc1.damageRoll(4, 8);
							}
							
							m_name = getMonsterName(m_ptr, r_ptr);
							out_val = String.format("%s wails out in pain!", m_name);
							IO.printMessage (out_val);
							i = Moria3.monsterTakeHit(c_ptr.creatureIndex, damage);
							if (i >= 0) {
								out_val = String.format("%s is embedded in the rock.", m_name);
								IO.printMessage(out_val);
								Misc3.printExperience();
							}
						} else if (r_ptr.cchar == 'E' || r_ptr.cchar == 'X') {
							/* must be an earth elemental or an earth spirit, or a Xorn
							 * increase its hit points */
							m_ptr.hitpoints += Misc1.damageRoll(4, 8);
						}
					}
					
					if ((c_ptr.fval >= Constants.MIN_CAVE_WALL) && (c_ptr.fval != Constants.BOUNDARY_WALL)) {
						c_ptr.fval  = Constants.CORR_FLOOR;
						c_ptr.permLight = false;
						c_ptr.fieldMark = false;
					} else if (c_ptr.fval <= Constants.MAX_CAVE_FLOOR) {
						tmp = Rnd.randomInt(10);
						if (tmp < 6) {
							c_ptr.fval  = Constants.QUARTZ_WALL;
						} else if (tmp < 9) {
							c_ptr.fval  = Constants.MAGMA_WALL;
						} else {
							c_ptr.fval  = Constants.GRANITE_WALL;
						}
						
						c_ptr.fieldMark = false;
					}
					Moria1.lightUpSpot(i, j);
				}
			}
		}
	}
	
	/* Evil creatures don't like this.		       -RAK-   */
	public static boolean protectFromEvil() {
		boolean res;
		PlayerFlags f_ptr;
		
		f_ptr = Player.py.flags;
		if (f_ptr.protectFromEvil == 0) {
			res = true;
		} else {
			res = false;
		}
		f_ptr.protectFromEvil += Rnd.randomInt(25) + 3 * Player.py.misc.level;
		return res;
	}
	
	/* Create some high quality mush for the player.	-RAK-	*/
	public static void createFood() {
		CaveType c_ptr;
		
		c_ptr = Variable.cave[Player.y][Player.x];
		if (c_ptr.treasureIndex != 0) {
			/* take no action here, don't want to destroy object under player */
			IO.printMessage ("There is already an object under you.");
			/* set free_turn_flag so that scroll/spell points won't be used */
			Variable.freeTurnFlag = true;
		} else {
			Misc3.placeObject(Player.y, Player.x, false);
			Desc.copyIntoInventory(Treasure.treasureList[c_ptr.treasureIndex], Constants.OBJ_MUSH);
		}
	}
	
	/* Attempts to destroy a type of creature.  Success depends on	*/
	/* the creatures level VS. the player's level		 -RAK-	 */
	public static boolean dispelCreature(int cflag, int damage) {
		int i;
		int k;
		boolean dispel;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		dispel = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			if ((m_ptr.currDistance <= Constants.MAX_SIGHT) && (cflag & Monsters.creatureList[m_ptr.index].cdefense) != 0 && Misc1.isInLineOfSight(Player.y, Player.x, m_ptr.y, m_ptr.x)) {
				r_ptr = Monsters.creatureList[m_ptr.index];
				Variable.creatureRecall[m_ptr.index].cdefense |= cflag;
				m_name = getMonsterName(m_ptr, r_ptr);
				k = Moria3.monsterTakeHit(i, Rnd.randomInt(damage));
				/* Should get these messages even if the monster is not
				 * visible.  */
				if (k >= 0) {
					out_val = String.format("%s dissolves!", m_name);
				} else {
					out_val = String.format("%s shudders.", m_name);
				}
				IO.printMessage(out_val);
				dispel = true;
				if (k >= 0) {
					Misc3.printExperience();
				}
			}
		}
		return dispel;
	}
	
	/* Attempt to turn (confuse) undead creatures.	-RAK-	*/
	public static boolean turnUndead() {
		int i;
		boolean turn_und;
		MonsterType m_ptr;
		CreatureType r_ptr;
		String out_val, m_name;
		
		turn_und = false;
		for (i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX; i--) {
			m_ptr = Monsters.monsterList[i];
			r_ptr = Monsters.creatureList[m_ptr.index];
			if ((m_ptr.currDistance <= Constants.MAX_SIGHT) && (Constants.CD_UNDEAD & r_ptr.cdefense) != 0 && (Misc1.isInLineOfSight(Player.y, Player.x, m_ptr.y, m_ptr.x))) {
				m_name = getMonsterName(m_ptr, r_ptr);
				if (((Player.py.misc.level + 1) > r_ptr.level) || (Rnd.randomInt(5) == 1)) {
					if (m_ptr.monsterLight) {
						out_val = String.format("%s runs frantically!", m_name);
						IO.printMessage(out_val);
						turn_und = true;
						Variable.creatureRecall[m_ptr.index].cdefense |= Constants.CD_UNDEAD;
					}
					m_ptr.confused = Player.py.misc.level;
				} else if (m_ptr.monsterLight) {
					out_val = String.format("%s is unaffected.", m_name);
					IO.printMessage(out_val);
				}
			}
		}
		return turn_und;
	}
	
	/* Leave a glyph of warding. Creatures will not pass over! -RAK-*/
	public static void wardingGlyph() {
		int i;
		CaveType c_ptr;
		
		c_ptr = Variable.cave[Player.y][Player.x];
		if (c_ptr.treasureIndex == 0) {
			i = Misc1.popTreasure();
			c_ptr.treasureIndex = i;
			Desc.copyIntoInventory(Treasure.treasureList[i], Constants.OBJ_SCARE_MON);
		}
	}
	
	/* Lose a strength point.				-RAK-	*/
	public static void loseStrength() {
		if (!Player.py.flags.sustainStr) {
			Misc3.decreaseStat(Constants.A_STR);
			IO.printMessage("You feel very sick.");
		} else {
			IO.printMessage("You feel sick for a moment,  it passes.");
		}
	}
	
	/* Lose an intelligence point.				-RAK-	*/
	public static void loseIntelligence() {
		if (!Player.py.flags.sustainInt) {
			Misc3.decreaseStat(Constants.A_INT);
			IO.printMessage("You become very dizzy.");
		} else {
			IO.printMessage("You become dizzy for a moment,  it passes.");
		}
	}
	
	/* Lose a wisdom point.					-RAK-	*/
	public static void loseWisdom() {
		if (!Player.py.flags.sustainWis) {
			Misc3.decreaseStat(Constants.A_WIS);
			IO.printMessage("You feel very naive.");
		} else {
			IO.printMessage("You feel naive for a moment,  it passes.");
		}
	}
	
	/* Lose a dexterity point.				-RAK-	*/
	public static void loseDexterity() {
		if (!Player.py.flags.sustainDex) {
			Misc3.decreaseStat(Constants.A_DEX);
			IO.printMessage("You feel very sore.");
		} else {
			IO.printMessage("You feel sore for a moment,  it passes.");
		}
	}
	
	/* Lose a constitution point.				-RAK-	*/
	public static void loseConstitution() {
		if (!Player.py.flags.sustainCon) {
			Misc3.decreaseStat(Constants.A_CON);
			IO.printMessage("You feel very sick.");
		} else {
			IO.printMessage("You feel sick for a moment,  it passes.");
		}
	}
	
	/* Lose a charisma point.				-RAK-	*/
	public static void loseCharisma() {
		if (!Player.py.flags.sustainChr) {
			Misc3.decreaseStat(Constants.A_CHR);
			IO.printMessage("Your skin starts to itch.");
		} else {
			IO.printMessage("Your skin starts to itch, but feels better now.");
		}
	}
	
	/* Lose experience					-RAK-	*/
	public static void loseExperience(int amount) {
		int i;
		PlayerMisc m_ptr;
		ClassType c_ptr;
		
		m_ptr = Player.py.misc;
		if (amount > m_ptr.currExp) {
			m_ptr.currExp = 0;
		} else {
			m_ptr.currExp -= amount;
		}
		Misc3.printExperience();
		
		i = 0;
		while ((Player.exp[i] * m_ptr.expFactor / 100) <= m_ptr.currExp) {
			i++;
		}
		/* increment i once more, because level 1 exp is stored in player_exp[0] */
		i++;
		
		if (m_ptr.level != i) {
			m_ptr.level = i;
			
			Misc3.calcHitpoints();
			c_ptr = Player.Class[m_ptr.playerClass];
			if (c_ptr.spell == Constants.MAGE) {
				Misc3.calcSpells(Constants.A_INT);
				Misc3.calcMana(Constants.A_INT);
			} else if (c_ptr.spell == Constants.PRIEST) {
				Misc3.calcSpells(Constants.A_WIS);
				Misc3.calcMana(Constants.A_WIS);
			}
			Misc3.printLevel();
			Misc3.printPlayerTitle();
		}
	}
	
	/* Slow Poison						-RAK-	*/
	public static boolean slowPoison() {
		boolean slow;
		PlayerFlags f_ptr;
		
		slow = false;
		f_ptr = Player.py.flags;
		if (f_ptr.poisoned > 0) {
			f_ptr.poisoned = f_ptr.poisoned / 2;
			if (f_ptr.poisoned < 1)	f_ptr.poisoned = 1;
			slow = true;
			IO.printMessage("The effect of the poison has been reduced.");
		}
		return slow;
	}
	
	/* Bless						-RAK-	*/
	public static void bless(int amount) {
		Player.py.flags.blessed += amount;
	}
	
	/* Detect Invisible for period of time			-RAK-	*/
	public static void detectInvisibleMonsters(int amount) {
		Player.py.flags.detectInvisible += amount;
	}
	
	public static void replaceSpot(int y, int x, int typ) {
		CaveType c_ptr;
		
		c_ptr = Variable.cave[y][x];
		switch(typ) {
		case 1: case 2: case 3:
			c_ptr.fval  = Constants.CORR_FLOOR;
			break;
		case 4: case 7: case 10:
			c_ptr.fval  = Constants.GRANITE_WALL;
			break;
		case 5: case 8: case 11:
			c_ptr.fval  = Constants.MAGMA_WALL;
			break;
		case 6: case 9: case 12:
			c_ptr.fval  = Constants.QUARTZ_WALL;
			break;
		default:
			break;
		}
		c_ptr.permLight = false;
		c_ptr.fieldMark = false;
		c_ptr.litRoom = false;  /* this is no longer part of a room */
		if (c_ptr.treasureIndex != 0) {
			Moria3.deleteObject(y, x);
		}
		if (c_ptr.creatureIndex > 1) {
			Moria3.deleteMonster(c_ptr.creatureIndex);
		}
	}
	
	/* The spell of destruction.				-RAK-	*/
	/* NOTE : Winning creatures that are deleted will be considered	 */
	/*	  as teleporting to another level.  This will NOT win the */
	/*	  game.						       */
	public static void destroyArea(int y, int x) {
		int i, j, k;
		
		if (Variable.dungeonLevel > 0) {
			for (i = (y - 15); i <= (y + 15); i++) {
				for (j = (x - 15); j <= (x + 15); j++) {
					if (Misc1.isInBounds(i, j) && (Variable.cave[i][j].fval != Constants.BOUNDARY_WALL)) {
						k = Misc1.distance(i, j, y, x);
						if (k == 0) {	/* clear player's spot, but don't put wall there */
							replaceSpot(i, j, 1);
						} else if (k < 13) {
							replaceSpot(i, j, Rnd.randomInt(6));
						} else if (k < 16) {
							replaceSpot(i, j, Rnd.randomInt(9));
						}
					}
				}
			}
		}
		IO.printMessage("There is a searing blast of light!");
		Player.py.flags.blind += 10 + Rnd.randomInt(10);
	}
	
	/* Enchants a plus onto an item.			-RAK-	*/
	public static boolean enchant(IntPointer plusses, int limit) {
		/* limit is the maximum bonus allowed; usually 10, but weapon's maximum damage
		 * when enchanting melee weapons to damage */
		
		int chance;
		boolean res;
		
		if (limit <= 0) {	/* avoid randint(0) call */
			return false;
		}
		chance = 0;
		res = false;
		if (plusses.value() > 0) {
			chance = plusses.value();
			if (Rnd.randomInt(100) == 1) {	/* very rarely allow enchantment over limit */
				chance = Rnd.randomInt(chance) - 1;
			}
		}
		if (Rnd.randomInt(limit) > chance) {
			plusses.value(plusses.value() + 1);
			res = true;
		}
		return res;
	}
	
	/* Removes curses from items in inventory		-RAK-	*/
	public static boolean removeCurse() {
		int i;
		boolean result;
		InvenType i_ptr;
		
		result = false;
		for (i = Constants.INVEN_WIELD; i <= Constants.INVEN_OUTER; i++) {
			i_ptr = Treasure.inventory[i];
			if ((Constants.TR_CURSED & i_ptr.flags) != 0) {
				i_ptr.flags &= ~Constants.TR_CURSED;
				Moria1.calcBonuses();
				result = true;
			}
		}
		return result;
	}
	
	/* Restores any drained experience			-RAK-	*/
	public static boolean restoreLevel() {
		boolean restore;
		PlayerMisc m_ptr;
		
		restore = false;
		m_ptr = Player.py.misc;
		if (m_ptr.maxExp > m_ptr.currExp) {
			restore = true;
			IO.printMessage("You feel your life energies returning.");
			/* this while loop is not redundant, ptr_exp may reduce the exp level */
			while (m_ptr.currExp < m_ptr.maxExp) {
				m_ptr.currExp = m_ptr.maxExp;
				Misc3.printExperience();
			}
		}
		return restore;
	}
}
