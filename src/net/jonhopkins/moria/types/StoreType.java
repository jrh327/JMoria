/*
 * StoreType.java: store object
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

public final class StoreType {
	public StoreType() {
		store_inven = new InvenRecord[Constants.STORE_INVEN_MAX];
		for (int i = 0; i < Constants.STORE_INVEN_MAX; i++) {
			store_inven[i] = new InvenRecord();
		}
	}
	
	public int store_open;
	public int insult_cur;
	public int owner;
	public int store_ctr;
	public int good_buy;
	public int bad_buy;
	public InvenRecord[] store_inven;
}
