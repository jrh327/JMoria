/*
 * Dungeon.java: the main command interpreter, updating player status
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
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.LongPointer;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.SpellType;

public class Dungeon {
	
	/* Moria game module					-RAK-	*/
	/* The code in this section has gone through many revisions, and */
	/* some of it could stand some more hard work.	-RAK-	       */
	
	/* It has had a bit more hard work.			-CJS- */
	
	private Creature creature;
	private Death death;
	private Desc desc;
	private Eat eat;
	private Files files;
	private Help help;
	private IO io;
	private Magic magic;
	private Misc1 m1;
	private Misc2 m2;
	private Misc3 m3;
	private Misc4 m4;
	private Monsters mon;
	private Moria1 mor1;
	private Moria2 mor2;
	private Moria3 mor3;
	private Moria4 mor4;
	private Player py;
	private Potions potions;
	private Prayer prayer;
	private Save save;
	private Scrolls scrolls;
	private Staffs staffs;
	private Spells spells;
	private Store1 store1;
	private Treasure t;
	private Variable var;
	private Wands wands;
	private Wizard wiz;
	
	private static Dungeon instance;
	private Dungeon() { }
	public static Dungeon getInstance() {
		if (instance == null) {
			instance = new Dungeon();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		creature = Creature.getInstance();
		death = Death.getInstance();
		desc = Desc.getInstance();
		eat = Eat.getInstance();
		files = Files.getInstance();
		help = Help.getInstance();
		io = IO.getInstance();
		magic = Magic.getInstance();
		m1 = Misc1.getInstance();
		m2 = Misc2.getInstance();
		m3 = Misc3.getInstance();
		m4 = Misc4.getInstance();
		mon = Monsters.getInstance();
		mor1 = Moria1.getInstance();
		mor2 = Moria2.getInstance();
		mor3 = Moria3.getInstance();
		mor4 = Moria4.getInstance();
		py = Player.getInstance();
		potions = Potions.getInstance();
		prayer = Prayer.getInstance();
		save = Save.getInstance();
		scrolls = Scrolls.getInstance();
		staffs = Staffs.getInstance();
		spells = Spells.getInstance();
		store1 = Store1.getInstance();
		t = Treasure.getInstance();
		var = Variable.getInstance();
		wands = Wands.getInstance();
		wiz = Wizard.getInstance();
	}
	
	public void dungeon() {
		int find_count, i = 0;
		int regen_amount;	    /* Regenerate hp and mana*/
		CharPointer command = new CharPointer();		/* Last command		 */
		PlayerMisc p_ptr;
		InvenType i_ptr;
		PlayerFlags f_ptr;
		
		/* Main procedure for dungeon.			-RAK-	*/
		/* Note: There is a lot of preliminary magic going on here at first*/
		
		/* init pointers. */
		f_ptr = py.py.flags;
		p_ptr = py.py.misc;
		
		/* Check light status for setup	   */
		i_ptr = t.inventory[Constants.INVEN_LIGHT];
		if (i_ptr.p1 > 0) {
			var.player_light = true;
		} else {
			var.player_light = false;
		}
		/* Check for a maximum level		   */
		if (var.dun_level > p_ptr.max_dlv) {
			p_ptr.max_dlv = var.dun_level;
		}
		
		/* Reset flags and initialize variables  */
		var.command_count = 0;
		find_count = 0;
		var.new_level_flag = false;
		var.find_flag = 0;
		var.teleport_flag = false;
		mon.mon_tot_mult = 0;
		var.cave[py.char_row][py.char_col].cptr = 1;
		/* Ensure we display the panel. Used to do this with a global var. -CJS- */
		var.panel_row = var.panel_col = -1;
		/* Light up the area around character	   */
		m4.check_view();
		/* must do this after panel_row/col set to -1, because mor1.search_off() will
		 * call check_view(), and so the panel_* variables must be valid before
		 * search_off() is called */
		if ((py.py.flags.status & Constants.PY_SEARCH) != 0) {
			mor1.search_off();
		}
		/* Light,  but do not move critters	    */
		creature.creatures(false);
		/* Print the depth			   */
		m3.prt_depth();
		
		/* Loop until dead,  or new level		*/
		do {
			/* Increment turn counter			*/
			var.turn++;
			
			/* turn over the store contents every, say, 1000 turns */
			if ((var.dun_level != 0) && ((var.turn % 1000) == 0)) {
				store1.store_maint();
			}
			
			/* Check for creature generation		*/
			if (m1.randint(Constants.MAX_MALLOC_CHANCE) == 1) {
				m1.alloc_monster(1, Constants.MAX_SIGHT, false);
			}
			/* Check light status			       */
			i_ptr = t.inventory[Constants.INVEN_LIGHT];
			if (var.player_light) {
				if (i_ptr.p1 > 0) {
					i_ptr.p1--;
					if (i_ptr.p1 == 0) {
						var.player_light = false;
						io.msg_print("Your light has gone out!");
						mor1.disturb(false, true);
						/* unlight creatures */
						creature.creatures(false);
					} else if ((i_ptr.p1 < 40) && (m1.randint(5) == 1) && (py.py.flags.blind < 1)) {
						mor1.disturb (false, false);
						io.msg_print("Your light is growing faint.");
					}
				} else {
					var.player_light = false;
					mor1.disturb(false, true);
					/* unlight creatures */
					creature.creatures(false);
				}
			} else if (i_ptr.p1 > 0) {
				i_ptr.p1--;
				var.player_light = true;
				mor1.disturb(false, true);
				/* light creatures */
				creature.creatures(false);
			}
			
			/* Update counters and messages			*/
			/* Heroism (must precede anything that can damage player)      */
			if (f_ptr.hero > 0) {
				if ((Constants.PY_HERO & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_HERO;
					mor1.disturb(false, false);
					p_ptr.mhp += 10;
					p_ptr.chp += 10;
					p_ptr.bth += 12;
					p_ptr.bthb+= 12;
					io.msg_print("You feel like a HERO!");
					m3.prt_mhp();
					m3.prt_chp();
				}
				f_ptr.hero--;
				if (f_ptr.hero == 0) {
					f_ptr.status &= ~Constants.PY_HERO;
					mor1.disturb(false, false);
					p_ptr.mhp -= 10;
					if (p_ptr.chp > p_ptr.mhp) {
						p_ptr.chp = p_ptr.mhp;
						p_ptr.chp_frac = 0;
						m3.prt_chp();
					}
					p_ptr.bth -= 12;
					p_ptr.bthb-= 12;
					io.msg_print("The heroism wears off.");
					m3.prt_mhp();
				}
			}
			/* Super Heroism */
			if (f_ptr.shero > 0) {
				if ((Constants.PY_SHERO & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_SHERO;
					mor1.disturb(false, false);
					p_ptr.mhp += 20;
					p_ptr.chp += 20;
					p_ptr.bth += 24;
					p_ptr.bthb+= 24;
					io.msg_print("You feel like a SUPER HERO!");
					m3.prt_mhp();
					m3.prt_chp();
				}
				f_ptr.shero--;
				if (f_ptr.shero == 0) {
					f_ptr.status &= ~Constants.PY_SHERO;
					mor1.disturb(false, false);
					p_ptr.mhp -= 20;
					if (p_ptr.chp > p_ptr.mhp) {
						p_ptr.chp = p_ptr.mhp;
						p_ptr.chp_frac = 0;
						m3.prt_chp();
					}
					p_ptr.bth -= 24;
					p_ptr.bthb-= 24;
					io.msg_print("The super heroism wears off.");
					m3.prt_mhp();
				}
			}
			/* Check food status	       */
			regen_amount = Constants.PLAYER_REGEN_NORMAL;
			if (f_ptr.food < Constants.PLAYER_FOOD_ALERT) {
				if (f_ptr.food < Constants.PLAYER_FOOD_WEAK) {
					if (f_ptr.food < 0) {
						regen_amount = 0;
					} else if (f_ptr.food < Constants.PLAYER_FOOD_FAINT) {
						regen_amount = Constants.PLAYER_REGEN_FAINT;
					} else if (f_ptr.food < Constants.PLAYER_FOOD_WEAK) {
						regen_amount = Constants.PLAYER_REGEN_WEAK;
					}
					if ((Constants.PY_WEAK & f_ptr.status) == 0) {
						f_ptr.status |= Constants.PY_WEAK;
						io.msg_print("You are getting weak from hunger.");
						mor1.disturb(false, false);
						m3.prt_hunger();
					}
					if ((f_ptr.food < Constants.PLAYER_FOOD_FAINT) && (m1.randint(8) == 1)) {
						f_ptr.paralysis += m1.randint(5);
						io.msg_print("You faint from the lack of food.");
						mor1.disturb(true, false);
					}
				} else if ((Constants.PY_HUNGRY & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_HUNGRY;
					io.msg_print("You are getting hungry.");
					mor1.disturb(false, false);
					m3.prt_hunger();
				}
			}
			/* Food consumption	*/
			/* Note: Speeded up characters really burn up the food!  */
			if (f_ptr.speed < 0) {
				f_ptr.food -= f_ptr.speed * f_ptr.speed;
			}
			f_ptr.food -= f_ptr.food_digested;
			if (f_ptr.food < 0) {
				mor1.take_hit(-f_ptr.food / 16, "starvation");   /* -CJS- */
				mor1.disturb(true, false);
			}
			/* Regenerate	       */
			if (f_ptr.regenerate) regen_amount = regen_amount * 3 / 2;
			if ((py.py.flags.status & Constants.PY_SEARCH) != 0 || f_ptr.rest != 0) {
				regen_amount = regen_amount * 2;
			}
			if ((py.py.flags.poisoned < 1) && (p_ptr.chp < p_ptr.mhp)) {
				regenhp(regen_amount);
			}
			if (p_ptr.cmana < p_ptr.mana) {
				regenmana(regen_amount);
			}
			/* Blindness	       */
			if (f_ptr.blind > 0) {
				if ((Constants.PY_BLIND & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_BLIND;
					m1.prt_map();
					m3.prt_blind();
					mor1.disturb(false, true);
					/* unlight creatures */
					creature.creatures(false);
				}
				f_ptr.blind--;
				if (f_ptr.blind == 0) {
					f_ptr.status &= ~Constants.PY_BLIND;
					m3.prt_blind();
					m1.prt_map();
					/* light creatures */
					mor1.disturb(false, true);
					creature.creatures(false);
					io.msg_print("The veil of darkness lifts.");
				}
			}
			/* Confusion	       */
			if (f_ptr.confused > 0) {
				if ((Constants.PY_CONFUSED & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_CONFUSED;
					m3.prt_confused();
				}
				f_ptr.confused--;
				if (f_ptr.confused == 0) {
					f_ptr.status &= ~Constants.PY_CONFUSED;
					m3.prt_confused();
					io.msg_print("You feel less confused now.");
					if (py.py.flags.rest != 0) {
						mor1.rest_off();
					}
				}
			}
			/* Afraid		       */
			if (f_ptr.afraid > 0) {
				if ((Constants.PY_FEAR & f_ptr.status) == 0) {
					if ((f_ptr.shero + f_ptr.hero) > 0) {
						f_ptr.afraid = 0;
					} else {
						f_ptr.status |= Constants.PY_FEAR;
						m3.prt_afraid();
					}
				} else if ((f_ptr.shero + f_ptr.hero) > 0) {
					f_ptr.afraid = 1;
				}
				f_ptr.afraid--;
				if (f_ptr.afraid == 0) {
					f_ptr.status &= ~Constants.PY_FEAR;
					m3.prt_afraid();
					io.msg_print("You feel bolder now.");
					mor1.disturb(false, false);
				}
			}
			/* Poisoned	       */
			if (f_ptr.poisoned > 0) {
				if ((Constants.PY_POISONED & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_POISONED;
					m3.prt_poisoned();
				}
				f_ptr.poisoned--;
				if (f_ptr.poisoned == 0) {
					f_ptr.status &= ~Constants.PY_POISONED;
					m3.prt_poisoned();
					io.msg_print("You feel better.");
					mor1.disturb(false, false);
				} else {
					switch(m3.con_adj())
					{
					case -4: i = 4; break;
					case -3:
					case -2: i = 3; break;
					case -1: i = 2; break;
					case 0:	 i = 1; break;
					case 1: case 2: case 3:
						i = ((var.turn % 2) == 0) ? 1 : 0;
						break;
					case 4: case 5:
						i = ((var.turn % 3) == 0) ? 1 : 0;
						break;
					case 6:
						i = ((var.turn % 4) == 0) ? 1 : 0;
						break;
					}
					mor1.take_hit(i, "poison");
					mor1.disturb(true, false);
				}
			}
			/* Fast		       */
			if (f_ptr.fast > 0) {
				if ((Constants.PY_FAST & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_FAST;
					mor1.change_speed(-1);
					io.msg_print("You feel yourself moving faster.");
					mor1.disturb(false, false);
				}
				f_ptr.fast--;
				if (f_ptr.fast == 0) {
					f_ptr.status &= ~Constants.PY_FAST;
					mor1.change_speed(1);
					io.msg_print("You feel yourself slow down.");
					mor1.disturb(false, false);
				}
			}
			/* Slow		       */
			if (f_ptr.slow > 0) {
				if ((Constants.PY_SLOW & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_SLOW;
					mor1.change_speed(1);
					io.msg_print("You feel yourself moving slower.");
					mor1.disturb(false, false);
				}
				f_ptr.slow--;
				if (f_ptr.slow == 0) {
					f_ptr.status &= ~Constants.PY_SLOW;
					mor1.change_speed(-1);
					io.msg_print("You feel yourself speed up.");
					mor1.disturb(false, false);
				}
			}
			/* Resting is over?      */
			if (f_ptr.rest > 0) {
				f_ptr.rest--;
				if (f_ptr.rest == 0) {	/* Resting over	       */
					mor1.rest_off();
				}
			} else if (f_ptr.rest < 0) {
				/* Rest until reach max mana and max hit points.  */
				f_ptr.rest++;
				if ((p_ptr.chp == p_ptr.mhp && p_ptr.cmana == p_ptr.mana) || f_ptr.rest == 0) {
					mor1.rest_off();
				}
			}
			
			/* Check for interrupts to find or rest. */
			if ((var.command_count > 0 || var.find_flag > 0 || f_ptr.rest != 0)
					//	#if defined(MSDOS) || defined(VMS)
					//&& kbhit()	
					//	#else	
					//&& (check_input(var.find_flag > 0 ? 0 : 10000))
					//	#endif
					) {	
				//io.inkey();	//find some way to check if a key has been pressed while inside this loop
				//mor1.disturb(false, false);
			}
			
			/* Hallucinating?	 (Random characters appear!)*/
			if (f_ptr.image > 0) {
				mor2.end_find();
				f_ptr.image--;
				if (f_ptr.image == 0) {
					m1.prt_map();	 /* Used to draw entire screen! -CJS- */
				}
			}
			/* Paralysis	       */
			if (f_ptr.paralysis > 0) {
				/* when paralysis true, you can not see any movement that occurs */
				f_ptr.paralysis--;
				mor1.disturb(true, false);
			}
			/* Protection from evil counter*/
			if (f_ptr.protevil > 0) {
				f_ptr.protevil--;
				if (f_ptr.protevil == 0) {
					io.msg_print("You no longer feel safe from evil.");
				}
			}
			/* Invulnerability	*/
			if (f_ptr.invuln > 0) {
				if ((Constants.PY_INVULN & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_INVULN;
					mor1.disturb(false, false);
					py.py.misc.pac += 100;
					py.py.misc.dis_ac += 100;
					m3.prt_pac();
					io.msg_print("Your skin turns into steel!");
				}
				f_ptr.invuln--;
				if (f_ptr.invuln == 0) {
					f_ptr.status &= ~Constants.PY_INVULN;
					mor1.disturb(false, false);
					py.py.misc.pac -= 100;
					py.py.misc.dis_ac -= 100;
					m3.prt_pac();
					io.msg_print("Your skin returns to normal.");
				}
			}
			/* Blessed       */
			if (f_ptr.blessed > 0) {
				if ((Constants.PY_BLESSED & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_BLESSED;
					mor1.disturb(false, false);
					p_ptr.bth += 5;
					p_ptr.bthb+= 5;
					p_ptr.pac += 2;
					p_ptr.dis_ac+= 2;
					io.msg_print("You feel righteous!");
					m3.prt_pac();
				}
				f_ptr.blessed--;
				if (f_ptr.blessed == 0) {
					f_ptr.status &= ~Constants.PY_BLESSED;
					mor1.disturb(false, false);
					p_ptr.bth -= 5;
					p_ptr.bthb-= 5;
					p_ptr.pac -= 2;
					p_ptr.dis_ac -= 2;
					io.msg_print("The prayer has expired.");
					m3.prt_pac();
				}
			}
			/* Resist Heat   */
			if (f_ptr.resist_heat > 0) {
				f_ptr.resist_heat--;
				if (f_ptr.resist_heat == 0) {
					io.msg_print("You no longer feel safe from flame.");
				}
			}
			/* Resist Cold   */
			if (f_ptr.resist_cold > 0) {
				f_ptr.resist_cold--;
				if (f_ptr.resist_cold == 0) {
					io.msg_print("You no longer feel safe from cold.");
				}
			}
			/* Detect Invisible      */
			if (f_ptr.detect_inv > 0) {
				if ((Constants.PY_DET_INV & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_DET_INV;
					f_ptr.see_inv = true;
					/* light but don't move creatures */
					creature.creatures(false);
				}
				f_ptr.detect_inv--;
				if (f_ptr.detect_inv == 0) {
					f_ptr.status &= ~Constants.PY_DET_INV;
					/* may still be able to see_inv if wearing magic item */
					mor1.calc_bonuses();
					/* unlight but don't move creatures */
					creature.creatures(false);
				}
			}
			/* Timed infra-vision    */
			if (f_ptr.tim_infra > 0) {
				if ((Constants.PY_TIM_INFRA & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_TIM_INFRA;
					f_ptr.see_infra++;
					/* light but don't move creatures */
					creature.creatures(false);
				}
				f_ptr.tim_infra--;
				if (f_ptr.tim_infra == 0) {
					f_ptr.status &= ~Constants.PY_TIM_INFRA;
					f_ptr.see_infra--;
					/* unlight but don't move creatures */
					creature.creatures(false);
				}
			}
			/* Word-of-Recall  Note: Word-of-Recall is a delayed action	 */
			if (f_ptr.word_recall > 0) {
				if (f_ptr.word_recall == 1) {
					var.new_level_flag = true;
					f_ptr.paralysis++;
					f_ptr.word_recall = 0;
					if (var.dun_level > 0) {
						var.dun_level = 0;
						io.msg_print("You feel yourself yanked upwards!");
					} else if (py.py.misc.max_dlv != 0) {
						var.dun_level = py.py.misc.max_dlv;
						io.msg_print("You feel yourself yanked downwards!");
					}
				} else {
					f_ptr.word_recall--;
				}
			}
			
			/* Random teleportation  */
			if ((py.py.flags.teleport > 0) && (m1.randint(100) == 1)) {
				mor1.disturb(false, false);
				m3.teleport(40);
			}
			
			/* See if we are too weak to handle the weapon or pack.  -CJS- */
			if ((py.py.flags.status & Constants.PY_STR_WGT) != 0) {
				m3.check_strength();
			}
			if ((py.py.flags.status & Constants.PY_STUDY) != 0) {
				m3.prt_study();
			}
			if ((py.py.flags.status & Constants.PY_SPEED) != 0) {
				py.py.flags.status &= ~Constants.PY_SPEED;
				m3.prt_speed();
			}
			if ((py.py.flags.status & Constants.PY_PARALYSED) != 0 && (py.py.flags.paralysis < 1)) {
				m3.prt_state();
				py.py.flags.status &= ~Constants.PY_PARALYSED;
			} else if (py.py.flags.paralysis > 0) {
				m3.prt_state();
				py.py.flags.status |= Constants.PY_PARALYSED;
			} else if (py.py.flags.rest != 0) {
				m3.prt_state();
			}
			
			if ((py.py.flags.status & Constants.PY_ARMOR) != 0) {
				m3.prt_pac();
				py.py.flags.status &= ~Constants.PY_ARMOR;
			}
			if ((py.py.flags.status & Constants.PY_STATS) != 0) {
				for (i = 0; i < 6; i++) {
					if (((Constants.PY_STR << i) & py.py.flags.status) != 0) {
						m3.prt_stat(i);
					}
				}
				py.py.flags.status &= ~Constants.PY_STATS;
			}
			if ((py.py.flags.status & Constants.PY_HP) != 0) {
				m3.prt_mhp();
				m3.prt_chp();
				py.py.flags.status &= ~Constants.PY_HP;
			}
			if ((py.py.flags.status & Constants.PY_MANA) != 0) {
				m3.prt_cmana();
				py.py.flags.status &= ~Constants.PY_MANA;
			}
			
			/* Allow for a slim chance of detect enchantment -CJS- */
			/* for 1st level char, check once every 2160 turns
			 * for 40th level char, check once every 416 turns */
			if (((var.turn & 0xF) == 0) && (f_ptr.confused == 0) && (m1.randint((int)(10 + 750 / (5 + py.py.misc.lev))) == 1)) {
				String tmp_str;
				
				for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
					if (i == t.inven_ctr) {
						i = 22;
					}
					i_ptr = t.inventory[i];
					/* if in inventory, succeed 1 out of 50 times,
					 * if in equipment list, success 1 out of 10 times */
					if ((i_ptr.tval != Constants.TV_NOTHING) && enchanted(i_ptr) && (m1.randint(i < 22 ? 50 : 10) == 1)) {
						//extern char *describe_use();
						
						tmp_str = String.format("There's something about what you are %s...", mor1.describe_use(i));
						mor1.disturb(false, false);
						io.msg_print(tmp_str);
						m4.add_inscribe(i_ptr, Constants.ID_MAGIK);
					}
				}
			}
			
			/* Check the state of the monster list, and delete some monsters if
			 * the monster list is nearly full.  This helps to avoid problems in
			 * creature.c when monsters try to multiply.  Compact_monsters() is
			 * much more likely to succeed if called from here, than if called
			 * from within creature.creatures().  */
			if (Constants.MAX_MALLOC - mon.mfptr < 10) {
				m1.compact_monsters();
			}
			
			if ((py.py.flags.paralysis < 1) && (py.py.flags.rest == 0) && (!var.death)) {
				/* Accept a command and execute it				 */
				do {
					if ((py.py.flags.status & Constants.PY_REPEAT) != 0) {
						m3.prt_state();
					}
					var.default_dir = 0;
					var.free_turn_flag = false;
					
					if (var.find_flag > 0) {
						mor2.find_run();
						find_count--;
						if (find_count == 0) {
							mor2.end_find();
						}
						io.put_qio();
					} else if (var.doing_inven > 0) {
						mor1.inven_command(var.doing_inven);
					} else {
						/* move the cursor to the players character */
						io.move_cursor_relative(py.char_row, py.char_col);
						if (var.command_count > 0) {
							var.msg_flag = 0;
							var.default_dir = 1;
						} else {
							var.msg_flag = 0;
							command.value(io.inkey());
							i = 0;
							/* Get a count for a command. */
							if ((var.rogue_like_commands.value() && command.value() >= '0' && command.value() <= '9') || (!var.rogue_like_commands.value() && command.value() == '#')) {
								String tmp;
								
								io.prt("Repeat count:", 0, 0);
								if (command.value() == '#') {
									command.value('0');
								}
								i = 0;
								while (true) {
									if (command.value() == Constants.DELETE || command.value() == (Constants.CTRL & 'H')) {
										i = i / 10;
										tmp = String.format("%d", i);
										if (tmp.length() > 8) tmp = tmp.substring(0, 8);
										io.prt(tmp, 0, 14);
									} else if (command.value() >= '0' && command.value() <= '9') {
										if (i > 99) {
											io.bell();
										} else {
											i = i * 10 + command.value() - '0';
											tmp = String.format("%d", i);
											io.prt(tmp, 0, 14);
										}
									} else {
										break;
									}
									command.value(io.inkey());
								}
								if (i == 0) {
									i = 99;
									tmp = String.format("%d", i);
									io.prt(tmp, 0, 14);
								}
								/* a special hack to allow numbers as commands */
								if (command.value() == ' ') {
									io.prt("Command:", 0, 20);
									command.value(io.inkey());
								}
							}
							/* Another way of typing control codes -CJS- */
							if (command.value() == '^') {
								if (var.command_count > 0) {
									m3.prt_state();
								}
								if (io.get_com("Control-", command)) {
									if (command.value() >= 'A' && command.value() <= 'Z') {
										command.value((char)(command.value() - 'A' - 1));
									} else if (command.value() >= 'a' && command.value() <= 'z') {
										command.value((char)(command.value() - 'a' - 1));
									} else {
										io.msg_print("Type ^ <letter> for a control char");
										command.value(' ');
									}
								} else {
									command.value(' ');
								}
							}
							/* move cursor to player char again, in case it moved */
							io.move_cursor_relative(py.char_row, py.char_col);
							/* Commands are always converted to rogue form. -CJS- */
							if (!var.rogue_like_commands.value()) {
								command.value(original_commands(command.value()));
							}
							if (i > 0) {
								if (!valid_countcommand(command.value())) {
									var.free_turn_flag = true;
									io.msg_print("Invalid command with a count.");
									command.value(' ');
								} else {
									var.command_count = i;
									m3.prt_state();
								}
							}
						}
						/* Flash the message line. */
						io.erase_line(Constants.MSG_LINE, 0);
						io.move_cursor_relative(py.char_row, py.char_col);
						io.put_qio();
						
						do_command(command.value());
						/* Find is counted differently, as the command changes. */
						if (var.find_flag > 0) {
							find_count = var.command_count - 1;
							var.command_count = 0;
						} else if (var.free_turn_flag) {
							var.command_count = 0;
						} else if (var.command_count > 0) {
							var.command_count--;
						}
					}
					/* End of commands				     */
				} while (var.free_turn_flag && !var.new_level_flag && var.eof_flag == 0);
			} else {
				/* if paralyzed, resting, or dead, flush output */
				/* but first move the cursor onto the player, for aesthetics */
				io.move_cursor_relative(py.char_row, py.char_col);
				io.put_qio ();
			}
			
			/* Teleport?		       */
			if (var.teleport_flag)	m3.teleport(100);
			/* Move the creatures	       */
			if (!var.new_level_flag)	creature.creatures(true);
			/* Exit when new_level_flag is set   */
		} while (!var.new_level_flag && var.eof_flag == 0);
	}
	
	public char original_commands(char com_val) {
		IntPointer dir_val = new IntPointer();
		
		switch(com_val)
		{
		case (Constants.CTRL & 'K'):	/*^K = exit    */
			com_val = 'Q';
			break;
		case (Constants.CTRL & 'J'):
		case (Constants.CTRL & 'M'):
			com_val = '+';
			break;
		case (Constants.CTRL & 'P'):	/*^P = repeat  */
		case (Constants.CTRL & 'W'):	/*^W = password*/
		case (Constants.CTRL & 'X'):	/*^X = save    */
		case (Constants.CTRL & 'V'):	/*^V = view license */
		case ' ':
		case '!':
		case '$':
			break;
		case '.':
			if (mor1.get_dir("", dir_val)) {
				switch (dir_val.value())
				{
				case 1:    com_val = 'B';    break;
				case 2:    com_val = 'J';    break;
				case 3:    com_val = 'N';    break;
				case 4:    com_val = 'H';    break;
				case 6:    com_val = 'L';    break;
				case 7:    com_val = 'Y';    break;
				case 8:    com_val = 'K';    break;
				case 9:    com_val = 'U';    break;
				default:   com_val = ' ';    break;
				}
			} else {
				com_val = ' ';
			}
			break;
		case '/':
		case '<':
		case '>':
		case '-':
		case '=':
		case '{':
		case '?':
		case 'A':
			break;
		case '1':
			com_val = 'b';
			break;
		case '2':
			com_val = 'j';
			break;
		case '3':
			com_val = 'n';
			break;
		case '4':
			com_val = 'h';
			break;
		case '5':	/* Rest one turn */
			com_val = '.';
			break;
		case '6':
			com_val = 'l';
			break;
		case '7':
			com_val = 'y';
			break;
		case '8':
			com_val = 'k';
			break;
		case '9':
			com_val = 'u';
			break;
		case 'B':
			com_val = 'f';
			break;
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
			break;
		case 'L':
			com_val = 'W';
			break;
		case 'M':
			break;
		case 'R':
			break;
		case 'S':
			com_val = '#';
			break;
		case 'T':
			if (mor1.get_dir("", dir_val)) {
				switch (dir_val.value())
				{
				case 1:    com_val = (Constants.CTRL & 'B');    break;
				case 2:    com_val = (Constants.CTRL & 'J');    break;
				case 3:    com_val = (Constants.CTRL & 'N');    break;
				case 4:    com_val = (Constants.CTRL & 'H');    break;
				case 6:    com_val = (Constants.CTRL & 'L');    break;
				case 7:    com_val = (Constants.CTRL & 'Y');    break;
				case 8:    com_val = (Constants.CTRL & 'K');    break;
				case 9:    com_val = (Constants.CTRL & 'U');    break;
				default:   com_val = ' ';	     break;
				}
			} else {
				com_val = ' ';
			}
			break;
		case 'V':
			break;
		case 'a':
			com_val = 'z';
			break;
		case 'b':
			com_val = 'P';
			break;
		case 'c':
		case 'd':
		case 'e':
			break;
		case 'f':
			com_val = 't';
			break;
		case 'h':
			com_val = '?';
			break;
		case 'i':
			break;
		case 'j':
			com_val = 'S';
			break;
		case 'l':
			com_val = 'x';
			break;
		case 'm':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
			break;
		case 't':
			com_val = 'T';
			break;
		case 'u':
			com_val = 'Z';
			break;
		case 'v':
		case 'w':
			break;
		case 'x':
			com_val = 'X';
			break;
			
			/* wizard mode commands follow */
		case (Constants.CTRL & 'A'): /*^A = cure all */
			break;
		case (Constants.CTRL & 'B'):	/*^B = objects */
			com_val = (Constants.CTRL & 'O');
			break;
		case (Constants.CTRL & 'D'):	/*^D = up/down */
			break;
		case (Constants.CTRL & 'H'):	/*^H = wizhelp */
			com_val = '\\';
			break;
		case (Constants.CTRL & 'I'):	/*^I = identify*/
			break;
		case (Constants.CTRL & 'L'):	/*^L = wizlight*/
			com_val = '*';
			break;
		case ':':
		case (Constants.CTRL & 'T'):	/*^T = teleport*/
		case (Constants.CTRL & 'E'):	/*^E = wizchar */
		case (Constants.CTRL & 'F'):	/*^F = genocide*/
		case (Constants.CTRL & 'G'):	/*^G = treasure*/
		case '@':
		case '+':
			break;
		case (Constants.CTRL & 'U'):	/*^U = summon  */
			com_val = '&';
			break;
		default:
			com_val = '~';  /* Anything illegal. */
			break;
		}
		return com_val;
	}
	
	public void do_command(char com_val) {
		IntPointer dir_val = new IntPointer();
		boolean do_pickup;
		IntPointer y, x;
		int i, j;
		String out_val, tmp_str;
		PlayerFlags f_ptr;
		
		/* hack for move without pickup.  Map '-' to a movement command. */
		if (com_val == '-') {
			do_pickup = false;
			i = var.command_count;
			if (mor1.get_dir("", dir_val)) {
				var.command_count = i;
				switch (dir_val.value())
				{
				case 1:    com_val = 'b';	 break;
				case 2:    com_val = 'j';	 break;
				case 3:    com_val = 'n';	 break;
				case 4:    com_val = 'h';	 break;
				case 6:    com_val = 'l';	 break;
				case 7:    com_val = 'y';	 break;
				case 8:    com_val = 'k';	 break;
				case 9:    com_val = 'u';	 break;
				default:   com_val = '~';	 break;
				}
			} else {
				com_val = ' ';
			}
		} else {
			do_pickup = true;
		}
		
		switch(com_val)
		{
		case 'Q':	/* (Q)uit		(^K)ill */
			io.flush();
			if (io.get_check("Do you really want to quit?")) {
				var.new_level_flag = true;
				var.death = true;
				var.died_from = "Quitting";
			}
			var.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'P'):	/* (^P)revious message. */
			if (var.command_count > 0) {
				i = var.command_count;
				if (i > Constants.MAX_SAVE_MSG) {
					i = Constants.MAX_SAVE_MSG;
				}
				var.command_count = 0;
			} else if (var.last_command != (Constants.CTRL & 'P')) {
				i = 1;
			} else {
				i = Constants.MAX_SAVE_MSG;
			}
			j = var.last_msg;
			if (i > 1) {
				io.save_screen();
				x = new IntPointer(i);
				while (i > 0) {
					i--;
					io.prt(var.old_msg[j], i, 0);
					if (j == 0) {
						j = Constants.MAX_SAVE_MSG - 1;
					} else {
						j--;
					}
				}
				io.erase_line(x.value(), 0);
				io.pause_line(x.value());
				io.restore_screen();
			} else {
				/* Distinguish real and recovered messages with a '>'. -CJS- */
				io.put_buffer(">", 0, 0);
				io.prt(var.old_msg[j], 0, 1);
			}
			var.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'V'):	/* (^V)iew license */
			files.helpfile(Config.MORIA_GPL);
			var.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'W'):	/* (^W)izard mode */
			if (var.wizard) {
				var.wizard = false;
				io.msg_print("Wizard mode off.");
			} else if (m3.enter_wiz_mode()) {
				io.msg_print("Wizard mode on.");
			}
			m3.prt_winner();
			var.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'X'):	/* e(^X)it and save */
			if (var.total_winner) {
				io.msg_print("You are a Total Winner,  your character must be retired.");
				if (var.rogue_like_commands.value()) {
					io.msg_print("Use 'Q' to when you are ready to quit.");
				} else {
					io.msg_print ("Use <Control>-K when you are ready to quit.");
				}
			} else {
				var.died_from = "(saved)";
				io.msg_print("Saving game...");
				if (save.save_char()) {
					death.exit_game();
				}
				var.died_from = "(alive and well)";
			}
			var.free_turn_flag = true;
			break;
		case '=':		/* (=) set options */
			io.save_screen();
			m2.set_options();
			io.restore_screen();
			var.free_turn_flag = true;
			break;
		case '{':		/* ({) inscribe an object    */
			m4.scribe_object();
			var.free_turn_flag = true;
			break;
		case '!':		/* (!) escape to the shell */
		case '$':
			io.shell_out();
			var.free_turn_flag = true;
			break;
		case Constants.ESCAPE:	/* (ESC)   do nothing. */
		case ' ':		/* (space) do nothing. */
			var.free_turn_flag = true;
			break;
		case 'b':		/* (b) down, left	(1) */
			mor3.move_char(1, do_pickup);
			break;
		case Constants.KEY_DOWN:
		case 'j':		/* (j) down		(2) */
			mor3.move_char(2, do_pickup);
			break;
		case 'n':		/* (n) down, right	(3) */
			mor3.move_char(3, do_pickup);
			break;
		case Constants.KEY_LEFT:
		case 'h':		/* (h) left		(4) */
			mor3.move_char(4, do_pickup);
			break;
		case Constants.KEY_RIGHT:
		case 'l':		/* (l) right		(6) */
			mor3.move_char(6, do_pickup);
			break;
		case 'y':		/* (y) up, left		(7) */
			mor3.move_char(7, do_pickup);
			break;
		case Constants.KEY_UP:
		case 'k':		/* (k) up		(8) */
			mor3.move_char(8, do_pickup);
			break;
		case 'u':		/* (u) up, right	(9) */
			mor3.move_char(9, do_pickup);
			break;
		case 'B':		/* (B) run down, left	(. 1) */
			mor2.find_init(1);
			break;
		case 'J':		/* (J) run down		(. 2) */
			mor2.find_init(2);
			break;
		case 'N':		/* (N) run down, right	(. 3) */
			mor2.find_init(3);
			break;
		case 'H':		/* (H) run left		(. 4) */
			mor2.find_init(4);
			break;
		case 'L':		/* (L) run right	(. 6) */
			mor2.find_init(6);
			break;
		case 'Y':		/* (Y) run up, left	(. 7) */
			mor2.find_init(7);
			break;
		case 'K':		/* (K) run up		(. 8) */
			mor2.find_init(8);
			break;
		case 'U':		/* (U) run up, right	(. 9) */
			mor2.find_init(9);
			break;
		case '/':		/* (/) identify a symbol */
			help.ident_char();
			var.free_turn_flag = true;
			break;
		case '.':		/* (.) stay in one place (5) */
			mor3.move_char(5, do_pickup);
			if (var.command_count > 1) {
				var.command_count--;
				mor1.rest();
			}
			break;
		case '<':		/* (<) go up a staircase */
			go_up();
			break;
		case '>':		/* (>) go down a staircase */
			go_down();
			break;
		case '?':		/* (?) help with commands */
			if (var.rogue_like_commands.value()) {
				files.helpfile(Config.MORIA_HELP);
			} else {
				files.helpfile(Config.MORIA_ORIG_HELP);
			}
			var.free_turn_flag = true;
			break;
		case 'f':		/* (f)orce		(B)ash */
			mor4.bash();
			break;
		case 'C':		/* (C)haracter description */
			io.save_screen();
			m3.change_name();
			io.restore_screen();
			var.free_turn_flag = true;
			break;
		case 'D':		/* (D)isarm trap */
			mor4.disarm_trap();
			break;
		case 'E':		/* (E)at food */
			eat.eat();
			break;
		case 'F':		/* (F)ill lamp */
			refill_lamp();
			break;
		case 'G':		/* (G)ain magic spells */
			m3.gain_spells();
			break;
		case 'V':		/* (V)iew scores */
			boolean b;
			if (var.last_command != 'V') {
				b = true;
			} else {
				b = false;
			}
			io.save_screen();
			death.display_scores(b);
			io.restore_screen();
			var.free_turn_flag = true;
			break;
		case 'W':		/* (W)here are we on the map	(L)ocate on map */
			if ((py.py.flags.blind > 0) || mor1.no_light()) {
				io.msg_print("You can't see your map.");
			} else {
				int cy, cx, p_y, p_x;
				
				y = new IntPointer(py.char_row);
				x = new IntPointer(py.char_col);
				if (m1.get_panel(y.value(), x.value(), true)) {
					m1.prt_map();
				}
				cy = var.panel_row;
				cx = var.panel_col;
				for(;;) {
					p_y = var.panel_row;
					p_x = var.panel_col;
					if (p_y == cy && p_x == cx) {
						tmp_str = "";
					} else {
						tmp_str = String.format("%s%s of", p_y < cy ? " North" : p_y > cy ? " South" : "", p_x < cx ? " West" : p_x > cx ? " East" : "");
					}
					out_val = String.format("Map sector [%d,%d], which is%s your sector. Look which direction?", p_y, p_x, tmp_str);
					if (!mor1.get_dir(out_val, dir_val)) {
						break;
					}
					/*								      -CJS-
					// Should really use the move function, but what the hell. This
					// is nicer, as it moves exactly to the same place in another
					// section. The direction calculation is not intuitive. Sorry.
					 */
					for(;;){
						x.value(x.value() + ((dir_val.value() - 1) % 3 - 1) * Constants.SCREEN_WIDTH / 2);
						y.value(y.value() - ((dir_val.value() - 1) / 3 - 1) * Constants.SCREEN_HEIGHT / 2);
						if (x.value() < 0 || y.value() < 0 || x.value() >= var.cur_width || y.value() >= var.cur_width) {
							io.msg_print("You've gone past the end of your map.");
							x.value(x.value() - ((dir_val.value() - 1) % 3 - 1) * Constants.SCREEN_WIDTH / 2);
							y.value(y.value() + ((dir_val.value() - 1) / 3 - 1) * Constants.SCREEN_HEIGHT / 2);
							break;
						}
						if (m1.get_panel(y.value(), x.value(), true)) {
							m1.prt_map();
							break;
						}
					}
				}
				/* Move to a new panel - but only if really necessary. */
				if (m1.get_panel(py.char_row, py.char_col, false)) {
					m1.prt_map();
				}
			}
			var.free_turn_flag = true;
			break;
		case 'R':		/* (R)est a while */
			mor1.rest();
			break;
		case '#':		/* (#) search toggle	(S)earch toggle */
			if ((py.py.flags.status & Constants.PY_SEARCH) != 0) {
				mor1.search_off();
			} else {
				mor1.search_on();
			}
			var.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'B'):		/* (^B) tunnel down left	(T 1) */
			mor4.tunnel(1);
			break;
		case (Constants.CTRL & 'M'):		/* cr must be treated same as lf. */
		case (Constants.CTRL & 'J'):		/* (^J) tunnel down		(T 2) */
			mor4.tunnel(2);
			break;
		case (Constants.CTRL & 'N'):		/* (^N) tunnel down right	(T 3) */
			mor4.tunnel(3);
			break;
		case (Constants.CTRL & 'H'):		/* (^H) tunnel left		(T 4) */
			mor4.tunnel(4);
			break;
		case (Constants.CTRL & 'L'):		/* (^L) tunnel right		(T 6) */
			mor4.tunnel(6);
			break;
		case (Constants.CTRL & 'Y'):		/* (^Y) tunnel up left		(T 7) */
			mor4.tunnel(7);
			break;
		case (Constants.CTRL & 'K'):		/* (^K) tunnel up		(T 8) */
			mor4.tunnel(8);
			break;
		case (Constants.CTRL & 'U'):		/* (^U) tunnel up right		(T 9) */
			mor4.tunnel(9);
			break;
		case 'z':		/* (z)ap a wand		(a)im a wand */
			wands.aim();
			break;
		case 'M':
			io.screen_map();
			var.free_turn_flag = true;
			break;
		case 'P':		/* (P)eruse a book	(B)rowse in a book */
			examine_book();
			var.free_turn_flag = true;
			break;
		case 'c':		/* (c)lose an object */
			mor3.closeobject();
			break;
		case 'd':		/* (d)rop something */
			mor1.inven_command('d');
			break;
		case 'e':		/* (e)quipment list */
			mor1.inven_command('e');
			break;
		case 't':		/* (t)hrow something	(f)ire something */
			mor4.throw_object();
			break;
		case 'i':		/* (i)nventory list */
			mor1.inven_command('i');
			break;
		case 'S':		/* (S)pike a door	(j)am a door */
			jamdoor();
			break;
		case 'x':		/* e(x)amine surrounds	(l)ook about */
			mor4.look();
			var.free_turn_flag = true;
			break;
		case 'm':		/* (m)agic spells */
			magic.cast();
			break;
		case 'o':		/* (o)pen something */
			mor3.openobject();
			break;
		case 'p':		/* (p)ray */
			prayer.pray();
			break;
		case 'q':		/* (q)uaff */
			potions.quaff();
			break;
		case 'r':		/* (r)ead */
			scrolls.read_scroll();
			break;
		case 's':		/* (s)earch for a turn */
			mor2.search(py.char_row, py.char_col, py.py.misc.srh);
			break;
		case 'T':		/* (T)ake off something	(t)ake off */
			mor1.inven_command('t');
			break;
		case 'Z':		/* (Z)ap a staff	(u)se a staff */
			staffs.use();
			break;
		case 'v':		/* (v)ersion of game */
			files.helpfile(Config.MORIA_VER);
			var.free_turn_flag = true;
			break;
		case 'w':		/* (w)ear or wield */
			mor1.inven_command('w');
			break;
		case 'X':		/* e(X)change weapons	e(x)change */
			mor1.inven_command('x');
			break;
		default:
			if (var.wizard) {
				var.free_turn_flag = true; /* Wizard commands are free moves*/
				switch(com_val)
				{
				case (Constants.CTRL & 'A'):	/*^A = Cure all*/
					spells.remove_curse();
					spells.cure_blindness();
					spells.cure_confusion();
					spells.cure_poison();
					spells.remove_fear();
					m3.res_stat(Constants.A_STR);
					m3.res_stat(Constants.A_INT);
					m3.res_stat(Constants.A_WIS);
					m3.res_stat(Constants.A_CON);
					m3.res_stat(Constants.A_DEX);
					m3.res_stat(Constants.A_CHR);
					f_ptr = py.py.flags;
					if (f_ptr.slow > 1) {
						f_ptr.slow = 1;
					}
					if (f_ptr.image > 1) {
						f_ptr.image = 1;
					}
					break;
				case (Constants.CTRL & 'E'):	/*^E = wizchar */
					wiz.change_character();
					io.erase_line(Constants.MSG_LINE, 0);
				break;
				case (Constants.CTRL & 'F'):	/*^F = genocide*/
					spells.mass_genocide();
					break;
				case (Constants.CTRL & 'G'):	/*^G = treasure*/
					if (var.command_count > 0) {
						i = var.command_count;
						var.command_count = 0;
					} else {
						i = 1;
					}
					m3.random_object(py.char_row, py.char_col, i);
					m1.prt_map();
					break;
				case (Constants.CTRL & 'D'):	/*^D = up/down */
					if (var.command_count > 0) {
						if (var.command_count > 99) {
							i = 0;
						} else {
							i = var.command_count;
						}
						var.command_count = 0;
					} else {
						io.prt("Go to which level (0-99) ? ", 0, 0);
						i = -1;
						if ((tmp_str = io.get_string(0, 27, 10)).length() > 0) {
							try {
								i = Integer.parseInt(tmp_str);
							} catch (NumberFormatException e) {
								System.err.println("tmp_str cannot be converted to an integer in Dungeon.do_command()");
								i = 0;
							}
						}
					}
					if (i > -1) {
						var.dun_level = i;
						if (var.dun_level > 99) {
							var.dun_level = 99;
						}
						var.new_level_flag = true;
					} else {
						io.erase_line(Constants.MSG_LINE, 0);
					}
					break;
				case (Constants.CTRL & 'O'):	/*^O = objects */
					files.print_objects();
					break;
				case '\\': /* \ wizard help */
					if (var.rogue_like_commands.value()) {
						files.helpfile(Config.MORIA_WIZ_HELP);
					} else {
						files.helpfile(Config.MORIA_OWIZ_HELP);
					}
					break;
				case (Constants.CTRL & 'I'):	/*^I = identify*/
					spells.ident_spell();
					break;
				case '*':
					wiz.wizard_light();
					break;
				case ':':
					spells.map_area();
					break;
				case (Constants.CTRL & 'T'):	/*^T = teleport*/
					m3.teleport(100);
					break;
				case '+':
					if (var.command_count > 0) {
						py.py.misc.exp = var.command_count;
						var.command_count = 0;
					} else if (py.py.misc.exp == 0) {
						py.py.misc.exp = 1;
					} else {
						py.py.misc.exp = py.py.misc.exp * 2;
					}
					m3.prt_experience();
					break;
				case '&':	/*& = summon  */
					y = new IntPointer(py.char_row);
					x = new IntPointer(py.char_col);
					m1.summon_monster(y, x, true);
					creature.creatures(false);
					break;
				case '@':
					wiz.wizard_create();
					break;
				default:
					if (var.rogue_like_commands.value()) {
						io.prt("Type '?' or '\\' for help.", 0, 0);
					} else {
						io.prt("Type '?' or ^H for help.", 0, 0);
					}
				}
			} else {
				io.prt("Type '?' for help.", 0, 0);
				var.free_turn_flag = true;
			}
		}
		var.last_command = com_val;
	}
	
	/* Check whether this command will accept a count.     -CJS-  */
	static boolean valid_countcommand(char c) {
		switch(c)
		{
		case 'Q':
		case (Constants.CTRL & 'W'):
		case (Constants.CTRL & 'X'):
		case '=':
		case '{':
		case '/':
		case '<':
		case '>':
		case '?':
		case 'C':
		case 'E':
		case 'F':
		case 'G':
		case 'V':
		case '#':
		case 'z':
		case 'P':
		case 'c':
		case 'd':
		case 'e':
		case 't':
		case 'i':
		case 'x':
		case 'm':
		case 'p':
		case 'q':
		case 'r':
		case 'T':
		case 'Z':
		case 'v':
		case 'w':
		case 'W':
		case 'X':
		case (Constants.CTRL & 'A'):
		case '\\':
		case (Constants.CTRL & 'I'):
		case '*':
		case ':':
		case (Constants.CTRL & 'T'):
		case (Constants.CTRL & 'E'):
		case (Constants.CTRL & 'F'):
		case (Constants.CTRL & 'S'):
		case (Constants.CTRL & 'Q'):
			return false;
		case (Constants.CTRL & 'P'):
		case Constants.ESCAPE:
		case ' ':
		case '-':
		case 'b':
		case 'f':
		case 'j':
		case 'n':
		case 'h':
		case 'l':
		case 'y':
		case 'k':
		case 'u':
		case '.':
		case 'B':
		case 'J':
		case 'N':
		case 'H':
		case 'L':
		case 'Y':
		case 'K':
		case 'U':
		case 'D':
		case 'R':
		case (Constants.CTRL & 'Y'):
		case (Constants.CTRL & 'K'):
		case (Constants.CTRL & 'U'):
		case (Constants.CTRL & 'L'):
		case (Constants.CTRL & 'N'):
		case (Constants.CTRL & 'J'):
		case (Constants.CTRL & 'B'):
		case (Constants.CTRL & 'H'):
		case 'S':
		case 'o':
		case 's':
		case (Constants.CTRL & 'D'):
		case (Constants.CTRL & 'G'):
		case '+':
			return true;
		default:
			return false;
		}
	}
	
	/* Regenerate hit points				-RAK-	*/
	public void regenhp(int percent) {
		PlayerMisc p_ptr;
		int new_chp, new_chp_frac;
		int old_chp;
		
		p_ptr = py.py.misc;
		old_chp = p_ptr.chp;
		new_chp = p_ptr.mhp * percent + Constants.PLAYER_REGEN_HPBASE;
		p_ptr.chp += new_chp >> 16;	/* div 65536 */
		/* check for overflow */
		if (p_ptr.chp < 0 && old_chp > 0) {
			p_ptr.chp = Constants.MAX_SHORT;
		}
		new_chp_frac = (new_chp & 0xFFFF) + p_ptr.chp_frac; /* mod 65536 */
		if (new_chp_frac >= 0x10000) {
			p_ptr.chp_frac = new_chp_frac - 0x10000;
			p_ptr.chp++;
		} else {
			p_ptr.chp_frac = new_chp_frac;
		}
		
		/* must set frac to zero even if equal */
		if (p_ptr.chp >= p_ptr.mhp) {
			p_ptr.chp = p_ptr.mhp;
			p_ptr.chp_frac = 0;
		}
		if (old_chp != p_ptr.chp) {
			m3.prt_chp();
		}
	}
	
	/* Regenerate mana points				-RAK-	*/
	public void regenmana(int percent) {
		PlayerMisc p_ptr;
		int new_mana, new_mana_frac;
		int old_cmana;
		
		p_ptr = py.py.misc;
		old_cmana = p_ptr.cmana;
		new_mana = p_ptr.mana * percent + Constants.PLAYER_REGEN_MNBASE;
		p_ptr.cmana += new_mana >> 16;  /* div 65536 */
		/* check for overflow */
		if (p_ptr.cmana < 0 && old_cmana > 0) {
			p_ptr.cmana = Constants.MAX_SHORT;
		}
		new_mana_frac = (new_mana & 0xFFFF) + p_ptr.cmana_frac; /* mod 65536 */
		if (new_mana_frac >= 0x10000) {
			p_ptr.cmana_frac = new_mana_frac - 0x10000;
			p_ptr.cmana++;
		} else {
			p_ptr.cmana_frac = new_mana_frac;
		}
		
		/* must set frac to zero even if equal */
		if (p_ptr.cmana >= p_ptr.mana) {
			p_ptr.cmana = p_ptr.mana;
			p_ptr.cmana_frac = 0;
		}
		if (old_cmana != p_ptr.cmana) {
			m3.prt_cmana();
		}
	}
	
	/* Is an item an enchanted weapon or armor and we don't know?  -CJS- */
	/* only returns true if it is a good enchantment */
	public boolean enchanted(InvenType t_ptr) {
		if (t_ptr.tval < Constants.TV_MIN_ENCHANT || t_ptr.tval > Constants.TV_MAX_ENCHANT || (t_ptr.flags & Constants.TR_CURSED) != 0) {
			return false;
		}
		if (desc.known2_p(t_ptr)) {
			return false;
		}
		if ((t_ptr.ident & Constants.ID_MAGIK) != 0) {
			return false;
		}
		if (t_ptr.tohit > 0 || t_ptr.todam > 0 || t_ptr.toac > 0) {
			return true;
		}
		if ((0x4000107fL & t_ptr.flags) != 0 && t_ptr.p1 > 0) {
			return true;
		}
		if ((0x07ffe980L & t_ptr.flags) != 0) {
			return true;
		}
		
		return false;
	}
	
	/* Examine a Book					-RAK-	*/
	public void examine_book() {
		LongPointer j;
		IntPointer i = new IntPointer(), k = new IntPointer();
		IntPointer item_val = new IntPointer();
		boolean flag;
		int[] spell_index = new int[31];
		InvenType i_ptr;
		SpellType s_ptr;
		
		if (!m3.find_range(Constants.TV_MAGIC_BOOK, Constants.TV_PRAYER_BOOK, i, k)) {
			io.msg_print("You are not carrying any books.");
		} else if (py.py.flags.blind > 0) {
			io.msg_print("You can't see to read your spell book!");
		} else if (mor1.no_light()) {
			io.msg_print("You have no light to read by.");
		} else if (py.py.flags.confused > 0) {
			io.msg_print("You are too confused.");
		} else if (mor1.get_item(item_val, "Which Book?", i.value(), k.value(), "", "")) {
			flag = true;
			i_ptr = t.inventory[item_val.value()];
			if (py.Class[py.py.misc.pclass].spell == Constants.MAGE) {
				if (i_ptr.tval != Constants.TV_MAGIC_BOOK) {
					flag = false;
				}
			} else if (py.Class[py.py.misc.pclass].spell == Constants.PRIEST) {
				if (i_ptr.tval != Constants.TV_PRAYER_BOOK) {
					flag = false;
				}
			} else {
				flag = false;
			}
			
			if (!flag) {
				io.msg_print("You do not understand the language.");
			} else {
				i.value(0);
				j = new LongPointer(t.inventory[item_val.value()].flags);
				while (j.value() != 0) {
					k.value(m1.bit_pos(j));
					s_ptr = py.magic_spell[py.py.misc.pclass - 1][k.value()];
					if (s_ptr.slevel < 99) {
						spell_index[i.value()] = k.value();
						i.value(i.value() + 1);
					}
				}
				io.save_screen();
				m3.print_spells(spell_index, i.value(), true, -1);
				io.pause_line(0);
				io.restore_screen();
			}
		}
	}
	
	/* Go up one level					-RAK-	*/
	public void go_up() {
		CaveType c_ptr;
		boolean no_stairs = false;
		
		c_ptr = var.cave[py.char_row][py.char_col];
		if (c_ptr.tptr != 0) {
			if (t.t_list[c_ptr.tptr].tval == Constants.TV_UP_STAIR) {
				var.dun_level--;
				var.new_level_flag = true;
				io.msg_print("You enter a maze of up staircases.");
				io.msg_print("You pass through a one-way door.");
			} else {
				no_stairs = true;
			}
		} else {
			no_stairs = true;
		}
		
		if (no_stairs) {
			io.msg_print("I see no up staircase here.");
			var.free_turn_flag = true;
		}
	}
	
	/* Go down one level					-RAK-	*/
	public void go_down() {
		CaveType c_ptr;
		boolean no_stairs = false;
		
		c_ptr = var.cave[py.char_row][py.char_col];
		if (c_ptr.tptr != 0) {
			if (t.t_list[c_ptr.tptr].tval == Constants.TV_DOWN_STAIR) {
				var.dun_level++;
				var.new_level_flag = true;
				io.msg_print("You enter a maze of down staircases.");
				io.msg_print("You pass through a one-way door.");
			} else {
				no_stairs = true;
			}
		} else {
			no_stairs = true;
		}
		
		if (no_stairs) {
			io.msg_print("I see no down staircase here.");
			var.free_turn_flag = true;
		}
	}
	
	/* Jam a closed door					-RAK-	*/
	public void jamdoor() {
		IntPointer y, x, dir = new IntPointer();
		IntPointer i = new IntPointer(), j = new IntPointer();
		CaveType c_ptr;
		InvenType t_ptr, i_ptr;
		String tmp_str;
		
		var.free_turn_flag = true;
		y = new IntPointer(py.char_row);
		x = new IntPointer(py.char_col);
		if (mor1.get_dir("", dir)) {
			m3.mmove(dir.value(), y, x);
			c_ptr = var.cave[y.value()][x.value()];
			if (c_ptr.tptr != 0) {
				t_ptr = t.t_list[c_ptr.tptr];
				if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
					if (c_ptr.cptr == 0) {
						if (m3.find_range(Constants.TV_SPIKE, Constants.TV_NEVER, i, j)) {
							var.free_turn_flag = false;
							io.count_msg_print("You jam the door with a spike.");
							if (t_ptr.p1 > 0) {
								t_ptr.p1 = -t_ptr.p1;	/* Make locked to stuck. */
							}
							/* Successive spikes have a progressively smaller effect.
							 * Series is: 0 20 30 37 43 48 52 56 60 64 67 70 ... */
							t_ptr.p1 -= 1 + 190 / (10 - t_ptr.p1);
							i_ptr = t.inventory[i.value()];
							if (i_ptr.number > 1) {
								i_ptr.number--;
								t.inven_weight -= i_ptr.weight;
							} else {
								m3.inven_destroy(i.value());
							}
						} else {
							io.msg_print("But you have no spikes.");
						}
					} else {
						var.free_turn_flag = false;
						tmp_str = String.format("The %s is in your way!", mon.c_list[mon.m_list[c_ptr.cptr].mptr].name);
						io.msg_print(tmp_str);
					}
				} else if (t_ptr.tval == Constants.TV_OPEN_DOOR) {
					io.msg_print("The door must be closed first.");
				} else {
					io.msg_print("That isn't a door!");
				}
			} else {
				io.msg_print("That isn't a door!");
			}
		}
	}
	
	/* Refill the players lamp				-RAK-	*/
	public void refill_lamp() {
		IntPointer i = new IntPointer(), j = new IntPointer();
		int k;
		InvenType i_ptr;
		
		var.free_turn_flag = true;
		k = t.inventory[Constants.INVEN_LIGHT].subval;
		if (k != 0) {
			io.msg_print("But you are not using a lamp.");
		} else if (!m3.find_range(Constants.TV_FLASK, Constants.TV_NEVER, i, j)) {
			io.msg_print("You have no oil.");
		} else {
			var.free_turn_flag = false;
			i_ptr = t.inventory[Constants.INVEN_LIGHT];
			i_ptr.p1 += t.inventory[i.value()].p1;
			if (i_ptr.p1 > Constants.OBJ_LAMP_MAX) {
				i_ptr.p1 = Constants.OBJ_LAMP_MAX;
				io.msg_print ("Your lamp overflows, spilling oil on the ground.");
				io.msg_print("Your lamp is full.");
			} else if (i_ptr.p1 > Constants.OBJ_LAMP_MAX/2) {
				io.msg_print ("Your lamp is more than half full.");
			} else if (i_ptr.p1 == Constants.OBJ_LAMP_MAX/2) {
				io.msg_print ("Your lamp is half full.");
			} else {
				io.msg_print ("Your lamp is less than half full.");
			}
			desc.desc_remain(i.value());
			m3.inven_destroy(i.value());
		}
	}
}
