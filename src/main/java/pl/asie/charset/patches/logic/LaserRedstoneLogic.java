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

package pl.asie.charset.patches.logic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class LaserRedstoneLogic {
	private LaserRedstoneLogic() {

	}

	public static class MyClassVisitor extends ClassVisitor {
		public MyClassVisitor(int api, ClassVisitor cv) {
			super(api, cv);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
		                                 String signature, String[] exceptions) {
			return new MyMethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
		}
	}

	public static class MyMethodVisitor extends MethodVisitor {
		public MyMethodVisitor(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
		                            String desc, boolean itf) {
			if (Opcodes.INVOKEVIRTUAL == opcode && "net/minecraft/world/World".equals(owner)
					&& ("getRedstonePower".equals(name) || "func_175651_c".equals(name))) {
				super.visitMethodInsn(Opcodes.INVOKESTATIC, "pl/asie/charset/patchwork/LaserRedstoneHook", "getRedstonePower",
						"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)I", false);
			} else {
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}
}
