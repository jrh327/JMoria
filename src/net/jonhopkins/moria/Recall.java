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
	private String[] desc_atype = {
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
	  "absorb charges" };
	private String[] desc_amethod = {
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
	  "insult" };
	private String[] desc_howmuch = {
	  " not at all",
	  " a bit",
	  "",
	  " quite",
	  " very",
	  " most",
	  " highly",
	  " extremely" };

	private String[] desc_move = {
	  "move invisibly",
	  "open doors",
	  "pass through walls",
	  "kill weaker creatures",
	  "pick up objects",
	  "breed explosively" };
	private String[] desc_spell = {
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
	  "unknown 2" };
	private String[] desc_breath = {
	  "lightning",
	  "poison gases",
	  "acid",
	  "frost",
	  "fire" };
	private String[] desc_weakness = {
	  "frost",
	  "fire",
	  "poison",
	  "acid",
	  "bright light",
	  "rock remover" };
	
	static String roffbuf;		/* Line buffer. */
	//static char *roffp;		/* Pointer into line buffer. */
	static String roffp;
	static int roffpline;		/* Place to print line now being loaded. */
	
	//#define plural(c, ss, sp)	((c) == 1 ? ss : sp)
	private static String plural(int c, String ss, String sp) { return ((c == 1) ? ss : sp); }
	
	/* Number of kills needed for information. */
	
	/* the higher the level of the monster, the fewer the kills you need */
	//#define knowarmor(l,d)		((d) > 304 / (4 + (l)))
	private static boolean knowarmor(int l, int d) { return ((d) > (304 / (4 + (l)))); }
	/* the higher the level of the monster, the fewer the attacks you need,
	 * the more damage an attack does, the more attacks you need */
	//#define knowdamage(l,a,d)	((4 + (l))*(a) > 80 * (d))
	private static boolean knowdamage(int l, int a, int d) { return ((4 + (l)) * (a) > 80 * (d)); }
	
	private IO io;
	private Monsters mon;
	private Player py;
	private Variable var;
	
	private static Recall instance;
	private Recall() { }
	public static Recall getInstance() {
		if (instance == null) {
			instance = new Recall();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		io = IO.getInstance();
		mon = Monsters.getInstance();
		py = Player.getInstance();
		var = Variable.getInstance();
	}
	
	/* Do we know anything about this monster? */
	public boolean bool_roff_recall(int mon_num) {
		MonsterRecallType mp;
		int i;
		
		if (var.wizard) {
			return true;
		}
		mp = var.c_recall[mon_num];
		if (mp.r_cmove != 0 || mp.r_cdefense != 0 || mp.r_kills != 0 || mp.r_spells != 0 || mp.r_deaths != 0) {
			return true;
		}
		for (i = 0; i < 4; i++) {
			if (mp.r_attacks[i] != 0) {
				return true;
			}
		}
		return false;
	}
	
	/* Print out what we have discovered about this monster. */
	public char roff_recall(int mon_num) {
		String p, q;
		int[] pu;
		String temp;
		MonsterRecallType mp;
		CreatureType cp;
		int i, k;
		long j;
		int templong;
		int mspeed;
		long rcmove, rspells;
		int rcdefense;
		MonsterRecallType save_mem = new MonsterRecallType();
		
		mp = var.c_recall[mon_num];
		cp = mon.c_list[mon_num];
		if (var.wizard) {
			save_mem = mp;
			mp.r_kills = Constants.MAX_SHORT;
			mp.r_wake = mp.r_ignore = Constants.MAX_UCHAR;
			j = ((cp.cmove & Constants.CM_4D2_OBJ) * 8)
					+ ((cp.cmove & Constants.CM_2D2_OBJ) * 4)
					+ ((cp.cmove & Constants.CM_1D2_OBJ) * 2)
					+ (cp.cmove & Constants.CM_90_RANDOM)
					+ (cp.cmove & Constants.CM_60_RANDOM);
			mp.r_cmove = (cp.cmove & ~Constants.CM_TREASURE) | (j << Constants.CM_TR_SHIFT);
			mp.r_cdefense = cp.cdefense;
			mp.r_spells = cp.spells | Constants.CS_FREQ;
			j = 0;
			pu = cp.damage;
			
			while (pu[(int)j] != 0 && j < 4) {
				/* Turbo C needs a 16 bit int for the array index.  */
				mp.r_attacks[(int)j] = Constants.MAX_UCHAR;
				j++;
			}
			/* A little hack to enable the display of info for Quylthulgs.  */
			if ((mp.r_cmove & Constants.CM_ONLY_MAGIC) != 0) {
				mp.r_attacks[0] = Constants.MAX_UCHAR;
			}
		}
		roffpline = 0;
		roffp = roffbuf;
		rspells = mp.r_spells & cp.spells & ~Constants.CS_FREQ;
		/* the Constants.CM_WIN property is always known, set it if a win monster */
		rcmove = mp.r_cmove | (Constants.CM_WIN & cp.cmove);
		rcdefense = mp.r_cdefense & cp.cdefense;
		temp = String.format("The %s:\n", cp.name);
		roff(temp);
		/* Conflict history. */
		if(mp.r_deaths > 0) {
			temp = String.format("%d of the contributors to your monster memory %s", mp.r_deaths, plural(mp.r_deaths, "has", "have") );
			roff(temp);
			roff(" been killed by this creature, and ");
			if (mp.r_kills == 0) {
				roff("it is not ever known to have been defeated.");
			} else {
				temp = String.format("at least %d of the beasts %s been exterminated.", mp.r_kills, plural(mp.r_kills, "has", "have") );
				roff(temp);
			}
		} else if (mp.r_kills > 0) {
			temp = String.format("At least %d of these creatures %s", mp.r_kills, plural(mp.r_kills, "has", "have") );
			roff(temp);
			roff(" been killed by contributors to your monster memory.");
		} else {
			roff("No known battles to the death are recalled.");
		}
		/* Immediately obvious. */
		k = 0;
		if (cp.level == 0) {
			roff(" It lives in the town");
			k = 1;
		} else if (mp.r_kills > 0) {
			/* The Balrog is a level 100 monster, but appears at 50 feet.  */
			i = cp.level;
			if (i > Constants.WIN_MON_APPEAR) {
				i = Constants.WIN_MON_APPEAR;
			}
			temp = String.format(" It is normally found at depths of %d feet", i * 50);
			roff(temp);
			k = 1;
		}
		/* the c_list speed value is 10 greater, so that it can be a int8u */
		mspeed = cp.speed - 10;
		if ((rcmove & Constants.CM_ALL_MV_FLAGS) != 0) {
			if (k > 0) {
				roff(", and");
			} else {
				roff(" It");
				k = 1;
			}
			roff(" moves");
			if ((rcmove & Constants.CM_RANDOM_MOVE) != 0) {
				/* Turbo C needs a 16 bit int for the array index.  */
				roff(desc_howmuch[(int)((rcmove & Constants.CM_RANDOM_MOVE) >> 3)]);
				roff(" erratically");
			}
			if (mspeed == 1) {
				roff(" at normal speed");
			} else {
				if ((rcmove & Constants.CM_RANDOM_MOVE) != 0) {
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
		if ((rcmove & Constants.CM_ATTACK_ONLY) != 0) {
			if(k > 0) {
				roff(", but");
			} else {
				roff(" It");
				k = 1;
			}
			roff(" does not deign to chase intruders");
		}
		if ((rcmove & Constants.CM_ONLY_MAGIC) != 0) {
			if (k > 0) {
				roff (", but");
			} else {
				roff (" It");
				k = 1;
			}
			roff (" always moves and attacks by using magic");
		}
		if(k > 0) {
			roff(".");
		}
		/* Kill it once to know experience, and quality (evil, undead, monsterous).
		 * The quality of being a dragon is obvious. */
		if (mp.r_kills > 0) {
			roff(" A kill of this");
			if ((cp.cdefense & Constants.CD_ANIMAL) != 0) {
				roff(" natural");
			}
			if ((cp.cdefense & Constants.CD_EVIL) != 0) {
				roff(" evil");
			}
			if ((cp.cdefense & Constants.CD_UNDEAD) != 0) {
				roff(" undead");
			}
			
			/* calculate the integer exp part, can be larger than 64K when first
			 * level character looks at Balrog info, so must store in long */
			templong = cp.mexp * cp.level / py.py.misc.lev;
			/* calculate the fractional exp part scaled by 100,
			 * must use long arithmetic to avoid overflow */
			j = ((cp.mexp * cp.level % py.py.misc.lev) * 1000 / py.py.misc.lev + 5) / 10;
			
			temp = String.format(" creature is worth %d.%02d point%s", templong, j, (templong == 1 && j == 0 ? "" : "s"));
			roff(temp);
			
			if (py.py.misc.lev / 10 == 1) {
				p = "th";
			} else {
				i = py.py.misc.lev % 10;
				if (i == 1) {
					p = "st";
				} else if (i == 2) {
					p = "nd";
				} else if (i == 3) {
					p = "rd";
				} else {
					p = "th";
				}
			}
			i = py.py.misc.lev;
			if (i == 8 || i == 11 || i == 18) {
				q = "n";
			} else {
				q = "";
			}
			temp = String.format(" for a%s %d%s level character.", q, i, p);
			roff(temp);
		}
		/* Spells known, if have been used against us. */
		k = 1;
		j = rspells;
		for (i = 0; (j & Constants.CS_BREATHE) != 0; i++) {
			if ((j & (Constants.CS_BR_LIGHT << i)) != 0) {
				j &= ~(Constants.CS_BR_LIGHT << i);
				if (k > 0) {
					roff(" It can breathe ");
					k = 0;
				} else if ((j & Constants.CS_BREATHE) != 0) {
					roff(", ");
				} else {
					roff(" and ");
				}
				roff(desc_breath[i]);
			}
		}
		k = 1;
		for (i = 0; (j & Constants.CS_SPELLS) != 0; i++) {
			if ((j & (Constants.CS_TEL_SHORT << i)) != 0) {
				j &= ~(Constants.CS_TEL_SHORT << i);
				if (k > 0) {
					if ((rspells & Constants.CS_BREATHE) != 0) {
						roff(", and is also");
					} else {
						roff(" It is");
					}
					roff(" magical, casting spells which ");
					k = 0;
				} else if ((j & Constants.CS_SPELLS) != 0) {
					roff(", ");
				} else {
					roff(" or ");
				}
				roff(desc_spell[i]);
			}
		}
		if ((rspells & (Constants.CS_BREATHE|Constants.CS_SPELLS)) != 0) {
			if ((mp.r_spells & Constants.CS_FREQ) > 5) {
				/* Could offset by level */
				temp = String.format("; 1 time in %d", cp.spells & Constants.CS_FREQ);
				roff(temp);
			}
			roff(".");
		}
		/* Do we know how hard they are to kill? Armor class, hit die. */
		if (knowarmor(cp.level, mp.r_kills)) {
			temp = String.format(" It has an armor rating of %d", cp.ac);
			roff(temp);
			temp = String.format(" and a%s life rating of %dd%d.", ((cp.cdefense & Constants.CD_MAX_HP) != 0 ? " maximized" : ""), cp.hd[0], cp.hd[1]);
			roff(temp);
		}
		/* Do we know how clever they are? Special abilities. */
		k = 1;
		j = rcmove;
		for (i = 0; (j & Constants.CM_SPECIAL) != 0; i++) {
			if ((j & (Constants.CM_INVISIBLE << i)) != 0) {
				j &= ~(Constants.CM_INVISIBLE << i);
				if (k > 0) {
					roff(" It can ");
					k = 0;
				} else if ((j & Constants.CM_SPECIAL) != 0) {
					roff(", ");
				} else {
					roff(" and ");
				}
				roff(desc_move[i]);
			}
		}
		if (k == 0) {
			roff(".");
		}
		/* Do we know its special weaknesses? Most cdefense flags. */
		k = 1;
		j = rcdefense;
		for (i = 0; (j & Constants.CD_WEAKNESS) != 0; i++) {
			if ((j & (Constants.CD_FROST << i)) != 0) {
				j &= ~(Constants.CD_FROST << i);
				if (k > 0) {
					roff(" It is susceptible to ");
					k = 0;
				} else if ((j & Constants.CD_WEAKNESS) != 0) {
					roff(", ");
				} else {
					roff(" and ");
				}
				roff(desc_weakness[i]);
			}
		}
		if (k == 0) {
			roff(".");
		}
		if ((rcdefense & Constants.CD_INFRA) != 0) {
			roff(" It is warm blooded");
		}
		if ((rcdefense & Constants.CD_NO_SLEEP) != 0) {
			if ((rcdefense & Constants.CD_INFRA) != 0) {
				roff(", and");
			} else {
				roff(" It");
			}
			roff(" cannot be charmed or slept");
		}
		if ((rcdefense & (Constants.CD_NO_SLEEP|Constants.CD_INFRA)) != 0) {
			roff(".");
		}
		/* Do we know how aware it is? */
		if (((mp.r_wake * mp.r_wake) > cp.sleep) || mp.r_ignore == Constants.MAX_UCHAR || (cp.sleep == 0 && mp.r_kills >= 10)) {
			roff(" It ");
			if(cp.sleep > 200) {
				roff("prefers to ignore");
			} else if(cp.sleep > 95) {
				roff("pays very little attention to");
			} else if(cp.sleep > 75) {
				roff("pays little attention to");
			} else if(cp.sleep > 45) {
				roff("tends to overlook");
			} else if(cp.sleep > 25) {
				roff("takes quite a while to see");
			} else if(cp.sleep > 10) {
				roff("takes a while to see");
			} else if(cp.sleep > 5) {
				roff("is fairly observant of");
			} else if(cp.sleep > 3) {
				roff("is observant of");
			} else if(cp.sleep > 1) {
				roff("is very observant of");
			} else if(cp.sleep != 0) {
				roff("is vigilant for");
			} else {
				roff("is ever vigilant for");
			}
			temp = String.format(" intruders, which it may notice from %d feet.", 10 * cp.aaf);
			roff(temp);
		}
		/* Do we know what it might carry? */
		if ((rcmove & (Constants.CM_CARRY_OBJ|Constants.CM_CARRY_GOLD)) != 0) {
			roff(" It may");
			j = (rcmove & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT;
			if (j == 1) {
				if ((cp.cmove & Constants.CM_TREASURE) == Constants.CM_60_RANDOM) {
					roff(" sometimes");
				} else {
					roff(" often");
				}
			} else if ((j == 2) && ((cp.cmove & Constants.CM_TREASURE) == (Constants.CM_60_RANDOM|Constants.CM_90_RANDOM))) {
				roff (" often");
			}
			roff(" carry");
			p = " objects";
			if (j == 1) {
				p = " an object";
			} else if (j == 2) {
				roff(" one or two");
			} else {
				temp = String.format(" up to %d", j);
				roff(temp);
			}
			if ((rcmove & Constants.CM_CARRY_OBJ) != 0) {
				roff(p);
				if ((rcmove & Constants.CM_CARRY_GOLD) != 0) {
					roff(" or treasure");
					if (j > 1) {
						roff("s");
					}
				}
				roff(".");
			} else if (j != 1) {
				roff(" treasures.");
			} else {
				roff(" treasure.");
			}
		}
		
		/* We know about attacks it has used on us, and maybe the damage they do. */
		/* k is the total number of known attacks, used for punctuation */
		k = 0;
		for (j = 0; j < 4; j++) {
			/* Turbo C needs a 16 bit int for the array index.  */
			if (mp.r_attacks[(int)j] != 0) {
				k++;
			}
		}
		pu = cp.damage;
		/* j counts the attacks as printed, used for punctuation */
		j = 0;
		for (i = 0; pu[i] != 0 && i < 4; i++) {
			int att_type, att_how, d1, d2;
			
			/* don't print out unknown attacks */
			if (mp.r_attacks[i] == 0) {
				continue;
			}
			
			att_type = mon.monster_attacks[pu[i]].attack_type;
			att_how = mon.monster_attacks[pu[i]].attack_desc;
			d1 = mon.monster_attacks[pu[i]].attack_dice;
			d2 = mon.monster_attacks[pu[i]].attack_sides;
			
			j++;
			if (j == 1) {
				roff(" It can ");
			} else if (j == k) {
				roff(", and ");
			} else {
				roff(", ");
			}
			
			if (att_how > 19) {
				att_how = 0;
			}
			roff(desc_amethod[att_how]);
			if (att_type != 1 || d1 > 0 && d2 > 0) {
				roff(" to ");
				if (att_type > 24) {
					att_type = 0;
				}
				roff(desc_atype[att_type]);
				if (d1 != 0 && d2 != 0) {
					if (knowdamage(cp.level, mp.r_attacks[i], d1 * d2)) {
						if (att_type == 19) {	/* Loss of experience */
							roff(" by");
						} else {
							roff(" with damage");
						}
						temp = String.format(" %dd%d", d1, d2 );
						roff(temp);
					}
				}
			}
		}
		if (j != 0) {
			roff(".");
		} else if (k > 0 && mp.r_attacks[0] >= 10) {
			roff(" It has no physical attacks.");
		} else {
			roff(" Nothing is known about its attack.");
		}
		/* Always know the win creature. */
		if ((cp.cmove & Constants.CM_WIN) != 0) {
			roff(" Killing one of these wins the game!");
		}
		roff("\n");
		io.prt("--pause--", roffpline, 0);
		if (var.wizard) {
			mp.r_cmove = save_mem.r_cmove;
			mp.r_spells = save_mem.r_spells;
			mp.r_kills = save_mem.r_kills;
			mp.r_deaths = save_mem.r_deaths;
			mp.r_cdefense = save_mem.r_cdefense;
			mp.r_wake = save_mem.r_wake;
			mp.r_ignore = save_mem.r_ignore;
			mp.r_attacks = save_mem.r_attacks;
		}
		return io.inkey();
	}
	
	/* Print out strings, filling up lines as we go. */
	public void roff(String p) {
		//String q, r;
		
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
				io.prt(roffbuf, roffpline, 0);
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
