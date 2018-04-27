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
			"Done!", "Accepted!", "Fine.", "Agreed!", "Ok.", "Taken!",
			"You drive a hard bargain, but taken.",
			"You'll force me bankrupt, but it's a deal.", "Sigh.  I'll take it.",
			"My poor sick children may starve, but done!", "Finally!  I accept.",
			"Robbed again.", "A pleasure to do business with you!",
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
			"%A2 gold?  HA!  %A1 is more like it.", "Try about %A1 gold.",
			"I wouldn't pay %A2 for your children, try %A1.",
			"*CHOKE* For that!?  Let's say %A1.", "How about %A1?",
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
			"Out of my place!", "out... Out... OUT!!!", "Come back tomorrow.",
			"Leave my place.  Begone!", "Come back when thou art richer."
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
	
	public static int lastStoreInc;
	
	private Store2() { }
	
	/**
	 * Comments vary. Comment one : Finished haggling. -RAK-
	 */
	public static void printComment1() {
		IO.printMessage(comment1[Rnd.randomInt(14) - 1]);
	}
	
	/**
	 * %A1 is offer, %A2 is asking.
	 * 
	 * @param offer player's offer price
	 * @param asking store's asking price
	 * @param finalChance if true, at final offer before store owner kicks player out
	 */
	public static void printComment2(int offer, int asking, boolean finalChance) {
		String comment;
		
		if (finalChance) {
			comment = comment2a[Rnd.randomInt(3) - 1];
		} else {
			comment = comment2b[Rnd.randomInt(16) - 1];
		}
		
		comment = Misc3.insertLong(comment, "%A1", offer, false);
		comment = Misc3.insertLong(comment, "%A2", asking, false);
		IO.printMessage(comment);
	}
	
	/**
	 * Player trying to sell for too high of a price.
	 * 
	 * @param offer store owner's offer
	 * @param asking player's asking price
	 * @param finalChance if true, at final offer before store owner kicks player out
	 */
	public static void printComment3(int offer, int asking, boolean finalChance) {
		String comment;
		
		if (finalChance) {
			comment = comment3a[Rnd.randomInt(3) - 1];
		} else {
			comment = comment3b[Rnd.randomInt(15) - 1];
		}
		
		comment = Misc3.insertLong(comment, "%A1", offer, false);
		comment = Misc3.insertLong(comment, "%A2", asking, false);
		IO.printMessage(comment);
	}
	
	/**
	 * Kick 'da bum out. -RAK-
	 */
	public static void printComment4() {
		int tmp = Rnd.randomInt(5) - 1;
		IO.printMessage(comment4a[tmp]);
		IO.printMessage(comment4b[tmp]);
	}
	
	/**
	 * Bad offer, store owner is insulted.
	 */
	public static void printComment5() {
		IO.printMessage(comment5[Rnd.randomInt(10) - 1]);
	}
	
	/**
	 * Store owner questioning player's offer.
	 */
	public static void printComment6() {
		IO.printMessage(comment6[Rnd.randomInt(5) - 1]);
	}
	
	/**
	 * Displays the set of commands. -RAK-
	 */
	public static void displayCommands() {
		IO.print("You may:", 20, 0);
		IO.print(" p) Purchase an item.           b) Browse store's inventory.", 21, 0);
		IO.print(" s) Sell an item.               i/e/t/w/x) Inventory/Equipment Lists.", 22, 0);
		IO.print("ESC) Exit from Building.        ^R) Redraw the screen.", 23, 0);
	}
	
	/**
	 * Displays the set of commands. -RAK-
	 * 
	 * @param typ specifies what the player is doing, -1 if selling, otherwise buying
	 */
	public static void haggleCommands(int typ) {
		if (typ == -1) {
			IO.print("Specify an asking-price in gold pieces.", 21, 0);
		} else {
			IO.print("Specify an offer in gold pieces.", 21, 0);
		}
		IO.print("ESC) Quit Haggling.", 22, 0);
		IO.eraseLine(23, 0); // clear last line
	}
	
	/**
	 * Displays a store's inventory. -RAK-
	 * 
	 * @param storeNum store number
	 * @param start where in the store's inventory to start listing items
	 */
	public static void displayInventory(int storeNum, int start) {
		StoreType store = Variable.store[storeNum];
		int i = (start % 12);
		int stop = ((start / 12) + 1) * 12;
		if (stop > store.storeCounter) {
			stop = store.storeCounter;
		}
		
		while (start < stop) {
			InvenType item = store.storeInven[start].item;
			int x = item.number;
			if (item.subCategory >= Constants.ITEM_SINGLE_STACK_MIN
					&& item.subCategory <= Constants.ITEM_SINGLE_STACK_MAX) {
				item.number = 1;
			}
			
			String itemDesc = Desc.describeObject(item, true);
			item.number = x;
			IO.print(String.format("%c) %s", 'a' + i, itemDesc), i + 5, 0);
			x = store.storeInven[start].cost;
			if (x <= 0) {
				int value = -x;
				value = value * Misc3.adjustCharisma() / 100;
				if (value <= 0) {
					value = 1;
				}
				IO.print(String.format("%9d", value), i + 5, 59);
			} else {
				IO.print(String.format("%9d [Fixed]", x), i + 5, 59);
			}
			i++;
			start++;
		}
		
		if (i < 12) {
			for (int j = 0; j < (11 - i + 1); j++) {
				IO.eraseLine(j + i + 5, 0); // clear remaining lines
			}
		}
		
		if (store.storeCounter > 12) {
			IO.putBuffer("- cont. -", 17, 60);
		} else {
			IO.eraseLine(17, 60);
		}
	}
	
	/**
	 * Re-displays only a single cost. -RAK-
	 * 
	 * @param storeNum store number
	 * @param pos position in the store's inventory for which to display the cost
	 */
	public static void displayCost(int storeNum, int pos) {
		StoreType s_ptr = Variable.store[storeNum];
		int i = (pos % 12);
		if (s_ptr.storeInven[pos].cost < 0) {
			int j = - s_ptr.storeInven[pos].cost;
			j = j * Misc3.adjustCharisma() / 100;
			IO.print(String.format("%d", j), i + 5, 59);
		} else {
			IO.print(String.format("%9d [Fixed]", s_ptr.storeInven[pos].cost), i + 5, 59);
		}
	}
	
	/**
	 * Displays player's gold. -RAK-
	 */
	public static void storePrintGold() {
		IO.print(String.format("Gold Remaining : %d", Player.py.misc.gold), 18, 17);
	}
	
	/**
	 * Displays store. -RAK-
	 * 
	 * @param storeNum store number
	 * @param curTop position in store's inventory currently at top of display
	 */
	public static void displayStore(int storeNum, int curTop) {
		StoreType store = Variable.store[storeNum];
		IO.clearScreen();
		IO.putBuffer(Tables.owners[store.owner].ownerName, 3, 9);
		IO.putBuffer("Item", 4, 3);
		IO.putBuffer("Asking Price", 4, 60);
		storePrintGold();
		displayCommands();
		displayInventory(storeNum, curTop);
	}
	
	/**
	 * Get the ID of a store item and return it's value. -RAK-
	 * 
	 * @param comVal stores player commands
	 * @param prompt display message
	 * @param firstLetter letter of the first item in the list
	 * @param lastLetter letter of the last item in the list
	 * @return whether the player selected an item to purchase
	 */
	public static boolean getStoreItem(IntPointer comVal, String prompt, int firstLetter, int lastLetter) {
		comVal.value(-1);
		
		CharPointer command = new CharPointer();
		boolean choseItem = false;
		final String message = String.format(
				"(Items %c-%c, ESC to exit) %s",
				firstLetter + 'a', lastLetter + 'a', prompt);
		
		while (IO.getCommand(message, command)) {
			command.value((char)(command.value() - 'a'));
			if (command.value() >= firstLetter && command.value() <= lastLetter) {
				choseItem = true;
				comVal.value(command.value());
				break;
			}
			IO.bell();
		}
		
		IO.eraseLine(Constants.MSG_LINE, 0);
		return choseItem;
	}
	
	/**
	 * Increase the insult counter and get angry if too many. -RAK-
	 * 
	 * @param storeNum store number
	 * @return whether the store owner is angry
	 */
	public static boolean increaseInsults(int storeNum) {
		boolean increase = false;
		StoreType store = Variable.store[storeNum];
		store.currInsult++;
		
		if (store.currInsult > Tables.owners[store.owner].insultMax) {
			printComment4();
			store.currInsult = 0;
			store.badBuy++;
			store.storeOpen = Variable.turn + 2500 + Rnd.randomInt(2500);
			increase = true;
		}
		
		return increase;
	}
	
	/**
	 * Decrease insults. -RAK-
	 * 
	 * @param storeNum store number
	 */
	public static void decreaseInsults(int storeNum) {
		StoreType store = Variable.store[storeNum];
		if (store.currInsult != 0) {
			store.currInsult--;
		}
	}
	
	/**
	 * Have insulted while haggling. -RAK-
	 * 
	 * @param storeNum store number
	 * @return whether the offer insults store owner
	 */
	public static boolean haggleInsults(int storeNum) {
		if (increaseInsults(storeNum)) {
			return true;
		}
		
		printComment5();
		IO.printMessage(""); // keep insult separate from rest of haggle
		return false;
	}
	
	/**
	 * Haggle with the store owner.
	 * 
	 * @param comment store owner's response to player's offer
	 * @param newOffer store's the player's newest offer
	 * @param numOffer how many offers have been made so far
	 * @return whether the player successfully purchased an item
	 */
	public static boolean getHaggle(String comment, IntPointer newOffer, boolean numOffer) {
		boolean purchasingItem = true;
		boolean increment = false;
		int commentLen = comment.length();
		int origCommentLen = commentLen;
		if (!numOffer) {
			lastStoreInc = 0;
		}
		
		int offer = 0;
		while (purchasingItem && offer == 0) {
			IO.print(comment, 0, 0);
			if (numOffer && lastStoreInc != 0) {
				String defaultOffer = String.format("[%c%d] ", (lastStoreInc < 0) ? '-' : '+', Math.abs(lastStoreInc));
				IO.print(defaultOffer, 0, origCommentLen);
				commentLen = origCommentLen + defaultOffer.length();
			}
			String strOffer = IO.getString(0, commentLen, 40);
			if (strOffer.isEmpty()) {
				purchasingItem = false;
				break;
			}
			
			strOffer = strOffer.trim();
			if (strOffer.charAt(0) == '+' || strOffer.charAt(0) == '-') {
				increment = true;
			}
			
			if (numOffer && increment) {
				try {
					offer = Integer.parseInt(strOffer);
				} catch (NumberFormatException e) {
					System.err.println("Could not convert out_val to an integer in Store2.get_haggle()");
					e.printStackTrace();
					offer = 0;
				}
				// Don't accept a zero here.  Turn off increment if it was zero
				// because a zero will not exit.  This can be zero if the user
				// did not type a number after the +/- sign.
				if (offer == 0) {
					increment = false;
				} else {
					lastStoreInc = offer;
				}
			} else if (numOffer && strOffer.isEmpty()) {
				offer = lastStoreInc;
				increment = true;
			} else {
				try {
					offer = Integer.parseInt(strOffer);
				} catch (NumberFormatException e) {
					System.err.println("Could not convert out_val to an integer in Store2.get_haggle()");
					e.printStackTrace();
					offer = 0;
				}
			}
			
			// don't allow incremental haggling, if player has not made an offer yet
			if (purchasingItem && !numOffer && increment) {
				IO.printMessage("You haven't even made your first offer yet!");
				offer = 0;
				increment = false;
			}
		}
		
		if (purchasingItem) {
			if (increment) {
				newOffer.value(newOffer.value() + offer);
			} else {
				newOffer.value(offer);
			}
		} else {
			IO.eraseLine(0, 0);
		}
		
		return purchasingItem;
	}
	
	/**
	 * Player makes an offer.
	 * 
	 * @param storeNum store number
	 * @param comment
	 * @param newOffer stores the player's newest offer
	 * @param lastOffer the player's last offer
	 * @param numOffer number of offers made so far
	 * @param factor
	 * @return
	 */
	public static int receiveOffer(int storeNum, String comment, IntPointer newOffer, int lastOffer, boolean numOffer, int factor) {
		int receive = 0;
		boolean flag = false;
		while (!flag) {
			if (getHaggle(comment, newOffer, numOffer)) {
				if (newOffer.value() * factor >= lastOffer * factor) {
					flag = true;
				} else if (haggleInsults(storeNum)) {
					receive = 2;
					flag = true;
				} else {
					// new_offer rejected, reset new_offer so that incremental
					// haggling works correctly
					newOffer.value(lastOffer);
				}
			} else {
				receive = 1;
				flag = true;
			}
		}
		
		return receive;
	}
	
	/**
	 * Haggling routine. -RAK-
	 * 
	 * @param storeNum store number
	 * @param price stores the price of the item
	 * @param item the item being purchased
	 * @return 
	 */
	public static int purchaseHaggle(int storeNum, IntPointer price, InvenType item) {
		price.value(0);
		
		IntPointer maxSell = new IntPointer();
		IntPointer minSell = new IntPointer();
		int cost = Store1.getSellPrice(storeNum, maxSell, minSell, item);
		maxSell.value(maxSell.value() * Misc3.adjustCharisma() / 100);
		if (maxSell.value() <= 0) {
			maxSell.value(1);
		}
		
		minSell.value(minSell.value() * Misc3.adjustCharisma() / 100);
		if (minSell.value() <= 0) {
			minSell.value(1);
		}
		
		StoreType store = Variable.store[storeNum];
		StoreOwnerType storeOwner = Tables.owners[store.owner];
		// cast max_inflate to signed so that subtraction works correctly
		int maxBuy = cost * (200 - storeOwner.maxInflate) / 100;
		if (maxBuy <= 0) {
			maxBuy = 1;
		}
		
		final int minHagglePercent = storeOwner.hagglePercent;
		final int maxHagglePercent = minHagglePercent * 3;
		haggleCommands(1);
		int curAskingValue = maxSell.value();
		final int finalAskingValue = minSell.value();
		final int minOffer = maxBuy;
		IntPointer newOffer = new IntPointer();
		boolean numOffer = false; // this prevents incremental haggling on first try
		String comment = "Asking";
		
		boolean didntHaggle = false;
		// go right to final price if player has bargained well
		if (Store1.doesNotNeedToBargain(storeNum, finalAskingValue)) {
			IO.printMessage("After a long bargaining session, you agree upon the price.");
			curAskingValue = minSell.value();
			comment = "Final offer";
			didntHaggle = true;
			
			// Set up automatic increment, so that a return will accept the
			// final price.
			lastStoreInc = minSell.value();
			numOffer = true;
		}
		
		int lastOffer = minOffer;
		int finalFlag = 0;
		int purchase = 0;
		boolean flag = false;
		while (!flag) {
			boolean loopFlag = true;
			while (!flag && loopFlag) {
				IO.putBuffer(String.format("%s :  %d", comment, curAskingValue), 1, 0);
				purchase = receiveOffer(storeNum, "What do you offer? ", newOffer, lastOffer, numOffer, 1);
				if (purchase != 0) {
					flag = true;
					break;
				}
				
				if (newOffer.value() > curAskingValue) {
					printComment6();
					// rejected, reset new_offer for incremental haggling
					newOffer.value(lastOffer);
					
					// If the automatic increment is large enough to overflow,
					// then the player must have made a mistake.  Clear it
					// because it is useless.
					if (lastOffer + lastStoreInc > curAskingValue) {
						lastStoreInc = 0;
					}
				} else if (newOffer.value() == curAskingValue) {
					flag = true;
					price.value(newOffer.value());
				} else {
					loopFlag = false;
				}
			}
			
			if (!flag) {
				int x1 = (newOffer.value() - lastOffer) * 100 / (curAskingValue - lastOffer);
				if (x1 < minHagglePercent) {
					flag = haggleInsults(storeNum);
					if (flag) {
						purchase = 2;
					}
				} else if (x1 > maxHagglePercent) {
					x1 = x1 * 75 / 100;
					if (x1 < maxHagglePercent) {
						x1 = maxHagglePercent;
					}
				}
				
				int x2 = x1 + Rnd.randomInt(5) - 3;
				int x3 = ((curAskingValue - newOffer.value()) * x2 / 100) + 1;
				// don't let the price go up
				if (x3 < 0) {
					x3 = 0;
				}
				
				curAskingValue -= x3;
				if (curAskingValue < finalAskingValue) {
					curAskingValue = finalAskingValue;
					comment = "Final Offer";
					// Set the automatic haggle increment so that RET will give
					// a new_offer equal to the final_ask price.
					lastStoreInc = finalAskingValue - newOffer.value();
					finalFlag++;
					if (finalFlag > 3) {
						if (increaseInsults(storeNum)) {
							purchase = 2;
						} else {
							purchase = 1;
						}
						flag = true;
					}
				} else if (newOffer.value() >= curAskingValue) {
					flag = true;
					price.value(newOffer.value());
				}
				
				if (!flag) {
					lastOffer = newOffer.value();
					numOffer = true; // enable incremental haggling
					IO.eraseLine(1, 0);
					IO.putBuffer(String.format("Your last offer : %d", lastOffer), 1, 39);
					printComment2(lastOffer, curAskingValue, finalFlag > 0);
					
					// If the current increment would take you over the store's
					// price, then decrease it to an exact match.
					if (curAskingValue - lastOffer < lastStoreInc) {
						lastStoreInc = curAskingValue - lastOffer;
					}
				}
			}
		}
		
		// update bargaining info
		if (purchase == 0 && !didntHaggle) {
			Store1.updateBargain(storeNum, price.value(), finalAskingValue);
		}
		
		return purchase;
	}
	
	/**
	 * Haggling routine. -RAK-
	 * 
	 * @param storeNum store number
	 * @param price stores the price
	 * @param item the item being sold
	 * @return 
	 */
	public static int sellHaggle(int storeNum, IntPointer price, InvenType item) {
		price.value(0);
		
		int sell = 0;
		int maxGold = 0;
		int minHagglePercent = 0;
		int maxHagglePercent = 0;
		int maxSell = 0;
		int maxBuy = 0;
		int minBuy = 0;
		boolean flag = false;
		int cost = Store1.getItemValue(item);
		if (cost < 1) {
			sell = 3;
			flag = true;
		} else {
			StoreType store = Variable.store[storeNum];
			StoreOwnerType storeOwner = Tables.owners[store.owner];
			cost = cost * (200 - Misc3.adjustCharisma()) / 100;
			cost = cost * (200 - Tables.raceGoldAdjust[storeOwner.ownerRace][Player.py.misc.playerRace]) / 100;
			if (cost < 1) {
				cost = 1;
			}
			
			maxSell = cost * storeOwner.maxInflate / 100;
			// cast max_inflate to signed so that subtraction works correctly
			maxBuy = cost * (200 - storeOwner.maxInflate) / 100;
			minBuy = cost * (200 - storeOwner.minInflate) / 100;
			if (minBuy < 1) {
				minBuy = 1;
			}
			if (maxBuy < 1) {
				maxBuy = 1;
			}
			if (minBuy < maxBuy) {
				minBuy = maxBuy;
			}
			minHagglePercent = storeOwner.hagglePercent;
			maxHagglePercent = minHagglePercent * 3;
			maxGold = storeOwner.maxCost;
		}
		
		int finalFlag = 0;
		boolean numOffer;
		boolean didntHaggle = false;
		String comment;
		int finalAskingPrice = 0;
		if (!flag) {
			int curAskingPrice;
			int minOffer;
			int lastOffer;
			haggleCommands(-1);
			numOffer = false; // this prevents incremental haggling on first try
			if (maxBuy > maxGold) {
				finalFlag = 1;
				comment = "Final Offer";
				
				// Disable the automatic haggle increment on RET.
				lastStoreInc = 0;
				curAskingPrice = maxGold;
				finalAskingPrice = maxGold;
				IO.printMessage("I am sorry, but I have not the money to afford such a fine item.");
				didntHaggle = true;
			} else {
				curAskingPrice = maxBuy;
				finalAskingPrice = minBuy;
				if (finalAskingPrice > maxGold) {
					finalAskingPrice = maxGold;
				}
				comment = "Offer";
				
				// go right to final price if player has bargained well
				if (Store1.doesNotNeedToBargain(storeNum, finalAskingPrice)) {
					IO.printMessage("After a long bargaining session, you agree upon the price.");
					curAskingPrice = finalAskingPrice;
					comment = "Final offer";
					didntHaggle = true;
					
					// Set up automatic increment, so that a return will accept the
					// final price.
					lastStoreInc = finalAskingPrice;
					numOffer = true;
				}
			}
			
			minOffer = maxSell;
			lastOffer = minOffer;
			IntPointer newOffer = new IntPointer();
			if (curAskingPrice < 1) {
				curAskingPrice = 1;
			}
			
			while (!flag) {
				boolean loopFlag = true;
				while (!flag && loopFlag) {
					IO.putBuffer(String.format("%s :  %d", comment, curAskingPrice), 1, 0);
					sell = receiveOffer(storeNum, "What price do you ask? ", newOffer, lastOffer, numOffer, -1);
					if (sell != 0) {
						flag = true;
						break;
					}
					
					if (newOffer.value() < curAskingPrice) {
						printComment6();
						// rejected, reset new_offer for incremental haggling
						newOffer.value(lastOffer);
						
						// If the automatic increment is large enough to
						// overflow, then the player must have made a mistake.
						// Clear it because it is useless.
						if (lastOffer + lastStoreInc < curAskingPrice) {
							lastStoreInc = 0;
						}
					} else if (newOffer.value() == curAskingPrice) {
						flag = true;
						price.value(newOffer.value());
					} else {
						loopFlag = false;
					}
				}
				
				if (!flag) {
					int x1 = (lastOffer - newOffer.value()) * 100 / (lastOffer - curAskingPrice);
					if (x1 < minHagglePercent) {
						flag = haggleInsults(storeNum);
						if (flag)	sell = 2;
					} else if (x1 > maxHagglePercent) {
						x1 = x1 * 75 / 100;
						if (x1 < maxHagglePercent) {
							x1 = maxHagglePercent;
						}
					}
					
					int x2 = x1 + Rnd.randomInt(5) - 3;
					int x3 = ((newOffer.value() - curAskingPrice) * x2 / 100) + 1;
					// don't let the price go down
					if (x3 < 0) {
						x3 = 0;
					}
					
					curAskingPrice += x3;
					if (curAskingPrice > finalAskingPrice) {
						curAskingPrice = finalAskingPrice;
						comment = "Final Offer";
						
						// Set the automatic haggle increment so that RET will give
						// a new_offer equal to the final_ask price.
						lastStoreInc = finalAskingPrice - newOffer.value();
						finalFlag++;
						if (finalFlag > 3) {
							if (increaseInsults(storeNum)) {
								sell = 2;
							} else {
								sell = 1;
							}
							flag = true;
						}
					} else if (newOffer.value() <= curAskingPrice) {
						flag = true;
						price.value(newOffer.value());
					}
					
					if (!flag) {
						lastOffer = newOffer.value();
						numOffer = true; // enable incremental haggling
						IO.eraseLine(1, 0);
						IO.putBuffer(String.format("Your last bid %d", lastOffer), 1, 39);
						printComment3(curAskingPrice, lastOffer, finalFlag > 0);
						
						// If the current decrement would take you under the store's
						// price, then increase it to an exact match.
						if (curAskingPrice - lastOffer > lastStoreInc) {
							lastStoreInc = curAskingPrice - lastOffer;
						}
					}
				}
			}
		}
		
		// update bargaining info
		if (sell == 0 && !didntHaggle) {
			Store1.updateBargain(storeNum, price.value(), finalAskingPrice);
		}
		
		return sell;
	}
	
	/**
	 * Buy an item from a store. -RAK-
	 * 
	 * @param storeNum store number
	 * @param curTop stores the store inventory position of the first item being displayed
	 * @return whether the player bought an item
	 */
	public static boolean storePurchase(int storeNum, IntPointer curTop) {
		StoreType store = Variable.store[storeNum];
		if (store.storeCounter < 1) {
			IO.printMessage("I am currently out of stock.");
			return false;
		}
		
		// shownItems == number of objects shown on screen
		int shownItems;
		if (curTop.value() == 12) {
			shownItems = store.storeCounter - 1 - 12;
		} else if (store.storeCounter > 11) {
			shownItems = 11;
		} else {
			shownItems = store.storeCounter - 1;
		}
		
		IntPointer itemVal = new IntPointer();
		// Get the item number to be bought
		if (!getStoreItem(itemVal, "Which item are you interested in? ", 0, shownItems)) {
			return false;
		}
		
		itemVal.value(itemVal.value() + curTop.value()); // true item_val
		InvenType itemToSell = new InvenType();
		Misc3.takeOneItem(itemToSell, store.storeInven[itemVal.value()].item);
		if (!Misc3.canPickUpItem(itemToSell)) {
			IO.print("You cannot carry that many different items.", 0, 0);
			return false;
		}
		
		IntPointer price = new IntPointer();
		int choice;
		if (store.storeInven[itemVal.value()].cost > 0) {
			price.value(store.storeInven[itemVal.value()].cost);
			choice = 0;
		} else {
			choice = purchaseHaggle(storeNum, price, itemToSell);
		}
		
		if (choice == 2) {
			return true;
		} else if (choice != 0) {
			return false;
		}
		
		if (Player.py.misc.gold >= price.value()) {
			printComment1();
			decreaseInsults(storeNum);
			Player.py.misc.gold -= price.value();
			int newItem = Misc3.pickUpItem(itemToSell);
			shownItems = store.storeCounter;
			Store1.storeDestroy(storeNum, itemVal.value(), true);
			String itemDesc = Desc.describeObject(Treasure.inventory[newItem], true);
			IO.print(String.format("You have %s (%c)", itemDesc, newItem + 'a'), 0, 0);
			Misc3.checkStrength();
			if (curTop.value() >= store.storeCounter) {
				curTop.value(0);
				displayInventory(storeNum, curTop.value());
			} else {
				InvenRecord item = store.storeInven[itemVal.value()];
				if (shownItems == store.storeCounter) {
					if (item.cost < 0) {
						item.cost = price.value();
						displayCost(storeNum, itemVal.value());
					}
				} else {
					displayInventory(storeNum, itemVal.value());
				}
			}
			storePrintGold();
		} else {
			if (increaseInsults(storeNum)) {
				return true;
			} else {
				printComment1();
				IO.printMessage("Liar!  You have not the gold!");
			}
		}
		
		// Less intuitive, but looks better here than in purchase_haggle.
		displayCommands();
		IO.eraseLine(1, 0);
		
		return false;
	}
	
	/**
	 * Check if the store will buy a particular item.
	 * 
	 * @param storeNum store number
	 * @param inv item being sold to the store
	 * @return whether the store will buy the item
	 */
	private static boolean storeBuy(int storeNum, InvenType inv) {
		switch (storeNum) {
		case Sets.GENERAL_STORE:
			return inv.isSoldInGeneralStore();
		case Sets.ARMORY:
			return inv.isSoldInArmory();
		case Sets.WEAPONSMITH:
			return inv.isSoldInWeaponsmith();
		case Sets.TEMPLE:
			return inv.isSoldInTemple();
		case Sets.ALCHEMIST:
			return inv.isSoldInAlchemist();
		case Sets.MAGIC_SHOP:
			return inv.isSoldInMagicShop();
		default:
			return false;
		}
	}
	
	/**
	 * Sell an item to the store. -RAK-
	 * 
	 * @param storeNum store number
	 * @param curTop stores the store inventory position of the first item being displayed
	 * @return whether sold an item
	 */
	public static boolean storeSell(int storeNum, IntPointer curTop) {
		int firstItem = Treasure.invenCounter;
		int lastItem = -1;
		char[] mask = new char[Constants.INVEN_WIELD];
		for (int i = 0; i < Treasure.invenCounter; i++) {
			boolean flag = storeBuy(storeNum, Treasure.inventory[i]);
			mask[i] = (flag ? (char)1 : (char)0);
			if (flag) {
				if (i < firstItem) {
					firstItem = i;
				}
				if (i > lastItem) {
					lastItem = i;
				}
			}
		}
		
		if (lastItem == -1) {
			IO.printMessage("You have nothing to sell to this store!");
			return false;
		}
		
		IntPointer itemVal = new IntPointer();
		if (!Moria1.getItemId(itemVal, "Which one? ", firstItem, lastItem, mask, "I do not buy such items.")) {
			IO.printMessage("I have not the room in my store to keep it.");
			return false;
		}
		
		InvenType soldObject = new InvenType();
		Misc3.takeOneItem(soldObject, Treasure.inventory[itemVal.value()]);
		String itemDesc = Desc.describeObject(soldObject, true);
		IO.printMessage(String.format("Selling %s (%c)", itemDesc, itemVal.value() + 'a'));
		if (!Store1.storeHasRoom(soldObject, storeNum)) {
			return false;
		}
		
		boolean sell = false;
		IntPointer price = new IntPointer();
		int choice = sellHaggle(storeNum, price, soldObject);
		if (choice == 0) {
			printComment1();
			decreaseInsults(storeNum);
			Player.py.misc.gold += price.value();
			// identify object in inventory to set object_ident
			Desc.identify(itemVal);
			// retake sold_obj so that it will be identified
			Misc3.takeOneItem(soldObject, Treasure.inventory[itemVal.value()]);
			// call known2 for store item, so charges/pluses are known
			Desc.identifyItemPlusses(soldObject);
			Misc3.destroyInvenItem(itemVal.value());
			itemDesc = Desc.describeObject(soldObject, true);
			IO.printMessage(String.format("You've sold %s", itemDesc));
			
			IntPointer item_pos = new IntPointer();
			Store1.storeCarry(storeNum, item_pos, soldObject);
			Misc3.checkStrength();
			if (item_pos.value() >= 0) {
				if (item_pos.value() < 12) {
					if (curTop.value() < 12) {
						displayInventory(storeNum, item_pos.value());
					} else {
						curTop.value(0);
						displayInventory(storeNum, curTop.value());
					}
				} else if (curTop.value() > 11) {
					displayInventory(storeNum, item_pos.value());
				} else {
					curTop.value(12);
					displayInventory(storeNum, curTop.value());
				}
			}
			storePrintGold();
		} else if (choice == 2) {
			sell = true;
		} else if (choice == 3) {
			IO.printMessage("How dare you!");
			IO.printMessage("I will not buy that!");
			sell = increaseInsults(storeNum);
		}
		
		// Less intuitive, but looks better here than in sell_haggle.
		IO.eraseLine(1, 0);
		displayCommands();
		
		return sell;
	}
	
	/**
	 * Entering a store. -RAK-
	 * 
	 * @param storeNum store number
	 */
	public static void enterStore(int storeNum) {
		StoreType store = Variable.store[storeNum];
		if (store.storeOpen >= Variable.turn) {
			IO.printMessage("The doors are locked.");
			return;
		}
		
		IntPointer curTop = new IntPointer(0);
		displayStore(storeNum, curTop.value());
		
		boolean exitFlag = false;
		while (!exitFlag) {
			IO.moveCursor(20, 9);
			// clear the msg flag just like we do in dungeon.java
			Variable.msgFlag = 0;
			CharPointer command = new CharPointer();
			if (IO.getCommand("", command)) {
				switch (command.value()) {
				case 'b':
					if (curTop.value() == 0) {
						if (store.storeCounter > 12) {
							curTop.value(12);
							displayInventory(storeNum, curTop.value());
						} else {
							IO.printMessage("Entire inventory is shown.");
						}
					} else {
						curTop.value(0);
						displayInventory(storeNum, curTop.value());
					}
					break;
				case 'E': case 'e': // Equipment List
				case 'I': case 'i': // Inventory
				case 'T': case 't': // Take off
				case 'W': case 'w': // Wear
				case 'X': case 'x': // Switch weapon
					int charisma = Player.py.stats.useStat[Constants.A_CHR];
					do {
						Moria1.doInvenCommand(command.value());
						command.value(Variable.doingInven);
					} while (command.value() != '\0');
					// redisplay store prices if charisma changes
					if (charisma != Player.py.stats.useStat[Constants.A_CHR]) {
						displayInventory(storeNum, curTop.value());
					}
					Variable.freeTurnFlag = false; // No free moves here. -CJS-
					break;
				case 'p':
					exitFlag = storePurchase(storeNum, curTop);
					break;
				case 's':
					exitFlag = storeSell(storeNum, curTop);
					break;
				default:
					IO.bell();
					break;
				}
			} else {
				exitFlag = true;
			}
		}
		// Can't save and restore the screen because inven_command does that.
		Misc3.drawCave();
	}
}
