/*
 * Store2.java: store code, entering, command interpreter, buying, selling
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

import net.jonhopkins.moria.types.CharPointer;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenRecord;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.StoreOwnerType;
import net.jonhopkins.moria.types.StoreType;

public class Store2 {
	
	public static String[] comment1 = {
			"Done!",  "Accepted!",  "Fine.",  "Agreed!",  "Ok.",  "Taken!",
			"You drive a hard bargain, but taken.",
			"You'll force me bankrupt, but it's a deal.",  "Sigh.  I'll take it.",
			"My poor sick children may starve, but done!",  "Finally!  I accept.",
			"Robbed again.",  "A pleasure to do business with you!",
			"My spouse will skin me, but accepted."
	};
	
	public static String[] comment2a = {
			"%A2 is my final offer; take it or leave it.",
			"I'll give you no more than %A2.",
			"My patience grows thin.  %A2 is final."
	};
	
	public static String[] comment2b = {
			"%A1 for such a fine item?  HA!  No less than %A2.",
			"%A1 is an insult!  Try %A2 gold pieces.",
			"%A1?!?  You would rob my poor starving children?",
			"Why, I'll take no less than %A2 gold pieces.",
			"Ha!  No less than %A2 gold pieces.",
			"Thou knave!  No less than %A2 gold pieces.",
			"%A1 is far too little, how about %A2?",
			"I paid more than %A1 for it myself, try %A2.",
			"%A1?  Are you mad?!?  How about %A2 gold pieces?",
			"As scrap this would bring %A1.  Try %A2 in gold.",
			"May the fleas of 1000 orcs molest you.  I want %A2.",
			"My mother you can get for %A1, this costs %A2.",
			"May your chickens grow lips.  I want %A2 in gold!",
			"Sell this for such a pittance?  Give me %A2 gold.",
			"May the Balrog find you tasty!  %A2 gold pieces?",
			"Your mother was a Troll!  %A2 or I'll tell."
	};
	
	public static String[] comment3a = {
			"I'll pay no more than %A1; take it or leave it.",
			"You'll get no more than %A1 from me.",
			"%A1 and that's final."
	};
	
	public static String[] comment3b = {
			"%A2 for that piece of junk?  No more than %A1.",
			"For %A2 I could own ten of those.  Try %A1.",
			"%A2?  NEVER!  %A1 is more like it.",
			"Let's be reasonable. How about %A1 gold pieces?",
			"%A1 gold for that junk, no more.",
			"%A1 gold pieces and be thankful for it!",
			"%A1 gold pieces and not a copper more.",
			"%A2 gold?  HA!  %A1 is more like it.",  "Try about %A1 gold.",
			"I wouldn't pay %A2 for your children, try %A1.",
			"*CHOKE* For that!?  Let's say %A1.",  "How about %A1?",
			"That looks war surplus!  Say %A1 gold.",
			"I'll buy it as scrap for %A1.",
			"%A2 is too much, let us say %A1 gold."
	};
	
	public static String[] comment4a = {
			"ENOUGH!  You have abused me once too often!",
			"THAT DOES IT!  You shall waste my time no more!",
			"This is getting nowhere.  I'm going home!",
			"BAH!  No more shall you insult me!",
			"Begone!  I have had enough abuse for one day."
	};
	
	public static String[] comment4b = {
			"Out of my place!",  "out... Out... OUT!!!",  "Come back tomorrow.",
			"Leave my place.  Begone!",  "Come back when thou art richer."
	};
	
	public static String[] comment5 = {
			"You will have to do better than that!",  "That's an insult!",
			"Do you wish to do business or not?",  "Hah!  Try again.",
			"Ridiculous!",  "You've got to be kidding!",  "You'd better be kidding!",
			"You try my patience.",  "I don't hear you.",
			"Hmmm, nice weather we're having."
	};
	
	public static String[] comment6 = {
			"I must have heard you wrong.",  "What was that?",
			"I'm sorry, say that again.",  "What did you say?",
			"Sorry, what was that again?"
	};
	
	public static int last_store_inc;
	
	private Store2() { }
	
	/**
	 * Comments vary. Comment one : Finished haggling. -RAK-
	 */
	public static void prt_comment1() {
		IO.msg_print(comment1[Misc1.randint(14) - 1]);
	}
	
	/**
	 * %A1 is offer, %A2 is asking.
	 * 
	 * @param offer - Player's offer price
	 * @param asking - Store's asking price
	 * @param final_ - If greater than 0, at final offer before store owner kicks player out
	 */
	public static void prt_comment2(int offer, int asking, int final_) {
		String comment;
		
		if (final_ > 0) {
			comment = comment2a[Misc1.randint(3) - 1];
		} else {
			comment = comment2b[Misc1.randint(16) - 1];
		}
		
		comment = Misc3.insert_lnum(comment, "%A1", offer, false);
		comment = Misc3.insert_lnum(comment, "%A2", asking, false);
		IO.msg_print(comment);
	}
	
	/**
	 * Player trying to sell for too high of a price.
	 * 
	 * @param offer - Store owner's offer
	 * @param asking - Player's asking price
	 * @param final_ - If greater than 0, at final offer before store owner kicks player out
	 */
	public static void prt_comment3(int offer, int asking, int final_) {
		String comment;
		
		if (final_ > 0) {
			comment = comment3a[Misc1.randint(3) - 1];
		} else {
			comment = comment3b[Misc1.randint(15) - 1];
		}
		
		comment = Misc3.insert_lnum(comment, "%A1", offer, false);
		comment = Misc3.insert_lnum(comment, "%A2", asking, false);
		IO.msg_print(comment);
	}
	
	/**
	 * Kick 'da bum out. -RAK-
	 */
	public static void prt_comment4() {
		int tmp;
		
		tmp = Misc1.randint(5) - 1;
		IO.msg_print(comment4a[tmp]);
		IO.msg_print(comment4b[tmp]);
	}
	
	/**
	 * Bad offer, store owner is insulted.
	 */
	public static void prt_comment5() {
		IO.msg_print(comment5[Misc1.randint(10) - 1]);
	}
	
	/**
	 * Store owner questioning player's offer.
	 */
	public static void prt_comment6() {
		IO.msg_print(comment6[Misc1.randint(5) - 1]);
	}
	
	/**
	 * Displays the set of commands. -RAK-
	 */
	public static void display_commands() {
		IO.prt("You may:", 20, 0);
		IO.prt(" p) Purchase an item.           b) Browse store's inventory.", 21, 0);
		IO.prt(" s) Sell an item.               i/e/t/w/x) Inventory/Equipment Lists.", 22, 0);
		IO.prt("ESC) Exit from Building.        ^R) Redraw the screen.", 23, 0);
	}
	
	/**
	 * Displays the set of commands. -RAK-
	 * 
	 * @param typ - Specifies what the player is doing, -1 if selling, otherwise buying
	 */
	public static void haggle_commands(int typ) {
		if (typ == -1) {
			IO.prt("Specify an asking-price in gold pieces.", 21, 0);
		} else {
			IO.prt("Specify an offer in gold pieces.", 21, 0);
		}
		IO.prt("ESC) Quit Haggling.", 22, 0);
		IO.erase_line(23, 0);	/* clear last line */
	}
	
	/**
	 * Displays a store's inventory. -RAK-
	 * 
	 * @param store_num - Store number
	 * @param start - Where in the store's inventory to start listing items
	 */
	public static void display_inventory(int store_num, int start) {
		StoreType s_ptr;
		InvenType i_ptr;
		int i, j, stop;
		String out_val1, out_val2;
		int x;
		
		s_ptr = Variable.store[store_num];
		i = (start % 12);
		stop = ((start / 12) + 1) * 12;
		if (stop > s_ptr.store_ctr)	stop = s_ptr.store_ctr;
		while (start < stop) {
			i_ptr = s_ptr.store_inven[start].sitem;
			x = i_ptr.number;
			if ((i_ptr.subval >= Constants.ITEM_SINGLE_STACK_MIN) && (i_ptr.subval <= Constants.ITEM_SINGLE_STACK_MAX)) {
				i_ptr.number = 1;
			}
			out_val1 = Desc.objdes(i_ptr, true);
			i_ptr.number = x;
			out_val2 = String.format("%c) %s", 'a' + i, out_val1);
			IO.prt(out_val2, i + 5, 0);
			x = s_ptr.store_inven[start].scost;
			if (x <= 0) {
				int value = -x;
				value = value * Misc3.chr_adj() / 100;
				if (value <= 0) {
					value = 1;
				}
				out_val2 = String.format("%9d", value);
			} else {
				out_val2 = String.format("%9d [Fixed]", x);
			}
			IO.prt(out_val2, i+5, 59);
			i++;
			start++;
		}
		if (i < 12) {
			for (j = 0; j < (11 - i + 1); j++) {
				IO.erase_line(j + i + 5, 0); /* clear remaining lines */
			}
		}
		if (s_ptr.store_ctr > 12) {
			IO.put_buffer("- cont. -", 17, 60);
		} else {
			IO.erase_line(17, 60);
		}
	}
	
	/**
	 * Re-displays only a single cost. -RAK-
	 * 
	 * @param store_num - Store number
	 * @param pos - Position in the store's inventory for which to display the cost
	 */
	public static void display_cost(int store_num, int pos) {
		int i;
		int j;
		String out_val;
		StoreType s_ptr;
		
		s_ptr = Variable.store[store_num];
		i = (pos % 12);
		if (s_ptr.store_inven[pos].scost < 0) {
			j = - s_ptr.store_inven[pos].scost;
			j = j * Misc3.chr_adj() / 100;
			out_val = String.format("%d", j);
		} else {
			out_val = String.format("%9d [Fixed]", s_ptr.store_inven[pos].scost);
		}
		IO.prt(out_val, i+5, 59);
	}
	
	/**
	 * Displays player's gold. -RAK-
	 */
	public static void store_prt_gold() {
		String out_val;
		
		out_val = String.format("Gold Remaining : %d", Player.py.misc.au);
		IO.prt(out_val, 18, 17);
	}
	
	/**
	 * Displays store. -RAK-
	 * 
	 * @param store_num - Store number
	 * @param cur_top - Position in store's inventory currently at top of display
	 */
	public static void display_store(int store_num, int cur_top) {
		StoreType s_ptr;
		
		s_ptr = Variable.store[store_num];
		IO.clear_screen();
		IO.put_buffer(Tables.owners[s_ptr.owner].owner_name, 3, 9);
		IO.put_buffer("Item", 4, 3);
		IO.put_buffer("Asking Price", 4, 60);
		store_prt_gold();
		display_commands();
		display_inventory(store_num, cur_top);
	}
	
	/**
	 * Get the ID of a store item and return it's value. -RAK-
	 * 
	 * @param com_val - Stores player commands
	 * @param pmt - Display message
	 * @param i - Letter of the first item in the list
	 * @param j - Letter of the last item in the list
	 * @return True if player selected an item to purchase
	 */
	public static boolean get_store_item(IntPointer com_val, String pmt, int i, int j) {
		CharPointer command = new CharPointer();
		String out_val;
		boolean flag;
		
		com_val.value(-1);
		flag = false;
		out_val = String.format("(Items %c-%c, ESC to exit) %s", i + 'a', j + 'a', pmt);
		while (IO.get_com(out_val, command)) {
			command.value((char)(command.value() - 'a'));
			if (command.value() >= i && command.value() <= j) {
				flag = true;
				com_val.value(command.value());
				break;
			}
			IO.bell();
		}
		IO.erase_line(Constants.MSG_LINE, 0);
		return flag;
	}
	
	/**
	 * Increase the insult counter and get angry if too many. -RAK-
	 * 
	 * @param store_num - Store number
	 * @return True if the store owner is angry
	 */
	public static boolean increase_insults(int store_num) {
		boolean increase;
		StoreType s_ptr;
		
		increase = false;
		s_ptr = Variable.store[store_num];
		s_ptr.insult_cur++;
		if (s_ptr.insult_cur > Tables.owners[s_ptr.owner].insult_max) {
			prt_comment4();
			s_ptr.insult_cur = 0;
			s_ptr.bad_buy++;
			s_ptr.store_open = Variable.turn + 2500 + Misc1.randint(2500);
			increase = true;
		}
		return increase;
	}
	
	/**
	 * Decrease insults. -RAK-
	 * 
	 * @param store_num - Store number
	 */
	public static void decrease_insults(int store_num) {
		StoreType s_ptr;
		
		s_ptr = Variable.store[store_num];
		if (s_ptr.insult_cur != 0) {
			s_ptr.insult_cur--;
		}
	}
	
	/**
	 * Have insulted while haggling. -RAK-
	 * 
	 * @param store_num - Store number
	 * @return True if offer insults store owner
	 */
	public static boolean haggle_insults(int store_num) {
		boolean haggle;
		
		haggle = false;
		if (increase_insults(store_num)) {
			haggle = true;
		} else {
			prt_comment5();
			IO.msg_print(""); /* keep insult separate from rest of haggle */
		}
		return haggle;
	}
	
	/**
	 * Haggle with the store owner.
	 * 
	 * @param comment - Store owner's response to player's offer
	 * @param new_offer - Store's the player's newest offer
	 * @param num_offer - How many offers have been made so far
	 * @return True if the player successfully purchased an item
	 */
	public static boolean get_haggle(String comment, IntPointer new_offer, boolean num_offer) {
		int i;
		String out_val, default_offer;
		boolean flag;
		int clen;
		int orig_clen;
		int p;
		boolean increment;
		
		flag = true;
		increment = false;
		clen = comment.length();
		orig_clen = clen;
		if (!num_offer) {
			last_store_inc = 0;
		}
		i = 0;
		do {
			IO.prt(comment, 0, 0);
			if (num_offer && last_store_inc != 0) {
				default_offer = String.format("[%c%d] ", (last_store_inc < 0) ? '-' : '+', Math.abs(last_store_inc));
				IO.prt(default_offer, 0, orig_clen);
				clen = orig_clen + default_offer.length();
			}
			out_val = IO.get_string(0, clen, 40);
			if (out_val.equals("")) {
				flag = false;
				break;
			}
			for (p = 0; out_val.charAt(p) == ' '; p++) {
				;
			}
			if (out_val.charAt(p) == '+' || out_val.charAt(p) == '-') {
				increment = true;
			}
			if (num_offer && increment) {
				try {
					i = Integer.parseInt(out_val);
				} catch (NumberFormatException e) {
					System.err.println("Could not convert out_val to an integer in Store2.get_haggle()");
					i = 0;
				}
				/* Don't accept a zero here.  Turn off increment if it was zero
				 * because a zero will not exit.  This can be zero if the user
				 * did not type a number after the +/- sign.  */
				if (i == 0) {
					increment = false;
				} else {
					last_store_inc = i;
				}
			} else if (num_offer && out_val.equals("")) {
				i = last_store_inc;
				increment = true;
			} else {
				try {
					i = Integer.parseInt(out_val);
				} catch (NumberFormatException e) {
					System.err.println("Could not convert out_val to an integer in Store2.get_haggle()");
					i = 0;
				}
			}
			
			/* don't allow incremental haggling, if player has not made an offer yet */
			if (flag && !num_offer && increment) {
				IO.msg_print("You haven't even made your first offer yet!");
				i = 0;
				increment = false;
			}
		} while (flag && (i == 0));
		
		if (flag) {
			if (increment) {
				new_offer.value(new_offer.value() + i);
			} else {
				new_offer.value(i);
			}
		} else {
			IO.erase_line(0, 0);
		}
		return flag;
	}
	
	/**
	 * Player makes an offer.
	 * 
	 * @param store_num - Store number
	 * @param comment - 
	 * @param new_offer - Stores the player's newest offer
	 * @param last_offer - The player's last offer
	 * @param num_offer - Number of offers made so far
	 * @param factor - 
	 * @return
	 */
	public static int receive_offer(int store_num, String comment, IntPointer new_offer, int last_offer, boolean num_offer, int factor) {
		boolean flag;
		int receive;
		
		receive = 0;
		flag = false;
		do {
			if (get_haggle(comment, new_offer, num_offer)) {
				if (new_offer.value() * factor >= last_offer * factor) {
					flag = true;
				} else if (haggle_insults(store_num)) {
					receive = 2;
					flag = true;
				} else {
					/* new_offer rejected, reset new_offer so that incremental
					 * haggling works correctly */
					new_offer.value(last_offer);
				}
			} else {
				receive = 1;
				flag = true;
			}
		} while (!flag);
		
		return receive;
	}
	
	/**
	 * Haggling routine. -RAK-
	 * 
	 * @param store_num - Store number
	 * @param price - Stores the price of the item
	 * @param item - The item being purchased
	 * @return 
	 */
	public static int purchase_haggle(int store_num, IntPointer price, InvenType item) {
		IntPointer max_sell = new IntPointer(), min_sell = new IntPointer();
		int max_buy;
		int cost, cur_ask, final_ask, min_offer;
		int last_offer;
		IntPointer new_offer = new IntPointer();
		int x1, x2, x3;
		int min_per, max_per;
		boolean flag, loop_flag;
		String comment;
		String out_val;
		int purchase, final_flag;
		boolean num_offer;
		boolean didnt_haggle;
		StoreType s_ptr;
		StoreOwnerType o_ptr;
		
		flag = false;
		purchase = 0;
		price.value(0);
		final_flag = 0;
		didnt_haggle = false;
		s_ptr = Variable.store[store_num];
		o_ptr = Tables.owners[s_ptr.owner];
		cost = Store1.sell_price(store_num, max_sell, min_sell, item);
		max_sell.value(max_sell.value() * Misc3.chr_adj() / 100);
		if (max_sell.value() <= 0)	max_sell.value(1);
		min_sell.value(min_sell.value() * Misc3.chr_adj() / 100);
		if (min_sell.value() <= 0)	min_sell.value(1);
		/* cast max_inflate to signed so that subtraction works correctly */
		max_buy = cost * (200 - o_ptr.max_inflate) / 100;
		if (max_buy <= 0) max_buy = 1;
		min_per  = o_ptr.haggle_per;
		max_per  = min_per * 3;
		haggle_commands(1);
		cur_ask   = max_sell.value();
		final_ask = min_sell.value();
		min_offer = max_buy;
		last_offer = min_offer;
		new_offer.value(0);
		num_offer = false; /* this prevents incremental haggling on first try */
		comment = "Asking";
		
		/* go right to final price if player has bargained well */
		if (Store1.noneedtobargain(store_num, final_ask)) {
			IO.msg_print("After a long bargaining session, you agree upon the price.");
			cur_ask = min_sell.value();
			comment = "Final offer";
			didnt_haggle = true;
			
			/* Set up automatic increment, so that a return will accept the
			 * final price.  */
			last_store_inc = min_sell.value();
			num_offer = true;
		}
		
		do {
			do {
				loop_flag = true;
				out_val = String.format("%s :  %d", comment, cur_ask);
				IO.put_buffer(out_val, 1, 0);
				purchase = receive_offer(store_num, "What do you offer? ", new_offer, last_offer, num_offer, 1);
				if (purchase != 0) {
					flag = true;
				} else {
					if (new_offer.value() > cur_ask) {
						prt_comment6();
						/* rejected, reset new_offer for incremental haggling */
						new_offer.value(last_offer);
						
						/* If the automatic increment is large enough to overflow,
						 * then the player must have made a mistake.  Clear it
						 * because it is useless.  */
						if (last_offer + last_store_inc > cur_ask) {
							last_store_inc = 0;
						}
					} else if (new_offer.value() == cur_ask) {
						flag = true;
						price.value(new_offer.value());
					} else {
						loop_flag = false;
					}
				}
			} while (!flag && loop_flag);
			
			if (!flag) {
				x1 = (new_offer.value() - last_offer) * 100 / (cur_ask - last_offer);
				if (x1 < min_per) {
					flag = haggle_insults(store_num);
					if (flag)	purchase = 2;
				} else if (x1 > max_per) {
					x1 = x1 * 75 / 100;
					if (x1 < max_per)	x1 = max_per;
				}
				x2 = x1 + Misc1.randint(5) - 3;
				x3 = ((cur_ask - new_offer.value()) * x2 / 100) + 1;
				/* don't let the price go up */
				if (x3 < 0) {
					x3 = 0;
				}
				cur_ask -= x3;
				if (cur_ask < final_ask) {
					cur_ask = final_ask;
					comment = "Final Offer";
					/* Set the automatic haggle increment so that RET will give
					 * a new_offer equal to the final_ask price.  */
					last_store_inc = final_ask - new_offer.value();
					final_flag++;
					if (final_flag > 3) {
						if (increase_insults(store_num)) {
							purchase = 2;
						} else {
							purchase = 1;
						}
						flag = true;
					}
				} else if (new_offer.value() >= cur_ask) {
					flag = true;
					price.value(new_offer.value());
				}
				if (!flag) {
					last_offer = new_offer.value();
					num_offer = true; /* enable incremental haggling */
					IO.erase_line(1, 0);
					out_val = String.format("Your last offer : %d", last_offer);
					IO.put_buffer(out_val, 1, 39);
					prt_comment2(last_offer, cur_ask, final_flag);
					
					/* If the current increment would take you over the store's
					 * price, then decrease it to an exact match.  */
					if (cur_ask - last_offer < last_store_inc) {
						last_store_inc = cur_ask - last_offer;
					}
				}
			}
		} while (!flag);
		
		/* update bargaining info */
		if ((purchase == 0) && (!didnt_haggle)) {
			Store1.updatebargain(store_num, price.value(), final_ask);
		}
		
		return purchase;
	}
	
	/**
	 * Haggling routine -RAK-
	 * 
	 * @param store_num - Store number
	 * @param price - Stores the price
	 * @param item - The item being sold
	 * @return 
	 */
	public static int sell_haggle(int store_num, IntPointer price, InvenType item) {
		int max_sell = 0, max_buy = 0, min_buy = 0;
		int cost, cur_ask, final_ask = 0, min_offer;
		int last_offer;
		IntPointer new_offer = new IntPointer();
		int max_gold = 0;
		int x1, x2, x3;
		int min_per = 0, max_per = 0;
		boolean flag, loop_flag;
		String comment;
		String out_val;
		StoreType s_ptr;
		StoreOwnerType o_ptr;
		int sell, final_flag;
		boolean num_offer;
		boolean didnt_haggle;
		
		flag = false;
		sell = 0;
		price.value(0);
		final_flag = 0;
		didnt_haggle = false;
		s_ptr = Variable.store[store_num];
		cost = Store1.item_value(item);
		if (cost < 1) {
			sell = 3;
			flag = true;
		} else {
			o_ptr = Tables.owners[s_ptr.owner];
			cost = cost * (200 - Misc3.chr_adj()) / 100;
			cost = cost * (200 - Tables.rgold_adj[o_ptr.owner_race][Player.py.misc.prace]) / 100;
			if (cost < 1)  cost = 1;
			max_sell = cost * o_ptr.max_inflate / 100;
			/* cast max_inflate to signed so that subtraction works correctly */
			max_buy  = cost * (200 - o_ptr.max_inflate) / 100;
			min_buy  = cost * (200 - o_ptr.min_inflate) / 100;
			if (min_buy < 1) min_buy = 1;
			if (max_buy < 1) max_buy = 1;
			if (min_buy < max_buy)  min_buy = max_buy;
			min_per  = o_ptr.haggle_per;
			max_per  = min_per * 3;
			max_gold = o_ptr.max_cost;
		}
		if (!flag) {
			haggle_commands(-1);
			num_offer = false; /* this prevents incremental haggling on first try */
			if (max_buy > max_gold) {
				final_flag= 1;
				comment = "Final Offer";
				/* Disable the automatic haggle increment on RET.  */
				last_store_inc = 0;
				cur_ask   = max_gold;
				final_ask = max_gold;
				IO.msg_print("I am sorry, but I have not the money to afford such a fine item.");
				didnt_haggle = true;
			} else {
				cur_ask   = max_buy;
				final_ask = min_buy;
				if (final_ask > max_gold) {
					final_ask = max_gold;
				}
				comment = "Offer";
				
				/* go right to final price if player has bargained well */
				if (Store1.noneedtobargain(store_num, final_ask)) {
					IO.msg_print("After a long bargaining session, you agree upon the price.");
					cur_ask = final_ask;
					comment = "Final offer";
					didnt_haggle = true;
					
					/* Set up automatic increment, so that a return will accept the
					 * final price.  */
					last_store_inc = final_ask;
					num_offer = true;
				}
			}
			min_offer = max_sell;
			last_offer = min_offer;
			new_offer.value(0);
			if (cur_ask < 1)	cur_ask = 1;
			do {
				do {
					loop_flag = true;
					out_val = String.format("%s :  %d", comment, cur_ask);
					IO.put_buffer(out_val, 1, 0);
					sell = receive_offer(store_num, "What price do you ask? ", new_offer, last_offer, num_offer, -1);
					if (sell != 0) {
						flag   = true;
					} else {
						if (new_offer.value() < cur_ask) {
							prt_comment6();
							/* rejected, reset new_offer for incremental haggling */
							new_offer.value(last_offer);
							
							/* If the automatic increment is large enough to
							 * overflow, then the player must have made a mistake.
							 * Clear it because it is useless.  */
							if (last_offer + last_store_inc < cur_ask) {
								last_store_inc = 0;
							}
						} else if (new_offer.value() == cur_ask) {
							flag = true;
							price.value(new_offer.value());
						} else {
							loop_flag = false;
						}
					}
				} while (!flag && loop_flag);
				
				if (!flag) {
					x1 = (last_offer - new_offer.value()) * 100 / (last_offer - cur_ask);
					if (x1 < min_per) {
						flag = haggle_insults(store_num);
						if (flag)	sell = 2;
					} else if (x1 > max_per) {
						x1 = x1 * 75 / 100;
						if (x1 < max_per)	x1 = max_per;
					}
					x2 = x1 + Misc1.randint(5) - 3;
					x3 = ((new_offer.value() - cur_ask) * x2 / 100) + 1;
					/* don't let the price go down */
					if (x3 < 0) {
						x3 = 0;
					}
					cur_ask += x3;
					if (cur_ask > final_ask) {
						cur_ask = final_ask;
						comment = "Final Offer";
						/* Set the automatic haggle increment so that RET will give
						 * a new_offer equal to the final_ask price.  */
						last_store_inc = final_ask - new_offer.value();
						final_flag++;
						if (final_flag > 3) {
							if (increase_insults(store_num)) {
								sell = 2;
							} else {
								sell = 1;
							}
							flag = true;
						}
					} else if (new_offer.value() <= cur_ask) {
						flag = true;
						price.value(new_offer.value());
					}
					if (!flag) {
						last_offer = new_offer.value();
						num_offer = true; /* enable incremental haggling */
						IO.erase_line(1, 0);
						out_val = String.format("Your last bid %d", last_offer);
						IO.put_buffer(out_val, 1, 39);
						prt_comment3(cur_ask, last_offer, final_flag);
						
						/* If the current decrement would take you under the store's
						 * price, then increase it to an exact match.  */
						if (cur_ask - last_offer > last_store_inc) {
							last_store_inc = cur_ask - last_offer;
						}
					}
				}
			} while (!flag);
		}
		
		/* update bargaining info */
		if ((sell == 0) && (!didnt_haggle)) {
			Store1.updatebargain(store_num, price.value(), final_ask);
		}
		
		return sell;
	}
	
	/**
	 * Buy an item from a store. -RAK-
	 * 
	 * @param store_num - Store number
	 * @param cur_top - Stores the store inventory position of the first item being displayed
	 * @return True if bought an item
	 */
	public static boolean store_purchase(int store_num, IntPointer cur_top) {
		IntPointer price = new IntPointer();
		int i, choice;
		String out_val, tmp_str;
		StoreType s_ptr;
		InvenType sell_obj = new InvenType();
		InvenRecord r_ptr;
		IntPointer item_val = new IntPointer();
		int item_new;
		boolean purchase;
		
		purchase = false;
		s_ptr = Variable.store[store_num];
		/* i == number of objects shown on screen	*/
		if (cur_top.value() == 12) {
			i = s_ptr.store_ctr - 1 - 12;
		} else if (s_ptr.store_ctr > 11) {
			i = 11;
		} else {
			i = s_ptr.store_ctr - 1;
		}
		if (s_ptr.store_ctr < 1) {
			IO.msg_print("I am currently out of stock.");
		
		/* Get the item number to be bought		*/
		} else if (get_store_item(item_val, "Which item are you interested in? ", 0, i)) {
			item_val.value(item_val.value() + cur_top.value());	/* true item_val	*/
			Misc3.take_one_item(sell_obj, s_ptr.store_inven[item_val.value()].sitem);
			if (Misc3.inven_check_num(sell_obj)) {
				if (s_ptr.store_inven[item_val.value()].scost > 0) {
					price.value(s_ptr.store_inven[item_val.value()].scost);
					choice = 0;
				} else {
					choice = purchase_haggle(store_num, price, sell_obj);
				}
				if (choice == 0) {
					if (Player.py.misc.au >= price.value()) {
						prt_comment1();
						decrease_insults(store_num);
						Player.py.misc.au -= price.value();
						item_new = Misc3.inven_carry(sell_obj);
						i = s_ptr.store_ctr;
						Store1.store_destroy(store_num, item_val.value(), true);
						tmp_str = Desc.objdes(Treasure.inventory[item_new], true);
						out_val = String.format("You have %s (%c)", tmp_str, item_new + 'a');
						IO.prt(out_val, 0, 0);
						Misc3.check_strength();
						if (cur_top.value() >= s_ptr.store_ctr) {
							cur_top.value(0);
							display_inventory(store_num, cur_top.value());
						} else {
							r_ptr = s_ptr.store_inven[item_val.value()];
							if (i == s_ptr.store_ctr) {
								if (r_ptr.scost < 0) {
									r_ptr.scost = price.value();
									display_cost(store_num, item_val.value());
								}
							} else {
								display_inventory(store_num, item_val.value());
							}
						}
						store_prt_gold();
					} else {
						if (increase_insults(store_num)) {
							purchase = true;
						} else {
							prt_comment1();
							IO.msg_print("Liar!  You have not the gold!");
						}
					}
				} else if (choice == 2) {
					purchase = true;
				}
				
				/* Less intuitive, but looks better here than in purchase_haggle. */
				display_commands();
				IO.erase_line(1, 0);
			} else {
				IO.prt("You cannot carry that many different items.", 0, 0);
			}
		}
		return purchase;
	}
	
	/**
	 * Check if the store will buy a particular item.
	 * 
	 * @param store_num - Store number
	 * @param inv - Item being sold to the store
	 * @return True if the store will buy the item
	 */
	private static boolean store_buy(int store_num, int inv) {
		switch(store_num) {
		case Sets.general_store:
			return Sets.general_store(inv);
		case Sets.armory:
			return Sets.armory(inv);
		case Sets.weaponsmith:
			return Sets.weaponsmith(inv);
		case Sets.temple:
			return Sets.temple(inv);
		case Sets.alchemist:
			return Sets.alchemist(inv);
		case Sets.magic_shop:
			return Sets.magic_shop(inv);
		default:
			return false;
		}
	}
	
	/**
	 * Sell an item to the store -RAK-
	 * 
	 * @param store_num - Store number
	 * @param cur_top - Stores the store inventory position of the first item being displayed
	 * @return True if sold an item
	 */
	public static boolean store_sell(int store_num, IntPointer cur_top) {
		IntPointer item_val = new IntPointer();
		IntPointer item_pos = new IntPointer();
		IntPointer price = new IntPointer();
		String out_val, tmp_str;
		InvenType sold_obj = new InvenType();
		boolean sell;
		int choice;
		boolean flag;
		char[] mask = new char[Constants.INVEN_WIELD];
		int counter, first_item, last_item;
		sell = false;
		first_item = Treasure.inven_ctr;
		last_item = -1;
		for (counter = 0; counter < Treasure.inven_ctr; counter++) {
			flag = store_buy(store_num, Treasure.inventory[counter].tval);
			mask[counter] = (char)(flag ? 1 : 0);
			if (flag) {
				if (counter < first_item) {
					first_item = counter;
				}
				if (counter > last_item) {
					last_item = counter;
				}
			} /* end of if (flag) */
		} /* end of for (counter) */
		if (last_item == -1) {
			IO.msg_print("You have nothing to sell to this store!");
		} else if (Moria1.get_item(item_val, "Which one? ", first_item, last_item, new String(mask), "I do not buy such items.")) {
			Misc3.take_one_item(sold_obj, Treasure.inventory[item_val.value()]);
			tmp_str = Desc.objdes(sold_obj, true);
			out_val = String.format("Selling %s (%c)", tmp_str, item_val.value() + 'a');
			IO.msg_print(out_val);
			if (Store1.store_check_num(sold_obj, store_num)) {
				choice = sell_haggle(store_num, price, sold_obj);
				if (choice == 0) {
					prt_comment1();
					decrease_insults(store_num);
					Player.py.misc.au += price.value();
					/* identify object in inventory to set object_ident */
					Desc.identify(item_val);
					/* retake sold_obj so that it will be identified */
					Misc3.take_one_item(sold_obj, Treasure.inventory[item_val.value()]);
					/* call known2 for store item, so charges/pluses are known */
					Desc.known2(sold_obj);
					Misc3.inven_destroy(item_val.value());
					tmp_str = Desc.objdes(sold_obj, true);
					out_val = String.format("You've sold %s", tmp_str);
					IO.msg_print(out_val);
					Store1.store_carry(store_num, item_pos, sold_obj);
					Misc3.check_strength();
					if (item_pos.value() >= 0) {
						if (item_pos.value() < 12) {
							if (cur_top.value() < 12) {
								display_inventory(store_num, item_pos.value());
							} else {
								cur_top.value(0);
								display_inventory(store_num, cur_top.value());
							}
						} else if (cur_top.value() > 11) {
							display_inventory(store_num, item_pos.value());
						} else {
							cur_top.value(12);
							display_inventory(store_num, cur_top.value());
						}
					}
					store_prt_gold();
				} else if (choice == 2) {
					sell = true;
				} else if (choice == 3) {
					IO.msg_print("How dare you!");
					IO.msg_print("I will not buy that!");
					sell = increase_insults(store_num);
				}
				/* Less intuitive, but looks better here than in sell_haggle. */
				IO.erase_line(1, 0);
				display_commands();
			} else {
				IO.msg_print("I have not the room in my store to keep it.");
			}
		}
		return sell;
	}
	
	/**
	 * Entering a store. -RAK-
	 * 
	 * @param store_num - Store number
	 */
	public static void enter_store(int store_num) {
		IntPointer cur_top = new IntPointer(0);
		int tmp_chr;
		CharPointer command = new CharPointer();
		boolean exit_flag;
		StoreType s_ptr;
		
		s_ptr = Variable.store[store_num];
		
		if (s_ptr.store_open < Variable.turn) {
			exit_flag = false;
			cur_top.value(0);
			display_store(store_num, cur_top.value());
			do {
				IO.move_cursor(20, 9);
				/* clear the msg flag just like we do in dungeon.java */
				Variable.msg_flag = 0;
				if (IO.get_com("", command)) {
					switch(command.value())
					{
					case 'b':
						if (cur_top.value() == 0) {
							if (s_ptr.store_ctr > 12) {
								cur_top.value(12);
								display_inventory(store_num, cur_top.value());
							} else {
								IO.msg_print("Entire inventory is shown.");
							}
						} else {
							cur_top.value(0);
							display_inventory(store_num, cur_top.value());
						}
						break;
					case 'E': case 'e':	 /* Equipment List	*/
					case 'I': case 'i':	 /* Inventory		*/
					case 'T': case 't':	 /* Take off		*/
					case 'W': case 'w':	/* Wear			*/
					case 'X': case 'x':	/* Switch weapon		*/
						tmp_chr = Player.py.stats.use_stat[Constants.A_CHR];
						do {
							Moria1.inven_command(command.value());
							command.value(Variable.doing_inven);
						} while (command.value() != '\0');
						/* redisplay store prices if charisma changes */
						if (tmp_chr != Player.py.stats.use_stat[Constants.A_CHR]) {
							display_inventory(store_num, cur_top.value());
						}
						Variable.free_turn_flag = false;	/* No free moves here. -CJS- */
						break;
					case 'p':
						exit_flag = store_purchase(store_num, cur_top);
						break;
					case 's':
						exit_flag = store_sell(store_num, cur_top);
						break;
					default:
						IO.bell();
						break;
					}
				} else {
					exit_flag = true;
				}
			} while (!exit_flag);
			/* Can't save and restore the screen because inven_command does that. */
			Misc3.draw_cave();
		} else {
			IO.msg_print("The doors are locked.");
		}
	}
}
