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
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;
import net.jonhopkins.moria.types.MonsterRecallType;

public class Creature {
	
	private Creature() { }
	
	/* Updates screen when monsters move about		-RAK-	*/
	public static void update_mon(int monptr) {
		boolean flag;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		m_ptr = Monsters.m_list[monptr];
		flag = false;
		if ((m_ptr.cdis <= Constants.MAX_SIGHT) && (Player.py.flags.status & Constants.PY_BLIND) == 0 && (Misc1.panel_contains(m_ptr.fy, m_ptr.fx))) {
			/* Wizard sight.	     */
			if (Variable.wizard) {
				flag = true;
			
			/* Normal sight.	     */
			} else if (Misc1.los(Player.char_row, Player.char_col, m_ptr.fy, m_ptr.fx)) {
				c_ptr = Variable.cave[m_ptr.fy][m_ptr.fx];
				r_ptr = Monsters.c_list[m_ptr.mptr];
				if (c_ptr.pl || c_ptr.tl || (Variable.find_flag != 0 && m_ptr.cdis < 2 && Variable.player_light)) {
					if ((Constants.CM_INVISIBLE & r_ptr.cmove) == 0) {
						flag = true;
					} else if (Player.py.flags.see_inv) {
						flag = true;
						Variable.c_recall[m_ptr.mptr].r_cmove |= Constants.CM_INVISIBLE;
					}
				
				/* Infra vision.	 */
				} else if ((Player.py.flags.see_infra > 0) && (m_ptr.cdis <= Player.py.flags.see_infra) && (Constants.CD_INFRA & r_ptr.cdefense) != 0) {
					flag = true;
					Variable.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_INFRA;
				}
			}
		}
		
		/* Light it up.	 */
		if (flag) {
			if (!m_ptr.ml) {
				Moria1.disturb(true, false);
				m_ptr.ml = true;
				Moria1.lite_spot(m_ptr.fy, m_ptr.fx);
				Variable.screen_change = true; /* notify inven_command */
			}
		
		/* Turn it off.	 */
		} else if (m_ptr.ml) {
			m_ptr.ml = false;
			Moria1.lite_spot(m_ptr.fy, m_ptr.fx);
			Variable.screen_change = true; /* notify inven_command */
		}
	}
	
	/* Given speed,	 returns number of moves this turn.	-RAK-	*/
	/* NOTE: Player must always move at least once per iteration,	  */
	/*	 a slowed player is handled by moving monsters faster	 */
	public static int movement_rate(int speed) {
		if (speed > 0) {
			if (Player.py.flags.rest != 0) {
				return 1;
			} else {
				return speed;
			}
		} else {
			/* speed must be negative here */
			return ((Variable.turn % (2 - speed)) == 0) ? 1 : 0;
		}
	}
	
	/* Makes sure a new creature gets lit up.			-CJS- */
	public static boolean check_mon_lite(int y, int x) {
		int monptr;
		
		monptr = Variable.cave[y][x].cptr;
		if (monptr <= 1) {
			return false;
		} else {
			update_mon(monptr);
			return Monsters.m_list[monptr].ml;
		}
	}
	
	/* Choose correct directions for monster movement	-RAK-	*/
	public static void get_moves(int monptr, int[] mm) {
		int y, ay, x, ax, move_val;
		
		y = Monsters.m_list[monptr].fy - Player.char_row;
		x = Monsters.m_list[monptr].fx - Player.char_col;
		if (y < 0) {
			move_val = 8;
			ay = -y;
		} else {
			move_val = 0;
			ay = y;
		}
		if (x > 0) {
			move_val += 4;
			ax = x;
		} else {
			ax = -x;
		}
		/* this has the advantage of preventing the diamond maneuvre, also faster */
		if (ay > (ax << 1)) {
			move_val += 2;
		} else if (ax > (ay << 1)) {
			move_val++;
		}
		switch(move_val)
		{
		case 0:
			mm[0] = 9;
			if (ay > ax) {
				mm[1] = 8;
				mm[2] = 6;
				mm[3] = 7;
				mm[4] = 3;
			} else {
				mm[1] = 6;
				mm[2] = 8;
				mm[3] = 3;
				mm[4] = 7;
			}
			break;
		case 1: case 9:
			mm[0] = 6;
			if (y < 0) {
				mm[1] = 3;
				mm[2] = 9;
				mm[3] = 2;
				mm[4] = 8;
			} else {
				mm[1] = 9;
				mm[2] = 3;
				mm[3] = 8;
				mm[4] = 2;
			}
			break;
		case 2: case 6:
			mm[0] = 8;
			if (x < 0) {
				mm[1] = 9;
				mm[2] = 7;
				mm[3] = 6;
				mm[4] = 4;
			} else {
				mm[1] = 7;
				mm[2] = 9;
				mm[3] = 4;
				mm[4] = 6;
			}
			break;
		case 4:
			mm[0] = 7;
			if (ay > ax) {
				mm[1] = 8;
				mm[2] = 4;
				mm[3] = 9;
				mm[4] = 1;
			} else {
				mm[1] = 4;
				mm[2] = 8;
				mm[3] = 1;
				mm[4] = 9;
			}
			break;
		case 5: case 13:
			mm[0] = 4;
			if (y < 0) {
				mm[1] = 1;
				mm[2] = 7;
				mm[3] = 2;
				mm[4] = 8;
			} else {
				mm[1] = 7;
				mm[2] = 1;
				mm[3] = 8;
				mm[4] = 2;
			}
			break;
		case 8:
			mm[0] = 3;
			if (ay > ax) {
				mm[1] = 2;
				mm[2] = 6;
				mm[3] = 1;
				mm[4] = 9;
			} else {
				mm[1] = 6;
				mm[2] = 2;
				mm[3] = 9;
				mm[4] = 1;
			}
			break;
		case 10: case 14:
			mm[0] = 2;
			if (x < 0) {
				mm[1] = 3;
				mm[2] = 1;
				mm[3] = 6;
				mm[4] = 4;
			} else {
				mm[1] = 1;
				mm[2] = 3;
				mm[3] = 4;
				mm[4] = 6;
			}
			break;
		case 12:
			mm[0] = 1;
			if (ay > ax) {
				mm[1] = 2;
				mm[2] = 4;
				mm[3] = 3;
				mm[4] = 7;
			} else {
				mm[1] = 4;
				mm[2] = 2;
				mm[3] = 7;
				mm[4] = 3;
			}
			break;
		}
	}
	
	/* Make an attack on the player (chuckle.)		-RAK-	*/
	public static void make_attack(int monptr) {
		int attype, adesc, adice, asides;
		IntPointer i = new IntPointer(), j = new IntPointer();
		int damage, attackn;
		boolean flag, notice, visible;
		int gold;
		int attstr;
		String cdesc, tmp_str, ddesc;
		CreatureType r_ptr;
		MonsterType m_ptr;
		PlayerMisc p_ptr;
		PlayerFlags f_ptr;
		InvenType i_ptr;
		
		if (Variable.death) {	/* don't beat a dead body! */
			return;
		}
		
		m_ptr = Monsters.m_list[monptr];
		r_ptr = Monsters.c_list[m_ptr.mptr];
		if (!m_ptr.ml) {
			cdesc = "It ";
		} else {
			cdesc = String.format("The %s ", r_ptr.name);
		}
		/* For "DIED_FROM" string	   */
		if ((Constants.CM_WIN & r_ptr.cmove) != 0) {
			ddesc = String.format("The %s", r_ptr.name);
		} else if (Desc.is_a_vowel(r_ptr.name.charAt(0))) {
			ddesc = String.format("an %s", r_ptr.name);
		} else {
			ddesc = String.format("a %s", r_ptr.name);
		}
		/* End DIED_FROM		   */
		
		attackn = 0;
		int attstrindex = 0;
		attstr = r_ptr.damage[attstrindex];
		while ((attstr != 0) && !Variable.death) {
			attype = Monsters.monster_attacks[attstr].attack_type;
			adesc = Monsters.monster_attacks[attstr].attack_desc;
			adice = Monsters.monster_attacks[attstr].attack_dice;
			asides = Monsters.monster_attacks[attstr].attack_sides;
			attstrindex++;
			attstr = r_ptr.damage[attstrindex];
			flag = false;
			if ((Player.py.flags.protevil > 0) && (r_ptr.cdefense & Constants.CD_EVIL) != 0 && ((Player.py.misc.lev + 1) > r_ptr.level)) {
				if (m_ptr.ml) {
					Variable.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_EVIL;
				}
				attype = 99;
				adesc = 99;
			}
			p_ptr = Player.py.misc;
			switch(attype)
			{
			case 1:	/*Normal attack  */
				if (Moria1.test_hit(60, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 2:	/*Lose Strength*/
				if (Moria1.test_hit(-3, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 3:	/*Confusion attack*/
				if (Moria1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 4:	/*Fear attack    */
				if (Moria1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 5:	/*Fire attack    */
				if (Moria1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 6:	/*Acid attack    */
				if (Moria1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 7:	/*Cold attack    */
				if (Moria1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 8:	/*Lightning attack*/
				if (Moria1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 9:	/*Corrosion attack*/
				if (Moria1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 10:	/*Blindness attack*/
				if (Moria1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 11:	/*Paralysis attack*/
				if (Moria1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 12:	/*Steal Money    */
				if ((Moria1.test_hit(5, r_ptr.level, 0, Player.py.misc.lev, Constants.CLA_MISC_HIT)) && (Player.py.misc.au > 0)) {
					flag = true;
				}
				break;
			case 13:	/*Steal Object   */
				if ((Moria1.test_hit(2, r_ptr.level, 0, Player.py.misc.lev, Constants.CLA_MISC_HIT)) && (Treasure.inven_ctr > 0)) {
					flag = true;
				}
				break;
			case 14:	/*Poison	       */
				if (Moria1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 15:	/*Lose dexterity*/
				if (Moria1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 16:	/*Lose constitution*/
				if (Moria1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 17:	/*Lose intelligence*/
				if (Moria1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 18:	/*Lose wisdom*/
				if (Moria1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 19:	/*Lose experience*/
				if (Moria1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 20:	/*Aggravate monsters*/
				flag = true;
				break;
			case 21:	/*Disenchant	  */
				if (Moria1.test_hit(20, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 22:	/*Eat food	  */
				if (Moria1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 23:      /*Eat light	  */
				if (Moria1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 24:      /*Eat charges	  */
				if ((Moria1.test_hit(15, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) && (Treasure.inven_ctr > 0)) {
					/* check to make sure an object exists */
					flag = true;
				}
				break;
			case 99:
				flag = true;
				break;
			default:
				break;
			}
			
			if (flag) {
				/* can not strcat to cdesc because the creature may have
				 * multiple attacks */
				Moria1.disturb(true, false);
				tmp_str = cdesc;
				switch(adesc)
				{
				case 1: IO.msg_print(tmp_str.concat("hits you.")); break;
				case 2: IO.msg_print(tmp_str.concat("bites you.")); break;
				case 3: IO.msg_print(tmp_str.concat("claws you.")); break;
				case 4: IO.msg_print(tmp_str.concat("stings you.")); break;
				case 5: IO.msg_print(tmp_str.concat("touches you.")); break;
				//case 6: msg_print(strcat(tmp_str, "kicks you.")); break;
				case 7: IO.msg_print(tmp_str.concat("gazes at you.")); break;
				case 8: IO.msg_print(tmp_str.concat("breathes on you.")); break;
				case 9: IO.msg_print(tmp_str.concat("spits on you.")); break;
				case 10: IO.msg_print(tmp_str.concat("makes a horrible wail."));break;
				//case 11: msg_print(strcat(tmp_str, "embraces you.")); break;
				case 12: IO.msg_print(tmp_str.concat("crawls on you.")); break;
				case 13: IO.msg_print(tmp_str.concat("releases a cloud of spores.")); break;
				case 14: IO.msg_print(tmp_str.concat("begs you for money.")); break;
				case 15: IO.msg_print("You've been slimed!"); break;
				case 16: IO.msg_print(tmp_str.concat("crushes you.")); break;
				case 17: IO.msg_print(tmp_str.concat("tramples you.")); break;
				case 18: IO.msg_print(tmp_str.concat("drools on you.")); break;
				case 19:
					switch(Misc1.randint(9))
					{
					case 1: IO.msg_print(tmp_str.concat("insults you!")); break;
					case 2: IO.msg_print(tmp_str.concat("insults your mother!")); break;
					case 3: IO.msg_print(tmp_str.concat("gives you the finger!")); break;
					case 4: IO.msg_print(tmp_str.concat("humiliates you!")); break;
					case 5: IO.msg_print(tmp_str.concat("wets on your leg!")); break;
					case 6: IO.msg_print(tmp_str.concat("defiles you!")); break;
					case 7: IO.msg_print(tmp_str.concat("dances around you!"));break;
					case 8: IO.msg_print(tmp_str.concat("makes obscene gestures!")); break;
					case 9: IO.msg_print(tmp_str.concat("moons you!!!")); break;
					}
					break;
				case 99: IO.msg_print(tmp_str.concat("is repelled.")); break;
				default: break;
				}
				
				notice = true;
				/* always fail to notice attack if creature invisible, set notice
				 * and visible here since creature may be visible when attacking
				 * and then teleport afterwards (becoming effectively invisible) */
				if (!m_ptr.ml) {
					visible = false;
					notice = false;
				} else {
					visible = true;
				}
				
				damage = Misc1.damroll(adice, asides);
				switch(attype)
				{
				case 1:	/*Normal attack	*/
					/* round half-way case down */
					damage -= ((p_ptr.pac + p_ptr.ptoac) * damage) / 200;
					Moria1.take_hit(damage, ddesc);
					break;
				case 2:	/*Lose Strength*/
					Moria1.take_hit(damage, ddesc);
					if (Player.py.flags.sustain_str) {
						IO.msg_print("You feel weaker for a moment, but it passes.");
					} else if (Misc1.randint(2) == 1) {
						IO.msg_print("You feel weaker.");
						Misc3.dec_stat(Constants.A_STR);
					} else {
						notice = false;
					}
					break;
				case 3:	/*Confusion attack*/
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					if (Misc1.randint(2) == 1) {
						if (f_ptr.confused < 1) {
							IO.msg_print("You feel confused.");
							f_ptr.confused += Misc1.randint(r_ptr.level);
						} else {
							notice = false;
						}
						f_ptr.confused += 3;
					} else {
						notice = false;
					}
					break;
				case 4:	/*Fear attack	*/
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					if (Misc3.player_saves()) {
						IO.msg_print("You resist the effects!");
					} else if (f_ptr.afraid < 1) {
						IO.msg_print("You are suddenly afraid!");
						f_ptr.afraid += 3 + Misc1.randint(r_ptr.level);
					} else {
						f_ptr.afraid += 3;
						notice = false;
					}
					break;
				case 5:	/*Fire attack	*/
					IO.msg_print("You are enveloped in flames!");
					Moria2.fire_dam(damage, ddesc);
					break;
				case 6:	/*Acid attack	*/
					IO.msg_print("You are covered in acid!");
					Moria2.acid_dam(damage, ddesc);
					break;
				case 7:	/*Cold attack	*/
					IO.msg_print("You are covered with frost!");
					Moria2.cold_dam(damage, ddesc);
					break;
				case 8:	/*Lightning attack*/
					IO.msg_print("Lightning strikes you!");
					Moria2.light_dam(damage, ddesc);
					break;
				case 9:	/*Corrosion attack*/
					IO.msg_print("A stinging red gas swirls about you.");
					Moria2.corrode_gas(ddesc);
					Moria1.take_hit(damage, ddesc);
					break;
				case 10:	/*Blindness attack*/
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					if (f_ptr.blind < 1) {
						f_ptr.blind += 10 + Misc1.randint(r_ptr.level);
						IO.msg_print("Your eyes begin to sting.");
					} else {
						f_ptr.blind += 5;
						notice = false;
					}
					break;
				case 11:	/*Paralysis attack*/
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					if (Misc3.player_saves()) {
						IO.msg_print("You resist the effects!");
					} else if (f_ptr.paralysis < 1) {
						if (f_ptr.free_act) {
							IO.msg_print("You are unaffected.");
						} else {
							f_ptr.paralysis = Misc1.randint(r_ptr.level) + 3;
							IO.msg_print("You are paralyzed.");
						}
					} else {
						notice = false;
					}
					break;
				case 12:	/*Steal Money	  */
					if ((Player.py.flags.paralysis < 1) && (Misc1.randint(124) < Player.py.stats.use_stat[Constants.A_DEX])) {
						IO.msg_print("You quickly protect your money pouch!");
					} else {
						gold = (p_ptr.au / 10) + Misc1.randint(25);
						if (gold > p_ptr.au) {
							p_ptr.au = 0;
						} else {
							p_ptr.au -= gold;
						}
						IO.msg_print("Your purse feels lighter.");
						Misc3.prt_gold();
					}
					if (Misc1.randint(2) == 1) {
						IO.msg_print("There is a puff of smoke!");
						Spells.teleport_away(monptr, Constants.MAX_SIGHT);
					}
					break;
				case 13:	/*Steal Object	 */
					if ((Player.py.flags.paralysis < 1) && (Misc1.randint(124) < Player.py.stats.use_stat[Constants.A_DEX])) {
						IO.msg_print("You grab hold of your backpack!");
					} else {
						i.value(Misc1.randint(Treasure.inven_ctr) - 1);
						Misc3.inven_destroy(i.value());
						IO.msg_print("Your backpack feels lighter.");
					}
					if (Misc1.randint(2) == 1) {
						IO.msg_print("There is a puff of smoke!");
						Spells.teleport_away(monptr, Constants.MAX_SIGHT);
					}
					break;
				case 14:	/*Poison	 */
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					IO.msg_print("You feel very sick.");
					f_ptr.poisoned += Misc1.randint(r_ptr.level) + 5;
					break;
				case 15:	/*Lose dexterity */
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					if (f_ptr.sustain_dex) {
						IO.msg_print("You feel clumsy for a moment, but it passes.");
					} else {
						IO.msg_print("You feel more clumsy.");
						Misc3.dec_stat(Constants.A_DEX);
					}
					break;
				case 16:	/*Lose constitution */
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					if (f_ptr.sustain_con) {
						IO.msg_print("Your body resists the effects of the disease.");
					} else {
						IO.msg_print("Your health is damaged!");
						Misc3.dec_stat(Constants.A_CON);
					}
					break;
				case 17:	/*Lose intelligence */
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					IO.msg_print("You have trouble thinking clearly.");
					if (f_ptr.sustain_int) {
						IO.msg_print("But your mind quickly clears.");
					} else {
						Misc3.dec_stat(Constants.A_INT);
					}
					break;
				case 18:	/*Lose wisdom	   */
					f_ptr = Player.py.flags;
					Moria1.take_hit(damage, ddesc);
					if (f_ptr.sustain_wis) {
						IO.msg_print("Your wisdom is sustained.");
					} else {
						IO.msg_print("Your wisdom is drained.");
						Misc3.dec_stat(Constants.A_WIS);
					}
					break;
				case 19:	/*Lose experience  */
					IO.msg_print("You feel your life draining away!");
					Spells.lose_exp(damage + (Player.py.misc.exp / 100) * Constants.MON_DRAIN_LIFE);
					break;
				case 20:	/*Aggravate monster*/
					Spells.aggravate_monster(20);
					break;
				case 21:	/*Disenchant	   */
					flag = false;
					switch(Misc1.randint(7))
					{
					case 1: i.value(Constants.INVEN_WIELD);	break;
					case 2: i.value(Constants.INVEN_BODY);	break;
					case 3: i.value(Constants.INVEN_ARM);	break;
					case 4: i.value(Constants.INVEN_OUTER);	break;
					case 5: i.value(Constants.INVEN_HANDS);	break;
					case 6: i.value(Constants.INVEN_HEAD);	break;
					case 7: i.value(Constants.INVEN_FEET);	break;
					}
					i_ptr = Treasure.inventory[i.value()];
					if (i_ptr.tohit > 0) {
						i_ptr.tohit -= Misc1.randint(2);
						/* don't send it below zero */
						if (i_ptr.tohit < 0) {
							i_ptr.tohit = 0;
						}
						flag = true;
					}
					if (i_ptr.todam > 0) {
						i_ptr.todam -= Misc1.randint(2);
						/* don't send it below zero */
						if (i_ptr.todam < 0) {
							i_ptr.todam = 0;
						}
						flag = true;
					}
					if (i_ptr.toac > 0) {
						i_ptr.toac -= Misc1.randint(2);
						/* don't send it below zero */
						if (i_ptr.toac < 0) {
							i_ptr.toac = 0;
						}
						flag = true;
					}
					if (flag) {
						IO.msg_print("There is a static feeling in the air.");
						Moria1.calc_bonuses();
					} else {
						notice = false;
					}
					break;
				case 22:	/*Eat food	   */
					if (Misc3.find_range(Constants.TV_FOOD, Constants.TV_NEVER, i, j)) {
						Misc3.inven_destroy(i.value());
						IO.msg_print("It got at your rations!");
					}
					else
						notice = false;
					break;
				case 23:	/*Eat light	   */
					i_ptr = Treasure.inventory[Constants.INVEN_LIGHT];
					if (i_ptr.p1 > 0) {
						i_ptr.p1 -= (250 + Misc1.randint(250));
						if (i_ptr.p1 < 1)	i_ptr.p1 = 1;
						if (Player.py.flags.blind < 1) {
							IO.msg_print("Your light dims.");
						} else {
							notice = false;
						}
					} else {
						notice = false;
					}
					break;
				case 24:	/*Eat charges	  */
					i.value(Misc1.randint(Treasure.inven_ctr) - 1);
					j.value(r_ptr.level);
					i_ptr = Treasure.inventory[i.value()];
					if (((i_ptr.tval == Constants.TV_STAFF) || (i_ptr.tval == Constants.TV_WAND)) && (i_ptr.p1 > 0)) {
						m_ptr.hp += j.value() * i_ptr.p1;
						i_ptr.p1 = 0;
						if (!Desc.known2_p(i_ptr)) {
							Misc4.add_inscribe(i_ptr, Constants.ID_EMPTY);
						}
						IO.msg_print("Energy drains from your pack!");
					} else {
						notice = false;
					}
					break;
				case 99:
					notice = false;
					break;
				default:
					notice = false;
					break;
				}
				
				/* Moved here from mon_move, so that monster only confused if it
				 * actually hits.  A monster that has been repelled has not hit
				 * the player, so it should not be confused.  */
				if (Player.py.flags.confuse_monster && adesc != 99) {
					IO.msg_print("Your hands stop glowing.");
					Player.py.flags.confuse_monster = false;
					if ((Misc1.randint(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) != 0) {
						tmp_str = String.format("%sis unaffected.", cdesc);
					} else {
						tmp_str = String.format("%sappears confused.", cdesc);
						if (m_ptr.confused > 0) {
							m_ptr.confused += 3;
						} else {
							m_ptr.confused = 2 + Misc1.randint(16);
						}
					}
					IO.msg_print(tmp_str);
					if (visible && !Variable.death && Misc1.randint(4) == 1) {
						Variable.c_recall[m_ptr.mptr].r_cdefense |= r_ptr.cdefense & Constants.CD_NO_SLEEP;
					}
				}
				
				/* increase number of attacks if notice true, or if visible and had
				 * previously noticed the attack (in which case all this does is
				 * help player learn damage), note that in the second case do
				 * not increase attacks if creature repelled (no damage done) */
				if ((notice || (visible && Variable.c_recall[m_ptr.mptr].r_attacks[attackn] != 0 && attype != 99)) && Variable.c_recall[m_ptr.mptr].r_attacks[attackn] < Constants.MAX_UCHAR) {
					Variable.c_recall[m_ptr.mptr].r_attacks[attackn]++;
				}
				if (Variable.death && Variable.c_recall[m_ptr.mptr].r_deaths < Constants.MAX_SHORT) {
					Variable.c_recall[m_ptr.mptr].r_deaths++;
				}
			} else {
				if ((adesc >= 1 && adesc <= 3) || (adesc == 6)) {
					Moria1.disturb(true, false);
					tmp_str = cdesc;
					IO.msg_print(tmp_str.concat("misses you."));
				}
			}
			if (attackn < Constants.MAX_MON_NATTACK - 1) {
				attackn++;
			} else {
				break;
			}
		}
	}
	
	/* Make the move if possible, five choices		-RAK-	*/
	public static void make_move(int monptr, int[] mm, LongPointer rcmove) {
		int i;
		IntPointer newy = new IntPointer(), newx = new IntPointer();
		boolean do_turn, do_move, stuck_door;
		long movebits;
		CaveType c_ptr;
		MonsterType m_ptr;
		InvenType t_ptr;
		
		i = 0;
		do_turn = false;
		do_move = false;
		m_ptr = Monsters.m_list[monptr];
		movebits = Monsters.c_list[m_ptr.mptr].cmove;
		do {
			/* Get new position		*/
			newy.value(m_ptr.fy);
			newx.value(m_ptr.fx);
			Misc3.mmove(mm[i], newy, newx);
			c_ptr = Variable.cave[newy.value()][newx.value()];
			if (c_ptr.fval != Constants.BOUNDARY_WALL) {
				/* Floor is open?		   */
				if (c_ptr.fval <= Constants.MAX_OPEN_SPACE) {
					do_move = true;
				
				/* Creature moves through walls? */
				} else if ((movebits & Constants.CM_PHASE) != 0) {
					do_move = true;
					rcmove.value(rcmove.value() | Constants.CM_PHASE);
				
				/* Creature can open doors?	   */
				} else if (c_ptr.tptr != 0) {
					t_ptr = Treasure.t_list[c_ptr.tptr];
					if ((movebits & Constants.CM_OPEN_DOOR) != 0) {	/* Creature can open doors.		     */
						stuck_door = false;
						if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
							do_turn = true;
							if (t_ptr.p1 == 0) {	/* Closed doors	 */
								do_move = true;
							} else if (t_ptr.p1 > 0) {	/* Locked doors	*/
								if (Misc1.randint((m_ptr.hp + 1) * (50 + t_ptr.p1)) < 40 * (m_ptr.hp - 10 - t_ptr.p1)) {
									t_ptr.p1 = 0;
								}
							} else if (t_ptr.p1 < 0) {	/* Stuck doors	*/
								if (Misc1.randint((m_ptr.hp + 1) * (50 - t_ptr.p1)) < 40 * (m_ptr.hp - 10 + t_ptr.p1)) {
									IO.msg_print("You hear a door burst open!");
									Moria1.disturb(true, false);
									stuck_door = true;
									do_move = true;
								}
							}
						} else if (t_ptr.tval == Constants.TV_SECRET_DOOR) {
							do_turn = true;
							do_move = true;
						}
						if (do_move) {
							Desc.invcopy(t_ptr, Constants.OBJ_OPEN_DOOR);
							if (stuck_door) {	/* 50% chance of breaking door */
								t_ptr.p1 = 1 - Misc1.randint(2);
							}
							c_ptr.fval = Constants.CORR_FLOOR;
							Moria1.lite_spot(newy.value(), newx.value());
							rcmove.value(rcmove.value() | Constants.CM_OPEN_DOOR);
							do_move = false;
						}
					} else {	/* Creature can not open doors, must bash them   */
						if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
							do_turn = true;
							if (Misc1.randint((m_ptr.hp + 1) * (80 + Math.abs(t_ptr.p1))) < 40 * (m_ptr.hp - 20 - Math.abs(t_ptr.p1))) {
								Desc.invcopy(t_ptr, Constants.OBJ_OPEN_DOOR);
								/* 50% chance of breaking door */
								t_ptr.p1 = 1 - Misc1.randint(2);
								c_ptr.fval = Constants.CORR_FLOOR;
								Moria1.lite_spot(newy.value(), newx.value());
								IO.msg_print("You hear a door burst open!");
								Moria1.disturb(true, false);
							}
						}
					}
				}
				/* Glyph of warding present?	   */
				if (do_move && (c_ptr.tptr != 0) && (Treasure.t_list[c_ptr.tptr].tval == Constants.TV_VIS_TRAP) && (Treasure.t_list[c_ptr.tptr].subval == 99)) {
					if (Misc1.randint(Constants.OBJ_RUNE_PROT) < Monsters.c_list[m_ptr.mptr].level) {
						if ((newy.value() == Player.char_row) && (newx.value() == Player.char_col)) {
							IO.msg_print("The rune of protection is broken!");
						}
						Moria3.delete_object(newy.value(), newx.value());
					} else {
						do_move = false;
						/* If the creature moves only to attack, */
						/* don't let it move if the glyph prevents */
						/* it from attacking */
						if ((movebits & Constants.CM_ATTACK_ONLY) != 0) {
							do_turn = true;
						}
					}
				}
				/* Creature has attempted to move on player?	   */
				if (do_move) {
					if (c_ptr.cptr == 1) {
						/* if the monster is not lit, must call update_mon, it may
						 * be faster than character, and hence could have just
						 * moved next to character this same turn */
						if (!m_ptr.ml) {
							update_mon(monptr);
						}
						make_attack(monptr);
						do_move = false;
						do_turn = true;
					
					/* Creature is attempting to move on other creature?	   */
					} else if ((c_ptr.cptr > 1) && ((newy.value() != m_ptr.fy) || (newx.value() != m_ptr.fx))) {
						/* Creature eats other creatures?	 */
						if ((movebits & Constants.CM_EATS_OTHER) != 0 && (Monsters.c_list[m_ptr.mptr].mexp >= Monsters.c_list[Monsters.m_list[c_ptr.cptr].mptr].mexp)) {
							if (Monsters.m_list[c_ptr.cptr].ml) {
								rcmove.value(rcmove.value() | Constants.CM_EATS_OTHER);
							}
							/* It ate an already processed monster. Handle normally. */
							if (monptr < c_ptr.cptr) {
								Moria3.delete_monster(c_ptr.cptr);
							
							/* If it eats this monster, an already processed monster
							 * will take its place, causing all kinds of havoc.  Delay
							 * the kill a bit. */
							} else {
								Moria3.fix1_delete_monster(c_ptr.cptr);
							}
						} else {
							do_move = false;
						}
					}
				}
				
				/* Creature has been allowed move.	 */
				if (do_move) {
					/* Pick up or eat an object	       */
					if ((movebits & Constants.CM_PICKS_UP) != 0) {
						c_ptr = Variable.cave[newy.value()][newx.value()];
						
						if ((c_ptr.tptr != 0) && (Treasure.t_list[c_ptr.tptr].tval <= Constants.TV_MAX_OBJECT)) {
							rcmove.value(rcmove.value() | Constants.CM_PICKS_UP);
							Moria3.delete_object(newy.value(), newx.value());
						}
					}
					/* Move creature record		       */
					Moria1.move_rec(m_ptr.fy, m_ptr.fx, newy.value(), newx.value());
					if (m_ptr.ml) {
						m_ptr.ml = false;
						Moria1.lite_spot(m_ptr.fy, m_ptr.fx);
					}
					m_ptr.fy = newy.value();
					m_ptr.fx = newx.value();
					m_ptr.cdis = Misc1.distance(Player.char_row, Player.char_col, newy.value(), newx.value());
					do_turn = true;
				}
			}
			i++;
			/* Up to 5 attempts at moving,   give up.	  */
	    } while ((!do_turn) && (i < 5));
	}
	
	/* Creatures can cast spells too.  (Dragon Breath)	-RAK-	*/
	/* cast_spell = true if creature changes position	*/
	/* took_turn  = true if creature casts a spell		*/
	public static boolean mon_cast_spell(int monptr) {
		LongPointer i;
		IntPointer y, x;
		int thrown_spell, r1;
		long chance;
		int k;
		int[] spell_choice = new int[30];
		String cdesc, outval, ddesc;
		MonsterType m_ptr;
		CreatureType r_ptr;
		boolean took_turn = false;
		
		if (Variable.death) {
			return false;
		}
		
		m_ptr = Monsters.m_list[monptr];
		r_ptr = Monsters.c_list[m_ptr.mptr];
		chance = (r_ptr.spells & Constants.CS_FREQ);
		/* 1 in x chance of casting spell		   */
		if (Misc1.randint((int)chance) != 1) {
			took_turn = false;
		/* Must be within certain range		   */
		} else if (m_ptr.cdis > Constants.MAX_SPELL_DIS) {
			took_turn = false;
		/* Must have unobstructed Line-Of-Sight	   */
		} else if (!Misc1.los(Player.char_row, Player.char_col, m_ptr.fy, m_ptr.fx)) {
			took_turn = false;
		} else {	/* Creature is going to cast a spell	 */
			took_turn = true;
			/* Check to see if monster should be lit. */
			update_mon (monptr);
			/* Describe the attack			       */
			if (m_ptr.ml) {
				cdesc = String.format("The %s ", r_ptr.name);
			} else {
				cdesc = "It ";
			}
			/* For "DIED_FROM" string	 */
			if ((Constants.CM_WIN & r_ptr.cmove) != 0) {
				ddesc = String.format("The %s", r_ptr.name);
			} else if (Desc.is_a_vowel(r_ptr.name.charAt(0))) {
				ddesc = String.format("an %s", r_ptr.name);
			} else {
				ddesc = String.format("a %s", r_ptr.name);
			}
			/* End DIED_FROM		       */
			
			/* Extract all possible spells into spell_choice */
			i = new LongPointer(r_ptr.spells & ~Constants.CS_FREQ);
			k = 0;
			while (i.value() != 0) {
				spell_choice[k] = Misc1.bit_pos(i);
				k++;
			}
			/* Choose a spell to cast			       */
			thrown_spell = spell_choice[Misc1.randint(k) - 1];
			thrown_spell++;
			/* all except teleport_away() and drain mana spells always disturb */
			if (thrown_spell > 6 && thrown_spell != 17) {
				Moria1.disturb(true, false);
			}
			/* save some code/data space here, with a small time penalty */
			if ((thrown_spell < 14 && thrown_spell > 6) || (thrown_spell == 16)) {
				cdesc = cdesc.concat("casts a spell.");
				IO.msg_print(cdesc);
			}
			/* Cast the spell.			     */
			switch(thrown_spell)
			{
			case 5:	 /*Teleport Short*/
				Spells.teleport_away(monptr, 5);
				break;
			case 6:	 /*Teleport Long */
				Spells.teleport_away(monptr, Constants.MAX_SIGHT);
				break;
			case 7:	 /*Teleport To	 */
				Spells.teleport_to(m_ptr.fy, m_ptr.fx);
				break;
			case 8:	 /*Light Wound	 */
				if (Misc3.player_saves()) {
					IO.msg_print("You resist the effects of the spell.");
				} else {
					Moria1.take_hit(Misc1.damroll(3, 8), ddesc);
				}
				break;
			case 9:	 /*Serious Wound */
				if (Misc3.player_saves()) {
					IO.msg_print("You resist the effects of the spell.");
				} else {
					Moria1.take_hit(Misc1.damroll(8, 8), ddesc);
				}
				break;
			case 10:  /*Hold Person	  */
				if (Player.py.flags.free_act) {
					IO.msg_print("You are unaffected.");
				} else if (Misc3.player_saves()) {
					IO.msg_print("You resist the effects of the spell.");
				} else if (Player.py.flags.paralysis > 0) {
					Player.py.flags.paralysis += 2;
				} else {
					Player.py.flags.paralysis = Misc1.randint(5)+4;
				}
				break;
			case 11:  /*Cause Blindness*/
				if (Misc3.player_saves()) {
					IO.msg_print("You resist the effects of the spell.");
				} else if (Player.py.flags.blind > 0) {
					Player.py.flags.blind += 6;
				} else {
					Player.py.flags.blind += 12 + Misc1.randint(3);
				}
				break;
			case 12:  /*Cause Confuse */
				if (Misc3.player_saves()) {
					IO.msg_print("You resist the effects of the spell.");
				} else if (Player.py.flags.confused > 0) {
					Player.py.flags.confused += 2;
				} else {
					Player.py.flags.confused = Misc1.randint(5) + 3;
				}
				break;
			case 13:  /*Cause Fear	  */
				if (Misc3.player_saves()) {
					IO.msg_print("You resist the effects of the spell.");
				} else if (Player.py.flags.afraid > 0) {
					Player.py.flags.afraid += 2;
				} else {
					Player.py.flags.afraid = Misc1.randint(5) + 3;
				}
				break;
			case 14:  /*Summon Monster*/
				cdesc = cdesc.concat("magically summons a monster!");
				IO.msg_print(cdesc);
				y = new IntPointer(Player.char_row);
				x = new IntPointer(Player.char_col);
				/* in case compact_monster() is called,it needs monptr */
				Variable.hack_monptr = monptr;
				Misc1.summon_monster(y, x, false);
				Variable.hack_monptr = -1;
				update_mon (Variable.cave[y.value()][x.value()].cptr);
				break;
			case 15:  /*Summon Undead*/
				cdesc = cdesc.concat("magically summons an undead!");
				IO.msg_print(cdesc);
				y = new IntPointer(Player.char_row);
				x = new IntPointer(Player.char_col);
				/* in case compact_monster() is called,it needs monptr */
				Variable.hack_monptr = monptr;
				Misc1.summon_undead(y, x);
				Variable.hack_monptr = -1;
				update_mon(Variable.cave[y.value()][x.value()].cptr);
				break;
			case 16:  /*Slow Person	 */
				if (Player.py.flags.free_act) {
					IO.msg_print("You are unaffected.");
				} else if (Misc3.player_saves()) {
					IO.msg_print("You resist the effects of the spell.");
				} else if (Player.py.flags.slow > 0) {
					Player.py.flags.slow += 2;
				} else {
					Player.py.flags.slow = Misc1.randint(5) + 3;
				}
				break;
			case 17:  /*Drain Mana	 */
				if (Player.py.misc.cmana > 0) {
					Moria1.disturb(true, false);
					outval = String.format("%sdraws psychic energy from you!", cdesc);
					IO.msg_print(outval);
					if (m_ptr.ml) {
						outval = String.format("%sappears healthier.", cdesc);
						IO.msg_print(outval);
					}
					r1 = (Misc1.randint((int)r_ptr.level) >> 1) + 1;
					if (r1 > Player.py.misc.cmana) {
						r1 = Player.py.misc.cmana;
						Player.py.misc.cmana = 0;
						Player.py.misc.cmana_frac = 0;
					} else {
						Player.py.misc.cmana -= r1;
					}
					Misc3.prt_cmana();
					m_ptr.hp += 6*(r1);
				}
				break;
			case 20:  /*Breath Light */
				cdesc = cdesc.concat("breathes lightning.");
				IO.msg_print(cdesc);
				Spells.breath(Constants.GF_LIGHTNING, Player.char_row, Player.char_col, (m_ptr.hp / 4), ddesc, monptr);
				break;
			case 21:  /*Breath Gas	 */
				cdesc = cdesc.concat("breathes gas.");
				IO.msg_print(cdesc);
				Spells.breath(Constants.GF_POISON_GAS, Player.char_row, Player.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			case 22:  /*Breath Acid	 */
				cdesc = cdesc.concat("breathes acid.");
				IO.msg_print(cdesc);
				Spells.breath(Constants.GF_ACID, Player.char_row, Player.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			case 23:  /*Breath Frost */
				cdesc = cdesc.concat("breathes frost.");
				IO.msg_print(cdesc);
				Spells.breath(Constants.GF_FROST, Player.char_row, Player.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			case 24:  /*Breath Fire	 */
				cdesc = cdesc.concat("breathes fire.");
				IO.msg_print(cdesc);
				Spells.breath(Constants.GF_FIRE, Player.char_row, Player.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			default:
				cdesc = cdesc.concat("cast unknown spell.");
				IO.msg_print(cdesc);
			}
			/* End of spells				       */
			if (m_ptr.ml) {
				Variable.c_recall[m_ptr.mptr].r_spells |= 1L << (thrown_spell - 1);
				if ((Variable.c_recall[m_ptr.mptr].r_spells & Constants.CS_FREQ) != Constants.CS_FREQ) {
					Variable.c_recall[m_ptr.mptr].r_spells++;
				}
				if (Variable.death && Variable.c_recall[m_ptr.mptr].r_deaths < Constants.MAX_SHORT) {
					Variable.c_recall[m_ptr.mptr].r_deaths++;
				}
			}
		}
		return took_turn;
	}
	
	/* Places creature adjacent to given location		-RAK-	*/
	/* Rats and Flys are fun!					 */
	public static boolean multiply_monster(int y, int x, int cr_index, int monptr) {
		int i, j, k;
		CaveType c_ptr;
		boolean result;
		
		i = 0;
		do {
			j = y - 2 + Misc1.randint(3);
			k = x - 2 + Misc1.randint(3);
			/* don't create a new creature on top of the old one, that causes
			 * invincible/invisible creatures to appear */
			if (Misc1.in_bounds(j, k) && (j != y || k != x)) {
				c_ptr = Variable.cave[j][k];
				if ((c_ptr.fval <= Constants.MAX_OPEN_SPACE) && (c_ptr.tptr == 0) && (c_ptr.cptr != 1)) {
					if (c_ptr.cptr > 1) {	/* Creature there already?	*/
						/* Some critters are cannibalistic!	    */
						if ((Monsters.c_list[cr_index].cmove & Constants.CM_EATS_OTHER) != 0 && Monsters.c_list[cr_index].mexp >= Monsters.c_list[Monsters.m_list[c_ptr.cptr].mptr].mexp) {
							/* Check the experience level -CJS- */ 
							/* It ate an already processed monster.Handle normally.*/
							if (monptr < c_ptr.cptr) {
								Moria3.delete_monster(c_ptr.cptr);
							
							/* If it eats this monster, an already processed mosnter
							 * will take its place, causing all kinds of havoc.
							 * Delay the kill a bit. */
							} else {
								Moria3.fix1_delete_monster(c_ptr.cptr);
							}
							
							/* in case compact_monster() is called,it needs monptr */
							Variable.hack_monptr = monptr;
							/* Place_monster() may fail if monster list full.  */
							result = Misc1.place_monster(j, k, cr_index, false);
							Variable.hack_monptr = -1;
							if (!result) {
								return false;
							}
							Monsters.mon_tot_mult++;
							return check_mon_lite(j, k);
						}
					} else {
						/* All clear,  place a monster	  */
						/* in case compact_monster() is called,it needs monptr */
						Variable.hack_monptr = monptr;
						/* Place_monster() may fail if monster list full.  */
						result = Misc1.place_monster(j, k, cr_index, false);
						Variable.hack_monptr = -1;
						if (!result) {
							return false;
						}
						Monsters.mon_tot_mult++;
						return check_mon_lite(j, k);
					}
				}
			}
			i++;
		} while (i <= 18);
		return false;
	}
	
	/* Move the critters about the dungeon			-RAK-	*/
	public static void mon_move(int monptr, LongPointer rcmove) {
		int i, j;
		int k, dir;
		boolean move_test;
		CreatureType r_ptr;
		MonsterType m_ptr;
		int[] mm = new int[9];
		int rest_val;
		
		m_ptr = Monsters.m_list[monptr];
		r_ptr = Monsters.c_list[m_ptr.mptr];
		/* Does the critter multiply?				   */
		/* rest could be negative, to be safe, only use mod with positive values. */
		rest_val = Math.abs(Player.py.flags.rest);
		if ((r_ptr.cmove & Constants.CM_MULTIPLY) != 0 && (Constants.MAX_MON_MULT >= Monsters.mon_tot_mult) && ((rest_val % Constants.MON_MULT_ADJ) == 0)) {
			k = 0;
			for (i = m_ptr.fy-1; i <= m_ptr.fy+1; i++) {
				for (j = m_ptr.fx-1; j <= m_ptr.fx+1; j++) {
					if (Misc1.in_bounds(i, j) && (Variable.cave[i][j].cptr > 1)) {
						k++;
					}
				}
			}
			/* can't call m1.randint with a value of zero, increment counter
			 * to allow creature multiplication */
			if (k == 0) {
				k++;
			}
			if ((k < 4) && (Misc1.randint(k * Constants.MON_MULT_ADJ) == 1)) {
				if (multiply_monster((int)m_ptr.fy, (int)m_ptr.fx, m_ptr.mptr, monptr)) {
					rcmove.value(rcmove.value() | Constants.CM_MULTIPLY);
				}
			}
		}
		move_test = false;
		
		/* if in wall, must immediately escape to a clear area */
		if ((r_ptr.cmove & Constants.CM_PHASE) == 0 && (Variable.cave[m_ptr.fy][m_ptr.fx].fval >= Constants.MIN_CAVE_WALL)) {
			/* If the monster is already dead, don't kill it again!
			 * This can happen for monsters moving faster than the player.  They
			 * will get multiple moves, but should not if they die on the first
			 * move.  This is only a problem for monsters stuck in rock.  */
			if (m_ptr.hp < 0) {
				return;
			}
			
			k = 0;
			dir = 1;
			/* note direction of for loops matches direction of keypad from 1 to 9*/
			/* do not allow attack against the player */
			/* Must cast fy-1 to signed int, so that a nagative value of i will
			 * fail the comparison.  */
			for (i = m_ptr.fy + 1; i >= (m_ptr.fy - 1); i--) {
				for (j = m_ptr.fx-1; j <= m_ptr.fx+1; j++) {
					if ((dir != 5) && (Variable.cave[i][j].fval <= Constants.MAX_OPEN_SPACE) && (Variable.cave[i][j].cptr != 1)) {
						mm[k++] = dir;
					}
					dir++;
				}
			}
			if (k != 0) {
				/* put a random direction first */
				dir = Misc1.randint(k) - 1;
				i = mm[0];
				mm[0] = mm[dir];
				mm[dir] = i;
				make_move(monptr, mm, rcmove);
				/* this can only fail if mm[0] has a rune of protection */
			}
			/* if still in a wall, let it dig itself out, but also apply some
			 * more damage */
			if (Variable.cave[m_ptr.fy][m_ptr.fx].fval >= Constants.MIN_CAVE_WALL) {
				/* in case the monster dies, may need to call fix1_delete_monster()
				 * instead of delete_monsters() */
				Variable.hack_monptr = monptr;
				i = Moria3.mon_take_hit(monptr, Misc1.damroll(8, 8));
				Variable.hack_monptr = -1;
				if (i >= 0) {
					IO.msg_print("You hear a scream muffled by rock!");
					Misc3.prt_experience();
				} else {
					IO.msg_print ("A creature digs itself out from the rock!");
					Moria3.twall(m_ptr.fy, m_ptr.fx, 1, 0);
				}
			}
			return;  /* monster movement finished */
		
		/* Creature is confused or undead turned? */
		} else if (m_ptr.confused > 0) {
			/* Undead only get confused from turn undead, so they should flee */
			if ((r_ptr.cdefense & Constants.CD_UNDEAD) != 0) {
				get_moves(monptr,mm);
				mm[0] = 10 - mm[0];
				mm[1] = 10 - mm[1];
				mm[2] = 10 - mm[2];
				mm[3] = Misc1.randint(9); /* May attack only if cornered */
				mm[4] = Misc1.randint(9);
			} else {
				mm[0] = Misc1.randint(9);
				mm[1] = Misc1.randint(9);
				mm[2] = Misc1.randint(9);
				mm[3] = Misc1.randint(9);
				mm[4] = Misc1.randint(9);
			}
			/* don't move him if he is not supposed to move! */
			if ((r_ptr.cmove & Constants.CM_ATTACK_ONLY) == 0) {
				make_move(monptr, mm, rcmove);
			}
			m_ptr.confused--;
			move_test = true;
		
		/* Creature may cast a spell */
		} else if ((r_ptr.spells & Constants.CS_FREQ) != 0) {
			move_test = mon_cast_spell(monptr);
		}
		if (!move_test) {
			/* 75% random movement */
			if ((r_ptr.cmove & Constants.CM_75_RANDOM) != 0 && (Misc1.randint(100) < 75)) {
				mm[0] = Misc1.randint(9);
				mm[1] = Misc1.randint(9);
				mm[2] = Misc1.randint(9);
				mm[3] = Misc1.randint(9);
				mm[4] = Misc1.randint(9);
				rcmove.value(rcmove.value() | Constants.CM_75_RANDOM);
				make_move(monptr, mm, rcmove);
			
			/* 40% random movement */
			} else if ((r_ptr.cmove & Constants.CM_40_RANDOM) != 0 && (Misc1.randint(100) < 40)) {
				mm[0] = Misc1.randint(9);
				mm[1] = Misc1.randint(9);
				mm[2] = Misc1.randint(9);
				mm[3] = Misc1.randint(9);
				mm[4] = Misc1.randint(9);
				rcmove.value(rcmove.value() | Constants.CM_40_RANDOM);
				make_move(monptr, mm, rcmove);
			
			/* 20% random movement */
			} else if ((r_ptr.cmove & Constants.CM_20_RANDOM) != 0 && (Misc1.randint(100) < 20)) {
				mm[0] = Misc1.randint(9);
				mm[1] = Misc1.randint(9);
				mm[2] = Misc1.randint(9);
				mm[3] = Misc1.randint(9);
				mm[4] = Misc1.randint(9);
				rcmove.value(rcmove.value() | Constants.CM_20_RANDOM);
				make_move(monptr, mm, rcmove);
			
			/* Normal movement */
			} else if ((r_ptr.cmove & Constants.CM_MOVE_NORMAL) != 0) {
				if (Misc1.randint(200) == 1) {
					mm[0] = Misc1.randint(9);
					mm[1] = Misc1.randint(9);
					mm[2] = Misc1.randint(9);
					mm[3] = Misc1.randint(9);
					mm[4] = Misc1.randint(9);
				} else {
					get_moves(monptr, mm);
				}
				rcmove.value(rcmove.value() | Constants.CM_MOVE_NORMAL);
				make_move(monptr, mm, rcmove);
			
			/* Attack, but don't move */
			} else if ((r_ptr.cmove & Constants.CM_ATTACK_ONLY) != 0) {
				if (m_ptr.cdis < 2) {
					get_moves(monptr, mm);
					make_move(monptr, mm, rcmove);
				} else {
					/* Learn that the monster does not move when it should have
					 * moved, but didn't.  */
					rcmove.value(rcmove.value() | Constants.CM_ATTACK_ONLY);
				}
			} else if ((r_ptr.cmove & Constants.CM_ONLY_MAGIC) != 0 && (m_ptr.cdis < 2)) {
				/* A little hack for Quylthulgs, so that one will eventually notice
				 * that they have no physical attacks.  */
				if (Variable.c_recall[m_ptr.mptr].r_attacks[0] < Constants.MAX_UCHAR) {
					Variable.c_recall[m_ptr.mptr].r_attacks[0]++;
				}
				/* Another little hack for Quylthulgs, so that one can eventually
				 * learn their speed.  */
				if (Variable.c_recall[m_ptr.mptr].r_attacks[0] > 20) {
					Variable.c_recall[m_ptr.mptr].r_cmove |= Constants.CM_ONLY_MAGIC;
				}
			}
		}
	}
	
	/* Creatures movement and attacking are done from here	-RAK-	*/
	public static void creatures(boolean attack) {
		int i, k;
		MonsterType m_ptr;
		MonsterRecallType r_ptr;
		long notice;
		LongPointer rcmove = new LongPointer();
		boolean wake, ignore;
		String cdesc;
		
		/* Process the monsters  */
		for (i = Monsters.mfptr - 1; i >= Constants.MIN_MONIX && !Variable.death; i--) {
			m_ptr = Monsters.m_list[i];
			/* Get rid of an eaten/breathed on monster.  Note: Be sure not to
			 * process this monster. This is necessary because we can't delete
			 * monsters while scanning the m_list here. */
			if (m_ptr.hp < 0) {
				Moria3.fix2_delete_monster(i);
				continue;
			}
			
			m_ptr.cdis = Misc1.distance(Player.char_row, Player.char_col, m_ptr.fy, m_ptr.fx);
			if (attack) {	/* Attack is argument passed to CREATURE*/
				k = movement_rate(m_ptr.cspeed);
				if (k <= 0) {
					update_mon(i);
				} else {
					while (k > 0) {
						k--;
						wake = false;
						ignore = false;
						rcmove.value(0);
						if (m_ptr.ml || (m_ptr.cdis <= Monsters.c_list[m_ptr.mptr].aaf)
								|| (((Monsters.c_list[m_ptr.mptr].cmove & Constants.CM_PHASE) == 0) && Variable.cave[m_ptr.fy][m_ptr.fx].fval >= Constants.MIN_CAVE_WALL)) {
							/* Monsters trapped in rock must be given a turn also,
							 * so that they will die/dig out immediately.  */
							
							if (m_ptr.csleep > 0) {
								if (Player.py.flags.aggravate > 0) {
									m_ptr.csleep = 0;
								} else if ((Player.py.flags.rest == 0 && Player.py.flags.paralysis < 1) || (Misc1.randint(50) == 1)) {
									notice = Misc1.randint(1024);
									if (notice * notice * notice <= (1L << (29 - Player.py.misc.stl))) {
										m_ptr.csleep -= (100 / m_ptr.cdis);
										if (m_ptr.csleep > 0) {
											ignore = true;
										} else {
											wake = true;
											/* force it to be exactly zero */
											m_ptr.csleep = 0;
										}
									}
								}
							}
							if (m_ptr.stunned != 0) {
								/* NOTE: Balrog = 100*100 = 10000, it always
								 * recovers instantly */
								if (Misc1.randint(5000) < Monsters.c_list[m_ptr.mptr].level * Monsters.c_list[m_ptr.mptr].level) {
									m_ptr.stunned = 0;
								} else {
									m_ptr.stunned--;
								}
								if (m_ptr.stunned == 0) {
									if (m_ptr.ml) {
										cdesc = String.format("The %s ", Monsters.c_list[m_ptr.mptr].name);
										IO.msg_print(cdesc.concat("recovers and glares at you."));
									}
								}
							}
							if ((m_ptr.csleep == 0) && (m_ptr.stunned == 0)) {
								mon_move(i, rcmove);
							}
						}
						
						update_mon(i);
						if (m_ptr.ml) {
							r_ptr = Variable.c_recall[m_ptr.mptr];
							if (wake) {
								if (r_ptr.r_wake < Constants.MAX_UCHAR) {
									r_ptr.r_wake++;
								}
							} else if (ignore) {
								if (r_ptr.r_ignore < Constants.MAX_UCHAR) {
									r_ptr.r_ignore++;
								}
							}
							r_ptr.r_cmove |= rcmove.value();
						}
					}
				}
			} else {
				update_mon(i);
			}
			
			/* Get rid of an eaten/breathed on monster.  This is necessary because
			 * we can't delete monsters while scanning the m_list here.  This
			 * monster may have been killed during mon_move(). */
			if (m_ptr.hp < 0) {
				Moria3.fix2_delete_monster(i);
				continue;
			}
		}
		/* End processing monsters	   */
	}
}
