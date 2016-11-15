/*
 * Help.java: identify a symbol
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

public class Help {
	
	private Help() { }
	
	public static void identifySymbol() {
		CharPointer command = new CharPointer();
		char query;
		int i, n;
		
		if (IO.getCommand("Enter character to be identified :", command)) {
			switch (command.value()) {
			/* every printing ASCII character is listed here, in the order in which
			 * they appear in the ASCII character set */
			case ' ': IO.print("  - An open pit.", 0, 0); break;
			case '!': IO.print("! - A potion.", 0, 0); break;
			case '"': IO.print("\" - An amulet, periapt, or necklace.", 0, 0); break;
			case '#': IO.print("# - A stone wall.", 0, 0); break;
			case '$': IO.print("$ - Treasure.", 0, 0); break;
			case '%':
				if (Variable.highlight_seams.value()) {
					IO.print("% - A magma or quartz vein.", 0, 0);
				} else {
					IO.print("% - Not used.", 0, 0);
				}
				break;
			case '&': IO.print("& - Treasure chest.", 0, 0); break;
			case '\'': IO.print("' - An open door.", 0, 0); break;
			case '(': IO.print("( - Soft armor.", 0, 0); break;
			case ')': IO.print(") - A shield.", 0, 0); break;
			case '*': IO.print("* - Gems.", 0, 0); break;
			case '+': IO.print("+ - A closed door.", 0, 0); break;
			case ',': IO.print(", - Food or mushroom patch.", 0, 0); break;
			case '-': IO.print("- - A wand", 0, 0); break;
			case '.': IO.print(". - Floor.", 0, 0); break;
			case '/': IO.print("/ - A pole weapon.", 0, 0); break;
			/* case '0': io.prt("0 - Not used.", 0, 0); break; */
			case '1': IO.print("1 - Entrance to General Store.", 0, 0); break;
			case '2': IO.print("2 - Entrance to Armory.", 0, 0); break;
			case '3': IO.print("3 - Entrance to Weaponsmith.", 0, 0); break;
			case '4': IO.print("4 - Entrance to Temple.", 0, 0); break;
			case '5': IO.print("5 - Entrance to Alchemy shop.", 0, 0); break;
			case '6': IO.print("6 - Entrance to Magic-Users store.", 0, 0); break;
			/* case '7': io.prt("7 - Not used.", 0, 0); break; */
			/* case '8': io.prt("8 - Not used.", 0, 0); break; */
			/* case '9': io.prt("9 - Not used.", 0, 0);  break;*/
			case ':': IO.print(": - Rubble.", 0, 0); break;
			case ';': IO.print("; - A loose rock.", 0, 0); break;
			case '<': IO.print("< - An up staircase.", 0, 0); break;
			case '=': IO.print("= - A ring.", 0, 0); break;
			case '>': IO.print("> - A down staircase.", 0, 0); break;
			case '?': IO.print("? - A scroll.", 0, 0); break;
			case '@': IO.print(Player.py.misc.name, 0, 0); break;
			case 'A': IO.print("A - Giant Ant Lion.", 0, 0); break;
			case 'B': IO.print("B - The Balrog.", 0, 0); break;
			case 'C': IO.print("C - Gelatinous Cube.", 0, 0); break;
			case 'D': IO.print("D - An Ancient Dragon (Beware).", 0, 0); break;
			case 'E': IO.print("E - Elemental.", 0, 0); break;
			case 'F': IO.print("F - Giant Fly.", 0, 0); break;
			case 'G': IO.print("G - Ghost.", 0, 0); break;
			case 'H': IO.print("H - Hobgoblin.", 0, 0); break;
			/* case 'I': io.prt("I - Invisible Stalker.", 0, 0); break; */
			case 'J': IO.print("J - Jelly.", 0, 0); break;
			case 'K': IO.print("K - Killer Beetle.", 0, 0); break;
			case 'L': IO.print("L - Lich.", 0, 0); break;
			case 'M': IO.print("M - Mummy.", 0, 0); break;
			/* case 'N': io.prt("N - Not used.", 0, 0); break; */
			case 'O': IO.print("O - Ooze.", 0, 0); break;
			case 'P': IO.print("P - Giant humanoid.", 0, 0); break;
			case 'Q': IO.print("Q - Quylthulg (Pulsing Flesh Mound).", 0, 0); break;
			case 'R': IO.print("R - Reptile.", 0, 0); break;
			case 'S': IO.print("S - Giant Scorpion.", 0, 0); break;
			case 'T': IO.print("T - Troll.", 0, 0); break;
			case 'U': IO.print("U - Umber Hulk.", 0, 0); break;
			case 'V': IO.print("V - Vampire.", 0, 0); break;
			case 'W': IO.print("W - Wight or Wraith.", 0, 0); break;
			case 'X': IO.print("X - Xorn.", 0, 0); break;
			case 'Y': IO.print("Y - Yeti.", 0, 0); break;
			/* case 'Z': io.prt("Z - Not used.", 0, 0); break; */
			case '[': IO.print("[ - Hard armor.", 0, 0); break;
			case '\\': IO.print("\\ - A hafted weapon.", 0, 0); break;
			case ']': IO.print("] - Misc. armor.", 0, 0); break;
			case '^': IO.print("^ - A trap.", 0, 0); break;
			case '_': IO.print("_ - A staff.", 0, 0); break;
			/* case '`': io.prt("` - Not used.", 0, 0); break; */
			case 'a': IO.print("a - Giant Ant.", 0, 0); break;
			case 'b': IO.print("b - Giant Bat.", 0, 0); break;
			case 'c': IO.print("c - Giant Centipede.", 0, 0); break;
			case 'd': IO.print("d - Dragon.", 0, 0); break;
			case 'e': IO.print("e - Floating Eye.", 0, 0); break;
			case 'f': IO.print("f - Giant Frog.", 0, 0); break;
			case 'g': IO.print("g - Golem.", 0, 0); break;
			case 'h': IO.print("h - Harpy.", 0, 0); break;
			case 'i': IO.print("i - Icky Thing.", 0, 0); break;
			case 'j': IO.print("j - Jackal.", 0, 0); break;
			case 'k': IO.print("k - Kobold.", 0, 0); break;
			case 'l': IO.print("l - Giant Louse.", 0, 0); break;
			case 'm': IO.print("m - Mold.", 0, 0); break;
			case 'n': IO.print("n - Naga.", 0, 0); break;
			case 'o': IO.print("o - Orc or Ogre.", 0, 0); break;
			case 'p': IO.print("p - Person (Humanoid).", 0, 0); break;
			case 'q': IO.print("q - Quasit.", 0, 0); break;
			case 'r': IO.print("r - Rodent.", 0, 0); break;
			case 's': IO.print("s - Skeleton.", 0, 0); break;
			case 't': IO.print("t - Giant Tick.", 0, 0); break;
			/* case 'u': io.prt(("u - Not used.", 0, 0); break; */
			/* case 'v': io.prt(("v - Not used.", 0, 0); break; */
			case 'w': IO.print("w - Worm or Worm Mass.", 0, 0); break;
			/* case 'x': io.prt(("x - Not used.", 0, 0); break; */
			case 'y': IO.print("y - Yeek.", 0, 0); break;
			case 'z': IO.print("z - Zombie.", 0, 0); break;
			case '{': IO.print("{ - Arrow, bolt, or bullet.", 0, 0); break;
			case '|': IO.print("| - A sword or dagger.", 0, 0); break;
			case '}': IO.print("} - Bow, crossbow, or sling.", 0, 0); break;
			case '~': IO.print("~ - Miscellaneous item.", 0, 0); break;
			default:  IO.print("Not Used.", 0, 0); break;
			}
		}
		/* Allow access to monster memory. -CJS- */
		n = 0;
		for (i = Constants.MAX_CREATURES - 1; i >= 0; i--) {
			if ((Monsters.c_list[i].cchar == command.value()) && Recall.canRecallMonster(i)) {
				if (n == 0) {
					IO.putBuffer("You recall those details? [y/n]", 0, 40);
					query = IO.inkey();
					if (query != 'y' && query != 'Y') {
						break;
					}
					IO.eraseLine (0, 40);
					IO.saveScreen();
				}
				n++;
				query = Recall.recallMonster(i);
				IO.restoreScreen();
				if (query == Constants.ESCAPE) {
					break;
				}
			}
		}
	}
}
