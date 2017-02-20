/*
 * Moria3.java: misc code, mainly to handle player commands
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
import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;
import net.jonhopkins.moria.types.SpellType;

public class Moria3 {
	
	private Moria3() { }
	
	/* Player hit a trap.	(Chuckle)			-RAK-	*/
	public static void tripTrap(int y, int x) {
		int i, num, dam;
		IntPointer ty, tx;
		CaveType c_ptr;
		PlayerMisc p_ptr;
		InvenType t_ptr;
		String tmp;
		
		Moria2.endFind();
		Moria2.revealTrap(y, x);
		c_ptr = Variable.cave[y][x];
		p_ptr = Player.py.misc;
		t_ptr = Treasure.treasureList[c_ptr.treasureIndex];
		dam = Misc1.pDamageRoll(t_ptr.damage);
		switch (t_ptr.subCategory) {
		case 1:  /* Open pit*/
			IO.printMessage("You fell into a pit!");
			if (Player.py.flags.freeFall > 0) {
				IO.printMessage("You gently float down.");
			} else {
				tmp = Desc.describeObject(t_ptr, true);
				Moria1.takeHit(dam, tmp);
			}
			break;
		case 2: /* Arrow trap*/
			if (Moria1.testHit(125, 0, 0, p_ptr.totalArmorClass+p_ptr.magicArmorClass, Constants.CLA_MISC_HIT)) {
				tmp = Desc.describeObject(t_ptr, true);
				Moria1.takeHit(dam, tmp);
				IO.printMessage("An arrow hits you.");
			} else {
				IO.printMessage("An arrow barely misses you.");
			}
			break;
		case 3: /* Covered pit*/
			IO.printMessage("You fell into a covered pit.");
			if (Player.py.flags.freeFall > 0) {
				IO.printMessage("You gently float down.");
			} else {
				tmp = Desc.describeObject(t_ptr, true);
				Moria1.takeHit(dam, tmp);
			}
			Misc3.placeTrap(y, x, 0);
			break;
		case 4: /* Trap door*/
			IO.printMessage("You fell through a trap door!");
			Variable.newLevelFlag = true;
			Variable.dungeonLevel++;
			if (Player.py.flags.freeFall > 0) {
				IO.printMessage("You gently float down.");
			} else {
				tmp = Desc.describeObject(t_ptr, true);
				Moria1.takeHit(dam, tmp);
			}
			/* Force the messages to display before starting to generate the
			 * next level.  */
			IO.printMessage("");
			break;
		case 5: /* Sleep gas*/
			if (Player.py.flags.paralysis == 0) {
				IO.printMessage("A strange white mist surrounds you!");
				if (Player.py.flags.freeAct) {
					IO.printMessage("You are unaffected.");
				} else {
					IO.printMessage("You fall asleep.");
					Player.py.flags.paralysis += Misc1.randomInt(10) + 4;
				}
			}
			break;
		case 6: /* Hid Obj*/
			deleteObject(y, x);
			Misc3.placeObject(y, x, false);
			IO.printMessage("Hmmm, there was something under this rock.");
			break;
		case 7:  /* STR Dart*/
			if (Moria1.testHit(125, 0, 0, p_ptr.totalArmorClass+p_ptr.magicArmorClass, Constants.CLA_MISC_HIT)) {
				if (!Player.py.flags.sustainStr) {
					Misc3.decreaseStat(Constants.A_STR);
					tmp = Desc.describeObject(t_ptr, true);
					Moria1.takeHit(dam, tmp);
					IO.printMessage("A small dart weakens you!");
				} else {
					IO.printMessage("A small dart hits you.");
				}
			} else {
				IO.printMessage("A small dart barely misses you.");
			}
			break;
		case 8: /* Teleport*/
			Variable.teleportFlag = true;
			IO.printMessage("You hit a teleport trap!");
			/* Light up the teleport trap, before we teleport away.  */
			Moria1.moveLight(y, x, y, x);
			break;
		case 9: /* Rockfall*/
			Moria1.takeHit(dam, "a falling rock");
			deleteObject(y, x);
			Misc3.placeRubble(y, x);
			IO.printMessage("You are hit by falling rock.");
			break;
		case 10: /* Corrode gas*/
			/* Makes more sense to print the message first, then damage an
		 object.  */
			IO.printMessage("A strange red gas surrounds you.");
			Moria2.corrodeGas("corrosion gas");
			break;
		case 11: /* Summon mon*/
			deleteObject(y, x);	/* Rune disappears.    */
			num = 2 + Misc1.randomInt (3);
			for (i = 0; i < num; i++) {
				ty = new IntPointer(y);
				tx = new IntPointer(x);
				Misc1.summonMonster(ty, tx, false);
			}
			break;
		case 12: /* Fire trap*/
			IO.printMessage("You are enveloped in flames!");
			Moria2.fireDamage(dam, "a fire trap");
			break;
		case 13: /* Acid trap*/
			IO.printMessage("You are splashed with acid!");
			Moria2.acidDamage(dam, "an acid trap");
			break;
		case 14: /* Poison gas*/
			IO.printMessage("A pungent green gas surrounds you!");
			Moria2.poisonGas(dam, "a poison gas trap");
			break;
		case 15: /* Blind Gas */
			IO.printMessage("A black gas surrounds you!");
			Player.py.flags.blind += Misc1.randomInt(50) + 50;
			break;
		case 16: /* Confuse Gas*/
			IO.printMessage("A gas of scintillating colors surrounds you!");
			Player.py.flags.confused += Misc1.randomInt(15) + 15;
			break;
		case 17: /* Slow Dart*/
			if (Moria1.testHit(125, 0, 0, p_ptr.totalArmorClass+p_ptr.magicArmorClass, Constants.CLA_MISC_HIT)) {
				tmp = Desc.describeObject(t_ptr, true);
				Moria1.takeHit(dam, tmp);
				IO.printMessage("A small dart hits you!");
				if (Player.py.flags.freeAct) {
					IO.printMessage("You are unaffected.");
				} else {
					Player.py.flags.slow += Misc1.randomInt(20) + 10;
				}
			} else {
				IO.printMessage("A small dart barely misses you.");
			}
			break;
		case 18: /* CON Dart*/
			if (Moria1.testHit(125, 0, 0, p_ptr.totalArmorClass+p_ptr.magicArmorClass, Constants.CLA_MISC_HIT)) {
				if (!Player.py.flags.sustainCon) {
					Misc3.decreaseStat(Constants.A_CON);
					tmp = Desc.describeObject(t_ptr, true);
					Moria1.takeHit(dam, tmp);
					IO.printMessage("A small dart saps your health!");
				} else {
					IO.printMessage("A small dart hits you.");
				}
			} else {
				IO.printMessage("A small dart barely misses you.");
			}
			break;
		case 19: /*Secret Door*/
			break;
		case 99: /* Scare Mon*/
			break;
			/* Town level traps are special,	the stores.	*/
		case 101: /* General    */
			Store2.enterStore(0);
			break;
		case 102: /* Armory	    */
			Store2.enterStore(1);
			break;
		case 103: /* Weaponsmith*/
			Store2.enterStore(2);
			break;
		case 104: /* Temple	    */
			Store2.enterStore(3);
			break;
		case 105: /* Alchemy    */
			Store2.enterStore(4);
			break;
		case 106: /* Magic-User */
			Store2.enterStore(5);
			break;
		default:
			IO.printMessage("Unknown trap value.");
			break;
		}
	}
	
	/* Return spell number and failure chance		-RAK-	*/
	/* returns -1 if no spells in book
	 * returns 1 if choose a spell in book to cast
	 * returns 0 if don't choose a spell, i.e. exit with an escape */
	public static int castSpell(String prompt, int item_val, IntPointer sn, IntPointer sc) {
		IntPointer j;
		int i, k;
		int[] spell = new int[31];
		int result, first_spell;
		SpellType[] s_ptr;
		
		result = -1;
		i = 0;
		j = new IntPointer(Treasure.inventory[item_val].flags);
		first_spell = Misc1.firstBitPos(j);
		/* set j again, since bit_pos modified it */
		j.value(Treasure.inventory[item_val].flags & Player.spellLearned);
		s_ptr = Player.magicSpell[Player.py.misc.playerClass - 1];
		while (j.value() != 0) {
			k = Misc1.firstBitPos(j);
			if (s_ptr[k].level <= Player.py.misc.level) {
				spell[i] = k;
				i++;
			}
		}
		if (i > 0) {
			result = Misc3.getSpell(spell, i, sn, sc, prompt, first_spell) ? 1 : 0;
			if (result != 0 && Player.magicSpell[Player.py.misc.playerClass - 1][sn.value()].manaCost > Player.py.misc.currMana) {
				if (Player.Class[Player.py.misc.playerClass].spell == Constants.MAGE) {
					result = IO.getCheck("You summon your limited strength to cast this one! Confirm?") ? 1 : 0;
				} else {
					result = IO.getCheck("The gods may think you presumptuous for this! Confirm?") ? 1 : 0;
				}
			}
		}
		return result;
	}
	
	/* Player is on an object.  Many things can happen based -RAK-	*/
	/* on the TVAL of the object.  Traps are set off, money and most */
	/* objects are picked up.  Some objects, such as open doors, just*/
	/* sit there.						       */
	public static void carry(int y, int x, boolean pickup) {
		int locn, i;
		String out_val;
		StringBuilder tmp_str;
		CaveType c_ptr;
		InvenType i_ptr;
		
		c_ptr = Variable.cave[y][x];
		i_ptr = Treasure.treasureList[c_ptr.treasureIndex];
		i = Treasure.treasureList[c_ptr.treasureIndex].category;
		if (i <= Constants.TV_MAX_PICK_UP) {
			Moria2.endFind();
			/* There's GOLD in them thar hills!      */
			if (i == Constants.TV_GOLD) {
				Player.py.misc.gold += i_ptr.cost;
				tmp_str = new StringBuilder().append(Desc.describeObject(i_ptr, true));
				out_val = String.format("You have found %d gold pieces worth of %s", i_ptr.cost, tmp_str);
				Misc3.printGold();
				deleteObject(y, x);
				IO.printMessage(out_val);
			} else {
				if (Misc3.canPickUpItem(i_ptr)) {	/* Too many objects?	    */
					/* Okay,  pick it up      */
					if (pickup && Variable.promptCarryFlag.value()) {
						tmp_str = new StringBuilder().append(Desc.describeObject(i_ptr, true));
						/* change the period to a question mark */
						tmp_str = new StringBuilder()
								.append(tmp_str.substring(0, tmp_str.length() - 1))
								.append('?');
						out_val = String.format("Pick up %s",
								tmp_str.toString());
						pickup = IO.getCheck(out_val);
					}
					/* Check to see if it will change the players speed. */
					if (pickup && !Misc3.checkItemWeight(i_ptr)) {
						tmp_str = new StringBuilder().append(Desc.describeObject(i_ptr, true));
						/* change the period to a question mark */
						tmp_str = new StringBuilder()
								.append(tmp_str.substring(0, tmp_str.length() - 1))
								.append('?');
						out_val = String.format("Exceed your weight limit to pick up %s",
								tmp_str.toString());
						pickup = IO.getCheck(out_val);
					}
					/* Attempt to pick up an object.	       */
					if (pickup) {
						locn = Misc3.pickUpItem(i_ptr);
						tmp_str = new StringBuilder().append(Desc.describeObject(Treasure.inventory[locn], true));
						out_val = String.format("You have %s (%c)", tmp_str.toString(), locn + 'a');
						IO.printMessage(out_val);
						deleteObject(y, x);
					}
				} else {
					tmp_str = new StringBuilder().append(Desc.describeObject(i_ptr, true));
					out_val = String.format("You can't carry %s", tmp_str.toString());
					IO.printMessage(out_val);
				}
			}
		
		/* OPPS!				   */
		} else if (i == Constants.TV_INVIS_TRAP || i == Constants.TV_VIS_TRAP || i == Constants.TV_STORE_DOOR) {
			tripTrap(y, x);
		}
	}
	
	/* Deletes a monster entry from the level		-RAK-	*/
	public static void deleteMonster(int monsterIndex) {
		MonsterType monster;
		
		monster = Monsters.monsterList[monsterIndex];
		Variable.cave[monster.y][monster.x].creatureIndex = 0;
		if (monster.monsterLight) {
			Moria1.lightUpSpot(monster.y, monster.x);
		}
		if (monsterIndex != Monsters.freeMonsterIndex - 1) {
			monster = Monsters.monsterList[Monsters.freeMonsterIndex - 1];
			Variable.cave[monster.y][monster.x].creatureIndex = monsterIndex;
			Monsters.monsterList[Monsters.freeMonsterIndex - 1].copyInto(Monsters.monsterList[monsterIndex]);
		}
		Monsters.freeMonsterIndex--;
		Monsters.getBlankMonster().copyInto(Monsters.monsterList[Monsters.freeMonsterIndex]);
		if (Monsters.totalMonsterMultiples > 0) {
			Monsters.totalMonsterMultiples--;
		}
	}
	
	/* The following two procedures implement the same function as delete monster.
	 * However, they are used within creatures(), because deleting a monster
	 * while scanning the m_list causes two problems, monsters might get two
	 * turns, and m_ptr/monptr might be invalid after the delete_monster.
	 * Hence the delete is done in two steps. */
	/* fix1_delete_monster does everything delete_monster does except delete
	 * the monster record and reduce mfptr, this is called in breathe, and
	 * a couple of places in creatures.c */
	public static void deleteMonster1(int j) {
		MonsterType m_ptr;
		
		m_ptr = Monsters.monsterList[j];
		/* force the hp negative to ensure that the monster is dead, for example,
		 * if the monster was just eaten by another, it will still have positive
		 * hit points */
		m_ptr.hitpoints = -1;
		Variable.cave[m_ptr.y][m_ptr.x].creatureIndex = 0;
		if (m_ptr.monsterLight) {
			Moria1.lightUpSpot(m_ptr.y, m_ptr.x);
		}
		if (Monsters.totalMonsterMultiples > 0) {
			Monsters.totalMonsterMultiples--;
		}
	}
	
	/* fix2_delete_monster does everything in delete_monster that wasn't done
	 * by fix1_monster_delete above, this is only called in creatures() */
	public static void deleteMonster2(int j) {
		MonsterType m_ptr;
		
		if (j != Monsters.freeMonsterIndex - 1) {
			m_ptr = Monsters.monsterList[Monsters.freeMonsterIndex - 1];
			Variable.cave[m_ptr.y][m_ptr.x].creatureIndex = j;
			Monsters.monsterList[Monsters.freeMonsterIndex - 1].copyInto(Monsters.monsterList[j]);
		}
		Monsters.getBlankMonster().copyInto(Monsters.monsterList[Monsters.freeMonsterIndex - 1]);
		Monsters.freeMonsterIndex--;
	}
	
	/* Creates objects nearby the coordinates given		-RAK-	  */
	public static int summonObject(int y, int x, int num, int typ) {
		int i, j, k;
		CaveType c_ptr;
		int real_typ, res;
		
		if (typ == 1 || typ == 5) {
			real_typ = 1; /* typ == 1 . objects */
		} else {
			real_typ = 256; /* typ == 2 . gold */
		}
		res = 0;
		do {
			i = 0;
			do {
				j = y - 3 + Misc1.randomInt(5);
				k = x - 3 + Misc1.randomInt(5);
				if (Misc1.isInBounds(j, k) && Misc1.isInLineOfSight(y, x, j, k)) {
					c_ptr = Variable.cave[j][k];
					if (c_ptr.fval <= Constants.MAX_OPEN_SPACE && (c_ptr.treasureIndex == 0)) {
						if (typ == 3 || typ == 7) {
							/* typ == 3 . 50% objects, 50% gold */
							if (Misc1.randomInt(100) < 50) {
								real_typ = 1;
							} else {
								real_typ = 256;
							}
						}
						if (real_typ == 1) {
							Misc3.placeObject(j, k, (typ >= 4));
						} else {
							Misc3.placeGold(j, k);
						}
						Moria1.lightUpSpot(j, k);
						if (Misc1.testLight(j, k)) {
							res += real_typ;
						}
						i = 20;
					}
				}
				i++;
			} while (i <= 20);
			num--;
		} while (num != 0);
		return res;
	}
	
	/* Deletes object from given location			-RAK-	*/
	public static boolean deleteObject(int y, int x) {
		boolean delete;
		CaveType c_ptr;
		
		c_ptr = Variable.cave[y][x];
		if (c_ptr.fval == Constants.BLOCKED_FLOOR) {
			c_ptr.fval = Constants.CORR_FLOOR;
		}
		Misc1.pusht(c_ptr.treasureIndex);
		c_ptr.treasureIndex = 0;
		c_ptr.fieldMark = false;
		Moria1.lightUpSpot(y, x);
		if (Misc1.testLight(y, x)) {
			delete = true;
		} else {
			delete = false;
		}
		return delete;
	}
	
	/* Allocates objects upon a creatures death		-RAK-	*/
	/* Oh well,  another creature bites the dust.  Reward the victor*/
	/* based on flags set in the main creature record		 */
	/* Returns a mask of bits from the given flags which indicates what the
	 * monster is seen to have dropped.  This may be added to monster memory. */
	public static int monsterDeath(int y, int x, long flags) {
		int i, number;
		int dump, res;
		
		if ((flags & Constants.CM_CARRY_OBJ) != 0) {
			i = 1;
		} else {
			i = 0;
		}
		if ((flags & Constants.CM_CARRY_GOLD) != 0) {
			i += 2;
		}
		if ((flags & Constants.CM_SMALL_OBJ) != 0) {
			i += 4;
		}
		
		number = 0;
		if ((flags & Constants.CM_60_RANDOM) != 0 && (Misc1.randomInt(100) < 60)) {
			number++;
		}
		if ((flags & Constants.CM_90_RANDOM) != 0 && (Misc1.randomInt(100) < 90)) {
			number++;
		}
		if ((flags & Constants.CM_1D2_OBJ) != 0) {
			number += Misc1.randomInt(2);
		}
		if ((flags & Constants.CM_2D2_OBJ) != 0) {
			number += Misc1.damageRoll(2, 2);
		}
		if ((flags & Constants.CM_4D2_OBJ) != 0) {
			number += Misc1.damageRoll(4, 2);
		}
		if (number > 0) {
			dump = summonObject(y, x, number, i);
		} else {
			dump = 0;
		}
		
		if ((flags & Constants.CM_WIN) != 0) {
			if (!Variable.death) {
				Variable.isTotalWinner = true;
				Misc3.printWinner();
				IO.printMessage("*** CONGRATULATIONS *** You have won the game.");
				IO.printMessage("You cannot save this game, but you may retire when ready.");
			}
		}
		
		if (dump != 0) {
			res = 0;
			if ((dump & 255) != 0) {
				res |= Constants.CM_CARRY_OBJ;
				if ((i & 0x04) != 0) {
					res |= Constants.CM_SMALL_OBJ;
				}
			}
			if (dump >= 256) {
				res |= Constants.CM_CARRY_GOLD;
			}
			dump = (dump % 256) + (dump / 256);  /* number of items */
			res |= dump << Constants.CM_TR_SHIFT;
		} else {
			res = 0;
		}
		
		return res;
	}
	
	/* Decreases monsters hit points and deletes monster if needed.	*/
	/* (Picking on my babies.)			       -RAK-   */
	public static int monsterTakeHit(int monptr, int dam) {
		int i;
		int new_exp, new_exp_frac;
		MonsterType m_ptr;
		PlayerMisc p_ptr;
		CreatureType c_ptr;
		int m_take_hit;
		int tmp;
		
		m_ptr = Monsters.monsterList[monptr];
		m_ptr.hitpoints -= dam;
		m_ptr.sleep = 0;
		if (m_ptr.hitpoints < 0) {
			i = monsterDeath(m_ptr.y, m_ptr.x, Monsters.creatureList[m_ptr.index].cmove);
			if ((Player.py.flags.blind < 1 && m_ptr.monsterLight) || (Monsters.creatureList[m_ptr.index].cmove & Constants.CM_WIN) != 0) {
				tmp = (Variable.creatureRecall[m_ptr.index].cmove & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT;
				if (tmp > ((i & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT)) {
					i = (i & ~Constants.CM_TREASURE) | (tmp << Constants.CM_TR_SHIFT);
				}
				Variable.creatureRecall[m_ptr.index].cmove = (Variable.creatureRecall[m_ptr.index].cmove & ~Constants.CM_TREASURE) | i;
				if (Variable.creatureRecall[m_ptr.index].kills < Constants.MAX_SHORT) {
					Variable.creatureRecall[m_ptr.index].kills++;
				}
			}
			c_ptr = Monsters.creatureList[m_ptr.index];
			p_ptr = Player.py.misc;
			
			new_exp = (c_ptr.mexp * c_ptr.level) / p_ptr.level;
			new_exp_frac = (((c_ptr.mexp * c_ptr.level) % p_ptr.level) * 0x10000 / p_ptr.level) + p_ptr.expFraction;
			if (new_exp_frac >= 0x10000) {
				new_exp++;
				p_ptr.expFraction = new_exp_frac - 0x10000;
			} else {
				p_ptr.expFraction = new_exp_frac;
			}
			
			p_ptr.currExp += new_exp;
			/* can't call prt_experience() here, as that would result in "new level"
			 * message appearing before "monster dies" message */
			m_take_hit = m_ptr.index;
			/* in case this is called from within creatures(), this is a
			 * horrible hack, the m_list/creatures() code needs to be
			 * rewritten */
			if (Variable.hackMonsterIndex < monptr) {
				deleteMonster(monptr);
			} else {
				deleteMonster1(monptr);
			}
		} else {
			m_take_hit = -1;
		}
		return m_take_hit;
	}
	
	/* Player attacks a (poor, defenseless) creature	-RAK-	*/
	public static void playerAttackMonster(int y, int x) {
		int k, blows;
		int crptr, monptr, base_tohit;
		IntPointer tot_tohit = new IntPointer();
		String m_name, out_val;
		InvenType i_ptr;
		PlayerMisc p_ptr;
		
		crptr = Variable.cave[y][x].creatureIndex;
		monptr = Monsters.monsterList[crptr].index;
		Monsters.monsterList[crptr].sleep = 0;
		i_ptr = Treasure.inventory[Constants.INVEN_WIELD];
		/* Does the player know what he's fighting?	   */
		if (!Monsters.monsterList[crptr].monsterLight) {
			m_name = "it";
		} else {
			m_name = String.format("the %s", Monsters.creatureList[monptr].name);
		}
		if (i_ptr.category != Constants.TV_NOTHING) {	/* Proper weapon */
			blows = Misc3.attackBlows(i_ptr.weight, tot_tohit);
		} else {	/* Bare hands?   */
			blows = 2;
			tot_tohit.value(-3);
		}
		if ((i_ptr.category >= Constants.TV_SLING_AMMO) && (i_ptr.category <= Constants.TV_SPIKE)) {
			/* Fix for arrows */
			blows = 1;
		}
		p_ptr = Player.py.misc;
		tot_tohit.value(tot_tohit.value() + p_ptr.plusToHit);
		/* if creature not lit, make it more difficult to hit */
		if (Monsters.monsterList[crptr].monsterLight) {
			base_tohit = p_ptr.baseToHit;
		} else {
			base_tohit = (p_ptr.baseToHit / 2) - (tot_tohit.value() * (Constants.BTH_PLUS_ADJ - 1)) - (p_ptr.level * Player.classLevelAdjust[p_ptr.playerClass][Constants.CLA_BTH] / 2);
		}
		
		/* Loop for number of blows,	trying to hit the critter.	  */
		do {
			if (Moria1.testHit(base_tohit, p_ptr.level, tot_tohit.value(), Monsters.creatureList[monptr].armorClass, Constants.CLA_BTH)) {
				out_val = String.format("You hit %s.", m_name);
				IO.printMessage(out_val);
				if (i_ptr.category != Constants.TV_NOTHING) {
					k = Misc1.pDamageRoll(i_ptr.damage);
					k = Misc3.totalDamage(i_ptr, k, monptr);
					k = Misc3.criticalBlow(i_ptr.weight, tot_tohit.value(), k, Constants.CLA_BTH);
				} else {	/* Bare hands!?  */
					k = Misc1.damageRoll(1, 1);
					k = Misc3.criticalBlow(1, 0, k, Constants.CLA_BTH);
				}
				k += p_ptr.plusToDamage;
				if (k < 0)	k = 0;
				
				if (Player.py.flags.confuseMonster) {
					Player.py.flags.confuseMonster = false;
					IO.printMessage("Your hands stop glowing.");
					if ((Monsters.creatureList[monptr].cdefense & Constants.CD_NO_SLEEP) != 0 || (Misc1.randomInt(Constants.MAX_MONS_LEVEL) < Monsters.creatureList[monptr].level)) {
						out_val = String.format("%s is unaffected.", m_name);
					} else {
						out_val = String.format("%s appears confused.", m_name);
						if (Monsters.monsterList[crptr].confused > 0) {
							Monsters.monsterList[crptr].confused += 3;
						} else {
							Monsters.monsterList[crptr].confused = 2 + Misc1.randomInt(16);
						}
					}
					IO.printMessage(out_val);
					if (Monsters.monsterList[crptr].monsterLight && Misc1.randomInt(4) == 1) {
						Variable.creatureRecall[monptr].cdefense |= Monsters.creatureList[monptr].cdefense & Constants.CD_NO_SLEEP;
					}
				}
				
				/* See if we done it in.				 */
				if (monsterTakeHit(crptr, k) >= 0) {
					out_val = String.format("You have slain %s.", m_name);
					IO.printMessage(out_val);
					Misc3.printExperience();
					blows = 0;
				}
				
				if ((i_ptr.category >= Constants.TV_SLING_AMMO) && (i_ptr.category <= Constants.TV_SPIKE)) {	/* Use missiles up*/
					i_ptr.number--;
					Treasure.invenWeight -= i_ptr.weight;
					Player.py.flags.status |= Constants.PY_STR_WGT;
					if (i_ptr.number == 0) {
						Treasure.equipCounter--;
						Moria1.adjustPlayerBonuses(i_ptr, -1);
						Desc.copyIntoInventory(i_ptr, Constants.OBJ_NOTHING);
						Moria1.calcBonuses();
					}
				}
			} else {
				out_val = String.format("You miss %s.", m_name);
				IO.printMessage(out_val);
			}
			blows--;
		} while (blows >= 1);
	}
	
	/* Moves player from one space to another.		-RAK-	*/
	/* Note: This routine has been pre-declared; see that for argument*/
	public static void movePlayer(int dir, boolean do_pickup) {
		int old_row, old_col, old_find_flag;
		IntPointer y, x;
		int i, j;
		CaveType c_ptr, d_ptr;
		
		if ((Player.py.flags.confused > 0)		/* Confused?	     */
				&& (Misc1.randomInt(4) > 1)	/* 75% random movement   */
				&& (dir != 5)) {		/* Never random if sitting*/
			dir = Misc1.randomInt(9);
			Moria2.endFind();
		}
		y = new IntPointer(Player.y);
		x = new IntPointer(Player.x);
		if (Misc3.moveMonster(dir, y, x)) {	/* Legal move?	      */
			c_ptr = Variable.cave[y.value()][x.value()];
			/* if there is no creature, or an unlit creature in the walls then... */
			/* disallow attacks against unlit creatures in walls because moving into
			 * a wall is a free turn normally, hence don't give player free turns
			 * attacking each wall in an attempt to locate the invisible creature,
			 * instead force player to tunnel into walls which always takes a turn */
			if ((c_ptr.creatureIndex < 2) || (!Monsters.monsterList[c_ptr.creatureIndex].monsterLight && c_ptr.fval >= Constants.MIN_CLOSED_SPACE)) {
				if (c_ptr.fval <= Constants.MAX_OPEN_SPACE) {	/* Open floor spot	*/
					/* Make final assignments of char co-ords */
					old_row = Player.y;
					old_col = Player.x;
					Player.y = y.value();
					Player.x = x.value();
					/* Move character record (-1)	       */
					Moria1.moveCreatureRecord(old_row, old_col, Player.y, Player.x);
					/* Check for new panel		       */
					if (Misc1.getPanel(Player.y, Player.x, false)) {
						Misc1.printMap();
					}
					/* Check to see if he should stop	       */
					if (Variable.findFlag != 0) {
						Moria2.areaAffect(dir, Player.y, Player.x);
					}
					/* Check to see if he notices something  */
					/* fos may be negative if have good rings of searching */
					if ((Player.py.misc.freqOfSearch <= 1) || (Misc1.randomInt(Player.py.misc.freqOfSearch) == 1) || (Player.py.flags.status & Constants.PY_SEARCH) != 0) {
						Moria2.search(Player.y, Player.x, Player.py.misc.searchChance);
					}
					/* A room of light should be lit.	     */
					if (c_ptr.fval == Constants.LIGHT_FLOOR) {
						if (!c_ptr.permLight && Player.py.flags.blind == 0) {
							Moria1.lightUpRoom(Player.y, Player.x);
						}
					
					/* In doorway of light-room?	       */
					} else if (c_ptr.litRoom && (Player.py.flags.blind < 1)) {
						for (i = (Player.y - 1); i <= (Player.y + 1); i++) {
							for (j = (Player.x - 1); j <= (Player.x + 1); j++) {
								d_ptr = Variable.cave[i][j];
								if ((d_ptr.fval == Constants.LIGHT_FLOOR) && (!d_ptr.permLight)) {
									Moria1.lightUpRoom(i, j);
								}
							}
						}
					}
					/* Move the light source		       */
					Moria1.moveLight(old_row, old_col, Player.y, Player.x);
					/* An object is beneath him.	     */
					if (c_ptr.treasureIndex != 0) {
						carry(Player.y, Player.x, do_pickup);
						/* if stepped on falling rock trap, and space contains
						 * rubble, then step back into a clear area */
						if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_RUBBLE) {
							Moria1.moveCreatureRecord(Player.y, Player.x, old_row, old_col);
							Moria1.moveLight(Player.y, Player.x, old_row, old_col);
							Player.y = old_row;
							Player.x = old_col;
							/* check to see if we have stepped back onto another
							 * trap, if so, set it off */
							c_ptr = Variable.cave[Player.y][Player.x];
							if (c_ptr.treasureIndex != 0) {
								i = Treasure.treasureList[c_ptr.treasureIndex].category;
								if (i == Constants.TV_INVIS_TRAP || i == Constants.TV_VIS_TRAP || i == Constants.TV_STORE_DOOR) {
									tripTrap(Player.y, Player.x);
								}
							}
						}
					}
				} else {	  /*Can't move onto floor space*/
					if (Variable.findFlag == 0 && (c_ptr.treasureIndex != 0)) {
						if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_RUBBLE) {
							IO.printMessage("There is rubble blocking your way.");
						} else if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_CLOSED_DOOR) {
							IO.printMessage("There is a closed door blocking your way.");
						}
					} else {
						Moria2.endFind();
					}
					Variable.freeTurnFlag = true;
				}
			} else {	/* Attacking a creature! */
				old_find_flag = Variable.findFlag;
				Moria2.endFind();
				/* if player can see monster, and was in find mode, then nothing */
				if (Monsters.monsterList[c_ptr.creatureIndex].monsterLight && old_find_flag != 0) {
					/* did not do anything this turn */
					Variable.freeTurnFlag = true;
				} else {
					if (Player.py.flags.afraid < 1) {	/* Coward?	*/
						playerAttackMonster(y.value(), x.value());
					} else {	/* Coward!	*/
						IO.printMessage("You are too afraid!");
					}
				}
			}
		}
	}
	
	/* Chests have traps too.				-RAK-	*/
	/* Note: Chest traps are based on the FLAGS value		 */
	public static void chestTrap(int y, int x) {
		int i;
		IntPointer j, k;
		InvenType t_ptr;
		
		t_ptr = Treasure.treasureList[Variable.cave[y][x].treasureIndex];
		if ((Constants.CH_LOSE_STR & t_ptr.flags) != 0) {
			IO.printMessage("A small needle has pricked you!");
			if (!Player.py.flags.sustainStr) {
				Misc3.decreaseStat(Constants.A_STR);
				Moria1.takeHit(Misc1.damageRoll(1, 4), "a poison needle");
				IO.printMessage("You feel weakened!");
			} else {
				IO.printMessage("You are unaffected.");
			}
		}
		if ((Constants.CH_POISON & t_ptr.flags) != 0) {
			IO.printMessage("A small needle has pricked you!");
			Moria1.takeHit(Misc1.damageRoll(1, 6), "a poison needle");
			Player.py.flags.poisoned += 10 + Misc1.randomInt(20);
		}
		if ((Constants.CH_PARALYSED & t_ptr.flags) != 0) {
			IO.printMessage("A puff of yellow gas surrounds you!");
			if (Player.py.flags.freeAct) {
				IO.printMessage("You are unaffected.");
			} else {
				IO.printMessage("You choke and pass out.");
				Player.py.flags.paralysis = 10 + Misc1.randomInt(20);
			}
		}
		if ((Constants.CH_SUMMON & t_ptr.flags) != 0) {
			for (i = 0; i < 3; i++) {
				j = new IntPointer(y);
				k = new IntPointer(x);
				Misc1.summonMonster(j, k, false);
			}
		}
		if ((Constants.CH_EXPLODE & t_ptr.flags) != 0) {
			IO.printMessage("There is a sudden explosion!");
			deleteObject(y, x);
			Moria1.takeHit(Misc1.damageRoll(5, 8), "an exploding chest");
		}
	}
	
	/* Opens a closed door or closed chest.		-RAK-	*/
	public static void openDoorOrChest() {
		IntPointer y, x, dir = new IntPointer();
		int i;
		boolean flag, no_object;
		CaveType c_ptr;
		InvenType t_ptr;
		PlayerMisc p_ptr;
		MonsterType m_ptr;
		String m_name, out_val;
		
		y = new IntPointer(Player.y);
		x = new IntPointer(Player.x);
		if (Moria1.getDirection("", dir)) {
			Misc3.moveMonster(dir.value(), y, x);
			c_ptr = Variable.cave[y.value()][x.value()];
			no_object = false;
			if (c_ptr.creatureIndex > 1 && c_ptr.treasureIndex != 0 && (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_CLOSED_DOOR || Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_CHEST)) {
				m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
				if (m_ptr.monsterLight) {
					m_name = String.format("The %s", Monsters.creatureList[m_ptr.index].name);
				} else {
					m_name = "Something";
				}
				out_val = String.format("%s is in your way!", m_name);
				IO.printMessage(out_val);
			} else if (c_ptr.treasureIndex != 0) {
				/* Closed door		 */
				if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_CLOSED_DOOR) {
					t_ptr = Treasure.treasureList[c_ptr.treasureIndex];
					if (t_ptr.misc > 0) {	/* It's locked.	*/
						p_ptr = Player.py.misc;
						i = p_ptr.disarmChance + 2 * Misc3.adjustToDisarm() + Misc3.adjustStat(Constants.A_INT) + (Player.classLevelAdjust[p_ptr.playerClass][Constants.CLA_DISARM] * p_ptr.level / 3);
						if (Player.py.flags.confused > 0) {
							IO.printMessage("You are too confused to pick the lock.");
						} else if ((i - t_ptr.misc) > Misc1.randomInt(100)) {
							IO.printMessage("You have picked the lock.");
							Player.py.misc.currExp++;
							Misc3.printExperience();
							t_ptr.misc = 0;
						} else {
							IO.countMessagePrint("You failed to pick the lock.");
						}
					} else if (t_ptr.misc < 0) {	/* It's stuck	  */
						IO.printMessage("It appears to be stuck.");
					}
					
					if (t_ptr.misc == 0) {
						Desc.copyIntoInventory(Treasure.treasureList[c_ptr.treasureIndex], Constants.OBJ_OPEN_DOOR);
						c_ptr.fval = Constants.CORR_FLOOR;
						Moria1.lightUpSpot(y.value(), x.value());
						Variable.commandCount = 0;
					}
				
				/* Open a closed chest.		     */
				} else if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_CHEST) {
					p_ptr = Player.py.misc;
					i = p_ptr.disarmChance + 2 * Misc3.adjustToDisarm() + Misc3.adjustStat(Constants.A_INT) + (Player.classLevelAdjust[p_ptr.playerClass][Constants.CLA_DISARM] * p_ptr.level / 3);
					t_ptr = Treasure.treasureList[c_ptr.treasureIndex];
					flag = false;
					if ((Constants.CH_LOCKED & t_ptr.flags) != 0) {
						if (Player.py.flags.confused > 0) {
							IO.printMessage("You are too confused to pick the lock.");
						} else if ((i - t_ptr.level) > Misc1.randomInt(100)) {
							IO.printMessage("You have picked the lock.");
							flag = true;
							Player.py.misc.currExp += t_ptr.level;
							Misc3.printExperience();
						} else {
							IO.countMessagePrint("You failed to pick the lock.");
						}
					} else {
						flag = true;
					}
					if (flag) {
						t_ptr.flags &= ~Constants.CH_LOCKED;
						t_ptr.specialName = Constants.SN_EMPTY;
						Desc.identifyItemPlusses(t_ptr);
						t_ptr.cost = 0;
					}
					flag = false;
					/* Was chest still trapped?	 (Snicker)   */
					if ((Constants.CH_LOCKED & t_ptr.flags) == 0) {
						chestTrap(y.value(), x.value());
						if (c_ptr.treasureIndex != 0) {
							flag = true;
						}
					}
					/* Chest treasure is allocated as if a creature   */
					/* had been killed.				   */
					if (flag) {
						/* clear the cursed chest/monster win flag, so that people
						 * can not win by opening a cursed chest */
						Treasure.treasureList[c_ptr.treasureIndex].flags &= ~Constants.TR_CURSED;
						monsterDeath(y.value(), x.value(), Treasure.treasureList[c_ptr.treasureIndex].flags);
						Treasure.treasureList[c_ptr.treasureIndex].flags = 0;
					}
				} else {
					no_object = true;
				}
			} else {
				no_object = true;
			}
			
			if (no_object) {
				IO.printMessage("I do not see anything you can open there.");
				Variable.freeTurnFlag = true;
			}
		}
	}
	
	/* Closes an open door.				-RAK-	*/
	public static void closeDoor() {
		IntPointer y, x, dir = new IntPointer();
		boolean no_object;
		String out_val, m_name;
		CaveType c_ptr;
		MonsterType m_ptr;
		
		y = new IntPointer(Player.y);
		x = new IntPointer(Player.x);
		if (Moria1.getDirection("", dir)) {
			Misc3.moveMonster(dir.value(), y, x);
			c_ptr = Variable.cave[y.value()][x.value()];
			no_object = false;
			if (c_ptr.treasureIndex != 0) {
				if (Treasure.treasureList[c_ptr.treasureIndex].category == Constants.TV_OPEN_DOOR) {
					if (c_ptr.creatureIndex == 0) {
						if (Treasure.treasureList[c_ptr.treasureIndex].misc == 0) {
							Desc.copyIntoInventory(Treasure.treasureList[c_ptr.treasureIndex], Constants.OBJ_CLOSED_DOOR);
							c_ptr.fval = Constants.BLOCKED_FLOOR;
							Moria1.lightUpSpot(y.value(), x.value());
						} else {
							IO.printMessage("The door appears to be broken.");
						}
					} else {
						m_ptr = Monsters.monsterList[c_ptr.creatureIndex];
						if (m_ptr.monsterLight) {
							m_name = String.format("The %s", Monsters.creatureList[m_ptr.index].name);
						} else {
							m_name = "Something";
						}
						out_val = String.format("%s is in your way!", m_name);
						IO.printMessage(out_val);
					}
				} else {
					no_object = true;
				}
			} else {
				no_object = true;
			}
			
			if (no_object) {
				IO.printMessage("I do not see anything you can close there.");
				Variable.freeTurnFlag = true;
			}
		}
	}
	
	/* Tunneling through real wall: 10, 11, 12		-RAK-	*/
	/* Used by TUNNEL and WALL_TO_MUD				 */
	public static boolean tunnelThroughWall(int y, int x, int t1, int t2) {
		int i, j;
		CaveType c_ptr;
		boolean res, found;
		
		res = false;
		if (t1 > t2) {
			c_ptr = Variable.cave[y][x];
			if (c_ptr.litRoom) {
				/* should become a room space, check to see whether it should be
				 * LIGHT_FLOOR or DARK_FLOOR */
				found = false;
				for (i = y - 1; i <= y + 1; i++) {
					for (j = x - 1; j <= x + 1; j++) {
						if (Variable.cave[i][j].fval <= Constants.MAX_CAVE_ROOM) {
							c_ptr.fval = Variable.cave[i][j].fval;
							c_ptr.permLight = Variable.cave[i][j].permLight;
							found = true;
							break;
						}
					}
				}
				if (!found) {
					c_ptr.fval = Constants.CORR_FLOOR;
					c_ptr.permLight = false;
				}
			} else {
				/* should become a corridor space */
				c_ptr.fval = Constants.CORR_FLOOR;
				c_ptr.permLight = false;
			}
			c_ptr.fieldMark = false;
			if (Misc1.panelContains(y, x)) {
				if ((c_ptr.tempLight || c_ptr.permLight) && c_ptr.treasureIndex != 0) {
					IO.printMessage("You have found something!");
				}
			}
			Moria1.lightUpSpot(y, x);
			res = true;
		}
		return res;
	}
}
