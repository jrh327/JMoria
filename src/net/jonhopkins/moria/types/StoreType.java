/**
 * StoreType.java: global type declarations
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
