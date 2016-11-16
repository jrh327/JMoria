/*
 * Save.java: save and restore games and monster memory info
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import net.jonhopkins.moria.types.BooleanPointer;
import net.jonhopkins.moria.types.CaveType;
import net.jonhopkins.moria.types.PlayerFlags;
import net.jonhopkins.moria.types.HighScoreType;
import net.jonhopkins.moria.types.IntPointer;
import net.jonhopkins.moria.types.InvenType;
import net.jonhopkins.moria.types.PlayerMisc;
import net.jonhopkins.moria.types.MonsterType;
import net.jonhopkins.moria.types.MonsterRecallType;
import net.jonhopkins.moria.types.PlayerStats;
import net.jonhopkins.moria.types.StoreType;

public class Save {
	/* For debugging the savefile code on systems with broken compilers.  */
	
	//	#define DEBUG(x)
	//	DEBUG(static FILE *logfile);
	
	/* these are used for the save file, to avoid having to pass them to every
	 * procedure */
	private static File fileptr;
	private static InputStream fis;
	private static OutputStream fos;
	private static byte[] bytes;
	private static int index;
	private static byte xorByte;
	private static int fromSavefile;	/* can overwrite old savefile when save */
	private static long startTime;	/* time that play started */
	
	private static final int SIZEOF_CHAR = 2;
	private static final int SIZEOF_INT  = 4;
	private static final int SIZEOF_LONG = 8;
	
	private Save() { }
	
	/* This save package was brought to by			-JWT-
	 * and							-RAK-
	 * and has been completely rewritten for UNIX by	-JEW-  */
	/* and has been completely rewritten again by	 -CJS-	*/
	/* and completely rewritten again! for portability by -JEW- */
	/* and completely rewritten yet again for java by -JRH- */
	
	private static boolean saveWrite() {
		long l;
		int i, j;
		int count;
		short char_tmp, prev_char;
		CaveType c_ptr;
		MonsterRecallType r_ptr;
		PlayerStats s_ptr;
		PlayerFlags f_ptr;
		StoreType st_ptr;
		PlayerMisc m_ptr;
		InvenType t_ptr;
		
		index = 0;
		bytes = new byte[512];
		
		/* clear the death flag when creating a HANGUP save file, so that player
		 * can see tombstone when restart */
		if (Variable.eofFlag > 0) {
			Variable.death = false;
		}
		
		l = 0;
		if (Variable.findCut.value()) {
			l |= 0x1;
		}
		if (Variable.findExamine.value()) {
			l |= 0x2;
		}
		if (Variable.findPrself.value()) {
			l |= 0x4;
		}
		if (Variable.findBound.value()) {
			l |= 0x8;
		}
		if (Variable.promptCarryFlag.value()) {
			l |= 0x10;
		}
		if (Variable.rogueLikeCommands.value()) {
			l |= 0x20;
		}
		if (Variable.showWeightFlag.value()) {
			l |= 0x40;
		}
		if (Variable.highlightSeams.value()) {
			l |= 0x80;
		}
		if (Variable.findIgnoreDoors.value()) {
			l |= 0x100;
		}
		if (Variable.soundBeepFlag.value()) {
			l |= 0x200;
		}
		if (Variable.displayCounts.value()) {
			l |= 0x400;
		}
		if (Variable.death) {
			l |= 0x80000000L;	/* Sign bit */
		}
		if (Variable.isTotalWinner) {
			l |= 0x40000000L;
		}
		
		for (i = 0; i < Constants.MAX_CREATURES; i++) {
			r_ptr = Variable.creatureRecall[i];
			if (r_ptr.cmove != 0 || r_ptr.cdefense != 0 || r_ptr.kills != 0
					|| r_ptr.spells != 0 || r_ptr.deaths != 0 || r_ptr.attacks[0] != 0
					|| r_ptr.attacks[1] != 0 || r_ptr.attacks[2] != 0 || r_ptr.attacks[3] != 0)
			{
				writeInt(i);
				writeLong(r_ptr.cmove);
				writeLong(r_ptr.spells);
				writeInt(r_ptr.kills);
				writeInt(r_ptr.deaths);
				writeInt(r_ptr.cdefense);
				writeInt(r_ptr.wake);
				writeInt(r_ptr.ignore);
				writeInts(r_ptr.attacks, Constants.MAX_MON_NATTACK);
			}
		}
		writeInt(0xFFFF); /* sentinel to indicate no more monster info */
		
		writeLong(l);
		
		m_ptr = Player.py.misc;
		writeString(m_ptr.name, Player.py.PLAYER_NAME_SIZE);
		writeByte((m_ptr.isMale ? (byte)1 : (byte)0));
		writeInt(m_ptr.gold);
		writeInt(m_ptr.maxExp);
		writeInt(m_ptr.currExp);
		writeInt(m_ptr.expFraction);
		writeInt(m_ptr.age);
		writeInt(m_ptr.height);
		writeInt(m_ptr.weight);
		writeInt(m_ptr.level);
		writeInt(m_ptr.maxDungeonLevel);
		writeInt(m_ptr.searchChance);
		writeInt(m_ptr.freqOfSearch);
		writeInt(m_ptr.baseToHit);
		writeInt(m_ptr.baseToHitBow);
		writeInt(m_ptr.maxMana);
		writeInt(m_ptr.maxHitpoints);
		writeInt(m_ptr.plusToHit);
		writeInt(m_ptr.plusToDamage);
		writeInt(m_ptr.totalArmorClass);
		writeInt(m_ptr.magicArmorClass);
		writeInt(m_ptr.displayPlusToHit);
		writeInt(m_ptr.displayPlusToDamage);
		writeInt(m_ptr.displayPlusToArmorClass);
		writeInt(m_ptr.displayPlusTotalArmorClass);
		writeInt(m_ptr.disarmChance);
		writeInt(m_ptr.savingThrow);
		writeInt(m_ptr.socialClass);
		writeInt(m_ptr.stealth);
		writeInt(m_ptr.playerClass);
		writeInt(m_ptr.playerRace);
		writeInt(m_ptr.hitDie);
		writeInt(m_ptr.expFactor);
		writeInt(m_ptr.currMana);
		writeInt(m_ptr.currManaFraction);
		writeInt(m_ptr.currHitpoints);
		writeInt(m_ptr.currHitpointsFraction);
		for (i = 0; i < 4; i++) {
			writeString(m_ptr.history[i], 60);
		}
		
		s_ptr = Player.py.stats;
		writeInts(s_ptr.maxStat, 6);
		writeInts(s_ptr.curStat, 6);
		writeInts(s_ptr.modStat, 6);
		writeInts(s_ptr.useStat, 6);
		
		f_ptr = Player.py.flags;
		writeLong(f_ptr.status);
		writeInt(f_ptr.rest);
		writeInt(f_ptr.blind);
		writeInt(f_ptr.paralysis);
		writeInt(f_ptr.confused);
		writeInt(f_ptr.food);
		writeInt(f_ptr.foodDigested);
		writeInt(f_ptr.protection);
		writeInt(f_ptr.speed);
		writeInt(f_ptr.fast);
		writeInt(f_ptr.slow);
		writeInt(f_ptr.afraid);
		writeInt(f_ptr.poisoned);
		writeInt(f_ptr.imagine);
		writeInt(f_ptr.protectFromEvil);
		writeInt(f_ptr.invulnerability);
		writeInt(f_ptr.hero);
		writeInt(f_ptr.superHero);
		writeInt(f_ptr.blessed);
		writeInt(f_ptr.resistHeat);
		writeInt(f_ptr.resistCold);
		writeInt(f_ptr.detectInvisible);
		writeInt(f_ptr.wordRecall);
		writeInt(f_ptr.seeInfrared);
		writeInt(f_ptr.timedSeeInfrared);
		writeByte((f_ptr.seeInvisible ? (byte)1 : (byte)0));
		writeByte((byte)f_ptr.teleport);
		writeByte((f_ptr.freeAct ? (byte)1 : (byte)0));
		writeByte((f_ptr.slowDigestion ? (byte)1 : (byte)0));
		writeByte((byte)f_ptr.aggravate);
		writeByte((byte)f_ptr.fireResistance);
		writeByte((byte)f_ptr.coldResistance);
		writeByte((byte)f_ptr.acidResistance);
		writeByte((f_ptr.regenerate ? (byte)1 : (byte)0));
		writeByte((byte)f_ptr.lightningResistance);
		writeByte((byte)f_ptr.freeFall);
		writeByte((f_ptr.sustainStr ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustainInt ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustainWis ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustainCon ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustainDex ? (byte)1 : (byte)0));
		writeByte((f_ptr.sustainChr ? (byte)1 : (byte)0));
		writeByte((f_ptr.confuseMonster ? (byte)1 : (byte)0));
		writeInt(f_ptr.newSpells);
		
		writeInt(Variable.missileCounter);
		writeInt(Variable.turn);
		writeInt(Treasure.invenCounter);
		for (i = 0; i < Treasure.invenCounter; i++) {
			writeItem(Treasure.inventory[i]);
		}
		for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
			writeItem(Treasure.inventory[i]);
		}
		writeInt(Treasure.invenWeight);
		writeInt(Treasure.equipCounter);
		writeLong(Player.spellLearned);
		writeLong(Player.spellWorked);
		writeLong(Player.spellForgotten);
		writeInts(Player.spellOrder, 32);
		writeInts(Treasure.objectIdent, Constants.OBJECT_IDENT_SIZE);
		writeLong(Variable.randesSeed);
		writeLong(Variable.townSeed);
		writeInt(Variable.lastMsg);
		for (i = 0; i < Constants.MAX_SAVE_MSG; i++) {
			writeString(Variable.oldMsg[i], Constants.VTYPESIZ);
		}
		
		/* this indicates 'cheating' if it is a one */
		writeInt(Variable.panicSave);
		writeByte((Variable.isTotalWinner ? (byte)1 : (byte)0));
		writeInt(Variable.noScore);
		writeInts(Player.hitpoints, Constants.MAX_PLAYER_LEVEL);
		
		for (i = 0; i < Constants.MAX_STORES; i++) {
			st_ptr = Variable.store[i];
			writeLong((long)st_ptr.storeOpen);
			writeInt(st_ptr.currInsult);
			writeByte((byte)st_ptr.owner);
			writeByte((byte)st_ptr.storeCounter);
			writeInt(st_ptr.goodBuy);
			writeInt(st_ptr.badBuy);
			for (j = 0; j < st_ptr.storeCounter; j++) {
				writeLong((long)st_ptr.storeInven[j].cost);
				writeItem(st_ptr.storeInven[j].item);
			}
		}
		
		/* save the current time in the savefile */
		l = java.util.Calendar.getInstance().getTimeInMillis();
		if (l < startTime) {
			/* someone is messing with the clock!, assume that we have been
			 * playing for 1 day */
			l = startTime + 86400L;
		}
		writeLong(l);
		
		/* starting with 5.2, put died_from string in savefile */
		writeString(Variable.diedFrom, 25);
		
		/* starting with 5.2.2, put the max_score in the savefile */
		l = Death.getTotalPoints();
		writeLong(l);
		
		/* starting with 5.2.2, put the birth_date in the savefile */
		writeLong(Variable.birthDate);
		
		/* only level specific info follows, this allows characters to be
		 * resurrected, the dungeon level info is not needed for a resurrection */
		if (Variable.death) {
			/*if (ferror(fileptr) || fflush(fileptr) == EOF) {
				return false;
			}*/
			return true;
		}
		
		writeInt(Variable.dungeonLevel);
		writeInt(Player.y);
		writeInt(Player.x);
		writeInt(Monsters.totalMonsterMultiples);
		writeInt(Variable.currHeight);
		writeInt(Variable.currWidth);
		writeInt(Variable.maxPanelRows);
		writeInt(Variable.maxPanelCols);
		
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.creatureIndex != 0) {
					writeByte((byte)i);
					writeByte((byte)j);
					writeByte((byte)c_ptr.creatureIndex);
				}
			}
		}
		writeByte((byte)0xFF); /* marks end of cptr info */
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				if (c_ptr.treasureIndex != 0) {
					writeByte((byte)i);
					writeByte((byte)j);
					writeByte((byte)c_ptr.treasureIndex);
				}
			}
		}
		writeByte((byte)0xFF); /* marks end of tptr info */
		/* must set counter to zero, note that code may write out two bytes
		 * unnecessarily */
		count = 0;
		prev_char = 0;
		for (i = 0; i < Constants.MAX_HEIGHT; i++) {
			for (j = 0; j < Constants.MAX_WIDTH; j++) {
				c_ptr = Variable.cave[i][j];
				char_tmp = (short)(c_ptr.fval | ((c_ptr.litRoom ? 1 : 0) << 4) | ((c_ptr.fieldMark ? 1 : 0) << 5) | ((c_ptr.permLight ? 1 : 0) << 6) | ((c_ptr.tempLight ? 1 : 0) << 7));
				if (char_tmp != prev_char || count == Constants.MAX_UCHAR) {
					writeByte((byte)count);
					writeByte((byte)prev_char);
					prev_char = char_tmp;
					count = 1;
				} else {
					count++;
				}
			}
		}
		/* save last entry */
		writeByte((byte)count);
		writeByte((byte)prev_char);
		
		/* must change graphics symbols for walls and floors back to default chars,
		 * this is necessary so that if the user changes the graphics line, the
		 * program will be able change all existing walls and floors to the new
		 * symbol */
		/* Or if the user moves the savefile from one machine to another, we
		 * must have a consistent representation here.  */
		for (i = Treasure.currTreasureIndex - 1; i >= Constants.MIN_TRIX; i--) {
			t_ptr = Treasure.treasureList[i];
			if (t_ptr.tchar == Variable.wallSymbol) {
				t_ptr.tchar = '#';
			}
			if (t_ptr.tchar == 240) {
				t_ptr.tchar = '#';
			}
		}
		writeInt(Treasure.currTreasureIndex);
		for (i = Constants.MIN_TRIX; i < Treasure.currTreasureIndex; i++) {
			writeItem(Treasure.treasureList[i]);
		}
		writeInt(Monsters.freeMonsterIndex);
		for (i = Constants.MIN_MONIX; i < Monsters.freeMonsterIndex; i++) {
			writeMonster(Monsters.monsterList[i]);
		}
		
		/*if (ferror(fileptr) || (fflush(fileptr) == EOF)) {
			return false;
		}*/
		
		// truncate any extra length
		bytes = Arrays.copyOf(bytes, index);
		
		try {
			fos = new BufferedOutputStream(new FileOutputStream(fileptr));
			fos.write(bytes);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean saveCharacter() {
		int i;
		String temp;
		
		while (!saveCharacter(Variable.savefile)) {
			temp = String.format("Savefile '%s' fails.", Variable.savefile);
			IO.printMessage(temp);
			i = 0;
/*			if (access(var.savefile, 0) < 0 || !io.get_check("File exists. Delete old savefile?") || (i = unlink(var.savefile)) < 0) {
				if (i < 0) {
					temp = String.format("Can't delete '%s'", var.savefile);
					io.msg_print(temp);
				}
				io.prt("New Savefile [ESC to give up]:", 0, 0);
				temp = io.get_string(0, 31, 45);
				if (temp.length() == 0) {
					return false;
				}
				if (!temp.equals("")) {
					var.savefile = temp;
				}
			}
*/
			temp = String.format("Saving with %s...", Variable.savefile);
			IO.print(temp, 0, 0);
		}
		return true;
	}
	
	private static boolean saveCharacter(String fnam) {
		String temp;
		boolean ok;
		int fd = 0;
		short char_tmp;
		
		if (Variable.characterSaved != 0) {
			return true;	/* Nothing to save. */
		}
		
		Signals.noSignals();
		IO.putQio();
		Moria1.disturbPlayer(true, false);	/* Turn off resting and searching. */
		Moria1.changeSpeed(-Variable.isPackHeavy);	/* Fix the speed */
		Variable.isPackHeavy = 0;
		ok = false;
//		fileptr = fopen(var.savefile, "w");
		
		if (fileptr != null) {
			xorByte = 0;
			writeInt(Constants.CUR_VERSION_MAJ);
			xorByte = 0;
			writeInt(Constants.CUR_VERSION_MIN);
			xorByte = 0;
			writeInt(Constants.PATCH_LEVEL);
			xorByte = 0;
			char_tmp = (short)(Misc1.randomInt(256) - 1);
			writeByte((byte)char_tmp);
			/* Note that xor_byte is now equal to char_tmp */
			
			ok = saveWrite();
			
//			if (fclose(fileptr) == EOF) {
//				ok = false;
//			}
		}
		
		if (!ok) {
			if (fd >= 0) {
//				unlink(fnam);
			}
			Signals.signals();
			if (fd >= 0) {
				temp = String.format("Error writing to file %s", fnam);
			} else {
				temp = String.format("Can't create new file %s", fnam);
			}
			IO.printMessage(temp);
			return false;
		} else {
			Variable.characterSaved = 1;
		}
		
		Variable.turn = -1;
		Signals.signals();
		
		return true;
	}
	
	/* Certain checks are omitted for the wizard. -CJS- */
	
	public static boolean getCharacter(BooleanPointer generate) {
		int i, j;
		int fd, c, total_count;
		boolean ok;
		long l, age, time_saved = 0;
		String temp;
		int int_tmp;
		CaveType c_ptr;
		MonsterRecallType r_ptr;
		PlayerMisc m_ptr;
		PlayerStats s_ptr;
		PlayerFlags f_ptr;
		StoreType st_ptr;
		int char_tmp, ychar, xchar, count;
		int version_maj = 0, version_min = 0, patch_level = 0;
		InvenType t_ptr;
		
		Signals.noSignals();
		generate.value(true);
		fd = -1;
		
		DataInputStream dis;
		
		try {
			dis = new DataInputStream(new FileInputStream(fileptr));
			bytes = new byte[(int)fileptr.length()];
			index = 0;
			dis.readFully(bytes);
			dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
//		if (access(var.savefile, 0) != 0) {
//			sigs.signals();
//			io.msg_print("Savefile does not exist.");
//			return false;	/* Don't bother with messages here. File absent. */
//		}
		
		IO.clearScreen();
		
		temp = String.format("Savefile %s present. Attempting restore.", Variable.savefile);
		IO.putBuffer(temp, 23, 0);
		
		if (Variable.turn >= 0) {
			IO.printMessage("IMPOSSIBLE! Attempt to restore while still alive!");
		
		/* Allow restoring a file belonging to someone else, if we can delete it. */
		/* Hence first try to read without doing a chmod. */
		
//		} else if ((fd = open(var.savefile, O_RDONLY, 0)) < 0 && (chmod(var.savefile, 0400) < 0 || (fd = open(var.savefile, O_RDONLY, 0)) < 0)) {
//			io.msg_print("Can't open file for reading.");
		} else {
			Variable.turn = -1;
			ok = true;
			
//			close(fd);
//			fileptr = fopen(var.savefile, "r");
			if (fileptr == null) {
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			
			IO.print("Restoring Memory...", 0, 0);
			IO.putQio();
			
			xorByte = 0;
			version_maj = readByte();
			xorByte = 0;
			version_min = readByte();
			xorByte = 0;
			patch_level = readByte();
			xorByte = 0;
			xorByte = readByte();
			
			/* COMPAT support savefiles from 5.0.14 to 5.0.17 */
			/* support savefiles from 5.1.0 to present */
			if ((version_maj != Constants.CUR_VERSION_MAJ) || (version_min == 0 && patch_level < 14)) {
				IO.print("Sorry. This savefile is from a different version of umoria.", 2, 0);
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			
			int_tmp = readInt();
			while (int_tmp != 0xFFFF) {
				if (int_tmp >= Constants.MAX_CREATURES) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				r_ptr = Variable.creatureRecall[int_tmp];
				r_ptr.cmove = readInt();
				r_ptr.spells = readInt();
				r_ptr.kills = readInt();
				r_ptr.deaths = readInt();
				r_ptr.cdefense = readInt();
				r_ptr.wake = readByte();
				r_ptr.ignore = readByte();
				readInts(r_ptr.attacks, Constants.MAX_MON_NATTACK);
				int_tmp = readInt();
			}
			
			/* for save files before 5.2.2, read and ignore log_index (sic) */
			if ((version_min < 2) || (version_min == 2 && patch_level < 2)) {
				int_tmp = readInt();
			}
			l = readLong();
			
			Variable.findCut.value((l & 0x1) != 0);
			Variable.findExamine.value((l & 0x2) != 0);
			Variable.findPrself.value((l & 0x4) != 0);
			Variable.findBound.value((l & 0x8) != 0);
			Variable.promptCarryFlag.value((l & 0x10) != 0);
			Variable.rogueLikeCommands.value((l & 0x20) != 0);
			Variable.showWeightFlag.value((l & 0x40) != 0);
			Variable.highlightSeams.value((l & 0x80) != 0);
			Variable.findIgnoreDoors.value((l & 0x100) != 0);
			/* save files before 5.2.2 don't have sound_beep_flag, set it on
			 * for compatibility */
			if ((version_min < 2) || (version_min == 2 && patch_level < 2)) {
				Variable.soundBeepFlag.value(true);
			} else {
				Variable.soundBeepFlag.value((l & 0x200) != 0);
			}
			/* save files before 5.2.2 don't have display_counts, set it on
			 * for compatibility */
			if ((version_min < 2) || (version_min == 2 && patch_level < 2)) {
				Variable.displayCounts.value(true);
			} else {
				Variable.displayCounts.value((l & 0x400) != 0);
			}
			
			/* Don't allow resurrection of total_winner characters.  It causes
			 * problems because the character level is out of the allowed range.  */
			if (Variable.toBeWizard && (l & 0x40000000L) != 0) {
				IO.printMessage("Sorry, this character is retired from moria.");
				IO.printMessage("You can not resurrect a retired character.");
			} else if (Variable.toBeWizard && (l & 0x80000000L) != 0 && IO.getCheck("Resurrect a dead character?")) {
				l &= ~0x80000000L;
			}
			if ((l & 0x80000000L) == 0) {
				m_ptr = Player.py.misc;
				m_ptr.name = readString(Player.py.PLAYER_NAME_SIZE);
				m_ptr.isMale = (readByte() == 1);
				m_ptr.gold = readInt();
				m_ptr.maxExp = readInt();
				m_ptr.currExp = readInt();
				m_ptr.expFraction = readInt();
				m_ptr.age = readInt();
				m_ptr.height = readInt();
				m_ptr.weight = readInt();
				m_ptr.level = readInt();
				m_ptr.maxDungeonLevel = readInt();
				m_ptr.searchChance = readInt();
				m_ptr.freqOfSearch = readInt();
				m_ptr.baseToHit = readInt();
				m_ptr.baseToHitBow = readInt();
				m_ptr.maxMana = readInt();
				m_ptr.maxHitpoints = readInt();
				m_ptr.plusToHit = readInt();
				m_ptr.plusToDamage = readInt();
				m_ptr.totalArmorClass = readInt();
				m_ptr.magicArmorClass = readInt();
				m_ptr.displayPlusToHit = readInt();
				m_ptr.displayPlusToDamage = readInt();
				m_ptr.displayPlusToArmorClass = readInt();
				m_ptr.displayPlusTotalArmorClass = readInt();
				m_ptr.disarmChance = readInt();
				m_ptr.savingThrow = readInt();
				m_ptr.socialClass = readInt();
				m_ptr.stealth = readInt();
				m_ptr.playerClass = readByte();
				m_ptr.playerRace = readByte();
				m_ptr.hitDie = readByte();
				m_ptr.expFactor = readByte();
				m_ptr.currMana = readInt();
				m_ptr.currManaFraction = readInt();
				m_ptr.currHitpoints = readInt();
				m_ptr.currHitpointsFraction = readInt();
				for (i = 0; i < 4; i++) {
					m_ptr.history[i] = readString(60);
				}
				
				s_ptr = Player.py.stats;
				readInts(s_ptr.maxStat, 6);
				readInts(s_ptr.curStat, 6);
				readInts(s_ptr.modStat, 6);
				readInts(s_ptr.useStat, 6);
				
				f_ptr = Player.py.flags;
				f_ptr.status = readLong();
				f_ptr.rest = readInt();
				f_ptr.blind = readInt();
				f_ptr.paralysis = readInt();
				f_ptr.confused = readInt();
				f_ptr.food = readInt();
				f_ptr.foodDigested = readInt();
				f_ptr.protection = readInt();
				f_ptr.speed = readInt();
				f_ptr.fast = readInt();
				f_ptr.slow = readInt();
				f_ptr.afraid = readInt();
				f_ptr.poisoned = readInt();
				f_ptr.imagine = readInt();
				f_ptr.protectFromEvil = readInt();
				f_ptr.invulnerability = readInt();
				f_ptr.hero = readInt();
				f_ptr.superHero = readInt();
				f_ptr.blessed = readInt();
				f_ptr.resistHeat = readInt();
				f_ptr.resistCold = readInt();
				f_ptr.detectInvisible = readInt();
				f_ptr.wordRecall = readInt();
				f_ptr.seeInfrared = readInt();
				f_ptr.timedSeeInfrared = readInt();
				f_ptr.seeInvisible = (readByte() == 1);
				f_ptr.teleport = readByte();
				f_ptr.freeAct = (readByte() == 1);
				f_ptr.slowDigestion = (readByte() == 1);
				f_ptr.aggravate = readByte();
				f_ptr.fireResistance = readByte();
				f_ptr.coldResistance = readByte();
				f_ptr.acidResistance = readByte();
				f_ptr.regenerate = (readByte() == 1);
				f_ptr.lightningResistance = readByte();
				f_ptr.freeFall = readByte();
				f_ptr.sustainStr = (readByte() == 1);
				f_ptr.sustainInt = (readByte() == 1);
				f_ptr.sustainWis = (readByte() == 1);
				f_ptr.sustainCon = (readByte() == 1);
				f_ptr.sustainDex = (readByte() == 1);
				f_ptr.sustainChr = (readByte() == 1);
				f_ptr.confuseMonster = (readByte() == 1);
				f_ptr.newSpells = readByte();
				
				Variable.missileCounter = readInt();
				Variable.turn = readInt();
				Treasure.invenCounter = readInt();
				if (Treasure.invenCounter > Constants.INVEN_WIELD) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				for (i = 0; i < Treasure.invenCounter; i++) {
					readItem(Treasure.inventory[i]);
				}
				for (i = Constants.INVEN_WIELD; i < Constants.INVEN_ARRAY_SIZE; i++) {
					readItem(Treasure.inventory[i]);
				}
				Treasure.invenWeight = readInt();
				Treasure.equipCounter = readInt();
				Player.spellLearned = readInt();
				Player.spellWorked = readInt();
				Player.spellForgotten = readInt();
				readInts(Player.spellOrder, 32);
				readInts(Treasure.objectIdent, Constants.OBJECT_IDENT_SIZE);
				Variable.randesSeed = readLong();
				Variable.townSeed = readLong();
				Variable.lastMsg = readInt();
				for (i = 0; i < Constants.MAX_SAVE_MSG; i++) {
					Variable.oldMsg[i] = readString(Constants.VTYPESIZ);
				}
				
				Variable.panicSave = readInt();
				Variable.isTotalWinner = (readInt() == 1);
				Variable.noScore = readInt();
				readInts(Player.hitpoints, Constants.MAX_PLAYER_LEVEL);
				
				if ((version_min >= 2) || (version_min == 1 && patch_level >= 3)) {
					for (i = 0; i < Constants.MAX_STORES; i++) {
						st_ptr = Variable.store[i];
						st_ptr.storeOpen = readInt();
						st_ptr.currInsult = readInt();
						st_ptr.owner = readByte();
						st_ptr.storeCounter = readByte();
						st_ptr.goodBuy = readInt();
						st_ptr.badBuy = readInt();
						if (st_ptr.storeCounter > Constants.STORE_INVEN_MAX) {
							return error(ok, fd, time_saved, version_maj, version_min);
						}
						for (j = 0; j < st_ptr.storeCounter; j++) {
							st_ptr.storeInven[j].cost = readInt();
							readItem(st_ptr.storeInven[j].item);
						}
					}
				}
				
				if ((version_min >= 2) || (version_min == 1 && patch_level >= 3)) {
					time_saved = readLong();
				}
				
				if (version_min >= 2) {
					Variable.diedFrom = readString(25);
				}
				
				if ((version_min >= 3) || (version_min == 2 && patch_level >= 2)) {
					Variable.maxScore = readInt();
				} else {
					Variable.maxScore = 0;
				}
				
				if ((version_min >= 3) || (version_min == 2 && patch_level >= 2)) {
					Variable.birthDate = readLong();
				} else {
					Variable.birthDate = java.util.Calendar.getInstance().getTimeInMillis();
				}
			}
			if (/*(c = getc(fileptr)) == EOF ||*/ (l & 0x80000000L) != 0) {
				if ((l & 0x80000000L) == 0) {
					if (!Variable.toBeWizard || Variable.turn < 0) {
						return error(ok, fd, time_saved, version_maj, version_min);
					}
					IO.print("Attempting a resurrection!", 0, 0);
					if (Player.py.misc.currHitpoints < 0) {
						Player.py.misc.currHitpoints =	 0;
						Player.py.misc.currHitpointsFraction = 0;
					}
					/* don't let him starve to death immediately */
					if (Player.py.flags.food < 0) {
						Player.py.flags.food = 0;
					}
					/* don't let him die of poison again immediately */
					if (Player.py.flags.poisoned > 1) {
						Player.py.flags.poisoned = 1;
					}
					Variable.dungeonLevel = 0; /* Resurrect on the town level. */
					Variable.isCharacterGenerated = true;
					/* set noscore to indicate a resurrection, and don't enter
					 * wizard mode */
					Variable.toBeWizard = false;
					Variable.noScore |= 0x1;
				} else {
					/* Make sure that this message is seen, since it is a bit
					 * more interesting than the other messages.  */
					IO.printMessage("Restoring Memory of a departed spirit...");
					Variable.turn = -1;
				}
				IO.putQio();
				return closeFiles(ok, fd, time_saved, version_maj, version_min);
			}
//			if (ungetc(c, fileptr) == EOF) {
//				return _error(ok, fd, time_saved, version_maj, version_min);
//			}
			
			IO.print("Restoring Character...", 0, 0);
			IO.putQio();
			
			/* only level specific info should follow, not present for dead
			 * characters */
			
			Variable.dungeonLevel = readInt();
			Player.y = readInt();
			Player.x = readInt();
			Monsters.totalMonsterMultiples = readInt();
			Variable.currHeight = readInt();
			Variable.currWidth = readInt();
			Variable.maxPanelRows = readInt();
			Variable.maxPanelCols = readInt();
			
			/* read in the creature ptr info */
			char_tmp = readByte() & 0xFF;
			while (char_tmp != 0xFF) {
				ychar = char_tmp;
				xchar = readByte() & 0xFF;
				char_tmp = readByte() & 0xFF;
				if (xchar > Constants.MAX_WIDTH || ychar > Constants.MAX_HEIGHT) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				Variable.cave[ychar][xchar].creatureIndex = char_tmp;
				char_tmp = readByte();
			}
			/* read in the treasure ptr info */
			char_tmp = readByte() & 0xFF;
			while (char_tmp != 0xFF) {
				ychar = char_tmp;
				xchar = readByte() & 0xFF;
				char_tmp = readByte() & 0xFF;
				if (xchar > Constants.MAX_WIDTH || ychar > Constants.MAX_HEIGHT) {
					return error(ok, fd, time_saved, version_maj, version_min);
				}
				Variable.cave[ychar][xchar].treasureIndex = char_tmp;
				char_tmp = readByte() & 0xFF;
			}
			/* read in the rest of the cave info */
			c_ptr = Variable.cave[0][0];
			total_count = 0;
			while (total_count != Constants.MAX_HEIGHT * Constants.MAX_WIDTH) {
				count = readByte();
				char_tmp = readByte();
//				for (i = count; i > 0; i--) {
//					if (c_ptr >= var.cave[Constants.MAX_HEIGHT][0]) {
//						return _error(ok, fd, time_saved, version_maj, version_min);
//					}
//					c_ptr.fval = char_tmp & 0xF;
//					c_ptr.lr = (((char_tmp >> 4) & 0x1) == 1);
//					c_ptr.fm = (((char_tmp >> 5) & 0x1) == 1);
//					c_ptr.pl = (((char_tmp >> 6) & 0x1) == 1);
//					c_ptr.tl = (((char_tmp >> 7) & 0x1) == 1);
//					c_ptr++;
//				}
				total_count += count;
			}
			
			Treasure.currTreasureIndex = readInt();
			if (Treasure.currTreasureIndex > Constants.MAX_TALLOC) {
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			for (i = Constants.MIN_TRIX; i < Treasure.currTreasureIndex; i++) {
				readItem(Treasure.treasureList[i]);
			}
			Monsters.freeMonsterIndex = readInt();
			if (Monsters.freeMonsterIndex > Constants.MAX_MALLOC) {
				return error(ok, fd, time_saved, version_maj, version_min);
			}
			for (i = Constants.MIN_MONIX; i < Monsters.freeMonsterIndex; i++) {
				readMonster(Monsters.monsterList[i]);
			}
			
			/* change walls and floors to graphic symbols */
			for (i = Treasure.currTreasureIndex - 1; i >= Constants.MIN_TRIX; i--) {
				t_ptr = Treasure.treasureList[Treasure.currTreasureIndex - 1];
				if (t_ptr.tchar == '#') {
					t_ptr.tchar = Variable.wallSymbol;
				}
			}
			
			generate.value(false);  /* We have restored a cave - no need to generate. */
			
			if ((version_min == 1 && patch_level < 3) || (version_min == 0)) {
				for (i = 0; i < Constants.MAX_STORES; i++) {
					st_ptr = Variable.store[i];
					st_ptr.storeOpen = readInt();
					st_ptr.currInsult = readInt();
					st_ptr.owner = readByte();
					st_ptr.storeCounter = readByte();
					st_ptr.goodBuy = readInt();
					st_ptr.badBuy = readInt();
					if (st_ptr.storeCounter > Constants.STORE_INVEN_MAX) {
						return error(ok, fd, time_saved, version_maj, version_min);
					}
					for (j = 0; j < st_ptr.storeCounter; j++) {
						st_ptr.storeInven[j].cost = readInt();
						readItem(st_ptr.storeInven[j].item);
					}
				}
			}
			
			/* read the time that the file was saved */
			if (version_min == 0 && patch_level < 16) {
				time_saved = 0; /* no time in file, clear to zero */
			} else if (version_min == 1 && patch_level < 3) {
				time_saved = readLong();
			}
			
//			if (ferror(fileptr)) {
//				return _error(ok, fd, time_saved, version_maj, version_min);
//			}
			
			if (Variable.turn < 0) {
				return error(ok, fd, time_saved, version_maj, version_min);	/* Assume bad data. */
			} else {
				/* don't overwrite the killed by string if character is dead */
				if (Player.py.misc.currHitpoints >= 0) {
					Variable.diedFrom = "(alive and well)";
				}
				Variable.isCharacterGenerated = true;
			}
			
			closeFiles(ok, fd, time_saved, version_maj, version_min);
		}
		
		Variable.turn = -1;
		IO.print("Please try again without that savefile.", 1, 0);
		Signals.signals();
		Death.exitGame();
		
		return false;	/* not reached, unless on mac */
	}
	
	private static boolean error(boolean ok, int fd, long time_saved, int version_maj, int version_min) {
//		return closefiles(ok, fd, time_saved, version_maj, version_min);
		return false;
	}
	
	private static boolean closeFiles(boolean ok, int fd, long time_saved, int version_maj, int version_min) {
		long age;
		String temp;
		
//		if (fileptr != null) {
//			if (fclose(fileptr) < 0) {
//				ok = false;
//			}
//		}
//		if (fd >= 0) {
//			close(fd);
//		}
		
		if (!ok) {
			IO.printMessage("Error during reading of file.");
		} else {
			/* let the user overwrite the old savefile when save/quit */
			fromSavefile = 1;
			
			Signals.signals();
			
			if (Variable.panicSave == 1) {
				temp = String.format("This game is from a panic save.  Score will not be added to scoreboard.");
				IO.printMessage(temp);
			} else if ((Variable.noScore & 0x04) == 0 && Death.isDuplicateCharacter()) {
				temp = String.format("This character is already on the scoreboard; it will not be scored again.");
				IO.printMessage(temp);
				Variable.noScore |= 0x4;
			}
			
			if (Variable.turn >= 0) {
				/* Only if a full restoration. */
				Variable.isWeaponHeavy = false;
				Variable.isPackHeavy = 0;
				Misc3.checkStrength();
				
				/* rotate store inventory, depending on how old the save file */
				/* is foreach day old (rounded up), call store_maint */
				/* calculate age in seconds */
				startTime = java.util.Calendar.getInstance().getTimeInMillis();
				/* check for reasonable values of time here ... */
				if (startTime < time_saved) {
					age = 0;
				} else {
					age = startTime - time_saved;
				}
				
				age = (age + 43200L) / 86400L;  /* age in days */
				if (age > 10) age = 10; /* in case savefile is very old */
				for (int i = 0; i < age; i++) {
					Store1.storeInventoryInit();
				}
			}
			
			if (Variable.noScore == 0) {
				IO.printMessage("This save file cannot be used to get on the score board.");
			}
			
			if (version_maj != Constants.CUR_VERSION_MAJ || version_min != Constants.CUR_VERSION_MIN) {
				temp = String.format(
						"Save file version %d.%d %s on game version %d.%d.",
						version_maj, version_min,
						(version_min <= Constants.CUR_VERSION_MIN) ? "accepted" : "risky" ,
						Constants.CUR_VERSION_MAJ, Constants.CUR_VERSION_MIN);
				IO.printMessage(temp);
			}
			
			return Variable.turn >= 0;
			/* If false, only restored options and monster memory. */
		}
		return false;
	}
	
	private static void writeByte(byte c) {
		if (index >= bytes.length) {
			bytes = Arrays.copyOf(bytes, bytes.length * 2);
		}
		xorByte ^= c;
		bytes[index] = xorByte;
		index++;
	}
	
	private static void writeChar(char c) {
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			writeByte((byte)(c >> (8 * i)));
		}
	}
	
	private static void writeInt(int s) {
		for (int i = 0; i < SIZEOF_INT; i++) {
			writeByte((byte)(s >> (8 * i)));
		}
	}
	
	private static void writeLong(long l) {
		for (int i = 0; i < SIZEOF_LONG; i++) {
			writeByte((byte)(l >> (8 * i)));
		}
	}
	
	private static void writeBytes(byte[] c, int count) {
		for (int i = 0; i < count; i++) {
			writeByte(c[i]);
		}
	}
	
	private static void writeString(String str, int len) {
		char[] c = str.toCharArray();
		int i;
		
		for (i = 0; i < c.length; i++) {
			if (i == len) {
				break;
			}
			writeChar(c[i]);
		}
		for (; i < len; i++) {
			writeChar('\0');
		}
	}
	
	private static void writeInts(int[] s, int count) {
		int i;
		
		for (i = 0; i < s.length; i++) {
			if (i == count) {
				break;
			}
			writeInt(s[i]);
		}
		for (; i < count; i++) {
			writeInt(0);
		}
	}
	
	private static void writeItem(InvenType item) {
		writeInt(item.index);
		writeInt(item.specialName);
		writeString(item.inscription, item.INSCRIP_SIZE);
		writeInt(item.flags);
		writeInt(item.category);
		writeInt(item.tchar);
		writeInt(item.misc);
		writeInt(item.cost);
		writeInt(item.subCategory);
		writeInt(item.number);
		writeInt(item.weight);
		writeInt(item.tohit);
		writeInt(item.plusToDam);
		writeInt(item.armorClass);
		writeInt(item.plusToArmorClass);
		writeInts(item.damage, 2);
		writeInt(item.level);
		writeInt(item.identify);
	}
	
	private static void writeMonster(MonsterType mon) {
		writeInt(mon.hitpoints);
		writeInt(mon.sleep);
		writeInt(mon.speed);
		writeInt(mon.index);
		writeInt(mon.y);
		writeInt(mon.x);
		writeInt(mon.currDistance);
		writeByte((byte)(mon.monsterLight ? 0x1 : 0x0));
		writeInt(mon.stunned);
		writeByte((byte)((mon.confused > 0) ? 0x1 : 0x0));
	}
	
	private static byte getNextByte() {
		if (index >= bytes.length) {
			return 0;
		}
		byte b = bytes[index];
		index++;
		return b;
	}
	
	private static byte readByte() {
		byte c, ptr;
		
		c = getNextByte();
		ptr = (byte)((c ^ xorByte) & 0xFF);
		xorByte = c;
		
		return ptr;
	}
	
	private static char readChar() {
		char c = 0;
		
		for (int i = 0; i < SIZEOF_CHAR; i++) {
			c |= (readByte() & 0xFF) << (8 * i);
		}
		
		return c;
	}
	
	private static int readInt() {
		int s;
		s = 0;
		
		for (int i = 0; i < SIZEOF_INT; i++) {
			s |= (readByte() & 0xFF) << (8 * i);
		}
		
		return s;
	}
	
	private static long readLong() {
		long l = 0;
		
		for (int i = 0; i < SIZEOF_LONG; i++) {
			l |= (readByte() & 0xFF) << (8 * i);
		}
		
		return l;
	}
	
	private static void readBytes(byte[] ch_ptr, int count) {
		int i;
		if (count > ch_ptr.length) {
			count = ch_ptr.length;
		}
		for (i = 0; i < ch_ptr.length; i++) {
			ch_ptr[i] = readByte();
		}
		for (; i < count; i++) {
			readByte();
		}
	}
	
	private static String readString(int len) {
		char[] str = new char[len];
		int i;
		
		for (i = 0; i < len; i++) {
			char c = readChar();
			if (c == '\0') {
				break;
			}
			str[i] = c;
		}
		if (i < len) {
			// truncate empty characters
			str = Arrays.copyOf(str, i);
		}
		for (; i < len; i++) {
			readChar();
		}
		
		return new String(str);
	}
	
	private static void readInts(int[] ptr, int count) {
		int i;
		
		for (i = 0; i < ptr.length; i++) {
			if (i == count) {
				break;
			}
			ptr[i] = readInt();
		}
		for (; i < count; i++) {
			readInt();
		}
	}
	
	private static void readItem(InvenType item) {
		item.index = readInt();
		item.specialName = readByte();
		item.inscription = readString(item.INSCRIP_SIZE);
		item.flags = readInt();
		item.category = readByte();
		item.tchar = (char)readByte();
		item.misc = readInt();
		item.cost = readInt();
		item.subCategory = readByte();
		item.number = readByte();
		item.weight = readInt();
		item.tohit = readInt();
		item.plusToDam = readInt();
		item.armorClass = readInt();
		item.plusToArmorClass = readInt();
		readInts(item.damage, 2);
		item.level = readByte();
		item.identify = readByte();
	}
	
	private static void readMonster(MonsterType mon) {
		mon.hitpoints = readInt();
		mon.sleep = readInt();
		mon.speed = readInt();
		mon.index = readInt();
		mon.y = readByte();
		mon.x = readByte();
		mon.currDistance = readByte();
		mon.monsterLight = (readByte() == 1);
		mon.stunned = readByte();
		mon.confused = readByte();
	}
	
	/* functions called from death.c to implement the score file */
	
	/* set the local fileptr to the scorefile fileptr */
	public static void setFilePointer(java.io.File file) {
		fileptr = file;
	}
	
	public static void writeHighScore(HighScoreType score) {
		/* Save the encryption byte for robustness.  */
		writeByte(xorByte);
		
		writeInt(score.points);
		writeLong(score.birthDate);
		writeInt(score.uid);
		writeInt(score.maxHitpoints);
		writeInt(score.currHitpoints);
		writeInt(score.dungeonLevel);
		writeInt(score.level);
		writeInt(score.maxDungeonLevel);
		writeInt(score.sex);
		writeInt(score.race);
		writeInt(score.playerClass);
		writeString(score.name, Player.py.PLAYER_NAME_SIZE);
		writeString(score.diedFrom, 25);
	}
	
	public static void readHighScore(HighScoreType score) {
		/* Read the encryption byte.  */
		xorByte = readByte();
		
		score.points = readInt();
		score.birthDate = readLong();
		score.uid = readInt();
		score.maxHitpoints = readInt();
		score.currHitpoints = readInt();
		score.dungeonLevel = readInt();
		score.level = readInt();
		score.maxDungeonLevel = readInt();
		score.sex = readInt();
		score.race = readInt();
		score.playerClass = readInt();
		score.name = readString(Player.py.PLAYER_NAME_SIZE);
		score.diedFrom = readString(25);
	}
}
