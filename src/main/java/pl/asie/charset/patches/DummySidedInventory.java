package pl.asie.charset.patches;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public abstract class DummySidedInventory implements ISidedInventory {
	public int[] getSlotsForFace(EnumFacing side) {
		return CPTUtil.getSlots(this.getSizeInventory());
	}

	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}

	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}
}
