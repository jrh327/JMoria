/**
 * Help.java: identify a symbol
 * <p>
 * Copyright (c) 2007 James E. Wilson, Robert A. Koeneke
 * <br>
 * Copyright (c) 2014 Jon Hopkins
 * <p>
 * This file is part of Moria.
 * <p>
 * Moria is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * Moria is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Moria.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jonhopkins.moria;

import net.jonhopkins.moria.types.CharPointer;

public class Help {
	private IO io;
	private Monsters mon;
	private Player py;
	private Recall recall;
	private Variable var;
	
	private static Help instance;
	private Help() { }
	public static Help getInstance() {
		if (instance == null) {
			instance = new Help();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		io = IO.getInstance();
		mon = Monsters.getInstance();
		py = Player.getInstance();
		recall = Recall.getInstance();
		var = Variable.getInstance();
	}
	
	public void ident_char() {
		CharPointer command = new CharPointer();
		char query;
		int i, n;
		
		if (io.get_com("Enter character to be identified :", command)) {
			switch(command.value())
			{
			/* every printing ASCII character is listed here, in the order in which
			 * they appear in the ASCII character set */
			case ' ': io.prt("  - An open pit.", 0, 0); break;
			case '!': io.prt("! - A potion.", 0, 0); break;
			case '"': io.prt("\" - An amulet, periapt, or necklace.", 0, 0); break;
			case '#': io.prt("# - A stone wall.", 0, 0); break;
			case '$': io.prt("$ - Treasure.", 0, 0); break;
			case '%':
				if (var.highlight_seams.value()) {
					io.prt("% - A magma or quartz vein.", 0, 0);
				} else {
					io.prt("% - Not used.", 0, 0);
				}
				break;
			case '&': io.prt("& - Treasure chest.", 0, 0); break;
			case '\'': io.prt("' - An open door.", 0, 0); break;
			case '(': io.prt("( - Soft armor.", 0, 0); break;
			case ')': io.prt(") - A shield.", 0, 0); break;
			case '*': io.prt("* - Gems.", 0, 0); break;
			case '+': io.prt("+ - A closed door.", 0, 0); break;
			case ',': io.prt(", - Food or mushroom patch.", 0, 0); break;
			case '-': io.prt("- - A wand", 0, 0); break;
			case '.': io.prt(". - Floor.", 0, 0); break;
			case '/': io.prt("/ - A pole weapon.", 0, 0); break;
			/* case '0': io.prt("0 - Not used.", 0, 0); break; */
			case '1': io.prt("1 - Entrance to General Store.", 0, 0); break;
			case '2': io.prt("2 - Entrance to Armory.", 0, 0); break;
			case '3': io.prt("3 - Entrance to Weaponsmith.", 0, 0); break;
			case '4': io.prt("4 - Entrance to Temple.", 0, 0); break;
			case '5': io.prt("5 - Entrance to Alchemy shop.", 0, 0); break;
			case '6': io.prt("6 - Entrance to Magic-Users store.", 0, 0); break;
			/* case '7': io.prt("7 - Not used.", 0, 0); break; */
			/* case '8': io.prt("8 - Not used.", 0, 0); break; */
			/* case '9': io.prt("9 - Not used.", 0, 0);  break;*/
			case ':': io.prt(": - Rubble.", 0, 0); break;
			case ';': io.prt("; - A loose rock.", 0, 0); break;
			case '<': io.prt("< - An up staircase.", 0, 0); break;
			case '=': io.prt("= - A ring.", 0, 0); break;
			case '>': io.prt("> - A down staircase.", 0, 0); break;
			case '?': io.prt("? - A scroll.", 0, 0); break;
			case '@': io.prt(py.py.misc.name, 0, 0); break;
			case 'A': io.prt("A - Giant Ant Lion.", 0, 0); break;
			case 'B': io.prt("B - The Balrog.", 0, 0); break;
			case 'C': io.prt("C - Gelatinous Cube.", 0, 0); break;
			case 'D': io.prt("D - An Ancient Dragon (Beware).", 0, 0); break;
			case 'E': io.prt("E - Elemental.", 0, 0); break;
			case 'F': io.prt("F - Giant Fly.", 0, 0); break;
			case 'G': io.prt("G - Ghost.", 0, 0); break;
			case 'H': io.prt("H - Hobgoblin.", 0, 0); break;
			/* case 'I': io.prt("I - Invisible Stalker.", 0, 0); break; */
			case 'J': io.prt("J - Jelly.", 0, 0); break;
			case 'K': io.prt("K - Killer Beetle.", 0, 0); break;
			case 'L': io.prt("L - Lich.", 0, 0); break;
			case 'M': io.prt("M - Mummy.", 0, 0); break;
			/* case 'N': io.prt("N - Not used.", 0, 0); break; */
			case 'O': io.prt("O - Ooze.", 0, 0); break;
			case 'P': io.prt("P - Giant humanoid.", 0, 0); break;
			case 'Q': io.prt("Q - Quylthulg (Pulsing Flesh Mound).", 0, 0); break;
			case 'R': io.prt("R - Reptile.", 0, 0); break;
			case 'S': io.prt("S - Giant Scorpion.", 0, 0); break;
			case 'T': io.prt("T - Troll.", 0, 0); break;
			case 'U': io.prt("U - Umber Hulk.", 0, 0); break;
			case 'V': io.prt("V - Vampire.", 0, 0); break;
			case 'W': io.prt("W - Wight or Wraith.", 0, 0); break;
			case 'X': io.prt("X - Xorn.", 0, 0); break;
			case 'Y': io.prt("Y - Yeti.", 0, 0); break;
			/* case 'Z': io.prt("Z - Not used.", 0, 0); break; */
			case '[': io.prt("[ - Hard armor.", 0, 0); break;
			case '\\': io.prt("\\ - A hafted weapon.", 0, 0); break;
			case ']': io.prt("] - Misc. armor.", 0, 0); break;
			case '^': io.prt("^ - A trap.", 0, 0); break;
			case '_': io.prt("_ - A staff.", 0, 0); break;
			/* case '`': io.prt("` - Not used.", 0, 0); break; */
			case 'a': io.prt("a - Giant Ant.", 0, 0); break;
			case 'b': io.prt("b - Giant Bat.", 0, 0); break;
			case 'c': io.prt("c - Giant Centipede.", 0, 0); break;
			case 'd': io.prt("d - Dragon.", 0, 0); break;
			case 'e': io.prt("e - Floating Eye.", 0, 0); break;
			case 'f': io.prt("f - Giant Frog.", 0, 0); break;
			case 'g': io.prt("g - Golem.", 0, 0); break;
			case 'h': io.prt("h - Harpy.", 0, 0); break;
			case 'i': io.prt("i - Icky Thing.", 0, 0); break;
			case 'j': io.prt("j - Jackal.", 0, 0); break;
			case 'k': io.prt("k - Kobold.", 0, 0); break;
			case 'l': io.prt("l - Giant Louse.", 0, 0); break;
			case 'm': io.prt("m - Mold.", 0, 0); break;
			case 'n': io.prt("n - Naga.", 0, 0); break;
			case 'o': io.prt("o - Orc or Ogre.", 0, 0); break;
			case 'p': io.prt("p - Person (Humanoid).", 0, 0); break;
			case 'q': io.prt("q - Quasit.", 0, 0); break;
			case 'r': io.prt("r - Rodent.", 0, 0); break;
			case 's': io.prt("s - Skeleton.", 0, 0); break;
			case 't': io.prt("t - Giant Tick.", 0, 0); break;
			/* case 'u': io.prt(("u - Not used.", 0, 0); break; */
			/* case 'v': io.prt(("v - Not used.", 0, 0); break; */
			case 'w': io.prt("w - Worm or Worm Mass.", 0, 0); break;
			/* case 'x': io.prt(("x - Not used.", 0, 0); break; */
			case 'y': io.prt("y - Yeek.", 0, 0); break;
			case 'z': io.prt("z - Zombie.", 0, 0); break;
			case '{': io.prt("{ - Arrow, bolt, or bullet.", 0, 0); break;
			case '|': io.prt("| - A sword or dagger.", 0, 0); break;
			case '}': io.prt("} - Bow, crossbow, or sling.", 0, 0); break;
			case '~': io.prt("~ - Miscellaneous item.", 0, 0); break;
			default:  io.prt("Not Used.", 0, 0); break;
			}
		}
		/* Allow access to monster memory. -CJS- */
		n = 0;
		for (i = Constants.MAX_CREATURES-1; i >= 0; i--) {
			if ((mon.c_list[i].cchar == command.value()) && recall.bool_roff_recall(i)) {
				if (n == 0) {
					io.put_buffer("You recall those details? [y/n]", 0, 40);
					query = io.inkey();
					if (query != 'y' && query != 'Y') {
						break;
					}
					io.erase_line (0, 40);
					io.save_screen();
				}
				n++;
				query = recall.roff_recall(i);
				io.restore_screen();
				if (query == Constants.ESCAPE) {
					break;
				}
			}
		}
	}
}
