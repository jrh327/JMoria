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
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Misc4 m4;
	private Monsters mon;
	private Moria1 mor1;
	private Moria2 mor2;
	private Moria3 mor3;
	private Player py;
	private Spells spells;
	private Treasure t;
	private Variable var;
	
	private static Creature instance;
	private Creature() { }
	public static Creature getInstance() {
		if (instance == null) {
			instance = new Creature();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		m4 = Misc4.getInstance();
		mon = Monsters.getInstance();
		mor1 = Moria1.getInstance();
		mor2 = Moria2.getInstance();
		mor3 = Moria3.getInstance();
		py = Player.getInstance();
		spells = Spells.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Updates screen when monsters move about		-RAK-	*/
	public void update_mon(int monptr) {
		boolean flag;
		CaveType c_ptr;
		MonsterType m_ptr;
		CreatureType r_ptr;
		
		m_ptr = mon.m_list[monptr];
		flag = false;
		if ((m_ptr.cdis <= Constants.MAX_SIGHT) && (py.py.flags.status & Constants.PY_BLIND) == 0 && (m1.panel_contains(m_ptr.fy, m_ptr.fx))) {
			/* Wizard sight.	     */
			if (var.wizard) {
				flag = true;
			
			/* Normal sight.	     */
			} else if (m1.los(py.char_row, py.char_col, m_ptr.fy, m_ptr.fx)) {
				c_ptr = var.cave[m_ptr.fy][m_ptr.fx];
				r_ptr = mon.c_list[m_ptr.mptr];
				if (c_ptr.pl || c_ptr.tl || (var.find_flag != 0 && m_ptr.cdis < 2 && var.player_light)) {
					if ((Constants.CM_INVISIBLE & r_ptr.cmove) == 0) {
						flag = true;
					} else if (py.py.flags.see_inv) {
						flag = true;
						var.c_recall[m_ptr.mptr].r_cmove |= Constants.CM_INVISIBLE;
					}
				
				/* Infra vision.	 */
				} else if ((py.py.flags.see_infra > 0) && (m_ptr.cdis <= py.py.flags.see_infra) && (Constants.CD_INFRA & r_ptr.cdefense) != 0) {
					flag = true;
					var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_INFRA;
				}
			}
		}
		
		/* Light it up.	 */
		if (flag) {
			if (!m_ptr.ml) {
				mor1.disturb(true, false);
				m_ptr.ml = true;
				mor1.lite_spot(m_ptr.fy, m_ptr.fx);
				var.screen_change = true; /* notify inven_command */
			}
		
		/* Turn it off.	 */
		} else if (m_ptr.ml) {
			m_ptr.ml = false;
			mor1.lite_spot(m_ptr.fy, m_ptr.fx);
			var.screen_change = true; /* notify inven_command */
		}
	}
	
	/* Given speed,	 returns number of moves this turn.	-RAK-	*/
	/* NOTE: Player must always move at least once per iteration,	  */
	/*	 a slowed player is handled by moving monsters faster	 */
	public int movement_rate(int speed) {
		if (speed > 0) {
			if (py.py.flags.rest != 0) {
				return 1;
			} else {
				return speed;
			}
		} else {
			/* speed must be negative here */
			return ((var.turn % (2 - speed)) == 0) ? 1 : 0;
		}
	}
	
	/* Makes sure a new creature gets lit up.			-CJS- */
	public boolean check_mon_lite(int y, int x) {
		int monptr;
		
		monptr = var.cave[y][x].cptr;
		if (monptr <= 1) {
			return false;
		} else {
			update_mon(monptr);
			return mon.m_list[monptr].ml;
		}
	}
	
	/* Choose correct directions for monster movement	-RAK-	*/
	public void get_moves(int monptr, int[] mm) {
		int y, ay, x, ax, move_val;
		
		y = mon.m_list[monptr].fy - py.char_row;
		x = mon.m_list[monptr].fx - py.char_col;
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
	public void make_attack(int monptr) {
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
		
		if (var.death) {	/* don't beat a dead body! */
			return;
		}
		
		m_ptr = mon.m_list[monptr];
		r_ptr = mon.c_list[m_ptr.mptr];
		if (!m_ptr.ml) {
			cdesc = "It ";
		} else {
			cdesc = String.format("The %s ", r_ptr.name);
		}
		/* For "DIED_FROM" string	   */
		if ((Constants.CM_WIN & r_ptr.cmove) != 0) {
			ddesc = String.format("The %s", r_ptr.name);
		} else if (desc.is_a_vowel(r_ptr.name.charAt(0))) {
			ddesc = String.format("an %s", r_ptr.name);
		} else {
			ddesc = String.format("a %s", r_ptr.name);
		}
		/* End DIED_FROM		   */
		
		attackn = 0;
		int attstrindex = 0;
		attstr = r_ptr.damage[attstrindex];
		while ((attstr != 0) && !var.death) {
			attype = mon.monster_attacks[attstr].attack_type;
			adesc = mon.monster_attacks[attstr].attack_desc;
			adice = mon.monster_attacks[attstr].attack_dice;
			asides = mon.monster_attacks[attstr].attack_sides;
			attstrindex++;
			attstr = r_ptr.damage[attstrindex];
			flag = false;
			if ((py.py.flags.protevil > 0) && (r_ptr.cdefense & Constants.CD_EVIL) != 0 && ((py.py.misc.lev + 1) > r_ptr.level)) {
				if (m_ptr.ml) {
					var.c_recall[m_ptr.mptr].r_cdefense |= Constants.CD_EVIL;
				}
				attype = 99;
				adesc = 99;
			}
			p_ptr = py.py.misc;
			switch(attype)
			{
			case 1:	/*Normal attack  */
				if (mor1.test_hit(60, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 2:	/*Lose Strength*/
				if (mor1.test_hit(-3, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 3:	/*Confusion attack*/
				if (mor1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 4:	/*Fear attack    */
				if (mor1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 5:	/*Fire attack    */
				if (mor1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 6:	/*Acid attack    */
				if (mor1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 7:	/*Cold attack    */
				if (mor1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 8:	/*Lightning attack*/
				if (mor1.test_hit(10, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 9:	/*Corrosion attack*/
				if (mor1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 10:	/*Blindness attack*/
				if (mor1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 11:	/*Paralysis attack*/
				if (mor1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 12:	/*Steal Money    */
				if ((mor1.test_hit(5, r_ptr.level, 0, py.py.misc.lev, Constants.CLA_MISC_HIT)) && (py.py.misc.au > 0)) {
					flag = true;
				}
				break;
			case 13:	/*Steal Object   */
				if ((mor1.test_hit(2, r_ptr.level, 0, py.py.misc.lev, Constants.CLA_MISC_HIT)) && (t.inven_ctr > 0)) {
					flag = true;
				}
				break;
			case 14:	/*Poison	       */
				if (mor1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 15:	/*Lose dexterity*/
				if (mor1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 16:	/*Lose constitution*/
				if (mor1.test_hit(0, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 17:	/*Lose intelligence*/
				if (mor1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 18:	/*Lose wisdom*/
				if (mor1.test_hit(2, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 19:	/*Lose experience*/
				if (mor1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 20:	/*Aggravate monsters*/
				flag = true;
				break;
			case 21:	/*Disenchant	  */
				if (mor1.test_hit(20, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 22:	/*Eat food	  */
				if (mor1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 23:      /*Eat light	  */
				if (mor1.test_hit(5, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
					flag = true;
				}
				break;
			case 24:      /*Eat charges	  */
				if ((mor1.test_hit(15, r_ptr.level, 0, p_ptr.pac + p_ptr.ptoac, Constants.CLA_MISC_HIT)) && (t.inven_ctr > 0)) {
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
				mor1.disturb(true, false);
				tmp_str = cdesc;
				switch(adesc)
				{
				case 1: io.msg_print(tmp_str.concat("hits you.")); break;
				case 2: io.msg_print(tmp_str.concat("bites you.")); break;
				case 3: io.msg_print(tmp_str.concat("claws you.")); break;
				case 4: io.msg_print(tmp_str.concat("stings you.")); break;
				case 5: io.msg_print(tmp_str.concat("touches you.")); break;
				//case 6: msg_print(strcat(tmp_str, "kicks you.")); break;
				case 7: io.msg_print(tmp_str.concat("gazes at you.")); break;
				case 8: io.msg_print(tmp_str.concat("breathes on you.")); break;
				case 9: io.msg_print(tmp_str.concat("spits on you.")); break;
				case 10: io.msg_print(tmp_str.concat("makes a horrible wail."));break;
				//case 11: msg_print(strcat(tmp_str, "embraces you.")); break;
				case 12: io.msg_print(tmp_str.concat("crawls on you.")); break;
				case 13: io.msg_print(tmp_str.concat("releases a cloud of spores.")); break;
				case 14: io.msg_print(tmp_str.concat("begs you for money.")); break;
				case 15: io.msg_print("You've been slimed!"); break;
				case 16: io.msg_print(tmp_str.concat("crushes you.")); break;
				case 17: io.msg_print(tmp_str.concat("tramples you.")); break;
				case 18: io.msg_print(tmp_str.concat("drools on you.")); break;
				case 19:
					switch(m1.randint(9))
					{
					case 1: io.msg_print(tmp_str.concat("insults you!")); break;
					case 2: io.msg_print(tmp_str.concat("insults your mother!")); break;
					case 3: io.msg_print(tmp_str.concat("gives you the finger!")); break;
					case 4: io.msg_print(tmp_str.concat("humiliates you!")); break;
					case 5: io.msg_print(tmp_str.concat("wets on your leg!")); break;
					case 6: io.msg_print(tmp_str.concat("defiles you!")); break;
					case 7: io.msg_print(tmp_str.concat("dances around you!"));break;
					case 8: io.msg_print(tmp_str.concat("makes obscene gestures!")); break;
					case 9: io.msg_print(tmp_str.concat("moons you!!!")); break;
					}
					break;
				case 99: io.msg_print(tmp_str.concat("is repelled.")); break;
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
				
				damage = m1.damroll(adice, asides);
				switch(attype)
				{
				case 1:	/*Normal attack	*/
					/* round half-way case down */
					damage -= ((p_ptr.pac + p_ptr.ptoac) * damage) / 200;
					mor1.take_hit(damage, ddesc);
					break;
				case 2:	/*Lose Strength*/
					mor1.take_hit(damage, ddesc);
					if (py.py.flags.sustain_str) {
						io.msg_print("You feel weaker for a moment, but it passes.");
					} else if (m1.randint(2) == 1) {
						io.msg_print("You feel weaker.");
						m3.dec_stat(Constants.A_STR);
					} else {
						notice = false;
					}
					break;
				case 3:	/*Confusion attack*/
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					if (m1.randint(2) == 1) {
						if (f_ptr.confused < 1) {
							io.msg_print("You feel confused.");
							f_ptr.confused += m1.randint(r_ptr.level);
						} else {
							notice = false;
						}
						f_ptr.confused += 3;
					} else {
						notice = false;
					}
					break;
				case 4:	/*Fear attack	*/
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					if (m3.player_saves()) {
						io.msg_print("You resist the effects!");
					} else if (f_ptr.afraid < 1) {
						io.msg_print("You are suddenly afraid!");
						f_ptr.afraid += 3 + m1.randint(r_ptr.level);
					} else {
						f_ptr.afraid += 3;
						notice = false;
					}
					break;
				case 5:	/*Fire attack	*/
					io.msg_print("You are enveloped in flames!");
					mor2.fire_dam(damage, ddesc);
					break;
				case 6:	/*Acid attack	*/
					io.msg_print("You are covered in acid!");
					mor2.acid_dam(damage, ddesc);
					break;
				case 7:	/*Cold attack	*/
					io.msg_print("You are covered with frost!");
					mor2.cold_dam(damage, ddesc);
					break;
				case 8:	/*Lightning attack*/
					io.msg_print("Lightning strikes you!");
					mor2.light_dam(damage, ddesc);
					break;
				case 9:	/*Corrosion attack*/
					io.msg_print("A stinging red gas swirls about you.");
					mor2.corrode_gas(ddesc);
					mor1.take_hit(damage, ddesc);
					break;
				case 10:	/*Blindness attack*/
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					if (f_ptr.blind < 1) {
						f_ptr.blind += 10 + m1.randint(r_ptr.level);
						io.msg_print("Your eyes begin to sting.");
					} else {
						f_ptr.blind += 5;
						notice = false;
					}
					break;
				case 11:	/*Paralysis attack*/
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					if (m3.player_saves()) {
						io.msg_print("You resist the effects!");
					} else if (f_ptr.paralysis < 1) {
						if (f_ptr.free_act) {
							io.msg_print("You are unaffected.");
						} else {
							f_ptr.paralysis = m1.randint(r_ptr.level) + 3;
							io.msg_print("You are paralyzed.");
						}
					} else {
						notice = false;
					}
					break;
				case 12:	/*Steal Money	  */
					if ((py.py.flags.paralysis < 1) && (m1.randint(124) < py.py.stats.use_stat[Constants.A_DEX])) {
						io.msg_print("You quickly protect your money pouch!");
					} else {
						gold = (p_ptr.au / 10) + m1.randint(25);
						if (gold > p_ptr.au) {
							p_ptr.au = 0;
						} else {
							p_ptr.au -= gold;
						}
						io.msg_print("Your purse feels lighter.");
						m3.prt_gold();
					}
					if (m1.randint(2) == 1) {
						io.msg_print("There is a puff of smoke!");
						spells.teleport_away(monptr, Constants.MAX_SIGHT);
					}
					break;
				case 13:	/*Steal Object	 */
					if ((py.py.flags.paralysis < 1) && (m1.randint(124) < py.py.stats.use_stat[Constants.A_DEX])) {
						io.msg_print("You grab hold of your backpack!");
					} else {
						i.value(m1.randint(t.inven_ctr) - 1);
						m3.inven_destroy(i.value());
						io.msg_print("Your backpack feels lighter.");
					}
					if (m1.randint(2) == 1) {
						io.msg_print("There is a puff of smoke!");
						spells.teleport_away(monptr, Constants.MAX_SIGHT);
					}
					break;
				case 14:	/*Poison	 */
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					io.msg_print("You feel very sick.");
					f_ptr.poisoned += m1.randint(r_ptr.level) + 5;
					break;
				case 15:	/*Lose dexterity */
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					if (f_ptr.sustain_dex) {
						io.msg_print("You feel clumsy for a moment, but it passes.");
					} else {
						io.msg_print("You feel more clumsy.");
						m3.dec_stat(Constants.A_DEX);
					}
					break;
				case 16:	/*Lose constitution */
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					if (f_ptr.sustain_con) {
						io.msg_print("Your body resists the effects of the disease.");
					} else {
						io.msg_print("Your health is damaged!");
						m3.dec_stat(Constants.A_CON);
					}
					break;
				case 17:	/*Lose intelligence */
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					io.msg_print("You have trouble thinking clearly.");
					if (f_ptr.sustain_int) {
						io.msg_print("But your mind quickly clears.");
					} else {
						m3.dec_stat(Constants.A_INT);
					}
					break;
				case 18:	/*Lose wisdom	   */
					f_ptr = py.py.flags;
					mor1.take_hit(damage, ddesc);
					if (f_ptr.sustain_wis) {
						io.msg_print("Your wisdom is sustained.");
					} else {
						io.msg_print("Your wisdom is drained.");
						m3.dec_stat(Constants.A_WIS);
					}
					break;
				case 19:	/*Lose experience  */
					io.msg_print("You feel your life draining away!");
					spells.lose_exp(damage + (py.py.misc.exp / 100) * Constants.MON_DRAIN_LIFE);
					break;
				case 20:	/*Aggravate monster*/
					spells.aggravate_monster(20);
					break;
				case 21:	/*Disenchant	   */
					flag = false;
					switch(m1.randint(7))
					{
					case 1: i.value(Constants.INVEN_WIELD);	break;
					case 2: i.value(Constants.INVEN_BODY);	break;
					case 3: i.value(Constants.INVEN_ARM);	break;
					case 4: i.value(Constants.INVEN_OUTER);	break;
					case 5: i.value(Constants.INVEN_HANDS);	break;
					case 6: i.value(Constants.INVEN_HEAD);	break;
					case 7: i.value(Constants.INVEN_FEET);	break;
					}
					i_ptr = t.inventory[i.value()];
					if (i_ptr.tohit > 0) {
						i_ptr.tohit -= m1.randint(2);
						/* don't send it below zero */
						if (i_ptr.tohit < 0) {
							i_ptr.tohit = 0;
						}
						flag = true;
					}
					if (i_ptr.todam > 0) {
						i_ptr.todam -= m1.randint(2);
						/* don't send it below zero */
						if (i_ptr.todam < 0) {
							i_ptr.todam = 0;
						}
						flag = true;
					}
					if (i_ptr.toac > 0) {
						i_ptr.toac -= m1.randint(2);
						/* don't send it below zero */
						if (i_ptr.toac < 0) {
							i_ptr.toac = 0;
						}
						flag = true;
					}
					if (flag) {
						io.msg_print("There is a static feeling in the air.");
						mor1.calc_bonuses();
					} else {
						notice = false;
					}
					break;
				case 22:	/*Eat food	   */
					if (m3.find_range(Constants.TV_FOOD, Constants.TV_NEVER, i, j)) {
						m3.inven_destroy(i.value());
						io.msg_print("It got at your rations!");
					}
					else
						notice = false;
					break;
				case 23:	/*Eat light	   */
					i_ptr = t.inventory[Constants.INVEN_LIGHT];
					if (i_ptr.p1 > 0) {
						i_ptr.p1 -= (250 + m1.randint(250));
						if (i_ptr.p1 < 1)	i_ptr.p1 = 1;
						if (py.py.flags.blind < 1) {
							io.msg_print("Your light dims.");
						} else {
							notice = false;
						}
					} else {
						notice = false;
					}
					break;
				case 24:	/*Eat charges	  */
					i.value(m1.randint(t.inven_ctr) - 1);
					j.value(r_ptr.level);
					i_ptr = t.inventory[i.value()];
					if (((i_ptr.tval == Constants.TV_STAFF) || (i_ptr.tval == Constants.TV_WAND)) && (i_ptr.p1 > 0)) {
						m_ptr.hp += j.value() * i_ptr.p1;
						i_ptr.p1 = 0;
						if (!desc.known2_p(i_ptr)) {
							m4.add_inscribe(i_ptr, Constants.ID_EMPTY);
						}
						io.msg_print("Energy drains from your pack!");
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
				if (py.py.flags.confuse_monster && adesc != 99) {
					io.msg_print("Your hands stop glowing.");
					py.py.flags.confuse_monster = false;
					if ((m1.randint(Constants.MAX_MONS_LEVEL) < r_ptr.level) || (Constants.CD_NO_SLEEP & r_ptr.cdefense) != 0) {
						tmp_str = String.format("%sis unaffected.", cdesc);
					} else {
						tmp_str = String.format("%sappears confused.", cdesc);
						m_ptr.confused = true;
					}
					io.msg_print(tmp_str);
					if (visible && !var.death && m1.randint(4) == 1) {
						var.c_recall[m_ptr.mptr].r_cdefense |= r_ptr.cdefense & Constants.CD_NO_SLEEP;
					}
				}
				
				/* increase number of attacks if notice true, or if visible and had
				 * previously noticed the attack (in which case all this does is
				 * help player learn damage), note that in the second case do
				 * not increase attacks if creature repelled (no damage done) */
				if ((notice || (visible && var.c_recall[m_ptr.mptr].r_attacks[attackn] != 0 && attype != 99)) && var.c_recall[m_ptr.mptr].r_attacks[attackn] < Constants.MAX_UCHAR) {
					var.c_recall[m_ptr.mptr].r_attacks[attackn]++;
				}
				if (var.death && var.c_recall[m_ptr.mptr].r_deaths < Constants.MAX_SHORT) {
					var.c_recall[m_ptr.mptr].r_deaths++;
				}
			} else {
				if ((adesc >= 1 && adesc <= 3) || (adesc == 6)) {
					mor1.disturb(true, false);
					tmp_str = cdesc;
					io.msg_print(tmp_str.concat("misses you."));
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
	public void make_move(int monptr, int[] mm, LongPointer rcmove) {
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
		m_ptr = mon.m_list[monptr];
		movebits = mon.c_list[m_ptr.mptr].cmove;
		do {
			/* Get new position		*/
			newy.value(m_ptr.fy);
			newx.value(m_ptr.fx);
			m3.mmove(mm[i], newy, newx);
			c_ptr = var.cave[newy.value()][newx.value()];
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
					t_ptr = t.t_list[c_ptr.tptr];
					if ((movebits & Constants.CM_OPEN_DOOR) != 0) {	/* Creature can open doors.		     */
						stuck_door = false;
						if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
							do_turn = true;
							if (t_ptr.p1 == 0) {	/* Closed doors	 */
								do_move = true;
							} else if (t_ptr.p1 > 0) {	/* Locked doors	*/
								if (m1.randint((m_ptr.hp + 1) * (50 + t_ptr.p1)) < 40 * (m_ptr.hp - 10 - t_ptr.p1)) {
									t_ptr.p1 = 0;
								}
							} else if (t_ptr.p1 < 0) {	/* Stuck doors	*/
								if (m1.randint((m_ptr.hp + 1) * (50 - t_ptr.p1)) < 40 * (m_ptr.hp - 10 + t_ptr.p1)) {
									io.msg_print("You hear a door burst open!");
									mor1.disturb(true, false);
									stuck_door = true;
									do_move = true;
								}
							}
						} else if (t_ptr.tval == Constants.TV_SECRET_DOOR) {
							do_turn = true;
							do_move = true;
						}
						if (do_move) {
							desc.invcopy(t_ptr, Constants.OBJ_OPEN_DOOR);
							if (stuck_door) {	/* 50% chance of breaking door */
								t_ptr.p1 = 1 - m1.randint(2);
							}
							c_ptr.fval = Constants.CORR_FLOOR;
							mor1.lite_spot(newy.value(), newx.value());
							rcmove.value(rcmove.value() | Constants.CM_OPEN_DOOR);
							do_move = false;
						}
					} else {	/* Creature can not open doors, must bash them   */
						if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
							do_turn = true;
							if (m1.randint((m_ptr.hp + 1) * (80 + Math.abs(t_ptr.p1))) < 40 * (m_ptr.hp - 20 - Math.abs(t_ptr.p1))) {
								desc.invcopy(t_ptr, Constants.OBJ_OPEN_DOOR);
								/* 50% chance of breaking door */
								t_ptr.p1 = 1 - m1.randint(2);
								c_ptr.fval = Constants.CORR_FLOOR;
								mor1.lite_spot(newy.value(), newx.value());
								io.msg_print("You hear a door burst open!");
								mor1.disturb(true, false);
							}
						}
					}
				}
				/* Glyph of warding present?	   */
				if (do_move && (c_ptr.tptr != 0) && (t.t_list[c_ptr.tptr].tval == Constants.TV_VIS_TRAP) && (t.t_list[c_ptr.tptr].subval == 99)) {
					if (m1.randint(Constants.OBJ_RUNE_PROT) < mon.c_list[m_ptr.mptr].level) {
						if ((newy.value() == py.char_row) && (newx.value() == py.char_col)) {
							io.msg_print("The rune of protection is broken!");
						}
						mor3.delete_object(newy.value(), newx.value());
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
						if ((movebits & Constants.CM_EATS_OTHER) != 0 && (mon.c_list[m_ptr.mptr].mexp >= mon.c_list[mon.m_list[c_ptr.cptr].mptr].mexp)) {
							if (mon.m_list[c_ptr.cptr].ml) {
								rcmove.value(rcmove.value() | Constants.CM_EATS_OTHER);
							}
							/* It ate an already processed monster. Handle normally. */
							if (monptr < c_ptr.cptr) {
								mor3.delete_monster(c_ptr.cptr);
							
							/* If it eats this monster, an already processed monster
							 * will take its place, causing all kinds of havoc.  Delay
							 * the kill a bit. */
							} else {
								mor3.fix1_delete_monster(c_ptr.cptr);
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
						c_ptr = var.cave[newy.value()][newx.value()];
						
						if ((c_ptr.tptr != 0) && (t.t_list[c_ptr.tptr].tval <= Constants.TV_MAX_OBJECT)) {
							rcmove.value(rcmove.value() | Constants.CM_PICKS_UP);
							mor3.delete_object(newy.value(), newx.value());
						}
					}
					/* Move creature record		       */
					mor1.move_rec(m_ptr.fy, m_ptr.fx, newy.value(), newx.value());
					if (m_ptr.ml) {
						m_ptr.ml = false;
						mor1.lite_spot(m_ptr.fy, m_ptr.fx);
					}
					m_ptr.fy = newy.value();
					m_ptr.fx = newx.value();
					m_ptr.cdis = m1.distance(py.char_row, py.char_col, newy.value(), newx.value());
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
	public boolean mon_cast_spell(int monptr) {
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
		
		if (var.death) {
			return false;
		}
		
		m_ptr = mon.m_list[monptr];
		r_ptr = mon.c_list[m_ptr.mptr];
		chance = (r_ptr.spells & Constants.CS_FREQ);
		/* 1 in x chance of casting spell		   */
		if (m1.randint((int)chance) != 1) {
			took_turn = false;
		/* Must be within certain range		   */
		} else if (m_ptr.cdis > Constants.MAX_SPELL_DIS) {
			took_turn = false;
		/* Must have unobstructed Line-Of-Sight	   */
		} else if (!m1.los(py.char_row, py.char_col, m_ptr.fy, m_ptr.fx)) {
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
			} else if (desc.is_a_vowel(r_ptr.name.charAt(0))) {
				ddesc = String.format("an %s", r_ptr.name);
			} else {
				ddesc = String.format("a %s", r_ptr.name);
			}
			/* End DIED_FROM		       */
			
			/* Extract all possible spells into spell_choice */
			i = new LongPointer(r_ptr.spells & ~Constants.CS_FREQ);
			k = 0;
			while (i.value() != 0) {
				spell_choice[k] = m1.bit_pos(i);
				k++;
			}
			/* Choose a spell to cast			       */
			thrown_spell = spell_choice[m1.randint(k) - 1];
			thrown_spell++;
			/* all except teleport_away() and drain mana spells always disturb */
			if (thrown_spell > 6 && thrown_spell != 17) {
				mor1.disturb(true, false);
			}
			/* save some code/data space here, with a small time penalty */
			if ((thrown_spell < 14 && thrown_spell > 6) || (thrown_spell == 16)) {
				cdesc = cdesc.concat("casts a spell.");
				io.msg_print(cdesc);
			}
			/* Cast the spell.			     */
			switch(thrown_spell)
			{
			case 5:	 /*Teleport Short*/
				spells.teleport_away(monptr, 5);
				break;
			case 6:	 /*Teleport Long */
				spells.teleport_away(monptr, Constants.MAX_SIGHT);
				break;
			case 7:	 /*Teleport To	 */
				spells.teleport_to(m_ptr.fy, m_ptr.fx);
				break;
			case 8:	 /*Light Wound	 */
				if (m3.player_saves()) {
					io.msg_print("You resist the effects of the spell.");
				} else {
					mor1.take_hit(m1.damroll(3, 8), ddesc);
				}
				break;
			case 9:	 /*Serious Wound */
				if (m3.player_saves()) {
					io.msg_print("You resist the effects of the spell.");
				} else {
					mor1.take_hit(m1.damroll(8, 8), ddesc);
				}
				break;
			case 10:  /*Hold Person	  */
				if (py.py.flags.free_act) {
					io.msg_print("You are unaffected.");
				} else if (m3.player_saves()) {
					io.msg_print("You resist the effects of the spell.");
				} else if (py.py.flags.paralysis > 0) {
					py.py.flags.paralysis += 2;
				} else {
					py.py.flags.paralysis = m1.randint(5)+4;
				}
				break;
			case 11:  /*Cause Blindness*/
				if (m3.player_saves()) {
					io.msg_print("You resist the effects of the spell.");
				} else if (py.py.flags.blind > 0) {
					py.py.flags.blind += 6;
				} else {
					py.py.flags.blind += 12 + m1.randint(3);
				}
				break;
			case 12:  /*Cause Confuse */
				if (m3.player_saves()) {
					io.msg_print("You resist the effects of the spell.");
				} else if (py.py.flags.confused > 0) {
					py.py.flags.confused += 2;
				} else {
					py.py.flags.confused = m1.randint(5) + 3;
				}
				break;
			case 13:  /*Cause Fear	  */
				if (m3.player_saves()) {
					io.msg_print("You resist the effects of the spell.");
				} else if (py.py.flags.afraid > 0) {
					py.py.flags.afraid += 2;
				} else {
					py.py.flags.afraid = m1.randint(5) + 3;
				}
				break;
			case 14:  /*Summon Monster*/
				cdesc = cdesc.concat("magically summons a monster!");
				io.msg_print(cdesc);
				y = new IntPointer(py.char_row);
				x = new IntPointer(py.char_col);
				/* in case compact_monster() is called,it needs monptr */
				var.hack_monptr = monptr;
				m1.summon_monster(y, x, false);
				var.hack_monptr = -1;
				update_mon (var.cave[y.value()][x.value()].cptr);
				break;
			case 15:  /*Summon Undead*/
				cdesc = cdesc.concat("magically summons an undead!");
				io.msg_print(cdesc);
				y = new IntPointer(py.char_row);
				x = new IntPointer(py.char_col);
				/* in case compact_monster() is called,it needs monptr */
				var.hack_monptr = monptr;
				m1.summon_undead(y, x);
				var.hack_monptr = -1;
				update_mon(var.cave[y.value()][x.value()].cptr);
				break;
			case 16:  /*Slow Person	 */
				if (py.py.flags.free_act) {
					io.msg_print("You are unaffected.");
				} else if (m3.player_saves()) {
					io.msg_print("You resist the effects of the spell.");
				} else if (py.py.flags.slow > 0) {
					py.py.flags.slow += 2;
				} else {
					py.py.flags.slow = m1.randint(5) + 3;
				}
				break;
			case 17:  /*Drain Mana	 */
				if (py.py.misc.cmana > 0) {
					mor1.disturb(true, false);
					outval = String.format("%sdraws psychic energy from you!", cdesc);
					io.msg_print(outval);
					if (m_ptr.ml) {
						outval = String.format("%sappears healthier.", cdesc);
						io.msg_print(outval);
					}
					r1 = (m1.randint((int)r_ptr.level) >> 1) + 1;
					if (r1 > py.py.misc.cmana) {
						r1 = py.py.misc.cmana;
						py.py.misc.cmana = 0;
						py.py.misc.cmana_frac = 0;
					} else {
						py.py.misc.cmana -= r1;
					}
					m3.prt_cmana();
					m_ptr.hp += 6*(r1);
				}
				break;
			case 20:  /*Breath Light */
				cdesc = cdesc.concat("breathes lightning.");
				io.msg_print(cdesc);
				spells.breath(Constants.GF_LIGHTNING, py.char_row, py.char_col, (m_ptr.hp / 4), ddesc, monptr);
				break;
			case 21:  /*Breath Gas	 */
				cdesc = cdesc.concat("breathes gas.");
				io.msg_print(cdesc);
				spells.breath(Constants.GF_POISON_GAS, py.char_row, py.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			case 22:  /*Breath Acid	 */
				cdesc = cdesc.concat("breathes acid.");
				io.msg_print(cdesc);
				spells.breath(Constants.GF_ACID, py.char_row, py.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			case 23:  /*Breath Frost */
				cdesc = cdesc.concat("breathes frost.");
				io.msg_print(cdesc);
				spells.breath(Constants.GF_FROST, py.char_row, py.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			case 24:  /*Breath Fire	 */
				cdesc = cdesc.concat("breathes fire.");
				io.msg_print(cdesc);
				spells.breath(Constants.GF_FIRE, py.char_row, py.char_col, (m_ptr.hp / 3), ddesc, monptr);
				break;
			default:
				cdesc = cdesc.concat("cast unknown spell.");
				io.msg_print(cdesc);
			}
			/* End of spells				       */
			if (m_ptr.ml) {
				var.c_recall[m_ptr.mptr].r_spells |= 1L << (thrown_spell - 1);
				if ((var.c_recall[m_ptr.mptr].r_spells & Constants.CS_FREQ) != Constants.CS_FREQ) {
					var.c_recall[m_ptr.mptr].r_spells++;
				}
				if (var.death && var.c_recall[m_ptr.mptr].r_deaths < Constants.MAX_SHORT) {
					var.c_recall[m_ptr.mptr].r_deaths++;
				}
			}
		}
		return took_turn;
	}
	
	/* Places creature adjacent to given location		-RAK-	*/
	/* Rats and Flys are fun!					 */
	public boolean multiply_monster(int y, int x, int cr_index, int monptr) {
		int i, j, k;
		CaveType c_ptr;
		boolean result;
		
		i = 0;
		do {
			j = y - 2 + m1.randint(3);
			k = x - 2 + m1.randint(3);
			/* don't create a new creature on top of the old one, that causes
			 * invincible/invisible creatures to appear */
			if (m1.in_bounds(j, k) && (j != y || k != x)) {
				c_ptr = var.cave[j][k];
				if ((c_ptr.fval <= Constants.MAX_OPEN_SPACE) && (c_ptr.tptr == 0) && (c_ptr.cptr != 1)) {
					if (c_ptr.cptr > 1) {	/* Creature there already?	*/
						/* Some critters are cannibalistic!	    */
						if ((mon.c_list[cr_index].cmove & Constants.CM_EATS_OTHER) != 0 && mon.c_list[cr_index].mexp >= mon.c_list[mon.m_list[c_ptr.cptr].mptr].mexp) {
							/* Check the experience level -CJS- */ 
							/* It ate an already processed monster.Handle normally.*/
							if (monptr < c_ptr.cptr) {
								mor3.delete_monster(c_ptr.cptr);
							
							/* If it eats this monster, an already processed mosnter
							 * will take its place, causing all kinds of havoc.
							 * Delay the kill a bit. */
							} else {
								mor3.fix1_delete_monster(c_ptr.cptr);
							}
							
							/* in case compact_monster() is called,it needs monptr */
							var.hack_monptr = monptr;
							/* Place_monster() may fail if monster list full.  */
							result = m1.place_monster(j, k, cr_index, false);
							var.hack_monptr = -1;
							if (!result) {
								return false;
							}
							mon.mon_tot_mult++;
							return check_mon_lite(j, k);
						}
					} else {
						/* All clear,  place a monster	  */
						/* in case compact_monster() is called,it needs monptr */
						var.hack_monptr = monptr;
						/* Place_monster() may fail if monster list full.  */
						result = m1.place_monster(j, k, cr_index, false);
						var.hack_monptr = -1;
						if (!result) {
							return false;
						}
						mon.mon_tot_mult++;
						return check_mon_lite(j, k);
					}
				}
			}
			i++;
		} while (i <= 18);
		return false;
	}
	
	/* Move the critters about the dungeon			-RAK-	*/
	public void mon_move(int monptr, LongPointer rcmove) {
		int i, j;
		int k, dir;
		boolean move_test;
		CreatureType r_ptr;
		MonsterType m_ptr;
		int[] mm = new int[9];
		int rest_val;
		
		m_ptr = mon.m_list[monptr];
		r_ptr = mon.c_list[m_ptr.mptr];
		/* Does the critter multiply?				   */
		/* rest could be negative, to be safe, only use mod with positive values. */
		rest_val = Math.abs(py.py.flags.rest);
		if ((r_ptr.cmove & Constants.CM_MULTIPLY) != 0 && (Constants.MAX_MON_MULT >= mon.mon_tot_mult) && ((rest_val % Constants.MON_MULT_ADJ) == 0)) {
			k = 0;
			for (i = m_ptr.fy-1; i <= m_ptr.fy+1; i++) {
				for (j = m_ptr.fx-1; j <= m_ptr.fx+1; j++) {
					if (m1.in_bounds(i, j) && (var.cave[i][j].cptr > 1)) {
						k++;
					}
				}
			}
			/* can't call m1.randint with a value of zero, increment counter
			 * to allow creature multiplication */
			if (k == 0) {
				k++;
			}
			if ((k < 4) && (m1.randint(k * Constants.MON_MULT_ADJ) == 1)) {
				if (multiply_monster((int)m_ptr.fy, (int)m_ptr.fx, m_ptr.mptr, monptr)) {
					rcmove.value(rcmove.value() | Constants.CM_MULTIPLY);
				}
			}
		}
		move_test = false;
		
		/* if in wall, must immediately escape to a clear area */
		if ((r_ptr.cmove & Constants.CM_PHASE) == 0 && (var.cave[m_ptr.fy][m_ptr.fx].fval >= Constants.MIN_CAVE_WALL)) {
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
					if ((dir != 5) && (var.cave[i][j].fval <= Constants.MAX_OPEN_SPACE) && (var.cave[i][j].cptr != 1)) {
						mm[k++] = dir;
					}
					dir++;
				}
			}
			if (k != 0) {
				/* put a random direction first */
				dir = m1.randint(k) - 1;
				i = mm[0];
				mm[0] = mm[dir];
				mm[dir] = i;
				make_move(monptr, mm, rcmove);
				/* this can only fail if mm[0] has a rune of protection */
			}
			/* if still in a wall, let it dig itself out, but also apply some
			 * more damage */
			if (var.cave[m_ptr.fy][m_ptr.fx].fval >= Constants.MIN_CAVE_WALL) {
				/* in case the monster dies, may need to call fix1_delete_monster()
				 * instead of delete_monsters() */
				var.hack_monptr = monptr;
				i = mor3.mon_take_hit(monptr, m1.damroll(8, 8));
				var.hack_monptr = -1;
				if (i >= 0) {
					io.msg_print("You hear a scream muffled by rock!");
					m3.prt_experience();
				} else {
					io.msg_print ("A creature digs itself out from the rock!");
					mor3.twall(m_ptr.fy, m_ptr.fx, 1, 0);
				}
			}
			return;  /* monster movement finished */
		
		/* Creature is confused?  Chance it becomes un-confused  */
		} else if (m_ptr.confused) {
			mm[0] = m1.randint(9);
			mm[1] = m1.randint(9);
			mm[2] = m1.randint(9);
			mm[3] = m1.randint(9);
			mm[4] = m1.randint(9);
			/* don't move him if he is not supposed to move! */
			if ((r_ptr.cmove & Constants.CM_ATTACK_ONLY) == 0) {
				make_move(monptr, mm, rcmove);
			}
			if (m1.randint(8) == 1) {
				m_ptr.confused = false;
			}
			move_test = true;
		
		/* Creature may cast a spell */
		} else if (r_ptr.spells != 0) {
			move_test = mon_cast_spell(monptr);
		}
		if (!move_test) {
			/* 75% random movement */
			if ((r_ptr.cmove & Constants.CM_75_RANDOM) != 0 && (m1.randint(100) < 75)) {
				mm[0] = m1.randint(9);
				mm[1] = m1.randint(9);
				mm[2] = m1.randint(9);
				mm[3] = m1.randint(9);
				mm[4] = m1.randint(9);
				rcmove.value(rcmove.value() | Constants.CM_75_RANDOM);
				make_move(monptr, mm, rcmove);
			
			/* 40% random movement */
			} else if ((r_ptr.cmove & Constants.CM_40_RANDOM) != 0 && (m1.randint(100) < 40)) {
				mm[0] = m1.randint(9);
				mm[1] = m1.randint(9);
				mm[2] = m1.randint(9);
				mm[3] = m1.randint(9);
				mm[4] = m1.randint(9);
				rcmove.value(rcmove.value() | Constants.CM_40_RANDOM);
				make_move(monptr, mm, rcmove);
			
			/* 20% random movement */
			} else if ((r_ptr.cmove & Constants.CM_20_RANDOM) != 0 && (m1.randint(100) < 20)) {
				mm[0] = m1.randint(9);
				mm[1] = m1.randint(9);
				mm[2] = m1.randint(9);
				mm[3] = m1.randint(9);
				mm[4] = m1.randint(9);
				rcmove.value(rcmove.value() | Constants.CM_20_RANDOM);
				make_move(monptr, mm, rcmove);
			
			/* Normal movement */
			} else if ((r_ptr.cmove & Constants.CM_MOVE_NORMAL) != 0) {
				if (m1.randint(200) == 1) {
					mm[0] = m1.randint(9);
					mm[1] = m1.randint(9);
					mm[2] = m1.randint(9);
					mm[3] = m1.randint(9);
					mm[4] = m1.randint(9);
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
				if (var.c_recall[m_ptr.mptr].r_attacks[0] < Constants.MAX_UCHAR) {
					var.c_recall[m_ptr.mptr].r_attacks[0]++;
				}
				/* Another little hack for Quylthulgs, so that one can eventually
				 * learn their speed.  */
				if (var.c_recall[m_ptr.mptr].r_attacks[0] > 20) {
					var.c_recall[m_ptr.mptr].r_cmove |= Constants.CM_ONLY_MAGIC;
				}
			}
		}
	}
	
	/* Creatures movement and attacking are done from here	-RAK-	*/
	public void creatures(boolean attack) {
		int i, k;
		MonsterType m_ptr;
		MonsterRecallType r_ptr;
		long notice;
		LongPointer rcmove = new LongPointer();
		boolean wake, ignore;
		String cdesc;
		
		/* Process the monsters  */
		for (i = mon.mfptr - 1; i >= Constants.MIN_MONIX && !var.death; i--) {
			m_ptr = mon.m_list[i];
			/* Get rid of an eaten/breathed on monster.  Note: Be sure not to
			 * process this monster. This is necessary because we can't delete
			 * monsters while scanning the m_list here. */
			if (m_ptr.hp < 0) {
				mor3.fix2_delete_monster(i);
				continue;
			}
			
			m_ptr.cdis = m1.distance(py.char_row, py.char_col, m_ptr.fy, m_ptr.fx);
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
						if (m_ptr.ml || (m_ptr.cdis <= mon.c_list[m_ptr.mptr].aaf)
								|| (((mon.c_list[m_ptr.mptr].cmove & Constants.CM_PHASE) == 0) && var.cave[m_ptr.fy][m_ptr.fx].fval >= Constants.MIN_CAVE_WALL)) {
							/* Monsters trapped in rock must be given a turn also,
							 * so that they will die/dig out immediately.  */
							
							if (m_ptr.csleep > 0) {
								if (py.py.flags.aggravate > 0) {
									m_ptr.csleep = 0;
								} else if ((py.py.flags.rest == 0 && py.py.flags.paralysis < 1) || (m1.randint(50) == 1)) {
									notice = m1.randint(1024);
									if (notice * notice * notice <= (1L << (29 - py.py.misc.stl))) {
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
								if (m1.randint(5000) < mon.c_list[m_ptr.mptr].level * mon.c_list[m_ptr.mptr].level) {
									m_ptr.stunned = 0;
								} else {
									m_ptr.stunned--;
								}
								if (m_ptr.stunned == 0) {
									if (!m_ptr.ml) {
										cdesc = "It ";
									} else {
										cdesc = String.format("The %s ", mon.c_list[m_ptr.mptr].name);
									}
									io.msg_print(cdesc.concat("recovers and glares at you."));
								}
							}
							if ((m_ptr.csleep == 0) && (m_ptr.stunned == 0)) {
								mon_move(i, rcmove);
							}
						}
						
						update_mon(i);
						if (m_ptr.ml) {
							r_ptr = var.c_recall[m_ptr.mptr];
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
				mor3.fix2_delete_monster(i);
				continue;
			}
		}
		/* End processing monsters	   */
	}
}
