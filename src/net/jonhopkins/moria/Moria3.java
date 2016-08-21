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
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;
import net.jonhopkins.moria.types.SpellType;

public class Moria3 {
	private Desc desc;
	private IO io;
	private Misc1 m1;
	private Misc3 m3;
	private Monsters mon;
	private Moria1 mor1;
	private Moria2 mor2;
	private Player py;
	private Store2 store2;
	private Treasure t;
	private Variable var;
	
	private static Moria3 instance;
	private Moria3() { }
	public static Moria3 getInstance() {
		if (instance == null) {
			instance = new Moria3();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		desc = Desc.getInstance();
		io = IO.getInstance();
		m1 = Misc1.getInstance();
		m3 = Misc3.getInstance();
		mon = Monsters.getInstance();
		mor1 = Moria1.getInstance();
		mor2 = Moria2.getInstance();
		py = Player.getInstance();
		store2 = Store2.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
	}
	
	/* Player hit a trap.	(Chuckle)			-RAK-	*/
	public void hit_trap(int y, int x) {
		int i, num, dam;
		IntPointer ty, tx;
		CaveType c_ptr;
		PlayerMisc p_ptr;
		InvenType t_ptr;
		String tmp;
		
		mor2.end_find();
		mor2.change_trap(y, x);
		c_ptr = var.cave[y][x];
		p_ptr = py.py.misc;
		t_ptr = t.t_list[c_ptr.tptr];
		dam = m1.pdamroll(t_ptr.damage);
		switch(t_ptr.subval)
		{
		case 1:  /* Open pit*/
			io.msg_print("You fell into a pit!");
			if (py.py.flags.ffall > 0) {
				io.msg_print("You gently float down.");
			} else {
				tmp = desc.objdes(t_ptr, true);
				mor1.take_hit(dam, tmp);
			}
			break;
		case 2: /* Arrow trap*/
			if (mor1.test_hit(125, 0, 0, p_ptr.pac+p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
				tmp = desc.objdes(t_ptr, true);
				mor1.take_hit(dam, tmp);
				io.msg_print("An arrow hits you.");
			} else {
				io.msg_print("An arrow barely misses you.");
			}
			break;
		case 3: /* Covered pit*/
			io.msg_print("You fell into a covered pit.");
			if (py.py.flags.ffall > 0) {
				io.msg_print("You gently float down.");
			} else {
				tmp = desc.objdes(t_ptr, true);
				mor1.take_hit(dam, tmp);
			}
			m3.place_trap(y, x, 0);
			break;
		case 4: /* Trap door*/
			io.msg_print("You fell through a trap door!");
			var.new_level_flag = true;
			var.dun_level++;
			if (py.py.flags.ffall > 0) {
				io.msg_print("You gently float down.");
			} else {
				tmp = desc.objdes(t_ptr, true);
				mor1.take_hit(dam, tmp);
			}
			/* Force the messages to display before starting to generate the
			 * next level.  */
			io.msg_print("");
			break;
		case 5: /* Sleep gas*/
			if (py.py.flags.paralysis == 0) {
				io.msg_print("A strange white mist surrounds you!");
				if (py.py.flags.free_act) {
					io.msg_print("You are unaffected.");
				} else {
					io.msg_print("You fall asleep.");
					py.py.flags.paralysis += m1.randint(10) + 4;
				}
			}
			break;
		case 6: /* Hid Obj*/
			delete_object(y, x);
			m3.place_object(y, x, false);
			io.msg_print("Hmmm, there was something under this rock.");
			break;
		case 7:  /* STR Dart*/
			if (mor1.test_hit(125, 0, 0, p_ptr.pac+p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
				if (!py.py.flags.sustain_str) {
					m3.dec_stat(Constants.A_STR);
					tmp = desc.objdes(t_ptr, true);
					mor1.take_hit(dam, tmp);
					io.msg_print("A small dart weakens you!");
				} else {
					io.msg_print("A small dart hits you.");
				}
			} else {
				io.msg_print("A small dart barely misses you.");
			}
			break;
		case 8: /* Teleport*/
			var.teleport_flag = true;
			io.msg_print("You hit a teleport trap!");
			/* Light up the teleport trap, before we teleport away.  */
			mor1.move_light(y, x, y, x);
			break;
		case 9: /* Rockfall*/
			mor1.take_hit(dam, "a falling rock");
			delete_object(y, x);
			m3.place_rubble(y, x);
			io.msg_print("You are hit by falling rock.");
			break;
		case 10: /* Corrode gas*/
			/* Makes more sense to print the message first, then damage an
		 object.  */
			io.msg_print("A strange red gas surrounds you.");
			mor2.corrode_gas("corrosion gas");
			break;
		case 11: /* Summon mon*/
			delete_object(y, x);	/* Rune disappears.    */
			num = 2 + m1.randint (3);
			for (i = 0; i < num; i++) {
				ty = new IntPointer(y);
				tx = new IntPointer(x);
				m1.summon_monster(ty, tx, false);
			}
			break;
		case 12: /* Fire trap*/
			io.msg_print("You are enveloped in flames!");
			mor2.fire_dam(dam, "a fire trap");
			break;
		case 13: /* Acid trap*/
			io.msg_print("You are splashed with acid!");
			mor2.acid_dam(dam, "an acid trap");
			break;
		case 14: /* Poison gas*/
			io.msg_print("A pungent green gas surrounds you!");
			mor2.poison_gas(dam, "a poison gas trap");
			break;
		case 15: /* Blind Gas */
			io.msg_print("A black gas surrounds you!");
			py.py.flags.blind += m1.randint(50) + 50;
			break;
		case 16: /* Confuse Gas*/
			io.msg_print("A gas of scintillating colors surrounds you!");
			py.py.flags.confused += m1.randint(15) + 15;
			break;
		case 17: /* Slow Dart*/
			if (mor1.test_hit(125, 0, 0, p_ptr.pac+p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
				tmp = desc.objdes(t_ptr, true);
				mor1.take_hit(dam, tmp);
				io.msg_print("A small dart hits you!");
				if (py.py.flags.free_act) {
					io.msg_print("You are unaffected.");
				} else {
					py.py.flags.slow += m1.randint(20) + 10;
				}
			} else {
				io.msg_print("A small dart barely misses you.");
			}
			break;
		case 18: /* CON Dart*/
			if (mor1.test_hit(125, 0, 0, p_ptr.pac+p_ptr.ptoac, Constants.CLA_MISC_HIT)) {
				if (!py.py.flags.sustain_con) {
					m3.dec_stat(Constants.A_CON);
					tmp = desc.objdes(t_ptr, true);
					mor1.take_hit(dam, tmp);
					io.msg_print("A small dart saps your health!");
				} else {
					io.msg_print("A small dart hits you.");
				}
			} else {
				io.msg_print("A small dart barely misses you.");
			}
			break;
		case 19: /*Secret Door*/
			break;
		case 99: /* Scare Mon*/
			break;
			/* Town level traps are special,	the stores.	*/
		case 101: /* General    */
			store2.enter_store(0);
			break;
		case 102: /* Armory	    */
			store2.enter_store(1);
			break;
		case 103: /* Weaponsmith*/
			store2.enter_store(2);
			break;
		case 104: /* Temple	    */
			store2.enter_store(3);
			break;
		case 105: /* Alchemy    */
			store2.enter_store(4);
			break;
		case 106: /* Magic-User */
			store2.enter_store(5);
			break;
		default:
			io.msg_print("Unknown trap value.");
			break;
		}
	}
	
	/* Return spell number and failure chance		-RAK-	*/
	/* returns -1 if no spells in book
	 * returns 1 if choose a spell in book to cast
	 * returns 0 if don't choose a spell, i.e. exit with an escape */
	public int cast_spell(String prompt, int item_val, IntPointer sn, IntPointer sc) {
		LongPointer j;
		int i, k;
		int[] spell = new int[31];
		int result, first_spell;
		SpellType[] s_ptr;
		
		result = -1;
		i = 0;
		j = new LongPointer(t.inventory[item_val].flags);
		first_spell = m1.bit_pos(j);
		/* set j again, since bit_pos modified it */
		j.value(t.inventory[item_val].flags & py.spell_learned);
		s_ptr = py.magic_spell[py.py.misc.pclass - 1];
		while (j.value() != 0) {
			k = m1.bit_pos(j);
			if (s_ptr[k].slevel <= py.py.misc.lev) {
				spell[i] = k;
				i++;
			}
		}
		if (i > 0) {
			result = m3.get_spell(spell, i, sn, sc, prompt, first_spell) ? 1 : 0;
			if (result != 0 && py.magic_spell[py.py.misc.pclass - 1][sn.value()].smana > py.py.misc.cmana) {
				if (py.Class[py.py.misc.pclass].spell == Constants.MAGE) {
					result = io.get_check("You summon your limited strength to cast this one! Confirm?") ? 1 : 0;
				} else {
					result = io.get_check("The gods may think you presumptuous for this! Confirm?") ? 1 : 0;
				}
			}
		}
		return result;
	}
	
	/* Player is on an object.  Many things can happen based -RAK-	*/
	/* on the TVAL of the object.  Traps are set off, money and most */
	/* objects are picked up.  Some objects, such as open doors, just*/
	/* sit there.						       */
	public void carry(int y, int x, boolean pickup) {
		int locn, i;
		String out_val, tmp_str;
		CaveType c_ptr;
		InvenType i_ptr;
		
		c_ptr = var.cave[y][x];
		i_ptr = t.t_list[c_ptr.tptr];
		i = t.t_list[c_ptr.tptr].tval;
		if (i <= Constants.TV_MAX_PICK_UP) {
			mor2.end_find();
			/* There's GOLD in them thar hills!      */
			if (i == Constants.TV_GOLD) {
				py.py.misc.au += i_ptr.cost;
				tmp_str = desc.objdes(i_ptr, true);
				out_val = String.format("You have found %d gold pieces worth of %s", i_ptr.cost, tmp_str);
				m3.prt_gold();
				delete_object(y, x);
				io.msg_print(out_val);
			} else {
				if (m3.inven_check_num(i_ptr)) {	/* Too many objects?	    */
					/* Okay,  pick it up      */
					if (pickup && var.prompt_carry_flag.value()) {
						tmp_str = desc.objdes(i_ptr, true);
						/* change the period to a question mark */
						tmp_str = tmp_str.substring(0, tmp_str.length() - 1) + "?";
						out_val = String.format("Pick up %s", tmp_str);
						pickup = io.get_check(out_val);
					}
					/* Check to see if it will change the players speed. */
					if (pickup && !m3.inven_check_weight(i_ptr)) {
						tmp_str = desc.objdes(i_ptr, true);
						/* change the period to a question mark */
						tmp_str = tmp_str.substring(0, tmp_str.length() - 1) + "?";
						out_val = String.format("Exceed your weight limit to pick up %s", tmp_str);
						pickup = io.get_check(out_val);
					}
					/* Attempt to pick up an object.	       */
					if (pickup) {
						locn = m3.inven_carry(i_ptr);
						tmp_str = desc.objdes(t.inventory[locn], true);
						out_val = String.format("You have %s (%c)", tmp_str, locn + 'a');
						io.msg_print(out_val);
						delete_object(y, x);
					}
				} else {
					tmp_str = desc.objdes(i_ptr, true);
					out_val = String.format("You can't carry %s", tmp_str);
					io.msg_print(out_val);
				}
			}
		
		/* OPPS!				   */
		} else if (i == Constants.TV_INVIS_TRAP || i == Constants.TV_VIS_TRAP || i == Constants.TV_STORE_DOOR) {
			hit_trap(y, x);
		}
	}
	
	private void mondeepcopy(MonsterType to, MonsterType from) {
		to.hp = from.hp;
		to.csleep = from.csleep;
		to.cspeed = from.cspeed;
		to.mptr = from.mptr;
		to.fy = from.fy;
		to.fx = from.fx;
		to.cdis = from.cdis;
		to.ml = from.ml;
		to.stunned = from.stunned;
		to.confused = from.confused;
	}
	
	/* Deletes a monster entry from the level		-RAK-	*/
	public void delete_monster(int j) {
		MonsterType m_ptr;
		
		m_ptr = mon.m_list[j];
		var.cave[m_ptr.fy][m_ptr.fx].cptr = 0;
		if (m_ptr.ml) {
			mor1.lite_spot(m_ptr.fy, m_ptr.fx);
		}
		if (j != mon.mfptr - 1) {
			m_ptr = mon.m_list[mon.mfptr - 1];
			var.cave[m_ptr.fy][m_ptr.fx].cptr = j;
			mondeepcopy(mon.m_list[j], mon.m_list[mon.mfptr - 1]);
		}
		mon.mfptr--;
		mondeepcopy(mon.m_list[mon.mfptr], mon.blank_monster());
		if (mon.mon_tot_mult > 0) {
			mon.mon_tot_mult--;
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
	public void fix1_delete_monster(int j) {
		MonsterType m_ptr;
		
		m_ptr = mon.m_list[j];
		/* force the hp negative to ensure that the monster is dead, for example,
		 * if the monster was just eaten by another, it will still have positive
		 * hit points */
		m_ptr.hp = -1;
		var.cave[m_ptr.fy][m_ptr.fx].cptr = 0;
		if (m_ptr.ml) {
			mor1.lite_spot(m_ptr.fy, m_ptr.fx);
		}
		if (mon.mon_tot_mult > 0) {
			mon.mon_tot_mult--;
		}
	}
	
	/* fix2_delete_monster does everything in delete_monster that wasn't done
	 * by fix1_monster_delete above, this is only called in creatures() */
	public void fix2_delete_monster(int j) {
		MonsterType m_ptr;
		
		if (j != mon.mfptr - 1) {
			m_ptr = mon.m_list[mon.mfptr - 1];
			var.cave[m_ptr.fy][m_ptr.fx].cptr = j;
			mondeepcopy(mon.m_list[j], mon.m_list[mon.mfptr - 1]);
		}
		mondeepcopy(mon.m_list[mon.mfptr - 1], mon.blank_monster());
		mon.mfptr--;
	}
	
	/* Creates objects nearby the coordinates given		-RAK-	  */
	public int summon_object(int y, int x, int num, int typ) {
		int i, j, k;
		CaveType c_ptr;
		int real_typ, res;
		
		if (typ == 1) {
			real_typ = 1; /* typ == 1 . objects */
		} else {
			real_typ = 256; /* typ == 2 . gold */
		}
		res = 0;
		do {
			i = 0;
			do {
				j = y - 3 + m1.randint(5);
				k = x - 3 + m1.randint(5);
				if (m1.in_bounds(j, k) && m1.los(y, x, j, k)) {
					c_ptr = var.cave[j][k];
					if (c_ptr.fval <= Constants.MAX_OPEN_SPACE && (c_ptr.tptr == 0)) {
						if (typ == 3) {	/* typ == 3 . 50% objects, 50% gold */
							if (m1.randint(100) < 50) {
								real_typ = 1;
							} else {
								real_typ = 256;
							}
						}
						if (real_typ == 1) {
							m3.place_object(j, k, (typ >= 4));
						} else {
							m3.place_gold(j, k);
						}
						mor1.lite_spot(j, k);
						if (m1.test_light(j, k)) {
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
	public boolean delete_object(int y, int x) {
		boolean delete;
		CaveType c_ptr;
		
		c_ptr = var.cave[y][x];
		if (c_ptr.fval == Constants.BLOCKED_FLOOR) {
			c_ptr.fval = Constants.CORR_FLOOR;
		}
		m1.pusht(c_ptr.tptr);
		c_ptr.tptr = 0;
		c_ptr.fm = false;
		mor1.lite_spot(y, x);
		if (m1.test_light(y, x)) {
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
	public long monster_death(int y, int x, long flags) {
		int i, number;
		long dump, res;
		
		if ((flags & Constants.CM_CARRY_OBJ) != 0) {
			i = 1;
		} else {
			i = 0;
		}
		if ((flags & Constants.CM_CARRY_GOLD) != 0) {
			i += 2;
		}
		
		number = 0;
		if ((flags & Constants.CM_60_RANDOM) != 0 && (m1.randint(100) < 60)) {
			number++;
		}
		if ((flags & Constants.CM_90_RANDOM) != 0 && (m1.randint(100) < 90)) {
			number++;
		}
		if ((flags & Constants.CM_1D2_OBJ) != 0) {
			number += m1.randint(2);
		}
		if ((flags & Constants.CM_2D2_OBJ) != 0) {
			number += m1.damroll(2, 2);
		}
		if ((flags & Constants.CM_4D2_OBJ) != 0) {
			number += m1.damroll(4, 2);
		}
		if (number > 0) {
			dump = summon_object(y, x, number, i);
		} else {
			dump = 0;
		}
		
		if ((flags & Constants.CM_WIN) != 0) {
			var.total_winner = true;
			m3.prt_winner();
			io.msg_print("*** CONGRATULATIONS *** You have won the game.");
			io.msg_print("You cannot save this game, but you may retire when ready.");
		}
		
		if (dump != 0) {
			res = 0;
			if ((dump & 255) != 0) {
				res |= Constants.CM_CARRY_OBJ;
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
	public int mon_take_hit(int monptr, int dam) {
		long i;
		int new_exp, new_exp_frac;
		MonsterType m_ptr;
		PlayerMisc p_ptr;
		CreatureType c_ptr;
		int m_take_hit;
		long tmp;
		
		m_ptr = mon.m_list[monptr];
		m_ptr.hp -= dam;
		m_ptr.csleep = 0;
		if (m_ptr.hp < 0) {
			i = monster_death(m_ptr.fy, m_ptr.fx, mon.c_list[m_ptr.mptr].cmove);
			if ((py.py.flags.blind < 1 && m_ptr.ml) || (mon.c_list[m_ptr.mptr].cmove & Constants.CM_WIN) != 0) {
				tmp = (var.c_recall[m_ptr.mptr].r_cmove & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT;
				if (tmp > ((i & Constants.CM_TREASURE) >> Constants.CM_TR_SHIFT)) {
					i = (i & ~Constants.CM_TREASURE) | (tmp << Constants.CM_TR_SHIFT);
				}
				var.c_recall[m_ptr.mptr].r_cmove = (var.c_recall[m_ptr.mptr].r_cmove & ~Constants.CM_TREASURE) | i;
				if (var.c_recall[m_ptr.mptr].r_kills < Constants.MAX_SHORT) {
					var.c_recall[m_ptr.mptr].r_kills++;
				}
			}
			c_ptr = mon.c_list[m_ptr.mptr];
			p_ptr = py.py.misc;
			
			new_exp = (c_ptr.mexp * c_ptr.level) / p_ptr.lev;
			new_exp_frac = (((c_ptr.mexp * c_ptr.level) % p_ptr.lev) * 0x10000 / p_ptr.lev) + p_ptr.exp_frac;
			if (new_exp_frac >= 0x10000) {
				new_exp++;
				p_ptr.exp_frac = new_exp_frac - 0x10000;
			} else {
				p_ptr.exp_frac = new_exp_frac;
			}
			
			p_ptr.exp += new_exp;
			/* can't call prt_experience() here, as that would result in "new level"
			 * message appearing before "monster dies" message */
			m_take_hit = m_ptr.mptr;
			/* in case this is called from within creatures(), this is a
			 * horrible hack, the m_list/creatures() code needs to be
			 * rewritten */
			if (var.hack_monptr < monptr) {
				delete_monster(monptr);
			} else {
				fix1_delete_monster(monptr);
			}
		} else {
			m_take_hit = -1;
		}
		return m_take_hit;
	}
	
	/* Player attacks a (poor, defenseless) creature	-RAK-	*/
	public void py_attack(int y, int x) {
		int k, blows;
		int crptr, monptr, base_tohit;
		IntPointer tot_tohit = new IntPointer();
		String m_name, out_val;
		InvenType i_ptr;
		PlayerMisc p_ptr;
		
		crptr = var.cave[y][x].cptr;
		monptr = mon.m_list[crptr].mptr;
		mon.m_list[crptr].csleep = 0;
		i_ptr = t.inventory[Constants.INVEN_WIELD];
		/* Does the player know what he's fighting?	   */
		if (!mon.m_list[crptr].ml) {
			m_name = "it";
		} else {
			m_name = String.format("the %s", mon.c_list[monptr].name);
		}
		if (i_ptr.tval != Constants.TV_NOTHING) {	/* Proper weapon */
			blows = m3.attack_blows(i_ptr.weight, tot_tohit);
		} else {	/* Bare hands?   */
			blows = 2;
			tot_tohit.value(-3);
		}
		if ((i_ptr.tval >= Constants.TV_SLING_AMMO) && (i_ptr.tval <= Constants.TV_SPIKE)) {
			/* Fix for arrows */
			blows = 1;
		}
		p_ptr = py.py.misc;
		tot_tohit.value(tot_tohit.value() + p_ptr.ptohit);
		/* if creature not lit, make it more difficult to hit */
		if (mon.m_list[crptr].ml) {
			base_tohit = p_ptr.bth;
		} else {
			base_tohit = (p_ptr.bth / 2) - (tot_tohit.value() * (Constants.BTH_PLUS_ADJ - 1)) - (p_ptr.lev * py.class_level_adj[p_ptr.pclass][Constants.CLA_BTH] / 2);
		}
		
		/* Loop for number of blows,	trying to hit the critter.	  */
		do {
			if (mor1.test_hit(base_tohit, p_ptr.lev, tot_tohit.value(), mon.c_list[monptr].ac, Constants.CLA_BTH)) {
				out_val = String.format("You hit %s.", m_name);
				io.msg_print(out_val);
				if (i_ptr.tval != Constants.TV_NOTHING) {
					k = m1.pdamroll(i_ptr.damage);
					k = m3.tot_dam(i_ptr, k, monptr);
					k = m3.critical_blow(i_ptr.weight, tot_tohit.value(), k, Constants.CLA_BTH);
				} else {	/* Bare hands!?  */
					k = m1.damroll(1, 1);
					k = m3.critical_blow(1, 0, k, Constants.CLA_BTH);
				}
				k += p_ptr.ptodam;
				if (k < 0)	k = 0;
				
				if (py.py.flags.confuse_monster) {
					py.py.flags.confuse_monster = false;
					io.msg_print("Your hands stop glowing.");
					if ((mon.c_list[monptr].cdefense & Constants.CD_NO_SLEEP) != 0 || (m1.randint(Constants.MAX_MONS_LEVEL) < mon.c_list[monptr].level)) {
						out_val = String.format("%s is unaffected.", m_name);
					} else {
						out_val = String.format("%s appears confused.", m_name);
						mon.m_list[crptr].confused = true;
					}
					io.msg_print(out_val);
					if (mon.m_list[crptr].ml && m1.randint(4) == 1) {
						var.c_recall[monptr].r_cdefense |= mon.c_list[monptr].cdefense & Constants.CD_NO_SLEEP;
					}
				}
				
				/* See if we done it in.				 */
				if (mon_take_hit(crptr, k) >= 0) {
					out_val = String.format("You have slain %s.", m_name);
					io.msg_print(out_val);
					m3.prt_experience();
					blows = 0;
				}
				
				if ((i_ptr.tval >= Constants.TV_SLING_AMMO) && (i_ptr.tval <= Constants.TV_SPIKE)) {	/* Use missiles up*/
					i_ptr.number--;
					t.inven_weight -= i_ptr.weight;
					py.py.flags.status |= Constants.PY_STR_WGT;
					if (i_ptr.number == 0) {
						t.equip_ctr--;
						mor1.py_bonuses(i_ptr, -1);
						desc.invcopy(i_ptr, Constants.OBJ_NOTHING);
						mor1.calc_bonuses();
					}
				}
			} else {
				out_val = String.format("You miss %s.", m_name);
				io.msg_print(out_val);
			}
			blows--;
		} while (blows >= 1);
	}
	
	/* Moves player from one space to another.		-RAK-	*/
	/* Note: This routine has been pre-declared; see that for argument*/
	public void move_char(int dir, boolean do_pickup) {
		int old_row, old_col, old_find_flag;
		IntPointer y, x;
		int i, j;
		CaveType c_ptr, d_ptr;
		
		if ((py.py.flags.confused > 0)		/* Confused?	     */
				&& (m1.randint(4) > 1)	/* 75% random movement   */
				&& (dir != 5)) {		/* Never random if sitting*/
			dir = m1.randint(9);
			mor2.end_find();
		}
		y = new IntPointer(py.char_row);
		x = new IntPointer(py.char_col);
		if (m3.mmove(dir, y, x)) {	/* Legal move?	      */
			c_ptr = var.cave[y.value()][x.value()];
			/* if there is no creature, or an unlit creature in the walls then... */
			/* disallow attacks against unlit creatures in walls because moving into
			 * a wall is a free turn normally, hence don't give player free turns
			 * attacking each wall in an attempt to locate the invisible creature,
			 * instead force player to tunnel into walls which always takes a turn */
			if ((c_ptr.cptr < 2) || (!mon.m_list[c_ptr.cptr].ml && c_ptr.fval >= Constants.MIN_CLOSED_SPACE)) {
				if (c_ptr.fval <= Constants.MAX_OPEN_SPACE) {	/* Open floor spot	*/
					/* Make final assignments of char co-ords */
					old_row = py.char_row;
					old_col = py.char_col;
					py.char_row = y.value();
					py.char_col = x.value();
					/* Move character record (-1)	       */
					mor1.move_rec(old_row, old_col, py.char_row, py.char_col);
					/* Check for new panel		       */
					if (m1.get_panel(py.char_row, py.char_col, false)) {
						m1.prt_map();
					}
					/* Check to see if he should stop	       */
					if (var.find_flag != 0) {
						mor2.area_affect(dir, py.char_row, py.char_col);
					}
					/* Check to see if he notices something  */
					/* fos may be negative if have good rings of searching */
					if ((py.py.misc.fos <= 1) || (m1.randint(py.py.misc.fos) == 1) || (py.py.flags.status & Constants.PY_SEARCH) != 0) {
						mor2.search(py.char_row, py.char_col, py.py.misc.srh);
					}
					/* A room of light should be lit.	     */
					if (c_ptr.fval == Constants.LIGHT_FLOOR) {
						if (!c_ptr.pl && py.py.flags.blind == 0) {
							mor1.light_room(py.char_row, py.char_col);
						}
					
					/* In doorway of light-room?	       */
					} else if (c_ptr.lr && (py.py.flags.blind < 1)) {
						for (i = (py.char_row - 1); i <= (py.char_row + 1); i++) {
							for (j = (py.char_col - 1); j <= (py.char_col + 1); j++) {
								d_ptr = var.cave[i][j];
								if ((d_ptr.fval == Constants.LIGHT_FLOOR) && (!d_ptr.pl)) {
									mor1.light_room(i, j);
								}
							}
						}
					}
					/* Move the light source		       */
					mor1.move_light(old_row, old_col, py.char_row, py.char_col);
					/* An object is beneath him.	     */
					if (c_ptr.tptr != 0) {
						carry(py.char_row, py.char_col, do_pickup);
						/* if stepped on falling rock trap, and space contains
						 * rubble, then step back into a clear area */
						if (t.t_list[c_ptr.tptr].tval == Constants.TV_RUBBLE) {
							mor1.move_rec(py.char_row, py.char_col, old_row, old_col);
							mor1.move_light(py.char_row, py.char_col, old_row, old_col);
							py.char_row = old_row;
							py.char_col = old_col;
							/* check to see if we have stepped back onto another
							 * trap, if so, set it off */
							c_ptr = var.cave[py.char_row][py.char_col];
							if (c_ptr.tptr != 0) {
								i = t.t_list[c_ptr.tptr].tval;
								if (i == Constants.TV_INVIS_TRAP || i == Constants.TV_VIS_TRAP || i == Constants.TV_STORE_DOOR) {
									hit_trap(py.char_row, py.char_col);
								}
							}
						}
					}
				} else {	  /*Can't move onto floor space*/
					if (var.find_flag == 0 && (c_ptr.tptr != 0)) {
						if (t.t_list[c_ptr.tptr].tval == Constants.TV_RUBBLE) {
							io.msg_print("There is rubble blocking your way.");
						} else if (t.t_list[c_ptr.tptr].tval == Constants.TV_CLOSED_DOOR) {
							io.msg_print("There is a closed door blocking your way.");
						}
					} else {
						mor2.end_find();
					}
					var.free_turn_flag = true;
				}
			} else {	/* Attacking a creature! */
				old_find_flag = var.find_flag;
				mor2.end_find();
				/* if player can see monster, and was in find mode, then nothing */
				if (mon.m_list[c_ptr.cptr].ml && old_find_flag != 0) {
					/* did not do anything this turn */
					var.free_turn_flag = true;
				} else {
					if (py.py.flags.afraid < 1) {	/* Coward?	*/
						py_attack(y.value(), x.value());
					} else {	/* Coward!	*/
						io.msg_print("You are too afraid!");
					}
				}
			}
		}
	}
	
	/* Chests have traps too.				-RAK-	*/
	/* Note: Chest traps are based on the FLAGS value		 */
	public void chest_trap(int y, int x) {
		int i;
		IntPointer j, k;
		InvenType t_ptr;
		
		t_ptr = t.t_list[var.cave[y][x].tptr];
		if ((Constants.CH_LOSE_STR & t_ptr.flags) != 0) {
			io.msg_print("A small needle has pricked you!");
			if (!py.py.flags.sustain_str) {
				m3.dec_stat(Constants.A_STR);
				mor1.take_hit(m1.damroll(1, 4), "a poison needle");
				io.msg_print("You feel weakened!");
			} else {
				io.msg_print("You are unaffected.");
			}
		}
		if ((Constants.CH_POISON & t_ptr.flags) != 0) {
			io.msg_print("A small needle has pricked you!");
			mor1.take_hit(m1.damroll(1, 6), "a poison needle");
			py.py.flags.poisoned += 10 + m1.randint(20);
		}
		if ((Constants.CH_PARALYSED & t_ptr.flags) != 0) {
			io.msg_print("A puff of yellow gas surrounds you!");
			if (py.py.flags.free_act) {
				io.msg_print("You are unaffected.");
			} else {
				io.msg_print("You choke and pass out.");
				py.py.flags.paralysis = 10 + m1.randint(20);
			}
		}
		if ((Constants.CH_SUMMON & t_ptr.flags) != 0) {
			for (i = 0; i < 3; i++) {
				j = new IntPointer(y);
				k = new IntPointer(x);
				m1.summon_monster(j, k, false);
			}
		}
		if ((Constants.CH_EXPLODE & t_ptr.flags) != 0) {
			io.msg_print("There is a sudden explosion!");
			delete_object(y, x);
			mor1.take_hit(m1.damroll(5, 8), "an exploding chest");
		}
	}
	
	/* Opens a closed door or closed chest.		-RAK-	*/
	public void openobject() {
		IntPointer y, x, dir = new IntPointer();
		int i;
		boolean flag, no_object;
		CaveType c_ptr;
		InvenType t_ptr;
		PlayerMisc p_ptr;
		MonsterType m_ptr;
		String m_name, out_val;
		
		y = new IntPointer(py.char_row);
		x = new IntPointer(py.char_col);
		if (mor1.get_dir("", dir)) {
			m3.mmove(dir.value(), y, x);
			c_ptr = var.cave[y.value()][x.value()];
			no_object = false;
			if (c_ptr.cptr > 1 && c_ptr.tptr != 0 && (t.t_list[c_ptr.tptr].tval == Constants.TV_CLOSED_DOOR || t.t_list[c_ptr.tptr].tval == Constants.TV_CHEST)) {
				m_ptr = mon.m_list[c_ptr.cptr];
				if (m_ptr.ml) {
					m_name = String.format("The %s", mon.c_list[m_ptr.mptr].name);
				} else {
					m_name = "Something";
				}
				out_val = String.format("%s is in your way!", m_name);
				io.msg_print(out_val);
			} else if (c_ptr.tptr != 0) {
				/* Closed door		 */
				if (t.t_list[c_ptr.tptr].tval == Constants.TV_CLOSED_DOOR) {
					t_ptr = t.t_list[c_ptr.tptr];
					if (t_ptr.p1 > 0) {	/* It's locked.	*/
						p_ptr = py.py.misc;
						i = p_ptr.disarm + 2 * m3.todis_adj() + m3.stat_adj(Constants.A_INT) + (py.class_level_adj[p_ptr.pclass][Constants.CLA_DISARM] * p_ptr.lev / 3);
						if (py.py.flags.confused > 0) {
							io.msg_print("You are too confused to pick the lock.");
						} else if ((i - t_ptr.p1) > m1.randint(100)) {
							io.msg_print("You have picked the lock.");
							py.py.misc.exp++;
							m3.prt_experience();
							t_ptr.p1 = 0;
						} else {
							io.count_msg_print("You failed to pick the lock.");
						}
					} else if (t_ptr.p1 < 0) {	/* It's stuck	  */
						io.msg_print("It appears to be stuck.");
					}
					
					if (t_ptr.p1 == 0) {
						desc.invcopy(t.t_list[c_ptr.tptr], Constants.OBJ_OPEN_DOOR);
						c_ptr.fval = Constants.CORR_FLOOR;
						mor1.lite_spot(y.value(), x.value());
						var.command_count = 0;
					}
				
				/* Open a closed chest.		     */
				} else if (t.t_list[c_ptr.tptr].tval == Constants.TV_CHEST) {
					p_ptr = py.py.misc;
					i = p_ptr.disarm + 2 * m3.todis_adj() + m3.stat_adj(Constants.A_INT) + (py.class_level_adj[p_ptr.pclass][Constants.CLA_DISARM] * p_ptr.lev / 3);
					t_ptr = t.t_list[c_ptr.tptr];
					flag = false;
					if ((Constants.CH_LOCKED & t_ptr.flags) != 0) {
						if (py.py.flags.confused > 0) {
							io.msg_print("You are too confused to pick the lock.");
						} else if ((i - t_ptr.level) > m1.randint(100)) {
							io.msg_print("You have picked the lock.");
							flag = true;
							py.py.misc.exp += t_ptr.level;
							m3.prt_experience();
						} else {
							io.count_msg_print("You failed to pick the lock.");
						}
					} else {
						flag = true;
					}
					if (flag) {
						t_ptr.flags &= ~Constants.CH_LOCKED;
						t_ptr.name2 = Constants.SN_EMPTY;
						desc.known2(t_ptr);
						t_ptr.cost = 0;
					}
					flag = false;
					/* Was chest still trapped?	 (Snicker)   */
					if ((Constants.CH_LOCKED & t_ptr.flags) == 0) {
						chest_trap(y.value(), x.value());
						if (c_ptr.tptr != 0) {
							flag = true;
						}
					}
					/* Chest treasure is allocated as if a creature   */
					/* had been killed.				   */
					if (flag) {
						/* clear the cursed chest/monster win flag, so that people
						 * can not win by opening a cursed chest */
						t.t_list[c_ptr.tptr].flags &= ~Constants.TR_CURSED;
						monster_death(y.value(), x.value(), t.t_list[c_ptr.tptr].flags);
						t.t_list[c_ptr.tptr].flags = 0;
					}
				} else {
					no_object = true;
				}
			} else {
				no_object = true;
			}
			
			if (no_object) {
				io.msg_print("I do not see anything you can open there.");
				var.free_turn_flag = true;
			}
		}
	}
	
	/* Closes an open door.				-RAK-	*/
	public void closeobject() {
		IntPointer y, x, dir = new IntPointer();
		boolean no_object;
		String out_val, m_name;
		CaveType c_ptr;
		MonsterType m_ptr;
		
		y = new IntPointer(py.char_row);
		x = new IntPointer(py.char_col);
		if (mor1.get_dir("", dir)) {
			m3.mmove(dir.value(), y, x);
			c_ptr = var.cave[y.value()][x.value()];
			no_object = false;
			if (c_ptr.tptr != 0) {
				if (t.t_list[c_ptr.tptr].tval == Constants.TV_OPEN_DOOR) {
					if (c_ptr.cptr == 0) {
						if (t.t_list[c_ptr.tptr].p1 == 0) {
							desc.invcopy(t.t_list[c_ptr.tptr], Constants.OBJ_CLOSED_DOOR);
							c_ptr.fval = Constants.BLOCKED_FLOOR;
							mor1.lite_spot(y.value(), x.value());
						} else {
							io.msg_print("The door appears to be broken.");
						}
					} else {
						m_ptr = mon.m_list[c_ptr.cptr];
						if (m_ptr.ml) {
							m_name = String.format("The %s", mon.c_list[m_ptr.mptr].name);
						} else {
							m_name = "Something";
						}
						out_val = String.format("%s is in your way!", m_name);
						io.msg_print(out_val);
					}
				} else {
					no_object = true;
				}
			} else {
				no_object = true;
			}
			
			if (no_object) {
				io.msg_print("I do not see anything you can close there.");
				var.free_turn_flag = true;
			}
		}
	}
	
	/* Tunneling through real wall: 10, 11, 12		-RAK-	*/
	/* Used by TUNNEL and WALL_TO_MUD				 */
	public boolean twall(int y, int x, int t1, int t2) {
		int i, j;
		CaveType c_ptr;
		boolean res, found;
		
		res = false;
		if (t1 > t2) {
			c_ptr = var.cave[y][x];
			if (c_ptr.lr) {
				/* should become a room space, check to see whether it should be
				 * LIGHT_FLOOR or DARK_FLOOR */
				found = false;
				for (i = y - 1; i <= y + 1; i++) {
					for (j = x - 1; j <= x + 1; j++)
						if (var.cave[i][j].fval <= Constants.MAX_CAVE_ROOM) {
							c_ptr.fval = var.cave[i][j].fval;
							c_ptr.pl = var.cave[i][j].pl;
							found = true;
							break;
						}
				}
				if (!found) {
					c_ptr.fval = Constants.CORR_FLOOR;
					c_ptr.pl = false;
				}
			} else {
				/* should become a corridor space */
				c_ptr.fval = Constants.CORR_FLOOR;
				c_ptr.pl = false;
			}
			c_ptr.fm = false;
			if (m1.panel_contains(y, x)) {
				if ((c_ptr.tl || c_ptr.pl) && c_ptr.tptr != 0) {
					io.msg_print("You have found something!");
				}
			}
			mor1.lite_spot(y, x);
			res = true;
		}
		return res;
	}
}
