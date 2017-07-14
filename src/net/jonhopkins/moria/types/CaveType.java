/*
 * CaveType.java: cave block
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
package net.jonhopkins.moria.types;

import net.jonhopkins.moria.Constants;

public final class CaveType {
	public int creatureIndex;
	public int treasureIndex;
	public int fval;
	
	/**
	 * Room should be lit with permanent light, walls with
	 * this set should be permanently lit after tunneled out
	 */
	public boolean litRoom;
	
	/**
	 * Field mark, used for traps/doors/stairs, object is hidden if fm is false
	 */
	public boolean fieldMark;
	
	/**
	 * Permanent light, used for walls and lighted rooms
	 */
	public boolean permLight;
	
	/**
	 * Temporary light, used for player's lamp light, etc.
	 */
	public boolean tempLight;
	
	/**
	 * Check if the space is occupied.
	 * <p>
	 * Possible obstructions include treasure, monsters,
	 * the player, doors, rubble, or walls.
	 * 
	 * @return Whether there is something there
	 */
	public boolean isOccupied() {
		return (this.fval > Constants.MAX_OPEN_SPACE)
				&& (this.treasureIndex != 0)
				&& (this.creatureIndex != 0);
	}
	
	public boolean isOccupiedByPlayer() {
		return this.creatureIndex == 1;
	}
	
	public boolean isOccupiedByMonster() {
		return this.creatureIndex > 1;
	}
	
	public boolean isOccupiedByTreasure() {
		return this.treasureIndex > 0;
	}
	
	/**
	 * Reset all attributes.
	 */
	public void blankAll() {
		this.creatureIndex = 0;
		this.treasureIndex = 0;
		this.fval = 0;
		this.litRoom = false;
		this.fieldMark = false;
		this.permLight = false;
		this.tempLight = false;
	}
}
