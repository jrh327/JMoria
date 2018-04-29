/*
 * Recall.java: print out monster memory info			-CJS-
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

import net.jonhopkins.moria.types.CreatureType;
import net.jonhopkins.moria.types.MonsterRecallType;

public class Recall {
	
	private static String[] descAttackType = {
			"do something undefined",
			"attack",
			"weaken",
			"confuse",
			"terrify",
			"shoot flames",
			"shoot acid",
			"freeze",
			"shoot lightning",
			"corrode",
			"blind",
			"paralyse",
			"steal money",
			"steal things",
			"poison",
			"reduce dexterity",
			"reduce constitution",
			"drain intelligence",
			"drain wisdom",
			"lower experience",
			"call for help",
			"disenchant",
			"eat your food",
			"absorb light",
			"absorb charges"
	};
	private static String[] descAttackMethod = {
			"make an undefined advance",
			"hit",
			"bite",
			"claw",
			"sting",
			"touch",
			"kick",
			"gaze",
			"breathe",
			"spit",
			"wail",
			"embrace",
			"crawl on you",
			"release spores",
			"beg",
			"slime you",
			"crush",
			"trample",
			"drool",
			"insult"
	};
	private static String[] descHowMuch = {
			" not at all",
			" a bit",
			"",
			" quite",
			" very",
			" most",
			" highly",
			" extremely"
	};
	private static String[] descMove = {
			"move invisibly",
			"open doors",
			"pass through walls",
			"kill weaker creatures",
			"pick up objects",
			"breed explosively"
	};
	private static String[] descSpell = {
			"teleport short distances",
			"teleport long distances",
			"teleport its prey",
			"cause light wounds",
			"cause serious wounds",
			"paralyse its prey",
			"induce blindness",
			"confuse",
			"terrify",
			"summon a monster",
			"summon the undead",
			"slow its prey",
			"drain mana",
			"unknown 1",
			"unknown 2"
	};
	private static String[] descBreath = {
			"lightning",
			"poison gases",
			"acid",
			"frost",
			"fire"
	};
	private static String[] descWeakness = {
			"frost",
			"fire",
			"poison",
			"acid",
			"bright light",
			"rock remover"
	};
	
	private static String roffbuf; // Line buffer.
	//static char *roffp; // Pointer into line buffer.
	private static String roffp;
	private static int roffpline; // Place to print line now being loaded.
	
	private static String plural(int count, String singular, String plural) {
		return (count == 1) ? singular : plural;
	}
	
	// Number of kills needed for information.
	
	// the higher the level of the monster, the fewer the kills you need
	private static boolean knowArmor(int level, int killed) {
		return killed > (304 / (4 + level));
	}
	// the higher the level of the monster, the fewer the attacks you need,
	// the more damage an attack does, the more attacks you need
	private static boolean knowDamage(int level, int attacks, int damage) {
		return (4 + level) * attacks > 80 * damage;
	}
	
	private Recall() { }
	
	/**
	 * Do we know anything about this monster?
	 * 
	 * @param monsterIndex
	 * @return
	 */
	public static boolean canRecallMonster(int monsterIndex) {
		if (Variable.isWizard) {
			return true;
		}
		
		MonsterRecallType memory = Variable.creatureRecall[monsterIndex];
		if (memory.cmove != 0 || memory.cdefense != 0
				|| memory.kills != 0 || memory.spells != 0
				|| memory.deaths != 0) {
			return true;
		}
		
		for (int i = 0; i < 4; i++) {
			if (memory.attacks[i] != 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Print out what we have discovered about this monster.
	 * 
	 * @param monsterIndex
	 * @return
	 */
	public static char recallMonster(int monsterIndex) {
		MonsterRecallType memory = Variable.creatureRecall[monsterIndex];
		CreatureType creature = Monsters.creatureList[monsterIndex];
		MonsterRecallType saveMem = saveMemory(memory, creature);
		
		roffpline = 0;
		roffp = roffbuf;
		// the Constants.CM_WIN property is always known, set it if a win monster
		roff(String.format("The %s:\n", creature.name));
		
		printConflictHistory(memory);
		
		boolean continueLine = printKnownLevel(memory, creature);
		continueLine = printSpeed(continueLine, memory, creature);
		continueLine = printAggressive(continueLine, memory, creature);
		continueLine = printMagic(continueLine, memory, creature);
		
		printQuality(memory, creature);
		printKnownSpells(memory, creature);
		printArmor(memory, creature);
		
		printSpecialAbilities(memory, creature);
		printWeaknesses(memory, creature);
		printAwareness(memory, creature);
		printDrops(memory, creature);
		printKnownAttacks(memory, creature);
		
		// Always know the win creature.
		if ((creature.cmove & Constants.CM_WIN) != 0) {
			roff(" Killing one of these wins the game!");
		}
		roff("\n");
		IO.print("--pause--", roffpline, 0);
		restoreMemory(saveMem, memory);
		
		return IO.inkey();
	}
	
	private static int getMovement(MonsterRecallType memory, CreatureType creature) {
		return memory.cmove | (Constants.CM_WIN & creature.cmove);
	}
	
	private static int getSpells(MonsterRecallType memory, CreatureType creature) {
		return memory.spells & creature.spells & ~Constants.CS_FREQ;
	}
	
	private static int getDefense(MonsterRecallType memory, CreatureType creature) {
		return memory.cdefense & creature.cdefense;
	}
	
	private static MonsterRecallType saveMemory(MonsterRecallType memory, CreatureType creature) {
		MonsterRecallType saveMem = new MonsterRecallType();
		if (!Variable.isWizard) {
			return saveMem;
		}
		
		saveMem.cmove = memory.cmove;
		saveMem.spells = memory.spells;
		saveMem.kills = memory.kills;
		saveMem.deaths = memory.deaths;
		saveMem.cdefense = memory.cdefense;
		saveMem.wake = memory.wake;
		saveMem.ignore = memory.ignore;
		System.arraycopy(memory.attacks, 0, saveMem.attacks, 0, memory.attacks.length);
		
		memory.kills = Constants.MAX_SHORT;
		memory.wake = Constants.MAX_UCHAR;
		memory.ignore = Constants.MAX_UCHAR;
		int j = ((creature.cmove & Constants.CM_4D2_OBJ) * 8)
				+ ((creature.cmove & Constants.CM_2D2_OBJ) * 4)
				+ ((creature.cmove & Constants.CM_1D2_OBJ) * 2)
				+ (creature.cmove & Constants.CM_90_RANDOM)
				+ (creature.cmove & Constants.CM_60_RANDOM);
		memory.cmove = (creature.cmove & ~Constants.CM_TREASURE) | (j << Constants.CM_TR_SHIFT);
		memory.cdefense = creature.cdefense;
		memory.spells = creature.spells | Constants.CS_FREQ;
		
		if ((creature.spells & Constants.CS_FREQ) != 0) {
			memory.spells = creature.spells | Constants.CS_FREQ;
		} else {
			memory.spells = creature.spells;
		}
		
		int[] damage = creature.damage;
		for (int i = 0; i < 4; i++) {
			if (damage[i] != 0) {
				break;
			}
			memory.attacks[i] = Constants.MAX_UCHAR;
		}
		
		// A little hack to enable the display of info for Quylthulgs.
		if ((memory.cmove & Constants.CM_ONLY_MAGIC) != 0) {
			memory.attacks[0] = Constants.MAX_UCHAR;
		}
		
		return saveMem;
	}
	
	private static void restoreMemory(MonsterRecallType saveMem, MonsterRecallType memory) {
		if (!Variable.isWizard) {
			return;
		}
		
		memory.cmove = saveMem.cmove;
		memory.spells = saveMem.spells;
		memory.kills = saveMem.kills;
		memory.deaths = saveMem.deaths;
		memory.cdefense = saveMem.cdefense;
		memory.wake = saveMem.wake;
		memory.ignore = saveMem.ignore;
		System.arraycopy(saveMem.attacks, 0, memory.attacks, 0, memory.attacks.length);
	}
	
	private static void printConflictHistory(MonsterRecallType memory) {
		if (memory.deaths > 0) {
			roff(String.format("%d of the contributors to your monster memory %s",
					memory.deaths, plural(memory.deaths, "has", "have")));
			roff(" been killed by this creature, and ");
			if (memory.kills == 0) {
				roff("it is not ever known to have been defeated.");
			} else {
				roff(String.format("at least %d of the beasts %s been exterminated.",
						memory.kills, plural(memory.kills, "has", "have")));
			}
		} else if (memory.kills > 0) {
			roff(String.format("At least %d of these creatures %s",
					memory.kills, plural(memory.kills, "has", "have")));
			roff(" been killed by contributors to your monster memory.");
		} else {
			roff("No known battles to the death are recalled.");
		}
	}
	
	private static boolean printKnownLevel(MonsterRecallType memory, CreatureType creature) {
		// Immediately obvious.
		if (creature.level == 0) {
			roff(" It lives in the town");
			return true;
		} else if (memory.kills > 0) {
			// The Balrog is a level 100 monster, but appears at 50 feet.
			int level = creature.level;
			if (level > Constants.WIN_MON_APPEAR) {
				level = Constants.WIN_MON_APPEAR;
			}
			
			roff(String.format(" It is normally found at depths of %d feet", level * 50));
			return true;
		}
		
		return false;
	}
	
	private static boolean printSpeed(boolean continueLine, MonsterRecallType memory, CreatureType creature) {
		// the c_list speed value is 10 greater, so that it can be a int8u
		int monsterMovement = getMovement(memory, creature);
		final int mspeed = creature.speed - 10;
		if ((monsterMovement & Constants.CM_ALL_MV_FLAGS) != 0) {
			if (continueLine) {
				roff(", and");
			} else {
				roff(" It");
				continueLine = true;
			}
			
			roff(" moves");
			if ((monsterMovement & Constants.CM_RANDOM_MOVE) != 0) {
				roff(descHowMuch[(monsterMovement & Constants.CM_RANDOM_MOVE) >> 3]);
				roff(" erratically");
			}
			
			if (mspeed == 1) {
				roff(" at normal speed");
			} else {
				if ((monsterMovement & Constants.CM_RANDOM_MOVE) != 0) {
					roff(", and");
				}
				if (mspeed <= 0) {
					if (mspeed == -1) {
						roff(" very");
					} else if (mspeed < -1) {
						roff(" incredibly");
					}
					roff(" slowly");
				} else {
					if (mspeed == 3) {
						roff(" very");
					} else if (mspeed > 3) {
						roff(" unbelievably");
					}
					roff(" quickly");
				}
			}
		}
		
		return continueLine;
	}
	
	private static boolean printAggressive(boolean continueLine, MonsterRecallType memory, CreatureType creature) {
		int monsterMovement = getMovement(memory, creature);
		if ((monsterMovement & Constants.CM_ATTACK_ONLY) != 0) {
			if (continueLine) {
				roff(", but");
			} else {
				roff(" It");
				continueLine = true;
			}
			roff(" does not deign to chase intruders");
		}
		
		return continueLine;
	}
	
	private static boolean printMagic(boolean continueLine, MonsterRecallType memory, CreatureType creature) {
		int rcmove = getMovement(memory, creature);
		if ((rcmove & Constants.CM_ONLY_MAGIC) != 0) {
			if (continueLine) {
				roff (", but");
			} else {
				roff (" It");
				continueLine = true;
			}
			roff (" always moves and attacks by using magic");
		}
		if (continueLine) {
			roff(".");
		}
		return continueLine;
	}
	
	private static void printQuality(MonsterRecallType memory, CreatureType creature) {
		// Kill it once to know experience, and quality (evil, undead, monsterous).
		// The quality of being a dragon is obvious.
		if (memory.kills <= 0) {
			return;
		}
		
		roff(" A kill of this");
		if ((creature.cdefense & Constants.CD_ANIMAL) != 0) {
			roff(" natural");
		}
		if ((creature.cdefense & Constants.CD_EVIL) != 0) {
			roff(" evil");
		}
		if ((creature.cdefense & Constants.CD_UNDEAD) != 0) {
			roff(" undead");
		}
		
		// calculate the integer exp part, can be larger than 64K when first
		// level character looks at Balrog info, so must store in long
		int exp = creature.mexp * creature.level / Player.py.misc.level;
		// calculate the fractional exp part scaled by 100,
		// must use long arithmetic to avoid overflow
		int expFraction = ((creature.mexp * creature.level % Player.py.misc.level)
				* 1000 / Player.py.misc.level + 5) / 10;
		
		roff(String.format(" creature is worth %d.%02d point%s",
				exp, expFraction, (exp == 1 && expFraction == 0) ? "" : "s"));
		
		String suffix;
		if (Player.py.misc.level / 10 == 1) {
			suffix = "th";
		} else {
			int ones = Player.py.misc.level % 10;
			if (ones == 1) {
				suffix = "st";
			} else if (ones == 2) {
				suffix = "nd";
			} else if (ones == 3) {
				suffix = "rd";
			} else {
				suffix = "th";
			}
		}
		
		int level = Player.py.misc.level;
		String article;
		if (level == 8 || level == 11 || level == 18) {
			article = "an";
		} else {
			article = "a";
		}
		
		roff(String.format(" for a%s %d%s level character.", article, level, suffix));
	}
	
	private static void printKnownSpells(MonsterRecallType memory, CreatureType creature) {
		// Spells known, if have been used against us.
		// Breath weapons or resistance might be known only because we cast spells 
		// at it.
		boolean firstLine = true;
		final int monsterSpells = getSpells(memory, creature);
		int spells = monsterSpells;
		for (int i = 0; (spells & Constants.CS_BREATHE) != 0; i++) {
			if ((spells & (Constants.CS_BR_LIGHT << i)) != 0) {
				spells &= ~(Constants.CS_BR_LIGHT << i);
				if (firstLine) {
					if ((memory.spells & Constants.CS_FREQ) != 0) {
						roff(" It can breathe ");
					} else {
						roff(" It is resistant to ");
					}
					firstLine = false;
				} else if ((spells & Constants.CS_BREATHE) != 0) {
					roff(", ");
				} else {
					roff(" and ");
				}
				roff(descBreath[i]);
			}
		}
		
		boolean continueLine = true;
		for (int i = 0; (spells & Constants.CS_SPELLS) != 0; i++) {
			if ((spells & (Constants.CS_TEL_SHORT << i)) != 0) {
				spells &= ~(Constants.CS_TEL_SHORT << i);
				if (continueLine) {
					if ((monsterSpells & Constants.CS_BREATHE) != 0) {
						roff(", and is also");
					} else {
						roff(" It is");
					}
					roff(" magical, casting spells which ");
					continueLine = false;
				} else if ((spells & Constants.CS_SPELLS) != 0) {
					roff(", ");
				} else {
					roff(" or ");
				}
				roff(descSpell[i]);
			}
		}
		
		if ((monsterSpells & (Constants.CS_BREATHE | Constants.CS_SPELLS)) != 0) {
			if ((memory.spells & Constants.CS_FREQ) > 5) {
				// Could offset by level
				roff(String.format("; 1 time in %d", creature.spells & Constants.CS_FREQ));
			}
			roff(".");
		}
	}
	
	private static void printArmor(MonsterRecallType memory, CreatureType creature) {
		// Do we know how hard they are to kill? Armor class, hit die.
		if (knowArmor(creature.level, memory.kills)) {
			roff(String.format(" It has an armor rating of %d", creature.armorClass));
			roff(String.format(" and a%s life rating of %dd%d.",
					(creature.cdefense & Constants.CD_MAX_HP) != 0 ? " maximized" : "",
					creature.hitDie[0], creature.hitDie[1]));
		}
	}
	
	private static void printSpecialAbilities(MonsterRecallType memory, CreatureType creature) {
		// Do we know how clever they are? Special abilities.
		boolean continueLine = true;
		int monsterMovement = getMovement(memory, creature);
		for (int i = 0; (monsterMovement & Constants.CM_SPECIAL) != 0; i++) {
			if ((monsterMovement & (Constants.CM_INVISIBLE << i)) != 0) {
				monsterMovement &= ~(Constants.CM_INVISIBLE << i);
				if (continueLine) {
					roff(" It can ");
					continueLine = false;
				} else if ((monsterMovement & Constants.CM_SPECIAL) != 0) {
					roff(", ");
				} else {
					roff(" and ");
				}
				roff(descMove[i]);
			}
		}
		if (!continueLine) {
			roff(".");
		}
	}
	
	private static void printWeaknesses(MonsterRecallType memory, CreatureType creature) {
		// Do we know its special weaknesses? Most cdefense flags.
		boolean continueLine = true;
		final int monsterDefense = getDefense(memory, creature);
		int defense = monsterDefense;
		for (int i = 0; (defense & Constants.CD_WEAKNESS) != 0; i++) {
			if ((defense & (Constants.CD_FROST << i)) != 0) {
				defense &= ~(Constants.CD_FROST << i);
				if (continueLine) {
					roff(" It is susceptible to ");
					continueLine = false;
				} else if ((defense & Constants.CD_WEAKNESS) != 0) {
					roff(", ");
				} else {
					roff(" and ");
				}
				roff(descWeakness[i]);
			}
		}
		if (!continueLine) {
			roff(".");
		}
		
		if ((monsterDefense & Constants.CD_INFRA) != 0) {
			roff(" It is warm blooded");
		}
		if ((monsterDefense & Constants.CD_NO_SLEEP) != 0) {
			if ((monsterDefense & Constants.CD_INFRA) != 0) {
				roff(", and");
			} else {
				roff(" It");
			}
			roff(" cannot be charmed or slept");
		}
		if ((monsterDefense & (Constants.CD_NO_SLEEP|Constants.CD_INFRA)) != 0) {
			roff(".");
		}
	}
	
	private static void printAwareness(MonsterRecallType memory, CreatureType creature) {
		// Do we know how aware it is?
		if (memory.wake * memory.wake <= creature.sleep
				&& memory.ignore != Constants.MAX_UCHAR
				&& (creature.sleep != 0 || memory.kills < 10)) {
			return;
		}
		
		roff(" It ");
		if (creature.sleep > 200) {
			roff("prefers to ignore");
		} else if (creature.sleep > 95) {
			roff("pays very little attention to");
		} else if (creature.sleep > 75) {
			roff("pays little attention to");
		} else if (creature.sleep > 45) {
			roff("tends to overlook");
		} else if (creature.sleep > 25) {
			roff("takes quite a while to see");
		} else if (creature.sleep > 10) {
			roff("takes a while to see");
		} else if (creature.sleep > 5) {
			roff("is fairly observant of");
		} else if (creature.sleep > 3) {
			roff("is observant of");
		} else if (creature.sleep > 1) {
			roff("is very observant of");
		} else if (creature.sleep != 0) {
			roff("is vigilant for");
		} else {
			roff("is ever vigilant for");
		}
		roff(String.format(" intruders, which it may notice from %d feet.",
				10 * creature.aoeRadius));
	}
	
	private static void printDrops(MonsterRecallType memory, CreatureType creature) {
		// Do we know what it might carry?
		int monsterMovement = getMovement(memory, creature);
		if ((monsterMovement & (Constants.CM_CARRY_OBJ|Constants.CM_CARRY_GOLD)) == 0) {
			return;
		}
		
		roff(" It may");
		int carry = (monsterMovement & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT;
		if (carry == 1) {
			if ((creature.cmove & Constants.CM_TREASURE) == Constants.CM_60_RANDOM) {
				roff(" sometimes");
			} else {
				roff(" often");
			}
		} else if (carry == 2 && ((creature.cmove & Constants.CM_TREASURE) == (Constants.CM_60_RANDOM|Constants.CM_90_RANDOM))) {
			roff(" often");
		}
		
		roff(" carry");
		String object;
		if ((monsterMovement & Constants.CM_SMALL_OBJ) != 0) {
			object = " small objects";
		} else {
			object = " objects";
		}
		
		if (carry == 1) {
			if ((monsterMovement & Constants.CM_SMALL_OBJ) != 0) {
				object = " a small object";
			} else {
				object = " an object";
			}
		} else if (carry == 2) {
			roff(" one or two");
		} else {
			roff(String.format(" up to %d", carry));
		}
		
		if ((monsterMovement & Constants.CM_CARRY_OBJ) != 0) {
			roff(object);
			if ((monsterMovement & Constants.CM_CARRY_GOLD) != 0) {
				roff(" or treasure");
				if (carry > 1) {
					roff("s");
				}
			}
			roff(".");
		} else if (carry != 1) {
			roff(" treasures.");
		} else {
			roff(" treasure.");
		}
	}
	
	private static void printKnownAttacks(MonsterRecallType memory, CreatureType creature) {
		// We know about attacks it has used on us, and maybe the damage they do.
		// k is the total number of known attacks, used for punctuation
		int knownAttacks = 0;
		for (int j = 0; j < 4; j++) {
			if (memory.attacks[j] != 0) {
				knownAttacks++;
			}
		}
		int[] damage = creature.damage;
		// j counts the attacks as printed, used for punctuation
		int j = 0;
		for (int i = 0; damage[i] != 0 && i < 4; i++) {
			// don't print out unknown attacks
			if (memory.attacks[i] == 0) {
				continue;
			}
			
			int attType = Monsters.monsterAttacks[damage[i]].attackType;
			int attHow = Monsters.monsterAttacks[damage[i]].attackDesc;
			int dice = Monsters.monsterAttacks[damage[i]].attackDice;
			int sides = Monsters.monsterAttacks[damage[i]].attackSides;
			
			j++;
			if (j == 1) {
				roff(" It can ");
			} else if (j == knownAttacks) {
				roff(", and ");
			} else {
				roff(", ");
			}
			
			if (attHow > 19) {
				attHow = 0;
			}
			
			roff(descAttackMethod[attHow]);
			if (attType != 1 || dice > 0 && sides > 0) {
				roff(" to ");
				if (attType > 24) {
					attType = 0;
				}
				roff(descAttackType[attType]);
				if (dice != 0 && sides != 0) {
					if (knowDamage(creature.level, memory.attacks[i], dice * sides)) {
						if (attType == 19) { // Loss of experience
							roff(" by");
						} else {
							roff(" with damage");
						}
						roff(String.format(" %dd%d", dice, sides));
					}
				}
			}
		}
		
		if (j != 0) {
			roff(".");
		} else if (knownAttacks > 0 && memory.attacks[0] >= 10) {
			roff(" It has no physical attacks.");
		} else {
			roff(" Nothing is known about its attack.");
		}
	}
	
	/**
	 * Print out strings, filling up lines as we go.
	 * 
	 * @param p
	 */
	public static void roff(String p) {
		//String q, r;
		// TODO pretty sure this is wrong. fix in master and pull in
		for (int i = 0; i < p.length(); i++) {
			roffp = roffp + p.charAt(i);
			if (p.charAt(i) == '\n' || roffp.length() > 80) {
				int j = i;
				if (p.charAt(i) != '\n') {
					while (p.charAt(j) != ' ') {
						j--;
					}
				}
				
				p = p.substring(0, j);
				IO.print(roffbuf, roffpline, 0);
				roffpline++;
				roffbuf = roffbuf.substring(j);
			}
		}
		/*
		while (*p) {
			*roffp = *p;
			if (*p == '\n' || roffp >= roffbuf + sizeof(roffbuf)-1) {
				q = roffp;
				if (*p != '\n') {
					while (*q != ' ') {
						q--;
					}
				}
				*q = 0;
				io.prt(roffbuf, roffpline, 0);
				roffpline++;
				r = roffbuf;
				while (q < roffp) {
					q++;
					*r = *q;
					r++;
				}
				roffp = r;
			} else {
				roffp++;
			}
			p++;
		}
		*/
	}
}
