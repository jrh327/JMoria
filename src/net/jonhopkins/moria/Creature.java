/* 
 * Creature.java: handle monster movement and attacks
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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;
import net.jonhopkins.moria.types.MonsterRecallType;

public class Creature {
	
	private Creature() { }
	
	/**
	 * Updates screen when monsters move about -RAK-
	 * 
	 * @param monsterIndex
	 */
	public static void updateMonster(int monsterIndex) {
		MonsterType monster = Monsters.monsterList[monsterIndex];
		boolean canSeeMonster = false;
		if ((monster.currDistance <= Constants.MAX_SIGHT)
				&& (Player.py.flags.status & Constants.PY_BLIND) == 0
				&& (Misc1.panelContains(monster.y, monster.x))) {
			// Wizard sight.
			if (Variable.isWizard) {
				canSeeMonster = true;
			
			// Normal sight.
			} else if (Misc1.isInLineOfSight(Player.y, Player.x, monster.y, monster.x)) {
				CaveType cavePos = Variable.cave[monster.y][monster.x];
				CreatureType creature = Monsters.creatureList[monster.index];
				if (cavePos.permLight
						|| cavePos.tempLight
						|| (Variable.findFlag != 0 && monster.currDistance < 2 && Variable.playerLight)) {
					if ((Constants.CM_INVISIBLE & creature.cmove) == 0) {
						canSeeMonster = true;
					} else if (Player.py.flags.seeInvisible) {
						canSeeMonster = true;
						Variable.creatureRecall[monster.index].cmove |= Constants.CM_INVISIBLE;
					}
				
				// Infra vision.
				} else if ((Player.py.flags.seeInfrared > 0)
						&& (monster.currDistance <= Player.py.flags.seeInfrared)
						&& (Constants.CD_INFRA & creature.cdefense) != 0) {
					canSeeMonster = true;
					Variable.creatureRecall[monster.index].cdefense |= Constants.CD_INFRA;
				}
			}
		}
		
		// Light it up.
		if (canSeeMonster) {
			if (!monster.monsterLight) {
				Moria1.disturbPlayer(true, false);
				toggleMonsterLight(monster);
			}
		
		// Turn it off.
		} else if (monster.monsterLight) {
			toggleMonsterLight(monster);
		}
	}
	
	private static void toggleMonsterLight(MonsterType monster) {
		monster.monsterLight = !monster.monsterLight;
		Moria1.lightUpSpot(monster.y, monster.x);
		Variable.didScreenChange = true; // notify inven_command
	}
	
	/**
	 * Calculate number of moves this turn. -RAK-
	 * <p>
	 * NOTE: Player must always move at least once per iteration,
	 *	 a slowed player is handled by moving monsters faster
	 * 
	 * @param speed The monster's speed
	 * @return Number of moves this turn
	 */
	public static int getMovesPerTurn(int speed) {
		if (speed > 0) {
			if (Player.py.flags.rest == 0) {
				return speed;
			}
			return 1;
		}
		
		// speed must be negative here
		return ((Variable.turn % (2 - speed)) == 0) ? 1 : 0;
	}
	
	/**
	 * Make sure a new creature gets lit up. -CJS-
	 * 
	 * @param y The vertical position of the monster
	 * @param x The horizontal position of the monster
	 * @return Whether the monster should be lit up
	 */
	public static boolean checkMonsterLight(int y, int x) {
		int monsterIndex = Variable.cave[y][x].creatureIndex;
		
		if (monsterIndex <= 1) {
			return false;
		}
		
		updateMonster(monsterIndex);
		return Monsters.monsterList[monsterIndex].monsterLight;
	}
	
	/**
	 * Choose correct directions for monster movement -RAK-
	 * 
	 * @param monsterIndex
	 * @param monsterMoves
	 */
	public static void getMoveDirections(int monsterIndex, int[] monsterMoves) {
		int dy = Monsters.monsterList[monsterIndex].y - Player.y;
		int dx = Monsters.monsterList[monsterIndex].x - Player.x;
		
		int ay = 0;
		int ax = 0;
		int moveVal = 0;
		if (dy < 0) {
			moveVal = 8;
			ay = -dy;
		} else {
			moveVal = 0;
			ay = dy;
		}
		if (dx > 0) {
			moveVal += 4;
			ax = dx;
		} else {
			ax = -dx;
		}
		// this has the advantage of preventing the diamond maneuver, also faster
		if (ay > (ax << 1)) {
			moveVal += 2;
		} else if (ax > (ay << 1)) {
			moveVal++;
		}
		switch (moveVal) {
		case 0:
			monsterMoves[0] = 9;
			if (ay > ax) {
				monsterMoves[1] = 8;
				monsterMoves[2] = 6;
				monsterMoves[3] = 7;
				monsterMoves[4] = 3;
			} else {
				monsterMoves[1] = 6;
				monsterMoves[2] = 8;
				monsterMoves[3] = 3;
				monsterMoves[4] = 7;
			}
			break;
		case 1: case 9:
			monsterMoves[0] = 6;
			if (dy < 0) {
				monsterMoves[1] = 3;
				monsterMoves[2] = 9;
				monsterMoves[3] = 2;
				monsterMoves[4] = 8;
			} else {
				monsterMoves[1] = 9;
				monsterMoves[2] = 3;
				monsterMoves[3] = 8;
				monsterMoves[4] = 2;
			}
			break;
		case 2: case 6:
			monsterMoves[0] = 8;
			if (dx < 0) {
				monsterMoves[1] = 9;
				monsterMoves[2] = 7;
				monsterMoves[3] = 6;
				monsterMoves[4] = 4;
			} else {
				monsterMoves[1] = 7;
				monsterMoves[2] = 9;
				monsterMoves[3] = 4;
				monsterMoves[4] = 6;
			}
			break;
		case 4:
			monsterMoves[0] = 7;
			if (ay > ax) {
				monsterMoves[1] = 8;
				monsterMoves[2] = 4;
				monsterMoves[3] = 9;
				monsterMoves[4] = 1;
			} else {
				monsterMoves[1] = 4;
				monsterMoves[2] = 8;
				monsterMoves[3] = 1;
				monsterMoves[4] = 9;
			}
			break;
		case 5: case 13:
			monsterMoves[0] = 4;
			if (dy < 0) {
				monsterMoves[1] = 1;
				monsterMoves[2] = 7;
				monsterMoves[3] = 2;
				monsterMoves[4] = 8;
			} else {
				monsterMoves[1] = 7;
				monsterMoves[2] = 1;
				monsterMoves[3] = 8;
				monsterMoves[4] = 2;
			}
			break;
		case 8:
			monsterMoves[0] = 3;
			if (ay > ax) {
				monsterMoves[1] = 2;
				monsterMoves[2] = 6;
				monsterMoves[3] = 1;
				monsterMoves[4] = 9;
			} else {
				monsterMoves[1] = 6;
				monsterMoves[2] = 2;
				monsterMoves[3] = 9;
				monsterMoves[4] = 1;
			}
			break;
		case 10: case 14:
			monsterMoves[0] = 2;
			if (dx < 0) {
				monsterMoves[1] = 3;
				monsterMoves[2] = 1;
				monsterMoves[3] = 6;
				monsterMoves[4] = 4;
			} else {
				monsterMoves[1] = 1;
				monsterMoves[2] = 3;
				monsterMoves[3] = 4;
				monsterMoves[4] = 6;
			}
			break;
		case 12:
			monsterMoves[0] = 1;
			if (ay > ax) {
				monsterMoves[1] = 2;
				monsterMoves[2] = 4;
				monsterMoves[3] = 3;
				monsterMoves[4] = 7;
			} else {
				monsterMoves[1] = 4;
				monsterMoves[2] = 2;
				monsterMoves[3] = 7;
				monsterMoves[4] = 3;
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Make an attack on the player. -RAK-
	 * 
	 * @param monsterIndex
	 */
	/* Make an attack on the player (chuckle.)		-RAK-	*/
	public static void makeAttack(int monsterIndex) {
		if (Variable.death) { // don't beat a dead body!
			return;
		}
		
		MonsterType monster = Monsters.monsterList[monsterIndex];
		CreatureType creature = Monsters.creatureList[monster.index];
		String creatureDesc;
		if (!monster.monsterLight) {
			creatureDesc = "It ";
		} else {
			creatureDesc = String.format("The %s ", creature.name);
		}
		
		// For "DIED_FROM" string
		String deathDesc;
		if ((Constants.CM_WIN & creature.cmove) != 0) {
			deathDesc = String.format("The %s", creature.name);
		} else if (Desc.isVowel(creature.name.charAt(0))) {
			deathDesc = String.format("an %s", creature.name);
		} else {
			deathDesc = String.format("a %s", creature.name);
		}
		// End DIED_FROM
		
		PlayerMisc playerMisc = Player.py.misc;
		PlayerFlags playerFlags = Player.py.flags;
		int attackStrIndex = 0;
		int attackStr = creature.damage[attackStrIndex];
		while (attackStr != 0 && !Variable.death) {
			int attackType = Monsters.monsterAttacks[attackStr].attackType;
			int attackDesc = Monsters.monsterAttacks[attackStr].attackDesc;
			int attackDice = Monsters.monsterAttacks[attackStr].attackDice;
			int attackSides = Monsters.monsterAttacks[attackStr].attackSides;
			attackStrIndex++;
			attackStr = creature.damage[attackStrIndex];
			
			if (playerFlags.protectFromEvil > 0
					&& (creature.cdefense & Constants.CD_EVIL) != 0
					&& (playerMisc.level + 1) > creature.level) {
				if (monster.monsterLight) {
					Variable.creatureRecall[monster.index].cdefense |= Constants.CD_EVIL;
				}
				attackType = 99;
				attackDesc = 99;
			}
			
			boolean successfulAttack = false;
			switch (attackType) {
			case 1: // Normal attack
				if (Moria1.testHit(60, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 2: // Lose Strength
				if (Moria1.testHit(-3, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 3: // Confusion attack
				if (Moria1.testHit(10, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 4: // Fear attack
				if (Moria1.testHit(10, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 5: // Fire attack
				if (Moria1.testHit(10, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 6: // Acid attack
				if (Moria1.testHit(0, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 7: // Cold attack
				if (Moria1.testHit(10, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 8: // Lightning attack
				if (Moria1.testHit(10, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 9: // Corrosion attack
				if (Moria1.testHit(0, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 10: // Blindness attack
				if (Moria1.testHit(2, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 11: // Paralysis attack
				if (Moria1.testHit(2, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 12: // Steal Money 
				if ((Moria1.testHit(5, creature.level, 0,
						playerMisc.level, Constants.CLA_MISC_HIT))
						&& (playerMisc.gold > 0)) {
					successfulAttack = true;
				}
				break;
			case 13: // Steal Object
				if ((Moria1.testHit(2, creature.level, 0,
						playerMisc.level, Constants.CLA_MISC_HIT))
						&& (Treasure.invenCounter > 0)) {
					successfulAttack = true;
				}
				break;
			case 14: // Poison
				if (Moria1.testHit(5, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 15: // Lose dexterity
				if (Moria1.testHit(0, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 16: // Lose constitution
				if (Moria1.testHit(0, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 17: // Lose intelligence
				if (Moria1.testHit(2, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 18: // Lose wisdom
				if (Moria1.testHit(2, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 19: // Lose experience
				if (Moria1.testHit(5, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 20: // Aggravate monsters
				successfulAttack = true;
				break;
			case 21: // Disenchant
				if (Moria1.testHit(20, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 22: // Eat food
				if (Moria1.testHit(5, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 23: // Eat light
				if (Moria1.testHit(5, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT)) {
					successfulAttack = true;
				}
				break;
			case 24: // Eat charges
				if ((Moria1.testHit(15, creature.level, 0,
						playerMisc.totalArmorClass + playerMisc.magicArmorClass,
						Constants.CLA_MISC_HIT))
						&& (Treasure.invenCounter > 0)) {
					// check to make sure an object exists
					successfulAttack = true;
				}
				break;
			case 99:
				successfulAttack = true;
				break;
			default:
				break;
			}
			
			int attackNum = 0;
			if (successfulAttack) {
				// can not strcat to creatureDesc because the creature 
				// may have multiple attacks
				Moria1.disturbPlayer(true, false);
				StringBuilder tmpStr = new StringBuilder().append(creatureDesc);
				switch (attackDesc) {
				case 1:
					tmpStr.append("hits you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 2:
					tmpStr.append("bites you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 3:
					tmpStr.append("claws you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 4:
					tmpStr.append("stings you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 5:
					tmpStr.append("touches you.");
					IO.printMessage(tmpStr.toString());
					break;
				//case 6: msg_print(strcat(tmp_str, "kicks you.")); break;
				case 7:
					tmpStr.append("gazes at you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 8:
					tmpStr.append("breathes on you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 9:
					tmpStr.append("spits on you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 10:
					tmpStr.append("makes a horrible wail.");
					IO.printMessage(tmpStr.toString());
					break;
				//case 11: msg_print(strcat(tmp_str, "embraces you.")); break;
				case 12:
					tmpStr.append("crawls on you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 13:
					tmpStr.append("releases a cloud of spores.");
					IO.printMessage(tmpStr.toString());
					break;
				case 14:
					tmpStr.append("begs you for money.");
					IO.printMessage(tmpStr.toString());
					break;
				case 15:
					IO.printMessage("You've been slimed!");
					break;
				case 16:
					tmpStr.append("crushes you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 17:
					tmpStr.append("tramples you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 18:
					tmpStr.append("drools on you.");
					IO.printMessage(tmpStr.toString());
					break;
				case 19:
					switch (Rnd.randomInt(9)) {
					case 1:
						tmpStr.append("insults you!");
						IO.printMessage(tmpStr.toString());
						break;
					case 2:
						tmpStr.append("insults your mother!");
						IO.printMessage(tmpStr.toString());
						break;
					case 3:
						tmpStr.append("gives you the finger!");
						IO.printMessage(tmpStr.toString());
						break;
					case 4:
						tmpStr.append("humiliates you!");
						IO.printMessage(tmpStr.toString());
						break;
					case 5:
						tmpStr.append("wets on your leg!");
						IO.printMessage(tmpStr.toString());
						break;
					case 6:
						tmpStr.append("defiles you!");
						IO.printMessage(tmpStr.toString());
						break;
					case 7:
						tmpStr.append("dances around you!");
						IO.printMessage(tmpStr.toString());
						break;
					case 8:
						tmpStr.append("makes obscene gestures!");
						IO.printMessage(tmpStr.toString());
						break;
					case 9:
						tmpStr.append("moons you!!!");
						IO.printMessage(tmpStr.toString());
						break;
					default:
						break;
					}
					break;
				case 99:
					tmpStr.append("is repelled.");
					IO.printMessage(tmpStr.toString());
					break;
				default:
					break;
				}
				
				boolean isMonsterVisible = true;
				boolean doesNotice = true;
				// always fail to notice attack if creature invisible, set notice
				// and visible here since creature may be visible when attacking
				// and then teleport afterwards (becoming effectively invisible)
				if (!monster.monsterLight) {
					isMonsterVisible = false;
					doesNotice = false;
				}
				
				int damage = Misc1.damageRoll(attackDice, attackSides);
				IntPointer itemIndex = new IntPointer();
				switch (attackType) {
				case 1: // Normal attack
					// round half-way case down
					damage -= ((playerMisc.totalArmorClass + playerMisc.magicArmorClass) * damage) / 200;
					Moria1.takeHit(damage, deathDesc);
					break;
				case 2: // Lose Strength
					Moria1.takeHit(damage, deathDesc);
					if (playerFlags.sustainStr) {
						IO.printMessage("You feel weaker for a moment, but it passes.");
					} else if (Rnd.randomInt(2) == 1) {
						IO.printMessage("You feel weaker.");
						Misc3.decreaseStat(Constants.A_STR);
					} else {
						doesNotice = false;
					}
					break;
				case 3: // Confusion attack
					Moria1.takeHit(damage, deathDesc);
					if (Rnd.randomInt(2) == 1) {
						if (playerFlags.confused < 1) {
							IO.printMessage("You feel confused.");
							playerFlags.confused += Rnd.randomInt(creature.level);
						} else {
							doesNotice = false;
						}
						playerFlags.confused += 3;
					} else {
						doesNotice = false;
					}
					break;
				case 4: //Fear attack
					Moria1.takeHit(damage, deathDesc);
					if (Misc3.playerSavingThrow()) {
						IO.printMessage("You resist the effects!");
					} else if (playerFlags.afraid < 1) {
						IO.printMessage("You are suddenly afraid!");
						playerFlags.afraid += 3 + Rnd.randomInt(creature.level);
					} else {
						playerFlags.afraid += 3;
						doesNotice = false;
					}
					break;
				case 5: // Fire attack
					IO.printMessage("You are enveloped in flames!");
					Moria2.fireDamage(damage, deathDesc);
					break;
				case 6: // Acid attack
					IO.printMessage("You are covered in acid!");
					Moria2.acidDamage(damage, deathDesc);
					break;
				case 7: // Cold attack
					IO.printMessage("You are covered with frost!");
					Moria2.coldDamage(damage, deathDesc);
					break;
				case 8: // Lightning attack
					IO.printMessage("Lightning strikes you!");
					Moria2.lightningDamage(damage, deathDesc);
					break;
				case 9: // Corrosion attack
					IO.printMessage("A stinging red gas swirls about you.");
					Moria2.corrodeGas(deathDesc);
					Moria1.takeHit(damage, deathDesc);
					break;
				case 10: // Blindness attack
					Moria1.takeHit(damage, deathDesc);
					if (playerFlags.blind < 1) {
						playerFlags.blind += 10 + Rnd.randomInt(creature.level);
						IO.printMessage("Your eyes begin to sting.");
					} else {
						playerFlags.blind += 5;
						doesNotice = false;
					}
					break;
				case 11: // Paralysis attack
					Moria1.takeHit(damage, deathDesc);
					if (Misc3.playerSavingThrow()) {
						IO.printMessage("You resist the effects!");
					} else if (playerFlags.paralysis < 1) {
						if (playerFlags.freeAct) {
							IO.printMessage("You are unaffected.");
						} else {
							playerFlags.paralysis = Rnd.randomInt(creature.level) + 3;
							IO.printMessage("You are paralyzed.");
						}
					} else {
						doesNotice = false;
					}
					break;
				case 12: // Steal Money
					if (playerFlags.paralysis < 1
							&& Rnd.randomInt(124) < Player.py.stats.useStat[Constants.A_DEX]) {
						IO.printMessage("You quickly protect your money pouch!");
					} else {
						int gold = (playerMisc.gold / 10) + Rnd.randomInt(25);
						if (gold > playerMisc.gold) {
							playerMisc.gold = 0;
						} else {
							playerMisc.gold -= gold;
						}
						IO.printMessage("Your purse feels lighter.");
						Misc3.printGold();
					}
					if (Rnd.randomInt(2) == 1) {
						IO.printMessage("There is a puff of smoke!");
						Spells.teleportMonsterAway(monsterIndex, Constants.MAX_SIGHT);
					}
					break;
				case 13: // Steal Object
					if (playerFlags.paralysis < 1
							&& Rnd.randomInt(124) < Player.py.stats.useStat[Constants.A_DEX]) {
						IO.printMessage("You grab hold of your backpack!");
					} else {
						itemIndex.value(Rnd.randomInt(Treasure.invenCounter) - 1);
						Misc3.destroyInvenItem(itemIndex.value());
						IO.printMessage("Your backpack feels lighter.");
					}
					if (Rnd.randomInt(2) == 1) {
						IO.printMessage("There is a puff of smoke!");
						Spells.teleportMonsterAway(monsterIndex, Constants.MAX_SIGHT);
					}
					break;
				case 14: // Poison
					Moria1.takeHit(damage, deathDesc);
					IO.printMessage("You feel very sick.");
					playerFlags.poisoned += Rnd.randomInt(creature.level) + 5;
					break;
				case 15: // Lose dexterity
					Moria1.takeHit(damage, deathDesc);
					if (playerFlags.sustainDex) {
						IO.printMessage("You feel clumsy for a moment, but it passes.");
					} else {
						IO.printMessage("You feel more clumsy.");
						Misc3.decreaseStat(Constants.A_DEX);
					}
					break;
				case 16: // Lose constitution
					Moria1.takeHit(damage, deathDesc);
					if (playerFlags.sustainCon) {
						IO.printMessage("Your body resists the effects of the disease.");
					} else {
						IO.printMessage("Your health is damaged!");
						Misc3.decreaseStat(Constants.A_CON);
					}
					break;
				case 17: // Lose intelligence
					Moria1.takeHit(damage, deathDesc);
					IO.printMessage("You have trouble thinking clearly.");
					if (playerFlags.sustainInt) {
						IO.printMessage("But your mind quickly clears.");
					} else {
						Misc3.decreaseStat(Constants.A_INT);
					}
					break;
				case 18: // Lose wisdom
					Moria1.takeHit(damage, deathDesc);
					if (playerFlags.sustainWis) {
						IO.printMessage("Your wisdom is sustained.");
					} else {
						IO.printMessage("Your wisdom is drained.");
						Misc3.decreaseStat(Constants.A_WIS);
					}
					break;
				case 19: // Lose experience
					IO.printMessage("You feel your life draining away!");
					Spells.loseExperience(damage + (playerMisc.currExp / 100) * Constants.MON_DRAIN_LIFE);
					break;
				case 20: // Aggravate monster
					Spells.aggravateMonster(20);
					break;
				case 21: // Disenchant
					successfulAttack = false;
					switch (Rnd.randomInt(7)) {
					case 1: itemIndex.value(Constants.INVEN_WIELD);	break;
					case 2: itemIndex.value(Constants.INVEN_BODY);  break;
					case 3: itemIndex.value(Constants.INVEN_ARM);   break;
					case 4: itemIndex.value(Constants.INVEN_OUTER); break;
					case 5: itemIndex.value(Constants.INVEN_HANDS); break;
					case 6: itemIndex.value(Constants.INVEN_HEAD);  break;
					case 7: itemIndex.value(Constants.INVEN_FEET);  break;
					default: break;
					}
					
					InvenType item = Treasure.inventory[itemIndex.value()];
					if (item.tohit > 0) {
						item.tohit -= Rnd.randomInt(2);
						// don't send it below zero
						if (item.tohit < 0) {
							item.tohit = 0;
						}
						successfulAttack = true;
					}
					
					if (item.plusToDam > 0) {
						item.plusToDam -= Rnd.randomInt(2);
						// don't send it below zero
						if (item.plusToDam < 0) {
							item.plusToDam = 0;
						}
						successfulAttack = true;
					}
					
					if (item.plusToArmorClass > 0) {
						item.plusToArmorClass -= Rnd.randomInt(2);
						// don't send it below zero
						if (item.plusToArmorClass < 0) {
							item.plusToArmorClass = 0;
						}
						successfulAttack = true;
					}
					
					if (successfulAttack) {
						IO.printMessage("There is a static feeling in the air.");
						Moria1.calcBonuses();
					} else {
						doesNotice = false;
					}
					break;
				case 22: // Eat food
					if (Misc3.findRange(Constants.TV_FOOD, Constants.TV_NEVER,
							itemIndex, new IntPointer())) {
						Misc3.destroyInvenItem(itemIndex.value());
						IO.printMessage("It got at your rations!");
					} else {
						doesNotice = false;
					}
					break;
				case 23: // Eat light
					item = Treasure.inventory[Constants.INVEN_LIGHT];
					if (item.misc > 0) {
						item.misc -= (250 + Rnd.randomInt(250));
						if (item.misc < 1)	{
							item.misc = 1;
						}
						
						if (playerFlags.blind < 1) {
							IO.printMessage("Your light dims.");
						} else {
							doesNotice = false;
						}
					} else {
						doesNotice = false;
					}
					break;
				case 24: // Eat charges
					itemIndex.value(Rnd.randomInt(Treasure.invenCounter) - 1);
					item = Treasure.inventory[itemIndex.value()];
					if ((item.category == Constants.TV_STAFF
								|| item.category == Constants.TV_WAND)
							&& item.misc > 0) {
						monster.hitpoints += creature.level * item.misc;
						item.misc = 0;
						
						if (!Desc.arePlussesKnownByPlayer(item)) {
							Misc4.addInscription(item, Constants.ID_EMPTY);
						}
						IO.printMessage("Energy drains from your pack!");
					} else {
						doesNotice = false;
					}
					break;
				case 99:
					doesNotice = false;
					break;
				default:
					doesNotice = false;
					break;
				}
				
				// Moved here from mon_move, so that monster only confused if it
				// actually hits.  A monster that has been repelled has not hit
				// the player, so it should not be confused.
				if (playerFlags.confuseMonster && attackDesc != 99) {
					IO.printMessage("Your hands stop glowing.");
					playerFlags.confuseMonster = false;
					tmpStr = new StringBuilder();
					if (Rnd.randomInt(Constants.MAX_MONS_LEVEL) < creature.level
							|| (Constants.CD_NO_SLEEP & creature.cdefense) != 0) {
						tmpStr.append(String.format("%sis unaffected.", creatureDesc));
					} else {
						tmpStr.append(String.format("%sappears confused.", creatureDesc));
						if (monster.confused > 0) {
							monster.confused += 3;
						} else {
							monster.confused = 2 + Rnd.randomInt(16);
						}
					}
					IO.printMessage(tmpStr.toString());
					
					if (isMonsterVisible && !Variable.death && Rnd.randomInt(4) == 1) {
						Variable.creatureRecall[monster.index].cdefense |=
								creature.cdefense & Constants.CD_NO_SLEEP;
					}
				}
				
				// increase number of attacks if notice true, or if visible and had
				// previously noticed the attack (in which case all this does is
				// help player learn damage), note that in the second case do
				// not increase attacks if creature repelled (no damage done)
				if ((doesNotice
						|| (isMonsterVisible
								&& Variable.creatureRecall[monster.index].attacks[attackNum] != 0
								&& attackType != 99))
						&& Variable.creatureRecall[monster.index].attacks[attackNum] < Constants.MAX_UCHAR) {
					Variable.creatureRecall[monster.index].attacks[attackNum]++;
				}
				
				if (Variable.death
						&& Variable.creatureRecall[monster.index].deaths < Constants.MAX_SHORT) {
					Variable.creatureRecall[monster.index].deaths++;
				}
			} else {
				if ((attackDesc >= 1 && attackDesc <= 3) || attackDesc == 6) {
					Moria1.disturbPlayer(true, false);
					StringBuilder tmpStr = new StringBuilder();
					tmpStr.append(creatureDesc).append("misses you.");
					IO.printMessage(tmpStr.toString());
				}
			}
			
			if (attackNum < Constants.MAX_MON_NATTACK - 1) {
				attackNum++;
			} else {
				break;
			}
		}
	}
	
	/**
	 * Make the move if possible, five choices -RAK-
	 * 
	 * @param monsterIndex
	 * @param monsterMoves
	 * @param rcmove
	 */
	public static void makeMove(int monsterIndex, int[] monsterMoves, IntPointer rcmove) {
		boolean doMove = false;
		MonsterType monster = Monsters.monsterList[monsterIndex];
		int moveBits = Monsters.creatureList[monster.index].cmove;
		
		// Up to 5 attempts at moving, then give up.
		boolean doTurn = false;
		for (int i = 0; i < 5 && !doTurn; i++) {
			// Get new position
			IntPointer newY = new IntPointer(monster.y);
			IntPointer newX = new IntPointer(monster.x);
			Misc3.moveMonster(monsterMoves[i], newY, newX);
			
			CaveType cavePos = Variable.cave[newY.value()][newX.value()];
			if (cavePos.fval != Constants.BOUNDARY_WALL) {
				// Floor is open?
				if (cavePos.fval <= Constants.MAX_OPEN_SPACE) {
					doMove = true;
				
				// Creature moves through walls?
				} else if ((moveBits & Constants.CM_PHASE) != 0) {
					doMove = true;
					rcmove.value(rcmove.value() | Constants.CM_PHASE);
				
				// Creature can open doors?
				} else if (cavePos.treasureIndex != 0) {
					InvenType item = Treasure.treasureList[cavePos.treasureIndex];
					if ((moveBits & Constants.CM_OPEN_DOOR) != 0) { // Creature can open doors.
						boolean isDoorStuck = false;
						if (item.category == Constants.TV_CLOSED_DOOR) {
							doTurn = true;
							if (item.misc == 0) { // Closed doors
								doMove = true;
							} else if (item.misc > 0) { // Locked doors
								if (Rnd.randomInt((monster.hitpoints + 1) * (50 + item.misc))
										< 40 * (monster.hitpoints - 10 - item.misc)) {
									item.misc = 0;
								}
							} else if (item.misc < 0) { // Stuck doors
								if (Rnd.randomInt((monster.hitpoints + 1) * (50 - item.misc))
										< 40 * (monster.hitpoints - 10 + item.misc)) {
									IO.printMessage("You hear a door burst open!");
									Moria1.disturbPlayer(true, false);
									isDoorStuck = true;
									doMove = true;
								}
							}
						} else if (item.category == Constants.TV_SECRET_DOOR) {
							doTurn = true;
							doMove = true;
						}
						
						if (doMove) {
							Desc.copyIntoInventory(item, Constants.OBJ_OPEN_DOOR);
							if (isDoorStuck) { // 50% chance of breaking door
								item.misc = 1 - Rnd.randomInt(2);
							}
							cavePos.fval = Constants.CORR_FLOOR;
							Moria1.lightUpSpot(newY.value(), newX.value());
							rcmove.value(rcmove.value() | Constants.CM_OPEN_DOOR);
							doMove = false;
						}
					} else { // Creature can not open doors, must bash them
						if (item.category == Constants.TV_CLOSED_DOOR) {
							doTurn = true;
							if (Rnd.randomInt((monster.hitpoints + 1) * (80 + Math.abs(item.misc)))
									< 40 * (monster.hitpoints - 20 - Math.abs(item.misc))) {
								Desc.copyIntoInventory(item, Constants.OBJ_OPEN_DOOR);
								// 50% chance of breaking door
								item.misc = 1 - Rnd.randomInt(2);
								cavePos.fval = Constants.CORR_FLOOR;
								Moria1.lightUpSpot(newY.value(), newX.value());
								IO.printMessage("You hear a door burst open!");
								Moria1.disturbPlayer(true, false);
							}
						}
					}
				}
				
				// Glyph of warding present?
				if (doMove && cavePos.treasureIndex != 0
						&& Treasure.treasureList[cavePos.treasureIndex].category == Constants.TV_VIS_TRAP
						&& Treasure.treasureList[cavePos.treasureIndex].subCategory == 99) {
					if (Rnd.randomInt(Constants.OBJ_RUNE_PROT)
							< Monsters.creatureList[monster.index].level) {
						if (newY.value() == Player.y && newX.value() == Player.x) {
							IO.printMessage("The rune of protection is broken!");
						}
						Moria3.deleteObject(newY.value(), newX.value());
					} else {
						doMove = false;
						// If the creature moves only to attack,
						// don't let it move if the glyph prevents
						// it from attacking
						if ((moveBits & Constants.CM_ATTACK_ONLY) != 0) {
							doTurn = true;
						}
					}
				}
				
				// Creature has attempted to move on player?
				if (doMove) {
					if (cavePos.creatureIndex == 1) {
						// if the monster is not lit, must call update_mon, it may
						// be faster than character, and hence could have just
						// moved next to character this same turn
						if (!monster.monsterLight) {
							updateMonster(monsterIndex);
						}
						makeAttack(monsterIndex);
						doMove = false;
						doTurn = true;
					
					// Creature is attempting to move on other creature?
					} else if (cavePos.creatureIndex > 1
							&& (newY.value() != monster.y || newX.value() != monster.x)) {
						// Creature eats other creatures?
						if ((moveBits & Constants.CM_EATS_OTHER) != 0
								&& Monsters.creatureList[monster.index].mexp
									>= Monsters.creatureList[Monsters.monsterList[cavePos.creatureIndex].index].mexp) {
							if (Monsters.monsterList[cavePos.creatureIndex].monsterLight) {
								rcmove.value(rcmove.value() | Constants.CM_EATS_OTHER);
							}
							
							// It ate an already processed monster. Handle normally.
							if (monsterIndex < cavePos.creatureIndex) {
								Moria3.deleteMonster(cavePos.creatureIndex);
							
							// If it eats this monster, an already processed monster
							// will take its place, causing all kinds of havoc. Delay
							// the kill a bit.
							} else {
								Moria3.deleteMonster1(cavePos.creatureIndex);
							}
						} else {
							doMove = false;
						}
					}
				}
				
				// Creature has been allowed move.
				if (doMove) {
					// Pick up or eat an object
					if ((moveBits & Constants.CM_PICKS_UP) != 0) {
						cavePos = Variable.cave[newY.value()][newX.value()];
						
						if (cavePos.treasureIndex != 0
								&& Treasure.treasureList[cavePos.treasureIndex].category
									<= Constants.TV_MAX_OBJECT) {
							rcmove.value(rcmove.value() | Constants.CM_PICKS_UP);
							Moria3.deleteObject(newY.value(), newX.value());
						}
					}
					
					// Move creature record
					Moria1.moveCreatureRecord(monster.y, monster.x, newY.value(), newX.value());
					if (monster.monsterLight) {
						monster.monsterLight = false;
						Moria1.lightUpSpot(monster.y, monster.x);
					}
					monster.y = newY.value();
					monster.x = newX.value();
					monster.currDistance = Misc1.distance(Player.y, Player.x, newY.value(), newX.value());
					doTurn = true;
				}
			}
	    }
	}
	
	/**
	 * Creatures can cast spells too. (Dragon Breath) -RAK-
	 * 
	 * @param monsterIndex
	 * @return
	 */
	/* cast_spell = true if creature changes position	*/
	/* took_turn  = true if creature casts a spell		*/
	public static boolean monsterCastSpell(int monsterIndex) {
		if (Variable.death) {
			return false;
		}
		
		MonsterType monster = Monsters.monsterList[monsterIndex];
		CreatureType creature = Monsters.creatureList[monster.index];
		int chance = (creature.spells & Constants.CS_FREQ);
		boolean tookTurn = false;
		// 1 in x chance of casting spell
		if (Rnd.randomInt(chance) != 1) {
			tookTurn = false;
		// Must be within certain range
		} else if (monster.currDistance > Constants.MAX_SPELL_DIS) {
			tookTurn = false;
		// Must have unobstructed Line-Of-Sight
		} else if (!Misc1.isInLineOfSight(Player.y, Player.x, monster.y, monster.x)) {
			tookTurn = false;
		} else { // Creature is going to cast a spell
			tookTurn = true;
			// Check to see if monster should be lit.
			updateMonster(monsterIndex);
			
			// Describe the attack
			StringBuilder creatureDesc = new StringBuilder();
			if (monster.monsterLight) {
				creatureDesc.append(String.format("The %s ", creature.name));
			} else {
				creatureDesc.append("It ");
			}
			
			// For "DIED_FROM" string
			String deathDesc;
			if ((Constants.CM_WIN & creature.cmove) != 0) {
				deathDesc = String.format("The %s", creature.name);
			} else if (Desc.isVowel(creature.name.charAt(0))) {
				deathDesc = String.format("an %s", creature.name);
			} else {
				deathDesc = String.format("a %s", creature.name);
			}
			// End DIED_FROM
			
			// Extract all possible spells into spell_choice
			IntPointer i = new IntPointer(creature.spells & ~Constants.CS_FREQ);
			int choices = 0;
			int[] spellChoice = new int[30];
			while (i.value() != 0) {
				spellChoice[choices] = Misc1.firstBitPos(i);
				choices++;
			}
			
			// Choose a spell to cast
			int thrownSpell = spellChoice[Rnd.randomInt(choices) - 1];
			thrownSpell++;
			// all except teleport_away() and drain mana spells always disturb
			if (thrownSpell > 6 && thrownSpell != 17) {
				Moria1.disturbPlayer(true, false);
			}
			
			// save some code/data space here, with a small time penalty
			if ((thrownSpell < 14 && thrownSpell > 6) || thrownSpell == 16) {
				creatureDesc.append("casts a spell.");
				IO.printMessage(creatureDesc.toString());
			}
			
			// Cast the spell.
			switch (thrownSpell) {
			case 5: // Teleport Short
				Spells.teleportMonsterAway(monsterIndex, 5);
				break;
			case 6: // Teleport Long
				Spells.teleportMonsterAway(monsterIndex, Constants.MAX_SIGHT);
				break;
			case 7: // Teleport To
				Spells.teleportPlayerTo(monster.y, monster.x);
				break;
			case 8: //  Light Wound
				if (Misc3.playerSavingThrow()) {
					IO.printMessage("You resist the effects of the spell.");
				} else {
					Moria1.takeHit(Misc1.damageRoll(3, 8), deathDesc);
				}
				break;
			case 9: // Serious Wound
				if (Misc3.playerSavingThrow()) {
					IO.printMessage("You resist the effects of the spell.");
				} else {
					Moria1.takeHit(Misc1.damageRoll(8, 8), deathDesc);
				}
				break;
			case 10: // Hold Person
				if (Player.py.flags.freeAct) {
					IO.printMessage("You are unaffected.");
				} else if (Misc3.playerSavingThrow()) {
					IO.printMessage("You resist the effects of the spell.");
				} else if (Player.py.flags.paralysis > 0) {
					Player.py.flags.paralysis += 2;
				} else {
					Player.py.flags.paralysis = Rnd.randomInt(5)+4;
				}
				break;
			case 11: // Cause Blindness
				if (Misc3.playerSavingThrow()) {
					IO.printMessage("You resist the effects of the spell.");
				} else if (Player.py.flags.blind > 0) {
					Player.py.flags.blind += 6;
				} else {
					Player.py.flags.blind += 12 + Rnd.randomInt(3);
				}
				break;
			case 12: // Cause Confuse
				if (Misc3.playerSavingThrow()) {
					IO.printMessage("You resist the effects of the spell.");
				} else if (Player.py.flags.confused > 0) {
					Player.py.flags.confused += 2;
				} else {
					Player.py.flags.confused = Rnd.randomInt(5) + 3;
				}
				break;
			case 13: // Cause Fear
				if (Misc3.playerSavingThrow()) {
					IO.printMessage("You resist the effects of the spell.");
				} else if (Player.py.flags.afraid > 0) {
					Player.py.flags.afraid += 2;
				} else {
					Player.py.flags.afraid = Rnd.randomInt(5) + 3;
				}
				break;
			case 14: // Summon Monster
				creatureDesc.append("magically summons a monster!");
				IO.printMessage(creatureDesc.toString());
				IntPointer y = new IntPointer(Player.y);
				IntPointer x = new IntPointer(Player.x);
				// in case compact_monster() is called,it needs monsterIndex
				Variable.hackMonsterIndex = monsterIndex;
				Misc1.summonMonster(y, x, false);
				Variable.hackMonsterIndex = -1;
				updateMonster (Variable.cave[y.value()][x.value()].creatureIndex);
				break;
			case 15: // Summon Undead
				creatureDesc.append("magically summons an undead!");
				IO.printMessage(creatureDesc.toString());
				y = new IntPointer(Player.y);
				x = new IntPointer(Player.x);
				// in case compact_monster() is called,it needs monsterIndex
				Variable.hackMonsterIndex = monsterIndex;
				Misc1.summonUndead(y, x);
				Variable.hackMonsterIndex = -1;
				updateMonster(Variable.cave[y.value()][x.value()].creatureIndex);
				break;
			case 16: // Slow Person
				if (Player.py.flags.freeAct) {
					IO.printMessage("You are unaffected.");
				} else if (Misc3.playerSavingThrow()) {
					IO.printMessage("You resist the effects of the spell.");
				} else if (Player.py.flags.slow > 0) {
					Player.py.flags.slow += 2;
				} else {
					Player.py.flags.slow = Rnd.randomInt(5) + 3;
				}
				break;
			case 17: // Drain Mana
				if (Player.py.misc.currMana > 0) {
					Moria1.disturbPlayer(true, false);
					String outval = String.format("%sdraws psychic energy from you!", creatureDesc);
					IO.printMessage(outval);
					if (monster.monsterLight) {
						outval = String.format("%sappears healthier.", creatureDesc);
						IO.printMessage(outval);
					}
					int r1 = (Rnd.randomInt(creature.level) >> 1) + 1;
					if (r1 > Player.py.misc.currMana) {
						r1 = Player.py.misc.currMana;
						Player.py.misc.currMana = 0;
						Player.py.misc.currManaFraction = 0;
					} else {
						Player.py.misc.currMana -= r1;
					}
					Misc3.printCurrentMana();
					monster.hitpoints += 6 * r1;
				}
				break;
			case 20: // Breath Light
				creatureDesc.append("breathes lightning.");
				IO.printMessage(creatureDesc.toString());
				Spells.breath(Constants.GF_LIGHTNING, Player.y, Player.x,
						(monster.hitpoints / 4), deathDesc, monsterIndex);
				break;
			case 21: // Breath Gas
				creatureDesc.append("breathes gas.");
				IO.printMessage(creatureDesc.toString());
				Spells.breath(Constants.GF_POISON_GAS, Player.y, Player.x,
						(monster.hitpoints / 3), deathDesc, monsterIndex);
				break;
			case 22: // Breath Acid
				creatureDesc.append("breathes acid.");
				IO.printMessage(creatureDesc.toString());
				Spells.breath(Constants.GF_ACID, Player.y, Player.x,
						(monster.hitpoints / 3), deathDesc, monsterIndex);
				break;
			case 23: // Breath Frost
				creatureDesc.append("breathes frost.");
				IO.printMessage(creatureDesc.toString());
				Spells.breath(Constants.GF_FROST, Player.y, Player.x,
						(monster.hitpoints / 3), deathDesc, monsterIndex);
				break;
			case 24: // Breath Fire
				creatureDesc.append("breathes fire.");
				IO.printMessage(creatureDesc.toString());
				Spells.breath(Constants.GF_FIRE, Player.y, Player.x,
						(monster.hitpoints / 3), deathDesc, monsterIndex);
				break;
			default:
				creatureDesc.append("cast unknown spell.");
				IO.printMessage(creatureDesc.toString());
			}
			
			// End of spells
			if (monster.monsterLight) {
				Variable.creatureRecall[monster.index].spells |= 1L << (thrownSpell - 1);
				if ((Variable.creatureRecall[monster.index].spells & Constants.CS_FREQ)
						!= Constants.CS_FREQ) {
					Variable.creatureRecall[monster.index].spells++;
				}
				if (Variable.death && Variable.creatureRecall[monster.index].deaths
						< Constants.MAX_SHORT) {
					Variable.creatureRecall[monster.index].deaths++;
				}
			}
		}
		
		return tookTurn;
	}
	
	private static boolean placeMonsterMultiple(int y, int x, int creatureIndex,
			int monsterIndex, boolean sleep) {
		// in case compact_monster() is called, it needs monsterIndex
		Variable.hackMonsterIndex = monsterIndex;
		// placeMonster() may fail if monster list full.
		boolean result = Misc1.placeMonster(y, x, creatureIndex, sleep);
		Variable.hackMonsterIndex = -1;
		
		if (!result) {
			return false;
		}
		
		Monsters.totalMonsterMultiples++;
		return checkMonsterLight(y, x);
	}
	
	/**
	 * Places creature adjacent to given location -RAK-
	 * 
	 * @param y
	 * @param x
	 * @param creatureIndex
	 * @param monsterIndex
	 * @return Whether the monster multiplied
	 */
	/* Rats and Flys are fun! */
	public static boolean multiplyMonster(int y, int x, int creatureIndex, int monsterIndex) {
		for (int i = 0; i <= 18; i++) {
			int newY = y - 2 + Rnd.randomInt(3);
			int newX = x - 2 + Rnd.randomInt(3);
			
			if (!Misc1.isInBounds(newY, newX)) {
				continue;
			}
			
			// don't create a new creature on top of the old one, that causes
			// invincible/invisible creatures to appear
			if (newY == y && newX == x) {
				continue;
			}
			
			CaveType cavePos = Variable.cave[newY][newX];
			if (cavePos.isOccupied() && !cavePos.isOccupiedByMonster()) {
				continue;
			}
			
			// Creature there already?
			if (!cavePos.isOccupied()) {
				// All clear, place a monster
				return placeMonsterMultiple(newY, newX, creatureIndex, monsterIndex, false);
			}
			
			// Some critters are cannibalistic!
			if ((Monsters.creatureList[creatureIndex].cmove & Constants.CM_EATS_OTHER) == 0) {
				continue;
			}
			
			// Can only eat a monster weaker than itself
			if (Monsters.creatureList[creatureIndex].mexp
					< Monsters.creatureList[Monsters.monsterList[cavePos.creatureIndex].index].mexp) {
				continue;
			}
			
			// Check the experience level -CJS-
			// It ate an already processed monster. Handle normally.
			if (monsterIndex < cavePos.creatureIndex) {
				Moria3.deleteMonster(cavePos.creatureIndex);
			
			// If it eats this monster, an already processed monster
			// will take its place, causing all kinds of havoc.
			// Delay the kill a bit.
			} else {
				Moria3.deleteMonster1(cavePos.creatureIndex);
			}
			
			return placeMonsterMultiple(newY, newX, creatureIndex, monsterIndex, false);
		}
		
		return false;
	}
	
	/**
	 * Move the critters about the dungeon -RAK-
	 * 
	 * @param monsterIndex
	 * @param rcmove
	 */
	public static void monsterMove(int monsterIndex, IntPointer rcmove) {
		MonsterType monster = Monsters.monsterList[monsterIndex];
		CreatureType creature = Monsters.creatureList[monster.index];
		
		// Does the critter multiply?
		// rest could be negative, to be safe,
		// only use mod with positive values.
		int restVal = Math.abs(Player.py.flags.rest);
		if ((creature.cmove & Constants.CM_MULTIPLY) != 0
				&& Constants.MAX_MON_MULT >= Monsters.totalMonsterMultiples
				&& (restVal % Constants.MON_MULT_ADJ) == 0) {
			int surroundingMonsters = 0;
			for (int i = monster.y - 1; i <= monster.y + 1; i++) {
				for (int j = monster.x - 1; j <= monster.x + 1; j++) {
					if (Misc1.isInBounds(i, j) && (Variable.cave[i][j].creatureIndex > 1)) {
						surroundingMonsters++;
					}
				}
			}
			
			// can't call randint with a value of zero, increment counter
			// to allow creature multiplication
			if (surroundingMonsters == 0) {
				surroundingMonsters++;
			}
			
			if (surroundingMonsters < 4
					&& Rnd.randomInt(surroundingMonsters * Constants.MON_MULT_ADJ) == 1) {
				if (multiplyMonster(monster.y, monster.x, monster.index, monsterIndex)) {
					rcmove.value(rcmove.value() | Constants.CM_MULTIPLY);
				}
			}
		}
		
		boolean move_test = false;
		int[] monsterMoves = new int[9];
		
		// if in wall, must immediately escape to a clear area
		if ((creature.cmove & Constants.CM_PHASE) == 0
				&& Variable.cave[monster.y][monster.x].fval >= Constants.MIN_CAVE_WALL) {
			// If the monster is already dead, don't kill it again!
			// This can happen for monsters moving faster than the player. They
			// will get multiple moves, but should not if they die on the first
			// move. This is only a problem for monsters stuck in rock.
			if (monster.hitpoints < 0) {
				return;
			}
			
			int k = 0;
			int dir = 1;
			// note direction of for loops matches direction of keypad from 1 to 9
			// do not allow attack against the player
			for (int i = monster.y + 1; i >= monster.y - 1; i--) {
				for (int j = monster.x - 1; j <= monster.x + 1; j++) {
					if (dir != 5
							&& Variable.cave[i][j].fval <= Constants.MAX_OPEN_SPACE
							&& Variable.cave[i][j].creatureIndex != 1) {
						monsterMoves[k++] = dir;
					}
					dir++;
				}
			}
			if (k != 0) {
				// put a random direction first
				dir = Rnd.randomInt(k) - 1;
				int tmp = monsterMoves[0];
				monsterMoves[0] = monsterMoves[dir];
				monsterMoves[dir] = tmp;
				makeMove(monsterIndex, monsterMoves, rcmove);
				// this can only fail if mm[0] has a rune of protection
			}
			
			// if still in a wall, let it dig itself out,
			// but also apply some more damage
			if (Variable.cave[monster.y][monster.x].fval >= Constants.MIN_CAVE_WALL) {
				// in case the monster dies, may need to call fix1_delete_monster()
				// instead of delete_monsters()
				Variable.hackMonsterIndex = monsterIndex;
				int trappedMonster = Moria3.monsterTakeHit(monsterIndex, Misc1.damageRoll(8, 8));
				Variable.hackMonsterIndex = -1;
				if (trappedMonster >= 0) {
					IO.printMessage("You hear a scream muffled by rock!");
					Misc3.printExperience();
				} else {
					IO.printMessage ("A creature digs itself out from the rock!");
					Moria3.tunnelThroughWall(monster.y, monster.x, 1, 0);
				}
			}
			return; // monster movement finished
		
		// Creature is confused or undead turned?
		} else if (monster.confused > 0) {
			// Undead only get confused from turn undead, so they should flee
			if ((creature.cdefense & Constants.CD_UNDEAD) != 0) {
				getMoveDirections(monsterIndex,monsterMoves);
				monsterMoves[0] = 10 - monsterMoves[0];
				monsterMoves[1] = 10 - monsterMoves[1];
				monsterMoves[2] = 10 - monsterMoves[2];
				monsterMoves[3] = Rnd.randomInt(9); // May attack only if cornered
				monsterMoves[4] = Rnd.randomInt(9);
			} else {
				monsterMoves[0] = Rnd.randomInt(9);
				monsterMoves[1] = Rnd.randomInt(9);
				monsterMoves[2] = Rnd.randomInt(9);
				monsterMoves[3] = Rnd.randomInt(9);
				monsterMoves[4] = Rnd.randomInt(9);
			}
			// don't move him if he is not supposed to move!
			if ((creature.cmove & Constants.CM_ATTACK_ONLY) == 0) {
				makeMove(monsterIndex, monsterMoves, rcmove);
			}
			monster.confused--;
			move_test = true;
		
		// Creature may cast a spell
		} else if ((creature.spells & Constants.CS_FREQ) != 0) {
			move_test = monsterCastSpell(monsterIndex);
		}
		if (!move_test) {
			// 75% random movement
			if ((creature.cmove & Constants.CM_75_RANDOM) != 0
					&& Rnd.randomInt(100) < 75) {
				monsterMoves[0] = Rnd.randomInt(9);
				monsterMoves[1] = Rnd.randomInt(9);
				monsterMoves[2] = Rnd.randomInt(9);
				monsterMoves[3] = Rnd.randomInt(9);
				monsterMoves[4] = Rnd.randomInt(9);
				rcmove.value(rcmove.value() | Constants.CM_75_RANDOM);
				makeMove(monsterIndex, monsterMoves, rcmove);
			
			// 40% random movement
			} else if ((creature.cmove & Constants.CM_40_RANDOM) != 0
					&& Rnd.randomInt(100) < 40) {
				monsterMoves[0] = Rnd.randomInt(9);
				monsterMoves[1] = Rnd.randomInt(9);
				monsterMoves[2] = Rnd.randomInt(9);
				monsterMoves[3] = Rnd.randomInt(9);
				monsterMoves[4] = Rnd.randomInt(9);
				rcmove.value(rcmove.value() | Constants.CM_40_RANDOM);
				makeMove(monsterIndex, monsterMoves, rcmove);
			
			// 20% random movement
			} else if ((creature.cmove & Constants.CM_20_RANDOM) != 0
					&& Rnd.randomInt(100) < 20) {
				monsterMoves[0] = Rnd.randomInt(9);
				monsterMoves[1] = Rnd.randomInt(9);
				monsterMoves[2] = Rnd.randomInt(9);
				monsterMoves[3] = Rnd.randomInt(9);
				monsterMoves[4] = Rnd.randomInt(9);
				rcmove.value(rcmove.value() | Constants.CM_20_RANDOM);
				makeMove(monsterIndex, monsterMoves, rcmove);
			
			// Normal movement
			} else if ((creature.cmove & Constants.CM_MOVE_NORMAL) != 0) {
				if (Rnd.randomInt(200) == 1) {
					monsterMoves[0] = Rnd.randomInt(9);
					monsterMoves[1] = Rnd.randomInt(9);
					monsterMoves[2] = Rnd.randomInt(9);
					monsterMoves[3] = Rnd.randomInt(9);
					monsterMoves[4] = Rnd.randomInt(9);
				} else {
					getMoveDirections(monsterIndex, monsterMoves);
				}
				rcmove.value(rcmove.value() | Constants.CM_MOVE_NORMAL);
				makeMove(monsterIndex, monsterMoves, rcmove);
			
			// Attack, but don't move
			} else if ((creature.cmove & Constants.CM_ATTACK_ONLY) != 0) {
				if (monster.currDistance < 2) {
					getMoveDirections(monsterIndex, monsterMoves);
					makeMove(monsterIndex, monsterMoves, rcmove);
				} else {
					// Learn that the monster does not move when it should have
					// moved, but didn't.
					rcmove.value(rcmove.value() | Constants.CM_ATTACK_ONLY);
				}
			} else if ((creature.cmove & Constants.CM_ONLY_MAGIC) != 0
					&& (monster.currDistance < 2)) {
				// A little hack for Quylthulgs, so that one will eventually notice
				// that they have no physical attacks.
				if (Variable.creatureRecall[monster.index].attacks[0] < Constants.MAX_UCHAR) {
					Variable.creatureRecall[monster.index].attacks[0]++;
				}
				// Another little hack for Quylthulgs, so that one can eventually
				// learn their speed.
				if (Variable.creatureRecall[monster.index].attacks[0] > 20) {
					Variable.creatureRecall[monster.index].cmove |= Constants.CM_ONLY_MAGIC;
				}
			}
		}
	}
	
	/**
	 * Creature movement and attacking -RAK-
	 * 
	 * @param doAttack Whether the creature is going to attack
	 */
	public static void creatures(boolean doAttack) {
		// Process the monsters
		for (int i = Monsters.freeMonsterIndex - 1; i >= Constants.MIN_MONIX && !Variable.death; i--) {
			MonsterType monster = Monsters.monsterList[i];
			// Get rid of an eaten/breathed on monster. Note: Be sure not to
			// process this monster. This is necessary because we can't delete
			// monsters while scanning the m_list here.
			if (monster.hitpoints < 0) {
				Moria3.deleteMonster2(i);
				continue;
			}
			
			monster.currDistance = Misc1.distance(Player.y, Player.x, monster.y, monster.x);
			if (doAttack) {
				int numMoves = getMovesPerTurn(monster.speed);
				if (numMoves <= 0) {
					updateMonster(i);
				} else {
					while (numMoves > 0) {
						numMoves--;
						boolean wake = false;
						boolean ignore = false;
						IntPointer rcmove = new IntPointer();
						if (monster.monsterLight
								|| monster.currDistance <= Monsters.creatureList[monster.index].aoeRadius
								|| ((Monsters.creatureList[monster.index].cmove & Constants.CM_PHASE) == 0
										&& Variable.cave[monster.y][monster.x].fval >= Constants.MIN_CAVE_WALL)) {
							// Monsters trapped in rock must be given a turn also,
							// so that they will die/dig out immediately.
							
							if (monster.sleep > 0) {
								if (Player.py.flags.aggravate > 0) {
									monster.sleep = 0;
								} else if ((Player.py.flags.rest == 0 && Player.py.flags.paralysis < 1)
										|| (Rnd.randomInt(50) == 1)) {
									int notice = Rnd.randomInt(1024);
									if (notice * notice * notice <= (1L << (29 - Player.py.misc.stealth))) {
										monster.sleep -= (100 / monster.currDistance);
										if (monster.sleep > 0) {
											ignore = true;
										} else {
											wake = true;
											// force it to be exactly zero
											monster.sleep = 0;
										}
									}
								}
							}
							
							if (monster.stunned != 0) {
								// NOTE: Balrog = 100*100 = 10000, it always
								// recovers instantly
								int level = Monsters.creatureList[monster.index].level;
								if (Rnd.randomInt(5000) < level * level) {
									monster.stunned = 0;
								} else {
									monster.stunned--;
								}
								
								if (monster.stunned == 0) {
									if (monster.monsterLight) {
										StringBuilder cdesc = new StringBuilder();
										cdesc.append(String.format("The %s ",
												Monsters.creatureList[monster.index].name));
										cdesc.append("recovers and glares at you.");
										IO.printMessage(cdesc.toString());
									}
								}
							}
							if ((monster.sleep == 0) && (monster.stunned == 0)) {
								monsterMove(i, rcmove);
							}
						}
						
						updateMonster(i);
						if (monster.monsterLight) {
							MonsterRecallType recallType = Variable.creatureRecall[monster.index];
							if (wake) {
								if (recallType.wake < Constants.MAX_UCHAR) {
									recallType.wake++;
								}
							} else if (ignore) {
								if (recallType.ignore < Constants.MAX_UCHAR) {
									recallType.ignore++;
								}
							}
							recallType.cmove |= rcmove.value();
						}
					}
				}
			} else {
				updateMonster(i);
			}
			
			// Get rid of an eaten/breathed on monster. This is necessary because
			// we can't delete monsters while scanning the m_list here. This
			// monster may have been killed during monsterMove().
			if (monster.hitpoints < 0) {
				Moria3.deleteMonster2(i);
			}
		}
	}
}
