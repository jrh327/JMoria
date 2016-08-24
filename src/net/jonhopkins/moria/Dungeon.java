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
	
	private Dungeon() { }
	
	public static void dungeon() {
		int find_count, i = 0;
		int regen_amount;	    /* Regenerate hp and mana*/
		CharPointer command = new CharPointer();		/* Last command		 */
		PlayerMisc p_ptr;
		InvenType i_ptr;
		PlayerFlags f_ptr;
		
		/* Main procedure for dungeon.			-RAK-	*/
		/* Note: There is a lot of preliminary magic going on here at first*/
		
		/* init pointers. */
		f_ptr = Player.py.flags;
		p_ptr = Player.py.misc;
		
		/* Check light status for setup	   */
		i_ptr = Treasure.inventory[Constants.INVEN_LIGHT];
		if (i_ptr.p1 > 0) {
			Variable.player_light = true;
		} else {
			Variable.player_light = false;
		}
		/* Check for a maximum level		   */
		if (Variable.dun_level > p_ptr.max_dlv) {
			p_ptr.max_dlv = Variable.dun_level;
		}
		
		/* Reset flags and initialize variables  */
		Variable.command_count = 0;
		find_count = 0;
		Variable.new_level_flag = false;
		Variable.find_flag = 0;
		Variable.teleport_flag = false;
		Monsters.mon_tot_mult = 0;
		Variable.cave[Player.char_row][Player.char_col].cptr = 1;
		/* Ensure we display the panel. Used to do this with a global var. -CJS- */
		Variable.panel_row = Variable.panel_col = -1;
		/* Light up the area around character	   */
		Misc4.check_view();
		/* must do this after panel_row/col set to -1, because mor1.search_off() will
		 * call check_view(), and so the panel_* variables must be valid before
		 * search_off() is called */
		if ((Player.py.flags.status & Constants.PY_SEARCH) != 0) {
			Moria1.search_off();
		}
		/* Light,  but do not move critters	    */
		Creature.creatures(false);
		/* Print the depth			   */
		Misc3.prt_depth();
		
		/* Loop until dead,  or new level		*/
		do {
			/* Increment turn counter			*/
			Variable.turn++;
			
			/* turn over the store contents every, say, 1000 turns */
			if ((Variable.dun_level != 0) && ((Variable.turn % 1000) == 0)) {
				Store1.store_maint();
			}
			
			/* Check for creature generation		*/
			if (Misc1.randint(Constants.MAX_MALLOC_CHANCE) == 1) {
				Misc1.alloc_monster(1, Constants.MAX_SIGHT, false);
			}
			/* Check light status			       */
			i_ptr = Treasure.inventory[Constants.INVEN_LIGHT];
			if (Variable.player_light) {
				if (i_ptr.p1 > 0) {
					i_ptr.p1--;
					if (i_ptr.p1 == 0) {
						Variable.player_light = false;
						IO.msg_print("Your light has gone out!");
						Moria1.disturb(false, true);
						/* unlight creatures */
						Creature.creatures(false);
					} else if ((i_ptr.p1 < 40) && (Misc1.randint(5) == 1) && (Player.py.flags.blind < 1)) {
						Moria1.disturb (false, false);
						IO.msg_print("Your light is growing faint.");
					}
				} else {
					Variable.player_light = false;
					Moria1.disturb(false, true);
					/* unlight creatures */
					Creature.creatures(false);
				}
			} else if (i_ptr.p1 > 0) {
				i_ptr.p1--;
				Variable.player_light = true;
				Moria1.disturb(false, true);
				/* light creatures */
				Creature.creatures(false);
			}
			
			/* Update counters and messages			*/
			/* Heroism (must precede anything that can damage player)      */
			if (f_ptr.hero > 0) {
				if ((Constants.PY_HERO & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_HERO;
					Moria1.disturb(false, false);
					p_ptr.mhp += 10;
					p_ptr.chp += 10;
					p_ptr.bth += 12;
					p_ptr.bthb+= 12;
					IO.msg_print("You feel like a HERO!");
					Misc3.prt_mhp();
					Misc3.prt_chp();
				}
				f_ptr.hero--;
				if (f_ptr.hero == 0) {
					f_ptr.status &= ~Constants.PY_HERO;
					Moria1.disturb(false, false);
					p_ptr.mhp -= 10;
					if (p_ptr.chp > p_ptr.mhp) {
						p_ptr.chp = p_ptr.mhp;
						p_ptr.chp_frac = 0;
						Misc3.prt_chp();
					}
					p_ptr.bth -= 12;
					p_ptr.bthb-= 12;
					IO.msg_print("The heroism wears off.");
					Misc3.prt_mhp();
				}
			}
			/* Super Heroism */
			if (f_ptr.shero > 0) {
				if ((Constants.PY_SHERO & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_SHERO;
					Moria1.disturb(false, false);
					p_ptr.mhp += 20;
					p_ptr.chp += 20;
					p_ptr.bth += 24;
					p_ptr.bthb+= 24;
					IO.msg_print("You feel like a SUPER HERO!");
					Misc3.prt_mhp();
					Misc3.prt_chp();
				}
				f_ptr.shero--;
				if (f_ptr.shero == 0) {
					f_ptr.status &= ~Constants.PY_SHERO;
					Moria1.disturb(false, false);
					p_ptr.mhp -= 20;
					if (p_ptr.chp > p_ptr.mhp) {
						p_ptr.chp = p_ptr.mhp;
						p_ptr.chp_frac = 0;
						Misc3.prt_chp();
					}
					p_ptr.bth -= 24;
					p_ptr.bthb-= 24;
					IO.msg_print("The super heroism wears off.");
					Misc3.prt_mhp();
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
						IO.msg_print("You are getting weak from hunger.");
						Moria1.disturb(false, false);
						Misc3.prt_hunger();
					}
					if ((f_ptr.food < Constants.PLAYER_FOOD_FAINT) && (Misc1.randint(8) == 1)) {
						f_ptr.paralysis += Misc1.randint(5);
						IO.msg_print("You faint from the lack of food.");
						Moria1.disturb(true, false);
					}
				} else if ((Constants.PY_HUNGRY & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_HUNGRY;
					IO.msg_print("You are getting hungry.");
					Moria1.disturb(false, false);
					Misc3.prt_hunger();
				}
			}
			/* Food consumption	*/
			/* Note: Speeded up characters really burn up the food!  */
			if (f_ptr.speed < 0) {
				f_ptr.food -= f_ptr.speed * f_ptr.speed;
			}
			f_ptr.food -= f_ptr.food_digested;
			if (f_ptr.food < 0) {
				Moria1.take_hit(-f_ptr.food / 16, "starvation");   /* -CJS- */
				Moria1.disturb(true, false);
			}
			/* Regenerate	       */
			if (f_ptr.regenerate) regen_amount = regen_amount * 3 / 2;
			if ((Player.py.flags.status & Constants.PY_SEARCH) != 0 || f_ptr.rest != 0) {
				regen_amount = regen_amount * 2;
			}
			if ((Player.py.flags.poisoned < 1) && (p_ptr.chp < p_ptr.mhp)) {
				regenhp(regen_amount);
			}
			if (p_ptr.cmana < p_ptr.mana) {
				regenmana(regen_amount);
			}
			/* Blindness	       */
			if (f_ptr.blind > 0) {
				if ((Constants.PY_BLIND & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_BLIND;
					Misc1.prt_map();
					Misc3.prt_blind();
					Moria1.disturb(false, true);
					/* unlight creatures */
					Creature.creatures(false);
				}
				f_ptr.blind--;
				if (f_ptr.blind == 0) {
					f_ptr.status &= ~Constants.PY_BLIND;
					Misc3.prt_blind();
					Misc1.prt_map();
					/* light creatures */
					Moria1.disturb(false, true);
					Creature.creatures(false);
					IO.msg_print("The veil of darkness lifts.");
				}
			}
			/* Confusion	       */
			if (f_ptr.confused > 0) {
				if ((Constants.PY_CONFUSED & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_CONFUSED;
					Misc3.prt_confused();
				}
				f_ptr.confused--;
				if (f_ptr.confused == 0) {
					f_ptr.status &= ~Constants.PY_CONFUSED;
					Misc3.prt_confused();
					IO.msg_print("You feel less confused now.");
					if (Player.py.flags.rest != 0) {
						Moria1.rest_off();
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
						Misc3.prt_afraid();
					}
				} else if ((f_ptr.shero + f_ptr.hero) > 0) {
					f_ptr.afraid = 1;
				}
				f_ptr.afraid--;
				if (f_ptr.afraid == 0) {
					f_ptr.status &= ~Constants.PY_FEAR;
					Misc3.prt_afraid();
					IO.msg_print("You feel bolder now.");
					Moria1.disturb(false, false);
				}
			}
			/* Poisoned	       */
			if (f_ptr.poisoned > 0) {
				if ((Constants.PY_POISONED & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_POISONED;
					Misc3.prt_poisoned();
				}
				f_ptr.poisoned--;
				if (f_ptr.poisoned == 0) {
					f_ptr.status &= ~Constants.PY_POISONED;
					Misc3.prt_poisoned();
					IO.msg_print("You feel better.");
					Moria1.disturb(false, false);
				} else {
					switch(Misc3.con_adj())
					{
					case -4: i = 4; break;
					case -3:
					case -2: i = 3; break;
					case -1: i = 2; break;
					case 0:	 i = 1; break;
					case 1: case 2: case 3:
						i = ((Variable.turn % 2) == 0) ? 1 : 0;
						break;
					case 4: case 5:
						i = ((Variable.turn % 3) == 0) ? 1 : 0;
						break;
					case 6:
						i = ((Variable.turn % 4) == 0) ? 1 : 0;
						break;
					}
					Moria1.take_hit(i, "poison");
					Moria1.disturb(true, false);
				}
			}
			/* Fast		       */
			if (f_ptr.fast > 0) {
				if ((Constants.PY_FAST & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_FAST;
					Moria1.change_speed(-1);
					IO.msg_print("You feel yourself moving faster.");
					Moria1.disturb(false, false);
				}
				f_ptr.fast--;
				if (f_ptr.fast == 0) {
					f_ptr.status &= ~Constants.PY_FAST;
					Moria1.change_speed(1);
					IO.msg_print("You feel yourself slow down.");
					Moria1.disturb(false, false);
				}
			}
			/* Slow		       */
			if (f_ptr.slow > 0) {
				if ((Constants.PY_SLOW & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_SLOW;
					Moria1.change_speed(1);
					IO.msg_print("You feel yourself moving slower.");
					Moria1.disturb(false, false);
				}
				f_ptr.slow--;
				if (f_ptr.slow == 0) {
					f_ptr.status &= ~Constants.PY_SLOW;
					Moria1.change_speed(-1);
					IO.msg_print("You feel yourself speed up.");
					Moria1.disturb(false, false);
				}
			}
			/* Resting is over?      */
			if (f_ptr.rest > 0) {
				f_ptr.rest--;
				if (f_ptr.rest == 0) {	/* Resting over	       */
					Moria1.rest_off();
				}
			} else if (f_ptr.rest < 0) {
				/* Rest until reach max mana and max hit points.  */
				f_ptr.rest++;
				if ((p_ptr.chp == p_ptr.mhp && p_ptr.cmana == p_ptr.mana) || f_ptr.rest == 0) {
					Moria1.rest_off();
				}
			}
			
			/* Check for interrupts to find or rest. */
			if ((Variable.command_count > 0 || Variable.find_flag > 0 || f_ptr.rest != 0)
					&& IO.isKeyAvailable()) {	
				IO.getch();
				Moria1.disturb(false, false);
			}
			
			/* Hallucinating?	 (Random characters appear!)*/
			if (f_ptr.image > 0) {
				Moria2.end_find();
				f_ptr.image--;
				if (f_ptr.image == 0) {
					Misc1.prt_map();	 /* Used to draw entire screen! -CJS- */
				}
			}
			/* Paralysis	       */
			if (f_ptr.paralysis > 0) {
				/* when paralysis true, you can not see any movement that occurs */
				f_ptr.paralysis--;
				Moria1.disturb(true, false);
			}
			/* Protection from evil counter*/
			if (f_ptr.protevil > 0) {
				f_ptr.protevil--;
				if (f_ptr.protevil == 0) {
					IO.msg_print("You no longer feel safe from evil.");
				}
			}
			/* Invulnerability	*/
			if (f_ptr.invuln > 0) {
				if ((Constants.PY_INVULN & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_INVULN;
					Moria1.disturb(false, false);
					Player.py.misc.pac += 100;
					Player.py.misc.dis_ac += 100;
					Misc3.prt_pac();
					IO.msg_print("Your skin turns into steel!");
				}
				f_ptr.invuln--;
				if (f_ptr.invuln == 0) {
					f_ptr.status &= ~Constants.PY_INVULN;
					Moria1.disturb(false, false);
					Player.py.misc.pac -= 100;
					Player.py.misc.dis_ac -= 100;
					Misc3.prt_pac();
					IO.msg_print("Your skin returns to normal.");
				}
			}
			/* Blessed       */
			if (f_ptr.blessed > 0) {
				if ((Constants.PY_BLESSED & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_BLESSED;
					Moria1.disturb(false, false);
					p_ptr.bth += 5;
					p_ptr.bthb+= 5;
					p_ptr.pac += 2;
					p_ptr.dis_ac+= 2;
					IO.msg_print("You feel righteous!");
					Misc3.prt_pac();
				}
				f_ptr.blessed--;
				if (f_ptr.blessed == 0) {
					f_ptr.status &= ~Constants.PY_BLESSED;
					Moria1.disturb(false, false);
					p_ptr.bth -= 5;
					p_ptr.bthb-= 5;
					p_ptr.pac -= 2;
					p_ptr.dis_ac -= 2;
					IO.msg_print("The prayer has expired.");
					Misc3.prt_pac();
				}
			}
			/* Resist Heat   */
			if (f_ptr.resist_heat > 0) {
				f_ptr.resist_heat--;
				if (f_ptr.resist_heat == 0) {
					IO.msg_print("You no longer feel safe from flame.");
				}
			}
			/* Resist Cold   */
			if (f_ptr.resist_cold > 0) {
				f_ptr.resist_cold--;
				if (f_ptr.resist_cold == 0) {
					IO.msg_print("You no longer feel safe from cold.");
				}
			}
			/* Detect Invisible      */
			if (f_ptr.detect_inv > 0) {
				if ((Constants.PY_DET_INV & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_DET_INV;
					f_ptr.see_inv = true;
					/* light but don't move creatures */
					Creature.creatures(false);
				}
				f_ptr.detect_inv--;
				if (f_ptr.detect_inv == 0) {
					f_ptr.status &= ~Constants.PY_DET_INV;
					/* may still be able to see_inv if wearing magic item */
					Moria1.calc_bonuses();
					/* unlight but don't move creatures */
					Creature.creatures(false);
				}
			}
			/* Timed infra-vision    */
			if (f_ptr.tim_infra > 0) {
				if ((Constants.PY_TIM_INFRA & f_ptr.status) == 0) {
					f_ptr.status |= Constants.PY_TIM_INFRA;
					f_ptr.see_infra++;
					/* light but don't move creatures */
					Creature.creatures(false);
				}
				f_ptr.tim_infra--;
				if (f_ptr.tim_infra == 0) {
					f_ptr.status &= ~Constants.PY_TIM_INFRA;
					f_ptr.see_infra--;
					/* unlight but don't move creatures */
					Creature.creatures(false);
				}
			}
			/* Word-of-Recall  Note: Word-of-Recall is a delayed action	 */
			if (f_ptr.word_recall > 0) {
				if (f_ptr.word_recall == 1) {
					Variable.new_level_flag = true;
					f_ptr.paralysis++;
					f_ptr.word_recall = 0;
					if (Variable.dun_level > 0) {
						Variable.dun_level = 0;
						IO.msg_print("You feel yourself yanked upwards!");
					} else if (Player.py.misc.max_dlv != 0) {
						Variable.dun_level = Player.py.misc.max_dlv;
						IO.msg_print("You feel yourself yanked downwards!");
					}
				} else {
					f_ptr.word_recall--;
				}
			}
			
			/* Random teleportation  */
			if ((Player.py.flags.teleport > 0) && (Misc1.randint(100) == 1)) {
				Moria1.disturb(false, false);
				Misc3.teleport(40);
			}
			
			/* See if we are too weak to handle the weapon or pack.  -CJS- */
			if ((Player.py.flags.status & Constants.PY_STR_WGT) != 0) {
				Misc3.check_strength();
			}
			if ((Player.py.flags.status & Constants.PY_STUDY) != 0) {
				Misc3.prt_study();
			}
			if ((Player.py.flags.status & Constants.PY_SPEED) != 0) {
				Player.py.flags.status &= ~Constants.PY_SPEED;
				Misc3.prt_speed();
			}
			if ((Player.py.flags.status & Constants.PY_PARALYSED) != 0 && (Player.py.flags.paralysis < 1)) {
				Misc3.prt_state();
				Player.py.flags.status &= ~Constants.PY_PARALYSED;
			} else if (Player.py.flags.paralysis > 0) {
				Misc3.prt_state();
				Player.py.flags.status |= Constants.PY_PARALYSED;
			} else if (Player.py.flags.rest != 0) {
				Misc3.prt_state();
			}
			
			if ((Player.py.flags.status & Constants.PY_ARMOR) != 0) {
				Misc3.prt_pac();
				Player.py.flags.status &= ~Constants.PY_ARMOR;
			}
			if ((Player.py.flags.status & Constants.PY_STATS) != 0) {
				for (i = 0; i < 6; i++) {
					if (((Constants.PY_STR << i) & Player.py.flags.status) != 0) {
						Misc3.prt_stat(i);
					}
				}
				Player.py.flags.status &= ~Constants.PY_STATS;
			}
			if ((Player.py.flags.status & Constants.PY_HP) != 0) {
				Misc3.prt_mhp();
				Misc3.prt_chp();
				Player.py.flags.status &= ~Constants.PY_HP;
			}
			if ((Player.py.flags.status & Constants.PY_MANA) != 0) {
				Misc3.prt_cmana();
				Player.py.flags.status &= ~Constants.PY_MANA;
			}
			
			/* Allow for a slim chance of detect enchantment -CJS- */
			/* for 1st level char, check once every 2160 turns
			 * for 40th level char, check once every 416 turns */
			if (((Variable.turn & 0xF) == 0) && (f_ptr.confused == 0) && (Misc1.randint((int)(10 + 750 / (5 + Player.py.misc.lev))) == 1)) {
				String tmp_str;
				
				for (i = 0; i < Constants.INVEN_ARRAY_SIZE; i++) {
					if (i == Treasure.inven_ctr) {
						i = 22;
					}
					i_ptr = Treasure.inventory[i];
					/* if in inventory, succeed 1 out of 50 times,
					 * if in equipment list, success 1 out of 10 times */
					if ((i_ptr.tval != Constants.TV_NOTHING) && enchanted(i_ptr) && (Misc1.randint(i < 22 ? 50 : 10) == 1)) {
						//extern char *describe_use();
						
						tmp_str = String.format("There's something about what you are %s...", Moria1.describe_use(i));
						Moria1.disturb(false, false);
						IO.msg_print(tmp_str);
						Misc4.add_inscribe(i_ptr, Constants.ID_MAGIK);
					}
				}
			}
			
			/* Check the state of the monster list, and delete some monsters if
			 * the monster list is nearly full.  This helps to avoid problems in
			 * creature.c when monsters try to multiply.  Compact_monsters() is
			 * much more likely to succeed if called from here, than if called
			 * from within creature.creatures().  */
			if (Constants.MAX_MALLOC - Monsters.mfptr < 10) {
				Misc1.compact_monsters();
			}
			
			if ((Player.py.flags.paralysis < 1) && (Player.py.flags.rest == 0) && (!Variable.death)) {
				/* Accept a command and execute it				 */
				do {
					if ((Player.py.flags.status & Constants.PY_REPEAT) != 0) {
						Misc3.prt_state();
					}
					Variable.default_dir = 0;
					Variable.free_turn_flag = false;
					
					if (Variable.find_flag > 0) {
						Moria2.find_run();
						find_count--;
						if (find_count == 0) {
							Moria2.end_find();
						}
						IO.put_qio();
					} else if (Variable.doing_inven > 0) {
						Moria1.inven_command(Variable.doing_inven);
					} else {
						/* move the cursor to the players character */
						IO.move_cursor_relative(Player.char_row, Player.char_col);
						if (Variable.command_count > 0) {
							Variable.msg_flag = 0;
							Variable.default_dir = 1;
						} else {
							Variable.msg_flag = 0;
							command.value(IO.inkey());
							i = 0;
							/* Get a count for a command. */
							if ((Variable.rogue_like_commands.value() && command.value() >= '0' && command.value() <= '9') || (!Variable.rogue_like_commands.value() && command.value() == '#')) {
								String tmp;
								
								IO.prt("Repeat count:", 0, 0);
								if (command.value() == '#') {
									command.value('0');
								}
								i = 0;
								while (true) {
									if (command.value() == Constants.DELETE || command.value() == (Constants.CTRL & 'H')) {
										i = i / 10;
										tmp = String.format("%d", i);
										if (tmp.length() > 8) tmp = tmp.substring(0, 8);
										IO.prt(tmp, 0, 14);
									} else if (command.value() >= '0' && command.value() <= '9') {
										if (i > 99) {
											IO.bell();
										} else {
											i = i * 10 + command.value() - '0';
											tmp = String.format("%d", i);
											IO.prt(tmp, 0, 14);
										}
									} else {
										break;
									}
									command.value(IO.inkey());
								}
								if (i == 0) {
									i = 99;
									tmp = String.format("%d", i);
									IO.prt(tmp, 0, 14);
								}
								/* a special hack to allow numbers as commands */
								if (command.value() == ' ') {
									IO.prt("Command:", 0, 20);
									command.value(IO.inkey());
								}
							}
							/* Another way of typing control codes -CJS- */
							if (command.value() == '^') {
								if (Variable.command_count > 0) {
									Misc3.prt_state();
								}
								if (IO.get_com("Control-", command)) {
									if (command.value() >= 'A' && command.value() <= 'Z') {
										command.value((char)(command.value() - 'A' - 1));
									} else if (command.value() >= 'a' && command.value() <= 'z') {
										command.value((char)(command.value() - 'a' - 1));
									} else {
										IO.msg_print("Type ^ <letter> for a control char");
										command.value(' ');
									}
								} else {
									command.value(' ');
								}
							}
							/* move cursor to player char again, in case it moved */
							IO.move_cursor_relative(Player.char_row, Player.char_col);
							/* Commands are always converted to rogue form. -CJS- */
							if (!Variable.rogue_like_commands.value()) {
								command.value(original_commands(command.value()));
							}
							if (i > 0) {
								if (!valid_countcommand(command.value())) {
									Variable.free_turn_flag = true;
									IO.msg_print("Invalid command with a count.");
									command.value(' ');
								} else {
									Variable.command_count = i;
									Misc3.prt_state();
								}
							}
						}
						/* Flash the message line. */
						IO.erase_line(Constants.MSG_LINE, 0);
						IO.move_cursor_relative(Player.char_row, Player.char_col);
						IO.put_qio();
						
						do_command(command.value());
						/* Find is counted differently, as the command changes. */
						if (Variable.find_flag > 0) {
							find_count = Variable.command_count - 1;
							Variable.command_count = 0;
						} else if (Variable.free_turn_flag) {
							Variable.command_count = 0;
						} else if (Variable.command_count > 0) {
							Variable.command_count--;
						}
					}
					/* End of commands				     */
				} while (Variable.free_turn_flag && !Variable.new_level_flag && Variable.eof_flag == 0);
			} else {
				/* if paralyzed, resting, or dead, flush output */
				/* but first move the cursor onto the player, for aesthetics */
				IO.move_cursor_relative(Player.char_row, Player.char_col);
				IO.put_qio ();
			}
			
			/* Teleport?		       */
			if (Variable.teleport_flag)	Misc3.teleport(100);
			/* Move the creatures	       */
			if (!Variable.new_level_flag)	Creature.creatures(true);
			/* Exit when new_level_flag is set   */
		} while (!Variable.new_level_flag && Variable.eof_flag == 0);
	}
	
	public static char original_commands(char com_val) {
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
			if (Moria1.get_dir("", dir_val)) {
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
			if (Moria1.get_dir("", dir_val)) {
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
	
	public static void do_command(char com_val) {
		IntPointer dir_val = new IntPointer();
		boolean do_pickup;
		IntPointer y, x;
		int i, j;
		String out_val, tmp_str;
		PlayerFlags f_ptr;
		
		/* hack for move without pickup.  Map '-' to a movement command. */
		if (com_val == '-') {
			do_pickup = false;
			i = Variable.command_count;
			if (Moria1.get_dir("", dir_val)) {
				Variable.command_count = i;
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
			IO.flush();
			if (IO.get_check("Do you really want to quit?")) {
				Variable.new_level_flag = true;
				Variable.death = true;
				Variable.died_from = "Quitting";
			}
			Variable.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'P'):	/* (^P)revious message. */
			if (Variable.command_count > 0) {
				i = Variable.command_count;
				if (i > Constants.MAX_SAVE_MSG) {
					i = Constants.MAX_SAVE_MSG;
				}
				Variable.command_count = 0;
			} else if (Variable.last_command != (Constants.CTRL & 'P')) {
				i = 1;
			} else {
				i = Constants.MAX_SAVE_MSG;
			}
			j = Variable.last_msg;
			if (i > 1) {
				IO.save_screen();
				x = new IntPointer(i);
				while (i > 0) {
					i--;
					IO.prt(Variable.old_msg[j], i, 0);
					if (j == 0) {
						j = Constants.MAX_SAVE_MSG - 1;
					} else {
						j--;
					}
				}
				IO.erase_line(x.value(), 0);
				IO.pause_line(x.value());
				IO.restore_screen();
			} else {
				/* Distinguish real and recovered messages with a '>'. -CJS- */
				IO.put_buffer(">", 0, 0);
				IO.prt(Variable.old_msg[j], 0, 1);
			}
			Variable.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'V'):	/* (^V)iew license */
			Files.helpfile(Config.MORIA_GPL);
			Variable.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'W'):	/* (^W)izard mode */
			if (Variable.wizard) {
				Variable.wizard = false;
				IO.msg_print("Wizard mode off.");
			} else if (Misc3.enter_wiz_mode()) {
				IO.msg_print("Wizard mode on.");
			}
			Misc3.prt_winner();
			Variable.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'X'):	/* e(^X)it and save */
			if (Variable.total_winner) {
				IO.msg_print("You are a Total Winner,  your character must be retired.");
				if (Variable.rogue_like_commands.value()) {
					IO.msg_print("Use 'Q' to when you are ready to quit.");
				} else {
					IO.msg_print ("Use <Control>-K when you are ready to quit.");
				}
			} else {
				Variable.died_from = "(saved)";
				IO.msg_print("Saving game...");
				if (Save.save_char()) {
					Death.exit_game();
				}
				Variable.died_from = "(alive and well)";
			}
			Variable.free_turn_flag = true;
			break;
		case '=':		/* (=) set options */
			IO.save_screen();
			Misc2.set_options();
			IO.restore_screen();
			Variable.free_turn_flag = true;
			break;
		case '{':		/* ({) inscribe an object    */
			Misc4.scribe_object();
			Variable.free_turn_flag = true;
			break;
		case '!':		/* (!) escape to the shell */
		case '$':
			IO.shell_out();
			Variable.free_turn_flag = true;
			break;
		case Constants.ESCAPE:	/* (ESC)   do nothing. */
		case ' ':		/* (space) do nothing. */
			Variable.free_turn_flag = true;
			break;
		case 'b':		/* (b) down, left	(1) */
			Moria3.move_char(1, do_pickup);
			break;
		case Constants.KEY_DOWN:
		case 'j':		/* (j) down		(2) */
			Moria3.move_char(2, do_pickup);
			break;
		case 'n':		/* (n) down, right	(3) */
			Moria3.move_char(3, do_pickup);
			break;
		case Constants.KEY_LEFT:
		case 'h':		/* (h) left		(4) */
			Moria3.move_char(4, do_pickup);
			break;
		case Constants.KEY_RIGHT:
		case 'l':		/* (l) right		(6) */
			Moria3.move_char(6, do_pickup);
			break;
		case 'y':		/* (y) up, left		(7) */
			Moria3.move_char(7, do_pickup);
			break;
		case Constants.KEY_UP:
		case 'k':		/* (k) up		(8) */
			Moria3.move_char(8, do_pickup);
			break;
		case 'u':		/* (u) up, right	(9) */
			Moria3.move_char(9, do_pickup);
			break;
		case 'B':		/* (B) run down, left	(. 1) */
			Moria2.find_init(1);
			break;
		case 'J':		/* (J) run down		(. 2) */
			Moria2.find_init(2);
			break;
		case 'N':		/* (N) run down, right	(. 3) */
			Moria2.find_init(3);
			break;
		case 'H':		/* (H) run left		(. 4) */
			Moria2.find_init(4);
			break;
		case 'L':		/* (L) run right	(. 6) */
			Moria2.find_init(6);
			break;
		case 'Y':		/* (Y) run up, left	(. 7) */
			Moria2.find_init(7);
			break;
		case 'K':		/* (K) run up		(. 8) */
			Moria2.find_init(8);
			break;
		case 'U':		/* (U) run up, right	(. 9) */
			Moria2.find_init(9);
			break;
		case '/':		/* (/) identify a symbol */
			Help.ident_char();
			Variable.free_turn_flag = true;
			break;
		case '.':		/* (.) stay in one place (5) */
			Moria3.move_char(5, do_pickup);
			if (Variable.command_count > 1) {
				Variable.command_count--;
				Moria1.rest();
			}
			break;
		case '<':		/* (<) go up a staircase */
			go_up();
			break;
		case '>':		/* (>) go down a staircase */
			go_down();
			break;
		case '?':		/* (?) help with commands */
			if (Variable.rogue_like_commands.value()) {
				Files.helpfile(Config.MORIA_HELP);
			} else {
				Files.helpfile(Config.MORIA_ORIG_HELP);
			}
			Variable.free_turn_flag = true;
			break;
		case 'f':		/* (f)orce		(B)ash */
			Moria4.bash();
			break;
		case 'C':		/* (C)haracter description */
			IO.save_screen();
			Misc3.change_name();
			IO.restore_screen();
			Variable.free_turn_flag = true;
			break;
		case 'D':		/* (D)isarm trap */
			Moria4.disarm_trap();
			break;
		case 'E':		/* (E)at food */
			Eat.eat();
			break;
		case 'F':		/* (F)ill lamp */
			refill_lamp();
			break;
		case 'G':		/* (G)ain magic spells */
			Misc3.gain_spells();
			break;
		case 'V':		/* (V)iew scores */
			boolean b;
			if (Variable.last_command != 'V') {
				b = true;
			} else {
				b = false;
			}
			IO.save_screen();
			Death.display_scores(b);
			IO.restore_screen();
			Variable.free_turn_flag = true;
			break;
		case 'W':		/* (W)here are we on the map	(L)ocate on map */
			if ((Player.py.flags.blind > 0) || Moria1.no_light()) {
				IO.msg_print("You can't see your map.");
			} else {
				int cy, cx, p_y, p_x;
				
				y = new IntPointer(Player.char_row);
				x = new IntPointer(Player.char_col);
				if (Misc1.get_panel(y.value(), x.value(), true)) {
					Misc1.prt_map();
				}
				cy = Variable.panel_row;
				cx = Variable.panel_col;
				for(;;) {
					p_y = Variable.panel_row;
					p_x = Variable.panel_col;
					if (p_y == cy && p_x == cx) {
						tmp_str = "";
					} else {
						tmp_str = String.format("%s%s of", p_y < cy ? " North" : p_y > cy ? " South" : "", p_x < cx ? " West" : p_x > cx ? " East" : "");
					}
					out_val = String.format("Map sector [%d,%d], which is%s your sector. Look which direction?", p_y, p_x, tmp_str);
					if (!Moria1.get_dir(out_val, dir_val)) {
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
						if (x.value() < 0 || y.value() < 0 || x.value() >= Variable.cur_width || y.value() >= Variable.cur_width) {
							IO.msg_print("You've gone past the end of your map.");
							x.value(x.value() - ((dir_val.value() - 1) % 3 - 1) * Constants.SCREEN_WIDTH / 2);
							y.value(y.value() + ((dir_val.value() - 1) / 3 - 1) * Constants.SCREEN_HEIGHT / 2);
							break;
						}
						if (Misc1.get_panel(y.value(), x.value(), true)) {
							Misc1.prt_map();
							break;
						}
					}
				}
				/* Move to a new panel - but only if really necessary. */
				if (Misc1.get_panel(Player.char_row, Player.char_col, false)) {
					Misc1.prt_map();
				}
			}
			Variable.free_turn_flag = true;
			break;
		case 'R':		/* (R)est a while */
			Moria1.rest();
			break;
		case '#':		/* (#) search toggle	(S)earch toggle */
			if ((Player.py.flags.status & Constants.PY_SEARCH) != 0) {
				Moria1.search_off();
			} else {
				Moria1.search_on();
			}
			Variable.free_turn_flag = true;
			break;
		case (Constants.CTRL & 'B'):		/* (^B) tunnel down left	(T 1) */
			Moria4.tunnel(1);
			break;
		case (Constants.CTRL & 'M'):		/* cr must be treated same as lf. */
		case (Constants.CTRL & 'J'):		/* (^J) tunnel down		(T 2) */
			Moria4.tunnel(2);
			break;
		case (Constants.CTRL & 'N'):		/* (^N) tunnel down right	(T 3) */
			Moria4.tunnel(3);
			break;
		case (Constants.CTRL & 'H'):		/* (^H) tunnel left		(T 4) */
			Moria4.tunnel(4);
			break;
		case (Constants.CTRL & 'L'):		/* (^L) tunnel right		(T 6) */
			Moria4.tunnel(6);
			break;
		case (Constants.CTRL & 'Y'):		/* (^Y) tunnel up left		(T 7) */
			Moria4.tunnel(7);
			break;
		case (Constants.CTRL & 'K'):		/* (^K) tunnel up		(T 8) */
			Moria4.tunnel(8);
			break;
		case (Constants.CTRL & 'U'):		/* (^U) tunnel up right		(T 9) */
			Moria4.tunnel(9);
			break;
		case 'z':		/* (z)ap a wand		(a)im a wand */
			Wands.aim();
			break;
		case 'M':
			IO.screen_map();
			Variable.free_turn_flag = true;
			break;
		case 'P':		/* (P)eruse a book	(B)rowse in a book */
			examine_book();
			Variable.free_turn_flag = true;
			break;
		case 'c':		/* (c)lose an object */
			Moria3.closeobject();
			break;
		case 'd':		/* (d)rop something */
			Moria1.inven_command('d');
			break;
		case 'e':		/* (e)quipment list */
			Moria1.inven_command('e');
			break;
		case 't':		/* (t)hrow something	(f)ire something */
			Moria4.throw_object();
			break;
		case 'i':		/* (i)nventory list */
			Moria1.inven_command('i');
			break;
		case 'S':		/* (S)pike a door	(j)am a door */
			jamdoor();
			break;
		case 'x':		/* e(x)amine surrounds	(l)ook about */
			Moria4.look();
			Variable.free_turn_flag = true;
			break;
		case 'm':		/* (m)agic spells */
			Magic.cast();
			break;
		case 'o':		/* (o)pen something */
			Moria3.openobject();
			break;
		case 'p':		/* (p)ray */
			Prayer.pray();
			break;
		case 'q':		/* (q)uaff */
			Potions.quaff();
			break;
		case 'r':		/* (r)ead */
			Scrolls.read_scroll();
			break;
		case 's':		/* (s)earch for a turn */
			Moria2.search(Player.char_row, Player.char_col, Player.py.misc.srh);
			break;
		case 'T':		/* (T)ake off something	(t)ake off */
			Moria1.inven_command('t');
			break;
		case 'Z':		/* (Z)ap a staff	(u)se a staff */
			Staffs.use();
			break;
		case 'v':		/* (v)ersion of game */
			Files.helpfile(Config.MORIA_VER);
			Variable.free_turn_flag = true;
			break;
		case 'w':		/* (w)ear or wield */
			Moria1.inven_command('w');
			break;
		case 'X':		/* e(X)change weapons	e(x)change */
			Moria1.inven_command('x');
			break;
		default:
			if (Variable.wizard) {
				Variable.free_turn_flag = true; /* Wizard commands are free moves*/
				switch(com_val)
				{
				case (Constants.CTRL & 'A'):	/*^A = Cure all*/
					Spells.remove_curse();
					Spells.cure_blindness();
					Spells.cure_confusion();
					Spells.cure_poison();
					Spells.remove_fear();
					Misc3.res_stat(Constants.A_STR);
					Misc3.res_stat(Constants.A_INT);
					Misc3.res_stat(Constants.A_WIS);
					Misc3.res_stat(Constants.A_CON);
					Misc3.res_stat(Constants.A_DEX);
					Misc3.res_stat(Constants.A_CHR);
					f_ptr = Player.py.flags;
					if (f_ptr.slow > 1) {
						f_ptr.slow = 1;
					}
					if (f_ptr.image > 1) {
						f_ptr.image = 1;
					}
					break;
				case (Constants.CTRL & 'E'):	/*^E = wizchar */
					Wizard.change_character();
					IO.erase_line(Constants.MSG_LINE, 0);
				break;
				case (Constants.CTRL & 'F'):	/*^F = genocide*/
					Spells.mass_genocide();
					break;
				case (Constants.CTRL & 'G'):	/*^G = treasure*/
					if (Variable.command_count > 0) {
						i = Variable.command_count;
						Variable.command_count = 0;
					} else {
						i = 1;
					}
					Misc3.random_object(Player.char_row, Player.char_col, i);
					Misc1.prt_map();
					break;
				case (Constants.CTRL & 'D'):	/*^D = up/down */
					if (Variable.command_count > 0) {
						if (Variable.command_count > 99) {
							i = 0;
						} else {
							i = Variable.command_count;
						}
						Variable.command_count = 0;
					} else {
						IO.prt("Go to which level (0-99) ? ", 0, 0);
						i = -1;
						if ((tmp_str = IO.get_string(0, 27, 10)).length() > 0) {
							try {
								i = Integer.parseInt(tmp_str);
							} catch (NumberFormatException e) {
								System.err.println("tmp_str cannot be converted to an integer in Dungeon.do_command()");
								i = 0;
							}
						}
					}
					if (i > -1) {
						Variable.dun_level = i;
						if (Variable.dun_level > 99) {
							Variable.dun_level = 99;
						}
						Variable.new_level_flag = true;
					} else {
						IO.erase_line(Constants.MSG_LINE, 0);
					}
					break;
				case (Constants.CTRL & 'O'):	/*^O = objects */
					Files.print_objects();
					break;
				case '\\': /* \ wizard help */
					if (Variable.rogue_like_commands.value()) {
						Files.helpfile(Config.MORIA_WIZ_HELP);
					} else {
						Files.helpfile(Config.MORIA_OWIZ_HELP);
					}
					break;
				case (Constants.CTRL & 'I'):	/*^I = identify*/
					Spells.ident_spell();
					break;
				case '*':
					Wizard.wizard_light();
					break;
				case ':':
					Spells.map_area();
					break;
				case (Constants.CTRL & 'T'):	/*^T = teleport*/
					Misc3.teleport(100);
					break;
				case '+':
					if (Variable.command_count > 0) {
						Player.py.misc.exp = Variable.command_count;
						Variable.command_count = 0;
					} else if (Player.py.misc.exp == 0) {
						Player.py.misc.exp = 1;
					} else {
						Player.py.misc.exp = Player.py.misc.exp * 2;
					}
					Misc3.prt_experience();
					break;
				case '&':	/*& = summon  */
					y = new IntPointer(Player.char_row);
					x = new IntPointer(Player.char_col);
					Misc1.summon_monster(y, x, true);
					Creature.creatures(false);
					break;
				case '@':
					Wizard.wizard_create();
					break;
				default:
					if (Variable.rogue_like_commands.value()) {
						IO.prt("Type '?' or '\\' for help.", 0, 0);
					} else {
						IO.prt("Type '?' or ^H for help.", 0, 0);
					}
				}
			} else {
				IO.prt("Type '?' for help.", 0, 0);
				Variable.free_turn_flag = true;
			}
		}
		Variable.last_command = com_val;
	}
	
	/* Check whether this command will accept a count.     -CJS-  */
	public static boolean valid_countcommand(char c) {
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
	public static void regenhp(int percent) {
		PlayerMisc p_ptr;
		int new_chp, new_chp_frac;
		int old_chp;
		
		p_ptr = Player.py.misc;
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
			Misc3.prt_chp();
		}
	}
	
	/* Regenerate mana points				-RAK-	*/
	public static void regenmana(int percent) {
		PlayerMisc p_ptr;
		int new_mana, new_mana_frac;
		int old_cmana;
		
		p_ptr = Player.py.misc;
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
			Misc3.prt_cmana();
		}
	}
	
	/* Is an item an enchanted weapon or armor and we don't know?  -CJS- */
	/* only returns true if it is a good enchantment */
	public static boolean enchanted(InvenType t_ptr) {
		if (t_ptr.tval < Constants.TV_MIN_ENCHANT || t_ptr.tval > Constants.TV_MAX_ENCHANT || (t_ptr.flags & Constants.TR_CURSED) != 0) {
			return false;
		}
		if (Desc.known2_p(t_ptr)) {
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
	public static void examine_book() {
		LongPointer j;
		IntPointer i = new IntPointer(), k = new IntPointer();
		IntPointer item_val = new IntPointer();
		boolean flag;
		int[] spell_index = new int[31];
		InvenType i_ptr;
		SpellType s_ptr;
		
		if (!Misc3.find_range(Constants.TV_MAGIC_BOOK, Constants.TV_PRAYER_BOOK, i, k)) {
			IO.msg_print("You are not carrying any books.");
		} else if (Player.py.flags.blind > 0) {
			IO.msg_print("You can't see to read your spell book!");
		} else if (Moria1.no_light()) {
			IO.msg_print("You have no light to read by.");
		} else if (Player.py.flags.confused > 0) {
			IO.msg_print("You are too confused.");
		} else if (Moria1.get_item(item_val, "Which Book?", i.value(), k.value(), "", "")) {
			flag = true;
			i_ptr = Treasure.inventory[item_val.value()];
			if (Player.Class[Player.py.misc.pclass].spell == Constants.MAGE) {
				if (i_ptr.tval != Constants.TV_MAGIC_BOOK) {
					flag = false;
				}
			} else if (Player.Class[Player.py.misc.pclass].spell == Constants.PRIEST) {
				if (i_ptr.tval != Constants.TV_PRAYER_BOOK) {
					flag = false;
				}
			} else {
				flag = false;
			}
			
			if (!flag) {
				IO.msg_print("You do not understand the language.");
			} else {
				i.value(0);
				j = new LongPointer(Treasure.inventory[item_val.value()].flags);
				while (j.value() != 0) {
					k.value(Misc1.bit_pos(j));
					s_ptr = Player.magic_spell[Player.py.misc.pclass - 1][k.value()];
					if (s_ptr.slevel < 99) {
						spell_index[i.value()] = k.value();
						i.value(i.value() + 1);
					}
				}
				IO.save_screen();
				Misc3.print_spells(spell_index, i.value(), true, -1);
				IO.pause_line(0);
				IO.restore_screen();
			}
		}
	}
	
	/* Go up one level					-RAK-	*/
	public static void go_up() {
		CaveType c_ptr;
		boolean no_stairs = false;
		
		c_ptr = Variable.cave[Player.char_row][Player.char_col];
		if (c_ptr.tptr != 0) {
			if (Treasure.t_list[c_ptr.tptr].tval == Constants.TV_UP_STAIR) {
				Variable.dun_level--;
				Variable.new_level_flag = true;
				IO.msg_print("You enter a maze of up staircases.");
				IO.msg_print("You pass through a one-way door.");
			} else {
				no_stairs = true;
			}
		} else {
			no_stairs = true;
		}
		
		if (no_stairs) {
			IO.msg_print("I see no up staircase here.");
			Variable.free_turn_flag = true;
		}
	}
	
	/* Go down one level					-RAK-	*/
	public static void go_down() {
		CaveType c_ptr;
		boolean no_stairs = false;
		
		c_ptr = Variable.cave[Player.char_row][Player.char_col];
		if (c_ptr.tptr != 0) {
			if (Treasure.t_list[c_ptr.tptr].tval == Constants.TV_DOWN_STAIR) {
				Variable.dun_level++;
				Variable.new_level_flag = true;
				IO.msg_print("You enter a maze of down staircases.");
				IO.msg_print("You pass through a one-way door.");
			} else {
				no_stairs = true;
			}
		} else {
			no_stairs = true;
		}
		
		if (no_stairs) {
			IO.msg_print("I see no down staircase here.");
			Variable.free_turn_flag = true;
		}
	}
	
	/* Jam a closed door					-RAK-	*/
	public static void jamdoor() {
		IntPointer y, x, dir = new IntPointer();
		IntPointer i = new IntPointer(), j = new IntPointer();
		CaveType c_ptr;
		InvenType t_ptr, i_ptr;
		String tmp_str;
		
		Variable.free_turn_flag = true;
		y = new IntPointer(Player.char_row);
		x = new IntPointer(Player.char_col);
		if (Moria1.get_dir("", dir)) {
			Misc3.mmove(dir.value(), y, x);
			c_ptr = Variable.cave[y.value()][x.value()];
			if (c_ptr.tptr != 0) {
				t_ptr = Treasure.t_list[c_ptr.tptr];
				if (t_ptr.tval == Constants.TV_CLOSED_DOOR) {
					if (c_ptr.cptr == 0) {
						if (Misc3.find_range(Constants.TV_SPIKE, Constants.TV_NEVER, i, j)) {
							Variable.free_turn_flag = false;
							IO.count_msg_print("You jam the door with a spike.");
							if (t_ptr.p1 > 0) {
								t_ptr.p1 = -t_ptr.p1;	/* Make locked to stuck. */
							}
							/* Successive spikes have a progressively smaller effect.
							 * Series is: 0 20 30 37 43 48 52 56 60 64 67 70 ... */
							t_ptr.p1 -= 1 + 190 / (10 - t_ptr.p1);
							i_ptr = Treasure.inventory[i.value()];
							if (i_ptr.number > 1) {
								i_ptr.number--;
								Treasure.inven_weight -= i_ptr.weight;
							} else {
								Misc3.inven_destroy(i.value());
							}
						} else {
							IO.msg_print("But you have no spikes.");
						}
					} else {
						Variable.free_turn_flag = false;
						tmp_str = String.format("The %s is in your way!", Monsters.c_list[Monsters.m_list[c_ptr.cptr].mptr].name);
						IO.msg_print(tmp_str);
					}
				} else if (t_ptr.tval == Constants.TV_OPEN_DOOR) {
					IO.msg_print("The door must be closed first.");
				} else {
					IO.msg_print("That isn't a door!");
				}
			} else {
				IO.msg_print("That isn't a door!");
			}
		}
	}
	
	/* Refill the players lamp				-RAK-	*/
	public static void refill_lamp() {
		IntPointer i = new IntPointer(), j = new IntPointer();
		int k;
		InvenType i_ptr;
		
		Variable.free_turn_flag = true;
		k = Treasure.inventory[Constants.INVEN_LIGHT].subval;
		if (k != 0) {
			IO.msg_print("But you are not using a lamp.");
		} else if (!Misc3.find_range(Constants.TV_FLASK, Constants.TV_NEVER, i, j)) {
			IO.msg_print("You have no oil.");
		} else {
			Variable.free_turn_flag = false;
			i_ptr = Treasure.inventory[Constants.INVEN_LIGHT];
			i_ptr.p1 += Treasure.inventory[i.value()].p1;
			if (i_ptr.p1 > Constants.OBJ_LAMP_MAX) {
				i_ptr.p1 = Constants.OBJ_LAMP_MAX;
				IO.msg_print ("Your lamp overflows, spilling oil on the ground.");
				IO.msg_print("Your lamp is full.");
			} else if (i_ptr.p1 > Constants.OBJ_LAMP_MAX/2) {
				IO.msg_print ("Your lamp is more than half full.");
			} else if (i_ptr.p1 == Constants.OBJ_LAMP_MAX/2) {
				IO.msg_print ("Your lamp is half full.");
			} else {
				IO.msg_print ("Your lamp is less than half full.");
			}
			Desc.desc_remain(i.value());
			Misc3.inven_destroy(i.value());
		}
	}
}
