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
	
	public static void ident_char() {
		CharPointer command = new CharPointer();
		char query;
		int i, n;
		
		if (IO.get_com("Enter character to be identified :", command)) {
			switch (command.value()) {
			/* every printing ASCII character is listed here, in the order in which
			 * they appear in the ASCII character set */
			case ' ': IO.prt("  - An open pit.", 0, 0); break;
			case '!': IO.prt("! - A potion.", 0, 0); break;
			case '"': IO.prt("\" - An amulet, periapt, or necklace.", 0, 0); break;
			case '#': IO.prt("# - A stone wall.", 0, 0); break;
			case '$': IO.prt("$ - Treasure.", 0, 0); break;
			case '%':
				if (Variable.highlight_seams.value()) {
					IO.prt("% - A magma or quartz vein.", 0, 0);
				} else {
					IO.prt("% - Not used.", 0, 0);
				}
				break;
			case '&': IO.prt("& - Treasure chest.", 0, 0); break;
			case '\'': IO.prt("' - An open door.", 0, 0); break;
			case '(': IO.prt("( - Soft armor.", 0, 0); break;
			case ')': IO.prt(") - A shield.", 0, 0); break;
			case '*': IO.prt("* - Gems.", 0, 0); break;
			case '+': IO.prt("+ - A closed door.", 0, 0); break;
			case ',': IO.prt(", - Food or mushroom patch.", 0, 0); break;
			case '-': IO.prt("- - A wand", 0, 0); break;
			case '.': IO.prt(". - Floor.", 0, 0); break;
			case '/': IO.prt("/ - A pole weapon.", 0, 0); break;
			/* case '0': io.prt("0 - Not used.", 0, 0); break; */
			case '1': IO.prt("1 - Entrance to General Store.", 0, 0); break;
			case '2': IO.prt("2 - Entrance to Armory.", 0, 0); break;
			case '3': IO.prt("3 - Entrance to Weaponsmith.", 0, 0); break;
			case '4': IO.prt("4 - Entrance to Temple.", 0, 0); break;
			case '5': IO.prt("5 - Entrance to Alchemy shop.", 0, 0); break;
			case '6': IO.prt("6 - Entrance to Magic-Users store.", 0, 0); break;
			/* case '7': io.prt("7 - Not used.", 0, 0); break; */
			/* case '8': io.prt("8 - Not used.", 0, 0); break; */
			/* case '9': io.prt("9 - Not used.", 0, 0);  break;*/
			case ':': IO.prt(": - Rubble.", 0, 0); break;
			case ';': IO.prt("; - A loose rock.", 0, 0); break;
			case '<': IO.prt("< - An up staircase.", 0, 0); break;
			case '=': IO.prt("= - A ring.", 0, 0); break;
			case '>': IO.prt("> - A down staircase.", 0, 0); break;
			case '?': IO.prt("? - A scroll.", 0, 0); break;
			case '@': IO.prt(Player.py.misc.name, 0, 0); break;
			case 'A': IO.prt("A - Giant Ant Lion.", 0, 0); break;
			case 'B': IO.prt("B - The Balrog.", 0, 0); break;
			case 'C': IO.prt("C - Gelatinous Cube.", 0, 0); break;
			case 'D': IO.prt("D - An Ancient Dragon (Beware).", 0, 0); break;
			case 'E': IO.prt("E - Elemental.", 0, 0); break;
			case 'F': IO.prt("F - Giant Fly.", 0, 0); break;
			case 'G': IO.prt("G - Ghost.", 0, 0); break;
			case 'H': IO.prt("H - Hobgoblin.", 0, 0); break;
			/* case 'I': io.prt("I - Invisible Stalker.", 0, 0); break; */
			case 'J': IO.prt("J - Jelly.", 0, 0); break;
			case 'K': IO.prt("K - Killer Beetle.", 0, 0); break;
			case 'L': IO.prt("L - Lich.", 0, 0); break;
			case 'M': IO.prt("M - Mummy.", 0, 0); break;
			/* case 'N': io.prt("N - Not used.", 0, 0); break; */
			case 'O': IO.prt("O - Ooze.", 0, 0); break;
			case 'P': IO.prt("P - Giant humanoid.", 0, 0); break;
			case 'Q': IO.prt("Q - Quylthulg (Pulsing Flesh Mound).", 0, 0); break;
			case 'R': IO.prt("R - Reptile.", 0, 0); break;
			case 'S': IO.prt("S - Giant Scorpion.", 0, 0); break;
			case 'T': IO.prt("T - Troll.", 0, 0); break;
			case 'U': IO.prt("U - Umber Hulk.", 0, 0); break;
			case 'V': IO.prt("V - Vampire.", 0, 0); break;
			case 'W': IO.prt("W - Wight or Wraith.", 0, 0); break;
			case 'X': IO.prt("X - Xorn.", 0, 0); break;
			case 'Y': IO.prt("Y - Yeti.", 0, 0); break;
			/* case 'Z': io.prt("Z - Not used.", 0, 0); break; */
			case '[': IO.prt("[ - Hard armor.", 0, 0); break;
			case '\\': IO.prt("\\ - A hafted weapon.", 0, 0); break;
			case ']': IO.prt("] - Misc. armor.", 0, 0); break;
			case '^': IO.prt("^ - A trap.", 0, 0); break;
			case '_': IO.prt("_ - A staff.", 0, 0); break;
			/* case '`': io.prt("` - Not used.", 0, 0); break; */
			case 'a': IO.prt("a - Giant Ant.", 0, 0); break;
			case 'b': IO.prt("b - Giant Bat.", 0, 0); break;
			case 'c': IO.prt("c - Giant Centipede.", 0, 0); break;
			case 'd': IO.prt("d - Dragon.", 0, 0); break;
			case 'e': IO.prt("e - Floating Eye.", 0, 0); break;
			case 'f': IO.prt("f - Giant Frog.", 0, 0); break;
			case 'g': IO.prt("g - Golem.", 0, 0); break;
			case 'h': IO.prt("h - Harpy.", 0, 0); break;
			case 'i': IO.prt("i - Icky Thing.", 0, 0); break;
			case 'j': IO.prt("j - Jackal.", 0, 0); break;
			case 'k': IO.prt("k - Kobold.", 0, 0); break;
			case 'l': IO.prt("l - Giant Louse.", 0, 0); break;
			case 'm': IO.prt("m - Mold.", 0, 0); break;
			case 'n': IO.prt("n - Naga.", 0, 0); break;
			case 'o': IO.prt("o - Orc or Ogre.", 0, 0); break;
			case 'p': IO.prt("p - Person (Humanoid).", 0, 0); break;
			case 'q': IO.prt("q - Quasit.", 0, 0); break;
			case 'r': IO.prt("r - Rodent.", 0, 0); break;
			case 's': IO.prt("s - Skeleton.", 0, 0); break;
			case 't': IO.prt("t - Giant Tick.", 0, 0); break;
			/* case 'u': io.prt(("u - Not used.", 0, 0); break; */
			/* case 'v': io.prt(("v - Not used.", 0, 0); break; */
			case 'w': IO.prt("w - Worm or Worm Mass.", 0, 0); break;
			/* case 'x': io.prt(("x - Not used.", 0, 0); break; */
			case 'y': IO.prt("y - Yeek.", 0, 0); break;
			case 'z': IO.prt("z - Zombie.", 0, 0); break;
			case '{': IO.prt("{ - Arrow, bolt, or bullet.", 0, 0); break;
			case '|': IO.prt("| - A sword or dagger.", 0, 0); break;
			case '}': IO.prt("} - Bow, crossbow, or sling.", 0, 0); break;
			case '~': IO.prt("~ - Miscellaneous item.", 0, 0); break;
			default:  IO.prt("Not Used.", 0, 0); break;
			}
		}
		/* Allow access to monster memory. -CJS- */
		n = 0;
		for (i = Constants.MAX_CREATURES - 1; i >= 0; i--) {
			if ((Monsters.c_list[i].cchar == command.value()) && Recall.bool_roff_recall(i)) {
				if (n == 0) {
					IO.put_buffer("You recall those details? [y/n]", 0, 40);
					query = IO.inkey();
					if (query != 'y' && query != 'Y') {
						break;
					}
					IO.erase_line (0, 40);
					IO.save_screen();
				}
				n++;
				query = Recall.roff_recall(i);
				IO.restore_screen();
				if (query == Constants.ESCAPE) {
					break;
				}
			}
		}
	}
}
