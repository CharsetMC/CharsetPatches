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

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pl.asie.charset.lib.block.IPatchCanPushListener;

public class CanPushHooks {
	public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing p_185646_5_) {
		if (blockStateIn.getBlock() instanceof IPatchCanPushListener) {
			return ((IPatchCanPushListener) blockStateIn.getBlock()).charsetCanPushByPiston(blockStateIn, worldIn, pos, facing, destroyBlocks, p_185646_5_);
		}

		return false;
	}

}
