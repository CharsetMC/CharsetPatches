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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import pl.asie.charset.lib.capability.Capabilities;
import pl.asie.charset.patchwork.LocksCapabilityHook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LockHooks implements ISidedInventory, ICapabilityProvider {
	// CAPABILITIES
	@Override
	@Hook(target = "tile")
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		LocksCapabilityHook.Result result = LocksCapabilityHook.handler.wrapCapability((TileEntity) ((Object) this), capability, facing);
		if (result.captures()) {
			return result.canApply();
		}
		return this.hasCapability_postCharset(capability, facing);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	@Hook(target = "tile")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		T result = this.getCapability_postCharset(capability, facing);
		LocksCapabilityHook.Result lockResult = LocksCapabilityHook.handler.wrapCapability((TileEntity) ((Object) this), capability, facing);
		if (lockResult.captures()) {
			return lockResult.canApply() ? (T) lockResult.apply(result) : null;
		}
		return result;
	}

	public boolean hasCapability_postCharset(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return false;
	}

	public <T> T getCapability_postCharset(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return null;
	}

	// ISIDEDINVENTORY

	@Override
	@Hook(target = "tile")
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		if (LocksCapabilityHook.handler.wrapCapability((TileEntity) ((Object) this), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).captures()) {
			return false;
		}
		return this.canInsertItem_postCharset(index, itemStackIn, direction);
	}

	@Override
	@Hook(target = "tile")
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (LocksCapabilityHook.handler.wrapCapability((TileEntity) ((Object) this), CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).captures()) {
			return false;
		}
		return this.canExtractItem_postCharset(index, stack, direction);
	}

	public boolean canInsertItem_postCharset(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	public boolean canExtractItem_postCharset(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	// WE DON'T TOUCH THESE, THEY CAN STAY THERE AND LOOK PRETTY

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {

	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}
}
