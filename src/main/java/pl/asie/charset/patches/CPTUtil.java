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
