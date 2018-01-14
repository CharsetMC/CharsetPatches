/*
 * Copyright (c) 2017, 2018 Adrian Siekierka
 *
 * This file is part of CharsetPatches.
 *
 * CharsetPatches is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CharsetPatches is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CharsetPatches.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.asie.charset.patches;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.network.INetHandler;

public final class CPTUtil {
	private static final TIntObjectMap<int[]> INT_ARRAYS = new TIntObjectHashMap<>();

	private CPTUtil() {

	}

	public static int[] getSlots(int size) {
		int[] slots = INT_ARRAYS.get(size);
		if (slots == null) {
			slots = new int[size];
			for (int i = 0; i < size; i++) {
				slots[i] = i;
			}
			INT_ARRAYS.put(size, slots);
		}
		return slots;
	}
}
